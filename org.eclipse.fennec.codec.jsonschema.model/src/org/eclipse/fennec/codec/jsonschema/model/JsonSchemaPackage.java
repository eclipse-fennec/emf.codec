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
package org.eclipse.fennec.codec.jsonschema.model;


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
 * JSON Schema draft-04 to 2020-12 and the OpenAPI Schema Object, as written (issue #316). Features carry the draft they appear in (annotation http://eclipse.org/fennec/jsonschema/model: since, until, dialect).
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaFactory
 * @model kind="package"
 * @generated
 */
@ProviderType
@EPackage(uri = JsonSchemaPackage.eNS_URI, fingerprint = "fp1:4547513d24a554bf6ede7c59cd9f92661b1b8a953b9cd2743c1f8341430fe928", genModel = "/model/jsonschema.genmodel", genModelSourceLocations = {"model/jsonschema.genmodel","org.eclipse.fennec.codec.jsonschema.model/model/jsonschema.genmodel"}, ecore = "/model/jsonschema.ecore", ecoreSourceLocations = "/model/jsonschema.ecore")
public interface JsonSchemaPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "model";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/jsonschema";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "jsonschema";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	JsonSchemaPackage eINSTANCE = org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl <em>Schema</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getSchema()
	 * @generated
	 */
	int SCHEMA = 0;

	/**
	 * The feature id for the '<em><b>Boolean Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__BOOLEAN_SCHEMA = 0;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__SCHEMA = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ID = 2;

	/**
	 * The feature id for the '<em><b>Legacy Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__LEGACY_ID = 3;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__REF = 4;

	/**
	 * The feature id for the '<em><b>Anchor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ANCHOR = 5;

	/**
	 * The feature id for the '<em><b>Dynamic Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DYNAMIC_REF = 6;

	/**
	 * The feature id for the '<em><b>Dynamic Anchor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DYNAMIC_ANCHOR = 7;

	/**
	 * The feature id for the '<em><b>Recursive Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__RECURSIVE_REF = 8;

	/**
	 * The feature id for the '<em><b>Recursive Anchor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__RECURSIVE_ANCHOR = 9;

	/**
	 * The feature id for the '<em><b>Vocabulary</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__VOCABULARY = 10;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__COMMENT = 11;

	/**
	 * The feature id for the '<em><b>Defs</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DEFS = 12;

	/**
	 * The feature id for the '<em><b>Definitions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DEFINITIONS = 13;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__TITLE = 14;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DESCRIPTION = 15;

	/**
	 * The feature id for the '<em><b>Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DEFAULT = 16;

	/**
	 * The feature id for the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DEPRECATED = 17;

	/**
	 * The feature id for the '<em><b>Read Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__READ_ONLY = 18;

	/**
	 * The feature id for the '<em><b>Write Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__WRITE_ONLY = 19;

	/**
	 * The feature id for the '<em><b>Examples</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXAMPLES = 20;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__TYPE = 21;

	/**
	 * The feature id for the '<em><b>Type As List</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__TYPE_AS_LIST = 22;

	/**
	 * The feature id for the '<em><b>Enum</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ENUM = 23;

	/**
	 * The feature id for the '<em><b>Const</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__CONST = 24;

	/**
	 * The feature id for the '<em><b>Multiple Of</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MULTIPLE_OF = 25;

	/**
	 * The feature id for the '<em><b>Maximum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MAXIMUM = 26;

	/**
	 * The feature id for the '<em><b>Exclusive Maximum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXCLUSIVE_MAXIMUM = 27;

	/**
	 * The feature id for the '<em><b>Exclusive Maximum Flag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXCLUSIVE_MAXIMUM_FLAG = 28;

	/**
	 * The feature id for the '<em><b>Minimum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MINIMUM = 29;

	/**
	 * The feature id for the '<em><b>Exclusive Minimum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXCLUSIVE_MINIMUM = 30;

	/**
	 * The feature id for the '<em><b>Exclusive Minimum Flag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXCLUSIVE_MINIMUM_FLAG = 31;

	/**
	 * The feature id for the '<em><b>Max Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MAX_LENGTH = 32;

	/**
	 * The feature id for the '<em><b>Min Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MIN_LENGTH = 33;

	/**
	 * The feature id for the '<em><b>Pattern</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__PATTERN = 34;

	/**
	 * The feature id for the '<em><b>Max Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MAX_ITEMS = 35;

	/**
	 * The feature id for the '<em><b>Min Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MIN_ITEMS = 36;

	/**
	 * The feature id for the '<em><b>Unique Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__UNIQUE_ITEMS = 37;

	/**
	 * The feature id for the '<em><b>Max Contains</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MAX_CONTAINS = 38;

	/**
	 * The feature id for the '<em><b>Min Contains</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MIN_CONTAINS = 39;

	/**
	 * The feature id for the '<em><b>Max Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MAX_PROPERTIES = 40;

	/**
	 * The feature id for the '<em><b>Min Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__MIN_PROPERTIES = 41;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__REQUIRED = 42;

	/**
	 * The feature id for the '<em><b>Dependent Required</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DEPENDENT_REQUIRED = 43;

	/**
	 * The feature id for the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__FORMAT = 44;

	/**
	 * The feature id for the '<em><b>All Of</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ALL_OF = 45;

	/**
	 * The feature id for the '<em><b>Any Of</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ANY_OF = 46;

	/**
	 * The feature id for the '<em><b>One Of</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ONE_OF = 47;

	/**
	 * The feature id for the '<em><b>Not</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__NOT = 48;

	/**
	 * The feature id for the '<em><b>If</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__IF = 49;

	/**
	 * The feature id for the '<em><b>Then</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__THEN = 50;

	/**
	 * The feature id for the '<em><b>Else</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ELSE = 51;

	/**
	 * The feature id for the '<em><b>Dependent Schemas</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DEPENDENT_SCHEMAS = 52;

	/**
	 * The feature id for the '<em><b>Dependencies</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DEPENDENCIES = 53;

	/**
	 * The feature id for the '<em><b>Prefix Items</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__PREFIX_ITEMS = 54;

	/**
	 * The feature id for the '<em><b>Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ITEMS = 55;

	/**
	 * The feature id for the '<em><b>Items List</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ITEMS_LIST = 56;

	/**
	 * The feature id for the '<em><b>Additional Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ADDITIONAL_ITEMS = 57;

	/**
	 * The feature id for the '<em><b>Contains</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__CONTAINS = 58;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__PROPERTIES = 59;

	/**
	 * The feature id for the '<em><b>Pattern Properties</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__PATTERN_PROPERTIES = 60;

	/**
	 * The feature id for the '<em><b>Additional Properties</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__ADDITIONAL_PROPERTIES = 61;

	/**
	 * The feature id for the '<em><b>Property Names</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__PROPERTY_NAMES = 62;

	/**
	 * The feature id for the '<em><b>Unevaluated Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__UNEVALUATED_ITEMS = 63;

	/**
	 * The feature id for the '<em><b>Unevaluated Properties</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__UNEVALUATED_PROPERTIES = 64;

	/**
	 * The feature id for the '<em><b>Content Encoding</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__CONTENT_ENCODING = 65;

	/**
	 * The feature id for the '<em><b>Content Media Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__CONTENT_MEDIA_TYPE = 66;

	/**
	 * The feature id for the '<em><b>Content Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__CONTENT_SCHEMA = 67;

	/**
	 * The feature id for the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__NULLABLE = 68;

	/**
	 * The feature id for the '<em><b>Discriminator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__DISCRIMINATOR = 69;

	/**
	 * The feature id for the '<em><b>Xml</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__XML = 70;

	/**
	 * The feature id for the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXTERNAL_DOCS = 71;

	/**
	 * The feature id for the '<em><b>Example</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__EXAMPLE = 72;

	/**
	 * The feature id for the '<em><b>Keywords</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__KEYWORDS = 73;

	/**
	 * The feature id for the '<em><b>Classifier</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA__CLASSIFIER = 74;

	/**
	 * The number of structural features of the '<em>Schema</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA_FEATURE_COUNT = 75;

	/**
	 * The number of operations of the '<em>Schema</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCHEMA_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.DiscriminatorImpl <em>Discriminator</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.DiscriminatorImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getDiscriminator()
	 * @generated
	 */
	int DISCRIMINATOR = 1;

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
	 * The feature id for the '<em><b>Default Mapping</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR__DEFAULT_MAPPING = 2;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR__EXTENSIONS = 3;

	/**
	 * The number of structural features of the '<em>Discriminator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Discriminator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISCRIMINATOR_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl <em>Xml</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getXml()
	 * @generated
	 */
	int XML = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XML__NAME = 0;

	/**
	 * The feature id for the '<em><b>Namespace</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XML__NAMESPACE = 1;

	/**
	 * The feature id for the '<em><b>Prefix</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XML__PREFIX = 2;

	/**
	 * The feature id for the '<em><b>Attribute</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XML__ATTRIBUTE = 3;

	/**
	 * The feature id for the '<em><b>Wrapped</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XML__WRAPPED = 4;

	/**
	 * The feature id for the '<em><b>Node Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XML__NODE_TYPE = 5;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XML__EXTENSIONS = 6;

	/**
	 * The number of structural features of the '<em>Xml</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XML_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Xml</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XML_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.ExternalDocumentationImpl <em>External Documentation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.ExternalDocumentationImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getExternalDocumentation()
	 * @generated
	 */
	int EXTERNAL_DOCUMENTATION = 3;

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
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.DependencyImpl <em>Dependency</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.DependencyImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getDependency()
	 * @generated
	 */
	int DEPENDENCY = 4;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY__SCHEMA = 0;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY__PROPERTIES = 1;

	/**
	 * The number of structural features of the '<em>Dependency</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Dependency</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaEntryImpl <em>Schema Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.SchemaEntryImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getSchemaEntry()
	 * @generated
	 */
	int SCHEMA_ENTRY = 5;

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
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.DependencyEntryImpl <em>Dependency Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.DependencyEntryImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getDependencyEntry()
	 * @generated
	 */
	int DEPENDENCY_ENTRY = 6;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Dependency Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Dependency Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.StringListEntryImpl <em>String List Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.StringListEntryImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getStringListEntry()
	 * @generated
	 */
	int STRING_LIST_ENTRY = 7;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_LIST_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_LIST_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>String List Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_LIST_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>String List Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_LIST_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.StringEntryImpl <em>String Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.StringEntryImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getStringEntry()
	 * @generated
	 */
	int STRING_ENTRY = 8;

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
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.VocabularyEntryImpl <em>Vocabulary Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.VocabularyEntryImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getVocabularyEntry()
	 * @generated
	 */
	int VOCABULARY_ENTRY = 9;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VOCABULARY_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VOCABULARY_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Vocabulary Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VOCABULARY_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Vocabulary Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VOCABULARY_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.KeywordEntryImpl <em>Keyword Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.KeywordEntryImpl
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getKeywordEntry()
	 * @generated
	 */
	int KEYWORD_ENTRY = 10;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KEYWORD_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KEYWORD_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Keyword Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KEYWORD_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Keyword Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KEYWORD_ENTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.jsonschema.model.SimpleType <em>Simple Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.jsonschema.model.SimpleType
	 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getSimpleType()
	 * @generated
	 */
	int SIMPLE_TYPE = 11;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.jsonschema.model.Schema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Schema</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema
	 * @generated
	 */
	EClass getSchema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getBooleanSchema <em>Boolean Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Boolean Schema</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getBooleanSchema()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_BooleanSchema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getSchema()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Schema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getId()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getLegacyId <em>Legacy Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Legacy Id</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getLegacyId()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_LegacyId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getRef()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAnchor <em>Anchor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Anchor</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getAnchor()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Anchor();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDynamicRef <em>Dynamic Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dynamic Ref</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDynamicRef()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_DynamicRef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDynamicAnchor <em>Dynamic Anchor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dynamic Anchor</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDynamicAnchor()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_DynamicAnchor();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveRef <em>Recursive Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Recursive Ref</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveRef()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_RecursiveRef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveAnchor <em>Recursive Anchor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Recursive Anchor</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveAnchor()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_RecursiveAnchor();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getVocabulary <em>Vocabulary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Vocabulary</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getVocabulary()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Vocabulary();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getComment <em>Comment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Comment</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getComment()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Comment();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefs <em>Defs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Defs</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDefs()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Defs();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefinitions <em>Definitions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Definitions</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDefinitions()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Definitions();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getTitle()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Title();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDescription()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefault <em>Default</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDefault()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Default();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDeprecated <em>Deprecated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deprecated</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDeprecated()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Deprecated();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getReadOnly <em>Read Only</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Read Only</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getReadOnly()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ReadOnly();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getWriteOnly <em>Write Only</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Write Only</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getWriteOnly()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_WriteOnly();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExamples <em>Examples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Examples</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getExamples()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Examples();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Type</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getType()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#isTypeAsList <em>Type As List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type As List</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#isTypeAsList()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_TypeAsList();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getEnum <em>Enum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Enum</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getEnum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Enum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getConst <em>Const</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Const</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getConst()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Const();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMultipleOf <em>Multiple Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Multiple Of</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMultipleOf()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MultipleOf();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaximum <em>Maximum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Maximum</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMaximum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Maximum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximum <em>Exclusive Maximum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusive Maximum</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ExclusiveMaximum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximumFlag <em>Exclusive Maximum Flag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusive Maximum Flag</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximumFlag()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ExclusiveMaximumFlag();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinimum <em>Minimum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Minimum</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMinimum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Minimum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimum <em>Exclusive Minimum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusive Minimum</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimum()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ExclusiveMinimum();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimumFlag <em>Exclusive Minimum Flag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclusive Minimum Flag</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimumFlag()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ExclusiveMinimumFlag();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxLength <em>Max Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Length</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxLength()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MaxLength();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinLength <em>Min Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Length</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMinLength()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MinLength();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPattern <em>Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pattern</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getPattern()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Pattern();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxItems <em>Max Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Items</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxItems()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MaxItems();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinItems <em>Min Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Items</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMinItems()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MinItems();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUniqueItems <em>Unique Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unique Items</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getUniqueItems()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_UniqueItems();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxContains <em>Max Contains</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Contains</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxContains()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MaxContains();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinContains <em>Min Contains</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Contains</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMinContains()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MinContains();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxProperties <em>Max Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Properties</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MaxProperties();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinProperties <em>Min Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Properties</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getMinProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_MinProperties();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Required</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getRequired()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Required();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentRequired <em>Dependent Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Dependent Required</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentRequired()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_DependentRequired();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getFormat <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Format</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getFormat()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Format();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAllOf <em>All Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>All Of</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getAllOf()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_AllOf();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAnyOf <em>Any Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Any Of</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getAnyOf()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_AnyOf();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getOneOf <em>One Of</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>One Of</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getOneOf()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_OneOf();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getNot <em>Not</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Not</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getNot()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Not();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getIf <em>If</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>If</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getIf()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_If();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getThen <em>Then</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Then</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getThen()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Then();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getElse <em>Else</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Else</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getElse()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Else();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentSchemas <em>Dependent Schemas</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Dependent Schemas</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentSchemas()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_DependentSchemas();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependencies <em>Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Dependencies</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDependencies()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Dependencies();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPrefixItems <em>Prefix Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Prefix Items</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getPrefixItems()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_PrefixItems();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getItems <em>Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Items</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getItems()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Items();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getItemsList <em>Items List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Items List</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getItemsList()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_ItemsList();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAdditionalItems <em>Additional Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Additional Items</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getAdditionalItems()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_AdditionalItems();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContains <em>Contains</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Contains</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getContains()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Contains();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Properties</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Properties();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPatternProperties <em>Pattern Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Pattern Properties</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getPatternProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_PatternProperties();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAdditionalProperties <em>Additional Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Additional Properties</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getAdditionalProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_AdditionalProperties();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPropertyNames <em>Property Names</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Property Names</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getPropertyNames()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_PropertyNames();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUnevaluatedItems <em>Unevaluated Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Unevaluated Items</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getUnevaluatedItems()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_UnevaluatedItems();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUnevaluatedProperties <em>Unevaluated Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Unevaluated Properties</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getUnevaluatedProperties()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_UnevaluatedProperties();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContentEncoding <em>Content Encoding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Content Encoding</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getContentEncoding()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ContentEncoding();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContentMediaType <em>Content Media Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Content Media Type</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getContentMediaType()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_ContentMediaType();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContentSchema <em>Content Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Content Schema</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getContentSchema()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_ContentSchema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getNullable <em>Nullable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nullable</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getNullable()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Nullable();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDiscriminator <em>Discriminator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Discriminator</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getDiscriminator()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Discriminator();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getXml <em>Xml</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Xml</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getXml()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Xml();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExternalDocs <em>External Docs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>External Docs</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getExternalDocs()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_ExternalDocs();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExample <em>Example</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Example</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getExample()
	 * @see #getSchema()
	 * @generated
	 */
	EAttribute getSchema_Example();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getKeywords <em>Keywords</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Keywords</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getKeywords()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Keywords();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getClassifier <em>Classifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Classifier</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema#getClassifier()
	 * @see #getSchema()
	 * @generated
	 */
	EReference getSchema_Classifier();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator <em>Discriminator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Discriminator</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Discriminator
	 * @generated
	 */
	EClass getDiscriminator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getPropertyName <em>Property Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Property Name</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Discriminator#getPropertyName()
	 * @see #getDiscriminator()
	 * @generated
	 */
	EAttribute getDiscriminator_PropertyName();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getMapping <em>Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Mapping</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Discriminator#getMapping()
	 * @see #getDiscriminator()
	 * @generated
	 */
	EReference getDiscriminator_Mapping();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getDefaultMapping <em>Default Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default Mapping</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Discriminator#getDefaultMapping()
	 * @see #getDiscriminator()
	 * @generated
	 */
	EAttribute getDiscriminator_DefaultMapping();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Discriminator#getExtensions()
	 * @see #getDiscriminator()
	 * @generated
	 */
	EReference getDiscriminator_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.jsonschema.model.Xml <em>Xml</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Xml</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Xml
	 * @generated
	 */
	EClass getXml();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Xml#getName()
	 * @see #getXml()
	 * @generated
	 */
	EAttribute getXml_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getNamespace <em>Namespace</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Namespace</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Xml#getNamespace()
	 * @see #getXml()
	 * @generated
	 */
	EAttribute getXml_Namespace();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getPrefix <em>Prefix</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Prefix</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Xml#getPrefix()
	 * @see #getXml()
	 * @generated
	 */
	EAttribute getXml_Prefix();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getAttribute <em>Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Attribute</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Xml#getAttribute()
	 * @see #getXml()
	 * @generated
	 */
	EAttribute getXml_Attribute();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getWrapped <em>Wrapped</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wrapped</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Xml#getWrapped()
	 * @see #getXml()
	 * @generated
	 */
	EAttribute getXml_Wrapped();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getNodeType <em>Node Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Node Type</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Xml#getNodeType()
	 * @see #getXml()
	 * @generated
	 */
	EAttribute getXml_NodeType();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Xml#getExtensions()
	 * @see #getXml()
	 * @generated
	 */
	EReference getXml_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation <em>External Documentation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>External Documentation</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation
	 * @generated
	 */
	EClass getExternalDocumentation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getDescription()
	 * @see #getExternalDocumentation()
	 * @generated
	 */
	EAttribute getExternalDocumentation_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getUrl()
	 * @see #getExternalDocumentation()
	 * @generated
	 */
	EAttribute getExternalDocumentation_Url();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extensions</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getExtensions()
	 * @see #getExternalDocumentation()
	 * @generated
	 */
	EReference getExternalDocumentation_Extensions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.jsonschema.model.Dependency <em>Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Dependency</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Dependency
	 * @generated
	 */
	EClass getDependency();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.codec.jsonschema.model.Dependency#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Schema</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Dependency#getSchema()
	 * @see #getDependency()
	 * @generated
	 */
	EReference getDependency_Schema();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.fennec.codec.jsonschema.model.Dependency#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Properties</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Dependency#getProperties()
	 * @see #getDependency()
	 * @generated
	 */
	EAttribute getDependency_Properties();

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
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Dependency Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Dependency Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueType="org.eclipse.fennec.codec.jsonschema.model.Dependency" valueContainment="true"
	 * @generated
	 */
	EClass getDependencyEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getDependencyEntry()
	 * @generated
	 */
	EAttribute getDependencyEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getDependencyEntry()
	 * @generated
	 */
	EReference getDependencyEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String List Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String List Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EString" valueMany="true"
	 * @generated
	 */
	EClass getStringListEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringListEntry()
	 * @generated
	 */
	EAttribute getStringListEntry_Key();

	/**
	 * Returns the meta object for the attribute list '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringListEntry()
	 * @generated
	 */
	EAttribute getStringListEntry_Value();

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
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Vocabulary Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Vocabulary Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EBooleanObject"
	 * @generated
	 */
	EClass getVocabularyEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getVocabularyEntry()
	 * @generated
	 */
	EAttribute getVocabularyEntry_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getVocabularyEntry()
	 * @generated
	 */
	EAttribute getVocabularyEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Keyword Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Keyword Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EJavaObject"
	 * @generated
	 */
	EClass getKeywordEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getKeywordEntry()
	 * @generated
	 */
	EAttribute getKeywordEntry_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getKeywordEntry()
	 * @generated
	 */
	EAttribute getKeywordEntry_Value();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.jsonschema.model.SimpleType <em>Simple Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Simple Type</em>'.
	 * @see org.eclipse.fennec.codec.jsonschema.model.SimpleType
	 * @generated
	 */
	EEnum getSimpleType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	JsonSchemaFactory getJsonSchemaFactory();

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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl <em>Schema</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getSchema()
		 * @generated
		 */
		EClass SCHEMA = eINSTANCE.getSchema();

		/**
		 * The meta object literal for the '<em><b>Boolean Schema</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__BOOLEAN_SCHEMA = eINSTANCE.getSchema_BooleanSchema();

		/**
		 * The meta object literal for the '<em><b>Schema</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__SCHEMA = eINSTANCE.getSchema_Schema();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__ID = eINSTANCE.getSchema_Id();

		/**
		 * The meta object literal for the '<em><b>Legacy Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__LEGACY_ID = eINSTANCE.getSchema_LegacyId();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__REF = eINSTANCE.getSchema_Ref();

		/**
		 * The meta object literal for the '<em><b>Anchor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__ANCHOR = eINSTANCE.getSchema_Anchor();

		/**
		 * The meta object literal for the '<em><b>Dynamic Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__DYNAMIC_REF = eINSTANCE.getSchema_DynamicRef();

		/**
		 * The meta object literal for the '<em><b>Dynamic Anchor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__DYNAMIC_ANCHOR = eINSTANCE.getSchema_DynamicAnchor();

		/**
		 * The meta object literal for the '<em><b>Recursive Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__RECURSIVE_REF = eINSTANCE.getSchema_RecursiveRef();

		/**
		 * The meta object literal for the '<em><b>Recursive Anchor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__RECURSIVE_ANCHOR = eINSTANCE.getSchema_RecursiveAnchor();

		/**
		 * The meta object literal for the '<em><b>Vocabulary</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__VOCABULARY = eINSTANCE.getSchema_Vocabulary();

		/**
		 * The meta object literal for the '<em><b>Comment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__COMMENT = eINSTANCE.getSchema_Comment();

		/**
		 * The meta object literal for the '<em><b>Defs</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__DEFS = eINSTANCE.getSchema_Defs();

		/**
		 * The meta object literal for the '<em><b>Definitions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__DEFINITIONS = eINSTANCE.getSchema_Definitions();

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
		 * The meta object literal for the '<em><b>Default</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__DEFAULT = eINSTANCE.getSchema_Default();

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
		 * The meta object literal for the '<em><b>Examples</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__EXAMPLES = eINSTANCE.getSchema_Examples();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__TYPE = eINSTANCE.getSchema_Type();

		/**
		 * The meta object literal for the '<em><b>Type As List</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__TYPE_AS_LIST = eINSTANCE.getSchema_TypeAsList();

		/**
		 * The meta object literal for the '<em><b>Enum</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__ENUM = eINSTANCE.getSchema_Enum();

		/**
		 * The meta object literal for the '<em><b>Const</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__CONST = eINSTANCE.getSchema_Const();

		/**
		 * The meta object literal for the '<em><b>Multiple Of</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MULTIPLE_OF = eINSTANCE.getSchema_MultipleOf();

		/**
		 * The meta object literal for the '<em><b>Maximum</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MAXIMUM = eINSTANCE.getSchema_Maximum();

		/**
		 * The meta object literal for the '<em><b>Exclusive Maximum</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__EXCLUSIVE_MAXIMUM = eINSTANCE.getSchema_ExclusiveMaximum();

		/**
		 * The meta object literal for the '<em><b>Exclusive Maximum Flag</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__EXCLUSIVE_MAXIMUM_FLAG = eINSTANCE.getSchema_ExclusiveMaximumFlag();

		/**
		 * The meta object literal for the '<em><b>Minimum</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MINIMUM = eINSTANCE.getSchema_Minimum();

		/**
		 * The meta object literal for the '<em><b>Exclusive Minimum</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__EXCLUSIVE_MINIMUM = eINSTANCE.getSchema_ExclusiveMinimum();

		/**
		 * The meta object literal for the '<em><b>Exclusive Minimum Flag</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__EXCLUSIVE_MINIMUM_FLAG = eINSTANCE.getSchema_ExclusiveMinimumFlag();

		/**
		 * The meta object literal for the '<em><b>Max Length</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MAX_LENGTH = eINSTANCE.getSchema_MaxLength();

		/**
		 * The meta object literal for the '<em><b>Min Length</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MIN_LENGTH = eINSTANCE.getSchema_MinLength();

		/**
		 * The meta object literal for the '<em><b>Pattern</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__PATTERN = eINSTANCE.getSchema_Pattern();

		/**
		 * The meta object literal for the '<em><b>Max Items</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MAX_ITEMS = eINSTANCE.getSchema_MaxItems();

		/**
		 * The meta object literal for the '<em><b>Min Items</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MIN_ITEMS = eINSTANCE.getSchema_MinItems();

		/**
		 * The meta object literal for the '<em><b>Unique Items</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__UNIQUE_ITEMS = eINSTANCE.getSchema_UniqueItems();

		/**
		 * The meta object literal for the '<em><b>Max Contains</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MAX_CONTAINS = eINSTANCE.getSchema_MaxContains();

		/**
		 * The meta object literal for the '<em><b>Min Contains</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MIN_CONTAINS = eINSTANCE.getSchema_MinContains();

		/**
		 * The meta object literal for the '<em><b>Max Properties</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MAX_PROPERTIES = eINSTANCE.getSchema_MaxProperties();

		/**
		 * The meta object literal for the '<em><b>Min Properties</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__MIN_PROPERTIES = eINSTANCE.getSchema_MinProperties();

		/**
		 * The meta object literal for the '<em><b>Required</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__REQUIRED = eINSTANCE.getSchema_Required();

		/**
		 * The meta object literal for the '<em><b>Dependent Required</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__DEPENDENT_REQUIRED = eINSTANCE.getSchema_DependentRequired();

		/**
		 * The meta object literal for the '<em><b>Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__FORMAT = eINSTANCE.getSchema_Format();

		/**
		 * The meta object literal for the '<em><b>All Of</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ALL_OF = eINSTANCE.getSchema_AllOf();

		/**
		 * The meta object literal for the '<em><b>Any Of</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ANY_OF = eINSTANCE.getSchema_AnyOf();

		/**
		 * The meta object literal for the '<em><b>One Of</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ONE_OF = eINSTANCE.getSchema_OneOf();

		/**
		 * The meta object literal for the '<em><b>Not</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__NOT = eINSTANCE.getSchema_Not();

		/**
		 * The meta object literal for the '<em><b>If</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__IF = eINSTANCE.getSchema_If();

		/**
		 * The meta object literal for the '<em><b>Then</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__THEN = eINSTANCE.getSchema_Then();

		/**
		 * The meta object literal for the '<em><b>Else</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ELSE = eINSTANCE.getSchema_Else();

		/**
		 * The meta object literal for the '<em><b>Dependent Schemas</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__DEPENDENT_SCHEMAS = eINSTANCE.getSchema_DependentSchemas();

		/**
		 * The meta object literal for the '<em><b>Dependencies</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__DEPENDENCIES = eINSTANCE.getSchema_Dependencies();

		/**
		 * The meta object literal for the '<em><b>Prefix Items</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__PREFIX_ITEMS = eINSTANCE.getSchema_PrefixItems();

		/**
		 * The meta object literal for the '<em><b>Items</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ITEMS = eINSTANCE.getSchema_Items();

		/**
		 * The meta object literal for the '<em><b>Items List</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ITEMS_LIST = eINSTANCE.getSchema_ItemsList();

		/**
		 * The meta object literal for the '<em><b>Additional Items</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ADDITIONAL_ITEMS = eINSTANCE.getSchema_AdditionalItems();

		/**
		 * The meta object literal for the '<em><b>Contains</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__CONTAINS = eINSTANCE.getSchema_Contains();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__PROPERTIES = eINSTANCE.getSchema_Properties();

		/**
		 * The meta object literal for the '<em><b>Pattern Properties</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__PATTERN_PROPERTIES = eINSTANCE.getSchema_PatternProperties();

		/**
		 * The meta object literal for the '<em><b>Additional Properties</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__ADDITIONAL_PROPERTIES = eINSTANCE.getSchema_AdditionalProperties();

		/**
		 * The meta object literal for the '<em><b>Property Names</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__PROPERTY_NAMES = eINSTANCE.getSchema_PropertyNames();

		/**
		 * The meta object literal for the '<em><b>Unevaluated Items</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__UNEVALUATED_ITEMS = eINSTANCE.getSchema_UnevaluatedItems();

		/**
		 * The meta object literal for the '<em><b>Unevaluated Properties</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__UNEVALUATED_PROPERTIES = eINSTANCE.getSchema_UnevaluatedProperties();

		/**
		 * The meta object literal for the '<em><b>Content Encoding</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__CONTENT_ENCODING = eINSTANCE.getSchema_ContentEncoding();

		/**
		 * The meta object literal for the '<em><b>Content Media Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__CONTENT_MEDIA_TYPE = eINSTANCE.getSchema_ContentMediaType();

		/**
		 * The meta object literal for the '<em><b>Content Schema</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__CONTENT_SCHEMA = eINSTANCE.getSchema_ContentSchema();

		/**
		 * The meta object literal for the '<em><b>Nullable</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__NULLABLE = eINSTANCE.getSchema_Nullable();

		/**
		 * The meta object literal for the '<em><b>Discriminator</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__DISCRIMINATOR = eINSTANCE.getSchema_Discriminator();

		/**
		 * The meta object literal for the '<em><b>Xml</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__XML = eINSTANCE.getSchema_Xml();

		/**
		 * The meta object literal for the '<em><b>External Docs</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__EXTERNAL_DOCS = eINSTANCE.getSchema_ExternalDocs();

		/**
		 * The meta object literal for the '<em><b>Example</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCHEMA__EXAMPLE = eINSTANCE.getSchema_Example();

		/**
		 * The meta object literal for the '<em><b>Keywords</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__KEYWORDS = eINSTANCE.getSchema_Keywords();

		/**
		 * The meta object literal for the '<em><b>Classifier</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCHEMA__CLASSIFIER = eINSTANCE.getSchema_Classifier();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.DiscriminatorImpl <em>Discriminator</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.DiscriminatorImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getDiscriminator()
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
		 * The meta object literal for the '<em><b>Default Mapping</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DISCRIMINATOR__DEFAULT_MAPPING = eINSTANCE.getDiscriminator_DefaultMapping();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DISCRIMINATOR__EXTENSIONS = eINSTANCE.getDiscriminator_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl <em>Xml</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getXml()
		 * @generated
		 */
		EClass XML = eINSTANCE.getXml();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XML__NAME = eINSTANCE.getXml_Name();

		/**
		 * The meta object literal for the '<em><b>Namespace</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XML__NAMESPACE = eINSTANCE.getXml_Namespace();

		/**
		 * The meta object literal for the '<em><b>Prefix</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XML__PREFIX = eINSTANCE.getXml_Prefix();

		/**
		 * The meta object literal for the '<em><b>Attribute</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XML__ATTRIBUTE = eINSTANCE.getXml_Attribute();

		/**
		 * The meta object literal for the '<em><b>Wrapped</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XML__WRAPPED = eINSTANCE.getXml_Wrapped();

		/**
		 * The meta object literal for the '<em><b>Node Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XML__NODE_TYPE = eINSTANCE.getXml_NodeType();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference XML__EXTENSIONS = eINSTANCE.getXml_Extensions();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.ExternalDocumentationImpl <em>External Documentation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.ExternalDocumentationImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getExternalDocumentation()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.DependencyImpl <em>Dependency</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.DependencyImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getDependency()
		 * @generated
		 */
		EClass DEPENDENCY = eINSTANCE.getDependency();

		/**
		 * The meta object literal for the '<em><b>Schema</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPENDENCY__SCHEMA = eINSTANCE.getDependency_Schema();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPENDENCY__PROPERTIES = eINSTANCE.getDependency_Properties();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaEntryImpl <em>Schema Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.SchemaEntryImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getSchemaEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.DependencyEntryImpl <em>Dependency Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.DependencyEntryImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getDependencyEntry()
		 * @generated
		 */
		EClass DEPENDENCY_ENTRY = eINSTANCE.getDependencyEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPENDENCY_ENTRY__KEY = eINSTANCE.getDependencyEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPENDENCY_ENTRY__VALUE = eINSTANCE.getDependencyEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.StringListEntryImpl <em>String List Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.StringListEntryImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getStringListEntry()
		 * @generated
		 */
		EClass STRING_LIST_ENTRY = eINSTANCE.getStringListEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_LIST_ENTRY__KEY = eINSTANCE.getStringListEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_LIST_ENTRY__VALUE = eINSTANCE.getStringListEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.StringEntryImpl <em>String Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.StringEntryImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getStringEntry()
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
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.VocabularyEntryImpl <em>Vocabulary Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.VocabularyEntryImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getVocabularyEntry()
		 * @generated
		 */
		EClass VOCABULARY_ENTRY = eINSTANCE.getVocabularyEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VOCABULARY_ENTRY__KEY = eINSTANCE.getVocabularyEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VOCABULARY_ENTRY__VALUE = eINSTANCE.getVocabularyEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.impl.KeywordEntryImpl <em>Keyword Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.KeywordEntryImpl
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getKeywordEntry()
		 * @generated
		 */
		EClass KEYWORD_ENTRY = eINSTANCE.getKeywordEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute KEYWORD_ENTRY__KEY = eINSTANCE.getKeywordEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute KEYWORD_ENTRY__VALUE = eINSTANCE.getKeywordEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.jsonschema.model.SimpleType <em>Simple Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.jsonschema.model.SimpleType
		 * @see org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl#getSimpleType()
		 * @generated
		 */
		EEnum SIMPLE_TYPE = eINSTANCE.getSimpleType();

	}

} //JsonSchemaPackage
