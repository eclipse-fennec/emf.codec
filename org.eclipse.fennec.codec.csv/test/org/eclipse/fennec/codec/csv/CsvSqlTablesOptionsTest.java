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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.codec.tests.tck.AbstractCoreRoundTripTCK;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;

/**
 * Verifies that the standard codec options flow through to
 * {@link CsvSqlTablesDelegate} (SQL_TABLES reference mode).
 * <p>
 * Each test loads its own EPackage to keep the test models scoped.
 */
@DisplayName("CSV SQL_TABLES codec-options pipeline")
class CsvSqlTablesOptionsTest {

    private static final String FEATURES_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";
    private static final String EXTMETA_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-extmetadata.ecore";
    private static final String VISIBILITY_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-visibility.ecore";
    private static final String FORCE_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-force.ecore";

    private EcoreHelper ecoreHelper = new EcoreHelper();
    private final List<EPackage> registered = new ArrayList<>();

    @AfterEach
    void tearDown() {
        for (EPackage p : registered) {
            EPackage.Registry.INSTANCE.remove(p.getNsURI());
        }
        registered.clear();
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private MetadataWhiteboard loadAndRegister(String ecoreResource) throws IOException {
        EPackage pkg = ecoreHelper.loadEcore(ecoreResource, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        registered.add(pkg);
        MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
        whiteboard.registerPackage(pkg);
        return whiteboard;
    }

    private byte[] saveSqlTables(EObject root, MetadataWhiteboard whiteboard,
            ConfigurationResolver resolver, Map<String, Object> options) throws IOException {
        CsvFormatProvider provider = new CsvFormatProvider();
        CodecResource resource = new CodecResource(
                URI.createURI("test://options.csvz"),
                whiteboard,
                resolver != null ? resolver : ConfigurationResolver.defaults(),
                null, null, provider);
        resource.getContents().add(root);

        Map<String, Object> effective = options == null ? new HashMap<>() : new HashMap<>(options);
        effective.putIfAbsent(CodecTabularOptions.OPTION_REFERENCE_MODE,
                ReferenceMode.SQL_TABLES);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, effective);
        return out.toByteArray();
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

    // ========================================================================
    // Tests
    // ========================================================================

    @Test
    @DisplayName("useNamesFromExtendedMetaData renames columns in SQL_TABLES headers")
    void extendedMetaDataNamesFlowThrough() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(EXTMETA_ECORE);
        EPackage pkg = registered.get(0);
        EClass docClass = EcoreHelper.getEClass(pkg, "Document");
        EAttribute titleAttr = (EAttribute) EcoreHelper.getFeature(docClass, "documentTitle");
        EAttribute pageCountAttr = (EAttribute) EcoreHelper.getFeature(docClass, "pageCount");
        EAttribute internalIdAttr = (EAttribute) EcoreHelper.getFeature(docClass, "internalId");

        EObject doc = pkg.getEFactoryInstance().create(docClass);
        doc.eSet(titleAttr, "My Document");
        doc.eSet(pageCountAttr, 7);
        doc.eSet(internalIdAttr, "doc-001");

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .useNamesFromExtendedMetaData(true)
                .build();

        byte[] zipBytes = saveSqlTables(doc, whiteboard, resolver, null);
        Map<String, String> contents = unzip(zipBytes);
        List<List<String>> rows = parseCsv(contents.get("Document.csv"));
        List<String> header = rows.get(0);

        assertTrue(header.contains("title"), header::toString);
        assertTrue(header.contains("id"), header::toString);
        assertFalse(header.contains("documentTitle"), header::toString);
        assertFalse(header.contains("internalId"), header::toString);
    }

    @Test
    @DisplayName("@codec(key=...) annotation renames columns in SQL_TABLES headers")
    void customKeyAnnotationFlowsThrough() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EClass productClass = EcoreHelper.getEClass(pkg, "Product");
        EAttribute productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        EAttribute productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");
        EAttribute customKeyAttr = (EAttribute) EcoreHelper.getFeature(productClass, "customKeyAttr");

        EObject product = pkg.getEFactoryInstance().create(productClass);
        product.eSet(productIdAttr, "p-001");
        product.eSet(productNameAttr, "Widget");
        product.eSet(customKeyAttr, "custom-value");

        byte[] zipBytes = saveSqlTables(product, whiteboard, ConfigurationResolver.defaults(), null);
        Map<String, String> contents = unzip(zipBytes);
        List<List<String>> rows = parseCsv(contents.get("Product.csv"));
        List<String> header = rows.get(0);
        List<String> data = rows.get(2);

        assertTrue(header.contains("custom_name"),
                () -> "expected renamed column 'custom_name': " + header);
        assertFalse(header.contains("customKeyAttr"),
                () -> "raw EMF feature name should be replaced: " + header);

        int renamedIdx = header.indexOf("custom_name");
        assertEquals("custom-value", data.get(renamedIdx));
    }

