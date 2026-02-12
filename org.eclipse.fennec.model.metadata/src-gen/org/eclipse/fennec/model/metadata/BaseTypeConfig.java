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
 * A representation of the model object '<em><b>Base Type Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base configuration for type information serialization. Controls how EClass type identity is written to the output. Shared by class-level and reference-level type configurations.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getStrategy <em>Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getSchemaKey <em>Schema Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getNameKey <em>Name Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseTypeConfig()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface BaseTypeConfig extends EObject {
	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * The default value is <code>"PLAIN"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.SerializationFormat}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Output format for type information: PLAIN writes a single value, STRUCTURED writes a nested object with schema/name keys.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @see #setFormat(SerializationFormat)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseTypeConfig_Format()
	 * @model default="PLAIN"
	 * @generated
	 */
	SerializationFormat getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @see #getFormat()
	 * @generated
	 */
	void setFormat(SerializationFormat value);

	/**
	 * Returns the value of the '<em><b>Strategy</b></em>' attribute.
	 * The default value is <code>"URI"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.TypeStrategy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Type identification strategy. Determines what kind of type identifier is written (NAME, CLASS, URI, SCHEMA_AND_TYPE, NUMERIC, NONE).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.TypeStrategy
	 * @see #setStrategy(TypeStrategy)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseTypeConfig_Strategy()
	 * @model default="URI"
	 * @generated
	 */
	TypeStrategy getStrategy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getStrategy <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.TypeStrategy
	 * @see #getStrategy()
	 * @generated
	 */
	void setStrategy(TypeStrategy value);

	/**
	 * Returns the value of the '<em><b>Type Key</b></em>' attribute.
	 * The default value is <code>"_type"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the type field. Default is '_type'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Key</em>' attribute.
	 * @see #setTypeKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseTypeConfig_TypeKey()
	 * @model default="_type"
	 * @generated
	 */
	String getTypeKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getTypeKey <em>Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Key</em>' attribute.
	 * @see #getTypeKey()
	 * @generated
	 */
	void setTypeKey(String value);

	/**
	 * Returns the value of the '<em><b>Schema Key</b></em>' attribute.
	 * The default value is <code>"schema"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the schema (nsURI) when using STRUCTURED format or SCHEMA_AND_TYPE strategy. Default is 'schema'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Schema Key</em>' attribute.
	 * @see #setSchemaKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseTypeConfig_SchemaKey()
	 * @model default="schema"
	 * @generated
	 */
	String getSchemaKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getSchemaKey <em>Schema Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema Key</em>' attribute.
	 * @see #getSchemaKey()
	 * @generated
	 */
	void setSchemaKey(String value);

	/**
	 * Returns the value of the '<em><b>Name Key</b></em>' attribute.
	 * The default value is <code>"name"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the type name inside a STRUCTURED type object. Default is 'name'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Name Key</em>' attribute.
	 * @see #setNameKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseTypeConfig_NameKey()
	 * @model default="name"
	 * @generated
	 */
	String getNameKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseTypeConfig#getNameKey <em>Name Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name Key</em>' attribute.
	 * @see #getNameKey()
	 * @generated
	 */
	void setNameKey(String value);

} // BaseTypeConfig
