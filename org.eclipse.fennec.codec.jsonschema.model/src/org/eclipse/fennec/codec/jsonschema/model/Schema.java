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

import java.math.BigDecimal;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Schema</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A JSON Schema of draft-04 to 2020-12, or an OpenAPI Schema Object. Holds the schema as written: a boolean schema (true / false) is a Schema with booleanSchema set; keywords without a feature go to keywords. The Ecore classifier derived from it is linked by classifier.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getBooleanSchema <em>Boolean Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getLegacyId <em>Legacy Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAnchor <em>Anchor</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDynamicRef <em>Dynamic Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDynamicAnchor <em>Dynamic Anchor</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveRef <em>Recursive Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveAnchor <em>Recursive Anchor</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getVocabulary <em>Vocabulary</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getComment <em>Comment</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefs <em>Defs</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefinitions <em>Definitions</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefault <em>Default</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDeprecated <em>Deprecated</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getReadOnly <em>Read Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getWriteOnly <em>Write Only</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExamples <em>Examples</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#isTypeAsList <em>Type As List</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getEnum <em>Enum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getConst <em>Const</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMultipleOf <em>Multiple Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaximum <em>Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximum <em>Exclusive Maximum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximumFlag <em>Exclusive Maximum Flag</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinimum <em>Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimum <em>Exclusive Minimum</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimumFlag <em>Exclusive Minimum Flag</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxLength <em>Max Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinLength <em>Min Length</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPattern <em>Pattern</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxItems <em>Max Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinItems <em>Min Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUniqueItems <em>Unique Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxContains <em>Max Contains</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinContains <em>Min Contains</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxProperties <em>Max Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinProperties <em>Min Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentRequired <em>Dependent Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getFormat <em>Format</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAllOf <em>All Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAnyOf <em>Any Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getOneOf <em>One Of</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getNot <em>Not</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getIf <em>If</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getThen <em>Then</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getElse <em>Else</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentSchemas <em>Dependent Schemas</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependencies <em>Dependencies</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPrefixItems <em>Prefix Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getItems <em>Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getItemsList <em>Items List</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAdditionalItems <em>Additional Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContains <em>Contains</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPatternProperties <em>Pattern Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAdditionalProperties <em>Additional Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPropertyNames <em>Property Names</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUnevaluatedItems <em>Unevaluated Items</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUnevaluatedProperties <em>Unevaluated Properties</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContentEncoding <em>Content Encoding</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContentMediaType <em>Content Media Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContentSchema <em>Content Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getNullable <em>Nullable</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDiscriminator <em>Discriminator</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getXml <em>Xml</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExternalDocs <em>External Docs</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExample <em>Example</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getKeywords <em>Keywords</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getClassifier <em>Classifier</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema()
 * @model annotation="http://eclipse.org/fennec/codec additionalPropertiesFeature='keywords'"
 * @generated
 */
