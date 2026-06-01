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
package org.eclipse.fennec.codec.xlsx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.codec.tests.tck.AbstractCoreRoundTripTCK;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests for {@link XlsxRenderer} via {@link XlsxFormatProvider}.
 * <p>
 * Loads the produced XLSX back through Apache POI and asserts on the {@link XSSFWorkbook}.
 */
@DisplayName("XLSX renderer")
class XlsxRendererTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass warehouseClass;
    private EAttribute warehouseNameAttr;
    private EReference featuredRef;
    private EReference itemsRef;

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

        warehouseClass = EcoreHelper.getEClass(testPackage, "Warehouse");
        warehouseNameAttr = (EAttribute) EcoreHelper.getFeature(warehouseClass, "name");
        featuredRef = (EReference) EcoreHelper.getFeature(warehouseClass, "featured");
        itemsRef = (EReference) EcoreHelper.getFeature(warehouseClass, "items");

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

    private EObject createProduct(String id, String name) {
        EObject p = testPackage.getEFactoryInstance().create(productClass);
        p.eSet(productIdAttr, id);
        p.eSet(productNameAttr, name);
        return p;
    }

    private EObject createWarehouse(String name, EObject featured, List<EObject> items) {
        EObject w = testPackage.getEFactoryInstance().create(warehouseClass);
        w.eSet(warehouseNameAttr, name);
        if (featured != null) {
            w.eSet(featuredRef, featured);
        }
        if (items != null && !items.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<EObject> list = (List<EObject>) w.eGet(itemsRef);
            list.addAll(items);
        }
        return w;
    }

    private byte[] save(List<EObject> roots, Map<String, Object> options) throws IOException {
        XlsxFormatProvider provider = new XlsxFormatProvider();
        CodecResource resource = new CodecResource(
                URI.createURI("test://out.xlsx"),
                metadataService, ConfigurationResolver.defaults(),
                null, null, provider);
        resource.getContents().addAll(roots);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options == null ? new HashMap<>() : new HashMap<>(options));
        return out.toByteArray();
    }

    private XSSFWorkbook load(byte[] bytes) throws IOException {
        return new XSSFWorkbook(new ByteArrayInputStream(bytes));
    }

    private static int columnIndex(Sheet sheet, String header) {
        Row r = sheet.getRow(0);
        if (r == null) {
            return -1;
        }
        for (int c = r.getFirstCellNum(); c < r.getLastCellNum(); c++) {
            Cell cell = r.getCell(c);
            if (cell != null && header.equals(cell.getStringCellValue())) {
                return c;
            }
        }
        return -1;
    }

    private static List<String> headersOf(Sheet sheet) {
        Row r = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        if (r == null) {
            return headers;
        }
        for (int c = r.getFirstCellNum(); c < r.getLastCellNum(); c++) {
            Cell cell = r.getCell(c);
            headers.add(cell == null ? null : cell.getStringCellValue());
        }
        return headers;
    }

    private static List<String> sheetNames(XSSFWorkbook wb) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            names.add(wb.getSheetName(i));
        }
        return names;
    }

    // ========================================================================
    // IGNORE — single sheet, attributes only
    // ========================================================================

    @Test
    @DisplayName("IGNORE mode: one sheet for the root EClass with attribute columns only")
    void ignoreModeEmitsSingleSheet() throws IOException {
        EObject p = createProduct("p-001", "Widget");
        byte[] bytes = save(List.of(p), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.IGNORE));
        try (XSSFWorkbook wb = load(bytes)) {
            assertEquals(1, wb.getNumberOfSheets(),
                    () -> "Expected exactly one sheet; got " + sheetNames(wb));
            Sheet sheet = wb.getSheetAt(0);
            assertEquals("Product", sheet.getSheetName());

            int idIdx = columnIndex(sheet, "productId");
            int nameIdx = columnIndex(sheet, "name");
            assertTrue(idIdx >= 0 && nameIdx >= 0);
            assertEquals("p-001", sheet.getRow(1).getCell(idIdx).getStringCellValue());
            assertEquals("Widget", sheet.getRow(1).getCell(nameIdx).getStringCellValue());
        }
    }

    // ========================================================================
    // FLAT — single sheet, dotted columns
    // ========================================================================

    @Test
    @DisplayName("FLAT mode: dotted columns for nested references in a single sheet")
    void flatModeEmitsDottedColumns() throws IOException {
        EObject featured = createProduct("p-001", "Featured Widget");
        EObject warehouse = createWarehouse("Acme", featured, List.of());
        byte[] bytes = save(List.of(warehouse), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.FLAT));
        try (XSSFWorkbook wb = load(bytes)) {
            assertEquals(1, wb.getNumberOfSheets(),
                    () -> "Expected exactly one sheet; got " + sheetNames(wb));
            Sheet sheet = wb.getSheetAt(0);
            assertEquals("Warehouse", sheet.getSheetName());

            int featuredIdIdx = columnIndex(sheet, "featured.productId");
            int featuredNameIdx = columnIndex(sheet, "featured.name");
            assertTrue(featuredIdIdx >= 0, "featured.productId missing");
            assertTrue(featuredNameIdx >= 0, "featured.name missing");
            assertEquals("p-001", sheet.getRow(1).getCell(featuredIdIdx).getStringCellValue());
            assertEquals("Featured Widget",
                    sheet.getRow(1).getCell(featuredNameIdx).getStringCellValue());
        }
    }

    // ========================================================================
    // SQL_TABLES — sheets + FK + hyperlinks
    // ========================================================================

    @Test
    @DisplayName("SQL_TABLES mode: one sheet per EClass with FK column linking them")
    void sqlTablesEmitsPerClassSheets() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme Warehouse", product, List.of());

        byte[] bytes = save(List.of(warehouse), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES));
        try (XSSFWorkbook wb = load(bytes)) {
            Sheet warehouseSheet = wb.getSheet("Warehouse");
            Sheet productSheet = wb.getSheet("Product");
            assertNotNull(warehouseSheet,
                    () -> "Warehouse sheet missing; have " + sheetNames(wb));
            assertNotNull(productSheet,
                    () -> "Product sheet missing; have " + sheetNames(wb));

            int wFkIdx = columnIndex(warehouseSheet, "featured_id");
            assertTrue(wFkIdx >= 0, "featured_id missing on Warehouse");
            Cell fkCell = warehouseSheet.getRow(1).getCell(wFkIdx);
            assertEquals(1.0, fkCell.getNumericCellValue(),
                    "Warehouse.featured_id should point to Product._id=1");

            int pPkIdx = columnIndex(productSheet, "_id");
            assertTrue(pPkIdx >= 0);
            assertEquals(1.0, productSheet.getRow(1).getCell(pPkIdx).getNumericCellValue());
        }
    }

    @Test
    @DisplayName("FK cell carries a DOCUMENT hyperlink to the target sheet's A1 by default")
    void fkCellHasDocumentHyperlinkByDefault() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        byte[] bytes = save(List.of(warehouse), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES));
        try (XSSFWorkbook wb = load(bytes)) {
            Sheet warehouseSheet = wb.getSheet("Warehouse");
            int fkIdx = columnIndex(warehouseSheet, "featured_id");
            Cell fkCell = warehouseSheet.getRow(1).getCell(fkIdx);
            Hyperlink link = fkCell.getHyperlink();
            assertNotNull(link, "FK cell should have a hyperlink");
            assertEquals("'Product'!A1", link.getAddress(),
                    () -> "Hyperlink should target Product!A1; was " + link.getAddress());
        }
    }

    @Test
    @DisplayName("OPTION_GENERATE_LINKS=false disables FK hyperlinks but keeps the numeric value")
    void disableHyperlinks() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
        options.put(CodecXlsxOptions.OPTION_GENERATE_LINKS, Boolean.FALSE);

        byte[] bytes = save(List.of(warehouse), options);
        try (XSSFWorkbook wb = load(bytes)) {
            Sheet warehouseSheet = wb.getSheet("Warehouse");
            int fkIdx = columnIndex(warehouseSheet, "featured_id");
            Cell fkCell = warehouseSheet.getRow(1).getCell(fkIdx);
            assertNull(fkCell.getHyperlink(),
                    "FK cell should have no hyperlink when option is disabled");
            assertEquals(1.0, fkCell.getNumericCellValue(),
                    "Numeric FK value should still be present");
        }
    }

    @Test
    @DisplayName("schema option becomes a sheet-name prefix")
    void schemaOptionBecomesSheetPrefix() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
        options.put(CodecTabularOptions.OPTION_SCHEMAS, Map.of(testPackage, "hr"));

        byte[] bytes = save(List.of(warehouse), options);
        try (XSSFWorkbook wb = load(bytes)) {
            assertNotNull(wb.getSheet("hr.Warehouse"),
                    () -> "hr.Warehouse missing; have " + sheetNames(wb));
            assertNotNull(wb.getSheet("hr.Product"),
                    () -> "hr.Product missing; have " + sheetNames(wb));
            assertFalse(sheetNames(wb).contains("Warehouse"),
                    () -> "unprefixed Warehouse should not appear: " + sheetNames(wb));
        }
    }

    @Test
    @DisplayName("multi-valued ref: FK column on child sheet (named after parent EClass)")
    void sqlTablesMultiValuedFkOnChild() throws IOException {
        EObject p1 = createProduct("p-001", "A");
        EObject p2 = createProduct("p-002", "B");
        EObject warehouse = createWarehouse("Acme", null, List.of(p1, p2));

        byte[] bytes = save(List.of(warehouse), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES));
        try (XSSFWorkbook wb = load(bytes)) {
            Sheet productSheet = wb.getSheet("Product");
            assertNotNull(productSheet,
                    () -> "Product sheet missing; have " + sheetNames(wb));
            int childFkIdx = columnIndex(productSheet, "warehouse_id");
            assertTrue(childFkIdx >= 0,
                    () -> "warehouse_id FK column missing on Product; headers: "
                            + headersOf(productSheet));
            assertEquals(1.0, productSheet.getRow(1).getCell(childFkIdx).getNumericCellValue());
            assertEquals(1.0, productSheet.getRow(2).getCell(childFkIdx).getNumericCellValue());
        }
    }

    @Test
    @DisplayName("empty resource produces no output")
    void emptyResourceIsAllowed() throws IOException {
        byte[] bytes = save(List.of(), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.IGNORE));
        assertEquals(0, bytes.length, "empty resource yields no bytes");
    }
}
