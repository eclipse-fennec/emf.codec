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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.deser.DeserializationEntryTestBase;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.StreamReadConstraints;

/**
 * Tests for S-6: Jackson StreamReadConstraints Configuration.
 * <p>
 * Verifies that the codec uses hardened StreamReadConstraints instead of
 * Jackson 3.1.0 defaults. The tighter limits protect against memory
 * exhaustion and stack overflow from malicious payloads.
 * </p>
 * <p>
 * Security: CWE-400 (Resource Exhaustion).
 * </p>
 */
@DisplayName("S-6: StreamReadConstraints Configuration")
class StreamReadConstraintsTest {

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
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Nested
    @DisplayName("Constraint values")
    class ConstraintValues {

        @Test
        @DisplayName("STREAM_READ_CONSTRAINTS is not null")
        void constraintsNotNull() {
            assertNotNull(CodecResource.STREAM_READ_CONSTRAINTS);
        }

        @Test
        @DisplayName("max nesting depth is 500 (backstop above codec's own 200 limit)")
        void maxNestingDepth() {
            assertEquals(500, CodecResource.STREAM_READ_CONSTRAINTS.getMaxNestingDepth());
        }

        @Test
        @DisplayName("max string length is 10 MB (not Jackson default 20 MB)")
        void maxStringLength() {
            assertEquals(10_000_000, CodecResource.STREAM_READ_CONSTRAINTS.getMaxStringLength());
        }

        @Test
        @DisplayName("max name length is 10 KB (not Jackson default 50 KB)")
        void maxNameLength() {
            assertEquals(10_000, CodecResource.STREAM_READ_CONSTRAINTS.getMaxNameLength());
        }

        @Test
        @DisplayName("string and name constraints are tighter than Jackson defaults")
        void tighterThanDefaults() {
            StreamReadConstraints defaults = StreamReadConstraints.defaults();
            StreamReadConstraints codec = CodecResource.STREAM_READ_CONSTRAINTS;

            // Nesting depth is kept at Jackson default (500) as backstop;
            // the codec's own MAX_NESTING_DEPTH (200) provides the real limit
            assertTrue(codec.getMaxStringLength() < defaults.getMaxStringLength(),
                    "Codec string length should be less than Jackson default");
            assertTrue(codec.getMaxNameLength() < defaults.getMaxNameLength(),
                    "Codec name length should be less than Jackson default");
        }
    }

    @Nested
    @DisplayName("End-to-end: oversized field name rejected")
    class OversizedFieldName {

        @Test
        @DisplayName("field name exceeding 10KB produces error")
        void oversizedFieldName_rejected() throws IOException {
            // Build a JSON with a field name larger than 10KB
            String longFieldName = "x".repeat(11_000);
            String json = "{\"_type\": \"Person\", \"" + longFieldName + "\": \"value\"}";

            MetadataWhiteboard metadataService = MetadataServiceFactory.create();
            metadataService.registerPackage(testPackage);
            ConfigurationResolver resolver = ConfigurationResolver.defaults();
            CodecResource resource = new CodecResource(
                    URI.createURI("test://oversized-field.json"), metadataService, resolver, null);

            Map<String, Object> options = Map.of(
                    CodecResource.CODEC_ROOT_TYPE, personClass
            );

            try {
                resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
            } catch (Exception e) {
                // Jackson should reject the oversized field name
                assertTrue(e.getMessage() != null
                        && (e.getMessage().contains("name length") || e.getMessage().contains("Name")),
                        "Expected field name length error, got: " + e.getMessage());
                return;
            }

            // If no exception, check for errors on the resource
            boolean hasError = !resource.getErrors().isEmpty();
            assertTrue(hasError, "Expected error for oversized field name");
        }
    }
}
