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

import java.io.IOException;
import java.util.Map;

import org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument;

/**
 * Renders a built {@link TabularDocument} to a format-specific target.
 * <p>
 * Format providers (CSV, ODS, XLSX, ...) implement this interface to convert the
 * shared in-memory {@code TabularDocument} into their native output. The
 * {@link TabularDocumentBuilder} produces the document; this SPI is the only
 * piece each format has to author beyond the {@code FormatDelegate} /
 * {@code CodecFormatProvider} plumbing.
 * <p>
 * Responsibilities of an implementation:
 * <ul>
 *   <li>Decide the container shape for the format (single file, ZIP of files,
 *       a {@code Workbook} with sheets, ...).</li>
 *   <li>Dispatch on the concrete {@link org.eclipse.fennec.codec.tabular.model.tabular.Cell}
 *       subclass to render each cell with the format's native value types
 *       (string, number, date with format, hyperlink for FK, ...).</li>
 *   <li>Honour the {@code Table.schema} field according to the format's idiom
 *       (subdirectory in CSV/ZIP, sheet-name prefix in ODS/XLSX, ignored
 *       otherwise).</li>
 *   <li>Honour any format-specific options not covered by
 *       {@link CodecTabularOptions} (e.g. CSV's delimiter / quote / line-ending,
 *       a spreadsheet's cell-style hints).</li>
 * </ul>
 * <p>
 * The renderer must <em>not</em> close the supplied {@code target} —
 * stream/workbook ownership stays with the caller.
 *
 * @param <T> the format-specific output target type
 *            (e.g. {@code OutputStream} for CSV / ODS / XLSX)
 * @since 1.0
 */
public interface TabularDocumentRenderer<T> {

    /**
     * Renders the given document into the target.
     *
     * @param doc the document to render, never {@code null}
     * @param target the format-specific target, never {@code null}
     * @param options the save-time options map, never {@code null} (may be empty)
     * @throws IOException if writing fails
     */
    void render(TabularDocument doc, T target, Map<String, Object> options) throws IOException;
}
