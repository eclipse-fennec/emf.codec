/*
 *  Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.lorawan.uplink.model.lorawan.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.lorawan.uplink.model.lorawan.LoraInfo;
import org.eclipse.fennec.lorawan.uplink.model.lorawan.LorawanPackage;
import org.eclipse.fennec.lorawan.uplink.model.lorawan.Modulation;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Modulation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.lorawan.uplink.model.lorawan.impl.ModulationImpl#getLora <em>Lora</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModulationImpl extends MinimalEObjectImpl.Container implements Modulation {
	/**
	 * The cached value of the '{@link #getLora() <em>Lora</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLora()
	 * @generated
	 * @ordered
	 */
	protected LoraInfo lora;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModulationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LorawanPackage.Literals.MODULATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LoraInfo getLora() {
		return lora;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLora(LoraInfo newLora, NotificationChain msgs) {
		LoraInfo oldLora = lora;
		lora = newLora;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, LorawanPackage.MODULATION__LORA, oldLora, newLora);
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
	public void setLora(LoraInfo newLora) {
		if (newLora != lora) {
			NotificationChain msgs = null;
			if (lora != null)
				msgs = ((InternalEObject)lora).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.MODULATION__LORA, null, msgs);
			if (newLora != null)
				msgs = ((InternalEObject)newLora).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.MODULATION__LORA, null, msgs);
			msgs = basicSetLora(newLora, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.MODULATION__LORA, newLora, newLora));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LorawanPackage.MODULATION__LORA:
				return basicSetLora(null, msgs);
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
			case LorawanPackage.MODULATION__LORA:
				return getLora();
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
			case LorawanPackage.MODULATION__LORA:
				setLora((LoraInfo)newValue);
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
			case LorawanPackage.MODULATION__LORA:
				setLora((LoraInfo)null);
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
			case LorawanPackage.MODULATION__LORA:
				return lora != null;
		}
		return super.eIsSet(featureID);
	}

} //ModulationImpl
