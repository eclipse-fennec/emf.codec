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

import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getEnum;
import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getMap;
import static org.eclipse.fennec.codec.config.ConfigMergeHelper.getString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;

/**
 * Immutable discriminator mapping configuration.
 * <p>
 * This class handles both Type Mapping Registry (on EClass) and Inline Mapping (on EReference).
 * It supports the cascading merge pattern where each configuration layer can override values
 * from the previous layer.
 * <p>
 * Type Mapping Registry properties (EClass level):
 * <ul>
 *   <li>typeMapId - registry ID</li>
 *   <li>typeDiscriminatorPath - JSON path to discriminator value</li>
 *   <li>typeDiscriminator - this class's discriminator value (for distributed registration)</li>
 *   <li>typeMappings - discriminator value → EClass URI mappings</li>
 * </ul>
 * <p>
 * Inline Mapping properties (EReference level):
 * <ul>
 *   <li>inlineMappings - discriminator value → EClass URI mappings</li>
 * </ul>
 * <p>
 * Common properties:
 * <ul>
 *   <li>fallbackStrategy - ERROR, SKIP, or FALLBACK (default)</li>
 *   <li>fallbackEClass - explicit fallback EClass URI</li>
 * </ul>
 *
 * @see ConfigProperty
 * @see ConfigMergeHelper
 */
public final class DiscriminatorConfig implements Mergeable<DiscriminatorConfig> {

    /**
     * Fallback strategy when discriminator value cannot be resolved.
     */
    public enum FallbackStrategy {
        /** Fail deserialization, throw exception */
        ERROR,
        /** Skip the element (don't add to collection), log warning */
        SKIP,
        /** Use fallback resolution (fallbackEClass, feature type hint, reference type) */
        FALLBACK
    }

    // Type Mapping Registry properties (EClass level)
    private final String typeMapId;
    private final String typeDiscriminatorPath;
    private final String typeDiscriminator;
    private final Map<String, String> typeMappings;

    // Inline Mapping properties (EReference level)
    private final Map<String, String> inlineMappings;

    // Common properties
    private final FallbackStrategy fallbackStrategy;
    private final String fallbackEClass;

    private DiscriminatorConfig(Builder builder) {
        this.typeMapId = builder.typeMapId;
        this.typeDiscriminatorPath = builder.typeDiscriminatorPath;
        this.typeDiscriminator = builder.typeDiscriminator;
        this.typeMappings = builder.typeMappings != null
                ? Collections.unmodifiableMap(new HashMap<>(builder.typeMappings))
                : Collections.emptyMap();
        this.inlineMappings = builder.inlineMappings != null
                ? Collections.unmodifiableMap(new HashMap<>(builder.inlineMappings))
                : Collections.emptyMap();
        this.fallbackStrategy = builder.fallbackStrategy;
        this.fallbackEClass = builder.fallbackEClass;
    }

    // ========================================================================
    // Accessors - Type Mapping Registry (EClass level)
    // ========================================================================

    /**
     * Returns the type mapping registry ID.
     * <p>
     * Used to group related type mappings. The mapId is embedded in the
     * annotation source URI: {@code http://eclipse.org/fennec/codec/typeMapping/{mapId}}
     */
    public String getTypeMapId() {
        return typeMapId;
    }

    /**
     * Returns the JSON path to the discriminator value.
     * <p>
     * Uses dot notation for nested paths (e.g., "info.profileName").
     */
    public String getTypeDiscriminatorPath() {
        return typeDiscriminatorPath;
    }

    /**
     * Returns this class's discriminator value for distributed registration.
     * <p>
     * When set on a concrete class, it registers itself with the type mapping
     * registry identified by typeMapId.
     */
    public String getTypeDiscriminator() {
        return typeDiscriminator;
    }

    /**
     * Returns the type mappings (discriminator value → EClass URI).
     * <p>
     * Used for centralized configuration where all mappings are defined
     * on the base class.
     *
     * @return unmodifiable map of discriminator values to EClass URIs
     */
    public Map<String, String> getTypeMappings() {
        return typeMappings;
    }

