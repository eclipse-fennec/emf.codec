/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 */
package org.eclipse.fennec.model.metadata.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.metadata.BaseReferenceConfig;
import org.eclipse.fennec.model.metadata.MetadataPackage;
import org.eclipse.fennec.model.metadata.SerializationFormat;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Base Reference Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseReferenceConfigImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseReferenceConfigImpl#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseReferenceConfigImpl#getRefKey <em>Ref Key</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class BaseReferenceConfigImpl extends MinimalEObjectImpl.Container implements BaseReferenceConfig {
	/**
	 * The default value of the '{@link #getFormat() <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormat()
	 * @generated
	 * @ordered
	 */
	protected static final SerializationFormat FORMAT_EDEFAULT = SerializationFormat.PLAIN;

	/**
	 * The cached value of the '{@link #getFormat() <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormat()
	 * @generated
	 * @ordered
	 */
	protected SerializationFormat format = FORMAT_EDEFAULT;

	/**
	 * The default value of the '{@link #getTypeKey() <em>Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeKey()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_KEY_EDEFAULT = "_type";

	/**
	 * The cached value of the '{@link #getTypeKey() <em>Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeKey()
	 * @generated
	 * @ordered
	 */
	protected String typeKey = TYPE_KEY_EDEFAULT;

	/**
	 * The default value of the '{@link #getRefKey() <em>Ref Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefKey()
	 * @generated
	 * @ordered
	 */
	protected static final String REF_KEY_EDEFAULT = "_ref";

	/**
	 * The cached value of the '{@link #getRefKey() <em>Ref Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefKey()
	 * @generated
	 * @ordered
	 */
	protected String refKey = REF_KEY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BaseReferenceConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetadataPackage.Literals.BASE_REFERENCE_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SerializationFormat getFormat() {
		return format;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFormat(SerializationFormat newFormat) {
		SerializationFormat oldFormat = format;
		format = newFormat == null ? FORMAT_EDEFAULT : newFormat;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_REFERENCE_CONFIG__FORMAT, oldFormat, format));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTypeKey() {
		return typeKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTypeKey(String newTypeKey) {
		String oldTypeKey = typeKey;
		typeKey = newTypeKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_REFERENCE_CONFIG__TYPE_KEY, oldTypeKey, typeKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRefKey() {
		return refKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRefKey(String newRefKey) {
		String oldRefKey = refKey;
		refKey = newRefKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_REFERENCE_CONFIG__REF_KEY, oldRefKey, refKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MetadataPackage.BASE_REFERENCE_CONFIG__FORMAT:
				return getFormat();
			case MetadataPackage.BASE_REFERENCE_CONFIG__TYPE_KEY:
				return getTypeKey();
			case MetadataPackage.BASE_REFERENCE_CONFIG__REF_KEY:
				return getRefKey();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MetadataPackage.BASE_REFERENCE_CONFIG__FORMAT:
				setFormat((SerializationFormat)newValue);
				return;
			case MetadataPackage.BASE_REFERENCE_CONFIG__TYPE_KEY:
				setTypeKey((String)newValue);
				return;
			case MetadataPackage.BASE_REFERENCE_CONFIG__REF_KEY:
				setRefKey((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MetadataPackage.BASE_REFERENCE_CONFIG__FORMAT:
				setFormat(FORMAT_EDEFAULT);
				return;
			case MetadataPackage.BASE_REFERENCE_CONFIG__TYPE_KEY:
				setTypeKey(TYPE_KEY_EDEFAULT);
				return;
			case MetadataPackage.BASE_REFERENCE_CONFIG__REF_KEY:
				setRefKey(REF_KEY_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MetadataPackage.BASE_REFERENCE_CONFIG__FORMAT:
				return format != FORMAT_EDEFAULT;
			case MetadataPackage.BASE_REFERENCE_CONFIG__TYPE_KEY:
				return TYPE_KEY_EDEFAULT == null ? typeKey != null : !TYPE_KEY_EDEFAULT.equals(typeKey);
			case MetadataPackage.BASE_REFERENCE_CONFIG__REF_KEY:
				return REF_KEY_EDEFAULT == null ? refKey != null : !REF_KEY_EDEFAULT.equals(refKey);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (format: ");
		result.append(format);
		result.append(", typeKey: ");
		result.append(typeKey);
		result.append(", refKey: ");
		result.append(refKey);
		result.append(')');
		return result.toString();
	}

} //BaseReferenceConfigImpl
