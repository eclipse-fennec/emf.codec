/**
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
package org.eclipse.fennec.codec.metadata.model.codec.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.fennec.codec.metadata.model.codec.*;

import org.eclipse.fennec.model.metadata.Aspect;
import org.eclipse.fennec.model.metadata.BaseFeatureConfig;
import org.eclipse.fennec.model.metadata.BaseIdConfig;
import org.eclipse.fennec.model.metadata.BaseReferenceConfig;
import org.eclipse.fennec.model.metadata.BaseSuperTypeConfig;
import org.eclipse.fennec.model.metadata.BaseTypeConfig;
import org.eclipse.fennec.model.metadata.ClassAspect;
import org.eclipse.fennec.model.metadata.ClassProfile;
import org.eclipse.fennec.model.metadata.FeatureAspect;
import org.eclipse.fennec.model.metadata.PackageProfile;

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
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage
 * @generated
 */
public class CodecSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static CodecPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CodecSwitch() {
		if (modelPackage == null) {
			modelPackage = CodecPackage.eINSTANCE;
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
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case CodecPackage.TYPE_SERIALIZATION_CONFIG: {
				TypeSerializationConfig typeSerializationConfig = (TypeSerializationConfig)theEObject;
				T result = caseTypeSerializationConfig(typeSerializationConfig);
				if (result == null) result = caseBaseTypeConfig(typeSerializationConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.ID_SERIALIZATION_CONFIG: {
				IdSerializationConfig idSerializationConfig = (IdSerializationConfig)theEObject;
				T result = caseIdSerializationConfig(idSerializationConfig);
				if (result == null) result = caseBaseIdConfig(idSerializationConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG: {
				ReferenceSerializationConfig referenceSerializationConfig = (ReferenceSerializationConfig)theEObject;
				T result = caseReferenceSerializationConfig(referenceSerializationConfig);
				if (result == null) result = caseBaseReferenceConfig(referenceSerializationConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.SUPER_TYPE_SERIALIZATION_CONFIG: {
				SuperTypeSerializationConfig superTypeSerializationConfig = (SuperTypeSerializationConfig)theEObject;
				T result = caseSuperTypeSerializationConfig(superTypeSerializationConfig);
				if (result == null) result = caseBaseSuperTypeConfig(superTypeSerializationConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG: {
				FeatureSerializationConfig featureSerializationConfig = (FeatureSerializationConfig)theEObject;
				T result = caseFeatureSerializationConfig(featureSerializationConfig);
				if (result == null) result = caseBaseFeatureConfig(featureSerializationConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.CLASS_CODEC_ASPECT: {
				ClassCodecAspect classCodecAspect = (ClassCodecAspect)theEObject;
				T result = caseClassCodecAspect(classCodecAspect);
				if (result == null) result = caseClassAspect(classCodecAspect);
				if (result == null) result = caseAspect(classCodecAspect);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.FEATURE_CODEC_ASPECT: {
				FeatureCodecAspect featureCodecAspect = (FeatureCodecAspect)theEObject;
				T result = caseFeatureCodecAspect(featureCodecAspect);
				if (result == null) result = caseFeatureAspect(featureCodecAspect);
				if (result == null) result = caseAspect(featureCodecAspect);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.REFERENCE_CODEC_ASPECT: {
				ReferenceCodecAspect referenceCodecAspect = (ReferenceCodecAspect)theEObject;
				T result = caseReferenceCodecAspect(referenceCodecAspect);
				if (result == null) result = caseFeatureCodecAspect(referenceCodecAspect);
				if (result == null) result = caseFeatureAspect(referenceCodecAspect);
				if (result == null) result = caseAspect(referenceCodecAspect);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.CODEC_PACKAGE_PROFILE: {
				CodecPackageProfile codecPackageProfile = (CodecPackageProfile)theEObject;
				T result = caseCodecPackageProfile(codecPackageProfile);
				if (result == null) result = casePackageProfile(codecPackageProfile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.CODEC_CLASS_PROFILE: {
				CodecClassProfile codecClassProfile = (CodecClassProfile)theEObject;
				T result = caseCodecClassProfile(codecClassProfile);
				if (result == null) result = caseClassProfile(codecClassProfile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CodecPackage.CODEC_CONFIG: {
				CodecConfig codecConfig = (CodecConfig)theEObject;
				T result = caseCodecConfig(codecConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Type Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Type Serialization Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTypeSerializationConfig(TypeSerializationConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Id Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Id Serialization Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIdSerializationConfig(IdSerializationConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Reference Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Reference Serialization Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReferenceSerializationConfig(ReferenceSerializationConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Super Type Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Super Type Serialization Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSuperTypeSerializationConfig(SuperTypeSerializationConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Feature Serialization Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Feature Serialization Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFeatureSerializationConfig(FeatureSerializationConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Class Codec Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Class Codec Aspect</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseClassCodecAspect(ClassCodecAspect object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Feature Codec Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Feature Codec Aspect</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFeatureCodecAspect(FeatureCodecAspect object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Reference Codec Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Reference Codec Aspect</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReferenceCodecAspect(ReferenceCodecAspect object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Package Profile</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Package Profile</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCodecPackageProfile(CodecPackageProfile object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Class Profile</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Class Profile</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCodecClassProfile(CodecClassProfile object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCodecConfig(CodecConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Base Type Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Base Type Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBaseTypeConfig(BaseTypeConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Base Id Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Base Id Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBaseIdConfig(BaseIdConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Base Reference Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Base Reference Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBaseReferenceConfig(BaseReferenceConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Base Super Type Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Base Super Type Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBaseSuperTypeConfig(BaseSuperTypeConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Base Feature Config</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Base Feature Config</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBaseFeatureConfig(BaseFeatureConfig object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Aspect</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAspect(Aspect object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Class Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Class Aspect</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseClassAspect(ClassAspect object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Feature Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Feature Aspect</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFeatureAspect(FeatureAspect object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Package Profile</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Package Profile</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePackageProfile(PackageProfile object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Class Profile</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Class Profile</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseClassProfile(ClassProfile object) {
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
	public T defaultCase(EObject object) {
		return null;
	}

} //CodecSwitch
