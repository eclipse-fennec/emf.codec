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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
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
 * End-to-end writer tests for the CSV codec.
 * <p>
 * Reuses the {@code test-format-parity.ecore} model from the codec.tests bundle so
 * we exercise a realistic set of EAttribute types.
 */
@DisplayName("CSV writer (single EObject)")
class CsvWriterTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-format-parity.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass itemClass;
    private EAttribute idAttr;
    private EAttribute labelAttr;
    private EAttribute countAttr;
    private EAttribute amountAttr;
    private EAttribute ratioAttr;
    private EAttribute enabledAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        itemClass = EcoreHelper.getEClass(testPackage, "Item");
        idAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "id");
        labelAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "label");
        countAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "count");
        amountAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "amount");
        ratioAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "ratio");
        enabledAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "enabled");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject createItem() {
        EObject item = testPackage.getEFactoryInstance().create(itemClass);
        item.eSet(idAttr, "item-001");
        item.eSet(labelAttr, "Hello World");
        item.eSet(countAttr, 42);
        item.eSet(amountAttr, 9876543210L);
        item.eSet(ratioAttr, 3.14);
        item.eSet(enabledAttr, true);
        return item;
    }

    private String save(EObject root, CsvFormatProvider provider, Map<String, Object> options) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://item.csv"),
                metadataService,
                ConfigurationResolver.defaults(),
                null, null, provider);
        resource.getContents().add(root);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.save(baos, options);
        return baos.toString(StandardCharsets.UTF_8);
    }

    private List<String> splitLines(String csv) {
        // Strip trailing line ending(s) and split on LF (provider default).
        String trimmed = csv.endsWith("\n") ? csv.substring(0, csv.length() - 1) : csv;
        return List.of(trimmed.split("\n", -1));
    }

    // ========================================================================
    // Tests
    // ========================================================================

    @Nested
    @DisplayName("Three-row layout")
    class ThreeRowLayout {

        @Test
        @DisplayName("emits exactly 3 rows for a single EObject")
        void emitsThreeRows() throws IOException {
            String csv = save(createItem(), new CsvFormatProvider(itemClass), Collections.emptyMap());
            List<String> lines = splitLines(csv);
            assertEquals(3, lines.size(), () -> "Expected 3 rows but got " + lines.size() + ":\n" + csv);
        }

        @Test
        @DisplayName("header row contains every set EAttribute name")
        void headerHasFeatureNames() throws IOException {
            String csv = save(createItem(), new CsvFormatProvider(itemClass), Collections.emptyMap());
            String header = splitLines(csv).get(0);

            assertTrue(header.contains("id"), header);
            assertTrue(header.contains("label"), header);
            assertTrue(header.contains("count"), header);
            assertTrue(header.contains("amount"), header);
            assertTrue(header.contains("ratio"), header);
            assertTrue(header.contains("enabled"), header);
        }

        @Test
        @DisplayName("type row carries default SQL types for each EAttribute")
        void typeRowHasSqlTypes() throws IOException {
            String csv = save(createItem(), new CsvFormatProvider(itemClass), Collections.emptyMap());
            String typeRow = splitLines(csv).get(1);

            // id and label are EString -> VARCHAR; count -> INTEGER;
            // amount -> BIGINT; ratio -> DOUBLE; enabled -> BOOLEAN.
            assertTrue(typeRow.contains("VARCHAR"), typeRow);
            assertTrue(typeRow.contains("INTEGER"), typeRow);
            assertTrue(typeRow.contains("BIGINT"), typeRow);
            assertTrue(typeRow.contains("DOUBLE"), typeRow);
            assertFalse(typeRow.contains("DOUBLE PRECISION"),
                    () -> "EDouble default should be the single-word DOUBLE (daanse-compat): " + typeRow);
            assertTrue(typeRow.contains("BOOLEAN"), typeRow);
        }

        @Test
        @DisplayName("data row carries the attribute values")
        void dataRowHasValues() throws IOException {
            String csv = save(createItem(), new CsvFormatProvider(itemClass), Collections.emptyMap());
            String dataRow = splitLines(csv).get(2);

            assertTrue(dataRow.contains("item-001"), dataRow);
            assertTrue(dataRow.contains("Hello World"), dataRow);
            assertTrue(dataRow.contains("42"), dataRow);
            assertTrue(dataRow.contains("9876543210"), dataRow);
            assertTrue(dataRow.contains("3.14"), dataRow);
            assertTrue(dataRow.contains("true"), dataRow);
        }

        @Test
        @DisplayName("header / type / data rows have the same number of columns")
        void rowsAreAligned() throws IOException {
            String csv = save(createItem(), new CsvFormatProvider(itemClass), Collections.emptyMap());
            List<String> lines = splitLines(csv);

            int headerCols = countColumns(lines.get(0));
            int typeCols = countColumns(lines.get(1));
            int dataCols = countColumns(lines.get(2));

            assertEquals(headerCols, typeCols, "type row column count");
            assertEquals(headerCols, dataCols, "data row column count");
        }

        private int countColumns(String row) {
            // Naive count (no embedded commas in test data) — counts ',' + 1.
            int commas = 0;
            for (int i = 0; i < row.length(); i++) {
                if (row.charAt(i) == ',') {
                    commas++;
                }
            }
            return commas + 1;
        }
    }

    @Nested
    @DisplayName("dataTypeInSecondRow option")
    class TypeRowToggle {

        @Test
        @DisplayName("default emits the SQL-type row (3 rows)")
        void defaultEmitsTypeRow() throws IOException {
            String csv = save(createItem(), new CsvFormatProvider(itemClass), Collections.emptyMap());
            List<String> lines = splitLines(csv);
            assertEquals(3, lines.size(), () -> "default should keep the type row:\n" + csv);
            assertTrue(lines.get(1).contains("VARCHAR"), () -> "row 1 should be the type row: " + lines.get(1));
        }

        @Test
        @DisplayName("dataTypeInSecondRow=false drops the type row (header + data only)")
        void falseDropsTypeRow() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecCsvOptions.OPTION_DATA_TYPE_IN_SECOND_ROW, false);
            String csv = save(createItem(), new CsvFormatProvider(itemClass, options), options);
            List<String> lines = splitLines(csv);

            assertEquals(2, lines.size(), () -> "type row should be omitted:\n" + csv);
            assertTrue(lines.get(0).contains("id"), () -> "row 0 is the header: " + lines.get(0));
            // Row 1 is now the data row, not the SQL-type row.
            assertTrue(lines.get(1).contains("item-001"), () -> "row 1 should be data: " + lines.get(1));
            assertFalse(lines.get(1).contains("VARCHAR"), () -> "row 1 must not be the type row: " + lines.get(1));
        }

        @Test
        @DisplayName("dataTypeInSecondRow=\"false\" (String) also drops the type row")
        void stringFalseDropsTypeRow() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecCsvOptions.OPTION_DATA_TYPE_IN_SECOND_ROW, "false");
            String csv = save(createItem(), new CsvFormatProvider(itemClass, options), options);
            assertEquals(2, splitLines(csv).size(), () -> "string 'false' should omit the type row:\n" + csv);
        }
    }

    @Nested
    @DisplayName("columnTypes override")
    class ColumnTypesOverride {

        @Test
        @DisplayName("overrides VARCHAR with VARCHAR(255) for a named feature")
        void overridesByName() throws IOException {
            Map<String, Object> options = Map.of(
                    CodecTabularOptions.OPTION_COLUMN_TYPES,
                    Map.of("label", "VARCHAR(255)", "amount", "DECIMAL(15,2)"));

            CsvFormatProvider provider = new CsvFormatProvider(itemClass, options);
            String csv = save(createItem(), provider, options);
            String typeRow = splitLines(csv).get(1);

            assertTrue(typeRow.contains("VARCHAR(255)"), typeRow);
            assertTrue(typeRow.contains("DECIMAL(15,2)"), typeRow);
        }
    }

    @Nested
    @DisplayName("Stateless provider (auto-discovery)")
    class StatelessProvider {

        @Test
        @DisplayName("auto-discovers root EClass from contents at save time")
        void autoDiscoversEClassFromContents() throws IOException {
            // No EClass passed to the constructor — the codec should pull it
            // from the EObject at save time and produce the correct SQL types.
            CsvFormatProvider provider = new CsvFormatProvider();
            String csv = save(createItem(), provider, Collections.emptyMap());
            String typeRow = splitLines(csv).get(1);

            assertNotNull(typeRow);
            assertTrue(typeRow.contains("VARCHAR"), typeRow);            // id, label
            assertTrue(typeRow.contains("INTEGER"), typeRow);            // count
            assertTrue(typeRow.contains("BIGINT"), typeRow);             // amount
            assertTrue(typeRow.contains("DOUBLE"), typeRow);             // ratio
            assertTrue(typeRow.contains("BOOLEAN"), typeRow);            // enabled
        }

        @Test
        @DisplayName("save-time columnTypes option overrides default mapping")
        void saveTimeColumnTypesApplied() throws IOException {
            CsvFormatProvider provider = new CsvFormatProvider();

            Map<String, Object> saveOptions = Map.of(
                    CodecTabularOptions.OPTION_COLUMN_TYPES,
                    Map.of("label", "VARCHAR(255)", "amount", "DECIMAL(15,2)"));

            String csv = save(createItem(), provider, saveOptions);
            String typeRow = splitLines(csv).get(1);

            assertTrue(typeRow.contains("VARCHAR(255)"), typeRow);
            assertTrue(typeRow.contains("DECIMAL(15,2)"), typeRow);
        }
    }
}
