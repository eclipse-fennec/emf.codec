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
 * A representation of the literals of the enumeration '<em><b>Multi Valued Ref Strategy</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getMultiValuedRefStrategy()
 * @model
 * @generated
 */
@ProviderType
public enum MultiValuedRefStrategy implements Enumerator {
	/**
	 * The '<em><b>ALWAYS JOIN TABLE</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ALWAYS_JOIN_TABLE_VALUE
	 * @generated
	 * @ordered
	 */
	ALWAYS_JOIN_TABLE(0, "ALWAYS_JOIN_TABLE", "ALWAYS_JOIN_TABLE"),

	/**
	 * The '<em><b>PREFER FK COLUMN</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PREFER_FK_COLUMN_VALUE
	 * @generated
	 * @ordered
	 */
	PREFER_FK_COLUMN(1, "PREFER_FK_COLUMN", "PREFER_FK_COLUMN");

	/**
	 * The '<em><b>ALWAYS JOIN TABLE</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ALWAYS_JOIN_TABLE
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int ALWAYS_JOIN_TABLE_VALUE = 0;

	/**
	 * The '<em><b>PREFER FK COLUMN</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PREFER_FK_COLUMN
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int PREFER_FK_COLUMN_VALUE = 1;

	/**
	 * An array of all the '<em><b>Multi Valued Ref Strategy</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final MultiValuedRefStrategy[] VALUES_ARRAY =
		new MultiValuedRefStrategy[] {
			ALWAYS_JOIN_TABLE,
			PREFER_FK_COLUMN,
		};

	/**
	 * A public read-only list of all the '<em><b>Multi Valued Ref Strategy</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<MultiValuedRefStrategy> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Multi Valued Ref Strategy</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static MultiValuedRefStrategy get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			MultiValuedRefStrategy result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Multi Valued Ref Strategy</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static MultiValuedRefStrategy getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			MultiValuedRefStrategy result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Multi Valued Ref Strategy</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static MultiValuedRefStrategy get(int value) {
		switch (value) {
			case ALWAYS_JOIN_TABLE_VALUE: return ALWAYS_JOIN_TABLE;
			case PREFER_FK_COLUMN_VALUE: return PREFER_FK_COLUMN;
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
	private MultiValuedRefStrategy(int value, String name, String literal) {
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
	
} //MultiValuedRefStrategy
