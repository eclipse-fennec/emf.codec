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
package org.eclipse.fennec.codec.info.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoFactory;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.IdentityInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.SuperTypeInfo;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.info.helper.CodecInfoHolderHelper;
import org.eclipse.fennec.codec.options.CodecAnnotations;
import org.eclipse.fennec.codec.options.CodecModelInfoOptions;
import org.eclipse.fennec.codec.options.CodecValueReaderConstants;
import org.eclipse.fennec.codec.options.CodecValueWriterConstants;
import org.gecko.emf.osgi.configurator.EPackageConfigurator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate = true, name = "CodecModelInfoService", service = CodecModelInfo.class)
public class CodecModelInfoImpl extends HashMap<String, Object> implements CodecModelInfo {

	private CodecInfoHolderHelper codecInfoHolderHelper;

	/** serialVersionUID */
	private static final long serialVersionUID = 7749336016374647599L;
	private static final Logger LOGGER = Logger.getLogger(CodecModelInfoImpl.class.getName());

	private transient List<EPackageConfigurator> list = new ArrayList<>();
	private transient List<EPackage> packageList = new ArrayList<>();

	private Map<String, PackageCodecInfo> ePackageCodecInfoMap = new ConcurrentHashMap<>();
	private Map<InfoType, CodecInfoHolder> codecInfoHolderMap = new ConcurrentHashMap<>();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	@Activate
	public void activate() {
		PackageCodecInfo packageInfo = doCreatePackageCodecInfo(EcorePackage.eINSTANCE);
		ePackageCodecInfoMap.put(EcorePackage.eINSTANCE.getNsURI(), packageInfo);
		createCodecInfoHolderMap();
	}

//	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void bindEPackageConfigurator(EPackageConfigurator configurator) {
		lock.writeLock().lock();
		try {
			list.add(configurator);
			refresh();
		} finally {
			lock.writeLock().unlock();
		}
	}
	

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void bindEPackage(EPackage ePackage) {
		lock.writeLock().lock();
		try {
			packageList.add(ePackage);
			createPackageCodecInfo(ePackage);
		} finally {
			lock.writeLock().unlock();
		}
	}

	private synchronized void refresh() {
		list.forEach(c -> c.configureEPackage(this));
	}
	
	public void unbindEPackageConfigurator(EPackageConfigurator configurator) {
		lock.writeLock().lock();
		try {
			list.remove(configurator);
			configurator.unconfigureEPackage(this);
			refresh();
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void unbindEPackage(EPackage ePackage) {
		lock.writeLock().lock();
		try {
			packageList.remove(ePackage);
			ePackageCodecInfoMap.remove(ePackage.getNsURI());
			refresh();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Reference
	public void setCodecInfoHolderHelper(CodecInfoHolderHelper codecInfoHolderHelper) {
		this.codecInfoHolderHelper = codecInfoHolderHelper;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(String uri, Object value) {
		if (value instanceof EPackage) {
			EPackage ePackage = (EPackage) value;
			return createPackageCodecInfo(ePackage);
		}
		return null;
	}

	private synchronized PackageCodecInfo createPackageCodecInfo(EPackage ePackage) {

		PackageCodecInfo packageInfo = doCreatePackageCodecInfo(ePackage);
		return ePackageCodecInfoMap.put(ePackage.getNsURI(), packageInfo);
	}

	@Override
	public Object remove(Object key) {
		ePackageCodecInfoMap.remove(key);
		return super.remove(key);
	}

	private PackageCodecInfo doCreatePackageCodecInfo(EPackage ePackage) {
		PackageCodecInfo ePackageCodecInfo = CodecInfoFactory.eINSTANCE.createPackageCodecInfo();
		ePackageCodecInfo.setId(ePackage.getNsURI());
		ePackageCodecInfo.setEPackage(ePackage);
		for(EPackage subPackage : ePackage.getESubpackages()) {
			ePackageCodecInfo.getSubPackageCodecInfo().add(doCreatePackageCodecInfo(subPackage));
		}
		ePackage.getEClassifiers().stream().forEach(ec -> {
			ePackageCodecInfo.getEClassCodecInfo().add(createCodecEClassInfo(ec));
		});
	
		return ePackageCodecInfo;
	}

	private EClassCodecInfo createCodecEClassInfo(EClassifier ec) {
		EClassCodecInfo eClassCodecInfo = CodecInfoFactory.eINSTANCE.createEClassCodecInfo();
		eClassCodecInfo.setId(EcoreUtil.getURI(ec).toString());
		eClassCodecInfo.setClassifier(ec);


		Map<String, String> extras = getAnnotationDetailsMap(ec, "codec.extras", true);
		if(!extras.isEmpty()) eClassCodecInfo.getCodecExtraProperties().putAll(extras);

		if(ec instanceof EClass eClass) {
						
			eClass.getEAllAttributes().forEach(att -> 
				eClassCodecInfo.getFeatureInfo().add(createCodecFeatureInfo(att)));
			eClass.getEAllReferences().forEach(ref -> 				
				eClassCodecInfo.getFeatureInfo().add(createCodecFeatureInfo(ref)));
			eClass.getEAllOperations().forEach(op -> {
				eClassCodecInfo.getFeatureInfo().add(createCodecFeatureInfo(op));
			});
									
//			add IdentityInfo based on EAnnotation
			eClassCodecInfo.setIdentityInfo(getIdentityInfo(eClass));
		};	
		
//		Add TypeInfo based on EAnnotation
		eClassCodecInfo.setTypeInfo(getTypeInfo(ec));		
		


		//		This is not really used at the moment.
		//		It's just pre set for maybe future needs to customize the supertype serialization process
		SuperTypeInfo superTypeInfo = CodecInfoFactory.eINSTANCE.createSuperTypeInfo();
		superTypeInfo.setId(UUID.randomUUID().toString());
		superTypeInfo.setType(InfoType.SUPER_TYPE);		
		String supertypeValue = getAnnotationDetails(ec, "codec.supertype", "include", true);
		if(supertypeValue != null && "false".equalsIgnoreCase(supertypeValue)) {
			superTypeInfo.setIgnoreSuperType(true);
		}

		eClassCodecInfo.setSuperTypeInfo(superTypeInfo);
		return eClassCodecInfo;
	}

	private static final List<String> TYPE_ANNOTATION_KEYS = List.of(CodecModelInfoOptions.CODEC_TYPE_INCLUDE,
			CodecModelInfoOptions.CODEC_TYPE_KEY, CodecModelInfoOptions.CODEC_TYPE_STRATEGY,
			CodecModelInfoOptions.CODEC_TYPE_INHERITS_FROM_PARENT);
	
	
	private IdentityInfo getIdentityInfo(EClass eClass) {
		IdentityInfo idInfo = CodecInfoFactory.eINSTANCE.createIdentityInfo();
		Map<String, String> idAnnotationDetails = getAnnotationDetailsMap(eClass, CodecAnnotations.CODEC_ID, true);
		
		String strategy = idAnnotationDetails.getOrDefault(CodecModelInfoOptions.CODEC_ID_STRATEGY, "ID_FIELD");
		idInfo.setIdStrategy(strategy);
		
		String key = idAnnotationDetails.getOrDefault(CodecModelInfoOptions.CODEC_ID_KEY, "_id");
		idInfo.setIdKey(key);
		
		String separator = idAnnotationDetails.getOrDefault(CodecModelInfoOptions.CODEC_ID_SEPARATOR, "-");
		idInfo.setIdSeparator(separator);
		
		if("COMBINED".equals(strategy)) {
			String idFeatures = idAnnotationDetails.getOrDefault(CodecModelInfoOptions.CODEC_ID_FEATURES_LIST, "");
			if(!idFeatures.isEmpty()) {
				String[] idFeaturesSplit = idFeatures.split(",");
				for(String featureName : idFeaturesSplit) {
					EStructuralFeature feature =  eClass.getEStructuralFeature(featureName);
					if(feature != null) idInfo.getIdFeatures().add(feature);
					else LOGGER.warning(String.format("No EStructuralFeature with name %s found in EClass %s", featureName, eClass.getName()));
				}
			}
		} else {
			idInfo.setIdValueReaderName(CodecValueReaderConstants.OBJECT_TO_STRING_READER);			
			idInfo.setIdValueWriterName(CodecValueWriterConstants.OBJECT_TO_STRING_WRITER);
			if(eClass.getEIDAttribute() != null) {
				idInfo.getIdFeatures().add(eClass.getEIDAttribute());
			}			
		}
		return idInfo;
	}

	private TypeInfo getTypeInfo(EModelElement modelElement) {
		TypeInfo typeInfo = CodecInfoFactory.eINSTANCE.createTypeInfo();
		Map<String, String> typeAnnotationDetails = getTypeAnnotationDetailsWithInheritance(modelElement);

		String typeValue = typeAnnotationDetails.getOrDefault(CodecModelInfoOptions.CODEC_TYPE_INCLUDE, "true");
		if("false".equalsIgnoreCase(typeValue)) {
			typeInfo.setIgnoreType(true);
		}
		typeInfo.setTypeKey(typeAnnotationDetails.getOrDefault(CodecModelInfoOptions.CODEC_TYPE_KEY, "_type"));
		String typeStrategy = typeAnnotationDetails.getOrDefault(CodecModelInfoOptions.CODEC_TYPE_STRATEGY, "");
		Map<String, String> typeMap = new HashMap<>();
		typeAnnotationDetails.forEach((k,v) -> {
			if(!TYPE_ANNOTATION_KEYS.contains(k)) {
				typeMap.put(k, v);
			}
		});
		
		typeInfo.getTypeMap().putAll(typeMap);
		if(!typeInfo.isIgnoreType()) {
			typeInfo.setTypeStrategy(typeStrategy);
			switch(typeStrategy) {
			case "NAME":
				typeInfo.setTypeValueWriterName(CodecValueWriterConstants.WRITER_BY_ECLASS_NAME);
				typeInfo.setTypeValueReaderName(CodecValueReaderConstants.READER_BY_ECLASS_NAME);
				break;
			case "CLASS":
				typeInfo.setTypeValueWriterName(CodecValueWriterConstants.WRITER_BY_INSTANCE_CLASS_NAME);
				typeInfo.setTypeValueReaderName(CodecValueReaderConstants.READER_BY_INSTANCE_CLASS_NAME);
				break;
			case "URI": default:
				typeInfo.setTypeValueWriterName(CodecValueWriterConstants.URI_WRITER);
				typeInfo.setTypeValueReaderName(CodecValueReaderConstants.URI_READER);
				break;				
			}
		}
		return typeInfo;
	}


	private FeatureCodecInfo createCodecFeatureInfo(ETypedElement feature) {
		FeatureCodecInfo featureInfo = CodecInfoFactory.eINSTANCE.createFeatureCodecInfo();
		featureInfo.setId(UUID.randomUUID().toString());
		featureInfo.setType(feature instanceof EAttribute ? InfoType.ATTRIBUTE :
			feature instanceof EOperation ? InfoType.OPERATION : InfoType.REFERENCE);
		if(feature.getEType() instanceof EEnum) featureInfo.setType(InfoType.ENUMERATOR);

		featureInfo.setKey(getElementName(feature));
		featureInfo.setFeature(feature);

		// Filter out transient
		if(feature instanceof EStructuralFeature f && f.isTransient()) {
			featureInfo.setIgnore(true);
		}

		// Operations should be ignored by default unless explicitly included via annotation
		if(feature instanceof EOperation) {
			String includeOperation = getAnnotationDetails(feature, "codec", "include");
			if(!"true".equalsIgnoreCase(includeOperation)) {
				featureInfo.setIgnore(true);
			}
		}

		String isIgnore = getAnnotationDetails(feature, "codec", "transient");
		if("true".equalsIgnoreCase(isIgnore)) featureInfo.setIgnore(Boolean.valueOf(isIgnore));	

		//		Set value reader/writer from annotation
		String valueReaderName = getAnnotationDetails(feature, "codec", CodecAnnotations.CODEC_VALUE_READER_NAME);
		if(valueReaderName != null) featureInfo.setValueReaderName(valueReaderName);
		else if(feature instanceof EReference) featureInfo.setValueReaderName(CodecValueReaderConstants.URI_READER);

		String valueWriterName = getAnnotationDetails(feature, "codec", CodecAnnotations.CODEC_VALUE_WRITER_NAME);
		if(valueWriterName != null) featureInfo.setValueWriterName(valueWriterName);
		else if(feature instanceof EReference) featureInfo.setValueWriterName(CodecValueWriterConstants.URI_WRITER);

		if(feature instanceof EReference) {
			featureInfo.setTypeInfo(getTypeInfo(feature));
		}
		
		Map<String, String> extras = getAnnotationDetailsMap(feature, "codec.extras", true);
		if(!extras.isEmpty()) featureInfo.getCodecExtraProperties().putAll(extras);

		return featureInfo;
	}

	private String getAnnotationDetails(EModelElement element, String annotationSource, String annotationKey) {
		EAnnotation annotation = element.getEAnnotation(annotationSource);
		if(annotation != null) return annotation.getDetails().get(annotationKey);
		return null;
	}

	private String getAnnotationDetails(EModelElement element, String annotationSource, String annotationKey, boolean deriveFromParent) {
		EAnnotation annotation = element.getEAnnotation(annotationSource);
		if(annotation != null) return annotation.getDetails().get(annotationKey);
		if(deriveFromParent && element instanceof EClass ec) {
			for(EClass parent : ec.getESuperTypes()) {
				if(getAnnotationDetails(element, "codec", "inherit") == null || "false".equalsIgnoreCase(getAnnotationDetails(element, "codec", "inherit"))) {
					if(!parent.getEPackage().getNsURI().equals(ec.getEPackage().getNsURI())) {
						continue;
					}
				}
				String parentAnn = getAnnotationDetails(parent, annotationSource, annotationKey);
				if(parentAnn != null) return parentAnn;
			}
		}
		return null;
	}

	private Map<String, String> getAnnotationDetailsMap(EModelElement element, String annotationSource, boolean deriveFromParent) {
		EAnnotation annotation = element.getEAnnotation(annotationSource);
		if(annotation != null) return annotation.getDetails().map();
		if(deriveFromParent && element instanceof EClass ec) {
			for(EClass parent : ec.getESuperTypes()) {
				if(getAnnotationDetails(element, "codec", "inherit") == null || "false".equalsIgnoreCase(getAnnotationDetails(element, "codec", "inherit"))) {
					if(!parent.getEPackage().getNsURI().equals(ec.getEPackage().getNsURI())) {
						continue;
					}
				}
				return getAnnotationDetailsMap(parent, annotationSource, false);
			}
		}
		return Collections.emptyMap();
	}

	/**
	 * Gets codec.type annotation details for an EModelElement with inheritance support for EReferences.
	 *
	 * For EReferences:
	 * - If the reference has NO codec.type annotation → inherit from target EClass
	 * - If the reference has codec.type annotation WITHOUT inherits.from.parent="false" → merge annotations (target first, then reference overrides)
	 * - If the reference has codec.type annotation WITH inherits.from.parent="false" → use only reference annotation
	 *
	 * Note: The inherits.from.parent value can be overridden at runtime via CodecOptionsBuilder.
	 *
	 * @param modelElement The element to get type annotation details for
	 * @return Map of codec.type annotation details (may be empty, never null)
	 */
	private Map<String, String> getTypeAnnotationDetailsWithInheritance(EModelElement modelElement) {
		// Get annotation details from the element itself (could be empty)
		EAnnotation referenceAnnotation = modelElement.getEAnnotation(CodecAnnotations.CODEC_TYPE);
		Map<String, String> referenceDetails = referenceAnnotation != null ?
			new HashMap<>(referenceAnnotation.getDetails().map()) : new HashMap<>();

		// Only apply inheritance logic for EReferences
		if (!(modelElement instanceof EReference reference)) {
			// For non-references, use standard inheritance from superclasses
			return getAnnotationDetailsMap(modelElement, CodecAnnotations.CODEC_TYPE, true);
		}

		// Check if inheritance from target EClass is explicitly disabled
		// This can come from annotation (String) or will be overridden by runtime options (Boolean) in CodecResource
		String inheritsFromParent = referenceDetails.get(CodecModelInfoOptions.CODEC_TYPE_INHERITS_FROM_PARENT);
		if ("false".equalsIgnoreCase(inheritsFromParent)) {
			// Inheritance disabled - use only reference annotation
			return referenceDetails;
		}

		// Get target EClass
		if (!(reference.getEType() instanceof EClass targetClass)) {
			// Not an EClass reference, return reference details as-is
			return referenceDetails;
		}

		// Get codec.type annotation from target EClass
		Map<String, String> targetDetails = getAnnotationDetailsMap(targetClass, CodecAnnotations.CODEC_TYPE, true);

		// If target has no codec.type annotation, return reference details
		if (targetDetails.isEmpty()) {
			return referenceDetails;
		}

		// Merge: target details first, then reference details override
		Map<String, String> merged = new HashMap<>(targetDetails);
		merged.putAll(referenceDetails);

		return merged;
	}

	private static final String EXTENDED_METADATA = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
	private static final String JSON_PROPERTY = "JsonProperty";
	private String getElementName(final ENamedElement element) {
		String value = getAnnotationDetails(element, EXTENDED_METADATA, "name");
		if(value != null) return value;
		value = getAnnotationDetails(element, JSON_PROPERTY, "value");
		if(value != null) return value;
		return element.getName();
	}

	private void createCodecInfoHolderMap() {
		for(InfoType type : InfoType.values()) {
			if(!codecInfoHolderMap.containsKey(type)) {
				codecInfoHolderMap.put(type, codecInfoHolderHelper.createCodecInfoHolderForType(type));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.EPackage.Registry#getEFactory(java.lang.String)
	 */
	@Override
	public EFactory getEFactory(String uri) {
		throw new UnsupportedOperationException("This method must not be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.EPackage.Registry#getEPackage(java.lang.String)
	 */
	@Override
	public EPackage getEPackage(String nsUri) {
		throw new UnsupportedOperationException("This method must not be called");

	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.info.CodecModelInfo#getCodecInfoForPackage(java.lang.String)
	 */
	@Override
	public Optional<PackageCodecInfo> getCodecInfoForPackage(String uri) {
		return Optional.ofNullable(ePackageCodecInfoMap.getOrDefault(uri, null))
				.map(ci -> (PackageCodecInfo) EcoreUtil.copy((EObject) ci));	
	}

	@Override
	public Optional<PackageCodecInfo> getCodecInfoForPackage(EPackage ePackage) {
		return Optional.ofNullable(ePackageCodecInfoMap.getOrDefault(ePackage.getNsURI(), null))
				.map(ci -> (PackageCodecInfo) EcoreUtil.copy((EObject) ci))
				.or(() -> Optional.of(doCreatePackageCodecInfo(ePackage)))
				;	
	}


	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.info.CodecModelInfo#getCodecInfoForEClass(org.eclipse.emf.ecore.EClass)
	 */
	@Override
	public Optional<EClassCodecInfo> getCodecInfoForEClass(EClass eClass) {		
		PackageCodecInfo ePackageCodecInfo = ePackageCodecInfoMap.getOrDefault(eClass.getEPackage().getNsURI(), null);
		if(ePackageCodecInfo == null) return Optional.empty();
		String eClassId = EcoreUtil.getURI(eClass).toString();
		return ePackageCodecInfo.getEClassCodecInfo().stream().filter(ecci -> ecci.getId().equals(eClassId))
				.findFirst().map(ecci -> (EClassCodecInfo) EcoreUtil.copy((EObject) ecci));		
	}


	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.info.CodecModelInfo#getCodecInfoHolderByType(org.gecko.codec.info.codecinfo.InfoType)
	 */
	@Override
	public CodecInfoHolder getCodecInfoHolderByType(InfoType infoType) {
		return codecInfoHolderMap.get(infoType);	
	}


	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.info.CodecModelInfo#addCodecValueWriterForType(org.gecko.codec.info.codecinfo.InfoType, org.gecko.codec.info.codecinfo.CodecValueWriter)
	 */
	@Override
	public void addCodecValueWriterForType(InfoType infoType, CodecValueWriter<?, ?> writer) {
		codecInfoHolderHelper.addCodecWriter(getCodecInfoHolderByType(infoType), writer);
		codecInfoHolderMap.put(infoType, getCodecInfoHolderByType(infoType));		
	}


	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.info.CodecModelInfo#addCodecValueReaderForType(org.gecko.codec.info.codecinfo.InfoType, org.gecko.codec.info.codecinfo.CodecValueReader)
	 */
	@Override
	public void addCodecValueReaderForType(InfoType infoType, CodecValueReader<?, ?> reader) {
		codecInfoHolderHelper.addCodecReader(getCodecInfoHolderByType(infoType), reader);
		codecInfoHolderMap.put(infoType, getCodecInfoHolderByType(infoType));	

	}
}