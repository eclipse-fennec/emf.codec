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
package org.eclipse.fennec.codec.info.codecinfo.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Feature Codec Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.FeatureCodecInfoImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.FeatureCodecInfoImpl#getFeature <em>Feature</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.FeatureCodecInfoImpl#getValueReaderName <em>Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.FeatureCodecInfoImpl#getValueWriterName <em>Value Writer Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.FeatureCodecInfoImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.FeatureCodecInfoImpl#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.FeatureCodecInfoImpl#isIgnore <em>Ignore</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.FeatureCodecInfoImpl#getCodecExtraProperties <em>Codec Extra Properties</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FeatureCodecInfoImpl extends TypedCodecInfoImpl implements FeatureCodecInfo {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFeature() <em>Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeature()
	 * @generated
	 * @ordered
	 */
	protected ETypedElement feature;

	/**
	 * The default value of the '{@link #getValueReaderName() <em>Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueReaderName()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_READER_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValueReaderName() <em>Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueReaderName()
	 * @generated
	 * @ordered
	 */
	protected String valueReaderName = VALUE_READER_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getValueWriterName() <em>Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueWriterName()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_WRITER_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValueWriterName() <em>Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueWriterName()
	 * @generated
	 * @ordered
	 */
	protected String valueWriterName = VALUE_WRITER_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final InfoType TYPE_EDEFAULT = InfoType.IDENTITY;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected InfoType type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getKey() <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected static final String KEY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getKey() <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected String key = KEY_EDEFAULT;

	/**
	 * The default value of the '{@link #isIgnore() <em>Ignore</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnore()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IGNORE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIgnore() <em>Ignore</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnore()
	 * @generated
	 * @ordered
	 */
	protected boolean ignore = IGNORE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getCodecExtraProperties() <em>Codec Extra Properties</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCodecExtraProperties()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> codecExtraProperties;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FeatureCodecInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecInfoPackage.Literals.FEATURE_CODEC_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.FEATURE_CODEC_INFO__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ETypedElement getFeature() {
		if (feature != null && feature.eIsProxy()) {
			InternalEObject oldFeature = (InternalEObject)feature;
			feature = (ETypedElement)eResolveProxy(oldFeature);
			if (feature != oldFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodecInfoPackage.FEATURE_CODEC_INFO__FEATURE, oldFeature, feature));
			}
		}
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ETypedElement basicGetFeature() {
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFeature(ETypedElement newFeature) {
		ETypedElement oldFeature = feature;
		feature = newFeature;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.FEATURE_CODEC_INFO__FEATURE, oldFeature, feature));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getValueReaderName() {
		return valueReaderName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValueReaderName(String newValueReaderName) {
		String oldValueReaderName = valueReaderName;
		valueReaderName = newValueReaderName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_READER_NAME, oldValueReaderName, valueReaderName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getValueWriterName() {
		return valueWriterName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValueWriterName(String newValueWriterName) {
		String oldValueWriterName = valueWriterName;
		valueWriterName = newValueWriterName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_WRITER_NAME, oldValueWriterName, valueWriterName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InfoType getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setType(InfoType newType) {
		InfoType oldType = type;
		type = newType == null ? TYPE_EDEFAULT : newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.FEATURE_CODEC_INFO__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getKey() {
		return key;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setKey(String newKey) {
		String oldKey = key;
		key = newKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.FEATURE_CODEC_INFO__KEY, oldKey, key));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIgnore() {
		return ignore;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIgnore(boolean newIgnore) {
		boolean oldIgnore = ignore;
		ignore = newIgnore;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.FEATURE_CODEC_INFO__IGNORE, oldIgnore, ignore));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, String> getCodecExtraProperties() {
		if (codecExtraProperties == null) {
			codecExtraProperties = new EcoreEMap<String,String>(CodecInfoPackage.Literals.STRING_TO_STRING_MAP, StringToStringMapImpl.class, this, CodecInfoPackage.FEATURE_CODEC_INFO__CODEC_EXTRA_PROPERTIES);
		}
		return codecExtraProperties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecInfoPackage.FEATURE_CODEC_INFO__CODEC_EXTRA_PROPERTIES:
				return ((InternalEList<?>)getCodecExtraProperties()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecInfoPackage.FEATURE_CODEC_INFO__ID:
				return getId();
			case CodecInfoPackage.FEATURE_CODEC_INFO__FEATURE:
				if (resolve) return getFeature();
				return basicGetFeature();
			case CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_READER_NAME:
				return getValueReaderName();
			case CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_WRITER_NAME:
				return getValueWriterName();
			case CodecInfoPackage.FEATURE_CODEC_INFO__TYPE:
				return getType();
			case CodecInfoPackage.FEATURE_CODEC_INFO__KEY:
				return getKey();
			case CodecInfoPackage.FEATURE_CODEC_INFO__IGNORE:
				return isIgnore();
			case CodecInfoPackage.FEATURE_CODEC_INFO__CODEC_EXTRA_PROPERTIES:
				if (coreType) return getCodecExtraProperties();
				else return getCodecExtraProperties().map();
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
			case CodecInfoPackage.FEATURE_CODEC_INFO__ID:
				setId((String)newValue);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__FEATURE:
				setFeature((ETypedElement)newValue);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_READER_NAME:
				setValueReaderName((String)newValue);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_WRITER_NAME:
				setValueWriterName((String)newValue);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__TYPE:
				setType((InfoType)newValue);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__KEY:
				setKey((String)newValue);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__IGNORE:
				setIgnore((Boolean)newValue);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__CODEC_EXTRA_PROPERTIES:
				((EStructuralFeature.Setting)getCodecExtraProperties()).set(newValue);
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
			case CodecInfoPackage.FEATURE_CODEC_INFO__ID:
				setId(ID_EDEFAULT);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__FEATURE:
				setFeature((ETypedElement)null);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_READER_NAME:
				setValueReaderName(VALUE_READER_NAME_EDEFAULT);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_WRITER_NAME:
				setValueWriterName(VALUE_WRITER_NAME_EDEFAULT);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__KEY:
				setKey(KEY_EDEFAULT);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__IGNORE:
				setIgnore(IGNORE_EDEFAULT);
				return;
			case CodecInfoPackage.FEATURE_CODEC_INFO__CODEC_EXTRA_PROPERTIES:
				getCodecExtraProperties().clear();
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
			case CodecInfoPackage.FEATURE_CODEC_INFO__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case CodecInfoPackage.FEATURE_CODEC_INFO__FEATURE:
				return feature != null;
			case CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_READER_NAME:
				return VALUE_READER_NAME_EDEFAULT == null ? valueReaderName != null : !VALUE_READER_NAME_EDEFAULT.equals(valueReaderName);
			case CodecInfoPackage.FEATURE_CODEC_INFO__VALUE_WRITER_NAME:
				return VALUE_WRITER_NAME_EDEFAULT == null ? valueWriterName != null : !VALUE_WRITER_NAME_EDEFAULT.equals(valueWriterName);
			case CodecInfoPackage.FEATURE_CODEC_INFO__TYPE:
				return type != TYPE_EDEFAULT;
			case CodecInfoPackage.FEATURE_CODEC_INFO__KEY:
				return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
			case CodecInfoPackage.FEATURE_CODEC_INFO__IGNORE:
				return ignore != IGNORE_EDEFAULT;
			case CodecInfoPackage.FEATURE_CODEC_INFO__CODEC_EXTRA_PROPERTIES:
				return codecExtraProperties != null && !codecExtraProperties.isEmpty();
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
		result.append(" (id: ");
		result.append(id);
		result.append(", valueReaderName: ");
		result.append(valueReaderName);
		result.append(", valueWriterName: ");
		result.append(valueWriterName);
		result.append(", type: ");
		result.append(type);
		result.append(", key: ");
		result.append(key);
		result.append(", ignore: ");
		result.append(ignore);
		result.append(')');
		return result.toString();
	}

} //FeatureCodecInfoImpl
