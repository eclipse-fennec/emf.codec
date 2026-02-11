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
package org.eclipse.fennec.codec.jackson.databind.deser;

import java.util.Collection;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.jackson.databind.CodecTokenBuffer;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Codec Deserializer for References
 * @author ilenia
 * @since Sep 30, 2024
 */
public class ReferenceCodecInfoDeserializer extends ValueDeserializer<EObject> implements CodecInfoDeserializer {
	
	private static final Logger LOGGER = Logger.getLogger(ReferenceCodecInfoDeserializer.class.getName());

	private final CodecModule codecModule;
	private final CodecModelInfo codecModelInfoService;
	private TypeInfo typeInfo;
	private CodecInfoHolder infoHolder;

	public ReferenceCodecInfoDeserializer(CodecModule codecModule, CodecModelInfo codecModelInfoService, TypeInfo typeCodecInfo) {
		this.codecModule = codecModule;
		this.codecModelInfoService = codecModelInfoService;
		infoHolder = codecModelInfoService.getCodecInfoHolderByType(InfoType.TYPE);
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
	
	private EMFCodecReadContext extractCodecContext(JsonParser jp) {
		EMFCodecReadContext codecReadCtxt = null;
		if(jp instanceof CodecTokenBuffer.Parser ctbp) {
			if(ctbp.streamReadContext() instanceof EMFCodecReadContext crc)
			codecReadCtxt = crc;
		} else {
			if(jp.streamReadContext() instanceof EMFCodecReadContext crc) {
				codecReadCtxt = crc;
			}
		}
		return codecReadCtxt;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EObject deserialize(JsonParser jp, DeserializationContext ctxt) {

		
		EMFCodecReadContext codecReadCtxt = extractCodecContext(jp);
		if(codecReadCtxt == null) {
			throw new IllegalArgumentException("Null EMFCodecReadContext in ReferenceCodecDeserializer! Cannot continue!");
		}

		EObject parent = codecReadCtxt.getCurrentEObject();
		EStructuralFeature reference = getCurrentFeature(codecReadCtxt);
		Resource resource = codecReadCtxt.getResource();
		
//		TODO: we could try to retrieve the typeCodecInfo from the context, because at this point we do not have it if we construct this deserializer from the module
		EClassCodecInfo eObjCodecInfo = extractModelInfo(reference.getEContainingClass());
		FeatureCodecInfo featureCodecInfo = eObjCodecInfo.getReferenceCodecInfo().stream().filter(r -> r.getFeature().getName().equals(reference.getName())).findFirst().orElse(null);
		if(featureCodecInfo == null) {
			throw new IllegalArgumentException(String.format("Cannot retrieve FeatureCodecInfo for current EStructuralFeature %s. Something went wrong!", reference.getName()));
		}
		
		typeInfo = featureCodecInfo.getTypeInfo();
		codecReadCtxt.setCurrentTypeInfo(typeInfo);
		
		String id = null;
		String type = null;

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			final String field = jp.currentName();

			if (field.equalsIgnoreCase(codecModule.getRefKey())) {
				id = jp.nextStringValue();

			} else if (field.equalsIgnoreCase(featureCodecInfo.getTypeInfo().getTypeKey())) {
				type = jp.nextStringValue();
			}
		}
		EClass eClass = null;
		CodecValueReader<String, EClass> valueReader = infoHolder.getReaderByName(typeInfo.getTypeValueReaderName());
		
		if(type != null) {
			if(typeInfo.getTypeMap().containsKey(type)) {
				eClass = valueReader.readValue(typeInfo.getTypeMap().get(type), ctxt);
			} else {
				LOGGER.warning(String.format("No type key %s match found in TypeInfo mapping, Trying to direclty deserialize token value", type));
				eClass = valueReader.readValue(type, ctxt);
			}			
		}
//		If there is no type info in the serialized document
		if(type == null && reference.getEType() instanceof EClass refEClass) {
			eClass = refEClass;
		}
		EObject ref = null;
		if (id != null && eClass != null) {
			URI baseURI = resource.getURI().trimFragment();
            URI uri = codecModule.getUriHandler().resolve(baseURI, URI.createURI(id));
			ref = codecModule.getProxyFactory().createProxy(eClass, uri);
			if(reference.isMany()) {
				Collection<EObject> objs = (Collection<EObject>) parent.eGet(reference);
				objs.add(ref);
			} else {
				parent.eSet(reference, ref);
			}			
		}
		return ref;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.demo.jackson.deser.CodecInfoDeserializer#deserializeAndSet(com.fasterxml.jackson.core.JsonParser, org.eclipse.emf.ecore.EObject, com.fasterxml.jackson.databind.DeserializationContext, org.eclipse.emf.ecore.resource.Resource)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deserializeAndSet(JsonParser jp, EObject current, DeserializationContext ctxt, Resource resource) {
//		deserialize(jp, ctxt);
		
		EMFCodecReadContext codecReadCtxt = extractCodecContext(jp);
		if(codecReadCtxt == null) {
			throw new IllegalArgumentException("Null EMFCodecReadContext in ReferenceCodecDeserializer! Cannot continue!");
		}

		
		EObject parent = getCurrentEObject((TokenStreamContext) codecReadCtxt);
		EStructuralFeature reference = getCurrentFeature(codecReadCtxt);
		
//		TODO: we could try to retrieve the typeCodecInfo from the context, because at this point we do not have it if we construct this deserializer from the module
		EClassCodecInfo eObjCodecInfo = extractModelInfo(reference.getEContainingClass());
		FeatureCodecInfo featureCodecInfo = eObjCodecInfo.getReferenceCodecInfo().stream().filter(r -> r.getFeature().getName().equals(reference.getName())).findFirst().orElse(null);
		if(featureCodecInfo == null) {
			throw new IllegalArgumentException(String.format("Cannot retrieve FeatureCodecInfo for current EStructuralFeature %s. Something went wrong!", reference.getName()));
		}
		typeInfo = featureCodecInfo.getTypeInfo();
		
		String id = null;
		String type = null;

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			final String field = jp.currentName();

			if (field.equalsIgnoreCase(codecModule.getRefKey())) {
				id = jp.nextStringValue();

			} else if (field.equalsIgnoreCase(featureCodecInfo.getTypeInfo().getTypeKey())) {
				type = jp.nextStringValue();
			}
		}
		EClass eClass = null;
		CodecValueReader<String, EClass> valueReader = infoHolder.getReaderByName(typeInfo.getTypeValueReaderName());
		if(type != null) {
			if(typeInfo.getTypeMap().containsKey(type)) {
				eClass = valueReader.readValue(typeInfo.getTypeMap().get(type), ctxt);
			} else {
				LOGGER.warning(String.format("No type key %s match found in TypeInfo mapping, Trying to direclty deserialize token value", type));
				eClass = valueReader.readValue(type, ctxt);
			}			
		}
//		If there is no type info in the serialized document
		if(type == null && reference.getEType() instanceof EClass refEClass) {
			eClass = refEClass;
		}
		EObject ref = null;
		if (id != null && eClass != null) {
			URI baseURI = resource.getURI().trimFragment();
            URI uri = codecModule.getUriHandler().resolve(baseURI, URI.createURI(id));
			ref = codecModule.getProxyFactory().createProxy(eClass, uri);
			if(reference.isMany()) {
				Collection<EObject> objs = (Collection<EObject>) parent.eGet(reference);
				objs.add(ref);
			} else {
				parent.eSet(reference, ref);
			}			
		}
	}
	
