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
package org.eclipse.fennec.codec.mongo.emf.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.options.CodecModuleOptions;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.gecko.emf.osgi.example.model.basic.BasicFactory;
import org.gecko.emf.osgi.example.model.basic.BasicPackage;
import org.gecko.emf.osgi.example.model.basic.BusinessPerson;
import org.gecko.emf.osgi.example.model.basic.EmployeeInfo;
import org.gecko.emf.osgi.example.model.basic.GenderType;
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
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;


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
		@Property(key = "type", value="mongo")
})
public class MongoExtendedMetadataIntegrationTest extends MongoEMFSetting{
	
	@InjectService(cardinality = 0, filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=mongo)")
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
	
	@BeforeEach
	public void doBefore(@InjectBundleContext BundleContext ctx) throws Exception {
		MongoClientProvider mongoClientProvider = mongoClientAware.waitForService(2000l);
		MongoClient mongoClient = mongoClientProvider.getMongoClient();
		super.doBefore(ctx, mongoClient);
		mapperAware.waitForService(2000l);
		codecModuleAware.waitForService(2000l);	
		resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
		assertThat(resourceSet.getResources()).isEmpty();
	}

	@AfterEach
	public void doAfter() {
		super.doAfter();
	}
	
	@Test
	public void testSaveNoExtendedMetadataAttribute() throws IOException {
		System.out.println("Dropping DB");
		MongoCollection<Document> bpCollection = getDatabase("test").getCollection("BusinessPerson");
		bpCollection.drop();
		
		assertEquals(0, bpCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://"+ mongoHost + ":27017/test/BusinessPerson/"));
		
		BusinessPerson person = BasicFactory.eINSTANCE.createBusinessPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann" );
		person.setGender(GenderType.MALE);
		assertNull(person.getId());
		person.setCompanyIdCardNumber("test1234");
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(BasicPackage.eINSTANCE.getBusinessPerson()).
				useNamesFromExtendedMetadata(false).
				build();
		options.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, Boolean.FALSE);
		resource.save(options);
		
		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		//		long start = System.currentTimeMillis();
		Resource findResource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/BusinessPerson/" + person.getId()));
		findResource.load(options);
		
		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());
		
		
		// doing some object checks
		BusinessPerson p = (BusinessPerson) findResource.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		assertNotNull(p.getId());
		assertEquals("test1234", p.getCompanyIdCardNumber());
		
		assertEquals(1, bpCollection.countDocuments());
		FindIterable<Document> docIterable = bpCollection.find();
		Document first = docIterable.first();
		Object cidField = first.get("companyIdCardNumber");
		assertNotNull(cidField);
		assertEquals("test1234", cidField);
		
