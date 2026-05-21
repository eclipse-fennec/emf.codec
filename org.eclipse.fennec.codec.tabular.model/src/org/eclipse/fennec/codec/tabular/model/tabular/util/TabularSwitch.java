/**
 */
package org.eclipse.fennec.codec.tabular.model.tabular.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.fennec.codec.tabular.model.tabular.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage
 * @generated
 */
public class TabularSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static TabularPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TabularSwitch() {
		if (modelPackage == null) {
			modelPackage = TabularPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case TabularPackage.TABULAR_DOCUMENT: {
				TabularDocument tabularDocument = (TabularDocument)theEObject;
				T result = caseTabularDocument(tabularDocument);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.TABLE: {
				Table table = (Table)theEObject;
				T result = caseTable(table);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.COLUMN: {
				Column column = (Column)theEObject;
				T result = caseColumn(column);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.ROW: {
				Row row = (Row)theEObject;
				T result = caseRow(row);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.CELL: {
				Cell cell = (Cell)theEObject;
				T result = caseCell(cell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.STRING_CELL: {
				StringCell stringCell = (StringCell)theEObject;
				T result = caseStringCell(stringCell);
				if (result == null) result = caseCell(stringCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.LONG_CELL: {
				LongCell longCell = (LongCell)theEObject;
				T result = caseLongCell(longCell);
				if (result == null) result = caseCell(longCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.DOUBLE_CELL: {
				DoubleCell doubleCell = (DoubleCell)theEObject;
				T result = caseDoubleCell(doubleCell);
				if (result == null) result = caseCell(doubleCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.BIG_DECIMAL_CELL: {
				BigDecimalCell bigDecimalCell = (BigDecimalCell)theEObject;
				T result = caseBigDecimalCell(bigDecimalCell);
				if (result == null) result = caseCell(bigDecimalCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.BOOLEAN_CELL: {
				BooleanCell booleanCell = (BooleanCell)theEObject;
				T result = caseBooleanCell(booleanCell);
				if (result == null) result = caseCell(booleanCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.DATE_CELL: {
				DateCell dateCell = (DateCell)theEObject;
				T result = caseDateCell(dateCell);
				if (result == null) result = caseCell(dateCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.BINARY_CELL: {
				BinaryCell binaryCell = (BinaryCell)theEObject;
				T result = caseBinaryCell(binaryCell);
				if (result == null) result = caseCell(binaryCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.FK_CELL: {
				FkCell fkCell = (FkCell)theEObject;
				T result = caseFkCell(fkCell);
				if (result == null) result = caseCell(fkCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.EMPTY_CELL: {
				EmptyCell emptyCell = (EmptyCell)theEObject;
				T result = caseEmptyCell(emptyCell);
				if (result == null) result = caseCell(emptyCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.JOIN_TABLE: {
				JoinTable joinTable = (JoinTable)theEObject;
				T result = caseJoinTable(joinTable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case TabularPackage.JOIN_TABLE_ROW: {
				JoinTableRow joinTableRow = (JoinTableRow)theEObject;
				T result = caseJoinTableRow(joinTableRow);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Document</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Document</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTabularDocument(TabularDocument object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Table</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Table</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTable(Table object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Column</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Column</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseColumn(Column object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Row</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Row</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRow(Row object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCell(Cell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringCell(StringCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Long Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Long Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLongCell(LongCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Double Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Double Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDoubleCell(DoubleCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Big Decimal Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Big Decimal Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBigDecimalCell(BigDecimalCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Boolean Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Boolean Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBooleanCell(BooleanCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Date Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Date Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDateCell(DateCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Binary Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Binary Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBinaryCell(BinaryCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Fk Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Fk Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFkCell(FkCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Empty Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Empty Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEmptyCell(EmptyCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Join Table</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Join Table</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseJoinTable(JoinTable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Join Table Row</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Join Table Row</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseJoinTableRow(JoinTableRow object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //TabularSwitch
