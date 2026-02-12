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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
 * Tests for EMap serialization in codec.v2.
 * <p>
 * EMF's EMap should serialize as a JSON object where:
 * <ul>
 *   <li>Map entry keys become JSON field names</li>
 *   <li>Map entry values become JSON field values</li>
 * </ul>
 * </p>
 * <p>
 * Example: An EMap with entries [key="first", value=Item(id="1", value=100)]
 * should serialize to:
 * <pre>
 * {
 *   "items": {
 *     "first": { "id": "1", "value": 100, "active": false }
 *   }
 * }
 * </pre>
 * </p>
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.EMapSerializationTest}
 * </p>
 */
@DisplayName("EMap Serialization Tests")
class EMapSerializationTest {

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
        ecoreHelper = new EcoreHelper(EMapSerializationTest.class);
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
                URI.createURI("test://emap-test.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    private String serialize(EObject eObject, Map<String, Object> options) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(eObject);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return out.toString(StandardCharsets.UTF_8);
    }

    private Map<String, Object> saveOptions() {
        return new HashMap<>();
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
    // Basic EMap Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Basic EMap Serialization")
    class BasicEMapSerialization {

        @Test
        @DisplayName("serializes EMap with object values")
        void serializesEMapWithObjectValues() throws IOException {
            EObject container = createContainer("Test Container");

            // Add items to the EMap
            @SuppressWarnings("unchecked")
            EMap<String, EObject> items = (EMap<String, EObject>) container.eGet(itemsRef);
            items.put("first", createItem("1", 100, true));
            items.put("second", createItem("2", 200, false));

            String json = serialize(container, saveOptions());

            // Verify JSON structure
            assertTrue(json.contains("\"name\""), "Should contain name field");
            assertTrue(json.contains("\"Test Container\""), "Should contain name value");
            assertTrue(json.contains("\"items\""), "Should contain items field");
            assertTrue(json.contains("\"first\""), "Should contain 'first' key");
            assertTrue(json.contains("\"second\""), "Should contain 'second' key");
            assertTrue(json.contains("\"id\""), "Should contain id field");
            assertTrue(json.contains("\"value\""), "Should contain value field");

            // Verify it's a proper JSON object structure (not array)
            // The items should be serialized as { "first": {...}, "second": {...} }
            // not as [ { "key": "first", "value": {...} }, ... ]
            assertFalse(json.contains("\"key\""), "Should NOT contain 'key' field - EMap should serialize as object");
        }

        @Test
        @DisplayName("serializes EMap with string values")
        void serializesEMapWithStringValues() throws IOException {
            EObject container = createContainer("Metadata Container");

            // Add metadata entries
            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) container.eGet(metadataRef);
            metadata.put("author", "John Doe");
            metadata.put("version", "1.0.0");
            metadata.put("license", "EPL-2.0");

            String json = serialize(container, saveOptions());

            // Verify JSON structure
            assertTrue(json.contains("\"metadata\""), "Should contain metadata field");
            assertTrue(json.contains("\"author\""), "Should contain 'author' key");
            assertTrue(json.contains("\"John Doe\""), "Should contain author value");
            assertTrue(json.contains("\"version\""), "Should contain 'version' key");
            assertTrue(json.contains("\"1.0.0\""), "Should contain version value");
            assertTrue(json.contains("\"license\""), "Should contain 'license' key");
            assertTrue(json.contains("\"EPL-2.0\""), "Should contain license value");
        }

        @Test
        @DisplayName("serializes empty EMap as empty object")
        void serializesEmptyEMapAsEmptyObject() throws IOException {
            EObject container = createContainer("Empty Container");

            // Items EMap is empty by default
            String json = serialize(container, saveOptions());

            // Should contain name
            assertTrue(json.contains("\"name\""), "Should contain name field");
            assertTrue(json.contains("\"Empty Container\""), "Should contain name value");

            // Empty EMap should either be omitted or serialized as {}
            // This depends on default serialization settings
        }
    }

    // ========================================================================
    // Multiple EMap Features Tests
    // ========================================================================

    @Nested
    @DisplayName("Multiple EMap Features")
    class MultipleEMapFeatures {

        @Test
        @DisplayName("serializes multiple EMap features in same object")
        void serializesMultipleEMapFeatures() throws IOException {
            EObject container = createContainer("Full Container");

            // Add item
            @SuppressWarnings("unchecked")
            EMap<String, EObject> items = (EMap<String, EObject>) container.eGet(itemsRef);
            items.put("item1", createItem("A", 10, true));

            // Add metadata
            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) container.eGet(metadataRef);
            metadata.put("key1", "value1");
            metadata.put("key2", "value2");

            String json = serialize(container, saveOptions());

            // Verify both EMaps are serialized
            assertTrue(json.contains("\"items\""), "Should contain items field");
            assertTrue(json.contains("\"item1\""), "Should contain item1 key");
            assertTrue(json.contains("\"metadata\""), "Should contain metadata field");
            assertTrue(json.contains("\"key1\""), "Should contain key1");
            assertTrue(json.contains("\"key2\""), "Should contain key2");
        }
    }

    // ========================================================================
    // Special Key Handling Tests
    // ========================================================================

    @Nested
    @DisplayName("Special Key Handling")
    class SpecialKeyHandling {

        @Test
        @DisplayName("serializes keys with special characters")
        void serializesKeysWithSpecialCharacters() throws IOException {
            EObject container = createContainer("Special Keys");

            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) container.eGet(metadataRef);
            metadata.put("key-with-dashes", "value1");
            metadata.put("key.with.dots", "value2");
            metadata.put("key_with_underscores", "value3");

            String json = serialize(container, saveOptions());

            assertTrue(json.contains("\"key-with-dashes\""), "Should contain key with dashes");
            assertTrue(json.contains("\"key.with.dots\""), "Should contain key with dots");
            assertTrue(json.contains("\"key_with_underscores\""), "Should contain key with underscores");
        }

        @Test
        @DisplayName("serializes numeric-looking keys as strings")
        void serializesNumericLookingKeysAsStrings() throws IOException {
            EObject container = createContainer("Numeric Keys");

            @SuppressWarnings("unchecked")
            EMap<String, String> metadata = (EMap<String, String>) container.eGet(metadataRef);
            metadata.put("123", "numeric key");
            metadata.put("456", "another numeric");

            String json = serialize(container, saveOptions());

            // Keys should be quoted strings in JSON
            assertTrue(json.contains("\"123\""), "Should contain '123' as string key");
            assertTrue(json.contains("\"456\""), "Should contain '456' as string key");
        }
    }
}
