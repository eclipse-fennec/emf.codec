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
 * A representation of the model object '<em><b>Aspect</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base class for all aspects. An aspect represents a cross-cutting concern (e.g., codec, ORM, history) attached to model metadata. Aspects are created by AspectProviders during package registration and contained by their respective metadata elements (PackageMetadata, ClassMetadata, or FeatureMetadata).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.Aspect#getTypeId <em>Type Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.Aspect#getDiagnostics <em>Diagnostics</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getAspect()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface Aspect extends EObject {
	/**
	 * Returns the value of the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Unique identifier for the aspect type (e.g., 'codec', 'orm', 'history'). Set by the MetadataService from the AspectProvider's getAspectTypeId() value. Used to look up specific aspects by type.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Id</em>' attribute.
	 * @see #setTypeId(String)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getAspect_TypeId()
	 * @model
	 * @generated
	 */
	String getTypeId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.Aspect#getTypeId <em>Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Id</em>' attribute.
	 * @see #getTypeId()
	 * @generated
	 */
	void setTypeId(String value);

	/**
	 * Returns the value of the '<em><b>Diagnostics</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.metadata.MetadataDiagnostic}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Diagnostics collected during aspect creation (e.g., annotation parsing warnings, invalid key combinations). Managed separately from the metadata element's own diagnostics.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Diagnostics</em>' containment reference list.
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getAspect_Diagnostics()
	 * @model containment="true"
	 * @generated
	 */
	EList<MetadataDiagnostic> getDiagnostics();

} // Aspect
