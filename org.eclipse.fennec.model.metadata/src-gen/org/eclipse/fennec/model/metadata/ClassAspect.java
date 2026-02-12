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
 * A representation of the model object '<em><b>Class Aspect</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Base class for aspects attached to ClassMetadata. Provides a bidirectional reference to the owning ClassMetadata, enabling navigation from the aspect back to the metadata context and its feature tree.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.ClassAspect#getClassMetadata <em>Class Metadata</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getClassAspect()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface ClassAspect extends Aspect {
	/**
	 * Returns the value of the '<em><b>Class Metadata</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.fennec.model.metadata.ClassMetadata#getAspects <em>Aspects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The ClassMetadata that contains this aspect. Bidirectional opposite of ClassMetadata.aspects. Use classMetadata.getEClass() to access the original EClass. Navigate classMetadata.getFeatures() to access feature metadata and their aspects.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Class Metadata</em>' container reference.
	 * @see #setClassMetadata(ClassMetadata)
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getClassAspect_ClassMetadata()
	 * @see org.eclipse.fennec.model.metadata.ClassMetadata#getAspects
	 * @model opposite="aspects" transient="false"
	 * @generated
	 */
	ClassMetadata getClassMetadata();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.metadata.ClassAspect#getClassMetadata <em>Class Metadata</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class Metadata</em>' container reference.
	 * @see #getClassMetadata()
	 * @generated
	 */
	void setClassMetadata(ClassMetadata value);

} // ClassAspect
