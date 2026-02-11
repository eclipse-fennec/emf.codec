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
import org.eclipse.emf.ecore.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Components</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Holds a set of reusable objects for different aspects of the OAS.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getSchemas <em>Schemas</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getSchemasPackage <em>Schemas Package</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getResponses <em>Responses</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getRequestBodies <em>Request Bodies</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getHeaders <em>Headers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getSecuritySchemes <em>Security Schemes</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getLinks <em>Links</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Components#getCallbacks <em>Callbacks</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents()
 * @model
 * @generated
 */
@ProviderType
public interface Components extends EObject {
	/**
	 * Returns the value of the '<em><b>Schemas</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Schema},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * OpenAPI-conformant JSON Schema representation. Always populated during deserialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Schemas</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_Schemas()
	 * @model mapType="org.eclipse.fennec.model.openapi.SchemaEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Schema&gt;"
	 * @generated
	 */
	EMap<String, Schema> getSchemas();

	/**
	 * Returns the value of the '<em><b>Schemas Package</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * EMF-native representation of schemas. Populated during deserialization if JSON-Schema to EPackage conversion succeeds. Not serialized (derived from schemas).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Schemas Package</em>' containment reference.
	 * @see #setSchemasPackage(EPackage)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_SchemasPackage()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec serialize='false'"
	 * @generated
	 */
	EPackage getSchemasPackage();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Components#getSchemasPackage <em>Schemas Package</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schemas Package</em>' containment reference.
	 * @see #getSchemasPackage()
	 * @generated
	 */
	void setSchemasPackage(EPackage value);

	/**
	 * Returns the value of the '<em><b>Responses</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Response},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Responses</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_Responses()
	 * @model mapType="org.eclipse.fennec.model.openapi.ResponseEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Response&gt;"
	 * @generated
	 */
	EMap<String, Response> getResponses();

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Parameter},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_Parameters()
	 * @model mapType="org.eclipse.fennec.model.openapi.ParameterEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Parameter&gt;"
	 * @generated
	 */
	EMap<String, Parameter> getParameters();

	/**
	 * Returns the value of the '<em><b>Examples</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Example},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Examples</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_Examples()
	 * @model mapType="org.eclipse.fennec.model.openapi.ExampleEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Example&gt;"
	 * @generated
	 */
	EMap<String, Example> getExamples();

	/**
	 * Returns the value of the '<em><b>Request Bodies</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.RequestBody},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Request Bodies</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_RequestBodies()
	 * @model mapType="org.eclipse.fennec.model.openapi.RequestBodyEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.RequestBody&gt;"
	 * @generated
	 */
	EMap<String, RequestBody> getRequestBodies();

	/**
	 * Returns the value of the '<em><b>Headers</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Header},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Headers</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_Headers()
	 * @model mapType="org.eclipse.fennec.model.openapi.HeaderEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Header&gt;"
	 * @generated
	 */
	EMap<String, Header> getHeaders();

	/**
	 * Returns the value of the '<em><b>Security Schemes</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.SecurityScheme},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Security Schemes</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_SecuritySchemes()
	 * @model mapType="org.eclipse.fennec.model.openapi.SecuritySchemeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.SecurityScheme&gt;"
	 * @generated
	 */
	EMap<String, SecurityScheme> getSecuritySchemes();

	/**
	 * Returns the value of the '<em><b>Links</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Link},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Links</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_Links()
	 * @model mapType="org.eclipse.fennec.model.openapi.LinkEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Link&gt;"
	 * @generated
	 */
	EMap<String, Link> getLinks();

	/**
	 * Returns the value of the '<em><b>Callbacks</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Callback},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Callbacks</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getComponents_Callbacks()
	 * @model mapType="org.eclipse.fennec.model.openapi.CallbackEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Callback&gt;"
	 * @generated
	 */
	EMap<String, Callback> getCallbacks();

} // Components
