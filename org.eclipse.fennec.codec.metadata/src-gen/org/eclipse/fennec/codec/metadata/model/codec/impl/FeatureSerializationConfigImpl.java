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
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Feature Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl#getFeatureName <em>Feature Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl#getValueWriterName <em>Value Writer Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl#getValueReaderName <em>Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl#getExpand <em>Expand</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl#getReferenceConfig <em>Reference Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureSerializationConfigImpl#getTypeConfig <em>Type Config</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FeatureSerializationConfigImpl extends BaseFeatureConfigImpl implements FeatureSerializationConfig {
	/**
	 * The default value of the '{@link #getFeatureName() <em>Feature Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatureName()
	 * @generated
	 * @ordered
	 */
	protected static final String FEATURE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFeatureName() <em>Feature Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatureName()
	 * @generated
	 * @ordered
	 */
	protected String featureName = FEATURE_NAME_EDEFAULT;

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
	 * The default value of the '{@link #getExpand() <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpand()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean EXPAND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExpand() <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpand()
	 * @generated
	 * @ordered
	 */
	protected Boolean expand = EXPAND_EDEFAULT;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FeatureSerializationConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.FEATURE_SERIALIZATION_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getFeatureName() {
		return featureName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFeatureName(String newFeatureName) {
		String oldFeatureName = featureName;
		featureName = newFeatureName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_SERIALIZATION_CONFIG__FEATURE_NAME, oldFeatureName, featureName));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_WRITER_NAME, oldValueWriterName, valueWriterName));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_READER_NAME, oldValueReaderName, valueReaderName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getExpand() {
		return expand;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpand(Boolean newExpand) {
		Boolean oldExpand = expand;
		expand = newExpand;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_SERIALIZATION_CONFIG__EXPAND, oldExpand, expand));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG, oldReferenceConfig, newReferenceConfig);
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
				msgs = ((InternalEObject)referenceConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG, null, msgs);
			if (newReferenceConfig != null)
				msgs = ((InternalEObject)newReferenceConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG, null, msgs);
			msgs = basicSetReferenceConfig(newReferenceConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG, newReferenceConfig, newReferenceConfig));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG, oldTypeConfig, newTypeConfig);
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
				msgs = ((InternalEObject)typeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG, null, msgs);
			if (newTypeConfig != null)
				msgs = ((InternalEObject)newTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG, null, msgs);
			msgs = basicSetTypeConfig(newTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG, newTypeConfig, newTypeConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG:
				return basicSetReferenceConfig(null, msgs);
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG:
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
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__FEATURE_NAME:
				return getFeatureName();
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_WRITER_NAME:
				return getValueWriterName();
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_READER_NAME:
				return getValueReaderName();
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__EXPAND:
				return getExpand();
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG:
				return getReferenceConfig();
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG:
				return getTypeConfig();
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
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__FEATURE_NAME:
				setFeatureName((String)newValue);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_WRITER_NAME:
				setValueWriterName((String)newValue);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_READER_NAME:
				setValueReaderName((String)newValue);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__EXPAND:
				setExpand((Boolean)newValue);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG:
				setReferenceConfig((ReferenceSerializationConfig)newValue);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)newValue);
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
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__FEATURE_NAME:
				setFeatureName(FEATURE_NAME_EDEFAULT);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_WRITER_NAME:
				setValueWriterName(VALUE_WRITER_NAME_EDEFAULT);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_READER_NAME:
				setValueReaderName(VALUE_READER_NAME_EDEFAULT);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__EXPAND:
				setExpand(EXPAND_EDEFAULT);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG:
				setReferenceConfig((ReferenceSerializationConfig)null);
				return;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)null);
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
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__FEATURE_NAME:
				return FEATURE_NAME_EDEFAULT == null ? featureName != null : !FEATURE_NAME_EDEFAULT.equals(featureName);
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_WRITER_NAME:
				return VALUE_WRITER_NAME_EDEFAULT == null ? valueWriterName != null : !VALUE_WRITER_NAME_EDEFAULT.equals(valueWriterName);
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__VALUE_READER_NAME:
				return VALUE_READER_NAME_EDEFAULT == null ? valueReaderName != null : !VALUE_READER_NAME_EDEFAULT.equals(valueReaderName);
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__EXPAND:
				return EXPAND_EDEFAULT == null ? expand != null : !EXPAND_EDEFAULT.equals(expand);
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG:
				return referenceConfig != null;
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG:
				return typeConfig != null;
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
		result.append(" (featureName: ");
		result.append(featureName);
		result.append(", valueWriterName: ");
		result.append(valueWriterName);
		result.append(", valueReaderName: ");
		result.append(valueReaderName);
		result.append(", expand: ");
		result.append(expand);
		result.append(')');
		return result.toString();
	}

} //FeatureSerializationConfigImpl
