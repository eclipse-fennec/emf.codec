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
package org.eclipse.fennec.codec.prefix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * The registry contract of 14-custom-values.md §13.3 (issue #193 / #195): registration by key,
 * duplicates rejected, registration order preserved, copies independent.
 */
@DisplayName("CodecPrefixRegistry")
class CodecPrefixRegistryTest {

    private CodecPrefixRegistry registry;
    private final CodecPrefixWriter writer = (key, object, ctx) -> false;
    private final CodecPrefixReader reader = (key, target, ctx) -> { };

    @BeforeEach
    void setUp() {
        registry = new CodecPrefixRegistry();
    }

    @Nested
    @DisplayName("registration")
    class Registration {

        @Test
        @DisplayName("a writer and a reader are registered independently under the same key")
        void writerAndReaderIndependently() {
            registry.register("_owner", writer);
            assertTrue(registry.hasWriter("_owner"));
            assertFalse(registry.hasReader("_owner"), "a key may have one of each; a writer is not a reader");

            registry.register("_owner", reader);
            assertTrue(registry.hasReader("_owner"));
            assertSame(writer, registry.getWriter("_owner").orElseThrow());
            assertSame(reader, registry.getReader("_owner").orElseThrow());
        }

        @Test
        @DisplayName("a second writer for a key is rejected - no first wins, no priority")
        void duplicateWriterRejected() {
            registry.register("_owner", writer);
            CodecPrefixWriter other = (key, object, ctx) -> true;

            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> registry.register("_owner", other));

            assertTrue(e.getMessage().contains("_owner"), "the refusal names the key, was: " + e.getMessage());
            assertSame(writer, registry.getWriter("_owner").orElseThrow(), "the first registration stands");
        }

        @Test
        @DisplayName("a second reader for a key is rejected independently of the writer side")
        void duplicateReaderRejected() {
            registry.register("_owner", reader);
            registry.register("_owner", writer);
            CodecPrefixReader other = (key, target, ctx) -> { };

            assertThrows(IllegalArgumentException.class, () -> registry.register("_owner", other));
            assertTrue(registry.hasWriter("_owner"), "the writer side is untouched by the reader refusal");
        }

        @Test
        @DisplayName("one instance under two keys is two entries, each told its own key")
        void oneInstanceTwoKeys() {
            registry.register("_owner", writer).register("_ownerRef", writer);

            assertEquals(List.of("_owner", "_ownerRef"), registry.writerKeys());
            assertSame(registry.getWriter("_owner").orElseThrow(), registry.getWriter("_ownerRef").orElseThrow());
        }

        @Test
        @DisplayName("null or empty keys and null handlers are refused")
        void nullsRefused() {
            assertThrows(IllegalArgumentException.class, () -> registry.register("", writer));
            assertThrows(IllegalArgumentException.class, () -> registry.register(null, writer));
            assertThrows(NullPointerException.class, () -> registry.register("_owner", (CodecPrefixWriter) null));
            assertThrows(NullPointerException.class, () -> registry.register("_owner", (CodecPrefixReader) null));
            assertTrue(registry.isEmpty());
        }
    }

    @Nested
    @DisplayName("order and lookup")
    class OrderAndLookup {

        @Test
        @DisplayName("writer keys come back in registration order - that is the output order")
        void writerOrderIsRegistrationOrder() {
            registry.register("_zeta", writer).register("_alpha", writer).register("_mid", writer);

            assertEquals(List.of("_zeta", "_alpha", "_mid"), registry.writerKeys(),
                    "the prefix is written in registration order, not alphabetically");
        }

        @Test
        @DisplayName("reader keys are a set in registration order")
        void readerKeys() {
            registry.register("_b", reader).register("_a", reader);

            assertEquals(Set.of("_a", "_b"), registry.readerKeys());
            assertEquals(List.of("_b", "_a"), List.copyOf(registry.readerKeys()));
        }

        @Test
        @DisplayName("lookups on unknown or null keys are empty, not exceptions")
        void unknownKeys() {
            assertTrue(registry.getWriter("_nope").isEmpty());
            assertTrue(registry.getReader(null).isEmpty());
            assertFalse(registry.hasWriter(null));
        }

        @Test
        @DisplayName("unregister frees the key for a new registration and leaves the other side alone")
        void unregister() {
            registry.register("_owner", writer).register("_owner", reader);

            registry.unregisterWriter("_owner");

            assertFalse(registry.hasWriter("_owner"));
            assertTrue(registry.hasReader("_owner"));
            CodecPrefixWriter other = (key, object, ctx) -> true;
            registry.register("_owner", other);
            assertSame(other, registry.getWriter("_owner").orElseThrow());
        }
    }

    @Nested
    @DisplayName("copy")
    class Copy {

        @Test
        @DisplayName("a copy has the same entries in the same order and is independent afterwards")
        void copyIsIndependent() {
            registry.register("_owner", writer).register("_tenant", writer).register("_owner", reader);

            CodecPrefixRegistry copy = registry.copy();

            assertEquals(registry.writerKeys(), copy.writerKeys());
            assertEquals(registry.readerKeys(), copy.readerKeys());

            copy.register("_extra", writer);
            registry.unregisterReader("_owner");

            assertFalse(registry.hasWriter("_extra"), "a registration on the copy does not reach the original");
            assertTrue(copy.hasReader("_owner"), "an unregistration on the original does not reach the copy");
        }
    }
}
