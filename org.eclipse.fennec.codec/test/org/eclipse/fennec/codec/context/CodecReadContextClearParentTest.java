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
package org.eclipse.fennec.codec.context;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecReadContext#clearAndGetParent}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@DisplayName("CodecReadContext.clearAndGetParent")
class CodecReadContextClearParentTest extends CodecReadContextTestBase {

    @Test
    @DisplayName("returns parent context")
    void returnsParentContext() {
        CodecReadContext child = (CodecReadContext) context.createChildObjectContext(1, 1);
        assertSame(context, child.clearAndGetParent());
    }

    @Test
    @DisplayName("clears current value")
    void clearsCurrentValue() {
        CodecReadContext child = (CodecReadContext) context.createChildObjectContext(1, 1);
        child.assignCurrentValue("testValue");
        child.clearAndGetParent();
        assertNull(child.currentValue());
    }
}
