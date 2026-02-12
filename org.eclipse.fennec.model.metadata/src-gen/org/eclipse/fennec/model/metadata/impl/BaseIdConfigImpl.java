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

import org.eclipse.fennec.model.metadata.BaseIdConfig;
import org.eclipse.fennec.model.metadata.IdKeyMode;
import org.eclipse.fennec.model.metadata.IdStrategy;
import org.eclipse.fennec.model.metadata.MetadataPackage;
import org.eclipse.fennec.model.metadata.SerializationFormat;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Base Id Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl#getStrategy <em>Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl#getKeyMode <em>Key Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl#getIdKey <em>Id Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl#getSeparator <em>Separator</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl#isOnTop <em>On Top</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl#isSerializeSeparator <em>Serialize Separator</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl#getSeparatorKey <em>Separator Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl#getValueKey <em>Value Key</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class BaseIdConfigImpl extends MinimalEObjectImpl.Container implements BaseIdConfig {
	/**
	 * The default value of the '{@link #getStrategy() <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStrategy()
	 * @generated
	 * @ordered
	 */
	protected static final IdStrategy STRATEGY_EDEFAULT = IdStrategy.ID_FIELD;

	/**
	 * The cached value of the '{@link #getStrategy() <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStrategy()
	 * @generated
	 * @ordered
	 */
	protected IdStrategy strategy = STRATEGY_EDEFAULT;

	/**
	 * The default value of the '{@link #getKeyMode() <em>Key Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKeyMode()
	 * @generated
	 * @ordered
	 */
	protected static final IdKeyMode KEY_MODE_EDEFAULT = IdKeyMode.ID_ONLY;

	/**
	 * The cached value of the '{@link #getKeyMode() <em>Key Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKeyMode()
	 * @generated
	 * @ordered
	 */
	protected IdKeyMode keyMode = KEY_MODE_EDEFAULT;

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
	 * The default value of the '{@link #getIdKey() <em>Id Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdKey()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_KEY_EDEFAULT = "_id";

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
	 * The default value of the '{@link #getSeparator() <em>Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSeparator()
	 * @generated
	 * @ordered
	 */
	protected static final String SEPARATOR_EDEFAULT = "-";

	/**
	 * The cached value of the '{@link #getSeparator() <em>Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSeparator()
	 * @generated
	 * @ordered
	 */
	protected String separator = SEPARATOR_EDEFAULT;

	/**
	 * The default value of the '{@link #isOnTop() <em>On Top</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isOnTop()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ON_TOP_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isOnTop() <em>On Top</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isOnTop()
	 * @generated
	 * @ordered
	 */
	protected boolean onTop = ON_TOP_EDEFAULT;

	/**
	 * The default value of the '{@link #isSerializeSeparator() <em>Serialize Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSerializeSeparator()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SERIALIZE_SEPARATOR_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isSerializeSeparator() <em>Serialize Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSerializeSeparator()
	 * @generated
	 * @ordered
	 */
	protected boolean serializeSeparator = SERIALIZE_SEPARATOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getSeparatorKey() <em>Separator Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSeparatorKey()
	 * @generated
	 * @ordered
	 */
	protected static final String SEPARATOR_KEY_EDEFAULT = "separator";

	/**
	 * The cached value of the '{@link #getSeparatorKey() <em>Separator Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSeparatorKey()
	 * @generated
	 * @ordered
	 */
	protected String separatorKey = SEPARATOR_KEY_EDEFAULT;

	/**
	 * The default value of the '{@link #getValueKey() <em>Value Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueKey()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_KEY_EDEFAULT = "id";

	/**
	 * The cached value of the '{@link #getValueKey() <em>Value Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueKey()
	 * @generated
	 * @ordered
	 */
	protected String valueKey = VALUE_KEY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BaseIdConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetadataPackage.Literals.BASE_ID_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IdStrategy getStrategy() {
		return strategy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStrategy(IdStrategy newStrategy) {
		IdStrategy oldStrategy = strategy;
		strategy = newStrategy == null ? STRATEGY_EDEFAULT : newStrategy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_ID_CONFIG__STRATEGY, oldStrategy, strategy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IdKeyMode getKeyMode() {
		return keyMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setKeyMode(IdKeyMode newKeyMode) {
		IdKeyMode oldKeyMode = keyMode;
		keyMode = newKeyMode == null ? KEY_MODE_EDEFAULT : newKeyMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_ID_CONFIG__KEY_MODE, oldKeyMode, keyMode));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_ID_CONFIG__FORMAT, oldFormat, format));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_ID_CONFIG__ID_KEY, oldIdKey, idKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSeparator() {
		return separator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSeparator(String newSeparator) {
		String oldSeparator = separator;
		separator = newSeparator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_ID_CONFIG__SEPARATOR, oldSeparator, separator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isOnTop() {
		return onTop;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOnTop(boolean newOnTop) {
		boolean oldOnTop = onTop;
		onTop = newOnTop;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_ID_CONFIG__ON_TOP, oldOnTop, onTop));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSerializeSeparator() {
		return serializeSeparator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSerializeSeparator(boolean newSerializeSeparator) {
		boolean oldSerializeSeparator = serializeSeparator;
		serializeSeparator = newSerializeSeparator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_ID_CONFIG__SERIALIZE_SEPARATOR, oldSerializeSeparator, serializeSeparator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSeparatorKey() {
		return separatorKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSeparatorKey(String newSeparatorKey) {
		String oldSeparatorKey = separatorKey;
		separatorKey = newSeparatorKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_ID_CONFIG__SEPARATOR_KEY, oldSeparatorKey, separatorKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getValueKey() {
		return valueKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValueKey(String newValueKey) {
		String oldValueKey = valueKey;
		valueKey = newValueKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_ID_CONFIG__VALUE_KEY, oldValueKey, valueKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MetadataPackage.BASE_ID_CONFIG__STRATEGY:
				return getStrategy();
			case MetadataPackage.BASE_ID_CONFIG__KEY_MODE:
				return getKeyMode();
			case MetadataPackage.BASE_ID_CONFIG__FORMAT:
				return getFormat();
			case MetadataPackage.BASE_ID_CONFIG__ID_KEY:
				return getIdKey();
			case MetadataPackage.BASE_ID_CONFIG__SEPARATOR:
				return getSeparator();
			case MetadataPackage.BASE_ID_CONFIG__ON_TOP:
				return isOnTop();
			case MetadataPackage.BASE_ID_CONFIG__SERIALIZE_SEPARATOR:
				return isSerializeSeparator();
			case MetadataPackage.BASE_ID_CONFIG__SEPARATOR_KEY:
				return getSeparatorKey();
			case MetadataPackage.BASE_ID_CONFIG__VALUE_KEY:
				return getValueKey();
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
			case MetadataPackage.BASE_ID_CONFIG__STRATEGY:
				setStrategy((IdStrategy)newValue);
				return;
			case MetadataPackage.BASE_ID_CONFIG__KEY_MODE:
				setKeyMode((IdKeyMode)newValue);
				return;
			case MetadataPackage.BASE_ID_CONFIG__FORMAT:
				setFormat((SerializationFormat)newValue);
				return;
			case MetadataPackage.BASE_ID_CONFIG__ID_KEY:
				setIdKey((String)newValue);
				return;
			case MetadataPackage.BASE_ID_CONFIG__SEPARATOR:
				setSeparator((String)newValue);
				return;
			case MetadataPackage.BASE_ID_CONFIG__ON_TOP:
				setOnTop((Boolean)newValue);
				return;
			case MetadataPackage.BASE_ID_CONFIG__SERIALIZE_SEPARATOR:
				setSerializeSeparator((Boolean)newValue);
				return;
			case MetadataPackage.BASE_ID_CONFIG__SEPARATOR_KEY:
				setSeparatorKey((String)newValue);
				return;
			case MetadataPackage.BASE_ID_CONFIG__VALUE_KEY:
				setValueKey((String)newValue);
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
			case MetadataPackage.BASE_ID_CONFIG__STRATEGY:
				setStrategy(STRATEGY_EDEFAULT);
				return;
			case MetadataPackage.BASE_ID_CONFIG__KEY_MODE:
				setKeyMode(KEY_MODE_EDEFAULT);
				return;
			case MetadataPackage.BASE_ID_CONFIG__FORMAT:
				setFormat(FORMAT_EDEFAULT);
				return;
			case MetadataPackage.BASE_ID_CONFIG__ID_KEY:
				setIdKey(ID_KEY_EDEFAULT);
				return;
			case MetadataPackage.BASE_ID_CONFIG__SEPARATOR:
				setSeparator(SEPARATOR_EDEFAULT);
				return;
			case MetadataPackage.BASE_ID_CONFIG__ON_TOP:
				setOnTop(ON_TOP_EDEFAULT);
				return;
			case MetadataPackage.BASE_ID_CONFIG__SERIALIZE_SEPARATOR:
				setSerializeSeparator(SERIALIZE_SEPARATOR_EDEFAULT);
				return;
			case MetadataPackage.BASE_ID_CONFIG__SEPARATOR_KEY:
				setSeparatorKey(SEPARATOR_KEY_EDEFAULT);
				return;
			case MetadataPackage.BASE_ID_CONFIG__VALUE_KEY:
				setValueKey(VALUE_KEY_EDEFAULT);
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
			case MetadataPackage.BASE_ID_CONFIG__STRATEGY:
				return strategy != STRATEGY_EDEFAULT;
			case MetadataPackage.BASE_ID_CONFIG__KEY_MODE:
				return keyMode != KEY_MODE_EDEFAULT;
			case MetadataPackage.BASE_ID_CONFIG__FORMAT:
				return format != FORMAT_EDEFAULT;
			case MetadataPackage.BASE_ID_CONFIG__ID_KEY:
				return ID_KEY_EDEFAULT == null ? idKey != null : !ID_KEY_EDEFAULT.equals(idKey);
			case MetadataPackage.BASE_ID_CONFIG__SEPARATOR:
				return SEPARATOR_EDEFAULT == null ? separator != null : !SEPARATOR_EDEFAULT.equals(separator);
			case MetadataPackage.BASE_ID_CONFIG__ON_TOP:
				return onTop != ON_TOP_EDEFAULT;
			case MetadataPackage.BASE_ID_CONFIG__SERIALIZE_SEPARATOR:
				return serializeSeparator != SERIALIZE_SEPARATOR_EDEFAULT;
			case MetadataPackage.BASE_ID_CONFIG__SEPARATOR_KEY:
				return SEPARATOR_KEY_EDEFAULT == null ? separatorKey != null : !SEPARATOR_KEY_EDEFAULT.equals(separatorKey);
			case MetadataPackage.BASE_ID_CONFIG__VALUE_KEY:
				return VALUE_KEY_EDEFAULT == null ? valueKey != null : !VALUE_KEY_EDEFAULT.equals(valueKey);
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
		result.append(" (strategy: ");
		result.append(strategy);
		result.append(", keyMode: ");
		result.append(keyMode);
		result.append(", format: ");
		result.append(format);
		result.append(", idKey: ");
		result.append(idKey);
		result.append(", separator: ");
		result.append(separator);
		result.append(", onTop: ");
		result.append(onTop);
		result.append(", serializeSeparator: ");
		result.append(serializeSeparator);
		result.append(", separatorKey: ");
		result.append(separatorKey);
		result.append(", valueKey: ");
		result.append(valueKey);
		result.append(')');
		return result.toString();
	}

} //BaseIdConfigImpl
