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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamReadException;

/**
 * Tests for {@link CodecReadContext#reset}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@DisplayName("CodecReadContext.reset")
class CodecReadContextResetTest extends CodecReadContextTestBase {

    @Test
    @DisplayName("resets context type")
    void resetsContextType() throws StreamReadException {
        CodecReadContext child = (CodecReadContext) context.createChildArrayContext(1, 1);
        assertTrue(child.inArray());
        child.reset(CodecReadContext.TYPE_OBJECT, 2, 2);
        assertTrue(child.inObject());
        assertFalse(child.inArray());
    }

    @Test
    @DisplayName("clears current name")
    void clearsCurrentName() throws StreamReadException {
        context.setCurrentName("test");
        context.reset(CodecReadContext.TYPE_ROOT, 1, 1);
        assertNull(context.currentName());
    }

    @Test
    @DisplayName("clears current value")
    void clearsCurrentValue() {
        context.assignCurrentValue("testValue");
        context.reset(CodecReadContext.TYPE_ROOT, 1, 1);
        assertNull(context.currentValue());
    }
}
