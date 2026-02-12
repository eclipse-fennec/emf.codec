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
import org.eclipse.fennec.model.metadata.EnumSerializationStrategy;

/**
 * Immutable feature-level codec configuration.
 * <p>
 * This class supports the cascading merge pattern where each configuration layer
 * can override values from the previous layer.
 *
 * @see ConfigProperty
 * @see ConfigMergeHelper
 */
public final class FeatureConfig implements Mergeable<FeatureConfig> {

    private final String key;
    private final boolean ignore;
    private final boolean ignoreRead;
    private final boolean ignoreWrite;
    private final boolean forceRead;
    private final boolean forceWrite;
    private final boolean serializeNull;
    private final boolean serializeEmpty;
    private final boolean serializeDefault;
    private final EnumSerializationStrategy enumSerialization;
    private final String valueReaderName;
    private final String valueWriterName;

    private FeatureConfig(Builder builder) {
        this.key = builder.key;
        this.ignore = builder.ignore;
        this.ignoreRead = builder.ignoreRead;
        this.ignoreWrite = builder.ignoreWrite;
        this.forceRead = builder.forceRead;
        this.forceWrite = builder.forceWrite;
        this.serializeNull = builder.serializeNull;
        this.serializeEmpty = builder.serializeEmpty;
        this.serializeDefault = builder.serializeDefault;
        this.enumSerialization = builder.enumSerialization;
        this.valueReaderName = builder.valueReaderName;
        this.valueWriterName = builder.valueWriterName;
    }

    // ========================================================================
    // Accessors
    // ========================================================================

    /**
     * Returns the JSON property key for this feature.
     * If null, the feature name should be used.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns whether this feature is ignored for both read and write.
     * Default: false
     */
    public boolean isIgnore() {
        return ignore;
    }

    /**
     * Returns whether this feature is ignored for read (deserialization).
     * Default: false
     */
    public boolean isIgnoreRead() {
        return ignoreRead;
    }

    /**
     * Returns whether this feature is ignored for write (serialization).
     * Default: false
     */
    public boolean isIgnoreWrite() {
        return ignoreWrite;
    }

    /**
     * Returns whether to force read of this feature even if transient/derived.
     * Default: false
     */
    public boolean isForceRead() {
        return forceRead;
    }

    /**
     * Returns whether to force write of this feature even if transient/derived.
     * Default: false
     */
    public boolean isForceWrite() {
        return forceWrite;
    }

    /**
     * Returns whether null values should be serialized.
     * Default: false
     */
    public boolean isSerializeNull() {
        return serializeNull;
    }

    /**
     * Returns whether empty collections should be serialized.
     * Default: false
     */
    public boolean isSerializeEmpty() {
        return serializeEmpty;
    }

    /**
     * Returns whether default values should be serialized.
     * Default: false
     */
    public boolean isSerializeDefault() {
        return serializeDefault;
    }

    /**
     * Returns the enum serialization strategy.
     * Default: LITERAL
     */
    public EnumSerializationStrategy getEnumSerialization() {
        return enumSerialization;
    }

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
    // Computed properties
    // ========================================================================

    /**
     * Returns whether this feature should be serialized (written).
     * Takes into account ignore, ignoreWrite, and forceWrite flags.
     */
    public boolean shouldSerialize() {
        if (forceWrite) {
            return true;
        }
        return !ignore && !ignoreWrite;
    }

    /**
     * Returns whether this feature should be deserialized (read).
     * Takes into account ignore, ignoreRead, and forceRead flags.
     */
    public boolean shouldDeserialize() {
        if (forceRead) {
            return true;
        }
        return !ignore && !ignoreRead;
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
    public FeatureConfig mergeWith(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return this;
        }

        return toBuilder()
                .key(getString(source, ConfigProperty.KEY, this.key))
                .ignore(getBoolean(source, ConfigProperty.IGNORE, this.ignore))
                .ignoreRead(getBoolean(source, ConfigProperty.IGNORE_READ, this.ignoreRead))
                .ignoreWrite(getBoolean(source, ConfigProperty.IGNORE_WRITE, this.ignoreWrite))
                .forceRead(getBoolean(source, ConfigProperty.FORCE_READ, this.forceRead))
                .forceWrite(getBoolean(source, ConfigProperty.FORCE_WRITE, this.forceWrite))
                .serializeNull(getBoolean(source, ConfigProperty.SERIALIZE_NULL, this.serializeNull))
                .serializeEmpty(getBoolean(source, ConfigProperty.SERIALIZE_EMPTY, this.serializeEmpty))
                .serializeDefault(getBoolean(source, ConfigProperty.SERIALIZE_DEFAULT, this.serializeDefault))
                .enumSerialization(getEnum(source, ConfigProperty.ENUM_SERIALIZATION, EnumSerializationStrategy.class, this.enumSerialization))
                .valueReaderName(getString(source, ConfigProperty.VALUE_READER_NAME, this.valueReaderName))
                .valueWriterName(getString(source, ConfigProperty.VALUE_WRITER_NAME, this.valueWriterName))
                .build();
    }

