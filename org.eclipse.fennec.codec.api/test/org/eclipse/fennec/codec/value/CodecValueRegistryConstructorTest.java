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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecValueRegistry} constructor.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecValueRegistry constructor")
class CodecValueRegistryConstructorTest extends CodecValueRegistryTestBase {

    @Test
    @DisplayName("default constructor creates empty registry")
    void defaultConstructorCreatesEmptyRegistry() {
        assertTrue(registry.getWriters().isEmpty());
        assertTrue(registry.getReaders().isEmpty());
    }

    @Test
    @DisplayName("constructor with maps initializes registry")
    void constructorWithMapsInitializesRegistry() {
        var writer = createStringWriter();
        var reader = createStringReader();

        Map<String, CodecValueWriter<?, ?>> writers = new HashMap<>();
        writers.put("stringWriter", writer);

        Map<String, CodecValueReader<?, ?>> readers = new HashMap<>();
        readers.put("stringReader", reader);

        CodecValueRegistry initRegistry = new CodecValueRegistry(writers, readers);

        assertTrue(initRegistry.hasWriter("stringWriter"));
        assertTrue(initRegistry.hasReader("stringReader"));
    }

    @Test
    @DisplayName("constructor with null maps creates empty registry")
    void constructorWithNullMapsCreatesEmptyRegistry() {
        CodecValueRegistry initRegistry = new CodecValueRegistry(null, null);
        assertTrue(initRegistry.getWriters().isEmpty());
        assertTrue(initRegistry.getReaders().isEmpty());
    }
}
