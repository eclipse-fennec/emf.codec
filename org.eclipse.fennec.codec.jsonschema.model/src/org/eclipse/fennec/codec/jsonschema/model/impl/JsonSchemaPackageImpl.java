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
package org.eclipse.fennec.codec.jsonschema.model.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.codec.jsonschema.model.Dependency;
import org.eclipse.fennec.codec.jsonschema.model.Discriminator;
import org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaFactory;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;
import org.eclipse.fennec.codec.jsonschema.model.Xml;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class JsonSchemaPackageImpl extends EPackageImpl implements JsonSchemaPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass schemaEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass discriminatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xmlEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass externalDocumentationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dependencyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass schemaEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dependencyEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stringListEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stringEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass vocabularyEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass keywordEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum simpleTypeEEnum = null;

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
	 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private JsonSchemaPackageImpl() {
		super(eNS_URI, JsonSchemaFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link JsonSchemaPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static JsonSchemaPackage init() {
		if (isInited) return (JsonSchemaPackage)EPackage.Registry.INSTANCE.getEPackage(JsonSchemaPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredJsonSchemaPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		JsonSchemaPackageImpl theJsonSchemaPackage = registeredJsonSchemaPackage instanceof JsonSchemaPackageImpl ? (JsonSchemaPackageImpl)registeredJsonSchemaPackage : new JsonSchemaPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theJsonSchemaPackage.createPackageContents();

		// Initialize created meta-data
		theJsonSchemaPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theJsonSchemaPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(JsonSchemaPackage.eNS_URI, theJsonSchemaPackage);
		return theJsonSchemaPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSchema() {
		return schemaEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_BooleanSchema() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Schema() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Id() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_LegacyId() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Ref() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Anchor() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_DynamicRef() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_DynamicAnchor() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_RecursiveRef() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_RecursiveAnchor() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Vocabulary() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Comment() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Defs() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Definitions() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Title() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Description() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Default() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Deprecated() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_ReadOnly() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_WriteOnly() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Examples() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Type() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(21);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_TypeAsList() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(22);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Enum() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(23);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Const() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(24);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_MultipleOf() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(25);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Maximum() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(26);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_ExclusiveMaximum() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(27);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_ExclusiveMaximumFlag() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(28);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Minimum() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(29);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_ExclusiveMinimum() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(30);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_ExclusiveMinimumFlag() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(31);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_MaxLength() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(32);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_MinLength() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(33);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Pattern() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(34);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_MaxItems() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(35);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_MinItems() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(36);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_UniqueItems() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(37);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_MaxContains() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(38);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_MinContains() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(39);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_MaxProperties() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(40);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_MinProperties() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(41);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Required() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(42);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_DependentRequired() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(43);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Format() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(44);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_AllOf() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(45);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_AnyOf() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(46);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_OneOf() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(47);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Not() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(48);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_If() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(49);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Then() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(50);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Else() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(51);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_DependentSchemas() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(52);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Dependencies() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(53);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_PrefixItems() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(54);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Items() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(55);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_ItemsList() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(56);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_AdditionalItems() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(57);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Contains() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(58);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Properties() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(59);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_PatternProperties() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(60);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_AdditionalProperties() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(61);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_PropertyNames() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(62);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_UnevaluatedItems() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(63);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_UnevaluatedProperties() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(64);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_ContentEncoding() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(65);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_ContentMediaType() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(66);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_ContentSchema() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(67);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Nullable() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(68);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Discriminator() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(69);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Xml() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(70);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_ExternalDocs() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(71);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchema_Example() {
		return (EAttribute)schemaEClass.getEStructuralFeatures().get(72);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Keywords() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(73);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchema_Classifier() {
		return (EReference)schemaEClass.getEStructuralFeatures().get(74);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDiscriminator() {
		return discriminatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDiscriminator_PropertyName() {
		return (EAttribute)discriminatorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDiscriminator_Mapping() {
		return (EReference)discriminatorEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDiscriminator_DefaultMapping() {
		return (EAttribute)discriminatorEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDiscriminator_Extensions() {
		return (EReference)discriminatorEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getXml() {
		return xmlEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXml_Name() {
		return (EAttribute)xmlEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXml_Namespace() {
		return (EAttribute)xmlEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXml_Prefix() {
		return (EAttribute)xmlEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXml_Attribute() {
		return (EAttribute)xmlEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXml_Wrapped() {
		return (EAttribute)xmlEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXml_NodeType() {
		return (EAttribute)xmlEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getXml_Extensions() {
		return (EReference)xmlEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExternalDocumentation() {
		return externalDocumentationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExternalDocumentation_Description() {
		return (EAttribute)externalDocumentationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExternalDocumentation_Url() {
		return (EAttribute)externalDocumentationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExternalDocumentation_Extensions() {
		return (EReference)externalDocumentationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDependency() {
		return dependencyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDependency_Schema() {
		return (EReference)dependencyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDependency_Properties() {
		return (EAttribute)dependencyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSchemaEntry() {
		return schemaEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchemaEntry_Key() {
		return (EAttribute)schemaEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchemaEntry_Value() {
		return (EReference)schemaEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDependencyEntry() {
		return dependencyEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDependencyEntry_Key() {
		return (EAttribute)dependencyEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDependencyEntry_Value() {
		return (EReference)dependencyEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStringListEntry() {
		return stringListEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringListEntry_Key() {
		return (EAttribute)stringListEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringListEntry_Value() {
		return (EAttribute)stringListEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStringEntry() {
		return stringEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringEntry_Key() {
		return (EAttribute)stringEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringEntry_Value() {
		return (EAttribute)stringEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getVocabularyEntry() {
		return vocabularyEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getVocabularyEntry_Key() {
		return (EAttribute)vocabularyEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getVocabularyEntry_Value() {
		return (EAttribute)vocabularyEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getKeywordEntry() {
		return keywordEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getKeywordEntry_Key() {
		return (EAttribute)keywordEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getKeywordEntry_Value() {
		return (EAttribute)keywordEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getSimpleType() {
		return simpleTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public JsonSchemaFactory getJsonSchemaFactory() {
		return (JsonSchemaFactory)getEFactoryInstance();
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
		schemaEClass = createEClass(SCHEMA);
		createEAttribute(schemaEClass, SCHEMA__BOOLEAN_SCHEMA);
		createEAttribute(schemaEClass, SCHEMA__SCHEMA);
		createEAttribute(schemaEClass, SCHEMA__ID);
		createEAttribute(schemaEClass, SCHEMA__LEGACY_ID);
		createEAttribute(schemaEClass, SCHEMA__REF);
		createEAttribute(schemaEClass, SCHEMA__ANCHOR);
		createEAttribute(schemaEClass, SCHEMA__DYNAMIC_REF);
		createEAttribute(schemaEClass, SCHEMA__DYNAMIC_ANCHOR);
		createEAttribute(schemaEClass, SCHEMA__RECURSIVE_REF);
		createEAttribute(schemaEClass, SCHEMA__RECURSIVE_ANCHOR);
		createEReference(schemaEClass, SCHEMA__VOCABULARY);
		createEAttribute(schemaEClass, SCHEMA__COMMENT);
		createEReference(schemaEClass, SCHEMA__DEFS);
		createEReference(schemaEClass, SCHEMA__DEFINITIONS);
		createEAttribute(schemaEClass, SCHEMA__TITLE);
		createEAttribute(schemaEClass, SCHEMA__DESCRIPTION);
		createEAttribute(schemaEClass, SCHEMA__DEFAULT);
		createEAttribute(schemaEClass, SCHEMA__DEPRECATED);
		createEAttribute(schemaEClass, SCHEMA__READ_ONLY);
		createEAttribute(schemaEClass, SCHEMA__WRITE_ONLY);
		createEAttribute(schemaEClass, SCHEMA__EXAMPLES);
		createEAttribute(schemaEClass, SCHEMA__TYPE);
		createEAttribute(schemaEClass, SCHEMA__TYPE_AS_LIST);
		createEAttribute(schemaEClass, SCHEMA__ENUM);
		createEAttribute(schemaEClass, SCHEMA__CONST);
		createEAttribute(schemaEClass, SCHEMA__MULTIPLE_OF);
		createEAttribute(schemaEClass, SCHEMA__MAXIMUM);
		createEAttribute(schemaEClass, SCHEMA__EXCLUSIVE_MAXIMUM);
		createEAttribute(schemaEClass, SCHEMA__EXCLUSIVE_MAXIMUM_FLAG);
		createEAttribute(schemaEClass, SCHEMA__MINIMUM);
		createEAttribute(schemaEClass, SCHEMA__EXCLUSIVE_MINIMUM);
		createEAttribute(schemaEClass, SCHEMA__EXCLUSIVE_MINIMUM_FLAG);
		createEAttribute(schemaEClass, SCHEMA__MAX_LENGTH);
		createEAttribute(schemaEClass, SCHEMA__MIN_LENGTH);
		createEAttribute(schemaEClass, SCHEMA__PATTERN);
		createEAttribute(schemaEClass, SCHEMA__MAX_ITEMS);
		createEAttribute(schemaEClass, SCHEMA__MIN_ITEMS);
		createEAttribute(schemaEClass, SCHEMA__UNIQUE_ITEMS);
		createEAttribute(schemaEClass, SCHEMA__MAX_CONTAINS);
		createEAttribute(schemaEClass, SCHEMA__MIN_CONTAINS);
		createEAttribute(schemaEClass, SCHEMA__MAX_PROPERTIES);
		createEAttribute(schemaEClass, SCHEMA__MIN_PROPERTIES);
		createEAttribute(schemaEClass, SCHEMA__REQUIRED);
		createEReference(schemaEClass, SCHEMA__DEPENDENT_REQUIRED);
		createEAttribute(schemaEClass, SCHEMA__FORMAT);
		createEReference(schemaEClass, SCHEMA__ALL_OF);
		createEReference(schemaEClass, SCHEMA__ANY_OF);
		createEReference(schemaEClass, SCHEMA__ONE_OF);
		createEReference(schemaEClass, SCHEMA__NOT);
		createEReference(schemaEClass, SCHEMA__IF);
		createEReference(schemaEClass, SCHEMA__THEN);
		createEReference(schemaEClass, SCHEMA__ELSE);
		createEReference(schemaEClass, SCHEMA__DEPENDENT_SCHEMAS);
		createEReference(schemaEClass, SCHEMA__DEPENDENCIES);
		createEReference(schemaEClass, SCHEMA__PREFIX_ITEMS);
		createEReference(schemaEClass, SCHEMA__ITEMS);
		createEReference(schemaEClass, SCHEMA__ITEMS_LIST);
		createEReference(schemaEClass, SCHEMA__ADDITIONAL_ITEMS);
		createEReference(schemaEClass, SCHEMA__CONTAINS);
		createEReference(schemaEClass, SCHEMA__PROPERTIES);
		createEReference(schemaEClass, SCHEMA__PATTERN_PROPERTIES);
		createEReference(schemaEClass, SCHEMA__ADDITIONAL_PROPERTIES);
		createEReference(schemaEClass, SCHEMA__PROPERTY_NAMES);
		createEReference(schemaEClass, SCHEMA__UNEVALUATED_ITEMS);
		createEReference(schemaEClass, SCHEMA__UNEVALUATED_PROPERTIES);
		createEAttribute(schemaEClass, SCHEMA__CONTENT_ENCODING);
		createEAttribute(schemaEClass, SCHEMA__CONTENT_MEDIA_TYPE);
		createEReference(schemaEClass, SCHEMA__CONTENT_SCHEMA);
		createEAttribute(schemaEClass, SCHEMA__NULLABLE);
		createEReference(schemaEClass, SCHEMA__DISCRIMINATOR);
		createEReference(schemaEClass, SCHEMA__XML);
		createEReference(schemaEClass, SCHEMA__EXTERNAL_DOCS);
		createEAttribute(schemaEClass, SCHEMA__EXAMPLE);
		createEReference(schemaEClass, SCHEMA__KEYWORDS);
		createEReference(schemaEClass, SCHEMA__CLASSIFIER);

		discriminatorEClass = createEClass(DISCRIMINATOR);
		createEAttribute(discriminatorEClass, DISCRIMINATOR__PROPERTY_NAME);
		createEReference(discriminatorEClass, DISCRIMINATOR__MAPPING);
		createEAttribute(discriminatorEClass, DISCRIMINATOR__DEFAULT_MAPPING);
		createEReference(discriminatorEClass, DISCRIMINATOR__EXTENSIONS);

		xmlEClass = createEClass(XML);
		createEAttribute(xmlEClass, XML__NAME);
		createEAttribute(xmlEClass, XML__NAMESPACE);
		createEAttribute(xmlEClass, XML__PREFIX);
		createEAttribute(xmlEClass, XML__ATTRIBUTE);
		createEAttribute(xmlEClass, XML__WRAPPED);
		createEAttribute(xmlEClass, XML__NODE_TYPE);
		createEReference(xmlEClass, XML__EXTENSIONS);

		externalDocumentationEClass = createEClass(EXTERNAL_DOCUMENTATION);
		createEAttribute(externalDocumentationEClass, EXTERNAL_DOCUMENTATION__DESCRIPTION);
		createEAttribute(externalDocumentationEClass, EXTERNAL_DOCUMENTATION__URL);
		createEReference(externalDocumentationEClass, EXTERNAL_DOCUMENTATION__EXTENSIONS);

		dependencyEClass = createEClass(DEPENDENCY);
		createEReference(dependencyEClass, DEPENDENCY__SCHEMA);
		createEAttribute(dependencyEClass, DEPENDENCY__PROPERTIES);

		schemaEntryEClass = createEClass(SCHEMA_ENTRY);
		createEAttribute(schemaEntryEClass, SCHEMA_ENTRY__KEY);
		createEReference(schemaEntryEClass, SCHEMA_ENTRY__VALUE);

		dependencyEntryEClass = createEClass(DEPENDENCY_ENTRY);
		createEAttribute(dependencyEntryEClass, DEPENDENCY_ENTRY__KEY);
		createEReference(dependencyEntryEClass, DEPENDENCY_ENTRY__VALUE);

		stringListEntryEClass = createEClass(STRING_LIST_ENTRY);
		createEAttribute(stringListEntryEClass, STRING_LIST_ENTRY__KEY);
		createEAttribute(stringListEntryEClass, STRING_LIST_ENTRY__VALUE);

		stringEntryEClass = createEClass(STRING_ENTRY);
		createEAttribute(stringEntryEClass, STRING_ENTRY__KEY);
		createEAttribute(stringEntryEClass, STRING_ENTRY__VALUE);

		vocabularyEntryEClass = createEClass(VOCABULARY_ENTRY);
		createEAttribute(vocabularyEntryEClass, VOCABULARY_ENTRY__KEY);
		createEAttribute(vocabularyEntryEClass, VOCABULARY_ENTRY__VALUE);

		keywordEntryEClass = createEClass(KEYWORD_ENTRY);
		createEAttribute(keywordEntryEClass, KEYWORD_ENTRY__KEY);
		createEAttribute(keywordEntryEClass, KEYWORD_ENTRY__VALUE);

		// Create enums
		simpleTypeEEnum = createEEnum(SIMPLE_TYPE);
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

		// Initialize classes, features, and operations; add parameters
		initEClass(schemaEClass, Schema.class, "Schema", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSchema_BooleanSchema(), ecorePackage.getEBooleanObject(), "booleanSchema", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Schema(), ecorePackage.getEString(), "schema", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Id(), ecorePackage.getEString(), "id", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_LegacyId(), ecorePackage.getEString(), "legacyId", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Anchor(), ecorePackage.getEString(), "anchor", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_DynamicRef(), ecorePackage.getEString(), "dynamicRef", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_DynamicAnchor(), ecorePackage.getEString(), "dynamicAnchor", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_RecursiveRef(), ecorePackage.getEString(), "recursiveRef", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_RecursiveAnchor(), ecorePackage.getEBooleanObject(), "recursiveAnchor", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Vocabulary(), this.getVocabularyEntry(), null, "vocabulary", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Comment(), ecorePackage.getEString(), "comment", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Defs(), this.getSchemaEntry(), null, "defs", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Definitions(), this.getSchemaEntry(), null, "definitions", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Title(), ecorePackage.getEString(), "title", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Description(), ecorePackage.getEString(), "description", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Default(), ecorePackage.getEJavaObject(), "default", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Deprecated(), ecorePackage.getEBooleanObject(), "deprecated", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_ReadOnly(), ecorePackage.getEBooleanObject(), "readOnly", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_WriteOnly(), ecorePackage.getEBooleanObject(), "writeOnly", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Examples(), ecorePackage.getEJavaObject(), "examples", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Type(), this.getSimpleType(), "type", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_TypeAsList(), ecorePackage.getEBoolean(), "typeAsList", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Enum(), ecorePackage.getEJavaObject(), "enum", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Const(), ecorePackage.getEJavaObject(), "const", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_MultipleOf(), ecorePackage.getEBigDecimal(), "multipleOf", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Maximum(), ecorePackage.getEBigDecimal(), "maximum", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_ExclusiveMaximum(), ecorePackage.getEBigDecimal(), "exclusiveMaximum", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_ExclusiveMaximumFlag(), ecorePackage.getEBooleanObject(), "exclusiveMaximumFlag", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Minimum(), ecorePackage.getEBigDecimal(), "minimum", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_ExclusiveMinimum(), ecorePackage.getEBigDecimal(), "exclusiveMinimum", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_ExclusiveMinimumFlag(), ecorePackage.getEBooleanObject(), "exclusiveMinimumFlag", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_MaxLength(), ecorePackage.getEIntegerObject(), "maxLength", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_MinLength(), ecorePackage.getEIntegerObject(), "minLength", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Pattern(), ecorePackage.getEString(), "pattern", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_MaxItems(), ecorePackage.getEIntegerObject(), "maxItems", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_MinItems(), ecorePackage.getEIntegerObject(), "minItems", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_UniqueItems(), ecorePackage.getEBooleanObject(), "uniqueItems", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_MaxContains(), ecorePackage.getEIntegerObject(), "maxContains", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_MinContains(), ecorePackage.getEIntegerObject(), "minContains", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_MaxProperties(), ecorePackage.getEIntegerObject(), "maxProperties", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_MinProperties(), ecorePackage.getEIntegerObject(), "minProperties", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Required(), ecorePackage.getEString(), "required", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_DependentRequired(), this.getStringListEntry(), null, "dependentRequired", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Format(), ecorePackage.getEString(), "format", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_AllOf(), this.getSchema(), null, "allOf", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_AnyOf(), this.getSchema(), null, "anyOf", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_OneOf(), this.getSchema(), null, "oneOf", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Not(), this.getSchema(), null, "not", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_If(), this.getSchema(), null, "if", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Then(), this.getSchema(), null, "then", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Else(), this.getSchema(), null, "else", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_DependentSchemas(), this.getSchemaEntry(), null, "dependentSchemas", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Dependencies(), this.getDependencyEntry(), null, "dependencies", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_PrefixItems(), this.getSchema(), null, "prefixItems", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Items(), this.getSchema(), null, "items", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_ItemsList(), this.getSchema(), null, "itemsList", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_AdditionalItems(), this.getSchema(), null, "additionalItems", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Contains(), this.getSchema(), null, "contains", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Properties(), this.getSchemaEntry(), null, "properties", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_PatternProperties(), this.getSchemaEntry(), null, "patternProperties", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_AdditionalProperties(), this.getSchema(), null, "additionalProperties", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_PropertyNames(), this.getSchema(), null, "propertyNames", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_UnevaluatedItems(), this.getSchema(), null, "unevaluatedItems", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_UnevaluatedProperties(), this.getSchema(), null, "unevaluatedProperties", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_ContentEncoding(), ecorePackage.getEString(), "contentEncoding", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_ContentMediaType(), ecorePackage.getEString(), "contentMediaType", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_ContentSchema(), this.getSchema(), null, "contentSchema", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Nullable(), ecorePackage.getEBooleanObject(), "nullable", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Discriminator(), this.getDiscriminator(), null, "discriminator", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Xml(), this.getXml(), null, "xml", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_ExternalDocs(), this.getExternalDocumentation(), null, "externalDocs", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSchema_Example(), ecorePackage.getEJavaObject(), "example", null, 0, 1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Keywords(), this.getKeywordEntry(), null, "keywords", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchema_Classifier(), ecorePackage.getEClassifier(), null, "classifier", null, 0, 1, Schema.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(discriminatorEClass, Discriminator.class, "Discriminator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDiscriminator_PropertyName(), ecorePackage.getEString(), "propertyName", null, 1, 1, Discriminator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDiscriminator_Mapping(), this.getStringEntry(), null, "mapping", null, 0, -1, Discriminator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDiscriminator_DefaultMapping(), ecorePackage.getEString(), "defaultMapping", null, 0, 1, Discriminator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDiscriminator_Extensions(), this.getKeywordEntry(), null, "extensions", null, 0, -1, Discriminator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(xmlEClass, Xml.class, "Xml", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getXml_Name(), ecorePackage.getEString(), "name", null, 0, 1, Xml.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXml_Namespace(), ecorePackage.getEString(), "namespace", null, 0, 1, Xml.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXml_Prefix(), ecorePackage.getEString(), "prefix", null, 0, 1, Xml.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXml_Attribute(), ecorePackage.getEBooleanObject(), "attribute", null, 0, 1, Xml.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXml_Wrapped(), ecorePackage.getEBooleanObject(), "wrapped", null, 0, 1, Xml.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXml_NodeType(), ecorePackage.getEString(), "nodeType", null, 0, 1, Xml.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getXml_Extensions(), this.getKeywordEntry(), null, "extensions", null, 0, -1, Xml.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(externalDocumentationEClass, ExternalDocumentation.class, "ExternalDocumentation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getExternalDocumentation_Description(), ecorePackage.getEString(), "description", null, 0, 1, ExternalDocumentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getExternalDocumentation_Url(), ecorePackage.getEString(), "url", null, 1, 1, ExternalDocumentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getExternalDocumentation_Extensions(), this.getKeywordEntry(), null, "extensions", null, 0, -1, ExternalDocumentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dependencyEClass, Dependency.class, "Dependency", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDependency_Schema(), this.getSchema(), null, "schema", null, 0, 1, Dependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDependency_Properties(), ecorePackage.getEString(), "properties", null, 0, -1, Dependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(schemaEntryEClass, Map.Entry.class, "SchemaEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSchemaEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchemaEntry_Value(), this.getSchema(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dependencyEntryEClass, Map.Entry.class, "DependencyEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDependencyEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDependencyEntry_Value(), this.getDependency(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stringListEntryEClass, Map.Entry.class, "StringListEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringListEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStringListEntry_Value(), ecorePackage.getEString(), "value", null, 0, -1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stringEntryEClass, Map.Entry.class, "StringEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStringEntry_Value(), ecorePackage.getEString(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(vocabularyEntryEClass, Map.Entry.class, "VocabularyEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getVocabularyEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getVocabularyEntry_Value(), ecorePackage.getEBooleanObject(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(keywordEntryEClass, Map.Entry.class, "KeywordEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getKeywordEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getKeywordEntry_Value(), ecorePackage.getEJavaObject(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(simpleTypeEEnum, SimpleType.class, "SimpleType");
		addEEnumLiteral(simpleTypeEEnum, SimpleType.ARRAY);
		addEEnumLiteral(simpleTypeEEnum, SimpleType.BOOLEAN);
		addEEnumLiteral(simpleTypeEEnum, SimpleType.INTEGER);
		addEEnumLiteral(simpleTypeEEnum, SimpleType.NULL);
		addEEnumLiteral(simpleTypeEEnum, SimpleType.NUMBER);
		addEEnumLiteral(simpleTypeEEnum, SimpleType.OBJECT);
		addEEnumLiteral(simpleTypeEEnum, SimpleType.STRING);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://eclipse.org/fennec/codec
		createCodecAnnotations();
		// http://eclipse.org/fennec/jsonschema/model
		createModelAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://eclipse.org/fennec/codec</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createCodecAnnotations() {
		String source = "http://eclipse.org/fennec/codec";
		addAnnotation
		  (schemaEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "keywords"
		   });
		addAnnotation
		  (getSchema_Schema(),
		   source,
		   new String[] {
			   "key", "$schema"
		   });
		addAnnotation
		  (getSchema_Id(),
		   source,
		   new String[] {
			   "key", "$id"
		   });
		addAnnotation
		  (getSchema_LegacyId(),
		   source,
		   new String[] {
			   "key", "id"
		   });
		addAnnotation
		  (getSchema_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getSchema_Anchor(),
		   source,
		   new String[] {
			   "key", "$anchor"
		   });
		addAnnotation
		  (getSchema_DynamicRef(),
		   source,
		   new String[] {
			   "key", "$dynamicRef"
		   });
		addAnnotation
		  (getSchema_DynamicAnchor(),
		   source,
		   new String[] {
			   "key", "$dynamicAnchor"
		   });
		addAnnotation
		  (getSchema_RecursiveRef(),
		   source,
		   new String[] {
			   "key", "$recursiveRef"
		   });
		addAnnotation
		  (getSchema_RecursiveAnchor(),
		   source,
		   new String[] {
			   "key", "$recursiveAnchor"
		   });
		addAnnotation
		  (getSchema_Vocabulary(),
		   source,
		   new String[] {
			   "key", "$vocabulary"
		   });
		addAnnotation
		  (getSchema_Comment(),
		   source,
		   new String[] {
			   "key", "$comment"
		   });
		addAnnotation
		  (getSchema_Defs(),
		   source,
		   new String[] {
			   "key", "$defs",
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_Definitions(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_ExclusiveMaximumFlag(),
		   source,
		   new String[] {
			   "key", "exclusiveMaximum"
		   });
		addAnnotation
		  (getSchema_ExclusiveMinimumFlag(),
		   source,
		   new String[] {
			   "key", "exclusiveMinimum"
		   });
		addAnnotation
		  (getSchema_AllOf(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_AnyOf(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_OneOf(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_Not(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_If(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_Then(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_Else(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_DependentSchemas(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_PrefixItems(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_Items(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_ItemsList(),
		   source,
		   new String[] {
			   "key", "items",
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_AdditionalItems(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_Contains(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_Properties(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_PatternProperties(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_AdditionalProperties(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_PropertyNames(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_UnevaluatedItems(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_UnevaluatedProperties(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchema_ContentSchema(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (discriminatorEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (xmlEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (externalDocumentationEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getDependency_Schema(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getSchemaEntry_Value(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
	}

	/**
	 * Initializes the annotations for <b>http://eclipse.org/fennec/jsonschema/model</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createModelAnnotations() {
		String source = "http://eclipse.org/fennec/jsonschema/model";
		addAnnotation
		  (getSchema_Id(),
		   source,
		   new String[] {
			   "since", "draft-06"
		   });
		addAnnotation
		  (getSchema_LegacyId(),
		   source,
		   new String[] {
			   "until", "draft-04"
		   });
		addAnnotation
		  (getSchema_Anchor(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_DynamicRef(),
		   source,
		   new String[] {
			   "since", "2020-12"
		   });
		addAnnotation
		  (getSchema_DynamicAnchor(),
		   source,
		   new String[] {
			   "since", "2020-12"
		   });
		addAnnotation
		  (getSchema_RecursiveRef(),
		   source,
		   new String[] {
			   "since", "2019-09",
			   "until", "2019-09"
		   });
		addAnnotation
		  (getSchema_RecursiveAnchor(),
		   source,
		   new String[] {
			   "since", "2019-09",
			   "until", "2019-09"
		   });
		addAnnotation
		  (getSchema_Vocabulary(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_Comment(),
		   source,
		   new String[] {
			   "since", "draft-07"
		   });
		addAnnotation
		  (getSchema_Defs(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_Deprecated(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_ReadOnly(),
		   source,
		   new String[] {
			   "since", "draft-07"
		   });
		addAnnotation
		  (getSchema_WriteOnly(),
		   source,
		   new String[] {
			   "since", "draft-07"
		   });
		addAnnotation
		  (getSchema_Examples(),
		   source,
		   new String[] {
			   "since", "draft-06"
		   });
		addAnnotation
		  (getSchema_Const(),
		   source,
		   new String[] {
			   "since", "draft-06"
		   });
		addAnnotation
		  (getSchema_ExclusiveMaximum(),
		   source,
		   new String[] {
			   "since", "draft-06"
		   });
		addAnnotation
		  (getSchema_ExclusiveMaximumFlag(),
		   source,
		   new String[] {
			   "until", "draft-04"
		   });
		addAnnotation
		  (getSchema_ExclusiveMinimum(),
		   source,
		   new String[] {
			   "since", "draft-06"
		   });
		addAnnotation
		  (getSchema_ExclusiveMinimumFlag(),
		   source,
		   new String[] {
			   "until", "draft-04"
		   });
		addAnnotation
		  (getSchema_MaxContains(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_MinContains(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_DependentRequired(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_If(),
		   source,
		   new String[] {
			   "since", "draft-07"
		   });
		addAnnotation
		  (getSchema_Then(),
		   source,
		   new String[] {
			   "since", "draft-07"
		   });
		addAnnotation
		  (getSchema_Else(),
		   source,
		   new String[] {
			   "since", "draft-07"
		   });
		addAnnotation
		  (getSchema_DependentSchemas(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_Dependencies(),
		   source,
		   new String[] {
			   "until", "2019-09"
		   });
		addAnnotation
		  (getSchema_PrefixItems(),
		   source,
		   new String[] {
			   "since", "2020-12"
		   });
		addAnnotation
		  (getSchema_ItemsList(),
		   source,
		   new String[] {
			   "until", "2019-09"
		   });
		addAnnotation
		  (getSchema_AdditionalItems(),
		   source,
		   new String[] {
			   "until", "2019-09"
		   });
		addAnnotation
		  (getSchema_Contains(),
		   source,
		   new String[] {
			   "since", "draft-06"
		   });
		addAnnotation
		  (getSchema_PropertyNames(),
		   source,
		   new String[] {
			   "since", "draft-06"
		   });
		addAnnotation
		  (getSchema_UnevaluatedItems(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_UnevaluatedProperties(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_ContentEncoding(),
		   source,
		   new String[] {
			   "since", "draft-07"
		   });
		addAnnotation
		  (getSchema_ContentMediaType(),
		   source,
		   new String[] {
			   "since", "draft-07"
		   });
		addAnnotation
		  (getSchema_ContentSchema(),
		   source,
		   new String[] {
			   "since", "2019-09"
		   });
		addAnnotation
		  (getSchema_Nullable(),
		   source,
		   new String[] {
			   "until", "oas-3.0",
			   "dialect", "oas"
		   });
		addAnnotation
		  (getSchema_Discriminator(),
		   source,
		   new String[] {
			   "dialect", "oas"
		   });
		addAnnotation
		  (getSchema_Xml(),
		   source,
		   new String[] {
			   "dialect", "oas"
		   });
		addAnnotation
		  (getSchema_ExternalDocs(),
		   source,
		   new String[] {
			   "dialect", "oas"
		   });
		addAnnotation
		  (getSchema_Example(),
		   source,
		   new String[] {
			   "dialect", "oas"
		   });
		addAnnotation
		  (getDiscriminator_DefaultMapping(),
		   source,
		   new String[] {
			   "since", "oas-3.2",
			   "dialect", "oas"
		   });
		addAnnotation
		  (getXml_NodeType(),
		   source,
		   new String[] {
			   "since", "oas-3.2",
			   "dialect", "oas"
		   });
	}

} //JsonSchemaPackageImpl
