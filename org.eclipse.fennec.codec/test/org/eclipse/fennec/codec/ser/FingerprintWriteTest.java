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
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Write-side tests for the in-band EPackage fingerprint carrier (issue #73, B.1).
 * <p>
 * Derived from {@code docs/codec-v2-spec/06-type.md} section 8:
 * </p>
 * <ul>
 *   <li>§8.1 — off by default, so existing output is unchanged (R1);</li>
 *   <li>§8.2 — PLAIN sibling vs STRUCTURED inner key, configurable key;</li>
 *   <li>§8.3 — first touch only: root, new package entering the document, deviation from
 *       an established pin.</li>
 * </ul>
 */
@DisplayName("Fingerprint write path (#73 B.1)")
class FingerprintWriteTest {

    private static final String NS_MAIN = "http://example.org/fpwrite/main/1.0";
    private static final String NS_OTHER = "http://example.org/fpwrite/other/1.0";

    private MetadataWhiteboard metadataService;
    private EPackage mainPackage;
    private EPackage otherPackage;
    private String mainFingerprint;
    private String otherFingerprint;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();
        mainPackage = buildMainPackage();
        otherPackage = buildOtherPackage();
        mainFingerprint = metadataService.registerPackage(mainPackage).getModelFingerprint();
        otherFingerprint = metadataService.registerPackage(otherPackage).getModelFingerprint();
    }

    /**
     * A package whose Node has a name, a containment to another Node of the same package
     * (to prove first-touch is not per-object) and a containment to a foreign package
     * (to prove a new package entering the document is announced).
     */
    private static EPackage buildMainPackage() {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("main");
        pkg.setNsPrefix("main");
        pkg.setNsURI(NS_MAIN);

        EClass node = EcoreFactory.eINSTANCE.createEClass();
        node.setName("Node");
        pkg.getEClassifiers().add(node);

        EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
        name.setName("name");
        name.setEType(EcorePackage.eINSTANCE.getEString());
        node.getEStructuralFeatures().add(name);

        EReference child = EcoreFactory.eINSTANCE.createEReference();
        child.setName("child");
        child.setEType(node);
        child.setContainment(true);
        node.getEStructuralFeatures().add(child);

        return pkg;
    }

    private static EPackage buildOtherPackage() {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("other");
        pkg.setNsPrefix("other");
        pkg.setNsURI(NS_OTHER);

        EClass leaf = EcoreFactory.eINSTANCE.createEClass();
        leaf.setName("Leaf");
        pkg.getEClassifiers().add(leaf);

        EAttribute label = EcoreFactory.eINSTANCE.createEAttribute();
        label.setName("label");
        label.setEType(EcorePackage.eINSTANCE.getEString());
        leaf.getEStructuralFeatures().add(label);

        return pkg;
    }

    /** Links main.Node to other.Leaf so a second package enters the document. */
    private void addForeignContainment() {
        EClass node = (EClass) mainPackage.getEClassifier("Node");
        EReference leafRef = EcoreFactory.eINSTANCE.createEReference();
        leafRef.setName("leaf");
        leafRef.setEType(otherPackage.getEClassifier("Leaf"));
        leafRef.setContainment(true);
        node.getEStructuralFeatures().add(leafRef);
        // Re-register: the model changed, so its fingerprint changed with it.
        mainFingerprint = metadataService.registerPackage(mainPackage).getModelFingerprint();
    }

    private EObject createNode(String name) {
        EClass node = (EClass) mainPackage.getEClassifier("Node");
        EObject object = mainPackage.getEFactoryInstance().create(node);
        object.eSet(node.getEStructuralFeature("name"), name);
        return object;
    }

    private EObject createLeaf(String label) {
        EClass leaf = (EClass) otherPackage.getEClassifier("Leaf");
        EObject object = otherPackage.getEFactoryInstance().create(leaf);
        object.eSet(leaf.getEStructuralFeature("label"), label);
        return object;
    }

    private String serialize(EObject root, Map<String, Object> options) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://fingerprint-write.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        resource.getContents().add(root);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return out.toString(StandardCharsets.UTF_8);
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
    // Section 1: Off by default (R1)
    // ========================================================================

    @Nested
    @DisplayName("1. Off by default (spec 8.1)")
    class OffByDefault {

        @Test
        @DisplayName("1.1 no options: output carries no fingerprint at all")
        void noFingerprintWithoutOptIn() throws IOException {
            String json = serialize(createNode("root"), Map.of());

            assertFalse(json.contains("_fingerprint"),
                    "the default must leave existing output untouched (R1): " + json);
            assertFalse(json.contains(mainFingerprint), "no fingerprint value may leak: " + json);
        }

        @Test
        @DisplayName("1.2 explicit NONE behaves exactly like the default")
        void explicitNoneWritesNothing() throws IOException {
            String json = serialize(createNode("root"),
                    Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "NONE"));

            assertFalse(json.contains("_fingerprint"), json);
        }
    }

    // ========================================================================
    // Section 2: Carrier placement and key
    // ========================================================================

    @Nested
    @DisplayName("2. Carrier placement (spec 8.2)")
    class Placement {

        @Test
        @DisplayName("2.1 PLAIN: the fingerprint is a sibling of the type key, right after it")
        void plainSibling() throws IOException {
            String json = serialize(createNode("root"),
                    Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            assertTrue(json.contains("\"_fingerprint\":\"" + mainFingerprint + "\""),
                    "expected the PLAIN sibling key with the package fingerprint: " + json);
            assertTrue(json.indexOf("\"_type\"") < json.indexOf("\"_fingerprint\""),
                    "the fingerprint slot follows the type (spec 5.0.2): " + json);
        }

        @Test
        @DisplayName("2.2 STRUCTURED: the fingerprint is an inner key of the type object")
        void structuredInnerKey() throws IOException {
            String json = serialize(createNode("root"), Map.of(
                    CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH",
                    CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED"));

            assertTrue(json.contains("\"fingerprint\":\"" + mainFingerprint + "\""),
                    "expected the unprefixed inner key inside the type object: " + json);
            assertFalse(json.contains("\"_fingerprint\""),
                    "STRUCTURED must not use the PLAIN sibling form: " + json);
        }

        @Test
        @DisplayName("2.3 a configured key is used, in its PLAIN-derived form")
        void configuredKey() throws IOException {
            String json = serialize(createNode("root"), Map.of(
                    CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH",
                    CodecOptions.CODEC_FINGERPRINT_KEY, "modelVersion"));

            assertTrue(json.contains("\"_modelVersion\":\"" + mainFingerprint + "\""), json);
            assertFalse(json.contains("\"_fingerprint\""), json);
        }

        @Test
        @DisplayName("2.4 an already prefixed configured key is not prefixed twice")
        void alreadyPrefixedKey() throws IOException {
            String json = serialize(createNode("root"), Map.of(
                    CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH",
                    CodecOptions.CODEC_FINGERPRINT_KEY, "@fp"));

            assertTrue(json.contains("\"@fp\":\"" + mainFingerprint + "\""), json);
            assertFalse(json.contains("\"_@fp\""), json);
        }
    }

    // ========================================================================
    // Section 3: First touch only
    // ========================================================================

    @Nested
    @DisplayName("3. First touch only (spec 8.3)")
    class FirstTouch {

        @Test
        @DisplayName("3.1 nested objects of the same package are not repeated")
        void sameePackageWrittenOnce() throws IOException {
            EObject root = createNode("root");
            EObject child = createNode("child");
            EClass node = (EClass) mainPackage.getEClassifier("Node");
            root.eSet(node.getEStructuralFeature("child"), child);

            String json = serialize(root, Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            assertTrue(json.contains("\"child\""), "the child must be serialized at all: " + json);
            assertEquals(1, countOccurrences(json, "\"_fingerprint\""),
                    "once the version is established, repeating it is pure redundancy: " + json);
        }

        @Test
        @DisplayName("3.3 typeStrategy=NONE writes no fingerprint - there is no type context to hold it")
        void noTypeContextMeansNoFingerprint() throws IOException {
            String json = serialize(createNode("root"), Map.of(
                    CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH",
                    CodecOptions.CODEC_TYPE_STRATEGY, "NONE"));

            assertFalse(json.contains("_fingerprint"),
                    "the carrier is a citizen of the type context: a caller who suppressed type "
                    + "information entirely gets no fingerprint either: " + json);
        }

        @Test
        @DisplayName("3.2 a second package entering the document is announced too")
        void newPackageIsAnnounced() throws IOException {
            addForeignContainment();

            EObject root = createNode("root");
            EObject leaf = createLeaf("leaf");
            EClass node = (EClass) mainPackage.getEClassifier("Node");
            root.eSet(node.getEStructuralFeature("leaf"), leaf);

            String json = serialize(root, Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            assertEquals(2, countOccurrences(json, "\"_fingerprint\""),
                    "each distinct package needs its own first-touch announcement: " + json);
            assertTrue(json.contains(mainFingerprint), "root package fingerprint missing: " + json);
            assertTrue(json.contains(otherFingerprint), "foreign package fingerprint missing: " + json);
        }
    }
}
