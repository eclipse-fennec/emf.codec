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

import org.eclipse.emf.common.util.EList;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder#getInfoType <em>Info Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder#getReaders <em>Readers</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder#getWriters <em>Writers</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getCodecInfoHolder()
 * @model
 * @generated
 */
@ProviderType
public interface CodecInfoHolder {
	/**
	 * Returns the value of the '<em><b>Info Type</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.info.codecinfo.InfoType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Info Type</em>' attribute.
	 * @see org.eclipse.fennec.codec.info.codecinfo.InfoType
	 * @see #setInfoType(InfoType)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getCodecInfoHolder_InfoType()
	 * @model required="true"
	 * @generated
	 */
	InfoType getInfoType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder#getInfoType <em>Info Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Info Type</em>' attribute.
	 * @see org.eclipse.fennec.codec.info.codecinfo.InfoType
	 * @see #getInfoType()
	 * @generated
	 */
	void setInfoType(InfoType value);

	/**
	 * Returns the value of the '<em><b>Readers</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.info.codecinfo.CodecValueReader}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Readers</em>' reference list.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getCodecInfoHolder_Readers()
	 * @model resolveProxies="false" keys="name" transient="true"
	 * @generated
	 */
	EList<CodecValueReader> getReaders();

	/**
	 * Returns the value of the '<em><b>Writers</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Writers</em>' reference list.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getCodecInfoHolder_Writers()
	 * @model resolveProxies="false" keys="name" transient="true"
	 * @generated
	 */
	EList<CodecValueWriter> getWriters();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='return getReaders().stream().filter(r -&gt; r.getName() == readerName).findFirst().orElse(null);'"
	 * @generated
	 */
	CodecValueReader getReaderByName(String readerName);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='return getWriters().stream().filter(w -&gt; w.getName() == writerName).findFirst().orElse(null);'"
	 * @generated
	 */
	CodecValueWriter getWriterByName(String writerName);

} // CodecInfoHolder
