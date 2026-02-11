/*
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 * 
 */
package org.eclipse.fennec.codec.metadata.model.codec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Strategy Scope</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Controls where a configuration setting applies in the object graph.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getStrategyScope()
 * @model
 * @generated
 */
@ProviderType
public enum StrategyScope implements Enumerator {
	/**
	 * The '<em><b>ALL</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Apply to root object and all nested objects (containment and non-containment). This is the default.
	 * <!-- end-model-doc -->
	 * @see #ALL_VALUE
	 * @generated
	 * @ordered
	 */
	ALL(0, "ALL", "ALL"),

	/**
	 * The '<em><b>ROOT ONLY</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Apply only to the root object. Nested objects use their own class-level config or defaults.
	 * <!-- end-model-doc -->
	 * @see #ROOT_ONLY_VALUE
	 * @generated
	 * @ordered
	 */
	ROOT_ONLY(1, "ROOT_ONLY", "ROOT_ONLY"),

	/**
	 * The '<em><b>ROOT CONTAINMENT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Apply to root and objects reached via containment references only.
	 * <!-- end-model-doc -->
	 * @see #ROOT_CONTAINMENT_VALUE
	 * @generated
	 * @ordered
	 */
	ROOT_CONTAINMENT(2, "ROOT_CONTAINMENT", "ROOT_CONTAINMENT"),

	/**
	 * The '<em><b>ROOT NON CONTAINMENT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Apply to root and objects reached via non-containment references only.
	 * <!-- end-model-doc -->
	 * @see #ROOT_NON_CONTAINMENT_VALUE
	 * @generated
	 * @ordered
	 */
	ROOT_NON_CONTAINMENT(3, "ROOT_NON_CONTAINMENT", "ROOT_NON_CONTAINMENT");

	/**
	 * The '<em><b>ALL</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Apply to root object and all nested objects (containment and non-containment). This is the default.
	 * <!-- end-model-doc -->
	 * @see #ALL
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ALL_VALUE = 0;

	/**
	 * The '<em><b>ROOT ONLY</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Apply only to the root object. Nested objects use their own class-level config or defaults.
	 * <!-- end-model-doc -->
	 * @see #ROOT_ONLY
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ROOT_ONLY_VALUE = 1;

	/**
	 * The '<em><b>ROOT CONTAINMENT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Apply to root and objects reached via containment references only.
	 * <!-- end-model-doc -->
	 * @see #ROOT_CONTAINMENT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ROOT_CONTAINMENT_VALUE = 2;

	/**
	 * The '<em><b>ROOT NON CONTAINMENT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Apply to root and objects reached via non-containment references only.
	 * <!-- end-model-doc -->
	 * @see #ROOT_NON_CONTAINMENT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ROOT_NON_CONTAINMENT_VALUE = 3;

	/**
	 * An array of all the '<em><b>Strategy Scope</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final StrategyScope[] VALUES_ARRAY =
		new StrategyScope[] {
			ALL,
			ROOT_ONLY,
			ROOT_CONTAINMENT,
			ROOT_NON_CONTAINMENT,
		};

	/**
	 * A public read-only list of all the '<em><b>Strategy Scope</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<StrategyScope> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Strategy Scope</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static StrategyScope get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			StrategyScope result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Strategy Scope</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static StrategyScope getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			StrategyScope result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Strategy Scope</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static StrategyScope get(int value) {
		switch (value) {
			case ALL_VALUE: return ALL;
			case ROOT_ONLY_VALUE: return ROOT_ONLY;
			case ROOT_CONTAINMENT_VALUE: return ROOT_CONTAINMENT;
			case ROOT_NON_CONTAINMENT_VALUE: return ROOT_NON_CONTAINMENT;
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
	private StrategyScope(int value, String name, String literal) {
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
	
} //StrategyScope
