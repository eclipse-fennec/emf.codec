/*
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
 */
package org.eclipse.fennec.codec.config;

import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getBoolean;
import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getEnum;
import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getList;
import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getString;

import java.util.List;
import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.IdKeyMode;
import org.eclipse.fennec.model.metadata.IdStrategy;
import org.eclipse.fennec.model.metadata.SerializationFormat;

/**
 * Immutable ID serialization configuration.
 * <p>
 * This class supports the cascading merge pattern where each configuration layer
 * (Annotation → Module → Factory → Resource → Options) can override values from
 * the previous layer. Each merge produces a new immutable instance.
 * <p>
 * Usage:
 * <pre>
 * IdConfig config = IdConfig.defaults()
 *     .mergeWith(annotationMap)
 *     .mergeWith(moduleMap)
 *     .mergeWith(factoryMap)
 *     .mergeWith(resourceMap)
 *     .mergeWith(optionsMap);
 * </pre>
 *
 * @see ConfigProperty
 * @see ConfigMergeHelper
 */
public final class IdConfig implements Mergeable<IdConfig> {

    private final String key;
    private final IdStrategy strategy;
    private final IdKeyMode keyMode;
    private final SerializationFormat format;
    private final String valueKey;
    private final List<String> idFeatures;
    private final String separator;
    private final String separatorKey;
    private final boolean serializeSeparator;
    private final boolean onTop;
    private final String valueWriterName;
    private final String valueReaderName;

    private IdConfig(Builder builder) {
        this.key = builder.key;
        this.strategy = builder.strategy;
        this.keyMode = builder.keyMode;
        this.format = builder.format;
        this.valueKey = builder.valueKey;
        this.idFeatures = List.copyOf(builder.idFeatures);
        this.separator = builder.separator;
        this.separatorKey = builder.separatorKey;
        this.serializeSeparator = builder.serializeSeparator;
        this.onTop = builder.onTop;
        this.valueWriterName = builder.valueWriterName;
        this.valueReaderName = builder.valueReaderName;
    }

    // ========================================================================
    // Accessors
    // ========================================================================

    /**
     * Returns the JSON property key for the ID field.
     * Default: "_id"
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the ID strategy (ID_FIELD, COMBINED, etc.).
     * Default: ID_FIELD
     */
    public IdStrategy getStrategy() {
        return strategy;
    }

    /**
     * Returns the key mode (ID_ONLY, BOTH, FEATURE_ONLY).
     * Default: ID_ONLY
     */
    public IdKeyMode getKeyMode() {
        return keyMode;
    }

    /**
     * Returns the serialization format (PLAIN, STRUCTURED).
     * Default: PLAIN
     */
    public SerializationFormat getFormat() {
        return format;
    }

    /**
     * Returns the key for the value field in STRUCTURED format.
     * Default: "id"
     */
    public String getValueKey() {
        return valueKey;
    }

    /**
     * Returns the list of feature names for COMBINED ID strategy.
     * Default: empty list
     */
    public List<String> getIdFeatures() {
        return idFeatures;
    }

    /**
     * Returns the separator for combining multiple ID features.
     * Default: "-"
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Returns the JSON key for the separator field in STRUCTURED format.
     * Default: "separator"
     */
    public String getSeparatorKey() {
        return separatorKey;
    }

    /**
     * Returns whether the separator should be serialized in STRUCTURED format.
     * Default: true
     */
    public boolean isSerializeSeparator() {
        return serializeSeparator;
    }

    /**
     * Returns whether the ID should appear at the top of the JSON object.
     * Default: false
     */
    public boolean isOnTop() {
        return onTop;
    }

    /**
     * Returns the custom value writer name, or null if none.
     */
    public String getValueWriterName() {
        return valueWriterName;
    }

    /**
     * Returns the custom value reader name, or null if none.
     */
    public String getValueReaderName() {
        return valueReaderName;
    }

    // ========================================================================
    // Merge support
    // ========================================================================

    /**
     * Creates a new config by merging this config with values from a property map.
     * <p>
     * Values present in the source map override this config's values.
     * Values not present in the source map are kept from this config.
     * <p>
     * This is the core operation for cascading configuration:
     * <pre>
     * IdConfig.defaults()
     *     .mergeWith(annotations)
     *     .mergeWith(moduleConfig)
     *     .mergeWith(factoryConfig)
     *     .mergeWith(resourceConfig)
     *     .mergeWith(options)
     * </pre>
     *
     * @param source the property map to merge (may be null or empty)
     * @return a new config with merged values, or this if source is null/empty
     */
    @Override
    public IdConfig mergeWith(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return this;
        }

