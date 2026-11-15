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

import org.eclipse.fennec.model.oas.Encoding;
import org.eclipse.fennec.model.oas.Header;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.ParameterStyle;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Encoding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.EncodingImpl#getContentType <em>Content Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.EncodingImpl#getHeaders <em>Headers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.EncodingImpl#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.EncodingImpl#getExplode <em>Explode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.EncodingImpl#isAllowReserved <em>Allow Reserved</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.EncodingImpl#getEncoding <em>Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.EncodingImpl#getPrefixEncoding <em>Prefix Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.EncodingImpl#getItemEncoding <em>Item Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.EncodingImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EncodingImpl extends MinimalEObjectImpl.Container implements Encoding {
	/**
	 * The default value of the '{@link #getContentType() <em>Content Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContentType()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTENT_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContentType() <em>Content Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContentType()
	 * @generated
	 * @ordered
	 */
	protected String contentType = CONTENT_TYPE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getHeaders() <em>Headers</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHeaders()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Header> headers;

	/**
	 * The default value of the '{@link #getStyle() <em>Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStyle()
	 * @generated
	 * @ordered
	 */
	protected static final ParameterStyle STYLE_EDEFAULT = ParameterStyle.NULL;

	/**
	 * The cached value of the '{@link #getStyle() <em>Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStyle()
	 * @generated
	 * @ordered
	 */
	protected ParameterStyle style = STYLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getExplode() <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExplode()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean EXPLODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExplode() <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExplode()
	 * @generated
	 * @ordered
	 */
	protected Boolean explode = EXPLODE_EDEFAULT;

	/**
	 * The default value of the '{@link #isAllowReserved() <em>Allow Reserved</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAllowReserved()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ALLOW_RESERVED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAllowReserved() <em>Allow Reserved</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAllowReserved()
	 * @generated
	 * @ordered
	 */
	protected boolean allowReserved = ALLOW_RESERVED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getEncoding() <em>Encoding</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEncoding()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Encoding> encoding;

	/**
	 * The cached value of the '{@link #getPrefixEncoding() <em>Prefix Encoding</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrefixEncoding()
	 * @generated
	 * @ordered
	 */
	protected EList<Encoding> prefixEncoding;

	/**
	 * The cached value of the '{@link #getItemEncoding() <em>Item Encoding</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItemEncoding()
	 * @generated
	 * @ordered
	 */
	protected Encoding itemEncoding;

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
	protected EncodingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OasPackage.Literals.ENCODING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContentType(String newContentType) {
		String oldContentType = contentType;
		contentType = newContentType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.ENCODING__CONTENT_TYPE, oldContentType, contentType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Header> getHeaders() {
		if (headers == null) {
			headers = new EcoreEMap<String,Header>(OasPackage.Literals.HEADER_ENTRY, HeaderEntryImpl.class, this, OasPackage.ENCODING__HEADERS);
		}
		return headers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ParameterStyle getStyle() {
		return style;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStyle(ParameterStyle newStyle) {
		ParameterStyle oldStyle = style;
		style = newStyle == null ? STYLE_EDEFAULT : newStyle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.ENCODING__STYLE, oldStyle, style));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getExplode() {
		return explode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExplode(Boolean newExplode) {
		Boolean oldExplode = explode;
		explode = newExplode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.ENCODING__EXPLODE, oldExplode, explode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isAllowReserved() {
		return allowReserved;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAllowReserved(boolean newAllowReserved) {
		boolean oldAllowReserved = allowReserved;
		allowReserved = newAllowReserved;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.ENCODING__ALLOW_RESERVED, oldAllowReserved, allowReserved));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Encoding> getEncoding() {
		if (encoding == null) {
			encoding = new EcoreEMap<String,Encoding>(OasPackage.Literals.ENCODING_ENTRY, EncodingEntryImpl.class, this, OasPackage.ENCODING__ENCODING);
		}
		return encoding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Encoding> getPrefixEncoding() {
		if (prefixEncoding == null) {
			prefixEncoding = new EObjectContainmentEList<Encoding>(Encoding.class, this, OasPackage.ENCODING__PREFIX_ENCODING);
		}
		return prefixEncoding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Encoding getItemEncoding() {
		return itemEncoding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetItemEncoding(Encoding newItemEncoding, NotificationChain msgs) {
		Encoding oldItemEncoding = itemEncoding;
		itemEncoding = newItemEncoding;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.ENCODING__ITEM_ENCODING, oldItemEncoding, newItemEncoding);
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
	public void setItemEncoding(Encoding newItemEncoding) {
		if (newItemEncoding != itemEncoding) {
			NotificationChain msgs = null;
			if (itemEncoding != null)
				msgs = ((InternalEObject)itemEncoding).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.ENCODING__ITEM_ENCODING, null, msgs);
			if (newItemEncoding != null)
				msgs = ((InternalEObject)newItemEncoding).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.ENCODING__ITEM_ENCODING, null, msgs);
			msgs = basicSetItemEncoding(newItemEncoding, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.ENCODING__ITEM_ENCODING, newItemEncoding, newItemEncoding));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap<String,Object>(OasPackage.Literals.EXTENSION_ENTRY, ExtensionEntryImpl.class, this, OasPackage.ENCODING__EXTENSIONS);
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
			case OasPackage.ENCODING__HEADERS:
				return ((InternalEList<?>)getHeaders()).basicRemove(otherEnd, msgs);
			case OasPackage.ENCODING__ENCODING:
				return ((InternalEList<?>)getEncoding()).basicRemove(otherEnd, msgs);
			case OasPackage.ENCODING__PREFIX_ENCODING:
				return ((InternalEList<?>)getPrefixEncoding()).basicRemove(otherEnd, msgs);
			case OasPackage.ENCODING__ITEM_ENCODING:
				return basicSetItemEncoding(null, msgs);
			case OasPackage.ENCODING__EXTENSIONS:
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
			case OasPackage.ENCODING__CONTENT_TYPE:
				return getContentType();
			case OasPackage.ENCODING__HEADERS:
				if (coreType) return getHeaders();
				else return getHeaders().map();
			case OasPackage.ENCODING__STYLE:
				return getStyle();
			case OasPackage.ENCODING__EXPLODE:
				return getExplode();
			case OasPackage.ENCODING__ALLOW_RESERVED:
				return isAllowReserved();
			case OasPackage.ENCODING__ENCODING:
				if (coreType) return getEncoding();
				else return getEncoding().map();
			case OasPackage.ENCODING__PREFIX_ENCODING:
				return getPrefixEncoding();
			case OasPackage.ENCODING__ITEM_ENCODING:
				return getItemEncoding();
			case OasPackage.ENCODING__EXTENSIONS:
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
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case OasPackage.ENCODING__CONTENT_TYPE:
				setContentType((String)newValue);
				return;
			case OasPackage.ENCODING__HEADERS:
				((EStructuralFeature.Setting)getHeaders()).set(newValue);
				return;
			case OasPackage.ENCODING__STYLE:
				setStyle((ParameterStyle)newValue);
				return;
			case OasPackage.ENCODING__EXPLODE:
				setExplode((Boolean)newValue);
				return;
			case OasPackage.ENCODING__ALLOW_RESERVED:
				setAllowReserved((Boolean)newValue);
				return;
			case OasPackage.ENCODING__ENCODING:
				((EStructuralFeature.Setting)getEncoding()).set(newValue);
				return;
			case OasPackage.ENCODING__PREFIX_ENCODING:
				getPrefixEncoding().clear();
				getPrefixEncoding().addAll((Collection<? extends Encoding>)newValue);
				return;
			case OasPackage.ENCODING__ITEM_ENCODING:
				setItemEncoding((Encoding)newValue);
				return;
			case OasPackage.ENCODING__EXTENSIONS:
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
			case OasPackage.ENCODING__CONTENT_TYPE:
				setContentType(CONTENT_TYPE_EDEFAULT);
				return;
			case OasPackage.ENCODING__HEADERS:
				getHeaders().clear();
				return;
			case OasPackage.ENCODING__STYLE:
				setStyle(STYLE_EDEFAULT);
				return;
			case OasPackage.ENCODING__EXPLODE:
				setExplode(EXPLODE_EDEFAULT);
				return;
			case OasPackage.ENCODING__ALLOW_RESERVED:
				setAllowReserved(ALLOW_RESERVED_EDEFAULT);
				return;
			case OasPackage.ENCODING__ENCODING:
				getEncoding().clear();
				return;
			case OasPackage.ENCODING__PREFIX_ENCODING:
				getPrefixEncoding().clear();
				return;
			case OasPackage.ENCODING__ITEM_ENCODING:
				setItemEncoding((Encoding)null);
				return;
			case OasPackage.ENCODING__EXTENSIONS:
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
			case OasPackage.ENCODING__CONTENT_TYPE:
				return CONTENT_TYPE_EDEFAULT == null ? contentType != null : !CONTENT_TYPE_EDEFAULT.equals(contentType);
			case OasPackage.ENCODING__HEADERS:
				return headers != null && !headers.isEmpty();
			case OasPackage.ENCODING__STYLE:
				return style != STYLE_EDEFAULT;
			case OasPackage.ENCODING__EXPLODE:
				return EXPLODE_EDEFAULT == null ? explode != null : !EXPLODE_EDEFAULT.equals(explode);
			case OasPackage.ENCODING__ALLOW_RESERVED:
				return allowReserved != ALLOW_RESERVED_EDEFAULT;
			case OasPackage.ENCODING__ENCODING:
				return encoding != null && !encoding.isEmpty();
			case OasPackage.ENCODING__PREFIX_ENCODING:
				return prefixEncoding != null && !prefixEncoding.isEmpty();
			case OasPackage.ENCODING__ITEM_ENCODING:
				return itemEncoding != null;
			case OasPackage.ENCODING__EXTENSIONS:
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
		result.append(" (contentType: ");
		result.append(contentType);
		result.append(", style: ");
		result.append(style);
		result.append(", explode: ");
		result.append(explode);
		result.append(", allowReserved: ");
		result.append(allowReserved);
		result.append(')');
		return result.toString();
	}

} //EncodingImpl
