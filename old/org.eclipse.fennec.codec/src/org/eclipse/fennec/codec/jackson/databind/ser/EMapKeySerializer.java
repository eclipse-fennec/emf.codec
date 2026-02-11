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
package org.eclipse.fennec.codec.jackson.databind.ser;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 
 * @author ilenia
 * @since Apr 24, 2025
 */
public class EMapKeySerializer extends ValueSerializer<Object> {

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueSerializer#serialize(java.lang.Object, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@Override
	public void serialize(final Object value, final JsonGenerator gen, final SerializationContext serializers) {
		
		EMFCodecWriteContext codecWriteCtxt = null;
		if(gen.streamWriteContext() instanceof EMFCodecWriteContext cwc) {
			codecWriteCtxt = cwc;
		}
		
//		EStructuralFeature feature = EMFContext.getFeature(serializers);
		EStructuralFeature feature = codecWriteCtxt != null ? codecWriteCtxt.getCurrentFeature() : null;
		Optional<EReference> mapRef = Optional.ofNullable(feature).filter(EReference.class::isInstance)
				.map(EReference.class::cast);
		Optional<EStructuralFeature> keyFeature = mapRef.map(EReference::getEReferenceType)
				.map(mapType -> mapType.getEStructuralFeature("key")).filter(Objects::nonNull);
		Optional<EDataType> keyType = keyFeature.filter(EAttribute.class::isInstance).map(EAttribute.class::cast)
				.map(EAttribute::getEAttributeType);
		if (keyType.isPresent()) {
			gen.writeName(EcoreUtil.convertToString(keyType.get(), value));
		} else {
			// the metamodel is probably incorrect...
			gen.writeName(value.toString());
		}
	}

}
