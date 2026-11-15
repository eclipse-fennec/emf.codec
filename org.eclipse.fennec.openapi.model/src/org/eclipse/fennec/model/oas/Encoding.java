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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Encoding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.Encoding#getContentType <em>Content Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Encoding#getHeaders <em>Headers</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Encoding#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Encoding#getExplode <em>Explode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Encoding#isAllowReserved <em>Allow Reserved</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Encoding#getEncoding <em>Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Encoding#getPrefixEncoding <em>Prefix Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Encoding#getItemEncoding <em>Item Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Encoding#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Encoding extends EObject {
	/**
	 * Returns the value of the '<em><b>Content Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content Type</em>' attribute.
	 * @see #setContentType(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding_ContentType()
	 * @model
	 * @generated
	 */
	String getContentType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Encoding#getContentType <em>Content Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content Type</em>' attribute.
	 * @see #getContentType()
	 * @generated
	 */
	void setContentType(String value);

	/**
	 * Returns the value of the '<em><b>Headers</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Header},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Headers</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding_Headers()
	 * @model mapType="org.eclipse.fennec.model.oas.HeaderEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Header&gt;"
	 * @generated
	 */
	EMap<String, Header> getHeaders();

	/**
	 * Returns the value of the '<em><b>Style</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.oas.ParameterStyle}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.ParameterStyle
	 * @see #setStyle(ParameterStyle)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding_Style()
	 * @model
	 * @generated
	 */
	ParameterStyle getStyle();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Encoding#getStyle <em>Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.ParameterStyle
	 * @see #getStyle()
	 * @generated
	 */
	void setStyle(ParameterStyle value);

	/**
	 * Returns the value of the '<em><b>Explode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Explode</em>' attribute.
	 * @see #setExplode(Boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding_Explode()
	 * @model
	 * @generated
	 */
	Boolean getExplode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Encoding#getExplode <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Explode</em>' attribute.
	 * @see #getExplode()
	 * @generated
	 */
	void setExplode(Boolean value);

	/**
	 * Returns the value of the '<em><b>Allow Reserved</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Allow Reserved</em>' attribute.
	 * @see #setAllowReserved(boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding_AllowReserved()
	 * @model
	 * @generated
	 */
	boolean isAllowReserved();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Encoding#isAllowReserved <em>Allow Reserved</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Allow Reserved</em>' attribute.
	 * @see #isAllowReserved()
	 * @generated
	 */
	void setAllowReserved(boolean value);

	/**
	 * Returns the value of the '<em><b>Encoding</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Encoding},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Encoding</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding_Encoding()
	 * @model mapType="org.eclipse.fennec.model.oas.EncodingEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Encoding&gt;"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	EMap<String, Encoding> getEncoding();

	/**
	 * Returns the value of the '<em><b>Prefix Encoding</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.Encoding}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Prefix Encoding</em>' containment reference list.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding_PrefixEncoding()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	EList<Encoding> getPrefixEncoding();

	/**
	 * Returns the value of the '<em><b>Item Encoding</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Item Encoding</em>' containment reference.
	 * @see #setItemEncoding(Encoding)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding_ItemEncoding()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	Encoding getItemEncoding();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Encoding#getItemEncoding <em>Item Encoding</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Item Encoding</em>' containment reference.
	 * @see #getItemEncoding()
	 * @generated
	 */
	void setItemEncoding(Encoding value);

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getEncoding_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // Encoding
