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
package org.eclipse.fennec.codec.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ConfigProperty} enum and its metadata.
 */
@DisplayName("ConfigProperty")
class ConfigPropertyTest {

    @Nested
    @DisplayName("enum values exist")
    class EnumValuesExist {

        @Test
        @DisplayName("ID properties exist")
        void idPropertiesExist() {
            assertNotNull(ConfigProperty.ID_KEY);
            assertNotNull(ConfigProperty.ID_STRATEGY);
            assertNotNull(ConfigProperty.ID_FORMAT);
            assertNotNull(ConfigProperty.ID_ON_TOP);
            assertNotNull(ConfigProperty.ID_FEATURES);
        }

        @Test
        @DisplayName("TYPE properties exist")
        void typePropertiesExist() {
            assertNotNull(ConfigProperty.TYPE_KEY);
            assertNotNull(ConfigProperty.TYPE_FORMAT);
            assertNotNull(ConfigProperty.TYPE_STRATEGY);
            assertNotNull(ConfigProperty.TYPE_INCLUDE);
        }

        @Test
        @DisplayName("SUPERTYPE properties exist")
        void supertypePropertiesExist() {
            assertNotNull(ConfigProperty.SUPERTYPE_SERIALIZE);
            assertNotNull(ConfigProperty.SUPERTYPE_KEY);
            assertNotNull(ConfigProperty.SUPERTYPE_STRATEGY);
        }

        @Test
        @DisplayName("REFERENCE properties exist")
        void referencePropertiesExist() {
            assertNotNull(ConfigProperty.REF_FORMAT);
            assertNotNull(ConfigProperty.REF_KEY);
            assertNotNull(ConfigProperty.EXPAND);
        }

        @Test
        @DisplayName("FEATURE properties exist")
        void featurePropertiesExist() {
            assertNotNull(ConfigProperty.KEY);
            assertNotNull(ConfigProperty.IGNORE);
            assertNotNull(ConfigProperty.SERIALIZE_NULL);
            assertNotNull(ConfigProperty.IGNORE_READ);
            assertNotNull(ConfigProperty.IGNORE_WRITE);
        }
    }

    @Nested
    @DisplayName("getKey()")
    class GetKey {

        @Test
        @DisplayName("returns short form without codec prefix")
        void returnsShortFormWithoutPrefix() {
            assertEquals("idKey", ConfigProperty.ID_KEY.getKey());
            assertEquals("typeKey", ConfigProperty.TYPE_KEY.getKey());
            assertEquals("refKey", ConfigProperty.REF_KEY.getKey());
        }

        @Test
        @DisplayName("feature properties have short key")
        void featurePropertiesHaveShortKey() {
            assertEquals("key", ConfigProperty.KEY.getKey());
            assertEquals("ignore", ConfigProperty.IGNORE.getKey());
            assertEquals("serializeNull", ConfigProperty.SERIALIZE_NULL.getKey());
        }

        @Test
        @DisplayName("all properties have non-null key")
        void allPropertiesHaveNonNullKey() {
            for (ConfigProperty prop : ConfigProperty.values()) {
                assertNotNull(prop.getKey(), "Property " + prop.name() + " has null key");
                assertFalse(prop.getKey().isEmpty(), "Property " + prop.name() + " has empty key");
            }
        }

        @Test
        @DisplayName("keys do not contain codec prefix")
        void keysDoNotContainCodecPrefix() {
            for (ConfigProperty prop : ConfigProperty.values()) {
                assertFalse(prop.getKey().contains("codec."),
                        "Property " + prop.name() + " key should not contain 'codec.'");
            }
        }
    }

    @Nested
    @DisplayName("getPropertyKey()")
    class GetPropertyKey {

        @Test
        @DisplayName("returns full key with codec prefix")
        void returnsFullKeyWithCodecPrefix() {
            assertEquals("codec.idKey", ConfigProperty.ID_KEY.getPropertyKey());
            assertEquals("codec.typeKey", ConfigProperty.TYPE_KEY.getPropertyKey());
            assertEquals("codec.refKey", ConfigProperty.REF_KEY.getPropertyKey());
        }

