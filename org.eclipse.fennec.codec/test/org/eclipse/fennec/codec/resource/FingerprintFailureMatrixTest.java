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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
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
 * The complete load-side case matrix for a fingerprint carried by the data stream
 * (issue #73, B.2 / R4).
 * <p>
 * Derived from {@code docs/codec-v2-spec/13-load-save-options.md} §2.10/§2.11. The shared line
 * through all of it: strictness governs tolerance towards <b>data</b>, and an explicit signal
 * is never silently degraded — every deviation is either a warning or an error, never nothing.
 * </p>
 * <p>
 * Documents are written by hand here rather than round-tripped, because several of these cases
 * (an unknown fingerprint, a fingerprint contradicting the caller) cannot be produced by the
 * writer at all — they arise from foreign data, older data or a canonicalization-scheme bump.
 * </p>
 */
@DisplayName("Fingerprint load-side failure matrix (#73 B.2)")
class FingerprintFailureMatrixTest {

    private static final String NS = "http://example.org/fpmatrix/1.0";
    private static final String TYPE_URI = NS + "#//Entity";
    private static final String UNKNOWN_FINGERPRINT = "fp1:0000000000000000000000000000000000000000000000000000000000000000";

    private MetadataWhiteboard metadataService;
    private EPackage packageA;
    private EPackage packageB;
    private String fingerprintA;
    private String fingerprintB;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();
        packageA = buildVersion("valueA");
        packageB = buildVersion("valueB");
        fingerprintA = metadataService.registerPackage(packageA).getModelFingerprint();
        fingerprintB = metadataService.registerPackage(packageB).getModelFingerprint();
    }

    private static EPackage buildVersion(String attributeName) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("entity");
        pkg.setNsPrefix("entity");
        pkg.setNsURI(NS);

        EClass entity = EcoreFactory.eINSTANCE.createEClass();
        entity.setName("Entity");
        pkg.getEClassifiers().add(entity);

        EAttribute value = EcoreFactory.eINSTANCE.createEAttribute();
        value.setName(attributeName);
        value.setEType(EcorePackage.eINSTANCE.getEString());
        entity.getEStructuralFeatures().add(value);

        // Resource-backed so EcoreUtil.getURI yields the canonical nsURI#//Entity.
        new ResourceImpl(URI.createURI(NS)).getContents().add(pkg);
        return pkg;
    }

    private String documentWith(String fingerprint, String attributeName, String value) {
        String fingerprintField = fingerprint == null
                ? ""
                : "\"_fingerprint\":\"" + fingerprint + "\",";
        return "{\"_type\":\"" + TYPE_URI + "\"," + fingerprintField
                + "\"" + attributeName + "\":\"" + value + "\"}";
    }

    private List<EObject> load(String json, Map<String, Object> options) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://fp-matrix.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);
        return resource.getContents();
    }

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

    private static Map<String, Object> strict() {
        return Map.of(CodecOptions.CODEC_DESERIALIZATION_MODE, "STRICT");
    }

    // ========================================================================
    // Section 1: The fingerprint resolves
    // ========================================================================

    @Nested
    @DisplayName("1. Stream fingerprint resolves")
    class Resolves {

        @Test
        @DisplayName("1.1 it selects the named version, where the nsURI alone could not")
        void selectsNamedVersion() throws IOException {
            List<EObject> roots = load(documentWith(fingerprintB, "valueB", "X"), Map.of());

            assertEquals(1, roots.size());
            assertSame(packageB.getEClassifier("Entity"), roots.get(0).eClass());
            assertEquals("X", roots.get(0).eGet(
                    roots.get(0).eClass().getEStructuralFeature("valueB")));
        }

        @Test
        @DisplayName("1.2 it works the same in STRICT mode - a resolvable signal is no problem")
        void resolvableIsFineInStrictMode() throws IOException {
            List<EObject> roots = load(documentWith(fingerprintA, "valueA", "X"), strict());

            assertSame(packageA.getEClassifier("Entity"), roots.get(0).eClass());
        }
    }

    // ========================================================================
    // Section 2: The fingerprint is unknown
    // ========================================================================

    @Nested
    @DisplayName("2. Stream fingerprint unknown (foreign data, old data, scheme bump)")
    class Unknown {

        @Test
        @DisplayName("2.1 LENIENT falls back to the nsURI path, which then reports the ambiguity")
        void lenientFallsBackToNsUriPath() {
            // Two versions are registered, so the nsURI fallback is itself ambiguous. The point
            // is that the fallback happens at all: the unknown fingerprint does not abort the
            // load, the nsURI rule does.
            Exception ex = assertThrows(Exception.class,
                    () -> load(documentWith(UNKNOWN_FINGERPRINT, "valueA", "X"), Map.of()));

            String message = collectMessages(ex);
            assertTrue(message.contains(NS),
                    "the failure must come from the nsURI candidate rule, not the fingerprint: "
                    + message);
        }

        @Test
        @DisplayName("2.2 LENIENT resolves via the nsURI when that is unambiguous - data stays readable")
        void lenientResolvesViaUnambiguousNsUri() throws IOException {
            // The single-version case is what a canonicalization-scheme bump looks like: the
            // model did not change, but its old fingerprint no longer matches anything.
            MetadataWhiteboard singleVersion = MetadataServiceFactory.create();
            EPackage only = buildVersion("valueOnly");
            singleVersion.registerPackage(only);

            CodecResource resource = new CodecResource(
                    URI.createURI("test://fp-matrix-single.json"),
                    singleVersion, ConfigurationResolver.defaults(), null);
            String json = documentWith(UNKNOWN_FINGERPRINT, "valueOnly", "X");
            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            assertEquals(1, resource.getContents().size(),
                    "an unresolvable fingerprint must not make readable data unreadable");
            assertSame(only.getEClassifier("Entity"), resource.getContents().get(0).eClass());
        }

        @Test
        @DisplayName("2.3 STRICT rejects it, naming the unknown fingerprint")
        void strictRejects() {
            Exception ex = assertThrows(Exception.class,
                    () -> load(documentWith(UNKNOWN_FINGERPRINT, "valueA", "X"), strict()));

            String message = collectMessages(ex);
            assertTrue(message.contains(UNKNOWN_FINGERPRINT),
                    "STRICT must name the offending value: " + message);
        }
    }

    // ========================================================================
    // Section 3: The caller disagrees with the document
    // ========================================================================

    @Nested
    @DisplayName("3. Caller fingerprint vs stream fingerprint (signal contract, spec 2.11)")
    class CallerVersusStream {

        @Test
        @DisplayName("3.1 LENIENT: the caller's directive wins over the document")
        void callerWinsInLenientMode() throws IOException {
            // The document says B, the caller says A. A migration reader deliberately reading
            // old data against a chosen version must be able to overrule the document.
            List<EObject> roots = load(documentWith(fingerprintB, "valueB", "X"),
                    Map.of(CodecOptions.CODEC_ROOT_FINGERPRINT, fingerprintA));

            assertSame(packageA.getEClassifier("Entity"), roots.get(0).eClass(),
                    "the caller's fingerprint is a directive and must beat the stream");
        }

        @Test
        @DisplayName("3.2 STRICT: the contradiction is an error naming both fingerprints")
        void contradictionIsErrorInStrictMode() {
            Exception ex = assertThrows(Exception.class,
                    () -> load(documentWith(fingerprintB, "valueB", "X"), Map.of(
                            CodecOptions.CODEC_ROOT_FINGERPRINT, fingerprintA,
                            CodecOptions.CODEC_DESERIALIZATION_MODE, "STRICT")));

            String message = collectMessages(ex);
            assertTrue(message.contains(fingerprintA) && message.contains(fingerprintB),
                    "both sides of the contradiction must be named: " + message);
        }

        @Test
        @DisplayName("3.3 agreement between caller and document is a silent no-op")
        void agreementIsSilent() throws IOException {
            List<EObject> roots = load(documentWith(fingerprintA, "valueA", "X"), Map.of(
                    CodecOptions.CODEC_ROOT_FINGERPRINT, fingerprintA,
                    CodecOptions.CODEC_DESERIALIZATION_MODE, "STRICT"));

            assertSame(packageA.getEClassifier("Entity"), roots.get(0).eClass(),
                    "redundant-but-consistent must not be treated as a conflict");
        }

        @Test
        @DisplayName("3.4 an unknown stream fingerprint alongside a caller fingerprint: caller wins")
        void callerWinsOverUnknownStreamFingerprint() throws IOException {
            List<EObject> roots = load(documentWith(UNKNOWN_FINGERPRINT, "valueA", "X"),
                    Map.of(CodecOptions.CODEC_ROOT_FINGERPRINT, fingerprintA));

            assertSame(packageA.getEClassifier("Entity"), roots.get(0).eClass(),
                    "an unresolvable claim in the data cannot unseat an explicit caller choice");
        }
    }
}
