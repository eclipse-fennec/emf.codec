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
 * A representation of the model object '<em><b>Security Scheme</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.SecurityScheme#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.SecurityScheme#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.SecurityScheme#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.SecurityScheme#getIn <em>In</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.SecurityScheme#getScheme <em>Scheme</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.SecurityScheme#getBearerFormat <em>Bearer Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.SecurityScheme#getFlows <em>Flows</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.SecurityScheme#getOpenIdConnectUrl <em>Open Id Connect Url</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSecurityScheme()
 * @model
 * @generated
 */
@ProviderType
public interface SecurityScheme extends EObject {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.openapi.SecuritySchemeType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.SecuritySchemeType
	 * @see #setType(SecuritySchemeType)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSecurityScheme_Type()
	 * @model required="true"
	 * @generated
	 */
	SecuritySchemeType getType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.SecuritySchemeType
	 * @see #getType()
	 * @generated
	 */
	void setType(SecuritySchemeType value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSecurityScheme_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * REQUIRED for apiKey. The name of the header, query or cookie parameter.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSecurityScheme_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>In</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.openapi.ApiKeyLocation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * REQUIRED for apiKey. Location of the API key.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>In</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.ApiKeyLocation
	 * @see #setIn(ApiKeyLocation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSecurityScheme_In()
	 * @model
	 * @generated
	 */
	ApiKeyLocation getIn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getIn <em>In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>In</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.ApiKeyLocation
	 * @see #getIn()
	 * @generated
	 */
	void setIn(ApiKeyLocation value);

	/**
	 * Returns the value of the '<em><b>Scheme</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * REQUIRED for http. HTTP Authorization scheme (e.g., basic, bearer).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Scheme</em>' attribute.
	 * @see #setScheme(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSecurityScheme_Scheme()
	 * @model
	 * @generated
	 */
	String getScheme();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getScheme <em>Scheme</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Scheme</em>' attribute.
	 * @see #getScheme()
	 * @generated
	 */
	void setScheme(String value);

	/**
	 * Returns the value of the '<em><b>Bearer Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Hint for http/bearer. Format of the bearer token (e.g., JWT).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Bearer Format</em>' attribute.
	 * @see #setBearerFormat(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSecurityScheme_BearerFormat()
	 * @model
	 * @generated
	 */
	String getBearerFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getBearerFormat <em>Bearer Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bearer Format</em>' attribute.
	 * @see #getBearerFormat()
	 * @generated
	 */
	void setBearerFormat(String value);

	/**
	 * Returns the value of the '<em><b>Flows</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * REQUIRED for oauth2. OAuth flow configuration.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Flows</em>' containment reference.
	 * @see #setFlows(OAuthFlows)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSecurityScheme_Flows()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlows getFlows();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getFlows <em>Flows</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Flows</em>' containment reference.
	 * @see #getFlows()
	 * @generated
	 */
	void setFlows(OAuthFlows value);

	/**
	 * Returns the value of the '<em><b>Open Id Connect Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * REQUIRED for openIdConnect. OpenId Connect discovery URL.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Open Id Connect Url</em>' attribute.
	 * @see #setOpenIdConnectUrl(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getSecurityScheme_OpenIdConnectUrl()
	 * @model
	 * @generated
	 */
	String getOpenIdConnectUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.SecurityScheme#getOpenIdConnectUrl <em>Open Id Connect Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Open Id Connect Url</em>' attribute.
	 * @see #getOpenIdConnectUrl()
	 * @generated
	 */
	void setOpenIdConnectUrl(String value);

} // SecurityScheme
