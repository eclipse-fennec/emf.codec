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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ConfigMergeHelper} utility class.
 */
@DisplayName("ConfigMergeHelper")
class ConfigMergeHelperTest {

    /**
     * Test enum for getEnum() tests.
     */
    enum ColorMode {
        RGB, CMYK, GRAYSCALE
    }

    @Nested
    @DisplayName("getValue()")
    class GetValue {

        @Test
        @DisplayName("returns null when source is null")
        void returnsNullWhenSourceIsNull() {
            Object result = ConfigMergeHelper.getValue(null, ConfigProperty.ID_KEY);

            assertNull(result);
        }

        @Test
        @DisplayName("returns null when property is null")
        void returnsNullWhenPropertyIsNull() {
            Map<String, Object> source = Map.of("idKey", "testValue");

            Object result = ConfigMergeHelper.getValue(source, null);

            assertNull(result);
        }

        @Test
        @DisplayName("returns value using short key")
        void returnsValueUsingShortKey() {
            Map<String, Object> source = Map.of("idKey", "customId");

            String result = ConfigMergeHelper.getValue(source, ConfigProperty.ID_KEY);

            assertEquals("customId", result);
        }

        @Test
        @DisplayName("returns value using prefixed key")
        void returnsValueUsingPrefixedKey() {
            Map<String, Object> source = Map.of("codec.idKey", "prefixedId");

            String result = ConfigMergeHelper.getValue(source, ConfigProperty.ID_KEY);

            assertEquals("prefixedId", result);
        }

        @Test
        @DisplayName("returns null when property not in source")
        void returnsNullWhenPropertyNotInSource() {
            Map<String, Object> source = Map.of("otherKey", "otherValue");

            Object result = ConfigMergeHelper.getValue(source, ConfigProperty.ID_KEY);

            assertNull(result);
        }

        @Test
        @DisplayName("short key takes precedence over prefixed key")
        void shortKeyTakesPrecedenceOverPrefixedKey() {
            Map<String, Object> source = new HashMap<>();
            source.put("idKey", "shortValue");
            source.put("codec.idKey", "prefixedValue");

            String result = ConfigMergeHelper.getValue(source, ConfigProperty.ID_KEY);

            assertEquals("shortValue", result);
        }

        @Test
        @DisplayName("returns correct type for different properties")
        void returnsCorrectTypeForDifferentProperties() {
            Map<String, Object> source = new HashMap<>();
            source.put("idKey", "testId");
            source.put("idStrategy", "COMBINED");
            source.put("idOnTop", true);

            String id = ConfigMergeHelper.getValue(source, ConfigProperty.ID_KEY);
            String strategy = ConfigMergeHelper.getValue(source, ConfigProperty.ID_STRATEGY);
            Boolean onTop = ConfigMergeHelper.getValue(source, ConfigProperty.ID_ON_TOP);

            assertEquals("testId", id);
            assertEquals("COMBINED", strategy);
            assertTrue(onTop);
        }
    }

    @Nested
    @DisplayName("getValueOrElse()")
    class GetValueOrElse {

        @Test
        @DisplayName("returns value when present")
        void returnsValueWhenPresent() {
            Map<String, Object> source = Map.of("idKey", "customId");

            String result = ConfigMergeHelper.getValueOrElse(source, ConfigProperty.ID_KEY, "_id");

            assertEquals("customId", result);
        }

        @Test
        @DisplayName("returns fallback when value is null")
        void returnsFallbackWhenValueIsNull() {
            Map<String, Object> source = Map.of();

            String result = ConfigMergeHelper.getValueOrElse(source, ConfigProperty.ID_KEY, "_id");

            assertEquals("_id", result);
        }

        @Test
        @DisplayName("returns fallback when source is null")
        void returnsFallbackWhenSourceIsNull() {
            String result = ConfigMergeHelper.getValueOrElse(null, ConfigProperty.ID_KEY, "_id");

            assertEquals("_id", result);
        }

        @Test
        @DisplayName("returns fallback when property is null")
        void returnsFallbackWhenPropertyIsNull() {
            Map<String, Object> source = Map.of("idKey", "value");

            String result = ConfigMergeHelper.getValueOrElse(source, null, "_id");

            assertEquals("_id", result);
        }

