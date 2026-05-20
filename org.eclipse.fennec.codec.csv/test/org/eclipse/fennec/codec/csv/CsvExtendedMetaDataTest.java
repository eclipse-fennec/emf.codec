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
package org.eclipse.fennec.codec.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.tests.tck.AbstractCoreRoundTripTCK;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@code codec.useNamesFromExtendedMetaData} flows into the CSV header row.
 */
@DisplayName("CSV ExtendedMetaData option")
class CsvExtendedMetaDataTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-extmetadata.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass documentClass;
    private EAttribute documentTitleAttr;
    private EAttribute pageCountAttr;
    private EAttribute internalIdAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        documentClass = EcoreHelper.getEClass(testPackage, "Document");
        documentTitleAttr = (EAttribute) EcoreHelper.getFeature(documentClass, "documentTitle");
        pageCountAttr = (EAttribute) EcoreHelper.getFeature(documentClass, "pageCount");
        internalIdAttr = (EAttribute) EcoreHelper.getFeature(documentClass, "internalId");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createDocument() {
        EObject doc = testPackage.getEFactoryInstance().create(documentClass);
        doc.eSet(documentTitleAttr, "My Document");
        doc.eSet(pageCountAttr, 42);
        doc.eSet(internalIdAttr, "doc-001");
        return doc;
    }

    private String save(ConfigurationResolver config) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://extmetadata.csv"),
                metadataService, config,
                null, null, new CsvFormatProvider(documentClass));
        resource.getContents().add(createDocument());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.save(baos, Collections.emptyMap());
        return baos.toString(StandardCharsets.UTF_8);
    }

    private List<String> lines(String csv) {
        String trimmed = csv.endsWith("\n") ? csv.substring(0, csv.length() - 1) : csv;
        return List.of(trimmed.split("\n", -1));
    }

    @Test
    @DisplayName("uses EMF feature names by default")
    void usesFeatureNamesByDefault() throws IOException {
        String csv = save(ConfigurationResolver.defaults());
        String header = lines(csv).get(0);

        assertTrue(header.contains("documentTitle"), header);
        assertTrue(header.contains("internalId"), header);
        assertTrue(header.contains("pageCount"), header);
        assertFalse(header.contains("title,") || header.endsWith("title"), header);
    }

    @Test
    @DisplayName("uses ExtendedMetaData names when option enabled")
    void usesExtendedMetaDataNames() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .useNamesFromExtendedMetaData(true)
                .build();
        String csv = save(config);

        List<String> rows = lines(csv);
        assertEquals(3, rows.size());

        String header = rows.get(0);
        assertTrue(header.contains("title"), header);
        assertTrue(header.contains("id"), header);
        assertTrue(header.contains("pageCount"), header); // pageCount has no EMD name → falls back
        assertFalse(header.contains("documentTitle"), header);
        assertFalse(header.contains("internalId"), header);
    }
}
