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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package Codec Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.PackageCodecInfoImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.PackageCodecInfoImpl#getEPackage <em>EPackage</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.PackageCodecInfoImpl#getSubPackageCodecInfo <em>Sub Package Codec Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.PackageCodecInfoImpl#getEClassCodecInfo <em>EClass Codec Info</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PackageCodecInfoImpl extends MinimalEObjectImpl.Container implements PackageCodecInfo {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getEPackage() <em>EPackage</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEPackage()
	 * @generated
	 * @ordered
	 */
	protected EPackage ePackage;

	/**
	 * The cached value of the '{@link #getSubPackageCodecInfo() <em>Sub Package Codec Info</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubPackageCodecInfo()
	 * @generated
	 * @ordered
	 */
	protected EList<PackageCodecInfo> subPackageCodecInfo;

	/**
	 * The cached value of the '{@link #getEClassCodecInfo() <em>EClass Codec Info</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEClassCodecInfo()
	 * @generated
	 * @ordered
	 */
	protected EList<EClassCodecInfo> eClassCodecInfo;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PackageCodecInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecInfoPackage.Literals.PACKAGE_CODEC_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.PACKAGE_CODEC_INFO__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EPackage getEPackage() {
		if (ePackage != null && ePackage.eIsProxy()) {
			InternalEObject oldEPackage = (InternalEObject)ePackage;
			ePackage = (EPackage)eResolveProxy(oldEPackage);
			if (ePackage != oldEPackage) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodecInfoPackage.PACKAGE_CODEC_INFO__EPACKAGE, oldEPackage, ePackage));
			}
		}
		return ePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EPackage basicGetEPackage() {
		return ePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEPackage(EPackage newEPackage) {
		EPackage oldEPackage = ePackage;
		ePackage = newEPackage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.PACKAGE_CODEC_INFO__EPACKAGE, oldEPackage, ePackage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PackageCodecInfo> getSubPackageCodecInfo() {
		if (subPackageCodecInfo == null) {
			subPackageCodecInfo = new EObjectContainmentEList<PackageCodecInfo>(PackageCodecInfo.class, this, CodecInfoPackage.PACKAGE_CODEC_INFO__SUB_PACKAGE_CODEC_INFO);
		}
		return subPackageCodecInfo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EClassCodecInfo> getEClassCodecInfo() {
		if (eClassCodecInfo == null) {
			eClassCodecInfo = new EObjectContainmentEList<EClassCodecInfo>(EClassCodecInfo.class, this, CodecInfoPackage.PACKAGE_CODEC_INFO__ECLASS_CODEC_INFO);
		}
		return eClassCodecInfo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecInfoPackage.PACKAGE_CODEC_INFO__SUB_PACKAGE_CODEC_INFO:
				return ((InternalEList<?>)getSubPackageCodecInfo()).basicRemove(otherEnd, msgs);
			case CodecInfoPackage.PACKAGE_CODEC_INFO__ECLASS_CODEC_INFO:
				return ((InternalEList<?>)getEClassCodecInfo()).basicRemove(otherEnd, msgs);
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
			case CodecInfoPackage.PACKAGE_CODEC_INFO__ID:
				return getId();
			case CodecInfoPackage.PACKAGE_CODEC_INFO__EPACKAGE:
				if (resolve) return getEPackage();
				return basicGetEPackage();
			case CodecInfoPackage.PACKAGE_CODEC_INFO__SUB_PACKAGE_CODEC_INFO:
				return getSubPackageCodecInfo();
			case CodecInfoPackage.PACKAGE_CODEC_INFO__ECLASS_CODEC_INFO:
				return getEClassCodecInfo();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case CodecInfoPackage.PACKAGE_CODEC_INFO__ID:
				setId((String)newValue);
				return;
			case CodecInfoPackage.PACKAGE_CODEC_INFO__EPACKAGE:
				setEPackage((EPackage)newValue);
				return;
			case CodecInfoPackage.PACKAGE_CODEC_INFO__SUB_PACKAGE_CODEC_INFO:
				getSubPackageCodecInfo().clear();
				getSubPackageCodecInfo().addAll((Collection<? extends PackageCodecInfo>)newValue);
				return;
			case CodecInfoPackage.PACKAGE_CODEC_INFO__ECLASS_CODEC_INFO:
				getEClassCodecInfo().clear();
				getEClassCodecInfo().addAll((Collection<? extends EClassCodecInfo>)newValue);
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
			case CodecInfoPackage.PACKAGE_CODEC_INFO__ID:
				setId(ID_EDEFAULT);
				return;
			case CodecInfoPackage.PACKAGE_CODEC_INFO__EPACKAGE:
				setEPackage((EPackage)null);
				return;
			case CodecInfoPackage.PACKAGE_CODEC_INFO__SUB_PACKAGE_CODEC_INFO:
				getSubPackageCodecInfo().clear();
				return;
			case CodecInfoPackage.PACKAGE_CODEC_INFO__ECLASS_CODEC_INFO:
				getEClassCodecInfo().clear();
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
			case CodecInfoPackage.PACKAGE_CODEC_INFO__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case CodecInfoPackage.PACKAGE_CODEC_INFO__EPACKAGE:
				return ePackage != null;
			case CodecInfoPackage.PACKAGE_CODEC_INFO__SUB_PACKAGE_CODEC_INFO:
				return subPackageCodecInfo != null && !subPackageCodecInfo.isEmpty();
			case CodecInfoPackage.PACKAGE_CODEC_INFO__ECLASS_CODEC_INFO:
				return eClassCodecInfo != null && !eClassCodecInfo.isEmpty();
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
		result.append(" (id: ");
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //PackageCodecInfoImpl
