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
package org.eclipse.fennec.codec.mongo.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.options.CodecModuleOptions;
import org.eclipse.fennec.codec.options.CodecResourceOptions;
import org.eclipse.fennec.codec.test.helper.CodecTestHelper;
import org.gecko.codec.demo.model.person.GENDER_TYPE;
import org.gecko.codec.demo.model.person.Person;
import org.gecko.codec.demo.model.person.PersonPackage;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.gecko.mongo.osgi.MongoClientProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
@WithFactoryConfiguration(name = "mongoClient", location = "?", factoryPid = "MongoClientProvider", properties = {
		@Property(key = "client_id", value = "test"), @Property(key = "uri", value = "mongodb://localhost:27017") })
@WithFactoryConfiguration(name = "mongoDatabase", location = "?", factoryPid = "MongoDatabaseProvider", properties = {
		@Property(key = "alias", value = "TestDB"), @Property(key = "database", value = "test") })
@WithFactoryConfiguration(factoryPid = "MongoCodecFactoryConfigurator", location = "?", name = "test")
@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
		@Property(key = "codecFactoryConfigurator.target", value="(type=mongo)"),
		@Property(key = "type", value="mongo"),
		@Property(key = "disableFeatures", value={"DeserializationFeature.FAIL_ON_TRAILING_TOKENS"}, type = Type.Array)
})
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value = "mongo") })
public class CodecMongoEnumTest extends MongoEMFSetting {

	@InjectService(cardinality = 0, filter = "(&(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=mongo)(" + EMFNamespaces.EMF_MODEL_NAME + "=person))")
	ServiceAware<ResourceSet> rsAware;

	@InjectService(cardinality = 0, filter = "(type=mongo)")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;

	@InjectService(cardinality = 0, filter = "(type=mongo)")
	ServiceAware<ObjectMapperConfigurator> mapperAware;

	@InjectService(cardinality = 0, filter = "(type=mongo)")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;

	@InjectService(cardinality = 0)
	ServiceAware<MongoClientProvider> mongoClientAware;

	private ResourceSet resourceSet;
	private MongoCollection<Document> bpCollection;

	@BeforeEach
	public void doBefore(@InjectBundleContext BundleContext ctx) throws Exception {
		MongoClientProvider mongoClientProvider = mongoClientAware.waitForService(2000l);
		MongoClient mongoClient = mongoClientProvider.getMongoClient();
		super.doBefore(ctx, mongoClient);

		bpCollection = getDatabase("test").getCollection("Person");
		cleanDBCollection(bpCollection);
		codecFactoryAware.waitForService(2000l);
		mapperAware.waitForService(2000l);
		codecModuleAware.waitForService(2000l);
		resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
	}

	@AfterEach
	@Override
	public void doAfter() {
		cleanDBCollection(bpCollection);
		super.doAfter();
	}

	@Test
	public void testSaveEnumDefault() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/"));

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		resource.save(null);

		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		Resource findResource = resourceSet
				.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/" + person.getId()));
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getPerson());
		findResource.load(options);

		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);

		assertEquals(person.getId(), p.getId());
		assertEquals(GENDER_TYPE.OTHER, p.getGender());
	}

	@Test
	public void testSaveEnumLiteral()
			throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/"));

		Person person = CodecTestHelper.getTestPerson();
		person.setGender(GENDER_TYPE.FEMALE);
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		options.put(CodecModuleOptions.CODEC_MODULE_WRITE_ENUM_LITERAL, true);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		Resource findResource = resourceSet
				.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/" + person.getId()));

		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getPerson());
		findResource.load(options);

		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);

		assertEquals(person.getId(), p.getId());
		assertEquals(GENDER_TYPE.FEMALE, p.getGender());
	}

	@Test
	public void testSaveEnumName() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/"));

		Person person = CodecTestHelper.getTestPerson();
		person.setGender(GENDER_TYPE.FEMALE);
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		resource.save(options);

		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		Resource findResource = resourceSet
				.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/" + person.getId()));

		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getPerson());
		findResource.load(options);

		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);

		assertEquals(person.getId(), p.getId());
		assertEquals(GENDER_TYPE.FEMALE, p.getGender());
	}

	@Test
	public void testSaveEnumNull() throws IOException {

		Resource resource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/"));

		Person person = CodecTestHelper.getTestPerson();
		person.setGender(null);
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_NULL_VALUE, true);
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_DEFAULT_VALUE, true);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		Resource findResource = resourceSet
				.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/" + person.getId()));

		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, PersonPackage.eINSTANCE.getPerson());
		findResource.load(options);

		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);

		assertEquals(person.getId(), p.getId());
		assertEquals(GENDER_TYPE.OTHER, p.getGender());
	}

}
