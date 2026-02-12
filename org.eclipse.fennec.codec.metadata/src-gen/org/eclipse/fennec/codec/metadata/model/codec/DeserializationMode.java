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
package org.eclipse.fennec.codec.metadata.model.codec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Deserialization Mode</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Controls type resolution strictness during deserialization.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getDeserializationMode()
 * @model
 * @generated
 */
@ProviderType
public enum DeserializationMode implements Enumerator {
	/**
	 * The '<em><b>LENIENT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Try configured strategy first, then fallback resolution. Log warnings for mismatches. This is the default.
	 * <!-- end-model-doc -->
	 * @see #LENIENT_VALUE
	 * @generated
	 * @ordered
	 */
	LENIENT(0, "LENIENT", "LENIENT"),

	/**
	 * The '<em><b>STRICT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Type field MUST match configured strategy exactly. Missing or malformed type causes ERROR.
	 * <!-- end-model-doc -->
	 * @see #STRICT_VALUE
	 * @generated
	 * @ordered
	 */
	STRICT(1, "STRICT", "STRICT"),

	/**
	 * The '<em><b>AUTO DETECT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ignore configured strategy. Probe JSON structure to auto-detect type format.
	 * <!-- end-model-doc -->
	 * @see #AUTO_DETECT_VALUE
	 * @generated
	 * @ordered
	 */
	AUTO_DETECT(2, "AUTO_DETECT", "AUTO_DETECT");

	/**
	 * The '<em><b>LENIENT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Try configured strategy first, then fallback resolution. Log warnings for mismatches. This is the default.
	 * <!-- end-model-doc -->
	 * @see #LENIENT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int LENIENT_VALUE = 0;

	/**
	 * The '<em><b>STRICT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Type field MUST match configured strategy exactly. Missing or malformed type causes ERROR.
	 * <!-- end-model-doc -->
	 * @see #STRICT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int STRICT_VALUE = 1;

	/**
	 * The '<em><b>AUTO DETECT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Ignore configured strategy. Probe JSON structure to auto-detect type format.
	 * <!-- end-model-doc -->
	 * @see #AUTO_DETECT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int AUTO_DETECT_VALUE = 2;

	/**
	 * An array of all the '<em><b>Deserialization Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final DeserializationMode[] VALUES_ARRAY =
		new DeserializationMode[] {
			LENIENT,
			STRICT,
			AUTO_DETECT,
		};

	/**
	 * A public read-only list of all the '<em><b>Deserialization Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<DeserializationMode> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Deserialization Mode</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static DeserializationMode get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DeserializationMode result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Deserialization Mode</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static DeserializationMode getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DeserializationMode result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Deserialization Mode</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static DeserializationMode get(int value) {
		switch (value) {
			case LENIENT_VALUE: return LENIENT;
			case STRICT_VALUE: return STRICT;
			case AUTO_DETECT_VALUE: return AUTO_DETECT;
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
	private DeserializationMode(int value, String name, String literal) {
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
	
} //DeserializationMode
