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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.codec.jsonschema.model.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class JsonSchemaFactoryImpl extends EFactoryImpl implements JsonSchemaFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static JsonSchemaFactory init() {
		try {
			JsonSchemaFactory theJsonSchemaFactory = (JsonSchemaFactory)EPackage.Registry.INSTANCE.getEFactory(JsonSchemaPackage.eNS_URI);
			if (theJsonSchemaFactory != null) {
				return theJsonSchemaFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new JsonSchemaFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public JsonSchemaFactoryImpl() {
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
			case JsonSchemaPackage.SCHEMA: return createSchema();
			case JsonSchemaPackage.DISCRIMINATOR: return createDiscriminator();
			case JsonSchemaPackage.XML: return createXml();
			case JsonSchemaPackage.EXTERNAL_DOCUMENTATION: return createExternalDocumentation();
			case JsonSchemaPackage.DEPENDENCY: return createDependency();
			case JsonSchemaPackage.SCHEMA_ENTRY: return (EObject)createSchemaEntry();
			case JsonSchemaPackage.DEPENDENCY_ENTRY: return (EObject)createDependencyEntry();
			case JsonSchemaPackage.STRING_LIST_ENTRY: return (EObject)createStringListEntry();
			case JsonSchemaPackage.STRING_ENTRY: return (EObject)createStringEntry();
			case JsonSchemaPackage.VOCABULARY_ENTRY: return (EObject)createVocabularyEntry();
			case JsonSchemaPackage.KEYWORD_ENTRY: return (EObject)createKeywordEntry();
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
			case JsonSchemaPackage.SIMPLE_TYPE:
				return createSimpleTypeFromString(eDataType, initialValue);
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
			case JsonSchemaPackage.SIMPLE_TYPE:
				return convertSimpleTypeToString(eDataType, instanceValue);
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
	public Schema createSchema() {
		SchemaImpl schema = new SchemaImpl();
		return schema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Discriminator createDiscriminator() {
		DiscriminatorImpl discriminator = new DiscriminatorImpl();
		return discriminator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Xml createXml() {
		XmlImpl xml = new XmlImpl();
		return xml;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ExternalDocumentation createExternalDocumentation() {
		ExternalDocumentationImpl externalDocumentation = new ExternalDocumentationImpl();
		return externalDocumentation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Dependency createDependency() {
		DependencyImpl dependency = new DependencyImpl();
		return dependency;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Schema> createSchemaEntry() {
		SchemaEntryImpl schemaEntry = new SchemaEntryImpl();
		return schemaEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Dependency> createDependencyEntry() {
		DependencyEntryImpl dependencyEntry = new DependencyEntryImpl();
		return dependencyEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, EList<String>> createStringListEntry() {
		StringListEntryImpl stringListEntry = new StringListEntryImpl();
		return stringListEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, String> createStringEntry() {
		StringEntryImpl stringEntry = new StringEntryImpl();
		return stringEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Boolean> createVocabularyEntry() {
		VocabularyEntryImpl vocabularyEntry = new VocabularyEntryImpl();
		return vocabularyEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Object> createKeywordEntry() {
		KeywordEntryImpl keywordEntry = new KeywordEntryImpl();
		return keywordEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SimpleType createSimpleTypeFromString(EDataType eDataType, String initialValue) {
		SimpleType result = SimpleType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSimpleTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public JsonSchemaPackage getJsonSchemaPackage() {
		return (JsonSchemaPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static JsonSchemaPackage getPackage() {
		return JsonSchemaPackage.eINSTANCE;
	}

} //JsonSchemaFactoryImpl
