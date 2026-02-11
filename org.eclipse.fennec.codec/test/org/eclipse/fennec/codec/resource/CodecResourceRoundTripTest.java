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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
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
 * Round-trip integration tests for {@link CodecResource} (new codec.* package).
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.CodecResourceRoundTripTest}.
 * Tests that objects can be serialized to JSON and deserialized back
 * with all data preserved.
 * </p>
 */
@DisplayName("CodecResource Round-Trip Tests")
class CodecResourceRoundTripTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass personClass;
    private EClass addressClass;
    private EClass companyClass;

    // EAttributes on Person
    private EAttribute nameAttribute;
    private EAttribute ageAttribute;
    private EAttribute activeAttribute;
    private EAttribute scoreAttribute;
    private EAttribute tagsAttribute;

    // EReferences on Person
    private EReference addressRef;
    private EReference friendsRef;

    // EReferences on Company
    private EReference employeesRef;
    private EReference ceoRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(CodecResourceRoundTripTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);

        // Register package in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service with CodecAspectProvider registered
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load EClasses
        personClass = ecoreHelper.getEClass(testPackage, "Person");
        addressClass = ecoreHelper.getEClass(testPackage, "Address");
        companyClass = ecoreHelper.getEClass(testPackage, "Company");

        // Load EAttributes
        nameAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        ageAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "age");
        activeAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "active");
        scoreAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "score");
        tagsAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "tags");

        // Load EReferences on Person
        addressRef = (EReference) ecoreHelper.getFeature(personClass, "address");
        friendsRef = (EReference) ecoreHelper.getFeature(personClass, "friends");

        // Load EReferences on Company
        employeesRef = (EReference) ecoreHelper.getFeature(companyClass, "employees");
        ceoRef = (EReference) ecoreHelper.getFeature(companyClass, "ceo");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createPerson() {
        return testPackage.getEFactoryInstance().create(personClass);
    }

    private EObject createPerson(String name) {
        EObject person = createPerson();
        person.eSet(nameAttribute, name);
        return person;
    }

    private EObject createAddress() {
        return testPackage.getEFactoryInstance().create(addressClass);
    }

    private EObject createCompany() {
        return testPackage.getEFactoryInstance().create(companyClass);
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://roundtrip.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    @Nested
    @DisplayName("Simple attribute round-trip")
    class SimpleAttributeRoundTrip {

        @Test
        @DisplayName("round-trips string attribute")
        void roundTripsStringAttribute() throws IOException {
            EObject person = createPerson();
            person.eSet(nameAttribute, "John Doe");

            String json = serialize(person);

            assertTrue(json.contains("\"name\""), "JSON should contain name field");
            assertTrue(json.contains("\"John Doe\""), "JSON should contain name value");

            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            assertEquals("John Doe", loaded.eGet(nameAttribute));
        }

        @Test
        @DisplayName("round-trips integer attribute")
        void roundTripsIntegerAttribute() throws IOException {
            EObject person = createPerson();
            person.eSet(ageAttribute, 30);

            String json = serialize(person);
            assertTrue(json.contains("\"age\""), "JSON should contain age field");
            assertTrue(json.contains("30"), "JSON should contain age value");

            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            assertEquals(30, loaded.eGet(ageAttribute));
        }

        @Test
        @DisplayName("round-trips boolean attribute")
        void roundTripsBooleanAttribute() throws IOException {
            EObject person = createPerson();
            person.eSet(activeAttribute, true);

            String json = serialize(person);
            assertTrue(json.contains("\"active\""), "JSON should contain active field");
            assertTrue(json.contains("true"), "JSON should contain active value");

            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            assertEquals(true, loaded.eGet(activeAttribute));
        }

        @Test
        @DisplayName("round-trips double attribute")
        void roundTripsDoubleAttribute() throws IOException {
            EObject person = createPerson();
            person.eSet(scoreAttribute, 95.5);

            String json = serialize(person);
            assertTrue(json.contains("\"score\""), "JSON should contain score field");
            assertTrue(json.contains("95.5"), "JSON should contain score value");

            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            assertEquals(95.5, (Double) loaded.eGet(scoreAttribute), 0.001);
        }
    }

    @Nested
    @DisplayName("Multi-valued attribute round-trip")
    class MultiValuedAttributeRoundTrip {

        @Test
        @DisplayName("round-trips string list attribute")
        @SuppressWarnings("unchecked")
        void roundTripsStringListAttribute() throws IOException {
            EObject person = createPerson();
            List<String> tags = (List<String>) person.eGet(tagsAttribute);
            tags.add("developer");
            tags.add("java");
            tags.add("emf");

            String json = serialize(person);
            assertTrue(json.contains("\"tags\""), "JSON should contain tags field");
            assertTrue(json.contains("\"developer\""), "JSON should contain tag value");

            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            List<String> loadedTags = (List<String>) loaded.eGet(tagsAttribute);
            assertEquals(3, loadedTags.size());
            assertEquals("developer", loadedTags.get(0));
            assertEquals("java", loadedTags.get(1));
            assertEquals("emf", loadedTags.get(2));
        }
    }

    @Nested
    @DisplayName("Containment reference round-trip")
    class ContainmentReferenceRoundTrip {

        @Test
        @DisplayName("round-trips contained object")
        void roundTripsContainedObject() throws IOException {
            EObject person = createPerson();
            person.eSet(nameAttribute, "Jane");

            EObject address = createAddress();
            address.eSet(addressClass.getEStructuralFeature("street"), "123 Main St");
            address.eSet(addressClass.getEStructuralFeature("city"), "Springfield");
            person.eSet(addressRef, address);

            String json = serialize(person);
            assertTrue(json.contains("\"address\""), "JSON should contain address field");
            assertTrue(json.contains("\"123 Main St\""), "JSON should contain street value");
            assertTrue(json.contains("\"Springfield\""), "JSON should contain city value");

            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            EObject loadedAddress = (EObject) loaded.eGet(addressRef);
            assertNotNull(loadedAddress);
            assertEquals("123 Main St", loadedAddress.eGet(addressClass.getEStructuralFeature("street")));
            assertEquals("Springfield", loadedAddress.eGet(addressClass.getEStructuralFeature("city")));
        }
    }

    @Nested
    @DisplayName("Complex object round-trip")
    class ComplexObjectRoundTrip {

        @Test
        @DisplayName("round-trips object with multiple attributes and containment")
        @SuppressWarnings("unchecked")
        void roundTripsComplexObject() throws IOException {
            EObject person = createPerson();
            person.eSet(nameAttribute, "Alice");
            person.eSet(ageAttribute, 28);
            person.eSet(activeAttribute, true);
            person.eSet(scoreAttribute, 87.5);

            List<String> tags = (List<String>) person.eGet(tagsAttribute);
            tags.add("lead");
            tags.add("architect");

            EObject address = createAddress();
            address.eSet(addressClass.getEStructuralFeature("street"), "456 Oak Ave");
            address.eSet(addressClass.getEStructuralFeature("city"), "Metropolis");
            person.eSet(addressRef, address);

            String json = serialize(person);

            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            assertEquals("Alice", loaded.eGet(nameAttribute));
            assertEquals(28, loaded.eGet(ageAttribute));
            assertEquals(true, loaded.eGet(activeAttribute));
            assertEquals(87.5, (Double) loaded.eGet(scoreAttribute), 0.001);

            List<String> loadedTags = (List<String>) loaded.eGet(tagsAttribute);
            assertEquals(2, loadedTags.size());
            assertEquals("lead", loadedTags.get(0));
            assertEquals("architect", loadedTags.get(1));

            EObject loadedAddress = (EObject) loaded.eGet(addressRef);
            assertNotNull(loadedAddress);
            assertEquals("456 Oak Ave", loadedAddress.eGet(addressClass.getEStructuralFeature("street")));
            assertEquals("Metropolis", loadedAddress.eGet(addressClass.getEStructuralFeature("city")));
        }
    }

    @Nested
    @DisplayName("Non-containment reference round-trip")
    class NonContainmentReferenceRoundTrip {

        @Test
        @DisplayName("round-trips company with CEO reference to contained employee")
        @SuppressWarnings("unchecked")
        void roundTripsCompanyWithCeoReference() throws IOException {
            EObject company = createCompany();
            company.eSet(companyClass.getEStructuralFeature("name"), "Acme Inc");

            EObject alice = createPerson("Alice");
            EObject bob = createPerson("Bob");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            employees.add(bob);

            // Set CEO to Alice (non-containment reference)
            company.eSet(ceoRef, alice);

            String json = serialize(company);
            assertTrue(json.contains("\"ceo\""), "JSON should contain ceo field");

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            assertEquals("Acme Inc", loaded.eGet(companyClass.getEStructuralFeature("name")));

            List<EObject> loadedEmployees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(2, loadedEmployees.size());
            assertEquals("Alice", loadedEmployees.get(0).eGet(nameAttribute));
            assertEquals("Bob", loadedEmployees.get(1).eGet(nameAttribute));

            EObject loadedCeo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(loadedCeo, "CEO reference should be resolved");
            assertEquals("Alice", loadedCeo.eGet(nameAttribute));
            assertSame(loadedEmployees.get(0), loadedCeo, "CEO should be same instance as first employee");
        }

        @Test
        @DisplayName("round-trips person with friends (multi-valued non-containment)")
        @SuppressWarnings("unchecked")
        void roundTripsPersonWithFriends() throws IOException {
            EObject company = createCompany();
            company.eSet(companyClass.getEStructuralFeature("name"), "Social Inc");

            EObject alice = createPerson("Alice");
            EObject bob = createPerson("Bob");
            EObject charlie = createPerson("Charlie");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            employees.add(bob);
            employees.add(charlie);

            List<EObject> aliceFriends = (List<EObject>) alice.eGet(friendsRef);
            aliceFriends.add(bob);
            aliceFriends.add(charlie);

            String json = serialize(company);

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            List<EObject> loadedEmployees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(3, loadedEmployees.size());

            EObject loadedAlice = loadedEmployees.get(0);
            EObject loadedBob = loadedEmployees.get(1);
            EObject loadedCharlie = loadedEmployees.get(2);

            List<EObject> loadedFriends = (List<EObject>) loadedAlice.eGet(friendsRef);
            assertEquals(2, loadedFriends.size(), "Alice should have 2 friends");
            assertSame(loadedBob, loadedFriends.get(0), "First friend should be Bob");
            assertSame(loadedCharlie, loadedFriends.get(1), "Second friend should be Charlie");
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String serialize(EObject object) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootEClass) throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
