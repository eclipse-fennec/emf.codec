/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular;

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage
 * @generated
 */
@ProviderType
public interface TabularFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TabularFactory eINSTANCE = org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Document</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Document</em>'.
	 * @generated
	 */
	TabularDocument createTabularDocument();

	/**
	 * Returns a new object of class '<em>Table</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Table</em>'.
	 * @generated
	 */
	Table createTable();

	/**
	 * Returns a new object of class '<em>Column</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Column</em>'.
	 * @generated
	 */
	Column createColumn();

	/**
	 * Returns a new object of class '<em>Row</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Row</em>'.
	 * @generated
	 */
	Row createRow();

	/**
	 * Returns a new object of class '<em>String Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>String Cell</em>'.
	 * @generated
	 */
	StringCell createStringCell();

	/**
	 * Returns a new object of class '<em>Long Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Long Cell</em>'.
	 * @generated
	 */
	LongCell createLongCell();

	/**
	 * Returns a new object of class '<em>Double Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Double Cell</em>'.
	 * @generated
	 */
	DoubleCell createDoubleCell();

	/**
	 * Returns a new object of class '<em>Big Decimal Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Big Decimal Cell</em>'.
	 * @generated
	 */
	BigDecimalCell createBigDecimalCell();

	/**
	 * Returns a new object of class '<em>Boolean Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Boolean Cell</em>'.
	 * @generated
	 */
	BooleanCell createBooleanCell();

	/**
	 * Returns a new object of class '<em>Date Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Date Cell</em>'.
	 * @generated
	 */
	DateCell createDateCell();

	/**
	 * Returns a new object of class '<em>Binary Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Binary Cell</em>'.
	 * @generated
	 */
	BinaryCell createBinaryCell();

	/**
	 * Returns a new object of class '<em>Fk Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Fk Cell</em>'.
	 * @generated
	 */
	FkCell createFkCell();

	/**
	 * Returns a new object of class '<em>Empty Cell</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Empty Cell</em>'.
	 * @generated
	 */
	EmptyCell createEmptyCell();

	/**
	 * Returns a new object of class '<em>Join Table</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Join Table</em>'.
	 * @generated
	 */
	JoinTable createJoinTable();

	/**
	 * Returns a new object of class '<em>Join Table Row</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Join Table Row</em>'.
	 * @generated
	 */
	JoinTableRow createJoinTableRow();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	TabularPackage getTabularPackage();

} //TabularFactory
