/*
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 * 
 */
package org.eclipse.fennec.codec.info.codecinfo.impl;

import java.lang.reflect.InvocationTargetException;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectEList;

import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Holder</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.CodecInfoHolderImpl#getInfoType <em>Info Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.CodecInfoHolderImpl#getReaders <em>Readers</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.info.codecinfo.impl.CodecInfoHolderImpl#getWriters <em>Writers</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CodecInfoHolderImpl extends MinimalEObjectImpl.Container implements CodecInfoHolder {
	/**
	 * The default value of the '{@link #getInfoType() <em>Info Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInfoType()
	 * @generated
	 * @ordered
	 */
	protected static final InfoType INFO_TYPE_EDEFAULT = InfoType.IDENTITY;

	/**
	 * The cached value of the '{@link #getInfoType() <em>Info Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInfoType()
	 * @generated
	 * @ordered
	 */
	protected InfoType infoType = INFO_TYPE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getReaders() <em>Readers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReaders()
	 * @generated
	 * @ordered
	 */
	protected EList<CodecValueReader> readers;

	/**
	 * The cached value of the '{@link #getWriters() <em>Writers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWriters()
	 * @generated
	 * @ordered
	 */
	protected EList<CodecValueWriter> writers;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CodecInfoHolderImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecInfoPackage.Literals.CODEC_INFO_HOLDER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public InfoType getInfoType() {
		return infoType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInfoType(InfoType newInfoType) {
		InfoType oldInfoType = infoType;
		infoType = newInfoType == null ? INFO_TYPE_EDEFAULT : newInfoType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodecInfoPackage.CODEC_INFO_HOLDER__INFO_TYPE, oldInfoType, infoType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<CodecValueReader> getReaders() {
		if (readers == null) {
			readers = new EObjectEList<CodecValueReader>(CodecValueReader.class, this, CodecInfoPackage.CODEC_INFO_HOLDER__READERS);
		}
		return readers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<CodecValueWriter> getWriters() {
		if (writers == null) {
			writers = new EObjectEList<CodecValueWriter>(CodecValueWriter.class, this, CodecInfoPackage.CODEC_INFO_HOLDER__WRITERS);
		}
		return writers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CodecValueReader getReaderByName(final String readerName) {
		return getReaders().stream().filter(r -> r.getName() == readerName).findFirst().orElse(null);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CodecValueWriter getWriterByName(final String writerName) {
		return getWriters().stream().filter(w -> w.getName() == writerName).findFirst().orElse(null);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodecInfoPackage.CODEC_INFO_HOLDER__INFO_TYPE:
				return getInfoType();
			case CodecInfoPackage.CODEC_INFO_HOLDER__READERS:
				return getReaders();
			case CodecInfoPackage.CODEC_INFO_HOLDER__WRITERS:
				return getWriters();
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
			case CodecInfoPackage.CODEC_INFO_HOLDER__INFO_TYPE:
				setInfoType((InfoType)newValue);
				return;
			case CodecInfoPackage.CODEC_INFO_HOLDER__READERS:
				getReaders().clear();
				getReaders().addAll((Collection<? extends CodecValueReader>)newValue);
				return;
			case CodecInfoPackage.CODEC_INFO_HOLDER__WRITERS:
				getWriters().clear();
				getWriters().addAll((Collection<? extends CodecValueWriter>)newValue);
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
			case CodecInfoPackage.CODEC_INFO_HOLDER__INFO_TYPE:
				setInfoType(INFO_TYPE_EDEFAULT);
				return;
			case CodecInfoPackage.CODEC_INFO_HOLDER__READERS:
				getReaders().clear();
				return;
			case CodecInfoPackage.CODEC_INFO_HOLDER__WRITERS:
				getWriters().clear();
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
			case CodecInfoPackage.CODEC_INFO_HOLDER__INFO_TYPE:
				return infoType != INFO_TYPE_EDEFAULT;
			case CodecInfoPackage.CODEC_INFO_HOLDER__READERS:
				return readers != null && !readers.isEmpty();
			case CodecInfoPackage.CODEC_INFO_HOLDER__WRITERS:
				return writers != null && !writers.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case CodecInfoPackage.CODEC_INFO_HOLDER___GET_READER_BY_NAME__STRING:
				return getReaderByName((String)arguments.get(0));
			case CodecInfoPackage.CODEC_INFO_HOLDER___GET_WRITER_BY_NAME__STRING:
				return getWriterByName((String)arguments.get(0));
		}
		return super.eInvoke(operationID, arguments);
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
		result.append(" (infoType: ");
		result.append(infoType);
		result.append(')');
		return result.toString();
	}

} //CodecInfoHolderImpl
