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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jackson.databind.CodecJsonWriteContext;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 
 * @author ilenia
 * @since Apr 24, 2025
 */
public class CodecResourceSerializer extends ValueSerializer<Resource> {

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueSerializer#serialize(java.lang.Object, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@Override
	public void serialize(final Resource value, final JsonGenerator jg, final SerializationContext provider) {
		
		if(jg instanceof CodecUTF8JsonGenerator codecJsonGen) {
			((CodecJsonWriteContext)codecJsonGen.streamWriteContext()).setResource(value);
			if (value.getContents().size() == 1) {
				serializeOne(value.getContents().get(0), codecJsonGen, provider);
			} else {
				codecJsonGen.writeStartArray();
				for (EObject o : value.getContents()) {
					serializeOne(o, codecJsonGen, provider);
				}
				codecJsonGen.writeEndArray();
			}
		} else {
			CodecGeneratorBaseImpl codecGenerator = null;
			if(jg instanceof CodecGeneratorBaseImpl) {
				codecGenerator = (CodecGeneratorBaseImpl) jg;
			} else if(jg instanceof GeneratorBase gb){
				codecGenerator = new CodecGeneratorWrapper(gb);
			} 
			if(codecGenerator != null) {
				codecGenerator.streamWriteContext().setResource(value);
				
				if (value.getContents().size() == 1) {
					serializeOne(value.getContents().get(0), codecGenerator, provider);
				} else {
					codecGenerator.writeStartArray();
					for (EObject o : value.getContents()) {
						serializeOne(o, codecGenerator, provider);
					}
					codecGenerator.writeEndArray();
				}
			} else {
				throw new IllegalArgumentException("Don't know what to do with the JsonGenerator I have here!");
			}
		}
		
		
		
	}	   

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueSerializer#handledType()
	 */
	@Override
	public Class<Resource> handledType() {
		return Resource.class;
	}

	private void serializeOne(final EObject object, final JsonGenerator jg, final SerializationContext provider) {
		final JavaType type = provider.constructType(object.getClass());
		final ValueSerializer<Object> serializer = provider.findValueSerializer(type);

		if (serializer != null) {
			serializer.serialize(object, jg, provider);
		}
	}

}
