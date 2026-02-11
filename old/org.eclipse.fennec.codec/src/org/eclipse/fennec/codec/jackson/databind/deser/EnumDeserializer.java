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

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * 
 * @author ilenia
 * @since Apr 25, 2025
 */
public class EnumDeserializer extends ValueDeserializer<Enumerator>{
	
	private CodecModule codecModule;

	public EnumDeserializer(CodecModule codecModule) {
		this.codecModule = codecModule;		
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueDeserializer#deserialize(tools.jackson.core.JsonParser, tools.jackson.databind.DeserializationContext)
	 */
	@Override
	public Enumerator deserialize(JsonParser jp, DeserializationContext ctxt) throws JacksonException {
//		EStructuralFeature feature = EMFContext.getFeature(ctxt);
		EStructuralFeature feature = null;
		EMFCodecReadContext codecReadCtxt = null;
		if(jp.streamReadContext() instanceof EMFCodecReadContext crc) {
			codecReadCtxt = crc;
		}
		if(codecReadCtxt != null) {
			feature = codecReadCtxt.getCurrentFeature();
		}
		EEnum eDataType = (EEnum) feature.getEType();
		EEnumLiteral literal = null;
		if(codecModule.isWriteEnumLiterals()) {
			literal = eDataType.getEEnumLiteralByLiteral(jp.getString());
		}
		else {
			literal = eDataType.getEEnumLiteral(jp.getString());
		}
		if(literal == null) {
//			fallback
			if(codecModule.isWriteEnumLiterals()) {
				literal = eDataType.getEEnumLiteral(jp.getString());
			}
			else {
				literal = eDataType.getEEnumLiteralByLiteral(jp.getString());
			}
		}
		return literal.getInstance();
	}

}