		bpCollection.drop();
	}
	
	/**
	 * Test creation of object and returning results
	 * @throws IOException 
	 */
	@Test
	public void testSaveExtendedMetadataAttribute() throws IOException {
		System.out.println("Dropping DB");
		MongoCollection<Document> bpCollection = getDatabase("test").getCollection("BusinessPerson");
		bpCollection.drop();
		
		assertEquals(0, bpCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://"+ mongoHost + ":27017/test/BusinessPerson/"));
		
		BusinessPerson person = BasicFactory.eINSTANCE.createBusinessPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann" );
		person.setGender(GenderType.MALE);
		assertNull(person.getId());
		person.setCompanyIdCardNumber("test1234");
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(BasicPackage.eINSTANCE.getBusinessPerson()).
				useNamesFromExtendedMetadata(true).
				build();
		options.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, Boolean.TRUE);
		resource.save(options);
		
		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		//		long start = System.currentTimeMillis();
		Resource findResource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/BusinessPerson/" + person.getId()));
		findResource.load(options);
		
		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());
		
		
		// doing some object checks
		BusinessPerson p = (BusinessPerson) findResource.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		assertNotNull(p.getId());
		assertEquals("test1234", p.getCompanyIdCardNumber());
		
		assertEquals(1, bpCollection.countDocuments());
		FindIterable<Document> docIterable = bpCollection.find();
		Document first = docIterable.first();
		Object cidField = first.get("compId");
		assertNotNull(cidField);
		assertEquals("test1234", cidField);
		
		bpCollection.drop();
	}
	
	/**
	 * Test creation of object and returning results
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSaveNoExtendedMetadataReferences() throws IOException {
		System.out.println("Dropping DB");
		MongoCollection<Document> bpCollection = getDatabase("test").getCollection("BusinessPerson");
		bpCollection.drop();
		
		assertEquals(0, bpCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://"+ mongoHost + ":27017/test/BusinessPerson/"));
		
		BusinessPerson person = BasicFactory.eINSTANCE.createBusinessPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann" );
		person.setGender(GenderType.MALE);
		assertNull(person.getId());
		person.setCompanyIdCardNumber("test1234");
		EmployeeInfo einfo01 = BasicFactory.eINSTANCE.createEmployeeInfo();
		einfo01.setPosition("CTO");
		EmployeeInfo einfo02 = BasicFactory.eINSTANCE.createEmployeeInfo();
		einfo02.setPosition("CEO");
		person.getEmployeeInfo().add(einfo01);
		person.getEmployeeInfo().add(einfo02);
		
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(BasicPackage.eINSTANCE.getBusinessPerson()).
				useNamesFromExtendedMetadata(false).
				build();
		options.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, Boolean.FALSE);
		resource.save(options);
		
		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		//		long start = System.currentTimeMillis();
		Resource findResource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/BusinessPerson/" + person.getId()));
		findResource.load(options);
		
		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());
		
		
		// doing some object checks
		BusinessPerson p = (BusinessPerson) findResource.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		assertNotNull(p.getId());
		assertEquals("test1234", p.getCompanyIdCardNumber());
		assertEquals(2, p.getEmployeeInfo().size());
		assertEquals("CTO", p.getEmployeeInfo().get(0).getPosition());
		assertEquals("CEO", p.getEmployeeInfo().get(1).getPosition());
		
		assertEquals(1, bpCollection.countDocuments());
		FindIterable<Document> docIterable = bpCollection.find();
		Document first = docIterable.first();
		Object eInfoField = first.get("employeeInfo");
		assertNotNull(eInfoField);
		assertTrue(eInfoField instanceof List);
		List<Document> docs = (List<Document>) eInfoField;
		assertEquals("CTO", docs.get(0).get("position"));
		assertEquals("CEO", docs.get(1).get("position"));
		Object eInfo2Field = first.get("employeeInfo2");
		assertNull(eInfo2Field);
		
		bpCollection.drop();
	}

	/**
	 * Test creation of object and returning results
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSaveExtendedMetadataReference() throws IOException {
		System.out.println("Dropping DB");
		MongoCollection<Document> bpCollection = getDatabase("test").getCollection("BusinessPerson");
		bpCollection.drop();
		
		assertEquals(0, bpCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://"+ mongoHost + ":27017/test/BusinessPerson/"));
		
		BusinessPerson person = BasicFactory.eINSTANCE.createBusinessPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann" );
		person.setGender(GenderType.MALE);
		assertNull(person.getId());
		person.setCompanyIdCardNumber("test1234");
		EmployeeInfo einfo01 = BasicFactory.eINSTANCE.createEmployeeInfo();
		einfo01.setPosition("CTO");
		EmployeeInfo einfo02 = BasicFactory.eINSTANCE.createEmployeeInfo();
		einfo02.setPosition("CEO");
		person.getEmployeeInfo().add(einfo01);
		person.getEmployeeInfo().add(einfo02);
		
		resource.getContents().add(person);
		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(BasicPackage.eINSTANCE.getBusinessPerson()).
				useNamesFromExtendedMetadata(true).
				build();
		options.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, Boolean.TRUE);
		resource.save(options);
		
		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		//		long start = System.currentTimeMillis();
		Resource findResource = resourceSet.createResource(URI.createURI("mongodb://" + mongoHost + ":27017/test/BusinessPerson/" + person.getId()));
		findResource.load(options);
		
		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());
		
		
		// doing some object checks
		BusinessPerson p = (BusinessPerson) findResource.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		assertNotNull(p.getId());
		assertEquals("test1234", p.getCompanyIdCardNumber());
		assertEquals(2, p.getEmployeeInfo().size());
		assertEquals("CTO", p.getEmployeeInfo().get(0).getPosition());
		assertEquals("CEO", p.getEmployeeInfo().get(1).getPosition());
		
		assertEquals(1, bpCollection.countDocuments());
		FindIterable<Document> docIterable = bpCollection.find();
		Document first = docIterable.first();
		Object eInfoField = first.get("eInfo");
		assertNotNull(eInfoField);
		assertTrue(eInfoField instanceof List);
		List<Document> docs = (List<Document>) eInfoField;
		assertEquals("CTO", docs.get(0).get("position"));
		assertEquals("CEO", docs.get(1).get("position"));
		Object eInfo2Field = first.get("employeeInfo");
		assertNull(eInfo2Field);
		
		bpCollection.drop();
	}
}
