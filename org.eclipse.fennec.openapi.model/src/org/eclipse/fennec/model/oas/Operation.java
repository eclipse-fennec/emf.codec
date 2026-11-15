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
 * A representation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getTags <em>Tags</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getExternalDocs <em>External Docs</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getOperationId <em>Operation Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getRequestBody <em>Request Body</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getResponses <em>Responses</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getCallbacks <em>Callbacks</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getSecurity <em>Security</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getServers <em>Servers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getMethod <em>Method</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Operation#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Operation extends EObject {
	/**
	 * Returns the value of the '<em><b>Tags</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tags</em>' attribute list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Tags()
	 * @model
	 * @generated
	 */
	EList<String> getTags();

	/**
	 * Returns the value of the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Summary</em>' attribute.
	 * @see #setSummary(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Summary()
	 * @model
	 * @generated
	 */
	String getSummary();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Operation#getSummary <em>Summary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Summary</em>' attribute.
	 * @see #getSummary()
	 * @generated
	 */
	void setSummary(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Operation#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External Docs</em>' containment reference.
	 * @see #setExternalDocs(ExternalDocumentation)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_ExternalDocs()
	 * @model containment="true"
	 * @generated
	 */
	ExternalDocumentation getExternalDocs();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Operation#getExternalDocs <em>External Docs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>External Docs</em>' containment reference.
	 * @see #getExternalDocs()
	 * @generated
	 */
	void setExternalDocs(ExternalDocumentation value);

	/**
	 * Returns the value of the '<em><b>Operation Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operation Id</em>' attribute.
	 * @see #setOperationId(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_OperationId()
	 * @model
	 * @generated
	 */
	String getOperationId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Operation#getOperationId <em>Operation Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operation Id</em>' attribute.
	 * @see #getOperationId()
	 * @generated
	 */
	void setOperationId(String value);

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.Parameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' containment reference list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Parameters()
	 * @model containment="true"
	 * @generated
	 */
	EList<Parameter> getParameters();

	/**
	 * Returns the value of the '<em><b>Request Body</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Request Body</em>' containment reference.
	 * @see #setRequestBody(RequestBody)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_RequestBody()
	 * @model containment="true"
	 * @generated
	 */
	RequestBody getRequestBody();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Operation#getRequestBody <em>Request Body</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Request Body</em>' containment reference.
	 * @see #getRequestBody()
	 * @generated
	 */
	void setRequestBody(RequestBody value);

	/**
	 * Returns the value of the '<em><b>Responses</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Response},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Responses</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Responses()
	 * @model mapType="org.eclipse.fennec.model.oas.ResponseEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Response&gt;"
	 * @generated
	 */
	EMap<String, Response> getResponses();

	/**
	 * Returns the value of the '<em><b>Callbacks</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Callback},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Callbacks</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Callbacks()
	 * @model mapType="org.eclipse.fennec.model.oas.CallbackEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Callback&gt;"
	 * @generated
	 */
	EMap<String, Callback> getCallbacks();

	/**
	 * Returns the value of the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deprecated</em>' attribute.
	 * @see #setDeprecated(boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Deprecated()
	 * @model
	 * @generated
	 */
	boolean isDeprecated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Operation#isDeprecated <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deprecated</em>' attribute.
	 * @see #isDeprecated()
	 * @generated
	 */
	void setDeprecated(boolean value);

	/**
	 * Returns the value of the '<em><b>Security</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.SecurityRequirement}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Security</em>' containment reference list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Security()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='securityRequirement' valueWriterName='securityRequirement'"
	 * @generated
	 */
	EList<SecurityRequirement> getSecurity();

	/**
	 * Returns the value of the '<em><b>Servers</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.Server}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Servers</em>' containment reference list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Servers()
	 * @model containment="true"
	 * @generated
	 */
	EList<Server> getServers();

	/**
	 * Returns the value of the '<em><b>Method</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * HTTP method, from the member the operation was read from (GET, ..., QUERY or the additionalOperations key); never written.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Method</em>' attribute.
	 * @see #setMethod(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Method()
	 * @model annotation="http://eclipse.org/fennec/codec ignoreWrite='true'"
	 * @generated
	 */
	String getMethod();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Operation#getMethod <em>Method</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Method</em>' attribute.
	 * @see #getMethod()
	 * @generated
	 */
	void setMethod(String value);

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getOperation_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // Operation
