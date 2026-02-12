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
package org.eclipse.fennec.model.metadata.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.fennec.model.metadata.MetadataPackage;
import org.eclipse.fennec.model.metadata.PackageAspect;
import org.eclipse.fennec.model.metadata.PackageMetadata;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package Aspect</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.PackageAspectImpl#getPackageMetadata <em>Package Metadata</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class PackageAspectImpl extends AspectImpl implements PackageAspect {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PackageAspectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetadataPackage.Literals.PACKAGE_ASPECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PackageMetadata getPackageMetadata() {
		if (eContainerFeatureID() != MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA) return null;
		return (PackageMetadata)eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPackageMetadata(PackageMetadata newPackageMetadata, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newPackageMetadata, MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPackageMetadata(PackageMetadata newPackageMetadata) {
		if (newPackageMetadata != eInternalContainer() || (eContainerFeatureID() != MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA && newPackageMetadata != null)) {
			if (EcoreUtil.isAncestor(this, newPackageMetadata))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newPackageMetadata != null)
				msgs = ((InternalEObject)newPackageMetadata).eInverseAdd(this, MetadataPackage.PACKAGE_METADATA__ASPECTS, PackageMetadata.class, msgs);
			msgs = basicSetPackageMetadata(newPackageMetadata, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA, newPackageMetadata, newPackageMetadata));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetPackageMetadata((PackageMetadata)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA:
				return basicSetPackageMetadata(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA:
				return eInternalContainer().eInverseRemove(this, MetadataPackage.PACKAGE_METADATA__ASPECTS, PackageMetadata.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA:
				return getPackageMetadata();
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
			case MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA:
				setPackageMetadata((PackageMetadata)newValue);
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
			case MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA:
				setPackageMetadata((PackageMetadata)null);
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
			case MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA:
				return getPackageMetadata() != null;
		}
		return super.eIsSet(featureID);
	}

} //PackageAspectImpl
