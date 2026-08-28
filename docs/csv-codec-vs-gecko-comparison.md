# CSV exporter comparison — fennec-codec vs gecko

**Date:** 2026-05-21
**Scope:** Side-by-side capability comparison between
- **fennec-codec CSV** (`org.eclipse.fennec.codec.csv` in this repo)
- **gecko CSV exporter** (`org.gecko.emf.exporter.csv` in
  `/opt/git/geckoprojects-emf-utils`)

Both are write-only EMF→CSV serializers; both can produce either a single CSV or a ZIP of
per-`EClass` CSVs; both have been audited via direct source reading rather than user-facing
docs. This document lists capabilities both share, capabilities only ours has, and
capabilities only gecko has, so future work on either side can be scoped against a clear
baseline.

For the longer architectural narrative — gecko's design choices, daanse interop, the JPA/eorm
target output — see `csv-codec-investigation.md`. This file is a flat comparison only.

---

## 1. Side-by-side at a glance

| Capability | gecko | fennec-codec |
|---|---|---|
| Output modes | `FLAT`, `ZIP` | `IGNORE`, `SQL_TABLES`, `FLAT` (`codec.tabular.referenceMode`) |
| Single-CSV with flattened refs (FLAT) | ✓ | ✓ |
| ZIP of per-`EClass` CSVs (multi-table) | ✓ | ✓ (`SQL_TABLES`) |
| Single-CSV attributes-only mode | ✗ | ✓ (`IGNORE` — default) |
| Single-valued ref → FK on parent | ✓ (suffix `._ref`) | ✓ (suffix `_id`, configurable via `codec.tabular.fkColumnSuffix`) |
| Multi-valued ref → join-table CSV | ✓ (always) | ✓ (configurable via `codec.tabular.multiValuedRefStrategy`) |
| Multi-valued containment → FK on child | ✗ | ✓ (`PREFER_FK_COLUMN` strategy — JPA-idiomatic, default) |
| Containment vs non-containment distinction | ✓ (`OPTION_EXPORT_NONCONTAINMENT`) | ✓ (drives strategy choice) |
| Pseudo-ID column (`_id`) per EClass | ✓ | ✓ |
| Native `iD` attribute as separate `id` column | ✓ | ✗ (uses pseudo-ID only) |
| Schema subdirectories in ZIP (`<schema>/<EClass>.csv`) | ✗ | ✓ (`codec.tabular.schemas`, EClass or EPackage keys) |
| Type row (SQL types in row 2 of every CSV) | ✗ | ✓ (SQL types via `SqlTypeMapper`) |
| Metadata CSV (separate sheet describing the schema) | ✓ (`OPTION_EXPORT_METADATA`) | ✗ |
| Per-EAttribute SQL-type override | ✗ | ✓ (`codec.tabular.columnTypes`) |
| `useNamesFromExtendedMetaData` | ✗ | ✓ |
| Per-feature `@codec(key="...")` rename | ✗ | ✓ |
| `globalIgnoreFeatures` / per-feature `ignore` | ✗ | ✓ |
| `forceWrite` for transient/derived/volatile | ✗ | ✓ |
| `dateFormat` (per-feature / class / global) | ✗ | ✓ (applied in all modes) |
| Custom value writers (`CodecValueWriter`) | ✗ | ✓ — IGNORE mode only (deferred for SQL_TABLES / FLAT) |
| URI-based reference values (`OPTION_SHOW_URIS`) | ✓ | ✗ |
| Multi-root `Resource` (multiple top-level `EObject`s) | ✗ (single root) | ✓ (all modes) |
| Cycle protection | partial (self-ref check only, FLAT) | ✓ (path-stack in FLAT, identity-set in SQL_TABLES) |
| Configurable delimiter / quote mode / line ending | ✗ (library defaults) | ✓ (`codec.csv.delimiter` / `quoteMode` / `lineEnding`) |
| Configurable charset | inherited from stream | ✓ (`codec.csv.charset`) |
| Reader (CSV → `EObject`) | ✗ (separate `org.gecko.emf.csv` module, unrelated) | ✗ |

---

## 2. What both can do

