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

	private static void assertRoundTrip(String schema) throws IOException {
		EPackage first = read(schema);
		EPackage second = read(MAPPER.writeValueAsString(write(first)));

		assertTrue(EcoreUtil.equals(first, second),
				"round trip differs:\n" + MAPPER.writeValueAsString(write(first)));
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
