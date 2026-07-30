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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for DeserializationMode (LENIENT, STRICT, AUTO_DETECT).
 * <p>
 * These tests verify that:
 * <ul>
 *   <li>LENIENT (default): Try configured strategy first, then fallback resolution with warnings</li>
 *   <li>STRICT: Type field MUST match configured strategy exactly; missing/malformed → ERROR</li>
 *   <li>AUTO_DETECT: Ignore configured strategy; probe JSON structure to determine format</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-type.md#652-deserialization-mode">Spec: Deserialization Mode</a>
 */
@DisplayName("DeserializationMode Tests")
class DeserializationModeTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass personClass;

    // EAttributes
    private EAttribute nameAttribute;
    private EAttribute ageAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, DeserializationModeTest.class);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "age");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Loads JSON into an EObject using the given mode.
     */
    private CodecResource loadWithMode(String json, String deserializationMode) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://deserialization-mode.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        if (deserializationMode != null) {
            options.put(CodecOptions.CODEC_DESERIALIZATION_MODE, deserializationMode);
        }

        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        return resource;
    }

    // ========================================================================
    // LENIENT Mode Tests (Default)
    // ========================================================================

    @Nested
    @DisplayName("LENIENT Mode (Default)")
    class LenientModeTests {

        @Test
        @DisplayName("Valid JSON deserializes successfully")
        void lenient_validJson_success() throws IOException {
            String json = """
                    {
                      "name": "Alice",
                      "age": 30
                    }
                    """;

            CodecResource resource = loadWithMode(json, "LENIENT");

            assertFalse(resource.getContents().isEmpty());
            EObject person = resource.getContents().get(0);
            assertEquals("Alice", person.eGet(nameAttribute));
            assertEquals(30, person.eGet(ageAttribute));
            assertTrue(resource.getErrors().isEmpty(), "Should have no errors");
        }

        @Test
        @DisplayName("Unknown type value produces WARNING, falls back to hint")
        void lenient_unknownType_warningAndFallback() throws IOException {
            String json = """
                    {
                      "_type": "NonExistentClass",
                      "name": "Bob",
                      "age": 25
                    }
                    """;

            CodecResource resource = loadWithMode(json, "LENIENT");

            // Should still deserialize using CODEC_ROOT_TYPE hint
            assertFalse(resource.getContents().isEmpty());
            EObject person = resource.getContents().get(0);
            assertEquals("Bob", person.eGet(nameAttribute));
            assertEquals(25, person.eGet(ageAttribute));

            // Should have a warning about type resolution fallback
            assertFalse(resource.getWarnings().isEmpty(), "Should have warnings about type fallback");
        }

        @Test
        @DisplayName("Default mode (null) behaves as LENIENT")
        void lenient_defaultMode_sameBehavior() throws IOException {
            String json = """
                    {
                      "_type": "NonExistentClass",
                      "name": "Charlie",
                      "age": 35
                    }
                    """;

            CodecResource resource = loadWithMode(json, null);

            // Should still deserialize using CODEC_ROOT_TYPE hint
            assertFalse(resource.getContents().isEmpty());
            EObject person = resource.getContents().get(0);
            assertEquals("Charlie", person.eGet(nameAttribute));

            // Should have warnings (lenient mode default)
            assertFalse(resource.getWarnings().isEmpty());
        }
    }

    // ========================================================================
    // STRICT Mode Tests
    // ========================================================================

    @Nested
    @DisplayName("STRICT Mode")
    class StrictModeTests {

        @Test
        @DisplayName("Valid JSON with resolvable type succeeds")
        void strict_validType_success() throws IOException {
            // Use full URI which should resolve
            String json = """
                    {
                      "_type": "%s#//Person",
                      "name": "Alice",
                      "age": 30
                    }
                    """.formatted(testPackage.getNsURI());

            CodecResource resource = loadWithMode(json, "STRICT");

            assertFalse(resource.getContents().isEmpty());
            EObject person = resource.getContents().get(0);
            assertEquals("Alice", person.eGet(nameAttribute));
            assertEquals(30, person.eGet(ageAttribute));
            assertTrue(resource.getErrors().isEmpty(), "Should have no errors");
        }

        @Test
        @DisplayName("Unknown type value produces ERROR")
        void strict_unknownType_error() throws IOException {
            String json = """
                    {
                      "_type": "NonExistentClass",
                      "name": "Bob",
                      "age": 25
                    }
                    """;

            CodecResource resource = loadWithMode(json, "STRICT");

            // In STRICT mode, unknown type should produce an ERROR
            assertFalse(resource.getErrors().isEmpty(), "Should have errors for unknown type in STRICT mode");

            // Check the error message mentions type resolution
            boolean hasTypeError = resource.getErrors().stream()
                    .map(Diagnostic::getMessage)
                    .anyMatch(msg -> msg.contains("Could not resolve") || msg.contains("type"));
            assertTrue(hasTypeError, "Error should mention type resolution failure");
        }

        @Test
        @DisplayName("Valid JSON without type field (uses hint) succeeds")
        void strict_noTypeField_usesHint() throws IOException {
            String json = """
                    {
                      "name": "David",
                      "age": 40
                    }
                    """;

            CodecResource resource = loadWithMode(json, "STRICT");

            // No _type field is OK as long as CODEC_ROOT_TYPE is provided
            assertFalse(resource.getContents().isEmpty());
            EObject person = resource.getContents().get(0);
            assertEquals("David", person.eGet(nameAttribute));
            assertEquals(40, person.eGet(ageAttribute));
        }

        @Test
        @DisplayName("Unexpected token for type produces ERROR")
        void strict_unexpectedToken_error() throws IOException {
            String json = """
                    {
                      "_type": 123,
                      "name": "Eve",
                      "age": 28
                    }
                    """;

            CodecResource resource = loadWithMode(json, "STRICT");

            // In STRICT mode, unexpected token (number instead of string) should produce an ERROR
            assertFalse(resource.getErrors().isEmpty(), "Should have errors for unexpected token in STRICT mode");
        }
    }

    // ========================================================================
    // AUTO_DETECT Mode Tests
    // ========================================================================

    @Nested
    @DisplayName("AUTO_DETECT Mode")
    class AutoDetectModeTests {

        @Test
        @DisplayName("Detects URI format automatically")
        void autoDetect_uriFormat_success() throws IOException {
            String json = """
                    {
                      "_type": "%s#//Person",
                      "name": "Frank",
                      "age": 45
                    }
                    """.formatted(testPackage.getNsURI());

            CodecResource resource = loadWithMode(json, "AUTO_DETECT");

            assertFalse(resource.getContents().isEmpty());
            EObject person = resource.getContents().get(0);
            assertEquals("Frank", person.eGet(nameAttribute));
            assertEquals(45, person.eGet(ageAttribute));
        }

        @Test
        @DisplayName("Falls back gracefully when type cannot be resolved")
        void autoDetect_unknownType_fallsBack() throws IOException {
            String json = """
                    {
                      "_type": "UnknownType",
                      "name": "Grace",
                      "age": 50
                    }
                    """;

            CodecResource resource = loadWithMode(json, "AUTO_DETECT");

            // AUTO_DETECT should behave like LENIENT for fallback
            assertFalse(resource.getContents().isEmpty());
            EObject person = resource.getContents().get(0);
            assertEquals("Grace", person.eGet(nameAttribute));
        }
    }

    // ========================================================================
    // DeserializationMode Enum Values Tests
    // ========================================================================

    @Nested
    @DisplayName("DeserializationMode Enum Values")
    class EnumValueTests {

        @Test
        @DisplayName("DeserializationMode.LENIENT is accepted")
        void enumValue_lenient_accepted() throws IOException {
            String json = """
                    {
                      "name": "Test",
                      "age": 20
                    }
                    """;

            // Use the actual enum if available
            CodecResource resource = loadWithMode(json, "LENIENT");
            assertNotNull(resource);
            assertFalse(resource.getContents().isEmpty());
        }

        @Test
        @DisplayName("DeserializationMode.STRICT is accepted")
        void enumValue_strict_accepted() throws IOException {
            String json = """
                    {
                      "name": "Test",
                      "age": 20
                    }
                    """;

            CodecResource resource = loadWithMode(json, "STRICT");
            assertNotNull(resource);
            assertFalse(resource.getContents().isEmpty());
        }

        @Test
        @DisplayName("DeserializationMode.AUTO_DETECT is accepted")
        void enumValue_autoDetect_accepted() throws IOException {
            String json = """
                    {
                      "name": "Test",
                      "age": 20
                    }
                    """;

            CodecResource resource = loadWithMode(json, "AUTO_DETECT");
            assertNotNull(resource);
            assertFalse(resource.getContents().isEmpty());
        }
    }
}