        return toBuilder()
                .strategy(getEnum(source, ConfigProperty.ID_STRATEGY, IdStrategy.class, this.strategy))
                .key(getString(source, ConfigProperty.ID_KEY, this.key))
                .valueKey(getString(source, ConfigProperty.ID_VALUE_KEY, this.valueKey))
                .format(getEnum(source, ConfigProperty.ID_FORMAT, SerializationFormat.class, this.format))
                .keyMode(getEnum(source, ConfigProperty.ID_KEY_MODE, IdKeyMode.class, this.keyMode))
                .idFeatures(getList(source, ConfigProperty.ID_FEATURES, this.idFeatures))
                .separator(getString(source, ConfigProperty.ID_SEPARATOR, this.separator))
                .separatorKey(getString(source, ConfigProperty.ID_SEPARATOR_KEY, this.separatorKey))
                .serializeSeparator(getBoolean(source, ConfigProperty.ID_SEPARATOR_SERIALIZE, this.serializeSeparator))
                .onTop(getBoolean(source, ConfigProperty.ID_ON_TOP, this.onTop))
                .valueReaderName(getString(source, ConfigProperty.ID_VALUE_READER_NAME, this.valueReaderName))
                .valueWriterName(getString(source, ConfigProperty.ID_VALUE_WRITER_NAME, this.valueWriterName))
                .build();
    }

    // ========================================================================
    // Validation support
    // ========================================================================

    /**
     * Validates this config and returns a validated (possibly normalized) config.
     * <p>
     * ID config constraints:
     * <ul>
     *   <li>COMBINED strategy requires non-empty idFeatures list</li>
     *   <li>valueKey is only meaningful for STRUCTURED format</li>
     *   <li>separatorKey is only meaningful for STRUCTURED format</li>
     * </ul>
     *
     * @param diagnostics collector for validation diagnostics
     * @return validated config (may be this if no changes needed)
     */
    @Override
    public IdConfig validate(DiagnosticCollector diagnostics) {
        boolean needsNormalization = false;
        Builder builder = toBuilder();

        // Constraint: COMBINED strategy requires non-empty idFeatures
        if (strategy == IdStrategy.COMBINED && (idFeatures == null || idFeatures.isEmpty())) {
            diagnostics.addError(
                "idFeatures must not be empty when idStrategy=COMBINED",
                "IdConfig.validate");
        }

        // Constraint: valueKey only meaningful for STRUCTURED format
        if (format != SerializationFormat.STRUCTURED && valueKey != null
                && !valueKey.equals(ConfigProperty.ID_VALUE_KEY.getDefaultValue())) {
            diagnostics.addWarning(
                "idValueKey is ignored when idFormat is not STRUCTURED",
                "IdConfig.validate");
            // Could normalize: builder.valueKey(null); needsNormalization = true;
        }

        // Constraint: separatorKey only meaningful for STRUCTURED format
        if (format != SerializationFormat.STRUCTURED && separatorKey != null
                && !separatorKey.equals(ConfigProperty.ID_SEPARATOR_KEY.getDefaultValue())) {
            diagnostics.addWarning(
                "idSeparatorKey is ignored when idFormat is not STRUCTURED",
                "IdConfig.validate");
        }

        return needsNormalization ? builder.build() : this;
    }

    // ========================================================================
    // Builder support
    // ========================================================================

    /**
     * Creates a new builder with default values from {@link ConfigProperty}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new builder initialized with this config's values.
     */
    public Builder toBuilder() {
        return new Builder()
                .key(this.key)
                .strategy(this.strategy)
                .keyMode(this.keyMode)
                .format(this.format)
                .valueKey(this.valueKey)
                .idFeatures(this.idFeatures)
                .separator(this.separator)
                .separatorKey(this.separatorKey)
                .serializeSeparator(this.serializeSeparator)
                .onTop(this.onTop)
                .valueWriterName(this.valueWriterName)
                .valueReaderName(this.valueReaderName);
    }

    /**
     * Creates a config with all default values from {@link ConfigProperty}.
     * <p>
     * This is the starting point for the merge chain.
     */
    public static IdConfig defaults() {
        return builder().build();
    }

    /**
     * Builder for IdConfig.
     */
    public static final class Builder {
        // Defaults from ConfigProperty
        private String key = ConfigProperty.ID_KEY.getDefaultValue();
        private IdStrategy strategy = IdStrategy.valueOf(ConfigProperty.ID_STRATEGY.getDefaultValue());
        private IdKeyMode keyMode = IdKeyMode.valueOf(ConfigProperty.ID_KEY_MODE.getDefaultValue());
        private SerializationFormat format = SerializationFormat.valueOf(ConfigProperty.ID_FORMAT.getDefaultValue());
        private String valueKey = ConfigProperty.ID_VALUE_KEY.getDefaultValue();
        private List<String> idFeatures = ConfigProperty.ID_FEATURES.getDefaultValue();
        private String separator = ConfigProperty.ID_SEPARATOR.getDefaultValue();
        private String separatorKey = ConfigProperty.ID_SEPARATOR_KEY.getDefaultValue();
        private boolean serializeSeparator = ConfigProperty.ID_SEPARATOR_SERIALIZE.getDefaultValue();
        private boolean onTop = ConfigProperty.ID_ON_TOP.getDefaultValue();
        private String valueWriterName = ConfigProperty.ID_VALUE_WRITER_NAME.getDefaultValue();
        private String valueReaderName = ConfigProperty.ID_VALUE_READER_NAME.getDefaultValue();

        private Builder() {}

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder strategy(IdStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder keyMode(IdKeyMode keyMode) {
            this.keyMode = keyMode;
            return this;
        }

        public Builder format(SerializationFormat format) {
            this.format = format;
            return this;
        }

        public Builder valueKey(String valueKey) {
            this.valueKey = valueKey;
            return this;
        }

        public Builder idFeatures(List<String> idFeatures) {
            this.idFeatures = idFeatures != null ? idFeatures : List.of();
            return this;
        }

        public Builder separator(String separator) {
            this.separator = separator;
            return this;
        }

        public Builder separatorKey(String separatorKey) {
            this.separatorKey = separatorKey;
            return this;
        }

        public Builder serializeSeparator(boolean serializeSeparator) {
            this.serializeSeparator = serializeSeparator;
            return this;
        }

        public Builder onTop(boolean onTop) {
            this.onTop = onTop;
            return this;
        }

        public Builder valueWriterName(String valueWriterName) {
            this.valueWriterName = valueWriterName;
            return this;
        }

        public Builder valueReaderName(String valueReaderName) {
            this.valueReaderName = valueReaderName;
            return this;
        }

        public IdConfig build() {
            return new IdConfig(this);
        }
    }
}
