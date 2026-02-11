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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.eclipse.emf.ecore.EAttribute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecValueRegistry#getWriter(String)}.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecValueRegistry getWriter")
class CodecValueRegistryGetWriterTest extends CodecValueRegistryTestBase {

    @Test
    @DisplayName("returns writer when found")
    void returnsWriterWhenFound() {
        var writer = createStringWriter();
        registry.registerWriter("test", writer);

        Optional<CodecValueWriter<?, ?>> result = registry.getWriter("test");
        assertTrue(result.isPresent());
        assertSame(writer, result.get());
    }

    @Test
    @DisplayName("returns empty when not found")
    void returnsEmptyWhenNotFound() {
        Optional<CodecValueWriter<?, ?>> result = registry.getWriter("nonexistent");
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("returns empty for null name")
    void returnsEmptyForNullName() {
        Optional<CodecValueWriter<?, ?>> result = registry.getWriter(null);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("returns empty for empty name")
    void returnsEmptyForEmptyName() {
        Optional<CodecValueWriter<?, ?>> result = registry.getWriter("");
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("getWriter with type and feature class returns typed writer")
    void getWriterWithTypeAndFeatureClassReturnsTypedWriter() {
        var writer = createStringWriter();
        registry.registerWriter("test", writer);

        Optional<CodecValueWriter<String, EAttribute>> result =
            registry.getWriter("test", String.class, EAttribute.class);
        assertTrue(result.isPresent());
    }
}
