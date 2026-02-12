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
package org.eclipse.fennec.codec.metadata.util;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utility class for parsing EAnnotation detail values.
 * <p>
 * Provides type-safe extraction of values from annotation detail maps
 * with support for defaults and null handling.
 * </p>
 * <p>
 * All methods are stateless and pure - they depend only on their inputs
 * and have no side effects beyond the returned value or consumer calls.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2026-01-24
 */
public final class AnnotationParseHelper {

    private AnnotationParseHelper() {
        // Utility class - prevent instantiation
    }

    // ========================================================================
    // Boolean Parsing
    // ========================================================================

    /**
     * Parses a boolean value from annotation details.
     *
     * @param details the annotation details map
     * @param key the key to look up
     * @param defaultValue the value to return if key is missing or blank
     * @return the parsed boolean or default value
     */
    public static boolean parseBoolean(Map<String, String> details, String key, boolean defaultValue) {
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(key, "key must not be null");

        String value = details.get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Parses a boolean and calls the consumer if the key is present.
     *
     * @param details the annotation details map
     * @param key the key to look up
     * @param consumer the consumer to call with the parsed value
     */
    public static void ifBooleanPresent(Map<String, String> details, String key, Consumer<Boolean> consumer) {
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(consumer, "consumer must not be null");

        String value = details.get(key);
        if (value != null) {
            consumer.accept(Boolean.parseBoolean(value));
        }
    }

    // ========================================================================
    // String Parsing
    // ========================================================================

    /**
     * Gets a string value from annotation details, returning default if missing or blank.
     *
     * @param details the annotation details map
     * @param key the key to look up
     * @param defaultValue the value to return if key is missing or blank
     * @return the string value or default
     */
    public static String getString(Map<String, String> details, String key, String defaultValue) {
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(key, "key must not be null");

        String value = details.get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Calls the consumer if a non-blank string value is present for the key.
     *
     * @param details the annotation details map
     * @param key the key to look up
     * @param consumer the consumer to call with the value
     */
    public static void ifStringPresent(Map<String, String> details, String key, Consumer<String> consumer) {
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(consumer, "consumer must not be null");

        String value = details.get(key);
        if (value != null && !value.isBlank()) {
            consumer.accept(value);
        }
    }

    // ========================================================================
    // Enum Parsing
    // ========================================================================

    /**
     * Parses an enum value from annotation details.
     * <p>
     * The string value is converted to uppercase using {@link Locale#ROOT} before
     * matching against enum constants. This ensures consistent behavior across locales.
     * </p>
     *
     * @param <E> the enum type
     * @param details the annotation details map
     * @param key the key to look up
     * @param enumType the enum class
     * @param defaultValue the value to return if key is missing, blank, or invalid
     * @return the parsed enum or default value
     */
    public static <E extends Enum<E>> E parseEnum(
            Map<String, String> details,
            String key,
            Class<E> enumType,
            E defaultValue) {
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(enumType, "enumType must not be null");

        String value = details.get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumType, value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            // Unknown enum value - return default
            // Consider logging a warning here in the future
            return defaultValue;
        }
    }

    /**
     * Parses an enum and calls the consumer if the key is present and valid.
     *
     * @param <E> the enum type
     * @param details the annotation details map
     * @param key the key to look up
     * @param enumType the enum class
     * @param consumer the consumer to call with the parsed value
     */
    public static <E extends Enum<E>> void ifEnumPresent(
            Map<String, String> details,
            String key,
            Class<E> enumType,
            Consumer<E> consumer) {
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(enumType, "enumType must not be null");
        Objects.requireNonNull(consumer, "consumer must not be null");

        String value = details.get(key);
        if (value != null && !value.isBlank()) {
            try {
                E enumValue = Enum.valueOf(enumType, value.toUpperCase(Locale.ROOT));
                consumer.accept(enumValue);
            } catch (IllegalArgumentException e) {
                // Unknown enum value - don't call consumer
            }
        }
    }

    // ========================================================================
    // Key Presence Checks
    // ========================================================================

    /**
     * Checks if any of the specified keys are present in the details map.
     *
     * @param details the annotation details map
     * @param keys the keys to check
     * @return true if any key is present
     */
    public static boolean hasAnyKey(Map<String, String> details, String... keys) {
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(keys, "keys must not be null");

        for (String key : keys) {
            if (details.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all of the specified keys are present in the details map.
     *
     * @param details the annotation details map
     * @param keys the keys to check
     * @return true if all keys are present
     */
    public static boolean hasAllKeys(Map<String, String> details, String... keys) {
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(keys, "keys must not be null");

        for (String key : keys) {
            if (!details.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    // ========================================================================
    // Prefix Extraction
    // ========================================================================

    /**
     * Extracts a suffix from a key that matches the given prefix.
     * <p>
     * For example, with prefix "inlineMapping." and key "inlineMapping.friend",
     * this returns "friend".
     * </p>
     *
     * @param key the key to check
     * @param prefix the prefix to match
     * @return the suffix after the prefix, or null if key doesn't start with prefix
     */
    public static String extractSuffix(String key, String prefix) {
        Objects.requireNonNull(prefix, "prefix must not be null");

        if (key == null || !key.startsWith(prefix)) {
            return null;
        }
        String suffix = key.substring(prefix.length());
        return suffix.isEmpty() ? null : suffix;
    }
}
