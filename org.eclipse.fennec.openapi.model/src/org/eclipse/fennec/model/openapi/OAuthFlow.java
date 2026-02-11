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
 * A representation of the model object '<em><b>OAuth Flow</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.OAuthFlow#getAuthorizationUrl <em>Authorization Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OAuthFlow#getTokenUrl <em>Token Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OAuthFlow#getRefreshUrl <em>Refresh Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OAuthFlow#getScopes <em>Scopes</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlow()
 * @model
 * @generated
 */
@ProviderType
public interface OAuthFlow extends EObject {
	/**
	 * Returns the value of the '<em><b>Authorization Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Authorization Url</em>' attribute.
	 * @see #setAuthorizationUrl(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlow_AuthorizationUrl()
	 * @model
	 * @generated
	 */
	String getAuthorizationUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OAuthFlow#getAuthorizationUrl <em>Authorization Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Authorization Url</em>' attribute.
	 * @see #getAuthorizationUrl()
	 * @generated
	 */
	void setAuthorizationUrl(String value);

	/**
	 * Returns the value of the '<em><b>Token Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Token Url</em>' attribute.
	 * @see #setTokenUrl(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlow_TokenUrl()
	 * @model
	 * @generated
	 */
	String getTokenUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OAuthFlow#getTokenUrl <em>Token Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Token Url</em>' attribute.
	 * @see #getTokenUrl()
	 * @generated
	 */
	void setTokenUrl(String value);

	/**
	 * Returns the value of the '<em><b>Refresh Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Refresh Url</em>' attribute.
	 * @see #setRefreshUrl(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlow_RefreshUrl()
	 * @model
	 * @generated
	 */
	String getRefreshUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OAuthFlow#getRefreshUrl <em>Refresh Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Refresh Url</em>' attribute.
	 * @see #getRefreshUrl()
	 * @generated
	 */
	void setRefreshUrl(String value);

	/**
	 * Returns the value of the '<em><b>Scopes</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Scopes</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlow_Scopes()
	 * @model mapType="org.eclipse.fennec.model.openapi.StringEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;"
	 * @generated
	 */
	EMap<String, String> getScopes();

} // OAuthFlow