    // ========================================================================
    // Accessors - Inline Mapping (EReference level)
    // ========================================================================

    /**
     * Returns the inline mappings (discriminator value → EClass URI).
     * <p>
     * Used for per-reference type mappings defined directly on the EReference.
     *
     * @return unmodifiable map of discriminator values to EClass URIs
     */
    public Map<String, String> getInlineMappings() {
        return inlineMappings;
    }

    // ========================================================================
    // Accessors - Common properties
    // ========================================================================

    /**
     * Returns the fallback strategy when discriminator value cannot be resolved.
     * <p>
     * Default: SKIP (enables graceful continuation to Type Strategy resolution)
     */
    public FallbackStrategy getFallbackStrategy() {
        return fallbackStrategy;
    }

    /**
     * Returns the explicit fallback EClass URI.
     * <p>
     * Used when fallbackStrategy=FALLBACK and the discriminator value
     * cannot be resolved through the mappings.
     */
    public String getFallbackEClass() {
        return fallbackEClass;
    }

    // ========================================================================
    // Computed properties
    // ========================================================================

    /**
     * Returns whether this config has type mapping registry configuration.
     */
    public boolean hasTypeMappingRegistry() {
        return typeMapId != null || !typeMappings.isEmpty() || typeDiscriminatorPath != null;
    }

    /**
     * Returns whether this config has inline mapping configuration.
     */
    public boolean hasInlineMapping() {
        return !inlineMappings.isEmpty();
    }

    /**
     * Returns whether this class is registered with a type mapping registry.
     */
    public boolean isRegisteredWithRegistry() {
        return typeMapId != null && typeDiscriminator != null;
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
    public DiscriminatorConfig mergeWith(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return this;
        }

        // For mappings, we merge the maps (source entries override base entries)
        Map<String, String> mergedTypeMappings = mergeMaps(
                this.typeMappings,
                getMap(source, ConfigProperty.TYPE_MAPPINGS, null));
        Map<String, String> mergedInlineMappings = mergeMaps(
                this.inlineMappings,
                getMap(source, ConfigProperty.INLINE_MAPPINGS, null));

        return toBuilder()
                .typeMapId(getString(source, ConfigProperty.TYPE_MAP_ID, this.typeMapId))
                .typeDiscriminatorPath(getString(source, ConfigProperty.TYPE_DISCRIMINATOR_PATH, this.typeDiscriminatorPath))
                .typeDiscriminator(getString(source, ConfigProperty.TYPE_DISCRIMINATOR, this.typeDiscriminator))
                .typeMappings(mergedTypeMappings)
                .inlineMappings(mergedInlineMappings)
                .fallbackStrategy(getEnum(source, ConfigProperty.FALLBACK_STRATEGY, FallbackStrategy.class, this.fallbackStrategy))
                .fallbackEClass(getString(source, ConfigProperty.FALLBACK_ECLASS, this.fallbackEClass))
                .build();
    }

    private Map<String, String> mergeMaps(Map<String, String> base, Map<String, String> override) {
        if (override == null || override.isEmpty()) {
            return base;
        }
        if (base == null || base.isEmpty()) {
            return override;
        }
        Map<String, String> merged = new HashMap<>(base);
        merged.putAll(override);
        return merged;
    }

    // ========================================================================
    // Validation support
    // ========================================================================

