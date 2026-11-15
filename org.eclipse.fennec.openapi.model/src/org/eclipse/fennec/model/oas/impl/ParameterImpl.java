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

import org.eclipse.fennec.codec.jsonschema.model.Schema;

import org.eclipse.fennec.model.oas.Example;
import org.eclipse.fennec.model.oas.MediaType;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.Parameter;
import org.eclipse.fennec.model.oas.ParameterLocation;
import org.eclipse.fennec.model.oas.ParameterStyle;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getIn <em>In</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getAllowEmptyValue <em>Allow Empty Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getExplode <em>Explode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getAllowReserved <em>Allow Reserved</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getContent <em>Content</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.ParameterImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParameterImpl extends MinimalEObjectImpl.Container implements Parameter {
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
	protected Parameter target;

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
	protected static final ParameterLocation IN_EDEFAULT = ParameterLocation.NULL;

	/**
	 * The cached value of the '{@link #getIn() <em>In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIn()
	 * @generated
	 * @ordered
	 */
	protected ParameterLocation in = IN_EDEFAULT;

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
	 * The default value of the '{@link #getRequired() <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequired()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean REQUIRED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRequired() <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequired()
	 * @generated
	 * @ordered
	 */
	protected Boolean required = REQUIRED_EDEFAULT;

	/**
	 * This is true if the Required attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean requiredESet;

	/**
	 * The default value of the '{@link #getDeprecated() <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeprecated()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean DEPRECATED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeprecated() <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeprecated()
	 * @generated
	 * @ordered
	 */
	protected Boolean deprecated = DEPRECATED_EDEFAULT;

	/**
	 * This is true if the Deprecated attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean deprecatedESet;

	/**
	 * The default value of the '{@link #getAllowEmptyValue() <em>Allow Empty Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAllowEmptyValue()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean ALLOW_EMPTY_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAllowEmptyValue() <em>Allow Empty Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAllowEmptyValue()
	 * @generated
	 * @ordered
	 */
	protected Boolean allowEmptyValue = ALLOW_EMPTY_VALUE_EDEFAULT;

	/**
	 * This is true if the Allow Empty Value attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean allowEmptyValueESet;

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
	 * This is true if the Explode attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean explodeESet;

	/**
	 * The default value of the '{@link #getAllowReserved() <em>Allow Reserved</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAllowReserved()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean ALLOW_RESERVED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAllowReserved() <em>Allow Reserved</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAllowReserved()
	 * @generated
	 * @ordered
	 */
	protected Boolean allowReserved = ALLOW_RESERVED_EDEFAULT;

	/**
	 * This is true if the Allow Reserved attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean allowReservedESet;

	/**
	 * The cached value of the '{@link #getSchema() <em>Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected Schema schema;

	/**
	 * The default value of the '{@link #getExample() <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExample()
	 * @generated
	 * @ordered
	 */
	protected static final Object EXAMPLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExample() <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExample()
	 * @generated
	 * @ordered
	 */
	protected Object example = EXAMPLE_EDEFAULT;

	/**
	 * This is true if the Example attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean exampleESet;

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
	 * The cached value of the '{@link #getContent() <em>Content</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, MediaType> content;

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
	protected ParameterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OasPackage.Literals.PARAMETER;
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__REF, oldRef, ref));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parameter getTarget() {
		if (target != null && target.eIsProxy()) {
			InternalEObject oldTarget = (InternalEObject)target;
			target = (Parameter)eResolveProxy(oldTarget);
			if (target != oldTarget) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, OasPackage.PARAMETER__TARGET, oldTarget, target));
			}
		}
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter basicGetTarget() {
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTarget(Parameter newTarget) {
		Parameter oldTarget = target;
		target = newTarget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__TARGET, oldTarget, target));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ParameterLocation getIn() {
		return in;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIn(ParameterLocation newIn) {
		ParameterLocation oldIn = in;
		in = newIn == null ? IN_EDEFAULT : newIn;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__IN, oldIn, in));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__SUMMARY, oldSummary, summary));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getRequired() {
		return required;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRequired(Boolean newRequired) {
		Boolean oldRequired = required;
		required = newRequired;
		boolean oldRequiredESet = requiredESet;
		requiredESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__REQUIRED, oldRequired, required, !oldRequiredESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetRequired() {
		Boolean oldRequired = required;
		boolean oldRequiredESet = requiredESet;
		required = REQUIRED_EDEFAULT;
		requiredESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, OasPackage.PARAMETER__REQUIRED, oldRequired, REQUIRED_EDEFAULT, oldRequiredESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetRequired() {
		return requiredESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getDeprecated() {
		return deprecated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeprecated(Boolean newDeprecated) {
		Boolean oldDeprecated = deprecated;
		deprecated = newDeprecated;
		boolean oldDeprecatedESet = deprecatedESet;
		deprecatedESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__DEPRECATED, oldDeprecated, deprecated, !oldDeprecatedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetDeprecated() {
		Boolean oldDeprecated = deprecated;
		boolean oldDeprecatedESet = deprecatedESet;
		deprecated = DEPRECATED_EDEFAULT;
		deprecatedESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, OasPackage.PARAMETER__DEPRECATED, oldDeprecated, DEPRECATED_EDEFAULT, oldDeprecatedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetDeprecated() {
		return deprecatedESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getAllowEmptyValue() {
		return allowEmptyValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAllowEmptyValue(Boolean newAllowEmptyValue) {
		Boolean oldAllowEmptyValue = allowEmptyValue;
		allowEmptyValue = newAllowEmptyValue;
		boolean oldAllowEmptyValueESet = allowEmptyValueESet;
		allowEmptyValueESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__ALLOW_EMPTY_VALUE, oldAllowEmptyValue, allowEmptyValue, !oldAllowEmptyValueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetAllowEmptyValue() {
		Boolean oldAllowEmptyValue = allowEmptyValue;
		boolean oldAllowEmptyValueESet = allowEmptyValueESet;
		allowEmptyValue = ALLOW_EMPTY_VALUE_EDEFAULT;
		allowEmptyValueESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, OasPackage.PARAMETER__ALLOW_EMPTY_VALUE, oldAllowEmptyValue, ALLOW_EMPTY_VALUE_EDEFAULT, oldAllowEmptyValueESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetAllowEmptyValue() {
		return allowEmptyValueESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__STYLE, oldStyle, style));
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
		boolean oldExplodeESet = explodeESet;
		explodeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__EXPLODE, oldExplode, explode, !oldExplodeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExplode() {
		Boolean oldExplode = explode;
		boolean oldExplodeESet = explodeESet;
		explode = EXPLODE_EDEFAULT;
		explodeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, OasPackage.PARAMETER__EXPLODE, oldExplode, EXPLODE_EDEFAULT, oldExplodeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExplode() {
		return explodeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean getAllowReserved() {
		return allowReserved;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAllowReserved(Boolean newAllowReserved) {
		Boolean oldAllowReserved = allowReserved;
		allowReserved = newAllowReserved;
		boolean oldAllowReservedESet = allowReservedESet;
		allowReservedESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__ALLOW_RESERVED, oldAllowReserved, allowReserved, !oldAllowReservedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetAllowReserved() {
		Boolean oldAllowReserved = allowReserved;
		boolean oldAllowReservedESet = allowReservedESet;
		allowReserved = ALLOW_RESERVED_EDEFAULT;
		allowReservedESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, OasPackage.PARAMETER__ALLOW_RESERVED, oldAllowReserved, ALLOW_RESERVED_EDEFAULT, oldAllowReservedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetAllowReserved() {
		return allowReservedESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getSchema() {
		return schema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSchema(Schema newSchema, NotificationChain msgs) {
		Schema oldSchema = schema;
		schema = newSchema;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__SCHEMA, oldSchema, newSchema);
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
	public void setSchema(Schema newSchema) {
		if (newSchema != schema) {
			NotificationChain msgs = null;
			if (schema != null)
				msgs = ((InternalEObject)schema).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.PARAMETER__SCHEMA, null, msgs);
			if (newSchema != null)
				msgs = ((InternalEObject)newSchema).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.PARAMETER__SCHEMA, null, msgs);
			msgs = basicSetSchema(newSchema, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__SCHEMA, newSchema, newSchema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getExample() {
		return example;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExample(Object newExample) {
		Object oldExample = example;
		example = newExample;
		boolean oldExampleESet = exampleESet;
		exampleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.PARAMETER__EXAMPLE, oldExample, example, !oldExampleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExample() {
		Object oldExample = example;
		boolean oldExampleESet = exampleESet;
		example = EXAMPLE_EDEFAULT;
		exampleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, OasPackage.PARAMETER__EXAMPLE, oldExample, EXAMPLE_EDEFAULT, oldExampleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExample() {
		return exampleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Example> getExamples() {
		if (examples == null) {
			examples = new EcoreEMap.Unsettable<String,Example>(OasPackage.Literals.EXAMPLE_ENTRY, ExampleEntryImpl.class, this, OasPackage.PARAMETER__EXAMPLES);
		}
		return examples;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetExamples() {
		if (examples != null) ((InternalEList.Unsettable<?>)examples).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetExamples() {
		return examples != null && ((InternalEList.Unsettable<?>)examples).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, MediaType> getContent() {
		if (content == null) {
			content = new EcoreEMap.Unsettable<String,MediaType>(OasPackage.Literals.MEDIA_TYPE_ENTRY, MediaTypeEntryImpl.class, this, OasPackage.PARAMETER__CONTENT);
		}
		return content;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetContent() {
		if (content != null) ((InternalEList.Unsettable<?>)content).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetContent() {
		return content != null && ((InternalEList.Unsettable<?>)content).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap<String,Object>(OasPackage.Literals.EXTENSION_ENTRY, ExtensionEntryImpl.class, this, OasPackage.PARAMETER__EXTENSIONS);
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
			case OasPackage.PARAMETER__SCHEMA:
				return basicSetSchema(null, msgs);
			case OasPackage.PARAMETER__EXAMPLES:
				return ((InternalEList<?>)getExamples()).basicRemove(otherEnd, msgs);
			case OasPackage.PARAMETER__CONTENT:
				return ((InternalEList<?>)getContent()).basicRemove(otherEnd, msgs);
			case OasPackage.PARAMETER__EXTENSIONS:
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
			case OasPackage.PARAMETER__REF:
				return getRef();
			case OasPackage.PARAMETER__TARGET:
				if (resolve) return getTarget();
				return basicGetTarget();
			case OasPackage.PARAMETER__NAME:
				return getName();
			case OasPackage.PARAMETER__IN:
				return getIn();
			case OasPackage.PARAMETER__SUMMARY:
				return getSummary();
			case OasPackage.PARAMETER__DESCRIPTION:
				return getDescription();
			case OasPackage.PARAMETER__REQUIRED:
				return getRequired();
			case OasPackage.PARAMETER__DEPRECATED:
				return getDeprecated();
			case OasPackage.PARAMETER__ALLOW_EMPTY_VALUE:
				return getAllowEmptyValue();
			case OasPackage.PARAMETER__STYLE:
				return getStyle();
			case OasPackage.PARAMETER__EXPLODE:
				return getExplode();
			case OasPackage.PARAMETER__ALLOW_RESERVED:
				return getAllowReserved();
			case OasPackage.PARAMETER__SCHEMA:
				return getSchema();
			case OasPackage.PARAMETER__EXAMPLE:
				return getExample();
			case OasPackage.PARAMETER__EXAMPLES:
				if (coreType) return getExamples();
				else return getExamples().map();
			case OasPackage.PARAMETER__CONTENT:
				if (coreType) return getContent();
				else return getContent().map();
			case OasPackage.PARAMETER__EXTENSIONS:
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
			case OasPackage.PARAMETER__REF:
				setRef((String)newValue);
				return;
			case OasPackage.PARAMETER__TARGET:
				setTarget((Parameter)newValue);
				return;
			case OasPackage.PARAMETER__NAME:
				setName((String)newValue);
				return;
			case OasPackage.PARAMETER__IN:
				setIn((ParameterLocation)newValue);
				return;
			case OasPackage.PARAMETER__SUMMARY:
				setSummary((String)newValue);
				return;
			case OasPackage.PARAMETER__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case OasPackage.PARAMETER__REQUIRED:
				setRequired((Boolean)newValue);
				return;
			case OasPackage.PARAMETER__DEPRECATED:
				setDeprecated((Boolean)newValue);
				return;
			case OasPackage.PARAMETER__ALLOW_EMPTY_VALUE:
				setAllowEmptyValue((Boolean)newValue);
				return;
			case OasPackage.PARAMETER__STYLE:
				setStyle((ParameterStyle)newValue);
				return;
			case OasPackage.PARAMETER__EXPLODE:
				setExplode((Boolean)newValue);
				return;
			case OasPackage.PARAMETER__ALLOW_RESERVED:
				setAllowReserved((Boolean)newValue);
				return;
			case OasPackage.PARAMETER__SCHEMA:
				setSchema((Schema)newValue);
				return;
			case OasPackage.PARAMETER__EXAMPLE:
				setExample(newValue);
				return;
			case OasPackage.PARAMETER__EXAMPLES:
				((EStructuralFeature.Setting)getExamples()).set(newValue);
				return;
			case OasPackage.PARAMETER__CONTENT:
				((EStructuralFeature.Setting)getContent()).set(newValue);
				return;
			case OasPackage.PARAMETER__EXTENSIONS:
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
			case OasPackage.PARAMETER__REF:
				setRef(REF_EDEFAULT);
				return;
			case OasPackage.PARAMETER__TARGET:
				setTarget((Parameter)null);
				return;
			case OasPackage.PARAMETER__NAME:
				setName(NAME_EDEFAULT);
				return;
			case OasPackage.PARAMETER__IN:
				setIn(IN_EDEFAULT);
				return;
			case OasPackage.PARAMETER__SUMMARY:
				setSummary(SUMMARY_EDEFAULT);
				return;
			case OasPackage.PARAMETER__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case OasPackage.PARAMETER__REQUIRED:
				unsetRequired();
				return;
			case OasPackage.PARAMETER__DEPRECATED:
				unsetDeprecated();
				return;
			case OasPackage.PARAMETER__ALLOW_EMPTY_VALUE:
				unsetAllowEmptyValue();
				return;
			case OasPackage.PARAMETER__STYLE:
				setStyle(STYLE_EDEFAULT);
				return;
			case OasPackage.PARAMETER__EXPLODE:
				unsetExplode();
				return;
			case OasPackage.PARAMETER__ALLOW_RESERVED:
				unsetAllowReserved();
				return;
			case OasPackage.PARAMETER__SCHEMA:
				setSchema((Schema)null);
				return;
			case OasPackage.PARAMETER__EXAMPLE:
				unsetExample();
				return;
			case OasPackage.PARAMETER__EXAMPLES:
				unsetExamples();
				return;
			case OasPackage.PARAMETER__CONTENT:
				unsetContent();
				return;
			case OasPackage.PARAMETER__EXTENSIONS:
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
			case OasPackage.PARAMETER__REF:
				return REF_EDEFAULT == null ? ref != null : !REF_EDEFAULT.equals(ref);
			case OasPackage.PARAMETER__TARGET:
				return target != null;
			case OasPackage.PARAMETER__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case OasPackage.PARAMETER__IN:
				return in != IN_EDEFAULT;
			case OasPackage.PARAMETER__SUMMARY:
				return SUMMARY_EDEFAULT == null ? summary != null : !SUMMARY_EDEFAULT.equals(summary);
			case OasPackage.PARAMETER__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case OasPackage.PARAMETER__REQUIRED:
				return isSetRequired();
			case OasPackage.PARAMETER__DEPRECATED:
				return isSetDeprecated();
			case OasPackage.PARAMETER__ALLOW_EMPTY_VALUE:
				return isSetAllowEmptyValue();
			case OasPackage.PARAMETER__STYLE:
				return style != STYLE_EDEFAULT;
			case OasPackage.PARAMETER__EXPLODE:
				return isSetExplode();
			case OasPackage.PARAMETER__ALLOW_RESERVED:
				return isSetAllowReserved();
			case OasPackage.PARAMETER__SCHEMA:
				return schema != null;
			case OasPackage.PARAMETER__EXAMPLE:
				return isSetExample();
			case OasPackage.PARAMETER__EXAMPLES:
				return isSetExamples();
			case OasPackage.PARAMETER__CONTENT:
				return isSetContent();
			case OasPackage.PARAMETER__EXTENSIONS:
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
		result.append(", name: ");
		result.append(name);
		result.append(", in: ");
		result.append(in);
		result.append(", summary: ");
		result.append(summary);
		result.append(", description: ");
		result.append(description);
		result.append(", required: ");
		if (requiredESet) result.append(required); else result.append("<unset>");
		result.append(", deprecated: ");
		if (deprecatedESet) result.append(deprecated); else result.append("<unset>");
		result.append(", allowEmptyValue: ");
		if (allowEmptyValueESet) result.append(allowEmptyValue); else result.append("<unset>");
		result.append(", style: ");
		result.append(style);
		result.append(", explode: ");
		if (explodeESet) result.append(explode); else result.append("<unset>");
		result.append(", allowReserved: ");
		if (allowReservedESet) result.append(allowReserved); else result.append("<unset>");
		result.append(", example: ");
		if (exampleESet) result.append(example); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //ParameterImpl
