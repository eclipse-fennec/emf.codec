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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecReadContext#createRootContext}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@DisplayName("CodecReadContext.createRootContext")
class CodecReadContextCreateRootTest extends CodecReadContextTestBase {

    @Test
    @DisplayName("creates root context with TYPE_ROOT")
    void createsRootContextWithTypeRoot() {
        assertTrue(context.inRoot());
        assertFalse(context.inArray());
        assertFalse(context.inObject());
    }

    @Test
    @DisplayName("creates root context with no parent")
    void createsRootContextWithNoParent() {
        assertNull(context.getParent());
        assertFalse(context.hasParentContext());
    }

    @Test
    @DisplayName("creates root context with metadata service")
    void createsRootContextWithMetadataService() {
        assertSame(metadataService, context.getMetadataService());
    }
}
