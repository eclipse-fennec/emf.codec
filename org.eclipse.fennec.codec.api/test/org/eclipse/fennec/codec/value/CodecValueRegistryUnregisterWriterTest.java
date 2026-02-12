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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecValueRegistry#unregisterWriter(String)}.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecValueRegistry unregisterWriter")
class CodecValueRegistryUnregisterWriterTest extends CodecValueRegistryTestBase {

    @Test
    @DisplayName("removes registered writer")
    void removesRegisteredWriter() {
        var writer = createStringWriter();
        registry.registerWriter("test", writer);
        registry.unregisterWriter("test");

        assertFalse(registry.hasWriter("test"));
    }

    @Test
    @DisplayName("handles null name gracefully")
    void handlesNullNameGracefully() {
        registry.unregisterWriter(null); // Should not throw
    }

    @Test
    @DisplayName("handles non-existent name gracefully")
    void handlesNonExistentNameGracefully() {
        registry.unregisterWriter("nonexistent"); // Should not throw
    }

    @Test
    @DisplayName("returns registry for chaining")
    void returnsRegistryForChaining() {
        assertSame(registry, registry.unregisterWriter("test"));
    }
}
