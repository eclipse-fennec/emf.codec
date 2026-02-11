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

import org.gecko.codec.demo.model.person.PersonPackage;
import org.gecko.codec.demo.model.person.SimpleMap;
import org.gecko.codec.demo.model.person.SimpleValue;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Simple Map</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.codec.demo.model.person.impl.SimpleMapImpl#getStringMapValues <em>String Map Values</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SimpleMapImpl extends MinimalEObjectImpl.Container implements SimpleMap {
	/**
	 * The cached value of the '{@link #getStringMapValues() <em>String Map Values</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStringMapValues()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, SimpleValue> stringMapValues;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SimpleMapImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PersonPackage.Literals.SIMPLE_MAP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, SimpleValue> getStringMapValues() {
		if (stringMapValues == null) {
			stringMapValues = new EcoreEMap<String,SimpleValue>(PersonPackage.Literals.STRING_TO_SIMPLE_VALUE_MAP, StringToSimpleValueMapImpl.class, this, PersonPackage.SIMPLE_MAP__STRING_MAP_VALUES);
		}
		return stringMapValues;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PersonPackage.SIMPLE_MAP__STRING_MAP_VALUES:
				return ((InternalEList<?>)getStringMapValues()).basicRemove(otherEnd, msgs);
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
			case PersonPackage.SIMPLE_MAP__STRING_MAP_VALUES:
				if (coreType) return getStringMapValues();
				else return getStringMapValues().map();
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
			case PersonPackage.SIMPLE_MAP__STRING_MAP_VALUES:
				((EStructuralFeature.Setting)getStringMapValues()).set(newValue);
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
			case PersonPackage.SIMPLE_MAP__STRING_MAP_VALUES:
				getStringMapValues().clear();
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
			case PersonPackage.SIMPLE_MAP__STRING_MAP_VALUES:
				return stringMapValues != null && !stringMapValues.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //SimpleMapImpl
