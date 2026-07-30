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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.fennec.codec.metadata.model.codec.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage
 * @generated
 */
public class CodecAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static CodecPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CodecAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = CodecPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CodecSwitch<Adapter> modelSwitch =
		new CodecSwitch<Adapter>() {
			@Override
			public Adapter caseBaseTypeConfig(BaseTypeConfig object) {
				return createBaseTypeConfigAdapter();
			}
			@Override
			public Adapter caseBaseIdConfig(BaseIdConfig object) {
				return createBaseIdConfigAdapter();
			}
			@Override
			public Adapter caseBaseReferenceConfig(BaseReferenceConfig object) {
				return createBaseReferenceConfigAdapter();
			}
			@Override
			public Adapter caseBaseSuperTypeConfig(BaseSuperTypeConfig object) {
				return createBaseSuperTypeConfigAdapter();
			}
			@Override
			public Adapter caseBaseFeatureConfig(BaseFeatureConfig object) {
				return createBaseFeatureConfigAdapter();
			}
			@Override
			public Adapter caseTypeSerializationConfig(TypeSerializationConfig object) {
				return createTypeSerializationConfigAdapter();
			}
			@Override
			public Adapter caseIdSerializationConfig(IdSerializationConfig object) {
				return createIdSerializationConfigAdapter();
			}
			@Override
			public Adapter caseReferenceSerializationConfig(ReferenceSerializationConfig object) {
				return createReferenceSerializationConfigAdapter();
			}
			@Override
			public Adapter caseSuperTypeSerializationConfig(SuperTypeSerializationConfig object) {
				return createSuperTypeSerializationConfigAdapter();
			}
			@Override
			public Adapter caseFeatureSerializationConfig(FeatureSerializationConfig object) {
				return createFeatureSerializationConfigAdapter();
			}
			@Override
			public Adapter caseClassCodecAspect(ClassCodecAspect object) {
				return createClassCodecAspectAdapter();
			}
			@Override
			public Adapter caseFeatureCodecAspect(FeatureCodecAspect object) {
				return createFeatureCodecAspectAdapter();
			}
			@Override
			public Adapter caseReferenceCodecAspect(ReferenceCodecAspect object) {
				return createReferenceCodecAspectAdapter();
			}
			@Override
			public Adapter caseCodecPackageProfile(CodecPackageProfile object) {
				return createCodecPackageProfileAdapter();
			}
			@Override
			public Adapter caseCodecClassProfile(CodecClassProfile object) {
				return createCodecClassProfileAdapter();
			}
			@Override
			public Adapter caseCodecConfig(CodecConfig object) {
				return createCodecConfigAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig <em>Base Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig
	 * @generated
	 */
	public Adapter createBaseTypeConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig <em>Base Id Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig
	 * @generated
	 */
	public Adapter createBaseIdConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig <em>Base Reference Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig
	 * @generated
	 */
	public Adapter createBaseReferenceConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig <em>Base Super Type Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig
	 * @generated
	 */
	public Adapter createBaseSuperTypeConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig <em>Base Feature Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig
	 * @generated
	 */
	public Adapter createBaseFeatureConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig <em>Type Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig
	 * @generated
	 */
	public Adapter createTypeSerializationConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig <em>Id Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig
	 * @generated
	 */
	public Adapter createIdSerializationConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig <em>Reference Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig
	 * @generated
	 */
	public Adapter createReferenceSerializationConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig <em>Super Type Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig
	 * @generated
	 */
	public Adapter createSuperTypeSerializationConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig <em>Feature Serialization Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig
	 * @generated
	 */
	public Adapter createFeatureSerializationConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect <em>Class Codec Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect
	 * @generated
	 */
	public Adapter createClassCodecAspectAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect <em>Feature Codec Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect
	 * @generated
	 */
	public Adapter createFeatureCodecAspectAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect <em>Reference Codec Aspect</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect
	 * @generated
	 */
	public Adapter createReferenceCodecAspectAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile <em>Package Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile
	 * @generated
	 */
	public Adapter createCodecPackageProfileAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile <em>Class Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile
	 * @generated
	 */
	public Adapter createCodecClassProfileAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.metadata.model.codec.CodecConfig <em>Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecConfig
	 * @generated
	 */
	public Adapter createCodecConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //CodecAdapterFactory
