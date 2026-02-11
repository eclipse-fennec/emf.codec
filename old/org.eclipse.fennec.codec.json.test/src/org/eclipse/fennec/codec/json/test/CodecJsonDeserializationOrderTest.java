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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.gecko.codec.demo.model.person.Address;
import org.gecko.codec.demo.model.person.BusinessAddress;
import org.gecko.codec.demo.model.person.Person;
import org.gecko.codec.demo.model.person.PersonPackage;
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
public class CodecJsonDeserializationOrderTest extends JsonTestSetting{
	
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
	public void testDeserializationRootObjectWOOption() throws IOException {

		Resource personRes = resourceSet.createResource(URI.createURI(System.getProperty("test-data")+ "type-contained-ref.json"));
		
		Map<String, Object> options = new HashMap<>();
		assertThrows(IllegalArgumentException.class, () -> {
			personRes.load(options);
		});
	}
	
	@Test
	public void testDeserializationContainedReferenceWithType() throws IOException {

		Resource personRes = resourceSet.createResource(URI.createURI(System.getProperty("test-data")+ "type-contained-ref.json"));

		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(PersonPackage.eINSTANCE.getPerson())
				.forClass(PersonPackage.Literals.PERSON)
					.forReference(PersonPackage.Literals.PERSON__ADDRESS)
						.typeStrategy("NAME")
						.and()
					.and()
				.build();
		personRes.load(options);

		assertThat(personRes.getContents()).hasSize(1);
		assertThat(personRes.getContents().get(0)).isInstanceOf(Person.class);
		Person person = (Person) personRes.getContents().get(0);
		assertThat(person.getAddress()).isNotNull();
		assertThat(person.getAddress()).isInstanceOf(BusinessAddress.class);
	}
	
	@Test
	public void testDeserializationContainedReferenceWOType() throws IOException {

		Resource personRes = resourceSet.createResource(URI.createURI(System.getProperty("test-data")+ "no-type-contained-ref.json"));

		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(PersonPackage.eINSTANCE.getPerson())
				.build();
		personRes.load(options);

		assertThat(personRes.getContents()).hasSize(1);
		assertThat(personRes.getContents().get(0)).isInstanceOf(Person.class);
		Person person = (Person) personRes.getContents().get(0);
		assertThat(person.getAddress()).isNotNull();
		assertThat(person.getAddress()).isInstanceOf(Address.class);
	}
	
	@Test
	public void testDeserializationRefWithBuffer() throws IOException {

		Resource personRes = resourceSet.createResource(URI.createURI(System.getProperty("test-data")+ "buffer.json"));

		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(PersonPackage.eINSTANCE.getPerson())
				.forClass(PersonPackage.Literals.PERSON)
					.forReference(PersonPackage.Literals.PERSON__ADDRESS)
						.typeStrategy("NAME")
						.and()
					.and()
				.build();
		personRes.load(options);

		assertThat(personRes.getContents()).hasSize(1);
		assertThat(personRes.getContents().get(0)).isInstanceOf(Person.class);
		Person person = (Person) personRes.getContents().get(0);
		assertThat(person.getAddress()).isNotNull();
		assertThat(person.getAddress()).isInstanceOf(BusinessAddress.class);
		BusinessAddress address = (BusinessAddress) person.getAddress();
		assertThat(address.getId()).isNotNull();
		assertThat(address.getStreet()).isNotNull();
	}

	
}
