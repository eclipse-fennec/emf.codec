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
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getStrategy <em>Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getSchemaKey <em>Schema Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getNameKey <em>Name Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseTypeConfig()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface BaseTypeConfig extends EObject {
	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * The default value is <code>"PLAIN"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Output format for type information: PLAIN writes a single value, STRUCTURED writes a nested object with schema/name keys. Unsettable so that consumers (e.g. the properties bridge, issues #106/#175) can distinguish an explicitly configured value that happens to equal this default from "not configured" via eIsSet.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat
	 * @see #isSetFormat()
	 * @see #unsetFormat()
	 * @see #setFormat(SerializationFormat)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseTypeConfig_Format()
	 * @model default="PLAIN" unsettable="true"
	 * @generated
	 */
	SerializationFormat getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getFormat <em>Format</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetFormat()
	 * @see #getFormat()
	 * @see #setFormat(SerializationFormat)
	 * @generated
	 */
	void unsetFormat();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getFormat <em>Format</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Strategy</b></em>' attribute.
	 * The default value is <code>"URI"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Type identification strategy. Determines what kind of type identifier is written (NAME, CLASS, URI, SCHEMA_AND_TYPE, NUMERIC, NONE). Unsettable so that consumers (e.g. the properties bridge, issues #106/#175) can distinguish an explicitly configured value that happens to equal this default from "not configured" via eIsSet.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy
	 * @see #isSetStrategy()
	 * @see #unsetStrategy()
	 * @see #setStrategy(TypeStrategy)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseTypeConfig_Strategy()
	 * @model default="URI" unsettable="true"
	 * @generated
	 */
	TypeStrategy getStrategy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getStrategy <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy
	 * @see #isSetStrategy()
	 * @see #unsetStrategy()
	 * @see #getStrategy()
	 * @generated
	 */
	void setStrategy(TypeStrategy value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getStrategy <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetStrategy()
	 * @see #getStrategy()
	 * @see #setStrategy(TypeStrategy)
	 * @generated
	 */
	void unsetStrategy();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getStrategy <em>Strategy</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Strategy</em>' attribute is set.
	 * @see #unsetStrategy()
	 * @see #getStrategy()
	 * @see #setStrategy(TypeStrategy)
	 * @generated
	 */
	boolean isSetStrategy();

	/**
	 * Returns the value of the '<em><b>Type Key</b></em>' attribute.
	 * The default value is <code>"_type"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the type field. Default is '_type'. Unsettable so that consumers (e.g. the properties bridge, issues #106/#175) can distinguish an explicitly configured value that happens to equal this default from "not configured" via eIsSet.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Key</em>' attribute.
	 * @see #isSetTypeKey()
	 * @see #unsetTypeKey()
	 * @see #setTypeKey(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseTypeConfig_TypeKey()
	 * @model default="_type" unsettable="true"
	 * @generated
	 */
	String getTypeKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getTypeKey <em>Type Key</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getTypeKey <em>Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTypeKey()
	 * @see #getTypeKey()
	 * @see #setTypeKey(String)
	 * @generated
	 */
	void unsetTypeKey();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getTypeKey <em>Type Key</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Schema Key</b></em>' attribute.
	 * The default value is <code>"schema"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the schema (nsURI) when using STRUCTURED format or SCHEMA_AND_TYPE strategy. Default is 'schema'. Unsettable so that consumers (e.g. the properties bridge, issues #106/#175) can distinguish an explicitly configured value that happens to equal this default from "not configured" via eIsSet.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Schema Key</em>' attribute.
	 * @see #isSetSchemaKey()
	 * @see #unsetSchemaKey()
	 * @see #setSchemaKey(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseTypeConfig_SchemaKey()
	 * @model default="schema" unsettable="true"
	 * @generated
	 */
	String getSchemaKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getSchemaKey <em>Schema Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema Key</em>' attribute.
	 * @see #isSetSchemaKey()
	 * @see #unsetSchemaKey()
	 * @see #getSchemaKey()
	 * @generated
	 */
	void setSchemaKey(String value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getSchemaKey <em>Schema Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSchemaKey()
	 * @see #getSchemaKey()
	 * @see #setSchemaKey(String)
	 * @generated
	 */
	void unsetSchemaKey();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getSchemaKey <em>Schema Key</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Schema Key</em>' attribute is set.
	 * @see #unsetSchemaKey()
	 * @see #getSchemaKey()
	 * @see #setSchemaKey(String)
	 * @generated
	 */
	boolean isSetSchemaKey();

	/**
	 * Returns the value of the '<em><b>Name Key</b></em>' attribute.
	 * The default value is <code>"name"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the type name inside a STRUCTURED type object. Default is 'name'. Unsettable so that consumers (e.g. the properties bridge, issues #106/#175) can distinguish an explicitly configured value that happens to equal this default from "not configured" via eIsSet.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Name Key</em>' attribute.
	 * @see #isSetNameKey()
	 * @see #unsetNameKey()
	 * @see #setNameKey(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getBaseTypeConfig_NameKey()
	 * @model default="name" unsettable="true"
	 * @generated
	 */
	String getNameKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getNameKey <em>Name Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name Key</em>' attribute.
	 * @see #isSetNameKey()
	 * @see #unsetNameKey()
	 * @see #getNameKey()
	 * @generated
	 */
	void setNameKey(String value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getNameKey <em>Name Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetNameKey()
	 * @see #getNameKey()
	 * @see #setNameKey(String)
	 * @generated
	 */
	void unsetNameKey();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig#getNameKey <em>Name Key</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Name Key</em>' attribute is set.
	 * @see #unsetNameKey()
	 * @see #getNameKey()
	 * @see #setNameKey(String)
	 * @generated
	 */
	boolean isSetNameKey();

} // BaseTypeConfig
