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
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Reference Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceSerializationConfigImpl#isIncludeType <em>Include Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceSerializationConfigImpl#isExpand <em>Expand</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReferenceSerializationConfigImpl extends BaseReferenceConfigImpl implements ReferenceSerializationConfig {
	/**
	 * The default value of the '{@link #isIncludeType() <em>Include Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeType()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INCLUDE_TYPE_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isIncludeType() <em>Include Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeType()
	 * @generated
	 * @ordered
	 */
	protected boolean includeType = INCLUDE_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #isExpand() <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExpand()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EXPAND_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isExpand() <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExpand()
	 * @generated
	 * @ordered
	 */
	protected boolean expand = EXPAND_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReferenceSerializationConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.REFERENCE_SERIALIZATION_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIncludeType() {
		return includeType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIncludeType(boolean newIncludeType) {
		boolean oldIncludeType = includeType;
		includeType = newIncludeType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.REFERENCE_SERIALIZATION_CONFIG__INCLUDE_TYPE, oldIncludeType, includeType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isExpand() {
		return expand;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpand(boolean newExpand) {
		boolean oldExpand = expand;
		expand = newExpand;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.REFERENCE_SERIALIZATION_CONFIG__EXPAND, oldExpand, expand));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG__INCLUDE_TYPE:
				return isIncludeType();
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG__EXPAND:
				return isExpand();
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
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG__INCLUDE_TYPE:
				setIncludeType((Boolean)newValue);
				return;
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG__EXPAND:
				setExpand((Boolean)newValue);
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
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG__INCLUDE_TYPE:
				setIncludeType(INCLUDE_TYPE_EDEFAULT);
				return;
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG__EXPAND:
				setExpand(EXPAND_EDEFAULT);
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
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG__INCLUDE_TYPE:
				return includeType != INCLUDE_TYPE_EDEFAULT;
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG__EXPAND:
				return expand != EXPAND_EDEFAULT;
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
		result.append(" (includeType: ");
		result.append(includeType);
		result.append(", expand: ");
		result.append(expand);
		result.append(')');
		return result.toString();
	}

} //ReferenceSerializationConfigImpl
