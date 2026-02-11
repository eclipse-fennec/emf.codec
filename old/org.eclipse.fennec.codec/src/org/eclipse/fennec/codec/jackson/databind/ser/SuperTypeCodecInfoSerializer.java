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

import static java.util.Objects.nonNull;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.CodecGeneratorBase;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.SuperTypeInfo;
import org.eclipse.fennec.codec.info.helper.CodecIOHelper;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.jdk.StringArraySerializer;

/**
 * Codec Serializer for SuperType Info
 * @author ilenia
 * @since Aug 22, 2024
 */
public class SuperTypeCodecInfoSerializer implements CodecInfoSerializer {
	
	private final ValueSerializer<String[]> serializer = StringArraySerializer.instance;
	
	private CodecModule codecModule;
	private CodecModelInfo codecModelInfoService;
	private SuperTypeInfo superTypeCodecInfo;
	
	public SuperTypeCodecInfoSerializer(final CodecModule codecMoule, final CodecModelInfo codecModelInfoService, 
			final EClassCodecInfo eObjCodecInfo, final SuperTypeInfo superTypeCodecInfo) {
		this.codecModule = codecMoule;
		this.codecModelInfoService = codecModelInfoService;
		this.superTypeCodecInfo = superTypeCodecInfo;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.ser.CodecInfoSerializer#serialize(org.eclipse.emf.ecore.EObject, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void serialize(EObject rootObj, JsonGenerator gen, SerializationContext provider) {
//		EMFContext.setParent(provider, rootObj);
		if(superTypeCodecInfo.isIgnoreSuperType()) return;
		
		if (codecModule.isSerializeType() && codecModule.isSerializeSuperTypes()) {
			if(gen.streamWriteContext() instanceof EMFCodecWriteContext cwt) {
				cwt.setCurrentEObject(rootObj);
			}
			CodecInfoHolder holder = codecModelInfoService.getCodecInfoHolderByType(InfoType.SUPER_TYPE);
			CodecValueWriter<EClass, String[]> writer = holder
					.getWriterByName(codecModule.isSerializeAllSuperTypes() ? CodecIOHelper.ALL_SUPERTYPE_WRITER.getName() 
							: CodecIOHelper.SINGLE_SUPERTYPE_WRITER.getName());
			String[] values = writer.writeValue(rootObj.eClass(), provider);
			CodecGeneratorBase cgb = null;
			if (gen instanceof CodecGeneratorBase) {
				cgb = (CodecGeneratorBase) gen;
			}
			if (nonNull(values) && values.length > 0) {
				gen.writeName(codecModule.getSuperTypeKey());
				/*
				 * We use our custom callback, if possible.
				 */
				if (!codecModule.isSerializeSuperTypesAsArray()) {
					if (nonNull(cgb) && cgb.canWriteSuperTypes()) {
						cgb.writeSuperTypes(values);
					} else {
						if (gen.canWriteTypeId()) {
							gen.writeTypeId(values);
						} else {
							//							Comma separated string...?
							StringBuilder sb = new StringBuilder();
							for(String s : values) {
								sb.append(s);
								sb.append(",");
							}
							sb.replace(sb.length()-1, sb.length(), "");
							gen.writeString(sb.toString());
						}

					}        		 
				} else {
					serializer.serialize(values, gen, provider);
				}
			}
		}
	}
}
