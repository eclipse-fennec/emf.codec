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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.model.openapi.ApiKeyLocation;
import org.eclipse.fennec.model.openapi.OAuthFlows;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.SecurityScheme;
import org.eclipse.fennec.model.openapi.SecuritySchemeType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Security Scheme</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl#getIn <em>In</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl#getScheme <em>Scheme</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl#getBearerFormat <em>Bearer Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl#getFlows <em>Flows</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.SecuritySchemeImpl#getOpenIdConnectUrl <em>Open Id Connect Url</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SecuritySchemeImpl extends MinimalEObjectImpl.Container implements SecurityScheme {
	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final SecuritySchemeType TYPE_EDEFAULT = SecuritySchemeType.API_KEY;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected SecuritySchemeType type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

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
	 * The default value of the '{@link #getIn() <em>In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIn()
	 * @generated
	 * @ordered
	 */
	protected static final ApiKeyLocation IN_EDEFAULT = ApiKeyLocation.QUERY;

	/**
	 * The cached value of the '{@link #getIn() <em>In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIn()
	 * @generated
	 * @ordered
	 */
	protected ApiKeyLocation in = IN_EDEFAULT;

	/**
	 * The default value of the '{@link #getScheme() <em>Scheme</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScheme()
	 * @generated
	 * @ordered
	 */
	protected static final String SCHEME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getScheme() <em>Scheme</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScheme()
	 * @generated
	 * @ordered
	 */
	protected String scheme = SCHEME_EDEFAULT;

	/**
	 * The default value of the '{@link #getBearerFormat() <em>Bearer Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBearerFormat()
	 * @generated
	 * @ordered
	 */
	protected static final String BEARER_FORMAT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBearerFormat() <em>Bearer Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBearerFormat()
	 * @generated
	 * @ordered
	 */
	protected String bearerFormat = BEARER_FORMAT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFlows() <em>Flows</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFlows()
	 * @generated
	 * @ordered
	 */
	protected OAuthFlows flows;

	/**
	 * The default value of the '{@link #getOpenIdConnectUrl() <em>Open Id Connect Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOpenIdConnectUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String OPEN_ID_CONNECT_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOpenIdConnectUrl() <em>Open Id Connect Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOpenIdConnectUrl()
	 * @generated
	 * @ordered
	 */
	protected String openIdConnectUrl = OPEN_ID_CONNECT_URL_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SecuritySchemeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OpenApiPackage.Literals.SECURITY_SCHEME;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SecuritySchemeType getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setType(SecuritySchemeType newType) {
		SecuritySchemeType oldType = type;
		type = newType == null ? TYPE_EDEFAULT : newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.SECURITY_SCHEME__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.SECURITY_SCHEME__DESCRIPTION, oldDescription, description));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.SECURITY_SCHEME__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ApiKeyLocation getIn() {
		return in;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIn(ApiKeyLocation newIn) {
		ApiKeyLocation oldIn = in;
		in = newIn == null ? IN_EDEFAULT : newIn;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.SECURITY_SCHEME__IN, oldIn, in));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getScheme() {
		return scheme;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setScheme(String newScheme) {
		String oldScheme = scheme;
		scheme = newScheme;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.SECURITY_SCHEME__SCHEME, oldScheme, scheme));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getBearerFormat() {
		return bearerFormat;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBearerFormat(String newBearerFormat) {
		String oldBearerFormat = bearerFormat;
		bearerFormat = newBearerFormat;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.SECURITY_SCHEME__BEARER_FORMAT, oldBearerFormat, bearerFormat));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OAuthFlows getFlows() {
		return flows;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFlows(OAuthFlows newFlows, NotificationChain msgs) {
		OAuthFlows oldFlows = flows;
		flows = newFlows;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenApiPackage.SECURITY_SCHEME__FLOWS, oldFlows, newFlows);
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
	public void setFlows(OAuthFlows newFlows) {
		if (newFlows != flows) {
			NotificationChain msgs = null;
			if (flows != null)
				msgs = ((InternalEObject)flows).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.SECURITY_SCHEME__FLOWS, null, msgs);
			if (newFlows != null)
				msgs = ((InternalEObject)newFlows).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.SECURITY_SCHEME__FLOWS, null, msgs);
			msgs = basicSetFlows(newFlows, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.SECURITY_SCHEME__FLOWS, newFlows, newFlows));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getOpenIdConnectUrl() {
		return openIdConnectUrl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOpenIdConnectUrl(String newOpenIdConnectUrl) {
		String oldOpenIdConnectUrl = openIdConnectUrl;
		openIdConnectUrl = newOpenIdConnectUrl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL, oldOpenIdConnectUrl, openIdConnectUrl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OpenApiPackage.SECURITY_SCHEME__FLOWS:
				return basicSetFlows(null, msgs);
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
			case OpenApiPackage.SECURITY_SCHEME__TYPE:
				return getType();
			case OpenApiPackage.SECURITY_SCHEME__DESCRIPTION:
				return getDescription();
			case OpenApiPackage.SECURITY_SCHEME__NAME:
				return getName();
			case OpenApiPackage.SECURITY_SCHEME__IN:
				return getIn();
			case OpenApiPackage.SECURITY_SCHEME__SCHEME:
				return getScheme();
			case OpenApiPackage.SECURITY_SCHEME__BEARER_FORMAT:
				return getBearerFormat();
			case OpenApiPackage.SECURITY_SCHEME__FLOWS:
				return getFlows();
			case OpenApiPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL:
				return getOpenIdConnectUrl();
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
			case OpenApiPackage.SECURITY_SCHEME__TYPE:
				setType((SecuritySchemeType)newValue);
				return;
			case OpenApiPackage.SECURITY_SCHEME__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case OpenApiPackage.SECURITY_SCHEME__NAME:
				setName((String)newValue);
				return;
			case OpenApiPackage.SECURITY_SCHEME__IN:
				setIn((ApiKeyLocation)newValue);
				return;
			case OpenApiPackage.SECURITY_SCHEME__SCHEME:
				setScheme((String)newValue);
				return;
			case OpenApiPackage.SECURITY_SCHEME__BEARER_FORMAT:
				setBearerFormat((String)newValue);
				return;
			case OpenApiPackage.SECURITY_SCHEME__FLOWS:
				setFlows((OAuthFlows)newValue);
				return;
			case OpenApiPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL:
				setOpenIdConnectUrl((String)newValue);
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
			case OpenApiPackage.SECURITY_SCHEME__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case OpenApiPackage.SECURITY_SCHEME__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case OpenApiPackage.SECURITY_SCHEME__NAME:
				setName(NAME_EDEFAULT);
				return;
			case OpenApiPackage.SECURITY_SCHEME__IN:
				setIn(IN_EDEFAULT);
				return;
			case OpenApiPackage.SECURITY_SCHEME__SCHEME:
				setScheme(SCHEME_EDEFAULT);
				return;
			case OpenApiPackage.SECURITY_SCHEME__BEARER_FORMAT:
				setBearerFormat(BEARER_FORMAT_EDEFAULT);
				return;
			case OpenApiPackage.SECURITY_SCHEME__FLOWS:
				setFlows((OAuthFlows)null);
				return;
			case OpenApiPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL:
				setOpenIdConnectUrl(OPEN_ID_CONNECT_URL_EDEFAULT);
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
			case OpenApiPackage.SECURITY_SCHEME__TYPE:
				return type != TYPE_EDEFAULT;
			case OpenApiPackage.SECURITY_SCHEME__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case OpenApiPackage.SECURITY_SCHEME__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case OpenApiPackage.SECURITY_SCHEME__IN:
				return in != IN_EDEFAULT;
			case OpenApiPackage.SECURITY_SCHEME__SCHEME:
				return SCHEME_EDEFAULT == null ? scheme != null : !SCHEME_EDEFAULT.equals(scheme);
			case OpenApiPackage.SECURITY_SCHEME__BEARER_FORMAT:
				return BEARER_FORMAT_EDEFAULT == null ? bearerFormat != null : !BEARER_FORMAT_EDEFAULT.equals(bearerFormat);
			case OpenApiPackage.SECURITY_SCHEME__FLOWS:
				return flows != null;
			case OpenApiPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL:
				return OPEN_ID_CONNECT_URL_EDEFAULT == null ? openIdConnectUrl != null : !OPEN_ID_CONNECT_URL_EDEFAULT.equals(openIdConnectUrl);
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
		result.append(" (type: ");
		result.append(type);
		result.append(", description: ");
		result.append(description);
		result.append(", name: ");
		result.append(name);
		result.append(", in: ");
		result.append(in);
		result.append(", scheme: ");
		result.append(scheme);
		result.append(", bearerFormat: ");
		result.append(bearerFormat);
		result.append(", openIdConnectUrl: ");
		result.append(openIdConnectUrl);
		result.append(')');
		return result.toString();
	}

} //SecuritySchemeImpl
