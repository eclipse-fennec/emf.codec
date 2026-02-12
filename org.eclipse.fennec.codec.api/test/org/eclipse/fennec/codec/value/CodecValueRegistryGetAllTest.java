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
package org.eclipse.fennec.codec.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecValueRegistry#getWriters()} and {@link CodecValueRegistry#getReaders()}.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecValueRegistry getWriters and getReaders")
class CodecValueRegistryGetAllTest extends CodecValueRegistryTestBase {

    @Nested
    @DisplayName("getWriters")
    class GetWriters {

        @Test
        @DisplayName("returns unmodifiable view")
        void returnsUnmodifiableView() {
            var writer = createStringWriter();
            registry.registerWriter("test", writer);

            Map<String, CodecValueWriter<?, ?>> writers = registry.getWriters();
            assertEquals(1, writers.size());
            assertThrows(UnsupportedOperationException.class,
                    () -> writers.put("new", writer));
        }
    }

    @Nested
    @DisplayName("getReaders")
    class GetReaders {

        @Test
        @DisplayName("returns unmodifiable view")
        void returnsUnmodifiableView() {
            var reader = createStringReader();
            registry.registerReader("test", reader);

            Map<String, CodecValueReader<?, ?>> readers = registry.getReaders();
            assertEquals(1, readers.size());
            assertThrows(UnsupportedOperationException.class,
                    () -> readers.put("new", reader));
        }
    }
}
