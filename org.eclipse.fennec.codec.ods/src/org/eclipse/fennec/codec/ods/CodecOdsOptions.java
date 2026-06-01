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

/**
 * Option keys for ODS-format-specific knobs.
 * <p>
 * Shared tabular options ({@code referenceMode}, {@code columnTypes},
 * {@code fkColumnSuffix}, {@code schemas}, {@code multiValuedRefStrategy}) live
 * in {@link org.eclipse.fennec.codec.tabular.CodecTabularOptions} — the ODS
 * provider reads them from there.
 *
 * @since 2026-06
 */
public interface CodecOdsOptions {

    /**
     * Whether the header row should be styled (bold, light-grey background,
     * centered).
     * <p>
     * Value: {@code Boolean} or its {@code String} form ({@code "true"} /
     * {@code "false"}). Default: {@code true}.
     */
    String OPTION_STYLE_HEADER = "codec.ods.styleHeader";

    /**
     * Whether to adjust each column's width to fit the widest cell in that
     * column.
     * <p>
     * Value: {@code Boolean} or its {@code String} form. Default: {@code true}.
     */
    String OPTION_ADJUST_COLUMN_WIDTH = "codec.ods.adjustColumnWidth";
}
