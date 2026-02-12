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

import org.eclipse.fennec.model.metadata.BaseTypeConfig;
import org.eclipse.fennec.model.metadata.MetadataPackage;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.TypeStrategy;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Base Type Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl#getStrategy <em>Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl#getSchemaKey <em>Schema Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.metadata.impl.BaseTypeConfigImpl#getNameKey <em>Name Key</em>}</li>
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
		return MetadataPackage.Literals.BASE_TYPE_CONFIG;
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
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_TYPE_CONFIG__FORMAT, oldFormat, format));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_TYPE_CONFIG__STRATEGY, oldStrategy, strategy));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_TYPE_CONFIG__TYPE_KEY, oldTypeKey, typeKey));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_TYPE_CONFIG__SCHEMA_KEY, oldSchemaKey, schemaKey));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetadataPackage.BASE_TYPE_CONFIG__NAME_KEY, oldNameKey, nameKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MetadataPackage.BASE_TYPE_CONFIG__FORMAT:
				return getFormat();
			case MetadataPackage.BASE_TYPE_CONFIG__STRATEGY:
				return getStrategy();
			case MetadataPackage.BASE_TYPE_CONFIG__TYPE_KEY:
				return getTypeKey();
			case MetadataPackage.BASE_TYPE_CONFIG__SCHEMA_KEY:
				return getSchemaKey();
			case MetadataPackage.BASE_TYPE_CONFIG__NAME_KEY:
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
			case MetadataPackage.BASE_TYPE_CONFIG__FORMAT:
				setFormat((SerializationFormat)newValue);
				return;
			case MetadataPackage.BASE_TYPE_CONFIG__STRATEGY:
				setStrategy((TypeStrategy)newValue);
				return;
			case MetadataPackage.BASE_TYPE_CONFIG__TYPE_KEY:
				setTypeKey((String)newValue);
				return;
			case MetadataPackage.BASE_TYPE_CONFIG__SCHEMA_KEY:
				setSchemaKey((String)newValue);
				return;
			case MetadataPackage.BASE_TYPE_CONFIG__NAME_KEY:
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
			case MetadataPackage.BASE_TYPE_CONFIG__FORMAT:
				setFormat(FORMAT_EDEFAULT);
				return;
			case MetadataPackage.BASE_TYPE_CONFIG__STRATEGY:
				setStrategy(STRATEGY_EDEFAULT);
				return;
			case MetadataPackage.BASE_TYPE_CONFIG__TYPE_KEY:
				setTypeKey(TYPE_KEY_EDEFAULT);
				return;
			case MetadataPackage.BASE_TYPE_CONFIG__SCHEMA_KEY:
				setSchemaKey(SCHEMA_KEY_EDEFAULT);
				return;
			case MetadataPackage.BASE_TYPE_CONFIG__NAME_KEY:
				setNameKey(NAME_KEY_EDEFAULT);
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
			case MetadataPackage.BASE_TYPE_CONFIG__FORMAT:
				return format != FORMAT_EDEFAULT;
			case MetadataPackage.BASE_TYPE_CONFIG__STRATEGY:
				return strategy != STRATEGY_EDEFAULT;
			case MetadataPackage.BASE_TYPE_CONFIG__TYPE_KEY:
				return TYPE_KEY_EDEFAULT == null ? typeKey != null : !TYPE_KEY_EDEFAULT.equals(typeKey);
			case MetadataPackage.BASE_TYPE_CONFIG__SCHEMA_KEY:
				return SCHEMA_KEY_EDEFAULT == null ? schemaKey != null : !SCHEMA_KEY_EDEFAULT.equals(schemaKey);
			case MetadataPackage.BASE_TYPE_CONFIG__NAME_KEY:
				return NAME_KEY_EDEFAULT == null ? nameKey != null : !NAME_KEY_EDEFAULT.equals(nameKey);
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
		result.append(format);
		result.append(", strategy: ");
		result.append(strategy);
		result.append(", typeKey: ");
		result.append(typeKey);
		result.append(", schemaKey: ");
		result.append(schemaKey);
		result.append(", nameKey: ");
		result.append(nameKey);
		result.append(')');
		return result.toString();
	}

} //BaseTypeConfigImpl
