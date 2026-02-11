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

import org.eclipse.emf.ecore.EStructuralFeature;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Identity Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdKey <em>Id Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdStrategy <em>Id Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdSeparator <em>Id Separator</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdFeatures <em>Id Features</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdValueReaderName <em>Id Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdValueWriterName <em>Id Value Writer Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getIdentityInfo()
 * @model
 * @generated
 */
@ProviderType
public interface IdentityInfo {
	/**
	 * Returns the value of the '<em><b>Id Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This is the property name to be used for the id field.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Key</em>' attribute.
	 * @see #setIdKey(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getIdentityInfo_IdKey()
	 * @model
	 * @generated
	 */
	String getIdKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdKey <em>Id Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Key</em>' attribute.
	 * @see #getIdKey()
	 * @generated
	 */
	void setIdKey(String value);

	/**
	 * Returns the value of the '<em><b>Id Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This supports the possibility of setting a strategy for id field determination, based on model annotations. For instance, an id field could be the combination of two model fields, and this should be marked in the model.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Strategy</em>' attribute.
	 * @see #setIdStrategy(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getIdentityInfo_IdStrategy()
	 * @model
	 * @generated
	 */
	String getIdStrategy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdStrategy <em>Id Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Strategy</em>' attribute.
	 * @see #getIdStrategy()
	 * @generated
	 */
	void setIdStrategy(String value);

	/**
	 * Returns the value of the '<em><b>Id Separator</b></em>' attribute.
	 * The default value is <code>"."</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This supports the possibility of setting an id as a combination of multiple fields. The idSeparator property indicates the separator to be used when building the id field.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Separator</em>' attribute.
	 * @see #setIdSeparator(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getIdentityInfo_IdSeparator()
	 * @model default="."
	 * @generated
	 */
	String getIdSeparator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdSeparator <em>Id Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Separator</em>' attribute.
	 * @see #getIdSeparator()
	 * @generated
	 */
	void setIdSeparator(String value);

	/**
	 * Returns the value of the '<em><b>Id Features</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EStructuralFeature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id Features</em>' reference list.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getIdentityInfo_IdFeatures()
	 * @model
	 * @generated
	 */
	EList<EStructuralFeature> getIdFeatures();

	/**
	 * Returns the value of the '<em><b>Id Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id Value Reader Name</em>' attribute.
	 * @see #setIdValueReaderName(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getIdentityInfo_IdValueReaderName()
	 * @model
	 * @generated
	 */
	String getIdValueReaderName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdValueReaderName <em>Id Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Value Reader Name</em>' attribute.
	 * @see #getIdValueReaderName()
	 * @generated
	 */
	void setIdValueReaderName(String value);

	/**
	 * Returns the value of the '<em><b>Id Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id Value Writer Name</em>' attribute.
	 * @see #setIdValueWriterName(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getIdentityInfo_IdValueWriterName()
	 * @model
	 * @generated
	 */
	String getIdValueWriterName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.IdentityInfo#getIdValueWriterName <em>Id Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Value Writer Name</em>' attribute.
	 * @see #getIdValueWriterName()
	 * @generated
	 */
	void setIdValueWriterName(String value);

} // IdentityInfo
