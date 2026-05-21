/*
 */
package org.eclipse.fennec.codec.tabular.model.tabular.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Join Table Row</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableRowImpl#getOwnerId <em>Owner Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableRowImpl#getTargetId <em>Target Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.tabular.model.tabular.impl.JoinTableRowImpl#getTargetEClass <em>Target EClass</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JoinTableRowImpl extends MinimalEObjectImpl.Container implements JoinTableRow {
	/**
	 * The default value of the '{@link #getOwnerId() <em>Owner Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOwnerId()
	 * @generated
	 * @ordered
	 */
	protected static final long OWNER_ID_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getOwnerId() <em>Owner Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOwnerId()
	 * @generated
	 * @ordered
	 */
	protected long ownerId = OWNER_ID_EDEFAULT;

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
	protected JoinTableRowImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TabularPackage.Literals.JOIN_TABLE_ROW;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getOwnerId() {
		return ownerId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOwnerId(long newOwnerId) {
		long oldOwnerId = ownerId;
		ownerId = newOwnerId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.JOIN_TABLE_ROW__OWNER_ID, oldOwnerId, ownerId));
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
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.JOIN_TABLE_ROW__TARGET_ID, oldTargetId, targetId));
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, TabularPackage.JOIN_TABLE_ROW__TARGET_ECLASS, oldTargetEClass, targetEClass));
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
			eNotify(new ENotificationImpl(this, Notification.SET, TabularPackage.JOIN_TABLE_ROW__TARGET_ECLASS, oldTargetEClass, targetEClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TabularPackage.JOIN_TABLE_ROW__OWNER_ID:
				return getOwnerId();
			case TabularPackage.JOIN_TABLE_ROW__TARGET_ID:
				return getTargetId();
			case TabularPackage.JOIN_TABLE_ROW__TARGET_ECLASS:
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
			case TabularPackage.JOIN_TABLE_ROW__OWNER_ID:
				setOwnerId((Long)newValue);
				return;
			case TabularPackage.JOIN_TABLE_ROW__TARGET_ID:
				setTargetId((Long)newValue);
				return;
			case TabularPackage.JOIN_TABLE_ROW__TARGET_ECLASS:
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
			case TabularPackage.JOIN_TABLE_ROW__OWNER_ID:
				setOwnerId(OWNER_ID_EDEFAULT);
				return;
			case TabularPackage.JOIN_TABLE_ROW__TARGET_ID:
				setTargetId(TARGET_ID_EDEFAULT);
				return;
			case TabularPackage.JOIN_TABLE_ROW__TARGET_ECLASS:
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
			case TabularPackage.JOIN_TABLE_ROW__OWNER_ID:
				return ownerId != OWNER_ID_EDEFAULT;
			case TabularPackage.JOIN_TABLE_ROW__TARGET_ID:
				return targetId != TARGET_ID_EDEFAULT;
			case TabularPackage.JOIN_TABLE_ROW__TARGET_ECLASS:
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
		result.append(" (ownerId: ");
		result.append(ownerId);
		result.append(", targetId: ");
		result.append(targetId);
		result.append(')');
		return result.toString();
	}

} //JoinTableRowImpl
