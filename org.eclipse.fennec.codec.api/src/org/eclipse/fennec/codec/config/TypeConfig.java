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
import org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;

/**
 * Immutable type serialization configuration.
 * <p>
 * Type serialization uses two orthogonal dimensions:
 * <ul>
 *   <li><b>Format</b> (PLAIN | STRUCTURED) - how data is presented</li>
 *   <li><b>Strategy</b> (URI | NAME | CLASS | NUMERIC | MAPPED | SCHEMA_AND_TYPE) - what information is transported</li>
 * </ul>
 * <p>
 * This class supports the cascading merge pattern where each configuration layer
 * can override values from the previous layer.
 *
 * @see ConfigProperty
 * @see ConfigMergeHelper
 */
public final class TypeConfig implements Mergeable<TypeConfig> {

    private final boolean include;
    private final SerializationFormat format;
    private final TypeStrategy strategy;
    private final String typeKey;
    private final String schemaKey;
    private final String nameKey;
    private final String discriminatorPath;
    private final String discriminatorValue;
    private final String valueReaderName;
    private final String valueWriterName;
    private final FingerprintMode fingerprintMode;
    private final String fingerprintKey;

    private TypeConfig(Builder builder) {
        this.include = builder.include;
        this.format = builder.format;
        this.strategy = builder.strategy;
        this.typeKey = builder.typeKey;
        this.schemaKey = builder.schemaKey;
        this.nameKey = builder.nameKey;
        this.discriminatorPath = builder.discriminatorPath;
        this.discriminatorValue = builder.discriminatorValue;
        this.valueReaderName = builder.valueReaderName;
        this.valueWriterName = builder.valueWriterName;
        this.fingerprintMode = builder.fingerprintMode;
        this.fingerprintKey = builder.fingerprintKey;
    }

    // ========================================================================
    // Accessors
    // ========================================================================

    /**
     * Returns whether type serialization is enabled.
     * Default: true
     */
    public boolean isInclude() {
        return include;
    }

    /**
     * Returns the serialization format (PLAIN or STRUCTURED).
     * Default: PLAIN
     */
    public SerializationFormat getFormat() {
        return format;
    }

    /**
     * Returns the type serialization strategy.
     * Default: URI
     */
    public TypeStrategy getStrategy() {
        return strategy;
    }

    /**
     * Returns the JSON property key for type information.
     * Default: "_type"
     */
    public String getTypeKey() {
        return typeKey;
    }

    /**
     * Returns the key for schema in STRUCTURED format.
     * Default: "schema"
     */
    public String getSchemaKey() {
        return schemaKey;
    }

    /**
     * Returns the key for name in STRUCTURED format.
     * Default: "type"
     */
    public String getNameKey() {
        return nameKey;
    }

    /**
     * Returns the discriminator path for MAPPED strategy.
     */
    public String getDiscriminatorPath() {
        return discriminatorPath;
    }

