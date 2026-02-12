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
 * A representation of the model object '<em><b>Base Id Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base configuration for ID serialization. Controls how object identity is written to the output, including the ID strategy, key naming, and format.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getStrategy <em>Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getKeyMode <em>Key Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getIdKey <em>Id Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getSeparator <em>Separator</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseIdConfig#isOnTop <em>On Top</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseIdConfig#isSerializeSeparator <em>Serialize Separator</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getSeparatorKey <em>Separator Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getValueKey <em>Value Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface BaseIdConfig extends EObject {
	/**
	 * Returns the value of the '<em><b>Strategy</b></em>' attribute.
	 * The default value is <code>"ID_FIELD"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.IdStrategy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Strategy for determining which features form the ID (ID_FIELD uses eID markers, COMBINED uses explicitly listed features). To disable ID serialization, use IdKeyMode.NONE instead.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.IdStrategy
	 * @see #setStrategy(IdStrategy)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig_Strategy()
	 * @model default="ID_FIELD"
	 * @generated
	 */
	IdStrategy getStrategy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getStrategy <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.IdStrategy
	 * @see #getStrategy()
	 * @generated
	 */
	void setStrategy(IdStrategy value);

	/**
	 * Returns the value of the '<em><b>Key Mode</b></em>' attribute.
	 * The default value is <code>"ID_ONLY"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.IdKeyMode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Controls what keys appear in the output: ID_ONLY writes only the dedicated ID key, BOTH writes ID key and feature keys, FEATURE_ONLY writes only feature keys, NONE suppresses all ID output.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Key Mode</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.IdKeyMode
	 * @see #setKeyMode(IdKeyMode)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig_KeyMode()
	 * @model default="ID_ONLY"
	 * @generated
	 */
	IdKeyMode getKeyMode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getKeyMode <em>Key Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key Mode</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.IdKeyMode
	 * @see #getKeyMode()
	 * @generated
	 */
	void setKeyMode(IdKeyMode value);

	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * The default value is <code>"PLAIN"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.SerializationFormat}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Output format for the ID: PLAIN writes a single value, STRUCTURED writes a nested object with value/separator keys.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @see #setFormat(SerializationFormat)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig_Format()
	 * @model default="PLAIN"
	 * @generated
	 */
	SerializationFormat getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.SerializationFormat
	 * @see #getFormat()
	 * @generated
	 */
	void setFormat(SerializationFormat value);

	/**
	 * Returns the value of the '<em><b>Id Key</b></em>' attribute.
	 * The default value is <code>"_id"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the dedicated ID field. Default is '_id'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Key</em>' attribute.
	 * @see #setIdKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig_IdKey()
	 * @model default="_id"
	 * @generated
	 */
	String getIdKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getIdKey <em>Id Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Key</em>' attribute.
	 * @see #getIdKey()
	 * @generated
	 */
	void setIdKey(String value);

	/**
	 * Returns the value of the '<em><b>Separator</b></em>' attribute.
	 * The default value is <code>"-"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Separator character for combining multiple ID feature values in COMBINED strategy. Default is '-'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Separator</em>' attribute.
	 * @see #setSeparator(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig_Separator()
	 * @model default="-"
	 * @generated
	 */
	String getSeparator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getSeparator <em>Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Separator</em>' attribute.
	 * @see #getSeparator()
	 * @generated
	 */
	void setSeparator(String value);

	/**
	 * Returns the value of the '<em><b>On Top</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether the ID field appears before the type field in the serialized output. Default true (ID first). Useful for MongoDB where _id should be the first field for indexing.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>On Top</em>' attribute.
	 * @see #setOnTop(boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig_OnTop()
	 * @model default="true"
	 * @generated
	 */
	boolean isOnTop();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#isOnTop <em>On Top</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>On Top</em>' attribute.
	 * @see #isOnTop()
	 * @generated
	 */
	void setOnTop(boolean value);

	/**
	 * Returns the value of the '<em><b>Serialize Separator</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to include the separator character in STRUCTURED ID output. When true, the separator is written alongside the ID value, enabling deserialization without knowing the separator in advance.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Separator</em>' attribute.
	 * @see #setSerializeSeparator(boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig_SerializeSeparator()
	 * @model default="true"
	 * @generated
	 */
	boolean isSerializeSeparator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#isSerializeSeparator <em>Serialize Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Serialize Separator</em>' attribute.
	 * @see #isSerializeSeparator()
	 * @generated
	 */
	void setSerializeSeparator(boolean value);

	/**
	 * Returns the value of the '<em><b>Separator Key</b></em>' attribute.
	 * The default value is <code>"separator"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the separator field in STRUCTURED ID format. Default is 'separator'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Separator Key</em>' attribute.
	 * @see #setSeparatorKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig_SeparatorKey()
	 * @model default="separator"
	 * @generated
	 */
	String getSeparatorKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getSeparatorKey <em>Separator Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Separator Key</em>' attribute.
	 * @see #getSeparatorKey()
	 * @generated
	 */
	void setSeparatorKey(String value);

	/**
	 * Returns the value of the '<em><b>Value Key</b></em>' attribute.
	 * The default value is <code>"id"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * JSON property name for the ID value inside a STRUCTURED ID object. Default is 'id'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value Key</em>' attribute.
	 * @see #setValueKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseIdConfig_ValueKey()
	 * @model default="id"
	 * @generated
	 */
	String getValueKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseIdConfig#getValueKey <em>Value Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value Key</em>' attribute.
	 * @see #getValueKey()
	 * @generated
	 */
	void setValueKey(String value);

} // BaseIdConfig
