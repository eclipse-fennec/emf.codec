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
package org.eclipse.fennec.codec.ser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Holds state during serialization of a single EObject.
 * <p>
 * This class provides caching for feature values to avoid duplicate
 * {@code eGet()} calls between {@code shouldSerialize()} and {@code serialize()}
 * methods. This is particularly beneficial for:
 * <ul>
 *   <li>Derived features with computed values</li>
 *   <li>Features with expensive getters</li>
 *   <li>Lazy-loading proxy resolution</li>
 * </ul>
 * </p>
 * <p>
 * A new SerializationState instance should be created for each EObject being
 * serialized. The state is not thread-safe and should not be shared across threads.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#6-eobject-serialization">Spec 6: EObject Serialization</a>
 * @author Mark Hoffmann
 * @since 2025-12-16
 */
public class SerializationState {

    private final EObject eObject;
    private final Map<EStructuralFeature, Object> valueCache;

    /**
     * Marker object for cached null values.
     * We need this because HashMap allows null values, so we can't distinguish
     * between "not cached" and "cached null" without a marker.
     */
    private static final Object NULL_MARKER = new Object();

    /**
     * Creates a new SerializationState for the given EObject.
     *
     * @param eObject the EObject being serialized, must not be null
     * @throws NullPointerException if eObject is null
     */
    public SerializationState(EObject eObject) {
        this.eObject = Objects.requireNonNull(eObject, "eObject must not be null");
        this.valueCache = new HashMap<>();
    }

    /**
     * Returns the EObject being serialized.
     *
     * @return the EObject, never null
     */
    public EObject getEObject() {
        return eObject;
    }

    /**
     * Gets the value of a feature, using cache if available.
     * <p>
     * The first call for a feature will invoke {@code eObject.eGet(feature)}
     * and cache the result. Subsequent calls return the cached value.
     * </p>
     *
     * @param feature the feature to get the value for
     * @return the feature value, may be null
     */
    public Object getValue(EStructuralFeature feature) {
        Objects.requireNonNull(feature, "feature must not be null");

        if (valueCache.containsKey(feature)) {
            Object cached = valueCache.get(feature);
            return cached == NULL_MARKER ? null : cached;
        }

        Object value = eObject.eGet(feature);
        valueCache.put(feature, value == null ? NULL_MARKER : value);
        return value;
    }

    /**
     * Checks if a feature value has been cached.
     *
     * @param feature the feature to check
     * @return true if the value has been cached
     */
    public boolean isCached(EStructuralFeature feature) {
        return valueCache.containsKey(feature);
    }

    /**
     * Clears all cached values.
     * <p>
     * This can be used to force re-evaluation of feature values,
     * though this is rarely needed in practice.
     * </p>
     */
    public void clearCache() {
        valueCache.clear();
    }

    /**
     * Returns the number of cached values.
     *
     * @return the cache size
     */
    public int getCacheSize() {
        return valueCache.size();
    }
}
