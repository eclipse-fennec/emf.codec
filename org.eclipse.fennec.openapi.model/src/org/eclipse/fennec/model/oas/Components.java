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
import org.eclipse.emf.ecore.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Components</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getSchemas <em>Schemas</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getResponses <em>Responses</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getRequestBodies <em>Request Bodies</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getHeaders <em>Headers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getSecuritySchemes <em>Security Schemes</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getLinks <em>Links</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getCallbacks <em>Callbacks</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getPathItems <em>Path Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getMediaTypes <em>Media Types</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Components extends EObject {
	/**
	 * Returns the value of the '<em><b>Schemas</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * components/schemas as EPackage; its classifiers are the named schemas.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Schemas</em>' containment reference.
	 * @see #setSchemas(EPackage)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Schemas()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec key='schemas' valueReaderName='jsonSchemaToEPackage' valueWriterName='ePackageToOpenApiSchemas'"
	 * @generated
	 */
	EPackage getSchemas();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Components#getSchemas <em>Schemas</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schemas</em>' containment reference.
	 * @see #getSchemas()
	 * @generated
	 */
	void setSchemas(EPackage value);

	/**
	 * Returns the value of the '<em><b>Responses</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Response},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Responses</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Responses()
	 * @model mapType="org.eclipse.fennec.model.oas.ResponseEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Response&gt;"
	 * @generated
	 */
	EMap<String, Response> getResponses();

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Parameter},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Parameters()
	 * @model mapType="org.eclipse.fennec.model.oas.ParameterEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Parameter&gt;"
	 * @generated
	 */
	EMap<String, Parameter> getParameters();

	/**
	 * Returns the value of the '<em><b>Examples</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Example},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Examples</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Examples()
	 * @model mapType="org.eclipse.fennec.model.oas.ExampleEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Example&gt;"
	 * @generated
	 */
	EMap<String, Example> getExamples();

	/**
	 * Returns the value of the '<em><b>Request Bodies</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.RequestBody},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Request Bodies</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_RequestBodies()
	 * @model mapType="org.eclipse.fennec.model.oas.RequestBodyEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.RequestBody&gt;"
	 * @generated
	 */
	EMap<String, RequestBody> getRequestBodies();

	/**
	 * Returns the value of the '<em><b>Headers</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Header},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Headers</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Headers()
	 * @model mapType="org.eclipse.fennec.model.oas.HeaderEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Header&gt;"
	 * @generated
	 */
	EMap<String, Header> getHeaders();

	/**
	 * Returns the value of the '<em><b>Security Schemes</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.SecurityScheme},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Security Schemes</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_SecuritySchemes()
	 * @model mapType="org.eclipse.fennec.model.oas.SecuritySchemeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.SecurityScheme&gt;"
	 * @generated
	 */
	EMap<String, SecurityScheme> getSecuritySchemes();

	/**
	 * Returns the value of the '<em><b>Links</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Link},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Links</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Links()
	 * @model mapType="org.eclipse.fennec.model.oas.LinkEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Link&gt;"
	 * @generated
	 */
	EMap<String, Link> getLinks();

	/**
	 * Returns the value of the '<em><b>Callbacks</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Callback},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Callbacks</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Callbacks()
	 * @model mapType="org.eclipse.fennec.model.oas.CallbackEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Callback&gt;"
	 * @generated
	 */
	EMap<String, Callback> getCallbacks();

	/**
	 * Returns the value of the '<em><b>Path Items</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.PathItem},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path Items</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_PathItems()
	 * @model mapType="org.eclipse.fennec.model.oas.PathItemEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.PathItem&gt;"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	EMap<String, PathItem> getPathItems();

	/**
	 * Returns the value of the '<em><b>Media Types</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.MediaType},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Media Types</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_MediaTypes()
	 * @model mapType="org.eclipse.fennec.model.oas.MediaTypeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.MediaType&gt;"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	EMap<String, MediaType> getMediaTypes();

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // Components
