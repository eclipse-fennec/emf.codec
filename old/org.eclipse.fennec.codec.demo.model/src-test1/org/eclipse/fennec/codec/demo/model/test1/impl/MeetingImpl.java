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

import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.codec.demo.model.test1.Meeting;
import org.eclipse.fennec.codec.demo.model.test1.Test1Person;
import org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Meeting</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.impl.MeetingImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.impl.MeetingImpl#getDate <em>Date</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.impl.MeetingImpl#getResponsiblePerson <em>Responsible Person</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MeetingImpl extends MinimalEObjectImpl.Container implements Meeting {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getDate() <em>Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDate() <em>Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDate()
	 * @generated
	 * @ordered
	 */
	protected Date date = DATE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getResponsiblePerson() <em>Responsible Person</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponsiblePerson()
	 * @generated
	 * @ordered
	 */
	protected Test1Person responsiblePerson;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MeetingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TestTypeAnnotationInheritancePackage.Literals.MEETING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TestTypeAnnotationInheritancePackage.MEETING__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getDate() {
		return date;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDate(Date newDate) {
		Date oldDate = date;
		date = newDate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TestTypeAnnotationInheritancePackage.MEETING__DATE, oldDate, date));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Test1Person getResponsiblePerson() {
		return responsiblePerson;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetResponsiblePerson(Test1Person newResponsiblePerson, NotificationChain msgs) {
		Test1Person oldResponsiblePerson = responsiblePerson;
		responsiblePerson = newResponsiblePerson;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, TestTypeAnnotationInheritancePackage.MEETING__RESPONSIBLE_PERSON, oldResponsiblePerson, newResponsiblePerson);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setResponsiblePerson(Test1Person newResponsiblePerson) {
		if (newResponsiblePerson != responsiblePerson) {
			NotificationChain msgs = null;
			if (responsiblePerson != null)
				msgs = ((InternalEObject)responsiblePerson).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TestTypeAnnotationInheritancePackage.MEETING__RESPONSIBLE_PERSON, null, msgs);
			if (newResponsiblePerson != null)
				msgs = ((InternalEObject)newResponsiblePerson).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - TestTypeAnnotationInheritancePackage.MEETING__RESPONSIBLE_PERSON, null, msgs);
			msgs = basicSetResponsiblePerson(newResponsiblePerson, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TestTypeAnnotationInheritancePackage.MEETING__RESPONSIBLE_PERSON, newResponsiblePerson, newResponsiblePerson));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case TestTypeAnnotationInheritancePackage.MEETING__RESPONSIBLE_PERSON:
				return basicSetResponsiblePerson(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TestTypeAnnotationInheritancePackage.MEETING__ID:
				return getId();
			case TestTypeAnnotationInheritancePackage.MEETING__DATE:
				return getDate();
			case TestTypeAnnotationInheritancePackage.MEETING__RESPONSIBLE_PERSON:
				return getResponsiblePerson();
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
			case TestTypeAnnotationInheritancePackage.MEETING__ID:
				setId((String)newValue);
				return;
			case TestTypeAnnotationInheritancePackage.MEETING__DATE:
				setDate((Date)newValue);
				return;
			case TestTypeAnnotationInheritancePackage.MEETING__RESPONSIBLE_PERSON:
				setResponsiblePerson((Test1Person)newValue);
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
			case TestTypeAnnotationInheritancePackage.MEETING__ID:
				setId(ID_EDEFAULT);
				return;
			case TestTypeAnnotationInheritancePackage.MEETING__DATE:
				setDate(DATE_EDEFAULT);
				return;
			case TestTypeAnnotationInheritancePackage.MEETING__RESPONSIBLE_PERSON:
				setResponsiblePerson((Test1Person)null);
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
			case TestTypeAnnotationInheritancePackage.MEETING__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case TestTypeAnnotationInheritancePackage.MEETING__DATE:
				return DATE_EDEFAULT == null ? date != null : !DATE_EDEFAULT.equals(date);
			case TestTypeAnnotationInheritancePackage.MEETING__RESPONSIBLE_PERSON:
				return responsiblePerson != null;
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
		result.append(" (id: ");
		result.append(id);
		result.append(", date: ");
		result.append(date);
		result.append(')');
		return result.toString();
	}

} //MeetingImpl
