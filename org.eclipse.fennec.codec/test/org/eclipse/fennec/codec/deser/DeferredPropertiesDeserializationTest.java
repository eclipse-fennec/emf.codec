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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Systematic tests for deferred properties deserialization.
 * <p>
 * When properties appear before the _type field in JSON, they are stored as
 * raw Java objects (Map, List, primitives) and replayed after type resolution.
 * This test class verifies that all JSON structure types are correctly handled
 * during replay.
 * </p>
 * <p>
 * Test scenarios:
 * <ul>
 *   <li>Primitive values deferred</li>
 *   <li>Primitive arrays deferred</li>
 *   <li>Nested objects deferred</li>
 *   <li>Arrays of objects deferred</li>
 *   <li>Deep nesting (arrays containing objects containing arrays)</li>
 *   <li>Matrix-like structures (arrays of arrays)</li>
 * </ul>
 * </p>
 *
 * @see CodecEObjectDeserializer#replayDeferredValue
 * @see CodecEObjectDeserializer#writeValueToBuffer
 */
@DisplayName("Deferred Properties Deserialization Tests")
class DeferredPropertiesDeserializationTest {

    private static final String TEST_ECORE = "test-deferred.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass containerClass;
    private EClass addressClass;
    private EClass tagsClass;
    private EClass itemClass;
    private EClass categoryClass;
    private EClass cellClass;
    private EClass rowClass;
    private EClass matrixClass;

    // Container features
    private EAttribute idAttr;
    private EAttribute nameAttr;
    private EAttribute countAttr;
    private EAttribute activeAttr;
    private EAttribute labelsAttr;
    private EAttribute scoresAttr;
    private EReference addressRef;
    private EReference tagsRef;
    private EReference itemsRef;
    private EReference categoriesRef;
    private EReference matrixRef;

    // Address features
    private EAttribute streetAttr;
    private EAttribute cityAttr;
    private EAttribute zipCodeAttr;

    // Tags features
    private EAttribute valuesAttr;

    // Item features
    private EAttribute itemNameAttr;
    private EAttribute quantityAttr;
    private EAttribute priceAttr;

    // Category features
    private EAttribute categoryNameAttr;
    private EReference categoryItemsRef;

    // Cell features
    private EAttribute cellValueAttr;

    // Row features
    private EReference cellsRef;

