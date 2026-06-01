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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Column</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getHeader <em>Header</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getSqlType <em>Sql Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getFeature <em>Feature</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getColumn()
 * @model
 * @generated
 */
@ProviderType
public interface Column extends EObject {
	/**
	 * Returns the value of the '<em><b>Header</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Header</em>' attribute.
	 * @see #setHeader(String)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getColumn_Header()
	 * @model
	 * @generated
	 */
	String getHeader();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getHeader <em>Header</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Header</em>' attribute.
	 * @see #getHeader()
	 * @generated
	 */
	void setHeader(String value);

	/**
	 * Returns the value of the '<em><b>Sql Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql Type</em>' attribute.
	 * @see #setSqlType(String)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getColumn_SqlType()
	 * @model
	 * @generated
	 */
	String getSqlType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getSqlType <em>Sql Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sql Type</em>' attribute.
	 * @see #getSqlType()
	 * @generated
	 */
	void setSqlType(String value);

	/**
	 * Returns the value of the '<em><b>Source</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' attribute.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource
	 * @see #setSource(ColumnSource)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getColumn_Source()
	 * @model
	 * @generated
	 */
	ColumnSource getSource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getSource <em>Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' attribute.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource
	 * @see #getSource()
	 * @generated
	 */
	void setSource(ColumnSource value);

	/**
	 * Returns the value of the '<em><b>Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Feature</em>' reference.
	 * @see #setFeature(EStructuralFeature)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getColumn_Feature()
	 * @model
	 * @generated
	 */
	EStructuralFeature getFeature();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getFeature <em>Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature</em>' reference.
	 * @see #getFeature()
	 * @generated
	 */
	void setFeature(EStructuralFeature value);

} // Column
