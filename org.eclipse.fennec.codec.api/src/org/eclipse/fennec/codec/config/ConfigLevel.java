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
package org.eclipse.fennec.codec.config;

/**
 * Configuration scope levels in order of specificity (most specific first).
 * <p>
 * When resolving a property, more specific levels take precedence over less specific ones.
 * Not all properties are valid at all levels - see {@link ConfigProperty#getValidLevels()}.
 *
 * @see ConfigProperty
 * @see ConfigurationResolver
 */
public enum ConfigLevel {

    /**
     * Feature-level configuration (EAttribute or EReference).
     * Most specific - applies to a single structural feature.
     */
    FEATURE,

    /**
     * Class-level configuration (EClass).
     * Applies to all instances of the class and its features (unless overridden).
     */
    ECLASS,

    /**
     * Global configuration.
     * Applies to all classes and features (unless overridden at more specific levels).
     */
    GLOBAL;

    /**
     * Returns levels in specificity order (most specific first).
     * Used for resolution: iterate in this order to find the most specific configured value.
     */
    public static ConfigLevel[] specificityOrder() {
        return new ConfigLevel[] { FEATURE, ECLASS, GLOBAL };
    }
}
