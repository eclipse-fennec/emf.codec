/*
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *      Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.codec.demo.model.test1;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test1 Business Person</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.Test1BusinessPerson#getCompanyId <em>Company Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.Test1BusinessPerson#getCompanyIdCardNumber <em>Company Id Card Number</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage#getTest1BusinessPerson()
 * @model annotation="codec.id strategy='ID_FIELD'"
 * @generated
 */
@ProviderType
public interface Test1BusinessPerson extends Test1Person {
	/**
	 * Returns the value of the '<em><b>Company Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Company Id</em>' attribute.
	 * @see #setCompanyId(String)
	 * @see org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage#getTest1BusinessPerson_CompanyId()
	 * @model required="true"
	 * @generated
	 */
	String getCompanyId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.demo.model.test1.Test1BusinessPerson#getCompanyId <em>Company Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Company Id</em>' attribute.
	 * @see #getCompanyId()
	 * @generated
	 */
	void setCompanyId(String value);

	/**
	 * Returns the value of the '<em><b>Company Id Card Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Company Id Card Number</em>' attribute.
	 * @see #setCompanyIdCardNumber(String)
	 * @see org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage#getTest1BusinessPerson_CompanyIdCardNumber()
	 * @model extendedMetaData="name='compId'"
	 * @generated
	 */
	String getCompanyIdCardNumber();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.demo.model.test1.Test1BusinessPerson#getCompanyIdCardNumber <em>Company Id Card Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Company Id Card Number</em>' attribute.
	 * @see #getCompanyIdCardNumber()
	 * @generated
	 */
	void setCompanyIdCardNumber(String value);

} // Test1BusinessPerson
