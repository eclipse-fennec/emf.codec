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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.fennec.codec.tabular.TabularDocumentRenderer;
import org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell;
import org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell;
import org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Cell;
import org.eclipse.fennec.codec.tabular.model.tabular.Column;
import org.eclipse.fennec.codec.tabular.model.tabular.DateCell;
import org.eclipse.fennec.codec.tabular.model.tabular.DoubleCell;
import org.eclipse.fennec.codec.tabular.model.tabular.EmptyCell;
import org.eclipse.fennec.codec.tabular.model.tabular.FkCell;
import org.eclipse.fennec.codec.tabular.model.tabular.JoinTable;
import org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow;
import org.eclipse.fennec.codec.tabular.model.tabular.LongCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Row;
import org.eclipse.fennec.codec.tabular.model.tabular.StringCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Table;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument;

/**
 * Renders a {@link TabularDocument} as an R {@code .RData} binary stream.
 * <p>
 * Each {@link Table} (and each {@link JoinTable}) becomes one R <em>data
 * frame</em> — a {@code VECSXP} of typed column vectors plus the canonical
 * {@code names} / {@code row.names} / {@code class="data.frame"} attribute
 * trailer.
 * <p>
 * Two output modes, toggled by
 * {@link CodecRLangOptions#OPTION_DATAFRAME_PER_FILE}:
 * <ul>
 *   <li>Default ({@code false}): one {@code .RData} containing all data frames
 *       as named variables in a {@code LISTSXP} chain. The R consumer does
 *       {@code load("foo.RData")} and gets each data frame bound under its
 *       name.</li>
 *   <li>ZIP mode ({@code true}): a ZIP archive with one {@code .RData} entry
 *       per data frame, each entry self-contained (header + one variable +
 *       sentinel).</li>
 * </ul>
 * <p>
 * Column-type inference: each {@link Column} is homogeneous (built from one
 * feature). The renderer walks until the first non-{@link EmptyCell} and picks
 * the R type from its concrete {@link Cell} subclass — {@link StringCell} →
 * {@code STRSXP}, {@link LongCell} / {@link FkCell} → {@code INTSXP} (widened
 * to {@code REALSXP} when any value overflows signed 32-bit),
 * {@link DoubleCell} / {@link BigDecimalCell} → {@code REALSXP},
 * {@link BooleanCell} → {@code LGLSXP}, {@link DateCell} → {@code REALSXP}
 * with a {@code class=c("POSIXct","POSIXt")} attribute (POSIXct is the more
 * general R datetime type and handles arbitrary timestamps without time-zone
 * ambiguity), {@link BinaryCell} → Base64 {@code STRSXP}. Empty cells become
 * the column type's typed NA.
 * <p>
 * Wire format details follow gecko's earlier port of the R RData serializer:
 * {@code RDX2\nX\n} ASCII magic, format version 2, big-endian XDR for ints
 * and IEEE-754 doubles, and the R serialization tag bits described in
 * {@code src/main/R/src/main/serialize.c}.
 *
 * @since 2026-06
 */
public class RLangRenderer implements TabularDocumentRenderer<OutputStream> {

    // ---- R serialization type tags (subset we actually emit) ----
    private static final int SYMSXP = 1;
    private static final int LISTSXP = 2;
    private static final int CHARSXP = 9;
    private static final int LGLSXP = 10;
    private static final int INTSXP = 13;
    private static final int REALSXP = 14;
    private static final int STRSXP = 16;
    private static final int VECSXP = 19;
    private static final int NILVALUESXP = 254;

    // ---- Flag bits ----
    private static final int ASCII_MASK = 1 << 6;
    private static final int IS_OBJECT_BIT = 1 << 8;
    private static final int HAS_ATTR_BIT = 1 << 9;
    private static final int HAS_TAG_BIT = 1 << 10;

    // ---- R NA sentinels ----
    private static final int NA_INT = Integer.MIN_VALUE;
    private static final int NA_STRING_LENGTH = -1;
    /** R's signaling NaN payload used as NA_real. Bytes: 7f f0 00 00 00 00 07 a2. */
    private static final long NA_REAL_BITS = 0x7FF00000000007A2L;

    private static final String RDATA_FILE_EXTENSION = "RData";

