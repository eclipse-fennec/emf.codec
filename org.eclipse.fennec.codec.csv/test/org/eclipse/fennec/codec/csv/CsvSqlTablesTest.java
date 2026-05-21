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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;

/**
 * End-to-end test for {@link CsvSqlTablesDelegate} (SQL_TABLES reference mode).
 * <p>
 * Uses {@code Warehouse} (root) with a non-containment reference to {@code Product}
 * — exercises the FK-column mechanic with two distinct EClasses.
 */
@DisplayName("CSV SQL_TABLES reference mode")
class CsvSqlTablesTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass warehouseClass;
    private EAttribute warehouseNameAttr;
    private EReference featuredRef;

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

    private EObject createWarehouse(String name, EObject featured) {
        EObject w = testPackage.getEFactoryInstance().create(warehouseClass);
        w.eSet(warehouseNameAttr, name);
        if (featured != null) {
            w.eSet(featuredRef, featured);
        }
        return w;
    }

    private byte[] save(EObject root, Map<String, Object> options) throws IOException {
        CsvFormatProvider provider = new CsvFormatProvider();
        CodecResource resource = new CodecResource(
                URI.createURI("test://warehouse.csvz"),
                metadataService, ConfigurationResolver.defaults(),
                null, null, provider);
        resource.getContents().add(root);

        Map<String, Object> effective = new HashMap<>(options);
        effective.putIfAbsent(CodecTabularOptions.OPTION_REFERENCE_MODE,
                ReferenceMode.SQL_TABLES);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, effective);
        return out.toByteArray();
    }

    /** Returns a map of zip-entry-name → CSV text. */
    private Map<String, String> unzip(byte[] zipBytes) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                byte[] tmp = new byte[2048];
                int n;
                while ((n = zip.read(tmp)) >= 0) {
                    buf.write(tmp, 0, n);
                }
                result.put(entry.getName(), buf.toString(StandardCharsets.UTF_8));
            }
        }
        return result;
    }

    /** Parses a CSV string into a list of records (each record is a list of fields). */
    private List<List<String>> parseCsv(String csv) {
        List<List<String>> records = new ArrayList<>();
        try (CsvReader<CsvRecord> reader = CsvReader.builder().ofCsvRecord(new StringReader(csv))) {
            for (CsvRecord r : reader) {
                records.add(r.getFields());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return records;
    }

    @Test
    @DisplayName("emits two CSVs in a ZIP, with FK linking from Warehouse to Product")
    void emitsTwoCsvsWithFkLink() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme Warehouse", product);

        byte[] zipBytes = save(warehouse, Map.of());
        Map<String, String> contents = unzip(zipBytes);

        assertTrue(contents.containsKey("Warehouse.csv"),
                () -> "ZIP should contain Warehouse.csv; got " + contents.keySet());
        assertTrue(contents.containsKey("Product.csv"),
                () -> "ZIP should contain Product.csv; got " + contents.keySet());
        assertEquals(2, contents.size(),
                () -> "Exactly 2 CSV entries expected; got " + contents.keySet());

        // --- Warehouse.csv ---
        List<List<String>> warehouseRows = parseCsv(contents.get("Warehouse.csv"));
        assertEquals(3, warehouseRows.size(), "header + type + 1 data row");
        List<String> wHeader = warehouseRows.get(0);
        List<String> wTypes = warehouseRows.get(1);
        List<String> wData = warehouseRows.get(2);

        assertTrue(wHeader.contains("_id"), wHeader::toString);
        assertTrue(wHeader.contains("name"), wHeader::toString);
        assertTrue(wHeader.contains("featured_id"), wHeader::toString);

        int featuredIdIdx = wHeader.indexOf("featured_id");
        assertEquals("BIGINT", wTypes.get(featuredIdIdx), "featured_id type should be BIGINT");

        String warehouseFkValue = wData.get(featuredIdIdx);
        assertNotNull(warehouseFkValue);
        assertEquals("1", warehouseFkValue,
                "Warehouse.featured_id should point to the first Product (_id=1)");

        // --- Product.csv ---
        List<List<String>> productRows = parseCsv(contents.get("Product.csv"));
        assertEquals(3, productRows.size(), "header + type + 1 data row");
        List<String> pHeader = productRows.get(0);
        List<String> pData = productRows.get(2);

        assertEquals("_id", pHeader.get(0));
        int productIdColIdx = pHeader.indexOf("_id");
        assertEquals("1", pData.get(productIdColIdx),
                "Product._id should match the FK referenced by Warehouse");

        assertTrue(pHeader.contains("productId"), pHeader::toString);
        assertTrue(pHeader.contains("name"), pHeader::toString);
        assertTrue(pData.contains("p-001"), pData::toString);
        assertTrue(pData.contains("Widget"), pData::toString);
    }

    @Test
    @DisplayName("emits a single Warehouse table when no reference is set")
    void emitsSingleTableWithoutReference() throws IOException {
        EObject warehouse = createWarehouse("Empty Warehouse", null);

        byte[] zipBytes = save(warehouse, Map.of());
        Map<String, String> contents = unzip(zipBytes);

        assertEquals(1, contents.size(),
                () -> "Only Warehouse.csv expected; got " + contents.keySet());
        assertTrue(contents.containsKey("Warehouse.csv"));

        List<List<String>> rows = parseCsv(contents.get("Warehouse.csv"));
        List<String> data = rows.get(2);
        int idx = rows.get(0).indexOf("featured_id");
        // The column still exists in the header (feature exists on the EClass),
        // but the cell is empty since no Product is linked.
        assertEquals("", data.get(idx),
                () -> "featured_id should be empty when no reference is set: " + data);
    }
}
