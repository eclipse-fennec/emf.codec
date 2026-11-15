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

import org.eclipse.fennec.codec.jsonschema.model.Schema;

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
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getIn <em>In</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getSummary <em>Summary</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getAllowEmptyValue <em>Allow Empty Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getExplode <em>Explode</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getAllowReserved <em>Allow Reserved</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getContent <em>Content</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.Parameter#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface Parameter extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of a Reference Object; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getRef <em>Ref</em>}' attribute.
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
	 * @see #setTarget(Parameter)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Target()
	 * @model transient="true"
	 * @generated
	 */
	Parameter getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(Parameter value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>In</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.oas.ParameterLocation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>In</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.ParameterLocation
	 * @see #setIn(ParameterLocation)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_In()
	 * @model
	 * @generated
	 */
	ParameterLocation getIn();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getIn <em>In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>In</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.ParameterLocation
	 * @see #getIn()
	 * @generated
	 */
	void setIn(ParameterLocation value);

	/**
	 * Returns the value of the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The summary of a Reference Object: overrides the summary of the referenced component.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Summary</em>' attribute.
	 * @see #setSummary(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Summary()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.1'"
	 * @generated
	 */
	String getSummary();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getSummary <em>Summary</em>}' attribute.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getDescription <em>Description</em>}' attribute.
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
	 * @see #isSetRequired()
	 * @see #unsetRequired()
	 * @see #setRequired(Boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Required()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeDefault='true'"
	 * @generated
	 */
	Boolean getRequired();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getRequired <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required</em>' attribute.
	 * @see #isSetRequired()
	 * @see #unsetRequired()
	 * @see #getRequired()
	 * @generated
	 */
	void setRequired(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getRequired <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRequired()
	 * @see #getRequired()
	 * @see #setRequired(Boolean)
	 * @generated
	 */
	void unsetRequired();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getRequired <em>Required</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Required</em>' attribute is set.
	 * @see #unsetRequired()
	 * @see #getRequired()
	 * @see #setRequired(Boolean)
	 * @generated
	 */
	boolean isSetRequired();

	/**
	 * Returns the value of the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deprecated</em>' attribute.
	 * @see #isSetDeprecated()
	 * @see #unsetDeprecated()
	 * @see #setDeprecated(Boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Deprecated()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeDefault='true'"
	 * @generated
	 */
	Boolean getDeprecated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getDeprecated <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deprecated</em>' attribute.
	 * @see #isSetDeprecated()
	 * @see #unsetDeprecated()
	 * @see #getDeprecated()
	 * @generated
	 */
	void setDeprecated(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getDeprecated <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDeprecated()
	 * @see #getDeprecated()
	 * @see #setDeprecated(Boolean)
	 * @generated
	 */
	void unsetDeprecated();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getDeprecated <em>Deprecated</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Deprecated</em>' attribute is set.
	 * @see #unsetDeprecated()
	 * @see #getDeprecated()
	 * @see #setDeprecated(Boolean)
	 * @generated
	 */
	boolean isSetDeprecated();

	/**
	 * Returns the value of the '<em><b>Allow Empty Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Allow Empty Value</em>' attribute.
	 * @see #isSetAllowEmptyValue()
	 * @see #unsetAllowEmptyValue()
	 * @see #setAllowEmptyValue(Boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_AllowEmptyValue()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeDefault='true'"
	 * @generated
	 */
	Boolean getAllowEmptyValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getAllowEmptyValue <em>Allow Empty Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Allow Empty Value</em>' attribute.
	 * @see #isSetAllowEmptyValue()
	 * @see #unsetAllowEmptyValue()
	 * @see #getAllowEmptyValue()
	 * @generated
	 */
	void setAllowEmptyValue(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getAllowEmptyValue <em>Allow Empty Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAllowEmptyValue()
	 * @see #getAllowEmptyValue()
	 * @see #setAllowEmptyValue(Boolean)
	 * @generated
	 */
	void unsetAllowEmptyValue();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getAllowEmptyValue <em>Allow Empty Value</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Allow Empty Value</em>' attribute is set.
	 * @see #unsetAllowEmptyValue()
	 * @see #getAllowEmptyValue()
	 * @see #setAllowEmptyValue(Boolean)
	 * @generated
	 */
	boolean isSetAllowEmptyValue();

	/**
	 * Returns the value of the '<em><b>Style</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.model.oas.ParameterStyle}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Style</em>' attribute.
	 * @see org.eclipse.fennec.model.oas.ParameterStyle
	 * @see #setStyle(ParameterStyle)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Style()
	 * @model
	 * @generated
	 */
	ParameterStyle getStyle();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getStyle <em>Style</em>}' attribute.
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
	 * @see #isSetExplode()
	 * @see #unsetExplode()
	 * @see #setExplode(Boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Explode()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeDefault='true'"
	 * @generated
	 */
	Boolean getExplode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getExplode <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Explode</em>' attribute.
	 * @see #isSetExplode()
	 * @see #unsetExplode()
	 * @see #getExplode()
	 * @generated
	 */
	void setExplode(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getExplode <em>Explode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExplode()
	 * @see #getExplode()
	 * @see #setExplode(Boolean)
	 * @generated
	 */
	void unsetExplode();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getExplode <em>Explode</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Explode</em>' attribute is set.
	 * @see #unsetExplode()
	 * @see #getExplode()
	 * @see #setExplode(Boolean)
	 * @generated
	 */
	boolean isSetExplode();

	/**
	 * Returns the value of the '<em><b>Allow Reserved</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Allow Reserved</em>' attribute.
	 * @see #isSetAllowReserved()
	 * @see #unsetAllowReserved()
	 * @see #setAllowReserved(Boolean)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_AllowReserved()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeDefault='true'"
	 * @generated
	 */
	Boolean getAllowReserved();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getAllowReserved <em>Allow Reserved</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Allow Reserved</em>' attribute.
	 * @see #isSetAllowReserved()
	 * @see #unsetAllowReserved()
	 * @see #getAllowReserved()
	 * @generated
	 */
	void setAllowReserved(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getAllowReserved <em>Allow Reserved</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAllowReserved()
	 * @see #getAllowReserved()
	 * @see #setAllowReserved(Boolean)
	 * @generated
	 */
	void unsetAllowReserved();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getAllowReserved <em>Allow Reserved</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Allow Reserved</em>' attribute is set.
	 * @see #unsetAllowReserved()
	 * @see #getAllowReserved()
	 * @see #setAllowReserved(Boolean)
	 * @generated
	 */
	boolean isSetAllowReserved();

	/**
	 * Returns the value of the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' containment reference.
	 * @see #setSchema(Schema)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Schema()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	Schema getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getSchema <em>Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' containment reference.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(Schema value);

	/**
	 * Returns the value of the '<em><b>Example</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Example</em>' attribute.
	 * @see #isSetExample()
	 * @see #unsetExample()
	 * @see #setExample(Object)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Example()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeNull='true'"
	 * @generated
	 */
	Object getExample();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getExample <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Example</em>' attribute.
	 * @see #isSetExample()
	 * @see #unsetExample()
	 * @see #getExample()
	 * @generated
	 */
	void setExample(Object value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getExample <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExample()
	 * @see #getExample()
	 * @see #setExample(Object)
	 * @generated
	 */
	void unsetExample();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getExample <em>Example</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Example</em>' attribute is set.
	 * @see #unsetExample()
	 * @see #getExample()
	 * @see #setExample(Object)
	 * @generated
	 */
	boolean isSetExample();

	/**
	 * Returns the value of the '<em><b>Examples</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Example},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Examples</em>' map.
	 * @see #isSetExamples()
	 * @see #unsetExamples()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Examples()
	 * @model mapType="org.eclipse.fennec.model.oas.ExampleEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Example&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Example> getExamples();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getExamples <em>Examples</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExamples()
	 * @see #getExamples()
	 * @generated
	 */
	void unsetExamples();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getExamples <em>Examples</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Examples</em>' map is set.
	 * @see #unsetExamples()
	 * @see #getExamples()
	 * @generated
	 */
	boolean isSetExamples();

	/**
	 * Returns the value of the '<em><b>Content</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.MediaType},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content</em>' map.
	 * @see #isSetContent()
	 * @see #unsetContent()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Content()
	 * @model mapType="org.eclipse.fennec.model.oas.MediaTypeEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.MediaType&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, MediaType> getContent();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getContent <em>Content</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetContent()
	 * @see #getContent()
	 * @generated
	 */
	void unsetContent();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.Parameter#getContent <em>Content</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Content</em>' map is set.
	 * @see #unsetContent()
	 * @see #getContent()
	 * @generated
	 */
	boolean isSetContent();

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getParameter_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // Parameter
