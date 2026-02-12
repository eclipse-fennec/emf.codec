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
package org.eclipse.fennec.codec.value;

import org.eclipse.fennec.codec.config.DiscriminatorConfig;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;

/**
 * The effective codec configuration after merging all sources.
 * <p>
 * This interface provides access to the fully-resolved configuration that
 * results from merging:
 * <ol>
 *   <li>EAnnotations on the model</li>
 *   <li>Factory defaults</li>
 *   <li>Resource options</li>
 *   <li>Load/Save options (highest priority)</li>
 * </ol>
 * <p>
 * Custom value readers and writers receive this configuration via their
 * context objects ({@link CodecReaderContext}, {@link CodecWriterContext}).
 * <p>
 * Note: Feature-specific and reference-specific configuration is not available
 * through this interface because readers/writers already receive the feature
 * as a parameter to their {@code read()}/{@code write()} methods. Global
 * settings like type strategy, ID configuration, and smart compression are
 * available.
 *
 * @see CodecReaderContext#getConfig()
 * @see CodecWriterContext#getConfig()
 */
public interface EffectiveCodecConfig {

    /**
     * Returns the effective type configuration for global settings.
     * <p>
     * Note: This returns the global type configuration. For feature-specific
     * type configuration, the reader/writer should use the feature parameter
     * passed to {@code read()}/{@code write()}.
     *
     * @return the global type configuration, never null
     */
    TypeConfig getTypeConfig();

    /**
     * Returns the effective supertype configuration.
     *
     * @return the supertype configuration, never null
     */
    SuperTypeConfig getSuperTypeConfig();

    /**
     * Returns the effective ID configuration.
     *
     * @return the ID configuration, never null
     */
    IdConfig getIdConfig();

    /**
     * Returns the effective discriminator configuration.
     *
     * @return the discriminator configuration, never null
     */
    DiscriminatorConfig getDiscriminatorConfig();

    /**
     * Returns whether smart compression is enabled.
     *
     * @return true if smart compression is enabled
     */
    boolean isSmartCompressionEnabled();
}
