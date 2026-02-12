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
 * A representation of the literals of the enumeration '<em><b>Id Key Mode</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Controls how the ID value is represented in the serialized output. Determines whether a dedicated ID key, the original feature keys, or both are written.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getIdKeyMode()
 * @model
 * @generated
 */
@ProviderType
public enum IdKeyMode implements Enumerator {
	/**
	 * The '<em><b>ID ONLY</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Only the dedicated ID key (e.g., '_id') is written. The original feature value is not duplicated under its feature name.
	 * <!-- end-model-doc -->
	 * @see #ID_ONLY_VALUE
	 * @generated
	 * @ordered
	 */
	ID_ONLY(0, "ID_ONLY", "ID_ONLY"),

	/**
	 * The '<em><b>BOTH</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Both the dedicated ID key and the original feature name(s) are written. Useful when consumers need both an ID field and the raw feature values.
	 * <!-- end-model-doc -->
	 * @see #BOTH_VALUE
	 * @generated
	 * @ordered
	 */
	BOTH(1, "BOTH", "BOTH"),

	/**
	 * The '<em><b>FEATURE ONLY</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Only the original feature name(s) are written, no dedicated ID key. The ID is implicit from the feature values.
	 * <!-- end-model-doc -->
	 * @see #FEATURE_ONLY_VALUE
	 * @generated
	 * @ordered
	 */
	FEATURE_ONLY(2, "FEATURE_ONLY", "FEATURE_ONLY"),

	/**
	 * The '<em><b>NONE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * No ID output at all. Both the dedicated ID key and the feature-based ID are suppressed entirely.
	 * <!-- end-model-doc -->
	 * @see #NONE_VALUE
	 * @generated
	 * @ordered
	 */
	NONE(3, "NONE", "NONE");

	/**
	 * The '<em><b>ID ONLY</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Only the dedicated ID key (e.g., '_id') is written. The original feature value is not duplicated under its feature name.
	 * <!-- end-model-doc -->
	 * @see #ID_ONLY
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ID_ONLY_VALUE = 0;

	/**
	 * The '<em><b>BOTH</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Both the dedicated ID key and the original feature name(s) are written. Useful when consumers need both an ID field and the raw feature values.
	 * <!-- end-model-doc -->
	 * @see #BOTH
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int BOTH_VALUE = 1;

	/**
	 * The '<em><b>FEATURE ONLY</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Only the original feature name(s) are written, no dedicated ID key. The ID is implicit from the feature values.
	 * <!-- end-model-doc -->
	 * @see #FEATURE_ONLY
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FEATURE_ONLY_VALUE = 2;

	/**
	 * The '<em><b>NONE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * No ID output at all. Both the dedicated ID key and the feature-based ID are suppressed entirely.
	 * <!-- end-model-doc -->
	 * @see #NONE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NONE_VALUE = 3;

	/**
	 * An array of all the '<em><b>Id Key Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final IdKeyMode[] VALUES_ARRAY =
		new IdKeyMode[] {
			ID_ONLY,
			BOTH,
			FEATURE_ONLY,
			NONE,
		};

	/**
	 * A public read-only list of all the '<em><b>Id Key Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<IdKeyMode> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Id Key Mode</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static IdKeyMode get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			IdKeyMode result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Id Key Mode</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static IdKeyMode getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			IdKeyMode result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Id Key Mode</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static IdKeyMode get(int value) {
		switch (value) {
			case ID_ONLY_VALUE: return ID_ONLY;
			case BOTH_VALUE: return BOTH;
			case FEATURE_ONLY_VALUE: return FEATURE_ONLY;
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
	private IdKeyMode(int value, String name, String literal) {
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
	
} //IdKeyMode
