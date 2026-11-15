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

import org.eclipse.fennec.codec.jsonschema.model.Schema;

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
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getItemSchema <em>Item Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getEncoding <em>Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getPrefixEncoding <em>Prefix Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getItemEncoding <em>Item Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.model.oas.MediaType#getExtensions <em>Extensions</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='extensions'"
 * @generated
 */
@ProviderType
public interface MediaType extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $ref of a Reference Object; written as read.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getRef <em>Ref</em>}' attribute.
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
	 * @see #setTarget(MediaType)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_Target()
	 * @model transient="true"
	 * @generated
	 */
	MediaType getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(MediaType value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_Description()
	 * @model annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' containment reference.
	 * @see #setSchema(Schema)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_Schema()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	Schema getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getSchema <em>Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' containment reference.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(Schema value);

	/**
	 * Returns the value of the '<em><b>Item Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Schema of each item of a sequential media type.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Item Schema</em>' containment reference.
	 * @see #setItemSchema(Schema)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_ItemSchema()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	Schema getItemSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getItemSchema <em>Item Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Item Schema</em>' containment reference.
	 * @see #getItemSchema()
	 * @generated
	 */
	void setItemSchema(Schema value);

	/**
	 * Returns the value of the '<em><b>Example</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Example</em>' attribute.
	 * @see #isSetExample()
	 * @see #unsetExample()
	 * @see #setExample(Object)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_Example()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeNull='true'"
	 * @generated
	 */
	Object getExample();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getExample <em>Example</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getExample <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExample()
	 * @see #getExample()
	 * @see #setExample(Object)
	 * @generated
	 */
	void unsetExample();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getExample <em>Example</em>}' attribute is set.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_Examples()
	 * @model mapType="org.eclipse.fennec.model.oas.ExampleEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Example&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Example> getExamples();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getExamples <em>Examples</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExamples()
	 * @see #getExamples()
	 * @generated
	 */
	void unsetExamples();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getExamples <em>Examples</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Examples</em>' map is set.
	 * @see #unsetExamples()
	 * @see #getExamples()
	 * @generated
	 */
	boolean isSetExamples();

	/**
	 * Returns the value of the '<em><b>Encoding</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.model.oas.Encoding},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Encoding</em>' map.
	 * @see #isSetEncoding()
	 * @see #unsetEncoding()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_Encoding()
	 * @model mapType="org.eclipse.fennec.model.oas.EncodingEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.model.oas.Encoding&gt;"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 * @generated
	 */
	EMap<String, Encoding> getEncoding();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getEncoding <em>Encoding</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetEncoding()
	 * @see #getEncoding()
	 * @generated
	 */
	void unsetEncoding();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getEncoding <em>Encoding</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Encoding</em>' map is set.
	 * @see #unsetEncoding()
	 * @see #getEncoding()
	 * @generated
	 */
	boolean isSetEncoding();

	/**
	 * Returns the value of the '<em><b>Prefix Encoding</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.model.oas.Encoding}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Prefix Encoding</em>' containment reference list.
	 * @see #isSetPrefixEncoding()
	 * @see #unsetPrefixEncoding()
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_PrefixEncoding()
	 * @model containment="true" unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec serializeEmpty='true'"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	EList<Encoding> getPrefixEncoding();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getPrefixEncoding <em>Prefix Encoding</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPrefixEncoding()
	 * @see #getPrefixEncoding()
	 * @generated
	 */
	void unsetPrefixEncoding();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getPrefixEncoding <em>Prefix Encoding</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Prefix Encoding</em>' containment reference list is set.
	 * @see #unsetPrefixEncoding()
	 * @see #getPrefixEncoding()
	 * @generated
	 */
	boolean isSetPrefixEncoding();

	/**
	 * Returns the value of the '<em><b>Item Encoding</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Item Encoding</em>' containment reference.
	 * @see #setItemEncoding(Encoding)
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_ItemEncoding()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/openapi since='3.2'"
	 * @generated
	 */
	Encoding getItemEncoding();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.model.oas.MediaType#getItemEncoding <em>Item Encoding</em>}' containment reference.
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
	 * @see org.eclipse.fennec.model.oas.OasPackage#getMediaType_Extensions()
	 * @model mapType="org.eclipse.fennec.model.oas.ExtensionEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getExtensions();

} // MediaType
