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
package org.eclipse.fennec.model.openapi;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Media Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.MediaType#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.MediaType#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.MediaType#getEncoding <em>Encoding</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getMediaType()
 * @model
 * @generated
 */
@ProviderType
public interface MediaType extends EObject {
	/**
	 * Returns the value of the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' containment reference.
	 * @see #setSchema(Schema)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getMediaType_Schema()
	 * @model containment="true"
	 * @generated
	 */
	Schema getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.MediaType#getSchema <em>Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' containment reference.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(Schema value);

	/**
	 * Returns the value of the '<em><b>Examples</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Example},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Examples</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getMediaType_Examples()
	 * @model mapType="org.eclipse.fennec.model.openapi.ExampleEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Example&gt;"
	 * @generated
	 */
	EMap<String, Example> getExamples();

	/**
	 * Returns the value of the '<em><b>Encoding</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.Encoding},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Encoding</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getMediaType_Encoding()
	 * @model mapType="org.eclipse.fennec.model.openapi.EncodingEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Encoding&gt;"
	 * @generated
	 */
	EMap<String, Encoding> getEncoding();

} // MediaType
