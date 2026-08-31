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

import org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Base Type Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseTypeConfigImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseTypeConfigImpl#getStrategy <em>Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseTypeConfigImpl#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseTypeConfigImpl#getSchemaKey <em>Schema Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseTypeConfigImpl#getNameKey <em>Name Key</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class BaseTypeConfigImpl extends MinimalEObjectImpl.Container implements BaseTypeConfig {
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
	 * The default value of the '{@link #getStrategy() <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStrategy()
	 * @generated
	 * @ordered
	 */
	protected static final TypeStrategy STRATEGY_EDEFAULT = TypeStrategy.URI;

	/**
	 * The cached value of the '{@link #getStrategy() <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStrategy()
	 * @generated
	 * @ordered
	 */
	protected TypeStrategy strategy = STRATEGY_EDEFAULT;

	/**
	 * This is true if the Strategy attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean strategyESet;

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
	 * The default value of the '{@link #getSchemaKey() <em>Schema Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchemaKey()
	 * @generated
	 * @ordered
	 */
	protected static final String SCHEMA_KEY_EDEFAULT = "schema";

	/**
	 * The cached value of the '{@link #getSchemaKey() <em>Schema Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchemaKey()
	 * @generated
	 * @ordered
	 */
	protected String schemaKey = SCHEMA_KEY_EDEFAULT;

	/**
	 * This is true if the Schema Key attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean schemaKeyESet;

	/**
	 * The default value of the '{@link #getNameKey() <em>Name Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNameKey()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_KEY_EDEFAULT = "name";

	/**
	 * The cached value of the '{@link #getNameKey() <em>Name Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNameKey()
	 * @generated
	 * @ordered
	 */
	protected String nameKey = NAME_KEY_EDEFAULT;

	/**
	 * This is true if the Name Key attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean nameKeyESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BaseTypeConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.BASE_TYPE_CONFIG;
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_TYPE_CONFIG__FORMAT, oldFormat, format, !oldFormatESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.BASE_TYPE_CONFIG__FORMAT, oldFormat, FORMAT_EDEFAULT, oldFormatESet));
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
	public TypeStrategy getStrategy() {
		return strategy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStrategy(TypeStrategy newStrategy) {
		TypeStrategy oldStrategy = strategy;
		strategy = newStrategy == null ? STRATEGY_EDEFAULT : newStrategy;
		boolean oldStrategyESet = strategyESet;
		strategyESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_TYPE_CONFIG__STRATEGY, oldStrategy, strategy, !oldStrategyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetStrategy() {
		TypeStrategy oldStrategy = strategy;
		boolean oldStrategyESet = strategyESet;
		strategy = STRATEGY_EDEFAULT;
		strategyESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.BASE_TYPE_CONFIG__STRATEGY, oldStrategy, STRATEGY_EDEFAULT, oldStrategyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetStrategy() {
		return strategyESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_TYPE_CONFIG__TYPE_KEY, oldTypeKey, typeKey, !oldTypeKeyESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.BASE_TYPE_CONFIG__TYPE_KEY, oldTypeKey, TYPE_KEY_EDEFAULT, oldTypeKeyESet));
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
	public String getSchemaKey() {
		return schemaKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSchemaKey(String newSchemaKey) {
		String oldSchemaKey = schemaKey;
		schemaKey = newSchemaKey;
		boolean oldSchemaKeyESet = schemaKeyESet;
		schemaKeyESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_TYPE_CONFIG__SCHEMA_KEY, oldSchemaKey, schemaKey, !oldSchemaKeyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetSchemaKey() {
		String oldSchemaKey = schemaKey;
		boolean oldSchemaKeyESet = schemaKeyESet;
		schemaKey = SCHEMA_KEY_EDEFAULT;
		schemaKeyESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.BASE_TYPE_CONFIG__SCHEMA_KEY, oldSchemaKey, SCHEMA_KEY_EDEFAULT, oldSchemaKeyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetSchemaKey() {
		return schemaKeyESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getNameKey() {
		return nameKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNameKey(String newNameKey) {
		String oldNameKey = nameKey;
		nameKey = newNameKey;
		boolean oldNameKeyESet = nameKeyESet;
		nameKeyESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_TYPE_CONFIG__NAME_KEY, oldNameKey, nameKey, !oldNameKeyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetNameKey() {
		String oldNameKey = nameKey;
		boolean oldNameKeyESet = nameKeyESet;
		nameKey = NAME_KEY_EDEFAULT;
		nameKeyESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.BASE_TYPE_CONFIG__NAME_KEY, oldNameKey, NAME_KEY_EDEFAULT, oldNameKeyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetNameKey() {
		return nameKeyESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecPackage.BASE_TYPE_CONFIG__FORMAT:
				return getFormat();
			case CodecPackage.BASE_TYPE_CONFIG__STRATEGY:
				return getStrategy();
			case CodecPackage.BASE_TYPE_CONFIG__TYPE_KEY:
				return getTypeKey();
			case CodecPackage.BASE_TYPE_CONFIG__SCHEMA_KEY:
				return getSchemaKey();
			case CodecPackage.BASE_TYPE_CONFIG__NAME_KEY:
				return getNameKey();
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
			case CodecPackage.BASE_TYPE_CONFIG__FORMAT:
				setFormat((SerializationFormat)newValue);
				return;
			case CodecPackage.BASE_TYPE_CONFIG__STRATEGY:
				setStrategy((TypeStrategy)newValue);
				return;
			case CodecPackage.BASE_TYPE_CONFIG__TYPE_KEY:
				setTypeKey((String)newValue);
				return;
			case CodecPackage.BASE_TYPE_CONFIG__SCHEMA_KEY:
				setSchemaKey((String)newValue);
				return;
			case CodecPackage.BASE_TYPE_CONFIG__NAME_KEY:
				setNameKey((String)newValue);
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
			case CodecPackage.BASE_TYPE_CONFIG__FORMAT:
				unsetFormat();
				return;
			case CodecPackage.BASE_TYPE_CONFIG__STRATEGY:
				unsetStrategy();
				return;
			case CodecPackage.BASE_TYPE_CONFIG__TYPE_KEY:
				unsetTypeKey();
				return;
			case CodecPackage.BASE_TYPE_CONFIG__SCHEMA_KEY:
				unsetSchemaKey();
				return;
			case CodecPackage.BASE_TYPE_CONFIG__NAME_KEY:
				unsetNameKey();
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
			case CodecPackage.BASE_TYPE_CONFIG__FORMAT:
				return isSetFormat();
			case CodecPackage.BASE_TYPE_CONFIG__STRATEGY:
				return isSetStrategy();
			case CodecPackage.BASE_TYPE_CONFIG__TYPE_KEY:
				return isSetTypeKey();
			case CodecPackage.BASE_TYPE_CONFIG__SCHEMA_KEY:
				return isSetSchemaKey();
			case CodecPackage.BASE_TYPE_CONFIG__NAME_KEY:
				return isSetNameKey();
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
		result.append(", strategy: ");
		if (strategyESet) result.append(strategy); else result.append("<unset>");
		result.append(", typeKey: ");
		if (typeKeyESet) result.append(typeKey); else result.append("<unset>");
		result.append(", schemaKey: ");
		if (schemaKeyESet) result.append(schemaKey); else result.append("<unset>");
		result.append(", nameKey: ");
		if (nameKeyESet) result.append(nameKey); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //BaseTypeConfigImpl
