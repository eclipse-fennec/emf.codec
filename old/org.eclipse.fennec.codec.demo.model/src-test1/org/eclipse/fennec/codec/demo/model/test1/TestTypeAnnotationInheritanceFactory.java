/*
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *      Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.codec.demo.model.test1;

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage
 * @generated
 */
@ProviderType
public interface TestTypeAnnotationInheritanceFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TestTypeAnnotationInheritanceFactory eINSTANCE = org.eclipse.fennec.codec.demo.model.test1.impl.TestTypeAnnotationInheritanceFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Test1 Person</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Test1 Person</em>'.
	 * @generated
	 */
	Test1Person createTest1Person();

	/**
	 * Returns a new object of class '<em>Test1 Business Person</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Test1 Business Person</em>'.
	 * @generated
	 */
	Test1BusinessPerson createTest1BusinessPerson();

	/**
	 * Returns a new object of class '<em>Meeting</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Meeting</em>'.
	 * @generated
	 */
	Meeting createMeeting();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	TestTypeAnnotationInheritancePackage getTestTypeAnnotationInheritancePackage();

} //TestTypeAnnotationInheritanceFactory
