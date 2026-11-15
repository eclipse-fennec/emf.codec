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
package org.eclipse.fennec.codec.jsonschema.model;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getWrapped <em>Wrapped</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getNodeType <em>Node Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getXml()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Xml extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getXml_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Namespace</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Namespace</em>' attribute.
	 * @see #setNamespace(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getXml_Namespace()
	 * @model
	 * @generated
	 */
	String getNamespace();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getNamespace <em>Namespace</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Namespace</em>' attribute.
	 * @see #getNamespace()
	 * @generated
	 */
	void setNamespace(String value);

	/**
	 * Returns the value of the '<em><b>Prefix</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Prefix</em>' attribute.
	 * @see #setPrefix(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getXml_Prefix()
	 * @model
	 * @generated
	 */
	String getPrefix();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getPrefix <em>Prefix</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Prefix</em>' attribute.
	 * @see #getPrefix()
	 * @generated
	 */
	void setPrefix(String value);

	/**
	 * Returns the value of the '<em><b>Attribute</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute</em>' attribute.
	 * @see #isSetAttribute()
	 * @see #unsetAttribute()
	 * @see #setAttribute(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getXml_Attribute()
	 * @model unsettable="true"
	 * @generated
	 */
	Boolean getAttribute();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getAttribute <em>Attribute</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attribute</em>' attribute.
	 * @see #isSetAttribute()
	 * @see #unsetAttribute()
	 * @see #getAttribute()
	 * @generated
	 */
	void setAttribute(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getAttribute <em>Attribute</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAttribute()
	 * @see #getAttribute()
	 * @see #setAttribute(Boolean)
	 * @generated
	 */
	void unsetAttribute();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getAttribute <em>Attribute</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Attribute</em>' attribute is set.
	 * @see #unsetAttribute()
	 * @see #getAttribute()
	 * @see #setAttribute(Boolean)
	 * @generated
	 */
	boolean isSetAttribute();

	/**
	 * Returns the value of the '<em><b>Wrapped</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wrapped</em>' attribute.
	 * @see #isSetWrapped()
	 * @see #unsetWrapped()
	 * @see #setWrapped(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getXml_Wrapped()
	 * @model unsettable="true"
	 * @generated
	 */
	Boolean getWrapped();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getWrapped <em>Wrapped</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wrapped</em>' attribute.
	 * @see #isSetWrapped()
	 * @see #unsetWrapped()
	 * @see #getWrapped()
	 * @generated
	 */
	void setWrapped(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getWrapped <em>Wrapped</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetWrapped()
	 * @see #getWrapped()
	 * @see #setWrapped(Boolean)
	 * @generated
	 */
	void unsetWrapped();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getWrapped <em>Wrapped</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Wrapped</em>' attribute is set.
	 * @see #unsetWrapped()
	 * @see #getWrapped()
	 * @see #setWrapped(Boolean)
	 * @generated
	 */
	boolean isSetWrapped();

	/**
	 * Returns the value of the '<em><b>Node Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Node Type</em>' attribute.
	 * @see #setNodeType(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getXml_NodeType()
	 * @model annotation="http://eclipse.org/fennec/jsonschema/model since='oas-3.2' dialect='oas'"
	 * @generated
	 */
	String getNodeType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getNodeType <em>Node Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Node Type</em>' attribute.
	 * @see #getNodeType()
	 * @generated
	 */
	void setNodeType(String value);

	/**
	 * Returns the value of the '<em><b>Extensions</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Object},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extensions</em>' map.
	 * @see #isSetExtensions()
	 * @see #unsetExtensions()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getXml_Extensions()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.KeywordEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getExtensions <em>Extensions</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExtensions()
	 * @see #getExtensions()
	 * @generated
	 */
	void unsetExtensions();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Xml#getExtensions <em>Extensions</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Extensions</em>' map is set.
	 * @see #unsetExtensions()
	 * @see #getExtensions()
	 * @generated
	 */
	boolean isSetExtensions();

} // Xml
