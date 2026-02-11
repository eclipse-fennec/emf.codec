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
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecContext;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.gecko.emf.utilities.FeaturePath;
import org.gecko.emf.utilities.UtilitiesFactory;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Codec Serializer for References
 * 
 * @author ilenia
 * @since Aug 22, 2024
 */
public class ReferenceCodecInfoSerializer implements CodecInfoSerializer {

	private static final Logger LOGGER = Logger.getLogger(ReferenceCodecInfoSerializer.class.getName());

	private CodecModule codecModule;
	private CodecModelInfo codecModelInfoService;
	private EClassCodecInfo eObjCodecInfo;
	private FeatureCodecInfo featureCodecInfo;
	private TypeInfo typeInfo;
	private CodecInfoHolder holder;

	public ReferenceCodecInfoSerializer(final CodecModule codecMoule, final CodecModelInfo codecModelInfoService,
			final EClassCodecInfo eObjCodecInfo, final FeatureCodecInfo featureCodecInfo) {
		this.codecModule = codecMoule;
		this.codecModelInfoService = codecModelInfoService;
		this.eObjCodecInfo = eObjCodecInfo;
		this.featureCodecInfo = featureCodecInfo;
		holder = codecModelInfoService.getCodecInfoHolderByType(InfoType.TYPE);
	}
	


	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.ser.CodecInfoSerializer#serialize(org.eclipse.emf.ecore.EObject, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void serialize(EObject rootObj, JsonGenerator jg, SerializationContext provider) {
		if (featureCodecInfo.isIgnore())
			return;

		// Check global ignore list
		if (featureCodecInfo.getFeature() != null &&
			codecModule.getGlobalIgnoreFeatureNames().contains(featureCodecInfo.getFeature().getName())) {
			return;
		}
		if(featureCodecInfo.getFeature() == null) {
			LOGGER.severe(String.format("No Feature found in CodecFeatureInfo. Feature will not be serialized!"));
			return;
		}
		EReference feature = (EReference) featureCodecInfo.getFeature();
		FeatureCodecInfo featureCodecInfo = eObjCodecInfo.getReferenceCodecInfo().stream().filter(r -> r.getFeature().getName().equals(feature.getName())).findFirst().orElse(null);
		if(featureCodecInfo == null) {
			throw new IllegalArgumentException(String.format("Cannot retrieve FeatureCodecInfo for current EStructuralFeature %s. Something went wrong!", feature.getName()));
		}
		typeInfo = featureCodecInfo.getTypeInfo();

		if (jg.streamWriteContext() instanceof EMFCodecContext cwt) {
			cwt.setCurrentFeature(feature);
			cwt.setCurrentEObject(rootObj);
			cwt.setResource(rootObj.eResource());
		} else {
			throw new IllegalArgumentException(String.format("StreamWriteContext is not of type EMFCodecWriteContext! Something went wrong!"));
		}

