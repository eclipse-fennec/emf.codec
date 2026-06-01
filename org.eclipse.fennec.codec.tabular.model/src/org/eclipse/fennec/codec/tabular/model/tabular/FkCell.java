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
package org.eclipse.fennec.codec.tabular.model.tabular;

import org.eclipse.emf.ecore.EClass;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Fk Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.FkCell#getTargetId <em>Target Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.FkCell#getTargetEClass <em>Target EClass</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getFkCell()
 * @model
 * @generated
 */
@ProviderType
public interface FkCell extends Cell {
	/**
	 * Returns the value of the '<em><b>Target Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Id</em>' attribute.
	 * @see #setTargetId(long)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getFkCell_TargetId()
	 * @model
	 * @generated
	 */
	long getTargetId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.FkCell#getTargetId <em>Target Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Id</em>' attribute.
	 * @see #getTargetId()
	 * @generated
	 */
	void setTargetId(long value);

	/**
	 * Returns the value of the '<em><b>Target EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target EClass</em>' reference.
	 * @see #setTargetEClass(EClass)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getFkCell_TargetEClass()
	 * @model
	 * @generated
	 */
	EClass getTargetEClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.FkCell#getTargetEClass <em>Target EClass</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target EClass</em>' reference.
	 * @see #getTargetEClass()
	 * @generated
	 */
	void setTargetEClass(EClass value);

} // FkCell
