/**
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
package org.eclipse.fennec.codec.demo.model.test1.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.codec.demo.model.test1.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TestTypeAnnotationInheritanceFactoryImpl extends EFactoryImpl implements TestTypeAnnotationInheritanceFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static TestTypeAnnotationInheritanceFactory init() {
		try {
			TestTypeAnnotationInheritanceFactory theTestTypeAnnotationInheritanceFactory = (TestTypeAnnotationInheritanceFactory)EPackage.Registry.INSTANCE.getEFactory(TestTypeAnnotationInheritancePackage.eNS_URI);
			if (theTestTypeAnnotationInheritanceFactory != null) {
				return theTestTypeAnnotationInheritanceFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new TestTypeAnnotationInheritanceFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TestTypeAnnotationInheritanceFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case TestTypeAnnotationInheritancePackage.TEST1_PERSON: return createTest1Person();
			case TestTypeAnnotationInheritancePackage.TEST1_BUSINESS_PERSON: return createTest1BusinessPerson();
			case TestTypeAnnotationInheritancePackage.MEETING: return createMeeting();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Test1Person createTest1Person() {
		Test1PersonImpl test1Person = new Test1PersonImpl();
		return test1Person;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Test1BusinessPerson createTest1BusinessPerson() {
		Test1BusinessPersonImpl test1BusinessPerson = new Test1BusinessPersonImpl();
		return test1BusinessPerson;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Meeting createMeeting() {
		MeetingImpl meeting = new MeetingImpl();
		return meeting;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TestTypeAnnotationInheritancePackage getTestTypeAnnotationInheritancePackage() {
		return (TestTypeAnnotationInheritancePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static TestTypeAnnotationInheritancePackage getPackage() {
		return TestTypeAnnotationInheritancePackage.eINSTANCE;
	}

} //TestTypeAnnotationInheritanceFactoryImpl
