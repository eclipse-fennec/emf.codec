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

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Codec Enumerator serializer
 * 
 * @author ilenia
 * @since Oct 28, 2024
 */
public class EnumeratorCodecInfoSerializer implements CodecInfoSerializer  {

	private static final Logger LOGGER = Logger.getLogger(EnumeratorCodecInfoSerializer.class.getName());

	private CodecModule codecModule;
	private FeatureCodecInfo featureCodecInfo;

	public EnumeratorCodecInfoSerializer(final CodecModule codecMoule, final FeatureCodecInfo featureCodecInfo) {
		this.codecModule = codecMoule;
		this.featureCodecInfo = featureCodecInfo;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.ser.CodecInfoSerializer#serialize(org.eclipse.emf.ecore.EObject, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void serialize(EObject rootObj, JsonGenerator gen, SerializationContext provider) {
		if (featureCodecInfo.isIgnore())
			return;
		if (featureCodecInfo.getFeature() == null) {
			LOGGER.severe(String.format("FeatureCodecInfo with no Feature inside! Cannot serialize!"));
		}
		EStructuralFeature feature = (EStructuralFeature) featureCodecInfo.getFeature();
//
//		EMFContext.setParent(provider, rootObj);
//		EMFContext.setFeature(provider, feature);

		
		if (gen.streamWriteContext() instanceof EMFCodecWriteContext cwt) {
			cwt.setCurrentFeature(feature);
			cwt.setCurrentEObject(rootObj);			
		}

		if (rootObj.eIsSet(feature)) {
			if (feature.isMany()) {
				List<Object> values = (List<Object>) rootObj.eGet(feature);
				serializeManyAttribute(values, feature, gen);
			} else {
				Object value = rootObj.eGet(feature);
				serializeSingleAttribute(value, feature, gen);
			}
		} else if (codecModule.isSerializeDefaultValue()) {
			if (feature.isMany()) {
				List<Object> values = (List<Object>) rootObj.eGet(feature);
				serializeManyAttribute(values, feature, gen);
			} else {
				Object value = feature.getDefaultValue();
				serializeSingleAttribute(value, feature, gen);
			}
		}
	}
	

	private void serializeSingleAttribute(Object value, EStructuralFeature feature, JsonGenerator gen) {
		if (value == null && !codecModule.isSerializeNullValue()) {
			return;
		}
		if (value instanceof String str && str.isEmpty() && !codecModule.isSerializeEmptyValue()) {
			return;
		}
		if (codecModule.isUseNamesFromExtendedMetaData()) {
			gen.writeName(featureCodecInfo.getKey());
		} else {
			gen.writeName(feature.getName());
		}
		serializeSingleAttributeValue(value, gen);
	}

	private void serializeSingleAttributeValue(Object value, JsonGenerator gen) {

		if (codecModule.isWriteEnumLiterals()) {
			gen.writeString(((Enumerator) value).getLiteral());
		} else {
			gen.writeString(((Enumerator) value).getName());
		}
	}

	private void serializeManyAttribute(List<Object> values, EStructuralFeature feature, JsonGenerator gen) {
		if (values.isEmpty() && (!codecModule.isSerializeDefaultValue() || !codecModule.isSerializeEmptyValue()))
			return;
		if (codecModule.isUseNamesFromExtendedMetaData()) {
			gen.writeName(featureCodecInfo.getKey());
		} else {
			gen.writeName(feature.getName());
		}

		//		TODO: check serailized array batched here...?
		gen.writeStartArray();
		values.forEach(value -> {
			serializeSingleAttributeValue(value, gen);
		});
		gen.writeEndArray();
	}
}
