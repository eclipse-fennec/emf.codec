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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.bson.Document;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.options.CodecModuleOptions;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.gecko.emf.osgi.example.model.basic.BasicFactory;
import org.gecko.emf.osgi.example.model.basic.BusinessPerson;
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
public class StoreSuperTypeIntegrationTest extends MongoEMFSetting{
	
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
		super.doBefore(ctx, mongoClient);		mapperAware.waitForService(2000l);
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
	public void testWriteSuperTypes() throws IOException, InterruptedException {
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
		
		assertEquals(0, resourceSet.getResources().size());
		assertEquals(0, personCollection.countDocuments());
		Resource resource = resourceSet.createResource(URI.createURI("mongodb://"+ mongoHost + ":27017/test/Person/"));
		testResourceSet(resourceSet, resource, 1, 0);
		
		Map<String, Object> saveOptions = CodecOptionsBuilder.create().
				serializeSuperTypes(true).
				build();
		BusinessPerson person = BasicFactory.eINSTANCE.createBusinessPerson();
		person.setFirstName("Mark");
		person.setLastName("Hoffmann" );
		person.setId("maho");
		person.setCompanyIdCardNumber("666");
		person.setGender(GenderType.MALE);
		person.getContact().add(EcoreUtil.copy(c1));
		person.getContact().add(EcoreUtil.copy(c2));
		resource.getContents().add(person);
		
		testResourceSet(resourceSet, resource, 1, 1);
		resource.save(saveOptions);
		testResourceSet(resourceSet, resource, 1, 1);
		
		resource.getContents().clear();
		testResourceSet(resourceSet, resource, 1, 0);
		resource.unload();
		
		assertEquals(1, personCollection.countDocuments());
		Document document = personCollection.find().first();
		assertNotNull(document);
		assertEquals("maho", document.get("_id"));
		assertTrue(document.containsKey("_supertype"));
		personCollection.drop();
		
		saveOptions = CodecOptionsBuilder.create().
				supertypeKey("mySupaType").
				serializeSuperTypes(true).
				build();
		resource.getContents().add(person);
		
		testResourceSet(resourceSet, resource, 1, 1);
		resource.save(saveOptions);
		testResourceSet(resourceSet, resource, 1, 1);
		
		assertEquals(1, personCollection.countDocuments());
		document = personCollection.find().first();
		assertNotNull(document);
		assertEquals("maho", document.get("_id"));
		assertFalse(document.containsKey("_supertype"));
		assertTrue(document.containsKey("mySupaType"));
		
		personCollection.drop();

		resource.getContents().clear();
		testResourceSet(resourceSet, resource, 1, 0);
		resource.unload();
		
		saveOptions = CodecOptionsBuilder.create().
				supertypeKey("mySupaType").
				serializeSuperTypes(false).
				build();
		resource.getContents().add(person);
		
		testResourceSet(resourceSet, resource, 1, 1);
		resource.save(saveOptions);
		testResourceSet(resourceSet, resource, 1, 1);
		
		assertEquals(1, personCollection.countDocuments());
		document = personCollection.find().first();
		assertNotNull(document);
		assertEquals("maho", document.get("_id"));
		assertFalse(document.containsKey("_supertype"));
		assertFalse(document.containsKey("mySupaType"));
		
		personCollection.drop();
		
		resource.getContents().clear();
		testResourceSet(resourceSet, resource, 1, 0);
		resource.unload();
		
		saveOptions = CodecOptionsBuilder.create().
				serializeSuperTypes(true).
				build();
		saveOptions.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_SUPER_TYPES, Boolean.TRUE);

		Person p = BasicFactory.eINSTANCE.createPerson();
		p.setFirstName("Emil");
		p.setLastName("Tester" );
		p.setId("etester");
		person.setGender(GenderType.MALE);
		
		resource.getContents().clear();
		testResourceSet(resourceSet, resource, 1, 0);
		resource.unload();
		resource.getContents().add(p);
		testResourceSet(resourceSet, resource, 1, 1);
		resource.save(saveOptions);
		testResourceSet(resourceSet, resource, 1, 1);
		
		assertEquals(1, personCollection.countDocuments());
		document = personCollection.find().first();
		assertEquals("etester", document.get("_id"));
		assertFalse(document.containsKey("_supertype"));
		
		personCollection.drop();
		
	}
	
	private void testResourceSet(ResourceSet rs, Resource r, int expectedResources, int expectedResourceContent) {
		assertNotNull(rs);
		assertNotNull(r);
		assertEquals(expectedResources, rs.getResources().size());
		assertTrue(rs.getResources().contains(r));
		assertEquals(expectedResourceContent, r.getContents().size());
	}
}
