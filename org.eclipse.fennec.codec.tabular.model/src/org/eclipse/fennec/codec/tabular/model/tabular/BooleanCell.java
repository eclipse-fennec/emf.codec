/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Boolean Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell#isValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getBooleanCell()
 * @model
 * @generated
 */
@ProviderType
public interface BooleanCell extends Cell {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(boolean)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getBooleanCell_Value()
	 * @model
	 * @generated
	 */
	boolean isValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell#isValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #isValue()
	 * @generated
	 */
	void setValue(boolean value);

} // BooleanCell
