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

import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile;
import org.eclipse.fennec.codec.metadata.model.codec.CodecConfig;
import org.eclipse.fennec.codec.metadata.model.codec.CodecFactory;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile;
import org.eclipse.fennec.codec.metadata.model.codec.DeserializationMode;
import org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.StrategyScope;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;

import org.eclipse.fennec.model.metadata.MetadataPackage;

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

		// Initialize simple dependencies
		MetadataPackage.eINSTANCE.eClass();

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
	public EAttribute getTypeSerializationConfig_StrategyScope() {
		return (EAttribute)typeSerializationConfigEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTypeSerializationConfig_FormatScope() {
		return (EAttribute)typeSerializationConfigEClass.getEStructuralFeatures().get(4);
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
	public EClass getCodecClassProfile() {
		return codecClassProfileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecClassProfile_TypeConfig() {
		return (EReference)codecClassProfileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecClassProfile_IdConfig() {
		return (EReference)codecClassProfileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecClassProfile_SuperTypeConfig() {
		return (EReference)codecClassProfileEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCodecClassProfile_FeatureConfigs() {
		return (EReference)codecClassProfileEClass.getEStructuralFeatures().get(3);
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
		typeSerializationConfigEClass = createEClass(TYPE_SERIALIZATION_CONFIG);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__MAP_ID);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_PATH);
		createEAttribute(typeSerializationConfigEClass, TYPE_SERIALIZATION_CONFIG__DISCRIMINATOR_VALUE);
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

		referenceCodecAspectEClass = createEClass(REFERENCE_CODEC_ASPECT);
		createEReference(referenceCodecAspectEClass, REFERENCE_CODEC_ASPECT__REFERENCE_CONFIG);
		createEReference(referenceCodecAspectEClass, REFERENCE_CODEC_ASPECT__TYPE_CONFIG);
		createEAttribute(referenceCodecAspectEClass, REFERENCE_CODEC_ASPECT__INHERIT_TYPE_FROM_TARGET);
		createEAttribute(referenceCodecAspectEClass, REFERENCE_CODEC_ASPECT__EXPAND);

		codecPackageProfileEClass = createEClass(CODEC_PACKAGE_PROFILE);

		codecClassProfileEClass = createEClass(CODEC_CLASS_PROFILE);
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

		// Obtain other dependent packages
		MetadataPackage theMetadataPackage = (MetadataPackage)EPackage.Registry.INSTANCE.getEPackage(MetadataPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		typeSerializationConfigEClass.getESuperTypes().add(theMetadataPackage.getBaseTypeConfig());
		idSerializationConfigEClass.getESuperTypes().add(theMetadataPackage.getBaseIdConfig());
		referenceSerializationConfigEClass.getESuperTypes().add(theMetadataPackage.getBaseReferenceConfig());
		superTypeSerializationConfigEClass.getESuperTypes().add(theMetadataPackage.getBaseSuperTypeConfig());
		featureSerializationConfigEClass.getESuperTypes().add(theMetadataPackage.getBaseFeatureConfig());
		classCodecAspectEClass.getESuperTypes().add(theMetadataPackage.getClassAspect());
		featureCodecAspectEClass.getESuperTypes().add(theMetadataPackage.getFeatureAspect());
		referenceCodecAspectEClass.getESuperTypes().add(this.getFeatureCodecAspect());
		codecPackageProfileEClass.getESuperTypes().add(theMetadataPackage.getPackageProfile());
		codecClassProfileEClass.getESuperTypes().add(theMetadataPackage.getClassProfile());

		// Initialize classes, features, and operations; add parameters
		initEClass(typeSerializationConfigEClass, TypeSerializationConfig.class, "TypeSerializationConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTypeSerializationConfig_MapId(), ecorePackage.getEString(), "mapId", null, 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTypeSerializationConfig_DiscriminatorPath(), ecorePackage.getEString(), "discriminatorPath", null, 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTypeSerializationConfig_DiscriminatorValue(), ecorePackage.getEString(), "discriminatorValue", null, 0, 1, TypeSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
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
		initEAttribute(getReferenceSerializationConfig_Expand(), ecorePackage.getEBoolean(), "expand", "false", 0, 1, ReferenceSerializationConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

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
		initEAttribute(getFeatureCodecAspect_Ignore(), ecorePackage.getEBoolean(), "ignore", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_IgnoreRead(), ecorePackage.getEBoolean(), "ignoreRead", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_IgnoreWrite(), ecorePackage.getEBoolean(), "ignoreWrite", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_ForceRead(), ecorePackage.getEBoolean(), "forceRead", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_ForceWrite(), ecorePackage.getEBoolean(), "forceWrite", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_SerializeNull(), ecorePackage.getEBoolean(), "serializeNull", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_SerializeEmpty(), ecorePackage.getEBoolean(), "serializeEmpty", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_SerializeDefaults(), ecorePackage.getEBoolean(), "serializeDefaults", "false", 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_ValueWriterName(), ecorePackage.getEString(), "valueWriterName", null, 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_ValueReaderName(), ecorePackage.getEString(), "valueReaderName", null, 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureCodecAspect_EnumSerialization(), theMetadataPackage.getEnumSerializationStrategy(), "enumSerialization", null, 0, 1, FeatureCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(referenceCodecAspectEClass, ReferenceCodecAspect.class, "ReferenceCodecAspect", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getReferenceCodecAspect_ReferenceConfig(), this.getReferenceSerializationConfig(), null, "referenceConfig", null, 0, 1, ReferenceCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReferenceCodecAspect_TypeConfig(), this.getTypeSerializationConfig(), null, "typeConfig", null, 0, 1, ReferenceCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReferenceCodecAspect_InheritTypeFromTarget(), ecorePackage.getEBoolean(), "inheritTypeFromTarget", "true", 0, 1, ReferenceCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReferenceCodecAspect_Expand(), ecorePackage.getEBoolean(), "expand", "false", 0, 1, ReferenceCodecAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(codecPackageProfileEClass, CodecPackageProfile.class, "CodecPackageProfile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(codecClassProfileEClass, CodecClassProfile.class, "CodecClassProfile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCodecClassProfile_TypeConfig(), this.getTypeSerializationConfig(), null, "typeConfig", null, 0, 1, CodecClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecClassProfile_IdConfig(), this.getIdSerializationConfig(), null, "idConfig", null, 0, 1, CodecClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecClassProfile_SuperTypeConfig(), this.getSuperTypeSerializationConfig(), null, "superTypeConfig", null, 0, 1, CodecClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCodecClassProfile_FeatureConfigs(), this.getFeatureSerializationConfig(), null, "featureConfigs", null, 0, -1, CodecClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(codecConfigEClass, CodecConfig.class, "CodecConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCodecConfig_Format(), theMetadataPackage.getSerializationFormat(), "format", "PLAIN", 0, 1, CodecConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
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
