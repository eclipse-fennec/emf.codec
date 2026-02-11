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
package org.eclipse.fennec.codec.jackson.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.CodecProxyFactory;
import org.eclipse.fennec.codec.configurator.ObjectMapperBuilderFactory;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.info.codecinfo.TypeMapStrategyType;
import org.eclipse.fennec.codec.info.codecinfo.TypedCodecInfo;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.options.CodecModelInfoOptions;
import org.eclipse.fennec.codec.options.CodecModuleOptions;
import org.eclipse.fennec.codec.options.CodecResourceOptions;
import org.eclipse.fennec.codec.options.CodecValueReaderConstants;
import org.eclipse.fennec.codec.options.CodecValueWriterConstants;
import org.eclipse.fennec.codec.options.ObjectMapperOptions;

import tools.jackson.core.FormatFeature;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.cfg.ContextAttributes;
import tools.jackson.databind.json.JsonMapper.Builder;

/**
 * Codec specific Resource, where we overwrite the CodecModule, ObjectMapper and CodecModelInfo options, 
 * based on what the user passes as options when saving/loading a resource
 * 
 * @author ilenia
 * @since Aug 12, 2024
 */
public class CodecResource extends ResourceImpl {

	private static final Logger LOGGER = Logger.getLogger(CodecResource.class.getName());


	protected CodecModelInfo modelInfoService;
	protected Builder objMapperBuilder;
	protected CodecModule.Builder moduleBuilder;
	protected ObjectMapper mapper;


	private final ObjectMapperBuilderFactory objMapperBuilderFactory;

	public CodecResource(URI uri, CodecModelInfo modelInfoService, 
			CodecModule.Builder moduleBuilder, ObjectMapperBuilderFactory objMapperBuilderFactory) {
		super(uri);
		this.modelInfoService = modelInfoService;
		this.objMapperBuilderFactory = objMapperBuilderFactory;
		this.moduleBuilder = moduleBuilder.bindCodecModelInfoService(modelInfoService);
	}
	
