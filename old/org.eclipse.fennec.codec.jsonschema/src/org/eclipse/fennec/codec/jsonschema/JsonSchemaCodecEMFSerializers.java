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
package org.eclipse.fennec.codec.jsonschema;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.CodecEMFSerializers;
import org.eclipse.fennec.codec.jackson.databind.ser.DefaultCodecEMFSerializers;
import org.eclipse.fennec.codec.jsonschema.writers.SmartJsonSchemaSerializer;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.annotation.JsonFormat.Value;

import tools.jackson.databind.BeanDescription.Supplier;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;

@Component(immediate = true, name = "JsonSchemaCodecEMFSerializers", service = CodecEMFSerializers.class)
public class JsonSchemaCodecEMFSerializers extends DefaultCodecEMFSerializers{

	@Override
	public ValueSerializer<?> findSerializer(SerializationConfig config, JavaType type, Supplier beanDescRef,
			Value formatOverrides) {
		if (type.isTypeOrSubTypeOf(Resource.class)) {
			return resourceSerializer;
		}
//		if(type.isTypeOrSubTypeOf(EPackage.class)) {
//			return new EPackageToJsonSchemaSerializer();
//		}
//		if (type.isTypeOrSubTypeOf(EObject.class)) {
//			return new CodecEObjectSerializer(codecModule, codecModelInfoService);
//		}
		if (type.isTypeOrSubTypeOf(EObject.class)) {
			return new SmartJsonSchemaSerializer(codecModule, codecModelInfoService);
		}
		return super.findSerializer(config, type, beanDescRef, formatOverrides);
	}
}
