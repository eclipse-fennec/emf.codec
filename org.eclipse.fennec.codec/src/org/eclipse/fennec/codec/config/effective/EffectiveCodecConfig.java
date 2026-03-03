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
package org.eclipse.fennec.codec.config.effective;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.ClassConfig;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.DiscriminatorConfig;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorReader;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataService;

/**
 * Effective codec configuration for a single serialization/deserialization operation.
 * <p>
 * This class bridges the new API config system ({@link ConfigurationResolver}) with the
 * codec context system. It is created once at Resource.save()/load() time and passed
 * through the context to serializers/deserializers.
 * </p>
 * <p>
 * Configuration resolution is delegated to {@link ConfigurationResolver}, which implements
 * the two-dimensional configuration model:
 * <ul>
 *   <li><b>Source Hierarchy (Vertical)</b>: OPTIONS → RESOURCE → FACTORY → MODULE → ANNOTATION → DEFAULT</li>
 *   <li><b>Scope Chain (Horizontal)</b>: FEATURE → ECLASS → GLOBAL</li>
 * </ul>
 * </p>
 * <p>
 * This class also holds operation-scoped state that doesn't belong in the resolver:
 * <ul>
 *   <li>{@link MetadataService} — for EClass/EPackage metadata lookups</li>
 *   <li>{@link DiagnosticCollector} — for collecting warnings/errors</li>
 *   <li>{@link CodecValueRegistry} — for custom value readers/writers</li>
 *   <li>{@link TypeDiscriminatorReader} — for MAPPED strategy resolution</li>
 *   <li>Global flags like smart compression, sort alphabetically, etc.</li>
 * </ul>
 * </p>
 *
 * @see ConfigurationResolver
 * @see <a href="docs/codec-v2-spec/02-config-resolution.md">Spec: Configuration Resolution</a>
 * @author Mark Hoffmann
 * @since 2026-02-01
 */
