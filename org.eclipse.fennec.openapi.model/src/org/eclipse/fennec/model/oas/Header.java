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
 * A representation of the model object '<em><b>Header</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#isRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#isDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getExplode <em>Explode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getContent <em>Content</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Header#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Header extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of a Reference Object; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Header#getRef <em>Ref</em>}' attribute.
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
	 * @see #setTarget(Header)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Target()
	 * @model transient="true"
	 * @generated
	 */
	Header getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Header#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(Header value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Header#getDescription <em>Description</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Required()
	 * @model
	 * @generated
	 */
	boolean isRequired();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Header#isRequired <em>Required</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Deprecated()
	 * @model
	 * @generated
	 */
	boolean isDeprecated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Header#isDeprecated <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deprecated</em>' attribute.
	 * @see #isDeprecated()
	 * @generated
	 */
	void setDeprecated(boolean value);

	/**
	 * Returns the value of the '<em><b>Style</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.oas.ParameterStyle}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.ParameterStyle
	 * @see #setStyle(ParameterStyle)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Style()
	 * @model
	 * @generated
	 */
	ParameterStyle getStyle();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Header#getStyle <em>Style</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Explode()
	 * @model
	 * @generated
	 */
	Boolean getExplode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Header#getExplode <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Explode</em>' attribute.
	 * @see #getExplode()
	 * @generated
	 */
	void setExplode(Boolean value);

	/**
	 * Returns the value of the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' containment reference.
	 * @see #setSchema(SchemaReference)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Schema()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='oasSchema' valueWriterName='oasSchema'"
	 * @generated
	 */
	SchemaReference getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Header#getSchema <em>Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' containment reference.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(SchemaReference value);

	/**
	 * Returns the value of the '<em><b>Example</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Example</em>' attribute.
	 * @see #setExample(Object)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Example()
	 * @model
	 * @generated
	 */
	Object getExample();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Header#getExample <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Example</em>' attribute.
	 * @see #getExample()
	 * @generated
	 */
	void setExample(Object value);

	/**
	 * Returns the value of the '<em><b>Examples</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Example},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Examples</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Examples()
	 * @model mapType="org.eclipse.fennec.model.oas.ExampleEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Example&gt;"
	 * @generated
	 */
	EMap<String, Example> getExamples();

	/**
	 * Returns the value of the '<em><b>Content</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.MediaType},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content</em>' map.
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Content()
	 * @model mapType="org.eclipse.fennec.model.oas.MediaTypeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.MediaType&gt;"
	 * @generated
	 */
	EMap<String, MediaType> getContent();

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getHeader_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // Header
