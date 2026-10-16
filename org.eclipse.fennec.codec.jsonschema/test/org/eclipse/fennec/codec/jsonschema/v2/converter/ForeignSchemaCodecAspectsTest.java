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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * A foreign schema produces the codec aspects under which the codec reads and writes the JSON the
 * schema describes (issue #277, spec 21 §3).
 */
@DisplayName("Foreign schema → codec aspects (#277)")
class ForeignSchemaCodecAspectsTest {

	private static final String NS_URI = "https://example.org/petstore/1.0.0";

	private static final String SCHEMAS = """
			{
			  "Pet": {
			    "type": "object",
			    "required": ["id", "name"],
			    "properties": {
			      "id": { "type": "integer", "format": "int64" },
			      "name": { "type": "string" },
			      "tag": { "type": "string", "nullable": true },
			      "nickname": { "type": ["string", "null"] },
			      "status": { "type": "string", "enum": ["available", "pending", "sold-out"] },
			      "category": { "$ref": "#/components/schemas/Category" },
			      "tags": { "type": "array", "items": { "$ref": "#/components/schemas/Tag" } },
			      "vaccinated": { "type": "boolean" },
			      "age": { "type": "integer", "format": "int32" },
			      "weight": { "type": "number" },
			      "birthday": { "type": "string", "format": "date-time" }
			    }
			  },
			  "Category": {
			    "type": "object",
			    "properties": {
			      "id": { "type": "integer" },
			      "name": { "type": "string" }
			    }
			  },
			  "Tag": {
			    "type": "object",
			    "properties": {
			      "id": { "type": "integer" },
			      "name": { "type": "string" }
			    }
			  }
			}
			""";

	private static final String DISCRIMINATED = """
			{
			  "Pet": {
			    "type": "object",
			    "required": ["petType"],
			    "properties": {
			      "name": { "type": "string" },
			      "petType": { "type": "string" }
			    },
			    "discriminator": {
			      "propertyName": "petType",
			      "mapping": { "dog": "#/components/schemas/Dog" }
			    }
			  },
			  "Dog": {
			    "allOf": [
			      { "$ref": "#/components/schemas/Pet" },
			      { "type": "object", "properties": { "bark": { "type": "boolean" } } }
			    ]
			  },
			  "Cat": {
			    "allOf": [
			      { "$ref": "#/components/schemas/Pet" },
			      { "type": "object", "properties": { "lives": { "type": "integer" } } }
			    ]
			  },
			  "Owner": {
			    "type": "object",
			    "properties": {
			      "pets": { "type": "array", "items": { "$ref": "#/components/schemas/Pet" } }
			    }
			  }
			}
			""";

	private final JsonMapper mapper = JsonMapper.builder().build();
	private MetadataWhiteboard metadataService;

	@BeforeEach
	void setUp() {
		metadataService = MetadataServiceFactory.create();
	}

	@Test
	@DisplayName("a $ref is a containment reference, x-containment: false keeps a non-containment")
	void refIsContainment() {
		EPackage ePackage = convert(SCHEMAS);
		EClass pet = (EClass) ePackage.getEClassifier("Pet");

		assertTrue(((EReference) pet.getEStructuralFeature("category")).isContainment());
		assertTrue(((EReference) pet.getEStructuralFeature("tags")).isContainment());

		EPackage marked = convert("""
				{
				  "A": { "type": "object", "properties": {
				    "b": { "$ref": "#/components/schemas/B", "x-containment": false } } },
				  "B": { "type": "object", "properties": { "id": { "type": "string" } } }
				}
				""");
		EClass a = (EClass) marked.getEClassifier("A");
		assertFalse(((EReference) a.getEStructuralFeature("b")).isContainment());
	}

	@Test
	@DisplayName("scalars are wrapper types, integer without int32 is a long")
	void scalarsAreWrapperTypes() {
		EClass pet = (EClass) convert(SCHEMAS).getEClassifier("Pet");

		assertEquals(EcorePackage.Literals.ELONG_OBJECT, type(pet, "id"));
		assertEquals(EcorePackage.Literals.EINTEGER_OBJECT, type(pet, "age"));
		assertEquals(EcorePackage.Literals.EDOUBLE_OBJECT, type(pet, "weight"));
		assertEquals(EcorePackage.Literals.EBOOLEAN_OBJECT, type(pet, "vaccinated"));
		assertEquals(EcorePackage.Literals.ESTRING, type(pet, "birthday"));
	}

	@Test
	@DisplayName("enum literals keep the value, names are valid Ecore names")
	void enumLiterals() {
		EClass pet = (EClass) convert(SCHEMAS).getEClassifier("Pet");
		EEnum status = (EEnum) type(pet, "status");

		assertEquals("sold-out", status.getELiterals().get(2).getLiteral());
		assertTrue(status.getELiterals().get(2).getName().matches("[A-Za-z_][A-Za-z0-9_]*"),
				status.getELiterals().get(2).getName());
	}

	@Test
	@DisplayName("a payload survives load and save unchanged")
	void payloadRoundTrip() throws IOException {
		EPackage ePackage = convert(SCHEMAS);
		String payload = """
				{
				  "id": 9007199254740993,
				  "name": "Rex",
				  "tag": null,
				  "nickname": null,
				  "status": "sold-out",
				  "category": { "id": 0, "name": "Dogs" },
				  "tags": [ { "id": 1, "name": "good" } ],
				  "vaccinated": false,
				  "age": 0,
				  "weight": 12.5,
				  "birthday": "2020-01-02T03:04:05.120+01:00"
				}
				""";

		assertPayloadRoundTrip(ePackage, "Pet", payload);
	}

	@Test
	@DisplayName("an absent nullable member stays absent")
	void absentNullableStaysAbsent() throws IOException {
		assertPayloadRoundTrip(convert(SCHEMAS), "Pet", """
				{ "id": 1, "name": "Rex" }
				""");
	}

	@Test
	@DisplayName("the discriminator picks the subclass and is kept as attribute")
	void discriminator() throws IOException {
		EPackage ePackage = convert(DISCRIMINATED);
		String payload = """
				{ "pets": [
				    { "name": "Rex", "petType": "dog", "bark": true },
				    { "name": "Tom", "petType": "Cat", "lives": 9 }
				] }
				""";

		CodecResource resource = load(ePackage, "Owner", payload);
		EObject owner = resource.getContents().get(0);
		@SuppressWarnings("unchecked")
		List<EObject> pets = (List<EObject>) owner.eGet(
				owner.eClass().getEStructuralFeature("pets"));
		assertEquals("Dog", pets.get(0).eClass().getName());
		assertEquals("Cat", pets.get(1).eClass().getName());
		assertEquals("dog", pets.get(0).eGet(pets.get(0).eClass().getEStructuralFeature("petType")));

		assertEquals(mapper.readTree(payload), save(resource));
	}

	private static final String OPEN = """
			{
			  "Settings": {
			    "type": "object",
			    "properties": { "name": { "type": "string" } },
			    "additionalProperties": true
			  },
			  "Inventory": {
			    "type": "object",
			    "properties": { "owner": { "type": "string" } },
			    "additionalProperties": { "$ref": "#/components/schemas/Stock" }
			  },
			  "Stock": {
			    "type": "object",
			    "properties": { "count": { "type": "integer" } }
			  }
			}
			""";

	private static final String UNIONS = """
			{
			  "Circle": {
			    "type": "object", "required": ["radius"],
			    "properties": { "radius": { "type": "number" } }
			  },
			  "Rect": {
			    "type": "object", "required": ["width", "height"],
			    "properties": { "width": { "type": "number" }, "height": { "type": "number" } }
			  },
			  "Shape": { "oneOf": [ { "$ref": "#/components/schemas/Circle" }, { "$ref": "#/components/schemas/Rect" } ] },
			  "Event": {
			    "oneOf": [ { "$ref": "#/components/schemas/Created" }, { "$ref": "#/components/schemas/Deleted" } ],
			    "discriminator": { "propertyName": "kind", "mapping": { "created": "#/components/schemas/Created" } }
			  },
			  "Created": {
			    "type": "object", "properties": { "kind": { "type": "string" }, "at": { "type": "string" } }
			  },
			  "Deleted": {
			    "type": "object", "properties": { "kind": { "type": "string" }, "id": { "type": "string" } }
			  },
			  "Drawing": {
			    "type": "object",
			    "properties": {
			      "shapes": { "type": "array", "items": { "$ref": "#/components/schemas/Shape" } },
			      "events": { "type": "array", "items": { "$ref": "#/components/schemas/Event" } }
			    }
			  }
			}
			""";

	@Test
	@DisplayName("additionalProperties: true keeps undeclared members (#278)")
	void additionalPropertiesRaw() throws IOException {
		assertPayloadRoundTrip(convert(OPEN), "Settings", """
				{ "name": "s", "theme": "dark", "limits": { "max": 3 }, "beta": false }
				""");
	}

	@Test
	@DisplayName("additionalProperties with $ref reads undeclared members as objects (#278)")
	void additionalPropertiesRef() throws IOException {
		EPackage ePackage = convert(OPEN);
		String payload = """
				{ "owner": "o", "apples": { "count": 3 }, "pears": { "count": 0 } }
				""";
		assertPayloadRoundTrip(ePackage, "Inventory", payload);
		EObject inventory = load(ePackage, "Inventory", payload).getContents().get(0);
		@SuppressWarnings("unchecked")
		EMap<String, EObject> stock = (EMap<String, EObject>) inventory.eGet(
				inventory.eClass().getEStructuralFeature("additionalProperties"));
		assertEquals("Stock", stock.get("apples").eClass().getName());
	}

	@Test
	@DisplayName("oneOf without discriminator infers the subclass, with discriminator maps it (#279)")
	void unions() throws IOException {
		EPackage ePackage = convert(UNIONS);
		String payload = """
				{
				  "shapes": [ { "radius": 1.5 }, { "width": 1.0, "height": 2.0 } ],
				  "events": [ { "kind": "created", "at": "now" }, { "kind": "Deleted", "id": "x" } ]
				}
				""";
		EObject drawing = load(ePackage, "Drawing", payload).getContents().get(0);
		@SuppressWarnings("unchecked")
		List<EObject> shapes = (List<EObject>) drawing.eGet(drawing.eClass().getEStructuralFeature("shapes"));
		@SuppressWarnings("unchecked")
		List<EObject> events = (List<EObject>) drawing.eGet(drawing.eClass().getEStructuralFeature("events"));
		assertEquals(List.of("Circle", "Rect"), shapes.stream().map(o -> o.eClass().getName()).toList());
		assertEquals(List.of("Created", "Deleted"), events.stream().map(o -> o.eClass().getName()).toList());
		assertPayloadRoundTrip(ePackage, "Drawing", payload);
	}

	private void assertPayloadRoundTrip(EPackage ePackage, String rootClass, String payload) throws IOException {
		CodecResource resource = load(ePackage, rootClass, payload);
		assertTrue(resource.getErrors().isEmpty(), resource.getErrors().toString());
		assertTrue(resource.getWarnings().isEmpty(), resource.getWarnings().toString());
		assertEquals(mapper.readTree(payload), save(resource));
	}

	private CodecResource load(EPackage ePackage, String rootClass, String payload) throws IOException {
		metadataService.registerPackage(ePackage);
		CodecResource resource = new CodecResource(URI.createURI("test://payload.json"), metadataService,
				ConfigurationResolver.defaults(), null);
		resource.load(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)),
				Map.of(CodecResource.CODEC_ROOT_TYPE, ePackage.getEClassifier(rootClass)));
		return resource;
	}

	private JsonNode save(CodecResource resource) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, null);
		return mapper.readTree(out.toString(StandardCharsets.UTF_8));
	}

	private EPackage convert(String schemas) {
		EPackage ePackage = new JsonSchemaToEPackageConverter().convert(mapper.readTree(schemas), null);
		ePackage.setName("petstore");
		ePackage.setNsPrefix("petstore");
		ePackage.setNsURI(NS_URI);
		new ResourceImpl(URI.createURI(NS_URI)).getContents().add(ePackage);
		return ePackage;
	}

	private static Object type(EClass eClass, String feature) {
		return ((EAttribute) eClass.getEStructuralFeature(feature)).getEType();
	}
}
