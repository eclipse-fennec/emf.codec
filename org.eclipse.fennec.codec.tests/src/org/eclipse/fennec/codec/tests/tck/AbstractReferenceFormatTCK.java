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
package org.eclipse.fennec.codec.tests.tck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for reference format tests.
 * <p>
 * Tests PLAIN and STRUCTURED reference formats.
 */
public abstract class AbstractReferenceFormatTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass warehouseClass;
    private EClass productClass;
    private EAttribute warehouseNameAttr;
    private EAttribute productIdAttr;
    private EAttribute productNameAttr;
    private EReference itemsRef;
    private EReference featuredRef;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractReferenceFormatTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        warehouseClass = EcoreHelper.getEClass(testPackage, "Warehouse");
        productClass = EcoreHelper.getEClass(testPackage, "Product");
        warehouseNameAttr = (EAttribute) EcoreHelper.getFeature(warehouseClass, "name");
        productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");
        itemsRef = (EReference) EcoreHelper.getFeature(warehouseClass, "items");
        featuredRef = (EReference) EcoreHelper.getFeature(warehouseClass, "featured");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("PLAIN reference format round-trip")
    @SuppressWarnings("unchecked")
    void plainReferenceFormat() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .moduleProperties(Map.of("refFormat", "PLAIN"))
                .build();

        EObject warehouse = createWarehouseWithRef();
        EObject loaded = roundTrip(warehouse, warehouseClass, config);

        assertNotNull(loaded);
        assertEquals("Main Warehouse", loaded.eGet(warehouseNameAttr));

        List<EObject> loadedItems = (List<EObject>) loaded.eGet(itemsRef);
        assertEquals(2, loadedItems.size());

        EObject loadedFeatured = (EObject) loaded.eGet(featuredRef);
        assertNotNull(loadedFeatured, "Featured reference should be resolved");
        assertEquals("Widget", loadedFeatured.eGet(productNameAttr));
        assertSame(loadedItems.get(0), loadedFeatured,
                "Featured should be same instance as first contained product");
    }

    @Test
    @DisplayName("STRUCTURED reference format round-trip")
    @SuppressWarnings("unchecked")
    void structuredReferenceFormat() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.defaults();

        EObject warehouse = createWarehouseWithRef();
        EObject loaded = roundTrip(warehouse, warehouseClass, config);

        assertNotNull(loaded);
        assertEquals("Main Warehouse", loaded.eGet(warehouseNameAttr));

        List<EObject> loadedItems = (List<EObject>) loaded.eGet(itemsRef);
        assertEquals(2, loadedItems.size());

        EObject loadedFeatured = (EObject) loaded.eGet(featuredRef);
        assertNotNull(loadedFeatured, "Featured reference should be resolved");
        assertEquals("Widget", loadedFeatured.eGet(productNameAttr));
        assertSame(loadedItems.get(0), loadedFeatured,
                "Featured should be same instance as first contained product");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    @SuppressWarnings("unchecked")
    private EObject createWarehouseWithRef() {
        EObject warehouse = testPackage.getEFactoryInstance().create(warehouseClass);
        warehouse.eSet(warehouseNameAttr, "Main Warehouse");

        EObject product1 = testPackage.getEFactoryInstance().create(productClass);
        product1.eSet(productIdAttr, "prod-1");
        product1.eSet(productNameAttr, "Widget");

        EObject product2 = testPackage.getEFactoryInstance().create(productClass);
        product2.eSet(productIdAttr, "prod-2");
        product2.eSet(productNameAttr, "Gadget");

        List<EObject> items = (List<EObject>) warehouse.eGet(itemsRef);
        items.add(product1);
        items.add(product2);

        warehouse.eSet(featuredRef, product1);

        return warehouse;
    }

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://refformat." + getFileExtension()),
                metadataService, config,
                null, null, createFormatProvider());
    }

    private EObject roundTrip(EObject object, EClass rootEClass, ConfigurationResolver config) throws IOException {
        CodecResource saveResource = createResource(config);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        CodecResource loadResource = createResource(config);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), options);
        assertNoDiagnostics(loadResource);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    /**
     * Fails when a round trip reported problems (issue #131).
     * <p>
     * Deserialization catches, logs and continues, so a load succeeds even when a value was
     * dropped. The diagnostics are the only trace - a test that ignores them cannot tell a
     * clean round trip from a lossy one.
     * </p>
     */
    private static void assertNoDiagnostics(CodecResource resource) {
        assertTrue(resource.getErrors().isEmpty(),
                "round trip reported errors: " + resource.getErrors());
        assertTrue(resource.getWarnings().isEmpty(),
                "round trip reported warnings: " + resource.getWarnings());
    }
}
