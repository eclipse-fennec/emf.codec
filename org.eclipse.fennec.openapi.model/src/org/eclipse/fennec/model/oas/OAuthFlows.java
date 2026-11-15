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
package org.eclipse.fennec.model.oas;

import org.eclipse.emf.common.util.EMap;

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
 *   <li>{@link org.eclipse.fennec.model.oas.OAuthFlows#getImplicit <em>Implicit</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OAuthFlows#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OAuthFlows#getClientCredentials <em>Client Credentials</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OAuthFlows#getAuthorizationCode <em>Authorization Code</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OAuthFlows#getDeviceAuthorization <em>Device Authorization</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OAuthFlows#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getOAuthFlows()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOAuthFlows_Implicit()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlow getImplicit();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OAuthFlows#getImplicit <em>Implicit</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOAuthFlows_Password()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlow getPassword();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OAuthFlows#getPassword <em>Password</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOAuthFlows_ClientCredentials()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlow getClientCredentials();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OAuthFlows#getClientCredentials <em>Client Credentials</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOAuthFlows_AuthorizationCode()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlow getAuthorizationCode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OAuthFlows#getAuthorizationCode <em>Authorization Code</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Authorization Code</em>' containment reference.
	 * @see #getAuthorizationCode()
	 * @generated
	 */
	void setAuthorizationCode(OAuthFlow value);

	/**
	 * Returns the value of the '<em><b>Device Authorization</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Device Authorization</em>' containment reference.
	 * @see #setDeviceAuthorization(OAuthFlow)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOAuthFlows_DeviceAuthorization()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	OAuthFlow getDeviceAuthorization();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OAuthFlows#getDeviceAuthorization <em>Device Authorization</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Device Authorization</em>' containment reference.
	 * @see #getDeviceAuthorization()
	 * @generated
	 */
	void setDeviceAuthorization(OAuthFlow value);

	/**
	 * Returns the value of the '<em><b>Extensions</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Object},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Specification Extensions (x-...): name to JSON value.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Extensions</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOAuthFlows_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // OAuthFlows
