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
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for smart compression behavior.
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.CodecResourceSmartCompressionTest}.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/04-global-options.md#1-smart-compression">Spec: Smart Compression</a>
 */
@DisplayName("CodecResource Smart Compression Tests")
class CodecResourceSmartCompressionTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-smart-compression.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass dogClass;
    private EClass catClass;
    private EClass ownerClass;
    private EClass kennelClass;

    // EReferences
    private EReference ownerPetRef;
    private EReference ownerPetsRef;
    private EReference kennelDogRef;
    private EReference kennelDogsRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(CodecResourceSmartCompressionTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load EClasses
        dogClass = ecoreHelper.getEClass(testPackage, "Dog");
        catClass = ecoreHelper.getEClass(testPackage, "Cat");
        ownerClass = ecoreHelper.getEClass(testPackage, "Owner");
        kennelClass = ecoreHelper.getEClass(testPackage, "Kennel");

        // Load EReferences
        ownerPetRef = (EReference) ownerClass.getEStructuralFeature("pet");
        ownerPetsRef = (EReference) ownerClass.getEStructuralFeature("pets");
        kennelDogRef = (EReference) kennelClass.getEStructuralFeature("dog");
        kennelDogsRef = (EReference) kennelClass.getEStructuralFeature("dogs");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Factory Methods
    // ========================================================================

    private EObject createDog(String name, String breed) {
        EObject dog = testPackage.getEFactoryInstance().create(dogClass);
        dog.eSet(dogClass.getEStructuralFeature("name"), name);
        dog.eSet(dogClass.getEStructuralFeature("breed"), breed);
        return dog;
    }

    private EObject createCat(String name, boolean indoor) {
        EObject cat = testPackage.getEFactoryInstance().create(catClass);
        cat.eSet(catClass.getEStructuralFeature("name"), name);
        cat.eSet(catClass.getEStructuralFeature("indoor"), indoor);
        return cat;
    }

    private EObject createOwner(String name) {
        EObject owner = testPackage.getEFactoryInstance().create(ownerClass);
        owner.eSet(ownerClass.getEStructuralFeature("name"), name);
        return owner;
    }

    private EObject createKennel(String name) {
        EObject kennel = testPackage.getEFactoryInstance().create(kennelClass);
        kennel.eSet(kennelClass.getEStructuralFeature("name"), name);
        return kennel;
    }

    private CodecResource createResource(boolean smartCompression) {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(Map.of("smartCompression", smartCompression))
                .build();
        return new CodecResource(
                URI.createURI("test://smartcompression.json"),
                metadataService,
                resolver,
                null);
    }

    private String serialize(EObject object, boolean smartCompression) throws IOException {
        CodecResource resource = createResource(smartCompression);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());

        return out.toString(StandardCharsets.UTF_8);
    }

    // ========================================================================
    // Smart Compression OFF Tests - _type always written
    // ========================================================================

    @Nested
    @DisplayName("Smart Compression OFF")
    class SmartCompressionOff {

        @Test
        @DisplayName("same-type reference writes _type")
        void sameType_smartCompressionOff_writesType() throws IOException {
            EObject kennel = createKennel("Happy Paws");
            EObject dog = createDog("Buddy", "Golden Retriever");
            kennel.eSet(kennelDogRef, dog);

            String json = serialize(kennel, false);
            System.out.println("Kennel with Dog (smart compression OFF):\n" + json);

            assertTrue(json.contains("\"_type\""),
                    "JSON should contain _type field when smart compression is OFF");
            assertTrue(json.contains("\"name\":\"Happy Paws\""),
                    "JSON should contain kennel name");
            assertTrue(json.contains("\"breed\":\"Golden Retriever\""),
                    "JSON should contain dog breed");
        }

        @Test
        @DisplayName("same-type list writes _type for each element")
        void sameType_list_smartCompressionOff_writesType() throws IOException {
            EObject kennel = createKennel("Happy Paws");
            EObject dog1 = createDog("Buddy", "Golden Retriever");
            EObject dog2 = createDog("Max", "German Shepherd");

            @SuppressWarnings("unchecked")
            EList<EObject> dogs = (EList<EObject>) kennel.eGet(kennelDogsRef);
            dogs.add(dog1);
            dogs.add(dog2);

            String json = serialize(kennel, false);
            System.out.println("Kennel with Dogs list (smart compression OFF):\n" + json);

            int typeCount = countOccurrences(json, "\"_type\"");
            assertTrue(typeCount >= 2,
                    "JSON should contain _type for each Dog when smart compression is OFF, found: " + typeCount);
        }

        @Test
        @DisplayName("polymorphic reference writes _type")
        void polymorphic_smartCompressionOff_writesType() throws IOException {
            EObject owner = createOwner("John");
            EObject dog = createDog("Buddy", "Golden Retriever");
            owner.eSet(ownerPetRef, dog);

            String json = serialize(owner, false);
            System.out.println("Owner with Dog pet (smart compression OFF):\n" + json);

            assertTrue(json.contains("\"_type\""),
                    "JSON should contain _type field for polymorphic reference");
        }
    }

    // ========================================================================
    // Smart Compression ON - Same Schema uses simple names
    // ========================================================================

    @Nested
    @DisplayName("Smart Compression ON - Same Schema")
    class SmartCompressionOnSameSchema {

        @Test
        @DisplayName("same-schema contained object uses simple type name")
        void sameSchema_smartCompressionOn_usesSimpleName() throws IOException {
            EObject kennel = createKennel("Happy Paws");
            EObject dog = createDog("Buddy", "Golden Retriever");
            kennel.eSet(kennelDogRef, dog);

            String json = serialize(kennel, true);
            System.out.println("Kennel with Dog (smart compression ON):\n" + json);

            String nsUri = testPackage.getNsURI();
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Kennel\""),
                    "Root Kennel should use full URI");
            assertTrue(json.contains("\"_type\":\"Dog\""),
                    "Contained Dog should use simple name 'Dog'");
            assertFalse(json.contains("\"_type\":\"" + nsUri + "#//Dog\""),
                    "Contained Dog should NOT use full URI");
        }

        @Test
        @DisplayName("same-schema list elements use simple type names")
        void sameSchema_list_smartCompressionOn_usesSimpleNames() throws IOException {
            EObject kennel = createKennel("Happy Paws");
            EObject dog1 = createDog("Buddy", "Golden Retriever");
            EObject dog2 = createDog("Max", "German Shepherd");

            @SuppressWarnings("unchecked")
            EList<EObject> dogs = (EList<EObject>) kennel.eGet(kennelDogsRef);
            dogs.add(dog1);
            dogs.add(dog2);

            String json = serialize(kennel, true);
            System.out.println("Kennel with Dogs list (smart compression ON):\n" + json);

            int simpleNameCount = countOccurrences(json, "\"_type\":\"Dog\"");
            assertEquals(2, simpleNameCount,
                    "Both Dog elements should use simple name 'Dog'");
        }
    }

    // ========================================================================
    // Smart Compression ON with Polymorphism - _type still written
    // ========================================================================

    @Nested
    @DisplayName("Smart Compression ON - Polymorphic")
    class SmartCompressionOnPolymorphic {

        @Test
        @DisplayName("polymorphic reference still writes _type")
        void polymorphic_smartCompressionOn_writesType() throws IOException {
            EObject owner = createOwner("John");
            EObject dog = createDog("Buddy", "Golden Retriever");
            owner.eSet(ownerPetRef, dog);

            String json = serialize(owner, true);
            System.out.println("Owner with Dog pet (smart compression ON):\n" + json);

            String petSection = extractSection(json, "pet");
            assertTrue(petSection.contains("\"_type\""),
                    "Pet section SHOULD contain _type when instance type != reference type.\n" +
                    "Pet section: " + petSection);
        }

        @Test
        @DisplayName("polymorphic list writes _type for each element")
        void polymorphic_list_smartCompressionOn_writesType() throws IOException {
            EObject owner = createOwner("John");
            EObject dog = createDog("Buddy", "Golden Retriever");
            EObject cat = createCat("Whiskers", true);

            @SuppressWarnings("unchecked")
            EList<EObject> pets = (EList<EObject>) owner.eGet(ownerPetsRef);
            pets.add(dog);
            pets.add(cat);

            String json = serialize(owner, true);
            System.out.println("Owner with Dog and Cat pets (smart compression ON):\n" + json);

            String petsSection = extractSection(json, "pets");
            int typeCount = countOccurrences(petsSection, "\"_type\"");
            assertEquals(2, typeCount,
                    "Pets section should contain _type for each pet when types differ.\n" +
                    "Pets section: " + petsSection);
        }
    }

    // ========================================================================
    // Edge Cases
    // ========================================================================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("root object still writes _type (no reference context)")
        void rootObject_smartCompressionOn_stillWritesType() throws IOException {
            EObject dog = createDog("Buddy", "Golden Retriever");

            String json = serialize(dog, true);
            System.out.println("Root Dog object (smart compression ON):\n" + json);

            assertTrue(json.contains("\"_type\""),
                    "Root object should contain _type even with smart compression ON");
        }

        @Test
        @DisplayName("mixed list with same base type writes _type for all")
        void mixedList_smartCompressionOn_writesTypeForAll() throws IOException {
            EObject owner = createOwner("John");
            EObject dog1 = createDog("Buddy", "Golden Retriever");
            EObject cat1 = createCat("Whiskers", true);
            EObject dog2 = createDog("Max", "German Shepherd");

            @SuppressWarnings("unchecked")
            EList<EObject> pets = (EList<EObject>) owner.eGet(ownerPetsRef);
            pets.add(dog1);
            pets.add(cat1);
            pets.add(dog2);

            String json = serialize(owner, true);
            System.out.println("Owner with mixed pets (smart compression ON):\n" + json);

            String petsSection = extractSection(json, "pets");
            int typeCount = countOccurrences(petsSection, "\"_type\"");
            assertEquals(3, typeCount,
                    "All pets in polymorphic list should have _type.\n" +
                    "Pets section: " + petsSection);
        }

        @Test
        @DisplayName("nested containment with smart compression uses simple names")
        void nestedContainment_smartCompressionOn() throws IOException {
            EObject kennel = createKennel("Happy Paws");
            EObject dog = createDog("Buddy", "Golden Retriever");
            kennel.eSet(kennelDogRef, dog);

            String json = serialize(kennel, true);
            System.out.println("Nested Kennel→Dog (smart compression ON):\n" + json);

            String nsUri = testPackage.getNsURI();
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Kennel\""),
                    "Root Kennel should have full URI");
            assertTrue(json.contains("\"_type\":\"Dog\""),
                    "Nested Dog should use simple name 'Dog'");
            assertFalse(json.contains("\"_type\":\"" + nsUri + "#//Dog\""),
                    "Nested Dog should NOT use full URI");
        }
    }

    // ========================================================================
    // Utility Methods
    // ========================================================================

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }

    private String extractSection(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) {
            return "";
        }
        start += pattern.length();

        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length()) {
            return "";
        }

        char firstChar = json.charAt(start);
        if (firstChar == '{') {
            return extractBalanced(json, start, '{', '}');
        } else if (firstChar == '[') {
            return extractBalanced(json, start, '[', ']');
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
                end++;
            }
            return json.substring(start, end);
        }
    }

    private String extractBalanced(String json, int start, char open, char close) {
        int depth = 0;
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == open) {
                depth++;
            } else if (c == close) {
                depth--;
                if (depth == 0) {
                    return json.substring(start, end + 1);
                }
            }
            end++;
        }
        return json.substring(start);
    }
}
