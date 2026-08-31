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
	 * This is true if the Ignore attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean ignoreESet;

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
	 * This is true if the Ignore Read attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean ignoreReadESet;

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
	 * This is true if the Ignore Write attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean ignoreWriteESet;

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
	 * This is true if the Force Read attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean forceReadESet;

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
	 * This is true if the Force Write attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean forceWriteESet;

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
	 * This is true if the Serialize Null attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean serializeNullESet;

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
	 * This is true if the Serialize Empty attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean serializeEmptyESet;

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
	 * This is true if the Serialize Defaults attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean serializeDefaultsESet;

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
		boolean oldIgnoreESet = ignoreESet;
		ignoreESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__IGNORE, oldIgnore, ignore, !oldIgnoreESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetIgnore() {
		boolean oldIgnore = ignore;
		boolean oldIgnoreESet = ignoreESet;
		ignore = IGNORE_EDEFAULT;
		ignoreESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.FEATURE_CODEC_ASPECT__IGNORE, oldIgnore, IGNORE_EDEFAULT, oldIgnoreESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetIgnore() {
		return ignoreESet;
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
		boolean oldIgnoreReadESet = ignoreReadESet;
		ignoreReadESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_READ, oldIgnoreRead, ignoreRead, !oldIgnoreReadESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetIgnoreRead() {
		boolean oldIgnoreRead = ignoreRead;
		boolean oldIgnoreReadESet = ignoreReadESet;
		ignoreRead = IGNORE_READ_EDEFAULT;
		ignoreReadESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_READ, oldIgnoreRead, IGNORE_READ_EDEFAULT, oldIgnoreReadESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetIgnoreRead() {
		return ignoreReadESet;
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
		boolean oldIgnoreWriteESet = ignoreWriteESet;
		ignoreWriteESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_WRITE, oldIgnoreWrite, ignoreWrite, !oldIgnoreWriteESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetIgnoreWrite() {
		boolean oldIgnoreWrite = ignoreWrite;
		boolean oldIgnoreWriteESet = ignoreWriteESet;
		ignoreWrite = IGNORE_WRITE_EDEFAULT;
		ignoreWriteESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_WRITE, oldIgnoreWrite, IGNORE_WRITE_EDEFAULT, oldIgnoreWriteESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetIgnoreWrite() {
		return ignoreWriteESet;
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
		boolean oldForceReadESet = forceReadESet;
		forceReadESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__FORCE_READ, oldForceRead, forceRead, !oldForceReadESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetForceRead() {
		boolean oldForceRead = forceRead;
		boolean oldForceReadESet = forceReadESet;
		forceRead = FORCE_READ_EDEFAULT;
		forceReadESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.FEATURE_CODEC_ASPECT__FORCE_READ, oldForceRead, FORCE_READ_EDEFAULT, oldForceReadESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetForceRead() {
		return forceReadESet;
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
		boolean oldForceWriteESet = forceWriteESet;
		forceWriteESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__FORCE_WRITE, oldForceWrite, forceWrite, !oldForceWriteESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetForceWrite() {
		boolean oldForceWrite = forceWrite;
		boolean oldForceWriteESet = forceWriteESet;
		forceWrite = FORCE_WRITE_EDEFAULT;
		forceWriteESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.FEATURE_CODEC_ASPECT__FORCE_WRITE, oldForceWrite, FORCE_WRITE_EDEFAULT, oldForceWriteESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetForceWrite() {
		return forceWriteESet;
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
		boolean oldSerializeNullESet = serializeNullESet;
		serializeNullESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_NULL, oldSerializeNull, serializeNull, !oldSerializeNullESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetSerializeNull() {
		boolean oldSerializeNull = serializeNull;
		boolean oldSerializeNullESet = serializeNullESet;
		serializeNull = SERIALIZE_NULL_EDEFAULT;
		serializeNullESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_NULL, oldSerializeNull, SERIALIZE_NULL_EDEFAULT, oldSerializeNullESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetSerializeNull() {
		return serializeNullESet;
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
		boolean oldSerializeEmptyESet = serializeEmptyESet;
		serializeEmptyESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY, oldSerializeEmpty, serializeEmpty, !oldSerializeEmptyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetSerializeEmpty() {
		boolean oldSerializeEmpty = serializeEmpty;
		boolean oldSerializeEmptyESet = serializeEmptyESet;
		serializeEmpty = SERIALIZE_EMPTY_EDEFAULT;
		serializeEmptyESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY, oldSerializeEmpty, SERIALIZE_EMPTY_EDEFAULT, oldSerializeEmptyESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetSerializeEmpty() {
		return serializeEmptyESet;
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
		boolean oldSerializeDefaultsESet = serializeDefaultsESet;
		serializeDefaultsESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS, oldSerializeDefaults, serializeDefaults, !oldSerializeDefaultsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetSerializeDefaults() {
		boolean oldSerializeDefaults = serializeDefaults;
		boolean oldSerializeDefaultsESet = serializeDefaultsESet;
		serializeDefaults = SERIALIZE_DEFAULTS_EDEFAULT;
		serializeDefaultsESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS, oldSerializeDefaults, SERIALIZE_DEFAULTS_EDEFAULT, oldSerializeDefaultsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetSerializeDefaults() {
		return serializeDefaultsESet;
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
				unsetIgnore();
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_READ:
				unsetIgnoreRead();
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_WRITE:
				unsetIgnoreWrite();
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_READ:
				unsetForceRead();
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_WRITE:
				unsetForceWrite();
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_NULL:
				unsetSerializeNull();
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY:
				unsetSerializeEmpty();
				return;
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS:
				unsetSerializeDefaults();
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
				return isSetIgnore();
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_READ:
				return isSetIgnoreRead();
			case CodecPackage.FEATURE_CODEC_ASPECT__IGNORE_WRITE:
				return isSetIgnoreWrite();
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_READ:
				return isSetForceRead();
			case CodecPackage.FEATURE_CODEC_ASPECT__FORCE_WRITE:
				return isSetForceWrite();
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_NULL:
				return isSetSerializeNull();
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY:
				return isSetSerializeEmpty();
			case CodecPackage.FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS:
				return isSetSerializeDefaults();
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
		if (ignoreESet) result.append(ignore); else result.append("<unset>");
		result.append(", ignoreRead: ");
		if (ignoreReadESet) result.append(ignoreRead); else result.append("<unset>");
		result.append(", ignoreWrite: ");
		if (ignoreWriteESet) result.append(ignoreWrite); else result.append("<unset>");
		result.append(", forceRead: ");
		if (forceReadESet) result.append(forceRead); else result.append("<unset>");
		result.append(", forceWrite: ");
		if (forceWriteESet) result.append(forceWrite); else result.append("<unset>");
		result.append(", serializeNull: ");
		if (serializeNullESet) result.append(serializeNull); else result.append("<unset>");
		result.append(", serializeEmpty: ");
		if (serializeEmptyESet) result.append(serializeEmpty); else result.append("<unset>");
		result.append(", serializeDefaults: ");
		if (serializeDefaultsESet) result.append(serializeDefaults); else result.append("<unset>");
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
