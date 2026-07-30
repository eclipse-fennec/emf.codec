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
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class Codec Aspect</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl#getTypeConfig <em>Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl#getIdConfig <em>Id Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl#getSuperTypeConfig <em>Super Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl#isInheritFromParent <em>Inherit From Parent</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl#getDiscriminatorValue <em>Discriminator Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl#isStrictOnUnknown <em>Strict On Unknown</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl#isStrictOnMissing <em>Strict On Missing</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl#isMetadataMerge <em>Metadata Merge</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.impl.ClassCodecAspectImpl#getMetadataKey <em>Metadata Key</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ClassCodecAspectImpl extends MinimalEObjectImpl.Container implements ClassCodecAspect {
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
	 * The cached value of the '{@link #getIdConfig() <em>Id Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdConfig()
	 * @generated
	 * @ordered
	 */
	protected IdSerializationConfig idConfig;

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
	 * The default value of the '{@link #isInheritFromParent() <em>Inherit From Parent</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isInheritFromParent()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INHERIT_FROM_PARENT_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isInheritFromParent() <em>Inherit From Parent</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isInheritFromParent()
	 * @generated
	 * @ordered
	 */
	protected boolean inheritFromParent = INHERIT_FROM_PARENT_EDEFAULT;

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
	protected ClassCodecAspectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.CLASS_CODEC_ASPECT;
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__TYPE_CONFIG, oldTypeConfig, newTypeConfig);
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
				msgs = ((InternalEObject)typeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CLASS_CODEC_ASPECT__TYPE_CONFIG, null, msgs);
			if (newTypeConfig != null)
				msgs = ((InternalEObject)newTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CLASS_CODEC_ASPECT__TYPE_CONFIG, null, msgs);
			msgs = basicSetTypeConfig(newTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__TYPE_CONFIG, newTypeConfig, newTypeConfig));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__ID_CONFIG, oldIdConfig, newIdConfig);
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
				msgs = ((InternalEObject)idConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CLASS_CODEC_ASPECT__ID_CONFIG, null, msgs);
			if (newIdConfig != null)
				msgs = ((InternalEObject)newIdConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CLASS_CODEC_ASPECT__ID_CONFIG, null, msgs);
			msgs = basicSetIdConfig(newIdConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__ID_CONFIG, newIdConfig, newIdConfig));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG, oldSuperTypeConfig, newSuperTypeConfig);
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
				msgs = ((InternalEObject)superTypeConfig).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG, null, msgs);
			if (newSuperTypeConfig != null)
				msgs = ((InternalEObject)newSuperTypeConfig).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CodecPackage.CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG, null, msgs);
			msgs = basicSetSuperTypeConfig(newSuperTypeConfig, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG, newSuperTypeConfig, newSuperTypeConfig));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isInheritFromParent() {
		return inheritFromParent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInheritFromParent(boolean newInheritFromParent) {
		boolean oldInheritFromParent = inheritFromParent;
		inheritFromParent = newInheritFromParent;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__INHERIT_FROM_PARENT, oldInheritFromParent, inheritFromParent));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__DISCRIMINATOR_VALUE, oldDiscriminatorValue, discriminatorValue));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_UNKNOWN, oldStrictOnUnknown, strictOnUnknown));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_MISSING, oldStrictOnMissing, strictOnMissing));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__METADATA_MERGE, oldMetadataMerge, metadataMerge));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CodecPackage.CLASS_CODEC_ASPECT__METADATA_KEY, oldMetadataKey, metadataKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodecPackage.CLASS_CODEC_ASPECT__TYPE_CONFIG:
				return basicSetTypeConfig(null, msgs);
			case CodecPackage.CLASS_CODEC_ASPECT__ID_CONFIG:
				return basicSetIdConfig(null, msgs);
			case CodecPackage.CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG:
				return basicSetSuperTypeConfig(null, msgs);
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
			case CodecPackage.CLASS_CODEC_ASPECT__TYPE_CONFIG:
				return getTypeConfig();
			case CodecPackage.CLASS_CODEC_ASPECT__ID_CONFIG:
				return getIdConfig();
			case CodecPackage.CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG:
				return getSuperTypeConfig();
			case CodecPackage.CLASS_CODEC_ASPECT__INHERIT_FROM_PARENT:
				return isInheritFromParent();
			case CodecPackage.CLASS_CODEC_ASPECT__DISCRIMINATOR_VALUE:
				return getDiscriminatorValue();
			case CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_UNKNOWN:
				return isStrictOnUnknown();
			case CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_MISSING:
				return isStrictOnMissing();
			case CodecPackage.CLASS_CODEC_ASPECT__METADATA_MERGE:
				return isMetadataMerge();
			case CodecPackage.CLASS_CODEC_ASPECT__METADATA_KEY:
				return getMetadataKey();
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
			case CodecPackage.CLASS_CODEC_ASPECT__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)newValue);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__ID_CONFIG:
				setIdConfig((IdSerializationConfig)newValue);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG:
				setSuperTypeConfig((SuperTypeSerializationConfig)newValue);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__INHERIT_FROM_PARENT:
				setInheritFromParent((Boolean)newValue);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__DISCRIMINATOR_VALUE:
				setDiscriminatorValue((String)newValue);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_UNKNOWN:
				setStrictOnUnknown((Boolean)newValue);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_MISSING:
				setStrictOnMissing((Boolean)newValue);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__METADATA_MERGE:
				setMetadataMerge((Boolean)newValue);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__METADATA_KEY:
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
			case CodecPackage.CLASS_CODEC_ASPECT__TYPE_CONFIG:
				setTypeConfig((TypeSerializationConfig)null);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__ID_CONFIG:
				setIdConfig((IdSerializationConfig)null);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG:
				setSuperTypeConfig((SuperTypeSerializationConfig)null);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__INHERIT_FROM_PARENT:
				setInheritFromParent(INHERIT_FROM_PARENT_EDEFAULT);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__DISCRIMINATOR_VALUE:
				setDiscriminatorValue(DISCRIMINATOR_VALUE_EDEFAULT);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_UNKNOWN:
				setStrictOnUnknown(STRICT_ON_UNKNOWN_EDEFAULT);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_MISSING:
				setStrictOnMissing(STRICT_ON_MISSING_EDEFAULT);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__METADATA_MERGE:
				setMetadataMerge(METADATA_MERGE_EDEFAULT);
				return;
			case CodecPackage.CLASS_CODEC_ASPECT__METADATA_KEY:
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
			case CodecPackage.CLASS_CODEC_ASPECT__TYPE_CONFIG:
				return typeConfig != null;
			case CodecPackage.CLASS_CODEC_ASPECT__ID_CONFIG:
				return idConfig != null;
			case CodecPackage.CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG:
				return superTypeConfig != null;
			case CodecPackage.CLASS_CODEC_ASPECT__INHERIT_FROM_PARENT:
				return inheritFromParent != INHERIT_FROM_PARENT_EDEFAULT;
			case CodecPackage.CLASS_CODEC_ASPECT__DISCRIMINATOR_VALUE:
				return DISCRIMINATOR_VALUE_EDEFAULT == null ? discriminatorValue != null : !DISCRIMINATOR_VALUE_EDEFAULT.equals(discriminatorValue);
			case CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_UNKNOWN:
				return strictOnUnknown != STRICT_ON_UNKNOWN_EDEFAULT;
			case CodecPackage.CLASS_CODEC_ASPECT__STRICT_ON_MISSING:
				return strictOnMissing != STRICT_ON_MISSING_EDEFAULT;
			case CodecPackage.CLASS_CODEC_ASPECT__METADATA_MERGE:
				return metadataMerge != METADATA_MERGE_EDEFAULT;
			case CodecPackage.CLASS_CODEC_ASPECT__METADATA_KEY:
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
		result.append(" (inheritFromParent: ");
		result.append(inheritFromParent);
		result.append(", discriminatorValue: ");
		result.append(discriminatorValue);
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

} //ClassCodecAspectImpl
