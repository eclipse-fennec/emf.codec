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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecReadContext#resolveEClass(String, EClass)} - Spec 15.4 Type Resolution.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#154-codec_root_object-option">Spec 15.4: Type Resolution</a>
 */
@DisplayName("CodecReadContext.resolveEClass(String, EClass) - Spec 15.4 Type Resolution")
class CodecReadContextResolveEClassWithHintTest extends CodecReadContextTestBase {

    @Test
    @DisplayName("priority 1: resolves from type value when valid")
    void priority1ResolvesFromTypeValue() {
        String typeValue = "http://example.org/model#//Employee";
        EClass employeeClass = mock(EClass.class);
        EClass personClass = mock(EClass.class);
        ClassMetadata metadata = mock(ClassMetadata.class);
        when(metadata.getEClass()).thenReturn(employeeClass);
        when(metadataService.getClassMetadataByURI(typeValue)).thenReturn(metadata);

        assertSame(employeeClass, context.resolveEClass(typeValue, personClass));
    }

    @Test
    @DisplayName("priority 2: returns hint when type value is null")
    void priority2ReturnsHintWhenTypeValueNull() {
        EClass hintClass = mock(EClass.class);
        assertSame(hintClass, context.resolveEClass(null, hintClass));
    }

    @Test
    @DisplayName("priority 2: returns hint when type value is empty")
    void priority2ReturnsHintWhenTypeValueEmpty() {
        EClass hintClass = mock(EClass.class);
        assertSame(hintClass, context.resolveEClass("", hintClass));
    }

    @Test
    @DisplayName("priority 2: returns hint when type value not resolved")
    void priority2ReturnsHintWhenTypeValueNotResolved() {
        EClass hintClass = mock(EClass.class);
        when(metadataService.getClassMetadataByURI("unknown")).thenReturn(null);
        assertSame(hintClass, context.resolveEClass("unknown", hintClass));
    }

    @Test
    @DisplayName("returns null when both type value and hint are null")
    void returnsNullWhenBothNull() {
        assertNull(context.resolveEClass(null, null));
    }
}