	public ObjectMapper getMapper() {
		return mapper;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#save(java.util.Map)
	 */
	@Override
	public void save(Map<?, ?> options) throws IOException {
//		Object saveOnlyIfChanged = options != null && options.containsKey(OPTION_SAVE_ONLY_IF_CHANGED)
//				? options.get(OPTION_SAVE_ONLY_IF_CHANGED)
//				: defaultSaveOptions != null ? defaultSaveOptions.get(OPTION_SAVE_ONLY_IF_CHANGED) : null;
		try {
			super.save(options);
		} catch (Exception e) {
			if (needOutputstream()) {
				throw e;
			} else {
				Map<?, ?> response = options == null ? null : (Map<?, ?>) options.get(URIConverter.OPTION_RESPONSE);
				if (response == null) {
					response = new HashMap<>();
				}
				try {
					doSave(null, options);
				} finally {
					handleSaveResponse(response, options);
				}
			}
		}
	}

	protected boolean needOutputstream() {
		return true;
	}
	
	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		EObject eObject = this.getContents().isEmpty() ? null : this.getContents().get(0);
		if(eObject == null) {
			LOGGER.severe(String.format("No content for Resource %s", this.getURI()));
			return;
		}

		PackageCodecInfo modelCodecInfo = modelInfoService.getCodecInfoForPackage(eObject.eClass().getEPackage()).get();	

		if(modelCodecInfo == null) {
			LOGGER.severe(String.format("No PackageCodecInfo associated with EObject %s has been found", eObject.eClass().getName()));
			return;
		}

		if(options == null) options = Collections.emptyMap();
		objMapperBuilder = objMapperBuilderFactory.createObjectMapperBuilder();

		//		Handle global ignore features list - extract feature names and pass to module
		if(options.containsKey(CodecModelInfoOptions.CODEC_GLOBAL_IGNORE_FEATURES_LIST)) {
			@SuppressWarnings("unchecked")
			List<EStructuralFeature> globalIgnoreFeatures = (List<EStructuralFeature>) options.get(CodecModelInfoOptions.CODEC_GLOBAL_IGNORE_FEATURES_LIST);
			List<String> featureNames = globalIgnoreFeatures.stream().map(f -> f.getName()).toList();
			moduleBuilder.withGlobalIgnoreFeatureNames(featureNames);
		}

		//		Update the CodecModule based on the passed options
		updateCodecModuleFromOptions(options);

		//		Update ObjectMapper based on the passed options
		updateMapperFromOptions(options);

//		Update the CodecModelInfo based on the passed options
		updateCodecModelInfoFromOptions(modelCodecInfo, options);
		
//		Check alphabetic order property (it's important to do it after the updateMapperFromOptions has been called, because 
//		this property may be overwritten by the options)
		moduleBuilder.withSortPropertiesAlphabetically(objMapperBuilder.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));

//		Bind the CodecModelInfo to the CodecModule.
//		This is necessary otherwise asking the ModelInfoService we would get a new instance every time
//		instead we need the same one here since it's the one we updated based on the options
		moduleBuilder.bindCodecModelInfo(modelCodecInfo);
		
//		Register the module with the mapper		
		mapper = objMapperBuilder.addModule(moduleBuilder.build()).build();
	}
	
	@Override
	public void load(Map<?, ?> options) throws IOException {
		try {
			super.load(options);
		} catch (Exception e) {
			if (needOutputstream()) {
				throw e;
			} else {
				Map<?, ?> response = options == null ? null : (Map<?, ?>) options.get(URIConverter.OPTION_RESPONSE);
				if (response == null) {
					response = new HashMap<>();
				}
				try {
					doLoad(null, options);
				} finally {
					handleLoadResponse(response, options);
				}
			}
		}
	}
	
	@Override
	public void delete(Map<?, ?> options) throws IOException {
		try {
			super.delete(options);
		} catch (Exception e) {
			ResourceSet resourceSet = getResourceSet();
			if (resourceSet != null) {
				resourceSet.getResources().remove(this);
			}
		}
	}
	
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		
		if (options == null) {
			options = Collections.<String, Object> emptyMap();
		}
		objMapperBuilder = objMapperBuilderFactory.createObjectMapperBuilder();
		EClass eClass = null;
		
//		We need to know which object has to be deserialized, otherwise we cannot access the right model info at this stage
		if(options.containsKey(CodecResourceOptions.CODEC_ROOT_OBJECT)) {
			eClass = (EClass) options.get(CodecResourceOptions.CODEC_ROOT_OBJECT);
		} else {
			throw new IllegalArgumentException(String.format("No CODEC_ROOT_OBJECT option found. Cannot continue without knowing the root object to deserialize!"));
		}
		
		PackageCodecInfo modelCodecInfo = null;
		if(eClass != null) {
			modelCodecInfo = modelInfoService.getCodecInfoForPackage(eClass.getEPackage()).get();	
			if(modelCodecInfo == null) {
				LOGGER.warning(String.format("No PackageCodecInfo associated with EClass %s has been found", eClass.getName()));
			}
		} else {
			LOGGER.warning(String.format("Not able to retrieve EClass from options ROOT_OBJECT"));
		}
		
		try {
			updateCodecModuleFromOptions(options);

			//		Update ObjectMapper based on the passed options
			updateMapperFromOptions(options);

			if(modelCodecInfo != null) {
//				Update the CodecModelInfo based on the passed options
				updateCodecModelInfoFromOptions(modelCodecInfo, options);
				
//				Bind the CodecModelInfo to the CodecModule.
//				This is necessary otherwise asking the ModelInfoService we would get a new instance every time
//				instead we need the same one here since it's the one we updated based on the options
				moduleBuilder.bindCodecModelInfo(modelCodecInfo);
			}
//			

//			Register the module with the mapper
			mapper = objMapperBuilder.addModule(moduleBuilder.build()).build();
			
		} catch(Exception e) {
			throw e;
		}
		
