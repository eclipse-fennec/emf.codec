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
package org.eclipse.fennec.codec.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PackageResolver} — the B.5 binding source order and the A.3
 * count-based candidate rule (issue #54).
 */
@DisplayName("PackageResolver (B.5 / A.3)")
class PackageResolverTest {

    private static final String NS_URI = "http://example.org/entity/1.0";
    private static final String TYPE_URI = NS_URI + "#//Entity";
    private static final String FOREIGN_NS = "http://example.org/foreign/1.0";

    private MetadataWhiteboard metadataService;
    private EPackage packageA;
    private EPackage packageB;
    private String fingerprintA;
    private String fingerprintB;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();
        packageA = buildVersion("alpha");
        packageB = buildVersion("beta");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(FOREIGN_NS);
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

    private void registerBoth() {
        fingerprintA = metadataService.registerPackage(packageA).getModelFingerprint();
        fingerprintB = metadataService.registerPackage(packageB).getModelFingerprint();
    }

    private PackageResolver resolver() {
        return new PackageResolver(metadataService, null);
    }

    @Test
    @DisplayName("exactly one registered version resolves to it (R1)")
    void singleVersionResolves() throws IOException {
        fingerprintA = metadataService.registerPackage(packageA).getModelFingerprint();
        assertSame(packageA, resolver().resolveEPackage(NS_URI, null));
    }

    @Test
    @DisplayName("more than one version without a fingerprint is an ambiguity error (A.3)")
    void multipleVersionsWithoutFingerprintIsError() {
        registerBoth();
        IOException ex = assertThrows(IOException.class,
                () -> resolver().resolvePackage(NS_URI, null));
        assertTrue(ex.getMessage().contains(fingerprintA), "error must list candidate fingerprints");
        assertTrue(ex.getMessage().contains(fingerprintB), "error must list candidate fingerprints");
    }

    @Test
    @DisplayName("fingerprint short-circuits to the exact version even with multiple candidates")
    void fingerprintSelectsVersion() throws IOException {
        registerBoth();
        assertSame(packageA, resolver().resolveEPackage(NS_URI, fingerprintA));
        assertSame(packageB, resolver().resolveEPackage(NS_URI, fingerprintB));
    }

    @Test
    @DisplayName("unknown fingerprint is an error")
    void unknownFingerprintIsError() {
        registerBoth();
        assertThrows(IOException.class, () -> resolver().resolvePackage(NS_URI, "fp1:nope"));
    }

    @Test
    @DisplayName("a pin disambiguates multiple versions (no error)")
    void pinDisambiguates() throws IOException {
        registerBoth();
        PackageResolver resolver = resolver();
        resolver.pin(packageA);
        assertSame(packageA, resolver.resolveEPackage(NS_URI, null));
    }

    @Test
    @DisplayName("resolveEClassFromTypeUri returns the correct version's EClass")
    void resolveEClassFromTypeUriPerVersion() throws IOException {
        registerBoth();
        EClass a = resolver().resolveEClassFromTypeUri(TYPE_URI, fingerprintA);
        assertSame(packageA.getEClassifier("Entity"), a);
        EClass b = resolver().resolveEClassFromTypeUri(TYPE_URI, fingerprintB);
        assertSame(packageB.getEClassifier("Entity"), b);
    }

    @Test
    @DisplayName("nsURI unknown to the service falls through to the global registry (tier 4)")
    void unknownNsUriFallsToGlobalRegistry() throws IOException {
        EPackage foreign = EcoreFactory.eINSTANCE.createEPackage();
        foreign.setName("foreign");
        foreign.setNsPrefix("foreign");
        foreign.setNsURI(FOREIGN_NS);
        EPackage.Registry.INSTANCE.put(FOREIGN_NS, foreign);

        PackageResolver resolver = resolver();
        // Not registered in the MetadataService -> resolvePackage returns null (unknown)...
        assertNull(resolver.resolvePackage(FOREIGN_NS, null));
        // ...but resolveEPackage falls through to the global registry.
        assertSame(foreign, resolver.resolveEPackage(FOREIGN_NS, null));
    }

    @Test
    @DisplayName("blank inputs resolve to null, not an error")
    void blankInputs() throws IOException {
        assertNull(resolver().resolvePackage(null, null));
        assertNull(resolver().resolvePackage("", null));
        assertEquals(null, resolver().resolveEClassFromTypeUri(null, null));
    }
}
