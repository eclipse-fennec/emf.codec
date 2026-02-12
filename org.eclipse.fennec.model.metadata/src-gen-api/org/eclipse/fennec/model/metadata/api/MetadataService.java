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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.fennec.model.metadata.ClassAspect;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.ClassProfile;
import org.eclipse.fennec.model.metadata.FeatureAspect;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.MetadataRegistry;
import org.eclipse.fennec.model.metadata.PackageAspect;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.PackageProfile;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Metadata Service</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Consumer-facing read-only interface for accessing pre-computed model metadata. Provides metadata lookups by various criteria, aspect access by type ID, profile access for pre-computed configurations, and index reader access for fast queries. Does NOT provide lifecycle management (register/unregister) — use MetadataWhiteboard for that. In OSGi, consumers inject this interface to access metadata without admin privileges.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.metadata.api.ApiPackage#getMetadataService()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface MetadataService {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get the index reader for fast metadata lookups. The index is automatically maintained when packages are registered/unregistered via MetadataWhiteboard.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	MetadataIndexReader getIndexReader();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get metadata for a registered EPackage by its namespace URI. Returns null if the package is not registered.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	PackageMetadata getPackageMetadata(String nsURI);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get metadata for an EClass. The EClass's EPackage must have been registered. Returns null if not found.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassMetadata getClassMetadata(EClass eClass);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get metadata for an EClass by its full EMF URI (e.g., 'http://example.org/model#//Person'). Returns null if not found.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassMetadata getClassMetadataByURI(String uri);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get metadata for an EClass by its name and package namespace URI. Returns null if not found.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassMetadata getClassMetadataByName(String className, String nsURI);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get metadata for an EStructuralFeature. The feature's owning EClass's EPackage must have been registered. Returns null if not found.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	FeatureMetadata getFeatureMetadata(EStructuralFeature feature);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get metadata for an EStructuralFeature by its full EMF URI (e.g., 'http://example.org/model#//Person/name'). Returns null if not found.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	FeatureMetadata getFeatureMetadataByURI(String uri);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get metadata for an EStructuralFeature by feature name, class name, and package namespace URI. Returns null if not found.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	FeatureMetadata getFeatureMetadataByName(String featureName, String className, String nsURI);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get metadata for an EStructuralFeature by feature name and owning ClassMetadata. More efficient than getFeatureMetadataByName when ClassMetadata is already known. Returns null if not found.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	FeatureMetadata getFeatureMetadataFromClass(String featureName, ClassMetadata classMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get a specific aspect from an EPackage's metadata by aspect type ID (e.g., 'codec'). Returns null if the package is not registered or has no aspect with the given type ID.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	PackageAspect getPackageAspect(EPackage ePackage, String aspectTypeId);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get a specific aspect from an EClass's metadata by aspect type ID (e.g., 'codec'). Returns null if the class is not registered or has no aspect with the given type ID.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassAspect getClassAspect(EClass eClass, String aspectTypeId);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get a specific aspect from an EStructuralFeature's metadata by aspect type ID (e.g., 'codec'). Returns null if the feature's class is not registered or has no aspect with the given type ID.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	FeatureAspect getFeatureAspect(EStructuralFeature feature, String aspectTypeId);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get the pre-computed profile for an EPackage by aspect provider type ID (e.g., 'codec'). Profiles contain the fully resolved annotation-layer configuration for all classes in the package. Returns null if the package is not registered or the provider did not build a profile.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	PackageProfile getPackageProfile(EPackage ePackage, String typeId);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get the pre-computed profile for an EPackage by namespace URI and aspect provider type ID. Returns null if the package is not registered or the provider did not build a profile.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	PackageProfile getPackageProfileByNsURI(String nsURI, String typeId);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get the pre-computed profile for an EClass by aspect provider type ID. The profile contains the fully resolved annotation-layer configuration for this class and all its features, with annotation-internal inheritance already applied (feature inherits from class, class inherits from package). Returns null if the class is not registered or the provider did not build a profile for this class. Implementation navigates ClassMetadata → PackageMetadata → profiles → classProfiles.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassProfile getClassProfile(EClass eClass, String typeId);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get the pre-computed profile for an EClass by its full EMF URI and aspect provider type ID. Returns null if the class is not registered or the provider did not build a profile for this class.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassProfile getClassProfileByURI(String eClassURI, String typeId);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Get the full metadata registry containing all registered PackageMetadata instances. Useful for serialization/caching of the entire metadata state.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	MetadataRegistry getRegistry();

} // MetadataService
