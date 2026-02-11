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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Codec Serializer for EObject
 * @author ilenia
 * @since Aug 6, 2024
 */
public class CodecEObjectSerializer extends ValueSerializer<EObject> implements CodecInfoSerializer {

	private final static Logger LOGGER = Logger.getLogger(CodecEObjectSerializer.class.getName());

	private final CodecModule codecModule;
	private final CodecModelInfo codecModelInfoService;


	public CodecEObjectSerializer(final CodecModule codecModule, 
			final CodecModelInfo codecModelInfoService) {
		this.codecModule = codecModule;
		this.codecModelInfoService = codecModelInfoService;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueSerializer#handledType()
	 */
	@Override
	public Class<EObject> handledType() {
		return EObject.class;
	}

	private EClassCodecInfo extractModelInfo(EClass type) {
		PackageCodecInfo codecModelInfo = codecModule.getCodecModelInfo();
		EClassCodecInfo eObjCodecInfo = null;
		if(type != null) {
			for(EClassCodecInfo eci : codecModelInfo.getEClassCodecInfo()) {
				if(eci.getClassifier().equals(type)) {
					eObjCodecInfo = eci;
					break;
				}
			}
		}
//		we look in other packages
		if(eObjCodecInfo == null) {
			eObjCodecInfo = codecModelInfoService.getCodecInfoForEClass(type).orElse(null);
		}
		return eObjCodecInfo;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueSerializer#serialize(java.lang.Object, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@Override
	public void serialize(EObject value, JsonGenerator gen, SerializationContext provider) {
		
		if(gen.streamWriteContext() instanceof EMFCodecContext cwt) {
			cwt.setCurrentEObject(value);
		} 

		EClassCodecInfo eObjCodecInfo = extractModelInfo(value.eClass());

		if(eObjCodecInfo == null) {
			LOGGER.severe(String.format("No EClassCodecInfo found in CodecModule for EObject of class %s", value.eClass()));
			throw new IllegalArgumentException(String.format("No EClassCodecInfo found in CodecModule for EObject of class %s", value.eClass()));
		}
		
		CodecInfoSerializer typeInfoSerializer = new TypeCodecInfoSerializer(codecModule, codecModelInfoService, eObjCodecInfo, eObjCodecInfo.getTypeInfo());
		CodecInfoSerializer superTypeInfoSerializer = new SuperTypeCodecInfoSerializer(codecModule, codecModelInfoService, eObjCodecInfo, eObjCodecInfo.getSuperTypeInfo());
		CodecInfoSerializer idInfoSerializer = new IdCodecInfoSerializer(codecModule, codecModelInfoService, eObjCodecInfo, eObjCodecInfo.getIdentityInfo());
		
		Map<String, CodecInfoSerializer> codecInfoSerializierMap = new HashMap<>();
		if(codecModule.isUseId()) codecInfoSerializierMap.put(eObjCodecInfo.getIdentityInfo().getIdKey(), idInfoSerializer);
		if(codecModule.isSerializeType()) codecInfoSerializierMap.put(eObjCodecInfo.getTypeInfo().getTypeKey(), typeInfoSerializer);
		if(codecModule.isSerializeSuperTypes()) codecInfoSerializierMap.put(codecModule.getSuperTypeKey(), superTypeInfoSerializer);
		eObjCodecInfo.getAttributeCodecInfo().forEach(aci -> 
			codecInfoSerializierMap.put(getSerializablePropertyName(aci), new FeatureCodecInfoSerializer(codecModule, codecModelInfoService, eObjCodecInfo, aci))
		);
		eObjCodecInfo.getReferenceCodecInfo().forEach(aci -> 
			codecInfoSerializierMap.put(getSerializablePropertyName(aci), new ReferenceCodecInfoSerializer(codecModule, codecModelInfoService, eObjCodecInfo, aci))
		);
		eObjCodecInfo.getOperationCodecInfo().forEach(aci -> 
			codecInfoSerializierMap.put(getSerializablePropertyName(aci), new OperationCodecInfoSerializer(codecModule, codecModelInfoService, eObjCodecInfo, aci))
		);
		eObjCodecInfo.getEnumeratorCodecInfo().forEach(aci -> 
			codecInfoSerializierMap.put(getSerializablePropertyName(aci), new EnumeratorCodecInfoSerializer(codecModule, aci))
		);
		
		LinkedHashMap<String, CodecInfoSerializer> finalOrderedSerMap = new LinkedHashMap<>();
		if(codecModule.isSortPropertiesAlphabetically()) {
			TreeMap<String, CodecInfoSerializer> orderedCodecInfoSerMap = new TreeMap<>(codecInfoSerializierMap);
			if(codecModule.isIdOnTop()) {
				if(orderedCodecInfoSerMap.remove(eObjCodecInfo.getIdentityInfo().getIdKey()) != null) {
					finalOrderedSerMap.put(eObjCodecInfo.getIdentityInfo().getIdKey(), idInfoSerializer);
					 for(Map.Entry<String, CodecInfoSerializer> entry : orderedCodecInfoSerMap.entrySet()) {
						 finalOrderedSerMap.put(entry.getKey(), entry.getValue());
					  }
				} else {
					finalOrderedSerMap.putAll(orderedCodecInfoSerMap);
				}				
			} else {
				finalOrderedSerMap.putAll(orderedCodecInfoSerMap);
			}			
		} else {
			if(codecModule.isIdOnTop()) {
				if(codecInfoSerializierMap.remove(eObjCodecInfo.getIdentityInfo().getIdKey()) != null) {
					finalOrderedSerMap.put(eObjCodecInfo.getIdentityInfo().getIdKey(), idInfoSerializer);
					for(Map.Entry<String, CodecInfoSerializer> entry : codecInfoSerializierMap.entrySet()) {
						 finalOrderedSerMap.put(entry.getKey(), entry.getValue());
					  }
				} 
			} else {
				finalOrderedSerMap.putAll(codecInfoSerializierMap);
			}
		}
		
		gen.writeStartObject(value);
		finalOrderedSerMap.forEach((k,v) -> {
			v.serialize(value, gen, provider);
		});
		gen.writeEndObject();
	}
	
	private String getSerializablePropertyName(FeatureCodecInfo featureCodecInfo) {
		
		if(codecModule.isUseNamesFromExtendedMetaData()) return featureCodecInfo.getKey();
		return featureCodecInfo.getFeature().getName();
	}
}
