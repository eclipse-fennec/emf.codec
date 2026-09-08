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
import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getInteger;
import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getString;

import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;

/**
 * Immutable reference serialization configuration.
 * <p>
 * This class supports the cascading merge pattern where each configuration layer
 * can override values from the previous layer.
 *
 * @see ConfigProperty
 * @see ConfigMergeHelper
 */
public final class ReferenceConfig implements Mergeable<ReferenceConfig> {

    private final SerializationFormat format;
    private final String refKey;
    private final String refTypeKey;
    private final String proxyKey;
    private final boolean expand;
    private final boolean expandGlobal;
    private final int expandDepth;
    private final boolean expandIgnoreBidirectional;
    private final boolean serializeInstanceType;
    private final String valueReaderName;
    private final String valueWriterName;
    // Per-reference type/id configuration (overrides class-level TypeConfig/IdConfig)
    private final String typeKey;
    private final String idKey;

    private ReferenceConfig(Builder builder) {
        this.format = builder.format;
        this.refKey = builder.refKey;
        this.refTypeKey = builder.refTypeKey;
        this.proxyKey = builder.proxyKey;
        this.expand = builder.expand;
        this.expandGlobal = builder.expandGlobal;
        this.expandDepth = builder.expandDepth;
        this.expandIgnoreBidirectional = builder.expandIgnoreBidirectional;
        this.serializeInstanceType = builder.serializeInstanceType;
        this.valueReaderName = builder.valueReaderName;
        this.valueWriterName = builder.valueWriterName;
        this.typeKey = builder.typeKey;
        this.idKey = builder.idKey;
    }

    // ========================================================================
    // Accessors
    // ========================================================================

    /**
     * Returns the serialization format (PLAIN or STRUCTURED).
     * Default: STRUCTURED
     */
    public SerializationFormat getFormat() {
        return format;
    }

    /**
     * Returns the JSON property key for reference value.
     * Default: "_ref"
     */
    public String getRefKey() {
        return refKey;
    }

    /**
     * Returns the JSON property key for type in STRUCTURED format.
     * Default: "_type"
     */
    public String getRefTypeKey() {
        return refTypeKey;
    }

    /**
     * Returns the JSON property key for proxy marker.
     * Default: "$proxy"
     */
    public String getProxyKey() {
        return proxyKey;
    }

    /**
     * Returns whether this specific reference should be expanded inline.
     * Default: false
     */
    public boolean isExpand() {
        return expand;
    }

    /**
     * Returns whether all non-containment references should be expanded globally.
     * Default: false
     */
    public boolean isExpandGlobal() {
        return expandGlobal;
    }

    /**
     * Returns the maximum depth for nested expansion.
     * Default: 1 (only direct references)
     */
    public int getExpandDepth() {
        return expandDepth;
    }

    /**
     * Returns whether to skip bidirectional (opposite) references during expansion.
     * Default: true (skip to prevent cycles)
     */
    public boolean isExpandIgnoreBidirectional() {
        return expandIgnoreBidirectional;
    }

