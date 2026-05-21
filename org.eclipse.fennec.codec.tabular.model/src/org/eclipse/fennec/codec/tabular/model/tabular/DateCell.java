/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Date Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.DateCell#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.DateCell#getDateFormat <em>Date Format</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getDateCell()
 * @model
 * @generated
 */
@ProviderType
public interface DateCell extends Cell {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(Date)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getDateCell_Value()
	 * @model
	 * @generated
	 */
	Date getValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.DateCell#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(Date value);

	/**
	 * Returns the value of the '<em><b>Date Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Date Format</em>' attribute.
	 * @see #setDateFormat(String)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getDateCell_DateFormat()
	 * @model
	 * @generated
	 */
	String getDateFormat();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.DateCell#getDateFormat <em>Date Format</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Date Format</em>' attribute.
	 * @see #getDateFormat()
	 * @generated
	 */
	void setDateFormat(String value);

} // DateCell
