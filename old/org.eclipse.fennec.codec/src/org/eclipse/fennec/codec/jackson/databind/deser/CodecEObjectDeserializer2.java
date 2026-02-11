///**
// * Copyright (c) 2012 - 2024 Data In Motion and others.
// * All rights reserved. 
// * 
// * This program and the accompanying materials are made
// * available under the terms of the Eclipse Public License 2.0
// * which is available at https://www.eclipse.org/legal/epl-2.0/
// *
// * SPDX-License-Identifier: EPL-2.0
// * 
// * Contributors:
// *     Data In Motion - initial API and implementation
// */
//package org.eclipse.fennec.codec.jackson.databind.deser;
//
//import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
//
//import java.util.logging.Logger;
//
//import org.eclipse.emf.ecore.EClass;
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.EReference;
//import org.eclipse.emf.ecore.resource.Resource;
//import org.eclipse.emf.ecore.util.EcoreUtil;
//import org.eclipse.fennec.codec.constants.CodecResourceOptions;
//import org.eclipse.fennec.codec.info.CodecModelInfo;
//import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
//import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
//import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
//import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
//import org.eclipse.fennec.codec.info.codecinfo.IdentityInfo;
//import org.eclipse.fennec.codec.info.codecinfo.InfoType;
//import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
//import org.eclipse.fennec.codec.info.codecinfo.SuperTypeInfo;
//import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
//import org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext;
//import org.eclipse.fennec.codec.jackson.module.CodecModule;
//import org.eclipse.fennec.codec.jackson.utils.CodecParserException;
//
//import tools.jackson.core.JsonParser;
//import tools.jackson.core.JsonToken;
//import tools.jackson.core.TokenStreamContext;
//import tools.jackson.databind.DeserializationContext;
//import tools.jackson.databind.ValueDeserializer;
//import tools.jackson.databind.deser.jdk.StringDeserializer;
//import tools.jackson.databind.util.TokenBuffer;
//
///**
// * 
// * @author ilenia
// * @since Sep 26, 2024
// */
//public class CodecEObjectDeserializer2 extends ValueDeserializer<EObject> {
//
//	private static final Logger LOGGER = Logger.getLogger(CodecEObjectDeserializer2.class.getName());
//
//	private final CodecModule codecModule;
//	private final CodecModelInfo codecModelInfoService;
//
//	public CodecEObjectDeserializer2(final Class<?> currentType, final CodecModule codecModule, 
//			final CodecModelInfo codecModelInfoService) {
//		this.codecModule = codecModule;
//		this.codecModelInfoService = codecModelInfoService;
//	}
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see tools.jackson.databind.ValueDeserializer#handledType()
//	 */
//	@Override
//	public Class<?> handledType() {
//		return EObject.class;
//	}
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see tools.jackson.databind.ValueDeserializer#deserialize(tools.jackson.core.JsonParser, tools.jackson.databind.DeserializationContext)
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public EObject deserialize(final JsonParser jp, final DeserializationContext ctxt) {
//
//		//		EMFContext.prepare(ctxt);
//		EMFCodecReadContext codecReadCtxt = null;
//		if(jp.streamReadContext() instanceof EMFCodecReadContext crc) {
//			codecReadCtxt = crc;
//		}
//		//		final Resource resource = getResource(ctxt);
//		//		EStructuralFeature feature = getFeature(ctxt);
//		//		final EClass defaultType = getDefaultType(ctxt);
//
//		final Resource resource = codecReadCtxt != null ? codecReadCtxt.getResource() : null;
//		final EClass defaultType = codecReadCtxt != null ? getDefaultType((TokenStreamContext)codecReadCtxt) : null;
//
//
//		//		In case of contained ref, the defaultType is set and we can immediately construct everything. 
//		//		This fixes the issue when we don't have a _type property for contained ref, to retrieve the actual type
//		//		In case of root obj we have the ROOT_OBJECT option that is mandatory if the _type is not set so we 
//		//		can use that to construct everything		
//		EClass type;
//		if (defaultType != null) {
//			type = defaultType;
//		} else {
//			type = (EClass) ctxt.getAttribute(CodecResourceOptions.CODEC_ROOT_OBJECT);
//		}
//		EObject current = type == null ? null : EcoreUtil.create(type);
//		if(codecReadCtxt != null) codecReadCtxt.setCurrentEObject(current);
//
//		//		In case of non contained ref w/o type info we try to retrieve the root ctxt so we know which ref we are trying to deserialize
////		if(current == null) {
////			DatabindContext rootCtxt = EMFContext.getRootContext();
////			if(rootCtxt != null) {
////				feature = EMFContext.getFeature(rootCtxt);
////				if(feature != null && feature.getEType() instanceof EClass eClass) {
////					type = eClass;
////					current = EcoreUtil.create(type);
////					if(codecReadCtxt != null) codecReadCtxt.setCurrentEObject(current);
////				}
////			}
////		}
//
//		PackageCodecInfo codecModelInfo = codecModule.getCodecModelInfo();
//		EClassCodecInfo eObjCodecInfo = null;
//		if(type != null) {
//			for(EClassCodecInfo eci : codecModelInfo.getEClassCodecInfo()) {
//				if(eci.getClassifier().equals(type)) {
//					eObjCodecInfo = eci;
//					break;
//				}
//			}
//		}
//
//		TokenBuffer buffer = null;
//		JsonToken nextToken = jp.nextToken();
//		CodecInfoHolder infoHolder = codecModelInfoService.getCodecInfoHolderByType(InfoType.TYPE);
//		while (nextToken != JsonToken.END_OBJECT && nextToken != null) {
//			final String field = jp.currentName();
//			//			If it was not possible to determine the type from the conditions before then we look for the _type in the serialized document
//			if(field.equals(codecModule.getTypeKey()) && current == null) {
//				jp.nextToken();
//				for(CodecValueReader<String, EClass> reader : infoHolder.getReaders()) {
//					try {
//						type = reader.readValue(StringDeserializer.instance.deserialize(jp, ctxt), ctxt);
//					} catch(Exception e) {
//						type = null;
//					}
//					if(type != null) break;
//				}
//				EClass rootObj = type;
//				eObjCodecInfo = codecModelInfo.getEClassCodecInfo().stream().
//						filter(eci -> 
//						eci.getClassifier().equals(rootObj))
//						.findFirst().get();
//				current = EcoreUtil.create(type);
//				if(codecReadCtxt != null) codecReadCtxt.setCurrentEObject(current);
//			}
//			else if(current != null) {
//				FeatureCodecInfo featureCodecInfo = getFeatureCodecInfo(field, eObjCodecInfo);
//				if(featureCodecInfo instanceof IdentityInfo idInfo) {
//					new IdCodecInfoDeserializer(codecModule, codecModelInfoService, eObjCodecInfo, idInfo)
//					.deserializeAndSet(jp, current, ctxt, resource);
//				} 
//				else if(featureCodecInfo != null && !(featureCodecInfo instanceof TypeInfo) && !(featureCodecInfo instanceof SuperTypeInfo)) {
//					new FeatureCodecInfoDeserializer(codecModule, codecModelInfoService, eObjCodecInfo, featureCodecInfo, eObjCodecInfo.getTypeInfo())
//					.deserializeAndSet(jp, current, ctxt, resource);
//
//				} else if(featureCodecInfo == null && current != null) {
//					handleUnknownProperty(jp, resource, ctxt, current.eClass());
//				} 
//			} else {
//				//				since current is not set, we are copying the structure and try with the next property 
//				//				(because we want to look for the type first so we know which object we have to build)
//				if (buffer == null) {
//					buffer = TokenBuffer.forBuffering(jp, ctxt);
//				}
//				buffer.copyCurrentStructure(jp);
//			}
//			nextToken = jp.nextToken();
//		}
//
//		//		 handle empty objects
//		if (buffer == null && current == null && defaultType != null) {
//			return EcoreUtil.create(defaultType);
//		}
//		return buffer == null ? current : postDeserialize(buffer, current, defaultType, ctxt, eObjCodecInfo, codecReadCtxt);
//	}
//
//	private EObject postDeserialize(final TokenBuffer buffer, EObject current, final EClass defaultType, final DeserializationContext ctxt, EClassCodecInfo eObjCodecInfo, EMFCodecReadContext codecReadCtxt) {
//		if (current == null && defaultType == null) {
//			return null;
//		}
//
//		//		Resource resource = getResource(ctxt);
//		Resource resource = codecReadCtxt.getResource();
//
//		if (current == null) {
//			current = EcoreUtil.create(defaultType);
//		}
//
//		JsonParser jp = buffer.asParser();
//		JsonToken nextToken = jp.nextToken();
//		while (nextToken != JsonToken.END_OBJECT && nextToken != null) {
//			final String field = jp.currentName();
//			FeatureCodecInfo featureCodecInfo = getFeatureCodecInfo(field, eObjCodecInfo);
//			if(featureCodecInfo instanceof IdentityInfo idInfo) {
//				new IdCodecInfoDeserializer(codecModule, codecModelInfoService, eObjCodecInfo, idInfo)
//				.deserializeAndSet(jp, current, ctxt, resource);
//			} 
//			else if(featureCodecInfo != null && !(featureCodecInfo instanceof TypeInfo) && !(featureCodecInfo instanceof SuperTypeInfo)) {
//				new FeatureCodecInfoDeserializer(codecModule, codecModelInfoService, eObjCodecInfo, featureCodecInfo, eObjCodecInfo.getTypeInfo())
//				.deserializeAndSet(jp, current, ctxt, resource);
//
//			} else if(featureCodecInfo == null && current != null) {
//				handleUnknownProperty(jp, resource, ctxt, current.eClass());
//			} 
//
//			nextToken = jp.nextToken();
//		}
//
//		jp.close();
//		buffer.close();
//		return current;
//	}
//
//	private void handleUnknownProperty(final JsonParser jp, final Resource resource, final DeserializationContext ctxt,	EClass currentEClass)  {
//		if (resource != null && ctxt.getConfig().hasDeserializationFeatures(FAIL_ON_UNKNOWN_PROPERTIES.getMask())) {
//			resource.getErrors().add(new CodecParserException(String.format("Unknown feature '%s' for %s", jp.currentName(), EcoreUtil.getURI(currentEClass)),jp.currentLocation()));
//		}
//		// we didn't find a feature so consume
//		// the field and move on
//		jp.nextToken();
//		jp.skipChildren();
//	}
//
//	private FeatureCodecInfo getFeatureCodecInfo(String fieldName, EClassCodecInfo eObjCodecInfo) {
//		if(fieldName == null) return null;
//		if(fieldName.equals(codecModule.getIdKey())) return eObjCodecInfo.getIdentityInfo();
//		if(fieldName.equals(codecModule.getTypeKey())) return eObjCodecInfo.getTypeInfo();
//		if(fieldName.equals(codecModule.getSuperTypeKey())) return eObjCodecInfo.getSuperTypeInfo();
//		for(FeatureCodecInfo featureCodecInfo : eObjCodecInfo.getFeatureInfo()) {
//			String key = codecModule.isUseNamesFromExtendedMetaData() ? featureCodecInfo.getKey() : featureCodecInfo.getFeatures().get(0).getName();
//			if(fieldName.equals(key)) {
//				return featureCodecInfo;
//			}
//		}
//		LOGGER.warning(() -> "No CodecInfo found for field " + fieldName + " in " + eObjCodecInfo.getId());
//		return null;
//	}
//
//	private EClass getDefaultType(final TokenStreamContext ctxt) {
//		EClass type = null;
//		TokenStreamContext codecReadCtxt = null;
//		if(ctxt.getParent() != null) {
//			if(ctxt.getParent().inObject()) {
//				codecReadCtxt = ctxt.getParent();
//			} else if(ctxt.getParent().inArray()) {
//				codecReadCtxt = ctxt.getParent().getParent();
//			}
//		}
//
//		if(codecReadCtxt != null) {
//			EReference reference = (EReference) (((EMFCodecReadContext) codecReadCtxt).getCurrentFeature());
//			if(reference != null) {
//				type = (EClass) reference.getEGenericType().getERawType();
//			}
//
//		}
//
//		return type;
//	}
//
//}
