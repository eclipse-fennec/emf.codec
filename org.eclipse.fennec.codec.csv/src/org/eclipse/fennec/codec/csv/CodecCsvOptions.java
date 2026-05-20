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
     * Map value, keyed preferably by {@code EStructuralFeature} — that key is
     * unambiguous across {@code EClass}es (e.g. {@code Person.name} vs
     * {@code Address.name}) and is the only form that works reliably in
     * {@link ReferenceMode#SQL_TABLES} multi-table output. A {@code String}-keyed
     * map (feature name) is also accepted for the single-{@code EClass} case but
     * is ambiguous when the same name appears in multiple classes.
     * <p>
     * When present, the value is written verbatim into the type row instead of
     * the default EMF -&gt; SQL mapping.
     * <p>
     * Required to express length/precision since {@code EString} alone cannot
     * distinguish {@code VARCHAR(50)} from {@code VARCHAR(255)} or {@code TEXT}.
     * <p>
     * Example:
     * <pre>
     * Map.of(personEClass.getEStructuralFeature("firstName"), "VARCHAR(255)",
     *        orderEClass.getEStructuralFeature("amount"),     "DECIMAL(15,2)")
     * </pre>
     */
    String OPTION_COLUMN_TYPES = "codec.csv.columnTypes";

    /**
     * Reference-handling mode for CSV output.
     * <p>
     * Value: a {@link ReferenceMode} enum constant, or the corresponding
     * {@code String} name. Default: {@link ReferenceMode#IGNORE}.
     */
    String OPTION_REFERENCE_MODE = "codec.csv.referenceMode";

    /**
     * Reference-handling mode controlling whether and how {@code EReference}s
     * are emitted into the CSV output.
     */
    enum ReferenceMode {

        /**
         * Drop all references — only {@code EAttribute}s are emitted.
         * Default for single-{@code EObject} CSV output.
         */
        IGNORE,

        /**
         * Multi-table output: one CSV per visited {@code EClass}, with foreign-key
         * columns (e.g. {@code address_id}) for single-valued references and
         * separate mapping CSVs for many-valued references. The result is a ZIP
         * containing all CSVs, intended for relational-DB import (e.g. via the
         * daanse {@code CsvDataImporter}).
         */
        SQL_TABLES,

        /**
         * Single-CSV output with references flattened into indexed columns
         * (e.g. {@code contacts.0.type, contacts.0.value, contacts.1.type, …}).
         * Reserved for a later iteration — not implemented yet.
         */
        FLAT
    }

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
