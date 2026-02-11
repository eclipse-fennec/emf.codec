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
package org.eclipse.fennec.codec.demo.model.test1.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.codec.demo.model.test1.Test1BusinessPerson;
import org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test1 Business Person</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.impl.Test1BusinessPersonImpl#getCompanyId <em>Company Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.impl.Test1BusinessPersonImpl#getCompanyIdCardNumber <em>Company Id Card Number</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Test1BusinessPersonImpl extends Test1PersonImpl implements Test1BusinessPerson {
	/**
	 * The default value of the '{@link #getCompanyId() <em>Company Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCompanyId()
	 * @generated
	 * @ordered
	 */
	protected static final String COMPANY_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCompanyId() <em>Company Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCompanyId()
	 * @generated
	 * @ordered
	 */
	protected String companyId = COMPANY_ID_EDEFAULT;

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
	protected Test1BusinessPersonImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TestTypeAnnotationInheritancePackage.Literals.TEST1_BUSINESS_PERSON;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCompanyId() {
		return companyId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCompanyId(String newCompanyId) {
		String oldCompanyId = companyId;
		companyId = newCompanyId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID, oldCompanyId, companyId));
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
			eNotify(new ENotificationImpl(this, Notification.SET, TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER, oldCompanyIdCardNumber, companyIdCardNumber));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID:
				return getCompanyId();
			case TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER:
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
			case TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID:
				setCompanyId((String)newValue);
				return;
			case TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER:
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
			case TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID:
				setCompanyId(COMPANY_ID_EDEFAULT);
				return;
			case TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER:
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
			case TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID:
				return COMPANY_ID_EDEFAULT == null ? companyId != null : !COMPANY_ID_EDEFAULT.equals(companyId);
			case TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER:
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
		result.append(" (companyId: ");
		result.append(companyId);
		result.append(", companyIdCardNumber: ");
		result.append(companyIdCardNumber);
		result.append(')');
		return result.toString();
	}

} //Test1BusinessPersonImpl
