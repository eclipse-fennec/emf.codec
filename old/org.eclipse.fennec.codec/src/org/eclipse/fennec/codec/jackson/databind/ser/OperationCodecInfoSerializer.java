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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.jackson.module.CodecModule;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Codec Serializer for EOperation 
 * 
 * @author ilenia
 * @since Aug 22, 2024
 */
public class OperationCodecInfoSerializer implements CodecInfoSerializer {
	
	
	private CodecModule codecModule;
	private FeatureCodecInfo featureCodecInfo;
	
	public OperationCodecInfoSerializer(final CodecModule codecMoule, final CodecModelInfo codecModelInfoService, 
			final EClassCodecInfo eObjCodecInfo, final FeatureCodecInfo featureCodecInfo) {
		this.codecModule = codecMoule;
		this.featureCodecInfo = featureCodecInfo;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.ser.CodecInfoSerializer#serialize(org.eclipse.emf.ecore.EObject, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@Override
	public void serialize(EObject rootObj, JsonGenerator gen, SerializationContext provider) {
		if(featureCodecInfo.isIgnore()) return;
		Object value;		
		EOperation operation = (EOperation) featureCodecInfo.getFeature();
		try {
			value = rootObj.eInvoke(operation, null);
		} catch (InvocationTargetException | NullPointerException e) {
			// handle error
			value = null;
		}

		if (value != null) {
			if(codecModule.isUseNamesFromExtendedMetaData()) {
				gen.writeName(featureCodecInfo.getKey() != null ? featureCodecInfo.getKey() : operation.getName());
			} else {
				gen.writeName(operation.getName());
			}		
			ValueSerializer<Object> serializer = provider.findValueSerializer(value.getClass());
			serializer.serialize(value, gen, provider);
		}
	}

}
