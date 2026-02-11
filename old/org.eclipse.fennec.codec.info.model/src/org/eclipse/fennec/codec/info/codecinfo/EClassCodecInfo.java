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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClassifier;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EClass Codec Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getClassifier <em>Classifier</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getSuperTypeInfo <em>Super Type Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getFeatureInfo <em>Feature Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getReferenceCodecInfo <em>Reference Codec Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getAttributeCodecInfo <em>Attribute Codec Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getOperationCodecInfo <em>Operation Codec Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getEnumeratorCodecInfo <em>Enumerator Codec Info</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getCodecExtraProperties <em>Codec Extra Properties</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo()
 * @model
 * @generated
 */
@ProviderType
public interface EClassCodecInfo extends TypedCodecInfo, IdentifiableCodecInfo {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Classifier</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Classifier</em>' reference.
	 * @see #setClassifier(EClassifier)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo_Classifier()
	 * @model
	 * @generated
	 */
	EClassifier getClassifier();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getClassifier <em>Classifier</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Classifier</em>' reference.
	 * @see #getClassifier()
	 * @generated
	 */
	void setClassifier(EClassifier value);

	/**
	 * Returns the value of the '<em><b>Super Type Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Super Type Info</em>' containment reference.
	 * @see #setSuperTypeInfo(SuperTypeInfo)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo_SuperTypeInfo()
	 * @model containment="true"
	 * @generated
	 */
	SuperTypeInfo getSuperTypeInfo();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo#getSuperTypeInfo <em>Super Type Info</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Super Type Info</em>' containment reference.
	 * @see #getSuperTypeInfo()
	 * @generated
	 */
	void setSuperTypeInfo(SuperTypeInfo value);

	/**
	 * Returns the value of the '<em><b>Feature Info</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Feature Info</em>' containment reference list.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo_FeatureInfo()
	 * @model containment="true"
	 * @generated
	 */
	EList<FeatureCodecInfo> getFeatureInfo();

	/**
	 * Returns the value of the '<em><b>Reference Codec Info</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reference Codec Info</em>' reference list.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo_ReferenceCodecInfo()
	 * @model transient="true" volatile="true" derived="true" suppressedSetVisibility="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel get='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.asEList(getFeatureInfo().stream().filter(f-&gt;&lt;%org.eclipse.fennec.codec.info.codecinfo.InfoType%&gt;.REFERENCE.equals(f.getType())).collect(&lt;%java.util.stream.Collectors%&gt;.toList()));'"
	 * @generated
	 */
	EList<FeatureCodecInfo> getReferenceCodecInfo();

	/**
	 * Returns the value of the '<em><b>Attribute Codec Info</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute Codec Info</em>' reference list.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo_AttributeCodecInfo()
	 * @model transient="true" volatile="true" derived="true" suppressedSetVisibility="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel get='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.asEList(getFeatureInfo().stream().filter(f-&gt;&lt;%org.eclipse.fennec.codec.info.codecinfo.InfoType%&gt;.ATTRIBUTE.equals(f.getType())).collect(&lt;%java.util.stream.Collectors%&gt;.toList()));'"
	 * @generated
	 */
	EList<FeatureCodecInfo> getAttributeCodecInfo();

	/**
	 * Returns the value of the '<em><b>Operation Codec Info</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Operation Codec Info</em>' reference list.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo_OperationCodecInfo()
	 * @model transient="true" volatile="true" derived="true" suppressedSetVisibility="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel get='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.asEList(getFeatureInfo().stream().filter(f-&gt;&lt;%org.eclipse.fennec.codec.info.codecinfo.InfoType%&gt;.OPERATION.equals(f.getType())).collect(&lt;%java.util.stream.Collectors%&gt;.toList()));'"
	 * @generated
	 */
	EList<FeatureCodecInfo> getOperationCodecInfo();

	/**
	 * Returns the value of the '<em><b>Enumerator Codec Info</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enumerator Codec Info</em>' reference list.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo_EnumeratorCodecInfo()
	 * @model transient="true" volatile="true" derived="true" suppressedSetVisibility="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel get='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.asEList(getFeatureInfo().stream().filter(f-&gt;&lt;%org.eclipse.fennec.codec.info.codecinfo.InfoType%&gt;.ENUMERATOR.equals(f.getType())).collect(&lt;%java.util.stream.Collectors%&gt;.toList()));'"
	 * @generated
	 */
	EList<FeatureCodecInfo> getEnumeratorCodecInfo();

	/**
	 * Returns the value of the '<em><b>Codec Extra Properties</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Codec Extra Properties</em>' map.
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage#getEClassCodecInfo_CodecExtraProperties()
	 * @model mapType="org.eclipse.fennec.codec.info.codecinfo.StringToStringMap&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;"
	 * @generated
	 */
	EMap<String, String> getCodecExtraProperties();

} // EClassCodecInfo
