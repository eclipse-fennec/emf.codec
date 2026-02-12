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
 * Tests for {@link CodecReadContext#resolveEClass(String)}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@DisplayName("CodecReadContext.resolveEClass(String)")
class CodecReadContextResolveEClassTest extends CodecReadContextTestBase {

    @Test
    @DisplayName("returns null for null type value")
    void returnsNullForNullTypeValue() {
        assertNull(context.resolveEClass(null));
    }

    @Test
    @DisplayName("returns null for empty type value")
    void returnsNullForEmptyTypeValue() {
        assertNull(context.resolveEClass(""));
    }

    @Test
    @DisplayName("resolves EClass by URI")
    void resolvesEClassByUri() {
        String uri = "http://example.org/model#//Person";
        EClass personClass = mock(EClass.class);
        ClassMetadata metadata = mock(ClassMetadata.class);
        when(metadata.getEClass()).thenReturn(personClass);
        when(metadataService.getClassMetadataByURI(uri)).thenReturn(metadata);

        assertSame(personClass, context.resolveEClass(uri));
    }

    @Test
    @DisplayName("returns null when URI not found")
    void returnsNullWhenUriNotFound() {
        when(metadataService.getClassMetadataByURI("unknown")).thenReturn(null);
        assertNull(context.resolveEClass("unknown"));
    }
}
