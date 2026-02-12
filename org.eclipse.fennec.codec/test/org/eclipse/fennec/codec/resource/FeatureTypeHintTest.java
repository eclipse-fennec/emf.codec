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
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for CODEC_FEATURE_TYPE_HINTS load option.
 * <p>
 * Tests that EClass type hints provided via load options are correctly used
 * to deserialize EObject-typed features where the concrete type cannot be
 * determined from JSON alone.
 * </p>
 * <p>
 * See specification: docs/codec-v2-spec/18-feature-type-hints.md
 * </p>
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.FeatureTypeHintTest}
 * </p>
 */
@DisplayName("Feature Type Hint Tests")
class FeatureTypeHintTest {

    private static final String TEST_ECORE = "test-feature-type-hints.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass personClass;
    private EClass addressClass;
    private EClass exampleClass;
    private EClass extensionClass;
    private EClass containerClass;

    // EReferences
    private EReference exampleValueRef;
    private EReference extensionValueRef;
    private EReference containerItemsRef;

    // EAttributes
    private EAttribute personNameAttr;
    private EAttribute personAgeAttr;
    private EAttribute addressStreetAttr;
    private EAttribute addressCityAttr;
    private EAttribute exampleSummaryAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(FeatureTypeHintTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute("/org/eclipse/fennec/codec/resource/" + TEST_ECORE);

        // Register package in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load EClasses
        personClass = ecoreHelper.getEClass(testPackage, "Person");
        addressClass = ecoreHelper.getEClass(testPackage, "Address");
        exampleClass = ecoreHelper.getEClass(testPackage, "Example");
        extensionClass = ecoreHelper.getEClass(testPackage, "Extension");
        containerClass = ecoreHelper.getEClass(testPackage, "Container");

        // Load EReferences
        exampleValueRef = (EReference) ecoreHelper.getFeature(exampleClass, "value");
        extensionValueRef = (EReference) ecoreHelper.getFeature(extensionClass, "value");
        containerItemsRef = (EReference) ecoreHelper.getFeature(containerClass, "items");

        // Load EAttributes
        personNameAttr = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        personAgeAttr = (EAttribute) ecoreHelper.getFeature(personClass, "age");
        addressStreetAttr = (EAttribute) ecoreHelper.getFeature(addressClass, "street");
        addressCityAttr = (EAttribute) ecoreHelper.getFeature(addressClass, "city");
        exampleSummaryAttr = (EAttribute) ecoreHelper.getFeature(exampleClass, "summary");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://feature-type-hint.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    // ========================================================================
    // Basic Type Hint Tests
    // ========================================================================

    @Nested
    @DisplayName("Basic Type Hint Resolution")
    class BasicTypeHintResolution {

        @Test
        @DisplayName("deserializes EObject-typed feature using type hint")
        void deserializesEObjectTypedFeatureUsingTypeHint() throws IOException {
            // JSON with Example containing a value that should be deserialized as Person
            String json = """
                {
                  "summary": "A person example",
                  "value": {
                    "name": "John Doe",
                    "age": 30
                  }
                }
                """;

            // Provide type hint for Example.value -> Person
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(exampleValueRef, personClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize
            EObject example = deserialize(json, options);

            // Verify
            assertNotNull(example);
            assertEquals("A person example", example.eGet(exampleSummaryAttr));

            EObject value = (EObject) example.eGet(exampleValueRef);
            assertNotNull(value, "Value should be deserialized");
            assertEquals(personClass, value.eClass(), "Value should be Person type");
            assertEquals("John Doe", value.eGet(personNameAttr));
            assertEquals(30, value.eGet(personAgeAttr));
        }

        @Test
        @DisplayName("deserializes with different type hints for different features")
        void deserializesWithDifferentTypeHintsForDifferentFeatures() throws IOException {
            // JSON with Container having Example (with Person value) and Extension (with Address value)
            String json = """
                {
                  "name": "Test Container",
                  "example": {
                    "summary": "Person example",
                    "value": {
                      "name": "Alice",
                      "age": 25
                    }
                  },
                  "extensions": [
                    {
                      "name": "x-location",
                      "value": {
                        "street": "123 Main St",
                        "city": "Springfield"
                      }
                    }
                  ]
                }
                """;

            // Provide different type hints for different features
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(exampleValueRef, personClass);
            typeHints.put(extensionValueRef, addressClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, containerClass);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize
            EObject container = deserialize(json, options);

            // Verify Container
            assertNotNull(container);
            assertEquals("Test Container", container.eGet(containerClass.getEStructuralFeature("name")));

            // Verify Example value is Person
            EObject example = (EObject) container.eGet(containerClass.getEStructuralFeature("example"));
            assertNotNull(example);
            EObject exampleValue = (EObject) example.eGet(exampleValueRef);
            assertNotNull(exampleValue, "Example value should be deserialized");
            assertEquals(personClass, exampleValue.eClass(), "Example value should be Person");
            assertEquals("Alice", exampleValue.eGet(personNameAttr));

            // Verify Extension value is Address
            @SuppressWarnings("unchecked")
            List<EObject> extensions = (List<EObject>) container.eGet(containerClass.getEStructuralFeature("extensions"));
            assertEquals(1, extensions.size());
            EObject extension = extensions.get(0);
            EObject extensionValue = (EObject) extension.eGet(extensionValueRef);
            assertNotNull(extensionValue, "Extension value should be deserialized");
            assertEquals(addressClass, extensionValue.eClass(), "Extension value should be Address");
            assertEquals("123 Main St", extensionValue.eGet(addressStreetAttr));
            assertEquals("Springfield", extensionValue.eGet(addressCityAttr));
        }
    }

    // ========================================================================
    // Multi-Valued Feature Tests
    // ========================================================================

    @Nested
    @DisplayName("Multi-Valued Feature Type Hints")
    class MultiValuedFeatureTypeHints {

        @Test
        @DisplayName("type hint applies to all elements in multi-valued feature")
        void typeHintAppliesToAllElementsInMultiValuedFeature() throws IOException {
            // JSON with Container having multiple items (all should be Person)
            String json = """
                {
                  "name": "People Container",
                  "items": [
                    { "name": "John", "age": 30 },
                    { "name": "Jane", "age": 25 },
                    { "name": "Bob", "age": 40 }
                  ]
                }
                """;

            // Provide type hint for Container.items -> Person
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(containerItemsRef, personClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, containerClass);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize
            EObject container = deserialize(json, options);

            // Verify
            assertNotNull(container);
            @SuppressWarnings("unchecked")
            List<EObject> items = (List<EObject>) container.eGet(containerItemsRef);
            assertEquals(3, items.size(), "Should have 3 items");

            // All items should be Person type
            for (EObject item : items) {
                assertEquals(personClass, item.eClass(), "All items should be Person type");
            }

            assertEquals("John", items.get(0).eGet(personNameAttr));
            assertEquals(30, items.get(0).eGet(personAgeAttr));
            assertEquals("Jane", items.get(1).eGet(personNameAttr));
            assertEquals(25, items.get(1).eGet(personAgeAttr));
            assertEquals("Bob", items.get(2).eGet(personNameAttr));
            assertEquals(40, items.get(2).eGet(personAgeAttr));
        }
    }

    // ========================================================================
    // Priority Tests
    // ========================================================================

    @Nested
    @DisplayName("Type Hint Priority")
    class TypeHintPriority {

        @Test
        @DisplayName("_type field in JSON overrides type hint")
        void typeFieldInJsonOverridesTypeHint() throws IOException {
            // JSON with explicit _type field that differs from hint
            String json = """
                {
                  "summary": "Should be Address despite hint",
                  "value": {
                    "_type": "Address",
                    "street": "Override Street",
                    "city": "Override City"
                  }
                }
                """;

            // Provide type hint for Person, but JSON has _type: Address
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(exampleValueRef, personClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize
            EObject example = deserialize(json, options);

            // Verify that _type wins over type hint
            assertNotNull(example);
            EObject value = (EObject) example.eGet(exampleValueRef);
            assertNotNull(value, "Value should be deserialized");
            assertEquals(addressClass, value.eClass(), "_type field should override type hint");
            assertEquals("Override Street", value.eGet(addressStreetAttr));
            assertEquals("Override City", value.eGet(addressCityAttr));
        }

        @Test
        @DisplayName("_type field overrides type hint per element in array")
        void typeFieldOverridesTypeHintPerElementInArray() throws IOException {
            // JSON with mixed types - some use hint, some have explicit _type
            String json = """
                {
                  "name": "Mixed Container",
                  "items": [
                    { "name": "John", "age": 30 },
                    { "_type": "Address", "street": "123 Main", "city": "NYC" },
                    { "name": "Jane", "age": 25 }
                  ]
                }
                """;

            // Provide type hint for Person
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(containerItemsRef, personClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, containerClass);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize
            EObject container = deserialize(json, options);

            // Verify
            assertNotNull(container);
            @SuppressWarnings("unchecked")
            List<EObject> items = (List<EObject>) container.eGet(containerItemsRef);
            assertEquals(3, items.size());

            // First item: Person (uses hint)
            assertEquals(personClass, items.get(0).eClass());
            assertEquals("John", items.get(0).eGet(personNameAttr));

            // Second item: Address (_type overrides hint)
            assertEquals(addressClass, items.get(1).eClass());
            assertEquals("123 Main", items.get(1).eGet(addressStreetAttr));

            // Third item: Person (uses hint)
            assertEquals(personClass, items.get(2).eClass());
            assertEquals("Jane", items.get(2).eGet(personNameAttr));
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip with Type Hints")
    class RoundTripWithTypeHints {

        @Test
        @DisplayName("round-trips EObject-typed feature with type hint")
        void roundTripsEObjectTypedFeatureWithTypeHint() throws IOException {
            // Create Example with Person value
            EObject example = testPackage.getEFactoryInstance().create(exampleClass);
            example.eSet(exampleSummaryAttr, "Person example");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Round Trip Test");
            person.eSet(personAgeAttr, 42);
            example.eSet(exampleValueRef, person);

            // Serialize (no special options needed for serialization)
            String json = serialize(example);
            System.out.println("Serialized JSON:\n" + json);

            // Verify JSON contains Person data
            assertTrue(json.contains("\"name\":\"Round Trip Test\""));
            assertTrue(json.contains("\"age\":42"));

            // Deserialize with type hint
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(exampleValueRef, personClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            EObject loaded = deserialize(json, options);

            // Verify round-trip
            assertNotNull(loaded);
            assertEquals("Person example", loaded.eGet(exampleSummaryAttr));

            EObject loadedValue = (EObject) loaded.eGet(exampleValueRef);
            assertNotNull(loadedValue);
            assertEquals(personClass, loadedValue.eClass());
            assertEquals("Round Trip Test", loadedValue.eGet(personNameAttr));
            assertEquals(42, loadedValue.eGet(personAgeAttr));
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String serialize(EObject object) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, Map<String, Object> options) throws IOException {
        CodecResource resource = createResource();

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
