/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.metadata.model.codec.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.codec.metadata.model.codec.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class CodecFactoryImpl extends EFactoryImpl implements CodecFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static CodecFactory init() {
		try {
			CodecFactory theCodecFactory = (CodecFactory)EPackage.Registry.INSTANCE.getEFactory(CodecPackage.eNS_URI);
			if (theCodecFactory != null) {
				return theCodecFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new CodecFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CodecFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case CodecPackage.TYPE_SERIALIZATION_CONFIG: return createTypeSerializationConfig();
			case CodecPackage.ID_SERIALIZATION_CONFIG: return createIdSerializationConfig();
			case CodecPackage.REFERENCE_SERIALIZATION_CONFIG: return createReferenceSerializationConfig();
			case CodecPackage.SUPER_TYPE_SERIALIZATION_CONFIG: return createSuperTypeSerializationConfig();
			case CodecPackage.FEATURE_SERIALIZATION_CONFIG: return createFeatureSerializationConfig();
			case CodecPackage.CLASS_CODEC_ASPECT: return createClassCodecAspect();
			case CodecPackage.FEATURE_CODEC_ASPECT: return createFeatureCodecAspect();
			case CodecPackage.REFERENCE_CODEC_ASPECT: return createReferenceCodecAspect();
			case CodecPackage.CODEC_PACKAGE_PROFILE: return createCodecPackageProfile();
			case CodecPackage.CODEC_CLASS_PROFILE: return createCodecClassProfile();
			case CodecPackage.CODEC_CONFIG: return createCodecConfig();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case CodecPackage.STRATEGY_SCOPE:
				return createStrategyScopeFromString(eDataType, initialValue);
			case CodecPackage.TYPE_HINT_MODE:
				return createTypeHintModeFromString(eDataType, initialValue);
			case CodecPackage.DESERIALIZATION_MODE:
				return createDeserializationModeFromString(eDataType, initialValue);
			case CodecPackage.FALLBACK_STRATEGY:
				return createFallbackStrategyFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case CodecPackage.STRATEGY_SCOPE:
				return convertStrategyScopeToString(eDataType, instanceValue);
			case CodecPackage.TYPE_HINT_MODE:
				return convertTypeHintModeToString(eDataType, instanceValue);
			case CodecPackage.DESERIALIZATION_MODE:
				return convertDeserializationModeToString(eDataType, instanceValue);
			case CodecPackage.FALLBACK_STRATEGY:
				return convertFallbackStrategyToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TypeSerializationConfig createTypeSerializationConfig() {
		TypeSerializationConfigImpl typeSerializationConfig = new TypeSerializationConfigImpl();
		return typeSerializationConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IdSerializationConfig createIdSerializationConfig() {
		IdSerializationConfigImpl idSerializationConfig = new IdSerializationConfigImpl();
		return idSerializationConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ReferenceSerializationConfig createReferenceSerializationConfig() {
		ReferenceSerializationConfigImpl referenceSerializationConfig = new ReferenceSerializationConfigImpl();
		return referenceSerializationConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SuperTypeSerializationConfig createSuperTypeSerializationConfig() {
		SuperTypeSerializationConfigImpl superTypeSerializationConfig = new SuperTypeSerializationConfigImpl();
		return superTypeSerializationConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FeatureSerializationConfig createFeatureSerializationConfig() {
		FeatureSerializationConfigImpl featureSerializationConfig = new FeatureSerializationConfigImpl();
		return featureSerializationConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ClassCodecAspect createClassCodecAspect() {
		ClassCodecAspectImpl classCodecAspect = new ClassCodecAspectImpl();
		return classCodecAspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FeatureCodecAspect createFeatureCodecAspect() {
		FeatureCodecAspectImpl featureCodecAspect = new FeatureCodecAspectImpl();
		return featureCodecAspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ReferenceCodecAspect createReferenceCodecAspect() {
		ReferenceCodecAspectImpl referenceCodecAspect = new ReferenceCodecAspectImpl();
		return referenceCodecAspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CodecPackageProfile createCodecPackageProfile() {
		CodecPackageProfileImpl codecPackageProfile = new CodecPackageProfileImpl();
		return codecPackageProfile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CodecClassProfile createCodecClassProfile() {
		CodecClassProfileImpl codecClassProfile = new CodecClassProfileImpl();
		return codecClassProfile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CodecConfig createCodecConfig() {
		CodecConfigImpl codecConfig = new CodecConfigImpl();
		return codecConfig;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StrategyScope createStrategyScopeFromString(EDataType eDataType, String initialValue) {
		StrategyScope result = StrategyScope.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertStrategyScopeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TypeHintMode createTypeHintModeFromString(EDataType eDataType, String initialValue) {
		TypeHintMode result = TypeHintMode.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTypeHintModeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeserializationMode createDeserializationModeFromString(EDataType eDataType, String initialValue) {
		DeserializationMode result = DeserializationMode.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDeserializationModeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FallbackStrategy createFallbackStrategyFromString(EDataType eDataType, String initialValue) {
		FallbackStrategy result = FallbackStrategy.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertFallbackStrategyToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CodecPackage getCodecPackage() {
		return (CodecPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static CodecPackage getPackage() {
		return CodecPackage.eINSTANCE;
	}

} //CodecFactoryImpl
