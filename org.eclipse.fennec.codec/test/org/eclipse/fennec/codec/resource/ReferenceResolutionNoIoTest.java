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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpServer;

/**
 * Reference resolution stays inside the loaded model — a URI in the payload never makes the
 * codec open a location.
 * <p>
 * A reference URI is data, not an instruction: the document is untrusted input, so reading it
 * must not turn into an outbound request or a file read at an address the document chose. What
 * the codec resolves is what is already in memory; everything else becomes a proxy the embedder
 * resolves deliberately. The last test pins that in-memory half, so the guard cannot be
 * satisfied by simply resolving nothing at all.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/10-reference.md#93-cross-resource-references">Spec 10 §9.3</a>
 */
@DisplayName("Reference resolution performs no I/O")
class ReferenceResolutionNoIoTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/ser/test-roundtrip.ecore";

    @TempDir
    Path tempDir;

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;
    private EAttribute nameAttribute;
    private EReference managerRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, ReferenceResolutionNoIoTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        managerRef = (EReference) EcoreHelper.getFeature(personClass, "manager");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("an http reference URI issues no request while loading")
    void httpReferenceIssuesNoRequest() throws Exception {
        List<String> requests = new CopyOnWriteArrayList<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requests.add(exchange.getRequestMethod() + " " + exchange.getRequestURI());
            byte[] body = "{}".getBytes(UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        int port = server.getAddress().getPort();

        try {
            ResourceSet resourceSet = newResourceSet();
            Resource resource = resourceSet.createResource(URI.createURI("temp/id.json"));
            String refUri = "http://127.0.0.1:" + port + "/probe.json#//0";

            load(resource, "{ \"name\": \"victim\", \"manager\": { \"_ref\": \"" + refUri + "\" } }");

            assertTrue(requests.isEmpty(),
                    "loading must not issue a request for a reference URI, but did: " + requests);
            assertProxyTo(resource, refUri);
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("a file reference URI opens no file while loading")
    void fileReferenceOpensNoFile() throws Exception {
        Path target = tempDir.resolve("target.json");
        Files.writeString(target, "{\"name\":\"target\"}");

        ResourceSet resourceSet = newResourceSet();
        Resource resource = resourceSet.createResource(URI.createURI("temp/id.json"));
        String refUri = URI.createFileURI(target.toAbsolutePath().toString()) + "#//0";

        load(resource, "{ \"name\": \"victim\", \"manager\": { \"_ref\": \"" + refUri + "\" } }");

        assertTrue(resourceSet.getResources().stream()
                        .noneMatch(r -> r.getURI().toString().endsWith("target.json")),
                "loading must not open the file a reference URI names, resources were: "
                        + resourceSet.getResources().stream().map(r -> r.getURI().toString()).toList());
        assertProxyTo(resource, refUri);
    }

    @Test
    @DisplayName("a reference into an already loaded resource still resolves")
    void referenceIntoLoadedResourceResolves() throws Exception {
        ResourceSet resourceSet = newResourceSet();

        URI managerUri = URI.createFileURI(tempDir.resolve("manager.json").toAbsolutePath().toString());
        Resource managerResource = resourceSet.createResource(managerUri);
        EObject boss = testPackage.getEFactoryInstance().create(personClass);
        boss.eSet(nameAttribute, "Boss");
        managerResource.getContents().add(boss);

        Resource resource = resourceSet.createResource(
                URI.createFileURI(tempDir.resolve("person.json").toAbsolutePath().toString()));
        String refUri = managerUri + "#" + managerResource.getURIFragment(boss);
        load(resource, "{ \"name\": \"Alice\", \"manager\": { \"_ref\": \"" + refUri + "\" } }");

        EObject manager = (EObject) resource.getContents().get(0).eGet(managerRef, false);
        assertNotNull(manager, "manager reference must be set");
        assertFalse(manager.eIsProxy(), "a target that is already in memory must resolve");
        assertEquals(boss, manager, "the reference must point at the object in the loaded resource");
    }

    private void load(Resource resource, String json) throws IOException {
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)),
                Map.of(CodecResource.CODEC_ROOT_TYPE, personClass));
    }

    private void assertProxyTo(Resource resource, String refUri) {
        EObject manager = (EObject) resource.getContents().get(0).eGet(managerRef, false);
        assertNotNull(manager, "the unresolved reference must still be represented");
        assertTrue(manager.eIsProxy(), "an unresolvable target must come back as a proxy");
        assertEquals(URI.createURI(refUri), ((InternalEObject) manager).eProxyURI(),
                "the proxy must carry the URI from the document");
    }

    private ResourceSet newResourceSet() {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("json",
                new CodecResourceFactory(metadataService, ConfigurationResolver.defaults()));
        return resourceSet;
    }
}
