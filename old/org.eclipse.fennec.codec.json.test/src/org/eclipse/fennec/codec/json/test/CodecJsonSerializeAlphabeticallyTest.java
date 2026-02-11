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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import tools.jackson.databind.MapperFeature;

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
		@Property(key = "type", value="json"),
		@Property(key = "enableFeatures", value={"SerializationFeature.INDENT_OUTPUT"}, type = Type.Array)
				
})
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
public class CodecJsonSerializeAlphabeticallyTest extends JsonTestSetting {
	
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
	public void beforeEach() throws Exception{
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
	public void testSerializationAlphabeticallyYES() throws IOException {
	
		Resource resource = resourceSet.createResource(URI.createURI(personFileName));
		
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.mapperFeaturesWith(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
			.build();
		resource.save(options);
		int l = 0, l1 = 0, l2 = 0;
		 try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			 String line = reader.readLine();
			 while(line != null) {
				 if(line.contains("\"age\" :")) {
					 l1 = l;
				 } else if(line.contains("\"birthDate\" :")) {
					 l2 = l;
				 }
				 line = reader.readLine();
				 l++;
			 }
		 }
		 assertThat(l1).isLessThan(l2);
	}
	
	@Test
	public void testSerializationAlphabeticallyYESIdOnTop() throws IOException {
	
		Resource resource = resourceSet.createResource(URI.createURI(personFileName));
		
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.mapperFeaturesWith(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
			.idOnTop(true)
			.build();
		resource.save(options);
		int l = 0, l1 = 0, l2 = 0;
		 try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			 String line = reader.readLine();
			 while(line != null) {
				 if(line.contains("\"_id\" :")) {
					 l1 = l;
				 } else if(line.contains("\"birthDate\" :")) {
					 l2 = l;
				 }
				 line = reader.readLine();
				 l++;
			 }
		 }
		 assertThat(l1).isLessThan(l2);
	}
	
	@Test
	public void testSerializationAlphabeticallyYESIdOnTopNO() throws IOException {
	
		Resource resource = resourceSet.createResource(URI.createURI(personFileName));
		
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.mapperFeaturesWith(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
			.idOnTop(false)
			.forClass(PersonPackage.Literals.PERSON)
				.idKey("id")
				.and()
			.build();
		resource.save(options);
		int l = 0, l1 = 0, l2 = 0;
		 try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			 String line = reader.readLine();
			 while(line != null) {
				 if(line.contains("\"id\" :")) {
					 l1 = l;
				 } else if(line.contains("\"birthDate\" :")) {
					 l2 = l;
				 }
				 line = reader.readLine();
				 l++;
			 }
		 }
		 assertThat(l1).isGreaterThan(l2);
	}
	

	@Test
	public void testSerializationAlphabeticallyNOT() throws IOException {
	
		Resource resource = resourceSet.createResource(URI.createURI(personFileName));
		
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.mapperFeaturesWithout(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
			.build();
		resource.save(options);
		int l = 0, l1 = 0, l2 = 0;
		 try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			 String line = reader.readLine();			 
			 while(line != null) {
				 if(line.contains("\"age\" :")) {
					 l1 = l;
				 } else if(line.contains("\"birthDate\" :")) {
					 l2 = l;
				 }
				 line = reader.readLine();
				 l++;
			 }
		 }
		 assertThat(l1).isGreaterThan(l2);
	}
	
	@Test
	public void testSerializationAlphabeticallyNOTIdOnTop() throws IOException {
	
		Resource resource = resourceSet.createResource(URI.createURI(personFileName));
		
		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.mapperFeaturesWithout(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
			.idOnTop(true)
			.forClass(PersonPackage.Literals.PERSON)
				.idKey("id")
				.and()
			.build();
		resource.save(options);
		int l = 0, l1 = 0, l2 = 0;
		 try (BufferedReader reader = new BufferedReader(new FileReader(personFileName))) {
			 String line = reader.readLine();			 
			 while(line != null) {
				 if(line.contains("\"id\" :")) {
					 l1 = l;
				 } else if(line.contains("\"birthDate\" :")) {
					 l2 = l;
				 }
				 line = reader.readLine();
				 l++;
			 }
		 }
		 assertThat(l1).isLessThan(l2);
	}
}
