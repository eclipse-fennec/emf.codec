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
 */
package org.eclipse.fennec.model.openapi;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Encoding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.Encoding#getContentType <em>Content Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Encoding#getHeaders <em>Headers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Encoding#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Encoding#isExplode <em>Explode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Encoding#isAllowReserved <em>Allow Reserved</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getEncoding()
 * @model
 * @generated
 */
@ProviderType
public interface Encoding extends EObject {
	/**
	 * Returns the value of the '<em><b>Content Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content Type</em>' attribute.
	 * @see #setContentType(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getEncoding_ContentType()
	 * @model
	 * @generated
	 */
	String getContentType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Encoding#getContentType <em>Content Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content Type</em>' attribute.
	 * @see #getContentType()
	 * @generated
	 */
	void setContentType(String value);

	/**
	 * Returns the value of the '<em><b>Headers</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Header},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Headers</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getEncoding_Headers()
	 * @model mapType="org.eclipse.fennec.model.openapi.HeaderEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Header&gt;"
	 * @generated
	 */
	EMap<String, Header> getHeaders();

	/**
	 * Returns the value of the '<em><b>Style</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.openapi.ParameterStyle}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.ParameterStyle
	 * @see #setStyle(ParameterStyle)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getEncoding_Style()
	 * @model
	 * @generated
	 */
	ParameterStyle getStyle();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Encoding#getStyle <em>Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.ParameterStyle
	 * @see #getStyle()
	 * @generated
	 */
	void setStyle(ParameterStyle value);

	/**
	 * Returns the value of the '<em><b>Explode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Explode</em>' attribute.
	 * @see #setExplode(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getEncoding_Explode()
	 * @model
	 * @generated
	 */
	boolean isExplode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Encoding#isExplode <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Explode</em>' attribute.
	 * @see #isExplode()
	 * @generated
	 */
	void setExplode(boolean value);

	/**
	 * Returns the value of the '<em><b>Allow Reserved</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Allow Reserved</em>' attribute.
	 * @see #setAllowReserved(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getEncoding_AllowReserved()
	 * @model
	 * @generated
	 */
	boolean isAllowReserved();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Encoding#isAllowReserved <em>Allow Reserved</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Allow Reserved</em>' attribute.
	 * @see #isAllowReserved()
	 * @generated
	 */
	void setAllowReserved(boolean value);

} // Encoding
