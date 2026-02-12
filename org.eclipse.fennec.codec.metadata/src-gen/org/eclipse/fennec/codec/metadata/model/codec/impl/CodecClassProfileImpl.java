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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;

import org.eclipse.fennec.model.metadata.impl.ClassProfileImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class Profile</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl#getTypeConfig <em>Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl#getIdConfig <em>Id Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl#getSuperTypeConfig <em>Super Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecClassProfileImpl#getFeatureConfigs <em>Feature Configs</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CodecClassProfileImpl extends ClassProfileImpl implements CodecClassProfile {
	/**
	 * The cached value of the '{@link #getTypeConfig() <em>Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeConfig()
	 * @generated
	 * @ordered
	 */
	protected TypeSerializationConfig typeConfig;

	/**
	 * The cached value of the '{@link #getIdConfig() <em>Id Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdConfig()
	 * @generated
	 * @ordered
	 */
	protected IdSerializationConfig idConfig;

	/**
	 * The cached value of the '{@link #getSuperTypeConfig() <em>Super Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperTypeConfig()
	 * @generated
	 * @ordered
	 */
	protected SuperTypeSerializationConfig superTypeConfig;

	/**
	 * The cached value of the '{@link #getFeatureConfigs() <em>Feature Configs</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatureConfigs()
	 * @generated
	 * @ordered
	 */
	protected EList<FeatureSerializationConfig> featureConfigs;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CodecClassProfileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.CODEC_CLASS_PROFILE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TypeSerializationConfig getTypeConfig() {
		return typeConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTypeConfig(TypeSerializationConfig newTypeConfig, NotificationChain msgs) {
		TypeSerializationConfig oldTypeConfig = typeConfig;
		typeConfig = newTypeConfig;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CLASS_PROFILE__TYPE_CONFIG, oldTypeConfig, newTypeConfig);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTypeConfig(TypeSerializationConfig newTypeConfig) {
		if (newTypeConfig != typeConfig) {
			NotificationChain msgs = null;
			if (typeConfig != null)
				msgs = ((InternalEObject)typeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CLASS_PROFILE__TYPE_CONFIG, null, msgs);
			if (newTypeConfig != null)
				msgs = ((InternalEObject)newTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CLASS_PROFILE__TYPE_CONFIG, null, msgs);
			msgs = basicSetTypeConfig(newTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CLASS_PROFILE__TYPE_CONFIG, newTypeConfig, newTypeConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IdSerializationConfig getIdConfig() {
		return idConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIdConfig(IdSerializationConfig newIdConfig, NotificationChain msgs) {
		IdSerializationConfig oldIdConfig = idConfig;
		idConfig = newIdConfig;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CLASS_PROFILE__ID_CONFIG, oldIdConfig, newIdConfig);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIdConfig(IdSerializationConfig newIdConfig) {
		if (newIdConfig != idConfig) {
			NotificationChain msgs = null;
			if (idConfig != null)
				msgs = ((InternalEObject)idConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CLASS_PROFILE__ID_CONFIG, null, msgs);
			if (newIdConfig != null)
				msgs = ((InternalEObject)newIdConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CLASS_PROFILE__ID_CONFIG, null, msgs);
			msgs = basicSetIdConfig(newIdConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CLASS_PROFILE__ID_CONFIG, newIdConfig, newIdConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SuperTypeSerializationConfig getSuperTypeConfig() {
		return superTypeConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSuperTypeConfig(SuperTypeSerializationConfig newSuperTypeConfig, NotificationChain msgs) {
		SuperTypeSerializationConfig oldSuperTypeConfig = superTypeConfig;
		superTypeConfig = newSuperTypeConfig;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG, oldSuperTypeConfig, newSuperTypeConfig);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSuperTypeConfig(SuperTypeSerializationConfig newSuperTypeConfig) {
		if (newSuperTypeConfig != superTypeConfig) {
			NotificationChain msgs = null;
			if (superTypeConfig != null)
				msgs = ((InternalEObject)superTypeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG, null, msgs);
			if (newSuperTypeConfig != null)
				msgs = ((InternalEObject)newSuperTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG, null, msgs);
			msgs = basicSetSuperTypeConfig(newSuperTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG, newSuperTypeConfig, newSuperTypeConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<FeatureSerializationConfig> getFeatureConfigs() {
		if (featureConfigs == null) {
			featureConfigs = new EObjectContainmentEList<FeatureSerializationConfig>(FeatureSerializationConfig.class, this, CodecPackage.CODEC_CLASS_PROFILE__FEATURE_CONFIGS);
		}
		return featureConfigs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecPackage.CODEC_CLASS_PROFILE__TYPE_CONFIG:
				return basicSetTypeConfig(null, msgs);
			case CodecPackage.CODEC_CLASS_PROFILE__ID_CONFIG:
				return basicSetIdConfig(null, msgs);
			case CodecPackage.CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG:
				return basicSetSuperTypeConfig(null, msgs);
			case CodecPackage.CODEC_CLASS_PROFILE__FEATURE_CONFIGS:
				return ((InternalEList<?>)getFeatureConfigs()).basicRemove(otherEnd, msgs);
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
			case CodecPackage.CODEC_CLASS_PROFILE__TYPE_CONFIG:
				return getTypeConfig();
			case CodecPackage.CODEC_CLASS_PROFILE__ID_CONFIG:
				return getIdConfig();
			case CodecPackage.CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG:
				return getSuperTypeConfig();
			case CodecPackage.CODEC_CLASS_PROFILE__FEATURE_CONFIGS:
				return getFeatureConfigs();
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
			case CodecPackage.CODEC_CLASS_PROFILE__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)newValue);
				return;
			case CodecPackage.CODEC_CLASS_PROFILE__ID_CONFIG:
				setIdConfig((IdSerializationConfig)newValue);
				return;
			case CodecPackage.CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG:
				setSuperTypeConfig((SuperTypeSerializationConfig)newValue);
				return;
			case CodecPackage.CODEC_CLASS_PROFILE__FEATURE_CONFIGS:
				getFeatureConfigs().clear();
				getFeatureConfigs().addAll((Collection<? extends FeatureSerializationConfig>)newValue);
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
			case CodecPackage.CODEC_CLASS_PROFILE__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)null);
				return;
			case CodecPackage.CODEC_CLASS_PROFILE__ID_CONFIG:
				setIdConfig((IdSerializationConfig)null);
				return;
			case CodecPackage.CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG:
				setSuperTypeConfig((SuperTypeSerializationConfig)null);
				return;
			case CodecPackage.CODEC_CLASS_PROFILE__FEATURE_CONFIGS:
				getFeatureConfigs().clear();
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
			case CodecPackage.CODEC_CLASS_PROFILE__TYPE_CONFIG:
				return typeConfig != null;
			case CodecPackage.CODEC_CLASS_PROFILE__ID_CONFIG:
				return idConfig != null;
			case CodecPackage.CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG:
				return superTypeConfig != null;
			case CodecPackage.CODEC_CLASS_PROFILE__FEATURE_CONFIGS:
				return featureConfigs != null && !featureConfigs.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //CodecClassProfileImpl
