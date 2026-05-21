/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Document</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getReferenceMode <em>Reference Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getTables <em>Tables</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getJoinTables <em>Join Tables</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getTabularDocument()
 * @model
 * @generated
 */
@ProviderType
public interface TabularDocument extends EObject {
	/**
	 * Returns the value of the '<em><b>Reference Mode</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reference Mode</em>' attribute.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode
	 * @see #setReferenceMode(ReferenceMode)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getTabularDocument_ReferenceMode()
	 * @model
	 * @generated
	 */
	ReferenceMode getReferenceMode();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument#getReferenceMode <em>Reference Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reference Mode</em>' attribute.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode
	 * @see #getReferenceMode()
	 * @generated
	 */
	void setReferenceMode(ReferenceMode value);

	/**
	 * Returns the value of the '<em><b>Tables</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.tabular.model.tabular.Table}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tables</em>' containment reference list.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getTabularDocument_Tables()
	 * @model containment="true"
	 * @generated
	 */
	EList<Table> getTables();

	/**
	 * Returns the value of the '<em><b>Join Tables</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Join Tables</em>' containment reference list.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getTabularDocument_JoinTables()
	 * @model containment="true"
	 * @generated
	 */
	EList<JoinTable> getJoinTables();

} // TabularDocument
