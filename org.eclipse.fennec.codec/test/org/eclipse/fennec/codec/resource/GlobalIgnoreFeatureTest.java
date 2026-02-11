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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
 * Integration tests for Global Feature Ignore List.
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.GlobalIgnoreFeatureTest}.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/03-global-options.md#4-global-feature-ignore-list">Spec: Global Feature Ignore List</a>
 */
@DisplayName("Global Feature Ignore List Tests")
class GlobalIgnoreFeatureTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass personClass;
    private EClass addressClass;

    // EAttributes on Person
    private EAttribute nameAttribute;
    private EAttribute ageAttribute;
    private EAttribute activeAttribute;
    private EAttribute scoreAttribute;

    // EReferences on Person
    private EReference addressRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(GlobalIgnoreFeatureTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = ecoreHelper.getEClass(testPackage, "Person");
        addressClass = ecoreHelper.getEClass(testPackage, "Address");

        nameAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        ageAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "age");
        activeAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "active");
        scoreAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "score");

        addressRef = (EReference) ecoreHelper.getFeature(personClass, "address");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private EObject createPerson(String name, int age, boolean active, double score) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttribute, name);
        person.eSet(ageAttribute, age);
        person.eSet(activeAttribute, active);
        person.eSet(scoreAttribute, score);
        return person;
    }

    private EObject createAddress(String street, String city) {
        EObject address = testPackage.getEFactoryInstance().create(addressClass);
        address.eSet(addressClass.getEStructuralFeature("street"), street);
        address.eSet(addressClass.getEStructuralFeature("city"), city);
        return address;
    }

    private ConfigurationResolver resolverWithIgnore(String... featureNames) {
        return ConfigurationResolver.builder()
                .moduleProperties(Map.of("ignoreFeatures", List.of(featureNames)))
                .build();
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://global-ignore-test.json"),
                metadataService,
                resolver,
                null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootType, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://global-ignore-test.json"),
                metadataService,
                resolver,
                null);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    // ========================================================================
    // Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Serialization with global ignore")
    class SerializationTests {

        @Test
        @DisplayName("single feature ignored in serialization")
        void singleFeatureIgnored() throws IOException {
            EObject person = createPerson("John", 30, true, 85.5);

            ConfigurationResolver resolver = resolverWithIgnore("age");

            String json = serialize(person, resolver);
            System.out.println("JSON with age ignored:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be serialized");
            assertFalse(json.contains("\"age\""), "age should be ignored");
            assertTrue(json.contains("\"active\""), "active should be serialized");
            assertTrue(json.contains("\"score\""), "score should be serialized");
        }

        @Test
        @DisplayName("multiple features ignored in serialization")
        void multipleFeaturesIgnored() throws IOException {
            EObject person = createPerson("Jane", 25, false, 92.0);

            ConfigurationResolver resolver = resolverWithIgnore("age", "score", "active");

            String json = serialize(person, resolver);
            System.out.println("JSON with multiple fields ignored:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be serialized");
            assertFalse(json.contains("\"age\""), "age should be ignored");
            assertFalse(json.contains("\"active\""), "active should be ignored");
            assertFalse(json.contains("\"score\""), "score should be ignored");
        }

        @Test
        @DisplayName("reference feature ignored in serialization")
        void referenceFeatureIgnored() throws IOException {
            EObject person = createPerson("Bob", 40, true, 75.0);
            EObject address = createAddress("123 Main St", "Springfield");
            person.eSet(addressRef, address);

            ConfigurationResolver resolver = resolverWithIgnore("address");

            String json = serialize(person, resolver);
            System.out.println("JSON with address ignored:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be serialized");
            assertFalse(json.contains("\"address\""), "address should be ignored");
            assertFalse(json.contains("\"street\""), "nested street should not appear");
        }

        @Test
        @DisplayName("no features ignored - default behavior")
        void noFeaturesIgnored() throws IOException {
            EObject person = createPerson("Alice", 35, true, 88.0);

            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            String json = serialize(person, resolver);
            System.out.println("JSON with no fields ignored:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be serialized");
            assertTrue(json.contains("\"age\""), "age should be serialized");
            assertTrue(json.contains("\"active\""), "active should be serialized");
            assertTrue(json.contains("\"score\""), "score should be serialized");
        }
    }

    // ========================================================================
    // Deserialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Deserialization with global ignore")
    class DeserializationTests {

        @Test
        @DisplayName("ignored field in JSON is silently skipped")
        void ignoredFieldSkipped() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "John",
                    "age": 30,
                    "active": true,
                    "score": 85.5
                }
                """;

            ConfigurationResolver resolver = resolverWithIgnore("age");

            EObject person = deserialize(json, personClass, resolver);

            assertNotNull(person, "Person should be deserialized");
            assertEquals("John", person.eGet(nameAttribute), "name should be set");
            assertEquals(0, person.eGet(ageAttribute), "age should retain default value (ignored)");
            assertEquals(true, person.eGet(activeAttribute), "active should be set");
            assertEquals(85.5, person.eGet(scoreAttribute), "score should be set");
        }

        @Test
        @DisplayName("multiple ignored fields in JSON are skipped")
        void multipleIgnoredFieldsSkipped() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "Jane",
                    "age": 25,
                    "active": false,
                    "score": 92.0
                }
                """;

            ConfigurationResolver resolver = resolverWithIgnore("age", "score");

            EObject person = deserialize(json, personClass, resolver);

            assertNotNull(person, "Person should be deserialized");
            assertEquals("Jane", person.eGet(nameAttribute), "name should be set");
            assertEquals(0, person.eGet(ageAttribute), "age should retain default (ignored)");
            assertEquals(false, person.eGet(activeAttribute), "active should be set");
            assertEquals(0.0, person.eGet(scoreAttribute), "score should retain default (ignored)");
        }

        @Test
        @DisplayName("ignored reference field in JSON is skipped")
        void ignoredReferenceSkipped() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "Bob",
                    "age": 40,
                    "address": {
                        "_type": "http://test.example.org/roundtrip/1.0#//Address",
                        "street": "123 Main St",
                        "city": "Springfield"
                    }
                }
                """;

            ConfigurationResolver resolver = resolverWithIgnore("address");

            EObject person = deserialize(json, personClass, resolver);

            assertNotNull(person, "Person should be deserialized");
            assertEquals("Bob", person.eGet(nameAttribute), "name should be set");
            assertEquals(40, person.eGet(ageAttribute), "age should be set");
            assertNull(person.eGet(addressRef), "address should be null (ignored)");
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-trip with global ignore")
    class RoundTripTests {

        @Test
        @DisplayName("round-trip preserves non-ignored fields")
        void roundTripPreservesNonIgnoredFields() throws IOException {
            EObject person = createPerson("Alice", 35, true, 88.0);

            ConfigurationResolver resolver = resolverWithIgnore("age");

            // Serialize
            String json = serialize(person, resolver);
            System.out.println("Serialized JSON:\n" + json);

            // Deserialize
            EObject deserialized = deserialize(json, personClass, resolver);

            assertNotNull(deserialized, "Should deserialize successfully");
            assertEquals("Alice", deserialized.eGet(nameAttribute), "name should round-trip");
            assertEquals(0, deserialized.eGet(ageAttribute), "age should be default (ignored both ways)");
            assertEquals(true, deserialized.eGet(activeAttribute), "active should round-trip");
            assertEquals(88.0, deserialized.eGet(scoreAttribute), "score should round-trip");
        }

        @Test
        @DisplayName("API versioning use case - V1 ignores V2 fields")
        void apiVersioningUseCase() throws IOException {
            String v2Json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "John",
                    "age": 30,
                    "score": 85.5,
                    "active": true
                }
                """;

            ConfigurationResolver v1Resolver = resolverWithIgnore("score");

            EObject person = deserialize(v2Json, personClass, v1Resolver);

            assertNotNull(person, "V1 should deserialize V2 JSON");
            assertEquals("John", person.eGet(nameAttribute));
            assertEquals(30, person.eGet(ageAttribute));
            assertEquals(0.0, person.eGet(scoreAttribute), "score ignored in V1");
        }
    }

    // ========================================================================
    // Edge Cases
    // ========================================================================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("ignoring non-existent feature name has no effect")
        void ignoreNonExistentFeature() throws IOException {
            EObject person = createPerson("Test", 20, true, 50.0);

            ConfigurationResolver resolver = resolverWithIgnore("nonExistentFeature");

            String json = serialize(person, resolver);

            assertTrue(json.contains("\"name\""));
            assertTrue(json.contains("\"age\""));
            assertTrue(json.contains("\"active\""));
            assertTrue(json.contains("\"score\""));
        }

        @Test
        @DisplayName("empty ignore list serializes all features")
        void emptyIgnoreList() throws IOException {
            EObject person = createPerson("Test", 20, true, 50.0);

            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            String json = serialize(person, resolver);

            assertTrue(json.contains("\"name\""));
            assertTrue(json.contains("\"age\""));
        }
    }
}
