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
package org.eclipse.fennec.codec.tests.tck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TCK for the in-band EPackage fingerprint (issue #73, B.1 / S7).
 * <p>
 * Applies to <b>property-stream</b> formats — JSON, YAML, CBOR, BSON and anything else that can
 * carry an extra named property next to the type. For those the fingerprint is just one more
 * property and the carrier must work identically to JSON.
 * </p>
 * <p>
 * <b>Column formats</b> (CSV, XLSX, ODS, tabular) deliberately have no in-band carrier: a
 * fingerprint would have to become a column, repeated on every row, and the tabular shape has no
 * per-object type context to attach it to. Those formats answer the version question through the
 * option path ({@code codec.rootFingerprint}) instead and must <b>not</b> extend this TCK.
 * </p>
 * <p>
 * Assertions are made on the deserialized object graph rather than on the encoded bytes, so the
 * suite works for binary formats as well. Every positive case is backed by a negative twin with
 * the carrier switched off, which fails — otherwise the format could appear to pass while
 * ignoring the fingerprint entirely.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-type.md#8-in-band-epackage-fingerprint">Spec 06 §8</a>
 */
public abstract class AbstractFingerprintTCK {

    private static final String NS_A = "http://example.org/tck/fingerprint/a/1.0";
    private static final String NS_B = "http://example.org/tck/fingerprint/b/1.0";

    private MetadataWhiteboard metadataService;

    private EPackage packageA1;
    private EPackage packageA2;
    private EPackage packageB1;
    private EPackage packageB2;

    private String fingerprintA1;
    private String fingerprintA2;

    /**
     * Subclasses provide the format provider to test.
     */
    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    /**
     * Returns the file extension for URIs (e.g. "yaml", "cbor").
     */
    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();

        packageB1 = buildB("labelV1");
        packageB2 = buildB("labelV2");
        packageA1 = buildA("titleV1", (EClass) packageB1.getEClassifier("Leaf"));
        packageA2 = buildA("titleV2", (EClass) packageB2.getEClassifier("Leaf"));

        metadataService.registerPackage(packageB1);
        fingerprintA1 = metadataService.registerPackage(packageA1).orElseThrow().getModelFingerprint();
        fingerprintA2 = metadataService.registerPackage(packageA2).orElseThrow().getModelFingerprint();
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
     * Backs a dynamically built package with a Resource named after its nsURI, so the written
     * type value is the canonical {@code nsURI#//Class} rather than a bare fragment.
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

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://tck-fingerprint." + getFileExtension()),
                metadataService,
                ConfigurationResolver.defaults(),
                null, null, createFormatProvider());
    }

    private byte[] save(List<EObject> roots, Map<String, Object> options) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().addAll(roots);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return out.toByteArray();
    }

    /** Loads into a brand-new resource with no version hint of any kind. */
    private List<EObject> load(byte[] data, Map<String, Object> options) throws IOException {
        CodecResource resource = createResource();
        resource.load(new ByteArrayInputStream(data), options);
        return resource.getContents();
    }

    // ========================================================================
    // Tests
    // ========================================================================

    @Nested
    @DisplayName("Fingerprint carrier")
    class Carrier {

        @Test
        @DisplayName("the fixture is genuinely ambiguous without the carrier")
        void ambiguousWithoutCarrier() throws IOException {
            assertNotEquals(fingerprintA1, fingerprintA2, "the two versions must differ");

            byte[] data = save(
                    List.of(createNode(packageA2, "titleV2", "root", packageB2, "labelV2", "leaf")),
                    Map.of());

            assertThrows(Exception.class, () -> load(data, Map.of()),
                    "two versions per nsURI and nothing in the document to choose between them "
                    + "must fail rather than guess - otherwise the positive cases prove nothing");
        }

        @Test
        @DisplayName("several packages and versions round-trip into a fresh resource")
        void roundTripsSeveralPackages() throws IOException {
            byte[] data = save(
                    List.of(createNode(packageA2, "titleV2", "root", packageB2, "labelV2", "leaf")),
                    Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            List<EObject> roots = load(data, Map.of());

            assertEquals(1, roots.size());
            EObject node = roots.get(0);
            assertSame(packageA2.getEClassifier("Node"), node.eClass(),
                    "the root must come back under its own version");
            assertEquals("root", node.eGet(node.eClass().getEStructuralFeature("titleV2")));

            EObject leaf = (EObject) node.eGet(node.eClass().getEStructuralFeature("leaf"));
            assertSame(packageB2.getEClassifier("Leaf"), leaf.eClass(),
                    "the contained object of another package must come back under its own version");
            assertEquals("leaf", leaf.eGet(leaf.eClass().getEStructuralFeature("labelV2")));
        }

        @Test
        @DisplayName("the other version combination round-trips just as well")
        void roundTripsOtherCombination() throws IOException {
            byte[] data = save(
                    List.of(createNode(packageA1, "titleV1", "root", packageB1, "labelV1", "leaf")),
                    Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            List<EObject> roots = load(data, Map.of());

            assertSame(packageA1.getEClassifier("Node"), roots.get(0).eClass(),
                    "version A1 must not come back as the last-registered version");
        }

        @Test
        @DisplayName("STRUCTURED format carries it inside the type object")
        void roundTripsStructured() throws IOException {
            byte[] data = save(
                    List.of(createNode(packageA2, "titleV2", "root", packageB2, "labelV2", "leaf")),
                    Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH",
                            CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED"));

            List<EObject> roots = load(data, Map.of(CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED"));

            assertSame(packageA2.getEClassifier("Node"), roots.get(0).eClass());
        }

        @Test
        @DisplayName("mixed versions of one nsURI keep their own version each")
        void roundTripsMixedVersions() throws IOException {
            // Two versions of one nsURI need two roots to sit side by side, so this case only
            // applies to formats that can hold an array root. BSON documents are objects, so it
            // legitimately cannot express the scenario at all - it is a format limit, not a gap
            // in the carrier.
            if (!createFormatProvider().supportsArrayRoot()) {
                return;
            }

            byte[] data = save(List.of(
                            createNode(packageA1, "titleV1", "first", null, null, null),
                            createNode(packageA2, "titleV2", "second", null, null, null)),
                    Map.of(CodecOptions.CODEC_FINGERPRINT_MODE, "FIRST_TOUCH"));

            List<EObject> roots = load(data, Map.of());

            assertEquals(2, roots.size());
            assertSame(packageA1.getEClassifier("Node"), roots.get(0).eClass());
            assertSame(packageA2.getEClassifier("Node"), roots.get(1).eClass(),
                    "the deviation marker must survive this format");
        }

        @Test
        @DisplayName("default is off: no carrier unless opted in")
        void offByDefault() throws IOException {
            // Registering only one version of each nsURI makes the document loadable without a
            // fingerprint, which is what lets us assert that nothing broke while nothing is written.
            MetadataWhiteboard single = MetadataServiceFactory.create();
            single.registerPackage(packageB2);
            single.registerPackage(packageA2);

            CodecResource writer = new CodecResource(
                    URI.createURI("test://tck-fingerprint-off." + getFileExtension()),
                    single, ConfigurationResolver.defaults(), null, null, createFormatProvider());
            writer.getContents().add(
                    createNode(packageA2, "titleV2", "root", packageB2, "labelV2", "leaf"));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.save(out, Map.of());

            CodecResource reader = new CodecResource(
                    URI.createURI("test://tck-fingerprint-off." + getFileExtension()),
                    single, ConfigurationResolver.defaults(), null, null, createFormatProvider());
            reader.load(new ByteArrayInputStream(out.toByteArray()), Map.of());

            assertEquals(1, reader.getContents().size());
            assertSame(packageA2.getEClassifier("Node"), reader.getContents().get(0).eClass());
        }
    }
}
