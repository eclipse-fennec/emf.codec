/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
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
 * Tests for EMap deserialization in codec.v2.
 * <p>
 * EMF's EMap is serialized as a JSON object where:
 * <ul>
 *   <li>JSON field names become map entry keys</li>
 *   <li>JSON field values become map entry values</li>
 * </ul>
 * </p>
 * <p>
 * Example:
 * <pre>
 * {
 *   "items": {
 *     "first": { "id": "1", "value": 100 },
 *     "second": { "id": "2", "value": 200 }
 *   }
 * }
 * </pre>
 * Results in an EMap with entries:
 * <ul>
 *   <li>key="first", value=Item(id="1", value=100)</li>
 *   <li>key="second", value=Item(id="2", value=200)</li>
 * </ul>
 * </p>
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.EMapDeserializationTest}
 * </p>
 */
@DisplayName("EMap Deserialization Tests")
class EMapDeserializationTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-emap.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass containerClass;
    private EClass itemClass;
    private EClass itemEntryClass;
    private EClass stringEntryClass;

    // EReferences
    private EReference itemsRef;
    private EReference metadataRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(EMapDeserializationTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);

        // Register package in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load EClasses
        containerClass = ecoreHelper.getEClass(testPackage, "Container");
        itemClass = ecoreHelper.getEClass(testPackage, "Item");
        itemEntryClass = ecoreHelper.getEClass(testPackage, "ItemEntry");
        stringEntryClass = ecoreHelper.getEClass(testPackage, "StringEntry");

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
                URI.createURI("test://emap-test.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    private EObject deserialize(String json, Map<String, Object> options) throws IOException {
        CodecResource resource = createResource();
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private Map<String, Object> loadOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, containerClass);
        return options;
    }

    // ========================================================================
    // Basic EMap Deserialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Basic EMap Deserialization")
    class BasicEMapDeserialization {

        @Test
        @DisplayName("deserializes EMap with object values")
        void deserializesEMapWithObjectValues() throws IOException {
            String json = """
                {
                  "name": "Test Container",
                  "items": {
                    "first": { "id": "1", "value": 100, "active": true },
                    "second": { "id": "2", "value": 200, "active": false }
                  }
                }
                """;

            EObject container = deserialize(json, loadOptions());

            assertNotNull(container);
            assertEquals("Test Container", container.eGet(containerClass.getEStructuralFeature("name")));

            // Get items as EMap
            @SuppressWarnings("unchecked")
            EMap<String, EObject> items = (EMap<String, EObject>) container.eGet(itemsRef);

            assertNotNull(items, "items EMap should not be null");
            assertEquals(2, items.size(), "Should have 2 items");

            // Verify first item
            EObject firstItem = items.get("first");
            assertNotNull(firstItem, "Should have item with key 'first'");
            assertEquals(itemClass, firstItem.eClass(), "Item should be of type Item");
            assertEquals("1", firstItem.eGet(itemClass.getEStructuralFeature("id")));
            assertEquals(100, firstItem.eGet(itemClass.getEStructuralFeature("value")));
            assertEquals(true, firstItem.eGet(itemClass.getEStructuralFeature("active")));

            // Verify second item
            EObject secondItem = items.get("second");
            assertNotNull(secondItem, "Should have item with key 'second'");
            assertEquals("2", secondItem.eGet(itemClass.getEStructuralFeature("id")));
            assertEquals(200, secondItem.eGet(itemClass.getEStructuralFeature("value")));
            assertEquals(false, secondItem.eGet(itemClass.getEStructuralFeature("active")));
        }

        @Test
        @DisplayName("deserializes EMap with string values")
        void deserializesEMapWithStringValues() throws IOException {
            String json = """
                {
                  "name": "Metadata Container",
                  "metadata": {
                    "author": "John Doe",
                    "version": "1.0.0",
                    "license": "EPL-2.0"
                  }
                }
                """;

            EObject container = deserialize(json, loadOptions());

            assertNotNull(container);

            // Get metadata as EMap
            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) container.eGet(metadataRef);

            assertNotNull(metadata, "metadata EMap should not be null");
            assertEquals(3, metadata.size(), "Should have 3 metadata entries");

            assertEquals("John Doe", metadata.get("author"));
            assertEquals("1.0.0", metadata.get("version"));
            assertEquals("EPL-2.0", metadata.get("license"));
        }

        @Test
        @DisplayName("deserializes empty EMap")
        void deserializesEmptyEMap() throws IOException {
            String json = """
                {
                  "name": "Empty Container",
                  "items": {}
                }
                """;

            EObject container = deserialize(json, loadOptions());

            assertNotNull(container);

            @SuppressWarnings("unchecked")
            EMap<String, EObject> items = (EMap<String, EObject>) container.eGet(itemsRef);

            assertNotNull(items, "items EMap should exist");
            assertTrue(items.isEmpty(), "items EMap should be empty");
        }

        @Test
        @DisplayName("deserializes container without EMap field")
        void deserializesContainerWithoutEMapField() throws IOException {
            String json = """
                {
                  "name": "No Items Container"
                }
                """;

            EObject container = deserialize(json, loadOptions());

            assertNotNull(container);
            assertEquals("No Items Container", container.eGet(containerClass.getEStructuralFeature("name")));

            // EMap should be empty by default
            @SuppressWarnings("unchecked")
            EMap<String, EObject> items = (EMap<String, EObject>) container.eGet(itemsRef);

            assertTrue(items.isEmpty(), "items EMap should be empty when not in JSON");
        }
    }

    // ========================================================================
    // Multiple EMap Features Tests
    // ========================================================================

    @Nested
    @DisplayName("Multiple EMap Features")
    class MultipleEMapFeatures {

        @Test
        @DisplayName("deserializes multiple EMap features in same object")
        void deserializesMultipleEMapFeatures() throws IOException {
            String json = """
                {
                  "name": "Full Container",
                  "items": {
                    "item1": { "id": "A", "value": 10, "active": true }
                  },
                  "metadata": {
                    "key1": "value1",
                    "key2": "value2"
                  }
                }
                """;

            EObject container = deserialize(json, loadOptions());

            assertNotNull(container);

            @SuppressWarnings("unchecked")
            EMap<String, EObject> items = (EMap<String, EObject>) container.eGet(itemsRef);
            assertEquals(1, items.size());
            assertNotNull(items.get("item1"));

            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) container.eGet(metadataRef);
            assertEquals(2, metadata.size());
            assertEquals("value1", metadata.get("key1"));
            assertEquals("value2", metadata.get("key2"));
        }
    }

    // ========================================================================
    // Special Key Handling Tests
    // ========================================================================

    @Nested
    @DisplayName("Special Key Handling")
    class SpecialKeyHandling {

        @Test
        @DisplayName("handles keys with special characters")
        void handlesKeysWithSpecialCharacters() throws IOException {
            String json = """
                {
                  "name": "Special Keys",
                  "metadata": {
                    "key-with-dashes": "value1",
                    "key.with.dots": "value2",
                    "key_with_underscores": "value3"
                  }
                }
                """;

            EObject container = deserialize(json, loadOptions());

            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) container.eGet(metadataRef);

            assertEquals("value1", metadata.get("key-with-dashes"));
            assertEquals("value2", metadata.get("key.with.dots"));
            assertEquals("value3", metadata.get("key_with_underscores"));
        }

        @Test
        @DisplayName("handles numeric-looking keys as strings")
        void handlesNumericLookingKeysAsStrings() throws IOException {
            String json = """
                {
                  "name": "Numeric Keys",
                  "metadata": {
                    "123": "numeric key",
                    "456": "another numeric"
                  }
                }
                """;

            EObject container = deserialize(json, loadOptions());

            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) container.eGet(metadataRef);

            assertEquals("numeric key", metadata.get("123"));
            assertEquals("another numeric", metadata.get("456"));
        }
    }

    // ========================================================================
    // Map Entry Detection Tests
    // ========================================================================

    @Nested
    @DisplayName("Map Entry Detection")
    class MapEntryDetection {

        @Test
        @DisplayName("detects map entry class by instanceClassName")
        void detectsMapEntryClassByInstanceClassName() {
            // ItemEntry should be detected as a map entry class
            assertEquals("java.util.Map$Entry", itemEntryClass.getInstanceClassName());
            assertEquals("java.util.Map$Entry", stringEntryClass.getInstanceClassName());
        }

        @Test
        @DisplayName("map entry class has key and value features")
        void mapEntryClassHasKeyAndValueFeatures() {
            assertNotNull(itemEntryClass.getEStructuralFeature("key"));
            assertNotNull(itemEntryClass.getEStructuralFeature("value"));

            assertNotNull(stringEntryClass.getEStructuralFeature("key"));
            assertNotNull(stringEntryClass.getEStructuralFeature("value"));
        }
    }
}
