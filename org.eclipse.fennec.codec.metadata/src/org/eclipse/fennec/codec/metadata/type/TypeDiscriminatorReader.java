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

import java.util.function.Function;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

/**
 * Read-only query interface for type discriminator lookups.
 * <p>
 * This interface extracts the read-only operations from {@link TypeDiscriminatorService}
 * so that consumers (codec serializers/deserializers) can depend on the query interface
 * without needing the full mutable service.
 * </p>
 *
 * @see TypeDiscriminatorService
 * @author Mark Hoffmann
 * @since 1.0
 */
public interface TypeDiscriminatorReader {

    // ========================================================================
    // Serialization (EClass → discriminator value)
    // ========================================================================

    /**
     * Gets the discriminator value for an EClass in a specific mapId context.
     *
     * @param mapId the namespace identifier
     * @param eClass the EClass
     * @return the corresponding discriminator value, or null if not found
     */
    String getDiscriminatorValue(String mapId, EClass eClass);

    /**
     * Gets the discriminator value for an EClass, searching all registries.
     *
     * @param eClass the EClass
     * @return the corresponding discriminator value, or null if not found
     */
    String getDiscriminatorValueFromAny(EClass eClass);

    /**
     * Gets the discriminator value for an EClass in a reference-scoped inline mapping.
     *
     * @param reference the EReference with inline mapping
     * @param eClass the EClass to look up
     * @return the discriminator value, or null if no inline mapping exists or EClass not found
     */
    String getDiscriminatorValueForReference(EReference reference, EClass eClass);

    /**
     * Gets the typeMapping mapId for an EClass by scanning its annotations.
     *
     * @param eClass the EClass to scan
     * @return the mapId, or null if no typeMapping annotation found
     */
    String getMapIdForEClass(EClass eClass);

    // ========================================================================
    // Deserialization (discriminator value → EClass)
    // ========================================================================

    /**
     * Gets the EClass for a discriminator value in a specific mapId context.
     *
     * @param mapId the namespace identifier
     * @param discriminatorValue the discriminator value
     * @return the corresponding EClass, or null if not found
     */
    EClass getEClass(String mapId, String discriminatorValue);

    /**
     * Gets the EClass for a discriminator value, searching all registries.
     *
     * @param discriminatorValue the discriminator value
     * @return the corresponding EClass, or null if not found in any registry
     */
    EClass getEClassFromAny(String discriminatorValue);

    /**
     * Resolves an EClass for a discriminator value in a specific mapId context,
     * applying the registry's fallback strategy.
     *
     * @param mapId the namespace identifier
     * @param discriminatorValue the discriminator value to resolve
     * @param eClassResolver function that resolves EClass URI strings to EClass instances
     * @return the resolved EClass, or null if not found and strategy is SKIP
     */
    EClass resolve(String mapId, String discriminatorValue, Function<String, EClass> eClassResolver);

    /**
     * Resolves an EClass from a discriminator value, searching all registries with
     * fallback-aware resolution.
     *
     * @param discriminatorValue the discriminator value to resolve
     * @param eClassResolver function that resolves EClass URI strings to EClass instances
     * @return the resolved EClass, or null if not found and all strategies are SKIP
     */
    EClass resolveFromAny(String discriminatorValue, Function<String, EClass> eClassResolver);

    /**
     * Resolves an EClass for an inline mapping on an EReference.
     *
     * @param reference the EReference with inline mapping
     * @param discriminatorValue the discriminator value to resolve
     * @param eClassResolver function that resolves EClass URI strings to EClass instances
     * @return the resolved EClass, or null if no inline mapping exists or value not found
     */
    EClass resolveForReference(EReference reference, String discriminatorValue,
            Function<String, EClass> eClassResolver);

    // ========================================================================
    // Path queries
    // ========================================================================

    /**
     * Gets the first available discriminator path from any registry.
     *
     * @return the first non-null discriminator path, or null if none found
     */
    String getAnyDiscriminatorPath();

    /**
     * Gets the discriminator path for a specific mapId.
     *
     * @param mapId the namespace identifier
     * @return the discriminator path, or null if not set
     */
    String getDiscriminatorPath(String mapId);
}
