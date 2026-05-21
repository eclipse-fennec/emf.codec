/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Column Source</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getColumnSource()
 * @model
 * @generated
 */
@ProviderType
public enum ColumnSource implements Enumerator {
	/**
	 * The '<em><b>PK</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PK_VALUE
	 * @generated
	 * @ordered
	 */
	PK(0, "PK", "PK"),

	/**
	 * The '<em><b>ATTRIBUTE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ATTRIBUTE_VALUE
	 * @generated
	 * @ordered
	 */
	ATTRIBUTE(1, "ATTRIBUTE", "ATTRIBUTE"),

	/**
	 * The '<em><b>FK PARENT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FK_PARENT_VALUE
	 * @generated
	 * @ordered
	 */
	FK_PARENT(2, "FK_PARENT", "FK_PARENT"),

	/**
	 * The '<em><b>FK CHILD</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FK_CHILD_VALUE
	 * @generated
	 * @ordered
	 */
	FK_CHILD(3, "FK_CHILD", "FK_CHILD"),

	/**
	 * The '<em><b>FLAT NESTED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FLAT_NESTED_VALUE
	 * @generated
	 * @ordered
	 */
	FLAT_NESTED(4, "FLAT_NESTED", "FLAT_NESTED");

	/**
	 * The '<em><b>PK</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PK
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PK_VALUE = 0;

	/**
	 * The '<em><b>ATTRIBUTE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ATTRIBUTE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ATTRIBUTE_VALUE = 1;

	/**
	 * The '<em><b>FK PARENT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FK_PARENT
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FK_PARENT_VALUE = 2;

	/**
	 * The '<em><b>FK CHILD</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FK_CHILD
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FK_CHILD_VALUE = 3;

	/**
	 * The '<em><b>FLAT NESTED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FLAT_NESTED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int FLAT_NESTED_VALUE = 4;

	/**
	 * An array of all the '<em><b>Column Source</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final ColumnSource[] VALUES_ARRAY =
		new ColumnSource[] {
			PK,
			ATTRIBUTE,
			FK_PARENT,
			FK_CHILD,
			FLAT_NESTED,
		};

	/**
	 * A public read-only list of all the '<em><b>Column Source</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<ColumnSource> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Column Source</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ColumnSource get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ColumnSource result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Column Source</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ColumnSource getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ColumnSource result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Column Source</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static ColumnSource get(int value) {
		switch (value) {
			case PK_VALUE: return PK;
			case ATTRIBUTE_VALUE: return ATTRIBUTE;
			case FK_PARENT_VALUE: return FK_PARENT;
			case FK_CHILD_VALUE: return FK_CHILD;
			case FLAT_NESTED_VALUE: return FLAT_NESTED;
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
	private ColumnSource(int value, String name, String literal) {
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
	
} //ColumnSource
