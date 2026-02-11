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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Open API</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This is the root object of the OpenAPI document.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.OpenAPI#getOpenapi <em>Openapi</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OpenAPI#getInfo <em>Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OpenAPI#getServers <em>Servers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OpenAPI#getPaths <em>Paths</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OpenAPI#getComponents <em>Components</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OpenAPI#getSecurity <em>Security</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OpenAPI#getTags <em>Tags</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.OpenAPI#getExternalDocs <em>External Docs</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOpenAPI()
 * @model
 * @generated
 */
@ProviderType
public interface OpenAPI extends EObject {
	/**
	 * Returns the value of the '<em><b>Openapi</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * REQUIRED. OpenAPI specification version (e.g., '3.0.3').
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Openapi</em>' attribute.
	 * @see #setOpenapi(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOpenAPI_Openapi()
	 * @model required="true"
	 * @generated
	 */
	String getOpenapi();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OpenAPI#getOpenapi <em>Openapi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Openapi</em>' attribute.
	 * @see #getOpenapi()
	 * @generated
	 */
	void setOpenapi(String value);

	/**
	 * Returns the value of the '<em><b>Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * REQUIRED. Provides metadata about the API.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Info</em>' containment reference.
	 * @see #setInfo(Info)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOpenAPI_Info()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Info getInfo();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OpenAPI#getInfo <em>Info</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Info</em>' containment reference.
	 * @see #getInfo()
	 * @generated
	 */
	void setInfo(Info value);

	/**
	 * Returns the value of the '<em><b>Servers</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.openapi.Server}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Servers</em>' containment reference list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOpenAPI_Servers()
	 * @model containment="true"
	 * @generated
	 */
	EList<Server> getServers();

	/**
	 * Returns the value of the '<em><b>Paths</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.PathItem},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The available paths and operations for the API.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Paths</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOpenAPI_Paths()
	 * @model mapType="org.eclipse.fennec.model.openapi.PathEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.PathItem&gt;"
	 * @generated
	 */
	EMap<String, PathItem> getPaths();

	/**
	 * Returns the value of the '<em><b>Components</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Components</em>' containment reference.
	 * @see #setComponents(Components)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOpenAPI_Components()
	 * @model containment="true"
	 * @generated
	 */
	Components getComponents();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OpenAPI#getComponents <em>Components</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Components</em>' containment reference.
	 * @see #getComponents()
	 * @generated
	 */
	void setComponents(Components value);

	/**
	 * Returns the value of the '<em><b>Security</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.openapi.SecurityRequirement}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Global security requirements that apply to all operations.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Security</em>' containment reference list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOpenAPI_Security()
	 * @model containment="true"
	 * @generated
	 */
	EList<SecurityRequirement> getSecurity();

	/**
	 * Returns the value of the '<em><b>Tags</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.openapi.Tag}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tags</em>' containment reference list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOpenAPI_Tags()
	 * @model containment="true"
	 * @generated
	 */
	EList<Tag> getTags();

	/**
	 * Returns the value of the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External Docs</em>' containment reference.
	 * @see #setExternalDocs(ExternalDocumentation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getOpenAPI_ExternalDocs()
	 * @model containment="true"
	 * @generated
	 */
	ExternalDocumentation getExternalDocs();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.OpenAPI#getExternalDocs <em>External Docs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>External Docs</em>' containment reference.
	 * @see #getExternalDocs()
	 * @generated
	 */
	void setExternalDocs(ExternalDocumentation value);

} // OpenAPI
