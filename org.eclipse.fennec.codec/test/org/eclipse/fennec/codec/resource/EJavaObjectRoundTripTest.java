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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Round trips for {@code EJavaObject} attributes (issue #115).
 * <p>
 * Spec 11-feature.md §9.3 maps {@code Map} to a JSON object and {@code List}/{@code Collection}
 * to a JSON array, and promises lossless round trips for any JSON value. The read side builds
 * maps and lists from JSON; these tests pin that the write side produces the JSON they expect.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Serialization</a>
 */
@DisplayName("EJavaObject round trips")
class EJavaObjectRoundTripTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/ser/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EAttribute nameAttribute;
    private EAttribute metadataAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, EJavaObjectRoundTripTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        metadataAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "metadata");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("a Map is written as a JSON object and read back as a Map")
    void mapRoundTrips() throws IOException {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("enabled", Boolean.TRUE);
        metadata.put("count", 42L);
        metadata.put("label", "primary");

        String json = serialize(person("Alice", metadata));
        assertTrue(json.contains("\"enabled\":true"),
                "a Map must be written as a JSON object, was: " + json);

        Object restored = deserialize(json).eGet(metadataAttribute);
        assertInstanceOf(Map.class, restored, "a JSON object must come back as a Map");
        assertEquals(metadata, restored);
    }

    @Test
    @DisplayName("a List is written as a JSON array and read back as a List")
    void listRoundTrips() throws IOException {
        List<Object> metadata = List.of("a", "b", "c");

        String json = serialize(person("Alice", metadata));
        assertTrue(json.contains("[\"a\",\"b\",\"c\"]"),
                "a List must be written as a JSON array, was: " + json);

        Object restored = deserialize(json).eGet(metadataAttribute);
        assertInstanceOf(List.class, restored, "a JSON array must come back as a List");
        assertEquals(metadata, restored);
    }

    @Test
    @DisplayName("nested maps and lists round trip unchanged")
    void nestedStructureRoundTrips() throws IOException {
        Map<String, Object> inner = new LinkedHashMap<>();
        inner.put("timeout", 30L);

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("tags", List.of("x", "y"));
        metadata.put("config", inner);
        metadata.put("empty", List.of());

        String json = serialize(person("Alice", metadata));
        Object restored = deserialize(json).eGet(metadataAttribute);

        assertEquals(metadata, restored, "nesting must survive the round trip: " + json);
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject person(String name, Object metadata) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttribute, name);
        person.eSet(metadataAttribute, metadata);
        return person;
    }

    private CodecResource resource() {
        return new CodecResource(URI.createURI("test://ejavaobject.json"),
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
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
                Collections.emptyMap());
        return resource.getContents().get(0);
    }
}
