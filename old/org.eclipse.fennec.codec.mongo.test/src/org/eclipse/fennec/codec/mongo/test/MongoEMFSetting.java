/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.bson.Document;
import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.codec.test.helper.CodecTestSetting;
import org.gecko.mongo.osgi.MongoClientProvider;
import org.gecko.mongo.osgi.MongoDatabaseProvider;
import org.gecko.mongo.osgi.configuration.ConfigurationProperties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author ilenia
 * @since Oct 14, 2024
 */
public abstract class MongoEMFSetting extends CodecTestSetting {

	static protected String mongoHost = System.getProperty("mongo.host", "localhost");

	protected MongoClient client;
	private List<MongoDatabase> dbs = new ArrayList<>();
	
	public void doBefore(BundleContext ctx, MongoClient mongoClient) throws Exception {
		client = mongoClient;
//		MongoClientOptions options = MongoClientOptions.builder().build();
//		client = new MongoClient(mongoHost, options);
	}

	public void doAfter() {
		dbs.forEach(db-> db.drop());
		dbs.clear();
		client.getDatabase("test").drop();

//		if (client != null) {
//			client.close();
//		}
	}
	
	protected void cleanDBCollection(MongoCollection<Document> collection ) {
		collection.drop();		
		assertEquals(0, collection.countDocuments());
	}

	
	protected void defaultSetup(ConfigurationAdmin ca) throws IOException, InvalidSyntaxException {
		// has to be a new configuration
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		// add service properties
		String clientId = "testClient";
		String clientUri = "mongodb://" + mongoHost + ":27017";
		props = new Hashtable<>();
		props.put(MongoClientProvider.PROP_CLIENT_ID, clientId);
		props.put(MongoClientProvider.PROP_URI, clientUri);
		Configuration clientConfig = ca.createFactoryConfiguration(ConfigurationProperties.CLIENT_PID, "?");
		clientConfig.update(props);
		
		// add service properties
		String dbAlias = "testDB";
		String db = "test";
		Dictionary<String, Object> dbp = new Hashtable<>();
		dbp.put(MongoDatabaseProvider.PROP_ALIAS, dbAlias);
		dbp.put(MongoDatabaseProvider.PROP_DATABASE, db);
		Configuration databaseConfig = ca.createFactoryConfiguration(ConfigurationProperties.DATABASE_PID, "?");
		databaseConfig.update(dbp);
	}
	
	@Override
	protected URI getPersonURI() {
		return URI.createURI("mongodb://"+ mongoHost + ":27017/test/Person/");
	}

	@Override
	protected URI getAddressURI() {
		return URI.createURI("mongodb://"+ mongoHost + ":27017/test/Address/");
	}
	
	protected MongoDatabase getDatabase(String dbname) {
		MongoDatabase database = client.getDatabase(dbname);
		dbs.add(database);
		return database;
	}
	
}
