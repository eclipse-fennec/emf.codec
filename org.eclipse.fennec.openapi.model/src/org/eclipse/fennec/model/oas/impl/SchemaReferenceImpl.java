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
package org.eclipse.fennec.model.oas.impl;

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

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.SchemaReference;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Schema Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getClassifier <em>Classifier</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#isMany <em>Many</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getMinItems <em>Min Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getMaxItems <em>Max Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#isUniqueItems <em>Unique Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#isNullable <em>Nullable</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getDefault <em>Default</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getEnum <em>Enum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getConst <em>Const</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getMinLength <em>Min Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getMaxLength <em>Max Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getPattern <em>Pattern</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getMinimum <em>Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getMaximum <em>Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getExclusiveMinimum <em>Exclusive Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getExclusiveMaximum <em>Exclusive Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getMultipleOf <em>Multiple Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#isReadOnly <em>Read Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#isWriteOnly <em>Write Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getContentMediaType <em>Content Media Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getContentEncoding <em>Content Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SchemaReferenceImpl#getKeywords <em>Keywords</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SchemaReferenceImpl extends MinimalEObjectImpl.Container implements SchemaReference {
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
	 * The cached value of the '{@link #getClassifier() <em>Classifier</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClassifier()
	 * @generated
	 * @ordered
	 */
	protected EClassifier classifier;

	/**
	 * The default value of the '{@link #isMany() <em>Many</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMany()
	 * @generated
	 * @ordered
	 */
	protected static final boolean MANY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isMany() <em>Many</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMany()
	 * @generated
	 * @ordered
	 */
	protected boolean many = MANY_EDEFAULT;

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
	 * The default value of the '{@link #isUniqueItems() <em>Unique Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUniqueItems()
	 * @generated
	 * @ordered
	 */
	protected static final boolean UNIQUE_ITEMS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isUniqueItems() <em>Unique Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUniqueItems()
	 * @generated
	 * @ordered
	 */
	protected boolean uniqueItems = UNIQUE_ITEMS_EDEFAULT;

	/**
	 * The default value of the '{@link #isNullable() <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNullable()
	 * @generated
	 * @ordered
	 */
	protected static final boolean NULLABLE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isNullable() <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNullable()
	 * @generated
	 * @ordered
	 */
	protected boolean nullable = NULLABLE_EDEFAULT;

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
	 * The default value of the '{@link #isReadOnly() <em>Read Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isReadOnly()
	 * @generated
	 * @ordered
	 */
	protected static final boolean READ_ONLY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isReadOnly() <em>Read Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isReadOnly()
	 * @generated
	 * @ordered
	 */
	protected boolean readOnly = READ_ONLY_EDEFAULT;

	/**
	 * The default value of the '{@link #isWriteOnly() <em>Write Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isWriteOnly()
	 * @generated
	 * @ordered
	 */
	protected static final boolean WRITE_ONLY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isWriteOnly() <em>Write Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isWriteOnly()
	 * @generated
	 * @ordered
	 */
	protected boolean writeOnly = WRITE_ONLY_EDEFAULT;

	/**
	 * The default value of the '{@link #isDeprecated() <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDeprecated()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DEPRECATED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDeprecated() <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDeprecated()
	 * @generated
	 * @ordered
	 */
	protected boolean deprecated = DEPRECATED_EDEFAULT;

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
	 * The cached value of the '{@link #getExamples() <em>Examples</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExamples()
	 * @generated
	 * @ordered
	 */
	protected EList<Object> examples;

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
	 * The cached value of the '{@link #getKeywords() <em>Keywords</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKeywords()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Object> keywords;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SchemaReferenceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OasPackage.Literals.SCHEMA_REFERENCE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__REF, oldRef, ref));
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, OasPackage.SCHEMA_REFERENCE__CLASSIFIER, oldClassifier, classifier));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__CLASSIFIER, oldClassifier, classifier));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isMany() {
		return many;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMany(boolean newMany) {
		boolean oldMany = many;
		many = newMany;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__MANY, oldMany, many));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__MIN_ITEMS, oldMinItems, minItems));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__MAX_ITEMS, oldMaxItems, maxItems));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isUniqueItems() {
		return uniqueItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUniqueItems(boolean newUniqueItems) {
		boolean oldUniqueItems = uniqueItems;
		uniqueItems = newUniqueItems;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__UNIQUE_ITEMS, oldUniqueItems, uniqueItems));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNullable(boolean newNullable) {
		boolean oldNullable = nullable;
		nullable = newNullable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__NULLABLE, oldNullable, nullable));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__FORMAT, oldFormat, format));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__TITLE, oldTitle, title));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__DESCRIPTION, oldDescription, description));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__DEFAULT, oldDefault, default_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Object> getEnum() {
		if (enum_ == null) {
			enum_ = new EDataTypeUniqueEList<Object>(Object.class, this, OasPackage.SCHEMA_REFERENCE__ENUM);
		}
		return enum_;
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__CONST, oldConst, const_));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__MIN_LENGTH, oldMinLength, minLength));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__MAX_LENGTH, oldMaxLength, maxLength));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__PATTERN, oldPattern, pattern));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__MINIMUM, oldMinimum, minimum));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__MAXIMUM, oldMaximum, maximum));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MINIMUM, oldExclusiveMinimum, exclusiveMinimum));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MAXIMUM, oldExclusiveMaximum, exclusiveMaximum));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__MULTIPLE_OF, oldMultipleOf, multipleOf));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setReadOnly(boolean newReadOnly) {
		boolean oldReadOnly = readOnly;
		readOnly = newReadOnly;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__READ_ONLY, oldReadOnly, readOnly));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isWriteOnly() {
		return writeOnly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWriteOnly(boolean newWriteOnly) {
		boolean oldWriteOnly = writeOnly;
		writeOnly = newWriteOnly;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__WRITE_ONLY, oldWriteOnly, writeOnly));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isDeprecated() {
		return deprecated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeprecated(boolean newDeprecated) {
		boolean oldDeprecated = deprecated;
		deprecated = newDeprecated;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__DEPRECATED, oldDeprecated, deprecated));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__EXAMPLE, oldExample, example));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Object> getExamples() {
		if (examples == null) {
			examples = new EDataTypeUniqueEList<Object>(Object.class, this, OasPackage.SCHEMA_REFERENCE__EXAMPLES);
		}
		return examples;
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__CONTENT_MEDIA_TYPE, oldContentMediaType, contentMediaType));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SCHEMA_REFERENCE__CONTENT_ENCODING, oldContentEncoding, contentEncoding));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getKeywords() {
		if (keywords == null) {
			keywords = new EcoreEMap<String,Object>(OasPackage.Literals.ANY_ENTRY, AnyEntryImpl.class, this, OasPackage.SCHEMA_REFERENCE__KEYWORDS);
		}
		return keywords;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OasPackage.SCHEMA_REFERENCE__KEYWORDS:
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
			case OasPackage.SCHEMA_REFERENCE__REF:
				return getRef();
			case OasPackage.SCHEMA_REFERENCE__CLASSIFIER:
				if (resolve) return getClassifier();
				return basicGetClassifier();
			case OasPackage.SCHEMA_REFERENCE__MANY:
				return isMany();
			case OasPackage.SCHEMA_REFERENCE__MIN_ITEMS:
				return getMinItems();
			case OasPackage.SCHEMA_REFERENCE__MAX_ITEMS:
				return getMaxItems();
			case OasPackage.SCHEMA_REFERENCE__UNIQUE_ITEMS:
				return isUniqueItems();
			case OasPackage.SCHEMA_REFERENCE__NULLABLE:
				return isNullable();
			case OasPackage.SCHEMA_REFERENCE__FORMAT:
				return getFormat();
			case OasPackage.SCHEMA_REFERENCE__TITLE:
				return getTitle();
			case OasPackage.SCHEMA_REFERENCE__DESCRIPTION:
				return getDescription();
			case OasPackage.SCHEMA_REFERENCE__DEFAULT:
				return getDefault();
			case OasPackage.SCHEMA_REFERENCE__ENUM:
				return getEnum();
			case OasPackage.SCHEMA_REFERENCE__CONST:
				return getConst();
			case OasPackage.SCHEMA_REFERENCE__MIN_LENGTH:
				return getMinLength();
			case OasPackage.SCHEMA_REFERENCE__MAX_LENGTH:
				return getMaxLength();
			case OasPackage.SCHEMA_REFERENCE__PATTERN:
				return getPattern();
			case OasPackage.SCHEMA_REFERENCE__MINIMUM:
				return getMinimum();
			case OasPackage.SCHEMA_REFERENCE__MAXIMUM:
				return getMaximum();
			case OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MINIMUM:
				return getExclusiveMinimum();
			case OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MAXIMUM:
				return getExclusiveMaximum();
			case OasPackage.SCHEMA_REFERENCE__MULTIPLE_OF:
				return getMultipleOf();
			case OasPackage.SCHEMA_REFERENCE__READ_ONLY:
				return isReadOnly();
			case OasPackage.SCHEMA_REFERENCE__WRITE_ONLY:
				return isWriteOnly();
			case OasPackage.SCHEMA_REFERENCE__DEPRECATED:
				return isDeprecated();
			case OasPackage.SCHEMA_REFERENCE__EXAMPLE:
				return getExample();
			case OasPackage.SCHEMA_REFERENCE__EXAMPLES:
				return getExamples();
			case OasPackage.SCHEMA_REFERENCE__CONTENT_MEDIA_TYPE:
				return getContentMediaType();
			case OasPackage.SCHEMA_REFERENCE__CONTENT_ENCODING:
				return getContentEncoding();
			case OasPackage.SCHEMA_REFERENCE__KEYWORDS:
				if (coreType) return getKeywords();
				else return getKeywords().map();
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
			case OasPackage.SCHEMA_REFERENCE__REF:
				setRef((String)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__CLASSIFIER:
				setClassifier((EClassifier)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__MANY:
				setMany((Boolean)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__MIN_ITEMS:
				setMinItems((Integer)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__MAX_ITEMS:
				setMaxItems((Integer)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__UNIQUE_ITEMS:
				setUniqueItems((Boolean)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__NULLABLE:
				setNullable((Boolean)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__FORMAT:
				setFormat((String)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__TITLE:
				setTitle((String)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__DEFAULT:
				setDefault(newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__ENUM:
				getEnum().clear();
				getEnum().addAll((Collection<? extends Object>)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__CONST:
				setConst(newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__MIN_LENGTH:
				setMinLength((Integer)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__MAX_LENGTH:
				setMaxLength((Integer)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__PATTERN:
				setPattern((String)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__MINIMUM:
				setMinimum((BigDecimal)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__MAXIMUM:
				setMaximum((BigDecimal)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MINIMUM:
				setExclusiveMinimum((BigDecimal)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MAXIMUM:
				setExclusiveMaximum((BigDecimal)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__MULTIPLE_OF:
				setMultipleOf((BigDecimal)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__READ_ONLY:
				setReadOnly((Boolean)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__WRITE_ONLY:
				setWriteOnly((Boolean)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__DEPRECATED:
				setDeprecated((Boolean)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__EXAMPLE:
				setExample(newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__EXAMPLES:
				getExamples().clear();
				getExamples().addAll((Collection<? extends Object>)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__CONTENT_MEDIA_TYPE:
				setContentMediaType((String)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__CONTENT_ENCODING:
				setContentEncoding((String)newValue);
				return;
			case OasPackage.SCHEMA_REFERENCE__KEYWORDS:
				((EStructuralFeature.Setting)getKeywords()).set(newValue);
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
			case OasPackage.SCHEMA_REFERENCE__REF:
				setRef(REF_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__CLASSIFIER:
				setClassifier((EClassifier)null);
				return;
			case OasPackage.SCHEMA_REFERENCE__MANY:
				setMany(MANY_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__MIN_ITEMS:
				setMinItems(MIN_ITEMS_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__MAX_ITEMS:
				setMaxItems(MAX_ITEMS_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__UNIQUE_ITEMS:
				setUniqueItems(UNIQUE_ITEMS_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__NULLABLE:
				setNullable(NULLABLE_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__FORMAT:
				setFormat(FORMAT_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__TITLE:
				setTitle(TITLE_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__DEFAULT:
				setDefault(DEFAULT_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__ENUM:
				getEnum().clear();
				return;
			case OasPackage.SCHEMA_REFERENCE__CONST:
				setConst(CONST_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__MIN_LENGTH:
				setMinLength(MIN_LENGTH_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__MAX_LENGTH:
				setMaxLength(MAX_LENGTH_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__PATTERN:
				setPattern(PATTERN_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__MINIMUM:
				setMinimum(MINIMUM_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__MAXIMUM:
				setMaximum(MAXIMUM_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MINIMUM:
				setExclusiveMinimum(EXCLUSIVE_MINIMUM_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MAXIMUM:
				setExclusiveMaximum(EXCLUSIVE_MAXIMUM_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__MULTIPLE_OF:
				setMultipleOf(MULTIPLE_OF_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__READ_ONLY:
				setReadOnly(READ_ONLY_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__WRITE_ONLY:
				setWriteOnly(WRITE_ONLY_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__DEPRECATED:
				setDeprecated(DEPRECATED_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__EXAMPLE:
				setExample(EXAMPLE_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__EXAMPLES:
				getExamples().clear();
				return;
			case OasPackage.SCHEMA_REFERENCE__CONTENT_MEDIA_TYPE:
				setContentMediaType(CONTENT_MEDIA_TYPE_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__CONTENT_ENCODING:
				setContentEncoding(CONTENT_ENCODING_EDEFAULT);
				return;
			case OasPackage.SCHEMA_REFERENCE__KEYWORDS:
				getKeywords().clear();
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
			case OasPackage.SCHEMA_REFERENCE__REF:
				return REF_EDEFAULT == null ? ref != null : !REF_EDEFAULT.equals(ref);
			case OasPackage.SCHEMA_REFERENCE__CLASSIFIER:
				return classifier != null;
			case OasPackage.SCHEMA_REFERENCE__MANY:
				return many != MANY_EDEFAULT;
			case OasPackage.SCHEMA_REFERENCE__MIN_ITEMS:
				return MIN_ITEMS_EDEFAULT == null ? minItems != null : !MIN_ITEMS_EDEFAULT.equals(minItems);
			case OasPackage.SCHEMA_REFERENCE__MAX_ITEMS:
				return MAX_ITEMS_EDEFAULT == null ? maxItems != null : !MAX_ITEMS_EDEFAULT.equals(maxItems);
			case OasPackage.SCHEMA_REFERENCE__UNIQUE_ITEMS:
				return uniqueItems != UNIQUE_ITEMS_EDEFAULT;
			case OasPackage.SCHEMA_REFERENCE__NULLABLE:
				return nullable != NULLABLE_EDEFAULT;
			case OasPackage.SCHEMA_REFERENCE__FORMAT:
				return FORMAT_EDEFAULT == null ? format != null : !FORMAT_EDEFAULT.equals(format);
			case OasPackage.SCHEMA_REFERENCE__TITLE:
				return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
			case OasPackage.SCHEMA_REFERENCE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case OasPackage.SCHEMA_REFERENCE__DEFAULT:
				return DEFAULT_EDEFAULT == null ? default_ != null : !DEFAULT_EDEFAULT.equals(default_);
			case OasPackage.SCHEMA_REFERENCE__ENUM:
				return enum_ != null && !enum_.isEmpty();
			case OasPackage.SCHEMA_REFERENCE__CONST:
				return CONST_EDEFAULT == null ? const_ != null : !CONST_EDEFAULT.equals(const_);
			case OasPackage.SCHEMA_REFERENCE__MIN_LENGTH:
				return MIN_LENGTH_EDEFAULT == null ? minLength != null : !MIN_LENGTH_EDEFAULT.equals(minLength);
			case OasPackage.SCHEMA_REFERENCE__MAX_LENGTH:
				return MAX_LENGTH_EDEFAULT == null ? maxLength != null : !MAX_LENGTH_EDEFAULT.equals(maxLength);
			case OasPackage.SCHEMA_REFERENCE__PATTERN:
				return PATTERN_EDEFAULT == null ? pattern != null : !PATTERN_EDEFAULT.equals(pattern);
			case OasPackage.SCHEMA_REFERENCE__MINIMUM:
				return MINIMUM_EDEFAULT == null ? minimum != null : !MINIMUM_EDEFAULT.equals(minimum);
			case OasPackage.SCHEMA_REFERENCE__MAXIMUM:
				return MAXIMUM_EDEFAULT == null ? maximum != null : !MAXIMUM_EDEFAULT.equals(maximum);
			case OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MINIMUM:
				return EXCLUSIVE_MINIMUM_EDEFAULT == null ? exclusiveMinimum != null : !EXCLUSIVE_MINIMUM_EDEFAULT.equals(exclusiveMinimum);
			case OasPackage.SCHEMA_REFERENCE__EXCLUSIVE_MAXIMUM:
				return EXCLUSIVE_MAXIMUM_EDEFAULT == null ? exclusiveMaximum != null : !EXCLUSIVE_MAXIMUM_EDEFAULT.equals(exclusiveMaximum);
			case OasPackage.SCHEMA_REFERENCE__MULTIPLE_OF:
				return MULTIPLE_OF_EDEFAULT == null ? multipleOf != null : !MULTIPLE_OF_EDEFAULT.equals(multipleOf);
			case OasPackage.SCHEMA_REFERENCE__READ_ONLY:
				return readOnly != READ_ONLY_EDEFAULT;
			case OasPackage.SCHEMA_REFERENCE__WRITE_ONLY:
				return writeOnly != WRITE_ONLY_EDEFAULT;
			case OasPackage.SCHEMA_REFERENCE__DEPRECATED:
				return deprecated != DEPRECATED_EDEFAULT;
			case OasPackage.SCHEMA_REFERENCE__EXAMPLE:
				return EXAMPLE_EDEFAULT == null ? example != null : !EXAMPLE_EDEFAULT.equals(example);
			case OasPackage.SCHEMA_REFERENCE__EXAMPLES:
				return examples != null && !examples.isEmpty();
			case OasPackage.SCHEMA_REFERENCE__CONTENT_MEDIA_TYPE:
				return CONTENT_MEDIA_TYPE_EDEFAULT == null ? contentMediaType != null : !CONTENT_MEDIA_TYPE_EDEFAULT.equals(contentMediaType);
			case OasPackage.SCHEMA_REFERENCE__CONTENT_ENCODING:
				return CONTENT_ENCODING_EDEFAULT == null ? contentEncoding != null : !CONTENT_ENCODING_EDEFAULT.equals(contentEncoding);
			case OasPackage.SCHEMA_REFERENCE__KEYWORDS:
				return keywords != null && !keywords.isEmpty();
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
		result.append(" (ref: ");
		result.append(ref);
		result.append(", many: ");
		result.append(many);
		result.append(", minItems: ");
		result.append(minItems);
		result.append(", maxItems: ");
		result.append(maxItems);
		result.append(", uniqueItems: ");
		result.append(uniqueItems);
		result.append(", nullable: ");
		result.append(nullable);
		result.append(", format: ");
		result.append(format);
		result.append(", title: ");
		result.append(title);
		result.append(", description: ");
		result.append(description);
		result.append(", default: ");
		result.append(default_);
		result.append(", enum: ");
		result.append(enum_);
		result.append(", const: ");
		result.append(const_);
		result.append(", minLength: ");
		result.append(minLength);
		result.append(", maxLength: ");
		result.append(maxLength);
		result.append(", pattern: ");
		result.append(pattern);
		result.append(", minimum: ");
		result.append(minimum);
		result.append(", maximum: ");
		result.append(maximum);
		result.append(", exclusiveMinimum: ");
		result.append(exclusiveMinimum);
		result.append(", exclusiveMaximum: ");
		result.append(exclusiveMaximum);
		result.append(", multipleOf: ");
		result.append(multipleOf);
		result.append(", readOnly: ");
		result.append(readOnly);
		result.append(", writeOnly: ");
		result.append(writeOnly);
		result.append(", deprecated: ");
		result.append(deprecated);
		result.append(", example: ");
		result.append(example);
		result.append(", examples: ");
		result.append(examples);
		result.append(", contentMediaType: ");
		result.append(contentMediaType);
		result.append(", contentEncoding: ");
		result.append(contentEncoding);
		result.append(')');
		return result.toString();
	}

} //SchemaReferenceImpl