        @Test
        @DisplayName("allows null fallback value")
        void allowsNullFallbackValue() {
            Map<String, Object> source = Map.of();

            String result = ConfigMergeHelper.getValueOrElse(source, ConfigProperty.ID_KEY, null);

            assertNull(result);
        }

        @Test
        @DisplayName("uses prefixed key when short key not present")
        void usesPrefixedKeyWhenShortKeyNotPresent() {
            Map<String, Object> source = Map.of("codec.idKey", "prefixedId");

            String result = ConfigMergeHelper.getValueOrElse(source, ConfigProperty.ID_KEY, "_id");

            assertEquals("prefixedId", result);
        }
    }

    @Nested
    @DisplayName("hasValue()")
    class HasValue {

        @Test
        @DisplayName("returns false when source is null")
        void returnsFalseWhenSourceIsNull() {
            boolean result = ConfigMergeHelper.hasValue(null, ConfigProperty.ID_KEY);

            assertFalse(result);
        }

        @Test
        @DisplayName("returns false when property is null")
        void returnsFalseWhenPropertyIsNull() {
            Map<String, Object> source = Map.of("idKey", "value");

            boolean result = ConfigMergeHelper.hasValue(source, null);

            assertFalse(result);
        }

        @Test
        @DisplayName("returns true when short key present")
        void returnsTrueWhenShortKeyPresent() {
            Map<String, Object> source = Map.of("idKey", "value");

            boolean result = ConfigMergeHelper.hasValue(source, ConfigProperty.ID_KEY);

            assertTrue(result);
        }

        @Test
        @DisplayName("returns true when prefixed key present")
        void returnsTrueWhenPrefixedKeyPresent() {
            Map<String, Object> source = Map.of("codec.idKey", "value");

            boolean result = ConfigMergeHelper.hasValue(source, ConfigProperty.ID_KEY);

            assertTrue(result);
        }

        @Test
        @DisplayName("returns false when neither key present")
        void returnsFalseWhenNeitherKeyPresent() {
            Map<String, Object> source = Map.of("otherKey", "value");

            boolean result = ConfigMergeHelper.hasValue(source, ConfigProperty.ID_KEY);

            assertFalse(result);
        }

        @Test
        @DisplayName("returns true when key present even if value is null")
        void returnsTrueWhenKeyPresentEvenIfValueIsNull() {
            Map<String, Object> source = new HashMap<>();
            source.put("idKey", null);

            boolean result = ConfigMergeHelper.hasValue(source, ConfigProperty.ID_KEY);

            assertTrue(result);
        }

        @Test
        @DisplayName("returns true when either short or prefixed key present")
        void returnsTrueWhenEitherKeyPresent() {
            Map<String, Object> source1 = Map.of("idKey", "value");
            Map<String, Object> source2 = Map.of("codec.idKey", "value");

            assertTrue(ConfigMergeHelper.hasValue(source1, ConfigProperty.ID_KEY));
            assertTrue(ConfigMergeHelper.hasValue(source2, ConfigProperty.ID_KEY));
        }
    }

    @Nested
    @DisplayName("getString()")
    class GetString {

        @Test
        @DisplayName("returns string value")
        void returnsStringValue() {
            Map<String, Object> source = Map.of("idKey", "customId");

            String result = ConfigMergeHelper.getString(source, ConfigProperty.ID_KEY, "_id");

            assertEquals("customId", result);
        }

        @Test
        @DisplayName("converts non-string to string via toString()")
        void convertsNonStringToStringViaToString() {
            Map<String, Object> source = Map.of("idOnTop", true);

            String result = ConfigMergeHelper.getString(source, ConfigProperty.ID_ON_TOP, "false");

            assertEquals("true", result);
        }

        @Test
        @DisplayName("converts integer to string")
        void convertsIntegerToString() {
            Map<String, Object> source = Map.of("expandDepth", 5);

            String result = ConfigMergeHelper.getString(source, ConfigProperty.EXPAND_DEPTH, "1");

            assertEquals("5", result);
        }

        @Test
        @DisplayName("returns fallback when not present")
        void returnsFallbackWhenNotPresent() {
            Map<String, Object> source = Map.of();

            String result = ConfigMergeHelper.getString(source, ConfigProperty.ID_KEY, "_id");

            assertEquals("_id", result);
        }

