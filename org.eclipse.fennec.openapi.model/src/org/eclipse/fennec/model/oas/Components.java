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

import org.eclipse.fennec.codec.jsonschema.model.Schema;

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
 *   <li>{@link org.eclipse.fennec.model.oas.Components#getSchemasPackage <em>Schemas Package</em>}</li>
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
	 * Returns the value of the '<em><b>Schemas</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * components/schemas, as written (JSON Schema model, #319).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Schemas</em>' map.
	 * @see #isSetSchemas()
	 * @see #unsetSchemas()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Schemas()
	 * @model mapType="org.eclipse.fennec.model.oas.SchemaEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.codec.jsonschema.model.Schema&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Schema> getSchemas();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getSchemas <em>Schemas</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSchemas()
	 * @see #getSchemas()
	 * @generated
	 */
	void unsetSchemas();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getSchemas <em>Schemas</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Schemas</em>' map is set.
	 * @see #unsetSchemas()
	 * @see #getSchemas()
	 * @generated
	 */
	boolean isSetSchemas();

	/**
	 * Returns the value of the '<em><b>Schemas Package</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The Ecore derived from the schemas: a classifier per named schema; its sub-package inline holds the classifiers of inline schemas. Set when a description is loaded, never written.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Schemas Package</em>' containment reference.
	 * @see #setSchemasPackage(EPackage)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_SchemasPackage()
	 * @model containment="true" transient="true"
	 * @generated
	 */
	EPackage getSchemasPackage();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Components#getSchemasPackage <em>Schemas Package</em>}' containment reference.
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
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Response},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Responses</em>' map.
	 * @see #isSetResponses()
	 * @see #unsetResponses()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Responses()
	 * @model mapType="org.eclipse.fennec.model.oas.ResponseEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Response&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Response> getResponses();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getResponses <em>Responses</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetResponses()
	 * @see #getResponses()
	 * @generated
	 */
	void unsetResponses();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getResponses <em>Responses</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Responses</em>' map is set.
	 * @see #unsetResponses()
	 * @see #getResponses()
	 * @generated
	 */
	boolean isSetResponses();

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Parameter},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' map.
	 * @see #isSetParameters()
	 * @see #unsetParameters()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Parameters()
	 * @model mapType="org.eclipse.fennec.model.oas.ParameterEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Parameter&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Parameter> getParameters();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getParameters <em>Parameters</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetParameters()
	 * @see #getParameters()
	 * @generated
	 */
	void unsetParameters();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getParameters <em>Parameters</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Parameters</em>' map is set.
	 * @see #unsetParameters()
	 * @see #getParameters()
	 * @generated
	 */
	boolean isSetParameters();

	/**
	 * Returns the value of the '<em><b>Examples</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Example},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Examples</em>' map.
	 * @see #isSetExamples()
	 * @see #unsetExamples()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Examples()
	 * @model mapType="org.eclipse.fennec.model.oas.ExampleEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Example&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Example> getExamples();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getExamples <em>Examples</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExamples()
	 * @see #getExamples()
	 * @generated
	 */
	void unsetExamples();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getExamples <em>Examples</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Examples</em>' map is set.
	 * @see #unsetExamples()
	 * @see #getExamples()
	 * @generated
	 */
	boolean isSetExamples();

	/**
	 * Returns the value of the '<em><b>Request Bodies</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.RequestBody},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Request Bodies</em>' map.
	 * @see #isSetRequestBodies()
	 * @see #unsetRequestBodies()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_RequestBodies()
	 * @model mapType="org.eclipse.fennec.model.oas.RequestBodyEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.RequestBody&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, RequestBody> getRequestBodies();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getRequestBodies <em>Request Bodies</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRequestBodies()
	 * @see #getRequestBodies()
	 * @generated
	 */
	void unsetRequestBodies();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getRequestBodies <em>Request Bodies</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Request Bodies</em>' map is set.
	 * @see #unsetRequestBodies()
	 * @see #getRequestBodies()
	 * @generated
	 */
	boolean isSetRequestBodies();

	/**
	 * Returns the value of the '<em><b>Headers</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Header},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Headers</em>' map.
	 * @see #isSetHeaders()
	 * @see #unsetHeaders()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Headers()
	 * @model mapType="org.eclipse.fennec.model.oas.HeaderEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Header&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Header> getHeaders();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getHeaders <em>Headers</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetHeaders()
	 * @see #getHeaders()
	 * @generated
	 */
	void unsetHeaders();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getHeaders <em>Headers</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Headers</em>' map is set.
	 * @see #unsetHeaders()
	 * @see #getHeaders()
	 * @generated
	 */
	boolean isSetHeaders();

	/**
	 * Returns the value of the '<em><b>Security Schemes</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.SecurityScheme},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Security Schemes</em>' map.
	 * @see #isSetSecuritySchemes()
	 * @see #unsetSecuritySchemes()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_SecuritySchemes()
	 * @model mapType="org.eclipse.fennec.model.oas.SecuritySchemeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.SecurityScheme&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, SecurityScheme> getSecuritySchemes();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getSecuritySchemes <em>Security Schemes</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSecuritySchemes()
	 * @see #getSecuritySchemes()
	 * @generated
	 */
	void unsetSecuritySchemes();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getSecuritySchemes <em>Security Schemes</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Security Schemes</em>' map is set.
	 * @see #unsetSecuritySchemes()
	 * @see #getSecuritySchemes()
	 * @generated
	 */
	boolean isSetSecuritySchemes();

	/**
	 * Returns the value of the '<em><b>Links</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Link},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Links</em>' map.
	 * @see #isSetLinks()
	 * @see #unsetLinks()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Links()
	 * @model mapType="org.eclipse.fennec.model.oas.LinkEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Link&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Link> getLinks();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getLinks <em>Links</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetLinks()
	 * @see #getLinks()
	 * @generated
	 */
	void unsetLinks();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getLinks <em>Links</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Links</em>' map is set.
	 * @see #unsetLinks()
	 * @see #getLinks()
	 * @generated
	 */
	boolean isSetLinks();

	/**
	 * Returns the value of the '<em><b>Callbacks</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Callback},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Callbacks</em>' map.
	 * @see #isSetCallbacks()
	 * @see #unsetCallbacks()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_Callbacks()
	 * @model mapType="org.eclipse.fennec.model.oas.CallbackEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Callback&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Callback> getCallbacks();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getCallbacks <em>Callbacks</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetCallbacks()
	 * @see #getCallbacks()
	 * @generated
	 */
	void unsetCallbacks();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getCallbacks <em>Callbacks</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Callbacks</em>' map is set.
	 * @see #unsetCallbacks()
	 * @see #getCallbacks()
	 * @generated
	 */
	boolean isSetCallbacks();

	/**
	 * Returns the value of the '<em><b>Path Items</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.PathItem},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path Items</em>' map.
	 * @see #isSetPathItems()
	 * @see #unsetPathItems()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_PathItems()
	 * @model mapType="org.eclipse.fennec.model.oas.PathItemEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.PathItem&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	EMap<String, PathItem> getPathItems();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getPathItems <em>Path Items</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPathItems()
	 * @see #getPathItems()
	 * @generated
	 */
	void unsetPathItems();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getPathItems <em>Path Items</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Path Items</em>' map is set.
	 * @see #unsetPathItems()
	 * @see #getPathItems()
	 * @generated
	 */
	boolean isSetPathItems();

	/**
	 * Returns the value of the '<em><b>Media Types</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.MediaType},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Media Types</em>' map.
	 * @see #isSetMediaTypes()
	 * @see #unsetMediaTypes()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getComponents_MediaTypes()
	 * @model mapType="org.eclipse.fennec.model.oas.MediaTypeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.MediaType&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	EMap<String, MediaType> getMediaTypes();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Components#getMediaTypes <em>Media Types</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMediaTypes()
	 * @see #getMediaTypes()
	 * @generated
	 */
	void unsetMediaTypes();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Components#getMediaTypes <em>Media Types</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Media Types</em>' map is set.
	 * @see #unsetMediaTypes()
	 * @see #getMediaTypes()
	 * @generated
	 */
	boolean isSetMediaTypes();

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
