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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@code serializeNull} / {@code serializeEmpty} / {@code serializeDefault}
 * each cause the corresponding column to appear in the CSV header (with an appropriate
 * cell value in the data row).
 */
@DisplayName("CSV ValueHandling option")
class CsvValueHandlingTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass productClass;
    private EAttribute productIdAttr;
    private EAttribute productNameAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        productClass = EcoreHelper.getEClass(testPackage, "Product");
        productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createProduct(String id, String name) {
        EObject p = testPackage.getEFactoryInstance().create(productClass);
        p.eSet(productIdAttr, id);
        p.eSet(productNameAttr, name);
        return p;
    }

    private String save(EObject product, ConfigurationResolver config) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://valuehandling.csv"),
                metadataService, config,
                null, null, new CsvFormatProvider(productClass));
        resource.getContents().add(product);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.save(baos, Collections.emptyMap());
        return baos.toString(StandardCharsets.UTF_8);
    }

    private List<String> lines(String csv) {
        String trimmed = csv.endsWith("\n") ? csv.substring(0, csv.length() - 1) : csv;
        return List.of(trimmed.split("\n", -1));
    }

    // ========================================================================

    @Nested
    @DisplayName("Defaults (no serializeNull/Empty/Default)")
    class DefaultsBehaviour {

        @Test
        @DisplayName("null/empty/default features are dropped from header")
        void dropsOptionalColumns() throws IOException {
            EObject product = createProduct("p-001", "Plain");
            String csv = save(product, ConfigurationResolver.defaults());
            String header = lines(csv).get(0);

            assertTrue(header.contains("productId"), header);
            assertTrue(header.contains("name"), header);
            assertFalse(header.contains("optionalDesc"),
                    () -> "null 'optionalDesc' should be dropped by default: " + header);
            assertFalse(header.contains("defaultedPrice"),
                    () -> "default-valued 'defaultedPrice' should be dropped by default: " + header);
            assertFalse(header.contains("tags"),
                    () -> "empty 'tags' should be dropped by default: " + header);
        }
    }

    @Nested
    @DisplayName("serializeNull(true)")
    class SerializeNull {

        @Test
        @DisplayName("includes null feature in header with empty cell")
        void includesNullFeature() throws IOException {
            ConfigurationResolver config = ConfigurationResolver.builder()
                    .serializeNull(true)
                    .build();

            EObject product = createProduct("p-002", "Null Product");
            String csv = save(product, config);
            List<String> rows = lines(csv);
            assertEquals(3, rows.size());

            String header = rows.get(0);
            assertTrue(header.contains("optionalDesc"),
                    () -> "serializeNull=true should bring 'optionalDesc' into header: " + header);
        }
    }

    @Nested
    @DisplayName("serializeDefault(true)")
    class SerializeDefault {

        @Test
        @DisplayName("includes default-valued feature in header and data")
        void includesDefaultedFeature() throws IOException {
            ConfigurationResolver config = ConfigurationResolver.builder()
                    .serializeDefault(true)
                    .build();

            EObject product = createProduct("p-003", "Default Price Product");
            String csv = save(product, config);
            List<String> rows = lines(csv);

            String header = rows.get(0);
            assertTrue(header.contains("defaultedPrice"),
                    () -> "serializeDefault=true should bring 'defaultedPrice' into header: " + header);

            String data = rows.get(2);
            assertTrue(data.contains("9.99"),
                    () -> "default value 9.99 should appear in data row: " + data);
        }
    }

    @Nested
    @DisplayName("serializeEmpty(true)")
    class SerializeEmpty {

        @Test
        @DisplayName("includes empty multi-valued feature in header")
        void includesEmptyTags() throws IOException {
            ConfigurationResolver config = ConfigurationResolver.builder()
                    .serializeEmpty(true)
                    .build();

            EObject product = createProduct("p-004", "Empty Tags Product");
            String csv = save(product, config);
            String header = lines(csv).get(0);

            assertTrue(header.contains("tags"),
                    () -> "serializeEmpty=true should bring 'tags' into header: " + header);
        }
    }
}
