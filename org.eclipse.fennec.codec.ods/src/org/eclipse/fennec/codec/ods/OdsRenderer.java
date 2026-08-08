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
package org.eclipse.fennec.codec.ods;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
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

import com.github.miachm.sods.Color;
import com.github.miachm.sods.LinkedValue;
import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import com.github.miachm.sods.Style;

/**
 * Renders a {@link TabularDocument} into an ODS spreadsheet via the
 * {@code sods} library.
 * <p>
 * One {@link Sheet} per {@link Table}; one extra {@code Sheet} per {@link JoinTable}.
 * The {@code Table.schema} field — when present — is prepended to the sheet name
 * as {@code <schema>.<name>}. The first row of every sheet is a header row, optionally
 * styled (bold, light-grey background, centered) via
 * {@link CodecOdsOptions#OPTION_STYLE_HEADER}; remaining rows carry the data
 * using sods' native cell value types (numeric / date / boolean / string).
 * <p>
 * No {@code SQL_TYPE} row is emitted: spreadsheet cells carry their type
 * natively, unlike CSV which needs a separate row to communicate the SQL types.
 * <p>
 * {@link FkCell}s (emitted in {@code SQL_TABLES} reference mode) become a
 * clickable {@link LinkedValue} pointing at the first row of the target sheet
 * ({@code #<target-sheet>.A1}) when
 * {@link CodecOdsOptions#OPTION_GENERATE_LINKS} is enabled (default). Sheets are
 * created in a first pass so an FK can resolve a link to a target table that is
 * visited later. Because the {@code sods} writer drops a cell's value once a
 * link is attached, a linked FK carries its id as the link's display text and is
 * therefore a string rather than a numeric cell.
 *
 * @since 1.0
 */
public class OdsRenderer implements TabularDocumentRenderer<OutputStream> {

    private static final Style HEADER_STYLE = new Style();
    static {
        HEADER_STYLE.setBackgroundColor(new Color("#d9d9d9"));
        HEADER_STYLE.setFontColor(new Color("#000000"));
        HEADER_STYLE.setBold(true);
        HEADER_STYLE.setTextAligment(Style.TEXT_ALIGMENT.Center);
    }

    @Override
    public void render(TabularDocument doc, OutputStream target, Map<String, Object> options)
            throws IOException {
        boolean styleHeader = resolveBoolean(options, CodecOdsOptions.OPTION_STYLE_HEADER, true);
        boolean adjustColumnWidth = resolveBoolean(options,
                CodecOdsOptions.OPTION_ADJUST_COLUMN_WIDTH, true);
        boolean generateLinks = resolveBoolean(options,
                CodecOdsOptions.OPTION_GENERATE_LINKS, true);

        SpreadSheet document = new SpreadSheet();

        // First pass: create every sheet up front so an FK cell can resolve a link
        // to its target Sheet — which may belong to a table visited later — before
        // any data is written. Insertion order is preserved (tables, then join
        // tables), matching the previous single-pass ordering.
        Map<Table, Sheet> sheetByTable = new LinkedHashMap<>();
        Map<EClass, Sheet> sheetByEClass = new LinkedHashMap<>();
        for (Table table : doc.getTables()) {
            int columnCount = table.getColumns().size();
            if (columnCount == 0) {
                continue;
            }
            Sheet sheet = new Sheet(buildSheetName(table.getSchema(), table.getName()),
                    1 + table.getRows().size(), columnCount);
            document.appendSheet(sheet);
            sheetByTable.put(table, sheet);
            if (table.getEClass() != null) {
                sheetByEClass.put(table.getEClass(), sheet);
            }
        }
        Map<JoinTable, Sheet> sheetByJoinTable = new LinkedHashMap<>();
        for (JoinTable jt : doc.getJoinTables()) {
            Sheet sheet = new Sheet(buildSheetName(jt.getSchema(), jt.getFileName()),
                    1 + jt.getRows().size(), 2);
            document.appendSheet(sheet);
            sheetByJoinTable.put(jt, sheet);
        }

        // Second pass: fill the sheets now that every link target exists.
        for (Map.Entry<Table, Sheet> entry : sheetByTable.entrySet()) {
            fillTable(entry.getValue(), entry.getKey(), styleHeader, adjustColumnWidth,
                    generateLinks, sheetByEClass);
        }
        for (Map.Entry<JoinTable, Sheet> entry : sheetByJoinTable.entrySet()) {
            fillJoinTable(entry.getValue(), entry.getKey(), styleHeader, adjustColumnWidth);
        }

        document.save(target);
    }

    // ========================================================================
    // Table → Sheet
    // ========================================================================

