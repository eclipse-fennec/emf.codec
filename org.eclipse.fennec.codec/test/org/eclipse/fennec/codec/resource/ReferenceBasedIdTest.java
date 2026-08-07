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

    @Test
    @DisplayName("the contained object writes its components, not a second id")
    void containedObjectWritesComponents() throws IOException {
        String json = serialize(holder("sales", 42L));

        assertTrue(json.contains("\"userGroup\":\"sales\""),
                "the components are the only source of the data, was: " + json);
        assertTrue(json.contains("\"userId\":42"), "was: " + json);
        assertEquals(1, countOccurrences(json, "\"_id\""),
                "the identity is written once, at the holder, was: " + json);
    }

    @Test
    @DisplayName("the contained type's separator is the one that joins the components")
    void containedSeparatorJoinsComponents() throws IOException {
        // The separator belongs to the type that defines the identity, not to the holder
        String json = serialize(holder("sales", 42L), separatorResolver("-"));

        assertTrue(json.contains("\"_id\":\"sales-42\""),
                "a changed separator must reach the joined value, was: " + json);
    }

    @Test
    @DisplayName("a changed separator round-trips")
    void changedSeparatorRoundTrips() throws IOException {
        ConfigurationResolver config = separatorResolver("-");
        String json = serialize(holder("sales", 42L), config);

        EObject contained = (EObject) deserialize(json, config).eGet(myIdReference);
        assertEquals("sales", contained.eGet(userGroupAttribute));
        assertEquals(42L, contained.eGet(userIdAttribute));
    }

    @Test
    @DisplayName("STRUCTURED writes the joined value under the inner key")
    void structuredUsesInnerKey() throws IOException {
        // One reference is one id feature, so the holder has a single value - which per
        // issue #119 goes under the inner key rather than a feature name
        String json = serialize(holder("sales", 42L), structuredResolver(null));

        assertTrue(json.contains("\"_id\":{\"id\":\"sales_42\"}"),
                "default inner key is 'id', was: " + json);
    }

    @Test
    @DisplayName("STRUCTURED honours a renamed inner key")
    void structuredHonoursRenamedInnerKey() throws IOException {
        String json = serialize(holder("sales", 42L), structuredResolver("xyz"));

        assertTrue(json.contains("\"_id\":{\"xyz\":\"sales_42\"}"),
                "idValueKey must rename the inner key here too, was: " + json);
    }

    @Test
    @DisplayName("STRUCTURED round-trips")
    void structuredRoundTrips() throws IOException {
        ConfigurationResolver config = structuredResolver(null);
        String json = serialize(holder("sales", 42L), config);

        EObject contained = (EObject) deserialize(json, config).eGet(myIdReference);
        assertEquals("sales", contained.eGet(userGroupAttribute));
        assertEquals(42L, contained.eGet(userIdAttribute));
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

    private ConfigurationResolver separatorResolver(String separator) {
        return ConfigurationResolver.builder()
                .resourceProperties(Map.of("codec.eClassConfig",
                        Map.of(containedClass, Map.of("idSeparator", separator))))
                .build();
    }

    private ConfigurationResolver structuredResolver(String valueKey) {
        Map<String, Object> props = new HashMap<>(Map.of("idFormat", "STRUCTURED"));
        if (valueKey != null) {
            props.put("idValueKey", valueKey);
        }
        return ConfigurationResolver.builder().resourceProperties(props).build();
    }

    private int countOccurrences(String haystack, String needle) {
        int count = 0;
        for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + 1)) {
            count++;
        }
        return count;
    }

    private EObject deserialize(String json, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://reference-id.json"),
                metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, holderClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().get(0);
    }

    private CodecResource resource() {
        return new CodecResource(URI.createURI("test://reference-id.json"),
                metadataService, ConfigurationResolver.defaults(), null);
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://reference-id.json"),
                metadataService, resolver, null);
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toString(StandardCharsets.UTF_8);
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