//		Update the CodecModule based on the passed options
		
	}
	
	
	/**
	 * This is responsible to update the EClassCodecInfo based on the options used to save/load the resource
	 *
	 * @param options the options passed in the {@link Resource} save/load methods.
	 */
	@SuppressWarnings("unchecked")
	private void updateCodecModelInfoFromOptions(EClassCodecInfo codecInfo, Map<?, ?> options) {
		if(options.containsKey(CodecModelInfoOptions.CODEC_ID_STRATEGY)) {
			codecInfo.getIdentityInfo().setIdStrategy((String) options.get(CodecModelInfoOptions.CODEC_ID_STRATEGY));
			if("ID_FIELD".equals(codecInfo.getIdentityInfo().getIdStrategy())) {
				codecInfo.getIdentityInfo().getIdFeatures().clear();
				if(codecInfo.getClassifier() instanceof EClass ec) {
					codecInfo.getIdentityInfo().getIdFeatures().add(ec.getEIDAttribute());
				}
			}
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_ID_KEY)) {
			codecInfo.getIdentityInfo().setIdKey((String) options.get(CodecModelInfoOptions.CODEC_ID_KEY));
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_ID_SEPARATOR)) {
			codecInfo.getIdentityInfo().setIdSeparator((String) options.get(CodecModelInfoOptions.CODEC_ID_SEPARATOR));
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_ID_FEATURES_LIST)) {
			List<EStructuralFeature> idFeatures = (List<EStructuralFeature>) options.get(CodecModelInfoOptions.CODEC_ID_FEATURES_LIST);
			codecInfo.getIdentityInfo().getIdFeatures().clear();
			codecInfo.getIdentityInfo().getIdFeatures().addAll(idFeatures);
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_ID_VALUE_READER)) {
			CodecValueReader<?,?> reader = (CodecValueReader<?,?>) options.get(CodecModelInfoOptions.CODEC_ID_VALUE_READER);
			codecInfo.getIdentityInfo().setIdValueReaderName(reader.getName());
			modelInfoService.addCodecValueReaderForType(InfoType.IDENTITY, reader);
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_ID_VALUE_WRITER)) {
			CodecValueWriter<?,?> writer = (CodecValueWriter<?,?>) options.get(CodecModelInfoOptions.CODEC_ID_VALUE_WRITER);
			codecInfo.getIdentityInfo().setIdValueWriterName(writer.getName());
			modelInfoService.addCodecValueWriterForType(InfoType.IDENTITY, writer);
		}
		
		
		if(options.containsKey(CodecModelInfoOptions.CODEC_IGNORE_FEATURES_LIST)) {
			List<EStructuralFeature> ignoreFeatures = (List<EStructuralFeature>) options.get(CodecModelInfoOptions.CODEC_IGNORE_FEATURES_LIST);
			ignoreFeatures.forEach(ignoreFeature -> {
				FeatureCodecInfo fci = codecInfo.getFeatureInfo().stream().filter(featureInfo -> ignoreFeature.equals(featureInfo.getFeature())).findFirst().get();
				if(fci != null) fci.setIgnore(true);
			});
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_IGNORE_NOT_FEATURES_LIST)) {
			List<EStructuralFeature> ignoreFeatures = (List<EStructuralFeature>) options.get(CodecModelInfoOptions.CODEC_IGNORE_NOT_FEATURES_LIST);
			ignoreFeatures.forEach(ignoreFeature -> {
				FeatureCodecInfo fci = codecInfo.getFeatureInfo().stream().filter(featureInfo -> ignoreFeature.equals(featureInfo.getFeature())).findFirst().get();
				if(fci != null) fci.setIgnore(false);
			});
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_VALUE_READERS_MAP)) {
			Map<ETypedElement, CodecValueReader<?,?>> readersMap = (Map<ETypedElement, CodecValueReader<?,?>>)options.get(CodecModelInfoOptions.CODEC_VALUE_READERS_MAP);
			readersMap.forEach((element, reader) -> {
				if(element instanceof EAttribute) {
					FeatureCodecInfo fci = codecInfo.getAttributeCodecInfo().stream().filter(featureInfo -> featureInfo.getFeature().equals(element)).findFirst().get();
					if(fci != null) {
						fci.setValueReaderName(reader.getName());
						modelInfoService.addCodecValueReaderForType(InfoType.ATTRIBUTE, reader);
					}
				}
				else if(element instanceof EReference) {
					FeatureCodecInfo fci = codecInfo.getReferenceCodecInfo().stream().filter(featureInfo -> featureInfo.getFeature().equals(element)).findFirst().get();
					if(fci != null) {
						fci.setValueReaderName(reader.getName());
						modelInfoService.addCodecValueReaderForType(InfoType.REFERENCE, reader);
					}
				}
				else if(element instanceof EOperation) {
					FeatureCodecInfo fci = codecInfo.getOperationCodecInfo().stream().filter(featureInfo -> featureInfo.getFeature().equals(element)).findFirst().get();
					if(fci != null) {
						fci.setValueReaderName(reader.getName());
						modelInfoService.addCodecValueReaderForType(InfoType.OPERATION, reader);
					}
				}
			});
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_VALUE_WRITERS_MAP)) {
			Map<ETypedElement, CodecValueWriter<?,?>> writersMap = (Map<ETypedElement, CodecValueWriter<?,?>>)options.get(CodecModelInfoOptions.CODEC_VALUE_WRITERS_MAP);
			writersMap.forEach((element, writer) -> {
				if(element instanceof EAttribute) {
					FeatureCodecInfo fci = codecInfo.getAttributeCodecInfo().stream().filter(featureInfo -> featureInfo.getFeature().equals(element)).findFirst().get();
					if(fci != null) {
						fci.setValueWriterName(writer.getName());
						modelInfoService.addCodecValueWriterForType(InfoType.ATTRIBUTE, writer);
					}
				}
				else if(element instanceof EReference) {
					FeatureCodecInfo fci = codecInfo.getReferenceCodecInfo().stream().filter(featureInfo -> featureInfo.getFeature().equals(element)).findFirst().get();
					if(fci != null) {
						fci.setValueWriterName(writer.getName());
						modelInfoService.addCodecValueWriterForType(InfoType.REFERENCE, writer);
					}
				}
				else if(element instanceof EOperation) {
					FeatureCodecInfo fci = codecInfo.getOperationCodecInfo().stream().filter(featureInfo -> featureInfo.getFeature().equals(element)).findFirst().get();
					if(fci != null) {
						fci.setValueWriterName(writer.getName());
						modelInfoService.addCodecValueWriterForType(InfoType.OPERATION, writer);
					}
				}
			});
		}	
		
		if(options.containsKey(CodecModelInfoOptions.CODEC_EXTRAS)) {
			Map<String, String> extras = (Map<String, String>) options.get(CodecModelInfoOptions.CODEC_EXTRAS);
			codecInfo.getCodecExtraProperties().putAll(extras);
		}
		
