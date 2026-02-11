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
 * Tests for {@link CodecValueRegistry#registerWriter(String, CodecValueWriter)}
 * and {@link CodecValueRegistry#register(CodecValueWriter)}.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecValueRegistry registerWriter")
class CodecValueRegistryRegisterWriterTest extends CodecValueRegistryTestBase {

    @Nested
    @DisplayName("explicit name registration")
    class ExplicitNameRegistration {

        @Test
        @DisplayName("registers writer with valid name")
        void registersWriterWithValidName() {
            var writer = createStringWriter();
            registry.registerWriter("test", writer);
            assertTrue(registry.hasWriter("test"));
        }

        @Test
        @DisplayName("throws exception for null name")
        void throwsExceptionForNullName() {
            var writer = createStringWriter();
            assertThrows(IllegalArgumentException.class,
                    () -> registry.registerWriter(null, writer));
        }

        @Test
        @DisplayName("throws exception for empty name")
        void throwsExceptionForEmptyName() {
            var writer = createStringWriter();
            assertThrows(IllegalArgumentException.class,
                    () -> registry.registerWriter("", writer));
        }

        @Test
        @DisplayName("throws exception for null writer")
        void throwsExceptionForNullWriter() {
            assertThrows(IllegalArgumentException.class,
                    () -> registry.registerWriter("test", null));
        }

        @Test
        @DisplayName("overwrites existing writer with same name")
        void overwritesExistingWriter() {
            var writer1 = createStringWriter();
            var writer2 = createIntWriter();

            registry.registerWriter("test", writer1);
            registry.registerWriter("test", writer2);

            assertSame(writer2, registry.getWriter("test").orElse(null));
        }

        @Test
        @DisplayName("returns registry for chaining")
        void returnsRegistryForChaining() {
            var writer = createStringWriter();
            assertSame(registry, registry.registerWriter("test", writer));
        }
    }

    @Nested
    @DisplayName("auto-registration using getName()")
    class AutoRegistration {

        @Test
        @DisplayName("registers writer using getName()")
        void registersWriterUsingGetName() {
            var writer = createWriterWithName("customWriter");
            registry.register(writer);
            assertTrue(registry.hasWriter("customWriter"));
        }

        @Test
        @DisplayName("throws exception for null writer")
        void throwsExceptionForNullWriter() {
            assertThrows(IllegalArgumentException.class,
                    () -> registry.register((CodecValueWriter<?, ?>) null));
        }

        @Test
        @DisplayName("throws exception when getName() returns null")
        void throwsExceptionWhenGetNameReturnsNull() {
            var writer = createWriterWithName(null);
            assertThrows(IllegalArgumentException.class,
                    () -> registry.register(writer));
        }

        @Test
        @DisplayName("throws exception when getName() returns empty")
        void throwsExceptionWhenGetNameReturnsEmpty() {
            var writer = createWriterWithName("");
            assertThrows(IllegalArgumentException.class,
                    () -> registry.register(writer));
        }

        @Test
        @DisplayName("returns registry for chaining")
        void returnsRegistryForChaining() {
            var writer = createStringWriter();
            assertSame(registry, registry.register(writer));
        }
    }

    @Nested
    @DisplayName("registerAll")
    class RegisterAll {

        @Test
        @DisplayName("registers multiple writers")
        void registersMultipleWriters() {
            var writer1 = createWriterWithName("writer1");
            var writer2 = createWriterWithName("writer2");

            registry.registerAll(writer1, writer2);

            assertTrue(registry.hasWriter("writer1"));
            assertTrue(registry.hasWriter("writer2"));
        }

        @Test
        @DisplayName("returns registry for chaining")
        void returnsRegistryForChaining() {
            var writer = createStringWriter();
            assertSame(registry, registry.registerAll(writer));
        }
    }
}
