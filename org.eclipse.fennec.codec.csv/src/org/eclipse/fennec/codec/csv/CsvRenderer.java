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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.codec.tabular.model.tabular.Row;
import org.eclipse.fennec.codec.tabular.model.tabular.StringCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Table;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument;

import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import de.siegmar.fastcsv.writer.QuoteStrategies;
import de.siegmar.fastcsv.writer.QuoteStrategy;

/**
 * Renders a {@link TabularDocument} to CSV.
 * <p>
 * Output shape depends on {@link TabularDocument#getReferenceMode()}:
 * <ul>
 *   <li>{@link ReferenceMode#IGNORE} / {@link ReferenceMode#FLAT} — a single CSV
 *       containing header, type, and data rows. Only the first {@code Table} is
 *       emitted (these modes produce documents with exactly one Table).</li>
 *   <li>{@link ReferenceMode#SQL_TABLES} — a ZIP with one CSV per {@code Table}
 *       (in {@code <schema>/<name>.csv} layout when the Table has a schema) plus
 *       one CSV per {@code JoinTable}.</li>
 * </ul>
 * <p>
 * Each emitted CSV has a header row, an optional SQL-type row, and then the data
 * rows. The SQL-type row is controlled by
 * {@link CodecCsvOptions#OPTION_DATA_TYPE_IN_SECOND_ROW} (default {@code true}).
 * Cells are stringified by dispatching on the concrete {@link Cell} subclass.
 * <p>
 * Dialect knobs are read from the options map under the keys in
 * {@link CodecCsvOptions}: delimiter, quote mode, line ending, charset, and the
 * SQL-type-row toggle.
 *
 * @since 2026-05
 */
public class CsvRenderer implements TabularDocumentRenderer<OutputStream> {

    private static final char DEFAULT_DELIMITER = ',';
    private static final QuoteStrategy DEFAULT_QUOTE_STRATEGY = QuoteStrategies.REQUIRED;
    private static final LineDelimiter DEFAULT_LINE_DELIMITER = LineDelimiter.LF;

    @Override
    public void render(TabularDocument doc, OutputStream target, Map<String, Object> options)
            throws IOException {
        Charset charset = resolveCharset(options);
        char delimiter = resolveDelimiter(options);
        QuoteStrategy quoteStrategy = resolveQuoteStrategy(options);
        LineDelimiter lineDelimiter = resolveLineDelimiter(options);

        boolean typeRow = resolveTypeRow(options);

        if (doc.getReferenceMode() == ReferenceMode.SQL_TABLES) {
            renderZip(doc, target, charset, delimiter, quoteStrategy, lineDelimiter, typeRow);
        } else {
            renderSingleCsv(doc, target, charset, delimiter, quoteStrategy, lineDelimiter, typeRow);
        }
    }

    // ========================================================================
    // Single-CSV path (IGNORE / FLAT)
    // ========================================================================

