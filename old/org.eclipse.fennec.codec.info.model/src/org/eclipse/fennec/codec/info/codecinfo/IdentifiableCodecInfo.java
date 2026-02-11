/*
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.codec.info.codecinfo;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Identifiable Codec Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.IdentifiableCodecInfo#getIdentityInfo <em>Identity Info</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getIdentifiableCodecInfo()
 * @model
 * @generated
 */
@ProviderType
public interface IdentifiableCodecInfo {
	/**
	 * Returns the value of the '<em><b>Identity Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Identity Info</em>' containment reference.
	 * @see #setIdentityInfo(IdentityInfo)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getIdentifiableCodecInfo_IdentityInfo()
	 * @model containment="true"
	 * @generated
	 */
	IdentityInfo getIdentityInfo();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.IdentifiableCodecInfo#getIdentityInfo <em>Identity Info</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Identity Info</em>' containment reference.
	 * @see #getIdentityInfo()
	 * @generated
	 */
	void setIdentityInfo(IdentityInfo value);

} // IdentifiableCodecInfo
