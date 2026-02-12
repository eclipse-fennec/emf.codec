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
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SerializationState}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#6-eobject-serialization">Spec 6: EObject Serialization</a>
 */
@DisplayName("SerializationState")
class SerializationStateTest {

    private EClass testEClass;
    private EAttribute nameAttribute;
    private EAttribute ageAttribute;
    private EObject eObject;

    @BeforeEach
    void setUp() {
        // Create a test EClass with attributes
        testEClass = EcoreFactory.eINSTANCE.createEClass();
        testEClass.setName("TestClass");

        nameAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttribute.setName("name");
        nameAttribute.setEType(EcorePackage.Literals.ESTRING);
        testEClass.getEStructuralFeatures().add(nameAttribute);

        ageAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        ageAttribute.setName("age");
        ageAttribute.setEType(EcorePackage.Literals.EINT);
        testEClass.getEStructuralFeatures().add(ageAttribute);

        eObject = mock(EObject.class);
        when(eObject.eClass()).thenReturn(testEClass);
    }

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("creates state with EObject")
        void createsStateWithEObject() {
            SerializationState state = new SerializationState(eObject);

            assertSame(eObject, state.getEObject());
        }

        @Test
        @DisplayName("throws NullPointerException for null EObject")
        void throwsForNullEObject() {
            assertThrows(NullPointerException.class, () -> new SerializationState(null));
        }

        @Test
        @DisplayName("initializes with empty cache")
        void initializesWithEmptyCache() {
            SerializationState state = new SerializationState(eObject);

            assertEquals(0, state.getCacheSize());
        }
    }

    @Nested
    @DisplayName("getValue")
    class GetValue {

        @Test
        @DisplayName("returns value from eGet on first call")
        void returnsValueFromEGetOnFirstCall() {
            when(eObject.eGet(nameAttribute)).thenReturn("John");

            SerializationState state = new SerializationState(eObject);
            Object value = state.getValue(nameAttribute);

            assertEquals("John", value);
            verify(eObject, times(1)).eGet(nameAttribute);
        }

        @Test
        @DisplayName("returns cached value on subsequent calls")
        void returnsCachedValueOnSubsequentCalls() {
            when(eObject.eGet(nameAttribute)).thenReturn("John");

            SerializationState state = new SerializationState(eObject);

            // First call
            Object value1 = state.getValue(nameAttribute);
            // Second call
            Object value2 = state.getValue(nameAttribute);
            // Third call
            Object value3 = state.getValue(nameAttribute);

            assertEquals("John", value1);
            assertEquals("John", value2);
            assertEquals("John", value3);

            // eGet should only be called once
            verify(eObject, times(1)).eGet(nameAttribute);
        }

        @Test
        @DisplayName("caches null values correctly")
        void cachesNullValuesCorrectly() {
            when(eObject.eGet(nameAttribute)).thenReturn(null);

            SerializationState state = new SerializationState(eObject);

            // First call
            Object value1 = state.getValue(nameAttribute);
            // Second call
            Object value2 = state.getValue(nameAttribute);

            assertNull(value1);
            assertNull(value2);

            // eGet should only be called once even for null
            verify(eObject, times(1)).eGet(nameAttribute);
        }

        @Test
        @DisplayName("caches different features separately")
        void cachesDifferentFeaturesSeparately() {
            when(eObject.eGet(nameAttribute)).thenReturn("John");
            when(eObject.eGet(ageAttribute)).thenReturn(30);

            SerializationState state = new SerializationState(eObject);

            assertEquals("John", state.getValue(nameAttribute));
            assertEquals(30, state.getValue(ageAttribute));

            // Call again to verify caching
            assertEquals("John", state.getValue(nameAttribute));
            assertEquals(30, state.getValue(ageAttribute));

            // Each feature should be retrieved only once
            verify(eObject, times(1)).eGet(nameAttribute);
            verify(eObject, times(1)).eGet(ageAttribute);
        }

        @Test
        @DisplayName("throws NullPointerException for null feature")
        void throwsForNullFeature() {
            SerializationState state = new SerializationState(eObject);

            assertThrows(NullPointerException.class, () -> state.getValue(null));
        }
    }

    @Nested
    @DisplayName("isCached")
    class IsCached {

        @Test
        @DisplayName("returns false for uncached feature")
        void returnsFalseForUncachedFeature() {
            SerializationState state = new SerializationState(eObject);

            assertFalse(state.isCached(nameAttribute));
        }

        @Test
        @DisplayName("returns true after getValue")
        void returnsTrueAfterGetValue() {
            when(eObject.eGet(nameAttribute)).thenReturn("John");

            SerializationState state = new SerializationState(eObject);
            state.getValue(nameAttribute);

            assertTrue(state.isCached(nameAttribute));
        }

        @Test
        @DisplayName("returns true for cached null value")
        void returnsTrueForCachedNullValue() {
            when(eObject.eGet(nameAttribute)).thenReturn(null);

            SerializationState state = new SerializationState(eObject);
            state.getValue(nameAttribute);

            assertTrue(state.isCached(nameAttribute));
        }
    }

    @Nested
    @DisplayName("clearCache")
    class ClearCache {

        @Test
        @DisplayName("clears all cached values")
        void clearsAllCachedValues() {
            when(eObject.eGet(nameAttribute)).thenReturn("John");
            when(eObject.eGet(ageAttribute)).thenReturn(30);

            SerializationState state = new SerializationState(eObject);
            state.getValue(nameAttribute);
            state.getValue(ageAttribute);

            assertEquals(2, state.getCacheSize());

            state.clearCache();

            assertEquals(0, state.getCacheSize());
            assertFalse(state.isCached(nameAttribute));
            assertFalse(state.isCached(ageAttribute));
        }

        @Test
        @DisplayName("allows re-fetching values after clear")
        void allowsReFetchingAfterClear() {
            when(eObject.eGet(nameAttribute)).thenReturn("John").thenReturn("Jane");

            SerializationState state = new SerializationState(eObject);

            assertEquals("John", state.getValue(nameAttribute));

            state.clearCache();

            assertEquals("Jane", state.getValue(nameAttribute));

            verify(eObject, times(2)).eGet(nameAttribute);
        }
    }

    @Nested
    @DisplayName("getCacheSize")
    class GetCacheSize {

        @Test
        @DisplayName("returns correct size")
        void returnsCorrectSize() {
            when(eObject.eGet(nameAttribute)).thenReturn("John");
            when(eObject.eGet(ageAttribute)).thenReturn(30);

            SerializationState state = new SerializationState(eObject);

            assertEquals(0, state.getCacheSize());

            state.getValue(nameAttribute);
            assertEquals(1, state.getCacheSize());

            state.getValue(ageAttribute);
            assertEquals(2, state.getCacheSize());

            // Getting the same value again should not increase size
            state.getValue(nameAttribute);
            assertEquals(2, state.getCacheSize());
        }
    }

    @Nested
    @DisplayName("getEObject")
    class GetEObject {

        @Test
        @DisplayName("returns the EObject")
        void returnsTheEObject() {
            SerializationState state = new SerializationState(eObject);

            assertSame(eObject, state.getEObject());
        }
    }
}
