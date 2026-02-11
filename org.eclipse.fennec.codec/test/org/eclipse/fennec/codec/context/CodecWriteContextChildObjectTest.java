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
package org.eclipse.fennec.codec.context;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecWriteContext#createChildObjectContext}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@DisplayName("CodecWriteContext.createChildObjectContext")
class CodecWriteContextChildObjectTest extends CodecWriteContextTestBase {

    @Test
    @DisplayName("creates child object context")
    void createsChildObjectContext() {
        CodecWriteContext child = (CodecWriteContext) context.createChildObjectContext();
        assertNotNull(child);
        assertTrue(child.inObject());
        assertFalse(child.inArray());
        assertFalse(child.inRoot());
    }

    @Test
    @DisplayName("child context has parent")
    void childHasParent() {
        CodecWriteContext child = (CodecWriteContext) context.createChildObjectContext();
        assertSame(context, child.getParent());
    }
}
