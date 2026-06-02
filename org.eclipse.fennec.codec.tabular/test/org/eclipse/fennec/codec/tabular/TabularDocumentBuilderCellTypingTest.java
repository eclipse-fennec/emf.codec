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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell;
import org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell;
import org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Cell;
import org.eclipse.fennec.codec.tabular.model.tabular.DateCell;
import org.eclipse.fennec.codec.tabular.model.tabular.DoubleCell;
import org.eclipse.fennec.codec.tabular.model.tabular.EmptyCell;
import org.eclipse.fennec.codec.tabular.model.tabular.FkCell;
import org.eclipse.fennec.codec.tabular.model.tabular.LongCell;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.codec.tabular.model.tabular.Row;
import org.eclipse.fennec.codec.tabular.model.tabular.StringCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Table;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link TabularDocumentBuilder} produces the correct concrete
 * {@code Cell} subclass (with the right typed value) for each EMF attribute
 * type.
 * <p>
 * These tests exist specifically to catch cell-typing bugs that are invisible
 * to the CSV test suite — the CSV renderer stringifies every cell via
 * {@code toString()}, so a {@code LongCell} mistakenly emitted as a
 * {@code StringCell} would produce identical CSV output but break the
 * upcoming ODS / XLSX renderers, which read the typed accessors.
 */
@DisplayName("TabularDocumentBuilder — cell typing")
class TabularDocumentBuilderCellTypingTest {

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

    // ========================================================================
    // Helpers
    // ========================================================================

    /** Builds a single-EAttribute EClass for the given type, registers it, and returns the EAttribute. */
    private void createScalarPackage(EDataType valueType, boolean many) {
        pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("typingpkg");
        pkg.setNsPrefix("typ");
        pkg.setNsURI("http://test/tabular/typing/" + valueType.getName() + (many ? "/many" : ""));

        thingClass = EcoreFactory.eINSTANCE.createEClass();
        thingClass.setName("Thing");
        pkg.getEClassifiers().add(thingClass);

        valueAttr = EcoreFactory.eINSTANCE.createEAttribute();
        valueAttr.setName("value");
        valueAttr.setEType(valueType);
        if (many) {
            valueAttr.setUpperBound(-1);
        }
        thingClass.getEStructuralFeatures().add(valueAttr);

        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
    }

    private EObject createThing(Object value) {
        EObject obj = pkg.getEFactoryInstance().create(thingClass);
        if (value != null) {
            obj.eSet(valueAttr, value);
        }
        return obj;
    }

    /** Builds a TabularDocument in IGNORE mode for a single Thing and returns the first row's first cell. */
    private Cell buildAndExtractFirstCell(EObject root, Map<String, Object> options,
            ConfigurationResolver resolver) {
        TabularDocument doc = TabularDocumentBuilder.build(
                List.of(root),
                options != null ? options : Collections.emptyMap(),
                resolver != null ? resolver : ConfigurationResolver.defaults());
        Table table = doc.getTables().get(0);
        Row row = table.getRows().get(0);
        return row.getCells().get(0);
    }

    private Cell singleCell(EDataType type, Object value) {
        createScalarPackage(type, false);
        return buildAndExtractFirstCell(createThing(value), null, null);
    }

    // ========================================================================
    // Scalar cells
    // ========================================================================

    @Test
    @DisplayName("EString → StringCell")
    void stringCell() {
        Cell c = singleCell(EcorePackage.Literals.ESTRING, "hello");
        assertEquals("hello", assertInstanceOf(StringCell.class, c).getValue());
    }

    @Test
    @DisplayName("EBoolean → BooleanCell")
    void booleanCell() {
        Cell c = singleCell(EcorePackage.Literals.EBOOLEAN, Boolean.TRUE);
        assertTrue(assertInstanceOf(BooleanCell.class, c).isValue());
    }

    @Test
    @DisplayName("EInt → LongCell (widened)")
    void intCellIsLong() {
        Cell c = singleCell(EcorePackage.Literals.EINT, Integer.valueOf(42));
        assertEquals(42L, assertInstanceOf(LongCell.class, c).getValue());
    }