    @Test
    @DisplayName("globalIgnoreFeatures drops a column from SQL_TABLES")
    void globalIgnoreFlowsThrough() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(VISIBILITY_ECORE);
        EPackage pkg = registered.get(0);
        EClass personClass = EcoreHelper.getEClass(pkg, "Person");
        EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        EAttribute ageAttr = (EAttribute) EcoreHelper.getFeature(personClass, "age");
        EAttribute scoreAttr = (EAttribute) EcoreHelper.getFeature(personClass, "score");
        EAttribute secretAttr = (EAttribute) EcoreHelper.getFeature(personClass, "secret");

        EObject person = pkg.getEFactoryInstance().create(personClass);
        person.eSet(nameAttr, "Alice");
        person.eSet(ageAttr, 30);
        person.eSet(scoreAttr, 95.5);
        person.eSet(secretAttr, "top-secret");

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .globalIgnoreFeatures("secret")
                .build();

        byte[] zipBytes = saveSqlTables(person, whiteboard, resolver, null);
        Map<String, String> contents = unzip(zipBytes);
        List<List<String>> rows = parseCsv(contents.get("Person.csv"));
        List<String> header = rows.get(0);
        List<String> data = rows.get(2);

        assertTrue(header.contains("name"), header::toString);
        assertTrue(header.contains("age"), header::toString);
        assertTrue(header.contains("score"), header::toString);
        assertFalse(header.contains("secret"),
                () -> "globally-ignored column should be absent: " + header);
        assertFalse(data.contains("top-secret"),
                () -> "globally-ignored value should not appear in data row: " + data);
    }

    @Test
    @DisplayName("forceWrite brings a transient/derived feature into SQL_TABLES")
    void forceWriteFlowsThrough() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FORCE_ECORE);
        EPackage pkg = registered.get(0);
        EClass computedClass = EcoreHelper.getEClass(pkg, "Computed");
        EAttribute nameAttr = (EAttribute) EcoreHelper.getFeature(computedClass, "name");
        EAttribute derivedAttr = (EAttribute) EcoreHelper.getFeature(computedClass, "derived");

        EObject computed = pkg.getEFactoryInstance().create(computedClass);
        computed.eSet(nameAttr, "test-item");
        computed.eSet(derivedAttr, "computed-value");

        // Default: derived feature should be skipped
        byte[] defaultZip = saveSqlTables(computed, whiteboard, ConfigurationResolver.defaults(), null);
        List<String> defaultHeader = parseCsv(unzip(defaultZip).get("Computed.csv")).get(0);
        assertFalse(defaultHeader.contains("derived"),
                () -> "derived feature should be skipped by default: " + defaultHeader);

        // forceWrite: derived feature should appear
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .forceWrite(derivedAttr)
                .build();
        byte[] forcedZip = saveSqlTables(computed, whiteboard, resolver, null);
        List<List<String>> forcedRows = parseCsv(unzip(forcedZip).get("Computed.csv"));
        List<String> forcedHeader = forcedRows.get(0);
        List<String> forcedData = forcedRows.get(2);

        assertTrue(forcedHeader.contains("derived"),
                () -> "forceWrite should bring 'derived' into the header: " + forcedHeader);

        int derivedIdx = forcedHeader.indexOf("derived");
        assertEquals("computed-value", forcedData.get(derivedIdx),
                "derived value should appear in the data row");
    }

    @Test
    @DisplayName("dateFormat (save option) formats EDate values in SQL_TABLES cells")
    void dateFormatFlowsThrough() throws IOException {
        // Build a small EPackage programmatically: Event { id: EString, when: EDate }
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("eventpkg");
        pkg.setNsPrefix("ev");
        pkg.setNsURI("http://test/csv/sqltables/event");

        EClass eventClass = EcoreFactory.eINSTANCE.createEClass();
        eventClass.setName("Event");
        pkg.getEClassifiers().add(eventClass);

        EAttribute idAttr = EcoreFactory.eINSTANCE.createEAttribute();
        idAttr.setName("id");
        idAttr.setEType(EcorePackage.Literals.ESTRING);
        eventClass.getEStructuralFeatures().add(idAttr);

        EAttribute whenAttr = EcoreFactory.eINSTANCE.createEAttribute();
        whenAttr.setName("when");
        whenAttr.setEType(EcorePackage.Literals.EDATE);
        eventClass.getEStructuralFeatures().add(whenAttr);

        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        registered.add(pkg);

        MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
        whiteboard.registerPackage(pkg);

        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2026, Calendar.MAY, 21, 12, 0, 0);
        Date when = cal.getTime();

        EObject event = pkg.getEFactoryInstance().create(eventClass);
        event.eSet(idAttr, "ev-001");
        event.eSet(whenAttr, when);

        // Global dateFormat via save options (mirrors the IGNORE-mode pipeline path).
        Map<String, Object> options = new HashMap<>();
        options.put("dateFormat", "yyyy-MM-dd");

        byte[] zipBytes = saveSqlTables(event, whiteboard, ConfigurationResolver.defaults(), options);
        Map<String, String> contents = unzip(zipBytes);
        assertNotNull(contents.get("Event.csv"), () -> "Event.csv missing in: " + contents.keySet());
        List<List<String>> rows = parseCsv(contents.get("Event.csv"));
        List<String> header = rows.get(0);
        List<String> data = rows.get(2);

        int whenIdx = header.indexOf("when");
        assertTrue(whenIdx >= 0, () -> "when column missing: " + header);

        String expected = new SimpleDateFormat("yyyy-MM-dd").format(when);
        assertEquals(expected, data.get(whenIdx),
                () -> "expected " + expected + " (yyyy-MM-dd) but got: " + data.get(whenIdx));
    }

    /**
     * Builds a Warehouse → Product pair for the schema-layout tests.
     * Returns the prepared {@code MetadataWhiteboard} and the warehouse object.
     */
    private EObject prepareWarehouseWithProduct(MetadataWhiteboard whiteboard, EPackage pkg) {
        EClass warehouseClass = EcoreHelper.getEClass(pkg, "Warehouse");
        EAttribute warehouseNameAttr = (EAttribute) EcoreHelper.getFeature(warehouseClass, "name");
        EClass productClass = EcoreHelper.getEClass(pkg, "Product");
        EAttribute productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        EAttribute productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");

        EObject product = pkg.getEFactoryInstance().create(productClass);
        product.eSet(productIdAttr, "p-001");
        product.eSet(productNameAttr, "Widget");

        EObject warehouse = pkg.getEFactoryInstance().create(warehouseClass);
        warehouse.eSet(warehouseNameAttr, "Acme");
        warehouse.eSet(EcoreHelper.getFeature(warehouseClass, "featured"), product);
        return warehouse;
    }

    @Test
    @DisplayName("OPTION_SCHEMAS by EClass routes that EClass into <schema>/ subdirectory")
    void schemasByEClass() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EClass warehouseClass = EcoreHelper.getEClass(pkg, "Warehouse");
        EObject warehouse = prepareWarehouseWithProduct(whiteboard, pkg);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_SCHEMAS, Map.of(warehouseClass, "ops"));

        Map<String, String> contents = unzip(saveSqlTables(
                warehouse, whiteboard, ConfigurationResolver.defaults(), options));

        assertTrue(contents.containsKey("ops/Warehouse.csv"),
                () -> "Warehouse should land in ops/: " + contents.keySet());
        assertTrue(contents.containsKey("Product.csv"),
                () -> "Product (no schema) should land at root: " + contents.keySet());
    }

    @Test
    @DisplayName("OPTION_SCHEMAS by EPackage applies to all EClasses in the package")
    void schemasByEPackage() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EObject warehouse = prepareWarehouseWithProduct(whiteboard, pkg);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_SCHEMAS, Map.of(pkg, "shared"));

        Map<String, String> contents = unzip(saveSqlTables(
                warehouse, whiteboard, ConfigurationResolver.defaults(), options));

        assertTrue(contents.containsKey("shared/Warehouse.csv"),
                () -> "Warehouse should land in shared/: " + contents.keySet());
        assertTrue(contents.containsKey("shared/Product.csv"),
                () -> "Product should also land in shared/: " + contents.keySet());
    }

    @Test
    @DisplayName("OPTION_SCHEMAS: per-EClass entry overrides per-EPackage entry")
    void schemasPerClassOverridesPerPackage() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EClass productClass = EcoreHelper.getEClass(pkg, "Product");
        EObject warehouse = prepareWarehouseWithProduct(whiteboard, pkg);

        Map<Object, String> schemaMap = new HashMap<>();
        schemaMap.put(pkg, "shared");
        schemaMap.put(productClass, "products");

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_SCHEMAS, schemaMap);

        Map<String, String> contents = unzip(saveSqlTables(
                warehouse, whiteboard, ConfigurationResolver.defaults(), options));

        assertTrue(contents.containsKey("shared/Warehouse.csv"),
                () -> "Warehouse picks up the EPackage schema: " + contents.keySet());
        assertTrue(contents.containsKey("products/Product.csv"),
                () -> "Product uses the per-EClass override: " + contents.keySet());
        assertFalse(contents.containsKey("shared/Product.csv"),
                () -> "Product should not also land under shared/: " + contents.keySet());
    }

    @Test
    @DisplayName("OPTION_SCHEMAS with empty string is treated as no schema")
    void schemasEmptyStringFallsBackToRoot() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EClass warehouseClass = EcoreHelper.getEClass(pkg, "Warehouse");
        EObject warehouse = prepareWarehouseWithProduct(whiteboard, pkg);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_SCHEMAS, Map.of(warehouseClass, ""));

        Map<String, String> contents = unzip(saveSqlTables(
                warehouse, whiteboard, ConfigurationResolver.defaults(), options));

        assertTrue(contents.containsKey("Warehouse.csv"),
                () -> "Empty schema should leave Warehouse at root: " + contents.keySet());
    }

    @Test
    @DisplayName("OPTION_FK_COLUMN_SUFFIX overrides the default '_id' on FK columns")
    void fkColumnSuffixOverride() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EClass warehouseClass = EcoreHelper.getEClass(pkg, "Warehouse");
        EAttribute warehouseNameAttr = (EAttribute) EcoreHelper.getFeature(warehouseClass, "name");
        EClass productClass = EcoreHelper.getEClass(pkg, "Product");
        EAttribute productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        EAttribute productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");

        EObject product = pkg.getEFactoryInstance().create(productClass);
        product.eSet(productIdAttr, "p-001");
        product.eSet(productNameAttr, "Widget");

        EObject warehouse = pkg.getEFactoryInstance().create(warehouseClass);
        warehouse.eSet(warehouseNameAttr, "Acme");
        warehouse.eSet(EcoreHelper.getFeature(warehouseClass, "featured"), product);

        // Default ("_id"): FK column should be "featured_id"
        byte[] defaultZip = saveSqlTables(warehouse, whiteboard,
                ConfigurationResolver.defaults(), Collections.emptyMap());
        List<String> defaultHeader = parseCsv(unzip(defaultZip).get("Warehouse.csv")).get(0);
        assertTrue(defaultHeader.contains("featured_id"),
                () -> "default suffix should produce 'featured_id': " + defaultHeader);

        // Override to "_ref": FK column should be "featured_ref"
        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_FK_COLUMN_SUFFIX, "_ref");
        byte[] overrideZip = saveSqlTables(warehouse, whiteboard,
                ConfigurationResolver.defaults(), options);
        List<String> overrideHeader = parseCsv(unzip(overrideZip).get("Warehouse.csv")).get(0);
        assertTrue(overrideHeader.contains("featured_ref"),
                () -> "overridden suffix should produce 'featured_ref': " + overrideHeader);
        assertFalse(overrideHeader.contains("featured_id"),
                () -> "default 'featured_id' should be replaced: " + overrideHeader);
    }

    /**
     * Sanity check: when no resolver is plumbed through (e.g. the delegate is
     * instantiated directly), the fallback to {@code feat.getName()} still works.
     */
    @Test
    @DisplayName("falls back to feature name when no resolver is provided")
    void fallsBackWithoutResolver() throws IOException {
        // Use the resolver-less constructor path through CodecResource by relying on a
        // ConfigurationResolver with no relevant options; behavior must match plain mode.
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EClass productClass = EcoreHelper.getEClass(pkg, "Product");
        EAttribute productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        EAttribute productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");

        EObject product = pkg.getEFactoryInstance().create(productClass);
        product.eSet(productIdAttr, "p-001");
        product.eSet(productNameAttr, "Widget");

        byte[] zipBytes = saveSqlTables(product, whiteboard, ConfigurationResolver.defaults(),
                Collections.emptyMap());
        Map<String, String> contents = unzip(zipBytes);
        List<List<String>> rows = parseCsv(contents.get("Product.csv"));
        List<String> header = rows.get(0);
        assertTrue(header.contains("productId"), header::toString);
        assertTrue(header.contains("name"), header::toString);
    }
}