    @Override
    public void render(TabularDocument doc, OutputStream target, Map<String, Object> options)
            throws IOException {
        boolean dataframePerFile = resolveBoolean(options,
                CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, false);

        List<NamedDataFrame> frames = buildDataFrames(doc);
        if (frames.isEmpty()) {
            return;
        }

        if (dataframePerFile) {
            writeZip(target, frames);
        } else {
            DataOutputStream out = new DataOutputStream(target);
            writeRDataHeader(out);
            writeListsxpChain(out, frames);
            out.flush();
        }
    }

    // ========================================================================
    // Build named data frames from the document
    // ========================================================================

    private List<NamedDataFrame> buildDataFrames(TabularDocument doc) {
        List<NamedDataFrame> frames = new ArrayList<>();
        for (Table table : doc.getTables()) {
            if (table.getColumns().isEmpty()) {
                continue;
            }
            frames.add(dataFrameFromTable(table));
        }
        for (JoinTable jt : doc.getJoinTables()) {
            frames.add(dataFrameFromJoinTable(jt));
        }
        return frames;
    }

    private NamedDataFrame dataFrameFromTable(Table table) {
        String name = qualifiedName(table.getSchema(), table.getName());
        int columnCount = table.getColumns().size();
        List<RColumn> columns = new ArrayList<>(columnCount);
        for (int c = 0; c < columnCount; c++) {
            Column col = table.getColumns().get(c);
            String header = col.getHeader() != null ? col.getHeader() : "";
            columns.add(buildColumn(header, table, c));
        }
        return new NamedDataFrame(name, columns);
    }

    private NamedDataFrame dataFrameFromJoinTable(JoinTable jt) {
        String name = qualifiedName(jt.getSchema(), jt.getFileName());
        int rows = jt.getRows().size();

        // ownerId column
        boolean ownerWide = false;
        long[] ownerIds = new long[rows];
        for (int r = 0; r < rows; r++) {
            JoinTableRow row = jt.getRows().get(r);
            ownerIds[r] = row.getOwnerId();
            if (!fitsInInt(row.getOwnerId())) {
                ownerWide = true;
            }
        }
        RColumn ownerCol = ownerWide
                ? RColumn.realFromLongs(jt.getOwnerCol(), ownerIds, new boolean[rows])
                : RColumn.intFromLongs(jt.getOwnerCol(), ownerIds, new boolean[rows]);

        // targetId column
        boolean targetWide = false;
        long[] targetIds = new long[rows];
        for (int r = 0; r < rows; r++) {
            JoinTableRow row = jt.getRows().get(r);
            targetIds[r] = row.getTargetId();
            if (!fitsInInt(row.getTargetId())) {
                targetWide = true;
            }
        }
        RColumn targetCol = targetWide
                ? RColumn.realFromLongs(jt.getTargetCol(), targetIds, new boolean[rows])
                : RColumn.intFromLongs(jt.getTargetCol(), targetIds, new boolean[rows]);

        return new NamedDataFrame(name, List.of(ownerCol, targetCol));
    }

    /** Picks the R type from the first non-empty cell in the column; gathers values. */
    private RColumn buildColumn(String header, Table table, int colIndex) {
        int rowCount = table.getRows().size();
        Cell sample = null;
        for (Row row : table.getRows()) {
            if (colIndex < row.getCells().size()) {
                Cell cell = row.getCells().get(colIndex);
                if (!(cell instanceof EmptyCell)) {
                    sample = cell;
                    break;
                }
            }
        }
        // All empty → STRSXP of NAs.
        if (sample == null) {
            String[] vals = new String[rowCount];
            boolean[] na = new boolean[rowCount];
            for (int r = 0; r < rowCount; r++) {
                na[r] = true;
            }
            return RColumn.string(header, vals, na);
        }

        if (sample instanceof StringCell) {
            return collectString(header, table, colIndex, rowCount);
        }
        if (sample instanceof LongCell || sample instanceof FkCell) {
            return collectIntOrReal(header, table, colIndex, rowCount);
        }
        if (sample instanceof DoubleCell || sample instanceof BigDecimalCell) {
            return collectReal(header, table, colIndex, rowCount);
        }
        if (sample instanceof BooleanCell) {
            return collectLogical(header, table, colIndex, rowCount);
        }
        if (sample instanceof DateCell) {
            return collectDate(header, table, colIndex, rowCount);
        }
        if (sample instanceof BinaryCell) {
            return collectBinary(header, table, colIndex, rowCount);
        }
        // Defensive fallback.
        return RColumn.string(header, new String[rowCount], new boolean[rowCount]);
    }

