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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecValueRegistry#clear()}.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecValueRegistry clear")
class CodecValueRegistryClearTest extends CodecValueRegistryTestBase {

    @Test
    @DisplayName("clears all writers and readers")
    void clearsAllWritersAndReaders() {
        var writer = createStringWriter();
        var reader = createStringReader();

        registry.registerWriter("writer", writer);
        registry.registerReader("reader", reader);
        registry.clear();

        assertTrue(registry.getWriters().isEmpty());
        assertTrue(registry.getReaders().isEmpty());
    }
}
