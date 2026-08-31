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
package org.eclipse.fennec.codec.metadata.model.codec.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.codec.metadata.model.codec.BaseFeatureConfig;
import org.eclipse.fennec.codec.metadata.model.codec.BaseIdConfig;
import org.eclipse.fennec.codec.metadata.model.codec.BaseReferenceConfig;
import org.eclipse.fennec.codec.metadata.model.codec.BaseSuperTypeConfig;
import org.eclipse.fennec.codec.metadata.model.codec.BaseTypeConfig;
import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile;
import org.eclipse.fennec.codec.metadata.model.codec.CodecConfig;
import org.eclipse.fennec.codec.metadata.model.codec.CodecFactory;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile;
import org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode;
import org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode;
import org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.StrategyScope;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class CodecPackageImpl extends EPackageImpl implements CodecPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass baseTypeConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass baseIdConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass baseReferenceConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass baseSuperTypeConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass baseFeatureConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass typeSerializationConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass idSerializationConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass referenceSerializationConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass superTypeSerializationConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass featureSerializationConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass classCodecAspectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass featureCodecAspectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass referenceCodecAspectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass codecPackageProfileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass codecClassProfileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass codecConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum strategyScopeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum typeHintModeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum deserializationModeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum fallbackStrategyEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum fingerprintModeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum serializationFormatEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum typeStrategyEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum idStrategyEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum idKeyModeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum superTypeSelectionEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum enumSerializationStrategyEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private CodecPackageImpl() {
		super(eNS_URI, CodecFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link CodecPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static CodecPackage init() {
		if (isInited) return (CodecPackage)EPackage.Registry.INSTANCE.getEPackage(CodecPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredCodecPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		CodecPackageImpl theCodecPackage = registeredCodecPackage instanceof CodecPackageImpl ? (CodecPackageImpl)registeredCodecPackage : new CodecPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theCodecPackage.createPackageContents();

		// Initialize created meta-data
		theCodecPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theCodecPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(CodecPackage.eNS_URI, theCodecPackage);
		return theCodecPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBaseTypeConfig() {
		return baseTypeConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseTypeConfig_Format() {
		return (EAttribute)baseTypeConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseTypeConfig_Strategy() {
		return (EAttribute)baseTypeConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseTypeConfig_TypeKey() {
		return (EAttribute)baseTypeConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseTypeConfig_SchemaKey() {
		return (EAttribute)baseTypeConfigEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseTypeConfig_NameKey() {
		return (EAttribute)baseTypeConfigEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBaseIdConfig() {
		return baseIdConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseIdConfig_Strategy() {
		return (EAttribute)baseIdConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseIdConfig_KeyMode() {
		return (EAttribute)baseIdConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseIdConfig_Format() {
		return (EAttribute)baseIdConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseIdConfig_IdKey() {
		return (EAttribute)baseIdConfigEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseIdConfig_Separator() {
		return (EAttribute)baseIdConfigEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseIdConfig_OnTop() {
		return (EAttribute)baseIdConfigEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseIdConfig_SerializeSeparator() {
		return (EAttribute)baseIdConfigEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseIdConfig_SeparatorKey() {
		return (EAttribute)baseIdConfigEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseIdConfig_ValueKey() {
		return (EAttribute)baseIdConfigEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBaseReferenceConfig() {
		return baseReferenceConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseReferenceConfig_Format() {
		return (EAttribute)baseReferenceConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseReferenceConfig_TypeKey() {
		return (EAttribute)baseReferenceConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseReferenceConfig_RefKey() {
		return (EAttribute)baseReferenceConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBaseSuperTypeConfig() {
		return baseSuperTypeConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseSuperTypeConfig_Enabled() {
		return (EAttribute)baseSuperTypeConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseSuperTypeConfig_Selection() {
		return (EAttribute)baseSuperTypeConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseSuperTypeConfig_Format() {
		return (EAttribute)baseSuperTypeConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseSuperTypeConfig_AsArray() {
		return (EAttribute)baseSuperTypeConfigEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseSuperTypeConfig_Separator() {
		return (EAttribute)baseSuperTypeConfigEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseSuperTypeConfig_SuperTypeKey() {
		return (EAttribute)baseSuperTypeConfigEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBaseFeatureConfig() {
		return baseFeatureConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_Key() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_Ignore() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_IgnoreRead() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_IgnoreWrite() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_ForceRead() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_ForceWrite() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_SerializeNull() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_SerializeEmpty() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_SerializeDefaults() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseFeatureConfig_EnumSerialization() {
		return (EAttribute)baseFeatureConfigEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTypeSerializationConfig() {
		return typeSerializationConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTypeSerializationConfig_MapId() {
		return (EAttribute)typeSerializationConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTypeSerializationConfig_DiscriminatorPath() {
		return (EAttribute)typeSerializationConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTypeSerializationConfig_DiscriminatorValue() {
		return (EAttribute)typeSerializationConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTypeSerializationConfig_FingerprintMode() {
		return (EAttribute)typeSerializationConfigEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTypeSerializationConfig_FingerprintKey() {
		return (EAttribute)typeSerializationConfigEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTypeSerializationConfig_StrategyScope() {
		return (EAttribute)typeSerializationConfigEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTypeSerializationConfig_FormatScope() {
		return (EAttribute)typeSerializationConfigEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIdSerializationConfig() {
		return idSerializationConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIdSerializationConfig_IdFeatures() {
		return (EAttribute)idSerializationConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIdSerializationConfig_IdValueWriterName() {
		return (EAttribute)idSerializationConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIdSerializationConfig_IdValueReaderName() {
		return (EAttribute)idSerializationConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIdSerializationConfig_StrategyScope() {
		return (EAttribute)idSerializationConfigEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIdSerializationConfig_FormatScope() {
		return (EAttribute)idSerializationConfigEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getReferenceSerializationConfig() {
		return referenceSerializationConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReferenceSerializationConfig_IncludeType() {
		return (EAttribute)referenceSerializationConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReferenceSerializationConfig_Expand() {
		return (EAttribute)referenceSerializationConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSuperTypeSerializationConfig() {
		return superTypeSerializationConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSuperTypeSerializationConfig_UseSmartCompression() {
		return (EAttribute)superTypeSerializationConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFeatureSerializationConfig() {
		return featureSerializationConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureSerializationConfig_FeatureName() {
		return (EAttribute)featureSerializationConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureSerializationConfig_ValueWriterName() {
		return (EAttribute)featureSerializationConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureSerializationConfig_ValueReaderName() {
		return (EAttribute)featureSerializationConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureSerializationConfig_Expand() {
		return (EAttribute)featureSerializationConfigEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFeatureSerializationConfig_ReferenceConfig() {
		return (EReference)featureSerializationConfigEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFeatureSerializationConfig_TypeConfig() {
		return (EReference)featureSerializationConfigEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getClassCodecAspect() {
		return classCodecAspectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassCodecAspect_TypeConfig() {
		return (EReference)classCodecAspectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassCodecAspect_IdConfig() {
		return (EReference)classCodecAspectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassCodecAspect_SuperTypeConfig() {
		return (EReference)classCodecAspectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassCodecAspect_InheritFromParent() {
		return (EAttribute)classCodecAspectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassCodecAspect_DiscriminatorValue() {
		return (EAttribute)classCodecAspectEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassCodecAspect_StrictOnUnknown() {
		return (EAttribute)classCodecAspectEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassCodecAspect_StrictOnMissing() {
		return (EAttribute)classCodecAspectEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassCodecAspect_MetadataMerge() {
		return (EAttribute)classCodecAspectEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassCodecAspect_MetadataKey() {
		return (EAttribute)classCodecAspectEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFeatureCodecAspect() {
		return featureCodecAspectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_EffectiveKey() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_Ignore() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_IgnoreRead() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_IgnoreWrite() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_ForceRead() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_ForceWrite() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_SerializeNull() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_SerializeEmpty() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_SerializeDefaults() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_ValueWriterName() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_ValueReaderName() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_EnumSerialization() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureCodecAspect_DateFormat() {
		return (EAttribute)featureCodecAspectEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getReferenceCodecAspect() {
		return referenceCodecAspectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReferenceCodecAspect_ReferenceConfig() {
		return (EReference)referenceCodecAspectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReferenceCodecAspect_TypeConfig() {
		return (EReference)referenceCodecAspectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReferenceCodecAspect_InheritTypeFromTarget() {
		return (EAttribute)referenceCodecAspectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReferenceCodecAspect_Expand() {
		return (EAttribute)referenceCodecAspectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCodecPackageProfile() {
		return codecPackageProfileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecPackageProfile_ClassProfiles() {
		return (EReference)codecPackageProfileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCodecClassProfile() {
		return codecClassProfileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecClassProfile_EClass() {
		return (EReference)codecClassProfileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecClassProfile_TypeConfig() {
		return (EReference)codecClassProfileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecClassProfile_IdConfig() {
		return (EReference)codecClassProfileEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecClassProfile_SuperTypeConfig() {
		return (EReference)codecClassProfileEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecClassProfile_FeatureConfigs() {
		return (EReference)codecClassProfileEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCodecConfig() {
		return codecConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_Format() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_UseNumericIds() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecConfig_TypeConfig() {
		return (EReference)codecConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecConfig_ContainmentTypeConfig() {
		return (EReference)codecConfigEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecConfig_ReferenceTypeConfig() {
		return (EReference)codecConfigEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecConfig_IdConfig() {
		return (EReference)codecConfigEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecConfig_ReferenceConfig() {
		return (EReference)codecConfigEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecConfig_SuperTypeConfig() {
		return (EReference)codecConfigEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecConfig_FeatureConfigs() {
		return (EReference)codecConfigEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_Expand() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_ExpandDepth() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_ExpandIgnoreBidirectional() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_SerializeNull() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_SerializeEmpty() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_SerializeDefaults() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_TypeHintMode() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_DeserializationMode() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_StrictOnUnknown() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_StrictOnMissing() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_MetadataMerge() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCodecConfig_MetadataKey() {
		return (EAttribute)codecConfigEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getStrategyScope() {
		return strategyScopeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getTypeHintMode() {
		return typeHintModeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getDeserializationMode() {
		return deserializationModeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getFallbackStrategy() {
		return fallbackStrategyEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getFingerprintMode() {
		return fingerprintModeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getSerializationFormat() {
		return serializationFormatEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getTypeStrategy() {
		return typeStrategyEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getIdStrategy() {
		return idStrategyEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getIdKeyMode() {
		return idKeyModeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getSuperTypeSelection() {
		return superTypeSelectionEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getEnumSerializationStrategy() {
		return enumSerializationStrategyEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CodecFactory getCodecFactory() {
		return (CodecFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		baseTypeConfigEClass = createEClass(BASE_TYPE_CONFIG);
		createEAttribute(baseTypeConfigEClass, BASE_TYPE_CONFIG__FORMAT);
		createEAttribute(baseTypeConfigEClass, BASE_TYPE_CONFIG__STRATEGY);
		createEAttribute(baseTypeConfigEClass, BASE_TYPE_CONFIG__TYPE_KEY);
		createEAttribute(baseTypeConfigEClass, BASE_TYPE_CONFIG__SCHEMA_KEY);
		createEAttribute(baseTypeConfigEClass, BASE_TYPE_CONFIG__NAME_KEY);

		baseIdConfigEClass = createEClass(BASE_ID_CONFIG);
		createEAttribute(baseIdConfigEClass, BASE_ID_CONFIG__STRATEGY);
		createEAttribute(baseIdConfigEClass, BASE_ID_CONFIG__KEY_MODE);
		createEAttribute(baseIdConfigEClass, BASE_ID_CONFIG__FORMAT);
		createEAttribute(baseIdConfigEClass, BASE_ID_CONFIG__ID_KEY);
		createEAttribute(baseIdConfigEClass, BASE_ID_CONFIG__SEPARATOR);
		createEAttribute(baseIdConfigEClass, BASE_ID_CONFIG__ON_TOP);
		createEAttribute(baseIdConfigEClass, BASE_ID_CONFIG__SERIALIZE_SEPARATOR);
		createEAttribute(baseIdConfigEClass, BASE_ID_CONFIG__SEPARATOR_KEY);
		createEAttribute(baseIdConfigEClass, BASE_ID_CONFIG__VALUE_KEY);

		baseReferenceConfigEClass = createEClass(BASE_REFERENCE_CONFIG);
		createEAttribute(baseReferenceConfigEClass, BASE_REFERENCE_CONFIG__FORMAT);
		createEAttribute(baseReferenceConfigEClass, BASE_REFERENCE_CONFIG__TYPE_KEY);
		createEAttribute(baseReferenceConfigEClass, BASE_REFERENCE_CONFIG__REF_KEY);

		baseSuperTypeConfigEClass = createEClass(BASE_SUPER_TYPE_CONFIG);
		createEAttribute(baseSuperTypeConfigEClass, BASE_SUPER_TYPE_CONFIG__ENABLED);
		createEAttribute(baseSuperTypeConfigEClass, BASE_SUPER_TYPE_CONFIG__SELECTION);
		createEAttribute(baseSuperTypeConfigEClass, BASE_SUPER_TYPE_CONFIG__FORMAT);
		createEAttribute(baseSuperTypeConfigEClass, BASE_SUPER_TYPE_CONFIG__AS_ARRAY);
		createEAttribute(baseSuperTypeConfigEClass, BASE_SUPER_TYPE_CONFIG__SEPARATOR);
		createEAttribute(baseSuperTypeConfigEClass, BASE_SUPER_TYPE_CONFIG__SUPER_TYPE_KEY);

		baseFeatureConfigEClass = createEClass(BASE_FEATURE_CONFIG);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__KEY);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__IGNORE);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__IGNORE_READ);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__IGNORE_WRITE);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__FORCE_READ);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__FORCE_WRITE);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__SERIALIZE_NULL);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__SERIALIZE_EMPTY);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__SERIALIZE_DEFAULTS);
		createEAttribute(baseFeatureConfigEClass, BASE_FEATURE_CONFIG__ENUM_SERIALIZATION);

		typeSerializationConfigEClass = createEClass(TYPE_SERIALIZATION_CONFIG);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__MAP_ID);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__FINGERPRINT_MODE);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__FINGERPRINT_KEY);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__STRATEGY_SCOPE);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__FORMAT_SCOPE);

		idSerializationConfigEClass = createEClass(ID_SERIALIZATION_CONFIG);
		createEAttribute(idSerializationConfigEClass, ID_SERIALIZATION_CONFIG__ID_FEATURES);
		createEAttribute(idSerializationConfigEClass, ID_SERIALIZATION_CONFIG__ID_VALUE_WRITER_NAME);
		createEAttribute(idSerializationConfigEClass, ID_SERIALIZATION_CONFIG__ID_VALUE_READER_NAME);
		createEAttribute(idSerializationConfigEClass, ID_SERIALIZATION_CONFIG__STRATEGY_SCOPE);
		createEAttribute(idSerializationConfigEClass, ID_SERIALIZATION_CONFIG__FORMAT_SCOPE);

		referenceSerializationConfigEClass = createEClass(REFERENCE_SERIALIZATION_CONFIG);
		createEAttribute(referenceSerializationConfigEClass, REFERENCE_SERIALIZATION_CONFIG__INCLUDE_TYPE);
		createEAttribute(referenceSerializationConfigEClass, REFERENCE_SERIALIZATION_CONFIG__EXPAND);

		superTypeSerializationConfigEClass = createEClass(SUPER_TYPE_SERIALIZATION_CONFIG);
		createEAttribute(superTypeSerializationConfigEClass, SUPER_TYPE_SERIALIZATION_CONFIG__USE_SMART_COMPRESSION);

		featureSerializationConfigEClass = createEClass(FEATURE_SERIALIZATION_CONFIG);
		createEAttribute(featureSerializationConfigEClass, FEATURE_SERIALIZATION_CONFIG__FEATURE_NAME);
		createEAttribute(featureSerializationConfigEClass, FEATURE_SERIALIZATION_CONFIG__VALUE_WRITER_NAME);
		createEAttribute(featureSerializationConfigEClass, FEATURE_SERIALIZATION_CONFIG__VALUE_READER_NAME);
		createEAttribute(featureSerializationConfigEClass, FEATURE_SERIALIZATION_CONFIG__EXPAND);
		createEReference(featureSerializationConfigEClass, FEATURE_SERIALIZATION_CONFIG__REFERENCE_CONFIG);
		createEReference(featureSerializationConfigEClass, FEATURE_SERIALIZATION_CONFIG__TYPE_CONFIG);

		classCodecAspectEClass = createEClass(CLASS_CODEC_ASPECT);
		createEReference(classCodecAspectEClass, CLASS_CODEC_ASPECT__TYPE_CONFIG);
		createEReference(classCodecAspectEClass, CLASS_CODEC_ASPECT__ID_CONFIG);
		createEReference(classCodecAspectEClass, CLASS_CODEC_ASPECT__SUPER_TYPE_CONFIG);
		createEAttribute(classCodecAspectEClass, CLASS_CODEC_ASPECT__INHERIT_FROM_PARENT);
		createEAttribute(classCodecAspectEClass, CLASS_CODEC_ASPECT__DISCRIMINATOR_VALUE);
		createEAttribute(classCodecAspectEClass, CLASS_CODEC_ASPECT__STRICT_ON_UNKNOWN);
		createEAttribute(classCodecAspectEClass, CLASS_CODEC_ASPECT__STRICT_ON_MISSING);
		createEAttribute(classCodecAspectEClass, CLASS_CODEC_ASPECT__METADATA_MERGE);
		createEAttribute(classCodecAspectEClass, CLASS_CODEC_ASPECT__METADATA_KEY);

		featureCodecAspectEClass = createEClass(FEATURE_CODEC_ASPECT);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__EFFECTIVE_KEY);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__IGNORE);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__IGNORE_READ);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__IGNORE_WRITE);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__FORCE_READ);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__FORCE_WRITE);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__SERIALIZE_NULL);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__SERIALIZE_EMPTY);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__SERIALIZE_DEFAULTS);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__VALUE_WRITER_NAME);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__VALUE_READER_NAME);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__ENUM_SERIALIZATION);
		createEAttribute(featureCodecAspectEClass, FEATURE_CODEC_ASPECT__DATE_FORMAT);

		referenceCodecAspectEClass = createEClass(REFERENCE_CODEC_ASPECT);
		createEReference(referenceCodecAspectEClass, REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG);
		createEReference(referenceCodecAspectEClass, REFERENCE_CODEC_ASPECT__TYPE_CONFIG);
		createEAttribute(referenceCodecAspectEClass, REFERENCE_CODEC_ASPECT__INHERIT_TYPE_FROM_TARGET);
		createEAttribute(referenceCodecAspectEClass, REFERENCE_CODEC_ASPECT__EXPAND);

		codecPackageProfileEClass = createEClass(CODEC_PACKAGE_PROFILE);
		createEReference(codecPackageProfileEClass, CODEC_PACKAGE_PROFILE__CLASS_PROFILES);

		codecClassProfileEClass = createEClass(CODEC_CLASS_PROFILE);
		createEReference(codecClassProfileEClass, CODEC_CLASS_PROFILE__ECLASS);
		createEReference(codecClassProfileEClass, CODEC_CLASS_PROFILE__TYPE_CONFIG);
		createEReference(codecClassProfileEClass, CODEC_CLASS_PROFILE__ID_CONFIG);
		createEReference(codecClassProfileEClass, CODEC_CLASS_PROFILE__SUPER_TYPE_CONFIG);
		createEReference(codecClassProfileEClass, CODEC_CLASS_PROFILE__FEATURE_CONFIGS);

		codecConfigEClass = createEClass(CODEC_CONFIG);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__FORMAT);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__USE_NUMERIC_IDS);
		createEReference(codecConfigEClass, CODEC_CONFIG__TYPE_CONFIG);
		createEReference(codecConfigEClass, CODEC_CONFIG__CONTAINMENT_TYPE_CONFIG);
		createEReference(codecConfigEClass, CODEC_CONFIG__REFERENCE_TYPE_CONFIG);
		createEReference(codecConfigEClass, CODEC_CONFIG__ID_CONFIG);
		createEReference(codecConfigEClass, CODEC_CONFIG__REFERENCE_CONFIG);
		createEReference(codecConfigEClass, CODEC_CONFIG__SUPER_TYPE_CONFIG);
		createEReference(codecConfigEClass, CODEC_CONFIG__FEATURE_CONFIGS);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__EXPAND);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__EXPAND_DEPTH);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__EXPAND_IGNORE_BIDIRECTIONAL);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__SERIALIZE_NULL);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__SERIALIZE_EMPTY);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__SERIALIZE_DEFAULTS);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__TYPE_HINT_MODE);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__DESERIALIZATION_MODE);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__STRICT_ON_UNKNOWN);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__STRICT_ON_MISSING);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__METADATA_MERGE);
		createEAttribute(codecConfigEClass, CODEC_CONFIG__METADATA_KEY);

		// Create enums
		strategyScopeEEnum = createEEnum(STRATEGY_SCOPE);
		typeHintModeEEnum = createEEnum(TYPE_HINT_MODE);
		deserializationModeEEnum = createEEnum(DESERIALIZATION_MODE);
		fallbackStrategyEEnum = createEEnum(FALLBACK_STRATEGY);
		fingerprintModeEEnum = createEEnum(FINGERPRINT_MODE);
		serializationFormatEEnum = createEEnum(SERIALIZATION_FORMAT);
		typeStrategyEEnum = createEEnum(TYPE_STRATEGY);
		idStrategyEEnum = createEEnum(ID_STRATEGY);
		idKeyModeEEnum = createEEnum(ID_KEY_MODE);
		superTypeSelectionEEnum = createEEnum(SUPER_TYPE_SELECTION);
		enumSerializationStrategyEEnum = createEEnum(ENUM_SERIALIZATION_STRATEGY);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		typeSerializationConfigEClass.getESuperTypes().add(this.getBaseTypeConfig());
		idSerializationConfigEClass.getESuperTypes().add(this.getBaseIdConfig());
		referenceSerializationConfigEClass.getESuperTypes().add(this.getBaseReferenceConfig());
		superTypeSerializationConfigEClass.getESuperTypes().add(this.getBaseSuperTypeConfig());
		featureSerializationConfigEClass.getESuperTypes().add(this.getBaseFeatureConfig());
		referenceCodecAspectEClass.getESuperTypes().add(this.getFeatureCodecAspect());

		// Initialize classes, features, and operations; add parameters
		initEClass(baseTypeConfigEClass, BaseTypeConfig.class, "BaseTypeConfig", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBaseTypeConfig_Format(), this.getSerializationFormat(), "format", "PLAIN", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseTypeConfig_Strategy(), this.getTypeStrategy(), "strategy", "URI", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseTypeConfig_TypeKey(), ecorePackage.getEString(), "typeKey", "_type", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseTypeConfig_SchemaKey(), ecorePackage.getEString(), "schemaKey", "schema", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseTypeConfig_NameKey(), ecorePackage.getEString(), "nameKey", "name", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(baseIdConfigEClass, BaseIdConfig.class, "BaseIdConfig", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBaseIdConfig_Strategy(), this.getIdStrategy(), "strategy", "ID_FIELD", 0, 1, BaseIdConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseIdConfig_KeyMode(), this.getIdKeyMode(), "keyMode", "ID_ONLY", 0, 1, BaseIdConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseIdConfig_Format(), this.getSerializationFormat(), "format", "PLAIN", 0, 1, BaseIdConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseIdConfig_IdKey(), ecorePackage.getEString(), "idKey", "_id", 0, 1, BaseIdConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseIdConfig_Separator(), ecorePackage.getEString(), "separator", "-", 0, 1, BaseIdConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseIdConfig_OnTop(), ecorePackage.getEBoolean(), "onTop", "true", 0, 1, BaseIdConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseIdConfig_SerializeSeparator(), ecorePackage.getEBoolean(), "serializeSeparator", "true", 0, 1, BaseIdConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseIdConfig_SeparatorKey(), ecorePackage.getEString(), "separatorKey", "separator", 0, 1, BaseIdConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseIdConfig_ValueKey(), ecorePackage.getEString(), "valueKey", "id", 0, 1, BaseIdConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(baseReferenceConfigEClass, BaseReferenceConfig.class, "BaseReferenceConfig", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBaseReferenceConfig_Format(), this.getSerializationFormat(), "format", "PLAIN", 0, 1, BaseReferenceConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseReferenceConfig_TypeKey(), ecorePackage.getEString(), "typeKey", "_type", 0, 1, BaseReferenceConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseReferenceConfig_RefKey(), ecorePackage.getEString(), "refKey", "_ref", 0, 1, BaseReferenceConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(baseSuperTypeConfigEClass, BaseSuperTypeConfig.class, "BaseSuperTypeConfig", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBaseSuperTypeConfig_Enabled(), ecorePackage.getEBoolean(), "enabled", "false", 0, 1, BaseSuperTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseSuperTypeConfig_Selection(), this.getSuperTypeSelection(), "selection", "ALL", 0, 1, BaseSuperTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseSuperTypeConfig_Format(), this.getSerializationFormat(), "format", "PLAIN", 0, 1, BaseSuperTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseSuperTypeConfig_AsArray(), ecorePackage.getEBoolean(), "asArray", "true", 0, 1, BaseSuperTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseSuperTypeConfig_Separator(), ecorePackage.getEString(), "separator", ",", 0, 1, BaseSuperTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseSuperTypeConfig_SuperTypeKey(), ecorePackage.getEString(), "superTypeKey", "_supertype", 0, 1, BaseSuperTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(baseFeatureConfigEClass, BaseFeatureConfig.class, "BaseFeatureConfig", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBaseFeatureConfig_Key(), ecorePackage.getEString(), "key", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseFeatureConfig_Ignore(), ecorePackage.getEBooleanObject(), "ignore", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseFeatureConfig_IgnoreRead(), ecorePackage.getEBooleanObject(), "ignoreRead", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseFeatureConfig_IgnoreWrite(), ecorePackage.getEBooleanObject(), "ignoreWrite", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseFeatureConfig_ForceRead(), ecorePackage.getEBooleanObject(), "forceRead", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseFeatureConfig_ForceWrite(), ecorePackage.getEBooleanObject(), "forceWrite", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseFeatureConfig_SerializeNull(), ecorePackage.getEBooleanObject(), "serializeNull", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseFeatureConfig_SerializeEmpty(), ecorePackage.getEBooleanObject(), "serializeEmpty", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseFeatureConfig_SerializeDefaults(), ecorePackage.getEBooleanObject(), "serializeDefaults", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseFeatureConfig_EnumSerialization(), this.getEnumSerializationStrategy(), "enumSerialization", null, 0, 1, BaseFeatureConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(typeSerializationConfigEClass, TypeSerializationConfig.class, "TypeSerializationConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTypeSerializationConfig_MapId(), ecorePackage.getEString(), "mapId", null, 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTypeSerializationConfig_DiscriminatorPath(), ecorePackage.getEString(), "discriminatorPath", null, 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTypeSerializationConfig_DiscriminatorValue(), ecorePackage.getEString(), "discriminatorValue", null, 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTypeSerializationConfig_FingerprintMode(), this.getFingerprintMode(), "fingerprintMode", "NONE", 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTypeSerializationConfig_FingerprintKey(), ecorePackage.getEString(), "fingerprintKey", null, 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTypeSerializationConfig_StrategyScope(), this.getStrategyScope(), "strategyScope", "ALL", 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTypeSerializationConfig_FormatScope(), this.getStrategyScope(), "formatScope", "ALL", 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(idSerializationConfigEClass, IdSerializationConfig.class, "IdSerializationConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIdSerializationConfig_IdFeatures(), ecorePackage.getEString(), "idFeatures", null, 0, -1, IdSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIdSerializationConfig_IdValueWriterName(), ecorePackage.getEString(), "idValueWriterName", null, 0, 1, IdSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIdSerializationConfig_IdValueReaderName(), ecorePackage.getEString(), "idValueReaderName", null, 0, 1, IdSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIdSerializationConfig_StrategyScope(), this.getStrategyScope(), "strategyScope", "ALL", 0, 1, IdSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIdSerializationConfig_FormatScope(), this.getStrategyScope(), "formatScope", "ALL", 0, 1, IdSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(referenceSerializationConfigEClass, ReferenceSerializationConfig.class, "ReferenceSerializationConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getReferenceSerializationConfig_IncludeType(), ecorePackage.getEBoolean(), "includeType", "true", 0, 1, ReferenceSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReferenceSerializationConfig_Expand(), ecorePackage.getEBoolean(), "expand", "false", 0, 1, ReferenceSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(superTypeSerializationConfigEClass, SuperTypeSerializationConfig.class, "SuperTypeSerializationConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSuperTypeSerializationConfig_UseSmartCompression(), ecorePackage.getEBoolean(), "useSmartCompression", "false", 0, 1, SuperTypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(featureSerializationConfigEClass, FeatureSerializationConfig.class, "FeatureSerializationConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFeatureSerializationConfig_FeatureName(), ecorePackage.getEString(), "featureName", null, 0, 1, FeatureSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureSerializationConfig_ValueWriterName(), ecorePackage.getEString(), "valueWriterName", null, 0, 1, FeatureSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureSerializationConfig_ValueReaderName(), ecorePackage.getEString(), "valueReaderName", null, 0, 1, FeatureSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureSerializationConfig_Expand(), ecorePackage.getEBooleanObject(), "expand", null, 0, 1, FeatureSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getFeatureSerializationConfig_ReferenceConfig(), this.getReferenceSerializationConfig(), null, "referenceConfig", null, 0, 1, FeatureSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getFeatureSerializationConfig_TypeConfig(), this.getTypeSerializationConfig(), null, "typeConfig", null, 0, 1, FeatureSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(classCodecAspectEClass, ClassCodecAspect.class, "ClassCodecAspect", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getClassCodecAspect_TypeConfig(), this.getTypeSerializationConfig(), null, "typeConfig", null, 0, 1, ClassCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClassCodecAspect_IdConfig(), this.getIdSerializationConfig(), null, "idConfig", null, 0, 1, ClassCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClassCodecAspect_SuperTypeConfig(), this.getSuperTypeSerializationConfig(), null, "superTypeConfig", null, 0, 1, ClassCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassCodecAspect_InheritFromParent(), ecorePackage.getEBoolean(), "inheritFromParent", "true", 0, 1, ClassCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassCodecAspect_DiscriminatorValue(), ecorePackage.getEString(), "discriminatorValue", null, 0, 1, ClassCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassCodecAspect_StrictOnUnknown(), ecorePackage.getEBoolean(), "strictOnUnknown", "false", 0, 1, ClassCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassCodecAspect_StrictOnMissing(), ecorePackage.getEBoolean(), "strictOnMissing", "false", 0, 1, ClassCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassCodecAspect_MetadataMerge(), ecorePackage.getEBoolean(), "metadataMerge", "false", 0, 1, ClassCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassCodecAspect_MetadataKey(), ecorePackage.getEString(), "metadataKey", "_metadata", 0, 1, ClassCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(featureCodecAspectEClass, FeatureCodecAspect.class, "FeatureCodecAspect", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFeatureCodecAspect_EffectiveKey(), ecorePackage.getEString(), "effectiveKey", null, 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_Ignore(), ecorePackage.getEBoolean(), "ignore", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_IgnoreRead(), ecorePackage.getEBoolean(), "ignoreRead", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_IgnoreWrite(), ecorePackage.getEBoolean(), "ignoreWrite", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_ForceRead(), ecorePackage.getEBoolean(), "forceRead", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_ForceWrite(), ecorePackage.getEBoolean(), "forceWrite", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_SerializeNull(), ecorePackage.getEBoolean(), "serializeNull", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_SerializeEmpty(), ecorePackage.getEBoolean(), "serializeEmpty", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_SerializeDefaults(), ecorePackage.getEBoolean(), "serializeDefaults", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_ValueWriterName(), ecorePackage.getEString(), "valueWriterName", null, 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_ValueReaderName(), ecorePackage.getEString(), "valueReaderName", null, 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_EnumSerialization(), this.getEnumSerializationStrategy(), "enumSerialization", null, 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_DateFormat(), ecorePackage.getEString(), "dateFormat", null, 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(referenceCodecAspectEClass, ReferenceCodecAspect.class, "ReferenceCodecAspect", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getReferenceCodecAspect_ReferenceConfig(), this.getReferenceSerializationConfig(), null, "referenceConfig", null, 0, 1, ReferenceCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReferenceCodecAspect_TypeConfig(), this.getTypeSerializationConfig(), null, "typeConfig", null, 0, 1, ReferenceCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReferenceCodecAspect_InheritTypeFromTarget(), ecorePackage.getEBoolean(), "inheritTypeFromTarget", "true", 0, 1, ReferenceCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReferenceCodecAspect_Expand(), ecorePackage.getEBoolean(), "expand", "false", 0, 1, ReferenceCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(codecPackageProfileEClass, CodecPackageProfile.class, "CodecPackageProfile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCodecPackageProfile_ClassProfiles(), this.getCodecClassProfile(), null, "classProfiles", null, 0, -1, CodecPackageProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(codecClassProfileEClass, CodecClassProfile.class, "CodecClassProfile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCodecClassProfile_EClass(), ecorePackage.getEClass(), null, "eClass", null, 0, 1, CodecClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecClassProfile_TypeConfig(), this.getTypeSerializationConfig(), null, "typeConfig", null, 0, 1, CodecClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecClassProfile_IdConfig(), this.getIdSerializationConfig(), null, "idConfig", null, 0, 1, CodecClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecClassProfile_SuperTypeConfig(), this.getSuperTypeSerializationConfig(), null, "superTypeConfig", null, 0, 1, CodecClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecClassProfile_FeatureConfigs(), this.getFeatureSerializationConfig(), null, "featureConfigs", null, 0, -1, CodecClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(codecConfigEClass, CodecConfig.class, "CodecConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCodecConfig_Format(), this.getSerializationFormat(), "format", "PLAIN", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_UseNumericIds(), ecorePackage.getEBoolean(), "useNumericIds", "false", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecConfig_TypeConfig(), this.getTypeSerializationConfig(), null, "typeConfig", null, 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecConfig_ContainmentTypeConfig(), this.getTypeSerializationConfig(), null, "containmentTypeConfig", null, 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecConfig_ReferenceTypeConfig(), this.getTypeSerializationConfig(), null, "referenceTypeConfig", null, 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecConfig_IdConfig(), this.getIdSerializationConfig(), null, "idConfig", null, 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecConfig_ReferenceConfig(), this.getReferenceSerializationConfig(), null, "referenceConfig", null, 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecConfig_SuperTypeConfig(), this.getSuperTypeSerializationConfig(), null, "superTypeConfig", null, 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecConfig_FeatureConfigs(), this.getFeatureSerializationConfig(), null, "featureConfigs", null, 0, -1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_Expand(), ecorePackage.getEBoolean(), "expand", "false", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_ExpandDepth(), ecorePackage.getEInt(), "expandDepth", "1", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_ExpandIgnoreBidirectional(), ecorePackage.getEBoolean(), "expandIgnoreBidirectional", "true", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_SerializeNull(), ecorePackage.getEBoolean(), "serializeNull", "false", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_SerializeEmpty(), ecorePackage.getEBoolean(), "serializeEmpty", "false", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_SerializeDefaults(), ecorePackage.getEBoolean(), "serializeDefaults", "false", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_TypeHintMode(), this.getTypeHintMode(), "typeHintMode", "HINT", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_DeserializationMode(), this.getDeserializationMode(), "deserializationMode", "LENIENT", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_StrictOnUnknown(), ecorePackage.getEBoolean(), "strictOnUnknown", "false", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_StrictOnMissing(), ecorePackage.getEBoolean(), "strictOnMissing", "false", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_MetadataMerge(), ecorePackage.getEBoolean(), "metadataMerge", "false", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCodecConfig_MetadataKey(), ecorePackage.getEString(), "metadataKey", "_metadata", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(strategyScopeEEnum, StrategyScope.class, "StrategyScope");
		addEEnumLiteral(strategyScopeEEnum, StrategyScope.ALL);
		addEEnumLiteral(strategyScopeEEnum, StrategyScope.ROOT_ONLY);
		addEEnumLiteral(strategyScopeEEnum, StrategyScope.ROOT_CONTAINMENT);
		addEEnumLiteral(strategyScopeEEnum, StrategyScope.ROOT_NON_CONTAINMENT);

		initEEnum(typeHintModeEEnum, TypeHintMode.class, "TypeHintMode");
		addEEnumLiteral(typeHintModeEEnum, TypeHintMode.HINT);
		addEEnumLiteral(typeHintModeEEnum, TypeHintMode.OVERRIDE);

		initEEnum(deserializationModeEEnum, DeserializationMode.class, "DeserializationMode");
		addEEnumLiteral(deserializationModeEEnum, DeserializationMode.LENIENT);
		addEEnumLiteral(deserializationModeEEnum, DeserializationMode.STRICT);
		addEEnumLiteral(deserializationModeEEnum, DeserializationMode.AUTO_DETECT);

		initEEnum(fallbackStrategyEEnum, FallbackStrategy.class, "FallbackStrategy");
		addEEnumLiteral(fallbackStrategyEEnum, FallbackStrategy.SKIP);
		addEEnumLiteral(fallbackStrategyEEnum, FallbackStrategy.ERROR);
		addEEnumLiteral(fallbackStrategyEEnum, FallbackStrategy.FALLBACK);

		initEEnum(fingerprintModeEEnum, FingerprintMode.class, "FingerprintMode");
		addEEnumLiteral(fingerprintModeEEnum, FingerprintMode.NONE);
		addEEnumLiteral(fingerprintModeEEnum, FingerprintMode.FIRST_TOUCH);

		initEEnum(serializationFormatEEnum, SerializationFormat.class, "SerializationFormat");
		addEEnumLiteral(serializationFormatEEnum, SerializationFormat.PLAIN);
		addEEnumLiteral(serializationFormatEEnum, SerializationFormat.STRUCTURED);

		initEEnum(typeStrategyEEnum, TypeStrategy.class, "TypeStrategy");
		addEEnumLiteral(typeStrategyEEnum, TypeStrategy.NAME);
		addEEnumLiteral(typeStrategyEEnum, TypeStrategy.CLASS);
		addEEnumLiteral(typeStrategyEEnum, TypeStrategy.URI);
		addEEnumLiteral(typeStrategyEEnum, TypeStrategy.SCHEMA_AND_TYPE);
		addEEnumLiteral(typeStrategyEEnum, TypeStrategy.NUMERIC);
		addEEnumLiteral(typeStrategyEEnum, TypeStrategy.NONE);

		initEEnum(idStrategyEEnum, IdStrategy.class, "IdStrategy");
		addEEnumLiteral(idStrategyEEnum, IdStrategy.ID_FIELD);
		addEEnumLiteral(idStrategyEEnum, IdStrategy.COMBINED);

		initEEnum(idKeyModeEEnum, IdKeyMode.class, "IdKeyMode");
		addEEnumLiteral(idKeyModeEEnum, IdKeyMode.ID_ONLY);
		addEEnumLiteral(idKeyModeEEnum, IdKeyMode.BOTH);
		addEEnumLiteral(idKeyModeEEnum, IdKeyMode.FEATURE_ONLY);
		addEEnumLiteral(idKeyModeEEnum, IdKeyMode.NONE);

		initEEnum(superTypeSelectionEEnum, SuperTypeSelection.class, "SuperTypeSelection");
		addEEnumLiteral(superTypeSelectionEEnum, SuperTypeSelection.ALL);
		addEEnumLiteral(superTypeSelectionEEnum, SuperTypeSelection.ALL_EMF);
		addEEnumLiteral(superTypeSelectionEEnum, SuperTypeSelection.SINGLE);
		addEEnumLiteral(superTypeSelectionEEnum, SuperTypeSelection.NONE);

		initEEnum(enumSerializationStrategyEEnum, EnumSerializationStrategy.class, "EnumSerializationStrategy");
		addEEnumLiteral(enumSerializationStrategyEEnum, EnumSerializationStrategy.LITERAL);
		addEEnumLiteral(enumSerializationStrategyEEnum, EnumSerializationStrategy.VALUE);
		addEEnumLiteral(enumSerializationStrategyEEnum, EnumSerializationStrategy.NAME);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// Version
		createVersionAnnotations();
	}

	/**
	 * Initializes the annotations for <b>Version</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createVersionAnnotations() {
		String source = "Version";
		addAnnotation
		  (this,
		   source,
		   new String[] {
			   "value", "1.0"
		   });
	}

} //CodecPackageImpl