        @Test
        @DisplayName("property key is short key prefixed with codec.")
        void propertyKeyIsPrefixed() {
            ConfigProperty prop = ConfigProperty.ID_STRATEGY;
            String expectedKey = "codec." + prop.getKey();
            assertEquals(expectedKey, prop.getPropertyKey());
        }
    }

    @Nested
    @DisplayName("getDefaultValue()")
    class GetDefaultValue {

        @Test
        @DisplayName("ID_KEY default is _id")
        void idKeyDefaultIsUnderscore() {
            assertEquals("_id", ConfigProperty.ID_KEY.getDefaultValue());
        }

        @Test
        @DisplayName("ID_STRATEGY default is ID_FIELD")
        void idStrategyDefaultIsIdField() {
            assertEquals("ID_FIELD", ConfigProperty.ID_STRATEGY.getDefaultValue());
        }

        @Test
        @DisplayName("TYPE_INCLUDE default is true")
        void typeIncludeDefaultIsTrue() {
            assertEquals(true, ConfigProperty.TYPE_INCLUDE.getDefaultValue());
        }

        @Test
        @DisplayName("SUPERTYPE_SERIALIZE default is false")
        void supertypeSerializeDefaultIsFalse() {
            assertEquals(false, ConfigProperty.SUPERTYPE_SERIALIZE.getDefaultValue());
        }

        @Test
        @DisplayName("TYPE_KEY default is _type")
        void typeKeyDefaultIsType() {
            assertEquals("_type", ConfigProperty.TYPE_KEY.getDefaultValue());
        }

        @Test
        @DisplayName("TYPE_STRATEGY default is URI")
        void typeStrategyDefaultIsUri() {
            assertEquals("URI", ConfigProperty.TYPE_STRATEGY.getDefaultValue());
        }

        @Test
        @DisplayName("TYPE_FORMAT default is PLAIN")
        void typeFormatDefaultIsPlain() {
            assertEquals("PLAIN", ConfigProperty.TYPE_FORMAT.getDefaultValue());
        }

        @Test
        @DisplayName("ID_FORMAT default is PLAIN")
        void idFormatDefaultIsPlain() {
            assertEquals("PLAIN", ConfigProperty.ID_FORMAT.getDefaultValue());
        }

        @Test
        @DisplayName("REF_KEY default is $ref")
        void refKeyDefaultIsDollarRef() {
            assertEquals("$ref", ConfigProperty.REF_KEY.getDefaultValue());
        }

        @Test
        @DisplayName("REF_FORMAT default is STRUCTURED")
        void refFormatDefaultIsStructured() {
            assertEquals("STRUCTURED", ConfigProperty.REF_FORMAT.getDefaultValue());
        }

        @Test
        @DisplayName("IGNORE default is false")
        void ignoreDefaultIsFalse() {
            assertEquals(false, ConfigProperty.IGNORE.getDefaultValue());
        }

        @Test
        @DisplayName("ID_FEATURES default is empty list")
        void idFeaturesDefaultIsEmptyList() {
            List<?> defaultValue = ConfigProperty.ID_FEATURES.getDefaultValue();
            assertNotNull(defaultValue);
            assertTrue(defaultValue.isEmpty());
        }

        @Test
        @DisplayName("null defaults are allowed for optional properties")
        void nullDefaultsAreAllowed() {
            assertNull(ConfigProperty.KEY.getDefaultValue());
            assertNull(ConfigProperty.ROOT_TYPE.getDefaultValue());
            assertNull(ConfigProperty.TYPE_MAP_ID.getDefaultValue());
        }
    }

    @Nested
    @DisplayName("getType()")
    class GetType {

        @Test
        @DisplayName("ID_KEY type is String")
        void idKeyTypeIsString() {
            assertEquals(String.class, ConfigProperty.ID_KEY.getType());
        }

        @Test
        @DisplayName("ID_ON_TOP type is Boolean")
        void idOnTopTypeIsBoolean() {
            assertEquals(Boolean.class, ConfigProperty.ID_ON_TOP.getType());
        }

        @Test
        @DisplayName("TYPE_STRATEGY type is String")
        void typeStrategyTypeIsString() {
            assertEquals(String.class, ConfigProperty.TYPE_STRATEGY.getType());
        }

        @Test
        @DisplayName("IGNORE type is Boolean")
        void ignoreTypeIsBoolean() {
            assertEquals(Boolean.class, ConfigProperty.IGNORE.getType());
        }

