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
package org.eclipse.fennec.model.metadata.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.model.metadata.Aspect;
import org.eclipse.fennec.model.metadata.AttributeMetadata;
import org.eclipse.fennec.model.metadata.BaseFeatureConfig;
import org.eclipse.fennec.model.metadata.BaseIdConfig;
import org.eclipse.fennec.model.metadata.BaseReferenceConfig;
import org.eclipse.fennec.model.metadata.BaseSuperTypeConfig;
import org.eclipse.fennec.model.metadata.BaseTypeConfig;
import org.eclipse.fennec.model.metadata.ClassAspect;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.ClassProfile;
import org.eclipse.fennec.model.metadata.DiagnosticContainer;
import org.eclipse.fennec.model.metadata.DiagnosticSeverity;
import org.eclipse.fennec.model.metadata.EnumSerializationStrategy;
import org.eclipse.fennec.model.metadata.FeatureAspect;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.IdKeyMode;
import org.eclipse.fennec.model.metadata.IdStrategy;
import org.eclipse.fennec.model.metadata.MetadataDiagnostic;
import org.eclipse.fennec.model.metadata.MetadataFactory;
import org.eclipse.fennec.model.metadata.MetadataPackage;
import org.eclipse.fennec.model.metadata.MetadataRegistry;
import org.eclipse.fennec.model.metadata.PackageAspect;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.PackageProfile;
import org.eclipse.fennec.model.metadata.ReferenceMetadata;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.SuperTypeSelection;
import org.eclipse.fennec.model.metadata.TypeStrategy;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MetadataPackageImpl extends EPackageImpl implements MetadataPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataDiagnosticEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass diagnosticContainerEClass = null;

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
	private EClass aspectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass packageAspectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass classAspectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass featureAspectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass packageProfileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass classProfileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass packageMetadataEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass classMetadataEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass featureMetadataEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass attributeMetadataEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass referenceMetadataEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataRegistryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum diagnosticSeverityEEnum = null;

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
	 * @see org.eclipse.fennec.model.metadata.MetadataPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private MetadataPackageImpl() {
		super(eNS_URI, MetadataFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link MetadataPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static MetadataPackage init() {
		if (isInited) return (MetadataPackage)EPackage.Registry.INSTANCE.getEPackage(MetadataPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredMetadataPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		MetadataPackageImpl theMetadataPackage = registeredMetadataPackage instanceof MetadataPackageImpl ? (MetadataPackageImpl)registeredMetadataPackage : new MetadataPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theMetadataPackage.createPackageContents();

		// Initialize created meta-data
		theMetadataPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theMetadataPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(MetadataPackage.eNS_URI, theMetadataPackage);
		return theMetadataPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadataDiagnostic() {
		return metadataDiagnosticEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMetadataDiagnostic_Severity() {
		return (EAttribute)metadataDiagnosticEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMetadataDiagnostic_Message() {
		return (EAttribute)metadataDiagnosticEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMetadataDiagnostic_Key() {
		return (EAttribute)metadataDiagnosticEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDiagnosticContainer() {
		return diagnosticContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDiagnosticContainer_Diagnostics() {
		return (EReference)diagnosticContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDiagnosticContainer_AllDiagnostics() {
		return (EReference)diagnosticContainerEClass.getEStructuralFeatures().get(1);
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
	public EClass getAspect() {
		return aspectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAspect_TypeId() {
		return (EAttribute)aspectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAspect_Diagnostics() {
		return (EReference)aspectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPackageAspect() {
		return packageAspectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPackageAspect_PackageMetadata() {
		return (EReference)packageAspectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getClassAspect() {
		return classAspectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassAspect_ClassMetadata() {
		return (EReference)classAspectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFeatureAspect() {
		return featureAspectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFeatureAspect_FeatureMetadata() {
		return (EReference)featureAspectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPackageProfile() {
		return packageProfileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPackageProfile_TypeId() {
		return (EAttribute)packageProfileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPackageProfile_ClassProfiles() {
		return (EReference)packageProfileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getClassProfile() {
		return classProfileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassProfile_EClass() {
		return (EReference)classProfileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPackageMetadata() {
		return packageMetadataEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPackageMetadata_EPackage() {
		return (EReference)packageMetadataEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPackageMetadata_NsURI() {
		return (EAttribute)packageMetadataEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPackageMetadata_Classes() {
		return (EReference)packageMetadataEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPackageMetadata_Aspects() {
		return (EReference)packageMetadataEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPackageMetadata_Profiles() {
		return (EReference)packageMetadataEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getClassMetadata() {
		return classMetadataEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassMetadata_Package() {
		return (EReference)classMetadataEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassMetadata_EClass() {
		return (EReference)classMetadataEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassMetadata_Name() {
		return (EAttribute)classMetadataEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassMetadata_ClassifierID() {
		return (EAttribute)classMetadataEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassMetadata_TypeURI() {
		return (EAttribute)classMetadataEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassMetadata_Features() {
		return (EReference)classMetadataEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassMetadata_SuperTypes() {
		return (EReference)classMetadataEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassMetadata_AllSuperTypes() {
		return (EReference)classMetadataEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassMetadata_IdFeatures() {
		return (EReference)classMetadataEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getClassMetadata_HasId() {
		return (EAttribute)classMetadataEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getClassMetadata_Aspects() {
		return (EReference)classMetadataEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFeatureMetadata() {
		return featureMetadataEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFeatureMetadata_ClassMetadata() {
		return (EReference)featureMetadataEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFeatureMetadata_EFeature() {
		return (EReference)featureMetadataEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureMetadata_Name() {
		return (EAttribute)featureMetadataEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureMetadata_ExtendedMetaDataName() {
		return (EAttribute)featureMetadataEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureMetadata_FeatureID() {
		return (EAttribute)featureMetadataEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFeatureMetadata_Aspects() {
		return (EReference)featureMetadataEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAttributeMetadata() {
		return attributeMetadataEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttributeMetadata_EAttribute() {
		return (EReference)attributeMetadataEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAttributeMetadata_IsId() {
		return (EAttribute)attributeMetadataEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAttributeMetadata_DefaultValue() {
		return (EAttribute)attributeMetadataEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getReferenceMetadata() {
		return referenceMetadataEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReferenceMetadata_EReference() {
		return (EReference)referenceMetadataEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReferenceMetadata_Containment() {
		return (EAttribute)referenceMetadataEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReferenceMetadata_TargetClassMetadata() {
		return (EReference)referenceMetadataEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReferenceMetadata_OppositeMetadata() {
		return (EReference)referenceMetadataEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReferenceMetadata_HasBidirectional() {
		return (EAttribute)referenceMetadataEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadataRegistry() {
		return metadataRegistryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMetadataRegistry_Packages() {
		return (EReference)metadataRegistryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getDiagnosticSeverity() {
		return diagnosticSeverityEEnum;
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
	public MetadataFactory getMetadataFactory() {
		return (MetadataFactory)getEFactoryInstance();
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
		metadataDiagnosticEClass = createEClass(METADATA_DIAGNOSTIC);
		createEAttribute(metadataDiagnosticEClass, METADATA_DIAGNOSTIC__SEVERITY);
		createEAttribute(metadataDiagnosticEClass, METADATA_DIAGNOSTIC__MESSAGE);
		createEAttribute(metadataDiagnosticEClass, METADATA_DIAGNOSTIC__KEY);

		diagnosticContainerEClass = createEClass(DIAGNOSTIC_CONTAINER);
		createEReference(diagnosticContainerEClass, DIAGNOSTIC_CONTAINER__DIAGNOSTICS);
		createEReference(diagnosticContainerEClass, DIAGNOSTIC_CONTAINER__ALL_DIAGNOSTICS);

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

		aspectEClass = createEClass(ASPECT);
		createEAttribute(aspectEClass, ASPECT__TYPE_ID);
		createEReference(aspectEClass, ASPECT__DIAGNOSTICS);

		packageAspectEClass = createEClass(PACKAGE_ASPECT);
		createEReference(packageAspectEClass, PACKAGE_ASPECT__PACKAGE_METADATA);

		classAspectEClass = createEClass(CLASS_ASPECT);
		createEReference(classAspectEClass, CLASS_ASPECT__CLASS_METADATA);

		featureAspectEClass = createEClass(FEATURE_ASPECT);
		createEReference(featureAspectEClass, FEATURE_ASPECT__FEATURE_METADATA);

		packageProfileEClass = createEClass(PACKAGE_PROFILE);
		createEAttribute(packageProfileEClass, PACKAGE_PROFILE__TYPE_ID);
		createEReference(packageProfileEClass, PACKAGE_PROFILE__CLASS_PROFILES);

		classProfileEClass = createEClass(CLASS_PROFILE);
		createEReference(classProfileEClass, CLASS_PROFILE__ECLASS);

		packageMetadataEClass = createEClass(PACKAGE_METADATA);
		createEReference(packageMetadataEClass, PACKAGE_METADATA__EPACKAGE);
		createEAttribute(packageMetadataEClass, PACKAGE_METADATA__NS_URI);
		createEReference(packageMetadataEClass, PACKAGE_METADATA__CLASSES);
		createEReference(packageMetadataEClass, PACKAGE_METADATA__ASPECTS);
		createEReference(packageMetadataEClass, PACKAGE_METADATA__PROFILES);

		classMetadataEClass = createEClass(CLASS_METADATA);
		createEReference(classMetadataEClass, CLASS_METADATA__PACKAGE);
		createEReference(classMetadataEClass, CLASS_METADATA__ECLASS);
		createEAttribute(classMetadataEClass, CLASS_METADATA__NAME);
		createEAttribute(classMetadataEClass, CLASS_METADATA__CLASSIFIER_ID);
		createEAttribute(classMetadataEClass, CLASS_METADATA__TYPE_URI);
		createEReference(classMetadataEClass, CLASS_METADATA__FEATURES);
		createEReference(classMetadataEClass, CLASS_METADATA__SUPER_TYPES);
		createEReference(classMetadataEClass, CLASS_METADATA__ALL_SUPER_TYPES);
		createEReference(classMetadataEClass, CLASS_METADATA__ID_FEATURES);
		createEAttribute(classMetadataEClass, CLASS_METADATA__HAS_ID);
		createEReference(classMetadataEClass, CLASS_METADATA__ASPECTS);

		featureMetadataEClass = createEClass(FEATURE_METADATA);
		createEReference(featureMetadataEClass, FEATURE_METADATA__CLASS_METADATA);
		createEReference(featureMetadataEClass, FEATURE_METADATA__EFEATURE);
		createEAttribute(featureMetadataEClass, FEATURE_METADATA__NAME);
		createEAttribute(featureMetadataEClass, FEATURE_METADATA__EXTENDED_META_DATA_NAME);
		createEAttribute(featureMetadataEClass, FEATURE_METADATA__FEATURE_ID);
		createEReference(featureMetadataEClass, FEATURE_METADATA__ASPECTS);

		attributeMetadataEClass = createEClass(ATTRIBUTE_METADATA);
		createEReference(attributeMetadataEClass, ATTRIBUTE_METADATA__EATTRIBUTE);
		createEAttribute(attributeMetadataEClass, ATTRIBUTE_METADATA__IS_ID);
		createEAttribute(attributeMetadataEClass, ATTRIBUTE_METADATA__DEFAULT_VALUE);

		referenceMetadataEClass = createEClass(REFERENCE_METADATA);
		createEReference(referenceMetadataEClass, REFERENCE_METADATA__EREFERENCE);
		createEAttribute(referenceMetadataEClass, REFERENCE_METADATA__CONTAINMENT);
		createEReference(referenceMetadataEClass, REFERENCE_METADATA__TARGET_CLASS_METADATA);
		createEReference(referenceMetadataEClass, REFERENCE_METADATA__OPPOSITE_METADATA);
		createEAttribute(referenceMetadataEClass, REFERENCE_METADATA__HAS_BIDIRECTIONAL);

		metadataRegistryEClass = createEClass(METADATA_REGISTRY);
		createEReference(metadataRegistryEClass, METADATA_REGISTRY__PACKAGES);

		// Create enums
		diagnosticSeverityEEnum = createEEnum(DIAGNOSTIC_SEVERITY);
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
		packageAspectEClass.getESuperTypes().add(this.getAspect());
		classAspectEClass.getESuperTypes().add(this.getAspect());
		featureAspectEClass.getESuperTypes().add(this.getAspect());
		packageMetadataEClass.getESuperTypes().add(this.getDiagnosticContainer());
		classMetadataEClass.getESuperTypes().add(this.getDiagnosticContainer());
		featureMetadataEClass.getESuperTypes().add(this.getDiagnosticContainer());
		attributeMetadataEClass.getESuperTypes().add(this.getFeatureMetadata());
		referenceMetadataEClass.getESuperTypes().add(this.getFeatureMetadata());

		// Initialize classes, features, and operations; add parameters
		initEClass(metadataDiagnosticEClass, MetadataDiagnostic.class, "MetadataDiagnostic", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMetadataDiagnostic_Severity(), this.getDiagnosticSeverity(), "severity", "WARNING", 0, 1, MetadataDiagnostic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMetadataDiagnostic_Message(), ecorePackage.getEString(), "message", null, 0, 1, MetadataDiagnostic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMetadataDiagnostic_Key(), ecorePackage.getEString(), "key", null, 0, 1, MetadataDiagnostic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(diagnosticContainerEClass, DiagnosticContainer.class, "DiagnosticContainer", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDiagnosticContainer_Diagnostics(), this.getMetadataDiagnostic(), null, "diagnostics", null, 0, -1, DiagnosticContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDiagnosticContainer_AllDiagnostics(), this.getMetadataDiagnostic(), null, "allDiagnostics", null, 0, -1, DiagnosticContainer.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(baseTypeConfigEClass, BaseTypeConfig.class, "BaseTypeConfig", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBaseTypeConfig_Format(), this.getSerializationFormat(), "format", "PLAIN", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseTypeConfig_Strategy(), this.getTypeStrategy(), "strategy", "URI", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseTypeConfig_TypeKey(), ecorePackage.getEString(), "typeKey", "_type", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseTypeConfig_SchemaKey(), ecorePackage.getEString(), "schemaKey", "schema", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseTypeConfig_NameKey(), ecorePackage.getEString(), "nameKey", "name", 0, 1, BaseTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

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
		initEAttribute(getBaseReferenceConfig_Format(), this.getSerializationFormat(), "format", "PLAIN", 0, 1, BaseReferenceConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseReferenceConfig_TypeKey(), ecorePackage.getEString(), "typeKey", "_type", 0, 1, BaseReferenceConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseReferenceConfig_RefKey(), ecorePackage.getEString(), "refKey", "_ref", 0, 1, BaseReferenceConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(baseSuperTypeConfigEClass, BaseSuperTypeConfig.class, "BaseSuperTypeConfig", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBaseSuperTypeConfig_Enabled(), ecorePackage.getEBoolean(), "enabled", "false", 0, 1, BaseSuperTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseSuperTypeConfig_Selection(), this.getSuperTypeSelection(), "selection", "ALL", 0, 1, BaseSuperTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBaseSuperTypeConfig_Format(), this.getSerializationFormat(), "format", "PLAIN", 0, 1, BaseSuperTypeConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
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

		initEClass(aspectEClass, Aspect.class, "Aspect", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAspect_TypeId(), ecorePackage.getEString(), "typeId", null, 0, 1, Aspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAspect_Diagnostics(), this.getMetadataDiagnostic(), null, "diagnostics", null, 0, -1, Aspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(packageAspectEClass, PackageAspect.class, "PackageAspect", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPackageAspect_PackageMetadata(), this.getPackageMetadata(), this.getPackageMetadata_Aspects(), "packageMetadata", null, 0, 1, PackageAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(classAspectEClass, ClassAspect.class, "ClassAspect", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getClassAspect_ClassMetadata(), this.getClassMetadata(), this.getClassMetadata_Aspects(), "classMetadata", null, 0, 1, ClassAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(featureAspectEClass, FeatureAspect.class, "FeatureAspect", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getFeatureAspect_FeatureMetadata(), this.getFeatureMetadata(), this.getFeatureMetadata_Aspects(), "featureMetadata", null, 0, 1, FeatureAspect.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(packageProfileEClass, PackageProfile.class, "PackageProfile", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPackageProfile_TypeId(), ecorePackage.getEString(), "typeId", null, 0, 1, PackageProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPackageProfile_ClassProfiles(), this.getClassProfile(), null, "classProfiles", null, 0, -1, PackageProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(classProfileEClass, ClassProfile.class, "ClassProfile", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getClassProfile_EClass(), ecorePackage.getEClass(), null, "eClass", null, 0, 1, ClassProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(packageMetadataEClass, PackageMetadata.class, "PackageMetadata", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPackageMetadata_EPackage(), ecorePackage.getEPackage(), null, "ePackage", null, 0, 1, PackageMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPackageMetadata_NsURI(), ecorePackage.getEString(), "nsURI", null, 0, 1, PackageMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPackageMetadata_Classes(), this.getClassMetadata(), this.getClassMetadata_Package(), "classes", null, 0, -1, PackageMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPackageMetadata_Aspects(), this.getPackageAspect(), this.getPackageAspect_PackageMetadata(), "aspects", null, 0, -1, PackageMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPackageMetadata_Profiles(), this.getPackageProfile(), null, "profiles", null, 0, -1, PackageMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(classMetadataEClass, ClassMetadata.class, "ClassMetadata", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getClassMetadata_Package(), this.getPackageMetadata(), this.getPackageMetadata_Classes(), "package", null, 0, 1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClassMetadata_EClass(), ecorePackage.getEClass(), null, "eClass", null, 0, 1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassMetadata_Name(), ecorePackage.getEString(), "name", null, 0, 1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassMetadata_ClassifierID(), ecorePackage.getEInt(), "classifierID", "-1", 0, 1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassMetadata_TypeURI(), ecorePackage.getEString(), "typeURI", null, 0, 1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClassMetadata_Features(), this.getFeatureMetadata(), this.getFeatureMetadata_ClassMetadata(), "features", null, 0, -1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClassMetadata_SuperTypes(), this.getClassMetadata(), null, "superTypes", null, 0, -1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClassMetadata_AllSuperTypes(), this.getClassMetadata(), null, "allSuperTypes", null, 0, -1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClassMetadata_IdFeatures(), this.getFeatureMetadata(), null, "idFeatures", null, 0, -1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassMetadata_HasId(), ecorePackage.getEBoolean(), "hasId", "false", 0, 1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClassMetadata_Aspects(), this.getClassAspect(), this.getClassAspect_ClassMetadata(), "aspects", null, 0, -1, ClassMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(featureMetadataEClass, FeatureMetadata.class, "FeatureMetadata", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getFeatureMetadata_ClassMetadata(), this.getClassMetadata(), this.getClassMetadata_Features(), "classMetadata", null, 0, 1, FeatureMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getFeatureMetadata_EFeature(), ecorePackage.getEStructuralFeature(), null, "eFeature", null, 0, 1, FeatureMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureMetadata_Name(), ecorePackage.getEString(), "name", null, 0, 1, FeatureMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureMetadata_ExtendedMetaDataName(), ecorePackage.getEString(), "extendedMetaDataName", null, 0, 1, FeatureMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureMetadata_FeatureID(), ecorePackage.getEInt(), "featureID", "-1", 0, 1, FeatureMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getFeatureMetadata_Aspects(), this.getFeatureAspect(), this.getFeatureAspect_FeatureMetadata(), "aspects", null, 0, -1, FeatureMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(attributeMetadataEClass, AttributeMetadata.class, "AttributeMetadata", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAttributeMetadata_EAttribute(), ecorePackage.getEAttribute(), null, "eAttribute", null, 0, 1, AttributeMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAttributeMetadata_IsId(), ecorePackage.getEBoolean(), "isId", "false", 0, 1, AttributeMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAttributeMetadata_DefaultValue(), ecorePackage.getEJavaObject(), "defaultValue", null, 0, 1, AttributeMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(referenceMetadataEClass, ReferenceMetadata.class, "ReferenceMetadata", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getReferenceMetadata_EReference(), ecorePackage.getEReference(), null, "eReference", null, 0, 1, ReferenceMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReferenceMetadata_Containment(), ecorePackage.getEBoolean(), "containment", "false", 0, 1, ReferenceMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReferenceMetadata_TargetClassMetadata(), this.getClassMetadata(), null, "targetClassMetadata", null, 0, 1, ReferenceMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReferenceMetadata_OppositeMetadata(), this.getReferenceMetadata(), null, "oppositeMetadata", null, 0, 1, ReferenceMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReferenceMetadata_HasBidirectional(), ecorePackage.getEBoolean(), "hasBidirectional", "false", 0, 1, ReferenceMetadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(metadataRegistryEClass, MetadataRegistry.class, "MetadataRegistry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMetadataRegistry_Packages(), this.getPackageMetadata(), null, "packages", null, 0, -1, MetadataRegistry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(diagnosticSeverityEEnum, DiagnosticSeverity.class, "DiagnosticSeverity");
		addEEnumLiteral(diagnosticSeverityEEnum, DiagnosticSeverity.WARNING);
		addEEnumLiteral(diagnosticSeverityEEnum, DiagnosticSeverity.ERROR);

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

} //MetadataPackageImpl
