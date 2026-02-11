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

import java.util.HashMap;
import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.CodecDataInput;
import org.eclipse.fennec.codec.CodecDataOutput;
import org.eclipse.fennec.codec.mongo.resource.CodecMongoResource;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.ContextAttributes;


final class MongoCodec implements Codec<EObject> {

	private final Map<?, ?> options;
	private final ObjectMapper mapper;
	private final CodecMongoResource resource;

	/**
	 * Creates a new instance.
	 * @param mapper 
	 * @param resource 
	 * @param options
	 */
	MongoCodec(ObjectMapper mapper, CodecMongoResource resource, Map<?, ?> options) {
		this.mapper = mapper;
		this.resource = resource;
		this.options = options;
	}

	@Override
	public void encode(BsonWriter writer, EObject value, EncoderContext encoderContext) {
	
		mapper.writer()
		.with(ContextAttributes
		         .getEmpty()
		         .withSharedAttributes(options == null ? new HashMap<>() : new HashMap<>(options)))
		.writeValue(new CodecDataOutput<>(writer, mapper), value);
	}

	@Override
	public Class<EObject> getEncoderClass() {
		return EObject.class;
	}

	@Override
	public EObject decode(BsonReader reader, DecoderContext decoderContext) {
		ContextAttributes attributes;
		attributes = ContextAttributes
		         .getEmpty()
		         .withSharedAttributes(options == null ? new HashMap<>() : new HashMap<>(options));
//				.withPerCallAttribute(RESOURCE_SET, resource.getResourceSet())
//				.withPerCallAttribute(RESOURCE, resource);

		Resource r = mapper.reader()
				.with(attributes)
				.forType(Resource.class)
				.withValueToUpdate(resource)
				.readValue(new CodecDataInput<>(reader, mapper));
		return r.getContents().isEmpty() ? null : r.getContents().get(0);
	}
}