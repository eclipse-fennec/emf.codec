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
package org.eclipse.fennec.codec.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for CODEC_ROOT_TYPE hint-based type resolution.
 * <p>
 * When _type field is missing:
 * - Root object: Uses CODEC_ROOT_TYPE hint (EClass or URI String)
 * - Nested object: Uses EReference.eType as fallback
 * </p>
 */
@DisplayName("Type Resolution: Hint Strategy")
class TypeResolutionHintTest {

    private static final String TEST_ECORE = "test-hint.ecore";

    private MetadataWhiteboard metadataService;
    private EcoreHelper ecoreHelper;

    private EPackage testPackage;
    private EClass customerClass;
    private EClass productClass;
    private EClass orderClass;
    private EClass carClass;
    private EClass motorcycleClass;
    private EClass garageClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, TypeResolutionHintTest.class);

        // Register in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service with CodecAspectProvider
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        customerClass = EcoreHelper.getEClass(testPackage, "Customer");
        productClass = EcoreHelper.getEClass(testPackage, "Product");
        orderClass = EcoreHelper.getEClass(testPackage, "Order");
        carClass = EcoreHelper.getEClass(testPackage, "Car");
        motorcycleClass = EcoreHelper.getEClass(testPackage, "Motorcycle");
        garageClass = EcoreHelper.getEClass(testPackage, "Garage");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Nested
    @DisplayName("Root object with EClass hint")
    class RootWithEClassHint {

        @Test
        @DisplayName("resolves Customer from EClass hint when _type missing")
        void customerFromEClassHint() throws IOException {
            String json = """
                {
                    "name": "John Doe",
                    "email": "john@example.org"
                }
                """;

            EObject result = deserializeWithHint(json, customerClass);

            assertNotNull(result, "Should resolve from EClass hint");
            assertEquals(customerClass, result.eClass());
            assertEquals("John Doe", result.eGet(customerClass.getEStructuralFeature("name")));
            assertEquals("john@example.org", result.eGet(customerClass.getEStructuralFeature("email")));
        }

        @Test
        @DisplayName("resolves Product from EClass hint when _type missing")
        void productFromEClassHint() throws IOException {
            String json = """
                {
                    "sku": "PROD-001",
                    "price": 29.99
                }
                """;

            EObject result = deserializeWithHint(json, productClass);

            assertNotNull(result, "Should resolve from EClass hint");
            assertEquals(productClass, result.eClass());
            assertEquals("PROD-001", result.eGet(productClass.getEStructuralFeature("sku")));
            assertEquals(29.99, result.eGet(productClass.getEStructuralFeature("price")));
        }
    }

    @Nested
    @DisplayName("Root object with URI String hint")
    class RootWithUriStringHint {

        @Test
        @DisplayName("resolves Customer from URI string hint")
        void customerFromUriString() throws IOException {
            String json = """
                {
                    "name": "Jane Smith",
                    "email": "jane@example.org"
                }
                """;

            EObject result = deserializeWithHint(json, "http://test.example.org/hint/1.0#//Customer");

            assertNotNull(result, "Should resolve from URI string hint");
            assertEquals(customerClass, result.eClass());
            assertEquals("Jane Smith", result.eGet(customerClass.getEStructuralFeature("name")));
        }

        @Test
        @DisplayName("returns null for invalid URI string hint")
        void invalidUriStringReturnsNull() throws IOException {
            String json = """
                {
                    "name": "Ghost"
                }
                """;

            EObject result = deserializeWithHint(json, "http://nonexistent/1.0#//Unknown");

            assertNull(result, "Should return null for invalid URI hint");
        }
    }

    @Nested
    @DisplayName("Nested object resolution from EReference.eType")
    class NestedFromEReference {

        @Test
        @DisplayName("nested concrete type resolves without _type")
        void nestedConcreteTypeResolves() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/hint/1.0#//Order",
                    "orderId": "ORD-001",
                    "customer": {
                        "name": "Alice",
                        "email": "alice@example.org"
                    },
                    "items": [
                        {"sku": "ITEM-A", "price": 10.0},
                        {"sku": "ITEM-B", "price": 20.0}
                    ]
                }
                """;

            EObject result = deserialize(json);

            assertNotNull(result, "Should deserialize order");
            assertEquals(orderClass, result.eClass());

            // Customer resolved from EReference.eType
            EObject customer = (EObject) result.eGet(orderClass.getEStructuralFeature("customer"));
            assertNotNull(customer, "Should have customer");
            assertEquals(customerClass, customer.eClass());
            assertEquals("Alice", customer.eGet(customerClass.getEStructuralFeature("name")));

            // Products resolved from EReference.eType
            @SuppressWarnings("unchecked")
            List<EObject> items = (List<EObject>) result.eGet(orderClass.getEStructuralFeature("items"));
            assertEquals(2, items.size());
            assertEquals(productClass, items.get(0).eClass());
            assertEquals(productClass, items.get(1).eClass());
        }

        @Test
        @DisplayName("nested abstract type requires _type for concrete class")
        void nestedAbstractTypeRequiresType() throws IOException {
            String json = """
                {
                    "_type": "http://test.example.org/hint/1.0#//Garage",
                    "name": "My Garage",
                    "vehicles": [
                        {
                            "_type": "http://test.example.org/hint/1.0#//Car",
                            "brand": "Toyota",
                            "doors": 4
                        },
                        {
                            "_type": "http://test.example.org/hint/1.0#//Motorcycle",
                            "brand": "Honda",
                            "engineCC": 750
                        }
                    ]
                }
                """;

            EObject result = deserialize(json);

            assertNotNull(result, "Should deserialize garage");
            assertEquals(garageClass, result.eClass());

            @SuppressWarnings("unchecked")
            List<EObject> vehicles = (List<EObject>) result.eGet(garageClass.getEStructuralFeature("vehicles"));
            assertEquals(2, vehicles.size());

            // First is Car
            assertEquals(carClass, vehicles.get(0).eClass());
            assertEquals("Toyota", vehicles.get(0).eGet(carClass.getEStructuralFeature("brand")));
            assertEquals(4, vehicles.get(0).eGet(carClass.getEStructuralFeature("doors")));

            // Second is Motorcycle
            assertEquals(motorcycleClass, vehicles.get(1).eClass());
            assertEquals("Honda", vehicles.get(1).eGet(motorcycleClass.getEStructuralFeature("brand")));
            assertEquals(750, vehicles.get(1).eGet(motorcycleClass.getEStructuralFeature("engineCC")));
        }
    }

    @Nested
    @DisplayName("No hint scenarios")
    class NoHintScenarios {

        @Test
        @DisplayName("root object without _type or hint returns null")
        void rootWithoutTypeOrHintReturnsNull() throws IOException {
            String json = """
                {
                    "name": "Orphan",
                    "email": "orphan@example.org"
                }
                """;

            EObject result = deserialize(json);

            assertNull(result, "Should return null without _type or hint");
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://hint-test.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    private EObject deserialize(String json) throws IOException {
        CodecResource resource = createResource();
        Map<String, Object> options = new HashMap<>();

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private EObject deserializeWithHint(String json, EClass hint) throws IOException {
        CodecResource resource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, hint);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private EObject deserializeWithHint(String json, String uriHint) throws IOException {
        CodecResource resource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, uriHint);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
