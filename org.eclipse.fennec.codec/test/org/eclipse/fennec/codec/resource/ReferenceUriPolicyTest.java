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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * The two options that decide what a reference URI from a document may cause.
 * <p>
 * Defaults are covered by {@link ReferenceResolutionNoIoTest}; this is about the deliberate
 * settings on top: opting into loading, and narrowing which schemes a document may name at all.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/10-reference.md#93-cross-resource-references">Spec 10 §9.3</a>
 */
@DisplayName("Reference URI policy")
class ReferenceUriPolicyTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/ser/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;
    private EReference managerRef;

    private HttpServer server;
    private final List<String> requests = new CopyOnWriteArrayList<>();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, ReferenceUriPolicyTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        managerRef = (EReference) EcoreHelper.getFeature(personClass, "manager");

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requests.add(exchange.getRequestMethod() + " " + exchange.getRequestURI());
            byte[] body = "{}".getBytes(UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("an external reference URI is reported as a warning even though nothing is opened")
    void externalUriIsReported() throws IOException {
        Resource resource = load(httpRef(), Map.of());

        assertTrue(requests.isEmpty(), "nothing may be opened by default");
        assertTrue(resource.getWarnings().stream()
                        .anyMatch(w -> w.getMessage().contains(httpRef())),
                "the URI must be visible as a warning, warnings were: " + resource.getWarnings());
    }

    @Test
    @DisplayName("a relative reference URI is not reported")
    void relativeUriIsNotReported() throws IOException {
        Resource resource = load("other.json#/0", Map.of());

        assertTrue(resource.getWarnings().stream()
                        .noneMatch(w -> w.getMessage().contains("outside this process")),
                "a relative URI names no location, warnings were: " + resource.getWarnings());
    }

    @Test
    @DisplayName("loadReferencedResources opts into loading the named resource")
    void loadingCanBeOptedInto() throws IOException {
        load(httpRef(), Map.of(CodecOptions.CODEC_LOAD_REFERENCED_RESOURCES, true));

        assertFalse(requests.isEmpty(), "the opt-in must actually load the named resource");
    }

    @Test
    @DisplayName("a scheme outside the allowlist is refused: no proxy, an error")
    void schemeOutsideAllowlistIsRefused() throws IOException {
        Resource resource = load(httpRef(), Map.of(CodecOptions.CODEC_REF_URI_SCHEMES, List.of("file")));

        EObject person = resource.getContents().get(0);
        assertNull(person.eGet(managerRef, false), "a refused reference must not become a proxy either");
        assertTrue(resource.getErrors().stream()
                        .anyMatch(e -> e.getMessage().contains("not among the allowed reference URI schemes")),
                "the refusal must be reported as an error, errors were: " + resource.getErrors());
    }

    @Test
    @DisplayName("the allowlist also gates the opt-in loading")
    void allowlistGatesLoading() throws IOException {
        load(httpRef(), Map.of(
                CodecOptions.CODEC_LOAD_REFERENCED_RESOURCES, true,
                CodecOptions.CODEC_REF_URI_SCHEMES, List.of("file")));

        assertTrue(requests.isEmpty(), "a refused scheme must not be loaded even with loading enabled");
    }

    @Test
    @DisplayName("a comma-separated scheme list is accepted, as OSGi configuration delivers it")
    void schemeListAcceptsCommaSeparatedString() throws IOException {
        Resource resource = load(httpRef(), Map.of(CodecOptions.CODEC_REF_URI_SCHEMES, "file, http"));

        EObject person = resource.getContents().get(0);
        assertTrue(resource.getErrors().isEmpty(), "http is listed, errors were: " + resource.getErrors());
        assertTrue(((EObject) person.eGet(managerRef, false)).eIsProxy(),
                "an allowed scheme still resolves to a proxy without loading");
        assertTrue(requests.isEmpty(), "allowing a scheme is not the same as loading it");
    }

    private String httpRef() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/probe.json#/0";
    }

    private Resource load(String refUri, Map<String, Object> codecOptions) throws IOException {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("json",
                new CodecResourceFactory(metadataService, ConfigurationResolver.defaults()));
        Resource resource = resourceSet.createResource(URI.createURI("temp/id.json"));

        Map<Object, Object> options = new HashMap<>(codecOptions);
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);

        String json = "{ \"name\": \"victim\", \"manager\": { \"$ref\": \"" + refUri + "\" } }";
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), options);
        return resource;
    }
}
