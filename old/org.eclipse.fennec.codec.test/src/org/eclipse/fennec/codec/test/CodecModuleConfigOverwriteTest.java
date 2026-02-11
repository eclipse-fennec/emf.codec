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
package org.eclipse.fennec.codec.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.jackson.resource.CodecResource;
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

import tools.jackson.databind.ObjectMapper;

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
		@Property(key = "type", value="json")
})
@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
public class CodecModuleConfigOverwriteTest {
	
	@InjectService(cardinality = 0, filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
	ServiceAware<ResourceSet> rsAware;
	
	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;
	
	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<ObjectMapperConfigurator> mapperAware;
	
	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;
	
	private ResourceSet resourceSet;	
	private CodecModuleConfigurator codecModuleConfigurator;
	private URI uri = URI.createURI("mytest.json");
	
	@BeforeEach() 
	public void beforeEach() throws Exception{
		codecFactoryAware.waitForService(2000l);
		mapperAware.waitForService(2000l);
		codecModuleAware.waitForService(2000l);	
		resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
		codecModuleConfigurator = codecModuleAware.waitForService(2000l);
		assertNotNull(codecModuleConfigurator);
	}
	
	@AfterEach()
	public void afterEach() {
		File f = new File("mytest.json");
		if(f.exists()) {
			f.delete();
		}
	}


	
	@Test
	public void testCodecModuleOverwriteProxyKey() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("_proxy", module.getProxyKey());

		Resource resource = resourceSet.createResource(uri);

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.proxyKey("test")
			.build();
		resource.save(options);
		
		module = getCodecModuleFromResource(resource);
		assertEquals("test", module.getProxyKey());		
	}
	
	@Test
	public void testCodecModuleOverwriteRefKey() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("$ref", module.getRefKey());

		Resource resource = resourceSet.createResource(uri);

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.referenceKey("test")
			.build();
		resource.save(options);
		
		module = getCodecModuleFromResource(resource);
		assertEquals("test", module.getRefKey());		
	}
	
	@Test
	public void testCodecModuleOverwriteTimestampKey() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("_timestamp", module.getTimestampKey());

		Resource resource = resourceSet.createResource(uri);

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.timestampKey("test")
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertEquals("test", module.getTimestampKey());
	}

	@Test
	public void testCodecModuleOverwriteSupertypeKey() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("_supertype", module.getSuperTypeKey());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.supertypeKey("test")
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertEquals("test", module.getSuperTypeKey());
	}

	@Test
	public void testCodecModuleOverwriteUseId() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isUseId());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.useId(false)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertFalse(module.isUseId());
	}


	@Test
	public void testCodecModuleOverwriteSerializeType() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isSerializeType());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeType(false)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertFalse(module.isSerializeType());
	}

	@Test
	public void testCodecModuleOverwriteSerializeSuperTypesAsArray() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isSerializeSuperTypesAsArray());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeSuperTypesAsArray(false)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertFalse(module.isSerializeSuperTypesAsArray());
	}

	@Test
	public void testCodecModuleOverwriteUseNamesFromExtendedMetaData() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isUseNamesFromExtendedMetaData());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.useNamesFromExtendedMetadata(true)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertTrue(module.isUseNamesFromExtendedMetaData());
	}
	
	@Test
	public void testCodecModuleOverwriteIdOnTop() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isIdOnTop());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.idOnTop(false)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertFalse(module.isIdOnTop());
	}

	@Test
	public void testCodecModuleOverwriteIdFeatureAsPrimaryKey() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isIdFeatureAsPrimaryKey());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.idFeatureAsPrimaryKey(false)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertFalse(module.isIdFeatureAsPrimaryKey());
	}

	@Test
	public void testCodecModuleOverwriteSerializeSuperTypes() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isSerializeSuperTypes());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeSuperTypes(true)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertTrue(module.isSerializeSuperTypes());
	}

	@Test
	public void testCodecModuleOverwriteSerializeDefaultValue() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isSerializeDefaultValue());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(true)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertTrue(module.isSerializeDefaultValue());
	}

	@Test
	public void testCodecModuleOverwriteSerializeEmptyValue() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isSerializeEmptyValue());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeEmptyValue(true)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertTrue(module.isSerializeEmptyValue());
	}

	@Test
	public void testCodecModuleOverwriteSerializeNullValue() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isSerializeNullValue());

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeNullValue(true)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertTrue(module.isSerializeNullValue());
	}

	@Test
	public void testCodecModuleOverwriteSerializeIdField() throws InterruptedException, IOException {

		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isSerializeIdField());

		Resource resource = resourceSet.createResource(uri);

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeIdField(true)
			.build();
		resource.save(options);

		module = getCodecModuleFromResource(resource);
		assertTrue(module.isSerializeIdField());
	}
	
	private CodecModule getCodecModuleFromResource(Resource resource) {
		assertTrue(resource instanceof CodecResource);
		CodecResource codecRes = (CodecResource) resource;
		ObjectMapper mapper = codecRes.getMapper();
		assertEquals(1, mapper.registeredModules().size());		
		assertTrue(mapper.registeredModules().stream().toList().get(0) instanceof CodecModule);		
		return  (CodecModule) mapper.registeredModules().stream().toList().get(0);
	}
}
