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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Object</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.codec.demo.model.person.TestObject#getRef1 <em>Ref1</em>}</li>
 *   <li>{@link org.gecko.codec.demo.model.person.TestObject#getRef2 <em>Ref2</em>}</li>
 * </ul>
 *
 * @see org.gecko.codec.demo.model.person.PersonPackage#getTestObject()
 * @model
 * @generated
 */
@ProviderType
public interface TestObject extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref1</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref1</em>' containment reference.
	 * @see #setRef1(Parent)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getTestObject_Ref1()
	 * @model containment="true"
	 * @generated
	 */
	Parent getRef1();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.TestObject#getRef1 <em>Ref1</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref1</em>' containment reference.
	 * @see #getRef1()
	 * @generated
	 */
	void setRef1(Parent value);

	/**
	 * Returns the value of the '<em><b>Ref2</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref2</em>' containment reference.
	 * @see #setRef2(Parent2)
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getTestObject_Ref2()
	 * @model containment="true"
	 * @generated
	 */
	Parent2 getRef2();

	/**
	 * Sets the value of the '{@link org.gecko.codec.demo.model.person.TestObject#getRef2 <em>Ref2</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref2</em>' containment reference.
	 * @see #getRef2()
	 * @generated
	 */
	void setRef2(Parent2 value);

} // TestObject
