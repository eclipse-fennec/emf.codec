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
package org.eclipse.fennec.codec.mongo;

import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.mongo.resource.CodecMongoResource;

import tools.jackson.databind.ObjectMapper;

/**
 * 
 * @author grune
 * @since Feb 2, 2024
 */
public final class MongoCodecProvider implements CodecProvider {
	/** options */
	private final Map<?, ?> options;
	private ObjectMapper mapper;
	private CodecMongoResource resource;

	/**
	 * Creates a new instance.
	 * 
	 * @param mapper
	 * @param resource
	 * @param options
	 */
	public MongoCodecProvider(ObjectMapper mapper, CodecMongoResource resource, Map<?, ?> options) {
		this.mapper = mapper;
		this.resource = resource;
		this.options = options;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.bson.codecs.configuration.CodecProvider#get(java.lang.Class, org.bson.codecs.configuration.CodecRegistry)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if (EObject.class.isAssignableFrom(clazz)) {
			return (Codec<T>) new MongoCodec(mapper, resource, options);
		} else {
			return null;
		}
	}
}