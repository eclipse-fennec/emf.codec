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
 * A representation of the model object '<em><b>Feature Aspect</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base class for aspects attached to FeatureMetadata. Provides a bidirectional reference to the owning FeatureMetadata, enabling navigation from the aspect back to the metadata context and up to the class level.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.FeatureAspect#getFeatureMetadata <em>Feature Metadata</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getFeatureAspect()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface FeatureAspect extends Aspect {
	/**
	 * Returns the value of the '<em><b>Feature Metadata</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.fennec.model.metadata.FeatureMetadata#getAspects <em>Aspects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The FeatureMetadata that contains this aspect. Bidirectional opposite of FeatureMetadata.aspects. Use featureMetadata.getEFeature() to access the original EStructuralFeature. Navigate featureMetadata.getClassMetadata() to reach the owning class.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Feature Metadata</em>' container reference.
	 * @see #setFeatureMetadata(FeatureMetadata)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getFeatureAspect_FeatureMetadata()
	 * @see org.eclipse.fennec.model.metadata.FeatureMetadata#getAspects
	 * @model opposite="aspects" transient="false"
	 * @generated
	 */
	FeatureMetadata getFeatureMetadata();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.FeatureAspect#getFeatureMetadata <em>Feature Metadata</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature Metadata</em>' container reference.
	 * @see #getFeatureMetadata()
	 * @generated
	 */
	void setFeatureMetadata(FeatureMetadata value);

} // FeatureAspect
