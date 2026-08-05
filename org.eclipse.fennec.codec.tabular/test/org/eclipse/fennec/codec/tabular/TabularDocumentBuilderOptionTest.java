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
package org.eclipse.fennec.codec.tabular;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.tabular.model.tabular.Cell;
import org.eclipse.fennec.codec.tabular.model.tabular.Column;
import org.eclipse.fennec.codec.tabular.model.tabular.LongCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Row;
import org.eclipse.fennec.codec.tabular.model.tabular.StringCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Table;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link TabularDocumentBuilder} honors the codec value gate
 * ({@code serializeNull}/{@code serializeEmpty}/{@code serializeDefault}) and the
 * {@code enumSerialization} strategy. These are format-agnostic, so they lock the
 * behavior for every tabular renderer (CSV/ODS/XLSX/R) at once — matching the
 * Jackson serialization pipeline.
 */
@DisplayName("TabularDocumentBuilder — codec options (value gate + enums)")
class TabularDocumentBuilderOptionTest {

    private EPackage pkg;
    private EClass thingClass;
    private EAttribute valueAttr;

    @AfterEach
    void cleanup() {
        if (pkg != null && pkg.getNsURI() != null) {
            EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        }
        pkg = null;
    }

    private void createPackage(String nsSuffix) {
        pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("optpkg");
        pkg.setNsPrefix("opt");
        pkg.setNsURI("http://test/tabular/option/" + nsSuffix);
        thingClass = EcoreFactory.eINSTANCE.createEClass();
        thingClass.setName("Thing");
        pkg.getEClassifiers().add(thingClass);
    }

    private void addValueAttr(org.eclipse.emf.ecore.EClassifier type) {
        valueAttr = EcoreFactory.eINSTANCE.createEAttribute();
        valueAttr.setName("value");
        valueAttr.setEType(type);
        thingClass.getEStructuralFeatures().add(valueAttr);
    }

    private void register() {
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
    }

    private Table buildTable(EObject root, ConfigurationResolver resolver) {
        TabularDocument doc = TabularDocumentBuilder.build(
                List.of(root), Collections.emptyMap(),
                resolver != null ? resolver : ConfigurationResolver.defaults());
        return doc.getTables().get(0);
    }

    private static boolean hasColumn(Table table, String header) {
        for (Column c : table.getColumns()) {
            if (header.equals(c.getHeader())) {
                return true;
            }
        }
        return false;
    }

    // ========================================================================
    // Value gate
    // ========================================================================

    @Test
    @DisplayName("default-valued attribute is dropped by default, kept with serializeDefault(true)")
    void defaultValueGate() {
        createPackage("default-gate");
        addValueAttr(EcorePackage.Literals.EINT);
        register();

        // EInt default is 0; setting 0 makes the value equal to the default.
        EObject obj = pkg.getEFactoryInstance().create(thingClass);
        obj.eSet(valueAttr, Integer.valueOf(0));

        Table dropped = buildTable(obj, ConfigurationResolver.defaults());
        assertTrue(!hasColumn(dropped, "value"),
                () -> "default-valued attribute should be dropped by default");

        ConfigurationResolver keep = ConfigurationResolver.builder()
                .serializeDefault(true)
                .build();
        Table kept = buildTable(obj, keep);
        assertTrue(hasColumn(kept, "value"),
                () -> "serializeDefault(true) should keep the column");
        assertEquals(0L, assertInstanceOf(LongCell.class, kept.getRows().get(0).getCells().get(0)).getValue());
    }

    @Test
    @DisplayName("null attribute is dropped by default, kept with serializeNull(true)")
    void nullValueGate() {
        createPackage("null-gate");
        addValueAttr(EcorePackage.Literals.ESTRING);
        register();

        // Unset (null) String.
        EObject obj = pkg.getEFactoryInstance().create(thingClass);

        Table dropped = buildTable(obj, ConfigurationResolver.defaults());
        assertTrue(!hasColumn(dropped, "value"),
                () -> "null attribute should be dropped by default");

        ConfigurationResolver keep = ConfigurationResolver.builder()
                .serializeNull(true)
                .build();
        Table kept = buildTable(obj, keep);
        assertTrue(hasColumn(kept, "value"),
                () -> "serializeNull(true) should keep the column");
    }

    // ========================================================================
    // enumSerialization
    // ========================================================================

    /** Builds Thing { value: Color } where GREEN has name=GREEN, literal="Green", value=1. */
    private EObject createEnumThing() {
        createPackage("enum");
        EEnum color = EcoreFactory.eINSTANCE.createEEnum();
        color.setName("Color");
        color.getELiterals().add(literal("RED", "red", 0));
        EEnumLiteral green = literal("GREEN", "Green", 1);
        color.getELiterals().add(green);
        pkg.getEClassifiers().add(color);
        addValueAttr(color);
        register();

        EObject obj = pkg.getEFactoryInstance().create(thingClass);
        // GREEN (value 1) is non-default (default is the first literal RED, value 0).
        obj.eSet(valueAttr, green.getInstance());
        return obj;
    }

