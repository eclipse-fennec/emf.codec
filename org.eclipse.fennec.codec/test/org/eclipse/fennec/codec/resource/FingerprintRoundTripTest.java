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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * End-to-end round-trip tests for the in-band EPackage fingerprint (issue #73, B.1).
 * <p>
 * These are the acceptance tests of Phase B's carrier: a document is saved, then loaded into
 * a <b>fresh</b> resource, and every object has to come back under its own model version -
 * driven solely by what the document carries, with no caller-side version option involved.
 * </p>
 * <p>
 * Every scenario has a negative twin that runs the identical model and data with the carrier
 * switched off. Those twins fail with an ambiguity error, which is what makes the positive
 * results evidence rather than coincidence (R6): the versions are genuinely indistinguishable
 * without the fingerprint.
 * </p>
 * <p>
 * Two nsURIs are used, each registered in two versions that differ only in an attribute's
 * JSON key. So four candidate versions are live at once and every single nsURI in the
 * document is ambiguous by itself.
 * </p>
 */
@DisplayName("Fingerprint round-trip: several packages and versions at once (#73 B.1)")
class FingerprintRoundTripTest {

    private static final String NS_A = "http://example.org/fproundtrip/a/1.0";
    private static final String NS_B = "http://example.org/fproundtrip/b/1.0";

    private MetadataWhiteboard metadataService;

    /** Two versions of nsURI A, differing in the JSON key of Node.title. */
    private EPackage packageA1;
    private EPackage packageA2;
    /** Two versions of nsURI B, differing in the JSON key of Leaf.label. */
    private EPackage packageB1;
    private EPackage packageB2;

    private String fingerprintA1;
    private String fingerprintA2;
    private String fingerprintB1;
    private String fingerprintB2;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();

        packageB1 = buildB("labelV1");
        packageB2 = buildB("labelV2");
        // A's Node contains a Leaf of a specific B version, so a second package - in a
        // specific version - necessarily enters the document.
        packageA1 = buildA("titleV1", (EClass) packageB1.getEClassifier("Leaf"));
        packageA2 = buildA("titleV2", (EClass) packageB2.getEClassifier("Leaf"));

        fingerprintB1 = metadataService.registerPackage(packageB1).getModelFingerprint();
        fingerprintB2 = metadataService.registerPackage(packageB2).getModelFingerprint();
        fingerprintA1 = metadataService.registerPackage(packageA1).getModelFingerprint();
        fingerprintA2 = metadataService.registerPackage(packageA2).getModelFingerprint();
    }

    private static EPackage buildA(String titleKey, EClass leafType) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("a");
        pkg.setNsPrefix("a");
        pkg.setNsURI(NS_A);

        EClass node = EcoreFactory.eINSTANCE.createEClass();
        node.setName("Node");
        pkg.getEClassifiers().add(node);

        EAttribute title = EcoreFactory.eINSTANCE.createEAttribute();
        title.setName(titleKey);
        title.setEType(EcorePackage.eINSTANCE.getEString());
        node.getEStructuralFeatures().add(title);

        EReference leaf = EcoreFactory.eINSTANCE.createEReference();
        leaf.setName("leaf");
        leaf.setEType(leafType);
        leaf.setContainment(true);
        node.getEStructuralFeatures().add(leaf);

        // Non-containment, so the target's version can only be known from the reference entry
        EReference peer = EcoreFactory.eINSTANCE.createEReference();
        peer.setName("peer");
        peer.setEType(leafType);
        peer.setContainment(false);
        node.getEStructuralFeatures().add(peer);

        return backWithResource(pkg);
    }

    private static EPackage buildB(String labelKey) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("b");
        pkg.setNsPrefix("b");
        pkg.setNsURI(NS_B);

        EClass leaf = EcoreFactory.eINSTANCE.createEClass();
        leaf.setName("Leaf");
        pkg.getEClassifiers().add(leaf);

        EAttribute label = EcoreFactory.eINSTANCE.createEAttribute();
        label.setName(labelKey);
        label.setEType(EcorePackage.eINSTANCE.getEString());
        leaf.getEStructuralFeatures().add(label);

        return backWithResource(pkg);
    }

    /**
     * Puts a dynamically built package into a Resource named after its nsURI.
     * <p>
     * Without this, {@code EcoreUtil.getURI} yields a bare fragment such as {@code #//Node}
     * instead of {@code nsURI#//Node}, and the written type value would carry no namespace at
     * all - which would make this test pass or fail for reasons having nothing to do with
     * fingerprints. Both versions of an nsURI get their own Resource instance, so they stay
     * distinct objects while sharing the canonical URI.
     * </p>
     */
    private static EPackage backWithResource(EPackage pkg) {
        new ResourceImpl(URI.createURI(pkg.getNsURI())).getContents().add(pkg);
        return pkg;
    }

    private EObject createNode(EPackage aPackage, String titleKey, String title,
            EPackage bPackage, String labelKey, String label) {
        EClass nodeClass = (EClass) aPackage.getEClassifier("Node");
        EObject node = aPackage.getEFactoryInstance().create(nodeClass);
        node.eSet(nodeClass.getEStructuralFeature(titleKey), title);

        if (bPackage != null) {
            EClass leafClass = (EClass) bPackage.getEClassifier("Leaf");
            EObject leaf = bPackage.getEFactoryInstance().create(leafClass);
            leaf.eSet(leafClass.getEStructuralFeature(labelKey), label);
            node.eSet(nodeClass.getEStructuralFeature("leaf"), leaf);
        }
        return node;
    }

    private CodecResource newResource(String name) {
        return new CodecResource(
                URI.createURI("test://fp-roundtrip-" + name + ".json"),
                metadataService, ConfigurationResolver.defaults(), null);
    }

    /** Saves the given roots and returns the produced document. */
    private String save(List<EObject> roots, Map<String, Object> options) throws IOException {
        CodecResource resource = newResource("save");
        resource.getContents().addAll(roots);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return out.toString(StandardCharsets.UTF_8);
    }

    /**
     * Loads a document into a brand-new resource. No root-type hint and no fingerprint
     * option: whatever version selection happens has to come out of the document itself.
     */
    private List<EObject> loadIntoFreshResource(String json, Map<String, Object> options)
            throws IOException {
        CodecResource resource = newResource("load");
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);
        return resource.getContents();
    }

    /** Flattens an exception chain's messages, since the cause carries the detail. */
    private static String collectMessages(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            builder.append(current.getMessage()).append(" | ");
            if (current.getCause() == current) {
                break;
            }
        }
        return builder.toString();
    }

    private static int countOccurrences(String haystack, String needle) {
        int count = 0;
        int index = haystack.indexOf(needle);
        while (index >= 0) {
            count++;
            index = haystack.indexOf(needle, index + needle.length());
        }
        return count;
    }

    // ========================================================================
    // Section 1: The fixture itself must be ambiguous, or nothing below proves anything
    // ========================================================================

    @Nested
    @DisplayName("1. Fixture is genuinely ambiguous")
    class FixtureIsAmbiguous {

        @Test
        @DisplayName("1.1 the four versions have four distinct fingerprints")
        void fingerprintsAreDistinct() {
            assertNotEquals(fingerprintA1, fingerprintA2, "the two A versions must differ");
            assertNotEquals(fingerprintB1, fingerprintB2, "the two B versions must differ");
            assertNotNull(fingerprintA1);
            assertNotNull(fingerprintB2);
        }

        @Test
        @DisplayName("1.2 without a fingerprint, both nsURIs are unresolvable - not merely wrong")
        void withoutCarrierTheLoadFails() throws IOException {
            EObject node = createNode(packageA2, "titleV2", "root", packageB2, "labelV2", "leaf");
            String json = save(List.of(node), Map.of());

            assertTrue(countOccurrences(json, "_fingerprint") == 0, json);

            // Mid-parse hard errors travel as unchecked exceptions: Jackson 3's parse path
            // declares none, so the codec throws IllegalStateException there.
            Exception ex = assertThrows(Exception.class,
                    () -> loadIntoFreshResource(json, Map.of()),
                    "with two registered versions per nsURI and nothing in the document to "
                    + "choose between them, the load must fail rather than guess");
            String message = collectMessages(ex);
            assertTrue(message.contains(NS_A) || message.contains(NS_B),
                    "the error should name the ambiguous nsURI: " + message);
        }
    }

    // ========================================================================
    // Section 2: Several packages at once, PLAIN
    // ========================================================================

    @Nested
    @DisplayName("2. Several packages at once (PLAIN)")
    class SeveralPackagesPlain {

        @Test
        @DisplayName("2.1 two packages round-trip into a fresh resource, each under its own version")
        void twoPackagesRoundTrip() throws IOException {
            EObject node = createNode(packageA2, "titleV2", "root", packageB2, "labelV2", "leaf");
            String json = save(List.of(node), Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            assertTrue(json.contains(fingerprintA2), "A's version must be announced: " + json);
            assertTrue(json.contains(fingerprintB2), "B's version must be announced: " + json);

            List<EObject> roots = loadIntoFreshResource(json, Map.of());

            assertEquals(1, roots.size());
            EObject loadedNode = roots.get(0);
            assertSame(packageA2.getEClassifier("Node"), loadedNode.eClass(),
                    "the root must be the exact EClass instance of version A2");
            assertEquals("root", loadedNode.eGet(loadedNode.eClass().getEStructuralFeature("titleV2")));

            EObject loadedLeaf = (EObject) loadedNode.eGet(
                    loadedNode.eClass().getEStructuralFeature("leaf"));
            assertNotNull(loadedLeaf, "the contained leaf must survive the round-trip");
            assertSame(packageB2.getEClassifier("Leaf"), loadedLeaf.eClass(),
                    "the contained object must be the exact EClass instance of version B2");
            assertEquals("leaf", loadedLeaf.eGet(loadedLeaf.eClass().getEStructuralFeature("labelV2")));
        }

        @Test
        @DisplayName("2.2 the other version combination round-trips just as well")
        void otherVersionCombinationRoundTrips() throws IOException {
            EObject node = createNode(packageA1, "titleV1", "root", packageB1, "labelV1", "leaf");
            String json = save(List.of(node), Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            List<EObject> roots = loadIntoFreshResource(json, Map.of());

            EObject loadedNode = roots.get(0);
            assertSame(packageA1.getEClassifier("Node"), loadedNode.eClass(),
                    "version A1 must come back as A1, not as the last-registered version");
            EObject loadedLeaf = (EObject) loadedNode.eGet(
                    loadedNode.eClass().getEStructuralFeature("leaf"));
            assertSame(packageB1.getEClassifier("Leaf"), loadedLeaf.eClass());
            assertEquals("leaf", loadedLeaf.eGet(loadedLeaf.eClass().getEStructuralFeature("labelV1")));
        }
    }

    // ========================================================================
    // Section 3: Several versions of the same nsURI in one document
    // ========================================================================

    @Nested
    @DisplayName("3. Mixed versions of one nsURI in one document")
    class MixedVersionsOfOneNsUri {

        @Test
        @DisplayName("3.1 two roots of different versions of the same nsURI each keep their own")
        void mixedVersionRootsRoundTrip() throws IOException {
            EObject nodeV1 = createNode(packageA1, "titleV1", "first", packageB1, "labelV1", "leafOne");
            EObject nodeV2 = createNode(packageA2, "titleV2", "second", packageB2, "labelV2", "leafTwo");

            String json = save(List.of(nodeV1, nodeV2),
                    Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            // First touch for A1 and B1, then the deviation markers for A2 and B2.
            assertEquals(4, countOccurrences(json, "_fingerprint"),
                    "each version deviating from an established pin needs its own marker: " + json);

            List<EObject> roots = loadIntoFreshResource(json, Map.of());

            assertEquals(2, roots.size(), "both roots must come back: " + json);
            assertSame(packageA1.getEClassifier("Node"), roots.get(0).eClass(),
                    "the first root establishes A1");
            assertSame(packageA2.getEClassifier("Node"), roots.get(1).eClass(),
                    "the second root deviates from the pin and must still land on A2");

            EObject leafOne = (EObject) roots.get(0).eGet(
                    roots.get(0).eClass().getEStructuralFeature("leaf"));
            EObject leafTwo = (EObject) roots.get(1).eGet(
                    roots.get(1).eClass().getEStructuralFeature("leaf"));
            assertSame(packageB1.getEClassifier("Leaf"), leafOne.eClass());
            assertSame(packageB2.getEClassifier("Leaf"), leafTwo.eClass());
            assertEquals("leafOne", leafOne.eGet(leafOne.eClass().getEStructuralFeature("labelV1")));
            assertEquals("leafTwo", leafTwo.eGet(leafTwo.eClass().getEStructuralFeature("labelV2")));
        }

        @Test
        @DisplayName("3.2 returning to the pinned version after a deviation still lands on the pin")
        void returnToPinnedVersionAfterDeviation() throws IOException {
            // A1, A2, A1: the writer announces A1 (first touch) and A2 (deviation), but writes
            // nothing for the third root, because it matches the established pin again. The
            // reader can only get that one right if its pin still says A1 - so this is the case
            // that forces reader pinning to keep the FIRST version, exactly as the writer does.
            EObject first = createNode(packageA1, "titleV1", "first", null, null, null);
            EObject second = createNode(packageA2, "titleV2", "second", null, null, null);
            EObject third = createNode(packageA1, "titleV1", "third", null, null, null);

            String json = save(List.of(first, second, third),
                    Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            assertEquals(2, countOccurrences(json, "_fingerprint"),
                    "the third root matches the pin again and needs no marker: " + json);

            List<EObject> roots = loadIntoFreshResource(json, Map.of());

            assertEquals(3, roots.size(), json);
            assertSame(packageA1.getEClassifier("Node"), roots.get(0).eClass());
            assertSame(packageA2.getEClassifier("Node"), roots.get(1).eClass());
            assertSame(packageA1.getEClassifier("Node"), roots.get(2).eClass(),
                    "an explicitly fingerprinted object must not move the pin: the unmarked "
                    + "third root belongs to the pinned version A1");
            assertEquals("third", roots.get(2).eGet(
                    roots.get(2).eClass().getEStructuralFeature("titleV1")));
        }
    }

    // ========================================================================
    // Section 5: Smart compression, which writes bare names instead of full URIs
    // ========================================================================

    @Nested
    @DisplayName("5. Smart compression (spec 8.3 interaction)")
    class SmartCompression {

        /**
         * Smart compression decides "same schema" by comparing nsURIs, and two versions of one
         * nsURI compare equal. So a deviating-version object may be written as a bare name whose
         * namespace resolves through the pin - the wrong version - unless the fingerprint that
         * accompanies it is applied to the composed URI.
         */

        @Test
        @DisplayName("5.1 every root keeps its own version, also under smart compression (#76)")
        void bareNameWithFingerprintResolvesCorrectly() throws IOException {
            CodecResource resource = new CodecResource(
                    URI.createURI("test://fp-smart-compression.json"),
                    metadataService,
                    ConfigurationResolver.builder()
                            .moduleProperties(Map.of("smartCompression", true))
                            .build(),
                    null);
            resource.getContents().add(createNode(packageA1, "titleV1", "first", null, null, null));
            resource.getContents().add(createNode(packageA2, "titleV2", "second", null, null, null));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));
            String json = out.toString(StandardCharsets.UTF_8);

            CodecResource loaded = new CodecResource(
                    URI.createURI("test://fp-smart-compression-load.json"),
                    metadataService,
                    ConfigurationResolver.builder()
                            .moduleProperties(Map.of("smartCompression", true))
                            .build(),
                    null);
            loaded.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            assertEquals(2, loaded.getContents().size(), json);
            assertSame(packageA1.getEClassifier("Node"), loaded.getContents().get(0).eClass(), json);
            assertSame(packageA2.getEClassifier("Node"), loaded.getContents().get(1).eClass(),
                    "since #76 every root establishes its own context and writes a full URI, so "
                    + "the deviating version resolves from nsURI plus its own fingerprint rather "
                    + "than from the pin: " + json);
        }
    }

    // ========================================================================
    // Section 4: STRUCTURED format and custom keys
    // ========================================================================

    @Nested
    @DisplayName("4. STRUCTURED format and custom keys")
    class StructuredAndCustomKeys {

        @Test
        @DisplayName("4.1 STRUCTURED round-trips with the fingerprint inside the type object")
        void structuredRoundTrip() throws IOException {
            EObject node = createNode(packageA2, "titleV2", "root", packageB2, "labelV2", "leaf");
            String json = save(List.of(node), Map.of(
                    CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH",
                    CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED"));

            assertTrue(json.contains("\"fingerprint\":\"" + fingerprintA2 + "\""), json);

            List<EObject> roots = loadIntoFreshResource(json,
                    Map.of(CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED"));

            EObject loadedNode = roots.get(0);
            assertSame(packageA2.getEClassifier("Node"), loadedNode.eClass());
            EObject loadedLeaf = (EObject) loadedNode.eGet(
                    loadedNode.eClass().getEStructuralFeature("leaf"));
            assertSame(packageB2.getEClassifier("Leaf"), loadedLeaf.eClass());
        }

        @Test
        @DisplayName("4.2 a custom key round-trips when the reader is told about it (spec 8.5)")
        void customKeyRoundTripsWithCallerSideKey() throws IOException {
            EObject node = createNode(packageA2, "titleV2", "root", packageB2, "labelV2", "leaf");
            String json = save(List.of(node), Map.of(
                    CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH",
                    CodecOptions.CODEC_FINGERPRINT_KEY, "modelVersion"));

            assertTrue(json.contains("\"_modelVersion\":\"" + fingerprintA2 + "\""), json);

            List<EObject> roots = loadIntoFreshResource(json,
                    Map.of(CodecOptions.CODEC_FINGERPRINT_KEY, "modelVersion"));

            assertSame(packageA2.getEClassifier("Node"), roots.get(0).eClass());
        }

        @Test
        @DisplayName("4.4 a STRUCTURED non-containment reference carries the fingerprint in its type object")
        void structuredReferenceCarriesFingerprint() throws IOException {
            // A cross-reference from A2's Node to a B2 Leaf held as a second root: the reference
            // is non-containment, so the target's version is only knowable from the reference
            // entry itself (B.3).
            EClass nodeClass = (EClass) packageA2.getEClassifier("Node");
            EClass leafClass = (EClass) packageB2.getEClassifier("Leaf");

            EObject leaf = packageB2.getEFactoryInstance().create(leafClass);
            leaf.eSet(leafClass.getEStructuralFeature("labelV2"), "target");
            EObject node = packageA2.getEFactoryInstance().create(nodeClass);
            node.eSet(nodeClass.getEStructuralFeature("titleV2"), "source");
            node.eSet(nodeClass.getEStructuralFeature("peer"), leaf);

            String json = save(List.of(node, leaf),
                    Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            // The fingerprint sits inside the reference's own type object, as the unprefixed
            // inner key next to $ref - not as a sibling of the enclosing object's _type.
            assertTrue(json.contains("\"fingerprint\":\"" + fingerprintB2 + "\""),
                    "the reference entry must carry the target's version: " + json);
            // Object writer and reference writer share the per-save pins, so the second root -
            // the very same Leaf, serialized after the reference announced its package - stays
            // unmarked. Announcing it twice would be redundant.
            assertEquals(1, countOccurrences(json, fingerprintB2),
                    "the shared pin must keep the announcement to one site: " + json);

            List<EObject> roots = loadIntoFreshResource(json, Map.of());

            assertEquals(2, roots.size(), json);
            assertSame(packageA2.getEClassifier("Node"), roots.get(0).eClass());
            assertSame(packageB2.getEClassifier("Leaf"), roots.get(1).eClass(),
                    "the referenced object must come back under its own version");
        }

        @Test
        @DisplayName("4.3 a reader configured for another key still accepts the default key")
        void defaultKeyIsAlwaysAccepted() throws IOException {
            EObject node = createNode(packageA2, "titleV2", "root", packageB2, "labelV2", "leaf");
            String json = save(List.of(node), Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            List<EObject> roots = loadIntoFreshResource(json,
                    Map.of(CodecOptions.CODEC_FINGERPRINT_KEY, "somethingElse"));

            assertSame(packageA2.getEClassifier("Node"), roots.get(0).eClass(),
                    "the default key is accepted in addition to a configured one (spec 8.5)");
        }
    }
}
