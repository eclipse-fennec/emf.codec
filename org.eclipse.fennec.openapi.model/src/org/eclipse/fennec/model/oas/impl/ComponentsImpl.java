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
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.jsonschema.model.Schema;

import org.eclipse.fennec.model.oas.Callback;
import org.eclipse.fennec.model.oas.Components;
import org.eclipse.fennec.model.oas.Example;
import org.eclipse.fennec.model.oas.Header;
import org.eclipse.fennec.model.oas.Link;
import org.eclipse.fennec.model.oas.MediaType;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.Parameter;
import org.eclipse.fennec.model.oas.PathItem;
import org.eclipse.fennec.model.oas.RequestBody;
import org.eclipse.fennec.model.oas.Response;
import org.eclipse.fennec.model.oas.SecurityScheme;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Components</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getSchemas <em>Schemas</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getSchemasPackage <em>Schemas Package</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getResponses <em>Responses</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getRequestBodies <em>Request Bodies</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getHeaders <em>Headers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getSecuritySchemes <em>Security Schemes</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getLinks <em>Links</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getCallbacks <em>Callbacks</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getPathItems <em>Path Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getMediaTypes <em>Media Types</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ComponentsImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ComponentsImpl extends MinimalEObjectImpl.Container implements Components {
	/**
	 * The cached value of the '{@link #getSchemas() <em>Schemas</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchemas()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Schema> schemas;

	/**
	 * The cached value of the '{@link #getSchemasPackage() <em>Schemas Package</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchemasPackage()
	 * @generated
	 * @ordered
	 */
	protected EPackage schemasPackage;

	/**
	 * The cached value of the '{@link #getResponses() <em>Responses</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponses()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Response> responses;

	/**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Parameter> parameters;

	/**
	 * The cached value of the '{@link #getExamples() <em>Examples</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExamples()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Example> examples;

	/**
	 * The cached value of the '{@link #getRequestBodies() <em>Request Bodies</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequestBodies()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, RequestBody> requestBodies;

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
	 * The cached value of the '{@link #getSecuritySchemes() <em>Security Schemes</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSecuritySchemes()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, SecurityScheme> securitySchemes;

	/**
	 * The cached value of the '{@link #getLinks() <em>Links</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLinks()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Link> links;

	/**
	 * The cached value of the '{@link #getCallbacks() <em>Callbacks</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCallbacks()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Callback> callbacks;

	/**
	 * The cached value of the '{@link #getPathItems() <em>Path Items</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPathItems()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, PathItem> pathItems;

	/**
	 * The cached value of the '{@link #getMediaTypes() <em>Media Types</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMediaTypes()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, MediaType> mediaTypes;

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
	protected ComponentsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OasPackage.Literals.COMPONENTS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Schema> getSchemas() {
		if (schemas == null) {
			schemas = new EcoreEMap<String,Schema>(OasPackage.Literals.SCHEMA_ENTRY, SchemaEntryImpl.class, this, OasPackage.COMPONENTS__SCHEMAS);
		}
		return schemas;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EPackage getSchemasPackage() {
		return schemasPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSchemasPackage(EPackage newSchemasPackage, NotificationChain msgs) {
		EPackage oldSchemasPackage = schemasPackage;
		schemasPackage = newSchemasPackage;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.COMPONENTS__SCHEMAS_PACKAGE, oldSchemasPackage, newSchemasPackage);
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
	public void setSchemasPackage(EPackage newSchemasPackage) {
		if (newSchemasPackage != schemasPackage) {
			NotificationChain msgs = null;
			if (schemasPackage != null)
				msgs = ((InternalEObject)schemasPackage).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.COMPONENTS__SCHEMAS_PACKAGE, null, msgs);
			if (newSchemasPackage != null)
				msgs = ((InternalEObject)newSchemasPackage).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.COMPONENTS__SCHEMAS_PACKAGE, null, msgs);
			msgs = basicSetSchemasPackage(newSchemasPackage, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.COMPONENTS__SCHEMAS_PACKAGE, newSchemasPackage, newSchemasPackage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Response> getResponses() {
		if (responses == null) {
			responses = new EcoreEMap<String,Response>(OasPackage.Literals.RESPONSE_ENTRY, ResponseEntryImpl.class, this, OasPackage.COMPONENTS__RESPONSES);
		}
		return responses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Parameter> getParameters() {
		if (parameters == null) {
			parameters = new EcoreEMap<String,Parameter>(OasPackage.Literals.PARAMETER_ENTRY, ParameterEntryImpl.class, this, OasPackage.COMPONENTS__PARAMETERS);
		}
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Example> getExamples() {
		if (examples == null) {
			examples = new EcoreEMap<String,Example>(OasPackage.Literals.EXAMPLE_ENTRY, ExampleEntryImpl.class, this, OasPackage.COMPONENTS__EXAMPLES);
		}
		return examples;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, RequestBody> getRequestBodies() {
		if (requestBodies == null) {
			requestBodies = new EcoreEMap<String,RequestBody>(OasPackage.Literals.REQUEST_BODY_ENTRY, RequestBodyEntryImpl.class, this, OasPackage.COMPONENTS__REQUEST_BODIES);
		}
		return requestBodies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Header> getHeaders() {
		if (headers == null) {
			headers = new EcoreEMap<String,Header>(OasPackage.Literals.HEADER_ENTRY, HeaderEntryImpl.class, this, OasPackage.COMPONENTS__HEADERS);
		}
		return headers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, SecurityScheme> getSecuritySchemes() {
		if (securitySchemes == null) {
			securitySchemes = new EcoreEMap<String,SecurityScheme>(OasPackage.Literals.SECURITY_SCHEME_ENTRY, SecuritySchemeEntryImpl.class, this, OasPackage.COMPONENTS__SECURITY_SCHEMES);
		}
		return securitySchemes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Link> getLinks() {
		if (links == null) {
			links = new EcoreEMap<String,Link>(OasPackage.Literals.LINK_ENTRY, LinkEntryImpl.class, this, OasPackage.COMPONENTS__LINKS);
		}
		return links;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Callback> getCallbacks() {
		if (callbacks == null) {
			callbacks = new EcoreEMap<String,Callback>(OasPackage.Literals.CALLBACK_ENTRY, CallbackEntryImpl.class, this, OasPackage.COMPONENTS__CALLBACKS);
		}
		return callbacks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, PathItem> getPathItems() {
		if (pathItems == null) {
			pathItems = new EcoreEMap<String,PathItem>(OasPackage.Literals.PATH_ITEM_ENTRY, PathItemEntryImpl.class, this, OasPackage.COMPONENTS__PATH_ITEMS);
		}
		return pathItems;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, MediaType> getMediaTypes() {
		if (mediaTypes == null) {
			mediaTypes = new EcoreEMap<String,MediaType>(OasPackage.Literals.MEDIA_TYPE_ENTRY, MediaTypeEntryImpl.class, this, OasPackage.COMPONENTS__MEDIA_TYPES);
		}
		return mediaTypes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap<String,Object>(OasPackage.Literals.EXTENSION_ENTRY, ExtensionEntryImpl.class, this, OasPackage.COMPONENTS__EXTENSIONS);
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
			case OasPackage.COMPONENTS__SCHEMAS:
				return ((InternalEList<?>)getSchemas()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__SCHEMAS_PACKAGE:
				return basicSetSchemasPackage(null, msgs);
			case OasPackage.COMPONENTS__RESPONSES:
				return ((InternalEList<?>)getResponses()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__PARAMETERS:
				return ((InternalEList<?>)getParameters()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__EXAMPLES:
				return ((InternalEList<?>)getExamples()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__REQUEST_BODIES:
				return ((InternalEList<?>)getRequestBodies()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__HEADERS:
				return ((InternalEList<?>)getHeaders()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__SECURITY_SCHEMES:
				return ((InternalEList<?>)getSecuritySchemes()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__LINKS:
				return ((InternalEList<?>)getLinks()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__CALLBACKS:
				return ((InternalEList<?>)getCallbacks()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__PATH_ITEMS:
				return ((InternalEList<?>)getPathItems()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__MEDIA_TYPES:
				return ((InternalEList<?>)getMediaTypes()).basicRemove(otherEnd, msgs);
			case OasPackage.COMPONENTS__EXTENSIONS:
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
			case OasPackage.COMPONENTS__SCHEMAS:
				if (coreType) return getSchemas();
				else return getSchemas().map();
			case OasPackage.COMPONENTS__SCHEMAS_PACKAGE:
				return getSchemasPackage();
			case OasPackage.COMPONENTS__RESPONSES:
				if (coreType) return getResponses();
				else return getResponses().map();
			case OasPackage.COMPONENTS__PARAMETERS:
				if (coreType) return getParameters();
				else return getParameters().map();
			case OasPackage.COMPONENTS__EXAMPLES:
				if (coreType) return getExamples();
				else return getExamples().map();
			case OasPackage.COMPONENTS__REQUEST_BODIES:
				if (coreType) return getRequestBodies();
				else return getRequestBodies().map();
			case OasPackage.COMPONENTS__HEADERS:
				if (coreType) return getHeaders();
				else return getHeaders().map();
			case OasPackage.COMPONENTS__SECURITY_SCHEMES:
				if (coreType) return getSecuritySchemes();
				else return getSecuritySchemes().map();
			case OasPackage.COMPONENTS__LINKS:
				if (coreType) return getLinks();
				else return getLinks().map();
			case OasPackage.COMPONENTS__CALLBACKS:
				if (coreType) return getCallbacks();
				else return getCallbacks().map();
			case OasPackage.COMPONENTS__PATH_ITEMS:
				if (coreType) return getPathItems();
				else return getPathItems().map();
			case OasPackage.COMPONENTS__MEDIA_TYPES:
				if (coreType) return getMediaTypes();
				else return getMediaTypes().map();
			case OasPackage.COMPONENTS__EXTENSIONS:
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
			case OasPackage.COMPONENTS__SCHEMAS:
				((EStructuralFeature.Setting)getSchemas()).set(newValue);
				return;
			case OasPackage.COMPONENTS__SCHEMAS_PACKAGE:
				setSchemasPackage((EPackage)newValue);
				return;
			case OasPackage.COMPONENTS__RESPONSES:
				((EStructuralFeature.Setting)getResponses()).set(newValue);
				return;
			case OasPackage.COMPONENTS__PARAMETERS:
				((EStructuralFeature.Setting)getParameters()).set(newValue);
				return;
			case OasPackage.COMPONENTS__EXAMPLES:
				((EStructuralFeature.Setting)getExamples()).set(newValue);
				return;
			case OasPackage.COMPONENTS__REQUEST_BODIES:
				((EStructuralFeature.Setting)getRequestBodies()).set(newValue);
				return;
			case OasPackage.COMPONENTS__HEADERS:
				((EStructuralFeature.Setting)getHeaders()).set(newValue);
				return;
			case OasPackage.COMPONENTS__SECURITY_SCHEMES:
				((EStructuralFeature.Setting)getSecuritySchemes()).set(newValue);
				return;
			case OasPackage.COMPONENTS__LINKS:
				((EStructuralFeature.Setting)getLinks()).set(newValue);
				return;
			case OasPackage.COMPONENTS__CALLBACKS:
				((EStructuralFeature.Setting)getCallbacks()).set(newValue);
				return;
			case OasPackage.COMPONENTS__PATH_ITEMS:
				((EStructuralFeature.Setting)getPathItems()).set(newValue);
				return;
			case OasPackage.COMPONENTS__MEDIA_TYPES:
				((EStructuralFeature.Setting)getMediaTypes()).set(newValue);
				return;
			case OasPackage.COMPONENTS__EXTENSIONS:
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
			case OasPackage.COMPONENTS__SCHEMAS:
				getSchemas().clear();
				return;
			case OasPackage.COMPONENTS__SCHEMAS_PACKAGE:
				setSchemasPackage((EPackage)null);
				return;
			case OasPackage.COMPONENTS__RESPONSES:
				getResponses().clear();
				return;
			case OasPackage.COMPONENTS__PARAMETERS:
				getParameters().clear();
				return;
			case OasPackage.COMPONENTS__EXAMPLES:
				getExamples().clear();
				return;
			case OasPackage.COMPONENTS__REQUEST_BODIES:
				getRequestBodies().clear();
				return;
			case OasPackage.COMPONENTS__HEADERS:
				getHeaders().clear();
				return;
			case OasPackage.COMPONENTS__SECURITY_SCHEMES:
				getSecuritySchemes().clear();
				return;
			case OasPackage.COMPONENTS__LINKS:
				getLinks().clear();
				return;
			case OasPackage.COMPONENTS__CALLBACKS:
				getCallbacks().clear();
				return;
			case OasPackage.COMPONENTS__PATH_ITEMS:
				getPathItems().clear();
				return;
			case OasPackage.COMPONENTS__MEDIA_TYPES:
				getMediaTypes().clear();
				return;
			case OasPackage.COMPONENTS__EXTENSIONS:
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
			case OasPackage.COMPONENTS__SCHEMAS:
				return schemas != null && !schemas.isEmpty();
			case OasPackage.COMPONENTS__SCHEMAS_PACKAGE:
				return schemasPackage != null;
			case OasPackage.COMPONENTS__RESPONSES:
				return responses != null && !responses.isEmpty();
			case OasPackage.COMPONENTS__PARAMETERS:
				return parameters != null && !parameters.isEmpty();
			case OasPackage.COMPONENTS__EXAMPLES:
				return examples != null && !examples.isEmpty();
			case OasPackage.COMPONENTS__REQUEST_BODIES:
				return requestBodies != null && !requestBodies.isEmpty();
			case OasPackage.COMPONENTS__HEADERS:
				return headers != null && !headers.isEmpty();
			case OasPackage.COMPONENTS__SECURITY_SCHEMES:
				return securitySchemes != null && !securitySchemes.isEmpty();
			case OasPackage.COMPONENTS__LINKS:
				return links != null && !links.isEmpty();
			case OasPackage.COMPONENTS__CALLBACKS:
				return callbacks != null && !callbacks.isEmpty();
			case OasPackage.COMPONENTS__PATH_ITEMS:
				return pathItems != null && !pathItems.isEmpty();
			case OasPackage.COMPONENTS__MEDIA_TYPES:
				return mediaTypes != null && !mediaTypes.isEmpty();
			case OasPackage.COMPONENTS__EXTENSIONS:
				return extensions != null && !extensions.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ComponentsImpl
