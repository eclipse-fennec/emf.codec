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
package org.eclipse.fennec.model.openapi;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;

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
 * <!-- begin-model-doc -->
 * OpenAPI 3.0.3 Specification Model
 * 
 * This EMF model provides a complete representation of the OpenAPI 3.0.3 specification.
 * It can be used to parse, validate, and generate OpenAPI documents.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.openapi.OpenApiFactory
 * @model kind="package"
 * @generated
 */
@ProviderType
@EPackage(uri = OpenApiPackage.eNS_URI, genModel = "/model/openapi_v3.genmodel", genModelSourceLocations = {"model/openapi_v3.genmodel","org.eclipse.fennec.openapi.model/model/openapi_v3.genmodel"}, ecore="/model/openapi_v3.ecore", ecoreSourceLocations="/model/openapi_v3.ecore")
public interface OpenApiPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "openapi";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://spec.openapis.org/oas/3.0.3/schema";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "openapi";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	OpenApiPackage eINSTANCE = org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl <em>Open API</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.OpenAPIImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getOpenAPI()
	 * @generated
	 */
	int OPEN_API = 0;

	/**
	 * The feature id for the '<em><b>Openapi</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__OPENAPI = 0;

	/**
	 * The feature id for the '<em><b>Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__INFO = 1;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__SERVERS = 2;

	/**
	 * The feature id for the '<em><b>Paths</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__PATHS = 3;

	/**
	 * The feature id for the '<em><b>Components</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__COMPONENTS = 4;

	/**
	 * The feature id for the '<em><b>Security</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__SECURITY = 5;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__TAGS = 6;

	/**
	 * The feature id for the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__EXTERNAL_DOCS = 7;

	/**
	 * The number of structural features of the '<em>Open API</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Open API</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.InfoImpl <em>Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.InfoImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getInfo()
	 * @generated
	 */
	int INFO = 1;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__TITLE = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Terms Of Service</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__TERMS_OF_SERVICE = 2;

	/**
	 * The feature id for the '<em><b>Contact</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__CONTACT = 3;

	/**
	 * The feature id for the '<em><b>License</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__LICENSE = 4;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__VERSION = 5;

	/**
	 * The number of structural features of the '<em>Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ContactImpl <em>Contact</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ContactImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getContact()
	 * @generated
	 */
	int CONTACT = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTACT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTACT__URL = 1;

	/**
	 * The feature id for the '<em><b>Email</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTACT__EMAIL = 2;

	/**
	 * The number of structural features of the '<em>Contact</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTACT_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Contact</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTACT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.LicenseImpl <em>License</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.LicenseImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getLicense()
	 * @generated
	 */
	int LICENSE = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LICENSE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LICENSE__URL = 1;

	/**
	 * The number of structural features of the '<em>License</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LICENSE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>License</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LICENSE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ServerImpl <em>Server</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ServerImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getServer()
	 * @generated
	 */
	int SERVER = 4;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER__URL = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Variables</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER__VARIABLES = 2;

	/**
	 * The number of structural features of the '<em>Server</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Server</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ServerVariableImpl <em>Server Variable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ServerVariableImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getServerVariable()
	 * @generated
	 */
	int SERVER_VARIABLE = 5;

	/**
	 * The feature id for the '<em><b>Enum</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE__ENUM = 0;

	/**
	 * The feature id for the '<em><b>Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE__DEFAULT = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE__DESCRIPTION = 2;

	/**
	 * The number of structural features of the '<em>Server Variable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Server Variable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.TagImpl <em>Tag</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.TagImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getTag()
	 * @generated
	 */
	int TAG = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__NAME = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__EXTERNAL_DOCS = 2;

	/**
	 * The number of structural features of the '<em>Tag</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Tag</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ExternalDocumentationImpl <em>External Documentation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ExternalDocumentationImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getExternalDocumentation()
	 * @generated
	 */
	int EXTERNAL_DOCUMENTATION = 7;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_DOCUMENTATION__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_DOCUMENTATION__URL = 1;

	/**
	 * The number of structural features of the '<em>External Documentation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_DOCUMENTATION_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>External Documentation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_DOCUMENTATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.PathItemImpl <em>Path Item</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.PathItemImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getPathItem()
	 * @generated
	 */
	int PATH_ITEM = 8;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__REF = 0;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__SUMMARY = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Get</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__GET = 3;

	/**
	 * The feature id for the '<em><b>Put</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__PUT = 4;

	/**
	 * The feature id for the '<em><b>Post</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__POST = 5;

	/**
	 * The feature id for the '<em><b>Delete</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__DELETE = 6;

	/**
	 * The feature id for the '<em><b>Options</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__OPTIONS = 7;

	/**
	 * The feature id for the '<em><b>Head</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__HEAD = 8;

	/**
	 * The feature id for the '<em><b>Patch</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__PATCH = 9;

	/**
	 * The feature id for the '<em><b>Trace</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__TRACE = 10;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__SERVERS = 11;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__PARAMETERS = 12;

	/**
	 * The number of structural features of the '<em>Path Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM_FEATURE_COUNT = 13;

	/**
	 * The number of operations of the '<em>Path Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.OperationImpl <em>Operation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.OperationImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getOperation()
	 * @generated
	 */
	int OPERATION = 9;

	/**
	 * The feature id for the '<em><b>Method</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__METHOD = 0;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__TAGS = 1;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__SUMMARY = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__EXTERNAL_DOCS = 4;

	/**
	 * The feature id for the '<em><b>Operation Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__OPERATION_ID = 5;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__PARAMETERS = 6;

	/**
	 * The feature id for the '<em><b>Request Body</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__REQUEST_BODY = 7;

	/**
	 * The feature id for the '<em><b>Responses</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__RESPONSES = 8;

	/**
	 * The feature id for the '<em><b>Callbacks</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__CALLBACKS = 9;

	/**
	 * The feature id for the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__DEPRECATED = 10;

	/**
	 * The feature id for the '<em><b>Security</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__SECURITY = 11;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__SERVERS = 12;

	/**
	 * The number of structural features of the '<em>Operation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_FEATURE_COUNT = 13;

	/**
	 * The number of operations of the '<em>Operation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ParameterImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 10;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__REF = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = 1;

	/**
	 * The feature id for the '<em><b>In</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__IN = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__REQUIRED = 4;

	/**
	 * The feature id for the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__DEPRECATED = 5;

	/**
	 * The feature id for the '<em><b>Allow Empty Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__ALLOW_EMPTY_VALUE = 6;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__STYLE = 7;

	/**
	 * The feature id for the '<em><b>Explode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__EXPLODE = 8;

	/**
	 * The feature id for the '<em><b>Allow Reserved</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__ALLOW_RESERVED = 9;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__SCHEMA = 10;

	/**
	 * The feature id for the '<em><b>Examples</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__EXAMPLES = 11;

	/**
	 * The feature id for the '<em><b>Content</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__CONTENT = 12;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = 13;

	/**
	 * The number of operations of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.RequestBodyImpl <em>Request Body</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.RequestBodyImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getRequestBody()
	 * @generated
	 */
	int REQUEST_BODY = 11;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__REF = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Content</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__CONTENT = 2;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__REQUIRED = 3;

	/**
	 * The number of structural features of the '<em>Request Body</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Request Body</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.MediaTypeImpl <em>Media Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.MediaTypeImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getMediaType()
	 * @generated
	 */
	int MEDIA_TYPE = 12;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__SCHEMA = 0;

	/**
	 * The feature id for the '<em><b>Examples</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__EXAMPLES = 1;

	/**
	 * The feature id for the '<em><b>Encoding</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__ENCODING = 2;

	/**
	 * The number of structural features of the '<em>Media Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Media Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.EncodingImpl <em>Encoding</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.EncodingImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getEncoding()
	 * @generated
	 */
	int ENCODING = 13;

	/**
	 * The feature id for the '<em><b>Content Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING__CONTENT_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Headers</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING__HEADERS = 1;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING__STYLE = 2;

	/**
	 * The feature id for the '<em><b>Explode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING__EXPLODE = 3;

	/**
	 * The feature id for the '<em><b>Allow Reserved</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING__ALLOW_RESERVED = 4;

	/**
	 * The number of structural features of the '<em>Encoding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Encoding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ResponseImpl <em>Response</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ResponseImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getResponse()
	 * @generated
	 */
	int RESPONSE = 14;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__REF = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Headers</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__HEADERS = 2;

	/**
	 * The feature id for the '<em><b>Content</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__CONTENT = 3;

	/**
	 * The feature id for the '<em><b>Links</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__LINKS = 4;

	/**
	 * The number of structural features of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl <em>Header</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.HeaderImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getHeader()
	 * @generated
	 */
	int HEADER = 15;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__REF = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__REQUIRED = 2;

	/**
	 * The feature id for the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__DEPRECATED = 3;

	/**
	 * The feature id for the '<em><b>Allow Empty Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__ALLOW_EMPTY_VALUE = 4;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__STYLE = 5;

	/**
	 * The feature id for the '<em><b>Explode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__EXPLODE = 6;

	/**
	 * The feature id for the '<em><b>Allow Reserved</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__ALLOW_RESERVED = 7;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__SCHEMA = 8;

	/**
	 * The feature id for the '<em><b>Examples</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__EXAMPLES = 9;

	/**
	 * The feature id for the '<em><b>Content</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__CONTENT = 10;

	/**
	 * The number of structural features of the '<em>Header</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER_FEATURE_COUNT = 11;

	/**
	 * The number of operations of the '<em>Header</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.CallbackImpl <em>Callback</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.CallbackImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getCallback()
	 * @generated
	 */
	int CALLBACK = 16;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK__REF = 0;

	/**
	 * The feature id for the '<em><b>Paths</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK__PATHS = 1;

	/**
	 * The number of structural features of the '<em>Callback</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Callback</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ExampleImpl <em>Example</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ExampleImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getExample()
	 * @generated
	 */
	int EXAMPLE = 17;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__REF = 0;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__SUMMARY = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__VALUE = 3;

	/**
	 * The feature id for the '<em><b>External Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__EXTERNAL_VALUE = 4;

	/**
	 * The number of structural features of the '<em>Example</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Example</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.LinkImpl <em>Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.LinkImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getLink()
	 * @generated
	 */
	int LINK = 18;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__REF = 0;

	/**
	 * The feature id for the '<em><b>Operation Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__OPERATION_REF = 1;

	/**
	 * The feature id for the '<em><b>Operation Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__OPERATION_ID = 2;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__PARAMETERS = 3;

	/**
	 * The feature id for the '<em><b>Request Body</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__REQUEST_BODY = 4;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__DESCRIPTION = 5;

	/**
	 * The feature id for the '<em><b>Server</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__SERVER = 6;

	/**
	 * The number of structural features of the '<em>Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.SchemaImpl <em>Schema</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.SchemaImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSchema()
	 * @generated
	 */
	int SCHEMA = 19;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__REF = 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__TYPE = 1;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__FORMAT = 2;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__TITLE = 3;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DESCRIPTION = 4;

	/**
	 * The feature id for the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__NULLABLE = 5;

	/**
	 * The feature id for the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DEPRECATED = 6;

	/**
	 * The feature id for the '<em><b>Read Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__READ_ONLY = 7;

	/**
	 * The feature id for the '<em><b>Write Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__WRITE_ONLY = 8;

	/**
	 * The feature id for the '<em><b>Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DEFAULT = 9;

	/**
	 * The feature id for the '<em><b>Enum</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ENUM = 10;

	/**
	 * The feature id for the '<em><b>Minimum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MINIMUM = 11;

	/**
	 * The feature id for the '<em><b>Maximum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MAXIMUM = 12;

	/**
	 * The feature id for the '<em><b>Exclusive Minimum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXCLUSIVE_MINIMUM = 13;

	/**
	 * The feature id for the '<em><b>Exclusive Maximum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXCLUSIVE_MAXIMUM = 14;

	/**
	 * The feature id for the '<em><b>Multiple Of</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MULTIPLE_OF = 15;

	/**
	 * The feature id for the '<em><b>Min Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MIN_LENGTH = 16;

	/**
	 * The feature id for the '<em><b>Max Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MAX_LENGTH = 17;

	/**
	 * The feature id for the '<em><b>Pattern</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__PATTERN = 18;

	/**
	 * The feature id for the '<em><b>Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ITEMS = 19;

	/**
	 * The feature id for the '<em><b>Min Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MIN_ITEMS = 20;

	/**
	 * The feature id for the '<em><b>Max Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MAX_ITEMS = 21;

	/**
	 * The feature id for the '<em><b>Unique Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__UNIQUE_ITEMS = 22;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__PROPERTIES = 23;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__REQUIRED = 24;

	/**
	 * The feature id for the '<em><b>Min Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MIN_PROPERTIES = 25;

	/**
	 * The feature id for the '<em><b>Max Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MAX_PROPERTIES = 26;

	/**
	 * The feature id for the '<em><b>Additional Properties</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ADDITIONAL_PROPERTIES = 27;

	/**
	 * The feature id for the '<em><b>Additional Properties Allowed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ADDITIONAL_PROPERTIES_ALLOWED = 28;

	/**
	 * The feature id for the '<em><b>All Of</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ALL_OF = 29;

	/**
	 * The feature id for the '<em><b>One Of</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ONE_OF = 30;

	/**
	 * The feature id for the '<em><b>Any Of</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ANY_OF = 31;

	/**
	 * The feature id for the '<em><b>Not</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__NOT = 32;

	/**
	 * The feature id for the '<em><b>Discriminator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DISCRIMINATOR = 33;

	/**
	 * The feature id for the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXTERNAL_DOCS = 34;

	/**
	 * The number of structural features of the '<em>Schema</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA_FEATURE_COUNT = 35;

	/**
	 * The number of operations of the '<em>Schema</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.DiscriminatorImpl <em>Discriminator</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.DiscriminatorImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getDiscriminator()
	 * @generated
	 */
	int DISCRIMINATOR = 20;

	/**
	 * The feature id for the '<em><b>Property Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR__PROPERTY_NAME = 0;

	/**
	 * The feature id for the '<em><b>Mapping</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR__MAPPING = 1;

	/**
	 * The number of structural features of the '<em>Discriminator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Discriminator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ComponentsImpl <em>Components</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ComponentsImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getComponents()
	 * @generated
	 */
	int COMPONENTS = 21;

	/**
	 * The feature id for the '<em><b>Schemas</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__SCHEMAS = 0;

	/**
	 * The feature id for the '<em><b>Schemas Package</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__SCHEMAS_PACKAGE = 1;

	/**
	 * The feature id for the '<em><b>Responses</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__RESPONSES = 2;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__PARAMETERS = 3;

	/**
	 * The feature id for the '<em><b>Examples</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__EXAMPLES = 4;

	/**
	 * The feature id for the '<em><b>Request Bodies</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__REQUEST_BODIES = 5;

	/**
	 * The feature id for the '<em><b>Headers</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__HEADERS = 6;

	/**
	 * The feature id for the '<em><b>Security Schemes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__SECURITY_SCHEMES = 7;

	/**
	 * The feature id for the '<em><b>Links</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__LINKS = 8;

	/**
	 * The feature id for the '<em><b>Callbacks</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__CALLBACKS = 9;

	/**
	 * The number of structural features of the '<em>Components</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_FEATURE_COUNT = 10;

	/**
	 * The number of operations of the '<em>Components</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl <em>Security Scheme</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecurityScheme()
	 * @generated
	 */
	int SECURITY_SCHEME = 22;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__TYPE = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__NAME = 2;

	/**
	 * The feature id for the '<em><b>In</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__IN = 3;

	/**
	 * The feature id for the '<em><b>Scheme</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__SCHEME = 4;

	/**
	 * The feature id for the '<em><b>Bearer Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__BEARER_FORMAT = 5;

	/**
	 * The feature id for the '<em><b>Flows</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__FLOWS = 6;

	/**
	 * The feature id for the '<em><b>Open Id Connect Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__OPEN_ID_CONNECT_URL = 7;

	/**
	 * The number of structural features of the '<em>Security Scheme</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Security Scheme</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.OAuthFlowsImpl <em>OAuth Flows</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.OAuthFlowsImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getOAuthFlows()
	 * @generated
	 */
	int OAUTH_FLOWS = 23;

	/**
	 * The feature id for the '<em><b>Implicit</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS__IMPLICIT = 0;

	/**
	 * The feature id for the '<em><b>Password</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS__PASSWORD = 1;

	/**
	 * The feature id for the '<em><b>Client Credentials</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS__CLIENT_CREDENTIALS = 2;

	/**
	 * The feature id for the '<em><b>Authorization Code</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS__AUTHORIZATION_CODE = 3;

	/**
	 * The number of structural features of the '<em>OAuth Flows</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>OAuth Flows</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.OAuthFlowImpl <em>OAuth Flow</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.OAuthFlowImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getOAuthFlow()
	 * @generated
	 */
	int OAUTH_FLOW = 24;

	/**
	 * The feature id for the '<em><b>Authorization Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__AUTHORIZATION_URL = 0;

	/**
	 * The feature id for the '<em><b>Token Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__TOKEN_URL = 1;

	/**
	 * The feature id for the '<em><b>Refresh Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__REFRESH_URL = 2;

	/**
	 * The feature id for the '<em><b>Scopes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__SCOPES = 3;

	/**
	 * The number of structural features of the '<em>OAuth Flow</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>OAuth Flow</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.SecurityRequirementImpl <em>Security Requirement</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.SecurityRequirementImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecurityRequirement()
	 * @generated
	 */
	int SECURITY_REQUIREMENT = 25;

	/**
	 * The feature id for the '<em><b>Schemes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_REQUIREMENT__SCHEMES = 0;

	/**
	 * The number of structural features of the '<em>Security Requirement</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_REQUIREMENT_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Security Requirement</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_REQUIREMENT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ExtensionImpl <em>Extension</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ExtensionImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getExtension()
	 * @generated
	 */
	int EXTENSION = 26;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Extension</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Extension</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.StringEntryImpl <em>String Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.StringEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getStringEntry()
	 * @generated
	 */
	int STRING_ENTRY = 27;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>String Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>String Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.PathEntryImpl <em>Path Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.PathEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getPathEntry()
	 * @generated
	 */
	int PATH_ENTRY = 28;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Path Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Path Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.SchemaEntryImpl <em>Schema Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.SchemaEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSchemaEntry()
	 * @generated
	 */
	int SCHEMA_ENTRY = 29;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Schema Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Schema Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ResponseEntryImpl <em>Response Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ResponseEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getResponseEntry()
	 * @generated
	 */
	int RESPONSE_ENTRY = 30;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Response Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Response Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ParameterEntryImpl <em>Parameter Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ParameterEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getParameterEntry()
	 * @generated
	 */
	int PARAMETER_ENTRY = 31;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Parameter Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Parameter Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ExampleEntryImpl <em>Example Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ExampleEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getExampleEntry()
	 * @generated
	 */
	int EXAMPLE_ENTRY = 32;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Example Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Example Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.RequestBodyEntryImpl <em>Request Body Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.RequestBodyEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getRequestBodyEntry()
	 * @generated
	 */
	int REQUEST_BODY_ENTRY = 33;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Request Body Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Request Body Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.HeaderEntryImpl <em>Header Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.HeaderEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getHeaderEntry()
	 * @generated
	 */
	int HEADER_ENTRY = 34;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Header Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Header Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeEntryImpl <em>Security Scheme Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.SecuritySchemeEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecuritySchemeEntry()
	 * @generated
	 */
	int SECURITY_SCHEME_ENTRY = 35;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Security Scheme Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Security Scheme Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.LinkEntryImpl <em>Link Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.LinkEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getLinkEntry()
	 * @generated
	 */
	int LINK_ENTRY = 36;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Link Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Link Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.CallbackEntryImpl <em>Callback Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.CallbackEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getCallbackEntry()
	 * @generated
	 */
	int CALLBACK_ENTRY = 37;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Callback Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Callback Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.MediaTypeEntryImpl <em>Media Type Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.MediaTypeEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getMediaTypeEntry()
	 * @generated
	 */
	int MEDIA_TYPE_ENTRY = 38;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Media Type Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Media Type Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.EncodingEntryImpl <em>Encoding Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.EncodingEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getEncodingEntry()
	 * @generated
	 */
	int ENCODING_ENTRY = 39;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Encoding Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Encoding Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.ServerVariableEntryImpl <em>Server Variable Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.ServerVariableEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getServerVariableEntry()
	 * @generated
	 */
	int SERVER_VARIABLE_ENTRY = 40;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Server Variable Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Server Variable Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.SecurityRequirementEntryImpl <em>Security Requirement Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.SecurityRequirementEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecurityRequirementEntry()
	 * @generated
	 */
	int SECURITY_REQUIREMENT_ENTRY = 41;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_REQUIREMENT_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_REQUIREMENT_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Security Requirement Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_REQUIREMENT_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Security Requirement Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_REQUIREMENT_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.impl.AnyEntryImpl <em>Any Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.impl.AnyEntryImpl
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getAnyEntry()
	 * @generated
	 */
	int ANY_ENTRY = 42;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANY_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANY_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Any Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANY_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Any Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANY_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.HttpMethod <em>Http Method</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.HttpMethod
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getHttpMethod()
	 * @generated
	 */
	int HTTP_METHOD = 43;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.ParameterLocation <em>Parameter Location</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.ParameterLocation
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getParameterLocation()
	 * @generated
	 */
	int PARAMETER_LOCATION = 44;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.ParameterStyle <em>Parameter Style</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.ParameterStyle
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getParameterStyle()
	 * @generated
	 */
	int PARAMETER_STYLE = 45;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.SecuritySchemeType <em>Security Scheme Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.SecuritySchemeType
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecuritySchemeType()
	 * @generated
	 */
	int SECURITY_SCHEME_TYPE = 46;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.openapi.ApiKeyLocation <em>Api Key Location</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.openapi.ApiKeyLocation
	 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getApiKeyLocation()
	 * @generated
	 */
	int API_KEY_LOCATION = 47;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.OpenAPI <em>Open API</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Open API</em>'.
	 * @see org.eclipse.fennec.model.openapi.OpenAPI
	 * @generated
	 */
	EClass getOpenAPI();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.OpenAPI#getOpenapi <em>Openapi</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Openapi</em>'.
	 * @see org.eclipse.fennec.model.openapi.OpenAPI#getOpenapi()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EAttribute getOpenAPI_Openapi();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.OpenAPI#getInfo <em>Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Info</em>'.
	 * @see org.eclipse.fennec.model.openapi.OpenAPI#getInfo()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Info();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.OpenAPI#getServers <em>Servers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Servers</em>'.
	 * @see org.eclipse.fennec.model.openapi.OpenAPI#getServers()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Servers();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.OpenAPI#getPaths <em>Paths</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Paths</em>'.
	 * @see org.eclipse.fennec.model.openapi.OpenAPI#getPaths()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Paths();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.OpenAPI#getComponents <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Components</em>'.
	 * @see org.eclipse.fennec.model.openapi.OpenAPI#getComponents()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Components();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.OpenAPI#getSecurity <em>Security</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Security</em>'.
	 * @see org.eclipse.fennec.model.openapi.OpenAPI#getSecurity()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Security();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.OpenAPI#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Tags</em>'.
	 * @see org.eclipse.fennec.model.openapi.OpenAPI#getTags()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Tags();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.OpenAPI#getExternalDocs <em>External Docs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>External Docs</em>'.
	 * @see org.eclipse.fennec.model.openapi.OpenAPI#getExternalDocs()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_ExternalDocs();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Info <em>Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Info</em>'.
	 * @see org.eclipse.fennec.model.openapi.Info
	 * @generated
	 */
	EClass getInfo();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Info#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.fennec.model.openapi.Info#getTitle()
	 * @see #getInfo()
	 * @generated
	 */
	EAttribute getInfo_Title();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Info#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Info#getDescription()
	 * @see #getInfo()
	 * @generated
	 */
	EAttribute getInfo_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Info#getTermsOfService <em>Terms Of Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Terms Of Service</em>'.
	 * @see org.eclipse.fennec.model.openapi.Info#getTermsOfService()
	 * @see #getInfo()
	 * @generated
	 */
	EAttribute getInfo_TermsOfService();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Info#getContact <em>Contact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Contact</em>'.
	 * @see org.eclipse.fennec.model.openapi.Info#getContact()
	 * @see #getInfo()
	 * @generated
	 */
	EReference getInfo_Contact();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Info#getLicense <em>License</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>License</em>'.
	 * @see org.eclipse.fennec.model.openapi.Info#getLicense()
	 * @see #getInfo()
	 * @generated
	 */
	EReference getInfo_License();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Info#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.fennec.model.openapi.Info#getVersion()
	 * @see #getInfo()
	 * @generated
	 */
	EAttribute getInfo_Version();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Contact <em>Contact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Contact</em>'.
	 * @see org.eclipse.fennec.model.openapi.Contact
	 * @generated
	 */
	EClass getContact();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Contact#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.openapi.Contact#getName()
	 * @see #getContact()
	 * @generated
	 */
	EAttribute getContact_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Contact#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.fennec.model.openapi.Contact#getUrl()
	 * @see #getContact()
	 * @generated
	 */
	EAttribute getContact_Url();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Contact#getEmail <em>Email</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Email</em>'.
	 * @see org.eclipse.fennec.model.openapi.Contact#getEmail()
	 * @see #getContact()
	 * @generated
	 */
	EAttribute getContact_Email();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.License <em>License</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>License</em>'.
	 * @see org.eclipse.fennec.model.openapi.License
	 * @generated
	 */
	EClass getLicense();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.License#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.openapi.License#getName()
	 * @see #getLicense()
	 * @generated
	 */
	EAttribute getLicense_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.License#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.fennec.model.openapi.License#getUrl()
	 * @see #getLicense()
	 * @generated
	 */
	EAttribute getLicense_Url();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Server <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Server</em>'.
	 * @see org.eclipse.fennec.model.openapi.Server
	 * @generated
	 */
	EClass getServer();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Server#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.fennec.model.openapi.Server#getUrl()
	 * @see #getServer()
	 * @generated
	 */
	EAttribute getServer_Url();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Server#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Server#getDescription()
	 * @see #getServer()
	 * @generated
	 */
	EAttribute getServer_Description();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Server#getVariables <em>Variables</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Variables</em>'.
	 * @see org.eclipse.fennec.model.openapi.Server#getVariables()
	 * @see #getServer()
	 * @generated
	 */
	EReference getServer_Variables();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.ServerVariable <em>Server Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Server Variable</em>'.
	 * @see org.eclipse.fennec.model.openapi.ServerVariable
	 * @generated
	 */
	EClass getServerVariable();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.openapi.ServerVariable#getEnum <em>Enum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Enum</em>'.
	 * @see org.eclipse.fennec.model.openapi.ServerVariable#getEnum()
	 * @see #getServerVariable()
	 * @generated
	 */
	EAttribute getServerVariable_Enum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.ServerVariable#getDefault <em>Default</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default</em>'.
	 * @see org.eclipse.fennec.model.openapi.ServerVariable#getDefault()
	 * @see #getServerVariable()
	 * @generated
	 */
	EAttribute getServerVariable_Default();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.ServerVariable#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.ServerVariable#getDescription()
	 * @see #getServerVariable()
	 * @generated
	 */
	EAttribute getServerVariable_Description();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Tag <em>Tag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tag</em>'.
	 * @see org.eclipse.fennec.model.openapi.Tag
	 * @generated
	 */
	EClass getTag();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Tag#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.openapi.Tag#getName()
	 * @see #getTag()
	 * @generated
	 */
	EAttribute getTag_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Tag#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Tag#getDescription()
	 * @see #getTag()
	 * @generated
	 */
	EAttribute getTag_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Tag#getExternalDocs <em>External Docs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>External Docs</em>'.
	 * @see org.eclipse.fennec.model.openapi.Tag#getExternalDocs()
	 * @see #getTag()
	 * @generated
	 */
	EReference getTag_ExternalDocs();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.ExternalDocumentation <em>External Documentation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>External Documentation</em>'.
	 * @see org.eclipse.fennec.model.openapi.ExternalDocumentation
	 * @generated
	 */
	EClass getExternalDocumentation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.ExternalDocumentation#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.ExternalDocumentation#getDescription()
	 * @see #getExternalDocumentation()
	 * @generated
	 */
	EAttribute getExternalDocumentation_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.ExternalDocumentation#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.fennec.model.openapi.ExternalDocumentation#getUrl()
	 * @see #getExternalDocumentation()
	 * @generated
	 */
	EAttribute getExternalDocumentation_Url();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.PathItem <em>Path Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Path Item</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem
	 * @generated
	 */
	EClass getPathItem();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.PathItem#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getRef()
	 * @see #getPathItem()
	 * @generated
	 */
	EAttribute getPathItem_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.PathItem#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getSummary()
	 * @see #getPathItem()
	 * @generated
	 */
	EAttribute getPathItem_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.PathItem#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getDescription()
	 * @see #getPathItem()
	 * @generated
	 */
	EAttribute getPathItem_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.PathItem#getGet <em>Get</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Get</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getGet()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Get();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.PathItem#getPut <em>Put</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Put</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getPut()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Put();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.PathItem#getPost <em>Post</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getPost()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Post();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.PathItem#getDelete <em>Delete</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Delete</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getDelete()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Delete();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.PathItem#getOptions <em>Options</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Options</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getOptions()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Options();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.PathItem#getHead <em>Head</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Head</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getHead()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Head();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.PathItem#getPatch <em>Patch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Patch</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getPatch()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Patch();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.PathItem#getTrace <em>Trace</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Trace</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getTrace()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Trace();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.PathItem#getServers <em>Servers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Servers</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getServers()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Servers();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.PathItem#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.eclipse.fennec.model.openapi.PathItem#getParameters()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Parameters();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Operation <em>Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Operation</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation
	 * @generated
	 */
	EClass getOperation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Operation#getMethod <em>Method</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getMethod()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Method();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.openapi.Operation#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Tags</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getTags()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Tags();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Operation#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getSummary()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Operation#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getDescription()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Operation#getExternalDocs <em>External Docs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>External Docs</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getExternalDocs()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_ExternalDocs();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Operation#getOperationId <em>Operation Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operation Id</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getOperationId()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_OperationId();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.Operation#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getParameters()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Parameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Operation#getRequestBody <em>Request Body</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Request Body</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getRequestBody()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_RequestBody();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Operation#getResponses <em>Responses</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Responses</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getResponses()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Responses();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Operation#getCallbacks <em>Callbacks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Callbacks</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getCallbacks()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Callbacks();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Operation#isDeprecated <em>Deprecated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deprecated</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#isDeprecated()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Deprecated();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.Operation#getSecurity <em>Security</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Security</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getSecurity()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Security();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.Operation#getServers <em>Servers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Servers</em>'.
	 * @see org.eclipse.fennec.model.openapi.Operation#getServers()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Servers();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#getRef()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#getName()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#getIn <em>In</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>In</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#getIn()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_In();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#getDescription()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#isRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#isRequired()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Required();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#isDeprecated <em>Deprecated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deprecated</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#isDeprecated()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Deprecated();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#isAllowEmptyValue <em>Allow Empty Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Empty Value</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#isAllowEmptyValue()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_AllowEmptyValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#getStyle <em>Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Style</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#getStyle()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Style();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#isExplode <em>Explode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Explode</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#isExplode()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Explode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Parameter#isAllowReserved <em>Allow Reserved</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Reserved</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#isAllowReserved()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_AllowReserved();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Parameter#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Schema</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#getSchema()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_Schema();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Parameter#getExamples <em>Examples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Examples</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#getExamples()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_Examples();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Parameter#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Content</em>'.
	 * @see org.eclipse.fennec.model.openapi.Parameter#getContent()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_Content();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.RequestBody <em>Request Body</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Request Body</em>'.
	 * @see org.eclipse.fennec.model.openapi.RequestBody
	 * @generated
	 */
	EClass getRequestBody();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.RequestBody#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.RequestBody#getRef()
	 * @see #getRequestBody()
	 * @generated
	 */
	EAttribute getRequestBody_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.RequestBody#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.RequestBody#getDescription()
	 * @see #getRequestBody()
	 * @generated
	 */
	EAttribute getRequestBody_Description();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.RequestBody#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Content</em>'.
	 * @see org.eclipse.fennec.model.openapi.RequestBody#getContent()
	 * @see #getRequestBody()
	 * @generated
	 */
	EReference getRequestBody_Content();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.RequestBody#isRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.fennec.model.openapi.RequestBody#isRequired()
	 * @see #getRequestBody()
	 * @generated
	 */
	EAttribute getRequestBody_Required();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.MediaType <em>Media Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Media Type</em>'.
	 * @see org.eclipse.fennec.model.openapi.MediaType
	 * @generated
	 */
	EClass getMediaType();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.MediaType#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Schema</em>'.
	 * @see org.eclipse.fennec.model.openapi.MediaType#getSchema()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_Schema();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.MediaType#getExamples <em>Examples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Examples</em>'.
	 * @see org.eclipse.fennec.model.openapi.MediaType#getExamples()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_Examples();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.MediaType#getEncoding <em>Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Encoding</em>'.
	 * @see org.eclipse.fennec.model.openapi.MediaType#getEncoding()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_Encoding();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Encoding <em>Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Encoding</em>'.
	 * @see org.eclipse.fennec.model.openapi.Encoding
	 * @generated
	 */
	EClass getEncoding();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Encoding#getContentType <em>Content Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Content Type</em>'.
	 * @see org.eclipse.fennec.model.openapi.Encoding#getContentType()
	 * @see #getEncoding()
	 * @generated
	 */
	EAttribute getEncoding_ContentType();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Encoding#getHeaders <em>Headers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Headers</em>'.
	 * @see org.eclipse.fennec.model.openapi.Encoding#getHeaders()
	 * @see #getEncoding()
	 * @generated
	 */
	EReference getEncoding_Headers();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Encoding#getStyle <em>Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Style</em>'.
	 * @see org.eclipse.fennec.model.openapi.Encoding#getStyle()
	 * @see #getEncoding()
	 * @generated
	 */
	EAttribute getEncoding_Style();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Encoding#isExplode <em>Explode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Explode</em>'.
	 * @see org.eclipse.fennec.model.openapi.Encoding#isExplode()
	 * @see #getEncoding()
	 * @generated
	 */
	EAttribute getEncoding_Explode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Encoding#isAllowReserved <em>Allow Reserved</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Reserved</em>'.
	 * @see org.eclipse.fennec.model.openapi.Encoding#isAllowReserved()
	 * @see #getEncoding()
	 * @generated
	 */
	EAttribute getEncoding_AllowReserved();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Response <em>Response</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response</em>'.
	 * @see org.eclipse.fennec.model.openapi.Response
	 * @generated
	 */
	EClass getResponse();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Response#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.Response#getRef()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Response#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Response#getDescription()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_Description();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Response#getHeaders <em>Headers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Headers</em>'.
	 * @see org.eclipse.fennec.model.openapi.Response#getHeaders()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Headers();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Response#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Content</em>'.
	 * @see org.eclipse.fennec.model.openapi.Response#getContent()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Content();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Response#getLinks <em>Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Links</em>'.
	 * @see org.eclipse.fennec.model.openapi.Response#getLinks()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Links();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Header <em>Header</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Header</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header
	 * @generated
	 */
	EClass getHeader();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Header#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#getRef()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Header#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#getDescription()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Header#isRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#isRequired()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Required();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Header#isDeprecated <em>Deprecated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deprecated</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#isDeprecated()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Deprecated();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Header#isAllowEmptyValue <em>Allow Empty Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Empty Value</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#isAllowEmptyValue()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_AllowEmptyValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Header#getStyle <em>Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Style</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#getStyle()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Style();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Header#isExplode <em>Explode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Explode</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#isExplode()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Explode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Header#isAllowReserved <em>Allow Reserved</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Reserved</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#isAllowReserved()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_AllowReserved();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Header#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Schema</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#getSchema()
	 * @see #getHeader()
	 * @generated
	 */
	EReference getHeader_Schema();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Header#getExamples <em>Examples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Examples</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#getExamples()
	 * @see #getHeader()
	 * @generated
	 */
	EReference getHeader_Examples();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Header#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Content</em>'.
	 * @see org.eclipse.fennec.model.openapi.Header#getContent()
	 * @see #getHeader()
	 * @generated
	 */
	EReference getHeader_Content();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Callback <em>Callback</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Callback</em>'.
	 * @see org.eclipse.fennec.model.openapi.Callback
	 * @generated
	 */
	EClass getCallback();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Callback#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.Callback#getRef()
	 * @see #getCallback()
	 * @generated
	 */
	EAttribute getCallback_Ref();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Callback#getPaths <em>Paths</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Paths</em>'.
	 * @see org.eclipse.fennec.model.openapi.Callback#getPaths()
	 * @see #getCallback()
	 * @generated
	 */
	EReference getCallback_Paths();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Example <em>Example</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Example</em>'.
	 * @see org.eclipse.fennec.model.openapi.Example
	 * @generated
	 */
	EClass getExample();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Example#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.Example#getRef()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Example#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.openapi.Example#getSummary()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Example#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Example#getDescription()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Example#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.openapi.Example#getValue()
	 * @see #getExample()
	 * @generated
	 */
	EReference getExample_Value();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Example#getExternalValue <em>External Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>External Value</em>'.
	 * @see org.eclipse.fennec.model.openapi.Example#getExternalValue()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_ExternalValue();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Link <em>Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Link</em>'.
	 * @see org.eclipse.fennec.model.openapi.Link
	 * @generated
	 */
	EClass getLink();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Link#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.Link#getRef()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Link#getOperationRef <em>Operation Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operation Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.Link#getOperationRef()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_OperationRef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Link#getOperationId <em>Operation Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operation Id</em>'.
	 * @see org.eclipse.fennec.model.openapi.Link#getOperationId()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_OperationId();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Link#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Parameters</em>'.
	 * @see org.eclipse.fennec.model.openapi.Link#getParameters()
	 * @see #getLink()
	 * @generated
	 */
	EReference getLink_Parameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Link#getRequestBody <em>Request Body</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Request Body</em>'.
	 * @see org.eclipse.fennec.model.openapi.Link#getRequestBody()
	 * @see #getLink()
	 * @generated
	 */
	EReference getLink_RequestBody();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Link#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Link#getDescription()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Link#getServer <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Server</em>'.
	 * @see org.eclipse.fennec.model.openapi.Link#getServer()
	 * @see #getLink()
	 * @generated
	 */
	EReference getLink_Server();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Schema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Schema</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema
	 * @generated
	 */
	EClass getSchema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getRef()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getType()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getFormat()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Format();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getTitle()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Title();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getDescription()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#isNullable <em>Nullable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nullable</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#isNullable()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Nullable();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#isDeprecated <em>Deprecated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deprecated</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#isDeprecated()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Deprecated();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#isReadOnly <em>Read Only</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Read Only</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#isReadOnly()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ReadOnly();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#isWriteOnly <em>Write Only</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Write Only</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#isWriteOnly()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_WriteOnly();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getDefault <em>Default</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getDefault()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Default();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.openapi.Schema#getEnum <em>Enum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Enum</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getEnum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Enum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getMinimum <em>Minimum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Minimum</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getMinimum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Minimum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getMaximum <em>Maximum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maximum</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getMaximum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Maximum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#isExclusiveMinimum <em>Exclusive Minimum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusive Minimum</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#isExclusiveMinimum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ExclusiveMinimum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#isExclusiveMaximum <em>Exclusive Maximum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusive Maximum</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#isExclusiveMaximum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ExclusiveMaximum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getMultipleOf <em>Multiple Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Multiple Of</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getMultipleOf()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MultipleOf();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getMinLength <em>Min Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Length</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getMinLength()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MinLength();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getMaxLength <em>Max Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Length</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getMaxLength()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MaxLength();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getPattern <em>Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pattern</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getPattern()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Pattern();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Schema#getItems <em>Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Items</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getItems()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Items();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getMinItems <em>Min Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Items</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getMinItems()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MinItems();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getMaxItems <em>Max Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Items</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getMaxItems()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MaxItems();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#isUniqueItems <em>Unique Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unique Items</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#isUniqueItems()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_UniqueItems();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Schema#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Properties</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Properties();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.openapi.Schema#getRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Required</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getRequired()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Required();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getMinProperties <em>Min Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Properties</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getMinProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MinProperties();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getMaxProperties <em>Max Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Properties</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getMaxProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MaxProperties();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Schema#getAdditionalProperties <em>Additional Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Additional Properties</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getAdditionalProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_AdditionalProperties();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Schema#getAdditionalPropertiesAllowed <em>Additional Properties Allowed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Additional Properties Allowed</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getAdditionalPropertiesAllowed()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_AdditionalPropertiesAllowed();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.Schema#getAllOf <em>All Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>All Of</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getAllOf()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_AllOf();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.Schema#getOneOf <em>One Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>One Of</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getOneOf()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_OneOf();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.openapi.Schema#getAnyOf <em>Any Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Any Of</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getAnyOf()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_AnyOf();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Schema#getNot <em>Not</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Not</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getNot()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Not();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Schema#getDiscriminator <em>Discriminator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Discriminator</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getDiscriminator()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Discriminator();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Schema#getExternalDocs <em>External Docs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>External Docs</em>'.
	 * @see org.eclipse.fennec.model.openapi.Schema#getExternalDocs()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_ExternalDocs();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Discriminator <em>Discriminator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Discriminator</em>'.
	 * @see org.eclipse.fennec.model.openapi.Discriminator
	 * @generated
	 */
	EClass getDiscriminator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Discriminator#getPropertyName <em>Property Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Property Name</em>'.
	 * @see org.eclipse.fennec.model.openapi.Discriminator#getPropertyName()
	 * @see #getDiscriminator()
	 * @generated
	 */
	EAttribute getDiscriminator_PropertyName();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Discriminator#getMapping <em>Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Mapping</em>'.
	 * @see org.eclipse.fennec.model.openapi.Discriminator#getMapping()
	 * @see #getDiscriminator()
	 * @generated
	 */
	EReference getDiscriminator_Mapping();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Components <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Components</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components
	 * @generated
	 */
	EClass getComponents();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Components#getSchemas <em>Schemas</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Schemas</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getSchemas()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Schemas();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Components#getSchemasPackage <em>Schemas Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Schemas Package</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getSchemasPackage()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_SchemasPackage();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Components#getResponses <em>Responses</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Responses</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getResponses()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Responses();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Components#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Parameters</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getParameters()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Parameters();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Components#getExamples <em>Examples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Examples</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getExamples()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Examples();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Components#getRequestBodies <em>Request Bodies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Request Bodies</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getRequestBodies()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_RequestBodies();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Components#getHeaders <em>Headers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Headers</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getHeaders()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Headers();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Components#getSecuritySchemes <em>Security Schemes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Security Schemes</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getSecuritySchemes()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_SecuritySchemes();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Components#getLinks <em>Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Links</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getLinks()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Links();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.Components#getCallbacks <em>Callbacks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Callbacks</em>'.
	 * @see org.eclipse.fennec.model.openapi.Components#getCallbacks()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Callbacks();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.SecurityScheme <em>Security Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Security Scheme</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityScheme
	 * @generated
	 */
	EClass getSecurityScheme();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityScheme#getType()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityScheme#getDescription()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityScheme#getName()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getIn <em>In</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>In</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityScheme#getIn()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_In();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getScheme <em>Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scheme</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityScheme#getScheme()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Scheme();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getBearerFormat <em>Bearer Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bearer Format</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityScheme#getBearerFormat()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_BearerFormat();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getFlows <em>Flows</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Flows</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityScheme#getFlows()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EReference getSecurityScheme_Flows();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getOpenIdConnectUrl <em>Open Id Connect Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Open Id Connect Url</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityScheme#getOpenIdConnectUrl()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_OpenIdConnectUrl();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.OAuthFlows <em>OAuth Flows</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>OAuth Flows</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlows
	 * @generated
	 */
	EClass getOAuthFlows();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.OAuthFlows#getImplicit <em>Implicit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Implicit</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlows#getImplicit()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_Implicit();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.OAuthFlows#getPassword <em>Password</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Password</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlows#getPassword()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_Password();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.OAuthFlows#getClientCredentials <em>Client Credentials</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Client Credentials</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlows#getClientCredentials()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_ClientCredentials();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.OAuthFlows#getAuthorizationCode <em>Authorization Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Authorization Code</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlows#getAuthorizationCode()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_AuthorizationCode();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.OAuthFlow <em>OAuth Flow</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>OAuth Flow</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlow
	 * @generated
	 */
	EClass getOAuthFlow();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.OAuthFlow#getAuthorizationUrl <em>Authorization Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Authorization Url</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlow#getAuthorizationUrl()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EAttribute getOAuthFlow_AuthorizationUrl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.OAuthFlow#getTokenUrl <em>Token Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Token Url</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlow#getTokenUrl()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EAttribute getOAuthFlow_TokenUrl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.OAuthFlow#getRefreshUrl <em>Refresh Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Refresh Url</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlow#getRefreshUrl()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EAttribute getOAuthFlow_RefreshUrl();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.OAuthFlow#getScopes <em>Scopes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Scopes</em>'.
	 * @see org.eclipse.fennec.model.openapi.OAuthFlow#getScopes()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EReference getOAuthFlow_Scopes();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.SecurityRequirement <em>Security Requirement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Security Requirement</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityRequirement
	 * @generated
	 */
	EClass getSecurityRequirement();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.openapi.SecurityRequirement#getSchemes <em>Schemes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Schemes</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecurityRequirement#getSchemes()
	 * @see #getSecurityRequirement()
	 * @generated
	 */
	EReference getSecurityRequirement_Schemes();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.openapi.Extension <em>Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Extension</em>'.
	 * @see org.eclipse.fennec.model.openapi.Extension
	 * @generated
	 */
	EClass getExtension();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.openapi.Extension#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.openapi.Extension#getName()
	 * @see #getExtension()
	 * @generated
	 */
	EAttribute getExtension_Name();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.openapi.Extension#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.openapi.Extension#getValue()
	 * @see #getExtension()
	 * @generated
	 */
	EReference getExtension_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EString"
	 * @generated
	 */
	EClass getStringEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringEntry()
	 * @generated
	 */
	EAttribute getStringEntry_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringEntry()
	 * @generated
	 */
	EAttribute getStringEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Path Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Path Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.PathItem" valueContainment="true"
	 * @generated
	 */
	EClass getPathEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getPathEntry()
	 * @generated
	 */
	EAttribute getPathEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getPathEntry()
	 * @generated
	 */
	EReference getPathEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Schema Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Schema Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.Schema" valueContainment="true"
	 * @generated
	 */
	EClass getSchemaEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getSchemaEntry()
	 * @generated
	 */
	EAttribute getSchemaEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getSchemaEntry()
	 * @generated
	 */
	EReference getSchemaEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Response Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.Response" valueContainment="true"
	 * @generated
	 */
	EClass getResponseEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getResponseEntry()
	 * @generated
	 */
	EAttribute getResponseEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getResponseEntry()
	 * @generated
	 */
	EReference getResponseEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Parameter Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.Parameter" valueContainment="true"
	 * @generated
	 */
	EClass getParameterEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getParameterEntry()
	 * @generated
	 */
	EAttribute getParameterEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getParameterEntry()
	 * @generated
	 */
	EReference getParameterEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Example Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Example Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.Example" valueContainment="true"
	 * @generated
	 */
	EClass getExampleEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getExampleEntry()
	 * @generated
	 */
	EAttribute getExampleEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getExampleEntry()
	 * @generated
	 */
	EReference getExampleEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Request Body Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Request Body Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.RequestBody" valueContainment="true"
	 * @generated
	 */
	EClass getRequestBodyEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getRequestBodyEntry()
	 * @generated
	 */
	EAttribute getRequestBodyEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getRequestBodyEntry()
	 * @generated
	 */
	EReference getRequestBodyEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Header Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Header Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.Header" valueContainment="true"
	 * @generated
	 */
	EClass getHeaderEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getHeaderEntry()
	 * @generated
	 */
	EAttribute getHeaderEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getHeaderEntry()
	 * @generated
	 */
	EReference getHeaderEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Security Scheme Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Security Scheme Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.SecurityScheme" valueContainment="true"
	 * @generated
	 */
	EClass getSecuritySchemeEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getSecuritySchemeEntry()
	 * @generated
	 */
	EAttribute getSecuritySchemeEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getSecuritySchemeEntry()
	 * @generated
	 */
	EReference getSecuritySchemeEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Link Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Link Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.Link" valueContainment="true"
	 * @generated
	 */
	EClass getLinkEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getLinkEntry()
	 * @generated
	 */
	EAttribute getLinkEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getLinkEntry()
	 * @generated
	 */
	EReference getLinkEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Callback Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Callback Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.Callback" valueContainment="true"
	 * @generated
	 */
	EClass getCallbackEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getCallbackEntry()
	 * @generated
	 */
	EAttribute getCallbackEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getCallbackEntry()
	 * @generated
	 */
	EReference getCallbackEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Media Type Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Media Type Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.MediaType" valueContainment="true"
	 * @generated
	 */
	EClass getMediaTypeEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getMediaTypeEntry()
	 * @generated
	 */
	EAttribute getMediaTypeEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getMediaTypeEntry()
	 * @generated
	 */
	EReference getMediaTypeEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Encoding Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Encoding Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.Encoding" valueContainment="true"
	 * @generated
	 */
	EClass getEncodingEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getEncodingEntry()
	 * @generated
	 */
	EAttribute getEncodingEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getEncodingEntry()
	 * @generated
	 */
	EReference getEncodingEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Server Variable Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Server Variable Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.openapi.ServerVariable" valueContainment="true"
	 * @generated
	 */
	EClass getServerVariableEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getServerVariableEntry()
	 * @generated
	 */
	EAttribute getServerVariableEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getServerVariableEntry()
	 * @generated
	 */
	EReference getServerVariableEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Security Requirement Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Security Requirement Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EString" valueMany="true"
	 * @generated
	 */
	EClass getSecurityRequirementEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getSecurityRequirementEntry()
	 * @generated
	 */
	EAttribute getSecurityRequirementEntry_Key();

	/**
	 * Returns the meta object for the attribute list '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getSecurityRequirementEntry()
	 * @generated
	 */
	EAttribute getSecurityRequirementEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Any Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Any Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.emf.ecore.EObject" valueContainment="true"
	 * @generated
	 */
	EClass getAnyEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getAnyEntry()
	 * @generated
	 */
	EAttribute getAnyEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getAnyEntry()
	 * @generated
	 */
	EReference getAnyEntry_Value();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.openapi.HttpMethod <em>Http Method</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Http Method</em>'.
	 * @see org.eclipse.fennec.model.openapi.HttpMethod
	 * @generated
	 */
	EEnum getHttpMethod();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.openapi.ParameterLocation <em>Parameter Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Parameter Location</em>'.
	 * @see org.eclipse.fennec.model.openapi.ParameterLocation
	 * @generated
	 */
	EEnum getParameterLocation();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.openapi.ParameterStyle <em>Parameter Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Parameter Style</em>'.
	 * @see org.eclipse.fennec.model.openapi.ParameterStyle
	 * @generated
	 */
	EEnum getParameterStyle();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.openapi.SecuritySchemeType <em>Security Scheme Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Security Scheme Type</em>'.
	 * @see org.eclipse.fennec.model.openapi.SecuritySchemeType
	 * @generated
	 */
	EEnum getSecuritySchemeType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.openapi.ApiKeyLocation <em>Api Key Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Api Key Location</em>'.
	 * @see org.eclipse.fennec.model.openapi.ApiKeyLocation
	 * @generated
	 */
	EEnum getApiKeyLocation();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	OpenApiFactory getOpenApiFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl <em>Open API</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.OpenAPIImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getOpenAPI()
		 * @generated
		 */
		EClass OPEN_API = eINSTANCE.getOpenAPI();

		/**
		 * The meta object literal for the '<em><b>Openapi</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPEN_API__OPENAPI = eINSTANCE.getOpenAPI_Openapi();

		/**
		 * The meta object literal for the '<em><b>Info</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__INFO = eINSTANCE.getOpenAPI_Info();

		/**
		 * The meta object literal for the '<em><b>Servers</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__SERVERS = eINSTANCE.getOpenAPI_Servers();

		/**
		 * The meta object literal for the '<em><b>Paths</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__PATHS = eINSTANCE.getOpenAPI_Paths();

		/**
		 * The meta object literal for the '<em><b>Components</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__COMPONENTS = eINSTANCE.getOpenAPI_Components();

		/**
		 * The meta object literal for the '<em><b>Security</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__SECURITY = eINSTANCE.getOpenAPI_Security();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__TAGS = eINSTANCE.getOpenAPI_Tags();

		/**
		 * The meta object literal for the '<em><b>External Docs</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__EXTERNAL_DOCS = eINSTANCE.getOpenAPI_ExternalDocs();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.InfoImpl <em>Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.InfoImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getInfo()
		 * @generated
		 */
		EClass INFO = eINSTANCE.getInfo();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INFO__TITLE = eINSTANCE.getInfo_Title();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INFO__DESCRIPTION = eINSTANCE.getInfo_Description();

		/**
		 * The meta object literal for the '<em><b>Terms Of Service</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INFO__TERMS_OF_SERVICE = eINSTANCE.getInfo_TermsOfService();

		/**
		 * The meta object literal for the '<em><b>Contact</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INFO__CONTACT = eINSTANCE.getInfo_Contact();

		/**
		 * The meta object literal for the '<em><b>License</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INFO__LICENSE = eINSTANCE.getInfo_License();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INFO__VERSION = eINSTANCE.getInfo_Version();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ContactImpl <em>Contact</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ContactImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getContact()
		 * @generated
		 */
		EClass CONTACT = eINSTANCE.getContact();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTACT__NAME = eINSTANCE.getContact_Name();

		/**
		 * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTACT__URL = eINSTANCE.getContact_Url();

		/**
		 * The meta object literal for the '<em><b>Email</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTACT__EMAIL = eINSTANCE.getContact_Email();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.LicenseImpl <em>License</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.LicenseImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getLicense()
		 * @generated
		 */
		EClass LICENSE = eINSTANCE.getLicense();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LICENSE__NAME = eINSTANCE.getLicense_Name();

		/**
		 * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LICENSE__URL = eINSTANCE.getLicense_Url();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ServerImpl <em>Server</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ServerImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getServer()
		 * @generated
		 */
		EClass SERVER = eINSTANCE.getServer();

		/**
		 * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER__URL = eINSTANCE.getServer_Url();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER__DESCRIPTION = eINSTANCE.getServer_Description();

		/**
		 * The meta object literal for the '<em><b>Variables</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER__VARIABLES = eINSTANCE.getServer_Variables();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ServerVariableImpl <em>Server Variable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ServerVariableImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getServerVariable()
		 * @generated
		 */
		EClass SERVER_VARIABLE = eINSTANCE.getServerVariable();

		/**
		 * The meta object literal for the '<em><b>Enum</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_VARIABLE__ENUM = eINSTANCE.getServerVariable_Enum();

		/**
		 * The meta object literal for the '<em><b>Default</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_VARIABLE__DEFAULT = eINSTANCE.getServerVariable_Default();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_VARIABLE__DESCRIPTION = eINSTANCE.getServerVariable_Description();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.TagImpl <em>Tag</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.TagImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getTag()
		 * @generated
		 */
		EClass TAG = eINSTANCE.getTag();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAG__NAME = eINSTANCE.getTag_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAG__DESCRIPTION = eINSTANCE.getTag_Description();

		/**
		 * The meta object literal for the '<em><b>External Docs</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TAG__EXTERNAL_DOCS = eINSTANCE.getTag_ExternalDocs();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ExternalDocumentationImpl <em>External Documentation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ExternalDocumentationImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getExternalDocumentation()
		 * @generated
		 */
		EClass EXTERNAL_DOCUMENTATION = eINSTANCE.getExternalDocumentation();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXTERNAL_DOCUMENTATION__DESCRIPTION = eINSTANCE.getExternalDocumentation_Description();

		/**
		 * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXTERNAL_DOCUMENTATION__URL = eINSTANCE.getExternalDocumentation_Url();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.PathItemImpl <em>Path Item</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.PathItemImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getPathItem()
		 * @generated
		 */
		EClass PATH_ITEM = eINSTANCE.getPathItem();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PATH_ITEM__REF = eINSTANCE.getPathItem_Ref();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PATH_ITEM__SUMMARY = eINSTANCE.getPathItem_Summary();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PATH_ITEM__DESCRIPTION = eINSTANCE.getPathItem_Description();

		/**
		 * The meta object literal for the '<em><b>Get</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__GET = eINSTANCE.getPathItem_Get();

		/**
		 * The meta object literal for the '<em><b>Put</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__PUT = eINSTANCE.getPathItem_Put();

		/**
		 * The meta object literal for the '<em><b>Post</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__POST = eINSTANCE.getPathItem_Post();

		/**
		 * The meta object literal for the '<em><b>Delete</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__DELETE = eINSTANCE.getPathItem_Delete();

		/**
		 * The meta object literal for the '<em><b>Options</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__OPTIONS = eINSTANCE.getPathItem_Options();

		/**
		 * The meta object literal for the '<em><b>Head</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__HEAD = eINSTANCE.getPathItem_Head();

		/**
		 * The meta object literal for the '<em><b>Patch</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__PATCH = eINSTANCE.getPathItem_Patch();

		/**
		 * The meta object literal for the '<em><b>Trace</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__TRACE = eINSTANCE.getPathItem_Trace();

		/**
		 * The meta object literal for the '<em><b>Servers</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__SERVERS = eINSTANCE.getPathItem_Servers();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__PARAMETERS = eINSTANCE.getPathItem_Parameters();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.OperationImpl <em>Operation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.OperationImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getOperation()
		 * @generated
		 */
		EClass OPERATION = eINSTANCE.getOperation();

		/**
		 * The meta object literal for the '<em><b>Method</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION__METHOD = eINSTANCE.getOperation_Method();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION__TAGS = eINSTANCE.getOperation_Tags();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION__SUMMARY = eINSTANCE.getOperation_Summary();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION__DESCRIPTION = eINSTANCE.getOperation_Description();

		/**
		 * The meta object literal for the '<em><b>External Docs</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__EXTERNAL_DOCS = eINSTANCE.getOperation_ExternalDocs();

		/**
		 * The meta object literal for the '<em><b>Operation Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION__OPERATION_ID = eINSTANCE.getOperation_OperationId();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__PARAMETERS = eINSTANCE.getOperation_Parameters();

		/**
		 * The meta object literal for the '<em><b>Request Body</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__REQUEST_BODY = eINSTANCE.getOperation_RequestBody();

		/**
		 * The meta object literal for the '<em><b>Responses</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__RESPONSES = eINSTANCE.getOperation_Responses();

		/**
		 * The meta object literal for the '<em><b>Callbacks</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__CALLBACKS = eINSTANCE.getOperation_Callbacks();

		/**
		 * The meta object literal for the '<em><b>Deprecated</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION__DEPRECATED = eINSTANCE.getOperation_Deprecated();

		/**
		 * The meta object literal for the '<em><b>Security</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__SECURITY = eINSTANCE.getOperation_Security();

		/**
		 * The meta object literal for the '<em><b>Servers</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__SERVERS = eINSTANCE.getOperation_Servers();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ParameterImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__REF = eINSTANCE.getParameter_Ref();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__NAME = eINSTANCE.getParameter_Name();

		/**
		 * The meta object literal for the '<em><b>In</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__IN = eINSTANCE.getParameter_In();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__DESCRIPTION = eINSTANCE.getParameter_Description();

		/**
		 * The meta object literal for the '<em><b>Required</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__REQUIRED = eINSTANCE.getParameter_Required();

		/**
		 * The meta object literal for the '<em><b>Deprecated</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__DEPRECATED = eINSTANCE.getParameter_Deprecated();

		/**
		 * The meta object literal for the '<em><b>Allow Empty Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__ALLOW_EMPTY_VALUE = eINSTANCE.getParameter_AllowEmptyValue();

		/**
		 * The meta object literal for the '<em><b>Style</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__STYLE = eINSTANCE.getParameter_Style();

		/**
		 * The meta object literal for the '<em><b>Explode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__EXPLODE = eINSTANCE.getParameter_Explode();

		/**
		 * The meta object literal for the '<em><b>Allow Reserved</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__ALLOW_RESERVED = eINSTANCE.getParameter_AllowReserved();

		/**
		 * The meta object literal for the '<em><b>Schema</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER__SCHEMA = eINSTANCE.getParameter_Schema();

		/**
		 * The meta object literal for the '<em><b>Examples</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER__EXAMPLES = eINSTANCE.getParameter_Examples();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER__CONTENT = eINSTANCE.getParameter_Content();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.RequestBodyImpl <em>Request Body</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.RequestBodyImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getRequestBody()
		 * @generated
		 */
		EClass REQUEST_BODY = eINSTANCE.getRequestBody();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST_BODY__REF = eINSTANCE.getRequestBody_Ref();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST_BODY__DESCRIPTION = eINSTANCE.getRequestBody_Description();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUEST_BODY__CONTENT = eINSTANCE.getRequestBody_Content();

		/**
		 * The meta object literal for the '<em><b>Required</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST_BODY__REQUIRED = eINSTANCE.getRequestBody_Required();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.MediaTypeImpl <em>Media Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.MediaTypeImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getMediaType()
		 * @generated
		 */
		EClass MEDIA_TYPE = eINSTANCE.getMediaType();

		/**
		 * The meta object literal for the '<em><b>Schema</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE__SCHEMA = eINSTANCE.getMediaType_Schema();

		/**
		 * The meta object literal for the '<em><b>Examples</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE__EXAMPLES = eINSTANCE.getMediaType_Examples();

		/**
		 * The meta object literal for the '<em><b>Encoding</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE__ENCODING = eINSTANCE.getMediaType_Encoding();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.EncodingImpl <em>Encoding</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.EncodingImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getEncoding()
		 * @generated
		 */
		EClass ENCODING = eINSTANCE.getEncoding();

		/**
		 * The meta object literal for the '<em><b>Content Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENCODING__CONTENT_TYPE = eINSTANCE.getEncoding_ContentType();

		/**
		 * The meta object literal for the '<em><b>Headers</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENCODING__HEADERS = eINSTANCE.getEncoding_Headers();

		/**
		 * The meta object literal for the '<em><b>Style</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENCODING__STYLE = eINSTANCE.getEncoding_Style();

		/**
		 * The meta object literal for the '<em><b>Explode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENCODING__EXPLODE = eINSTANCE.getEncoding_Explode();

		/**
		 * The meta object literal for the '<em><b>Allow Reserved</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENCODING__ALLOW_RESERVED = eINSTANCE.getEncoding_AllowReserved();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ResponseImpl <em>Response</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ResponseImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getResponse()
		 * @generated
		 */
		EClass RESPONSE = eINSTANCE.getResponse();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESPONSE__REF = eINSTANCE.getResponse_Ref();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESPONSE__DESCRIPTION = eINSTANCE.getResponse_Description();

		/**
		 * The meta object literal for the '<em><b>Headers</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE__HEADERS = eINSTANCE.getResponse_Headers();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE__CONTENT = eINSTANCE.getResponse_Content();

		/**
		 * The meta object literal for the '<em><b>Links</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE__LINKS = eINSTANCE.getResponse_Links();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl <em>Header</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.HeaderImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getHeader()
		 * @generated
		 */
		EClass HEADER = eINSTANCE.getHeader();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__REF = eINSTANCE.getHeader_Ref();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__DESCRIPTION = eINSTANCE.getHeader_Description();

		/**
		 * The meta object literal for the '<em><b>Required</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__REQUIRED = eINSTANCE.getHeader_Required();

		/**
		 * The meta object literal for the '<em><b>Deprecated</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__DEPRECATED = eINSTANCE.getHeader_Deprecated();

		/**
		 * The meta object literal for the '<em><b>Allow Empty Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__ALLOW_EMPTY_VALUE = eINSTANCE.getHeader_AllowEmptyValue();

		/**
		 * The meta object literal for the '<em><b>Style</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__STYLE = eINSTANCE.getHeader_Style();

		/**
		 * The meta object literal for the '<em><b>Explode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__EXPLODE = eINSTANCE.getHeader_Explode();

		/**
		 * The meta object literal for the '<em><b>Allow Reserved</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__ALLOW_RESERVED = eINSTANCE.getHeader_AllowReserved();

		/**
		 * The meta object literal for the '<em><b>Schema</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HEADER__SCHEMA = eINSTANCE.getHeader_Schema();

		/**
		 * The meta object literal for the '<em><b>Examples</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HEADER__EXAMPLES = eINSTANCE.getHeader_Examples();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HEADER__CONTENT = eINSTANCE.getHeader_Content();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.CallbackImpl <em>Callback</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.CallbackImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getCallback()
		 * @generated
		 */
		EClass CALLBACK = eINSTANCE.getCallback();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CALLBACK__REF = eINSTANCE.getCallback_Ref();

		/**
		 * The meta object literal for the '<em><b>Paths</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CALLBACK__PATHS = eINSTANCE.getCallback_Paths();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ExampleImpl <em>Example</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ExampleImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getExample()
		 * @generated
		 */
		EClass EXAMPLE = eINSTANCE.getExample();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXAMPLE__REF = eINSTANCE.getExample_Ref();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXAMPLE__SUMMARY = eINSTANCE.getExample_Summary();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXAMPLE__DESCRIPTION = eINSTANCE.getExample_Description();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EXAMPLE__VALUE = eINSTANCE.getExample_Value();

		/**
		 * The meta object literal for the '<em><b>External Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXAMPLE__EXTERNAL_VALUE = eINSTANCE.getExample_ExternalValue();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.LinkImpl <em>Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.LinkImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getLink()
		 * @generated
		 */
		EClass LINK = eINSTANCE.getLink();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK__REF = eINSTANCE.getLink_Ref();

		/**
		 * The meta object literal for the '<em><b>Operation Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK__OPERATION_REF = eINSTANCE.getLink_OperationRef();

		/**
		 * The meta object literal for the '<em><b>Operation Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK__OPERATION_ID = eINSTANCE.getLink_OperationId();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINK__PARAMETERS = eINSTANCE.getLink_Parameters();

		/**
		 * The meta object literal for the '<em><b>Request Body</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINK__REQUEST_BODY = eINSTANCE.getLink_RequestBody();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK__DESCRIPTION = eINSTANCE.getLink_Description();

		/**
		 * The meta object literal for the '<em><b>Server</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINK__SERVER = eINSTANCE.getLink_Server();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.SchemaImpl <em>Schema</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.SchemaImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSchema()
		 * @generated
		 */
		EClass SCHEMA = eINSTANCE.getSchema();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__REF = eINSTANCE.getSchema_Ref();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__TYPE = eINSTANCE.getSchema_Type();

		/**
		 * The meta object literal for the '<em><b>Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__FORMAT = eINSTANCE.getSchema_Format();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__TITLE = eINSTANCE.getSchema_Title();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__DESCRIPTION = eINSTANCE.getSchema_Description();

		/**
		 * The meta object literal for the '<em><b>Nullable</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__NULLABLE = eINSTANCE.getSchema_Nullable();

		/**
		 * The meta object literal for the '<em><b>Deprecated</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__DEPRECATED = eINSTANCE.getSchema_Deprecated();

		/**
		 * The meta object literal for the '<em><b>Read Only</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__READ_ONLY = eINSTANCE.getSchema_ReadOnly();

		/**
		 * The meta object literal for the '<em><b>Write Only</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__WRITE_ONLY = eINSTANCE.getSchema_WriteOnly();

		/**
		 * The meta object literal for the '<em><b>Default</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__DEFAULT = eINSTANCE.getSchema_Default();

		/**
		 * The meta object literal for the '<em><b>Enum</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__ENUM = eINSTANCE.getSchema_Enum();

		/**
		 * The meta object literal for the '<em><b>Minimum</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MINIMUM = eINSTANCE.getSchema_Minimum();

		/**
		 * The meta object literal for the '<em><b>Maximum</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MAXIMUM = eINSTANCE.getSchema_Maximum();

		/**
		 * The meta object literal for the '<em><b>Exclusive Minimum</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__EXCLUSIVE_MINIMUM = eINSTANCE.getSchema_ExclusiveMinimum();

		/**
		 * The meta object literal for the '<em><b>Exclusive Maximum</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__EXCLUSIVE_MAXIMUM = eINSTANCE.getSchema_ExclusiveMaximum();

		/**
		 * The meta object literal for the '<em><b>Multiple Of</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MULTIPLE_OF = eINSTANCE.getSchema_MultipleOf();

		/**
		 * The meta object literal for the '<em><b>Min Length</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MIN_LENGTH = eINSTANCE.getSchema_MinLength();

		/**
		 * The meta object literal for the '<em><b>Max Length</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MAX_LENGTH = eINSTANCE.getSchema_MaxLength();

		/**
		 * The meta object literal for the '<em><b>Pattern</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__PATTERN = eINSTANCE.getSchema_Pattern();

		/**
		 * The meta object literal for the '<em><b>Items</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ITEMS = eINSTANCE.getSchema_Items();

		/**
		 * The meta object literal for the '<em><b>Min Items</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MIN_ITEMS = eINSTANCE.getSchema_MinItems();

		/**
		 * The meta object literal for the '<em><b>Max Items</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MAX_ITEMS = eINSTANCE.getSchema_MaxItems();

		/**
		 * The meta object literal for the '<em><b>Unique Items</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__UNIQUE_ITEMS = eINSTANCE.getSchema_UniqueItems();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__PROPERTIES = eINSTANCE.getSchema_Properties();

		/**
		 * The meta object literal for the '<em><b>Required</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__REQUIRED = eINSTANCE.getSchema_Required();

		/**
		 * The meta object literal for the '<em><b>Min Properties</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MIN_PROPERTIES = eINSTANCE.getSchema_MinProperties();

		/**
		 * The meta object literal for the '<em><b>Max Properties</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MAX_PROPERTIES = eINSTANCE.getSchema_MaxProperties();

		/**
		 * The meta object literal for the '<em><b>Additional Properties</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ADDITIONAL_PROPERTIES = eINSTANCE.getSchema_AdditionalProperties();

		/**
		 * The meta object literal for the '<em><b>Additional Properties Allowed</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__ADDITIONAL_PROPERTIES_ALLOWED = eINSTANCE.getSchema_AdditionalPropertiesAllowed();

		/**
		 * The meta object literal for the '<em><b>All Of</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ALL_OF = eINSTANCE.getSchema_AllOf();

		/**
		 * The meta object literal for the '<em><b>One Of</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ONE_OF = eINSTANCE.getSchema_OneOf();

		/**
		 * The meta object literal for the '<em><b>Any Of</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ANY_OF = eINSTANCE.getSchema_AnyOf();

		/**
		 * The meta object literal for the '<em><b>Not</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__NOT = eINSTANCE.getSchema_Not();

		/**
		 * The meta object literal for the '<em><b>Discriminator</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__DISCRIMINATOR = eINSTANCE.getSchema_Discriminator();

		/**
		 * The meta object literal for the '<em><b>External Docs</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__EXTERNAL_DOCS = eINSTANCE.getSchema_ExternalDocs();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.DiscriminatorImpl <em>Discriminator</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.DiscriminatorImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getDiscriminator()
		 * @generated
		 */
		EClass DISCRIMINATOR = eINSTANCE.getDiscriminator();

		/**
		 * The meta object literal for the '<em><b>Property Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DISCRIMINATOR__PROPERTY_NAME = eINSTANCE.getDiscriminator_PropertyName();

		/**
		 * The meta object literal for the '<em><b>Mapping</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DISCRIMINATOR__MAPPING = eINSTANCE.getDiscriminator_Mapping();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ComponentsImpl <em>Components</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ComponentsImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getComponents()
		 * @generated
		 */
		EClass COMPONENTS = eINSTANCE.getComponents();

		/**
		 * The meta object literal for the '<em><b>Schemas</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__SCHEMAS = eINSTANCE.getComponents_Schemas();

		/**
		 * The meta object literal for the '<em><b>Schemas Package</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__SCHEMAS_PACKAGE = eINSTANCE.getComponents_SchemasPackage();

		/**
		 * The meta object literal for the '<em><b>Responses</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__RESPONSES = eINSTANCE.getComponents_Responses();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__PARAMETERS = eINSTANCE.getComponents_Parameters();

		/**
		 * The meta object literal for the '<em><b>Examples</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__EXAMPLES = eINSTANCE.getComponents_Examples();

		/**
		 * The meta object literal for the '<em><b>Request Bodies</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__REQUEST_BODIES = eINSTANCE.getComponents_RequestBodies();

		/**
		 * The meta object literal for the '<em><b>Headers</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__HEADERS = eINSTANCE.getComponents_Headers();

		/**
		 * The meta object literal for the '<em><b>Security Schemes</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__SECURITY_SCHEMES = eINSTANCE.getComponents_SecuritySchemes();

		/**
		 * The meta object literal for the '<em><b>Links</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__LINKS = eINSTANCE.getComponents_Links();

		/**
		 * The meta object literal for the '<em><b>Callbacks</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__CALLBACKS = eINSTANCE.getComponents_Callbacks();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl <em>Security Scheme</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecurityScheme()
		 * @generated
		 */
		EClass SECURITY_SCHEME = eINSTANCE.getSecurityScheme();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__TYPE = eINSTANCE.getSecurityScheme_Type();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__DESCRIPTION = eINSTANCE.getSecurityScheme_Description();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__NAME = eINSTANCE.getSecurityScheme_Name();

		/**
		 * The meta object literal for the '<em><b>In</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__IN = eINSTANCE.getSecurityScheme_In();

		/**
		 * The meta object literal for the '<em><b>Scheme</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__SCHEME = eINSTANCE.getSecurityScheme_Scheme();

		/**
		 * The meta object literal for the '<em><b>Bearer Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__BEARER_FORMAT = eINSTANCE.getSecurityScheme_BearerFormat();

		/**
		 * The meta object literal for the '<em><b>Flows</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SECURITY_SCHEME__FLOWS = eINSTANCE.getSecurityScheme_Flows();

		/**
		 * The meta object literal for the '<em><b>Open Id Connect Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__OPEN_ID_CONNECT_URL = eINSTANCE.getSecurityScheme_OpenIdConnectUrl();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.OAuthFlowsImpl <em>OAuth Flows</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.OAuthFlowsImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getOAuthFlows()
		 * @generated
		 */
		EClass OAUTH_FLOWS = eINSTANCE.getOAuthFlows();

		/**
		 * The meta object literal for the '<em><b>Implicit</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OAUTH_FLOWS__IMPLICIT = eINSTANCE.getOAuthFlows_Implicit();

		/**
		 * The meta object literal for the '<em><b>Password</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OAUTH_FLOWS__PASSWORD = eINSTANCE.getOAuthFlows_Password();

		/**
		 * The meta object literal for the '<em><b>Client Credentials</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OAUTH_FLOWS__CLIENT_CREDENTIALS = eINSTANCE.getOAuthFlows_ClientCredentials();

		/**
		 * The meta object literal for the '<em><b>Authorization Code</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OAUTH_FLOWS__AUTHORIZATION_CODE = eINSTANCE.getOAuthFlows_AuthorizationCode();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.OAuthFlowImpl <em>OAuth Flow</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.OAuthFlowImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getOAuthFlow()
		 * @generated
		 */
		EClass OAUTH_FLOW = eINSTANCE.getOAuthFlow();

		/**
		 * The meta object literal for the '<em><b>Authorization Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OAUTH_FLOW__AUTHORIZATION_URL = eINSTANCE.getOAuthFlow_AuthorizationUrl();

		/**
		 * The meta object literal for the '<em><b>Token Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OAUTH_FLOW__TOKEN_URL = eINSTANCE.getOAuthFlow_TokenUrl();

		/**
		 * The meta object literal for the '<em><b>Refresh Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OAUTH_FLOW__REFRESH_URL = eINSTANCE.getOAuthFlow_RefreshUrl();

		/**
		 * The meta object literal for the '<em><b>Scopes</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OAUTH_FLOW__SCOPES = eINSTANCE.getOAuthFlow_Scopes();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.SecurityRequirementImpl <em>Security Requirement</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.SecurityRequirementImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecurityRequirement()
		 * @generated
		 */
		EClass SECURITY_REQUIREMENT = eINSTANCE.getSecurityRequirement();

		/**
		 * The meta object literal for the '<em><b>Schemes</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SECURITY_REQUIREMENT__SCHEMES = eINSTANCE.getSecurityRequirement_Schemes();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ExtensionImpl <em>Extension</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ExtensionImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getExtension()
		 * @generated
		 */
		EClass EXTENSION = eINSTANCE.getExtension();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXTENSION__NAME = eINSTANCE.getExtension_Name();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EXTENSION__VALUE = eINSTANCE.getExtension_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.StringEntryImpl <em>String Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.StringEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getStringEntry()
		 * @generated
		 */
		EClass STRING_ENTRY = eINSTANCE.getStringEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_ENTRY__KEY = eINSTANCE.getStringEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_ENTRY__VALUE = eINSTANCE.getStringEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.PathEntryImpl <em>Path Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.PathEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getPathEntry()
		 * @generated
		 */
		EClass PATH_ENTRY = eINSTANCE.getPathEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PATH_ENTRY__KEY = eINSTANCE.getPathEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ENTRY__VALUE = eINSTANCE.getPathEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.SchemaEntryImpl <em>Schema Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.SchemaEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSchemaEntry()
		 * @generated
		 */
		EClass SCHEMA_ENTRY = eINSTANCE.getSchemaEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA_ENTRY__KEY = eINSTANCE.getSchemaEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA_ENTRY__VALUE = eINSTANCE.getSchemaEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ResponseEntryImpl <em>Response Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ResponseEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getResponseEntry()
		 * @generated
		 */
		EClass RESPONSE_ENTRY = eINSTANCE.getResponseEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESPONSE_ENTRY__KEY = eINSTANCE.getResponseEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE_ENTRY__VALUE = eINSTANCE.getResponseEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ParameterEntryImpl <em>Parameter Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ParameterEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getParameterEntry()
		 * @generated
		 */
		EClass PARAMETER_ENTRY = eINSTANCE.getParameterEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_ENTRY__KEY = eINSTANCE.getParameterEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER_ENTRY__VALUE = eINSTANCE.getParameterEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ExampleEntryImpl <em>Example Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ExampleEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getExampleEntry()
		 * @generated
		 */
		EClass EXAMPLE_ENTRY = eINSTANCE.getExampleEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXAMPLE_ENTRY__KEY = eINSTANCE.getExampleEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EXAMPLE_ENTRY__VALUE = eINSTANCE.getExampleEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.RequestBodyEntryImpl <em>Request Body Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.RequestBodyEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getRequestBodyEntry()
		 * @generated
		 */
		EClass REQUEST_BODY_ENTRY = eINSTANCE.getRequestBodyEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST_BODY_ENTRY__KEY = eINSTANCE.getRequestBodyEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUEST_BODY_ENTRY__VALUE = eINSTANCE.getRequestBodyEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.HeaderEntryImpl <em>Header Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.HeaderEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getHeaderEntry()
		 * @generated
		 */
		EClass HEADER_ENTRY = eINSTANCE.getHeaderEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER_ENTRY__KEY = eINSTANCE.getHeaderEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HEADER_ENTRY__VALUE = eINSTANCE.getHeaderEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeEntryImpl <em>Security Scheme Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.SecuritySchemeEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecuritySchemeEntry()
		 * @generated
		 */
		EClass SECURITY_SCHEME_ENTRY = eINSTANCE.getSecuritySchemeEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME_ENTRY__KEY = eINSTANCE.getSecuritySchemeEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SECURITY_SCHEME_ENTRY__VALUE = eINSTANCE.getSecuritySchemeEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.LinkEntryImpl <em>Link Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.LinkEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getLinkEntry()
		 * @generated
		 */
		EClass LINK_ENTRY = eINSTANCE.getLinkEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK_ENTRY__KEY = eINSTANCE.getLinkEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINK_ENTRY__VALUE = eINSTANCE.getLinkEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.CallbackEntryImpl <em>Callback Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.CallbackEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getCallbackEntry()
		 * @generated
		 */
		EClass CALLBACK_ENTRY = eINSTANCE.getCallbackEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CALLBACK_ENTRY__KEY = eINSTANCE.getCallbackEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CALLBACK_ENTRY__VALUE = eINSTANCE.getCallbackEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.MediaTypeEntryImpl <em>Media Type Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.MediaTypeEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getMediaTypeEntry()
		 * @generated
		 */
		EClass MEDIA_TYPE_ENTRY = eINSTANCE.getMediaTypeEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEDIA_TYPE_ENTRY__KEY = eINSTANCE.getMediaTypeEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE_ENTRY__VALUE = eINSTANCE.getMediaTypeEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.EncodingEntryImpl <em>Encoding Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.EncodingEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getEncodingEntry()
		 * @generated
		 */
		EClass ENCODING_ENTRY = eINSTANCE.getEncodingEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENCODING_ENTRY__KEY = eINSTANCE.getEncodingEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENCODING_ENTRY__VALUE = eINSTANCE.getEncodingEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.ServerVariableEntryImpl <em>Server Variable Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.ServerVariableEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getServerVariableEntry()
		 * @generated
		 */
		EClass SERVER_VARIABLE_ENTRY = eINSTANCE.getServerVariableEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_VARIABLE_ENTRY__KEY = eINSTANCE.getServerVariableEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_VARIABLE_ENTRY__VALUE = eINSTANCE.getServerVariableEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.SecurityRequirementEntryImpl <em>Security Requirement Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.SecurityRequirementEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecurityRequirementEntry()
		 * @generated
		 */
		EClass SECURITY_REQUIREMENT_ENTRY = eINSTANCE.getSecurityRequirementEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_REQUIREMENT_ENTRY__KEY = eINSTANCE.getSecurityRequirementEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_REQUIREMENT_ENTRY__VALUE = eINSTANCE.getSecurityRequirementEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.impl.AnyEntryImpl <em>Any Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.impl.AnyEntryImpl
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getAnyEntry()
		 * @generated
		 */
		EClass ANY_ENTRY = eINSTANCE.getAnyEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANY_ENTRY__KEY = eINSTANCE.getAnyEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANY_ENTRY__VALUE = eINSTANCE.getAnyEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.HttpMethod <em>Http Method</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.HttpMethod
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getHttpMethod()
		 * @generated
		 */
		EEnum HTTP_METHOD = eINSTANCE.getHttpMethod();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.ParameterLocation <em>Parameter Location</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.ParameterLocation
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getParameterLocation()
		 * @generated
		 */
		EEnum PARAMETER_LOCATION = eINSTANCE.getParameterLocation();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.ParameterStyle <em>Parameter Style</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.ParameterStyle
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getParameterStyle()
		 * @generated
		 */
		EEnum PARAMETER_STYLE = eINSTANCE.getParameterStyle();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.SecuritySchemeType <em>Security Scheme Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.SecuritySchemeType
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getSecuritySchemeType()
		 * @generated
		 */
		EEnum SECURITY_SCHEME_TYPE = eINSTANCE.getSecuritySchemeType();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.openapi.ApiKeyLocation <em>Api Key Location</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.openapi.ApiKeyLocation
		 * @see org.eclipse.fennec.model.openapi.impl.OpenApiPackageImpl#getApiKeyLocation()
		 * @generated
		 */
		EEnum API_KEY_LOCATION = eINSTANCE.getApiKeyLocation();

	}

} //OpenApiPackage
