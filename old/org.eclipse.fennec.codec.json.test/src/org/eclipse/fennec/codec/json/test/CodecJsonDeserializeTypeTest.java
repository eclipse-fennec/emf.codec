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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.eclipse.fennec.codec.test.helper.CodecTestHelper;
import org.gecko.codec.demo.model.person.Child;
import org.gecko.codec.demo.model.person.Child2;
import org.gecko.codec.demo.model.person.Person;
import org.gecko.codec.demo.model.person.PersonPackage;
import org.gecko.codec.demo.model.person.TestObject;
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
import org.osgi.test.common.annotation.Property.TemplateArgument;
import org.osgi.test.common.annotation.Property.ValueSource;
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
@WithFactoryConfiguration(factoryPid = "DynamicModelConfigurator", location = "?", name = "test", properties = {
		@Property(key = "emf.dynamicEcoreUri", value="file:%s/type-as-feature.ecore", templateArguments = { //
                @TemplateArgument(source = ValueSource.SystemProperty, value = "basePath")})
})
public class CodecJsonDeserializeTypeTest extends JsonTestSetting{

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
		resourceSet = rsAware.waitForService(40000l);
		assertNotNull(resourceSet);
	}

	@AfterEach() 
	@Override
	public void afterEach() throws IOException {
		super.afterEach();
	}

	@Test
	public void testDifferentTypeKey() throws IOException {
		Resource resource = resourceSet.createResource(URI.createURI(personFileName));

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(PersonPackage.Literals.PERSON)
				.forClass(PersonPackage.Literals.PERSON)
					.typeKey("type")
					.typeStrategy("URI")
				.and()
				.build();
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		Resource findResource = resourceSet.createResource(URI.createURI(personFileName));
		findResource.load(options);

		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);
		assertNotNull(p);
	}

	@Test
	public void testDeserializationDifferentTypeKeysFeatureLevelDynamicModel(@InjectService(filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
	ServiceAware<ResourceSet> rsAware, @InjectService(filter = "(" + EMFNamespaces.EMF_MODEL_NSURI + "=http://example.de/type/1.0)") ServiceAware<EPackage> ePackageAware ) throws IOException {


		EPackage epackage = ePackageAware.getService();

		EClass testClass = (EClass) epackage.getEClassifier("TestObject");


		// load dynamic eobjects from json with classifier from ecore
		Resource resource = resourceSet.createResource(URI.createURI(System.getProperty("test-data") + "type-different-keys.json"));

		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(testClass)
				.forClass(testClass)
					.forReference((EReference) testClass.getEStructuralFeature("ref1"))
						.typeKey("type")
						.typeStrategy("URI")
					.and()
					.forReference((EReference) testClass.getEStructuralFeature("ref2"))
						.typeKey("kind")
						.typeStrategy("URI")
					.and()
				.and()
				.build();
		//		options.put(CodecModuleOptions.CODEC_MODULE_TYPE_KEY, "type");
		resource.load(options);

		assertThat(resource.getContents()).hasSize(1);

		EObject loadClass = resource.getContents().get(0);


		EStructuralFeature ref1Feature = loadClass.eClass().getEStructuralFeature("ref1");
		assertThat(ref1Feature).isNotNull();
		assertThat(((EObject)loadClass.eGet(ref1Feature)).eClass().getName()).isEqualTo("Child");

		EStructuralFeature ref2Feature = loadClass.eClass().getEStructuralFeature("ref2");
		assertThat(ref2Feature).isNotNull();
		assertThat(((EObject)loadClass.eGet(ref2Feature)).eClass().getName()).isEqualTo("Child2");	
		
	}

	

	@Test
	public void testDeserializationDifferentTypeKeysFeatureLevel() throws IOException {

		// load dynamic eobjects from json with classifier from ecore
		Resource resource = resourceSet.createResource(URI.createURI(ctx.getBundle().getEntry("test-data/person-model-different-keys.json").toString()));

		Map<String, Object> options = CodecOptionsBuilder.create()
				.rootObject(PersonPackage.Literals.TEST_OBJECT)
				.forClass(PersonPackage.Literals.TEST_OBJECT)
					.forReference(PersonPackage.Literals.TEST_OBJECT__REF1)
						.typeKey("type")
						.typeStrategy("URI")
					.and()
					.forReference(PersonPackage.Literals.TEST_OBJECT__REF2)
						.typeKey("kind")
						.typeStrategy("URI")
					.and()
				.and()
				.build();
		//		options.put(CodecModuleOptions.CODEC_MODULE_TYPE_KEY, "type");
		resource.load(options);

		assertThat(resource.getContents()).hasSize(1);

		EObject loadClass = resource.getContents().get(0);
		assertTrue(loadClass instanceof TestObject);
		TestObject testObj = (TestObject) resource.getContents().get(0);
		assertNotNull(testObj.getRef1());
		assertTrue(testObj.getRef1() instanceof Child);
		assertNotNull(testObj.getRef2());
		assertTrue(testObj.getRef2() instanceof Child2);
	}
}
