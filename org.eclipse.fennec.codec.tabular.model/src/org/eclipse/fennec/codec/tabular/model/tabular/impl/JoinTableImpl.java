/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.tabular.model.tabular.JoinTable;
import org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Join Table</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl#getOwnerEClass <em>Owner EClass</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl#getFileName <em>File Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl#getOwnerCol <em>Owner Col</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl#getTargetCol <em>Target Col</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableImpl#getRows <em>Rows</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JoinTableImpl extends MinimalEObjectImpl.Container implements JoinTable {
	/**
	 * The cached value of the '{@link #getOwnerEClass() <em>Owner EClass</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOwnerEClass()
	 * @generated
	 * @ordered
	 */
	protected EClass ownerEClass;

	/**
	 * The cached value of the '{@link #getRef() <em>Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRef()
	 * @generated
	 * @ordered
	 */
	protected EReference ref;

	/**
	 * The default value of the '{@link #getFileName() <em>File Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFileName()
	 * @generated
	 * @ordered
	 */
	protected static final String FILE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFileName() <em>File Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFileName()
	 * @generated
	 * @ordered
	 */
	protected String fileName = FILE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getSchema() <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected static final String SCHEMA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSchema() <em>Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchema()
	 * @generated
	 * @ordered
	 */
	protected String schema = SCHEMA_EDEFAULT;

	/**
	 * The default value of the '{@link #getOwnerCol() <em>Owner Col</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOwnerCol()
	 * @generated
	 * @ordered
	 */
	protected static final String OWNER_COL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOwnerCol() <em>Owner Col</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOwnerCol()
	 * @generated
	 * @ordered
	 */
	protected String ownerCol = OWNER_COL_EDEFAULT;

	/**
	 * The default value of the '{@link #getTargetCol() <em>Target Col</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetCol()
	 * @generated
	 * @ordered
	 */
	protected static final String TARGET_COL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTargetCol() <em>Target Col</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetCol()
	 * @generated
	 * @ordered
	 */
	protected String targetCol = TARGET_COL_EDEFAULT;

	/**
	 * The cached value of the '{@link #getRows() <em>Rows</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRows()
	 * @generated
	 * @ordered
	 */
	protected EList<JoinTableRow> rows;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected JoinTableImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TabularPackage.Literals.JOIN_TABLE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOwnerEClass() {
		if (ownerEClass != null && ownerEClass.eIsProxy()) {
			InternalEObject oldOwnerEClass = (InternalEObject)ownerEClass;
			ownerEClass = (EClass)eResolveProxy(oldOwnerEClass);
			if (ownerEClass != oldOwnerEClass) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TabularPackage.JOIN_TABLE__OWNER_ECLASS, oldOwnerEClass, ownerEClass));
			}
		}
		return ownerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass basicGetOwnerEClass() {
		return ownerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOwnerEClass(EClass newOwnerEClass) {
		EClass oldOwnerEClass = ownerEClass;
		ownerEClass = newOwnerEClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.JOIN_TABLE__OWNER_ECLASS, oldOwnerEClass, ownerEClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRef() {
		if (ref != null && ref.eIsProxy()) {
			InternalEObject oldRef = (InternalEObject)ref;
			ref = (EReference)eResolveProxy(oldRef);
			if (ref != oldRef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TabularPackage.JOIN_TABLE__REF, oldRef, ref));
			}
		}
		return ref;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference basicGetRef() {
		return ref;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRef(EReference newRef) {
		EReference oldRef = ref;
		ref = newRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.JOIN_TABLE__REF, oldRef, ref));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getFileName() {
		return fileName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFileName(String newFileName) {
		String oldFileName = fileName;
		fileName = newFileName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.JOIN_TABLE__FILE_NAME, oldFileName, fileName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getSchema() {
		return schema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSchema(String newSchema) {
		String oldSchema = schema;
		schema = newSchema;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.JOIN_TABLE__SCHEMA, oldSchema, schema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getOwnerCol() {
		return ownerCol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOwnerCol(String newOwnerCol) {
		String oldOwnerCol = ownerCol;
		ownerCol = newOwnerCol;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.JOIN_TABLE__OWNER_COL, oldOwnerCol, ownerCol));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTargetCol() {
		return targetCol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTargetCol(String newTargetCol) {
		String oldTargetCol = targetCol;
		targetCol = newTargetCol;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.JOIN_TABLE__TARGET_COL, oldTargetCol, targetCol));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<JoinTableRow> getRows() {
		if (rows == null) {
			rows = new EObjectContainmentEList<JoinTableRow>(JoinTableRow.class, this, TabularPackage.JOIN_TABLE__ROWS);
		}
		return rows;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case TabularPackage.JOIN_TABLE__ROWS:
				return ((InternalEList<?>)getRows()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TabularPackage.JOIN_TABLE__OWNER_ECLASS:
				if (resolve) return getOwnerEClass();
				return basicGetOwnerEClass();
			case TabularPackage.JOIN_TABLE__REF:
				if (resolve) return getRef();
				return basicGetRef();
			case TabularPackage.JOIN_TABLE__FILE_NAME:
				return getFileName();
			case TabularPackage.JOIN_TABLE__SCHEMA:
				return getSchema();
			case TabularPackage.JOIN_TABLE__OWNER_COL:
				return getOwnerCol();
			case TabularPackage.JOIN_TABLE__TARGET_COL:
				return getTargetCol();
			case TabularPackage.JOIN_TABLE__ROWS:
				return getRows();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case TabularPackage.JOIN_TABLE__OWNER_ECLASS:
				setOwnerEClass((EClass)newValue);
				return;
			case TabularPackage.JOIN_TABLE__REF:
				setRef((EReference)newValue);
				return;
			case TabularPackage.JOIN_TABLE__FILE_NAME:
				setFileName((String)newValue);
				return;
			case TabularPackage.JOIN_TABLE__SCHEMA:
				setSchema((String)newValue);
				return;
			case TabularPackage.JOIN_TABLE__OWNER_COL:
				setOwnerCol((String)newValue);
				return;
			case TabularPackage.JOIN_TABLE__TARGET_COL:
				setTargetCol((String)newValue);
				return;
			case TabularPackage.JOIN_TABLE__ROWS:
				getRows().clear();
				getRows().addAll((Collection<? extends JoinTableRow>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case TabularPackage.JOIN_TABLE__OWNER_ECLASS:
				setOwnerEClass((EClass)null);
				return;
			case TabularPackage.JOIN_TABLE__REF:
				setRef((EReference)null);
				return;
			case TabularPackage.JOIN_TABLE__FILE_NAME:
				setFileName(FILE_NAME_EDEFAULT);
				return;
			case TabularPackage.JOIN_TABLE__SCHEMA:
				setSchema(SCHEMA_EDEFAULT);
				return;
			case TabularPackage.JOIN_TABLE__OWNER_COL:
				setOwnerCol(OWNER_COL_EDEFAULT);
				return;
			case TabularPackage.JOIN_TABLE__TARGET_COL:
				setTargetCol(TARGET_COL_EDEFAULT);
				return;
			case TabularPackage.JOIN_TABLE__ROWS:
				getRows().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case TabularPackage.JOIN_TABLE__OWNER_ECLASS:
				return ownerEClass != null;
			case TabularPackage.JOIN_TABLE__REF:
				return ref != null;
			case TabularPackage.JOIN_TABLE__FILE_NAME:
				return FILE_NAME_EDEFAULT == null ? fileName != null : !FILE_NAME_EDEFAULT.equals(fileName);
			case TabularPackage.JOIN_TABLE__SCHEMA:
				return SCHEMA_EDEFAULT == null ? schema != null : !SCHEMA_EDEFAULT.equals(schema);
			case TabularPackage.JOIN_TABLE__OWNER_COL:
				return OWNER_COL_EDEFAULT == null ? ownerCol != null : !OWNER_COL_EDEFAULT.equals(ownerCol);
			case TabularPackage.JOIN_TABLE__TARGET_COL:
				return TARGET_COL_EDEFAULT == null ? targetCol != null : !TARGET_COL_EDEFAULT.equals(targetCol);
			case TabularPackage.JOIN_TABLE__ROWS:
				return rows != null && !rows.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (fileName: ");
		result.append(fileName);
		result.append(", schema: ");
		result.append(schema);
		result.append(", ownerCol: ");
		result.append(ownerCol);
		result.append(", targetCol: ");
		result.append(targetCol);
		result.append(')');
		return result.toString();
	}

} //JoinTableImpl
