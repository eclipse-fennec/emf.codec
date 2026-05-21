/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular;

import java.math.BigDecimal;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Big Decimal Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getBigDecimalCell()
 * @model
 * @generated
 */
@ProviderType
public interface BigDecimalCell extends Cell {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(BigDecimal)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getBigDecimalCell_Value()
	 * @model
	 * @generated
	 */
	BigDecimal getValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(BigDecimal value);

} // BigDecimalCell
