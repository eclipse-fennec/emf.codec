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
package org.eclipse.fennec.codec.jackson;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecTokenBufferReadContext}.
 * <p>
 * This context is used when replaying buffered tokens (e.g., after scanning
 * for featurePath-based type resolution). It must preserve EMF context from
 * the original parser context.
 * </p>
 */
@DisplayName("CodecTokenBufferReadContext Tests")
class CodecTokenBufferReadContextTest {

    private MetadataService metadataService;
    private EffectiveCodecConfig effectiveConfig;

    @BeforeEach
    void setUp() {
        metadataService = mock(MetadataService.class);
        effectiveConfig = EffectiveCodecConfig.builder()
                .resolver(ConfigurationResolver.defaults())
                .diagnostics(new DiagnosticCollector())
                .metadataService(metadataService)
                .build();
    }

    @Nested
    @DisplayName("Context creation from CodecJsonReadContext")
    class CreationFromCodecJsonReadContext {

        @Test
        @DisplayName("preserves EMFContextHolder from original CodecJsonReadContext")
        void preservesEmfContextHolder() {
            // Create original context with EMF state
            CodecJsonReadContext origContext = CodecJsonReadContext.createRootContext(null, effectiveConfig);
            EClass typeHint = EcoreFactory.eINSTANCE.createEClass();
            EObject eObject = EcoreFactory.eINSTANCE.createEObject();
            Resource resource = mock(Resource.class);

            origContext.setCurrentTypeHint(typeHint);
            origContext.setCurrentEObject(eObject);
            origContext.setResource(resource);

            // Create token buffer context from original
            CodecTokenBufferReadContext bufferContext = CodecTokenBufferReadContext.createRootContext(origContext);

            // Verify EMF state is preserved
            assertSame(typeHint, bufferContext.getCurrentTypeHint());
            assertSame(eObject, bufferContext.getCurrentEObject());
            assertSame(resource, bufferContext.getResource());
            assertSame(metadataService, bufferContext.getMetadataService());
        }

        @Test
        @DisplayName("shares EMFContextHolder with original context")
        void sharesEmfContextHolder() {
            CodecJsonReadContext origContext = CodecJsonReadContext.createRootContext(null, effectiveConfig);

            CodecTokenBufferReadContext bufferContext = CodecTokenBufferReadContext.createRootContext(origContext);

            // Should share the same holder instance
            assertSame(origContext.getEMFContextHolder(), bufferContext.getEMFContextHolder());
        }
    }

    @Nested
    @DisplayName("Child context creation")
    class ChildContextCreation {

        @Test
        @DisplayName("creates child object context")
        void createsChildObjectContext() {
            CodecTokenBufferReadContext root = CodecTokenBufferReadContext.createRootContext(null);

            CodecTokenBufferReadContext child = root.createChildObjectContext(1, 1);

            assertNotNull(child);
            assertTrue(child.inObject());
            assertSame(root, child.getParent());
        }

        @Test
        @DisplayName("creates child array context")
        void createsChildArrayContext() {
            CodecTokenBufferReadContext root = CodecTokenBufferReadContext.createRootContext(null);

            CodecTokenBufferReadContext child = root.createChildArrayContext(1, 1);

            assertNotNull(child);
            assertTrue(child.inArray());
            assertSame(root, child.getParent());
        }

        @Test
        @DisplayName("child inherits metadata service from parent")
        void childInheritsMetadataService() {
            CodecJsonReadContext origContext = CodecJsonReadContext.createRootContext(null, effectiveConfig);
            CodecTokenBufferReadContext root = CodecTokenBufferReadContext.createRootContext(origContext);

            CodecTokenBufferReadContext child = root.createChildObjectContext(1, 1);

            assertSame(metadataService, child.getMetadataService());
        }
    }

    @Nested
    @DisplayName("Type hint isolation in buffer context")
    class TypeHintIsolation {

        @Test
        @DisplayName("child buffer context starts with null type hint")
        void childStartsWithNullTypeHint() {
            CodecTokenBufferReadContext root = CodecTokenBufferReadContext.createRootContext(null);
            EClass rootType = EcoreFactory.eINSTANCE.createEClass();
            root.setCurrentTypeHint(rootType);

            CodecTokenBufferReadContext child = root.createChildObjectContext(1, 1);

            // Child should start fresh, not inherit parent's type hint
            assertNull(child.getCurrentTypeHint());
        }

