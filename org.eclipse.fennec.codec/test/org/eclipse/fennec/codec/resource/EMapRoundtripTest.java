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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Roundtrip tests for EMap serialization/deserialization in codec.v2.
 * <p>
 * These tests verify that:
 * <ol>
 *   <li>An EObject with EMaps can be serialized to JSON</li>
 *   <li>The JSON can be deserialized back to an EObject</li>
 *   <li>The deserialized EObject is equivalent to the original</li>
 * </ol>
 * </p>
 */
@DisplayName("EMap Roundtrip Tests")
class EMapRoundtripTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-emap.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass containerClass;
    private EClass itemClass;

    // EReferences
    private EReference itemsRef;
    private EReference metadataRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(EMapRoundtripTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);

        // Register package in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load EClasses
        containerClass = ecoreHelper.getEClass(testPackage, "Container");
        itemClass = ecoreHelper.getEClass(testPackage, "Item");

        // Load EReferences
        itemsRef = (EReference) ecoreHelper.getFeature(containerClass, "items");
        metadataRef = (EReference) ecoreHelper.getFeature(containerClass, "metadata");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://emap-roundtrip.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    private String serialize(EObject eObject) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(eObject);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, new HashMap<>());
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json) throws IOException {
        CodecResource resource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, containerClass);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private EObject createContainer(String name) {
        EObject container = testPackage.getEFactoryInstance().create(containerClass);
        container.eSet(containerClass.getEStructuralFeature("name"), name);
        return container;
    }

    private EObject createItem(String id, int value, boolean active) {
        EObject item = testPackage.getEFactoryInstance().create(itemClass);
        item.eSet(itemClass.getEStructuralFeature("id"), id);
        item.eSet(itemClass.getEStructuralFeature("value"), value);
        item.eSet(itemClass.getEStructuralFeature("active"), active);
        return item;
    }

    // ========================================================================
    // Roundtrip Tests
    // ========================================================================

    @Nested
    @DisplayName("EMap Roundtrip")
    class EMapRoundtrip {

        @Test
        @DisplayName("roundtrip EMap with object values")
        void roundtripEMapWithObjectValues() throws IOException {
            // Create original
            EObject original = createContainer("Roundtrip Test");

            @SuppressWarnings("unchecked")
            EMap<String, EObject> items = (EMap<String, EObject>) original.eGet(itemsRef);
            items.put("first", createItem("1", 100, true));
            items.put("second", createItem("2", 200, false));

            // Serialize
            String json = serialize(original);

            // Deserialize
            EObject restored = deserialize(json);

            // Verify
            assertNotNull(restored, "Restored object should not be null");
            assertEquals(
                    original.eGet(containerClass.getEStructuralFeature("name")),
                    restored.eGet(containerClass.getEStructuralFeature("name")),
                    "Name should match");

            @SuppressWarnings("unchecked")
            EMap<String, EObject> restoredItems = (EMap<String, EObject>) restored.eGet(itemsRef);

            assertEquals(2, restoredItems.size(), "Should have 2 items");

            // Verify first item
            EObject firstItem = restoredItems.get("first");
            assertNotNull(firstItem, "Should have 'first' item");
            assertEquals("1", firstItem.eGet(itemClass.getEStructuralFeature("id")));
            assertEquals(100, firstItem.eGet(itemClass.getEStructuralFeature("value")));
            assertEquals(true, firstItem.eGet(itemClass.getEStructuralFeature("active")));

            // Verify second item
            EObject secondItem = restoredItems.get("second");
            assertNotNull(secondItem, "Should have 'second' item");
            assertEquals("2", secondItem.eGet(itemClass.getEStructuralFeature("id")));
            assertEquals(200, secondItem.eGet(itemClass.getEStructuralFeature("value")));
            assertEquals(false, secondItem.eGet(itemClass.getEStructuralFeature("active")));
        }

        @Test
        @DisplayName("roundtrip EMap with string values")
        void roundtripEMapWithStringValues() throws IOException {
            // Create original
            EObject original = createContainer("String Map Test");

            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) original.eGet(metadataRef);
            metadata.put("author", "John Doe");
            metadata.put("version", "1.0.0");
            metadata.put("license", "EPL-2.0");

            // Serialize
            String json = serialize(original);

            // Deserialize
            EObject restored = deserialize(json);

            // Verify
            assertNotNull(restored, "Restored object should not be null");

            @SuppressWarnings("unchecked")
            EMap<String, String> restoredMetadata = (EMap<String, String>) restored.eGet(metadataRef);

            assertEquals(3, restoredMetadata.size(), "Should have 3 metadata entries");
            assertEquals("John Doe", restoredMetadata.get("author"));
            assertEquals("1.0.0", restoredMetadata.get("version"));
            assertEquals("EPL-2.0", restoredMetadata.get("license"));
        }

        @Test
        @DisplayName("roundtrip multiple EMaps")
        void roundtripMultipleEMaps() throws IOException {
            // Create original with both EMaps populated
            EObject original = createContainer("Multi-Map Test");

            @SuppressWarnings("unchecked")
            EMap<String, EObject> items = (EMap<String, EObject>) original.eGet(itemsRef);
            items.put("itemA", createItem("A", 10, true));

            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) original.eGet(metadataRef);
            metadata.put("key1", "value1");

            // Serialize
            String json = serialize(original);

            // Deserialize
            EObject restored = deserialize(json);

            // Verify items
            @SuppressWarnings("unchecked")
            EMap<String, EObject> restoredItems = (EMap<String, EObject>) restored.eGet(itemsRef);
            assertEquals(1, restoredItems.size());
            assertNotNull(restoredItems.get("itemA"));

            // Verify metadata
            @SuppressWarnings("unchecked")
            EMap<String, String> restoredMetadata = (EMap<String, String>) restored.eGet(metadataRef);
            assertEquals(1, restoredMetadata.size());
            assertEquals("value1", restoredMetadata.get("key1"));
        }

        @Test
        @DisplayName("roundtrip empty EMap")
        void roundtripEmptyEMap() throws IOException {
            // Create original with empty EMaps
            EObject original = createContainer("Empty Map Test");

            // Serialize
            String json = serialize(original);

            // Deserialize
            EObject restored = deserialize(json);

            // Verify
            assertNotNull(restored);
            assertEquals("Empty Map Test", restored.eGet(containerClass.getEStructuralFeature("name")));

            @SuppressWarnings("unchecked")
            EMap<String, EObject> restoredItems = (EMap<String, EObject>) restored.eGet(itemsRef);
            assertTrue(restoredItems.isEmpty(), "Items should be empty");

            @SuppressWarnings("unchecked")
            EMap<String, String> restoredMetadata = (EMap<String, String>) restored.eGet(metadataRef);
            assertTrue(restoredMetadata.isEmpty(), "Metadata should be empty");
        }

        @Test
        @DisplayName("roundtrip keys with special characters")
        void roundtripKeysWithSpecialCharacters() throws IOException {
            EObject original = createContainer("Special Keys Test");

            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) original.eGet(metadataRef);
            metadata.put("key-with-dashes", "value1");
            metadata.put("key.with.dots", "value2");
            metadata.put("123", "numeric key");

            // Serialize and deserialize
            String json = serialize(original);
            EObject restored = deserialize(json);

            // Verify
            @SuppressWarnings("unchecked")
            EMap<String, String> restoredMetadata = (EMap<String, String>) restored.eGet(metadataRef);

            assertEquals(3, restoredMetadata.size());
            assertEquals("value1", restoredMetadata.get("key-with-dashes"));
            assertEquals("value2", restoredMetadata.get("key.with.dots"));
            assertEquals("numeric key", restoredMetadata.get("123"));
        }
    }
}
