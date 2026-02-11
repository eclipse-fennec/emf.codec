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
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigProperty;
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
 * Integration tests for directional feature visibility flags: ignoreRead and ignoreWrite.
 * <p>
 * These tests verify that:
 * <ul>
 *   <li>{@code ignoreWrite} - Feature is skipped during serialization but read during deserialization</li>
 *   <li>{@code ignoreRead} - Feature is skipped during deserialization but written during serialization</li>
 *   <li>{@code ignore} - Feature is skipped both ways (bidirectional)</li>
 *   <li>Combinations work correctly with both attributes and references</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Serialization</a>
 * @see <a href="docs/codec-v2-spec/12-feature-serialization.md">Spec: Feature Visibility</a>
 */
@DisplayName("Feature Visibility Integration Tests (ignoreRead/ignoreWrite)")
class FeatureVisibilityIntegrationTest {

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
        ecoreHelper = new EcoreHelper(FeatureVisibilityIntegrationTest.class);
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

    /**
     * Creates a ConfigurationResolver with feature-level ignoreWrite setting.
     */
    private ConfigurationResolver resolverWithIgnoreWrite(String featureName) {
        Map<String, Object> featureProps = Map.of(
                ConfigProperty.IGNORE_WRITE.getKey(), true
        );
        Map<String, Object> classProps = Map.of(featureName, featureProps);
        Map<String, Object> moduleProps = Map.of("Person", classProps);

        return ConfigurationResolver.builder()
                .moduleProperties(moduleProps)
                .build();
    }

    /**
     * Creates a ConfigurationResolver with feature-level ignoreRead setting.
     */
    private ConfigurationResolver resolverWithIgnoreRead(String featureName) {
        Map<String, Object> featureProps = Map.of(
                ConfigProperty.IGNORE_READ.getKey(), true
        );
        Map<String, Object> classProps = Map.of(featureName, featureProps);
        Map<String, Object> moduleProps = Map.of("Person", classProps);

        return ConfigurationResolver.builder()
                .moduleProperties(moduleProps)
                .build();
    }

