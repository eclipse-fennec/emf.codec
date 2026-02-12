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
package org.eclipse.fennec.model.metadata;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Base Super Type Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base configuration for supertype information serialization. Controls whether and how the EClass inheritance hierarchy is included in the output. Supertype serialization is a sub-aspect of type serialization and is implicitly disabled when type serialization is disabled.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#isEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSelection <em>Selection</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#isAsArray <em>As Array</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSeparator <em>Separator</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSuperTypeKey <em>Super Type Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseSuperTypeConfig()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface BaseSuperTypeConfig extends EObject {
	/**
	 * Returns the value of the '<em><b>Enabled</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether supertype information is serialized. Default is false (supertypes are not written).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Enabled</em>' attribute.
	 * @see #setEnabled(boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseSuperTypeConfig_Enabled()
	 * @model default="false"
	 * @generated
	 */
	boolean isEnabled();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#isEnabled <em>Enabled</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Enabled</em>' attribute.
	 * @see #isEnabled()
	 * @generated
	 */
	void setEnabled(boolean value);

	/**
	 * Returns the value of the '<em><b>Selection</b></em>' attribute.
	 * The default value is <code>"ALL"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.SuperTypeSelection}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Which supertypes to include when enabled: ALL (domain types only), ALL_EMF (including EObject etc.), SINGLE (direct parent only), NONE.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Selection</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SuperTypeSelection
	 * @see #setSelection(SuperTypeSelection)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseSuperTypeConfig_Selection()
	 * @model default="ALL"
	 * @generated
	 */
	SuperTypeSelection getSelection();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSelection <em>Selection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Selection</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SuperTypeSelection
	 * @see #getSelection()
	 * @generated
	 */
	void setSelection(SuperTypeSelection value);

	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * The default value is <code>"PLAIN"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.SerializationFormat}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Output format for supertype entries. Inherits from the parent type configuration format if not explicitly set.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @see #setFormat(SerializationFormat)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseSuperTypeConfig_Format()
	 * @model default="PLAIN"
	 * @generated
	 */
	SerializationFormat getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @see #getFormat()
	 * @generated
	 */
	void setFormat(SerializationFormat value);

	/**
	 * Returns the value of the '<em><b>As Array</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to write supertypes as a JSON array (true) or as a separator-joined string (false). Default is true (array).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>As Array</em>' attribute.
	 * @see #setAsArray(boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseSuperTypeConfig_AsArray()
	 * @model default="true"
	 * @generated
	 */
	boolean isAsArray();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#isAsArray <em>As Array</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>As Array</em>' attribute.
	 * @see #isAsArray()
	 * @generated
	 */
	void setAsArray(boolean value);

	/**
	 * Returns the value of the '<em><b>Separator</b></em>' attribute.
	 * The default value is <code>","</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Separator character when asArray=false. Default is comma. Only used when supertypes are joined into a single string.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Separator</em>' attribute.
	 * @see #setSeparator(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseSuperTypeConfig_Separator()
	 * @model default=","
	 * @generated
	 */
	String getSeparator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSeparator <em>Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Separator</em>' attribute.
	 * @see #getSeparator()
	 * @generated
	 */
	void setSeparator(String value);

	/**
	 * Returns the value of the '<em><b>Super Type Key</b></em>' attribute.
	 * The default value is <code>"_supertype"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for supertype information. In PLAIN format, this is a top-level field. In STRUCTURED format, this is a key inside the type object. Default is '_supertype'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Super Type Key</em>' attribute.
	 * @see #setSuperTypeKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseSuperTypeConfig_SuperTypeKey()
	 * @model default="_supertype"
	 * @generated
	 */
	String getSuperTypeKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseSuperTypeConfig#getSuperTypeKey <em>Super Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Super Type Key</em>' attribute.
	 * @see #getSuperTypeKey()
	 * @generated
	 */
	void setSuperTypeKey(String value);

} // BaseSuperTypeConfig
