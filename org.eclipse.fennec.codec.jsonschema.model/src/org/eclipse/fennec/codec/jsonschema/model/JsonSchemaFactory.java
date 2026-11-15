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

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage
 * @generated
 */
@ProviderType
public interface JsonSchemaFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	JsonSchemaFactory eINSTANCE = org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Schema</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Schema</em>'.
	 * @generated
	 */
	Schema createSchema();

	/**
	 * Returns a new object of class '<em>Discriminator</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Discriminator</em>'.
	 * @generated
	 */
	Discriminator createDiscriminator();

	/**
	 * Returns a new object of class '<em>Xml</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Xml</em>'.
	 * @generated
	 */
	Xml createXml();

	/**
	 * Returns a new object of class '<em>External Documentation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>External Documentation</em>'.
	 * @generated
	 */
	ExternalDocumentation createExternalDocumentation();

	/**
	 * Returns a new object of class '<em>Dependency</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Dependency</em>'.
	 * @generated
	 */
	Dependency createDependency();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	JsonSchemaPackage getJsonSchemaPackage();

} //JsonSchemaFactory
