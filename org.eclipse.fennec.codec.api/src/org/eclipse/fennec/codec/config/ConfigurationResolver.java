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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.TypeStrategy;

/**
 * Resolves effective configuration by merging all configuration sources.
 * <p>
 * This is the SINGLE POINT where configuration resolution is orchestrated.
 * It implements the two-dimensional configuration model from the spec:
 * <ul>
 *   <li><b>Source Hierarchy (Vertical)</b>: OPTIONS → RESOURCE → FACTORY → MODULE → ANNOTATION → DEFAULT</li>
 *   <li><b>Scope Chain (Horizontal)</b>: FEATURE → ECLASS → GLOBAL</li>
 * </ul>
 * <p>
 * Usage pattern:
 * <pre>
 * ConfigurationResolver resolver = ConfigurationResolver.builder()
 *     .annotationProperties(annotationMap)
 *     .moduleProperties(moduleMap)
 *     .factoryProperties(factoryMap)
 *     .resourceProperties(resourceMap)
 *     .optionsProperties(optionsMap)
 *     .build();
 *
 * // Get effective configs (cached)
 * TypeConfig typeConfig = resolver.resolveTypeConfig(eClass, diagnostics);
 * FeatureConfig featureConfig = resolver.resolveFeatureConfig(feature, diagnostics);
 * </pre>
 * <p>
 * The resolver caches effective configurations per EClass/EStructuralFeature for efficiency.
 *
 * @see ConfigSource
 * @see ConfigLevel
 * @see Mergeable
 */
public final class ConfigurationResolver {

    // Configuration sources (lower index = higher priority)
    private final Map<String, Object> optionsProperties;      // Level 1 - highest priority
    private final Map<String, Object> resourceProperties;     // Level 2
    private final Map<String, Object> factoryProperties;      // Level 3
    private final Map<String, Object> moduleProperties;       // Level 4
    private final Map<String, Object> annotationProperties;   // Level 5
    // Level 6 (defaults) is built into config classes

    // Caches for resolved configurations
    private final ConcurrentHashMap<EClass, TypeConfig> typeConfigCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<EClass, SuperTypeConfig> superTypeConfigCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<EClass, IdConfig> idConfigCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<EClass, DiscriminatorConfig> discriminatorConfigCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<EClass, ClassConfig> classConfigCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<EStructuralFeature, FeatureConfig> featureConfigCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<EStructuralFeature, ReferenceConfig> referenceConfigCache = new ConcurrentHashMap<>();

    // Global config cache (singleton per resolver)
    private volatile TypeConfig globalTypeConfig;
    private volatile SuperTypeConfig globalSuperTypeConfig;
    private volatile IdConfig globalIdConfig;
    private volatile DiscriminatorConfig globalDiscriminatorConfig;
    private volatile ClassConfig globalClassConfig;
    private volatile FeatureConfig globalFeatureConfig;
    private volatile ReferenceConfig globalReferenceConfig;

    // Force write/read feature sets (from builder convenience methods)
    private final Set<EStructuralFeature> forceWriteFeatures;
    private final Set<EStructuralFeature> forceReadFeatures;

    private ConfigurationResolver(Builder builder) {
        this.optionsProperties = builder.optionsProperties;
        this.resourceProperties = builder.resourceProperties;
        this.factoryProperties = builder.factoryProperties;
        this.moduleProperties = builder.moduleProperties;
        this.annotationProperties = builder.annotationProperties;
        this.forceWriteFeatures = builder.forceWriteFeatures != null
                ? Set.copyOf(builder.forceWriteFeatures)
                : Set.of();
        this.forceReadFeatures = builder.forceReadFeatures != null
                ? Set.copyOf(builder.forceReadFeatures)
                : Set.of();
    }

    // ========================================================================
    // Type Configuration Resolution
    // ========================================================================

