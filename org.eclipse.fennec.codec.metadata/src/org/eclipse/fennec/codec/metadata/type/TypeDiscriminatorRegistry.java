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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy;

/**
 * Registry for type discriminator mappings within a single mapId scope.
 * <p>
 * This registry maintains a bidirectional mapping between discriminator values
 * and EClasses for a specific mapId (namespace). The MAPPED TypeStrategy uses
 * discriminator values to determine the concrete type during deserialization.
 * </p>
 * <p>
 * Each registry is scoped to a single mapId. Use {@link TypeDiscriminatorService}
 * to manage multiple registries across different mapIds.
 * </p>
 *
 * @see TypeDiscriminatorService
 * @author Mark Hoffmann
 * @since 2025-12-17
 */
public class TypeDiscriminatorRegistry {

    private static final Logger LOGGER = Logger.getLogger(TypeDiscriminatorRegistry.class.getName());

    private final String mapId;
    private final Map<String, EClass> valueToClass = new ConcurrentHashMap<>();
    private final Map<EClass, String> classToValue = new ConcurrentHashMap<>();

    /**
     * The discriminator path for this registry (e.g., "info.profileName" or "_type").
     * <p>
     * This is set when registering the first class with a discriminator path,
     * and applies to all classes in this mapId scope.
     * </p>
     */
    private volatile String discriminatorPath;

    /**
     * Controls behavior when a discriminator value cannot be resolved to an EClass.
     * Default is SKIP (log warning, return null so caller continues to next resolution step).
     */
    private volatile FallbackStrategy fallbackStrategy = FallbackStrategy.SKIP;

    /**
     * Explicit fallback EClass URI. Required when fallbackStrategy is FALLBACK.
     */
    private volatile String fallbackEClass;

    /**
     * Creates a new TypeDiscriminatorRegistry for the given mapId.
     *
     * @param mapId the namespace identifier for this registry
     */
    public TypeDiscriminatorRegistry(String mapId) {
        this.mapId = Objects.requireNonNull(mapId, "mapId must not be null");
    }

    /**
     * Returns the mapId (namespace) of this registry.
     */
    public String getMapId() {
        return mapId;
    }

    /**
     * Returns the discriminator path for this registry.
     * <p>
     * The path can be a simple field name (e.g., "_type", "messageType") or
     * a dot-separated feature path (e.g., "info.profileName", "deviceInfo.deviceProfileName").
     * </p>
     *
     * @return the discriminator path, or null if not set
     */
    public String getDiscriminatorPath() {
        return discriminatorPath;
    }

    /**
     * Sets the discriminator path for this registry.
     * <p>
     * This is typically set when the first class with a discriminator path is registered.
     * All classes in the same mapId scope share the same discriminator path.
     * </p>
     *
     * @param discriminatorPath the path where discriminator values are found
     */
    public void setDiscriminatorPath(String discriminatorPath) {
        String existing = this.discriminatorPath;
        if (existing != null && !existing.equals(discriminatorPath)) {
            LOGGER.warning("[" + mapId + "] Discriminator path changed from '" + existing
                    + "' to '" + discriminatorPath + "'");
        }
        this.discriminatorPath = discriminatorPath;
        LOGGER.fine("[" + mapId + "] Set discriminator path: " + discriminatorPath);
    }

    /**
     * Checks if the discriminator path is a feature path (contains dots).
     * <p>
     * Feature paths like "info.profileName" require scanning nested objects,
     * while simple field names like "_type" are found at the current level.
     * </p>
     *
     * @return true if the path contains dots (is a nested path)
     */
    public boolean isFeaturePath() {
        return discriminatorPath != null && discriminatorPath.contains(".");
    }

    /**
     * Returns the fallback strategy for this registry.
     *
     * @return the fallback strategy (never null, defaults to SKIP)
     */
    public FallbackStrategy getFallbackStrategy() {
        return fallbackStrategy;
    }

    /**
     * Sets the fallback strategy for this registry.
     *
     * @param fallbackStrategy the strategy to use when discriminator value is not found
     */
    public void setFallbackStrategy(FallbackStrategy fallbackStrategy) {
        this.fallbackStrategy = Objects.requireNonNull(fallbackStrategy, "fallbackStrategy must not be null");
    }

    /**
     * Returns the fallback EClass URI.
     *
     * @return the fallback EClass URI, or null if not set
     */
    public String getFallbackEClass() {
        return fallbackEClass;
    }

    /**
     * Sets the fallback EClass URI.
     *
     * @param fallbackEClass the EClass URI to use when strategy is FALLBACK
     */
    public void setFallbackEClass(String fallbackEClass) {
        this.fallbackEClass = fallbackEClass;
    }

