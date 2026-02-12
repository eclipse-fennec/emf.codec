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
 * Configuration sources in order of priority (highest first).
 * <p>
 * When resolving a property, higher priority sources override lower ones.
 * The principle is: dynamic overrides static, runtime overrides model.
 *
 * @see ConfigProperty
 * @see ConfigurationResolver
 */
public enum ConfigSource {

    /**
     * Load/save options passed to resource.load() or resource.save().
     * Highest priority - per-operation runtime configuration.
     */
    OPTIONS(1),

    /**
     * Resource-level configuration.
     * Set when creating or configuring a specific resource instance.
     */
    RESOURCE(2),

    /**
     * ResourceFactory-level configuration.
     * Factory-wide defaults for all resources created by this factory.
     */
    RESOURCE_FACTORY(3),

    /**
     * Jackson Module configuration.
     * Codec-wide defaults set on the CodecModule.
     */
    MODULE(4),

    /**
     * EAnnotation on the Ecore model.
     * Static configuration defined in the .ecore file.
     */
    ANNOTATION(5),

    /**
     * Built-in defaults.
     * Lowest priority - hardcoded fallback values in the codec.
     */
    DEFAULT(6);

    private final int priority;

    ConfigSource(int priority) {
        this.priority = priority;
    }

    /**
     * Returns the priority (lower number = higher priority).
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns sources in priority order (highest priority first).
     * Used for resolution: iterate in this order to find the first configured value.
     */
    public static ConfigSource[] priorityOrder() {
        return new ConfigSource[] { OPTIONS, RESOURCE, RESOURCE_FACTORY, MODULE, ANNOTATION, DEFAULT };
    }
}
