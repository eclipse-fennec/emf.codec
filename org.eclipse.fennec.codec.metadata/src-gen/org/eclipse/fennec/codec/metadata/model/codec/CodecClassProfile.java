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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class Profile</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Codec-specific class profile containing the fully resolved annotation-layer configuration for one EClass and all its features. All annotation-internal inheritance is pre-merged: feature inherits from class, class inherits from package defaults. Contains the effective type/ID/supertype configs, forward/reverse key mappings, feature serialization order, and per-feature configs. At runtime, the ConfigurationResolver merges dynamic overrides (levels 1-4) on top of this profile (levels 5+6).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getEClass <em>EClass</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getTypeConfig <em>Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getIdConfig <em>Id Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getSuperTypeConfig <em>Super Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getFeatureConfigs <em>Feature Configs</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecClassProfile()
 * @model
 * @generated
 */
@ProviderType
public interface CodecClassProfile extends EObject {
	/**
	 * Returns the value of the '<em><b>EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The EClass this profile describes. Used to look up the profile by EClass identity.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>EClass</em>' reference.
	 * @see #setEClass(EClass)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecClassProfile_EClass()
	 * @model
	 * @generated
	 */
	EClass getEClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getEClass <em>EClass</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>EClass</em>' reference.
	 * @see #getEClass()
	 * @generated
	 */
	void setEClass(EClass value);

	/**
	 * Returns the value of the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Effective type serialization configuration after annotation-internal inheritance merge. Null means no type info configured at any level.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Config</em>' containment reference.
	 * @see #setTypeConfig(TypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecClassProfile_TypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	TypeSerializationConfig getTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getTypeConfig <em>Type Config</em>}' containment reference.
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
	 * Effective ID serialization configuration after annotation-internal inheritance merge. Null means no ID config at any level.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Config</em>' containment reference.
	 * @see #setIdConfig(IdSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecClassProfile_IdConfig()
	 * @model containment="true"
	 * @generated
	 */
	IdSerializationConfig getIdConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getIdConfig <em>Id Config</em>}' containment reference.
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
	 * Effective supertype serialization configuration after annotation-internal inheritance merge. Null means no supertype config at any level.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Super Type Config</em>' containment reference.
	 * @see #setSuperTypeConfig(SuperTypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecClassProfile_SuperTypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	SuperTypeSerializationConfig getSuperTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile#getSuperTypeConfig <em>Super Type Config</em>}' containment reference.
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
	 * Pre-computed per-feature serialization configurations. One entry per serializable feature, in serialization order. Each config has the effective key, serialize flag, and any feature-specific overrides already resolved.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Feature Configs</em>' containment reference list.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecClassProfile_FeatureConfigs()
	 * @model containment="true"
	 * @generated
	 */
	EList<FeatureSerializationConfig> getFeatureConfigs();

} // CodecClassProfile
