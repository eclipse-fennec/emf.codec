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
package org.eclipse.fennec.codec.jsonschema.model.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.jsonschema.model.Discriminator;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Discriminator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.DiscriminatorImpl#getPropertyName <em>Property Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.DiscriminatorImpl#getMapping <em>Mapping</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.DiscriminatorImpl#getDefaultMapping <em>Default Mapping</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.DiscriminatorImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DiscriminatorImpl extends MinimalEObjectImpl.Container implements Discriminator {
	/**
	 * The default value of the '{@link #getPropertyName() <em>Property Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyName()
	 * @generated
	 * @ordered
	 */
	protected static final String PROPERTY_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPropertyName() <em>Property Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyName()
	 * @generated
	 * @ordered
	 */
	protected String propertyName = PROPERTY_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getMapping() <em>Mapping</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMapping()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> mapping;

	/**
	 * The default value of the '{@link #getDefaultMapping() <em>Default Mapping</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultMapping()
	 * @generated
	 * @ordered
	 */
	protected static final String DEFAULT_MAPPING_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDefaultMapping() <em>Default Mapping</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultMapping()
	 * @generated
	 * @ordered
	 */
	protected String defaultMapping = DEFAULT_MAPPING_EDEFAULT;

	/**
	 * The cached value of the '{@link #getExtensions() <em>Extensions</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExtensions()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Object> extensions;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DiscriminatorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return JsonSchemaPackage.Literals.DISCRIMINATOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPropertyName(String newPropertyName) {
		String oldPropertyName = propertyName;
		propertyName = newPropertyName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.DISCRIMINATOR__PROPERTY_NAME, oldPropertyName, propertyName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, String> getMapping() {
		if (mapping == null) {
			mapping = new EcoreEMap.Unsettable<String,String>(JsonSchemaPackage.Literals.STRING_ENTRY, StringEntryImpl.class, this, JsonSchemaPackage.DISCRIMINATOR__MAPPING);
		}
		return mapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMapping() {
		if (mapping != null) ((InternalEList.Unsettable<?>)mapping).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMapping() {
		return mapping != null && ((InternalEList.Unsettable<?>)mapping).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDefaultMapping() {
		return defaultMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDefaultMapping(String newDefaultMapping) {
		String oldDefaultMapping = defaultMapping;
		defaultMapping = newDefaultMapping;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.DISCRIMINATOR__DEFAULT_MAPPING, oldDefaultMapping, defaultMapping));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap.Unsettable<String,Object>(JsonSchemaPackage.Literals.KEYWORD_ENTRY, KeywordEntryImpl.class, this, JsonSchemaPackage.DISCRIMINATOR__EXTENSIONS);
		}
		return extensions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExtensions() {
		if (extensions != null) ((InternalEList.Unsettable<?>)extensions).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExtensions() {
		return extensions != null && ((InternalEList.Unsettable<?>)extensions).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case JsonSchemaPackage.DISCRIMINATOR__MAPPING:
				return ((InternalEList<?>)getMapping()).basicRemove(otherEnd, msgs);
			case JsonSchemaPackage.DISCRIMINATOR__EXTENSIONS:
				return ((InternalEList<?>)getExtensions()).basicRemove(otherEnd, msgs);
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
			case JsonSchemaPackage.DISCRIMINATOR__PROPERTY_NAME:
				return getPropertyName();
			case JsonSchemaPackage.DISCRIMINATOR__MAPPING:
				if (coreType) return getMapping();
				else return getMapping().map();
			case JsonSchemaPackage.DISCRIMINATOR__DEFAULT_MAPPING:
				return getDefaultMapping();
			case JsonSchemaPackage.DISCRIMINATOR__EXTENSIONS:
				if (coreType) return getExtensions();
				else return getExtensions().map();
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
			case JsonSchemaPackage.DISCRIMINATOR__PROPERTY_NAME:
				setPropertyName((String)newValue);
				return;
			case JsonSchemaPackage.DISCRIMINATOR__MAPPING:
				((EStructuralFeature.Setting)getMapping()).set(newValue);
				return;
			case JsonSchemaPackage.DISCRIMINATOR__DEFAULT_MAPPING:
				setDefaultMapping((String)newValue);
				return;
			case JsonSchemaPackage.DISCRIMINATOR__EXTENSIONS:
				((EStructuralFeature.Setting)getExtensions()).set(newValue);
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
			case JsonSchemaPackage.DISCRIMINATOR__PROPERTY_NAME:
				setPropertyName(PROPERTY_NAME_EDEFAULT);
				return;
			case JsonSchemaPackage.DISCRIMINATOR__MAPPING:
				unsetMapping();
				return;
			case JsonSchemaPackage.DISCRIMINATOR__DEFAULT_MAPPING:
				setDefaultMapping(DEFAULT_MAPPING_EDEFAULT);
				return;
			case JsonSchemaPackage.DISCRIMINATOR__EXTENSIONS:
				unsetExtensions();
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
			case JsonSchemaPackage.DISCRIMINATOR__PROPERTY_NAME:
				return PROPERTY_NAME_EDEFAULT == null ? propertyName != null : !PROPERTY_NAME_EDEFAULT.equals(propertyName);
			case JsonSchemaPackage.DISCRIMINATOR__MAPPING:
				return isSetMapping();
			case JsonSchemaPackage.DISCRIMINATOR__DEFAULT_MAPPING:
				return DEFAULT_MAPPING_EDEFAULT == null ? defaultMapping != null : !DEFAULT_MAPPING_EDEFAULT.equals(defaultMapping);
			case JsonSchemaPackage.DISCRIMINATOR__EXTENSIONS:
				return isSetExtensions();
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
		result.append(" (propertyName: ");
		result.append(propertyName);
		result.append(", defaultMapping: ");
		result.append(defaultMapping);
		result.append(')');
		return result.toString();
	}

} //DiscriminatorImpl
