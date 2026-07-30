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

import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.CODEC_SOURCE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_DISCRIMINATOR;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_DISCRIMINATOR_PATH;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.TYPE_MAPPING_SOURCE_PREFIX;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.metadata.provider.CodecAspectProvider;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for the B.6 per-step composed discriminator view (issue #54): version-scoped
 * composition from pinned packages + value-collision detection.
 * <p>
 * Two same-nsURI versions each declare {@code Entity} under the same mapId with the same
 * discriminator value {@code "X"} (a differentiating codec key on a dummy attribute gives the
 * two versions distinct fingerprints). Composing without a pin therefore sees the value mapped
 * to two different {@code EClass} instances → collision error; pinning one version scopes the
 * view to it → clean resolution.
 * </p>
 */
@DisplayName("Discriminator composed view (B.6)")
class TypeDiscriminatorComposedViewTest {

    private static final String NS_URI = "http://example.org/entity/1.0";
    private static final String MAP_ID = "m";

    private MetadataWhiteboard metadataService;
    private PackageMetadata metaA;
    private PackageMetadata metaB;
    private EClass entityA;
    private EClass entityB;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServices.createWhiteboard(new CodecAspectProvider());
        EPackage packageA = buildVersion("alphaKey");
        EPackage packageB = buildVersion("betaKey");
        entityA = (EClass) packageA.getEClassifier("Entity");
        entityB = (EClass) packageB.getEClassifier("Entity");

        metaA = metadataService.registerPackage(packageA).orElseThrow();
        metaB = metadataService.registerPackage(packageB).orElseThrow();
        // Distinct versions are the premise of the test.
        assertNotEquals(metaA.getModelFingerprint(), metaB.getModelFingerprint(),
                "the two versions must have distinct fingerprints");
    }

    private static EPackage buildVersion(String keyDiff) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("entity");
        pkg.setNsPrefix("entity");
        pkg.setNsURI(NS_URI);

        EClass entity = EcoreFactory.eINSTANCE.createEClass();
        entity.setName("Entity");
        pkg.getEClassifiers().add(entity);

        // Discriminator mapping: mapId "m", value "X".
        EAnnotation mapping = EcoreFactory.eINSTANCE.createEAnnotation();
        mapping.setSource(TYPE_MAPPING_SOURCE_PREFIX + MAP_ID);
        mapping.getDetails().put(KEY_TYPE_DISCRIMINATOR, "X");
        mapping.getDetails().put(KEY_TYPE_DISCRIMINATOR_PATH, "_type");
        entity.getEAnnotations().add(mapping);

        // Dummy attribute with a differentiating codec key so the two versions get distinct
        // fingerprints (annotations are part of the fingerprint).
        EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
        attr.setName("v");
        attr.setEType(EcorePackage.eINSTANCE.getEString());
        entity.getEStructuralFeatures().add(attr);
        EAnnotation key = EcoreFactory.eINSTANCE.createEAnnotation();
        key.setSource(CODEC_SOURCE);
        key.getDetails().put("key", keyDiff);
        attr.getEAnnotations().add(key);

        return pkg;
    }

    @Test
    @DisplayName("no pin + same value across two versions -> collision error")
    void collisionWithoutPinIsError() {
        Function<String, PackageMetadata> noPin = ns -> null;
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> TypeDiscriminatorService.composedFor(metadataService, noPin));
        assertTrue(ex.getMessage().contains("collision") && ex.getMessage().contains("X"),
                "collision error should name the colliding value: " + ex.getMessage());
    }

    @Test
    @DisplayName("pinning a version scopes the composed view to it (no collision)")
    void pinScopesToVersion() {
        Function<String, PackageMetadata> pinA = ns -> NS_URI.equals(ns) ? metaA : null;
        TypeDiscriminatorService composed = TypeDiscriminatorService.composedFor(metadataService, pinA);
        assertSame(entityA, composed.getRegistry(MAP_ID).getEClass("X"),
                "pinned version A must resolve X to its own Entity");

        Function<String, PackageMetadata> pinB = ns -> NS_URI.equals(ns) ? metaB : null;
        TypeDiscriminatorService composedB = TypeDiscriminatorService.composedFor(metadataService, pinB);
        assertSame(entityB, composedB.getRegistry(MAP_ID).getEClass("X"),
                "pinned version B must resolve X to its own Entity");
    }

    @Test
    @DisplayName("single registered version composes without error (R1)")
    void singleVersionComposes() {
        MetadataWhiteboard single = MetadataServices.createWhiteboard(new CodecAspectProvider());
        EPackage only = buildVersion("onlyKey");
        EClass entity = (EClass) only.getEClassifier("Entity");
        single.registerPackage(only);

        TypeDiscriminatorService composed = TypeDiscriminatorService.composedFor(single, ns -> null);
        assertSame(entity, composed.getRegistry(MAP_ID).getEClass("X"));
        assertNull(composed.getRegistry("nope"), "unknown mapId has no registry");
    }
}
