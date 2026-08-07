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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Type resolution failures reach the resource, not only the logger (issue #134).
 * <p>
 * {@code TypeResolutionHelper} is a static utility without a context, so its ten distinct
 * "could not resolve" messages went to the JUL logger alone. A caller inspecting
 * {@link org.eclipse.emf.ecore.resource.Resource#getErrors()} saw nothing of them - the
 * reason a type failed to resolve was the one thing not on record.
 * </p>
 * <p>
 * The generic fallback warning the entry adds is not enough: it says <em>that</em> resolution
 * fell back, never <em>why</em>. Distinguishing an unregistered package from a misspelled
 * class name is exactly what a caller needs to fix its data.
 * </p>
 */
@DisplayName("Type resolution diagnostics reach the resource")
class DiagnosticsReachTheResourceTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, DiagnosticsReachTheResourceTest.class);
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
    @DisplayName("the reason reaches the resource, naming what was searched")
    void reasonReachesTheResource() throws IOException {
        String json = "{\"_type\":\"http://not.registered.example.org/1.0#//Person\",\"name\":\"A\"}";

        CodecResource resource = load(json);

        assertTrue(messages(resource).anyMatch(m -> m.contains("Could not resolve EClass")
                        && m.contains(testPackage.getNsURI())),
                "the package that was searched is the actionable part, was: " + all(resource));
    }

    @Test
    @DisplayName("the generic fallback warning is no longer the only record")
    void reasonAccompaniesTheFallbackWarning() throws IOException {
        String json = "{\"_type\":\"" + testPackage.getNsURI() + "#//NoSuchClass\",\"name\":\"A\"}";

        CodecResource resource = load(json);

        assertTrue(messages(resource).anyMatch(m -> m.contains("Type resolved via fallback")),
                "the entry still reports that resolution fell back, was: " + all(resource));
        assertTrue(messages(resource).anyMatch(m -> m.contains("Could not resolve EClass")),
                "and now the helper says why, was: " + all(resource));
    }

    @Test
    @DisplayName("a clean load stays clean")
    void cleanLoadHasNoDiagnostics() throws IOException {
        String json = "{\"_type\":\"" + testPackage.getNsURI() + "#//Person\",\"name\":\"A\"}";

        CodecResource resource = load(json);

        assertTrue(resource.getErrors().isEmpty() && resource.getWarnings().isEmpty(),
                "reporting more must not mean reporting noise, was: " + all(resource));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private Stream<String> messages(CodecResource resource) {
        return Stream.concat(resource.getErrors().stream(), resource.getWarnings().stream())
                .map(Diagnostic::getMessage);
    }

    private String all(CodecResource resource) {
        return "errors=" + resource.getErrors() + " warnings=" + resource.getWarnings();
    }

    private CodecResource load(String json) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://diagnostics.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource;
    }
}
