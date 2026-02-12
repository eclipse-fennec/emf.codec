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

import org.eclipse.fennec.model.metadata.EnumSerializationStrategy;
import org.eclipse.fennec.model.metadata.FeatureAspect;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feature Codec Aspect</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Codec aspect for EStructuralFeature serialization configuration. Attached to FeatureMetadata.aspects.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getEffectiveKey <em>Effective Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnore <em>Ignore</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnoreRead <em>Ignore Read</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnoreWrite <em>Ignore Write</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isForceRead <em>Force Read</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isForceWrite <em>Force Write</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeNull <em>Serialize Null</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeEmpty <em>Serialize Empty</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeDefaults <em>Serialize Defaults</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getValueWriterName <em>Value Writer Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getValueReaderName <em>Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getEnumSerialization <em>Enum Serialization</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect()
 * @model
 * @generated
 */
@ProviderType
public interface FeatureCodecAspect extends FeatureAspect {
	/**
	 * Returns the value of the '<em><b>Effective Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Resolved JSON property key for this feature.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Effective Key</em>' attribute.
	 * @see #setEffectiveKey(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_EffectiveKey()
	 * @model
	 * @generated
	 */
	String getEffectiveKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getEffectiveKey <em>Effective Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Effective Key</em>' attribute.
	 * @see #getEffectiveKey()
	 * @generated
	 */
	void setEffectiveKey(String value);

	/**
	 * Returns the value of the '<em><b>Ignore</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Skip this feature for both serialization and deserialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ignore</em>' attribute.
	 * @see #setIgnore(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_Ignore()
	 * @model default="false"
	 * @generated
	 */
	boolean isIgnore();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnore <em>Ignore</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ignore</em>' attribute.
	 * @see #isIgnore()
	 * @generated
	 */
	void setIgnore(boolean value);

	/**
	 * Returns the value of the '<em><b>Ignore Read</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Skip this feature during deserialization only.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ignore Read</em>' attribute.
	 * @see #setIgnoreRead(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_IgnoreRead()
	 * @model default="false"
	 * @generated
	 */
	boolean isIgnoreRead();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnoreRead <em>Ignore Read</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ignore Read</em>' attribute.
	 * @see #isIgnoreRead()
	 * @generated
	 */
	void setIgnoreRead(boolean value);

	/**
	 * Returns the value of the '<em><b>Ignore Write</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Skip this feature during serialization only.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ignore Write</em>' attribute.
	 * @see #setIgnoreWrite(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_IgnoreWrite()
	 * @model default="false"
	 * @generated
	 */
	boolean isIgnoreWrite();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isIgnoreWrite <em>Ignore Write</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ignore Write</em>' attribute.
	 * @see #isIgnoreWrite()
	 * @generated
	 */
	void setIgnoreWrite(boolean value);

	/**
	 * Returns the value of the '<em><b>Force Read</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Force deserialization of EMF transient/volatile features.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Force Read</em>' attribute.
	 * @see #setForceRead(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_ForceRead()
	 * @model default="false"
	 * @generated
	 */
	boolean isForceRead();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isForceRead <em>Force Read</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Force Read</em>' attribute.
	 * @see #isForceRead()
	 * @generated
	 */
	void setForceRead(boolean value);

	/**
	 * Returns the value of the '<em><b>Force Write</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Force serialization of EMF transient/volatile/derived features.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Force Write</em>' attribute.
	 * @see #setForceWrite(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_ForceWrite()
	 * @model default="false"
	 * @generated
	 */
	boolean isForceWrite();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isForceWrite <em>Force Write</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Force Write</em>' attribute.
	 * @see #isForceWrite()
	 * @generated
	 */
	void setForceWrite(boolean value);

	/**
	 * Returns the value of the '<em><b>Serialize Null</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to serialize null values.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Null</em>' attribute.
	 * @see #setSerializeNull(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_SerializeNull()
	 * @model default="false"
	 * @generated
	 */
	boolean isSerializeNull();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeNull <em>Serialize Null</em>}' attribute.
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
	 * Whether to serialize empty collections.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Empty</em>' attribute.
	 * @see #setSerializeEmpty(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_SerializeEmpty()
	 * @model default="false"
	 * @generated
	 */
	boolean isSerializeEmpty();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeEmpty <em>Serialize Empty</em>}' attribute.
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
	 * Whether to serialize default values.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Serialize Defaults</em>' attribute.
	 * @see #setSerializeDefaults(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_SerializeDefaults()
	 * @model default="false"
	 * @generated
	 */
	boolean isSerializeDefaults();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#isSerializeDefaults <em>Serialize Defaults</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Serialize Defaults</em>' attribute.
	 * @see #isSerializeDefaults()
	 * @generated
	 */
	void setSerializeDefaults(boolean value);

	/**
	 * Returns the value of the '<em><b>Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Custom value writer name for serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value Writer Name</em>' attribute.
	 * @see #setValueWriterName(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_ValueWriterName()
	 * @model
	 * @generated
	 */
	String getValueWriterName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getValueWriterName <em>Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value Writer Name</em>' attribute.
	 * @see #getValueWriterName()
	 * @generated
	 */
	void setValueWriterName(String value);

	/**
	 * Returns the value of the '<em><b>Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Custom value reader name for deserialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value Reader Name</em>' attribute.
	 * @see #setValueReaderName(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_ValueReaderName()
	 * @model
	 * @generated
	 */
	String getValueReaderName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getValueReaderName <em>Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value Reader Name</em>' attribute.
	 * @see #getValueReaderName()
	 * @generated
	 */
	void setValueReaderName(String value);

	/**
	 * Returns the value of the '<em><b>Enum Serialization</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.metadata.EnumSerializationStrategy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Strategy for serializing enum values (null = use LITERAL default).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Enum Serialization</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.EnumSerializationStrategy
	 * @see #setEnumSerialization(EnumSerializationStrategy)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureCodecAspect_EnumSerialization()
	 * @model
	 * @generated
	 */
	EnumSerializationStrategy getEnumSerialization();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect#getEnumSerialization <em>Enum Serialization</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Enum Serialization</em>' attribute.
	 * @see org.eclipse.fennec.model.metadata.EnumSerializationStrategy
	 * @see #getEnumSerialization()
	 * @generated
	 */
	void setEnumSerialization(EnumSerializationStrategy value);

} // FeatureCodecAspect
