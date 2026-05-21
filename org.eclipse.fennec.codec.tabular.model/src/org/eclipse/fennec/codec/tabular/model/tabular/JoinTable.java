/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Join Table</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getOwnerEClass <em>Owner EClass</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getFileName <em>File Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getOwnerCol <em>Owner Col</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getTargetCol <em>Target Col</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getRows <em>Rows</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getJoinTable()
 * @model
 * @generated
 */
@ProviderType
public interface JoinTable extends EObject {
	/**
	 * Returns the value of the '<em><b>Owner EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Owner EClass</em>' reference.
	 * @see #setOwnerEClass(EClass)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getJoinTable_OwnerEClass()
	 * @model
	 * @generated
	 */
	EClass getOwnerEClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getOwnerEClass <em>Owner EClass</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Owner EClass</em>' reference.
	 * @see #getOwnerEClass()
	 * @generated
	 */
	void setOwnerEClass(EClass value);

	/**
	 * Returns the value of the '<em><b>Ref</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref</em>' reference.
	 * @see #setRef(EReference)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getJoinTable_Ref()
	 * @model
	 * @generated
	 */
	EReference getRef();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getRef <em>Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' reference.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(EReference value);

	/**
	 * Returns the value of the '<em><b>File Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>File Name</em>' attribute.
	 * @see #setFileName(String)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getJoinTable_FileName()
	 * @model
	 * @generated
	 */
	String getFileName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getFileName <em>File Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>File Name</em>' attribute.
	 * @see #getFileName()
	 * @generated
	 */
	void setFileName(String value);

	/**
	 * Returns the value of the '<em><b>Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Schema</em>' attribute.
	 * @see #setSchema(String)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getJoinTable_Schema()
	 * @model
	 * @generated
	 */
	String getSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getSchema <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Schema</em>' attribute.
	 * @see #getSchema()
	 * @generated
	 */
	void setSchema(String value);

	/**
	 * Returns the value of the '<em><b>Owner Col</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Owner Col</em>' attribute.
	 * @see #setOwnerCol(String)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getJoinTable_OwnerCol()
	 * @model
	 * @generated
	 */
	String getOwnerCol();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getOwnerCol <em>Owner Col</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Owner Col</em>' attribute.
	 * @see #getOwnerCol()
	 * @generated
	 */
	void setOwnerCol(String value);

	/**
	 * Returns the value of the '<em><b>Target Col</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Col</em>' attribute.
	 * @see #setTargetCol(String)
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getJoinTable_TargetCol()
	 * @model
	 * @generated
	 */
	String getTargetCol();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTable#getTargetCol <em>Target Col</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Col</em>' attribute.
	 * @see #getTargetCol()
	 * @generated
	 */
	void setTargetCol(String value);

	/**
	 * Returns the value of the '<em><b>Rows</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rows</em>' containment reference list.
	 * @see org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage#getJoinTable_Rows()
	 * @model containment="true"
	 * @generated
	 */
	EList<JoinTableRow> getRows();

} // JoinTable
