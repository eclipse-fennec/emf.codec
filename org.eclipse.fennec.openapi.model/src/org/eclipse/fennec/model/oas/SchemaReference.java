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

import java.math.BigDecimal;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Schema Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A Schema Object at a use site (parameter, header, media type). Its type is a classifier: one of components/schemas, an inline classifier, or an Ecore data type for simple schemas, whose facets are kept here (issue #301).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getClassifier <em>Classifier</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#isMany <em>Many</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getMinItems <em>Min Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getMaxItems <em>Max Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#isUniqueItems <em>Unique Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#isNullable <em>Nullable</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getDefault <em>Default</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getEnum <em>Enum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getConst <em>Const</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getMinLength <em>Min Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getMaxLength <em>Max Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getPattern <em>Pattern</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getMinimum <em>Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getMaximum <em>Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getExclusiveMinimum <em>Exclusive Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getExclusiveMaximum <em>Exclusive Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getMultipleOf <em>Multiple Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#isReadOnly <em>Read Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#isWriteOnly <em>Write Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getContentMediaType <em>Content Media Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getContentEncoding <em>Content Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SchemaReference#getKeywords <em>Keywords</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference()
 * @model
 * @generated
 */
@ProviderType
public interface SchemaReference extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of the schema; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getRef <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' attribute.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(String value);

	/**
	 * Returns the value of the '<em><b>Classifier</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The type: a classifier of components/schemas, an inline classifier or an Ecore data type.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Classifier</em>' reference.
	 * @see #setClassifier(EClassifier)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Classifier()
	 * @model
	 * @generated
	 */
	EClassifier getClassifier();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getClassifier <em>Classifier</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Classifier</em>' reference.
	 * @see #getClassifier()
	 * @generated
	 */
	void setClassifier(EClassifier value);

	/**
	 * Returns the value of the '<em><b>Many</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * An array schema: the classifier is the item type.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Many</em>' attribute.
	 * @see #setMany(boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Many()
	 * @model
	 * @generated
	 */
	boolean isMany();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#isMany <em>Many</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Many</em>' attribute.
	 * @see #isMany()
	 * @generated
	 */
	void setMany(boolean value);

	/**
	 * Returns the value of the '<em><b>Min Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Items</em>' attribute.
	 * @see #setMinItems(Integer)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_MinItems()
	 * @model
	 * @generated
	 */
	Integer getMinItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getMinItems <em>Min Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Items</em>' attribute.
	 * @see #getMinItems()
	 * @generated
	 */
	void setMinItems(Integer value);

	/**
	 * Returns the value of the '<em><b>Max Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Items</em>' attribute.
	 * @see #setMaxItems(Integer)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_MaxItems()
	 * @model
	 * @generated
	 */
	Integer getMaxItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getMaxItems <em>Max Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Items</em>' attribute.
	 * @see #getMaxItems()
	 * @generated
	 */
	void setMaxItems(Integer value);

	/**
	 * Returns the value of the '<em><b>Unique Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unique Items</em>' attribute.
	 * @see #setUniqueItems(boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_UniqueItems()
	 * @model
	 * @generated
	 */
	boolean isUniqueItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#isUniqueItems <em>Unique Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unique Items</em>' attribute.
	 * @see #isUniqueItems()
	 * @generated
	 */
	void setUniqueItems(boolean value);

	/**
	 * Returns the value of the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * null is allowed: nullable in 3.0, a null type in 3.1+.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Nullable</em>' attribute.
	 * @see #setNullable(boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Nullable()
	 * @model
	 * @generated
	 */
	boolean isNullable();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#isNullable <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nullable</em>' attribute.
	 * @see #isNullable()
	 * @generated
	 */
	void setNullable(boolean value);

	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see #setFormat(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Format()
	 * @model
	 * @generated
	 */
	String getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format</em>' attribute.
	 * @see #getFormat()
	 * @generated
	 */
	void setFormat(String value);

	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Title()
	 * @model
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default</em>' attribute.
	 * @see #setDefault(Object)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Default()
	 * @model
	 * @generated
	 */
	Object getDefault();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getDefault <em>Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default</em>' attribute.
	 * @see #getDefault()
	 * @generated
	 */
	void setDefault(Object value);

	/**
	 * Returns the value of the '<em><b>Enum</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Object}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enum</em>' attribute list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Enum()
	 * @model
	 * @generated
	 */
	EList<Object> getEnum();

	/**
	 * Returns the value of the '<em><b>Const</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Const</em>' attribute.
	 * @see #setConst(Object)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Const()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	Object getConst();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getConst <em>Const</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Const</em>' attribute.
	 * @see #getConst()
	 * @generated
	 */
	void setConst(Object value);

	/**
	 * Returns the value of the '<em><b>Min Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Length</em>' attribute.
	 * @see #setMinLength(Integer)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_MinLength()
	 * @model
	 * @generated
	 */
	Integer getMinLength();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getMinLength <em>Min Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Length</em>' attribute.
	 * @see #getMinLength()
	 * @generated
	 */
	void setMinLength(Integer value);

	/**
	 * Returns the value of the '<em><b>Max Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Length</em>' attribute.
	 * @see #setMaxLength(Integer)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_MaxLength()
	 * @model
	 * @generated
	 */
	Integer getMaxLength();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getMaxLength <em>Max Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Length</em>' attribute.
	 * @see #getMaxLength()
	 * @generated
	 */
	void setMaxLength(Integer value);

	/**
	 * Returns the value of the '<em><b>Pattern</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pattern</em>' attribute.
	 * @see #setPattern(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Pattern()
	 * @model
	 * @generated
	 */
	String getPattern();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getPattern <em>Pattern</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pattern</em>' attribute.
	 * @see #getPattern()
	 * @generated
	 */
	void setPattern(String value);

	/**
	 * Returns the value of the '<em><b>Minimum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Minimum</em>' attribute.
	 * @see #setMinimum(BigDecimal)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Minimum()
	 * @model
	 * @generated
	 */
	BigDecimal getMinimum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getMinimum <em>Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Minimum</em>' attribute.
	 * @see #getMinimum()
	 * @generated
	 */
	void setMinimum(BigDecimal value);

	/**
	 * Returns the value of the '<em><b>Maximum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Maximum</em>' attribute.
	 * @see #setMaximum(BigDecimal)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Maximum()
	 * @model
	 * @generated
	 */
	BigDecimal getMaximum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getMaximum <em>Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Maximum</em>' attribute.
	 * @see #getMaximum()
	 * @generated
	 */
	void setMaximum(BigDecimal value);

	/**
	 * Returns the value of the '<em><b>Exclusive Minimum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The exclusive bound itself (3.1+ form); a 3.0 exclusiveMinimum: true with minimum is read into it.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Exclusive Minimum</em>' attribute.
	 * @see #setExclusiveMinimum(BigDecimal)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_ExclusiveMinimum()
	 * @model
	 * @generated
	 */
	BigDecimal getExclusiveMinimum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getExclusiveMinimum <em>Exclusive Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusive Minimum</em>' attribute.
	 * @see #getExclusiveMinimum()
	 * @generated
	 */
	void setExclusiveMinimum(BigDecimal value);

	/**
	 * Returns the value of the '<em><b>Exclusive Maximum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The exclusive bound itself (3.1+ form); a 3.0 exclusiveMaximum: true with maximum is read into it.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Exclusive Maximum</em>' attribute.
	 * @see #setExclusiveMaximum(BigDecimal)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_ExclusiveMaximum()
	 * @model
	 * @generated
	 */
	BigDecimal getExclusiveMaximum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getExclusiveMaximum <em>Exclusive Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusive Maximum</em>' attribute.
	 * @see #getExclusiveMaximum()
	 * @generated
	 */
	void setExclusiveMaximum(BigDecimal value);

	/**
	 * Returns the value of the '<em><b>Multiple Of</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Multiple Of</em>' attribute.
	 * @see #setMultipleOf(BigDecimal)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_MultipleOf()
	 * @model
	 * @generated
	 */
	BigDecimal getMultipleOf();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getMultipleOf <em>Multiple Of</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Multiple Of</em>' attribute.
	 * @see #getMultipleOf()
	 * @generated
	 */
	void setMultipleOf(BigDecimal value);

	/**
	 * Returns the value of the '<em><b>Read Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Read Only</em>' attribute.
	 * @see #setReadOnly(boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_ReadOnly()
	 * @model
	 * @generated
	 */
	boolean isReadOnly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#isReadOnly <em>Read Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Read Only</em>' attribute.
	 * @see #isReadOnly()
	 * @generated
	 */
	void setReadOnly(boolean value);

	/**
	 * Returns the value of the '<em><b>Write Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Write Only</em>' attribute.
	 * @see #setWriteOnly(boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_WriteOnly()
	 * @model
	 * @generated
	 */
	boolean isWriteOnly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#isWriteOnly <em>Write Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Write Only</em>' attribute.
	 * @see #isWriteOnly()
	 * @generated
	 */
	void setWriteOnly(boolean value);

	/**
	 * Returns the value of the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deprecated</em>' attribute.
	 * @see #setDeprecated(boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Deprecated()
	 * @model
	 * @generated
	 */
	boolean isDeprecated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#isDeprecated <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deprecated</em>' attribute.
	 * @see #isDeprecated()
	 * @generated
	 */
	void setDeprecated(boolean value);

	/**
	 * Returns the value of the '<em><b>Example</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Example</em>' attribute.
	 * @see #setExample(Object)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Example()
	 * @model
	 * @generated
	 */
	Object getExample();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getExample <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Example</em>' attribute.
	 * @see #getExample()
	 * @generated
	 */
	void setExample(Object value);

	/**
	 * Returns the value of the '<em><b>Examples</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Object}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Examples</em>' attribute list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Examples()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	EList<Object> getExamples();

	/**
	 * Returns the value of the '<em><b>Content Media Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content Media Type</em>' attribute.
	 * @see #setContentMediaType(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_ContentMediaType()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	String getContentMediaType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getContentMediaType <em>Content Media Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content Media Type</em>' attribute.
	 * @see #getContentMediaType()
	 * @generated
	 */
	void setContentMediaType(String value);

	/**
	 * Returns the value of the '<em><b>Content Encoding</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content Encoding</em>' attribute.
	 * @see #setContentEncoding(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_ContentEncoding()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	String getContentEncoding();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SchemaReference#getContentEncoding <em>Content Encoding</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content Encoding</em>' attribute.
	 * @see #getContentEncoding()
	 * @generated
	 */
	void setContentEncoding(String value);

	/**
	 * Returns the value of the '<em><b>Keywords</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Object},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Keywords of the schema not represented above, kept as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Keywords</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSchemaReference_Keywords()
	 * @model mapType="org.eclipse.fennec.model.oas.AnyEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getKeywords();

} // SchemaReference