    // ========================================================================
    // Validation support
    // ========================================================================

    /**
     * Validates this config and returns a validated (possibly normalized) config.
     * <p>
     * Feature config constraints:
     * <ul>
     *   <li>ignore=true with forceRead/forceWrite is contradictory</li>
     *   <li>ignoreRead=true with forceRead=true is contradictory</li>
     *   <li>ignoreWrite=true with forceWrite=true is contradictory</li>
     * </ul>
     */
    @Override
    public FeatureConfig validate(DiagnosticCollector diagnostics) {
        // Constraint: ignore with force is contradictory
        if (ignore && (forceRead || forceWrite)) {
            diagnostics.addWarning(
                "ignore=true combined with forceRead/forceWrite is contradictory; force takes precedence",
                "FeatureConfig.validate");
        }

        // Constraint: ignoreRead with forceRead is contradictory
        if (ignoreRead && forceRead) {
            diagnostics.addWarning(
                "ignoreRead=true combined with forceRead=true is contradictory; forceRead takes precedence",
                "FeatureConfig.validate");
        }

        // Constraint: ignoreWrite with forceWrite is contradictory
        if (ignoreWrite && forceWrite) {
            diagnostics.addWarning(
                "ignoreWrite=true combined with forceWrite=true is contradictory; forceWrite takes precedence",
                "FeatureConfig.validate");
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
                .key(this.key)
                .ignore(this.ignore)
                .ignoreRead(this.ignoreRead)
                .ignoreWrite(this.ignoreWrite)
                .forceRead(this.forceRead)
                .forceWrite(this.forceWrite)
                .serializeNull(this.serializeNull)
                .serializeEmpty(this.serializeEmpty)
                .serializeDefault(this.serializeDefault)
                .enumSerialization(this.enumSerialization)
                .valueReaderName(this.valueReaderName)
                .valueWriterName(this.valueWriterName);
    }

    /**
     * Creates a config with all default values from {@link ConfigProperty}.
     */
    public static FeatureConfig defaults() {
        return builder().build();
    }

    public static final class Builder {
        private String key = ConfigProperty.KEY.getDefaultValue();
        private boolean ignore = ConfigProperty.IGNORE.getDefaultValue();
        private boolean ignoreRead = ConfigProperty.IGNORE_READ.getDefaultValue();
        private boolean ignoreWrite = ConfigProperty.IGNORE_WRITE.getDefaultValue();
        private boolean forceRead = ConfigProperty.FORCE_READ.getDefaultValue();
        private boolean forceWrite = ConfigProperty.FORCE_WRITE.getDefaultValue();
        private boolean serializeNull = ConfigProperty.SERIALIZE_NULL.getDefaultValue();
        private boolean serializeEmpty = ConfigProperty.SERIALIZE_EMPTY.getDefaultValue();
        private boolean serializeDefault = ConfigProperty.SERIALIZE_DEFAULT.getDefaultValue();
        private EnumSerializationStrategy enumSerialization = EnumSerializationStrategy.valueOf(ConfigProperty.ENUM_SERIALIZATION.getDefaultValue());
        private String valueReaderName = ConfigProperty.VALUE_READER_NAME.getDefaultValue();
        private String valueWriterName = ConfigProperty.VALUE_WRITER_NAME.getDefaultValue();

        private Builder() {}

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder ignore(boolean ignore) {
            this.ignore = ignore;
            return this;
        }

        public Builder ignoreRead(boolean ignoreRead) {
            this.ignoreRead = ignoreRead;
            return this;
        }

        public Builder ignoreWrite(boolean ignoreWrite) {
            this.ignoreWrite = ignoreWrite;
            return this;
        }

        public Builder forceRead(boolean forceRead) {
            this.forceRead = forceRead;
            return this;
        }

        public Builder forceWrite(boolean forceWrite) {
            this.forceWrite = forceWrite;
            return this;
        }

        public Builder serializeNull(boolean serializeNull) {
            this.serializeNull = serializeNull;
            return this;
        }

        public Builder serializeEmpty(boolean serializeEmpty) {
            this.serializeEmpty = serializeEmpty;
            return this;
        }

        public Builder serializeDefault(boolean serializeDefault) {
            this.serializeDefault = serializeDefault;
            return this;
        }

        public Builder enumSerialization(EnumSerializationStrategy enumSerialization) {
            this.enumSerialization = enumSerialization;
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

        public FeatureConfig build() {
            return new FeatureConfig(this);
        }
    }
}
