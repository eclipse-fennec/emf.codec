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
package org.eclipse.fennec.codec.metadata.model.codec;


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
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecFactory
 * @model kind="package"
 *        annotation="Version value='1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = CodecPackage.eNS_URI, fingerprint = "fp1:0b9de76be976df8df617cf48a03107d37591210ec08f4150340bf1b7dc02daca", genModel = "/model/codec.genmodel", genModelSourceLocations = {"model/codec.genmodel","org.eclipse.fennec.codec.metadata/model/codec.genmodel"}, ecore = "/model/codec.ecore", ecoreSourceLocations = "/model/codec.ecore")
public interface CodecPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "codec";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/codec/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "codec";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CodecPackage eINSTANCE = org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseTypeConfigImpl <em>Base Type Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseTypeConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseTypeConfig()
	 * @generated
	 */
	int BASE_TYPE_CONFIG = 0;

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
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseIdConfigImpl <em>Base Id Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseIdConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseIdConfig()
	 * @generated
	 */
	int BASE_ID_CONFIG = 1;

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
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseReferenceConfigImpl <em>Base Reference Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseReferenceConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseReferenceConfig()
	 * @generated
	 */
	int BASE_REFERENCE_CONFIG = 2;

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
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl <em>Base Super Type Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseSuperTypeConfig()
	 * @generated
	 */
	int BASE_SUPER_TYPE_CONFIG = 3;

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
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseFeatureConfigImpl <em>Base Feature Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseFeatureConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseFeatureConfig()
	 * @generated
	 */
	int BASE_FEATURE_CONFIG = 4;

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
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl <em>Type Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getTypeSerializationConfig()
	 * @generated
	 */
	int TYPE_SERIALIZATION_CONFIG = 5;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__FORMAT = BASE_TYPE_CONFIG__FORMAT;

	/**
	 * The feature id for the '<em><b>Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__STRATEGY = BASE_TYPE_CONFIG__STRATEGY;

	/**
	 * The feature id for the '<em><b>Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__TYPE_KEY = BASE_TYPE_CONFIG__TYPE_KEY;

	/**
	 * The feature id for the '<em><b>Schema Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__SCHEMA_KEY = BASE_TYPE_CONFIG__SCHEMA_KEY;

	/**
	 * The feature id for the '<em><b>Name Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__NAME_KEY = BASE_TYPE_CONFIG__NAME_KEY;

	/**
	 * The feature id for the '<em><b>Map Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__MAP_ID = BASE_TYPE_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Discriminator Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH = BASE_TYPE_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Discriminator Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE = BASE_TYPE_CONFIG_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Fingerprint Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__FINGERPRINT_MODE = BASE_TYPE_CONFIG_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Fingerprint Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__FINGERPRINT_KEY = BASE_TYPE_CONFIG_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Strategy Scope</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__STRATEGY_SCOPE = BASE_TYPE_CONFIG_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Format Scope</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__FORMAT_SCOPE = BASE_TYPE_CONFIG_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Type Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG_FEATURE_COUNT = BASE_TYPE_CONFIG_FEATURE_COUNT + 7;

	/**
	 * The number of operations of the '<em>Type Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG_OPERATION_COUNT = BASE_TYPE_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl <em>Id Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getIdSerializationConfig()
	 * @generated
	 */
	int ID_SERIALIZATION_CONFIG = 6;

	/**
	 * The feature id for the '<em><b>Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__STRATEGY = BASE_ID_CONFIG__STRATEGY;

	/**
	 * The feature id for the '<em><b>Key Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__KEY_MODE = BASE_ID_CONFIG__KEY_MODE;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__FORMAT = BASE_ID_CONFIG__FORMAT;

	/**
	 * The feature id for the '<em><b>Id Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ID_KEY = BASE_ID_CONFIG__ID_KEY;

	/**
	 * The feature id for the '<em><b>Separator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__SEPARATOR = BASE_ID_CONFIG__SEPARATOR;

	/**
	 * The feature id for the '<em><b>On Top</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ON_TOP = BASE_ID_CONFIG__ON_TOP;

	/**
	 * The feature id for the '<em><b>Serialize Separator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__SERIALIZE_SEPARATOR = BASE_ID_CONFIG__SERIALIZE_SEPARATOR;

	/**
	 * The feature id for the '<em><b>Separator Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__SEPARATOR_KEY = BASE_ID_CONFIG__SEPARATOR_KEY;

	/**
	 * The feature id for the '<em><b>Value Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__VALUE_KEY = BASE_ID_CONFIG__VALUE_KEY;

	/**
	 * The feature id for the '<em><b>Id Features</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ID_FEATURES = BASE_ID_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Id Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ID_VALUE_WRITER_NAME = BASE_ID_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Id Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ID_VALUE_READER_NAME = BASE_ID_CONFIG_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Strategy Scope</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__STRATEGY_SCOPE = BASE_ID_CONFIG_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Format Scope</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__FORMAT_SCOPE = BASE_ID_CONFIG_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Id Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG_FEATURE_COUNT = BASE_ID_CONFIG_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Id Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG_OPERATION_COUNT = BASE_ID_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceSerializationConfigImpl <em>Reference Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getReferenceSerializationConfig()
	 * @generated
	 */
	int REFERENCE_SERIALIZATION_CONFIG = 7;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__FORMAT = BASE_REFERENCE_CONFIG__FORMAT;

	/**
	 * The feature id for the '<em><b>Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__TYPE_KEY = BASE_REFERENCE_CONFIG__TYPE_KEY;

	/**
	 * The feature id for the '<em><b>Ref Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__REF_KEY = BASE_REFERENCE_CONFIG__REF_KEY;

	/**
	 * The feature id for the '<em><b>Include Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__INCLUDE_TYPE = BASE_REFERENCE_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Expand</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__EXPAND = BASE_REFERENCE_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Reference Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG_FEATURE_COUNT = BASE_REFERENCE_CONFIG_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Reference Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG_OPERATION_COUNT = BASE_REFERENCE_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.SuperTypeSerializationConfigImpl <em>Super Type Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.SuperTypeSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getSuperTypeSerializationConfig()
	 * @generated
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG = 8;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__ENABLED = BASE_SUPER_TYPE_CONFIG__ENABLED;

	/**
	 * The feature id for the '<em><b>Selection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__SELECTION = BASE_SUPER_TYPE_CONFIG__SELECTION;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__FORMAT = BASE_SUPER_TYPE_CONFIG__FORMAT;

	/**
	 * The feature id for the '<em><b>As Array</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__AS_ARRAY = BASE_SUPER_TYPE_CONFIG__AS_ARRAY;

	/**
	 * The feature id for the '<em><b>Separator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__SEPARATOR = BASE_SUPER_TYPE_CONFIG__SEPARATOR;

	/**
	 * The feature id for the '<em><b>Super Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__SUPER_TYPE_KEY = BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY;

	/**
	 * The feature id for the '<em><b>Use Smart Compression</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__USE_SMART_COMPRESSION = BASE_SUPER_TYPE_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Super Type Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG_FEATURE_COUNT = BASE_SUPER_TYPE_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Super Type Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG_OPERATION_COUNT = BASE_SUPER_TYPE_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl <em>Feature Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFeatureSerializationConfig()
	 * @generated
	 */
	int FEATURE_SERIALIZATION_CONFIG = 9;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__KEY = BASE_FEATURE_CONFIG__KEY;

	/**
	 * The feature id for the '<em><b>Ignore</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__IGNORE = BASE_FEATURE_CONFIG__IGNORE;

	/**
	 * The feature id for the '<em><b>Ignore Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__IGNORE_READ = BASE_FEATURE_CONFIG__IGNORE_READ;

	/**
	 * The feature id for the '<em><b>Ignore Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__IGNORE_WRITE = BASE_FEATURE_CONFIG__IGNORE_WRITE;

	/**
	 * The feature id for the '<em><b>Force Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__FORCE_READ = BASE_FEATURE_CONFIG__FORCE_READ;

	/**
	 * The feature id for the '<em><b>Force Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__FORCE_WRITE = BASE_FEATURE_CONFIG__FORCE_WRITE;

	/**
	 * The feature id for the '<em><b>Serialize Null</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__SERIALIZE_NULL = BASE_FEATURE_CONFIG__SERIALIZE_NULL;

	/**
	 * The feature id for the '<em><b>Serialize Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__SERIALIZE_EMPTY = BASE_FEATURE_CONFIG__SERIALIZE_EMPTY;

	/**
	 * The feature id for the '<em><b>Serialize Defaults</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__SERIALIZE_DEFAULTS = BASE_FEATURE_CONFIG__SERIALIZE_DEFAULTS;

	/**
	 * The feature id for the '<em><b>Enum Serialization</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__ENUM_SERIALIZATION = BASE_FEATURE_CONFIG__ENUM_SERIALIZATION;

	/**
	 * The feature id for the '<em><b>Feature Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__FEATURE_NAME = BASE_FEATURE_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__VALUE_WRITER_NAME = BASE_FEATURE_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__VALUE_READER_NAME = BASE_FEATURE_CONFIG_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Expand</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__EXPAND = BASE_FEATURE_CONFIG_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Reference Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG = BASE_FEATURE_CONFIG_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG = BASE_FEATURE_CONFIG_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Feature Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG_FEATURE_COUNT = BASE_FEATURE_CONFIG_FEATURE_COUNT + 6;

	/**
	 * The number of operations of the '<em>Feature Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG_OPERATION_COUNT = BASE_FEATURE_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl <em>Class Codec Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getClassCodecAspect()
	 * @generated
	 */
	int CLASS_CODEC_ASPECT = 10;

	/**
	 * The feature id for the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__TYPE_CONFIG = 0;

	/**
	 * The feature id for the '<em><b>Id Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__ID_CONFIG = 1;

	/**
	 * The feature id for the '<em><b>Super Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG = 2;

	/**
	 * The feature id for the '<em><b>Inherit From Parent</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__INHERIT_FROM_PARENT = 3;

	/**
	 * The feature id for the '<em><b>Discriminator Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__DISCRIMINATOR_VALUE = 4;

	/**
	 * The feature id for the '<em><b>Strict On Unknown</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__STRICT_ON_UNKNOWN = 5;

	/**
	 * The feature id for the '<em><b>Strict On Missing</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__STRICT_ON_MISSING = 6;

	/**
	 * The feature id for the '<em><b>Metadata Merge</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__METADATA_MERGE = 7;

	/**
	 * The feature id for the '<em><b>Metadata Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__METADATA_KEY = 8;

	/**
	 * The number of structural features of the '<em>Class Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT_FEATURE_COUNT = 9;

	/**
	 * The number of operations of the '<em>Class Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl <em>Feature Codec Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFeatureCodecAspect()
	 * @generated
	 */
	int FEATURE_CODEC_ASPECT = 11;

	/**
	 * The feature id for the '<em><b>Effective Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__EFFECTIVE_KEY = 0;

	/**
	 * The feature id for the '<em><b>Ignore</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__IGNORE = 1;

	/**
	 * The feature id for the '<em><b>Ignore Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__IGNORE_READ = 2;

	/**
	 * The feature id for the '<em><b>Ignore Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__IGNORE_WRITE = 3;

	/**
	 * The feature id for the '<em><b>Force Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__FORCE_READ = 4;

	/**
	 * The feature id for the '<em><b>Force Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__FORCE_WRITE = 5;

	/**
	 * The feature id for the '<em><b>Serialize Null</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__SERIALIZE_NULL = 6;

	/**
	 * The feature id for the '<em><b>Serialize Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY = 7;

	/**
	 * The feature id for the '<em><b>Serialize Defaults</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS = 8;

	/**
	 * The feature id for the '<em><b>Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME = 9;

	/**
	 * The feature id for the '<em><b>Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__VALUE_READER_NAME = 10;

	/**
	 * The feature id for the '<em><b>Enum Serialization</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION = 11;

	/**
	 * The feature id for the '<em><b>Date Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__DATE_FORMAT = 12;

	/**
	 * The number of structural features of the '<em>Feature Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT_FEATURE_COUNT = 13;

	/**
	 * The number of operations of the '<em>Feature Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl <em>Reference Codec Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getReferenceCodecAspect()
	 * @generated
	 */
	int REFERENCE_CODEC_ASPECT = 12;

	/**
	 * The feature id for the '<em><b>Effective Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__EFFECTIVE_KEY = FEATURE_CODEC_ASPECT__EFFECTIVE_KEY;

	/**
	 * The feature id for the '<em><b>Ignore</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__IGNORE = FEATURE_CODEC_ASPECT__IGNORE;

	/**
	 * The feature id for the '<em><b>Ignore Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__IGNORE_READ = FEATURE_CODEC_ASPECT__IGNORE_READ;

	/**
	 * The feature id for the '<em><b>Ignore Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__IGNORE_WRITE = FEATURE_CODEC_ASPECT__IGNORE_WRITE;

	/**
	 * The feature id for the '<em><b>Force Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__FORCE_READ = FEATURE_CODEC_ASPECT__FORCE_READ;

	/**
	 * The feature id for the '<em><b>Force Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__FORCE_WRITE = FEATURE_CODEC_ASPECT__FORCE_WRITE;

	/**
	 * The feature id for the '<em><b>Serialize Null</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__SERIALIZE_NULL = FEATURE_CODEC_ASPECT__SERIALIZE_NULL;

	/**
	 * The feature id for the '<em><b>Serialize Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__SERIALIZE_EMPTY = FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY;

	/**
	 * The feature id for the '<em><b>Serialize Defaults</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__SERIALIZE_DEFAULTS = FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS;

	/**
	 * The feature id for the '<em><b>Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__VALUE_WRITER_NAME = FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME;

	/**
	 * The feature id for the '<em><b>Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__VALUE_READER_NAME = FEATURE_CODEC_ASPECT__VALUE_READER_NAME;

	/**
	 * The feature id for the '<em><b>Enum Serialization</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__ENUM_SERIALIZATION = FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION;

	/**
	 * The feature id for the '<em><b>Date Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__DATE_FORMAT = FEATURE_CODEC_ASPECT__DATE_FORMAT;

	/**
	 * The feature id for the '<em><b>Reference Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG = FEATURE_CODEC_ASPECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__TYPE_CONFIG = FEATURE_CODEC_ASPECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Inherit Type From Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__INHERIT_TYPE_FROM_TARGET = FEATURE_CODEC_ASPECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Expand</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__EXPAND = FEATURE_CODEC_ASPECT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Reference Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT_FEATURE_COUNT = FEATURE_CODEC_ASPECT_FEATURE_COUNT + 4;

	/**
	 * The number of operations of the '<em>Reference Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT_OPERATION_COUNT = FEATURE_CODEC_ASPECT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageProfileImpl <em>Package Profile</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageProfileImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getCodecPackageProfile()
	 * @generated
	 */
	int CODEC_PACKAGE_PROFILE = 13;

	/**
	 * The feature id for the '<em><b>Class Profiles</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_PACKAGE_PROFILE__CLASS_PROFILES = 0;

	/**
	 * The number of structural features of the '<em>Package Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_PACKAGE_PROFILE_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Package Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_PACKAGE_PROFILE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl <em>Class Profile</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getCodecClassProfile()
	 * @generated
	 */
	int CODEC_CLASS_PROFILE = 14;

	/**
	 * The feature id for the '<em><b>EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__ECLASS = 0;

	/**
	 * The feature id for the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__TYPE_CONFIG = 1;

	/**
	 * The feature id for the '<em><b>Id Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__ID_CONFIG = 2;

	/**
	 * The feature id for the '<em><b>Super Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG = 3;

	/**
	 * The feature id for the '<em><b>Feature Configs</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__FEATURE_CONFIGS = 4;

	/**
	 * The number of structural features of the '<em>Class Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Class Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl <em>Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getCodecConfig()
	 * @generated
	 */
	int CODEC_CONFIG = 15;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__FORMAT = 0;

	/**
	 * The feature id for the '<em><b>Use Numeric Ids</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__USE_NUMERIC_IDS = 1;

	/**
	 * The feature id for the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__TYPE_CONFIG = 2;

	/**
	 * The feature id for the '<em><b>Containment Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG = 3;

	/**
	 * The feature id for the '<em><b>Reference Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__REFERENCE_TYPE_CONFIG = 4;

	/**
	 * The feature id for the '<em><b>Id Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__ID_CONFIG = 5;

	/**
	 * The feature id for the '<em><b>Reference Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__REFERENCE_CONFIG = 6;

	/**
	 * The feature id for the '<em><b>Super Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__SUPER_TYPE_CONFIG = 7;

	/**
	 * The feature id for the '<em><b>Feature Configs</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__FEATURE_CONFIGS = 8;

	/**
	 * The feature id for the '<em><b>Expand</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__EXPAND = 9;

	/**
	 * The feature id for the '<em><b>Expand Depth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__EXPAND_DEPTH = 10;

	/**
	 * The feature id for the '<em><b>Expand Ignore Bidirectional</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__EXPAND_IGNORE_BIDIRECTIONAL = 11;

	/**
	 * The feature id for the '<em><b>Serialize Null</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__SERIALIZE_NULL = 12;

	/**
	 * The feature id for the '<em><b>Serialize Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__SERIALIZE_EMPTY = 13;

	/**
	 * The feature id for the '<em><b>Serialize Defaults</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__SERIALIZE_DEFAULTS = 14;

	/**
	 * The feature id for the '<em><b>Type Hint Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__TYPE_HINT_MODE = 15;

	/**
	 * The feature id for the '<em><b>Deserialization Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__DESERIALIZATION_MODE = 16;

	/**
	 * The feature id for the '<em><b>Strict On Unknown</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__STRICT_ON_UNKNOWN = 17;

	/**
	 * The feature id for the '<em><b>Strict On Missing</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__STRICT_ON_MISSING = 18;

	/**
	 * The feature id for the '<em><b>Metadata Merge</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__METADATA_MERGE = 19;

	/**
	 * The feature id for the '<em><b>Metadata Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG__METADATA_KEY = 20;

	/**
	 * The number of structural features of the '<em>Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG_FEATURE_COUNT = 21;

	/**
	 * The number of operations of the '<em>Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CONFIG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.StrategyScope <em>Strategy Scope</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getStrategyScope()
	 * @generated
	 */
	int STRATEGY_SCOPE = 16;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode <em>Type Hint Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getTypeHintMode()
	 * @generated
	 */
	int TYPE_HINT_MODE = 17;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode <em>Deserialization Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getDeserializationMode()
	 * @generated
	 */
	int DESERIALIZATION_MODE = 18;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy <em>Fallback Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFallbackStrategy()
	 * @generated
	 */
	int FALLBACK_STRATEGY = 19;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode <em>Fingerprint Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFingerprintMode()
	 * @generated
	 */
	int FINGERPRINT_MODE = 20;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat <em>Serialization Format</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getSerializationFormat()
	 * @generated
	 */
	int SERIALIZATION_FORMAT = 21;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy <em>Type Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getTypeStrategy()
	 * @generated
	 */
	int TYPE_STRATEGY = 22;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.IdStrategy <em>Id Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdStrategy
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getIdStrategy()
	 * @generated
	 */
	int ID_STRATEGY = 23;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode <em>Id Key Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getIdKeyMode()
	 * @generated
	 */
	int ID_KEY_MODE = 24;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection <em>Super Type Selection</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getSuperTypeSelection()
	 * @generated
	 */
	int SUPER_TYPE_SELECTION = 25;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy <em>Enum Serialization Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getEnumSerializationStrategy()
	 * @generated
	 */
	int ENUM_SERIALIZATION_STRATEGY = 26;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig <em>Base Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig
	 * @generated
	 */
	EClass getBaseTypeConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getFormat()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getStrategy <em>Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strategy</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getStrategy()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_Strategy();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getTypeKey <em>Type Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getTypeKey()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_TypeKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getSchemaKey <em>Schema Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getSchemaKey()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_SchemaKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getNameKey <em>Name Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getNameKey()
	 * @see #getBaseTypeConfig()
	 * @generated
	 */
	EAttribute getBaseTypeConfig_NameKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig <em>Base Id Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Id Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig
	 * @generated
	 */
	EClass getBaseIdConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getStrategy <em>Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strategy</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getStrategy()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_Strategy();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getKeyMode <em>Key Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key Mode</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getKeyMode()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_KeyMode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getFormat()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getIdKey <em>Id Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getIdKey()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_IdKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getSeparator <em>Separator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Separator</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getSeparator()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_Separator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#isOnTop <em>On Top</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>On Top</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#isOnTop()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_OnTop();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#isSerializeSeparator <em>Serialize Separator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Separator</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#isSerializeSeparator()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_SerializeSeparator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getSeparatorKey <em>Separator Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Separator Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getSeparatorKey()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_SeparatorKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getValueKey <em>Value Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig#getValueKey()
	 * @see #getBaseIdConfig()
	 * @generated
	 */
	EAttribute getBaseIdConfig_ValueKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig <em>Base Reference Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Reference Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig
	 * @generated
	 */
	EClass getBaseReferenceConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getFormat()
	 * @see #getBaseReferenceConfig()
	 * @generated
	 */
	EAttribute getBaseReferenceConfig_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getTypeKey <em>Type Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getTypeKey()
	 * @see #getBaseReferenceConfig()
	 * @generated
	 */
	EAttribute getBaseReferenceConfig_TypeKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getRefKey <em>Ref Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getRefKey()
	 * @see #getBaseReferenceConfig()
	 * @generated
	 */
	EAttribute getBaseReferenceConfig_RefKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig <em>Base Super Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Super Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig
	 * @generated
	 */
	EClass getBaseSuperTypeConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#isEnabled <em>Enabled</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enabled</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#isEnabled()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_Enabled();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#getSelection <em>Selection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Selection</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#getSelection()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_Selection();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#getFormat()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#isAsArray <em>As Array</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>As Array</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#isAsArray()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_AsArray();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#getSeparator <em>Separator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Separator</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#getSeparator()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_Separator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#getSuperTypeKey <em>Super Type Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Super Type Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig#getSuperTypeKey()
	 * @see #getBaseSuperTypeConfig()
	 * @generated
	 */
	EAttribute getBaseSuperTypeConfig_SuperTypeKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig <em>Base Feature Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Base Feature Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig
	 * @generated
	 */
	EClass getBaseFeatureConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getKey()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_Key();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getIgnore <em>Ignore</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getIgnore()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_Ignore();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getIgnoreRead <em>Ignore Read</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore Read</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getIgnoreRead()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_IgnoreRead();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getIgnoreWrite <em>Ignore Write</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore Write</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getIgnoreWrite()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_IgnoreWrite();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getForceRead <em>Force Read</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Force Read</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getForceRead()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_ForceRead();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getForceWrite <em>Force Write</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Force Write</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getForceWrite()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_ForceWrite();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getSerializeNull <em>Serialize Null</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Null</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getSerializeNull()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_SerializeNull();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getSerializeEmpty <em>Serialize Empty</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Empty</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getSerializeEmpty()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_SerializeEmpty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getSerializeDefaults <em>Serialize Defaults</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Defaults</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getSerializeDefaults()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_SerializeDefaults();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getEnumSerialization <em>Enum Serialization</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enum Serialization</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig#getEnumSerialization()
	 * @see #getBaseFeatureConfig()
	 * @generated
	 */
	EAttribute getBaseFeatureConfig_EnumSerialization();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig <em>Type Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Type Serialization Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig
	 * @generated
	 */
	EClass getTypeSerializationConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getMapId <em>Map Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Id</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getMapId()
	 * @see #getTypeSerializationConfig()
	 * @generated
	 */
	EAttribute getTypeSerializationConfig_MapId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getDiscriminatorPath <em>Discriminator Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Discriminator Path</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getDiscriminatorPath()
	 * @see #getTypeSerializationConfig()
	 * @generated
	 */
	EAttribute getTypeSerializationConfig_DiscriminatorPath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getDiscriminatorValue <em>Discriminator Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Discriminator Value</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getDiscriminatorValue()
	 * @see #getTypeSerializationConfig()
	 * @generated
	 */
	EAttribute getTypeSerializationConfig_DiscriminatorValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getFingerprintMode <em>Fingerprint Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fingerprint Mode</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getFingerprintMode()
	 * @see #getTypeSerializationConfig()
	 * @generated
	 */
	EAttribute getTypeSerializationConfig_FingerprintMode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getFingerprintKey <em>Fingerprint Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fingerprint Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getFingerprintKey()
	 * @see #getTypeSerializationConfig()
	 * @generated
	 */
	EAttribute getTypeSerializationConfig_FingerprintKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getStrategyScope <em>Strategy Scope</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strategy Scope</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getStrategyScope()
	 * @see #getTypeSerializationConfig()
	 * @generated
	 */
	EAttribute getTypeSerializationConfig_StrategyScope();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getFormatScope <em>Format Scope</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format Scope</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getFormatScope()
	 * @see #getTypeSerializationConfig()
	 * @generated
	 */
	EAttribute getTypeSerializationConfig_FormatScope();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig <em>Id Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Id Serialization Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig
	 * @generated
	 */
	EClass getIdSerializationConfig();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdFeatures <em>Id Features</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Id Features</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdFeatures()
	 * @see #getIdSerializationConfig()
	 * @generated
	 */
	EAttribute getIdSerializationConfig_IdFeatures();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdValueWriterName <em>Id Value Writer Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id Value Writer Name</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdValueWriterName()
	 * @see #getIdSerializationConfig()
	 * @generated
	 */
	EAttribute getIdSerializationConfig_IdValueWriterName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdValueReaderName <em>Id Value Reader Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id Value Reader Name</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdValueReaderName()
	 * @see #getIdSerializationConfig()
	 * @generated
	 */
	EAttribute getIdSerializationConfig_IdValueReaderName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getStrategyScope <em>Strategy Scope</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strategy Scope</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getStrategyScope()
	 * @see #getIdSerializationConfig()
	 * @generated
	 */
	EAttribute getIdSerializationConfig_StrategyScope();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getFormatScope <em>Format Scope</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format Scope</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getFormatScope()
	 * @see #getIdSerializationConfig()
	 * @generated
	 */
	EAttribute getIdSerializationConfig_FormatScope();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig <em>Reference Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Reference Serialization Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig
	 * @generated
	 */
	EClass getReferenceSerializationConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig#isIncludeType <em>Include Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Include Type</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig#isIncludeType()
	 * @see #getReferenceSerializationConfig()
	 * @generated
	 */
	EAttribute getReferenceSerializationConfig_IncludeType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig#isExpand <em>Expand</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expand</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig#isExpand()
	 * @see #getReferenceSerializationConfig()
	 * @generated
	 */
	EAttribute getReferenceSerializationConfig_Expand();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig <em>Super Type Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Super Type Serialization Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig
	 * @generated
	 */
	EClass getSuperTypeSerializationConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig#isUseSmartCompression <em>Use Smart Compression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Use Smart Compression</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig#isUseSmartCompression()
	 * @see #getSuperTypeSerializationConfig()
	 * @generated
	 */
	EAttribute getSuperTypeSerializationConfig_UseSmartCompression();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig <em>Feature Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature Serialization Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig
	 * @generated
	 */
	EClass getFeatureSerializationConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getFeatureName <em>Feature Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Feature Name</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getFeatureName()
	 * @see #getFeatureSerializationConfig()
	 * @generated
	 */
	EAttribute getFeatureSerializationConfig_FeatureName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getValueWriterName <em>Value Writer Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Writer Name</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getValueWriterName()
	 * @see #getFeatureSerializationConfig()
	 * @generated
	 */
	EAttribute getFeatureSerializationConfig_ValueWriterName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getValueReaderName <em>Value Reader Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Reader Name</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getValueReaderName()
	 * @see #getFeatureSerializationConfig()
	 * @generated
	 */
	EAttribute getFeatureSerializationConfig_ValueReaderName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getExpand <em>Expand</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expand</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getExpand()
	 * @see #getFeatureSerializationConfig()
	 * @generated
	 */
	EAttribute getFeatureSerializationConfig_Expand();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getReferenceConfig <em>Reference Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Reference Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getReferenceConfig()
	 * @see #getFeatureSerializationConfig()
	 * @generated
	 */
	EReference getFeatureSerializationConfig_ReferenceConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getTypeConfig <em>Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getTypeConfig()
	 * @see #getFeatureSerializationConfig()
	 * @generated
	 */
	EReference getFeatureSerializationConfig_TypeConfig();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect <em>Class Codec Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Class Codec Aspect</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect
	 * @generated
	 */
	EClass getClassCodecAspect();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getTypeConfig <em>Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getTypeConfig()
	 * @see #getClassCodecAspect()
	 * @generated
	 */
	EReference getClassCodecAspect_TypeConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getIdConfig <em>Id Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Id Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getIdConfig()
	 * @see #getClassCodecAspect()
	 * @generated
	 */
	EReference getClassCodecAspect_IdConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getSuperTypeConfig <em>Super Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Super Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getSuperTypeConfig()
	 * @see #getClassCodecAspect()
	 * @generated
	 */
	EReference getClassCodecAspect_SuperTypeConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isInheritFromParent <em>Inherit From Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Inherit From Parent</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isInheritFromParent()
	 * @see #getClassCodecAspect()
	 * @generated
	 */
	EAttribute getClassCodecAspect_InheritFromParent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getDiscriminatorValue <em>Discriminator Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Discriminator Value</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getDiscriminatorValue()
	 * @see #getClassCodecAspect()
	 * @generated
	 */
	EAttribute getClassCodecAspect_DiscriminatorValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isStrictOnUnknown <em>Strict On Unknown</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strict On Unknown</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isStrictOnUnknown()
	 * @see #getClassCodecAspect()
	 * @generated
	 */
	EAttribute getClassCodecAspect_StrictOnUnknown();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isStrictOnMissing <em>Strict On Missing</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strict On Missing</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isStrictOnMissing()
	 * @see #getClassCodecAspect()
	 * @generated
	 */
	EAttribute getClassCodecAspect_StrictOnMissing();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isMetadataMerge <em>Metadata Merge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Metadata Merge</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isMetadataMerge()
	 * @see #getClassCodecAspect()
	 * @generated
	 */
	EAttribute getClassCodecAspect_MetadataMerge();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getMetadataKey <em>Metadata Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Metadata Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getMetadataKey()
	 * @see #getClassCodecAspect()
	 * @generated
	 */
	EAttribute getClassCodecAspect_MetadataKey();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect <em>Feature Codec Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature Codec Aspect</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect
	 * @generated
	 */
	EClass getFeatureCodecAspect();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getEffectiveKey <em>Effective Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Effective Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getEffectiveKey()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_EffectiveKey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnore <em>Ignore</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnore()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_Ignore();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnoreRead <em>Ignore Read</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore Read</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnoreRead()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_IgnoreRead();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnoreWrite <em>Ignore Write</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ignore Write</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnoreWrite()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_IgnoreWrite();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isForceRead <em>Force Read</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Force Read</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isForceRead()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_ForceRead();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isForceWrite <em>Force Write</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Force Write</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isForceWrite()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_ForceWrite();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeNull <em>Serialize Null</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Null</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeNull()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_SerializeNull();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeEmpty <em>Serialize Empty</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Empty</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeEmpty()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_SerializeEmpty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeDefaults <em>Serialize Defaults</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Defaults</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeDefaults()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_SerializeDefaults();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getValueWriterName <em>Value Writer Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Writer Name</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getValueWriterName()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_ValueWriterName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getValueReaderName <em>Value Reader Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Reader Name</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getValueReaderName()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_ValueReaderName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getEnumSerialization <em>Enum Serialization</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Enum Serialization</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getEnumSerialization()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_EnumSerialization();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getDateFormat <em>Date Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Date Format</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getDateFormat()
	 * @see #getFeatureCodecAspect()
	 * @generated
	 */
	EAttribute getFeatureCodecAspect_DateFormat();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect <em>Reference Codec Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Reference Codec Aspect</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect
	 * @generated
	 */
	EClass getReferenceCodecAspect();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#getReferenceConfig <em>Reference Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Reference Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#getReferenceConfig()
	 * @see #getReferenceCodecAspect()
	 * @generated
	 */
	EReference getReferenceCodecAspect_ReferenceConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#getTypeConfig <em>Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#getTypeConfig()
	 * @see #getReferenceCodecAspect()
	 * @generated
	 */
	EReference getReferenceCodecAspect_TypeConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isInheritTypeFromTarget <em>Inherit Type From Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Inherit Type From Target</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isInheritTypeFromTarget()
	 * @see #getReferenceCodecAspect()
	 * @generated
	 */
	EAttribute getReferenceCodecAspect_InheritTypeFromTarget();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isExpand <em>Expand</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expand</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isExpand()
	 * @see #getReferenceCodecAspect()
	 * @generated
	 */
	EAttribute getReferenceCodecAspect_Expand();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile <em>Package Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Package Profile</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile
	 * @generated
	 */
	EClass getCodecPackageProfile();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile#getClassProfiles <em>Class Profiles</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Class Profiles</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile#getClassProfiles()
	 * @see #getCodecPackageProfile()
	 * @generated
	 */
	EReference getCodecPackageProfile_ClassProfiles();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile <em>Class Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Class Profile</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile
	 * @generated
	 */
	EClass getCodecClassProfile();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getEClass <em>EClass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>EClass</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getEClass()
	 * @see #getCodecClassProfile()
	 * @generated
	 */
	EReference getCodecClassProfile_EClass();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getTypeConfig <em>Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getTypeConfig()
	 * @see #getCodecClassProfile()
	 * @generated
	 */
	EReference getCodecClassProfile_TypeConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getIdConfig <em>Id Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Id Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getIdConfig()
	 * @see #getCodecClassProfile()
	 * @generated
	 */
	EReference getCodecClassProfile_IdConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getSuperTypeConfig <em>Super Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Super Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getSuperTypeConfig()
	 * @see #getCodecClassProfile()
	 * @generated
	 */
	EReference getCodecClassProfile_SuperTypeConfig();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getFeatureConfigs <em>Feature Configs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Feature Configs</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getFeatureConfigs()
	 * @see #getCodecClassProfile()
	 * @generated
	 */
	EReference getCodecClassProfile_FeatureConfigs();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig <em>Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig
	 * @generated
	 */
	EClass getCodecConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getFormat()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isUseNumericIds <em>Use Numeric Ids</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Use Numeric Ids</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isUseNumericIds()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_UseNumericIds();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getTypeConfig <em>Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getTypeConfig()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EReference getCodecConfig_TypeConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getContainmentTypeConfig <em>Containment Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Containment Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getContainmentTypeConfig()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EReference getCodecConfig_ContainmentTypeConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getReferenceTypeConfig <em>Reference Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Reference Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getReferenceTypeConfig()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EReference getCodecConfig_ReferenceTypeConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getIdConfig <em>Id Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Id Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getIdConfig()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EReference getCodecConfig_IdConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getReferenceConfig <em>Reference Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Reference Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getReferenceConfig()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EReference getCodecConfig_ReferenceConfig();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getSuperTypeConfig <em>Super Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Super Type Config</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getSuperTypeConfig()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EReference getCodecConfig_SuperTypeConfig();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getFeatureConfigs <em>Feature Configs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Feature Configs</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getFeatureConfigs()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EReference getCodecConfig_FeatureConfigs();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isExpand <em>Expand</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expand</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isExpand()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_Expand();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getExpandDepth <em>Expand Depth</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expand Depth</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getExpandDepth()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_ExpandDepth();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isExpandIgnoreBidirectional <em>Expand Ignore Bidirectional</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expand Ignore Bidirectional</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isExpandIgnoreBidirectional()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_ExpandIgnoreBidirectional();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeNull <em>Serialize Null</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Null</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeNull()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_SerializeNull();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeEmpty <em>Serialize Empty</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Empty</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeEmpty()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_SerializeEmpty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeDefaults <em>Serialize Defaults</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialize Defaults</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeDefaults()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_SerializeDefaults();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getTypeHintMode <em>Type Hint Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type Hint Mode</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getTypeHintMode()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_TypeHintMode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getDeserializationMode <em>Deserialization Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deserialization Mode</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getDeserializationMode()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_DeserializationMode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isStrictOnUnknown <em>Strict On Unknown</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strict On Unknown</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isStrictOnUnknown()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_StrictOnUnknown();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isStrictOnMissing <em>Strict On Missing</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strict On Missing</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isStrictOnMissing()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_StrictOnMissing();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isMetadataMerge <em>Metadata Merge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Metadata Merge</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isMetadataMerge()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_MetadataMerge();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getMetadataKey <em>Metadata Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Metadata Key</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getMetadataKey()
	 * @see #getCodecConfig()
	 * @generated
	 */
	EAttribute getCodecConfig_MetadataKey();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.StrategyScope <em>Strategy Scope</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Strategy Scope</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
	 * @generated
	 */
	EEnum getStrategyScope();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode <em>Type Hint Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Type Hint Mode</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode
	 * @generated
	 */
	EEnum getTypeHintMode();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode <em>Deserialization Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Deserialization Mode</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode
	 * @generated
	 */
	EEnum getDeserializationMode();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy <em>Fallback Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Fallback Strategy</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy
	 * @generated
	 */
	EEnum getFallbackStrategy();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode <em>Fingerprint Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Fingerprint Mode</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode
	 * @generated
	 */
	EEnum getFingerprintMode();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat <em>Serialization Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Serialization Format</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat
	 * @generated
	 */
	EEnum getSerializationFormat();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy <em>Type Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Type Strategy</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy
	 * @generated
	 */
	EEnum getTypeStrategy();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.IdStrategy <em>Id Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Id Strategy</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdStrategy
	 * @generated
	 */
	EEnum getIdStrategy();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode <em>Id Key Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Id Key Mode</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode
	 * @generated
	 */
	EEnum getIdKeyMode();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection <em>Super Type Selection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Super Type Selection</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection
	 * @generated
	 */
	EEnum getSuperTypeSelection();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy <em>Enum Serialization Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Enum Serialization Strategy</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy
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
	CodecFactory getCodecFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseTypeConfigImpl <em>Base Type Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseTypeConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseTypeConfig()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseIdConfigImpl <em>Base Id Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseIdConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseIdConfig()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseReferenceConfigImpl <em>Base Reference Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseReferenceConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseReferenceConfig()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl <em>Base Super Type Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseSuperTypeConfig()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseFeatureConfigImpl <em>Base Feature Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.BaseFeatureConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getBaseFeatureConfig()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl <em>Type Serialization Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getTypeSerializationConfig()
		 * @generated
		 */
		EClass TYPE_SERIALIZATION_CONFIG = eINSTANCE.getTypeSerializationConfig();

		/**
		 * The meta object literal for the '<em><b>Map Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE_SERIALIZATION_CONFIG__MAP_ID = eINSTANCE.getTypeSerializationConfig_MapId();

		/**
		 * The meta object literal for the '<em><b>Discriminator Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH = eINSTANCE.getTypeSerializationConfig_DiscriminatorPath();

		/**
		 * The meta object literal for the '<em><b>Discriminator Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE = eINSTANCE.getTypeSerializationConfig_DiscriminatorValue();

		/**
		 * The meta object literal for the '<em><b>Fingerprint Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE_SERIALIZATION_CONFIG__FINGERPRINT_MODE = eINSTANCE.getTypeSerializationConfig_FingerprintMode();

		/**
		 * The meta object literal for the '<em><b>Fingerprint Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE_SERIALIZATION_CONFIG__FINGERPRINT_KEY = eINSTANCE.getTypeSerializationConfig_FingerprintKey();

		/**
		 * The meta object literal for the '<em><b>Strategy Scope</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE_SERIALIZATION_CONFIG__STRATEGY_SCOPE = eINSTANCE.getTypeSerializationConfig_StrategyScope();

		/**
		 * The meta object literal for the '<em><b>Format Scope</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE_SERIALIZATION_CONFIG__FORMAT_SCOPE = eINSTANCE.getTypeSerializationConfig_FormatScope();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl <em>Id Serialization Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getIdSerializationConfig()
		 * @generated
		 */
		EClass ID_SERIALIZATION_CONFIG = eINSTANCE.getIdSerializationConfig();

		/**
		 * The meta object literal for the '<em><b>Id Features</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ID_SERIALIZATION_CONFIG__ID_FEATURES = eINSTANCE.getIdSerializationConfig_IdFeatures();

		/**
		 * The meta object literal for the '<em><b>Id Value Writer Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ID_SERIALIZATION_CONFIG__ID_VALUE_WRITER_NAME = eINSTANCE.getIdSerializationConfig_IdValueWriterName();

		/**
		 * The meta object literal for the '<em><b>Id Value Reader Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ID_SERIALIZATION_CONFIG__ID_VALUE_READER_NAME = eINSTANCE.getIdSerializationConfig_IdValueReaderName();

		/**
		 * The meta object literal for the '<em><b>Strategy Scope</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ID_SERIALIZATION_CONFIG__STRATEGY_SCOPE = eINSTANCE.getIdSerializationConfig_StrategyScope();

		/**
		 * The meta object literal for the '<em><b>Format Scope</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ID_SERIALIZATION_CONFIG__FORMAT_SCOPE = eINSTANCE.getIdSerializationConfig_FormatScope();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceSerializationConfigImpl <em>Reference Serialization Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceSerializationConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getReferenceSerializationConfig()
		 * @generated
		 */
		EClass REFERENCE_SERIALIZATION_CONFIG = eINSTANCE.getReferenceSerializationConfig();

		/**
		 * The meta object literal for the '<em><b>Include Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_SERIALIZATION_CONFIG__INCLUDE_TYPE = eINSTANCE.getReferenceSerializationConfig_IncludeType();

		/**
		 * The meta object literal for the '<em><b>Expand</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_SERIALIZATION_CONFIG__EXPAND = eINSTANCE.getReferenceSerializationConfig_Expand();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.SuperTypeSerializationConfigImpl <em>Super Type Serialization Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.SuperTypeSerializationConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getSuperTypeSerializationConfig()
		 * @generated
		 */
		EClass SUPER_TYPE_SERIALIZATION_CONFIG = eINSTANCE.getSuperTypeSerializationConfig();

		/**
		 * The meta object literal for the '<em><b>Use Smart Compression</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SUPER_TYPE_SERIALIZATION_CONFIG__USE_SMART_COMPRESSION = eINSTANCE.getSuperTypeSerializationConfig_UseSmartCompression();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl <em>Feature Serialization Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFeatureSerializationConfig()
		 * @generated
		 */
		EClass FEATURE_SERIALIZATION_CONFIG = eINSTANCE.getFeatureSerializationConfig();

		/**
		 * The meta object literal for the '<em><b>Feature Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_SERIALIZATION_CONFIG__FEATURE_NAME = eINSTANCE.getFeatureSerializationConfig_FeatureName();

		/**
		 * The meta object literal for the '<em><b>Value Writer Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_SERIALIZATION_CONFIG__VALUE_WRITER_NAME = eINSTANCE.getFeatureSerializationConfig_ValueWriterName();

		/**
		 * The meta object literal for the '<em><b>Value Reader Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_SERIALIZATION_CONFIG__VALUE_READER_NAME = eINSTANCE.getFeatureSerializationConfig_ValueReaderName();

		/**
		 * The meta object literal for the '<em><b>Expand</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_SERIALIZATION_CONFIG__EXPAND = eINSTANCE.getFeatureSerializationConfig_Expand();

		/**
		 * The meta object literal for the '<em><b>Reference Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG = eINSTANCE.getFeatureSerializationConfig_ReferenceConfig();

		/**
		 * The meta object literal for the '<em><b>Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG = eINSTANCE.getFeatureSerializationConfig_TypeConfig();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl <em>Class Codec Aspect</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getClassCodecAspect()
		 * @generated
		 */
		EClass CLASS_CODEC_ASPECT = eINSTANCE.getClassCodecAspect();

		/**
		 * The meta object literal for the '<em><b>Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_CODEC_ASPECT__TYPE_CONFIG = eINSTANCE.getClassCodecAspect_TypeConfig();

		/**
		 * The meta object literal for the '<em><b>Id Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_CODEC_ASPECT__ID_CONFIG = eINSTANCE.getClassCodecAspect_IdConfig();

		/**
		 * The meta object literal for the '<em><b>Super Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG = eINSTANCE.getClassCodecAspect_SuperTypeConfig();

		/**
		 * The meta object literal for the '<em><b>Inherit From Parent</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_CODEC_ASPECT__INHERIT_FROM_PARENT = eINSTANCE.getClassCodecAspect_InheritFromParent();

		/**
		 * The meta object literal for the '<em><b>Discriminator Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_CODEC_ASPECT__DISCRIMINATOR_VALUE = eINSTANCE.getClassCodecAspect_DiscriminatorValue();

		/**
		 * The meta object literal for the '<em><b>Strict On Unknown</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_CODEC_ASPECT__STRICT_ON_UNKNOWN = eINSTANCE.getClassCodecAspect_StrictOnUnknown();

		/**
		 * The meta object literal for the '<em><b>Strict On Missing</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_CODEC_ASPECT__STRICT_ON_MISSING = eINSTANCE.getClassCodecAspect_StrictOnMissing();

		/**
		 * The meta object literal for the '<em><b>Metadata Merge</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_CODEC_ASPECT__METADATA_MERGE = eINSTANCE.getClassCodecAspect_MetadataMerge();

		/**
		 * The meta object literal for the '<em><b>Metadata Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_CODEC_ASPECT__METADATA_KEY = eINSTANCE.getClassCodecAspect_MetadataKey();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl <em>Feature Codec Aspect</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFeatureCodecAspect()
		 * @generated
		 */
		EClass FEATURE_CODEC_ASPECT = eINSTANCE.getFeatureCodecAspect();

		/**
		 * The meta object literal for the '<em><b>Effective Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__EFFECTIVE_KEY = eINSTANCE.getFeatureCodecAspect_EffectiveKey();

		/**
		 * The meta object literal for the '<em><b>Ignore</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__IGNORE = eINSTANCE.getFeatureCodecAspect_Ignore();

		/**
		 * The meta object literal for the '<em><b>Ignore Read</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__IGNORE_READ = eINSTANCE.getFeatureCodecAspect_IgnoreRead();

		/**
		 * The meta object literal for the '<em><b>Ignore Write</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__IGNORE_WRITE = eINSTANCE.getFeatureCodecAspect_IgnoreWrite();

		/**
		 * The meta object literal for the '<em><b>Force Read</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__FORCE_READ = eINSTANCE.getFeatureCodecAspect_ForceRead();

		/**
		 * The meta object literal for the '<em><b>Force Write</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__FORCE_WRITE = eINSTANCE.getFeatureCodecAspect_ForceWrite();

		/**
		 * The meta object literal for the '<em><b>Serialize Null</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__SERIALIZE_NULL = eINSTANCE.getFeatureCodecAspect_SerializeNull();

		/**
		 * The meta object literal for the '<em><b>Serialize Empty</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY = eINSTANCE.getFeatureCodecAspect_SerializeEmpty();

		/**
		 * The meta object literal for the '<em><b>Serialize Defaults</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS = eINSTANCE.getFeatureCodecAspect_SerializeDefaults();

		/**
		 * The meta object literal for the '<em><b>Value Writer Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME = eINSTANCE.getFeatureCodecAspect_ValueWriterName();

		/**
		 * The meta object literal for the '<em><b>Value Reader Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__VALUE_READER_NAME = eINSTANCE.getFeatureCodecAspect_ValueReaderName();

		/**
		 * The meta object literal for the '<em><b>Enum Serialization</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION = eINSTANCE.getFeatureCodecAspect_EnumSerialization();

		/**
		 * The meta object literal for the '<em><b>Date Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_CODEC_ASPECT__DATE_FORMAT = eINSTANCE.getFeatureCodecAspect_DateFormat();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl <em>Reference Codec Aspect</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getReferenceCodecAspect()
		 * @generated
		 */
		EClass REFERENCE_CODEC_ASPECT = eINSTANCE.getReferenceCodecAspect();

		/**
		 * The meta object literal for the '<em><b>Reference Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG = eINSTANCE.getReferenceCodecAspect_ReferenceConfig();

		/**
		 * The meta object literal for the '<em><b>Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFERENCE_CODEC_ASPECT__TYPE_CONFIG = eINSTANCE.getReferenceCodecAspect_TypeConfig();

		/**
		 * The meta object literal for the '<em><b>Inherit Type From Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_CODEC_ASPECT__INHERIT_TYPE_FROM_TARGET = eINSTANCE.getReferenceCodecAspect_InheritTypeFromTarget();

		/**
		 * The meta object literal for the '<em><b>Expand</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_CODEC_ASPECT__EXPAND = eINSTANCE.getReferenceCodecAspect_Expand();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageProfileImpl <em>Package Profile</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageProfileImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getCodecPackageProfile()
		 * @generated
		 */
		EClass CODEC_PACKAGE_PROFILE = eINSTANCE.getCodecPackageProfile();

		/**
		 * The meta object literal for the '<em><b>Class Profiles</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_PACKAGE_PROFILE__CLASS_PROFILES = eINSTANCE.getCodecPackageProfile_ClassProfiles();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl <em>Class Profile</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getCodecClassProfile()
		 * @generated
		 */
		EClass CODEC_CLASS_PROFILE = eINSTANCE.getCodecClassProfile();

		/**
		 * The meta object literal for the '<em><b>EClass</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CLASS_PROFILE__ECLASS = eINSTANCE.getCodecClassProfile_EClass();

		/**
		 * The meta object literal for the '<em><b>Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CLASS_PROFILE__TYPE_CONFIG = eINSTANCE.getCodecClassProfile_TypeConfig();

		/**
		 * The meta object literal for the '<em><b>Id Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CLASS_PROFILE__ID_CONFIG = eINSTANCE.getCodecClassProfile_IdConfig();

		/**
		 * The meta object literal for the '<em><b>Super Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG = eINSTANCE.getCodecClassProfile_SuperTypeConfig();

		/**
		 * The meta object literal for the '<em><b>Feature Configs</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CLASS_PROFILE__FEATURE_CONFIGS = eINSTANCE.getCodecClassProfile_FeatureConfigs();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl <em>Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getCodecConfig()
		 * @generated
		 */
		EClass CODEC_CONFIG = eINSTANCE.getCodecConfig();

		/**
		 * The meta object literal for the '<em><b>Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__FORMAT = eINSTANCE.getCodecConfig_Format();

		/**
		 * The meta object literal for the '<em><b>Use Numeric Ids</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__USE_NUMERIC_IDS = eINSTANCE.getCodecConfig_UseNumericIds();

		/**
		 * The meta object literal for the '<em><b>Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CONFIG__TYPE_CONFIG = eINSTANCE.getCodecConfig_TypeConfig();

		/**
		 * The meta object literal for the '<em><b>Containment Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG = eINSTANCE.getCodecConfig_ContainmentTypeConfig();

		/**
		 * The meta object literal for the '<em><b>Reference Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CONFIG__REFERENCE_TYPE_CONFIG = eINSTANCE.getCodecConfig_ReferenceTypeConfig();

		/**
		 * The meta object literal for the '<em><b>Id Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CONFIG__ID_CONFIG = eINSTANCE.getCodecConfig_IdConfig();

		/**
		 * The meta object literal for the '<em><b>Reference Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CONFIG__REFERENCE_CONFIG = eINSTANCE.getCodecConfig_ReferenceConfig();

		/**
		 * The meta object literal for the '<em><b>Super Type Config</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CONFIG__SUPER_TYPE_CONFIG = eINSTANCE.getCodecConfig_SuperTypeConfig();

		/**
		 * The meta object literal for the '<em><b>Feature Configs</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CODEC_CONFIG__FEATURE_CONFIGS = eINSTANCE.getCodecConfig_FeatureConfigs();

		/**
		 * The meta object literal for the '<em><b>Expand</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__EXPAND = eINSTANCE.getCodecConfig_Expand();

		/**
		 * The meta object literal for the '<em><b>Expand Depth</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__EXPAND_DEPTH = eINSTANCE.getCodecConfig_ExpandDepth();

		/**
		 * The meta object literal for the '<em><b>Expand Ignore Bidirectional</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__EXPAND_IGNORE_BIDIRECTIONAL = eINSTANCE.getCodecConfig_ExpandIgnoreBidirectional();

		/**
		 * The meta object literal for the '<em><b>Serialize Null</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__SERIALIZE_NULL = eINSTANCE.getCodecConfig_SerializeNull();

		/**
		 * The meta object literal for the '<em><b>Serialize Empty</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__SERIALIZE_EMPTY = eINSTANCE.getCodecConfig_SerializeEmpty();

		/**
		 * The meta object literal for the '<em><b>Serialize Defaults</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__SERIALIZE_DEFAULTS = eINSTANCE.getCodecConfig_SerializeDefaults();

		/**
		 * The meta object literal for the '<em><b>Type Hint Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__TYPE_HINT_MODE = eINSTANCE.getCodecConfig_TypeHintMode();

		/**
		 * The meta object literal for the '<em><b>Deserialization Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__DESERIALIZATION_MODE = eINSTANCE.getCodecConfig_DeserializationMode();

		/**
		 * The meta object literal for the '<em><b>Strict On Unknown</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__STRICT_ON_UNKNOWN = eINSTANCE.getCodecConfig_StrictOnUnknown();

		/**
		 * The meta object literal for the '<em><b>Strict On Missing</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__STRICT_ON_MISSING = eINSTANCE.getCodecConfig_StrictOnMissing();

		/**
		 * The meta object literal for the '<em><b>Metadata Merge</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__METADATA_MERGE = eINSTANCE.getCodecConfig_MetadataMerge();

		/**
		 * The meta object literal for the '<em><b>Metadata Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CODEC_CONFIG__METADATA_KEY = eINSTANCE.getCodecConfig_MetadataKey();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.StrategyScope <em>Strategy Scope</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getStrategyScope()
		 * @generated
		 */
		EEnum STRATEGY_SCOPE = eINSTANCE.getStrategyScope();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode <em>Type Hint Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getTypeHintMode()
		 * @generated
		 */
		EEnum TYPE_HINT_MODE = eINSTANCE.getTypeHintMode();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode <em>Deserialization Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getDeserializationMode()
		 * @generated
		 */
		EEnum DESERIALIZATION_MODE = eINSTANCE.getDeserializationMode();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy <em>Fallback Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFallbackStrategy()
		 * @generated
		 */
		EEnum FALLBACK_STRATEGY = eINSTANCE.getFallbackStrategy();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode <em>Fingerprint Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFingerprintMode()
		 * @generated
		 */
		EEnum FINGERPRINT_MODE = eINSTANCE.getFingerprintMode();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat <em>Serialization Format</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getSerializationFormat()
		 * @generated
		 */
		EEnum SERIALIZATION_FORMAT = eINSTANCE.getSerializationFormat();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy <em>Type Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getTypeStrategy()
		 * @generated
		 */
		EEnum TYPE_STRATEGY = eINSTANCE.getTypeStrategy();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.IdStrategy <em>Id Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.IdStrategy
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getIdStrategy()
		 * @generated
		 */
		EEnum ID_STRATEGY = eINSTANCE.getIdStrategy();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode <em>Id Key Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getIdKeyMode()
		 * @generated
		 */
		EEnum ID_KEY_MODE = eINSTANCE.getIdKeyMode();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection <em>Super Type Selection</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getSuperTypeSelection()
		 * @generated
		 */
		EEnum SUPER_TYPE_SELECTION = eINSTANCE.getSuperTypeSelection();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy <em>Enum Serialization Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getEnumSerializationStrategy()
		 * @generated
		 */
		EEnum ENUM_SERIALIZATION_STRATEGY = eINSTANCE.getEnumSerializationStrategy();

	}

} //CodecPackage
