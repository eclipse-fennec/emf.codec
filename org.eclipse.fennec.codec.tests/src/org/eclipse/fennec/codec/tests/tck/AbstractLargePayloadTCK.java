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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
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
 * Abstract TCK for large payload tests.
 * <p>
 * Uses the arrayroot ecore model (Entry class) with 1000+ objects.
 * Requires array root support since the test adds 1000 objects to
 * the resource contents list.
 */
public abstract class AbstractLargePayloadTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-arrayroot.ecore";
    private static final int PAYLOAD_SIZE = 1000;

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass entryClass;
    private EAttribute labelAttr;
    private EAttribute valueAttr;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractLargePayloadTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        entryClass = EcoreHelper.getEClass(testPackage, "Entry");
        labelAttr = (EAttribute) EcoreHelper.getFeature(entryClass, "label");
        valueAttr = (EAttribute) EcoreHelper.getFeature(entryClass, "value");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("large payload (1000 objects) round-trip")
    void largePayloadRoundTrip() throws IOException {
        if (!createFormatProvider().supportsArrayRoot()) {
            // Large payload requires array root — nothing to test for single-doc formats
            return;
        }

        CodecResource saveResource = createResource();

        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            EObject entry = testPackage.getEFactoryInstance().create(entryClass);
            entry.eSet(labelAttr, "entry-" + i);
            entry.eSet(valueAttr, i);
            saveResource.getContents().add(entry);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        CodecResource loadResource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, entryClass);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), options);

        assertEquals(PAYLOAD_SIZE, loadResource.getContents().size(),
                "Should have " + PAYLOAD_SIZE + " root objects");

        // Spot-check first, middle, and last entries
        EObject first = loadResource.getContents().get(0);
        assertNotNull(first);
        assertEquals("entry-0", first.eGet(labelAttr));
        assertEquals(0, first.eGet(valueAttr));

        EObject middle = loadResource.getContents().get(PAYLOAD_SIZE / 2);
        assertNotNull(middle);
        assertEquals("entry-" + (PAYLOAD_SIZE / 2), middle.eGet(labelAttr));
        assertEquals(PAYLOAD_SIZE / 2, middle.eGet(valueAttr));

        EObject last = loadResource.getContents().get(PAYLOAD_SIZE - 1);
        assertNotNull(last);
        assertEquals("entry-" + (PAYLOAD_SIZE - 1), last.eGet(labelAttr));
        assertEquals(PAYLOAD_SIZE - 1, last.eGet(valueAttr));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://largepayload." + getFileExtension()),
                metadataService, ConfigurationResolver.defaults(),
                null, null, createFormatProvider());
    }
}
