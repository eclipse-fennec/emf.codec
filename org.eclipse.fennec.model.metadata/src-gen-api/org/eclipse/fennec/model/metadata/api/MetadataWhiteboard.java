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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.fennec.model.metadata.PackageMetadata;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Metadata Whiteboard</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Admin/lifecycle interface for metadata management. Extends MetadataService with package registration/unregistration, aspect provider lifecycle, and index management. Used by OSGi whiteboard components and bundle trackers to manage the metadata system. In OSGi, the implementation is registered under both MetadataService and MetadataWhiteboard interfaces. Consumers inject MetadataService (read-only), admins inject MetadataWhiteboard (full lifecycle control).
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.metadata.api.ApiPackage#getMetadataWhiteboard()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface MetadataWhiteboard extends MetadataService {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Register an EPackage and build metadata for all its classes and features. Calls all registered AspectProviders to build aspects, resolves cross-references, then calls buildProfiles on each provider. Automatically indexes the metadata. Returns the existing metadata if the package is already registered (no re-computation). To refresh, unregister first, then re-register.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	PackageMetadata registerPackage(EPackage ePackage);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Unregister an EPackage and remove all associated metadata, aspects, and profiles. Automatically removes from index. No-op if the package is not registered.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void unregisterPackage(EPackage ePackage);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Register an AspectProvider to contribute aspects and profiles when packages are registered. If packages are already registered, the provider's build methods are called immediately for all existing packages (late registration). After building aspects, buildProfiles is called with a filtered metadata copy containing only this provider's aspects.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void registerAspectProvider(AspectProvider provider);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Unregister an AspectProvider. Removes all aspects and profiles with this provider's typeId from all registered packages.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void unregisterAspectProvider(AspectProvider provider);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get all currently registered AspectProviders.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	EList<AspectProvider> getAspectProviders();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get the currently configured MetadataIndex implementation. Returns null if no index is set.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	MetadataIndex getMetadataIndex();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Set the MetadataIndex implementation. Called by OSGi DS to inject the index (e.g., MapBasedMetadataIndex). If packages are already registered, the index is populated with all existing metadata. Replaces any previously set index.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void setMetadataIndex(MetadataIndex index);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Remove the given MetadataIndex implementation. Called by OSGi DS when the index service is unbound. The parameter must match the currently set index — if it does not match, this is a no-op (guards against out-of-order DS lifecycle events). Clears the index before removing it. After this call, getIndexReader() on MetadataService returns null until a new index is set.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void unsetMetadataIndex(MetadataIndex index);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Add a MetadataHandler to receive lifecycle callbacks. If packages are already registered, onPackageRegistered is called immediately for each existing package (late binding, like setMetadataIndex).
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void addMetadataHandler(MetadataHandler handler);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Remove a MetadataHandler. Calls clear() on the handler before removing it.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void removeMetadataHandler(MetadataHandler handler);

} // MetadataWhiteboard
