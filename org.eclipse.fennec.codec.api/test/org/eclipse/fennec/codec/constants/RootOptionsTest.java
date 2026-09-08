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
package org.eclipse.fennec.codec.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RootOptions} - issue #208: the root type and root schema options
 * each answer to two keys, the canonical dotted one and the literal alias, and both
 * are read everywhere (Spec 13 §2.1, §2.12).
 */
@DisplayName("RootOptions - dual-key root options (issue #208)")
class RootOptionsTest {

    @Nested
    @DisplayName("rootType")
    class RootType {

        @Test
        @DisplayName("returns null for null options")
        void nullOptions() throws IOException {
            assertNull(RootOptions.rootType(null));
            assertFalse(RootOptions.hasRootType(null));
        }

        @Test
        @DisplayName("returns null when neither key is set")
        void neitherKey() throws IOException {
            assertNull(RootOptions.rootType(Map.of("other", "value")));
            assertFalse(RootOptions.hasRootType(Map.of("other", "value")));
        }

        @Test
        @DisplayName("reads the canonical dotted key codec.rootType")
        void dottedKey() throws IOException {
            Map<String, Object> options = Map.of(CodecOptions.CODEC_ROOT_TYPE, EcorePackage.Literals.ECLASS);

            assertSame(EcorePackage.Literals.ECLASS, RootOptions.rootType(options));
            assertTrue(RootOptions.hasRootType(options));
        }

        @Test
        @DisplayName("reads the literal key CODEC_ROOT_TYPE")
        void literalKey() throws IOException {
            Map<String, Object> options = Map.of(CodecOptions.CODEC_ROOT_TYPE_LITERAL, EcorePackage.Literals.ECLASS);

            assertSame(EcorePackage.Literals.ECLASS, RootOptions.rootType(options));
            assertTrue(RootOptions.hasRootType(options));
        }

        @Test
        @DisplayName("accepts both keys when they carry the same value")
        void bothKeysAgree() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_ROOT_TYPE, EcorePackage.Literals.ECLASS,
                    CodecOptions.CODEC_ROOT_TYPE_LITERAL, EcorePackage.Literals.ECLASS);

            assertSame(EcorePackage.Literals.ECLASS, RootOptions.rootType(options));
        }

        @Test
        @DisplayName("reports contradictory values under the two keys")
        void bothKeysConflict() {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_ROOT_TYPE, EcorePackage.Literals.ECLASS,
                    CodecOptions.CODEC_ROOT_TYPE_LITERAL, EcorePackage.Literals.EPACKAGE);

            IOException failure = assertThrows(IOException.class, () -> RootOptions.rootType(options));

            assertTrue(failure.getMessage().contains(CodecOptions.CODEC_ROOT_TYPE),
                    "message names the dotted key: " + failure.getMessage());
            assertTrue(failure.getMessage().contains(CodecOptions.CODEC_ROOT_TYPE_LITERAL),
                    "message names the literal key: " + failure.getMessage());
            assertTrue(failure.getMessage().contains("EClass") && failure.getMessage().contains("EPackage"),
                    "message names both values: " + failure.getMessage());
        }

        @Test
        @DisplayName("a null value counts as absent, so it is no conflict")
        void nullValueIsAbsent() throws IOException {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_ROOT_TYPE, null);
            options.put(CodecOptions.CODEC_ROOT_TYPE_LITERAL, EcorePackage.Literals.ECLASS);

            assertSame(EcorePackage.Literals.ECLASS, RootOptions.rootType(options));
        }
    }

    @Nested
    @DisplayName("rootSchema")
    class RootSchema {

        @Test
        @DisplayName("returns null for null options")
        void nullOptions() throws IOException {
            assertNull(RootOptions.rootSchema(null));
            assertFalse(RootOptions.hasRootSchema(null));
        }

        @Test
        @DisplayName("reads the canonical dotted key codec.rootSchema")
        void dottedKey() throws IOException {
            Map<String, Object> options = Map.of(CodecOptions.CODEC_ROOT_SCHEMA, EcorePackage.eNS_URI);

            assertEquals(EcorePackage.eNS_URI, RootOptions.rootSchema(options));
            assertTrue(RootOptions.hasRootSchema(options));
        }

        @Test
        @DisplayName("reads the literal key CODEC_ROOT_SCHEMA")
        void literalKey() throws IOException {
            Map<String, Object> options = Map.of(CodecOptions.CODEC_ROOT_SCHEMA_LITERAL, EcorePackage.eINSTANCE);

            assertSame(EcorePackage.eINSTANCE, RootOptions.rootSchema(options));
            assertTrue(RootOptions.hasRootSchema(options));
        }

        @Test
        @DisplayName("accepts both keys when they carry the same value")
        void bothKeysAgree() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_ROOT_SCHEMA, EcorePackage.eNS_URI,
                    CodecOptions.CODEC_ROOT_SCHEMA_LITERAL, EcorePackage.eNS_URI);

            assertEquals(EcorePackage.eNS_URI, RootOptions.rootSchema(options));
        }

        @Test
        @DisplayName("reports contradictory values under the two keys")
        void bothKeysConflict() {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_ROOT_SCHEMA, EcorePackage.eNS_URI,
                    CodecOptions.CODEC_ROOT_SCHEMA_LITERAL, "http://example.org/other/1.0");

            IOException failure = assertThrows(IOException.class, () -> RootOptions.rootSchema(options));

            assertTrue(failure.getMessage().contains(CodecOptions.CODEC_ROOT_SCHEMA),
                    "message names the dotted key: " + failure.getMessage());
            assertTrue(failure.getMessage().contains("http://example.org/other/1.0"),
                    "message names the conflicting value: " + failure.getMessage());
        }
    }

    @Nested
    @DisplayName("key constants")
    class KeyConstants {

        @Test
        @DisplayName("the literal keys carry the historical spelling")
        void literalSpelling() {
            assertEquals("CODEC_ROOT_TYPE", CodecOptions.CODEC_ROOT_TYPE_LITERAL);
            assertEquals("CODEC_ROOT_SCHEMA", CodecOptions.CODEC_ROOT_SCHEMA_LITERAL);
        }

        @Test
        @DisplayName("the canonical keys are the dotted spec keys")
        void dottedSpelling() {
            assertEquals("codec.rootType", CodecOptions.CODEC_ROOT_TYPE);
            assertEquals("codec.rootSchema", CodecOptions.CODEC_ROOT_SCHEMA);
        }
    }
}
