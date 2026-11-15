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
package org.eclipse.fennec.model.oas.impl;

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

import org.eclipse.fennec.model.oas.OAuthFlow;
import org.eclipse.fennec.model.oas.OAuthFlows;
import org.eclipse.fennec.model.oas.OasPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>OAuth Flows</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl#getImplicit <em>Implicit</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl#getClientCredentials <em>Client Credentials</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl#getAuthorizationCode <em>Authorization Code</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl#getDeviceAuthorization <em>Device Authorization</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowsImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OAuthFlowsImpl extends MinimalEObjectImpl.Container implements OAuthFlows {
	/**
	 * The cached value of the '{@link #getImplicit() <em>Implicit</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplicit()
	 * @generated
	 * @ordered
	 */
	protected OAuthFlow implicit;

	/**
	 * The cached value of the '{@link #getPassword() <em>Password</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPassword()
	 * @generated
	 * @ordered
	 */
	protected OAuthFlow password;

	/**
	 * The cached value of the '{@link #getClientCredentials() <em>Client Credentials</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClientCredentials()
	 * @generated
	 * @ordered
	 */
	protected OAuthFlow clientCredentials;

	/**
	 * The cached value of the '{@link #getAuthorizationCode() <em>Authorization Code</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthorizationCode()
	 * @generated
	 * @ordered
	 */
	protected OAuthFlow authorizationCode;

	/**
	 * The cached value of the '{@link #getDeviceAuthorization() <em>Device Authorization</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceAuthorization()
	 * @generated
	 * @ordered
	 */
	protected OAuthFlow deviceAuthorization;

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
	protected OAuthFlowsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OasPackage.Literals.OAUTH_FLOWS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OAuthFlow getImplicit() {
		return implicit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetImplicit(OAuthFlow newImplicit, NotificationChain msgs) {
		OAuthFlow oldImplicit = implicit;
		implicit = newImplicit;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__IMPLICIT, oldImplicit, newImplicit);
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
	public void setImplicit(OAuthFlow newImplicit) {
		if (newImplicit != implicit) {
			NotificationChain msgs = null;
			if (implicit != null)
				msgs = ((InternalEObject)implicit).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__IMPLICIT, null, msgs);
			if (newImplicit != null)
				msgs = ((InternalEObject)newImplicit).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__IMPLICIT, null, msgs);
			msgs = basicSetImplicit(newImplicit, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__IMPLICIT, newImplicit, newImplicit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OAuthFlow getPassword() {
		return password;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPassword(OAuthFlow newPassword, NotificationChain msgs) {
		OAuthFlow oldPassword = password;
		password = newPassword;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__PASSWORD, oldPassword, newPassword);
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
	public void setPassword(OAuthFlow newPassword) {
		if (newPassword != password) {
			NotificationChain msgs = null;
			if (password != null)
				msgs = ((InternalEObject)password).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__PASSWORD, null, msgs);
			if (newPassword != null)
				msgs = ((InternalEObject)newPassword).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__PASSWORD, null, msgs);
			msgs = basicSetPassword(newPassword, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__PASSWORD, newPassword, newPassword));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OAuthFlow getClientCredentials() {
		return clientCredentials;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetClientCredentials(OAuthFlow newClientCredentials, NotificationChain msgs) {
		OAuthFlow oldClientCredentials = clientCredentials;
		clientCredentials = newClientCredentials;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS, oldClientCredentials, newClientCredentials);
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
	public void setClientCredentials(OAuthFlow newClientCredentials) {
		if (newClientCredentials != clientCredentials) {
			NotificationChain msgs = null;
			if (clientCredentials != null)
				msgs = ((InternalEObject)clientCredentials).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS, null, msgs);
			if (newClientCredentials != null)
				msgs = ((InternalEObject)newClientCredentials).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS, null, msgs);
			msgs = basicSetClientCredentials(newClientCredentials, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS, newClientCredentials, newClientCredentials));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OAuthFlow getAuthorizationCode() {
		return authorizationCode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAuthorizationCode(OAuthFlow newAuthorizationCode, NotificationChain msgs) {
		OAuthFlow oldAuthorizationCode = authorizationCode;
		authorizationCode = newAuthorizationCode;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__AUTHORIZATION_CODE, oldAuthorizationCode, newAuthorizationCode);
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
	public void setAuthorizationCode(OAuthFlow newAuthorizationCode) {
		if (newAuthorizationCode != authorizationCode) {
			NotificationChain msgs = null;
			if (authorizationCode != null)
				msgs = ((InternalEObject)authorizationCode).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__AUTHORIZATION_CODE, null, msgs);
			if (newAuthorizationCode != null)
				msgs = ((InternalEObject)newAuthorizationCode).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__AUTHORIZATION_CODE, null, msgs);
			msgs = basicSetAuthorizationCode(newAuthorizationCode, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__AUTHORIZATION_CODE, newAuthorizationCode, newAuthorizationCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OAuthFlow getDeviceAuthorization() {
		return deviceAuthorization;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDeviceAuthorization(OAuthFlow newDeviceAuthorization, NotificationChain msgs) {
		OAuthFlow oldDeviceAuthorization = deviceAuthorization;
		deviceAuthorization = newDeviceAuthorization;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__DEVICE_AUTHORIZATION, oldDeviceAuthorization, newDeviceAuthorization);
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
	public void setDeviceAuthorization(OAuthFlow newDeviceAuthorization) {
		if (newDeviceAuthorization != deviceAuthorization) {
			NotificationChain msgs = null;
			if (deviceAuthorization != null)
				msgs = ((InternalEObject)deviceAuthorization).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__DEVICE_AUTHORIZATION, null, msgs);
			if (newDeviceAuthorization != null)
				msgs = ((InternalEObject)newDeviceAuthorization).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.OAUTH_FLOWS__DEVICE_AUTHORIZATION, null, msgs);
			msgs = basicSetDeviceAuthorization(newDeviceAuthorization, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOWS__DEVICE_AUTHORIZATION, newDeviceAuthorization, newDeviceAuthorization));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap<String,Object>(OasPackage.Literals.EXTENSION_ENTRY, ExtensionEntryImpl.class, this, OasPackage.OAUTH_FLOWS__EXTENSIONS);
		}
		return extensions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OasPackage.OAUTH_FLOWS__IMPLICIT:
				return basicSetImplicit(null, msgs);
			case OasPackage.OAUTH_FLOWS__PASSWORD:
				return basicSetPassword(null, msgs);
			case OasPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				return basicSetClientCredentials(null, msgs);
			case OasPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				return basicSetAuthorizationCode(null, msgs);
			case OasPackage.OAUTH_FLOWS__DEVICE_AUTHORIZATION:
				return basicSetDeviceAuthorization(null, msgs);
			case OasPackage.OAUTH_FLOWS__EXTENSIONS:
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
			case OasPackage.OAUTH_FLOWS__IMPLICIT:
				return getImplicit();
			case OasPackage.OAUTH_FLOWS__PASSWORD:
				return getPassword();
			case OasPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				return getClientCredentials();
			case OasPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				return getAuthorizationCode();
			case OasPackage.OAUTH_FLOWS__DEVICE_AUTHORIZATION:
				return getDeviceAuthorization();
			case OasPackage.OAUTH_FLOWS__EXTENSIONS:
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
			case OasPackage.OAUTH_FLOWS__IMPLICIT:
				setImplicit((OAuthFlow)newValue);
				return;
			case OasPackage.OAUTH_FLOWS__PASSWORD:
				setPassword((OAuthFlow)newValue);
				return;
			case OasPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				setClientCredentials((OAuthFlow)newValue);
				return;
			case OasPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				setAuthorizationCode((OAuthFlow)newValue);
				return;
			case OasPackage.OAUTH_FLOWS__DEVICE_AUTHORIZATION:
				setDeviceAuthorization((OAuthFlow)newValue);
				return;
			case OasPackage.OAUTH_FLOWS__EXTENSIONS:
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
			case OasPackage.OAUTH_FLOWS__IMPLICIT:
				setImplicit((OAuthFlow)null);
				return;
			case OasPackage.OAUTH_FLOWS__PASSWORD:
				setPassword((OAuthFlow)null);
				return;
			case OasPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				setClientCredentials((OAuthFlow)null);
				return;
			case OasPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				setAuthorizationCode((OAuthFlow)null);
				return;
			case OasPackage.OAUTH_FLOWS__DEVICE_AUTHORIZATION:
				setDeviceAuthorization((OAuthFlow)null);
				return;
			case OasPackage.OAUTH_FLOWS__EXTENSIONS:
				getExtensions().clear();
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
			case OasPackage.OAUTH_FLOWS__IMPLICIT:
				return implicit != null;
			case OasPackage.OAUTH_FLOWS__PASSWORD:
				return password != null;
			case OasPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				return clientCredentials != null;
			case OasPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				return authorizationCode != null;
			case OasPackage.OAUTH_FLOWS__DEVICE_AUTHORIZATION:
				return deviceAuthorization != null;
			case OasPackage.OAUTH_FLOWS__EXTENSIONS:
				return extensions != null && !extensions.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //OAuthFlowsImpl
