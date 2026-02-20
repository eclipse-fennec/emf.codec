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
package org.eclipse.fennec.codec.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EMapHelper}.
 */
@DisplayName("EMapHelper")
class EMapHelperTest {

    private static final String TEST_EMAP_ECORE = "/org/eclipse/fennec/codec/resource/test-emap.ecore";
    private static final String TEST_SERIALIZATION_ECORE = "../ser/test-serialization.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage emapPackage;
    private EPackage serPackage;

    // EMap model classes
    private EClass containerClass;
    private EClass itemEntryClass;
    private EClass stringEntryClass;
    private EClass itemClass;

    // EMap references
    private EReference itemsRef;
    private EReference metadataRef;

    // Non-map model classes (from test-serialization.ecore)
    private EClass personClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();

        // Load EMap test model
        emapPackage = ecoreHelper.loadEcore(TEST_EMAP_ECORE, EMapHelperTest.class);
        containerClass = EcoreHelper.getEClass(emapPackage, "Container");
        itemEntryClass = EcoreHelper.getEClass(emapPackage, "ItemEntry");
        stringEntryClass = EcoreHelper.getEClass(emapPackage, "StringEntry");
        itemClass = EcoreHelper.getEClass(emapPackage, "Item");

        itemsRef = (EReference) EcoreHelper.getFeature(containerClass, "items");
        metadataRef = (EReference) EcoreHelper.getFeature(containerClass, "metadata");

