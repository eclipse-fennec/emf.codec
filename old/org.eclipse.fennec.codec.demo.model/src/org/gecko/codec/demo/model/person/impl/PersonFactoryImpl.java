/**
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
package org.gecko.codec.demo.model.person.impl;

import java.util.Map;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.gecko.codec.demo.model.person.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PersonFactoryImpl extends EFactoryImpl implements PersonFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static PersonFactory init() {
		try {
			PersonFactory thePersonFactory = (PersonFactory)EPackage.Registry.INSTANCE.getEFactory(PersonPackage.eNS_URI);
			if (thePersonFactory != null) {
				return thePersonFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new PersonFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PersonFactoryImpl() {
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
			case PersonPackage.PERSON: return createPerson();
			case PersonPackage.ADDRESS: return createAddress();
			case PersonPackage.BUSINESS_PERSON: return createBusinessPerson();
			case PersonPackage.CONTACT: return createContact();
			case PersonPackage.BUSINESS_ADDRESS: return createBusinessAddress();
			case PersonPackage.SPECIFIC_BUSINESS_PERSON: return createSpecificBusinessPerson();
			case PersonPackage.MAP_IN_MAP: return createMapInMap();
			case PersonPackage.STRING_TO_STRING_MAP_IN_MAP: return (EObject)createStringToStringMapInMap();
			case PersonPackage.STRING_TO_SIMPLE_VALUE_MAP: return (EObject)createStringToSimpleValueMap();
			case PersonPackage.SIMPLE_VALUE: return createSimpleValue();
			case PersonPackage.SIMPLE_MAP: return createSimpleMap();
			case PersonPackage.TYPE_KEY_ECLASS: return createTypeKeyEClass();
			case PersonPackage.SENSOR_BOOK: return createSensorBook();
			case PersonPackage.SENSOR: return createSensor();
			case PersonPackage.PARENT: return createParent();
			case PersonPackage.PARENT2: return createParent2();
			case PersonPackage.CHILD: return createChild();
			case PersonPackage.CHILD2: return createChild2();
			case PersonPackage.TEST_OBJECT: return createTestObject();
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
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case PersonPackage.GENDER_TYPE:
				return createGENDER_TYPEFromString(eDataType, initialValue);
			case PersonPackage.CUSTOM_DATA_TYPE:
				return createCustomDataTypeFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case PersonPackage.GENDER_TYPE:
				return convertGENDER_TYPEToString(eDataType, instanceValue);
			case PersonPackage.CUSTOM_DATA_TYPE:
				return convertCustomDataTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Person createPerson() {
		PersonImpl person = new PersonImpl();
		return person;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Address createAddress() {
		AddressImpl address = new AddressImpl();
		return address;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BusinessPerson createBusinessPerson() {
		BusinessPersonImpl businessPerson = new BusinessPersonImpl();
		return businessPerson;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Contact createContact() {
		ContactImpl contact = new ContactImpl();
		return contact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BusinessAddress createBusinessAddress() {
		BusinessAddressImpl businessAddress = new BusinessAddressImpl();
		return businessAddress;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SpecificBusinessPerson createSpecificBusinessPerson() {
		SpecificBusinessPersonImpl specificBusinessPerson = new SpecificBusinessPersonImpl();
		return specificBusinessPerson;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MapInMap createMapInMap() {
		MapInMapImpl mapInMap = new MapInMapImpl();
		return mapInMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, EMap<String, SimpleValue>> createStringToStringMapInMap() {
		StringToStringMapInMapImpl stringToStringMapInMap = new StringToStringMapInMapImpl();
		return stringToStringMapInMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, SimpleValue> createStringToSimpleValueMap() {
		StringToSimpleValueMapImpl stringToSimpleValueMap = new StringToSimpleValueMapImpl();
		return stringToSimpleValueMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SimpleValue createSimpleValue() {
		SimpleValueImpl simpleValue = new SimpleValueImpl();
		return simpleValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SimpleMap createSimpleMap() {
		SimpleMapImpl simpleMap = new SimpleMapImpl();
		return simpleMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TypeKeyEClass createTypeKeyEClass() {
		TypeKeyEClassImpl typeKeyEClass = new TypeKeyEClassImpl();
		return typeKeyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SensorBook createSensorBook() {
		SensorBookImpl sensorBook = new SensorBookImpl();
		return sensorBook;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Sensor createSensor() {
		SensorImpl sensor = new SensorImpl();
		return sensor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parent createParent() {
		ParentImpl parent = new ParentImpl();
		return parent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parent2 createParent2() {
		Parent2Impl parent2 = new Parent2Impl();
		return parent2;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Child createChild() {
		ChildImpl child = new ChildImpl();
		return child;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Child2 createChild2() {
		Child2Impl child2 = new Child2Impl();
		return child2;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TestObject createTestObject() {
		TestObjectImpl testObject = new TestObjectImpl();
		return testObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GENDER_TYPE createGENDER_TYPEFromString(EDataType eDataType, String initialValue) {
		GENDER_TYPE result = GENDER_TYPE.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertGENDER_TYPEToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createCustomDataTypeFromString(EDataType eDataType, String initialValue) {
		return (String)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertCustomDataTypeToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PersonPackage getPersonPackage() {
		return (PersonPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static PersonPackage getPackage() {
		return PersonPackage.eINSTANCE;
	}

} //PersonFactoryImpl
