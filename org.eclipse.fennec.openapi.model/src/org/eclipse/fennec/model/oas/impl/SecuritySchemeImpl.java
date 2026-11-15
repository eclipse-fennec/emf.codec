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

import org.eclipse.fennec.model.oas.ApiKeyLocation;
import org.eclipse.fennec.model.oas.OAuthFlows;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.SecurityScheme;
import org.eclipse.fennec.model.oas.SecuritySchemeType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Security Scheme</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getIn <em>In</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getScheme <em>Scheme</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getBearerFormat <em>Bearer Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getFlows <em>Flows</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getOpenIdConnectUrl <em>Open Id Connect Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getOauth2MetadataUrl <em>Oauth2 Metadata Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.SecuritySchemeImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SecuritySchemeImpl extends MinimalEObjectImpl.Container implements SecurityScheme {
	/**
	 * The default value of the '{@link #getRef() <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRef()
	 * @generated
	 * @ordered
	 */
	protected static final String REF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRef() <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRef()
	 * @generated
	 * @ordered
	 */
	protected String ref = REF_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTarget()
	 * @generated
	 * @ordered
	 */
	protected SecurityScheme target;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final SecuritySchemeType TYPE_EDEFAULT = SecuritySchemeType.NULL;

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
	protected static final ApiKeyLocation IN_EDEFAULT = ApiKeyLocation.NULL;

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
	 * The default value of the '{@link #getOauth2MetadataUrl() <em>Oauth2 Metadata Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOauth2MetadataUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String OAUTH2_METADATA_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOauth2MetadataUrl() <em>Oauth2 Metadata Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOauth2MetadataUrl()
	 * @generated
	 * @ordered
	 */
	protected String oauth2MetadataUrl = OAUTH2_METADATA_URL_EDEFAULT;

	/**
	 * The default value of the '{@link #isDeprecated() <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDeprecated()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DEPRECATED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDeprecated() <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDeprecated()
	 * @generated
	 * @ordered
	 */
	protected boolean deprecated = DEPRECATED_EDEFAULT;

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
		return OasPackage.Literals.SECURITY_SCHEME;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRef() {
		return ref;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRef(String newRef) {
		String oldRef = ref;
		ref = newRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__REF, oldRef, ref));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SecurityScheme getTarget() {
		if (target != null && target.eIsProxy()) {
			InternalEObject oldTarget = (InternalEObject)target;
			target = (SecurityScheme)eResolveProxy(oldTarget);
			if (target != oldTarget) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, OasPackage.SECURITY_SCHEME__TARGET, oldTarget, target));
			}
		}
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SecurityScheme basicGetTarget() {
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTarget(SecurityScheme newTarget) {
		SecurityScheme oldTarget = target;
		target = newTarget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__TARGET, oldTarget, target));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__TYPE, oldType, type));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__DESCRIPTION, oldDescription, description));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__NAME, oldName, name));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__IN, oldIn, in));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__SCHEME, oldScheme, scheme));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__BEARER_FORMAT, oldBearerFormat, bearerFormat));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__FLOWS, oldFlows, newFlows);
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
				msgs = ((InternalEObject)flows).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.SECURITY_SCHEME__FLOWS, null, msgs);
			if (newFlows != null)
				msgs = ((InternalEObject)newFlows).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.SECURITY_SCHEME__FLOWS, null, msgs);
			msgs = basicSetFlows(newFlows, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__FLOWS, newFlows, newFlows));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL, oldOpenIdConnectUrl, openIdConnectUrl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getOauth2MetadataUrl() {
		return oauth2MetadataUrl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOauth2MetadataUrl(String newOauth2MetadataUrl) {
		String oldOauth2MetadataUrl = oauth2MetadataUrl;
		oauth2MetadataUrl = newOauth2MetadataUrl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__OAUTH2_METADATA_URL, oldOauth2MetadataUrl, oauth2MetadataUrl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isDeprecated() {
		return deprecated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeprecated(boolean newDeprecated) {
		boolean oldDeprecated = deprecated;
		deprecated = newDeprecated;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.SECURITY_SCHEME__DEPRECATED, oldDeprecated, deprecated));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap<String,Object>(OasPackage.Literals.EXTENSION_ENTRY, ExtensionEntryImpl.class, this, OasPackage.SECURITY_SCHEME__EXTENSIONS);
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
			case OasPackage.SECURITY_SCHEME__FLOWS:
				return basicSetFlows(null, msgs);
			case OasPackage.SECURITY_SCHEME__EXTENSIONS:
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
			case OasPackage.SECURITY_SCHEME__REF:
				return getRef();
			case OasPackage.SECURITY_SCHEME__TARGET:
				if (resolve) return getTarget();
				return basicGetTarget();
			case OasPackage.SECURITY_SCHEME__TYPE:
				return getType();
			case OasPackage.SECURITY_SCHEME__DESCRIPTION:
				return getDescription();
			case OasPackage.SECURITY_SCHEME__NAME:
				return getName();
			case OasPackage.SECURITY_SCHEME__IN:
				return getIn();
			case OasPackage.SECURITY_SCHEME__SCHEME:
				return getScheme();
			case OasPackage.SECURITY_SCHEME__BEARER_FORMAT:
				return getBearerFormat();
			case OasPackage.SECURITY_SCHEME__FLOWS:
				return getFlows();
			case OasPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL:
				return getOpenIdConnectUrl();
			case OasPackage.SECURITY_SCHEME__OAUTH2_METADATA_URL:
				return getOauth2MetadataUrl();
			case OasPackage.SECURITY_SCHEME__DEPRECATED:
				return isDeprecated();
			case OasPackage.SECURITY_SCHEME__EXTENSIONS:
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
			case OasPackage.SECURITY_SCHEME__REF:
				setRef((String)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__TARGET:
				setTarget((SecurityScheme)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__TYPE:
				setType((SecuritySchemeType)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__NAME:
				setName((String)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__IN:
				setIn((ApiKeyLocation)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__SCHEME:
				setScheme((String)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__BEARER_FORMAT:
				setBearerFormat((String)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__FLOWS:
				setFlows((OAuthFlows)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL:
				setOpenIdConnectUrl((String)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__OAUTH2_METADATA_URL:
				setOauth2MetadataUrl((String)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__DEPRECATED:
				setDeprecated((Boolean)newValue);
				return;
			case OasPackage.SECURITY_SCHEME__EXTENSIONS:
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
			case OasPackage.SECURITY_SCHEME__REF:
				setRef(REF_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__TARGET:
				setTarget((SecurityScheme)null);
				return;
			case OasPackage.SECURITY_SCHEME__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__NAME:
				setName(NAME_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__IN:
				setIn(IN_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__SCHEME:
				setScheme(SCHEME_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__BEARER_FORMAT:
				setBearerFormat(BEARER_FORMAT_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__FLOWS:
				setFlows((OAuthFlows)null);
				return;
			case OasPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL:
				setOpenIdConnectUrl(OPEN_ID_CONNECT_URL_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__OAUTH2_METADATA_URL:
				setOauth2MetadataUrl(OAUTH2_METADATA_URL_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__DEPRECATED:
				setDeprecated(DEPRECATED_EDEFAULT);
				return;
			case OasPackage.SECURITY_SCHEME__EXTENSIONS:
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
			case OasPackage.SECURITY_SCHEME__REF:
				return REF_EDEFAULT == null ? ref != null : !REF_EDEFAULT.equals(ref);
			case OasPackage.SECURITY_SCHEME__TARGET:
				return target != null;
			case OasPackage.SECURITY_SCHEME__TYPE:
				return type != TYPE_EDEFAULT;
			case OasPackage.SECURITY_SCHEME__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case OasPackage.SECURITY_SCHEME__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case OasPackage.SECURITY_SCHEME__IN:
				return in != IN_EDEFAULT;
			case OasPackage.SECURITY_SCHEME__SCHEME:
				return SCHEME_EDEFAULT == null ? scheme != null : !SCHEME_EDEFAULT.equals(scheme);
			case OasPackage.SECURITY_SCHEME__BEARER_FORMAT:
				return BEARER_FORMAT_EDEFAULT == null ? bearerFormat != null : !BEARER_FORMAT_EDEFAULT.equals(bearerFormat);
			case OasPackage.SECURITY_SCHEME__FLOWS:
				return flows != null;
			case OasPackage.SECURITY_SCHEME__OPEN_ID_CONNECT_URL:
				return OPEN_ID_CONNECT_URL_EDEFAULT == null ? openIdConnectUrl != null : !OPEN_ID_CONNECT_URL_EDEFAULT.equals(openIdConnectUrl);
			case OasPackage.SECURITY_SCHEME__OAUTH2_METADATA_URL:
				return OAUTH2_METADATA_URL_EDEFAULT == null ? oauth2MetadataUrl != null : !OAUTH2_METADATA_URL_EDEFAULT.equals(oauth2MetadataUrl);
			case OasPackage.SECURITY_SCHEME__DEPRECATED:
				return deprecated != DEPRECATED_EDEFAULT;
			case OasPackage.SECURITY_SCHEME__EXTENSIONS:
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
		result.append(" (ref: ");
		result.append(ref);
		result.append(", type: ");
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
		result.append(", oauth2MetadataUrl: ");
		result.append(oauth2MetadataUrl);
		result.append(", deprecated: ");
		result.append(deprecated);
		result.append(')');
		return result.toString();
	}

} //SecuritySchemeImpl