        @Test
        @DisplayName("returns fallback when source is null")
        void returnsFallbackWhenSourceIsNull() {
            String result = ConfigMergeHelper.getString(null, ConfigProperty.ID_KEY, "_id");

            assertEquals("_id", result);
        }

        @Test
        @DisplayName("uses prefixed key when short key not present")
        void usesPrefixedKeyWhenShortKeyNotPresent() {
            Map<String, Object> source = Map.of("codec.idKey", "prefixedId");

            String result = ConfigMergeHelper.getString(source, ConfigProperty.ID_KEY, "_id");

            assertEquals("prefixedId", result);
        }
    }

    @Nested
    @DisplayName("getBoolean()")
    class GetBoolean {

        @Test
        @DisplayName("returns Boolean value directly")
        void returnsBooleanValueDirectly() {
            Map<String, Object> source = Map.of("idOnTop", true);

            boolean result = ConfigMergeHelper.getBoolean(source, ConfigProperty.ID_ON_TOP, false);

            assertTrue(result);
        }

        @Test
        @DisplayName("parses 'true' string")
        void parsesTrueString() {
            Map<String, Object> source = Map.of("idOnTop", "true");

            boolean result = ConfigMergeHelper.getBoolean(source, ConfigProperty.ID_ON_TOP, false);

            assertTrue(result);
        }

        @Test
        @DisplayName("parses 'false' string")
        void parsesFalseString() {
            Map<String, Object> source = Map.of("idOnTop", "false");

            boolean result = ConfigMergeHelper.getBoolean(source, ConfigProperty.ID_ON_TOP, true);

            assertFalse(result);
        }

        @Test
        @DisplayName("parses case-insensitive boolean strings")
        void parsesCaseInsensitiveBooleanStrings() {
            Map<String, Object> source1 = Map.of("idOnTop", "TRUE");
            Map<String, Object> source2 = Map.of("idOnTop", "False");

            boolean result1 = ConfigMergeHelper.getBoolean(source1, ConfigProperty.ID_ON_TOP, false);
            boolean result2 = ConfigMergeHelper.getBoolean(source2, ConfigProperty.ID_ON_TOP, true);

            assertTrue(result1);
            assertFalse(result2);
        }

        @Test
        @DisplayName("returns fallback when not present")
        void returnsFallbackWhenNotPresent() {
            Map<String, Object> source = Map.of();

            boolean result = ConfigMergeHelper.getBoolean(source, ConfigProperty.ID_ON_TOP, true);

            assertTrue(result);
        }

        @Test
        @DisplayName("returns fallback when source is null")
        void returnsFallbackWhenSourceIsNull() {
            boolean result = ConfigMergeHelper.getBoolean(null, ConfigProperty.ID_ON_TOP, true);

            assertTrue(result);
        }

