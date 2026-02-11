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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecValueRegistry#hasWriter(String)} and {@link CodecValueRegistry#hasReader(String)}.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecValueRegistry hasWriter and hasReader")
class CodecValueRegistryHasWriterReaderTest extends CodecValueRegistryTestBase {

    @Nested
    @DisplayName("hasWriter")
    class HasWriter {

        @Test
        @DisplayName("returns true for registered writer")
        void returnsTrueForRegistered() {
            var writer = createStringWriter();
            registry.registerWriter("test", writer);
            assertTrue(registry.hasWriter("test"));
        }

        @Test
        @DisplayName("returns false for unregistered")
        void returnsFalseForUnregistered() {
            assertFalse(registry.hasWriter("nonexistent"));
        }

        @Test
        @DisplayName("returns false for null")
        void returnsFalseForNull() {
            assertFalse(registry.hasWriter(null));
        }
    }

    @Nested
    @DisplayName("hasReader")
    class HasReader {

        @Test
        @DisplayName("returns true for registered reader")
        void returnsTrueForRegistered() {
            var reader = createStringReader();
            registry.registerReader("test", reader);
            assertTrue(registry.hasReader("test"));
        }

        @Test
        @DisplayName("returns false for unregistered")
        void returnsFalseForUnregistered() {
            assertFalse(registry.hasReader("nonexistent"));
        }

        @Test
        @DisplayName("returns false for null")
        void returnsFalseForNull() {
            assertFalse(registry.hasReader(null));
        }
    }
}
