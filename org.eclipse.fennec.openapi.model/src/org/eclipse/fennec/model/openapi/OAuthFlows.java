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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>OAuth Flows</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.OAuthFlows#getImplicit <em>Implicit</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OAuthFlows#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OAuthFlows#getClientCredentials <em>Client Credentials</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OAuthFlows#getAuthorizationCode <em>Authorization Code</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlows()
 * @model
 * @generated
 */
@ProviderType
public interface OAuthFlows extends EObject {
	/**
	 * Returns the value of the '<em><b>Implicit</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Implicit</em>' containment reference.
	 * @see #setImplicit(OAuthFlow)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlows_Implicit()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlow getImplicit();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OAuthFlows#getImplicit <em>Implicit</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Implicit</em>' containment reference.
	 * @see #getImplicit()
	 * @generated
	 */
	void setImplicit(OAuthFlow value);

	/**
	 * Returns the value of the '<em><b>Password</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Password</em>' containment reference.
	 * @see #setPassword(OAuthFlow)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlows_Password()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlow getPassword();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OAuthFlows#getPassword <em>Password</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Password</em>' containment reference.
	 * @see #getPassword()
	 * @generated
	 */
	void setPassword(OAuthFlow value);

	/**
	 * Returns the value of the '<em><b>Client Credentials</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Client Credentials</em>' containment reference.
	 * @see #setClientCredentials(OAuthFlow)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlows_ClientCredentials()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlow getClientCredentials();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OAuthFlows#getClientCredentials <em>Client Credentials</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Client Credentials</em>' containment reference.
	 * @see #getClientCredentials()
	 * @generated
	 */
	void setClientCredentials(OAuthFlow value);

	/**
	 * Returns the value of the '<em><b>Authorization Code</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Authorization Code</em>' containment reference.
	 * @see #setAuthorizationCode(OAuthFlow)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOAuthFlows_AuthorizationCode()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlow getAuthorizationCode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OAuthFlows#getAuthorizationCode <em>Authorization Code</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Authorization Code</em>' containment reference.
	 * @see #getAuthorizationCode()
	 * @generated
	 */
	void setAuthorizationCode(OAuthFlow value);

} // OAuthFlows
