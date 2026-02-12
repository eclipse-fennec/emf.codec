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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.MetadataDiagnostic;
import org.eclipse.fennec.model.metadata.MetadataPackage;
import org.eclipse.fennec.model.metadata.PackageAspect;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.PackageProfile;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package Metadata</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl#getDiagnostics <em>Diagnostics</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl#getAllDiagnostics <em>All Diagnostics</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl#getEPackage <em>EPackage</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl#getNsURI <em>Ns URI</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl#getClasses <em>Classes</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl#getAspects <em>Aspects</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.PackageMetadataImpl#getProfiles <em>Profiles</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PackageMetadataImpl extends MinimalEObjectImpl.Container implements PackageMetadata {
	/**
	 * The cached value of the '{@link #getDiagnostics() <em>Diagnostics</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiagnostics()
	 * @generated
	 * @ordered
	 */
	protected EList<MetadataDiagnostic> diagnostics;

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
	 * The default value of the '{@link #getNsURI() <em>Ns URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNsURI()
	 * @generated
	 * @ordered
	 */
	protected static final String NS_URI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNsURI() <em>Ns URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNsURI()
	 * @generated
	 * @ordered
	 */
	protected String nsURI = NS_URI_EDEFAULT;

	/**
	 * The cached value of the '{@link #getClasses() <em>Classes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClasses()
	 * @generated
	 * @ordered
	 */
	protected EList<ClassMetadata> classes;

	/**
	 * The cached value of the '{@link #getAspects() <em>Aspects</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAspects()
	 * @generated
	 * @ordered
	 */
	protected EList<PackageAspect> aspects;

	/**
	 * The cached value of the '{@link #getProfiles() <em>Profiles</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProfiles()
	 * @generated
	 * @ordered
	 */
	protected EList<PackageProfile> profiles;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PackageMetadataImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetadataPackage.Literals.PACKAGE_METADATA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<MetadataDiagnostic> getDiagnostics() {
		if (diagnostics == null) {
			diagnostics = new EObjectContainmentEList<MetadataDiagnostic>(MetadataDiagnostic.class, this, MetadataPackage.PACKAGE_METADATA__DIAGNOSTICS);
		}
		return diagnostics;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<MetadataDiagnostic> getAllDiagnostics() {
		EList<MetadataDiagnostic> result = new BasicEList<>();
		result.addAll(getDiagnostics());
		EClass eClassType = eClass();
		if (eClassType == MetadataPackage.Literals.PACKAGE_METADATA) {
		    for (ClassMetadata classMetadata : ((PackageMetadata) this).getClasses()) {
		        result.addAll(classMetadata.getAllDiagnostics());
		    }
		} else if (eClassType == MetadataPackage.Literals.CLASS_METADATA) {
		    for (FeatureMetadata featureMetadata : ((ClassMetadata) this).getFeatures()) {
		        result.addAll(featureMetadata.getAllDiagnostics());
		    }
		}
		return result;
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MetadataPackage.PACKAGE_METADATA__EPACKAGE, oldEPackage, ePackage));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.PACKAGE_METADATA__EPACKAGE, oldEPackage, ePackage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getNsURI() {
		return nsURI;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNsURI(String newNsURI) {
		String oldNsURI = nsURI;
		nsURI = newNsURI;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.PACKAGE_METADATA__NS_URI, oldNsURI, nsURI));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ClassMetadata> getClasses() {
		if (classes == null) {
			classes = new EObjectContainmentWithInverseEList<ClassMetadata>(ClassMetadata.class, this, MetadataPackage.PACKAGE_METADATA__CLASSES, MetadataPackage.CLASS_METADATA__PACKAGE);
		}
		return classes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PackageAspect> getAspects() {
		if (aspects == null) {
			aspects = new EObjectContainmentWithInverseEList<PackageAspect>(PackageAspect.class, this, MetadataPackage.PACKAGE_METADATA__ASPECTS, MetadataPackage.PACKAGE_ASPECT__PACKAGE_METADATA);
		}
		return aspects;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<PackageProfile> getProfiles() {
		if (profiles == null) {
			profiles = new EObjectContainmentEList<PackageProfile>(PackageProfile.class, this, MetadataPackage.PACKAGE_METADATA__PROFILES);
		}
		return profiles;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MetadataPackage.PACKAGE_METADATA__CLASSES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getClasses()).basicAdd(otherEnd, msgs);
			case MetadataPackage.PACKAGE_METADATA__ASPECTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getAspects()).basicAdd(otherEnd, msgs);
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
			case MetadataPackage.PACKAGE_METADATA__DIAGNOSTICS:
				return ((InternalEList<?>)getDiagnostics()).basicRemove(otherEnd, msgs);
			case MetadataPackage.PACKAGE_METADATA__CLASSES:
				return ((InternalEList<?>)getClasses()).basicRemove(otherEnd, msgs);
			case MetadataPackage.PACKAGE_METADATA__ASPECTS:
				return ((InternalEList<?>)getAspects()).basicRemove(otherEnd, msgs);
			case MetadataPackage.PACKAGE_METADATA__PROFILES:
				return ((InternalEList<?>)getProfiles()).basicRemove(otherEnd, msgs);
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
			case MetadataPackage.PACKAGE_METADATA__DIAGNOSTICS:
				return getDiagnostics();
			case MetadataPackage.PACKAGE_METADATA__ALL_DIAGNOSTICS:
				return getAllDiagnostics();
			case MetadataPackage.PACKAGE_METADATA__EPACKAGE:
				if (resolve) return getEPackage();
				return basicGetEPackage();
			case MetadataPackage.PACKAGE_METADATA__NS_URI:
				return getNsURI();
			case MetadataPackage.PACKAGE_METADATA__CLASSES:
				return getClasses();
			case MetadataPackage.PACKAGE_METADATA__ASPECTS:
				return getAspects();
			case MetadataPackage.PACKAGE_METADATA__PROFILES:
				return getProfiles();
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
			case MetadataPackage.PACKAGE_METADATA__DIAGNOSTICS:
				getDiagnostics().clear();
				getDiagnostics().addAll((Collection<? extends MetadataDiagnostic>)newValue);
				return;
			case MetadataPackage.PACKAGE_METADATA__EPACKAGE:
				setEPackage((EPackage)newValue);
				return;
			case MetadataPackage.PACKAGE_METADATA__NS_URI:
				setNsURI((String)newValue);
				return;
			case MetadataPackage.PACKAGE_METADATA__CLASSES:
				getClasses().clear();
				getClasses().addAll((Collection<? extends ClassMetadata>)newValue);
				return;
			case MetadataPackage.PACKAGE_METADATA__ASPECTS:
				getAspects().clear();
				getAspects().addAll((Collection<? extends PackageAspect>)newValue);
				return;
			case MetadataPackage.PACKAGE_METADATA__PROFILES:
				getProfiles().clear();
				getProfiles().addAll((Collection<? extends PackageProfile>)newValue);
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
			case MetadataPackage.PACKAGE_METADATA__DIAGNOSTICS:
				getDiagnostics().clear();
				return;
			case MetadataPackage.PACKAGE_METADATA__EPACKAGE:
				setEPackage((EPackage)null);
				return;
			case MetadataPackage.PACKAGE_METADATA__NS_URI:
				setNsURI(NS_URI_EDEFAULT);
				return;
			case MetadataPackage.PACKAGE_METADATA__CLASSES:
				getClasses().clear();
				return;
			case MetadataPackage.PACKAGE_METADATA__ASPECTS:
				getAspects().clear();
				return;
			case MetadataPackage.PACKAGE_METADATA__PROFILES:
				getProfiles().clear();
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
			case MetadataPackage.PACKAGE_METADATA__DIAGNOSTICS:
				return diagnostics != null && !diagnostics.isEmpty();
			case MetadataPackage.PACKAGE_METADATA__ALL_DIAGNOSTICS:
				return !getAllDiagnostics().isEmpty();
			case MetadataPackage.PACKAGE_METADATA__EPACKAGE:
				return ePackage != null;
			case MetadataPackage.PACKAGE_METADATA__NS_URI:
				return NS_URI_EDEFAULT == null ? nsURI != null : !NS_URI_EDEFAULT.equals(nsURI);
			case MetadataPackage.PACKAGE_METADATA__CLASSES:
				return classes != null && !classes.isEmpty();
			case MetadataPackage.PACKAGE_METADATA__ASPECTS:
				return aspects != null && !aspects.isEmpty();
			case MetadataPackage.PACKAGE_METADATA__PROFILES:
				return profiles != null && !profiles.isEmpty();
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
		result.append(" (nsURI: ");
		result.append(nsURI);
		result.append(')');
		return result.toString();
	}

} //PackageMetadataImpl
