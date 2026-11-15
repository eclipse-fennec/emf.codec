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
 * A representation of the model object '<em><b>Request Body</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.RequestBody#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.RequestBody#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.RequestBody#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.RequestBody#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.RequestBody#getContent <em>Content</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.RequestBody#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.RequestBody#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getRequestBody()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface RequestBody extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of a Reference Object; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getRequestBody_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.RequestBody#getRef <em>Ref</em>}' attribute.
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
	 * @see #setTarget(RequestBody)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getRequestBody_Target()
	 * @model transient="true"
	 * @generated
	 */
	RequestBody getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.RequestBody#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(RequestBody value);

	/**
	 * Returns the value of the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The summary of a Reference Object: overrides the summary of the referenced component.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Summary</em>' attribute.
	 * @see #setSummary(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getRequestBody_Summary()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	String getSummary();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.RequestBody#getSummary <em>Summary</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getRequestBody_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.RequestBody#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Content</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.MediaType},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content</em>' map.
	 * @see #isSetContent()
	 * @see #unsetContent()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getRequestBody_Content()
	 * @model mapType="org.eclipse.fennec.model.oas.MediaTypeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.MediaType&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, MediaType> getContent();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.RequestBody#getContent <em>Content</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetContent()
	 * @see #getContent()
	 * @generated
	 */
	void unsetContent();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.RequestBody#getContent <em>Content</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Content</em>' map is set.
	 * @see #unsetContent()
	 * @see #getContent()
	 * @generated
	 */
	boolean isSetContent();

	/**
	 * Returns the value of the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required</em>' attribute.
	 * @see #isSetRequired()
	 * @see #unsetRequired()
	 * @see #setRequired(Boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getRequestBody_Required()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeDefault='true'"
	 * @generated
	 */
	Boolean getRequired();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.RequestBody#getRequired <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required</em>' attribute.
	 * @see #isSetRequired()
	 * @see #unsetRequired()
	 * @see #getRequired()
	 * @generated
	 */
	void setRequired(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.RequestBody#getRequired <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRequired()
	 * @see #getRequired()
	 * @see #setRequired(Boolean)
	 * @generated
	 */
	void unsetRequired();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.RequestBody#getRequired <em>Required</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Required</em>' attribute is set.
	 * @see #unsetRequired()
	 * @see #getRequired()
	 * @see #setRequired(Boolean)
	 * @generated
	 */
	boolean isSetRequired();

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getRequestBody_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // RequestBody
