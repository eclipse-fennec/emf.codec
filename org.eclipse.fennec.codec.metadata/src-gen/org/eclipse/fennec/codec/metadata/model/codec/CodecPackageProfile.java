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
package org.eclipse.fennec.codec.metadata.model.codec;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Package Profile</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Codec-specific package profile containing pre-computed, fully resolved annotation-layer configurations for all classes in a package. Built by the codec's metadata handler after all metadata and aspects are constructed. Contains one CodecClassProfile per EClass with annotation-internal inheritance already applied (feature inherits from class, class inherits from package defaults). At runtime, serves as the static base that dynamic configuration (options, resource, factory, module) merges on top of.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile#getClassProfiles <em>Class Profiles</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecPackageProfile()
 * @model
 * @generated
 */
@ProviderType
public interface CodecPackageProfile extends EObject {
	/**
	 * Returns the value of the '<em><b>Class Profiles</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Pre-computed profiles for each EClass in the package. One CodecClassProfile per EClass that the codec is interested in.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Class Profiles</em>' containment reference list.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecPackageProfile_ClassProfiles()
	 * @model containment="true"
	 * @generated
	 */
	EList<CodecClassProfile> getClassProfiles();

} // CodecPackageProfile
