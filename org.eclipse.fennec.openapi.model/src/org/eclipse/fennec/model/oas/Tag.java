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
package org.eclipse.fennec.model.oas;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tag</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.Tag#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Tag#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Tag#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Tag#getExternalDocs <em>External Docs</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Tag#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Tag#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Tag#getParentTag <em>Parent Tag</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Tag#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getTag()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Tag extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getTag_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Tag#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Summary</em>' attribute.
	 * @see #setSummary(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getTag_Summary()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	String getSummary();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Tag#getSummary <em>Summary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Summary</em>' attribute.
	 * @see #getSummary()
	 * @generated
	 */
	void setSummary(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getTag_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Tag#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External Docs</em>' containment reference.
	 * @see #setExternalDocs(ExternalDocumentation)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getTag_ExternalDocs()
	 * @model containment="true"
	 * @generated
	 */
	ExternalDocumentation getExternalDocs();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Tag#getExternalDocs <em>External Docs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>External Docs</em>' containment reference.
	 * @see #getExternalDocs()
	 * @generated
	 */
	void setExternalDocs(ExternalDocumentation value);

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent</em>' attribute.
	 * @see #setParent(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getTag_Parent()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	String getParent();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Tag#getParent <em>Parent</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' attribute.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(String value);

	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see #setKind(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getTag_Kind()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	String getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Tag#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see #getKind()
	 * @generated
	 */
	void setKind(String value);

	/**
	 * Returns the value of the '<em><b>Parent Tag</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The tag parent names; set when a description is loaded, never written.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Parent Tag</em>' reference.
	 * @see #setParentTag(Tag)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getTag_ParentTag()
	 * @model transient="true"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	Tag getParentTag();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Tag#getParentTag <em>Parent Tag</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Tag</em>' reference.
	 * @see #getParentTag()
	 * @generated
	 */
	void setParentTag(Tag value);

	/**
	 * Returns the value of the '<em><b>Extensions</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Object},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Specification Extensions (x-...): name to JSON value.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Extensions</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getTag_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // Tag
