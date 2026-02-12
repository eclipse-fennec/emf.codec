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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DeserializationState}.
 */
@DisplayName("DeserializationState")
class DeserializationStateTest extends DeserializationEntryTestBase {

    @Nested
    @DisplayName("Root state creation")
    class RootStateCreation {

        @Test
        @DisplayName("creates root state with null resource")
        void createsRootStateWithNullResource() {
            DeserializationState state = new DeserializationState(null);

            assertTrue(state.isRootObject());
            assertNull(state.getResource());
            assertNull(state.getParent());
            assertNull(state.getContainingFeature());
            assertNull(state.getEObject());
            assertNull(state.getResolvedEClass());
            assertNotNull(state.getUnresolvedReferences());
            assertTrue(state.getUnresolvedReferences().isEmpty());
        }
    }

    @Nested
    @DisplayName("Nested state creation")
    class NestedStateCreation {

        @Test
        @DisplayName("creates nested state with parent and containing feature")
        void createsNestedStateWithParentAndContainingFeature() {
            DeserializationState parent = new DeserializationState(null);
            DeserializationState child = new DeserializationState(parent, addressRef);

            assertFalse(child.isRootObject());
            assertSame(parent, child.getParent());
            assertSame(addressRef, child.getContainingFeature());
        }

        @Test
        @DisplayName("shares unresolved references with parent")
        void sharesUnresolvedReferencesWithParent() {
            DeserializationState parent = new DeserializationState(null);
            DeserializationState child = new DeserializationState(parent, addressRef);

            assertSame(parent.getUnresolvedReferences(), child.getUnresolvedReferences());
        }

        @Test
        @DisplayName("throws when parent is null")
        void throwsWhenParentIsNull() {
            assertThrows(NullPointerException.class, () ->
                new DeserializationState(null, addressRef));
        }

        @Test
        @DisplayName("throws when containing feature is null")
        void throwsWhenContainingFeatureIsNull() {
            DeserializationState parent = new DeserializationState(null);
            assertThrows(NullPointerException.class, () ->
                new DeserializationState(parent, null));
        }
    }

    @Nested
    @DisplayName("EClass resolution")
    class EClassResolution {

        @Test
        @DisplayName("sets and gets resolved EClass")
        void setsAndGetsResolvedEClass() {
            DeserializationState state = new DeserializationState(null);

            state.setResolvedEClass(personClass);

            assertEquals(personClass, state.getResolvedEClass());
        }
    }

    @Nested
    @DisplayName("EObject creation")
    class EObjectCreation {

        @Test
        @DisplayName("creates EObject from resolved EClass")
        void createsEObjectFromResolvedEClass() {
            DeserializationState state = new DeserializationState(null);
            state.setResolvedEClass(personClass);

            EObject created = state.createEObject();

            assertNotNull(created);
            assertEquals(personClass, created.eClass());
            assertSame(created, state.getEObject());
        }

        @Test
        @DisplayName("throws when EClass not resolved")
        void throwsWhenEClassNotResolved() {
            DeserializationState state = new DeserializationState(null);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> state.createEObject());
            assertTrue(ex.getMessage().contains("EClass not resolved"));
        }

        @Test
        @DisplayName("sets EObject directly")
        void setsEObjectDirectly() {
            DeserializationState state = new DeserializationState(null);
            EObject person = createPerson();

            state.setEObject(person);

            assertSame(person, state.getEObject());
        }
    }

    @Nested
    @DisplayName("Feature value setting")
    class FeatureValueSetting {

        @Test
        @DisplayName("sets single-valued feature")
        void setsSingleValuedFeature() {
            DeserializationState state = createStateWithObject(createPerson());

            state.setFeatureValue(nameAttribute, "John");

            assertEquals("John", state.getEObject().eGet(nameAttribute));
        }

        @Test
        @DisplayName("sets integer feature")
        void setsIntegerFeature() {
            DeserializationState state = createStateWithObject(createPerson());

            state.setFeatureValue(ageAttribute, 30);

            assertEquals(30, state.getEObject().eGet(ageAttribute));
        }

        @Test
        @DisplayName("throws when EObject not created")
        void throwsWhenEObjectNotCreated() {
            DeserializationState state = new DeserializationState(null);
            state.setResolvedEClass(personClass);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> state.setFeatureValue(nameAttribute, "John"));
            assertTrue(ex.getMessage().contains("EObject not created"));
        }
    }

    @Nested
    @DisplayName("Multi-valued feature operations")
    class MultiValuedFeatureOperations {

        @Test
        @DisplayName("adds value to multi-valued attribute")
        void addsValueToMultiValuedAttribute() {
            DeserializationState state = createStateWithObject(createPerson());

            state.addFeatureValue(tagsAttribute, "tag1");
            state.addFeatureValue(tagsAttribute, "tag2");

            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) state.getEObject().eGet(tagsAttribute);
            assertEquals(2, tags.size());
            assertEquals("tag1", tags.get(0));
            assertEquals("tag2", tags.get(1));
        }

        @Test
        @DisplayName("throws when adding value without EObject")
        void throwsWhenAddingValueWithoutEObject() {
            DeserializationState state = new DeserializationState(null);
            state.setResolvedEClass(personClass);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> state.addFeatureValue(tagsAttribute, "tag1"));
            assertTrue(ex.getMessage().contains("EObject not created"));
        }
    }

    @Nested
    @DisplayName("Unresolved references")
    class UnresolvedReferences {

        @Test
        @DisplayName("adds unresolved reference")
        void addsUnresolvedReference() {
            DeserializationState state = createStateWithObject(createPerson());
            DeserializationState.UnresolvedReference ref =
                new DeserializationState.UnresolvedReference(
                    state.getEObject(), managerRef, "#/persons/1", -1);

            state.addUnresolvedReference(ref);

            assertEquals(1, state.getUnresolvedReferences().size());
            assertSame(ref, state.getUnresolvedReferences().get(0));
        }

        @Test
        @DisplayName("unresolved reference captures source, reference, and target URI")
        void unresolvedReferenceCapturesData() {
            EObject person = createPerson();
            DeserializationState.UnresolvedReference ref =
                new DeserializationState.UnresolvedReference(
                    person, managerRef, "#/persons/1", -1);

            assertSame(person, ref.getSource());
            assertSame(managerRef, ref.getReference());
            assertEquals("#/persons/1", ref.getTargetUri());
            assertEquals(-1, ref.getIndex());
            assertFalse(ref.isMultiValued());
        }

        @Test
        @DisplayName("multi-valued unresolved reference has index")
        void multiValuedUnresolvedReferenceHasIndex() {
            EObject person = createPerson();
            DeserializationState.UnresolvedReference ref =
                new DeserializationState.UnresolvedReference(
                    person, colleaguesRef, "#/persons/2", 0);

            assertEquals(0, ref.getIndex());
            assertTrue(ref.isMultiValued());
        }
    }
}
