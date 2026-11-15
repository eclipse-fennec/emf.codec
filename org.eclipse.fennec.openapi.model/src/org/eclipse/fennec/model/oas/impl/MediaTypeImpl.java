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

import org.eclipse.fennec.codec.jsonschema.model.Schema;

import org.eclipse.fennec.model.oas.Encoding;
import org.eclipse.fennec.model.oas.Example;
import org.eclipse.fennec.model.oas.MediaType;
import org.eclipse.fennec.model.oas.OasPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Media Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getItemSchema <em>Item Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getEncoding <em>Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getPrefixEncoding <em>Prefix Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getItemEncoding <em>Item Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.impl.MediaTypeImpl#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MediaTypeImpl extends MinimalEObjectImpl.Container implements MediaType {
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
	protected MediaType target;

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
	 * The cached value of the '{@link #getSchema() <em>Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected Schema schema;

	/**
	 * The cached value of the '{@link #getItemSchema() <em>Item Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItemSchema()
	 * @generated
	 * @ordered
	 */
	protected Schema itemSchema;

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
	protected MediaTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OasPackage.Literals.MEDIA_TYPE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__REF, oldRef, ref));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MediaType getTarget() {
		if (target != null && target.eIsProxy()) {
			InternalEObject oldTarget = (InternalEObject)target;
			target = (MediaType)eResolveProxy(oldTarget);
			if (target != oldTarget) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, OasPackage.MEDIA_TYPE__TARGET, oldTarget, target));
			}
		}
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MediaType basicGetTarget() {
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTarget(MediaType newTarget) {
		MediaType oldTarget = target;
		target = newTarget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__TARGET, oldTarget, target));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__SUMMARY, oldSummary, summary));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__DESCRIPTION, oldDescription, description));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__SCHEMA, oldSchema, newSchema);
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
				msgs = ((InternalEObject)schema).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.MEDIA_TYPE__SCHEMA, null, msgs);
			if (newSchema != null)
				msgs = ((InternalEObject)newSchema).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.MEDIA_TYPE__SCHEMA, null, msgs);
			msgs = basicSetSchema(newSchema, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__SCHEMA, newSchema, newSchema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Schema getItemSchema() {
		return itemSchema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetItemSchema(Schema newItemSchema, NotificationChain msgs) {
		Schema oldItemSchema = itemSchema;
		itemSchema = newItemSchema;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__ITEM_SCHEMA, oldItemSchema, newItemSchema);
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
	public void setItemSchema(Schema newItemSchema) {
		if (newItemSchema != itemSchema) {
			NotificationChain msgs = null;
			if (itemSchema != null)
				msgs = ((InternalEObject)itemSchema).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.MEDIA_TYPE__ITEM_SCHEMA, null, msgs);
			if (newItemSchema != null)
				msgs = ((InternalEObject)newItemSchema).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.MEDIA_TYPE__ITEM_SCHEMA, null, msgs);
			msgs = basicSetItemSchema(newItemSchema, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__ITEM_SCHEMA, newItemSchema, newItemSchema));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__EXAMPLE, oldExample, example, !oldExampleESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, OasPackage.MEDIA_TYPE__EXAMPLE, oldExample, EXAMPLE_EDEFAULT, oldExampleESet));
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
			examples = new EcoreEMap.Unsettable<String,Example>(OasPackage.Literals.EXAMPLE_ENTRY, ExampleEntryImpl.class, this, OasPackage.MEDIA_TYPE__EXAMPLES);
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
	public EMap<String, Encoding> getEncoding() {
		if (encoding == null) {
			encoding = new EcoreEMap.Unsettable<String,Encoding>(OasPackage.Literals.ENCODING_ENTRY, EncodingEntryImpl.class, this, OasPackage.MEDIA_TYPE__ENCODING);
		}
		return encoding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetEncoding() {
		if (encoding != null) ((InternalEList.Unsettable<?>)encoding).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetEncoding() {
		return encoding != null && ((InternalEList.Unsettable<?>)encoding).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Encoding> getPrefixEncoding() {
		if (prefixEncoding == null) {
			prefixEncoding = new EObjectContainmentEList.Unsettable<Encoding>(Encoding.class, this, OasPackage.MEDIA_TYPE__PREFIX_ENCODING);
		}
		return prefixEncoding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetPrefixEncoding() {
		if (prefixEncoding != null) ((InternalEList.Unsettable<?>)prefixEncoding).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetPrefixEncoding() {
		return prefixEncoding != null && ((InternalEList.Unsettable<?>)prefixEncoding).isSet();
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__ITEM_ENCODING, oldItemEncoding, newItemEncoding);
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
				msgs = ((InternalEObject)itemEncoding).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OasPackage.MEDIA_TYPE__ITEM_ENCODING, null, msgs);
			if (newItemEncoding != null)
				msgs = ((InternalEObject)newItemEncoding).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OasPackage.MEDIA_TYPE__ITEM_ENCODING, null, msgs);
			msgs = basicSetItemEncoding(newItemEncoding, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OasPackage.MEDIA_TYPE__ITEM_ENCODING, newItemEncoding, newItemEncoding));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EMap<String, Object> getExtensions() {
		if (extensions == null) {
			extensions = new EcoreEMap<String,Object>(OasPackage.Literals.EXTENSION_ENTRY, ExtensionEntryImpl.class, this, OasPackage.MEDIA_TYPE__EXTENSIONS);
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
			case OasPackage.MEDIA_TYPE__SCHEMA:
				return basicSetSchema(null, msgs);
			case OasPackage.MEDIA_TYPE__ITEM_SCHEMA:
				return basicSetItemSchema(null, msgs);
			case OasPackage.MEDIA_TYPE__EXAMPLES:
				return ((InternalEList<?>)getExamples()).basicRemove(otherEnd, msgs);
			case OasPackage.MEDIA_TYPE__ENCODING:
				return ((InternalEList<?>)getEncoding()).basicRemove(otherEnd, msgs);
			case OasPackage.MEDIA_TYPE__PREFIX_ENCODING:
				return ((InternalEList<?>)getPrefixEncoding()).basicRemove(otherEnd, msgs);
			case OasPackage.MEDIA_TYPE__ITEM_ENCODING:
				return basicSetItemEncoding(null, msgs);
			case OasPackage.MEDIA_TYPE__EXTENSIONS:
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
			case OasPackage.MEDIA_TYPE__REF:
				return getRef();
			case OasPackage.MEDIA_TYPE__TARGET:
				if (resolve) return getTarget();
				return basicGetTarget();
			case OasPackage.MEDIA_TYPE__SUMMARY:
				return getSummary();
			case OasPackage.MEDIA_TYPE__DESCRIPTION:
				return getDescription();
			case OasPackage.MEDIA_TYPE__SCHEMA:
				return getSchema();
			case OasPackage.MEDIA_TYPE__ITEM_SCHEMA:
				return getItemSchema();
			case OasPackage.MEDIA_TYPE__EXAMPLE:
				return getExample();
			case OasPackage.MEDIA_TYPE__EXAMPLES:
				if (coreType) return getExamples();
				else return getExamples().map();
			case OasPackage.MEDIA_TYPE__ENCODING:
				if (coreType) return getEncoding();
				else return getEncoding().map();
			case OasPackage.MEDIA_TYPE__PREFIX_ENCODING:
				return getPrefixEncoding();
			case OasPackage.MEDIA_TYPE__ITEM_ENCODING:
				return getItemEncoding();
			case OasPackage.MEDIA_TYPE__EXTENSIONS:
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
			case OasPackage.MEDIA_TYPE__REF:
				setRef((String)newValue);
				return;
			case OasPackage.MEDIA_TYPE__TARGET:
				setTarget((MediaType)newValue);
				return;
			case OasPackage.MEDIA_TYPE__SUMMARY:
				setSummary((String)newValue);
				return;
			case OasPackage.MEDIA_TYPE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case OasPackage.MEDIA_TYPE__SCHEMA:
				setSchema((Schema)newValue);
				return;
			case OasPackage.MEDIA_TYPE__ITEM_SCHEMA:
				setItemSchema((Schema)newValue);
				return;
			case OasPackage.MEDIA_TYPE__EXAMPLE:
				setExample(newValue);
				return;
			case OasPackage.MEDIA_TYPE__EXAMPLES:
				((EStructuralFeature.Setting)getExamples()).set(newValue);
				return;
			case OasPackage.MEDIA_TYPE__ENCODING:
				((EStructuralFeature.Setting)getEncoding()).set(newValue);
				return;
			case OasPackage.MEDIA_TYPE__PREFIX_ENCODING:
				getPrefixEncoding().clear();
				getPrefixEncoding().addAll((Collection<? extends Encoding>)newValue);
				return;
			case OasPackage.MEDIA_TYPE__ITEM_ENCODING:
				setItemEncoding((Encoding)newValue);
				return;
			case OasPackage.MEDIA_TYPE__EXTENSIONS:
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
			case OasPackage.MEDIA_TYPE__REF:
				setRef(REF_EDEFAULT);
				return;
			case OasPackage.MEDIA_TYPE__TARGET:
				setTarget((MediaType)null);
				return;
			case OasPackage.MEDIA_TYPE__SUMMARY:
				setSummary(SUMMARY_EDEFAULT);
				return;
			case OasPackage.MEDIA_TYPE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case OasPackage.MEDIA_TYPE__SCHEMA:
				setSchema((Schema)null);
				return;
			case OasPackage.MEDIA_TYPE__ITEM_SCHEMA:
				setItemSchema((Schema)null);
				return;
			case OasPackage.MEDIA_TYPE__EXAMPLE:
				unsetExample();
				return;
			case OasPackage.MEDIA_TYPE__EXAMPLES:
				unsetExamples();
				return;
			case OasPackage.MEDIA_TYPE__ENCODING:
				unsetEncoding();
				return;
			case OasPackage.MEDIA_TYPE__PREFIX_ENCODING:
				unsetPrefixEncoding();
				return;
			case OasPackage.MEDIA_TYPE__ITEM_ENCODING:
				setItemEncoding((Encoding)null);
				return;
			case OasPackage.MEDIA_TYPE__EXTENSIONS:
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
			case OasPackage.MEDIA_TYPE__REF:
				return REF_EDEFAULT == null ? ref != null : !REF_EDEFAULT.equals(ref);
			case OasPackage.MEDIA_TYPE__TARGET:
				return target != null;
			case OasPackage.MEDIA_TYPE__SUMMARY:
				return SUMMARY_EDEFAULT == null ? summary != null : !SUMMARY_EDEFAULT.equals(summary);
			case OasPackage.MEDIA_TYPE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case OasPackage.MEDIA_TYPE__SCHEMA:
				return schema != null;
			case OasPackage.MEDIA_TYPE__ITEM_SCHEMA:
				return itemSchema != null;
			case OasPackage.MEDIA_TYPE__EXAMPLE:
				return isSetExample();
			case OasPackage.MEDIA_TYPE__EXAMPLES:
				return isSetExamples();
			case OasPackage.MEDIA_TYPE__ENCODING:
				return isSetEncoding();
			case OasPackage.MEDIA_TYPE__PREFIX_ENCODING:
				return isSetPrefixEncoding();
			case OasPackage.MEDIA_TYPE__ITEM_ENCODING:
				return itemEncoding != null;
			case OasPackage.MEDIA_TYPE__EXTENSIONS:
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
		result.append(", summary: ");
		result.append(summary);
		result.append(", description: ");
		result.append(description);
		result.append(", example: ");
		if (exampleESet) result.append(example); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //MediaTypeImpl
