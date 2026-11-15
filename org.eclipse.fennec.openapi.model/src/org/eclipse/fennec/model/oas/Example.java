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
 * A representation of the model object '<em><b>Example</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.Example#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Example#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Example#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Example#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Example#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Example#getDataValue <em>Data Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Example#getSerializedValue <em>Serialized Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Example#getExternalValue <em>External Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Example#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getExample()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Example extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of a Reference Object; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getExample_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Example#getRef <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' attribute.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(String value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The object an internal $ref points at in components; set when a description is loaded, never written.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(Example)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getExample_Target()
	 * @model transient="true"
	 * @generated
	 */
	Example getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Example#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(Example value);

	/**
	 * Returns the value of the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Summary</em>' attribute.
	 * @see #setSummary(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getExample_Summary()
	 * @model
	 * @generated
	 */
	String getSummary();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Example#getSummary <em>Summary</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getExample_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Example#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(Object)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getExample_Value()
	 * @model
	 * @generated
	 */
	Object getValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Example#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(Object value);

	/**
	 * Returns the value of the '<em><b>Data Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Value</em>' attribute.
	 * @see #setDataValue(Object)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getExample_DataValue()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	Object getDataValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Example#getDataValue <em>Data Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data Value</em>' attribute.
	 * @see #getDataValue()
	 * @generated
	 */
	void setDataValue(Object value);

	/**
	 * Returns the value of the '<em><b>Serialized Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Serialized Value</em>' attribute.
	 * @see #setSerializedValue(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getExample_SerializedValue()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	String getSerializedValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Example#getSerializedValue <em>Serialized Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Serialized Value</em>' attribute.
	 * @see #getSerializedValue()
	 * @generated
	 */
	void setSerializedValue(String value);

	/**
	 * Returns the value of the '<em><b>External Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External Value</em>' attribute.
	 * @see #setExternalValue(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getExample_ExternalValue()
	 * @model
	 * @generated
	 */
	String getExternalValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Example#getExternalValue <em>External Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>External Value</em>' attribute.
	 * @see #getExternalValue()
	 * @generated
	 */
	void setExternalValue(String value);

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getExample_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // Example
