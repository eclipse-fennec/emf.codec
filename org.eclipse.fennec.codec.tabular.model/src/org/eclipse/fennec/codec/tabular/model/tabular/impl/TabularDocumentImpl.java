/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.fennec.codec.tabular.model.tabular.JoinTable;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.codec.tabular.model.tabular.Table;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Document</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularDocumentImpl#getReferenceMode <em>Reference Mode</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularDocumentImpl#getTables <em>Tables</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.TabularDocumentImpl#getJoinTables <em>Join Tables</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TabularDocumentImpl extends MinimalEObjectImpl.Container implements TabularDocument {
	/**
	 * The default value of the '{@link #getReferenceMode() <em>Reference Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferenceMode()
	 * @generated
	 * @ordered
	 */
	protected static final ReferenceMode REFERENCE_MODE_EDEFAULT = ReferenceMode.IGNORE;

	/**
	 * The cached value of the '{@link #getReferenceMode() <em>Reference Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferenceMode()
	 * @generated
	 * @ordered
	 */
	protected ReferenceMode referenceMode = REFERENCE_MODE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTables() <em>Tables</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTables()
	 * @generated
	 * @ordered
	 */
	protected EList<Table> tables;

	/**
	 * The cached value of the '{@link #getJoinTables() <em>Join Tables</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getJoinTables()
	 * @generated
	 * @ordered
	 */
	protected EList<JoinTable> joinTables;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TabularDocumentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TabularPackage.Literals.TABULAR_DOCUMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ReferenceMode getReferenceMode() {
		return referenceMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setReferenceMode(ReferenceMode newReferenceMode) {
		ReferenceMode oldReferenceMode = referenceMode;
		referenceMode = newReferenceMode == null ? REFERENCE_MODE_EDEFAULT : newReferenceMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.TABULAR_DOCUMENT__REFERENCE_MODE, oldReferenceMode, referenceMode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Table> getTables() {
		if (tables == null) {
			tables = new EObjectContainmentEList<Table>(Table.class, this, TabularPackage.TABULAR_DOCUMENT__TABLES);
		}
		return tables;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<JoinTable> getJoinTables() {
		if (joinTables == null) {
			joinTables = new EObjectContainmentEList<JoinTable>(JoinTable.class, this, TabularPackage.TABULAR_DOCUMENT__JOIN_TABLES);
		}
		return joinTables;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case TabularPackage.TABULAR_DOCUMENT__TABLES:
				return ((InternalEList<?>)getTables()).basicRemove(otherEnd, msgs);
			case TabularPackage.TABULAR_DOCUMENT__JOIN_TABLES:
				return ((InternalEList<?>)getJoinTables()).basicRemove(otherEnd, msgs);
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
			case TabularPackage.TABULAR_DOCUMENT__REFERENCE_MODE:
				return getReferenceMode();
			case TabularPackage.TABULAR_DOCUMENT__TABLES:
				return getTables();
			case TabularPackage.TABULAR_DOCUMENT__JOIN_TABLES:
				return getJoinTables();
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
			case TabularPackage.TABULAR_DOCUMENT__REFERENCE_MODE:
				setReferenceMode((ReferenceMode)newValue);
				return;
			case TabularPackage.TABULAR_DOCUMENT__TABLES:
				getTables().clear();
				getTables().addAll((Collection<? extends Table>)newValue);
				return;
			case TabularPackage.TABULAR_DOCUMENT__JOIN_TABLES:
				getJoinTables().clear();
				getJoinTables().addAll((Collection<? extends JoinTable>)newValue);
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
			case TabularPackage.TABULAR_DOCUMENT__REFERENCE_MODE:
				setReferenceMode(REFERENCE_MODE_EDEFAULT);
				return;
			case TabularPackage.TABULAR_DOCUMENT__TABLES:
				getTables().clear();
				return;
			case TabularPackage.TABULAR_DOCUMENT__JOIN_TABLES:
				getJoinTables().clear();
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
			case TabularPackage.TABULAR_DOCUMENT__REFERENCE_MODE:
				return referenceMode != REFERENCE_MODE_EDEFAULT;
			case TabularPackage.TABULAR_DOCUMENT__TABLES:
				return tables != null && !tables.isEmpty();
			case TabularPackage.TABULAR_DOCUMENT__JOIN_TABLES:
				return joinTables != null && !joinTables.isEmpty();
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
		result.append(" (referenceMode: ");
		result.append(referenceMode);
		result.append(')');
		return result.toString();
	}

} //TabularDocumentImpl
