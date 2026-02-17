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
package org.eclipse.fennec.codec.metadata.type;

import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.MetadataRegistry;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataHandler;
import org.eclipse.fennec.model.metadata.api.MetadataService;

/**
 * Service for managing type discriminator registries across multiple mapIds.
 * <p>
 * This service acts as the parent container for all {@link TypeDiscriminatorRegistry}
 * instances. Each registry is scoped to a specific mapId (namespace), allowing
 * the same discriminator value to map to different EClasses in different contexts.
 * </p>
 *
 * <h3>MapId Concept</h3>
 * <p>
 * A mapId identifies a specific type discrimination context. Common patterns:
 * </p>
 * <ul>
 *   <li>{@code "iot-sensors"} - IoT sensor devices</li>
 *   <li>{@code "lorawan-devices"} - LoRaWAN devices</li>
 *   <li>Package nsURI - Using the EPackage namespace as mapId</li>
 * </ul>
 *
 * <h3>Population from MetadataService</h3>
 * <p>
 * The service can be populated automatically from a {@link MetadataService} by
 * scanning all registered packages for {@code typeMapping/{mapId}} annotation sources
 * and discriminator values.
 * </p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);
 *
 * // Resolve type in "iot-sensors" context
 * EClass sensorClass = service.getEClass("iot-sensors", "temp-sensor");
 *
 * // Get discriminator for serialization
 * String discriminator = service.getDiscriminatorValue("iot-sensors", sensorClass);
 * }</pre>
 *
 * @see TypeDiscriminatorRegistry
 * @see ClassCodecAspect#getDiscriminatorValue()
 * @author Mark Hoffmann
 * @since 2025-12-17
 */
public class TypeDiscriminatorService implements MetadataHandler, TypeDiscriminatorReader {

    private static final Logger LOGGER = Logger.getLogger(TypeDiscriminatorService.class.getName());

    /** Default mapId used when no specific mapId is provided */
    public static final String DEFAULT_MAP_ID = "default";

    private final Map<String, TypeDiscriminatorRegistry> registries = new ConcurrentHashMap<>();

    /**
     * Creates an empty TypeDiscriminatorService.
     */
    public TypeDiscriminatorService() {
        // Empty service
    }

    /**
     * Creates a TypeDiscriminatorService populated from a MetadataService.
     * <p>
     * This method iterates through all registered metadata and extracts discriminator
     * mappings from {@link ClassCodecAspect} instances.
     * </p>
     *
     * @param metadataService the metadata service to scan
     * @return a new service populated with discriminator mappings
     */
    public static TypeDiscriminatorService fromMetadataService(MetadataService metadataService) {
        Objects.requireNonNull(metadataService, "metadataService must not be null");

        TypeDiscriminatorService service = new TypeDiscriminatorService();

        MetadataRegistry registry = metadataService.getRegistry();
        if (registry != null) {
            for (PackageMetadata pkgMetadata : registry.getPackages()) {
                // Phase 1: register discriminator values from ClassCodecAspect
                for (ClassMetadata classMetadata : pkgMetadata.getClasses()) {
                    service.registerFromClassMetadata(classMetadata);
                }

                // Phase 2: scan raw annotations for fallback config and inline mappings
                EPackage ePackage = pkgMetadata.getEPackage();
                if (ePackage != null) {
                    Function<String, EClass> eClassResolver = uri -> resolveEClassFromUri(uri, ePackage);
                    service.registerAnnotationMappings(ePackage, eClassResolver);
                }
            }
        }

        return service;
    }

    // ========================================================================
    // MetadataHandler implementation
    // ========================================================================

