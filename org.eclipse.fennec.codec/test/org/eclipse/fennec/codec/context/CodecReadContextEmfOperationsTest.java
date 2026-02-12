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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecReadContext} EMF context operations.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@DisplayName("CodecReadContext EMF context operations")
class CodecReadContextEmfOperationsTest extends CodecReadContextTestBase {

    @Test
    @DisplayName("setCurrentEObject and getCurrentEObject")
    void setAndGetCurrentEObject() {
        EObject eObject = mock(EObject.class);
        context.setCurrentEObject(eObject);
        assertSame(eObject, context.getCurrentEObject());
    }

    @Test
    @DisplayName("setCurrentFeature and getCurrentFeature")
    void setAndGetCurrentFeature() {
        EStructuralFeature feature = mock(EStructuralFeature.class);
        context.setCurrentFeature(feature);
        assertSame(feature, context.getCurrentFeature());
    }

    @Test
    @DisplayName("resetFeature sets current feature to null")
    void resetFeatureSetsNull() {
        EStructuralFeature feature = mock(EStructuralFeature.class);
        context.setCurrentFeature(feature);
        context.resetFeature();
        assertNull(context.getCurrentFeature());
    }

    @Test
    @DisplayName("setResource and getResource")
    void setAndGetResource() {
        Resource resource = mock(Resource.class);
        context.setResource(resource);
        assertSame(resource, context.getResource());
    }

    @Test
    @DisplayName("getClassMetadata delegates to metadata service")
    void getClassMetadataDelegates() {
        EClass eClass = mock(EClass.class);
        ClassMetadata metadata = mock(ClassMetadata.class);
        when(metadataService.getClassMetadata(eClass)).thenReturn(metadata);

        assertSame(metadata, context.getClassMetadata(eClass));
    }
}
