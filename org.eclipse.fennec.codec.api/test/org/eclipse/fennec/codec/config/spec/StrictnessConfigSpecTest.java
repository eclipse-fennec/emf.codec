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
package org.eclipse.fennec.codec.config.spec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.eclipse.fennec.codec.config.ConfigDirection;
import org.eclipse.fennec.codec.config.ConfigLevel;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based tests for strictness configuration properties.
 * <p>
 * These tests verify that {@code strictOnUnknown} and {@code strictOnMissing} are
 * properly defined in the configuration system with correct defaults, levels, and
 * directions as specified in the codec-v2 spec.
 * <p>
 * Spec references:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/11-feature.md} §11 - Feature Strictness</li>
 *   <li>{@code docs/codec-v2-spec/16-annotation-reference.md} - strictOnUnknown, strictOnMissing</li>
 *   <li>{@code docs/codec-v2-spec/02-config-resolution.md} - Scope: G, C</li>
 * </ul>
 * <p>
 * Note: Strictness is a deserialization runtime concern. These tests verify the
 * configuration metadata. Runtime wiring tests will be added when the deserialization
 * pipeline integrates strictness checking.
 */
@DisplayName("Strictness Config Spec Tests (spec 11-feature.md §11)")
class StrictnessConfigSpecTest {

    // ========================================================================
    // Section 1: STRICT_ON_UNKNOWN ConfigProperty
    // Spec: 11-feature.md §11 / 16-annotation-reference.md
    // ========================================================================

    @Nested
    @DisplayName("1. strictOnUnknown Property (spec §11)")
    class StrictOnUnknownProperty {

        /**
         * Spec §11: "strictOnUnknown | codec.strictOnUnknown | false"
         */
        @Test
        @DisplayName("1.1 short key is 'strictOnUnknown'")
        void shortKey() {
            assertEquals("strictOnUnknown", ConfigProperty.STRICT_ON_UNKNOWN.getKey());
        }

        /**
         * Spec §11: Default is false (LENIENT mode)
         */
        @Test
        @DisplayName("1.2 default value is false (LENIENT)")
        void defaultValue_isFalse() {
            Boolean defaultValue = ConfigProperty.STRICT_ON_UNKNOWN.getDefaultValue();
            assertFalse(defaultValue,
                "Spec §11: strictOnUnknown defaults to false (LENIENT mode)");
        }

        /**
         * Spec §11: Type is Boolean
         */
        @Test
        @DisplayName("1.3 type is Boolean")
        void type_isBoolean() {
            assertEquals(Boolean.class, ConfigProperty.STRICT_ON_UNKNOWN.getType());
        }

        /**
         * Spec §11 + 02-config-resolution.md: Valid at GLOBAL and ECLASS levels only.
         * NOT valid at FEATURE level.
         */
        @Test
        @DisplayName("1.4 valid at GLOBAL and ECLASS levels only")
        void validLevels_globalAndEClass() {
            Set<ConfigLevel> levels = ConfigProperty.STRICT_ON_UNKNOWN.getValidLevels();

            assertTrue(levels.contains(ConfigLevel.GLOBAL),
                "Spec: strictOnUnknown valid at GLOBAL level");
            assertTrue(levels.contains(ConfigLevel.ECLASS),
                "Spec: strictOnUnknown valid at ECLASS level");
            assertFalse(levels.contains(ConfigLevel.FEATURE),
                "Spec: strictOnUnknown NOT valid at FEATURE level");
            assertEquals(2, levels.size(),
                "Only 2 levels: GLOBAL, ECLASS");
        }

        /**
         * Spec §11: Direction is READ only (deserialization concern).
         */
        @Test
        @DisplayName("1.5 direction is READ only (deserialization)")
        void direction_readOnly() {
            Set<ConfigDirection> directions = ConfigProperty.STRICT_ON_UNKNOWN.getDirections();

            assertTrue(directions.contains(ConfigDirection.READ),
                "Spec: strictOnUnknown applies to READ (deserialization)");
            assertFalse(directions.contains(ConfigDirection.WRITE),
                "Spec: strictOnUnknown does NOT apply to WRITE (serialization)");
        }
    }

    // ========================================================================
    // Section 2: STRICT_ON_MISSING ConfigProperty
    // Spec: 11-feature.md §11 / 16-annotation-reference.md
    // ========================================================================

    @Nested
    @DisplayName("2. strictOnMissing Property (spec §11)")
    class StrictOnMissingProperty {

        /**
         * Spec §11: "strictOnMissing | codec.strictOnMissing | false"
         */
        @Test
        @DisplayName("2.1 short key is 'strictOnMissing'")
        void shortKey() {
            assertEquals("strictOnMissing", ConfigProperty.STRICT_ON_MISSING.getKey());
        }

        /**
         * Spec §11: Default is false (LENIENT mode)
         */
        @Test
        @DisplayName("2.2 default value is false (LENIENT)")
        void defaultValue_isFalse() {
            Boolean defaultValue = ConfigProperty.STRICT_ON_MISSING.getDefaultValue();
            assertFalse(defaultValue,
                "Spec §11: strictOnMissing defaults to false (LENIENT mode)");
        }

        /**
         * Spec §11: Type is Boolean
         */
        @Test
        @DisplayName("2.3 type is Boolean")
        void type_isBoolean() {
            assertEquals(Boolean.class, ConfigProperty.STRICT_ON_MISSING.getType());
        }