public final class EffectiveCodecConfig
        implements org.eclipse.fennec.codec.value.EffectiveCodecConfig {

    private final ConfigurationResolver resolver;
    private final DiagnosticCollector diagnostics;
    private final MetadataService metadataService;
    private final TypeDiscriminatorReader typeDiscriminatorService;
    private final CodecValueRegistry valueRegistry;

    // Global settings (not per-class)
    private final List<String> globalIgnoreFeatures;
    private final boolean sortPropertiesAlphabetically;
    private final boolean smartCompression;
    private final boolean useNamesFromExtendedMetaData;
    private final Map<String, Object> customProperties;

    // Expand settings
    private final boolean expandGlobal;
    private final Set<EReference> expandReferences;
    private final Set<String> expandReferenceNames;
    private final int expandDepth;
    private final boolean expandIgnoreBidirectional;

    private EffectiveCodecConfig(Builder builder) {
        this.resolver = requireNonNull(builder.resolver, "resolver must not be null");
        this.diagnostics = requireNonNull(builder.diagnostics, "diagnostics must not be null");
        this.metadataService = builder.metadataService;
        this.typeDiscriminatorService = builder.typeDiscriminatorService;
        this.valueRegistry = builder.valueRegistry;
        this.globalIgnoreFeatures = builder.globalIgnoreFeatures != null
                ? List.copyOf(builder.globalIgnoreFeatures) : List.of();
        this.sortPropertiesAlphabetically = builder.sortPropertiesAlphabetically;
        this.smartCompression = builder.smartCompression;
        this.useNamesFromExtendedMetaData = builder.useNamesFromExtendedMetaData;
        this.customProperties = builder.customProperties != null
                ? Map.copyOf(builder.customProperties) : Map.of();
        this.expandGlobal = builder.expandGlobal;
        this.expandReferences = builder.expandReferences != null
                ? Set.copyOf(builder.expandReferences) : Collections.emptySet();
        this.expandReferenceNames = builder.expandReferenceNames != null
                ? Set.copyOf(builder.expandReferenceNames) : Collections.emptySet();
        this.expandDepth = builder.expandDepth;
        this.expandIgnoreBidirectional = builder.expandIgnoreBidirectional;
    }

    // ========================================================================
    // Config Resolution (delegated to ConfigurationResolver)
    // ========================================================================

    /**
     * Resolves effective TypeConfig for an EClass.
     *
     * @param eClass the EClass
     * @return the effective TypeConfig (cached by resolver)
     */
    public TypeConfig resolveTypeConfig(EClass eClass) {
        return resolver.resolveTypeConfig(eClass, diagnostics);
    }

    /**
     * Resolves global TypeConfig (no EClass context).
     *
     * @return the effective global TypeConfig
     */
    public TypeConfig resolveGlobalTypeConfig() {
        return resolver.resolveGlobalTypeConfig(diagnostics);
    }

    /**
     * Resolves effective IdConfig for an EClass.
     *
     * @param eClass the EClass
     * @return the effective IdConfig (cached by resolver)
     */
    public IdConfig resolveIdConfig(EClass eClass) {
        return resolver.resolveIdConfig(eClass, diagnostics);
    }

    /**
     * Resolves global IdConfig (no EClass context).
     *
     * @return the effective global IdConfig
     */
    public IdConfig resolveGlobalIdConfig() {
        return resolver.resolveGlobalIdConfig(diagnostics);
    }

    /**
     * Resolves effective SuperTypeConfig for an EClass.
     *
     * @param eClass the EClass
     * @return the effective SuperTypeConfig (cached by resolver)
     */
    public SuperTypeConfig resolveSuperTypeConfig(EClass eClass) {
        return resolver.resolveSuperTypeConfig(eClass, diagnostics);
    }

    /**
     * Resolves global SuperTypeConfig (no EClass context).
     *
     * @return the effective global SuperTypeConfig
     */
    public SuperTypeConfig resolveGlobalSuperTypeConfig() {
        return resolver.resolveGlobalSuperTypeConfig(diagnostics);
    }

    /**
     * Resolves effective DiscriminatorConfig for an EClass.
     *
     * @param eClass the EClass
     * @return the effective DiscriminatorConfig (cached by resolver)
     */
    public DiscriminatorConfig resolveDiscriminatorConfig(EClass eClass) {
        return resolver.resolveDiscriminatorConfig(eClass, diagnostics);
    }

    /**
     * Resolves global DiscriminatorConfig (no EClass context).
     *
     * @return the effective global DiscriminatorConfig
     */
    public DiscriminatorConfig resolveGlobalDiscriminatorConfig() {
        return resolver.resolveGlobalDiscriminatorConfig(diagnostics);
    }

    /**
     * Resolves effective ClassConfig for an EClass.
     * <p>
     * ClassConfig controls strictness behavior during deserialization:
     * <ul>
     *   <li>{@code strictOnUnknown}: ERROR on unknown JSON field (default: false)</li>
     *   <li>{@code strictOnMissing}: ERROR on missing required feature (default: false)</li>
     * </ul>
     *
     * @param eClass the EClass
     * @return the effective ClassConfig (cached by resolver)
     */
    public ClassConfig resolveClassConfig(EClass eClass) {
        return resolver.resolveClassConfig(eClass, diagnostics);
    }

    /**
     * Resolves global ClassConfig (no EClass context).
     *
     * @return the effective global ClassConfig
     */
    public ClassConfig resolveGlobalClassConfig() {
        return resolver.resolveGlobalClassConfig(diagnostics);
    }

    /**
     * Resolves effective FeatureConfig for an EStructuralFeature.
     *
     * @param feature the EStructuralFeature
     * @return the effective FeatureConfig (cached by resolver)
     */
    public FeatureConfig resolveFeatureConfig(EStructuralFeature feature) {
        return resolver.resolveFeatureConfig(feature, diagnostics);
    }

    /**
     * Resolves global FeatureConfig (no feature context).
     *
     * @return the effective global FeatureConfig
     */
    public FeatureConfig resolveGlobalFeatureConfig() {
        return resolver.resolveGlobalFeatureConfig(diagnostics);
    }

    /**
     * Resolves effective ReferenceConfig for an EStructuralFeature.
     *
     * @param feature the EReference (as EStructuralFeature)
     * @return the effective ReferenceConfig (cached by resolver)
     */
    public ReferenceConfig resolveReferenceConfig(EStructuralFeature feature) {
        return resolver.resolveReferenceConfig(feature, diagnostics);
    }

    /**
     * Resolves global ReferenceConfig (no feature context).
     *
     * @return the effective global ReferenceConfig
     */
    public ReferenceConfig resolveGlobalReferenceConfig() {
        return resolver.resolveGlobalReferenceConfig(diagnostics);
    }

    /**
     * Resolves and validates both TypeConfig and SuperTypeConfig for an EClass.
     * <p>
     * Performs cross-config validation (e.g., STRUCTURED + NONE + superTypeSerialize).
     * </p>
     *
     * @param eClass the EClass
     * @return the resolved TypeConfig
     */
    public TypeConfig resolveTypeAndSuperTypeConfig(EClass eClass) {
        return resolver.resolveTypeAndSuperTypeConfig(eClass, diagnostics);
    }

    // ========================================================================
    // Global Settings
    // ========================================================================

    /**
     * Returns the metadata service for EClass resolution and aspect lookups.
     *
     * @return the metadata service, or null if not configured
     */
    public MetadataService getMetadataService() {
        return metadataService;
    }

    /**
     * Convenience method to get ClassMetadata for an EClass.
     *
     * @param eClass the EClass
     * @return the class metadata, or null if not found or no metadata service
     */
    public ClassMetadata getClassMetadata(EClass eClass) {
        return metadataService != null ? metadataService.getClassMetadata(eClass) : null;
    }

    /**
     * Resolves an EClass from its URI string using the metadata service.
     *
     * @param uri the EClass URI (e.g., "http://example.org/model#//MyClass")
     * @return the resolved EClass, or null if not found or no metadata service
     */
    public EClass resolveEClassByURI(String uri) {
        if (uri == null || uri.isEmpty() || metadataService == null) {
            return null;
        }
        ClassMetadata metadata = metadataService.getClassMetadataByURI(uri);
        return metadata != null ? metadata.getEClass() : null;
    }

    /**
     * Returns the type discriminator service for MAPPED strategy resolution.
     *
     * @return the discriminator service, or null if not configured
     */
    public TypeDiscriminatorReader getTypeDiscriminatorReader() {
        return typeDiscriminatorService;
    }

    /**
     * Returns the diagnostic collector for this operation.
     *
     * @return the diagnostic collector
     */
    public DiagnosticCollector getDiagnostics() {
        return diagnostics;
    }

    /**
     * Returns the underlying configuration resolver.
     *
     * @return the configuration resolver
     */
    public ConfigurationResolver getResolver() {
        return resolver;
    }

    /**
     * Returns the registry for custom value readers and writers.
     *
     * @return the value registry, or null if not configured
     */
    public CodecValueRegistry getValueRegistry() {
        return valueRegistry;
    }

    /**
     * Gets a custom value writer by name from the registry.
     *
     * @param <T> the value type
     * @param <F> the feature type
     * @param name the writer name
     * @return the writer, or null if not found or no registry
     */
    @SuppressWarnings("unchecked")
    public <T, F extends EStructuralFeature> CodecValueWriter<T, F> getValueWriter(String name) {
        if (valueRegistry == null || name == null || name.isEmpty()) {
            return null;
        }
        return (CodecValueWriter<T, F>) valueRegistry.getWriter(name).orElse(null);
    }

    /**
     * Gets a custom value reader by name from the registry.
     *
     * @param <T> the value type
     * @param <F> the feature type
     * @param name the reader name
     * @return the reader, or null if not found or no registry
     */
    @SuppressWarnings("unchecked")
    public <T, F extends EStructuralFeature> CodecValueReader<T, F> getValueReader(String name) {
        if (valueRegistry == null || name == null || name.isEmpty()) {
            return null;
        }
        return (CodecValueReader<T, F>) valueRegistry.getReader(name).orElse(null);
    }

    /**
     * Returns the list of globally ignored feature names.
     */
    public List<String> getGlobalIgnoreFeatures() {
        return globalIgnoreFeatures;
    }

    /**
     * Checks if a feature name is globally ignored.
     */
    public boolean isGloballyIgnored(String featureName) {
        return featureName != null && globalIgnoreFeatures.contains(featureName);
    }

    /**
     * Returns whether JSON properties should be sorted alphabetically.
     */
    public boolean isSortPropertiesAlphabetically() {
        return sortPropertiesAlphabetically;
    }

    /**
     * Returns whether smart compression is enabled.
     * <p>
     * When enabled, type information (_type) is omitted when it can be inferred
     * from the reference declaration (instance type == reference type).
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/04-global-options.md#1-smart-compression">Spec: Smart Compression</a>
     */
    public boolean isSmartCompression() {
        return smartCompression;
    }

    // ========================================================================
    // Interface Implementation (org.eclipse.fennec.codec.value.EffectiveCodecConfig)
    // ========================================================================

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link #resolveGlobalTypeConfig()}.
     */
    @Override
    public TypeConfig getTypeConfig() {
        return resolveGlobalTypeConfig();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link #resolveGlobalSuperTypeConfig()}.
     */
    @Override
    public SuperTypeConfig getSuperTypeConfig() {
        return resolveGlobalSuperTypeConfig();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link #resolveGlobalIdConfig()}.
     */
    @Override
    public IdConfig getIdConfig() {
        return resolveGlobalIdConfig();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link #resolveGlobalDiscriminatorConfig()}.
     */
    @Override
    public DiscriminatorConfig getDiscriminatorConfig() {
        return resolveGlobalDiscriminatorConfig();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link #isSmartCompression()}.
     */
    @Override
    public boolean isSmartCompressionEnabled() {
        return isSmartCompression();
    }

    /**
     * Returns whether to use names from ExtendedMetaData annotations.
     */
    public boolean isUseNamesFromExtendedMetaData() {
        return useNamesFromExtendedMetaData;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    // ========================================================================
    // Expand Settings
    // ========================================================================

    /**
     * Returns whether all non-containment references should be expanded globally.
     */
    public boolean isExpandGlobal() {
        return expandGlobal;
    }

    /**
     * Returns the set of specific EReferences to expand.
     */
    public Set<EReference> getExpandReferences() {
        return expandReferences;
    }

    /**
     * Returns the set of reference names to expand.
     */
    public Set<String> getExpandReferenceNames() {
        return expandReferenceNames;
    }

    /**
     * Checks if a specific EReference should be expanded.
     *
     * @param reference the EReference to check
     * @return true if the reference should be expanded
     */
    public boolean shouldExpand(EReference reference) {
        if (reference == null) {
            return false;
        }
        if (expandGlobal) {
            return true;
        }
        if (expandReferences.contains(reference)) {
            return true;
        }
        return expandReferenceNames.contains(reference.getName());
    }

    /**
     * Returns the maximum depth for nested expansion.
     */
    public int getExpandDepth() {
        return expandDepth;
    }

    /**
     * Returns whether bi-directional references should be ignored during expansion.
     */
    public boolean isExpandIgnoreBidirectional() {
        return expandIgnoreBidirectional;
    }

    // ========================================================================
    // Cache Management
    // ========================================================================

    /**
     * Clears all cached configurations in the underlying resolver.
     */
    public void clearCaches() {
        resolver.clearCaches();
    }

    // ========================================================================
    // Builder
    // ========================================================================

    /**
     * Creates a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for EffectiveCodecConfig.
     */
    public static final class Builder {
        private ConfigurationResolver resolver;
        private DiagnosticCollector diagnostics;
        private MetadataService metadataService;
        private TypeDiscriminatorReader typeDiscriminatorService;
        private CodecValueRegistry valueRegistry;
        private List<String> globalIgnoreFeatures;
        private boolean sortPropertiesAlphabetically = false;
        private boolean smartCompression = false;
        private boolean useNamesFromExtendedMetaData = false;
        private Map<String, Object> customProperties;
        private boolean expandGlobal = false;
        private Set<EReference> expandReferences;
        private Set<String> expandReferenceNames;
        private int expandDepth = 1;
        private boolean expandIgnoreBidirectional = true;

        private Builder() {}

        public Builder resolver(ConfigurationResolver resolver) {
            this.resolver = resolver;
            return this;
        }

        public Builder diagnostics(DiagnosticCollector diagnostics) {
            this.diagnostics = diagnostics;
            return this;
        }

        public Builder metadataService(MetadataService metadataService) {
            this.metadataService = metadataService;
            return this;
        }

        public Builder typeDiscriminatorService(TypeDiscriminatorReader typeDiscriminatorService) {
            this.typeDiscriminatorService = typeDiscriminatorService;
            return this;
        }

        public Builder valueRegistry(CodecValueRegistry valueRegistry) {
            this.valueRegistry = valueRegistry;
            return this;
        }

        public Builder globalIgnoreFeatures(List<String> globalIgnoreFeatures) {
            this.globalIgnoreFeatures = globalIgnoreFeatures;
            return this;
        }

        public Builder sortPropertiesAlphabetically(boolean sortPropertiesAlphabetically) {
            this.sortPropertiesAlphabetically = sortPropertiesAlphabetically;
            return this;
        }

        public Builder smartCompression(boolean smartCompression) {
            this.smartCompression = smartCompression;
            return this;
        }

        public Builder useNamesFromExtendedMetaData(boolean useNamesFromExtendedMetaData) {
            this.useNamesFromExtendedMetaData = useNamesFromExtendedMetaData;
            return this;
        }

        public Builder customProperties(Map<String, Object> customProperties) {
            this.customProperties = customProperties;
            return this;
        }

        public Builder expandGlobal(boolean expandGlobal) {
            this.expandGlobal = expandGlobal;
            return this;
        }

        public Builder expandReferences(Set<EReference> expandReferences) {
            this.expandReferences = expandReferences;
            return this;
        }

        public Builder expandReferenceNames(Set<String> expandReferenceNames) {
            this.expandReferenceNames = expandReferenceNames;
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

        public EffectiveCodecConfig build() {
            return new EffectiveCodecConfig(this);
        }
    }
}
