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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for EMap round-trip tests.
 */
public abstract class AbstractEMapTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-emap.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass mapContainerClass;
    private EClass itemClass;
    private EReference metadataRef;
    private EReference itemsRef;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractEMapTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        mapContainerClass = EcoreHelper.getEClass(testPackage, "MapContainer");
        itemClass = EcoreHelper.getEClass(testPackage, "Item");
        metadataRef = (EReference) EcoreHelper.getFeature(mapContainerClass, "metadata");
        itemsRef = (EReference) EcoreHelper.getFeature(mapContainerClass, "items");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("String-to-String EMap round-trip")
    @SuppressWarnings("unchecked")
    void stringMapRoundTrip() throws IOException {
        EObject container = testPackage.getEFactoryInstance().create(mapContainerClass);
        container.eSet(EcoreHelper.getFeature(mapContainerClass, "name"), "test-container");

        EMap<String, String> metadata = (EMap<String, String>) container.eGet(metadataRef);
        metadata.put("key1", "value1");
        metadata.put("key2", "value2");
        metadata.put("key3", "value3");

        EObject loaded = roundTrip(container, mapContainerClass);

        assertNotNull(loaded);
        assertEquals("test-container", loaded.eGet(EcoreHelper.getFeature(mapContainerClass, "name")));
        EMap<String, String> loadedMetadata = (EMap<String, String>) loaded.eGet(metadataRef);
        assertEquals(3, loadedMetadata.size());
        assertEquals("value1", loadedMetadata.get("key1"));
        assertEquals("value2", loadedMetadata.get("key2"));
        assertEquals("value3", loadedMetadata.get("key3"));
    }

    @Test
    @DisplayName("String-to-Object EMap round-trip")
    @SuppressWarnings("unchecked")
    void objectMapRoundTrip() throws IOException {
        EObject container = testPackage.getEFactoryInstance().create(mapContainerClass);
        container.eSet(EcoreHelper.getFeature(mapContainerClass, "name"), "item-container");

        EMap<String, EObject> items = (EMap<String, EObject>) container.eGet(itemsRef);

        EObject item1 = testPackage.getEFactoryInstance().create(itemClass);
        item1.eSet(EcoreHelper.getFeature(itemClass, "name"), "Widget");
        item1.eSet(EcoreHelper.getFeature(itemClass, "count"), 10);
        items.put("widget", item1);

        EObject item2 = testPackage.getEFactoryInstance().create(itemClass);
        item2.eSet(EcoreHelper.getFeature(itemClass, "name"), "Gadget");
        item2.eSet(EcoreHelper.getFeature(itemClass, "count"), 5);
        items.put("gadget", item2);

        EObject loaded = roundTrip(container, mapContainerClass);

        assertNotNull(loaded);
        EMap<String, EObject> loadedItems = (EMap<String, EObject>) loaded.eGet(itemsRef);
        assertEquals(2, loadedItems.size());

        EObject loadedWidget = loadedItems.get("widget");
        assertNotNull(loadedWidget);
        assertEquals("Widget", loadedWidget.eGet(EcoreHelper.getFeature(itemClass, "name")));
        assertEquals(10, loadedWidget.eGet(EcoreHelper.getFeature(itemClass, "count")));

        EObject loadedGadget = loadedItems.get("gadget");
        assertNotNull(loadedGadget);
        assertEquals("Gadget", loadedGadget.eGet(EcoreHelper.getFeature(itemClass, "name")));
        assertEquals(5, loadedGadget.eGet(EcoreHelper.getFeature(itemClass, "count")));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://emap." + getFileExtension()),
                metadataService, ConfigurationResolver.defaults(),
                null, null, createFormatProvider());
    }

    private EObject roundTrip(EObject object, EClass rootEClass) throws IOException {
        CodecResource saveResource = createResource();
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        CodecResource loadResource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), options);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }
}
