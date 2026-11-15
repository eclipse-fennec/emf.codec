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

import org.eclipse.fennec.model.oas.Example;
import org.eclipse.fennec.model.oas.Header;
import org.eclipse.fennec.model.oas.MediaType;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.ParameterStyle;
import org.eclipse.fennec.model.oas.SchemaReference;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Header</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#isRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#isAllowEmptyValue <em>Allow Empty Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getExplode <em>Explode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#isAllowReserved <em>Allow Reserved</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getContent <em>Content</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.HeaderImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HeaderImpl extends MinimalEObjectImpl.Container implements Header {
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
	protected Header target;

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
	 * The default value of the '{@link #isRequired() <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequired()
	 * @generated
	 * @ordered
	 */
	protected static final boolean REQUIRED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isRequired() <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequired()
	 * @generated
	 * @ordered
	 */
	protected boolean required = REQUIRED_EDEFAULT;

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
	 * The default value of the '{@link #isAllowEmptyValue() <em>Allow Empty Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAllowEmptyValue()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ALLOW_EMPTY_VALUE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAllowEmptyValue() <em>Allow Empty Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAllowEmptyValue()
	 * @generated
	 * @ordered
	 */
	protected boolean allowEmptyValue = ALLOW_EMPTY_VALUE_EDEFAULT;

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
	 * The cached value of the '{@link #getSchema() <em>Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected SchemaReference schema;

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
	protected HeaderImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OasPackage.Literals.HEADER;
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__REF, oldRef, ref));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Header getTarget() {
		if (target != null && target.eIsProxy()) {
			InternalEObject oldTarget = (InternalEObject)target;
			target = (Header)eResolveProxy(oldTarget);
			if (target != oldTarget) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, OasPackage.HEADER__TARGET, oldTarget, target));
			}
		}
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Header basicGetTarget() {
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTarget(Header newTarget) {
		Header oldTarget = target;
		target = newTarget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__TARGET, oldTarget, target));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRequired(boolean newRequired) {
		boolean oldRequired = required;
		required = newRequired;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__REQUIRED, oldRequired, required));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__DEPRECATED, oldDeprecated, deprecated));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isAllowEmptyValue() {
		return allowEmptyValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAllowEmptyValue(boolean newAllowEmptyValue) {
		boolean oldAllowEmptyValue = allowEmptyValue;
		allowEmptyValue = newAllowEmptyValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__ALLOW_EMPTY_VALUE, oldAllowEmptyValue, allowEmptyValue));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__STYLE, oldStyle, style));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__EXPLODE, oldExplode, explode));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__ALLOW_RESERVED, oldAllowReserved, allowReserved));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SchemaReference getSchema() {
		return schema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSchema(SchemaReference newSchema, NotificationChain msgs) {
		SchemaReference oldSchema = schema;
		schema = newSchema;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__SCHEMA, oldSchema, newSchema);
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
	public void setSchema(SchemaReference newSchema) {
		if (newSchema != schema) {
			NotificationChain msgs = null;
			if (schema != null)
				msgs = ((InternalEObject)schema).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.HEADER__SCHEMA, null, msgs);
			if (newSchema != null)
				msgs = ((InternalEObject)newSchema).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.HEADER__SCHEMA, null, msgs);
			msgs = basicSetSchema(newSchema, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__SCHEMA, newSchema, newSchema));
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.HEADER__EXAMPLE, oldExample, example));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Example> getExamples() {
		if (examples == null) {
			examples = new EcoreEMap<String,Example>(OasPackage.Literals.EXAMPLE_ENTRY, ExampleEntryImpl.class, this, OasPackage.HEADER__EXAMPLES);
		}
		return examples;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, MediaType> getContent() {
		if (content == null) {
			content = new EcoreEMap<String,MediaType>(OasPackage.Literals.MEDIA_TYPE_ENTRY, MediaTypeEntryImpl.class, this, OasPackage.HEADER__CONTENT);
		}
		return content;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap<String,Object>(OasPackage.Literals.EXTENSION_ENTRY, ExtensionEntryImpl.class, this, OasPackage.HEADER__EXTENSIONS);
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
			case OasPackage.HEADER__SCHEMA:
				return basicSetSchema(null, msgs);
			case OasPackage.HEADER__EXAMPLES:
				return ((InternalEList<?>)getExamples()).basicRemove(otherEnd, msgs);
			case OasPackage.HEADER__CONTENT:
				return ((InternalEList<?>)getContent()).basicRemove(otherEnd, msgs);
			case OasPackage.HEADER__EXTENSIONS:
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
			case OasPackage.HEADER__REF:
				return getRef();
			case OasPackage.HEADER__TARGET:
				if (resolve) return getTarget();
				return basicGetTarget();
			case OasPackage.HEADER__DESCRIPTION:
				return getDescription();
			case OasPackage.HEADER__REQUIRED:
				return isRequired();
			case OasPackage.HEADER__DEPRECATED:
				return isDeprecated();
			case OasPackage.HEADER__ALLOW_EMPTY_VALUE:
				return isAllowEmptyValue();
			case OasPackage.HEADER__STYLE:
				return getStyle();
			case OasPackage.HEADER__EXPLODE:
				return getExplode();
			case OasPackage.HEADER__ALLOW_RESERVED:
				return isAllowReserved();
			case OasPackage.HEADER__SCHEMA:
				return getSchema();
			case OasPackage.HEADER__EXAMPLE:
				return getExample();
			case OasPackage.HEADER__EXAMPLES:
				if (coreType) return getExamples();
				else return getExamples().map();
			case OasPackage.HEADER__CONTENT:
				if (coreType) return getContent();
				else return getContent().map();
			case OasPackage.HEADER__EXTENSIONS:
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
			case OasPackage.HEADER__REF:
				setRef((String)newValue);
				return;
			case OasPackage.HEADER__TARGET:
				setTarget((Header)newValue);
				return;
			case OasPackage.HEADER__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case OasPackage.HEADER__REQUIRED:
				setRequired((Boolean)newValue);
				return;
			case OasPackage.HEADER__DEPRECATED:
				setDeprecated((Boolean)newValue);
				return;
			case OasPackage.HEADER__ALLOW_EMPTY_VALUE:
				setAllowEmptyValue((Boolean)newValue);
				return;
			case OasPackage.HEADER__STYLE:
				setStyle((ParameterStyle)newValue);
				return;
			case OasPackage.HEADER__EXPLODE:
				setExplode((Boolean)newValue);
				return;
			case OasPackage.HEADER__ALLOW_RESERVED:
				setAllowReserved((Boolean)newValue);
				return;
			case OasPackage.HEADER__SCHEMA:
				setSchema((SchemaReference)newValue);
				return;
			case OasPackage.HEADER__EXAMPLE:
				setExample(newValue);
				return;
			case OasPackage.HEADER__EXAMPLES:
				((EStructuralFeature.Setting)getExamples()).set(newValue);
				return;
			case OasPackage.HEADER__CONTENT:
				((EStructuralFeature.Setting)getContent()).set(newValue);
				return;
			case OasPackage.HEADER__EXTENSIONS:
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
			case OasPackage.HEADER__REF:
				setRef(REF_EDEFAULT);
				return;
			case OasPackage.HEADER__TARGET:
				setTarget((Header)null);
				return;
			case OasPackage.HEADER__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case OasPackage.HEADER__REQUIRED:
				setRequired(REQUIRED_EDEFAULT);
				return;
			case OasPackage.HEADER__DEPRECATED:
				setDeprecated(DEPRECATED_EDEFAULT);
				return;
			case OasPackage.HEADER__ALLOW_EMPTY_VALUE:
				setAllowEmptyValue(ALLOW_EMPTY_VALUE_EDEFAULT);
				return;
			case OasPackage.HEADER__STYLE:
				setStyle(STYLE_EDEFAULT);
				return;
			case OasPackage.HEADER__EXPLODE:
				setExplode(EXPLODE_EDEFAULT);
				return;
			case OasPackage.HEADER__ALLOW_RESERVED:
				setAllowReserved(ALLOW_RESERVED_EDEFAULT);
				return;
			case OasPackage.HEADER__SCHEMA:
				setSchema((SchemaReference)null);
				return;
			case OasPackage.HEADER__EXAMPLE:
				setExample(EXAMPLE_EDEFAULT);
				return;
			case OasPackage.HEADER__EXAMPLES:
				getExamples().clear();
				return;
			case OasPackage.HEADER__CONTENT:
				getContent().clear();
				return;
			case OasPackage.HEADER__EXTENSIONS:
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
			case OasPackage.HEADER__REF:
				return REF_EDEFAULT == null ? ref != null : !REF_EDEFAULT.equals(ref);
			case OasPackage.HEADER__TARGET:
				return target != null;
			case OasPackage.HEADER__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case OasPackage.HEADER__REQUIRED:
				return required != REQUIRED_EDEFAULT;
			case OasPackage.HEADER__DEPRECATED:
				return deprecated != DEPRECATED_EDEFAULT;
			case OasPackage.HEADER__ALLOW_EMPTY_VALUE:
				return allowEmptyValue != ALLOW_EMPTY_VALUE_EDEFAULT;
			case OasPackage.HEADER__STYLE:
				return style != STYLE_EDEFAULT;
			case OasPackage.HEADER__EXPLODE:
				return EXPLODE_EDEFAULT == null ? explode != null : !EXPLODE_EDEFAULT.equals(explode);
			case OasPackage.HEADER__ALLOW_RESERVED:
				return allowReserved != ALLOW_RESERVED_EDEFAULT;
			case OasPackage.HEADER__SCHEMA:
				return schema != null;
			case OasPackage.HEADER__EXAMPLE:
				return EXAMPLE_EDEFAULT == null ? example != null : !EXAMPLE_EDEFAULT.equals(example);
			case OasPackage.HEADER__EXAMPLES:
				return examples != null && !examples.isEmpty();
			case OasPackage.HEADER__CONTENT:
				return content != null && !content.isEmpty();
			case OasPackage.HEADER__EXTENSIONS:
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
		result.append(", description: ");
		result.append(description);
		result.append(", required: ");
		result.append(required);
		result.append(", deprecated: ");
		result.append(deprecated);
		result.append(", allowEmptyValue: ");
		result.append(allowEmptyValue);
		result.append(", style: ");
		result.append(style);
		result.append(", explode: ");
		result.append(explode);
		result.append(", allowReserved: ");
		result.append(allowReserved);
		result.append(", example: ");
		result.append(example);
		result.append(')');
		return result.toString();
	}

} //HeaderImpl
