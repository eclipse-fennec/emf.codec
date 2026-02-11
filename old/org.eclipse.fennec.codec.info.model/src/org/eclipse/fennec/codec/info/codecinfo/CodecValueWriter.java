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

import tools.jackson.databind.SerializationContext;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Codec Value Writer</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getCodecValueWriter()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface CodecValueWriter<T, V> {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getCodecValueWriter_Name()
	 * @model id="true" required="true" changeable="false"
	 * @generated
	 */
	String getName();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model valueRequired="true" contextDataType="org.eclipse.fennec.codec.info.codecinfo.SerializationContext" contextRequired="true"
	 * @generated
	 */
	V writeValue(T value, SerializationContext context);

} // CodecValueWriter
