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

import org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Base Super Type Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl#isEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl#getSelection <em>Selection</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl#isAsArray <em>As Array</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl#getSeparator <em>Separator</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.BaseSuperTypeConfigImpl#getSuperTypeKey <em>Super Type Key</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class BaseSuperTypeConfigImpl extends MinimalEObjectImpl.Container implements BaseSuperTypeConfig {
	/**
	 * The default value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnabled()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ENABLED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnabled()
	 * @generated
	 * @ordered
	 */
	protected boolean enabled = ENABLED_EDEFAULT;

	/**
	 * The default value of the '{@link #getSelection() <em>Selection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSelection()
	 * @generated
	 * @ordered
	 */
	protected static final SuperTypeSelection SELECTION_EDEFAULT = SuperTypeSelection.ALL;

	/**
	 * The cached value of the '{@link #getSelection() <em>Selection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSelection()
	 * @generated
	 * @ordered
	 */
	protected SuperTypeSelection selection = SELECTION_EDEFAULT;

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
	 * The default value of the '{@link #isAsArray() <em>As Array</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAsArray()
	 * @generated
	 * @ordered
	 */
	protected static final boolean AS_ARRAY_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isAsArray() <em>As Array</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAsArray()
	 * @generated
	 * @ordered
	 */
	protected boolean asArray = AS_ARRAY_EDEFAULT;

	/**
	 * The default value of the '{@link #getSeparator() <em>Separator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSeparator()
	 * @generated
	 * @ordered
	 */
	protected static final String SEPARATOR_EDEFAULT = ",";

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
	 * The default value of the '{@link #getSuperTypeKey() <em>Super Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperTypeKey()
	 * @generated
	 * @ordered
	 */
	protected static final String SUPER_TYPE_KEY_EDEFAULT = "_supertype";

	/**
	 * The cached value of the '{@link #getSuperTypeKey() <em>Super Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperTypeKey()
	 * @generated
	 * @ordered
	 */
	protected String superTypeKey = SUPER_TYPE_KEY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BaseSuperTypeConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.BASE_SUPER_TYPE_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEnabled(boolean newEnabled) {
		boolean oldEnabled = enabled;
		enabled = newEnabled;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_SUPER_TYPE_CONFIG__ENABLED, oldEnabled, enabled));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SuperTypeSelection getSelection() {
		return selection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSelection(SuperTypeSelection newSelection) {
		SuperTypeSelection oldSelection = selection;
		selection = newSelection == null ? SELECTION_EDEFAULT : newSelection;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_SUPER_TYPE_CONFIG__SELECTION, oldSelection, selection));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_SUPER_TYPE_CONFIG__FORMAT, oldFormat, format, !oldFormatESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.BASE_SUPER_TYPE_CONFIG__FORMAT, oldFormat, FORMAT_EDEFAULT, oldFormatESet));
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
	public boolean isAsArray() {
		return asArray;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAsArray(boolean newAsArray) {
		boolean oldAsArray = asArray;
		asArray = newAsArray;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_SUPER_TYPE_CONFIG__AS_ARRAY, oldAsArray, asArray));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_SUPER_TYPE_CONFIG__SEPARATOR, oldSeparator, separator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSuperTypeKey() {
		return superTypeKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSuperTypeKey(String newSuperTypeKey) {
		String oldSuperTypeKey = superTypeKey;
		superTypeKey = newSuperTypeKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY, oldSuperTypeKey, superTypeKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__ENABLED:
				return isEnabled();
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SELECTION:
				return getSelection();
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__FORMAT:
				return getFormat();
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__AS_ARRAY:
				return isAsArray();
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SEPARATOR:
				return getSeparator();
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY:
				return getSuperTypeKey();
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
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__ENABLED:
				setEnabled((Boolean)newValue);
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SELECTION:
				setSelection((SuperTypeSelection)newValue);
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__FORMAT:
				setFormat((SerializationFormat)newValue);
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__AS_ARRAY:
				setAsArray((Boolean)newValue);
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SEPARATOR:
				setSeparator((String)newValue);
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY:
				setSuperTypeKey((String)newValue);
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
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__ENABLED:
				setEnabled(ENABLED_EDEFAULT);
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SELECTION:
				setSelection(SELECTION_EDEFAULT);
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__FORMAT:
				unsetFormat();
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__AS_ARRAY:
				setAsArray(AS_ARRAY_EDEFAULT);
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SEPARATOR:
				setSeparator(SEPARATOR_EDEFAULT);
				return;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY:
				setSuperTypeKey(SUPER_TYPE_KEY_EDEFAULT);
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
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__ENABLED:
				return enabled != ENABLED_EDEFAULT;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SELECTION:
				return selection != SELECTION_EDEFAULT;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__FORMAT:
				return isSetFormat();
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__AS_ARRAY:
				return asArray != AS_ARRAY_EDEFAULT;
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SEPARATOR:
				return SEPARATOR_EDEFAULT == null ? separator != null : !SEPARATOR_EDEFAULT.equals(separator);
			case CodecPackage.BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY:
				return SUPER_TYPE_KEY_EDEFAULT == null ? superTypeKey != null : !SUPER_TYPE_KEY_EDEFAULT.equals(superTypeKey);
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
		result.append(" (enabled: ");
		result.append(enabled);
		result.append(", selection: ");
		result.append(selection);
		result.append(", format: ");
		if (formatESet) result.append(format); else result.append("<unset>");
		result.append(", asArray: ");
		result.append(asArray);
		result.append(", separator: ");
		result.append(separator);
		result.append(", superTypeKey: ");
		result.append(superTypeKey);
		result.append(')');
		return result.toString();
	}

} //BaseSuperTypeConfigImpl
