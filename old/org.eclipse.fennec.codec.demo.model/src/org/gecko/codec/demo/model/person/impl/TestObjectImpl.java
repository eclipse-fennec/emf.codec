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
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.gecko.codec.demo.model.person.Parent;
import org.gecko.codec.demo.model.person.Parent2;
import org.gecko.codec.demo.model.person.PersonPackage;
import org.gecko.codec.demo.model.person.TestObject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Object</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.codec.demo.model.person.impl.TestObjectImpl#getRef1 <em>Ref1</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.impl.TestObjectImpl#getRef2 <em>Ref2</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TestObjectImpl extends MinimalEObjectImpl.Container implements TestObject {
	/**
	 * The cached value of the '{@link #getRef1() <em>Ref1</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRef1()
	 * @generated
	 * @ordered
	 */
	protected Parent ref1;

	/**
	 * The cached value of the '{@link #getRef2() <em>Ref2</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRef2()
	 * @generated
	 * @ordered
	 */
	protected Parent2 ref2;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TestObjectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PersonPackage.Literals.TEST_OBJECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parent getRef1() {
		return ref1;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRef1(Parent newRef1, NotificationChain msgs) {
		Parent oldRef1 = ref1;
		ref1 = newRef1;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PersonPackage.TEST_OBJECT__REF1, oldRef1, newRef1);
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
	public void setRef1(Parent newRef1) {
		if (newRef1 != ref1) {
			NotificationChain msgs = null;
			if (ref1 != null)
				msgs = ((InternalEObject)ref1).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PersonPackage.TEST_OBJECT__REF1, null, msgs);
			if (newRef1 != null)
				msgs = ((InternalEObject)newRef1).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PersonPackage.TEST_OBJECT__REF1, null, msgs);
			msgs = basicSetRef1(newRef1, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PersonPackage.TEST_OBJECT__REF1, newRef1, newRef1));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parent2 getRef2() {
		return ref2;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRef2(Parent2 newRef2, NotificationChain msgs) {
		Parent2 oldRef2 = ref2;
		ref2 = newRef2;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PersonPackage.TEST_OBJECT__REF2, oldRef2, newRef2);
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
	public void setRef2(Parent2 newRef2) {
		if (newRef2 != ref2) {
			NotificationChain msgs = null;
			if (ref2 != null)
				msgs = ((InternalEObject)ref2).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PersonPackage.TEST_OBJECT__REF2, null, msgs);
			if (newRef2 != null)
				msgs = ((InternalEObject)newRef2).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PersonPackage.TEST_OBJECT__REF2, null, msgs);
			msgs = basicSetRef2(newRef2, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PersonPackage.TEST_OBJECT__REF2, newRef2, newRef2));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PersonPackage.TEST_OBJECT__REF1:
				return basicSetRef1(null, msgs);
			case PersonPackage.TEST_OBJECT__REF2:
				return basicSetRef2(null, msgs);
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
			case PersonPackage.TEST_OBJECT__REF1:
				return getRef1();
			case PersonPackage.TEST_OBJECT__REF2:
				return getRef2();
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
			case PersonPackage.TEST_OBJECT__REF1:
				setRef1((Parent)newValue);
				return;
			case PersonPackage.TEST_OBJECT__REF2:
				setRef2((Parent2)newValue);
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
			case PersonPackage.TEST_OBJECT__REF1:
				setRef1((Parent)null);
				return;
			case PersonPackage.TEST_OBJECT__REF2:
				setRef2((Parent2)null);
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
			case PersonPackage.TEST_OBJECT__REF1:
				return ref1 != null;
			case PersonPackage.TEST_OBJECT__REF2:
				return ref2 != null;
		}
		return super.eIsSet(featureID);
	}

} //TestObjectImpl
