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

import org.eclipse.fennec.codec.tabular.model.tabular.MultiValuedRefStrategy;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;

/**
 * Option keys shared by every tabular codec format (CSV, ODS, XLSX, ...).
 * <p>
 * Format-specific knobs (CSV's delimiter / quote / line-ending, an eventual
 * ODS' cell-style options, etc.) live in each format provider's own options
 * interface; the keys defined here are the ones the tabular shared
 * infrastructure ({@code TabularDocumentBuilder}) consumes regardless of which
 * format ends up rendering the resulting {@code TabularDocument}.
 * <p>
 * The {@link ReferenceMode} and {@link MultiValuedRefStrategy} enums are part
 * of the tabular EMF model (see {@code tabular.ecore}); they are referenced
 * here for ergonomic option typing — callers can pass either the enum constant
 * or its {@code String} name.
 *
 * @since 1.0
 */
public interface CodecTabularOptions {

    /**
     * Per-feature SQL type override.
     * <p>
     * Map value, keyed preferably by {@code EStructuralFeature} — that key is
     * unambiguous across {@code EClass}es (e.g. {@code Person.name} vs
     * {@code Address.name}) and is the only form that works reliably in
     * {@link ReferenceMode#SQL_TABLES} multi-table output. A {@code String}-keyed
     * map (resolved column header or raw feature name) is also accepted for the
     * single-{@code EClass} case but is ambiguous when the same name appears in
     * multiple classes.
     * <p>
     * When present, the value is written verbatim into the {@code Column.sqlType}
     * field of the produced {@code TabularDocument}, overriding the default
     * EMF&nbsp;&rarr;&nbsp;SQL mapping. Required to express length/precision since
     * {@code EString} alone cannot distinguish {@code VARCHAR(50)} from
     * {@code VARCHAR(255)} or {@code TEXT}.
     * <p>
     * Example:
     * <pre>
     * Map.of(personEClass.getEStructuralFeature("firstName"), "VARCHAR(255)",
     *        orderEClass.getEStructuralFeature("amount"),     "DECIMAL(15,2)")
     * </pre>
     */
    String OPTION_COLUMN_TYPES = "codec.tabular.columnTypes";

    /**
     * Reference-handling mode for the tabular output.
     * <p>
     * Value: a {@link ReferenceMode} enum constant, or the corresponding
     * {@code String} name. Default: {@link ReferenceMode#IGNORE}.
     */
    String OPTION_REFERENCE_MODE = "codec.tabular.referenceMode";

    /**
     * Suffix appended to a single-valued {@code EReference}'s resolved key (or
     * to a multi-valued ref's owner/target name in a join table) to form the
     * foreign-key column name in {@link ReferenceMode#SQL_TABLES} mode.
     * <p>
     * Default: {@code "_id"}, which matches JPA / Hibernate conventions and is
     * SQL-dialect-friendly (no quoting required). Example: with the default,
     * the {@code Warehouse.featured} reference produces a column named
     * {@code featured_id}.
     * <p>
     * Conceptually adjacent to the codec's global {@link
     * org.eclipse.fennec.codec.config.ConfigProperty#REF_KEY} but kept as a
     * distinct option because the syntactic roles differ: {@code REF_KEY} is an
     * <em>inner</em> JSON key (e.g. {@code "address": {"$ref": "..."}}), whereas
     * this is a <em>suffix</em> on the parent column name. Keeping them separate
     * avoids cross-format surprises when a user sets one without realising it
     * would affect the other.
     * <p>
     * Has no effect in {@link ReferenceMode#IGNORE} (no references emitted) or
     * {@link ReferenceMode#FLAT} (references flattened into dotted columns, no
     * FK column).
     * <p>
     * Value: {@code String}.
     */
    String OPTION_FK_COLUMN_SUFFIX = "codec.tabular.fkColumnSuffix";

    /**
     * Per-{@code EClass} (or per-{@code EPackage}) schema name. Used in
     * {@link ReferenceMode#SQL_TABLES} to lay out the produced ZIP/workbook so
     * that each EClass's table belongs to a schema — daanse's
     * {@code CsvDataImporter} treats subdirectories as DB schemas.
     * <p>
     * Value: {@code Map<?, String>} where each key is an {@code EClass} or an
     * {@code EPackage}. Lookup precedence per EClass: exact EClass match → the
     * EClass's containing EPackage → no schema.
     * <p>
     * Example: send every {@code Person} and {@code Address} to schema {@code hr},
     * but {@code AuditLog} to schema {@code ops}:
     * <pre>
     * Map.of(
     *     hrPackage,        "hr",
     *     auditLogEClass,   "ops")
     * </pre>
     * <p>
     * Schema names are written verbatim into {@code Table.schema} on the produced
     * {@code TabularDocument}; each format renderer interprets this as a
     * subdirectory (CSV/ZIP), a sheet-name prefix (ODS/XLSX), or ignores it.
     * Has no effect in {@link ReferenceMode#IGNORE} or {@link ReferenceMode#FLAT}
     * (both single-table layouts).
     */
    String OPTION_SCHEMAS = "codec.tabular.schemas";

    /**
     * How to represent <em>multi-valued</em> {@code EReference}s in
     * {@link ReferenceMode#SQL_TABLES} mode. Single-valued references are always
     * an FK column on the parent and are unaffected by this option.
     * <p>
     * Value: a {@link MultiValuedRefStrategy} enum constant, or its {@code String}
     * name. Default: {@link MultiValuedRefStrategy#PREFER_FK_COLUMN}.
     * <p>
     * See §8.7 of the CSV-codec investigation doc for the rationale behind
     * exposing the two shapes side by side instead of picking one.
     */
    String OPTION_MULTI_VALUED_REF_STRATEGY = "codec.tabular.multiValuedRefStrategy";
}
