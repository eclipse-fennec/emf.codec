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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AttributeSerializationEntry}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#64-attribute-serialization">Spec 6.4: Attribute Serialization</a>
 */
@DisplayName("AttributeSerializationEntry")
class AttributeSerializationEntryTest extends SerializationEntryTestBase {

    private FeatureConfig createDefaultConfig(String key, EAttribute attribute) {
        return FeatureConfig.builder()
                .key(key)
                .serializeNull(false)
                .serializeEmpty(false)
                .serializeDefault(false)
                .build();
    }

    @Nested
    @DisplayName("getKey")
    class GetKeyTests {

        @Test
        @DisplayName("returns key from config")
        void returnsKeyFromConfig() {
            FeatureConfig config = createDefaultConfig("name", nameAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);
            assertEquals("name", entry.getKey());
        }

        @Test
        @DisplayName("returns custom key from config")
        void returnsCustomKeyFromConfig() {
            FeatureConfig config = createDefaultConfig("firstName", nameAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);
            assertEquals("firstName", entry.getKey());
        }
    }

    @Nested
    @DisplayName("shouldSerialize")
    class ShouldSerializeTests {

        @Test
        @DisplayName("returns false when serialize is false")
        void returnsFalseWhenSerializeIsFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .ignoreWrite(true)
                    .build();

            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);
            EObject person = createPerson();

