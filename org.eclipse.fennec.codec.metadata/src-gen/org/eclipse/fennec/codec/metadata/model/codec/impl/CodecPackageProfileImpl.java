/*
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.metadata.model.codec.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile;

import org.eclipse.fennec.model.metadata.impl.PackageProfileImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package Profile</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class CodecPackageProfileImpl extends PackageProfileImpl implements CodecPackageProfile {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CodecPackageProfileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodecPackage.Literals.CODEC_PACKAGE_PROFILE;
	}

} //CodecPackageProfileImpl
