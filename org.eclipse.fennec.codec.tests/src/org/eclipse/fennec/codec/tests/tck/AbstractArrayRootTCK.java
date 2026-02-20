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
import static org.junit.jupiter.api.Assertions.assertThrows;

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
 * Abstract TCK for array root (multiple root objects) tests.
 * <p>
 * Formats that support array root will run the round-trip tests.
 * Formats that do not support array root will verify that an error is raised.
 */
public abstract class AbstractArrayRootTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-arrayroot.ecore";

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
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractArrayRootTCK.class);
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
    @DisplayName("multiple root objects round-trip or error for unsupported formats")
    void multipleRootObjectsRoundTrip() throws IOException {
        CodecResource saveResource = createResource();

        for (int i = 0; i < 3; i++) {
            EObject entry = testPackage.getEFactoryInstance().create(entryClass);
            entry.eSet(labelAttr, "entry-" + i);
            entry.eSet(valueAttr, (i + 1) * 10);
            saveResource.getContents().add(entry);
        }

        if (!createFormatProvider().supportsArrayRoot()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            assertThrows(IOException.class, () -> saveResource.save(out, Collections.emptyMap()),
                    "Should throw when format does not support multiple root objects");
            return;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        CodecResource loadResource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, entryClass);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), options);

        assertEquals(3, loadResource.getContents().size(), "Should have 3 root objects");

        for (int i = 0; i < 3; i++) {
            EObject loaded = loadResource.getContents().get(i);
            assertNotNull(loaded);
            assertEquals("entry-" + i, loaded.eGet(labelAttr));
            assertEquals((i + 1) * 10, loaded.eGet(valueAttr));
        }
    }

    @Test
    @DisplayName("single root object round-trip")
    void singleRootNotArray() throws IOException {
        CodecResource saveResource = createResource();

        EObject entry = testPackage.getEFactoryInstance().create(entryClass);
        entry.eSet(labelAttr, "solo");
        entry.eSet(valueAttr, 42);
        saveResource.getContents().add(entry);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        CodecResource loadResource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, entryClass);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), options);

        assertEquals(1, loadResource.getContents().size(), "Should have 1 root object");
        EObject loaded = loadResource.getContents().get(0);
        assertNotNull(loaded);
        assertEquals("solo", loaded.eGet(labelAttr));
        assertEquals(42, loaded.eGet(valueAttr));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://arrayroot." + getFileExtension()),
                metadataService, ConfigurationResolver.defaults(),
                null, null, createFormatProvider());
    }
}
