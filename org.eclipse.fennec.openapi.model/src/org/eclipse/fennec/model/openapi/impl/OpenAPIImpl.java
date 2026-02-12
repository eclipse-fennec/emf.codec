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
package org.eclipse.fennec.model.openapi.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.openapi.Components;
import org.eclipse.fennec.model.openapi.ExternalDocumentation;
import org.eclipse.fennec.model.openapi.Info;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.PathItem;
import org.eclipse.fennec.model.openapi.SecurityRequirement;
import org.eclipse.fennec.model.openapi.Server;
import org.eclipse.fennec.model.openapi.Tag;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Open API</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl#getOpenapi <em>Openapi</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl#getInfo <em>Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl#getServers <em>Servers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl#getPaths <em>Paths</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl#getComponents <em>Components</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl#getSecurity <em>Security</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl#getTags <em>Tags</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OpenAPIImpl#getExternalDocs <em>External Docs</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OpenAPIImpl extends MinimalEObjectImpl.Container implements OpenAPI {
	/**
	 * The default value of the '{@link #getOpenapi() <em>Openapi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOpenapi()
	 * @generated
	 * @ordered
	 */
	protected static final String OPENAPI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOpenapi() <em>Openapi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOpenapi()
	 * @generated
	 * @ordered
	 */
	protected String openapi = OPENAPI_EDEFAULT;

	/**
	 * The cached value of the '{@link #getInfo() <em>Info</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInfo()
	 * @generated
	 * @ordered
	 */
	protected Info info;

	/**
	 * The cached value of the '{@link #getServers() <em>Servers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServers()
	 * @generated
	 * @ordered
	 */
	protected EList<Server> servers;

	/**
	 * The cached value of the '{@link #getPaths() <em>Paths</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPaths()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, PathItem> paths;

	/**
	 * The cached value of the '{@link #getComponents() <em>Components</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponents()
	 * @generated
	 * @ordered
	 */
	protected Components components;

	/**
	 * The cached value of the '{@link #getSecurity() <em>Security</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSecurity()
	 * @generated
	 * @ordered
	 */
	protected EList<SecurityRequirement> security;

	/**
	 * The cached value of the '{@link #getTags() <em>Tags</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTags()
	 * @generated
	 * @ordered
	 */
	protected EList<Tag> tags;

	/**
	 * The cached value of the '{@link #getExternalDocs() <em>External Docs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExternalDocs()
	 * @generated
	 * @ordered
	 */
	protected ExternalDocumentation externalDocs;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OpenAPIImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OpenApiPackage.Literals.OPEN_API;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getOpenapi() {
		return openapi;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOpenapi(String newOpenapi) {
		String oldOpenapi = openapi;
		openapi = newOpenapi;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.OPEN_API__OPENAPI, oldOpenapi, openapi));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Info getInfo() {
		return info;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetInfo(Info newInfo, NotificationChain msgs) {
		Info oldInfo = info;
		info = newInfo;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenApiPackage.OPEN_API__INFO, oldInfo, newInfo);
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
	public void setInfo(Info newInfo) {
		if (newInfo != info) {
			NotificationChain msgs = null;
			if (info != null)
				msgs = ((InternalEObject)info).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OPEN_API__INFO, null, msgs);
			if (newInfo != null)
				msgs = ((InternalEObject)newInfo).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OPEN_API__INFO, null, msgs);
			msgs = basicSetInfo(newInfo, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.OPEN_API__INFO, newInfo, newInfo));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Server> getServers() {
		if (servers == null) {
			servers = new EObjectContainmentEList<Server>(Server.class, this, OpenApiPackage.OPEN_API__SERVERS);
		}
		return servers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, PathItem> getPaths() {
		if (paths == null) {
			paths = new EcoreEMap<String,PathItem>(OpenApiPackage.Literals.PATH_ENTRY, PathEntryImpl.class, this, OpenApiPackage.OPEN_API__PATHS);
		}
		return paths;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Components getComponents() {
		return components;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetComponents(Components newComponents, NotificationChain msgs) {
		Components oldComponents = components;
		components = newComponents;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenApiPackage.OPEN_API__COMPONENTS, oldComponents, newComponents);
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
	public void setComponents(Components newComponents) {
		if (newComponents != components) {
			NotificationChain msgs = null;
			if (components != null)
				msgs = ((InternalEObject)components).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OPEN_API__COMPONENTS, null, msgs);
			if (newComponents != null)
				msgs = ((InternalEObject)newComponents).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OPEN_API__COMPONENTS, null, msgs);
			msgs = basicSetComponents(newComponents, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.OPEN_API__COMPONENTS, newComponents, newComponents));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<SecurityRequirement> getSecurity() {
		if (security == null) {
			security = new EObjectContainmentEList<SecurityRequirement>(SecurityRequirement.class, this, OpenApiPackage.OPEN_API__SECURITY);
		}
		return security;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Tag> getTags() {
		if (tags == null) {
			tags = new EObjectContainmentEList<Tag>(Tag.class, this, OpenApiPackage.OPEN_API__TAGS);
		}
		return tags;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ExternalDocumentation getExternalDocs() {
		return externalDocs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExternalDocs(ExternalDocumentation newExternalDocs, NotificationChain msgs) {
		ExternalDocumentation oldExternalDocs = externalDocs;
		externalDocs = newExternalDocs;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenApiPackage.OPEN_API__EXTERNAL_DOCS, oldExternalDocs, newExternalDocs);
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
	public void setExternalDocs(ExternalDocumentation newExternalDocs) {
		if (newExternalDocs != externalDocs) {
			NotificationChain msgs = null;
			if (externalDocs != null)
				msgs = ((InternalEObject)externalDocs).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OPEN_API__EXTERNAL_DOCS, null, msgs);
			if (newExternalDocs != null)
				msgs = ((InternalEObject)newExternalDocs).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OPEN_API__EXTERNAL_DOCS, null, msgs);
			msgs = basicSetExternalDocs(newExternalDocs, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.OPEN_API__EXTERNAL_DOCS, newExternalDocs, newExternalDocs));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OpenApiPackage.OPEN_API__INFO:
				return basicSetInfo(null, msgs);
			case OpenApiPackage.OPEN_API__SERVERS:
				return ((InternalEList<?>)getServers()).basicRemove(otherEnd, msgs);
			case OpenApiPackage.OPEN_API__PATHS:
				return ((InternalEList<?>)getPaths()).basicRemove(otherEnd, msgs);
			case OpenApiPackage.OPEN_API__COMPONENTS:
				return basicSetComponents(null, msgs);
			case OpenApiPackage.OPEN_API__SECURITY:
				return ((InternalEList<?>)getSecurity()).basicRemove(otherEnd, msgs);
			case OpenApiPackage.OPEN_API__TAGS:
				return ((InternalEList<?>)getTags()).basicRemove(otherEnd, msgs);
			case OpenApiPackage.OPEN_API__EXTERNAL_DOCS:
				return basicSetExternalDocs(null, msgs);
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
			case OpenApiPackage.OPEN_API__OPENAPI:
				return getOpenapi();
			case OpenApiPackage.OPEN_API__INFO:
				return getInfo();
			case OpenApiPackage.OPEN_API__SERVERS:
				return getServers();
			case OpenApiPackage.OPEN_API__PATHS:
				if (coreType) return getPaths();
				else return getPaths().map();
			case OpenApiPackage.OPEN_API__COMPONENTS:
				return getComponents();
			case OpenApiPackage.OPEN_API__SECURITY:
				return getSecurity();
			case OpenApiPackage.OPEN_API__TAGS:
				return getTags();
			case OpenApiPackage.OPEN_API__EXTERNAL_DOCS:
				return getExternalDocs();
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
			case OpenApiPackage.OPEN_API__OPENAPI:
				setOpenapi((String)newValue);
				return;
			case OpenApiPackage.OPEN_API__INFO:
				setInfo((Info)newValue);
				return;
			case OpenApiPackage.OPEN_API__SERVERS:
				getServers().clear();
				getServers().addAll((Collection<? extends Server>)newValue);
				return;
			case OpenApiPackage.OPEN_API__PATHS:
				((EStructuralFeature.Setting)getPaths()).set(newValue);
				return;
			case OpenApiPackage.OPEN_API__COMPONENTS:
				setComponents((Components)newValue);
				return;
			case OpenApiPackage.OPEN_API__SECURITY:
				getSecurity().clear();
				getSecurity().addAll((Collection<? extends SecurityRequirement>)newValue);
				return;
			case OpenApiPackage.OPEN_API__TAGS:
				getTags().clear();
				getTags().addAll((Collection<? extends Tag>)newValue);
				return;
			case OpenApiPackage.OPEN_API__EXTERNAL_DOCS:
				setExternalDocs((ExternalDocumentation)newValue);
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
			case OpenApiPackage.OPEN_API__OPENAPI:
				setOpenapi(OPENAPI_EDEFAULT);
				return;
			case OpenApiPackage.OPEN_API__INFO:
				setInfo((Info)null);
				return;
			case OpenApiPackage.OPEN_API__SERVERS:
				getServers().clear();
				return;
			case OpenApiPackage.OPEN_API__PATHS:
				getPaths().clear();
				return;
			case OpenApiPackage.OPEN_API__COMPONENTS:
				setComponents((Components)null);
				return;
			case OpenApiPackage.OPEN_API__SECURITY:
				getSecurity().clear();
				return;
			case OpenApiPackage.OPEN_API__TAGS:
				getTags().clear();
				return;
			case OpenApiPackage.OPEN_API__EXTERNAL_DOCS:
				setExternalDocs((ExternalDocumentation)null);
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
			case OpenApiPackage.OPEN_API__OPENAPI:
				return OPENAPI_EDEFAULT == null ? openapi != null : !OPENAPI_EDEFAULT.equals(openapi);
			case OpenApiPackage.OPEN_API__INFO:
				return info != null;
			case OpenApiPackage.OPEN_API__SERVERS:
				return servers != null && !servers.isEmpty();
			case OpenApiPackage.OPEN_API__PATHS:
				return paths != null && !paths.isEmpty();
			case OpenApiPackage.OPEN_API__COMPONENTS:
				return components != null;
			case OpenApiPackage.OPEN_API__SECURITY:
				return security != null && !security.isEmpty();
			case OpenApiPackage.OPEN_API__TAGS:
				return tags != null && !tags.isEmpty();
			case OpenApiPackage.OPEN_API__EXTERNAL_DOCS:
				return externalDocs != null;
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
		result.append(" (openapi: ");
		result.append(openapi);
		result.append(')');
		return result.toString();
	}

} //OpenAPIImpl
