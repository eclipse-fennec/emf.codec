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
package org.eclipse.fennec.codec.jackson.databind.ser;

import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.jdk.MapSerializer;

/**
 * 
 * @author ilenia
 * @since Apr 24, 2025
 */
public class EMapSerializer extends ValueSerializer<EList<Map.Entry<?, ?>>> {

	/** The Map serializer we delegate the job to. */
	private final MapSerializer delegate;

	public EMapSerializer(final MapSerializer delegateMapSerialize) {
		this.delegate = delegateMapSerialize;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueSerializer#serialize(java.lang.Object, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void serialize(final EList<Map.Entry<?, ?>> value, final JsonGenerator jg,
			final SerializationContext serializers) {
		if (value == null || value.isEmpty()) {
			jg.writeNull();
		} else if (value instanceof EMap) {
			delegate.serialize(((EMap) value).map(), jg, serializers);
		} else {
			// iterate on entries manually
			jg.writeStartObject();
			for (Map.Entry<?, ?> entry : value) {
				Object key = Optional.ofNullable((Object) entry.getKey()).orElse("");
				((ValueSerializer<Object>) delegate.getKeySerializer()).serialize(key, jg, serializers);
				Object objectValue = entry.getValue();
				((ValueSerializer<Object>) delegate.getContentSerializer()).serialize(objectValue, jg, serializers);
			}
			jg.writeEndObject();
		}
	}

}
