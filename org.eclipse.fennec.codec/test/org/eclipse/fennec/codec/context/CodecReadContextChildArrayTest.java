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

import tools.jackson.core.TokenStreamContext;

/**
 * Tests for {@link CodecReadContext#createChildArrayContext}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@DisplayName("CodecReadContext.createChildArrayContext")
class CodecReadContextChildArrayTest extends CodecReadContextTestBase {

    @Test
    @DisplayName("creates child array context")
    void createsChildArrayContext() {
        CodecReadContext child = (CodecReadContext) context.createChildArrayContext(1, 1);
        assertNotNull(child);
        assertTrue(child.inArray());
        assertFalse(child.inObject());
        assertFalse(child.inRoot());
    }

    @Test
    @DisplayName("child context has parent")
    void childHasParent() {
        CodecReadContext child = (CodecReadContext) context.createChildArrayContext(1, 1);
        assertSame(context, child.getParent());
        assertTrue(child.hasParentContext());
    }

    @Test
    @DisplayName("child context inherits metadata service")
    void childInheritsMetadataService() {
        CodecReadContext child = (CodecReadContext) context.createChildArrayContext(1, 1);
        assertSame(metadataService, child.getMetadataService());
    }

    @Test
    @DisplayName("reuses cached child context")
    void reusesCachedChildContext() {
        TokenStreamContext child1 = context.createChildArrayContext(1, 1);
        ((CodecReadContext) child1).clearAndGetParent();
        TokenStreamContext child2 = context.createChildArrayContext(2, 2);
        assertSame(child1, child2);
    }
}
