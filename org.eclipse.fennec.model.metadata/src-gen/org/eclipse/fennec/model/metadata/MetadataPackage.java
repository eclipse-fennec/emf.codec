/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 */
package org.eclipse.fennec.model.metadata;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.fennec.emf.osgi.annotation.provide.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.model.metadata.MetadataFactory
 * @model kind="package"
 *        annotation="Version value='1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = MetadataPackage.eNS_URI, genModel = "/model/metadata.genmodel", genModelSourceLocations = {"model/metadata.genmodel","org.eclipse.fennec.model.metadata/model/metadata.genmodel"}, ecore = "/model/metadata.ecore", ecoreSourceLocations = "/model/metadata.ecore")
public interface MetadataPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "metadata";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/metadata/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "metadata";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MetadataPackage eINSTANCE = org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.MetadataDiagnosticImpl <em>Diagnostic</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataDiagnosticImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getMetadataDiagnostic()
	 * @generated
	 */
	int METADATA_DIAGNOSTIC = 0;

	/**
	 * The feature id for the '<em><b>Severity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_DIAGNOSTIC__SEVERITY = 0;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_DIAGNOSTIC__MESSAGE = 1;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_DIAGNOSTIC__KEY = 2;

	/**
	 * The number of structural features of the '<em>Diagnostic</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_DIAGNOSTIC_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Diagnostic</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_DIAGNOSTIC_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.DiagnosticContainer <em>Diagnostic Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.DiagnosticContainer
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getDiagnosticContainer()
	 * @generated
	 */
	int DIAGNOSTIC_CONTAINER = 1;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIAGNOSTIC_CONTAINER__DIAGNOSTICS = 0;

	/**
	 * The feature id for the '<em><b>All Diagnostics</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIAGNOSTIC_CONTAINER__ALL_DIAGNOSTICS = 1;

	/**
	 * The number of structural features of the '<em>Diagnostic Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIAGNOSTIC_CONTAINER_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Diagnostic Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DIAGNOSTIC_CONTAINER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl <em>Base Type Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseTypeConfig()
	 * @generated
	 */
	int BASE_TYPE_CONFIG = 2;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_TYPE_CONFIG__FORMAT = 0;

	/**
	 * The feature id for the '<em><b>Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_TYPE_CONFIG__STRATEGY = 1;

	/**
	 * The feature id for the '<em><b>Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_TYPE_CONFIG__TYPE_KEY = 2;

	/**
	 * The feature id for the '<em><b>Schema Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_TYPE_CONFIG__SCHEMA_KEY = 3;

	/**
	 * The feature id for the '<em><b>Name Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_TYPE_CONFIG__NAME_KEY = 4;

	/**
	 * The number of structural features of the '<em>Base Type Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_TYPE_CONFIG_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Base Type Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_TYPE_CONFIG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl <em>Base Id Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseIdConfig()
	 * @generated
	 */
	int BASE_ID_CONFIG = 3;

	/**
	 * The feature id for the '<em><b>Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG__STRATEGY = 0;

	/**
	 * The feature id for the '<em><b>Key Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG__KEY_MODE = 1;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG__FORMAT = 2;

	/**
	 * The feature id for the '<em><b>Id Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG__ID_KEY = 3;

	/**
	 * The feature id for the '<em><b>Separator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG__SEPARATOR = 4;

	/**
	 * The feature id for the '<em><b>On Top</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG__ON_TOP = 5;

	/**
	 * The feature id for the '<em><b>Serialize Separator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG__SERIALIZE_SEPARATOR = 6;

	/**
	 * The feature id for the '<em><b>Separator Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG__SEPARATOR_KEY = 7;

	/**
	 * The feature id for the '<em><b>Value Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG__VALUE_KEY = 8;

	/**
	 * The number of structural features of the '<em>Base Id Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG_FEATURE_COUNT = 9;

	/**
	 * The number of operations of the '<em>Base Id Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_ID_CONFIG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.BaseReferenceConfigImpl <em>Base Reference Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.BaseReferenceConfigImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseReferenceConfig()
	 * @generated
	 */
	int BASE_REFERENCE_CONFIG = 4;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REFERENCE_CONFIG__FORMAT = 0;

	/**
	 * The feature id for the '<em><b>Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REFERENCE_CONFIG__TYPE_KEY = 1;

	/**
	 * The feature id for the '<em><b>Ref Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REFERENCE_CONFIG__REF_KEY = 2;

	/**
	 * The number of structural features of the '<em>Base Reference Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REFERENCE_CONFIG_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Base Reference Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_REFERENCE_CONFIG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.BaseSuperTypeConfigImpl <em>Base Super Type Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.BaseSuperTypeConfigImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseSuperTypeConfig()
	 * @generated
	 */
	int BASE_SUPER_TYPE_CONFIG = 5;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_SUPER_TYPE_CONFIG__ENABLED = 0;

	/**
	 * The feature id for the '<em><b>Selection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_SUPER_TYPE_CONFIG__SELECTION = 1;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_SUPER_TYPE_CONFIG__FORMAT = 2;

	/**
	 * The feature id for the '<em><b>As Array</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_SUPER_TYPE_CONFIG__AS_ARRAY = 3;

	/**
	 * The feature id for the '<em><b>Separator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_SUPER_TYPE_CONFIG__SEPARATOR = 4;

	/**
	 * The feature id for the '<em><b>Super Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY = 5;

	/**
	 * The number of structural features of the '<em>Base Super Type Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_SUPER_TYPE_CONFIG_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Base Super Type Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_SUPER_TYPE_CONFIG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.BaseFeatureConfigImpl <em>Base Feature Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.BaseFeatureConfigImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseFeatureConfig()
	 * @generated
	 */
	int BASE_FEATURE_CONFIG = 6;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__KEY = 0;

	/**
	 * The feature id for the '<em><b>Ignore</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__IGNORE = 1;

	/**
	 * The feature id for the '<em><b>Ignore Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__IGNORE_READ = 2;

	/**
	 * The feature id for the '<em><b>Ignore Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__IGNORE_WRITE = 3;

	/**
	 * The feature id for the '<em><b>Force Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__FORCE_READ = 4;

	/**
	 * The feature id for the '<em><b>Force Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__FORCE_WRITE = 5;

	/**
	 * The feature id for the '<em><b>Serialize Null</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__SERIALIZE_NULL = 6;

	/**
	 * The feature id for the '<em><b>Serialize Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__SERIALIZE_EMPTY = 7;

	/**
	 * The feature id for the '<em><b>Serialize Defaults</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__SERIALIZE_DEFAULTS = 8;

	/**
	 * The feature id for the '<em><b>Enum Serialization</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG__ENUM_SERIALIZATION = 9;

	/**
	 * The number of structural features of the '<em>Base Feature Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG_FEATURE_COUNT = 10;

	/**
	 * The number of operations of the '<em>Base Feature Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BASE_FEATURE_CONFIG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.AspectImpl <em>Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.AspectImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getAspect()
	 * @generated
	 */
	int ASPECT = 7;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT__TYPE_ID = 0;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT__DIAGNOSTICS = 1;

	/**
	 * The number of structural features of the '<em>Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.PackageAspectImpl <em>Package Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.PackageAspectImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getPackageAspect()
	 * @generated
	 */
	int PACKAGE_ASPECT = 8;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_ASPECT__TYPE_ID = ASPECT__TYPE_ID;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_ASPECT__DIAGNOSTICS = ASPECT__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Package Metadata</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_ASPECT__PACKAGE_METADATA = ASPECT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Package Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_ASPECT_FEATURE_COUNT = ASPECT_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Package Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_ASPECT_OPERATION_COUNT = ASPECT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.ClassAspectImpl <em>Class Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.ClassAspectImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getClassAspect()
	 * @generated
	 */
	int CLASS_ASPECT = 9;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_ASPECT__TYPE_ID = ASPECT__TYPE_ID;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_ASPECT__DIAGNOSTICS = ASPECT__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Class Metadata</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_ASPECT__CLASS_METADATA = ASPECT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Class Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_ASPECT_FEATURE_COUNT = ASPECT_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Class Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_ASPECT_OPERATION_COUNT = ASPECT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.FeatureAspectImpl <em>Feature Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.FeatureAspectImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getFeatureAspect()
	 * @generated
	 */
	int FEATURE_ASPECT = 10;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_ASPECT__TYPE_ID = ASPECT__TYPE_ID;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_ASPECT__DIAGNOSTICS = ASPECT__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Feature Metadata</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_ASPECT__FEATURE_METADATA = ASPECT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Feature Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_ASPECT_FEATURE_COUNT = ASPECT_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Feature Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_ASPECT_OPERATION_COUNT = ASPECT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.PackageProfileImpl <em>Package Profile</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.PackageProfileImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getPackageProfile()
	 * @generated
	 */
	int PACKAGE_PROFILE = 11;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_PROFILE__TYPE_ID = 0;

	/**
	 * The feature id for the '<em><b>Class Profiles</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_PROFILE__CLASS_PROFILES = 1;

	/**
	 * The number of structural features of the '<em>Package Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_PROFILE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Package Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_PROFILE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.ClassProfileImpl <em>Class Profile</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.ClassProfileImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getClassProfile()
	 * @generated
	 */
	int CLASS_PROFILE = 12;

	/**
	 * The feature id for the '<em><b>EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_PROFILE__ECLASS = 0;

	/**
	 * The number of structural features of the '<em>Class Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_PROFILE_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Class Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_PROFILE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl <em>Package Metadata</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getPackageMetadata()
	 * @generated
	 */
	int PACKAGE_METADATA = 13;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_METADATA__DIAGNOSTICS = DIAGNOSTIC_CONTAINER__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>All Diagnostics</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_METADATA__ALL_DIAGNOSTICS = DIAGNOSTIC_CONTAINER__ALL_DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>EPackage</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_METADATA__EPACKAGE = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Ns URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_METADATA__NS_URI = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Classes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_METADATA__CLASSES = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Aspects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_METADATA__ASPECTS = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Profiles</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_METADATA__PROFILES = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Package Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_METADATA_FEATURE_COUNT = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Package Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_METADATA_OPERATION_COUNT = DIAGNOSTIC_CONTAINER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.ClassMetadataImpl <em>Class Metadata</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.ClassMetadataImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getClassMetadata()
	 * @generated
	 */
	int CLASS_METADATA = 14;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__DIAGNOSTICS = DIAGNOSTIC_CONTAINER__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>All Diagnostics</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__ALL_DIAGNOSTICS = DIAGNOSTIC_CONTAINER__ALL_DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Package</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__PACKAGE = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__ECLASS = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__NAME = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Classifier ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__CLASSIFIER_ID = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Type URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__TYPE_URI = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Features</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__FEATURES = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Super Types</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__SUPER_TYPES = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>All Super Types</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__ALL_SUPER_TYPES = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Id Features</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__ID_FEATURES = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Has Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__HAS_ID = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Aspects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA__ASPECTS = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 10;

	/**
	 * The number of structural features of the '<em>Class Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA_FEATURE_COUNT = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 11;

	/**
	 * The number of operations of the '<em>Class Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_METADATA_OPERATION_COUNT = DIAGNOSTIC_CONTAINER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.FeatureMetadataImpl <em>Feature Metadata</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.FeatureMetadataImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getFeatureMetadata()
	 * @generated
	 */
	int FEATURE_METADATA = 15;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA__DIAGNOSTICS = DIAGNOSTIC_CONTAINER__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>All Diagnostics</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA__ALL_DIAGNOSTICS = DIAGNOSTIC_CONTAINER__ALL_DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Class Metadata</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA__CLASS_METADATA = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>EFeature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA__EFEATURE = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA__NAME = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Extended Meta Data Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA__EXTENDED_META_DATA_NAME = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Feature ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA__FEATURE_ID = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Aspects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA__ASPECTS = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Feature Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA_FEATURE_COUNT = DIAGNOSTIC_CONTAINER_FEATURE_COUNT + 6;

	/**
	 * The number of operations of the '<em>Feature Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_METADATA_OPERATION_COUNT = DIAGNOSTIC_CONTAINER_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.AttributeMetadataImpl <em>Attribute Metadata</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.AttributeMetadataImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getAttributeMetadata()
	 * @generated
	 */
	int ATTRIBUTE_METADATA = 16;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__DIAGNOSTICS = FEATURE_METADATA__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>All Diagnostics</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__ALL_DIAGNOSTICS = FEATURE_METADATA__ALL_DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Class Metadata</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__CLASS_METADATA = FEATURE_METADATA__CLASS_METADATA;

	/**
	 * The feature id for the '<em><b>EFeature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__EFEATURE = FEATURE_METADATA__EFEATURE;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__NAME = FEATURE_METADATA__NAME;

	/**
	 * The feature id for the '<em><b>Extended Meta Data Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__EXTENDED_META_DATA_NAME = FEATURE_METADATA__EXTENDED_META_DATA_NAME;

	/**
	 * The feature id for the '<em><b>Feature ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__FEATURE_ID = FEATURE_METADATA__FEATURE_ID;

	/**
	 * The feature id for the '<em><b>Aspects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__ASPECTS = FEATURE_METADATA__ASPECTS;

	/**
	 * The feature id for the '<em><b>EAttribute</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__EATTRIBUTE = FEATURE_METADATA_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Is Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__IS_ID = FEATURE_METADATA_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA__DEFAULT_VALUE = FEATURE_METADATA_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Attribute Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA_FEATURE_COUNT = FEATURE_METADATA_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Attribute Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_METADATA_OPERATION_COUNT = FEATURE_METADATA_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.ReferenceMetadataImpl <em>Reference Metadata</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.ReferenceMetadataImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getReferenceMetadata()
	 * @generated
	 */
	int REFERENCE_METADATA = 17;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__DIAGNOSTICS = FEATURE_METADATA__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>All Diagnostics</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__ALL_DIAGNOSTICS = FEATURE_METADATA__ALL_DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Class Metadata</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__CLASS_METADATA = FEATURE_METADATA__CLASS_METADATA;

	/**
	 * The feature id for the '<em><b>EFeature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__EFEATURE = FEATURE_METADATA__EFEATURE;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__NAME = FEATURE_METADATA__NAME;

	/**
	 * The feature id for the '<em><b>Extended Meta Data Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__EXTENDED_META_DATA_NAME = FEATURE_METADATA__EXTENDED_META_DATA_NAME;

	/**
	 * The feature id for the '<em><b>Feature ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__FEATURE_ID = FEATURE_METADATA__FEATURE_ID;

	/**
	 * The feature id for the '<em><b>Aspects</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__ASPECTS = FEATURE_METADATA__ASPECTS;

	/**
	 * The feature id for the '<em><b>EReference</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__EREFERENCE = FEATURE_METADATA_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Containment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__CONTAINMENT = FEATURE_METADATA_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Target Class Metadata</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__TARGET_CLASS_METADATA = FEATURE_METADATA_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Opposite Metadata</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__OPPOSITE_METADATA = FEATURE_METADATA_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Has Bidirectional</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA__HAS_BIDIRECTIONAL = FEATURE_METADATA_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Reference Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA_FEATURE_COUNT = FEATURE_METADATA_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Reference Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_METADATA_OPERATION_COUNT = FEATURE_METADATA_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.impl.MetadataRegistryImpl <em>Registry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataRegistryImpl
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getMetadataRegistry()
	 * @generated
	 */
	int METADATA_REGISTRY = 18;

	/**
	 * The feature id for the '<em><b>Packages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_REGISTRY__PACKAGES = 0;

	/**
	 * The number of structural features of the '<em>Registry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_REGISTRY_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Registry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_REGISTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.DiagnosticSeverity <em>Diagnostic Severity</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.DiagnosticSeverity
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getDiagnosticSeverity()
	 * @generated
	 */
	int DIAGNOSTIC_SEVERITY = 19;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.SerializationFormat <em>Serialization Format</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getSerializationFormat()
	 * @generated
	 */
	int SERIALIZATION_FORMAT = 20;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.TypeStrategy <em>Type Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.TypeStrategy
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getTypeStrategy()
	 * @generated
	 */
	int TYPE_STRATEGY = 21;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.IdStrategy <em>Id Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.IdStrategy
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getIdStrategy()
	 * @generated
	 */
	int ID_STRATEGY = 22;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.IdKeyMode <em>Id Key Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.IdKeyMode
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getIdKeyMode()
	 * @generated
	 */
	int ID_KEY_MODE = 23;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.SuperTypeSelection <em>Super Type Selection</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.SuperTypeSelection
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getSuperTypeSelection()
	 * @generated
	 */
	int SUPER_TYPE_SELECTION = 24;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.EnumSerializationStrategy <em>Enum Serialization Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.EnumSerializationStrategy
	 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getEnumSerializationStrategy()
	 * @generated
	 */
	int ENUM_SERIALIZATION_STRATEGY = 25;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.MetadataDiagnostic <em>Diagnostic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Diagnostic</em>'.
	 * @see org.eclipse.fennec.model.metadata.MetadataDiagnostic
	 * @generated
	 */
	EClass getMetadataDiagnostic();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.MetadataDiagnostic#getSeverity <em>Severity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Severity</em>'.
	 * @see org.eclipse.fennec.model.metadata.MetadataDiagnostic#getSeverity()
	 * @see #getMetadataDiagnostic()
	 * @generated
	 */
	EAttribute getMetadataDiagnostic_Severity();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.MetadataDiagnostic#getMessage <em>Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Message</em>'.
	 * @see org.eclipse.fennec.model.metadata.MetadataDiagnostic#getMessage()
	 * @see #getMetadataDiagnostic()
	 * @generated
	 */
	EAttribute getMetadataDiagnostic_Message();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.MetadataDiagnostic#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.MetadataDiagnostic#getKey()
	 * @see #getMetadataDiagnostic()
	 * @generated
	 */
	EAttribute getMetadataDiagnostic_Key();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.DiagnosticContainer <em>Diagnostic Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Diagnostic Container</em>'.
	 * @see org.eclipse.fennec.model.metadata.DiagnosticContainer
	 * @generated
	 */
	EClass getDiagnosticContainer();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.DiagnosticContainer#getDiagnostics <em>Diagnostics</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Diagnostics</em>'.
	 * @see org.eclipse.fennec.model.metadata.DiagnosticContainer#getDiagnostics()
	 * @see #getDiagnosticContainer()
	 * @generated
	 */
	EReference getDiagnosticContainer_Diagnostics();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.model.metadata.DiagnosticContainer#getAllDiagnostics <em>All Diagnostics</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>All Diagnostics</em>'.
	 * @see org.eclipse.fennec.model.metadata.DiagnosticContainer#getAllDiagnostics()
	 * @see #getDiagnosticContainer()
	 * @generated
	 */
	EReference getDiagnosticContainer_AllDiagnostics();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig <em>Base Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Type Config</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseTypeConfig
	 * @generated
	 */
	EClass getBaseTypeConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseTypeConfig#getFormat()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getStrategy <em>Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strategy</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseTypeConfig#getStrategy()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_Strategy();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getTypeKey <em>Type Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseTypeConfig#getTypeKey()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_TypeKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getSchemaKey <em>Schema Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseTypeConfig#getSchemaKey()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_SchemaKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getNameKey <em>Name Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseTypeConfig#getNameKey()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_NameKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.BaseIdConfig <em>Base Id Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Id Config</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig
	 * @generated
	 */
	EClass getBaseIdConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getStrategy <em>Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strategy</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig#getStrategy()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_Strategy();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getKeyMode <em>Key Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key Mode</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig#getKeyMode()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_KeyMode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig#getFormat()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getIdKey <em>Id Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig#getIdKey()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_IdKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getSeparator <em>Separator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Separator</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig#getSeparator()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_Separator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#isOnTop <em>On Top</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>On Top</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig#isOnTop()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_OnTop();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#isSerializeSeparator <em>Serialize Separator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Separator</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig#isSerializeSeparator()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_SerializeSeparator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getSeparatorKey <em>Separator Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Separator Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig#getSeparatorKey()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_SeparatorKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getValueKey <em>Value Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseIdConfig#getValueKey()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_ValueKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig <em>Base Reference Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Reference Config</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseReferenceConfig
	 * @generated
	 */
	EClass getBaseReferenceConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseReferenceConfig#getFormat()
	 * @see #getBaseReferenceConfig()
	 * @generated
	 */
	EAttribute getBaseReferenceConfig_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig#getTypeKey <em>Type Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseReferenceConfig#getTypeKey()
	 * @see #getBaseReferenceConfig()
	 * @generated
	 */
	EAttribute getBaseReferenceConfig_TypeKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig#getRefKey <em>Ref Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseReferenceConfig#getRefKey()
	 * @see #getBaseReferenceConfig()
	 * @generated
	 */
	EAttribute getBaseReferenceConfig_RefKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig <em>Base Super Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Super Type Config</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseSuperTypeConfig
	 * @generated
	 */
	EClass getBaseSuperTypeConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#isEnabled <em>Enabled</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enabled</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#isEnabled()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_Enabled();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSelection <em>Selection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Selection</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSelection()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_Selection();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getFormat()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#isAsArray <em>As Array</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>As Array</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#isAsArray()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_AsArray();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSeparator <em>Separator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Separator</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSeparator()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_Separator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSuperTypeKey <em>Super Type Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Super Type Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSuperTypeKey()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_SuperTypeKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig <em>Base Feature Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Feature Config</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig
	 * @generated
	 */
	EClass getBaseFeatureConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getKey()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_Key();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnore <em>Ignore</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnore()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_Ignore();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnoreRead <em>Ignore Read</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore Read</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnoreRead()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_IgnoreRead();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnoreWrite <em>Ignore Write</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore Write</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnoreWrite()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_IgnoreWrite();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getForceRead <em>Force Read</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Force Read</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getForceRead()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_ForceRead();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getForceWrite <em>Force Write</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Force Write</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getForceWrite()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_ForceWrite();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeNull <em>Serialize Null</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Null</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeNull()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_SerializeNull();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeEmpty <em>Serialize Empty</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Empty</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeEmpty()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_SerializeEmpty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeDefaults <em>Serialize Defaults</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Defaults</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeDefaults()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_SerializeDefaults();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getEnumSerialization <em>Enum Serialization</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enum Serialization</em>'.
	 * @see org.eclipse.fennec.model.metadata.BaseFeatureConfig#getEnumSerialization()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_EnumSerialization();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.Aspect <em>Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Aspect</em>'.
	 * @see org.eclipse.fennec.model.metadata.Aspect
	 * @generated
	 */
	EClass getAspect();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.Aspect#getTypeId <em>Type Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Id</em>'.
	 * @see org.eclipse.fennec.model.metadata.Aspect#getTypeId()
	 * @see #getAspect()
	 * @generated
	 */
	EAttribute getAspect_TypeId();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.Aspect#getDiagnostics <em>Diagnostics</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Diagnostics</em>'.
	 * @see org.eclipse.fennec.model.metadata.Aspect#getDiagnostics()
	 * @see #getAspect()
	 * @generated
	 */
	EReference getAspect_Diagnostics();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.PackageAspect <em>Package Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Package Aspect</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageAspect
	 * @generated
	 */
	EClass getPackageAspect();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.fennec.model.metadata.PackageAspect#getPackageMetadata <em>Package Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Package Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageAspect#getPackageMetadata()
	 * @see #getPackageAspect()
	 * @generated
	 */
	EReference getPackageAspect_PackageMetadata();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.ClassAspect <em>Class Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Class Aspect</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassAspect
	 * @generated
	 */
	EClass getClassAspect();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.fennec.model.metadata.ClassAspect#getClassMetadata <em>Class Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Class Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassAspect#getClassMetadata()
	 * @see #getClassAspect()
	 * @generated
	 */
	EReference getClassAspect_ClassMetadata();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.FeatureAspect <em>Feature Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature Aspect</em>'.
	 * @see org.eclipse.fennec.model.metadata.FeatureAspect
	 * @generated
	 */
	EClass getFeatureAspect();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.fennec.model.metadata.FeatureAspect#getFeatureMetadata <em>Feature Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Feature Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.FeatureAspect#getFeatureMetadata()
	 * @see #getFeatureAspect()
	 * @generated
	 */
	EReference getFeatureAspect_FeatureMetadata();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.PackageProfile <em>Package Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Package Profile</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageProfile
	 * @generated
	 */
	EClass getPackageProfile();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.PackageProfile#getTypeId <em>Type Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Id</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageProfile#getTypeId()
	 * @see #getPackageProfile()
	 * @generated
	 */
	EAttribute getPackageProfile_TypeId();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.PackageProfile#getClassProfiles <em>Class Profiles</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Class Profiles</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageProfile#getClassProfiles()
	 * @see #getPackageProfile()
	 * @generated
	 */
	EReference getPackageProfile_ClassProfiles();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.ClassProfile <em>Class Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Class Profile</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassProfile
	 * @generated
	 */
	EClass getClassProfile();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.metadata.ClassProfile#getEClass <em>EClass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>EClass</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassProfile#getEClass()
	 * @see #getClassProfile()
	 * @generated
	 */
	EReference getClassProfile_EClass();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.PackageMetadata <em>Package Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Package Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageMetadata
	 * @generated
	 */
	EClass getPackageMetadata();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.metadata.PackageMetadata#getEPackage <em>EPackage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>EPackage</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageMetadata#getEPackage()
	 * @see #getPackageMetadata()
	 * @generated
	 */
	EReference getPackageMetadata_EPackage();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.PackageMetadata#getNsURI <em>Ns URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ns URI</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageMetadata#getNsURI()
	 * @see #getPackageMetadata()
	 * @generated
	 */
	EAttribute getPackageMetadata_NsURI();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.PackageMetadata#getClasses <em>Classes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Classes</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageMetadata#getClasses()
	 * @see #getPackageMetadata()
	 * @generated
	 */
	EReference getPackageMetadata_Classes();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.PackageMetadata#getAspects <em>Aspects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Aspects</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageMetadata#getAspects()
	 * @see #getPackageMetadata()
	 * @generated
	 */
	EReference getPackageMetadata_Aspects();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.PackageMetadata#getProfiles <em>Profiles</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Profiles</em>'.
	 * @see org.eclipse.fennec.model.metadata.PackageMetadata#getProfiles()
	 * @see #getPackageMetadata()
	 * @generated
	 */
	EReference getPackageMetadata_Profiles();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.ClassMetadata <em>Class Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Class Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata
	 * @generated
	 */
	EClass getClassMetadata();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getPackage <em>Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Package</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getPackage()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EReference getClassMetadata_Package();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getEClass <em>EClass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>EClass</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getEClass()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EReference getClassMetadata_EClass();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getName()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EAttribute getClassMetadata_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getClassifierID <em>Classifier ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Classifier ID</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getClassifierID()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EAttribute getClassMetadata_ClassifierID();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getTypeURI <em>Type URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type URI</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getTypeURI()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EAttribute getClassMetadata_TypeURI();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getFeatures <em>Features</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Features</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getFeatures()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EReference getClassMetadata_Features();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getSuperTypes <em>Super Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Super Types</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getSuperTypes()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EReference getClassMetadata_SuperTypes();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getAllSuperTypes <em>All Super Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>All Super Types</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getAllSuperTypes()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EReference getClassMetadata_AllSuperTypes();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getIdFeatures <em>Id Features</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Id Features</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getIdFeatures()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EReference getClassMetadata_IdFeatures();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.ClassMetadata#isHasId <em>Has Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Has Id</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#isHasId()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EAttribute getClassMetadata_HasId();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getAspects <em>Aspects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Aspects</em>'.
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getAspects()
	 * @see #getClassMetadata()
	 * @generated
	 */
	EReference getClassMetadata_Aspects();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.FeatureMetadata <em>Feature Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.FeatureMetadata
	 * @generated
	 */
	EClass getFeatureMetadata();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.fennec.model.metadata.FeatureMetadata#getClassMetadata <em>Class Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Class Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.FeatureMetadata#getClassMetadata()
	 * @see #getFeatureMetadata()
	 * @generated
	 */
	EReference getFeatureMetadata_ClassMetadata();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.metadata.FeatureMetadata#getEFeature <em>EFeature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>EFeature</em>'.
	 * @see org.eclipse.fennec.model.metadata.FeatureMetadata#getEFeature()
	 * @see #getFeatureMetadata()
	 * @generated
	 */
	EReference getFeatureMetadata_EFeature();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.FeatureMetadata#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.metadata.FeatureMetadata#getName()
	 * @see #getFeatureMetadata()
	 * @generated
	 */
	EAttribute getFeatureMetadata_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.FeatureMetadata#getExtendedMetaDataName <em>Extended Meta Data Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Extended Meta Data Name</em>'.
	 * @see org.eclipse.fennec.model.metadata.FeatureMetadata#getExtendedMetaDataName()
	 * @see #getFeatureMetadata()
	 * @generated
	 */
	EAttribute getFeatureMetadata_ExtendedMetaDataName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.FeatureMetadata#getFeatureID <em>Feature ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Feature ID</em>'.
	 * @see org.eclipse.fennec.model.metadata.FeatureMetadata#getFeatureID()
	 * @see #getFeatureMetadata()
	 * @generated
	 */
	EAttribute getFeatureMetadata_FeatureID();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.FeatureMetadata#getAspects <em>Aspects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Aspects</em>'.
	 * @see org.eclipse.fennec.model.metadata.FeatureMetadata#getAspects()
	 * @see #getFeatureMetadata()
	 * @generated
	 */
	EReference getFeatureMetadata_Aspects();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.AttributeMetadata <em>Attribute Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.AttributeMetadata
	 * @generated
	 */
	EClass getAttributeMetadata();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.metadata.AttributeMetadata#getEAttribute <em>EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>EAttribute</em>'.
	 * @see org.eclipse.fennec.model.metadata.AttributeMetadata#getEAttribute()
	 * @see #getAttributeMetadata()
	 * @generated
	 */
	EReference getAttributeMetadata_EAttribute();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.AttributeMetadata#isIsId <em>Is Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Id</em>'.
	 * @see org.eclipse.fennec.model.metadata.AttributeMetadata#isIsId()
	 * @see #getAttributeMetadata()
	 * @generated
	 */
	EAttribute getAttributeMetadata_IsId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.AttributeMetadata#getDefaultValue <em>Default Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see org.eclipse.fennec.model.metadata.AttributeMetadata#getDefaultValue()
	 * @see #getAttributeMetadata()
	 * @generated
	 */
	EAttribute getAttributeMetadata_DefaultValue();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.ReferenceMetadata <em>Reference Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Reference Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.ReferenceMetadata
	 * @generated
	 */
	EClass getReferenceMetadata();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.metadata.ReferenceMetadata#getEReference <em>EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>EReference</em>'.
	 * @see org.eclipse.fennec.model.metadata.ReferenceMetadata#getEReference()
	 * @see #getReferenceMetadata()
	 * @generated
	 */
	EReference getReferenceMetadata_EReference();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.ReferenceMetadata#isContainment <em>Containment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Containment</em>'.
	 * @see org.eclipse.fennec.model.metadata.ReferenceMetadata#isContainment()
	 * @see #getReferenceMetadata()
	 * @generated
	 */
	EAttribute getReferenceMetadata_Containment();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.metadata.ReferenceMetadata#getTargetClassMetadata <em>Target Class Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target Class Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.ReferenceMetadata#getTargetClassMetadata()
	 * @see #getReferenceMetadata()
	 * @generated
	 */
	EReference getReferenceMetadata_TargetClassMetadata();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.metadata.ReferenceMetadata#getOppositeMetadata <em>Opposite Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Opposite Metadata</em>'.
	 * @see org.eclipse.fennec.model.metadata.ReferenceMetadata#getOppositeMetadata()
	 * @see #getReferenceMetadata()
	 * @generated
	 */
	EReference getReferenceMetadata_OppositeMetadata();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.metadata.ReferenceMetadata#isHasBidirectional <em>Has Bidirectional</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Has Bidirectional</em>'.
	 * @see org.eclipse.fennec.model.metadata.ReferenceMetadata#isHasBidirectional()
	 * @see #getReferenceMetadata()
	 * @generated
	 */
	EAttribute getReferenceMetadata_HasBidirectional();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.MetadataRegistry <em>Registry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Registry</em>'.
	 * @see org.eclipse.fennec.model.metadata.MetadataRegistry
	 * @generated
	 */
	EClass getMetadataRegistry();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.metadata.MetadataRegistry#getPackages <em>Packages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Packages</em>'.
	 * @see org.eclipse.fennec.model.metadata.MetadataRegistry#getPackages()
	 * @see #getMetadataRegistry()
	 * @generated
	 */
	EReference getMetadataRegistry_Packages();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.metadata.DiagnosticSeverity <em>Diagnostic Severity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Diagnostic Severity</em>'.
	 * @see org.eclipse.fennec.model.metadata.DiagnosticSeverity
	 * @generated
	 */
	EEnum getDiagnosticSeverity();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.metadata.SerializationFormat <em>Serialization Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Serialization Format</em>'.
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @generated
	 */
	EEnum getSerializationFormat();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.metadata.TypeStrategy <em>Type Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Type Strategy</em>'.
	 * @see org.eclipse.fennec.model.metadata.TypeStrategy
	 * @generated
	 */
	EEnum getTypeStrategy();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.metadata.IdStrategy <em>Id Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Id Strategy</em>'.
	 * @see org.eclipse.fennec.model.metadata.IdStrategy
	 * @generated
	 */
	EEnum getIdStrategy();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.metadata.IdKeyMode <em>Id Key Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Id Key Mode</em>'.
	 * @see org.eclipse.fennec.model.metadata.IdKeyMode
	 * @generated
	 */
	EEnum getIdKeyMode();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.metadata.SuperTypeSelection <em>Super Type Selection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Super Type Selection</em>'.
	 * @see org.eclipse.fennec.model.metadata.SuperTypeSelection
	 * @generated
	 */
	EEnum getSuperTypeSelection();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.metadata.EnumSerializationStrategy <em>Enum Serialization Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Enum Serialization Strategy</em>'.
	 * @see org.eclipse.fennec.model.metadata.EnumSerializationStrategy
	 * @generated
	 */
	EEnum getEnumSerializationStrategy();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MetadataFactory getMetadataFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.MetadataDiagnosticImpl <em>Diagnostic</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataDiagnosticImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getMetadataDiagnostic()
		 * @generated
		 */
		EClass METADATA_DIAGNOSTIC = eINSTANCE.getMetadataDiagnostic();

		/**
		 * The meta object literal for the '<em><b>Severity</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute METADATA_DIAGNOSTIC__SEVERITY = eINSTANCE.getMetadataDiagnostic_Severity();

		/**
		 * The meta object literal for the '<em><b>Message</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute METADATA_DIAGNOSTIC__MESSAGE = eINSTANCE.getMetadataDiagnostic_Message();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute METADATA_DIAGNOSTIC__KEY = eINSTANCE.getMetadataDiagnostic_Key();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.DiagnosticContainer <em>Diagnostic Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.DiagnosticContainer
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getDiagnosticContainer()
		 * @generated
		 */
		EClass DIAGNOSTIC_CONTAINER = eINSTANCE.getDiagnosticContainer();

		/**
		 * The meta object literal for the '<em><b>Diagnostics</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DIAGNOSTIC_CONTAINER__DIAGNOSTICS = eINSTANCE.getDiagnosticContainer_Diagnostics();

		/**
		 * The meta object literal for the '<em><b>All Diagnostics</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DIAGNOSTIC_CONTAINER__ALL_DIAGNOSTICS = eINSTANCE.getDiagnosticContainer_AllDiagnostics();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl <em>Base Type Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseTypeConfig()
		 * @generated
		 */
		EClass BASE_TYPE_CONFIG = eINSTANCE.getBaseTypeConfig();

		/**
		 * The meta object literal for the '<em><b>Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_TYPE_CONFIG__FORMAT = eINSTANCE.getBaseTypeConfig_Format();

		/**
		 * The meta object literal for the '<em><b>Strategy</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_TYPE_CONFIG__STRATEGY = eINSTANCE.getBaseTypeConfig_Strategy();

		/**
		 * The meta object literal for the '<em><b>Type Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_TYPE_CONFIG__TYPE_KEY = eINSTANCE.getBaseTypeConfig_TypeKey();

		/**
		 * The meta object literal for the '<em><b>Schema Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_TYPE_CONFIG__SCHEMA_KEY = eINSTANCE.getBaseTypeConfig_SchemaKey();

		/**
		 * The meta object literal for the '<em><b>Name Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_TYPE_CONFIG__NAME_KEY = eINSTANCE.getBaseTypeConfig_NameKey();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl <em>Base Id Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseIdConfig()
		 * @generated
		 */
		EClass BASE_ID_CONFIG = eINSTANCE.getBaseIdConfig();

		/**
		 * The meta object literal for the '<em><b>Strategy</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_ID_CONFIG__STRATEGY = eINSTANCE.getBaseIdConfig_Strategy();

		/**
		 * The meta object literal for the '<em><b>Key Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_ID_CONFIG__KEY_MODE = eINSTANCE.getBaseIdConfig_KeyMode();

		/**
		 * The meta object literal for the '<em><b>Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_ID_CONFIG__FORMAT = eINSTANCE.getBaseIdConfig_Format();

		/**
		 * The meta object literal for the '<em><b>Id Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_ID_CONFIG__ID_KEY = eINSTANCE.getBaseIdConfig_IdKey();

		/**
		 * The meta object literal for the '<em><b>Separator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_ID_CONFIG__SEPARATOR = eINSTANCE.getBaseIdConfig_Separator();

		/**
		 * The meta object literal for the '<em><b>On Top</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_ID_CONFIG__ON_TOP = eINSTANCE.getBaseIdConfig_OnTop();

		/**
		 * The meta object literal for the '<em><b>Serialize Separator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_ID_CONFIG__SERIALIZE_SEPARATOR = eINSTANCE.getBaseIdConfig_SerializeSeparator();

		/**
		 * The meta object literal for the '<em><b>Separator Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_ID_CONFIG__SEPARATOR_KEY = eINSTANCE.getBaseIdConfig_SeparatorKey();

		/**
		 * The meta object literal for the '<em><b>Value Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_ID_CONFIG__VALUE_KEY = eINSTANCE.getBaseIdConfig_ValueKey();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.BaseReferenceConfigImpl <em>Base Reference Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.BaseReferenceConfigImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseReferenceConfig()
		 * @generated
		 */
		EClass BASE_REFERENCE_CONFIG = eINSTANCE.getBaseReferenceConfig();

		/**
		 * The meta object literal for the '<em><b>Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_REFERENCE_CONFIG__FORMAT = eINSTANCE.getBaseReferenceConfig_Format();

		/**
		 * The meta object literal for the '<em><b>Type Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_REFERENCE_CONFIG__TYPE_KEY = eINSTANCE.getBaseReferenceConfig_TypeKey();

		/**
		 * The meta object literal for the '<em><b>Ref Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_REFERENCE_CONFIG__REF_KEY = eINSTANCE.getBaseReferenceConfig_RefKey();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.BaseSuperTypeConfigImpl <em>Base Super Type Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.BaseSuperTypeConfigImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseSuperTypeConfig()
		 * @generated
		 */
		EClass BASE_SUPER_TYPE_CONFIG = eINSTANCE.getBaseSuperTypeConfig();

		/**
		 * The meta object literal for the '<em><b>Enabled</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_SUPER_TYPE_CONFIG__ENABLED = eINSTANCE.getBaseSuperTypeConfig_Enabled();

		/**
		 * The meta object literal for the '<em><b>Selection</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_SUPER_TYPE_CONFIG__SELECTION = eINSTANCE.getBaseSuperTypeConfig_Selection();

		/**
		 * The meta object literal for the '<em><b>Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_SUPER_TYPE_CONFIG__FORMAT = eINSTANCE.getBaseSuperTypeConfig_Format();

		/**
		 * The meta object literal for the '<em><b>As Array</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_SUPER_TYPE_CONFIG__AS_ARRAY = eINSTANCE.getBaseSuperTypeConfig_AsArray();

		/**
		 * The meta object literal for the '<em><b>Separator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_SUPER_TYPE_CONFIG__SEPARATOR = eINSTANCE.getBaseSuperTypeConfig_Separator();

		/**
		 * The meta object literal for the '<em><b>Super Type Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY = eINSTANCE.getBaseSuperTypeConfig_SuperTypeKey();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.BaseFeatureConfigImpl <em>Base Feature Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.BaseFeatureConfigImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getBaseFeatureConfig()
		 * @generated
		 */
		EClass BASE_FEATURE_CONFIG = eINSTANCE.getBaseFeatureConfig();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__KEY = eINSTANCE.getBaseFeatureConfig_Key();

		/**
		 * The meta object literal for the '<em><b>Ignore</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__IGNORE = eINSTANCE.getBaseFeatureConfig_Ignore();

		/**
		 * The meta object literal for the '<em><b>Ignore Read</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__IGNORE_READ = eINSTANCE.getBaseFeatureConfig_IgnoreRead();

		/**
		 * The meta object literal for the '<em><b>Ignore Write</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__IGNORE_WRITE = eINSTANCE.getBaseFeatureConfig_IgnoreWrite();

		/**
		 * The meta object literal for the '<em><b>Force Read</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__FORCE_READ = eINSTANCE.getBaseFeatureConfig_ForceRead();

		/**
		 * The meta object literal for the '<em><b>Force Write</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__FORCE_WRITE = eINSTANCE.getBaseFeatureConfig_ForceWrite();

		/**
		 * The meta object literal for the '<em><b>Serialize Null</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__SERIALIZE_NULL = eINSTANCE.getBaseFeatureConfig_SerializeNull();

		/**
		 * The meta object literal for the '<em><b>Serialize Empty</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__SERIALIZE_EMPTY = eINSTANCE.getBaseFeatureConfig_SerializeEmpty();

		/**
		 * The meta object literal for the '<em><b>Serialize Defaults</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__SERIALIZE_DEFAULTS = eINSTANCE.getBaseFeatureConfig_SerializeDefaults();

		/**
		 * The meta object literal for the '<em><b>Enum Serialization</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BASE_FEATURE_CONFIG__ENUM_SERIALIZATION = eINSTANCE.getBaseFeatureConfig_EnumSerialization();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.AspectImpl <em>Aspect</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.AspectImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getAspect()
		 * @generated
		 */
		EClass ASPECT = eINSTANCE.getAspect();

		/**
		 * The meta object literal for the '<em><b>Type Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASPECT__TYPE_ID = eINSTANCE.getAspect_TypeId();

		/**
		 * The meta object literal for the '<em><b>Diagnostics</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ASPECT__DIAGNOSTICS = eINSTANCE.getAspect_Diagnostics();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.PackageAspectImpl <em>Package Aspect</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.PackageAspectImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getPackageAspect()
		 * @generated
		 */
		EClass PACKAGE_ASPECT = eINSTANCE.getPackageAspect();

		/**
		 * The meta object literal for the '<em><b>Package Metadata</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE_ASPECT__PACKAGE_METADATA = eINSTANCE.getPackageAspect_PackageMetadata();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.ClassAspectImpl <em>Class Aspect</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.ClassAspectImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getClassAspect()
		 * @generated
		 */
		EClass CLASS_ASPECT = eINSTANCE.getClassAspect();

		/**
		 * The meta object literal for the '<em><b>Class Metadata</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_ASPECT__CLASS_METADATA = eINSTANCE.getClassAspect_ClassMetadata();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.FeatureAspectImpl <em>Feature Aspect</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.FeatureAspectImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getFeatureAspect()
		 * @generated
		 */
		EClass FEATURE_ASPECT = eINSTANCE.getFeatureAspect();

		/**
		 * The meta object literal for the '<em><b>Feature Metadata</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_ASPECT__FEATURE_METADATA = eINSTANCE.getFeatureAspect_FeatureMetadata();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.PackageProfileImpl <em>Package Profile</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.PackageProfileImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getPackageProfile()
		 * @generated
		 */
		EClass PACKAGE_PROFILE = eINSTANCE.getPackageProfile();

		/**
		 * The meta object literal for the '<em><b>Type Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE_PROFILE__TYPE_ID = eINSTANCE.getPackageProfile_TypeId();

		/**
		 * The meta object literal for the '<em><b>Class Profiles</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE_PROFILE__CLASS_PROFILES = eINSTANCE.getPackageProfile_ClassProfiles();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.ClassProfileImpl <em>Class Profile</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.ClassProfileImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getClassProfile()
		 * @generated
		 */
		EClass CLASS_PROFILE = eINSTANCE.getClassProfile();

		/**
		 * The meta object literal for the '<em><b>EClass</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_PROFILE__ECLASS = eINSTANCE.getClassProfile_EClass();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl <em>Package Metadata</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getPackageMetadata()
		 * @generated
		 */
		EClass PACKAGE_METADATA = eINSTANCE.getPackageMetadata();

		/**
		 * The meta object literal for the '<em><b>EPackage</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE_METADATA__EPACKAGE = eINSTANCE.getPackageMetadata_EPackage();

		/**
		 * The meta object literal for the '<em><b>Ns URI</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE_METADATA__NS_URI = eINSTANCE.getPackageMetadata_NsURI();

		/**
		 * The meta object literal for the '<em><b>Classes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE_METADATA__CLASSES = eINSTANCE.getPackageMetadata_Classes();

		/**
		 * The meta object literal for the '<em><b>Aspects</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE_METADATA__ASPECTS = eINSTANCE.getPackageMetadata_Aspects();

		/**
		 * The meta object literal for the '<em><b>Profiles</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE_METADATA__PROFILES = eINSTANCE.getPackageMetadata_Profiles();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.ClassMetadataImpl <em>Class Metadata</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.ClassMetadataImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getClassMetadata()
		 * @generated
		 */
		EClass CLASS_METADATA = eINSTANCE.getClassMetadata();

		/**
		 * The meta object literal for the '<em><b>Package</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_METADATA__PACKAGE = eINSTANCE.getClassMetadata_Package();

		/**
		 * The meta object literal for the '<em><b>EClass</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_METADATA__ECLASS = eINSTANCE.getClassMetadata_EClass();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_METADATA__NAME = eINSTANCE.getClassMetadata_Name();

		/**
		 * The meta object literal for the '<em><b>Classifier ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_METADATA__CLASSIFIER_ID = eINSTANCE.getClassMetadata_ClassifierID();

		/**
		 * The meta object literal for the '<em><b>Type URI</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_METADATA__TYPE_URI = eINSTANCE.getClassMetadata_TypeURI();

		/**
		 * The meta object literal for the '<em><b>Features</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_METADATA__FEATURES = eINSTANCE.getClassMetadata_Features();

		/**
		 * The meta object literal for the '<em><b>Super Types</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_METADATA__SUPER_TYPES = eINSTANCE.getClassMetadata_SuperTypes();

		/**
		 * The meta object literal for the '<em><b>All Super Types</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_METADATA__ALL_SUPER_TYPES = eINSTANCE.getClassMetadata_AllSuperTypes();

		/**
		 * The meta object literal for the '<em><b>Id Features</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_METADATA__ID_FEATURES = eINSTANCE.getClassMetadata_IdFeatures();

		/**
		 * The meta object literal for the '<em><b>Has Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_METADATA__HAS_ID = eINSTANCE.getClassMetadata_HasId();

		/**
		 * The meta object literal for the '<em><b>Aspects</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_METADATA__ASPECTS = eINSTANCE.getClassMetadata_Aspects();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.FeatureMetadataImpl <em>Feature Metadata</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.FeatureMetadataImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getFeatureMetadata()
		 * @generated
		 */
		EClass FEATURE_METADATA = eINSTANCE.getFeatureMetadata();

		/**
		 * The meta object literal for the '<em><b>Class Metadata</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_METADATA__CLASS_METADATA = eINSTANCE.getFeatureMetadata_ClassMetadata();

		/**
		 * The meta object literal for the '<em><b>EFeature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_METADATA__EFEATURE = eINSTANCE.getFeatureMetadata_EFeature();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_METADATA__NAME = eINSTANCE.getFeatureMetadata_Name();

		/**
		 * The meta object literal for the '<em><b>Extended Meta Data Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_METADATA__EXTENDED_META_DATA_NAME = eINSTANCE.getFeatureMetadata_ExtendedMetaDataName();

		/**
		 * The meta object literal for the '<em><b>Feature ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_METADATA__FEATURE_ID = eINSTANCE.getFeatureMetadata_FeatureID();

		/**
		 * The meta object literal for the '<em><b>Aspects</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_METADATA__ASPECTS = eINSTANCE.getFeatureMetadata_Aspects();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.AttributeMetadataImpl <em>Attribute Metadata</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.AttributeMetadataImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getAttributeMetadata()
		 * @generated
		 */
		EClass ATTRIBUTE_METADATA = eINSTANCE.getAttributeMetadata();

		/**
		 * The meta object literal for the '<em><b>EAttribute</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTRIBUTE_METADATA__EATTRIBUTE = eINSTANCE.getAttributeMetadata_EAttribute();

		/**
		 * The meta object literal for the '<em><b>Is Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE_METADATA__IS_ID = eINSTANCE.getAttributeMetadata_IsId();

		/**
		 * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE_METADATA__DEFAULT_VALUE = eINSTANCE.getAttributeMetadata_DefaultValue();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.ReferenceMetadataImpl <em>Reference Metadata</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.ReferenceMetadataImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getReferenceMetadata()
		 * @generated
		 */
		EClass REFERENCE_METADATA = eINSTANCE.getReferenceMetadata();

		/**
		 * The meta object literal for the '<em><b>EReference</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFERENCE_METADATA__EREFERENCE = eINSTANCE.getReferenceMetadata_EReference();

		/**
		 * The meta object literal for the '<em><b>Containment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_METADATA__CONTAINMENT = eINSTANCE.getReferenceMetadata_Containment();

		/**
		 * The meta object literal for the '<em><b>Target Class Metadata</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFERENCE_METADATA__TARGET_CLASS_METADATA = eINSTANCE.getReferenceMetadata_TargetClassMetadata();

		/**
		 * The meta object literal for the '<em><b>Opposite Metadata</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFERENCE_METADATA__OPPOSITE_METADATA = eINSTANCE.getReferenceMetadata_OppositeMetadata();

		/**
		 * The meta object literal for the '<em><b>Has Bidirectional</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_METADATA__HAS_BIDIRECTIONAL = eINSTANCE.getReferenceMetadata_HasBidirectional();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.impl.MetadataRegistryImpl <em>Registry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataRegistryImpl
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getMetadataRegistry()
		 * @generated
		 */
		EClass METADATA_REGISTRY = eINSTANCE.getMetadataRegistry();

		/**
		 * The meta object literal for the '<em><b>Packages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference METADATA_REGISTRY__PACKAGES = eINSTANCE.getMetadataRegistry_Packages();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.DiagnosticSeverity <em>Diagnostic Severity</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.DiagnosticSeverity
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getDiagnosticSeverity()
		 * @generated
		 */
		EEnum DIAGNOSTIC_SEVERITY = eINSTANCE.getDiagnosticSeverity();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.SerializationFormat <em>Serialization Format</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.SerializationFormat
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getSerializationFormat()
		 * @generated
		 */
		EEnum SERIALIZATION_FORMAT = eINSTANCE.getSerializationFormat();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.TypeStrategy <em>Type Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.TypeStrategy
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getTypeStrategy()
		 * @generated
		 */
		EEnum TYPE_STRATEGY = eINSTANCE.getTypeStrategy();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.IdStrategy <em>Id Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.IdStrategy
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getIdStrategy()
		 * @generated
		 */
		EEnum ID_STRATEGY = eINSTANCE.getIdStrategy();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.IdKeyMode <em>Id Key Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.IdKeyMode
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getIdKeyMode()
		 * @generated
		 */
		EEnum ID_KEY_MODE = eINSTANCE.getIdKeyMode();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.SuperTypeSelection <em>Super Type Selection</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.SuperTypeSelection
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getSuperTypeSelection()
		 * @generated
		 */
		EEnum SUPER_TYPE_SELECTION = eINSTANCE.getSuperTypeSelection();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.EnumSerializationStrategy <em>Enum Serialization Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.EnumSerializationStrategy
		 * @see org.eclipse.fennec.model.metadata.impl.MetadataPackageImpl#getEnumSerializationStrategy()
		 * @generated
		 */
		EEnum ENUM_SERIALIZATION_STRATEGY = eINSTANCE.getEnumSerializationStrategy();

	}

} //MetadataPackage
