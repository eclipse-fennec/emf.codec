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
package org.eclipse.fennec.codec.metadata.provider;

import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.CODEC_SOURCE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.INLINE_MAPPING_KNOWN_KEYS;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.INLINE_MAPPING_SOURCE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_FALLBACK_ECLASS;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_FALLBACK_STRATEGY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_DISCRIMINATOR;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_DISCRIMINATOR_PATH;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.TYPE_MAPPING_KNOWN_KEYS;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.TYPE_MAPPING_SOURCE_PREFIX;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.extractMapIdFromSource;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.isTypeMappingSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for helper methods and constants in {@link CodecAnnotationConstants}.
 */
@DisplayName("CodecAnnotationConstants")
class CodecAnnotationConstantsTest {

    // ========================================================================
    // extractMapIdFromSource
    // ========================================================================

    @Nested
    @DisplayName("extractMapIdFromSource")
    class ExtractMapIdFromSource {

        @Test
        @DisplayName("extracts mapId from valid typeMapping source")
        void extractsMapId() {
            assertEquals("iot-sensors",
                    extractMapIdFromSource("http://eclipse.org/fennec/codec/typeMapping/iot-sensors"));
        }

        @Test
        @DisplayName("extracts mapId with slashes")
        void extractsMapIdWithSlashes() {
            assertEquals("my/custom/map",
                    extractMapIdFromSource("http://eclipse.org/fennec/codec/typeMapping/my/custom/map"));
        }

        @Test
        @DisplayName("returns null for null source")
        void returnsNullForNull() {
            assertNull(extractMapIdFromSource(null));
        }

        @Test
        @DisplayName("returns null for non-typeMapping source")
        void returnsNullForNonTypeMappingSource() {
            assertNull(extractMapIdFromSource(CODEC_SOURCE));
        }

        @Test
        @DisplayName("returns null for empty mapId after prefix")
        void returnsNullForEmptyMapId() {
            assertNull(extractMapIdFromSource(TYPE_MAPPING_SOURCE_PREFIX));
        }

        @Test
        @DisplayName("returns null for inlineMapping source")
        void returnsNullForInlineMappingSource() {
            assertNull(extractMapIdFromSource(INLINE_MAPPING_SOURCE));
        }
    }

    // ========================================================================
    // isTypeMappingSource
    // ========================================================================

    @Nested
    @DisplayName("isTypeMappingSource")
    class IsTypeMappingSource {

        @Test
        @DisplayName("returns true for valid typeMapping source")
        void returnsTrueForTypeMapping() {
            assertTrue(isTypeMappingSource("http://eclipse.org/fennec/codec/typeMapping/iot-sensors"));
        }

        @Test
        @DisplayName("returns true for typeMapping prefix only")
        void returnsTrueForPrefixOnly() {
            assertTrue(isTypeMappingSource(TYPE_MAPPING_SOURCE_PREFIX));
        }

        @Test
        @DisplayName("returns false for codec source")
        void returnsFalseForCodecSource() {
            assertFalse(isTypeMappingSource(CODEC_SOURCE));
        }

        @Test
        @DisplayName("returns false for inlineMapping source")
        void returnsFalseForInlineMappingSource() {
            assertFalse(isTypeMappingSource(INLINE_MAPPING_SOURCE));
        }

        @Test
        @DisplayName("returns false for null")
        void returnsFalseForNull() {
            assertFalse(isTypeMappingSource(null));
        }

        @Test
        @DisplayName("returns false for empty string")
        void returnsFalseForEmpty() {
            assertFalse(isTypeMappingSource(""));
        }
    }

    // ========================================================================
    // Known keys sets
    // ========================================================================

    @Nested
    @DisplayName("known keys sets")
    class KnownKeysSets {

        @Test
        @DisplayName("TYPE_MAPPING_KNOWN_KEYS contains expected keys")
        void typeMappingKnownKeys() {
            assertTrue(TYPE_MAPPING_KNOWN_KEYS.contains(KEY_TYPE_DISCRIMINATOR_PATH));
            assertTrue(TYPE_MAPPING_KNOWN_KEYS.contains(KEY_TYPE_DISCRIMINATOR));
            assertTrue(TYPE_MAPPING_KNOWN_KEYS.contains(KEY_FALLBACK_STRATEGY));
            assertTrue(TYPE_MAPPING_KNOWN_KEYS.contains(KEY_FALLBACK_ECLASS));
            assertEquals(4, TYPE_MAPPING_KNOWN_KEYS.size());
        }

        @Test
        @DisplayName("INLINE_MAPPING_KNOWN_KEYS contains expected keys")
        void inlineMappingKnownKeys() {
            assertTrue(INLINE_MAPPING_KNOWN_KEYS.contains(KEY_FALLBACK_STRATEGY));
            assertTrue(INLINE_MAPPING_KNOWN_KEYS.contains(KEY_FALLBACK_ECLASS));
            assertEquals(2, INLINE_MAPPING_KNOWN_KEYS.size());
        }

        @Test
        @DisplayName("TYPE_MAPPING_KNOWN_KEYS is immutable")
        void typeMappingKeysImmutable() {
            assertThrows(UnsupportedOperationException.class,
                    () -> TYPE_MAPPING_KNOWN_KEYS.add("test"));
        }

        @Test
        @DisplayName("INLINE_MAPPING_KNOWN_KEYS is immutable")
        void inlineMappingKeysImmutable() {
            assertThrows(UnsupportedOperationException.class,
                    () -> INLINE_MAPPING_KNOWN_KEYS.add("test"));
        }
    }
}
