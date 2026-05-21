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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.eclipse.fennec.codec.tabular.model.tabular.MultiValuedRefStrategy;
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
 * Exercises {@link CsvSqlTablesDelegate} handling of multi-valued {@code EReference}s
 * under the two strategies exposed via
 * {@link CodecCsvOptions#OPTION_MULTI_VALUED_REF_STRATEGY}.
 * <p>
 * Covers:
 * <ul>
 *   <li>PREFER_FK_COLUMN (default): multi-valued containment → FK column on child;
 *       multi-valued non-containment → join-table CSV.</li>
 *   <li>ALWAYS_JOIN_TABLE: every multi-valued reference → join-table CSV.</li>
 * </ul>
 */
@DisplayName("CSV SQL_TABLES multi-valued reference handling")
class CsvSqlTablesMultiRefTest {

    private static final String FEATURES_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private final EcoreHelper ecoreHelper = new EcoreHelper();
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

    private byte[] save(List<EObject> roots, MetadataWhiteboard whiteboard,
            Map<String, Object> options) throws IOException {
        CsvFormatProvider provider = new CsvFormatProvider();
        CodecResource resource = new CodecResource(
                URI.createURI("test://multiref.csvz"),
                whiteboard, ConfigurationResolver.defaults(),
                null, null, provider);
        resource.getContents().addAll(roots);
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
    // Multi-valued containment (Warehouse.items: Product*, containment)
    // ========================================================================

    @Test
    @DisplayName("PREFER_FK_COLUMN (default): multi-valued containment yields FK on child, no join-table")
    void preferFkOnChildForContainment() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EClass warehouseClass = EcoreHelper.getEClass(pkg, "Warehouse");
        EAttribute warehouseNameAttr = (EAttribute) EcoreHelper.getFeature(warehouseClass, "name");
        EReference itemsRef = (EReference) EcoreHelper.getFeature(warehouseClass, "items");
        EClass productClass = EcoreHelper.getEClass(pkg, "Product");
        EAttribute productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        EAttribute productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");

        EObject p1 = pkg.getEFactoryInstance().create(productClass);
        p1.eSet(productIdAttr, "p-001");
        p1.eSet(productNameAttr, "Alpha");
        EObject p2 = pkg.getEFactoryInstance().create(productClass);
        p2.eSet(productIdAttr, "p-002");
        p2.eSet(productNameAttr, "Beta");

        EObject warehouse = pkg.getEFactoryInstance().create(warehouseClass);
        warehouse.eSet(warehouseNameAttr, "Acme");
        @SuppressWarnings("unchecked")
        List<EObject> items = (List<EObject>) warehouse.eGet(itemsRef);
        items.add(p1);
        items.add(p2);

        // Default strategy is PREFER_FK_COLUMN — no option needed.
        Map<String, String> zipContents = unzip(save(List.of(warehouse), whiteboard, null));

        assertNull(zipContents.get("warehouse_items.csv"),
                () -> "No join-table CSV expected in PREFER_FK_COLUMN mode: " + zipContents.keySet());

        List<List<String>> productRows = parseCsv(zipContents.get("Product.csv"));
        List<String> header = productRows.get(0);
        assertTrue(header.contains("warehouse_id"),
                () -> "Product.csv should have a 'warehouse_id' FK column: " + header);

        int warehouseIdIdx = header.indexOf("warehouse_id");
        // Both Products were contained in the same Warehouse (pseudo-id 1).
        assertEquals("1", productRows.get(2).get(warehouseIdIdx),
                () -> "first Product row's warehouse_id: " + productRows.get(2));
        assertEquals("1", productRows.get(3).get(warehouseIdIdx),
                () -> "second Product row's warehouse_id: " + productRows.get(3));
    }

    @Test
    @DisplayName("ALWAYS_JOIN_TABLE: multi-valued containment yields a join-table CSV, no FK on child")
    void alwaysJoinTableForContainment() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EClass warehouseClass = EcoreHelper.getEClass(pkg, "Warehouse");
        EAttribute warehouseNameAttr = (EAttribute) EcoreHelper.getFeature(warehouseClass, "name");
        EReference itemsRef = (EReference) EcoreHelper.getFeature(warehouseClass, "items");
        EClass productClass = EcoreHelper.getEClass(pkg, "Product");
        EAttribute productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");

        EObject p1 = pkg.getEFactoryInstance().create(productClass);
        p1.eSet(productIdAttr, "p-001");
        EObject p2 = pkg.getEFactoryInstance().create(productClass);
        p2.eSet(productIdAttr, "p-002");

        EObject warehouse = pkg.getEFactoryInstance().create(warehouseClass);
        warehouse.eSet(warehouseNameAttr, "Acme");
        @SuppressWarnings("unchecked")
        List<EObject> items = (List<EObject>) warehouse.eGet(itemsRef);
        items.add(p1);
        items.add(p2);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_MULTI_VALUED_REF_STRATEGY,
                MultiValuedRefStrategy.ALWAYS_JOIN_TABLE);

        Map<String, String> zipContents = unzip(save(List.of(warehouse), whiteboard, options));

        // Join-table CSV present.
        String joinTable = zipContents.get("warehouse_items.csv");
        assertNotNull(joinTable, () -> "join-table CSV missing: " + zipContents.keySet());
        List<List<String>> joinRows = parseCsv(joinTable);
        assertEquals(4, joinRows.size(), "header + types + 2 data rows");
        assertEquals(List.of("warehouse_id", "items_id"), joinRows.get(0));
        assertEquals(List.of("BIGINT", "BIGINT"), joinRows.get(1));
        assertEquals(List.of("1", "1"), joinRows.get(2));
        assertEquals(List.of("1", "2"), joinRows.get(3));

        // Product.csv must NOT have a warehouse_id column in this mode.
        List<String> productHeader = parseCsv(zipContents.get("Product.csv")).get(0);
        assertFalse(productHeader.contains("warehouse_id"),
                () -> "ALWAYS_JOIN_TABLE should not also emit FK-on-child: " + productHeader);
    }

    // ========================================================================
    // Multi-valued non-containment (built programmatically)
    // ========================================================================

    @Test
    @DisplayName("PREFER_FK_COLUMN: multi-valued non-containment yields a join-table CSV")
    void preferFkButJoinTableForNonContainment() throws IOException {
        // Build a small EPackage: Person { name, friends: Person* (non-containment) }
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("socialpkg");
        pkg.setNsPrefix("sp");
        pkg.setNsURI("http://test/csv/sqltables/social");

        EClass personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        pkg.getEClassifiers().add(personClass);

        EAttribute nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(nameAttr);

        EReference friendsRef = EcoreFactory.eINSTANCE.createEReference();
        friendsRef.setName("friends");
        friendsRef.setEType(personClass);
        friendsRef.setUpperBound(-1);
        friendsRef.setContainment(false);
        personClass.getEStructuralFeatures().add(friendsRef);

        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        registered.add(pkg);
        MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
        whiteboard.registerPackage(pkg);

        EObject alice = pkg.getEFactoryInstance().create(personClass);
        alice.eSet(nameAttr, "Alice");
        EObject bob = pkg.getEFactoryInstance().create(personClass);
        bob.eSet(nameAttr, "Bob");
        EObject carol = pkg.getEFactoryInstance().create(personClass);
        carol.eSet(nameAttr, "Carol");
        @SuppressWarnings("unchecked")
        List<EObject> aliceFriends = (List<EObject>) alice.eGet(friendsRef);
        aliceFriends.add(bob);
        aliceFriends.add(carol);
        @SuppressWarnings("unchecked")
        List<EObject> bobFriends = (List<EObject>) bob.eGet(friendsRef);
        bobFriends.add(carol); // shared target: Carol is referenced by both Alice and Bob

        Map<String, String> zipContents = unzip(save(List.of(alice, bob, carol), whiteboard, null));

        // Person.csv must NOT have a person_id column (would have been the FK-on-child shape).
        List<String> personHeader = parseCsv(zipContents.get("Person.csv")).get(0);
        assertFalse(personHeader.contains("person_id"),
                () -> "non-containment must not use FK-on-child: " + personHeader);

        // Join-table CSV present with the expected shape.
        String joinTable = zipContents.get("person_friends.csv");
        assertNotNull(joinTable, () -> "join-table CSV missing: " + zipContents.keySet());
        List<List<String>> joinRows = parseCsv(joinTable);
        assertEquals(List.of("person_id", "friends_id"), joinRows.get(0));
        assertEquals(List.of("BIGINT", "BIGINT"), joinRows.get(1));
        // 3 link rows: (Alice→Bob), (Alice→Carol), (Bob→Carol)
        assertEquals(2 + 3, joinRows.size(), "header + types + 3 data rows");

        // Distinct second column from owner column verifies self-ref disambiguation works.
        assertFalse(joinRows.get(0).get(0).equals(joinRows.get(0).get(1)),
                () -> "owner and target columns must differ for self-ref: " + joinRows.get(0));
    }

    @Test
    @DisplayName("Default strategy (option absent) is PREFER_FK_COLUMN")
    void defaultStrategyIsPreferFk() throws IOException {
        MetadataWhiteboard whiteboard = loadAndRegister(FEATURES_ECORE);
        EPackage pkg = registered.get(0);
        EClass warehouseClass = EcoreHelper.getEClass(pkg, "Warehouse");
        EReference itemsRef = (EReference) EcoreHelper.getFeature(warehouseClass, "items");
        EClass productClass = EcoreHelper.getEClass(pkg, "Product");
        EAttribute productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");

        EObject product = pkg.getEFactoryInstance().create(productClass);
        product.eSet(productIdAttr, "p-001");

        EObject warehouse = pkg.getEFactoryInstance().create(warehouseClass);
        @SuppressWarnings("unchecked")
        List<EObject> items = (List<EObject>) warehouse.eGet(itemsRef);
        items.add(product);

        Map<String, String> zipContents = unzip(save(List.of(warehouse), whiteboard, Collections.emptyMap()));

        // No option set → FK-on-child path → no join table.
        assertNull(zipContents.get("warehouse_items.csv"),
                () -> "default mode should not emit a join table for containment: " + zipContents.keySet());
        assertTrue(parseCsv(zipContents.get("Product.csv")).get(0).contains("warehouse_id"),
                () -> "Product.csv should have warehouse_id column under default strategy");
    }
}
