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

import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;
import org.eclipse.fennec.codec.jsonschema.model.Xml;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl#getWrapped <em>Wrapped</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl#getNodeType <em>Node Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.impl.XmlImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XmlImpl extends MinimalEObjectImpl.Container implements Xml {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getNamespace() <em>Namespace</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamespace()
	 * @generated
	 * @ordered
	 */
	protected static final String NAMESPACE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNamespace() <em>Namespace</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamespace()
	 * @generated
	 * @ordered
	 */
	protected String namespace = NAMESPACE_EDEFAULT;

	/**
	 * The default value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrefix()
	 * @generated
	 * @ordered
	 */
	protected static final String PREFIX_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrefix()
	 * @generated
	 * @ordered
	 */
	protected String prefix = PREFIX_EDEFAULT;

	/**
	 * The default value of the '{@link #getAttribute() <em>Attribute</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttribute()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean ATTRIBUTE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAttribute() <em>Attribute</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttribute()
	 * @generated
	 * @ordered
	 */
	protected Boolean attribute = ATTRIBUTE_EDEFAULT;

	/**
	 * This is true if the Attribute attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean attributeESet;

	/**
	 * The default value of the '{@link #getWrapped() <em>Wrapped</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWrapped()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean WRAPPED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWrapped() <em>Wrapped</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWrapped()
	 * @generated
	 * @ordered
	 */
	protected Boolean wrapped = WRAPPED_EDEFAULT;

	/**
	 * This is true if the Wrapped attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean wrappedESet;

	/**
	 * The default value of the '{@link #getNodeType() <em>Node Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNodeType()
	 * @generated
	 * @ordered
	 */
	protected static final String NODE_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNodeType() <em>Node Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNodeType()
	 * @generated
	 * @ordered
	 */
	protected String nodeType = NODE_TYPE_EDEFAULT;

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
	protected XmlImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return JsonSchemaPackage.Literals.XML;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.XML__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getNamespace() {
		return namespace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNamespace(String newNamespace) {
		String oldNamespace = namespace;
		namespace = newNamespace;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.XML__NAMESPACE, oldNamespace, namespace));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPrefix() {
		return prefix;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPrefix(String newPrefix) {
		String oldPrefix = prefix;
		prefix = newPrefix;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.XML__PREFIX, oldPrefix, prefix));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getAttribute() {
		return attribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAttribute(Boolean newAttribute) {
		Boolean oldAttribute = attribute;
		attribute = newAttribute;
		boolean oldAttributeESet = attributeESet;
		attributeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.XML__ATTRIBUTE, oldAttribute, attribute, !oldAttributeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetAttribute() {
		Boolean oldAttribute = attribute;
		boolean oldAttributeESet = attributeESet;
		attribute = ATTRIBUTE_EDEFAULT;
		attributeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.XML__ATTRIBUTE, oldAttribute, ATTRIBUTE_EDEFAULT, oldAttributeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetAttribute() {
		return attributeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getWrapped() {
		return wrapped;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWrapped(Boolean newWrapped) {
		Boolean oldWrapped = wrapped;
		wrapped = newWrapped;
		boolean oldWrappedESet = wrappedESet;
		wrappedESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.XML__WRAPPED, oldWrapped, wrapped, !oldWrappedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetWrapped() {
		Boolean oldWrapped = wrapped;
		boolean oldWrappedESet = wrappedESet;
		wrapped = WRAPPED_EDEFAULT;
		wrappedESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, JsonSchemaPackage.XML__WRAPPED, oldWrapped, WRAPPED_EDEFAULT, oldWrappedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetWrapped() {
		return wrappedESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNodeType(String newNodeType) {
		String oldNodeType = nodeType;
		nodeType = newNodeType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, JsonSchemaPackage.XML__NODE_TYPE, oldNodeType, nodeType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap.Unsettable<String,Object>(JsonSchemaPackage.Literals.KEYWORD_ENTRY, KeywordEntryImpl.class, this, JsonSchemaPackage.XML__EXTENSIONS);
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
			case JsonSchemaPackage.XML__EXTENSIONS:
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
			case JsonSchemaPackage.XML__NAME:
				return getName();
			case JsonSchemaPackage.XML__NAMESPACE:
				return getNamespace();
			case JsonSchemaPackage.XML__PREFIX:
				return getPrefix();
			case JsonSchemaPackage.XML__ATTRIBUTE:
				return getAttribute();
			case JsonSchemaPackage.XML__WRAPPED:
				return getWrapped();
			case JsonSchemaPackage.XML__NODE_TYPE:
				return getNodeType();
			case JsonSchemaPackage.XML__EXTENSIONS:
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
			case JsonSchemaPackage.XML__NAME:
				setName((String)newValue);
				return;
			case JsonSchemaPackage.XML__NAMESPACE:
				setNamespace((String)newValue);
				return;
			case JsonSchemaPackage.XML__PREFIX:
				setPrefix((String)newValue);
				return;
			case JsonSchemaPackage.XML__ATTRIBUTE:
				setAttribute((Boolean)newValue);
				return;
			case JsonSchemaPackage.XML__WRAPPED:
				setWrapped((Boolean)newValue);
				return;
			case JsonSchemaPackage.XML__NODE_TYPE:
				setNodeType((String)newValue);
				return;
			case JsonSchemaPackage.XML__EXTENSIONS:
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
			case JsonSchemaPackage.XML__NAME:
				setName(NAME_EDEFAULT);
				return;
			case JsonSchemaPackage.XML__NAMESPACE:
				setNamespace(NAMESPACE_EDEFAULT);
				return;
			case JsonSchemaPackage.XML__PREFIX:
				setPrefix(PREFIX_EDEFAULT);
				return;
			case JsonSchemaPackage.XML__ATTRIBUTE:
				unsetAttribute();
				return;
			case JsonSchemaPackage.XML__WRAPPED:
				unsetWrapped();
				return;
			case JsonSchemaPackage.XML__NODE_TYPE:
				setNodeType(NODE_TYPE_EDEFAULT);
				return;
			case JsonSchemaPackage.XML__EXTENSIONS:
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
			case JsonSchemaPackage.XML__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case JsonSchemaPackage.XML__NAMESPACE:
				return NAMESPACE_EDEFAULT == null ? namespace != null : !NAMESPACE_EDEFAULT.equals(namespace);
			case JsonSchemaPackage.XML__PREFIX:
				return PREFIX_EDEFAULT == null ? prefix != null : !PREFIX_EDEFAULT.equals(prefix);
			case JsonSchemaPackage.XML__ATTRIBUTE:
				return isSetAttribute();
			case JsonSchemaPackage.XML__WRAPPED:
				return isSetWrapped();
			case JsonSchemaPackage.XML__NODE_TYPE:
				return NODE_TYPE_EDEFAULT == null ? nodeType != null : !NODE_TYPE_EDEFAULT.equals(nodeType);
			case JsonSchemaPackage.XML__EXTENSIONS:
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
		result.append(" (name: ");
		result.append(name);
		result.append(", namespace: ");
		result.append(namespace);
		result.append(", prefix: ");
		result.append(prefix);
		result.append(", attribute: ");
		if (attributeESet) result.append(attribute); else result.append("<unset>");
		result.append(", wrapped: ");
		if (wrappedESet) result.append(wrapped); else result.append("<unset>");
		result.append(", nodeType: ");
		result.append(nodeType);
		result.append(')');
		return result.toString();
	}

} //XmlImpl
