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
package org.eclipse.fennec.codec.json.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.demo.model.test1.Meeting;
import org.eclipse.fennec.codec.demo.model.test1.Test1BusinessPerson;
import org.eclipse.fennec.codec.demo.model.test1.Test1Person;
import org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;


/**
 * Tests for codec.type annotation inheritance feature.
 * Verifies that EReferences automatically inherit type discrimination information
 * from their target EClass.
 *
 * See documentation here:
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
public class CodecJsonDeserializeTypeInheritanceTest extends JsonTestSetting{

	@InjectBundleContext
	BundleContext ctx;

	@InjectService(cardinality = 0, filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
	ServiceAware<ResourceSet> rsAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<ObjectMapperConfigurator> mapperAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;

	ResourceSet resourceSet = null;

	@BeforeEach
	public void before() throws InterruptedException {
		resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
		assertNotNull(codecFactoryAware.waitForService(2000l));
		assertNotNull(mapperAware.waitForService(2000l));
		assertNotNull(codecModuleAware.waitForService(2000l));
	}

	@AfterEach
	public void after() {
		resourceSet = null;
	}

	/**
	 * Test that Person with companyId field is deserialized as BusinessPerson.
	 * This verifies that the codec.type annotation on Person EClass works.
	 */
	@Test
	public void testDeserializeBusinessPersonFromPersonType(@InjectService(timeout = 2000l) TestTypeAnnotationInheritancePackage testModel) throws IOException {
		assertNotNull(testModel);

		String json = """
		{
			"name": "John",
			"lastName": "Doe",
			"companyId": "ACME-001"
		}
		""";

		Resource resource = resourceSet.createResource(URI.createURI("test.json"));
		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(testModel.getTest1Person())
				.serializeType(false)
				.forClass(testModel.getTest1Person())
				.typeKey("*")
				.build();

		InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		resource.load(is, options);

		assertThat(resource.getContents()).hasSize(1);
		EObject obj = resource.getContents().get(0);

		// Verify it's a BusinessPerson (not just Person)
		assertThat(obj).isInstanceOf(Test1BusinessPerson.class);
		Test1BusinessPerson businessPerson = (Test1BusinessPerson) obj;

		assertEquals("John", businessPerson.getName());
		assertEquals("Doe", businessPerson.getLastName());
		assertEquals("ACME-001", businessPerson.getCompanyId());
	}

	/**
	 * Test that Person without companyId field is deserialized as Person.
	 * This verifies that type discrimination only happens when the discriminator field is present.
	 */
	@Test
	public void testDeserializePersonWithoutDiscriminator(@InjectService(timeout = 2000l) TestTypeAnnotationInheritancePackage testModel) throws IOException {
		assertNotNull(testModel);

		String json = """
		{
			"name": "Jane",
			"lastName": "Smith"
		}
		""";

		Resource resource = resourceSet.createResource(URI.createURI("test.json"));
		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(testModel.getTest1Person())
				.serializeType(false)
				.build();

		InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		resource.load(is, options);

		assertThat(resource.getContents()).hasSize(1);
		EObject obj = resource.getContents().get(0);

		// Verify it's a Person (not BusinessPerson)
		assertThat(obj).isInstanceOf(Test1Person.class);
		assertThat(obj).isNotInstanceOf(Test1BusinessPerson.class);

		Test1Person person = (Test1Person) obj;
		assertEquals("Jane", person.getName());
		assertEquals("Smith", person.getLastName());
	}

	/**
	 * Test that Meeting.responsiblePerson inherits codec.type from Person EClass.
	 * This is the KEY test for the new inheritance feature:
	 * - Meeting.responsiblePerson reference has NO codec.type annotation
	 * - It should automatically inherit from Person (the target EClass)
	 * - When JSON has companyId, it should deserialize as BusinessPerson
	 */
	@Test
	public void testDeserializeMeetingWithBusinessPersonInheritance(@InjectService(timeout = 2000l) TestTypeAnnotationInheritancePackage testModel) throws IOException {
		assertNotNull(testModel);

		String json = """
		{
			"id": "meeting-001",
			"date": "2025-01-15T10:00:00Z",
			"responsiblePerson": {
				"name": "Alice",
				"lastName": "Johnson",
				"companyId": "TECH-123"
			}
		}
		""";

		Resource resource = resourceSet.createResource(URI.createURI("test.json"));
		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(testModel.getMeeting())
				.serializeType(false)
				.forClass(testModel.getMeeting())
				.forReference(testModel.getMeeting_ResponsiblePerson())
				.typeKey("*")
				.build();

		InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		resource.load(is, options);

		assertThat(resource.getContents()).hasSize(1);
		EObject obj = resource.getContents().get(0);

		assertThat(obj).isInstanceOf(Meeting.class);
		Meeting meeting = (Meeting) obj;

		assertEquals("meeting-001", meeting.getId());
		assertNotNull(meeting.getResponsiblePerson());

		// KEY ASSERTION: responsiblePerson should be BusinessPerson (not just Person)
		// This proves that the EReference inherited the codec.type annotation from Person
		assertThat(meeting.getResponsiblePerson()).isInstanceOf(Test1BusinessPerson.class);
		Test1BusinessPerson responsiblePerson = (Test1BusinessPerson) meeting.getResponsiblePerson();

		assertEquals("Alice", responsiblePerson.getName());
		assertEquals("Johnson", responsiblePerson.getLastName());
		assertEquals("TECH-123", responsiblePerson.getCompanyId());
	}

	/**
	 * Test that Meeting.responsiblePerson can be a regular Person (without discriminator).
	 * Verifies that inheritance doesn't break normal deserialization.
	 */
	@Test
	public void testDeserializeMeetingWithRegularPerson(@InjectService(timeout = 2000l) TestTypeAnnotationInheritancePackage testModel) throws IOException {
		assertNotNull(testModel);

		String json = """
		{
			"id": "meeting-002",
			"date": "2025-01-16T14:30:00Z",
			"responsiblePerson": {
				"name": "Bob",
				"lastName": "Williams"
			}
		}
		""";

		Resource resource = resourceSet.createResource(URI.createURI("test.json"));
		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(testModel.getMeeting())
				.serializeType(false)
				.build();

		InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		resource.load(is, options);

		assertThat(resource.getContents()).hasSize(1);
		EObject obj = resource.getContents().get(0);

		assertThat(obj).isInstanceOf(Meeting.class);
		Meeting meeting = (Meeting) obj;

		assertEquals("meeting-002", meeting.getId());
		assertNotNull(meeting.getResponsiblePerson());

		// Should be regular Person (not BusinessPerson)
		assertThat(meeting.getResponsiblePerson()).isInstanceOf(Test1Person.class);
		assertThat(meeting.getResponsiblePerson()).isNotInstanceOf(Test1BusinessPerson.class);

		Test1Person responsiblePerson = meeting.getResponsiblePerson();
		assertEquals("Bob", responsiblePerson.getName());
		assertEquals("Williams", responsiblePerson.getLastName());
	}

	/**
	 * Test that runtime option inheritsTypeFromParent(false) disables inheritance.
	 * When inheritance is disabled, responsiblePerson should always be Person (never BusinessPerson).
	 */
	@Test
	public void testDisableInheritanceViaRuntimeOption(@InjectService(timeout = 2000l) TestTypeAnnotationInheritancePackage testModel) throws IOException {
		assertNotNull(testModel);

		String json = """
		{
			"id": "meeting-003",
			"date": "2025-01-17T09:00:00Z",
			"responsiblePerson": {
				"name": "Charlie",
				"lastName": "Brown",
				"companyId": "COMPANY-999"
			}
		}
		""";

		Resource resource = resourceSet.createResource(URI.createURI("test.json"));
		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(testModel.getMeeting())
				.serializeType(false)
				.forClass(testModel.getMeeting())
					.forReference(testModel.getMeeting_ResponsiblePerson())
						.inheritsTypeFromParent(false)  // Disable inheritance!
					.and()
				.and()
				.build();

		InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		resource.load(is, options);

		assertThat(resource.getContents()).hasSize(1);
		EObject obj = resource.getContents().get(0);

		assertThat(obj).isInstanceOf(Meeting.class);
		Meeting meeting = (Meeting) obj;

		assertEquals("meeting-003", meeting.getId());
		assertNotNull(meeting.getResponsiblePerson());

		// With inheritance disabled, responsiblePerson should be Person (not BusinessPerson)
		// even though JSON has companyId field
		assertThat(meeting.getResponsiblePerson()).isInstanceOf(Test1Person.class);
		assertThat(meeting.getResponsiblePerson()).isNotInstanceOf(Test1BusinessPerson.class);

		Test1Person responsiblePerson = meeting.getResponsiblePerson();
		assertEquals("Charlie", responsiblePerson.getName());
		assertEquals("Brown", responsiblePerson.getLastName());
	}
}
