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

/**
 * Option keys for XLSX-format-specific knobs.
 * <p>
 * Shared tabular options ({@code referenceMode}, {@code columnTypes},
 * {@code fkColumnSuffix}, {@code schemas}, {@code multiValuedRefStrategy}) live
 * in {@link org.eclipse.fennec.codec.tabular.CodecTabularOptions} — the XLSX
 * provider reads them from there.
 *
 * @since 2026-06
 */
public interface CodecXlsxOptions {

    /**
     * Whether the header row should be styled (bold, light-grey background,
     * centered).
     * <p>
     * Value: {@code Boolean} or {@code "true"}/{@code "false"}. Default: {@code true}.
     */
    String OPTION_STYLE_HEADER = "codec.xlsx.styleHeader";

    /**
     * Whether to auto-size each column to fit its content.
     * <p>
     * Value: {@code Boolean} or {@code "true"}/{@code "false"}. Default: {@code true}.
     */
    String OPTION_ADJUST_COLUMN_WIDTH = "codec.xlsx.adjustColumnWidth";

    /**
     * Whether to freeze the header row so it stays visible on vertical scroll.
     * <p>
     * Value: {@code Boolean} or {@code "true"}/{@code "false"}. Default: {@code true}.
     */
    String OPTION_FREEZE_HEADER_ROW = "codec.xlsx.freezeHeaderRow";

    /**
     * Whether to render FK cells as hyperlinks pointing at the referenced
     * sheet's first row ({@code A1}). Applies to {@code FkCell}s emitted by
     * {@link org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode#SQL_TABLES}.
     * <p>
     * Value: {@code Boolean} or {@code "true"}/{@code "false"}. Default: {@code true}.
     */
    String OPTION_GENERATE_LINKS = "codec.xlsx.generateLinks";

    /**
     * Fallback date format ({@link java.text.SimpleDateFormat}-style pattern, or
     * a POI built-in like {@code "m/d/yy"}) applied to {@code DateCell}s that
     * do not carry a per-cell {@code dateFormat}.
     * <p>
     * Value: {@code String}. Default: {@code "m/d/yy h:mm"}.
     */
    String OPTION_DEFAULT_DATE_FORMAT = "codec.xlsx.defaultDateFormat";
}
