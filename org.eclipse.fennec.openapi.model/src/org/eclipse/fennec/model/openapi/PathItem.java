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
package org.eclipse.fennec.model.openapi;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Path Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Describes the operations available on a single path.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getGet <em>Get</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getPut <em>Put</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getPost <em>Post</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getDelete <em>Delete</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getOptions <em>Options</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getHead <em>Head</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getPatch <em>Patch</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getTrace <em>Trace</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getServers <em>Servers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.PathItem#getParameters <em>Parameters</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem()
 * @model
 * @generated
 */
@ProviderType
public interface PathItem extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getRef <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' attribute.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(String value);

	/**
	 * Returns the value of the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Summary</em>' attribute.
	 * @see #setSummary(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Summary()
	 * @model
	 * @generated
	 */
	String getSummary();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getSummary <em>Summary</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Get</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Get</em>' containment reference.
	 * @see #setGet(Operation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Get()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getGet();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getGet <em>Get</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Get</em>' containment reference.
	 * @see #getGet()
	 * @generated
	 */
	void setGet(Operation value);

	/**
	 * Returns the value of the '<em><b>Put</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Put</em>' containment reference.
	 * @see #setPut(Operation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Put()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getPut();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getPut <em>Put</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Put</em>' containment reference.
	 * @see #getPut()
	 * @generated
	 */
	void setPut(Operation value);

	/**
	 * Returns the value of the '<em><b>Post</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Post</em>' containment reference.
	 * @see #setPost(Operation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Post()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getPost();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getPost <em>Post</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Post</em>' containment reference.
	 * @see #getPost()
	 * @generated
	 */
	void setPost(Operation value);

	/**
	 * Returns the value of the '<em><b>Delete</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Delete</em>' containment reference.
	 * @see #setDelete(Operation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Delete()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getDelete();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getDelete <em>Delete</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Delete</em>' containment reference.
	 * @see #getDelete()
	 * @generated
	 */
	void setDelete(Operation value);

	/**
	 * Returns the value of the '<em><b>Options</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Options</em>' containment reference.
	 * @see #setOptions(Operation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Options()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getOptions();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getOptions <em>Options</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Options</em>' containment reference.
	 * @see #getOptions()
	 * @generated
	 */
	void setOptions(Operation value);

	/**
	 * Returns the value of the '<em><b>Head</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Head</em>' containment reference.
	 * @see #setHead(Operation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Head()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getHead();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getHead <em>Head</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Head</em>' containment reference.
	 * @see #getHead()
	 * @generated
	 */
	void setHead(Operation value);

	/**
	 * Returns the value of the '<em><b>Patch</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Patch</em>' containment reference.
	 * @see #setPatch(Operation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Patch()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getPatch();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getPatch <em>Patch</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Patch</em>' containment reference.
	 * @see #getPatch()
	 * @generated
	 */
	void setPatch(Operation value);

	/**
	 * Returns the value of the '<em><b>Trace</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Trace</em>' containment reference.
	 * @see #setTrace(Operation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Trace()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getTrace();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.PathItem#getTrace <em>Trace</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Trace</em>' containment reference.
	 * @see #getTrace()
	 * @generated
	 */
	void setTrace(Operation value);

	/**
	 * Returns the value of the '<em><b>Servers</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.openapi.Server}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Servers</em>' containment reference list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Servers()
	 * @model containment="true"
	 * @generated
	 */
	EList<Server> getServers();

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.openapi.Parameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Parameters applicable for all operations on this path.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Parameters</em>' containment reference list.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getPathItem_Parameters()
	 * @model containment="true"
	 * @generated
	 */
	EList<Parameter> getParameters();

} // PathItem
