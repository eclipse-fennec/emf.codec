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
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#getIn <em>In</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#isRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#isAllowEmptyValue <em>Allow Empty Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#isExplode <em>Explode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#isAllowReserved <em>Allow Reserved</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.openapi.Parameter#getContent <em>Content</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter()
 * @model
 * @generated
 */
@ProviderType
public interface Parameter extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#getRef <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' attribute.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>In</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.openapi.ParameterLocation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>In</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.ParameterLocation
	 * @see #setIn(ParameterLocation)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_In()
	 * @model required="true"
	 * @generated
	 */
	ParameterLocation getIn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#getIn <em>In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>In</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.ParameterLocation
	 * @see #getIn()
	 * @generated
	 */
	void setIn(ParameterLocation value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required</em>' attribute.
	 * @see #setRequired(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Required()
	 * @model
	 * @generated
	 */
	boolean isRequired();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#isRequired <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required</em>' attribute.
	 * @see #isRequired()
	 * @generated
	 */
	void setRequired(boolean value);

	/**
	 * Returns the value of the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deprecated</em>' attribute.
	 * @see #setDeprecated(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Deprecated()
	 * @model
	 * @generated
	 */
	boolean isDeprecated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#isDeprecated <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deprecated</em>' attribute.
	 * @see #isDeprecated()
	 * @generated
	 */
	void setDeprecated(boolean value);

	/**
	 * Returns the value of the '<em><b>Allow Empty Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Allow Empty Value</em>' attribute.
	 * @see #setAllowEmptyValue(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_AllowEmptyValue()
	 * @model
	 * @generated
	 */
	boolean isAllowEmptyValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#isAllowEmptyValue <em>Allow Empty Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Allow Empty Value</em>' attribute.
	 * @see #isAllowEmptyValue()
	 * @generated
	 */
	void setAllowEmptyValue(boolean value);

	/**
	 * Returns the value of the '<em><b>Style</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.openapi.ParameterStyle}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.ParameterStyle
	 * @see #setStyle(ParameterStyle)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Style()
	 * @model
	 * @generated
	 */
	ParameterStyle getStyle();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#getStyle <em>Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.fennec.model.openapi.ParameterStyle
	 * @see #getStyle()
	 * @generated
	 */
	void setStyle(ParameterStyle value);

	/**
	 * Returns the value of the '<em><b>Explode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Explode</em>' attribute.
	 * @see #setExplode(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Explode()
	 * @model
	 * @generated
	 */
	boolean isExplode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#isExplode <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Explode</em>' attribute.
	 * @see #isExplode()
	 * @generated
	 */
	void setExplode(boolean value);

	/**
	 * Returns the value of the '<em><b>Allow Reserved</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Allow Reserved</em>' attribute.
	 * @see #setAllowReserved(boolean)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_AllowReserved()
	 * @model
	 * @generated
	 */
	boolean isAllowReserved();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#isAllowReserved <em>Allow Reserved</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Allow Reserved</em>' attribute.
	 * @see #isAllowReserved()
	 * @generated
	 */
	void setAllowReserved(boolean value);

	/**
	 * Returns the value of the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' containment reference.
	 * @see #setSchema(Schema)
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Schema()
	 * @model containment="true"
	 * @generated
	 */
	Schema getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.openapi.Parameter#getSchema <em>Schema</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Examples()
	 * @model mapType="org.eclipse.fennec.model.openapi.ExampleEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.Example&gt;"
	 * @generated
	 */
	EMap<String, Example> getExamples();

	/**
	 * Returns the value of the '<em><b>Content</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.openapi.MediaType},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content</em>' map.
	 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameter_Content()
	 * @model mapType="org.eclipse.fennec.model.openapi.MediaTypeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.openapi.MediaType&gt;"
	 * @generated
	 */
	EMap<String, MediaType> getContent();

} // Parameter
