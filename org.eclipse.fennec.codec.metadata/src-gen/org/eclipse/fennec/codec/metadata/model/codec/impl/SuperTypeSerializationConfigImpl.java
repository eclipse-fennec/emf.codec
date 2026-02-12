/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 */
package org.eclipse.fennec.codec.metadata.model.codec.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;

import org.eclipse.fennec.model.metadata.impl.BaseSuperTypeConfigImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Super Type Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.SuperTypeSerializationConfigImpl#isUseSmartCompression <em>Use Smart Compression</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SuperTypeSerializationConfigImpl extends BaseSuperTypeConfigImpl implements SuperTypeSerializationConfig {
	/**
	 * The default value of the '{@link #isUseSmartCompression() <em>Use Smart Compression</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUseSmartCompression()
	 * @generated
	 * @ordered
	 */
	protected static final boolean USE_SMART_COMPRESSION_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isUseSmartCompression() <em>Use Smart Compression</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUseSmartCompression()
	 * @generated
	 * @ordered
	 */
	protected boolean useSmartCompression = USE_SMART_COMPRESSION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SuperTypeSerializationConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.SUPER_TYPE_SERIALIZATION_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isUseSmartCompression() {
		return useSmartCompression;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUseSmartCompression(boolean newUseSmartCompression) {
		boolean oldUseSmartCompression = useSmartCompression;
		useSmartCompression = newUseSmartCompression;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.SUPER_TYPE_SERIALIZATION_CONFIG__USE_SMART_COMPRESSION, oldUseSmartCompression, useSmartCompression));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecPackage.SUPER_TYPE_SERIALIZATION_CONFIG__USE_SMART_COMPRESSION:
				return isUseSmartCompression();
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
			case CodecPackage.SUPER_TYPE_SERIALIZATION_CONFIG__USE_SMART_COMPRESSION:
				setUseSmartCompression((Boolean)newValue);
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
			case CodecPackage.SUPER_TYPE_SERIALIZATION_CONFIG__USE_SMART_COMPRESSION:
				setUseSmartCompression(USE_SMART_COMPRESSION_EDEFAULT);
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
			case CodecPackage.SUPER_TYPE_SERIALIZATION_CONFIG__USE_SMART_COMPRESSION:
				return useSmartCompression != USE_SMART_COMPRESSION_EDEFAULT;
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
		result.append(" (useSmartCompression: ");
		result.append(useSmartCompression);
		result.append(')');
		return result.toString();
	}

} //SuperTypeSerializationConfigImpl
