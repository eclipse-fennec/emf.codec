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

/**
 * Tests for {@link CodecWriteContext#reset}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@DisplayName("CodecWriteContext.reset")
class CodecWriteContextResetTest extends CodecWriteContextTestBase {

    @Test
    @DisplayName("resets context type and index")
    void resetsContextTypeAndIndex() {
        CodecWriteContext child = (CodecWriteContext) context.createChildArrayContext();
        assertTrue(child.inArray());
        child.reset(CodecWriteContext.TYPE_OBJECT, null);
        assertTrue(child.inObject());
        assertFalse(child.inArray());
    }

    @Test
    @DisplayName("clears current name")
    void clearCurrentName() {
        CodecWriteContext objContext = (CodecWriteContext) context.createChildObjectContext();
        objContext.writeName("test");
        objContext.reset(CodecWriteContext.TYPE_OBJECT, null);
        assertNull(objContext.currentName());
    }
}
