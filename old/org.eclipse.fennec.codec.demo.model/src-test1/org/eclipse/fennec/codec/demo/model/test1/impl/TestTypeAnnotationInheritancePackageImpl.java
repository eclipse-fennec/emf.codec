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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.codec.demo.model.test1.Meeting;
import org.eclipse.fennec.codec.demo.model.test1.Test1BusinessPerson;
import org.eclipse.fennec.codec.demo.model.test1.Test1Person;
import org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritanceFactory;
import org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TestTypeAnnotationInheritancePackageImpl extends EPackageImpl implements TestTypeAnnotationInheritancePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass test1PersonEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass test1BusinessPersonEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass meetingEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private TestTypeAnnotationInheritancePackageImpl() {
		super(eNS_URI, TestTypeAnnotationInheritanceFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link TestTypeAnnotationInheritancePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static TestTypeAnnotationInheritancePackage init() {
		if (isInited) return (TestTypeAnnotationInheritancePackage)EPackage.Registry.INSTANCE.getEPackage(TestTypeAnnotationInheritancePackage.eNS_URI);

		// Obtain or create and register package
		Object registeredTestTypeAnnotationInheritancePackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		TestTypeAnnotationInheritancePackageImpl theTestTypeAnnotationInheritancePackage = registeredTestTypeAnnotationInheritancePackage instanceof TestTypeAnnotationInheritancePackageImpl ? (TestTypeAnnotationInheritancePackageImpl)registeredTestTypeAnnotationInheritancePackage : new TestTypeAnnotationInheritancePackageImpl();

		isInited = true;

		// Create package meta-data objects
		theTestTypeAnnotationInheritancePackage.createPackageContents();

		// Initialize created meta-data
		theTestTypeAnnotationInheritancePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theTestTypeAnnotationInheritancePackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(TestTypeAnnotationInheritancePackage.eNS_URI, theTestTypeAnnotationInheritancePackage);
		return theTestTypeAnnotationInheritancePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTest1Person() {
		return test1PersonEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTest1Person_Name() {
		return (EAttribute)test1PersonEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTest1Person_LastName() {
		return (EAttribute)test1PersonEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTest1BusinessPerson() {
		return test1BusinessPersonEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTest1BusinessPerson_CompanyId() {
		return (EAttribute)test1BusinessPersonEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTest1BusinessPerson_CompanyIdCardNumber() {
		return (EAttribute)test1BusinessPersonEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMeeting() {
		return meetingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMeeting_Id() {
		return (EAttribute)meetingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMeeting_Date() {
		return (EAttribute)meetingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMeeting_ResponsiblePerson() {
		return (EReference)meetingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TestTypeAnnotationInheritanceFactory getTestTypeAnnotationInheritanceFactory() {
		return (TestTypeAnnotationInheritanceFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		test1PersonEClass = createEClass(TEST1_PERSON);
		createEAttribute(test1PersonEClass, TEST1_PERSON__NAME);
		createEAttribute(test1PersonEClass, TEST1_PERSON__LAST_NAME);

		test1BusinessPersonEClass = createEClass(TEST1_BUSINESS_PERSON);
		createEAttribute(test1BusinessPersonEClass, TEST1_BUSINESS_PERSON__COMPANY_ID);
		createEAttribute(test1BusinessPersonEClass, TEST1_BUSINESS_PERSON__COMPANY_ID_CARD_NUMBER);

		meetingEClass = createEClass(MEETING);
		createEAttribute(meetingEClass, MEETING__ID);
		createEAttribute(meetingEClass, MEETING__DATE);
		createEReference(meetingEClass, MEETING__RESPONSIBLE_PERSON);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		test1BusinessPersonEClass.getESuperTypes().add(this.getTest1Person());

		// Initialize classes, features, and operations; add parameters
		initEClass(test1PersonEClass, Test1Person.class, "Test1Person", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTest1Person_Name(), ecorePackage.getEString(), "name", null, 0, 1, Test1Person.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTest1Person_LastName(), ecorePackage.getEString(), "lastName", null, 0, 1, Test1Person.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(test1BusinessPersonEClass, Test1BusinessPerson.class, "Test1BusinessPerson", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTest1BusinessPerson_CompanyId(), ecorePackage.getEString(), "companyId", null, 1, 1, Test1BusinessPerson.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTest1BusinessPerson_CompanyIdCardNumber(), ecorePackage.getEString(), "companyIdCardNumber", null, 0, 1, Test1BusinessPerson.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(meetingEClass, Meeting.class, "Meeting", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMeeting_Id(), ecorePackage.getEString(), "id", null, 1, 1, Meeting.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMeeting_Date(), ecorePackage.getEDate(), "date", null, 0, 1, Meeting.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMeeting_ResponsiblePerson(), this.getTest1Person(), null, "responsiblePerson", null, 0, 1, Meeting.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// Version
		createVersionAnnotations();
		// codec.type
		createCodecAnnotations();
		// codec.id
		createCodec_1Annotations();
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>Version</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createVersionAnnotations() {
		String source = "Version";
		addAnnotation
		  (this,
		   source,
		   new String[] {
			   "value", "1.0"
		   });
	}

	/**
	 * Initializes the annotations for <b>codec.type</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createCodecAnnotations() {
		String source = "codec.type";
		addAnnotation
		  (test1PersonEClass,
		   source,
		   new String[] {
			   "include", "true",
			   "strategy", "NAME",
			   "companyId", "Test1BusinessPerson"
		   });
	}

	/**
	 * Initializes the annotations for <b>codec.id</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createCodec_1Annotations() {
		String source = "codec.id";
		addAnnotation
		  (test1PersonEClass,
		   source,
		   new String[] {
			   "strategy", "COMBINED",
			   "separator", "-",
			   "idFeatures", "name,lastName"
		   });
		addAnnotation
		  (test1BusinessPersonEClass,
		   source,
		   new String[] {
			   "strategy", "ID_FIELD"
		   });
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
		addAnnotation
		  (getTest1BusinessPerson_CompanyIdCardNumber(),
		   source,
		   new String[] {
			   "name", "compId"
		   });
	}

} //TestTypeAnnotationInheritancePackageImpl
