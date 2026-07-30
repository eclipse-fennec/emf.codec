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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Root configuration for codec serialization behavior.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isUseNumericIds <em>Use Numeric Ids</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getTypeConfig <em>Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getContainmentTypeConfig <em>Containment Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getReferenceTypeConfig <em>Reference Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getIdConfig <em>Id Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getReferenceConfig <em>Reference Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getSuperTypeConfig <em>Super Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getFeatureConfigs <em>Feature Configs</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isExpand <em>Expand</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getExpandDepth <em>Expand Depth</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isExpandIgnoreBidirectional <em>Expand Ignore Bidirectional</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeNull <em>Serialize Null</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeEmpty <em>Serialize Empty</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeDefaults <em>Serialize Defaults</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getTypeHintMode <em>Type Hint Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getDeserializationMode <em>Deserialization Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isStrictOnUnknown <em>Strict On Unknown</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isStrictOnMissing <em>Strict On Missing</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isMetadataMerge <em>Metadata Merge</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getMetadataKey <em>Metadata Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig()
 * @model
 * @generated
 */
@ProviderType
public interface CodecConfig extends EObject {
	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * The default value is <code>"PLAIN"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Codec-wide default serialization format.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat
	 * @see #setFormat(SerializationFormat)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_Format()
	 * @model default="PLAIN"
	 * @generated
	 */
	SerializationFormat getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat
	 * @see #getFormat()
	 * @generated
	 */
	void setFormat(SerializationFormat value);

	/**
	 * Returns the value of the '<em><b>Use Numeric Ids</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use numeric classifier IDs instead of names (for STRUCTURED format).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Use Numeric Ids</em>' attribute.
	 * @see #setUseNumericIds(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_UseNumericIds()
	 * @model default="false"
	 * @generated
	 */
	boolean isUseNumericIds();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isUseNumericIds <em>Use Numeric Ids</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Use Numeric Ids</em>' attribute.
	 * @see #isUseNumericIds()
	 * @generated
	 */
	void setUseNumericIds(boolean value);

