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
package org.eclipse.fennec.model.oas;


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
 * <!-- begin-model-doc -->
 * OpenAPI 3.0, 3.1 and 3.2 in one model (issue #299). Features carry the version they appear in (annotation http://eclipse.org/fennec/openapi, key since); without it a feature exists in every version.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.oas.OasFactory
 * @model kind="package"
 * @generated
 */
@ProviderType
@EPackage(uri = OasPackage.eNS_URI, fingerprint = "fp1:414f076916e03368ad7f6893bc50e79b79c80ea1a4586e7ac074e2341b205d29", genModel = "/model/oas.genmodel", genModelSourceLocations = {"model/oas.genmodel","org.eclipse.fennec.openapi.model/model/oas.genmodel"}, ecore = "/model/oas.ecore", ecoreSourceLocations = "/model/oas.ecore")
public interface OasPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "oas";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/openapi/3";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "oas";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	OasPackage eINSTANCE = org.eclipse.fennec.model.oas.impl.OasPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.OpenAPIImpl <em>Open API</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.OpenAPIImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOpenAPI()
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
	 * The feature id for the '<em><b>Self</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__SELF = 1;

	/**
	 * The feature id for the '<em><b>Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__INFO = 2;

	/**
	 * The feature id for the '<em><b>Json Schema Dialect</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__JSON_SCHEMA_DIALECT = 3;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__SERVERS = 4;

	/**
	 * The feature id for the '<em><b>Paths</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__PATHS = 5;

	/**
	 * The feature id for the '<em><b>Webhooks</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__WEBHOOKS = 6;

	/**
	 * The feature id for the '<em><b>Components</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__COMPONENTS = 7;

	/**
	 * The feature id for the '<em><b>Security</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__SECURITY = 8;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__TAGS = 9;

	/**
	 * The feature id for the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__EXTERNAL_DOCS = 10;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API__EXTENSIONS = 11;

	/**
	 * The number of structural features of the '<em>Open API</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API_FEATURE_COUNT = 12;

	/**
	 * The number of operations of the '<em>Open API</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPEN_API_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.InfoImpl <em>Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.InfoImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getInfo()
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
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__SUMMARY = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Terms Of Service</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__TERMS_OF_SERVICE = 3;

	/**
	 * The feature id for the '<em><b>Contact</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__CONTACT = 4;

	/**
	 * The feature id for the '<em><b>License</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__LICENSE = 5;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__VERSION = 6;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO__EXTENSIONS = 7;

	/**
	 * The number of structural features of the '<em>Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INFO_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ContactImpl <em>Contact</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ContactImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getContact()
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
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTACT__EXTENSIONS = 3;

	/**
	 * The number of structural features of the '<em>Contact</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTACT_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Contact</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTACT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.LicenseImpl <em>License</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.LicenseImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getLicense()
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
	 * The feature id for the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LICENSE__IDENTIFIER = 1;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LICENSE__URL = 2;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LICENSE__EXTENSIONS = 3;

	/**
	 * The number of structural features of the '<em>License</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LICENSE_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>License</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LICENSE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ServerImpl <em>Server</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ServerImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getServer()
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
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER__NAME = 2;

	/**
	 * The feature id for the '<em><b>Variables</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER__VARIABLES = 3;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER__EXTENSIONS = 4;

	/**
	 * The number of structural features of the '<em>Server</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Server</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ServerVariableImpl <em>Server Variable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ServerVariableImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getServerVariable()
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
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE__EXTENSIONS = 3;

	/**
	 * The number of structural features of the '<em>Server Variable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Server Variable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_VARIABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl <em>Components</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ComponentsImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getComponents()
	 * @generated
	 */
	int COMPONENTS = 6;

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
	 * The feature id for the '<em><b>Path Items</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__PATH_ITEMS = 10;

	/**
	 * The feature id for the '<em><b>Media Types</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__MEDIA_TYPES = 11;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS__EXTENSIONS = 12;

	/**
	 * The number of structural features of the '<em>Components</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_FEATURE_COUNT = 13;

	/**
	 * The number of operations of the '<em>Components</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.PathItemImpl <em>Path Item</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.PathItemImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getPathItem()
	 * @generated
	 */
	int PATH_ITEM = 7;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__REF = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__SUMMARY = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Get</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__GET = 4;

	/**
	 * The feature id for the '<em><b>Put</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__PUT = 5;

	/**
	 * The feature id for the '<em><b>Post</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__POST = 6;

	/**
	 * The feature id for the '<em><b>Delete</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__DELETE = 7;

	/**
	 * The feature id for the '<em><b>Options</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__OPTIONS = 8;

	/**
	 * The feature id for the '<em><b>Head</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__HEAD = 9;

	/**
	 * The feature id for the '<em><b>Patch</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__PATCH = 10;

	/**
	 * The feature id for the '<em><b>Trace</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__TRACE = 11;

	/**
	 * The feature id for the '<em><b>Query</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__QUERY = 12;

	/**
	 * The feature id for the '<em><b>Additional Operations</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__ADDITIONAL_OPERATIONS = 13;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__SERVERS = 14;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__PARAMETERS = 15;

	/**
	 * The feature id for the '<em><b>All Operations</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__ALL_OPERATIONS = 16;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM__EXTENSIONS = 17;

	/**
	 * The number of structural features of the '<em>Path Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM_FEATURE_COUNT = 18;

	/**
	 * The number of operations of the '<em>Path Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.OperationImpl <em>Operation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.OperationImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOperation()
	 * @generated
	 */
	int OPERATION = 8;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__TAGS = 0;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__SUMMARY = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__EXTERNAL_DOCS = 3;

	/**
	 * The feature id for the '<em><b>Operation Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__OPERATION_ID = 4;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__PARAMETERS = 5;

	/**
	 * The feature id for the '<em><b>Request Body</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__REQUEST_BODY = 6;

	/**
	 * The feature id for the '<em><b>Responses</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__RESPONSES = 7;

	/**
	 * The feature id for the '<em><b>Callbacks</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__CALLBACKS = 8;

	/**
	 * The feature id for the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__DEPRECATED = 9;

	/**
	 * The feature id for the '<em><b>Security</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__SECURITY = 10;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__SERVERS = 11;

	/**
	 * The feature id for the '<em><b>Method</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__METHOD = 12;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION__EXTENSIONS = 13;

	/**
	 * The number of structural features of the '<em>Operation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_FEATURE_COUNT = 14;

	/**
	 * The number of operations of the '<em>Operation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ExternalDocumentationImpl <em>External Documentation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ExternalDocumentationImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getExternalDocumentation()
	 * @generated
	 */
	int EXTERNAL_DOCUMENTATION = 9;

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
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_DOCUMENTATION__EXTENSIONS = 2;

	/**
	 * The number of structural features of the '<em>External Documentation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_DOCUMENTATION_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>External Documentation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_DOCUMENTATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ParameterImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getParameter()
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
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = 2;

	/**
	 * The feature id for the '<em><b>In</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__IN = 3;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__SUMMARY = 4;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__DESCRIPTION = 5;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__REQUIRED = 6;

	/**
	 * The feature id for the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__DEPRECATED = 7;

	/**
	 * The feature id for the '<em><b>Allow Empty Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__ALLOW_EMPTY_VALUE = 8;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__STYLE = 9;

	/**
	 * The feature id for the '<em><b>Explode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__EXPLODE = 10;

	/**
	 * The feature id for the '<em><b>Allow Reserved</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__ALLOW_RESERVED = 11;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__SCHEMA = 12;

	/**
	 * The feature id for the '<em><b>Example</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__EXAMPLE = 13;

	/**
	 * The feature id for the '<em><b>Examples</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__EXAMPLES = 14;

	/**
	 * The feature id for the '<em><b>Content</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__CONTENT = 15;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__EXTENSIONS = 16;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = 17;

	/**
	 * The number of operations of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.RequestBodyImpl <em>Request Body</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.RequestBodyImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getRequestBody()
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
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__SUMMARY = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Content</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__CONTENT = 4;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__REQUIRED = 5;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY__EXTENSIONS = 6;

	/**
	 * The number of structural features of the '<em>Request Body</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Request Body</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUEST_BODY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl <em>Media Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.MediaTypeImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getMediaType()
	 * @generated
	 */
	int MEDIA_TYPE = 12;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__REF = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__SUMMARY = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__SCHEMA = 4;

	/**
	 * The feature id for the '<em><b>Item Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__ITEM_SCHEMA = 5;

	/**
	 * The feature id for the '<em><b>Example</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__EXAMPLE = 6;

	/**
	 * The feature id for the '<em><b>Examples</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__EXAMPLES = 7;

	/**
	 * The feature id for the '<em><b>Encoding</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__ENCODING = 8;

	/**
	 * The feature id for the '<em><b>Prefix Encoding</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__PREFIX_ENCODING = 9;

	/**
	 * The feature id for the '<em><b>Item Encoding</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__ITEM_ENCODING = 10;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE__EXTENSIONS = 11;

	/**
	 * The number of structural features of the '<em>Media Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE_FEATURE_COUNT = 12;

	/**
	 * The number of operations of the '<em>Media Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEDIA_TYPE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.EncodingImpl <em>Encoding</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.EncodingImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getEncoding()
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
	 * The feature id for the '<em><b>Encoding</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING__ENCODING = 5;

	/**
	 * The feature id for the '<em><b>Prefix Encoding</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING__PREFIX_ENCODING = 6;

	/**
	 * The feature id for the '<em><b>Item Encoding</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING__ITEM_ENCODING = 7;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING__EXTENSIONS = 8;

	/**
	 * The number of structural features of the '<em>Encoding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING_FEATURE_COUNT = 9;

	/**
	 * The number of operations of the '<em>Encoding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENCODING_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ResponseImpl <em>Response</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ResponseImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getResponse()
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
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__SUMMARY = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Headers</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__HEADERS = 4;

	/**
	 * The feature id for the '<em><b>Content</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__CONTENT = 5;

	/**
	 * The feature id for the '<em><b>Links</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__LINKS = 6;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__EXTENSIONS = 7;

	/**
	 * The number of structural features of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.CallbackImpl <em>Callback</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.CallbackImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getCallback()
	 * @generated
	 */
	int CALLBACK = 15;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK__REF = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Expressions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK__EXPRESSIONS = 2;

	/**
	 * The number of structural features of the '<em>Callback</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Callback</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALLBACK_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ExampleImpl <em>Example</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ExampleImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getExample()
	 * @generated
	 */
	int EXAMPLE = 16;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__REF = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__SUMMARY = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__VALUE = 4;

	/**
	 * The feature id for the '<em><b>Data Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__DATA_VALUE = 5;

	/**
	 * The feature id for the '<em><b>Serialized Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__SERIALIZED_VALUE = 6;

	/**
	 * The feature id for the '<em><b>External Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__EXTERNAL_VALUE = 7;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE__EXTENSIONS = 8;

	/**
	 * The number of structural features of the '<em>Example</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE_FEATURE_COUNT = 9;

	/**
	 * The number of operations of the '<em>Example</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXAMPLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.LinkImpl <em>Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.LinkImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getLink()
	 * @generated
	 */
	int LINK = 17;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__REF = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Operation Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__OPERATION_REF = 2;

	/**
	 * The feature id for the '<em><b>Operation Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__OPERATION_ID = 3;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__PARAMETERS = 4;

	/**
	 * The feature id for the '<em><b>Request Body</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__REQUEST_BODY = 5;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__SUMMARY = 6;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__DESCRIPTION = 7;

	/**
	 * The feature id for the '<em><b>Server</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__SERVER = 8;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__EXTENSIONS = 9;

	/**
	 * The number of structural features of the '<em>Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK_FEATURE_COUNT = 10;

	/**
	 * The number of operations of the '<em>Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.HeaderImpl <em>Header</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.HeaderImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getHeader()
	 * @generated
	 */
	int HEADER = 18;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__REF = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__SUMMARY = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__REQUIRED = 4;

	/**
	 * The feature id for the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__DEPRECATED = 5;

	/**
	 * The feature id for the '<em><b>Allow Empty Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__ALLOW_EMPTY_VALUE = 6;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__STYLE = 7;

	/**
	 * The feature id for the '<em><b>Explode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__EXPLODE = 8;

	/**
	 * The feature id for the '<em><b>Allow Reserved</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__ALLOW_RESERVED = 9;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__SCHEMA = 10;

	/**
	 * The feature id for the '<em><b>Example</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__EXAMPLE = 11;

	/**
	 * The feature id for the '<em><b>Examples</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__EXAMPLES = 12;

	/**
	 * The feature id for the '<em><b>Content</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__CONTENT = 13;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER__EXTENSIONS = 14;

	/**
	 * The number of structural features of the '<em>Header</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER_FEATURE_COUNT = 15;

	/**
	 * The number of operations of the '<em>Header</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEADER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.TagImpl <em>Tag</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.TagImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getTag()
	 * @generated
	 */
	int TAG = 19;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__NAME = 0;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__SUMMARY = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__EXTERNAL_DOCS = 3;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__PARENT = 4;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__KIND = 5;

	/**
	 * The feature id for the '<em><b>Parent Tag</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__PARENT_TAG = 6;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__EXTENSIONS = 7;

	/**
	 * The number of structural features of the '<em>Tag</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Tag</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl <em>Security Scheme</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecurityScheme()
	 * @generated
	 */
	int SECURITY_SCHEME = 20;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__REF = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__TYPE = 2;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__SUMMARY = 3;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__DESCRIPTION = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__NAME = 5;

	/**
	 * The feature id for the '<em><b>In</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__IN = 6;

	/**
	 * The feature id for the '<em><b>Scheme</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__SCHEME = 7;

	/**
	 * The feature id for the '<em><b>Bearer Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__BEARER_FORMAT = 8;

	/**
	 * The feature id for the '<em><b>Flows</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__FLOWS = 9;

	/**
	 * The feature id for the '<em><b>Open Id Connect Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__OPEN_ID_CONNECT_URL = 10;

	/**
	 * The feature id for the '<em><b>Oauth2 Metadata Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__OAUTH2_METADATA_URL = 11;

	/**
	 * The feature id for the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__DEPRECATED = 12;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME__EXTENSIONS = 13;

	/**
	 * The number of structural features of the '<em>Security Scheme</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME_FEATURE_COUNT = 14;

	/**
	 * The number of operations of the '<em>Security Scheme</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SECURITY_SCHEME_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl <em>OAuth Flows</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOAuthFlows()
	 * @generated
	 */
	int OAUTH_FLOWS = 21;

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
	 * The feature id for the '<em><b>Device Authorization</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS__DEVICE_AUTHORIZATION = 4;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS__EXTENSIONS = 5;

	/**
	 * The number of structural features of the '<em>OAuth Flows</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>OAuth Flows</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOWS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.OAuthFlowImpl <em>OAuth Flow</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.OAuthFlowImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOAuthFlow()
	 * @generated
	 */
	int OAUTH_FLOW = 22;

	/**
	 * The feature id for the '<em><b>Authorization Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__AUTHORIZATION_URL = 0;

	/**
	 * The feature id for the '<em><b>Device Authorization Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__DEVICE_AUTHORIZATION_URL = 1;

	/**
	 * The feature id for the '<em><b>Token Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__TOKEN_URL = 2;

	/**
	 * The feature id for the '<em><b>Refresh Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__REFRESH_URL = 3;

	/**
	 * The feature id for the '<em><b>Scopes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__SCOPES = 4;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW__EXTENSIONS = 5;

	/**
	 * The number of structural features of the '<em>OAuth Flow</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>OAuth Flow</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OAUTH_FLOW_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.SecurityRequirementImpl <em>Security Requirement</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.SecurityRequirementImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecurityRequirement()
	 * @generated
	 */
	int SECURITY_REQUIREMENT = 23;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.PathItemEntryImpl <em>Path Item Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.PathItemEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getPathItemEntry()
	 * @generated
	 */
	int PATH_ITEM_ENTRY = 24;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Path Item Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Path Item Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATH_ITEM_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.OperationEntryImpl <em>Operation Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.OperationEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOperationEntry()
	 * @generated
	 */
	int OPERATION_ENTRY = 25;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Operation Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Operation Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OPERATION_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ResponseEntryImpl <em>Response Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ResponseEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getResponseEntry()
	 * @generated
	 */
	int RESPONSE_ENTRY = 26;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ParameterEntryImpl <em>Parameter Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ParameterEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getParameterEntry()
	 * @generated
	 */
	int PARAMETER_ENTRY = 27;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ExampleEntryImpl <em>Example Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ExampleEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getExampleEntry()
	 * @generated
	 */
	int EXAMPLE_ENTRY = 28;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.RequestBodyEntryImpl <em>Request Body Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.RequestBodyEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getRequestBodyEntry()
	 * @generated
	 */
	int REQUEST_BODY_ENTRY = 29;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.HeaderEntryImpl <em>Header Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.HeaderEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getHeaderEntry()
	 * @generated
	 */
	int HEADER_ENTRY = 30;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeEntryImpl <em>Security Scheme Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.SecuritySchemeEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecuritySchemeEntry()
	 * @generated
	 */
	int SECURITY_SCHEME_ENTRY = 31;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.LinkEntryImpl <em>Link Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.LinkEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getLinkEntry()
	 * @generated
	 */
	int LINK_ENTRY = 32;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.CallbackEntryImpl <em>Callback Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.CallbackEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getCallbackEntry()
	 * @generated
	 */
	int CALLBACK_ENTRY = 33;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.MediaTypeEntryImpl <em>Media Type Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.MediaTypeEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getMediaTypeEntry()
	 * @generated
	 */
	int MEDIA_TYPE_ENTRY = 34;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.EncodingEntryImpl <em>Encoding Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.EncodingEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getEncodingEntry()
	 * @generated
	 */
	int ENCODING_ENTRY = 35;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ServerVariableEntryImpl <em>Server Variable Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ServerVariableEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getServerVariableEntry()
	 * @generated
	 */
	int SERVER_VARIABLE_ENTRY = 36;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.StringEntryImpl <em>String Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.StringEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getStringEntry()
	 * @generated
	 */
	int STRING_ENTRY = 37;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.AnyEntryImpl <em>Any Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.AnyEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getAnyEntry()
	 * @generated
	 */
	int ANY_ENTRY = 38;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANY_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.ExtensionEntryImpl <em>Extension Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.ExtensionEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getExtensionEntry()
	 * @generated
	 */
	int EXTENSION_ENTRY = 39;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Extension Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Extension Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.SecurityRequirementEntryImpl <em>Security Requirement Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.SecurityRequirementEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecurityRequirementEntry()
	 * @generated
	 */
	int SECURITY_REQUIREMENT_ENTRY = 40;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.impl.SchemaEntryImpl <em>Schema Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.impl.SchemaEntryImpl
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSchemaEntry()
	 * @generated
	 */
	int SCHEMA_ENTRY = 41;

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
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.ParameterLocation <em>Parameter Location</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.ParameterLocation
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getParameterLocation()
	 * @generated
	 */
	int PARAMETER_LOCATION = 42;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.ParameterStyle <em>Parameter Style</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.ParameterStyle
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getParameterStyle()
	 * @generated
	 */
	int PARAMETER_STYLE = 43;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.SecuritySchemeType <em>Security Scheme Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.SecuritySchemeType
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecuritySchemeType()
	 * @generated
	 */
	int SECURITY_SCHEME_TYPE = 44;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.model.oas.ApiKeyLocation <em>Api Key Location</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.model.oas.ApiKeyLocation
	 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getApiKeyLocation()
	 * @generated
	 */
	int API_KEY_LOCATION = 45;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.OpenAPI <em>Open API</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Open API</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI
	 * @generated
	 */
	EClass getOpenAPI();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.OpenAPI#getOpenapi <em>Openapi</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Openapi</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getOpenapi()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EAttribute getOpenAPI_Openapi();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.OpenAPI#getSelf <em>Self</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Self</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getSelf()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EAttribute getOpenAPI_Self();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.OpenAPI#getInfo <em>Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Info</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getInfo()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Info();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.OpenAPI#getJsonSchemaDialect <em>Json Schema Dialect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Json Schema Dialect</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getJsonSchemaDialect()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EAttribute getOpenAPI_JsonSchemaDialect();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.OpenAPI#getServers <em>Servers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Servers</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getServers()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Servers();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.OpenAPI#getPaths <em>Paths</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Paths</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getPaths()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Paths();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.OpenAPI#getWebhooks <em>Webhooks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Webhooks</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getWebhooks()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Webhooks();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.OpenAPI#getComponents <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Components</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getComponents()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Components();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.OpenAPI#getSecurity <em>Security</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Security</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getSecurity()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Security();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.OpenAPI#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Tags</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getTags()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Tags();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.OpenAPI#getExternalDocs <em>External Docs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>External Docs</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getExternalDocs()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_ExternalDocs();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.OpenAPI#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.OpenAPI#getExtensions()
	 * @see #getOpenAPI()
	 * @generated
	 */
	EReference getOpenAPI_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Info <em>Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Info</em>'.
	 * @see org.eclipse.fennec.model.oas.Info
	 * @generated
	 */
	EClass getInfo();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Info#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.fennec.model.oas.Info#getTitle()
	 * @see #getInfo()
	 * @generated
	 */
	EAttribute getInfo_Title();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Info#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.Info#getSummary()
	 * @see #getInfo()
	 * @generated
	 */
	EAttribute getInfo_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Info#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.Info#getDescription()
	 * @see #getInfo()
	 * @generated
	 */
	EAttribute getInfo_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Info#getTermsOfService <em>Terms Of Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Terms Of Service</em>'.
	 * @see org.eclipse.fennec.model.oas.Info#getTermsOfService()
	 * @see #getInfo()
	 * @generated
	 */
	EAttribute getInfo_TermsOfService();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Info#getContact <em>Contact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Contact</em>'.
	 * @see org.eclipse.fennec.model.oas.Info#getContact()
	 * @see #getInfo()
	 * @generated
	 */
	EReference getInfo_Contact();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Info#getLicense <em>License</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>License</em>'.
	 * @see org.eclipse.fennec.model.oas.Info#getLicense()
	 * @see #getInfo()
	 * @generated
	 */
	EReference getInfo_License();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Info#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.fennec.model.oas.Info#getVersion()
	 * @see #getInfo()
	 * @generated
	 */
	EAttribute getInfo_Version();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Info#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Info#getExtensions()
	 * @see #getInfo()
	 * @generated
	 */
	EReference getInfo_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Contact <em>Contact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Contact</em>'.
	 * @see org.eclipse.fennec.model.oas.Contact
	 * @generated
	 */
	EClass getContact();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Contact#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.oas.Contact#getName()
	 * @see #getContact()
	 * @generated
	 */
	EAttribute getContact_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Contact#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.fennec.model.oas.Contact#getUrl()
	 * @see #getContact()
	 * @generated
	 */
	EAttribute getContact_Url();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Contact#getEmail <em>Email</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Email</em>'.
	 * @see org.eclipse.fennec.model.oas.Contact#getEmail()
	 * @see #getContact()
	 * @generated
	 */
	EAttribute getContact_Email();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Contact#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Contact#getExtensions()
	 * @see #getContact()
	 * @generated
	 */
	EReference getContact_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.License <em>License</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>License</em>'.
	 * @see org.eclipse.fennec.model.oas.License
	 * @generated
	 */
	EClass getLicense();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.License#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.oas.License#getName()
	 * @see #getLicense()
	 * @generated
	 */
	EAttribute getLicense_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.License#getIdentifier <em>Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Identifier</em>'.
	 * @see org.eclipse.fennec.model.oas.License#getIdentifier()
	 * @see #getLicense()
	 * @generated
	 */
	EAttribute getLicense_Identifier();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.License#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.fennec.model.oas.License#getUrl()
	 * @see #getLicense()
	 * @generated
	 */
	EAttribute getLicense_Url();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.License#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.License#getExtensions()
	 * @see #getLicense()
	 * @generated
	 */
	EReference getLicense_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Server <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Server</em>'.
	 * @see org.eclipse.fennec.model.oas.Server
	 * @generated
	 */
	EClass getServer();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Server#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.fennec.model.oas.Server#getUrl()
	 * @see #getServer()
	 * @generated
	 */
	EAttribute getServer_Url();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Server#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.Server#getDescription()
	 * @see #getServer()
	 * @generated
	 */
	EAttribute getServer_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Server#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.oas.Server#getName()
	 * @see #getServer()
	 * @generated
	 */
	EAttribute getServer_Name();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Server#getVariables <em>Variables</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Variables</em>'.
	 * @see org.eclipse.fennec.model.oas.Server#getVariables()
	 * @see #getServer()
	 * @generated
	 */
	EReference getServer_Variables();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Server#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Server#getExtensions()
	 * @see #getServer()
	 * @generated
	 */
	EReference getServer_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.ServerVariable <em>Server Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Server Variable</em>'.
	 * @see org.eclipse.fennec.model.oas.ServerVariable
	 * @generated
	 */
	EClass getServerVariable();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.oas.ServerVariable#getEnum <em>Enum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Enum</em>'.
	 * @see org.eclipse.fennec.model.oas.ServerVariable#getEnum()
	 * @see #getServerVariable()
	 * @generated
	 */
	EAttribute getServerVariable_Enum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.ServerVariable#getDefault <em>Default</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default</em>'.
	 * @see org.eclipse.fennec.model.oas.ServerVariable#getDefault()
	 * @see #getServerVariable()
	 * @generated
	 */
	EAttribute getServerVariable_Default();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.ServerVariable#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.ServerVariable#getDescription()
	 * @see #getServerVariable()
	 * @generated
	 */
	EAttribute getServerVariable_Description();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.ServerVariable#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.ServerVariable#getExtensions()
	 * @see #getServerVariable()
	 * @generated
	 */
	EReference getServerVariable_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Components <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Components</em>'.
	 * @see org.eclipse.fennec.model.oas.Components
	 * @generated
	 */
	EClass getComponents();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getSchemas <em>Schemas</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Schemas</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getSchemas()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Schemas();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Components#getSchemasPackage <em>Schemas Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Schemas Package</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getSchemasPackage()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_SchemasPackage();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getResponses <em>Responses</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Responses</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getResponses()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Responses();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Parameters</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getParameters()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Parameters();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getExamples <em>Examples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Examples</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getExamples()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Examples();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getRequestBodies <em>Request Bodies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Request Bodies</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getRequestBodies()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_RequestBodies();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getHeaders <em>Headers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Headers</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getHeaders()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Headers();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getSecuritySchemes <em>Security Schemes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Security Schemes</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getSecuritySchemes()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_SecuritySchemes();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getLinks <em>Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Links</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getLinks()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Links();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getCallbacks <em>Callbacks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Callbacks</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getCallbacks()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Callbacks();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getPathItems <em>Path Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Path Items</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getPathItems()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_PathItems();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getMediaTypes <em>Media Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Media Types</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getMediaTypes()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_MediaTypes();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Components#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Components#getExtensions()
	 * @see #getComponents()
	 * @generated
	 */
	EReference getComponents_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.PathItem <em>Path Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Path Item</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem
	 * @generated
	 */
	EClass getPathItem();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.PathItem#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getRef()
	 * @see #getPathItem()
	 * @generated
	 */
	EAttribute getPathItem_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.PathItem#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getTarget()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Target();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.PathItem#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getSummary()
	 * @see #getPathItem()
	 * @generated
	 */
	EAttribute getPathItem_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.PathItem#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getDescription()
	 * @see #getPathItem()
	 * @generated
	 */
	EAttribute getPathItem_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.PathItem#getGet <em>Get</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Get</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getGet()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Get();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.PathItem#getPut <em>Put</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Put</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getPut()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Put();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.PathItem#getPost <em>Post</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Post</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getPost()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Post();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.PathItem#getDelete <em>Delete</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Delete</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getDelete()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Delete();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.PathItem#getOptions <em>Options</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Options</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getOptions()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Options();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.PathItem#getHead <em>Head</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Head</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getHead()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Head();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.PathItem#getPatch <em>Patch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Patch</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getPatch()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Patch();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.PathItem#getTrace <em>Trace</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Trace</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getTrace()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Trace();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.PathItem#getQuery <em>Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Query</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getQuery()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Query();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.PathItem#getAdditionalOperations <em>Additional Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Additional Operations</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getAdditionalOperations()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_AdditionalOperations();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.PathItem#getServers <em>Servers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Servers</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getServers()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Servers();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.PathItem#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getParameters()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Parameters();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.model.oas.PathItem#getAllOperations <em>All Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>All Operations</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getAllOperations()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_AllOperations();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.PathItem#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.PathItem#getExtensions()
	 * @see #getPathItem()
	 * @generated
	 */
	EReference getPathItem_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Operation <em>Operation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Operation</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation
	 * @generated
	 */
	EClass getOperation();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.model.oas.Operation#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Tags</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getTags()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Tags();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Operation#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getSummary()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Operation#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getDescription()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Operation#getExternalDocs <em>External Docs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>External Docs</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getExternalDocs()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_ExternalDocs();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Operation#getOperationId <em>Operation Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operation Id</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getOperationId()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_OperationId();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.Operation#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getParameters()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Parameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Operation#getRequestBody <em>Request Body</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Request Body</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getRequestBody()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_RequestBody();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Operation#getResponses <em>Responses</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Responses</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getResponses()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Responses();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Operation#getCallbacks <em>Callbacks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Callbacks</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getCallbacks()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Callbacks();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Operation#getDeprecated <em>Deprecated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deprecated</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getDeprecated()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Deprecated();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.Operation#getSecurity <em>Security</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Security</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getSecurity()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Security();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.Operation#getServers <em>Servers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Servers</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getServers()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Servers();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Operation#getMethod <em>Method</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getMethod()
	 * @see #getOperation()
	 * @generated
	 */
	EAttribute getOperation_Method();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Operation#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Operation#getExtensions()
	 * @see #getOperation()
	 * @generated
	 */
	EReference getOperation_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.ExternalDocumentation <em>External Documentation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>External Documentation</em>'.
	 * @see org.eclipse.fennec.model.oas.ExternalDocumentation
	 * @generated
	 */
	EClass getExternalDocumentation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.ExternalDocumentation#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.ExternalDocumentation#getDescription()
	 * @see #getExternalDocumentation()
	 * @generated
	 */
	EAttribute getExternalDocumentation_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.ExternalDocumentation#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.fennec.model.oas.ExternalDocumentation#getUrl()
	 * @see #getExternalDocumentation()
	 * @generated
	 */
	EAttribute getExternalDocumentation_Url();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.ExternalDocumentation#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.ExternalDocumentation#getExtensions()
	 * @see #getExternalDocumentation()
	 * @generated
	 */
	EReference getExternalDocumentation_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getRef()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.Parameter#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getTarget()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_Target();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getName()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getIn <em>In</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>In</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getIn()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_In();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getSummary()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getDescription()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getRequired()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Required();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getDeprecated <em>Deprecated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deprecated</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getDeprecated()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Deprecated();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getAllowEmptyValue <em>Allow Empty Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Empty Value</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getAllowEmptyValue()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_AllowEmptyValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getStyle <em>Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Style</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getStyle()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Style();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getExplode <em>Explode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Explode</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getExplode()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Explode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getAllowReserved <em>Allow Reserved</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Reserved</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getAllowReserved()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_AllowReserved();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Parameter#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Schema</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getSchema()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_Schema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Parameter#getExample <em>Example</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Example</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getExample()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Example();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Parameter#getExamples <em>Examples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Examples</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getExamples()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_Examples();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Parameter#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Content</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getContent()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_Content();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Parameter#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Parameter#getExtensions()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.RequestBody <em>Request Body</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Request Body</em>'.
	 * @see org.eclipse.fennec.model.oas.RequestBody
	 * @generated
	 */
	EClass getRequestBody();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.RequestBody#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.RequestBody#getRef()
	 * @see #getRequestBody()
	 * @generated
	 */
	EAttribute getRequestBody_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.RequestBody#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.RequestBody#getTarget()
	 * @see #getRequestBody()
	 * @generated
	 */
	EReference getRequestBody_Target();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.RequestBody#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.RequestBody#getSummary()
	 * @see #getRequestBody()
	 * @generated
	 */
	EAttribute getRequestBody_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.RequestBody#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.RequestBody#getDescription()
	 * @see #getRequestBody()
	 * @generated
	 */
	EAttribute getRequestBody_Description();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.RequestBody#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Content</em>'.
	 * @see org.eclipse.fennec.model.oas.RequestBody#getContent()
	 * @see #getRequestBody()
	 * @generated
	 */
	EReference getRequestBody_Content();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.RequestBody#getRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.fennec.model.oas.RequestBody#getRequired()
	 * @see #getRequestBody()
	 * @generated
	 */
	EAttribute getRequestBody_Required();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.RequestBody#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.RequestBody#getExtensions()
	 * @see #getRequestBody()
	 * @generated
	 */
	EReference getRequestBody_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.MediaType <em>Media Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Media Type</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType
	 * @generated
	 */
	EClass getMediaType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.MediaType#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getRef()
	 * @see #getMediaType()
	 * @generated
	 */
	EAttribute getMediaType_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.MediaType#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getTarget()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_Target();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.MediaType#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getSummary()
	 * @see #getMediaType()
	 * @generated
	 */
	EAttribute getMediaType_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.MediaType#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getDescription()
	 * @see #getMediaType()
	 * @generated
	 */
	EAttribute getMediaType_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.MediaType#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Schema</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getSchema()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_Schema();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.MediaType#getItemSchema <em>Item Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Item Schema</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getItemSchema()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_ItemSchema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.MediaType#getExample <em>Example</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Example</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getExample()
	 * @see #getMediaType()
	 * @generated
	 */
	EAttribute getMediaType_Example();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.MediaType#getExamples <em>Examples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Examples</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getExamples()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_Examples();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.MediaType#getEncoding <em>Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Encoding</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getEncoding()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_Encoding();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.MediaType#getPrefixEncoding <em>Prefix Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Prefix Encoding</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getPrefixEncoding()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_PrefixEncoding();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.MediaType#getItemEncoding <em>Item Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Item Encoding</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getItemEncoding()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_ItemEncoding();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.MediaType#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.MediaType#getExtensions()
	 * @see #getMediaType()
	 * @generated
	 */
	EReference getMediaType_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Encoding <em>Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Encoding</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding
	 * @generated
	 */
	EClass getEncoding();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Encoding#getContentType <em>Content Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Content Type</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding#getContentType()
	 * @see #getEncoding()
	 * @generated
	 */
	EAttribute getEncoding_ContentType();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Encoding#getHeaders <em>Headers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Headers</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding#getHeaders()
	 * @see #getEncoding()
	 * @generated
	 */
	EReference getEncoding_Headers();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Encoding#getStyle <em>Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Style</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding#getStyle()
	 * @see #getEncoding()
	 * @generated
	 */
	EAttribute getEncoding_Style();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Encoding#getExplode <em>Explode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Explode</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding#getExplode()
	 * @see #getEncoding()
	 * @generated
	 */
	EAttribute getEncoding_Explode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Encoding#getAllowReserved <em>Allow Reserved</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Reserved</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding#getAllowReserved()
	 * @see #getEncoding()
	 * @generated
	 */
	EAttribute getEncoding_AllowReserved();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Encoding#getEncoding <em>Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Encoding</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding#getEncoding()
	 * @see #getEncoding()
	 * @generated
	 */
	EReference getEncoding_Encoding();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.model.oas.Encoding#getPrefixEncoding <em>Prefix Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Prefix Encoding</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding#getPrefixEncoding()
	 * @see #getEncoding()
	 * @generated
	 */
	EReference getEncoding_PrefixEncoding();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Encoding#getItemEncoding <em>Item Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Item Encoding</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding#getItemEncoding()
	 * @see #getEncoding()
	 * @generated
	 */
	EReference getEncoding_ItemEncoding();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Encoding#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Encoding#getExtensions()
	 * @see #getEncoding()
	 * @generated
	 */
	EReference getEncoding_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Response <em>Response</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response</em>'.
	 * @see org.eclipse.fennec.model.oas.Response
	 * @generated
	 */
	EClass getResponse();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Response#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.Response#getRef()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.Response#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.Response#getTarget()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Target();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Response#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.Response#getSummary()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Response#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.Response#getDescription()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_Description();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Response#getHeaders <em>Headers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Headers</em>'.
	 * @see org.eclipse.fennec.model.oas.Response#getHeaders()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Headers();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Response#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Content</em>'.
	 * @see org.eclipse.fennec.model.oas.Response#getContent()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Content();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Response#getLinks <em>Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Links</em>'.
	 * @see org.eclipse.fennec.model.oas.Response#getLinks()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Links();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Response#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Response#getExtensions()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Callback <em>Callback</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Callback</em>'.
	 * @see org.eclipse.fennec.model.oas.Callback
	 * @generated
	 */
	EClass getCallback();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Callback#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.Callback#getRef()
	 * @see #getCallback()
	 * @generated
	 */
	EAttribute getCallback_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.Callback#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.Callback#getTarget()
	 * @see #getCallback()
	 * @generated
	 */
	EReference getCallback_Target();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Callback#getExpressions <em>Expressions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Expressions</em>'.
	 * @see org.eclipse.fennec.model.oas.Callback#getExpressions()
	 * @see #getCallback()
	 * @generated
	 */
	EReference getCallback_Expressions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Example <em>Example</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Example</em>'.
	 * @see org.eclipse.fennec.model.oas.Example
	 * @generated
	 */
	EClass getExample();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Example#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.Example#getRef()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.Example#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.Example#getTarget()
	 * @see #getExample()
	 * @generated
	 */
	EReference getExample_Target();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Example#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.Example#getSummary()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Example#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.Example#getDescription()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Example#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.model.oas.Example#getValue()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_Value();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Example#getDataValue <em>Data Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data Value</em>'.
	 * @see org.eclipse.fennec.model.oas.Example#getDataValue()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_DataValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Example#getSerializedValue <em>Serialized Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialized Value</em>'.
	 * @see org.eclipse.fennec.model.oas.Example#getSerializedValue()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_SerializedValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Example#getExternalValue <em>External Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>External Value</em>'.
	 * @see org.eclipse.fennec.model.oas.Example#getExternalValue()
	 * @see #getExample()
	 * @generated
	 */
	EAttribute getExample_ExternalValue();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Example#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Example#getExtensions()
	 * @see #getExample()
	 * @generated
	 */
	EReference getExample_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Link <em>Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Link</em>'.
	 * @see org.eclipse.fennec.model.oas.Link
	 * @generated
	 */
	EClass getLink();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Link#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getRef()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.Link#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getTarget()
	 * @see #getLink()
	 * @generated
	 */
	EReference getLink_Target();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Link#getOperationRef <em>Operation Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operation Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getOperationRef()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_OperationRef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Link#getOperationId <em>Operation Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operation Id</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getOperationId()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_OperationId();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Link#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Parameters</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getParameters()
	 * @see #getLink()
	 * @generated
	 */
	EReference getLink_Parameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Link#getRequestBody <em>Request Body</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Request Body</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getRequestBody()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_RequestBody();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Link#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getSummary()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Link#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getDescription()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Link#getServer <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Server</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getServer()
	 * @see #getLink()
	 * @generated
	 */
	EReference getLink_Server();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Link#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Link#getExtensions()
	 * @see #getLink()
	 * @generated
	 */
	EReference getLink_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Header <em>Header</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Header</em>'.
	 * @see org.eclipse.fennec.model.oas.Header
	 * @generated
	 */
	EClass getHeader();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getRef()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.Header#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getTarget()
	 * @see #getHeader()
	 * @generated
	 */
	EReference getHeader_Target();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getSummary()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getDescription()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getRequired()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Required();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getDeprecated <em>Deprecated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deprecated</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getDeprecated()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Deprecated();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getAllowEmptyValue <em>Allow Empty Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Empty Value</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getAllowEmptyValue()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_AllowEmptyValue();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getStyle <em>Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Style</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getStyle()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Style();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getExplode <em>Explode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Explode</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getExplode()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Explode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getAllowReserved <em>Allow Reserved</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow Reserved</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getAllowReserved()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_AllowReserved();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Header#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Schema</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getSchema()
	 * @see #getHeader()
	 * @generated
	 */
	EReference getHeader_Schema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Header#getExample <em>Example</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Example</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getExample()
	 * @see #getHeader()
	 * @generated
	 */
	EAttribute getHeader_Example();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Header#getExamples <em>Examples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Examples</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getExamples()
	 * @see #getHeader()
	 * @generated
	 */
	EReference getHeader_Examples();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Header#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Content</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getContent()
	 * @see #getHeader()
	 * @generated
	 */
	EReference getHeader_Content();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Header#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Header#getExtensions()
	 * @see #getHeader()
	 * @generated
	 */
	EReference getHeader_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.Tag <em>Tag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tag</em>'.
	 * @see org.eclipse.fennec.model.oas.Tag
	 * @generated
	 */
	EClass getTag();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Tag#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.oas.Tag#getName()
	 * @see #getTag()
	 * @generated
	 */
	EAttribute getTag_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Tag#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.Tag#getSummary()
	 * @see #getTag()
	 * @generated
	 */
	EAttribute getTag_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Tag#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.Tag#getDescription()
	 * @see #getTag()
	 * @generated
	 */
	EAttribute getTag_Description();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.Tag#getExternalDocs <em>External Docs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>External Docs</em>'.
	 * @see org.eclipse.fennec.model.oas.Tag#getExternalDocs()
	 * @see #getTag()
	 * @generated
	 */
	EReference getTag_ExternalDocs();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Tag#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parent</em>'.
	 * @see org.eclipse.fennec.model.oas.Tag#getParent()
	 * @see #getTag()
	 * @generated
	 */
	EAttribute getTag_Parent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.Tag#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.fennec.model.oas.Tag#getKind()
	 * @see #getTag()
	 * @generated
	 */
	EAttribute getTag_Kind();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.Tag#getParentTag <em>Parent Tag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent Tag</em>'.
	 * @see org.eclipse.fennec.model.oas.Tag#getParentTag()
	 * @see #getTag()
	 * @generated
	 */
	EReference getTag_ParentTag();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.Tag#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.Tag#getExtensions()
	 * @see #getTag()
	 * @generated
	 */
	EReference getTag_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.SecurityScheme <em>Security Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Security Scheme</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme
	 * @generated
	 */
	EClass getSecurityScheme();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getRef()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Ref();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.model.oas.SecurityScheme#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getTarget()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EReference getSecurityScheme_Target();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getType()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getSummary()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Summary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getDescription()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getName()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getIn <em>In</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>In</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getIn()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_In();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getScheme <em>Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scheme</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getScheme()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Scheme();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getBearerFormat <em>Bearer Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bearer Format</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getBearerFormat()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_BearerFormat();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.SecurityScheme#getFlows <em>Flows</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Flows</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getFlows()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EReference getSecurityScheme_Flows();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getOpenIdConnectUrl <em>Open Id Connect Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Open Id Connect Url</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getOpenIdConnectUrl()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_OpenIdConnectUrl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getOauth2MetadataUrl <em>Oauth2 Metadata Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Oauth2 Metadata Url</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getOauth2MetadataUrl()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Oauth2MetadataUrl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.SecurityScheme#getDeprecated <em>Deprecated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deprecated</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getDeprecated()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EAttribute getSecurityScheme_Deprecated();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.SecurityScheme#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityScheme#getExtensions()
	 * @see #getSecurityScheme()
	 * @generated
	 */
	EReference getSecurityScheme_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.OAuthFlows <em>OAuth Flows</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>OAuth Flows</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlows
	 * @generated
	 */
	EClass getOAuthFlows();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.OAuthFlows#getImplicit <em>Implicit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Implicit</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlows#getImplicit()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_Implicit();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.OAuthFlows#getPassword <em>Password</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Password</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlows#getPassword()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_Password();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.OAuthFlows#getClientCredentials <em>Client Credentials</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Client Credentials</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlows#getClientCredentials()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_ClientCredentials();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.OAuthFlows#getAuthorizationCode <em>Authorization Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Authorization Code</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlows#getAuthorizationCode()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_AuthorizationCode();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.model.oas.OAuthFlows#getDeviceAuthorization <em>Device Authorization</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Device Authorization</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlows#getDeviceAuthorization()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_DeviceAuthorization();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.OAuthFlows#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlows#getExtensions()
	 * @see #getOAuthFlows()
	 * @generated
	 */
	EReference getOAuthFlows_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.OAuthFlow <em>OAuth Flow</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>OAuth Flow</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlow
	 * @generated
	 */
	EClass getOAuthFlow();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.OAuthFlow#getAuthorizationUrl <em>Authorization Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Authorization Url</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlow#getAuthorizationUrl()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EAttribute getOAuthFlow_AuthorizationUrl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.OAuthFlow#getDeviceAuthorizationUrl <em>Device Authorization Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Device Authorization Url</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlow#getDeviceAuthorizationUrl()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EAttribute getOAuthFlow_DeviceAuthorizationUrl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.OAuthFlow#getTokenUrl <em>Token Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Token Url</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlow#getTokenUrl()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EAttribute getOAuthFlow_TokenUrl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.model.oas.OAuthFlow#getRefreshUrl <em>Refresh Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Refresh Url</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlow#getRefreshUrl()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EAttribute getOAuthFlow_RefreshUrl();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.OAuthFlow#getScopes <em>Scopes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Scopes</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlow#getScopes()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EReference getOAuthFlow_Scopes();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.OAuthFlow#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.model.oas.OAuthFlow#getExtensions()
	 * @see #getOAuthFlow()
	 * @generated
	 */
	EReference getOAuthFlow_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.model.oas.SecurityRequirement <em>Security Requirement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Security Requirement</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityRequirement
	 * @generated
	 */
	EClass getSecurityRequirement();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.model.oas.SecurityRequirement#getSchemes <em>Schemes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Schemes</em>'.
	 * @see org.eclipse.fennec.model.oas.SecurityRequirement#getSchemes()
	 * @see #getSecurityRequirement()
	 * @generated
	 */
	EReference getSecurityRequirement_Schemes();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Path Item Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Path Item Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.oas.PathItem" valueContainment="true"
	 * @generated
	 */
	EClass getPathItemEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getPathItemEntry()
	 * @generated
	 */
	EAttribute getPathItemEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getPathItemEntry()
	 * @generated
	 */
	EReference getPathItemEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Operation Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Operation Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.oas.Operation" valueContainment="true"
	 * @generated
	 */
	EClass getOperationEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getOperationEntry()
	 * @generated
	 */
	EAttribute getOperationEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getOperationEntry()
	 * @generated
	 */
	EReference getOperationEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Response Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.model.oas.Response" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.Parameter" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.Example" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.RequestBody" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.Header" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.SecurityScheme" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.Link" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.Callback" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.MediaType" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.Encoding" valueContainment="true"
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
	 *        valueType="org.eclipse.fennec.model.oas.ServerVariable" valueContainment="true"
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
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Any Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Any Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EJavaObject"
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
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getAnyEntry()
	 * @generated
	 */
	EAttribute getAnyEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Extension Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Extension Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EJavaObject"
	 * @generated
	 */
	EClass getExtensionEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getExtensionEntry()
	 * @generated
	 */
	EAttribute getExtensionEntry_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getExtensionEntry()
	 * @generated
	 */
	EAttribute getExtensionEntry_Value();

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
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Schema Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Schema Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.codec.jsonschema.model.Schema" valueContainment="true"
	 *        valueAnnotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
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
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.oas.ParameterLocation <em>Parameter Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Parameter Location</em>'.
	 * @see org.eclipse.fennec.model.oas.ParameterLocation
	 * @generated
	 */
	EEnum getParameterLocation();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.oas.ParameterStyle <em>Parameter Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Parameter Style</em>'.
	 * @see org.eclipse.fennec.model.oas.ParameterStyle
	 * @generated
	 */
	EEnum getParameterStyle();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.oas.SecuritySchemeType <em>Security Scheme Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Security Scheme Type</em>'.
	 * @see org.eclipse.fennec.model.oas.SecuritySchemeType
	 * @generated
	 */
	EEnum getSecuritySchemeType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.model.oas.ApiKeyLocation <em>Api Key Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Api Key Location</em>'.
	 * @see org.eclipse.fennec.model.oas.ApiKeyLocation
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
	OasFactory getOasFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.OpenAPIImpl <em>Open API</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.OpenAPIImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOpenAPI()
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
		 * The meta object literal for the '<em><b>Self</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPEN_API__SELF = eINSTANCE.getOpenAPI_Self();

		/**
		 * The meta object literal for the '<em><b>Info</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__INFO = eINSTANCE.getOpenAPI_Info();

		/**
		 * The meta object literal for the '<em><b>Json Schema Dialect</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPEN_API__JSON_SCHEMA_DIALECT = eINSTANCE.getOpenAPI_JsonSchemaDialect();

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
		 * The meta object literal for the '<em><b>Webhooks</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__WEBHOOKS = eINSTANCE.getOpenAPI_Webhooks();

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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPEN_API__EXTENSIONS = eINSTANCE.getOpenAPI_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.InfoImpl <em>Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.InfoImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getInfo()
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
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INFO__SUMMARY = eINSTANCE.getInfo_Summary();

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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INFO__EXTENSIONS = eINSTANCE.getInfo_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ContactImpl <em>Contact</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ContactImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getContact()
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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTACT__EXTENSIONS = eINSTANCE.getContact_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.LicenseImpl <em>License</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.LicenseImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getLicense()
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
		 * The meta object literal for the '<em><b>Identifier</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LICENSE__IDENTIFIER = eINSTANCE.getLicense_Identifier();

		/**
		 * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LICENSE__URL = eINSTANCE.getLicense_Url();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LICENSE__EXTENSIONS = eINSTANCE.getLicense_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ServerImpl <em>Server</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ServerImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getServer()
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
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER__NAME = eINSTANCE.getServer_Name();

		/**
		 * The meta object literal for the '<em><b>Variables</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER__VARIABLES = eINSTANCE.getServer_Variables();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER__EXTENSIONS = eINSTANCE.getServer_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ServerVariableImpl <em>Server Variable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ServerVariableImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getServerVariable()
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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_VARIABLE__EXTENSIONS = eINSTANCE.getServerVariable_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl <em>Components</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ComponentsImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getComponents()
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
		 * The meta object literal for the '<em><b>Path Items</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__PATH_ITEMS = eINSTANCE.getComponents_PathItems();

		/**
		 * The meta object literal for the '<em><b>Media Types</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__MEDIA_TYPES = eINSTANCE.getComponents_MediaTypes();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS__EXTENSIONS = eINSTANCE.getComponents_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.PathItemImpl <em>Path Item</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.PathItemImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getPathItem()
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
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__TARGET = eINSTANCE.getPathItem_Target();

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
		 * The meta object literal for the '<em><b>Query</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__QUERY = eINSTANCE.getPathItem_Query();

		/**
		 * The meta object literal for the '<em><b>Additional Operations</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__ADDITIONAL_OPERATIONS = eINSTANCE.getPathItem_AdditionalOperations();

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
		 * The meta object literal for the '<em><b>All Operations</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__ALL_OPERATIONS = eINSTANCE.getPathItem_AllOperations();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM__EXTENSIONS = eINSTANCE.getPathItem_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.OperationImpl <em>Operation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.OperationImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOperation()
		 * @generated
		 */
		EClass OPERATION = eINSTANCE.getOperation();

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
		 * The meta object literal for the '<em><b>Method</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION__METHOD = eINSTANCE.getOperation_Method();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION__EXTENSIONS = eINSTANCE.getOperation_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ExternalDocumentationImpl <em>External Documentation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ExternalDocumentationImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getExternalDocumentation()
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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EXTERNAL_DOCUMENTATION__EXTENSIONS = eINSTANCE.getExternalDocumentation_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ParameterImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getParameter()
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
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER__TARGET = eINSTANCE.getParameter_Target();

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
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__SUMMARY = eINSTANCE.getParameter_Summary();

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
		 * The meta object literal for the '<em><b>Example</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__EXAMPLE = eINSTANCE.getParameter_Example();

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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER__EXTENSIONS = eINSTANCE.getParameter_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.RequestBodyImpl <em>Request Body</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.RequestBodyImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getRequestBody()
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
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUEST_BODY__TARGET = eINSTANCE.getRequestBody_Target();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REQUEST_BODY__SUMMARY = eINSTANCE.getRequestBody_Summary();

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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUEST_BODY__EXTENSIONS = eINSTANCE.getRequestBody_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl <em>Media Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.MediaTypeImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getMediaType()
		 * @generated
		 */
		EClass MEDIA_TYPE = eINSTANCE.getMediaType();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEDIA_TYPE__REF = eINSTANCE.getMediaType_Ref();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE__TARGET = eINSTANCE.getMediaType_Target();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEDIA_TYPE__SUMMARY = eINSTANCE.getMediaType_Summary();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEDIA_TYPE__DESCRIPTION = eINSTANCE.getMediaType_Description();

		/**
		 * The meta object literal for the '<em><b>Schema</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE__SCHEMA = eINSTANCE.getMediaType_Schema();

		/**
		 * The meta object literal for the '<em><b>Item Schema</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE__ITEM_SCHEMA = eINSTANCE.getMediaType_ItemSchema();

		/**
		 * The meta object literal for the '<em><b>Example</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MEDIA_TYPE__EXAMPLE = eINSTANCE.getMediaType_Example();

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
		 * The meta object literal for the '<em><b>Prefix Encoding</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE__PREFIX_ENCODING = eINSTANCE.getMediaType_PrefixEncoding();

		/**
		 * The meta object literal for the '<em><b>Item Encoding</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE__ITEM_ENCODING = eINSTANCE.getMediaType_ItemEncoding();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MEDIA_TYPE__EXTENSIONS = eINSTANCE.getMediaType_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.EncodingImpl <em>Encoding</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.EncodingImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getEncoding()
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
		 * The meta object literal for the '<em><b>Encoding</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENCODING__ENCODING = eINSTANCE.getEncoding_Encoding();

		/**
		 * The meta object literal for the '<em><b>Prefix Encoding</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENCODING__PREFIX_ENCODING = eINSTANCE.getEncoding_PrefixEncoding();

		/**
		 * The meta object literal for the '<em><b>Item Encoding</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENCODING__ITEM_ENCODING = eINSTANCE.getEncoding_ItemEncoding();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENCODING__EXTENSIONS = eINSTANCE.getEncoding_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ResponseImpl <em>Response</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ResponseImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getResponse()
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
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE__TARGET = eINSTANCE.getResponse_Target();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESPONSE__SUMMARY = eINSTANCE.getResponse_Summary();

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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE__EXTENSIONS = eINSTANCE.getResponse_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.CallbackImpl <em>Callback</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.CallbackImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getCallback()
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
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CALLBACK__TARGET = eINSTANCE.getCallback_Target();

		/**
		 * The meta object literal for the '<em><b>Expressions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CALLBACK__EXPRESSIONS = eINSTANCE.getCallback_Expressions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ExampleImpl <em>Example</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ExampleImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getExample()
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
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EXAMPLE__TARGET = eINSTANCE.getExample_Target();

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
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXAMPLE__VALUE = eINSTANCE.getExample_Value();

		/**
		 * The meta object literal for the '<em><b>Data Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXAMPLE__DATA_VALUE = eINSTANCE.getExample_DataValue();

		/**
		 * The meta object literal for the '<em><b>Serialized Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXAMPLE__SERIALIZED_VALUE = eINSTANCE.getExample_SerializedValue();

		/**
		 * The meta object literal for the '<em><b>External Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXAMPLE__EXTERNAL_VALUE = eINSTANCE.getExample_ExternalValue();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EXAMPLE__EXTENSIONS = eINSTANCE.getExample_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.LinkImpl <em>Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.LinkImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getLink()
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
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINK__TARGET = eINSTANCE.getLink_Target();

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
		 * The meta object literal for the '<em><b>Request Body</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK__REQUEST_BODY = eINSTANCE.getLink_RequestBody();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK__SUMMARY = eINSTANCE.getLink_Summary();

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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINK__EXTENSIONS = eINSTANCE.getLink_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.HeaderImpl <em>Header</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.HeaderImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getHeader()
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
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HEADER__TARGET = eINSTANCE.getHeader_Target();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__SUMMARY = eINSTANCE.getHeader_Summary();

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
		 * The meta object literal for the '<em><b>Example</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEADER__EXAMPLE = eINSTANCE.getHeader_Example();

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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HEADER__EXTENSIONS = eINSTANCE.getHeader_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.TagImpl <em>Tag</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.TagImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getTag()
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
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAG__SUMMARY = eINSTANCE.getTag_Summary();

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
		 * The meta object literal for the '<em><b>Parent</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAG__PARENT = eINSTANCE.getTag_Parent();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAG__KIND = eINSTANCE.getTag_Kind();

		/**
		 * The meta object literal for the '<em><b>Parent Tag</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TAG__PARENT_TAG = eINSTANCE.getTag_ParentTag();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TAG__EXTENSIONS = eINSTANCE.getTag_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl <em>Security Scheme</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecurityScheme()
		 * @generated
		 */
		EClass SECURITY_SCHEME = eINSTANCE.getSecurityScheme();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__REF = eINSTANCE.getSecurityScheme_Ref();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SECURITY_SCHEME__TARGET = eINSTANCE.getSecurityScheme_Target();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__TYPE = eINSTANCE.getSecurityScheme_Type();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__SUMMARY = eINSTANCE.getSecurityScheme_Summary();

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
		 * The meta object literal for the '<em><b>Oauth2 Metadata Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__OAUTH2_METADATA_URL = eINSTANCE.getSecurityScheme_Oauth2MetadataUrl();

		/**
		 * The meta object literal for the '<em><b>Deprecated</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SECURITY_SCHEME__DEPRECATED = eINSTANCE.getSecurityScheme_Deprecated();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SECURITY_SCHEME__EXTENSIONS = eINSTANCE.getSecurityScheme_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl <em>OAuth Flows</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOAuthFlows()
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
		 * The meta object literal for the '<em><b>Device Authorization</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OAUTH_FLOWS__DEVICE_AUTHORIZATION = eINSTANCE.getOAuthFlows_DeviceAuthorization();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OAUTH_FLOWS__EXTENSIONS = eINSTANCE.getOAuthFlows_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.OAuthFlowImpl <em>OAuth Flow</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.OAuthFlowImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOAuthFlow()
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
		 * The meta object literal for the '<em><b>Device Authorization Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OAUTH_FLOW__DEVICE_AUTHORIZATION_URL = eINSTANCE.getOAuthFlow_DeviceAuthorizationUrl();

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
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OAUTH_FLOW__EXTENSIONS = eINSTANCE.getOAuthFlow_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.SecurityRequirementImpl <em>Security Requirement</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.SecurityRequirementImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecurityRequirement()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.PathItemEntryImpl <em>Path Item Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.PathItemEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getPathItemEntry()
		 * @generated
		 */
		EClass PATH_ITEM_ENTRY = eINSTANCE.getPathItemEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PATH_ITEM_ENTRY__KEY = eINSTANCE.getPathItemEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PATH_ITEM_ENTRY__VALUE = eINSTANCE.getPathItemEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.OperationEntryImpl <em>Operation Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.OperationEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getOperationEntry()
		 * @generated
		 */
		EClass OPERATION_ENTRY = eINSTANCE.getOperationEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OPERATION_ENTRY__KEY = eINSTANCE.getOperationEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OPERATION_ENTRY__VALUE = eINSTANCE.getOperationEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ResponseEntryImpl <em>Response Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ResponseEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getResponseEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ParameterEntryImpl <em>Parameter Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ParameterEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getParameterEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ExampleEntryImpl <em>Example Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ExampleEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getExampleEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.RequestBodyEntryImpl <em>Request Body Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.RequestBodyEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getRequestBodyEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.HeaderEntryImpl <em>Header Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.HeaderEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getHeaderEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeEntryImpl <em>Security Scheme Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.SecuritySchemeEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecuritySchemeEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.LinkEntryImpl <em>Link Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.LinkEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getLinkEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.CallbackEntryImpl <em>Callback Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.CallbackEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getCallbackEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.MediaTypeEntryImpl <em>Media Type Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.MediaTypeEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getMediaTypeEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.EncodingEntryImpl <em>Encoding Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.EncodingEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getEncodingEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ServerVariableEntryImpl <em>Server Variable Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ServerVariableEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getServerVariableEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.StringEntryImpl <em>String Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.StringEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getStringEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.AnyEntryImpl <em>Any Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.AnyEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getAnyEntry()
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
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANY_ENTRY__VALUE = eINSTANCE.getAnyEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.ExtensionEntryImpl <em>Extension Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.ExtensionEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getExtensionEntry()
		 * @generated
		 */
		EClass EXTENSION_ENTRY = eINSTANCE.getExtensionEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXTENSION_ENTRY__KEY = eINSTANCE.getExtensionEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXTENSION_ENTRY__VALUE = eINSTANCE.getExtensionEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.SecurityRequirementEntryImpl <em>Security Requirement Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.SecurityRequirementEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecurityRequirementEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.impl.SchemaEntryImpl <em>Schema Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.impl.SchemaEntryImpl
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSchemaEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.ParameterLocation <em>Parameter Location</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.ParameterLocation
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getParameterLocation()
		 * @generated
		 */
		EEnum PARAMETER_LOCATION = eINSTANCE.getParameterLocation();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.ParameterStyle <em>Parameter Style</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.ParameterStyle
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getParameterStyle()
		 * @generated
		 */
		EEnum PARAMETER_STYLE = eINSTANCE.getParameterStyle();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.SecuritySchemeType <em>Security Scheme Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.SecuritySchemeType
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getSecuritySchemeType()
		 * @generated
		 */
		EEnum SECURITY_SCHEME_TYPE = eINSTANCE.getSecuritySchemeType();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.model.oas.ApiKeyLocation <em>Api Key Location</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.model.oas.ApiKeyLocation
		 * @see org.eclipse.fennec.model.oas.impl.OasPackageImpl#getApiKeyLocation()
		 * @generated
		 */
		EEnum API_KEY_LOCATION = eINSTANCE.getApiKeyLocation();

	}

} //OasPackage
