/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Row</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.Row#getCells <em>Cells</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getRow()
 * @model
 * @generated
 */
@ProviderType
public interface Row extends EObject {
	/**
	 * Returns the value of the '<em><b>Cells</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.tabular.model.tabular.Cell}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cells</em>' containment reference list.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getRow_Cells()
	 * @model containment="true"
	 * @generated
	 */
	EList<Cell> getCells();

} // Row
