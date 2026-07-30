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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@code CODEC_ROOT_FINGERPRINT} load option and the {@code EPackage}
 * variant of {@code CODEC_ROOT_SCHEMA} (issue #54, work packages A.2/A.4).
 * <p>
 * Uses two packages sharing one nsURI, identical structure, only the codec {@code key}
 * annotation differs (alpha vs beta) — so their model fingerprints differ (annotations
 * are part of the fingerprint) and a String root type is ambiguous across versions unless
 * disambiguated by the fingerprint. Includes the mandatory R4 negative cases (unknown
 * option fingerprint; option fingerprint conflicting with an instance root option).
 * </p>
 */
@DisplayName("CODEC_ROOT_FINGERPRINT option (A.2/A.4)")
class RootFingerprintOptionTest {

    private static final String NS_URI = "http://example.org/entity/1.0";
    private static final String TYPE_URI = NS_URI + "#//Entity";

    private MetadataWhiteboard metadataService;

    private EClass entityA;
    private EClass entityB;
    private EAttribute valueA;
    private EAttribute valueB;
    private String fingerprintA;
    private String fingerprintB;

    @BeforeEach
    void setUp() {
        EPackage packageA = buildVersion("alpha");
        EPackage packageB = buildVersion("beta");

        entityA = (EClass) packageA.getEClassifier("Entity");
        entityB = (EClass) packageB.getEClassifier("Entity");
        valueA = (EAttribute) entityA.getEStructuralFeature("value");
        valueB = (EAttribute) entityB.getEStructuralFeature("value");

        metadataService = MetadataServiceFactory.create();
        fingerprintA = metadataService.registerPackage(packageA).orElseThrow().getModelFingerprint();
        fingerprintB = metadataService.registerPackage(packageB).orElseThrow().getModelFingerprint();

        // Sanity: the versions must be distinguishable by fingerprint, otherwise the
        // disambiguation/mismatch cases below would be meaningless.
        assertNotEquals(fingerprintA, fingerprintB, "the two versions must have distinct fingerprints");
    }

    private static EPackage buildVersion(String jsonKey) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("entity");
        pkg.setNsPrefix("entity");
        pkg.setNsURI(NS_URI);

        EClass entity = EcoreFactory.eINSTANCE.createEClass();
        entity.setName("Entity");
        pkg.getEClassifiers().add(entity);

        EAttribute value = EcoreFactory.eINSTANCE.createEAttribute();
        value.setName("value");
        value.setEType(EcorePackage.eINSTANCE.getEString());
        entity.getEStructuralFeatures().add(value);

        EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
        ann.setSource(AnnotationSources.CODEC);
        ann.getDetails().put("key", jsonKey);
        value.getEAnnotations().add(ann);

        return pkg;
    }

    private CodecResource newResource() {
        return new CodecResource(
                URI.createURI("test://root-fingerprint.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    private EObject load(String json, Map<String, Object> options) throws IOException {
        CodecResource resource = newResource();
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    @Test
    @DisplayName("String root type + fingerprint resolves the correct version (both variants)")
    void stringRootTypeWithFingerprintResolvesCorrectVersion() throws IOException {
        EObject a = load("{\"alpha\":\"X\"}", Map.of(
                CodecResource.CODEC_ROOT_TYPE, TYPE_URI,
                CodecResource.CODEC_ROOT_FINGERPRINT, fingerprintA));
        assertNotNull(a);
        assertSame(entityA, a.eClass(), "fingerprint A must select version A");
        assertEquals("X", a.eGet(valueA), "version A reads its own key 'alpha'");

        EObject b = load("{\"beta\":\"Y\"}", Map.of(
                CodecResource.CODEC_ROOT_TYPE, TYPE_URI,
                CodecResource.CODEC_ROOT_FINGERPRINT, fingerprintB));
        assertNotNull(b);
        assertSame(entityB, b.eClass(), "fingerprint B must select version B");
        assertEquals("Y", b.eGet(valueB), "version B reads its own key 'beta'");
    }

    @Test
    @DisplayName("R4: unknown option fingerprint is an error")
    void unknownFingerprintIsError() {
        assertThrows(IOException.class, () -> load("{\"alpha\":\"X\"}", Map.of(
                CodecResource.CODEC_ROOT_TYPE, TYPE_URI,
                CodecResource.CODEC_ROOT_FINGERPRINT, "fp1:does-not-exist")),
                "an unknown option fingerprint must fail, not silently fall back");
    }

    @Test
    @DisplayName("R4: fingerprint conflicting with an EClass root type is an error")
    void fingerprintMismatchWithEClassRootTypeIsError() {
        // EClass instance from version B, but fingerprint of version A -> contradiction.
        assertThrows(IOException.class, () -> load("{\"beta\":\"Y\"}", Map.of(
                CodecResource.CODEC_ROOT_TYPE, entityB,
                CodecResource.CODEC_ROOT_FINGERPRINT, fingerprintA)),
                "an EClass root type from a different version than the fingerprint must fail");
    }

    @Test
    @DisplayName("fingerprint consistent with an EClass root type is accepted")
    void fingerprintConsistentWithEClassRootTypeOk() throws IOException {
        EObject a = load("{\"alpha\":\"X\"}", Map.of(
                CodecResource.CODEC_ROOT_TYPE, entityA,
                CodecResource.CODEC_ROOT_FINGERPRINT, fingerprintA));
        assertNotNull(a);
        assertSame(entityA, a.eClass());
        assertEquals("X", a.eGet(valueA));
    }

    @Test
    @DisplayName("CODEC_ROOT_SCHEMA accepts an EPackage instance")
    void rootSchemaAcceptsEPackageInstance() throws IOException {
        EObject a = load("{\"alpha\":\"X\"}", Map.of(
                CodecResource.CODEC_ROOT_TYPE, entityA,
                CodecResource.CODEC_ROOT_SCHEMA, entityA.getEPackage()));
        assertNotNull(a);
        assertSame(entityA, a.eClass());
        assertEquals("X", a.eGet(valueA));
    }

    @Test
    @DisplayName("R4: EPackage schema conflicting with the fingerprint is an error")
    void ePackageSchemaMismatchWithFingerprintIsError() {
        // schema EPackage is version B, fingerprint is version A -> contradiction.
        assertThrows(IOException.class, () -> load("{\"alpha\":\"X\"}", Map.of(
                CodecResource.CODEC_ROOT_TYPE, entityA,
                CodecResource.CODEC_ROOT_SCHEMA, entityB.getEPackage(),
                CodecResource.CODEC_ROOT_FINGERPRINT, fingerprintA)),
                "an EPackage schema from a different version than the fingerprint must fail");
    }
}
