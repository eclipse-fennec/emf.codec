/*
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.model.openapi.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.openapi.OAuthFlow;
import org.eclipse.fennec.model.openapi.OAuthFlows;
import org.eclipse.fennec.model.openapi.OpenApiPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>OAuth Flows</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OAuthFlowsImpl#getImplicit <em>Implicit</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OAuthFlowsImpl#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OAuthFlowsImpl#getClientCredentials <em>Client Credentials</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.OAuthFlowsImpl#getAuthorizationCode <em>Authorization Code</em>}</li>
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
		return OpenApiPackage.Literals.OAUTH_FLOWS;
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenApiPackage.OAUTH_FLOWS__IMPLICIT, oldImplicit, newImplicit);
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
				msgs = ((InternalEObject)implicit).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OAUTH_FLOWS__IMPLICIT, null, msgs);
			if (newImplicit != null)
				msgs = ((InternalEObject)newImplicit).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OAUTH_FLOWS__IMPLICIT, null, msgs);
			msgs = basicSetImplicit(newImplicit, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.OAUTH_FLOWS__IMPLICIT, newImplicit, newImplicit));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenApiPackage.OAUTH_FLOWS__PASSWORD, oldPassword, newPassword);
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
				msgs = ((InternalEObject)password).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OAUTH_FLOWS__PASSWORD, null, msgs);
			if (newPassword != null)
				msgs = ((InternalEObject)newPassword).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OAUTH_FLOWS__PASSWORD, null, msgs);
			msgs = basicSetPassword(newPassword, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.OAUTH_FLOWS__PASSWORD, newPassword, newPassword));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenApiPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS, oldClientCredentials, newClientCredentials);
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
				msgs = ((InternalEObject)clientCredentials).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS, null, msgs);
			if (newClientCredentials != null)
				msgs = ((InternalEObject)newClientCredentials).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS, null, msgs);
			msgs = basicSetClientCredentials(newClientCredentials, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS, newClientCredentials, newClientCredentials));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenApiPackage.OAUTH_FLOWS__AUTHORIZATION_CODE, oldAuthorizationCode, newAuthorizationCode);
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
				msgs = ((InternalEObject)authorizationCode).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OAUTH_FLOWS__AUTHORIZATION_CODE, null, msgs);
			if (newAuthorizationCode != null)
				msgs = ((InternalEObject)newAuthorizationCode).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.OAUTH_FLOWS__AUTHORIZATION_CODE, null, msgs);
			msgs = basicSetAuthorizationCode(newAuthorizationCode, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.OAUTH_FLOWS__AUTHORIZATION_CODE, newAuthorizationCode, newAuthorizationCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OpenApiPackage.OAUTH_FLOWS__IMPLICIT:
				return basicSetImplicit(null, msgs);
			case OpenApiPackage.OAUTH_FLOWS__PASSWORD:
				return basicSetPassword(null, msgs);
			case OpenApiPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				return basicSetClientCredentials(null, msgs);
			case OpenApiPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				return basicSetAuthorizationCode(null, msgs);
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
			case OpenApiPackage.OAUTH_FLOWS__IMPLICIT:
				return getImplicit();
			case OpenApiPackage.OAUTH_FLOWS__PASSWORD:
				return getPassword();
			case OpenApiPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				return getClientCredentials();
			case OpenApiPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				return getAuthorizationCode();
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
			case OpenApiPackage.OAUTH_FLOWS__IMPLICIT:
				setImplicit((OAuthFlow)newValue);
				return;
			case OpenApiPackage.OAUTH_FLOWS__PASSWORD:
				setPassword((OAuthFlow)newValue);
				return;
			case OpenApiPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				setClientCredentials((OAuthFlow)newValue);
				return;
			case OpenApiPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				setAuthorizationCode((OAuthFlow)newValue);
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
			case OpenApiPackage.OAUTH_FLOWS__IMPLICIT:
				setImplicit((OAuthFlow)null);
				return;
			case OpenApiPackage.OAUTH_FLOWS__PASSWORD:
				setPassword((OAuthFlow)null);
				return;
			case OpenApiPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				setClientCredentials((OAuthFlow)null);
				return;
			case OpenApiPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				setAuthorizationCode((OAuthFlow)null);
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
			case OpenApiPackage.OAUTH_FLOWS__IMPLICIT:
				return implicit != null;
			case OpenApiPackage.OAUTH_FLOWS__PASSWORD:
				return password != null;
			case OpenApiPackage.OAUTH_FLOWS__CLIENT_CREDENTIALS:
				return clientCredentials != null;
			case OpenApiPackage.OAUTH_FLOWS__AUTHORIZATION_CODE:
				return authorizationCode != null;
		}
		return super.eIsSet(featureID);
	}

} //OAuthFlowsImpl
