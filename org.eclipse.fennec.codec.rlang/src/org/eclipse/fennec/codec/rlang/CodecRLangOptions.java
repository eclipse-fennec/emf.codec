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

/**
 * Option keys for R Language ({@code .RData}) format-specific knobs.
 * <p>
 * Shared tabular options ({@code referenceMode}, {@code columnTypes},
 * {@code fkColumnSuffix}, {@code schemas}, {@code multiValuedRefStrategy})
 * live in {@link org.eclipse.fennec.codec.tabular.CodecTabularOptions} — the
 * R-Language provider reads them from there.
 *
 * @since 1.0
 */
public interface CodecRLangOptions {

    /**
     * Whether to emit one data frame per file in a ZIP archive (each entry is a
     * single-data-frame {@code .RData}), or all data frames packed inside a
     * single {@code .RData} as named variables.
     * <p>
     * The {@code .RData} {@link
     * org.eclipse.emf.ecore.resource.Resource.Factory ResourceFactory} defaults
     * this to {@code false} (single file); the {@code .rdataz} factory defaults
     * to {@code true} (ZIP of single-data-frame files).
     * <p>
     * Value: {@code Boolean} or {@code "true"}/{@code "false"}. Default:
     * {@code false}.
     */
    String OPTION_DATAFRAME_PER_FILE = "codec.rlang.dataframePerFile";
}
