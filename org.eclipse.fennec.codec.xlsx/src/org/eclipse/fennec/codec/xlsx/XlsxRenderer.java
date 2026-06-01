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
package org.eclipse.fennec.codec.xlsx;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.codec.tabular.TabularDocumentRenderer;
import org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell;
import org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell;
import org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Column;
import org.eclipse.fennec.codec.tabular.model.tabular.DateCell;
import org.eclipse.fennec.codec.tabular.model.tabular.DoubleCell;
import org.eclipse.fennec.codec.tabular.model.tabular.EmptyCell;
import org.eclipse.fennec.codec.tabular.model.tabular.FkCell;
import org.eclipse.fennec.codec.tabular.model.tabular.JoinTable;
import org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow;
import org.eclipse.fennec.codec.tabular.model.tabular.LongCell;
import org.eclipse.fennec.codec.tabular.model.tabular.StringCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Table;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument;

/**
 * Renders a {@link TabularDocument} into an XLSX workbook via Apache POI.
 * <p>
 * One {@link Sheet} per {@link Table}; one extra {@code Sheet} per {@link JoinTable}.
 * Sheet names follow {@code <schema>.<name>} when a schema is set, then are
 * truncated to POI's 31-character limit and disambiguated with a numeric suffix
 * on collisions.
 * <p>
 * Cells dispatch on the concrete
 * {@link org.eclipse.fennec.codec.tabular.model.tabular.Cell} subclass and use
 * POI's native value types (numeric / date / boolean / string). {@link FkCell}s
 * are written as a numeric value plus an optional
 * {@link HyperlinkType#DOCUMENT} hyperlink to {@code '<target-sheet>'!A1} when
 * {@link CodecXlsxOptions#OPTION_GENERATE_LINKS} is enabled (default).
 * <p>
 * {@code CellStyle}s are created once at the workbook level and reused — XLSX
 * caps the per-workbook style table at 64K entries, so caching matters.
 *
 * @since 2026-06
 */
public class XlsxRenderer implements TabularDocumentRenderer<OutputStream> {

    private static final int XLSX_SHEET_NAME_MAX_LENGTH = 31;
    private static final String DEFAULT_DATE_FORMAT = "m/d/yy h:mm";
    private static final String FONT_NAME = "Arial";
    private static final short FONT_HEIGHT_POINTS = 10;

