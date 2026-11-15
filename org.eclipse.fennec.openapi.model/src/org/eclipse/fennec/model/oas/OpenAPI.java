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
 * Root of an OpenAPI 3.0, 3.1 or 3.2 description.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getOpenapi <em>Openapi</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getSelf <em>Self</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getInfo <em>Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getJsonSchemaDialect <em>Json Schema Dialect</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getServers <em>Servers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getPaths <em>Paths</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getWebhooks <em>Webhooks</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getComponents <em>Components</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getSecurity <em>Security</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getTags <em>Tags</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getExternalDocs <em>External Docs</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.OpenAPI#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface OpenAPI extends EObject {
	/**
	 * Returns the value of the '<em><b>Openapi</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Openapi</em>' attribute.
	 * @see #setOpenapi(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Openapi()
	 * @model required="true"
	 * @generated
	 */
	String getOpenapi();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OpenAPI#getOpenapi <em>Openapi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Openapi</em>' attribute.
	 * @see #getOpenapi()
	 * @generated
	 */
	void setOpenapi(String value);

	/**
	 * Returns the value of the '<em><b>Self</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * URI of the document itself, the base for relative references.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Self</em>' attribute.
	 * @see #setSelf(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Self()
	 * @model annotation="http://eclipse.org/fennec/codec key='$self'"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	String getSelf();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OpenAPI#getSelf <em>Self</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Self</em>' attribute.
	 * @see #getSelf()
	 * @generated
	 */
	void setSelf(String value);

	/**
	 * Returns the value of the '<em><b>Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Info</em>' containment reference.
	 * @see #setInfo(Info)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Info()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Info getInfo();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OpenAPI#getInfo <em>Info</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Info</em>' containment reference.
	 * @see #getInfo()
	 * @generated
	 */
	void setInfo(Info value);

	/**
	 * Returns the value of the '<em><b>Json Schema Dialect</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Json Schema Dialect</em>' attribute.
	 * @see #setJsonSchemaDialect(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_JsonSchemaDialect()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	String getJsonSchemaDialect();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OpenAPI#getJsonSchemaDialect <em>Json Schema Dialect</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Json Schema Dialect</em>' attribute.
	 * @see #getJsonSchemaDialect()
	 * @generated
	 */
	void setJsonSchemaDialect(String value);

	/**
	 * Returns the value of the '<em><b>Servers</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.Server}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Servers</em>' containment reference list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Servers()
	 * @model containment="true"
	 * @generated
	 */
	EList<Server> getServers();

	/**
	 * Returns the value of the '<em><b>Paths</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.PathItem},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Paths</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Paths()
	 * @model mapType="org.eclipse.fennec.model.oas.PathItemEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.PathItem&gt;"
	 * @generated
	 */
	EMap<String, PathItem> getPaths();

	/**
	 * Returns the value of the '<em><b>Webhooks</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.PathItem},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Webhooks</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Webhooks()
	 * @model mapType="org.eclipse.fennec.model.oas.PathItemEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.PathItem&gt;"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	EMap<String, PathItem> getWebhooks();

	/**
	 * Returns the value of the '<em><b>Components</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Components</em>' containment reference.
	 * @see #setComponents(Components)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Components()
	 * @model containment="true"
	 * @generated
	 */
	Components getComponents();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OpenAPI#getComponents <em>Components</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Components</em>' containment reference.
	 * @see #getComponents()
	 * @generated
	 */
	void setComponents(Components value);

	/**
	 * Returns the value of the '<em><b>Security</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.SecurityRequirement}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Security</em>' containment reference list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Security()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='securityRequirement' valueWriterName='securityRequirement'"
	 * @generated
	 */
	EList<SecurityRequirement> getSecurity();

	/**
	 * Returns the value of the '<em><b>Tags</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.Tag}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tags</em>' containment reference list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Tags()
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_ExternalDocs()
	 * @model containment="true"
	 * @generated
	 */
	ExternalDocumentation getExternalDocs();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.OpenAPI#getExternalDocs <em>External Docs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>External Docs</em>' containment reference.
	 * @see #getExternalDocs()
	 * @generated
	 */
	void setExternalDocs(ExternalDocumentation value);

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOpenAPI_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // OpenAPI
