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
package org.gecko.codec.demo.model.person;

import java.util.Date;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Person</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getName <em>Name</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getLastName <em>Last Name</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getAddress <em>Address</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getAddresses <em>Addresses</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getBirthDate <em>Birth Date</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getAge <em>Age</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#isMarried <em>Married</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getGender <em>Gender</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getNonContainedAdd <em>Non Contained Add</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getNonContainedAdds <em>Non Contained Adds</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getId <em>Id</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getTitles <em>Titles</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getTransientAtt <em>Transient Att</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getBusinessAdd <em>Business Add</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getHeight <em>Height</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getWeight <em>Weight</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.Person#getCustomDataType <em>Custom Data Type</em>}</li>
 * </ul>
 *
 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson()
 * @model annotation="codec.type include='true' strategy='NAME'"
 *        annotation="codec.id strategy='COMBINED' separator='-' idFeatures='name,lastName'"
 * @generated
 */
@ProviderType
public interface Person extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Last Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Last Name</em>' attribute.
	 * @see #setLastName(String)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_LastName()
	 * @model
	 * @generated
	 */
	String getLastName();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getLastName <em>Last Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Last Name</em>' attribute.
	 * @see #getLastName()
	 * @generated
	 */
	void setLastName(String value);

	/**
	 * Returns the value of the '<em><b>Address</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Address</em>' containment reference.
	 * @see #setAddress(Address)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Address()
	 * @model containment="true"
	 * @generated
	 */
	Address getAddress();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getAddress <em>Address</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Address</em>' containment reference.
	 * @see #getAddress()
	 * @generated
	 */
	void setAddress(Address value);

	/**
	 * Returns the value of the '<em><b>Addresses</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.codec.demo.model.person.Address}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Addresses</em>' containment reference list.
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Addresses()
	 * @model containment="true"
	 * @generated
	 */
	EList<Address> getAddresses();

	/**
	 * Returns the value of the '<em><b>Birth Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Birth Date</em>' attribute.
	 * @see #setBirthDate(Date)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_BirthDate()
	 * @model
	 * @generated
	 */
	Date getBirthDate();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getBirthDate <em>Birth Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Birth Date</em>' attribute.
	 * @see #getBirthDate()
	 * @generated
	 */
	void setBirthDate(Date value);

	/**
	 * Returns the value of the '<em><b>Age</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Age</em>' attribute.
	 * @see #setAge(int)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Age()
	 * @model
	 * @generated
	 */
	int getAge();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getAge <em>Age</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Age</em>' attribute.
	 * @see #getAge()
	 * @generated
	 */
	void setAge(int value);

	/**
	 * Returns the value of the '<em><b>Married</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Married</em>' attribute.
	 * @see #setMarried(boolean)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Married()
	 * @model
	 * @generated
	 */
	boolean isMarried();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#isMarried <em>Married</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Married</em>' attribute.
	 * @see #isMarried()
	 * @generated
	 */
	void setMarried(boolean value);

	/**
	 * Returns the value of the '<em><b>Gender</b></em>' attribute.
	 * The literals are from the enumeration {@link org.gecko.codec.demo.model.person.GENDER_TYPE}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gender</em>' attribute.
	 * @see org.gecko.codec.demo.model.person.GENDER_TYPE
	 * @see #setGender(GENDER_TYPE)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Gender()
	 * @model
	 * @generated
	 */
	GENDER_TYPE getGender();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getGender <em>Gender</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gender</em>' attribute.
	 * @see org.gecko.codec.demo.model.person.GENDER_TYPE
	 * @see #getGender()
	 * @generated
	 */
	void setGender(GENDER_TYPE value);

	/**
	 * Returns the value of the '<em><b>Non Contained Add</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Non Contained Add</em>' reference.
	 * @see #setNonContainedAdd(Address)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_NonContainedAdd()
	 * @model
	 * @generated
	 */
	Address getNonContainedAdd();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getNonContainedAdd <em>Non Contained Add</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Non Contained Add</em>' reference.
	 * @see #getNonContainedAdd()
	 * @generated
	 */
	void setNonContainedAdd(Address value);

	/**
	 * Returns the value of the '<em><b>Non Contained Adds</b></em>' reference list.
	 * The list contents are of type {@link org.gecko.codec.demo.model.person.Address}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Non Contained Adds</em>' reference list.
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_NonContainedAdds()
	 * @model
	 * @generated
	 */
	EList<Address> getNonContainedAdds();

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Id()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Titles</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Titles</em>' attribute list.
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Titles()
	 * @model extendedMetaData="name='title'"
	 * @generated
	 */
	EList<String> getTitles();

	/**
	 * Returns the value of the '<em><b>Transient Att</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transient Att</em>' attribute.
	 * @see #setTransientAtt(int)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_TransientAtt()
	 * @model transient="true"
	 * @generated
	 */
	int getTransientAtt();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getTransientAtt <em>Transient Att</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transient Att</em>' attribute.
	 * @see #getTransientAtt()
	 * @generated
	 */
	void setTransientAtt(int value);

	/**
	 * Returns the value of the '<em><b>Business Add</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Business Add</em>' containment reference.
	 * @see #setBusinessAdd(BusinessAddress)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_BusinessAdd()
	 * @model containment="true"
	 * @generated
	 */
	BusinessAddress getBusinessAdd();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getBusinessAdd <em>Business Add</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Business Add</em>' containment reference.
	 * @see #getBusinessAdd()
	 * @generated
	 */
	void setBusinessAdd(BusinessAddress value);

	/**
	 * Returns the value of the '<em><b>Height</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Height</em>' attribute.
	 * @see #setHeight(Double)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Height()
	 * @model
	 * @generated
	 */
	Double getHeight();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getHeight <em>Height</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Height</em>' attribute.
	 * @see #getHeight()
	 * @generated
	 */
	void setHeight(Double value);

	/**
	 * Returns the value of the '<em><b>Weight</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Weight</em>' attribute.
	 * @see #setWeight(Float)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_Weight()
	 * @model
	 * @generated
	 */
	Float getWeight();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getWeight <em>Weight</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Weight</em>' attribute.
	 * @see #getWeight()
	 * @generated
	 */
	void setWeight(Float value);

	/**
	 * Returns the value of the '<em><b>Custom Data Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Custom Data Type</em>' attribute.
	 * @see #setCustomDataType(String)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getPerson_CustomDataType()
	 * @model dataType="org.gecko.codec.demo.model.person.CustomDataType"
	 * @generated
	 */
	String getCustomDataType();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.Person#getCustomDataType <em>Custom Data Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Custom Data Type</em>' attribute.
	 * @see #getCustomDataType()
	 * @generated
	 */
	void setCustomDataType(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.name + \" \" + this.lastName;'"
	 * @generated
	 */
	String getFullName();

} // Person
