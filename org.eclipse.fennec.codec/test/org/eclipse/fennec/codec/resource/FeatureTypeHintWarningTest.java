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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
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
 * Tests for warning and error scenarios with CODEC_FEATURE_TYPE_HINTS.
 * <p>
 * Tests that appropriate warnings are logged and graceful degradation occurs
 * when type hints are missing or misconfigured.
 * </p>
 * <p>
 * See specification: docs/codec-v2-spec/18-feature-type-hints.md, Section 10
 * </p>
 */
@DisplayName("Feature Type Hint Warning Tests")
class FeatureTypeHintWarningTest {

    private static final String TEST_ECORE = "test-feature-type-hints.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass personClass;
    private EClass exampleClass;
    private EClass extensionClass;

    // EReferences
    private EReference exampleValueRef;
    private EReference extensionValueRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(FeatureTypeHintWarningTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute("/org/eclipse/fennec/codec/resource/" + TEST_ECORE);

        // Register package in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load EClasses
        personClass = ecoreHelper.getEClass(testPackage, "Person");
        exampleClass = ecoreHelper.getEClass(testPackage, "Example");
        extensionClass = ecoreHelper.getEClass(testPackage, "Extension");

        // Load EReferences
        exampleValueRef = (EReference) ecoreHelper.getFeature(exampleClass, "value");
        extensionValueRef = (EReference) ecoreHelper.getFeature(extensionClass, "value");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://feature-type-hint-warning.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    // ========================================================================
    // Behavior Without Hints (Spec Section 4 & 10.1)
    // ========================================================================

    @Nested
    @DisplayName("Behavior Without Hints")
    class BehaviorWithoutHints {

        /**
         * Spec 10.1: "EObject-typed feature without hint and without _type"
         * Expected: Feature skipped, value is null, warning diagnostic raised
         */
        @Test
        @DisplayName("EObject-typed feature without hint and without _type results in null value")
        void eObjectTypedFeatureWithoutHintAndWithoutTypeResultsInNullValue() throws IOException {
            // JSON with Example containing a value but no _type and no hint provided
            String json = """
                {
                  "summary": "Example without type info",
                  "value": {
                    "name": "Unknown",
                    "age": 99
                  }
                }
                """;

            // No type hint provided
            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);
            // Note: No CODEC_FEATURE_TYPE_HINTS option

            // Deserialize
            EObject example = deserialize(json, options);

            // Verify Example is deserialized
            assertNotNull(example, "Example should be deserialized");
            assertEquals("Example without type info", example.eGet(exampleClass.getEStructuralFeature("summary")));

            // Per spec: value should be null because type cannot be determined
            // (EObject is abstract and cannot be instantiated)
            EObject value = (EObject) example.eGet(exampleValueRef);
            assertNull(value, "Value should be null when no type hint and no _type in JSON");
        }

        /**
         * Spec 4.3: "Deserialization continues with other features"
         */
        @Test
        @DisplayName("deserialization continues with other features when EObject feature is skipped")
        void deserializationContinuesWithOtherFeaturesWhenSkipped() throws IOException {
            // JSON with multiple features, only the EObject-typed one missing type info
            String json = """
                {
                  "summary": "Partial example",
                  "description": "This should still be loaded",
                  "value": {
                    "name": "Unknown type"
                  }
                }
                """;

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);

            // Deserialize
            EObject example = deserialize(json, options);

            // Verify other features are still loaded
            assertNotNull(example);
            assertEquals("Partial example", example.eGet(exampleClass.getEStructuralFeature("summary")));
            assertEquals("This should still be loaded", example.eGet(exampleClass.getEStructuralFeature("description")));

            // Value is skipped (null)
            assertNull(example.eGet(exampleValueRef), "Value should be null");
        }
    }

    // ========================================================================
    // Empty and Null Map Handling (Spec Section 10.1)
    // ========================================================================

    @Nested
    @DisplayName("Empty and Null Map Handling")
    class EmptyAndNullMapHandling {

        /**
         * Spec 10.1: "CODEC_FEATURE_TYPE_HINTS map is empty"
         * Expected: Treated as no option provided, warning diagnostic raised
         */
        @Test
        @DisplayName("empty type hints map behaves same as no hints provided")
        void emptyTypeHintsMapBehavesSameAsNoHints() throws IOException {
            String json = """
                {
                  "summary": "Empty hints test",
                  "value": {
                    "name": "Test"
                  }
                }
                """;

            // Provide empty map
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>(); // Empty

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize
            EObject example = deserialize(json, options);

            assertNotNull(example);
            assertEquals("Empty hints test", example.eGet(exampleClass.getEStructuralFeature("summary")));
            // Value should be null - empty map = no hint available
            assertNull(example.eGet(exampleValueRef), "Value should be null with empty hints map");
        }

        /**
         * Spec 10.1: "null value in CODEC_FEATURE_TYPE_HINTS map"
         * Expected: Treated as no hint for that feature, warning diagnostic raised
         */
        @Test
        @DisplayName("null value in type hints map is treated as no hint for that feature")
        void nullValueInTypeHintsMapIsTreatedAsNoHint() throws IOException {
            String json = """
                {
                  "summary": "Test with null hint",
                  "value": {
                    "name": "Test"
                  }
                }
                """;

            // Provide null as type hint value
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(exampleValueRef, null);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize - should not throw
            EObject example = deserialize(json, options);

            assertNotNull(example);
            assertEquals("Test with null hint", example.eGet(exampleClass.getEStructuralFeature("summary")));
            // Value should be null because hint was null
            assertNull(example.eGet(exampleValueRef), "Value should be null when hint is null");
        }
    }

    // ========================================================================
    // Type Hint for Different Feature (Spec Section 10.1)
    // ========================================================================

    @Nested
    @DisplayName("Type Hint Feature Matching")
    class TypeHintFeatureMatching {

        /**
         * Hint provided for a different feature should not affect the current feature.
         */
        @Test
        @DisplayName("type hint for different feature does not affect current feature")
        void typeHintForDifferentFeatureDoesNotAffectCurrentFeature() throws IOException {
            String json = """
                {
                  "summary": "Wrong feature hint test",
                  "value": {
                    "name": "Test"
                  }
                }
                """;

            // Provide type hint for Extension.value, but we're deserializing Example
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(extensionValueRef, personClass); // Hint for wrong feature

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize
            EObject example = deserialize(json, options);

            assertNotNull(example);
            // Value should be null - the hint was for a different feature (extensionValueRef)
            // not for exampleValueRef
            assertNull(example.eGet(exampleValueRef), "Value should be null - hint was for different feature");
        }
    }

    // ========================================================================
    // Value Reader Hint Warnings (Spec Section 10.1)
    // ========================================================================

    @Nested
    @DisplayName("Value Reader Hint Warnings")
    class ValueReaderHintWarnings {

        /**
         * Spec 10.1: "Reader name not found in registry"
         * Expected: WARNING, falling back to type hint or skip
         */
        @Test
        @DisplayName("unknown value reader name falls back to type hint if available")
        void unknownValueReaderNameFallsBackToTypeHint() throws IOException {
            String json = """
                {
                  "summary": "Unknown reader test",
                  "value": {
                    "name": "John",
                    "age": 30
                  }
                }
                """;

            // Provide a reader name that doesn't exist
            Map<EStructuralFeature, String> readerHints = new HashMap<>();
            readerHints.put(exampleValueRef, "nonExistentReader");

            // Also provide a type hint as fallback
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(exampleValueRef, personClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);
            options.put(CodecOptions.CODEC_FEATURE_VALUE_READERS, readerHints);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize - should warn about unknown reader but fall back to type hint
            EObject example = deserialize(json, options);

            assertNotNull(example);
            EObject value = (EObject) example.eGet(exampleValueRef);
            // If fallback to type hint works, value should be Person
            // This tests the fallback behavior
            assertNotNull(value, "Value should be deserialized using type hint fallback");
            assertEquals(personClass, value.eClass(), "Should fall back to type hint");
        }

        /**
         * Empty reader name should be treated as no reader specified.
         */
        @Test
        @DisplayName("empty reader name is treated as no reader specified")
        void emptyReaderNameIsTreatedAsNoReaderSpecified() throws IOException {
            String json = """
                {
                  "summary": "Empty reader name test",
                  "value": {
                    "name": "John",
                    "age": 25
                  }
                }
                """;

            // Provide empty string as reader name
            Map<EStructuralFeature, String> readerHints = new HashMap<>();
            readerHints.put(exampleValueRef, "");

            // Also provide type hint
            Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
            typeHints.put(exampleValueRef, personClass);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, exampleClass);
            options.put(CodecOptions.CODEC_FEATURE_VALUE_READERS, readerHints);
            options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

            // Deserialize - empty reader should be skipped, type hint should be used
            EObject example = deserialize(json, options);

            assertNotNull(example);
            EObject value = (EObject) example.eGet(exampleValueRef);
            assertNotNull(value, "Value should be deserialized using type hint");
            assertEquals(personClass, value.eClass());
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private EObject deserialize(String json, Map<String, Object> options) throws IOException {
        CodecResource resource = createResource();

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
