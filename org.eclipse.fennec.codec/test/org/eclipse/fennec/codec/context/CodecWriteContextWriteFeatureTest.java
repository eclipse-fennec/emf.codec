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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecWriteContext#writeFeatureAndFieldName}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@DisplayName("CodecWriteContext.writeFeatureAndFieldName")
class CodecWriteContextWriteFeatureTest extends CodecWriteContextTestBase {

    @Test
    @DisplayName("writes field name and sets feature")
    void writesFieldNameAndSetsFeature() {
        // Create object context first (root can't have field names)
        CodecWriteContext objContext = (CodecWriteContext) context.createChildObjectContext();

        EStructuralFeature feature = mock(EStructuralFeature.class);
        int status = objContext.writeFeatureAndFieldName(feature, "testField");

        assertEquals(CodecWriteContext.STATUS_OK_AS_IS, status);
        assertSame(feature, objContext.getCurrentFeature());
        assertEquals("testField", objContext.currentName());
    }

    @Test
    @DisplayName("returns STATUS_OK_AFTER_COMMA for second field")
    void returnsStatusAfterCommaForSecondField() {
        CodecWriteContext objContext = (CodecWriteContext) context.createChildObjectContext();
        EStructuralFeature feature1 = mock(EStructuralFeature.class);
        EStructuralFeature feature2 = mock(EStructuralFeature.class);

        objContext.writeFeatureAndFieldName(feature1, "field1");
        objContext.writeValue(); // Complete the first field

        int status = objContext.writeFeatureAndFieldName(feature2, "field2");
        assertEquals(CodecWriteContext.STATUS_OK_AFTER_COMMA, status);
    }
}
