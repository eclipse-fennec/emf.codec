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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Base Reference Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base configuration for non-containment reference serialization. Controls how cross-references between EObjects are represented in the output.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getRefKey <em>Ref Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseReferenceConfig()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface BaseReferenceConfig extends EObject {
	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * The default value is <code>"PLAIN"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Output format for references: PLAIN writes a single reference value, STRUCTURED writes a nested object with type and reference keys. Unsettable so that consumers (e.g. the properties bridge, issues #106/#175) can distinguish an explicitly configured value that happens to equal this default from "not configured" via eIsSet.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat
	 * @see #isSetFormat()
	 * @see #unsetFormat()
	 * @see #setFormat(SerializationFormat)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseReferenceConfig_Format()
	 * @model default="PLAIN" unsettable="true"
	 * @generated
	 */
	SerializationFormat getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat
	 * @see #isSetFormat()
	 * @see #unsetFormat()
	 * @see #getFormat()
	 * @generated
	 */
	void setFormat(SerializationFormat value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetFormat()
	 * @see #getFormat()
	 * @see #setFormat(SerializationFormat)
	 * @generated
	 */
	void unsetFormat();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getFormat <em>Format</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Format</em>' attribute is set.
	 * @see #unsetFormat()
	 * @see #getFormat()
	 * @see #setFormat(SerializationFormat)
	 * @generated
	 */
	boolean isSetFormat();

	/**
	 * Returns the value of the '<em><b>Type Key</b></em>' attribute.
	 * The default value is <code>"_type"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the type field inside a STRUCTURED reference object. Default is '_type'. Unsettable so that consumers (e.g. the properties bridge, issues #106/#175) can distinguish an explicitly configured value that happens to equal this default from "not configured" via eIsSet.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Key</em>' attribute.
	 * @see #isSetTypeKey()
	 * @see #unsetTypeKey()
	 * @see #setTypeKey(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseReferenceConfig_TypeKey()
	 * @model default="_type" unsettable="true"
	 * @generated
	 */
	String getTypeKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getTypeKey <em>Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Key</em>' attribute.
	 * @see #isSetTypeKey()
	 * @see #unsetTypeKey()
	 * @see #getTypeKey()
	 * @generated
	 */
	void setTypeKey(String value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getTypeKey <em>Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTypeKey()
	 * @see #getTypeKey()
	 * @see #setTypeKey(String)
	 * @generated
	 */
	void unsetTypeKey();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getTypeKey <em>Type Key</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Type Key</em>' attribute is set.
	 * @see #unsetTypeKey()
	 * @see #getTypeKey()
	 * @see #setTypeKey(String)
	 * @generated
	 */
	boolean isSetTypeKey();

	/**
	 * Returns the value of the '<em><b>Ref Key</b></em>' attribute.
	 * The default value is <code>"_ref"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the reference value inside a STRUCTURED reference object. Default is '_ref'. Unsettable so that consumers (e.g. the properties bridge, issues #106/#175) can distinguish an explicitly configured value that happens to equal this default from "not configured" via eIsSet.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref Key</em>' attribute.
	 * @see #isSetRefKey()
	 * @see #unsetRefKey()
	 * @see #setRefKey(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseReferenceConfig_RefKey()
	 * @model default="_ref" unsettable="true"
	 * @generated
	 */
	String getRefKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getRefKey <em>Ref Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref Key</em>' attribute.
	 * @see #isSetRefKey()
	 * @see #unsetRefKey()
	 * @see #getRefKey()
	 * @generated
	 */
	void setRefKey(String value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getRefKey <em>Ref Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRefKey()
	 * @see #getRefKey()
	 * @see #setRefKey(String)
	 * @generated
	 */
	void unsetRefKey();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig#getRefKey <em>Ref Key</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Ref Key</em>' attribute is set.
	 * @see #unsetRefKey()
	 * @see #getRefKey()
	 * @see #setRefKey(String)
	 * @generated
	 */
	boolean isSetRefKey();

} // BaseReferenceConfig
