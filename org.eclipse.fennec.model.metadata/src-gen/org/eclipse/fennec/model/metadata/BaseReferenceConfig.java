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
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig#getRefKey <em>Ref Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseReferenceConfig()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface BaseReferenceConfig extends EObject {
	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * The default value is <code>"PLAIN"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.SerializationFormat}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Output format for references: PLAIN writes a single reference value, STRUCTURED writes a nested object with type and reference keys.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @see #setFormat(SerializationFormat)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseReferenceConfig_Format()
	 * @model default="PLAIN"
	 * @generated
	 */
	SerializationFormat getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @see #getFormat()
	 * @generated
	 */
	void setFormat(SerializationFormat value);

	/**
	 * Returns the value of the '<em><b>Type Key</b></em>' attribute.
	 * The default value is <code>"_type"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the type field inside a STRUCTURED reference object. Default is '_type'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Key</em>' attribute.
	 * @see #setTypeKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseReferenceConfig_TypeKey()
	 * @model default="_type"
	 * @generated
	 */
	String getTypeKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig#getTypeKey <em>Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Key</em>' attribute.
	 * @see #getTypeKey()
	 * @generated
	 */
	void setTypeKey(String value);

	/**
	 * Returns the value of the '<em><b>Ref Key</b></em>' attribute.
	 * The default value is <code>"_ref"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the reference value inside a STRUCTURED reference object. Default is '_ref'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref Key</em>' attribute.
	 * @see #setRefKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseReferenceConfig_RefKey()
	 * @model default="_ref"
	 * @generated
	 */
	String getRefKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseReferenceConfig#getRefKey <em>Ref Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref Key</em>' attribute.
	 * @see #getRefKey()
	 * @generated
	 */
	void setRefKey(String value);

} // BaseReferenceConfig
