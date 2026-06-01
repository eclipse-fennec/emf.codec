/**
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
package org.eclipse.fennec.codec.tabular.model.tabular.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.codec.tabular.model.tabular.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TabularFactoryImpl extends EFactoryImpl implements TabularFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static TabularFactory init() {
		try {
			TabularFactory theTabularFactory = (TabularFactory)EPackage.Registry.INSTANCE.getEFactory(TabularPackage.eNS_URI);
			if (theTabularFactory != null) {
				return theTabularFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new TabularFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TabularFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case TabularPackage.TABULAR_DOCUMENT: return createTabularDocument();
			case TabularPackage.TABLE: return createTable();
			case TabularPackage.COLUMN: return createColumn();
			case TabularPackage.ROW: return createRow();
			case TabularPackage.STRING_CELL: return createStringCell();
			case TabularPackage.LONG_CELL: return createLongCell();
			case TabularPackage.DOUBLE_CELL: return createDoubleCell();
			case TabularPackage.BIG_DECIMAL_CELL: return createBigDecimalCell();
			case TabularPackage.BOOLEAN_CELL: return createBooleanCell();
			case TabularPackage.DATE_CELL: return createDateCell();
			case TabularPackage.BINARY_CELL: return createBinaryCell();
			case TabularPackage.FK_CELL: return createFkCell();
			case TabularPackage.EMPTY_CELL: return createEmptyCell();
			case TabularPackage.JOIN_TABLE: return createJoinTable();
			case TabularPackage.JOIN_TABLE_ROW: return createJoinTableRow();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case TabularPackage.REFERENCE_MODE:
				return createReferenceModeFromString(eDataType, initialValue);
			case TabularPackage.MULTI_VALUED_REF_STRATEGY:
				return createMultiValuedRefStrategyFromString(eDataType, initialValue);
			case TabularPackage.COLUMN_SOURCE:
				return createColumnSourceFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case TabularPackage.REFERENCE_MODE:
				return convertReferenceModeToString(eDataType, instanceValue);
			case TabularPackage.MULTI_VALUED_REF_STRATEGY:
				return convertMultiValuedRefStrategyToString(eDataType, instanceValue);
			case TabularPackage.COLUMN_SOURCE:
				return convertColumnSourceToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TabularDocument createTabularDocument() {
		TabularDocumentImpl tabularDocument = new TabularDocumentImpl();
		return tabularDocument;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Table createTable() {
		TableImpl table = new TableImpl();
		return table;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Column createColumn() {
		ColumnImpl column = new ColumnImpl();
		return column;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Row createRow() {
		RowImpl row = new RowImpl();
		return row;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public StringCell createStringCell() {
		StringCellImpl stringCell = new StringCellImpl();
		return stringCell;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LongCell createLongCell() {
		LongCellImpl longCell = new LongCellImpl();
		return longCell;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DoubleCell createDoubleCell() {
		DoubleCellImpl doubleCell = new DoubleCellImpl();
		return doubleCell;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BigDecimalCell createBigDecimalCell() {
		BigDecimalCellImpl bigDecimalCell = new BigDecimalCellImpl();
		return bigDecimalCell;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BooleanCell createBooleanCell() {
		BooleanCellImpl booleanCell = new BooleanCellImpl();
		return booleanCell;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DateCell createDateCell() {
		DateCellImpl dateCell = new DateCellImpl();
		return dateCell;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BinaryCell createBinaryCell() {
		BinaryCellImpl binaryCell = new BinaryCellImpl();
		return binaryCell;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FkCell createFkCell() {
		FkCellImpl fkCell = new FkCellImpl();
		return fkCell;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EmptyCell createEmptyCell() {
		EmptyCellImpl emptyCell = new EmptyCellImpl();
		return emptyCell;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public JoinTable createJoinTable() {
		JoinTableImpl joinTable = new JoinTableImpl();
		return joinTable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public JoinTableRow createJoinTableRow() {
		JoinTableRowImpl joinTableRow = new JoinTableRowImpl();
		return joinTableRow;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferenceMode createReferenceModeFromString(EDataType eDataType, String initialValue) {
		ReferenceMode result = ReferenceMode.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertReferenceModeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MultiValuedRefStrategy createMultiValuedRefStrategyFromString(EDataType eDataType, String initialValue) {
		MultiValuedRefStrategy result = MultiValuedRefStrategy.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertMultiValuedRefStrategyToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ColumnSource createColumnSourceFromString(EDataType eDataType, String initialValue) {
		ColumnSource result = ColumnSource.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertColumnSourceToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TabularPackage getTabularPackage() {
		return (TabularPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static TabularPackage getPackage() {
		return TabularPackage.eINSTANCE;
	}

} //TabularFactoryImpl
