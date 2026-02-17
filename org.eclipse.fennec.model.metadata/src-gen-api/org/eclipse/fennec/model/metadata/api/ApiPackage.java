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
package org.eclipse.fennec.model.metadata.api;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;

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
 * @see org.eclipse.fennec.model.metadata.api.ApiFactory
 * @model kind="package"
 *        annotation="Version value='1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = ApiPackage.eNS_URI, genModel = "/model/metadata-api.genmodel", genModelSourceLocations = {"model/metadata-api.genmodel","org.eclipse.fennec.model.metadata/model/metadata-api.genmodel"}, ecore = "/model/metadata-api.ecore", ecoreSourceLocations = "/model/metadata-api.ecore")
public interface ApiPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "api";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/metadata/api/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "metadata.api";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ApiPackage eINSTANCE = org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader <em>Metadata Index Reader</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader
	 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataIndexReader()
	 * @generated
	 */
	int METADATA_INDEX_READER = 0;

	/**
	 * The number of structural features of the '<em>Metadata Index Reader</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Find By Instance Class Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER___FIND_BY_INSTANCE_CLASS_NAME__STRING_STRING = 0;

	/**
	 * The operation id for the '<em>Find All By Instance Class Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER___FIND_ALL_BY_INSTANCE_CLASS_NAME__STRING = 1;

	/**
	 * The operation id for the '<em>Find By Class Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER___FIND_BY_CLASS_NAME__STRING_STRING = 2;

	/**
	 * The operation id for the '<em>Find All By Class Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER___FIND_ALL_BY_CLASS_NAME__STRING = 3;

	/**
	 * The operation id for the '<em>Find Class By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER___FIND_CLASS_BY_URI__STRING = 4;

	/**
	 * The operation id for the '<em>Find Feature By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER___FIND_FEATURE_BY_URI__STRING = 5;

	/**
	 * The operation id for the '<em>Find Classes By Annotation</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER___FIND_CLASSES_BY_ANNOTATION__STRING_STRING_STRING = 6;

	/**
	 * The operation id for the '<em>Find Features By Annotation</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER___FIND_FEATURES_BY_ANNOTATION__STRING_STRING_STRING = 7;

	/**
	 * The number of operations of the '<em>Metadata Index Reader</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_READER_OPERATION_COUNT = 8;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter <em>Metadata Index Writer</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter
	 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataIndexWriter()
	 * @generated
	 */
	int METADATA_INDEX_WRITER = 1;

	/**
	 * The number of structural features of the '<em>Metadata Index Writer</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_WRITER_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Index Package</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_WRITER___INDEX_PACKAGE__PACKAGEMETADATA = 0;

	/**
	 * The operation id for the '<em>Index Class</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_WRITER___INDEX_CLASS__CLASSMETADATA = 1;

	/**
	 * The operation id for the '<em>Index Feature</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_WRITER___INDEX_FEATURE__FEATUREMETADATA = 2;

	/**
	 * The operation id for the '<em>Remove Package</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_WRITER___REMOVE_PACKAGE__PACKAGEMETADATA = 3;

	/**
	 * The operation id for the '<em>Remove Class</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_WRITER___REMOVE_CLASS__CLASSMETADATA = 4;

	/**
	 * The operation id for the '<em>Remove Feature</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_WRITER___REMOVE_FEATURE__FEATUREMETADATA = 5;

	/**
	 * The operation id for the '<em>Clear</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_WRITER___CLEAR = 6;

	/**
	 * The number of operations of the '<em>Metadata Index Writer</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_WRITER_OPERATION_COUNT = 7;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndex <em>Metadata Index</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndex
	 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataIndex()
	 * @generated
	 */
	int METADATA_INDEX = 2;

	/**
	 * The number of structural features of the '<em>Metadata Index</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_FEATURE_COUNT = METADATA_INDEX_READER_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Find By Instance Class Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___FIND_BY_INSTANCE_CLASS_NAME__STRING_STRING = METADATA_INDEX_READER___FIND_BY_INSTANCE_CLASS_NAME__STRING_STRING;

	/**
	 * The operation id for the '<em>Find All By Instance Class Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___FIND_ALL_BY_INSTANCE_CLASS_NAME__STRING = METADATA_INDEX_READER___FIND_ALL_BY_INSTANCE_CLASS_NAME__STRING;

	/**
	 * The operation id for the '<em>Find By Class Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___FIND_BY_CLASS_NAME__STRING_STRING = METADATA_INDEX_READER___FIND_BY_CLASS_NAME__STRING_STRING;

	/**
	 * The operation id for the '<em>Find All By Class Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___FIND_ALL_BY_CLASS_NAME__STRING = METADATA_INDEX_READER___FIND_ALL_BY_CLASS_NAME__STRING;

	/**
	 * The operation id for the '<em>Find Class By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___FIND_CLASS_BY_URI__STRING = METADATA_INDEX_READER___FIND_CLASS_BY_URI__STRING;

	/**
	 * The operation id for the '<em>Find Feature By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___FIND_FEATURE_BY_URI__STRING = METADATA_INDEX_READER___FIND_FEATURE_BY_URI__STRING;

	/**
	 * The operation id for the '<em>Find Classes By Annotation</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___FIND_CLASSES_BY_ANNOTATION__STRING_STRING_STRING = METADATA_INDEX_READER___FIND_CLASSES_BY_ANNOTATION__STRING_STRING_STRING;

	/**
	 * The operation id for the '<em>Find Features By Annotation</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___FIND_FEATURES_BY_ANNOTATION__STRING_STRING_STRING = METADATA_INDEX_READER___FIND_FEATURES_BY_ANNOTATION__STRING_STRING_STRING;

	/**
	 * The operation id for the '<em>Index Package</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___INDEX_PACKAGE__PACKAGEMETADATA = METADATA_INDEX_READER_OPERATION_COUNT + 0;

	/**
	 * The operation id for the '<em>Index Class</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___INDEX_CLASS__CLASSMETADATA = METADATA_INDEX_READER_OPERATION_COUNT + 1;

	/**
	 * The operation id for the '<em>Index Feature</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___INDEX_FEATURE__FEATUREMETADATA = METADATA_INDEX_READER_OPERATION_COUNT + 2;

	/**
	 * The operation id for the '<em>Remove Package</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___REMOVE_PACKAGE__PACKAGEMETADATA = METADATA_INDEX_READER_OPERATION_COUNT + 3;

	/**
	 * The operation id for the '<em>Remove Class</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___REMOVE_CLASS__CLASSMETADATA = METADATA_INDEX_READER_OPERATION_COUNT + 4;

	/**
	 * The operation id for the '<em>Remove Feature</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___REMOVE_FEATURE__FEATUREMETADATA = METADATA_INDEX_READER_OPERATION_COUNT + 5;

	/**
	 * The operation id for the '<em>Clear</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX___CLEAR = METADATA_INDEX_READER_OPERATION_COUNT + 6;

	/**
	 * The number of operations of the '<em>Metadata Index</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_INDEX_OPERATION_COUNT = METADATA_INDEX_READER_OPERATION_COUNT + 7;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService <em>Metadata Service</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService
	 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataService()
	 * @generated
	 */
	int METADATA_SERVICE = 3;

	/**
	 * The number of structural features of the '<em>Metadata Service</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Get Index Reader</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_INDEX_READER = 0;

	/**
	 * The operation id for the '<em>Get Package Metadata</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_PACKAGE_METADATA__STRING = 1;

	/**
	 * The operation id for the '<em>Get Class Metadata</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_CLASS_METADATA__ECLASS = 2;

	/**
	 * The operation id for the '<em>Get Class Metadata By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_CLASS_METADATA_BY_URI__STRING = 3;

	/**
	 * The operation id for the '<em>Get Class Metadata By Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_CLASS_METADATA_BY_NAME__STRING_STRING = 4;

	/**
	 * The operation id for the '<em>Get Feature Metadata</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_FEATURE_METADATA__ESTRUCTURALFEATURE = 5;

	/**
	 * The operation id for the '<em>Get Feature Metadata By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_FEATURE_METADATA_BY_URI__STRING = 6;

	/**
	 * The operation id for the '<em>Get Feature Metadata By Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_FEATURE_METADATA_BY_NAME__STRING_STRING_STRING = 7;

	/**
	 * The operation id for the '<em>Get Feature Metadata From Class</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_FEATURE_METADATA_FROM_CLASS__STRING_CLASSMETADATA = 8;

	/**
	 * The operation id for the '<em>Get Package Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_PACKAGE_ASPECT__EPACKAGE_STRING = 9;

	/**
	 * The operation id for the '<em>Get Class Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_CLASS_ASPECT__ECLASS_STRING = 10;

	/**
	 * The operation id for the '<em>Get Feature Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_FEATURE_ASPECT__ESTRUCTURALFEATURE_STRING = 11;

	/**
	 * The operation id for the '<em>Get Package Profile</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_PACKAGE_PROFILE__EPACKAGE_STRING = 12;

	/**
	 * The operation id for the '<em>Get Package Profile By Ns URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_PACKAGE_PROFILE_BY_NS_URI__STRING_STRING = 13;

	/**
	 * The operation id for the '<em>Get Class Profile</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_CLASS_PROFILE__ECLASS_STRING = 14;

	/**
	 * The operation id for the '<em>Get Class Profile By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_CLASS_PROFILE_BY_URI__STRING_STRING = 15;

	/**
	 * The operation id for the '<em>Get Registry</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE___GET_REGISTRY = 16;

	/**
	 * The number of operations of the '<em>Metadata Service</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_SERVICE_OPERATION_COUNT = 17;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.api.MetadataHandler <em>Metadata Handler</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.api.MetadataHandler
	 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataHandler()
	 * @generated
	 */
	int METADATA_HANDLER = 4;

	/**
	 * The number of structural features of the '<em>Metadata Handler</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_HANDLER_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>On Package Registered</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_HANDLER___ON_PACKAGE_REGISTERED__PACKAGEMETADATA = 0;

	/**
	 * The operation id for the '<em>On Package Unregistered</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_HANDLER___ON_PACKAGE_UNREGISTERED__PACKAGEMETADATA = 1;

	/**
	 * The operation id for the '<em>Clear</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_HANDLER___CLEAR = 2;

	/**
	 * The number of operations of the '<em>Metadata Handler</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_HANDLER_OPERATION_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard <em>Metadata Whiteboard</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard
	 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataWhiteboard()
	 * @generated
	 */
	int METADATA_WHITEBOARD = 5;

	/**
	 * The number of structural features of the '<em>Metadata Whiteboard</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD_FEATURE_COUNT = METADATA_SERVICE_FEATURE_COUNT + 0;

	/**
	 * The operation id for the '<em>Get Index Reader</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_INDEX_READER = METADATA_SERVICE___GET_INDEX_READER;

	/**
	 * The operation id for the '<em>Get Package Metadata</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_PACKAGE_METADATA__STRING = METADATA_SERVICE___GET_PACKAGE_METADATA__STRING;

	/**
	 * The operation id for the '<em>Get Class Metadata</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_CLASS_METADATA__ECLASS = METADATA_SERVICE___GET_CLASS_METADATA__ECLASS;

	/**
	 * The operation id for the '<em>Get Class Metadata By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_CLASS_METADATA_BY_URI__STRING = METADATA_SERVICE___GET_CLASS_METADATA_BY_URI__STRING;

	/**
	 * The operation id for the '<em>Get Class Metadata By Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_CLASS_METADATA_BY_NAME__STRING_STRING = METADATA_SERVICE___GET_CLASS_METADATA_BY_NAME__STRING_STRING;

	/**
	 * The operation id for the '<em>Get Feature Metadata</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_FEATURE_METADATA__ESTRUCTURALFEATURE = METADATA_SERVICE___GET_FEATURE_METADATA__ESTRUCTURALFEATURE;

	/**
	 * The operation id for the '<em>Get Feature Metadata By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_FEATURE_METADATA_BY_URI__STRING = METADATA_SERVICE___GET_FEATURE_METADATA_BY_URI__STRING;

	/**
	 * The operation id for the '<em>Get Feature Metadata By Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_FEATURE_METADATA_BY_NAME__STRING_STRING_STRING = METADATA_SERVICE___GET_FEATURE_METADATA_BY_NAME__STRING_STRING_STRING;

	/**
	 * The operation id for the '<em>Get Feature Metadata From Class</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_FEATURE_METADATA_FROM_CLASS__STRING_CLASSMETADATA = METADATA_SERVICE___GET_FEATURE_METADATA_FROM_CLASS__STRING_CLASSMETADATA;

	/**
	 * The operation id for the '<em>Get Package Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_PACKAGE_ASPECT__EPACKAGE_STRING = METADATA_SERVICE___GET_PACKAGE_ASPECT__EPACKAGE_STRING;

	/**
	 * The operation id for the '<em>Get Class Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_CLASS_ASPECT__ECLASS_STRING = METADATA_SERVICE___GET_CLASS_ASPECT__ECLASS_STRING;

	/**
	 * The operation id for the '<em>Get Feature Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_FEATURE_ASPECT__ESTRUCTURALFEATURE_STRING = METADATA_SERVICE___GET_FEATURE_ASPECT__ESTRUCTURALFEATURE_STRING;

	/**
	 * The operation id for the '<em>Get Package Profile</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_PACKAGE_PROFILE__EPACKAGE_STRING = METADATA_SERVICE___GET_PACKAGE_PROFILE__EPACKAGE_STRING;

	/**
	 * The operation id for the '<em>Get Package Profile By Ns URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_PACKAGE_PROFILE_BY_NS_URI__STRING_STRING = METADATA_SERVICE___GET_PACKAGE_PROFILE_BY_NS_URI__STRING_STRING;

	/**
	 * The operation id for the '<em>Get Class Profile</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_CLASS_PROFILE__ECLASS_STRING = METADATA_SERVICE___GET_CLASS_PROFILE__ECLASS_STRING;

	/**
	 * The operation id for the '<em>Get Class Profile By URI</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_CLASS_PROFILE_BY_URI__STRING_STRING = METADATA_SERVICE___GET_CLASS_PROFILE_BY_URI__STRING_STRING;

	/**
	 * The operation id for the '<em>Get Registry</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_REGISTRY = METADATA_SERVICE___GET_REGISTRY;

	/**
	 * The operation id for the '<em>Register Package</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___REGISTER_PACKAGE__EPACKAGE = METADATA_SERVICE_OPERATION_COUNT + 0;

	/**
	 * The operation id for the '<em>Unregister Package</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___UNREGISTER_PACKAGE__EPACKAGE = METADATA_SERVICE_OPERATION_COUNT + 1;

	/**
	 * The operation id for the '<em>Register Aspect Provider</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___REGISTER_ASPECT_PROVIDER__ASPECTPROVIDER = METADATA_SERVICE_OPERATION_COUNT + 2;

	/**
	 * The operation id for the '<em>Unregister Aspect Provider</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___UNREGISTER_ASPECT_PROVIDER__ASPECTPROVIDER = METADATA_SERVICE_OPERATION_COUNT + 3;

	/**
	 * The operation id for the '<em>Get Aspect Providers</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_ASPECT_PROVIDERS = METADATA_SERVICE_OPERATION_COUNT + 4;

	/**
	 * The operation id for the '<em>Get Metadata Index</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___GET_METADATA_INDEX = METADATA_SERVICE_OPERATION_COUNT + 5;

	/**
	 * The operation id for the '<em>Set Metadata Index</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___SET_METADATA_INDEX__METADATAINDEX = METADATA_SERVICE_OPERATION_COUNT + 6;

	/**
	 * The operation id for the '<em>Unset Metadata Index</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___UNSET_METADATA_INDEX__METADATAINDEX = METADATA_SERVICE_OPERATION_COUNT + 7;

	/**
	 * The operation id for the '<em>Add Metadata Handler</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___ADD_METADATA_HANDLER__METADATAHANDLER = METADATA_SERVICE_OPERATION_COUNT + 8;

	/**
	 * The operation id for the '<em>Remove Metadata Handler</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD___REMOVE_METADATA_HANDLER__METADATAHANDLER = METADATA_SERVICE_OPERATION_COUNT + 9;

	/**
	 * The number of operations of the '<em>Metadata Whiteboard</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_WHITEBOARD_OPERATION_COUNT = METADATA_SERVICE_OPERATION_COUNT + 10;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.metadata.api.AspectProvider <em>Aspect Provider</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.metadata.api.AspectProvider
	 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getAspectProvider()
	 * @generated
	 */
	int ASPECT_PROVIDER = 6;

	/**
	 * The number of structural features of the '<em>Aspect Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_PROVIDER_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Get Aspect Type Id</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_PROVIDER___GET_ASPECT_TYPE_ID = 0;

	/**
	 * The operation id for the '<em>Build Package Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_PROVIDER___BUILD_PACKAGE_ASPECT__PACKAGEMETADATA = 1;

	/**
	 * The operation id for the '<em>Build Class Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_PROVIDER___BUILD_CLASS_ASPECT__CLASSMETADATA = 2;

	/**
	 * The operation id for the '<em>Build Feature Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_PROVIDER___BUILD_FEATURE_ASPECT__FEATUREMETADATA = 3;

	/**
	 * The operation id for the '<em>Build Attribute Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_PROVIDER___BUILD_ATTRIBUTE_ASPECT__ATTRIBUTEMETADATA = 4;

	/**
	 * The operation id for the '<em>Build Reference Aspect</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_PROVIDER___BUILD_REFERENCE_ASPECT__REFERENCEMETADATA = 5;

	/**
	 * The operation id for the '<em>Build Profiles</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_PROVIDER___BUILD_PROFILES__PACKAGEMETADATA = 6;

	/**
	 * The number of operations of the '<em>Aspect Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASPECT_PROVIDER_OPERATION_COUNT = 7;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader <em>Metadata Index Reader</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata Index Reader</em>'.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader
	 * @generated
	 */
	EClass getMetadataIndexReader();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findByInstanceClassName(java.lang.String, java.lang.String) <em>Find By Instance Class Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find By Instance Class Name</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findByInstanceClassName(java.lang.String, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataIndexReader__FindByInstanceClassName__String_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findAllByInstanceClassName(java.lang.String) <em>Find All By Instance Class Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find All By Instance Class Name</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findAllByInstanceClassName(java.lang.String)
	 * @generated
	 */
	EOperation getMetadataIndexReader__FindAllByInstanceClassName__String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findByClassName(java.lang.String, java.lang.String) <em>Find By Class Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find By Class Name</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findByClassName(java.lang.String, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataIndexReader__FindByClassName__String_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findAllByClassName(java.lang.String) <em>Find All By Class Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find All By Class Name</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findAllByClassName(java.lang.String)
	 * @generated
	 */
	EOperation getMetadataIndexReader__FindAllByClassName__String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findClassByURI(java.lang.String) <em>Find Class By URI</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find Class By URI</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findClassByURI(java.lang.String)
	 * @generated
	 */
	EOperation getMetadataIndexReader__FindClassByURI__String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findFeatureByURI(java.lang.String) <em>Find Feature By URI</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find Feature By URI</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findFeatureByURI(java.lang.String)
	 * @generated
	 */
	EOperation getMetadataIndexReader__FindFeatureByURI__String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findClassesByAnnotation(java.lang.String, java.lang.String, java.lang.String) <em>Find Classes By Annotation</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find Classes By Annotation</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findClassesByAnnotation(java.lang.String, java.lang.String, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataIndexReader__FindClassesByAnnotation__String_String_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findFeaturesByAnnotation(java.lang.String, java.lang.String, java.lang.String) <em>Find Features By Annotation</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find Features By Annotation</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader#findFeaturesByAnnotation(java.lang.String, java.lang.String, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataIndexReader__FindFeaturesByAnnotation__String_String_String();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter <em>Metadata Index Writer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata Index Writer</em>'.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter
	 * @generated
	 */
	EClass getMetadataIndexWriter();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#indexPackage(org.eclipse.fennec.model.metadata.PackageMetadata) <em>Index Package</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Index Package</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#indexPackage(org.eclipse.fennec.model.metadata.PackageMetadata)
	 * @generated
	 */
	EOperation getMetadataIndexWriter__IndexPackage__PackageMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#indexClass(org.eclipse.fennec.model.metadata.ClassMetadata) <em>Index Class</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Index Class</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#indexClass(org.eclipse.fennec.model.metadata.ClassMetadata)
	 * @generated
	 */
	EOperation getMetadataIndexWriter__IndexClass__ClassMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#indexFeature(org.eclipse.fennec.model.metadata.FeatureMetadata) <em>Index Feature</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Index Feature</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#indexFeature(org.eclipse.fennec.model.metadata.FeatureMetadata)
	 * @generated
	 */
	EOperation getMetadataIndexWriter__IndexFeature__FeatureMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#removePackage(org.eclipse.fennec.model.metadata.PackageMetadata) <em>Remove Package</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Remove Package</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#removePackage(org.eclipse.fennec.model.metadata.PackageMetadata)
	 * @generated
	 */
	EOperation getMetadataIndexWriter__RemovePackage__PackageMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#removeClass(org.eclipse.fennec.model.metadata.ClassMetadata) <em>Remove Class</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Remove Class</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#removeClass(org.eclipse.fennec.model.metadata.ClassMetadata)
	 * @generated
	 */
	EOperation getMetadataIndexWriter__RemoveClass__ClassMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#removeFeature(org.eclipse.fennec.model.metadata.FeatureMetadata) <em>Remove Feature</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Remove Feature</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#removeFeature(org.eclipse.fennec.model.metadata.FeatureMetadata)
	 * @generated
	 */
	EOperation getMetadataIndexWriter__RemoveFeature__FeatureMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#clear() <em>Clear</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Clear</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter#clear()
	 * @generated
	 */
	EOperation getMetadataIndexWriter__Clear();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.api.MetadataIndex <em>Metadata Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata Index</em>'.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataIndex
	 * @generated
	 */
	EClass getMetadataIndex();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.api.MetadataService <em>Metadata Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata Service</em>'.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService
	 * @generated
	 */
	EClass getMetadataService();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getIndexReader() <em>Get Index Reader</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Index Reader</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getIndexReader()
	 * @generated
	 */
	EOperation getMetadataService__GetIndexReader();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getPackageMetadata(java.lang.String) <em>Get Package Metadata</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Package Metadata</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getPackageMetadata(java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetPackageMetadata__String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getClassMetadata(org.eclipse.emf.ecore.EClass) <em>Get Class Metadata</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Class Metadata</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getClassMetadata(org.eclipse.emf.ecore.EClass)
	 * @generated
	 */
	EOperation getMetadataService__GetClassMetadata__EClass();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getClassMetadataByURI(java.lang.String) <em>Get Class Metadata By URI</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Class Metadata By URI</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getClassMetadataByURI(java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetClassMetadataByURI__String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getClassMetadataByName(java.lang.String, java.lang.String) <em>Get Class Metadata By Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Class Metadata By Name</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getClassMetadataByName(java.lang.String, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetClassMetadataByName__String_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureMetadata(org.eclipse.emf.ecore.EStructuralFeature) <em>Get Feature Metadata</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Feature Metadata</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureMetadata(org.eclipse.emf.ecore.EStructuralFeature)
	 * @generated
	 */
	EOperation getMetadataService__GetFeatureMetadata__EStructuralFeature();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureMetadataByURI(java.lang.String) <em>Get Feature Metadata By URI</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Feature Metadata By URI</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureMetadataByURI(java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetFeatureMetadataByURI__String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureMetadataByName(java.lang.String, java.lang.String, java.lang.String) <em>Get Feature Metadata By Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Feature Metadata By Name</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureMetadataByName(java.lang.String, java.lang.String, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetFeatureMetadataByName__String_String_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureMetadataFromClass(java.lang.String, org.eclipse.fennec.model.metadata.ClassMetadata) <em>Get Feature Metadata From Class</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Feature Metadata From Class</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureMetadataFromClass(java.lang.String, org.eclipse.fennec.model.metadata.ClassMetadata)
	 * @generated
	 */
	EOperation getMetadataService__GetFeatureMetadataFromClass__String_ClassMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getPackageAspect(org.eclipse.emf.ecore.EPackage, java.lang.String) <em>Get Package Aspect</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Package Aspect</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getPackageAspect(org.eclipse.emf.ecore.EPackage, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetPackageAspect__EPackage_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getClassAspect(org.eclipse.emf.ecore.EClass, java.lang.String) <em>Get Class Aspect</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Class Aspect</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getClassAspect(org.eclipse.emf.ecore.EClass, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetClassAspect__EClass_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureAspect(org.eclipse.emf.ecore.EStructuralFeature, java.lang.String) <em>Get Feature Aspect</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Feature Aspect</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getFeatureAspect(org.eclipse.emf.ecore.EStructuralFeature, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetFeatureAspect__EStructuralFeature_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getPackageProfile(org.eclipse.emf.ecore.EPackage, java.lang.String) <em>Get Package Profile</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Package Profile</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getPackageProfile(org.eclipse.emf.ecore.EPackage, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetPackageProfile__EPackage_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getPackageProfileByNsURI(java.lang.String, java.lang.String) <em>Get Package Profile By Ns URI</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Package Profile By Ns URI</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getPackageProfileByNsURI(java.lang.String, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetPackageProfileByNsURI__String_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getClassProfile(org.eclipse.emf.ecore.EClass, java.lang.String) <em>Get Class Profile</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Class Profile</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getClassProfile(org.eclipse.emf.ecore.EClass, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetClassProfile__EClass_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getClassProfileByURI(java.lang.String, java.lang.String) <em>Get Class Profile By URI</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Class Profile By URI</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getClassProfileByURI(java.lang.String, java.lang.String)
	 * @generated
	 */
	EOperation getMetadataService__GetClassProfileByURI__String_String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService#getRegistry() <em>Get Registry</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Registry</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataService#getRegistry()
	 * @generated
	 */
	EOperation getMetadataService__GetRegistry();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.api.MetadataHandler <em>Metadata Handler</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata Handler</em>'.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataHandler
	 * @generated
	 */
	EClass getMetadataHandler();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataHandler#onPackageRegistered(org.eclipse.fennec.model.metadata.PackageMetadata) <em>On Package Registered</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>On Package Registered</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataHandler#onPackageRegistered(org.eclipse.fennec.model.metadata.PackageMetadata)
	 * @generated
	 */
	EOperation getMetadataHandler__OnPackageRegistered__PackageMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataHandler#onPackageUnregistered(org.eclipse.fennec.model.metadata.PackageMetadata) <em>On Package Unregistered</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>On Package Unregistered</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataHandler#onPackageUnregistered(org.eclipse.fennec.model.metadata.PackageMetadata)
	 * @generated
	 */
	EOperation getMetadataHandler__OnPackageUnregistered__PackageMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataHandler#clear() <em>Clear</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Clear</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataHandler#clear()
	 * @generated
	 */
	EOperation getMetadataHandler__Clear();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard <em>Metadata Whiteboard</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata Whiteboard</em>'.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard
	 * @generated
	 */
	EClass getMetadataWhiteboard();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#registerPackage(org.eclipse.emf.ecore.EPackage) <em>Register Package</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Register Package</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#registerPackage(org.eclipse.emf.ecore.EPackage)
	 * @generated
	 */
	EOperation getMetadataWhiteboard__RegisterPackage__EPackage();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#unregisterPackage(org.eclipse.emf.ecore.EPackage) <em>Unregister Package</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Unregister Package</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#unregisterPackage(org.eclipse.emf.ecore.EPackage)
	 * @generated
	 */
	EOperation getMetadataWhiteboard__UnregisterPackage__EPackage();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#registerAspectProvider(org.eclipse.fennec.model.metadata.api.AspectProvider) <em>Register Aspect Provider</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Register Aspect Provider</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#registerAspectProvider(org.eclipse.fennec.model.metadata.api.AspectProvider)
	 * @generated
	 */
	EOperation getMetadataWhiteboard__RegisterAspectProvider__AspectProvider();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#unregisterAspectProvider(org.eclipse.fennec.model.metadata.api.AspectProvider) <em>Unregister Aspect Provider</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Unregister Aspect Provider</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#unregisterAspectProvider(org.eclipse.fennec.model.metadata.api.AspectProvider)
	 * @generated
	 */
	EOperation getMetadataWhiteboard__UnregisterAspectProvider__AspectProvider();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#getAspectProviders() <em>Get Aspect Providers</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Aspect Providers</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#getAspectProviders()
	 * @generated
	 */
	EOperation getMetadataWhiteboard__GetAspectProviders();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#getMetadataIndex() <em>Get Metadata Index</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Metadata Index</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#getMetadataIndex()
	 * @generated
	 */
	EOperation getMetadataWhiteboard__GetMetadataIndex();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#setMetadataIndex(org.eclipse.fennec.model.metadata.api.MetadataIndex) <em>Set Metadata Index</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Set Metadata Index</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#setMetadataIndex(org.eclipse.fennec.model.metadata.api.MetadataIndex)
	 * @generated
	 */
	EOperation getMetadataWhiteboard__SetMetadataIndex__MetadataIndex();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#unsetMetadataIndex(org.eclipse.fennec.model.metadata.api.MetadataIndex) <em>Unset Metadata Index</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Unset Metadata Index</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#unsetMetadataIndex(org.eclipse.fennec.model.metadata.api.MetadataIndex)
	 * @generated
	 */
	EOperation getMetadataWhiteboard__UnsetMetadataIndex__MetadataIndex();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#addMetadataHandler(org.eclipse.fennec.model.metadata.api.MetadataHandler) <em>Add Metadata Handler</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Add Metadata Handler</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#addMetadataHandler(org.eclipse.fennec.model.metadata.api.MetadataHandler)
	 * @generated
	 */
	EOperation getMetadataWhiteboard__AddMetadataHandler__MetadataHandler();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#removeMetadataHandler(org.eclipse.fennec.model.metadata.api.MetadataHandler) <em>Remove Metadata Handler</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Remove Metadata Handler</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard#removeMetadataHandler(org.eclipse.fennec.model.metadata.api.MetadataHandler)
	 * @generated
	 */
	EOperation getMetadataWhiteboard__RemoveMetadataHandler__MetadataHandler();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.metadata.api.AspectProvider <em>Aspect Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Aspect Provider</em>'.
	 * @see org.eclipse.fennec.model.metadata.api.AspectProvider
	 * @generated
	 */
	EClass getAspectProvider();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.AspectProvider#getAspectTypeId() <em>Get Aspect Type Id</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Aspect Type Id</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.AspectProvider#getAspectTypeId()
	 * @generated
	 */
	EOperation getAspectProvider__GetAspectTypeId();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.AspectProvider#buildPackageAspect(org.eclipse.fennec.model.metadata.PackageMetadata) <em>Build Package Aspect</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Build Package Aspect</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.AspectProvider#buildPackageAspect(org.eclipse.fennec.model.metadata.PackageMetadata)
	 * @generated
	 */
	EOperation getAspectProvider__BuildPackageAspect__PackageMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.AspectProvider#buildClassAspect(org.eclipse.fennec.model.metadata.ClassMetadata) <em>Build Class Aspect</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Build Class Aspect</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.AspectProvider#buildClassAspect(org.eclipse.fennec.model.metadata.ClassMetadata)
	 * @generated
	 */
	EOperation getAspectProvider__BuildClassAspect__ClassMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.AspectProvider#buildFeatureAspect(org.eclipse.fennec.model.metadata.FeatureMetadata) <em>Build Feature Aspect</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Build Feature Aspect</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.AspectProvider#buildFeatureAspect(org.eclipse.fennec.model.metadata.FeatureMetadata)
	 * @generated
	 */
	EOperation getAspectProvider__BuildFeatureAspect__FeatureMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.AspectProvider#buildAttributeAspect(org.eclipse.fennec.model.metadata.AttributeMetadata) <em>Build Attribute Aspect</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Build Attribute Aspect</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.AspectProvider#buildAttributeAspect(org.eclipse.fennec.model.metadata.AttributeMetadata)
	 * @generated
	 */
	EOperation getAspectProvider__BuildAttributeAspect__AttributeMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.AspectProvider#buildReferenceAspect(org.eclipse.fennec.model.metadata.ReferenceMetadata) <em>Build Reference Aspect</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Build Reference Aspect</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.AspectProvider#buildReferenceAspect(org.eclipse.fennec.model.metadata.ReferenceMetadata)
	 * @generated
	 */
	EOperation getAspectProvider__BuildReferenceAspect__ReferenceMetadata();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.model.metadata.api.AspectProvider#buildProfiles(org.eclipse.fennec.model.metadata.PackageMetadata) <em>Build Profiles</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Build Profiles</em>' operation.
	 * @see org.eclipse.fennec.model.metadata.api.AspectProvider#buildProfiles(org.eclipse.fennec.model.metadata.PackageMetadata)
	 * @generated
	 */
	EOperation getAspectProvider__BuildProfiles__PackageMetadata();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ApiFactory getApiFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexReader <em>Metadata Index Reader</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexReader
		 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataIndexReader()
		 * @generated
		 */
		EClass METADATA_INDEX_READER = eINSTANCE.getMetadataIndexReader();

		/**
		 * The meta object literal for the '<em><b>Find By Instance Class Name</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_READER___FIND_BY_INSTANCE_CLASS_NAME__STRING_STRING = eINSTANCE.getMetadataIndexReader__FindByInstanceClassName__String_String();

		/**
		 * The meta object literal for the '<em><b>Find All By Instance Class Name</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_READER___FIND_ALL_BY_INSTANCE_CLASS_NAME__STRING = eINSTANCE.getMetadataIndexReader__FindAllByInstanceClassName__String();

		/**
		 * The meta object literal for the '<em><b>Find By Class Name</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_READER___FIND_BY_CLASS_NAME__STRING_STRING = eINSTANCE.getMetadataIndexReader__FindByClassName__String_String();

		/**
		 * The meta object literal for the '<em><b>Find All By Class Name</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_READER___FIND_ALL_BY_CLASS_NAME__STRING = eINSTANCE.getMetadataIndexReader__FindAllByClassName__String();

		/**
		 * The meta object literal for the '<em><b>Find Class By URI</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_READER___FIND_CLASS_BY_URI__STRING = eINSTANCE.getMetadataIndexReader__FindClassByURI__String();

		/**
		 * The meta object literal for the '<em><b>Find Feature By URI</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_READER___FIND_FEATURE_BY_URI__STRING = eINSTANCE.getMetadataIndexReader__FindFeatureByURI__String();

		/**
		 * The meta object literal for the '<em><b>Find Classes By Annotation</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_READER___FIND_CLASSES_BY_ANNOTATION__STRING_STRING_STRING = eINSTANCE.getMetadataIndexReader__FindClassesByAnnotation__String_String_String();

		/**
		 * The meta object literal for the '<em><b>Find Features By Annotation</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_READER___FIND_FEATURES_BY_ANNOTATION__STRING_STRING_STRING = eINSTANCE.getMetadataIndexReader__FindFeaturesByAnnotation__String_String_String();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndexWriter <em>Metadata Index Writer</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.api.MetadataIndexWriter
		 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataIndexWriter()
		 * @generated
		 */
		EClass METADATA_INDEX_WRITER = eINSTANCE.getMetadataIndexWriter();

		/**
		 * The meta object literal for the '<em><b>Index Package</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_WRITER___INDEX_PACKAGE__PACKAGEMETADATA = eINSTANCE.getMetadataIndexWriter__IndexPackage__PackageMetadata();

		/**
		 * The meta object literal for the '<em><b>Index Class</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_WRITER___INDEX_CLASS__CLASSMETADATA = eINSTANCE.getMetadataIndexWriter__IndexClass__ClassMetadata();

		/**
		 * The meta object literal for the '<em><b>Index Feature</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_WRITER___INDEX_FEATURE__FEATUREMETADATA = eINSTANCE.getMetadataIndexWriter__IndexFeature__FeatureMetadata();

		/**
		 * The meta object literal for the '<em><b>Remove Package</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_WRITER___REMOVE_PACKAGE__PACKAGEMETADATA = eINSTANCE.getMetadataIndexWriter__RemovePackage__PackageMetadata();

		/**
		 * The meta object literal for the '<em><b>Remove Class</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_WRITER___REMOVE_CLASS__CLASSMETADATA = eINSTANCE.getMetadataIndexWriter__RemoveClass__ClassMetadata();

		/**
		 * The meta object literal for the '<em><b>Remove Feature</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_WRITER___REMOVE_FEATURE__FEATUREMETADATA = eINSTANCE.getMetadataIndexWriter__RemoveFeature__FeatureMetadata();

		/**
		 * The meta object literal for the '<em><b>Clear</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_INDEX_WRITER___CLEAR = eINSTANCE.getMetadataIndexWriter__Clear();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.api.MetadataIndex <em>Metadata Index</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.api.MetadataIndex
		 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataIndex()
		 * @generated
		 */
		EClass METADATA_INDEX = eINSTANCE.getMetadataIndex();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.api.MetadataService <em>Metadata Service</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.api.MetadataService
		 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataService()
		 * @generated
		 */
		EClass METADATA_SERVICE = eINSTANCE.getMetadataService();

		/**
		 * The meta object literal for the '<em><b>Get Index Reader</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_INDEX_READER = eINSTANCE.getMetadataService__GetIndexReader();

		/**
		 * The meta object literal for the '<em><b>Get Package Metadata</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_PACKAGE_METADATA__STRING = eINSTANCE.getMetadataService__GetPackageMetadata__String();

		/**
		 * The meta object literal for the '<em><b>Get Class Metadata</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_CLASS_METADATA__ECLASS = eINSTANCE.getMetadataService__GetClassMetadata__EClass();

		/**
		 * The meta object literal for the '<em><b>Get Class Metadata By URI</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_CLASS_METADATA_BY_URI__STRING = eINSTANCE.getMetadataService__GetClassMetadataByURI__String();

		/**
		 * The meta object literal for the '<em><b>Get Class Metadata By Name</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_CLASS_METADATA_BY_NAME__STRING_STRING = eINSTANCE.getMetadataService__GetClassMetadataByName__String_String();

		/**
		 * The meta object literal for the '<em><b>Get Feature Metadata</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_FEATURE_METADATA__ESTRUCTURALFEATURE = eINSTANCE.getMetadataService__GetFeatureMetadata__EStructuralFeature();

		/**
		 * The meta object literal for the '<em><b>Get Feature Metadata By URI</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_FEATURE_METADATA_BY_URI__STRING = eINSTANCE.getMetadataService__GetFeatureMetadataByURI__String();

		/**
		 * The meta object literal for the '<em><b>Get Feature Metadata By Name</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_FEATURE_METADATA_BY_NAME__STRING_STRING_STRING = eINSTANCE.getMetadataService__GetFeatureMetadataByName__String_String_String();

		/**
		 * The meta object literal for the '<em><b>Get Feature Metadata From Class</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_FEATURE_METADATA_FROM_CLASS__STRING_CLASSMETADATA = eINSTANCE.getMetadataService__GetFeatureMetadataFromClass__String_ClassMetadata();

		/**
		 * The meta object literal for the '<em><b>Get Package Aspect</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_PACKAGE_ASPECT__EPACKAGE_STRING = eINSTANCE.getMetadataService__GetPackageAspect__EPackage_String();

		/**
		 * The meta object literal for the '<em><b>Get Class Aspect</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_CLASS_ASPECT__ECLASS_STRING = eINSTANCE.getMetadataService__GetClassAspect__EClass_String();

		/**
		 * The meta object literal for the '<em><b>Get Feature Aspect</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_FEATURE_ASPECT__ESTRUCTURALFEATURE_STRING = eINSTANCE.getMetadataService__GetFeatureAspect__EStructuralFeature_String();

		/**
		 * The meta object literal for the '<em><b>Get Package Profile</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_PACKAGE_PROFILE__EPACKAGE_STRING = eINSTANCE.getMetadataService__GetPackageProfile__EPackage_String();

		/**
		 * The meta object literal for the '<em><b>Get Package Profile By Ns URI</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_PACKAGE_PROFILE_BY_NS_URI__STRING_STRING = eINSTANCE.getMetadataService__GetPackageProfileByNsURI__String_String();

		/**
		 * The meta object literal for the '<em><b>Get Class Profile</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_CLASS_PROFILE__ECLASS_STRING = eINSTANCE.getMetadataService__GetClassProfile__EClass_String();

		/**
		 * The meta object literal for the '<em><b>Get Class Profile By URI</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_CLASS_PROFILE_BY_URI__STRING_STRING = eINSTANCE.getMetadataService__GetClassProfileByURI__String_String();

		/**
		 * The meta object literal for the '<em><b>Get Registry</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_SERVICE___GET_REGISTRY = eINSTANCE.getMetadataService__GetRegistry();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.api.MetadataHandler <em>Metadata Handler</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.api.MetadataHandler
		 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataHandler()
		 * @generated
		 */
		EClass METADATA_HANDLER = eINSTANCE.getMetadataHandler();

		/**
		 * The meta object literal for the '<em><b>On Package Registered</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_HANDLER___ON_PACKAGE_REGISTERED__PACKAGEMETADATA = eINSTANCE.getMetadataHandler__OnPackageRegistered__PackageMetadata();

		/**
		 * The meta object literal for the '<em><b>On Package Unregistered</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_HANDLER___ON_PACKAGE_UNREGISTERED__PACKAGEMETADATA = eINSTANCE.getMetadataHandler__OnPackageUnregistered__PackageMetadata();

		/**
		 * The meta object literal for the '<em><b>Clear</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_HANDLER___CLEAR = eINSTANCE.getMetadataHandler__Clear();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.api.MetadataWhiteboard <em>Metadata Whiteboard</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.api.MetadataWhiteboard
		 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getMetadataWhiteboard()
		 * @generated
		 */
		EClass METADATA_WHITEBOARD = eINSTANCE.getMetadataWhiteboard();

		/**
		 * The meta object literal for the '<em><b>Register Package</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___REGISTER_PACKAGE__EPACKAGE = eINSTANCE.getMetadataWhiteboard__RegisterPackage__EPackage();

		/**
		 * The meta object literal for the '<em><b>Unregister Package</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___UNREGISTER_PACKAGE__EPACKAGE = eINSTANCE.getMetadataWhiteboard__UnregisterPackage__EPackage();

		/**
		 * The meta object literal for the '<em><b>Register Aspect Provider</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___REGISTER_ASPECT_PROVIDER__ASPECTPROVIDER = eINSTANCE.getMetadataWhiteboard__RegisterAspectProvider__AspectProvider();

		/**
		 * The meta object literal for the '<em><b>Unregister Aspect Provider</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___UNREGISTER_ASPECT_PROVIDER__ASPECTPROVIDER = eINSTANCE.getMetadataWhiteboard__UnregisterAspectProvider__AspectProvider();

		/**
		 * The meta object literal for the '<em><b>Get Aspect Providers</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___GET_ASPECT_PROVIDERS = eINSTANCE.getMetadataWhiteboard__GetAspectProviders();

		/**
		 * The meta object literal for the '<em><b>Get Metadata Index</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___GET_METADATA_INDEX = eINSTANCE.getMetadataWhiteboard__GetMetadataIndex();

		/**
		 * The meta object literal for the '<em><b>Set Metadata Index</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___SET_METADATA_INDEX__METADATAINDEX = eINSTANCE.getMetadataWhiteboard__SetMetadataIndex__MetadataIndex();

		/**
		 * The meta object literal for the '<em><b>Unset Metadata Index</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___UNSET_METADATA_INDEX__METADATAINDEX = eINSTANCE.getMetadataWhiteboard__UnsetMetadataIndex__MetadataIndex();

		/**
		 * The meta object literal for the '<em><b>Add Metadata Handler</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___ADD_METADATA_HANDLER__METADATAHANDLER = eINSTANCE.getMetadataWhiteboard__AddMetadataHandler__MetadataHandler();

		/**
		 * The meta object literal for the '<em><b>Remove Metadata Handler</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation METADATA_WHITEBOARD___REMOVE_METADATA_HANDLER__METADATAHANDLER = eINSTANCE.getMetadataWhiteboard__RemoveMetadataHandler__MetadataHandler();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.metadata.api.AspectProvider <em>Aspect Provider</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.metadata.api.AspectProvider
		 * @see org.eclipse.fennec.model.metadata.api.impl.ApiPackageImpl#getAspectProvider()
		 * @generated
		 */
		EClass ASPECT_PROVIDER = eINSTANCE.getAspectProvider();

		/**
		 * The meta object literal for the '<em><b>Get Aspect Type Id</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ASPECT_PROVIDER___GET_ASPECT_TYPE_ID = eINSTANCE.getAspectProvider__GetAspectTypeId();

		/**
		 * The meta object literal for the '<em><b>Build Package Aspect</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ASPECT_PROVIDER___BUILD_PACKAGE_ASPECT__PACKAGEMETADATA = eINSTANCE.getAspectProvider__BuildPackageAspect__PackageMetadata();

		/**
		 * The meta object literal for the '<em><b>Build Class Aspect</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ASPECT_PROVIDER___BUILD_CLASS_ASPECT__CLASSMETADATA = eINSTANCE.getAspectProvider__BuildClassAspect__ClassMetadata();

		/**
		 * The meta object literal for the '<em><b>Build Feature Aspect</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ASPECT_PROVIDER___BUILD_FEATURE_ASPECT__FEATUREMETADATA = eINSTANCE.getAspectProvider__BuildFeatureAspect__FeatureMetadata();

		/**
		 * The meta object literal for the '<em><b>Build Attribute Aspect</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ASPECT_PROVIDER___BUILD_ATTRIBUTE_ASPECT__ATTRIBUTEMETADATA = eINSTANCE.getAspectProvider__BuildAttributeAspect__AttributeMetadata();

		/**
		 * The meta object literal for the '<em><b>Build Reference Aspect</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ASPECT_PROVIDER___BUILD_REFERENCE_ASPECT__REFERENCEMETADATA = eINSTANCE.getAspectProvider__BuildReferenceAspect__ReferenceMetadata();

		/**
		 * The meta object literal for the '<em><b>Build Profiles</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ASPECT_PROVIDER___BUILD_PROFILES__PACKAGEMETADATA = eINSTANCE.getAspectProvider__BuildProfiles__PackageMetadata();

	}

} //ApiPackage
