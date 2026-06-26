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
package org.eclipse.fennec.codec.format;

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
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.impl.JacksonFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.json.JsonFactory;

/**
 * End-to-end integration test that routes codec serialization/deserialization
 * through the FormatDelegate path.
 * <p>
 * Uses {@link CodecResource} with a {@link JacksonFormatProvider} (backed by
 * {@link JsonFactory}) to verify that the full chain works:
 * <pre>
 * EObject → ObjectMapper → FormatDelegateGenerator → FormatDelegate → OutputStream
 * InputStream → FormatReaderDelegate → FormatDelegateParser → ObjectMapper → EObject
 * </pre>
 * <p>
 * These tests mirror {@code CodecResourceRoundTripTest} but exercise the
 * FormatDelegate code path instead of the direct CodecJsonFactory path.
 */
@DisplayName("FormatDelegate JSON Round-Trip")
class FormatDelegateJsonRoundTripTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private JacksonFormatProvider formatProvider;

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
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, FormatDelegateJsonRoundTripTest.class);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        formatProvider = new JacksonFormatProvider("json", new JsonFactory());

        // Load EClasses
        personClass = EcoreHelper.getEClass(testPackage, "Person");
        addressClass = EcoreHelper.getEClass(testPackage, "Address");
        companyClass = EcoreHelper.getEClass(testPackage, "Company");

        // Load EAttributes
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "age");
        activeAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "active");
        scoreAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "score");
        tagsAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "tags");

        // Load EReferences on Person
        addressRef = (EReference) EcoreHelper.getFeature(personClass, "address");
        friendsRef = (EReference) EcoreHelper.getFeature(personClass, "friends");

        // Load EReferences on Company
        employeesRef = (EReference) EcoreHelper.getFeature(companyClass, "employees");
        ceoRef = (EReference) EcoreHelper.getFeature(companyClass, "ceo");
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
                null, null, formatProvider);
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
            assertTrue(json.contains("\"name\""));
            assertTrue(json.contains("\"John Doe\""));

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
            assertTrue(json.contains("\"age\""));
            assertTrue(json.contains("30"));

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
            assertTrue(json.contains("\"active\""));
            assertTrue(json.contains("true"));

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
            assertTrue(json.contains("\"score\""));
            assertTrue(json.contains("95.5"));

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
            assertTrue(json.contains("\"tags\""));
            assertTrue(json.contains("\"developer\""));

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
            assertTrue(json.contains("\"address\""));
            assertTrue(json.contains("\"123 Main St\""));
            assertTrue(json.contains("\"Springfield\""));

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

            company.eSet(ceoRef, alice);

            String json = serialize(company);
            assertTrue(json.contains("\"ceo\""));

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

    @Nested
    @DisplayName("FormatDelegate path produces same JSON as direct path")
    class OutputParity {

        @Test
        @DisplayName("format delegate JSON matches direct codec JSON for simple object")
        void formatDelegateMatchesDirectCodec() throws IOException {
            EObject person = createPerson();
            person.eSet(nameAttribute, "Parity Test");
            person.eSet(ageAttribute, 42);

            // Serialize via FormatDelegate path
            String formatDelegateJson = serialize(person);

            // Serialize via direct path
            CodecResource directResource = new CodecResource(
                    URI.createURI("test://direct.json"),
                    metadataService,
                    ConfigurationResolver.defaults(),
                    null);
            directResource.getContents().add(createPerson());
            directResource.getContents().get(0).eSet(nameAttribute, "Parity Test");
            directResource.getContents().get(0).eSet(ageAttribute, 42);

            ByteArrayOutputStream directOut = new ByteArrayOutputStream();
            directResource.save(directOut, Collections.emptyMap());
            String directJson = directOut.toString(StandardCharsets.UTF_8);

            assertEquals(directJson, formatDelegateJson,
                    "FormatDelegate path should produce identical JSON to direct path");
        }
    }

    // ========================================================================
    // Field Order Tests
    // ========================================================================

    @Nested
    @DisplayName("Field ordering")
    class FieldOrder {

        @Test
        @DisplayName("ALPHABETICAL fieldOrder sorts JSON keys alphabetically")
        void alphabeticalFieldOrderSortsKeysAlphabetically() throws IOException {
            EObject person = createPerson();
            person.eSet(nameAttribute, "Alice");
            person.eSet(ageAttribute, 30);
            person.eSet(scoreAttribute, 1.5);

            Map<String, Object> saveOptions = new HashMap<>();
            saveOptions.put(ConfigProperty.FIELD_ORDER.getKey(), "ALPHABETICAL");
            String json = serialize(person, saveOptions);

            // Declaration order: name, age, score
            // Alphabetical order: age, name, score
            int ageIdx = json.indexOf("\"age\"");
            int nameIdx = json.indexOf("\"name\"");
            int scoreIdx = json.indexOf("\"score\"");

            assertTrue(ageIdx >= 0, "age key must be present");
            assertTrue(nameIdx >= 0, "name key must be present");
            assertTrue(scoreIdx >= 0, "score key must be present");
            assertTrue(ageIdx < nameIdx, "age should appear before name in alphabetical order but was: " + json);
            assertTrue(nameIdx < scoreIdx, "name should appear before score in alphabetical order but was: " + json);
        }

        @Test
        @DisplayName("default fieldOrder preserves EClass declaration order")
        void defaultFieldOrderPreservesDeclarationOrder() throws IOException {
            EObject person = createPerson();
            person.eSet(nameAttribute, "Alice");
            person.eSet(ageAttribute, 30);
            person.eSet(scoreAttribute, 1.5);

            String json = serialize(person);

            // Declaration order: name (1st), age (2nd), score (4th)
            int nameIdx = json.indexOf("\"name\"");
            int ageIdx = json.indexOf("\"age\"");
            int scoreIdx = json.indexOf("\"score\"");

            assertTrue(nameIdx >= 0, "name key must be present");
            assertTrue(ageIdx >= 0, "age key must be present");
            assertTrue(scoreIdx >= 0, "score key must be present");
            assertTrue(nameIdx < ageIdx, "name should appear before age in declaration order but was: " + json);
            assertTrue(ageIdx < scoreIdx, "age should appear before score in declaration order but was: " + json);
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

    private String serialize(EObject object, Map<String, Object> saveOptions) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, saveOptions);

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
