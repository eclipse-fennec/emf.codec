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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.metadata.model.codec.CodecConfig;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;

import org.eclipse.fennec.model.metadata.SerializationFormat;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#isUseNumericIds <em>Use Numeric Ids</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getTypeConfig <em>Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getContainmentTypeConfig <em>Containment Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getReferenceTypeConfig <em>Reference Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getIdConfig <em>Id Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getReferenceConfig <em>Reference Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getSuperTypeConfig <em>Super Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getFeatureConfigs <em>Feature Configs</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#isExpand <em>Expand</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getExpandDepth <em>Expand Depth</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#isExpandIgnoreBidirectional <em>Expand Ignore Bidirectional</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#isSerializeNull <em>Serialize Null</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#isSerializeEmpty <em>Serialize Empty</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#isSerializeDefaults <em>Serialize Defaults</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getTypeHintMode <em>Type Hint Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getDeserializationMode <em>Deserialization Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#isStrictOnUnknown <em>Strict On Unknown</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#isStrictOnMissing <em>Strict On Missing</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#isMetadataMerge <em>Metadata Merge</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.CodecConfigImpl#getMetadataKey <em>Metadata Key</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CodecConfigImpl extends MinimalEObjectImpl.Container implements CodecConfig {
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
	 * The default value of the '{@link #isUseNumericIds() <em>Use Numeric Ids</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUseNumericIds()
	 * @generated
	 * @ordered
	 */
	protected static final boolean USE_NUMERIC_IDS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isUseNumericIds() <em>Use Numeric Ids</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUseNumericIds()
	 * @generated
	 * @ordered
	 */
	protected boolean useNumericIds = USE_NUMERIC_IDS_EDEFAULT;

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
	 * The cached value of the '{@link #getContainmentTypeConfig() <em>Containment Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContainmentTypeConfig()
	 * @generated
	 * @ordered
	 */
	protected TypeSerializationConfig containmentTypeConfig;

	/**
	 * The cached value of the '{@link #getReferenceTypeConfig() <em>Reference Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferenceTypeConfig()
	 * @generated
	 * @ordered
	 */
	protected TypeSerializationConfig referenceTypeConfig;

	/**
	 * The cached value of the '{@link #getIdConfig() <em>Id Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdConfig()
	 * @generated
	 * @ordered
	 */
	protected IdSerializationConfig idConfig;

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
	 * The cached value of the '{@link #getSuperTypeConfig() <em>Super Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperTypeConfig()
	 * @generated
	 * @ordered
	 */
	protected SuperTypeSerializationConfig superTypeConfig;

	/**
	 * The cached value of the '{@link #getFeatureConfigs() <em>Feature Configs</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatureConfigs()
	 * @generated
	 * @ordered
	 */
	protected EList<FeatureSerializationConfig> featureConfigs;

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
	 * The default value of the '{@link #getExpandDepth() <em>Expand Depth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpandDepth()
	 * @generated
	 * @ordered
	 */
	protected static final int EXPAND_DEPTH_EDEFAULT = 1;

	/**
	 * The cached value of the '{@link #getExpandDepth() <em>Expand Depth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpandDepth()
	 * @generated
	 * @ordered
	 */
	protected int expandDepth = EXPAND_DEPTH_EDEFAULT;

	/**
	 * The default value of the '{@link #isExpandIgnoreBidirectional() <em>Expand Ignore Bidirectional</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExpandIgnoreBidirectional()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EXPAND_IGNORE_BIDIRECTIONAL_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isExpandIgnoreBidirectional() <em>Expand Ignore Bidirectional</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExpandIgnoreBidirectional()
	 * @generated
	 * @ordered
	 */
	protected boolean expandIgnoreBidirectional = EXPAND_IGNORE_BIDIRECTIONAL_EDEFAULT;

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
	 * The default value of the '{@link #getTypeHintMode() <em>Type Hint Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeHintMode()
	 * @generated
	 * @ordered
	 */
	protected static final TypeHintMode TYPE_HINT_MODE_EDEFAULT = TypeHintMode.HINT;

	/**
	 * The cached value of the '{@link #getTypeHintMode() <em>Type Hint Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeHintMode()
	 * @generated
	 * @ordered
	 */
	protected TypeHintMode typeHintMode = TYPE_HINT_MODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDeserializationMode() <em>Deserialization Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeserializationMode()
	 * @generated
	 * @ordered
	 */
	protected static final DeserializationMode DESERIALIZATION_MODE_EDEFAULT = DeserializationMode.LENIENT;

	/**
	 * The cached value of the '{@link #getDeserializationMode() <em>Deserialization Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeserializationMode()
	 * @generated
	 * @ordered
	 */
	protected DeserializationMode deserializationMode = DESERIALIZATION_MODE_EDEFAULT;

	/**
	 * The default value of the '{@link #isStrictOnUnknown() <em>Strict On Unknown</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStrictOnUnknown()
	 * @generated
	 * @ordered
	 */
	protected static final boolean STRICT_ON_UNKNOWN_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isStrictOnUnknown() <em>Strict On Unknown</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStrictOnUnknown()
	 * @generated
	 * @ordered
	 */
	protected boolean strictOnUnknown = STRICT_ON_UNKNOWN_EDEFAULT;

	/**
	 * The default value of the '{@link #isStrictOnMissing() <em>Strict On Missing</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStrictOnMissing()
	 * @generated
	 * @ordered
	 */
	protected static final boolean STRICT_ON_MISSING_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isStrictOnMissing() <em>Strict On Missing</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStrictOnMissing()
	 * @generated
	 * @ordered
	 */
	protected boolean strictOnMissing = STRICT_ON_MISSING_EDEFAULT;

	/**
	 * The default value of the '{@link #isMetadataMerge() <em>Metadata Merge</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMetadataMerge()
	 * @generated
	 * @ordered
	 */
	protected static final boolean METADATA_MERGE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isMetadataMerge() <em>Metadata Merge</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMetadataMerge()
	 * @generated
	 * @ordered
	 */
	protected boolean metadataMerge = METADATA_MERGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getMetadataKey() <em>Metadata Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMetadataKey()
	 * @generated
	 * @ordered
	 */
	protected static final String METADATA_KEY_EDEFAULT = "_metadata";

	/**
	 * The cached value of the '{@link #getMetadataKey() <em>Metadata Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMetadataKey()
	 * @generated
	 * @ordered
	 */
	protected String metadataKey = METADATA_KEY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CodecConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.CODEC_CONFIG;
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__FORMAT, oldFormat, format));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isUseNumericIds() {
		return useNumericIds;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUseNumericIds(boolean newUseNumericIds) {
		boolean oldUseNumericIds = useNumericIds;
		useNumericIds = newUseNumericIds;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__USE_NUMERIC_IDS, oldUseNumericIds, useNumericIds));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__TYPE_CONFIG, oldTypeConfig, newTypeConfig);
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
				msgs = ((InternalEObject)typeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__TYPE_CONFIG, null, msgs);
			if (newTypeConfig != null)
				msgs = ((InternalEObject)newTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__TYPE_CONFIG, null, msgs);
			msgs = basicSetTypeConfig(newTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__TYPE_CONFIG, newTypeConfig, newTypeConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TypeSerializationConfig getContainmentTypeConfig() {
		return containmentTypeConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetContainmentTypeConfig(TypeSerializationConfig newContainmentTypeConfig, NotificationChain msgs) {
		TypeSerializationConfig oldContainmentTypeConfig = containmentTypeConfig;
		containmentTypeConfig = newContainmentTypeConfig;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG, oldContainmentTypeConfig, newContainmentTypeConfig);
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
	public void setContainmentTypeConfig(TypeSerializationConfig newContainmentTypeConfig) {
		if (newContainmentTypeConfig != containmentTypeConfig) {
			NotificationChain msgs = null;
			if (containmentTypeConfig != null)
				msgs = ((InternalEObject)containmentTypeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG, null, msgs);
			if (newContainmentTypeConfig != null)
				msgs = ((InternalEObject)newContainmentTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG, null, msgs);
			msgs = basicSetContainmentTypeConfig(newContainmentTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG, newContainmentTypeConfig, newContainmentTypeConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TypeSerializationConfig getReferenceTypeConfig() {
		return referenceTypeConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetReferenceTypeConfig(TypeSerializationConfig newReferenceTypeConfig, NotificationChain msgs) {
		TypeSerializationConfig oldReferenceTypeConfig = referenceTypeConfig;
		referenceTypeConfig = newReferenceTypeConfig;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__REFERENCE_TYPE_CONFIG, oldReferenceTypeConfig, newReferenceTypeConfig);
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
	public void setReferenceTypeConfig(TypeSerializationConfig newReferenceTypeConfig) {
		if (newReferenceTypeConfig != referenceTypeConfig) {
			NotificationChain msgs = null;
			if (referenceTypeConfig != null)
				msgs = ((InternalEObject)referenceTypeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__REFERENCE_TYPE_CONFIG, null, msgs);
			if (newReferenceTypeConfig != null)
				msgs = ((InternalEObject)newReferenceTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__REFERENCE_TYPE_CONFIG, null, msgs);
			msgs = basicSetReferenceTypeConfig(newReferenceTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__REFERENCE_TYPE_CONFIG, newReferenceTypeConfig, newReferenceTypeConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IdSerializationConfig getIdConfig() {
		return idConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIdConfig(IdSerializationConfig newIdConfig, NotificationChain msgs) {
		IdSerializationConfig oldIdConfig = idConfig;
		idConfig = newIdConfig;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__ID_CONFIG, oldIdConfig, newIdConfig);
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
	public void setIdConfig(IdSerializationConfig newIdConfig) {
		if (newIdConfig != idConfig) {
			NotificationChain msgs = null;
			if (idConfig != null)
				msgs = ((InternalEObject)idConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__ID_CONFIG, null, msgs);
			if (newIdConfig != null)
				msgs = ((InternalEObject)newIdConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__ID_CONFIG, null, msgs);
			msgs = basicSetIdConfig(newIdConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__ID_CONFIG, newIdConfig, newIdConfig));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__REFERENCE_CONFIG, oldReferenceConfig, newReferenceConfig);
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
				msgs = ((InternalEObject)referenceConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__REFERENCE_CONFIG, null, msgs);
			if (newReferenceConfig != null)
				msgs = ((InternalEObject)newReferenceConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__REFERENCE_CONFIG, null, msgs);
			msgs = basicSetReferenceConfig(newReferenceConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__REFERENCE_CONFIG, newReferenceConfig, newReferenceConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SuperTypeSerializationConfig getSuperTypeConfig() {
		return superTypeConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSuperTypeConfig(SuperTypeSerializationConfig newSuperTypeConfig, NotificationChain msgs) {
		SuperTypeSerializationConfig oldSuperTypeConfig = superTypeConfig;
		superTypeConfig = newSuperTypeConfig;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__SUPER_TYPE_CONFIG, oldSuperTypeConfig, newSuperTypeConfig);
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
	public void setSuperTypeConfig(SuperTypeSerializationConfig newSuperTypeConfig) {
		if (newSuperTypeConfig != superTypeConfig) {
			NotificationChain msgs = null;
			if (superTypeConfig != null)
				msgs = ((InternalEObject)superTypeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__SUPER_TYPE_CONFIG, null, msgs);
			if (newSuperTypeConfig != null)
				msgs = ((InternalEObject)newSuperTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CODEC_CONFIG__SUPER_TYPE_CONFIG, null, msgs);
			msgs = basicSetSuperTypeConfig(newSuperTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__SUPER_TYPE_CONFIG, newSuperTypeConfig, newSuperTypeConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<FeatureSerializationConfig> getFeatureConfigs() {
		if (featureConfigs == null) {
			featureConfigs = new EObjectContainmentEList<FeatureSerializationConfig>(FeatureSerializationConfig.class, this, CodecPackage.CODEC_CONFIG__FEATURE_CONFIGS);
		}
		return featureConfigs;
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__EXPAND, oldExpand, expand));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getExpandDepth() {
		return expandDepth;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpandDepth(int newExpandDepth) {
		int oldExpandDepth = expandDepth;
		expandDepth = newExpandDepth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__EXPAND_DEPTH, oldExpandDepth, expandDepth));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isExpandIgnoreBidirectional() {
		return expandIgnoreBidirectional;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpandIgnoreBidirectional(boolean newExpandIgnoreBidirectional) {
		boolean oldExpandIgnoreBidirectional = expandIgnoreBidirectional;
		expandIgnoreBidirectional = newExpandIgnoreBidirectional;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__EXPAND_IGNORE_BIDIRECTIONAL, oldExpandIgnoreBidirectional, expandIgnoreBidirectional));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__SERIALIZE_NULL, oldSerializeNull, serializeNull));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__SERIALIZE_EMPTY, oldSerializeEmpty, serializeEmpty));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__SERIALIZE_DEFAULTS, oldSerializeDefaults, serializeDefaults));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TypeHintMode getTypeHintMode() {
		return typeHintMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTypeHintMode(TypeHintMode newTypeHintMode) {
		TypeHintMode oldTypeHintMode = typeHintMode;
		typeHintMode = newTypeHintMode == null ? TYPE_HINT_MODE_EDEFAULT : newTypeHintMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__TYPE_HINT_MODE, oldTypeHintMode, typeHintMode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DeserializationMode getDeserializationMode() {
		return deserializationMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeserializationMode(DeserializationMode newDeserializationMode) {
		DeserializationMode oldDeserializationMode = deserializationMode;
		deserializationMode = newDeserializationMode == null ? DESERIALIZATION_MODE_EDEFAULT : newDeserializationMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__DESERIALIZATION_MODE, oldDeserializationMode, deserializationMode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isStrictOnUnknown() {
		return strictOnUnknown;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStrictOnUnknown(boolean newStrictOnUnknown) {
		boolean oldStrictOnUnknown = strictOnUnknown;
		strictOnUnknown = newStrictOnUnknown;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__STRICT_ON_UNKNOWN, oldStrictOnUnknown, strictOnUnknown));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isStrictOnMissing() {
		return strictOnMissing;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStrictOnMissing(boolean newStrictOnMissing) {
		boolean oldStrictOnMissing = strictOnMissing;
		strictOnMissing = newStrictOnMissing;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__STRICT_ON_MISSING, oldStrictOnMissing, strictOnMissing));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isMetadataMerge() {
		return metadataMerge;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMetadataMerge(boolean newMetadataMerge) {
		boolean oldMetadataMerge = metadataMerge;
		metadataMerge = newMetadataMerge;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__METADATA_MERGE, oldMetadataMerge, metadataMerge));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMetadataKey() {
		return metadataKey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMetadataKey(String newMetadataKey) {
		String oldMetadataKey = metadataKey;
		metadataKey = newMetadataKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CODEC_CONFIG__METADATA_KEY, oldMetadataKey, metadataKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecPackage.CODEC_CONFIG__TYPE_CONFIG:
				return basicSetTypeConfig(null, msgs);
			case CodecPackage.CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG:
				return basicSetContainmentTypeConfig(null, msgs);
			case CodecPackage.CODEC_CONFIG__REFERENCE_TYPE_CONFIG:
				return basicSetReferenceTypeConfig(null, msgs);
			case CodecPackage.CODEC_CONFIG__ID_CONFIG:
				return basicSetIdConfig(null, msgs);
			case CodecPackage.CODEC_CONFIG__REFERENCE_CONFIG:
				return basicSetReferenceConfig(null, msgs);
			case CodecPackage.CODEC_CONFIG__SUPER_TYPE_CONFIG:
				return basicSetSuperTypeConfig(null, msgs);
			case CodecPackage.CODEC_CONFIG__FEATURE_CONFIGS:
				return ((InternalEList<?>)getFeatureConfigs()).basicRemove(otherEnd, msgs);
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
			case CodecPackage.CODEC_CONFIG__FORMAT:
				return getFormat();
			case CodecPackage.CODEC_CONFIG__USE_NUMERIC_IDS:
				return isUseNumericIds();
			case CodecPackage.CODEC_CONFIG__TYPE_CONFIG:
				return getTypeConfig();
			case CodecPackage.CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG:
				return getContainmentTypeConfig();
			case CodecPackage.CODEC_CONFIG__REFERENCE_TYPE_CONFIG:
				return getReferenceTypeConfig();
			case CodecPackage.CODEC_CONFIG__ID_CONFIG:
				return getIdConfig();
			case CodecPackage.CODEC_CONFIG__REFERENCE_CONFIG:
				return getReferenceConfig();
			case CodecPackage.CODEC_CONFIG__SUPER_TYPE_CONFIG:
				return getSuperTypeConfig();
			case CodecPackage.CODEC_CONFIG__FEATURE_CONFIGS:
				return getFeatureConfigs();
			case CodecPackage.CODEC_CONFIG__EXPAND:
				return isExpand();
			case CodecPackage.CODEC_CONFIG__EXPAND_DEPTH:
				return getExpandDepth();
			case CodecPackage.CODEC_CONFIG__EXPAND_IGNORE_BIDIRECTIONAL:
				return isExpandIgnoreBidirectional();
			case CodecPackage.CODEC_CONFIG__SERIALIZE_NULL:
				return isSerializeNull();
			case CodecPackage.CODEC_CONFIG__SERIALIZE_EMPTY:
				return isSerializeEmpty();
			case CodecPackage.CODEC_CONFIG__SERIALIZE_DEFAULTS:
				return isSerializeDefaults();
			case CodecPackage.CODEC_CONFIG__TYPE_HINT_MODE:
				return getTypeHintMode();
			case CodecPackage.CODEC_CONFIG__DESERIALIZATION_MODE:
				return getDeserializationMode();
			case CodecPackage.CODEC_CONFIG__STRICT_ON_UNKNOWN:
				return isStrictOnUnknown();
			case CodecPackage.CODEC_CONFIG__STRICT_ON_MISSING:
				return isStrictOnMissing();
			case CodecPackage.CODEC_CONFIG__METADATA_MERGE:
				return isMetadataMerge();
			case CodecPackage.CODEC_CONFIG__METADATA_KEY:
				return getMetadataKey();
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
			case CodecPackage.CODEC_CONFIG__FORMAT:
				setFormat((SerializationFormat)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__USE_NUMERIC_IDS:
				setUseNumericIds((Boolean)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG:
				setContainmentTypeConfig((TypeSerializationConfig)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__REFERENCE_TYPE_CONFIG:
				setReferenceTypeConfig((TypeSerializationConfig)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__ID_CONFIG:
				setIdConfig((IdSerializationConfig)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__REFERENCE_CONFIG:
				setReferenceConfig((ReferenceSerializationConfig)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__SUPER_TYPE_CONFIG:
				setSuperTypeConfig((SuperTypeSerializationConfig)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__FEATURE_CONFIGS:
				getFeatureConfigs().clear();
				getFeatureConfigs().addAll((Collection<? extends FeatureSerializationConfig>)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__EXPAND:
				setExpand((Boolean)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__EXPAND_DEPTH:
				setExpandDepth((Integer)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__EXPAND_IGNORE_BIDIRECTIONAL:
				setExpandIgnoreBidirectional((Boolean)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__SERIALIZE_NULL:
				setSerializeNull((Boolean)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__SERIALIZE_EMPTY:
				setSerializeEmpty((Boolean)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__SERIALIZE_DEFAULTS:
				setSerializeDefaults((Boolean)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__TYPE_HINT_MODE:
				setTypeHintMode((TypeHintMode)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__DESERIALIZATION_MODE:
				setDeserializationMode((DeserializationMode)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__STRICT_ON_UNKNOWN:
				setStrictOnUnknown((Boolean)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__STRICT_ON_MISSING:
				setStrictOnMissing((Boolean)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__METADATA_MERGE:
				setMetadataMerge((Boolean)newValue);
				return;
			case CodecPackage.CODEC_CONFIG__METADATA_KEY:
				setMetadataKey((String)newValue);
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
			case CodecPackage.CODEC_CONFIG__FORMAT:
				setFormat(FORMAT_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__USE_NUMERIC_IDS:
				setUseNumericIds(USE_NUMERIC_IDS_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)null);
				return;
			case CodecPackage.CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG:
				setContainmentTypeConfig((TypeSerializationConfig)null);
				return;
			case CodecPackage.CODEC_CONFIG__REFERENCE_TYPE_CONFIG:
				setReferenceTypeConfig((TypeSerializationConfig)null);
				return;
			case CodecPackage.CODEC_CONFIG__ID_CONFIG:
				setIdConfig((IdSerializationConfig)null);
				return;
			case CodecPackage.CODEC_CONFIG__REFERENCE_CONFIG:
				setReferenceConfig((ReferenceSerializationConfig)null);
				return;
			case CodecPackage.CODEC_CONFIG__SUPER_TYPE_CONFIG:
				setSuperTypeConfig((SuperTypeSerializationConfig)null);
				return;
			case CodecPackage.CODEC_CONFIG__FEATURE_CONFIGS:
				getFeatureConfigs().clear();
				return;
			case CodecPackage.CODEC_CONFIG__EXPAND:
				setExpand(EXPAND_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__EXPAND_DEPTH:
				setExpandDepth(EXPAND_DEPTH_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__EXPAND_IGNORE_BIDIRECTIONAL:
				setExpandIgnoreBidirectional(EXPAND_IGNORE_BIDIRECTIONAL_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__SERIALIZE_NULL:
				setSerializeNull(SERIALIZE_NULL_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__SERIALIZE_EMPTY:
				setSerializeEmpty(SERIALIZE_EMPTY_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__SERIALIZE_DEFAULTS:
				setSerializeDefaults(SERIALIZE_DEFAULTS_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__TYPE_HINT_MODE:
				setTypeHintMode(TYPE_HINT_MODE_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__DESERIALIZATION_MODE:
				setDeserializationMode(DESERIALIZATION_MODE_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__STRICT_ON_UNKNOWN:
				setStrictOnUnknown(STRICT_ON_UNKNOWN_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__STRICT_ON_MISSING:
				setStrictOnMissing(STRICT_ON_MISSING_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__METADATA_MERGE:
				setMetadataMerge(METADATA_MERGE_EDEFAULT);
				return;
			case CodecPackage.CODEC_CONFIG__METADATA_KEY:
				setMetadataKey(METADATA_KEY_EDEFAULT);
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
			case CodecPackage.CODEC_CONFIG__FORMAT:
				return format != FORMAT_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__USE_NUMERIC_IDS:
				return useNumericIds != USE_NUMERIC_IDS_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__TYPE_CONFIG:
				return typeConfig != null;
			case CodecPackage.CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG:
				return containmentTypeConfig != null;
			case CodecPackage.CODEC_CONFIG__REFERENCE_TYPE_CONFIG:
				return referenceTypeConfig != null;
			case CodecPackage.CODEC_CONFIG__ID_CONFIG:
				return idConfig != null;
			case CodecPackage.CODEC_CONFIG__REFERENCE_CONFIG:
				return referenceConfig != null;
			case CodecPackage.CODEC_CONFIG__SUPER_TYPE_CONFIG:
				return superTypeConfig != null;
			case CodecPackage.CODEC_CONFIG__FEATURE_CONFIGS:
				return featureConfigs != null && !featureConfigs.isEmpty();
			case CodecPackage.CODEC_CONFIG__EXPAND:
				return expand != EXPAND_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__EXPAND_DEPTH:
				return expandDepth != EXPAND_DEPTH_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__EXPAND_IGNORE_BIDIRECTIONAL:
				return expandIgnoreBidirectional != EXPAND_IGNORE_BIDIRECTIONAL_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__SERIALIZE_NULL:
				return serializeNull != SERIALIZE_NULL_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__SERIALIZE_EMPTY:
				return serializeEmpty != SERIALIZE_EMPTY_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__SERIALIZE_DEFAULTS:
				return serializeDefaults != SERIALIZE_DEFAULTS_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__TYPE_HINT_MODE:
				return typeHintMode != TYPE_HINT_MODE_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__DESERIALIZATION_MODE:
				return deserializationMode != DESERIALIZATION_MODE_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__STRICT_ON_UNKNOWN:
				return strictOnUnknown != STRICT_ON_UNKNOWN_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__STRICT_ON_MISSING:
				return strictOnMissing != STRICT_ON_MISSING_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__METADATA_MERGE:
				return metadataMerge != METADATA_MERGE_EDEFAULT;
			case CodecPackage.CODEC_CONFIG__METADATA_KEY:
				return METADATA_KEY_EDEFAULT == null ? metadataKey != null : !METADATA_KEY_EDEFAULT.equals(metadataKey);
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
		result.append(", useNumericIds: ");
		result.append(useNumericIds);
		result.append(", expand: ");
		result.append(expand);
		result.append(", expandDepth: ");
		result.append(expandDepth);
		result.append(", expandIgnoreBidirectional: ");
		result.append(expandIgnoreBidirectional);
		result.append(", serializeNull: ");
		result.append(serializeNull);
		result.append(", serializeEmpty: ");
		result.append(serializeEmpty);
		result.append(", serializeDefaults: ");
		result.append(serializeDefaults);
		result.append(", typeHintMode: ");
		result.append(typeHintMode);
		result.append(", deserializationMode: ");
		result.append(deserializationMode);
		result.append(", strictOnUnknown: ");
		result.append(strictOnUnknown);
		result.append(", strictOnMissing: ");
		result.append(strictOnMissing);
		result.append(", metadataMerge: ");
		result.append(metadataMerge);
		result.append(", metadataKey: ");
		result.append(metadataKey);
		result.append(')');
		return result.toString();
	}

} //CodecConfigImpl
