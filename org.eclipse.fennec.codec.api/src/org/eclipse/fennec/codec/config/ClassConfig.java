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

import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getBoolean;

import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;

/**
 * Immutable class-level codec configuration for strictness handling.
 * <p>
 * This class controls how the deserializer handles unknown JSON fields and
 * missing required EMF features. It supports the cascading merge pattern where
 * each configuration layer can override values from the previous layer.
 * <p>
 * Strictness properties apply at Global and EClass level only, not on features.
 * <p>
 * <b>Behavior:</b>
 * <ul>
 *   <li>{@code strictOnUnknown=true}: ERROR on unknown JSON field</li>
 *   <li>{@code strictOnUnknown=false}: WARNING + skip unknown field (default)</li>
 *   <li>{@code strictOnMissing=true}: ERROR on missing required feature</li>
 *   <li>{@code strictOnMissing=false}: WARNING + use default value (default)</li>
 * </ul>
 *
 * @see ConfigProperty#STRICT_ON_UNKNOWN
 * @see ConfigProperty#STRICT_ON_MISSING
 * @see ConfigMergeHelper
 */
public final class ClassConfig implements Mergeable<ClassConfig> {

    private final boolean strictOnUnknown;
    private final boolean strictOnMissing;
    private final boolean strictOnConversion;

    private ClassConfig(Builder builder) {
        this.strictOnUnknown = builder.strictOnUnknown;
        this.strictOnMissing = builder.strictOnMissing;
        this.strictOnConversion = builder.strictOnConversion;
    }

    // ========================================================================
    // Accessors
    // ========================================================================

    /**
     * Returns whether to throw an error on unknown JSON fields.
     * <p>
     * When {@code true}, any JSON field that doesn't correspond to an EMF feature
     * in the target EClass will cause deserialization to fail with an error.
     * <p>
     * When {@code false} (default), unknown fields generate a warning and are skipped.
     *
     * @return {@code true} if unknown fields should cause an error
     */
    public boolean isStrictOnUnknown() {
        return strictOnUnknown;
    }

    /**
     * Returns whether to throw an error on missing required features.
     * <p>
     * When {@code true}, any required EMF feature (lowerBound &gt;= 1) that is not
     * present in the JSON will cause deserialization to fail with an error.
     * <p>
     * When {@code false} (default), missing required features generate a warning
     * and use the EMF default value.
     *
     * @return {@code true} if missing required features should cause an error
     */
    public boolean isStrictOnMissing() {
        return strictOnMissing;
    }

    /**
     * Returns whether a value that cannot be converted should fail the load.
     * <p>
     * When {@code false} (default), a failed conversion is reported as a diagnostic and the
     * feature keeps its default value - which is indistinguishable from the value simply
     * being absent. A caller who would rather fail than silently store a default can set
     * this to {@code true} (issue #131).
     *
     * @return {@code true} if a failed value conversion should cause an error
     */
    public boolean isStrictOnConversion() {
        return strictOnConversion;
    }

    // ========================================================================
    // Merge support
    // ========================================================================

    /**
     * Creates a new config by merging this config with values from a property map.
     *
     * @param source the property map to merge (may be null or empty)
     * @return a new config with merged values, or this if source is null/empty
     */
    @Override
    public ClassConfig mergeWith(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return this;
        }

        return toBuilder()
                .strictOnUnknown(getBoolean(source, ConfigProperty.STRICT_ON_UNKNOWN, this.strictOnUnknown))
                .strictOnMissing(getBoolean(source, ConfigProperty.STRICT_ON_MISSING, this.strictOnMissing))
                .strictOnConversion(getBoolean(source, ConfigProperty.STRICT_ON_CONVERSION, this.strictOnConversion))
                .build();
    }

    // ========================================================================
    // Validation support
    // ========================================================================

    /**
     * Validates this config and returns a validated (possibly normalized) config.
     * <p>
     * ClassConfig has no validation constraints - both strictness flags can be
     * independently enabled or disabled.
     */
    @Override
    public ClassConfig validate(DiagnosticCollector diagnostics) {
        // No validation constraints for ClassConfig
        return this;
    }

    // ========================================================================
    // Builder support
    // ========================================================================

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .strictOnUnknown(this.strictOnUnknown)
                .strictOnMissing(this.strictOnMissing)
                .strictOnConversion(this.strictOnConversion);
    }

    /**
     * Creates a config with all default values from {@link ConfigProperty}.
     */
    public static ClassConfig defaults() {
        return builder().build();
    }

    public static final class Builder {
        private boolean strictOnUnknown = ConfigProperty.STRICT_ON_UNKNOWN.getDefaultValue();
        private boolean strictOnMissing = ConfigProperty.STRICT_ON_MISSING.getDefaultValue();
        private boolean strictOnConversion = ConfigProperty.STRICT_ON_CONVERSION.getDefaultValue();

        private Builder() {}

        public Builder strictOnUnknown(boolean strictOnUnknown) {
            this.strictOnUnknown = strictOnUnknown;
            return this;
        }

        /**
         * Sets whether a failed value conversion fails the load.
         *
         * @param strictOnConversion true to fail instead of keeping the default value
         * @return this builder
         */
        public Builder strictOnConversion(boolean strictOnConversion) {
            this.strictOnConversion = strictOnConversion;
            return this;
        }

        public Builder strictOnMissing(boolean strictOnMissing) {
            this.strictOnMissing = strictOnMissing;
            return this;
        }

        public ClassConfig build() {
            return new ClassConfig(this);
        }
    }

    @Override
    public String toString() {
        return "ClassConfig[strictOnUnknown=" + strictOnUnknown
                + ", strictOnMissing=" + strictOnMissing
                + ", strictOnConversion=" + strictOnConversion + "]";
    }
}
