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

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Interface for serialization entries that write a specific aspect of an EObject.
 * <p>
 * Each implementation handles a specific serialization concern:
 * <ul>
 *   <li>{@link IdSerializationEntry} - ID field serialization</li>
 *   <li>{@link TypeSerializationEntry} - Type information serialization</li>
 *   <li>{@link SuperTypeSerializationEntry} - Supertype information serialization</li>
 *   <li>{@link AttributeSerializationEntry} - EAttribute value serialization</li>
 *   <li>{@link ReferenceSerializationEntry} - EReference value serialization</li>
 * </ul>
 * </p>
 * <p>
 * The {@link SerializationState} parameter provides:
 * <ul>
 *   <li>Access to the EObject being serialized</li>
 *   <li>Caching of feature values to avoid duplicate {@code eGet()} calls</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#6-eobject-serialization">Spec 6: EObject Serialization</a>
 * @author Mark Hoffmann
 * @since 1.0
 */
public interface SerializationEntry {

    /**
     * Returns the JSON property key for this entry.
     *
     * @return the property key
     */
    String getKey();

    /**
     * Serializes this entry's content to the JSON generator.
     *
     * @param state the serialization state containing the EObject and value cache
     * @param gen the JSON generator
     * @param ctxt the serialization context
     */
    void serialize(SerializationState state, JsonGenerator gen, SerializationContext ctxt);

    /**
     * Checks if this entry should be serialized for the given EObject.
     * <p>
     * This allows entries to conditionally skip serialization based on
     * configuration or the object's state. Implementations should use
     * {@link SerializationState#getValue(org.eclipse.emf.ecore.EStructuralFeature)}
     * to access feature values, ensuring the cache is populated for the
     * subsequent {@link #serialize} call.
     * </p>
     *
     * @param state the serialization state containing the EObject and value cache
     * @return true if this entry should be serialized
     */
    default boolean shouldSerialize(SerializationState state) {
        return true;
    }

    /**
     * Checks if this entry should be serialized, with access to serialization context.
     * <p>
     * This overload provides access to the serialization context for entries that
     * need context information (e.g., smart compression suppress type flag).
     * </p>
     *
     * @param state the serialization state containing the EObject and value cache
     * @param ctxt the serialization context
     * @return true if this entry should be serialized
     */
    default boolean shouldSerialize(SerializationState state, SerializationContext ctxt) {
        return shouldSerialize(state);
    }
}
