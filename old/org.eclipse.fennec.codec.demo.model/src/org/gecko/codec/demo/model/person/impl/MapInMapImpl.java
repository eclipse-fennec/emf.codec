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

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.gecko.codec.demo.model.person.MapInMap;
import org.gecko.codec.demo.model.person.PersonPackage;
import org.gecko.codec.demo.model.person.SimpleValue;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Map In Map</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.codec.demo.model.person.impl.MapInMapImpl#getStringMapInMapValues <em>String Map In Map Values</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MapInMapImpl extends MinimalEObjectImpl.Container implements MapInMap {
	/**
	 * The cached value of the '{@link #getStringMapInMapValues() <em>String Map In Map Values</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStringMapInMapValues()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, EMap<String, SimpleValue>> stringMapInMapValues;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MapInMapImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PersonPackage.Literals.MAP_IN_MAP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, EMap<String, SimpleValue>> getStringMapInMapValues() {
		if (stringMapInMapValues == null) {
			stringMapInMapValues = new EcoreEMap<String,EMap<String, SimpleValue>>(PersonPackage.Literals.STRING_TO_STRING_MAP_IN_MAP, StringToStringMapInMapImpl.class, this, PersonPackage.MAP_IN_MAP__STRING_MAP_IN_MAP_VALUES);
		}
		return stringMapInMapValues;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PersonPackage.MAP_IN_MAP__STRING_MAP_IN_MAP_VALUES:
				return ((InternalEList<?>)getStringMapInMapValues()).basicRemove(otherEnd, msgs);
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
			case PersonPackage.MAP_IN_MAP__STRING_MAP_IN_MAP_VALUES:
				if (coreType) return getStringMapInMapValues();
				else return getStringMapInMapValues().map();
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
			case PersonPackage.MAP_IN_MAP__STRING_MAP_IN_MAP_VALUES:
				((EStructuralFeature.Setting)getStringMapInMapValues()).set(newValue);
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
			case PersonPackage.MAP_IN_MAP__STRING_MAP_IN_MAP_VALUES:
				getStringMapInMapValues().clear();
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
			case PersonPackage.MAP_IN_MAP__STRING_MAP_IN_MAP_VALUES:
				return stringMapInMapValues != null && !stringMapInMapValues.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //MapInMapImpl
