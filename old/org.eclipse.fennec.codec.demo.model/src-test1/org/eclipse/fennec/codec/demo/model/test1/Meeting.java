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

import java.util.Date;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Meeting</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.Meeting#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.Meeting#getDate <em>Date</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.demo.model.test1.Meeting#getResponsiblePerson <em>Responsible Person</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage#getMeeting()
 * @model
 * @generated
 */
@ProviderType
public interface Meeting extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage#getMeeting_Id()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.demo.model.test1.Meeting#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Date</em>' attribute.
	 * @see #setDate(Date)
	 * @see org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage#getMeeting_Date()
	 * @model
	 * @generated
	 */
	Date getDate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.demo.model.test1.Meeting#getDate <em>Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Date</em>' attribute.
	 * @see #getDate()
	 * @generated
	 */
	void setDate(Date value);

	/**
	 * Returns the value of the '<em><b>Responsible Person</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Responsible Person</em>' containment reference.
	 * @see #setResponsiblePerson(Test1Person)
	 * @see org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage#getMeeting_ResponsiblePerson()
	 * @model containment="true"
	 * @generated
	 */
	Test1Person getResponsiblePerson();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.demo.model.test1.Meeting#getResponsiblePerson <em>Responsible Person</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Responsible Person</em>' containment reference.
	 * @see #getResponsiblePerson()
	 * @generated
	 */
	void setResponsiblePerson(Test1Person value);

} // Meeting
