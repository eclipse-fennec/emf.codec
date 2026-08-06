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

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Reading a truncated document must terminate (issue #132).
 * <p>
 * A token loop comparing only against its closing token spins forever once the parser
 * returns {@code null}. The failure mode is not a wrong result but a hang: no error, no
 * output, nothing to look at, and a blocked build. These tests therefore assert with a
 * timeout — a regression fails here instead of stopping the suite.
 * </p>
 * <p>
 * What the reader reports about the broken content is deliberately not asserted; the point
 * is that it comes back at all.
 * </p>
 */
@DisplayName("Truncated documents terminate")
class TruncatedDocumentTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/ser/test-roundtrip.ecore";
    private static final Duration LIMIT = Duration.ofSeconds(10);

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, TruncatedDocumentTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
        personClass = EcoreHelper.getEClass(testPackage, "Person");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("a document cut off inside a multi-valued attribute terminates")
    void truncatedInsideAttributeArray() {
        assertTimeoutPreemptively(LIMIT, () -> load("""
                {
                  "_type": "%s#//Person",
                  "name": "Alice",
                  "tags": ["a", "b"
                """.formatted(testPackage.getNsURI())));
    }

    @Test
    @DisplayName("a document cut off inside a containment object terminates")
    void truncatedInsideContainedObject() {
        assertTimeoutPreemptively(LIMIT, () -> load("""
                {
                  "_type": "%s#//Person",
                  "name": "Alice",
                  "address": { "street": "Main Street 1"
                """.formatted(testPackage.getNsURI())));
    }

    @Test
    @DisplayName("a document cut off inside a reference array terminates")
    void truncatedInsideReferenceArray() {
        assertTimeoutPreemptively(LIMIT, () -> load("""
                {
                  "_type": "%s#//Person",
                  "name": "Alice",
                  "friends": [ { "_type": "%s#//Person", "name": "Bob" }
                """.formatted(testPackage.getNsURI(), testPackage.getNsURI())));
    }

    @Test
    @DisplayName("a document cut off right after the type terminates")
    void truncatedAfterType() {
        assertTimeoutPreemptively(LIMIT, () -> load("""
                { "_type": "%s#//Person",
                """.formatted(testPackage.getNsURI())));
    }

    /** Loads the fragment; any outcome is fine as long as the call returns. */
    private void load(String json) {
        CodecResource resource = new CodecResource(URI.createURI("test://truncated.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        try {
            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        } catch (Exception expected) {
            // A broken document may well be rejected - that is a fine answer. Hanging is not.
        }
    }
}
