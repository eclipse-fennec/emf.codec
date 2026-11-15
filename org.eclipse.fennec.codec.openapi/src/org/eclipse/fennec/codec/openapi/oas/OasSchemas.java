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
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.v2.model.SchemaJson;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Derives the Ecore of a loaded description from its schemas (issues #301, #319). The schemas
 * themselves stay as read, in the JSON Schema model; this adds the classifiers.
 * <ul>
 *   <li>{@code components/schemas} becomes the schemas package ({@code Components.schemasPackage}).</li>
 *   <li>At a use site, a simple type (string, integer, number, boolean, also as array items) gets an
 *       Ecore data type as {@link Schema#getClassifier() classifier}; an array's classifier is its item
 *       type. A {@code $ref} is linked by {@link OasLinker}.</li>
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

	private OasSchemas() {
	}

	/**
	 * Derives the Ecore of a loaded description: the schemas package from {@code components/schemas},
	 * and a classifier for every Schema Object at a use site.
	 *
	 * @param api a loaded description
	 * @param problems collects what could not be mapped
	 */
	static void convert(OpenAPI api, List<String> problems) {
		Components components = api.getComponents();
		ObjectNode componentDefs = MAPPER.createObjectNode();
		if (components != null) {
			for (Map.Entry<String, Schema> entry : components.getSchemas()) {
				componentDefs.set(entry.getKey(), SchemaJson.write(entry.getValue()));
			}
		}

		Set<String> used = new HashSet<>();
		componentDefs.propertyNames().forEach(used::add);
		// The converter turns schemas of the same text into one classifier: an inline schema equal to
		// a component is that component's classifier, equal inline schemas share one
		Map<String, String> componentByText = new HashMap<>();
		for (Map.Entry<String, JsonNode> entry : componentDefs.properties()) {
			componentByText.putIfAbsent(entry.getValue().toString(), entry.getKey());
		}
		Map<String, String> inlineByText = new HashMap<>();
		Map<Schema, String> inline = new LinkedHashMap<>();
		Map<Schema, String> sameAsComponent = new LinkedHashMap<>();
		ObjectNode inlineDefs = MAPPER.createObjectNode();
		for (Schema schema : useSites(api)) {
			JsonNode complex = map(schema, SchemaJson.write(schema));
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
				: new JsonSchemaToEPackageConverter().convertDefinitions(components.getSchemas().map());
		for (Map.Entry<Schema, String> entry : sameAsComponent.entrySet()) {
			entry.getKey().setClassifier(OpenApiReferences.classifier(schemas, entry.getValue()));
		}
		if (!inline.isEmpty()) {
			EPackage inlinePackage = convertInline(componentDefs, inlineDefs, schemas, problems);
			schemas.getESubpackages().add(inlinePackage);
			for (Map.Entry<Schema, String> entry : inline.entrySet()) {
				EClassifier classifier = OpenApiReferences.classifier(inlinePackage, entry.getValue());
				if (classifier == null) {
					problems.add("inline schema '" + entry.getValue() + "' could not be converted");
				}
				entry.getKey().setClassifier(classifier);
			}
		}
		if (!componentDefs.isEmpty() || !inline.isEmpty() || components != null) {
			if (components == null) {
				components = OasFactory.eINSTANCE.createComponents();
				api.setComponents(components);
			}
			components.setSchemasPackage(schemas);
		}
	}

	/** The schemas of parameters, headers and media types - not the ones nested in other schemas. */
	private static List<Schema> useSites(OpenAPI api) {
		List<Schema> sites = new ArrayList<>();
		for (TreeIterator<EObject> it = api.eAllContents(); it.hasNext();) {
			EObject object = it.next();
			if (object instanceof EPackage) {
				it.prune();
			} else if (object instanceof Schema schema) {
				it.prune();
				EObject owner = schema.eContainer();
				if (!(owner instanceof Schema) && !(owner instanceof Map.Entry<?, ?>)) {
					sites.add(schema);
				}
			}
		}
		return sites;
	}

	/**
	 * Sets the classifier of a use site where it is a simple type; a {@code $ref} is linked by
	 * {@link OasLinker}.
	 *
	 * @return the JSON to convert into an inline classifier, {@code null} if there is none
	 */
	static JsonNode map(Schema schema, JsonNode json) {
		if (json == null || json.isBoolean()) {
			if (json != null && json.asBoolean()) {
				schema.setClassifier(EcorePackage.Literals.EJAVA_OBJECT); // true: any value
			}
			return null;
		}
		if (!json.isObject()) {
			return null;
		}
		// anyOf: [{type: integer}, {type: null}] is a nullable integer
		JsonNode unwrapped = JsonSchemaToEPackageConverter.unwrapNullableUnion(json);
		if (unwrapped != json) {
			return map(schema, unwrapped);
		}
		if (isComplex(json)) {
			return json;
		}
		if (json.has("$ref")) {
			return null;
		}
		List<String> types = types(json);
		types.remove("null");
		String type = types.size() == 1 ? types.get(0) : null;
		if ("array".equals(type) || (type == null && json.has("items"))) {
			JsonNode items = json.get("items");
			if (items == null || !items.isObject()) {
				schema.setClassifier(EcorePackage.Literals.EJAVA_OBJECT);
				return null;
			}
			JsonNode unwrappedItems = JsonSchemaToEPackageConverter.unwrapNullableUnion(items);
			if (isComplex(unwrappedItems) || types(unwrappedItems).contains("array") || unwrappedItems.has("items")) {
				return unwrappedItems;
			}
			if (unwrappedItems.has("$ref")) {
				return null; // linked by OasLinker
			}
			List<String> itemTypes = types(unwrappedItems);
			itemTypes.remove("null");
			schema.setClassifier(dataType(itemTypes.size() == 1 ? itemTypes.get(0) : null, text(unwrappedItems, "format")));
			return null;
		}
		schema.setClassifier(types.size() > 1 ? EcorePackage.Literals.EJAVA_OBJECT : dataType(type, text(json, "format")));
		return null;
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

	/** The Ecore data type of a simple JSON Schema type, refined by format - as the converter maps it. */
	static EDataType dataType(String type, String format) {
		return JsonSchemaToEPackageConverter.dataType(type, format);
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
		inline.getEClassifiers().addAll(keep);
		// every reference into the dropped duplicates points to the components package now; a
		// classifier only an inline schema needs moves along
		for (int i = 0; i < inline.getEClassifiers().size(); i++) {
			if (inline.getEClassifiers().get(i) instanceof EClass eClass) {
				repoint(eClass, duplicates, converted, inline);
			}
		}
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

	private static void repoint(EClass eClass, Map<EClassifier, EClassifier> duplicates, EPackage converted,
			EPackage inline) {
		for (int i = 0; i < eClass.getESuperTypes().size(); i++) {
			EClassifier target = target(eClass.getESuperTypes().get(i), duplicates, converted, inline);
			if (target instanceof EClass superType && superType != eClass.getESuperTypes().get(i)) {
				eClass.getESuperTypes().set(i, superType);
			}
		}
		for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
			EClassifier target = target(feature.getEType(), duplicates, converted, inline);
			if (target != feature.getEType()) {
				feature.setEType(target);
			}
		}
	}

	/** Where a reference to a classifier of the helper conversion must point. */
	private static EClassifier target(EClassifier classifier, Map<EClassifier, EClassifier> duplicates,
			EPackage converted, EPackage inline) {
		if (classifier == null || classifier.getEPackage() != converted) {
			return classifier;
		}
		EClassifier duplicate = duplicates.get(classifier);
		if (duplicate != null) {
			return duplicate;
		}
		inline.getEClassifiers().add(classifier); // needed by an inline schema only
		return classifier;
	}

	private static String unique(String name, Set<String> used) {
		String candidate = name;
		for (int i = 2; !used.add(candidate); i++) {
			candidate = name + i;
		}
		return candidate;
	}

	/** A name from the place of the schema: operation, status code, media type, parameter, ... */
	static String nameFor(Schema schema) {
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
