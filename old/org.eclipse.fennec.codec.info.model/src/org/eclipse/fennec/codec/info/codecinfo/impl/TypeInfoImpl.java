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
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.info.codecinfo.TypeMapStrategyType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Type Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.TypeInfoImpl#getTypeStrategy <em>Type Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.TypeInfoImpl#isIgnoreType <em>Ignore Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.TypeInfoImpl#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.TypeInfoImpl#getTypeMap <em>Type Map</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.TypeInfoImpl#getTypeValueReaderName <em>Type Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.TypeInfoImpl#getTypeValueWriterName <em>Type Value Writer Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.TypeInfoImpl#getTypeMapStrategy <em>Type Map Strategy</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TypeInfoImpl extends MinimalEObjectImpl.Container implements TypeInfo {
	/**
	 * The default value of the '{@link #getTypeStrategy() <em>Type Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeStrategy()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_STRATEGY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTypeStrategy() <em>Type Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeStrategy()
	 * @generated
	 * @ordered
	 */
	protected String typeStrategy = TYPE_STRATEGY_EDEFAULT;

	/**
	 * The default value of the '{@link #isIgnoreType() <em>Ignore Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnoreType()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IGNORE_TYPE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIgnoreType() <em>Ignore Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnoreType()
	 * @generated
	 * @ordered
	 */
	protected boolean ignoreType = IGNORE_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getTypeKey() <em>Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeKey()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_KEY_EDEFAULT = null;

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
	 * The cached value of the '{@link #getTypeMap() <em>Type Map</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeMap()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> typeMap;

	/**
	 * The default value of the '{@link #getTypeValueReaderName() <em>Type Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeValueReaderName()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_VALUE_READER_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTypeValueReaderName() <em>Type Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeValueReaderName()
	 * @generated
	 * @ordered
	 */
	protected String typeValueReaderName = TYPE_VALUE_READER_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getTypeValueWriterName() <em>Type Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeValueWriterName()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_VALUE_WRITER_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTypeValueWriterName() <em>Type Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeValueWriterName()
	 * @generated
	 * @ordered
	 */
	protected String typeValueWriterName = TYPE_VALUE_WRITER_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getTypeMapStrategy() <em>Type Map Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeMapStrategy()
	 * @generated
	 * @ordered
	 */
	protected static final TypeMapStrategyType TYPE_MAP_STRATEGY_EDEFAULT = TypeMapStrategyType.OVERWRITE;

	/**
	 * The cached value of the '{@link #getTypeMapStrategy() <em>Type Map Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeMapStrategy()
	 * @generated
	 * @ordered
	 */
	protected TypeMapStrategyType typeMapStrategy = TYPE_MAP_STRATEGY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TypeInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecInfoPackage.Literals.TYPE_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTypeStrategy() {
		return typeStrategy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTypeStrategy(String newTypeStrategy) {
		String oldTypeStrategy = typeStrategy;
		typeStrategy = newTypeStrategy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.TYPE_INFO__TYPE_STRATEGY, oldTypeStrategy, typeStrategy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIgnoreType() {
		return ignoreType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIgnoreType(boolean newIgnoreType) {
		boolean oldIgnoreType = ignoreType;
		ignoreType = newIgnoreType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.TYPE_INFO__IGNORE_TYPE, oldIgnoreType, ignoreType));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.TYPE_INFO__TYPE_KEY, oldTypeKey, typeKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, String> getTypeMap() {
		if (typeMap == null) {
			typeMap = new EcoreEMap<String,String>(CodecInfoPackage.Literals.STRING_TO_STRING_MAP, StringToStringMapImpl.class, this, CodecInfoPackage.TYPE_INFO__TYPE_MAP);
		}
		return typeMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTypeValueReaderName() {
		return typeValueReaderName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTypeValueReaderName(String newTypeValueReaderName) {
		String oldTypeValueReaderName = typeValueReaderName;
		typeValueReaderName = newTypeValueReaderName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.TYPE_INFO__TYPE_VALUE_READER_NAME, oldTypeValueReaderName, typeValueReaderName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTypeValueWriterName() {
		return typeValueWriterName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTypeValueWriterName(String newTypeValueWriterName) {
		String oldTypeValueWriterName = typeValueWriterName;
		typeValueWriterName = newTypeValueWriterName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.TYPE_INFO__TYPE_VALUE_WRITER_NAME, oldTypeValueWriterName, typeValueWriterName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TypeMapStrategyType getTypeMapStrategy() {
		return typeMapStrategy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTypeMapStrategy(TypeMapStrategyType newTypeMapStrategy) {
		TypeMapStrategyType oldTypeMapStrategy = typeMapStrategy;
		typeMapStrategy = newTypeMapStrategy == null ? TYPE_MAP_STRATEGY_EDEFAULT : newTypeMapStrategy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.TYPE_INFO__TYPE_MAP_STRATEGY, oldTypeMapStrategy, typeMapStrategy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecInfoPackage.TYPE_INFO__TYPE_MAP:
				return ((InternalEList<?>)getTypeMap()).basicRemove(otherEnd, msgs);
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
			case CodecInfoPackage.TYPE_INFO__TYPE_STRATEGY:
				return getTypeStrategy();
			case CodecInfoPackage.TYPE_INFO__IGNORE_TYPE:
				return isIgnoreType();
			case CodecInfoPackage.TYPE_INFO__TYPE_KEY:
				return getTypeKey();
			case CodecInfoPackage.TYPE_INFO__TYPE_MAP:
				if (coreType) return getTypeMap();
				else return getTypeMap().map();
			case CodecInfoPackage.TYPE_INFO__TYPE_VALUE_READER_NAME:
				return getTypeValueReaderName();
			case CodecInfoPackage.TYPE_INFO__TYPE_VALUE_WRITER_NAME:
				return getTypeValueWriterName();
			case CodecInfoPackage.TYPE_INFO__TYPE_MAP_STRATEGY:
				return getTypeMapStrategy();
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
			case CodecInfoPackage.TYPE_INFO__TYPE_STRATEGY:
				setTypeStrategy((String)newValue);
				return;
			case CodecInfoPackage.TYPE_INFO__IGNORE_TYPE:
				setIgnoreType((Boolean)newValue);
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_KEY:
				setTypeKey((String)newValue);
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_MAP:
				((EStructuralFeature.Setting)getTypeMap()).set(newValue);
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_VALUE_READER_NAME:
				setTypeValueReaderName((String)newValue);
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_VALUE_WRITER_NAME:
				setTypeValueWriterName((String)newValue);
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_MAP_STRATEGY:
				setTypeMapStrategy((TypeMapStrategyType)newValue);
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
			case CodecInfoPackage.TYPE_INFO__TYPE_STRATEGY:
				setTypeStrategy(TYPE_STRATEGY_EDEFAULT);
				return;
			case CodecInfoPackage.TYPE_INFO__IGNORE_TYPE:
				setIgnoreType(IGNORE_TYPE_EDEFAULT);
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_KEY:
				setTypeKey(TYPE_KEY_EDEFAULT);
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_MAP:
				getTypeMap().clear();
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_VALUE_READER_NAME:
				setTypeValueReaderName(TYPE_VALUE_READER_NAME_EDEFAULT);
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_VALUE_WRITER_NAME:
				setTypeValueWriterName(TYPE_VALUE_WRITER_NAME_EDEFAULT);
				return;
			case CodecInfoPackage.TYPE_INFO__TYPE_MAP_STRATEGY:
				setTypeMapStrategy(TYPE_MAP_STRATEGY_EDEFAULT);
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
			case CodecInfoPackage.TYPE_INFO__TYPE_STRATEGY:
				return TYPE_STRATEGY_EDEFAULT == null ? typeStrategy != null : !TYPE_STRATEGY_EDEFAULT.equals(typeStrategy);
			case CodecInfoPackage.TYPE_INFO__IGNORE_TYPE:
				return ignoreType != IGNORE_TYPE_EDEFAULT;
			case CodecInfoPackage.TYPE_INFO__TYPE_KEY:
				return TYPE_KEY_EDEFAULT == null ? typeKey != null : !TYPE_KEY_EDEFAULT.equals(typeKey);
			case CodecInfoPackage.TYPE_INFO__TYPE_MAP:
				return typeMap != null && !typeMap.isEmpty();
			case CodecInfoPackage.TYPE_INFO__TYPE_VALUE_READER_NAME:
				return TYPE_VALUE_READER_NAME_EDEFAULT == null ? typeValueReaderName != null : !TYPE_VALUE_READER_NAME_EDEFAULT.equals(typeValueReaderName);
			case CodecInfoPackage.TYPE_INFO__TYPE_VALUE_WRITER_NAME:
				return TYPE_VALUE_WRITER_NAME_EDEFAULT == null ? typeValueWriterName != null : !TYPE_VALUE_WRITER_NAME_EDEFAULT.equals(typeValueWriterName);
			case CodecInfoPackage.TYPE_INFO__TYPE_MAP_STRATEGY:
				return typeMapStrategy != TYPE_MAP_STRATEGY_EDEFAULT;
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
		result.append(" (typeStrategy: ");
		result.append(typeStrategy);
		result.append(", ignoreType: ");
		result.append(ignoreType);
		result.append(", typeKey: ");
		result.append(typeKey);
		result.append(", typeValueReaderName: ");
		result.append(typeValueReaderName);
		result.append(", typeValueWriterName: ");
		result.append(typeValueWriterName);
		result.append(", typeMapStrategy: ");
		result.append(typeMapStrategy);
		result.append(')');
		return result.toString();
	}

} //TypeInfoImpl