//		Update the TypeInfo part
		updateCodecModelInfoFromOptions((TypedCodecInfo) codecInfo, options);
		
//		Look for options specific to EReference of the EClass
		if(options.containsKey(CodecResourceOptions.CODEC_OPTIONS)) {
			Map<EReference, Map<String, Object>> codecOptions = (Map<EReference, Map<String, Object>>) options.get(CodecResourceOptions.CODEC_OPTIONS);
			codecOptions.forEach((ec, opt) -> {
				FeatureCodecInfo featureCodecInfo = codecInfo.getReferenceCodecInfo().stream().filter(eci -> eci.getFeature().getName().equals(ec.getName())).findFirst().orElse(null);
				if(featureCodecInfo == null) {
					LOGGER.severe(String.format("No FeatureCodecInfo associated with EReference %s has been found", ec.getName()));
				}
				else {
//					Update the TypeInfo part
					updateCodecModelInfoFromOptions(featureCodecInfo, opt);
					if(opt.containsKey(CodecModelInfoOptions.CODEC_EXTRAS)) {
						Map<String, String> extras = (Map<String, String>) opt.get(CodecModelInfoOptions.CODEC_EXTRAS);
						featureCodecInfo.getCodecExtraProperties().putAll(extras);
					}
				}
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateCodecModelInfoFromOptions(TypedCodecInfo codecInfo, Map<?, ?> options) {
		if(options.containsKey(CodecModelInfoOptions.CODEC_TYPE_STRATEGY)) {
			String typeUse = (String) options.get(CodecModelInfoOptions.CODEC_TYPE_STRATEGY);
			switch(typeUse) {
			case "NAME":
				codecInfo.getTypeInfo().setTypeValueWriterName(CodecValueWriterConstants.WRITER_BY_ECLASS_NAME);
				codecInfo.getTypeInfo().setTypeValueReaderName(CodecValueReaderConstants.READER_BY_ECLASS_NAME);
				codecInfo.getTypeInfo().setTypeStrategy(typeUse);
				break;
			case "CLASS":
				codecInfo.getTypeInfo().setTypeValueWriterName(CodecValueWriterConstants.WRITER_BY_INSTANCE_CLASS_NAME);
				codecInfo.getTypeInfo().setTypeValueReaderName(CodecValueReaderConstants.READER_BY_INSTANCE_CLASS_NAME);
				codecInfo.getTypeInfo().setTypeStrategy(typeUse);
				break;
			case "URI": 
				codecInfo.getTypeInfo().setTypeValueWriterName(CodecValueWriterConstants.URI_WRITER);
				codecInfo.getTypeInfo().setTypeValueReaderName(CodecValueReaderConstants.URI_READER);
				codecInfo.getTypeInfo().setTypeStrategy(typeUse);
				break;	
			default:
				LOGGER.warning(String.format("No Reader/Writer available for type use %s. Keeping the default ones.", typeUse));
			}			
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_TYPE_VALUE_READER)) {
			CodecValueReader<?,?> reader = (CodecValueReader<?,?>) options.get(CodecModelInfoOptions.CODEC_TYPE_VALUE_READER);
			codecInfo.getTypeInfo().setTypeValueReaderName(reader.getName());
			modelInfoService.addCodecValueReaderForType(InfoType.TYPE, reader);
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_TYPE_VALUE_WRITER)) {
			CodecValueWriter<?,?> writer = (CodecValueWriter<?,?>) options.get(CodecModelInfoOptions.CODEC_TYPE_VALUE_WRITER);
			codecInfo.getTypeInfo().setTypeValueWriterName(writer.getName());
			modelInfoService.addCodecValueWriterForType(InfoType.TYPE, writer);
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_TYPE_INCLUDE)) {
			codecInfo.getTypeInfo().setIgnoreType(!((Boolean) options.get(CodecModelInfoOptions.CODEC_TYPE_INCLUDE)));
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_TYPE_KEY)) {
			codecInfo.getTypeInfo().setTypeKey((String) options.get(CodecModelInfoOptions.CODEC_TYPE_KEY));
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_TYPE_MAP_STRATEGY)) {
			Object mapStrategy = options.get(CodecModelInfoOptions.CODEC_TYPE_MAP_STRATEGY);
			if(mapStrategy instanceof TypeMapStrategyType mapStrategyType) {
				codecInfo.getTypeInfo().setTypeMapStrategy(mapStrategyType); 
			} else if(mapStrategy instanceof String mapStrategyStr) {
				codecInfo.getTypeInfo().setTypeMapStrategy(TypeMapStrategyType.valueOf(mapStrategyStr));
			} else {
				LOGGER.warning(String.format("Option CodecModelInfoOptions.CODEC_TYPE_MAP_STRATEGY must be either a String or a TypeMapStrategyType. Instead it is %s", mapStrategy.getClass().getName()));
			}
		}
		
		if(options.containsKey(CodecModelInfoOptions.CODEC_TYPE_MAP)) {
			Map<String, String> typeMap = (Map<String, String>) options.get(CodecModelInfoOptions.CODEC_TYPE_MAP);
			if(TypeMapStrategyType.OVERWRITE.equals(codecInfo.getTypeInfo().getTypeMapStrategy())) {
				codecInfo.getTypeInfo().getTypeMap().clear(); //overwrite what we have in the annotation
			}			
			codecInfo.getTypeInfo().getTypeMap().putAll(typeMap);
		}
		if(options.containsKey(CodecModelInfoOptions.CODEC_TYPE_INFO)) {
			TypeInfo typeInfo = (TypeInfo) options.get(CodecModelInfoOptions.CODEC_TYPE_INFO);
			codecInfo.setTypeInfo(typeInfo);
		}

		// Handle inherits.from.parent option for EReferences
		// This controls whether the reference inherits codec.type from its target EClass
		if(options.containsKey(CodecModelInfoOptions.CODEC_TYPE_INHERITS_FROM_PARENT)) {
			Boolean inheritsFromParent = (Boolean) options.get(CodecModelInfoOptions.CODEC_TYPE_INHERITS_FROM_PARENT);

			// Only relevant for FeatureCodecInfo (EReferences)
			if(codecInfo instanceof FeatureCodecInfo featureInfo &&
			   featureInfo.getFeature() instanceof EReference reference) {

				if(!inheritsFromParent) {
					// Inheritance is disabled - we need to rebuild TypeInfo from only the reference's own annotations
					// Get the reference's own codec.type annotation (without inheritance)
					EAnnotation codecTypeAnnotation = reference.getEAnnotation("codec.type");

					if(codecTypeAnnotation != null) {
						// Clear inherited type mappings and rebuild from reference's annotation only
						codecInfo.getTypeInfo().getTypeMap().clear();
						codecTypeAnnotation.getDetails().entrySet().forEach(entry -> {
							// Skip the control keys, add only the type mappings
							if(!entry.getKey().equals("include") && !entry.getKey().equals("strategy") &&
							   !entry.getKey().equals("typeKey") && !entry.getKey().equals(CodecModelInfoOptions.CODEC_TYPE_INHERITS_FROM_PARENT)) {
								codecInfo.getTypeInfo().getTypeMap().put(entry.getKey(), entry.getValue());
							}
						});
					} else {
						// No annotation on reference, so clear all inherited mappings
						codecInfo.getTypeInfo().getTypeMap().clear();
					}
				}
				// Note: If inheritsFromParent==true, the inheritance was already applied during
				// CodecModelInfo creation, so we don't need to do anything
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateCodecModelInfoFromOptions(PackageCodecInfo codecModelInfo, Map<?, ?> options) {

		if(options.containsKey(CodecResourceOptions.CODEC_OPTIONS)) {
			Map<EClass, Map<String, Object>> codecOptions = (Map<EClass, Map<String, Object>>) options.get(CodecResourceOptions.CODEC_OPTIONS);
			codecOptions.forEach((ec, opt) -> {
				EClassCodecInfo eClassCodecInfo = codecModelInfo.getEClassCodecInfo().stream().filter(eci -> eci.getClassifier().getName().equals(ec.getName())).findFirst().orElse(null);
				if(eClassCodecInfo == null) {
					LOGGER.severe(String.format("No EClassCodecInfo associated with EClass %s has been found", ec.eClass().getName()));
				}
				else {
					updateCodecModelInfoFromOptions(eClassCodecInfo, opt);
				}
			});
		}
	}

	/**
	 * This updates the {@link CodecModule} based on the options passed when saving/loading the resource
	 * @param options the options passed in the {@link Resource} save/load methods.
	 */
	@SuppressWarnings("unchecked")
	private void updateCodecModuleFromOptions(Map<?, ?> options) {
		options.forEach((k,v) -> {
			switch((String)k) {
			case CodecModuleOptions.CODEC_MODULE_ID_FEATURE_AS_PRIMARY_KEY:
				moduleBuilder.withIdFeatureAsPrimaryKey((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_ID_ON_TOP:
				moduleBuilder.withIdOnTop((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_PROXY_KEY:
				moduleBuilder.withProxyKey((String) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_REFERENCE_KEY:
				moduleBuilder.withRefKey((String) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_SERIALIZE_DEFAULT_VALUE:
				moduleBuilder.withSerializeDefaultValue((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_SERIALIZE_EMPTY_VALUE:
				moduleBuilder.withSerializeEmptyValue((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_SERIALIZE_NULL_VALUE:
				moduleBuilder.withSerializeNullValue((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_SERIALIZE_ID_FIELD:
				moduleBuilder.withSerializeIdField((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_SERIALIZE_SUPER_TYPES:
				moduleBuilder.withSerializeSuperTypes((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_SERIALIZE_ALL_SUPER_TYPES:
				moduleBuilder.withSerializeAllSuperTypes((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_SERIALIZE_SUPER_TYPES_AS_ARRAY:
				moduleBuilder.withSerailizeSuperTypesAsArray((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_SERIALIZE_TYPE:
				moduleBuilder.withSerializeType((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_DESERIALIZE_TYPE:
				moduleBuilder.withDeserializeType((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_TIMESTAMP_KEY:
				moduleBuilder.withTimestampKey((String) v);
				break;
//			case CodecModuleOptions.CODEC_MODULE_TYPE_KEY:
//				moduleBuilder.withTypeKey((String) v);
//				break;
			case CodecModuleOptions.CODEC_MODULE_SUPERTYPE_KEY:
				moduleBuilder.withSuperTypeKey((String) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_USE_ID:
				moduleBuilder.withUseId((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA:
				moduleBuilder.withUseNamesFromExtendedMetaData((boolean) v);
				break;			
			case CodecModuleOptions.CODEC_MODULE_WRITE_ENUM_LITERAL:
				moduleBuilder.withWriteEnumLiterals((boolean) v);
				break;
			case CodecModuleOptions.CODEC_MODULE_REFERENCE_DESERIALIZER:
				if(v instanceof ValueDeserializer) {
					moduleBuilder.bindReferenceDeserializer((ValueDeserializer<EObject> ) v);
				} else {
					LOGGER.warning(() -> CodecModuleOptions.CODEC_MODULE_REFERENCE_DESERIALIZER +" must be an instance of JsonDeserializer for.");
				}
				break;
			case CodecModuleOptions.CODEC_PROXY_FACTORY:
				if(v instanceof CodecProxyFactory pf) {
					moduleBuilder.bindCodecProxyFactory(pf);
				} else {
					LOGGER.warning(() -> CodecModuleOptions.CODEC_PROXY_FACTORY +" must be an instance of CodecProxyFactory for.");
				}
				break;
			}
		});
	}

	/**
	 * Here the {@link CodecObjectMapper} can be configured based on the options passed with the {@link Resource} save/load methods.
	 * Only Serialization/Deserialization features should be configurable at this point. 
	 * Also GeneratorFeatures and ParserFeature can be overwritten here. 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void updateMapperFromOptions(Map<?, ?> options) {

		DateFormat dateFormat =  options.containsKey(ObjectMapperOptions.OBJ_MAPPER_DATE_FORMAT) ? (DateFormat) options.get(ObjectMapperOptions.OBJ_MAPPER_DATE_FORMAT) : null;
		if(dateFormat != null) objMapperBuilder = objMapperBuilder.defaultDateFormat(dateFormat);

		Locale locale = options.containsKey(ObjectMapperOptions.OBJ_MAPPER_LOCALE) ? (Locale) options.get(ObjectMapperOptions.OBJ_MAPPER_LOCALE) : null;
		if(locale != null) objMapperBuilder = objMapperBuilder.defaultLocale(locale);

		TimeZone timeZone = options.containsKey(ObjectMapperOptions.OBJ_MAPPER_TIME_ZONE) ? (TimeZone) options.get(ObjectMapperOptions.OBJ_MAPPER_TIME_ZONE) : null;
		if(timeZone != null) objMapperBuilder = objMapperBuilder.defaultTimeZone(timeZone);

		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_SERIALIZATION_FEATURES_WITH)) {
			List<SerializationFeature> serFeatureWith = (List<SerializationFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_SERIALIZATION_FEATURES_WITH);
			serFeatureWith.forEach(sf -> objMapperBuilder.enable(sf));
		}
		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_SERIALIZATION_FEATURES_WITHOUT)) {
			List<SerializationFeature> serFeatureWithout = (List<SerializationFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_SERIALIZATION_FEATURES_WITHOUT);
			serFeatureWithout.forEach(sf -> objMapperBuilder.disable(sf));
		}
		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_DESERIALIZATION_FEATURES_WITH)) {
			List<DeserializationFeature> deserFeatureWith = (List<DeserializationFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_DESERIALIZATION_FEATURES_WITH);
			deserFeatureWith.forEach(sf -> objMapperBuilder.enable(sf));
		}
		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_DESERIALIZATION_FEATURES_WITHOUT)) {
			List<DeserializationFeature> deserFeatureWithout = (List<DeserializationFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_DESERIALIZATION_FEATURES_WITHOUT);
			deserFeatureWithout.forEach(sf -> objMapperBuilder.disable(sf));
		}
		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_FEATURES_WITH)) {
			List<MapperFeature> featureWith = (List<MapperFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_FEATURES_WITH);
			featureWith.forEach(sf -> objMapperBuilder.enable(sf));
		}
		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_FEATURES_WITHOUT)) {
			List<MapperFeature> featureWithout = (List<MapperFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_FEATURES_WITHOUT);
			featureWithout.forEach(sf -> objMapperBuilder.disable(sf));
		}
		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_FORMAT_SER_FEATURES_WITHOUT)) {
			List<FormatFeature> featureWithout = (List<FormatFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_FORMAT_SER_FEATURES_WITHOUT);			
			featureWithout.forEach(sf -> objMapperBuilder.disable((JsonWriteFeature) sf));
		}
		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_FORMAT_SER_FEATURES_WITH)) {
			List<FormatFeature> featureWithout = (List<FormatFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_FORMAT_SER_FEATURES_WITH);			
			featureWithout.forEach(sf -> objMapperBuilder.enable((JsonWriteFeature) sf));
		}
		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_FORMAT_DESER_FEATURES_WITHOUT)) {
			List<FormatFeature> featureWithout = (List<FormatFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_FORMAT_DESER_FEATURES_WITHOUT);			
			featureWithout.forEach(sf -> objMapperBuilder.disable((JsonReadFeature) sf));
		}
		if(options.containsKey(ObjectMapperOptions.OBJ_MAPPER_FORMAT_DESER_FEATURES_WITH)) {
			List<FormatFeature> featureWithout = (List<FormatFeature>) options.get(ObjectMapperOptions.OBJ_MAPPER_FORMAT_DESER_FEATURES_WITH);			
			featureWithout.forEach(sf -> objMapperBuilder.enable((JsonReadFeature) sf));
		}
	}
	
	protected static ContextAttributes from(final Map<?, ?> options) {
	      return ContextAttributes
	         .getEmpty()
	         .withSharedAttributes(options == null ? new HashMap<>() : new HashMap<>(options));
	   }
}
