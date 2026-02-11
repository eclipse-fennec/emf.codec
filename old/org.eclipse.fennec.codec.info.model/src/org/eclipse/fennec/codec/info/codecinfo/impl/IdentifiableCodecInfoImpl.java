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
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage;
import org.eclipse.fennec.codec.info.codecinfo.IdentifiableCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.IdentityInfo;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Identifiable Codec Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.IdentifiableCodecInfoImpl#getIdentityInfo <em>Identity Info</em>}</li>
 * </ul>
 *
 * @generated
 */
public class IdentifiableCodecInfoImpl extends MinimalEObjectImpl.Container implements IdentifiableCodecInfo {
	/**
	 * The cached value of the '{@link #getIdentityInfo() <em>Identity Info</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdentityInfo()
	 * @generated
	 * @ordered
	 */
	protected IdentityInfo identityInfo;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IdentifiableCodecInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecInfoPackage.Literals.IDENTIFIABLE_CODEC_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIdentityInfo(IdentityInfo newIdentityInfo, NotificationChain msgs) {
		IdentityInfo oldIdentityInfo = identityInfo;
		identityInfo = newIdentityInfo;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecInfoPackage.IDENTIFIABLE_CODEC_INFO__IDENTITY_INFO, oldIdentityInfo, newIdentityInfo);
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
	public void setIdentityInfo(IdentityInfo newIdentityInfo) {
		if (newIdentityInfo != identityInfo) {
			NotificationChain msgs = null;
			if (identityInfo != null)
				msgs = ((InternalEObject)identityInfo).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecInfoPackage.IDENTIFIABLE_CODEC_INFO__IDENTITY_INFO, null, msgs);
			if (newIdentityInfo != null)
				msgs = ((InternalEObject)newIdentityInfo).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecInfoPackage.IDENTIFIABLE_CODEC_INFO__IDENTITY_INFO, null, msgs);
			msgs = basicSetIdentityInfo(newIdentityInfo, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.IDENTIFIABLE_CODEC_INFO__IDENTITY_INFO, newIdentityInfo, newIdentityInfo));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecInfoPackage.IDENTIFIABLE_CODEC_INFO__IDENTITY_INFO:
				return basicSetIdentityInfo(null, msgs);
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
			case CodecInfoPackage.IDENTIFIABLE_CODEC_INFO__IDENTITY_INFO:
				return getIdentityInfo();
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
			case CodecInfoPackage.IDENTIFIABLE_CODEC_INFO__IDENTITY_INFO:
				setIdentityInfo((IdentityInfo)newValue);
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
			case CodecInfoPackage.IDENTIFIABLE_CODEC_INFO__IDENTITY_INFO:
				setIdentityInfo((IdentityInfo)null);
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
			case CodecInfoPackage.IDENTIFIABLE_CODEC_INFO__IDENTITY_INFO:
				return identityInfo != null;
		}
		return super.eIsSet(featureID);
	}

} //IdentifiableCodecInfoImpl