    @Test
    @DisplayName("EShort → LongCell (widened)")
    void shortCellIsLong() {
        Cell c = singleCell(EcorePackage.Literals.ESHORT, (short) 7);
        assertEquals(7L, assertInstanceOf(LongCell.class, c).getValue());
    }

    @Test
    @DisplayName("EByte → LongCell (widened)")
    void byteCellIsLong() {
        Cell c = singleCell(EcorePackage.Literals.EBYTE, (byte) 5);
        assertEquals(5L, assertInstanceOf(LongCell.class, c).getValue());
    }

    @Test
    @DisplayName("ELong → LongCell")
    void longCell() {
        Cell c = singleCell(EcorePackage.Literals.ELONG, 9876543210L);
        assertEquals(9876543210L, assertInstanceOf(LongCell.class, c).getValue());
    }

    @Test
    @DisplayName("EDouble → DoubleCell")
    void doubleCell() {
        Cell c = singleCell(EcorePackage.Literals.EDOUBLE, Double.valueOf(3.14));
        assertEquals(3.14, assertInstanceOf(DoubleCell.class, c).getValue(), 0.0);
    }

    @Test
    @DisplayName("EFloat → DoubleCell (widened)")
    void floatCellIsDouble() {
        Cell c = singleCell(EcorePackage.Literals.EFLOAT, Float.valueOf(2.5f));
        assertEquals(2.5, assertInstanceOf(DoubleCell.class, c).getValue(), 0.0);
    }

    @Test
    @DisplayName("EBigDecimal → BigDecimalCell")
    void bigDecimalCell() {
        BigDecimal value = new BigDecimal("15.42");
        Cell c = singleCell(EcorePackage.Literals.EBIG_DECIMAL, value);
        assertEquals(value, assertInstanceOf(BigDecimalCell.class, c).getValue());
    }

    @Test
    @DisplayName("EBigInteger → BigDecimalCell (no BigIntegerCell in the model)")
    void bigIntegerCellPromotedToBigDecimal() {
        BigInteger value = new BigInteger("12345678901234567890");
        Cell c = singleCell(EcorePackage.Literals.EBIG_INTEGER, value);
        BigDecimalCell bdc = assertInstanceOf(BigDecimalCell.class, c);
        assertEquals(new BigDecimal(value), bdc.getValue());
    }

    @Test
    @DisplayName("EDate without format hint → DateCell with the original Date and null dateFormat")
    void dateCellWithoutFormat() {
        Date value = new GregorianCalendar(2026, Calendar.MAY, 21).getTime();
        Cell c = singleCell(EcorePackage.Literals.EDATE, value);
        DateCell dc = assertInstanceOf(DateCell.class, c);
        assertEquals(value, dc.getValue());
        assertEquals(null, dc.getDateFormat(),
                () -> "no dateFormat configured → cell should not carry a format hint");
    }

    @Test
    @DisplayName("EDate with global dateFormat option → DateCell carries the format hint")
    void dateCellWithFormat() {
        createScalarPackage(EcorePackage.Literals.EDATE, false);

        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2026, Calendar.MAY, 21, 12, 0, 0);
        Date value = cal.getTime();

