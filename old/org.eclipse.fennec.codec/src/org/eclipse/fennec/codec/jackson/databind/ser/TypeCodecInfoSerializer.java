/**
lio di  * Copyright (c) 2012 - 2024 Data In Motion and others.
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

import java.util.Map.Entry;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.gecko.emf.utilities.FeaturePath;
import org.gecko.emf.utilities.UtilitiesFactory;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Codec Serializer for TypeInfo
 * @author ilenia
 * @since Aug 22, 2024
 */
public class TypeCodecInfoSerializer implements CodecInfoSerializer {

	private static final Logger LOGGER = Logger.getLogger(TypeCodecInfoSerializer.class.getName());

	private CodecModule codecModule;
	private CodecModelInfo codecModelInfoService;
	private TypeInfo typeCodecInfo;
	private CodecInfoHolder holder;

	public TypeCodecInfoSerializer(final CodecModule codecMoule, final CodecModelInfo codecModelInfoService, 
			final EClassCodecInfo eObjCodecInfo, final TypeInfo typeCodecInfo) {
		this.codecModule = codecMoule;
		this.codecModelInfoService = codecModelInfoService;
		this.typeCodecInfo = typeCodecInfo;
		this.holder = codecModelInfoService.getCodecInfoHolderByType(InfoType.TYPE);
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.ser.CodecInfoSerializer#serialize(org.eclipse.emf.ecore.EObject, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void serialize(EObject rootObj, JsonGenerator gen, SerializationContext provider) {
		//		EMFContext.setParent(provider, rootObj);
		if(!typeCodecInfo.isIgnoreType()) {
			if (codecModule.isSerializeType()) {
				if(gen.streamWriteContext() instanceof EMFCodecWriteContext cwt) {
					cwt.setCurrentEObject(rootObj);
				}				
//				If the typeKey is an EStructuralFeature of the EObject then we do not have to write anything
//				because it will be written with that feature
				String[] typeKeySplit = typeCodecInfo.getTypeKey().split("\\.");
				FeaturePath featurePath = UtilitiesFactory.eINSTANCE.createFeaturePath();				
				EStructuralFeature feature = null;
				boolean serializeType = false;
				for(String typeKeySegment : typeKeySplit) {
					if(feature != null && feature instanceof EReference ref) {
						feature = ref.getEReferenceType().getEStructuralFeature(typeKeySegment);
						if(feature != null) featurePath.getFeature().add(feature);
						else {
							LOGGER.warning(String.format("Feature %s not found in Object %s", typeKeySegment, ref.getName()));
							serializeType = true;
							break;
						}
					} else {
						feature = rootObj.eClass().getEStructuralFeature(typeKeySegment);
						if(feature != null) featurePath.getFeature().add(feature);
						else {
							LOGGER.warning(String.format("Feature %s not found in Object %s", typeKeySegment, rootObj.eClass().getName()));
							serializeType = true;
							break;
						}
					}					
				}
				if(serializeType) {
					CodecValueWriter<EClass, String> writer = holder.getWriterByName(typeCodecInfo.getTypeValueWriterName());
					String v = writer.writeValue(rootObj.eClass(), provider);
					String valueToWrite = v;
					if(typeCodecInfo.getTypeMap().containsValue(v)) {
						Entry<String, String> entry = typeCodecInfo.getTypeMap().stream().filter(e -> e.getValue().equals(v)).findFirst().orElse(null);
						if(entry != null) valueToWrite = entry.getKey();
					}
					
					gen.writeName(typeCodecInfo.getTypeKey());
					if (gen.canWriteTypeId()) {
						gen.writeTypeId(valueToWrite);
					} else {
						gen.writeString(valueToWrite);
					}
				}
			}
		}
	}


//	private Object resolveFeaturePath(EObject root, FeaturePath featurePath) {
//		EObject current = root;
//		Object value = null;
//
//		for (int i = 0; i < featurePath.getFeature().size(); i++) {
//			EStructuralFeature feature = featurePath.getFeature().get(i);
//			value = current.eGet(feature);
//
//			// If not at the end of the path, prepare for next step
//			if (i < featurePath.getFeature().size() - 1) {
//				if (value instanceof EObject) {
//					current = (EObject) value;
//				} else {
//					// We expected an EObject to navigate further, but got something else
//					LOGGER.severe(String.format("Error while navigating through FeaturePath at feature %s", feature.getName()));
//					return null;
//				}
//			}
//		}
//		return value;
//	}
}
