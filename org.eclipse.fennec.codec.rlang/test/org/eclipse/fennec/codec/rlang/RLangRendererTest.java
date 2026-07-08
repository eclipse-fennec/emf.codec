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
package org.eclipse.fennec.codec.rlang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
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

/**
 * End-to-end tests for {@link RLangRenderer}. The renderer's output is a binary
 * RData stream — there's no off-the-shelf Java reader for it in the workspace,
 * so this test carries a minimal inline decoder ({@link RDataReader}) that
 * walks just the subset of the SXP grammar the renderer actually emits.
 */
@DisplayName("R Language renderer")
class RLangRendererTest {

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
    // EMF helpers
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

    private byte[] save(String uriStr, List<EObject> roots, Map<String, Object> options)
            throws IOException {
        RLangFormatProvider provider = new RLangFormatProvider();
        CodecResource resource = new CodecResource(
                URI.createURI(uriStr),
                metadataService, ConfigurationResolver.defaults(),
                null, null, provider);
        resource.getContents().addAll(roots);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options == null ? new HashMap<>() : new HashMap<>(options));
        return out.toByteArray();
    }

    // ========================================================================
    // IGNORE — one data frame, attributes only
    // ========================================================================

    @Test
    @DisplayName("IGNORE mode: one data frame named after the root EClass")
    void ignoreModeEmitsOneDataFrame() throws IOException {
        EObject p = createProduct("p-001", "Widget");
        byte[] bytes = save("test://out.RData", List.of(p), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.IGNORE));

        Map<String, DataFrame> frames = RDataReader.read(bytes);
        assertEquals(1, frames.size(),
                () -> "Expected exactly one data frame; got " + frames.keySet());
        DataFrame df = frames.get("Product");
        assertNotNull(df, () -> "Product data frame missing; got " + frames.keySet());

        Column productId = df.columns.get("productId");
        Column name = df.columns.get("name");
        assertNotNull(productId);
        assertNotNull(name);
        assertEquals(Column.Kind.STRING, productId.kind);
        assertEquals("p-001", productId.strings[0]);
        assertEquals("Widget", name.strings[0]);
    }

    // ========================================================================
    // SQL_TABLES — multiple data frames + integer FKs
    // ========================================================================

    @Test
    @DisplayName("SQL_TABLES: per-EClass data frames, _id is integer, FK is integer")
    void sqlTablesEmitsTypedDataFrames() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        byte[] bytes = save("test://out.RData", List.of(warehouse), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES));

        Map<String, DataFrame> frames = RDataReader.read(bytes);
        DataFrame warehouseDf = frames.get("Warehouse");
        DataFrame productDf = frames.get("Product");
        assertNotNull(warehouseDf, () -> "Warehouse missing; got " + frames.keySet());
        assertNotNull(productDf, () -> "Product missing; got " + frames.keySet());

        Column wId = warehouseDf.columns.get("_id");
        Column wFk = warehouseDf.columns.get("featured_id");
        assertEquals(Column.Kind.INT, wId.kind, "_id should be INTSXP");
        assertEquals(Column.Kind.INT, wFk.kind, "featured_id should be INTSXP for small ids");
        assertEquals(1, wFk.ints[0], "Warehouse.featured_id should point at Product._id=1");

        Column pId = productDf.columns.get("_id");
        assertEquals(Column.Kind.INT, pId.kind);
        assertEquals(1, pId.ints[0]);
    }

    // ========================================================================
    // FLAT — single data frame, dotted columns
    // ========================================================================

    @Test
    @DisplayName("FLAT mode: dotted columns inside one data frame")
    void flatModeEmitsDottedColumns() throws IOException {
        EObject featured = createProduct("p-001", "Featured Widget");
        EObject warehouse = createWarehouse("Acme", featured, List.of());

        byte[] bytes = save("test://out.RData", List.of(warehouse), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.FLAT));

        Map<String, DataFrame> frames = RDataReader.read(bytes);
        assertEquals(1, frames.size());
        DataFrame df = frames.get("Warehouse");
        assertNotNull(df);
        Column nested = df.columns.get("featured.productId");
        assertNotNull(nested,
                () -> "Expected featured.productId column; got " + df.columns.keySet());
        assertEquals("p-001", nested.strings[0]);
    }

    // ========================================================================
    // ZIP mode (.rdataz / dataframePerFile=true)
    // ========================================================================

    @Test
    @DisplayName("ZIP mode: one .RData entry per data frame")
    void zipModeEmitsOneRDataPerDataFrame() throws IOException {
        EObject product = createProduct("p-001", "Widget");
        EObject warehouse = createWarehouse("Acme", product, List.of());

        byte[] bytes = save("test://out.RData", List.of(warehouse), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES,
                CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE));

        Map<String, byte[]> entries = readZip(bytes);
        assertTrue(entries.containsKey("Warehouse.RData"),
                () -> "Warehouse.RData entry missing; got " + entries.keySet());
        assertTrue(entries.containsKey("Product.RData"),
                () -> "Product.RData entry missing; got " + entries.keySet());

        Map<String, DataFrame> warehouseFrames = RDataReader.read(entries.get("Warehouse.RData"));
        assertEquals(1, warehouseFrames.size(),
                "Each zip entry should hold exactly one data frame");
        assertNotNull(warehouseFrames.get("Warehouse"));
    }

    // ========================================================================
    // Empty resource
    // ========================================================================

    @Test
    @DisplayName("empty resource produces no output")
    void emptyResourceIsAllowed() throws IOException {
        byte[] bytes = save("test://out.RData", List.of(), Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.IGNORE));
        assertEquals(0, bytes.length, "empty resource yields no bytes");
    }

    // ========================================================================
    // Utilities
    // ========================================================================

    private static Map<String, byte[]> readZip(byte[] zipBytes) throws IOException {
        Map<String, byte[]> result = new LinkedHashMap<>();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                byte[] tmp = new byte[4096];
                int n;
                while ((n = zip.read(tmp)) >= 0) {
                    buf.write(tmp, 0, n);
                }
                result.put(entry.getName(), buf.toByteArray());
            }
        }
        return result;
    }

    // ========================================================================
    // Minimal RData reader — enough to walk what the renderer emits.
    // ========================================================================

    private static final class DataFrame {
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
    }

    private static final class Column {
        enum Kind { INT, REAL, LOGICAL, STRING, POSIXCT }
        Kind kind;
        int length;
        int[] ints;       // INT, LOGICAL
        double[] doubles; // REAL, POSIXCT
        String[] strings; // STRING (null entry = NA)
        boolean[] na;     // INT/REAL/LOGICAL NA mask
    }

    private static final class RDataReader {
        private static final int SYMSXP = 1;
        private static final int LISTSXP = 2;
        private static final int CHARSXP = 9;
        private static final int LGLSXP = 10;
        private static final int INTSXP = 13;
        private static final int REALSXP = 14;
        private static final int STRSXP = 16;
        private static final int VECSXP = 19;
        private static final int NILVALUESXP = 254;

        private static final int HAS_ATTR_BIT = 1 << 9;
        private static final int NA_INT = Integer.MIN_VALUE;
        private static final long NA_REAL_BITS = 0x7FF00000000007A2L;

        static Map<String, DataFrame> read(byte[] bytes) throws IOException {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            // Skip "RDX2\nX\n" header (7 bytes) and 3 version ints.
            byte[] magic = new byte[7];
            in.readFully(magic);
            in.readInt(); in.readInt(); in.readInt();

            LinkedHashMap<String, DataFrame> result = new LinkedHashMap<>();
            while (true) {
                int flags = in.readInt();
                if (flags == NILVALUESXP) {
                    break;
                }
                int type = flags & 0xFF;
                if (type != LISTSXP) {
                    throw new IOException("Expected LISTSXP, got " + type);
                }
                String name = readSymbol(in);
                DataFrame df = readDataFrame(in);
                result.put(name, df);
            }
            return result;
        }

        private static String readSymbol(DataInputStream in) throws IOException {
            int symFlags = in.readInt();
            if ((symFlags & 0xFF) != SYMSXP) {
                throw new IOException("Expected SYMSXP, got " + (symFlags & 0xFF));
            }
            return readCharsxp(in);
        }

        private static String readCharsxp(DataInputStream in) throws IOException {
            int flags = in.readInt();
            if ((flags & 0xFF) != CHARSXP) {
                throw new IOException("Expected CHARSXP, got " + (flags & 0xFF));
            }
            int length = in.readInt();
            if (length < 0) {
                return null; // NA_STRING
            }
            byte[] bytes = new byte[length];
            in.readFully(bytes);
            return new String(bytes, java.nio.charset.StandardCharsets.US_ASCII);
        }

        private static DataFrame readDataFrame(DataInputStream in) throws IOException {
            int flags = in.readInt();
            int type = flags & 0xFF;
            if (type != VECSXP) {
                throw new IOException("Expected VECSXP, got " + type);
            }
            int columnCount = in.readInt();
            Column[] columns = new Column[columnCount];
            for (int c = 0; c < columnCount; c++) {
                columns[c] = readColumn(in);
            }
            // Attribute trailer: names / row.names / class
            String[] columnNames = null;
            while (true) {
                int attrFlags = in.readInt();
                if (attrFlags == NILVALUESXP) {
                    break;
                }
                if ((attrFlags & 0xFF) != LISTSXP) {
                    throw new IOException("Expected LISTSXP in attr trailer; got "
                            + (attrFlags & 0xFF));
                }
                String attrName = readSymbol(in);
                switch (attrName) {
                    case "names" -> columnNames = readStringVector(in);
                    case "row.names" -> readIntVector(in); // skip
                    case "class" -> readStringVector(in);  // skip (data.frame)
                    default -> skipAny(in);
                }
            }
            DataFrame df = new DataFrame();
            if (columnNames != null) {
                for (int i = 0; i < columnCount; i++) {
                    df.columns.put(columnNames[i], columns[i]);
                }
            }
            return df;
        }

        private static Column readColumn(DataInputStream in) throws IOException {
            int flags = in.readInt();
            int type = flags & 0xFF;
            boolean hasAttr = (flags & HAS_ATTR_BIT) != 0;
            Column col = new Column();
            switch (type) {
                case INTSXP -> readIntsxpInto(in, col, Column.Kind.INT);
                case REALSXP -> readRealsxpInto(in, col,
                        hasAttr ? Column.Kind.POSIXCT : Column.Kind.REAL);
                case LGLSXP -> readIntsxpInto(in, col, Column.Kind.LOGICAL);
                case STRSXP -> readStringsxpInto(in, col);
                default -> throw new IOException("Unsupported column type " + type);
            }
            if (hasAttr) {
                // class attribute trailer
                while (true) {
                    int attrFlags = in.readInt();
                    if (attrFlags == NILVALUESXP) {
                        break;
                    }
                    String attrName = readSymbol(in);
                    if ("class".equals(attrName)) {
                        // consume the class attribute trailer (not asserted)
                        readStringVector(in);
                    } else {
                        skipAny(in);
                    }
                }
            }
            return col;
        }

        private static void readIntsxpInto(DataInputStream in, Column col, Column.Kind kind)
                throws IOException {
            col.kind = kind;
            col.length = in.readInt();
            col.ints = new int[col.length];
            col.na = new boolean[col.length];
            for (int i = 0; i < col.length; i++) {
                int v = in.readInt();
                if (v == NA_INT) {
                    col.na[i] = true;
                } else {
                    col.ints[i] = v;
                }
            }
        }

        private static void readRealsxpInto(DataInputStream in, Column col, Column.Kind kind)
                throws IOException {
            col.kind = kind;
            col.length = in.readInt();
            col.doubles = new double[col.length];
            col.na = new boolean[col.length];
            for (int i = 0; i < col.length; i++) {
                long bits = in.readLong();
                if (bits == NA_REAL_BITS) {
                    col.na[i] = true;
                } else {
                    col.doubles[i] = Double.longBitsToDouble(bits);
                }
            }
        }

        private static void readStringsxpInto(DataInputStream in, Column col) throws IOException {
            col.kind = Column.Kind.STRING;
            col.length = in.readInt();
            col.strings = new String[col.length];
            col.na = new boolean[col.length];
            for (int i = 0; i < col.length; i++) {
                col.strings[i] = readCharsxp(in);
                if (col.strings[i] == null) {
                    col.na[i] = true;
                }
            }
        }

        private static String[] readStringVector(DataInputStream in) throws IOException {
            int flags = in.readInt();
            if ((flags & 0xFF) != STRSXP) {
                throw new IOException("Expected STRSXP; got " + (flags & 0xFF));
            }
            int length = in.readInt();
            String[] out = new String[length];
            for (int i = 0; i < length; i++) {
                out[i] = readCharsxp(in);
            }
            return out;
        }

        private static int[] readIntVector(DataInputStream in) throws IOException {
            int flags = in.readInt();
            if ((flags & 0xFF) != INTSXP) {
                throw new IOException("Expected INTSXP; got " + (flags & 0xFF));
            }
            int length = in.readInt();
            int[] out = new int[length];
            for (int i = 0; i < length; i++) {
                out[i] = in.readInt();
            }
            return out;
        }

        /** Best-effort consume of an unknown attribute value. Reads the type tag and
         *  delegates to the appropriate primitive reader; only the cases we actually
         *  emit are covered. */
        private static void skipAny(DataInputStream in) throws IOException {
            int flags = in.readInt();
            int type = flags & 0xFF;
            switch (type) {
                case INTSXP, LGLSXP -> {
                    int length = in.readInt();
                    in.skipBytes(length * 4);
                }
                case REALSXP -> {
                    int length = in.readInt();
                    in.skipBytes(length * 8);
                }
                case STRSXP -> {
                    int length = in.readInt();
                    for (int i = 0; i < length; i++) {
                        readCharsxp(in);
                    }
                }
                default -> throw new IOException("skipAny: unsupported type " + type);
            }
        }
    }
}
