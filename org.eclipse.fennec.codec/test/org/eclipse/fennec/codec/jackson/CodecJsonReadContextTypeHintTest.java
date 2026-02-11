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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for type hint isolation between parent and child contexts.
 * <p>
 * Verifies that:
 * <ul>
 *   <li>Type hints are isolated per context</li>
 *   <li>Child type hints don't affect parent</li>
 *   <li>Parent type hints don't leak to child</li>
 *   <li>Type hints are cleared on context reset/reuse</li>
 * </ul>
 * </p>
 */
@DisplayName("CodecJsonReadContext Type Hint Isolation Tests")
class CodecJsonReadContextTypeHintTest extends CodecJsonReadContextTestBase {

    private EClass parentTypeHint;
    private EClass childTypeHint;

    @BeforeEach
    void setUpTypeHints() {
        parentTypeHint = EcoreFactory.eINSTANCE.createEClass();
        parentTypeHint.setName("ParentType");

        childTypeHint = EcoreFactory.eINSTANCE.createEClass();
        childTypeHint.setName("ChildType");
    }

    @Nested
    @DisplayName("Type hint isolation")
    class TypeHintIsolation {

        @Test
        @DisplayName("child context starts with null type hint")
        void childStartsWithNullTypeHint() {
            context.setCurrentTypeHint(parentTypeHint);

            CodecJsonReadContext child = context.createChildObjectContext(1, 1);

            assertNull(child.getCurrentTypeHint(),
                    "Child context should start with null type hint, not inherit from parent");
        }

        @Test
        @DisplayName("setting child type hint does not affect parent")
        void settingChildTypeHintDoesNotAffectParent() {
            context.setCurrentTypeHint(parentTypeHint);
            CodecJsonReadContext child = context.createChildObjectContext(1, 1);

            child.setCurrentTypeHint(childTypeHint);

            assertSame(parentTypeHint, context.getCurrentTypeHint(),
                    "Parent type hint should remain unchanged");
            assertSame(childTypeHint, child.getCurrentTypeHint(),
                    "Child should have its own type hint");
        }

        @Test
        @DisplayName("changing parent type hint does not affect existing child")
        void changingParentTypeHintDoesNotAffectChild() {
            CodecJsonReadContext child = context.createChildObjectContext(1, 1);
            child.setCurrentTypeHint(childTypeHint);

            // Change parent after child is created
            context.setCurrentTypeHint(parentTypeHint);

            assertSame(childTypeHint, child.getCurrentTypeHint(),
                    "Child type hint should remain unchanged when parent changes");
        }

        @Test
        @DisplayName("nested children at different levels have independent type hints")
        void nestedChildrenHaveIndependentTypeHints() {
            // Jackson pools sibling contexts, so we test nested (parent-child) independence
            EClass type1 = EcoreFactory.eINSTANCE.createEClass();
            type1.setName("Type1");
            EClass type2 = EcoreFactory.eINSTANCE.createEClass();
            type2.setName("Type2");

            // Level 1: object context
            CodecJsonReadContext level1 = context.createChildObjectContext(1, 1);
            level1.setCurrentTypeHint(type1);

            // Level 2: array context (child of level1, not sibling)
            CodecJsonReadContext level2 = level1.createChildArrayContext(2, 1);
            level2.setCurrentTypeHint(type2);

            // Each nesting level maintains independent type hints
            assertSame(type1, level1.getCurrentTypeHint());
            assertSame(type2, level2.getCurrentTypeHint());
            assertNull(context.getCurrentTypeHint());
        }
    }

    @Nested
    @DisplayName("Type hint in nested contexts")
    class TypeHintInNestedContexts {

