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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.jsonschema.model.Dependency;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;
import org.eclipse.fennec.codec.jsonschema.model.Schema;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dependency</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.DependencyImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.DependencyImpl#getProperties <em>Properties</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DependencyImpl extends MinimalEObjectImpl.Container implements Dependency {
	/**
	 * The cached value of the '{@link #getSchema() <em>Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected Schema schema;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected EList<String> properties;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DependencyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return JsonSchemaPackage.Literals.DEPENDENCY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getSchema() {
		return schema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSchema(Schema newSchema, NotificationChain msgs) {
		Schema oldSchema = schema;
		schema = newSchema;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.DEPENDENCY__SCHEMA, oldSchema, newSchema);
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
	public void setSchema(Schema newSchema) {
		if (newSchema != schema) {
			NotificationChain msgs = null;
			if (schema != null)
				msgs = ((InternalEObject)schema).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.DEPENDENCY__SCHEMA, null, msgs);
			if (newSchema != null)
				msgs = ((InternalEObject)newSchema).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonSchemaPackage.DEPENDENCY__SCHEMA, null, msgs);
			msgs = basicSetSchema(newSchema, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.DEPENDENCY__SCHEMA, newSchema, newSchema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getProperties() {
		if (properties == null) {
			properties = new EDataTypeEList.Unsettable<String>(String.class, this, JsonSchemaPackage.DEPENDENCY__PROPERTIES);
		}
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetProperties() {
		if (properties != null) ((InternalEList.Unsettable<?>)properties).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetProperties() {
		return properties != null && ((InternalEList.Unsettable<?>)properties).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case JsonSchemaPackage.DEPENDENCY__SCHEMA:
				return basicSetSchema(null, msgs);
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
			case JsonSchemaPackage.DEPENDENCY__SCHEMA:
				return getSchema();
			case JsonSchemaPackage.DEPENDENCY__PROPERTIES:
				return getProperties();
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
			case JsonSchemaPackage.DEPENDENCY__SCHEMA:
				setSchema((Schema)newValue);
				return;
			case JsonSchemaPackage.DEPENDENCY__PROPERTIES:
				getProperties().clear();
				getProperties().addAll((Collection<? extends String>)newValue);
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
			case JsonSchemaPackage.DEPENDENCY__SCHEMA:
				setSchema((Schema)null);
				return;
			case JsonSchemaPackage.DEPENDENCY__PROPERTIES:
				unsetProperties();
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
			case JsonSchemaPackage.DEPENDENCY__SCHEMA:
				return schema != null;
			case JsonSchemaPackage.DEPENDENCY__PROPERTIES:
				return isSetProperties();
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
		result.append(" (properties: ");
		result.append(properties);
		result.append(')');
		return result.toString();
	}

} //DependencyImpl
