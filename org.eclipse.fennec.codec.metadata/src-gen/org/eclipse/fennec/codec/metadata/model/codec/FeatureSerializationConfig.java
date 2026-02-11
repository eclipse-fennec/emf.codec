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

import org.eclipse.fennec.model.metadata.BaseFeatureConfig;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feature Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Concrete per-feature serialization configuration. Extends BaseFeatureConfig with codec-specific settings.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getFeatureName <em>Feature Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getValueWriterName <em>Value Writer Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getValueReaderName <em>Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getExpand <em>Expand</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getReferenceConfig <em>Reference Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getTypeConfig <em>Type Config</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureSerializationConfig()
 * @model
 * @generated
 */
@ProviderType
public interface FeatureSerializationConfig extends BaseFeatureConfig {
	/**
	 * Returns the value of the '<em><b>Feature Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Name of the feature this config applies to.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Feature Name</em>' attribute.
	 * @see #setFeatureName(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureSerializationConfig_FeatureName()
	 * @model
	 * @generated
	 */
	String getFeatureName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getFeatureName <em>Feature Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature Name</em>' attribute.
	 * @see #getFeatureName()
	 * @generated
	 */
	void setFeatureName(String value);

	/**
	 * Returns the value of the '<em><b>Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Custom value writer for serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value Writer Name</em>' attribute.
	 * @see #setValueWriterName(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureSerializationConfig_ValueWriterName()
	 * @model
	 * @generated
	 */
	String getValueWriterName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getValueWriterName <em>Value Writer Name</em>}' attribute.
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
	 * Custom value reader for deserialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Value Reader Name</em>' attribute.
	 * @see #setValueReaderName(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureSerializationConfig_ValueReaderName()
	 * @model
	 * @generated
	 */
	String getValueReaderName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getValueReaderName <em>Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value Reader Name</em>' attribute.
	 * @see #getValueReaderName()
	 * @generated
	 */
	void setValueReaderName(String value);

	/**
	 * Returns the value of the '<em><b>Expand</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Override expand setting for this reference feature.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expand</em>' attribute.
	 * @see #setExpand(Boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureSerializationConfig_Expand()
	 * @model
	 * @generated
	 */
	Boolean getExpand();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getExpand <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expand</em>' attribute.
	 * @see #getExpand()
	 * @generated
	 */
	void setExpand(Boolean value);

	/**
	 * Returns the value of the '<em><b>Reference Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Reference-specific config override (for EReference features).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Reference Config</em>' containment reference.
	 * @see #setReferenceConfig(ReferenceSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureSerializationConfig_ReferenceConfig()
	 * @model containment="true"
	 * @generated
	 */
	ReferenceSerializationConfig getReferenceConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getReferenceConfig <em>Reference Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reference Config</em>' containment reference.
	 * @see #getReferenceConfig()
	 * @generated
	 */
	void setReferenceConfig(ReferenceSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Type config override for this feature's contained/referenced objects.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Config</em>' containment reference.
	 * @see #setTypeConfig(TypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFeatureSerializationConfig_TypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	TypeSerializationConfig getTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig#getTypeConfig <em>Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Config</em>' containment reference.
	 * @see #getTypeConfig()
	 * @generated
	 */
	void setTypeConfig(TypeSerializationConfig value);

} // FeatureSerializationConfig
