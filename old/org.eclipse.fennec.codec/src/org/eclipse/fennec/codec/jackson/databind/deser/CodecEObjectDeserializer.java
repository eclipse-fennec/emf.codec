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

import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.SuperTypeInfo;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.jackson.databind.CodecTokenBuffer;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.jackson.utils.CodecParserException;
import org.eclipse.fennec.codec.options.CodecModelInfoOptions;
import org.eclipse.fennec.codec.options.CodecResourceOptions;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * 
 * @author ilenia
 * @since Sep 26, 2024
 */
public class CodecEObjectDeserializer extends ValueDeserializer<EObject> {

	private static final Logger LOGGER = Logger.getLogger(CodecEObjectDeserializer.class.getName());

	private final CodecModule codecModule;
	private final CodecModelInfo codecModelInfoService;

	private CodecInfoHolder infoHolder;
	private EClass type = null;

	public CodecEObjectDeserializer(final Class<?> currentType, final CodecModule codecModule, 
			final CodecModelInfo codecModelInfoService) {
		this.codecModule = codecModule;
		this.codecModelInfoService = codecModelInfoService;
		infoHolder = codecModelInfoService.getCodecInfoHolderByType(InfoType.TYPE);
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueDeserializer#handledType()
	 */
	@Override
	public Class<?> handledType() {
		return EObject.class;
	}

	private EMFCodecReadContext extractCodecContext(JsonParser jp) {
		EMFCodecReadContext codecReadCtxt = null;
		if(jp.streamReadContext() instanceof EMFCodecReadContext crc) {
			codecReadCtxt = crc;
		}
//		If we do not have a EMFCodecReadContext yet it might be we are trying to read the buffer
		if(codecReadCtxt == null && jp instanceof CodecTokenBuffer.Parser ctbp) {
			if(ctbp.streamReadContext() instanceof EMFCodecReadContext crc)
			codecReadCtxt = crc;
		}
		return codecReadCtxt;
	}

	private boolean isRootObject(TokenStreamContext codecContext) {
		if(codecContext.getParent() == null) return true;
		if(codecContext.inObject() && codecContext.getParent().inRoot()) return true; //if we are in a Resource then the root object has as parent the Resource ctxt
		return false;
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

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueDeserializer#deserialize(tools.jackson.core.JsonParser, tools.jackson.databind.DeserializationContext)
	 */
	@Override
	public EObject deserialize(final JsonParser jp, final DeserializationContext ctxt) {

		//		EMFContext.prepare(ctxt);
		EMFCodecReadContext codecReadCtxt = extractCodecContext(jp);
		if(codecReadCtxt == null) {
			throw new IllegalArgumentException(String.format("StreamReadContext is not of type EMFCodecReadContext! Something went wrong!"));
		}

		CodecTokenBuffer buffer = null;
		if(isRootObject((TokenStreamContext)codecReadCtxt) && getCurrentFeature(codecReadCtxt) == null) { //also non contained ref end up here!!
			if(ctxt.getAttribute(CodecResourceOptions.CODEC_ROOT_OBJECT) != null) {
				type  = (EClass) ctxt.getAttribute(CodecResourceOptions.CODEC_ROOT_OBJECT);
				System.out.println("Root object with CODEC_ROOT_OBJECT option!");
				EClassCodecInfo eObjCodecInfo = extractModelInfo(type);
				buffer = determineType(jp, ctxt, eObjCodecInfo.getTypeInfo());
			} else {
				//				We look in the type key (the type key has to be the same for all root objects otherwise we have no way to decide which key to use when)
				System.out.println("Root object but without CODEC_ROOT_OBJECT option!");
//				TODO: can we really end up here???
//				buffer = determineType(jp, ctxt);
//				if(type == null) {
//					throw new IllegalArgumentException(String.format("It was not possible to determine the type of the EObject from the type key %s", eObjCodecInfo.getTypeInfo().getTypeKey()));
//				}
			}
		} else {
			EStructuralFeature currentFeature = getCurrentFeature(codecReadCtxt);
			
			if(currentFeature == null) {
				throw new IllegalArgumentException(String.format("Current Feature is not set in context. Something went wrong!"));
			}
			EClassCodecInfo eObjCodecInfo = extractModelInfo(currentFeature.getEContainingClass());
			FeatureCodecInfo featureCodecInfo = eObjCodecInfo.getReferenceCodecInfo().stream().filter(r -> r.getFeature().getName().equals(currentFeature.getName())).findFirst().orElse(null);
			if(featureCodecInfo == null) {
				throw new IllegalArgumentException(String.format("Cannot retrieve FeatureCodecInfo for current EStructuralFeature %s. Something went wrong!", currentFeature.getName()));
			}
			buffer = determineType(jp, ctxt, featureCodecInfo.getTypeInfo());
			if(type == null) {
				LOGGER.warning(() -> String.format("It was not possible to determine the type of the EReference %s from the type key %s. The type of the EReference will be set to its default type", currentFeature.getName(), featureCodecInfo.getTypeInfo().getTypeKey()));
				type = (EClass) currentFeature.getEGenericType().getERawType();
			}
		}

		
		EObject current = EcoreUtil.create(type);
		codecReadCtxt.setCurrentEObject(current);
		
		if(buffer != null) {
			doDeserialize(buffer.asParser(), ctxt, current, codecReadCtxt);
		} else {
			doDeserialize(jp, ctxt, current, codecReadCtxt);
		}

		return current;
	}
	
	private void doDeserialize(JsonParser jp, DeserializationContext ctxt, EObject current, EMFCodecReadContext codecReadContext) {
		EClassCodecInfo eObjCodecInfo = extractModelInfo(type);
		Resource resource = codecReadContext.getResource();		
		JsonToken nextToken = jp.nextToken();
		while (nextToken != JsonToken.END_OBJECT && nextToken != null) {
			final String field = jp.currentName();
			if(eObjCodecInfo.getIdentityInfo().getIdKey().equals(field)) {
				new IdCodecInfoDeserializer(codecModule, codecModelInfoService, eObjCodecInfo, eObjCodecInfo.getIdentityInfo())
				.deserializeAndSet(jp, current, ctxt, resource);
			}
			FeatureCodecInfo featureCodecInfo = getFeatureCodecInfo(field, eObjCodecInfo);			
			if(featureCodecInfo != null && !(featureCodecInfo instanceof SuperTypeInfo)) {
				codecReadContext.setCurrentEObject(current);
				if(featureCodecInfo.getFeature() instanceof EStructuralFeature) codecReadContext.setCurrentFeature((EStructuralFeature)featureCodecInfo.getFeature());
				new FeatureCodecInfoDeserializer(codecModule, codecModelInfoService, eObjCodecInfo, featureCodecInfo, eObjCodecInfo.getTypeInfo())
				.deserializeAndSet(jp, current, ctxt, resource);
			} else if(featureCodecInfo == null && current != null) {
				handleUnknownProperty(jp, resource, ctxt, current.eClass());
			} 
			nextToken = jp.nextToken();
			if(nextToken == null) {
				nextToken = jp.nextToken();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private CodecTokenBuffer determineType(JsonParser jp, DeserializationContext ctxt, TypeInfo typeInfo) {

		String typeKey = typeInfo.getTypeKey();
		Map<String, String> typeMap = typeInfo.getTypeMap().map();

		// Feature-based discrimination: if typeKey is null or "*", check for property presence
		if(typeKey == null || typeKey.equals(CodecModelInfoOptions.CODEC_TYPE_KEY_FEATURE_BASED)) {
			return determineTypeByFeaturePresence(jp, ctxt, typeInfo, typeMap);
		}

		// Value-based discrimination: existing behavior
		String typeReaderName = typeInfo.getTypeValueReaderName();
		CodecValueReader<String, EClass> typeReader = infoHolder.getReaderByName(typeReaderName);
		String[] typeKeySplit = typeKey.split("\\.");

		int i = 0, l = typeKeySplit.length;
		Integer depth = 1;
		CodecTokenBuffer buffer = CodecTokenBuffer.forBuffering(jp, ctxt);
		JsonToken nextToken = getAndSaveNextToken(jp, buffer);
		depth = updateDepth(nextToken, depth);

		while (depth > 0 && nextToken != null) {
			final String field = jp.currentName();
			if(field != null && field.equals(typeKeySplit[i]) && depth == i + 1) {
				if(l > i + 1) {
					i++;
					nextToken = getAndSaveNextToken(jp, buffer);
					depth = updateDepth(nextToken, depth);
					nextToken = getAndSaveNextToken(jp, buffer);
					depth = updateDepth(nextToken, depth);
					continue;
				} else {
					nextToken = getAndSaveNextToken(jp, buffer);
					depth = updateDepth(nextToken, depth);
					if(typeMap.containsKey(jp.getString())) {
						EClass deserializedType = typeReader.readValue(typeMap.get(jp.getString()), ctxt);
						if(deserializedType == null) {
							LOGGER.severe(String.format("Failed to deserialize type from typeKey %s, with reader type %s from value %s. "
									+ "We will use the default type, if any.", typeKey, typeReaderName, jp.getString()));
						} else {
							type = deserializedType;
						}
					} else {
						LOGGER.warning(String.format("No type mapping for token %s. Trying to directly deserialize token value.", jp.getString()));
						EClass deserializedType = typeReader.readValue(jp.getString(), ctxt);
						if(deserializedType == null) {
							LOGGER.severe(String.format("Failed to deserialize type from typeKey %s, with reader type %s from value %s. "
									+ "We will use the default type, if any.", typeKey, typeReaderName, jp.getString()));
						} else {
							type = deserializedType;
						}
					}
				}
			}
			nextToken = getAndSaveNextToken(jp, buffer);
			depth = updateDepth(nextToken, depth);
		}
		return buffer;
	}

	/**
	 * Feature-based type discrimination: determines type based on which property exists in the JSON object.
	 * Used for JSON Schema oneOf patterns where different variants have different property names.
	 *
	 * @param jp The JSON parser
	 * @param ctxt The deserialization context
	 * @param typeInfo The type information
	 * @param typeMap Map from property name to type identifier
	 * @return A buffer containing all parsed tokens for replay
	 */
	@SuppressWarnings("unchecked")
	private CodecTokenBuffer determineTypeByFeaturePresence(JsonParser jp, DeserializationContext ctxt,
			TypeInfo typeInfo, Map<String, String> typeMap) {

		if(typeMap.isEmpty()) {
			LOGGER.warning("Feature-based type discrimination enabled but typeMap is empty. Using default type.");
			return null;
		}

		String typeReaderName = typeInfo.getTypeValueReaderName();
		CodecValueReader<String, EClass> typeReader = infoHolder.getReaderByName(typeReaderName);

		CodecTokenBuffer buffer = CodecTokenBuffer.forBuffering(jp, ctxt);
		JsonToken nextToken = getAndSaveNextToken(jp, buffer);
		Integer depth = 1;
		depth = updateDepth(nextToken, depth);

		// Scan all field names at depth 1 (top-level properties of the object)
		while (depth > 0 && nextToken != null) {
			final String field = jp.currentName();

			// Check if this field name matches any key in the typeMap
			if(field != null && depth == 1 && typeMap.containsKey(field)) {
				// Found a matching feature - determine the type
				String typeIdentifier = typeMap.get(field);
				EClass deserializedType = typeReader.readValue(typeIdentifier, ctxt);

				if(deserializedType == null) {
					LOGGER.severe(String.format("Failed to deserialize type from feature '%s' mapped to type '%s'. "
							+ "We will continue scanning for other features.", field, typeIdentifier));
				} else {
					type = deserializedType;
					LOGGER.fine(String.format("Determined type %s based on presence of feature '%s'",
							deserializedType.getName(), field));

					// Continue buffering the rest of the tokens but don't change the type
					while (depth > 0 && nextToken != null) {
						nextToken = getAndSaveNextToken(jp, buffer);
						depth = updateDepth(nextToken, depth);
					}
					return buffer;
				}
			}

			nextToken = getAndSaveNextToken(jp, buffer);
			depth = updateDepth(nextToken, depth);
		}

		if(type == null) {
			LOGGER.warning(String.format("Feature-based type discrimination: no matching property found in JSON. "
					+ "Expected one of: %s", String.join(", ", typeMap.keySet())));
		}

		return buffer;
	}
	
	private int updateDepth(JsonToken nextToken, int depth) {
		if (nextToken == JsonToken.START_OBJECT || nextToken == JsonToken.START_ARRAY) {
	        depth = depth + 1;
	    } else if (nextToken == JsonToken.END_OBJECT || nextToken == JsonToken.END_ARRAY) {
	    	depth = depth - 1;
	    }
		return depth;
	}
	
	private JsonToken getAndSaveNextToken(JsonParser jp, CodecTokenBuffer buffer) {
		JsonToken nextToken = jp.nextToken();	
		if(nextToken != null) {
//			System.out.println(nextToken + " -> " + jp.getString());
			buffer.copyCurrentEvent(jp);
			
		} else {
			System.out.println("Null token in getAndSave");
			
		}
		return nextToken;
	}


	private void handleUnknownProperty(final JsonParser jp, final Resource resource, final DeserializationContext ctxt,	EClass currentEClass)  {
		if (resource != null && ctxt.getConfig().hasDeserializationFeatures(FAIL_ON_UNKNOWN_PROPERTIES.getMask())) {
			resource.getErrors().add(new CodecParserException(String.format("Unknown feature '%s' for %s", jp.currentName(), EcoreUtil.getURI(currentEClass)),jp.currentLocation()));
		}
		// we didn't find a feature so consume
		// the field and move on
		jp.nextToken();
		jp.skipChildren();
	}

	private FeatureCodecInfo getFeatureCodecInfo(String fieldName, EClassCodecInfo eObjCodecInfo) {
		if(fieldName == null) return null;
//		if(fieldName.equals(codecModule.getIdKey())) return eObjCodecInfo.getIdentityInfo();
//		if(fieldName.equals(codecModule.getTypeKey()) && !codecModule.isDeserializeType()) return eObjCodecInfo.getTypeInfo();
		if(fieldName.equals(codecModule.getSuperTypeKey())) return eObjCodecInfo.getSuperTypeInfo();
		for(FeatureCodecInfo featureCodecInfo : eObjCodecInfo.getFeatureInfo()) {
			String key = codecModule.isUseNamesFromExtendedMetaData() ? featureCodecInfo.getKey() : featureCodecInfo.getFeature().getName();
			if(fieldName.equals(key)) {
				return featureCodecInfo;
			}
		}
		LOGGER.warning(() -> "No CodecInfo found for field " + fieldName + " in " + eObjCodecInfo.getId());
		return null;
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
				parentCodecReadCtxt = ctxt.getParent().getParent();
			}
		}
		if(parentCodecReadCtxt != null) {
			EReference reference = (EReference) (((EMFCodecReadContext) parentCodecReadCtxt).getCurrentFeature());
			return reference;

		}
		return null;
	}

}