    /**
     * Validates this config and returns a validated (possibly normalized) config.
     * <p>
     * Discriminator config constraints:
     * <ul>
     *   <li>typeDiscriminator requires typeMapId (for distributed registration)</li>
     *   <li>typeMappings requires typeMapId (registry identification)</li>
     *   <li>typeDiscriminatorPath requires typeMapId (registry identification)</li>
     *   <li>typeDiscriminatorPath should be set when typeMappings is non-empty</li>
     *   <li>fallbackEClass only meaningful when fallbackStrategy=FALLBACK</li>
     * </ul>
     */
    @Override
    public DiscriminatorConfig validate(DiagnosticCollector diagnostics) {
        // Constraint: typeDiscriminator requires typeMapId
        if (typeDiscriminator != null && typeMapId == null) {
            diagnostics.addError(
                "typeDiscriminator requires typeMapId for distributed registration",
                "DiscriminatorConfig.validate");
        }

        // Constraint: typeMappings requires typeMapId
        if (!typeMappings.isEmpty() && typeMapId == null) {
            diagnostics.addError(
                "typeMappings requires typeMapId for registry identification",
                "DiscriminatorConfig.validate");
        }

        // Constraint: typeDiscriminatorPath requires typeMapId
        if (typeDiscriminatorPath != null && typeMapId == null) {
            diagnostics.addError(
                "typeDiscriminatorPath requires typeMapId for registry identification",
                "DiscriminatorConfig.validate");
        }

        // Constraint: typeMappings without typeDiscriminatorPath is likely a misconfiguration
        if (!typeMappings.isEmpty() && typeDiscriminatorPath == null) {
            diagnostics.addWarning(
                "typeMappings is set but typeDiscriminatorPath is not set",
                "DiscriminatorConfig.validate");
        }

        // Constraint: fallbackEClass only meaningful when fallbackStrategy=FALLBACK
        if (fallbackStrategy != FallbackStrategy.FALLBACK && fallbackEClass != null) {
            diagnostics.addWarning(
                "fallbackEClass is ignored when fallbackStrategy is not FALLBACK",
                "DiscriminatorConfig.validate");
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
                .typeMapId(this.typeMapId)
                .typeDiscriminatorPath(this.typeDiscriminatorPath)
                .typeDiscriminator(this.typeDiscriminator)
                .typeMappings(this.typeMappings)
                .inlineMappings(this.inlineMappings)
                .fallbackStrategy(this.fallbackStrategy)
                .fallbackEClass(this.fallbackEClass);
    }

    /**
     * Creates a config with all default values.
     */
    public static DiscriminatorConfig defaults() {
        return builder().build();
    }

    public static final class Builder {
        private String typeMapId = null;
        private String typeDiscriminatorPath = null;
        private String typeDiscriminator = null;
        private Map<String, String> typeMappings = null;
        private Map<String, String> inlineMappings = null;
        private FallbackStrategy fallbackStrategy = FallbackStrategy.SKIP;  // Spec default: SKIP
        private String fallbackEClass = null;

        private Builder() {}

        public Builder typeMapId(String typeMapId) {
            this.typeMapId = typeMapId;
            return this;
        }

        public Builder typeDiscriminatorPath(String typeDiscriminatorPath) {
            this.typeDiscriminatorPath = typeDiscriminatorPath;
            return this;
        }

        public Builder typeDiscriminator(String typeDiscriminator) {
            this.typeDiscriminator = typeDiscriminator;
            return this;
        }

        public Builder typeMappings(Map<String, String> typeMappings) {
            this.typeMappings = typeMappings;
            return this;
        }

        public Builder addTypeMapping(String discriminatorValue, String eClassUri) {
            if (this.typeMappings == null) {
                this.typeMappings = new HashMap<>();
            }
            this.typeMappings.put(discriminatorValue, eClassUri);
            return this;
        }

        public Builder inlineMappings(Map<String, String> inlineMappings) {
            this.inlineMappings = inlineMappings;
            return this;
        }

        public Builder addInlineMapping(String discriminatorValue, String eClassUri) {
            if (this.inlineMappings == null) {
                this.inlineMappings = new HashMap<>();
            }
            this.inlineMappings.put(discriminatorValue, eClassUri);
            return this;
        }

        public Builder fallbackStrategy(FallbackStrategy fallbackStrategy) {
            this.fallbackStrategy = fallbackStrategy;
            return this;
        }

        public Builder fallbackEClass(String fallbackEClass) {
            this.fallbackEClass = fallbackEClass;
            return this;
        }

        public DiscriminatorConfig build() {
            return new DiscriminatorConfig(this);
        }
    }
}
