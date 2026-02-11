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
package org.eclipse.fennec.codec.jackson.databind.deser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.type.CollectionType;

/**
 * 
 * @author ilenia
 * @since Apr 24, 2025
 */
public class CodecCollectionDeserializer extends ValueDeserializer<Collection<Object>> {

	private final CollectionType baseType;
	private final ValueDeserializer<? extends EObject> deserializer;
	private final ValueDeserializer<? extends EObject> referenceDeserializer;

	public CodecCollectionDeserializer(final CollectionType type, final ValueDeserializer<? extends EObject> deserializer,
			final ValueDeserializer<EObject> referenceDeserializer) {
		this.baseType = type;
		this.deserializer = deserializer;
		this.referenceDeserializer = referenceDeserializer;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueDeserializer#deserialize(tools.jackson.core.JsonParser, tools.jackson.databind.DeserializationContext)
	 */
	@Override
	public Collection<Object> deserialize(final JsonParser p, final DeserializationContext ctxt)  {
		Collection<Object> values = createCollection(ctxt);
		while (p.nextToken() != JsonToken.END_ARRAY) {
			EObject result = deserializer.deserialize(p, ctxt);
			if (result != null) {
				values.add(result);
			}
		}
		return values;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueDeserializer#deserialize(tools.jackson.core.JsonParser, tools.jackson.databind.DeserializationContext, java.lang.Object)
	 */
	@Override
	public Collection<Object> deserialize(final JsonParser p, final DeserializationContext ctxt, final Collection<Object> intoValue) {
		
		final EObject parent = ((EMFCodecReadContext) p.streamReadContext().getParent()).getCurrentEObject();
		final EReference feature = (EReference) (((EMFCodecReadContext) p.streamReadContext().getParent()).getCurrentFeature());
		final Resource resource = ((EMFCodecReadContext) p.streamReadContext().getParent()).getResource();

		while (p.nextToken() != JsonToken.END_ARRAY) {
			
			((EMFCodecReadContext) p.streamReadContext()).setCurrentEObject(parent);
			((EMFCodecReadContext) p.streamReadContext()).setCurrentFeature(feature);
			((EMFCodecReadContext) p.streamReadContext()).setResource(resource);

			if (feature != null && feature.isContainment()) {
				EObject result = deserializer.deserialize(p, ctxt);
				if (result != null) {
					intoValue.add(result);
				}
			} else {
				EObject entry = referenceDeserializer.deserialize(p, ctxt);
				if (entry != null) {
					intoValue.add(entry);
				}
			}
		}
		return intoValue;
	}
	
	@SuppressWarnings("unchecked")
	private Collection<Object> createCollection(final DeserializationContext ctxt) {
		CollectionType type = baseType;
		try {
			if (baseType.isAbstract() && baseType.isCollectionLikeType()) {
				type = (CollectionType) ctxt.getTypeFactory().constructCollectionLikeType(type.getRawClass(), type.getContentType().getRawClass());// mapAbstractType(ctxt.getConfig(), type);
			}
			if (!type.isAbstract()) {
				return (Collection<Object>) type.getRawClass().getConstructor().newInstance();
			}
		} catch (Exception e) {}
		// use HashSet as fallback implementation for Sets which could not evaluate it's type
		if (type.isTypeOrSubTypeOf(Set.class)) {
			return new HashSet<>();
		}
		// fallback to default behavior
		return new ArrayList<>();
	}

}