    /**
     * Returns whether to serialize the instance type in STRUCTURED format.
     * Default: true
     */
    public boolean isSerializeInstanceType() {
        return serializeInstanceType;
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
     * Returns the per-reference type key override, or null if not set.
     * <p>
     * When set, this overrides the class-level typeKey from TypeConfig for
     * objects serialized through this reference.
     * </p>
     */
    public String getTypeKey() {
        return typeKey;
    }

    /**
     * Returns the per-reference ID key override, or null if not set.
     * <p>
     * <b>Not consumed by the codec.</b> A reference-scoped {@code idKey} is resolved through
     * {@link ConfigurationResolver#resolveIdConfig(org.eclipse.emf.ecore.EClass,
     * org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.fennec.codec.diagnostic.DiagnosticCollector)}
     * instead, so that one configuration object owns everything about an identity (issue #176).
     * </p>
     * <p>
     * This field is kept because it fits the other reading of a reference-scoped id key -
     * writing the target's identity in place of a {@code _ref} URI, for a non-containment
     * reference. That is a separate feature, deliberately out of scope for #176, and nothing
     * implements it yet.
     * </p>
     */
    public String getIdKey() {
        return idKey;
    }

    // ========================================================================
    // Computed properties
    // ========================================================================

    /**
     * Returns whether references should be expanded based on expand flags.
     * A reference is expanded if either expand=true or expandGlobal=true.
     */
    public boolean shouldExpand() {
        return expand || expandGlobal;
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
    public ReferenceConfig mergeWith(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return this;
        }

        return toBuilder()
                .format(getEnum(source, ConfigProperty.REF_FORMAT, SerializationFormat.class, this.format))
                .refKey(getString(source, ConfigProperty.REF_KEY, this.refKey))
                .refTypeKey(getString(source, ConfigProperty.REF_TYPE_KEY, this.refTypeKey))
                .proxyKey(getString(source, ConfigProperty.PROXY_KEY, this.proxyKey))
                .expand(getBoolean(source, ConfigProperty.EXPAND, this.expand))
                .expandGlobal(getBoolean(source, ConfigProperty.EXPAND_GLOBAL, this.expandGlobal))
                .expandDepth(getInteger(source, ConfigProperty.EXPAND_DEPTH, this.expandDepth))
                .expandIgnoreBidirectional(getBoolean(source, ConfigProperty.EXPAND_IGNORE_BIDIRECTIONAL, this.expandIgnoreBidirectional))
                .serializeInstanceType(getBoolean(source, ConfigProperty.SERIALIZE_INSTANCE_TYPE, this.serializeInstanceType))
                .valueReaderName(getString(source, ConfigProperty.VALUE_READER_NAME, this.valueReaderName))
                .valueWriterName(getString(source, ConfigProperty.VALUE_WRITER_NAME, this.valueWriterName))
                .typeKey(getString(source, ConfigProperty.TYPE_KEY, this.typeKey))
                .idKey(getString(source, ConfigProperty.ID_KEY, this.idKey))
                .build();
    }

    // ========================================================================
    // Validation support
    // ========================================================================

    /**
     * Validates this config and returns a validated (possibly normalized) config.
     * <p>
     * Reference config constraints:
     * <ul>
     *   <li>refTypeKey is only meaningful for STRUCTURED format</li>
     *   <li>expandDepth greater than 1 is not yet supported</li>
     *   <li>expandIgnoreBidirectional only matters when expand is true</li>
     * </ul>
     */
    @Override
    public ReferenceConfig validate(DiagnosticCollector diagnostics) {
        // Constraint: refTypeKey only meaningful for STRUCTURED format
        if (format != SerializationFormat.STRUCTURED && refTypeKey != null
                && !refTypeKey.equals(ConfigProperty.REF_TYPE_KEY.getDefaultValue())) {
            diagnostics.addWarning(
                "refTypeKey is ignored when refFormat is not STRUCTURED",
                "ReferenceConfig.validate");
        }

        // Constraint: expandDepth > 1 not yet supported
        if (expandDepth > 1) {
            diagnostics.addWarning(
                "expandDepth values greater than 1 are not yet supported; using depth=1",
                "ReferenceConfig.validate");
        }

        // Constraint: expand settings only matter when expanding
        if (!shouldExpand() && expandIgnoreBidirectional != ConfigProperty.EXPAND_IGNORE_BIDIRECTIONAL.<Boolean>getDefaultValue()) {
            diagnostics.addInfo(
                "expandIgnoreBidirectional is ignored when expand is not enabled",
                "ReferenceConfig.validate");
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
                .format(this.format)
                .refKey(this.refKey)
                .refTypeKey(this.refTypeKey)
                .proxyKey(this.proxyKey)
                .expand(this.expand)
                .expandGlobal(this.expandGlobal)
                .expandDepth(this.expandDepth)
                .expandIgnoreBidirectional(this.expandIgnoreBidirectional)
                .serializeInstanceType(this.serializeInstanceType)
                .valueReaderName(this.valueReaderName)
                .valueWriterName(this.valueWriterName)
                .typeKey(this.typeKey)
                .idKey(this.idKey);
    }

    /**
     * Creates a config with all default values from {@link ConfigProperty}.
     */
    public static ReferenceConfig defaults() {
        return builder().build();
    }

    public static final class Builder {
        private SerializationFormat format = SerializationFormat.valueOf(ConfigProperty.REF_FORMAT.getDefaultValue());
        private String refKey = ConfigProperty.REF_KEY.getDefaultValue();
        private String refTypeKey = ConfigProperty.REF_TYPE_KEY.getDefaultValue();
        private String proxyKey = ConfigProperty.PROXY_KEY.getDefaultValue();
        // Note: expand defaults to false - it's set per-reference based on expand list matching
        private boolean expand = false;
        private boolean expandGlobal = ConfigProperty.EXPAND_GLOBAL.getDefaultValue();
        private int expandDepth = ConfigProperty.EXPAND_DEPTH.getDefaultValue();
        private boolean expandIgnoreBidirectional = ConfigProperty.EXPAND_IGNORE_BIDIRECTIONAL.getDefaultValue();
        private boolean serializeInstanceType = ConfigProperty.SERIALIZE_INSTANCE_TYPE.getDefaultValue();
        private String valueReaderName = ConfigProperty.VALUE_READER_NAME.getDefaultValue();
        private String valueWriterName = ConfigProperty.VALUE_WRITER_NAME.getDefaultValue();
        // Per-reference overrides (null = use class-level config)
        private String typeKey = null;
        private String idKey = null;

        private Builder() {}

        public Builder format(SerializationFormat format) {
            this.format = format;
            return this;
        }

        public Builder refKey(String refKey) {
            this.refKey = refKey;
            return this;
        }

        public Builder refTypeKey(String refTypeKey) {
            this.refTypeKey = refTypeKey;
            return this;
        }

        public Builder proxyKey(String proxyKey) {
            this.proxyKey = proxyKey;
            return this;
        }

        public Builder expand(boolean expand) {
            this.expand = expand;
            return this;
        }

        public Builder expandGlobal(boolean expandGlobal) {
            this.expandGlobal = expandGlobal;
            return this;
        }

        public Builder expandDepth(int expandDepth) {
            this.expandDepth = expandDepth;
            return this;
        }

        public Builder expandIgnoreBidirectional(boolean expandIgnoreBidirectional) {
            this.expandIgnoreBidirectional = expandIgnoreBidirectional;
            return this;
        }

        public Builder serializeInstanceType(boolean serializeInstanceType) {
            this.serializeInstanceType = serializeInstanceType;
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
         * Sets the per-reference type key override.
         * <p>
         * When set, objects serialized through this reference will use this key
         * instead of the class-level typeKey.
         * </p>
         *
         * @param typeKey the type key, or null to use class-level config
         */
        public Builder typeKey(String typeKey) {
            this.typeKey = typeKey;
            return this;
        }

        /**
         * Sets the per-reference ID key override.
         * <p>
         * When set, objects serialized through this reference will use this key
         * instead of the class-level idKey.
         * </p>
         *
         * @param idKey the ID key, or null to use class-level config
         */
        public Builder idKey(String idKey) {
            this.idKey = idKey;
            return this;
        }

        public ReferenceConfig build() {
            return new ReferenceConfig(this);
        }
    }
}
