/*
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.model.openapi;

import java.math.BigDecimal;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Schema</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The Schema Object allows the definition of input and output data types.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#isNullable <em>Nullable</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#isReadOnly <em>Read Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#isWriteOnly <em>Write Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getDefault <em>Default</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getEnum <em>Enum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getMinimum <em>Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getMaximum <em>Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#isExclusiveMinimum <em>Exclusive Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#isExclusiveMaximum <em>Exclusive Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getMultipleOf <em>Multiple Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getMinLength <em>Min Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getMaxLength <em>Max Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getPattern <em>Pattern</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getItems <em>Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getMinItems <em>Min Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getMaxItems <em>Max Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#isUniqueItems <em>Unique Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getMinProperties <em>Min Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getMaxProperties <em>Max Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getAdditionalProperties <em>Additional Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getAdditionalPropertiesAllowed <em>Additional Properties Allowed</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getAllOf <em>All Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getOneOf <em>One Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getAnyOf <em>Any Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getNot <em>Not</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getDiscriminator <em>Discriminator</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Schema#getExternalDocs <em>External Docs</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema()
 * @model
 * @generated
 */
@ProviderType
public interface Schema extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getRef <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' attribute.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Type()
	 * @model
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see #setFormat(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Format()
	 * @model
	 * @generated
	 */
	String getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getFormat <em>Format</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Title()
	 * @model
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getTitle <em>Title</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nullable</em>' attribute.
	 * @see #setNullable(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Nullable()
	 * @model
	 * @generated
	 */
	boolean isNullable();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#isNullable <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nullable</em>' attribute.
	 * @see #isNullable()
	 * @generated
	 */
	void setNullable(boolean value);

	/**
	 * Returns the value of the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deprecated</em>' attribute.
	 * @see #setDeprecated(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Deprecated()
	 * @model
	 * @generated
	 */
	boolean isDeprecated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#isDeprecated <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deprecated</em>' attribute.
	 * @see #isDeprecated()
	 * @generated
	 */
	void setDeprecated(boolean value);

	/**
	 * Returns the value of the '<em><b>Read Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Read Only</em>' attribute.
	 * @see #setReadOnly(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_ReadOnly()
	 * @model
	 * @generated
	 */
	boolean isReadOnly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#isReadOnly <em>Read Only</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_WriteOnly()
	 * @model
	 * @generated
	 */
	boolean isWriteOnly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#isWriteOnly <em>Write Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Write Only</em>' attribute.
	 * @see #isWriteOnly()
	 * @generated
	 */
	void setWriteOnly(boolean value);

	/**
	 * Returns the value of the '<em><b>Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default</em>' attribute.
	 * @see #setDefault(Object)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Default()
	 * @model
	 * @generated
	 */
	Object getDefault();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getDefault <em>Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default</em>' attribute.
	 * @see #getDefault()
	 * @generated
	 */
	void setDefault(Object value);

	/**
	 * Returns the value of the '<em><b>Enum</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enum</em>' attribute list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Enum()
	 * @model
	 * @generated
	 */
	EList<String> getEnum();

	/**
	 * Returns the value of the '<em><b>Minimum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Minimum</em>' attribute.
	 * @see #setMinimum(BigDecimal)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Minimum()
	 * @model
	 * @generated
	 */
	BigDecimal getMinimum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getMinimum <em>Minimum</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Maximum()
	 * @model
	 * @generated
	 */
	BigDecimal getMaximum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getMaximum <em>Maximum</em>}' attribute.
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
	 * @return the value of the '<em>Exclusive Minimum</em>' attribute.
	 * @see #setExclusiveMinimum(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_ExclusiveMinimum()
	 * @model
	 * @generated
	 */
	boolean isExclusiveMinimum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#isExclusiveMinimum <em>Exclusive Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusive Minimum</em>' attribute.
	 * @see #isExclusiveMinimum()
	 * @generated
	 */
	void setExclusiveMinimum(boolean value);

	/**
	 * Returns the value of the '<em><b>Exclusive Maximum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclusive Maximum</em>' attribute.
	 * @see #setExclusiveMaximum(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_ExclusiveMaximum()
	 * @model
	 * @generated
	 */
	boolean isExclusiveMaximum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#isExclusiveMaximum <em>Exclusive Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusive Maximum</em>' attribute.
	 * @see #isExclusiveMaximum()
	 * @generated
	 */
	void setExclusiveMaximum(boolean value);

	/**
	 * Returns the value of the '<em><b>Multiple Of</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Multiple Of</em>' attribute.
	 * @see #setMultipleOf(BigDecimal)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_MultipleOf()
	 * @model
	 * @generated
	 */
	BigDecimal getMultipleOf();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getMultipleOf <em>Multiple Of</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Multiple Of</em>' attribute.
	 * @see #getMultipleOf()
	 * @generated
	 */
	void setMultipleOf(BigDecimal value);

	/**
	 * Returns the value of the '<em><b>Min Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Length</em>' attribute.
	 * @see #setMinLength(Integer)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_MinLength()
	 * @model
	 * @generated
	 */
	Integer getMinLength();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getMinLength <em>Min Length</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_MaxLength()
	 * @model
	 * @generated
	 */
	Integer getMaxLength();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getMaxLength <em>Max Length</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Pattern()
	 * @model
	 * @generated
	 */
	String getPattern();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getPattern <em>Pattern</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pattern</em>' attribute.
	 * @see #getPattern()
	 * @generated
	 */
	void setPattern(String value);

	/**
	 * Returns the value of the '<em><b>Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Items</em>' containment reference.
	 * @see #setItems(Schema)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Items()
	 * @model containment="true"
	 * @generated
	 */
	Schema getItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getItems <em>Items</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Items</em>' containment reference.
	 * @see #getItems()
	 * @generated
	 */
	void setItems(Schema value);

	/**
	 * Returns the value of the '<em><b>Min Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Items</em>' attribute.
	 * @see #setMinItems(Integer)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_MinItems()
	 * @model
	 * @generated
	 */
	Integer getMinItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getMinItems <em>Min Items</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_MaxItems()
	 * @model
	 * @generated
	 */
	Integer getMaxItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getMaxItems <em>Max Items</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_UniqueItems()
	 * @model
	 * @generated
	 */
	boolean isUniqueItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#isUniqueItems <em>Unique Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unique Items</em>' attribute.
	 * @see #isUniqueItems()
	 * @generated
	 */
	void setUniqueItems(boolean value);

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Schema},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Properties()
	 * @model mapType="org.eclipse.fennec.model.openapi.SchemaEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Schema&gt;"
	 * @generated
	 */
	EMap<String, Schema> getProperties();

	/**
	 * Returns the value of the '<em><b>Required</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required</em>' attribute list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Required()
	 * @model
	 * @generated
	 */
	EList<String> getRequired();

	/**
	 * Returns the value of the '<em><b>Min Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Properties</em>' attribute.
	 * @see #setMinProperties(Integer)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_MinProperties()
	 * @model
	 * @generated
	 */
	Integer getMinProperties();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getMinProperties <em>Min Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Properties</em>' attribute.
	 * @see #getMinProperties()
	 * @generated
	 */
	void setMinProperties(Integer value);

	/**
	 * Returns the value of the '<em><b>Max Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Properties</em>' attribute.
	 * @see #setMaxProperties(Integer)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_MaxProperties()
	 * @model
	 * @generated
	 */
	Integer getMaxProperties();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getMaxProperties <em>Max Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Properties</em>' attribute.
	 * @see #getMaxProperties()
	 * @generated
	 */
	void setMaxProperties(Integer value);

	/**
	 * Returns the value of the '<em><b>Additional Properties</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Schema for additional properties. Use additionalPropertiesAllowed for boolean case.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Additional Properties</em>' containment reference.
	 * @see #setAdditionalProperties(Schema)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_AdditionalProperties()
	 * @model containment="true"
	 * @generated
	 */
	Schema getAdditionalProperties();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getAdditionalProperties <em>Additional Properties</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Additional Properties</em>' containment reference.
	 * @see #getAdditionalProperties()
	 * @generated
	 */
	void setAdditionalProperties(Schema value);

	/**
	 * Returns the value of the '<em><b>Additional Properties Allowed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Boolean form of additionalProperties. null=not specified, true=any allowed, false=none allowed. If additionalProperties schema is set, this field is ignored.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Additional Properties Allowed</em>' attribute.
	 * @see #setAdditionalPropertiesAllowed(Boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_AdditionalPropertiesAllowed()
	 * @model
	 * @generated
	 */
	Boolean getAdditionalPropertiesAllowed();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getAdditionalPropertiesAllowed <em>Additional Properties Allowed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Additional Properties Allowed</em>' attribute.
	 * @see #getAdditionalPropertiesAllowed()
	 * @generated
	 */
	void setAdditionalPropertiesAllowed(Boolean value);

	/**
	 * Returns the value of the '<em><b>All Of</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.openapi.Schema}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>All Of</em>' containment reference list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_AllOf()
	 * @model containment="true"
	 * @generated
	 */
	EList<Schema> getAllOf();

	/**
	 * Returns the value of the '<em><b>One Of</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.openapi.Schema}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>One Of</em>' containment reference list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_OneOf()
	 * @model containment="true"
	 * @generated
	 */
	EList<Schema> getOneOf();

	/**
	 * Returns the value of the '<em><b>Any Of</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.openapi.Schema}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Any Of</em>' containment reference list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_AnyOf()
	 * @model containment="true"
	 * @generated
	 */
	EList<Schema> getAnyOf();

	/**
	 * Returns the value of the '<em><b>Not</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Not</em>' containment reference.
	 * @see #setNot(Schema)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Not()
	 * @model containment="true"
	 * @generated
	 */
	Schema getNot();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getNot <em>Not</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Not</em>' containment reference.
	 * @see #getNot()
	 * @generated
	 */
	void setNot(Schema value);

	/**
	 * Returns the value of the '<em><b>Discriminator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Discriminator</em>' containment reference.
	 * @see #setDiscriminator(Discriminator)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_Discriminator()
	 * @model containment="true"
	 * @generated
	 */
	Discriminator getDiscriminator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getDiscriminator <em>Discriminator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Discriminator</em>' containment reference.
	 * @see #getDiscriminator()
	 * @generated
	 */
	void setDiscriminator(Discriminator value);

	/**
	 * Returns the value of the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External Docs</em>' containment reference.
	 * @see #setExternalDocs(ExternalDocumentation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSchema_ExternalDocs()
	 * @model containment="true"
	 * @generated
	 */
	ExternalDocumentation getExternalDocs();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Schema#getExternalDocs <em>External Docs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>External Docs</em>' containment reference.
	 * @see #getExternalDocs()
	 * @generated
	 */
	void setExternalDocs(ExternalDocumentation value);

} // Schema
