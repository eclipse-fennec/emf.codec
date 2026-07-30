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

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package Profile</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecPackageProfileImpl#getClassProfiles <em>Class Profiles</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CodecPackageProfileImpl extends MinimalEObjectImpl.Container implements CodecPackageProfile {
	/**
	 * The cached value of the '{@link #getClassProfiles() <em>Class Profiles</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClassProfiles()
	 * @generated
	 * @ordered
	 */
	protected EList<CodecClassProfile> classProfiles;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CodecPackageProfileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.CODEC_PACKAGE_PROFILE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<CodecClassProfile> getClassProfiles() {
		if (classProfiles == null) {
			classProfiles = new EObjectContainmentEList<CodecClassProfile>(CodecClassProfile.class, this, CodecPackage.CODEC_PACKAGE_PROFILE__CLASS_PROFILES);
		}
		return classProfiles;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecPackage.CODEC_PACKAGE_PROFILE__CLASS_PROFILES:
				return ((InternalEList<?>)getClassProfiles()).basicRemove(otherEnd, msgs);
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
			case CodecPackage.CODEC_PACKAGE_PROFILE__CLASS_PROFILES:
				return getClassProfiles();
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
			case CodecPackage.CODEC_PACKAGE_PROFILE__CLASS_PROFILES:
				getClassProfiles().clear();
				getClassProfiles().addAll((Collection<? extends CodecClassProfile>)newValue);
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
			case CodecPackage.CODEC_PACKAGE_PROFILE__CLASS_PROFILES:
				getClassProfiles().clear();
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
			case CodecPackage.CODEC_PACKAGE_PROFILE__CLASS_PROFILES:
				return classProfiles != null && !classProfiles.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //CodecPackageProfileImpl
