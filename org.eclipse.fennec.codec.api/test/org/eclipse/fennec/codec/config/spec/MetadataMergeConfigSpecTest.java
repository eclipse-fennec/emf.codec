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
 * Spec-based tests for metadata merge configuration properties.
 * <p>
 * These tests verify that {@code metadataMerge} and {@code metadataKey} are
 * properly defined in the configuration system with correct defaults, levels, and
 * directions as specified in the codec-v2 spec.
 * <p>
 * Spec references:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/05-global-options.md} §5 - Metadata Merge</li>
 *   <li>{@code docs/codec-v2-spec/16-annotation-reference.md} - metadataMerge, metadataKey</li>
 *   <li>{@code docs/codec-v2-spec/02-config-resolution.md} - Scope: G, C</li>
 * </ul>
 */
@DisplayName("Metadata Merge Config Spec Tests (spec 05-global-options.md §5)")
class MetadataMergeConfigSpecTest {

    // ========================================================================
    // Section 1: METADATA_MERGE ConfigProperty
    // ========================================================================

    @Nested
    @DisplayName("1. metadataMerge Property (spec §5)")
    class MetadataMergeProperty {

        @Test
        @DisplayName("1.1 short key is 'metadataMerge'")
        void shortKey() {
            assertEquals("metadataMerge", ConfigProperty.METADATA_MERGE.getKey());
        }

        @Test
        @DisplayName("1.2 default value is false")
        void defaultValue_isFalse() {
            Boolean defaultValue = ConfigProperty.METADATA_MERGE.getDefaultValue();
            assertFalse(defaultValue,
                "Spec §5: metadataMerge defaults to false");
        }

        @Test
        @DisplayName("1.3 type is Boolean")
        void type_isBoolean() {
            assertEquals(Boolean.class, ConfigProperty.METADATA_MERGE.getType());
        }

        @Test
        @DisplayName("1.4 valid at GLOBAL and ECLASS levels only")
        void validLevels_globalAndEClass() {
            Set<ConfigLevel> levels = ConfigProperty.METADATA_MERGE.getValidLevels();

            assertTrue(levels.contains(ConfigLevel.GLOBAL),
                "Spec: metadataMerge valid at GLOBAL level");
            assertTrue(levels.contains(ConfigLevel.ECLASS),
                "Spec: metadataMerge valid at ECLASS level");
            assertFalse(levels.contains(ConfigLevel.FEATURE),
                "Spec: metadataMerge NOT valid at FEATURE level");
            assertEquals(2, levels.size(),
                "Only 2 levels: GLOBAL, ECLASS");
        }

        @Test
        @DisplayName("1.5 direction is READ and WRITE")
        void direction_readAndWrite() {
            Set<ConfigDirection> directions = ConfigProperty.METADATA_MERGE.getDirections();

            assertTrue(directions.contains(ConfigDirection.READ),
                "Spec: metadataMerge applies to READ (deserialization split)");
            assertTrue(directions.contains(ConfigDirection.WRITE),
                "Spec: metadataMerge applies to WRITE (serialization merge)");
        }

        @Test
        @DisplayName("1.6 full property key is codec.metadataMerge")
        void prefixedKey() {
            assertEquals("codec.metadataMerge", ConfigProperty.METADATA_MERGE.getPropertyKey());
        }
    }

    // ========================================================================
    // Section 2: METADATA_KEY ConfigProperty
    // ========================================================================

    @Nested
    @DisplayName("2. metadataKey Property (spec §5)")
    class MetadataKeyProperty {

        @Test
        @DisplayName("2.1 short key is 'metadataKey'")
        void shortKey() {
            assertEquals("metadataKey", ConfigProperty.METADATA_KEY.getKey());
        }

        @Test
        @DisplayName("2.2 default value is '_metadata'")
        void defaultValue_isMetadata() {
            String defaultValue = ConfigProperty.METADATA_KEY.getDefaultValue();
            assertEquals("_metadata", defaultValue,
                "Spec §5: metadataKey defaults to '_metadata'");
        }

        @Test
        @DisplayName("2.3 type is String")
        void type_isString() {
            assertEquals(String.class, ConfigProperty.METADATA_KEY.getType());
        }

        @Test
        @DisplayName("2.4 valid at GLOBAL and ECLASS levels only")
        void validLevels_globalAndEClass() {
            Set<ConfigLevel> levels = ConfigProperty.METADATA_KEY.getValidLevels();

            assertTrue(levels.contains(ConfigLevel.GLOBAL),
                "Spec: metadataKey valid at GLOBAL level");
            assertTrue(levels.contains(ConfigLevel.ECLASS),
                "Spec: metadataKey valid at ECLASS level");
            assertFalse(levels.contains(ConfigLevel.FEATURE),
                "Spec: metadataKey NOT valid at FEATURE level");
            assertEquals(2, levels.size(),
                "Only 2 levels: GLOBAL, ECLASS");
        }

        @Test
        @DisplayName("2.5 direction is READ and WRITE")
        void direction_readAndWrite() {
            Set<ConfigDirection> directions = ConfigProperty.METADATA_KEY.getDirections();

            assertTrue(directions.contains(ConfigDirection.READ),
                "Spec: metadataKey applies to READ");
            assertTrue(directions.contains(ConfigDirection.WRITE),
                "Spec: metadataKey applies to WRITE");
        }

        @Test
        @DisplayName("2.6 full property key is codec.metadataKey")
        void prefixedKey() {
            assertEquals("codec.metadataKey", ConfigProperty.METADATA_KEY.getPropertyKey());
        }
    }

    // ========================================================================
    // Section 3: Metadata Merge Semantics
    // ========================================================================

    @Nested
    @DisplayName("3. Metadata Merge Semantics (spec §5)")
    class MetadataMergeSemantics {

        @Test
        @DisplayName("3.1 metadataMerge defaults to false (separate objects)")
        void default_isSeparateObjects() {
            assertFalse(ConfigProperty.METADATA_MERGE.<Boolean>getDefaultValue(),
                "Default: type and ID are separate top-level objects");
        }

        @Test
        @DisplayName("3.2 metadataMerge and metadataKey are independent properties")
        void properties_areIndependent() {
            assertFalse(ConfigProperty.METADATA_MERGE == ConfigProperty.METADATA_KEY);
        }

        @Test
        @DisplayName("3.3 both properties share same levels (G, C)")
        void sameValidLevels() {
            assertEquals(
                ConfigProperty.METADATA_MERGE.getValidLevels(),
                ConfigProperty.METADATA_KEY.getValidLevels(),
                "Both have same valid levels");
        }

        @Test
        @DisplayName("3.4 both properties share same directions (R, W)")
        void sameDirections() {
            assertEquals(
                ConfigProperty.METADATA_MERGE.getDirections(),
                ConfigProperty.METADATA_KEY.getDirections(),
                "Both have same valid directions");
        }
    }
}
