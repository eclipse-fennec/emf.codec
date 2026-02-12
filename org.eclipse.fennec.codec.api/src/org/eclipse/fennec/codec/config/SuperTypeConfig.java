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
import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getEnum;
import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getString;

import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.SuperTypeSelection;

/**
 * Immutable supertype serialization configuration.
 * <p>
 * This class supports the cascading merge pattern where each configuration layer
 * can override values from the previous layer.
 * <p>
 * Note: In STRUCTURED format, supertype inherits {@code schemaKey} from TypeConfig.
 * <p>
 * Note: The {@code superTypeKey} default is format-dependent:
 * <ul>
 *   <li>PLAIN format: {@code "_supertype"} (underscore prefix for root-level metadata)</li>
 *   <li>STRUCTURED format: {@code "supertype"} (no underscore inside _type object)</li>
 * </ul>
 * When {@code superTypeKey} is null, the effective config resolver should apply the
 * format-dependent default based on the resolved {@code superTypeFormat}.
 *
 * @see ConfigProperty
 * @see ConfigMergeHelper
 */
public final class SuperTypeConfig implements Mergeable<SuperTypeConfig> {

    /** Default superTypeKey for PLAIN format */
    public static final String DEFAULT_KEY_PLAIN = "_supertype";

    /** Default superTypeKey for STRUCTURED format */
    public static final String DEFAULT_KEY_STRUCTURED = "supertype";

    private final boolean serialize;
    private final SuperTypeSelection strategy;
    private final SerializationFormat format;
    private final boolean asArray;
    private final String separator;
    private final String superTypeKey;
    private final String valueReaderName;
    private final String valueWriterName;

    private SuperTypeConfig(Builder builder) {
        this.serialize = builder.serialize;
        this.strategy = builder.strategy;
        this.format = builder.format;
        this.asArray = builder.asArray;
        this.separator = builder.separator;
        this.superTypeKey = builder.superTypeKey;
        this.valueReaderName = builder.valueReaderName;
        this.valueWriterName = builder.valueWriterName;
    }

    // ========================================================================
    // Accessors
    // ========================================================================

    /**
     * Returns whether supertype serialization is enabled.
     * Default: false
     */
    public boolean isSerialize() {
        return serialize;
    }

    /**
     * Returns which supertypes to include (ALL, ALL_EMF, SINGLE, NONE).
     * Default: ALL
     */
    public SuperTypeSelection getStrategy() {
        return strategy;
    }

    /**
     * Returns the serialization format (PLAIN, STRUCTURED).
     * If null, inherits from typeFormat.
     */
    public SerializationFormat getFormat() {
        return format;
    }

    /**
     * Returns whether to serialize supertypes as JSON array (true) or
     * as separator-joined string (false).
     * Default: true
     */
    public boolean isAsArray() {
        return asArray;
    }

    /**
     * Returns the separator character for STRING presentation (when asArray=false).
     * Default: ","
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Returns the JSON property key for supertype information.
     * <p>
     * The default value is format-dependent:
     * <ul>
     *   <li>PLAIN format: {@code "_supertype"}</li>
     *   <li>STRUCTURED format: {@code "supertype"}</li>
     * </ul>
     * Returns null if not explicitly set (effective config resolver applies format-dependent default).
     *
     * @see #DEFAULT_KEY_PLAIN
     * @see #DEFAULT_KEY_STRUCTURED
     */
    public String getSuperTypeKey() {
        return superTypeKey;
    }

    /**
     * Returns the effective superTypeKey based on the given format.
     * <p>
     * If superTypeKey is explicitly set, returns that value.
     * Otherwise returns the format-dependent default.
     *
     * @param effectiveFormat the resolved format (PLAIN or STRUCTURED)
     * @return the effective superTypeKey
     */
    public String getEffectiveSuperTypeKey(SerializationFormat effectiveFormat) {
        if (superTypeKey != null) {
            return superTypeKey;
        }
        return effectiveFormat == SerializationFormat.STRUCTURED
                ? DEFAULT_KEY_STRUCTURED
                : DEFAULT_KEY_PLAIN;
    }

    // Note: schemaKey is not in SuperTypeConfig - codec uses TypeConfig.getSchemaKey() directly

    /**
     * Returns the custom value reader name, or null if none.
     */
    public String getValueReaderName() {
        return valueReaderName;
    }

