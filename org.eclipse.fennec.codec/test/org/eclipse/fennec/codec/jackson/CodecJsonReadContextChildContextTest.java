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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecJsonReadContext} child context creation and parent traversal.
 * <p>
 * Verifies:
 * <ul>
 *   <li>Child object/array context creation</li>
 *   <li>Parent-child relationships</li>
 *   <li>Nesting depth tracking</li>
 *   <li>clearAndGetParent navigation</li>
 * </ul>
 * </p>
 */
@DisplayName("CodecJsonReadContext Child Context Tests")
class CodecJsonReadContextChildContextTest extends CodecJsonReadContextTestBase {

    @Nested
    @DisplayName("createChildObjectContext")
    class CreateChildObjectContext {

        @Test
        @DisplayName("creates child object context")
        void createsChildObjectContext() {
            CodecJsonReadContext child = context.createChildObjectContext(1, 1);

            assertNotNull(child);
            assertTrue(child.inObject());
            assertFalse(child.inArray());
            assertFalse(child.inRoot());
        }

        @Test
        @DisplayName("child has correct parent reference")
        void childHasParent() {
            CodecJsonReadContext child = context.createChildObjectContext(1, 1);

            assertSame(context, child.getParent());
            assertTrue(child.hasParentContext());
        }

        @Test
        @DisplayName("child inherits metadata service from parent")
        void childInheritsMetadataService() {
            CodecJsonReadContext child = context.createChildObjectContext(1, 1);

            assertSame(metadataService, child.getMetadataService());
        }

        @Test
        @DisplayName("child has increased nesting depth")
        void childHasIncreasedNestingDepth() {
            CodecJsonReadContext child = context.createChildObjectContext(1, 1);

            assertEquals(context.getNestingDepth() + 1, child.getNestingDepth());
        }
    }

    @Nested
    @DisplayName("createChildArrayContext")
    class CreateChildArrayContext {

        @Test
        @DisplayName("creates child array context")
        void createsChildArrayContext() {
            CodecJsonReadContext child = context.createChildArrayContext(1, 1);

            assertNotNull(child);
            assertTrue(child.inArray());
            assertFalse(child.inObject());
            assertFalse(child.inRoot());
        }

        @Test
        @DisplayName("child has correct parent reference")
        void childHasParent() {
            CodecJsonReadContext child = context.createChildArrayContext(1, 1);

            assertSame(context, child.getParent());
            assertTrue(child.hasParentContext());
        }

        @Test
        @DisplayName("child inherits metadata service from parent")
        void childInheritsMetadataService() {
            CodecJsonReadContext child = context.createChildArrayContext(1, 1);

            assertSame(metadataService, child.getMetadataService());
        }
    }

    @Nested
    @DisplayName("Multi-level nesting")
    class MultiLevelNesting {

        @Test
        @DisplayName("supports deep nesting: root -> object -> array -> object")
        void supportsDeepNesting() {
            CodecJsonReadContext level1 = context.createChildObjectContext(1, 1);
            CodecJsonReadContext level2 = level1.createChildArrayContext(2, 1);
            CodecJsonReadContext level3 = level2.createChildObjectContext(3, 1);

            // Verify nesting depths
            assertEquals(0, context.getNestingDepth());
            assertEquals(1, level1.getNestingDepth());
            assertEquals(2, level2.getNestingDepth());
            assertEquals(3, level3.getNestingDepth());

            // Verify parent chain
            assertSame(level2, level3.getParent());
            assertSame(level1, level2.getParent());
            assertSame(context, level1.getParent());
            assertNull(context.getParent());
        }

        @Test
        @DisplayName("each level inherits metadata service")
        void eachLevelInheritsMetadataService() {
            CodecJsonReadContext level1 = context.createChildObjectContext(1, 1);
            CodecJsonReadContext level2 = level1.createChildArrayContext(2, 1);
            CodecJsonReadContext level3 = level2.createChildObjectContext(3, 1);

            assertSame(metadataService, level1.getMetadataService());
            assertSame(metadataService, level2.getMetadataService());
            assertSame(metadataService, level3.getMetadataService());
        }
    }

    @Nested
    @DisplayName("clearAndGetParent navigation")
    class ClearAndGetParent {

        @Test
        @DisplayName("returns parent context")
        void returnsParentContext() {
            CodecJsonReadContext child = context.createChildObjectContext(1, 1);

            CodecJsonReadContext parent = child.clearAndGetParent();

            assertSame(context, parent);
        }

        @Test
        @DisplayName("clears current value")
        void clearsCurrentValue() {
            CodecJsonReadContext child = context.createChildObjectContext(1, 1);
            child.assignCurrentValue("test-value");

            child.clearAndGetParent();

            assertNull(child.currentValue());
        }

        @Test
        @DisplayName("returns null for root context")
        void returnsNullForRoot() {
            CodecJsonReadContext parent = context.clearAndGetParent();

            assertNull(parent);
        }

        @Test
        @DisplayName("allows navigating back through multiple levels")
        void navigatesBackThroughMultipleLevels() {
            CodecJsonReadContext level1 = context.createChildObjectContext(1, 1);
            CodecJsonReadContext level2 = level1.createChildArrayContext(2, 1);
            CodecJsonReadContext level3 = level2.createChildObjectContext(3, 1);

            // Navigate back
            assertSame(level2, level3.clearAndGetParent());
            assertSame(level1, level2.clearAndGetParent());
            assertSame(context, level1.clearAndGetParent());
            assertNull(context.clearAndGetParent());
        }
    }

    @Nested
    @DisplayName("Root context properties")
    class RootContextProperties {

        @Test
        @DisplayName("root context has no parent")
        void rootHasNoParent() {
            assertNull(context.getParent());
            assertFalse(context.hasParentContext());
        }

        @Test
        @DisplayName("root context is in root state")
        void rootIsInRootState() {
            assertTrue(context.inRoot());
            assertFalse(context.inObject());
            assertFalse(context.inArray());
        }

        @Test
        @DisplayName("root context has nesting depth 0")
        void rootHasNestingDepthZero() {
            assertEquals(0, context.getNestingDepth());
        }
    }
}
