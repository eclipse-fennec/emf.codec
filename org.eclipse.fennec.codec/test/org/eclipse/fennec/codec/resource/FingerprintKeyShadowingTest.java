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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * A model feature must never be eaten by the in-band fingerprint carrier (issue #217).
 * <p>
 * Two rules keep the two apart, and both are exercised here:
 * </p>
 * <ol>
 *   <li><b>Placement.</b> Spec 06 §8.2 gives the fingerprint one key with two placements: the
 *       {@code _}-prefixed sibling next to the type in PLAIN, and the unprefixed inner key
 *       <i>inside</i> the STRUCTURED type object. A reader that accepts the inner form in the
 *       sibling placement reserves {@code fingerprint} everywhere and swallows the model's own
 *       attribute of that name.</li>
 *   <li><b>The model wins.</b> Where a key could still be both - a feature actually named
 *       {@code _fingerprint} - the declared feature takes it, the same rule the reference key
 *       already follows for OpenAPI's {@code $ref}.</li>
 * </ol>
 * <p>
 * The carrier itself must keep working throughout, so every shadowing case is paired with a
 * document that genuinely needs the fingerprint to pick its version.
 * </p>
 */
@DisplayName("Fingerprint key vs. model feature (#217)")
class FingerprintKeyShadowingTest {

    private static final String NS_AUDIT = "http://example.org/audit/1.0";
    private static final String AUDIT_TYPE = NS_AUDIT + "#//AuditRecord";
    private static final String NS_VERSIONED = "http://example.org/versioned/1.0";
    private static final String VERSIONED_TYPE = NS_VERSIONED + "#//Entity";

    private MetadataWhiteboard metadataService;
    private EPackage auditPackage;
    private EPackage versionedV1;
    private EPackage versionedV2;
    private String fingerprintV2;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();

        auditPackage = buildAuditPackage();
        metadataService.registerPackage(auditPackage).orElseThrow();

        versionedV1 = buildVersionedPackage("valueV1");
        versionedV2 = buildVersionedPackage("valueV2");
        metadataService.registerPackage(versionedV1).orElseThrow();
        fingerprintV2 = metadataService.registerPackage(versionedV2).orElseThrow().getModelFingerprint();
    }

    /**
     * The model under test: an audit record whose own attribute is called {@code fingerprint},
     * plus a class whose attribute carries the PLAIN sibling name {@code _fingerprint}.
     */
    private static EPackage buildAuditPackage() {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("audit");
        pkg.setNsPrefix("audit");
        pkg.setNsURI(NS_AUDIT);

        EClass record = EcoreFactory.eINSTANCE.createEClass();
        record.setName("AuditRecord");
        pkg.getEClassifiers().add(record);
        record.getEStructuralFeatures().add(attribute("fingerprint"));
        record.getEStructuralFeatures().add(attribute("note"));

        EReference related = EcoreFactory.eINSTANCE.createEReference();
        related.setName("related");
        related.setEType(record);
        related.setContainment(false);
        record.getEStructuralFeatures().add(related);

        EClass shadow = EcoreFactory.eINSTANCE.createEClass();
        shadow.setName("ShadowRecord");
        pkg.getEClassifiers().add(shadow);
        shadow.getEStructuralFeatures().add(attribute("_fingerprint"));

        EReference shadowRef = EcoreFactory.eINSTANCE.createEReference();
        shadowRef.setName("shadow");
        shadowRef.setEType(shadow);
        shadowRef.setContainment(true);
        record.getEStructuralFeatures().add(shadowRef);

        return backWithResource(pkg);
    }

    /**
     * Two versions of one nsURI, each with an attribute named {@code fingerprint}: the version
     * question can only be answered by the carrier, and the attribute must survive the answer.
     */
    private static EPackage buildVersionedPackage(String valueName) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("versioned");
        pkg.setNsPrefix("versioned");
        pkg.setNsURI(NS_VERSIONED);

        EClass entity = EcoreFactory.eINSTANCE.createEClass();
        entity.setName("Entity");
        pkg.getEClassifiers().add(entity);
        entity.getEStructuralFeatures().add(attribute("fingerprint"));
        entity.getEStructuralFeatures().add(attribute(valueName));

        return backWithResource(pkg);
    }

    private static EAttribute attribute(String name) {
        EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
        attribute.setName(name);
        attribute.setEType(EcorePackage.eINSTANCE.getEString());
        return attribute;
    }

    /** Resource-backed so {@code EcoreUtil.getURI} yields the canonical {@code nsURI#//Name}. */
    private static EPackage backWithResource(EPackage pkg) {
        new ResourceImpl(URI.createURI(pkg.getNsURI())).getContents().add(pkg);
        return pkg;
    }

    private CodecResource newResource(String name) {
        return new CodecResource(URI.createURI("fp-shadow-" + name + ".json"),
                metadataService, ConfigurationResolver.defaults(), null);
    }

    private String save(List<EObject> roots) throws IOException {
        CodecResource resource = newResource("save");
        resource.getContents().addAll(roots);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());
        return out.toString(StandardCharsets.UTF_8);
    }

    private CodecResource loadInto(String json) throws IOException {
        CodecResource resource = newResource("load");
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());
        return resource;
    }

    private List<EObject> load(String json) throws IOException {
        return loadInto(json).getContents();
    }

    private EObject newAuditRecord(String fingerprintValue, String note) {
        EClass record = (EClass) auditPackage.getEClassifier("AuditRecord");
        EObject instance = auditPackage.getEFactoryInstance().create(record);
        instance.eSet(record.getEStructuralFeature("fingerprint"), fingerprintValue);
        instance.eSet(record.getEStructuralFeature("note"), note);
        return instance;
    }

    private static Object valueOf(EObject eObject, String featureName) {
        return eObject.eGet(eObject.eClass().getEStructuralFeature(featureName));
    }

    // ========================================================================
    // Section 1: The model's own attribute
    // ========================================================================

    @Nested
    @DisplayName("1. An attribute named 'fingerprint' is data")
    class ModelAttributeSurvives {

        @Test
        @DisplayName("1.1 it round-trips through JSON")
        void roundTrips() throws IOException {
            String json = save(List.of(newAuditRecord("sha256:abcdef", "nightly build")));
            assertTrue(json.contains("\"fingerprint\":\"sha256:abcdef\""),
                    "the writer emits the attribute under its own name: " + json);

            List<EObject> loaded = load(json);
            assertEquals(1, loaded.size(), json);
            assertEquals("sha256:abcdef", valueOf(loaded.get(0), "fingerprint"),
                    "the attribute must come back, not be consumed as the in-band carrier");
            assertEquals("nightly build", valueOf(loaded.get(0), "note"));
        }

        @Test
        @DisplayName("1.2 it is read from a hand-written document")
        void readFromForeignDocument() throws IOException {
            String json = "{\"_type\":\"" + AUDIT_TYPE + "\",\"fingerprint\":\"x\",\"note\":\"n\"}";

            List<EObject> loaded = load(json);
            assertEquals("x", valueOf(loaded.get(0), "fingerprint"));
            assertEquals("n", valueOf(loaded.get(0), "note"));
        }

        @Test
        @DisplayName("1.3 it survives inside a reference projection")
        void readFromReferenceProjection() throws IOException {
            String json = "{\"_type\":\"" + AUDIT_TYPE + "\",\"note\":\"root\","
                    + "\"related\":{\"_ref\":\"other.json#/\",\"fingerprint\":\"p\"}}";

            EObject root = load(json).get(0);
            EObject related = (EObject) root.eGet(
                    root.eClass().getEStructuralFeature("related"), false);
            assertNotNull(related, "the projection object must be built: " + json);
            assertEquals("p", valueOf(related, "fingerprint"),
                    "a projected attribute named 'fingerprint' is data, not the carrier");
        }

        @Test
        @DisplayName("1.4 the model wins even for the PLAIN sibling name '_fingerprint'")
        void declaredFeatureBeatsTheSiblingKey() throws IOException {
            // Nested, so the containment reference names the class before the key is read -
            // which is what lets the model-first rule answer at all (see 1.5).
            String json = "{\"_type\":\"" + AUDIT_TYPE + "\",\"note\":\"root\","
                    + "\"shadow\":{\"_fingerprint\":\"s\"}}";

            EObject root = load(json).get(0);
            EObject shadow = (EObject) valueOf(root, "shadow");
            assertNotNull(shadow, "the contained object must be built: " + json);
            assertEquals("s", valueOf(shadow, "_fingerprint"),
                    "a declared feature takes its key back from the codec, as for '$ref'");
        }

        @Test
        @DisplayName("1.5 where the class is not yet known, the collision is reported, not silent")
        void unknowableCollisionIsReported() throws IOException {
            // A root with no hint: the sibling key arrives before the type is resolved, and
            // resolving the type is what the fingerprint is for (spec 06 §8.5). The codec
            // cannot ask the model here - but it must not lose the value quietly either.
            String json = "{\"_type\":\"" + NS_AUDIT + "#//ShadowRecord\",\"_fingerprint\":\"s\"}";

            CodecResource resource = loadInto(json);
            assertTrue(resource.getWarnings().stream()
                            .anyMatch(w -> w.getMessage().contains("_fingerprint")
                                    && w.getMessage().contains("ShadowRecord")),
                    "the shadowed feature has to be named in a diagnostic, was: "
                            + resource.getWarnings());
        }
    }

    // ========================================================================
    // Section 2: The carrier still carries
    // ========================================================================

    @Nested
    @DisplayName("2. The in-band carrier keeps working")
    class CarrierStillWorks {

        @Test
        @DisplayName("2.1 the PLAIN sibling picks the version while the attribute is read")
        void plainSiblingSelectsVersion() throws IOException {
            String json = "{\"_type\":\"" + VERSIONED_TYPE + "\",\"_fingerprint\":\"" + fingerprintV2
                    + "\",\"fingerprint\":\"data\",\"valueV2\":\"v\"}";

            EObject loaded = load(json).get(0);
            assertSame(versionedV2, loaded.eClass().getEPackage(),
                    "the sibling key must still select the version the document names");
            assertEquals("data", valueOf(loaded, "fingerprint"));
            assertEquals("v", valueOf(loaded, "valueV2"));
        }

        @Test
        @DisplayName("2.2 the STRUCTURED inner key picks the version while the attribute is read")
        void structuredInnerKeySelectsVersion() throws IOException {
            String json = "{\"_type\":{\"type\":\"" + VERSIONED_TYPE + "\",\"fingerprint\":\""
                    + fingerprintV2 + "\"},\"fingerprint\":\"data\",\"valueV2\":\"v\"}";

            EObject loaded = load(json).get(0);
            assertSame(versionedV2, loaded.eClass().getEPackage(),
                    "inside the type object 'fingerprint' is the carrier, not data");
            assertEquals("data", valueOf(loaded, "fingerprint"),
                    "the sibling of the same name is still the model's attribute");
            assertEquals("v", valueOf(loaded, "valueV2"));
        }
    }
}
