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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

/**
 * End-to-end tests for {@link OdsRenderer} via {@link OdsFormatProvider}.
 * <p>
 * Loads the produced ODS back through the {@code sods} library and asserts on
 * the {@link SpreadSheet} model — same approach the gecko exporter tests used.
 */
@DisplayName("ODS renderer")
class OdsRendererTest {

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
        OdsFormatProvider provider = new OdsFormatProvider();
        CodecResource resource = new CodecResource(
                URI.createURI("test://out.ods"),
                metadataService, ConfigurationResolver.defaults(),
                null, null, provider);
        resource.getContents().addAll(roots);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options == null ? new HashMap<>() : new HashMap<>(options));
        return out.toByteArray();
    }

    private SpreadSheet load(byte[] bytes) throws IOException {
        return new SpreadSheet(new ByteArrayInputStream(bytes));
    }

    /** Extracts the {@code content.xml} entry from an ODS (zip) byte array. */
    private static String contentXml(byte[] odsBytes) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(odsBytes))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if ("content.xml".equals(entry.getName())) {
                    return new String(zip.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
        }
        throw new AssertionError("content.xml not found in ODS output");
    }

    private static Sheet sheetByName(SpreadSheet doc, String name) {
        for (Sheet s : doc.getSheets()) {
            if (name.equals(s.getName())) {
                return s;
            }
        }
        return null;
    }

    private static int columnIndex(Sheet sheet, String header) {
        int cols = sheet.getMaxColumns();
        for (int c = 0; c < cols; c++) {
            Object v = sheet.getRange(0, c).getValue();
            if (header.equals(String.valueOf(v))) {
                return c;
            }
        }
        return -1;
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
        SpreadSheet doc = load(bytes);

        assertEquals(1, doc.getSheets().size(),
                () -> "Exactly one sheet expected; got " + sheetNames(doc));
        Sheet sheet = doc.getSheets().get(0);
        assertEquals("Product", sheet.getName());

        int idColIdx = columnIndex(sheet, "productId");
        int nameColIdx = columnIndex(sheet, "name");
        assertTrue(idColIdx >= 0 && nameColIdx >= 0,
                "productId and name columns should be present");
        assertEquals("p-001", String.valueOf(sheet.getRange(1, idColIdx).getValue()));
        assertEquals("Widget", String.valueOf(sheet.getRange(1, nameColIdx).getValue()));
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
        SpreadSheet doc = load(bytes);

        assertEquals(1, doc.getSheets().size(),
                () -> "Exactly one sheet expected; got " + sheetNames(doc));
        Sheet sheet = doc.getSheets().get(0);
        assertEquals("Warehouse", sheet.getName());

        int featuredIdIdx = columnIndex(sheet, "featured.productId");
        int featuredNameIdx = columnIndex(sheet, "featured.name");
        assertTrue(featuredIdIdx >= 0, "featured.productId missing");
        assertTrue(featuredNameIdx >= 0, "featured.name missing");
        assertEquals("p-001", String.valueOf(sheet.getRange(1, featuredIdIdx).getValue()));
        assertEquals("Featured Widget",
                String.valueOf(sheet.getRange(1, featuredNameIdx).getValue()));
    }

    // ========================================================================
    // SQL_TABLES — one sheet per EClass + FK columns
    // ========================================================================

    @Test
    @DisplayName("SQL_TABLES mode: one sheet per visited EClass with FK column linking them")
    void sqlTablesEmitsPerClassSheets() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme Warehouse", product, List.of());

        // Links off so the FK stays a numeric value this test can assert on; the
        // linking behaviour has dedicated tests below.
        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
        options.put(CodecOdsOptions.OPTION_GENERATE_LINKS, false);

        byte[] bytes = save(List.of(warehouse), options);
        SpreadSheet doc = load(bytes);

        Sheet warehouseSheet = sheetByName(doc, "Warehouse");
        Sheet productSheet = sheetByName(doc, "Product");
        assertNotNull(warehouseSheet, () -> "Warehouse sheet missing; have " + sheetNames(doc));
        assertNotNull(productSheet, () -> "Product sheet missing; have " + sheetNames(doc));

        // Warehouse sheet: _id, name, featured_id
        int wPkIdx = columnIndex(warehouseSheet, "_id");
        int wNameIdx = columnIndex(warehouseSheet, "name");
        int wFkIdx = columnIndex(warehouseSheet, "featured_id");
        assertTrue(wPkIdx >= 0, "_id missing on Warehouse");
        assertTrue(wNameIdx >= 0, "name missing on Warehouse");
        assertTrue(wFkIdx >= 0, "featured_id missing on Warehouse");

        // FK and PK should be numeric. sods exposes whole numbers as Integer or Long.
        Object fkValue = warehouseSheet.getRange(1, wFkIdx).getValue();
        assertEquals(1L, asLong(fkValue),
                () -> "Warehouse.featured_id should point to Product._id=1; got " + fkValue);

        // Product sheet: _id and the original attributes.
        int pPkIdx = columnIndex(productSheet, "_id");
        int pIdAttrIdx = columnIndex(productSheet, "productId");
        int pNameIdx = columnIndex(productSheet, "name");
        assertTrue(pPkIdx >= 0 && pIdAttrIdx >= 0 && pNameIdx >= 0,
                "Product sheet should have _id, productId, name columns");

        assertEquals(1L, asLong(productSheet.getRange(1, pPkIdx).getValue()),
                "first Product should have _id=1");
        assertEquals("p-001", String.valueOf(productSheet.getRange(1, pIdAttrIdx).getValue()));
        assertEquals("Widget", String.valueOf(productSheet.getRange(1, pNameIdx).getValue()));
    }

    @Test
    @DisplayName("SQL_TABLES: multi-valued ref appears as FK column on the child by default")
    void sqlTablesMultiValuedFkOnChild() throws IOException {
        EObject p1 = createProduct("p-001", "A");
        EObject p2 = createProduct("p-002", "B");
        EObject warehouse = createWarehouse("Acme", null, List.of(p1, p2));

        // Links off so the FK column stays numeric for this structural assertion.
        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
        options.put(CodecOdsOptions.OPTION_GENERATE_LINKS, false);

        byte[] bytes = save(List.of(warehouse), options);
        SpreadSheet doc = load(bytes);

        Sheet productSheet = sheetByName(doc, "Product");
        assertNotNull(productSheet, () -> "Product sheet missing; have " + sheetNames(doc));

        // FK-on-child column is named after the parent EClass (Warehouse → warehouse_id),
        // not after the reference (items_id) — matches CSV's PREFER_FK_COLUMN behaviour.
        int childFkIdx = columnIndex(productSheet, "warehouse_id");
        assertTrue(childFkIdx >= 0,
                () -> "warehouse_id FK column missing on Product; headers: "
                        + headersOf(productSheet));
        assertEquals(1L, asLong(productSheet.getRange(1, childFkIdx).getValue()));
        assertEquals(1L, asLong(productSheet.getRange(2, childFkIdx).getValue()));
    }

    // ========================================================================
    // SQL_TABLES — cross-table hyperlinks (FkCell → target sheet)
    // ========================================================================

    @Test
    @DisplayName("SQL_TABLES: FK cells link to the target sheet by default")
    void sqlTablesGeneratesLinksByDefault() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme Warehouse", product, List.of());

        // No generateLinks option -> default true.
        byte[] bytes = save(List.of(warehouse), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES));

        // Links are written but the sods reader does not parse them back, so we
        // assert on the raw content.xml rather than a reloaded SpreadSheet.
        String content = contentXml(bytes);
        assertTrue(content.contains("#Product.A1"),
                () -> "expected a hyperlink to '#Product.A1' in content.xml");
        assertTrue(content.contains("text:a") || content.contains(":a "),
                "expected a text:a hyperlink element in content.xml");
    }

    @Test
    @DisplayName("SQL_TABLES: generateLinks=false keeps FK numeric and emits no links")
    void generateLinksFalseSuppressesLinks() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme Warehouse", product, List.of());

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
        options.put(CodecOdsOptions.OPTION_GENERATE_LINKS, false);

        byte[] bytes = save(List.of(warehouse), options);

        String content = contentXml(bytes);
        assertFalse(content.contains("#Product.A1"),
                "no sheet hyperlink expected when generateLinks=false");

        // FK round-trips as a numeric value when not linked.
        SpreadSheet doc = load(bytes);
        Sheet warehouseSheet = sheetByName(doc, "Warehouse");
        assertNotNull(warehouseSheet, () -> "Warehouse sheet missing; have " + sheetNames(doc));
        int wFkIdx = columnIndex(warehouseSheet, "featured_id");
        assertTrue(wFkIdx >= 0, "featured_id missing on Warehouse");
        assertEquals(1L, asLong(warehouseSheet.getRange(1, wFkIdx).getValue()));
    }

    @Test
    @DisplayName("schema prefix is honoured in the link target sheet name")
    void linkTargetUsesSchemaPrefixedSheetName() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
        options.put(CodecTabularOptions.OPTION_SCHEMAS, Map.of(testPackage, "hr"));

        byte[] bytes = save(List.of(warehouse), options);

        String content = contentXml(bytes);
        assertTrue(content.contains("#hr.Product.A1"),
                () -> "expected link target '#hr.Product.A1' in content.xml");
    }

    @Test
    @DisplayName("schema option becomes a sheet-name prefix")
    void schemaOptionBecomesSheetPrefix() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
        options.put(CodecTabularOptions.OPTION_SCHEMAS,
                Map.of(testPackage, "hr"));

        byte[] bytes = save(List.of(warehouse), options);
        SpreadSheet doc = load(bytes);

        assertNotNull(sheetByName(doc, "hr.Warehouse"),
                () -> "hr.Warehouse missing; have " + sheetNames(doc));
        assertNotNull(sheetByName(doc, "hr.Product"),
                () -> "hr.Product missing; have " + sheetNames(doc));
        assertFalse(sheetNames(doc).contains("Warehouse"),
                () -> "unprefixed Warehouse should not appear: " + sheetNames(doc));
    }

    @Test
    @DisplayName("empty resource produces no output")
    void emptyResourceIsAllowed() throws IOException {
        byte[] bytes = save(List.of(), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.IGNORE));
        // CodecResource short-circuits an empty contents list; the OutputStream stays empty.
        assertEquals(0, bytes.length, "empty resource yields no bytes");
    }

    // ========================================================================
    // Small utilities
    // ========================================================================

    private static long asLong(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (value instanceof String s) {
            return Long.parseLong(s.trim());
        }
        throw new AssertionError("Not a numeric value: " + value
                + " (" + (value == null ? "null" : value.getClass().getName()) + ")");
    }

    private static List<String> sheetNames(SpreadSheet doc) {
        return doc.getSheets().stream().map(Sheet::getName).toList();
    }

    private static List<String> headersOf(Sheet sheet) {
        int cols = sheet.getMaxColumns();
        java.util.ArrayList<String> headers = new java.util.ArrayList<>(cols);
        for (int c = 0; c < cols; c++) {
            headers.add(String.valueOf(sheet.getRange(0, c).getValue()));
        }
        return headers;
    }
}
