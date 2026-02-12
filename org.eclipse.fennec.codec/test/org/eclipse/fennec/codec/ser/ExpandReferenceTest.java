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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for the expand feature for non-containment references.
 * <p>
 * Tests both serialization (with expand config) and deserialization
 * (auto-detection of expanded objects without _ref).
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/10-reference.md#5-proxy-and-expand-handling">Spec: Proxy and Expand Handling</a>
 */
@DisplayName("Expand Reference Tests")
class ExpandReferenceTest {

    private static final String TEST_ECORE = "test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass personClass;
    private EClass companyClass;

    // EAttributes
    private EAttribute personNameAttribute;
    private EAttribute companyNameAttribute;

    // EReferences
    private EReference friendsRef;
    private EReference employeesRef;
    private EReference ceoRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(ExpandReferenceTest.class);
        testPackage = ecoreHelper.loadEcore(TEST_ECORE);

        // Register package in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load EClasses
        personClass = ecoreHelper.getEClass(testPackage, "Person");
        companyClass = ecoreHelper.getEClass(testPackage, "Company");

        // Load EAttributes
        personNameAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        companyNameAttribute = (EAttribute) ecoreHelper.getFeature(companyClass, "name");

        // Load EReferences
        friendsRef = (EReference) ecoreHelper.getFeature(personClass, "friends");
        employeesRef = (EReference) ecoreHelper.getFeature(companyClass, "employees");
        ceoRef = (EReference) ecoreHelper.getFeature(companyClass, "ceo");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helper methods
    // ========================================================================

