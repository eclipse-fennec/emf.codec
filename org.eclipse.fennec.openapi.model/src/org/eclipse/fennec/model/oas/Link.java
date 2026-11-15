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
 * A representation of the model object '<em><b>Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.Link#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Link#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Link#getOperationRef <em>Operation Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Link#getOperationId <em>Operation Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Link#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Link#getRequestBody <em>Request Body</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Link#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Link#getServer <em>Server</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Link#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getLink()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Link extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of a Reference Object; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getLink_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Link#getRef <em>Ref</em>}' attribute.
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
	 * @see #setTarget(Link)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getLink_Target()
	 * @model transient="true"
	 * @generated
	 */
	Link getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Link#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(Link value);

	/**
	 * Returns the value of the '<em><b>Operation Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operation Ref</em>' attribute.
	 * @see #setOperationRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getLink_OperationRef()
	 * @model
	 * @generated
	 */
	String getOperationRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Link#getOperationRef <em>Operation Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operation Ref</em>' attribute.
	 * @see #getOperationRef()
	 * @generated
	 */
	void setOperationRef(String value);

	/**
	 * Returns the value of the '<em><b>Operation Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operation Id</em>' attribute.
	 * @see #setOperationId(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getLink_OperationId()
	 * @model
	 * @generated
	 */
	String getOperationId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Link#getOperationId <em>Operation Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operation Id</em>' attribute.
	 * @see #getOperationId()
	 * @generated
	 */
	void setOperationId(String value);

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Object},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' map.
	 * @see #isSetParameters()
	 * @see #unsetParameters()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getLink_Parameters()
	 * @model mapType="org.eclipse.fennec.model.oas.AnyEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Object> getParameters();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Link#getParameters <em>Parameters</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetParameters()
	 * @see #getParameters()
	 * @generated
	 */
	void unsetParameters();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Link#getParameters <em>Parameters</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Parameters</em>' map is set.
	 * @see #unsetParameters()
	 * @see #getParameters()
	 * @generated
	 */
	boolean isSetParameters();

	/**
	 * Returns the value of the '<em><b>Request Body</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Request Body</em>' attribute.
	 * @see #isSetRequestBody()
	 * @see #unsetRequestBody()
	 * @see #setRequestBody(Object)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getLink_RequestBody()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeNull='true'"
	 * @generated
	 */
	Object getRequestBody();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Link#getRequestBody <em>Request Body</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Request Body</em>' attribute.
	 * @see #isSetRequestBody()
	 * @see #unsetRequestBody()
	 * @see #getRequestBody()
	 * @generated
	 */
	void setRequestBody(Object value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Link#getRequestBody <em>Request Body</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRequestBody()
	 * @see #getRequestBody()
	 * @see #setRequestBody(Object)
	 * @generated
	 */
	void unsetRequestBody();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Link#getRequestBody <em>Request Body</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Request Body</em>' attribute is set.
	 * @see #unsetRequestBody()
	 * @see #getRequestBody()
	 * @see #setRequestBody(Object)
	 * @generated
	 */
	boolean isSetRequestBody();

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getLink_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Link#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Server</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Server</em>' containment reference.
	 * @see #setServer(Server)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getLink_Server()
	 * @model containment="true"
	 * @generated
	 */
	Server getServer();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Link#getServer <em>Server</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Server</em>' containment reference.
	 * @see #getServer()
	 * @generated
	 */
	void setServer(Server value);

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getLink_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // Link
