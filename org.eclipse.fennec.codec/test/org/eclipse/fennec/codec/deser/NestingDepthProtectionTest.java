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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for S-2: Nesting Depth Protection in the deserializer.
 * <p>
 * Verifies that deeply nested JSON structures are rejected with a clear error
 * instead of causing a StackOverflowError. Protection is applied in two places:
 * <ul>
 *   <li>{@link CodecEObjectDeserializer} — deferred properties (before {@code _type})</li>
 *   <li>{@link AttributeDeserializationEntry} — EJavaObject attributes (after {@code _type})</li>
 * </ul>
 * </p>
 * <p>
 * Security: CWE-674 (Uncontrolled Recursion), CWE-400 (Resource Exhaustion).
 * </p>
 */
@DisplayName("S-2: Nesting Depth Protection")
class NestingDepthProtectionTest {

    private static final String TEST_ECORE = "test-deserialization.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private EClass personClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, DeserializationEntryTestBase.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        personClass = EcoreHelper.getEClass(testPackage, "Person");
    }

    @AfterEach
    void tearDown() {
        ecoreHelper.releaseAll();
    }

    @Nested
    @DisplayName("Attribute path (after _type, via AttributeDeserializationEntry)")
    class AttributePath {

        @Test
        @DisplayName("deeply nested objects beyond limit produce resource warning")
        void deeplyNestedObjects_rejected() throws IOException {
            // metadata is EJavaObject → goes through readAnyJsonValue → readJsonObjectAsMap recursion
            int depth = CodecEObjectDeserializer.MAX_NESTING_DEPTH + 10;
            String nestedValue = buildNestedObjectValue(depth);
            String json = "{\"_type\": \"Person\", \"name\": \"Alice\", \"metadata\": " + nestedValue + "}";

            CodecResource resource = loadJson(json);
            assertNestingDepthDiagnostic(resource);
        }

        @Test
        @DisplayName("deeply nested arrays beyond limit produce resource warning")
        void deeplyNestedArrays_rejected() throws IOException {
            int depth = CodecEObjectDeserializer.MAX_NESTING_DEPTH + 10;
            String nestedValue = buildNestedArrayValue(depth);
            String json = "{\"_type\": \"Person\", \"name\": \"Alice\", \"metadata\": " + nestedValue + "}";

            CodecResource resource = loadJson(json);
            assertNestingDepthDiagnostic(resource);
        }

        @Test
        @DisplayName("mixed object/array nesting beyond limit produces resource warning")
        void mixedNesting_rejected() throws IOException {
            int depth = CodecEObjectDeserializer.MAX_NESTING_DEPTH + 10;
            String nestedValue = buildMixedNestingValue(depth);
            String json = "{\"_type\": \"Person\", \"name\": \"Alice\", \"metadata\": " + nestedValue + "}";

            CodecResource resource = loadJson(json);
            assertNestingDepthDiagnostic(resource);
        }

        @Test
        @DisplayName("objects within limit are accepted without diagnostics")
        void objectsWithinLimit_accepted() throws IOException {
            String json = """
                    {"_type": "Person", "name": "Alice", "metadata": {"a": {"b": {"c": "deep"}}}}
                    """;

            CodecResource resource = loadJson(json);
            assertNoDiagnostics(resource);
        }
    }

    @Nested
    @DisplayName("Deferred property path (before _type, via CodecEObjectDeserializer)")
    class DeferredPath {

        @Test
        @DisplayName("deeply nested deferred objects beyond limit produce resource warning")
        void deeplyNestedDeferredObjects_rejected() throws IOException {
            // metadata BEFORE _type → readCurrentValue skips children, adds warning,
            // returns null. The null deferred value is dropped during replay.
            int depth = CodecEObjectDeserializer.MAX_NESTING_DEPTH + 10;
            String nestedValue = buildNestedObjectValue(depth);
            String json = "{\"metadata\": " + nestedValue + ", \"_type\": \"Person\", \"name\": \"Alice\"}";

            CodecResource resource = loadJson(json);
            assertFalse(resource.getContents().isEmpty(), "Should still produce an EObject");
            assertNestingDepthDiagnostic(resource);
        }

        @Test
        @DisplayName("deeply nested deferred arrays beyond limit produce resource warning")
        void deeplyNestedDeferredArrays_rejected() throws IOException {
            int depth = CodecEObjectDeserializer.MAX_NESTING_DEPTH + 10;
            String nestedValue = buildNestedArrayValue(depth);
            String json = "{\"metadata\": " + nestedValue + ", \"_type\": \"Person\", \"name\": \"Alice\"}";

            CodecResource resource = loadJson(json);
            assertFalse(resource.getContents().isEmpty(), "Should still produce an EObject");
            assertNestingDepthDiagnostic(resource);
        }

        @Test
        @DisplayName("deferred objects within limit are accepted without diagnostics")
        void deferredWithinLimit_accepted() throws IOException {
            String json = """
                    {"metadata": {"a": {"b": {"c": "deep"}}}, "_type": "Person", "name": "Alice"}
                    """;

            CodecResource resource = loadJson(json);
            assertNoDiagnostics(resource);
        }
    }

    @Nested
    @DisplayName("Constants")
    class Constants {

        @Test
        @DisplayName("MAX_NESTING_DEPTH is 200")
        void maxNestingDepthValue() {
            assertEquals(200, CodecEObjectDeserializer.MAX_NESTING_DEPTH);
        }

        @Test
        @DisplayName("AttributeDeserializationEntry uses same limit")
        void attributeEntrySharesLimit() {
            assertEquals(CodecEObjectDeserializer.MAX_NESTING_DEPTH,
                    AttributeDeserializationEntry.MAX_NESTING_DEPTH);
        }
    }

    // ========================================================================
    // Helper methods
    // ========================================================================

    private void assertNestingDepthDiagnostic(CodecResource resource) {
        // Check errors and warnings for the nesting depth message
        boolean found = resource.getErrors().stream()
                .anyMatch(d -> d.getMessage() != null
                        && d.getMessage().contains("Maximum nesting depth exceeded"))
                || resource.getWarnings().stream()
                .anyMatch(d -> d.getMessage() != null
                        && d.getMessage().contains("Maximum nesting depth exceeded"));
        assertTrue(found,
                "Expected 'Maximum nesting depth exceeded' diagnostic on resource, got errors: "
                + resource.getErrors() + ", warnings: " + resource.getWarnings());
    }

    private void assertNoDiagnostics(CodecResource resource) {
        assertTrue(resource.getErrors().isEmpty(),
                "Expected no errors, got: " + resource.getErrors());
        assertTrue(resource.getWarnings().isEmpty(),
                "Expected no warnings, got: " + resource.getWarnings());
    }

    private CodecResource loadJson(String json) throws IOException {
        MetadataWhiteboard metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
        ConfigurationResolver resolver = ConfigurationResolver.defaults();
        CodecResource resource = new CodecResource(
                URI.createURI("test://nesting.json"), metadataService, resolver, null);

        Map<String, Object> options = Map.of(
                CodecResource.CODEC_ROOT_TYPE, personClass
        );

        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource;
    }

    /**
     * Builds a deeply nested object value.
     * Example at depth 3: {"a":{"a":{"a":"end"}}}
     */
    private String buildNestedObjectValue(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("{\"a\":");
        }
        sb.append("\"end\"");
        for (int i = 0; i < depth; i++) {
            sb.append("}");
        }
        return sb.toString();
    }

    /**
     * Builds a deeply nested array value.
     * Example at depth 3: [[[1]]]
     */
    private String buildNestedArrayValue(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("[");
        }
        sb.append("1");
        for (int i = 0; i < depth; i++) {
            sb.append("]");
        }
        return sb.toString();
    }

    /**
     * Builds a value with alternating object/array nesting.
     * Example at depth 4: {"a":[{"a":[1]}]}
     */
    private String buildMixedNestingValue(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            if (i % 2 == 0) {
                sb.append("{\"a\":");
            } else {
                sb.append("[");
            }
        }
        sb.append("1");
        for (int i = depth - 1; i >= 0; i--) {
            if (i % 2 == 0) {
                sb.append("}");
            } else {
                sb.append("]");
            }
        }
        return sb.toString();
    }
}