    private RColumn collectString(String header, Table table, int colIndex, int rowCount) {
        String[] vals = new String[rowCount];
        boolean[] na = new boolean[rowCount];
        for (int r = 0; r < rowCount; r++) {
            Cell cell = cellAt(table, r, colIndex);
            if (cell instanceof StringCell c && c.getValue() != null) {
                vals[r] = c.getValue();
            } else if (cell instanceof StringCell) {
                vals[r] = "";
            } else {
                na[r] = true;
            }
        }
        return RColumn.string(header, vals, na);
    }

    private RColumn collectIntOrReal(String header, Table table, int colIndex, int rowCount) {
        long[] vals = new long[rowCount];
        boolean[] na = new boolean[rowCount];
        boolean wide = false;
        for (int r = 0; r < rowCount; r++) {
            Cell cell = cellAt(table, r, colIndex);
            if (cell instanceof LongCell c) {
                vals[r] = c.getValue();
                if (!fitsInInt(c.getValue())) {
                    wide = true;
                }
            } else if (cell instanceof FkCell c) {
                vals[r] = c.getTargetId();
                if (!fitsInInt(c.getTargetId())) {
                    wide = true;
                }
            } else {
                na[r] = true;
            }
        }
        return wide
                ? RColumn.realFromLongs(header, vals, na)
                : RColumn.intFromLongs(header, vals, na);
    }

    private RColumn collectReal(String header, Table table, int colIndex, int rowCount) {
        double[] vals = new double[rowCount];
        boolean[] na = new boolean[rowCount];
        for (int r = 0; r < rowCount; r++) {
            Cell cell = cellAt(table, r, colIndex);
            if (cell instanceof DoubleCell c) {
                vals[r] = c.getValue();
            } else if (cell instanceof BigDecimalCell c && c.getValue() != null) {
                vals[r] = c.getValue().doubleValue();
            } else {
                na[r] = true;
            }
        }
        return RColumn.real(header, vals, na);
    }

    private RColumn collectLogical(String header, Table table, int colIndex, int rowCount) {
        int[] vals = new int[rowCount];
        boolean[] na = new boolean[rowCount];
        for (int r = 0; r < rowCount; r++) {
            Cell cell = cellAt(table, r, colIndex);
            if (cell instanceof BooleanCell c) {
                vals[r] = c.isValue() ? 1 : 0;
            } else {
                na[r] = true;
            }
        }
        return RColumn.logical(header, vals, na);
    }

    private RColumn collectDate(String header, Table table, int colIndex, int rowCount) {
        double[] secondsSinceEpoch = new double[rowCount];
        boolean[] na = new boolean[rowCount];
        for (int r = 0; r < rowCount; r++) {
            Cell cell = cellAt(table, r, colIndex);
            if (cell instanceof DateCell c && c.getValue() != null) {
                secondsSinceEpoch[r] = c.getValue().getTime() / 1000.0;
            } else {
                na[r] = true;
            }
        }
        return RColumn.posixct(header, secondsSinceEpoch, na);
    }

    private RColumn collectBinary(String header, Table table, int colIndex, int rowCount) {
        String[] vals = new String[rowCount];
        boolean[] na = new boolean[rowCount];
        for (int r = 0; r < rowCount; r++) {
            Cell cell = cellAt(table, r, colIndex);
            if (cell instanceof BinaryCell c && c.getValue() != null) {
                vals[r] = Base64.getEncoder().encodeToString(c.getValue());
            } else {
                na[r] = true;
            }
        }
        return RColumn.string(header, vals, na);
    }

    private static Cell cellAt(Table table, int rowIndex, int colIndex) {
        Row row = table.getRows().get(rowIndex);
        return colIndex < row.getCells().size() ? row.getCells().get(colIndex) : null;
    }

    private static boolean fitsInInt(long v) {
        return v >= Integer.MIN_VALUE + 1 && v <= Integer.MAX_VALUE;
    }

    private static String qualifiedName(String schema, String name) {
        String base = name != null ? name : "";
        if (schema != null && !schema.isEmpty()) {
            return schema + "." + base;
        }
        return base;
    }

    // ========================================================================
    // ZIP output
    // ========================================================================

    private void writeZip(OutputStream target, List<NamedDataFrame> frames) throws IOException {
        ZipOutputStream zip = new ZipOutputStream(target);
        for (NamedDataFrame frame : frames) {
            zip.putNextEntry(new ZipEntry(zipEntryName(frame.name)));
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream entryOut = new DataOutputStream(buf);
            writeRDataHeader(entryOut);
            writeListsxpChain(entryOut, List.of(frame));
            entryOut.flush();
            zip.write(buf.toByteArray());
            zip.closeEntry();
        }
        zip.finish();
    }

