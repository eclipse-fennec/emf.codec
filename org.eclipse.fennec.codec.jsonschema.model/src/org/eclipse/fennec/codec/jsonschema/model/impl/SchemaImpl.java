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
package org.eclipse.fennec.codec.jsonschema.model.impl;

import java.math.BigDecimal;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.jsonschema.model.Dependency;
import org.eclipse.fennec.codec.jsonschema.model.Discriminator;
import org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;
import org.eclipse.fennec.codec.jsonschema.model.Xml;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Schema</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getBooleanSchema <em>Boolean Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getLegacyId <em>Legacy Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getAnchor <em>Anchor</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDynamicRef <em>Dynamic Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDynamicAnchor <em>Dynamic Anchor</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getRecursiveRef <em>Recursive Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getRecursiveAnchor <em>Recursive Anchor</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getVocabulary <em>Vocabulary</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getComment <em>Comment</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDefs <em>Defs</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDefinitions <em>Definitions</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDefault <em>Default</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getReadOnly <em>Read Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getWriteOnly <em>Write Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#isTypeAsList <em>Type As List</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getEnum <em>Enum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getConst <em>Const</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMultipleOf <em>Multiple Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMaximum <em>Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getExclusiveMaximum <em>Exclusive Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getExclusiveMaximumFlag <em>Exclusive Maximum Flag</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMinimum <em>Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getExclusiveMinimum <em>Exclusive Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getExclusiveMinimumFlag <em>Exclusive Minimum Flag</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMaxLength <em>Max Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMinLength <em>Min Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getPattern <em>Pattern</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMaxItems <em>Max Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMinItems <em>Min Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getUniqueItems <em>Unique Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMaxContains <em>Max Contains</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMinContains <em>Min Contains</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMaxProperties <em>Max Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getMinProperties <em>Min Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDependentRequired <em>Dependent Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getAllOf <em>All Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getAnyOf <em>Any Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getOneOf <em>One Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getNot <em>Not</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getIf <em>If</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getThen <em>Then</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getElse <em>Else</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDependentSchemas <em>Dependent Schemas</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDependencies <em>Dependencies</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getPrefixItems <em>Prefix Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getItems <em>Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getItemsList <em>Items List</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getAdditionalItems <em>Additional Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getContains <em>Contains</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getPatternProperties <em>Pattern Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getAdditionalProperties <em>Additional Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getPropertyNames <em>Property Names</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getUnevaluatedItems <em>Unevaluated Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getUnevaluatedProperties <em>Unevaluated Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getContentEncoding <em>Content Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getContentMediaType <em>Content Media Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getContentSchema <em>Content Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getNullable <em>Nullable</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getDiscriminator <em>Discriminator</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getXml <em>Xml</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getExternalDocs <em>External Docs</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getKeywords <em>Keywords</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.SchemaImpl#getClassifier <em>Classifier</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SchemaImpl extends MinimalEObjectImpl.Container implements Schema {
	/**
	 * The default value of the '{@link #getBooleanSchema() <em>Boolean Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBooleanSchema()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean BOOLEAN_SCHEMA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBooleanSchema() <em>Boolean Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBooleanSchema()
	 * @generated
	 * @ordered
	 */
	protected Boolean booleanSchema = BOOLEAN_SCHEMA_EDEFAULT;

	/**
	 * This is true if the Boolean Schema attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean booleanSchemaESet;

	/**
	 * The default value of the '{@link #getSchema() <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected static final String SCHEMA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSchema() <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected String schema = SCHEMA_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getLegacyId() <em>Legacy Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLegacyId()
	 * @generated
	 * @ordered
	 */
	protected static final String LEGACY_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLegacyId() <em>Legacy Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLegacyId()
	 * @generated
	 * @ordered
	 */
	protected String legacyId = LEGACY_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getRef() <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRef()
	 * @generated
	 * @ordered
	 */
	protected static final String REF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRef() <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRef()
	 * @generated
	 * @ordered
	 */
	protected String ref = REF_EDEFAULT;

	/**
	 * The default value of the '{@link #getAnchor() <em>Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAnchor()
	 * @generated
	 * @ordered
	 */
	protected static final String ANCHOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAnchor() <em>Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAnchor()
	 * @generated
	 * @ordered
	 */
	protected String anchor = ANCHOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getDynamicRef() <em>Dynamic Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDynamicRef()
	 * @generated
	 * @ordered
	 */
	protected static final String DYNAMIC_REF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDynamicRef() <em>Dynamic Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDynamicRef()
	 * @generated
	 * @ordered
	 */
	protected String dynamicRef = DYNAMIC_REF_EDEFAULT;

	/**
	 * The default value of the '{@link #getDynamicAnchor() <em>Dynamic Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDynamicAnchor()
	 * @generated
	 * @ordered
	 */
	protected static final String DYNAMIC_ANCHOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDynamicAnchor() <em>Dynamic Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDynamicAnchor()
	 * @generated
	 * @ordered
	 */
	protected String dynamicAnchor = DYNAMIC_ANCHOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getRecursiveRef() <em>Recursive Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecursiveRef()
	 * @generated
	 * @ordered
	 */
	protected static final String RECURSIVE_REF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRecursiveRef() <em>Recursive Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecursiveRef()
	 * @generated
	 * @ordered
	 */
	protected String recursiveRef = RECURSIVE_REF_EDEFAULT;

	/**
	 * The default value of the '{@link #getRecursiveAnchor() <em>Recursive Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecursiveAnchor()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean RECURSIVE_ANCHOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRecursiveAnchor() <em>Recursive Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecursiveAnchor()
	 * @generated
	 * @ordered
	 */
	protected Boolean recursiveAnchor = RECURSIVE_ANCHOR_EDEFAULT;

	/**
	 * This is true if the Recursive Anchor attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean recursiveAnchorESet;

	/**
	 * The cached value of the '{@link #getVocabulary() <em>Vocabulary</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVocabulary()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Boolean> vocabulary;

	/**
	 * The default value of the '{@link #getComment() <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected static final String COMMENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getComment() <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected String comment = COMMENT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getDefs() <em>Defs</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefs()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Schema> defs;

	/**
	 * The cached value of the '{@link #getDefinitions() <em>Definitions</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefinitions()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Schema> definitions;

	/**
	 * The default value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected static final String TITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected String title = TITLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDefault() <em>Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefault()
	 * @generated
	 * @ordered
	 */
	protected static final Object DEFAULT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDefault() <em>Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefault()
	 * @generated
	 * @ordered
	 */
	protected Object default_ = DEFAULT_EDEFAULT;

	/**
	 * This is true if the Default attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean defaultESet;

	/**
	 * The default value of the '{@link #getDeprecated() <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeprecated()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean DEPRECATED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeprecated() <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeprecated()
	 * @generated
	 * @ordered
	 */
	protected Boolean deprecated = DEPRECATED_EDEFAULT;

	/**
	 * This is true if the Deprecated attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean deprecatedESet;

	/**
	 * The default value of the '{@link #getReadOnly() <em>Read Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReadOnly()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean READ_ONLY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getReadOnly() <em>Read Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReadOnly()
	 * @generated
	 * @ordered
	 */
	protected Boolean readOnly = READ_ONLY_EDEFAULT;

	/**
	 * This is true if the Read Only attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean readOnlyESet;

	/**
	 * The default value of the '{@link #getWriteOnly() <em>Write Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWriteOnly()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean WRITE_ONLY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWriteOnly() <em>Write Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWriteOnly()
	 * @generated
	 * @ordered
	 */
	protected Boolean writeOnly = WRITE_ONLY_EDEFAULT;

	/**
	 * This is true if the Write Only attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean writeOnlyESet;

	/**
	 * The cached value of the '{@link #getExamples() <em>Examples</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExamples()
	 * @generated
	 * @ordered
	 */
	protected EList<Object> examples;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected EList<SimpleType> type;

	/**
	 * The default value of the '{@link #isTypeAsList() <em>Type As List</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isTypeAsList()
	 * @generated
	 * @ordered
	 */
	protected static final boolean TYPE_AS_LIST_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isTypeAsList() <em>Type As List</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isTypeAsList()
	 * @generated
	 * @ordered
	 */
	protected boolean typeAsList = TYPE_AS_LIST_EDEFAULT;

	/**
	 * The cached value of the '{@link #getEnum() <em>Enum</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEnum()
	 * @generated
	 * @ordered
	 */
	protected EList<Object> enum_;

	/**
	 * The default value of the '{@link #getConst() <em>Const</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConst()
	 * @generated
	 * @ordered
	 */
	protected static final Object CONST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConst() <em>Const</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConst()
	 * @generated
	 * @ordered
	 */
	protected Object const_ = CONST_EDEFAULT;

	/**
	 * This is true if the Const attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean constESet;

	/**
	 * The default value of the '{@link #getMultipleOf() <em>Multiple Of</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMultipleOf()
	 * @generated
	 * @ordered
	 */
	protected static final BigDecimal MULTIPLE_OF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMultipleOf() <em>Multiple Of</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMultipleOf()
	 * @generated
	 * @ordered
	 */
	protected BigDecimal multipleOf = MULTIPLE_OF_EDEFAULT;

	/**
	 * This is true if the Multiple Of attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean multipleOfESet;

	/**
	 * The default value of the '{@link #getMaximum() <em>Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaximum()
	 * @generated
	 * @ordered
	 */
	protected static final BigDecimal MAXIMUM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMaximum() <em>Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaximum()
	 * @generated
	 * @ordered
	 */
	protected BigDecimal maximum = MAXIMUM_EDEFAULT;

	/**
	 * This is true if the Maximum attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean maximumESet;

	/**
	 * The default value of the '{@link #getExclusiveMaximum() <em>Exclusive Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusiveMaximum()
	 * @generated
	 * @ordered
	 */
	protected static final BigDecimal EXCLUSIVE_MAXIMUM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExclusiveMaximum() <em>Exclusive Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusiveMaximum()
	 * @generated
	 * @ordered
	 */
	protected BigDecimal exclusiveMaximum = EXCLUSIVE_MAXIMUM_EDEFAULT;

	/**
	 * This is true if the Exclusive Maximum attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean exclusiveMaximumESet;

	/**
	 * The default value of the '{@link #getExclusiveMaximumFlag() <em>Exclusive Maximum Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusiveMaximumFlag()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean EXCLUSIVE_MAXIMUM_FLAG_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExclusiveMaximumFlag() <em>Exclusive Maximum Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusiveMaximumFlag()
	 * @generated
	 * @ordered
	 */
	protected Boolean exclusiveMaximumFlag = EXCLUSIVE_MAXIMUM_FLAG_EDEFAULT;

	/**
	 * This is true if the Exclusive Maximum Flag attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean exclusiveMaximumFlagESet;

	/**
	 * The default value of the '{@link #getMinimum() <em>Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinimum()
	 * @generated
	 * @ordered
	 */
	protected static final BigDecimal MINIMUM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMinimum() <em>Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinimum()
	 * @generated
	 * @ordered
	 */
	protected BigDecimal minimum = MINIMUM_EDEFAULT;

	/**
	 * This is true if the Minimum attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean minimumESet;

	/**
	 * The default value of the '{@link #getExclusiveMinimum() <em>Exclusive Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusiveMinimum()
	 * @generated
	 * @ordered
	 */
	protected static final BigDecimal EXCLUSIVE_MINIMUM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExclusiveMinimum() <em>Exclusive Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusiveMinimum()
	 * @generated
	 * @ordered
	 */
	protected BigDecimal exclusiveMinimum = EXCLUSIVE_MINIMUM_EDEFAULT;

	/**
	 * This is true if the Exclusive Minimum attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean exclusiveMinimumESet;

	/**
	 * The default value of the '{@link #getExclusiveMinimumFlag() <em>Exclusive Minimum Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusiveMinimumFlag()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean EXCLUSIVE_MINIMUM_FLAG_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExclusiveMinimumFlag() <em>Exclusive Minimum Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusiveMinimumFlag()
	 * @generated
	 * @ordered
	 */
	protected Boolean exclusiveMinimumFlag = EXCLUSIVE_MINIMUM_FLAG_EDEFAULT;

	/**
	 * This is true if the Exclusive Minimum Flag attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean exclusiveMinimumFlagESet;

	/**
	 * The default value of the '{@link #getMaxLength() <em>Max Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxLength()
	 * @generated
	 * @ordered
	 */
	protected static final Integer MAX_LENGTH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMaxLength() <em>Max Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxLength()
	 * @generated
	 * @ordered
	 */
	protected Integer maxLength = MAX_LENGTH_EDEFAULT;

	/**
	 * This is true if the Max Length attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean maxLengthESet;

	/**
	 * The default value of the '{@link #getMinLength() <em>Min Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinLength()
	 * @generated
	 * @ordered
	 */
	protected static final Integer MIN_LENGTH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMinLength() <em>Min Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinLength()
	 * @generated
	 * @ordered
	 */
	protected Integer minLength = MIN_LENGTH_EDEFAULT;

	/**
	 * This is true if the Min Length attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean minLengthESet;

	/**
	 * The default value of the '{@link #getPattern() <em>Pattern</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPattern()
	 * @generated
	 * @ordered
	 */
	protected static final String PATTERN_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPattern() <em>Pattern</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPattern()
	 * @generated
	 * @ordered
	 */
	protected String pattern = PATTERN_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaxItems() <em>Max Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxItems()
	 * @generated
	 * @ordered
	 */
	protected static final Integer MAX_ITEMS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMaxItems() <em>Max Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxItems()
	 * @generated
	 * @ordered
	 */
	protected Integer maxItems = MAX_ITEMS_EDEFAULT;

	/**
	 * This is true if the Max Items attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean maxItemsESet;

	/**
	 * The default value of the '{@link #getMinItems() <em>Min Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinItems()
	 * @generated
	 * @ordered
	 */
	protected static final Integer MIN_ITEMS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMinItems() <em>Min Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinItems()
	 * @generated
	 * @ordered
	 */
	protected Integer minItems = MIN_ITEMS_EDEFAULT;

	/**
	 * This is true if the Min Items attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean minItemsESet;

	/**
	 * The default value of the '{@link #getUniqueItems() <em>Unique Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUniqueItems()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean UNIQUE_ITEMS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUniqueItems() <em>Unique Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUniqueItems()
	 * @generated
	 * @ordered
	 */
	protected Boolean uniqueItems = UNIQUE_ITEMS_EDEFAULT;

	/**
	 * This is true if the Unique Items attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean uniqueItemsESet;

	/**
	 * The default value of the '{@link #getMaxContains() <em>Max Contains</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxContains()
	 * @generated
	 * @ordered
	 */
	protected static final Integer MAX_CONTAINS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMaxContains() <em>Max Contains</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxContains()
	 * @generated
	 * @ordered
	 */
	protected Integer maxContains = MAX_CONTAINS_EDEFAULT;

	/**
	 * This is true if the Max Contains attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean maxContainsESet;

	/**
	 * The default value of the '{@link #getMinContains() <em>Min Contains</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinContains()
	 * @generated
	 * @ordered
	 */
	protected static final Integer MIN_CONTAINS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMinContains() <em>Min Contains</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinContains()
	 * @generated
	 * @ordered
	 */
	protected Integer minContains = MIN_CONTAINS_EDEFAULT;

	/**
	 * This is true if the Min Contains attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean minContainsESet;

	/**
	 * The default value of the '{@link #getMaxProperties() <em>Max Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxProperties()
	 * @generated
	 * @ordered
	 */
	protected static final Integer MAX_PROPERTIES_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMaxProperties() <em>Max Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxProperties()
	 * @generated
	 * @ordered
	 */
	protected Integer maxProperties = MAX_PROPERTIES_EDEFAULT;

	/**
	 * This is true if the Max Properties attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean maxPropertiesESet;

	/**
	 * The default value of the '{@link #getMinProperties() <em>Min Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinProperties()
	 * @generated
	 * @ordered
	 */
	protected static final Integer MIN_PROPERTIES_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMinProperties() <em>Min Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinProperties()
	 * @generated
	 * @ordered
	 */
	protected Integer minProperties = MIN_PROPERTIES_EDEFAULT;

	/**
	 * This is true if the Min Properties attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean minPropertiesESet;

	/**
	 * The cached value of the '{@link #getRequired() <em>Required</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequired()
	 * @generated
	 * @ordered
	 */
	protected EList<String> required;

	/**
	 * The cached value of the '{@link #getDependentRequired() <em>Dependent Required</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDependentRequired()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, EList<String>> dependentRequired;

	/**
	 * The default value of the '{@link #getFormat() <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormat()
	 * @generated
	 * @ordered
	 */
	protected static final String FORMAT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFormat() <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormat()
	 * @generated
	 * @ordered
	 */
	protected String format = FORMAT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAllOf() <em>All Of</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAllOf()
	 * @generated
	 * @ordered
	 */
	protected EList<Schema> allOf;

	/**
	 * The cached value of the '{@link #getAnyOf() <em>Any Of</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAnyOf()
	 * @generated
	 * @ordered
	 */
	protected EList<Schema> anyOf;

	/**
	 * The cached value of the '{@link #getOneOf() <em>One Of</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOneOf()
	 * @generated
	 * @ordered
	 */
	protected EList<Schema> oneOf;

	/**
	 * The cached value of the '{@link #getNot() <em>Not</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNot()
	 * @generated
	 * @ordered
	 */
	protected Schema not;

	/**
	 * The cached value of the '{@link #getIf() <em>If</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIf()
	 * @generated
	 * @ordered
	 */
	protected Schema if_;

	/**
	 * The cached value of the '{@link #getThen() <em>Then</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getThen()
	 * @generated
	 * @ordered
	 */
	protected Schema then;

	/**
	 * The cached value of the '{@link #getElse() <em>Else</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getElse()
	 * @generated
	 * @ordered
	 */
	protected Schema else_;

	/**
	 * The cached value of the '{@link #getDependentSchemas() <em>Dependent Schemas</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDependentSchemas()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Schema> dependentSchemas;

	/**
	 * The cached value of the '{@link #getDependencies() <em>Dependencies</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDependencies()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Dependency> dependencies;

	/**
	 * The cached value of the '{@link #getPrefixItems() <em>Prefix Items</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrefixItems()
	 * @generated
	 * @ordered
	 */
	protected EList<Schema> prefixItems;

	/**
	 * The cached value of the '{@link #getItems() <em>Items</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItems()
	 * @generated
	 * @ordered
	 */
	protected Schema items;

	/**
	 * The cached value of the '{@link #getItemsList() <em>Items List</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItemsList()
	 * @generated
	 * @ordered
	 */
	protected EList<Schema> itemsList;

	/**
	 * The cached value of the '{@link #getAdditionalItems() <em>Additional Items</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAdditionalItems()
	 * @generated
	 * @ordered
	 */
	protected Schema additionalItems;

	/**
	 * The cached value of the '{@link #getContains() <em>Contains</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContains()
	 * @generated
	 * @ordered
	 */
	protected Schema contains;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Schema> properties;

	/**
	 * The cached value of the '{@link #getPatternProperties() <em>Pattern Properties</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPatternProperties()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Schema> patternProperties;

	/**
	 * The cached value of the '{@link #getAdditionalProperties() <em>Additional Properties</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAdditionalProperties()
	 * @generated
	 * @ordered
	 */
	protected Schema additionalProperties;

	/**
	 * The cached value of the '{@link #getPropertyNames() <em>Property Names</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyNames()
	 * @generated
	 * @ordered
	 */
	protected Schema propertyNames;

	/**
	 * The cached value of the '{@link #getUnevaluatedItems() <em>Unevaluated Items</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnevaluatedItems()
	 * @generated
	 * @ordered
	 */
	protected Schema unevaluatedItems;

	/**
	 * The cached value of the '{@link #getUnevaluatedProperties() <em>Unevaluated Properties</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnevaluatedProperties()
	 * @generated
	 * @ordered
	 */
	protected Schema unevaluatedProperties;

	/**
	 * The default value of the '{@link #getContentEncoding() <em>Content Encoding</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContentEncoding()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTENT_ENCODING_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContentEncoding() <em>Content Encoding</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContentEncoding()
	 * @generated
	 * @ordered
	 */
	protected String contentEncoding = CONTENT_ENCODING_EDEFAULT;

	/**
	 * The default value of the '{@link #getContentMediaType() <em>Content Media Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContentMediaType()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTENT_MEDIA_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContentMediaType() <em>Content Media Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContentMediaType()
	 * @generated
	 * @ordered
	 */
	protected String contentMediaType = CONTENT_MEDIA_TYPE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getContentSchema() <em>Content Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContentSchema()
	 * @generated
	 * @ordered
	 */
	protected Schema contentSchema;

	/**
	 * The default value of the '{@link #getNullable() <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNullable()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean NULLABLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNullable() <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNullable()
	 * @generated
	 * @ordered
	 */
	protected Boolean nullable = NULLABLE_EDEFAULT;

	/**
	 * This is true if the Nullable attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean nullableESet;

	/**
	 * The cached value of the '{@link #getDiscriminator() <em>Discriminator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiscriminator()
	 * @generated
	 * @ordered
	 */
	protected Discriminator discriminator;

	/**
	 * The cached value of the '{@link #getXml() <em>Xml</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXml()
	 * @generated
	 * @ordered
	 */
	protected Xml xml;

	/**
	 * The cached value of the '{@link #getExternalDocs() <em>External Docs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExternalDocs()
	 * @generated
	 * @ordered
	 */
	protected ExternalDocumentation externalDocs;

	/**
	 * The default value of the '{@link #getExample() <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExample()
	 * @generated
	 * @ordered
	 */
	protected static final Object EXAMPLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExample() <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExample()
	 * @generated
	 * @ordered
	 */
	protected Object example = EXAMPLE_EDEFAULT;

	/**
	 * This is true if the Example attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean exampleESet;

	/**
	 * The cached value of the '{@link #getKeywords() <em>Keywords</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKeywords()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Object> keywords;

	/**
	 * The cached value of the '{@link #getClassifier() <em>Classifier</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClassifier()
	 * @generated
	 * @ordered
	 */
	protected EClassifier classifier;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SchemaImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return JsonSchemaPackage.Literals.SCHEMA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getBooleanSchema() {
		return booleanSchema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBooleanSchema(Boolean newBooleanSchema) {
		Boolean oldBooleanSchema = booleanSchema;
		booleanSchema = newBooleanSchema;
		boolean oldBooleanSchemaESet = booleanSchemaESet;
		booleanSchemaESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__BOOLEAN_SCHEMA, oldBooleanSchema, booleanSchema, !oldBooleanSchemaESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetBooleanSchema() {
		Boolean oldBooleanSchema = booleanSchema;
		boolean oldBooleanSchemaESet = booleanSchemaESet;
		booleanSchema = BOOLEAN_SCHEMA_EDEFAULT;
		booleanSchemaESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__BOOLEAN_SCHEMA, oldBooleanSchema, BOOLEAN_SCHEMA_EDEFAULT, oldBooleanSchemaESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetBooleanSchema() {
		return booleanSchemaESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSchema() {
		return schema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSchema(String newSchema) {
		String oldSchema = schema;
		schema = newSchema;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__SCHEMA, oldSchema, schema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getLegacyId() {
		return legacyId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLegacyId(String newLegacyId) {
		String oldLegacyId = legacyId;
		legacyId = newLegacyId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__LEGACY_ID, oldLegacyId, legacyId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRef() {
		return ref;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRef(String newRef) {
		String oldRef = ref;
		ref = newRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__REF, oldRef, ref));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAnchor() {
		return anchor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAnchor(String newAnchor) {
		String oldAnchor = anchor;
		anchor = newAnchor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ANCHOR, oldAnchor, anchor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDynamicRef() {
		return dynamicRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDynamicRef(String newDynamicRef) {
		String oldDynamicRef = dynamicRef;
		dynamicRef = newDynamicRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__DYNAMIC_REF, oldDynamicRef, dynamicRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDynamicAnchor() {
		return dynamicAnchor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDynamicAnchor(String newDynamicAnchor) {
		String oldDynamicAnchor = dynamicAnchor;
		dynamicAnchor = newDynamicAnchor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__DYNAMIC_ANCHOR, oldDynamicAnchor, dynamicAnchor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRecursiveRef() {
		return recursiveRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRecursiveRef(String newRecursiveRef) {
		String oldRecursiveRef = recursiveRef;
		recursiveRef = newRecursiveRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__RECURSIVE_REF, oldRecursiveRef, recursiveRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getRecursiveAnchor() {
		return recursiveAnchor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRecursiveAnchor(Boolean newRecursiveAnchor) {
		Boolean oldRecursiveAnchor = recursiveAnchor;
		recursiveAnchor = newRecursiveAnchor;
		boolean oldRecursiveAnchorESet = recursiveAnchorESet;
		recursiveAnchorESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__RECURSIVE_ANCHOR, oldRecursiveAnchor, recursiveAnchor, !oldRecursiveAnchorESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetRecursiveAnchor() {
		Boolean oldRecursiveAnchor = recursiveAnchor;
		boolean oldRecursiveAnchorESet = recursiveAnchorESet;
		recursiveAnchor = RECURSIVE_ANCHOR_EDEFAULT;
		recursiveAnchorESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__RECURSIVE_ANCHOR, oldRecursiveAnchor, RECURSIVE_ANCHOR_EDEFAULT, oldRecursiveAnchorESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetRecursiveAnchor() {
		return recursiveAnchorESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Boolean> getVocabulary() {
		if (vocabulary == null) {
			vocabulary = new EcoreEMap.Unsettable<String,Boolean>(JsonSchemaPackage.Literals.VOCABULARY_ENTRY, VocabularyEntryImpl.class, this, JsonSchemaPackage.SCHEMA__VOCABULARY);
		}
		return vocabulary;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetVocabulary() {
		if (vocabulary != null) ((InternalEList.Unsettable<?>)vocabulary).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetVocabulary() {
		return vocabulary != null && ((InternalEList.Unsettable<?>)vocabulary).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getComment() {
		return comment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setComment(String newComment) {
		String oldComment = comment;
		comment = newComment;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__COMMENT, oldComment, comment));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Schema> getDefs() {
		if (defs == null) {
			defs = new EcoreEMap.Unsettable<String,Schema>(JsonSchemaPackage.Literals.SCHEMA_ENTRY, SchemaEntryImpl.class, this, JsonSchemaPackage.SCHEMA__DEFS);
		}
		return defs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetDefs() {
		if (defs != null) ((InternalEList.Unsettable<?>)defs).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetDefs() {
		return defs != null && ((InternalEList.Unsettable<?>)defs).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Schema> getDefinitions() {
		if (definitions == null) {
			definitions = new EcoreEMap.Unsettable<String,Schema>(JsonSchemaPackage.Literals.SCHEMA_ENTRY, SchemaEntryImpl.class, this, JsonSchemaPackage.SCHEMA__DEFINITIONS);
		}
		return definitions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetDefinitions() {
		if (definitions != null) ((InternalEList.Unsettable<?>)definitions).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetDefinitions() {
		return definitions != null && ((InternalEList.Unsettable<?>)definitions).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTitle(String newTitle) {
		String oldTitle = title;
		title = newTitle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__TITLE, oldTitle, title));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getDefault() {
		return default_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDefault(Object newDefault) {
		Object oldDefault = default_;
		default_ = newDefault;
		boolean oldDefaultESet = defaultESet;
		defaultESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__DEFAULT, oldDefault, default_, !oldDefaultESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetDefault() {
		Object oldDefault = default_;
		boolean oldDefaultESet = defaultESet;
		default_ = DEFAULT_EDEFAULT;
		defaultESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__DEFAULT, oldDefault, DEFAULT_EDEFAULT, oldDefaultESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetDefault() {
		return defaultESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getDeprecated() {
		return deprecated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeprecated(Boolean newDeprecated) {
		Boolean oldDeprecated = deprecated;
		deprecated = newDeprecated;
		boolean oldDeprecatedESet = deprecatedESet;
		deprecatedESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__DEPRECATED, oldDeprecated, deprecated, !oldDeprecatedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetDeprecated() {
		Boolean oldDeprecated = deprecated;
		boolean oldDeprecatedESet = deprecatedESet;
		deprecated = DEPRECATED_EDEFAULT;
		deprecatedESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__DEPRECATED, oldDeprecated, DEPRECATED_EDEFAULT, oldDeprecatedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetDeprecated() {
		return deprecatedESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getReadOnly() {
		return readOnly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setReadOnly(Boolean newReadOnly) {
		Boolean oldReadOnly = readOnly;
		readOnly = newReadOnly;
		boolean oldReadOnlyESet = readOnlyESet;
		readOnlyESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__READ_ONLY, oldReadOnly, readOnly, !oldReadOnlyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetReadOnly() {
		Boolean oldReadOnly = readOnly;
		boolean oldReadOnlyESet = readOnlyESet;
		readOnly = READ_ONLY_EDEFAULT;
		readOnlyESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__READ_ONLY, oldReadOnly, READ_ONLY_EDEFAULT, oldReadOnlyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetReadOnly() {
		return readOnlyESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getWriteOnly() {
		return writeOnly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWriteOnly(Boolean newWriteOnly) {
		Boolean oldWriteOnly = writeOnly;
		writeOnly = newWriteOnly;
		boolean oldWriteOnlyESet = writeOnlyESet;
		writeOnlyESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__WRITE_ONLY, oldWriteOnly, writeOnly, !oldWriteOnlyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetWriteOnly() {
		Boolean oldWriteOnly = writeOnly;
		boolean oldWriteOnlyESet = writeOnlyESet;
		writeOnly = WRITE_ONLY_EDEFAULT;
		writeOnlyESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__WRITE_ONLY, oldWriteOnly, WRITE_ONLY_EDEFAULT, oldWriteOnlyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetWriteOnly() {
		return writeOnlyESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Object> getExamples() {
		if (examples == null) {
			examples = new EDataTypeEList.Unsettable<Object>(Object.class, this, JsonSchemaPackage.SCHEMA__EXAMPLES);
		}
		return examples;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExamples() {
		if (examples != null) ((InternalEList.Unsettable<?>)examples).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExamples() {
		return examples != null && ((InternalEList.Unsettable<?>)examples).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<SimpleType> getType() {
		if (type == null) {
			type = new EDataTypeEList.Unsettable<SimpleType>(SimpleType.class, this, JsonSchemaPackage.SCHEMA__TYPE);
		}
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetType() {
		if (type != null) ((InternalEList.Unsettable<?>)type).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetType() {
		return type != null && ((InternalEList.Unsettable<?>)type).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isTypeAsList() {
		return typeAsList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTypeAsList(boolean newTypeAsList) {
		boolean oldTypeAsList = typeAsList;
		typeAsList = newTypeAsList;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__TYPE_AS_LIST, oldTypeAsList, typeAsList));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Object> getEnum() {
		if (enum_ == null) {
			enum_ = new EDataTypeEList.Unsettable<Object>(Object.class, this, JsonSchemaPackage.SCHEMA__ENUM);
		}
		return enum_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetEnum() {
		if (enum_ != null) ((InternalEList.Unsettable<?>)enum_).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetEnum() {
		return enum_ != null && ((InternalEList.Unsettable<?>)enum_).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getConst() {
		return const_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setConst(Object newConst) {
		Object oldConst = const_;
		const_ = newConst;
		boolean oldConstESet = constESet;
		constESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__CONST, oldConst, const_, !oldConstESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetConst() {
		Object oldConst = const_;
		boolean oldConstESet = constESet;
		const_ = CONST_EDEFAULT;
		constESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__CONST, oldConst, CONST_EDEFAULT, oldConstESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetConst() {
		return constESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BigDecimal getMultipleOf() {
		return multipleOf;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMultipleOf(BigDecimal newMultipleOf) {
		BigDecimal oldMultipleOf = multipleOf;
		multipleOf = newMultipleOf;
		boolean oldMultipleOfESet = multipleOfESet;
		multipleOfESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MULTIPLE_OF, oldMultipleOf, multipleOf, !oldMultipleOfESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMultipleOf() {
		BigDecimal oldMultipleOf = multipleOf;
		boolean oldMultipleOfESet = multipleOfESet;
		multipleOf = MULTIPLE_OF_EDEFAULT;
		multipleOfESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MULTIPLE_OF, oldMultipleOf, MULTIPLE_OF_EDEFAULT, oldMultipleOfESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMultipleOf() {
		return multipleOfESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BigDecimal getMaximum() {
		return maximum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMaximum(BigDecimal newMaximum) {
		BigDecimal oldMaximum = maximum;
		maximum = newMaximum;
		boolean oldMaximumESet = maximumESet;
		maximumESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MAXIMUM, oldMaximum, maximum, !oldMaximumESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMaximum() {
		BigDecimal oldMaximum = maximum;
		boolean oldMaximumESet = maximumESet;
		maximum = MAXIMUM_EDEFAULT;
		maximumESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MAXIMUM, oldMaximum, MAXIMUM_EDEFAULT, oldMaximumESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMaximum() {
		return maximumESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BigDecimal getExclusiveMaximum() {
		return exclusiveMaximum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExclusiveMaximum(BigDecimal newExclusiveMaximum) {
		BigDecimal oldExclusiveMaximum = exclusiveMaximum;
		exclusiveMaximum = newExclusiveMaximum;
		boolean oldExclusiveMaximumESet = exclusiveMaximumESet;
		exclusiveMaximumESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM, oldExclusiveMaximum, exclusiveMaximum, !oldExclusiveMaximumESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExclusiveMaximum() {
		BigDecimal oldExclusiveMaximum = exclusiveMaximum;
		boolean oldExclusiveMaximumESet = exclusiveMaximumESet;
		exclusiveMaximum = EXCLUSIVE_MAXIMUM_EDEFAULT;
		exclusiveMaximumESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM, oldExclusiveMaximum, EXCLUSIVE_MAXIMUM_EDEFAULT, oldExclusiveMaximumESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExclusiveMaximum() {
		return exclusiveMaximumESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getExclusiveMaximumFlag() {
		return exclusiveMaximumFlag;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExclusiveMaximumFlag(Boolean newExclusiveMaximumFlag) {
		Boolean oldExclusiveMaximumFlag = exclusiveMaximumFlag;
		exclusiveMaximumFlag = newExclusiveMaximumFlag;
		boolean oldExclusiveMaximumFlagESet = exclusiveMaximumFlagESet;
		exclusiveMaximumFlagESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM_FLAG, oldExclusiveMaximumFlag, exclusiveMaximumFlag, !oldExclusiveMaximumFlagESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExclusiveMaximumFlag() {
		Boolean oldExclusiveMaximumFlag = exclusiveMaximumFlag;
		boolean oldExclusiveMaximumFlagESet = exclusiveMaximumFlagESet;
		exclusiveMaximumFlag = EXCLUSIVE_MAXIMUM_FLAG_EDEFAULT;
		exclusiveMaximumFlagESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM_FLAG, oldExclusiveMaximumFlag, EXCLUSIVE_MAXIMUM_FLAG_EDEFAULT, oldExclusiveMaximumFlagESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExclusiveMaximumFlag() {
		return exclusiveMaximumFlagESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BigDecimal getMinimum() {
		return minimum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMinimum(BigDecimal newMinimum) {
		BigDecimal oldMinimum = minimum;
		minimum = newMinimum;
		boolean oldMinimumESet = minimumESet;
		minimumESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MINIMUM, oldMinimum, minimum, !oldMinimumESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMinimum() {
		BigDecimal oldMinimum = minimum;
		boolean oldMinimumESet = minimumESet;
		minimum = MINIMUM_EDEFAULT;
		minimumESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MINIMUM, oldMinimum, MINIMUM_EDEFAULT, oldMinimumESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMinimum() {
		return minimumESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BigDecimal getExclusiveMinimum() {
		return exclusiveMinimum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExclusiveMinimum(BigDecimal newExclusiveMinimum) {
		BigDecimal oldExclusiveMinimum = exclusiveMinimum;
		exclusiveMinimum = newExclusiveMinimum;
		boolean oldExclusiveMinimumESet = exclusiveMinimumESet;
		exclusiveMinimumESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM, oldExclusiveMinimum, exclusiveMinimum, !oldExclusiveMinimumESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExclusiveMinimum() {
		BigDecimal oldExclusiveMinimum = exclusiveMinimum;
		boolean oldExclusiveMinimumESet = exclusiveMinimumESet;
		exclusiveMinimum = EXCLUSIVE_MINIMUM_EDEFAULT;
		exclusiveMinimumESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM, oldExclusiveMinimum, EXCLUSIVE_MINIMUM_EDEFAULT, oldExclusiveMinimumESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExclusiveMinimum() {
		return exclusiveMinimumESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getExclusiveMinimumFlag() {
		return exclusiveMinimumFlag;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExclusiveMinimumFlag(Boolean newExclusiveMinimumFlag) {
		Boolean oldExclusiveMinimumFlag = exclusiveMinimumFlag;
		exclusiveMinimumFlag = newExclusiveMinimumFlag;
		boolean oldExclusiveMinimumFlagESet = exclusiveMinimumFlagESet;
		exclusiveMinimumFlagESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM_FLAG, oldExclusiveMinimumFlag, exclusiveMinimumFlag, !oldExclusiveMinimumFlagESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExclusiveMinimumFlag() {
		Boolean oldExclusiveMinimumFlag = exclusiveMinimumFlag;
		boolean oldExclusiveMinimumFlagESet = exclusiveMinimumFlagESet;
		exclusiveMinimumFlag = EXCLUSIVE_MINIMUM_FLAG_EDEFAULT;
		exclusiveMinimumFlagESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM_FLAG, oldExclusiveMinimumFlag, EXCLUSIVE_MINIMUM_FLAG_EDEFAULT, oldExclusiveMinimumFlagESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExclusiveMinimumFlag() {
		return exclusiveMinimumFlagESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getMaxLength() {
		return maxLength;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMaxLength(Integer newMaxLength) {
		Integer oldMaxLength = maxLength;
		maxLength = newMaxLength;
		boolean oldMaxLengthESet = maxLengthESet;
		maxLengthESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MAX_LENGTH, oldMaxLength, maxLength, !oldMaxLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMaxLength() {
		Integer oldMaxLength = maxLength;
		boolean oldMaxLengthESet = maxLengthESet;
		maxLength = MAX_LENGTH_EDEFAULT;
		maxLengthESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MAX_LENGTH, oldMaxLength, MAX_LENGTH_EDEFAULT, oldMaxLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMaxLength() {
		return maxLengthESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getMinLength() {
		return minLength;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMinLength(Integer newMinLength) {
		Integer oldMinLength = minLength;
		minLength = newMinLength;
		boolean oldMinLengthESet = minLengthESet;
		minLengthESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MIN_LENGTH, oldMinLength, minLength, !oldMinLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMinLength() {
		Integer oldMinLength = minLength;
		boolean oldMinLengthESet = minLengthESet;
		minLength = MIN_LENGTH_EDEFAULT;
		minLengthESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MIN_LENGTH, oldMinLength, MIN_LENGTH_EDEFAULT, oldMinLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMinLength() {
		return minLengthESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPattern() {
		return pattern;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPattern(String newPattern) {
		String oldPattern = pattern;
		pattern = newPattern;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__PATTERN, oldPattern, pattern));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getMaxItems() {
		return maxItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMaxItems(Integer newMaxItems) {
		Integer oldMaxItems = maxItems;
		maxItems = newMaxItems;
		boolean oldMaxItemsESet = maxItemsESet;
		maxItemsESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MAX_ITEMS, oldMaxItems, maxItems, !oldMaxItemsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMaxItems() {
		Integer oldMaxItems = maxItems;
		boolean oldMaxItemsESet = maxItemsESet;
		maxItems = MAX_ITEMS_EDEFAULT;
		maxItemsESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MAX_ITEMS, oldMaxItems, MAX_ITEMS_EDEFAULT, oldMaxItemsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMaxItems() {
		return maxItemsESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getMinItems() {
		return minItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMinItems(Integer newMinItems) {
		Integer oldMinItems = minItems;
		minItems = newMinItems;
		boolean oldMinItemsESet = minItemsESet;
		minItemsESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MIN_ITEMS, oldMinItems, minItems, !oldMinItemsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMinItems() {
		Integer oldMinItems = minItems;
		boolean oldMinItemsESet = minItemsESet;
		minItems = MIN_ITEMS_EDEFAULT;
		minItemsESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MIN_ITEMS, oldMinItems, MIN_ITEMS_EDEFAULT, oldMinItemsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMinItems() {
		return minItemsESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getUniqueItems() {
		return uniqueItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUniqueItems(Boolean newUniqueItems) {
		Boolean oldUniqueItems = uniqueItems;
		uniqueItems = newUniqueItems;
		boolean oldUniqueItemsESet = uniqueItemsESet;
		uniqueItemsESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__UNIQUE_ITEMS, oldUniqueItems, uniqueItems, !oldUniqueItemsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetUniqueItems() {
		Boolean oldUniqueItems = uniqueItems;
		boolean oldUniqueItemsESet = uniqueItemsESet;
		uniqueItems = UNIQUE_ITEMS_EDEFAULT;
		uniqueItemsESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__UNIQUE_ITEMS, oldUniqueItems, UNIQUE_ITEMS_EDEFAULT, oldUniqueItemsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetUniqueItems() {
		return uniqueItemsESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getMaxContains() {
		return maxContains;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMaxContains(Integer newMaxContains) {
		Integer oldMaxContains = maxContains;
		maxContains = newMaxContains;
		boolean oldMaxContainsESet = maxContainsESet;
		maxContainsESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MAX_CONTAINS, oldMaxContains, maxContains, !oldMaxContainsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMaxContains() {
		Integer oldMaxContains = maxContains;
		boolean oldMaxContainsESet = maxContainsESet;
		maxContains = MAX_CONTAINS_EDEFAULT;
		maxContainsESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MAX_CONTAINS, oldMaxContains, MAX_CONTAINS_EDEFAULT, oldMaxContainsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMaxContains() {
		return maxContainsESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getMinContains() {
		return minContains;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMinContains(Integer newMinContains) {
		Integer oldMinContains = minContains;
		minContains = newMinContains;
		boolean oldMinContainsESet = minContainsESet;
		minContainsESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MIN_CONTAINS, oldMinContains, minContains, !oldMinContainsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMinContains() {
		Integer oldMinContains = minContains;
		boolean oldMinContainsESet = minContainsESet;
		minContains = MIN_CONTAINS_EDEFAULT;
		minContainsESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MIN_CONTAINS, oldMinContains, MIN_CONTAINS_EDEFAULT, oldMinContainsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMinContains() {
		return minContainsESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getMaxProperties() {
		return maxProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMaxProperties(Integer newMaxProperties) {
		Integer oldMaxProperties = maxProperties;
		maxProperties = newMaxProperties;
		boolean oldMaxPropertiesESet = maxPropertiesESet;
		maxPropertiesESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MAX_PROPERTIES, oldMaxProperties, maxProperties, !oldMaxPropertiesESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMaxProperties() {
		Integer oldMaxProperties = maxProperties;
		boolean oldMaxPropertiesESet = maxPropertiesESet;
		maxProperties = MAX_PROPERTIES_EDEFAULT;
		maxPropertiesESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MAX_PROPERTIES, oldMaxProperties, MAX_PROPERTIES_EDEFAULT, oldMaxPropertiesESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMaxProperties() {
		return maxPropertiesESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getMinProperties() {
		return minProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMinProperties(Integer newMinProperties) {
		Integer oldMinProperties = minProperties;
		minProperties = newMinProperties;
		boolean oldMinPropertiesESet = minPropertiesESet;
		minPropertiesESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__MIN_PROPERTIES, oldMinProperties, minProperties, !oldMinPropertiesESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMinProperties() {
		Integer oldMinProperties = minProperties;
		boolean oldMinPropertiesESet = minPropertiesESet;
		minProperties = MIN_PROPERTIES_EDEFAULT;
		minPropertiesESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__MIN_PROPERTIES, oldMinProperties, MIN_PROPERTIES_EDEFAULT, oldMinPropertiesESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMinProperties() {
		return minPropertiesESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getRequired() {
		if (required == null) {
			required = new EDataTypeEList.Unsettable<String>(String.class, this, JsonSchemaPackage.SCHEMA__REQUIRED);
		}
		return required;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetRequired() {
		if (required != null) ((InternalEList.Unsettable<?>)required).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetRequired() {
		return required != null && ((InternalEList.Unsettable<?>)required).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, EList<String>> getDependentRequired() {
		if (dependentRequired == null) {
			dependentRequired = new EcoreEMap.Unsettable<String,EList<String>>(JsonSchemaPackage.Literals.STRING_LIST_ENTRY, StringListEntryImpl.class, this, JsonSchemaPackage.SCHEMA__DEPENDENT_REQUIRED);
		}
		return dependentRequired;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetDependentRequired() {
		if (dependentRequired != null) ((InternalEList.Unsettable<?>)dependentRequired).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetDependentRequired() {
		return dependentRequired != null && ((InternalEList.Unsettable<?>)dependentRequired).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getFormat() {
		return format;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFormat(String newFormat) {
		String oldFormat = format;
		format = newFormat;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__FORMAT, oldFormat, format));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Schema> getAllOf() {
		if (allOf == null) {
			allOf = new EObjectContainmentEList.Unsettable<Schema>(Schema.class, this, JsonSchemaPackage.SCHEMA__ALL_OF);
		}
		return allOf;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetAllOf() {
		if (allOf != null) ((InternalEList.Unsettable<?>)allOf).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetAllOf() {
		return allOf != null && ((InternalEList.Unsettable<?>)allOf).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Schema> getAnyOf() {
		if (anyOf == null) {
			anyOf = new EObjectContainmentEList.Unsettable<Schema>(Schema.class, this, JsonSchemaPackage.SCHEMA__ANY_OF);
		}
		return anyOf;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetAnyOf() {
		if (anyOf != null) ((InternalEList.Unsettable<?>)anyOf).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetAnyOf() {
		return anyOf != null && ((InternalEList.Unsettable<?>)anyOf).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Schema> getOneOf() {
		if (oneOf == null) {
			oneOf = new EObjectContainmentEList.Unsettable<Schema>(Schema.class, this, JsonSchemaPackage.SCHEMA__ONE_OF);
		}
		return oneOf;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetOneOf() {
		if (oneOf != null) ((InternalEList.Unsettable<?>)oneOf).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetOneOf() {
		return oneOf != null && ((InternalEList.Unsettable<?>)oneOf).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getNot() {
		return not;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNot(Schema newNot, NotificationChain msgs) {
		Schema oldNot = not;
		not = newNot;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__NOT, oldNot, newNot);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNot(Schema newNot) {
		if (newNot != not) {
			NotificationChain msgs = null;
			if (not != null)
				msgs = ((InternalEObject)not).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__NOT, null, msgs);
			if (newNot != null)
				msgs = ((InternalEObject)newNot).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__NOT, null, msgs);
			msgs = basicSetNot(newNot, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__NOT, newNot, newNot));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getIf() {
		return if_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIf(Schema newIf, NotificationChain msgs) {
		Schema oldIf = if_;
		if_ = newIf;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__IF, oldIf, newIf);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIf(Schema newIf) {
		if (newIf != if_) {
			NotificationChain msgs = null;
			if (if_ != null)
				msgs = ((InternalEObject)if_).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__IF, null, msgs);
			if (newIf != null)
				msgs = ((InternalEObject)newIf).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__IF, null, msgs);
			msgs = basicSetIf(newIf, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__IF, newIf, newIf));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getThen() {
		return then;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetThen(Schema newThen, NotificationChain msgs) {
		Schema oldThen = then;
		then = newThen;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__THEN, oldThen, newThen);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setThen(Schema newThen) {
		if (newThen != then) {
			NotificationChain msgs = null;
			if (then != null)
				msgs = ((InternalEObject)then).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__THEN, null, msgs);
			if (newThen != null)
				msgs = ((InternalEObject)newThen).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__THEN, null, msgs);
			msgs = basicSetThen(newThen, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__THEN, newThen, newThen));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getElse() {
		return else_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetElse(Schema newElse, NotificationChain msgs) {
		Schema oldElse = else_;
		else_ = newElse;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ELSE, oldElse, newElse);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setElse(Schema newElse) {
		if (newElse != else_) {
			NotificationChain msgs = null;
			if (else_ != null)
				msgs = ((InternalEObject)else_).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__ELSE, null, msgs);
			if (newElse != null)
				msgs = ((InternalEObject)newElse).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__ELSE, null, msgs);
			msgs = basicSetElse(newElse, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ELSE, newElse, newElse));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Schema> getDependentSchemas() {
		if (dependentSchemas == null) {
			dependentSchemas = new EcoreEMap.Unsettable<String,Schema>(JsonSchemaPackage.Literals.SCHEMA_ENTRY, SchemaEntryImpl.class, this, JsonSchemaPackage.SCHEMA__DEPENDENT_SCHEMAS);
		}
		return dependentSchemas;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetDependentSchemas() {
		if (dependentSchemas != null) ((InternalEList.Unsettable<?>)dependentSchemas).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetDependentSchemas() {
		return dependentSchemas != null && ((InternalEList.Unsettable<?>)dependentSchemas).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Dependency> getDependencies() {
		if (dependencies == null) {
			dependencies = new EcoreEMap.Unsettable<String,Dependency>(JsonSchemaPackage.Literals.DEPENDENCY_ENTRY, DependencyEntryImpl.class, this, JsonSchemaPackage.SCHEMA__DEPENDENCIES);
		}
		return dependencies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetDependencies() {
		if (dependencies != null) ((InternalEList.Unsettable<?>)dependencies).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetDependencies() {
		return dependencies != null && ((InternalEList.Unsettable<?>)dependencies).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Schema> getPrefixItems() {
		if (prefixItems == null) {
			prefixItems = new EObjectContainmentEList.Unsettable<Schema>(Schema.class, this, JsonSchemaPackage.SCHEMA__PREFIX_ITEMS);
		}
		return prefixItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetPrefixItems() {
		if (prefixItems != null) ((InternalEList.Unsettable<?>)prefixItems).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetPrefixItems() {
		return prefixItems != null && ((InternalEList.Unsettable<?>)prefixItems).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getItems() {
		return items;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetItems(Schema newItems, NotificationChain msgs) {
		Schema oldItems = items;
		items = newItems;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ITEMS, oldItems, newItems);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setItems(Schema newItems) {
		if (newItems != items) {
			NotificationChain msgs = null;
			if (items != null)
				msgs = ((InternalEObject)items).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__ITEMS, null, msgs);
			if (newItems != null)
				msgs = ((InternalEObject)newItems).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__ITEMS, null, msgs);
			msgs = basicSetItems(newItems, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ITEMS, newItems, newItems));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Schema> getItemsList() {
		if (itemsList == null) {
			itemsList = new EObjectContainmentEList.Unsettable<Schema>(Schema.class, this, JsonSchemaPackage.SCHEMA__ITEMS_LIST);
		}
		return itemsList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetItemsList() {
		if (itemsList != null) ((InternalEList.Unsettable<?>)itemsList).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetItemsList() {
		return itemsList != null && ((InternalEList.Unsettable<?>)itemsList).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getAdditionalItems() {
		return additionalItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAdditionalItems(Schema newAdditionalItems, NotificationChain msgs) {
		Schema oldAdditionalItems = additionalItems;
		additionalItems = newAdditionalItems;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ADDITIONAL_ITEMS, oldAdditionalItems, newAdditionalItems);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAdditionalItems(Schema newAdditionalItems) {
		if (newAdditionalItems != additionalItems) {
			NotificationChain msgs = null;
			if (additionalItems != null)
				msgs = ((InternalEObject)additionalItems).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__ADDITIONAL_ITEMS, null, msgs);
			if (newAdditionalItems != null)
				msgs = ((InternalEObject)newAdditionalItems).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__ADDITIONAL_ITEMS, null, msgs);
			msgs = basicSetAdditionalItems(newAdditionalItems, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ADDITIONAL_ITEMS, newAdditionalItems, newAdditionalItems));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getContains() {
		return contains;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetContains(Schema newContains, NotificationChain msgs) {
		Schema oldContains = contains;
		contains = newContains;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__CONTAINS, oldContains, newContains);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContains(Schema newContains) {
		if (newContains != contains) {
			NotificationChain msgs = null;
			if (contains != null)
				msgs = ((InternalEObject)contains).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__CONTAINS, null, msgs);
			if (newContains != null)
				msgs = ((InternalEObject)newContains).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__CONTAINS, null, msgs);
			msgs = basicSetContains(newContains, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__CONTAINS, newContains, newContains));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Schema> getProperties() {
		if (properties == null) {
			properties = new EcoreEMap.Unsettable<String,Schema>(JsonSchemaPackage.Literals.SCHEMA_ENTRY, SchemaEntryImpl.class, this, JsonSchemaPackage.SCHEMA__PROPERTIES);
		}
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetProperties() {
		if (properties != null) ((InternalEList.Unsettable<?>)properties).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetProperties() {
		return properties != null && ((InternalEList.Unsettable<?>)properties).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Schema> getPatternProperties() {
		if (patternProperties == null) {
			patternProperties = new EcoreEMap.Unsettable<String,Schema>(JsonSchemaPackage.Literals.SCHEMA_ENTRY, SchemaEntryImpl.class, this, JsonSchemaPackage.SCHEMA__PATTERN_PROPERTIES);
		}
		return patternProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetPatternProperties() {
		if (patternProperties != null) ((InternalEList.Unsettable<?>)patternProperties).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetPatternProperties() {
		return patternProperties != null && ((InternalEList.Unsettable<?>)patternProperties).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getAdditionalProperties() {
		return additionalProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAdditionalProperties(Schema newAdditionalProperties, NotificationChain msgs) {
		Schema oldAdditionalProperties = additionalProperties;
		additionalProperties = newAdditionalProperties;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ADDITIONAL_PROPERTIES, oldAdditionalProperties, newAdditionalProperties);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAdditionalProperties(Schema newAdditionalProperties) {
		if (newAdditionalProperties != additionalProperties) {
			NotificationChain msgs = null;
			if (additionalProperties != null)
				msgs = ((InternalEObject)additionalProperties).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__ADDITIONAL_PROPERTIES, null, msgs);
			if (newAdditionalProperties != null)
				msgs = ((InternalEObject)newAdditionalProperties).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__ADDITIONAL_PROPERTIES, null, msgs);
			msgs = basicSetAdditionalProperties(newAdditionalProperties, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__ADDITIONAL_PROPERTIES, newAdditionalProperties, newAdditionalProperties));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getPropertyNames() {
		return propertyNames;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPropertyNames(Schema newPropertyNames, NotificationChain msgs) {
		Schema oldPropertyNames = propertyNames;
		propertyNames = newPropertyNames;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__PROPERTY_NAMES, oldPropertyNames, newPropertyNames);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPropertyNames(Schema newPropertyNames) {
		if (newPropertyNames != propertyNames) {
			NotificationChain msgs = null;
			if (propertyNames != null)
				msgs = ((InternalEObject)propertyNames).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__PROPERTY_NAMES, null, msgs);
			if (newPropertyNames != null)
				msgs = ((InternalEObject)newPropertyNames).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__PROPERTY_NAMES, null, msgs);
			msgs = basicSetPropertyNames(newPropertyNames, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__PROPERTY_NAMES, newPropertyNames, newPropertyNames));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getUnevaluatedItems() {
		return unevaluatedItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUnevaluatedItems(Schema newUnevaluatedItems, NotificationChain msgs) {
		Schema oldUnevaluatedItems = unevaluatedItems;
		unevaluatedItems = newUnevaluatedItems;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__UNEVALUATED_ITEMS, oldUnevaluatedItems, newUnevaluatedItems);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUnevaluatedItems(Schema newUnevaluatedItems) {
		if (newUnevaluatedItems != unevaluatedItems) {
			NotificationChain msgs = null;
			if (unevaluatedItems != null)
				msgs = ((InternalEObject)unevaluatedItems).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__UNEVALUATED_ITEMS, null, msgs);
			if (newUnevaluatedItems != null)
				msgs = ((InternalEObject)newUnevaluatedItems).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__UNEVALUATED_ITEMS, null, msgs);
			msgs = basicSetUnevaluatedItems(newUnevaluatedItems, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__UNEVALUATED_ITEMS, newUnevaluatedItems, newUnevaluatedItems));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getUnevaluatedProperties() {
		return unevaluatedProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUnevaluatedProperties(Schema newUnevaluatedProperties, NotificationChain msgs) {
		Schema oldUnevaluatedProperties = unevaluatedProperties;
		unevaluatedProperties = newUnevaluatedProperties;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__UNEVALUATED_PROPERTIES, oldUnevaluatedProperties, newUnevaluatedProperties);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUnevaluatedProperties(Schema newUnevaluatedProperties) {
		if (newUnevaluatedProperties != unevaluatedProperties) {
			NotificationChain msgs = null;
			if (unevaluatedProperties != null)
				msgs = ((InternalEObject)unevaluatedProperties).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__UNEVALUATED_PROPERTIES, null, msgs);
			if (newUnevaluatedProperties != null)
				msgs = ((InternalEObject)newUnevaluatedProperties).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__UNEVALUATED_PROPERTIES, null, msgs);
			msgs = basicSetUnevaluatedProperties(newUnevaluatedProperties, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__UNEVALUATED_PROPERTIES, newUnevaluatedProperties, newUnevaluatedProperties));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getContentEncoding() {
		return contentEncoding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContentEncoding(String newContentEncoding) {
		String oldContentEncoding = contentEncoding;
		contentEncoding = newContentEncoding;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__CONTENT_ENCODING, oldContentEncoding, contentEncoding));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getContentMediaType() {
		return contentMediaType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContentMediaType(String newContentMediaType) {
		String oldContentMediaType = contentMediaType;
		contentMediaType = newContentMediaType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__CONTENT_MEDIA_TYPE, oldContentMediaType, contentMediaType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getContentSchema() {
		return contentSchema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetContentSchema(Schema newContentSchema, NotificationChain msgs) {
		Schema oldContentSchema = contentSchema;
		contentSchema = newContentSchema;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__CONTENT_SCHEMA, oldContentSchema, newContentSchema);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContentSchema(Schema newContentSchema) {
		if (newContentSchema != contentSchema) {
			NotificationChain msgs = null;
			if (contentSchema != null)
				msgs = ((InternalEObject)contentSchema).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__CONTENT_SCHEMA, null, msgs);
			if (newContentSchema != null)
				msgs = ((InternalEObject)newContentSchema).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__CONTENT_SCHEMA, null, msgs);
			msgs = basicSetContentSchema(newContentSchema, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__CONTENT_SCHEMA, newContentSchema, newContentSchema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getNullable() {
		return nullable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNullable(Boolean newNullable) {
		Boolean oldNullable = nullable;
		nullable = newNullable;
		boolean oldNullableESet = nullableESet;
		nullableESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__NULLABLE, oldNullable, nullable, !oldNullableESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetNullable() {
		Boolean oldNullable = nullable;
		boolean oldNullableESet = nullableESet;
		nullable = NULLABLE_EDEFAULT;
		nullableESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__NULLABLE, oldNullable, NULLABLE_EDEFAULT, oldNullableESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetNullable() {
		return nullableESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Discriminator getDiscriminator() {
		return discriminator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDiscriminator(Discriminator newDiscriminator, NotificationChain msgs) {
		Discriminator oldDiscriminator = discriminator;
		discriminator = newDiscriminator;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__DISCRIMINATOR, oldDiscriminator, newDiscriminator);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDiscriminator(Discriminator newDiscriminator) {
		if (newDiscriminator != discriminator) {
			NotificationChain msgs = null;
			if (discriminator != null)
				msgs = ((InternalEObject)discriminator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__DISCRIMINATOR, null, msgs);
			if (newDiscriminator != null)
				msgs = ((InternalEObject)newDiscriminator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__DISCRIMINATOR, null, msgs);
			msgs = basicSetDiscriminator(newDiscriminator, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__DISCRIMINATOR, newDiscriminator, newDiscriminator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Xml getXml() {
		return xml;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetXml(Xml newXml, NotificationChain msgs) {
		Xml oldXml = xml;
		xml = newXml;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__XML, oldXml, newXml);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setXml(Xml newXml) {
		if (newXml != xml) {
			NotificationChain msgs = null;
			if (xml != null)
				msgs = ((InternalEObject)xml).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__XML, null, msgs);
			if (newXml != null)
				msgs = ((InternalEObject)newXml).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__XML, null, msgs);
			msgs = basicSetXml(newXml, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__XML, newXml, newXml));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ExternalDocumentation getExternalDocs() {
		return externalDocs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExternalDocs(ExternalDocumentation newExternalDocs, NotificationChain msgs) {
		ExternalDocumentation oldExternalDocs = externalDocs;
		externalDocs = newExternalDocs;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__EXTERNAL_DOCS, oldExternalDocs, newExternalDocs);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExternalDocs(ExternalDocumentation newExternalDocs) {
		if (newExternalDocs != externalDocs) {
			NotificationChain msgs = null;
			if (externalDocs != null)
				msgs = ((InternalEObject)externalDocs).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__EXTERNAL_DOCS, null, msgs);
			if (newExternalDocs != null)
				msgs = ((InternalEObject)newExternalDocs).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.SCHEMA__EXTERNAL_DOCS, null, msgs);
			msgs = basicSetExternalDocs(newExternalDocs, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__EXTERNAL_DOCS, newExternalDocs, newExternalDocs));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getExample() {
		return example;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExample(Object newExample) {
		Object oldExample = example;
		example = newExample;
		boolean oldExampleESet = exampleESet;
		exampleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__EXAMPLE, oldExample, example, !oldExampleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExample() {
		Object oldExample = example;
		boolean oldExampleESet = exampleESet;
		example = EXAMPLE_EDEFAULT;
		exampleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.SCHEMA__EXAMPLE, oldExample, EXAMPLE_EDEFAULT, oldExampleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExample() {
		return exampleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getKeywords() {
		if (keywords == null) {
			keywords = new EcoreEMap.Unsettable<String,Object>(JsonSchemaPackage.Literals.KEYWORD_ENTRY, KeywordEntryImpl.class, this, JsonSchemaPackage.SCHEMA__KEYWORDS);
		}
		return keywords;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetKeywords() {
		if (keywords != null) ((InternalEList.Unsettable<?>)keywords).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetKeywords() {
		return keywords != null && ((InternalEList.Unsettable<?>)keywords).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClassifier getClassifier() {
		if (classifier != null && classifier.eIsProxy()) {
			InternalEObject oldClassifier = (InternalEObject)classifier;
			classifier = (EClassifier)eResolveProxy(oldClassifier);
			if (classifier != oldClassifier) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, JsonSchemaPackage.SCHEMA__CLASSIFIER, oldClassifier, classifier));
			}
		}
		return classifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClassifier basicGetClassifier() {
		return classifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setClassifier(EClassifier newClassifier) {
		EClassifier oldClassifier = classifier;
		classifier = newClassifier;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.SCHEMA__CLASSIFIER, oldClassifier, classifier));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case JsonSchemaPackage.SCHEMA__VOCABULARY:
				return ((InternalEList<?>)getVocabulary()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__DEFS:
				return ((InternalEList<?>)getDefs()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__DEFINITIONS:
				return ((InternalEList<?>)getDefinitions()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__DEPENDENT_REQUIRED:
				return ((InternalEList<?>)getDependentRequired()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__ALL_OF:
				return ((InternalEList<?>)getAllOf()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__ANY_OF:
				return ((InternalEList<?>)getAnyOf()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__ONE_OF:
				return ((InternalEList<?>)getOneOf()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__NOT:
				return basicSetNot(null, msgs);
			case JsonSchemaPackage.SCHEMA__IF:
				return basicSetIf(null, msgs);
			case JsonSchemaPackage.SCHEMA__THEN:
				return basicSetThen(null, msgs);
			case JsonSchemaPackage.SCHEMA__ELSE:
				return basicSetElse(null, msgs);
			case JsonSchemaPackage.SCHEMA__DEPENDENT_SCHEMAS:
				return ((InternalEList<?>)getDependentSchemas()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__DEPENDENCIES:
				return ((InternalEList<?>)getDependencies()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__PREFIX_ITEMS:
				return ((InternalEList<?>)getPrefixItems()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__ITEMS:
				return basicSetItems(null, msgs);
			case JsonSchemaPackage.SCHEMA__ITEMS_LIST:
				return ((InternalEList<?>)getItemsList()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_ITEMS:
				return basicSetAdditionalItems(null, msgs);
			case JsonSchemaPackage.SCHEMA__CONTAINS:
				return basicSetContains(null, msgs);
			case JsonSchemaPackage.SCHEMA__PROPERTIES:
				return ((InternalEList<?>)getProperties()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__PATTERN_PROPERTIES:
				return ((InternalEList<?>)getPatternProperties()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_PROPERTIES:
				return basicSetAdditionalProperties(null, msgs);
			case JsonSchemaPackage.SCHEMA__PROPERTY_NAMES:
				return basicSetPropertyNames(null, msgs);
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_ITEMS:
				return basicSetUnevaluatedItems(null, msgs);
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_PROPERTIES:
				return basicSetUnevaluatedProperties(null, msgs);
			case JsonSchemaPackage.SCHEMA__CONTENT_SCHEMA:
				return basicSetContentSchema(null, msgs);
			case JsonSchemaPackage.SCHEMA__DISCRIMINATOR:
				return basicSetDiscriminator(null, msgs);
			case JsonSchemaPackage.SCHEMA__XML:
				return basicSetXml(null, msgs);
			case JsonSchemaPackage.SCHEMA__EXTERNAL_DOCS:
				return basicSetExternalDocs(null, msgs);
			case JsonSchemaPackage.SCHEMA__KEYWORDS:
				return ((InternalEList<?>)getKeywords()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case JsonSchemaPackage.SCHEMA__BOOLEAN_SCHEMA:
				return getBooleanSchema();
			case JsonSchemaPackage.SCHEMA__SCHEMA:
				return getSchema();
			case JsonSchemaPackage.SCHEMA__ID:
				return getId();
			case JsonSchemaPackage.SCHEMA__LEGACY_ID:
				return getLegacyId();
			case JsonSchemaPackage.SCHEMA__REF:
				return getRef();
			case JsonSchemaPackage.SCHEMA__ANCHOR:
				return getAnchor();
			case JsonSchemaPackage.SCHEMA__DYNAMIC_REF:
				return getDynamicRef();
			case JsonSchemaPackage.SCHEMA__DYNAMIC_ANCHOR:
				return getDynamicAnchor();
			case JsonSchemaPackage.SCHEMA__RECURSIVE_REF:
				return getRecursiveRef();
			case JsonSchemaPackage.SCHEMA__RECURSIVE_ANCHOR:
				return getRecursiveAnchor();
			case JsonSchemaPackage.SCHEMA__VOCABULARY:
				if (coreType) return getVocabulary();
				else return getVocabulary().map();
			case JsonSchemaPackage.SCHEMA__COMMENT:
				return getComment();
			case JsonSchemaPackage.SCHEMA__DEFS:
				if (coreType) return getDefs();
				else return getDefs().map();
			case JsonSchemaPackage.SCHEMA__DEFINITIONS:
				if (coreType) return getDefinitions();
				else return getDefinitions().map();
			case JsonSchemaPackage.SCHEMA__TITLE:
				return getTitle();
			case JsonSchemaPackage.SCHEMA__DESCRIPTION:
				return getDescription();
			case JsonSchemaPackage.SCHEMA__DEFAULT:
				return getDefault();
			case JsonSchemaPackage.SCHEMA__DEPRECATED:
				return getDeprecated();
			case JsonSchemaPackage.SCHEMA__READ_ONLY:
				return getReadOnly();
			case JsonSchemaPackage.SCHEMA__WRITE_ONLY:
				return getWriteOnly();
			case JsonSchemaPackage.SCHEMA__EXAMPLES:
				return getExamples();
			case JsonSchemaPackage.SCHEMA__TYPE:
				return getType();
			case JsonSchemaPackage.SCHEMA__TYPE_AS_LIST:
				return isTypeAsList();
			case JsonSchemaPackage.SCHEMA__ENUM:
				return getEnum();
			case JsonSchemaPackage.SCHEMA__CONST:
				return getConst();
			case JsonSchemaPackage.SCHEMA__MULTIPLE_OF:
				return getMultipleOf();
			case JsonSchemaPackage.SCHEMA__MAXIMUM:
				return getMaximum();
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM:
				return getExclusiveMaximum();
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM_FLAG:
				return getExclusiveMaximumFlag();
			case JsonSchemaPackage.SCHEMA__MINIMUM:
				return getMinimum();
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM:
				return getExclusiveMinimum();
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM_FLAG:
				return getExclusiveMinimumFlag();
			case JsonSchemaPackage.SCHEMA__MAX_LENGTH:
				return getMaxLength();
			case JsonSchemaPackage.SCHEMA__MIN_LENGTH:
				return getMinLength();
			case JsonSchemaPackage.SCHEMA__PATTERN:
				return getPattern();
			case JsonSchemaPackage.SCHEMA__MAX_ITEMS:
				return getMaxItems();
			case JsonSchemaPackage.SCHEMA__MIN_ITEMS:
				return getMinItems();
			case JsonSchemaPackage.SCHEMA__UNIQUE_ITEMS:
				return getUniqueItems();
			case JsonSchemaPackage.SCHEMA__MAX_CONTAINS:
				return getMaxContains();
			case JsonSchemaPackage.SCHEMA__MIN_CONTAINS:
				return getMinContains();
			case JsonSchemaPackage.SCHEMA__MAX_PROPERTIES:
				return getMaxProperties();
			case JsonSchemaPackage.SCHEMA__MIN_PROPERTIES:
				return getMinProperties();
			case JsonSchemaPackage.SCHEMA__REQUIRED:
				return getRequired();
			case JsonSchemaPackage.SCHEMA__DEPENDENT_REQUIRED:
				if (coreType) return getDependentRequired();
				else return getDependentRequired().map();
			case JsonSchemaPackage.SCHEMA__FORMAT:
				return getFormat();
			case JsonSchemaPackage.SCHEMA__ALL_OF:
				return getAllOf();
			case JsonSchemaPackage.SCHEMA__ANY_OF:
				return getAnyOf();
			case JsonSchemaPackage.SCHEMA__ONE_OF:
				return getOneOf();
			case JsonSchemaPackage.SCHEMA__NOT:
				return getNot();
			case JsonSchemaPackage.SCHEMA__IF:
				return getIf();
			case JsonSchemaPackage.SCHEMA__THEN:
				return getThen();
			case JsonSchemaPackage.SCHEMA__ELSE:
				return getElse();
			case JsonSchemaPackage.SCHEMA__DEPENDENT_SCHEMAS:
				if (coreType) return getDependentSchemas();
				else return getDependentSchemas().map();
			case JsonSchemaPackage.SCHEMA__DEPENDENCIES:
				if (coreType) return getDependencies();
				else return getDependencies().map();
			case JsonSchemaPackage.SCHEMA__PREFIX_ITEMS:
				return getPrefixItems();
			case JsonSchemaPackage.SCHEMA__ITEMS:
				return getItems();
			case JsonSchemaPackage.SCHEMA__ITEMS_LIST:
				return getItemsList();
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_ITEMS:
				return getAdditionalItems();
			case JsonSchemaPackage.SCHEMA__CONTAINS:
				return getContains();
			case JsonSchemaPackage.SCHEMA__PROPERTIES:
				if (coreType) return getProperties();
				else return getProperties().map();
			case JsonSchemaPackage.SCHEMA__PATTERN_PROPERTIES:
				if (coreType) return getPatternProperties();
				else return getPatternProperties().map();
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_PROPERTIES:
				return getAdditionalProperties();
			case JsonSchemaPackage.SCHEMA__PROPERTY_NAMES:
				return getPropertyNames();
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_ITEMS:
				return getUnevaluatedItems();
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_PROPERTIES:
				return getUnevaluatedProperties();
			case JsonSchemaPackage.SCHEMA__CONTENT_ENCODING:
				return getContentEncoding();
			case JsonSchemaPackage.SCHEMA__CONTENT_MEDIA_TYPE:
				return getContentMediaType();
			case JsonSchemaPackage.SCHEMA__CONTENT_SCHEMA:
				return getContentSchema();
			case JsonSchemaPackage.SCHEMA__NULLABLE:
				return getNullable();
			case JsonSchemaPackage.SCHEMA__DISCRIMINATOR:
				return getDiscriminator();
			case JsonSchemaPackage.SCHEMA__XML:
				return getXml();
			case JsonSchemaPackage.SCHEMA__EXTERNAL_DOCS:
				return getExternalDocs();
			case JsonSchemaPackage.SCHEMA__EXAMPLE:
				return getExample();
			case JsonSchemaPackage.SCHEMA__KEYWORDS:
				if (coreType) return getKeywords();
				else return getKeywords().map();
			case JsonSchemaPackage.SCHEMA__CLASSIFIER:
				if (resolve) return getClassifier();
				return basicGetClassifier();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case JsonSchemaPackage.SCHEMA__BOOLEAN_SCHEMA:
				setBooleanSchema((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__SCHEMA:
				setSchema((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ID:
				setId((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__LEGACY_ID:
				setLegacyId((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__REF:
				setRef((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ANCHOR:
				setAnchor((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DYNAMIC_REF:
				setDynamicRef((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DYNAMIC_ANCHOR:
				setDynamicAnchor((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__RECURSIVE_REF:
				setRecursiveRef((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__RECURSIVE_ANCHOR:
				setRecursiveAnchor((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__VOCABULARY:
				((EStructuralFeature.Setting)getVocabulary()).set(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__COMMENT:
				setComment((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DEFS:
				((EStructuralFeature.Setting)getDefs()).set(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DEFINITIONS:
				((EStructuralFeature.Setting)getDefinitions()).set(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__TITLE:
				setTitle((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DEFAULT:
				setDefault(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DEPRECATED:
				setDeprecated((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__READ_ONLY:
				setReadOnly((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__WRITE_ONLY:
				setWriteOnly((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__EXAMPLES:
				getExamples().clear();
				getExamples().addAll((Collection<? extends Object>)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__TYPE:
				getType().clear();
				getType().addAll((Collection<? extends SimpleType>)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__TYPE_AS_LIST:
				setTypeAsList((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ENUM:
				getEnum().clear();
				getEnum().addAll((Collection<? extends Object>)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__CONST:
				setConst(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MULTIPLE_OF:
				setMultipleOf((BigDecimal)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MAXIMUM:
				setMaximum((BigDecimal)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM:
				setExclusiveMaximum((BigDecimal)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM_FLAG:
				setExclusiveMaximumFlag((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MINIMUM:
				setMinimum((BigDecimal)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM:
				setExclusiveMinimum((BigDecimal)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM_FLAG:
				setExclusiveMinimumFlag((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MAX_LENGTH:
				setMaxLength((Integer)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MIN_LENGTH:
				setMinLength((Integer)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__PATTERN:
				setPattern((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MAX_ITEMS:
				setMaxItems((Integer)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MIN_ITEMS:
				setMinItems((Integer)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__UNIQUE_ITEMS:
				setUniqueItems((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MAX_CONTAINS:
				setMaxContains((Integer)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MIN_CONTAINS:
				setMinContains((Integer)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MAX_PROPERTIES:
				setMaxProperties((Integer)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__MIN_PROPERTIES:
				setMinProperties((Integer)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__REQUIRED:
				getRequired().clear();
				getRequired().addAll((Collection<? extends String>)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DEPENDENT_REQUIRED:
				((EStructuralFeature.Setting)getDependentRequired()).set(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__FORMAT:
				setFormat((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ALL_OF:
				getAllOf().clear();
				getAllOf().addAll((Collection<? extends Schema>)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ANY_OF:
				getAnyOf().clear();
				getAnyOf().addAll((Collection<? extends Schema>)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ONE_OF:
				getOneOf().clear();
				getOneOf().addAll((Collection<? extends Schema>)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__NOT:
				setNot((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__IF:
				setIf((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__THEN:
				setThen((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ELSE:
				setElse((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DEPENDENT_SCHEMAS:
				((EStructuralFeature.Setting)getDependentSchemas()).set(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DEPENDENCIES:
				((EStructuralFeature.Setting)getDependencies()).set(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__PREFIX_ITEMS:
				getPrefixItems().clear();
				getPrefixItems().addAll((Collection<? extends Schema>)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ITEMS:
				setItems((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ITEMS_LIST:
				getItemsList().clear();
				getItemsList().addAll((Collection<? extends Schema>)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_ITEMS:
				setAdditionalItems((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__CONTAINS:
				setContains((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__PROPERTIES:
				((EStructuralFeature.Setting)getProperties()).set(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__PATTERN_PROPERTIES:
				((EStructuralFeature.Setting)getPatternProperties()).set(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_PROPERTIES:
				setAdditionalProperties((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__PROPERTY_NAMES:
				setPropertyNames((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_ITEMS:
				setUnevaluatedItems((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_PROPERTIES:
				setUnevaluatedProperties((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__CONTENT_ENCODING:
				setContentEncoding((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__CONTENT_MEDIA_TYPE:
				setContentMediaType((String)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__CONTENT_SCHEMA:
				setContentSchema((Schema)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__NULLABLE:
				setNullable((Boolean)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__DISCRIMINATOR:
				setDiscriminator((Discriminator)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__XML:
				setXml((Xml)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__EXTERNAL_DOCS:
				setExternalDocs((ExternalDocumentation)newValue);
				return;
			case JsonSchemaPackage.SCHEMA__EXAMPLE:
				setExample(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__KEYWORDS:
				((EStructuralFeature.Setting)getKeywords()).set(newValue);
				return;
			case JsonSchemaPackage.SCHEMA__CLASSIFIER:
				setClassifier((EClassifier)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case JsonSchemaPackage.SCHEMA__BOOLEAN_SCHEMA:
				unsetBooleanSchema();
				return;
			case JsonSchemaPackage.SCHEMA__SCHEMA:
				setSchema(SCHEMA_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__ID:
				setId(ID_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__LEGACY_ID:
				setLegacyId(LEGACY_ID_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__REF:
				setRef(REF_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__ANCHOR:
				setAnchor(ANCHOR_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__DYNAMIC_REF:
				setDynamicRef(DYNAMIC_REF_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__DYNAMIC_ANCHOR:
				setDynamicAnchor(DYNAMIC_ANCHOR_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__RECURSIVE_REF:
				setRecursiveRef(RECURSIVE_REF_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__RECURSIVE_ANCHOR:
				unsetRecursiveAnchor();
				return;
			case JsonSchemaPackage.SCHEMA__VOCABULARY:
				unsetVocabulary();
				return;
			case JsonSchemaPackage.SCHEMA__COMMENT:
				setComment(COMMENT_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__DEFS:
				unsetDefs();
				return;
			case JsonSchemaPackage.SCHEMA__DEFINITIONS:
				unsetDefinitions();
				return;
			case JsonSchemaPackage.SCHEMA__TITLE:
				setTitle(TITLE_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__DEFAULT:
				unsetDefault();
				return;
			case JsonSchemaPackage.SCHEMA__DEPRECATED:
				unsetDeprecated();
				return;
			case JsonSchemaPackage.SCHEMA__READ_ONLY:
				unsetReadOnly();
				return;
			case JsonSchemaPackage.SCHEMA__WRITE_ONLY:
				unsetWriteOnly();
				return;
			case JsonSchemaPackage.SCHEMA__EXAMPLES:
				unsetExamples();
				return;
			case JsonSchemaPackage.SCHEMA__TYPE:
				unsetType();
				return;
			case JsonSchemaPackage.SCHEMA__TYPE_AS_LIST:
				setTypeAsList(TYPE_AS_LIST_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__ENUM:
				unsetEnum();
				return;
			case JsonSchemaPackage.SCHEMA__CONST:
				unsetConst();
				return;
			case JsonSchemaPackage.SCHEMA__MULTIPLE_OF:
				unsetMultipleOf();
				return;
			case JsonSchemaPackage.SCHEMA__MAXIMUM:
				unsetMaximum();
				return;
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM:
				unsetExclusiveMaximum();
				return;
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM_FLAG:
				unsetExclusiveMaximumFlag();
				return;
			case JsonSchemaPackage.SCHEMA__MINIMUM:
				unsetMinimum();
				return;
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM:
				unsetExclusiveMinimum();
				return;
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM_FLAG:
				unsetExclusiveMinimumFlag();
				return;
			case JsonSchemaPackage.SCHEMA__MAX_LENGTH:
				unsetMaxLength();
				return;
			case JsonSchemaPackage.SCHEMA__MIN_LENGTH:
				unsetMinLength();
				return;
			case JsonSchemaPackage.SCHEMA__PATTERN:
				setPattern(PATTERN_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__MAX_ITEMS:
				unsetMaxItems();
				return;
			case JsonSchemaPackage.SCHEMA__MIN_ITEMS:
				unsetMinItems();
				return;
			case JsonSchemaPackage.SCHEMA__UNIQUE_ITEMS:
				unsetUniqueItems();
				return;
			case JsonSchemaPackage.SCHEMA__MAX_CONTAINS:
				unsetMaxContains();
				return;
			case JsonSchemaPackage.SCHEMA__MIN_CONTAINS:
				unsetMinContains();
				return;
			case JsonSchemaPackage.SCHEMA__MAX_PROPERTIES:
				unsetMaxProperties();
				return;
			case JsonSchemaPackage.SCHEMA__MIN_PROPERTIES:
				unsetMinProperties();
				return;
			case JsonSchemaPackage.SCHEMA__REQUIRED:
				unsetRequired();
				return;
			case JsonSchemaPackage.SCHEMA__DEPENDENT_REQUIRED:
				unsetDependentRequired();
				return;
			case JsonSchemaPackage.SCHEMA__FORMAT:
				setFormat(FORMAT_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__ALL_OF:
				unsetAllOf();
				return;
			case JsonSchemaPackage.SCHEMA__ANY_OF:
				unsetAnyOf();
				return;
			case JsonSchemaPackage.SCHEMA__ONE_OF:
				unsetOneOf();
				return;
			case JsonSchemaPackage.SCHEMA__NOT:
				setNot((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__IF:
				setIf((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__THEN:
				setThen((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__ELSE:
				setElse((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__DEPENDENT_SCHEMAS:
				unsetDependentSchemas();
				return;
			case JsonSchemaPackage.SCHEMA__DEPENDENCIES:
				unsetDependencies();
				return;
			case JsonSchemaPackage.SCHEMA__PREFIX_ITEMS:
				unsetPrefixItems();
				return;
			case JsonSchemaPackage.SCHEMA__ITEMS:
				setItems((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__ITEMS_LIST:
				unsetItemsList();
				return;
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_ITEMS:
				setAdditionalItems((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__CONTAINS:
				setContains((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__PROPERTIES:
				unsetProperties();
				return;
			case JsonSchemaPackage.SCHEMA__PATTERN_PROPERTIES:
				unsetPatternProperties();
				return;
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_PROPERTIES:
				setAdditionalProperties((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__PROPERTY_NAMES:
				setPropertyNames((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_ITEMS:
				setUnevaluatedItems((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_PROPERTIES:
				setUnevaluatedProperties((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__CONTENT_ENCODING:
				setContentEncoding(CONTENT_ENCODING_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__CONTENT_MEDIA_TYPE:
				setContentMediaType(CONTENT_MEDIA_TYPE_EDEFAULT);
				return;
			case JsonSchemaPackage.SCHEMA__CONTENT_SCHEMA:
				setContentSchema((Schema)null);
				return;
			case JsonSchemaPackage.SCHEMA__NULLABLE:
				unsetNullable();
				return;
			case JsonSchemaPackage.SCHEMA__DISCRIMINATOR:
				setDiscriminator((Discriminator)null);
				return;
			case JsonSchemaPackage.SCHEMA__XML:
				setXml((Xml)null);
				return;
			case JsonSchemaPackage.SCHEMA__EXTERNAL_DOCS:
				setExternalDocs((ExternalDocumentation)null);
				return;
			case JsonSchemaPackage.SCHEMA__EXAMPLE:
				unsetExample();
				return;
			case JsonSchemaPackage.SCHEMA__KEYWORDS:
				unsetKeywords();
				return;
			case JsonSchemaPackage.SCHEMA__CLASSIFIER:
				setClassifier((EClassifier)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case JsonSchemaPackage.SCHEMA__BOOLEAN_SCHEMA:
				return isSetBooleanSchema();
			case JsonSchemaPackage.SCHEMA__SCHEMA:
				return SCHEMA_EDEFAULT == null ? schema != null : !SCHEMA_EDEFAULT.equals(schema);
			case JsonSchemaPackage.SCHEMA__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case JsonSchemaPackage.SCHEMA__LEGACY_ID:
				return LEGACY_ID_EDEFAULT == null ? legacyId != null : !LEGACY_ID_EDEFAULT.equals(legacyId);
			case JsonSchemaPackage.SCHEMA__REF:
				return REF_EDEFAULT == null ? ref != null : !REF_EDEFAULT.equals(ref);
			case JsonSchemaPackage.SCHEMA__ANCHOR:
				return ANCHOR_EDEFAULT == null ? anchor != null : !ANCHOR_EDEFAULT.equals(anchor);
			case JsonSchemaPackage.SCHEMA__DYNAMIC_REF:
				return DYNAMIC_REF_EDEFAULT == null ? dynamicRef != null : !DYNAMIC_REF_EDEFAULT.equals(dynamicRef);
			case JsonSchemaPackage.SCHEMA__DYNAMIC_ANCHOR:
				return DYNAMIC_ANCHOR_EDEFAULT == null ? dynamicAnchor != null : !DYNAMIC_ANCHOR_EDEFAULT.equals(dynamicAnchor);
			case JsonSchemaPackage.SCHEMA__RECURSIVE_REF:
				return RECURSIVE_REF_EDEFAULT == null ? recursiveRef != null : !RECURSIVE_REF_EDEFAULT.equals(recursiveRef);
			case JsonSchemaPackage.SCHEMA__RECURSIVE_ANCHOR:
				return isSetRecursiveAnchor();
			case JsonSchemaPackage.SCHEMA__VOCABULARY:
				return isSetVocabulary();
			case JsonSchemaPackage.SCHEMA__COMMENT:
				return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
			case JsonSchemaPackage.SCHEMA__DEFS:
				return isSetDefs();
			case JsonSchemaPackage.SCHEMA__DEFINITIONS:
				return isSetDefinitions();
			case JsonSchemaPackage.SCHEMA__TITLE:
				return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
			case JsonSchemaPackage.SCHEMA__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case JsonSchemaPackage.SCHEMA__DEFAULT:
				return isSetDefault();
			case JsonSchemaPackage.SCHEMA__DEPRECATED:
				return isSetDeprecated();
			case JsonSchemaPackage.SCHEMA__READ_ONLY:
				return isSetReadOnly();
			case JsonSchemaPackage.SCHEMA__WRITE_ONLY:
				return isSetWriteOnly();
			case JsonSchemaPackage.SCHEMA__EXAMPLES:
				return isSetExamples();
			case JsonSchemaPackage.SCHEMA__TYPE:
				return isSetType();
			case JsonSchemaPackage.SCHEMA__TYPE_AS_LIST:
				return typeAsList != TYPE_AS_LIST_EDEFAULT;
			case JsonSchemaPackage.SCHEMA__ENUM:
				return isSetEnum();
			case JsonSchemaPackage.SCHEMA__CONST:
				return isSetConst();
			case JsonSchemaPackage.SCHEMA__MULTIPLE_OF:
				return isSetMultipleOf();
			case JsonSchemaPackage.SCHEMA__MAXIMUM:
				return isSetMaximum();
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM:
				return isSetExclusiveMaximum();
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MAXIMUM_FLAG:
				return isSetExclusiveMaximumFlag();
			case JsonSchemaPackage.SCHEMA__MINIMUM:
				return isSetMinimum();
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM:
				return isSetExclusiveMinimum();
			case JsonSchemaPackage.SCHEMA__EXCLUSIVE_MINIMUM_FLAG:
				return isSetExclusiveMinimumFlag();
			case JsonSchemaPackage.SCHEMA__MAX_LENGTH:
				return isSetMaxLength();
			case JsonSchemaPackage.SCHEMA__MIN_LENGTH:
				return isSetMinLength();
			case JsonSchemaPackage.SCHEMA__PATTERN:
				return PATTERN_EDEFAULT == null ? pattern != null : !PATTERN_EDEFAULT.equals(pattern);
			case JsonSchemaPackage.SCHEMA__MAX_ITEMS:
				return isSetMaxItems();
			case JsonSchemaPackage.SCHEMA__MIN_ITEMS:
				return isSetMinItems();
			case JsonSchemaPackage.SCHEMA__UNIQUE_ITEMS:
				return isSetUniqueItems();
			case JsonSchemaPackage.SCHEMA__MAX_CONTAINS:
				return isSetMaxContains();
			case JsonSchemaPackage.SCHEMA__MIN_CONTAINS:
				return isSetMinContains();
			case JsonSchemaPackage.SCHEMA__MAX_PROPERTIES:
				return isSetMaxProperties();
			case JsonSchemaPackage.SCHEMA__MIN_PROPERTIES:
				return isSetMinProperties();
			case JsonSchemaPackage.SCHEMA__REQUIRED:
				return isSetRequired();
			case JsonSchemaPackage.SCHEMA__DEPENDENT_REQUIRED:
				return isSetDependentRequired();
			case JsonSchemaPackage.SCHEMA__FORMAT:
				return FORMAT_EDEFAULT == null ? format != null : !FORMAT_EDEFAULT.equals(format);
			case JsonSchemaPackage.SCHEMA__ALL_OF:
				return isSetAllOf();
			case JsonSchemaPackage.SCHEMA__ANY_OF:
				return isSetAnyOf();
			case JsonSchemaPackage.SCHEMA__ONE_OF:
				return isSetOneOf();
			case JsonSchemaPackage.SCHEMA__NOT:
				return not != null;
			case JsonSchemaPackage.SCHEMA__IF:
				return if_ != null;
			case JsonSchemaPackage.SCHEMA__THEN:
				return then != null;
			case JsonSchemaPackage.SCHEMA__ELSE:
				return else_ != null;
			case JsonSchemaPackage.SCHEMA__DEPENDENT_SCHEMAS:
				return isSetDependentSchemas();
			case JsonSchemaPackage.SCHEMA__DEPENDENCIES:
				return isSetDependencies();
			case JsonSchemaPackage.SCHEMA__PREFIX_ITEMS:
				return isSetPrefixItems();
			case JsonSchemaPackage.SCHEMA__ITEMS:
				return items != null;
			case JsonSchemaPackage.SCHEMA__ITEMS_LIST:
				return isSetItemsList();
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_ITEMS:
				return additionalItems != null;
			case JsonSchemaPackage.SCHEMA__CONTAINS:
				return contains != null;
			case JsonSchemaPackage.SCHEMA__PROPERTIES:
				return isSetProperties();
			case JsonSchemaPackage.SCHEMA__PATTERN_PROPERTIES:
				return isSetPatternProperties();
			case JsonSchemaPackage.SCHEMA__ADDITIONAL_PROPERTIES:
				return additionalProperties != null;
			case JsonSchemaPackage.SCHEMA__PROPERTY_NAMES:
				return propertyNames != null;
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_ITEMS:
				return unevaluatedItems != null;
			case JsonSchemaPackage.SCHEMA__UNEVALUATED_PROPERTIES:
				return unevaluatedProperties != null;
			case JsonSchemaPackage.SCHEMA__CONTENT_ENCODING:
				return CONTENT_ENCODING_EDEFAULT == null ? contentEncoding != null : !CONTENT_ENCODING_EDEFAULT.equals(contentEncoding);
			case JsonSchemaPackage.SCHEMA__CONTENT_MEDIA_TYPE:
				return CONTENT_MEDIA_TYPE_EDEFAULT == null ? contentMediaType != null : !CONTENT_MEDIA_TYPE_EDEFAULT.equals(contentMediaType);
			case JsonSchemaPackage.SCHEMA__CONTENT_SCHEMA:
				return contentSchema != null;
			case JsonSchemaPackage.SCHEMA__NULLABLE:
				return isSetNullable();
			case JsonSchemaPackage.SCHEMA__DISCRIMINATOR:
				return discriminator != null;
			case JsonSchemaPackage.SCHEMA__XML:
				return xml != null;
			case JsonSchemaPackage.SCHEMA__EXTERNAL_DOCS:
				return externalDocs != null;
			case JsonSchemaPackage.SCHEMA__EXAMPLE:
				return isSetExample();
			case JsonSchemaPackage.SCHEMA__KEYWORDS:
				return isSetKeywords();
			case JsonSchemaPackage.SCHEMA__CLASSIFIER:
				return classifier != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (booleanSchema: ");
		if (booleanSchemaESet) result.append(booleanSchema); else result.append("<unset>");
		result.append(", schema: ");
		result.append(schema);
		result.append(", id: ");
		result.append(id);
		result.append(", legacyId: ");
		result.append(legacyId);
		result.append(", ref: ");
		result.append(ref);
		result.append(", anchor: ");
		result.append(anchor);
		result.append(", dynamicRef: ");
		result.append(dynamicRef);
		result.append(", dynamicAnchor: ");
		result.append(dynamicAnchor);
		result.append(", recursiveRef: ");
		result.append(recursiveRef);
		result.append(", recursiveAnchor: ");
		if (recursiveAnchorESet) result.append(recursiveAnchor); else result.append("<unset>");
		result.append(", comment: ");
		result.append(comment);
		result.append(", title: ");
		result.append(title);
		result.append(", description: ");
		result.append(description);
		result.append(", default: ");
		if (defaultESet) result.append(default_); else result.append("<unset>");
		result.append(", deprecated: ");
		if (deprecatedESet) result.append(deprecated); else result.append("<unset>");
		result.append(", readOnly: ");
		if (readOnlyESet) result.append(readOnly); else result.append("<unset>");
		result.append(", writeOnly: ");
		if (writeOnlyESet) result.append(writeOnly); else result.append("<unset>");
		result.append(", examples: ");
		result.append(examples);
		result.append(", type: ");
		result.append(type);
		result.append(", typeAsList: ");
		result.append(typeAsList);
		result.append(", enum: ");
		result.append(enum_);
		result.append(", const: ");
		if (constESet) result.append(const_); else result.append("<unset>");
		result.append(", multipleOf: ");
		if (multipleOfESet) result.append(multipleOf); else result.append("<unset>");
		result.append(", maximum: ");
		if (maximumESet) result.append(maximum); else result.append("<unset>");
		result.append(", exclusiveMaximum: ");
		if (exclusiveMaximumESet) result.append(exclusiveMaximum); else result.append("<unset>");
		result.append(", exclusiveMaximumFlag: ");
		if (exclusiveMaximumFlagESet) result.append(exclusiveMaximumFlag); else result.append("<unset>");
		result.append(", minimum: ");
		if (minimumESet) result.append(minimum); else result.append("<unset>");
		result.append(", exclusiveMinimum: ");
		if (exclusiveMinimumESet) result.append(exclusiveMinimum); else result.append("<unset>");
		result.append(", exclusiveMinimumFlag: ");
		if (exclusiveMinimumFlagESet) result.append(exclusiveMinimumFlag); else result.append("<unset>");
		result.append(", maxLength: ");
		if (maxLengthESet) result.append(maxLength); else result.append("<unset>");
		result.append(", minLength: ");
		if (minLengthESet) result.append(minLength); else result.append("<unset>");
		result.append(", pattern: ");
		result.append(pattern);
		result.append(", maxItems: ");
		if (maxItemsESet) result.append(maxItems); else result.append("<unset>");
		result.append(", minItems: ");
		if (minItemsESet) result.append(minItems); else result.append("<unset>");
		result.append(", uniqueItems: ");
		if (uniqueItemsESet) result.append(uniqueItems); else result.append("<unset>");
		result.append(", maxContains: ");
		if (maxContainsESet) result.append(maxContains); else result.append("<unset>");
		result.append(", minContains: ");
		if (minContainsESet) result.append(minContains); else result.append("<unset>");
		result.append(", maxProperties: ");
		if (maxPropertiesESet) result.append(maxProperties); else result.append("<unset>");
		result.append(", minProperties: ");
		if (minPropertiesESet) result.append(minProperties); else result.append("<unset>");
		result.append(", required: ");
		result.append(required);
		result.append(", format: ");
		result.append(format);
		result.append(", contentEncoding: ");
		result.append(contentEncoding);
		result.append(", contentMediaType: ");
		result.append(contentMediaType);
		result.append(", nullable: ");
		if (nullableESet) result.append(nullable); else result.append("<unset>");
		result.append(", example: ");
		if (exampleESet) result.append(example); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //SchemaImpl
