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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.codec.jsonschema.v2.converter.EPackageToJsonSchemaConverter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.SchemaReference;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * Writes a {@link SchemaReference} as the Schema Object it stands for (issue #301), in the form of
 * the document's version: {@code nullable: true} and a boolean {@code exclusiveMinimum} for 3.0, a
 * type array with {@code "null"} and a numeric bound for 3.1 and later.
 * <ul>
 *   <li>{@code $ref} → {@code {"$ref": …}}, inside {@code items} when {@code many};</li>
 *   <li>an Ecore data type → its JSON type, {@code format} and facets;</li>
 *   <li>an inline classifier → the schema the classifier stands for, nested inline classifiers
 *       inline, references to {@code components/schemas} as {@code $ref}.</li>
 * </ul>
 */
public class SchemaReferenceValueWriter implements ReferenceValueWriter<SchemaReference> {

	private static final JsonMapper MAPPER = JsonMapper.builder().build();
	private static final String COMPONENTS_SCHEMAS = "components/schemas";

	@Override
	public String getName() {
		return SchemaReferenceValueReader.NAME;
	}

	@Override
	public boolean canHandle(EReference reference) {
		return reference.getEReferenceType() == OasPackage.Literals.SCHEMA_REFERENCE;
	}

	@Override
	public void write(SchemaReference value, EReference reference, CodecWriterContext ctx) throws IOException {
		ctx.getGenerator().writeTree(toJson(value, isV30(value)));
	}

	/**
	 * @param schema a use site
	 * @param v30 whether the document is OpenAPI 3.0
	 * @return the Schema Object
	 */
	static JsonNode toJson(SchemaReference schema, boolean v30) throws IOException {
		EClassifier classifier = schema.getClassifier();
		ObjectNode item;
		boolean itemFacets;
		if (isInline(classifier)) {
			item = inlineSchema(classifier, v30).deepCopy();
			itemFacets = false;
		} else if (schema.getRef() != null) {
			item = MAPPER.createObjectNode().put("$ref", schema.getRef());
			itemFacets = false;
		} else {
			item = MAPPER.createObjectNode();
			type(item, classifier, schema.isNullable() && !schema.isMany(), v30);
			itemFacets = true;
		}
		ObjectNode outer = item;
		if (schema.isMany()) {
			outer = MAPPER.createObjectNode();
			outer.put("type", "array");
			if (schema.isNullable()) {
				nullable(outer, "array", v30);
			}
			outer.set("items", item);
		}
		ObjectNode facets = itemFacets ? item : outer;
		if (schema.getFormat() != null) {
			facets.put("format", schema.getFormat());
		}
		valueFacets(facets, schema, v30);
		annotationFacets(outer, schema);
		if (schema.getMinItems() != null) {
			outer.put("minItems", schema.getMinItems());
		}
		if (schema.getMaxItems() != null) {
			outer.put("maxItems", schema.getMaxItems());
		}
		if (schema.isUniqueItems()) {
			outer.put("uniqueItems", true);
		}
		for (Map.Entry<String, Object> keyword : schema.getKeywords()) {
			outer.set(keyword.getKey(), MAPPER.valueToTree(keyword.getValue()));
		}
		return outer;
	}

	private static void type(ObjectNode node, EClassifier classifier, boolean nullable, boolean v30) {
		String type = jsonType(classifier);
		if (type != null) {
			node.put("type", type);
			if (nullable) {
				nullable(node, type, v30);
			}
		}
	}

	private static void nullable(ObjectNode node, String type, boolean v30) {
		if (v30) {
			node.put("nullable", true);
		} else {
			ArrayNode types = MAPPER.createArrayNode().add(type).add("null");
			node.set("type", types);
		}
	}

	private static String jsonType(EClassifier classifier) {
		if (classifier == null) {
			return null;
		}
		if (classifier == EcorePackage.Literals.ESTRING) {
			return "string";
		}
		if (classifier == EcorePackage.Literals.EINTEGER_OBJECT || classifier == EcorePackage.Literals.ELONG_OBJECT
				|| classifier == EcorePackage.Literals.EINT || classifier == EcorePackage.Literals.ELONG) {
			return "integer";
		}
		if (classifier == EcorePackage.Literals.EDOUBLE_OBJECT || classifier == EcorePackage.Literals.EFLOAT_OBJECT
				|| classifier == EcorePackage.Literals.EBIG_DECIMAL) {
			return "number";
		}
		if (classifier == EcorePackage.Literals.EBOOLEAN_OBJECT || classifier == EcorePackage.Literals.EBOOLEAN) {
			return "boolean";
		}
		return null;
	}

	/** Facets that restrict the value: on the items of an array. */
	private static void valueFacets(ObjectNode node, SchemaReference schema, boolean v30) {
		if (!schema.getEnum().isEmpty()) {
			node.set("enum", MAPPER.valueToTree(schema.getEnum()));
		}
		if (schema.getConst() != null) {
			node.set("const", MAPPER.valueToTree(schema.getConst()));
		}
		if (schema.getMinLength() != null) {
			node.put("minLength", schema.getMinLength());
		}
		if (schema.getMaxLength() != null) {
			node.put("maxLength", schema.getMaxLength());
		}
		if (schema.getPattern() != null) {
			node.put("pattern", schema.getPattern());
		}
		bound(node, "minimum", "exclusiveMinimum", schema.getMinimum(), schema.getExclusiveMinimum(), v30);
		bound(node, "maximum", "exclusiveMaximum", schema.getMaximum(), schema.getExclusiveMaximum(), v30);
		if (schema.getMultipleOf() != null) {
			node.put("multipleOf", schema.getMultipleOf());
		}
	}

	private static void bound(ObjectNode node, String inclusive, String exclusive, BigDecimal value,
			BigDecimal exclusiveValue, boolean v30) {
		if (value != null) {
			node.put(inclusive, value);
		}
		if (exclusiveValue != null) {
			if (v30) {
				node.put(inclusive, exclusiveValue);
				node.put(exclusive, true);
			} else {
				node.put(exclusive, exclusiveValue);
			}
		}
	}

	/** Facets that describe the schema: on the outermost schema. */
	private static void annotationFacets(ObjectNode node, SchemaReference schema) {
		if (schema.getTitle() != null) {
			node.put("title", schema.getTitle());
		}
		if (schema.getDescription() != null) {
			node.put("description", schema.getDescription());
		}
		if (schema.getDefault() != null) {
			node.set("default", MAPPER.valueToTree(schema.getDefault()));
		}
		if (schema.isReadOnly()) {
			node.put("readOnly", true);
		}
		if (schema.isWriteOnly()) {
			node.put("writeOnly", true);
		}
		if (schema.isDeprecated()) {
			node.put("deprecated", true);
		}
		if (schema.getExample() != null) {
			node.set("example", MAPPER.valueToTree(schema.getExample()));
		}
		if (!schema.getExamples().isEmpty()) {
			node.set("examples", MAPPER.valueToTree(schema.getExamples()));
		}
		if (schema.getContentMediaType() != null) {
			node.put("contentMediaType", schema.getContentMediaType());
		}
		if (schema.getContentEncoding() != null) {
			node.put("contentEncoding", schema.getContentEncoding());
		}
	}

	private static boolean isInline(EClassifier classifier) {
		return classifier != null && classifier.getEPackage() != null
				&& OasSchemas.INLINE.equals(classifier.getEPackage().getName())
				&& classifier.getEPackage().getESuperPackage() != null;
	}

	/** The schema of an inline classifier, from one conversion of its package per save. */
	private static ObjectNode inlineSchema(EClassifier classifier, boolean v30) throws IOException {
		EPackage inline = classifier.getEPackage();
		Map<String, JsonNode> schemas = InlineCache.get(inline, v30);
		String key = EcoreUtil.getAnnotation(classifier, AnnotationSources.JSONSCHEMA, "originalName");
		JsonNode schema = schemas.get(key != null ? key : classifier.getName());
		return schema instanceof ObjectNode object ? object : MAPPER.createObjectNode();
	}

	private static boolean isV30(SchemaReference schema) {
		if (schema.eResource() instanceof OasResourceImpl resource) {
			return resource.getVersion() == OasVersion.V3_0;
		}
		return EcoreUtil.getRootContainer(schema) instanceof OpenAPI api && OasVersion.V3_0.matches(api.getOpenapi());
	}

	/**
	 * The converted inline packages of the current save, per package. A save resets it
	 * ({@link OasResourceImpl}), so a changed model is converted again.
	 */
	static final class InlineCache {

		private static final ThreadLocal<Map<EPackage, Map<String, JsonNode>>> CACHE = ThreadLocal
				.withInitial(HashMap::new);

		private InlineCache() {
		}

		static void reset() {
			CACHE.get().clear();
		}

		static Map<String, JsonNode> get(EPackage inline, boolean v30) throws IOException {
			Map<String, JsonNode> schemas = CACHE.get().get(inline);
			if (schemas == null) {
				schemas = convert(inline, v30);
				CACHE.get().put(inline, schemas);
			}
			return schemas;
		}

		private static Map<String, JsonNode> convert(EPackage inline, boolean v30) throws IOException {
			Map<String, Object> options = new HashMap<>();
			options.put(CodecJsonSchemaOptions.OPTION_INLINE_LOCAL_REFS, Boolean.TRUE);
			options.put(CodecJsonSchemaOptions.OPTION_NULLABLE_KEYWORD, v30);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			new EPackageToJsonSchemaConverter().convert(inline, out, COMPONENTS_SCHEMAS, false, options);
			JsonNode document = MAPPER.readTree(out.toByteArray());
			Map<String, JsonNode> schemas = new HashMap<>();
			JsonNode definitions = document.get(COMPONENTS_SCHEMAS);
			if (definitions != null) {
				for (Map.Entry<String, JsonNode> entry : definitions.properties()) {
					schemas.put(entry.getKey(), entry.getValue());
				}
			}
			return schemas;
		}
	}
}