    /**
     * Called when a package is registered with the MetadataWhiteboard.
     * <p>
     * Incrementally scans the package's ClassMetadata for discriminator annotations
     * and registers them, rather than rebuilding the entire service.
     * </p>
     *
     * @param packageMetadata the newly registered package metadata
     */
    @Override
    public void onPackageRegistered(PackageMetadata packageMetadata) {
        if (packageMetadata == null) {
            return;
        }

        // Phase 1: register discriminator values from ClassCodecAspect
        for (ClassMetadata classMetadata : packageMetadata.getClasses()) {
            registerFromClassMetadata(classMetadata);
        }

        // Phase 2: scan raw annotations for fallback config and inline mappings
        EPackage ePackage = packageMetadata.getEPackage();
        if (ePackage != null) {
            Function<String, EClass> eClassResolver = uri -> resolveEClassFromUri(uri, ePackage);
            registerAnnotationMappings(ePackage, eClassResolver);
        }
    }

    /**
     * Called when a package is unregistered from the MetadataWhiteboard.
     * <p>
     * Removes all discriminator mappings for EClasses from the specified package.
     * </p>
     *
     * @param packageMetadata the package metadata being unregistered
     */
    @Override
    public void onPackageUnregistered(PackageMetadata packageMetadata) {
        unregisterPackage(packageMetadata);
    }

    /**
     * Scans all EClasses in a package for typeMapping fallback configuration and
     * all EReferences for inlineMapping annotations.
     * <p>
     * This complements {@link #registerFromClassMetadata(ClassMetadata)} which only
     * extracts discriminator values from {@link ClassCodecAspect}. This method handles:
     * <ul>
     *   <li>Fallback strategy and fallbackEClass from typeMapping annotations</li>
     *   <li>Inline mappings from inlineMapping annotations on EReferences</li>
     * </ul>
     * </p>
     *
     * @param ePackage the package to scan
     * @param eClassResolver function that resolves EClass URI strings to EClass instances
     */
    private void registerAnnotationMappings(EPackage ePackage, Function<String, EClass> eClassResolver) {
        for (EClassifier classifier : ePackage.getEClassifiers()) {
            if (!(classifier instanceof EClass eClass)) {
                continue;
            }

            // Scan typeMapping/{mapId} annotations for fallback config
            for (EAnnotation ann : eClass.getEAnnotations()) {
                String source = ann.getSource();
                String mapId = extractMapIdFromSource(source);
                if (mapId != null) {
                    registerFallbackConfig(mapId, ann.getDetails().map());
                }
            }

            // Scan EReferences for inlineMapping annotations
            for (EReference ref : eClass.getEReferences()) {
                EAnnotation inlineAnn = ref.getEAnnotation(INLINE_MAPPING_SOURCE);
                if (inlineAnn != null) {
                    registerInlineMappings(ref, inlineAnn.getDetails().map(), eClassResolver);
                }
            }
        }
    }

    /**
     * Registers fallback configuration from a typeMapping annotation's details.
     * <p>
     * Only sets fallbackStrategy and fallbackEClass; does not register mapping entries
     * (those are already handled by {@link #registerFromClassMetadata}).
     * </p>
     *
     * @param mapId the mapId extracted from the annotation source
     * @param details the annotation details
     */
    private void registerFallbackConfig(String mapId, Map<String, String> details) {
        String fallbackStrategyStr = details.get(KEY_FALLBACK_STRATEGY);
        if (fallbackStrategyStr != null && !fallbackStrategyStr.isEmpty()) {
            TypeDiscriminatorRegistry registry = getOrCreateRegistry(mapId);
            try {
                registry.setFallbackStrategy(FallbackStrategy.valueOf(fallbackStrategyStr));
            } catch (IllegalArgumentException e) {
                LOGGER.warning("[" + mapId + "] Invalid fallbackStrategy: " + fallbackStrategyStr);
            }
        }

        String fallbackEClassUri = details.get(KEY_FALLBACK_ECLASS);
        if (fallbackEClassUri != null && !fallbackEClassUri.isEmpty()) {
            TypeDiscriminatorRegistry registry = getOrCreateRegistry(mapId);
            registry.setFallbackEClass(fallbackEClassUri);
        }
    }