        @Test
        @DisplayName("returns parsed false for invalid string (parseBoolean behavior)")
        void returnsParsedFalseForInvalidString() {
            Map<String, Object> source = Map.of("idOnTop", "not_a_boolean");

            // Boolean.parseBoolean returns false for any string that is not "true"
            boolean result = ConfigMergeHelper.getBoolean(source, ConfigProperty.ID_ON_TOP, true);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("getInteger()")
    class GetInteger {

        @Test
        @DisplayName("returns Integer value directly")
        void returnsIntegerValueDirectly() {
            Map<String, Object> source = Map.of("expandDepth", 5);

            int result = ConfigMergeHelper.getInteger(source, ConfigProperty.EXPAND_DEPTH, 1);

            assertEquals(5, result);
        }

        @Test
        @DisplayName("returns Number value converted to int")
        void returnsNumberValueConvertedToInt() {
            Map<String, Object> source = Map.of("expandDepth", 42L);

            int result = ConfigMergeHelper.getInteger(source, ConfigProperty.EXPAND_DEPTH, 1);

            assertEquals(42, result);
        }

        @Test
        @DisplayName("parses string to integer")
        void parsesStringToInteger() {
            Map<String, Object> source = Map.of("expandDepth", "10");

            int result = ConfigMergeHelper.getInteger(source, ConfigProperty.EXPAND_DEPTH, 1);

            assertEquals(10, result);
        }

        @Test
        @DisplayName("returns fallback on parse error")
        void returnsFallbackOnParseError() {
            Map<String, Object> source = Map.of("expandDepth", "not_a_number");

            int result = ConfigMergeHelper.getInteger(source, ConfigProperty.EXPAND_DEPTH, 1);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("returns fallback when not present")
        void returnsFallbackWhenNotPresent() {
            Map<String, Object> source = Map.of();

            int result = ConfigMergeHelper.getInteger(source, ConfigProperty.EXPAND_DEPTH, 1);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("returns fallback when source is null")
        void returnsFallbackWhenSourceIsNull() {
            int result = ConfigMergeHelper.getInteger(null, ConfigProperty.EXPAND_DEPTH, 1);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("handles negative integers")
        void handlesNegativeIntegers() {
            Map<String, Object> source = Map.of("expandDepth", -5);

            int result = ConfigMergeHelper.getInteger(source, ConfigProperty.EXPAND_DEPTH, 1);

            assertEquals(-5, result);
        }

        @Test
        @DisplayName("handles negative integer strings")
        void handlesNegativeIntegerStrings() {
            Map<String, Object> source = Map.of("expandDepth", "-10");

            int result = ConfigMergeHelper.getInteger(source, ConfigProperty.EXPAND_DEPTH, 1);

            assertEquals(-10, result);
        }
    }

    @Nested
    @DisplayName("getEnum()")
    class GetEnum {

        @Test
        @DisplayName("returns enum instance directly")
        void returnsEnumInstanceDirectly() {
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.ID_KEY.getKey(), ColorMode.RGB);

            ColorMode result = ConfigMergeHelper.getEnum(source, ConfigProperty.ID_KEY, ColorMode.class,
                    ColorMode.GRAYSCALE);

            assertEquals(ColorMode.RGB, result);
        }

        @Test
        @DisplayName("parses string to enum by name")
        void parsesStringToEnumByName() {
            Map<String, Object> source = Map.of("idKey", "RGB");

            ColorMode result = ConfigMergeHelper.getEnum(source, ConfigProperty.ID_KEY, ColorMode.class,
                    ColorMode.CMYK);

            assertEquals(ColorMode.RGB, result);
        }

        @Test
        @DisplayName("returns fallback on invalid enum name")
        void returnsFallbackOnInvalidEnumName() {
            Map<String, Object> source = Map.of("idKey", "INVALID_MODE");

            ColorMode result = ConfigMergeHelper.getEnum(source, ConfigProperty.ID_KEY, ColorMode.class,
                    ColorMode.CMYK);

            assertEquals(ColorMode.CMYK, result);
        }

        @Test
        @DisplayName("returns fallback when not present")
        void returnsFallbackWhenNotPresent() {
            Map<String, Object> source = Map.of();

            ColorMode result = ConfigMergeHelper.getEnum(source, ConfigProperty.ID_KEY, ColorMode.class,
                    ColorMode.GRAYSCALE);

            assertEquals(ColorMode.GRAYSCALE, result);
        }

        @Test
        @DisplayName("returns fallback when source is null")
        void returnsFallbackWhenSourceIsNull() {
            ColorMode result = ConfigMergeHelper.getEnum(null, ConfigProperty.ID_KEY, ColorMode.class,
                    ColorMode.RGB);

            assertEquals(ColorMode.RGB, result);
        }

        @Test
        @DisplayName("is case-insensitive for enum name parsing (per spec)")
        void isCaseInsensitiveForEnumNameParsing() {
            Map<String, Object> source = Map.of("idKey", "cmyk");

            ColorMode result = ConfigMergeHelper.getEnum(source, ConfigProperty.ID_KEY, ColorMode.class,
                    ColorMode.GRAYSCALE);

            // Should return CMYK because case-insensitive matching is supported per spec
            assertEquals(ColorMode.CMYK, result);
        }

        @Test
        @DisplayName("parses different enum types")
        void parsesDifferentEnumTypes() {
            Map<String, Object> source = Map.of("idKey", "CMYK");

            ColorMode result = ConfigMergeHelper.getEnum(source, ConfigProperty.ID_KEY, ColorMode.class,
                    ColorMode.RGB);

            assertEquals(ColorMode.CMYK, result);
        }
    }

    @Nested
    @DisplayName("getList()")
    class GetList {

        @Test
        @DisplayName("returns List value directly")
        void returnsListValueDirectly() {
            List<String> expectedList = List.of("a", "b", "c");
            Map<String, Object> source = new HashMap<>();
            source.put("idFeatures", expectedList);

            List<String> result = ConfigMergeHelper.getList(source, ConfigProperty.ID_FEATURES, List.of());

            assertEquals(expectedList, result);
        }

        @Test
        @DisplayName("returns fallback when not a List")
        void returnsFallbackWhenNotAList() {
            Map<String, Object> source = Map.of("idFeatures", "not_a_list");

            List<String> result = ConfigMergeHelper.getList(source, ConfigProperty.ID_FEATURES, List.of("default"));

            assertEquals(List.of("default"), result);
        }

        @Test
        @DisplayName("returns fallback when not present")
        void returnsFallbackWhenNotPresent() {
            Map<String, Object> source = Map.of();

            List<String> result = ConfigMergeHelper.getList(source, ConfigProperty.ID_FEATURES, List.of("default"));

            assertEquals(List.of("default"), result);
        }

        @Test
        @DisplayName("returns fallback when source is null")
        void returnsFallbackWhenSourceIsNull() {
            List<String> result = ConfigMergeHelper.getList(null, ConfigProperty.ID_FEATURES, List.of("default"));

            assertEquals(List.of("default"), result);
        }

        @Test
        @DisplayName("handles empty list")
        void handlesEmptyList() {
            Map<String, Object> source = Map.of("idFeatures", List.of());

            List<String> result = ConfigMergeHelper.getList(source, ConfigProperty.ID_FEATURES, List.of("default"));

            assertEquals(List.of(), result);
        }

        @Test
        @DisplayName("returns list with multiple elements")
        void returnsListWithMultipleElements() {
            List<String> expectedList = List.of("name", "type", "id");
            Map<String, Object> source = new HashMap<>();
            source.put("idFeatures", expectedList);

            List<String> result = ConfigMergeHelper.getList(source, ConfigProperty.ID_FEATURES, List.of());

            assertEquals(3, result.size());
            assertTrue(result.containsAll(expectedList));
        }

        @Test
        @DisplayName("uses prefixed key when short key not present")
        void usesPrefixedKeyWhenShortKeyNotPresent() {
            List<String> expectedList = List.of("x", "y");
            Map<String, Object> source = new HashMap<>();
            source.put("codec.idFeatures", expectedList);

            List<String> result = ConfigMergeHelper.getList(source, ConfigProperty.ID_FEATURES, List.of());

            assertEquals(expectedList, result);
        }
    }

    @Nested
    @DisplayName("getMap()")
    class GetMap {

        @Test
        @DisplayName("returns Map value directly")
        void returnsMapValueDirectly() {
            Map<String, String> expectedMap = Map.of("key1", "value1", "key2", "value2");
            Map<String, Object> source = new HashMap<>();
            source.put("typeMappings", expectedMap);

            Map<String, String> result = ConfigMergeHelper.getMap(source, ConfigProperty.TYPE_MAPPINGS, Map.of());

            assertEquals(expectedMap, result);
        }

        @Test
        @DisplayName("returns fallback when not a Map")
        void returnsFallbackWhenNotAMap() {
            Map<String, Object> source = Map.of("typeMappings", "not_a_map");

            Map<String, String> result = ConfigMergeHelper.getMap(source, ConfigProperty.TYPE_MAPPINGS,
                    Map.of("default", "value"));

            assertEquals(Map.of("default", "value"), result);
        }

        @Test
        @DisplayName("returns fallback when not present")
        void returnsFallbackWhenNotPresent() {
            Map<String, Object> source = Map.of();

            Map<String, String> result = ConfigMergeHelper.getMap(source, ConfigProperty.TYPE_MAPPINGS,
                    Map.of("default", "value"));

            assertEquals(Map.of("default", "value"), result);
        }

        @Test
        @DisplayName("returns fallback when source is null")
        void returnsFallbackWhenSourceIsNull() {
            Map<String, String> result = ConfigMergeHelper.getMap(null, ConfigProperty.TYPE_MAPPINGS,
                    Map.of("default", "value"));

            assertEquals(Map.of("default", "value"), result);
        }

        @Test
        @DisplayName("handles empty map")
        void handlesEmptyMap() {
            Map<String, Object> source = Map.of("typeMappings", Map.of());

            Map<String, String> result = ConfigMergeHelper.getMap(source, ConfigProperty.TYPE_MAPPINGS,
                    Map.of("default", "value"));

            assertEquals(Map.of(), result);
        }

        @Test
        @DisplayName("returns map with multiple entries")
        void returnsMapWithMultipleEntries() {
            Map<String, String> expectedMap = Map.of("a", "1", "b", "2", "c", "3");
            Map<String, Object> source = new HashMap<>();
            source.put("typeMappings", expectedMap);

            Map<String, String> result = ConfigMergeHelper.getMap(source, ConfigProperty.TYPE_MAPPINGS, Map.of());

            assertEquals(3, result.size());
            assertEquals("1", result.get("a"));
            assertEquals("2", result.get("b"));
            assertEquals("3", result.get("c"));
        }

        @Test
        @DisplayName("uses prefixed key when short key not present")
        void usesPrefixedKeyWhenShortKeyNotPresent() {
            Map<String, String> expectedMap = Map.of("x", "10", "y", "20");
            Map<String, Object> source = new HashMap<>();
            source.put("codec.typeMappings", expectedMap);

            Map<String, String> result = ConfigMergeHelper.getMap(source, ConfigProperty.TYPE_MAPPINGS, Map.of());

            assertEquals(expectedMap, result);
        }
    }

    @Nested
    @DisplayName("integration scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("cascading configuration lookups")
        void cascadingConfigurationLookups() {
            Map<String, Object> options = new HashMap<>();
            options.put("idKey", "customId");
            options.put("codec.idStrategy", "COMBINED");
            options.put("codec.idOnTop", true);

            String idKey = ConfigMergeHelper.getString(options, ConfigProperty.ID_KEY, "_id");
            String strategy = ConfigMergeHelper.getString(options, ConfigProperty.ID_STRATEGY, "ID_FIELD");
            boolean onTop = ConfigMergeHelper.getBoolean(options, ConfigProperty.ID_ON_TOP, false);

            assertEquals("customId", idKey);
            assertEquals("COMBINED", strategy);
            assertTrue(onTop);
        }

        @Test
        @DisplayName("mixed key formats in same source")
        void mixedKeyFormatsInSameSource() {
            Map<String, Object> source = new HashMap<>();
            source.put("idKey", "id1");
            source.put("codec.idStrategy", "COMBINED");
            source.put("idOnTop", true);

            String id = ConfigMergeHelper.getValue(source, ConfigProperty.ID_KEY);
            String strategy = ConfigMergeHelper.getValue(source, ConfigProperty.ID_STRATEGY);
            Boolean onTop = ConfigMergeHelper.getValue(source, ConfigProperty.ID_ON_TOP);

            assertEquals("id1", id);
            assertEquals("COMBINED", strategy);
            assertTrue(onTop);
        }

        @Test
        @DisplayName("fallback chain for complex types")
        void fallbackChainForComplexTypes() {
            Map<String, Object> source = Map.of("otherKey", "otherValue");

            List<String> list = ConfigMergeHelper.getList(source, ConfigProperty.ID_FEATURES, List.of("a", "b"));
            Map<String, String> map = ConfigMergeHelper.getMap(source, ConfigProperty.TYPE_MAPPINGS,
                    Map.of("key", "value"));

            assertEquals(List.of("a", "b"), list);
            assertEquals(Map.of("key", "value"), map);
        }

        @Test
        @DisplayName("multiple property checks on same source")
        void multiplePropertyChecksOnSameSource() {
            Map<String, Object> source = new HashMap<>();
            source.put("typeKey", "_type");
            source.put("codec.typeInclude", true);
            source.put("typeFormat", "PLAIN");

            boolean hasTypeKey = ConfigMergeHelper.hasValue(source, ConfigProperty.TYPE_KEY);
            boolean hasTypeInclude = ConfigMergeHelper.hasValue(source, ConfigProperty.TYPE_INCLUDE);
            boolean hasTypeFormat = ConfigMergeHelper.hasValue(source, ConfigProperty.TYPE_FORMAT);
            boolean hasTypeStrategy = ConfigMergeHelper.hasValue(source, ConfigProperty.TYPE_STRATEGY);

            assertTrue(hasTypeKey);
            assertTrue(hasTypeInclude);
            assertTrue(hasTypeFormat);
            assertFalse(hasTypeStrategy);
        }
    }
}
