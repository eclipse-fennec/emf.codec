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
 * Integration tests for ID serialization formats.
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.CodecResourceIdTest}.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-id.md">Spec: ID Serialization</a>
 */
@DisplayName("ID Serialization Tests")
class CodecResourceIdTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-id.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass personClass;
    private EClass multiIdClass;

    // EAttributes
    private EAttribute personIdAttribute;
    private EAttribute personNameAttribute;
    private EAttribute firstNameAttribute;
    private EAttribute lastNameAttribute;
    private EAttribute sequenceAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(CodecResourceIdTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = ecoreHelper.getEClass(testPackage, "Person");
        multiIdClass = ecoreHelper.getEClass(testPackage, "MultiId");

        personIdAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "personId");
        personNameAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        firstNameAttribute = (EAttribute) ecoreHelper.getFeature(multiIdClass, "firstName");
        lastNameAttribute = (EAttribute) ecoreHelper.getFeature(multiIdClass, "lastName");
        sequenceAttribute = (EAttribute) ecoreHelper.getFeature(multiIdClass, "sequence");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private EObject createPerson(String id, String name) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(personIdAttribute, id);
        person.eSet(personNameAttribute, name);
        return person;
    }

    private EObject createMultiId(String firstName, String lastName, int sequence) {
        EObject obj = testPackage.getEFactoryInstance().create(multiIdClass);
        obj.eSet(firstNameAttribute, firstName);
        obj.eSet(lastNameAttribute, lastName);
        obj.eSet(sequenceAttribute, sequence);
        return obj;
    }

    private CodecResource createResource(ConfigurationResolver resolver) {
        return new CodecResource(
                URI.createURI("test://id-test.json"),
                metadataService,
                resolver,
                null);
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = createResource(resolver);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass eClass, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = createResource(resolver);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, eClass);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    // ========================================================================
    // ConfigurationResolver builders for ID configs
    // ========================================================================

    private ConfigurationResolver idResolver(Map<String, Object> props) {
        return ConfigurationResolver.builder()
                .moduleProperties(props)
                .build();
    }

    // ========================================================================
    // PLAIN Format Tests
    // ========================================================================

    @Nested
    @DisplayName("PLAIN format (default)")
    class PlainFormatTests {

        @Test
        @DisplayName("single ID feature - PLAIN format")
        void singleIdPlainFormat() throws IOException {
            EObject person = createPerson("john-123", "John Doe");

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD"));

            String json = serialize(person, resolver);

            assertTrue(json.contains("\"_id\":\"john-123\""), "Should have _id as string");
            assertTrue(json.contains("\"name\":\"John Doe\""), "Should have name");
        }

        @Test
        @DisplayName("multiple ID features - PLAIN format with separator")
        void multipleIdPlainFormat() throws IOException {
            EObject obj = createMultiId("John", "Doe", 42);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idSeparator", "-"));

            String json = serialize(obj, resolver);

            assertTrue(json.contains("\"_id\":\"John-Doe-42\""), "Should have combined _id");
        }

        @Test
        @DisplayName("custom separator")
        void customSeparator() throws IOException {
            EObject obj = createMultiId("John", "Doe", 1);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName"),
                    "idSeparator", "_"));

            String json = serialize(obj, resolver);

            assertTrue(json.contains("\"_id\":\"John_Doe\""), "Should use custom separator");
        }
    }

    // ========================================================================
    // STRUCTURED Format Tests
    // ========================================================================

    @Nested
    @DisplayName("STRUCTURED format")
    class StructuredFormatTests {

        @Test
        @DisplayName("single ID feature - STRUCTURED format")
        void singleIdStructuredFormat() throws IOException {
            EObject person = createPerson("john-123", "John Doe");

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFormat", "STRUCTURED"));

            String json = serialize(person, resolver);

            assertTrue(json.contains("\"_id\":{"), "Should have _id as object");
            assertTrue(json.contains("\"personId\":\"john-123\""), "Should have personId inside _id");
            assertFalse(json.contains("\"separator\""), "Should not have separator for single ID");
        }

        @Test
        @DisplayName("multiple ID features - STRUCTURED format with separator serialized (default)")
        void multipleIdStructuredFormatWithSeparator() throws IOException {
            EObject obj = createMultiId("John", "Doe", 42);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idFormat", "STRUCTURED",
                    "idSeparator", "-"));

            String json = serialize(obj, resolver);

            assertTrue(json.contains("\"_id\":{"), "Should have _id as object");
            assertTrue(json.contains("\"separator\":\"-\""), "Separator SHOULD be serialized (default)");
            assertTrue(json.contains("\"firstName\":\"John\""), "Should have firstName");
            assertTrue(json.contains("\"lastName\":\"Doe\""), "Should have lastName");
            assertTrue(json.contains("\"sequence\":42"), "Should have sequence as number");
        }

        @Test
        @DisplayName("multiple ID features - STRUCTURED format WITHOUT separator serialized")
        void multipleIdStructuredFormatWithoutSeparator() throws IOException {
            EObject obj = createMultiId("John", "Doe", 42);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idFormat", "STRUCTURED",
                    "idSeparator", "-",
                    "idSeparatorSerialize", false));

            String json = serialize(obj, resolver);

            assertTrue(json.contains("\"_id\":{"), "Should have _id as object");
            assertFalse(json.contains("\"separator\""), "Separator should NOT be serialized");
            assertTrue(json.contains("\"firstName\":\"John\""), "Should have firstName");
            assertTrue(json.contains("\"lastName\":\"Doe\""), "Should have lastName");
            assertTrue(json.contains("\"sequence\":42"), "Should have sequence as number");
        }

        @Test
        @DisplayName("custom separator key")
        void customSeparatorKey() throws IOException {
            EObject obj = createMultiId("John", "Doe", 42);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName"),
                    "idFormat", "STRUCTURED",
                    "idSeparator", "|",
                    "idSeparatorKey", "sep"));

            String json = serialize(obj, resolver);

            assertTrue(json.contains("\"sep\":\"|\""), "Should use custom separator key");
            assertFalse(json.contains("\"separator\""), "Should not use default key");
        }

        @Test
        @DisplayName("custom ID key - STRUCTURED format")
        void customIdKeyStructuredFormat() throws IOException {
            EObject person = createPerson("john-123", "John Doe");

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idKey", "identifier",
                    "idFormat", "STRUCTURED"));

            String json = serialize(person, resolver);

            assertTrue(json.contains("\"identifier\":{"), "Should use custom key");
            assertFalse(json.contains("\"_id\""), "Should not have default _id key");
        }
    }

    // ========================================================================
    // IdKeyMode Tests
    // ========================================================================

    @Nested
    @DisplayName("IdKeyMode")
    class IdKeyModeTests {

        @Test
        @DisplayName("ID_ONLY mode - only _id field")
        void idOnlyMode() throws IOException {
            EObject person = createPerson("john-123", "John Doe");

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idKeyMode", "ID_ONLY"));

            String json = serialize(person, resolver);

            assertTrue(json.contains("\"_id\":\"john-123\""), "Should have _id");
        }

        @Test
        @DisplayName("FEATURE_ONLY mode - no _id field")
        void featureOnlyMode() throws IOException {
            EObject person = createPerson("john-123", "John Doe");

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idKeyMode", "FEATURE_ONLY"));

            String json = serialize(person, resolver);

            assertFalse(json.contains("\"_id\""), "Should NOT have _id field");
            assertTrue(json.contains("\"personId\":\"john-123\""), "Should have personId field");
        }

        @Test
        @DisplayName("BOTH mode - _id and feature fields")
        void bothMode() throws IOException {
            EObject person = createPerson("john-123", "John Doe");

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idKeyMode", "BOTH"));

            String json = serialize(person, resolver);

            assertTrue(json.contains("\"_id\":\"john-123\""), "Should have _id");
            assertTrue(json.contains("\"personId\":\"john-123\""), "Should also have personId");
        }
    }

    // ========================================================================
    // Edge Cases
    // ========================================================================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("ID disabled")
        void idDisabled() throws IOException {
            EObject person = createPerson("john-123", "John Doe");

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idKeyMode", "NONE"));

            String json = serialize(person, resolver);

            assertFalse(json.contains("\"_id\""), "Should not have _id when disabled");
        }

        @Test
        @DisplayName("feature ordering preserved in PLAIN format")
        void featureOrderingPreservedPlain() throws IOException {
            EObject obj = createMultiId("John", "Doe", 1);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("lastName", "firstName"),
                    "idSeparator", "-"));

            String json = serialize(obj, resolver);

            assertTrue(json.contains("\"_id\":\"Doe-John\""), "Should preserve feature order");
        }

        @Test
        @DisplayName("feature ordering preserved in STRUCTURED format")
        void featureOrderingPreservedStructured() throws IOException {
            EObject obj = createMultiId("John", "Doe", 1);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("lastName", "firstName"),
                    "idFormat", "STRUCTURED",
                    "idSeparator", "-"));

            String json = serialize(obj, resolver);

            int lastNamePos = json.indexOf("\"lastName\"");
            int firstNamePos = json.indexOf("\"firstName\"");
            assertTrue(lastNamePos < firstNamePos, "lastName should appear before firstName");
        }
    }

    // ========================================================================
    // Deserialization Tests
    // ========================================================================

    @Nested
    @DisplayName("PLAIN format deserialization")
    class PlainDeserializationTests {

        @Test
        @DisplayName("single ID feature - PLAIN format")
        void deserializeSingleIdPlainFormat() throws IOException {
            String json = "{\"_id\":\"john-123\",\"name\":\"John Doe\"}";

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD"));

            EObject result = deserialize(json, personClass, resolver);
            assertNotNull(result, "Result should not be null");

            assertEquals("john-123", result.eGet(personIdAttribute), "Should have personId set");
            assertEquals("John Doe", result.eGet(personNameAttribute), "Should have name set");
        }

        @Test
        @DisplayName("multiple ID features - PLAIN format with separator")
        void deserializeMultipleIdPlainFormat() throws IOException {
            String json = "{\"_id\":\"John-Doe-42\",\"sequence\":42}";

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idSeparator", "-"));

            EObject result = deserialize(json, multiIdClass, resolver);

            assertEquals("John", result.eGet(firstNameAttribute), "Should have firstName set");
            assertEquals("Doe", result.eGet(lastNameAttribute), "Should have lastName set");
            assertEquals(42, result.eGet(sequenceAttribute), "Should have sequence set");
        }

        @Test
        @DisplayName("custom separator")
        void deserializeCustomSeparator() throws IOException {
            String json = "{\"_id\":\"John_Doe\"}";

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName"),
                    "idSeparator", "_"));

            EObject result = deserialize(json, multiIdClass, resolver);

            assertEquals("John", result.eGet(firstNameAttribute), "Should have firstName set");
            assertEquals("Doe", result.eGet(lastNameAttribute), "Should have lastName set");
        }
    }

    @Nested
    @DisplayName("STRUCTURED format deserialization")
    class StructuredDeserializationTests {

        @Test
        @DisplayName("single ID feature - STRUCTURED format")
        void deserializeSingleIdStructuredFormat() throws IOException {
            String json = "{\"_id\":{\"personId\":\"john-123\"},\"name\":\"John Doe\"}";

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFormat", "STRUCTURED"));

            EObject result = deserialize(json, personClass, resolver);

            assertEquals("john-123", result.eGet(personIdAttribute), "Should have personId set");
            assertEquals("John Doe", result.eGet(personNameAttribute), "Should have name set");
        }

        @Test
        @DisplayName("multiple ID features - STRUCTURED format")
        void deserializeMultipleIdStructuredFormat() throws IOException {
            String json = "{\"_id\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"sequence\":42}}";

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idFormat", "STRUCTURED"));

            EObject result = deserialize(json, multiIdClass, resolver);

            assertEquals("John", result.eGet(firstNameAttribute), "Should have firstName set");
            assertEquals("Doe", result.eGet(lastNameAttribute), "Should have lastName set");
            assertEquals(42, result.eGet(sequenceAttribute), "Should have sequence set");
        }

        @Test
        @DisplayName("STRUCTURED format with separator in JSON")
        void deserializeStructuredWithSeparatorInJson() throws IOException {
            String json = "{\"_id\":{\"separator\":\"-\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"sequence\":42}}";

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idFormat", "STRUCTURED",
                    "idSeparator", "|"));

            EObject result = deserialize(json, multiIdClass, resolver);

            assertEquals("John", result.eGet(firstNameAttribute), "Should have firstName set");
            assertEquals("Doe", result.eGet(lastNameAttribute), "Should have lastName set");
            assertEquals(42, result.eGet(sequenceAttribute), "Should have sequence set");
        }

        @Test
        @DisplayName("STRUCTURED format with custom separator key in JSON")
        void deserializeStructuredWithCustomSeparatorKey() throws IOException {
            String json = "{\"_id\":{\"sep\":\"|\",\"firstName\":\"John\",\"lastName\":\"Doe\"}}";

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName"),
                    "idFormat", "STRUCTURED",
                    "idSeparatorKey", "sep"));

            EObject result = deserialize(json, multiIdClass, resolver);

            assertEquals("John", result.eGet(firstNameAttribute), "Should have firstName set");
            assertEquals("Doe", result.eGet(lastNameAttribute), "Should have lastName set");
        }

        @Test
        @DisplayName("custom ID key - STRUCTURED format")
        void deserializeCustomIdKeyStructuredFormat() throws IOException {
            String json = "{\"identifier\":{\"personId\":\"john-123\"},\"name\":\"John Doe\"}";

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idKey", "identifier",
                    "idFormat", "STRUCTURED"));

            EObject result = deserialize(json, personClass, resolver);

            assertEquals("john-123", result.eGet(personIdAttribute), "Should have personId set");
        }
    }

    @Nested
    @DisplayName("Round-trip tests")
    class RoundTripTests {

        @Test
        @DisplayName("round-trip PLAIN format")
        void roundTripPlainFormat() throws IOException {
            EObject original = createMultiId("John", "Doe", 42);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idSeparator", "-"));

            String json = serialize(original, resolver);

            EObject result = deserialize(json, multiIdClass, resolver);
            assertNotNull(result, "Result should not be null. JSON was: " + json);

            assertEquals(original.eGet(firstNameAttribute), result.eGet(firstNameAttribute));
            assertEquals(original.eGet(lastNameAttribute), result.eGet(lastNameAttribute));
            assertEquals(original.eGet(sequenceAttribute), result.eGet(sequenceAttribute));
        }

        @Test
        @DisplayName("round-trip STRUCTURED format")
        void roundTripStructuredFormat() throws IOException {
            EObject original = createMultiId("John", "Doe", 42);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idFormat", "STRUCTURED"));

            String json = serialize(original, resolver);

            EObject result = deserialize(json, multiIdClass, resolver);
            assertNotNull(result, "Result should not be null. JSON was: " + json);

            assertEquals(original.eGet(firstNameAttribute), result.eGet(firstNameAttribute));
            assertEquals(original.eGet(lastNameAttribute), result.eGet(lastNameAttribute));
            assertEquals(original.eGet(sequenceAttribute), result.eGet(sequenceAttribute));
        }

        @Test
        @DisplayName("round-trip STRUCTURED format with separator serialized")
        void roundTripStructuredFormatWithSeparator() throws IOException {
            EObject original = createMultiId("John", "Doe", 42);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idFormat", "STRUCTURED",
                    "idSeparator", "-"));

            String json = serialize(original, resolver);

            assertTrue(json.contains("\"separator\":\"-\""), "Separator should be in JSON");

            EObject result = deserialize(json, multiIdClass, resolver);
            assertNotNull(result, "Result should not be null. JSON was: " + json);

            assertEquals(original.eGet(firstNameAttribute), result.eGet(firstNameAttribute));
            assertEquals(original.eGet(lastNameAttribute), result.eGet(lastNameAttribute));
            assertEquals(original.eGet(sequenceAttribute), result.eGet(sequenceAttribute));
        }

        @Test
        @DisplayName("round-trip STRUCTURED format without separator serialized")
        void roundTripStructuredFormatWithoutSeparator() throws IOException {
            EObject original = createMultiId("John", "Doe", 42);

            ConfigurationResolver resolver = idResolver(Map.of(
                    "idStrategy", "ID_FIELD",
                    "idFeatures", List.of("firstName", "lastName", "sequence"),
                    "idFormat", "STRUCTURED",
                    "idSeparator", "-",
                    "idSeparatorSerialize", false));

            String json = serialize(original, resolver);

            assertFalse(json.contains("\"separator\""), "Separator should NOT be in JSON");

            EObject result = deserialize(json, multiIdClass, resolver);
            assertNotNull(result, "Result should not be null. JSON was: " + json);

            assertEquals(original.eGet(firstNameAttribute), result.eGet(firstNameAttribute));
            assertEquals(original.eGet(lastNameAttribute), result.eGet(lastNameAttribute));
            assertEquals(original.eGet(sequenceAttribute), result.eGet(sequenceAttribute));
        }
    }
}
