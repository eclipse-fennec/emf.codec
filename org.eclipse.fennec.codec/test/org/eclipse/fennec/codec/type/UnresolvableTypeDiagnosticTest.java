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
package org.eclipse.fennec.codec.type;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A document that names a type nobody knows is a different failure from a document that names
 * no type at all (issue #160).
 * <p>
 * Both end the same way — nothing is deserialized — but they ask the reader to look in
 * different places: the first at the registry and the package version, the second at the
 * writer. One message for both described the second case only, so an unresolvable
 * discriminator was reported as a missing one.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-type.md#632-unknown-type-handling">Spec 06 §6.3.2</a>
 */
@DisplayName("Unresolvable type value is reported as such")
class UnresolvableTypeDiagnosticTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/type/test-hint.ecore";
    private static final String GHOST_TYPE = "http://ghost.example.org/1.0#//Ghost";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, UnresolvableTypeDiagnosticTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("an unresolvable type value is named in the error")
    void unresolvableTypeValueIsNamed() throws IOException {
        Resource resource = load("{ \"_type\": \"" + GHOST_TYPE + "\", \"brand\": \"VW\" }");

        assertTrue(messages(resource.getErrors()).stream()
                        .anyMatch(m -> m.contains(GHOST_TYPE) && m.contains("could not be resolved")),
                "the error must name the value that failed, errors were: " + resource.getErrors());
        assertTrue(messages(resource.getErrors()).stream()
                        .noneMatch(m -> m.contains("no type information found")),
                "the document did carry type information, errors were: " + resource.getErrors());
    }

    @Test
    @DisplayName("a document without any type keeps the message it always had")
    void missingTypeKeepsItsMessage() throws IOException {
        Resource resource = load("{ \"brand\": \"VW\" }");

        assertTrue(messages(resource.getErrors()).stream()
                        .anyMatch(m -> m.contains("no type information found and no CODEC_ROOT_TYPE hint")),
                "spec 06 §6.3.2 names this message verbatim, errors were: " + resource.getErrors());
    }

    private List<String> messages(List<Resource.Diagnostic> diagnostics) {
        return diagnostics.stream().map(Resource.Diagnostic::getMessage).toList();
    }

    private Resource load(String json) throws IOException {
        Resource resource = new CodecResource(URI.createURI("test/unresolvable-type.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        // No CODEC_ROOT_TYPE: the hint is what would otherwise mask the failure
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), Map.of());
        return resource;
    }
}