        @Test
        @DisplayName("ID_FEATURES type is List")
        void idFeaturesTypeIsList() {
            assertEquals(List.class, ConfigProperty.ID_FEATURES.getType());
        }

        @Test
        @DisplayName("TYPE_MAPPINGS type is Map")
        void typeMappingsTypeIsMap() {
            assertEquals(Map.class, ConfigProperty.TYPE_MAPPINGS.getType());
        }

        @Test
        @DisplayName("EXPAND_DEPTH type is Integer")
        void expandDepthTypeIsInteger() {
            assertEquals(Integer.class, ConfigProperty.EXPAND_DEPTH.getType());
        }

        @Test
        @DisplayName("all properties have non-null type")
        void allPropertiesHaveNonNullType() {
            for (ConfigProperty prop : ConfigProperty.values()) {
                assertNotNull(prop.getType(), "Property " + prop.name() + " has null type");
            }
        }
    }

    @Nested
    @DisplayName("getValidLevels()")
    class GetValidLevels {

        @Test
        @DisplayName("ID_KEY has GLOBAL and ECLASS and FEATURE levels")
        void idKeyHasThreeLevels() {
            Set<ConfigLevel> levels = ConfigProperty.ID_KEY.getValidLevels();
            assertEquals(3, levels.size());
            assertTrue(levels.contains(ConfigLevel.GLOBAL));
            assertTrue(levels.contains(ConfigLevel.ECLASS));
            assertTrue(levels.contains(ConfigLevel.FEATURE));
        }

        @Test
        @DisplayName("KEY has only FEATURE level")
        void keyHasOnlyFeatureLevel() {
            Set<ConfigLevel> levels = ConfigProperty.KEY.getValidLevels();
            assertEquals(1, levels.size());
            assertTrue(levels.contains(ConfigLevel.FEATURE));
            assertFalse(levels.contains(ConfigLevel.ECLASS));
            assertFalse(levels.contains(ConfigLevel.GLOBAL));
        }

        @Test
        @DisplayName("TYPE_DISCRIMINATOR has only ECLASS level")
        void typeDiscriminatorHasOnlyEClassLevel() {
            Set<ConfigLevel> levels = ConfigProperty.TYPE_DISCRIMINATOR.getValidLevels();
            assertEquals(1, levels.size());
            assertTrue(levels.contains(ConfigLevel.ECLASS));
            assertFalse(levels.contains(ConfigLevel.FEATURE));
            assertFalse(levels.contains(ConfigLevel.GLOBAL));
        }

        @Test
        @DisplayName("ID_FEATURES has only ECLASS level")
        void idFeaturesHasOnlyEClassLevel() {
            Set<ConfigLevel> levels = ConfigProperty.ID_FEATURES.getValidLevels();
            assertEquals(1, levels.size());
            assertTrue(levels.contains(ConfigLevel.ECLASS));
        }

        @Test
        @DisplayName("TYPE_STRATEGY has GLOBAL, ECLASS, FEATURE levels")
        void typeStrategyHasThreeLevels() {
            Set<ConfigLevel> levels = ConfigProperty.TYPE_STRATEGY.getValidLevels();
            assertEquals(3, levels.size());
            assertTrue(levels.contains(ConfigLevel.GLOBAL));
            assertTrue(levels.contains(ConfigLevel.ECLASS));
            assertTrue(levels.contains(ConfigLevel.FEATURE));
        }

        @Test
        @DisplayName("REF_KEY has GLOBAL and FEATURE levels (but not ECLASS)")
        void refKeyHasTwoLevels() {
            Set<ConfigLevel> levels = ConfigProperty.REF_KEY.getValidLevels();
            assertEquals(2, levels.size());
            assertTrue(levels.contains(ConfigLevel.GLOBAL));
            assertTrue(levels.contains(ConfigLevel.FEATURE));
            assertFalse(levels.contains(ConfigLevel.ECLASS));
        }

        @Test
        @DisplayName("all properties have at least one valid level")
        void allPropertiesHaveAtLeastOneLevel() {
            for (ConfigProperty prop : ConfigProperty.values()) {
                assertFalse(prop.getValidLevels().isEmpty(),
                        "Property " + prop.name() + " has no valid levels");
            }
        }

