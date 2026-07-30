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
import java.util.Map;

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
 * Verifies that per-feature {@code ignoreWrite} / {@code ignore} flags drop
 * the corresponding column from the CSV header. {@code ignoreRead} is a
 * read-side flag and must NOT affect the writer output.
 */
@DisplayName("CSV Visibility option")
class CsvVisibilityTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-visibility.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EAttribute nameAttr;
    private EAttribute ageAttr;
    private EAttribute scoreAttr;
    private EAttribute secretAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        nameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttr = (EAttribute) EcoreHelper.getFeature(personClass, "age");
        scoreAttr = (EAttribute) EcoreHelper.getFeature(personClass, "score");
        secretAttr = (EAttribute) EcoreHelper.getFeature(personClass, "secret");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createPerson() {
        EObject p = testPackage.getEFactoryInstance().create(personClass);
        p.eSet(nameAttr, "Alice");
        p.eSet(ageAttr, 30);
        p.eSet(scoreAttr, 95.5);
        p.eSet(secretAttr, "top-secret");
        return p;
    }

    private String save(ConfigurationResolver config) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://visibility.csv"),
                metadataService, config,
                null, null, new CsvFormatProvider(personClass));
        resource.getContents().add(createPerson());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.save(baos, Collections.emptyMap());
        return baos.toString(StandardCharsets.UTF_8);
    }

    private List<String> lines(String csv) {
        String trimmed = csv.endsWith("\n") ? csv.substring(0, csv.length() - 1) : csv;
        return List.of(trimmed.split("\n", -1));
    }

    private ConfigurationResolver perFeatureFlag(String flag) {
        Map<String, Object> featureProps = Map.of(flag, true);
        Map<String, Object> classProps = Map.of("secret", featureProps);
        return ConfigurationResolver.builder()
                .moduleProperties(Map.of("Person", classProps))
                .build();
    }

    @Test
    @DisplayName("ignoreWrite drops column from header and data row")
    void ignoreWriteDropsColumn() throws IOException {
        String csv = save(perFeatureFlag("ignoreWrite"));
        List<String> rows = lines(csv);
        assertEquals(3, rows.size());

        String header = rows.get(0);
        assertTrue(header.contains("name"), header);
        assertFalse(header.contains("secret"),
                () -> "ignoreWrite=true should drop 'secret' from header: " + header);

        String data = rows.get(2);
        assertFalse(data.contains("top-secret"),
                () -> "ignoreWrite=true should drop the 'secret' value: " + data);
    }

    @Test
    @DisplayName("ignore=true (both directions) drops column")
    void ignoreBothDirectionsDropsColumn() throws IOException {
        String csv = save(perFeatureFlag("ignore"));
        String header = lines(csv).get(0);

        assertTrue(header.contains("name"), header);
        assertFalse(header.contains("secret"),
                () -> "ignore=true should drop 'secret' from header: " + header);
    }

    @Test
    @DisplayName("ignoreRead does NOT affect writer output")
    void ignoreReadDoesNotAffectWriter() throws IOException {
        String csv = save(perFeatureFlag("ignoreRead"));
        String header = lines(csv).get(0);

        assertTrue(header.contains("secret"),
                () -> "ignoreRead is read-side only and must not drop 'secret' from writer output: " + header);
    }
}
