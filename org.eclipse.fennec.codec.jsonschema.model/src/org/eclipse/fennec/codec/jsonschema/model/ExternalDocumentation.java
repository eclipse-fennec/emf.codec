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
 * A representation of the model object '<em><b>External Documentation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getUrl <em>Url</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getExternalDocumentation()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface ExternalDocumentation extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getExternalDocumentation_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Url</em>' attribute.
	 * @see #setUrl(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getExternalDocumentation_Url()
	 * @model required="true"
	 * @generated
	 */
	String getUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getUrl <em>Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Url</em>' attribute.
	 * @see #getUrl()
	 * @generated
	 */
	void setUrl(String value);

	/**
	 * Returns the value of the '<em><b>Extensions</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Object},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extensions</em>' map.
	 * @see #isSetExtensions()
	 * @see #unsetExtensions()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getExternalDocumentation_Extensions()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.KeywordEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getExtensions <em>Extensions</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExtensions()
	 * @see #getExtensions()
	 * @generated
	 */
	void unsetExtensions();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation#getExtensions <em>Extensions</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Extensions</em>' map is set.
	 * @see #unsetExtensions()
	 * @see #getExtensions()
	 * @generated
	 */
	boolean isSetExtensions();

} // ExternalDocumentation
