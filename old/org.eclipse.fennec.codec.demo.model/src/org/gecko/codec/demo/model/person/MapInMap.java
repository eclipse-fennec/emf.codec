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

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Map In Map</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.codec.demo.model.person.MapInMap#getStringMapInMapValues <em>String Map In Map Values</em>}</li>
 * </ul>
 *
 * @see org.gecko.codec.demo.model.person.PersonPackage#getMapInMap()
 * @model
 * @generated
 */
@ProviderType
public interface MapInMap extends EObject {
	/**
	 * Returns the value of the '<em><b>String Map In Map Values</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type list of {@link java.util.Map.Entry<java.lang.String, org.gecko.codec.demo.model.person.SimpleValue>},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>String Map In Map Values</em>' map.
	 * @see org.gecko.codec.demo.model.person.PersonPackage#getMapInMap_StringMapInMapValues()
	 * @model mapType="org.gecko.codec.demo.model.person.StringToStringMapInMap&lt;org.eclipse.emf.ecore.EString, org.gecko.codec.demo.model.person.StringToSimpleValueMap&gt;"
	 * @generated
	 */
	EMap<String, EMap<String, SimpleValue>> getStringMapInMapValues();

} // MapInMap
