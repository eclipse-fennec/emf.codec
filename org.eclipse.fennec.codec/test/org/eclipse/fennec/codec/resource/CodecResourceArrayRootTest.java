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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
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
 * Tests for array root object support.
 * <p>
 * EMF Resources support multiple root elements. When serializing to JSON:
 * <ul>
 *   <li>Single root object → JSON object</li>
 *   <li>Multiple root objects → JSON array</li>
 * </ul>
 * </p>
 * <p>
 * On deserialization, the codec automatically detects whether the root is
 * an array or object and populates the resource contents accordingly.
 * </p>
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.CodecResourceArrayRootTest}
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#187-root-element--multiple-contents">Spec 18.7</a>
 */
@DisplayName("CodecResource Array Root Tests")
class CodecResourceArrayRootTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass personClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(CodecResourceArrayRootTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = ecoreHelper.getEClass(testPackage, "Person");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Factory Methods
    // ========================================================================

    private EObject createPerson(String name, int age) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(personClass.getEStructuralFeature("name"), name);
        person.eSet(personClass.getEStructuralFeature("age"), age);
        return person;
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://array-root.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    // ========================================================================
    // Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Serialization")
    class Serialization {

        @Test
        @DisplayName("single root serializes as JSON object")
        void singleRoot_serializesAsObject() throws IOException {
            CodecResource resource = createResource();
            resource.getContents().add(createPerson("John Doe", 30));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, Map.of());

            String json = out.toString(StandardCharsets.UTF_8);
            System.out.println("Single root JSON:\n" + json);

            // Should be a JSON object (starts with {)
            assertTrue(json.trim().startsWith("{"),
                    "Single root should serialize as JSON object");
            assertFalse(json.trim().startsWith("["),
                    "Single root should NOT serialize as JSON array");

            // Verify content
            assertTrue(json.contains("\"name\":\"John Doe\""));
            assertTrue(json.contains("\"age\":30"));
        }

        @Test
        @DisplayName("multiple roots serialize as JSON array")
        void multipleRoots_serializesAsArray() throws IOException {
            CodecResource resource = createResource();
            resource.getContents().add(createPerson("John Doe", 30));
            resource.getContents().add(createPerson("Jane Smith", 25));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, Map.of());

            String json = out.toString(StandardCharsets.UTF_8);
            System.out.println("Multiple roots JSON:\n" + json);

            // Should be a JSON array (starts with [)
            assertTrue(json.trim().startsWith("["),
                    "Multiple roots should serialize as JSON array");

            // Verify both persons are present
            assertTrue(json.contains("\"name\":\"John Doe\""));
            assertTrue(json.contains("\"name\":\"Jane Smith\""));
        }

        @Test
        @DisplayName("three roots serialize as JSON array with three elements")
        void threeRoots_serializesAsArrayWithThreeElements() throws IOException {
            CodecResource resource = createResource();
            resource.getContents().add(createPerson("John Doe", 30));
            resource.getContents().add(createPerson("Jane Smith", 25));
            resource.getContents().add(createPerson("Bob Wilson", 40));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, Map.of());

            String json = out.toString(StandardCharsets.UTF_8);
            System.out.println("Three roots JSON:\n" + json);

            // Count occurrences of name to verify 3 objects
            int count = countOccurrences(json, "\"name\"");
            assertEquals(3, count, "Should contain 3 person objects");
        }
    }

    // ========================================================================
    // Deserialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Deserialization")
    class Deserialization {

        @Test
        @DisplayName("JSON object deserializes as single root")
        void jsonObject_deserializesAsSingleRoot() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/roundtrip/1.0#//Person",
                    "name": "John Doe",
                    "age": 30
                }
                """;

            CodecResource resource = createResource();
            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            assertEquals(1, resource.getContents().size(),
                    "Should have exactly one root object");

            EObject person = resource.getContents().get(0);
            assertEquals("John Doe", person.eGet(personClass.getEStructuralFeature("name")));
            assertEquals(30, person.eGet(personClass.getEStructuralFeature("age")));
        }

        @Test
        @DisplayName("JSON array deserializes as multiple roots")
        void jsonArray_deserializesAsMultipleRoots() throws IOException {
            String json = """
                [
                    {
                        "_type": "http://test.example.org/roundtrip/1.0#//Person",
                        "name": "John Doe",
                        "age": 30
                    },
                    {
                        "_type": "http://test.example.org/roundtrip/1.0#//Person",
                        "name": "Jane Smith",
                        "age": 25
                    }
                ]
                """;

            CodecResource resource = createResource();
            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            assertEquals(2, resource.getContents().size(),
                    "Should have exactly two root objects");

            EObject person1 = resource.getContents().get(0);
            assertEquals("John Doe", person1.eGet(personClass.getEStructuralFeature("name")));

            EObject person2 = resource.getContents().get(1);
            assertEquals("Jane Smith", person2.eGet(personClass.getEStructuralFeature("name")));
        }

        @Test
        @DisplayName("JSON array with three elements deserializes as three roots")
        void jsonArrayWithThree_deserializesAsThreeRoots() throws IOException {
            String json = """
                [
                    {
                        "_type": "http://test.example.org/roundtrip/1.0#//Person",
                        "name": "John Doe",
                        "age": 30
                    },
                    {
                        "_type": "http://test.example.org/roundtrip/1.0#//Person",
                        "name": "Jane Smith",
                        "age": 25
                    },
                    {
                        "_type": "http://test.example.org/roundtrip/1.0#//Person",
                        "name": "Bob Wilson",
                        "age": 40
                    }
                ]
                """;

            CodecResource resource = createResource();
            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            assertEquals(3, resource.getContents().size(),
                    "Should have exactly three root objects");

            assertEquals("John Doe", resource.getContents().get(0).eGet(personClass.getEStructuralFeature("name")));
            assertEquals("Jane Smith", resource.getContents().get(1).eGet(personClass.getEStructuralFeature("name")));
            assertEquals("Bob Wilson", resource.getContents().get(2).eGet(personClass.getEStructuralFeature("name")));
        }

        @Test
        @DisplayName("empty JSON array deserializes as empty contents")
        void emptyJsonArray_deserializesAsEmptyContents() throws IOException {
            String json = "[]";

            CodecResource resource = createResource();
            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            assertEquals(0, resource.getContents().size(),
                    "Should have no root objects for empty array");
        }

        @Test
        @DisplayName("JSON array with CODEC_ROOT_TYPE hint")
        void jsonArrayWithHint_usesHintForTypeResolution() throws IOException {
            // JSON without _type - relies on CODEC_ROOT_TYPE hint
            String json = """
                [
                    {
                        "name": "John Doe",
                        "age": 30
                    },
                    {
                        "name": "Jane Smith",
                        "age": 25
                    }
                ]
                """;

            CodecResource resource = createResource();
            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, personClass);

            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

            assertEquals(2, resource.getContents().size(),
                    "Should have two root objects using hint");

            // Verify both are Person instances
            assertEquals(personClass, resource.getContents().get(0).eClass());
            assertEquals(personClass, resource.getContents().get(1).eClass());

            // Verify data
            assertEquals("John Doe", resource.getContents().get(0).eGet(personClass.getEStructuralFeature("name")));
            assertEquals("Jane Smith", resource.getContents().get(1).eGet(personClass.getEStructuralFeature("name")));
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip")
    class RoundTrip {

        @Test
        @DisplayName("multiple roots round-trip correctly")
        void multipleRoots_roundTrip() throws IOException {
            // Create resource with multiple roots
            CodecResource saveResource = createResource();
            saveResource.getContents().add(createPerson("John Doe", 30));
            saveResource.getContents().add(createPerson("Jane Smith", 25));

            // Serialize
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            saveResource.save(out, Map.of());
            String json = out.toString(StandardCharsets.UTF_8);
            System.out.println("Round-trip JSON:\n" + json);

            // Deserialize
            CodecResource loadResource = createResource();
            loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            // Verify
            assertEquals(2, loadResource.getContents().size(),
                    "Should round-trip two root objects");

            EObject person1 = loadResource.getContents().get(0);
            assertEquals("John Doe", person1.eGet(personClass.getEStructuralFeature("name")));
            assertEquals(30, person1.eGet(personClass.getEStructuralFeature("age")));

            EObject person2 = loadResource.getContents().get(1);
            assertEquals("Jane Smith", person2.eGet(personClass.getEStructuralFeature("name")));
            assertEquals(25, person2.eGet(personClass.getEStructuralFeature("age")));
        }

        @Test
        @DisplayName("single root round-trip correctly")
        void singleRoot_roundTrip() throws IOException {
            // Create resource with single root
            CodecResource saveResource = createResource();
            saveResource.getContents().add(createPerson("John Doe", 30));

            // Serialize
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            saveResource.save(out, Map.of());
            String json = out.toString(StandardCharsets.UTF_8);

            // Verify it's an object, not array
            assertTrue(json.trim().startsWith("{"));

            // Deserialize
            CodecResource loadResource = createResource();
            loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            // Verify single root
            assertEquals(1, loadResource.getContents().size());
            EObject person = loadResource.getContents().get(0);
            assertEquals("John Doe", person.eGet(personClass.getEStructuralFeature("name")));
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
}
