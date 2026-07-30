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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.util.PackageResolver;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests that discriminator-path class-URI resolution goes through the per-load
 * {@link PackageResolver} rather than the global registry (issue #54, remaining B.5 scope).
 * <p>
 * The global registry holds one EPackage per nsURI, so under multi-version it answers with
 * whichever version was registered last. Every resolution site in the codec has to use the
 * binding order and the count-based candidate rule instead — this was the last site that did
 * not.
 * </p>
 */
@DisplayName("FeaturePath class-URI resolution via PackageResolver (B.5)")
class FeaturePathTypeResolverPackageResolutionTest {

    private static final String NS = "http://example.org/featurepath/1.0";
    private static final String TYPE_URI = NS + "#//Entity";

    private MetadataWhiteboard metadataService;
    private EPackage packageA;
    private EPackage packageB;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();
        packageA = buildVersion("valueA");
        packageB = buildVersion("valueB");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(NS);
    }

    private static EPackage buildVersion(String attributeName) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("featurepath");
        pkg.setNsPrefix("fp");
        pkg.setNsURI(NS);

        EClass entity = EcoreFactory.eINSTANCE.createEClass();
        entity.setName("Entity");
        pkg.getEClassifiers().add(entity);

        EAttribute value = EcoreFactory.eINSTANCE.createEAttribute();
        value.setName(attributeName);
        value.setEType(EcorePackage.eINSTANCE.getEString());
        entity.getEStructuralFeatures().add(value);

        new ResourceImpl(URI.createURI(NS)).getContents().add(pkg);
        return pkg;
    }

    private PackageResolver newResolver() {
        return new PackageResolver(metadataService, null);
    }

    @Test
    @DisplayName("a single registered version resolves - unchanged behavior (R1)")
    void singleVersionResolves() {
        metadataService.registerPackage(packageA);

        EClass resolved = FeaturePathTypeResolver.resolveEClassFromUri(TYPE_URI, newResolver());

        assertSame(packageA.getEClassifier("Entity"), resolved);
    }

    @Test
    @DisplayName("two registered versions are an ambiguity error, not a silent last-wins pick")
    void multiVersionIsAmbiguityError() {
        metadataService.registerPackage(packageA);
        metadataService.registerPackage(packageB);

        // The global registry would have answered with one of them and hidden the problem.
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> FeaturePathTypeResolver.resolveEClassFromUri(TYPE_URI, newResolver()));

        assertTrue(ex.getMessage().contains(NS),
                "the error must name the ambiguous nsURI: " + ex.getMessage());
    }

    @Test
    @DisplayName("an established pin selects the version, even with several registered")
    void pinSelectsVersion() {
        metadataService.registerPackage(packageA);
        metadataService.registerPackage(packageB);

        PackageResolver resolver = newResolver();
        // A caller's root option pins the version for this load; resolution must follow it
        // instead of reporting ambiguity.
        resolver.pin(packageB);

        assertSame(packageB.getEClassifier("Entity"),
                FeaturePathTypeResolver.resolveEClassFromUri(TYPE_URI, resolver));
    }

    @Test
    @DisplayName("without a resolver the global registry still answers - foreign/plain-EMF packages")
    void withoutResolverFallsBackToGlobalRegistry() {
        EPackage.Registry.INSTANCE.put(NS, packageA);

        assertSame(packageA.getEClassifier("Entity"),
                FeaturePathTypeResolver.resolveEClassFromUri(TYPE_URI, null));
    }

    @Test
    @DisplayName("an unknown nsURI resolves to null rather than throwing")
    void unknownNsUriIsNull() {
        assertNull(FeaturePathTypeResolver.resolveEClassFromUri(
                "http://example.org/nowhere/1.0#//Missing", newResolver()));
    }
}
