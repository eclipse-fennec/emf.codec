/*
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 * 
 */
package org.eclipse.fennec.codec.info.codecinfo;

import org.eclipse.emf.common.util.EMap;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeStrategy <em>Type Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#isIgnoreType <em>Ignore Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeKey <em>Type Key</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeMap <em>Type Map</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeValueReaderName <em>Type Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeValueWriterName <em>Type Value Writer Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeMapStrategy <em>Type Map Strategy</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getTypeInfo()
 * @model
 * @generated
 */
@ProviderType
public interface TypeInfo {
	/**
	 * Returns the value of the '<em><b>Type Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This supports the possibility of setting a strategy for type serialization, e.g. if the URI of the class should be used as type, or the class name, or another value.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Strategy</em>' attribute.
	 * @see #setTypeStrategy(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getTypeInfo_TypeStrategy()
	 * @model
	 * @generated
	 */
	String getTypeStrategy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeStrategy <em>Type Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Strategy</em>' attribute.
	 * @see #getTypeStrategy()
	 * @generated
	 */
	void setTypeStrategy(String value);

	/**
	 * Returns the value of the '<em><b>Ignore Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * If set to false, the type information of the EClassifier with such a TypeInfo object will not be serialized, unless this property is then overwritten at a later point.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Ignore Type</em>' attribute.
	 * @see #setIgnoreType(boolean)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getTypeInfo_IgnoreType()
	 * @model
	 * @generated
	 */
	boolean isIgnoreType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#isIgnoreType <em>Ignore Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ignore Type</em>' attribute.
	 * @see #isIgnoreType()
	 * @generated
	 */
	void setIgnoreType(boolean value);

	/**
	 * Returns the value of the '<em><b>Type Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This supports the possibility of specifying a type key, meaning the String that has to be looked for when deserializing the element marked like this.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Key</em>' attribute.
	 * @see #setTypeKey(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getTypeInfo_TypeKey()
	 * @model
	 * @generated
	 */
	String getTypeKey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeKey <em>Type Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Key</em>' attribute.
	 * @see #getTypeKey()
	 * @generated
	 */
	void setTypeKey(String value);

	/**
	 * Returns the value of the '<em><b>Type Map</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This supports the possibility of specifying a map for matching the type key with a certain type, based on its value. The keys are the value of the type key to be expected to found in the document to deserialize, while the value 
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Map</em>' map.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getTypeInfo_TypeMap()
	 * @model mapType="org.eclipse.fennec.codec.info.codecinfo.StringToStringMap&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;"
	 * @generated
	 */
	EMap<String, String> getTypeMap();

	/**
	 * Returns the value of the '<em><b>Type Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type Value Reader Name</em>' attribute.
	 * @see #setTypeValueReaderName(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getTypeInfo_TypeValueReaderName()
	 * @model
	 * @generated
	 */
	String getTypeValueReaderName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeValueReaderName <em>Type Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Value Reader Name</em>' attribute.
	 * @see #getTypeValueReaderName()
	 * @generated
	 */
	void setTypeValueReaderName(String value);

	/**
	 * Returns the value of the '<em><b>Type Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type Value Writer Name</em>' attribute.
	 * @see #setTypeValueWriterName(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getTypeInfo_TypeValueWriterName()
	 * @model
	 * @generated
	 */
	String getTypeValueWriterName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeValueWriterName <em>Type Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Value Writer Name</em>' attribute.
	 * @see #getTypeValueWriterName()
	 * @generated
	 */
	void setTypeValueWriterName(String value);

	/**
	 * Returns the value of the '<em><b>Type Map Strategy</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.info.codecinfo.TypeMapStrategyType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * This specifies whether to overwrite or to merge eventual type mapping coming from the load/save options to the ones provided via model annotation.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Map Strategy</em>' attribute.
	 * @see org.eclipse.fennec.codec.info.codecinfo.TypeMapStrategyType
	 * @see #setTypeMapStrategy(TypeMapStrategyType)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getTypeInfo_TypeMapStrategy()
	 * @model
	 * @generated
	 */
	TypeMapStrategyType getTypeMapStrategy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.TypeInfo#getTypeMapStrategy <em>Type Map Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Map Strategy</em>' attribute.
	 * @see org.eclipse.fennec.codec.info.codecinfo.TypeMapStrategyType
	 * @see #getTypeMapStrategy()
	 * @generated
	 */
	void setTypeMapStrategy(TypeMapStrategyType value);

} // TypeInfo
