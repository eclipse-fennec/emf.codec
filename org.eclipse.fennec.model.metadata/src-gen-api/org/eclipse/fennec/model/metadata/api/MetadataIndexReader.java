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

import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.FeatureMetadata;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Metadata Index Reader</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Read-only query interface for metadata lookups. Provides fast indexed queries for ClassMetadata and FeatureMetadata by various criteria (name, URI, instance class name, annotation). Consumers inject MetadataService and call getIndexReader() to access this interface.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.metadata.api.ApiPackage#getMetadataIndexReader()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface MetadataIndexReader {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Find ClassMetadata by Java instanceClassName within a specific package. Returns null if not found. Used by CLASS TypeStrategy deserialization.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassMetadata findByInstanceClassName(String nsURI, String instanceClassName);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Find all ClassMetadata with matching Java instanceClassName across all indexed packages. May return multiple results (e.g., java.util.Map$Entry pattern). Used by CLASS TypeStrategy deserialization when no context schema is available.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	EList<ClassMetadata> findAllByInstanceClassName(String instanceClassName);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Find ClassMetadata by EClass name within a specific package. Returns null if not found. Used by NAME TypeStrategy deserialization.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassMetadata findByClassName(String nsURI, String className);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Find all ClassMetadata with matching EClass name across all indexed packages.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	EList<ClassMetadata> findAllByClassName(String className);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Find ClassMetadata by its full EMF URI (e.g., 'http://example.org/model#//Person'). Used by URI TypeStrategy deserialization.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	ClassMetadata findClassByURI(String uri);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Find FeatureMetadata by its full EMF URI (e.g., 'http://example.org/model#//Person/name').
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	FeatureMetadata findFeatureByURI(String uri);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Find all ClassMetadata where the EClass has an EAnnotation with the given source, key, and value. Pass null for value to match any value for the given key.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	EList<ClassMetadata> findClassesByAnnotation(String annotationSource, String key, String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Find all FeatureMetadata where the EStructuralFeature has an EAnnotation with the given source, key, and value. Pass null for value to match any value for the given key.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	EList<FeatureMetadata> findFeaturesByAnnotation(String annotationSource, String key, String value);

} // MetadataIndexReader
