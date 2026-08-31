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

import java.util.List;
import java.util.Map;

/**
 * Helper class for merging configuration property maps.
 * <p>
 * This utility supports the cascading merge pattern where each layer
 * (Module → Factory → Resource → Options) can provide property values
 * that override the previous layer's configuration.
 * <p>
 * Property lookup supports both key formats:
 * <ul>
 *   <li>Short key: "idKey"</li>
 *   <li>Prefixed key: "codec.idKey"</li>
 * </ul>
 *
 * @see ConfigProperty
 */
public final class ConfigMergeHelper {

    private ConfigMergeHelper() {
        // Utility class
    }

    /**
     * Gets a value from the source map for the given property.
     * <p>
     * Looks up both the short key ("idKey") and prefixed key ("codec.idKey").
     * Returns null if the property is not present in the map.
     *
     * @param source the source property map
     * @param property the property to look up
     * @param <T> the expected value type
     * @return the value, or null if not present
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(Map<String, Object> source, ConfigProperty property) {
        if (source == null || property == null) {
            return null;
        }

        // Try short key first ("idKey")
        Object value = source.get(property.getKey());
        if (value != null) {
            return (T) value;
        }

        // Try prefixed key ("codec.idKey")
        value = source.get(property.getPropertyKey());
        return (T) value;
    }

    /**
     * Gets a value from the source map, returning the fallback if not present.
     *
     * @param source the source property map
     * @param property the property to look up
     * @param fallback the fallback value if property is not in source
     * @param <T> the expected value type
     * @return the value from source, or fallback if not present
     */
    public static <T> T getValueOrElse(Map<String, Object> source, ConfigProperty property, T fallback) {
        T value = getValue(source, property);
        return value != null ? value : fallback;
    }

    /**
     * Checks if a property is present in the source map.
     *
     * @param source the source property map
     * @param property the property to check
     * @return true if the property is present (with either key format)
     */
    public static boolean hasValue(Map<String, Object> source, ConfigProperty property) {
        if (source == null || property == null) {
            return false;
        }
        return source.containsKey(property.getKey()) || source.containsKey(property.getPropertyKey());
    }

    /**
     * Gets a String value from the source map.
     *
     * @param source the source property map
     * @param property the property to look up
     * @param fallback the fallback value
     * @return the String value, or fallback if not present
     */
    public static String getString(Map<String, Object> source, ConfigProperty property, String fallback) {
        Object value = getValue(source, property);
        if (value == null) {
            return fallback;
        }
        return value.toString();
    }

    /**
     * Gets a Boolean value from the source map.
     *
     * @param source the source property map
     * @param property the property to look up
     * @param fallback the fallback value
     * @return the Boolean value, or fallback if not present or unparseable
     */
    public static boolean getBoolean(Map<String, Object> source, ConfigProperty property, boolean fallback) {
        Object value = getValue(source, property);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        // Boolean.parseBoolean answers "false" to everything that is not "true", so it turned
        // typeInclude="yes" into an explicit opt-out - not a fallback, an inversion of the
        // caller's intent (issue #174). Only the two words the property accepts are parsed;
        // anything else keeps the fallback and is reported by ConfigValueValidator.
        String text = value.toString();
        if ("true".equalsIgnoreCase(text)) {
            return true;
        }
        if ("false".equalsIgnoreCase(text)) {
            return false;
        }
        return fallback;
    }

    /**
     * Gets an Integer value from the source map.
     *
     * @param source the source property map
     * @param property the property to look up
     * @param fallback the fallback value
     * @return the Integer value, or fallback if not present
     */
    public static int getInteger(Map<String, Object> source, ConfigProperty property, int fallback) {
        Object value = getValue(source, property);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * Gets an enum value from the source map.
     * <p>
     * Supports both enum instances and String values (matched by name, case-insensitive).
     *
     * @param source the source property map
     * @param property the property to look up
     * @param enumType the enum class
     * @param fallback the fallback value
     * @param <E> the enum type
     * @return the enum value, or fallback if not present or invalid
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E getEnum(Map<String, Object> source, ConfigProperty property,
                                                 Class<E> enumType, E fallback) {
        Object value = getValue(source, property);
        if (value == null) {
            return fallback;
        }
        if (enumType.isInstance(value)) {
            return (E) value;
        }
        String stringValue = value.toString();
        // Try exact match first
        try {
            return Enum.valueOf(enumType, stringValue);
        } catch (IllegalArgumentException e) {
            // Try case-insensitive match
            for (E constant : enumType.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(stringValue)) {
                    return constant;
                }
            }
            return fallback;
        }
    }

    /**
     * Gets a List value from the source map.
     * <p>
     * The value must already be a List; no conversion is performed.
     *
     * @param source the source property map
     * @param property the property to look up
     * @param fallback the fallback value
     * @param <T> the list element type
     * @return the List value, or fallback if not present or not a List
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getList(Map<String, Object> source, ConfigProperty property, List<T> fallback) {
        Object value = getValue(source, property);
        if (value == null) {
            return fallback;
        }
        if (value instanceof List<?> list) {
            return (List<T>) list;
        }
        return fallback;
    }

    /**
     * Gets a Map value from the source map.
     * <p>
     * The value must already be a Map; no conversion is performed.
     *
     * @param source the source property map
     * @param property the property to look up
     * @param fallback the fallback value
     * @param <K> the map key type
     * @param <V> the map value type
     * @return the Map value, or fallback if not present or not a Map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> getMap(Map<String, Object> source, ConfigProperty property, Map<K, V> fallback) {
        Object value = getValue(source, property);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Map<?, ?> map) {
            return (Map<K, V>) map;
        }
        return fallback;
    }
}