        @Test
        @DisplayName("isValidAt works correctly")
        void isValidAtWorksCorrectly() {
            assertTrue(ConfigProperty.ID_KEY.isValidAt(ConfigLevel.GLOBAL));
            assertTrue(ConfigProperty.ID_KEY.isValidAt(ConfigLevel.ECLASS));
            assertTrue(ConfigProperty.ID_KEY.isValidAt(ConfigLevel.FEATURE));

            assertTrue(ConfigProperty.KEY.isValidAt(ConfigLevel.FEATURE));
            assertFalse(ConfigProperty.KEY.isValidAt(ConfigLevel.GLOBAL));
            assertFalse(ConfigProperty.KEY.isValidAt(ConfigLevel.ECLASS));
        }
    }

    @Nested
    @DisplayName("getDirections() / getWriteDirections()")
    class GetDirections {

        @Test
        @DisplayName("ID_KEY applies to both READ and WRITE")
        void idKeyAppliesToBothDirections() {
            Set<ConfigDirection> directions = ConfigProperty.ID_KEY.getDirections();
            assertEquals(2, directions.size());
            assertTrue(directions.contains(ConfigDirection.READ));
            assertTrue(directions.contains(ConfigDirection.WRITE));
        }

        @Test
        @DisplayName("SUPERTYPE_SERIALIZE primarily applies to WRITE")
        void supertypeSerializePrimarilyWriteWithFutureRead() {
            Set<ConfigDirection> directions = ConfigProperty.SUPERTYPE_SERIALIZE.getDirections();
            assertEquals(1, directions.size());
            assertTrue(directions.contains(ConfigDirection.WRITE));

            Set<ConfigDirection> futureDirections = ConfigProperty.SUPERTYPE_SERIALIZE.getFutureDirections();
            assertTrue(futureDirections.contains(ConfigDirection.READ));
        }

        @Test
        @DisplayName("TYPE_VALUE_READER_NAME applies only to READ")
        void typeValueReaderNameAppliesOnlyToRead() {
            Set<ConfigDirection> directions = ConfigProperty.TYPE_VALUE_READER_NAME.getDirections();
            assertEquals(1, directions.size());
            assertTrue(directions.contains(ConfigDirection.READ));
            assertFalse(directions.contains(ConfigDirection.WRITE));
        }

        @Test
        @DisplayName("VALUE_WRITER_NAME applies only to WRITE")
        void valueWriterNameAppliesOnlyToWrite() {
            Set<ConfigDirection> directions = ConfigProperty.VALUE_WRITER_NAME.getDirections();
            assertEquals(1, directions.size());
            assertTrue(directions.contains(ConfigDirection.WRITE));
            assertFalse(directions.contains(ConfigDirection.READ));
        }

        @Test
        @DisplayName("appliesTo checks primary directions")
        void appliesToChecksPrimary() {
            assertTrue(ConfigProperty.ID_KEY.appliesTo(ConfigDirection.READ));
            assertTrue(ConfigProperty.ID_KEY.appliesTo(ConfigDirection.WRITE));

            assertTrue(ConfigProperty.SUPERTYPE_SERIALIZE.appliesTo(ConfigDirection.WRITE));
            assertFalse(ConfigProperty.SUPERTYPE_SERIALIZE.appliesTo(ConfigDirection.READ));

            assertTrue(ConfigProperty.SUPERTYPE_SERIALIZE.appliesToIncludingFuture(ConfigDirection.READ));
        }

        @Test
        @DisplayName("all properties have at least one direction")
        void allPropertiesHaveAtLeastOneDirection() {
            for (ConfigProperty prop : ConfigProperty.values()) {
                int primaryCount = prop.getDirections().size();
                int futureCount = prop.getFutureDirections().size();
                assertTrue(primaryCount > 0 || futureCount > 0,
                        "Property " + prop.name() + " has no directions");
            }
        }
    }

    @Nested
    @DisplayName("getFutureDirections()")
    class GetFutureDirections {

        @Test
        @DisplayName("SUPERTYPE_SERIALIZE has READ as future direction")
        void supertypeSerializeHasReadAsFuture() {
            Set<ConfigDirection> futureDirections = ConfigProperty.SUPERTYPE_SERIALIZE.getFutureDirections();
            assertEquals(1, futureDirections.size());
            assertTrue(futureDirections.contains(ConfigDirection.READ));
        }