    /**
     * Returns the discriminator value for this class (MAPPED strategy).
     */
    public String getDiscriminatorValue() {
        return discriminatorValue;
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

    /**
     * Returns whether and how the in-band EPackage fingerprint is written (issue #73, B.1).
     * <p>
     * Default: {@link FingerprintMode#NONE} — nothing is written unless a use case opts in.
     * This governs <b>writing only</b>; reading always accepts a fingerprint it finds.
     * </p>
     *
     * @return the fingerprint write mode, never {@code null}
     * @see <a href="docs/codec-v2-spec/06-type.md#8-in-band-epackage-fingerprint">Spec 06 §8</a>
     */
    public FingerprintMode getFingerprintMode() {
        return fingerprintMode;
    }

    /**
     * Returns whether writing the fingerprint is enabled at all.
     *
     * @return {@code true} unless the mode is {@link FingerprintMode#NONE}
     */
    public boolean isFingerprintWriteEnabled() {
        return fingerprintMode != null && fingerprintMode != FingerprintMode.NONE;
    }

    /**
     * Returns the <b>inner</b> key carrying the fingerprint, as used inside a STRUCTURED
     * type object.
     * <p>
     * Default: {@code "fingerprint"}. For the PLAIN sibling form see
     * {@link #getPlainFingerprintKey()}.
     * </p>
     *
     * @return the inner fingerprint key
     */
    public String getFingerprintKey() {
        return fingerprintKey;
    }

    /**
     * Returns the fingerprint key in its <b>PLAIN sibling</b> form, derived from
     * {@link #getFingerprintKey()} by prefixing {@code _}.
     * <p>
     * A configured key that already starts with {@code _} or {@code @} is returned
     * unchanged, so {@code @fingerprint} stays {@code @fingerprint}. This is the same
     * one-value-two-placements rule the schema key uses, so configuring the key does not
     * require knowing which format will be used.
     * </p>
     *
     * @return the PLAIN form of the fingerprint key
     */
    public String getPlainFingerprintKey() {
        return toPlainKey(fingerprintKey);
    }

    /**
     * Derives the PLAIN sibling form of an inner metadata key: prefixed with {@code _}
     * unless the key already carries a metadata prefix ({@code _} or {@code @}).
     * <p>
     * Public so that read-side code, which has to accept both placements of a key without
     * holding a {@link TypeConfig}, applies exactly the same rule as the write side.
     * </p>
     *
     * @param innerKey the inner (unprefixed) key form, may be {@code null}
     * @return the PLAIN sibling form
     */
    public static String toPlainKey(String innerKey) {
        if (innerKey == null || innerKey.isEmpty()) {
            return innerKey;
        }
        if (innerKey.startsWith("_") || innerKey.startsWith("@")) {
            return innerKey;
        }
        return "_" + innerKey;
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
    public TypeConfig mergeWith(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return this;
        }

        return toBuilder()
                .include(getBoolean(source, ConfigProperty.TYPE_INCLUDE, this.include))
                .format(getEnum(source, ConfigProperty.TYPE_FORMAT, SerializationFormat.class, this.format))
                .strategy(getEnum(source, ConfigProperty.TYPE_STRATEGY, TypeStrategy.class, this.strategy))
                .typeKey(getString(source, ConfigProperty.TYPE_KEY, this.typeKey))
                .schemaKey(getString(source, ConfigProperty.TYPE_SCHEMA_KEY, this.schemaKey))
                .nameKey(getString(source, ConfigProperty.TYPE_NAME_KEY, this.nameKey))
                .discriminatorPath(getString(source, ConfigProperty.TYPE_DISCRIMINATOR_PATH, this.discriminatorPath))
                .discriminatorValue(getString(source, ConfigProperty.TYPE_DISCRIMINATOR, this.discriminatorValue))
                .valueReaderName(getString(source, ConfigProperty.TYPE_VALUE_READER_NAME, this.valueReaderName))
                .valueWriterName(getString(source, ConfigProperty.TYPE_VALUE_WRITER_NAME, this.valueWriterName))
                .fingerprintMode(getEnum(source, ConfigProperty.FINGERPRINT_MODE, FingerprintMode.class,
                        this.fingerprintMode))
                .fingerprintKey(getString(source, ConfigProperty.FINGERPRINT_KEY, this.fingerprintKey))
                .build();
    }

    // ========================================================================
    // Validation support
    // ========================================================================

    /**
     * Validates this config and returns a validated (possibly normalized) config.
     * <p>
     * Type config constraints:
     * <ul>
     *   <li>nameKey (inner type key) is only meaningful for STRUCTURED format</li>
     *   <li>schemaKey is only meaningful for STRUCTURED format or SCHEMA_AND_TYPE strategy</li>
     * </ul>
     * <p>
     * Note: discriminatorPath is validated separately by discriminator mapping configuration,
     * as discriminator mapping is a separate layer from TypeStrategy.
     */
    @Override
    public TypeConfig validate(DiagnosticCollector diagnostics) {
        // Constraint: nameKey only meaningful for STRUCTURED format
        if (format != SerializationFormat.STRUCTURED && nameKey != null
                && !nameKey.equals(ConfigProperty.TYPE_NAME_KEY.getDefaultValue())) {
            diagnostics.addWarning(
                "typeNameKey is ignored when typeFormat is not STRUCTURED",
                "TypeConfig.validate");
        }

        // Constraint: schemaKey only meaningful for STRUCTURED format or SCHEMA_AND_TYPE strategy
        if (format != SerializationFormat.STRUCTURED && strategy != TypeStrategy.SCHEMA_AND_TYPE
                && schemaKey != null && !schemaKey.equals(ConfigProperty.TYPE_SCHEMA_KEY.getDefaultValue())) {
            diagnostics.addWarning(
                "typeSchemaKey is ignored when typeFormat is not STRUCTURED and typeStrategy is not SCHEMA_AND_TYPE",
                "TypeConfig.validate");
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
                .include(this.include)
                .format(this.format)
                .strategy(this.strategy)
                .typeKey(this.typeKey)
                .schemaKey(this.schemaKey)
                .nameKey(this.nameKey)
                .discriminatorPath(this.discriminatorPath)
                .discriminatorValue(this.discriminatorValue)
                .valueReaderName(this.valueReaderName)
                .valueWriterName(this.valueWriterName)
                .fingerprintMode(this.fingerprintMode)
                .fingerprintKey(this.fingerprintKey);
    }

    /**
     * Creates a config with all default values from {@link ConfigProperty}.
     */
    public static TypeConfig defaults() {
        return builder().build();
    }

    public static final class Builder {
        private boolean include = ConfigProperty.TYPE_INCLUDE.getDefaultValue();
        private SerializationFormat format = SerializationFormat.valueOf(ConfigProperty.TYPE_FORMAT.getDefaultValue());
        private TypeStrategy strategy = TypeStrategy.valueOf(ConfigProperty.TYPE_STRATEGY.getDefaultValue());
        private String typeKey = ConfigProperty.TYPE_KEY.getDefaultValue();
        private String schemaKey = ConfigProperty.TYPE_SCHEMA_KEY.getDefaultValue();
        private String nameKey = ConfigProperty.TYPE_NAME_KEY.getDefaultValue();
        private String discriminatorPath = ConfigProperty.TYPE_DISCRIMINATOR_PATH.getDefaultValue();
        private String discriminatorValue = ConfigProperty.TYPE_DISCRIMINATOR.getDefaultValue();
        private String valueReaderName = ConfigProperty.TYPE_VALUE_READER_NAME.getDefaultValue();
        private String valueWriterName = ConfigProperty.TYPE_VALUE_WRITER_NAME.getDefaultValue();
        private FingerprintMode fingerprintMode =
                FingerprintMode.get(ConfigProperty.FINGERPRINT_MODE.getDefaultValue());
        private String fingerprintKey = ConfigProperty.FINGERPRINT_KEY.getDefaultValue();

        private Builder() {}

        public Builder include(boolean include) {
            this.include = include;
            return this;
        }

        public Builder format(SerializationFormat format) {
            this.format = format;
            return this;
        }

        public Builder strategy(TypeStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder typeKey(String typeKey) {
            this.typeKey = typeKey;
            return this;
        }

        public Builder schemaKey(String schemaKey) {
            this.schemaKey = schemaKey;
            return this;
        }

        public Builder nameKey(String nameKey) {
            this.nameKey = nameKey;
            return this;
        }

        public Builder discriminatorPath(String discriminatorPath) {
            this.discriminatorPath = discriminatorPath;
            return this;
        }

        public Builder discriminatorValue(String discriminatorValue) {
            this.discriminatorValue = discriminatorValue;
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

        /**
         * Sets whether and how the in-band EPackage fingerprint is written.
         * A {@code null} value falls back to {@link FingerprintMode#NONE}.
         */
        public Builder fingerprintMode(FingerprintMode fingerprintMode) {
            this.fingerprintMode = fingerprintMode != null ? fingerprintMode : FingerprintMode.NONE;
            return this;
        }

        /**
         * Sets the inner key carrying the fingerprint. The PLAIN sibling is derived from it.
         */
        public Builder fingerprintKey(String fingerprintKey) {
            this.fingerprintKey = fingerprintKey;
            return this;
        }

        public TypeConfig build() {
            return new TypeConfig(this);
        }
    }
}
