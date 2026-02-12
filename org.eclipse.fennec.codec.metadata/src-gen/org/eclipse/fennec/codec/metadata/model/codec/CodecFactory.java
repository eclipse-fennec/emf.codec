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
package org.eclipse.fennec.codec.metadata.model.codec;

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage
 * @generated
 */
@ProviderType
public interface CodecFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CodecFactory eINSTANCE = org.eclipse.fennec.codec.metadata.model.codec.impl.CodecFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Type Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Type Serialization Config</em>'.
	 * @generated
	 */
	TypeSerializationConfig createTypeSerializationConfig();

	/**
	 * Returns a new object of class '<em>Id Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Id Serialization Config</em>'.
	 * @generated
	 */
	IdSerializationConfig createIdSerializationConfig();

	/**
	 * Returns a new object of class '<em>Reference Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Reference Serialization Config</em>'.
	 * @generated
	 */
	ReferenceSerializationConfig createReferenceSerializationConfig();

	/**
	 * Returns a new object of class '<em>Super Type Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Super Type Serialization Config</em>'.
	 * @generated
	 */
	SuperTypeSerializationConfig createSuperTypeSerializationConfig();

	/**
	 * Returns a new object of class '<em>Feature Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Feature Serialization Config</em>'.
	 * @generated
	 */
	FeatureSerializationConfig createFeatureSerializationConfig();

	/**
	 * Returns a new object of class '<em>Class Codec Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Class Codec Aspect</em>'.
	 * @generated
	 */
	ClassCodecAspect createClassCodecAspect();

	/**
	 * Returns a new object of class '<em>Feature Codec Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Feature Codec Aspect</em>'.
	 * @generated
	 */
	FeatureCodecAspect createFeatureCodecAspect();

	/**
	 * Returns a new object of class '<em>Reference Codec Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Reference Codec Aspect</em>'.
	 * @generated
	 */
	ReferenceCodecAspect createReferenceCodecAspect();

	/**
	 * Returns a new object of class '<em>Package Profile</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Package Profile</em>'.
	 * @generated
	 */
	CodecPackageProfile createCodecPackageProfile();

	/**
	 * Returns a new object of class '<em>Class Profile</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Class Profile</em>'.
	 * @generated
	 */
	CodecClassProfile createCodecClassProfile();

	/**
	 * Returns a new object of class '<em>Config</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Config</em>'.
	 * @generated
	 */
	CodecConfig createCodecConfig();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	CodecPackage getCodecPackage();

} //CodecFactory
