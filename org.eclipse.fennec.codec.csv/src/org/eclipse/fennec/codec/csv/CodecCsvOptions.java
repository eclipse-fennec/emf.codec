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
 * Option keys for CSV-format-specific dialect knobs.
 * <p>
 * Shared tabular options ({@code referenceMode}, {@code columnTypes},
 * {@code fkColumnSuffix}, {@code schemas}, {@code multiValuedRefStrategy}) live
 * in {@link org.eclipse.fennec.codec.tabular.CodecTabularOptions} — the CSV
 * provider reads them from there.
 *
 * @since 2026-05
 */
public interface CodecCsvOptions {

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