        // Load non-map model for negative tests
        serPackage = ecoreHelper.loadEcore(TEST_SERIALIZATION_ECORE, EMapHelperTest.class);
        personClass = EcoreHelper.getEClass(serPackage, "Person");
    }

    @AfterEach
    void tearDown() {
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // isMapEntryReference
    // ========================================================================

    @Nested
    @DisplayName("isMapEntryReference")
    class IsMapEntryReference {

        @Test
        @DisplayName("returns true for EMap reference with object values (ItemEntry)")
        void returnsTrueForObjectMapReference() {
            assertTrue(EMapHelper.isMapEntryReference(itemsRef));
        }

        @Test
        @DisplayName("returns true for EMap reference with string values (StringEntry)")
        void returnsTrueForStringMapReference() {
            assertTrue(EMapHelper.isMapEntryReference(metadataRef));
        }

        @Test
        @DisplayName("returns false for non-map containment reference")
        void returnsFalseForNonMapContainment() {
            // Person has containment references that are NOT EMap entries
            for (EReference ref : personClass.getEAllReferences()) {
                if (ref.isContainment()) {
                    assertFalse(EMapHelper.isMapEntryReference(ref),
                            "Non-map containment reference '" + ref.getName() + "' should not be detected as EMap");
                }
            }
        }

        @Test
        @DisplayName("returns true even for non-containment reference to Map.Entry type")
        void returnsTrueForNonContainmentMapEntryRef() {
            // Create a non-containment reference to a Map.Entry type
            EClass holder = EcoreFactory.eINSTANCE.createEClass();
            holder.setName("Holder");

            EReference nonContainmentRef = EcoreFactory.eINSTANCE.createEReference();
            nonContainmentRef.setName("entryRef");
            nonContainmentRef.setEType(itemEntryClass);
            nonContainmentRef.setContainment(false);
            holder.getEStructuralFeatures().add(nonContainmentRef);

            // isMapEntryReference checks the type, not containment
            assertTrue(EMapHelper.isMapEntryReference(nonContainmentRef));
        }

        @Test
        @DisplayName("returns false for null reference")
        void returnsFalseForNull() {
            assertFalse(EMapHelper.isMapEntryReference(null));
        }
    }

    // ========================================================================
    // isMapEntryClass
    // ========================================================================

    @Nested
    @DisplayName("isMapEntryClass")
    class IsMapEntryClass {

        @Test
        @DisplayName("returns true for class with Map$Entry instanceClassName")
        void returnsTrueForMapEntryInstanceClassName() {
            assertTrue(EMapHelper.isMapEntryClass(itemEntryClass));
            assertEquals("java.util.Map$Entry", itemEntryClass.getInstanceClassName());
        }

        @Test
        @DisplayName("returns true for StringEntry with Map$Entry instanceClassName")
        void returnsTrueForStringEntry() {
            assertTrue(EMapHelper.isMapEntryClass(stringEntryClass));
        }

        @Test
        @DisplayName("returns false for regular EClass")
        void returnsFalseForRegularClass() {
            assertFalse(EMapHelper.isMapEntryClass(personClass));
        }

        @Test
        @DisplayName("returns false for Item class (has 'value' but no 'key')")
        void returnsFalseForItemClass() {
            // Item has 'value' but not 'key', so should NOT be a map entry
            assertNotNull(itemClass.getEStructuralFeature("value"));
            assertNull(itemClass.getEStructuralFeature("key"));
            assertFalse(EMapHelper.isMapEntryClass(itemClass));
        }

        @Test
        @DisplayName("returns true for class with key and value features but no instanceClassName (fallback)")
        void returnsTrueForFallbackDetection() {
            // Create a dynamic EClass with "key" and "value" but no instanceClassName
            EClass dynamicEntry = EcoreFactory.eINSTANCE.createEClass();
            dynamicEntry.setName("DynamicEntry");

            EAttribute keyAttr = EcoreFactory.eINSTANCE.createEAttribute();
            keyAttr.setName("key");
            keyAttr.setEType(EcorePackage.Literals.ESTRING);
            dynamicEntry.getEStructuralFeatures().add(keyAttr);

            EAttribute valueAttr = EcoreFactory.eINSTANCE.createEAttribute();
            valueAttr.setName("value");
            valueAttr.setEType(EcorePackage.Literals.ESTRING);
            dynamicEntry.getEStructuralFeatures().add(valueAttr);

            // No instanceClassName set — fallback should detect key+value
            assertNull(dynamicEntry.getInstanceClassName());
            assertTrue(EMapHelper.isMapEntryClass(dynamicEntry),
                    "Should detect map entry class via key+value fallback");
        }

        @Test
        @DisplayName("returns false for class with only 'key' feature (no 'value')")
        void returnsFalseForClassWithOnlyKey() {
            EClass onlyKey = EcoreFactory.eINSTANCE.createEClass();
            onlyKey.setName("OnlyKey");

            EAttribute keyAttr = EcoreFactory.eINSTANCE.createEAttribute();
            keyAttr.setName("key");
            keyAttr.setEType(EcorePackage.Literals.ESTRING);
            onlyKey.getEStructuralFeatures().add(keyAttr);

            assertFalse(EMapHelper.isMapEntryClass(onlyKey));
        }

        @Test
        @DisplayName("returns false for class with only 'value' feature (no 'key')")
        void returnsFalseForClassWithOnlyValue() {
            EClass onlyValue = EcoreFactory.eINSTANCE.createEClass();
            onlyValue.setName("OnlyValue");

            EAttribute valueAttr = EcoreFactory.eINSTANCE.createEAttribute();
            valueAttr.setName("value");
            valueAttr.setEType(EcorePackage.Literals.ESTRING);
            onlyValue.getEStructuralFeatures().add(valueAttr);

            assertFalse(EMapHelper.isMapEntryClass(onlyValue));
        }

        @Test
        @DisplayName("returns false for null class")
        void returnsFalseForNull() {
            assertFalse(EMapHelper.isMapEntryClass(null));
        }
    }

    // ========================================================================
    // getKeyFeature / getValueFeature
    // ========================================================================

    @Nested
    @DisplayName("getKeyFeature")
    class GetKeyFeature {

        @Test
        @DisplayName("returns key feature from ItemEntry")
        void returnsKeyFeatureFromItemEntry() {
            EStructuralFeature keyFeature = EMapHelper.getKeyFeature(itemEntryClass);
            assertNotNull(keyFeature);
            assertEquals("key", keyFeature.getName());
        }

        @Test
        @DisplayName("returns key feature from StringEntry")
        void returnsKeyFeatureFromStringEntry() {
            EStructuralFeature keyFeature = EMapHelper.getKeyFeature(stringEntryClass);
            assertNotNull(keyFeature);
            assertEquals("key", keyFeature.getName());
        }

        @Test
        @DisplayName("returns null for class without key feature")
        void returnsNullForClassWithoutKey() {
            assertNull(EMapHelper.getKeyFeature(personClass));
        }

        @Test
        @DisplayName("returns null for null class")
        void returnsNullForNull() {
            assertNull(EMapHelper.getKeyFeature(null));
        }
    }

    @Nested
    @DisplayName("getValueFeature")
    class GetValueFeature {

        @Test
        @DisplayName("returns value feature from ItemEntry (EReference)")
        void returnsValueFeatureFromItemEntry() {
            EStructuralFeature valueFeature = EMapHelper.getValueFeature(itemEntryClass);
            assertNotNull(valueFeature);
            assertEquals("value", valueFeature.getName());
            assertTrue(valueFeature instanceof EReference, "ItemEntry value should be an EReference");
        }

        @Test
        @DisplayName("returns value feature from StringEntry (EAttribute)")
        void returnsValueFeatureFromStringEntry() {
            EStructuralFeature valueFeature = EMapHelper.getValueFeature(stringEntryClass);
            assertNotNull(valueFeature);
            assertEquals("value", valueFeature.getName());
            assertTrue(valueFeature instanceof org.eclipse.emf.ecore.EAttribute,
                    "StringEntry value should be an EAttribute");
        }

        @Test
        @DisplayName("returns null for class without value feature")
        void returnsNullForClassWithoutValue() {
            assertNull(EMapHelper.getValueFeature(personClass));
        }

        @Test
        @DisplayName("returns null for null class")
        void returnsNullForNull() {
            assertNull(EMapHelper.getValueFeature(null));
        }
    }

    // ========================================================================
    // MAP_ENTRY_CLASS_NAME constant
    // ========================================================================

    @Nested
    @DisplayName("Constants")
    class Constants {

        @Test
        @DisplayName("MAP_ENTRY_CLASS_NAME has correct value")
        void mapEntryClassNameHasCorrectValue() {
            assertEquals("java.util.Map$Entry", EMapHelper.MAP_ENTRY_CLASS_NAME);
        }
    }
}
