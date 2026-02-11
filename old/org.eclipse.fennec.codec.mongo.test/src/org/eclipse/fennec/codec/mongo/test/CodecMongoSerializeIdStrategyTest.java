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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.jackson.resource.CodecResource;
import org.eclipse.fennec.codec.options.CodecModelInfoOptions;
import org.eclipse.fennec.codec.options.CodecResourceOptions;
import org.eclipse.fennec.codec.options.ObjectMapperOptions;
import org.eclipse.fennec.codec.test.helper.CodecTestHelper;
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
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import tools.jackson.databind.SerializationFeature;

//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;

/**
 * See documentation here: https://github.com/osgi/osgi-test
 * https://github.com/osgi/osgi-test/wiki Examples:
 * https://github.com/osgi/osgi-test/tree/main/examples
 */
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
		@Property(key = "codecFactoryConfigurator.target", value = "(type=mongo)"),
		@Property(key = "type", value = "mongo") })
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value = "mongo") })
public class CodecMongoSerializeIdStrategyTest extends MongoEMFSetting {

	@InjectService(cardinality = 0, filter = "(type=mongo)")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;

	@InjectService(cardinality = 0, filter = "(type=mongo)")
	ServiceAware<ObjectMapperConfigurator> mapperAware;

	@InjectService(cardinality = 0, filter = "(type=mongo)")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;

	@InjectService(cardinality = 0)
	ServiceAware<MongoClientProvider> mongoClientAware;

	private MongoCollection<Document> bpCollection;
	private MongoCollection<Document> addCollection;

	@BeforeEach
	public void doBefore(@InjectBundleContext BundleContext ctx) throws Exception {
		MongoClientProvider mongoClientProvider = mongoClientAware.waitForService(2000l);
		MongoClient mongoClient = mongoClientProvider.getMongoClient();
		super.doBefore(ctx, mongoClient);
		bpCollection = getDatabase("test").getCollection("Person");
		cleanDBCollection(bpCollection);
		addCollection = getDatabase("test").getCollection("Address");
		cleanDBCollection(addCollection);
		codecFactoryAware.waitForService(2000l);
		mapperAware.waitForService(2000l);
		codecModuleAware.waitForService(2000l);
	}

	@AfterEach
	@Override
	public void doAfter() {
		cleanDBCollection(bpCollection);
		cleanDBCollection(addCollection);
		super.doAfter();
	}

