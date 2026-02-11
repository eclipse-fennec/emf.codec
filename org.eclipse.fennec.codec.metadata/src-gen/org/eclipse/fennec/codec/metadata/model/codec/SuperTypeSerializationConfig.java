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

import org.eclipse.fennec.model.metadata.BaseSuperTypeConfig;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Super Type Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Concrete configuration for supertype serialization. Extends BaseSuperTypeConfig with codec-specific settings.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig#isUseSmartCompression <em>Use Smart Compression</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getSuperTypeSerializationConfig()
 * @model
 * @generated
 */
@ProviderType
public interface SuperTypeSerializationConfig extends BaseSuperTypeConfig {
	/**
	 * Returns the value of the '<em><b>Use Smart Compression</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use plain names for same-schema supertypes, full URI for others.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Use Smart Compression</em>' attribute.
	 * @see #setUseSmartCompression(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getSuperTypeSerializationConfig_UseSmartCompression()
	 * @model default="false"
	 * @generated
	 */
	boolean isUseSmartCompression();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig#isUseSmartCompression <em>Use Smart Compression</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Use Smart Compression</em>' attribute.
	 * @see #isUseSmartCompression()
	 * @generated
	 */
	void setUseSmartCompression(boolean value);

} // SuperTypeSerializationConfig
