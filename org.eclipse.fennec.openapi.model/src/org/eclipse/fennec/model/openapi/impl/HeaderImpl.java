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

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.model.openapi.Example;
import org.eclipse.fennec.model.openapi.Header;
import org.eclipse.fennec.model.openapi.MediaType;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.ParameterStyle;
import org.eclipse.fennec.model.openapi.Schema;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Header</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#isRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#isAllowEmptyValue <em>Allow Empty Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#isExplode <em>Explode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#isAllowReserved <em>Allow Reserved</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.impl.HeaderImpl#getContent <em>Content</em>}</li>
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
	protected static final ParameterStyle STYLE_EDEFAULT = ParameterStyle.MATRIX;

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
	 * The default value of the '{@link #isExplode() <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExplode()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EXPLODE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isExplode() <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExplode()
	 * @generated
	 * @ordered
	 */
	protected boolean explode = EXPLODE_EDEFAULT;

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
	protected Schema schema;

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
		return OpenApiPackage.Literals.HEADER;
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
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__REF, oldRef, ref));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__DESCRIPTION, oldDescription, description));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__REQUIRED, oldRequired, required));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__DEPRECATED, oldDeprecated, deprecated));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__ALLOW_EMPTY_VALUE, oldAllowEmptyValue, allowEmptyValue));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__STYLE, oldStyle, style));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isExplode() {
		return explode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExplode(boolean newExplode) {
		boolean oldExplode = explode;
		explode = newExplode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__EXPLODE, oldExplode, explode));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__ALLOW_RESERVED, oldAllowReserved, allowReserved));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__SCHEMA, oldSchema, newSchema);
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
				msgs = ((InternalEObject)schema).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.HEADER__SCHEMA, null, msgs);
			if (newSchema != null)
				msgs = ((InternalEObject)newSchema).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenApiPackage.HEADER__SCHEMA, null, msgs);
			msgs = basicSetSchema(newSchema, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OpenApiPackage.HEADER__SCHEMA, newSchema, newSchema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Example> getExamples() {
		if (examples == null) {
			examples = new EcoreEMap<String,Example>(OpenApiPackage.Literals.EXAMPLE_ENTRY, ExampleEntryImpl.class, this, OpenApiPackage.HEADER__EXAMPLES);
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
			content = new EcoreEMap<String,MediaType>(OpenApiPackage.Literals.MEDIA_TYPE_ENTRY, MediaTypeEntryImpl.class, this, OpenApiPackage.HEADER__CONTENT);
		}
		return content;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OpenApiPackage.HEADER__SCHEMA:
				return basicSetSchema(null, msgs);
			case OpenApiPackage.HEADER__EXAMPLES:
				return ((InternalEList<?>)getExamples()).basicRemove(otherEnd, msgs);
			case OpenApiPackage.HEADER__CONTENT:
				return ((InternalEList<?>)getContent()).basicRemove(otherEnd, msgs);
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
			case OpenApiPackage.HEADER__REF:
				return getRef();
			case OpenApiPackage.HEADER__DESCRIPTION:
				return getDescription();
			case OpenApiPackage.HEADER__REQUIRED:
				return isRequired();
			case OpenApiPackage.HEADER__DEPRECATED:
				return isDeprecated();
			case OpenApiPackage.HEADER__ALLOW_EMPTY_VALUE:
				return isAllowEmptyValue();
			case OpenApiPackage.HEADER__STYLE:
				return getStyle();
			case OpenApiPackage.HEADER__EXPLODE:
				return isExplode();
			case OpenApiPackage.HEADER__ALLOW_RESERVED:
				return isAllowReserved();
			case OpenApiPackage.HEADER__SCHEMA:
				return getSchema();
			case OpenApiPackage.HEADER__EXAMPLES:
				if (coreType) return getExamples();
				else return getExamples().map();
			case OpenApiPackage.HEADER__CONTENT:
				if (coreType) return getContent();
				else return getContent().map();
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
			case OpenApiPackage.HEADER__REF:
				setRef((String)newValue);
				return;
			case OpenApiPackage.HEADER__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case OpenApiPackage.HEADER__REQUIRED:
				setRequired((Boolean)newValue);
				return;
			case OpenApiPackage.HEADER__DEPRECATED:
				setDeprecated((Boolean)newValue);
				return;
			case OpenApiPackage.HEADER__ALLOW_EMPTY_VALUE:
				setAllowEmptyValue((Boolean)newValue);
				return;
			case OpenApiPackage.HEADER__STYLE:
				setStyle((ParameterStyle)newValue);
				return;
			case OpenApiPackage.HEADER__EXPLODE:
				setExplode((Boolean)newValue);
				return;
			case OpenApiPackage.HEADER__ALLOW_RESERVED:
				setAllowReserved((Boolean)newValue);
				return;
			case OpenApiPackage.HEADER__SCHEMA:
				setSchema((Schema)newValue);
				return;
			case OpenApiPackage.HEADER__EXAMPLES:
				((EStructuralFeature.Setting)getExamples()).set(newValue);
				return;
			case OpenApiPackage.HEADER__CONTENT:
				((EStructuralFeature.Setting)getContent()).set(newValue);
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
			case OpenApiPackage.HEADER__REF:
				setRef(REF_EDEFAULT);
				return;
			case OpenApiPackage.HEADER__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case OpenApiPackage.HEADER__REQUIRED:
				setRequired(REQUIRED_EDEFAULT);
				return;
			case OpenApiPackage.HEADER__DEPRECATED:
				setDeprecated(DEPRECATED_EDEFAULT);
				return;
			case OpenApiPackage.HEADER__ALLOW_EMPTY_VALUE:
				setAllowEmptyValue(ALLOW_EMPTY_VALUE_EDEFAULT);
				return;
			case OpenApiPackage.HEADER__STYLE:
				setStyle(STYLE_EDEFAULT);
				return;
			case OpenApiPackage.HEADER__EXPLODE:
				setExplode(EXPLODE_EDEFAULT);
				return;
			case OpenApiPackage.HEADER__ALLOW_RESERVED:
				setAllowReserved(ALLOW_RESERVED_EDEFAULT);
				return;
			case OpenApiPackage.HEADER__SCHEMA:
				setSchema((Schema)null);
				return;
			case OpenApiPackage.HEADER__EXAMPLES:
				getExamples().clear();
				return;
			case OpenApiPackage.HEADER__CONTENT:
				getContent().clear();
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
			case OpenApiPackage.HEADER__REF:
				return REF_EDEFAULT == null ? ref != null : !REF_EDEFAULT.equals(ref);
			case OpenApiPackage.HEADER__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case OpenApiPackage.HEADER__REQUIRED:
				return required != REQUIRED_EDEFAULT;
			case OpenApiPackage.HEADER__DEPRECATED:
				return deprecated != DEPRECATED_EDEFAULT;
			case OpenApiPackage.HEADER__ALLOW_EMPTY_VALUE:
				return allowEmptyValue != ALLOW_EMPTY_VALUE_EDEFAULT;
			case OpenApiPackage.HEADER__STYLE:
				return style != STYLE_EDEFAULT;
			case OpenApiPackage.HEADER__EXPLODE:
				return explode != EXPLODE_EDEFAULT;
			case OpenApiPackage.HEADER__ALLOW_RESERVED:
				return allowReserved != ALLOW_RESERVED_EDEFAULT;
			case OpenApiPackage.HEADER__SCHEMA:
				return schema != null;
			case OpenApiPackage.HEADER__EXAMPLES:
				return examples != null && !examples.isEmpty();
			case OpenApiPackage.HEADER__CONTENT:
				return content != null && !content.isEmpty();
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
		result.append(')');
		return result.toString();
	}

} //HeaderImpl
