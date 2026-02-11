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

import org.eclipse.fennec.lorawan.uplink.model.lorawan.LorawanPackage;
import org.eclipse.fennec.lorawan.uplink.model.lorawan.Modulation;
import org.eclipse.fennec.lorawan.uplink.model.lorawan.TxInfo;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Tx Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.lorawan.uplink.model.lorawan.impl.TxInfoImpl#getFrequency <em>Frequency</em>}</li>
 *   <li>{@link org.eclipse.fennec.lorawan.uplink.model.lorawan.impl.TxInfoImpl#getModulation <em>Modulation</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TxInfoImpl extends MinimalEObjectImpl.Container implements TxInfo {
	/**
	 * The default value of the '{@link #getFrequency() <em>Frequency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrequency()
	 * @generated
	 * @ordered
	 */
	protected static final long FREQUENCY_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getFrequency() <em>Frequency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrequency()
	 * @generated
	 * @ordered
	 */
	protected long frequency = FREQUENCY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getModulation() <em>Modulation</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModulation()
	 * @generated
	 * @ordered
	 */
	protected Modulation modulation;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TxInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LorawanPackage.Literals.TX_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getFrequency() {
		return frequency;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFrequency(long newFrequency) {
		long oldFrequency = frequency;
		frequency = newFrequency;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.TX_INFO__FREQUENCY, oldFrequency, frequency));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Modulation getModulation() {
		return modulation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetModulation(Modulation newModulation, NotificationChain msgs) {
		Modulation oldModulation = modulation;
		modulation = newModulation;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, LorawanPackage.TX_INFO__MODULATION, oldModulation, newModulation);
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
	public void setModulation(Modulation newModulation) {
		if (newModulation != modulation) {
			NotificationChain msgs = null;
			if (modulation != null)
				msgs = ((InternalEObject)modulation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.TX_INFO__MODULATION, null, msgs);
			if (newModulation != null)
				msgs = ((InternalEObject)newModulation).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.TX_INFO__MODULATION, null, msgs);
			msgs = basicSetModulation(newModulation, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.TX_INFO__MODULATION, newModulation, newModulation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LorawanPackage.TX_INFO__MODULATION:
				return basicSetModulation(null, msgs);
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
			case LorawanPackage.TX_INFO__FREQUENCY:
				return getFrequency();
			case LorawanPackage.TX_INFO__MODULATION:
				return getModulation();
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
			case LorawanPackage.TX_INFO__FREQUENCY:
				setFrequency((Long)newValue);
				return;
			case LorawanPackage.TX_INFO__MODULATION:
				setModulation((Modulation)newValue);
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
			case LorawanPackage.TX_INFO__FREQUENCY:
				setFrequency(FREQUENCY_EDEFAULT);
				return;
			case LorawanPackage.TX_INFO__MODULATION:
				setModulation((Modulation)null);
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
			case LorawanPackage.TX_INFO__FREQUENCY:
				return frequency != FREQUENCY_EDEFAULT;
			case LorawanPackage.TX_INFO__MODULATION:
				return modulation != null;
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
		result.append(" (frequency: ");
		result.append(frequency);
		result.append(')');
		return result.toString();
	}

} //TxInfoImpl
