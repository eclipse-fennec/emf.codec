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
 * A representation of the model object '<em><b>Diagnostic Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Interface for metadata elements that can contain diagnostics. Implemented by PackageMetadata, ClassMetadata, and FeatureMetadata. Provides both direct diagnostics and aggregated diagnostics from the containment subtree.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.DiagnosticContainer#getDiagnostics <em>Diagnostics</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.DiagnosticContainer#getAllDiagnostics <em>All Diagnostics</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getDiagnosticContainer()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface DiagnosticContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>Diagnostics</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.metadata.MetadataDiagnostic}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Diagnostics directly owned by this metadata element. Does not include diagnostics from contained children or aspects.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Diagnostics</em>' containment reference list.
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getDiagnosticContainer_Diagnostics()
	 * @model containment="true"
	 * @generated
	 */
	EList<MetadataDiagnostic> getDiagnostics();

	/**
	 * Returns the value of the '<em><b>All Diagnostics</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.metadata.MetadataDiagnostic}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * All diagnostics from this element and its contained metadata subtree. For FeatureMetadata: same as diagnostics (leaf node). For ClassMetadata: own diagnostics + all feature diagnostics. For PackageMetadata: own diagnostics + all class allDiagnostics. Note: Aspect diagnostics are managed separately and not aggregated here.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>All Diagnostics</em>' reference list.
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getDiagnosticContainer_AllDiagnostics()
	 * @model transient="true" changeable="false" volatile="true" derived="true"
	 * @generated
	 */
	EList<MetadataDiagnostic> getAllDiagnostics();

} // DiagnosticContainer
