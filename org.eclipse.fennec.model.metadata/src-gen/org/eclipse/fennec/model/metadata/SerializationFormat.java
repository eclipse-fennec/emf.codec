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
 * A representation of the literals of the enumeration '<em><b>Serialization Format</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Format strategy for serialization output. Controls whether metadata fields (type, ID, reference, supertype) are written as simple values or nested objects. Applies uniformly across all serialization targets.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getSerializationFormat()
 * @model
 * @generated
 */
@ProviderType
public enum SerializationFormat implements Enumerator {
	/**
	 * The '<em><b>PLAIN</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Simple value output. Writes a single string, number, or array of simple values. Example: {"_type": "http://example.org/model#//Person"}
	 * <!-- end-model-doc -->
	 * @see #PLAIN_VALUE
	 * @generated
	 * @ordered
	 */
	PLAIN(0, "PLAIN", "PLAIN"),

	/**
	 * The '<em><b>STRUCTURED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Nested object output with configurable keys. Example: {"_type": {"schema": "http://example.org/model", "name": "Person"}}
	 * <!-- end-model-doc -->
	 * @see #STRUCTURED_VALUE
	 * @generated
	 * @ordered
	 */
	STRUCTURED(1, "STRUCTURED", "STRUCTURED");

	/**
	 * The '<em><b>PLAIN</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Simple value output. Writes a single string, number, or array of simple values. Example: {"_type": "http://example.org/model#//Person"}
	 * <!-- end-model-doc -->
	 * @see #PLAIN
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PLAIN_VALUE = 0;

	/**
	 * The '<em><b>STRUCTURED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Nested object output with configurable keys. Example: {"_type": {"schema": "http://example.org/model", "name": "Person"}}
	 * <!-- end-model-doc -->
	 * @see #STRUCTURED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int STRUCTURED_VALUE = 1;

	/**
	 * An array of all the '<em><b>Serialization Format</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final SerializationFormat[] VALUES_ARRAY =
		new SerializationFormat[] {
			PLAIN,
			STRUCTURED,
		};

	/**
	 * A public read-only list of all the '<em><b>Serialization Format</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<SerializationFormat> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Serialization Format</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static SerializationFormat get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			SerializationFormat result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Serialization Format</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static SerializationFormat getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			SerializationFormat result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Serialization Format</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static SerializationFormat get(int value) {
		switch (value) {
			case PLAIN_VALUE: return PLAIN;
			case STRUCTURED_VALUE: return STRUCTURED;
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
	private SerializationFormat(int value, String name, String literal) {
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
	
} //SerializationFormat
