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
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for ExtendedMetaData name mapping tests.
 */
public abstract class AbstractExtendedMetaDataTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-extmetadata.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass documentClass;
    private EAttribute documentTitleAttr;
    private EAttribute pageCountAttr;
    private EAttribute internalIdAttr;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(AbstractExtendedMetaDataTCK.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        documentClass = ecoreHelper.getEClass(testPackage, "Document");
        documentTitleAttr = (EAttribute) ecoreHelper.getFeature(documentClass, "documentTitle");
        pageCountAttr = (EAttribute) ecoreHelper.getFeature(documentClass, "pageCount");
        internalIdAttr = (EAttribute) ecoreHelper.getFeature(documentClass, "internalId");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("ExtendedMetaData names round-trip")
    void extendedMetaDataNamesRoundTrip() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .useNamesFromExtendedMetaData(true)
                .build();

        EObject doc = testPackage.getEFactoryInstance().create(documentClass);
        doc.eSet(documentTitleAttr, "My Document");
        doc.eSet(pageCountAttr, 42);
        doc.eSet(internalIdAttr, "doc-001");

        EObject loaded = roundTrip(doc, documentClass, config);

        assertNotNull(loaded);
        assertEquals("My Document", loaded.eGet(documentTitleAttr));
        assertEquals(42, loaded.eGet(pageCountAttr));
        assertEquals("doc-001", loaded.eGet(internalIdAttr));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://extmetadata." + getFileExtension()),
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

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }
}
