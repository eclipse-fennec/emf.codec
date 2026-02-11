/*
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.metadata.model.codec.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Reference Codec Aspect</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl#getReferenceConfig <em>Reference Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl#getTypeConfig <em>Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl#isInheritTypeFromTarget <em>Inherit Type From Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ReferenceCodecAspectImpl#isExpand <em>Expand</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReferenceCodecAspectImpl extends FeatureCodecAspectImpl implements ReferenceCodecAspect {
	/**
	 * The cached value of the '{@link #getReferenceConfig() <em>Reference Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferenceConfig()
	 * @generated
	 * @ordered
	 */
	protected ReferenceSerializationConfig referenceConfig;

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
	 * The default value of the '{@link #isInheritTypeFromTarget() <em>Inherit Type From Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isInheritTypeFromTarget()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INHERIT_TYPE_FROM_TARGET_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isInheritTypeFromTarget() <em>Inherit Type From Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isInheritTypeFromTarget()
	 * @generated
	 * @ordered
	 */
	protected boolean inheritTypeFromTarget = INHERIT_TYPE_FROM_TARGET_EDEFAULT;

	/**
	 * The default value of the '{@link #isExpand() <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExpand()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EXPAND_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isExpand() <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExpand()
	 * @generated
	 * @ordered
	 */
	protected boolean expand = EXPAND_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReferenceCodecAspectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.REFERENCE_CODEC_ASPECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ReferenceSerializationConfig getReferenceConfig() {
		return referenceConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetReferenceConfig(ReferenceSerializationConfig newReferenceConfig, NotificationChain msgs) {
		ReferenceSerializationConfig oldReferenceConfig = referenceConfig;
		referenceConfig = newReferenceConfig;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG, oldReferenceConfig, newReferenceConfig);
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
	public void setReferenceConfig(ReferenceSerializationConfig newReferenceConfig) {
		if (newReferenceConfig != referenceConfig) {
			NotificationChain msgs = null;
			if (referenceConfig != null)
				msgs = ((InternalEObject)referenceConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG, null, msgs);
			if (newReferenceConfig != null)
				msgs = ((InternalEObject)newReferenceConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG, null, msgs);
			msgs = basicSetReferenceConfig(newReferenceConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG, newReferenceConfig, newReferenceConfig));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.REFERENCE_CODEC_ASPECT__TYPE_CONFIG, oldTypeConfig, newTypeConfig);
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
				msgs = ((InternalEObject)typeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.REFERENCE_CODEC_ASPECT__TYPE_CONFIG, null, msgs);
			if (newTypeConfig != null)
				msgs = ((InternalEObject)newTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.REFERENCE_CODEC_ASPECT__TYPE_CONFIG, null, msgs);
			msgs = basicSetTypeConfig(newTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.REFERENCE_CODEC_ASPECT__TYPE_CONFIG, newTypeConfig, newTypeConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isInheritTypeFromTarget() {
		return inheritTypeFromTarget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInheritTypeFromTarget(boolean newInheritTypeFromTarget) {
		boolean oldInheritTypeFromTarget = inheritTypeFromTarget;
		inheritTypeFromTarget = newInheritTypeFromTarget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.REFERENCE_CODEC_ASPECT__INHERIT_TYPE_FROM_TARGET, oldInheritTypeFromTarget, inheritTypeFromTarget));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isExpand() {
		return expand;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpand(boolean newExpand) {
		boolean oldExpand = expand;
		expand = newExpand;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.REFERENCE_CODEC_ASPECT__EXPAND, oldExpand, expand));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecPackage.REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG:
				return basicSetReferenceConfig(null, msgs);
			case CodecPackage.REFERENCE_CODEC_ASPECT__TYPE_CONFIG:
				return basicSetTypeConfig(null, msgs);
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
			case CodecPackage.REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG:
				return getReferenceConfig();
			case CodecPackage.REFERENCE_CODEC_ASPECT__TYPE_CONFIG:
				return getTypeConfig();
			case CodecPackage.REFERENCE_CODEC_ASPECT__INHERIT_TYPE_FROM_TARGET:
				return isInheritTypeFromTarget();
			case CodecPackage.REFERENCE_CODEC_ASPECT__EXPAND:
				return isExpand();
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
			case CodecPackage.REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG:
				setReferenceConfig((ReferenceSerializationConfig)newValue);
				return;
			case CodecPackage.REFERENCE_CODEC_ASPECT__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)newValue);
				return;
			case CodecPackage.REFERENCE_CODEC_ASPECT__INHERIT_TYPE_FROM_TARGET:
				setInheritTypeFromTarget((Boolean)newValue);
				return;
			case CodecPackage.REFERENCE_CODEC_ASPECT__EXPAND:
				setExpand((Boolean)newValue);
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
			case CodecPackage.REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG:
				setReferenceConfig((ReferenceSerializationConfig)null);
				return;
			case CodecPackage.REFERENCE_CODEC_ASPECT__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)null);
				return;
			case CodecPackage.REFERENCE_CODEC_ASPECT__INHERIT_TYPE_FROM_TARGET:
				setInheritTypeFromTarget(INHERIT_TYPE_FROM_TARGET_EDEFAULT);
				return;
			case CodecPackage.REFERENCE_CODEC_ASPECT__EXPAND:
				setExpand(EXPAND_EDEFAULT);
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
			case CodecPackage.REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG:
				return referenceConfig != null;
			case CodecPackage.REFERENCE_CODEC_ASPECT__TYPE_CONFIG:
				return typeConfig != null;
			case CodecPackage.REFERENCE_CODEC_ASPECT__INHERIT_TYPE_FROM_TARGET:
				return inheritTypeFromTarget != INHERIT_TYPE_FROM_TARGET_EDEFAULT;
			case CodecPackage.REFERENCE_CODEC_ASPECT__EXPAND:
				return expand != EXPAND_EDEFAULT;
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
		result.append(" (inheritTypeFromTarget: ");
		result.append(inheritTypeFromTarget);
		result.append(", expand: ");
		result.append(expand);
		result.append(')');
		return result.toString();
	}

} //ReferenceCodecAspectImpl