    private static String zipEntryName(String name) {
        String normalized = name == null ? "" : name.strip().replaceAll("[()]", "")
                .replaceAll("(?U)[^\\w.\\-]+", "_");
        return normalized + "." + RDATA_FILE_EXTENSION;
    }

    // ========================================================================
    // Low-level RData encoding
    // ========================================================================

    private static void writeRDataHeader(DataOutputStream out) throws IOException {
        // "RDX2\nX\n" magic, then format version 2, writer/reader R versions.
        out.write(new byte[] {'R', 'D', 'X', '2', '\n', 'X', '\n'});
        out.writeInt(2);
        out.writeInt(packedVersion(3, 0, 0));
        out.writeInt(packedVersion(2, 3, 0));
    }

    private static int packedVersion(int v, int p, int s) {
        return s + (p << 8) + (v << 16);
    }

    private void writeListsxpChain(DataOutputStream out, List<NamedDataFrame> frames)
            throws IOException {
        for (NamedDataFrame frame : frames) {
            out.writeInt(LISTSXP | HAS_TAG_BIT);
            writeSymbol(out, frame.name);
            writeDataFrame(out, frame);
        }
        out.writeInt(NILVALUESXP);
    }

    private void writeSymbol(DataOutputStream out, String name) throws IOException {
        out.writeInt(SYMSXP);
        writeCharsxp(out, name);
    }

    private void writeDataFrame(DataOutputStream out, NamedDataFrame frame) throws IOException {
        int rowCount = frame.columns.isEmpty() ? 0 : frame.columns.get(0).length();
        // VECSXP with IS_OBJECT + HAS_ATTR (the data.frame trailer).
        out.writeInt(VECSXP | IS_OBJECT_BIT | HAS_ATTR_BIT);
        out.writeInt(frame.columns.size());
        for (RColumn col : frame.columns) {
            col.write(out);
        }
        // Attribute trailer: names / row.names / class
        writeAttribute(out, "names", attrColumnNames(frame.columns));
        writeAttribute(out, "row.names", attrRowNames(rowCount));
        writeAttribute(out, "class", attrString(new String[] {"data.frame"}));
        out.writeInt(NILVALUESXP);
    }

    private void writeAttribute(DataOutputStream out, String name, AttrValue value)
            throws IOException {
        out.writeInt(LISTSXP | HAS_TAG_BIT);
        writeSymbol(out, name);
        value.write(out);
    }

    private static AttrValue attrString(String[] values) {
        return out -> RColumn.writeStrsxp(out, values, new boolean[values.length]);
    }