	@Test
//	@RepeatedTest(value = 50)
	public void testSerializationIdFieldStrategy(@InjectService(cardinality = 0, filter = "(&("
			+ EMFNamespaces.EMF_CONFIGURATOR_NAME + "=mongo)("
			+ EMFNamespaces.EMF_MODEL_NAME + "=person))") ServiceAware<ResourceSet> rsAware)
			throws InterruptedException, IOException {
		ResourceSet resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
//		Thread.sleep(500);
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/"));
		assertThat(resource).isInstanceOf(CodecResource.class);

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> personOptions = new HashMap<>();
		personOptions.put(CodecModelInfoOptions.CODEC_ID_STRATEGY, "ID_FIELD");

		classOptions.put(PersonPackage.eINSTANCE.getPerson(), personOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		assertEquals(1, bpCollection.countDocuments());
		FindIterable<Document> docIterable = bpCollection.find();
		Document first = docIterable.first();
		assertTrue(first.containsKey("_id"));
		assertEquals(person.getId(), first.get("_id"));
	}

	@Test
	public void testSerializationIdCombinedDefaultStrategy(@InjectService(cardinality = 0, filter = "(&("
			+ EMFNamespaces.EMF_CONFIGURATOR_NAME + "=mongo)("
			+ EMFNamespaces.EMF_MODEL_NAME + "=person))") ServiceAware<ResourceSet> rsAware)
			throws IOException, InterruptedException {
		ResourceSet resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);

		Resource resource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/"));

		Person person = CodecTestHelper.getTestPerson();
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> personOptions = new HashMap<>();
		personOptions.put(CodecModelInfoOptions.CODEC_ID_STRATEGY, "COMBINED");

		classOptions.put(PersonPackage.eINSTANCE.getPerson(), personOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		assertEquals(1, bpCollection.countDocuments());
		FindIterable<Document> docIterable = bpCollection.find();
		Document first = docIterable.first();
		assertTrue(first.containsKey("_id"));
		assertEquals(person.getName().concat("-").concat(person.getLastName()), first.get("_id"));
	}

	@Test
	public void testSerializationIdCombinedStrategyDiffFeatures(@InjectService(cardinality = 0, filter = "(&("
			+ EMFNamespaces.EMF_CONFIGURATOR_NAME + "=mongo)("
			+ EMFNamespaces.EMF_MODEL_NAME + "=person))") ServiceAware<ResourceSet> rsAware)
			throws InterruptedException, IOException {
		ResourceSet resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);

		Resource resource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/"));

		Person person = CodecTestHelper.getTestPerson();
		person.setAge(42);
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> personOptions = new HashMap<>();
		options.put(ObjectMapperOptions.OBJ_MAPPER_SERIALIZATION_FEATURES_WITH,
				List.of(SerializationFeature.INDENT_OUTPUT));
		personOptions.put(CodecModelInfoOptions.CODEC_ID_STRATEGY, "COMBINED");
		personOptions.put(CodecModelInfoOptions.CODEC_ID_FEATURES_LIST,
				List.of(PersonPackage.eINSTANCE.getPerson_Name(), PersonPackage.eINSTANCE.getPerson_Age()));

		classOptions.put(PersonPackage.eINSTANCE.getPerson(), personOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		assertEquals(1, bpCollection.countDocuments());
		FindIterable<Document> docIterable = bpCollection.find();
		Document first = docIterable.first();
		assertTrue(first.containsKey("_id"));
		assertEquals(person.getName().concat("-").concat("" + person.getAge()), first.get("_id"));
	}

	@Test
	public void testSerializationIdCombinedStrategyDiffFeaturesOrder(@InjectService(cardinality = 0, filter = "(&("
			+ EMFNamespaces.EMF_CONFIGURATOR_NAME + "=mongo)("
			+ EMFNamespaces.EMF_MODEL_NAME + "=person))") ServiceAware<ResourceSet> rsAware)
			throws InterruptedException, IOException {
		ResourceSet resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);

		Resource resource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/"));

		Person person = CodecTestHelper.getTestPerson();
		person.setAge(42);
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> personOptions = new HashMap<>();
		options.put(ObjectMapperOptions.OBJ_MAPPER_SERIALIZATION_FEATURES_WITH,
				List.of(SerializationFeature.INDENT_OUTPUT));
		personOptions.put(CodecModelInfoOptions.CODEC_ID_STRATEGY, "COMBINED");
		personOptions.put(CodecModelInfoOptions.CODEC_ID_FEATURES_LIST,
				List.of(PersonPackage.eINSTANCE.getPerson_Age(), PersonPackage.eINSTANCE.getPerson_Name()));

		classOptions.put(PersonPackage.eINSTANCE.getPerson(), personOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		assertEquals(1, bpCollection.countDocuments());
		FindIterable<Document> docIterable = bpCollection.find();
		Document first = docIterable.first();
		assertTrue(first.containsKey("_id"));
		assertEquals(("" + person.getAge()).concat("-").concat(person.getName()), first.get("_id"));

	}

	@Test
	public void testSerializationIdCombinedStrategyDiffSeparator(@InjectService(cardinality = 0, filter = "(&("
			+ EMFNamespaces.EMF_CONFIGURATOR_NAME + "=mongo)("
			+ EMFNamespaces.EMF_MODEL_NAME + "=person))") ServiceAware<ResourceSet> rsAware)
			throws InterruptedException, IOException {
		ResourceSet resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);

		Resource resource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/Person/"));

		Person person = CodecTestHelper.getTestPerson();
		person.setAge(42);
		resource.getContents().add(person);
		Map<String, Object> options = new HashMap<>();
		Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
		Map<String, Object> personOptions = new HashMap<>();
		options.put(ObjectMapperOptions.OBJ_MAPPER_SERIALIZATION_FEATURES_WITH,
				List.of(SerializationFeature.INDENT_OUTPUT));
		personOptions.put(CodecModelInfoOptions.CODEC_ID_STRATEGY, "COMBINED");
		personOptions.put(CodecModelInfoOptions.CODEC_ID_SEPARATOR, "test");

		classOptions.put(PersonPackage.eINSTANCE.getPerson(), personOptions);
		options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		resource.save(options);

		resource.getContents().clear();
		resource.unload();

		assertEquals(1, bpCollection.countDocuments());
		FindIterable<Document> docIterable = bpCollection.find();
		Document first = docIterable.first();
		assertTrue(first.containsKey("_id"));
		assertEquals(person.getName().concat("test").concat(person.getLastName()), first.get("_id"));

	}
}