        // Configure the resolver with a global dateFormat. The builder reads it via FeatureConfig.getDateFormat().
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of("dateFormat", "yyyy-MM-dd"))
                .build();

        Cell c = buildAndExtractFirstCell(createThing(value), null, resolver);
        DateCell dc = assertInstanceOf(DateCell.class, c);
        assertEquals(value, dc.getValue());
        assertEquals("yyyy-MM-dd", dc.getDateFormat());
        // Renderer-side stringification sanity (not what we're testing, but a useful anchor):
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(value),
                new SimpleDateFormat(dc.getDateFormat()).format(dc.getValue()));
    }

    @Test
    @DisplayName("EByteArray → BinaryCell")
    void binaryCell() {
        byte[] value = new byte[] { 1, 2, 3, 4 };
        Cell c = singleCell(EcorePackage.Literals.EBYTE_ARRAY, value);
        assertEquals(value, assertInstanceOf(BinaryCell.class, c).getValue());
    }

    // ========================================================================
    // Null / unset
    // ========================================================================

    @Test
    @DisplayName("included null EString → EmptyCell")
    void unsetStringIsEmpty() {
        createScalarPackage(EcorePackage.Literals.ESTRING, false);
        EObject obj = pkg.getEFactoryInstance().create(thingClass);
        // Do not call eSet: value stays unset (null for a single-valued EString).
        // The value gate drops null columns by default, so keep it with serializeNull(true)
        // to assert the cell typing of an included-but-null value.
        ConfigurationResolver keepNull = ConfigurationResolver.builder()
                .serializeNull(true)
                .build();
        Cell c = buildAndExtractFirstCell(obj, null, keepNull);
        assertInstanceOf(EmptyCell.class, c);
    }

    // ========================================================================
    // Multi-valued attribute → joined StringCell
    // ========================================================================

    @Test
    @DisplayName("multi-valued EString → StringCell with ';'-joined values")
    void multiValuedStringJoinsToStringCell() {
        createScalarPackage(EcorePackage.Literals.ESTRING, true);
        EObject obj = pkg.getEFactoryInstance().create(thingClass);
        @SuppressWarnings("unchecked")
        List<String> values = (List<String>) obj.eGet(valueAttr);
        values.add("red");
        values.add("large");
        values.add("sale");

        Cell c = buildAndExtractFirstCell(obj, null, null);
        StringCell sc = assertInstanceOf(StringCell.class, c);
        assertEquals("red;large;sale", sc.getValue());
    }

    // ========================================================================
    // Reference → FkCell (SQL_TABLES mode)
    // ========================================================================

    @Test
    @DisplayName("single-valued EReference in SQL_TABLES mode → FkCell pointing at the target")
    void singleValuedRefProducesFkCell() {
        // Build a small EPackage: Holder { thing: Thing }, where Thing has an id.
        pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("fkpkg");
        pkg.setNsPrefix("fk");
        pkg.setNsURI("http://test/tabular/typing/fk");

        thingClass = EcoreFactory.eINSTANCE.createEClass();
        thingClass.setName("Thing");
        pkg.getEClassifiers().add(thingClass);
        EAttribute thingIdAttr = EcoreFactory.eINSTANCE.createEAttribute();
        thingIdAttr.setName("id");
        thingIdAttr.setEType(EcorePackage.Literals.ESTRING);
        thingClass.getEStructuralFeatures().add(thingIdAttr);

        EClass holderClass = EcoreFactory.eINSTANCE.createEClass();
        holderClass.setName("Holder");
        pkg.getEClassifiers().add(holderClass);
        EReference thingRef = EcoreFactory.eINSTANCE.createEReference();
        thingRef.setName("thing");
        thingRef.setEType(thingClass);
        thingRef.setContainment(false);
        holderClass.getEStructuralFeatures().add(thingRef);

        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        EObject thing = pkg.getEFactoryInstance().create(thingClass);
        thing.eSet(thingIdAttr, "t-001");
        EObject holder = pkg.getEFactoryInstance().create(holderClass);
        holder.eSet(thingRef, thing);

        TabularDocument doc = TabularDocumentBuilder.build(
                List.of(holder, thing),
                Map.of(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES),
                ConfigurationResolver.defaults());

        // Find the Holder table and locate the FK cell in its single row.
        Table holderTable = doc.getTables().stream()
                .filter(t -> t.getEClass() == holderClass)
                .findFirst()
                .orElseThrow();
        // Columns: PK + thing_id (FK_PARENT). Row cells: LongCell PK, then FkCell.
        List<Cell> cells = new ArrayList<>(holderTable.getRows().get(0).getCells());
        assertInstanceOf(LongCell.class, cells.get(0)); // PK
        FkCell fk = assertInstanceOf(FkCell.class, cells.get(1));
        assertEquals(thingClass, fk.getTargetEClass());
        // The Thing was visited; its pseudo-id is 1 in its own EClass space.
        assertEquals(1L, fk.getTargetId());
    }
}