    private static AttrValue attrColumnNames(List<RColumn> columns) {
        String[] names = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            names[i] = columns.get(i).header;
        }
        return out -> RColumn.writeStrsxp(out, names, new boolean[names.length]);
    }

    /** Compact row.names form: c(NA_integer_, -length). */
    private static AttrValue attrRowNames(int length) {
        return out -> {
            out.writeInt(INTSXP);
            out.writeInt(2);
            out.writeInt(NA_INT);
            out.writeInt(-length);
        };
    }

    private static void writeCharsxp(DataOutputStream out, String value) throws IOException {
        if (value == null) {
            out.writeInt(CHARSXP);
            out.writeInt(NA_STRING_LENGTH);
            return;
        }
        out.writeInt(CHARSXP | (ASCII_MASK << 12));
        byte[] bytes = value.getBytes(StandardCharsets.US_ASCII);
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    // ========================================================================
    // Option resolution
    // ========================================================================

    private static boolean resolveBoolean(Map<String, Object> options, String key,
            boolean defaultValue) {
        Object value = options.get(key);
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s && !s.isBlank()) {
            return Boolean.parseBoolean(s);
        }
        return defaultValue;
    }

    // ========================================================================
    // Internal value carriers
    // ========================================================================

    @FunctionalInterface
    private interface AttrValue {
        void write(DataOutputStream out) throws IOException;
    }

    private static final class NamedDataFrame {
        final String name;
        final List<RColumn> columns;

        NamedDataFrame(String name, List<RColumn> columns) {
            this.name = name;
            this.columns = columns;
        }
    }

    /**
     * A typed R column ready to serialize. Carries length so the data frame
     * knows the row count without reaching back into the source table.
     */
    private static final class RColumn {

        enum Kind { INT, REAL, LOGICAL, STRING, POSIXCT }

        final String header;
        final Kind kind;
        final int length;

        // Storage is union-ish — only one of these is populated based on kind.
        private final long[] longs;     // INT (truncated to int) or REAL (via longBits)
        private final double[] doubles; // REAL, POSIXCT
        private final int[] ints;       // LOGICAL (0/1/NA_INT)
        private final String[] strings; // STRING
        private final boolean[] na;     // parallel mask

        private RColumn(String header, Kind kind, int length, long[] longs, double[] doubles,
                int[] ints, String[] strings, boolean[] na) {
            this.header = header;
            this.kind = kind;
            this.length = length;
            this.longs = longs;
            this.doubles = doubles;
            this.ints = ints;
            this.strings = strings;
            this.na = na;
        }

        int length() {
            return length;
        }

        static RColumn intFromLongs(String header, long[] values, boolean[] na) {
            return new RColumn(header, Kind.INT, values.length, values, null, null, null, na);
        }

        static RColumn realFromLongs(String header, long[] values, boolean[] na) {
            double[] doubles = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                doubles[i] = (double) values[i];
            }
            return new RColumn(header, Kind.REAL, values.length, null, doubles, null, null, na);
        }

        static RColumn real(String header, double[] values, boolean[] na) {
            return new RColumn(header, Kind.REAL, values.length, null, values, null, null, na);
        }

        static RColumn logical(String header, int[] values, boolean[] na) {
            return new RColumn(header, Kind.LOGICAL, values.length, null, null, values, null, na);
        }

        static RColumn string(String header, String[] values, boolean[] na) {
            return new RColumn(header, Kind.STRING, values.length, null, null, null, values, na);
        }

        static RColumn posixct(String header, double[] secondsSinceEpoch, boolean[] na) {
            return new RColumn(header, Kind.POSIXCT, secondsSinceEpoch.length, null,
                    secondsSinceEpoch, null, null, na);
        }

        void write(DataOutputStream out) throws IOException {
            switch (kind) {
                case INT -> writeIntsxp(out);
                case REAL -> writeRealsxp(out, false);
                case LOGICAL -> writeLglsxp(out);
                case STRING -> writeStrsxp(out, strings, na);
                case POSIXCT -> writeRealsxp(out, true);
            }
        }

        private void writeIntsxp(DataOutputStream out) throws IOException {
            out.writeInt(INTSXP);
            out.writeInt(length);
            for (int i = 0; i < length; i++) {
                if (na[i]) {
                    out.writeInt(NA_INT);
                } else {
                    out.writeInt((int) longs[i]);
                }
            }
        }

        private void writeRealsxp(DataOutputStream out, boolean posixct) throws IOException {
            out.writeInt(REALSXP | (posixct ? HAS_ATTR_BIT : 0));
            out.writeInt(length);
            for (int i = 0; i < length; i++) {
                if (na[i]) {
                    out.writeLong(NA_REAL_BITS);
                } else {
                    out.writeDouble(doubles[i]);
                }
            }
            if (posixct) {
                // class attribute trailer: class = c("POSIXct", "POSIXt").
                out.writeInt(LISTSXP | HAS_TAG_BIT);
                out.writeInt(SYMSXP);
                writeCharsxp(out, "class");
                writeStrsxp(out, new String[] {"POSIXct", "POSIXt"}, new boolean[2]);
                out.writeInt(NILVALUESXP);
            }
        }

        private void writeLglsxp(DataOutputStream out) throws IOException {
            out.writeInt(LGLSXP);
            out.writeInt(length);
            for (int i = 0; i < length; i++) {
                out.writeInt(na[i] ? NA_INT : ints[i]);
            }
        }

        /**
         * Static so attribute trailers (e.g. the {@code names} / {@code class} attributes
         * of a data frame) can emit a STRSXP without needing an {@code RColumn}
         * instance.
         */
        static void writeStrsxp(DataOutputStream out, String[] values, boolean[] na)
                throws IOException {
            out.writeInt(STRSXP);
            out.writeInt(values.length);
            for (int i = 0; i < values.length; i++) {
                if (na[i]) {
                    writeCharsxp(out, null);
                } else {
                    writeCharsxp(out, values[i] != null ? values[i] : "");
                }
            }
        }
    }
}
