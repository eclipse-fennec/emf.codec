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
 * A representation of the model object '<em><b>Security Requirement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.SecurityRequirement#getSchemes <em>Schemes</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityRequirement()
 * @model
 * @generated
 */
@ProviderType
public interface SecurityRequirement extends EObject {
	/**
	 * Returns the value of the '<em><b>Schemes</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type list of {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schemes</em>' map.
	 * @see #isSetSchemes()
	 * @see #unsetSchemes()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getSecurityRequirement_Schemes()
	 * @model mapType="org.eclipse.fennec.model.oas.SecurityRequirementEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, EList<String>> getSchemes();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.SecurityRequirement#getSchemes <em>Schemes</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSchemes()
	 * @see #getSchemes()
	 * @generated
	 */
	void unsetSchemes();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.SecurityRequirement#getSchemes <em>Schemes</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Schemes</em>' map is set.
	 * @see #unsetSchemes()
	 * @see #getSchemes()
	 * @generated
	 */
	boolean isSetSchemes();

} // SecurityRequirement
