/**
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
package org.eclipse.fennec.codec.info.codecinfo.util;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.fennec.codec.info.codecinfo.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage
 * @generated
 */
public class CodecInfoSwitch<T1> extends Switch<T1> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static CodecInfoPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CodecInfoSwitch() {
		if (modelPackage == null) {
			modelPackage = CodecInfoPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T1 doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case CodecInfoPackage.PACKAGE_CODEC_INFO: {
				PackageCodecInfo packageCodecInfo = (PackageCodecInfo)theEObject;
				T1 result = casePackageCodecInfo(packageCodecInfo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.ECLASS_CODEC_INFO: {
				EClassCodecInfo eClassCodecInfo = (EClassCodecInfo)theEObject;
				T1 result = caseEClassCodecInfo(eClassCodecInfo);
				if (result == null) result = caseTypedCodecInfo(eClassCodecInfo);
				if (result == null) result = caseIdentifiableCodecInfo(eClassCodecInfo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.FEATURE_CODEC_INFO: {
				FeatureCodecInfo featureCodecInfo = (FeatureCodecInfo)theEObject;
				T1 result = caseFeatureCodecInfo(featureCodecInfo);
				if (result == null) result = caseTypedCodecInfo(featureCodecInfo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.TYPE_INFO: {
				TypeInfo typeInfo = (TypeInfo)theEObject;
				T1 result = caseTypeInfo(typeInfo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.SUPER_TYPE_INFO: {
				SuperTypeInfo superTypeInfo = (SuperTypeInfo)theEObject;
				T1 result = caseSuperTypeInfo(superTypeInfo);
				if (result == null) result = caseFeatureCodecInfo(superTypeInfo);
				if (result == null) result = caseTypedCodecInfo(superTypeInfo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.IDENTITY_INFO: {
				IdentityInfo identityInfo = (IdentityInfo)theEObject;
				T1 result = caseIdentityInfo(identityInfo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.CODEC_VALUE_READER: {
				CodecValueReader<?, ?> codecValueReader = (CodecValueReader<?, ?>)theEObject;
				T1 result = caseCodecValueReader(codecValueReader);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.CODEC_VALUE_WRITER: {
				CodecValueWriter<?, ?> codecValueWriter = (CodecValueWriter<?, ?>)theEObject;
				T1 result = caseCodecValueWriter(codecValueWriter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.CODEC_INFO_HOLDER: {
				CodecInfoHolder codecInfoHolder = (CodecInfoHolder)theEObject;
				T1 result = caseCodecInfoHolder(codecInfoHolder);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.SAMPLE_VALUE_READER: {
				SampleValueReader<?, ?> sampleValueReader = (SampleValueReader<?, ?>)theEObject;
				T1 result = caseSampleValueReader(sampleValueReader);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.STRING_TO_STRING_MAP: {
				@SuppressWarnings("unchecked") Map.Entry<String, String> stringToStringMap = (Map.Entry<String, String>)theEObject;
				T1 result = caseStringToStringMap(stringToStringMap);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.TYPED_CODEC_INFO: {
				TypedCodecInfo typedCodecInfo = (TypedCodecInfo)theEObject;
				T1 result = caseTypedCodecInfo(typedCodecInfo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecInfoPackage.IDENTIFIABLE_CODEC_INFO: {
				IdentifiableCodecInfo identifiableCodecInfo = (IdentifiableCodecInfo)theEObject;
				T1 result = caseIdentifiableCodecInfo(identifiableCodecInfo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Package Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Package Codec Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 casePackageCodecInfo(PackageCodecInfo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EClass Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EClass Codec Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseEClassCodecInfo(EClassCodecInfo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Feature Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Feature Codec Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseFeatureCodecInfo(FeatureCodecInfo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Type Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Type Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseTypeInfo(TypeInfo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Super Type Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Super Type Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseSuperTypeInfo(SuperTypeInfo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Identity Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Identity Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseIdentityInfo(IdentityInfo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Codec Value Reader</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Codec Value Reader</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <V, T> T1 caseCodecValueReader(CodecValueReader<V, T> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Codec Value Writer</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Codec Value Writer</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <T, V> T1 caseCodecValueWriter(CodecValueWriter<T, V> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Holder</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Holder</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseCodecInfoHolder(CodecInfoHolder object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sample Value Reader</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sample Value Reader</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <V, T> T1 caseSampleValueReader(SampleValueReader<V, T> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseStringToStringMap(Map.Entry<String, String> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Typed Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Typed Codec Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseTypedCodecInfo(TypedCodecInfo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Identifiable Codec Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Identifiable Codec Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseIdentifiableCodecInfo(IdentifiableCodecInfo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T1 defaultCase(EObject object) {
		return null;
	}

} //CodecInfoSwitch
