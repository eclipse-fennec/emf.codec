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
package org.eclipse.fennec.codec.jackson.databind.ser;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.jackson.utils.TypeConstructorHelper;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Codec Serailizer for FeatureInfo
 * 
 * @author ilenia
 * @since Aug 22, 2024
 */
public class FeatureCodecInfoSerializer implements CodecInfoSerializer{
	
	private final static Logger LOGGER = Logger.getLogger(FeatureCodecInfoSerializer.class.getName());
	
	private CodecModule codecModule;
	private CodecModelInfo codecModelInfoService;
	private EClassCodecInfo eObjCodecInfo;
	private FeatureCodecInfo featureCodecInfo;
	private ValueSerializer<Object> serializer;
	
	public FeatureCodecInfoSerializer(final CodecModule codecMoule, final CodecModelInfo codecModelInfoService, 
			final EClassCodecInfo eObjCodecInfo, final FeatureCodecInfo featureCodecInfo) {
		this.codecModule = codecMoule;
		this.codecModelInfoService = codecModelInfoService;
		this.eObjCodecInfo = eObjCodecInfo;
		this.featureCodecInfo = featureCodecInfo;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.ser.CodecInfoSerializer#serialize(org.eclipse.emf.ecore.EObject, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@SuppressWarnings("unchecked")
	public void serialize(EObject rootObj, JsonGenerator gen, SerializationContext provider) {
		if(featureCodecInfo.isIgnore()) return;
		
		if(featureCodecInfo.getFeature() == null) {
			LOGGER.severe(String.format("No Feature found in CodecFeatureInfo. Feature will not be serialized!"));
			return;
		}

		// Check global ignore list
		if (featureCodecInfo.getFeature() != null &&
			codecModule.getGlobalIgnoreFeatureNames().contains(featureCodecInfo.getFeature().getName())) {
			return;
		}

		
		EStructuralFeature feature = (EStructuralFeature) featureCodecInfo.getFeature();		
		if(!codecModule.isSerializeIdField()) {
			if(eObjCodecInfo.getIdentityInfo().getIdFeatures().contains(feature)) {
				return;
			}
		}
		
		if(gen.streamWriteContext() instanceof EMFCodecWriteContext cwt) {
			cwt.setCurrentFeature(feature);
			cwt.setCurrentEObject(rootObj);
		} else {
			throw new IllegalArgumentException(String.format("StreamWriteContext is not of type EMFCodecWriteContext! Something went wrong!"));
		}
		
		JavaType javaType = TypeConstructorHelper.constructJavaTypeFromFeature(feature, provider);
		serializer = provider.findValueSerializer(javaType);
		
		if (rootObj.eIsSet(feature)) {					
			if(feature.isMany()) {
				List<Object> values = (List<Object>) rootObj.eGet(feature);
				serializeManyAttribute(values, feature, gen, provider);
			} else {
				Object value = rootObj.eGet(feature);
				serializeSingleAttribute(rootObj, value, feature, gen, provider);
			}			
		} else if(codecModule.isSerializeDefaultValue()) {
			if(feature.isMany()) {
				List<Object> values = (List<Object>) rootObj.eGet(feature);
				serializeManyAttribute(values, feature, gen, provider);
			} else {
				Object value = feature.getDefaultValue();
				serializeSingleAttribute(rootObj, value, feature, gen, provider);
			}
		}
	}

	
	private void serializeSingleAttribute(EObject rootObj, Object value, EStructuralFeature feature, JsonGenerator gen,
			SerializationContext provider) {
		
		if(value == null && !codecModule.isSerializeNullValue()) {
			return;
		}
		if(value instanceof String str && str != null && str.isEmpty() && !codecModule.isSerializeEmptyValue()) {
			return;
		}
		if(codecModule.isUseNamesFromExtendedMetaData()) {
			gen.writeName(featureCodecInfo.getKey());
		} else {
			gen.writeName(feature.getName());
		}
		serializeSingleAttributeValue(value, feature, gen, provider);
	}
	
	@SuppressWarnings("unchecked")
	private void serializeSingleAttributeValue(Object value, EStructuralFeature feature, JsonGenerator gen,
			SerializationContext provider) {
		
		if(value == null) {
			gen.writeNull();
			return;
		} 
		else {
			CodecInfoHolder infoHolder = codecModelInfoService.getCodecInfoHolderByType(InfoType.ATTRIBUTE);
			CodecValueWriter<Object,?> writer = infoHolder.getWriterByName(featureCodecInfo.getValueWriterName());

			if(writer != null) serializer.serialize(writer.writeValue(value, provider), gen, provider);
			else serializer.serialize(value, gen, provider);
		}
	}

	
	@SuppressWarnings("unchecked")
	private void serializeManyAttribute(List<Object> values, EStructuralFeature feature,
			JsonGenerator gen, SerializationContext provider) {
//		We need to check weather there is some null value inside the list and decide weather to serialize it or not
		List<Object> valuesToSerialize = values;
		if(!codecModule.isSerializeNullValue()) {
			valuesToSerialize = values.stream().filter(v -> v != null).toList();
		}

		if(valuesToSerialize.isEmpty() && (!codecModule.isSerializeDefaultValue() || !codecModule.isSerializeEmptyValue())) return;
		
		if(codecModule.isUseNamesFromExtendedMetaData()) {
			gen.writeName(featureCodecInfo.getKey());
		} else {
			gen.writeName(feature.getName());
		}	
				
		CodecInfoHolder infoHolder = codecModelInfoService.getCodecInfoHolderByType(InfoType.ATTRIBUTE);
		CodecValueWriter<Object,?> writer = infoHolder.getWriterByName(featureCodecInfo.getValueWriterName());
				
		if(writer != null) serializer.serialize(writer.writeValue(valuesToSerialize, provider), gen, provider);
		else serializer.serialize(valuesToSerialize, gen, provider);
	}
}
