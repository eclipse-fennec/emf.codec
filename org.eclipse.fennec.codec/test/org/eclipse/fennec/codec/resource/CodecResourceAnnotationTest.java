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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.IdStrategy;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for EAnnotation-based codec configuration.
 * <p>
 * These tests verify that EAnnotations in the Ecore model are correctly
 * parsed and used during serialization/deserialization.
 * </p>
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.CodecResourceAnnotationTest}
 * </p>
 */
@DisplayName("CodecResource EAnnotation Configuration Tests")
class CodecResourceAnnotationTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-annotated.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass productClass;
    private EClass orderClass;
    private EClass orderItemClass;
    private EClass customerClass;
    private EClass vipCustomerClass;

    // Product attributes
    private EAttribute skuAttribute;
    private EAttribute productNameAttribute;
    private EAttribute priceAttribute;
    private EAttribute internalCodeAttribute;

    // Order attributes
    private EAttribute customerIdAttribute;
    private EAttribute orderDateAttribute;
    private EAttribute totalAttribute;
    private EReference itemsRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(CodecResourceAnnotationTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);

        // Register package in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service with CodecAspectProvider
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load EClasses
        productClass = ecoreHelper.getEClass(testPackage, "Product");
        orderClass = ecoreHelper.getEClass(testPackage, "Order");
        orderItemClass = ecoreHelper.getEClass(testPackage, "OrderItem");
        customerClass = ecoreHelper.getEClass(testPackage, "Customer");
        vipCustomerClass = ecoreHelper.getEClass(testPackage, "VIPCustomer");

        // Load Product attributes
        skuAttribute = (EAttribute) ecoreHelper.getFeature(productClass, "sku");
        productNameAttribute = (EAttribute) ecoreHelper.getFeature(productClass, "name");
        priceAttribute = (EAttribute) ecoreHelper.getFeature(productClass, "price");
        internalCodeAttribute = (EAttribute) ecoreHelper.getFeature(productClass, "internalCode");

        // Load Order attributes
        customerIdAttribute = (EAttribute) ecoreHelper.getFeature(orderClass, "customerId");
        orderDateAttribute = (EAttribute) ecoreHelper.getFeature(orderClass, "orderDate");
        totalAttribute = (EAttribute) ecoreHelper.getFeature(orderClass, "total");
        itemsRef = (EReference) ecoreHelper.getFeature(orderClass, "items");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createProduct() {
        return testPackage.getEFactoryInstance().create(productClass);
    }

    private EObject createOrder() {
        return testPackage.getEFactoryInstance().create(orderClass);
    }

    private EObject createOrderItem() {
        return testPackage.getEFactoryInstance().create(orderItemClass);
    }

    private EObject createVIPCustomer() {
        return testPackage.getEFactoryInstance().create(vipCustomerClass);
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://annotated.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    // ========================================================================
    // Aspect Parsing Tests - verify annotations are correctly parsed
    // ========================================================================

    @Nested
    @DisplayName("Aspect Parsing")
    class AspectParsing {

        @Test
        @DisplayName("parses ID configuration from codec.id annotation")
        void parsesIdConfiguration() {
            ClassMetadata metadata = metadataService.getClassMetadata(productClass);
            assertNotNull(metadata, "ClassMetadata should exist");

            ClassCodecAspect aspect = metadata.getAspects().stream()
                    .filter(ClassCodecAspect.class::isInstance)
                    .map(ClassCodecAspect.class::cast)
                    .findFirst()
                    .orElse(null);

            assertNotNull(aspect, "ClassCodecAspect should exist");
            assertNotNull(aspect.getIdConfig(), "IdConfig should exist");
            assertEquals(IdStrategy.ID_FIELD, aspect.getIdConfig().getStrategy());
            assertEquals("id", aspect.getIdConfig().getIdKey());
        }

        @Test
        @DisplayName("parses type configuration from codec.type annotation")
        void parsesTypeConfiguration() {
            ClassMetadata metadata = metadataService.getClassMetadata(productClass);
            ClassCodecAspect aspect = metadata.getAspects().stream()
                    .filter(ClassCodecAspect.class::isInstance)
                    .map(ClassCodecAspect.class::cast)
                    .findFirst()
                    .orElse(null);

            assertNotNull(aspect, "ClassCodecAspect should exist");
            assertNotNull(aspect.getTypeConfig(), "TypeConfig should exist");
            assertEquals(TypeStrategy.NAME, aspect.getTypeConfig().getStrategy());
            assertEquals("type", aspect.getTypeConfig().getTypeKey());
        }

        @Test
        @DisplayName("parses combined ID configuration from Order class")
        void parsesCombinedIdConfiguration() {
            ClassMetadata metadata = metadataService.getClassMetadata(orderClass);
            ClassCodecAspect aspect = metadata.getAspects().stream()
                    .filter(ClassCodecAspect.class::isInstance)
                    .map(ClassCodecAspect.class::cast)
                    .findFirst()
                    .orElse(null);

            assertNotNull(aspect, "ClassCodecAspect should exist");
            assertNotNull(aspect.getIdConfig(), "IdConfig should exist");
            assertEquals(IdStrategy.COMBINED, aspect.getIdConfig().getStrategy());
            assertEquals("orderId", aspect.getIdConfig().getIdKey());
            assertEquals("-", aspect.getIdConfig().getSeparator());

            List<String> idFeatures = aspect.getIdConfig().getIdFeatures();
            assertEquals(2, idFeatures.size());
            assertTrue(idFeatures.contains("customerId"));
            assertTrue(idFeatures.contains("orderDate"));
        }

        @Test
        @DisplayName("parses transient annotation on feature")
        void parsesTransientAnnotation() {
            FeatureMetadata metadata = metadataService.getFeatureMetadata(internalCodeAttribute);
            assertNotNull(metadata, "FeatureMetadata should exist");

            FeatureCodecAspect aspect = metadata.getAspects().stream()
                    .filter(FeatureCodecAspect.class::isInstance)
                    .map(FeatureCodecAspect.class::cast)
                    .findFirst()
                    .orElse(null);

            assertNotNull(aspect, "FeatureCodecAspect should exist");
            assertTrue(aspect.isIgnore(), "Transient feature should have ignore=true");
        }

        @Test
        @DisplayName("parses supertype configuration from Customer class")
        void parsesSuperTypeConfiguration() {
            ClassMetadata metadata = metadataService.getClassMetadata(customerClass);
            ClassCodecAspect aspect = metadata.getAspects().stream()
                    .filter(ClassCodecAspect.class::isInstance)
                    .map(ClassCodecAspect.class::cast)
                    .findFirst()
                    .orElse(null);

            assertNotNull(aspect, "ClassCodecAspect should exist");
            assertNotNull(aspect.getSuperTypeConfig(), "SuperTypeConfig should exist");
            assertTrue(aspect.getSuperTypeConfig().isEnabled());
            assertEquals("_extends", aspect.getSuperTypeConfig().getSuperTypeKey());
        }
    }

    // ========================================================================
    // Serialization Tests - verify annotations affect JSON output
    // ========================================================================

    @Nested
    @DisplayName("Custom ID Serialization")
    class CustomIdSerialization {

        @Test
        @DisplayName("uses custom ID key from annotation")
        void usesCustomIdKey() throws IOException {
            EObject product = createProduct();
            product.eSet(skuAttribute, "SKU-12345");
            product.eSet(productNameAttribute, "Widget");
            product.eSet(priceAttribute, 29.99);

            String json = serialize(product);

            // Should use "id" instead of "_id" based on annotation
            assertTrue(json.contains("\"id\""), "JSON should use custom ID key 'id'");
            assertTrue(json.contains("\"SKU-12345\""), "JSON should contain SKU value");
        }

        @Test
        @DisplayName("uses NAME type strategy from annotation")
        void usesNameTypeStrategy() throws IOException {
            EObject product = createProduct();
            product.eSet(skuAttribute, "SKU-001");
            product.eSet(productNameAttribute, "Gadget");

            String json = serialize(product);

            // Should use "type" key with simple name value
            assertTrue(json.contains("\"type\""), "JSON should use custom type key 'type'");
            assertTrue(json.contains("\"Product\""), "JSON should contain class name 'Product'");
            // Should NOT contain full URI
            assertFalse(json.contains("http://test.example.org"), "JSON should NOT contain URI");
        }
    }

    @Nested
    @DisplayName("Transient Feature Handling")
    class TransientFeatureHandling {

        @Test
        @DisplayName("excludes transient features from serialization")
        void excludesTransientFeatures() throws IOException {
            EObject product = createProduct();
            product.eSet(skuAttribute, "SKU-999");
            product.eSet(productNameAttribute, "Secret Widget");
            product.eSet(internalCodeAttribute, "INTERNAL-SECRET-CODE");

            String json = serialize(product);

            // internalCode should NOT be in JSON due to codec.transient annotation
            assertFalse(json.contains("\"internalCode\""), "Transient feature should not be serialized");
            assertFalse(json.contains("INTERNAL-SECRET-CODE"), "Transient value should not appear in JSON");

            // Other fields should be present
            assertTrue(json.contains("\"name\""), "Regular features should be serialized");
            assertTrue(json.contains("\"Secret Widget\""));
        }
    }

    @Nested
    @DisplayName("Round-trip with Annotations")
    class RoundTripWithAnnotations {

        @Test
        @DisplayName("round-trips Product with custom ID configuration")
        void roundTripsProductWithCustomId() throws IOException {
            EObject product = createProduct();
            product.eSet(skuAttribute, "PROD-123");
            product.eSet(productNameAttribute, "Amazing Widget");
            product.eSet(priceAttribute, 49.99);
            product.eSet(internalCodeAttribute, "SECRET");

            // Serialize
            String json = serialize(product);
            System.out.println("Product JSON:\n" + json);

            // Deserialize
            EObject loaded = deserialize(json, productClass);

            // Verify
            assertNotNull(loaded);
            assertEquals("PROD-123", loaded.eGet(skuAttribute));
            assertEquals("Amazing Widget", loaded.eGet(productNameAttribute));
            assertEquals(49.99, (Double) loaded.eGet(priceAttribute), 0.001);

            // Transient field should be null/default after round-trip
            assertNull(loaded.eGet(internalCodeAttribute), "Transient field should not be restored");
        }

        @Test
        @DisplayName("round-trips Order with nested items")
        @SuppressWarnings("unchecked")
        void roundTripsOrderWithItems() throws IOException {
            EObject order = createOrder();
            order.eSet(customerIdAttribute, "CUST-001");
            order.eSet(orderDateAttribute, "2025-12-17");
            order.eSet(totalAttribute, 149.97);

            EObject item1 = createOrderItem();
            item1.eSet(orderItemClass.getEStructuralFeature("productName"), "Widget A");
            item1.eSet(orderItemClass.getEStructuralFeature("quantity"), 2);
            item1.eSet(orderItemClass.getEStructuralFeature("unitPrice"), 24.99);

            EObject item2 = createOrderItem();
            item2.eSet(orderItemClass.getEStructuralFeature("productName"), "Widget B");
            item2.eSet(orderItemClass.getEStructuralFeature("quantity"), 1);
            item2.eSet(orderItemClass.getEStructuralFeature("unitPrice"), 99.99);

            List<EObject> items = (List<EObject>) order.eGet(itemsRef);
            items.add(item1);
            items.add(item2);

            // Serialize
            String json = serialize(order);
            System.out.println("Order JSON:\n" + json);

            // Verify custom ID key is used
            assertTrue(json.contains("\"orderId\""), "JSON should use custom ID key 'orderId'");

            // Deserialize
            EObject loaded = deserialize(json, orderClass);

            // Verify
            assertNotNull(loaded);
            assertEquals("CUST-001", loaded.eGet(customerIdAttribute));
            assertEquals("2025-12-17", loaded.eGet(orderDateAttribute));
            assertEquals(149.97, (Double) loaded.eGet(totalAttribute), 0.001);

            List<EObject> loadedItems = (List<EObject>) loaded.eGet(itemsRef);
            assertEquals(2, loadedItems.size());
            assertEquals("Widget A", loadedItems.get(0).eGet(orderItemClass.getEStructuralFeature("productName")));
            assertEquals(2, loadedItems.get(0).eGet(orderItemClass.getEStructuralFeature("quantity")));
        }

        @Test
        @DisplayName("round-trips VIPCustomer (subclass)")
        void roundTripsVIPCustomer() throws IOException {
            EObject vip = createVIPCustomer();
            vip.eSet(customerClass.getEStructuralFeature("email"), "vip@example.com");
            vip.eSet(customerClass.getEStructuralFeature("firstName"), "John");
            vip.eSet(customerClass.getEStructuralFeature("lastName"), "Doe");
            vip.eSet(vipCustomerClass.getEStructuralFeature("vipLevel"), 3);
            vip.eSet(vipCustomerClass.getEStructuralFeature("discountPercent"), 15.0);

            // Serialize
            String json = serialize(vip);
            System.out.println("VIPCustomer JSON:\n" + json);

            // Deserialize
            EObject loaded = deserialize(json, vipCustomerClass);

            // Verify
            assertNotNull(loaded);
            assertEquals("vip@example.com", loaded.eGet(customerClass.getEStructuralFeature("email")));
            assertEquals("John", loaded.eGet(customerClass.getEStructuralFeature("firstName")));
            assertEquals("Doe", loaded.eGet(customerClass.getEStructuralFeature("lastName")));
            assertEquals(3, loaded.eGet(vipCustomerClass.getEStructuralFeature("vipLevel")));
            assertEquals(15.0, (Double) loaded.eGet(vipCustomerClass.getEStructuralFeature("discountPercent")), 0.001);
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String serialize(EObject object) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootEClass) throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private String serializeWithOptions(EObject object, Map<String, Object> options) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);

        return out.toString(StandardCharsets.UTF_8);
    }

    // ========================================================================
    // Options Override Tests - verify load/save options take precedence
    // ========================================================================

    @Nested
    @DisplayName("Options Override Annotations")
    class OptionsOverrideAnnotations {

        @Test
        @DisplayName("save options override annotation typeKey")
        void saveOptionsOverrideAnnotationTypeKey() throws IOException {
            // Product annotation has typeKey="type", typeStrategy=NAME
            // We override typeKey to "_customType" via save options
            EObject product = createProduct();
            product.eSet(skuAttribute, "PROD-001");
            product.eSet(productNameAttribute, "Test Product");

            Map<String, Object> options = new HashMap<>();
            options.put("typeKey", "_customType");

            String json = serializeWithOptions(product, options);
            System.out.println("Product with overridden typeKey:\n" + json);

            // Should use the overridden key, not the annotation's "type"
            assertTrue(json.contains("\"_customType\""),
                "JSON should use overridden typeKey '_customType', but got: " + json);
            assertFalse(json.contains("\"type\":\"Product\""),
                "JSON should NOT use annotation typeKey 'type'");
        }

        @Test
        @DisplayName("save options override annotation typeStrategy")
        void saveOptionsOverrideAnnotationTypeStrategy() throws IOException {
            // Product annotation has typeStrategy=NAME
            // We override to NONE to suppress type info
            EObject product = createProduct();
            product.eSet(skuAttribute, "PROD-002");
            product.eSet(productNameAttribute, "No Type Product");

            Map<String, Object> options = new HashMap<>();
            options.put("typeStrategy", "NONE");

            String json = serializeWithOptions(product, options);
            System.out.println("Product with typeStrategy=NONE:\n" + json);

            // Should NOT contain type info
            assertFalse(json.contains("\"type\""),
                "JSON should NOT contain type key when typeStrategy=NONE");
            assertFalse(json.contains("\"_type\""),
                "JSON should NOT contain _type when typeStrategy=NONE");
            assertFalse(json.contains("\"Product\"") && json.contains("type"),
                "JSON should NOT contain type name as type indicator");
        }

        @Test
        @DisplayName("save options override annotation idKey")
        void saveOptionsOverrideAnnotationIdKey() throws IOException {
            // Order annotation has idKey="orderId"
            // We override to "_customId" via save options
            EObject order = createOrder();
            order.eSet(customerIdAttribute, "CUST-001");
            order.eSet(orderDateAttribute, "2025-01-15");
            order.eSet(totalAttribute, 99.99);

            Map<String, Object> options = new HashMap<>();
            options.put("idKey", "_customId");

            String json = serializeWithOptions(order, options);
            System.out.println("Order with overridden idKey:\n" + json);

            // Should use the overridden key
            assertTrue(json.contains("\"_customId\""),
                "JSON should use overridden idKey '_customId', but got: " + json);
        }

        @Test
        @DisplayName("save options can force serialization of ignored feature")
        void saveOptionsForceIgnoredFeature() throws IOException {
            // Product.internalCode has ignore=true in annotation
            // We override with forceWrite=true to serialize it anyway
            EObject product = createProduct();
            product.eSet(skuAttribute, "PROD-003");
            product.eSet(productNameAttribute, "Secret Product");
            product.eSet(internalCodeAttribute, "TOP-SECRET-123");

            // First verify without override - internalCode should be absent
            String jsonWithoutOverride = serialize(product);
            assertFalse(jsonWithoutOverride.contains("internalCode"),
                "Without override, internalCode should be ignored");
            assertFalse(jsonWithoutOverride.contains("TOP-SECRET"),
                "Without override, secret value should not appear");

            // Now with forceWrite at global level
            Map<String, Object> options = new HashMap<>();
            // Use nested map for feature-level config
            Map<String, Object> productConfig = new HashMap<>();
            Map<String, Object> internalCodeConfig = new HashMap<>();
            internalCodeConfig.put("forceWrite", true);
            productConfig.put("internalCode", internalCodeConfig);
            options.put("Product", productConfig);

            String jsonWithOverride = serializeWithOptions(product, options);
            System.out.println("Product with forceWrite for internalCode:\n" + jsonWithOverride);

            assertTrue(jsonWithOverride.contains("\"internalCode\""),
                "With forceWrite, internalCode should be serialized");
            assertTrue(jsonWithOverride.contains("TOP-SECRET-123"),
                "With forceWrite, secret value should appear");
        }

        @Test
        @DisplayName("global options apply to all classes")
        void globalOptionsApplyToAllClasses() throws IOException {
            // Set global typeStrategy=NONE - should affect all classes
            EObject product = createProduct();
            product.eSet(skuAttribute, "PROD-004");
            product.eSet(productNameAttribute, "Global Test");

            EObject order = createOrder();
            order.eSet(customerIdAttribute, "CUST-002");
            order.eSet(orderDateAttribute, "2025-02-01");

            Map<String, Object> options = new HashMap<>();
            options.put("typeStrategy", "NONE");

            String productJson = serializeWithOptions(product, options);
            System.out.println("Product with global typeStrategy=NONE:\n" + productJson);

            // Product normally has typeStrategy=NAME from annotation
            // Global option should override it
            assertFalse(productJson.contains("\"type\":"),
                "Global typeStrategy=NONE should override Product annotation");
        }
    }
}
