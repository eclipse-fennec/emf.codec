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
package org.eclipse.fennec.codec.jsonschema.v2.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * JSON Schema → EPackage → JSON Schema → EPackage is stable for the constructs OpenAPI
 * descriptions use in {@code components/schemas} (issue #293): the second EPackage equals the first.
 */
@DisplayName("Schema round trip")
class SchemaRoundTripTest {

	private static final JsonMapper MAPPER = JsonMapper.builder().build();

	@Nested
	@DisplayName("top-level array schema")
	class TopLevelArray {

		private static final String SCHEMA = """
				{
				  "$defs": {
				    "Pet": {
				      "type": "object",
				      "properties": { "name": { "type": "string" } }
				    },
				    "Pets": {
				      "type": "array",
				      "description": "A page of pets",
				      "minItems": 1,
				      "maxItems": 100,
				      "items": { "$ref": "#/$defs/Pet" }
				    }
				  }
				}
				""";

		@Test
		@DisplayName("is written back as an array schema")
		void writtenAsArray() throws IOException {
			JsonNode pets = write(read(SCHEMA)).get("$defs").get("Pets");

			assertNotNull(pets, "Pets must not be dropped");
			assertEquals("array", pets.get("type").asString());
			assertEquals("#/$defs/Pet", pets.get("items").get("$ref").asString());
			assertEquals(1, pets.get("minItems").asInt());
			assertEquals(100, pets.get("maxItems").asInt());
			assertEquals("A page of pets", pets.get("description").asString());
		}

		@Test
		@DisplayName("keeps minItems and maxItems as bounds of the items feature")
		void boundsRead() throws IOException {
			EClass pets = (EClass) read(SCHEMA).getEClassifier("Pets");
			EStructuralFeature items = pets.getEStructuralFeature("items");

			assertEquals(1, items.getLowerBound());
			assertEquals(100, items.getUpperBound());
		}

		@Test
		@DisplayName("reads back equal")
		void roundTrip() throws IOException {
			assertRoundTrip(SCHEMA);
		}
	}

	@Nested
	@DisplayName("allOf")
	class AllOf {

		private static final String SCHEMA = """
				{
				  "$defs": {
				    "NewPet": {
				      "type": "object",
				      "required": ["name"],
				      "properties": {
				        "name": { "type": "string" },
				        "tag": { "type": "string" }
				      }
				    },
				    "Pet": {
				      "allOf": [
				        { "$ref": "#/$defs/NewPet" },
				        {
				          "type": "object",
				          "required": ["id"],
				          "properties": { "id": { "type": "integer", "format": "int64" } }
				        }
				      ]
				    }
				  }
				}
				""";

		@Test
		@DisplayName("is mapped to the supertype and the own features")
		void mapped() throws IOException {
			EPackage pkg = read(SCHEMA);
			EClass pet = (EClass) pkg.getEClassifier("Pet");

			assertEquals(List.of(pkg.getEClassifier("NewPet")), pet.getESuperTypes());
			assertEquals(1, pet.getEStructuralFeature("id").getLowerBound());
			assertNotNull(pet.getEAllStructuralFeatures().stream()
					.filter(f -> "name".equals(f.getName())).findFirst().orElse(null));
		}

		@Test
		@DisplayName("keeps no copy of the source text, which would go stale")
		void noSourceCopy() throws IOException {
			EClass pet = (EClass) read(SCHEMA).getEClassifier("Pet");

			assertNull(EcoreUtil.getAnnotation(pet, AnnotationSources.JSONSCHEMA, "allOf"));
		}

		@Test
		@DisplayName("reads back equal")
		void roundTrip() throws IOException {
			assertRoundTrip(SCHEMA);
		}
	}

	@Nested
	@DisplayName("named simple schemas")
	class NamedSimple {

		private static final String SCHEMA = """
				{
				  "$defs": {
				    "coordinate": { "type": "integer", "minimum": 1, "maximum": 3, "example": 1 },
				    "errorMessage": { "type": "string", "maxLength": 256, "description": "An error" },
				    "code": { "type": "string", "format": "uuid", "pattern": "^[a-f0-9-]+$", "minLength": 36 },
				    "limit": { "type": "number", "exclusiveMinimum": 0, "multipleOf": 0.5 },
				    "Cell": {
				      "type": "object",
				      "properties": { "row": { "$ref": "#/$defs/coordinate" } }
				    }
				  }
				}
				""";

		@Test
		@DisplayName("keep their key as original name")
		void originalName() throws IOException {
			EPackage pkg = read(SCHEMA);

			assertEquals("coordinate", EcoreUtil.getAnnotation(pkg.getEClassifier("Coordinate"),
					AnnotationSources.JSONSCHEMA, "originalName"));
			assertNotNull(write(pkg).get("$defs").get("coordinate"), "written under its key");
		}

		@Test
		@DisplayName("keep their facets")
		void facets() throws IOException {
			JsonNode defs = write(read(SCHEMA)).get("$defs");

			assertEquals(1, defs.get("coordinate").get("minimum").asInt());
			assertEquals(3, defs.get("coordinate").get("maximum").asInt());
			assertEquals(256, defs.get("errorMessage").get("maxLength").asInt());
			assertEquals("uuid", defs.get("code").get("format").asString());
			assertEquals("^[a-f0-9-]+$", defs.get("code").get("pattern").asString());
			assertEquals(36, defs.get("code").get("minLength").asInt());
		}

		@Test
		@DisplayName("a numeric exclusiveMinimum (2020-12) stays a number")
		void numericExclusiveMinimum() throws IOException {
			JsonNode limit = write(read(SCHEMA)).get("$defs").get("limit");

			assertTrue(limit.get("exclusiveMinimum").isNumber(), limit.toString());
			assertEquals(0, limit.get("exclusiveMinimum").asInt());
		}

		@Test
		@DisplayName("reads back equal")
		void roundTrip() throws IOException {
			assertRoundTrip(SCHEMA);
		}
	}

	@Nested
	@DisplayName("references to named enums and arrays (tictactoe)")
	class NamedReferences {

		private static final String SCHEMA = """
				{
				  "$defs": {
				    "mark": { "type": "string", "enum": [".", "X", "O"], "description": "A square" },
				    "winner": { "type": "string", "enum": [".", "X", "O"], "description": "The winner" },
				    "board": {
				      "type": "array", "minItems": 3, "maxItems": 3,
				      "items": { "type": "array", "minItems": 3, "maxItems": 3, "items": { "$ref": "#/$defs/mark" } }
				    },
				    "status": {
				      "type": "object",
				      "properties": {
				        "winner": { "$ref": "#/$defs/winner" },
				        "board": { "$ref": "#/$defs/board" }
				      }
				    }
				  }
				}
				""";

		@Test
		@DisplayName("are written as $ref under their keys")
		void writtenAsRef() throws IOException {
			JsonNode defs = write(read(SCHEMA)).get("$defs");

			assertNotNull(defs.get("board"), defs.toString());
			assertNull(defs.get("Board"), defs.toString());
			JsonNode status = defs.get("status").get("properties");
			assertEquals("#/$defs/winner", String.valueOf(status.get("winner").get("$ref")).replace("\"", ""), defs.toString());
			assertEquals("#/$defs/board", String.valueOf(status.get("board").get("$ref")).replace("\"", ""), defs.toString());
		}

		@Test
		@DisplayName("read back equal")
		void roundTrip() throws IOException {
			assertRoundTrip(SCHEMA);
		}
	}

	@Nested
	@DisplayName("definitions the converter used to drop (real-world OpenAPI)")
	class DroppedDefinitions {

		private static final String SCHEMA = """
				{
				  "$defs": {
				    "limit": { "type": "object", "properties": { "x": { "type": "string" } } },
				    "untyped": { "properties": { "name": { "type": "string" } }, "required": ["name"] },
				    "anything": { "description": "Any value" },
				    "maybeLimit": { "anyOf": [ { "$ref": "#/$defs/limit" }, { "type": "null" } ] },
				    "maybeCount": { "anyOf": [ { "type": "integer" }, { "type": "null" } ], "description": "A count" },
				    "limitOrEmpty": {
				      "anyOf": [ { "$ref": "#/$defs/limit" }, { "type": "object", "properties": {}, "additionalProperties": false } ]
				    },
				    "Order": { "type": "string", "enum": ["asc", "desc"] },
				    "OtherOrder": { "type": "string", "enum": ["asc", "desc"] },
				    "user": { "type": "object", "properties": { "n": { "type": "string" } } },
				    "issue-event": { "title": "Event", "anyOf": [ { "$ref": "#/$defs/limit" }, { "$ref": "#/$defs/user" } ] }
				  }
				}
				""";

		@Test
		@DisplayName("every definition becomes a classifier")
		void allDefinitions() throws IOException {
			EPackage pkg = read(SCHEMA);

			for (String key : List.of("limit", "untyped", "anything", "maybeCount", "limitOrEmpty", "Order", "OtherOrder",
					"issue-event")) {
				assertNotNull(OpenApiStyle.classifier(pkg, key), key + " in " + pkg.getEClassifiers().stream().map(EClassifier::getName).toList());
			}
			assertNotNull(((EClass) OpenApiStyle.classifier(pkg, "untyped")).getEStructuralFeature("name"));
		}

		@Test
		@DisplayName("a nullable union of one simple type is that type")
		void nullableSimple() throws IOException {
			EClassifier count = OpenApiStyle.classifier(read(SCHEMA), "maybeCount");

			assertEquals(Integer.class, count.getInstanceClass());
		}

		@Test
		@DisplayName("anyOf is written back as anyOf")
		void anyOfKeyword() throws IOException {
			JsonNode limitOrEmpty = write(read(SCHEMA)).get("$defs").get("limitOrEmpty");

			assertNotNull(limitOrEmpty.get("anyOf"), limitOrEmpty.toString());
		}
	}

	@Nested
	@DisplayName("class names")
	class ClassNames {

		private static final String SCHEMA = """
				{
				  "$defs": {
				    "labeled-issue-event": { "type": "object", "properties": { "label": { "type": "string" } } },
				    "simple_user": { "type": "object", "properties": { "login": { "type": "string" } } },
				    "2fa-method": { "type": "string", "enum": ["sms", "app"] },
				    "Pet": { "type": "object", "properties": { "owner": { "$ref": "#/$defs/simple_user" } } }
				  }
				}
				""";

		@Test
		@DisplayName("are Java identifiers, invalid characters joining CamelCase parts; the key stays the original name")
		void sanitized() throws IOException {
			EPackage pkg = read(SCHEMA);

			assertEquals(List.of("LabeledIssueEvent", "Simple_user", "_2faMethod", "Pet"),
					pkg.getEClassifiers().stream().map(EClassifier::getName).toList());
			assertEquals("labeled-issue-event", EcoreUtil.getAnnotation(pkg.getEClassifier("LabeledIssueEvent"),
					AnnotationSources.JSONSCHEMA, "originalName"));
		}

		@Test
		@DisplayName("are written back under their keys")
		void keys() throws IOException {
			JsonNode defs = write(read(SCHEMA)).get("$defs");

			assertNotNull(defs.get("labeled-issue-event"), defs.toString());
			assertNotNull(defs.get("simple_user"), defs.toString());
			assertEquals("#/$defs/simple_user", defs.get("Pet").get("properties").get("owner").get("$ref").asString());
		}

		@Test
		@DisplayName("read back equal")
		void roundTrip() throws IOException {
			assertRoundTrip(SCHEMA);
		}
	}

	/** Finds a classifier by its key, as the OpenAPI codec does. */
	static final class OpenApiStyle {
		static EClassifier classifier(EPackage pkg, String key) {
			for (EClassifier classifier : pkg.getEClassifiers()) {
				if (key.equals(EcoreUtil.getAnnotation(classifier, AnnotationSources.JSONSCHEMA, "originalName"))
						|| key.equals(classifier.getName())) {
					return classifier;
				}
			}
			return null;
		}
	}

	private static void assertRoundTrip(String schema) throws IOException {
		EPackage first = read(schema);
		EPackage second = read(MAPPER.writeValueAsString(write(first)));

		assertTrue(EcoreUtil.equals(first, second),
				"round trip differs at " + firstDifference(first, second) + ":\n" + MAPPER.writeValueAsString(write(first)));
	}

	/** Names the first classifier or feature whose name, type, bounds or annotations differ. */
	private static String firstDifference(EPackage first, EPackage second) {
		if (first.getEClassifiers().size() != second.getEClassifiers().size()) {
			return "classifier count " + first.getEClassifiers().size() + " <> " + second.getEClassifiers().size();
		}
		for (int i = 0; i < first.getEClassifiers().size(); i++) {
			EClassifier a = first.getEClassifiers().get(i);
			EClassifier b = second.getEClassifiers().get(i);
			String d = describe(a).equals(describe(b)) ? null : describe(a) + " <> " + describe(b);
			if (d != null) {
				return d;
			}
			if (a instanceof EClass ca && b instanceof EClass cb) {
				for (int j = 0; j < Math.min(ca.getEStructuralFeatures().size(), cb.getEStructuralFeatures().size()); j++) {
					EStructuralFeature fa = ca.getEStructuralFeatures().get(j);
					EStructuralFeature fb = cb.getEStructuralFeatures().get(j);
					if (!describe(fa).equals(describe(fb))) {
						return a.getName() + ": " + describe(fa) + " <> " + describe(fb);
					}
				}
			}
		}
		return "package " + describe(first) + " <> " + describe(second);
	}

	private static String describe(ENamedElement e) {
		StringBuilder sb = new StringBuilder(e.eClass().getName()).append(' ').append(e.getName());
		if (e instanceof ETypedElement t) {
			sb.append(':').append(t.getEType() == null ? null : t.getEType().getName())
					.append('[').append(t.getLowerBound()).append("..").append(t.getUpperBound()).append(']');
		}
		if (e instanceof EClassifier c) {
			sb.append(" ic=").append(c.getInstanceClassName());
		}
		if (e instanceof EClass c) {
			sb.append(" super=").append(c.getESuperTypes().stream().map(EClass::getName).toList())
					.append(" features=").append(c.getEStructuralFeatures().size());
		}
		e.getEAnnotations().forEach(x -> sb.append(' ').append(x.getSource())
				.append(x.getDetails().stream().map(d -> d.getKey() + "=" + d.getValue()).toList()));
		return sb.toString();
	}

	private static EPackage read(String schema) throws IOException {
		return new JsonSchemaToEPackageConverter()
				.convert(new ByteArrayInputStream(schema.getBytes(StandardCharsets.UTF_8)), "$defs");
	}

	private static JsonNode write(EPackage pkg) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new EPackageToJsonSchemaConverter().convert(pkg, out, "$defs");
		return MAPPER.readTree(out.toByteArray());
	}
}