        @Test
        @DisplayName("SERIALIZE_NULL has READ as future direction")
        void serializeNullHasReadAsFuture() {
            Set<ConfigDirection> futureDirections = ConfigProperty.SERIALIZE_NULL.getFutureDirections();
            assertTrue(futureDirections.contains(ConfigDirection.READ));
        }

        @Test
        @DisplayName("properties with future directions support appliesToIncludingFuture")
        void propertiesWithFutureDirectionsAreDetectable() {
            assertTrue(ConfigProperty.SUPERTYPE_SERIALIZE.appliesToIncludingFuture(ConfigDirection.READ));
            assertFalse(ConfigProperty.SUPERTYPE_SERIALIZE.appliesTo(ConfigDirection.READ));
        }
    }

    @Nested
    @DisplayName("lookup methods")
    class LookupMethods {

        @Test
        @DisplayName("byKey finds property by short key")
        void byKeyFindsProperty() {
            ConfigProperty prop = ConfigProperty.byKey("idKey");
            assertNotNull(prop);
            assertEquals(ConfigProperty.ID_KEY, prop);
        }

        @Test
        @DisplayName("byKey returns null for unknown key")
        void byKeyReturnsNullForUnknown() {
            ConfigProperty prop = ConfigProperty.byKey("unknownKey");
            assertNull(prop);
        }

        @Test
        @DisplayName("byPropertyKey finds property by full key with codec prefix")
        void byPropertyKeyFindsProperty() {
            ConfigProperty prop = ConfigProperty.byPropertyKey("codec.idKey");
            assertNotNull(prop);
            assertEquals(ConfigProperty.ID_KEY, prop);
        }

        @Test
        @DisplayName("byPropertyKey returns null for key without codec prefix")
        void byPropertyKeyReturnsNullWithoutPrefix() {
            ConfigProperty prop = ConfigProperty.byPropertyKey("idKey");
            assertNull(prop);
        }

        @Test
        @DisplayName("byPropertyKey returns null for null input")
        void byPropertyKeyReturnsNullForNull() {
            ConfigProperty prop = ConfigProperty.byPropertyKey(null);
            assertNull(prop);
        }

        @Test
        @DisplayName("byPropertyKey handles various properties")
        void byPropertyKeyHandlesVariousProperties() {
            assertEquals(ConfigProperty.TYPE_KEY, ConfigProperty.byPropertyKey("codec.typeKey"));
            assertEquals(ConfigProperty.SUPERTYPE_SERIALIZE,
                    ConfigProperty.byPropertyKey("codec.superTypeSerialize"));
            assertEquals(ConfigProperty.REF_FORMAT, ConfigProperty.byPropertyKey("codec.refFormat"));
            assertEquals(ConfigProperty.IGNORE, ConfigProperty.byPropertyKey("codec.ignore"));
        }
    }

    @Nested
    @DisplayName("consistency checks")
    class ConsistencyChecks {

        @Test
        @DisplayName("all enum values have valid configuration")
        void allEnumValuesHaveValidConfiguration() {
            for (ConfigProperty prop : ConfigProperty.values()) {
                assertNotNull(prop.getKey(), "Property " + prop.name() + " has null key");
                assertNotNull(prop.getType(), "Property " + prop.name() + " has null type");
                assertFalse(prop.getValidLevels().isEmpty(),
                        "Property " + prop.name() + " has no valid levels");
                assertFalse(prop.getDirections().isEmpty() && prop.getFutureDirections().isEmpty(),
                        "Property " + prop.name() + " has no directions");
            }
        }

        @Test
        @DisplayName("property keys are unique")
        void propertyKeysAreUnique() {
            Set<String> keys = new java.util.HashSet<>();
            for (ConfigProperty prop : ConfigProperty.values()) {
                String key = prop.getKey();
                assertTrue(keys.add(key), "Duplicate key: " + key);
            }
        }

        @Test
        @DisplayName("getPropertyKey returns unique values")
        void propertyKeysWithCodecPrefixAreUnique() {
            Set<String> keys = new java.util.HashSet<>();
            for (ConfigProperty prop : ConfigProperty.values()) {
                String propertyKey = prop.getPropertyKey();
                assertTrue(keys.add(propertyKey), "Duplicate property key: " + propertyKey);
            }
        }

