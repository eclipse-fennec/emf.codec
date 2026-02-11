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
 * 
 */
package org.eclipse.fennec.codec.metadata.model.codec;

import org.eclipse.fennec.model.metadata.ClassAspect;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class Codec Aspect</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Codec aspect for EClass serialization configuration. Attached to ClassMetadata.aspects.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getTypeConfig <em>Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getIdConfig <em>Id Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getSuperTypeConfig <em>Super Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isInheritFromParent <em>Inherit From Parent</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getDiscriminatorValue <em>Discriminator Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isStrictOnUnknown <em>Strict On Unknown</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isStrictOnMissing <em>Strict On Missing</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isMetadataMerge <em>Metadata Merge</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getMetadataKey <em>Metadata Key</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect()
 * @model
 * @generated
 */
@ProviderType
public interface ClassCodecAspect extends ClassAspect {
	/**
	 * Returns the value of the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Type serialization configuration for this class.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Config</em>' containment reference.
	 * @see #setTypeConfig(TypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect_TypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	TypeSerializationConfig getTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getTypeConfig <em>Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Config</em>' containment reference.
	 * @see #getTypeConfig()
	 * @generated
	 */
	void setTypeConfig(TypeSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Id Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * ID serialization configuration for this class.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Config</em>' containment reference.
	 * @see #setIdConfig(IdSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect_IdConfig()
	 * @model containment="true"
	 * @generated
	 */
	IdSerializationConfig getIdConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getIdConfig <em>Id Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Config</em>' containment reference.
	 * @see #getIdConfig()
	 * @generated
	 */
	void setIdConfig(IdSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Super Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Supertype serialization configuration for this class.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Super Type Config</em>' containment reference.
	 * @see #setSuperTypeConfig(SuperTypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect_SuperTypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	SuperTypeSerializationConfig getSuperTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getSuperTypeConfig <em>Super Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Super Type Config</em>' containment reference.
	 * @see #getSuperTypeConfig()
	 * @generated
	 */
	void setSuperTypeConfig(SuperTypeSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Inherit From Parent</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to inherit codec configuration from parent EClass.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Inherit From Parent</em>' attribute.
	 * @see #setInheritFromParent(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect_InheritFromParent()
	 * @model default="true"
	 * @generated
	 */
	boolean isInheritFromParent();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isInheritFromParent <em>Inherit From Parent</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Inherit From Parent</em>' attribute.
	 * @see #isInheritFromParent()
	 * @generated
	 */
	void setInheritFromParent(boolean value);

	/**
	 * Returns the value of the '<em><b>Discriminator Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Discriminator value for MAPPED type strategy.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Discriminator Value</em>' attribute.
	 * @see #setDiscriminatorValue(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect_DiscriminatorValue()
	 * @model
	 * @generated
	 */
	String getDiscriminatorValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getDiscriminatorValue <em>Discriminator Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Discriminator Value</em>' attribute.
	 * @see #getDiscriminatorValue()
	 * @generated
	 */
	void setDiscriminatorValue(String value);

	/**
	 * Returns the value of the '<em><b>Strict On Unknown</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * ERROR on unknown JSON fields during deserialization. Default false (LENIENT: warning + skip).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Strict On Unknown</em>' attribute.
	 * @see #setStrictOnUnknown(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect_StrictOnUnknown()
	 * @model default="false"
	 * @generated
	 */
	boolean isStrictOnUnknown();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isStrictOnUnknown <em>Strict On Unknown</em>}' attribute.
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
	 * ERROR on missing required features during deserialization. Default false (LENIENT: warning + use default).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Strict On Missing</em>' attribute.
	 * @see #setStrictOnMissing(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect_StrictOnMissing()
	 * @model default="false"
	 * @generated
	 */
	boolean isStrictOnMissing();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isStrictOnMissing <em>Strict On Missing</em>}' attribute.
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
	 * Merge type + id (+ supertype) STRUCTURED outputs into a single metadata object. Only activates when both typeFormat and idFormat are STRUCTURED. Default false.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Metadata Merge</em>' attribute.
	 * @see #setMetadataMerge(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect_MetadataMerge()
	 * @model default="false"
	 * @generated
	 */
	boolean isMetadataMerge();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#isMetadataMerge <em>Metadata Merge</em>}' attribute.
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
	 * JSON key for the merged metadata object. Only meaningful when metadataMerge=true. Default '_metadata'.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Metadata Key</em>' attribute.
	 * @see #setMetadataKey(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getClassCodecAspect_MetadataKey()
	 * @model default="_metadata"
	 * @generated
	 */
	String getMetadataKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect#getMetadataKey <em>Metadata Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Metadata Key</em>' attribute.
	 * @see #getMetadataKey()
	 * @generated
	 */
	void setMetadataKey(String value);

} // ClassCodecAspect