- **Write `EObject`s to CSV.** Both are write-only; both target standard delimited CSV via
  the `de.siegmar.fastcsv` library.
- **Two output topologies.** FLAT (one CSV with references flattened into dotted columns)
  and a multi-table ZIP (one CSV per EClass).
- **Pseudo-ID per EClass.** Both auto-assign a stable id when emitting each EClass's table,
  used as the FK target.
- **Foreign-key column on the parent for single-valued references.** Gecko names it
  `<refname>._ref`; ours names it `<refname>_id` (configurable). Same mechanic.
- **Join-table CSV for many-to-many.** Both can emit a separate CSV with two FK columns
  linking parent and child. (Gecko does it for *every* multi-valued reference; ours does
  it for non-containment by default and optionally for all.)
- **Containment-aware traversal.** Both distinguish containment from non-containment
  references and emit different output for each — gecko gates non-containment behind
  `OPTION_EXPORT_NONCONTAINMENT`, we use the distinction to pick the FK-vs-join-table
  strategy.
- **In-memory buffer then write.** Both build the per-EClass tables in memory before
  emitting the ZIP entries (or the single CSV) — neither streams row-by-row across files.

---

## 3. Only fennec-codec

### Output topology

- **`IGNORE` mode** — a single CSV that contains only the root EObject's `EAttribute`s,
  references dropped. The simplest case and the default. Gecko has no equivalent; its
  closest match is FLAT, which always flattens references.
- **Schema subdirectories.** `codec.tabular.schemas` (a `Map<EClass|EPackage, String>`) maps
  EClasses (or whole packages) into subdirectories of the ZIP. Produces `hr/Person.csv`,
  `finance/Invoice.csv` etc., matching daanse's "subdirectory = DB schema" convention.
  Gecko writes everything to the ZIP root.
- **Multi-root `Resource` support.** A `Resource` can hold N top-level `EObject`s and all
  modes handle that — IGNORE produces N data rows in one CSV, SQL_TABLES walks all N as
  graph entry points (shared visited set), FLAT produces N rows with union-of-columns
  alignment. Gecko's exporter takes a single root.

### Per-EClass output

- **Type row** (row 2) carrying SQL type names (`VARCHAR`, `BIGINT`, `INTEGER`, `DOUBLE`,
  `BOOLEAN`, `DATE`, `TIMESTAMP`, …). Default mapping via `SqlTypeMapper`; override per
  feature via `codec.tabular.columnTypes`. Gecko's only schema-description mechanism is an
  optional separate "Metadata" CSV (see §4).
- **Configurable FK column suffix.** `codec.tabular.fkColumnSuffix` defaults to `_id` (JPA /
  Hibernate / daanse-friendly). Gecko hardcodes `._ref`, which needs SQL quoting in most
  dialects.

### Reference handling

- **`PREFER_FK_COLUMN` strategy** for multi-valued containment refs — emits the FK on the
  *child* table (matching JPA's `OneToMany`/`@JoinColumn` idiom and the `test1` JPA
  fixture). Gecko always emits a separate join-table for multi-valued refs.
- **Configurable strategy.** `codec.tabular.multiValuedRefStrategy` exposes both shapes
  (`PREFER_FK_COLUMN` for JPA-idiomatic; `ALWAYS_JOIN_TABLE` for gecko-style uniformity).

### Codec configuration pipeline

The CSV codec plugs into the same `CodecResource` pipeline as the JSON / CBOR / YAML / BSON
codecs, which means every codec option that resolves to "what should this column be named"
or "should this field be emitted" flows through unchanged:

- **`useNamesFromExtendedMetaData`** — header uses EMF `ExtendedMetaData` names.
- **Per-feature `@codec(key="...")`** — renames the column (including for dotted FLAT columns
  and FK columns).
- **`globalIgnoreFeatures` / per-feature `ignore`, `ignoreWrite`** — drops columns from the
  header *and* their values from the data rows.
- **`forceWrite`** — brings transient / derived / volatile features into the header.
- **`dateFormat`** (global / per-class / per-feature) — `EDate` values rendered via
  `SimpleDateFormat`. Applies to IGNORE, SQL_TABLES, and FLAT.
