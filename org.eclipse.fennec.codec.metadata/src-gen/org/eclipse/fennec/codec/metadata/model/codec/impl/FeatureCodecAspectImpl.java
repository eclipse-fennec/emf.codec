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

import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Feature Codec Aspect</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#getEffectiveKey <em>Effective Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#isIgnore <em>Ignore</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#isIgnoreRead <em>Ignore Read</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#isIgnoreWrite <em>Ignore Write</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#isForceRead <em>Force Read</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#isForceWrite <em>Force Write</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#isSerializeNull <em>Serialize Null</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#isSerializeEmpty <em>Serialize Empty</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#isSerializeDefaults <em>Serialize Defaults</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#getValueWriterName <em>Value Writer Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#getValueReaderName <em>Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#getEnumSerialization <em>Enum Serialization</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.FeatureCodecAspectImpl#getDateFormat <em>Date Format</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FeatureCodecAspectImpl extends MinimalEObjectImpl.Container implements FeatureCodecAspect {
	/**
	 * The default value of the '{@link #getEffectiveKey() <em>Effective Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEffectiveKey()
	 * @generated
	 * @ordered
	 */
	protected static final String EFFECTIVE_KEY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEffectiveKey() <em>Effective Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEffectiveKey()
	 * @generated
	 * @ordered
	 */
	protected String effectiveKey = EFFECTIVE_KEY_EDEFAULT;

	/**
	 * The default value of the '{@link #isIgnore() <em>Ignore</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnore()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IGNORE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIgnore() <em>Ignore</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnore()
	 * @generated
	 * @ordered
	 */
	protected boolean ignore = IGNORE_EDEFAULT;

	/**
	 * The default value of the '{@link #isIgnoreRead() <em>Ignore Read</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnoreRead()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IGNORE_READ_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIgnoreRead() <em>Ignore Read</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnoreRead()
	 * @generated
	 * @ordered
	 */
	protected boolean ignoreRead = IGNORE_READ_EDEFAULT;

	/**
	 * The default value of the '{@link #isIgnoreWrite() <em>Ignore Write</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnoreWrite()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IGNORE_WRITE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIgnoreWrite() <em>Ignore Write</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIgnoreWrite()
	 * @generated
	 * @ordered
	 */
	protected boolean ignoreWrite = IGNORE_WRITE_EDEFAULT;

	/**
	 * The default value of the '{@link #isForceRead() <em>Force Read</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isForceRead()
	 * @generated
	 * @ordered
	 */
	protected static final boolean FORCE_READ_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isForceRead() <em>Force Read</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isForceRead()
	 * @generated
	 * @ordered
	 */
	protected boolean forceRead = FORCE_READ_EDEFAULT;

	/**
	 * The default value of the '{@link #isForceWrite() <em>Force Write</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isForceWrite()
	 * @generated
	 * @ordered
	 */
	protected static final boolean FORCE_WRITE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isForceWrite() <em>Force Write</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isForceWrite()
	 * @generated
	 * @ordered
	 */
	protected boolean forceWrite = FORCE_WRITE_EDEFAULT;

	/**
	 * The default value of the '{@link #isSerializeNull() <em>Serialize Null</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSerializeNull()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SERIALIZE_NULL_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSerializeNull() <em>Serialize Null</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSerializeNull()
	 * @generated
	 * @ordered
	 */
	protected boolean serializeNull = SERIALIZE_NULL_EDEFAULT;

	/**
	 * The default value of the '{@link #isSerializeEmpty() <em>Serialize Empty</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSerializeEmpty()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SERIALIZE_EMPTY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSerializeEmpty() <em>Serialize Empty</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSerializeEmpty()
	 * @generated
	 * @ordered
	 */
	protected boolean serializeEmpty = SERIALIZE_EMPTY_EDEFAULT;

	/**
	 * The default value of the '{@link #isSerializeDefaults() <em>Serialize Defaults</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSerializeDefaults()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SERIALIZE_DEFAULTS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSerializeDefaults() <em>Serialize Defaults</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSerializeDefaults()
	 * @generated
	 * @ordered
	 */
	protected boolean serializeDefaults = SERIALIZE_DEFAULTS_EDEFAULT;

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
	 * The default value of the '{@link #getEnumSerialization() <em>Enum Serialization</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEnumSerialization()
	 * @generated
	 * @ordered
	 */
	protected static final EnumSerializationStrategy ENUM_SERIALIZATION_EDEFAULT = EnumSerializationStrategy.LITERAL;

	/**
	 * The cached value of the '{@link #getEnumSerialization() <em>Enum Serialization</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEnumSerialization()
	 * @generated
	 * @ordered
	 */
	protected EnumSerializationStrategy enumSerialization = ENUM_SERIALIZATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDateFormat() <em>Date Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDateFormat()
	 * @generated
	 * @ordered
	 */
	protected static final String DATE_FORMAT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDateFormat() <em>Date Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDateFormat()
	 * @generated
	 * @ordered
	 */
	protected String dateFormat = DATE_FORMAT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FeatureCodecAspectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.FEATURE_CODEC_ASPECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getEffectiveKey() {
		return effectiveKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEffectiveKey(String newEffectiveKey) {
		String oldEffectiveKey = effectiveKey;
		effectiveKey = newEffectiveKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__EFFECTIVE_KEY, oldEffectiveKey, effectiveKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIgnore() {
		return ignore;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIgnore(boolean newIgnore) {
		boolean oldIgnore = ignore;
		ignore = newIgnore;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__IGNORE, oldIgnore, ignore));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIgnoreRead() {
		return ignoreRead;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIgnoreRead(boolean newIgnoreRead) {
		boolean oldIgnoreRead = ignoreRead;
		ignoreRead = newIgnoreRead;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_READ, oldIgnoreRead, ignoreRead));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIgnoreWrite() {
		return ignoreWrite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIgnoreWrite(boolean newIgnoreWrite) {
		boolean oldIgnoreWrite = ignoreWrite;
		ignoreWrite = newIgnoreWrite;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_WRITE, oldIgnoreWrite, ignoreWrite));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isForceRead() {
		return forceRead;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setForceRead(boolean newForceRead) {
		boolean oldForceRead = forceRead;
		forceRead = newForceRead;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__FORCE_READ, oldForceRead, forceRead));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isForceWrite() {
		return forceWrite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setForceWrite(boolean newForceWrite) {
		boolean oldForceWrite = forceWrite;
		forceWrite = newForceWrite;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__FORCE_WRITE, oldForceWrite, forceWrite));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSerializeNull() {
		return serializeNull;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSerializeNull(boolean newSerializeNull) {
		boolean oldSerializeNull = serializeNull;
		serializeNull = newSerializeNull;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_NULL, oldSerializeNull, serializeNull));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSerializeEmpty() {
		return serializeEmpty;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSerializeEmpty(boolean newSerializeEmpty) {
		boolean oldSerializeEmpty = serializeEmpty;
		serializeEmpty = newSerializeEmpty;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY, oldSerializeEmpty, serializeEmpty));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSerializeDefaults() {
		return serializeDefaults;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSerializeDefaults(boolean newSerializeDefaults) {
		boolean oldSerializeDefaults = serializeDefaults;
		serializeDefaults = newSerializeDefaults;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS, oldSerializeDefaults, serializeDefaults));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME, oldValueWriterName, valueWriterName));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__VALUE_READER_NAME, oldValueReaderName, valueReaderName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EnumSerializationStrategy getEnumSerialization() {
		return enumSerialization;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEnumSerialization(EnumSerializationStrategy newEnumSerialization) {
		EnumSerializationStrategy oldEnumSerialization = enumSerialization;
		enumSerialization = newEnumSerialization == null ? ENUM_SERIALIZATION_EDEFAULT : newEnumSerialization;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION, oldEnumSerialization, enumSerialization));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDateFormat(String newDateFormat) {
		String oldDateFormat = dateFormat;
		dateFormat = newDateFormat;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__DATE_FORMAT, oldDateFormat, dateFormat));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecPackage.FEATURE_CODEC_ASPECT__EFFECTIVE_KEY:
				return getEffectiveKey();
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE:
				return isIgnore();
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_READ:
				return isIgnoreRead();
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_WRITE:
				return isIgnoreWrite();
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_READ:
				return isForceRead();
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_WRITE:
				return isForceWrite();
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_NULL:
				return isSerializeNull();
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY:
				return isSerializeEmpty();
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS:
				return isSerializeDefaults();
			case CodecPackage.FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME:
				return getValueWriterName();
			case CodecPackage.FEATURE_CODEC_ASPECT__VALUE_READER_NAME:
				return getValueReaderName();
			case CodecPackage.FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION:
				return getEnumSerialization();
			case CodecPackage.FEATURE_CODEC_ASPECT__DATE_FORMAT:
				return getDateFormat();
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
			case CodecPackage.FEATURE_CODEC_ASPECT__EFFECTIVE_KEY:
				setEffectiveKey((String)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE:
				setIgnore((Boolean)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_READ:
				setIgnoreRead((Boolean)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_WRITE:
				setIgnoreWrite((Boolean)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_READ:
				setForceRead((Boolean)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_WRITE:
				setForceWrite((Boolean)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_NULL:
				setSerializeNull((Boolean)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY:
				setSerializeEmpty((Boolean)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS:
				setSerializeDefaults((Boolean)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME:
				setValueWriterName((String)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__VALUE_READER_NAME:
				setValueReaderName((String)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION:
				setEnumSerialization((EnumSerializationStrategy)newValue);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__DATE_FORMAT:
				setDateFormat((String)newValue);
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
			case CodecPackage.FEATURE_CODEC_ASPECT__EFFECTIVE_KEY:
				setEffectiveKey(EFFECTIVE_KEY_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE:
				setIgnore(IGNORE_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_READ:
				setIgnoreRead(IGNORE_READ_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_WRITE:
				setIgnoreWrite(IGNORE_WRITE_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_READ:
				setForceRead(FORCE_READ_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_WRITE:
				setForceWrite(FORCE_WRITE_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_NULL:
				setSerializeNull(SERIALIZE_NULL_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY:
				setSerializeEmpty(SERIALIZE_EMPTY_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS:
				setSerializeDefaults(SERIALIZE_DEFAULTS_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME:
				setValueWriterName(VALUE_WRITER_NAME_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__VALUE_READER_NAME:
				setValueReaderName(VALUE_READER_NAME_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION:
				setEnumSerialization(ENUM_SERIALIZATION_EDEFAULT);
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__DATE_FORMAT:
				setDateFormat(DATE_FORMAT_EDEFAULT);
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
			case CodecPackage.FEATURE_CODEC_ASPECT__EFFECTIVE_KEY:
				return EFFECTIVE_KEY_EDEFAULT == null ? effectiveKey != null : !EFFECTIVE_KEY_EDEFAULT.equals(effectiveKey);
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE:
				return ignore != IGNORE_EDEFAULT;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_READ:
				return ignoreRead != IGNORE_READ_EDEFAULT;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_WRITE:
				return ignoreWrite != IGNORE_WRITE_EDEFAULT;
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_READ:
				return forceRead != FORCE_READ_EDEFAULT;
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_WRITE:
				return forceWrite != FORCE_WRITE_EDEFAULT;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_NULL:
				return serializeNull != SERIALIZE_NULL_EDEFAULT;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY:
				return serializeEmpty != SERIALIZE_EMPTY_EDEFAULT;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS:
				return serializeDefaults != SERIALIZE_DEFAULTS_EDEFAULT;
			case CodecPackage.FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME:
				return VALUE_WRITER_NAME_EDEFAULT == null ? valueWriterName != null : !VALUE_WRITER_NAME_EDEFAULT.equals(valueWriterName);
			case CodecPackage.FEATURE_CODEC_ASPECT__VALUE_READER_NAME:
				return VALUE_READER_NAME_EDEFAULT == null ? valueReaderName != null : !VALUE_READER_NAME_EDEFAULT.equals(valueReaderName);
			case CodecPackage.FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION:
				return enumSerialization != ENUM_SERIALIZATION_EDEFAULT;
			case CodecPackage.FEATURE_CODEC_ASPECT__DATE_FORMAT:
				return DATE_FORMAT_EDEFAULT == null ? dateFormat != null : !DATE_FORMAT_EDEFAULT.equals(dateFormat);
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
		result.append(" (effectiveKey: ");
		result.append(effectiveKey);
		result.append(", ignore: ");
		result.append(ignore);
		result.append(", ignoreRead: ");
		result.append(ignoreRead);
		result.append(", ignoreWrite: ");
		result.append(ignoreWrite);
		result.append(", forceRead: ");
		result.append(forceRead);
		result.append(", forceWrite: ");
		result.append(forceWrite);
		result.append(", serializeNull: ");
		result.append(serializeNull);
		result.append(", serializeEmpty: ");
		result.append(serializeEmpty);
		result.append(", serializeDefaults: ");
		result.append(serializeDefaults);
		result.append(", valueWriterName: ");
		result.append(valueWriterName);
		result.append(", valueReaderName: ");
		result.append(valueReaderName);
		result.append(", enumSerialization: ");
		result.append(enumSerialization);
		result.append(", dateFormat: ");
		result.append(dateFormat);
		result.append(')');
		return result.toString();
	}

} //FeatureCodecAspectImpl
