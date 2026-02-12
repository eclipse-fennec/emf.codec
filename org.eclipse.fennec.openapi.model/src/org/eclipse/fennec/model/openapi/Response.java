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
package org.eclipse.fennec.model.openapi;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Response</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.Response#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Response#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Response#getHeaders <em>Headers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Response#getContent <em>Content</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Response#getLinks <em>Links</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getResponse()
 * @model
 * @generated
 */
@ProviderType
public interface Response extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getResponse_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Response#getRef <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' attribute.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getResponse_Description()
	 * @model required="true"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Response#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Headers</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Header},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Headers</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getResponse_Headers()
	 * @model mapType="org.eclipse.fennec.model.openapi.HeaderEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Header&gt;"
	 * @generated
	 */
	EMap<String, Header> getHeaders();

	/**
	 * Returns the value of the '<em><b>Content</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.MediaType},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getResponse_Content()
	 * @model mapType="org.eclipse.fennec.model.openapi.MediaTypeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.MediaType&gt;"
	 * @generated
	 */
	EMap<String, MediaType> getContent();

	/**
	 * Returns the value of the '<em><b>Links</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Link},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Links</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getResponse_Links()
	 * @model mapType="org.eclipse.fennec.model.openapi.LinkEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Link&gt;"
	 * @generated
	 */
	EMap<String, Link> getLinks();

} // Response
