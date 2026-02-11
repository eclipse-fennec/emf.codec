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

import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;

/**
 * Interface for immutable configuration objects that support cascading merge and validation.
 * <p>
 * The merge pattern allows configuration to be built layer by layer:
 * <pre>
 * T config = T.defaults()
 *     .mergeWith(annotationMap)
 *     .mergeWith(moduleMap)
 *     .mergeWith(factoryMap)
 *     .mergeWith(resourceMap)
 *     .mergeWith(optionsMap)
 *     .validate(diagnostics);
 * </pre>
 * <p>
 * Each merge produces a new immutable instance. The final validate step checks
 * cross-property constraints and collects diagnostics.
 *
 * @param <T> the concrete config type (for fluent API)
 * @see ConfigProperty
 * @see ConfigMergeHelper
 */
public interface Mergeable<T extends Mergeable<T>> {

    /**
     * Creates a new config by merging this config with values from a property map.
     * <p>
     * Values present in the source map override this config's values.
     * Values not present in the source map are kept from this config.
     *
     * @param source the property map to merge (may be null or empty)
     * @return a new immutable config with merged values, or this if source is null/empty
     */
    T mergeWith(Map<String, Object> source);

    /**
     * Validates this config and returns a validated (possibly normalized) config.
     * <p>
     * Validation checks cross-property constraints and collects diagnostics:
     * <ul>
     *   <li>ERROR - invalid configuration that prevents operation</li>
     *   <li>WARNING - configuration that may not work as expected</li>
     *   <li>INFO - informational messages about normalization</li>
     * </ul>
     * <p>
     * The returned config may have invalid values normalized (e.g., setting
     * typeNameKey to null when format is PLAIN).
     *
     * @param diagnostics collector for validation diagnostics
     * @return validated config (may be this if no changes needed)
     */
    T validate(DiagnosticCollector diagnostics);

    /**
     * Merges this config with values from a property map and validates the result.
     * <p>
     * Convenience method equivalent to: {@code mergeWith(source).validate(diagnostics)}
     *
     * @param source the property map to merge (may be null or empty)
     * @param diagnostics collector for validation diagnostics
     * @return a new validated config with merged values
     */
    default T mergeAndValidate(Map<String, Object> source, DiagnosticCollector diagnostics) {
        return mergeWith(source).validate(diagnostics);
    }
}
