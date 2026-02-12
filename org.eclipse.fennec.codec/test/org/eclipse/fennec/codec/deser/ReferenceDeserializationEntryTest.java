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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;

/**
 * Tests for {@link ReferenceDeserializationEntry}.
 */
@DisplayName("ReferenceDeserializationEntry")
class ReferenceDeserializationEntryTest extends DeserializationEntryTestBase {

    private static final String DEFAULT_REF_KEY = "_ref";

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("creates entry with config, reference, and refKey")
        void createsEntryWithConfigReferenceAndRefKey() {
            FeatureConfig config = createDefaultConfig("address", addressRef);
            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(config, addressRef, DEFAULT_REF_KEY);

            assertEquals("address", entry.getKey());
            assertSame(addressRef, entry.getReference());
            assertEquals(DEFAULT_REF_KEY, entry.getRefKey());
        }

        @Test
        @DisplayName("throws when config is null")
        void throwsWhenConfigIsNull() {
            assertThrows(NullPointerException.class,
                () -> new ReferenceDeserializationEntry(null, addressRef, DEFAULT_REF_KEY));
        }

        @Test
        @DisplayName("throws when reference is null")
        void throwsWhenReferenceIsNull() {
            FeatureConfig config = createDefaultConfig("address", addressRef);
            assertThrows(NullPointerException.class,
                () -> new ReferenceDeserializationEntry(config, null, DEFAULT_REF_KEY));
        }

        @Test
        @DisplayName("throws when refKey is null")
        void throwsWhenRefKeyIsNull() {
            FeatureConfig config = createDefaultConfig("address", addressRef);
            assertThrows(NullPointerException.class,
                () -> new ReferenceDeserializationEntry(config, addressRef, null));
        }
    }

    @Nested
    @DisplayName("Null reference deserialization")
    class NullReferenceDeserialization {

        @Test
        @DisplayName("sets null for single-valued reference")
        void setsNullForSingleValuedReference() {
            EObject person = createPerson();
            EObject address = createAddress("123 Main St", "City");
            person.eSet(addressRef, address);
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("null")) {
                FeatureConfig config = createDefaultConfig("address", addressRef);
                ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(config, addressRef, DEFAULT_REF_KEY);

                entry.deserialize(state, parser, null);

                assertNull(person.eGet(addressRef));
            }
        }
    }

    @Nested
    @DisplayName("Non-containment reference deserialization")
    class NonContainmentReferenceDeserialization {

        @Test
        @DisplayName("adds unresolved reference for single-valued non-containment ref")
        void addsUnresolvedReferenceForSingleValuedNonContainmentRef() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // JSON: {"_ref": "#/persons/1"}
            try (JsonParser parser = createParser("{\"_ref\": \"#/persons/1\"}")) {
                FeatureConfig config = createDefaultConfig("manager", managerRef);
                ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(config, managerRef, DEFAULT_REF_KEY);

                entry.deserialize(state, parser, null);

                assertEquals(1, state.getUnresolvedReferences().size());
                DeserializationState.UnresolvedReference unresolved = state.getUnresolvedReferences().get(0);
                assertSame(person, unresolved.getSource());
                assertSame(managerRef, unresolved.getReference());
                assertEquals("#/persons/1", unresolved.getTargetUri());
                assertFalse(unresolved.isMultiValued());
            }
        }

        @Test
        @DisplayName("adds unresolved references for multi-valued non-containment ref")
        void addsUnresolvedReferencesForMultiValuedNonContainmentRef() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // JSON: [{"_ref": "#/persons/1"}, {"_ref": "#/persons/2"}]
            try (JsonParser parser = createParser("[{\"_ref\": \"#/persons/1\"}, {\"_ref\": \"#/persons/2\"}]")) {
                FeatureConfig config = createDefaultConfig("colleagues", colleaguesRef);
                ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(config, colleaguesRef, DEFAULT_REF_KEY);

                entry.deserialize(state, parser, null);

                assertEquals(2, state.getUnresolvedReferences().size());

                DeserializationState.UnresolvedReference ref1 = state.getUnresolvedReferences().get(0);
                assertEquals("#/persons/1", ref1.getTargetUri());
                assertEquals(0, ref1.getIndex());
                assertTrue(ref1.isMultiValued());

                DeserializationState.UnresolvedReference ref2 = state.getUnresolvedReferences().get(1);
                assertEquals("#/persons/2", ref2.getTargetUri());
                assertEquals(1, ref2.getIndex());
            }
        }

        @Test
        @DisplayName("supports direct URI string format for non-containment ref")
        void supportsDirectUriStringFormatForNonContainmentRef() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // JSON: ["#/persons/1", "#/persons/2"] - direct URI strings instead of {_ref: ...}
            try (JsonParser parser = createParser("[\"#/persons/1\", \"#/persons/2\"]")) {
                FeatureConfig config = createDefaultConfig("colleagues", colleaguesRef);
                ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(config, colleaguesRef, DEFAULT_REF_KEY);

                entry.deserialize(state, parser, null);

                assertEquals(2, state.getUnresolvedReferences().size());
                assertEquals("#/persons/1", state.getUnresolvedReferences().get(0).getTargetUri());
                assertEquals("#/persons/2", state.getUnresolvedReferences().get(1).getTargetUri());
            }
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {

        @Test
        @DisplayName("handles missing EObject gracefully")
        void handlesMissingEObjectGracefully() {
            DeserializationState state = createState(personClass);
            // Note: EObject not created yet

            try (JsonParser parser = createParser("{\"_ref\": \"#/persons/1\"}")) {
                FeatureConfig config = createDefaultConfig("manager", managerRef);
                ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(config, managerRef, DEFAULT_REF_KEY);

                // Should not throw - just log warning
                assertDoesNotThrow(() -> entry.deserialize(state, parser, null));
            }
        }
    }

    private FeatureConfig createDefaultConfig(String key, EReference reference) {
        return FeatureConfig.builder()
                .key(key)
                .serializeNull(false)
                .serializeEmpty(false)
                .serializeDefault(false)
                .build();
    }
}
