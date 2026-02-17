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
package org.eclipse.fennec.model.metadata.api;

import org.eclipse.fennec.model.metadata.PackageMetadata;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Metadata Handler</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Generic lifecycle callback triggered by MetadataWhiteboard when packages are registered or unregistered. Implementations can incrementally update their internal state (e.g., TypeDiscriminatorService) instead of rebuilding from scratch. Follows the same set/unset pattern as MetadataIndex.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.metadata.api.ApiPackage#getMetadataHandler()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface MetadataHandler {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Called after a package has been registered with the MetadataWhiteboard. The handler should incrementally update its state based on the new package metadata.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void onPackageRegistered(PackageMetadata packageMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Called before a package is removed from the MetadataWhiteboard. The handler should remove any state associated with this package.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void onPackageUnregistered(PackageMetadata packageMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Clear all internal state. Called when the handler is removed from the whiteboard.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void clear();

} // MetadataHandler
