/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.eclipse.fennec.codec.test.helper.CodecTestHelper;
import org.gecko.codec.demo.model.person.Person;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import tools.jackson.databind.SerializationFeature;

/**
 * See documentation here: https://github.com/osgi/osgi-test
 * https://github.com/osgi/osgi-test/wiki Examples:
 * https://github.com/osgi/osgi-test/tree/main/examples
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(MockitoExtension.class)
@ExtendWith(ConfigurationExtension.class)
@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value = "json") })
@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value = "json") })
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value = "json") })
public class CodecJsonSerializeEmptyValuesTest extends JsonTestSetting {

	@InjectService(cardinality = 0, filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
	ServiceAware<ResourceSet> rsAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<ObjectMapperConfigurator> mapperAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;

	private ResourceSet resourceSet;

	@BeforeEach()
	@Override
	public void beforeEach() throws Exception {
		super.beforeEach();
		codecFactoryAware.waitForService(2000l);
		mapperAware.waitForService(2000l);
		codecModuleAware.waitForService(2000l);
		resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
	}

	@AfterEach()
	@Override
	public void afterEach() throws IOException {
		super.afterEach();
	}

	@Test
	public void testSerializationEmptyManyValuesNODefYES() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.getTitles().clear();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(true)
			.serializeEmptyValue(false)
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"title\" : [ ]")) { // we need to check against "title" and not "titles" because
														// use-name-from-extended-metadata is true by default
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(found);
		}
	}

	@Test
	public void testSerializationEmptyManyRefNODefYES() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.getAddresses().clear();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(true)
			.serializeEmptyValue(false)
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"addresses\" : [ ]")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(found);
		}
	}

	@Test
	public void testSerializationEmptyManyRefNODefNO() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.getAddresses().clear();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(false)
			.serializeEmptyValue(false)
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"addresses\" : [ ]")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(found);
		}
	}

	@Test
	public void testSerializationEmptyManyRefYESDefNO() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.getAddresses().clear();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(false)
			.serializeEmptyValue(true)
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"addresses\" : [ ]")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(found);
		}
	}

	@Test
	public void testSerializationEmptyManyRefYESDefYES() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.getAddresses().clear();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(true)
			.serializeEmptyValue(true)
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"addresses\" : [ ]")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertTrue(found);
		}
	}

	@Test
	public void testSerializationEmptyManyValuesYESDefNO() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.getTitles().clear();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(false)
			.serializeEmptyValue(true)
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"title\" : [ ]")) {// we need to check against "title" and not "titles" because
														// use-name-from-extended-metadata is true by default
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(found);
		}
	}

	@Test
	public void testSerializationEmptyManyValuesNODefNO() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.getTitles().clear();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(false)
			.serializeEmptyValue(false)
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"title\" : [ ]")) {// we need to check against "title" and not "titles" because
														// use-name-from-extended-metadata is true by default
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(found);
		}
	}

	@Test
	public void testSerializationEmptyManyValuesYESDefYES() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.getTitles().clear();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(true)
			.serializeEmptyValue(true)
			.useNamesFromExtendedMetadata(true)
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"title\" : [ ]")) {// we need to check against "title" and not "titles" because
														// use-name-from-extended-metadata is true by default
					found = true;
				}
				line = reader.readLine();
			}
			assertTrue(found);
		}
	}

	@Test
	public void testSerializationEmptySingleValueYES() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.setLastName("");
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeEmptyValue(true)
			.serializeIdField(true) // lastName is part of the id in the model so we need this option as well to get it serialized
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"lastName\" : \"\",")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertTrue(found);
		}
	}

	@Test
	public void testSerializationEmptySingleValueNO() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		person.setLastName("");
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeEmptyValue(false)
			.serializeIdField(true) // lastName is part of the id in the model so we need this option as well to get it serialized
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"lastName\" : \"\",")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(found);
		}
	}
}
