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
package org.eclipse.fennec.codec.ods;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.codec.tests.tck.AbstractCoreRoundTripTCK;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

/**
 * ODS has no value gate of its own - it shares {@code TabularDocumentBuilder} with the other
 * tabular renderers - so what is verified here is that the gate is reachable through the two
 * ways a caller configures a save: the {@link ConfigurationResolver} and the save-options map.
 * <p>
 * The options-map half is the half that was broken: the gate worked, but the published constant
 * named a key no resolver reads, so a caller passing it got the default behaviour back with
 * nothing said. {@link CodecOptions#CODEC_SERIALIZE_DEFAULT} is the corrected spelling.
 * <p>
 * {@code CsvValueHandlingTest} covers the same matrix for CSV, driven through the resolver only.
 */
@DisplayName("ODS ValueHandling option")
class OdsValueHandlingTest {

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

    // ========================================================================
    // Helpers
    // ========================================================================

    /** {@code defaultedPrice} is declared {@code defaultValueLiteral="9.99"} in the test ecore. */
    private EObject createDefaultPricedProduct(String id, String name) {
        EObject p = testPackage.getEFactoryInstance().create(productClass);
        p.eSet(productIdAttr, id);
        p.eSet(productNameAttr, name);
        p.eSet(EcoreHelper.getFeature(productClass, "defaultedPrice"), Double.valueOf(9.99));
        return p;
    }

    private Sheet save(EObject product, ConfigurationResolver config, Map<String, Object> options)
            throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("valuehandling.ods"),
                metadataService, config == null ? ConfigurationResolver.defaults() : config,
                null, null, new OdsFormatProvider());
        resource.getContents().add(product);

        Map<String, Object> saveOptions = new HashMap<>();
        saveOptions.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.IGNORE);
        if (options != null) {
            saveOptions.putAll(options);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.save(baos, saveOptions);
        return new SpreadSheet(new ByteArrayInputStream(baos.toByteArray())).getSheets().get(0);
    }

    private static List<String> headers(Sheet sheet) {
        List<String> found = new ArrayList<>();
        for (int c = 0; c < sheet.getMaxColumns(); c++) {
            Object v = sheet.getRange(0, c).getValue();
            if (v != null) {
                found.add(String.valueOf(v));
            }
        }
        return found;
    }

    private static Object cellUnder(Sheet sheet, String header) {
        for (int c = 0; c < sheet.getMaxColumns(); c++) {
            if (header.equals(String.valueOf(sheet.getRange(0, c).getValue()))) {
                return sheet.getRange(1, c).getValue();
            }
        }
        return null;
    }

    // ========================================================================

    @Nested
    @DisplayName("Defaults (no serializeNull/Empty/Default)")
    class DefaultsBehaviour {

        @Test
        @DisplayName("null/empty/default features are dropped from the header row")
        void dropsOptionalColumns() throws IOException {
            Sheet sheet = save(createDefaultPricedProduct("p-001", "Plain"), null, null);
            List<String> header = headers(sheet);

            assertTrue(header.contains("productId"), () -> "productId missing: " + header);
            assertTrue(header.contains("name"), () -> "name missing: " + header);
            assertTrue(!header.contains("optionalDesc"),
                    () -> "null 'optionalDesc' should be dropped by default: " + header);
            assertTrue(!header.contains("defaultedPrice"),
                    () -> "default-valued 'defaultedPrice' should be dropped by default: " + header);
            assertTrue(!header.contains("tags"),
                    () -> "empty 'tags' should be dropped by default: " + header);
        }
    }

    @Nested
    @DisplayName("serializeDefault(true) through the ConfigurationResolver")
    class ThroughResolver {

        @Test
        @DisplayName("includes the default-valued feature with its value")
        void includesDefaultedFeature() throws IOException {
            ConfigurationResolver config = ConfigurationResolver.builder()
                    .serializeDefault(true)
                    .build();

            Sheet sheet = save(createDefaultPricedProduct("p-002", "Defaulted"), config, null);

            assertTrue(headers(sheet).contains("defaultedPrice"),
                    () -> "serializeDefault(true) should bring 'defaultedPrice' in: " + headers(sheet));
            assertEquals(9.99, ((Number) cellUnder(sheet, "defaultedPrice")).doubleValue(), 0.0001);
        }
    }

    @Nested
    @DisplayName("serializeDefault=true through the save-options map")
    class ThroughSaveOptions {

        @Test
        @DisplayName("the published CodecOptions constant is honoured")
        void publishedConstantIsHonoured() throws IOException {
            Sheet sheet = save(createDefaultPricedProduct("p-003", "Defaulted"), null,
                    Map.of(CodecOptions.CODEC_SERIALIZE_DEFAULT, Boolean.TRUE));

            assertTrue(headers(sheet).contains("defaultedPrice"),
                    () -> "CodecOptions.CODEC_SERIALIZE_DEFAULT must reach the value gate; "
                            + "a key no resolver reads is dropped in silence. Header: "
                            + headers(sheet));
            assertEquals(9.99, ((Number) cellUnder(sheet, "defaultedPrice")).doubleValue(), 0.0001);
        }

        @Test
        @DisplayName("the short key is honoured too")
        void shortKeyIsHonoured() throws IOException {
            Sheet sheet = save(createDefaultPricedProduct("p-004", "Defaulted"), null,
                    Map.of("serializeDefault", Boolean.TRUE));

            assertTrue(headers(sheet).contains("defaultedPrice"),
                    () -> "the short key should reach the value gate: " + headers(sheet));
        }
    }
}
