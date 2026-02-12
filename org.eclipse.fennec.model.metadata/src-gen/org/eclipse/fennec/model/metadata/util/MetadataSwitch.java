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
package org.eclipse.fennec.model.metadata.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.fennec.model.metadata.*;

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
 * @see org.eclipse.fennec.model.metadata.MetadataPackage
 * @generated
 */
public class MetadataSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MetadataPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetadataSwitch() {
		if (modelPackage == null) {
			modelPackage = MetadataPackage.eINSTANCE;
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
			case MetadataPackage.METADATA_DIAGNOSTIC: {
				MetadataDiagnostic metadataDiagnostic = (MetadataDiagnostic)theEObject;
				T result = caseMetadataDiagnostic(metadataDiagnostic);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.DIAGNOSTIC_CONTAINER: {
				DiagnosticContainer diagnosticContainer = (DiagnosticContainer)theEObject;
				T result = caseDiagnosticContainer(diagnosticContainer);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.BASE_TYPE_CONFIG: {
				BaseTypeConfig baseTypeConfig = (BaseTypeConfig)theEObject;
				T result = caseBaseTypeConfig(baseTypeConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.BASE_ID_CONFIG: {
				BaseIdConfig baseIdConfig = (BaseIdConfig)theEObject;
				T result = caseBaseIdConfig(baseIdConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.BASE_REFERENCE_CONFIG: {
				BaseReferenceConfig baseReferenceConfig = (BaseReferenceConfig)theEObject;
				T result = caseBaseReferenceConfig(baseReferenceConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.BASE_SUPER_TYPE_CONFIG: {
				BaseSuperTypeConfig baseSuperTypeConfig = (BaseSuperTypeConfig)theEObject;
				T result = caseBaseSuperTypeConfig(baseSuperTypeConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.BASE_FEATURE_CONFIG: {
				BaseFeatureConfig baseFeatureConfig = (BaseFeatureConfig)theEObject;
				T result = caseBaseFeatureConfig(baseFeatureConfig);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.ASPECT: {
				Aspect aspect = (Aspect)theEObject;
				T result = caseAspect(aspect);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.PACKAGE_ASPECT: {
				PackageAspect packageAspect = (PackageAspect)theEObject;
				T result = casePackageAspect(packageAspect);
				if (result == null) result = caseAspect(packageAspect);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.CLASS_ASPECT: {
				ClassAspect classAspect = (ClassAspect)theEObject;
				T result = caseClassAspect(classAspect);
				if (result == null) result = caseAspect(classAspect);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.FEATURE_ASPECT: {
				FeatureAspect featureAspect = (FeatureAspect)theEObject;
				T result = caseFeatureAspect(featureAspect);
				if (result == null) result = caseAspect(featureAspect);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.PACKAGE_PROFILE: {
				PackageProfile packageProfile = (PackageProfile)theEObject;
				T result = casePackageProfile(packageProfile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.CLASS_PROFILE: {
				ClassProfile classProfile = (ClassProfile)theEObject;
				T result = caseClassProfile(classProfile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.PACKAGE_METADATA: {
				PackageMetadata packageMetadata = (PackageMetadata)theEObject;
				T result = casePackageMetadata(packageMetadata);
				if (result == null) result = caseDiagnosticContainer(packageMetadata);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.CLASS_METADATA: {
				ClassMetadata classMetadata = (ClassMetadata)theEObject;
				T result = caseClassMetadata(classMetadata);
				if (result == null) result = caseDiagnosticContainer(classMetadata);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.FEATURE_METADATA: {
				FeatureMetadata featureMetadata = (FeatureMetadata)theEObject;
				T result = caseFeatureMetadata(featureMetadata);
				if (result == null) result = caseDiagnosticContainer(featureMetadata);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.ATTRIBUTE_METADATA: {
				AttributeMetadata attributeMetadata = (AttributeMetadata)theEObject;
				T result = caseAttributeMetadata(attributeMetadata);
				if (result == null) result = caseFeatureMetadata(attributeMetadata);
				if (result == null) result = caseDiagnosticContainer(attributeMetadata);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.REFERENCE_METADATA: {
				ReferenceMetadata referenceMetadata = (ReferenceMetadata)theEObject;
				T result = caseReferenceMetadata(referenceMetadata);
				if (result == null) result = caseFeatureMetadata(referenceMetadata);
				if (result == null) result = caseDiagnosticContainer(referenceMetadata);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MetadataPackage.METADATA_REGISTRY: {
				MetadataRegistry metadataRegistry = (MetadataRegistry)theEObject;
				T result = caseMetadataRegistry(metadataRegistry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Diagnostic</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Diagnostic</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMetadataDiagnostic(MetadataDiagnostic object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Diagnostic Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Diagnostic Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDiagnosticContainer(DiagnosticContainer object) {
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
	 * Returns the result of interpreting the object as an instance of '<em>Package Aspect</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Package Aspect</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePackageAspect(PackageAspect object) {
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
	 * Returns the result of interpreting the object as an instance of '<em>Package Metadata</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Package Metadata</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePackageMetadata(PackageMetadata object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Class Metadata</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Class Metadata</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseClassMetadata(ClassMetadata object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Feature Metadata</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Feature Metadata</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFeatureMetadata(FeatureMetadata object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attribute Metadata</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attribute Metadata</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttributeMetadata(AttributeMetadata object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Reference Metadata</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Reference Metadata</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReferenceMetadata(ReferenceMetadata object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Registry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Registry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMetadataRegistry(MetadataRegistry object) {
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

} //MetadataSwitch
