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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@code idFeatures} pointing at a containment EReference (issue #120, spec 09-id.md §4).
 * <p>
 * The identity lives in the contained object and is built from <b>its</b> id configuration —
 * its features, its separator. The writer used to stringify the reference value itself, which
 * put an object's {@code toString()} including its identity hash into {@code _id}: unusable,
 * and different on every run.
 * </p>
 * <p>
 * The contained object stays in the payload. Unlike an id attribute it is not suppressed,
 * because it is the only source of the data (spec §4).
 * </p>
 */
@DisplayName("Reference-based id")
class ReferenceBasedIdTest {

    private static final String TEST_ECORE = "test-id.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass holderClass;
    private EClass containedClass;
    private EReference myIdReference;
    private EAttribute labelAttribute;
    private EAttribute userGroupAttribute;
    private EAttribute userIdAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, ReferenceBasedIdTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        holderClass = EcoreHelper.getEClass(testPackage, "IdHolder");
        containedClass = EcoreHelper.getEClass(testPackage, "ContainedId");
        myIdReference = (EReference) EcoreHelper.getFeature(holderClass, "myId");
        labelAttribute = (EAttribute) EcoreHelper.getFeature(holderClass, "label");
        userGroupAttribute = (EAttribute) EcoreHelper.getFeature(containedClass, "userGroup");
        userIdAttribute = (EAttribute) EcoreHelper.getFeature(containedClass, "userId");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("the id comes from the contained object's own id configuration")
    void idComesFromContainedObject() throws IOException {
        String json = serialize(holder("sales", 42L));

        assertTrue(json.contains("\"_id\":\"sales_42\""),
                "components in the contained order, joined by its separator, was: " + json);
    }

    @Test
    @DisplayName("no object toString leaks into the id")
    void noObjectToStringInId() throws IOException {
        String json = serialize(holder("sales", 42L));

        assertFalse(json.contains("Impl@"),
                "an object's identity hash must never end up in the id, was: " + json);
    }

    @Test
    @DisplayName("the contained object stays in the payload")
    void containedObjectStaysInPayload() throws IOException {
        String json = serialize(holder("sales", 42L));

        assertTrue(json.contains("\"myId\""),
                "the contained object is the only source of the data, was: " + json);
    }

    @Test
    @DisplayName("the contained object round-trips")
    void containedObjectRoundTrips() throws IOException {
        String json = serialize(holder("sales", 42L));

        EObject loaded = deserialize(json);
        EObject contained = (EObject) loaded.eGet(myIdReference);
        assertNotNull(contained, "the contained object must be restored");
        assertEquals("sales", contained.eGet(userGroupAttribute));
        assertEquals(42L, contained.eGet(userIdAttribute));
    }

    @Test
    @DisplayName("an empty reference yields no id rather than a guessed one")
    void emptyReferenceYieldsNoId() throws IOException {
        EObject holder = testPackage.getEFactoryInstance().create(holderClass);
        holder.eSet(labelAttribute, "no id here");

        String json = serialize(holder);

        assertFalse(json.contains("\"_id\""),
                "without a contained object there is no identity, was: " + json);
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject holder(String userGroup, long userId) {
        EObject contained = testPackage.getEFactoryInstance().create(containedClass);
        contained.eSet(userGroupAttribute, userGroup);
        contained.eSet(userIdAttribute, userId);

        EObject holder = testPackage.getEFactoryInstance().create(holderClass);
        holder.eSet(myIdReference, contained);
        holder.eSet(labelAttribute, "example");
        return holder;
    }

    private CodecResource resource() {
        return new CodecResource(URI.createURI("test://reference-id.json"),
                metadataService, ConfigurationResolver.defaults(), null);
    }

    private String serialize(EObject object) throws IOException {
        CodecResource resource = resource();
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json) throws IOException {
        CodecResource resource = resource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, holderClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().get(0);
    }
}
