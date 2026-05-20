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

/**
 * Option keys specific to the CSV codec.
 * <p>
 * The CSV codec reuses the existing codec configuration for everything except
 * SQL type overrides and CSV-dialect knobs. In particular, column renaming is
 * handled by the standard per-feature {@code key} mechanism
 * ({@code codec.featureConfig} or {@code @codec(key="...")} EAnnotation), not
 * by a CSV-specific option.
 *
 * @since 2026-05
 */
public interface CodecCsvOptions {

    /**
     * Per-feature SQL type override.
     * <p>
     * Map value, keyed either by {@code EStructuralFeature} or by feature name
     * ({@code String}). When present, the value is written verbatim into the
     * type row instead of the default EMF -&gt; SQL mapping.
     * <p>
     * Required to express length/precision since {@code EString} alone cannot
     * distinguish {@code VARCHAR(50)} from {@code VARCHAR(255)} or {@code TEXT}.
     * <p>
     * Example: {@code Map.of("firstName", "VARCHAR(255)", "amount", "DECIMAL(15,2)")}
     */
    String OPTION_COLUMN_TYPES = "codec.csv.columnTypes";

    /**
     * CSV field delimiter character.
     * <p>
     * Default: {@code ','}
     */
    String OPTION_DELIMITER = "codec.csv.delimiter";

    /**
     * CSV quote strategy.
     * <p>
     * Values (fastcsv's {@code QuoteStrategies}):
     * {@code "REQUIRED"} (default — quote only when needed),
     * {@code "ALWAYS"} (quote every field),
     * {@code "NON_EMPTY"} (quote any non-empty field),
     * {@code "EMPTY"} (quote empty fields to differentiate from null).
     */
    String OPTION_QUOTE_MODE = "codec.csv.quoteMode";

    /**
     * CSV line ending.
     * <p>
     * Values: {@code "LF"} (default), {@code "CR"}, {@code "CRLF"}, {@code "PLATFORM"}.
     */
    String OPTION_LINE_ENDING = "codec.csv.lineEnding";

    /**
     * CSV charset.
     * <p>
     * Value: {@code java.nio.charset.Charset} or charset name ({@code String}).
     * Default: {@code UTF-8}.
     */
    String OPTION_CHARSET = "codec.csv.charset";
}
