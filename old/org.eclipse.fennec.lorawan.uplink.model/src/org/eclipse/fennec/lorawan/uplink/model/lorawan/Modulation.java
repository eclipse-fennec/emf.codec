/*
 *  Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.lorawan.uplink.model.lorawan;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Modulation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.lorawan.uplink.model.lorawan.Modulation#getLora <em>Lora</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.lorawan.uplink.model.lorawan.LorawanPackage#getModulation()
 * @model
 * @generated
 */
@ProviderType
public interface Modulation extends EObject {
	/**
	 * Returns the value of the '<em><b>Lora</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lora</em>' containment reference.
	 * @see #setLora(LoraInfo)
	 * @see org.eclipse.fennec.lorawan.uplink.model.lorawan.LorawanPackage#getModulation_Lora()
	 * @model containment="true"
	 * @generated
	 */
	LoraInfo getLora();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.lorawan.uplink.model.lorawan.Modulation#getLora <em>Lora</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lora</em>' containment reference.
	 * @see #getLora()
	 * @generated
	 */
	void setLora(LoraInfo value);

} // Modulation
