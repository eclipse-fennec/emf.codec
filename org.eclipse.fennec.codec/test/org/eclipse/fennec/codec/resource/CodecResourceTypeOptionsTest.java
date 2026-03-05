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
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for type serialization options exercised through
 * {@link CodecResource} save/load. Each {@code @Nested} group covers a
 * feature area from the codec spec (docs/codec-v2-spec/06-type.md).
 */
@DisplayName("CodecResource Type Options Integration Tests")
class CodecResourceTypeOptionsTest {

    private static final String ROUNDTRIP_ECORE = "test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage roundtripPackage;
    private MetadataWhiteboard metadataService;

    // EClasses from test-roundtrip.ecore
    private EClass personClass;
    private EClass addressClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        roundtripPackage = ecoreHelper.loadEcore(
                "/org/eclipse/fennec/codec/resource/" + ROUNDTRIP_ECORE,
                CodecResourceTypeOptionsTest.class);
        EPackage.Registry.INSTANCE.put(roundtripPackage.getNsURI(), roundtripPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(roundtripPackage);

        personClass = EcoreHelper.getEClass(roundtripPackage, "Person");
        addressClass = EcoreHelper.getEClass(roundtripPackage, "Address");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(roundtripPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helper methods
    // ========================================================================

    private EObject createPerson(String name, int age) {
        EObject person = roundtripPackage.getEFactoryInstance().create(personClass);
        person.eSet(personClass.getEStructuralFeature("name"), name);
        person.eSet(personClass.getEStructuralFeature("age"), age);
        return person;
    }

    private EObject createAddress(String street, String city) {
        EObject address = roundtripPackage.getEFactoryInstance().create(addressClass);
        address.eSet(addressClass.getEStructuralFeature("street"), street);
        address.eSet(addressClass.getEStructuralFeature("city"), city);
        return address;
    }

    private CodecResource createResource(Map<String, Object> options) {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .optionsProperties(options)
                .build();
        return new CodecResource(
                URI.createURI("test://type-options.json"),
                metadataService, config, null);
    }



    private String serialize(EObject object, Map<String, Object> configOptions) throws IOException {
        CodecResource resource = createResource(configOptions);
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, Map<String, Object> configOptions,
                                Map<String, Object> loadOptions) throws IOException {
        CodecResource resource = createResource(configOptions);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, loadOptions);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    // ========================================================================
    // 1. NONE Strategy — spec §1.4
    // ========================================================================

    @Nested
    @DisplayName("NoneStrategy")
    class NoneStrategy {

        private final Map<String, Object> noneOptions = Map.of(
                CodecOptions.CODEC_TYPE_STRATEGY, "NONE");

        @Test
        @DisplayName("no type field in JSON output")
        void noneStrategy_noTypeFieldInJson() throws IOException {
            EObject person = createPerson("Alice", 30);
            String json = serialize(person, noneOptions);

            assertFalse(json.contains("\"_type\""), "NONE strategy should omit _type field");
            assertTrue(json.contains("\"Alice\""), "Name should be present");
        }

        @Test
        @DisplayName("round-trip with root type hint")
        void noneStrategy_roundTrip_withRootTypeHint() throws IOException {
            EObject person = createPerson("Bob", 25);
            String json = serialize(person, noneOptions);

            Map<String, Object> loadOptions = new HashMap<>();
            loadOptions.put(CodecResource.CODEC_ROOT_TYPE, personClass);

            EObject loaded = deserialize(json, noneOptions, loadOptions);

            assertNotNull(loaded);
            assertEquals(personClass, loaded.eClass());
            assertEquals("Bob", loaded.eGet(personClass.getEStructuralFeature("name")));
            assertEquals(25, loaded.eGet(personClass.getEStructuralFeature("age")));
        }

        @Test
        @DisplayName("polymorphic deserialize without hint returns null or fails")
        void noneStrategy_polymorphic_failsWithoutHint() throws IOException {
            String json = """
                    {"name": "Charlie", "age": 35}
                    """;

            EObject loaded = deserialize(json, noneOptions, Map.of());

            // Without type info and no hint, deserialization cannot determine the EClass
            assertNull(loaded, "Should not be able to deserialize without type hint in NONE strategy");
        }
    }

    // ========================================================================
    // 2. NAME Strategy — spec §1.1
    // ========================================================================

    @Nested
    @DisplayName("NameStrategy")
    class NameStrategy {

        private final Map<String, Object> nameOptions = Map.of(
                CodecOptions.CODEC_TYPE_STRATEGY, "NAME");

        @Test
        @DisplayName("serializes simple class name")
        void nameStrategy_serializesSimpleName() throws IOException {
            EObject person = createPerson("Alice", 30);
            String json = serialize(person, nameOptions);

            assertTrue(json.contains("\"Person\""), "NAME strategy should output simple class name");
            assertFalse(json.contains("http://"), "Should not contain package URI");
        }

        @Test
        @DisplayName("round-trip with schema hint")
        void nameStrategy_roundTrip_withSchemaHint() throws IOException {
            EObject person = createPerson("Diana", 28);
            String json = serialize(person, nameOptions);

            Map<String, Object> loadOptions = new HashMap<>();
            loadOptions.put(CodecResource.CODEC_ROOT_SCHEMA, roundtripPackage.getNsURI());

            EObject loaded = deserialize(json, nameOptions, loadOptions);

            assertNotNull(loaded);
            assertEquals(personClass, loaded.eClass());
            assertEquals("Diana", loaded.eGet(personClass.getEStructuralFeature("name")));
        }

        @Test
        @DisplayName("round-trip with root type hint")
        void nameStrategy_roundTrip_withRootTypeHint() throws IOException {
            EObject person = createPerson("Eve", 40);
            String json = serialize(person, nameOptions);

            Map<String, Object> loadOptions = new HashMap<>();
            loadOptions.put(CodecResource.CODEC_ROOT_TYPE, personClass);

            EObject loaded = deserialize(json, nameOptions, loadOptions);

            assertNotNull(loaded);
            assertEquals(personClass, loaded.eClass());
            assertEquals("Eve", loaded.eGet(personClass.getEStructuralFeature("name")));
            assertEquals(40, loaded.eGet(personClass.getEStructuralFeature("age")));
        }
    }

    // ========================================================================
    // 3. NUMERIC Strategy — spec §1.7
    // ========================================================================

    @Nested
    @DisplayName("NumericStrategy")
    class NumericStrategy {

        private final Map<String, Object> numericOptions = Map.of(
                CodecOptions.CODEC_TYPE_STRATEGY, "NUMERIC");

        @Test
        @DisplayName("serializes classifier ID")
        void numericStrategy_serializesClassifierId() throws IOException {
            EObject person = createPerson("Frank", 50);
            String json = serialize(person, numericOptions);

            // The _type should be the numeric classifier ID
            int classifierId = personClass.getClassifierID();
            assertTrue(json.contains("\"_type\""), "Should contain _type field");
            // Numeric IDs are serialized as strings
            assertTrue(json.contains("\"" + classifierId + "\"") || json.contains("" + classifierId),
                    "Should contain classifier ID " + classifierId);
        }

        @Test
        @DisplayName("round-trip with root schema hint")
        void numericStrategy_roundTrip_withRootSchemaHint() throws IOException {
            EObject person = createPerson("Grace", 33);
            String json = serialize(person, numericOptions);

            Map<String, Object> loadOptions = new HashMap<>();
            loadOptions.put(CodecResource.CODEC_ROOT_SCHEMA, roundtripPackage.getNsURI());

            EObject loaded = deserialize(json, numericOptions, loadOptions);

            assertNotNull(loaded);
            assertEquals(personClass, loaded.eClass());
            assertEquals("Grace", loaded.eGet(personClass.getEStructuralFeature("name")));
            assertEquals(33, loaded.eGet(personClass.getEStructuralFeature("age")));
        }
    }

    // ========================================================================
    // 4. SCHEMA_AND_TYPE Strategy — spec §1.6
    // ========================================================================

    @Nested
    @DisplayName("SchemaAndTypeStrategy")
    class SchemaAndTypeStrategy {

        private final Map<String, Object> schemaAndTypeOptions = Map.of(
                CodecOptions.CODEC_TYPE_STRATEGY, "SCHEMA_AND_TYPE");

        @Test
        @DisplayName("plain format writes two fields")
        void schemaAndType_plain_writesTwoFields() throws IOException {
            EObject person = createPerson("Hank", 45);
            String json = serialize(person, schemaAndTypeOptions);

            assertTrue(json.contains("\"_schema\"") || json.contains("\"schema\""),
                    "Should contain schema field");
            assertTrue(json.contains("\"_type\""),
                    "Should contain type field");
            assertTrue(json.contains(roundtripPackage.getNsURI()),
                    "Should contain package nsURI as schema value");
            assertTrue(json.contains("\"Person\""),
                    "Should contain class name as type value");
        }

        @Test
        @DisplayName("structured format writes nested object")
        void schemaAndType_structured_writesNestedObject() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_TYPE_STRATEGY, "SCHEMA_AND_TYPE",
                    CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED");

            EObject person = createPerson("Iris", 38);
            String json = serialize(person, options);

            // In STRUCTURED format, type info is nested: "_type": {"schema": "...", "type": "..."}
            assertTrue(json.contains("\"_type\""), "Should contain _type key");
            assertTrue(json.contains(roundtripPackage.getNsURI()),
                    "Should contain package nsURI");
            assertTrue(json.contains("\"Person\""),
                    "Should contain class name");
        }

        @Test
        @DisplayName("round-trip with plain format")
        void schemaAndType_roundTrip() throws IOException {
            EObject person = createPerson("Jack", 55);
            String json = serialize(person, schemaAndTypeOptions);

            Map<String, Object> loadOptions = new HashMap<>();
            loadOptions.put(CodecResource.CODEC_ROOT_TYPE, personClass);

            EObject loaded = deserialize(json, schemaAndTypeOptions, loadOptions);

            assertNotNull(loaded);
            assertEquals(personClass, loaded.eClass());
            assertEquals("Jack", loaded.eGet(personClass.getEStructuralFeature("name")));
            assertEquals(55, loaded.eGet(personClass.getEStructuralFeature("age")));
        }
    }

    // ========================================================================
    // 5. Custom Type Key — spec §3
    // ========================================================================

    @Nested
    @DisplayName("CustomTypeKey")
    class CustomTypeKey {

        @Test
        @DisplayName("@type as custom key")
        void customTypeKey_atType() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_TYPE_KEY, "@type");

            EObject person = createPerson("Kate", 29);
            String json = serialize(person, options);

            assertTrue(json.contains("\"@type\""), "Should use @type as type key");
            assertFalse(json.contains("\"_type\""), "Should not use default _type key");
        }

