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
package org.eclipse.fennec.em310udl.mesage.model.em310udl;

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.em310udl.mesage.model.em310udl.EM310UDLPackage
 * @generated
 */
@ProviderType
public interface EM310UDLFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EM310UDLFactory eINSTANCE = org.eclipse.fennec.em310udl.mesage.model.em310udl.impl.EM310UDLFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Decoded Object</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Decoded Object</em>'.
	 * @generated
	 */
	DecodedObject createDecodedObject();

	/**
	 * Returns a new object of class '<em>Uplink</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Uplink</em>'.
	 * @generated
	 */
	EM310UDLUplink createEM310UDLUplink();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	EM310UDLPackage getEM310UDLPackage();

} //EM310UDLFactory
