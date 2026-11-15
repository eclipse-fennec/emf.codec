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
import org.eclipse.fennec.model.oas.OasPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>OAuth Flow</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowImpl#getAuthorizationUrl <em>Authorization Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowImpl#getDeviceAuthorizationUrl <em>Device Authorization Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowImpl#getTokenUrl <em>Token Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowImpl#getRefreshUrl <em>Refresh Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowImpl#getScopes <em>Scopes</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OAuthFlowImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OAuthFlowImpl extends MinimalEObjectImpl.Container implements OAuthFlow {
	/**
	 * The default value of the '{@link #getAuthorizationUrl() <em>Authorization Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthorizationUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String AUTHORIZATION_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAuthorizationUrl() <em>Authorization Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthorizationUrl()
	 * @generated
	 * @ordered
	 */
	protected String authorizationUrl = AUTHORIZATION_URL_EDEFAULT;

	/**
	 * The default value of the '{@link #getDeviceAuthorizationUrl() <em>Device Authorization Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceAuthorizationUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String DEVICE_AUTHORIZATION_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeviceAuthorizationUrl() <em>Device Authorization Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceAuthorizationUrl()
	 * @generated
	 * @ordered
	 */
	protected String deviceAuthorizationUrl = DEVICE_AUTHORIZATION_URL_EDEFAULT;

	/**
	 * The default value of the '{@link #getTokenUrl() <em>Token Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTokenUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String TOKEN_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTokenUrl() <em>Token Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTokenUrl()
	 * @generated
	 * @ordered
	 */
	protected String tokenUrl = TOKEN_URL_EDEFAULT;

	/**
	 * The default value of the '{@link #getRefreshUrl() <em>Refresh Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefreshUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String REFRESH_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRefreshUrl() <em>Refresh Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefreshUrl()
	 * @generated
	 * @ordered
	 */
	protected String refreshUrl = REFRESH_URL_EDEFAULT;

	/**
	 * The cached value of the '{@link #getScopes() <em>Scopes</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScopes()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> scopes;

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
	protected OAuthFlowImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OasPackage.Literals.OAUTH_FLOW;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAuthorizationUrl() {
		return authorizationUrl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAuthorizationUrl(String newAuthorizationUrl) {
		String oldAuthorizationUrl = authorizationUrl;
		authorizationUrl = newAuthorizationUrl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOW__AUTHORIZATION_URL, oldAuthorizationUrl, authorizationUrl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDeviceAuthorizationUrl() {
		return deviceAuthorizationUrl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeviceAuthorizationUrl(String newDeviceAuthorizationUrl) {
		String oldDeviceAuthorizationUrl = deviceAuthorizationUrl;
		deviceAuthorizationUrl = newDeviceAuthorizationUrl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOW__DEVICE_AUTHORIZATION_URL, oldDeviceAuthorizationUrl, deviceAuthorizationUrl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTokenUrl() {
		return tokenUrl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTokenUrl(String newTokenUrl) {
		String oldTokenUrl = tokenUrl;
		tokenUrl = newTokenUrl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOW__TOKEN_URL, oldTokenUrl, tokenUrl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRefreshUrl() {
		return refreshUrl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRefreshUrl(String newRefreshUrl) {
		String oldRefreshUrl = refreshUrl;
		refreshUrl = newRefreshUrl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OAUTH_FLOW__REFRESH_URL, oldRefreshUrl, refreshUrl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, String> getScopes() {
		if (scopes == null) {
			scopes = new EcoreEMap<String,String>(OasPackage.Literals.STRING_ENTRY, StringEntryImpl.class, this, OasPackage.OAUTH_FLOW__SCOPES);
		}
		return scopes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap<String,Object>(OasPackage.Literals.EXTENSION_ENTRY, ExtensionEntryImpl.class, this, OasPackage.OAUTH_FLOW__EXTENSIONS);
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
			case OasPackage.OAUTH_FLOW__SCOPES:
				return ((InternalEList<?>)getScopes()).basicRemove(otherEnd, msgs);
			case OasPackage.OAUTH_FLOW__EXTENSIONS:
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
			case OasPackage.OAUTH_FLOW__AUTHORIZATION_URL:
				return getAuthorizationUrl();
			case OasPackage.OAUTH_FLOW__DEVICE_AUTHORIZATION_URL:
				return getDeviceAuthorizationUrl();
			case OasPackage.OAUTH_FLOW__TOKEN_URL:
				return getTokenUrl();
			case OasPackage.OAUTH_FLOW__REFRESH_URL:
				return getRefreshUrl();
			case OasPackage.OAUTH_FLOW__SCOPES:
				if (coreType) return getScopes();
				else return getScopes().map();
			case OasPackage.OAUTH_FLOW__EXTENSIONS:
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
			case OasPackage.OAUTH_FLOW__AUTHORIZATION_URL:
				setAuthorizationUrl((String)newValue);
				return;
			case OasPackage.OAUTH_FLOW__DEVICE_AUTHORIZATION_URL:
				setDeviceAuthorizationUrl((String)newValue);
				return;
			case OasPackage.OAUTH_FLOW__TOKEN_URL:
				setTokenUrl((String)newValue);
				return;
			case OasPackage.OAUTH_FLOW__REFRESH_URL:
				setRefreshUrl((String)newValue);
				return;
			case OasPackage.OAUTH_FLOW__SCOPES:
				((EStructuralFeature.Setting)getScopes()).set(newValue);
				return;
			case OasPackage.OAUTH_FLOW__EXTENSIONS:
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
			case OasPackage.OAUTH_FLOW__AUTHORIZATION_URL:
				setAuthorizationUrl(AUTHORIZATION_URL_EDEFAULT);
				return;
			case OasPackage.OAUTH_FLOW__DEVICE_AUTHORIZATION_URL:
				setDeviceAuthorizationUrl(DEVICE_AUTHORIZATION_URL_EDEFAULT);
				return;
			case OasPackage.OAUTH_FLOW__TOKEN_URL:
				setTokenUrl(TOKEN_URL_EDEFAULT);
				return;
			case OasPackage.OAUTH_FLOW__REFRESH_URL:
				setRefreshUrl(REFRESH_URL_EDEFAULT);
				return;
			case OasPackage.OAUTH_FLOW__SCOPES:
				getScopes().clear();
				return;
			case OasPackage.OAUTH_FLOW__EXTENSIONS:
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
			case OasPackage.OAUTH_FLOW__AUTHORIZATION_URL:
				return AUTHORIZATION_URL_EDEFAULT == null ? authorizationUrl != null : !AUTHORIZATION_URL_EDEFAULT.equals(authorizationUrl);
			case OasPackage.OAUTH_FLOW__DEVICE_AUTHORIZATION_URL:
				return DEVICE_AUTHORIZATION_URL_EDEFAULT == null ? deviceAuthorizationUrl != null : !DEVICE_AUTHORIZATION_URL_EDEFAULT.equals(deviceAuthorizationUrl);
			case OasPackage.OAUTH_FLOW__TOKEN_URL:
				return TOKEN_URL_EDEFAULT == null ? tokenUrl != null : !TOKEN_URL_EDEFAULT.equals(tokenUrl);
			case OasPackage.OAUTH_FLOW__REFRESH_URL:
				return REFRESH_URL_EDEFAULT == null ? refreshUrl != null : !REFRESH_URL_EDEFAULT.equals(refreshUrl);
			case OasPackage.OAUTH_FLOW__SCOPES:
				return scopes != null && !scopes.isEmpty();
			case OasPackage.OAUTH_FLOW__EXTENSIONS:
				return extensions != null && !extensions.isEmpty();
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
		result.append(" (authorizationUrl: ");
		result.append(authorizationUrl);
		result.append(", deviceAuthorizationUrl: ");
		result.append(deviceAuthorizationUrl);
		result.append(", tokenUrl: ");
		result.append(tokenUrl);
		result.append(", refreshUrl: ");
		result.append(refreshUrl);
		result.append(')');
		return result.toString();
	}

} //OAuthFlowImpl
