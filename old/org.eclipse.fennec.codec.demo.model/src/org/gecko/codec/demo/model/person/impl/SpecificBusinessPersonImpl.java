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
package org.gecko.codec.demo.model.person.impl;

import org.eclipse.emf.ecore.EClass;

import org.gecko.codec.demo.model.person.PersonPackage;
import org.gecko.codec.demo.model.person.SpecificBusinessPerson;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Specific Business Person</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class SpecificBusinessPersonImpl extends BusinessPersonImpl implements SpecificBusinessPerson {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SpecificBusinessPersonImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PersonPackage.Literals.SPECIFIC_BUSINESS_PERSON;
	}

} //SpecificBusinessPersonImpl
