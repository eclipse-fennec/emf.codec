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
 * A representation of the model object '<em><b>Security Scheme</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getIn <em>In</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getScheme <em>Scheme</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getBearerFormat <em>Bearer Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getFlows <em>Flows</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getOpenIdConnectUrl <em>Open Id Connect Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getOauth2MetadataUrl <em>Oauth2 Metadata Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityScheme#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface SecurityScheme extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of a Reference Object; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getRef <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' attribute.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(String value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The object an internal $ref points at in components; set when a description is loaded, never written.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(SecurityScheme)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Target()
	 * @model transient="true"
	 * @generated
	 */
	SecurityScheme getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(SecurityScheme value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.oas.SecuritySchemeType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.SecuritySchemeType
	 * @see #setType(SecuritySchemeType)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Type()
	 * @model
	 * @generated
	 */
	SecuritySchemeType getType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.SecuritySchemeType
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getDescription <em>Description</em>}' attribute.
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
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>In</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.oas.ApiKeyLocation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>In</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.ApiKeyLocation
	 * @see #setIn(ApiKeyLocation)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_In()
	 * @model
	 * @generated
	 */
	ApiKeyLocation getIn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getIn <em>In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>In</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.ApiKeyLocation
	 * @see #getIn()
	 * @generated
	 */
	void setIn(ApiKeyLocation value);

	/**
	 * Returns the value of the '<em><b>Scheme</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Scheme</em>' attribute.
	 * @see #setScheme(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Scheme()
	 * @model
	 * @generated
	 */
	String getScheme();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getScheme <em>Scheme</em>}' attribute.
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
	 * @return the value of the '<em>Bearer Format</em>' attribute.
	 * @see #setBearerFormat(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_BearerFormat()
	 * @model
	 * @generated
	 */
	String getBearerFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getBearerFormat <em>Bearer Format</em>}' attribute.
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
	 * @return the value of the '<em>Flows</em>' containment reference.
	 * @see #setFlows(OAuthFlows)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Flows()
	 * @model containment="true"
	 * @generated
	 */
	OAuthFlows getFlows();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getFlows <em>Flows</em>}' containment reference.
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
	 * @return the value of the '<em>Open Id Connect Url</em>' attribute.
	 * @see #setOpenIdConnectUrl(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_OpenIdConnectUrl()
	 * @model
	 * @generated
	 */
	String getOpenIdConnectUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getOpenIdConnectUrl <em>Open Id Connect Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Open Id Connect Url</em>' attribute.
	 * @see #getOpenIdConnectUrl()
	 * @generated
	 */
	void setOpenIdConnectUrl(String value);

	/**
	 * Returns the value of the '<em><b>Oauth2 Metadata Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Oauth2 Metadata Url</em>' attribute.
	 * @see #setOauth2MetadataUrl(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Oauth2MetadataUrl()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	String getOauth2MetadataUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#getOauth2MetadataUrl <em>Oauth2 Metadata Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Oauth2 Metadata Url</em>' attribute.
	 * @see #getOauth2MetadataUrl()
	 * @generated
	 */
	void setOauth2MetadataUrl(String value);

	/**
	 * Returns the value of the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deprecated</em>' attribute.
	 * @see #setDeprecated(boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Deprecated()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	boolean isDeprecated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.SecurityScheme#isDeprecated <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deprecated</em>' attribute.
	 * @see #isDeprecated()
	 * @generated
	 */
	void setDeprecated(boolean value);

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityScheme_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // SecurityScheme
