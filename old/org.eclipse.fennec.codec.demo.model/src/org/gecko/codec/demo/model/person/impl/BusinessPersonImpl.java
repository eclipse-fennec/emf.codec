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
package org.gecko.codec.demo.model.person.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.gecko.codec.demo.model.person.BusinessPerson;
import org.gecko.codec.demo.model.person.PersonPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Business Person</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.codec.demo.model.person.impl.BusinessPersonImpl#getCompanyIdCardNumber <em>Company Id Card Number</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BusinessPersonImpl extends PersonImpl implements BusinessPerson {
	/**
	 * The default value of the '{@link #getCompanyIdCardNumber() <em>Company Id Card Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCompanyIdCardNumber()
	 * @generated
	 * @ordered
	 */
	protected static final String COMPANY_ID_CARD_NUMBER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCompanyIdCardNumber() <em>Company Id Card Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCompanyIdCardNumber()
	 * @generated
	 * @ordered
	 */
	protected String companyIdCardNumber = COMPANY_ID_CARD_NUMBER_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BusinessPersonImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PersonPackage.Literals.BUSINESS_PERSON;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCompanyIdCardNumber() {
		return companyIdCardNumber;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCompanyIdCardNumber(String newCompanyIdCardNumber) {
		String oldCompanyIdCardNumber = companyIdCardNumber;
		companyIdCardNumber = newCompanyIdCardNumber;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PersonPackage.BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER, oldCompanyIdCardNumber, companyIdCardNumber));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PersonPackage.BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER:
				return getCompanyIdCardNumber();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PersonPackage.BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER:
				setCompanyIdCardNumber((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PersonPackage.BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER:
				setCompanyIdCardNumber(COMPANY_ID_CARD_NUMBER_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PersonPackage.BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER:
				return COMPANY_ID_CARD_NUMBER_EDEFAULT == null ? companyIdCardNumber != null : !COMPANY_ID_CARD_NUMBER_EDEFAULT.equals(companyIdCardNumber);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (companyIdCardNumber: ");
		result.append(companyIdCardNumber);
		result.append(')');
		return result.toString();
	}

} //BusinessPersonImpl
