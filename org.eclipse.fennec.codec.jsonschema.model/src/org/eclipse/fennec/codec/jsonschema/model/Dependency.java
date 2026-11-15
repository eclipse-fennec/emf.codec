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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dependency</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A draft-04 to 2019-09 dependency: a schema, or the names of the properties required with the property.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Dependency#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Dependency#getProperties <em>Properties</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getDependency()
 * @model
 * @generated
 */
@ProviderType
public interface Dependency extends EObject {
	/**
	 * Returns the value of the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' containment reference.
	 * @see #setSchema(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getDependency_Schema()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	Schema getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Dependency#getSchema <em>Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' containment reference.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(Schema value);

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties</em>' attribute list.
	 * @see #isSetProperties()
	 * @see #unsetProperties()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getDependency_Properties()
	 * @model unique="false" unsettable="true"
	 * @generated
	 */
	EList<String> getProperties();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Dependency#getProperties <em>Properties</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetProperties()
	 * @see #getProperties()
	 * @generated
	 */
	void unsetProperties();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Dependency#getProperties <em>Properties</em>}' attribute list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Properties</em>' attribute list is set.
	 * @see #unsetProperties()
	 * @see #getProperties()
	 * @generated
	 */
	boolean isSetProperties();

} // Dependency
