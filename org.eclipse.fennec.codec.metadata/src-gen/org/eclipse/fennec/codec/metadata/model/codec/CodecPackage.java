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

import org.eclipse.fennec.model.metadata.MetadataPackage;

import org.gecko.emf.osgi.annotation.provide.EPackage;

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
@EPackage(uri = CodecPackage.eNS_URI, genModel = "/model/codec.genmodel", genModelSourceLocations = {"model/codec.genmodel","org.eclipse.fennec.codec.metadata/model/codec.genmodel"}, ecore="/model/codec.ecore", ecoreSourceLocations="/model/codec.ecore")
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
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl <em>Type Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getTypeSerializationConfig()
	 * @generated
	 */
	int TYPE_SERIALIZATION_CONFIG = 0;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__FORMAT = MetadataPackage.BASE_TYPE_CONFIG__FORMAT;

	/**
	 * The feature id for the '<em><b>Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__STRATEGY = MetadataPackage.BASE_TYPE_CONFIG__STRATEGY;

	/**
	 * The feature id for the '<em><b>Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__TYPE_KEY = MetadataPackage.BASE_TYPE_CONFIG__TYPE_KEY;

	/**
	 * The feature id for the '<em><b>Schema Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__SCHEMA_KEY = MetadataPackage.BASE_TYPE_CONFIG__SCHEMA_KEY;

	/**
	 * The feature id for the '<em><b>Name Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__NAME_KEY = MetadataPackage.BASE_TYPE_CONFIG__NAME_KEY;

	/**
	 * The feature id for the '<em><b>Map Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__MAP_ID = MetadataPackage.BASE_TYPE_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Discriminator Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH = MetadataPackage.BASE_TYPE_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Discriminator Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE = MetadataPackage.BASE_TYPE_CONFIG_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Strategy Scope</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__STRATEGY_SCOPE = MetadataPackage.BASE_TYPE_CONFIG_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Format Scope</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG__FORMAT_SCOPE = MetadataPackage.BASE_TYPE_CONFIG_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Type Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG_FEATURE_COUNT = MetadataPackage.BASE_TYPE_CONFIG_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Type Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_SERIALIZATION_CONFIG_OPERATION_COUNT = MetadataPackage.BASE_TYPE_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl <em>Id Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getIdSerializationConfig()
	 * @generated
	 */
	int ID_SERIALIZATION_CONFIG = 1;

	/**
	 * The feature id for the '<em><b>Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__STRATEGY = MetadataPackage.BASE_ID_CONFIG__STRATEGY;

	/**
	 * The feature id for the '<em><b>Key Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__KEY_MODE = MetadataPackage.BASE_ID_CONFIG__KEY_MODE;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__FORMAT = MetadataPackage.BASE_ID_CONFIG__FORMAT;

	/**
	 * The feature id for the '<em><b>Id Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ID_KEY = MetadataPackage.BASE_ID_CONFIG__ID_KEY;

	/**
	 * The feature id for the '<em><b>Separator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__SEPARATOR = MetadataPackage.BASE_ID_CONFIG__SEPARATOR;

	/**
	 * The feature id for the '<em><b>On Top</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ON_TOP = MetadataPackage.BASE_ID_CONFIG__ON_TOP;

	/**
	 * The feature id for the '<em><b>Serialize Separator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__SERIALIZE_SEPARATOR = MetadataPackage.BASE_ID_CONFIG__SERIALIZE_SEPARATOR;

	/**
	 * The feature id for the '<em><b>Separator Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__SEPARATOR_KEY = MetadataPackage.BASE_ID_CONFIG__SEPARATOR_KEY;

	/**
	 * The feature id for the '<em><b>Value Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__VALUE_KEY = MetadataPackage.BASE_ID_CONFIG__VALUE_KEY;

	/**
	 * The feature id for the '<em><b>Id Features</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ID_FEATURES = MetadataPackage.BASE_ID_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Id Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ID_VALUE_WRITER_NAME = MetadataPackage.BASE_ID_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Id Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__ID_VALUE_READER_NAME = MetadataPackage.BASE_ID_CONFIG_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Strategy Scope</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__STRATEGY_SCOPE = MetadataPackage.BASE_ID_CONFIG_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Format Scope</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG__FORMAT_SCOPE = MetadataPackage.BASE_ID_CONFIG_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Id Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG_FEATURE_COUNT = MetadataPackage.BASE_ID_CONFIG_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Id Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ID_SERIALIZATION_CONFIG_OPERATION_COUNT = MetadataPackage.BASE_ID_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceSerializationConfigImpl <em>Reference Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getReferenceSerializationConfig()
	 * @generated
	 */
	int REFERENCE_SERIALIZATION_CONFIG = 2;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__FORMAT = MetadataPackage.BASE_REFERENCE_CONFIG__FORMAT;

	/**
	 * The feature id for the '<em><b>Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__TYPE_KEY = MetadataPackage.BASE_REFERENCE_CONFIG__TYPE_KEY;

	/**
	 * The feature id for the '<em><b>Ref Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__REF_KEY = MetadataPackage.BASE_REFERENCE_CONFIG__REF_KEY;

	/**
	 * The feature id for the '<em><b>Include Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__INCLUDE_TYPE = MetadataPackage.BASE_REFERENCE_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Expand</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG__EXPAND = MetadataPackage.BASE_REFERENCE_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Reference Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG_FEATURE_COUNT = MetadataPackage.BASE_REFERENCE_CONFIG_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Reference Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_SERIALIZATION_CONFIG_OPERATION_COUNT = MetadataPackage.BASE_REFERENCE_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.SuperTypeSerializationConfigImpl <em>Super Type Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.SuperTypeSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getSuperTypeSerializationConfig()
	 * @generated
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG = 3;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__ENABLED = MetadataPackage.BASE_SUPER_TYPE_CONFIG__ENABLED;

	/**
	 * The feature id for the '<em><b>Selection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__SELECTION = MetadataPackage.BASE_SUPER_TYPE_CONFIG__SELECTION;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__FORMAT = MetadataPackage.BASE_SUPER_TYPE_CONFIG__FORMAT;

	/**
	 * The feature id for the '<em><b>As Array</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__AS_ARRAY = MetadataPackage.BASE_SUPER_TYPE_CONFIG__AS_ARRAY;

	/**
	 * The feature id for the '<em><b>Separator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__SEPARATOR = MetadataPackage.BASE_SUPER_TYPE_CONFIG__SEPARATOR;

	/**
	 * The feature id for the '<em><b>Super Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__SUPER_TYPE_KEY = MetadataPackage.BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY;

	/**
	 * The feature id for the '<em><b>Use Smart Compression</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG__USE_SMART_COMPRESSION = MetadataPackage.BASE_SUPER_TYPE_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Super Type Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG_FEATURE_COUNT = MetadataPackage.BASE_SUPER_TYPE_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Super Type Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUPER_TYPE_SERIALIZATION_CONFIG_OPERATION_COUNT = MetadataPackage.BASE_SUPER_TYPE_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl <em>Feature Serialization Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFeatureSerializationConfig()
	 * @generated
	 */
	int FEATURE_SERIALIZATION_CONFIG = 4;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__KEY = MetadataPackage.BASE_FEATURE_CONFIG__KEY;

	/**
	 * The feature id for the '<em><b>Ignore</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__IGNORE = MetadataPackage.BASE_FEATURE_CONFIG__IGNORE;

	/**
	 * The feature id for the '<em><b>Ignore Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__IGNORE_READ = MetadataPackage.BASE_FEATURE_CONFIG__IGNORE_READ;

	/**
	 * The feature id for the '<em><b>Ignore Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__IGNORE_WRITE = MetadataPackage.BASE_FEATURE_CONFIG__IGNORE_WRITE;

	/**
	 * The feature id for the '<em><b>Force Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__FORCE_READ = MetadataPackage.BASE_FEATURE_CONFIG__FORCE_READ;

	/**
	 * The feature id for the '<em><b>Force Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__FORCE_WRITE = MetadataPackage.BASE_FEATURE_CONFIG__FORCE_WRITE;

	/**
	 * The feature id for the '<em><b>Serialize Null</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__SERIALIZE_NULL = MetadataPackage.BASE_FEATURE_CONFIG__SERIALIZE_NULL;

	/**
	 * The feature id for the '<em><b>Serialize Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__SERIALIZE_EMPTY = MetadataPackage.BASE_FEATURE_CONFIG__SERIALIZE_EMPTY;

	/**
	 * The feature id for the '<em><b>Serialize Defaults</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__SERIALIZE_DEFAULTS = MetadataPackage.BASE_FEATURE_CONFIG__SERIALIZE_DEFAULTS;

	/**
	 * The feature id for the '<em><b>Enum Serialization</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__ENUM_SERIALIZATION = MetadataPackage.BASE_FEATURE_CONFIG__ENUM_SERIALIZATION;

	/**
	 * The feature id for the '<em><b>Feature Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__FEATURE_NAME = MetadataPackage.BASE_FEATURE_CONFIG_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__VALUE_WRITER_NAME = MetadataPackage.BASE_FEATURE_CONFIG_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__VALUE_READER_NAME = MetadataPackage.BASE_FEATURE_CONFIG_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Expand</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__EXPAND = MetadataPackage.BASE_FEATURE_CONFIG_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Reference Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG = MetadataPackage.BASE_FEATURE_CONFIG_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG = MetadataPackage.BASE_FEATURE_CONFIG_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Feature Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG_FEATURE_COUNT = MetadataPackage.BASE_FEATURE_CONFIG_FEATURE_COUNT + 6;

	/**
	 * The number of operations of the '<em>Feature Serialization Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_SERIALIZATION_CONFIG_OPERATION_COUNT = MetadataPackage.BASE_FEATURE_CONFIG_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl <em>Class Codec Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getClassCodecAspect()
	 * @generated
	 */
	int CLASS_CODEC_ASPECT = 5;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__TYPE_ID = MetadataPackage.CLASS_ASPECT__TYPE_ID;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__DIAGNOSTICS = MetadataPackage.CLASS_ASPECT__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Class Metadata</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__CLASS_METADATA = MetadataPackage.CLASS_ASPECT__CLASS_METADATA;

	/**
	 * The feature id for the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__TYPE_CONFIG = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Id Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__ID_CONFIG = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Super Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Inherit From Parent</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__INHERIT_FROM_PARENT = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Discriminator Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__DISCRIMINATOR_VALUE = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Strict On Unknown</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__STRICT_ON_UNKNOWN = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Strict On Missing</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__STRICT_ON_MISSING = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Metadata Merge</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__METADATA_MERGE = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Metadata Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT__METADATA_KEY = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 8;

	/**
	 * The number of structural features of the '<em>Class Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT_FEATURE_COUNT = MetadataPackage.CLASS_ASPECT_FEATURE_COUNT + 9;

	/**
	 * The number of operations of the '<em>Class Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_CODEC_ASPECT_OPERATION_COUNT = MetadataPackage.CLASS_ASPECT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl <em>Feature Codec Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFeatureCodecAspect()
	 * @generated
	 */
	int FEATURE_CODEC_ASPECT = 6;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__TYPE_ID = MetadataPackage.FEATURE_ASPECT__TYPE_ID;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__DIAGNOSTICS = MetadataPackage.FEATURE_ASPECT__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Feature Metadata</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__FEATURE_METADATA = MetadataPackage.FEATURE_ASPECT__FEATURE_METADATA;

	/**
	 * The feature id for the '<em><b>Effective Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__EFFECTIVE_KEY = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Ignore</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__IGNORE = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ignore Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__IGNORE_READ = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Ignore Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__IGNORE_WRITE = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Force Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__FORCE_READ = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Force Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__FORCE_WRITE = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Serialize Null</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__SERIALIZE_NULL = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Serialize Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Serialize Defaults</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__VALUE_READER_NAME = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Enum Serialization</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 11;

	/**
	 * The number of structural features of the '<em>Feature Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT_FEATURE_COUNT = MetadataPackage.FEATURE_ASPECT_FEATURE_COUNT + 12;

	/**
	 * The number of operations of the '<em>Feature Codec Aspect</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_CODEC_ASPECT_OPERATION_COUNT = MetadataPackage.FEATURE_ASPECT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl <em>Reference Codec Aspect</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getReferenceCodecAspect()
	 * @generated
	 */
	int REFERENCE_CODEC_ASPECT = 7;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__TYPE_ID = FEATURE_CODEC_ASPECT__TYPE_ID;

	/**
	 * The feature id for the '<em><b>Diagnostics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__DIAGNOSTICS = FEATURE_CODEC_ASPECT__DIAGNOSTICS;

	/**
	 * The feature id for the '<em><b>Feature Metadata</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CODEC_ASPECT__FEATURE_METADATA = FEATURE_CODEC_ASPECT__FEATURE_METADATA;

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
	int CODEC_PACKAGE_PROFILE = 8;

	/**
	 * The feature id for the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_PACKAGE_PROFILE__TYPE_ID = MetadataPackage.PACKAGE_PROFILE__TYPE_ID;

	/**
	 * The feature id for the '<em><b>Class Profiles</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_PACKAGE_PROFILE__CLASS_PROFILES = MetadataPackage.PACKAGE_PROFILE__CLASS_PROFILES;

	/**
	 * The number of structural features of the '<em>Package Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_PACKAGE_PROFILE_FEATURE_COUNT = MetadataPackage.PACKAGE_PROFILE_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Package Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_PACKAGE_PROFILE_OPERATION_COUNT = MetadataPackage.PACKAGE_PROFILE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl <em>Class Profile</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getCodecClassProfile()
	 * @generated
	 */
	int CODEC_CLASS_PROFILE = 9;

	/**
	 * The feature id for the '<em><b>EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__ECLASS = MetadataPackage.CLASS_PROFILE__ECLASS;

	/**
	 * The feature id for the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__TYPE_CONFIG = MetadataPackage.CLASS_PROFILE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Id Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__ID_CONFIG = MetadataPackage.CLASS_PROFILE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Super Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG = MetadataPackage.CLASS_PROFILE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Feature Configs</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE__FEATURE_CONFIGS = MetadataPackage.CLASS_PROFILE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Class Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE_FEATURE_COUNT = MetadataPackage.CLASS_PROFILE_FEATURE_COUNT + 4;

	/**
	 * The number of operations of the '<em>Class Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CODEC_CLASS_PROFILE_OPERATION_COUNT = MetadataPackage.CLASS_PROFILE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl <em>Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getCodecConfig()
	 * @generated
	 */
	int CODEC_CONFIG = 10;

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
	int STRATEGY_SCOPE = 11;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode <em>Type Hint Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getTypeHintMode()
	 * @generated
	 */
	int TYPE_HINT_MODE = 12;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode <em>Deserialization Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getDeserializationMode()
	 * @generated
	 */
	int DESERIALIZATION_MODE = 13;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy <em>Fallback Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy
	 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getFallbackStrategy()
	 * @generated
	 */
	int FALLBACK_STRATEGY = 14;


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
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile <em>Class Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Class Profile</em>'.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile
	 * @generated
	 */
	EClass getCodecClassProfile();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl <em>Class Profile</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl
		 * @see org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageImpl#getCodecClassProfile()
		 * @generated
		 */
		EClass CODEC_CLASS_PROFILE = eINSTANCE.getCodecClassProfile();

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

	}

} //CodecPackage
