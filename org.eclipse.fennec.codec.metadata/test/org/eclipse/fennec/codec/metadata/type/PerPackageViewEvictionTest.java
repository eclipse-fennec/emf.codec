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
package org.eclipse.fennec.codec.metadata.type;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.metadata.provider.CodecAspectProvider;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The per-package view cache releases a package when it is unregistered (issue #81).
 * <p>
 * The cache is static and keyed by {@link PackageMetadata}, which transitively holds the
 * EPackage and every EClass in it. Nothing ever removed an entry, so in a dynamic OSGi
 * deployment - model bundles updating, register/unregister cycles - every version a system had
 * ever seen stayed reachable for the lifetime of the JVM, and a re-registered package was
 * served the view built from its previous incarnation.
 * </p>
 * <p>
 * Identity is the observable here: a cached view is returned as the same instance, an evicted
 * one is rebuilt. That says what the private map holds without reaching into it.
 * </p>
 */
@DisplayName("Per-package view eviction")
class PerPackageViewEvictionTest {

    private static final String NS_URI = "http://example.org/eviction/1.0";

    private MetadataWhiteboard metadataService;
    private PackageMetadata packageMetadata;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServices.createWhiteboard(new CodecAspectProvider());
        packageMetadata = metadataService.registerPackage(buildPackage()).orElseThrow();
    }

    @Test
    @DisplayName("the view is cached while the package is registered")
    void viewIsCachedWhileRegistered() {
        TypeDiscriminatorService view = TypeDiscriminatorService.perPackageView(packageMetadata);

        assertSame(view, TypeDiscriminatorService.perPackageView(packageMetadata),
                "the whole point of the cache is that the view is built once");
    }

    @Test
    @DisplayName("unregistering the package drops the cached view")
    void unregisteringDropsTheCachedView() {
        TypeDiscriminatorService view = TypeDiscriminatorService.perPackageView(packageMetadata);

        new TypeDiscriminatorService().unregisterPackage(packageMetadata);

        assertNotSame(view, TypeDiscriminatorService.perPackageView(packageMetadata),
                "an unregistered package must not stay reachable from a static map");
    }

    @Test
    @DisplayName("the handler callback drops it as well")
    void handlerCallbackDropsTheCachedView() {
        // onPackageUnregistered is what the whiteboard actually calls
        TypeDiscriminatorService service = new TypeDiscriminatorService();
        TypeDiscriminatorService view = TypeDiscriminatorService.perPackageView(packageMetadata);

        service.onPackageUnregistered(packageMetadata);

        assertNotSame(view, TypeDiscriminatorService.perPackageView(packageMetadata),
                "the contract that anticipates unregistration has to honour it");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static EPackage buildPackage() {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("eviction");
        pkg.setNsPrefix("evict");
        pkg.setNsURI(NS_URI);

        EClass entity = EcoreFactory.eINSTANCE.createEClass();
        entity.setName("Entity");
        EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
        name.setName("name");
        name.setEType(EcorePackage.Literals.ESTRING);
        entity.getEStructuralFeatures().add(name);
        pkg.getEClassifiers().add(entity);
        return pkg;
    }
}
