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
import org.eclipse.fennec.codec.CodecEMFDeserializers;
import org.eclipse.fennec.codec.jackson.databind.deser.DefaultCodecEMFDeserializers;
import org.eclipse.fennec.codec.jsonschema.readers.SmartJsonSchemaDeserializer;
import org.osgi.service.component.annotations.Component;

import tools.jackson.databind.BeanDescription.Supplier;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;

/**
 * 
 * @author ilenia
 * @since Aug 7, 2025
 */
@Component(immediate = true, name = "JsonSchemaCodecEMFDeserializers", service = CodecEMFDeserializers.class)
public class JsonSchemaCodecEMFDeserializers extends DefaultCodecEMFDeserializers {
	
	@Override
	public ValueDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
			Supplier beanDescRef) {
		
	
		if (type.isTypeOrSubTypeOf(Resource.class)) {
			return resourceDeserializer;
		}

		if (type.isReferenceType()) {
			return referenceDeserializer;
		}
		if (type.isTypeOrSubTypeOf(EObject.class)) {
			return new SmartJsonSchemaDeserializer(type.getRawClass(), module, codecModelInfoService);
		}
		return super.findBeanDeserializer(type, config, beanDescRef);
	}

}
