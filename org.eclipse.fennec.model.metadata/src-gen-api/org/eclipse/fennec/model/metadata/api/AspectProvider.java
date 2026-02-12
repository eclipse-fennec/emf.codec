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

import org.eclipse.fennec.model.metadata.AttributeMetadata;
import org.eclipse.fennec.model.metadata.ClassAspect;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.FeatureAspect;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.PackageAspect;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.PackageProfile;
import org.eclipse.fennec.model.metadata.ReferenceMetadata;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Aspect Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Extension point for contributing aspects and profiles to model metadata. Registered with MetadataWhiteboard and called during package registration. Each provider has a unique typeId (e.g., 'codec', 'orm') and builds aspects for packages, classes, and features. After all aspects are built, buildProfiles is called with a filtered copy of the metadata containing only this provider's aspects. Implementations should be stateless — all context is provided via the metadata parameters.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.metadata.api.ApiPackage#getAspectProvider()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface AspectProvider {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns the unique identifier for this aspect type (e.g., 'codec', 'orm', 'history'). Used by the MetadataService to set typeId on built aspects and profiles, and to filter aspects for provider isolation during buildProfiles.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	String getAspectTypeId();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Build a PackageAspect for the given PackageMetadata. Called once per package during registration. The metadata provides access to the EPackage via packageMetadata.getEPackage(). Returns null if this provider does not contribute package-level aspects.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	PackageAspect buildPackageAspect(PackageMetadata packageMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Build a ClassAspect for the given ClassMetadata. Called once per EClass during registration, after all feature aspects for this class have been built. The metadata provides access to the EClass via classMetadata.getEClass(), and to the feature tree via classMetadata.getFeatures(). Returns null if this provider does not contribute class-level aspects.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassAspect buildClassAspect(ClassMetadata classMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Build a FeatureAspect for the given FeatureMetadata. Called once per EStructuralFeature during registration. The metadata provides access to the feature via featureMetadata.getEFeature(), and to the owning class via featureMetadata.getClassMetadata(). Returns null if this provider does not contribute feature-level aspects. Default dispatch: the MetadataService calls buildAttributeAspect for EAttributes and buildReferenceAspect for EReferences, falling back to this method if those return null.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	FeatureAspect buildFeatureAspect(FeatureMetadata featureMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Build a FeatureAspect for an AttributeMetadata. Override for attribute-specific logic. The metadata provides access to the EAttribute via attributeMetadata.getEAttribute() and to the owning class via attributeMetadata.getClassMetadata(). Returns null to fall back to buildFeatureAspect.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	FeatureAspect buildAttributeAspect(AttributeMetadata attributeMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Build a FeatureAspect for a ReferenceMetadata. Override for reference-specific logic. The metadata provides access to the EReference via referenceMetadata.getEReference(), target class via referenceMetadata.getTargetClassMetadata(), and owning class via referenceMetadata.getClassMetadata(). Returns null to fall back to buildFeatureAspect.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	FeatureAspect buildReferenceAspect(ReferenceMetadata referenceMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Build pre-computed profiles for a package after all metadata and aspects are fully constructed (including cross-reference resolution). Called once per package per provider, after registerPackage completes metadata/aspect construction. The parameter is an immutable copy of the PackageMetadata containing ONLY this provider's aspects (filtered by typeId). Other providers' aspects are removed to ensure isolation. The provider iterates the copy's classes and builds a PackageProfile containing ClassProfiles with fully resolved annotation-layer configuration (annotation-internal hierarchy pre-merged: feature inherits from class, class inherits from package defaults). Returns null if this provider does not produce profiles. The returned profile is stored in the original PackageMetadata.profiles by the MetadataService.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	PackageProfile buildProfiles(PackageMetadata filteredMetadataCopy);

} // AspectProvider
