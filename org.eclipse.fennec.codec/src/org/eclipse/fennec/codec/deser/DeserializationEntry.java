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
package org.eclipse.fennec.codec.deser;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

/**
 * Interface for deserialization entries that handle specific JSON properties.
 * <p>
 * Each entry is responsible for deserializing a specific aspect of an EObject:
 * <ul>
 *   <li>{@code TypeDeserializationEntry} - resolves _type to EClass</li>
 *   <li>{@code IdDeserializationEntry} - reads _id and sets on EObject</li>
 *   <li>{@code AttributeDeserializationEntry} - reads attribute values</li>
 *   <li>{@code ReferenceDeserializationEntry} - reads references (inline or _ref)</li>
 * </ul>
 * </p>
 * <p>
 * Entries receive pre-resolved effective configuration, eliminating runtime
 * resolution overhead.
 * </p>
 *
 * @see DeserializationState
 * @see <a href="docs/codec-v2-serialization-spec.md#15-deserialization-requirements">Spec 15: Deserialization</a>
 * @author Mark Hoffmann
 * @since 1.0
 */
public interface DeserializationEntry {

    /**
     * Returns the JSON property key this entry handles.
     * <p>
     * This is used to map incoming JSON properties to the correct entry.
     * </p>
     *
     * @return the JSON property key (e.g., "_type", "_id", "name", "address")
     */
    String getKey();

    /**
     * Deserializes the current JSON value into the target EObject.
     * <p>
     * The parser is positioned at the value (after the property name).
     * The entry should read the value and apply it to the EObject in the state.
     * </p>
     *
     * @param state the deserialization state containing the target EObject
     * @param parser the JSON parser positioned at the value
     * @param ctxt the Jackson deserialization context
     */
    void deserialize(DeserializationState state, JsonParser parser, DeserializationContext ctxt);
}
