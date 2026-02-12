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
package org.eclipse.fennec.model.metadata;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Package Profile</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base class for pre-computed, provider-specific profiles at the package level. A profile aggregates the fully resolved annotation-layer configuration for all classes in a package. Built by AspectProvider.buildProfiles() after all metadata and aspects are constructed. Concrete subclasses (e.g., CodecPackageProfile) contain provider-specific content. Contained by PackageMetadata.profiles (one per provider).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.PackageProfile#getTypeId <em>Type Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.PackageProfile#getClassProfiles <em>Class Profiles</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getPackageProfile()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface PackageProfile extends EObject {
	/**
	 * Returns the value of the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Identifier of the AspectProvider that built this profile (e.g., 'codec', 'orm'). Matches the provider's getAspectTypeId() value. Used to look up profiles by provider type.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Id</em>' attribute.
	 * @see #setTypeId(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getPackageProfile_TypeId()
	 * @model
	 * @generated
	 */
	String getTypeId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.PackageProfile#getTypeId <em>Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Id</em>' attribute.
	 * @see #getTypeId()
	 * @generated
	 */
	void setTypeId(String value);

	/**
	 * Returns the value of the '<em><b>Class Profiles</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.metadata.ClassProfile}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Pre-computed profiles for each EClass in the package. One ClassProfile per EClass that the provider is interested in.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Class Profiles</em>' containment reference list.
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getPackageProfile_ClassProfiles()
	 * @model containment="true"
	 * @generated
	 */
	EList<ClassProfile> getClassProfiles();

} // PackageProfile
