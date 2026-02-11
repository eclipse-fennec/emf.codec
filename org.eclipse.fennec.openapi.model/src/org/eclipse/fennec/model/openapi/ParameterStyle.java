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
 */
package org.eclipse.fennec.model.openapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Parameter Style</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.model.openapi.OpenApiPackage#getParameterStyle()
 * @model
 * @generated
 */
@ProviderType
public enum ParameterStyle implements Enumerator {
	/**
	 * The '<em><b>Matrix</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MATRIX_VALUE
	 * @generated
	 * @ordered
	 */
	MATRIX(0, "matrix", "matrix"),

	/**
	 * The '<em><b>Label</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LABEL_VALUE
	 * @generated
	 * @ordered
	 */
	LABEL(1, "label", "label"),

	/**
	 * The '<em><b>Form</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FORM_VALUE
	 * @generated
	 * @ordered
	 */
	FORM(2, "form", "form"),

	/**
	 * The '<em><b>Simple</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SIMPLE_VALUE
	 * @generated
	 * @ordered
	 */
	SIMPLE(3, "simple", "simple"),

	/**
	 * The '<em><b>Space Delimited</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SPACE_DELIMITED_VALUE
	 * @generated
	 * @ordered
	 */
	SPACE_DELIMITED(4, "spaceDelimited", "spaceDelimited"),

	/**
	 * The '<em><b>Pipe Delimited</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PIPE_DELIMITED_VALUE
	 * @generated
	 * @ordered
	 */
	PIPE_DELIMITED(5, "pipeDelimited", "pipeDelimited"),

	/**
	 * The '<em><b>Deep Object</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEEP_OBJECT_VALUE
	 * @generated
	 * @ordered
	 */
	DEEP_OBJECT(6, "deepObject", "deepObject");

	/**
	 * The '<em><b>Matrix</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MATRIX
	 * @model name="matrix"
	 * @generated
	 * @ordered
	 */
	public static final int MATRIX_VALUE = 0;

	/**
	 * The '<em><b>Label</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LABEL
	 * @model name="label"
	 * @generated
	 * @ordered
	 */
	public static final int LABEL_VALUE = 1;

	/**
	 * The '<em><b>Form</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FORM
	 * @model name="form"
	 * @generated
	 * @ordered
	 */
	public static final int FORM_VALUE = 2;

	/**
	 * The '<em><b>Simple</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SIMPLE
	 * @model name="simple"
	 * @generated
	 * @ordered
	 */
	public static final int SIMPLE_VALUE = 3;

	/**
	 * The '<em><b>Space Delimited</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #SPACE_DELIMITED
	 * @model name="spaceDelimited"
	 * @generated
	 * @ordered
	 */
	public static final int SPACE_DELIMITED_VALUE = 4;

	/**
	 * The '<em><b>Pipe Delimited</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PIPE_DELIMITED
	 * @model name="pipeDelimited"
	 * @generated
	 * @ordered
	 */
	public static final int PIPE_DELIMITED_VALUE = 5;

	/**
	 * The '<em><b>Deep Object</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DEEP_OBJECT
	 * @model name="deepObject"
	 * @generated
	 * @ordered
	 */
	public static final int DEEP_OBJECT_VALUE = 6;

	/**
	 * An array of all the '<em><b>Parameter Style</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final ParameterStyle[] VALUES_ARRAY =
		new ParameterStyle[] {
			MATRIX,
			LABEL,
			FORM,
			SIMPLE,
			SPACE_DELIMITED,
			PIPE_DELIMITED,
			DEEP_OBJECT,
		};

	/**
	 * A public read-only list of all the '<em><b>Parameter Style</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<ParameterStyle> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Parameter Style</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ParameterStyle get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ParameterStyle result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Parameter Style</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ParameterStyle getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ParameterStyle result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Parameter Style</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ParameterStyle get(int value) {
		switch (value) {
			case MATRIX_VALUE: return MATRIX;
			case LABEL_VALUE: return LABEL;
			case FORM_VALUE: return FORM;
			case SIMPLE_VALUE: return SIMPLE;
			case SPACE_DELIMITED_VALUE: return SPACE_DELIMITED;
			case PIPE_DELIMITED_VALUE: return PIPE_DELIMITED;
			case DEEP_OBJECT_VALUE: return DEEP_OBJECT;
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
	private ParameterStyle(int value, String name, String literal) {
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
	
} //ParameterStyle
