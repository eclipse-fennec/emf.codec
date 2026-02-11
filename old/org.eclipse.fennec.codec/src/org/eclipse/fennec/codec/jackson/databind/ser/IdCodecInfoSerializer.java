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
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.IdentityInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Codec Serializer for IdInfo
 * @author ilenia
 * @since Aug 22, 2024
 */
public class IdCodecInfoSerializer implements CodecInfoSerializer{

	private final static Logger LOGGER = Logger.getLogger(IdCodecInfoSerializer.class.getName());

	private CodecModule codecModule;
	private CodecModelInfo codecModelInfoService;
	private IdentityInfo idCodecInfo;

	public IdCodecInfoSerializer(final CodecModule codecMoule, final CodecModelInfo codecModelInfoService, 
			final EClassCodecInfo eObjCodecInfo, final IdentityInfo idCodecInfo) {
		this.codecModule = codecMoule;
		this.codecModelInfoService = codecModelInfoService;
		this.idCodecInfo = idCodecInfo;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.ser.CodecInfoSerializer#serialize(org.eclipse.emf.ecore.EObject, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@SuppressWarnings("unchecked")
	public void serialize(EObject rootObj, JsonGenerator gen, SerializationContext provider) {
//		EMFContext.setParent(provider, rootObj);
		if(gen.streamWriteContext() instanceof EMFCodecWriteContext cwt) {
			cwt.setCurrentEObject(rootObj);
		}
		
		String idStrategy = idCodecInfo.getIdStrategy() != null ? idCodecInfo.getIdStrategy() : "";
		List<EStructuralFeature> idFeatures = idCodecInfo.getIdFeatures().stream().filter(f -> f instanceof EStructuralFeature).map(EStructuralFeature.class::cast).collect(Collectors.toList());

		switch(idStrategy) {
		case "COMBINED":
			CodecValueWriter<Object, String> w = codecModelInfoService.getCodecInfoHolderByType(InfoType.IDENTITY).getWriterByName(idCodecInfo.getIdValueWriterName());
			String idSeparator = idCodecInfo.getIdSeparator();
			String id = "";
			for(EStructuralFeature f : idFeatures) {
				if(rootObj.eGet(f) != null) {
					id = id.concat(rootObj.eGet(f).toString()).concat(idSeparator);
				}
			}
			int start = id.lastIndexOf(idSeparator);
			StringBuilder builder = new StringBuilder();						
			id = builder.append(id.substring(0, start)).toString();
			if(w != null) {
				id = w.writeValue(id, provider);
			}
			
			gen.writeName(idCodecInfo.getIdKey());
			
			if(gen.canWriteObjectId() && codecModule.isIdFeatureAsPrimaryKey()) {
				gen.writeObjectId(id);
			} else {
				gen.writeString(id);
			}
			break;
		case "ID_FIELD": default:
			if(idFeatures.size() == 0) {
				LOGGER.warning(String.format("ID strategy is ID_FIELD but no id feature has been found. Not doing anything."));
				break;
			}
			if(idFeatures.size() != 1) {
				LOGGER.severe(String.format("ID strategy is ID_FIELD but id features are %d. There should be exactly 1!", idFeatures.size()));
				break;
			}
			gen.writeName(idCodecInfo.getIdKey());
			EStructuralFeature idFeature = idFeatures.get(0);
			Object featureValue = rootObj.eGet(idFeature);
			CodecValueWriter<Object, String> writer = codecModelInfoService.getCodecInfoHolderByType(InfoType.IDENTITY).getWriterByName(idCodecInfo.getIdValueWriterName());	
			if(writer != null) {
				String value = writer.writeValue(featureValue, provider);
				if(gen.canWriteObjectId() && codecModule.isIdFeatureAsPrimaryKey()) {
					gen.writeObjectId(value);
				} else {
					gen.writeString(value);
				}
			}
			else {
				if(gen.canWriteObjectId() && codecModule.isIdFeatureAsPrimaryKey()) {
					gen.writeObjectId(featureValue);
					if(featureValue == null) {
						rootObj.eSet(idFeature, gen.streamWriteContext().currentValue().toString()); //if the id feature is null, the generator takes care of creating a new one and then we have to set it to the EObject
					}
				} else {
					if(featureValue == null) {
						gen.writeNull();
					} else {
						gen.writeString(featureValue.toString());
					}	
				}
			}
			break;					
		}	
	}
}
