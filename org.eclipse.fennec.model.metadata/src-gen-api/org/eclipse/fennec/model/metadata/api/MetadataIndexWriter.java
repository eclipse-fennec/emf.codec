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

import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.PackageMetadata;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Metadata Index Writer</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Write interface for index maintenance. Used internally by MetadataService to keep the index in sync when packages are registered or unregistered. Not intended for direct consumer use.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.metadata.api.ApiPackage#getMetadataIndexWriter()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface MetadataIndexWriter {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Index a PackageMetadata and all its contained ClassMetadata and FeatureMetadata. Called by MetadataService after registerPackage completes.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void indexPackage(PackageMetadata packageMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Index a single ClassMetadata and all its contained FeatureMetadata.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void indexClass(ClassMetadata classMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Index a single FeatureMetadata.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void indexFeature(FeatureMetadata featureMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Remove a PackageMetadata and all its contained metadata from the index. Called by MetadataService during unregisterPackage.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void removePackage(PackageMetadata packageMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Remove a ClassMetadata and all its contained FeatureMetadata from the index.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void removeClass(ClassMetadata classMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Remove a FeatureMetadata from the index.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void removeFeature(FeatureMetadata featureMetadata);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Clear all index entries. Called during MetadataService shutdown or when replacing the index implementation.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void clear();

} // MetadataIndexWriter
