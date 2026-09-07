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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
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
 * Every type-resolution path goes through the per-load {@code PackageResolver}; none of them
 * reads {@link EPackage.Registry#INSTANCE} behind its back (issue #207).
 * <p>
 * The whiteboard is how a model is published in Fennec, and nothing on that path touches the
 * JVM-global registry. A resolution path that consults the global registry instead therefore
 * fails outright in an OSGi runtime that never mirrors its packages there — and, worse, when
 * something else <i>has</i> put a different instance of the same nsURI in the global registry,
 * it answers with that foreign version instead of the one the load selected.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-type.md#63-type-resolution">Spec 06 §6.3</a>
 */
@DisplayName("Type resolution never bypasses the per-load PackageResolver (#207)")
class ResolverBypassTest {

    /**
     * NUMERIC transports a classifier id, so the package has to be located by the schema hint
     * before the id means anything at all. That lookup is the resolver's job.
     */
    @Nested
    @DisplayName("NUMERIC strategy in a whiteboard-only runtime")
    class NumericStrategy {

        private static final String TEST_ECORE = "/org/eclipse/fennec/codec/type/test-hint.ecore";

        private EcoreHelper ecoreHelper;
        private EPackage testPackage;
        private MetadataWhiteboard metadataService;
        private EClass carClass;

        @BeforeEach
        void setUp() throws IOException {
            ecoreHelper = new EcoreHelper();
            testPackage = ecoreHelper.loadEcore(TEST_ECORE, ResolverBypassTest.class);
            // Deliberately NOT in EPackage.Registry.INSTANCE: the whiteboard publishes it.
            metadataService = MetadataServiceFactory.create();
            metadataService.registerPackage(testPackage);
            carClass = EcoreHelper.getEClass(testPackage, "Car");
        }

        @AfterEach
        void tearDown() {
            ecoreHelper.releaseAll();
        }

        @Test
        @DisplayName("the global registry really does not know this package")
        void globalRegistryStaysOutOfIt() {
            assertNull(EPackage.Registry.INSTANCE.getEPackage(testPackage.getNsURI()),
                    "test precondition: the package must be published through the service only");
        }

        @Test
        @DisplayName("PLAIN classifier id resolves through the service")
        void plainClassifierId() throws IOException {
            String json = "{ \"_type\": \"" + carClass.getClassifierID() + "\","
                    + " \"brand\": \"VW\", \"doors\": 5 }";

            CodecResource resource = load(metadataService, json, Map.of(
                    CodecOptions.CODEC_TYPE_STRATEGY, "NUMERIC",
                    CodecResource.CODEC_ROOT_SCHEMA, testPackage.getNsURI()));

            assertEquals(1, resource.getContents().size(),
                    "errors were: " + resource.getErrors());
            assertSame(carClass, resource.getContents().get(0).eClass(),
                    "the schema hint names a package the MetadataService knows");
        }

        @Test
        @DisplayName("STRUCTURED schema + classifier resolves through the service")
        void structuredSchemaAndClassifier() throws IOException {
            String json = "{ \"_type\": { \"schema\": \"" + testPackage.getNsURI() + "\","
                    + " \"classifier\": " + carClass.getClassifierID() + " },"
                    + " \"brand\": \"VW\", \"doors\": 5 }";

            CodecResource resource = load(metadataService, json, Map.of(
                    CodecOptions.CODEC_TYPE_STRATEGY, "NUMERIC",
                    CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED"));

            assertEquals(1, resource.getContents().size(),
                    "errors were: " + resource.getErrors());
            assertSame(carClass, resource.getContents().get(0).eClass(),
                    "the schema travels in the document; no global registry is involved");
        }
    }

    /**
     * A reference entry's type URI is resolved by the resolver, which already applies the whole
     * binding order — global registry included, as its own last tier for nsURIs the
     * MetadataService does not know. A second, direct global lookup after that answer can only
     * contradict it, and here it contradicts it with a class from a different version of the
     * very nsURI the load has already selected a version for.
     */
    @Nested
    @DisplayName("reference type URIs")
    class ReferenceTypeUris {

        private static final String NS_URI = "http://test.example.org/refversion/1.0";

        private MetadataWhiteboard metadataService;
        private EPackage selectedVersion;
        private EPackage staleVersion;
        private EClass holderClass;
        private EClass nodeClass;

        @BeforeEach
        void setUp() {
            selectedVersion = buildSelectedVersion();
            holderClass = (EClass) selectedVersion.getEClassifier("Holder");
            nodeClass = (EClass) selectedVersion.getEClassifier("Node");

            metadataService = MetadataServiceFactory.create();
            metadataService.registerPackage(selectedVersion);

            // A different version of the same nsURI, reachable only through the global
            // registry — the situation an OSGi runtime that mirrors packages there produces.
            staleVersion = EcoreFactory.eINSTANCE.createEPackage();
            staleVersion.setName("refversion");
            staleVersion.setNsPrefix("refversion");
            staleVersion.setNsURI(NS_URI);
            EClass retiredNode = EcoreFactory.eINSTANCE.createEClass();
            retiredNode.setName("RetiredNode");
            // A subtype of the selected version's Node, so nothing but the version check
            // stands between this class and the reference it must not end up on.
            retiredNode.getESuperTypes().add(nodeClass);
            staleVersion.getEClassifiers().add(retiredNode);
            EPackage.Registry.INSTANCE.put(NS_URI, staleVersion);
        }

        @AfterEach
        void tearDown() {
            EPackage.Registry.INSTANCE.remove(NS_URI);
        }

        private EPackage buildSelectedVersion() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("refversion");
            pkg.setNsPrefix("refversion");
            pkg.setNsURI(NS_URI);

            EClass node = EcoreFactory.eINSTANCE.createEClass();
            node.setName("Node");
            pkg.getEClassifiers().add(node);

            EClass holder = EcoreFactory.eINSTANCE.createEClass();
            holder.setName("Holder");
            EReference target = EcoreFactory.eINSTANCE.createEReference();
            target.setName("target");
            target.setEType(node);
            target.setContainment(false);
            holder.getEStructuralFeatures().add(target);
            pkg.getEClassifiers().add(holder);

            return pkg;
        }

        @Test
        @DisplayName("test precondition: the two versions are different packages")
        void twoVersionsOfOneNsUri() {
            assertSame(staleVersion, EPackage.Registry.INSTANCE.getEPackage(NS_URI));
            assertNull(selectedVersion.getEClassifier("RetiredNode"),
                    "the selected version must not know the class the document names");
        }

        @Test
        @DisplayName("a class only another version knows is not smuggled onto the proxy")
        void staleGlobalVersionDoesNotWin() throws IOException {
            String json = "{ \"_type\": \"" + NS_URI + "#//Holder\","
                    + " \"target\": { \"$ref\": \"file:/other-resource.json#/1\","
                    + " \"_type\": \"" + NS_URI + "#//RetiredNode\" } }";

            CodecResource resource = load(metadataService, json, Map.of());

            assertEquals(1, resource.getContents().size(),
                    "errors were: " + resource.getErrors());
            EObject holder = resource.getContents().get(0);
            assertSame(holderClass, holder.eClass());
            EObject proxy = (EObject) holder.eGet(holderClass.getEStructuralFeature("target"));
            assertNotNull(proxy, "the reference must still produce a proxy");
            assertSame(nodeClass, proxy.eClass(),
                    "the type is unresolvable in the selected version, so the declared "
                    + "reference type stands; RetiredNode belongs to another version");
        }
    }

    private static CodecResource load(MetadataWhiteboard metadataService, String json,
            Map<String, Object> options) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("resolver-bypass.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), options);
        return resource;
    }
}
