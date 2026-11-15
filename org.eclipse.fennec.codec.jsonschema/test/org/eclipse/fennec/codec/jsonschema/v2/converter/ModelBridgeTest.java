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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.v2.model.SchemaJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** The converter takes the JSON Schema model and links each schema to its classifier (issue #318). */
@DisplayName("JSON Schema model → Ecore")
class ModelBridgeTest {

	private static final JsonMapper MAPPER = JsonMapper.builder().build();

	private static final String DEFINITIONS = """
			{
			  "Pet": {
			    "type": "object",
			    "required": ["name"],
			    "properties": {
			      "name": {"type": "string"},
			      "tags": {"type": "array", "items": {"type": "string"}},
			      "owner": {"$ref": "#/components/schemas/Owner"},
			      "kind": {"type": "string", "enum": ["cat", "dog"]}
			    }
			  },
			  "Owner": {"type": "object", "properties": {"pets": {"type": "array", "items": {"$ref": "#/components/schemas/Pet"}}}},
			  "Color": {"type": "string", "enum": ["red", "green"]},
			  "Shape": {"oneOf": [{"$ref": "#/components/schemas/Circle"}, {"$ref": "#/components/schemas/Square"}]},
			  "Circle": {"type": "object", "properties": {"radius": {"type": "number"}}},
			  "Square": {"type": "object", "properties": {"side": {"type": "number"}}}
			}
			""";

	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = { "meter-reading.json", "pipeline_schema.json", "top-level-eclass.json" })
	@DisplayName("a document gives the same Ecore from the model as from JSON")
	void documentSameAsFromJson(String file) throws IOException {
		JsonNode json = MAPPER.readTree(Path.of("test-data", file).toFile());
		EPackage fromJson = new JsonSchemaToEPackageConverter().convert(json, null);
		EPackage fromModel = new JsonSchemaToEPackageConverter().convert(SchemaJson.read(json), null);
		assertTrue(EcoreUtil.equals(fromJson, fromModel), "the Ecore differs for " + file);
	}

	@Test
	@DisplayName("definitions give the same Ecore from the model as from JSON")
	void definitionsSameAsFromJson() {
		JsonNode json = MAPPER.readTree(DEFINITIONS);
		EPackage fromJson = new JsonSchemaToEPackageConverter().convert(json, null);
		EPackage fromModel = new JsonSchemaToEPackageConverter().convertDefinitions(definitions(json));
		assertEquals(fromJson.getEClassifiers().size(), fromModel.getEClassifiers().size());
		for (int i = 0; i < fromJson.getEClassifiers().size(); i++) {
			assertTrue(EcoreUtil.equals(fromJson.getEClassifiers().get(i), fromModel.getEClassifiers().get(i)),
					fromJson.getEClassifiers().get(i).getName());
		}
	}

	@Test
	@DisplayName("each definition and each property schema has its classifier")
	void classifiersLinked() {
		Map<String, Schema> definitions = definitions(MAPPER.readTree(DEFINITIONS));
		EPackage ePackage = new JsonSchemaToEPackageConverter().convertDefinitions(definitions);

		EClass pet = (EClass) ePackage.getEClassifier("Pet");
		EClass owner = (EClass) ePackage.getEClassifier("Owner");
		assertSame(pet, definitions.get("Pet").getClassifier());
		assertSame(owner, definitions.get("Owner").getClassifier());
		assertTrue(definitions.get("Color").getClassifier() instanceof EEnum);
		assertSame(ePackage.getEClassifier("Shape"), definitions.get("Shape").getClassifier());

		Map<String, Schema> properties = definitions.get("Pet").getProperties().map();
		assertSame(EcorePackage.Literals.ESTRING, properties.get("name").getClassifier());
		assertSame(EcorePackage.Literals.ESTRING, properties.get("tags").getClassifier(), "an array: its item type");
		assertSame(owner, properties.get("owner").getClassifier(), "a $ref: its target");
		assertSame(pet.getEStructuralFeature("kind").getEType(), properties.get("kind").getClassifier());
		assertSame(pet, definitions.get("Owner").getProperties().get("pets").getClassifier());
	}

	@Test
	@DisplayName("a classifier set before is kept")
	void classifierKept() {
		Map<String, Schema> definitions = definitions(MAPPER.readTree(DEFINITIONS));
		definitions.get("Pet").setClassifier(EcorePackage.Literals.EOBJECT);
		new JsonSchemaToEPackageConverter().convertDefinitions(definitions);
		assertSame(EcorePackage.Literals.EOBJECT, definitions.get("Pet").getClassifier());
	}

	@Test
	@DisplayName("definitions are names only: none is read as a section or as root properties")
	void namesAreNamesOnly() {
		JsonNode json = MAPPER.readTree("""
				{"schemas": {"type": "object", "properties": {"a": {"type": "string"}}},
				 "properties": {"type": "object", "properties": {"b": {"type": "integer"}}}}
				""");
		EPackage ePackage = new JsonSchemaToEPackageConverter().convertDefinitions(definitions(json));
		assertNotNull(ePackage.getEClassifier("Schemas"));
		assertNotNull(ePackage.getEClassifier("Properties"));
		assertEquals(2, ePackage.getEClassifiers().size());
	}

	@Test
	@DisplayName("a schema map of model schemas takes the same way")
	void schemaMapOfModel() {
		Map<String, Schema> definitions = definitions(MAPPER.readTree(DEFINITIONS));
		EMap<String, EObject> map = new BasicEMap<>();
		definitions.forEach(map::put);
		EPackage ePackage = new JsonSchemaToEPackageConverter().convertFromSchemaMap(map);
		assertEquals("schemas", ePackage.getName());
		assertSame(ePackage.getEClassifier("Pet"), definitions.get("Pet").getClassifier());
	}

	private static Map<String, Schema> definitions(JsonNode json) {
		Map<String, Schema> definitions = new LinkedHashMap<>();
		json.properties().forEach(entry -> definitions.put(entry.getKey(), SchemaJson.read(entry.getValue())));
		return definitions;
	}
}
