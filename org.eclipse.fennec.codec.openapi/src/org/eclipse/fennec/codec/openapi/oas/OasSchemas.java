/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.codec.openapi.oas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaToEPackageConverter;
import org.eclipse.fennec.codec.openapi.OpenApiReferences;
import org.eclipse.fennec.model.oas.Components;
import org.eclipse.fennec.model.oas.OasFactory;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.Operation;
import org.eclipse.fennec.model.oas.Parameter;
import org.eclipse.fennec.model.oas.RequestBody;
import org.eclipse.fennec.model.oas.SchemaReference;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Turns the Schema Objects of a loaded description into classifiers (issue #301).
 * <ul>
 *   <li>{@code components/schemas} becomes the schemas package, as for the 3.0 model.</li>
 *   <li>A use site ({@link SchemaReference}) with {@code $ref} keeps it; the classifier is linked by
 *       {@link OasLinker}. A simple type (string, integer, number, boolean, also as array items) maps to an
 *       Ecore data type, its facets stay on the {@link SchemaReference}. An array sets {@code many}; its
 *       {@code $ref} or type is the one of its items.</li>
 *   <li>Any other inline schema - object, string enum, composition, nested array - becomes a classifier
 *       in the sub-package {@code inline} of the schemas package, named after its place
 *       ({@code ListPets200ApplicationJson}). Inline schemas are converted together with
 *       {@code components/schemas}, so that their {@code $ref}s resolve; the duplicates of the component
 *       classifiers this produces are dropped and the references point to the components package.</li>
 * </ul>
 */
final class OasSchemas {

	static final String INLINE = "inline";

	private static final JsonMapper MAPPER = JsonMapper.builder().build();
	private static final Set<String> COMPLEX = Set.of("properties", "additionalProperties", "patternProperties",
			"allOf", "oneOf", "anyOf", "not", "if", "then", "else", "prefixItems", "dependentSchemas",
			"unevaluatedProperties", "discriminator", "required");
	private static final Set<String> FACETS = Set.of("$ref", "type", "items", "nullable", "format", "title",
			"description", "default", "enum", "const", "minLength", "maxLength", "pattern", "minimum", "maximum",
			"exclusiveMinimum", "exclusiveMaximum", "multipleOf", "readOnly", "writeOnly", "deprecated", "example",
			"examples", "contentMediaType", "contentEncoding", "minItems", "maxItems", "uniqueItems");

	private OasSchemas() {
	}

	/**
	 * @param api a loaded description whose readers kept the Schema Object JSON
	 * @param problems collects what could not be mapped
	 */
	static void convert(OpenAPI api, List<String> problems) {
		Components components = api.getComponents();
		JsonNode componentsJson = components == null || components.getSchemas() == null ? null
				: OasSchemaSource.take(components.getSchemas());
		ObjectNode componentDefs = componentsJson instanceof ObjectNode object ? object : MAPPER.createObjectNode();

		Set<String> used = new HashSet<>();
		componentDefs.propertyNames().forEach(used::add);
		// The converter turns schemas of the same text into one classifier: an inline schema equal to
		// a component is that component's classifier, equal inline schemas share one
		Map<String, String> componentByText = new HashMap<>();
		for (Map.Entry<String, JsonNode> entry : componentDefs.properties()) {
			componentByText.putIfAbsent(entry.getValue().toString(), entry.getKey());
		}
		Map<String, String> inlineByText = new HashMap<>();
		Map<SchemaReference, String> inline = new LinkedHashMap<>();
		Map<SchemaReference, String> sameAsComponent = new LinkedHashMap<>();
		ObjectNode inlineDefs = MAPPER.createObjectNode();
		for (SchemaReference schema : pendingSchemas(api)) {
			JsonNode json = OasSchemaSource.take(schema);
			JsonNode complex = map(schema, json);
			if (complex == null) {
				continue;
			}
			String text = complex.toString();
			if (componentByText.containsKey(text)) {
				sameAsComponent.put(schema, componentByText.get(text));
				continue;
			}
			String name = inlineByText.get(text);
			if (name == null) {
				name = unique(nameFor(schema), used);
				inlineDefs.set(name, marked(complex, name));
				inlineByText.put(text, name);
			}
			inline.put(schema, name);
		}

		EPackage schemas = componentDefs.isEmpty() ? EcoreFactory.eINSTANCE.createEPackage()
				: new JsonSchemaToEPackageConverter().convert(componentDefs, null);
		for (Map.Entry<SchemaReference, String> entry : sameAsComponent.entrySet()) {
			entry.getKey().setClassifier(OpenApiReferences.classifier(schemas, entry.getValue()));
		}
		if (!inline.isEmpty()) {
			EPackage inlinePackage = convertInline(componentDefs, inlineDefs, schemas, problems);
			schemas.getESubpackages().add(inlinePackage);
			for (Map.Entry<SchemaReference, String> entry : inline.entrySet()) {
				EClassifier classifier = OpenApiReferences.classifier(inlinePackage, entry.getValue());
				if (classifier == null) {
					problems.add("inline schema '" + entry.getValue() + "' could not be converted");
				}
				entry.getKey().setClassifier(classifier);
			}
		}
		if (!componentDefs.isEmpty() || !inline.isEmpty()) {
			if (components == null) {
				components = OasFactory.eINSTANCE.createComponents();
				api.setComponents(components);
			}
			components.setSchemas(schemas);
		} else if (components != null && components.getSchemas() != null && componentsJson != null) {
			components.setSchemas(schemas); // an empty components/schemas
		}
	}

	private static List<SchemaReference> pendingSchemas(OpenAPI api) {
		List<SchemaReference> pending = new ArrayList<>();
		for (TreeIterator<EObject> it = api.eAllContents(); it.hasNext();) {
			EObject object = it.next();
			if (object instanceof EPackage) {
				it.prune();
			} else if (object instanceof SchemaReference schema && hasSource(schema)) {
				pending.add(schema);
			}
		}
		return pending;
	}

	private static boolean hasSource(SchemaReference schema) {
		return schema.eAdapters().stream().anyMatch(a -> a instanceof OasSchemaSource);
	}

	/**
	 * Maps what a {@link SchemaReference} holds itself.
	 *
	 * @return the JSON to convert into an inline classifier, {@code null} if there is none
	 */
	static JsonNode map(SchemaReference schema, JsonNode json) {
		if (json == null) {
			return null;
		}
		if (json.isBoolean()) {
			if (json.asBoolean()) {
				schema.setClassifier(EcorePackage.Literals.EJAVA_OBJECT); // true: any value
			} else {
				schema.getKeywords().put("not", Map.of()); // false: no value
			}
			return null;
		}
		if (!json.isObject()) {
			return null;
		}
		// anyOf: [{type: integer}, {type: null}] is a nullable integer
		JsonNode unwrapped = JsonSchemaToEPackageConverter.unwrapNullableUnion(json);
		if (unwrapped != json) {
			schema.setNullable(isNullableUnion(json));
			return map(schema, unwrapped);
		}
		if (isComplex(json)) {
			return json;
		}
		facets(schema, json);
		List<String> types = types(json);
		if (types.remove("null")) {
			schema.setNullable(true);
		}
		if (json.has("$ref")) {
			schema.setRef(json.get("$ref").asString());
			return null;
		}
		String type = types.size() == 1 ? types.get(0) : null;
		if ("array".equals(type) || (type == null && json.has("items"))) {
			schema.setMany(true);
			JsonNode items = json.get("items");
			if (items == null || !items.isObject()) {
				schema.setClassifier(EcorePackage.Literals.EJAVA_OBJECT);
				return null;
			}
			if (isComplex(items) || types(items).contains("array") || items.has("items")) {
				return items;
			}
			if (items.has("$ref")) {
				schema.setRef(items.get("$ref").asString());
				return null;
			}
			facets(schema, items);
			List<String> itemTypes = types(items);
			if (itemTypes.remove("null")) {
				schema.setNullable(true);
			}
			schema.setClassifier(dataType(itemTypes.size() == 1 ? itemTypes.get(0) : null, text(items, "format")));
			return null;
		}
		if (types.size() > 1) {
			schema.setClassifier(EcorePackage.Literals.EJAVA_OBJECT);
			schema.getKeywords().put("type", types);
			return null;
		}
		schema.setClassifier(dataType(type, schema.getFormat()));
		return null;
	}

	private static boolean isNullableUnion(JsonNode json) {
		for (String keyword : new String[] { "anyOf", "oneOf" }) {
			JsonNode union = json.get(keyword);
			if (union != null) {
				for (JsonNode branch : union) {
					if (branch.has("type") && "null".equals(branch.get("type").asString())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean isComplex(JsonNode json) {
		for (String keyword : COMPLEX) {
			if (json.has(keyword)) {
				return true;
			}
		}
		List<String> types = types(json);
		return types.contains("object") || (json.has("enum") && (types.contains("string") || types.isEmpty()));
	}

	private static List<String> types(JsonNode json) {
		List<String> types = new ArrayList<>();
		JsonNode type = json.get("type");
		if (type != null && type.isArray()) {
			type.forEach(t -> types.add(t.asString()));
		} else if (type != null && type.isString()) {
			types.add(type.asString());
		}
		return types;
	}

	/** The facets of a simple schema; keywords without a feature go to {@code keywords}. */
	private static void facets(SchemaReference schema, JsonNode json) {
		for (Map.Entry<String, JsonNode> entry : json.properties()) {
			String key = entry.getKey();
			JsonNode value = entry.getValue();
			switch (key) {
				case "format" -> schema.setFormat(value.asString());
				case "title" -> schema.setTitle(value.asString());
				case "description" -> schema.setDescription(value.asString());
				case "default" -> schema.setDefault(java(value));
				case "enum" -> value.forEach(v -> schema.getEnum().add(java(v)));
				case "const" -> schema.setConst(java(value));
				case "minLength" -> schema.setMinLength(value.asInt());
				case "maxLength" -> schema.setMaxLength(value.asInt());
				case "pattern" -> schema.setPattern(value.asString());
				case "minimum" -> schema.setMinimum(value.decimalValue());
				case "maximum" -> schema.setMaximum(value.decimalValue());
				case "exclusiveMinimum" -> {
					if (value.isNumber()) {
						schema.setExclusiveMinimum(value.decimalValue());
					} else if (value.asBoolean() && json.has("minimum")) {
						schema.setExclusiveMinimum(json.get("minimum").decimalValue()); // 3.0 form
					}
				}
				case "exclusiveMaximum" -> {
					if (value.isNumber()) {
						schema.setExclusiveMaximum(value.decimalValue());
					} else if (value.asBoolean() && json.has("maximum")) {
						schema.setExclusiveMaximum(json.get("maximum").decimalValue()); // 3.0 form
					}
				}
				case "multipleOf" -> schema.setMultipleOf(value.decimalValue());
				case "readOnly" -> schema.setReadOnly(value.asBoolean());
				case "writeOnly" -> schema.setWriteOnly(value.asBoolean());
				case "deprecated" -> schema.setDeprecated(value.asBoolean());
				case "example" -> schema.setExample(java(value));
				case "examples" -> value.forEach(v -> schema.getExamples().add(java(v)));
				case "contentMediaType" -> schema.setContentMediaType(value.asString());
				case "contentEncoding" -> schema.setContentEncoding(value.asString());
				case "minItems" -> schema.setMinItems(value.asInt());
				case "maxItems" -> schema.setMaxItems(value.asInt());
				case "uniqueItems" -> schema.setUniqueItems(value.asBoolean());
				case "nullable" -> schema.setNullable(schema.isNullable() || value.asBoolean());
				default -> {
					if (!FACETS.contains(key)) {
						schema.getKeywords().put(key, java(value));
					}
				}
			}
		}
		// the 3.0 form names the bound twice: minimum and exclusiveMinimum: true
		if (schema.getExclusiveMinimum() != null && schema.getExclusiveMinimum().equals(schema.getMinimum())
				&& json.has("exclusiveMinimum") && json.get("exclusiveMinimum").isBoolean()) {
			schema.setMinimum(null);
		}
		if (schema.getExclusiveMaximum() != null && schema.getExclusiveMaximum().equals(schema.getMaximum())
				&& json.has("exclusiveMaximum") && json.get("exclusiveMaximum").isBoolean()) {
			schema.setMaximum(null);
		}
	}

	/** The Ecore data type of a simple JSON Schema type, refined by format. */
	static EDataType dataType(String type, String format) {
		if (type == null) {
			return EcorePackage.Literals.EJAVA_OBJECT;
		}
		return switch (type) {
			case "string" -> EcorePackage.Literals.ESTRING;
			case "integer" -> "int64".equals(format) ? EcorePackage.Literals.ELONG_OBJECT : EcorePackage.Literals.EINTEGER_OBJECT;
			case "number" -> "float".equals(format) ? EcorePackage.Literals.EFLOAT_OBJECT : EcorePackage.Literals.EDOUBLE_OBJECT;
			case "boolean" -> EcorePackage.Literals.EBOOLEAN_OBJECT;
			default -> EcorePackage.Literals.EJAVA_OBJECT;
		};
	}

	private static Object java(JsonNode value) {
		return MAPPER.treeToValue(value, Object.class);
	}

	private static String text(JsonNode json, String key) {
		return json.has(key) ? json.get(key).asString() : null;
	}

	/**
	 * Converts the inline schemas together with the component schemas, so that their {@code $ref}s
	 * resolve, then points them to the classifiers of the components package and drops the duplicates.
	 */
	private static EPackage convertInline(ObjectNode componentDefs, ObjectNode inlineDefs, EPackage schemas,
			List<String> problems) {
		ObjectNode all = MAPPER.createObjectNode();
		all.setAll(componentDefs);
		all.setAll(inlineDefs);
		EPackage converted = new JsonSchemaToEPackageConverter().convert(all, null);
		Map<EClassifier, EClassifier> duplicates = new HashMap<>();
		for (String key : componentDefs.propertyNames()) {
			EClassifier copy = OpenApiReferences.classifier(converted, key);
			EClassifier original = OpenApiReferences.classifier(schemas, key);
			if (copy != null && original != null) {
				duplicates.put(copy, original);
			}
		}
		// artificial helpers of the component schemas are duplicates as well: by name
		for (EClassifier copy : converted.getEClassifiers()) {
			if (!duplicates.containsKey(copy) && !inlineDefs.has(originalName(copy)) && schemas.getEClassifier(copy.getName()) != null
					&& !isInlineHelper(copy, inlineDefs)) {
				duplicates.put(copy, schemas.getEClassifier(copy.getName()));
			}
		}
		for (EClassifier classifier : converted.getEClassifiers()) {
			unmark(classifier);
		}
		EPackage inline = EcoreFactory.eINSTANCE.createEPackage();
		inline.setName(INLINE);
		inline.setNsPrefix(INLINE);
		List<EClassifier> keep = new ArrayList<>(converted.getEClassifiers());
		keep.removeAll(duplicates.keySet());
		for (EClassifier classifier : keep) {
			if (classifier instanceof EClass eClass) {
				repoint(eClass, duplicates);
			}
		}
		inline.getEClassifiers().addAll(keep);
		return inline;
	}

	private static final String MARK = "fennec-inline:";

	/**
	 * Makes the text of an inline schema unique: the converter turns schemas of the same text into
	 * one classifier, which would give an inline schema the classifier of an equal property enum of
	 * a component, under another name. Removed again by {@link #unmark(EClassifier)}.
	 */
	private static JsonNode marked(JsonNode schema, String name) {
		if (schema instanceof ObjectNode object && !object.has("$comment")) {
			ObjectNode copy = object.deepCopy();
			copy.put("$comment", MARK + name);
			return copy;
		}
		return schema;
	}

	private static void unmark(EClassifier classifier) {
		var annotation = classifier.getEAnnotation(AnnotationSources.JSONSCHEMA);
		String comment = annotation == null ? null : annotation.getDetails().get("comment");
		if (comment != null && comment.startsWith(MARK)) {
			annotation.getDetails().removeKey("comment");
		}
	}

	private static boolean isInlineHelper(EClassifier classifier, ObjectNode inlineDefs) {
		String name = classifier.getName();
		for (String inlineName : inlineDefs.propertyNames()) {
			if (name.startsWith(Character.toUpperCase(inlineName.charAt(0)) + inlineName.substring(1))) {
				return true;
			}
		}
		return false;
	}

	private static String originalName(EClassifier classifier) {
		String original = EcoreUtil.getAnnotation(classifier, AnnotationSources.JSONSCHEMA, "originalName");
		return original != null ? original : classifier.getName();
	}

	private static void repoint(EClass eClass, Map<EClassifier, EClassifier> duplicates) {
		for (int i = 0; i < eClass.getESuperTypes().size(); i++) {
			EClassifier target = duplicates.get(eClass.getESuperTypes().get(i));
			if (target instanceof EClass superType) {
				eClass.getESuperTypes().set(i, superType);
			}
		}
		for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
			EClassifier target = duplicates.get(feature.getEType());
			if (target != null) {
				feature.setEType(target);
			}
		}
	}

	private static String unique(String name, Set<String> used) {
		String candidate = name;
		for (int i = 2; !used.add(candidate); i++) {
			candidate = name + i;
		}
		return candidate;
	}

	/** A name from the place of the schema: operation, status code, media type, parameter, ... */
	static String nameFor(SchemaReference schema) {
		List<String> segments = new ArrayList<>();
		if ("itemSchema".equals(schema.eContainingFeature() == null ? null : schema.eContainingFeature().getName())) {
			segments.add("Item");
		}
		for (EObject current = schema.eContainer(); current != null && !(current instanceof OpenAPI);
				current = current.eContainer()) {
			if (current instanceof Operation operation && operation.getOperationId() != null) {
				segments.add(0, operation.getOperationId());
				break;
			}
			if (current instanceof Operation) {
				segments.add(0, current.eContainingFeature().getName());
			} else if (current instanceof Parameter parameter && parameter.getName() != null) {
				segments.add(0, parameter.getName());
			} else if (current instanceof RequestBody) {
				segments.add(0, "Request");
			} else if (current.eClass().getInstanceClass() == Map.Entry.class) {
				Object key = current.eGet(current.eClass().getEStructuralFeature("key"));
				if (key != null) {
					segments.add(0, key.toString());
				}
			}
		}
		StringBuilder name = new StringBuilder();
		for (String segment : segments) {
			for (String word : segment.split("[^A-Za-z0-9]+")) {
				if (!word.isEmpty()) {
					name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
				}
			}
		}
		if (name.isEmpty()) {
			name.append("Schema");
		} else if (Character.isDigit(name.charAt(0))) {
			name.insert(0, "Schema");
		}
		return name.toString();
	}
}