        /**
         * Spec §11 + 02-config-resolution.md: Valid at GLOBAL and ECLASS levels only.
         * NOT valid at FEATURE level.
         */
        @Test
        @DisplayName("2.4 valid at GLOBAL and ECLASS levels only")
        void validLevels_globalAndEClass() {
            Set<ConfigLevel> levels = ConfigProperty.STRICT_ON_MISSING.getValidLevels();

            assertTrue(levels.contains(ConfigLevel.GLOBAL),
                "Spec: strictOnMissing valid at GLOBAL level");
            assertTrue(levels.contains(ConfigLevel.ECLASS),
                "Spec: strictOnMissing valid at ECLASS level");
            assertFalse(levels.contains(ConfigLevel.FEATURE),
                "Spec: strictOnMissing NOT valid at FEATURE level");
            assertEquals(2, levels.size(),
                "Only 2 levels: GLOBAL, ECLASS");
        }

        /**
         * Spec §11: Direction is READ only (deserialization concern).
         */
        @Test
        @DisplayName("2.5 direction is READ only (deserialization)")
        void direction_readOnly() {
            Set<ConfigDirection> directions = ConfigProperty.STRICT_ON_MISSING.getDirections();

            assertTrue(directions.contains(ConfigDirection.READ),
                "Spec: strictOnMissing applies to READ (deserialization)");
            assertFalse(directions.contains(ConfigDirection.WRITE),
                "Spec: strictOnMissing does NOT apply to WRITE (serialization)");
        }
    }

    // ========================================================================
    // Section 3: Strictness Behavior Semantics (spec documentation)
    // Spec: 11-feature.md §11 behavior table
    // ========================================================================

    @Nested
    @DisplayName("3. Strictness Behavior Semantics (spec §11 behavior table)")
    class StrictnessBehaviorSemantics {

        /**
         * Spec §11: LENIENT mode is the default for both properties.
         */
        @Test
        @DisplayName("3.1 both properties default to LENIENT (false)")
        void bothDefaults_areLenient() {
            assertFalse(ConfigProperty.STRICT_ON_UNKNOWN.<Boolean>getDefaultValue(),
                "LENIENT: unknown fields → warning + skip");
            assertFalse(ConfigProperty.STRICT_ON_MISSING.<Boolean>getDefaultValue(),
                "LENIENT: missing required → warning + use default");
        }

        /**
         * Spec §11: strictOnUnknown and strictOnMissing are independent.
         * You can enable one without the other.
         */
        @Test
        @DisplayName("3.2 strictOnUnknown and strictOnMissing are independent properties")
        void strictnessProperties_areIndependent() {
            // They are different enum values
            assertFalse(ConfigProperty.STRICT_ON_UNKNOWN == ConfigProperty.STRICT_ON_MISSING);

            // They have the same levels and directions (both G, C + READ)
            assertEquals(
                ConfigProperty.STRICT_ON_UNKNOWN.getValidLevels(),
                ConfigProperty.STRICT_ON_MISSING.getValidLevels(),
                "Both have same valid levels");
            assertEquals(
                ConfigProperty.STRICT_ON_UNKNOWN.getDirections(),
                ConfigProperty.STRICT_ON_MISSING.getDirections(),
                "Both have same valid directions");
        }

        /**
         * Spec §11: Strictness shares scope with deserializationMode (both G, C + READ).
         */
        @Test
        @DisplayName("3.3 strictness properties share scope with deserializationMode")
        void strictnessSharesScope_withDeserializationMode() {
            assertEquals(
                ConfigProperty.STRICT_ON_UNKNOWN.getValidLevels(),
                ConfigProperty.DESERIALIZATION_MODE.getValidLevels(),
                "Same levels as deserializationMode");
            assertEquals(
                ConfigProperty.STRICT_ON_UNKNOWN.getDirections(),
                ConfigProperty.DESERIALIZATION_MODE.getDirections(),
                "Same directions as deserializationMode");
        }
    }

    // ========================================================================
    // Section 4: Property Key Naming Convention
    // Spec: 03-naming-conventions.md / 16-annotation-reference.md
    // ========================================================================

    @Nested
    @DisplayName("4. Property Key Naming Convention")
    class PropertyKeyNaming {

        /**
         * Spec: Property key follows camelCase convention without prefix.
         */
        @Test
        @DisplayName("4.1 strictOnUnknown uses camelCase short key")
        void strictOnUnknown_camelCase() {
            assertEquals("strictOnUnknown", ConfigProperty.STRICT_ON_UNKNOWN.getKey());
        }

        @Test
        @DisplayName("4.2 strictOnMissing uses camelCase short key")
        void strictOnMissing_camelCase() {
            assertEquals("strictOnMissing", ConfigProperty.STRICT_ON_MISSING.getKey());
        }

        /**
         * Spec: Full property key is codec.strictOnUnknown
         */
        @Test
        @DisplayName("4.3 full property key is codec.strictOnUnknown")
        void prefixedKey_strictOnUnknown() {
            assertEquals("codec.strictOnUnknown", ConfigProperty.STRICT_ON_UNKNOWN.getPropertyKey());
        }

        @Test
        @DisplayName("4.4 full property key is codec.strictOnMissing")
        void prefixedKey_strictOnMissing() {
            assertEquals("codec.strictOnMissing", ConfigProperty.STRICT_ON_MISSING.getPropertyKey());
        }
    }
}