    // Matrix features
    private EAttribute matrixNameAttr;
    private EReference rowsRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(DeferredPropertiesDeserializationTest.class);
        testPackage = ecoreHelper.loadEcore(TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load EClasses
        containerClass = ecoreHelper.getEClass(testPackage, "Container");
        addressClass = ecoreHelper.getEClass(testPackage, "Address");
        tagsClass = ecoreHelper.getEClass(testPackage, "Tags");
        itemClass = ecoreHelper.getEClass(testPackage, "Item");
        categoryClass = ecoreHelper.getEClass(testPackage, "Category");
        cellClass = ecoreHelper.getEClass(testPackage, "Cell");
        rowClass = ecoreHelper.getEClass(testPackage, "Row");
        matrixClass = ecoreHelper.getEClass(testPackage, "Matrix");

        // Container features
        idAttr = (EAttribute) ecoreHelper.getFeature(containerClass, "id");
        nameAttr = (EAttribute) ecoreHelper.getFeature(containerClass, "name");
        countAttr = (EAttribute) ecoreHelper.getFeature(containerClass, "count");
        activeAttr = (EAttribute) ecoreHelper.getFeature(containerClass, "active");
        labelsAttr = (EAttribute) ecoreHelper.getFeature(containerClass, "labels");
        scoresAttr = (EAttribute) ecoreHelper.getFeature(containerClass, "scores");
        addressRef = (EReference) ecoreHelper.getFeature(containerClass, "address");
        tagsRef = (EReference) ecoreHelper.getFeature(containerClass, "tags");
        itemsRef = (EReference) ecoreHelper.getFeature(containerClass, "items");
        categoriesRef = (EReference) ecoreHelper.getFeature(containerClass, "categories");
        matrixRef = (EReference) ecoreHelper.getFeature(containerClass, "matrix");

        // Address features
        streetAttr = (EAttribute) ecoreHelper.getFeature(addressClass, "street");
        cityAttr = (EAttribute) ecoreHelper.getFeature(addressClass, "city");
        zipCodeAttr = (EAttribute) ecoreHelper.getFeature(addressClass, "zipCode");

        // Tags features
        valuesAttr = (EAttribute) ecoreHelper.getFeature(tagsClass, "values");

        // Item features
        itemNameAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "name");
        quantityAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "quantity");
        priceAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "price");

        // Category features
        categoryNameAttr = (EAttribute) ecoreHelper.getFeature(categoryClass, "name");
        categoryItemsRef = (EReference) ecoreHelper.getFeature(categoryClass, "items");

        // Cell features
        cellValueAttr = (EAttribute) ecoreHelper.getFeature(cellClass, "value");

        // Row features
        cellsRef = (EReference) ecoreHelper.getFeature(rowClass, "cells");

        // Matrix features
        matrixNameAttr = (EAttribute) ecoreHelper.getFeature(matrixClass, "name");
        rowsRef = (EReference) ecoreHelper.getFeature(matrixClass, "rows");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject loadJson(String json) throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();
        CodecResource resource = new CodecResource(
                URI.createURI("test://deferred.json"),
                metadataService,
                resolver,
                null);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, containerClass);

        try (var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            resource.load(is, options);
        }

        assertTrue(resource.getErrors().isEmpty(), "Should have no errors: " + resource.getErrors());
        assertEquals(1, resource.getContents().size(), "Should have exactly one root object");

        return resource.getContents().get(0);
    }

    @Nested
    @DisplayName("Primitive Values Deferred")
    class PrimitiveValuesDeferred {

        @Test
        @DisplayName("string before _type")
        void stringBeforeType() throws IOException {
            String json = """
                {
                    "name": "Test Container",
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            assertEquals("Test Container", result.eGet(nameAttr));
        }

        @Test
        @DisplayName("integer before _type")
        void integerBeforeType() throws IOException {
            String json = """
                {
                    "count": 42,
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            assertEquals(42, result.eGet(countAttr));
        }

        @Test
        @DisplayName("boolean before _type")
        void booleanBeforeType() throws IOException {
            String json = """
                {
                    "active": true,
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            assertEquals(true, result.eGet(activeAttr));
        }

        @Test
        @DisplayName("multiple primitives before _type")
        void multiplePrimitivesBeforeType() throws IOException {
            String json = """
                {
                    "id": "C001",
                    "name": "Test",
                    "count": 100,
                    "active": false,
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            assertEquals("C001", result.eGet(idAttr));
            assertEquals("Test", result.eGet(nameAttr));
            assertEquals(100, result.eGet(countAttr));
            assertEquals(false, result.eGet(activeAttr));
        }
    }

    @Nested
    @DisplayName("Primitive Arrays Deferred")
    class PrimitiveArraysDeferred {

        @Test
        @DisplayName("string array before _type")
        @SuppressWarnings("unchecked")
        void stringArrayBeforeType() throws IOException {
            String json = """
                {
                    "labels": ["alpha", "beta", "gamma"],
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EList<String> labels = (EList<String>) result.eGet(labelsAttr);
            assertEquals(3, labels.size());
            assertEquals("alpha", labels.get(0));
            assertEquals("beta", labels.get(1));
            assertEquals("gamma", labels.get(2));
        }

        @Test
        @DisplayName("double array before _type")
        @SuppressWarnings("unchecked")
        void doubleArrayBeforeType() throws IOException {
            String json = """
                {
                    "scores": [1.5, 2.5, 3.5],
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EList<Double> scores = (EList<Double>) result.eGet(scoresAttr);
            assertEquals(3, scores.size());
            assertEquals(1.5, scores.get(0));
            assertEquals(2.5, scores.get(1));
            assertEquals(3.5, scores.get(2));
        }

        @Test
        @DisplayName("empty array before _type")
        @SuppressWarnings("unchecked")
        void emptyArrayBeforeType() throws IOException {
            String json = """
                {
                    "labels": [],
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EList<String> labels = (EList<String>) result.eGet(labelsAttr);
            assertTrue(labels.isEmpty());
        }
    }

    @Nested
    @DisplayName("Nested Objects Deferred")
    class NestedObjectsDeferred {

        @Test
        @DisplayName("single nested object before _type")
        void singleNestedObjectBeforeType() throws IOException {
            String json = """
                {
                    "address": {
                        "street": "123 Main St",
                        "city": "Springfield",
                        "zipCode": "12345"
                    },
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EObject address = (EObject) result.eGet(addressRef);

            assertNotNull(address);
            assertEquals("123 Main St", address.eGet(streetAttr));
            assertEquals("Springfield", address.eGet(cityAttr));
            assertEquals("12345", address.eGet(zipCodeAttr));
        }

        @Test
        @DisplayName("nested object with primitive array before _type")
        @SuppressWarnings("unchecked")
        void nestedObjectWithArrayBeforeType() throws IOException {
            String json = """
                {
                    "tags": {
                        "values": ["tag1", "tag2", "tag3"]
                    },
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EObject tags = (EObject) result.eGet(tagsRef);

            assertNotNull(tags);
            EList<String> values = (EList<String>) tags.eGet(valuesAttr);
            assertEquals(3, values.size());
            assertEquals("tag1", values.get(0));
            assertEquals("tag2", values.get(1));
            assertEquals("tag3", values.get(2));
        }

        @Test
        @DisplayName("null nested object before _type")
        void nullNestedObjectBeforeType() throws IOException {
            String json = """
                {
                    "address": null,
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            assertNull(result.eGet(addressRef));
        }
    }

    @Nested
    @DisplayName("Arrays of Objects Deferred")
    class ArraysOfObjectsDeferred {

        @Test
        @DisplayName("array of objects before _type")
        @SuppressWarnings("unchecked")
        void arrayOfObjectsBeforeType() throws IOException {
            String json = """
                {
                    "items": [
                        {"name": "Item 1", "quantity": 10, "price": 9.99},
                        {"name": "Item 2", "quantity": 5, "price": 19.99},
                        {"name": "Item 3", "quantity": 20, "price": 4.99}
                    ],
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EList<EObject> items = (EList<EObject>) result.eGet(itemsRef);

            assertEquals(3, items.size());

            EObject item1 = items.get(0);
            assertEquals("Item 1", item1.eGet(itemNameAttr));
            assertEquals(10, item1.eGet(quantityAttr));
            assertEquals(9.99, item1.eGet(priceAttr));

            EObject item2 = items.get(1);
            assertEquals("Item 2", item2.eGet(itemNameAttr));
            assertEquals(5, item2.eGet(quantityAttr));
            assertEquals(19.99, item2.eGet(priceAttr));

            EObject item3 = items.get(2);
            assertEquals("Item 3", item3.eGet(itemNameAttr));
            assertEquals(20, item3.eGet(quantityAttr));
            assertEquals(4.99, item3.eGet(priceAttr));
        }

        @Test
        @DisplayName("single item array before _type")
        @SuppressWarnings("unchecked")
        void singleItemArrayBeforeType() throws IOException {
            String json = """
                {
                    "items": [
                        {"name": "Solo Item", "quantity": 1, "price": 99.99}
                    ],
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EList<EObject> items = (EList<EObject>) result.eGet(itemsRef);

            assertEquals(1, items.size());
            assertEquals("Solo Item", items.get(0).eGet(itemNameAttr));
        }

        @Test
        @DisplayName("empty object array before _type")
        @SuppressWarnings("unchecked")
        void emptyObjectArrayBeforeType() throws IOException {
            String json = """
                {
                    "items": [],
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EList<EObject> items = (EList<EObject>) result.eGet(itemsRef);
            assertTrue(items.isEmpty());
        }
    }

    @Nested
    @DisplayName("Deep Nesting Deferred")
    class DeepNestingDeferred {

        @Test
        @DisplayName("categories with nested items before _type")
        @SuppressWarnings("unchecked")
        void categoriesWithNestedItemsBeforeType() throws IOException {
            String json = """
                {
                    "categories": [
                        {
                            "name": "Electronics",
                            "items": [
                                {"name": "Phone", "quantity": 50, "price": 599.99},
                                {"name": "Laptop", "quantity": 20, "price": 1299.99}
                            ]
                        },
                        {
                            "name": "Books",
                            "items": [
                                {"name": "Java Guide", "quantity": 100, "price": 39.99}
                            ]
                        }
                    ],
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EList<EObject> categories = (EList<EObject>) result.eGet(categoriesRef);

            assertEquals(2, categories.size());

            // Electronics category
            EObject electronics = categories.get(0);
            assertEquals("Electronics", electronics.eGet(categoryNameAttr));
            EList<EObject> electronicsItems = (EList<EObject>) electronics.eGet(categoryItemsRef);
            assertEquals(2, electronicsItems.size());
            assertEquals("Phone", electronicsItems.get(0).eGet(itemNameAttr));
            assertEquals(599.99, electronicsItems.get(0).eGet(priceAttr));
            assertEquals("Laptop", electronicsItems.get(1).eGet(itemNameAttr));

            // Books category
            EObject books = categories.get(1);
            assertEquals("Books", books.eGet(categoryNameAttr));
            EList<EObject> booksItems = (EList<EObject>) books.eGet(categoryItemsRef);
            assertEquals(1, booksItems.size());
            assertEquals("Java Guide", booksItems.get(0).eGet(itemNameAttr));
        }

        @Test
        @DisplayName("empty nested array before _type")
        @SuppressWarnings("unchecked")
        void emptyNestedArrayBeforeType() throws IOException {
            String json = """
                {
                    "categories": [
                        {
                            "name": "Empty Category",
                            "items": []
                        }
                    ],
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EList<EObject> categories = (EList<EObject>) result.eGet(categoriesRef);

            assertEquals(1, categories.size());
            EObject category = categories.get(0);
            assertEquals("Empty Category", category.eGet(categoryNameAttr));
            EList<EObject> items = (EList<EObject>) category.eGet(categoryItemsRef);
            assertTrue(items.isEmpty());
        }
    }

    @Nested
    @DisplayName("Matrix Structure Deferred")
    class MatrixStructureDeferred {

        @Test
        @DisplayName("matrix with rows and cells before _type")
        @SuppressWarnings("unchecked")
        void matrixBeforeType() throws IOException {
            String json = """
                {
                    "matrix": {
                        "name": "Identity",
                        "rows": [
                            {"cells": [{"value": 1}, {"value": 0}, {"value": 0}]},
                            {"cells": [{"value": 0}, {"value": 1}, {"value": 0}]},
                            {"cells": [{"value": 0}, {"value": 0}, {"value": 1}]}
                        ]
                    },
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            EObject matrix = (EObject) result.eGet(matrixRef);

            assertNotNull(matrix);
            assertEquals("Identity", matrix.eGet(matrixNameAttr));

            EList<EObject> rows = (EList<EObject>) matrix.eGet(rowsRef);
            assertEquals(3, rows.size());

            // Check first row: [1, 0, 0]
            EList<EObject> row0Cells = (EList<EObject>) rows.get(0).eGet(cellsRef);
            assertEquals(3, row0Cells.size());
            assertEquals(1, row0Cells.get(0).eGet(cellValueAttr));
            assertEquals(0, row0Cells.get(1).eGet(cellValueAttr));
            assertEquals(0, row0Cells.get(2).eGet(cellValueAttr));

            // Check second row: [0, 1, 0]
            EList<EObject> row1Cells = (EList<EObject>) rows.get(1).eGet(cellsRef);
            assertEquals(3, row1Cells.size());
            assertEquals(0, row1Cells.get(0).eGet(cellValueAttr));
            assertEquals(1, row1Cells.get(1).eGet(cellValueAttr));
            assertEquals(0, row1Cells.get(2).eGet(cellValueAttr));

            // Check third row: [0, 0, 1]
            EList<EObject> row2Cells = (EList<EObject>) rows.get(2).eGet(cellsRef);
            assertEquals(3, row2Cells.size());
            assertEquals(0, row2Cells.get(0).eGet(cellValueAttr));
            assertEquals(0, row2Cells.get(1).eGet(cellValueAttr));
            assertEquals(1, row2Cells.get(2).eGet(cellValueAttr));
        }
    }

    @Nested
    @DisplayName("Mixed Scenarios")
    class MixedScenarios {

        @Test
        @DisplayName("all property types before _type")
        @SuppressWarnings("unchecked")
        void allPropertyTypesBeforeType() throws IOException {
            String json = """
                {
                    "id": "MIX001",
                    "name": "Mixed Container",
                    "count": 999,
                    "active": true,
                    "labels": ["L1", "L2"],
                    "scores": [1.1, 2.2],
                    "address": {
                        "street": "456 Oak Ave",
                        "city": "Metropolis",
                        "zipCode": "67890"
                    },
                    "items": [
                        {"name": "Widget", "quantity": 3, "price": 12.50}
                    ],
                    "categories": [
                        {
                            "name": "Misc",
                            "items": [
                                {"name": "Thing", "quantity": 1, "price": 5.00}
                            ]
                        }
                    ],
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);

            // Primitives
            assertEquals("MIX001", result.eGet(idAttr));
            assertEquals("Mixed Container", result.eGet(nameAttr));
            assertEquals(999, result.eGet(countAttr));
            assertEquals(true, result.eGet(activeAttr));

            // Primitive arrays
            EList<String> labels = (EList<String>) result.eGet(labelsAttr);
            assertEquals(2, labels.size());
            EList<Double> scores = (EList<Double>) result.eGet(scoresAttr);
            assertEquals(2, scores.size());

            // Nested object
            EObject address = (EObject) result.eGet(addressRef);
            assertNotNull(address);
            assertEquals("456 Oak Ave", address.eGet(streetAttr));

            // Array of objects
            EList<EObject> items = (EList<EObject>) result.eGet(itemsRef);
            assertEquals(1, items.size());
            assertEquals("Widget", items.get(0).eGet(itemNameAttr));

            // Deep nesting
            EList<EObject> categories = (EList<EObject>) result.eGet(categoriesRef);
            assertEquals(1, categories.size());
            assertEquals("Misc", categories.get(0).eGet(categoryNameAttr));
        }

        @Test
        @DisplayName("_type in the middle of properties")
        @SuppressWarnings("unchecked")
        void typeInMiddle() throws IOException {
            String json = """
                {
                    "id": "MIDDLE",
                    "items": [
                        {"name": "Before", "quantity": 1, "price": 1.00}
                    ],
                    "_type": "http://test.fennec/deferred#//Container",
                    "name": "After Type",
                    "categories": [
                        {
                            "name": "Post",
                            "items": [
                                {"name": "After", "quantity": 2, "price": 2.00}
                            ]
                        }
                    ]
                }
                """;

            EObject result = loadJson(json);

            // Before _type (deferred)
            assertEquals("MIDDLE", result.eGet(idAttr));
            EList<EObject> items = (EList<EObject>) result.eGet(itemsRef);
            assertEquals(1, items.size());
            assertEquals("Before", items.get(0).eGet(itemNameAttr));

            // After _type (direct)
            assertEquals("After Type", result.eGet(nameAttr));
            EList<EObject> categories = (EList<EObject>) result.eGet(categoriesRef);
            assertEquals(1, categories.size());
            assertEquals("Post", categories.get(0).eGet(categoryNameAttr));
        }

        @Test
        @DisplayName("_type at the end")
        void typeAtEnd() throws IOException {
            String json = """
                {
                    "id": "END001",
                    "name": "Type At End",
                    "count": 42,
                    "address": {
                        "street": "Last St",
                        "city": "Finale",
                        "zipCode": "99999"
                    },
                    "_type": "http://test.fennec/deferred#//Container"
                }
                """;

            EObject result = loadJson(json);
            assertEquals("END001", result.eGet(idAttr));
            assertEquals("Type At End", result.eGet(nameAttr));
            assertEquals(42, result.eGet(countAttr));

            EObject address = (EObject) result.eGet(addressRef);
            assertNotNull(address);
            assertEquals("Last St", address.eGet(streetAttr));
        }
    }
}
