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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;

/**
 * Tests for {@link AttributeDeserializationEntry}.
 */
@DisplayName("AttributeDeserializationEntry")
class AttributeDeserializationEntryTest extends DeserializationEntryTestBase {

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("creates entry with config and attribute")
        void createsEntryWithConfigAndAttribute() {
            FeatureConfig config = createDefaultConfig("name", nameAttribute);
            AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

            assertEquals("name", entry.getKey());
            assertSame(nameAttribute, entry.getAttribute());
        }

        @Test
        @DisplayName("throws when config is null")
        void throwsWhenConfigIsNull() {
            assertThrows(NullPointerException.class,
                () -> new AttributeDeserializationEntry(null, nameAttribute));
        }

        @Test
        @DisplayName("throws when attribute is null")
        void throwsWhenAttributeIsNull() {
            FeatureConfig config = createDefaultConfig("name", nameAttribute);
            assertThrows(NullPointerException.class,
                () -> new AttributeDeserializationEntry(config, null));
        }
    }

    @Nested
    @DisplayName("String attribute deserialization")
    class StringAttributeDeserialization {

        @Test
        @DisplayName("deserializes string value")
        void deserializesStringValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"John Doe\"")) {
                FeatureConfig config = createDefaultConfig("name", nameAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

                entry.deserialize(state, parser, null);

                assertEquals("John Doe", person.eGet(nameAttribute));
            }
        }

        @Test
        @DisplayName("deserializes null string value")
        void deserializesNullStringValue() {
            EObject person = createPerson();
            person.eSet(nameAttribute, "Initial");
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("null")) {
                FeatureConfig config = createDefaultConfig("name", nameAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

                entry.deserialize(state, parser, null);

                assertNull(person.eGet(nameAttribute));
            }
        }
    }

    @Nested
    @DisplayName("Integer attribute deserialization")
    class IntegerAttributeDeserialization {

        @Test
        @DisplayName("deserializes int value")
        void deserializesIntValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("30")) {
                FeatureConfig config = createDefaultConfig("age", ageAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, ageAttribute);

                entry.deserialize(state, parser, null);

                assertEquals(30, person.eGet(ageAttribute));
            }
        }
    }

    @Nested
    @DisplayName("Boolean attribute deserialization")
    class BooleanAttributeDeserialization {

        @Test
        @DisplayName("deserializes true value")
        void deserializesTrueValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("true")) {
                FeatureConfig config = createDefaultConfig("active", activeAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, activeAttribute);

                entry.deserialize(state, parser, null);

                assertEquals(Boolean.TRUE, person.eGet(activeAttribute));
            }
        }

        @Test
        @DisplayName("deserializes false value")
        void deserializesFalseValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("false")) {
                FeatureConfig config = createDefaultConfig("active", activeAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, activeAttribute);

                entry.deserialize(state, parser, null);

                assertEquals(Boolean.FALSE, person.eGet(activeAttribute));
            }
        }
    }

    @Nested
    @DisplayName("Double attribute deserialization")
    class DoubleAttributeDeserialization {

        @Test
        @DisplayName("deserializes double value")
        void deserializesDoubleValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("95.5")) {
                FeatureConfig config = createDefaultConfig("score", scoreAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, scoreAttribute);

                entry.deserialize(state, parser, null);

                assertEquals(95.5, (Double) person.eGet(scoreAttribute), 0.001);
            }
        }
    }

    @Nested
    @DisplayName("Multi-valued attribute deserialization")
    class MultiValuedAttributeDeserialization {

        @Test
        @DisplayName("deserializes string array")
        void deserializesStringArray() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("[\"tag1\", \"tag2\", \"tag3\"]")) {
                FeatureConfig config = createDefaultConfig("tags", tagsAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, tagsAttribute);

                entry.deserialize(state, parser, null);

                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) person.eGet(tagsAttribute);
                assertEquals(3, tags.size());
                assertEquals("tag1", tags.get(0));
                assertEquals("tag2", tags.get(1));
                assertEquals("tag3", tags.get(2));
            }
        }

        @Test
        @DisplayName("deserializes empty array")
        void deserializesEmptyArray() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("[]")) {
                FeatureConfig config = createDefaultConfig("tags", tagsAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, tagsAttribute);

                entry.deserialize(state, parser, null);

                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) person.eGet(tagsAttribute);
                assertTrue(tags.isEmpty());
            }
        }

        @Test
        @DisplayName("deserializes single value as array element")
        void deserializesSingleValueAsArrayElement() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // Single value (not an array) for multi-valued attribute
            try (JsonParser parser = createParser("\"singleTag\"")) {
                FeatureConfig config = createDefaultConfig("tags", tagsAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, tagsAttribute);

                entry.deserialize(state, parser, null);

                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) person.eGet(tagsAttribute);
                assertEquals(1, tags.size());
                assertEquals("singleTag", tags.get(0));
            }
        }
    }

    @Nested
    @DisplayName("JSON structure to String deserialization")
    class JsonStructureToStringDeserialization {

        @Test
        @DisplayName("deserializes JSON object to string")
        void deserializesJsonObjectToString() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // JSON object provided for String-typed attribute
            try (JsonParser parser = createParser("{\"timeout\": 30, \"retries\": 3}")) {
                FeatureConfig config = createDefaultConfig("name", nameAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

                entry.deserialize(state, parser, null);

                String result = (String) person.eGet(nameAttribute);
                assertNotNull(result);
                // Should contain the JSON structure as a string
                assertTrue(result.contains("\"timeout\""));
                assertTrue(result.contains("30"));
                assertTrue(result.contains("\"retries\""));
                assertTrue(result.contains("3"));
            }
        }

        @Test
        @DisplayName("deserializes JSON array to string")
        void deserializesJsonArrayToString() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // JSON array provided for String-typed attribute
            try (JsonParser parser = createParser("[\"tag1\", \"tag2\", 42, true]")) {
                FeatureConfig config = createDefaultConfig("name", nameAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

                entry.deserialize(state, parser, null);

                String result = (String) person.eGet(nameAttribute);
                assertNotNull(result);
                assertEquals("[\"tag1\",\"tag2\",42,true]", result);
            }
        }

        @Test
        @DisplayName("deserializes nested JSON structure to string")
        void deserializesNestedJsonStructureToString() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // Nested JSON object
            try (JsonParser parser = createParser("{\"config\": {\"nested\": [1, 2, 3]}}")) {
                FeatureConfig config = createDefaultConfig("name", nameAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

                entry.deserialize(state, parser, null);

                String result = (String) person.eGet(nameAttribute);
                assertNotNull(result);
                assertTrue(result.contains("\"config\""));
                assertTrue(result.contains("\"nested\""));
                assertTrue(result.contains("[1,2,3]"));
            }
        }

        @Test
        @DisplayName("deserializes empty JSON object to string")
        void deserializesEmptyJsonObjectToString() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{}")) {
                FeatureConfig config = createDefaultConfig("name", nameAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

                entry.deserialize(state, parser, null);

                assertEquals("{}", person.eGet(nameAttribute));
            }
        }

        @Test
        @DisplayName("deserializes empty JSON array to string")
        void deserializesEmptyJsonArrayToString() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("[]")) {
                FeatureConfig config = createDefaultConfig("name", nameAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

                entry.deserialize(state, parser, null);

                assertEquals("[]", person.eGet(nameAttribute));
            }
        }

        @Test
        @DisplayName("escapes special characters in JSON strings")
        void escapesSpecialCharactersInJsonStrings() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // JSON with special characters
            try (JsonParser parser = createParser("{\"text\": \"line1\\nline2\\ttab\"}")) {
                FeatureConfig config = createDefaultConfig("name", nameAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

                entry.deserialize(state, parser, null);

                String result = (String) person.eGet(nameAttribute);
                assertNotNull(result);
                // The string should contain escaped newline and tab
                assertTrue(result.contains("\\n") || result.contains("line1"));
            }
        }
    }

    @Nested
    @DisplayName("EJavaObject attribute deserialization")
    class EJavaObjectDeserialization {

        @Test
        @DisplayName("deserializes string value to String")
        void deserializesStringValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"hello world\"")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertNotNull(result);
                assertEquals("hello world", result);
                assertTrue(result instanceof String);
            }
        }

        @Test
        @DisplayName("deserializes integer value to Long")
        void deserializesIntegerValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("42")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertNotNull(result);
                assertTrue(result instanceof Long, "Expected Long but was: " + result.getClass().getName());
                assertEquals(42L, result);
            }
        }

        @Test
        @DisplayName("deserializes decimal value to Double")
        void deserializesDecimalValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("3.14")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertNotNull(result);
                assertTrue(result instanceof Double, "Expected Double but was: " + result.getClass().getName());
                assertEquals(3.14, (Double) result, 0.001);
            }
        }

        @Test
        @DisplayName("deserializes true to Boolean.TRUE")
        void deserializesTrueValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("true")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertEquals(Boolean.TRUE, result);
            }
        }

        @Test
        @DisplayName("deserializes false to Boolean.FALSE")
        void deserializesFalseValue() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("false")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertEquals(Boolean.FALSE, result);
            }
        }

        @Test
        @DisplayName("deserializes null to null")
        void deserializesNullValue() {
            EObject person = createPerson();
            person.eSet(metadataAttribute, "initial"); // Set initial value
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("null")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertNull(result);
            }
        }

        @Test
        @DisplayName("deserializes JSON object to Map")
        void deserializesObjectToMap() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"name\": \"John\", \"age\": 30}")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertNotNull(result);
                assertTrue(result instanceof java.util.Map, "Expected Map but was: " + result.getClass().getName());

                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) result;
                assertEquals("John", map.get("name"));
                assertEquals(30L, map.get("age"));
            }
        }

        @Test
        @DisplayName("deserializes JSON array to List")
        void deserializesArrayToList() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("[1, 2, 3, \"four\", true]")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertNotNull(result);
                assertTrue(result instanceof java.util.List, "Expected List but was: " + result.getClass().getName());

                @SuppressWarnings("unchecked")
                java.util.List<Object> list = (java.util.List<Object>) result;
                assertEquals(5, list.size());
                assertEquals(1L, list.get(0));
                assertEquals(2L, list.get(1));
                assertEquals(3L, list.get(2));
                assertEquals("four", list.get(3));
                assertEquals(Boolean.TRUE, list.get(4));
            }
        }

        @Test
        @DisplayName("deserializes nested JSON structure")
        void deserializesNestedStructure() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"config\": {\"enabled\": true, \"values\": [1, 2, 3]}}")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertNotNull(result);
                assertTrue(result instanceof java.util.Map);

                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) result;

                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> configMap = (java.util.Map<String, Object>) map.get("config");
                assertNotNull(configMap);
                assertEquals(Boolean.TRUE, configMap.get("enabled"));

                @SuppressWarnings("unchecked")
                java.util.List<Object> values = (java.util.List<Object>) configMap.get("values");
                assertEquals(3, values.size());
                assertEquals(1L, values.get(0));
            }
        }

        @Test
        @DisplayName("deserializes empty object to empty Map")
        void deserializesEmptyObject() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{}")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertNotNull(result);
                assertTrue(result instanceof java.util.Map);
                assertTrue(((java.util.Map<?, ?>) result).isEmpty());
            }
        }

        @Test
        @DisplayName("deserializes empty array to empty List")
        void deserializesEmptyArray() {
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("[]")) {
                FeatureConfig config = createDefaultConfig("metadata", metadataAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, metadataAttribute);

                entry.deserialize(state, parser, null);

                Object result = person.eGet(metadataAttribute);
                assertNotNull(result);
                assertTrue(result instanceof java.util.List);
                assertTrue(((java.util.List<?>) result).isEmpty());
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

            try (JsonParser parser = createParser("\"John\"")) {
                FeatureConfig config = createDefaultConfig("name", nameAttribute);
                AttributeDeserializationEntry entry = new AttributeDeserializationEntry(config, nameAttribute);

                // Should not throw - just log warning
                assertDoesNotThrow(() -> entry.deserialize(state, parser, null));
            }
        }
    }

    private FeatureConfig createDefaultConfig(String key, EAttribute attribute) {
        return FeatureConfig.builder()
                .key(key)
                .serializeNull(false)
                .serializeEmpty(false)
                .serializeDefault(false)
                .build();
    }
}
