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
 * A representation of the literals of the enumeration '<em><b>Fingerprint Mode</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * Controls whether the in-band EPackage fingerprint is written. Write-side only - the read side always accepts a fingerprint when it finds one, independent of this setting.
 * <!-- end-model-doc -->
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getFingerprintMode()
 * @model
 * @generated
 */
@ProviderType
public enum FingerprintMode implements Enumerator {
	/**
	 * The '<em><b>NONE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * No fingerprint is written. This is the default: documents stay byte-identical to single-version output unless the fingerprint is explicitly opted in.
	 * <!-- end-model-doc -->
	 * @see #NONE_VALUE
	 * @generated
	 * @ordered
	 */
	NONE(0, "NONE", "NONE"),

	/**
	 * The '<em><b>FIRST TOUCH</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Write the fingerprint at the first occurrence of each distinct EPackage instance in document order - the root for the root's package, plus every site where a new package instance enters the document (supertype substitution, reference or containment targets from other packages) - and additionally at any site whose instance deviates from the pin already established for its nsURI. Writer first-touch and reader pinning are the same rule seen from both sides.
	 * <!-- end-model-doc -->
	 * @see #FIRST_TOUCH_VALUE
	 * @generated
	 * @ordered
	 */
	FIRST_TOUCH(1, "FIRST_TOUCH", "FIRST_TOUCH");

	/**
	 * The '<em><b>NONE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * No fingerprint is written. This is the default: documents stay byte-identical to single-version output unless the fingerprint is explicitly opted in.
	 * <!-- end-model-doc -->
	 * @see #NONE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int NONE_VALUE = 0;

	/**
	 * The '<em><b>FIRST TOUCH</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Write the fingerprint at the first occurrence of each distinct EPackage instance in document order - the root for the root's package, plus every site where a new package instance enters the document (supertype substitution, reference or containment targets from other packages) - and additionally at any site whose instance deviates from the pin already established for its nsURI. Writer first-touch and reader pinning are the same rule seen from both sides.
	 * <!-- end-model-doc -->
	 * @see #FIRST_TOUCH
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FIRST_TOUCH_VALUE = 1;

	/**
	 * An array of all the '<em><b>Fingerprint Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final FingerprintMode[] VALUES_ARRAY =
		new FingerprintMode[] {
			NONE,
			FIRST_TOUCH,
		};

	/**
	 * A public read-only list of all the '<em><b>Fingerprint Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<FingerprintMode> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Fingerprint Mode</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static FingerprintMode get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			FingerprintMode result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Fingerprint Mode</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static FingerprintMode getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			FingerprintMode result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Fingerprint Mode</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static FingerprintMode get(int value) {
		switch (value) {
			case NONE_VALUE: return NONE;
			case FIRST_TOUCH_VALUE: return FIRST_TOUCH;
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
	private FingerprintMode(int value, String name, String literal) {
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
	
} //FingerprintMode
