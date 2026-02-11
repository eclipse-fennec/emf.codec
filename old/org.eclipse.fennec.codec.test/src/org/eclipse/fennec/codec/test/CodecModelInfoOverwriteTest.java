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

import static org.assertj.core.api.Assertions.assertThat;
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
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.IdentityInfo;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.jackson.resource.CodecResource;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.eclipse.fennec.codec.options.CodecValueReaderConstants;
import org.eclipse.fennec.codec.options.CodecValueWriterConstants;
import org.eclipse.fennec.codec.test.helper.CodecTestHelper;
import org.gecko.codec.demo.model.person.Person;
import org.gecko.codec.demo.model.person.PersonPackage;
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
public class CodecModelInfoOverwriteTest {
	
	@InjectService(cardinality = 0, filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
	ServiceAware<ResourceSet> rsAware;
	
	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;
	
	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<ObjectMapperConfigurator> mapperAware;
	
	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;
	
	private ResourceSet resourceSet;
	private URI uri = URI.createURI("mytest.json");
	
	@BeforeEach() 
	public void beforeEach() throws Exception{
		codecFactoryAware.waitForService(2000l);
		mapperAware.waitForService(2000l);
		codecModuleAware.waitForService(2000l);	
		resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
	}
	
	@AfterEach()
	public void afterEach() {
		File f = new File("mytest.json");
		if(f.exists()) {
			f.delete();
		}
	}
	
	@Test
	public void testCodecModelInfoOverwriteIdKey() throws InterruptedException, IOException {
	
		Resource resource = resourceSet.createResource(uri);		
		Person person = CodecTestHelper.getTestPerson();		
		resource.getContents().add(person);
		
		Map<String, Object> options = CodecOptionsBuilder.create()
	      .forClass(PersonPackage.eINSTANCE.getPerson())
	          .idKey("myId")
	          .and()
	      .build();
	
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		assertNotNull(personCodecInfo.getIdentityInfo());
		assertEquals("myId", personCodecInfo.getIdentityInfo().getIdKey());
	}

	@Test
	public void testCodecModelInfoOverwriteIdStrategy() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.idStrategy("TEST")
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		assertNotNull(personCodecInfo.getIdentityInfo());
		assertEquals("TEST", personCodecInfo.getIdentityInfo().getIdStrategy());
	}
	