    /**
     * Resolves an EClass for a discriminator value, applying the configured fallback strategy.
     * <p>
     * Resolution order:
     * <ol>
     *   <li>Look up discriminator value in this registry's mappings</li>
     *   <li>If not found, apply fallback strategy:
     *     <ul>
     *       <li>{@code SKIP} — return null (caller should continue to next resolution step)</li>
     *       <li>{@code ERROR} — throw CodecException</li>
     *       <li>{@code FALLBACK} — resolve fallbackEClass URI via the provided resolver</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param discriminatorValue the value to resolve
     * @param eClassResolver function that resolves an EClass URI string to an EClass instance
     * @return the resolved EClass, or null when strategy is SKIP and value is not found
     * @throws IllegalStateException on ERROR strategy, or FALLBACK with no fallbackEClass configured
     */
    public EClass resolve(String discriminatorValue, Function<String, EClass> eClassResolver) {
        EClass result = getEClass(discriminatorValue);
        if (result != null) {
            return result;
        }

        switch (fallbackStrategy) {
            case ERROR:
                throw new IllegalStateException("[" + mapId + "] Discriminator value '"
                        + discriminatorValue + "' not found in mapping and fallback strategy is ERROR");
            case FALLBACK:
                if (fallbackEClass == null) {
                    throw new IllegalStateException("[" + mapId + "] Fallback strategy is FALLBACK but no fallbackEClass is configured");
                }
                EClass fallback = eClassResolver.apply(fallbackEClass);
                if (fallback == null) {
                    throw new IllegalStateException("[" + mapId + "] Could not resolve fallbackEClass URI: " + fallbackEClass);
                }
                LOGGER.fine("[" + mapId + "] Using fallback EClass " + fallback.getName()
                        + " for unresolved discriminator '" + discriminatorValue + "'");
                return fallback;
            case SKIP:
            default:
                LOGGER.fine("[" + mapId + "] Discriminator value '" + discriminatorValue
                        + "' not found, strategy is SKIP — continuing to next resolution step");
                return null;
        }
    }

    /**
     * Registers a discriminator value to EClass mapping.
     *
     * @param discriminatorValue the discriminator value (e.g., "Dragino_LSE01")
     * @param eClass the EClass that corresponds to this discriminator
     */
    public void register(String discriminatorValue, EClass eClass) {
        Objects.requireNonNull(discriminatorValue, "discriminatorValue must not be null");
        Objects.requireNonNull(eClass, "eClass must not be null");

        EClass existing = valueToClass.put(discriminatorValue, eClass);
        if (existing != null && existing != eClass) {
            LOGGER.warning("[" + mapId + "] Discriminator '" + discriminatorValue
                    + "' remapped from " + existing.getName() + " to " + eClass.getName());
        }

        classToValue.put(eClass, discriminatorValue);
        LOGGER.fine("[" + mapId + "] Registered '" + discriminatorValue + "' -> " + eClass.getName());
    }

    /**
     * Unregisters a discriminator value mapping.
     *
     * @param discriminatorValue the discriminator value to remove
     * @return the previously mapped EClass, or null if none
     */
    public EClass unregister(String discriminatorValue) {
        if (discriminatorValue == null) {
            return null;
        }
        EClass removed = valueToClass.remove(discriminatorValue);
        if (removed != null) {
            classToValue.remove(removed);
        }
        return removed;
    }

    /**
     * Gets the EClass for a discriminator value.
     *
     * @param discriminatorValue the discriminator value
     * @return the corresponding EClass, or null if not found
     */
    public EClass getEClass(String discriminatorValue) {
        if (discriminatorValue == null) {
            return null;
        }
        return valueToClass.get(discriminatorValue);
    }

    /**
     * Returns an immutable snapshot of this registry's {@code discriminatorValue -> EClass}
     * mappings. Used to compose a per-load effective view from per-package views (B.6).
     *
     * @return an immutable copy of the value-to-class mappings
     */
    public Map<String, EClass> valueMappings() {
        return Map.copyOf(valueToClass);
    }

    /**
     * Gets the discriminator value for an EClass.
     *
     * @param eClass the EClass
     * @return the corresponding discriminator value, or null if not registered
     */
    public String getDiscriminatorValue(EClass eClass) {
        if (eClass == null) {
            return null;
        }
        return classToValue.get(eClass);
    }

    /**
     * Checks if a discriminator value is registered.
     *
     * @param discriminatorValue the discriminator value
     * @return true if the value is registered
     */
    public boolean hasMapping(String discriminatorValue) {
        return discriminatorValue != null && valueToClass.containsKey(discriminatorValue);
    }

    /**
     * Checks if an EClass has a discriminator value registered.
     *
     * @param eClass the EClass
     * @return true if the class has a discriminator
     */
    public boolean hasDiscriminator(EClass eClass) {
        return eClass != null && classToValue.containsKey(eClass);
    }

    /**
     * Returns the number of registered mappings.
     */
    public int size() {
        return valueToClass.size();
    }

    /**
     * Clears all registered mappings.
     */
    public void clear() {
        valueToClass.clear();
        classToValue.clear();
    }

    /**
     * Returns an unmodifiable view of all discriminator values.
     */
    public Set<String> getDiscriminatorValues() {
        return Collections.unmodifiableSet(valueToClass.keySet());
    }

    @Override
    public String toString() {
        return "TypeDiscriminatorRegistry[mapId=" + mapId + ", mappings=" + valueToClass.size() + "]";
    }
}
