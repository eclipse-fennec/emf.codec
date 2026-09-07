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

import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.INLINE_MAPPING_SOURCE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_FALLBACK_ECLASS;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_FALLBACK_STRATEGY;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.metadata.provider.CodecAspectProvider;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A mapping annotation naming a class in another package resolves through the
 * {@code MetadataService} the discriminator view was built from, not through
 * {@link EPackage.Registry#INSTANCE} (issue #207).
 * <p>
 * Neither package is registered globally here — the whiteboard is the only publisher, exactly
 * as in an OSGi runtime that does not mirror its packages into the JVM-global registry. A
 * cross-package lookup that falls back to that registry finds nothing there and drops the
 * mapping silently, leaving the discriminator value unresolvable at read time.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/08-discriminator-mapping.md">Spec 08</a>
 */
@DisplayName("Cross-package mapping URIs resolve through the MetadataService (#207)")
class CrossPackageMappingResolutionTest {

    private static final String HOLDER_NS = "http://example.org/holder/1.0";
    private static final String LEAF_NS = "http://example.org/leaf/1.0";

    private MetadataWhiteboard metadataService;
    private EReference itemsReference;
    private EClass leafClass;
    private EClass otherLeafClass;

    @BeforeEach
    void setUp() {
        EPackage leafPackage = EcoreFactory.eINSTANCE.createEPackage();
        leafPackage.setName("leaf");
        leafPackage.setNsPrefix("leaf");
        leafPackage.setNsURI(LEAF_NS);
        leafClass = EcoreFactory.eINSTANCE.createEClass();
        leafClass.setName("Leaf");
        leafPackage.getEClassifiers().add(leafClass);
        otherLeafClass = EcoreFactory.eINSTANCE.createEClass();
        otherLeafClass.setName("OtherLeaf");
        otherLeafClass.getESuperTypes().add(leafClass);
        leafPackage.getEClassifiers().add(otherLeafClass);

        EPackage holderPackage = EcoreFactory.eINSTANCE.createEPackage();
        holderPackage.setName("holder");
        holderPackage.setNsPrefix("holder");
        holderPackage.setNsURI(HOLDER_NS);
        EClass holder = EcoreFactory.eINSTANCE.createEClass();
        holder.setName("Holder");
        itemsReference = EcoreFactory.eINSTANCE.createEReference();
        itemsReference.setName("items");
        itemsReference.setEType(leafClass);
        itemsReference.setContainment(true);
        itemsReference.setUpperBound(-1);
        // Inline mapping pointing across the package boundary, plus a cross-package fallback.
        EAnnotation inline = EcoreFactory.eINSTANCE.createEAnnotation();
        inline.setSource(INLINE_MAPPING_SOURCE);
        inline.getDetails().put("leaf", LEAF_NS + "#//Leaf");
        inline.getDetails().put(KEY_FALLBACK_STRATEGY, "FALLBACK");
        inline.getDetails().put(KEY_FALLBACK_ECLASS, LEAF_NS + "#//OtherLeaf");
        itemsReference.getEAnnotations().add(inline);
        holder.getEStructuralFeatures().add(itemsReference);
        holderPackage.getEClassifiers().add(holder);

        metadataService = MetadataServices.createWhiteboard(new CodecAspectProvider());
        metadataService.registerPackage(leafPackage);
        metadataService.registerPackage(holderPackage);
    }

    @AfterEach
    void tearDown() {
        assertNull(EPackage.Registry.INSTANCE.getEPackage(LEAF_NS),
                "test precondition: nothing may publish the leaf package globally");
    }

    @Test
    @DisplayName("an inline mapping entry naming another package's class resolves")
    void inlineMappingAcrossPackages() {
        TypeDiscriminatorService service =
                TypeDiscriminatorService.fromMetadataService(metadataService);

        String mapId = EcoreUtil.getURI(itemsReference).toString();
        assertSame(leafClass, service.getEClass(mapId, "leaf"),
                "the class lives in a package the MetadataService knows");
    }

    @Test
    @DisplayName("a cross-package fallbackEClass resolves as well")
    void fallbackEClassAcrossPackages() {
        TypeDiscriminatorService service =
                TypeDiscriminatorService.fromMetadataService(metadataService);

        String mapId = EcoreUtil.getURI(itemsReference).toString();
        assertSame(otherLeafClass,
                service.resolve(mapId, "unmapped-value", uri -> null),
                "FALLBACK must land on the configured class, resolved via the service");
    }
}
