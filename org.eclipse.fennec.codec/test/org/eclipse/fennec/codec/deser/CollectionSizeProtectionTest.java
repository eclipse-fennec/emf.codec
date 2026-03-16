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
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for S-3: Collection Size Guard in the deserializer.
 * <p>
 * Verifies that arrays and objects with excessive element counts are truncated
 * with a warning diagnostic instead of causing OutOfMemoryError.
 * The protection applies to {@code readArrayAsList()}/{@code readObjectAsMap()} in
 * {@link CodecEObjectDeserializer} and {@code readJsonArrayAsCollection()}/
 * {@code readJsonObjectAsMap()} in {@link AttributeDeserializationEntry}.
 * </p>
 * <p>
 * Security: CWE-400 (Resource Exhaustion), CWE-770 (Allocation Without Limits).
 * </p>
 */
@DisplayName("S-3: Collection Size Protection")
class CollectionSizeProtectionTest {

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
        @DisplayName("oversized array in EJavaObject attribute produces warning")
        void oversizedArray_rejected() throws IOException {
            String json = "{\"_type\": \"Person\", \"name\": \"Alice\", \"metadata\": "
                    + buildLargeArray(CodecEObjectDeserializer.MAX_COLLECTION_SIZE + 100) + "}";

            CodecResource resource = loadJson(json);
            assertCollectionSizeDiagnostic(resource);
        }

        @Test
        @DisplayName("oversized object in EJavaObject attribute produces warning")
        void oversizedObject_rejected() throws IOException {
            String json = "{\"_type\": \"Person\", \"name\": \"Alice\", \"metadata\": "
                    + buildLargeObject(CodecEObjectDeserializer.MAX_COLLECTION_SIZE + 100) + "}";

            CodecResource resource = loadJson(json);
            assertCollectionSizeDiagnostic(resource);
        }

        @Test
        @DisplayName("small array is accepted without diagnostics")
        void smallArray_accepted() throws IOException {
            String json = """
                    {"_type": "Person", "name": "Alice", "metadata": [1, 2, 3]}
                    """;

            CodecResource resource = loadJson(json);
            assertNoDiagnostics(resource);
        }
    }

    @Nested
    @DisplayName("Deferred property path (before _type, via CodecEObjectDeserializer)")
    class DeferredPath {

        @Test
        @DisplayName("oversized deferred array produces warning")
        void oversizedDeferredArray_rejected() throws IOException {
            String json = "{\"metadata\": "
                    + buildLargeArray(CodecEObjectDeserializer.MAX_COLLECTION_SIZE + 100)
                    + ", \"_type\": \"Person\", \"name\": \"Alice\"}";

            CodecResource resource = loadJson(json);
            assertCollectionSizeDiagnostic(resource);
        }

        @Test
        @DisplayName("oversized deferred object produces warning")
        void oversizedDeferredObject_rejected() throws IOException {
            String json = "{\"metadata\": "
                    + buildLargeObject(CodecEObjectDeserializer.MAX_COLLECTION_SIZE + 100)
                    + ", \"_type\": \"Person\", \"name\": \"Alice\"}";

            CodecResource resource = loadJson(json);
            assertCollectionSizeDiagnostic(resource);
        }

        @Test
        @DisplayName("deserialization completes after truncation")
        void completesAfterTruncation() throws IOException {
            String json = "{\"metadata\": "
                    + buildLargeArray(CodecEObjectDeserializer.MAX_COLLECTION_SIZE + 100)
                    + ", \"_type\": \"Person\", \"name\": \"Alice\"}";

            CodecResource resource = loadJson(json);
            assertFalse(resource.getContents().isEmpty(), "Should still produce an EObject");
        }
    }

    @Nested
    @DisplayName("Constants")
    class Constants {

        @Test
        @DisplayName("MAX_COLLECTION_SIZE is 100000")
        void maxCollectionSizeValue() {
            assertEquals(100_000, CodecEObjectDeserializer.MAX_COLLECTION_SIZE);
        }

        @Test
        @DisplayName("AttributeDeserializationEntry uses same limit")
        void attributeEntrySharesLimit() {
            assertEquals(CodecEObjectDeserializer.MAX_COLLECTION_SIZE,
                    AttributeDeserializationEntry.MAX_COLLECTION_SIZE);
        }
    }

    // ========================================================================
    // Helper methods
    // ========================================================================

    private void assertCollectionSizeDiagnostic(CodecResource resource) {
        boolean found = resource.getErrors().stream()
                .anyMatch(d -> d.getMessage() != null
                        && d.getMessage().contains("exceeds maximum size"))
                || resource.getWarnings().stream()
                .anyMatch(d -> d.getMessage() != null
                        && d.getMessage().contains("exceeds maximum size"));
        assertTrue(found,
                "Expected 'exceeds maximum size' diagnostic on resource, got errors: "
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
                URI.createURI("test://collection-size.json"), metadataService, resolver, null);

        Map<String, Object> options = Map.of(
                CodecResource.CODEC_ROOT_TYPE, personClass
        );

        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource;
    }

    /**
     * Builds a JSON array with the specified number of integer elements.
     * Example at count 3: [0,1,2]
     */
    private String buildLargeArray(int count) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(i);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Builds a JSON object with the specified number of properties.
     * Example at count 3: {"k0":0,"k1":1,"k2":2}
     */
    private String buildLargeObject(int count) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"k").append(i).append("\":").append(i);
        }
        sb.append("}");
        return sb.toString();
    }
}
