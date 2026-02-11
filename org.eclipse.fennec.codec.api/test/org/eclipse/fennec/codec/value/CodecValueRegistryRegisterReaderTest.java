/**
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
package org.eclipse.fennec.codec.value;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecValueRegistry#registerReader(String, CodecValueReader)}
 * and {@link CodecValueRegistry#register(CodecValueReader)}.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecValueRegistry registerReader")
class CodecValueRegistryRegisterReaderTest extends CodecValueRegistryTestBase {

    @Nested
    @DisplayName("explicit name registration")
    class ExplicitNameRegistration {

        @Test
        @DisplayName("registers reader with valid name")
        void registersReaderWithValidName() {
            var reader = createStringReader();
            registry.registerReader("test", reader);
            assertTrue(registry.hasReader("test"));
        }

        @Test
        @DisplayName("throws exception for null name")
        void throwsExceptionForNullName() {
            var reader = createStringReader();
            assertThrows(IllegalArgumentException.class,
                    () -> registry.registerReader(null, reader));
        }

        @Test
        @DisplayName("throws exception for empty name")
        void throwsExceptionForEmptyName() {
            var reader = createStringReader();
            assertThrows(IllegalArgumentException.class,
                    () -> registry.registerReader("", reader));
        }

        @Test
        @DisplayName("throws exception for null reader")
        void throwsExceptionForNullReader() {
            assertThrows(IllegalArgumentException.class,
                    () -> registry.registerReader("test", null));
        }

        @Test
        @DisplayName("returns registry for chaining")
        void returnsRegistryForChaining() {
            var reader = createStringReader();
            assertSame(registry, registry.registerReader("test", reader));
        }
    }

    @Nested
    @DisplayName("auto-registration using getName()")
    class AutoRegistration {

        @Test
        @DisplayName("registers reader using getName()")
        void registersReaderUsingGetName() {
            var reader = createReaderWithName("customReader");
            registry.register(reader);
            assertTrue(registry.hasReader("customReader"));
        }

        @Test
        @DisplayName("throws exception for null reader")
        void throwsExceptionForNullReader() {
            assertThrows(IllegalArgumentException.class,
                    () -> registry.register((CodecValueReader<?, ?>) null));
        }

        @Test
        @DisplayName("throws exception when getName() returns null")
        void throwsExceptionWhenGetNameReturnsNull() {
            var reader = createReaderWithName(null);
            assertThrows(IllegalArgumentException.class,
                    () -> registry.register(reader));
        }

        @Test
        @DisplayName("throws exception when getName() returns empty")
        void throwsExceptionWhenGetNameReturnsEmpty() {
            var reader = createReaderWithName("");
            assertThrows(IllegalArgumentException.class,
                    () -> registry.register(reader));
        }

        @Test
        @DisplayName("returns registry for chaining")
        void returnsRegistryForChaining() {
            var reader = createStringReader();
            assertSame(registry, registry.register(reader));
        }
    }

    @Nested
    @DisplayName("registerAll")
    class RegisterAll {

        @Test
        @DisplayName("registers multiple readers")
        void registersMultipleReaders() {
            var reader1 = createReaderWithName("reader1");
            var reader2 = createReaderWithName("reader2");

            registry.registerAll(reader1, reader2);

            assertTrue(registry.hasReader("reader1"));
            assertTrue(registry.hasReader("reader2"));
        }

        @Test
        @DisplayName("returns registry for chaining")
        void returnsRegistryForChaining() {
            var reader = createStringReader();
            assertSame(registry, registry.registerAll(reader));
        }
    }
}
