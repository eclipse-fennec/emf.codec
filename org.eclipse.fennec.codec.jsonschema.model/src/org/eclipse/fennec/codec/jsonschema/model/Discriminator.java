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
 * A representation of the model object '<em><b>Discriminator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getPropertyName <em>Property Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getMapping <em>Mapping</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getDefaultMapping <em>Default Mapping</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getDiscriminator()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Discriminator extends EObject {
	/**
	 * Returns the value of the '<em><b>Property Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property Name</em>' attribute.
	 * @see #setPropertyName(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getDiscriminator_PropertyName()
	 * @model required="true"
	 * @generated
	 */
	String getPropertyName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getPropertyName <em>Property Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Property Name</em>' attribute.
	 * @see #getPropertyName()
	 * @generated
	 */
	void setPropertyName(String value);

	/**
	 * Returns the value of the '<em><b>Mapping</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapping</em>' map.
	 * @see #isSetMapping()
	 * @see #unsetMapping()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getDiscriminator_Mapping()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.StringEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;"
	 * @generated
	 */
	EMap<String, String> getMapping();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getMapping <em>Mapping</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMapping()
	 * @see #getMapping()
	 * @generated
	 */
	void unsetMapping();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getMapping <em>Mapping</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Mapping</em>' map is set.
	 * @see #unsetMapping()
	 * @see #getMapping()
	 * @generated
	 */
	boolean isSetMapping();

	/**
	 * Returns the value of the '<em><b>Default Mapping</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default Mapping</em>' attribute.
	 * @see #setDefaultMapping(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getDiscriminator_DefaultMapping()
	 * @model annotation="http://eclipse.org/fennec/jsonschema/model since='oas-3.2' dialect='oas'"
	 * @generated
	 */
	String getDefaultMapping();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getDefaultMapping <em>Default Mapping</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default Mapping</em>' attribute.
	 * @see #getDefaultMapping()
	 * @generated
	 */
	void setDefaultMapping(String value);

	/**
	 * Returns the value of the '<em><b>Extensions</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Object},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extensions</em>' map.
	 * @see #isSetExtensions()
	 * @see #unsetExtensions()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getDiscriminator_Extensions()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.KeywordEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getExtensions <em>Extensions</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExtensions()
	 * @see #getExtensions()
	 * @generated
	 */
	void unsetExtensions();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator#getExtensions <em>Extensions</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Extensions</em>' map is set.
	 * @see #unsetExtensions()
	 * @see #getExtensions()
	 * @generated
	 */
	boolean isSetExtensions();

} // Discriminator
