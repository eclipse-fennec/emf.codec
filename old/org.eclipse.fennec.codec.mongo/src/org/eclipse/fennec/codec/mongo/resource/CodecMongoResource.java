/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.mongo.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.fennec.codec.configurator.ObjectMapperBuilderFactory;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.jackson.resource.CodecResource;
import org.eclipse.fennec.codec.mongo.MongoCodecProvider;
import org.eclipse.fennec.codec.mongo.helper.MongoUtils;
import org.eclipse.fennec.codec.mongo.options.CodecMongoOptions;
import org.gecko.mongo.osgi.MongoDatabaseProvider;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

public final class CodecMongoResource extends CodecResource {

	private MongoDatabaseProvider provider;
	
	public CodecMongoResource(URI uri, CodecModelInfo modelInfo, CodecModule.Builder moduleBuilder, 
			ObjectMapperBuilderFactory objMapperBuilderFactory) {
		super(uri, modelInfo, moduleBuilder, objMapperBuilderFactory);
	}

	public CodecMongoResource(URI uri, CodecModelInfo modelInfo, CodecModule.Builder moduleBuilder, 
			ObjectMapperBuilderFactory objMapperBuilderFactory, MongoDatabaseProvider provider) {
		super(uri, modelInfo, moduleBuilder, objMapperBuilderFactory);
		this.provider = provider;
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		super.doSave(outputStream, options);

		if (options == null) {
			options = Collections.<String, Object> emptyMap();
		}		
		
		MongoCollection<EObject> collection = getCollection(options);

		CodecRegistry eobjectRegistry = CodecRegistries
				.fromProviders(new MongoCodecProvider(mapper, this, options));
		CodecRegistry defaultRegistry = MongoClient.getDefaultCodecRegistry();
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(eobjectRegistry, defaultRegistry);
		
//		FindOneAndReplaceOptions farOptions = new FindOneAndReplaceOptions()
//				.upsert(true) //this is to insert a new document if none is found to be updated
//				.returnDocument(ReturnDocument.AFTER); //this is to return the document after the update and not the one before
//
//		
////		loop over the contents of the resource
//		for(EObject eObject : getContents()) {
//			
////			create the update filter looking for the id of the EObject
//			Bson updateFilter = createUpdateFilter(eObject, options);
//		}
		
//		

		collection.withCodecRegistry(codecRegistry).withWriteConcern(WriteConcern.ACKNOWLEDGED).insertMany(getContents());
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void delete(Map<?, ?> options) throws IOException {
		super.delete(options);
		MongoCollection<EObject> collection = getCollection(options);
		CodecRegistry eobjectRegistry = CodecRegistries.fromProviders(new MongoCodecProvider(mapper, this, options));
		CodecRegistry defaultRegistry = MongoClient.getDefaultCodecRegistry();
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(eobjectRegistry, defaultRegistry);

		boolean countResults = false;
		long elementCount;
		DeleteResult deleteResult;

		Map<Object, Object> response = options == null ? null
				: (Map<Object, Object>) options.get(URIConverter.OPTION_RESPONSE);
		if(options != null) {
			Object optionCountResult = options.get(CodecMongoOptions.OPTION_COUNT_RESULT);
			countResults = optionCountResult != null && Boolean.TRUE.equals(optionCountResult);
		}
		if (response == null) {
			response = new HashMap<Object, Object>();
		}
		if (uri.query() != null) {
//			if (queryEngine == null) {
//				throw new IOException("The query engine was not found");
//			}
//
//			EMongoQuery mongoQuery = queryEngine.buildMongoQuery(uri, options);
//
//			Document filter = mongoQuery.getFilter();
//
//			if (filter != null) {
//				deleteResult = collection.deleteMany(filter);
//				if (countResults) {
//					elementCount = deleteResult.getDeletedCount();
//				}
//			} else {
//				deleteResult = collection.deleteOne(new BasicDBObject(Keywords.ID_KEY, MongoUtils.getID(uri)));
//				if (countResults) {
//					elementCount = deleteResult.getDeletedCount();
//				}
//			}
//			if (countResults) {
//				response.put(Options.OPTION_COUNT_RESPONSE, Long.valueOf(elementCount));
//			}

		} else {

			deleteResult = collection.withCodecRegistry(codecRegistry)
					.deleteOne(new BasicDBObject(CodecMongoOptions.ID_KEY, MongoUtils.getID(uri)));
			if (countResults) {
				elementCount = deleteResult.getDeletedCount();
				response.put(CodecMongoOptions.OPTION_COUNT_RESPONSE, Long.valueOf(elementCount));
			}
		}
	}

	@Override
	protected boolean needOutputstream() {
		return false;
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		super.doLoad(inputStream, options);
	      
	      if (options == null) {
				options = Collections.<String, Object> emptyMap();
			}
		
		MongoCollection<EObject> collection = getCollection(options);

		CodecRegistry eobjectRegistry = CodecRegistries
				.fromProviders(new MongoCodecProvider(mapper, this, options));
		CodecRegistry defaultRegistry = MongoClient.getDefaultCodecRegistry();
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(eobjectRegistry, defaultRegistry);

		FindIterable<EObject> resultIterable = collection.withCodecRegistry(codecRegistry)
				.withDocumentClass(EObject.class).find();
		try (MongoCursor<EObject> mongoCursor = resultIterable.iterator()) {
			while (mongoCursor.hasNext()) {
				mongoCursor.next();
			}
		}
	}

	private MongoCollection<EObject> getCollection(Map<?, ?> options) {
		MongoDatabase database = provider.getDatabase();
		if(options != null && options.containsKey(CodecMongoOptions.CODEC_MONGO_COLLECTION_NAME)) {
			if(options.get(CodecMongoOptions.CODEC_MONGO_COLLECTION_NAME) instanceof EClass collEClass) {
				return database.getCollection(collEClass.getName(), EObject.class);
			}
			else if(options.get(CodecMongoOptions.CODEC_MONGO_COLLECTION_NAME) instanceof String collName) {
				return database.getCollection(collName, EObject.class);
			}
		}
		String collectionName = uri.segment(1);
		return database.getCollection(collectionName, EObject.class);
	}
}