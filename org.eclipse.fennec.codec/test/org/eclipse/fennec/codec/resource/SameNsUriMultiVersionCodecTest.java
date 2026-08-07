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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
 * Coincidence-proof end-to-end test for same-nsURI multi-version config resolution
 * (issue #54 / Phase A, work package A.1).
 * <p>
 * Two {@link EPackage}s share one {@code nsURI} and have <b>identical structure</b>
 * (an {@code Entity} with a single {@code value} EAttribute). The <b>only</b> difference
 * is the configured JSON key on that attribute — package A maps it to {@code "alpha"},
 * package B maps it to {@code "beta"} via a codec {@code key} annotation. Because the
 * structure is identical, the observable output is driven <b>solely</b> by config, so a
 * structure-only test could pass by accident (ser+deser using the same wrong config
 * consistently). The assertions here are therefore keyed to the <b>diverging</b> config
 * (own key present, other variant's key absent) in both directions — the R6
 * coincidence-proof pattern.
 * </p>
 * <p>
 * Against the current class-name-keyed annotation bridge (finding F6) these tests
 * <b>fail</b>: both {@code Entity} versions collide under the bare class name, the
 * last-registered version (B) wins, and version-A objects (de)serialize with B's
 * {@code beta} key. Instance-based resolution (A.1) makes them pass.
 * </p>
 */
@DisplayName("Same-nsURI multi-version codec resolution (A.1)")
class SameNsUriMultiVersionCodecTest {

    private static final String NS_URI = "http://example.org/entity/1.0";

    private MetadataWhiteboard metadataService;

    private EPackage packageA;
    private EPackage packageB;
    private EClass entityA;
    private EClass entityB;
    private EAttribute valueA;
    private EAttribute valueB;

    @BeforeEach
    void setUp() {
        packageA = buildVersion("alpha");
        packageB = buildVersion("beta");

        entityA = (EClass) packageA.getEClassifier("Entity");
        entityB = (EClass) packageB.getEClassifier("Entity");
        valueA = (EAttribute) entityA.getEStructuralFeature("value");
        valueB = (EAttribute) entityB.getEStructuralFeature("value");

        metadataService = MetadataServiceFactory.create();
        // Register A first, then B — so the buggy name-keyed bridge lets B win the
        // "Entity" name and mis-serializes version-A objects with B's config.
        metadataService.registerPackage(packageA);
        metadataService.registerPackage(packageB);
    }

    /**
     * Builds one version of the package: same nsURI, same {@code Entity} class and
     * {@code value} attribute; only the codec {@code key} annotation differs.
     */
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

        // Codec annotation: rename the JSON key of 'value' to the version's key.
        EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
        ann.setSource(AnnotationSources.CODEC);
        ann.getDetails().put("key", jsonKey);
        value.getEAnnotations().add(ann);

        return pkg;
    }

    private EObject createEntity(EPackage pkg, EClass eClass, EAttribute attr, String value) {
        EObject obj = pkg.getEFactoryInstance().create(eClass);
        obj.eSet(attr, value);
        return obj;
    }

    private CodecResource newResource() {
        return new CodecResource(
                URI.createURI("test://same-nsuri-multiversion.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    private String serialize(EObject object) throws IOException {
        CodecResource resource = newResource();
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootType) throws IOException {
        CodecResource resource = load(json, rootType);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private CodecResource load(String json, EClass rootType) throws IOException {
        CodecResource resource = newResource();
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, rootType));
        return resource;
    }

    @Test
    @DisplayName("an ambiguous type resolves via the hint, and says so (issue #131)")
    void ambiguousTypeWarnsWithoutFingerprint() throws IOException {
        // Two versions share an nsURI, so the written type names both of them. Nothing in
        // the document tells them apart - only the caller's hint does. Resolving that
        // silently would hide a genuine ambiguity, so it is a warning rather than a fact.
        // Telling the versions apart from the data alone is what fingerprinting is for.
        CodecResource resource = load(serialize(createEntity(packageA, entityA, valueA, "X")),
                entityA);

        assertTrue(resource.getWarnings().stream()
                        .anyMatch(w -> w.getMessage().contains("Type resolved via fallback")),
                "an ambiguous type must not resolve without a word, was: "
                        + resource.getWarnings());
        assertTrue(resource.getErrors().isEmpty(),
                "but it stays readable, was: " + resource.getErrors());
    }

    @Test
    @DisplayName("each version serializes with its OWN configured key (R6)")
    void serializesWithOwnConfig() throws IOException {
        String jsonA = serialize(createEntity(packageA, entityA, valueA, "X"));
        assertTrue(jsonA.contains("\"alpha\""), "version A must serialize with its own key 'alpha': " + jsonA);
        assertFalse(jsonA.contains("\"beta\""), "version A must NOT use version B's key 'beta': " + jsonA);

        String jsonB = serialize(createEntity(packageB, entityB, valueB, "Y"));
        assertTrue(jsonB.contains("\"beta\""), "version B must serialize with its own key 'beta': " + jsonB);
        assertFalse(jsonB.contains("\"alpha\""), "version B must NOT use version A's key 'alpha': " + jsonB);
    }

    @Test
    @DisplayName("each version deserializes against its OWN key; ignores the other's (R6)")
    void deserializesWithOwnConfig() throws IOException {
        EObject a = deserialize("{\"alpha\":\"X\"}", entityA);
        assertNotNull(a, "version A object should be created");
        assertEquals("X", a.eGet(valueA), "version A must read its own key 'alpha'");

        EObject b = deserialize("{\"beta\":\"Y\"}", entityB);
        assertNotNull(b, "version B object should be created");
        assertEquals("Y", b.eGet(valueB), "version B must read its own key 'beta'");

        // Coincidence-proof cross-negative: version B must NOT read version A's key.
        EObject bFromAlpha = deserialize("{\"alpha\":\"X\"}", entityB);
        assertNotNull(bFromAlpha, "object should still be created");
        assertNull(bFromAlpha.eGet(valueB), "version B must NOT populate its value from version A's key 'alpha'");
    }

    @Test
    @DisplayName("round-trips per version with its own config")
    void roundTripsPerVersion() throws IOException {
        EObject a = deserialize(serialize(createEntity(packageA, entityA, valueA, "X")), entityA);
        assertNotNull(a);
        assertEquals("X", a.eGet(valueA), "version A round-trip must preserve its value");

        EObject b = deserialize(serialize(createEntity(packageB, entityB, valueB, "Y")), entityB);
        assertNotNull(b);
        assertEquals("Y", b.eGet(valueB), "version B round-trip must preserve its value");
    }

    @Test
    @DisplayName("unregistering one version does not corrupt the survivor's config")
    void unregisterDoesNotAffectOther() throws IOException {
        metadataService.unregisterPackage(packageB);

        String jsonA = serialize(createEntity(packageA, entityA, valueA, "X"));
        assertTrue(jsonA.contains("\"alpha\""), "surviving version A must keep its own key 'alpha': " + jsonA);
        assertFalse(jsonA.contains("\"beta\""), "surviving version A must not inherit removed B's key 'beta': " + jsonA);
    }
}
