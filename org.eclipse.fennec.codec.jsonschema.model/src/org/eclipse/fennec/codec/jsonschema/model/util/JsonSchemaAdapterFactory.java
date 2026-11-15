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
package org.eclipse.fennec.codec.jsonschema.model.util;

import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.fennec.codec.jsonschema.model.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage
 * @generated
 */
public class JsonSchemaAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static JsonSchemaPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public JsonSchemaAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = JsonSchemaPackage.eINSTANCE;
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
	protected JsonSchemaSwitch<Adapter> modelSwitch =
		new JsonSchemaSwitch<Adapter>() {
			@Override
			public Adapter caseSchema(Schema object) {
				return createSchemaAdapter();
			}
			@Override
			public Adapter caseDiscriminator(Discriminator object) {
				return createDiscriminatorAdapter();
			}
			@Override
			public Adapter caseXml(Xml object) {
				return createXmlAdapter();
			}
			@Override
			public Adapter caseExternalDocumentation(ExternalDocumentation object) {
				return createExternalDocumentationAdapter();
			}
			@Override
			public Adapter caseDependency(Dependency object) {
				return createDependencyAdapter();
			}
			@Override
			public Adapter caseSchemaEntry(Map.Entry<String, Schema> object) {
				return createSchemaEntryAdapter();
			}
			@Override
			public Adapter caseDependencyEntry(Map.Entry<String, Dependency> object) {
				return createDependencyEntryAdapter();
			}
			@Override
			public Adapter caseStringListEntry(Map.Entry<String, EList<String>> object) {
				return createStringListEntryAdapter();
			}
			@Override
			public Adapter caseStringEntry(Map.Entry<String, String> object) {
				return createStringEntryAdapter();
			}
			@Override
			public Adapter caseVocabularyEntry(Map.Entry<String, Boolean> object) {
				return createVocabularyEntryAdapter();
			}
			@Override
			public Adapter caseKeywordEntry(Map.Entry<String, Object> object) {
				return createKeywordEntryAdapter();
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
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.jsonschema.model.Schema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Schema
	 * @generated
	 */
	public Adapter createSchemaAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.jsonschema.model.Discriminator <em>Discriminator</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Discriminator
	 * @generated
	 */
	public Adapter createDiscriminatorAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.jsonschema.model.Xml <em>Xml</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Xml
	 * @generated
	 */
	public Adapter createXmlAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation <em>External Documentation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.jsonschema.model.ExternalDocumentation
	 * @generated
	 */
	public Adapter createExternalDocumentationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.fennec.codec.jsonschema.model.Dependency <em>Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.fennec.codec.jsonschema.model.Dependency
	 * @generated
	 */
	public Adapter createDependencyAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Schema Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createSchemaEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Dependency Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createDependencyEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String List Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createStringListEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createStringEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Vocabulary Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createVocabularyEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Keyword Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createKeywordEntryAdapter() {
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

} //JsonSchemaAdapterFactory
