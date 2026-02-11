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
import org.gecko.codec.demo.model.person.Address;
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
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

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
		@Property(key = "type", value="json"),
		@Property(key = "enableFeatures", value = "SerializationFeature.INDENT_OUTPUT", type = Type.Array),
		@Property(key = "disableFeatures", value={"JsonWriteFeature.ESCAPE_FORWARD_SLASHES"}, type = Type.Array)
})
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value = "json") })
public class CodecJsonSerializeTypeTest extends JsonTestSetting {

	@InjectService(cardinality = 0, filter="("+EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
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
	public void testSerializeTypeRootObjYES() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
				.serializeType(true)
				.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"_type\" : ")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertTrue(found);
		}
	}

//	@Test
//	public void testSerializeTypeRootObjMetadataNO() throws IOException {
//
//		Resource resource = resourceSet.createResource(URI.createURI(personFileName));
//
//		org.eclipse.fennec.codec.test.models.metadata.Person person = org.eclipse.fennec.codec.test.models.metadata.MetadataFactory.eINSTANCE.createPerson();
//		person.setKind("Person");
//		
//		resource.getContents().add(person);
//		Map<String, Object> options = new HashMap<>();
//		Map<String, Object> classOptions = new HashMap<>();
//		classOptions.put(CodecModelInfoOptions.CODEC_TYPE_KEY, "kind");
//		options.put(CodecResourceOptions.CODEC_OPTIONS, Map.of(org.eclipse.fennec.codec.test.models.metadata.MetadataPackage.Literals.PERSON, classOptions));
//		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_TYPE, true);
//		options.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, false);
//		resource.save(options);
//
//		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
//			String line = reader.readLine();
//			boolean found = false;
//			while (line != null) {
//				if (line.contains("\"kind\" : ")) {
//					found = true;
//				}
//				line = reader.readLine();
//			}
//			assertTrue(found);
//		}
//	}
	
//	@Test
//	public void testSerializeTypeRootObjMetadataYES() throws IOException {
//
//		Resource resource = resourceSet.createResource(URI.createURI(personFileName));
//
//		org.eclipse.fennec.codec.test.models.metadata.Person person = org.eclipse.fennec.codec.test.models.metadata.MetadataFactory.eINSTANCE.createPerson();
//		person.setKind("Person");
//		
//		resource.getContents().add(person);
//		Map<String, Object> options = new HashMap<>();
//		Map<String, Object> classOptions = new HashMap<>();
//		classOptions.put(CodecModelInfoOptions.CODEC_TYPE_KEY, "kind");
//		options.put(CodecResourceOptions.CODEC_OPTIONS, Map.of(org.eclipse.fennec.codec.test.models.metadata.MetadataPackage.Literals.PERSON, classOptions));
//		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_TYPE, true);
//		options.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, true);
//		resource.save(options);
//
//		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
//			String line = reader.readLine();
//			boolean found = false;
//			while (line != null) {
//				if (line.contains("\"typeOfPerson\" : ")) {
//					found = true;
//				}
//				line = reader.readLine();
//			}
//			assertTrue(found);
//		}
//	}

	
	@Test
	public void testSerializeTypeRootObjNO() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
				.serializeType(false)
				.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"_type\" : ")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(found);
		}
	}

	@Test
	public void testSerializeTypeContainedRefYES() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		Address address = CodecTestHelper.getTestAddress();
		person.setAddress(address);
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
				.serializeType(true)
				.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false, foundSecond = false;
			while (line != null) {
				if (line.contains("\"_type\" : ")) {
					if (found)
						foundSecond = true;
					found = true;
				}
				line = reader.readLine();
			}
			assertTrue(foundSecond);
		}
	}

	@Test
	public void testSerializeTypeContainedRefNO() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		Address address = CodecTestHelper.getTestAddress();
		person.setAddress(address);
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
				.serializeType(false)
				.build();
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false, foundSecond = false;
			while (line != null) {
				if (line.contains("\"_type\" : ")) {
					if (found)
						foundSecond = true;
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(foundSecond);
		}
	}

	@Test
	public void testSerializationNonContainedRefTypeYES() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(addFileName));
		Map<String, Object> options = CodecOptionsBuilder.create()
				.build();

		Person person = CodecTestHelper.getTestPerson();
		Address address = CodecTestHelper.getTestAddress();
		person.setNonContainedAdd(address);
		resource.getContents().add(address);
		resource.save(options);

		resource = resourceSet.createResource(URI.createURI(personFileName));
		resource.getContents().add(person);
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"_type\" : \"http://example.de/person/1.0#//Address\"")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertTrue(found);
		}
	}

	@Test
	public void testSerializationNonContainedRefTypeNO() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI(addFileName));
		Map<String, Object> options = CodecOptionsBuilder.create()
				.serializeType(false)
				.build();
		Person person = CodecTestHelper.getTestPerson();
		Address address = CodecTestHelper.getTestAddress();
		person.setNonContainedAdd(address);
		resource.getContents().add(address);
		resource.save(options);

		resource = resourceSet.createResource(URI.createURI(personFileName));
		resource.getContents().add(person);
		resource.save(options);

		try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains("\"_type\" : \"http://example.de/person/1.0#//Address\"")) {
					found = true;
				}
				line = reader.readLine();
			}
			assertFalse(found);
		}
	}

}