	private EStructuralFeature getCurrentFeature(EMFCodecReadContext ctxt) {
		if(ctxt.getCurrentFeature() != null) return ctxt.getCurrentFeature();
		return getCurrentFeature((TokenStreamContext) ctxt);
	}
	
	private EStructuralFeature getCurrentFeature(final TokenStreamContext ctxt) {
		TokenStreamContext parentCodecReadCtxt = null;
		if(ctxt.getParent() != null) {
			if(ctxt.getParent().inObject()) {
				parentCodecReadCtxt = ctxt.getParent();
			} else if(ctxt.getParent().inArray()) {
				parentCodecReadCtxt = ctxt.getParent();
			}
		}
		if(parentCodecReadCtxt != null) {
			EReference reference = (EReference) (((EMFCodecReadContext) parentCodecReadCtxt).getCurrentFeature());
			return reference;

		}
		return null;
	}
	
	private EObject getCurrentEObject(final TokenStreamContext ctxt) {
		TokenStreamContext parentCodecReadCtxt = null;
		if(ctxt.getParent() != null) {
			if(ctxt.getParent().inObject()) {
				parentCodecReadCtxt = ctxt.getParent();
			} else if(ctxt.getParent().inArray()) {
				parentCodecReadCtxt = ctxt.getParent().getParent();
			}
		}
		if(parentCodecReadCtxt != null) {
			EObject reference = (EObject) (((EMFCodecReadContext) parentCodecReadCtxt).getCurrentEObject());
			return reference;

		}
		return null;
	}
}