    /**
     * Resolves effective TypeConfig for an EClass.
     * <p>
     * Resolution order (first non-null wins at each property):
     * <ol>
     *   <li>OPTIONS: EClass-scoped, then GLOBAL-scoped</li>
     *   <li>RESOURCE: EClass-scoped, then GLOBAL-scoped</li>
     *   <li>FACTORY: EClass-scoped, then GLOBAL-scoped</li>
     *   <li>MODULE: EClass-scoped, then GLOBAL-scoped</li>
     *   <li>ANNOTATION: from EClass annotation</li>
     *   <li>DEFAULT: built-in defaults</li>
     * </ol>
     *
     * @param eClass the EClass to resolve config for
     * @param diagnostics collector for validation diagnostics
     * @return the effective TypeConfig (cached)
     */
    public TypeConfig resolveTypeConfig(EClass eClass, DiagnosticCollector diagnostics) {
        Objects.requireNonNull(eClass, "eClass must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        return typeConfigCache.computeIfAbsent(eClass, ec -> {
            // Start with defaults, merge in reverse priority order
            // Annotation layer: walk up the EClass hierarchy (parents first, then child overrides)
            TypeConfig resolved = TypeConfig.defaults()
                    .mergeWith(extractGlobalProperties(annotationProperties));
            for (EClass superType : ec.getEAllSuperTypes()) {
                resolved = resolved.mergeWith(extractClassProperties(annotationProperties, superType));
            }
            return resolved
                    .mergeWith(extractClassProperties(annotationProperties, ec))
                    .mergeWith(extractGlobalProperties(moduleProperties))
                    .mergeWith(extractClassProperties(moduleProperties, ec))
                    .mergeWith(extractGlobalProperties(factoryProperties))
                    .mergeWith(extractClassProperties(factoryProperties, ec))
                    .mergeWith(extractGlobalProperties(resourceProperties))
                    .mergeWith(extractClassProperties(resourceProperties, ec))
                    .mergeWith(extractGlobalProperties(optionsProperties))
                    .mergeWith(extractClassProperties(optionsProperties, ec))
                    .validate(diagnostics);
        });
    }

    /**
     * Resolves effective TypeConfig for an EClass in the context of a containing feature.
     * <p>
     * Extends the standard EClass-level resolution by also merging feature-scoped
     * properties (e.g., per-EReference type strategy overrides via {@code CODEC_EREFERENCE_CONFIG}).
     * The feature-level properties have the highest priority within each configuration source.
     * </p>
     *
     * @param eClass the EClass to resolve config for
     * @param feature the containing feature (e.g., the EReference through which this object is accessed)
     * @param diagnostics collector for validation diagnostics
     * @return the effective TypeConfig with feature-level overrides applied
     */
    public TypeConfig resolveTypeConfig(EClass eClass, EStructuralFeature feature, DiagnosticCollector diagnostics) {
        Objects.requireNonNull(eClass, "eClass must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        if (feature == null) {
            return resolveTypeConfig(eClass, diagnostics);
        }

        // Start from the EClass-level resolved config, then layer feature overrides on top.
        // Feature-level properties have highest priority within each source.
        return resolveTypeConfig(eClass, diagnostics)
                .mergeWith(extractFeatureProperties(annotationProperties, feature))
                .mergeWith(extractFeatureProperties(moduleProperties, feature))
                .mergeWith(extractFeatureProperties(factoryProperties, feature))
                .mergeWith(extractFeatureProperties(resourceProperties, feature))
                .mergeWith(extractFeatureProperties(optionsProperties, feature))
                .validate(diagnostics);
    }

    /**
     * Resolves global TypeConfig (no EClass context).
     *
     * @param diagnostics collector for validation diagnostics
     * @return the effective global TypeConfig
     */
    public TypeConfig resolveGlobalTypeConfig(DiagnosticCollector diagnostics) {
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        if (globalTypeConfig == null) {
            synchronized (this) {
                if (globalTypeConfig == null) {
                    globalTypeConfig = TypeConfig.defaults()
                            .mergeWith(extractGlobalProperties(annotationProperties))
                            .mergeWith(extractGlobalProperties(moduleProperties))
                            .mergeWith(extractGlobalProperties(factoryProperties))
                            .mergeWith(extractGlobalProperties(resourceProperties))
                            .mergeWith(extractGlobalProperties(optionsProperties))
                            .validate(diagnostics);
                }
            }
        }
        return globalTypeConfig;
    }

    // ========================================================================
    // SuperType Configuration Resolution
    // ========================================================================

    /**
     * Resolves effective SuperTypeConfig for an EClass.
     *
     * @param eClass the EClass to resolve config for
     * @param diagnostics collector for validation diagnostics
     * @return the effective SuperTypeConfig (cached)
     */
    public SuperTypeConfig resolveSuperTypeConfig(EClass eClass, DiagnosticCollector diagnostics) {
        Objects.requireNonNull(eClass, "eClass must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        return superTypeConfigCache.computeIfAbsent(eClass, ec -> {
            return SuperTypeConfig.defaults()
                    .mergeWith(extractGlobalProperties(annotationProperties))
                    .mergeWith(extractClassProperties(annotationProperties, ec))
                    .mergeWith(extractGlobalProperties(moduleProperties))
                    .mergeWith(extractClassProperties(moduleProperties, ec))
                    .mergeWith(extractGlobalProperties(factoryProperties))
                    .mergeWith(extractClassProperties(factoryProperties, ec))
                    .mergeWith(extractGlobalProperties(resourceProperties))
                    .mergeWith(extractClassProperties(resourceProperties, ec))
                    .mergeWith(extractGlobalProperties(optionsProperties))
                    .mergeWith(extractClassProperties(optionsProperties, ec))
                    .validate(diagnostics);
        });
    }

    /**
     * Resolves global SuperTypeConfig (no EClass context).
     *
     * @param diagnostics collector for validation diagnostics
     * @return the effective global SuperTypeConfig
     */
    public SuperTypeConfig resolveGlobalSuperTypeConfig(DiagnosticCollector diagnostics) {
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        if (globalSuperTypeConfig == null) {
            synchronized (this) {
                if (globalSuperTypeConfig == null) {
                    globalSuperTypeConfig = SuperTypeConfig.defaults()
                            .mergeWith(extractGlobalProperties(annotationProperties))
                            .mergeWith(extractGlobalProperties(moduleProperties))
                            .mergeWith(extractGlobalProperties(factoryProperties))
                            .mergeWith(extractGlobalProperties(resourceProperties))
                            .mergeWith(extractGlobalProperties(optionsProperties))
                            .validate(diagnostics);
                }
            }
        }
        return globalSuperTypeConfig;
    }

    // ========================================================================
    // ID Configuration Resolution
    // ========================================================================

    /**
     * Resolves effective IdConfig for an EClass.
     *
     * @param eClass the EClass to resolve config for
     * @param diagnostics collector for validation diagnostics
     * @return the effective IdConfig (cached)
     */
    public IdConfig resolveIdConfig(EClass eClass, DiagnosticCollector diagnostics) {
        Objects.requireNonNull(eClass, "eClass must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        return idConfigCache.computeIfAbsent(eClass, ec -> {
            return IdConfig.defaults()
                    .mergeWith(extractGlobalProperties(annotationProperties))
                    .mergeWith(extractClassProperties(annotationProperties, ec))
                    .mergeWith(extractGlobalProperties(moduleProperties))
                    .mergeWith(extractClassProperties(moduleProperties, ec))
                    .mergeWith(extractGlobalProperties(factoryProperties))
                    .mergeWith(extractClassProperties(factoryProperties, ec))
                    .mergeWith(extractGlobalProperties(resourceProperties))
                    .mergeWith(extractClassProperties(resourceProperties, ec))
                    .mergeWith(extractGlobalProperties(optionsProperties))
                    .mergeWith(extractClassProperties(optionsProperties, ec))
                    .validate(diagnostics);
        });
    }

    /**
     * Resolves global IdConfig (no EClass context).
     *
     * @param diagnostics collector for validation diagnostics
     * @return the effective global IdConfig
     */
    public IdConfig resolveGlobalIdConfig(DiagnosticCollector diagnostics) {
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        if (globalIdConfig == null) {
            synchronized (this) {
                if (globalIdConfig == null) {
                    globalIdConfig = IdConfig.defaults()
                            .mergeWith(extractGlobalProperties(annotationProperties))
                            .mergeWith(extractGlobalProperties(moduleProperties))
                            .mergeWith(extractGlobalProperties(factoryProperties))
                            .mergeWith(extractGlobalProperties(resourceProperties))
                            .mergeWith(extractGlobalProperties(optionsProperties))
                            .validate(diagnostics);
                }
            }
        }
        return globalIdConfig;
    }

    // ========================================================================
    // Discriminator Configuration Resolution
    // ========================================================================

    /**
     * Resolves effective DiscriminatorConfig for an EClass.
     *
     * @param eClass the EClass to resolve config for
     * @param diagnostics collector for validation diagnostics
     * @return the effective DiscriminatorConfig (cached)
     */
    public DiscriminatorConfig resolveDiscriminatorConfig(EClass eClass, DiagnosticCollector diagnostics) {
        Objects.requireNonNull(eClass, "eClass must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        return discriminatorConfigCache.computeIfAbsent(eClass, ec -> {
            return DiscriminatorConfig.defaults()
                    .mergeWith(extractGlobalProperties(annotationProperties))
                    .mergeWith(extractClassProperties(annotationProperties, ec))
                    .mergeWith(extractGlobalProperties(moduleProperties))
                    .mergeWith(extractClassProperties(moduleProperties, ec))
                    .mergeWith(extractGlobalProperties(factoryProperties))
                    .mergeWith(extractClassProperties(factoryProperties, ec))
                    .mergeWith(extractGlobalProperties(resourceProperties))
                    .mergeWith(extractClassProperties(resourceProperties, ec))
                    .mergeWith(extractGlobalProperties(optionsProperties))
                    .mergeWith(extractClassProperties(optionsProperties, ec))
                    .validate(diagnostics);
        });
    }

    /**
     * Resolves global DiscriminatorConfig (no EClass context).
     *
     * @param diagnostics collector for validation diagnostics
     * @return the effective global DiscriminatorConfig
     */
    public DiscriminatorConfig resolveGlobalDiscriminatorConfig(DiagnosticCollector diagnostics) {
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        if (globalDiscriminatorConfig == null) {
            synchronized (this) {
                if (globalDiscriminatorConfig == null) {
                    globalDiscriminatorConfig = DiscriminatorConfig.defaults()
                            .mergeWith(extractGlobalProperties(annotationProperties))
                            .mergeWith(extractGlobalProperties(moduleProperties))
                            .mergeWith(extractGlobalProperties(factoryProperties))
                            .mergeWith(extractGlobalProperties(resourceProperties))
                            .mergeWith(extractGlobalProperties(optionsProperties))
                            .validate(diagnostics);
                }
            }
        }
        return globalDiscriminatorConfig;
    }

    // ========================================================================
    // Class Configuration Resolution (Strictness)
    // ========================================================================

    /**
     * Resolves effective ClassConfig for an EClass.
     * <p>
     * ClassConfig controls strictness behavior during deserialization:
     * <ul>
     *   <li>{@code strictOnUnknown}: ERROR on unknown JSON field (default: false)</li>
     *   <li>{@code strictOnMissing}: ERROR on missing required feature (default: false)</li>
     * </ul>
     *
     * @param eClass the EClass to resolve config for
     * @param diagnostics collector for validation diagnostics
     * @return the effective ClassConfig (cached)
     */
    public ClassConfig resolveClassConfig(EClass eClass, DiagnosticCollector diagnostics) {
        Objects.requireNonNull(eClass, "eClass must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        return classConfigCache.computeIfAbsent(eClass, ec -> {
            return ClassConfig.defaults()
                    .mergeWith(extractGlobalProperties(annotationProperties))
                    .mergeWith(extractClassProperties(annotationProperties, ec))
                    .mergeWith(extractGlobalProperties(moduleProperties))
                    .mergeWith(extractClassProperties(moduleProperties, ec))
                    .mergeWith(extractGlobalProperties(factoryProperties))
                    .mergeWith(extractClassProperties(factoryProperties, ec))
                    .mergeWith(extractGlobalProperties(resourceProperties))
                    .mergeWith(extractClassProperties(resourceProperties, ec))
                    .mergeWith(extractGlobalProperties(optionsProperties))
                    .mergeWith(extractClassProperties(optionsProperties, ec))
                    .validate(diagnostics);
        });
    }

    /**
     * Resolves global ClassConfig (no EClass context).
     *
     * @param diagnostics collector for validation diagnostics
     * @return the effective global ClassConfig
     */
    public ClassConfig resolveGlobalClassConfig(DiagnosticCollector diagnostics) {
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        if (globalClassConfig == null) {
            synchronized (this) {
                if (globalClassConfig == null) {
                    globalClassConfig = ClassConfig.defaults()
                            .mergeWith(extractGlobalProperties(annotationProperties))
                            .mergeWith(extractGlobalProperties(moduleProperties))
                            .mergeWith(extractGlobalProperties(factoryProperties))
                            .mergeWith(extractGlobalProperties(resourceProperties))
                            .mergeWith(extractGlobalProperties(optionsProperties))
                            .validate(diagnostics);
                }
            }
        }
        return globalClassConfig;
    }

    // ========================================================================
    // Feature Configuration Resolution
    // ========================================================================

    /**
     * Resolves effective FeatureConfig for an EStructuralFeature.
     * <p>
     * Resolution order:
     * <ol>
     *   <li>OPTIONS: Feature-scoped, then EClass-scoped, then GLOBAL-scoped</li>
     *   <li>RESOURCE: Feature-scoped, then EClass-scoped, then GLOBAL-scoped</li>
     *   <li>... (same pattern for all sources)</li>
     *   <li>ANNOTATION: from EStructuralFeature annotation, then EClass annotation</li>
     *   <li>DEFAULT: built-in defaults</li>
     * </ol>
     *
     * @param feature the EStructuralFeature to resolve config for
     * @param diagnostics collector for validation diagnostics
     * @return the effective FeatureConfig (cached)
     */
    public FeatureConfig resolveFeatureConfig(EStructuralFeature feature, DiagnosticCollector diagnostics) {
        Objects.requireNonNull(feature, "feature must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        return featureConfigCache.computeIfAbsent(feature, f -> {
            EClass eClass = f.getEContainingClass();
            FeatureConfig resolved = FeatureConfig.defaults()
                    .mergeWith(extractGlobalProperties(annotationProperties))
                    .mergeWith(extractClassProperties(annotationProperties, eClass))
                    .mergeWith(extractFeatureProperties(annotationProperties, f))
                    .mergeWith(extractGlobalProperties(moduleProperties))
                    .mergeWith(extractClassProperties(moduleProperties, eClass))
                    .mergeWith(extractFeatureProperties(moduleProperties, f))
                    .mergeWith(extractGlobalProperties(factoryProperties))
                    .mergeWith(extractClassProperties(factoryProperties, eClass))
                    .mergeWith(extractFeatureProperties(factoryProperties, f))
                    .mergeWith(extractGlobalProperties(resourceProperties))
                    .mergeWith(extractClassProperties(resourceProperties, eClass))
                    .mergeWith(extractFeatureProperties(resourceProperties, f))
                    .mergeWith(extractGlobalProperties(optionsProperties))
                    .mergeWith(extractClassProperties(optionsProperties, eClass))
                    .mergeWith(extractFeatureProperties(optionsProperties, f))
                    .validate(diagnostics);

            // Apply key when no explicit key was configured
            // (ConfigProperty.KEY default is null = "use feature name or ExtendedMetaData name")
            if (resolved.getKey() == null) {
                String key = resolveDefaultFeatureKey(f);
                resolved = resolved.toBuilder().key(key).build();
            }

            // Apply forceWrite/forceRead from builder convenience methods
            // These override any other settings for the specified features
            // When forceWrite is set, also clear the ignore flag so the feature is serialized
            if (forceWriteFeatures.contains(f)) {
                resolved = resolved.toBuilder().forceWrite(true).ignore(false).build();
            }
            if (forceReadFeatures.contains(f)) {
                resolved = resolved.toBuilder().forceRead(true).build();
            }

            // Apply global ignoreFeatures list
            List<String> globalIgnore = getGlobalProperty(ConfigProperty.IGNORE_FEATURES);
            if (globalIgnore != null && globalIgnore.contains(f.getName())) {
                if (!resolved.isForceWrite()) {
                    resolved = resolved.toBuilder().ignore(true).build();
                }
            }

            // Skip transient/derived/volatile features unless explicitly forced
            // This matches the old ConfigurationMerger.resolveFeatureSerialize() behavior
            // forceWrite enables serialization, forceRead enables deserialization
            if (f.isDerived() || f.isTransient() || f.isVolatile()) {
                if (!resolved.isForceWrite() && !resolved.isForceRead()) {
                    resolved = resolved.toBuilder().ignore(true).build();
                }
            }

            return resolved;
        });
    }

    /**
     * Resolves the default feature key when no explicit key is configured.
     * <p>
     * If useNamesFromExtendedMetaData is enabled, checks for ExtendedMetaData "name"
     * annotation on the feature. Otherwise, uses the feature name.
     * </p>
     *
     * @param feature the feature to resolve key for
     * @return the resolved key (never null)
     */
    private String resolveDefaultFeatureKey(EStructuralFeature feature) {
        // Check if ExtendedMetaData names should be used
        Boolean useExtendedMetaData = getGlobalProperty(ConfigProperty.USE_NAMES_FROM_EXTENDED_METADATA);
        if (Boolean.TRUE.equals(useExtendedMetaData)) {
            String extendedMetaDataName = getExtendedMetaDataName(feature);
            if (extendedMetaDataName != null && !extendedMetaDataName.isEmpty()) {
                return extendedMetaDataName;
            }
        }
        // Fall back to feature name
        return feature.getName();
    }

    /**
     * Gets the ExtendedMetaData name from a feature's annotation.
     * <p>
     * Looks for the annotation source "http:///org/eclipse/emf/ecore/util/ExtendedMetaData"
     * and retrieves the "name" detail.
     * </p>
     *
     * @param feature the feature to check
     * @return the ExtendedMetaData name, or null if not present
     */
    private static String getExtendedMetaDataName(EStructuralFeature feature) {
        EAnnotation annotation = feature.getEAnnotation(
                "http:///org/eclipse/emf/ecore/util/ExtendedMetaData");
        if (annotation != null) {
            return annotation.getDetails().get("name");
        }
        return null;
    }

    /**
     * Resolves global FeatureConfig (no Feature context).
     *
     * @param diagnostics collector for validation diagnostics
     * @return the effective global FeatureConfig
     */
    public FeatureConfig resolveGlobalFeatureConfig(DiagnosticCollector diagnostics) {
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        if (globalFeatureConfig == null) {
            synchronized (this) {
                if (globalFeatureConfig == null) {
                    globalFeatureConfig = FeatureConfig.defaults()
                            .mergeWith(extractGlobalProperties(annotationProperties))
                            .mergeWith(extractGlobalProperties(moduleProperties))
                            .mergeWith(extractGlobalProperties(factoryProperties))
                            .mergeWith(extractGlobalProperties(resourceProperties))
                            .mergeWith(extractGlobalProperties(optionsProperties))
                            .validate(diagnostics);
                }
            }
        }
        return globalFeatureConfig;
    }

    // ========================================================================
    // Reference Configuration Resolution
    // ========================================================================

    /**
     * Resolves effective ReferenceConfig for an EStructuralFeature (must be EReference).
     *
     * @param feature the EReference to resolve config for
     * @param diagnostics collector for validation diagnostics
     * @return the effective ReferenceConfig (cached)
     */
    public ReferenceConfig resolveReferenceConfig(EStructuralFeature feature, DiagnosticCollector diagnostics) {
        Objects.requireNonNull(feature, "feature must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        return referenceConfigCache.computeIfAbsent(feature, f -> {
            EClass eClass = f.getEContainingClass();
            return ReferenceConfig.defaults()
                    .mergeWith(extractGlobalProperties(annotationProperties))
                    .mergeWith(extractClassProperties(annotationProperties, eClass))
                    .mergeWith(extractFeatureProperties(annotationProperties, f))
                    .mergeWith(extractGlobalProperties(moduleProperties))
                    .mergeWith(extractClassProperties(moduleProperties, eClass))
                    .mergeWith(extractFeatureProperties(moduleProperties, f))
                    .mergeWith(extractGlobalProperties(factoryProperties))
                    .mergeWith(extractClassProperties(factoryProperties, eClass))
                    .mergeWith(extractFeatureProperties(factoryProperties, f))
                    .mergeWith(extractGlobalProperties(resourceProperties))
                    .mergeWith(extractClassProperties(resourceProperties, eClass))
                    .mergeWith(extractFeatureProperties(resourceProperties, f))
                    .mergeWith(extractGlobalProperties(optionsProperties))
                    .mergeWith(extractClassProperties(optionsProperties, eClass))
                    .mergeWith(extractFeatureProperties(optionsProperties, f))
                    .validate(diagnostics);
        });
    }

    /**
     * Resolves global ReferenceConfig (no Feature context).
     *
     * @param diagnostics collector for validation diagnostics
     * @return the effective global ReferenceConfig
     */
    public ReferenceConfig resolveGlobalReferenceConfig(DiagnosticCollector diagnostics) {
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        if (globalReferenceConfig == null) {
            synchronized (this) {
                if (globalReferenceConfig == null) {
                    globalReferenceConfig = ReferenceConfig.defaults()
                            .mergeWith(extractGlobalProperties(annotationProperties))
                            .mergeWith(extractGlobalProperties(moduleProperties))
                            .mergeWith(extractGlobalProperties(factoryProperties))
                            .mergeWith(extractGlobalProperties(resourceProperties))
                            .mergeWith(extractGlobalProperties(optionsProperties))
                            .validate(diagnostics);
                }
            }
        }
        return globalReferenceConfig;
    }

    // ========================================================================
    // Cross-Config Validation
    // ========================================================================

    /**
     * Validates cross-config constraints between TypeConfig and SuperTypeConfig.
     * <p>
     * Spec reference: 07-supertype.md section 6.0 "Configuration Constraints"
     * <p>
     * Constraints validated:
     * <ul>
     *   <li><b>STRUCTURED requires Type:</b> typeFormat=STRUCTURED + typeStrategy=NONE + superTypeSerialize=true → ERROR</li>
     *   <li><b>Custom reader conflict:</b> STRUCTURED + typeValueReaderName + superTypeValueReaderName → WARNING (superType reader ignored)</li>
     *   <li><b>Custom writer conflict:</b> STRUCTURED + typeValueWriterName + superTypeValueWriterName → WARNING (superType writer ignored)</li>
     * </ul>
     *
     * @param typeConfig the resolved TypeConfig
     * @param superTypeConfig the resolved SuperTypeConfig
     * @param diagnostics collector for validation diagnostics
     */
    public void validateCrossConfig(TypeConfig typeConfig, SuperTypeConfig superTypeConfig, DiagnosticCollector diagnostics) {
        Objects.requireNonNull(typeConfig, "typeConfig must not be null");
        Objects.requireNonNull(superTypeConfig, "superTypeConfig must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        // Determine effective format for supertype (inherits from type if not set)
        SerializationFormat effectiveFormat = superTypeConfig.getFormat() != null
                ? superTypeConfig.getFormat()
                : typeConfig.getFormat();

        // Constraint: STRUCTURED + NONE + superTypeSerialize=true → ERROR
        // Spec: "Cannot write supertype inside _type object when no _type is written"
        if (effectiveFormat == SerializationFormat.STRUCTURED
                && typeConfig.getStrategy() == TypeStrategy.NONE
                && superTypeConfig.isSerialize()) {
            diagnostics.addError(
                "Invalid configuration: typeFormat=STRUCTURED + typeStrategy=NONE + superTypeSerialize=true. " +
                "Cannot write supertype inside _type object when no _type is written.",
                "ConfigurationResolver.validateCrossConfig");
        }

        // Constraint: STRUCTURED + both custom readers → WARNING (superType reader ignored)
        // Spec: "In STRUCTURED format, when both are configured: superTypeValueReaderName is IGNORED"
        if (effectiveFormat == SerializationFormat.STRUCTURED
                && typeConfig.getValueReaderName() != null
                && superTypeConfig.getValueReaderName() != null) {
            diagnostics.addWarning(
                "superTypeValueReaderName is ignored when typeFormat=STRUCTURED and typeValueReaderName is set. " +
                "The type value reader handles the entire _type object including supertype.",
                "ConfigurationResolver.validateCrossConfig");
        }

        // Constraint: STRUCTURED + both custom writers → WARNING (superType writer ignored)
        // Spec: "In STRUCTURED format, when both are configured: superTypeValueWriterName is IGNORED"
        if (effectiveFormat == SerializationFormat.STRUCTURED
                && typeConfig.getValueWriterName() != null
                && superTypeConfig.getValueWriterName() != null) {
            diagnostics.addWarning(
                "superTypeValueWriterName is ignored when typeFormat=STRUCTURED and typeValueWriterName is set. " +
                "The type value writer handles the entire _type object including supertype.",
                "ConfigurationResolver.validateCrossConfig");
        }
    }

    /**
     * Resolves and validates both TypeConfig and SuperTypeConfig for an EClass.
     * <p>
     * This is a convenience method that resolves both configs and performs
     * cross-config validation in one call.
     *
     * @param eClass the EClass to resolve configs for
     * @param diagnostics collector for validation diagnostics
     * @return the resolved TypeConfig (SuperTypeConfig is resolved as side effect)
     */
    public TypeConfig resolveTypeAndSuperTypeConfig(EClass eClass, DiagnosticCollector diagnostics) {
        TypeConfig typeConfig = resolveTypeConfig(eClass, diagnostics);
        SuperTypeConfig superTypeConfig = resolveSuperTypeConfig(eClass, diagnostics);
        validateCrossConfig(typeConfig, superTypeConfig, diagnostics);
        return typeConfig;
    }

    // ========================================================================
    // Cache Management
    // ========================================================================

    /**
     * Clears all cached configurations.
     * <p>
     * Call this when underlying configuration sources have changed.
     */
    public void clearCaches() {
        typeConfigCache.clear();
        superTypeConfigCache.clear();
        idConfigCache.clear();
        discriminatorConfigCache.clear();
        classConfigCache.clear();
        featureConfigCache.clear();
        referenceConfigCache.clear();
        globalTypeConfig = null;
        globalSuperTypeConfig = null;
        globalIdConfig = null;
        globalDiscriminatorConfig = null;
        globalClassConfig = null;
        globalFeatureConfig = null;
        globalReferenceConfig = null;
    }

    // ========================================================================
    // Source Property Access
    // ========================================================================

    /**
     * Returns the options properties map (highest priority configuration source).
     *
     * @return the options properties map, or null if not set
     */
    public Map<String, Object> getOptionsProperties() {
        return optionsProperties;
    }

    /**
     * Retrieves a global property value by searching all configuration sources
     * in priority order (OPTIONS → RESOURCE → FACTORY → MODULE → ANNOTATION).
     * <p>
     * Returns the first non-null value found, or the property's default value
     * if no source has it set.
     * </p>
     *
     * @param <T> the property value type
     * @param property the config property to look up
     * @return the resolved value, or the property's default
     */
    @SuppressWarnings("unchecked")
    public <T> T getGlobalProperty(ConfigProperty property) {
        // Search in priority order (highest first)
        Map<String, Object>[] sources = new Map[] {
            optionsProperties, resourceProperties, factoryProperties, moduleProperties, annotationProperties
        };
        String key = property.getKey();
        for (Map<String, Object> source : sources) {
            if (source != null && source.containsKey(key)) {
                Object value = source.get(key);
                if (value != null) {
                    return (T) value;
                }
            }
        }
        return property.getDefaultValue();
    }

    // ========================================================================
    // Property Extraction Helpers
    // ========================================================================

    /**
     * Extracts global-scoped properties from a source map.
     * <p>
     * Global properties use keys without prefix (e.g., "typeStrategy")
     * or with "codec." prefix (e.g., "codec.typeStrategy").
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractGlobalProperties(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        // Look for nested "global" map first
        Object globalMap = source.get("global");
        if (globalMap instanceof Map) {
            return (Map<String, Object>) globalMap;
        }
        // Return source as-is (properties without class prefix are global)
        return source;
    }

    /**
     * Extracts EClass-scoped properties from a source map.
     * <p>
     * Supports multiple lookup patterns (in priority order):
     * <ol>
     *   <li>{@code codec.eClassConfig} key with {@code Map<EClass, Map<String, Object>>}</li>
     *   <li>Nested map under EClass name key (e.g., {@code "Person" -> Map})</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractClassProperties(Map<String, Object> source, EClass eClass) {
        if (source == null || source.isEmpty() || eClass == null) {
            return null;
        }

        // Pattern 1: Check for CODEC_ECLASS_CONFIG (Map<EClass, Map<String, Object>>)
        // Try both short key ("eClassConfig") and prefixed key ("codec.eClassConfig")
        Object eClassConfigMap = source.get(ConfigProperty.ECLASS_CONFIG.getKey());
        if (eClassConfigMap == null) {
            eClassConfigMap = source.get(ConfigProperty.ECLASS_CONFIG.getPropertyKey());
        }
        if (eClassConfigMap instanceof Map<?, ?> eClassMap) {
            Object classProps = eClassMap.get(eClass);
            if (classProps instanceof Map) {
                return (Map<String, Object>) classProps;
            }
        }

        // Pattern 2: Look for nested map under class name string
        String className = eClass.getName();
        Object classMap = source.get(className);
        if (classMap instanceof Map) {
            return (Map<String, Object>) classMap;
        }
        return null;
    }

    /**
     * Extracts feature-scoped properties from a source map.
     * <p>
     * Supports multiple lookup patterns (in priority order):
     * <ol>
     *   <li>{@code codec.eReferenceConfig} key with {@code Map<EReference, Map<String, Object>>}</li>
     *   <li>{@code codec.eAttributeConfig} key with {@code Map<EAttribute, Map<String, Object>>}</li>
     *   <li>Nested map under "ClassName.featureName" key</li>
     *   <li>Nested map under class name, then feature name</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractFeatureProperties(Map<String, Object> source, EStructuralFeature feature) {
        if (source == null || source.isEmpty() || feature == null) {
            return null;
        }
        EClass eClass = feature.getEContainingClass();
        if (eClass == null) {
            return null;
        }

        // Pattern 1: Check for CODEC_EREFERENCE_CONFIG or CODEC_EATTRIBUTE_CONFIG
        // Try both short key and prefixed key for each
        if (feature instanceof EReference) {
            Object eRefConfigMap = source.get(ConfigProperty.EREFERENCE_CONFIG.getKey());
            if (eRefConfigMap == null) {
                eRefConfigMap = source.get(ConfigProperty.EREFERENCE_CONFIG.getPropertyKey());
            }
            if (eRefConfigMap instanceof Map<?, ?> refMap) {
                Object featureProps = refMap.get(feature);
                if (featureProps instanceof Map) {
                    return (Map<String, Object>) featureProps;
                }
            }
        } else if (feature instanceof EAttribute) {
            Object eAttrConfigMap = source.get(ConfigProperty.EATTRIBUTE_CONFIG.getKey());
            if (eAttrConfigMap == null) {
                eAttrConfigMap = source.get(ConfigProperty.EATTRIBUTE_CONFIG.getPropertyKey());
            }
            if (eAttrConfigMap instanceof Map<?, ?> attrMap) {
                Object featureProps = attrMap.get(feature);
                if (featureProps instanceof Map) {
                    return (Map<String, Object>) featureProps;
                }
            }
        }

        // Pattern 2: Look for "ClassName.featureName" key
        String featureKey = eClass.getName() + "." + feature.getName();
        Object featureMap = source.get(featureKey);
        if (featureMap instanceof Map) {
            return (Map<String, Object>) featureMap;
        }

        // Pattern 3: Look for nested map under class name, then feature name
        Object classMap = source.get(eClass.getName());
        if (classMap instanceof Map) {
            Object fMap = ((Map<String, Object>) classMap).get(feature.getName());
            if (fMap instanceof Map) {
                return (Map<String, Object>) fMap;
            }
        }

        return null;
    }

    // ========================================================================
    // Builder
    // ========================================================================

    /**
     * Creates a new builder pre-populated with this resolver's configuration.
     * <p>
     * Useful for creating a modified resolver that inherits existing settings.
     */
    public Builder toBuilder() {
        Builder builder = new Builder()
                .optionsProperties(this.optionsProperties)
                .resourceProperties(this.resourceProperties)
                .factoryProperties(this.factoryProperties)
                .moduleProperties(this.moduleProperties)
                .annotationProperties(this.annotationProperties);

        // Preserve forceWrite/forceRead feature sets
        for (EStructuralFeature f : this.forceWriteFeatures) {
            builder.forceWrite(f);
        }
        for (EStructuralFeature f : this.forceReadFeatures) {
            builder.forceRead(f);
        }

        return builder;
    }

    /**
     * Creates a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a resolver with no configuration (all defaults).
     */
    public static ConfigurationResolver defaults() {
        return builder().build();
    }

    public static final class Builder {
        private Map<String, Object> optionsProperties;
        private Map<String, Object> resourceProperties;
        private Map<String, Object> factoryProperties;
        private Map<String, Object> moduleProperties;
        private Map<String, Object> annotationProperties;

        // Convenience tracking for expand references (collected until build)
        private List<Object> expandReferences;
        // Convenience tracking for ignore features (collected until build)
        private List<String> ignoreFeatures;
        // Convenience tracking for ID features (collected until build)
        private List<String> idFeaturesList;
        // Convenience tracking for forceWrite features (collected until build)
        private Set<EStructuralFeature> forceWriteFeatures;
        // Convenience tracking for forceRead features (collected until build)
        private Set<EStructuralFeature> forceReadFeatures;

        private Builder() {}

        /**
         * Sets load/save options properties (highest priority).
         */
        public Builder optionsProperties(Map<String, Object> optionsProperties) {
            this.optionsProperties = optionsProperties;
            return this;
        }

        /**
         * Sets resource-level properties.
         */
        public Builder resourceProperties(Map<String, Object> resourceProperties) {
            this.resourceProperties = resourceProperties;
            return this;
        }

        /**
         * Sets factory-level properties.
         */
        public Builder factoryProperties(Map<String, Object> factoryProperties) {
            this.factoryProperties = factoryProperties;
            return this;
        }

        /**
         * Sets module-level properties.
         */
        public Builder moduleProperties(Map<String, Object> moduleProperties) {
            this.moduleProperties = moduleProperties;
            return this;
        }

        /**
         * Sets annotation-derived properties (from EAnnotations).
         */
        public Builder annotationProperties(Map<String, Object> annotationProperties) {
            this.annotationProperties = annotationProperties;
            return this;
        }

        // ====================================================================
        // Convenience Methods for Reference Expansion
        // ====================================================================

        /**
         * Enables global expansion of all non-containment references.
         * <p>
         * When enabled, all non-containment references are serialized inline
         * instead of as proxy references.
         *
         * @param expandGlobal true to expand all references, false otherwise
         * @return this builder
         * @see ConfigProperty#EXPAND_GLOBAL
         */
        public Builder expandGlobal(boolean expandGlobal) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.EXPAND_GLOBAL.getKey(), expandGlobal);
            return this;
        }

        /**
         * Adds specific EReferences to expand inline.
         * <p>
         * Only the specified references will be expanded; others remain as proxies.
         * Can be called multiple times to accumulate references.
         *
         * @param references the EReferences to expand
         * @return this builder
         * @see ConfigProperty#EXPAND
         */
        public Builder expand(EReference... references) {
            if (references != null) {
                ensureExpandReferences();
                for (EReference ref : references) {
                    if (ref != null) {
                        expandReferences.add(ref);
                    }
                }
            }
            return this;
        }

        /**
         * Adds specific reference names to expand inline.
         * <p>
         * The names are resolved against the EClass at runtime.
         * Can be called multiple times to accumulate references.
         *
         * @param referenceNames the reference names to expand
         * @return this builder
         * @see ConfigProperty#EXPAND
         */
        public Builder expand(String... referenceNames) {
            if (referenceNames != null) {
                ensureExpandReferences();
                for (String name : referenceNames) {
                    if (name != null && !name.isEmpty()) {
                        expandReferences.add(name);
                    }
                }
            }
            return this;
        }

        /**
         * Adds a list of EReferences to expand inline.
         * <p>
         * Only the specified references will be expanded; others remain as proxies.
         * Can be called multiple times to accumulate references.
         *
         * @param references the list of EReferences to expand
         * @return this builder
         * @see ConfigProperty#EXPAND
         */
        public Builder expand(List<EReference> references) {
            if (references != null && !references.isEmpty()) {
                ensureExpandReferences();
                for (EReference ref : references) {
                    if (ref != null) {
                        expandReferences.add(ref);
                    }
                }
            }
            return this;
        }

        /**
         * Adds a set of reference names to expand inline.
         * <p>
         * The names are resolved against the EClass at runtime.
         * Can be called multiple times to accumulate references.
         *
         * @param referenceNames the set of reference names to expand
         * @return this builder
         * @see ConfigProperty#EXPAND
         */
        public Builder expand(Set<String> referenceNames) {
            if (referenceNames != null && !referenceNames.isEmpty()) {
                ensureExpandReferences();
                for (String name : referenceNames) {
                    if (name != null && !name.isEmpty()) {
                        expandReferences.add(name);
                    }
                }
            }
            return this;
        }

        /**
         * Sets the maximum depth for nested expansion.
         * <p>
         * Currently only depth=1 is supported. Higher values produce a warning.
         *
         * @param depth the maximum expansion depth
         * @return this builder
         * @see ConfigProperty#EXPAND_DEPTH
         */
        public Builder expandDepth(int depth) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.EXPAND_DEPTH.getKey(), depth);
            return this;
        }

        /**
         * Controls whether bidirectional (opposite) references are skipped during expansion.
         * <p>
         * Default is true (skip) to prevent cycles.
         *
         * @param ignore true to skip bidirectional references, false to include
         * @return this builder
         * @see ConfigProperty#EXPAND_IGNORE_BIDIRECTIONAL
         */
        public Builder expandIgnoreBidirectional(boolean ignore) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.EXPAND_IGNORE_BIDIRECTIONAL.getKey(), ignore);
            return this;
        }

        // ====================================================================
        // Convenience Methods for Type Configuration
        // ====================================================================

        /**
         * Sets the type serialization strategy.
         *
         * @param strategy the type strategy (URI, NAME, NONE, etc.)
         * @return this builder
         * @see ConfigProperty#TYPE_STRATEGY
         */
        public Builder typeStrategy(TypeStrategy strategy) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.TYPE_STRATEGY.getKey(), strategy.name());
            return this;
        }

        /**
         * Sets the JSON key for type information.
         *
         * @param key the type key (default: "_type")
         * @return this builder
         * @see ConfigProperty#TYPE_KEY
         */
        public Builder typeKey(String key) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.TYPE_KEY.getKey(), key);
            return this;
        }

        /**
         * Sets whether to include type information.
         *
         * @param include true to include type, false to omit
         * @return this builder
         * @see ConfigProperty#TYPE_INCLUDE
         */
        public Builder typeInclude(boolean include) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.TYPE_INCLUDE.getKey(), include);
            return this;
        }

        // ====================================================================
        // Convenience Methods for Format Configuration
        // ====================================================================

        /**
         * Sets the default serialization format.
         * <p>
         * This affects how structured data (type, ID, references) is written:
         * <ul>
         *   <li>PLAIN - Simple key-value format (e.g., "_type": "Person")</li>
         *   <li>STRUCTURED - Nested object format (e.g., "_type": {"schema": "...", "type": "Person"})</li>
         * </ul>
         *
         * @param format the default format (PLAIN or STRUCTURED)
         * @return this builder
         * @see ConfigProperty#TYPE_FORMAT
         */
        public Builder defaultFormat(SerializationFormat format) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.TYPE_FORMAT.getKey(), format.name());
            return this;
        }

        // ====================================================================
        // Convenience Methods for ID Configuration
        // ====================================================================

        /**
         * Sets the ID serialization strategy.
         * <p>
         * Strategies:
         * <ul>
         *   <li>ID_FIELD - Use the EClass's ID attribute (default)</li>
         *   <li>NONE - Don't serialize ID</li>
         *   <li>COMBINED - Combine multiple features into one ID</li>
         * </ul>
         *
         * @param strategy the ID strategy
         * @return this builder
         * @see ConfigProperty#ID_STRATEGY
         */
        public Builder idStrategy(String strategy) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.ID_STRATEGY.getKey(), strategy);
            return this;
        }

        /**
         * Enables or disables ID serialization.
         * <p>
         * Convenience method that sets idStrategy to "ID_FIELD" (enabled) or "NONE" (disabled).
         *
         * @param useId true to enable ID serialization, false to disable
         * @return this builder
         * @see #idStrategy(String)
         */
        public Builder useId(boolean useId) {
            return idStrategy(useId ? "ID_FIELD" : "NONE");
        }

        /**
         * Sets the JSON key for ID information.
         *
         * @param key the ID key (default: "_id")
         * @return this builder
         * @see ConfigProperty#ID_KEY
         */
        public Builder idKey(String key) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.ID_KEY.getKey(), key);
            return this;
        }

        /**
         * Sets whether ID should appear first in the serialized output.
         * <p>
         * When true, the ID field is written before other properties.
         *
         * @param onTop true to place ID first, false for normal ordering
         * @return this builder
         * @see ConfigProperty#ID_ON_TOP
         */
        public Builder idOnTop(boolean onTop) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.ID_ON_TOP.getKey(), onTop);
            return this;
        }

        /**
         * Sets the ID serialization format.
         * <p>
         * <ul>
         *   <li>PLAIN - ID as simple value (e.g., "_id": "123")</li>
         *   <li>STRUCTURED - ID as object with components (e.g., "_id": {"value": "123", ...})</li>
         * </ul>
         *
         * @param format the ID format (PLAIN or STRUCTURED)
         * @return this builder
         * @see ConfigProperty#ID_FORMAT
         */
        public Builder idFormat(SerializationFormat format) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.ID_FORMAT.getKey(), format.name());
            return this;
        }

        /**
         * Sets the separator for combining multiple ID features in PLAIN format.
         * <p>
         * When using COMBINED strategy with multiple features, this separator
         * joins the values (e.g., with "-": "part1-part2-part3").
         *
         * @param separator the separator string (default: "-")
         * @return this builder
         * @see ConfigProperty#ID_SEPARATOR
         */
        public Builder idSeparator(String separator) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.ID_SEPARATOR.getKey(), separator);
            return this;
        }

        /**
         * Sets the feature names to use for combined ID.
         * <p>
         * When using COMBINED strategy, these features are combined to form the ID.
         *
         * @param featureNames the feature names to combine
         * @return this builder
         * @see ConfigProperty#ID_FEATURES
         */
        public Builder idFeatures(String... featureNames) {
            if (featureNames != null && featureNames.length > 0) {
                ensureIdFeatures();
                for (String name : featureNames) {
                    if (name != null && !name.isEmpty()) {
                        idFeaturesList.add(name);
                    }
                }
            }
            return this;
        }

        /**
         * Sets the features to use for combined ID.
         * <p>
         * When using COMBINED strategy, these features are combined to form the ID.
         *
         * @param features the features to combine
         * @return this builder
         * @see ConfigProperty#ID_FEATURES
         */
        public Builder idFeatures(EStructuralFeature... features) {
            if (features != null && features.length > 0) {
                ensureIdFeatures();
                for (EStructuralFeature feature : features) {
                    if (feature != null) {
                        idFeaturesList.add(feature.getName());
                    }
                }
            }
            return this;
        }

        /**
         * Sets the ID key mode.
         * <p>
         * Controls how ID is written:
         * <ul>
         *   <li>ID_ONLY - Only write the _id field</li>
         *   <li>BOTH - Write both _id field and the original feature</li>
         *   <li>FEATURE_ONLY - Only write the original feature, no _id</li>
         * </ul>
         *
         * @param mode the ID key mode
         * @return this builder
         * @see ConfigProperty#ID_KEY_MODE
         */
        public Builder idKeyMode(String mode) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.ID_KEY_MODE.getKey(), mode);
            return this;
        }

        /**
         * Sets whether to serialize the separator in STRUCTURED ID format.
         * <p>
         * When true, the separator is included in the output, allowing deserialization
         * without pre-configured separator knowledge.
         *
         * @param serialize true to include separator in output
         * @return this builder
         * @see ConfigProperty#ID_SEPARATOR_SERIALIZE
         */
        public Builder idSerializeSeparator(boolean serialize) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.ID_SEPARATOR_SERIALIZE.getKey(), serialize);
            return this;
        }

        /**
         * Sets the JSON key for the separator field in STRUCTURED ID format.
         *
         * @param key the separator key (default: "separator")
         * @return this builder
         * @see ConfigProperty#ID_SEPARATOR_KEY
         */
        public Builder idSeparatorKey(String key) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.ID_SEPARATOR_KEY.getKey(), key);
            return this;
        }

        // ====================================================================
        // Convenience Methods for Feature Configuration
        // ====================================================================

        /**
         * Enables use of ExtendedMetaData names for JSON keys.
         *
         * @param use true to use ExtendedMetaData names, false to use feature names
         * @return this builder
         * @see ConfigProperty#USE_NAMES_FROM_EXTENDED_METADATA
         */
        public Builder useNamesFromExtendedMetaData(boolean use) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.USE_NAMES_FROM_EXTENDED_METADATA.getKey(), use);
            return this;
        }

        // ====================================================================
        // Convenience Methods for Smart Compression
        // ====================================================================

        /**
         * Enables or disables smart compression.
         * <p>
         * When enabled, type information (_type) is omitted when it can be inferred
         * from the reference declaration or context:
         * <ul>
         *   <li>For contained objects: omit _type when instance type == declared type</li>
         *   <li>For non-contained refs: omit _type when instance type == declared type</li>
         *   <li>For supertypes: use plain names instead of URIs for same-schema types</li>
         * </ul>
         *
         * @param smartCompression true to enable, false to disable (default)
         * @return this builder
         * @see ConfigProperty#SMART_COMPRESSION
         */
        public Builder smartCompression(boolean smartCompression) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.SMART_COMPRESSION.getKey(), smartCompression);
            return this;
        }

        // ====================================================================
        // Convenience Methods for Reference Configuration
        // ====================================================================

        /**
         * Sets the JSON key for reference URIs.
         * <p>
         * Default is "$ref". Used in STRUCTURED format for non-containment references.
         *
         * @param key the reference key
         * @return this builder
         * @see ConfigProperty#REF_KEY
         */
        public Builder refKey(String key) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.REF_KEY.getKey(), key);
            return this;
        }

        /**
         * Sets the JSON key for proxy objects.
         * <p>
         * Default is "$proxy". Used to mark proxy objects during deserialization.
         *
         * @param key the proxy key
         * @return this builder
         * @see ConfigProperty#PROXY_KEY
         */
        public Builder proxyKey(String key) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.PROXY_KEY.getKey(), key);
            return this;
        }

        // ====================================================================
        // Convenience Methods for SuperType Configuration
        // ====================================================================

        /**
         * Enables or disables supertype serialization.
         * <p>
         * When enabled, the inheritance hierarchy is written alongside type information.
         *
         * @param serialize true to serialize supertypes, false to omit (default)
         * @return this builder
         * @see ConfigProperty#SUPERTYPE_SERIALIZE
         */
        public Builder serializeSuperTypes(boolean serialize) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.SUPERTYPE_SERIALIZE.getKey(), serialize);
            return this;
        }

        // ====================================================================
        // Convenience Methods for Global Ignore Features
        // ====================================================================

        /**
         * Adds feature names to the global ignore list.
         * <p>
         * Features in this list will be skipped during serialization/deserialization
         * unless explicitly forced via forceRead/forceWrite.
         *
         * @param featureNames the feature names to ignore
         * @return this builder
         * @see ConfigProperty#IGNORE_FEATURES
         */
        public Builder globalIgnoreFeatures(String... featureNames) {
            if (featureNames != null && featureNames.length > 0) {
                ensureIgnoreFeatures();
                for (String name : featureNames) {
                    if (name != null && !name.isEmpty()) {
                        ignoreFeatures.add(name);
                    }
                }
            }
            return this;
        }

        /**
         * Adds features to the global ignore list.
         * <p>
         * Features in this list will be skipped during serialization/deserialization
         * unless explicitly forced via forceRead/forceWrite.
         *
         * @param features the features to ignore
         * @return this builder
         * @see ConfigProperty#IGNORE_FEATURES
         */
        public Builder globalIgnoreFeatures(EStructuralFeature... features) {
            if (features != null && features.length > 0) {
                ensureIgnoreFeatures();
                for (EStructuralFeature feature : features) {
                    if (feature != null) {
                        ignoreFeatures.add(feature.getName());
                    }
                }
            }
            return this;
        }

        /**
         * Adds a single feature name to the global ignore list.
         *
         * @param featureName the feature name to ignore
         * @return this builder
         * @see ConfigProperty#IGNORE_FEATURES
         */
        public Builder globalIgnore(String featureName) {
            if (featureName != null && !featureName.isEmpty()) {
                ensureIgnoreFeatures();
                ignoreFeatures.add(featureName);
            }
            return this;
        }

        // ====================================================================
        // Convenience Methods for Force Write/Read
        // ====================================================================

        /**
         * Force-writes the specified features regardless of their EMF flags.
         * <p>
         * Use this to serialize volatile/transient/derived features that would
         * normally be skipped. Common use case: GeoJSON coordinates stored as
         * volatile "data" attributes.
         * </p>
         * <p>
         * Example:
         * <pre>
         * ConfigurationResolver.builder()
         *     .forceWrite(
         *         GeoJsonPackage.Literals.POINT__DATA,
         *         GeoJsonPackage.Literals.GEO_JSON_OBJECT__BBOX
         *     )
         *     .build();
         * </pre>
         * </p>
         *
         * @param features the EStructuralFeatures to force-write
         * @return this builder
         * @see ConfigProperty#FORCE_WRITE
         */
        public Builder forceWrite(EStructuralFeature... features) {
            if (features != null) {
                for (EStructuralFeature feature : features) {
                    if (feature != null) {
                        ensureForceWriteFeatures();
                        forceWriteFeatures.add(feature);
                    }
                }
            }
            return this;
        }

        /**
         * Force-reads the specified features regardless of their EMF flags.
         * <p>
         * Use this to deserialize into volatile/transient features that would
         * normally be skipped. The feature must be changeable for this to work.
         * </p>
         * <p>
         * Example:
         * <pre>
         * ConfigurationResolver.builder()
         *     .forceRead(
         *         GeoJsonPackage.Literals.POINT__DATA,
         *         GeoJsonPackage.Literals.GEO_JSON_OBJECT__BBOX
         *     )
         *     .build();
         * </pre>
         * </p>
         *
         * @param features the EStructuralFeatures to force-read
         * @return this builder
         * @see ConfigProperty#FORCE_READ
         */
        public Builder forceRead(EStructuralFeature... features) {
            if (features != null) {
                for (EStructuralFeature feature : features) {
                    if (feature != null) {
                        ensureForceReadFeatures();
                        forceReadFeatures.add(feature);
                    }
                }
            }
            return this;
        }

        // ====================================================================
        // Convenience Methods for Value Serialization
        // ====================================================================

        /**
         * Controls whether null values are serialized.
         * <p>
         * When false (default), null values are omitted from the output.
         * When true, null values are explicitly written as JSON null.
         *
         * @param serialize true to serialize nulls, false to omit (default)
         * @return this builder
         * @see ConfigProperty#SERIALIZE_NULL
         */
        public Builder serializeNull(boolean serialize) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.SERIALIZE_NULL.getKey(), serialize);
            return this;
        }

        /**
         * Controls whether empty collections are serialized.
         * <p>
         * When false (default), empty lists/arrays are omitted from the output.
         * When true, empty collections are written as empty JSON arrays.
         *
         * @param serialize true to serialize empty collections, false to omit (default)
         * @return this builder
         * @see ConfigProperty#SERIALIZE_EMPTY
         */
        public Builder serializeEmpty(boolean serialize) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.SERIALIZE_EMPTY.getKey(), serialize);
            return this;
        }

        /**
         * Controls whether default values are serialized.
         * <p>
         * When false (default), values equal to their EMF default are omitted.
         * When true, default values are explicitly written.
         *
         * @param serialize true to serialize defaults, false to omit (default)
         * @return this builder
         * @see ConfigProperty#SERIALIZE_DEFAULT
         */
        public Builder serializeDefault(boolean serialize) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.SERIALIZE_DEFAULT.getKey(), serialize);
            return this;
        }

        // ====================================================================
        // Convenience Methods for Strictness Configuration
        // ====================================================================

        /**
         * Controls whether unknown JSON fields cause an error during deserialization.
         * <p>
         * When false (default), unknown fields generate a warning and are skipped.
         * When true, unknown fields cause deserialization to fail with an error.
         *
         * @param strict true to error on unknown fields, false to skip with warning (default)
         * @return this builder
         * @see ConfigProperty#STRICT_ON_UNKNOWN
         */
        public Builder strictOnUnknown(boolean strict) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.STRICT_ON_UNKNOWN.getKey(), strict);
            return this;
        }

        /**
         * Controls whether missing required features cause an error during deserialization.
         * <p>
         * When false (default), missing required features generate a warning and use
         * the EMF default value. When true, missing required features cause
         * deserialization to fail with an error.
         *
         * @param strict true to error on missing required fields, false to use defaults (default)
         * @return this builder
         * @see ConfigProperty#STRICT_ON_MISSING
         */
        public Builder strictOnMissing(boolean strict) {
            ensureResourceProperties();
            resourceProperties.put(ConfigProperty.STRICT_ON_MISSING.getKey(), strict);
            return this;
        }

        // ====================================================================
        // Helper Methods
        // ====================================================================

        private void ensureResourceProperties() {
            if (resourceProperties == null) {
                resourceProperties = new HashMap<>();
            }
        }

        private void ensureExpandReferences() {
            if (expandReferences == null) {
                expandReferences = new ArrayList<>();
            }
        }

        private void ensureIgnoreFeatures() {
            if (ignoreFeatures == null) {
                ignoreFeatures = new ArrayList<>();
            }
        }

        private void ensureIdFeatures() {
            if (idFeaturesList == null) {
                idFeaturesList = new ArrayList<>();
            }
        }

        private void ensureForceWriteFeatures() {
            if (forceWriteFeatures == null) {
                forceWriteFeatures = new HashSet<>();
            }
        }

        private void ensureForceReadFeatures() {
            if (forceReadFeatures == null) {
                forceReadFeatures = new HashSet<>();
            }
        }

        /**
         * Builds the resolver.
         */
        public ConfigurationResolver build() {
            // Apply collected expand references to resource properties
            if (expandReferences != null && !expandReferences.isEmpty()) {
                ensureResourceProperties();
                resourceProperties.put(ConfigProperty.EXPAND.getKey(), new ArrayList<>(expandReferences));
            }
            // Apply collected ignore features to resource properties
            if (ignoreFeatures != null && !ignoreFeatures.isEmpty()) {
                ensureResourceProperties();
                resourceProperties.put(ConfigProperty.IGNORE_FEATURES.getKey(), new ArrayList<>(ignoreFeatures));
            }
            // Apply collected ID features to resource properties
            if (idFeaturesList != null && !idFeaturesList.isEmpty()) {
                ensureResourceProperties();
                resourceProperties.put(ConfigProperty.ID_FEATURES.getKey(), new ArrayList<>(idFeaturesList));
            }
            // forceWrite/forceRead features are stored directly in the resolver
            // and applied during resolveFeatureConfig()
            return new ConfigurationResolver(this);
        }
    }
}
