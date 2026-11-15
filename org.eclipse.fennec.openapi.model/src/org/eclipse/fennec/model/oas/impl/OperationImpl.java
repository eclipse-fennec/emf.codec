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

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.oas.Callback;
import org.eclipse.fennec.model.oas.ExternalDocumentation;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.Operation;
import org.eclipse.fennec.model.oas.Parameter;
import org.eclipse.fennec.model.oas.RequestBody;
import org.eclipse.fennec.model.oas.Response;
import org.eclipse.fennec.model.oas.SecurityRequirement;
import org.eclipse.fennec.model.oas.Server;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getTags <em>Tags</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getExternalDocs <em>External Docs</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getOperationId <em>Operation Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getRequestBody <em>Request Body</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getResponses <em>Responses</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getCallbacks <em>Callbacks</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getSecurity <em>Security</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getServers <em>Servers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getMethod <em>Method</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.OperationImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OperationImpl extends MinimalEObjectImpl.Container implements Operation {
	/**
	 * The cached value of the '{@link #getTags() <em>Tags</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTags()
	 * @generated
	 * @ordered
	 */
	protected EList<String> tags;

	/**
	 * The default value of the '{@link #getSummary() <em>Summary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSummary()
	 * @generated
	 * @ordered
	 */
	protected static final String SUMMARY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSummary() <em>Summary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSummary()
	 * @generated
	 * @ordered
	 */
	protected String summary = SUMMARY_EDEFAULT;

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
	 * The cached value of the '{@link #getExternalDocs() <em>External Docs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExternalDocs()
	 * @generated
	 * @ordered
	 */
	protected ExternalDocumentation externalDocs;

	/**
	 * The default value of the '{@link #getOperationId() <em>Operation Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperationId()
	 * @generated
	 * @ordered
	 */
	protected static final String OPERATION_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOperationId() <em>Operation Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperationId()
	 * @generated
	 * @ordered
	 */
	protected String operationId = OPERATION_ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> parameters;

	/**
	 * The cached value of the '{@link #getRequestBody() <em>Request Body</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequestBody()
	 * @generated
	 * @ordered
	 */
	protected RequestBody requestBody;

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
	 * The cached value of the '{@link #getCallbacks() <em>Callbacks</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCallbacks()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Callback> callbacks;

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
	 * The cached value of the '{@link #getSecurity() <em>Security</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSecurity()
	 * @generated
	 * @ordered
	 */
	protected EList<SecurityRequirement> security;

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
	 * The default value of the '{@link #getMethod() <em>Method</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMethod()
	 * @generated
	 * @ordered
	 */
	protected static final String METHOD_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMethod() <em>Method</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMethod()
	 * @generated
	 * @ordered
	 */
	protected String method = METHOD_EDEFAULT;

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
	protected OperationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OasPackage.Literals.OPERATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getTags() {
		if (tags == null) {
			tags = new EDataTypeUniqueEList<String>(String.class, this, OasPackage.OPERATION__TAGS);
		}
		return tags;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSummary() {
		return summary;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSummary(String newSummary) {
		String oldSummary = summary;
		summary = newSummary;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OPERATION__SUMMARY, oldSummary, summary));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OPERATION__DESCRIPTION, oldDescription, description));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.OPERATION__EXTERNAL_DOCS, oldExternalDocs, newExternalDocs);
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
				msgs = ((InternalEObject)externalDocs).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.OPERATION__EXTERNAL_DOCS, null, msgs);
			if (newExternalDocs != null)
				msgs = ((InternalEObject)newExternalDocs).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.OPERATION__EXTERNAL_DOCS, null, msgs);
			msgs = basicSetExternalDocs(newExternalDocs, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OPERATION__EXTERNAL_DOCS, newExternalDocs, newExternalDocs));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getOperationId() {
		return operationId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOperationId(String newOperationId) {
		String oldOperationId = operationId;
		operationId = newOperationId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OPERATION__OPERATION_ID, oldOperationId, operationId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Parameter> getParameters() {
		if (parameters == null) {
			parameters = new EObjectContainmentEList<Parameter>(Parameter.class, this, OasPackage.OPERATION__PARAMETERS);
		}
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public RequestBody getRequestBody() {
		return requestBody;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRequestBody(RequestBody newRequestBody, NotificationChain msgs) {
		RequestBody oldRequestBody = requestBody;
		requestBody = newRequestBody;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.OPERATION__REQUEST_BODY, oldRequestBody, newRequestBody);
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
	public void setRequestBody(RequestBody newRequestBody) {
		if (newRequestBody != requestBody) {
			NotificationChain msgs = null;
			if (requestBody != null)
				msgs = ((InternalEObject)requestBody).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.OPERATION__REQUEST_BODY, null, msgs);
			if (newRequestBody != null)
				msgs = ((InternalEObject)newRequestBody).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.OPERATION__REQUEST_BODY, null, msgs);
			msgs = basicSetRequestBody(newRequestBody, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OPERATION__REQUEST_BODY, newRequestBody, newRequestBody));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Response> getResponses() {
		if (responses == null) {
			responses = new EcoreEMap<String,Response>(OasPackage.Literals.RESPONSE_ENTRY, ResponseEntryImpl.class, this, OasPackage.OPERATION__RESPONSES);
		}
		return responses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Callback> getCallbacks() {
		if (callbacks == null) {
			callbacks = new EcoreEMap<String,Callback>(OasPackage.Literals.CALLBACK_ENTRY, CallbackEntryImpl.class, this, OasPackage.OPERATION__CALLBACKS);
		}
		return callbacks;
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OPERATION__DEPRECATED, oldDeprecated, deprecated));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<SecurityRequirement> getSecurity() {
		if (security == null) {
			security = new EObjectContainmentEList<SecurityRequirement>(SecurityRequirement.class, this, OasPackage.OPERATION__SECURITY);
		}
		return security;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Server> getServers() {
		if (servers == null) {
			servers = new EObjectContainmentEList<Server>(Server.class, this, OasPackage.OPERATION__SERVERS);
		}
		return servers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMethod() {
		return method;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMethod(String newMethod) {
		String oldMethod = method;
		method = newMethod;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.OPERATION__METHOD, oldMethod, method));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap<String,Object>(OasPackage.Literals.EXTENSION_ENTRY, ExtensionEntryImpl.class, this, OasPackage.OPERATION__EXTENSIONS);
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
			case OasPackage.OPERATION__EXTERNAL_DOCS:
				return basicSetExternalDocs(null, msgs);
			case OasPackage.OPERATION__PARAMETERS:
				return ((InternalEList<?>)getParameters()).basicRemove(otherEnd, msgs);
			case OasPackage.OPERATION__REQUEST_BODY:
				return basicSetRequestBody(null, msgs);
			case OasPackage.OPERATION__RESPONSES:
				return ((InternalEList<?>)getResponses()).basicRemove(otherEnd, msgs);
			case OasPackage.OPERATION__CALLBACKS:
				return ((InternalEList<?>)getCallbacks()).basicRemove(otherEnd, msgs);
			case OasPackage.OPERATION__SECURITY:
				return ((InternalEList<?>)getSecurity()).basicRemove(otherEnd, msgs);
			case OasPackage.OPERATION__SERVERS:
				return ((InternalEList<?>)getServers()).basicRemove(otherEnd, msgs);
			case OasPackage.OPERATION__EXTENSIONS:
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
			case OasPackage.OPERATION__TAGS:
				return getTags();
			case OasPackage.OPERATION__SUMMARY:
				return getSummary();
			case OasPackage.OPERATION__DESCRIPTION:
				return getDescription();
			case OasPackage.OPERATION__EXTERNAL_DOCS:
				return getExternalDocs();
			case OasPackage.OPERATION__OPERATION_ID:
				return getOperationId();
			case OasPackage.OPERATION__PARAMETERS:
				return getParameters();
			case OasPackage.OPERATION__REQUEST_BODY:
				return getRequestBody();
			case OasPackage.OPERATION__RESPONSES:
				if (coreType) return getResponses();
				else return getResponses().map();
			case OasPackage.OPERATION__CALLBACKS:
				if (coreType) return getCallbacks();
				else return getCallbacks().map();
			case OasPackage.OPERATION__DEPRECATED:
				return isDeprecated();
			case OasPackage.OPERATION__SECURITY:
				return getSecurity();
			case OasPackage.OPERATION__SERVERS:
				return getServers();
			case OasPackage.OPERATION__METHOD:
				return getMethod();
			case OasPackage.OPERATION__EXTENSIONS:
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
			case OasPackage.OPERATION__TAGS:
				getTags().clear();
				getTags().addAll((Collection<? extends String>)newValue);
				return;
			case OasPackage.OPERATION__SUMMARY:
				setSummary((String)newValue);
				return;
			case OasPackage.OPERATION__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case OasPackage.OPERATION__EXTERNAL_DOCS:
				setExternalDocs((ExternalDocumentation)newValue);
				return;
			case OasPackage.OPERATION__OPERATION_ID:
				setOperationId((String)newValue);
				return;
			case OasPackage.OPERATION__PARAMETERS:
				getParameters().clear();
				getParameters().addAll((Collection<? extends Parameter>)newValue);
				return;
			case OasPackage.OPERATION__REQUEST_BODY:
				setRequestBody((RequestBody)newValue);
				return;
			case OasPackage.OPERATION__RESPONSES:
				((EStructuralFeature.Setting)getResponses()).set(newValue);
				return;
			case OasPackage.OPERATION__CALLBACKS:
				((EStructuralFeature.Setting)getCallbacks()).set(newValue);
				return;
			case OasPackage.OPERATION__DEPRECATED:
				setDeprecated((Boolean)newValue);
				return;
			case OasPackage.OPERATION__SECURITY:
				getSecurity().clear();
				getSecurity().addAll((Collection<? extends SecurityRequirement>)newValue);
				return;
			case OasPackage.OPERATION__SERVERS:
				getServers().clear();
				getServers().addAll((Collection<? extends Server>)newValue);
				return;
			case OasPackage.OPERATION__METHOD:
				setMethod((String)newValue);
				return;
			case OasPackage.OPERATION__EXTENSIONS:
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
			case OasPackage.OPERATION__TAGS:
				getTags().clear();
				return;
			case OasPackage.OPERATION__SUMMARY:
				setSummary(SUMMARY_EDEFAULT);
				return;
			case OasPackage.OPERATION__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case OasPackage.OPERATION__EXTERNAL_DOCS:
				setExternalDocs((ExternalDocumentation)null);
				return;
			case OasPackage.OPERATION__OPERATION_ID:
				setOperationId(OPERATION_ID_EDEFAULT);
				return;
			case OasPackage.OPERATION__PARAMETERS:
				getParameters().clear();
				return;
			case OasPackage.OPERATION__REQUEST_BODY:
				setRequestBody((RequestBody)null);
				return;
			case OasPackage.OPERATION__RESPONSES:
				getResponses().clear();
				return;
			case OasPackage.OPERATION__CALLBACKS:
				getCallbacks().clear();
				return;
			case OasPackage.OPERATION__DEPRECATED:
				setDeprecated(DEPRECATED_EDEFAULT);
				return;
			case OasPackage.OPERATION__SECURITY:
				getSecurity().clear();
				return;
			case OasPackage.OPERATION__SERVERS:
				getServers().clear();
				return;
			case OasPackage.OPERATION__METHOD:
				setMethod(METHOD_EDEFAULT);
				return;
			case OasPackage.OPERATION__EXTENSIONS:
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
			case OasPackage.OPERATION__TAGS:
				return tags != null && !tags.isEmpty();
			case OasPackage.OPERATION__SUMMARY:
				return SUMMARY_EDEFAULT == null ? summary != null : !SUMMARY_EDEFAULT.equals(summary);
			case OasPackage.OPERATION__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case OasPackage.OPERATION__EXTERNAL_DOCS:
				return externalDocs != null;
			case OasPackage.OPERATION__OPERATION_ID:
				return OPERATION_ID_EDEFAULT == null ? operationId != null : !OPERATION_ID_EDEFAULT.equals(operationId);
			case OasPackage.OPERATION__PARAMETERS:
				return parameters != null && !parameters.isEmpty();
			case OasPackage.OPERATION__REQUEST_BODY:
				return requestBody != null;
			case OasPackage.OPERATION__RESPONSES:
				return responses != null && !responses.isEmpty();
			case OasPackage.OPERATION__CALLBACKS:
				return callbacks != null && !callbacks.isEmpty();
			case OasPackage.OPERATION__DEPRECATED:
				return deprecated != DEPRECATED_EDEFAULT;
			case OasPackage.OPERATION__SECURITY:
				return security != null && !security.isEmpty();
			case OasPackage.OPERATION__SERVERS:
				return servers != null && !servers.isEmpty();
			case OasPackage.OPERATION__METHOD:
				return METHOD_EDEFAULT == null ? method != null : !METHOD_EDEFAULT.equals(method);
			case OasPackage.OPERATION__EXTENSIONS:
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
		result.append(" (tags: ");
		result.append(tags);
		result.append(", summary: ");
		result.append(summary);
		result.append(", description: ");
		result.append(description);
		result.append(", operationId: ");
		result.append(operationId);
		result.append(", deprecated: ");
		result.append(deprecated);
		result.append(", method: ");
		result.append(method);
		result.append(')');
		return result.toString();
	}

} //OperationImpl