            assertFalse(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("returns false for null value by default")
        void returnsFalseForNullValueByDefault() {
            FeatureConfig config = createDefaultConfig("name", nameAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);

            EObject person = createPerson();
            // name is null by default

            assertFalse(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("returns true for null value when serializeNull is true")
        void returnsTrueForNullValueWhenConfigured() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .serializeNull(true)
                    .build();

            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);
            EObject person = createPerson();
            // name is null by default

            assertTrue(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("returns false for empty list by default")
        void returnsFalseForEmptyListByDefault() {
            FeatureConfig config = createDefaultConfig("tags", tagsAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, tagsAttribute);

            EObject person = createPerson();
            // tags is empty by default

            assertFalse(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("returns true for empty list when serializeEmpty is true")
        void returnsTrueForEmptyListWhenConfigured() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("tags")
                    .serializeEmpty(true)
                    .build();

            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, tagsAttribute);
            EObject person = createPerson();
            // tags is empty by default

            assertTrue(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("returns false for default value by default")
        void returnsFalseForDefaultValueByDefault() {
            FeatureConfig config = createDefaultConfig("age", ageAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, ageAttribute);

            EObject person = createPerson();
            // age defaults to 0

            assertFalse(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("returns true for default value when serializeDefaults is true")
        void returnsTrueForDefaultValueWhenConfigured() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("age")
                    .serializeDefault(true)
                    .build();

            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, ageAttribute);
            EObject person = createPerson();
            // age defaults to 0

            assertTrue(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("returns true for non-null, non-default value")
        void returnsTrueForNonNullNonDefaultValue() {
            FeatureConfig config = createDefaultConfig("name", nameAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);

            EObject person = createPerson("John");

            assertTrue(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("forceWrite with non-null value returns true")
        void forceWriteWithNonNullValueReturnsTrue() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .forceWrite(true)
                    .serializeNull(false)
                    .build();

            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);
            EObject person = createPerson("John");

            assertTrue(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("forceWrite with null value respects serializeNull=false")
        void forceWriteWithNullValueRespectsSerializeNullFalse() {
            // forceWrite only overrides ignore for volatile/transient features
            // It does NOT override serializeNull behavior
            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .forceWrite(true)
                    .serializeNull(false)
                    .build();

            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);
            EObject person = createPerson(); // name is null

            assertFalse(entry.shouldSerialize(createState(person)),
                    "forceWrite should NOT override serializeNull=false");
        }

        @Test
        @DisplayName("forceWrite with null value and serializeNull=true returns true")
        void forceWriteWithNullValueAndSerializeNullTrueReturnsTrue() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .forceWrite(true)
                    .serializeNull(true)
                    .build();

            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);
            EObject person = createPerson(); // name is null

            assertTrue(entry.shouldSerialize(createState(person)));
        }

        @Test
        @DisplayName("forceWrite with empty list respects serializeEmpty=false")
        void forceWriteWithEmptyListRespectsSerializeEmptyFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("tags")
                    .forceWrite(true)
                    .serializeEmpty(false)
                    .build();

            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, tagsAttribute);
            EObject person = createPerson(); // tags is empty

            assertFalse(entry.shouldSerialize(createState(person)),
                    "forceWrite should NOT override serializeEmpty=false");
        }

        @Test
        @DisplayName("forceWrite with default value respects serializeDefault=false")
        void forceWriteWithDefaultValueRespectsSerializeDefaultFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("age")
                    .forceWrite(true)
                    .serializeDefault(false)
                    .build();

            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, ageAttribute);
            EObject person = createPerson(); // age defaults to 0

            assertFalse(entry.shouldSerialize(createState(person)),
                    "forceWrite should NOT override serializeDefault=false");
        }
    }

    @Nested
    @DisplayName("serialize")
    class SerializeTests {

        @Test
        @DisplayName("writes string value")
        void writesStringValue() {
            FeatureConfig config = createDefaultConfig("name", nameAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);

            EObject person = createPerson("John");

            entry.serialize(createState(person), generator, null);

            verify(generator).writeName("name");
            verify(generator).writeString("John");
        }

        @Test
        @DisplayName("writes integer value")
        void writesIntegerValue() {
            FeatureConfig config = createDefaultConfig("age", ageAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, ageAttribute);

            EObject person = createPerson();
            person.eSet(ageAttribute, 25);

            entry.serialize(createState(person), generator, null);

            verify(generator).writeName("age");
            verify(generator).writeNumber(25);
        }

        @Test
        @DisplayName("writes null property when value is null")
        void writesNullPropertyWhenValueNull() {
            FeatureConfig config = createDefaultConfig("name", nameAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);

            EObject person = createPerson();
            // name is null by default

            entry.serialize(createState(person), generator, null);

            verify(generator).writeNullProperty("name");
        }

        @Test
        @DisplayName("writes array for multi-valued attribute")
        void writesArrayForMultiValued() {
            FeatureConfig config = createDefaultConfig("tags", tagsAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, tagsAttribute);

            EObject person = createPerson();
            @SuppressWarnings("unchecked")
            EList<String> tags = (EList<String>) person.eGet(tagsAttribute);
            tags.addAll(List.of("tag1", "tag2"));

            entry.serialize(createState(person), generator, null);

            verify(generator).writeName("tags");
            verify(generator).writeStartArray();
            verify(generator).writeString("tag1");
            verify(generator).writeString("tag2");
            verify(generator).writeEndArray();
        }

        @Test
        @DisplayName("writes boolean value")
        void writesBooleanValue() {
            FeatureConfig config = createDefaultConfig("active", activeAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, activeAttribute);

            EObject person = createPerson();
            person.eSet(activeAttribute, true);

            entry.serialize(createState(person), generator, null);

            verify(generator).writeName("active");
            verify(generator).writeBoolean(true);
        }

        @Test
        @DisplayName("writes double value")
        void writesDoubleValue() {
            FeatureConfig config = createDefaultConfig("score", scoreAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, scoreAttribute);

            EObject person = createPerson();
            person.eSet(scoreAttribute, 19.99);

            entry.serialize(createState(person), generator, null);

            verify(generator).writeName("score");
            verify(generator).writeNumber(19.99);
        }

        @Test
        @DisplayName("writes custom key from config")
        void writesCustomKeyFromConfig() {
            FeatureConfig config = createDefaultConfig("firstName", nameAttribute);
            AttributeSerializationEntry entry = new AttributeSerializationEntry(config, nameAttribute);

            EObject person = createPerson("John");

            entry.serialize(createState(person), generator, null);

            verify(generator).writeName("firstName");
            verify(generator).writeString("John");
        }
    }
}