        @Test
        @DisplayName("deep nesting maintains type hint isolation")
        void deepNestingMaintainsIsolation() {
            EClass rootType = EcoreFactory.eINSTANCE.createEClass();
            rootType.setName("RootType");
            EClass level1Type = EcoreFactory.eINSTANCE.createEClass();
            level1Type.setName("Level1Type");
            EClass level2Type = EcoreFactory.eINSTANCE.createEClass();
            level2Type.setName("Level2Type");

            context.setCurrentTypeHint(rootType);
            CodecJsonReadContext level1 = context.createChildObjectContext(1, 1);
            level1.setCurrentTypeHint(level1Type);
            CodecJsonReadContext level2 = level1.createChildObjectContext(2, 1);
            level2.setCurrentTypeHint(level2Type);

            // All type hints are independent
            assertSame(rootType, context.getCurrentTypeHint());
            assertSame(level1Type, level1.getCurrentTypeHint());
            assertSame(level2Type, level2.getCurrentTypeHint());
        }

        @Test
        @DisplayName("type hint survives navigation back to parent")
        void typeHintSurvivesNavigationToParent() {
            context.setCurrentTypeHint(parentTypeHint);
            CodecJsonReadContext child = context.createChildObjectContext(1, 1);
            child.setCurrentTypeHint(childTypeHint);

            // Navigate back to parent
            CodecJsonReadContext navigatedParent = child.clearAndGetParent();

            assertSame(context, navigatedParent);
            assertSame(parentTypeHint, navigatedParent.getCurrentTypeHint(),
                    "Parent type hint should be preserved after child navigation");
        }
    }

    @Nested
    @DisplayName("Type hint on context reuse")
    class TypeHintOnContextReuse {

        @Test
        @DisplayName("type hint is cleared when child context is reused")
        void typeHintClearedOnReuse() {
            // First usage
            CodecJsonReadContext child1 = context.createChildObjectContext(1, 1);
            child1.setCurrentTypeHint(childTypeHint);
            assertSame(childTypeHint, child1.getCurrentTypeHint());

            // Second usage - Jackson may reuse the same context instance
            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);

            // The type hint should be cleared (null) for the reused context
            assertNull(child2.getCurrentTypeHint(),
                    "Type hint should be cleared when context is reused");
        }

        @Test
        @DisplayName("EMF state is cleared when child context is reused")
        void emfStateClearedOnReuse() {
            // First usage - set some EMF state
            CodecJsonReadContext child1 = context.createChildObjectContext(1, 1);
            child1.setCurrentTypeHint(childTypeHint);
            child1.setCurrentEObject(mock(org.eclipse.emf.ecore.EObject.class));
            child1.setCurrentFeature(mock(org.eclipse.emf.ecore.EStructuralFeature.class));

            // Second usage - context is reused
            CodecJsonReadContext child2 = context.createChildObjectContext(2, 1);

            // All EMF state should be cleared
            assertNull(child2.getCurrentTypeHint());
            assertNull(child2.getCurrentEObject());
            assertNull(child2.getCurrentFeature());
        }
    }

    @Nested
    @DisplayName("Type hint with array contexts")
    class TypeHintWithArrayContexts {

        @Test
        @DisplayName("array context reuses child - streaming pattern")
        void arrayContextReusesChild_streamingPattern() {
            // Jackson pools child contexts for performance
            // This tests the streaming deserialization pattern:
            // 1. Enter array element, set type hint, process, exit
            // 2. Enter next element - context is reset, type hint is null
            CodecJsonReadContext arrayCtx = context.createChildArrayContext(1, 1);
            arrayCtx.setCurrentTypeHint(parentTypeHint);

            // First array element - process completely
            CodecJsonReadContext element1 = arrayCtx.createChildObjectContext(2, 1);
            element1.setCurrentTypeHint(childTypeHint);
            assertSame(childTypeHint, element1.getCurrentTypeHint());
            // ... would process element1 here ...

            // Second array element - Jackson reuses the same context instance (reset)
            CodecJsonReadContext element2 = arrayCtx.createChildObjectContext(3, 1);

            // The context is reset, so type hint is null
            // This is correct for streaming: we're done with element1
            assertNull(element2.getCurrentTypeHint(),
                    "Reused context should have null type hint (streaming pattern)");

            // Array context's type hint is unaffected
            assertSame(parentTypeHint, arrayCtx.getCurrentTypeHint());
        }
    }
}