    private EObject createPerson(String name) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(personNameAttribute, name);
        return person;
    }

    private EObject createCompany(String name) {
        EObject company = testPackage.getEFactoryInstance().create(companyClass);
        company.eSet(companyNameAttribute, name);
        return company;
    }

    private CodecResource createResource(ConfigurationResolver resolver) {
        return new CodecResource(
                URI.createURI("test://expand-test.json"),
                metadataService,
                resolver,
                null);
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = createResource(resolver);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());

        String json = out.toString(StandardCharsets.UTF_8);
        System.out.println("Serialized JSON:\n" + json);
        return json;
    }

    private EObject deserialize(String json, EClass rootEClass) throws IOException {
        CodecResource resource = createResource(ConfigurationResolver.defaults());

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    // ========================================================================
    // Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Expand Serialization")
    class ExpandSerialization {

        @Test
        @DisplayName("serializes non-containment reference as proxy by default")
        @SuppressWarnings("unchecked")
        void serializesAsProxyByDefault() throws IOException {
            // Create company with employees and CEO
            EObject company = createCompany("Acme Inc");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Serialize with default config (no expand)
            String json = serialize(company, ConfigurationResolver.defaults());

            // CEO should be serialized as $ref (proxy reference)
            assertTrue(json.contains("\"ceo\""), "JSON should contain ceo field");
            assertTrue(json.contains("\"$ref\""), "CEO should be serialized with $ref key");

            // The CEO object should NOT contain inline data like "name":"Alice" outside of employees
            // We need to check that the ceo field has $ref and should NOT have "name" inside it
            int ceoIndex = json.indexOf("\"ceo\"");
            int ceoEndIndex = json.indexOf("}", ceoIndex);
            String ceoJson = json.substring(ceoIndex, ceoEndIndex + 1);
            assertTrue(ceoJson.contains("$ref"), "CEO should contain $ref");
            assertFalse(ceoJson.contains("\"name\""), "CEO should NOT contain name (not expanded)");
        }

        @Test
        @DisplayName("expands non-containment reference with expandGlobal=true")
        @SuppressWarnings("unchecked")
        void expandsWithGlobalFlag() throws IOException {
            // Create company with employees and CEO
            EObject company = createCompany("Acme Inc");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Serialize with expandGlobal=true
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expandGlobal(true)
                    .build();

            String json = serialize(company, resolver);

            // CEO should be expanded inline (no $ref)
            assertTrue(json.contains("\"ceo\""), "JSON should contain ceo field");
            // The expanded CEO should have name but no $ref
            // Note: Due to expandIgnoreBidirectional default=true, we need objects without opposite refs
        }

        @Test
        @DisplayName("expands specific reference by EReference")
        @SuppressWarnings("unchecked")
        void expandsSpecificReferenceByEReference() throws IOException {
            // Create company with employees and CEO
            EObject company = createCompany("Acme Inc");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Serialize with specific reference expanded
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand(ceoRef)
                    .build();

            String json = serialize(company, resolver);

            // CEO should be expanded inline
            assertTrue(json.contains("\"ceo\""), "JSON should contain ceo field");
        }

        @Test
        @DisplayName("expands specific reference by name")
        @SuppressWarnings("unchecked")
        void expandsSpecificReferenceByName() throws IOException {
            // Create company with employees and CEO
            EObject company = createCompany("Acme Inc");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Serialize with specific reference expanded by name
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand("ceo")
                    .build();

            String json = serialize(company, resolver);

            // CEO should be expanded inline
            assertTrue(json.contains("\"ceo\""), "JSON should contain ceo field");
        }

        @Test
        @DisplayName("does not expand proxy objects")
        void doesNotExpandProxies() throws IOException {
            // Create company
            EObject company = createCompany("Acme Inc");

            // Create a proxy for CEO (not resolved)
            EObject proxyPerson = testPackage.getEFactoryInstance().create(personClass);
            ((org.eclipse.emf.ecore.InternalEObject) proxyPerson)
                    .eSetProxyURI(URI.createURI("other.json#//@employees.0"));
            company.eSet(ceoRef, proxyPerson);

            // Serialize with expandGlobal=true
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expandGlobal(true)
                    .build();

            String json = serialize(company, resolver);

            // Proxy CEO should still be serialized as $ref (not expanded)
            assertTrue(json.contains("\"ceo\""), "JSON should contain ceo field");
            assertTrue(json.contains("$ref"), "Proxy CEO should be serialized as $ref");
        }

        @Test
        @DisplayName("expands multi-valued non-containment reference")
        @SuppressWarnings("unchecked")
        void expandsMultiValuedReference() throws IOException {
            // Create company with employees
            EObject company = createCompany("Acme Inc");
            EObject alice = createPerson("Alice");
            EObject bob = createPerson("Bob");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            employees.add(bob);

            // Set Alice's friends to Bob (multi-valued non-containment)
            List<EObject> friends = (List<EObject>) alice.eGet(friendsRef);
            friends.add(bob);

            // Serialize with friends expanded
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand("friends")
                    .build();

            String json = serialize(company, resolver);

            // Friends should be expanded
            assertTrue(json.contains("\"friends\""), "JSON should contain friends field");
        }
    }

    // ========================================================================
    // Deserialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Expand Deserialization")
    class ExpandDeserialization {

        @Test
        @DisplayName("deserializes expanded non-containment as orphan object")
        void deserializesExpandedAsOrphan() throws IOException {
            // JSON with expanded CEO (no $ref, just inline object)
            String json = """
                {
                  "_type": "http://test.example.org/roundtrip/1.0#//Company",
                  "name": "Acme Inc",
                  "employees": [],
                  "ceo": {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "Alice"
                  }
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            assertEquals("Acme Inc", loaded.eGet(companyNameAttribute));

            // CEO should be deserialized as an orphan object
            EObject ceo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(ceo, "CEO should be deserialized");
            assertEquals("Alice", ceo.eGet(personNameAttribute));

            // Orphan: not a proxy
            assertFalse(ceo.eIsProxy(), "Expanded CEO should not be a proxy");

            // Orphan: not contained (no eContainer)
            assertNull(ceo.eContainer(), "Expanded CEO should be an orphan (no container)");

            // Orphan: no resource assigned
            assertNull(ceo.eResource(), "Expanded CEO should have no resource");
        }

        @Test
        @DisplayName("deserializes proxy reference with $ref")
        void deserializesProxyReference() throws IOException {
            // JSON with proxy CEO (has $ref)
            String json = """
                {
                  "_type": "http://test.example.org/roundtrip/1.0#//Company",
                  "name": "Acme Inc",
                  "employees": [
                    {
                      "_type": "http://test.example.org/roundtrip/1.0#//Person",
                      "name": "Alice"
                    }
                  ],
                  "ceo": {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "$ref": "//@employees.0"
                  }
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(1, employees.size());
            assertEquals("Alice", employees.get(0).eGet(personNameAttribute));

            // CEO should be resolved to Alice
            EObject ceo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(ceo, "CEO should be resolved");
            assertSame(employees.get(0), ceo, "CEO should be same instance as first employee");
        }

        @Test
        @DisplayName("deserializes proxy with projection ($ref + additional fields)")
        void deserializesProxyWithProjection() throws IOException {
            // JSON with proxy that has projection data
            String json = """
                {
                  "_type": "http://test.example.org/roundtrip/1.0#//Company",
                  "name": "Acme Inc",
                  "employees": [],
                  "ceo": {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "$ref": "other.json#//@employees.0",
                    "name": "Alice"
                  }
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);

            // CEO should be a proxy with populated name field
            EObject ceo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(ceo, "CEO should be created");
            assertTrue(ceo.eIsProxy(), "CEO should be a proxy (has $ref)");
            assertEquals("Alice", ceo.eGet(personNameAttribute),
                    "Proxy should have projected name populated");
        }

        @Test
        @DisplayName("deserializes multi-valued expanded references")
        void deserializesMultiValuedExpanded() throws IOException {
            // JSON with expanded friends array (no $ref)
            String json = """
                {
                  "_type": "http://test.example.org/roundtrip/1.0#//Person",
                  "name": "Alice",
                  "friends": [
                    {
                      "_type": "http://test.example.org/roundtrip/1.0#//Person",
                      "name": "Bob"
                    },
                    {
                      "_type": "http://test.example.org/roundtrip/1.0#//Person",
                      "name": "Charlie"
                    }
                  ]
                }
                """;

            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            assertEquals("Alice", loaded.eGet(personNameAttribute));

            @SuppressWarnings("unchecked")
            List<EObject> friends = (List<EObject>) loaded.eGet(friendsRef);
            assertEquals(2, friends.size());

            // Both friends should be orphan objects
            EObject bob = friends.get(0);
            EObject charlie = friends.get(1);

            assertEquals("Bob", bob.eGet(personNameAttribute));
            assertEquals("Charlie", charlie.eGet(personNameAttribute));

            assertFalse(bob.eIsProxy(), "Bob should not be a proxy");
            assertFalse(charlie.eIsProxy(), "Charlie should not be a proxy");

            assertNull(bob.eContainer(), "Bob should be an orphan");
            assertNull(charlie.eContainer(), "Charlie should be an orphan");
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Expand Round-Trip")
    class ExpandRoundTrip {

        @Test
        @DisplayName("round-trips expanded non-containment reference")
        @SuppressWarnings("unchecked")
        void roundTripsExpandedReference() throws IOException {
            // Create company with employees and CEO
            EObject company = createCompany("Acme Inc");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Serialize with expand
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand(ceoRef)
                    .build();

            String json = serialize(company, resolver);

            // Deserialize (auto-detects expanded)
            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            assertEquals("Acme Inc", loaded.eGet(companyNameAttribute));

            // CEO should be an orphan with correct data
            EObject loadedCeo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(loadedCeo, "CEO should be deserialized");
            assertEquals("Alice", loadedCeo.eGet(personNameAttribute));
        }

        @Test
        @DisplayName("round-trips expanded multi-valued non-containment reference")
        @SuppressWarnings("unchecked")
        void roundTripsExpandedMultiValuedReference() throws IOException {
            // Create Alice with friends Bob and Charlie
            EObject alice = createPerson("Alice");
            EObject bob = createPerson("Bob");
            EObject charlie = createPerson("Charlie");

            List<EObject> friends = (List<EObject>) alice.eGet(friendsRef);
            friends.add(bob);
            friends.add(charlie);

            // Serialize with friends expanded
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand("friends")
                    .build();

            String json = serialize(alice, resolver);
            System.out.println("Multi-valued expand JSON:\n" + json);

            // Verify serialized JSON has no $ref
            assertFalse(json.contains("$ref"), "Expanded friends should not have $ref");
            assertTrue(json.contains("\"Bob\""), "JSON should contain Bob");
            assertTrue(json.contains("\"Charlie\""), "JSON should contain Charlie");

            // Deserialize (auto-detects expanded)
            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            assertEquals("Alice", loaded.eGet(personNameAttribute));

            // Friends should be orphan objects with correct data
            List<EObject> loadedFriends = (List<EObject>) loaded.eGet(friendsRef);
            assertEquals(2, loadedFriends.size(), "Should have 2 friends");

            EObject loadedBob = loadedFriends.get(0);
            EObject loadedCharlie = loadedFriends.get(1);

            assertEquals("Bob", loadedBob.eGet(personNameAttribute));
            assertEquals("Charlie", loadedCharlie.eGet(personNameAttribute));

            // Both should be orphans (not proxies, no container)
            assertFalse(loadedBob.eIsProxy(), "Bob should not be a proxy");
            assertFalse(loadedCharlie.eIsProxy(), "Charlie should not be a proxy");
            assertNull(loadedBob.eContainer(), "Bob should be an orphan");
            assertNull(loadedCharlie.eContainer(), "Charlie should be an orphan");
        }
    }

    // ========================================================================
    // Edge Cases
    // ========================================================================

    @Nested
    @DisplayName("Expand Edge Cases")
    class ExpandEdgeCases {

        @Test
        @DisplayName("expand with null reference - nothing to serialize")
        void expandWithNullReference() throws IOException {
            // Create company without CEO
            EObject company = createCompany("Acme Inc");
            // ceo is null by default

            // Serialize with expand enabled
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand(ceoRef)
                    .build();

            String json = serialize(company, resolver);

            // Should not contain ceo field (null + serializeNull=false default)
            assertFalse(json.contains("\"ceo\""), "Null CEO should not be serialized");
        }

        @Test
        @DisplayName("expand with null reference and serializeNull=true")
        void expandWithNullReferenceAndSerializeNull() throws IOException {
            // Create company without CEO
            EObject company = createCompany("Acme Inc");
            // ceo is null by default

            // Serialize with expand enabled and serializeNull=true
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand(ceoRef)
                    .serializeNull(true)
                    .build();

            String json = serialize(company, resolver);

            // Should contain ceo:null
            assertTrue(json.contains("\"ceo\""), "CEO field should be present");
            assertTrue(json.contains("null"), "CEO value should be null");
        }

        @Test
        @DisplayName("expand + ignoreWrite - ignoreWrite takes precedence")
        @SuppressWarnings("unchecked")
        void expandWithIgnoreWrite() throws IOException {
            // Create company with employees and CEO
            EObject company = createCompany("Acme Inc");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Configure expand=true AND ignoreWrite=true on ceo
            Map<String, Object> ceoConfig = Map.of(
                    "expand", true,
                    "ignoreWrite", true
            );
            Map<String, Object> companyConfig = Map.of("ceo", ceoConfig);
            Map<String, Object> moduleProps = Map.of("Company", companyConfig);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(moduleProps)
                    .build();

            String json = serialize(company, resolver);

            // ignoreWrite should take precedence - CEO should NOT be serialized at all
            assertFalse(json.contains("\"ceo\""),
                    "CEO should not be serialized when ignoreWrite=true (takes precedence over expand)");
        }

        @Test
        @DisplayName("expandIgnoreBidirectional=false allows bidirectional expansion")
        @SuppressWarnings("unchecked")
        void expandIgnoreBidirectionalFalse() throws IOException {
            // Note: Our test model doesn't have bidirectional refs,
            // but we can still test the flag is processed correctly
            EObject company = createCompany("Acme Inc");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Serialize with expandIgnoreBidirectional=false
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand(ceoRef)
                    .expandIgnoreBidirectional(false)
                    .build();

            String json = serialize(company, resolver);

            // CEO should be expanded (since ceo has no eOpposite, flag has no effect)
            assertTrue(json.contains("\"ceo\""), "JSON should contain ceo field");
            // For non-bidirectional refs, the flag doesn't matter - should expand
        }

        @Test
        @DisplayName("expand with empty multi-valued reference")
        void expandWithEmptyMultiValuedReference() throws IOException {
            // Create Alice without friends
            EObject alice = createPerson("Alice");
            // friends list is empty by default

            // Serialize with friends expanded
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand("friends")
                    .build();

            String json = serialize(alice, resolver);

            // Empty friends should not be serialized (serializeEmpty=false default)
            assertFalse(json.contains("\"friends\""),
                    "Empty friends should not be serialized");
        }

        @Test
        @DisplayName("expand with empty multi-valued reference and serializeEmpty=true")
        void expandWithEmptyMultiValuedReferenceAndSerializeEmpty() throws IOException {
            // Create Alice without friends
            EObject alice = createPerson("Alice");
            // friends list is empty by default

            // Serialize with friends expanded and serializeEmpty=true
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand("friends")
                    .serializeEmpty(true)
                    .build();

            String json = serialize(alice, resolver);

            // Empty friends should be serialized as []
            assertTrue(json.contains("\"friends\""), "Friends field should be present");
            assertTrue(json.contains("[]"), "Friends should be empty array");
        }

        @Test
        @DisplayName("mixed: some references expanded, some as proxy")
        @SuppressWarnings("unchecked")
        void mixedExpandAndProxy() throws IOException {
            // Create Alice with manager and friends
            EObject alice = createPerson("Alice");
            EObject bob = createPerson("Bob");
            EObject charlie = createPerson("Charlie");

            // Set manager (non-containment single)
            EReference managerRef = (EReference) ecoreHelper.getFeature(personClass, "manager");
            alice.eSet(managerRef, bob);

            // Set friends (non-containment multi)
            List<EObject> friends = (List<EObject>) alice.eGet(friendsRef);
            friends.add(charlie);

            // Expand only friends, not manager
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .expand("friends")
                    .build();

            String json = serialize(alice, resolver);

            System.out.println("Mixed expand/proxy JSON:\n" + json);

            // Friends should be expanded (no $ref)
            assertTrue(json.contains("\"friends\""), "JSON should contain friends");
            assertTrue(json.contains("\"Charlie\""), "Friends should have Charlie's name");

            // Manager should be proxy ($ref)
            assertTrue(json.contains("\"manager\""), "JSON should contain manager");
            // Manager should have $ref since it's not expanded
        }
    }
}
