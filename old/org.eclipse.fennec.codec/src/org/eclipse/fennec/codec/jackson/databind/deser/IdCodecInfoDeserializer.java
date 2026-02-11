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
package org.eclipse.fennec.codec.jackson.databind.deser;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.IdentityInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Codec Deserializer for IdInfo
 * @author ilenia
 * @since Sep 26, 2024
 */
public class IdCodecInfoDeserializer extends ValueDeserializer<String> {
	
	private CodecModule codecModule;
	private CodecModelInfo codecModelInfoService;
	private IdentityInfo idCodecInfo;
	
	public IdCodecInfoDeserializer(final CodecModule codecMoule, final CodecModelInfo codecModelInfoService, 
			final EClassCodecInfo eObjCodecInfo, final IdentityInfo idCodecInfo) {
		this.codecModule = codecMoule;
		this.codecModelInfoService = codecModelInfoService;
		this.idCodecInfo = idCodecInfo;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecInfoDeserializer#deserialize(tools.jackson.core.JsonParser, tools.jackson.databind.DeserializationContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctxt)  {
		if (jp.currentToken() == JsonToken.PROPERTY_NAME) {
			jp.nextToken();
		}
		Object value;
		switch (jp.currentToken()) {
		case VALUE_STRING:
			value = jp.getValueAsString();
			break;
		case VALUE_NUMBER_INT:
			value = jp.getValueAsInt();
			break;
		case VALUE_NUMBER_FLOAT:
			value = jp.getValueAsLong();
			break;
		default:
			value = null;
		}
		
		if(jp instanceof CodecParserBaseImpl codecParser) {
			if(codecParser.canReadObjectId()) {
				value = codecParser.getObjectId();
			}
		}
		String id = null;
		if (value != null) {
			String readerName = idCodecInfo.getIdValueReaderName();
			
			if(readerName != null) {
				CodecInfoHolder infoHolder = codecModelInfoService.getCodecInfoHolderByType(InfoType.IDENTITY);
				id = (String) infoHolder.getReaderByName(readerName).readValue(value, ctxt);
			} else {
				id = value.toString();
			}
//			If the serializedIdField is false then we need to retrieve the values from the _id
//			TODO: when we can extract current from the streamReadContext then we can also uncomment here
			EObject parent = jp.streamReadContext() instanceof EMFCodecReadContext ? ((EMFCodecReadContext) jp.streamReadContext()).getCurrentEObject() : null;
//			EObject parent = EMFContext.getParent(ctxt);
			if(!codecModule.isSerializeIdField()) {
				setIdFields(parent, id);
			}
		}
		return id;

	}


	@SuppressWarnings("unchecked")
	public void deserializeAndSet(JsonParser jp, EObject current, DeserializationContext ctxt, Resource resource) {
		if (jp.currentToken() == JsonToken.PROPERTY_NAME) {
			jp.nextToken();
		}		
		Object value;
		switch (jp.currentToken()) {
		case VALUE_STRING:
			value = jp.getValueAsString();
			break;
		case VALUE_NUMBER_INT:
			value = jp.getValueAsInt();
			break;
		case VALUE_NUMBER_FLOAT:
			value = jp.getValueAsLong();
			break;
		default:
			value = null;
		}
		
		if(jp instanceof CodecParserBaseImpl codecParser) {
			if(codecParser.canReadObjectId()) {
				value = codecParser.getObjectId();
			}
		}

		if (value != null) {
			String readerName = idCodecInfo.getIdValueReaderName();
			String id = null;
			if(readerName != null) {
				CodecInfoHolder infoHolder = codecModelInfoService.getCodecInfoHolderByType(InfoType.IDENTITY);
				id = (String) infoHolder.getReaderByName(readerName).readValue(value, ctxt);
			} else {
				id = value.toString();
			}
//			If the serializedIdField is false then we need to retrieve the values from the _id
			if(!codecModule.isSerializeIdField()) {
				setIdFields(current, id);
			}
		}

	}

	private void setIdFields(EObject current, String deserializedIdValue) {
		switch(idCodecInfo.getIdStrategy()) {
		case "COMBINED":
			String[] idSegments = deserializedIdValue.split(idCodecInfo.getIdSeparator());
			for(int i = 0; i < idSegments.length; i++) {
				EStructuralFeature feature = (EStructuralFeature) idCodecInfo.getIdFeatures().get(i);
				current.eSet(feature, idSegments[i]);
			}
			break;
		case "ID_FIELD":
			EcoreUtil.setID(current, deserializedIdValue);
			break;
		default:
			
		}
	}
}
