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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.gecko.emf.osgi.example.model.basic.BasicFactory;
import org.gecko.emf.osgi.example.model.basic.BasicPackage;
import org.gecko.emf.osgi.example.model.basic.Contact;
import org.gecko.emf.osgi.example.model.basic.ContactContextType;
import org.gecko.emf.osgi.example.model.basic.ContactType;
import org.gecko.emf.osgi.example.model.basic.GenderType;
import org.gecko.emf.osgi.example.model.basic.Person;
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
public class MongoIntegrationTest extends MongoEMFSetting{
	
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
	public void testCreateId() throws IOException {
		System.out.println("Dropping DB");
		MongoCollection<Document> personCollection = getDatabase("test").getCollection("Person");
		personCollection.drop();

		assertEquals(0, personCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://localhost:27017/test/Person/"));

		Person person = BasicFactory.eINSTANCE.createPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann");
		person.setGender(GenderType.MALE);
		assertNull(person.getId());
		resource.getContents().add(person);
		resource.save(null);

		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		// long start = System.currentTimeMillis();
		Resource findResource = resourceSet
				.createResource(URI.createURI("mongodb://localhost:27017/test/Person/" + person.getId()));
		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(BasicPackage.eINSTANCE.getPerson()).
				build();
		findResource.load(options);

		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		assertNotNull(p.getId());

		assertEquals(1, personCollection.countDocuments());
		FindIterable<Document> docIterable = personCollection.find();
		Document first = docIterable.first();
		Object idField = first.get("_id");
		assertTrue(idField instanceof ObjectId);

		personCollection.drop();
	}

	@Test
	public void testBigIntegerConverter()
			throws IOException {

		System.out.println("Dropping DB");
		MongoCollection<Document> personCollection = getDatabase("test").getCollection("Person");
		personCollection.drop();

		assertEquals(0, personCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://localhost:27017/test/Person/"));

		Person person = BasicFactory.eINSTANCE.createPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann");
		person.setGender(GenderType.MALE);
		person.setBigInt(new BigInteger("12"));
		assertNull(person.getId());
		resource.getContents().add(person);
		resource.save(null);

		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		// long start = System.currentTimeMillis();
		Resource findResource = resourceSet
				.createResource(URI.createURI("mongodb://localhost:27017/test/Person/" + person.getId()));
		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(BasicPackage.eINSTANCE.getPerson()).
				build();
		findResource.load(options);


		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		assertNotNull(p.getId());
		assertNotNull(p.getBigInt());
		assertEquals(12, p.getBigInt().intValue());

		personCollection.drop();
	}
	
	@Test
	public void testByteArrayConverter()
			throws IOException {
		System.out.println("Dropping DB");
		MongoCollection<Document> personCollection = getDatabase("test").getCollection("Person");
		personCollection.drop();

		assertEquals(0, personCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://localhost:27017/test/Person/"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(0);
		baos.write(8);
		baos.write(1);
		baos.write(5);
		Person person = BasicFactory.eINSTANCE.createPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann");
		person.setGender(GenderType.MALE);
		person.setImage(baos.toByteArray());
		assertNull(person.getId());
		resource.getContents().add(person);
		resource.save(null);

		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		// long start = System.currentTimeMillis();
		Resource findResource = resourceSet
				.createResource(URI.createURI("mongodb://localhost:27017/test/Person/" + person.getId()));
		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(BasicPackage.eINSTANCE.getPerson()).
				build();
		findResource.load(options);

		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		assertNotNull(p.getId());
		assertNotNull(p.getImage());
		byte[] image = p.getImage();
		assertEquals(4, image.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(image);
		assertEquals(0, bais.read());
		assertEquals(8, bais.read());
		assertEquals(1, bais.read());
		assertEquals(5, bais.read());

		personCollection.drop();
	}
	
	@Test
	public void testBigDecimalConverter()
			throws IOException {
		System.out.println("Dropping DB");
		MongoCollection<Document> personCollection = getDatabase("test").getCollection("Person");
		personCollection.drop();

		assertEquals(0, personCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://localhost:27017/test/Person/"));

		Person person = BasicFactory.eINSTANCE.createPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann");
		person.setGender(GenderType.MALE);
		person.getBigDec().add(new BigDecimal("12.3"));
		person.getBigDec().add(new BigDecimal("45.6"));
		assertNull(person.getId());
		resource.getContents().add(person);
		resource.save(null);

		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		// long start = System.currentTimeMillis();
		Resource findResource = resourceSet
				.createResource(URI.createURI("mongodb://localhost:27017/test/Person/" + person.getId()));
		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(BasicPackage.eINSTANCE.getPerson()).
				build();
		findResource.load(options);

		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		assertNotNull(p.getId());
		assertEquals(2, p.getBigDec().size());
		assertTrue(p.getBigDec().get(0) instanceof BigDecimal);
		assertTrue(p.getBigDec().get(1) instanceof BigDecimal);
		assertEquals("12.3", p.getBigDec().get(0).toString());
		assertEquals("45.6", p.getBigDec().get(1).toString());

		personCollection.drop();
	}
	
	@Test
	public void testCreateContainmentSingle()
			throws IOException {
		System.out.println("Dropping DB");
		MongoCollection<Document> personCollection = getDatabase("test").getCollection("Person");
		personCollection.drop();

		// create contacts
		Contact c1 = BasicFactory.eINSTANCE.createContact();
		c1.setContext(ContactContextType.PRIVATE);
		c1.setType(ContactType.SKYPE);
		c1.setValue("charles-brown");
		Contact c2 = BasicFactory.eINSTANCE.createContact();
		c2.setContext(ContactContextType.WORK);
		c2.setType(ContactType.EMAIL);
		c2.setValue("mark.hoffmann@tests.de");

		assertEquals(0, personCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://localhost:27017/test/Person/"));

		Person person = BasicFactory.eINSTANCE.createPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann");
		person.setGender(GenderType.MALE);
		person.getContact().add(EcoreUtil.copy(c1));
		person.getContact().add(EcoreUtil.copy(c2));
		resource.getContents().add(person);
		resource.save(null);

		resource.getContents().clear();
		resource.unload();
		/*
		 * Find person in the collection
		 */
		// long start = System.currentTimeMillis();
		Resource findResource = resourceSet
				.createResource(URI.createURI("mongodb://localhost:27017/test/Person/" + person.getId()));
		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(BasicPackage.eINSTANCE.getPerson()).
				build();
		findResource.load(options);


		// get the person
		assertNotNull(findResource);
		assertFalse(findResource.getContents().isEmpty());
		assertEquals(1, findResource.getContents().size());

		// doing some object checks
		Person p = (Person) findResource.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		assertEquals(2, p.getContact().size());
		assertEquals("charles-brown", p.getContact().get(0).getValue());
		assertEquals("mark.hoffmann@tests.de", p.getContact().get(1).getValue());

		personCollection.drop();
	}
}