        @Test
        @DisplayName("byKey and byPropertyKey are consistent")
        void byKeyAndByPropertyKeyAreConsistent() {
            for (ConfigProperty prop : ConfigProperty.values()) {
                ConfigProperty byShortKey = ConfigProperty.byKey(prop.getKey());
                ConfigProperty byFullKey = ConfigProperty.byPropertyKey(prop.getPropertyKey());

                assertEquals(prop, byShortKey, "Inconsistent byKey for " + prop.name());
                assertEquals(prop, byFullKey, "Inconsistent byPropertyKey for " + prop.name());
            }
        }

        @Test
        @DisplayName("all level combinations are valid")
        void allLevelCombinationsAreValid() {
            // Just verify that level sets contain only valid ConfigLevel values
            for (ConfigProperty prop : ConfigProperty.values()) {
                Set<ConfigLevel> levels = prop.getValidLevels();
                for (ConfigLevel level : levels) {
                    assertNotNull(level, "Property " + prop.name() + " has null level");
                }
            }
        }
    }

    @Nested
    @DisplayName("specific property combinations")
    class SpecificPropertyCombinations {

        @Test
        @DisplayName("ID properties have appropriate configurations")
        void idPropertiesHaveAppropriateConfigurations() {
            // ID_KEY should be usable at all three levels
            assertTrue(ConfigProperty.ID_KEY.isValidAt(ConfigLevel.FEATURE));
            assertTrue(ConfigProperty.ID_KEY.isValidAt(ConfigLevel.ECLASS));
            assertTrue(ConfigProperty.ID_KEY.isValidAt(ConfigLevel.GLOBAL));

            // ID_FEATURES should only be at ECLASS level
            assertTrue(ConfigProperty.ID_FEATURES.isValidAt(ConfigLevel.ECLASS));
            assertFalse(ConfigProperty.ID_FEATURES.isValidAt(ConfigLevel.FEATURE));
            assertFalse(ConfigProperty.ID_FEATURES.isValidAt(ConfigLevel.GLOBAL));
        }

        @Test
        @DisplayName("TYPE properties have appropriate configurations")
        void typePropertiesHaveAppropriateConfigurations() {
            // Type-related properties should generally be at multiple levels
            assertTrue(ConfigProperty.TYPE_KEY.isValidAt(ConfigLevel.FEATURE));
            assertTrue(ConfigProperty.TYPE_KEY.isValidAt(ConfigLevel.ECLASS));
            assertTrue(ConfigProperty.TYPE_KEY.isValidAt(ConfigLevel.GLOBAL));

            // Scope properties are typically global only
            assertTrue(ConfigProperty.TYPE_SCOPE.isValidAt(ConfigLevel.GLOBAL));
            assertFalse(ConfigProperty.TYPE_SCOPE.isValidAt(ConfigLevel.ECLASS));
            assertFalse(ConfigProperty.TYPE_SCOPE.isValidAt(ConfigLevel.FEATURE));
        }

        @Test
        @DisplayName("REFERENCE properties have appropriate configurations")
        void referencePropertiesHaveAppropriateConfigurations() {
            // REF_KEY should be at GLOBAL and FEATURE levels (not ECLASS per spec)
            assertTrue(ConfigProperty.REF_KEY.isValidAt(ConfigLevel.FEATURE));
            assertTrue(ConfigProperty.REF_KEY.isValidAt(ConfigLevel.GLOBAL));
            assertFalse(ConfigProperty.REF_KEY.isValidAt(ConfigLevel.ECLASS));

            // EXPAND should be usable at all levels
            assertTrue(ConfigProperty.EXPAND.isValidAt(ConfigLevel.FEATURE));
            assertTrue(ConfigProperty.EXPAND.isValidAt(ConfigLevel.ECLASS));
            assertTrue(ConfigProperty.EXPAND.isValidAt(ConfigLevel.GLOBAL));
        }