        @Test
        @DisplayName("setting child type hint does not affect parent")
        void settingChildTypeHintDoesNotAffectParent() {
            CodecTokenBufferReadContext root = CodecTokenBufferReadContext.createRootContext(null);
            EClass rootType = EcoreFactory.eINSTANCE.createEClass();
            EClass childType = EcoreFactory.eINSTANCE.createEClass();
            root.setCurrentTypeHint(rootType);

            CodecTokenBufferReadContext child = root.createChildObjectContext(1, 1);
            child.setCurrentTypeHint(childType);

            assertSame(rootType, root.getCurrentTypeHint());
            assertSame(childType, child.getCurrentTypeHint());
        }
    }

    @Nested
    @DisplayName("Parent navigation")
    class ParentNavigation {

        @Test
        @DisplayName("parentOrCopy returns parent for buffer context children")
        void parentOrCopyReturnsParent() {
            CodecTokenBufferReadContext root = CodecTokenBufferReadContext.createRootContext(null);
            CodecTokenBufferReadContext child = root.createChildObjectContext(1, 1);

            CodecTokenBufferReadContext parent = child.parentOrCopy();

            assertSame(root, parent);
        }

        @Test
        @DisplayName("clearAndGetParent returns parent")
        void clearAndGetParentReturnsParent() {
            CodecTokenBufferReadContext root = CodecTokenBufferReadContext.createRootContext(null);
            CodecTokenBufferReadContext child = root.createChildObjectContext(1, 1);
            child.assignCurrentValue("test");

            var parent = child.clearAndGetParent();

            assertSame(root, parent);
            assertNull(child.currentValue());
        }

        @Test
        @DisplayName("hasParentContext returns true for child")
        void hasParentContextReturnsTrue() {
            CodecTokenBufferReadContext root = CodecTokenBufferReadContext.createRootContext(null);
            CodecTokenBufferReadContext child = root.createChildObjectContext(1, 1);

            assertTrue(child.hasParentContext());
        }

        @Test
        @DisplayName("hasParentContext returns false for root")
        void hasParentContextReturnsFalse() {
            CodecTokenBufferReadContext root = CodecTokenBufferReadContext.createRootContext(null);

            assertFalse(root.hasParentContext());
        }
    }

    @Nested
    @DisplayName("EMF operations")
    class EmfOperations {

        @Test
        @DisplayName("can set and get current EObject")
        void canSetAndGetCurrentEObject() {
            CodecTokenBufferReadContext context = CodecTokenBufferReadContext.createRootContext(null);
            EObject eObject = EcoreFactory.eINSTANCE.createEObject();

            context.setCurrentEObject(eObject);

            assertSame(eObject, context.getCurrentEObject());
        }

        @Test
        @DisplayName("can set and get current feature")
        void canSetAndGetCurrentFeature() {
            CodecTokenBufferReadContext context = CodecTokenBufferReadContext.createRootContext(null);
            EStructuralFeature feature = EcoreFactory.eINSTANCE.createEAttribute();

            context.setCurrentFeature(feature);

            assertSame(feature, context.getCurrentFeature());
        }

        @Test
        @DisplayName("resetFeature clears current feature")
        void resetFeatureClearsCurrentFeature() {
            CodecTokenBufferReadContext context = CodecTokenBufferReadContext.createRootContext(null);
            EStructuralFeature feature = EcoreFactory.eINSTANCE.createEAttribute();
            context.setCurrentFeature(feature);

            context.resetFeature();

            assertNull(context.getCurrentFeature());
        }

        @Test
        @DisplayName("can set and get resource")
        void canSetAndGetResource() {
            CodecTokenBufferReadContext context = CodecTokenBufferReadContext.createRootContext(null);
            Resource resource = mock(Resource.class);

            context.setResource(resource);

            assertSame(resource, context.getResource());
        }
    }

    @Nested
    @DisplayName("Reset behavior")
    class ResetBehavior {

        @Test
        @DisplayName("reset clears type and index")
        void resetClearsTypeAndIndex() {
            CodecTokenBufferReadContext context = CodecTokenBufferReadContext.createRootContext(null);
            CodecTokenBufferReadContext child = context.createChildObjectContext(1, 1);

            CodecTokenBufferReadContext reset = child.reset(CodecTokenBufferReadContext.TYPE_ARRAY, 5, 10);

            assertSame(child, reset);
            assertTrue(child.inArray());
        }
    }
}