    private void renderSingleCsv(TabularDocument doc, OutputStream target, Charset charset,
            char delimiter, QuoteStrategy quoteStrategy, LineDelimiter lineDelimiter, boolean typeRow)
            throws IOException {
        if (doc.getTables().isEmpty()) {
            return;
        }
        Table table = doc.getTables().get(0);
        CsvWriter csv = CsvWriter.builder()
                .fieldSeparator(delimiter)
                .quoteStrategy(quoteStrategy)
                .lineDelimiter(lineDelimiter)
                .build(target, charset);
        try {
            writeTable(csv, table, typeRow);
            csv.flush();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
        // Intentionally not closing csv — the target OutputStream is caller-owned.
    }

    // ========================================================================
    // ZIP path (SQL_TABLES)
    // ========================================================================

    private void renderZip(TabularDocument doc, OutputStream target, Charset charset,
            char delimiter, QuoteStrategy quoteStrategy, LineDelimiter lineDelimiter, boolean typeRow)
            throws IOException {
        ZipOutputStream zip = new ZipOutputStream(target);
        for (Table table : doc.getTables()) {
            String entryName = (table.getSchema() != null && !table.getSchema().isEmpty()
                    ? table.getSchema() + "/" : "") + table.getName() + ".csv";
            zip.putNextEntry(new ZipEntry(entryName));
            writeTableToZipEntry(zip, table, charset, delimiter, quoteStrategy, lineDelimiter, typeRow);
            zip.closeEntry();
        }
        for (JoinTable jt : doc.getJoinTables()) {
            String entryName = (jt.getSchema() != null && !jt.getSchema().isEmpty()
                    ? jt.getSchema() + "/" : "") + jt.getFileName() + ".csv";
            zip.putNextEntry(new ZipEntry(entryName));
            writeJoinTableToZipEntry(zip, jt, charset, delimiter, quoteStrategy, lineDelimiter, typeRow);
            zip.closeEntry();
        }
        // Write the ZIP's central directory but do not close the underlying OutputStream.
        zip.finish();
    }

    private void writeTableToZipEntry(ZipOutputStream zip, Table table, Charset charset,
            char delimiter, QuoteStrategy quoteStrategy, LineDelimiter lineDelimiter, boolean typeRow)
            throws IOException {
        // Suppress close of the underlying stream — CsvWriter.close() would otherwise
        // close the ZipOutputStream's entry stream, which is not what we want.
        OutputStream guarded = new FilterOutputStream(zip) {
            @Override public void close() { /* deliberate no-op */ }
        };
        CsvWriter csv = CsvWriter.builder()
                .fieldSeparator(delimiter)
                .quoteStrategy(quoteStrategy)
                .lineDelimiter(lineDelimiter)
                .build(guarded, charset);
        try {
            writeTable(csv, table, typeRow);
            csv.flush();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private void writeJoinTableToZipEntry(ZipOutputStream zip, JoinTable jt, Charset charset,
            char delimiter, QuoteStrategy quoteStrategy, LineDelimiter lineDelimiter, boolean typeRow)
            throws IOException {
        OutputStream guarded = new FilterOutputStream(zip) {
            @Override public void close() { /* deliberate no-op */ }
        };
        CsvWriter csv = CsvWriter.builder()
                .fieldSeparator(delimiter)
                .quoteStrategy(quoteStrategy)
                .lineDelimiter(lineDelimiter)
                .build(guarded, charset);
        try {
            csv.writeRecord(List.of(jt.getOwnerCol(), jt.getTargetCol()));
            if (typeRow) {
                csv.writeRecord(List.of("BIGINT", "BIGINT"));
            }
            for (JoinTableRow row : jt.getRows()) {
                csv.writeRecord(List.of(Long.toString(row.getOwnerId()), Long.toString(row.getTargetId())));
            }
            csv.flush();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    // ========================================================================
    // Table writing — header, types, data
    // ========================================================================

    private void writeTable(CsvWriter csv, Table table, boolean typeRow) {
        List<String> headers = new ArrayList<>(table.getColumns().size());
        List<String> types = new ArrayList<>(table.getColumns().size());
        for (Column col : table.getColumns()) {
            headers.add(col.getHeader() != null ? col.getHeader() : "");
            types.add(col.getSqlType() != null ? col.getSqlType() : "");
        }
        csv.writeRecord(headers);
        if (typeRow) {
            csv.writeRecord(types);
        }
        for (Row row : table.getRows()) {
            List<String> values = new ArrayList<>(row.getCells().size());
            for (Cell cell : row.getCells()) {
                values.add(cellToString(cell));
            }
            csv.writeRecord(values);
        }
    }

    // ========================================================================
    // Cell dispatch
    // ========================================================================

    private String cellToString(Cell cell) {
        if (cell instanceof StringCell c) {
            return c.getValue() != null ? c.getValue() : "";
        }
        if (cell instanceof LongCell c) {
            return Long.toString(c.getValue());
        }
        if (cell instanceof DoubleCell c) {
            return Double.toString(c.getValue());
        }
        if (cell instanceof BigDecimalCell c) {
            return c.getValue() != null ? c.getValue().toPlainString() : "";
        }
        if (cell instanceof BooleanCell c) {
            return Boolean.toString(c.isValue());
        }
        if (cell instanceof DateCell c) {
            if (c.getValue() == null) {
                return "";
            }
            String fmt = c.getDateFormat();
            if (fmt != null && !fmt.isBlank()) {
                return new SimpleDateFormat(fmt).format(c.getValue());
            }
            return c.getValue().toString();
        }
        if (cell instanceof BinaryCell c) {
            return c.getValue() != null ? Base64.getEncoder().encodeToString(c.getValue()) : "";
        }
        if (cell instanceof FkCell c) {
            return Long.toString(c.getTargetId());
        }
        if (cell instanceof EmptyCell) {
            return "";
        }
        return "";
    }

    // ========================================================================
    // Option resolution
    // ========================================================================

    private static Charset resolveCharset(Map<String, Object> options) {
        Object value = options.get(CodecCsvOptions.OPTION_CHARSET);
        if (value instanceof Charset c) {
            return c;
        }
        if (value instanceof String s && !s.isBlank()) {
            return Charset.forName(s);
        }
        return StandardCharsets.UTF_8;
    }

    private static char resolveDelimiter(Map<String, Object> options) {
        Object value = options.get(CodecCsvOptions.OPTION_DELIMITER);
        if (value instanceof Character c) {
            return c;
        }
        if (value instanceof String s && s.length() == 1) {
            return s.charAt(0);
        }
        return DEFAULT_DELIMITER;
    }

    private static QuoteStrategy resolveQuoteStrategy(Map<String, Object> options) {
        Object value = options.get(CodecCsvOptions.OPTION_QUOTE_MODE);
        if (value instanceof QuoteStrategy q) {
            return q;
        }
        if (value instanceof String s && !s.isBlank()) {
            return QuoteStrategies.valueOf(s.toUpperCase());
        }
        return DEFAULT_QUOTE_STRATEGY;
    }

    private static boolean resolveTypeRow(Map<String, Object> options) {
        Object value = options.get(CodecCsvOptions.OPTION_DATA_TYPE_IN_SECOND_ROW);
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s && !s.isBlank()) {
            return Boolean.parseBoolean(s);
        }
        return true; // default: emit the SQL-type row
    }

    private static LineDelimiter resolveLineDelimiter(Map<String, Object> options) {
        Object value = options.get(CodecCsvOptions.OPTION_LINE_ENDING);
        if (value instanceof LineDelimiter d) {
            return d;
        }
        if (value instanceof String s && !s.isBlank()) {
            return LineDelimiter.valueOf(s.toUpperCase());
        }
        return DEFAULT_LINE_DELIMITER;
    }
}
