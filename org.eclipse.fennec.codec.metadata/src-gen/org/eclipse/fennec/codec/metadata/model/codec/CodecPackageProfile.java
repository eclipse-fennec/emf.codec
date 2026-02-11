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
package org.eclipse.fennec.codec.metadata.model.codec;

import org.eclipse.fennec.model.metadata.PackageProfile;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Package Profile</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Codec-specific package profile containing pre-computed, fully resolved annotation-layer configurations for all classes in a package. Built by CodecAspectProvider.buildProfiles() after all metadata and aspects are constructed. Contains one CodecClassProfile per EClass with annotation-internal inheritance already applied (feature inherits from class, class inherits from package defaults). At runtime, serves as the static base that dynamic configuration (options, resource, factory, module) merges on top of.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getCodecPackageProfile()
 * @model
 * @generated
 */
@ProviderType
public interface CodecPackageProfile extends PackageProfile {
} // CodecPackageProfile
