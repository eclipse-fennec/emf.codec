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
package org.eclipse.fennec.codec.info.codecinfo.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage;
import org.eclipse.fennec.codec.info.codecinfo.SuperTypeInfo;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Super Type Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.SuperTypeInfoImpl#getSuperTypeStrategy <em>Super Type Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.SuperTypeInfoImpl#getSuperTypeSeparator <em>Super Type Separator</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.SuperTypeInfoImpl#isIgnoreSuperType <em>Ignore Super Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SuperTypeInfoImpl extends FeatureCodecInfoImpl implements SuperTypeInfo {
	/**
	 * The default value of the '{@link #getSuperTypeStrategy() <em>Super Type Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperTypeStrategy()
	 * @generated
	 * @ordered
	 */
	protected static final String SUPER_TYPE_STRATEGY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSuperTypeStrategy() <em>Super Type Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperTypeStrategy()
	 * @generated
	 * @ordered
	 */
	protected String superTypeStrategy = SUPER_TYPE_STRATEGY_EDEFAULT;

	/**
	 * The default value of the '{@link #getSuperTypeSeparator() <em>Super Type Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperTypeSeparator()
	 * @generated
	 * @ordered
	 */
	protected static final String SUPER_TYPE_SEPARATOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSuperTypeSeparator() <em>Super Type Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperTypeSeparator()
	 * @generated
	 * @ordered
	 */
	protected String superTypeSeparator = SUPER_TYPE_SEPARATOR_EDEFAULT;

	/**
	 * The default value of the '{@link #isIgnoreSuperType() <em>Ignore Super Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnoreSuperType()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IGNORE_SUPER_TYPE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIgnoreSuperType() <em>Ignore Super Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnoreSuperType()
	 * @generated
	 * @ordered
	 */
	protected boolean ignoreSuperType = IGNORE_SUPER_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SuperTypeInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecInfoPackage.Literals.SUPER_TYPE_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSuperTypeStrategy() {
		return superTypeStrategy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSuperTypeStrategy(String newSuperTypeStrategy) {
		String oldSuperTypeStrategy = superTypeStrategy;
		superTypeStrategy = newSuperTypeStrategy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_STRATEGY, oldSuperTypeStrategy, superTypeStrategy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSuperTypeSeparator() {
		return superTypeSeparator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSuperTypeSeparator(String newSuperTypeSeparator) {
		String oldSuperTypeSeparator = superTypeSeparator;
		superTypeSeparator = newSuperTypeSeparator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_SEPARATOR, oldSuperTypeSeparator, superTypeSeparator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIgnoreSuperType() {
		return ignoreSuperType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIgnoreSuperType(boolean newIgnoreSuperType) {
		boolean oldIgnoreSuperType = ignoreSuperType;
		ignoreSuperType = newIgnoreSuperType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.SUPER_TYPE_INFO__IGNORE_SUPER_TYPE, oldIgnoreSuperType, ignoreSuperType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_STRATEGY:
				return getSuperTypeStrategy();
			case CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_SEPARATOR:
				return getSuperTypeSeparator();
			case CodecInfoPackage.SUPER_TYPE_INFO__IGNORE_SUPER_TYPE:
				return isIgnoreSuperType();
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
			case CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_STRATEGY:
				setSuperTypeStrategy((String)newValue);
				return;
			case CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_SEPARATOR:
				setSuperTypeSeparator((String)newValue);
				return;
			case CodecInfoPackage.SUPER_TYPE_INFO__IGNORE_SUPER_TYPE:
				setIgnoreSuperType((Boolean)newValue);
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
			case CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_STRATEGY:
				setSuperTypeStrategy(SUPER_TYPE_STRATEGY_EDEFAULT);
				return;
			case CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_SEPARATOR:
				setSuperTypeSeparator(SUPER_TYPE_SEPARATOR_EDEFAULT);
				return;
			case CodecInfoPackage.SUPER_TYPE_INFO__IGNORE_SUPER_TYPE:
				setIgnoreSuperType(IGNORE_SUPER_TYPE_EDEFAULT);
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
			case CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_STRATEGY:
				return SUPER_TYPE_STRATEGY_EDEFAULT == null ? superTypeStrategy != null : !SUPER_TYPE_STRATEGY_EDEFAULT.equals(superTypeStrategy);
			case CodecInfoPackage.SUPER_TYPE_INFO__SUPER_TYPE_SEPARATOR:
				return SUPER_TYPE_SEPARATOR_EDEFAULT == null ? superTypeSeparator != null : !SUPER_TYPE_SEPARATOR_EDEFAULT.equals(superTypeSeparator);
			case CodecInfoPackage.SUPER_TYPE_INFO__IGNORE_SUPER_TYPE:
				return ignoreSuperType != IGNORE_SUPER_TYPE_EDEFAULT;
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
		result.append(" (superTypeStrategy: ");
		result.append(superTypeStrategy);
		result.append(", superTypeSeparator: ");
		result.append(superTypeSeparator);
		result.append(", ignoreSuperType: ");
		result.append(ignoreSuperType);
		result.append(')');
		return result.toString();
	}

} //SuperTypeInfoImpl
