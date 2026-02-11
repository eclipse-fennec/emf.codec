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
package org.eclipse.fennec.codec.mongo.options;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;

/**
 * 
 * @author ilenia
 * @since Oct 1, 2025
 */
public class CodecMongoOptionsBuilder extends CodecOptionsBuilder{
	
	private CodecMongoOptionsBuilder() {
		super();
	}
	
	/**
	 * Creates a new options builder.
	 *
	 * @return a new CodecOptionsBuilder instance
	 */
	public static CodecMongoOptionsBuilder create() {
		return new CodecMongoOptionsBuilder();
	}
	
	// ========== Supported Mongo-Specific Options ==========

	/**
	 * Sets the name for the collection where to store/load the data
	 * If not specified, the name of the root class object is used
	 *
	 * @param collectionName the name of the mongo collection
	 * @return this builder
	 */
	public CodecMongoOptionsBuilder collectionName(String collectionName) {
		options.put(CodecMongoOptions.CODEC_MONGO_COLLECTION_NAME, collectionName);
		return this;
	}
	
	/**
	 * Sets the name for the collection where to store/load the data
	 * If not specified, the name of the root class object is used
	 *
	 * @param collectionEClass the EClass from which to extract the collection name
	 * @return this builder
	 */
	public CodecMongoOptionsBuilder collectionName(EClass collectionEClass) {
		options.put(CodecMongoOptions.CODEC_MONGO_COLLECTION_NAME, collectionEClass);
		return this;
	}
}
