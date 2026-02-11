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
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.info.codecinfo.TypedCodecInfo;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Typed Codec Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.TypedCodecInfoImpl#getTypeInfo <em>Type Info</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TypedCodecInfoImpl extends MinimalEObjectImpl.Container implements TypedCodecInfo {
	/**
	 * The cached value of the '{@link #getTypeInfo() <em>Type Info</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeInfo()
	 * @generated
	 * @ordered
	 */
	protected TypeInfo typeInfo;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TypedCodecInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecInfoPackage.Literals.TYPED_CODEC_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TypeInfo getTypeInfo() {
		return typeInfo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTypeInfo(TypeInfo newTypeInfo, NotificationChain msgs) {
		TypeInfo oldTypeInfo = typeInfo;
		typeInfo = newTypeInfo;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecInfoPackage.TYPED_CODEC_INFO__TYPE_INFO, oldTypeInfo, newTypeInfo);
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
	public void setTypeInfo(TypeInfo newTypeInfo) {
		if (newTypeInfo != typeInfo) {
			NotificationChain msgs = null;
			if (typeInfo != null)
				msgs = ((InternalEObject)typeInfo).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecInfoPackage.TYPED_CODEC_INFO__TYPE_INFO, null, msgs);
			if (newTypeInfo != null)
				msgs = ((InternalEObject)newTypeInfo).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecInfoPackage.TYPED_CODEC_INFO__TYPE_INFO, null, msgs);
			msgs = basicSetTypeInfo(newTypeInfo, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.TYPED_CODEC_INFO__TYPE_INFO, newTypeInfo, newTypeInfo));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecInfoPackage.TYPED_CODEC_INFO__TYPE_INFO:
				return basicSetTypeInfo(null, msgs);
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
			case CodecInfoPackage.TYPED_CODEC_INFO__TYPE_INFO:
				return getTypeInfo();
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
			case CodecInfoPackage.TYPED_CODEC_INFO__TYPE_INFO:
				setTypeInfo((TypeInfo)newValue);
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
			case CodecInfoPackage.TYPED_CODEC_INFO__TYPE_INFO:
				setTypeInfo((TypeInfo)null);
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
			case CodecInfoPackage.TYPED_CODEC_INFO__TYPE_INFO:
				return typeInfo != null;
		}
		return super.eIsSet(featureID);
	}

} //TypedCodecInfoImpl