	/**
	 * Returns the value of the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Configuration for root object type serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Config</em>' containment reference.
	 * @see #setTypeConfig(TypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_TypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	TypeSerializationConfig getTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getTypeConfig <em>Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Config</em>' containment reference.
	 * @see #getTypeConfig()
	 * @generated
	 */
	void setTypeConfig(TypeSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Containment Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Configuration for contained object type serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Containment Type Config</em>' containment reference.
	 * @see #setContainmentTypeConfig(TypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_ContainmentTypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	TypeSerializationConfig getContainmentTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getContainmentTypeConfig <em>Containment Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Containment Type Config</em>' containment reference.
	 * @see #getContainmentTypeConfig()
	 * @generated
	 */
	void setContainmentTypeConfig(TypeSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Reference Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Configuration for reference type serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Reference Type Config</em>' containment reference.
	 * @see #setReferenceTypeConfig(TypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_ReferenceTypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	TypeSerializationConfig getReferenceTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getReferenceTypeConfig <em>Reference Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reference Type Config</em>' containment reference.
	 * @see #getReferenceTypeConfig()
	 * @generated
	 */
	void setReferenceTypeConfig(TypeSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Id Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Configuration for ID serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Config</em>' containment reference.
	 * @see #setIdConfig(IdSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_IdConfig()
	 * @model containment="true"
	 * @generated
	 */
	IdSerializationConfig getIdConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getIdConfig <em>Id Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Config</em>' containment reference.
	 * @see #getIdConfig()
	 * @generated
	 */
	void setIdConfig(IdSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Reference Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Configuration for reference serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Reference Config</em>' containment reference.
	 * @see #setReferenceConfig(ReferenceSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_ReferenceConfig()
	 * @model containment="true"
	 * @generated
	 */
	ReferenceSerializationConfig getReferenceConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getReferenceConfig <em>Reference Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reference Config</em>' containment reference.
	 * @see #getReferenceConfig()
	 * @generated
	 */
	void setReferenceConfig(ReferenceSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Super Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Configuration for supertype serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Super Type Config</em>' containment reference.
	 * @see #setSuperTypeConfig(SuperTypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_SuperTypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	SuperTypeSerializationConfig getSuperTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getSuperTypeConfig <em>Super Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Super Type Config</em>' containment reference.
	 * @see #getSuperTypeConfig()
	 * @generated
	 */
	void setSuperTypeConfig(SuperTypeSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Feature Configs</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Per-feature serialization overrides.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Feature Configs</em>' containment reference list.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_FeatureConfigs()
	 * @model containment="true"
	 * @generated
	 */
	EList<FeatureSerializationConfig> getFeatureConfigs();

	/**
	 * Returns the value of the '<em><b>Expand</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Enable inline serialization for resolved references.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expand</em>' attribute.
	 * @see #setExpand(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_Expand()
	 * @model default="false"
	 * @generated
	 */
	boolean isExpand();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isExpand <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expand</em>' attribute.
	 * @see #isExpand()
	 * @generated
	 */
	void setExpand(boolean value);

	/**
	 * Returns the value of the '<em><b>Expand Depth</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Maximum depth for nested expansion.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expand Depth</em>' attribute.
	 * @see #setExpandDepth(int)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_ExpandDepth()
	 * @model default="1"
	 * @generated
	 */
	int getExpandDepth();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getExpandDepth <em>Expand Depth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expand Depth</em>' attribute.
	 * @see #getExpandDepth()
	 * @generated
	 */
	void setExpandDepth(int value);

	/**
	 * Returns the value of the '<em><b>Expand Ignore Bidirectional</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Skip opposite/bi-directional references when expanding.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expand Ignore Bidirectional</em>' attribute.
	 * @see #setExpandIgnoreBidirectional(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_ExpandIgnoreBidirectional()
	 * @model default="true"
	 * @generated
	 */
	boolean isExpandIgnoreBidirectional();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isExpandIgnoreBidirectional <em>Expand Ignore Bidirectional</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expand Ignore Bidirectional</em>' attribute.
	 * @see #isExpandIgnoreBidirectional()
	 * @generated
	 */
	void setExpandIgnoreBidirectional(boolean value);

	/**
	 * Returns the value of the '<em><b>Serialize Null</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Default: whether to serialize null values.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Null</em>' attribute.
	 * @see #setSerializeNull(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_SerializeNull()
	 * @model default="false"
	 * @generated
	 */
	boolean isSerializeNull();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeNull <em>Serialize Null</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Serialize Null</em>' attribute.
	 * @see #isSerializeNull()
	 * @generated
	 */
	void setSerializeNull(boolean value);

	/**
	 * Returns the value of the '<em><b>Serialize Empty</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Default: whether to serialize empty collections.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Empty</em>' attribute.
	 * @see #setSerializeEmpty(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_SerializeEmpty()
	 * @model default="false"
	 * @generated
	 */
	boolean isSerializeEmpty();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeEmpty <em>Serialize Empty</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Serialize Empty</em>' attribute.
	 * @see #isSerializeEmpty()
	 * @generated
	 */
	void setSerializeEmpty(boolean value);

	/**
	 * Returns the value of the '<em><b>Serialize Defaults</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Default: whether to serialize default values.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Defaults</em>' attribute.
	 * @see #setSerializeDefaults(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_SerializeDefaults()
	 * @model default="false"
	 * @generated
	 */
	boolean isSerializeDefaults();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isSerializeDefaults <em>Serialize Defaults</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Serialize Defaults</em>' attribute.
	 * @see #isSerializeDefaults()
	 * @generated
	 */
	void setSerializeDefaults(boolean value);

	/**
	 * Returns the value of the '<em><b>Type Hint Mode</b></em>' attribute.
	 * The default value is <code>"HINT"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * How type hints (CODEC_ROOT_OBJECT) are treated during deserialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Hint Mode</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode
	 * @see #setTypeHintMode(TypeHintMode)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_TypeHintMode()
	 * @model default="HINT"
	 * @generated
	 */
	TypeHintMode getTypeHintMode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getTypeHintMode <em>Type Hint Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Hint Mode</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode
	 * @see #getTypeHintMode()
	 * @generated
	 */
	void setTypeHintMode(TypeHintMode value);

	/**
	 * Returns the value of the '<em><b>Deserialization Mode</b></em>' attribute.
	 * The default value is <code>"LENIENT"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Type resolution strictness during deserialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Deserialization Mode</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode
	 * @see #setDeserializationMode(DeserializationMode)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_DeserializationMode()
	 * @model default="LENIENT"
	 * @generated
	 */
	DeserializationMode getDeserializationMode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getDeserializationMode <em>Deserialization Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deserialization Mode</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode
	 * @see #getDeserializationMode()
	 * @generated
	 */
	void setDeserializationMode(DeserializationMode value);

	/**
	 * Returns the value of the '<em><b>Strict On Unknown</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Global default: ERROR on unknown JSON fields during deserialization. Default false (LENIENT).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Strict On Unknown</em>' attribute.
	 * @see #setStrictOnUnknown(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_StrictOnUnknown()
	 * @model default="false"
	 * @generated
	 */
	boolean isStrictOnUnknown();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isStrictOnUnknown <em>Strict On Unknown</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Strict On Unknown</em>' attribute.
	 * @see #isStrictOnUnknown()
	 * @generated
	 */
	void setStrictOnUnknown(boolean value);

	/**
	 * Returns the value of the '<em><b>Strict On Missing</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Global default: ERROR on missing required features during deserialization. Default false (LENIENT).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Strict On Missing</em>' attribute.
	 * @see #setStrictOnMissing(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_StrictOnMissing()
	 * @model default="false"
	 * @generated
	 */
	boolean isStrictOnMissing();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isStrictOnMissing <em>Strict On Missing</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Strict On Missing</em>' attribute.
	 * @see #isStrictOnMissing()
	 * @generated
	 */
	void setStrictOnMissing(boolean value);

	/**
	 * Returns the value of the '<em><b>Metadata Merge</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Global default: merge type + id (+ supertype) STRUCTURED outputs into a single metadata object. Default false.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Metadata Merge</em>' attribute.
	 * @see #setMetadataMerge(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_MetadataMerge()
	 * @model default="false"
	 * @generated
	 */
	boolean isMetadataMerge();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#isMetadataMerge <em>Metadata Merge</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Metadata Merge</em>' attribute.
	 * @see #isMetadataMerge()
	 * @generated
	 */
	void setMetadataMerge(boolean value);

	/**
	 * Returns the value of the '<em><b>Metadata Key</b></em>' attribute.
	 * The default value is <code>"_metadata"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Global default: JSON key for the merged metadata object. Default '_metadata'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Metadata Key</em>' attribute.
	 * @see #setMetadataKey(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecConfig_MetadataKey()
	 * @model default="_metadata"
	 * @generated
	 */
	String getMetadataKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig#getMetadataKey <em>Metadata Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Metadata Key</em>' attribute.
	 * @see #getMetadataKey()
	 * @generated
	 */
	void setMetadataKey(String value);

} // CodecConfig