    /**
     * Resolves an EClass URI string using a package as context.
     * <p>
     * Supports fragment-based URIs (e.g., "http://example.org/1.0#//ClassName")
     * by looking up the classifier in the global package registry.
     * </p>
     */
    private static EClass resolveEClassFromUri(String uriStr, EPackage contextPackage) {
        if (uriStr == null || uriStr.isEmpty()) {
            return null;
        }
        try {
            URI uri = URI.createURI(uriStr);
            String fragment = uri.fragment();
            if (fragment != null && fragment.startsWith("//")) {
                String className = fragment.substring(2);
                // Try context package first
                EClassifier classifier = contextPackage.getEClassifier(className);
                if (classifier instanceof EClass eClass) {
                    return eClass;
                }
                // Try global registry
                String nsUri = uri.trimFragment().toString();
                EPackage pkg = EPackage.Registry.INSTANCE.getEPackage(nsUri);
                if (pkg != null) {
                    classifier = pkg.getEClassifier(className);
                    if (classifier instanceof EClass eClass) {
                        return eClass;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to resolve EClass URI: " + uriStr + " — " + e.getMessage());
        }
        return null;
    }

    /**
     * Registers discriminator mappings from a ClassMetadata.
     * <p>
     * Extracts the discriminator value and path from the {@link ClassCodecAspect} if present.
     * The discriminator path (e.g., "info.sensorType") is set at the registry level since
     * all classes in the same mapId share the same path.
     * </p>
     * <p>
     * If the concrete class doesn't have a discriminatorPath in its typeConfig, we look
     * at the supertype hierarchy to find it (typically defined on an abstract base class).
     * </p>
     *
     * @param classMetadata the class metadata to process
     */
    public void registerFromClassMetadata(ClassMetadata classMetadata) {
        Objects.requireNonNull(classMetadata, "classMetadata must not be null");

        EClass eClass = classMetadata.getEClass();
        if (eClass == null) {
            return;
        }

        // Extract discriminator from ClassCodecAspect
        classMetadata.getAspects().stream()
                .filter(ClassCodecAspect.class::isInstance)
                .map(ClassCodecAspect.class::cast)
                .findFirst()
                .ifPresent(aspect -> {
                    String discriminator = aspect.getDiscriminatorValue();
                    if (discriminator != null && !discriminator.isEmpty()) {
                        // Determine mapId from typeMapping/{mapId} annotation source
                        String mapId = resolveMapId(aspect, eClass);
                        if (mapId == null) {
                            LOGGER.warning("Discriminator value '" + discriminator
                                    + "' on " + eClass.getName() + " but no typeMapping annotation found");
                            return;
                        }
                        TypeDiscriminatorRegistry registry = getOrCreateRegistry(mapId);
                        registry.register(discriminator, eClass);

                        // Set discriminator path if present (from typeConfig or inherited)
                        String discriminatorPath = resolveDiscriminatorPath(eClass, aspect, mapId);
                        if (discriminatorPath != null && !discriminatorPath.isEmpty()
                                && registry.getDiscriminatorPath() == null) {
                            registry.setDiscriminatorPath(discriminatorPath);
                        }
                    }
                });
    }

    /**
     * Resolves the discriminator path for an EClass.
     * <p>
     * First checks the class's own typeConfig. If not found, walks up the
     * supertype hierarchy scanning for {@code typeMapping/{mapId}} annotation sources
     * that contain a {@code typeDiscriminatorPath} detail.
     * </p>
     *
     * @param eClass the EClass to check
     * @param aspect the current class's aspect (may be null)
     * @param mapId the mapId context to search within
     * @return the discriminator path, or null if not found
     */
    private String resolveDiscriminatorPath(EClass eClass, ClassCodecAspect aspect, String mapId) {
        // First check the current class's typeConfig
        if (aspect != null && aspect.getTypeConfig() != null) {
            String path = aspect.getTypeConfig().getDiscriminatorPath();
            if (path != null && !path.isEmpty()) {
                return path;
            }
        }

        // Walk up the supertype hierarchy scanning for typeMapping/{mapId} sources
        String expectedSource = TYPE_MAPPING_SOURCE_PREFIX + mapId;
        for (EClass superType : eClass.getEAllSuperTypes()) {
            EAnnotation ann = superType.getEAnnotation(expectedSource);
            if (ann != null) {
                String path = ann.getDetails().get(KEY_TYPE_DISCRIMINATOR_PATH);
                if (path != null && !path.isEmpty()) {
                    return path;
                }
            }
        }

        return null;
    }

    /**
     * Resolves the mapId for a ClassCodecAspect by scanning for {@code typeMapping/{mapId}} annotation sources.
     * <p>
     * Scans the EClass's annotations for sources starting with {@code TYPE_MAPPING_SOURCE_PREFIX}.
     * The mapId is extracted from the source URI suffix.
     * </p>
     *
     * @param aspect the codec aspect
     * @param eClass the EClass to scan for annotations
     * @return the mapId to use, or null if no typeMapping annotation found
     */
    private String resolveMapId(ClassCodecAspect aspect, EClass eClass) {
        // First check the aspect's typeConfig (already parsed by CodecAspectProvider)
        if (aspect.getTypeConfig() != null && aspect.getTypeConfig().getMapId() != null
                && !aspect.getTypeConfig().getMapId().isEmpty()) {
            return aspect.getTypeConfig().getMapId();
        }
        // Scan annotations directly for typeMapping/{mapId} source
        for (EAnnotation ann : eClass.getEAnnotations()) {
            String source = ann.getSource();
            if (source != null && source.startsWith(TYPE_MAPPING_SOURCE_PREFIX)) {
                String mapId = source.substring(TYPE_MAPPING_SOURCE_PREFIX.length());
                if (!mapId.isEmpty()) {
                    return mapId;
                }
            }
        }
        return null;
    }

    /**
     * Registers static mappings from a {@code typeMapping/{mapId}} annotation on an EClass.
     * <p>
     * Static mappings are key/value details in the annotation where the key is a discriminator
     * value and the value is an EClass URI. Known keys ({@code typeDiscriminatorPath},
     * {@code typeDiscriminator}, {@code fallbackStrategy}, {@code fallbackEClass}) are excluded
     * from mapping registration — they are configuration keys, not mapping entries.
     * </p>
     *
     * @param mapId the mapId extracted from the annotation source
     * @param details the annotation details (key/value pairs)
     * @param eClassResolver function that resolves EClass URI strings to EClass instances
     */
    public void registerStaticMappings(String mapId, Map<String, String> details,
            Function<String, EClass> eClassResolver) {
        Objects.requireNonNull(mapId, "mapId must not be null");
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(eClassResolver, "eClassResolver must not be null");

        TypeDiscriminatorRegistry registry = getOrCreateRegistry(mapId);

        // Parse discriminator path
        String path = details.get(KEY_TYPE_DISCRIMINATOR_PATH);
        if (path != null && !path.isEmpty()) {
            registry.setDiscriminatorPath(path);
        }

        // Parse fallback configuration
        String fallbackStrategyStr = details.get(KEY_FALLBACK_STRATEGY);
        if (fallbackStrategyStr != null && !fallbackStrategyStr.isEmpty()) {
            try {
                registry.setFallbackStrategy(FallbackStrategy.valueOf(fallbackStrategyStr));
            } catch (IllegalArgumentException e) {
                LOGGER.warning("[" + mapId + "] Invalid fallbackStrategy: " + fallbackStrategyStr);
            }
        }

        String fallbackEClassUri = details.get(KEY_FALLBACK_ECLASS);
        if (fallbackEClassUri != null && !fallbackEClassUri.isEmpty()) {
            registry.setFallbackEClass(fallbackEClassUri);
        }

        // Register mapping entries (exclude known configuration keys)
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            if (isTypeMappingConfigKey(key)) {
                continue;
            }
            String eClassUri = entry.getValue();
            if (eClassUri == null || eClassUri.isEmpty()) {
                continue;
            }
            EClass eClass = eClassResolver.apply(eClassUri);
            if (eClass != null) {
                registry.register(key, eClass);
            } else {
                LOGGER.warning("[" + mapId + "] Could not resolve EClass URI: " + eClassUri
                        + " for discriminator '" + key + "'");
            }
        }
    }

    /**
     * Registers inline mappings from an {@code inlineMapping} annotation on an EReference.
     * <p>
     * The mapId for inline mappings is derived from the EReference URI
     * ({@code EcoreUtil.getURI(reference).toString()}), making each inline mapping
     * scoped to a specific reference.
     * </p>
     *
     * @param reference the EReference with the inlineMapping annotation
     * @param details the annotation details (key/value pairs)
     * @param eClassResolver function that resolves EClass URI strings to EClass instances
     * @return the mapId used for this inline mapping (the EReference URI)
     */
    public String registerInlineMappings(EReference reference, Map<String, String> details,
            Function<String, EClass> eClassResolver) {
        Objects.requireNonNull(reference, "reference must not be null");
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(eClassResolver, "eClassResolver must not be null");

        String mapId = EcoreUtil.getURI(reference).toString();
        TypeDiscriminatorRegistry registry = getOrCreateRegistry(mapId);

        // Parse fallback configuration
        String fallbackStrategyStr = details.get(KEY_FALLBACK_STRATEGY);
        if (fallbackStrategyStr != null && !fallbackStrategyStr.isEmpty()) {
            try {
                registry.setFallbackStrategy(FallbackStrategy.valueOf(fallbackStrategyStr));
            } catch (IllegalArgumentException e) {
                LOGGER.warning("[" + mapId + "] Invalid fallbackStrategy: " + fallbackStrategyStr);
            }
        }

        String fallbackEClassUri = details.get(KEY_FALLBACK_ECLASS);
        if (fallbackEClassUri != null && !fallbackEClassUri.isEmpty()) {
            registry.setFallbackEClass(fallbackEClassUri);
        }

        // Register mapping entries (exclude known configuration keys)
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            if (isInlineMappingConfigKey(key)) {
                continue;
            }
            String eClassUri = entry.getValue();
            if (eClassUri == null || eClassUri.isEmpty()) {
                continue;
            }
            EClass eClass = eClassResolver.apply(eClassUri);
            if (eClass != null) {
                registry.register(key, eClass);
            } else {
                LOGGER.warning("[" + mapId + "] Could not resolve EClass URI: " + eClassUri
                        + " for inline discriminator '" + key + "'");
            }
        }

        return mapId;
    }

    /**
     * Resolves an EClass for a discriminator value in a specific mapId context,
     * applying the registry's fallback strategy.
     *
     * @param mapId the namespace identifier
     * @param discriminatorValue the discriminator value to resolve
     * @param eClassResolver function that resolves EClass URI strings to EClass instances
     * @return the resolved EClass, or null if not found and strategy is SKIP
     * @throws IllegalStateException on ERROR strategy, or FALLBACK with no fallbackEClass
     */
    @Override
    public EClass resolve(String mapId, String discriminatorValue, Function<String, EClass> eClassResolver) {
        TypeDiscriminatorRegistry registry = getRegistry(mapId);
        if (registry == null) {
            return null;
        }
        return registry.resolve(discriminatorValue, eClassResolver);
    }

    /**
     * Resolves an EClass for an inline mapping on an EReference.
     * <p>
     * Uses the EReference URI as mapId to look up the inline mapping registry.
     * </p>
     *
     * @param reference the EReference with inline mapping
     * @param discriminatorValue the discriminator value to resolve
     * @param eClassResolver function that resolves EClass URI strings to EClass instances
     * @return the resolved EClass, or null if no inline mapping exists or value not found
     */
    @Override
    public EClass resolveForReference(EReference reference, String discriminatorValue,
            Function<String, EClass> eClassResolver) {
        if (reference == null) {
            return null;
        }
        String mapId = EcoreUtil.getURI(reference).toString();
        return resolve(mapId, discriminatorValue, eClassResolver);
    }

    /**
     * Checks if a key is a configuration key for typeMapping annotations (not a mapping entry).
     */
    private boolean isTypeMappingConfigKey(String key) {
        return KEY_TYPE_DISCRIMINATOR_PATH.equals(key)
                || KEY_TYPE_DISCRIMINATOR.equals(key)
                || KEY_FALLBACK_STRATEGY.equals(key)
                || KEY_FALLBACK_ECLASS.equals(key);
    }

    /**
     * Checks if a key is a configuration key for inlineMapping annotations (not a mapping entry).
     */
    private boolean isInlineMappingConfigKey(String key) {
        return KEY_FALLBACK_STRATEGY.equals(key)
                || KEY_FALLBACK_ECLASS.equals(key);
    }

    /**
     * Gets or creates a registry for the given mapId.
     *
     * @param mapId the namespace identifier
     * @return the registry for this mapId
     */
    public TypeDiscriminatorRegistry getOrCreateRegistry(String mapId) {
        Objects.requireNonNull(mapId, "mapId must not be null");
        return registries.computeIfAbsent(mapId, TypeDiscriminatorRegistry::new);
    }

    /**
     * Gets a registry for the given mapId, or null if not exists.
     *
     * @param mapId the namespace identifier
     * @return the registry, or null if not found
     */
    public TypeDiscriminatorRegistry getRegistry(String mapId) {
        if (mapId == null) {
            return null;
        }
        return registries.get(mapId);
    }

    /**
     * Checks if a registry exists for the given mapId.
     *
     * @param mapId the namespace identifier
     * @return true if a registry exists
     */
    public boolean hasRegistry(String mapId) {
        return mapId != null && registries.containsKey(mapId);
    }

    /**
     * Gets the EClass for a discriminator value in a specific mapId context.
     *
     * @param mapId the namespace identifier
     * @param discriminatorValue the discriminator value
     * @return the corresponding EClass, or null if not found
     */
    @Override
    public EClass getEClass(String mapId, String discriminatorValue) {
        TypeDiscriminatorRegistry registry = getRegistry(mapId);
        return registry != null ? registry.getEClass(discriminatorValue) : null;
    }

    /**
     * Gets the EClass for a discriminator value, searching all registries.
     * <p>
     * This method searches through all registries and returns the first match.
     * Use {@link #getEClass(String, String)} when the mapId is known.
     * </p>
     *
     * @param discriminatorValue the discriminator value
     * @return the corresponding EClass, or null if not found in any registry
     */
    @Override
    public EClass getEClassFromAny(String discriminatorValue) {
        if (discriminatorValue == null) {
            return null;
        }
        for (TypeDiscriminatorRegistry registry : registries.values()) {
            EClass eClass = registry.getEClass(discriminatorValue);
            if (eClass != null) {
                return eClass;
            }
        }
        return null;
    }

    /**
     * Resolves an EClass from a discriminator value, searching all registries with
     * fallback-aware resolution.
     * <p>
     * Unlike {@link #getEClassFromAny(String)} which only does direct lookup,
     * this method applies each registry's fallback strategy (SKIP, ERROR, FALLBACK)
     * when the discriminator is not found in that registry.
     * </p>
     * <p>
     * The resolution order is:
     * <ol>
     *   <li>Direct lookup across all registries (returns immediately on match)</li>
     *   <li>If no direct match, apply fallback strategy of the first registry
     *       that has a non-SKIP fallback configured</li>
     * </ol>
     * </p>
     *
     * @param discriminatorValue the discriminator value to resolve
     * @param eClassResolver function that resolves EClass URI strings to EClass instances
     * @return the resolved EClass, or null if not found and all strategies are SKIP
     * @throws IllegalStateException if ERROR strategy is active, or FALLBACK with missing fallbackEClass
     */
    @Override
    public EClass resolveFromAny(String discriminatorValue, Function<String, EClass> eClassResolver) {
        if (discriminatorValue == null) {
            return null;
        }

        // Phase 1: direct lookup across all registries
        for (TypeDiscriminatorRegistry registry : registries.values()) {
            EClass eClass = registry.getEClass(discriminatorValue);
            if (eClass != null) {
                return eClass;
            }
        }

        // Phase 2: no direct match — apply fallback strategy
        // Find the first registry with a non-SKIP fallback strategy
        for (TypeDiscriminatorRegistry registry : registries.values()) {
            if (registry.getFallbackStrategy() != FallbackStrategy.SKIP) {
                return registry.resolve(discriminatorValue, eClassResolver);
            }
        }

        // No registry with fallback configured — return null (default SKIP behavior)
        return null;
    }

    /**
     * Gets the discriminator value for an EClass in a specific mapId context.
     *
     * @param mapId the namespace identifier
     * @param eClass the EClass
     * @return the corresponding discriminator value, or null if not found
     */
    @Override
    public String getDiscriminatorValue(String mapId, EClass eClass) {
        TypeDiscriminatorRegistry registry = getRegistry(mapId);
        return registry != null ? registry.getDiscriminatorValue(eClass) : null;
    }

    /**
     * Gets the discriminator value for an EClass, searching all registries.
     *
     * @param eClass the EClass
     * @return the corresponding discriminator value, or null if not found
     */
    @Override
    public String getDiscriminatorValueFromAny(EClass eClass) {
        if (eClass == null) {
            return null;
        }
        for (TypeDiscriminatorRegistry registry : registries.values()) {
            String discriminator = registry.getDiscriminatorValue(eClass);
            if (discriminator != null) {
                return discriminator;
            }
        }
        return null;
    }

    /**
     * Gets the discriminator value for an EClass in a reference-scoped inline mapping.
     * <p>
     * Uses the EReference URI as mapId to look up the inline mapping registry.
     * This is the serialization counterpart to {@link #resolveForReference}.
     * </p>
     *
     * @param reference the EReference with inline mapping
     * @param eClass the EClass to look up
     * @return the discriminator value, or null if no inline mapping exists or EClass not found
     */
    @Override
    public String getDiscriminatorValueForReference(EReference reference, EClass eClass) {
        if (reference == null || eClass == null) {
            return null;
        }
        String mapId = EcoreUtil.getURI(reference).toString();
        return getDiscriminatorValue(mapId, eClass);
    }

    /**
     * Gets the discriminator path for a specific mapId.
     *
     * @param mapId the namespace identifier
     * @return the discriminator path, or null if not set
     */
    @Override
    public String getDiscriminatorPath(String mapId) {
        TypeDiscriminatorRegistry registry = getRegistry(mapId);
        return registry != null ? registry.getDiscriminatorPath() : null;
    }

    /**
     * Gets the first available discriminator path from any registry.
     * <p>
     * This is useful when the mapId is unknown and you need to find
     * the discriminator path to scan the JSON content.
     * </p>
     *
     * @return the first non-null discriminator path, or null if none found
     */
    @Override
    public String getAnyDiscriminatorPath() {
        for (TypeDiscriminatorRegistry registry : registries.values()) {
            String path = registry.getDiscriminatorPath();
            if (path != null) {
                return path;
            }
        }
        return null;
    }

    /**
     * Finds a registry that has a discriminator path set.
     * <p>
     * Used for hint-free deserialization where we need to find which registry
     * can be used for type resolution.
     * </p>
     *
     * @return the first registry with a discriminator path, or null if none
     */
    public TypeDiscriminatorRegistry findRegistryWithPath() {
        for (TypeDiscriminatorRegistry registry : registries.values()) {
            if (registry.getDiscriminatorPath() != null) {
                return registry;
            }
        }
        return null;
    }

    /**
     * Gets the typeMapping mapId for an EClass by scanning its annotations.
     * <p>
     * Looks for annotations with source starting with {@code http://eclipse.org/fennec/codec/typeMapping/}
     * and extracts the mapId suffix. This allows callers to determine which registry
     * should be used for resolution without needing access to the internal aspect objects.
     * </p>
     *
     * @param eClass the EClass to scan
     * @return the mapId, or null if no typeMapping annotation found
     */
    @Override
    public String getMapIdForEClass(EClass eClass) {
        if (eClass == null) {
            return null;
        }
        // Check the EClass itself first
        String mapId = extractMapIdFromAnnotations(eClass);
        if (mapId != null) {
            return mapId;
        }
        // Walk up the supertype hierarchy
        for (EClass superType : eClass.getEAllSuperTypes()) {
            mapId = extractMapIdFromAnnotations(superType);
            if (mapId != null) {
                return mapId;
            }
        }
        return null;
    }

    /**
     * Extracts the mapId from a {@code typeMapping/{mapId}} annotation on an EClass.
     *
     * @param eClass the EClass to check
     * @return the mapId, or null if no typeMapping annotation found
     */
    private String extractMapIdFromAnnotations(EClass eClass) {
        for (EAnnotation ann : eClass.getEAnnotations()) {
            String source = ann.getSource();
            if (source != null && source.startsWith(TYPE_MAPPING_SOURCE_PREFIX)) {
                String mapId = source.substring(TYPE_MAPPING_SOURCE_PREFIX.length());
                if (!mapId.isEmpty()) {
                    return mapId;
                }
            }
        }
        return null;
    }

    /**
     * Returns all registered mapIds.
     */
    public Set<String> getMapIds() {
        return Collections.unmodifiableSet(registries.keySet());
    }

    /**
     * Returns all registries.
     */
    public Collection<TypeDiscriminatorRegistry> getRegistries() {
        return Collections.unmodifiableCollection(registries.values());
    }

    /**
     * Returns the total number of mappings across all registries.
     */
    public int getTotalMappings() {
        return registries.values().stream()
                .mapToInt(TypeDiscriminatorRegistry::size)
                .sum();
    }

    /**
     * Unregisters all discriminator mappings for EClasses from a specific package.
     * <p>
     * This method removes all discriminators that were registered for EClasses
     * belonging to the specified EPackage. Used when an EPackage is dynamically
     * unregistered from the MetadataService.
     * </p>
     *
     * @param packageMetadata the package metadata to unregister
     */
    public void unregisterPackage(PackageMetadata packageMetadata) {
        if (packageMetadata == null) {
            return;
        }
        for (ClassMetadata classMetadata : packageMetadata.getClasses()) {
            unregisterClass(classMetadata);
        }
    }

    /**
     * Unregisters discriminator mapping for a specific EClass.
     *
     * @param classMetadata the class metadata to unregister
     */
    public void unregisterClass(ClassMetadata classMetadata) {
        if (classMetadata == null) {
            return;
        }
        EClass eClass = classMetadata.getEClass();
        if (eClass == null) {
            return;
        }

        // Find and remove from all registries that have this EClass
        for (TypeDiscriminatorRegistry registry : registries.values()) {
            String discriminator = registry.getDiscriminatorValue(eClass);
            if (discriminator != null) {
                registry.unregister(discriminator);
                LOGGER.fine("Unregistered discriminator '" + discriminator + "' for " + eClass.getName());
            }
        }
    }

    /**
     * Clears all registries.
     */
    @Override
    public void clear() {
        registries.clear();
    }

    @Override
    public String toString() {
        return "TypeDiscriminatorService[registries=" + registries.size()
                + ", totalMappings=" + getTotalMappings() + "]";
    }
}
