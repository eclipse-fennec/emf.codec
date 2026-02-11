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

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.jackson.utils.FeatureKind;
import org.eclipse.fennec.codec.jackson.utils.TypeConstructorHelper;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;

/**
 * Codec Deserializer for FeatureInfo
 * @author ilenia
 * @since Sep 27, 2024
 */
public class FeatureCodecInfoDeserializer implements CodecInfoDeserializer {

	private CodecModule codecModule;
	private CodecModelInfo codecModelInfoService;
	private FeatureCodecInfo featureCodecInfo;
	private TypeInfo typeCodecInfo;
	private ValueDeserializer<Object> deserializer;


	public FeatureCodecInfoDeserializer(final CodecModule codecMoule, final CodecModelInfo codecModelInfoService, 
			final EClassCodecInfo eObjCodecInfo, final FeatureCodecInfo featureCodecInfo, TypeInfo typeInfo) {
		this.codecModule = codecMoule;
		this.codecModelInfoService = codecModelInfoService;
		this.featureCodecInfo = featureCodecInfo;
		this.typeCodecInfo = typeInfo;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecInfoDeserializer#deserializeAndSet(tools.jackson.core.JsonParser, org.eclipse.emf.ecore.EObject, tools.jackson.databind.DeserializationContext, org.eclipse.emf.ecore.resource.Resource)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deserializeAndSet(JsonParser jp, EObject current, DeserializationContext ctxt, Resource resource) {

		if(featureCodecInfo.getFeature() instanceof EOperation || featureCodecInfo.isIgnore())  {
			jp.nextToken();
			jp.skipChildren();
			return;
		}
		EStructuralFeature feature = (EStructuralFeature) featureCodecInfo.getFeature();
		EMFCodecReadContext codecReadCtxt = jp.streamReadContext() instanceof EMFCodecReadContext ? (EMFCodecReadContext) jp.streamReadContext() : null;
		if(codecReadCtxt == null) {
			throw new IllegalArgumentException(String.format("No EMFCodecReadContext available. Something went wrong!"));
		}
			
		codecReadCtxt.setCurrentFeature(feature);
		codecReadCtxt.setCurrentEObject(current);
		codecReadCtxt.setResource(resource);
		
		JsonToken token = jp.currentToken();

		if (jp.currentToken() == JsonToken.PROPERTY_NAME) {
			token = jp.nextToken();
		}

		if (jp.currentToken() == JsonToken.VALUE_NULL) {
			return;
		}

		JavaType javaType = TypeConstructorHelper.constructJavaTypeFromFeature(feature, ctxt);			
		deserializer = ctxt.findContextualValueDeserializer(javaType, null);
		
		boolean isMap = false;
		switch (FeatureKind.get(feature)) {
		case MAP:
			isMap = true;
			//$FALL-THROUGH$
		case MANY_CONTAINMENT:
		case SINGLE_CONTAINMENT: 
		case SINGLE_ATTRIBUTE:
		case MANY_ATTRIBUTE: {
			String readerName = featureCodecInfo.getValueReaderName();
			CodecInfoHolder infoHolder = codecModelInfoService.getCodecInfoHolderByType(InfoType.ATTRIBUTE);
			CodecValueReader<Object, ?> reader =  infoHolder.getReaderByName(readerName);
			if (feature.isMany()) {
				if (token != JsonToken.START_ARRAY && !isMap) {
					throw new StreamReadException(jp, "Expected START_ARRAY token, got " + token);
				}
				
				Collection<Object> objs = (Collection<Object>) deserializer.deserialize(jp, ctxt, current.eGet(feature));
				//	               If a custom ValueReader is set we use it to convert the deserialized value
				if(objs != null && reader != null) {
					Collection<Object> newObjs = new LinkedList<>();
					objs.forEach(obj -> {
						Object v = reader.readValue(obj, ctxt);
						newObjs.add(v);
					});
					current.eSet(feature, newObjs);
				}
			}  else {
				Object value = deserializer.deserialize(jp, ctxt);
				//		                If a custom ValueReader is set we use it to convert the deserialized value
				if (value != null && reader != null) {    	
					Object v = reader.readValue(value, ctxt);
					current.eSet(feature, v);
				}
				else current.eSet(feature, value);
			}				
		}
		break;
		case MANY_REFERENCE:
		case SINGLE_REFERENCE: {
			if (feature.isMany()) {
				deserializer.deserialize(jp, ctxt, current.eGet(feature));
			} else {
				new ReferenceCodecInfoDeserializer(codecModule, codecModelInfoService, typeCodecInfo)
				.deserializeAndSet(jp, current, ctxt, resource);	  
			}
		}
		break;
		default:
			break;
		}
	}


}
