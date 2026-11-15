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
 * A representation of the model object '<em><b>Path Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getGet <em>Get</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getPut <em>Put</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getPost <em>Post</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getDelete <em>Delete</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getOptions <em>Options</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getHead <em>Head</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getPatch <em>Patch</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getTrace <em>Trace</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getQuery <em>Query</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getAdditionalOperations <em>Additional Operations</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getServers <em>Servers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getAllOperations <em>All Operations</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.PathItem#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface PathItem extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of a Reference Object; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getRef <em>Ref</em>}' attribute.
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
	 * @see #setTarget(PathItem)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Target()
	 * @model transient="true"
	 * @generated
	 */
	PathItem getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(PathItem value);

	/**
	 * Returns the value of the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Summary</em>' attribute.
	 * @see #setSummary(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Summary()
	 * @model
	 * @generated
	 */
	String getSummary();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getSummary <em>Summary</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getDescription <em>Description</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Get()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getGet();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getGet <em>Get</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Put()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getPut();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getPut <em>Put</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Post()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getPost();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getPost <em>Post</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Delete()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getDelete();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getDelete <em>Delete</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Options()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getOptions();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getOptions <em>Options</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Head()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getHead();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getHead <em>Head</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Patch()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getPatch();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getPatch <em>Patch</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Trace()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 * @generated
	 */
	Operation getTrace();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getTrace <em>Trace</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Trace</em>' containment reference.
	 * @see #getTrace()
	 * @generated
	 */
	void setTrace(Operation value);

	/**
	 * Returns the value of the '<em><b>Query</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Query</em>' containment reference.
	 * @see #setQuery(Operation)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Query()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='operation'"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	Operation getQuery();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getQuery <em>Query</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Query</em>' containment reference.
	 * @see #getQuery()
	 * @generated
	 */
	void setQuery(Operation value);

	/**
	 * Returns the value of the '<em><b>Additional Operations</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Operation},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Additional Operations</em>' map.
	 * @see #isSetAdditionalOperations()
	 * @see #unsetAdditionalOperations()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_AdditionalOperations()
	 * @model mapType="org.eclipse.fennec.model.oas.OperationEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Operation&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	EMap<String, Operation> getAdditionalOperations();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getAdditionalOperations <em>Additional Operations</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAdditionalOperations()
	 * @see #getAdditionalOperations()
	 * @generated
	 */
	void unsetAdditionalOperations();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getAdditionalOperations <em>Additional Operations</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Additional Operations</em>' map is set.
	 * @see #unsetAdditionalOperations()
	 * @see #getAdditionalOperations()
	 * @generated
	 */
	boolean isSetAdditionalOperations();

	/**
	 * Returns the value of the '<em><b>Servers</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.Server}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Servers</em>' containment reference list.
	 * @see #isSetServers()
	 * @see #unsetServers()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Servers()
	 * @model containment="true" unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EList<Server> getServers();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getServers <em>Servers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetServers()
	 * @see #getServers()
	 * @generated
	 */
	void unsetServers();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getServers <em>Servers</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Servers</em>' containment reference list is set.
	 * @see #unsetServers()
	 * @see #getServers()
	 * @generated
	 */
	boolean isSetServers();

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.Parameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' containment reference list.
	 * @see #isSetParameters()
	 * @see #unsetParameters()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Parameters()
	 * @model containment="true" unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EList<Parameter> getParameters();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getParameters <em>Parameters</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetParameters()
	 * @see #getParameters()
	 * @generated
	 */
	void unsetParameters();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.PathItem#getParameters <em>Parameters</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Parameters</em>' containment reference list is set.
	 * @see #unsetParameters()
	 * @see #getParameters()
	 * @generated
	 */
	boolean isSetParameters();

	/**
	 * Returns the value of the '<em><b>All Operations</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.Operation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * All operations of this path item: the fixed methods, then additionalOperations.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>All Operations</em>' reference list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_AllOperations()
	 * @model transient="true" volatile="true" derived="true"
	 * @generated
	 */
	EList<Operation> getAllOperations();

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getPathItem_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // PathItem
