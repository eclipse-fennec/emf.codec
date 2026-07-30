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
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@code forceWrite} makes a transient/volatile/derived feature
 * appear in the CSV header row and data row.
 */
@DisplayName("CSV ForceWrite option")
class CsvForceWriteTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-force.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass computedClass;
    private EAttribute nameAttr;
    private EAttribute derivedAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        computedClass = EcoreHelper.getEClass(testPackage, "Computed");
        nameAttr = (EAttribute) EcoreHelper.getFeature(computedClass, "name");
        derivedAttr = (EAttribute) EcoreHelper.getFeature(computedClass, "derived");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createComputed() {
        EObject computed = testPackage.getEFactoryInstance().create(computedClass);
        computed.eSet(nameAttr, "test-item");
        computed.eSet(derivedAttr, "computed-value");
        return computed;
    }

    private String save(ConfigurationResolver config) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://force.csv"),
                metadataService, config,
                null, null, new CsvFormatProvider(computedClass));
        resource.getContents().add(createComputed());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.save(baos, Collections.emptyMap());
        return baos.toString(StandardCharsets.UTF_8);
    }

    private List<String> lines(String csv) {
        String trimmed = csv.endsWith("\n") ? csv.substring(0, csv.length() - 1) : csv;
        return List.of(trimmed.split("\n", -1));
    }

    @Test
    @DisplayName("derived feature is dropped by default")
    void derivedSkippedByDefault() throws IOException {
        String csv = save(ConfigurationResolver.defaults());
        String header = lines(csv).get(0);

        assertTrue(header.contains("name"), header);
        assertFalse(header.contains("derived"),
                () -> "derived feature (volatile/transient/derived) should be skipped by default: " + header);
    }

    @Test
    @DisplayName("forceWrite includes derived feature in header and data")
    void forceWriteIncludesDerived() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .forceWrite(derivedAttr)
                .build();
        String csv = save(config);
        List<String> rows = lines(csv);
        assertEquals(3, rows.size());

        String header = rows.get(0);
        assertTrue(header.contains("derived"),
                () -> "forceWrite should bring 'derived' into the header: " + header);

        String data = rows.get(2);
        assertTrue(data.contains("computed-value"),
                () -> "derived value should appear in the data row: " + data);
    }
}
