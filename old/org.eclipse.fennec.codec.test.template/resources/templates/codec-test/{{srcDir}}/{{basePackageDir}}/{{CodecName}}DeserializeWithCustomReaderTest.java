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
package org.gecko.codec.json.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gecko.codec.configurator.CodecFactoryConfigurator;
import org.gecko.codec.configurator.CodecModuleConfigurator;
import org.gecko.codec.configurator.ObjectMapperConfigurator;
import org.gecko.codec.constants.CodecModelInfoOptions;
import org.gecko.codec.constants.CodecModuleOptions;
import org.gecko.codec.constants.CodecResourceOptions;
import org.gecko.codec.demo.model.person.Address;
import org.gecko.codec.demo.model.person.Person;
import org.gecko.codec.demo.model.person.PersonPackage;
import org.gecko.codec.test.helper.CodecTestHelper;
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

//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;

/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(MockitoExtension.class)
@ExtendWith(ConfigurationExtension.class)
@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="{{CodecType}}")
})
@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="{{CodecType}}")
})
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="{{CodecType}}")
})
public class {{CodecName}}DeserializeWithCustomReaderTest extends CodecTestSetting{

	@InjectService(cardinality = 0, filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
	ServiceAware<ResourceSet> rsAware;
	
	@InjectService(cardinality = 0, filter = "(type={{CodecType}})")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;
	
	@InjectService(cardinality = 0, filter = "(type={{CodecType}})")
	ServiceAware<ObjectMapperConfigurator> mapperAware;
	
	@InjectService(cardinality = 0, filter = "(type={{CodecType}})")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;
	
	private ResourceSet resourceSet;	
	
	@BeforeEach() 
	public void beforeEach() throws Exception{
		
		
	}
	
	@AfterEach() 
	public void afterEach() {
		
	}
	
	public URI getPersonURI() {
//		TODO: implement this method
		return null;
	}
	
	public URI getAddressURI() {
//		TODO: implement this method
		return null;
	}
	

	@Test
	public void testDeserializationIdCustomReader() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(getAddressURI());

		Address address = CodecTestHelper.getTestAddress();
		resource.getContents().add(address);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> addOptions = new HashMap<>();
		addOptions.put(CodecAnnotations.CODEC_ID_VALUE_WRITER, CodecTestHelper.TEST_VALUE_WRITER);
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_ID_FIELD, true);

		classOptions.put(PersonPackage.eINSTANCE.getAddress(), addOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		Resource findResource = resourceSet.createResource(getAddressURI());
		options = new HashMap<>();
		classOptions = new HashMap<>();
		addOptions = new HashMap<>();

		addOptions.put(CodecAnnotations.CODEC_ID_VALUE_READER, CodecTestHelper.TEST_VALUE_READER);
		classOptions.put(PersonPackage.eINSTANCE.getAddress(), addOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getAddress());
		findResource.load(options);

		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Address add = (Address) findResource.getContents().get(0);
		assertNotNull(add);
		assertEquals(address.getId(), add.getId());
	}
	
	@Test
	public void testDeserializationIdCombinedCustomReader() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(getPersonURI());

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> personOptions = new HashMap<>();
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_ID_FIELD, true);
		personOptions.put(CodecAnnotations.CODEC_ID_VALUE_WRITER, CodecTestHelper.TEST_VALUE_WRITER);
		classOptions.put(PersonPackage.eINSTANCE.getPerson(), personOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		Resource findResource = resourceSet.createResource(getPersonURI());
		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getPerson());
		personOptions.put(CodecAnnotations.CODEC_ID_VALUE_READER, CodecTestHelper.TEST_VALUE_READER);

		findResource.load(options);

		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);
		assertEquals("John", p.getName());
		assertEquals("Doe", p.getLastName());
	}
	
	
	@Test
	public void testDeserializationTypeCustomReader() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(getAddressURI());

		Address address = CodecTestHelper.getTestAddress();
		resource.getContents().add(address);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> addOptions = new HashMap<>();
		addOptions.put(CodecAnnotations.CODEC_TYPE_VALUE_WRITER, CodecTestHelper.TEST_TYPE_WRITER);

		classOptions.put(PersonPackage.eINSTANCE.getAddress(), addOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		Resource findResource = resourceSet.createResource(getAddressURI());
		options = new HashMap<>();
		classOptions = new HashMap<>();
		addOptions = new HashMap<>();

		addOptions.put(CodecAnnotations.CODEC_TYPE_VALUE_READER, CodecTestHelper.TEST_TYPE_READER);
		classOptions.put(PersonPackage.eINSTANCE.getAddress(), addOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getAddress());
		findResource.load(options);

		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Address add = (Address) findResource.getContents().get(0);
		assertNotNull(add);
	}
	
	@Test
	public void testDeserializationCustomReaderSingleAttribute() throws InterruptedException, IOException {


		Resource resource = resourceSet.createResource(getPersonURI());

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> personOptions = new HashMap<>();
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_ID_FIELD, true);
		personOptions.put(CodecAnnotations.CODEC_VALUE_WRITERS_MAP, Map.of(PersonPackage.eINSTANCE.getPerson_Name(), CodecTestHelper.TEST_VALUE_WRITER));
		personOptions.put(CodecAnnotations.CODEC_VALUE_READERS_MAP, Map.of(PersonPackage.eINSTANCE.getPerson_Name(), CodecTestHelper.TEST_VALUE_READER));

		classOptions.put(PersonPackage.eINSTANCE.getPerson(), personOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		Resource findResource = resourceSet.createResource(getPersonURI());
		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getPerson());

		findResource.load(options);

		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);
		assertEquals("John", p.getName());
	}

	@Test
	public void testDeserializationCustomReaderManyAttribute() throws InterruptedException, IOException {


		Resource resource = resourceSet.createResource(getPersonURI());

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> personOptions = new HashMap<>();
		personOptions.put(CodecAnnotations.CODEC_VALUE_WRITERS_MAP, Map.of(PersonPackage.eINSTANCE.getPerson_Titles(), CodecTestHelper.TEST_MULTI_VALUE_WRITER));
		personOptions.put(CodecAnnotations.CODEC_VALUE_READERS_MAP, Map.of(PersonPackage.eINSTANCE.getPerson_Titles(), CodecTestHelper.TEST_VALUE_READER));

		classOptions.put(PersonPackage.eINSTANCE.getPerson(), personOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		Resource findResource = resourceSet.createResource(getPersonURI());
		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getPerson());

		findResource.load(options);

		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);
		assertThat(p.getTitles()).contains("Mrs", "Dr");
	}
}
