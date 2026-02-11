/*
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 * 
 */
package org.eclipse.fennec.codec.info.codecinfo;

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage
 * @generated
 */
@ProviderType
public interface CodecInfoFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CodecInfoFactory eINSTANCE = org.eclipse.fennec.codec.info.codecinfo.impl.CodecInfoFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Package Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Package Codec Info</em>'.
	 * @generated
	 */
	PackageCodecInfo createPackageCodecInfo();

	/**
	 * Returns a new object of class '<em>EClass Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>EClass Codec Info</em>'.
	 * @generated
	 */
	EClassCodecInfo createEClassCodecInfo();

	/**
	 * Returns a new object of class '<em>Feature Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Feature Codec Info</em>'.
	 * @generated
	 */
	FeatureCodecInfo createFeatureCodecInfo();

	/**
	 * Returns a new object of class '<em>Type Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Type Info</em>'.
	 * @generated
	 */
	TypeInfo createTypeInfo();

	/**
	 * Returns a new object of class '<em>Super Type Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Super Type Info</em>'.
	 * @generated
	 */
	SuperTypeInfo createSuperTypeInfo();

	/**
	 * Returns a new object of class '<em>Identity Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Identity Info</em>'.
	 * @generated
	 */
	IdentityInfo createIdentityInfo();

	/**
	 * Returns a new object of class '<em>Holder</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Holder</em>'.
	 * @generated
	 */
	CodecInfoHolder createCodecInfoHolder();

	/**
	 * Returns a new object of class '<em>Typed Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Typed Codec Info</em>'.
	 * @generated
	 */
	TypedCodecInfo createTypedCodecInfo();

	/**
	 * Returns a new object of class '<em>Identifiable Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Identifiable Codec Info</em>'.
	 * @generated
	 */
	IdentifiableCodecInfo createIdentifiableCodecInfo();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	CodecInfoPackage getCodecInfoPackage();

} //CodecInfoFactory
