/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binary Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getBinaryCell()
 * @model
 * @generated
 */
@ProviderType
public interface BinaryCell extends Cell {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(byte[])
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getBinaryCell_Value()
	 * @model
	 * @generated
	 */
	byte[] getValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(byte[] value);

} // BinaryCell
