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
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.codec.tests.tck.AbstractCoreRoundTripTCK;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;

/**
 * End-to-end test for {@link CsvFlatDelegate} (FLAT reference mode).
 * <p>
 * Uses {@code Warehouse} (root) with a single-valued reference ({@code featured})
 * and a multi-valued containment reference ({@code items}) to {@code Product}, so
 * we cover both dotted (single-ref) and dotted-with-index (multi-ref) flattening.
 */
@DisplayName("CSV FLAT reference mode")
class CsvFlatTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass warehouseClass;
    private EAttribute warehouseNameAttr;
    private EReference itemsRef;
    private EReference featuredRef;

    private EClass productClass;
    private EAttribute productIdAttr;
    private EAttribute productNameAttr;
    private EAttribute productCustomKeyAttr;
    private EAttribute productTagsAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        warehouseClass = EcoreHelper.getEClass(testPackage, "Warehouse");
        warehouseNameAttr = (EAttribute) EcoreHelper.getFeature(warehouseClass, "name");
        itemsRef = (EReference) EcoreHelper.getFeature(warehouseClass, "items");
        featuredRef = (EReference) EcoreHelper.getFeature(warehouseClass, "featured");

        productClass = EcoreHelper.getEClass(testPackage, "Product");
        productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");
        productCustomKeyAttr = (EAttribute) EcoreHelper.getFeature(productClass, "customKeyAttr");
        productTagsAttr = (EAttribute) EcoreHelper.getFeature(productClass, "tags");
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

    private byte[] save(List<EObject> roots, MetadataWhiteboard whiteboard,
            ConfigurationResolver resolver, Map<String, Object> options) throws IOException {
        CsvFormatProvider provider = new CsvFormatProvider();
        CodecResource resource = new CodecResource(
                URI.createURI("test://flat.csv"),
                whiteboard,
                resolver != null ? resolver : ConfigurationResolver.defaults(),
                null, null, provider);
        resource.getContents().addAll(roots);

        Map<String, Object> effective = options == null ? new HashMap<>() : new HashMap<>(options);
        effective.putIfAbsent(CodecTabularOptions.OPTION_REFERENCE_MODE,
                ReferenceMode.FLAT);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, effective);
        return out.toByteArray();
    }

    private List<List<String>> parseCsv(byte[] bytes) {
        List<List<String>> records = new ArrayList<>();
        String csv = new String(bytes, StandardCharsets.UTF_8);
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
    @DisplayName("single-valued reference flattens to <ref>.<sub>")
    void singleValuedReferenceFlattens() throws IOException {
        EObject featured = createProduct("p-001", "Featured Widget");
        EObject warehouse = createWarehouse("Acme", featured, List.of());

        byte[] bytes = save(List.of(warehouse), metadataService,
                ConfigurationResolver.defaults(), null);
        List<List<String>> rows = parseCsv(bytes);

        List<String> header = rows.get(0);
        assertTrue(header.contains("name"), header::toString);
        assertTrue(header.contains("featured.productId"), header::toString);
        assertTrue(header.contains("featured.name"), header::toString);
        assertFalse(header.contains("featured.0.productId"),
                () -> "single-valued ref should NOT use an index: " + header);

        List<String> data = rows.get(2);
        int featuredIdIdx = header.indexOf("featured.productId");
        int featuredNameIdx = header.indexOf("featured.name");
        assertEquals("p-001", data.get(featuredIdIdx));
        assertEquals("Featured Widget", data.get(featuredNameIdx));
    }

    @Test
    @DisplayName("multi-valued reference flattens to <ref>.<index>.<sub>")
    void multiValuedReferenceUsesIndex() throws IOException {
        EObject p1 = createProduct("p-001", "First");
        EObject p2 = createProduct("p-002", "Second");
        EObject p3 = createProduct("p-003", "Third");
        EObject warehouse = createWarehouse("Acme", null, List.of(p1, p2, p3));

        byte[] bytes = save(List.of(warehouse), metadataService,
                ConfigurationResolver.defaults(), null);
        List<List<String>> rows = parseCsv(bytes);
        List<String> header = rows.get(0);
        List<String> data = rows.get(2);

        assertTrue(header.contains("items.0.productId"), header::toString);
        assertTrue(header.contains("items.0.name"), header::toString);
        assertTrue(header.contains("items.1.productId"), header::toString);
        assertTrue(header.contains("items.2.name"), header::toString);

        assertEquals("p-001", data.get(header.indexOf("items.0.productId")));
        assertEquals("First", data.get(header.indexOf("items.0.name")));
        assertEquals("p-002", data.get(header.indexOf("items.1.productId")));
        assertEquals("Third", data.get(header.indexOf("items.2.name")));
    }

    @Test
    @DisplayName("multiple roots with different multi-ref sizes produce aligned sparse rows")
    void multipleRootsAlignedSparse() throws IOException {
        EObject w1 = createWarehouse("W1", null, List.of(
                createProduct("p-001", "A"), createProduct("p-002", "B")));
        EObject w2 = createWarehouse("W2", null, List.of(
                createProduct("p-003", "C")));
        EObject w3 = createWarehouse("W3", null, List.of(
                createProduct("p-004", "D"),
                createProduct("p-005", "E"),
                createProduct("p-006", "F")));

        byte[] bytes = save(List.of(w1, w2, w3), metadataService,
                ConfigurationResolver.defaults(), null);
        List<List<String>> rows = parseCsv(bytes);

        // header + types + 3 data rows
        assertEquals(5, rows.size(), () -> "Expected 5 rows but got " + rows.size());

        List<String> header = rows.get(0);
        // Highest multi-ref index across rows determines the column count.
        assertTrue(header.contains("items.2.productId"), header::toString);
        assertTrue(header.contains("items.2.name"), header::toString);

        int items2NameIdx = header.indexOf("items.2.name");
        assertEquals("", rows.get(2).get(items2NameIdx),
                () -> "w1 has no items[2]: " + rows.get(2));
        assertEquals("", rows.get(3).get(items2NameIdx),
                () -> "w2 has no items[2]: " + rows.get(3));
        assertEquals("F", rows.get(4).get(items2NameIdx),
                () -> "w3.items[2].name should be F: " + rows.get(4));
    }

    @Test
    @DisplayName("null reference adds no columns")
    void nullReferenceProducesNoColumns() throws IOException {
        EObject warehouse = createWarehouse("Empty", null, List.of());

        byte[] bytes = save(List.of(warehouse), metadataService,
                ConfigurationResolver.defaults(), null);
        List<List<String>> rows = parseCsv(bytes);
        List<String> header = rows.get(0);

        assertTrue(header.contains("name"), header::toString);
        assertFalse(header.stream().anyMatch(h -> h.startsWith("featured.")),
                () -> "no 'featured.*' columns should appear: " + header);
        assertFalse(header.stream().anyMatch(h -> h.startsWith("items.")),
                () -> "no 'items.*' columns should appear: " + header);
    }

    @Test
    @DisplayName("multi-valued attribute is joined with ';'")
    void multiValuedAttributeJoined() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) product.eGet(productTagsAttr);
        tags.add("red");
        tags.add("large");
        tags.add("sale");

        EObject warehouse = createWarehouse("Acme", product, List.of());

        byte[] bytes = save(List.of(warehouse), metadataService,
                ConfigurationResolver.defaults(), null);
        List<List<String>> rows = parseCsv(bytes);
        List<String> header = rows.get(0);
        List<String> data = rows.get(2);

        int featuredTagsIdx = header.indexOf("featured.tags");
        assertTrue(featuredTagsIdx >= 0, () -> "featured.tags missing from header: " + header);
        assertEquals("red;large;sale", data.get(featuredTagsIdx));
    }

    @Test
    @DisplayName("@codec(key=...) annotation renames dotted sub-columns")
    void resolvedKeyAppliesToDottedNames() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        product.eSet(productCustomKeyAttr, "custom-value");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        byte[] bytes = save(List.of(warehouse), metadataService,
                ConfigurationResolver.defaults(), null);
        List<List<String>> rows = parseCsv(bytes);
        List<String> header = rows.get(0);

        assertTrue(header.contains("featured.custom_name"),
                () -> "expected resolved key in dotted column: " + header);
        assertFalse(header.contains("featured.customKeyAttr"),
                () -> "raw EMF name should not appear: " + header);

        int idx = header.indexOf("featured.custom_name");
        assertEquals("custom-value", rows.get(2).get(idx));
    }

    @Test
    @DisplayName("globalIgnoreFeatures drops a leaf column from the dotted layout")
    void globalIgnoreDropsLeafColumn() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        product.eSet(productCustomKeyAttr, "should-not-appear");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .globalIgnoreFeatures("customKeyAttr")
                .build();
        byte[] bytes = save(List.of(warehouse), metadataService, resolver, null);
        List<List<String>> rows = parseCsv(bytes);
        List<String> header = rows.get(0);

        assertFalse(header.contains("featured.custom_name"),
                () -> "ignored feature should not appear: " + header);
        assertFalse(header.contains("featured.customKeyAttr"),
                () -> "ignored feature should not appear: " + header);
        // Sibling columns still present.
        assertTrue(header.contains("featured.productId"), header::toString);
        assertTrue(header.contains("featured.name"), header::toString);
    }

    @Test
    @DisplayName("OPTION_COLUMN_TYPES override accepts the dotted column name")
    void columnTypesOverrideByDottedName() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        Map<String, Object> options = new HashMap<>();
        options.put(CodecTabularOptions.OPTION_COLUMN_TYPES,
                Map.of("featured.name", "VARCHAR(255)"));

        byte[] bytes = save(List.of(warehouse), metadataService,
                ConfigurationResolver.defaults(), options);
        List<List<String>> rows = parseCsv(bytes);

        int idx = rows.get(0).indexOf("featured.name");
        assertEquals("VARCHAR(255)", rows.get(1).get(idx),
                () -> "expected VARCHAR(255) in types row: " + rows.get(1));
    }

    @Test
    @DisplayName("cycle in EReferences is broken by the path-stack guard")
    void cyclesBrokenByPathStack() throws IOException {
        // Build a small EPackage with a self-referencing EClass: Node { name, next: Node }
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("cyclepkg");
        pkg.setNsPrefix("cy");
        pkg.setNsURI("http://test/csv/flat/cycle");

        EClass nodeClass = EcoreFactory.eINSTANCE.createEClass();
        nodeClass.setName("Node");
        pkg.getEClassifiers().add(nodeClass);

        EAttribute nodeNameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        nodeNameAttr.setName("name");
        nodeNameAttr.setEType(EcorePackage.Literals.ESTRING);
        nodeClass.getEStructuralFeatures().add(nodeNameAttr);

        EReference nextRef = EcoreFactory.eINSTANCE.createEReference();
        nextRef.setName("next");
        nextRef.setEType(nodeClass);
        nodeClass.getEStructuralFeatures().add(nextRef);

        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        try {
            MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
            whiteboard.registerPackage(pkg);

            EObject n1 = pkg.getEFactoryInstance().create(nodeClass);
            n1.eSet(nodeNameAttr, "n1");
            EObject n2 = pkg.getEFactoryInstance().create(nodeClass);
            n2.eSet(nodeNameAttr, "n2");
            n1.eSet(nextRef, n2);
            n2.eSet(nextRef, n1); // cycle

            byte[] bytes = save(List.of(n1), whiteboard, ConfigurationResolver.defaults(), null);
            List<List<String>> rows = parseCsv(bytes);
            List<String> header = rows.get(0);
            List<String> data = rows.get(2);

            // n1.name + n1.next.name reachable; n1.next.next.* must not appear (would be a cycle).
            assertTrue(header.contains("name"), header::toString);
            assertTrue(header.contains("next.name"), header::toString);
            assertFalse(header.stream().anyMatch(h -> h.startsWith("next.next.")),
                    () -> "cycle must be broken; 'next.next.*' should not appear: " + header);

            assertEquals("n1", data.get(header.indexOf("name")));
            assertEquals("n2", data.get(header.indexOf("next.name")));

            assertNotNull(bytes); // sanity
        } finally {
            EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        }
    }

    @Test
    @DisplayName("falls back gracefully with an empty Resource")
    void emptyResourceIsAllowed() throws IOException {
        byte[] bytes = save(Collections.emptyList(), metadataService,
                ConfigurationResolver.defaults(), null);
        // Empty resource is short-circuited in CodecResource.doSave (warning logged),
        // so the produced byte array is empty.
        assertEquals(0, bytes.length, "empty resource produces no output");
    }
}
