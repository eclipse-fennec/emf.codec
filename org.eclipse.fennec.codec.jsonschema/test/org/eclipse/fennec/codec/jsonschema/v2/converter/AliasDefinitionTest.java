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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.v2.model.SchemaJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** A definition that is only a {@code $ref} is another name of its target (spec 22 §3, rule 13). */
@DisplayName("JSON Schema → Ecore: alias definitions")
class AliasDefinitionTest {

	private static final JsonMapper MAPPER = JsonMapper.builder().build();

	private static final String DEFINITIONS = """
			{
			  "Pet": {"type": "object", "properties": {"name": {"type": "string"}}},
			  "PetAlias": {"$ref": "#/components/schemas/Pet", "description": "another name"},
			  "PetAliasAlias": {"$ref": "#/components/schemas/PetAlias"},
			  "Color": {"type": "string", "enum": ["red", "green"]},
			  "ColorAlias": {"$ref": "#/components/schemas/Color"},
			  "Loop1": {"$ref": "#/components/schemas/Loop2"},
			  "Loop2": {"$ref": "#/components/schemas/Loop1"},
			  "Lost": {"$ref": "#/components/schemas/Missing"},
			  "Owner": {
			    "type": "object",
			    "properties": {
			      "pet": {"$ref": "#/components/schemas/PetAlias"},
			      "pets": {"type": "array", "items": {"$ref": "#/components/schemas/PetAliasAlias"}},
			      "color": {"$ref": "#/components/schemas/ColorAlias"},
			      "loop": {"$ref": "#/components/schemas/Loop1"}
			    }
			  },
			  "Dog": {"allOf": [{"$ref": "#/components/schemas/PetAlias"}, {"type": "object", "properties": {"breed": {"type": "string"}}}]}
			}
			""";

	@Test
	@DisplayName("an alias has no classifier of its own")
	void noClassifierOfItsOwn() {
		EPackage ePackage = convert(definitions());
		assertNull(ePackage.getEClassifier("PetAlias"));
		assertNull(ePackage.getEClassifier("PetAliasAlias"));
		assertNull(ePackage.getEClassifier("ColorAlias"));
	}

	@Test
	@DisplayName("a $ref to an alias is a $ref to its target, also through a chain")
	void refToAliasIsRefToTarget() {
		EPackage ePackage = convert(definitions());
		EClass pet = (EClass) ePackage.getEClassifier("Pet");
		EClass owner = (EClass) ePackage.getEClassifier("Owner");

		assertSame(pet, ((EReference) owner.getEStructuralFeature("pet")).getEType());
		assertSame(pet, ((EReference) owner.getEStructuralFeature("pets")).getEType());
		assertTrue(owner.getEStructuralFeature("pets").isMany());
		EAttribute color = (EAttribute) owner.getEStructuralFeature("color");
		assertSame(ePackage.getEClassifier("Color"), color.getEType());
		assertTrue(color.getEType() instanceof EEnum);
	}

	@Test
	@DisplayName("allOf of an alias has its target as supertype")
	void allOfAlias() {
		EPackage ePackage = convert(definitions());
		EClass dog = (EClass) ePackage.getEClassifier("Dog");
		assertEquals(1, dog.getESuperTypes().size());
		assertSame(ePackage.getEClassifier("Pet"), dog.getESuperTypes().get(0));
	}

	@Test
	@DisplayName("the alias schema links to the classifier of its target")
	void aliasSchemaLinked() {
		Map<String, Schema> definitions = definitions();
		EPackage ePackage = convert(definitions);
		assertSame(ePackage.getEClassifier("Pet"), definitions.get("PetAlias").getClassifier());
		assertSame(ePackage.getEClassifier("Pet"), definitions.get("PetAliasAlias").getClassifier());
		assertSame(ePackage.getEClassifier("Color"), definitions.get("ColorAlias").getClassifier());
	}

	@Test
	@DisplayName("a cycle of aliases and an alias of nothing stay unresolved")
	void unresolvable() {
		Map<String, Schema> definitions = definitions();
		EPackage ePackage = convert(definitions);
		assertNull(definitions.get("Loop1").getClassifier());
		assertNull(definitions.get("Lost").getClassifier());
		EClass owner = (EClass) ePackage.getEClassifier("Owner");
		assertEquals("true", owner.getEStructuralFeature("loop")
				.getEAnnotation("http://fennec.eclipse.org/jsonschema").getDetails().get("unresolvedType"));
	}

	private static EPackage convert(Map<String, Schema> definitions) {
		return new JsonSchemaToEPackageConverter().convertDefinitions(definitions);
	}

	private static Map<String, Schema> definitions() {
		JsonNode json = MAPPER.readTree(DEFINITIONS);
		Map<String, Schema> definitions = new LinkedHashMap<>();
		json.properties().forEach(entry -> definitions.put(entry.getKey(), SchemaJson.read(entry.getValue())));
		return definitions;
	}
}
