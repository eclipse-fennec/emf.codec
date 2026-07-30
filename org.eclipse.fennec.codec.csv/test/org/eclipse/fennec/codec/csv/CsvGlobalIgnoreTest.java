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
 * Verifies that {@code codec.ignoreFeatures} drops the matching columns from
 * the CSV header row.
 */
@DisplayName("CSV GlobalIgnore option")
class CsvGlobalIgnoreTest {

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
                URI.createURI("test://globalignore.csv"),
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

    @Test
    @DisplayName("all features present without ignore")
    void allFeaturesPresentByDefault() throws IOException {
        String csv = save(ConfigurationResolver.defaults());
        String header = lines(csv).get(0);

        assertTrue(header.contains("name"), header);
        assertTrue(header.contains("age"), header);
        assertTrue(header.contains("score"), header);
        assertTrue(header.contains("secret"), header);
    }

    @Test
    @DisplayName("globalIgnoreFeatures drops a single column")
    void dropsSingleColumn() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .globalIgnoreFeatures("secret")
                .build();
        String csv = save(config);
        List<String> rows = lines(csv);
        assertEquals(3, rows.size());

        String header = rows.get(0);
        assertTrue(header.contains("name"), header);
        assertTrue(header.contains("age"), header);
        assertTrue(header.contains("score"), header);
        assertFalse(header.contains("secret"),
                () -> "'secret' should be globally ignored: " + header);

        // Data row must not carry the ignored value either.
        String data = rows.get(2);
        assertFalse(data.contains("top-secret"),
                () -> "ignored value 'top-secret' must not appear: " + data);
    }

    @Test
    @DisplayName("globalIgnoreFeatures drops multiple columns")
    void dropsMultipleColumns() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .globalIgnoreFeatures("secret", "score")
                .build();
        String csv = save(config);
        String header = lines(csv).get(0);

        assertTrue(header.contains("name"), header);
        assertTrue(header.contains("age"), header);
        assertFalse(header.contains("score"), header);
        assertFalse(header.contains("secret"), header);
    }
}