    private static EEnumLiteral literal(String name, String literal, int value) {
        EEnumLiteral l = EcoreFactory.eINSTANCE.createEEnumLiteral();
        l.setName(name);
        l.setLiteral(literal);
        l.setValue(value);
        return l;
    }

    private Cell enumCell(String strategyOrNull) {
        EObject obj = createEnumThing();
        ConfigurationResolver resolver = strategyOrNull == null
                ? ConfigurationResolver.defaults()
                : ConfigurationResolver.builder()
                        .optionsProperties(Map.of("enumSerialization", strategyOrNull))
                        .build();
        Table table = buildTable(obj, resolver);
        Row row = table.getRows().get(0);
        return row.getCells().get(0);
    }

    @Test
    @DisplayName("LITERAL (default) → StringCell with the literal")
    void enumLiteralDefault() {
        assertEquals("Green", assertInstanceOf(StringCell.class, enumCell(null)).getValue());
    }

    @Test
    @DisplayName("NAME → StringCell with the constant name")
    void enumName() {
        assertEquals("GREEN", assertInstanceOf(StringCell.class, enumCell("NAME")).getValue());
    }

    @Test
    @DisplayName("VALUE → LongCell with the integer value")
    void enumValue() {
        assertEquals(1L, assertInstanceOf(LongCell.class, enumCell("VALUE")).getValue());
    }

    // ========================================================================
    // Column ordering: idOnTop + fieldOrder=ALPHABETICAL
    // ========================================================================

    /** Builds Item { zebra, id (eID), alpha } in that declared order, all set to non-default values. */
    private EObject createOrderedItem() {
        createPackage("order");
        EAttribute zebra = stringAttr("zebra");
        EAttribute id = stringAttr("id");
        id.setID(true);
        EAttribute alpha = stringAttr("alpha");
        thingClass.getEStructuralFeatures().add(zebra);
        thingClass.getEStructuralFeatures().add(id);
        thingClass.getEStructuralFeatures().add(alpha);
        register();

        EObject obj = pkg.getEFactoryInstance().create(thingClass);
        obj.eSet(zebra, "z");
        obj.eSet(id, "i");
        obj.eSet(alpha, "a");
        return obj;
    }

    private static EAttribute stringAttr(String name) {
        EAttribute a = EcoreFactory.eINSTANCE.createEAttribute();
        a.setName(name);
        a.setEType(EcorePackage.Literals.ESTRING);
        return a;
    }

    private List<String> headers(EObject root, Map<String, Object> opts, ConfigurationResolver resolver) {
        Table table = TabularDocumentBuilder.build(List.of(root), opts,
                resolver != null ? resolver : ConfigurationResolver.defaults()).getTables().get(0);
        List<String> result = new ArrayList<>();
        for (Column c : table.getColumns()) {
            result.add(c.getHeader());
        }
        return result;
    }

    private static ConfigurationResolver idOnTopResolver() {
        return ConfigurationResolver.builder()
                .optionsProperties(Map.of("idOnTop", Boolean.TRUE))
                .build();
    }

    private static ConfigurationResolver idOnTopFalseResolver() {
        return ConfigurationResolver.builder()
                .optionsProperties(Map.of("idOnTop", Boolean.FALSE))
                .build();
    }

    @Test
    @DisplayName("default → eID column floats to front (idOnTop defaults to true, issue #106)")
    void defaultFloatsIdColumn() {
        assertEquals(List.of("id", "zebra", "alpha"),
                headers(createOrderedItem(), Collections.emptyMap(), ConfigurationResolver.defaults()));
    }

    @Test
    @DisplayName("idOnTop=false → EClass-declared order")
    void declarationOrder() {
        assertEquals(List.of("zebra", "id", "alpha"),
                headers(createOrderedItem(), Collections.emptyMap(), idOnTopFalseResolver()));
    }

    @Test
    @DisplayName("idOnTop → eID attribute floated to first column")
    void idOnTop() {
        assertEquals(List.of("id", "zebra", "alpha"),
                headers(createOrderedItem(), Collections.emptyMap(), idOnTopResolver()));
    }

    @Test
    @DisplayName("fieldOrder=ALPHABETICAL → columns sorted by header (idOnTop=false)")
    void alphabetical() {
        assertEquals(List.of("alpha", "id", "zebra"),
                headers(createOrderedItem(), Map.of("fieldOrder", "ALPHABETICAL"),
                        idOnTopFalseResolver()));
    }

    @Test
    @DisplayName("alphabetical + idOnTop → sorted, then id floated to front")
    void alphabeticalThenIdOnTop() {
        assertEquals(List.of("id", "alpha", "zebra"),
                headers(createOrderedItem(), Map.of("fieldOrder", "ALPHABETICAL"), idOnTopResolver()));
    }
}
