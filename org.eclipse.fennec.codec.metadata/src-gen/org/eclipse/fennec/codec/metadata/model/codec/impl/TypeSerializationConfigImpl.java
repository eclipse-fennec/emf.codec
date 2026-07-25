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

import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode;
import org.eclipse.fennec.codec.metadata.model.codec.StrategyScope;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;

import org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Type Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl#getMapId <em>Map Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl#getDiscriminatorPath <em>Discriminator Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl#getDiscriminatorValue <em>Discriminator Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl#getFingerprintMode <em>Fingerprint Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl#getFingerprintKey <em>Fingerprint Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl#getStrategyScope <em>Strategy Scope</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.TypeSerializationConfigImpl#getFormatScope <em>Format Scope</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TypeSerializationConfigImpl extends BaseTypeConfigImpl implements TypeSerializationConfig {
	/**
	 * The default value of the '{@link #getMapId() <em>Map Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapId()
	 * @generated
	 * @ordered
	 */
	protected static final String MAP_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMapId() <em>Map Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapId()
	 * @generated
	 * @ordered
	 */
	protected String mapId = MAP_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getDiscriminatorPath() <em>Discriminator Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiscriminatorPath()
	 * @generated
	 * @ordered
	 */
	protected static final String DISCRIMINATOR_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDiscriminatorPath() <em>Discriminator Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiscriminatorPath()
	 * @generated
	 * @ordered
	 */
	protected String discriminatorPath = DISCRIMINATOR_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getDiscriminatorValue() <em>Discriminator Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiscriminatorValue()
	 * @generated
	 * @ordered
	 */
	protected static final String DISCRIMINATOR_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDiscriminatorValue() <em>Discriminator Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiscriminatorValue()
	 * @generated
	 * @ordered
	 */
	protected String discriminatorValue = DISCRIMINATOR_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getFingerprintMode() <em>Fingerprint Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFingerprintMode()
	 * @generated
	 * @ordered
	 */
	protected static final FingerprintMode FINGERPRINT_MODE_EDEFAULT = FingerprintMode.NONE;

	/**
	 * The cached value of the '{@link #getFingerprintMode() <em>Fingerprint Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFingerprintMode()
	 * @generated
	 * @ordered
	 */
	protected FingerprintMode fingerprintMode = FINGERPRINT_MODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getFingerprintKey() <em>Fingerprint Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFingerprintKey()
	 * @generated
	 * @ordered
	 */
	protected static final String FINGERPRINT_KEY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFingerprintKey() <em>Fingerprint Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFingerprintKey()
	 * @generated
	 * @ordered
	 */
	protected String fingerprintKey = FINGERPRINT_KEY_EDEFAULT;

	/**
	 * The default value of the '{@link #getStrategyScope() <em>Strategy Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStrategyScope()
	 * @generated
	 * @ordered
	 */
	protected static final StrategyScope STRATEGY_SCOPE_EDEFAULT = StrategyScope.ALL;

	/**
	 * The cached value of the '{@link #getStrategyScope() <em>Strategy Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStrategyScope()
	 * @generated
	 * @ordered
	 */
	protected StrategyScope strategyScope = STRATEGY_SCOPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getFormatScope() <em>Format Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormatScope()
	 * @generated
	 * @ordered
	 */
	protected static final StrategyScope FORMAT_SCOPE_EDEFAULT = StrategyScope.ALL;

	/**
	 * The cached value of the '{@link #getFormatScope() <em>Format Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormatScope()
	 * @generated
	 * @ordered
	 */
	protected StrategyScope formatScope = FORMAT_SCOPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TypeSerializationConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.TYPE_SERIALIZATION_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMapId() {
		return mapId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMapId(String newMapId) {
		String oldMapId = mapId;
		mapId = newMapId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.TYPE_SERIALIZATION_CONFIG__MAP_ID, oldMapId, mapId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDiscriminatorPath() {
		return discriminatorPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDiscriminatorPath(String newDiscriminatorPath) {
		String oldDiscriminatorPath = discriminatorPath;
		discriminatorPath = newDiscriminatorPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH, oldDiscriminatorPath, discriminatorPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDiscriminatorValue() {
		return discriminatorValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDiscriminatorValue(String newDiscriminatorValue) {
		String oldDiscriminatorValue = discriminatorValue;
		discriminatorValue = newDiscriminatorValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE, oldDiscriminatorValue, discriminatorValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FingerprintMode getFingerprintMode() {
		return fingerprintMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFingerprintMode(FingerprintMode newFingerprintMode) {
		FingerprintMode oldFingerprintMode = fingerprintMode;
		fingerprintMode = newFingerprintMode == null ? FINGERPRINT_MODE_EDEFAULT : newFingerprintMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_MODE, oldFingerprintMode, fingerprintMode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getFingerprintKey() {
		return fingerprintKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFingerprintKey(String newFingerprintKey) {
		String oldFingerprintKey = fingerprintKey;
		fingerprintKey = newFingerprintKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_KEY, oldFingerprintKey, fingerprintKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StrategyScope getStrategyScope() {
		return strategyScope;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStrategyScope(StrategyScope newStrategyScope) {
		StrategyScope oldStrategyScope = strategyScope;
		strategyScope = newStrategyScope == null ? STRATEGY_SCOPE_EDEFAULT : newStrategyScope;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.TYPE_SERIALIZATION_CONFIG__STRATEGY_SCOPE, oldStrategyScope, strategyScope));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StrategyScope getFormatScope() {
		return formatScope;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFormatScope(StrategyScope newFormatScope) {
		StrategyScope oldFormatScope = formatScope;
		formatScope = newFormatScope == null ? FORMAT_SCOPE_EDEFAULT : newFormatScope;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.TYPE_SERIALIZATION_CONFIG__FORMAT_SCOPE, oldFormatScope, formatScope));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__MAP_ID:
				return getMapId();
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH:
				return getDiscriminatorPath();
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE:
				return getDiscriminatorValue();
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_MODE:
				return getFingerprintMode();
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_KEY:
				return getFingerprintKey();
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__STRATEGY_SCOPE:
				return getStrategyScope();
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FORMAT_SCOPE:
				return getFormatScope();
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
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__MAP_ID:
				setMapId((String)newValue);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH:
				setDiscriminatorPath((String)newValue);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE:
				setDiscriminatorValue((String)newValue);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_MODE:
				setFingerprintMode((FingerprintMode)newValue);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_KEY:
				setFingerprintKey((String)newValue);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__STRATEGY_SCOPE:
				setStrategyScope((StrategyScope)newValue);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FORMAT_SCOPE:
				setFormatScope((StrategyScope)newValue);
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
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__MAP_ID:
				setMapId(MAP_ID_EDEFAULT);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH:
				setDiscriminatorPath(DISCRIMINATOR_PATH_EDEFAULT);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE:
				setDiscriminatorValue(DISCRIMINATOR_VALUE_EDEFAULT);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_MODE:
				setFingerprintMode(FINGERPRINT_MODE_EDEFAULT);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_KEY:
				setFingerprintKey(FINGERPRINT_KEY_EDEFAULT);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__STRATEGY_SCOPE:
				setStrategyScope(STRATEGY_SCOPE_EDEFAULT);
				return;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FORMAT_SCOPE:
				setFormatScope(FORMAT_SCOPE_EDEFAULT);
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
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__MAP_ID:
				return MAP_ID_EDEFAULT == null ? mapId != null : !MAP_ID_EDEFAULT.equals(mapId);
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH:
				return DISCRIMINATOR_PATH_EDEFAULT == null ? discriminatorPath != null : !DISCRIMINATOR_PATH_EDEFAULT.equals(discriminatorPath);
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE:
				return DISCRIMINATOR_VALUE_EDEFAULT == null ? discriminatorValue != null : !DISCRIMINATOR_VALUE_EDEFAULT.equals(discriminatorValue);
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_MODE:
				return fingerprintMode != FINGERPRINT_MODE_EDEFAULT;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FINGERPRINT_KEY:
				return FINGERPRINT_KEY_EDEFAULT == null ? fingerprintKey != null : !FINGERPRINT_KEY_EDEFAULT.equals(fingerprintKey);
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__STRATEGY_SCOPE:
				return strategyScope != STRATEGY_SCOPE_EDEFAULT;
			case CodecPackage.TYPE_SERIALIZATION_CONFIG__FORMAT_SCOPE:
				return formatScope != FORMAT_SCOPE_EDEFAULT;
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
		result.append(" (mapId: ");
		result.append(mapId);
		result.append(", discriminatorPath: ");
		result.append(discriminatorPath);
		result.append(", discriminatorValue: ");
		result.append(discriminatorValue);
		result.append(", fingerprintMode: ");
		result.append(fingerprintMode);
		result.append(", fingerprintKey: ");
		result.append(fingerprintKey);
		result.append(", strategyScope: ");
		result.append(strategyScope);
		result.append(", formatScope: ");
		result.append(formatScope);
		result.append(')');
		return result.toString();
	}

} //TypeSerializationConfigImpl