    /**
     * Creates a ConfigurationResolver with feature-level ignore (bidirectional) setting.
     */
    private ConfigurationResolver resolverWithIgnore(String featureName) {
        Map<String, Object> featureProps = Map.of(
                ConfigProperty.IGNORE.getKey(), true
        );
        Map<String, Object> classProps = Map.of(featureName, featureProps);
        Map<String, Object> moduleProps = Map.of("Person", classProps);

        return ConfigurationResolver.builder()
                .moduleProperties(moduleProps)
                .build();
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://visibility-test.json"),
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
                URI.createURI("test://visibility-test.json"),
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
    // ignoreWrite Tests - Attribute
    // ========================================================================

    @Nested
    @DisplayName("ignoreWrite on Attributes")
    class IgnoreWriteAttributeTests {

        @Test
        @DisplayName("attribute with ignoreWrite is excluded from serialization")
        void attributeWithIgnoreWriteExcludedFromSerialization() throws IOException {
            EObject person = createPerson("John", 30, true, 85.5);

            ConfigurationResolver resolver = resolverWithIgnoreWrite("age");

            String json = serialize(person, resolver);
            System.out.println("JSON with age ignoreWrite:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be serialized");
            assertFalse(json.contains("\"age\""), "age should NOT be serialized (ignoreWrite)");
            assertTrue(json.contains("\"active\""), "active should be serialized");
            assertTrue(json.contains("\"score\""), "score should be serialized");
        }

        @Test
        @DisplayName("attribute with ignoreWrite is still deserialized")
        void attributeWithIgnoreWriteStillDeserialized() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "John",
                    "age": 30,
                    "active": true,
                    "score": 85.5
                }
                """;

            ConfigurationResolver resolver = resolverWithIgnoreWrite("age");

            EObject person = deserialize(json, personClass, resolver);

            assertNotNull(person, "Person should be deserialized");
            assertEquals("John", person.eGet(nameAttribute));
            assertEquals(30, person.eGet(ageAttribute), "age should be read despite ignoreWrite");
            assertEquals(true, person.eGet(activeAttribute));
            assertEquals(85.5, person.eGet(scoreAttribute));
        }

        @Test
        @DisplayName("multiple attributes with ignoreWrite")
        void multipleAttributesWithIgnoreWrite() throws IOException {
            // Note: Using active=true (non-default) so it gets serialized
            // active=false (the default) would be skipped due to serializeDefaults=false
            EObject person = createPerson("Jane", 25, true, 92.0);

            // Configure multiple features with ignoreWrite
            Map<String, Object> ageProps = Map.of(ConfigProperty.IGNORE_WRITE.getKey(), true);
            Map<String, Object> scoreProps = Map.of(ConfigProperty.IGNORE_WRITE.getKey(), true);
            Map<String, Object> classProps = Map.of(
                    "age", ageProps,
                    "score", scoreProps
            );
            Map<String, Object> moduleProps = Map.of("Person", classProps);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(moduleProps)
                    .build();

            String json = serialize(person, resolver);
            System.out.println("JSON with age and score ignoreWrite:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be serialized");
            assertFalse(json.contains("\"age\""), "age should NOT be serialized");
            assertTrue(json.contains("\"active\""), "active should be serialized");
            assertFalse(json.contains("\"score\""), "score should NOT be serialized");
        }
    }

    // ========================================================================
    // ignoreRead Tests - Attribute
    // ========================================================================

    @Nested
    @DisplayName("ignoreRead on Attributes")
    class IgnoreReadAttributeTests {

        @Test
        @DisplayName("attribute with ignoreRead is still serialized")
        void attributeWithIgnoreReadStillSerialized() throws IOException {
            EObject person = createPerson("John", 30, true, 85.5);

            ConfigurationResolver resolver = resolverWithIgnoreRead("age");

            String json = serialize(person, resolver);
            System.out.println("JSON with age ignoreRead:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be serialized");
            assertTrue(json.contains("\"age\""), "age should be serialized despite ignoreRead");
            assertTrue(json.contains("\"active\""), "active should be serialized");
            assertTrue(json.contains("\"score\""), "score should be serialized");
        }

        @Test
        @DisplayName("attribute with ignoreRead is excluded from deserialization")
        void attributeWithIgnoreReadExcludedFromDeserialization() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "John",
                    "age": 30,
                    "active": true,
                    "score": 85.5
                }
                """;

            ConfigurationResolver resolver = resolverWithIgnoreRead("age");

            EObject person = deserialize(json, personClass, resolver);

            assertNotNull(person, "Person should be deserialized");
            assertEquals("John", person.eGet(nameAttribute));
            assertEquals(0, person.eGet(ageAttribute), "age should retain default (ignoreRead)");
            assertEquals(true, person.eGet(activeAttribute));
            assertEquals(85.5, person.eGet(scoreAttribute));
        }

