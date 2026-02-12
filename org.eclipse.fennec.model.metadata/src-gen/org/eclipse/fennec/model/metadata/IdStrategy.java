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
 * A representation of the literals of the enumeration '<em><b>Id Strategy</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Strategy for determining which EStructuralFeatures form the serialized ID of an EObject.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.model.metadata.MetadataPackage#getIdStrategy()
 * @model
 * @generated
 */
@ProviderType
public enum IdStrategy implements Enumerator {
	/**
	 * The '<em><b>ID FIELD</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use features marked with eID=true in the Ecore model. The standard EMF approach.
	 * <!-- end-model-doc -->
	 * @see #ID_FIELD_VALUE
	 * @generated
	 * @ordered
	 */
	ID_FIELD(0, "ID_FIELD", "ID_FIELD"),

	/**
	 * The '<em><b>COMBINED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use explicitly specified features combined with a separator. The features and separator are configured via idFeatures and idSeparator annotation keys.
	 * <!-- end-model-doc -->
	 * @see #COMBINED_VALUE
	 * @generated
	 * @ordered
	 */
	COMBINED(1, "COMBINED", "COMBINED");

	/**
	 * The '<em><b>ID FIELD</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use features marked with eID=true in the Ecore model. The standard EMF approach.
	 * <!-- end-model-doc -->
	 * @see #ID_FIELD
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ID_FIELD_VALUE = 0;

	/**
	 * The '<em><b>COMBINED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Use explicitly specified features combined with a separator. The features and separator are configured via idFeatures and idSeparator annotation keys.
	 * <!-- end-model-doc -->
	 * @see #COMBINED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int COMBINED_VALUE = 1;

	/**
	 * An array of all the '<em><b>Id Strategy</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final IdStrategy[] VALUES_ARRAY =
		new IdStrategy[] {
			ID_FIELD,
			COMBINED,
		};

	/**
	 * A public read-only list of all the '<em><b>Id Strategy</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<IdStrategy> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Id Strategy</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static IdStrategy get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			IdStrategy result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Id Strategy</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static IdStrategy getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			IdStrategy result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Id Strategy</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static IdStrategy get(int value) {
		switch (value) {
			case ID_FIELD_VALUE: return ID_FIELD;
			case COMBINED_VALUE: return COMBINED;
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
	private IdStrategy(int value, String name, String literal) {
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
	
} //IdStrategy
