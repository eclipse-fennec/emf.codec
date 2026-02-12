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
 * A representation of the model object '<em><b>Base Feature Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base configuration for EStructuralFeature serialization. Controls visibility, key naming, null/empty/default handling, and enum strategy for individual features. These settings can be configured per-feature via EAnnotations or globally via runtime options.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnore <em>Ignore</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnoreRead <em>Ignore Read</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnoreWrite <em>Ignore Write</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getForceRead <em>Force Read</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getForceWrite <em>Force Write</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeNull <em>Serialize Null</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeEmpty <em>Serialize Empty</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeDefaults <em>Serialize Defaults</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getEnumSerialization <em>Enum Serialization</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface BaseFeatureConfig extends EObject {
	/**
	 * Returns the value of the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Custom JSON property name for this feature. When null, the feature name (or extendedMetaDataName if useNamesFromExtendedMetadata is enabled) is used.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Key</em>' attribute.
	 * @see #setKey(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_Key()
	 * @model
	 * @generated
	 */
	String getKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getKey <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key</em>' attribute.
	 * @see #getKey()
	 * @generated
	 */
	void setKey(String value);

	/**
	 * Returns the value of the '<em><b>Ignore</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Skip this feature for both serialization and deserialization. Null means false (not ignored). Set to true to make the feature invisible to the codec in both directions.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ignore</em>' attribute.
	 * @see #setIgnore(Boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_Ignore()
	 * @model
	 * @generated
	 */
	Boolean getIgnore();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnore <em>Ignore</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ignore</em>' attribute.
	 * @see #getIgnore()
	 * @generated
	 */
	void setIgnore(Boolean value);

	/**
	 * Returns the value of the '<em><b>Ignore Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Skip this feature during deserialization only. Null means false. Set to true to prevent reading this feature from input while still writing it to output.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ignore Read</em>' attribute.
	 * @see #setIgnoreRead(Boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_IgnoreRead()
	 * @model
	 * @generated
	 */
	Boolean getIgnoreRead();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnoreRead <em>Ignore Read</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ignore Read</em>' attribute.
	 * @see #getIgnoreRead()
	 * @generated
	 */
	void setIgnoreRead(Boolean value);

	/**
	 * Returns the value of the '<em><b>Ignore Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Skip this feature during serialization only. Null means false. Set to true to prevent writing this feature to output while still reading it from input.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ignore Write</em>' attribute.
	 * @see #setIgnoreWrite(Boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_IgnoreWrite()
	 * @model
	 * @generated
	 */
	Boolean getIgnoreWrite();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getIgnoreWrite <em>Ignore Write</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ignore Write</em>' attribute.
	 * @see #getIgnoreWrite()
	 * @generated
	 */
	void setIgnoreWrite(Boolean value);

	/**
	 * Returns the value of the '<em><b>Force Read</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Force deserialization of EMF transient/volatile features. Null means false. Set to true to override the default skipping of transient/volatile features during deserialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Force Read</em>' attribute.
	 * @see #setForceRead(Boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_ForceRead()
	 * @model
	 * @generated
	 */
	Boolean getForceRead();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getForceRead <em>Force Read</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Force Read</em>' attribute.
	 * @see #getForceRead()
	 * @generated
	 */
	void setForceRead(Boolean value);

	/**
	 * Returns the value of the '<em><b>Force Write</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Force serialization of EMF transient/volatile/derived features. Null means false. Set to true to override the default skipping of transient/volatile/derived features during serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Force Write</em>' attribute.
	 * @see #setForceWrite(Boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_ForceWrite()
	 * @model
	 * @generated
	 */
	Boolean getForceWrite();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getForceWrite <em>Force Write</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Force Write</em>' attribute.
	 * @see #getForceWrite()
	 * @generated
	 */
	void setForceWrite(Boolean value);

	/**
	 * Returns the value of the '<em><b>Serialize Null</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to write this feature when its value is null. Null means use the codec-level default. When true, null values are explicitly written (e.g., {"name": null}).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Null</em>' attribute.
	 * @see #setSerializeNull(Boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_SerializeNull()
	 * @model
	 * @generated
	 */
	Boolean getSerializeNull();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeNull <em>Serialize Null</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Serialize Null</em>' attribute.
	 * @see #getSerializeNull()
	 * @generated
	 */
	void setSerializeNull(Boolean value);

	/**
	 * Returns the value of the '<em><b>Serialize Empty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to write this feature when its collection value is empty. Null means use the codec-level default. When true, empty collections are explicitly written (e.g., {"items": []}).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Empty</em>' attribute.
	 * @see #setSerializeEmpty(Boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_SerializeEmpty()
	 * @model
	 * @generated
	 */
	Boolean getSerializeEmpty();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeEmpty <em>Serialize Empty</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Serialize Empty</em>' attribute.
	 * @see #getSerializeEmpty()
	 * @generated
	 */
	void setSerializeEmpty(Boolean value);

	/**
	 * Returns the value of the '<em><b>Serialize Defaults</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to write this feature when its value equals the EMF default value. Null means use the codec-level default. When false, features with default values are omitted from output.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Defaults</em>' attribute.
	 * @see #setSerializeDefaults(Boolean)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_SerializeDefaults()
	 * @model
	 * @generated
	 */
	Boolean getSerializeDefaults();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getSerializeDefaults <em>Serialize Defaults</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Serialize Defaults</em>' attribute.
	 * @see #getSerializeDefaults()
	 * @generated
	 */
	void setSerializeDefaults(Boolean value);

	/**
	 * Returns the value of the '<em><b>Enum Serialization</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.EnumSerializationStrategy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Strategy for serializing enum-typed features (LITERAL, VALUE, or NAME). Null means use LITERAL as default.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Enum Serialization</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.EnumSerializationStrategy
	 * @see #setEnumSerialization(EnumSerializationStrategy)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getBaseFeatureConfig_EnumSerialization()
	 * @model
	 * @generated
	 */
	EnumSerializationStrategy getEnumSerialization();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.BaseFeatureConfig#getEnumSerialization <em>Enum Serialization</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Enum Serialization</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.EnumSerializationStrategy
	 * @see #getEnumSerialization()
	 * @generated
	 */
	void setEnumSerialization(EnumSerializationStrategy value);

} // BaseFeatureConfig