        @Test
        @DisplayName("multiple attributes with ignoreRead")
        void multipleAttributesWithIgnoreRead() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "Jane",
                    "age": 25,
                    "active": false,
                    "score": 92.0
                }
                """;

            // Configure multiple features with ignoreRead
            Map<String, Object> ageProps = Map.of(ConfigProperty.IGNORE_READ.getKey(), true);
            Map<String, Object> scoreProps = Map.of(ConfigProperty.IGNORE_READ.getKey(), true);
            Map<String, Object> classProps = Map.of(
                    "age", ageProps,
                    "score", scoreProps
            );
            Map<String, Object> moduleProps = Map.of("Person", classProps);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(moduleProps)
                    .build();

            EObject person = deserialize(json, personClass, resolver);

            assertNotNull(person, "Person should be deserialized");
            assertEquals("Jane", person.eGet(nameAttribute));
            assertEquals(0, person.eGet(ageAttribute), "age should retain default (ignoreRead)");
            assertEquals(false, person.eGet(activeAttribute));
            assertEquals(0.0, person.eGet(scoreAttribute), "score should retain default (ignoreRead)");
        }
    }

    // ========================================================================
    // ignoreWrite Tests - Reference (Containment)
    // ========================================================================

    @Nested
    @DisplayName("ignoreWrite on Containment References")
    class IgnoreWriteContainmentReferenceTests {

        @Test
        @DisplayName("containment reference with ignoreWrite is excluded from serialization")
        void containmentRefWithIgnoreWriteExcludedFromSerialization() throws IOException {
            EObject person = createPerson("Bob", 40, true, 75.0);
            EObject address = createAddress("123 Main St", "Springfield");
            person.eSet(addressRef, address);

            ConfigurationResolver resolver = resolverWithIgnoreWrite("address");

            String json = serialize(person, resolver);
            System.out.println("JSON with address ignoreWrite:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be serialized");
            assertFalse(json.contains("\"address\""), "address should NOT be serialized (ignoreWrite)");
            assertFalse(json.contains("\"street\""), "nested street should not appear");
            assertFalse(json.contains("\"city\""), "nested city should not appear");
        }

        @Test
        @DisplayName("containment reference with ignoreWrite is still deserialized")
        void containmentRefWithIgnoreWriteStillDeserialized() throws IOException {
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

            ConfigurationResolver resolver = resolverWithIgnoreWrite("address");

            EObject person = deserialize(json, personClass, resolver);

            assertNotNull(person, "Person should be deserialized");
            assertEquals("Bob", person.eGet(nameAttribute));
            EObject address = (EObject) person.eGet(addressRef);
            assertNotNull(address, "address should be deserialized despite ignoreWrite");
            assertEquals("123 Main St", address.eGet(addressClass.getEStructuralFeature("street")));
            assertEquals("Springfield", address.eGet(addressClass.getEStructuralFeature("city")));
        }
    }

    // ========================================================================
    // ignoreRead Tests - Reference (Containment)
    // ========================================================================

    @Nested
    @DisplayName("ignoreRead on Containment References")
    class IgnoreReadContainmentReferenceTests {

        @Test
        @DisplayName("containment reference with ignoreRead is still serialized")
        void containmentRefWithIgnoreReadStillSerialized() throws IOException {
            EObject person = createPerson("Bob", 40, true, 75.0);
            EObject address = createAddress("123 Main St", "Springfield");
            person.eSet(addressRef, address);

            ConfigurationResolver resolver = resolverWithIgnoreRead("address");

            String json = serialize(person, resolver);
            System.out.println("JSON with address ignoreRead:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be serialized");
            assertTrue(json.contains("\"address\""), "address should be serialized despite ignoreRead");
            assertTrue(json.contains("\"street\""), "nested street should appear");
            assertTrue(json.contains("\"city\""), "nested city should appear");
        }

        @Test
        @DisplayName("containment reference with ignoreRead is excluded from deserialization")
        void containmentRefWithIgnoreReadExcludedFromDeserialization() throws IOException {
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

            ConfigurationResolver resolver = resolverWithIgnoreRead("address");

            EObject person = deserialize(json, personClass, resolver);

            assertNotNull(person, "Person should be deserialized");
            assertEquals("Bob", person.eGet(nameAttribute));
            assertNull(person.eGet(addressRef), "address should be null (ignoreRead)");
        }
    }

    // ========================================================================
    // ignore (bidirectional) Tests
    // ========================================================================

    @Nested
    @DisplayName("ignore (bidirectional)")
    class IgnoreBidirectionalTests {

        @Test
        @DisplayName("attribute with ignore is excluded from both directions")
        void attributeWithIgnoreExcludedBothDirections() throws IOException {
            EObject person = createPerson("Alice", 35, true, 88.0);

            ConfigurationResolver resolver = resolverWithIgnore("age");

            // Test serialization
            String json = serialize(person, resolver);
            System.out.println("JSON with age ignore:\n" + json);
            assertFalse(json.contains("\"age\""), "age should NOT be serialized (ignore)");

            // Test deserialization
            String jsonWithAge = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "Alice",
                    "age": 99,
                    "active": true,
                    "score": 88.0
                }
                """;

            EObject loaded = deserialize(jsonWithAge, personClass, resolver);
            assertEquals(0, loaded.eGet(ageAttribute), "age should retain default (ignore)");
        }

        @Test
        @DisplayName("reference with ignore is excluded from both directions")
        void referenceWithIgnoreExcludedBothDirections() throws IOException {
            EObject person = createPerson("Bob", 40, true, 75.0);
            EObject address = createAddress("123 Main St", "Springfield");
            person.eSet(addressRef, address);

            ConfigurationResolver resolver = resolverWithIgnore("address");

            // Test serialization
            String json = serialize(person, resolver);
            assertFalse(json.contains("\"address\""), "address should NOT be serialized (ignore)");

            // Test deserialization
            String jsonWithAddress = """
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

            EObject loaded = deserialize(jsonWithAddress, personClass, resolver);
            assertNull(loaded.eGet(addressRef), "address should be null (ignore)");
        }
    }

    // ========================================================================
    // Comparison: ignore vs ignoreWrite vs ignoreRead
    // ========================================================================

    @Nested
    @DisplayName("Comparison: ignore vs ignoreWrite vs ignoreRead")
    class ComparisonTests {

        @Test
        @DisplayName("ignore=true is equivalent to ignoreRead=true AND ignoreWrite=true")
        void ignoreEquivalentToBothDirections() throws IOException {
            EObject person = createPerson("Test", 50, true, 100.0);

            String jsonWithAge = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "Test",
                    "age": 50,
                    "active": true,
                    "score": 100.0
                }
                """;

            // Test with ignore=true
            ConfigurationResolver ignoreResolver = resolverWithIgnore("age");
            String jsonIgnore = serialize(person, ignoreResolver);
            EObject loadedIgnore = deserialize(jsonWithAge, personClass, ignoreResolver);

            // Test with ignoreRead=true AND ignoreWrite=true
            Map<String, Object> bothProps = Map.of(
                    ConfigProperty.IGNORE_READ.getKey(), true,
                    ConfigProperty.IGNORE_WRITE.getKey(), true
            );
            Map<String, Object> classProps = Map.of("age", bothProps);
            Map<String, Object> moduleProps = Map.of("Person", classProps);
            ConfigurationResolver bothResolver = ConfigurationResolver.builder()
                    .moduleProperties(moduleProps)
                    .build();
            String jsonBoth = serialize(person, bothResolver);
            EObject loadedBoth = deserialize(jsonWithAge, personClass, bothResolver);

            // Serialization should be the same
            assertFalse(jsonIgnore.contains("\"age\""), "ignore: age should NOT be serialized");
            assertFalse(jsonBoth.contains("\"age\""), "both: age should NOT be serialized");

            // Deserialization should be the same
            assertEquals(0, loadedIgnore.eGet(ageAttribute), "ignore: age should retain default");
            assertEquals(0, loadedBoth.eGet(ageAttribute), "both: age should retain default");
        }

        @Test
        @DisplayName("ignoreWrite affects only serialization, ignoreRead affects only deserialization")
        void directionalFlagsAffectOnlyTheirDirection() throws IOException {
            EObject person = createPerson("Mixed", 42, true, 77.0);

            String jsonWithAll = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "Mixed",
                    "age": 42,
                    "active": true,
                    "score": 77.0
                }
                """;

            // Configure age with ignoreWrite, score with ignoreRead
            Map<String, Object> ageProps = Map.of(ConfigProperty.IGNORE_WRITE.getKey(), true);
            Map<String, Object> scoreProps = Map.of(ConfigProperty.IGNORE_READ.getKey(), true);
            Map<String, Object> classProps = Map.of(
                    "age", ageProps,
                    "score", scoreProps
            );
            Map<String, Object> moduleProps = Map.of("Person", classProps);
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(moduleProps)
                    .build();

            // Serialization
            String json = serialize(person, resolver);
            System.out.println("JSON with age=ignoreWrite, score=ignoreRead:\n" + json);

            assertFalse(json.contains("\"age\""), "age should NOT be serialized (ignoreWrite)");
            assertTrue(json.contains("\"score\""), "score should be serialized (ignoreRead only affects read)");

            // Deserialization
            EObject loaded = deserialize(jsonWithAll, personClass, resolver);

            assertEquals(42, loaded.eGet(ageAttribute), "age should be read (ignoreWrite only affects write)");
            assertEquals(0.0, loaded.eGet(scoreAttribute), "score should retain default (ignoreRead)");
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip with directional ignore")
    class RoundTripTests {

        @Test
        @DisplayName("round-trip with ignoreWrite loses data on serialization")
        void roundTripWithIgnoreWriteLosesData() throws IOException {
            EObject person = createPerson("RoundTrip", 55, true, 90.0);

            ConfigurationResolver resolver = resolverWithIgnoreWrite("age");

            // Serialize - age will be lost
            String json = serialize(person, resolver);
            assertFalse(json.contains("\"age\""));

            // Deserialize - age cannot be restored (not in JSON)
            EObject loaded = deserialize(json, personClass, resolver);

            assertEquals("RoundTrip", loaded.eGet(nameAttribute));
            assertEquals(0, loaded.eGet(ageAttribute), "age lost during round-trip (ignoreWrite)");
            assertEquals(true, loaded.eGet(activeAttribute));
            assertEquals(90.0, loaded.eGet(scoreAttribute));
        }

        @Test
        @DisplayName("round-trip with ignoreRead loses data on deserialization")
        void roundTripWithIgnoreReadLosesData() throws IOException {
            EObject person = createPerson("RoundTrip", 55, true, 90.0);

            ConfigurationResolver resolver = resolverWithIgnoreRead("age");

            // Serialize - age is included
            String json = serialize(person, resolver);
            assertTrue(json.contains("\"age\""));

            // Deserialize - age is skipped
            EObject loaded = deserialize(json, personClass, resolver);

            assertEquals("RoundTrip", loaded.eGet(nameAttribute));
            assertEquals(0, loaded.eGet(ageAttribute), "age lost during round-trip (ignoreRead)");
            assertEquals(true, loaded.eGet(activeAttribute));
            assertEquals(90.0, loaded.eGet(scoreAttribute));
        }

        @Test
        @DisplayName("asymmetric round-trip: serialize without ignoreWrite, deserialize with ignoreRead")
        void asymmetricRoundTrip() throws IOException {
            EObject person = createPerson("Asymmetric", 60, true, 95.0);

            // Serialize with default resolver (all features written)
            ConfigurationResolver defaultResolver = ConfigurationResolver.defaults();
            String json = serialize(person, defaultResolver);
            assertTrue(json.contains("\"age\":60"));

            // Deserialize with ignoreRead on age
            ConfigurationResolver ignoreReadResolver = resolverWithIgnoreRead("age");
            EObject loaded = deserialize(json, personClass, ignoreReadResolver);

            assertEquals("Asymmetric", loaded.eGet(nameAttribute));
            assertEquals(0, loaded.eGet(ageAttribute), "age ignored during load");
            assertEquals(true, loaded.eGet(activeAttribute));
            assertEquals(95.0, loaded.eGet(scoreAttribute));
        }
    }

    // ========================================================================
    // Edge Cases
    // ========================================================================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("ignoreWrite on non-existent feature has no effect")
        void ignoreWriteOnNonExistentFeature() throws IOException {
            EObject person = createPerson("Test", 20, true, 50.0);

            ConfigurationResolver resolver = resolverWithIgnoreWrite("nonExistentFeature");

            String json = serialize(person, resolver);

            // All features should be serialized
            assertTrue(json.contains("\"name\""));
            assertTrue(json.contains("\"age\""));
            assertTrue(json.contains("\"active\""));
            assertTrue(json.contains("\"score\""));
        }

        @Test
        @DisplayName("ignoreRead on non-existent feature has no effect")
        void ignoreReadOnNonExistentFeature() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "Test",
                    "age": 20,
                    "active": true,
                    "score": 50.0
                }
                """;

            ConfigurationResolver resolver = resolverWithIgnoreRead("nonExistentFeature");

            EObject person = deserialize(json, personClass, resolver);

            // All features should be deserialized
            assertEquals("Test", person.eGet(nameAttribute));
            assertEquals(20, person.eGet(ageAttribute));
            assertEquals(true, person.eGet(activeAttribute));
            assertEquals(50.0, person.eGet(scoreAttribute));
        }

        @Test
        @DisplayName("ignoreWrite with null value - nothing to ignore")
        void ignoreWriteWithNullValue() throws IOException {
            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(nameAttribute, "NullTest");
            // age, active, score are defaults; address is null

            ConfigurationResolver resolver = resolverWithIgnoreWrite("address");

            String json = serialize(person, resolver);

            // address was null anyway, so ignoreWrite has no visible effect
            assertTrue(json.contains("\"name\""));
            assertFalse(json.contains("\"address\""), "null address not serialized by default");
        }
    }
}
