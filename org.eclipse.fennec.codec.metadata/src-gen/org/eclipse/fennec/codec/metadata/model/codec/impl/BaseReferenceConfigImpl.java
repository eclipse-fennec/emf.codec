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
package org.eclipse.fennec.codec.metadata.model.codec.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Base Reference Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseReferenceConfigImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseReferenceConfigImpl#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseReferenceConfigImpl#getRefKey <em>Ref Key</em>}</li>
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
	 * This is true if the Format attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean formatESet;

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
	 * This is true if the Type Key attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean typeKeyESet;

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
	 * This is true if the Ref Key attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean refKeyESet;

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
		return CodecPackage.Literals.BASE_REFERENCE_CONFIG;
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
		boolean oldFormatESet = formatESet;
		formatESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_REFERENCE_CONFIG__FORMAT, oldFormat, format, !oldFormatESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetFormat() {
		SerializationFormat oldFormat = format;
		boolean oldFormatESet = formatESet;
		format = FORMAT_EDEFAULT;
		formatESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.BASE_REFERENCE_CONFIG__FORMAT, oldFormat, FORMAT_EDEFAULT, oldFormatESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetFormat() {
		return formatESet;
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
		boolean oldTypeKeyESet = typeKeyESet;
		typeKeyESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_REFERENCE_CONFIG__TYPE_KEY, oldTypeKey, typeKey, !oldTypeKeyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetTypeKey() {
		String oldTypeKey = typeKey;
		boolean oldTypeKeyESet = typeKeyESet;
		typeKey = TYPE_KEY_EDEFAULT;
		typeKeyESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.BASE_REFERENCE_CONFIG__TYPE_KEY, oldTypeKey, TYPE_KEY_EDEFAULT, oldTypeKeyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetTypeKey() {
		return typeKeyESet;
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
		boolean oldRefKeyESet = refKeyESet;
		refKeyESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_REFERENCE_CONFIG__REF_KEY, oldRefKey, refKey, !oldRefKeyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetRefKey() {
		String oldRefKey = refKey;
		boolean oldRefKeyESet = refKeyESet;
		refKey = REF_KEY_EDEFAULT;
		refKeyESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.BASE_REFERENCE_CONFIG__REF_KEY, oldRefKey, REF_KEY_EDEFAULT, oldRefKeyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetRefKey() {
		return refKeyESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecPackage.BASE_REFERENCE_CONFIG__FORMAT:
				return getFormat();
			case CodecPackage.BASE_REFERENCE_CONFIG__TYPE_KEY:
				return getTypeKey();
			case CodecPackage.BASE_REFERENCE_CONFIG__REF_KEY:
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
			case CodecPackage.BASE_REFERENCE_CONFIG__FORMAT:
				setFormat((SerializationFormat)newValue);
				return;
			case CodecPackage.BASE_REFERENCE_CONFIG__TYPE_KEY:
				setTypeKey((String)newValue);
				return;
			case CodecPackage.BASE_REFERENCE_CONFIG__REF_KEY:
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
			case CodecPackage.BASE_REFERENCE_CONFIG__FORMAT:
				unsetFormat();
				return;
			case CodecPackage.BASE_REFERENCE_CONFIG__TYPE_KEY:
				unsetTypeKey();
				return;
			case CodecPackage.BASE_REFERENCE_CONFIG__REF_KEY:
				unsetRefKey();
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
			case CodecPackage.BASE_REFERENCE_CONFIG__FORMAT:
				return isSetFormat();
			case CodecPackage.BASE_REFERENCE_CONFIG__TYPE_KEY:
				return isSetTypeKey();
			case CodecPackage.BASE_REFERENCE_CONFIG__REF_KEY:
				return isSetRefKey();
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
		if (formatESet) result.append(format); else result.append("<unset>");
		result.append(", typeKey: ");
		if (typeKeyESet) result.append(typeKey); else result.append("<unset>");
		result.append(", refKey: ");
		if (refKeyESet) result.append(refKey); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //BaseReferenceConfigImpl
