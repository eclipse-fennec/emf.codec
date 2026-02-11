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
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecValueRegistry#unregisterReader(String)}.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecValueRegistry unregisterReader")
class CodecValueRegistryUnregisterReaderTest extends CodecValueRegistryTestBase {

    @Test
    @DisplayName("removes registered reader")
    void removesRegisteredReader() {
        var reader = createStringReader();
        registry.registerReader("test", reader);
        registry.unregisterReader("test");

        assertFalse(registry.hasReader("test"));
    }

    @Test
    @DisplayName("handles null name gracefully")
    void handlesNullNameGracefully() {
        registry.unregisterReader(null); // Should not throw
    }

    @Test
    @DisplayName("returns registry for chaining")
    void returnsRegistryForChaining() {
        assertSame(registry, registry.unregisterReader("test"));
    }
}