    @Override
    public void render(TabularDocument doc, OutputStream target, Map<String, Object> options)
            throws IOException {
        boolean styleHeader = resolveBoolean(options, CodecXlsxOptions.OPTION_STYLE_HEADER, true);
        boolean adjustColumnWidth = resolveBoolean(options,
                CodecXlsxOptions.OPTION_ADJUST_COLUMN_WIDTH, true);
        boolean freezeHeader = resolveBoolean(options,
                CodecXlsxOptions.OPTION_FREEZE_HEADER_ROW, true);
        boolean generateLinks = resolveBoolean(options,
                CodecXlsxOptions.OPTION_GENERATE_LINKS, true);
        String defaultDateFormat = resolveString(options,
                CodecXlsxOptions.OPTION_DEFAULT_DATE_FORMAT, DEFAULT_DATE_FORMAT);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            RenderContext ctx = new RenderContext(workbook, styleHeader, adjustColumnWidth,
                    freezeHeader, generateLinks, defaultDateFormat);

            // First pass: pick sheet names so FK hyperlinks can resolve before sheets exist.
            Map<EClass, String> sheetNameByEClass = new LinkedHashMap<>();
            for (Table table : doc.getTables()) {
                String rawName = buildSheetName(table.getSchema(), table.getName());
                String finalName = ctx.uniqueSheetName(rawName);
                if (table.getEClass() != null) {
                    sheetNameByEClass.put(table.getEClass(), finalName);
                }
                ctx.sheetNameFor(table, finalName);
            }
            for (JoinTable jt : doc.getJoinTables()) {
                String rawName = buildSheetName(jt.getSchema(), jt.getFileName());
                String finalName = ctx.uniqueSheetName(rawName);
                ctx.sheetNameFor(jt, finalName);
            }
            ctx.sheetNamesByEClass = sheetNameByEClass;

            // Second pass: actually create sheets and fill them.
            for (Table table : doc.getTables()) {
                renderTable(ctx, table);
            }
            for (JoinTable jt : doc.getJoinTables()) {
                renderJoinTable(ctx, jt);
            }

            workbook.write(target);
        }
    }

    // ========================================================================
    // Table → Sheet
    // ========================================================================

    private void renderTable(RenderContext ctx, Table table) {
        int columnCount = table.getColumns().size();
        if (columnCount == 0) {
            return;
        }
        Sheet sheet = ctx.workbook.createSheet(ctx.sheetNameFor(table));

        // Header
        Row headerRow = sheet.createRow(0);
        for (int c = 0; c < columnCount; c++) {
            Column col = table.getColumns().get(c);
            Cell cell = headerRow.createCell(c);
            cell.setCellValue(col.getHeader() != null ? col.getHeader() : "");
            if (ctx.styleHeader) {
                cell.setCellStyle(ctx.headerStyle());
            }
        }

        // Data
        int rowIndex = 1;
        for (org.eclipse.fennec.codec.tabular.model.tabular.Row modelRow : table.getRows()) {
            Row dataRow = sheet.createRow(rowIndex);
            int cellCount = Math.min(modelRow.getCells().size(), columnCount);
            for (int c = 0; c < cellCount; c++) {
                writeCell(ctx, dataRow.createCell(c), modelRow.getCells().get(c));
            }
            rowIndex++;
        }

        finishSheet(ctx, sheet, columnCount);
    }

    // ========================================================================
    // JoinTable → Sheet
    // ========================================================================

    private void renderJoinTable(RenderContext ctx, JoinTable jt) {
        Sheet sheet = ctx.workbook.createSheet(ctx.sheetNameFor(jt));

        Row headerRow = sheet.createRow(0);
        Cell h0 = headerRow.createCell(0);
        h0.setCellValue(jt.getOwnerCol());
        Cell h1 = headerRow.createCell(1);
        h1.setCellValue(jt.getTargetCol());
        if (ctx.styleHeader) {
            h0.setCellStyle(ctx.headerStyle());
            h1.setCellStyle(ctx.headerStyle());
        }

        int rowIndex = 1;
        for (JoinTableRow modelRow : jt.getRows()) {
            Row dataRow = sheet.createRow(rowIndex);
            dataRow.createCell(0).setCellValue(modelRow.getOwnerId());
            dataRow.createCell(1).setCellValue(modelRow.getTargetId());
            // Hyperlinks on join-table rows would need owner/target ownerEClass info
            // resolved separately; deferred until a consumer asks for it.
            rowIndex++;
        }

        finishSheet(ctx, sheet, 2);
    }

    private void finishSheet(RenderContext ctx, Sheet sheet, int columnCount) {
        if (ctx.freezeHeader) {
            sheet.createFreezePane(0, 1);
        }
        if (ctx.adjustColumnWidth) {
            for (int c = 0; c < columnCount; c++) {
                sheet.autoSizeColumn(c);
            }
        }
    }

    // ========================================================================
    // Cell dispatch
    // ========================================================================

    private void writeCell(RenderContext ctx, Cell cell,
            org.eclipse.fennec.codec.tabular.model.tabular.Cell value) {
        if (value instanceof StringCell c) {
            cell.setCellValue(c.getValue() != null ? c.getValue() : "");
        } else if (value instanceof LongCell c) {
            cell.setCellValue(c.getValue());
        } else if (value instanceof DoubleCell c) {
            cell.setCellValue(c.getValue());
        } else if (value instanceof BigDecimalCell c) {
            if (c.getValue() != null) {
                cell.setCellValue(c.getValue().doubleValue());
            }
        } else if (value instanceof BooleanCell c) {
            cell.setCellValue(c.isValue());
        } else if (value instanceof DateCell c) {
            if (c.getValue() != null) {
                cell.setCellValue(c.getValue());
                String fmt = c.getDateFormat();
                cell.setCellStyle(ctx.dateStyle(fmt != null && !fmt.isBlank()
                        ? fmt
                        : ctx.defaultDateFormat));
            }
        } else if (value instanceof BinaryCell c) {
            cell.setCellValue(c.getValue() != null
                    ? Base64.getEncoder().encodeToString(c.getValue())
                    : "");
        } else if (value instanceof FkCell c) {
            cell.setCellValue(c.getTargetId());
            if (ctx.generateLinks && ctx.sheetNamesByEClass != null) {
                String targetSheet = ctx.sheetNamesByEClass.get(c.getTargetEClass());
                if (targetSheet != null) {
                    Hyperlink link = ctx.creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
                    link.setAddress("'" + targetSheet.replace("'", "''") + "'!A1");
                    cell.setHyperlink(link);
                    cell.setCellStyle(ctx.hyperlinkStyle());
                }
            }
        } else if (value instanceof EmptyCell) {
            cell.setBlank();
        }
    }

    // ========================================================================
    // Sheet-name helpers
    // ========================================================================

    private static String buildSheetName(String schema, String name) {
        String base = name != null ? name : "";
        if (schema != null && !schema.isEmpty()) {
            base = schema + "." + base;
        }
        return base;
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

    private static String resolveString(Map<String, Object> options, String key,
            String defaultValue) {
        Object value = options.get(key);
        if (value instanceof String s && !s.isBlank()) {
            return s;
        }
        return defaultValue;
    }

    // ========================================================================
    // Render context — caches workbook-level state
    // ========================================================================

    private static final class RenderContext {
        final XSSFWorkbook workbook;
        final boolean styleHeader;
        final boolean adjustColumnWidth;
        final boolean freezeHeader;
        final boolean generateLinks;
        final String defaultDateFormat;
        final CreationHelper creationHelper;

        private final Map<Object, String> sheetNamesByModelObject = new HashMap<>();
        private final java.util.Set<String> usedSheetNames = new java.util.HashSet<>();
        Map<EClass, String> sheetNamesByEClass;

        private CellStyle headerStyle;
        private CellStyle hyperlinkStyle;
        private final Map<String, CellStyle> dateStyles = new HashMap<>();
        private DataFormat dataFormat;
        private Font baseFont;
        private Font headerFont;
        private Font hyperlinkFont;

        RenderContext(XSSFWorkbook workbook, boolean styleHeader, boolean adjustColumnWidth,
                boolean freezeHeader, boolean generateLinks, String defaultDateFormat) {
            this.workbook = workbook;
            this.styleHeader = styleHeader;
            this.adjustColumnWidth = adjustColumnWidth;
            this.freezeHeader = freezeHeader;
            this.generateLinks = generateLinks;
            this.defaultDateFormat = defaultDateFormat;
            this.creationHelper = workbook.getCreationHelper();
        }

        String uniqueSheetName(String raw) {
            String safe = WorkbookUtil.createSafeSheetName(raw);
            if (safe.length() > XLSX_SHEET_NAME_MAX_LENGTH) {
                safe = safe.substring(0, XLSX_SHEET_NAME_MAX_LENGTH);
            }
            String candidate = safe;
            int suffix = 1;
            while (usedSheetNames.contains(candidate)) {
                String tag = "~" + suffix++;
                int keep = XLSX_SHEET_NAME_MAX_LENGTH - tag.length();
                candidate = (safe.length() > keep ? safe.substring(0, keep) : safe) + tag;
            }
            usedSheetNames.add(candidate);
            return candidate;
        }

        void sheetNameFor(Object modelObject, String name) {
            sheetNamesByModelObject.put(modelObject, name);
        }

        String sheetNameFor(Object modelObject) {
            return sheetNamesByModelObject.get(modelObject);
        }

        CellStyle headerStyle() {
            if (headerStyle == null) {
                headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setFont(headerFont());
            }
            return headerStyle;
        }

        CellStyle hyperlinkStyle() {
            if (hyperlinkStyle == null) {
                hyperlinkStyle = workbook.createCellStyle();
                hyperlinkStyle.setFont(hyperlinkFont());
            }
            return hyperlinkStyle;
        }

        CellStyle dateStyle(String formatString) {
            CellStyle cached = dateStyles.get(formatString);
            if (cached != null) {
                return cached;
            }
            if (dataFormat == null) {
                dataFormat = workbook.createDataFormat();
            }
            CellStyle style = workbook.createCellStyle();
            style.setFont(baseFont());
            style.setDataFormat(dataFormat.getFormat(formatString));
            dateStyles.put(formatString, style);
            return style;
        }

        private Font baseFont() {
            if (baseFont == null) {
                baseFont = workbook.createFont();
                baseFont.setFontName(FONT_NAME);
                baseFont.setFontHeightInPoints(FONT_HEIGHT_POINTS);
                baseFont.setColor(IndexedColors.BLACK.getIndex());
            }
            return baseFont;
        }

        private Font headerFont() {
            if (headerFont == null) {
                headerFont = workbook.createFont();
                headerFont.setFontName(FONT_NAME);
                headerFont.setFontHeightInPoints(FONT_HEIGHT_POINTS);
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.BLACK.getIndex());
            }
            return headerFont;
        }

        private Font hyperlinkFont() {
            if (hyperlinkFont == null) {
                hyperlinkFont = workbook.createFont();
                hyperlinkFont.setFontName(FONT_NAME);
                hyperlinkFont.setFontHeightInPoints(FONT_HEIGHT_POINTS);
                hyperlinkFont.setUnderline(Font.U_SINGLE);
                hyperlinkFont.setColor(IndexedColors.BLUE.getIndex());
            }
            return hyperlinkFont;
        }
    }

}