        @Test
        @DisplayName("SUPERTYPE properties primarily write with future read")
        void supertypePropertiesPrimarilyWrite() {
            // Check a few SUPERTYPE properties have the (R)W pattern
            assertTrue(ConfigProperty.SUPERTYPE_SERIALIZE.appliesTo(ConfigDirection.WRITE));
            assertTrue(ConfigProperty.SUPERTYPE_SERIALIZE.appliesToIncludingFuture(ConfigDirection.READ));

            assertTrue(ConfigProperty.SUPERTYPE_KEY.appliesTo(ConfigDirection.WRITE));
            assertTrue(ConfigProperty.SUPERTYPE_KEY.appliesToIncludingFuture(ConfigDirection.READ));
        }
    }

    @Nested
    @DisplayName("read-only and write-only properties")
    class ReadOnlyAndWriteOnlyProperties {

        @Test
        @DisplayName("READ-only properties don't apply to WRITE")
        void readOnlyProperties() {
            assertTrue(ConfigProperty.TYPE_VALUE_READER_NAME.appliesTo(ConfigDirection.READ));
            assertFalse(ConfigProperty.TYPE_VALUE_READER_NAME.appliesTo(ConfigDirection.WRITE));

            assertTrue(ConfigProperty.STRICT_ON_UNKNOWN.appliesTo(ConfigDirection.READ));
            assertFalse(ConfigProperty.STRICT_ON_UNKNOWN.appliesTo(ConfigDirection.WRITE));
        }

        @Test
        @DisplayName("WRITE-only properties don't apply to READ")
        void writeOnlyProperties() {
            assertTrue(ConfigProperty.VALUE_WRITER_NAME.appliesTo(ConfigDirection.WRITE));
            assertFalse(ConfigProperty.VALUE_WRITER_NAME.appliesTo(ConfigDirection.READ));

            assertTrue(ConfigProperty.SERIALIZE_INSTANCE_TYPE.appliesTo(ConfigDirection.WRITE));
            assertFalse(ConfigProperty.SERIALIZE_INSTANCE_TYPE.appliesTo(ConfigDirection.READ));
        }

        @Test
        @DisplayName("RW properties apply to both READ and WRITE")
        void rwProperties() {
            // Test a few key RW properties
            assertTrue(ConfigProperty.ID_KEY.appliesTo(ConfigDirection.READ));
            assertTrue(ConfigProperty.ID_KEY.appliesTo(ConfigDirection.WRITE));

            assertTrue(ConfigProperty.IGNORE.appliesTo(ConfigDirection.READ));
            assertTrue(ConfigProperty.IGNORE.appliesTo(ConfigDirection.WRITE));

            assertTrue(ConfigProperty.EXPAND.appliesTo(ConfigDirection.READ));
            assertTrue(ConfigProperty.EXPAND.appliesTo(ConfigDirection.WRITE));
        }
    }

    @Nested
    @DisplayName("edge cases")
    class EdgeCases {

        @Test
        @DisplayName("empty directions set never occurs")
        void emptyDirectionsSetNeverOccurs() {
            for (ConfigProperty prop : ConfigProperty.values()) {
                assertTrue(!prop.getDirections().isEmpty() || !prop.getFutureDirections().isEmpty(),
                        "Property " + prop.name() + " has no directions at all");
            }
        }

        @Test
        @DisplayName("properties with List type")
        void propertiesWithListType() {
            assertEquals(List.class, ConfigProperty.ID_FEATURES.getType());
            assertEquals(List.class, ConfigProperty.IGNORE_FEATURES.getType());

            // Verify their defaults are lists (empty or not)
            Object idFeaturesDefault = ConfigProperty.ID_FEATURES.getDefaultValue();
            assertTrue(idFeaturesDefault instanceof List || idFeaturesDefault == null);
        }

        @Test
        @DisplayName("properties with Map type")
        void propertiesWithMapType() {
            assertEquals(Map.class, ConfigProperty.TYPE_MAPPINGS.getType());
            assertEquals(Map.class, ConfigProperty.VALUE_READERS.getType());
            assertEquals(Map.class, ConfigProperty.FEATURE_VALUE_WRITERS.getType());
        }

        @Test
        @DisplayName("properties with Integer type")
        void propertiesWithIntegerType() {
            assertEquals(Integer.class, ConfigProperty.EXPAND_DEPTH.getType());
            Object defaultValue = ConfigProperty.EXPAND_DEPTH.getDefaultValue();
            assertTrue(defaultValue instanceof Integer);
            assertEquals(Integer.valueOf(1), (Integer) defaultValue);
        }
    }
}
