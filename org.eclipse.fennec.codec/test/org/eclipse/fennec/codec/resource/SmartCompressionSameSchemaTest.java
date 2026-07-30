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
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for same-schema smart compression.
 *
 * @see <a href="docs/codec-v2-spec/04-global-options.md#1-smart-compression">Spec: Smart Compression</a>
 */
@DisplayName("Smart Compression Same-Schema Tests")
class SmartCompressionSameSchemaTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass companyClass;
    private EClass personClass;
    private EClass addressClass;
    private EAttribute companyNameAttr;
    private EAttribute personNameAttr;
    private EAttribute streetAttr;
    private EAttribute cityAttr;
    private EReference employeesRef;
    private EReference personAddressRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, SmartCompressionSameSchemaTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        companyClass = EcoreHelper.getEClass(testPackage, "Company");
        personClass = EcoreHelper.getEClass(testPackage, "Person");
        addressClass = EcoreHelper.getEClass(testPackage, "Address");

        companyNameAttr = (EAttribute) EcoreHelper.getFeature(companyClass, "name");
        personNameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        streetAttr = (EAttribute) EcoreHelper.getFeature(addressClass, "street");
        cityAttr = (EAttribute) EcoreHelper.getFeature(addressClass, "city");

        employeesRef = (EReference) EcoreHelper.getFeature(companyClass, "employees");
        personAddressRef = (EReference) EcoreHelper.getFeature(personClass, "address");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private String serialize(EObject object, boolean smartCompression) throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(Map.of("smartCompression", smartCompression))
                .build();

        CodecResource resource = new CodecResource(
                URI.createURI("test://smart-compression-test.json"),
                metadataService,
                resolver,
                null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toString(StandardCharsets.UTF_8);
    }

    @Nested
    @DisplayName("Smart Compression OFF")
    class SmartCompressionOff {

        @Test
        @DisplayName("All types use full URIs")
        void allTypesUseFullUris() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Alice");

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person);

            String json = serialize(company, false);
            System.out.println("=== Smart Compression OFF ===");
            System.out.println(json);
            System.out.println();

            String nsUri = testPackage.getNsURI();
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Company\""),
                    "Root Company should use full URI");
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Person\""),
                    "Contained Person should use full URI");
        }

        @Test
        @DisplayName("Nested containment uses full URIs")
        void nestedContainmentUsesFullUris() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Alice");

            EObject address = testPackage.getEFactoryInstance().create(addressClass);
            address.eSet(streetAttr, "123 Main St");
            address.eSet(cityAttr, "Springfield");
            person.eSet(personAddressRef, address);

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person);

            String json = serialize(company, false);
            System.out.println("=== Nested Containment - Smart Compression OFF ===");
            System.out.println(json);
            System.out.println();

            String nsUri = testPackage.getNsURI();
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Company\""),
                    "Root Company should use full URI");
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Person\""),
                    "Contained Person should use full URI");
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Address\""),
                    "Nested Address should use full URI");
        }
    }

    @Nested
    @DisplayName("Smart Compression ON")
    class SmartCompressionOn {

        @Test
        @DisplayName("Root uses full URI, contained uses simple name")
        void rootFullUriContainedSimpleName() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Alice");

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person);

            String json = serialize(company, true);
            System.out.println("=== Smart Compression ON ===");
            System.out.println(json);
            System.out.println();

            String nsUri = testPackage.getNsURI();
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Company\""),
                    "Root Company should use full URI to establish context");
            assertTrue(json.contains("\"_type\":\"Person\""),
                    "Contained Person should use simple name 'Person'");
            assertFalse(json.contains("\"_type\":\"" + nsUri + "#//Person\""),
                    "Contained Person should NOT use full URI");
        }

        @Test
        @DisplayName("Nested containment uses simple names")
        void nestedContainmentUsesSimpleNames() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Alice");

            EObject address = testPackage.getEFactoryInstance().create(addressClass);
            address.eSet(streetAttr, "123 Main St");
            address.eSet(cityAttr, "Springfield");
            person.eSet(personAddressRef, address);

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person);

            String json = serialize(company, true);
            System.out.println("=== Nested Containment - Smart Compression ON ===");
            System.out.println(json);
            System.out.println();

            String nsUri = testPackage.getNsURI();
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Company\""),
                    "Root Company should use full URI");
            assertTrue(json.contains("\"_type\":\"Person\""),
                    "Contained Person should use simple name");
            assertTrue(json.contains("\"_type\":\"Address\""),
                    "Nested Address should use simple name");
        }

        @Test
        @DisplayName("Multiple employees all use simple names")
        void multipleEmployeesUseSimpleNames() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme");

            EObject person1 = testPackage.getEFactoryInstance().create(personClass);
            person1.eSet(personNameAttr, "Alice");

            EObject person2 = testPackage.getEFactoryInstance().create(personClass);
            person2.eSet(personNameAttr, "Bob");

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person1);
            employees.add(person2);

            String json = serialize(company, true);
            System.out.println("=== Multiple Employees - Smart Compression ON ===");
            System.out.println(json);
            System.out.println();

            int simpleNameCount = countOccurrences(json, "\"_type\":\"Person\"");
            assertEquals(2, simpleNameCount,
                    "Should have 2 occurrences of simple name 'Person'");

            String nsUri = testPackage.getNsURI();
            assertFalse(json.contains("\"_type\":\"" + nsUri + "#//Person\""),
                    "Should NOT have full URI for Person");
        }
    }

    @Nested
    @DisplayName("Root Object")
    class RootObject {

        @Test
        @DisplayName("Root object always uses full URI even with smart compression")
        void rootAlwaysUsesFullUri() throws IOException {
            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Alice");

            String json = serialize(person, true);
            System.out.println("=== Root Object with Smart Compression ON ===");
            System.out.println(json);
            System.out.println();

            String nsUri = testPackage.getNsURI();
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//Person\""),
                    "Root Person should use full URI even with smart compression");
        }
    }

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }

    private EObject deserialize(String json) throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(Map.of("smartCompression", true))
                .build();

        CodecResource resource = new CodecResource(
                URI.createURI("test://smart-compression-deser.json"),
                metadataService,
                resolver,
                null);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, Collections.emptyMap());

        assertFalse(resource.getContents().isEmpty(), "Resource should have contents after load");
        return resource.getContents().get(0);
    }

    /**
     * Saves several resource roots in one document and loads it back (issue #76).
     * <p>
     * This is the coverage that was missing: the existing tests assert the written output for a
     * single root, so nothing noticed that a second root was written in a shape the reader could
     * not resolve.
     * </p>
     */
    private CodecResource roundTripRoots(List<EObject> roots) throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(Map.of("smartCompression", true))
                .build();

        CodecResource saveResource = new CodecResource(
                URI.createURI("test://smart-compression-multiroot.json"),
                metadataService, resolver, null);
        saveResource.getContents().addAll(roots);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://smart-compression-multiroot.json"),
                metadataService, resolver, null);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), Collections.emptyMap());
        return loadResource;
    }

    @Nested
    @DisplayName("Multiple Resource Roots (issue #76)")
    class MultipleResourceRoots {

        @Test
        @DisplayName("every root establishes its own context and uses a full URI")
        void everyRootUsesFullUri() throws IOException {
            EObject first = testPackage.getEFactoryInstance().create(personClass);
            first.eSet(personNameAttr, "Alice");
            EObject second = testPackage.getEFactoryInstance().create(personClass);
            second.eSet(personNameAttr, "Bob");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of("smartCompression", true))
                    .build();
            CodecResource resource = new CodecResource(
                    URI.createURI("test://smart-compression-multiroot-write.json"),
                    metadataService, resolver, null);
            resource.getContents().add(first);
            resource.getContents().add(second);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, Collections.emptyMap());
            String json = out.toString(StandardCharsets.UTF_8);

            // Compression is defined relative to *the root object*: a root establishes the
            // context, nested objects consume it. A second root is a root, not a nested object.
            assertEquals(2, countOccurrences(json, "\"_type\":\"" + testPackage.getNsURI() + "#//Person\""),
                    "each root must carry the full URI: " + json);
            assertFalse(json.contains("\"_type\":\"Person\""),
                    "no root may be compressed to a bare name: " + json);
        }

        @Test
        @DisplayName("all roots survive the round-trip")
        void allRootsSurviveRoundTrip() throws IOException {
            EObject first = testPackage.getEFactoryInstance().create(personClass);
            first.eSet(personNameAttr, "Alice");
            EObject second = testPackage.getEFactoryInstance().create(personClass);
            second.eSet(personNameAttr, "Bob");
            EObject third = testPackage.getEFactoryInstance().create(personClass);
            third.eSet(personNameAttr, "Carol");

            CodecResource loaded = roundTripRoots(List.of(first, second, third));

            assertTrue(loaded.getErrors().isEmpty(),
                    "the load must not report errors: " + loaded.getErrors());
            assertEquals(3, loaded.getContents().size(),
                    "no root may be dropped silently");
            assertEquals("Alice", loaded.getContents().get(0).eGet(personNameAttr));
            assertEquals("Bob", loaded.getContents().get(1).eGet(personNameAttr));
            assertEquals("Carol", loaded.getContents().get(2).eGet(personNameAttr));
        }

        @Test
        @DisplayName("compression still applies to objects nested inside each root")
        void compressionStillAppliesWithinRoots() throws IOException {
            EObject companyOne = testPackage.getEFactoryInstance().create(companyClass);
            companyOne.eSet(companyNameAttr, "Acme");
            EObject employee = testPackage.getEFactoryInstance().create(personClass);
            employee.eSet(personNameAttr, "Alice");
            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) companyOne.eGet(employeesRef);
            employees.add(employee);

            EObject companyTwo = testPackage.getEFactoryInstance().create(companyClass);
            companyTwo.eSet(companyNameAttr, "Globex");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of("smartCompression", true))
                    .build();
            CodecResource resource = new CodecResource(
                    URI.createURI("test://smart-compression-multiroot-nested.json"),
                    metadataService, resolver, null);
            resource.getContents().add(companyOne);
            resource.getContents().add(companyTwo);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, Collections.emptyMap());
            String json = out.toString(StandardCharsets.UTF_8);

            // The point of per-root contexts is that they do not disable compression, they scope
            // it: the contained employee is still written as a bare name.
            assertTrue(json.contains("\"_type\":\"Person\""),
                    "a nested object must still be compressed: " + json);
            assertEquals(2, countOccurrences(json, "\"_type\":\"" + testPackage.getNsURI() + "#//Company\""),
                    "both roots must carry the full URI: " + json);

            CodecResource loaded = roundTripRoots(List.of(companyOne, companyTwo));
            assertEquals(2, loaded.getContents().size(), "both roots must load");
        }
    }

    @Nested
    @DisplayName("Deserialization Round-Trip")
    class DeserializationRoundTrip {

        @Test
        @DisplayName("Deserialize JSON with simple type names")
        void deserializeSimpleTypeNames() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Alice");

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person);

            String json = serialize(company, true);
            System.out.println("=== Deserialization Round-Trip ===");
            System.out.println(json);

            assertTrue(json.contains("\"_type\":\"Person\""),
                    "Serialized JSON should use simple name for Person");

            EObject loaded = deserialize(json);

            assertNotNull(loaded, "Loaded object should not be null");
            assertEquals(companyClass, loaded.eClass(), "Root should be Company");
            assertEquals("Acme", loaded.eGet(companyNameAttr), "Company name should match");

            @SuppressWarnings("unchecked")
            List<EObject> loadedEmployees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(1, loadedEmployees.size(), "Should have 1 employee");

            EObject loadedPerson = loadedEmployees.get(0);
            assertEquals(personClass, loadedPerson.eClass(),
                    "Employee should be Person (resolved from simple name)");
            assertEquals("Alice", loadedPerson.eGet(personNameAttr), "Person name should match");
        }

        @Test
        @DisplayName("Deserialize nested containment with simple names")
        void deserializeNestedContainment() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Alice");

            EObject address = testPackage.getEFactoryInstance().create(addressClass);
            address.eSet(streetAttr, "123 Main St");
            address.eSet(cityAttr, "Springfield");
            person.eSet(personAddressRef, address);

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person);

            String json = serialize(company, true);
            System.out.println("=== Nested Deserialization Round-Trip ===");
            System.out.println(json);

            assertTrue(json.contains("\"_type\":\"Person\""),
                    "Should use simple name for Person");
            assertTrue(json.contains("\"_type\":\"Address\""),
                    "Should use simple name for Address");

            EObject loaded = deserialize(json);

            assertEquals(companyClass, loaded.eClass(), "Root should be Company");

            @SuppressWarnings("unchecked")
            List<EObject> loadedEmployees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(1, loadedEmployees.size(), "Should have 1 employee");

            EObject loadedPerson = loadedEmployees.get(0);
            assertEquals(personClass, loadedPerson.eClass(), "Employee should be Person");
            assertEquals("Alice", loadedPerson.eGet(personNameAttr), "Person name should match");

            EObject loadedAddress = (EObject) loadedPerson.eGet(personAddressRef);
            assertNotNull(loadedAddress, "Person should have address");
            assertEquals(addressClass, loadedAddress.eClass(),
                    "Address should be resolved from simple name");
            assertEquals("123 Main St", loadedAddress.eGet(streetAttr), "Street should match");
            assertEquals("Springfield", loadedAddress.eGet(cityAttr), "City should match");
        }

        @Test
        @DisplayName("Deserialize multiple employees with simple names")
        void deserializeMultipleEmployees() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme");

            EObject person1 = testPackage.getEFactoryInstance().create(personClass);
            person1.eSet(personNameAttr, "Alice");

            EObject person2 = testPackage.getEFactoryInstance().create(personClass);
            person2.eSet(personNameAttr, "Bob");

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person1);
            employees.add(person2);

            String json = serialize(company, true);
            System.out.println("=== Multiple Employees Deserialization ===");
            System.out.println(json);

            EObject loaded = deserialize(json);

            assertEquals(companyClass, loaded.eClass(), "Root should be Company");

            @SuppressWarnings("unchecked")
            List<EObject> loadedEmployees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(2, loadedEmployees.size(), "Should have 2 employees");

            assertEquals(personClass, loadedEmployees.get(0).eClass(), "First employee should be Person");
            assertEquals("Alice", loadedEmployees.get(0).eGet(personNameAttr), "First person name");

            assertEquals(personClass, loadedEmployees.get(1).eClass(), "Second employee should be Person");
            assertEquals("Bob", loadedEmployees.get(1).eGet(personNameAttr), "Second person name");
        }

        @Test
        @DisplayName("Deserialize raw JSON with simple type names")
        void deserializeRawJsonWithSimpleNames() throws IOException {
            String nsUri = testPackage.getNsURI();
            String json = """
                    {
                      "_type": "%s#//Company",
                      "name": "TestCorp",
                      "employees": [
                        { "_type": "Person", "name": "Charlie" },
                        { "_type": "Person", "name": "Diana" }
                      ]
                    }
                    """.formatted(nsUri);

            System.out.println("=== Raw JSON with Simple Names ===");
            System.out.println(json);

            EObject loaded = deserialize(json);

            assertEquals(companyClass, loaded.eClass(), "Root should be Company");
            assertEquals("TestCorp", loaded.eGet(companyNameAttr), "Company name should match");

            @SuppressWarnings("unchecked")
            List<EObject> loadedEmployees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(2, loadedEmployees.size(), "Should have 2 employees");

            assertEquals(personClass, loadedEmployees.get(0).eClass(),
                    "First employee should be Person (from simple name)");
            assertEquals("Charlie", loadedEmployees.get(0).eGet(personNameAttr));

            assertEquals(personClass, loadedEmployees.get(1).eClass(),
                    "Second employee should be Person (from simple name)");
            assertEquals("Diana", loadedEmployees.get(1).eGet(personNameAttr));
        }

        @Test
        @DisplayName("Deserialize deeply nested JSON with simple names")
        void deserializeDeeplyNestedSimpleNames() throws IOException {
            String nsUri = testPackage.getNsURI();
            String json = """
                    {
                      "_type": "%s#//Company",
                      "name": "DeepCorp",
                      "employees": [
                        {
                          "_type": "Person",
                          "name": "Eve",
                          "address": {
                            "_type": "Address",
                            "street": "456 Oak Ave",
                            "city": "Metropolis"
                          }
                        }
                      ]
                    }
                    """.formatted(nsUri);

            System.out.println("=== Deeply Nested JSON with Simple Names ===");
            System.out.println(json);

            EObject loaded = deserialize(json);

            assertEquals(companyClass, loaded.eClass(), "Root should be Company");

            @SuppressWarnings("unchecked")
            List<EObject> loadedEmployees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(1, loadedEmployees.size());

            EObject loadedPerson = loadedEmployees.get(0);
            assertEquals(personClass, loadedPerson.eClass(), "Should be Person from simple name");
            assertEquals("Eve", loadedPerson.eGet(personNameAttr));

            EObject loadedAddress = (EObject) loadedPerson.eGet(personAddressRef);
            assertNotNull(loadedAddress, "Person should have address");
            assertEquals(addressClass, loadedAddress.eClass(), "Should be Address from simple name");
            assertEquals("456 Oak Ave", loadedAddress.eGet(streetAttr));
            assertEquals("Metropolis", loadedAddress.eGet(cityAttr));
        }
    }
}
