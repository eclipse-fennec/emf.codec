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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.StrategyScope;

import org.eclipse.fennec.model.metadata.impl.BaseIdConfigImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Id Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl#getIdFeatures <em>Id Features</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl#getIdValueWriterName <em>Id Value Writer Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl#getIdValueReaderName <em>Id Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl#getStrategyScope <em>Strategy Scope</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.IdSerializationConfigImpl#getFormatScope <em>Format Scope</em>}</li>
 * </ul>
 *
 * @generated
 */
public class IdSerializationConfigImpl extends BaseIdConfigImpl implements IdSerializationConfig {
	/**
	 * The cached value of the '{@link #getIdFeatures() <em>Id Features</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdFeatures()
	 * @generated
	 * @ordered
	 */
	protected EList<String> idFeatures;

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
	protected IdSerializationConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.ID_SERIALIZATION_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getIdFeatures() {
		if (idFeatures == null) {
			idFeatures = new EDataTypeUniqueEList<String>(String.class, this, CodecPackage.ID_SERIALIZATION_CONFIG__ID_FEATURES);
		}
		return idFeatures;
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_WRITER_NAME, oldIdValueWriterName, idValueWriterName));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_READER_NAME, oldIdValueReaderName, idValueReaderName));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.ID_SERIALIZATION_CONFIG__STRATEGY_SCOPE, oldStrategyScope, strategyScope));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.ID_SERIALIZATION_CONFIG__FORMAT_SCOPE, oldFormatScope, formatScope));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_FEATURES:
				return getIdFeatures();
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_WRITER_NAME:
				return getIdValueWriterName();
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_READER_NAME:
				return getIdValueReaderName();
			case CodecPackage.ID_SERIALIZATION_CONFIG__STRATEGY_SCOPE:
				return getStrategyScope();
			case CodecPackage.ID_SERIALIZATION_CONFIG__FORMAT_SCOPE:
				return getFormatScope();
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
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_FEATURES:
				getIdFeatures().clear();
				getIdFeatures().addAll((Collection<? extends String>)newValue);
				return;
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_WRITER_NAME:
				setIdValueWriterName((String)newValue);
				return;
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_READER_NAME:
				setIdValueReaderName((String)newValue);
				return;
			case CodecPackage.ID_SERIALIZATION_CONFIG__STRATEGY_SCOPE:
				setStrategyScope((StrategyScope)newValue);
				return;
			case CodecPackage.ID_SERIALIZATION_CONFIG__FORMAT_SCOPE:
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
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_FEATURES:
				getIdFeatures().clear();
				return;
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_WRITER_NAME:
				setIdValueWriterName(ID_VALUE_WRITER_NAME_EDEFAULT);
				return;
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_READER_NAME:
				setIdValueReaderName(ID_VALUE_READER_NAME_EDEFAULT);
				return;
			case CodecPackage.ID_SERIALIZATION_CONFIG__STRATEGY_SCOPE:
				setStrategyScope(STRATEGY_SCOPE_EDEFAULT);
				return;
			case CodecPackage.ID_SERIALIZATION_CONFIG__FORMAT_SCOPE:
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
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_FEATURES:
				return idFeatures != null && !idFeatures.isEmpty();
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_WRITER_NAME:
				return ID_VALUE_WRITER_NAME_EDEFAULT == null ? idValueWriterName != null : !ID_VALUE_WRITER_NAME_EDEFAULT.equals(idValueWriterName);
			case CodecPackage.ID_SERIALIZATION_CONFIG__ID_VALUE_READER_NAME:
				return ID_VALUE_READER_NAME_EDEFAULT == null ? idValueReaderName != null : !ID_VALUE_READER_NAME_EDEFAULT.equals(idValueReaderName);
			case CodecPackage.ID_SERIALIZATION_CONFIG__STRATEGY_SCOPE:
				return strategyScope != STRATEGY_SCOPE_EDEFAULT;
			case CodecPackage.ID_SERIALIZATION_CONFIG__FORMAT_SCOPE:
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
		result.append(" (idFeatures: ");
		result.append(idFeatures);
		result.append(", idValueWriterName: ");
		result.append(idValueWriterName);
		result.append(", idValueReaderName: ");
		result.append(idValueReaderName);
		result.append(", strategyScope: ");
		result.append(strategyScope);
		result.append(", formatScope: ");
		result.append(formatScope);
		result.append(')');
		return result.toString();
	}

} //IdSerializationConfigImpl
