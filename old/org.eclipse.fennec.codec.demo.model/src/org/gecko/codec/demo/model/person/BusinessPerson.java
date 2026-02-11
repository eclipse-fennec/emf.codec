/*
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 * 
 */
package org.gecko.codec.demo.model.person;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Business Person</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.codec.demo.model.person.BusinessPerson#getCompanyIdCardNumber <em>Company Id Card Number</em>}</li>
 * </ul>
 *
 * @see org.gecko.codec.demo.model.person.PersonPackage#getBusinessPerson()
 * @model annotation="codec.id strategy='ID_FIELD'"
 * @generated
 */
@ProviderType
public interface BusinessPerson extends Person {
	/**
	 * Returns the value of the '<em><b>Company Id Card Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Company Id Card Number</em>' attribute.
	 * @see #setCompanyIdCardNumber(String)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getBusinessPerson_CompanyIdCardNumber()
	 * @model extendedMetaData="name='compId'"
	 * @generated
	 */
	String getCompanyIdCardNumber();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.BusinessPerson#getCompanyIdCardNumber <em>Company Id Card Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Company Id Card Number</em>' attribute.
	 * @see #getCompanyIdCardNumber()
	 * @generated
	 */
	void setCompanyIdCardNumber(String value);

} // BusinessPerson
