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
 * Exercises the CSV codec with a {@link CodecResource} that holds multiple root
 * {@code EObject}s — covering both IGNORE (one CSV, many data rows) and
 * SQL_TABLES (one ZIP whose CSVs cover all reachable EObjects across all roots).
 */
@DisplayName("CSV writer with multiple root EObjects")
class CsvMultiRootTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-format-parity.ecore";
    private static final String SQL_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage parityPackage;
    private EPackage sqlPackage;
    private MetadataWhiteboard metadataService;

    private EClass itemClass;
    private EAttribute idAttr;
    private EAttribute labelAttr;
    private EAttribute countAttr;

    private EClass productClass;
    private EAttribute productIdAttr;
    private EAttribute productNameAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        parityPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
        sqlPackage = ecoreHelper.loadEcore(SQL_ECORE, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(parityPackage.getNsURI(), parityPackage);
        EPackage.Registry.INSTANCE.put(sqlPackage.getNsURI(), sqlPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(parityPackage);
        metadataService.registerPackage(sqlPackage);

        itemClass = EcoreHelper.getEClass(parityPackage, "Item");
        idAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "id");
        labelAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "label");
        countAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "count");

        productClass = EcoreHelper.getEClass(sqlPackage, "Product");
        productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(parityPackage.getNsURI());
        EPackage.Registry.INSTANCE.remove(sqlPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject createItem(String id, String label, int count) {
        EObject item = parityPackage.getEFactoryInstance().create(itemClass);
        item.eSet(idAttr, id);
        item.eSet(labelAttr, label);
        item.eSet(countAttr, count);
        return item;
    }

    private EObject createProduct(String id, String name) {
        EObject p = sqlPackage.getEFactoryInstance().create(productClass);
        p.eSet(productIdAttr, id);
        p.eSet(productNameAttr, name);
        return p;
    }

    private byte[] saveAll(List<EObject> roots, Map<String, Object> options, String uri) throws IOException {
        CsvFormatProvider provider = new CsvFormatProvider();
        CodecResource resource = new CodecResource(
                URI.createURI(uri),
                metadataService, ConfigurationResolver.defaults(),
                null, null, provider);
        resource.getContents().addAll(roots);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return out.toByteArray();
    }

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

    // ========================================================================
    // Tests
    // ========================================================================

    @Test
    @DisplayName("IGNORE: N same-EClass roots produce 2 + N rows in one CSV")
    void ignoreModeEmitsOneRowPerRoot() throws IOException {
        List<EObject> roots = List.of(
                createItem("a", "Alpha", 1),
                createItem("b", "Beta", 2),
                createItem("c", "Gamma", 3));

        byte[] bytes = saveAll(roots, new HashMap<>(), "test://items.csv");
        List<List<String>> rows = parseCsv(new String(bytes, StandardCharsets.UTF_8));

        assertEquals(2 + roots.size(), rows.size(),
                () -> "Expected header + types + 3 data rows; got " + rows.size());

        List<String> header = rows.get(0);
        assertTrue(header.contains("id"), header::toString);
        assertTrue(header.contains("label"), header::toString);
        assertTrue(header.contains("count"), header::toString);

        int idIdx = header.indexOf("id");
        int labelIdx = header.indexOf("label");
        int countIdx = header.indexOf("count");

        assertEquals("a", rows.get(2).get(idIdx));
        assertEquals("Alpha", rows.get(2).get(labelIdx));
        assertEquals("1", rows.get(2).get(countIdx));

        assertEquals("b", rows.get(3).get(idIdx));
        assertEquals("Beta", rows.get(3).get(labelIdx));
        assertEquals("2", rows.get(3).get(countIdx));

        assertEquals("c", rows.get(4).get(idIdx));
        assertEquals("Gamma", rows.get(4).get(labelIdx));
        assertEquals("3", rows.get(4).get(countIdx));
    }

    @Test
    @DisplayName("SQL_TABLES: multiple disconnected roots all land in the ZIP")
    void sqlTablesModeWalksAllRoots() throws IOException {
        List<EObject> roots = List.of(
                createProduct("p-001", "Widget"),
                createProduct("p-002", "Sprocket"),
                createProduct("p-003", "Gizmo"));

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);

        byte[] zipBytes = saveAll(roots, options, "test://products.csvz");
        Map<String, String> contents = unzip(zipBytes);

        assertEquals(1, contents.size(),
                () -> "Single Product.csv expected; got " + contents.keySet());
        assertTrue(contents.containsKey("Product.csv"));

        List<List<String>> rows = parseCsv(contents.get("Product.csv"));
        // header + types + 3 data rows
        assertEquals(2 + roots.size(), rows.size(),
                () -> "Expected header + types + 3 data rows; got " + rows.size());

        List<String> header = rows.get(0);
        int idColIdx = header.indexOf("_id");
        int productIdColIdx = header.indexOf("productId");

        assertEquals("1", rows.get(2).get(idColIdx));
        assertEquals("p-001", rows.get(2).get(productIdColIdx));

        assertEquals("2", rows.get(3).get(idColIdx));
        assertEquals("p-002", rows.get(3).get(productIdColIdx));

        assertEquals("3", rows.get(4).get(idColIdx));
        assertEquals("p-003", rows.get(4).get(productIdColIdx));
    }
}
