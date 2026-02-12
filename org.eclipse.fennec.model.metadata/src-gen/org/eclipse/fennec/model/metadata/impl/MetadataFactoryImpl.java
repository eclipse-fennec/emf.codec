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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.model.metadata.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MetadataFactoryImpl extends EFactoryImpl implements MetadataFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static MetadataFactory init() {
		try {
			MetadataFactory theMetadataFactory = (MetadataFactory)EPackage.Registry.INSTANCE.getEFactory(MetadataPackage.eNS_URI);
			if (theMetadataFactory != null) {
				return theMetadataFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new MetadataFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetadataFactoryImpl() {
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
			case MetadataPackage.METADATA_DIAGNOSTIC: return createMetadataDiagnostic();
			case MetadataPackage.PACKAGE_METADATA: return createPackageMetadata();
			case MetadataPackage.CLASS_METADATA: return createClassMetadata();
			case MetadataPackage.ATTRIBUTE_METADATA: return createAttributeMetadata();
			case MetadataPackage.REFERENCE_METADATA: return createReferenceMetadata();
			case MetadataPackage.METADATA_REGISTRY: return createMetadataRegistry();
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
			case MetadataPackage.DIAGNOSTIC_SEVERITY:
				return createDiagnosticSeverityFromString(eDataType, initialValue);
			case MetadataPackage.SERIALIZATION_FORMAT:
				return createSerializationFormatFromString(eDataType, initialValue);
			case MetadataPackage.TYPE_STRATEGY:
				return createTypeStrategyFromString(eDataType, initialValue);
			case MetadataPackage.ID_STRATEGY:
				return createIdStrategyFromString(eDataType, initialValue);
			case MetadataPackage.ID_KEY_MODE:
				return createIdKeyModeFromString(eDataType, initialValue);
			case MetadataPackage.SUPER_TYPE_SELECTION:
				return createSuperTypeSelectionFromString(eDataType, initialValue);
			case MetadataPackage.ENUM_SERIALIZATION_STRATEGY:
				return createEnumSerializationStrategyFromString(eDataType, initialValue);
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
			case MetadataPackage.DIAGNOSTIC_SEVERITY:
				return convertDiagnosticSeverityToString(eDataType, instanceValue);
			case MetadataPackage.SERIALIZATION_FORMAT:
				return convertSerializationFormatToString(eDataType, instanceValue);
			case MetadataPackage.TYPE_STRATEGY:
				return convertTypeStrategyToString(eDataType, instanceValue);
			case MetadataPackage.ID_STRATEGY:
				return convertIdStrategyToString(eDataType, instanceValue);
			case MetadataPackage.ID_KEY_MODE:
				return convertIdKeyModeToString(eDataType, instanceValue);
			case MetadataPackage.SUPER_TYPE_SELECTION:
				return convertSuperTypeSelectionToString(eDataType, instanceValue);
			case MetadataPackage.ENUM_SERIALIZATION_STRATEGY:
				return convertEnumSerializationStrategyToString(eDataType, instanceValue);
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
	public MetadataDiagnostic createMetadataDiagnostic() {
		MetadataDiagnosticImpl metadataDiagnostic = new MetadataDiagnosticImpl();
		return metadataDiagnostic;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PackageMetadata createPackageMetadata() {
		PackageMetadataImpl packageMetadata = new PackageMetadataImpl();
		return packageMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ClassMetadata createClassMetadata() {
		ClassMetadataImpl classMetadata = new ClassMetadataImpl();
		return classMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttributeMetadata createAttributeMetadata() {
		AttributeMetadataImpl attributeMetadata = new AttributeMetadataImpl();
		return attributeMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ReferenceMetadata createReferenceMetadata() {
		ReferenceMetadataImpl referenceMetadata = new ReferenceMetadataImpl();
		return referenceMetadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MetadataRegistry createMetadataRegistry() {
		MetadataRegistryImpl metadataRegistry = new MetadataRegistryImpl();
		return metadataRegistry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DiagnosticSeverity createDiagnosticSeverityFromString(EDataType eDataType, String initialValue) {
		DiagnosticSeverity result = DiagnosticSeverity.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDiagnosticSeverityToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SerializationFormat createSerializationFormatFromString(EDataType eDataType, String initialValue) {
		SerializationFormat result = SerializationFormat.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSerializationFormatToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TypeStrategy createTypeStrategyFromString(EDataType eDataType, String initialValue) {
		TypeStrategy result = TypeStrategy.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTypeStrategyToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IdStrategy createIdStrategyFromString(EDataType eDataType, String initialValue) {
		IdStrategy result = IdStrategy.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIdStrategyToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IdKeyMode createIdKeyModeFromString(EDataType eDataType, String initialValue) {
		IdKeyMode result = IdKeyMode.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIdKeyModeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SuperTypeSelection createSuperTypeSelectionFromString(EDataType eDataType, String initialValue) {
		SuperTypeSelection result = SuperTypeSelection.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSuperTypeSelectionToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnumSerializationStrategy createEnumSerializationStrategyFromString(EDataType eDataType, String initialValue) {
		EnumSerializationStrategy result = EnumSerializationStrategy.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEnumSerializationStrategyToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MetadataPackage getMetadataPackage() {
		return (MetadataPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static MetadataPackage getPackage() {
		return MetadataPackage.eINSTANCE;
	}

} //MetadataFactoryImpl
