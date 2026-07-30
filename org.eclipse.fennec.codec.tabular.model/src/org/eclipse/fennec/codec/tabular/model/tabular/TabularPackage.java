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
package org.eclipse.fennec.codec.tabular.model.tabular;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.fennec.emf.osgi.annotation.provide.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularFactory
 * @model kind="package"
 *        annotation="Version value='1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = TabularPackage.eNS_URI, fingerprint = "fp1:43c688e1ba621e79d37e587a6d9f960e7b8f94fb2f8807f6d443f13eb758907d", genModel = "/model/tabular.genmodel", genModelSourceLocations = {"model/tabular.genmodel","org.eclipse.fennec.codec.tabular.model/model/tabular.genmodel"}, ecore = "/model/tabular.ecore", ecoreSourceLocations = "/model/tabular.ecore")
public interface TabularPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "tabular";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/tabular/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "tabular";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TabularPackage eINSTANCE = org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularDocumentImpl <em>Document</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularDocumentImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getTabularDocument()
	 * @generated
	 */
	int TABULAR_DOCUMENT = 0;

	/**
	 * The feature id for the '<em><b>Reference Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABULAR_DOCUMENT__REFERENCE_MODE = 0;

	/**
	 * The feature id for the '<em><b>Tables</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABULAR_DOCUMENT__TABLES = 1;

	/**
	 * The feature id for the '<em><b>Join Tables</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABULAR_DOCUMENT__JOIN_TABLES = 2;

	/**
	 * The number of structural features of the '<em>Document</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABULAR_DOCUMENT_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Document</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABULAR_DOCUMENT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.TableImpl <em>Table</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TableImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getTable()
	 * @generated
	 */
	int TABLE = 1;

	/**
	 * The feature id for the '<em><b>EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__ECLASS = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__NAME = 1;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__SCHEMA = 2;

	/**
	 * The feature id for the '<em><b>Columns</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__COLUMNS = 3;

	/**
	 * The feature id for the '<em><b>Rows</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE__ROWS = 4;

	/**
	 * The number of structural features of the '<em>Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.ColumnImpl <em>Column</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.ColumnImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getColumn()
	 * @generated
	 */
	int COLUMN = 2;

	/**
	 * The feature id for the '<em><b>Header</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__HEADER = 0;

	/**
	 * The feature id for the '<em><b>Sql Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__SQL_TYPE = 1;

	/**
	 * The feature id for the '<em><b>Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__SOURCE = 2;

	/**
	 * The feature id for the '<em><b>Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN__FEATURE = 3;

	/**
	 * The number of structural features of the '<em>Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Column</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLUMN_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.RowImpl <em>Row</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.RowImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getRow()
	 * @generated
	 */
	int ROW = 3;

	/**
	 * The feature id for the '<em><b>Cells</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROW__CELLS = 0;

	/**
	 * The number of structural features of the '<em>Row</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROW_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Row</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ROW_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.CellImpl <em>Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.CellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getCell()
	 * @generated
	 */
	int CELL = 4;

	/**
	 * The number of structural features of the '<em>Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CELL_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CELL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.StringCellImpl <em>String Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.StringCellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getStringCell()
	 * @generated
	 */
	int STRING_CELL = 5;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_CELL__VALUE = CELL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>String Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>String Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.LongCellImpl <em>Long Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.LongCellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getLongCell()
	 * @generated
	 */
	int LONG_CELL = 6;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LONG_CELL__VALUE = CELL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Long Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LONG_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Long Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LONG_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.DoubleCellImpl <em>Double Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.DoubleCellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getDoubleCell()
	 * @generated
	 */
	int DOUBLE_CELL = 7;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOUBLE_CELL__VALUE = CELL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Double Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOUBLE_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Double Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOUBLE_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.BigDecimalCellImpl <em>Big Decimal Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.BigDecimalCellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getBigDecimalCell()
	 * @generated
	 */
	int BIG_DECIMAL_CELL = 8;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BIG_DECIMAL_CELL__VALUE = CELL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Big Decimal Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BIG_DECIMAL_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Big Decimal Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BIG_DECIMAL_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.BooleanCellImpl <em>Boolean Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.BooleanCellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getBooleanCell()
	 * @generated
	 */
	int BOOLEAN_CELL = 9;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_CELL__VALUE = CELL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Boolean Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Boolean Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.DateCellImpl <em>Date Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.DateCellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getDateCell()
	 * @generated
	 */
	int DATE_CELL = 10;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_CELL__VALUE = CELL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Date Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_CELL__DATE_FORMAT = CELL_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Date Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Date Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.BinaryCellImpl <em>Binary Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.BinaryCellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getBinaryCell()
	 * @generated
	 */
	int BINARY_CELL = 11;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINARY_CELL__VALUE = CELL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Binary Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINARY_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Binary Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINARY_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.FkCellImpl <em>Fk Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.FkCellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getFkCell()
	 * @generated
	 */
	int FK_CELL = 12;

	/**
	 * The feature id for the '<em><b>Target Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FK_CELL__TARGET_ID = CELL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FK_CELL__TARGET_ECLASS = CELL_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Fk Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FK_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Fk Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FK_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.EmptyCellImpl <em>Empty Cell</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.EmptyCellImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getEmptyCell()
	 * @generated
	 */
	int EMPTY_CELL = 13;

	/**
	 * The number of structural features of the '<em>Empty Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMPTY_CELL_FEATURE_COUNT = CELL_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Empty Cell</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMPTY_CELL_OPERATION_COUNT = CELL_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl <em>Join Table</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getJoinTable()
	 * @generated
	 */
	int JOIN_TABLE = 14;

	/**
	 * The feature id for the '<em><b>Owner EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__OWNER_ECLASS = 0;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__REF = 1;

	/**
	 * The feature id for the '<em><b>File Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__FILE_NAME = 2;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__SCHEMA = 3;

	/**
	 * The feature id for the '<em><b>Owner Col</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__OWNER_COL = 4;

	/**
	 * The feature id for the '<em><b>Target Col</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__TARGET_COL = 5;

	/**
	 * The feature id for the '<em><b>Rows</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE__ROWS = 6;

	/**
	 * The number of structural features of the '<em>Join Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Join Table</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableRowImpl <em>Join Table Row</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableRowImpl
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getJoinTableRow()
	 * @generated
	 */
	int JOIN_TABLE_ROW = 15;

	/**
	 * The feature id for the '<em><b>Owner Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE_ROW__OWNER_ID = 0;

	/**
	 * The feature id for the '<em><b>Target Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE_ROW__TARGET_ID = 1;

	/**
	 * The feature id for the '<em><b>Target EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE_ROW__TARGET_ECLASS = 2;

	/**
	 * The number of structural features of the '<em>Join Table Row</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE_ROW_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Join Table Row</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JOIN_TABLE_ROW_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode <em>Reference Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getReferenceMode()
	 * @generated
	 */
	int REFERENCE_MODE = 16;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.MultiValuedRefStrategy <em>Multi Valued Ref Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.MultiValuedRefStrategy
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getMultiValuedRefStrategy()
	 * @generated
	 */
	int MULTI_VALUED_REF_STRATEGY = 17;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource <em>Column Source</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getColumnSource()
	 * @generated
	 */
	int COLUMN_SOURCE = 18;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument <em>Document</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Document</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument
	 * @generated
	 */
	EClass getTabularDocument();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getReferenceMode <em>Reference Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Reference Mode</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getReferenceMode()
	 * @see #getTabularDocument()
	 * @generated
	 */
	EAttribute getTabularDocument_ReferenceMode();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getTables <em>Tables</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Tables</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getTables()
	 * @see #getTabularDocument()
	 * @generated
	 */
	EReference getTabularDocument_Tables();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getJoinTables <em>Join Tables</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Join Tables</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getJoinTables()
	 * @see #getTabularDocument()
	 * @generated
	 */
	EReference getTabularDocument_JoinTables();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.Table <em>Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Table</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Table
	 * @generated
	 */
	EClass getTable();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.codec.tabular.model.tabular.Table#getEClass <em>EClass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>EClass</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Table#getEClass()
	 * @see #getTable()
	 * @generated
	 */
	EReference getTable_EClass();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.Table#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Table#getName()
	 * @see #getTable()
	 * @generated
	 */
	EAttribute getTable_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.Table#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Table#getSchema()
	 * @see #getTable()
	 * @generated
	 */
	EAttribute getTable_Schema();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.tabular.model.tabular.Table#getColumns <em>Columns</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Columns</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Table#getColumns()
	 * @see #getTable()
	 * @generated
	 */
	EReference getTable_Columns();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.tabular.model.tabular.Table#getRows <em>Rows</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Rows</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Table#getRows()
	 * @see #getTable()
	 * @generated
	 */
	EReference getTable_Rows();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.Column <em>Column</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Column</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Column
	 * @generated
	 */
	EClass getColumn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getHeader <em>Header</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Header</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Column#getHeader()
	 * @see #getColumn()
	 * @generated
	 */
	EAttribute getColumn_Header();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getSqlType <em>Sql Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sql Type</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Column#getSqlType()
	 * @see #getColumn()
	 * @generated
	 */
	EAttribute getColumn_SqlType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Source</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Column#getSource()
	 * @see #getColumn()
	 * @generated
	 */
	EAttribute getColumn_Source();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.codec.tabular.model.tabular.Column#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Feature</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Column#getFeature()
	 * @see #getColumn()
	 * @generated
	 */
	EReference getColumn_Feature();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.Row <em>Row</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Row</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Row
	 * @generated
	 */
	EClass getRow();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.tabular.model.tabular.Row#getCells <em>Cells</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Cells</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Row#getCells()
	 * @see #getRow()
	 * @generated
	 */
	EReference getRow_Cells();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.Cell <em>Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.Cell
	 * @generated
	 */
	EClass getCell();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.StringCell <em>String Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.StringCell
	 * @generated
	 */
	EClass getStringCell();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.StringCell#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.StringCell#getValue()
	 * @see #getStringCell()
	 * @generated
	 */
	EAttribute getStringCell_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.LongCell <em>Long Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Long Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.LongCell
	 * @generated
	 */
	EClass getLongCell();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.LongCell#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.LongCell#getValue()
	 * @see #getLongCell()
	 * @generated
	 */
	EAttribute getLongCell_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.DoubleCell <em>Double Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Double Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.DoubleCell
	 * @generated
	 */
	EClass getDoubleCell();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.DoubleCell#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.DoubleCell#getValue()
	 * @see #getDoubleCell()
	 * @generated
	 */
	EAttribute getDoubleCell_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell <em>Big Decimal Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Big Decimal Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell
	 * @generated
	 */
	EClass getBigDecimalCell();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell#getValue()
	 * @see #getBigDecimalCell()
	 * @generated
	 */
	EAttribute getBigDecimalCell_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell <em>Boolean Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Boolean Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell
	 * @generated
	 */
	EClass getBooleanCell();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell#isValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell#isValue()
	 * @see #getBooleanCell()
	 * @generated
	 */
	EAttribute getBooleanCell_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.DateCell <em>Date Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Date Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.DateCell
	 * @generated
	 */
	EClass getDateCell();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.DateCell#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.DateCell#getValue()
	 * @see #getDateCell()
	 * @generated
	 */
	EAttribute getDateCell_Value();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.DateCell#getDateFormat <em>Date Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Date Format</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.DateCell#getDateFormat()
	 * @see #getDateCell()
	 * @generated
	 */
	EAttribute getDateCell_DateFormat();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell <em>Binary Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Binary Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell
	 * @generated
	 */
	EClass getBinaryCell();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell#getValue()
	 * @see #getBinaryCell()
	 * @generated
	 */
	EAttribute getBinaryCell_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.FkCell <em>Fk Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Fk Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.FkCell
	 * @generated
	 */
	EClass getFkCell();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.FkCell#getTargetId <em>Target Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Id</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.FkCell#getTargetId()
	 * @see #getFkCell()
	 * @generated
	 */
	EAttribute getFkCell_TargetId();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.codec.tabular.model.tabular.FkCell#getTargetEClass <em>Target EClass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target EClass</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.FkCell#getTargetEClass()
	 * @see #getFkCell()
	 * @generated
	 */
	EReference getFkCell_TargetEClass();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.EmptyCell <em>Empty Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Empty Cell</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.EmptyCell
	 * @generated
	 */
	EClass getEmptyCell();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable <em>Join Table</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Join Table</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTable
	 * @generated
	 */
	EClass getJoinTable();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getOwnerEClass <em>Owner EClass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Owner EClass</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getOwnerEClass()
	 * @see #getJoinTable()
	 * @generated
	 */
	EReference getJoinTable_OwnerEClass();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Ref</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getRef()
	 * @see #getJoinTable()
	 * @generated
	 */
	EReference getJoinTable_Ref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getFileName <em>File Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File Name</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getFileName()
	 * @see #getJoinTable()
	 * @generated
	 */
	EAttribute getJoinTable_FileName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Schema</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getSchema()
	 * @see #getJoinTable()
	 * @generated
	 */
	EAttribute getJoinTable_Schema();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getOwnerCol <em>Owner Col</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Owner Col</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getOwnerCol()
	 * @see #getJoinTable()
	 * @generated
	 */
	EAttribute getJoinTable_OwnerCol();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getTargetCol <em>Target Col</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Col</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getTargetCol()
	 * @see #getJoinTable()
	 * @generated
	 */
	EAttribute getJoinTable_TargetCol();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getRows <em>Rows</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Rows</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getRows()
	 * @see #getJoinTable()
	 * @generated
	 */
	EReference getJoinTable_Rows();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow <em>Join Table Row</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Join Table Row</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow
	 * @generated
	 */
	EClass getJoinTableRow();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow#getOwnerId <em>Owner Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Owner Id</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow#getOwnerId()
	 * @see #getJoinTableRow()
	 * @generated
	 */
	EAttribute getJoinTableRow_OwnerId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow#getTargetId <em>Target Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Id</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow#getTargetId()
	 * @see #getJoinTableRow()
	 * @generated
	 */
	EAttribute getJoinTableRow_TargetId();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow#getTargetEClass <em>Target EClass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target EClass</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow#getTargetEClass()
	 * @see #getJoinTableRow()
	 * @generated
	 */
	EReference getJoinTableRow_TargetEClass();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode <em>Reference Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Reference Mode</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode
	 * @generated
	 */
	EEnum getReferenceMode();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.tabular.model.tabular.MultiValuedRefStrategy <em>Multi Valued Ref Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Multi Valued Ref Strategy</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.MultiValuedRefStrategy
	 * @generated
	 */
	EEnum getMultiValuedRefStrategy();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource <em>Column Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Column Source</em>'.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource
	 * @generated
	 */
	EEnum getColumnSource();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	TabularFactory getTabularFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularDocumentImpl <em>Document</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularDocumentImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getTabularDocument()
		 * @generated
		 */
		EClass TABULAR_DOCUMENT = eINSTANCE.getTabularDocument();

		/**
		 * The meta object literal for the '<em><b>Reference Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TABULAR_DOCUMENT__REFERENCE_MODE = eINSTANCE.getTabularDocument_ReferenceMode();

		/**
		 * The meta object literal for the '<em><b>Tables</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TABULAR_DOCUMENT__TABLES = eINSTANCE.getTabularDocument_Tables();

		/**
		 * The meta object literal for the '<em><b>Join Tables</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TABULAR_DOCUMENT__JOIN_TABLES = eINSTANCE.getTabularDocument_JoinTables();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.TableImpl <em>Table</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TableImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getTable()
		 * @generated
		 */
		EClass TABLE = eINSTANCE.getTable();

		/**
		 * The meta object literal for the '<em><b>EClass</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TABLE__ECLASS = eINSTANCE.getTable_EClass();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TABLE__NAME = eINSTANCE.getTable_Name();

		/**
		 * The meta object literal for the '<em><b>Schema</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TABLE__SCHEMA = eINSTANCE.getTable_Schema();

		/**
		 * The meta object literal for the '<em><b>Columns</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TABLE__COLUMNS = eINSTANCE.getTable_Columns();

		/**
		 * The meta object literal for the '<em><b>Rows</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TABLE__ROWS = eINSTANCE.getTable_Rows();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.ColumnImpl <em>Column</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.ColumnImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getColumn()
		 * @generated
		 */
		EClass COLUMN = eINSTANCE.getColumn();

		/**
		 * The meta object literal for the '<em><b>Header</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COLUMN__HEADER = eINSTANCE.getColumn_Header();

		/**
		 * The meta object literal for the '<em><b>Sql Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COLUMN__SQL_TYPE = eINSTANCE.getColumn_SqlType();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COLUMN__SOURCE = eINSTANCE.getColumn_Source();

		/**
		 * The meta object literal for the '<em><b>Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COLUMN__FEATURE = eINSTANCE.getColumn_Feature();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.RowImpl <em>Row</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.RowImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getRow()
		 * @generated
		 */
		EClass ROW = eINSTANCE.getRow();

		/**
		 * The meta object literal for the '<em><b>Cells</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ROW__CELLS = eINSTANCE.getRow_Cells();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.CellImpl <em>Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.CellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getCell()
		 * @generated
		 */
		EClass CELL = eINSTANCE.getCell();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.StringCellImpl <em>String Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.StringCellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getStringCell()
		 * @generated
		 */
		EClass STRING_CELL = eINSTANCE.getStringCell();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_CELL__VALUE = eINSTANCE.getStringCell_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.LongCellImpl <em>Long Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.LongCellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getLongCell()
		 * @generated
		 */
		EClass LONG_CELL = eINSTANCE.getLongCell();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LONG_CELL__VALUE = eINSTANCE.getLongCell_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.DoubleCellImpl <em>Double Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.DoubleCellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getDoubleCell()
		 * @generated
		 */
		EClass DOUBLE_CELL = eINSTANCE.getDoubleCell();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOUBLE_CELL__VALUE = eINSTANCE.getDoubleCell_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.BigDecimalCellImpl <em>Big Decimal Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.BigDecimalCellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getBigDecimalCell()
		 * @generated
		 */
		EClass BIG_DECIMAL_CELL = eINSTANCE.getBigDecimalCell();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BIG_DECIMAL_CELL__VALUE = eINSTANCE.getBigDecimalCell_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.BooleanCellImpl <em>Boolean Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.BooleanCellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getBooleanCell()
		 * @generated
		 */
		EClass BOOLEAN_CELL = eINSTANCE.getBooleanCell();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BOOLEAN_CELL__VALUE = eINSTANCE.getBooleanCell_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.DateCellImpl <em>Date Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.DateCellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getDateCell()
		 * @generated
		 */
		EClass DATE_CELL = eINSTANCE.getDateCell();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATE_CELL__VALUE = eINSTANCE.getDateCell_Value();

		/**
		 * The meta object literal for the '<em><b>Date Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATE_CELL__DATE_FORMAT = eINSTANCE.getDateCell_DateFormat();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.BinaryCellImpl <em>Binary Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.BinaryCellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getBinaryCell()
		 * @generated
		 */
		EClass BINARY_CELL = eINSTANCE.getBinaryCell();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BINARY_CELL__VALUE = eINSTANCE.getBinaryCell_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.FkCellImpl <em>Fk Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.FkCellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getFkCell()
		 * @generated
		 */
		EClass FK_CELL = eINSTANCE.getFkCell();

		/**
		 * The meta object literal for the '<em><b>Target Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FK_CELL__TARGET_ID = eINSTANCE.getFkCell_TargetId();

		/**
		 * The meta object literal for the '<em><b>Target EClass</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FK_CELL__TARGET_ECLASS = eINSTANCE.getFkCell_TargetEClass();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.EmptyCellImpl <em>Empty Cell</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.EmptyCellImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getEmptyCell()
		 * @generated
		 */
		EClass EMPTY_CELL = eINSTANCE.getEmptyCell();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl <em>Join Table</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getJoinTable()
		 * @generated
		 */
		EClass JOIN_TABLE = eINSTANCE.getJoinTable();

		/**
		 * The meta object literal for the '<em><b>Owner EClass</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference JOIN_TABLE__OWNER_ECLASS = eINSTANCE.getJoinTable_OwnerEClass();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference JOIN_TABLE__REF = eINSTANCE.getJoinTable_Ref();

		/**
		 * The meta object literal for the '<em><b>File Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JOIN_TABLE__FILE_NAME = eINSTANCE.getJoinTable_FileName();

		/**
		 * The meta object literal for the '<em><b>Schema</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JOIN_TABLE__SCHEMA = eINSTANCE.getJoinTable_Schema();

		/**
		 * The meta object literal for the '<em><b>Owner Col</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JOIN_TABLE__OWNER_COL = eINSTANCE.getJoinTable_OwnerCol();

		/**
		 * The meta object literal for the '<em><b>Target Col</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JOIN_TABLE__TARGET_COL = eINSTANCE.getJoinTable_TargetCol();

		/**
		 * The meta object literal for the '<em><b>Rows</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference JOIN_TABLE__ROWS = eINSTANCE.getJoinTable_Rows();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableRowImpl <em>Join Table Row</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableRowImpl
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getJoinTableRow()
		 * @generated
		 */
		EClass JOIN_TABLE_ROW = eINSTANCE.getJoinTableRow();

		/**
		 * The meta object literal for the '<em><b>Owner Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JOIN_TABLE_ROW__OWNER_ID = eINSTANCE.getJoinTableRow_OwnerId();

		/**
		 * The meta object literal for the '<em><b>Target Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JOIN_TABLE_ROW__TARGET_ID = eINSTANCE.getJoinTableRow_TargetId();

		/**
		 * The meta object literal for the '<em><b>Target EClass</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference JOIN_TABLE_ROW__TARGET_ECLASS = eINSTANCE.getJoinTableRow_TargetEClass();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode <em>Reference Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getReferenceMode()
		 * @generated
		 */
		EEnum REFERENCE_MODE = eINSTANCE.getReferenceMode();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.MultiValuedRefStrategy <em>Multi Valued Ref Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.MultiValuedRefStrategy
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getMultiValuedRefStrategy()
		 * @generated
		 */
		EEnum MULTI_VALUED_REF_STRATEGY = eINSTANCE.getMultiValuedRefStrategy();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource <em>Column Source</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource
		 * @see org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularPackageImpl#getColumnSource()
		 * @generated
		 */
		EEnum COLUMN_SOURCE = eINSTANCE.getColumnSource();

	}

} //TabularPackage
