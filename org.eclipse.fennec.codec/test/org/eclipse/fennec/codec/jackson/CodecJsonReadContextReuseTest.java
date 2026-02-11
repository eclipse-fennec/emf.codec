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
package org.eclipse.fennec.codec.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for context reuse/pooling behavior in {@link CodecJsonReadContext}.
 * <p>
 * Jackson pools context instances for performance. These tests verify that
 * our EMF state is properly reset when contexts are reused.
 * </p>
 */
@DisplayName("CodecJsonReadContext Reuse/Pooling Tests")
class CodecJsonReadContextReuseTest extends CodecJsonReadContextTestBase {

    @Nested
    @DisplayName("Context pooling behavior")
    class ContextPoolingBehavior {

        @Test
        @DisplayName("consecutive child contexts may reuse same instance")
        void consecutiveChildContextsMayReuseSameInstance() {
            // Create first child
            CodecJsonReadContext child1 = context.createChildObjectContext(1, 1);

            // Create second child - Jackson may return the same pooled instance
            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);

            // Both should be usable and have proper parent
            assertSame(context, child1.getParent());
            assertSame(context, child2.getParent());
        }

        @Test
        @DisplayName("reused context has metadata service preserved")
        void reusedContextHasMetadataServicePreserved() {
            CodecJsonReadContext child1 = context.createChildObjectContext(1, 1);
            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);

            // Metadata service should be available in both
            assertSame(metadataService, child1.getMetadataService());
            assertSame(metadataService, child2.getMetadataService());
        }
    }

    @Nested
    @DisplayName("State reset on reuse")
    class StateResetOnReuse {

        @Test
        @DisplayName("type hint is reset when context is reused")
        void typeHintIsResetOnReuse() {
            EClass typeHint = EcoreFactory.eINSTANCE.createEClass();
            typeHint.setName("TestType");

            // First usage
            CodecJsonReadContext child1 = context.createChildObjectContext(1, 1);
            child1.setCurrentTypeHint(typeHint);
            assertEquals(typeHint, child1.getCurrentTypeHint());

            // Second usage - the context is reset
            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);

            // Type hint should be null for the reset context
            assertNull(child2.getCurrentTypeHint(),
                    "Type hint must be null after context reset");
        }

        @Test
        @DisplayName("current EObject is reset when context is reused")
        void currentEObjectIsResetOnReuse() {
            EObject eObject = EcoreFactory.eINSTANCE.createEObject();

            CodecJsonReadContext child1 = context.createChildObjectContext(1, 1);
            child1.setCurrentEObject(eObject);

            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);

            assertNull(child2.getCurrentEObject(),
                    "Current EObject must be null after context reset");
        }

        @Test
        @DisplayName("current feature is reset when context is reused")
        void currentFeatureIsResetOnReuse() {
            EStructuralFeature feature = EcoreFactory.eINSTANCE.createEAttribute();
            feature.setName("testFeature");

            CodecJsonReadContext child1 = context.createChildObjectContext(1, 1);
            child1.setCurrentFeature(feature);

            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);

            assertNull(child2.getCurrentFeature(),
                    "Current feature must be null after context reset");
        }
    }

    @Nested
    @DisplayName("Array element context reuse")
    class ArrayElementContextReuse {

        @Test
        @DisplayName("array element contexts are properly reset between elements")
        void arrayElementContextsProperlyReset() {
            EClass type1 = EcoreFactory.eINSTANCE.createEClass();
            type1.setName("Type1");
            EClass type2 = EcoreFactory.eINSTANCE.createEClass();
            type2.setName("Type2");

            CodecJsonReadContext arrayCtx = context.createChildArrayContext(1, 1);

            // First array element
            CodecJsonReadContext element1 = arrayCtx.createChildObjectContext(2, 1);
            element1.setCurrentTypeHint(type1);
            assertEquals(type1, element1.getCurrentTypeHint());

            // Second array element - should start fresh
            CodecJsonReadContext element2 = arrayCtx.createChildObjectContext(3, 1);
            assertNull(element2.getCurrentTypeHint(),
                    "New array element context should have null type hint");

            // Set type hint for second element
            element2.setCurrentTypeHint(type2);
            assertEquals(type2, element2.getCurrentTypeHint());
        }
    }

    @Nested
    @DisplayName("Reset method behavior")
    class ResetMethodBehavior {

        @Test
        @DisplayName("reset clears EMF state")
        void resetClearsEmfState() {
            EClass typeHint = EcoreFactory.eINSTANCE.createEClass();
            EObject eObject = EcoreFactory.eINSTANCE.createEObject();
            EStructuralFeature feature = EcoreFactory.eINSTANCE.createEAttribute();

            CodecJsonReadContext child = context.createChildObjectContext(1, 1);
            child.setCurrentTypeHint(typeHint);
            child.setCurrentEObject(eObject);
            child.setCurrentFeature(feature);

            // Create another child which triggers reset of the pooled instance
            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);

            // The reset context should have null EMF state
            assertNull(child2.getCurrentTypeHint());
            assertNull(child2.getCurrentEObject());
            assertNull(child2.getCurrentFeature());
        }

        @Test
        @DisplayName("metadata service survives reset")
        void metadataServiceSurvivesReset() {
            CodecJsonReadContext child1 = context.createChildObjectContext(1, 1);
            assertSame(metadataService, child1.getMetadataService());

            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);
            assertSame(metadataService, child2.getMetadataService(),
                    "Metadata service should survive context reset");
        }

        @Test
        @DisplayName("parent reference survives reset")
        void parentReferenceSurvivesReset() {
            CodecJsonReadContext child1 = context.createChildObjectContext(1, 1);
            assertSame(context, child1.getParent());

            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);
            assertSame(context, child2.getParent(),
                    "Parent reference should be correct after reset");
        }
    }
}