    private void fillTable(Sheet sheet, Table table, boolean styleHeader,
            boolean adjustColumnWidth, boolean generateLinks,
            Map<EClass, Sheet> sheetByEClass) {
        int columnCount = table.getColumns().size();

        // Header
        for (int c = 0; c < columnCount; c++) {
            Column col = table.getColumns().get(c);
            sheet.getRange(0, c).setValue(col.getHeader() != null ? col.getHeader() : "");
        }
        if (styleHeader) {
            sheet.getRange(0, 0, 1, columnCount).setStyle(HEADER_STYLE);
        }

        // Data rows
        int rowIndex = 1;
        for (Row row : table.getRows()) {
            int cellCount = Math.min(row.getCells().size(), columnCount);
            for (int c = 0; c < cellCount; c++) {
                writeCell(sheet.getRange(rowIndex, c), row.getCells().get(c),
                        generateLinks, sheetByEClass);
            }
            rowIndex++;
        }

        if (adjustColumnWidth) {
            adjustColumnsWidth(sheet);
        }
    }

    // ========================================================================
    // JoinTable → Sheet
    // ========================================================================

    private void fillJoinTable(Sheet sheet, JoinTable jt, boolean styleHeader,
            boolean adjustColumnWidth) {
        sheet.getRange(0, 0).setValue(jt.getOwnerCol());
        sheet.getRange(0, 1).setValue(jt.getTargetCol());
        if (styleHeader) {
            sheet.getRange(0, 0, 1, 2).setStyle(HEADER_STYLE);
        }

        int rowIndex = 1;
        for (JoinTableRow row : jt.getRows()) {
            sheet.getRange(rowIndex, 0).setValue(row.getOwnerId());
            sheet.getRange(rowIndex, 1).setValue(row.getTargetId());
            rowIndex++;
        }

        if (adjustColumnWidth) {
            adjustColumnsWidth(sheet);
        }
    }

    // ========================================================================
    // Cell dispatch
    // ========================================================================

    private void writeCell(Range cell, Cell value, boolean generateLinks,
            Map<EClass, Sheet> sheetByEClass) {
        if (value instanceof StringCell c) {
            cell.setValue(c.getValue() != null ? c.getValue() : "");
        } else if (value instanceof LongCell c) {
            cell.setValue(c.getValue());
        } else if (value instanceof DoubleCell c) {
            cell.setValue(c.getValue());
        } else if (value instanceof BigDecimalCell c) {
            // sods writes BigDecimal as numeric. Fall back to plain string if null.
            cell.setValue(c.getValue() != null ? c.getValue() : "");
        } else if (value instanceof BooleanCell c) {
            cell.setValue(c.isValue());
        } else if (value instanceof DateCell c) {
            if (c.getValue() == null) {
                cell.setValue("");
            } else {
                String fmt = c.getDateFormat();
                if (fmt != null && !fmt.isBlank()) {
                    // Use the configured format for display fidelity. Keeping it as a
                    // string sidesteps locale-specific date rendering by sods.
                    cell.setValue(new SimpleDateFormat(fmt).format(c.getValue()));
                } else {
                    cell.setValue(c.getValue());
                }
            }
        } else if (value instanceof BinaryCell c) {
            cell.setValue(c.getValue() != null
                    ? Base64.getEncoder().encodeToString(c.getValue())
                    : "");
        } else if (value instanceof FkCell c) {
            Sheet targetSheet = generateLinks ? sheetByEClass.get(c.getTargetEClass()) : null;
            if (targetSheet != null) {
                // sods drops the numeric value once a link is attached, so the FK
                // id travels as the link's display text. The cell becomes a
                // clickable string pointing at the target sheet's first row.
                cell.addLinkedValue(
                        new LinkedValue(String.valueOf(c.getTargetId()), targetSheet));
            } else {
                cell.setValue(c.getTargetId());
            }
        } else if (value instanceof EmptyCell) {
            cell.clear();
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static String buildSheetName(String schema, String name) {
        String base = name != null ? name : "";
        if (schema != null && !schema.isEmpty()) {
            base = schema + "." + base;
        }
        // ODS has no hard 31-char sheet-name limit like XLSX, but very long names
        // can still confuse downstream consumers — leave as is for now.
        return base;
    }

    private static void adjustColumnsWidth(Sheet sheet) {
        int columnsCount = sheet.getMaxColumns();
        int rowsCount = sheet.getMaxRows();
        for (int c = 0; c < columnsCount; c++) {
            int maxChars = 0;
            for (int r = 0; r < rowsCount; r++) {
                Object v = sheet.getRange(r, c).getValue();
                if (v != null) {
                    int len = String.valueOf(v).length();
                    if (len > maxChars) {
                        maxChars = len;
                    }
                }
            }
            // Heuristic: ~3 units per character, matching the gecko exporter's behaviour.
            sheet.setColumnWidth(c, (double) maxChars * 3);
        }
    }

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
}
