/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.eclipse.fennec.codec.options.CodecResourceOptions;
import org.gecko.codec.demo.model.person.Person;
import org.gecko.codec.demo.model.person.PersonFactory;
import org.gecko.codec.demo.model.person.PersonPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the non-OSGi codec setup.
 *
 * @author Mark Hoffmann
 * @since 02.12.2025
 */
class CodecNoOSGiTest {

	private CodecSetup codecSetup;

	@BeforeEach
	void before() {
		codecSetup = new CodecSetup();
		codecSetup.registerEPackage(PersonPackage.eINSTANCE);
	}

	@Test
	void testSerializePerson() throws Exception {
		// Create a Person
		Person person = PersonFactory.eINSTANCE.createPerson();
		person.setId("p001");
		person.setName("John");
		person.setLastName("Doe");
		person.setAge(30);
		person.setMarried(true);

		// Create ResourceSet and Resource
		ResourceSet rs = codecSetup.createResourceSet();
		Resource resource = rs.createResource(URI.createURI("test.json"));
		resource.getContents().add(person);

		// Serialize to ByteArrayOutputStream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		resource.save(baos, null);

		String json = baos.toString(StandardCharsets.UTF_8);
		System.out.println("Serialized JSON:\n" + json);

		// Verify the JSON contains expected content
		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertTrue(json.contains("John"));
		assertTrue(json.contains("Doe"));
		assertTrue(json.contains("30"));
	}

	@Test
	void testDeserializePerson() throws Exception {
		// JSON to deserialize
		String json = """
			{
				"_type": "Person",
				"_id": "Jane-Smith",
				"name": "Jane",
				"lastName": "Smith",
				"age": 25,
				"married": false
			}
			""";

		// Create ResourceSet and Resource
		ResourceSet rs = codecSetup.createResourceSet();
		Resource resource = rs.createResource(URI.createURI("test.json"));

		// Deserialize from ByteArrayInputStream with CODEC_ROOT_OBJECT option
		ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		Map<String, Object> loadOptions = new HashMap<>();
		loadOptions.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getPerson());
		resource.load(bais, loadOptions);

		// Verify the deserialized object
		assertFalse(resource.getContents().isEmpty());
		assertTrue(resource.getContents().get(0) instanceof Person);

		Person person = (Person) resource.getContents().get(0);
		assertEquals("Jane", person.getName());
		assertEquals("Smith", person.getLastName());
		assertEquals(25, person.getAge());
		assertFalse(person.isMarried());
	}

	@Test
	void testRoundTrip() throws Exception {
		// Create original Person
		Person original = PersonFactory.eINSTANCE.createPerson();
		original.setId("p002");
		original.setName("Alice");
		original.setLastName("Wonder");
		original.setAge(28);
		original.setMarried(false);
		original.setHeight(1.75);
		original.setWeight(65.5f);

		// Serialize
		ResourceSet rs1 = codecSetup.createResourceSet();
		Resource resource1 = rs1.createResource(URI.createURI("test.json"));
		resource1.getContents().add(original);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		resource1.save(baos, null);

		String json = baos.toString(StandardCharsets.UTF_8);
		System.out.println("Round-trip JSON:\n" + json);

		// Deserialize with CODEC_ROOT_OBJECT option
		ResourceSet rs2 = codecSetup.createResourceSet();
		Resource resource2 = rs2.createResource(URI.createURI("test2.json"));

		ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		Map<String, Object> loadOptions = new HashMap<>();
		loadOptions.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getPerson());
		resource2.load(bais, loadOptions);

		// Verify round-trip
		assertFalse(resource2.getContents().isEmpty());
		Person loaded = (Person) resource2.getContents().get(0);

		assertEquals(original.getName(), loaded.getName());
		assertEquals(original.getLastName(), loaded.getLastName());
		assertEquals(original.getAge(), loaded.getAge());
		assertEquals(original.isMarried(), loaded.isMarried());
		assertEquals(original.getHeight(), loaded.getHeight());
		assertEquals(original.getWeight(), loaded.getWeight());
	}

	@Test
	void testCustomConfiguration() throws Exception {
		// Create setup with custom configuration
		CodecSetup customSetup = new CodecSetup(
			DefaultCodecModuleConfig.builder()
				.serializeType(true)
				.useId(true)
				.idOnTop(true)
				.build()
		);
		customSetup.registerEPackage(PersonPackage.eINSTANCE);

		Person person = PersonFactory.eINSTANCE.createPerson();
		person.setId("p003");
		person.setName("Bob");
		person.setLastName("Builder");

		ResourceSet rs = customSetup.createResourceSet();
		Resource resource = rs.createResource(URI.createURI("custom.json"));
		resource.getContents().add(person);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		resource.save(baos, null);

		String json = baos.toString(StandardCharsets.UTF_8);
		System.out.println("Custom config JSON:\n" + json);

		assertNotNull(json);
		assertTrue(json.contains("Bob"));
		assertTrue(json.contains("Builder"));
	}

	@Test
	void testRoundTripWithUriTypeStrategy() throws Exception {
		// Create original Person
		Person original = PersonFactory.eINSTANCE.createPerson();
		original.setId("p004");
		original.setName("Charlie");
		original.setLastName("Brown");
		original.setAge(35);

		// Build save options with URI type strategy
		Map<String, Object> saveOptions = CodecOptionsBuilder.create()
				.forClass(PersonPackage.eINSTANCE.getPerson())
					.typeStrategy("URI")
				.build();

		// Serialize
		ResourceSet rs1 = codecSetup.createResourceSet();
		Resource resource1 = rs1.createResource(URI.createURI("test.json"));
		resource1.getContents().add(original);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		resource1.save(baos, saveOptions);

		String json = baos.toString(StandardCharsets.UTF_8);
		System.out.println("URI Type Strategy JSON:\n" + json);

		// Verify the JSON contains the URI type format
		assertTrue(json.contains("_type\":\"http://example.de/person/1.0#//Person"));

		// Build load options with URI type strategy, root object, and deserializeType enabled
		Map<String, Object> loadOptions = CodecOptionsBuilder.create()
				.rootObject(PersonPackage.eINSTANCE.getPerson())
				.deserializeType(true)
				.forClass(PersonPackage.eINSTANCE.getPerson())
					.typeStrategy("URI")
				.build();

		// Deserialize
		ResourceSet rs2 = codecSetup.createResourceSet();
		Resource resource2 = rs2.createResource(URI.createURI("test2.json"));

		ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		resource2.load(bais, loadOptions);

		// Verify round-trip - note: name/lastName are used for _id computation, not serialized separately
		assertFalse(resource2.getContents().isEmpty());
		Person loaded = (Person) resource2.getContents().get(0);

		// Verify age is preserved (name/lastName are part of id strategy, not direct features)
		assertEquals(original.getAge(), loaded.getAge());
		assertEquals(original.getId(), loaded.getId());
	}
}
