/*
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
package org.eclipse.fennec.model.metadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Type Strategy</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Strategy for serializing type information. Determines what kind of type identifier is written to the output for each EObject.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getTypeStrategy()
 * @model
 * @generated
 */
@ProviderType
public enum TypeStrategy implements Enumerator {
	/**
	 * The '<em><b>NAME</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use the simple EClass name (e.g., 'Person'). Requires unique class names within the deserialization context.
	 * <!-- end-model-doc -->
	 * @see #NAME_VALUE
	 * @generated
	 * @ordered
	 */
	NAME(0, "NAME", "NAME"),

	/**
	 * The '<em><b>CLASS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use the fully qualified Java instance class name (e.g., 'org.example.model.Person').
	 * <!-- end-model-doc -->
	 * @see #CLASS_VALUE
	 * @generated
	 * @ordered
	 */
	CLASS(1, "CLASS", "CLASS"),

	/**
	 * The '<em><b>URI</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use the full EMF EClass URI (e.g., 'http://example.org/model#//Person'). Most precise, always unique.
	 * <!-- end-model-doc -->
	 * @see #URI_VALUE
	 * @generated
	 * @ordered
	 */
	URI(2, "URI", "URI"),

	/**
	 * The '<em><b>SCHEMA AND TYPE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use separate top-level fields for schema (nsURI) and type (class name). Useful for formats that benefit from split metadata.
	 * <!-- end-model-doc -->
	 * @see #SCHEMA_AND_TYPE_VALUE
	 * @generated
	 * @ordered
	 */
	SCHEMA_AND_TYPE(3, "SCHEMA_AND_TYPE", "SCHEMA_AND_TYPE"),

	/**
	 * The '<em><b>NUMERIC</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use numeric classifier IDs from the EPackage. Compact but fragile: IDs can change when the model evolves. Use only in controlled environments.
	 * <!-- end-model-doc -->
	 * @see #NUMERIC_VALUE
	 * @generated
	 * @ordered
	 */
	NUMERIC(4, "NUMERIC", "NUMERIC"),

	/**
	 * The '<em><b>NONE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * No type information serialized. For deserialization, the EClass must be provided via CODEC_ROOT_TYPE hint or be unambiguous from context.
	 * <!-- end-model-doc -->
	 * @see #NONE_VALUE
	 * @generated
	 * @ordered
	 */
	NONE(5, "NONE", "NONE");

	/**
	 * The '<em><b>NAME</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use the simple EClass name (e.g., 'Person'). Requires unique class names within the deserialization context.
	 * <!-- end-model-doc -->
	 * @see #NAME
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NAME_VALUE = 0;

	/**
	 * The '<em><b>CLASS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use the fully qualified Java instance class name (e.g., 'org.example.model.Person').
	 * <!-- end-model-doc -->
	 * @see #CLASS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int CLASS_VALUE = 1;

	/**
	 * The '<em><b>URI</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use the full EMF EClass URI (e.g., 'http://example.org/model#//Person'). Most precise, always unique.
	 * <!-- end-model-doc -->
	 * @see #URI
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int URI_VALUE = 2;

	/**
	 * The '<em><b>SCHEMA AND TYPE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use separate top-level fields for schema (nsURI) and type (class name). Useful for formats that benefit from split metadata.
	 * <!-- end-model-doc -->
	 * @see #SCHEMA_AND_TYPE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int SCHEMA_AND_TYPE_VALUE = 3;

	/**
	 * The '<em><b>NUMERIC</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use numeric classifier IDs from the EPackage. Compact but fragile: IDs can change when the model evolves. Use only in controlled environments.
	 * <!-- end-model-doc -->
	 * @see #NUMERIC
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NUMERIC_VALUE = 4;

	/**
	 * The '<em><b>NONE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * No type information serialized. For deserialization, the EClass must be provided via CODEC_ROOT_TYPE hint or be unambiguous from context.
	 * <!-- end-model-doc -->
	 * @see #NONE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NONE_VALUE = 5;

	/**
	 * An array of all the '<em><b>Type Strategy</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final TypeStrategy[] VALUES_ARRAY =
		new TypeStrategy[] {
			NAME,
			CLASS,
			URI,
			SCHEMA_AND_TYPE,
			NUMERIC,
			NONE,
		};

	/**
	 * A public read-only list of all the '<em><b>Type Strategy</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<TypeStrategy> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Type Strategy</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static TypeStrategy get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			TypeStrategy result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Type Strategy</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static TypeStrategy getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			TypeStrategy result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Type Strategy</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static TypeStrategy get(int value) {
		switch (value) {
			case NAME_VALUE: return NAME;
			case CLASS_VALUE: return CLASS;
			case URI_VALUE: return URI;
			case SCHEMA_AND_TYPE_VALUE: return SCHEMA_AND_TYPE;
			case NUMERIC_VALUE: return NUMERIC;
			case NONE_VALUE: return NONE;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private TypeStrategy(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //TypeStrategy