@ProviderType
public interface Schema extends EObject {
	/**
	 * Returns the value of the '<em><b>Boolean Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Set for the boolean schemas true and false; then no keyword is set.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Boolean Schema</em>' attribute.
	 * @see #isSetBooleanSchema()
	 * @see #unsetBooleanSchema()
	 * @see #setBooleanSchema(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_BooleanSchema()
	 * @model unsettable="true"
	 * @generated
	 */
	Boolean getBooleanSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getBooleanSchema <em>Boolean Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Boolean Schema</em>' attribute.
	 * @see #isSetBooleanSchema()
	 * @see #unsetBooleanSchema()
	 * @see #getBooleanSchema()
	 * @generated
	 */
	void setBooleanSchema(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getBooleanSchema <em>Boolean Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetBooleanSchema()
	 * @see #getBooleanSchema()
	 * @see #setBooleanSchema(Boolean)
	 * @generated
	 */
	void unsetBooleanSchema();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getBooleanSchema <em>Boolean Schema</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Boolean Schema</em>' attribute is set.
	 * @see #unsetBooleanSchema()
	 * @see #getBooleanSchema()
	 * @see #setBooleanSchema(Boolean)
	 * @generated
	 */
	boolean isSetBooleanSchema();

	/**
	 * Returns the value of the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' attribute.
	 * @see #setSchema(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Schema()
	 * @model annotation="http://eclipse.org/fennec/codec key='$schema'"
	 * @generated
	 */
	String getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getSchema <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' attribute.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * $id; draft-04 named it id, see legacyId.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Id()
	 * @model annotation="http://eclipse.org/fennec/codec key='$id'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-06'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Legacy Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * draft-04 id.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Legacy Id</em>' attribute.
	 * @see #setLegacyId(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_LegacyId()
	 * @model annotation="http://eclipse.org/fennec/codec key='id'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model until='draft-04'"
	 * @generated
	 */
	String getLegacyId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getLegacyId <em>Legacy Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Legacy Id</em>' attribute.
	 * @see #getLegacyId()
	 * @generated
	 */
	void setLegacyId(String value);

	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Ref()
	 * @model annotation="http://eclipse.org/fennec/codec key='$ref'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRef <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' attribute.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(String value);

	/**
	 * Returns the value of the '<em><b>Anchor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Anchor</em>' attribute.
	 * @see #setAnchor(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Anchor()
	 * @model annotation="http://eclipse.org/fennec/codec key='$anchor'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	String getAnchor();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAnchor <em>Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Anchor</em>' attribute.
	 * @see #getAnchor()
	 * @generated
	 */
	void setAnchor(String value);

	/**
	 * Returns the value of the '<em><b>Dynamic Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dynamic Ref</em>' attribute.
	 * @see #setDynamicRef(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_DynamicRef()
	 * @model annotation="http://eclipse.org/fennec/codec key='$dynamicRef'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2020-12'"
	 * @generated
	 */
	String getDynamicRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDynamicRef <em>Dynamic Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dynamic Ref</em>' attribute.
	 * @see #getDynamicRef()
	 * @generated
	 */
	void setDynamicRef(String value);

	/**
	 * Returns the value of the '<em><b>Dynamic Anchor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dynamic Anchor</em>' attribute.
	 * @see #setDynamicAnchor(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_DynamicAnchor()
	 * @model annotation="http://eclipse.org/fennec/codec key='$dynamicAnchor'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2020-12'"
	 * @generated
	 */
	String getDynamicAnchor();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDynamicAnchor <em>Dynamic Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dynamic Anchor</em>' attribute.
	 * @see #getDynamicAnchor()
	 * @generated
	 */
	void setDynamicAnchor(String value);

	/**
	 * Returns the value of the '<em><b>Recursive Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Recursive Ref</em>' attribute.
	 * @see #setRecursiveRef(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_RecursiveRef()
	 * @model annotation="http://eclipse.org/fennec/codec key='$recursiveRef'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09' until='2019-09'"
	 * @generated
	 */
	String getRecursiveRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveRef <em>Recursive Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Recursive Ref</em>' attribute.
	 * @see #getRecursiveRef()
	 * @generated
	 */
	void setRecursiveRef(String value);

	/**
	 * Returns the value of the '<em><b>Recursive Anchor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Recursive Anchor</em>' attribute.
	 * @see #isSetRecursiveAnchor()
	 * @see #unsetRecursiveAnchor()
	 * @see #setRecursiveAnchor(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_RecursiveAnchor()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec key='$recursiveAnchor'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09' until='2019-09'"
	 * @generated
	 */
	Boolean getRecursiveAnchor();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveAnchor <em>Recursive Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Recursive Anchor</em>' attribute.
	 * @see #isSetRecursiveAnchor()
	 * @see #unsetRecursiveAnchor()
	 * @see #getRecursiveAnchor()
	 * @generated
	 */
	void setRecursiveAnchor(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveAnchor <em>Recursive Anchor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRecursiveAnchor()
	 * @see #getRecursiveAnchor()
	 * @see #setRecursiveAnchor(Boolean)
	 * @generated
	 */
	void unsetRecursiveAnchor();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRecursiveAnchor <em>Recursive Anchor</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Recursive Anchor</em>' attribute is set.
	 * @see #unsetRecursiveAnchor()
	 * @see #getRecursiveAnchor()
	 * @see #setRecursiveAnchor(Boolean)
	 * @generated
	 */
	boolean isSetRecursiveAnchor();

	/**
	 * Returns the value of the '<em><b>Vocabulary</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Boolean},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vocabulary</em>' map.
	 * @see #isSetVocabulary()
	 * @see #unsetVocabulary()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Vocabulary()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.VocabularyEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EBooleanObject&gt;"
	 *        annotation="http://eclipse.org/fennec/codec key='$vocabulary'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	EMap<String, Boolean> getVocabulary();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getVocabulary <em>Vocabulary</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetVocabulary()
	 * @see #getVocabulary()
	 * @generated
	 */
	void unsetVocabulary();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getVocabulary <em>Vocabulary</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Vocabulary</em>' map is set.
	 * @see #unsetVocabulary()
	 * @see #getVocabulary()
	 * @generated
	 */
	boolean isSetVocabulary();

	/**
	 * Returns the value of the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Comment</em>' attribute.
	 * @see #setComment(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Comment()
	 * @model annotation="http://eclipse.org/fennec/codec key='$comment'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-07'"
	 * @generated
	 */
	String getComment();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getComment <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Comment</em>' attribute.
	 * @see #getComment()
	 * @generated
	 */
	void setComment(String value);

	/**
	 * Returns the value of the '<em><b>Defs</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Defs</em>' map.
	 * @see #isSetDefs()
	 * @see #unsetDefs()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Defs()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.SchemaEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.codec.jsonschema.model.Schema&gt;"
	 *        annotation="http://eclipse.org/fennec/codec key='$defs' valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	EMap<String, Schema> getDefs();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefs <em>Defs</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDefs()
	 * @see #getDefs()
	 * @generated
	 */
	void unsetDefs();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefs <em>Defs</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Defs</em>' map is set.
	 * @see #unsetDefs()
	 * @see #getDefs()
	 * @generated
	 */
	boolean isSetDefs();

	/**
	 * Returns the value of the '<em><b>Definitions</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Definitions</em>' map.
	 * @see #isSetDefinitions()
	 * @see #unsetDefinitions()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Definitions()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.SchemaEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.codec.jsonschema.model.Schema&gt;"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	EMap<String, Schema> getDefinitions();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefinitions <em>Definitions</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDefinitions()
	 * @see #getDefinitions()
	 * @generated
	 */
	void unsetDefinitions();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefinitions <em>Definitions</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Definitions</em>' map is set.
	 * @see #unsetDefinitions()
	 * @see #getDefinitions()
	 * @generated
	 */
	boolean isSetDefinitions();

	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Title()
	 * @model
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default</em>' attribute.
	 * @see #isSetDefault()
	 * @see #unsetDefault()
	 * @see #setDefault(Object)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Default()
	 * @model unsettable="true"
	 * @generated
	 */
	Object getDefault();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefault <em>Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default</em>' attribute.
	 * @see #isSetDefault()
	 * @see #unsetDefault()
	 * @see #getDefault()
	 * @generated
	 */
	void setDefault(Object value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefault <em>Default</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDefault()
	 * @see #getDefault()
	 * @see #setDefault(Object)
	 * @generated
	 */
	void unsetDefault();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDefault <em>Default</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Default</em>' attribute is set.
	 * @see #unsetDefault()
	 * @see #getDefault()
	 * @see #setDefault(Object)
	 * @generated
	 */
	boolean isSetDefault();

	/**
	 * Returns the value of the '<em><b>Deprecated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deprecated</em>' attribute.
	 * @see #isSetDeprecated()
	 * @see #unsetDeprecated()
	 * @see #setDeprecated(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Deprecated()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	Boolean getDeprecated();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDeprecated <em>Deprecated</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDeprecated <em>Deprecated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDeprecated()
	 * @see #getDeprecated()
	 * @see #setDeprecated(Boolean)
	 * @generated
	 */
	void unsetDeprecated();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDeprecated <em>Deprecated</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Read Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Read Only</em>' attribute.
	 * @see #isSetReadOnly()
	 * @see #unsetReadOnly()
	 * @see #setReadOnly(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ReadOnly()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-07'"
	 * @generated
	 */
	Boolean getReadOnly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getReadOnly <em>Read Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Read Only</em>' attribute.
	 * @see #isSetReadOnly()
	 * @see #unsetReadOnly()
	 * @see #getReadOnly()
	 * @generated
	 */
	void setReadOnly(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getReadOnly <em>Read Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetReadOnly()
	 * @see #getReadOnly()
	 * @see #setReadOnly(Boolean)
	 * @generated
	 */
	void unsetReadOnly();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getReadOnly <em>Read Only</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Read Only</em>' attribute is set.
	 * @see #unsetReadOnly()
	 * @see #getReadOnly()
	 * @see #setReadOnly(Boolean)
	 * @generated
	 */
	boolean isSetReadOnly();

	/**
	 * Returns the value of the '<em><b>Write Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Write Only</em>' attribute.
	 * @see #isSetWriteOnly()
	 * @see #unsetWriteOnly()
	 * @see #setWriteOnly(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_WriteOnly()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-07'"
	 * @generated
	 */
	Boolean getWriteOnly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getWriteOnly <em>Write Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Write Only</em>' attribute.
	 * @see #isSetWriteOnly()
	 * @see #unsetWriteOnly()
	 * @see #getWriteOnly()
	 * @generated
	 */
	void setWriteOnly(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getWriteOnly <em>Write Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetWriteOnly()
	 * @see #getWriteOnly()
	 * @see #setWriteOnly(Boolean)
	 * @generated
	 */
	void unsetWriteOnly();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getWriteOnly <em>Write Only</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Write Only</em>' attribute is set.
	 * @see #unsetWriteOnly()
	 * @see #getWriteOnly()
	 * @see #setWriteOnly(Boolean)
	 * @generated
	 */
	boolean isSetWriteOnly();

	/**
	 * Returns the value of the '<em><b>Examples</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Object}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Examples</em>' attribute list.
	 * @see #isSetExamples()
	 * @see #unsetExamples()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Examples()
	 * @model unique="false" unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-06'"
	 * @generated
	 */
	EList<Object> getExamples();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExamples <em>Examples</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExamples()
	 * @see #getExamples()
	 * @generated
	 */
	void unsetExamples();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExamples <em>Examples</em>}' attribute list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Examples</em>' attribute list is set.
	 * @see #unsetExamples()
	 * @see #getExamples()
	 * @generated
	 */
	boolean isSetExamples();

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.jsonschema.model.SimpleType}.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.jsonschema.model.SimpleType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The type, or the list of types.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type</em>' attribute list.
	 * @see org.eclipse.fennec.codec.jsonschema.model.SimpleType
	 * @see #isSetType()
	 * @see #unsetType()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Type()
	 * @model unique="false" unsettable="true"
	 * @generated
	 */
	EList<SimpleType> getType();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getType <em>Type</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetType()
	 * @see #getType()
	 * @generated
	 */
	void unsetType();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getType <em>Type</em>}' attribute list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Type</em>' attribute list is set.
	 * @see #unsetType()
	 * @see #getType()
	 * @generated
	 */
	boolean isSetType();

	/**
	 * Returns the value of the '<em><b>Type As List</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * type was written as a list (also with one entry); kept for writing.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type As List</em>' attribute.
	 * @see #setTypeAsList(boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_TypeAsList()
	 * @model
	 * @generated
	 */
	boolean isTypeAsList();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#isTypeAsList <em>Type As List</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type As List</em>' attribute.
	 * @see #isTypeAsList()
	 * @generated
	 */
	void setTypeAsList(boolean value);

	/**
	 * Returns the value of the '<em><b>Enum</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Object}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enum</em>' attribute list.
	 * @see #isSetEnum()
	 * @see #unsetEnum()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Enum()
	 * @model unique="false" unsettable="true"
	 * @generated
	 */
	EList<Object> getEnum();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getEnum <em>Enum</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetEnum()
	 * @see #getEnum()
	 * @generated
	 */
	void unsetEnum();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getEnum <em>Enum</em>}' attribute list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Enum</em>' attribute list is set.
	 * @see #unsetEnum()
	 * @see #getEnum()
	 * @generated
	 */
	boolean isSetEnum();

	/**
	 * Returns the value of the '<em><b>Const</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Const</em>' attribute.
	 * @see #isSetConst()
	 * @see #unsetConst()
	 * @see #setConst(Object)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Const()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-06'"
	 * @generated
	 */
	Object getConst();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getConst <em>Const</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Const</em>' attribute.
	 * @see #isSetConst()
	 * @see #unsetConst()
	 * @see #getConst()
	 * @generated
	 */
	void setConst(Object value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getConst <em>Const</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetConst()
	 * @see #getConst()
	 * @see #setConst(Object)
	 * @generated
	 */
	void unsetConst();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getConst <em>Const</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Const</em>' attribute is set.
	 * @see #unsetConst()
	 * @see #getConst()
	 * @see #setConst(Object)
	 * @generated
	 */
	boolean isSetConst();

	/**
	 * Returns the value of the '<em><b>Multiple Of</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Multiple Of</em>' attribute.
	 * @see #isSetMultipleOf()
	 * @see #unsetMultipleOf()
	 * @see #setMultipleOf(BigDecimal)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_MultipleOf()
	 * @model unsettable="true"
	 * @generated
	 */
	BigDecimal getMultipleOf();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMultipleOf <em>Multiple Of</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Multiple Of</em>' attribute.
	 * @see #isSetMultipleOf()
	 * @see #unsetMultipleOf()
	 * @see #getMultipleOf()
	 * @generated
	 */
	void setMultipleOf(BigDecimal value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMultipleOf <em>Multiple Of</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMultipleOf()
	 * @see #getMultipleOf()
	 * @see #setMultipleOf(BigDecimal)
	 * @generated
	 */
	void unsetMultipleOf();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMultipleOf <em>Multiple Of</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Multiple Of</em>' attribute is set.
	 * @see #unsetMultipleOf()
	 * @see #getMultipleOf()
	 * @see #setMultipleOf(BigDecimal)
	 * @generated
	 */
	boolean isSetMultipleOf();

	/**
	 * Returns the value of the '<em><b>Maximum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Maximum</em>' attribute.
	 * @see #isSetMaximum()
	 * @see #unsetMaximum()
	 * @see #setMaximum(BigDecimal)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Maximum()
	 * @model unsettable="true"
	 * @generated
	 */
	BigDecimal getMaximum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaximum <em>Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Maximum</em>' attribute.
	 * @see #isSetMaximum()
	 * @see #unsetMaximum()
	 * @see #getMaximum()
	 * @generated
	 */
	void setMaximum(BigDecimal value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaximum <em>Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMaximum()
	 * @see #getMaximum()
	 * @see #setMaximum(BigDecimal)
	 * @generated
	 */
	void unsetMaximum();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaximum <em>Maximum</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Maximum</em>' attribute is set.
	 * @see #unsetMaximum()
	 * @see #getMaximum()
	 * @see #setMaximum(BigDecimal)
	 * @generated
	 */
	boolean isSetMaximum();

	/**
	 * Returns the value of the '<em><b>Exclusive Maximum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The exclusive bound (draft-06 and later).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Exclusive Maximum</em>' attribute.
	 * @see #isSetExclusiveMaximum()
	 * @see #unsetExclusiveMaximum()
	 * @see #setExclusiveMaximum(BigDecimal)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ExclusiveMaximum()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-06'"
	 * @generated
	 */
	BigDecimal getExclusiveMaximum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximum <em>Exclusive Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusive Maximum</em>' attribute.
	 * @see #isSetExclusiveMaximum()
	 * @see #unsetExclusiveMaximum()
	 * @see #getExclusiveMaximum()
	 * @generated
	 */
	void setExclusiveMaximum(BigDecimal value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximum <em>Exclusive Maximum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExclusiveMaximum()
	 * @see #getExclusiveMaximum()
	 * @see #setExclusiveMaximum(BigDecimal)
	 * @generated
	 */
	void unsetExclusiveMaximum();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximum <em>Exclusive Maximum</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Exclusive Maximum</em>' attribute is set.
	 * @see #unsetExclusiveMaximum()
	 * @see #getExclusiveMaximum()
	 * @see #setExclusiveMaximum(BigDecimal)
	 * @generated
	 */
	boolean isSetExclusiveMaximum();

	/**
	 * Returns the value of the '<em><b>Exclusive Maximum Flag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * draft-04 / OpenAPI 3.0: maximum is exclusive. Read and written by the schema reader.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Exclusive Maximum Flag</em>' attribute.
	 * @see #isSetExclusiveMaximumFlag()
	 * @see #unsetExclusiveMaximumFlag()
	 * @see #setExclusiveMaximumFlag(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ExclusiveMaximumFlag()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec key='exclusiveMaximum'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model until='draft-04'"
	 * @generated
	 */
	Boolean getExclusiveMaximumFlag();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximumFlag <em>Exclusive Maximum Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusive Maximum Flag</em>' attribute.
	 * @see #isSetExclusiveMaximumFlag()
	 * @see #unsetExclusiveMaximumFlag()
	 * @see #getExclusiveMaximumFlag()
	 * @generated
	 */
	void setExclusiveMaximumFlag(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximumFlag <em>Exclusive Maximum Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExclusiveMaximumFlag()
	 * @see #getExclusiveMaximumFlag()
	 * @see #setExclusiveMaximumFlag(Boolean)
	 * @generated
	 */
	void unsetExclusiveMaximumFlag();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMaximumFlag <em>Exclusive Maximum Flag</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Exclusive Maximum Flag</em>' attribute is set.
	 * @see #unsetExclusiveMaximumFlag()
	 * @see #getExclusiveMaximumFlag()
	 * @see #setExclusiveMaximumFlag(Boolean)
	 * @generated
	 */
	boolean isSetExclusiveMaximumFlag();

	/**
	 * Returns the value of the '<em><b>Minimum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Minimum</em>' attribute.
	 * @see #isSetMinimum()
	 * @see #unsetMinimum()
	 * @see #setMinimum(BigDecimal)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Minimum()
	 * @model unsettable="true"
	 * @generated
	 */
	BigDecimal getMinimum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinimum <em>Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Minimum</em>' attribute.
	 * @see #isSetMinimum()
	 * @see #unsetMinimum()
	 * @see #getMinimum()
	 * @generated
	 */
	void setMinimum(BigDecimal value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinimum <em>Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMinimum()
	 * @see #getMinimum()
	 * @see #setMinimum(BigDecimal)
	 * @generated
	 */
	void unsetMinimum();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinimum <em>Minimum</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Minimum</em>' attribute is set.
	 * @see #unsetMinimum()
	 * @see #getMinimum()
	 * @see #setMinimum(BigDecimal)
	 * @generated
	 */
	boolean isSetMinimum();

	/**
	 * Returns the value of the '<em><b>Exclusive Minimum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The exclusive bound (draft-06 and later).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Exclusive Minimum</em>' attribute.
	 * @see #isSetExclusiveMinimum()
	 * @see #unsetExclusiveMinimum()
	 * @see #setExclusiveMinimum(BigDecimal)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ExclusiveMinimum()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-06'"
	 * @generated
	 */
	BigDecimal getExclusiveMinimum();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimum <em>Exclusive Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusive Minimum</em>' attribute.
	 * @see #isSetExclusiveMinimum()
	 * @see #unsetExclusiveMinimum()
	 * @see #getExclusiveMinimum()
	 * @generated
	 */
	void setExclusiveMinimum(BigDecimal value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimum <em>Exclusive Minimum</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExclusiveMinimum()
	 * @see #getExclusiveMinimum()
	 * @see #setExclusiveMinimum(BigDecimal)
	 * @generated
	 */
	void unsetExclusiveMinimum();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimum <em>Exclusive Minimum</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Exclusive Minimum</em>' attribute is set.
	 * @see #unsetExclusiveMinimum()
	 * @see #getExclusiveMinimum()
	 * @see #setExclusiveMinimum(BigDecimal)
	 * @generated
	 */
	boolean isSetExclusiveMinimum();

	/**
	 * Returns the value of the '<em><b>Exclusive Minimum Flag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * draft-04 / OpenAPI 3.0: minimum is exclusive. Read and written by the schema reader.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Exclusive Minimum Flag</em>' attribute.
	 * @see #isSetExclusiveMinimumFlag()
	 * @see #unsetExclusiveMinimumFlag()
	 * @see #setExclusiveMinimumFlag(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ExclusiveMinimumFlag()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec key='exclusiveMinimum'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model until='draft-04'"
	 * @generated
	 */
	Boolean getExclusiveMinimumFlag();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimumFlag <em>Exclusive Minimum Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclusive Minimum Flag</em>' attribute.
	 * @see #isSetExclusiveMinimumFlag()
	 * @see #unsetExclusiveMinimumFlag()
	 * @see #getExclusiveMinimumFlag()
	 * @generated
	 */
	void setExclusiveMinimumFlag(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimumFlag <em>Exclusive Minimum Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExclusiveMinimumFlag()
	 * @see #getExclusiveMinimumFlag()
	 * @see #setExclusiveMinimumFlag(Boolean)
	 * @generated
	 */
	void unsetExclusiveMinimumFlag();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExclusiveMinimumFlag <em>Exclusive Minimum Flag</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Exclusive Minimum Flag</em>' attribute is set.
	 * @see #unsetExclusiveMinimumFlag()
	 * @see #getExclusiveMinimumFlag()
	 * @see #setExclusiveMinimumFlag(Boolean)
	 * @generated
	 */
	boolean isSetExclusiveMinimumFlag();

	/**
	 * Returns the value of the '<em><b>Max Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Length</em>' attribute.
	 * @see #isSetMaxLength()
	 * @see #unsetMaxLength()
	 * @see #setMaxLength(Integer)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_MaxLength()
	 * @model unsettable="true"
	 * @generated
	 */
	Integer getMaxLength();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxLength <em>Max Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Length</em>' attribute.
	 * @see #isSetMaxLength()
	 * @see #unsetMaxLength()
	 * @see #getMaxLength()
	 * @generated
	 */
	void setMaxLength(Integer value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxLength <em>Max Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMaxLength()
	 * @see #getMaxLength()
	 * @see #setMaxLength(Integer)
	 * @generated
	 */
	void unsetMaxLength();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxLength <em>Max Length</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Max Length</em>' attribute is set.
	 * @see #unsetMaxLength()
	 * @see #getMaxLength()
	 * @see #setMaxLength(Integer)
	 * @generated
	 */
	boolean isSetMaxLength();

	/**
	 * Returns the value of the '<em><b>Min Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Length</em>' attribute.
	 * @see #isSetMinLength()
	 * @see #unsetMinLength()
	 * @see #setMinLength(Integer)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_MinLength()
	 * @model unsettable="true"
	 * @generated
	 */
	Integer getMinLength();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinLength <em>Min Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Length</em>' attribute.
	 * @see #isSetMinLength()
	 * @see #unsetMinLength()
	 * @see #getMinLength()
	 * @generated
	 */
	void setMinLength(Integer value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinLength <em>Min Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMinLength()
	 * @see #getMinLength()
	 * @see #setMinLength(Integer)
	 * @generated
	 */
	void unsetMinLength();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinLength <em>Min Length</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Min Length</em>' attribute is set.
	 * @see #unsetMinLength()
	 * @see #getMinLength()
	 * @see #setMinLength(Integer)
	 * @generated
	 */
	boolean isSetMinLength();

	/**
	 * Returns the value of the '<em><b>Pattern</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pattern</em>' attribute.
	 * @see #setPattern(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Pattern()
	 * @model
	 * @generated
	 */
	String getPattern();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPattern <em>Pattern</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pattern</em>' attribute.
	 * @see #getPattern()
	 * @generated
	 */
	void setPattern(String value);

	/**
	 * Returns the value of the '<em><b>Max Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Items</em>' attribute.
	 * @see #isSetMaxItems()
	 * @see #unsetMaxItems()
	 * @see #setMaxItems(Integer)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_MaxItems()
	 * @model unsettable="true"
	 * @generated
	 */
	Integer getMaxItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxItems <em>Max Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Items</em>' attribute.
	 * @see #isSetMaxItems()
	 * @see #unsetMaxItems()
	 * @see #getMaxItems()
	 * @generated
	 */
	void setMaxItems(Integer value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxItems <em>Max Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMaxItems()
	 * @see #getMaxItems()
	 * @see #setMaxItems(Integer)
	 * @generated
	 */
	void unsetMaxItems();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxItems <em>Max Items</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Max Items</em>' attribute is set.
	 * @see #unsetMaxItems()
	 * @see #getMaxItems()
	 * @see #setMaxItems(Integer)
	 * @generated
	 */
	boolean isSetMaxItems();

	/**
	 * Returns the value of the '<em><b>Min Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Items</em>' attribute.
	 * @see #isSetMinItems()
	 * @see #unsetMinItems()
	 * @see #setMinItems(Integer)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_MinItems()
	 * @model unsettable="true"
	 * @generated
	 */
	Integer getMinItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinItems <em>Min Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Items</em>' attribute.
	 * @see #isSetMinItems()
	 * @see #unsetMinItems()
	 * @see #getMinItems()
	 * @generated
	 */
	void setMinItems(Integer value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinItems <em>Min Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMinItems()
	 * @see #getMinItems()
	 * @see #setMinItems(Integer)
	 * @generated
	 */
	void unsetMinItems();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinItems <em>Min Items</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Min Items</em>' attribute is set.
	 * @see #unsetMinItems()
	 * @see #getMinItems()
	 * @see #setMinItems(Integer)
	 * @generated
	 */
	boolean isSetMinItems();

	/**
	 * Returns the value of the '<em><b>Unique Items</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unique Items</em>' attribute.
	 * @see #isSetUniqueItems()
	 * @see #unsetUniqueItems()
	 * @see #setUniqueItems(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_UniqueItems()
	 * @model unsettable="true"
	 * @generated
	 */
	Boolean getUniqueItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUniqueItems <em>Unique Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unique Items</em>' attribute.
	 * @see #isSetUniqueItems()
	 * @see #unsetUniqueItems()
	 * @see #getUniqueItems()
	 * @generated
	 */
	void setUniqueItems(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUniqueItems <em>Unique Items</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetUniqueItems()
	 * @see #getUniqueItems()
	 * @see #setUniqueItems(Boolean)
	 * @generated
	 */
	void unsetUniqueItems();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUniqueItems <em>Unique Items</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Unique Items</em>' attribute is set.
	 * @see #unsetUniqueItems()
	 * @see #getUniqueItems()
	 * @see #setUniqueItems(Boolean)
	 * @generated
	 */
	boolean isSetUniqueItems();

	/**
	 * Returns the value of the '<em><b>Max Contains</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Contains</em>' attribute.
	 * @see #isSetMaxContains()
	 * @see #unsetMaxContains()
	 * @see #setMaxContains(Integer)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_MaxContains()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	Integer getMaxContains();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxContains <em>Max Contains</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Contains</em>' attribute.
	 * @see #isSetMaxContains()
	 * @see #unsetMaxContains()
	 * @see #getMaxContains()
	 * @generated
	 */
	void setMaxContains(Integer value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxContains <em>Max Contains</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMaxContains()
	 * @see #getMaxContains()
	 * @see #setMaxContains(Integer)
	 * @generated
	 */
	void unsetMaxContains();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxContains <em>Max Contains</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Max Contains</em>' attribute is set.
	 * @see #unsetMaxContains()
	 * @see #getMaxContains()
	 * @see #setMaxContains(Integer)
	 * @generated
	 */
	boolean isSetMaxContains();

	/**
	 * Returns the value of the '<em><b>Min Contains</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Contains</em>' attribute.
	 * @see #isSetMinContains()
	 * @see #unsetMinContains()
	 * @see #setMinContains(Integer)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_MinContains()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	Integer getMinContains();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinContains <em>Min Contains</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Contains</em>' attribute.
	 * @see #isSetMinContains()
	 * @see #unsetMinContains()
	 * @see #getMinContains()
	 * @generated
	 */
	void setMinContains(Integer value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinContains <em>Min Contains</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMinContains()
	 * @see #getMinContains()
	 * @see #setMinContains(Integer)
	 * @generated
	 */
	void unsetMinContains();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinContains <em>Min Contains</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Min Contains</em>' attribute is set.
	 * @see #unsetMinContains()
	 * @see #getMinContains()
	 * @see #setMinContains(Integer)
	 * @generated
	 */
	boolean isSetMinContains();

	/**
	 * Returns the value of the '<em><b>Max Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Properties</em>' attribute.
	 * @see #isSetMaxProperties()
	 * @see #unsetMaxProperties()
	 * @see #setMaxProperties(Integer)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_MaxProperties()
	 * @model unsettable="true"
	 * @generated
	 */
	Integer getMaxProperties();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxProperties <em>Max Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Properties</em>' attribute.
	 * @see #isSetMaxProperties()
	 * @see #unsetMaxProperties()
	 * @see #getMaxProperties()
	 * @generated
	 */
	void setMaxProperties(Integer value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxProperties <em>Max Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMaxProperties()
	 * @see #getMaxProperties()
	 * @see #setMaxProperties(Integer)
	 * @generated
	 */
	void unsetMaxProperties();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMaxProperties <em>Max Properties</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Max Properties</em>' attribute is set.
	 * @see #unsetMaxProperties()
	 * @see #getMaxProperties()
	 * @see #setMaxProperties(Integer)
	 * @generated
	 */
	boolean isSetMaxProperties();

	/**
	 * Returns the value of the '<em><b>Min Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Properties</em>' attribute.
	 * @see #isSetMinProperties()
	 * @see #unsetMinProperties()
	 * @see #setMinProperties(Integer)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_MinProperties()
	 * @model unsettable="true"
	 * @generated
	 */
	Integer getMinProperties();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinProperties <em>Min Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Properties</em>' attribute.
	 * @see #isSetMinProperties()
	 * @see #unsetMinProperties()
	 * @see #getMinProperties()
	 * @generated
	 */
	void setMinProperties(Integer value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinProperties <em>Min Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMinProperties()
	 * @see #getMinProperties()
	 * @see #setMinProperties(Integer)
	 * @generated
	 */
	void unsetMinProperties();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getMinProperties <em>Min Properties</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Min Properties</em>' attribute is set.
	 * @see #unsetMinProperties()
	 * @see #getMinProperties()
	 * @see #setMinProperties(Integer)
	 * @generated
	 */
	boolean isSetMinProperties();

	/**
	 * Returns the value of the '<em><b>Required</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required</em>' attribute list.
	 * @see #isSetRequired()
	 * @see #unsetRequired()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Required()
	 * @model unique="false" unsettable="true"
	 * @generated
	 */
	EList<String> getRequired();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRequired <em>Required</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRequired()
	 * @see #getRequired()
	 * @generated
	 */
	void unsetRequired();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getRequired <em>Required</em>}' attribute list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Required</em>' attribute list is set.
	 * @see #unsetRequired()
	 * @see #getRequired()
	 * @generated
	 */
	boolean isSetRequired();

	/**
	 * Returns the value of the '<em><b>Dependent Required</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type list of {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dependent Required</em>' map.
	 * @see #isSetDependentRequired()
	 * @see #unsetDependentRequired()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_DependentRequired()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.StringListEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	EMap<String, EList<String>> getDependentRequired();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentRequired <em>Dependent Required</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDependentRequired()
	 * @see #getDependentRequired()
	 * @generated
	 */
	void unsetDependentRequired();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentRequired <em>Dependent Required</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Dependent Required</em>' map is set.
	 * @see #unsetDependentRequired()
	 * @see #getDependentRequired()
	 * @generated
	 */
	boolean isSetDependentRequired();

	/**
	 * Returns the value of the '<em><b>Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Format</em>' attribute.
	 * @see #setFormat(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Format()
	 * @model
	 * @generated
	 */
	String getFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getFormat <em>Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format</em>' attribute.
	 * @see #getFormat()
	 * @generated
	 */
	void setFormat(String value);

	/**
	 * Returns the value of the '<em><b>All Of</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>All Of</em>' containment reference list.
	 * @see #isSetAllOf()
	 * @see #unsetAllOf()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_AllOf()
	 * @model containment="true" unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	EList<Schema> getAllOf();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAllOf <em>All Of</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAllOf()
	 * @see #getAllOf()
	 * @generated
	 */
	void unsetAllOf();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAllOf <em>All Of</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>All Of</em>' containment reference list is set.
	 * @see #unsetAllOf()
	 * @see #getAllOf()
	 * @generated
	 */
	boolean isSetAllOf();

	/**
	 * Returns the value of the '<em><b>Any Of</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Any Of</em>' containment reference list.
	 * @see #isSetAnyOf()
	 * @see #unsetAnyOf()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_AnyOf()
	 * @model containment="true" unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	EList<Schema> getAnyOf();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAnyOf <em>Any Of</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetAnyOf()
	 * @see #getAnyOf()
	 * @generated
	 */
	void unsetAnyOf();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAnyOf <em>Any Of</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Any Of</em>' containment reference list is set.
	 * @see #unsetAnyOf()
	 * @see #getAnyOf()
	 * @generated
	 */
	boolean isSetAnyOf();

	/**
	 * Returns the value of the '<em><b>One Of</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>One Of</em>' containment reference list.
	 * @see #isSetOneOf()
	 * @see #unsetOneOf()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_OneOf()
	 * @model containment="true" unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	EList<Schema> getOneOf();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getOneOf <em>One Of</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetOneOf()
	 * @see #getOneOf()
	 * @generated
	 */
	void unsetOneOf();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getOneOf <em>One Of</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>One Of</em>' containment reference list is set.
	 * @see #unsetOneOf()
	 * @see #getOneOf()
	 * @generated
	 */
	boolean isSetOneOf();

	/**
	 * Returns the value of the '<em><b>Not</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Not</em>' containment reference.
	 * @see #setNot(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Not()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	Schema getNot();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getNot <em>Not</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Not</em>' containment reference.
	 * @see #getNot()
	 * @generated
	 */
	void setNot(Schema value);

	/**
	 * Returns the value of the '<em><b>If</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>If</em>' containment reference.
	 * @see #setIf(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_If()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-07'"
	 * @generated
	 */
	Schema getIf();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getIf <em>If</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>If</em>' containment reference.
	 * @see #getIf()
	 * @generated
	 */
	void setIf(Schema value);

	/**
	 * Returns the value of the '<em><b>Then</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Then</em>' containment reference.
	 * @see #setThen(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Then()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-07'"
	 * @generated
	 */
	Schema getThen();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getThen <em>Then</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Then</em>' containment reference.
	 * @see #getThen()
	 * @generated
	 */
	void setThen(Schema value);

	/**
	 * Returns the value of the '<em><b>Else</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Else</em>' containment reference.
	 * @see #setElse(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Else()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-07'"
	 * @generated
	 */
	Schema getElse();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getElse <em>Else</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Else</em>' containment reference.
	 * @see #getElse()
	 * @generated
	 */
	void setElse(Schema value);

	/**
	 * Returns the value of the '<em><b>Dependent Schemas</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dependent Schemas</em>' map.
	 * @see #isSetDependentSchemas()
	 * @see #unsetDependentSchemas()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_DependentSchemas()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.SchemaEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.codec.jsonschema.model.Schema&gt;"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	EMap<String, Schema> getDependentSchemas();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentSchemas <em>Dependent Schemas</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDependentSchemas()
	 * @see #getDependentSchemas()
	 * @generated
	 */
	void unsetDependentSchemas();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependentSchemas <em>Dependent Schemas</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Dependent Schemas</em>' map is set.
	 * @see #unsetDependentSchemas()
	 * @see #getDependentSchemas()
	 * @generated
	 */
	boolean isSetDependentSchemas();

	/**
	 * Returns the value of the '<em><b>Dependencies</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.codec.jsonschema.model.Dependency},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * draft-04 to 2019-09: a schema or a list of property names per property.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Dependencies</em>' map.
	 * @see #isSetDependencies()
	 * @see #unsetDependencies()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Dependencies()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.DependencyEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.codec.jsonschema.model.Dependency&gt;"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model until='2019-09'"
	 * @generated
	 */
	EMap<String, Dependency> getDependencies();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependencies <em>Dependencies</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDependencies()
	 * @see #getDependencies()
	 * @generated
	 */
	void unsetDependencies();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDependencies <em>Dependencies</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Dependencies</em>' map is set.
	 * @see #unsetDependencies()
	 * @see #getDependencies()
	 * @generated
	 */
	boolean isSetDependencies();

	/**
	 * Returns the value of the '<em><b>Prefix Items</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Prefix Items</em>' containment reference list.
	 * @see #isSetPrefixItems()
	 * @see #unsetPrefixItems()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_PrefixItems()
	 * @model containment="true" unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2020-12'"
	 * @generated
	 */
	EList<Schema> getPrefixItems();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPrefixItems <em>Prefix Items</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPrefixItems()
	 * @see #getPrefixItems()
	 * @generated
	 */
	void unsetPrefixItems();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPrefixItems <em>Prefix Items</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Prefix Items</em>' containment reference list is set.
	 * @see #unsetPrefixItems()
	 * @see #getPrefixItems()
	 * @generated
	 */
	boolean isSetPrefixItems();

	/**
	 * Returns the value of the '<em><b>Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The schema of all items (2020-12: of the items after prefixItems).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Items</em>' containment reference.
	 * @see #setItems(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Items()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	Schema getItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getItems <em>Items</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Items</em>' containment reference.
	 * @see #getItems()
	 * @generated
	 */
	void setItems(Schema value);

	/**
	 * Returns the value of the '<em><b>Items List</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * draft-04 to 2019-09: items as a list of schemas (tuple form). Read and written by the schema reader.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Items List</em>' containment reference list.
	 * @see #isSetItemsList()
	 * @see #unsetItemsList()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ItemsList()
	 * @model containment="true" unsettable="true"
	 *        annotation="http://eclipse.org/fennec/codec key='items' valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model until='2019-09'"
	 * @generated
	 */
	EList<Schema> getItemsList();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getItemsList <em>Items List</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetItemsList()
	 * @see #getItemsList()
	 * @generated
	 */
	void unsetItemsList();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getItemsList <em>Items List</em>}' containment reference list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Items List</em>' containment reference list is set.
	 * @see #unsetItemsList()
	 * @see #getItemsList()
	 * @generated
	 */
	boolean isSetItemsList();

	/**
	 * Returns the value of the '<em><b>Additional Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Additional Items</em>' containment reference.
	 * @see #setAdditionalItems(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_AdditionalItems()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model until='2019-09'"
	 * @generated
	 */
	Schema getAdditionalItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAdditionalItems <em>Additional Items</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Additional Items</em>' containment reference.
	 * @see #getAdditionalItems()
	 * @generated
	 */
	void setAdditionalItems(Schema value);

	/**
	 * Returns the value of the '<em><b>Contains</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contains</em>' containment reference.
	 * @see #setContains(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Contains()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-06'"
	 * @generated
	 */
	Schema getContains();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContains <em>Contains</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Contains</em>' containment reference.
	 * @see #getContains()
	 * @generated
	 */
	void setContains(Schema value);

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties</em>' map.
	 * @see #isSetProperties()
	 * @see #unsetProperties()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Properties()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.SchemaEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.codec.jsonschema.model.Schema&gt;"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	EMap<String, Schema> getProperties();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getProperties <em>Properties</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetProperties()
	 * @see #getProperties()
	 * @generated
	 */
	void unsetProperties();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getProperties <em>Properties</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Properties</em>' map is set.
	 * @see #unsetProperties()
	 * @see #getProperties()
	 * @generated
	 */
	boolean isSetProperties();

	/**
	 * Returns the value of the '<em><b>Pattern Properties</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.eclipse.fennec.codec.jsonschema.model.Schema},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pattern Properties</em>' map.
	 * @see #isSetPatternProperties()
	 * @see #unsetPatternProperties()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_PatternProperties()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.SchemaEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.fennec.codec.jsonschema.model.Schema&gt;"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	EMap<String, Schema> getPatternProperties();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPatternProperties <em>Pattern Properties</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetPatternProperties()
	 * @see #getPatternProperties()
	 * @generated
	 */
	void unsetPatternProperties();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPatternProperties <em>Pattern Properties</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Pattern Properties</em>' map is set.
	 * @see #unsetPatternProperties()
	 * @see #getPatternProperties()
	 * @generated
	 */
	boolean isSetPatternProperties();

	/**
	 * Returns the value of the '<em><b>Additional Properties</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Additional Properties</em>' containment reference.
	 * @see #setAdditionalProperties(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_AdditionalProperties()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 * @generated
	 */
	Schema getAdditionalProperties();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getAdditionalProperties <em>Additional Properties</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Additional Properties</em>' containment reference.
	 * @see #getAdditionalProperties()
	 * @generated
	 */
	void setAdditionalProperties(Schema value);

	/**
	 * Returns the value of the '<em><b>Property Names</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property Names</em>' containment reference.
	 * @see #setPropertyNames(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_PropertyNames()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='draft-06'"
	 * @generated
	 */
	Schema getPropertyNames();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getPropertyNames <em>Property Names</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Property Names</em>' containment reference.
	 * @see #getPropertyNames()
	 * @generated
	 */
	void setPropertyNames(Schema value);

	/**
	 * Returns the value of the '<em><b>Unevaluated Items</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unevaluated Items</em>' containment reference.
	 * @see #setUnevaluatedItems(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_UnevaluatedItems()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	Schema getUnevaluatedItems();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUnevaluatedItems <em>Unevaluated Items</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unevaluated Items</em>' containment reference.
	 * @see #getUnevaluatedItems()
	 * @generated
	 */
	void setUnevaluatedItems(Schema value);

	/**
	 * Returns the value of the '<em><b>Unevaluated Properties</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unevaluated Properties</em>' containment reference.
	 * @see #setUnevaluatedProperties(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_UnevaluatedProperties()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	Schema getUnevaluatedProperties();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getUnevaluatedProperties <em>Unevaluated Properties</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unevaluated Properties</em>' containment reference.
	 * @see #getUnevaluatedProperties()
	 * @generated
	 */
	void setUnevaluatedProperties(Schema value);

	/**
	 * Returns the value of the '<em><b>Content Encoding</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content Encoding</em>' attribute.
	 * @see #setContentEncoding(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ContentEncoding()
	 * @model annotation="http://eclipse.org/fennec/jsonschema/model since='draft-07'"
	 * @generated
	 */
	String getContentEncoding();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContentEncoding <em>Content Encoding</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content Encoding</em>' attribute.
	 * @see #getContentEncoding()
	 * @generated
	 */
	void setContentEncoding(String value);

	/**
	 * Returns the value of the '<em><b>Content Media Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content Media Type</em>' attribute.
	 * @see #setContentMediaType(String)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ContentMediaType()
	 * @model annotation="http://eclipse.org/fennec/jsonschema/model since='draft-07'"
	 * @generated
	 */
	String getContentMediaType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContentMediaType <em>Content Media Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content Media Type</em>' attribute.
	 * @see #getContentMediaType()
	 * @generated
	 */
	void setContentMediaType(String value);

	/**
	 * Returns the value of the '<em><b>Content Schema</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content Schema</em>' containment reference.
	 * @see #setContentSchema(Schema)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ContentSchema()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/codec valueReaderName='jsonSchema' valueWriterName='jsonSchema'"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model since='2019-09'"
	 * @generated
	 */
	Schema getContentSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getContentSchema <em>Content Schema</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content Schema</em>' containment reference.
	 * @see #getContentSchema()
	 * @generated
	 */
	void setContentSchema(Schema value);

	/**
	 * Returns the value of the '<em><b>Nullable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * OpenAPI 3.0.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Nullable</em>' attribute.
	 * @see #isSetNullable()
	 * @see #unsetNullable()
	 * @see #setNullable(Boolean)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Nullable()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model until='oas-3.0' dialect='oas'"
	 * @generated
	 */
	Boolean getNullable();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getNullable <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nullable</em>' attribute.
	 * @see #isSetNullable()
	 * @see #unsetNullable()
	 * @see #getNullable()
	 * @generated
	 */
	void setNullable(Boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getNullable <em>Nullable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetNullable()
	 * @see #getNullable()
	 * @see #setNullable(Boolean)
	 * @generated
	 */
	void unsetNullable();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getNullable <em>Nullable</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Nullable</em>' attribute is set.
	 * @see #unsetNullable()
	 * @see #getNullable()
	 * @see #setNullable(Boolean)
	 * @generated
	 */
	boolean isSetNullable();

	/**
	 * Returns the value of the '<em><b>Discriminator</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Discriminator</em>' containment reference.
	 * @see #setDiscriminator(Discriminator)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Discriminator()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model dialect='oas'"
	 * @generated
	 */
	Discriminator getDiscriminator();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getDiscriminator <em>Discriminator</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Discriminator</em>' containment reference.
	 * @see #getDiscriminator()
	 * @generated
	 */
	void setDiscriminator(Discriminator value);

	/**
	 * Returns the value of the '<em><b>Xml</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Xml</em>' containment reference.
	 * @see #setXml(Xml)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Xml()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model dialect='oas'"
	 * @generated
	 */
	Xml getXml();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getXml <em>Xml</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Xml</em>' containment reference.
	 * @see #getXml()
	 * @generated
	 */
	void setXml(Xml value);

	/**
	 * Returns the value of the '<em><b>External Docs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External Docs</em>' containment reference.
	 * @see #setExternalDocs(ExternalDocumentation)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_ExternalDocs()
	 * @model containment="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model dialect='oas'"
	 * @generated
	 */
	ExternalDocumentation getExternalDocs();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExternalDocs <em>External Docs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>External Docs</em>' containment reference.
	 * @see #getExternalDocs()
	 * @generated
	 */
	void setExternalDocs(ExternalDocumentation value);

	/**
	 * Returns the value of the '<em><b>Example</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * OpenAPI: a single example (deprecated in 3.1+ for examples).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Example</em>' attribute.
	 * @see #isSetExample()
	 * @see #unsetExample()
	 * @see #setExample(Object)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Example()
	 * @model unsettable="true"
	 *        annotation="http://eclipse.org/fennec/jsonschema/model dialect='oas'"
	 * @generated
	 */
	Object getExample();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExample <em>Example</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExample <em>Example</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExample()
	 * @see #getExample()
	 * @see #setExample(Object)
	 * @generated
	 */
	void unsetExample();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getExample <em>Example</em>}' attribute is set.
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
	 * Returns the value of the '<em><b>Keywords</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Object},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Keywords of the schema without a feature (unknown keywords, x- extensions), as native JSON values.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Keywords</em>' map.
	 * @see #isSetKeywords()
	 * @see #unsetKeywords()
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Keywords()
	 * @model mapType="org.eclipse.fennec.codec.jsonschema.model.KeywordEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getKeywords();

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getKeywords <em>Keywords</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetKeywords()
	 * @see #getKeywords()
	 * @generated
	 */
	void unsetKeywords();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getKeywords <em>Keywords</em>}' map is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Keywords</em>' map is set.
	 * @see #unsetKeywords()
	 * @see #getKeywords()
	 * @generated
	 */
	boolean isSetKeywords();

	/**
	 * Returns the value of the '<em><b>Classifier</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The Ecore classifier derived from this schema (#318); never written.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Classifier</em>' reference.
	 * @see #setClassifier(EClassifier)
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#getSchema_Classifier()
	 * @model transient="true"
	 * @generated
	 */
	EClassifier getClassifier();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.jsonschema.model.Schema#getClassifier <em>Classifier</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Classifier</em>' reference.
	 * @see #getClassifier()
	 * @generated
	 */
	void setClassifier(EClassifier value);

} // Schema
