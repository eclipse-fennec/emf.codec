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
 * A representation of the model object '<em><b>Tx Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.lorawan.uplink.model.lorawan.TxInfo#getFrequency <em>Frequency</em>}</li>
 *   <li>{@link org.eclipse.fennec.lorawan.uplink.model.lorawan.TxInfo#getModulation <em>Modulation</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.lorawan.uplink.model.lorawan.LorawanPackage#getTxInfo()
 * @model
 * @generated
 */
@ProviderType
public interface TxInfo extends EObject {
	/**
	 * Returns the value of the '<em><b>Frequency</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Frequency</em>' attribute.
	 * @see #setFrequency(long)
	 * @see org.eclipse.fennec.lorawan.uplink.model.lorawan.LorawanPackage#getTxInfo_Frequency()
	 * @model
	 * @generated
	 */
	long getFrequency();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.lorawan.uplink.model.lorawan.TxInfo#getFrequency <em>Frequency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Frequency</em>' attribute.
	 * @see #getFrequency()
	 * @generated
	 */
	void setFrequency(long value);

	/**
	 * Returns the value of the '<em><b>Modulation</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Modulation</em>' containment reference.
	 * @see #setModulation(Modulation)
	 * @see org.eclipse.fennec.lorawan.uplink.model.lorawan.LorawanPackage#getTxInfo_Modulation()
	 * @model containment="true"
	 * @generated
	 */
	Modulation getModulation();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.lorawan.uplink.model.lorawan.TxInfo#getModulation <em>Modulation</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Modulation</em>' containment reference.
	 * @see #getModulation()
	 * @generated
	 */
	void setModulation(Modulation value);

} // TxInfo