		if (feature.isMany()) {
			List<EObject> values = (List<EObject>) rootObj.eGet(feature);
			serializeManyReferences(rootObj, values, feature, jg, provider);
		} else {
			EObject value = (EObject) rootObj.eGet(feature);
			serializeSingleReference(rootObj, value, feature, jg, provider);
		}
	}

	private void serializeManyReferences(EObject rootObj, List<EObject> values, EReference feature, JsonGenerator jg,
			SerializationContext provider) {

		if (values.isEmpty() && (!codecModule.isSerializeDefaultValue() || !codecModule.isSerializeEmptyValue()))
			return;
		if (codecModule.isUseNamesFromExtendedMetaData()) {
			jg.writeName(featureCodecInfo.getKey());
		} else {
			jg.writeName(feature.getName());
		}
		if (values instanceof EMap eMap) {
			serializeEMap(jg, provider, eMap);
		} else {
			serializeArray(rootObj, values, feature, jg, provider);
		}
	}

	@SuppressWarnings("unchecked")
	private void serializeEMap(JsonGenerator jg, SerializationContext provider, EMap<?, ?> eMap) {
		jg.writeStartObject();
		eMap.forEach(value -> {

			BasicEMap.Entry<String, Object> entry = (BasicEMap.Entry<String, Object>) value;
			jg.writeName(entry.getKey());
			Object v = entry.getValue();
			if (v == null) {
				jg.writeNull();
			} else if (v instanceof EObject eo) {
				if (((EObject) entry).eContainmentFeature().isContainment()) {
					new CodecEObjectSerializer(codecModule, codecModelInfoService).serialize(eo, jg, provider);
				} else {
					serializeNonContainment((EObject) entry, eo, jg, provider);
				}
			} else if (v instanceof EMap<?,?> innerMap) {
				serializeEMap(jg, provider, innerMap);
			} else {
				ValueSerializer<Object> valueSerializer = provider.findValueSerializer(v.getClass());
				valueSerializer.serialize(v, jg, provider);
			}

		});
		jg.writeEndObject();
	}

	private void serializeArray(EObject rootObj, List<EObject> values, EReference feature, JsonGenerator jg,
			SerializationContext provider)  {
		jg.writeStartArray(values);
		values.forEach(value -> {
			serializeSingleReferenceValue(rootObj, value, feature, jg, provider);

		});
		jg.writeEndArray();
	}

	private void serializeSingleReference(EObject rootObj, EObject value, EReference feature, JsonGenerator jg,
			SerializationContext provider) {
		if (value == null && (!codecModule.isSerializeDefaultValue() || !codecModule.isSerializeNullValue()))
			return;

		if (codecModule.isUseNamesFromExtendedMetaData()) {
			jg.writeName(featureCodecInfo.getKey());
		} else {
			jg.writeName(feature.getName());
		}
//		We have to reset the current feature because for some reason calling writeName resets it...
		((EMFCodecContext) jg.streamWriteContext()).setCurrentFeature(feature);
		serializeSingleReferenceValue(rootObj, value, feature, jg, provider);
	}

	private void serializeSingleReferenceValue(EObject rootObj, EObject value, EReference feature, JsonGenerator jg,
			SerializationContext provider) {

		if (feature.isContainment()) {
			if (value == null)
				jg.writeNull();
			else {
				ValueSerializer<Object> valueSerializer = provider.findValueSerializer(value.getClass());
				valueSerializer.serialize(value, jg, provider);
//				new CodecEObjectSerializer(codecModule, codecModelInfoService).serialize(value, jg, provider);
			}
		} else {
			serializeNonContainment(rootObj, value, jg, provider);
		}
	}

	@SuppressWarnings("unchecked")
	private void serializeNonContainment(EObject rootObj, EObject value, JsonGenerator jg, SerializationContext provider) {
		if (value == null) {
			jg.writeNull();
			return;
		}
		final String href = getHRef(jg, rootObj, value);

		jg.writeStartObject(value);
		
		if(!typeInfo.isIgnoreType()) {
			if(codecModule.isSerializeType()) {
				String[] typeKeySplit = typeInfo.getTypeKey().split("\\.");
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
						feature = value.eClass().getEStructuralFeature(typeKeySegment);
						if(feature != null) featurePath.getFeature().add(feature);
						else {
							LOGGER.warning(String.format("Feature %s not found in Object %s", typeKeySegment, value.eClass().getName()));
							serializeType = true;
							break;
						}
					}					
				}
				if(serializeType) {
					CodecValueWriter<EClass, String> writer = holder.getWriterByName(typeInfo.getTypeValueWriterName());
					String v = writer.writeValue(value.eClass(), provider);
					String valueToWrite = v;
					if(typeInfo.getTypeMap().containsValue(v)) {
						Entry<String, String> entry = typeInfo.getTypeMap().stream().filter(e -> e.getValue().equals(v)).findFirst().orElse(null);
						if(entry != null) valueToWrite = entry.getKey();
					}
					
					jg.writeName(typeInfo.getTypeKey());
					if (jg.canWriteTypeId()) {
						jg.writeTypeId(valueToWrite);
					} else {
						jg.writeString(valueToWrite);
					}
				}
			}
		}
		

		if (href == null) {
			jg.writeNullProperty(codecModule.getRefKey());
		} else {
			jg.writeStringProperty(codecModule.getRefKey(), href);
		}
		jg.writeEndObject();

	}
//	
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

	private String getHRef(JsonGenerator jg, final EObject parent, final EObject value) {
		if (isExternal(jg, parent, value)) {

//			URI targetURI = EMFContext.getURI(ctxt, value);
//			URI sourceURI = EMFContext.getURI(ctxt, parent);
			URI targetURI = EcoreUtil.getURI(value);
			URI sourceURI = EcoreUtil.getURI(parent);
			URI deresolved = codecModule.getUriHandler().deresolve(sourceURI, targetURI);

			return deresolved == null ? null : deresolved.toString();

		}
		Resource resource = ((EMFCodecWriteContext) jg.streamWriteContext()).getResource();
		if (resource != null) {
			return resource.getURIFragment(value);
		}

		return null;
	}

//	private boolean isExternal(final SerializationContext ctxt, final EObject source, final EObject target) {
//		Resource sourceResource = ((CodecWriteContext) ctxt.getGenerator().streamWriteContext()).getResource();
//
//		if (target.eIsProxy() && target instanceof InternalEObject internalEObject) {
//			URI uri = internalEObject.eProxyURI();
//
//			return sourceResource != null && sourceResource.getURI() != null
//					&& !sourceResource.getURI().equals(uri.trimFragment());
//		}
//
//		return sourceResource == null || sourceResource != EMFContext.getResource(ctxt, target);
//	}
	
	private boolean isExternal(JsonGenerator jg, final EObject source, final EObject target) {
		Resource sourceResource = ((EMFCodecWriteContext) jg.streamWriteContext()).getResource();

		if (target.eIsProxy() && target instanceof InternalEObject internalEObject) {
			URI uri = internalEObject.eProxyURI();

			return sourceResource != null && sourceResource.getURI() != null
					&& !sourceResource.getURI().equals(uri.trimFragment());
		}

		// Check if target is in a different resource than source
		Resource targetResource = target.eResource();
		return sourceResource == null || sourceResource != targetResource;
	}
}