- **Custom value writers** (`CodecValueWriter`) — work in IGNORE mode through the standard
  Jackson pipeline; deferred for SQL_TABLES / FLAT (those bypass the pipeline).

Gecko has none of these — it consults the EMF structural-feature names directly and uses
`String.valueOf()` for values.

### Dialect knobs

- `codec.csv.delimiter` (default `,`)
- `codec.csv.quoteMode` (`REQUIRED` / `ALWAYS` / …)
- `codec.csv.lineEnding` (`LF` / `CRLF` / `PLATFORM`)
- `codec.csv.charset` (default UTF-8)

Gecko exposes none of these; it uses the fastcsv builder defaults and inherits charset from
the supplied `OutputStream`.

### Robustness

- **Cycle protection.** FLAT uses a per-row identity-based path stack to break back-edges
  without spuriously deduplicating sibling references. SQL_TABLES uses an identity-based
  visited set during BFS. Gecko has a self-reference check in FLAT mode only
  (`EMFCSVExporter.java:420-424`); non-trivial cycles aren't explicitly handled.

---

## 4. Only gecko

### Native `iD` attribute as a second `id` column

Gecko emits **two** id columns per row: column 0 is the synthetic UUID `_id`, column 1 is
the native EMF `iD`-flagged attribute's value (or empty if the EClass has no such
attribute). `AbstractEMFExporter.java:92-98`. Our codec emits only the pseudo-`_id` BIGINT;
a feature with `iD=true` just becomes a regular attribute column, distinguishable only by
the EClass schema.

This affects users who load the CSV into a database and want the original EMF identity
preserved as a separate column — gecko gives it to them; we don't (today).

### Metadata CSV per EClass (schema sheet)

`OPTION_EXPORT_METADATA` (default true in ZIP mode) emits a separate `<EClass>Metadata.csv`
per EClass with columns:

```
Name | Type | isMany | isRequired | isID | Default value | Documentation
```

`AbstractEMFExporter.java:73-74`. It's a human/tooling-readable description of the schema.

Our codec's type row (single SQL type per column) is closer to "what does the SQL importer
need to CREATE TABLE" — it doesn't carry isMany / isRequired / EMF docs strings. Different
target audience, different metadata.

### URI-based reference values (`OPTION_SHOW_URIS`)

When `OPTION_SHOW_URIS` is true (gecko's default), FK columns can contain the EMF URI of the
target instead of its pseudo-id. Useful when references cross resources / files. Our codec
always emits the pseudo-id of the resolved target instance; cross-resource references
aren't a first-class output.

### `OPTION_EXPORT_NONCONTAINMENT` toggle

Gecko gates whether non-containment references are emitted at all behind a single option,
defaulting to `false` (`AbstractEMFExporter.java:273-275`). Our codec always emits
non-containment references when the reference mode is SQL_TABLES or FLAT — the equivalent
on our side is the ignore mechanics (`globalIgnoreFeatures`, per-feature `ignore`), which
are per-EReference rather than a global flag.

### `OPTION_LOCALE`

Logged at `EMFCSVExporter.java:113`. Not wired into actual value formatting (no
`SimpleDateFormat(locale=…)` etc. in the codepath), so its functional reach is limited —
but the option exists. Our codec has no locale knob.

---

## 5. Architectural shape (not a feature comparison, but explains the differences)

Worth noting briefly so the asymmetries above make sense:

- **Gecko** is a *standalone* `EMFExporter` service. It owns the entire export pipeline:
  matrix construction, value formatting, file naming, ZIP packaging. Adding a new option
  means changing the exporter itself.
- **Ours** is a `CodecFormatProvider` plugged into `CodecResource`, the same shell that
  hosts the JSON/CBOR/YAML/BSON codecs. For IGNORE mode that means the standard Jackson
  pipeline drives the writes (custom value writers, dateFormat, ExtendedMetaData names —
  all flow through for free). For SQL_TABLES and FLAT, the delegate bypasses the pipeline
  and does its own walk (which is why custom value writers don't yet work there), but it
  still receives the operation `ConfigurationResolver` and applies `FeatureConfig` per
  feature.

This is why we inherit the entire codec option family without coding it CSV-specifically,
and why gecko has none of it.
