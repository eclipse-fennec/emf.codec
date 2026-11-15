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
 * A representation of the model object '<em><b>Callback</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.Callback#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Callback#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Callback#getExpressions <em>Expressions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getCallback()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='expressions'"
 * @generated
 */
@ProviderType
public interface Callback extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of a Reference Object; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getCallback_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Callback#getRef <em>Ref</em>}' attribute.
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
	 * @see #setTarget(Callback)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getCallback_Target()
	 * @model transient="true"
	 * @generated
	 */
	Callback getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Callback#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(Callback value);

	/**
	 * Returns the value of the '<em><b>Expressions</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.PathItem},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Runtime expression to the path item it describes.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expressions</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getCallback_Expressions()
	 * @model mapType="org.eclipse.fennec.model.oas.PathItemEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.PathItem&gt;"
	 * @generated
	 */
	EMap<String, PathItem> getExpressions();

} // Callback
