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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for PLAIN reference format deserialization and serialization.
 * <p>
 * PLAIN format for references carries only the reference value (URI/ID) as a bare string,
 * without type information. This test verifies:
 * <ul>
 *   <li>Deserialization with concrete declared type (uses EReference.getEReferenceType())</li>
 *   <li>Deserialization with CODEC_FEATURE_TYPE_HINTS (runtime type hint)</li>
 *   <li>Serialization behavior</li>
 *   <li>Single and multi-valued references in PLAIN format</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/10-reference.md#11-plain-strategy">Spec: PLAIN Strategy</a>
 * @see <a href="docs/codec-v2-spec/13-load-save-options.md#3-feature-type-hints">Spec: Feature Type Hints</a>
 */
@DisplayName("PLAIN Reference Format Tests")
class PlainReferenceFormatTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

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
    private EReference friendsRef;       // Person.friends (multi, non-containment)
    private EReference employeesRef;     // Company.employees (multi, containment)
    private EReference ceoRef;           // Company.ceo (single, non-containment)

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, PlainReferenceFormatTest.class);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        companyClass = EcoreHelper.getEClass(testPackage, "Company");

        personNameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        companyNameAttribute = (EAttribute) EcoreHelper.getFeature(companyClass, "name");

        friendsRef = (EReference) EcoreHelper.getFeature(personClass, "friends");
        employeesRef = (EReference) EcoreHelper.getFeature(companyClass, "employees");
        ceoRef = (EReference) EcoreHelper.getFeature(companyClass, "ceo");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helper Methods
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

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://plain-ref-test.json"),
                metadataService,
                resolver,
                null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootType, ConfigurationResolver resolver,
                                 Map<String, Object> additionalOptions) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://plain-ref-test.json"),
                metadataService,
                resolver,
                null);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        if (additionalOptions != null) {
            options.putAll(additionalOptions);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private EObject deserialize(String json, EClass rootType) throws IOException {
        return deserialize(json, rootType, ConfigurationResolver.defaults(), null);
    }

    // ========================================================================
    // PLAIN Deserialization Tests - Single-Valued
    // ========================================================================

    @Nested
    @DisplayName("PLAIN Deserialization - Single-Valued Reference")
    class PlainDeserializationSingleValued {

        @Test
        @DisplayName("deserializes single-valued PLAIN reference using declared type")
        void deserializesSingleValuedPlainReference() throws IOException {
            // JSON with PLAIN format: ceo is a bare string (URI)
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
                    "ceo": "//@employees.0"
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            assertEquals("Acme Inc", loaded.eGet(companyNameAttribute));

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(1, employees.size());

            // CEO should be resolved to the first employee
            EObject ceo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(ceo, "CEO should be resolved from PLAIN reference");
            assertSame(employees.get(0), ceo, "CEO should be same instance as first employee");
        }

        @Test
        @DisplayName("PLAIN format is auto-detected from string token")
        void plainFormatAutoDetectedFromStringToken() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Company",
                    "name": "TechCorp",
                    "employees": [
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Bob"
                        }
                    ],
                    "ceo": "//@employees.0"
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            EObject ceo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(ceo, "CEO should be deserialized from PLAIN format string");
        }
    }

    // ========================================================================
    // PLAIN Deserialization Tests - Multi-Valued
    // ========================================================================

    @Nested
    @DisplayName("PLAIN Deserialization - Multi-Valued Reference")
    class PlainDeserializationMultiValued {

        @Test
        @DisplayName("deserializes multi-valued PLAIN reference array")
        void deserializesMultiValuedPlainReferenceArray() throws IOException {
            // JSON with PLAIN format: friends is an array of bare strings
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Company",
                    "name": "Acme Inc",
                    "employees": [
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Alice"
                        },
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Bob"
                        },
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Charlie",
                            "friends": [
                                "//@employees.0",
                                "//@employees.1"
                            ]
                        }
                    ]
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(3, employees.size());

            // Charlie's friends should be resolved
            EObject charlie = employees.get(2);
            @SuppressWarnings("unchecked")
            List<EObject> friends = (List<EObject>) charlie.eGet(friendsRef);
            assertEquals(2, friends.size(), "Charlie should have 2 friends");
            assertSame(employees.get(0), friends.get(0), "First friend should be Alice");
            assertSame(employees.get(1), friends.get(1), "Second friend should be Bob");
        }
    }

    // ========================================================================
    // PLAIN Deserialization Tests - With Type Hints
    // ========================================================================

    @Nested
    @DisplayName("PLAIN Deserialization with CODEC_FEATURE_TYPE_HINTS")
    class PlainDeserializationWithTypeHints {

        @Test
        @DisplayName("uses CODEC_FEATURE_TYPE_HINTS for type resolution")
        void usesFeatureTypeHintsForTypeResolution() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Company",
                    "name": "StartupCo",
                    "employees": [
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Charlie"
                        }
                    ],
                    "ceo": "//@employees.0"
                }
                """;

            // Provide type hint for the ceo reference
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(ceoRef, personClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            EObject loaded = deserialize(json, companyClass, ConfigurationResolver.defaults(), options);

            assertNotNull(loaded);
            EObject ceo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(ceo, "CEO should be resolved using type hint");
        }

        @Test
        @DisplayName("CODEC_FEATURE_TYPE_HINTS takes precedence over declared type")
        void typeHintTakesPrecedenceOverDeclaredType() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Company",
                    "name": "PriorityCorp",
                    "employees": [
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Diana"
                        }
                    ],
                    "ceo": "//@employees.0"
                }
                """;

            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(ceoRef, personClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            EObject loaded = deserialize(json, companyClass, ConfigurationResolver.defaults(), options);

            assertNotNull(loaded);
            EObject ceo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(ceo);
            assertEquals(personClass, ceo.eClass(), "CEO should use type from hint");
        }
    }

    // ========================================================================
    // Mixed Format Tests
    // ========================================================================

    @Nested
    @DisplayName("Mixed PLAIN and STRUCTURED Format")
    class MixedFormatTests {

        @Test
        @DisplayName("deserializes mixed PLAIN and STRUCTURED references in same document")
        void deserializesMixedFormats() throws IOException {
            // JSON with mixed formats - PLAIN for ceo, STRUCTURED inline for employees
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Company",
                    "name": "MixedCorp",
                    "employees": [
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Eve"
                        },
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Frank"
                        }
                    ],
                    "ceo": "//@employees.1"
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            assertEquals("MixedCorp", loaded.eGet(companyNameAttribute));

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(2, employees.size());
            assertEquals("Eve", employees.get(0).eGet(personNameAttribute));
            assertEquals("Frank", employees.get(1).eGet(personNameAttribute));

            // CEO should be resolved from PLAIN format to Frank
            EObject ceo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(ceo, "CEO should be resolved from PLAIN reference");
            assertSame(employees.get(1), ceo, "CEO should be Frank (second employee)");
        }

        @Test
        @DisplayName("STRUCTURED reference in array alongside PLAIN references")
        void structuredAndPlainInArray() throws IOException {
            // JSON where friends array has mixed formats
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Company",
                    "name": "MixedArrayCorp",
                    "employees": [
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Alice"
                        },
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Bob"
                        },
                        {
                            "_type": "http://test.example.org/roundtrip/1.0#//Person",
                            "name": "Grace",
                            "friends": [
                                "//@employees.0",
                                { "_type": "http://test.example.org/roundtrip/1.0#//Person", "$ref": "//@employees.1" }
                            ]
                        }
                    ]
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(3, employees.size());

            EObject grace = employees.get(2);
            assertEquals("Grace", grace.eGet(personNameAttribute));

            @SuppressWarnings("unchecked")
            List<EObject> friends = (List<EObject>) grace.eGet(friendsRef);
            assertEquals(2, friends.size(), "Both PLAIN and STRUCTURED references should be parsed");
            assertSame(employees.get(0), friends.get(0), "First friend (PLAIN) should be Alice");
            assertSame(employees.get(1), friends.get(1), "Second friend (STRUCTURED) should be Bob");
        }
    }

    // ========================================================================
    // Edge Cases
    // ========================================================================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("handles null PLAIN reference")
        void handlesNullPlainReference() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Company",
                    "name": "NullCorp",
                    "employees": [],
                    "ceo": null
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            assertEquals("NullCorp", loaded.eGet(companyNameAttribute));
            assertNull(loaded.eGet(ceoRef), "Null reference should remain null");
        }

        @Test
        @DisplayName("handles empty multi-valued PLAIN reference array")
        void handlesEmptyPlainReferenceArray() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "Lonely",
                    "friends": []
                }
                """;

            EObject loaded = deserialize(json, personClass);

            assertNotNull(loaded);
            @SuppressWarnings("unchecked")
            List<EObject> friends = (List<EObject>) loaded.eGet(friendsRef);
            assertTrue(friends.isEmpty(), "Empty array should result in empty list");
        }

        @Test
        @DisplayName("PLAIN reference with cross-document URI creates proxy")
        void plainReferenceWithCrossDocumentUri() throws IOException {
            // PLAIN reference pointing to another document
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Company",
                    "name": "CrossDocCorp",
                    "employees": [],
                    "ceo": "other-file.json#//@persons.0"
                }
                """;

            EObject loaded = deserialize(json, companyClass);

            assertNotNull(loaded);
            EObject ceo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(ceo, "Cross-document reference should create proxy");
            assertTrue(ceo.eIsProxy(), "Cross-document PLAIN reference should be a proxy");
        }
    }

    // ========================================================================
    // PLAIN Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("PLAIN Serialization")
    class PlainSerializationTests {

        @Test
        @DisplayName("serializes single-valued reference in PLAIN format when configured")
        @SuppressWarnings("unchecked")
        void serializesSingleValuedPlainReference() throws IOException {
            // Create company with employee and CEO
            EObject company = createCompany("PlainCorp");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Configure PLAIN format for references via optionsProperties
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("refFormat", "PLAIN"))
                    .build();

            String json = serialize(company, resolver);
            System.out.println("PLAIN serialization output:\n" + json);

            // Verify PLAIN format is used (bare string, no object wrapper)
            assertTrue(json.contains("\"ceo\""), "JSON should contain ceo field");
            // PLAIN format should NOT have $ref inside an object for ceo
            // Check that ceo is followed by a string value, not an object
            assertTrue(json.matches("(?s).*\"ceo\"\\s*:\\s*\"[^{].*"),
                    "PLAIN format should write bare URI string, not object");
        }

        @Test
        @DisplayName("serializes multi-valued reference in PLAIN format when configured")
        @SuppressWarnings("unchecked")
        void serializesMultiValuedPlainReference() throws IOException {
            // Create person with friends
            EObject alice = createPerson("Alice");
            EObject bob = createPerson("Bob");
            EObject charlie = createPerson("Charlie");

            // Put all in a company so they have a resource
            EObject company = createCompany("FriendsCorp");
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            employees.add(bob);
            employees.add(charlie);

            // Set friends for Alice
            List<EObject> friends = (List<EObject>) alice.eGet(friendsRef);
            friends.add(bob);
            friends.add(charlie);

            // Configure PLAIN format
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("refFormat", "PLAIN"))
                    .build();

            String json = serialize(company, resolver);
            System.out.println("PLAIN multi-valued serialization output:\n" + json);

            // Verify PLAIN format for multi-valued: array of strings
            assertTrue(json.contains("\"friends\""), "JSON should contain friends field");
            // Friends array should contain URI strings, not objects with $ref
        }

        @Test
        @DisplayName("default serialization uses STRUCTURED format")
        @SuppressWarnings("unchecked")
        void defaultSerializesAsStructured() throws IOException {
            // Create company with employee and CEO
            EObject company = createCompany("StructuredCorp");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Use default resolver (STRUCTURED format)
            String json = serialize(company, ConfigurationResolver.defaults());

            // Verify STRUCTURED format is used (contains $ref)
            assertTrue(json.contains("\"ceo\""), "JSON should contain ceo field");
            assertTrue(json.contains("$ref"),
                    "Default serialization should use STRUCTURED format with $ref key");
        }

        @Test
        @DisplayName("PLAIN serialization round-trips with PLAIN deserialization")
        @SuppressWarnings("unchecked")
        void plainRoundTrip() throws IOException {
            // Create company with employee and CEO
            EObject company = createCompany("RoundTripCorp");
            EObject alice = createPerson("Alice");

            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(alice);
            company.eSet(ceoRef, alice);

            // Serialize with PLAIN format
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .optionsProperties(Map.of("refFormat", "PLAIN"))
                    .build();

            String json = serialize(company, resolver);
            System.out.println("PLAIN round-trip JSON:\n" + json);

            // Deserialize (PLAIN format auto-detected)
            EObject loaded = deserialize(json, companyClass, resolver, null);

            assertNotNull(loaded);
            assertEquals("RoundTripCorp", loaded.eGet(companyNameAttribute));

            List<EObject> loadedEmployees = (List<EObject>) loaded.eGet(employeesRef);
            assertEquals(1, loadedEmployees.size());

            EObject loadedCeo = (EObject) loaded.eGet(ceoRef);
            assertNotNull(loadedCeo, "CEO should be resolved after round-trip");
            assertSame(loadedEmployees.get(0), loadedCeo, "CEO should be same instance as employee");
        }
    }
}