	@Test
	public void testCodecModelInfoOverwriteIdOrder() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.idFeatures(PersonPackage.eINSTANCE.getPerson_LastName(), PersonPackage.eINSTANCE.getPerson_Name())
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		assertNotNull(personCodecInfo.getIdentityInfo());
		assertEquals(2, personCodecInfo.getIdentityInfo().getIdFeatures().size());
		assertEquals(PersonPackage.eINSTANCE.getPerson_LastName(), personCodecInfo.getIdentityInfo().getIdFeatures().get(0));
		assertEquals(PersonPackage.eINSTANCE.getPerson_Name(), personCodecInfo.getIdentityInfo().getIdFeatures().get(1));
	}
	
	@Test
	public void testCodecModelInfoOverwriteIdSeparator() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.idSeparator("/")
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		assertNotNull(personCodecInfo.getIdentityInfo());
		assertEquals("/", personCodecInfo.getIdentityInfo().getIdSeparator());
	}
	
	@Test
	public void testCodecModelInfoOverwriteIgnoreFeature() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getAddress())
				.ignoreNotFeatures(PersonPackage.eINSTANCE.getAddress_Zip())
				.ignoreFeatures(PersonPackage.eINSTANCE.getAddress_Street())
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo addressCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getAddress().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(addressCodecInfo);
		assertNotNull(addressCodecInfo.getFeatureInfo());
		assertThat(addressCodecInfo.getFeatureInfo()).hasSize(3);
		for(FeatureCodecInfo fi : addressCodecInfo.getFeatureInfo()) {
			if(PersonPackage.eINSTANCE.getAddress_Zip().equals(fi.getFeature())) {
				assertFalse(fi.isIgnore());
			}
			if(PersonPackage.eINSTANCE.getAddress_Street().equals(fi.getFeature())) {
				assertTrue(fi.isIgnore());
			}
		}
	}
	
	@Test
	public void testCodecModelInfoOverwriteTypeInclude() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.typeInclude(false)
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		assertNotNull(personCodecInfo.getTypeInfo());
		assertTrue(personCodecInfo.getTypeInfo().isIgnoreType());
	}
	
	@Test
	public void testCodecModelInfoOverwriteTypeStrategy() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.typeStrategy("CLASS")
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		assertNotNull(personCodecInfo.getTypeInfo());
		assertEquals("CLASS", personCodecInfo.getTypeInfo().getTypeStrategy());
		assertEquals(CodecValueWriterConstants.WRITER_BY_INSTANCE_CLASS_NAME, personCodecInfo.getTypeInfo().getTypeValueWriterName());
		assertEquals(CodecValueReaderConstants.READER_BY_INSTANCE_CLASS_NAME, personCodecInfo.getTypeInfo().getTypeValueReaderName());
	}
	
	@Test
	public void testCodecModelInfoOverwriteTypeKey() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.typeKey("eClass")
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		assertNotNull(personCodecInfo.getTypeInfo());
		assertEquals("eClass", personCodecInfo.getTypeInfo().getTypeKey());
	}
	
	@Test
	public void testCodecModelInfoOverwriteTypeMap() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.typeMap(Map.of("person1", "Person", "person2", "Person"))
				.typeStrategy("NAME")
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		assertNotNull(personCodecInfo.getTypeInfo());
		assertNotNull(personCodecInfo.getTypeInfo().getTypeMap());
		assertTrue(personCodecInfo.getTypeInfo().getTypeMap().containsKey("person1"));
		assertTrue(personCodecInfo.getTypeInfo().getTypeMap().containsKey("person2"));
	}
	
	
	@Test
	public void testCodecModelInfoOverwriteWriter() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.valueWriters(Map.of(PersonPackage.eINSTANCE.getPerson_LastName(), CodecTestHelper.TEST_VALUE_WRITER))
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		FeatureCodecInfo featureInfo = personCodecInfo.getFeatureInfo().stream().filter(fi -> fi.getFeature().equals(PersonPackage.eINSTANCE.getPerson_LastName())).findFirst().get();
		assertNotNull(featureInfo);
		assertEquals("TEST_VALUE_WRITER", featureInfo.getValueWriterName());
	}
	
	@Test
	public void testCodecModelInfoOverwriteReader() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.valueReaders(Map.of(PersonPackage.eINSTANCE.getPerson_BirthDate(), CodecTestHelper.TEST_VALUE_READER))
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		FeatureCodecInfo featureInfo = personCodecInfo.getFeatureInfo().stream().filter(fi -> fi.getFeature().equals(PersonPackage.eINSTANCE.getPerson_BirthDate())).findFirst().get();
		assertNotNull(featureInfo);
		assertEquals("TEST_VALUE_READER", featureInfo.getValueReaderName());
	}
	
	@Test
	public void testCodecModelInfoOverwriteIdWriter() throws InterruptedException, IOException {

		Resource resource = resourceSet.createResource(uri);
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);

		Map<String, Object> options = CodecOptionsBuilder.create()
			.forClass(PersonPackage.eINSTANCE.getPerson())
				.idValueWriter(CodecTestHelper.TEST_VALUE_WRITER)
				.idStrategy("ID_FIELD")
			.build();
		resource.save(options);
		
		CodecModule module = getCodecModuleFromResource(resource);
		PackageCodecInfo modelCodecInfo = module.getCodecModelInfo();
		assertNotNull(modelCodecInfo);
		EClassCodecInfo personCodecInfo = modelCodecInfo.getEClassCodecInfo().stream().filter(ci -> PersonPackage.eINSTANCE.getPerson().getName().equals(ci.getClassifier().getName())).findFirst().get();
		assertNotNull(personCodecInfo);
		IdentityInfo featureInfo = personCodecInfo.getIdentityInfo();
		assertNotNull(featureInfo);
		assertEquals("TEST_VALUE_WRITER", featureInfo.getIdValueWriterName());
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
