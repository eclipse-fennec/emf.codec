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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reference Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Concrete configuration for reference (non-containment) serialization. Extends BaseReferenceConfig with codec-specific settings.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig#isIncludeType <em>Include Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig#isExpand <em>Expand</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getReferenceSerializationConfig()
 * @model
 * @generated
 */
@ProviderType
public interface ReferenceSerializationConfig extends BaseReferenceConfig {
	/**
	 * Returns the value of the '<em><b>Include Type</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Include type info in STRUCTURED format.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Include Type</em>' attribute.
	 * @see #setIncludeType(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getReferenceSerializationConfig_IncludeType()
	 * @model default="true"
	 * @generated
	 */
	boolean isIncludeType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig#isIncludeType <em>Include Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Include Type</em>' attribute.
	 * @see #isIncludeType()
	 * @generated
	 */
	void setIncludeType(boolean value);

	/**
	 * Returns the value of the '<em><b>Expand</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Override codec-level expand setting (null = use codec default).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expand</em>' attribute.
	 * @see #setExpand(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getReferenceSerializationConfig_Expand()
	 * @model default="false"
	 * @generated
	 */
	boolean isExpand();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig#isExpand <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expand</em>' attribute.
	 * @see #isExpand()
	 * @generated
	 */
	void setExpand(boolean value);

} // ReferenceSerializationConfig