    /**
     * Returns the custom value writer name, or null if none.
     */
    public String getValueWriterName() {
        return valueWriterName;
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
    public SuperTypeConfig mergeWith(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return this;
        }

        return toBuilder()
                .serialize(getBoolean(source, ConfigProperty.SUPERTYPE_SERIALIZE, this.serialize))
                .strategy(getEnum(source, ConfigProperty.SUPERTYPE_STRATEGY, SuperTypeSelection.class, this.strategy))
                .format(getEnum(source, ConfigProperty.SUPERTYPE_FORMAT, SerializationFormat.class, this.format))
                .asArray(getBoolean(source, ConfigProperty.SUPERTYPE_AS_ARRAY, this.asArray))
                .separator(getString(source, ConfigProperty.SUPERTYPE_SEPARATOR, this.separator))
                .superTypeKey(getString(source, ConfigProperty.SUPERTYPE_KEY, this.superTypeKey))
                .valueReaderName(getString(source, ConfigProperty.SUPERTYPE_VALUE_READER_NAME, this.valueReaderName))
                .valueWriterName(getString(source, ConfigProperty.SUPERTYPE_VALUE_WRITER_NAME, this.valueWriterName))
                .build();
    }

    // ========================================================================
    // Validation support
    // ========================================================================

    /**
     * Validates this config and returns a validated (possibly normalized) config.
     * <p>
     * SuperType config constraints:
     * <ul>
     *   <li>separator is only meaningful when asArray=false</li>
     * </ul>
     * <p>
     * Note: The constraint "STRUCTURED format + typeStrategy=NONE + superTypeSerialize=true is invalid"
     * must be validated at a higher level where TypeConfig is also available.
     */
    @Override
    public SuperTypeConfig validate(DiagnosticCollector diagnostics) {
        // Constraint: separator only meaningful when asArray=false
        if (asArray && separator != null
                && !separator.equals(ConfigProperty.SUPERTYPE_SEPARATOR.getDefaultValue())) {
            diagnostics.addWarning(
                "superTypeSeparator is ignored when superTypeAsArray=true",
                "SuperTypeConfig.validate");
        }

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
                .serialize(this.serialize)
                .strategy(this.strategy)
                .format(this.format)
                .asArray(this.asArray)
                .separator(this.separator)
                .superTypeKey(this.superTypeKey)
                .valueReaderName(this.valueReaderName)
                .valueWriterName(this.valueWriterName);
    }

    /**
     * Creates a config with all default values from {@link ConfigProperty}.
     */
    public static SuperTypeConfig defaults() {
        return builder().build();
    }

    public static final class Builder {
        private boolean serialize = ConfigProperty.SUPERTYPE_SERIALIZE.getDefaultValue();
        private SuperTypeSelection strategy = SuperTypeSelection.valueOf(ConfigProperty.SUPERTYPE_STRATEGY.getDefaultValue());
        private SerializationFormat format = null;  // null means inherit from typeFormat
        private boolean asArray = ConfigProperty.SUPERTYPE_AS_ARRAY.getDefaultValue();
        private String separator = ConfigProperty.SUPERTYPE_SEPARATOR.getDefaultValue();
        private String superTypeKey = null;  // null means format-dependent default (see getEffectiveSuperTypeKey)
        // Note: schemaKey not here - codec uses TypeConfig.getSchemaKey() directly
        private String valueReaderName = ConfigProperty.SUPERTYPE_VALUE_READER_NAME.getDefaultValue();
        private String valueWriterName = ConfigProperty.SUPERTYPE_VALUE_WRITER_NAME.getDefaultValue();

        private Builder() {}

        public Builder serialize(boolean serialize) {
            this.serialize = serialize;
            return this;
        }

        public Builder strategy(SuperTypeSelection strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder format(SerializationFormat format) {
            this.format = format;
            return this;
        }

        public Builder asArray(boolean asArray) {
            this.asArray = asArray;
            return this;
        }

        public Builder separator(String separator) {
            this.separator = separator;
            return this;
        }

        public Builder superTypeKey(String superTypeKey) {
            this.superTypeKey = superTypeKey;
            return this;
        }

        public Builder valueReaderName(String valueReaderName) {
            this.valueReaderName = valueReaderName;
            return this;
        }

        public Builder valueWriterName(String valueWriterName) {
            this.valueWriterName = valueWriterName;
            return this;
        }

        public SuperTypeConfig build() {
            return new SuperTypeConfig(this);
        }
    }
}
