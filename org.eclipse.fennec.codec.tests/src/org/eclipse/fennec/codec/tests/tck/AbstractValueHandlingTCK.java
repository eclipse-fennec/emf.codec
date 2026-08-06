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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
 * Abstract TCK for value handling tests.
 * <p>
 * Tests null serialization, empty collection, and default value handling.
 */
public abstract class AbstractValueHandlingTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass productClass;
    private EAttribute productIdAttr;
    private EAttribute productNameAttr;
    private EAttribute optionalDescAttr;
    private EAttribute defaultedPriceAttr;
    private EAttribute tagsAttr;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractValueHandlingTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        productClass = EcoreHelper.getEClass(testPackage, "Product");
        productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");
        optionalDescAttr = (EAttribute) EcoreHelper.getFeature(productClass, "optionalDesc");
        defaultedPriceAttr = (EAttribute) EcoreHelper.getFeature(productClass, "defaultedPrice");
        tagsAttr = (EAttribute) EcoreHelper.getFeature(productClass, "tags");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("null serialization round-trip")
    void serializeNullRoundTrip() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .serializeNull(true)
                .build();

        EObject product = testPackage.getEFactoryInstance().create(productClass);
        product.eSet(productIdAttr, "null-test");
        product.eSet(productNameAttr, "Null Product");
        // optionalDesc is NOT set — remains null

        EObject loaded = roundTrip(product, productClass, config);

        assertNotNull(loaded);
        assertEquals("null-test", loaded.eGet(productIdAttr));
        assertEquals("Null Product", loaded.eGet(productNameAttr));
        assertNull(loaded.eGet(optionalDescAttr), "optionalDesc should be null after round-trip");
    }

    @Test
    @DisplayName("empty collection serialization round-trip")
    @SuppressWarnings("unchecked")
    void serializeEmptyCollectionRoundTrip() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .serializeEmpty(true)
                .build();

        EObject product = testPackage.getEFactoryInstance().create(productClass);
        product.eSet(productIdAttr, "empty-test");
        product.eSet(productNameAttr, "Empty Tags Product");
        // tags list is empty by default

        EObject loaded = roundTrip(product, productClass, config);

        assertNotNull(loaded);
        assertEquals("empty-test", loaded.eGet(productIdAttr));
        List<String> loadedTags = (List<String>) loaded.eGet(tagsAttr);
        assertTrue(loadedTags.isEmpty(), "Tags should be empty after round-trip");
    }

    @Test
    @DisplayName("default value serialization round-trip")
    void serializeDefaultValueRoundTrip() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .serializeDefault(true)
                .build();

        EObject product = testPackage.getEFactoryInstance().create(productClass);
        product.eSet(productIdAttr, "default-test");
        product.eSet(productNameAttr, "Default Price Product");
        // defaultedPrice is NOT set — stays at default 9.99

        EObject loaded = roundTrip(product, productClass, config);

        assertNotNull(loaded);
        assertEquals("default-test", loaded.eGet(productIdAttr));
        assertEquals(9.99, (Double) loaded.eGet(defaultedPriceAttr), 0.001,
                "Default price should be preserved after round-trip");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://valuehandling." + getFileExtension()),
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
