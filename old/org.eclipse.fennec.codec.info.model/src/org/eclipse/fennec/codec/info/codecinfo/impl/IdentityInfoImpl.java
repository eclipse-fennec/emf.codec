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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage;
import org.eclipse.fennec.codec.info.codecinfo.IdentityInfo;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Identity Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.IdentityInfoImpl#getIdKey <em>Id Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.IdentityInfoImpl#getIdStrategy <em>Id Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.IdentityInfoImpl#getIdSeparator <em>Id Separator</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.IdentityInfoImpl#getIdFeatures <em>Id Features</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.IdentityInfoImpl#getIdValueReaderName <em>Id Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.IdentityInfoImpl#getIdValueWriterName <em>Id Value Writer Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class IdentityInfoImpl extends MinimalEObjectImpl.Container implements IdentityInfo {
	/**
	 * The default value of the '{@link #getIdKey() <em>Id Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdKey()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_KEY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIdKey() <em>Id Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdKey()
	 * @generated
	 * @ordered
	 */
	protected String idKey = ID_KEY_EDEFAULT;

	/**
	 * The default value of the '{@link #getIdStrategy() <em>Id Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdStrategy()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_STRATEGY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIdStrategy() <em>Id Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdStrategy()
	 * @generated
	 * @ordered
	 */
	protected String idStrategy = ID_STRATEGY_EDEFAULT;

	/**
	 * The default value of the '{@link #getIdSeparator() <em>Id Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdSeparator()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_SEPARATOR_EDEFAULT = ".";

	/**
	 * The cached value of the '{@link #getIdSeparator() <em>Id Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdSeparator()
	 * @generated
	 * @ordered
	 */
	protected String idSeparator = ID_SEPARATOR_EDEFAULT;

	/**
	 * The cached value of the '{@link #getIdFeatures() <em>Id Features</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdFeatures()
	 * @generated
	 * @ordered
	 */
	protected EList<EStructuralFeature> idFeatures;

	/**
	 * The default value of the '{@link #getIdValueReaderName() <em>Id Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdValueReaderName()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_VALUE_READER_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIdValueReaderName() <em>Id Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdValueReaderName()
	 * @generated
	 * @ordered
	 */
	protected String idValueReaderName = ID_VALUE_READER_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getIdValueWriterName() <em>Id Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdValueWriterName()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_VALUE_WRITER_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIdValueWriterName() <em>Id Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdValueWriterName()
	 * @generated
	 * @ordered
	 */
	protected String idValueWriterName = ID_VALUE_WRITER_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IdentityInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecInfoPackage.Literals.IDENTITY_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getIdKey() {
		return idKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIdKey(String newIdKey) {
		String oldIdKey = idKey;
		idKey = newIdKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.IDENTITY_INFO__ID_KEY, oldIdKey, idKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getIdStrategy() {
		return idStrategy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIdStrategy(String newIdStrategy) {
		String oldIdStrategy = idStrategy;
		idStrategy = newIdStrategy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.IDENTITY_INFO__ID_STRATEGY, oldIdStrategy, idStrategy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getIdSeparator() {
		return idSeparator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIdSeparator(String newIdSeparator) {
		String oldIdSeparator = idSeparator;
		idSeparator = newIdSeparator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.IDENTITY_INFO__ID_SEPARATOR, oldIdSeparator, idSeparator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EStructuralFeature> getIdFeatures() {
		if (idFeatures == null) {
			idFeatures = new EObjectResolvingEList<EStructuralFeature>(EStructuralFeature.class, this, CodecInfoPackage.IDENTITY_INFO__ID_FEATURES);
		}
		return idFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getIdValueReaderName() {
		return idValueReaderName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIdValueReaderName(String newIdValueReaderName) {
		String oldIdValueReaderName = idValueReaderName;
		idValueReaderName = newIdValueReaderName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.IDENTITY_INFO__ID_VALUE_READER_NAME, oldIdValueReaderName, idValueReaderName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getIdValueWriterName() {
		return idValueWriterName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIdValueWriterName(String newIdValueWriterName) {
		String oldIdValueWriterName = idValueWriterName;
		idValueWriterName = newIdValueWriterName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.IDENTITY_INFO__ID_VALUE_WRITER_NAME, oldIdValueWriterName, idValueWriterName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecInfoPackage.IDENTITY_INFO__ID_KEY:
				return getIdKey();
			case CodecInfoPackage.IDENTITY_INFO__ID_STRATEGY:
				return getIdStrategy();
			case CodecInfoPackage.IDENTITY_INFO__ID_SEPARATOR:
				return getIdSeparator();
			case CodecInfoPackage.IDENTITY_INFO__ID_FEATURES:
				return getIdFeatures();
			case CodecInfoPackage.IDENTITY_INFO__ID_VALUE_READER_NAME:
				return getIdValueReaderName();
			case CodecInfoPackage.IDENTITY_INFO__ID_VALUE_WRITER_NAME:
				return getIdValueWriterName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case CodecInfoPackage.IDENTITY_INFO__ID_KEY:
				setIdKey((String)newValue);
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_STRATEGY:
				setIdStrategy((String)newValue);
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_SEPARATOR:
				setIdSeparator((String)newValue);
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_FEATURES:
				getIdFeatures().clear();
				getIdFeatures().addAll((Collection<? extends EStructuralFeature>)newValue);
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_VALUE_READER_NAME:
				setIdValueReaderName((String)newValue);
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_VALUE_WRITER_NAME:
				setIdValueWriterName((String)newValue);
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
			case CodecInfoPackage.IDENTITY_INFO__ID_KEY:
				setIdKey(ID_KEY_EDEFAULT);
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_STRATEGY:
				setIdStrategy(ID_STRATEGY_EDEFAULT);
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_SEPARATOR:
				setIdSeparator(ID_SEPARATOR_EDEFAULT);
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_FEATURES:
				getIdFeatures().clear();
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_VALUE_READER_NAME:
				setIdValueReaderName(ID_VALUE_READER_NAME_EDEFAULT);
				return;
			case CodecInfoPackage.IDENTITY_INFO__ID_VALUE_WRITER_NAME:
				setIdValueWriterName(ID_VALUE_WRITER_NAME_EDEFAULT);
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
			case CodecInfoPackage.IDENTITY_INFO__ID_KEY:
				return ID_KEY_EDEFAULT == null ? idKey != null : !ID_KEY_EDEFAULT.equals(idKey);
			case CodecInfoPackage.IDENTITY_INFO__ID_STRATEGY:
				return ID_STRATEGY_EDEFAULT == null ? idStrategy != null : !ID_STRATEGY_EDEFAULT.equals(idStrategy);
			case CodecInfoPackage.IDENTITY_INFO__ID_SEPARATOR:
				return ID_SEPARATOR_EDEFAULT == null ? idSeparator != null : !ID_SEPARATOR_EDEFAULT.equals(idSeparator);
			case CodecInfoPackage.IDENTITY_INFO__ID_FEATURES:
				return idFeatures != null && !idFeatures.isEmpty();
			case CodecInfoPackage.IDENTITY_INFO__ID_VALUE_READER_NAME:
				return ID_VALUE_READER_NAME_EDEFAULT == null ? idValueReaderName != null : !ID_VALUE_READER_NAME_EDEFAULT.equals(idValueReaderName);
			case CodecInfoPackage.IDENTITY_INFO__ID_VALUE_WRITER_NAME:
				return ID_VALUE_WRITER_NAME_EDEFAULT == null ? idValueWriterName != null : !ID_VALUE_WRITER_NAME_EDEFAULT.equals(idValueWriterName);
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
		result.append(" (idKey: ");
		result.append(idKey);
		result.append(", idStrategy: ");
		result.append(idStrategy);
		result.append(", idSeparator: ");
		result.append(idSeparator);
		result.append(", idValueReaderName: ");
		result.append(idValueReaderName);
		result.append(", idValueWriterName: ");
		result.append(idValueWriterName);
		result.append(')');
		return result.toString();
	}

} //IdentityInfoImpl
