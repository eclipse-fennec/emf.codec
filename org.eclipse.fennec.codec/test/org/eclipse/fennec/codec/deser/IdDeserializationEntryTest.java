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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;

/**
 * Tests for {@link IdDeserializationEntry}.
 * <p>
 * Tests the ID deserialization for both PLAIN and STRUCTURED formats:
 * <ul>
 *   <li>PLAIN format: Single ID, multiple ID features with separator, type conversions</li>
 *   <li>STRUCTURED format: Nested object with individual fields</li>
 *   <li>Edge cases (null, missing EObject)</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/07-id.md">Spec: ID Serialization</a>
 */
@DisplayName("IdDeserializationEntry")
class IdDeserializationEntryTest extends DeserializationEntryTestBase {

    // ========================================================================
    // Single ID Feature Tests (using Person.id)
    // ========================================================================

    @Nested
    @DisplayName("Single ID Feature")
    class SingleIdFeatureTests {

        @Test
        @DisplayName("deserializes single string ID")
        void deserializesSingleStringId() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);
            assertEquals("_id", entry.getKey());

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"john-123\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("john-123", person.eGet(idAttribute));
            }
        }

        @Test
        @DisplayName("getKey returns configured key")
        void getKeyReturnsConfiguredKey() {
            IdConfig config = IdConfig.builder()
                    .key("customId")
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);
            assertEquals("customId", entry.getKey());
        }

        @Test
        @DisplayName("getIdAttribute returns the ID attribute")
        void getIdAttributeReturnsIdAttribute() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);
            assertEquals(idAttribute, entry.getIdAttribute());
        }
    }

    // ========================================================================
    // Single-entry idFeatures list (issue #112)
    //
    // A one-entry list is a configured id feature like any other: it wins over the
    // eID attribute, which keeps its own value. Covered for both formats, with and
    // without an eID attribute on the class.
    // ========================================================================

    @Nested
    @DisplayName("Single-entry idFeatures list")
    class SingleEntryIdFeaturesTests {

        @Test
        @DisplayName("PLAIN sets the configured feature and leaves the eID attribute alone")
        void plainSetsConfiguredFeatureAndLeavesIdAttributeAlone() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .idFeatures(List.of("name"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);

            EObject person = createPerson();
            person.eSet(idAttribute, "p-123");
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"John Doe\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("John Doe", person.eGet(nameAttribute));
                assertEquals("p-123", person.eGet(idAttribute),
                        "eID attribute must keep its own value when idFeatures names another feature");
            }
        }

        @Test
        @DisplayName("STRUCTURED sets the configured feature and leaves the eID attribute alone")
        void structuredSetsConfiguredFeatureAndLeavesIdAttributeAlone() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .idFeatures(List.of("name"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);

            EObject person = createPerson();
            person.eSet(idAttribute, "p-123");
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"name\": \"John Doe\"}")) {
                entry.deserialize(state, parser, null);
                assertEquals("John Doe", person.eGet(nameAttribute));
                assertEquals("p-123", person.eGet(idAttribute));
            }
        }

        @Test
        @DisplayName("PLAIN sets the configured feature on a class without an eID attribute")
        void plainSetsConfiguredFeatureWithoutIdAttribute() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .idFeatures(List.of("firstName"))
                    .build();

            // MultiIdPerson has no eID attribute at all
            IdDeserializationEntry entry = new IdDeserializationEntry(config, multiIdPersonClass);

            EObject person = createMultiIdPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"John\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("John", person.eGet(firstNameAttribute));
            }
        }

        @Test
        @DisplayName("PLAIN keeps the plain single-id behaviour when the entry is the eID attribute")
        void plainKeepsBehaviourWhenEntryIsTheIdAttribute() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .idFeatures(List.of("orderId"))
                    .build();

            // orderId is the eID attribute - the common case must not change
            IdDeserializationEntry entry = new IdDeserializationEntry(config, orderLineClass);

            EObject orderLine = createOrderLine();
            DeserializationState state = createStateWithObject(orderLine);

            try (JsonParser parser = createParser("\"A\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("A", orderLine.eGet(orderIdAttribute));
            }
        }

        @Test
        @DisplayName("PLAIN does not split a single-entry value on the separator")
        void plainDoesNotSplitSingleEntryValue() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("-")
                    .idFeatures(List.of("name"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // One feature means the whole value belongs to it, separator or not
            try (JsonParser parser = createParser("\"John-Doe-Jr\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("John-Doe-Jr", person.eGet(nameAttribute));
            }
        }
    }

    // ========================================================================
    // Multiple ID Features Tests (using MultiIdPerson)
    // ========================================================================

    @Nested
    @DisplayName("Multiple ID Features")
    class MultipleIdFeaturesTests {

        @Test
        @DisplayName("splits combined ID by separator")
        void splitsCombinedIdBySeparator() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("-")
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, multiIdPersonClass);

            EObject person = createMultiIdPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"John-Doe\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("John", person.eGet(firstNameAttribute));
                assertEquals("Doe", person.eGet(lastNameAttribute));
            }
        }

        @Test
        @DisplayName("uses custom separator")
        void usesCustomSeparator() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("_")
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, multiIdPersonClass);

            EObject person = createMultiIdPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"John_Doe\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("John", person.eGet(firstNameAttribute));
                assertEquals("Doe", person.eGet(lastNameAttribute));
            }
        }

        @Test
        @DisplayName("handles more parts than features")
        void handlesMorePartsThanFeatures() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("-")
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, multiIdPersonClass);

            EObject person = createMultiIdPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"John-Doe-Extra-Parts\"")) {
                entry.deserialize(state, parser, null);
                // Only first two parts should be used
                assertEquals("John", person.eGet(firstNameAttribute));
                assertEquals("Doe", person.eGet(lastNameAttribute));
            }
        }

        @Test
        @DisplayName("handles fewer parts than features")
        void handlesFewerPartsThanFeatures() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("-")
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, multiIdPersonClass);

            EObject person = createMultiIdPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"John\"")) {
                entry.deserialize(state, parser, null);
                // Only firstName should be set
                assertEquals("John", person.eGet(firstNameAttribute));
                assertNull(person.eGet(lastNameAttribute));
            }
        }

        @Test
        @DisplayName("sets the combined value on a separate derived eID attribute")
        void setsCombinedValueOnSeparateDerivedIdAttribute() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("-")
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, derivedKeyPersonClass);

            EObject person = createDerivedKeyPerson();
            DeserializationState state = createStateWithObject(person);

            // "key" is the eID attribute but not an ID feature - it holds the combined value,
            // mirroring the STRUCTURED behavior (issue #108)
            try (JsonParser parser = createParser("\"John-Doe\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("John", person.eGet(derivedFirstNameAttribute));
                assertEquals("Doe", person.eGet(derivedLastNameAttribute));
                assertEquals("John-Doe", person.eGet(derivedKeyAttribute));
            }
        }

        @Test
        @DisplayName("keeps the component value when the eID attribute is itself an ID feature")
        void keepsComponentValueWhenEIdAttributeIsIdFeature() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("-")
                    .idFeatures(List.of("orderId", "lineNo"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, orderLineClass);

            EObject orderLine = createOrderLine();
            DeserializationState state = createStateWithObject(orderLine);

            // orderId is the eID attribute AND the first ID feature - it keeps its component
            // value, the combined "A-2" must not be written over it (issue #108)
            try (JsonParser parser = createParser("\"A-2\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("A", orderLine.eGet(orderIdAttribute));
                assertEquals(2, orderLine.eGet(lineNoAttribute));
            }
        }
    }

    // ========================================================================
    // Null and Edge Case Tests
    // ========================================================================

    @Nested
    @DisplayName("Null and Edge Cases")
    class NullAndEdgeCaseTests {

        @Test
        @DisplayName("handles null ID value")
        void handlesNullIdValue() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("null")) {
                entry.deserialize(state, parser, null);
                // ID should remain null
                assertNull(person.eGet(idAttribute));
            }
        }

        @Test
        @DisplayName("handles EClass without ID attribute")
        void handlesEClassWithoutIdAttribute() {
            // Address class has no ID attribute
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, addressClass);
            assertNull(entry.getIdAttribute());
        }

        @Test
        @DisplayName("handles missing EObject in state")
        void handlesMissingEObjectInState() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);

            DeserializationState state = createState(personClass);
            // Note: NOT creating EObject on state

            try (JsonParser parser = createParser("\"test-id\"")) {
                // Should not throw, just log warning
                entry.deserialize(state, parser, null);
            }
        }
    }

    // ========================================================================
    // Type Conversion Tests (using IntIdEntity and LongIdEntity)
    // ========================================================================

    @Nested
    @DisplayName("Type Conversion")
    class TypeConversionTests {

        @Test
        @DisplayName("converts integer ID values")
        void convertsIntegerIdValues() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, intIdEntityClass);

            EObject entity = createIntIdEntity();
            DeserializationState state = createStateWithObject(entity);

            try (JsonParser parser = createParser("42")) {
                entry.deserialize(state, parser, null);
                assertEquals(42, entity.eGet(intIdAttribute));
            }
        }

        @Test
        @DisplayName("converts long ID values")
        void convertsLongIdValues() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, longIdEntityClass);

            EObject entity = createLongIdEntity();
            DeserializationState state = createStateWithObject(entity);

            try (JsonParser parser = createParser("9876543210")) {
                entry.deserialize(state, parser, null);
                assertEquals(9876543210L, entity.eGet(longIdAttribute));
            }
        }
    }

    // ========================================================================
    // STRUCTURED Format Tests
    // ========================================================================

    @Nested
    @DisplayName("STRUCTURED format")
    class StructuredFormatTests {

        @Test
        @DisplayName("deserializes single ID as nested object")
        void deserializesSingleIdAsNestedObject() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // STRUCTURED format: {"id": "john-123"}
            try (JsonParser parser = createParser("{\"id\": \"john-123\"}")) {
                entry.deserialize(state, parser, null);
                assertEquals("john-123", person.eGet(idAttribute));
            }
        }

        @Test
        @DisplayName("deserializes multiple IDs as nested object")
        void deserializesMultipleIdsAsNestedObject() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, multiIdPersonClass);

            EObject person = createMultiIdPerson();
            DeserializationState state = createStateWithObject(person);

            // STRUCTURED format: {"firstName": "John", "lastName": "Doe"}
            try (JsonParser parser = createParser("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")) {
                entry.deserialize(state, parser, null);
                assertEquals("John", person.eGet(firstNameAttribute));
                assertEquals("Doe", person.eGet(lastNameAttribute));
            }
        }

        @Test
        @DisplayName("reads separator from nested object")
        void readsSeparatorFromNestedObject() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .separator("-")  // Default separator
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, multiIdPersonClass);

            EObject person = createMultiIdPerson();
            DeserializationState state = createStateWithObject(person);

            // STRUCTURED format includes separator field (uses "separator" key for STRUCTURED)
            try (JsonParser parser = createParser("{\"separator\": \"_\", \"firstName\": \"John\", \"lastName\": \"Doe\"}")) {
                entry.deserialize(state, parser, null);
                assertEquals("John", person.eGet(firstNameAttribute));
                assertEquals("Doe", person.eGet(lastNameAttribute));
            }
        }

        @Test
        @DisplayName("handles integer ID in structured format")
        void handlesIntegerIdInStructuredFormat() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, intIdEntityClass);

            EObject entity = createIntIdEntity();
            DeserializationState state = createStateWithObject(entity);

            // STRUCTURED format: {"id": 42}
            try (JsonParser parser = createParser("{\"id\": 42}")) {
                entry.deserialize(state, parser, null);
                assertEquals(42, entity.eGet(intIdAttribute));
            }
        }

        @Test
        @DisplayName("handles long ID in structured format")
        void handlesLongIdInStructuredFormat() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, longIdEntityClass);

            EObject entity = createLongIdEntity();
            DeserializationState state = createStateWithObject(entity);

            // STRUCTURED format: {"id": 9876543210}
            try (JsonParser parser = createParser("{\"id\": 9876543210}")) {
                entry.deserialize(state, parser, null);
                assertEquals(9876543210L, entity.eGet(longIdAttribute));
            }
        }

        @Test
        @DisplayName("handles null values in structured format")
        void handlesNullValuesInStructuredFormat() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, personClass);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // null ID value
            try (JsonParser parser = createParser("null")) {
                entry.deserialize(state, parser, null);
                assertNull(person.eGet(idAttribute));
            }
        }

        @Test
        @DisplayName("ignores unknown fields in structured format")
        void ignoresUnknownFieldsInStructuredFormat() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, multiIdPersonClass);

            EObject person = createMultiIdPerson();
            DeserializationState state = createStateWithObject(person);

            // STRUCTURED format with unknown field
            try (JsonParser parser = createParser("{\"firstName\": \"John\", \"unknownField\": \"ignored\", \"lastName\": \"Doe\"}")) {
                entry.deserialize(state, parser, null);
                assertEquals("John", person.eGet(firstNameAttribute));
                assertEquals("Doe", person.eGet(lastNameAttribute));
            }
        }

        @Test
        @DisplayName("handles partial ID fields in structured format")
        void handlesPartialIdFieldsInStructuredFormat() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, multiIdPersonClass);

            EObject person = createMultiIdPerson();
            DeserializationState state = createStateWithObject(person);

            // STRUCTURED format with only one ID field
            try (JsonParser parser = createParser("{\"firstName\": \"John\"}")) {
                entry.deserialize(state, parser, null);
                assertEquals("John", person.eGet(firstNameAttribute));
                assertNull(person.eGet(lastNameAttribute));
            }
        }

        @Test
        @DisplayName("keeps the component value when the eID attribute is itself an ID feature")
        void keepsComponentValueWhenEIdAttributeIsIdFeature() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .separator("-")
                    .idFeatures(List.of("orderId", "lineNo"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, orderLineClass);

            EObject orderLine = createOrderLine();
            DeserializationState state = createStateWithObject(orderLine);

            // orderId is the eID attribute AND the first ID feature - it must not receive
            // the combined value "A-2" (issue #108)
            try (JsonParser parser = createParser("{\"orderId\": \"A\", \"lineNo\": 2}")) {
                entry.deserialize(state, parser, null);
                assertEquals("A", orderLine.eGet(orderIdAttribute));
                assertEquals(2, orderLine.eGet(lineNoAttribute));
            }
        }

        @Test
        @DisplayName("sets the combined value on a separate derived eID attribute")
        void setsCombinedValueOnSeparateDerivedIdAttribute() {
            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .separator("-")
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdDeserializationEntry entry = new IdDeserializationEntry(config, derivedKeyPersonClass);

            EObject person = createDerivedKeyPerson();
            DeserializationState state = createStateWithObject(person);

            // "key" is the eID attribute but not an ID feature - it holds the combined value
            try (JsonParser parser = createParser("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")) {
                entry.deserialize(state, parser, null);
                assertEquals("John", person.eGet(derivedFirstNameAttribute));
                assertEquals("Doe", person.eGet(derivedLastNameAttribute));
                assertEquals("John-Doe", person.eGet(derivedKeyAttribute));
            }
        }
    }
}