        @Test
        @DisplayName("round-trip with custom key and NAME strategy")
        void customTypeKey_roundTrip() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_TYPE_KEY, "@type",
                    CodecOptions.CODEC_TYPE_STRATEGY, "NAME");

            EObject person = createPerson("Leo", 42);
            String json = serialize(person, options);

            assertTrue(json.contains("\"@type\""), "Should use @type key");
            assertTrue(json.contains("\"Person\""), "Should use NAME strategy");

            Map<String, Object> loadOptions = new HashMap<>();
            loadOptions.put(CodecResource.CODEC_ROOT_SCHEMA, roundtripPackage.getNsURI());

            EObject loaded = deserialize(json, options, loadOptions);

            assertNotNull(loaded);
            assertEquals(personClass, loaded.eClass());
            assertEquals("Leo", loaded.eGet(personClass.getEStructuralFeature("name")));
        }

        @Test
        @DisplayName("'type' as custom key (common API pattern)")
        void customTypeKey_type_roundTrip() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_TYPE_KEY, "type",
                    CodecOptions.CODEC_TYPE_STRATEGY, "NAME");

            EObject person = createPerson("Mia", 31);
            String json = serialize(person, options);

            assertTrue(json.contains("\"type\""), "Should use 'type' as type key");

            Map<String, Object> loadOptions = new HashMap<>();
            loadOptions.put(CodecResource.CODEC_ROOT_SCHEMA, roundtripPackage.getNsURI());

            EObject loaded = deserialize(json, options, loadOptions);

            assertNotNull(loaded);
            assertEquals(personClass, loaded.eClass());
            assertEquals("Mia", loaded.eGet(personClass.getEStructuralFeature("name")));
        }
    }

    // ========================================================================
    // 6. STRUCTURED Format — spec §1.2
    // ========================================================================

    @Nested
    @DisplayName("StructuredFormat")
    class StructuredFormat {

        @Test
        @DisplayName("structured URI writes nested object")
        void structured_uri_writesNestedObject() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED");

            EObject person = createPerson("Nina", 27);
            String json = serialize(person, options);

            // STRUCTURED format with default URI strategy nests the type value
            assertTrue(json.contains("\"_type\""), "Should contain _type key");
            assertTrue(json.contains(roundtripPackage.getNsURI()),
                    "Should contain package URI in structured output");
        }

        @Test
        @DisplayName("structured NAME writes nested object")
        void structured_name_writesNestedObject() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_TYPE_STRATEGY, "NAME",
                    CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED");

            EObject person = createPerson("Oscar", 36);
            String json = serialize(person, options);

            assertTrue(json.contains("\"_type\""), "Should contain _type key");
            assertTrue(json.contains("\"Person\""), "Should contain class name");
        }

        @Test
        @DisplayName("structured format round-trip")
        void structured_roundTrip() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED");

            EObject person = createPerson("Pat", 44);
            String json = serialize(person, options);

            Map<String, Object> loadOptions = new HashMap<>();
            loadOptions.put(CodecResource.CODEC_ROOT_TYPE, personClass);

            EObject loaded = deserialize(json, options, loadOptions);

            assertNotNull(loaded);
            assertEquals(personClass, loaded.eClass());
            assertEquals("Pat", loaded.eGet(personClass.getEStructuralFeature("name")));
            assertEquals(44, loaded.eGet(personClass.getEStructuralFeature("age")));
        }
    }

    // ========================================================================
    // 7. Per-Class Type Config — spec §16 scope chain
    // ========================================================================

    @Nested
    @DisplayName("PerClassTypeConfig")
    class PerClassTypeConfig {

        @Test
        @DisplayName("per-class overrides global strategy")
        void perClass_overridesGlobal() throws IOException {
            // Global: NAME strategy, but Address uses NONE
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_TYPE_STRATEGY, "NAME");
            options.put(CodecOptions.CODEC_ECLASS_CONFIG,
                    Map.of(addressClass, Map.of(
                            CodecOptions.CODEC_TYPE_STRATEGY, "NONE")));

            // Create a person with an address
            EObject person = createPerson("Quinn", 32);
            EObject address = createAddress("123 Main St", "Springfield");
            person.eSet(personClass.getEStructuralFeature("address"), address);

            String json = serialize(person, options);

            // Person should have NAME type
            assertTrue(json.contains("\"Person\""), "Person should have NAME type");
            // Address should have NONE type
            assertFalse(json.contains("\"Address\""), "Address should have NONE type");
        }

        @Test
        @DisplayName("different strategies per class")
        void perClass_differentStrategiesPerClass() throws IOException {
            // Global: URI, Person overrides to NAME
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_TYPE_STRATEGY, "URI");
            options.put(CodecOptions.CODEC_ECLASS_CONFIG,
                    Map.of(personClass, Map.of(
                            CodecOptions.CODEC_TYPE_STRATEGY, "NAME")));

            EObject person = createPerson("Rose", 41);
            String json = serialize(person, options);

            // Person should use NAME (overridden), not URI
            assertTrue(json.contains("\"Person\""),
                    "Person should use NAME strategy from per-class override");
            assertFalse(json.contains("http://test.example.org"),
                    "Person should not have URI from global config");
        }
    }

    // ========================================================================
    // 8. Type Scope — spec-gap tests (@Disabled)
    // ========================================================================

    @Nested
    @DisplayName("TypeScope")
    class TypeScope {

        @Test
        @Disabled("typeScope=ROOT_ONLY not yet implemented")
        @DisplayName("ROOT_ONLY: type only on root object")
        void rootOnly_typeOnlyOnRoot() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_TYPE_SCOPE, "ROOT_ONLY",
                    CodecOptions.CODEC_TYPE_STRATEGY, "NAME");

            EObject person = createPerson("Sam", 37);
            EObject address = createAddress("456 Oak Ave", "Shelbyville");
            person.eSet(personClass.getEStructuralFeature("address"), address);

            String json = serialize(person, options);

            // Root should have type, contained address should not
            assertTrue(json.contains("\"Person\""), "Root should have type info");
            assertFalse(json.contains("\"Address\""), "Contained object should not have type info");
        }

        @Test
        @Disabled("typeScope=ROOT_CONTAINMENT not yet implemented")
        @DisplayName("ROOT_CONTAINMENT: type on root and contained objects")
        void rootContainment_typeOnRootAndContained() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_TYPE_SCOPE, "ROOT_CONTAINMENT",
                    CodecOptions.CODEC_TYPE_STRATEGY, "NAME");

            EObject person = createPerson("Tina", 29);
            EObject address = createAddress("789 Pine Rd", "Capitol City");
            person.eSet(personClass.getEStructuralFeature("address"), address);

            String json = serialize(person, options);

            assertTrue(json.contains("\"Person\""), "Root should have type info");
            assertTrue(json.contains("\"Address\""), "Contained object should have type info");
        }
    }

    // ========================================================================
    // 9. Per-Reference Type Config — spec-gap tests (@Disabled)
    // ========================================================================

    @Nested
    @DisplayName("PerReferenceTypeConfig")
    class PerReferenceTypeConfig {

        @Test
//        @Disabled("Per-reference type config override not yet wired")
        @DisplayName("per-reference overrides global")
        void perReference_overridesGlobal() throws IOException {
            // Global: NONE, but the 'address' reference overrides to NAME
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_TYPE_STRATEGY, "NONE");
            options.put(CodecOptions.CODEC_EREFERENCE_CONFIG,
                    Map.of(personClass.getEStructuralFeature("address"),
                            Map.of(CodecOptions.CODEC_TYPE_STRATEGY, "NAME")));

            EObject person = createPerson("Uma", 34);
            EObject address = createAddress("321 Elm St", "Ogdenville");
            person.eSet(personClass.getEStructuralFeature("address"), address);

            String json = serialize(person, options);

            // Root person should NOT have type (NONE globally)
            assertFalse(json.contains("\"Person\""), "Root should use NONE strategy");
            // Address under the overridden reference should have NAME type
            assertTrue(json.contains("\"Address\""),
                    "Address should use NAME strategy from per-reference override");
        }
    }
}
