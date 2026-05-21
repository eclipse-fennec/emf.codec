/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.codec.tabular.model.tabular.FkCell;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Fk Cell</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.FkCellImpl#getTargetId <em>Target Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.FkCellImpl#getTargetEClass <em>Target EClass</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FkCellImpl extends CellImpl implements FkCell {
	/**
	 * The default value of the '{@link #getTargetId() <em>Target Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetId()
	 * @generated
	 * @ordered
	 */
	protected static final long TARGET_ID_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getTargetId() <em>Target Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetId()
	 * @generated
	 * @ordered
	 */
	protected long targetId = TARGET_ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTargetEClass() <em>Target EClass</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetEClass()
	 * @generated
	 * @ordered
	 */
	protected EClass targetEClass;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FkCellImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TabularPackage.Literals.FK_CELL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getTargetId() {
		return targetId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTargetId(long newTargetId) {
		long oldTargetId = targetId;
		targetId = newTargetId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.FK_CELL__TARGET_ID, oldTargetId, targetId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTargetEClass() {
		if (targetEClass != null && targetEClass.eIsProxy()) {
			InternalEObject oldTargetEClass = (InternalEObject)targetEClass;
			targetEClass = (EClass)eResolveProxy(oldTargetEClass);
			if (targetEClass != oldTargetEClass) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TabularPackage.FK_CELL__TARGET_ECLASS, oldTargetEClass, targetEClass));
			}
		}
		return targetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass basicGetTargetEClass() {
		return targetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTargetEClass(EClass newTargetEClass) {
		EClass oldTargetEClass = targetEClass;
		targetEClass = newTargetEClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.FK_CELL__TARGET_ECLASS, oldTargetEClass, targetEClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TabularPackage.FK_CELL__TARGET_ID:
				return getTargetId();
			case TabularPackage.FK_CELL__TARGET_ECLASS:
				if (resolve) return getTargetEClass();
				return basicGetTargetEClass();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case TabularPackage.FK_CELL__TARGET_ID:
				setTargetId((Long)newValue);
				return;
			case TabularPackage.FK_CELL__TARGET_ECLASS:
				setTargetEClass((EClass)newValue);
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
			case TabularPackage.FK_CELL__TARGET_ID:
				setTargetId(TARGET_ID_EDEFAULT);
				return;
			case TabularPackage.FK_CELL__TARGET_ECLASS:
				setTargetEClass((EClass)null);
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
			case TabularPackage.FK_CELL__TARGET_ID:
				return targetId != TARGET_ID_EDEFAULT;
			case TabularPackage.FK_CELL__TARGET_ECLASS:
				return targetEClass != null;
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
		result.append(" (targetId: ");
		result.append(targetId);
		result.append(')');
		return result.toString();
	}

} //FkCellImpl
