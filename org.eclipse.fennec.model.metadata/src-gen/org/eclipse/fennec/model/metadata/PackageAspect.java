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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Package Aspect</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base class for aspects attached to PackageMetadata. Provides a bidirectional reference to the owning PackageMetadata, enabling navigation from the aspect back to the metadata context.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.PackageAspect#getPackageMetadata <em>Package Metadata</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getPackageAspect()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface PackageAspect extends Aspect {
	/**
	 * Returns the value of the '<em><b>Package Metadata</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.fennec.model.metadata.PackageMetadata#getAspects <em>Aspects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The PackageMetadata that contains this aspect. Bidirectional opposite of PackageMetadata.aspects. Use packageMetadata.getEPackage() to access the original EPackage.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Package Metadata</em>' container reference.
	 * @see #setPackageMetadata(PackageMetadata)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getPackageAspect_PackageMetadata()
	 * @see org.eclipse.fennec.model.metadata.PackageMetadata#getAspects
	 * @model opposite="aspects" transient="false"
	 * @generated
	 */
	PackageMetadata getPackageMetadata();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.PackageAspect#getPackageMetadata <em>Package Metadata</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package Metadata</em>' container reference.
	 * @see #getPackageMetadata()
	 * @generated
	 */
	void setPackageMetadata(PackageMetadata value);

} // PackageAspect
