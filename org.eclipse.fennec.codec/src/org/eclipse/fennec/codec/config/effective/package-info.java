/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
/**
 * Effective codec configuration bridge.
 * <p>
 * This package provides the {@link org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig}
 * class that bridges the new API config system ({@link org.eclipse.fennec.codec.config.ConfigurationResolver})
 * with the codec context system. It is the single entry point for serializers and deserializers
 * to access resolved configuration.
 * </p>
 *
 * @see org.eclipse.fennec.codec.config.ConfigurationResolver
 * @see org.eclipse.fennec.codec.config.TypeConfig
 * @see org.eclipse.fennec.codec.config.IdConfig
 * @see org.eclipse.fennec.codec.config.FeatureConfig
 */
@org.osgi.annotation.bundle.Export
@org.osgi.annotation.versioning.Version("1.0.0")
package org.eclipse.fennec.codec.config.effective;
