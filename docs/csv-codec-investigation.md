# CSV Codec — Investigation Report

**Date:** 2026-05-20
**Goal:** Scope what it would take for fennec-codec to serialize an `EObject` to a CSV file with
header row = feature names, second row = feature types, third row = values. Multi-object support
is explicitly deferred to a future wrapper. Long-term target: migrate the exporters from
`/opt/git/geckoprojects-emf-utils` into the codec.

---

## 1. The old "CSV codec" is not what we want

`/opt/git/org.gecko.codec/org.eclipse.fennec.codec.csv` is misleadingly named. It does **not** produce CSV.

- `CodecCSVParser` actually reads URL-style query strings (`key=value&key2=value2`) into a single
  map and feeds that through the JSON deserializer
  (`QueryStringParser.java:31`).
- `CSVCodecFactoryConfigurator.getGenFactory()` returns `null`
  (`CSVCodecFactoryConfigurator.java:46`) — serialization is **not implemented at all**.
- `CSVResourceFactory.java:33` registers the factory under the `csv` file extension, but it only
  handles inbound URL-query payloads from sensor/IoT-style sources.

**Conclusion:** this is an inbound adapter for query-string data, not a CSV writer. We cannot
reuse it for our goal.

---

## 2. The real CSV writer lives in `geckoprojects-emf-utils`

`org.gecko.emf.exporter.csv/.../EMFCSVExporter.java` is the production CSV exporter (~1700 LOC).
Highlights:

- Implements `EMFExporter.exportEObjectsTo(List<EObject>, OutputStream, options)` — a stand-alone
  service, **not** a `Resource`.
- Uses the `de.siegmar.fastcsv` library (lean, no Jackson dependency).
- Two export modes via `EMFCSVExportOptions`:
  - **FLAT** — one CSV, references flattened into dot-named columns.
  - **ZIP** — one CSV per EClass, packed together with a mapping table.
- Builds an in-memory `Table<Integer, Integer, Object>` per "matrix name" (one per EClass), with
  row 1 = column headers, then flattens references into columns:
  - one-ref → `field.subField`
  - many-ref → `field.0.subField, field.1.subField, …`
- It does **not** emit a "type" row — that's a new requirement on our side.

**Conclusion:** logic is reusable but expensive to port wholesale. Good reference for the
multi-object follow-up; overkill for a single-EObject MVP.

---

## 3. How fennec-codec wires new formats

Looking at the BSON/CBOR/YAML modules and `CodecResource.doSaveWithFormat()`
(`CodecResource.java:432`), the established extension pattern is:

1. Implement `CodecFormatProvider<S, T>` (e.g. `<InputStream, OutputStream>`) —
   `getFormatId()`, `createWriter(target)`, `createReader(source)`, file extensions, content types.
2. Implement `FormatDelegate<T>` (write) and `FormatReaderDelegate<S>` (read) — a Jackson-like
   streaming API: `writeStartObject`, `writeName`, `writeString`, `writeInt`, …
3. `CodecResource` wraps the delegate with `FormatDelegateGenerator`/`FormatDelegateParser`, so
   existing serializers (`CodecEObjectSerializer`, attribute/reference entries) work unchanged.
4. A new bundle is added alongside `org.eclipse.fennec.codec.{bson,cbor,yaml}` with a small
   `bnd.bnd` (see CBOR/YAML for the shortest example).

**The friction for CSV:** the `FormatDelegate` API is streaming and tree-shaped. CSV is row-shaped
and needs the header row decided *before* any data is written. So a CSV `FormatDelegate` has to
**buffer** field-name/value pairs in insertion order and emit rows on `close()` / `flush()`.

---

## 4. Recommended MVP — `org.eclipse.fennec.codec.csv`

A buffering format provider, scoped to a single `EObject` with `EAttribute`s only.

```
org.eclipse.fennec.codec.csv/
  bnd.bnd
  src/org/eclipse/fennec/codec/csv/
    CsvFormatProvider.java       // implements CodecFormatProvider<InputStream, OutputStream>
    CsvFormatDelegate.java       // buffers (name -> value) pairs; emits header/type/data on close
    CsvTypeInfoResolver.java     // (EObject root) -> { feature -> EDataType name } row
```

### Concrete behaviour

- **Row 1 — header:** feature names in the order they appear, optionally overridden via the
  `codec.csv.columnNames` option (see §5).
- **Row 2 — types:** SQL type names (`BIGINT`, `VARCHAR`, `DECIMAL`, …), derived from a built-in
  EMF → SQL default table, optionally overridden per feature via the `codec.csv.columnTypes`
  option (see §5). SQL types are the target so the produced CSV can be ingested directly into a
  relational database.
- **Row 3 — values:** the buffered values, with `null` → empty cell.
- **Out of scope for MVP:** references, multi-valued features, contained `EObject`s — emit empty
  cell + warning diagnostic, mirroring how the BSON provider rejects array root in
  `CodecResource.java:464`. Reference handling lands in a follow-up (see §5).
- **Writer-only first.** `createReader` can throw `UnsupportedOperationException` until parsing
  is needed — the old gecko parser handles a niche unrelated use case anyway.
- **CSV library:** `de.siegmar.fastcsv` (already used by the exporter, lean). Apache Commons CSV
  is the alternative; `fastcsv` is the lower-friction choice.

### Why this shape

- Drops into `CodecResource` with zero changes — the format-provider seam already exists.
- Keeps the door open for the multi-object / reference-flattening pass: when we tackle
  multi-`EObject`, the wrapper can route N `EObject`s of the same `EClass` through the same
  delegate, accumulating rows before emit. The header/type rows stay identical to the
  single-object case.
- Avoids a 1700-LOC re-port until we actually need the FLAT/ZIP + reference-flattening features
  the exporter offers.

### Alternatives considered

- **Option B — wrap the existing `EMFCSVExporter` as a `CodecFormatProvider`.** Less integration
  with the codec's serialization pipeline, but reuses proven logic. Heavier, more dependencies,
  doesn't match the `FormatDelegate` streaming model.
- **Option C — use `jackson-dataformat-csv`.** Pre-build a CSV schema from `EClass` features,
  hand it to Jackson's `CsvFactory`. Plays well with the existing `JacksonFormatProvider`
  machinery, but the schema-required-up-front nature still requires the same pre-pass.

---

## 5. Resolved direction

The "type" row carries **SQL type names** so the produced CSV is directly ingestible into a
relational database. The codec does not require any external mapping artifact (no `.eorm`, no
schema file) — it works from the EMF model plus a small set of codec options.

### 5.1 Default EMF → SQL type mapping

The codec ships with a built-in mapping table. This is sufficient for any plain ecore without
extra configuration.

| EMF type | SQL type |
|---|---|
| `EString` | `VARCHAR` |
| `EBoolean` / `EBooleanObject` | `BOOLEAN` |
| `EInt` / `EIntegerObject` | `INTEGER` |
| `ELong` / `ELongObject` | `BIGINT` |
| `EShort` / `EShortObject` | `SMALLINT` |
| `EFloat` / `EFloatObject` | `REAL` |
| `EDouble` / `EDoubleObject` | `DOUBLE PRECISION` |
| `EBigDecimal` | `DECIMAL` |
| `EBigInteger` | `NUMERIC` |
| `EDate` | `TIMESTAMP` |
| `EByteArray` | `BLOB` |
| `EEnum` | `VARCHAR` |
| `EChar` / `ECharacterObject` | `CHAR(1)` |

### 5.2 Per-feature overrides via codec options

**Column renaming is *not* a new option.** It is already covered by the existing per-feature
`key` mechanism in the codec — either via the `codec.featureConfig` map or via an
`@codec(key="...")` EAnnotation on the feature. The CSV codec inherits this automatically (see
§7 for the full flow). This means a user who wants `firstName` to appear as `first_name` in the
header sets the standard codec key — no CSV-specific renaming option needed.

Only **one** genuinely CSV-specific override is required, because SQL type is not in the
existing config model:

```
codec.csv.columnTypes : Map<EStructuralFeature, String>
                        or Map<String, String>  (keyed by feature name)
    // Overrides the SQL type for a given feature.
    // Required to express length/precision since EString alone can't
    // distinguish VARCHAR(50) from VARCHAR(255) or TEXT.
    // Examples:
    //   Person.firstName -> "VARCHAR(255)"
    //   Order.amount     -> "DECIMAL(15,2)"
```

### 5.3 Reference-handling modes (post-MVP)

When multi-object / reference support lands, three modes are exposed. See §8 for the full
design discussion (how gecko handled this and the architectural options for porting).

| Mode | Layout | Use case |
|------|--------|----------|
| `SQL_TABLES` (default) | ZIP (or directory) of CSVs, one per `EClass`. FK columns for all references (containment → FK back to parent; non-containment → FK to target). Many-to-many uses a separate mapping CSV. | Loading into a relational DB. |
| `FLAT` | Single CSV. References flattened into indexed columns (`contacts.0.type, contacts.0.value, contacts.1.type, …`). | Spreadsheet / single-sheet ingestion. |
| `HYBRID` | Containment flattened, non-containment as FK column only. | When children are 1:1-ish and you want one row per parent. |

```
codec.csv.referenceMode      : SQL_TABLES | FLAT | HYBRID   (default: SQL_TABLES)
codec.csv.referenceOverrides : Map<EReference, ReferenceMode>   // per-reference override
```

### 5.4 CSV dialect options

```
codec.csv.delimiter   : char       (default: ',')
codec.csv.quoteMode   : enum       (always / minimal / non-numeric / never)
codec.csv.lineEnding  : enum       (lf / crlf / platform; default: lf)
codec.csv.charset     : Charset    (default: UTF-8)
```

---

## 6. Suggested next steps

1. Create `org.eclipse.fennec.codec.csv` bundle (model after `org.eclipse.fennec.codec.yaml` for
   the bnd.bnd shape).
2. Implement the default EMF → SQL type mapping table (§5.1) as a static helper.
3. Implement `CsvFormatProvider` + `CsvFormatDelegate` (writer path only) backed by
   `de.siegmar.fastcsv`. Scope: single `EObject`, `EAttribute`s only, no references.
4. Wire the `codec.csv.columnTypes` option (§5.2) through `CodecOptions`. Column renaming reuses
   the existing per-feature `key` mechanism (§7) — no new option needed.
5. Add a `CsvCoreRoundTripTCKTest`-style test, but writer-only (no round-trip yet).
6. Once green: spec out the multi-object wrapper and start porting reference-flattening from
   `EMFCSVExporter`, exposing `referenceMode` (§5.3).

---

## 7. Interaction with existing CodecOptions

Because the CSV codec plugs in via the `CodecFormatProvider` seam, it inherits the full
serialization pipeline (`CodecResource` → `mapper` → `CodecEObjectSerializer` →
`AttributeSerializationEntry` → `gen.writeName(...)`). All option resolution happens **upstream**
of the `FormatDelegate`, so the delegate just receives already-resolved names and primitive
values.

### 7.1 Name resolution chain (the `useNamesFromExtendedMetaData` example)

`ConfigurationResolver.resolveDefaultFeatureKey()`
(`ConfigurationResolver.java:536-547`) reads the global option and decides what name to use:

```java
private String resolveDefaultFeatureKey(EStructuralFeature feature) {
    Boolean useExtendedMetaData = getGlobalProperty(ConfigProperty.USE_NAMES_FROM_EXTENDED_METADATA);
    if (Boolean.TRUE.equals(useExtendedMetaData)) {
        String extendedMetaDataName = getExtendedMetaDataName(feature);
        if (extendedMetaDataName != null && !extendedMetaDataName.isEmpty()) {
            return extendedMetaDataName;
        }
    }
    return feature.getName();
}
```

That resolved string becomes `FeatureConfig.key`, then `AttributeSerializationEntry.serialize()`
calls `gen.writeName(config.getKey())` (`AttributeSerializationEntry.java:153`). `gen` is the
`FormatDelegateGenerator` wrapping `CsvFormatDelegate`, so the delegate's `writeName(...)`
receives the **already-resolved** name and writes it straight into the header row.

→ Setting `codec.useNamesFromExtendedMetaData=true` works for CSV out of the box. No CSV-specific
code needed.

### 7.2 Options that work for free

These all participate via the same serializer chain:

| Option | CSV effect |
|---|---|
| `codec.useNamesFromExtendedMetaData` | Header row uses EMF `ExtendedMetaData` names |
| `codec.ignoreFeatures` and per-feature `ignore` | Feature dropped — column not emitted |
| `codec.forceWrite` / `codec.serializeEmpty` / `codec.serializeDefault` | Controls whether empty/default values produce empty cells or are skipped |
| Custom value writers (`codec.featureValueWriters`) | Stringifies the value before it reaches the CSV cell |
| Per-feature key override (annotation or `codec.featureConfig` + `key`) | **This is the column-rename mechanism** — `firstName` → `first_name` |
| `codec.dateFormat` (global / per-EClass / per-feature) | Formats `EDate` values written into CSV cells |

### 7.3 Options needing CSV-specific defaults or overrides

| Option | Issue |
|---|---|
| `codec.typeStrategy` / `codec.typeKey` | If enabled, a `_type` column would appear in every row. CSV should **default `typeStrategy=NONE`** since each file is per-`EClass`. |
| `codec.idStrategy` / `codec.idKey` | Risks clashing with an existing `iD=true` `EAttribute`. CSV should default to "use the `iD`-flagged `EAttribute` as the `id` column, no extra column." |
| `codec.referenceFormat` (`flat` / `inline` / `id-only`) | Has nothing to do with CSV's row/table layout. **Superseded by `codec.csv.referenceMode`** (§5.3). |
| Indent / pretty-print / Jackson features | Not applicable to CSV. Ignored. |

### 7.4 Consequence for the option surface

Because the existing key mechanism already covers column renaming, the CSV codec adds a very
small option surface of its own:

- `codec.csv.columnTypes` — SQL-type override (genuinely new)
- `codec.csv.referenceMode` + `codec.csv.referenceOverrides` — layout choice (post-MVP)
- `codec.csv.delimiter` / `quoteMode` / `lineEnding` / `charset` — dialect knobs

Everything else (`columnNames`, ignore, forceWrite, value writers, date formatting,
extended-metadata names) reuses the existing codec configuration system unchanged.

---

## 8. Multi-table reference handling (SQL_TABLES mode)

This section captures the design discussion around producing **one CSV per `EClass`** with
FK columns linking rows across files — the layout suitable for direct relational-database
import. It walks through how the legacy gecko exporter solved this, the architectural
mismatch with our `FormatDelegate` API, and the options for porting.

### 8.1 How the gecko exporter handles multi-table output

`AbstractEMFExporter` builds an **in-memory graph of matrices** before writing anything. Each
matrix becomes one CSV in the ZIP output.

#### 8.1.1 One matrix per EClass

`constructEClassMatrixName(eClass)` produces names like `Person`, `Address`. Stored in a
`Map<String, Table<Integer, Integer, Object>>`, where the value is Guava's `Table` indexed by
`(row, column)`. Row 1 holds column headers; row 2+ holds data.

#### 8.1.2 Every row gets two ID columns

```
Column 0 (_id)    — internal pseudo-ID generated by the exporter (always present)
Column 1 (id)     — the EClass's native iD attribute, if any
```
(`AbstractEMFExporter.java:92-98`)

The pseudo-ID `_id` is the actual primary key used for FK linking. The native `id` is preserved
but isn't load-bearing for joins. This lets the exporter handle EClasses with or without an
`iD=true` attribute uniformly.

#### 8.1.3 Single-valued reference → FK column on parent

For `Person.address` → `Address`, the `Person` matrix gets a column `address._ref` whose value
is the linked Address's `_id`. The suffix `._ref` (`AbstractEMFExporter.java:100`) marks it as
a foreign-key column.

#### 8.1.4 Multi-valued reference → mapping table (join table)

For `Person.contacts` → multiple `Contact`, the exporter emits a **third CSV** — a "mapping
matrix" named `Person_contacts_Mapping.csv` with exactly two columns: `person_id` and
`contact_id`. Each row joins one Person row to one Contact row. This is the SQL N-to-M
join-table pattern.

Created by `constructEReferencesMappingMatrixIfNotExists()` (`AbstractEMFExporter.java:315`).
Gated by the `addMappingTable` option.

#### 8.1.5 Object-graph walk

`constructMatrixForEObjectWithEReferences()` (`AbstractEMFExporter.java:198`) recursively walks
references (containment + non-containment), creating a matrix for every EClass it visits.
Tracks `processedEObjectsIdentifiers` to avoid cycles.

#### 8.1.6 ZIP output

Once all matrices are built, `exportMatricesToCSVInZipMode()` writes one CSV entry per matrix
into a `ZipOutputStream`:

```
out.zip
  ├── Person.csv                       (_id, id, firstName, lastName, address._ref)
  ├── Address.csv                      (_id, id, street, city)
  ├── Contact.csv                      (_id, id, type, value)
  └── Person_contacts_Mapping.csv      (person_id, contact_id)
```

This is exactly the layout a SQL DB importer expects: one file per table, FK columns, join
tables for N-to-M.

### 8.2 Architectural mismatch with our FormatDelegate

The current `FormatDelegate` API is **Jackson-style streaming to a single OutputStream**. It
works because JSON/YAML/CBOR/BSON each produce a single document and Jackson drives the writes
token-by-token. The gecko model is the opposite: build the whole in-memory graph **first**,
then write multiple files at the end.

These don't compose well. There are three porting paths.

#### Option 1 — Stay in the FormatDelegate path, output a ZIP

The delegate writes a ZIP archive to the single `OutputStream`. Internally it buffers all
matrices (just like gecko) and emits `Person.csv`, `Address.csv`, etc. as ZIP entries on
`close()`. The mismatch is hidden inside the delegate; the codec API surface stays unchanged.

- **Pros:** drops into the existing `CodecResource` / `Resource.Factory` pipeline. URI ends in
  `.zip` (or `.csv` if we accept that semantics).
- **Cons:** still buffers the whole object graph in memory. The single-EObject CSV path and the
  multi-table ZIP path become quite different inside the same delegate — probably warrants two
  delegates and the provider chooses one based on `referenceMode`.

#### Option 2 — Separate `MultiTableExporter` API alongside `CodecFormatProvider`

A new interface that takes a directory `Path` (or a `ZipOutputStream`) plus the root
`EObject`. Not a `FormatDelegate`. Used directly by a new `CsvMultiTableExporter` service.

- **Pros:** cleaner abstraction; doesn't shoehorn N-file output into a single-stream API.
  Matches what gecko already has.
- **Cons:** not pluggable into `Resource.save(OutputStream)` without wrapping. Users have to
  know to use the new API.

#### Option 3 — Port the gecko exporter wholesale as a separate service

`org.eclipse.fennec.codec.csv.exporter` bundle that exposes the `EMFExporter`-equivalent +
`Resource.save()` integration via ZIP. Reuses ~80 % of the gecko code (~2000 LOC), adds OSGi
DS and metadata-service wiring.

- **Pros:** fastest path to feature parity with the existing exporter.
- **Cons:** doesn't reuse the codec serialization pipeline at all — bypasses
  `CodecEObjectSerializer`, so all the codec options (extended-metadata names, force-write,
  value-writers, etc.) won't apply.

### 8.3 Recommendation — Option 1 with two delegate variants

Concretely:

- Add `CodecCsvOptions.OPTION_REFERENCE_MODE` with values `SQL_TABLES` / `FLAT` / `IGNORE`
  (current MVP behavior).
- `CsvFormatProvider.createWriter(...)` returns:
  - `CsvFormatDelegate` (the existing single-EObject delegate) when mode = `IGNORE`.
  - `CsvSqlTablesDelegate` (new) when mode = `SQL_TABLES`.
  - `CsvFlatDelegate` (new) when mode = `FLAT`.
- The `CsvSqlTablesDelegate`:
  - Walks the `EObject` graph using the existing serializer (still gets feature names from
    extended metadata, respects ignore, etc.).
  - Builds in-memory matrices keyed by `EClass`.
  - On `close()`, writes a ZIP containing one CSV per matrix, mirroring the gecko layout
    (§8.1.6).
- The user picks the mode either via the option or by URI extension (`.csv.zip` → SQL_TABLES,
  `.csv` → IGNORE/single-EObject).

This reuses the codec option mechanics (extended-metadata names, ignore, value writers,
dateFormat — all still flow through). It does mean buffering the whole graph in memory, like
gecko already does. For typical export sizes that's fine.

The reference-flattening (`FLAT`) mode can come later as a third delegate variant — same
architecture.

### 8.4 Open questions for the SQL_TABLES delegate

Worth pinning down before any code lands:

1. **Pseudo-ID vs native `iD` attribute** — replicate gecko's dual `_id` / `id` columns, or
   require an `iD`-flagged attribute and use only that? The dual approach is more permissive
   but adds a column users may not want; the strict approach is cleaner but pushes a modelling
   requirement onto the user.
2. **FK column naming** — keep gecko's `address._ref` suffix, or switch to SQL-friendlier
   `address_id` ? `_ref` is unusual in SQL; `_id` is the JPA/Hibernate norm.
3. **Containment cascade semantics** — purely a layout decision in CSV (containment doesn't
   map to a different CSV structure). But the CSV could carry a hint column (e.g.,
   `_containment=true` in the EReference's row of the mapping table) for downstream tools.
4. **Cycle handling** — pseudo-IDs + `processedEObjectsIdentifiers` set already covers cycles
   in gecko. We can reuse the same approach.
5. **ZIP target vs directory target** — ZIP fits the `OutputStream`-based `FormatDelegate`
   API; directory output would need a new path (Option 2 territory). Start with ZIP.
6. **Per-EClass type-row preservation** — the SQL type row (§5.1) must be emitted per CSV in
   the ZIP. The default EMF → SQL mapping applies per-EClass; `columnTypes` option must be
   keyed in a way that disambiguates (e.g., `Person.firstName` vs just `firstName`).

### 8.5 Suggested incremental steps

1. **Spike** — implement `CsvSqlTablesDelegate` for the minimal case: one root EObject with
   one single-valued non-containment reference. Output a 2-file ZIP. Get the
   pseudo-ID/`_ref` mechanics working end-to-end.
2. Add support for containment references (same FK pattern, recursive walk).
3. Add support for multi-valued references via mapping CSV (§8.1.4).
4. Add cycle protection (`processedEObjectsIdentifiers`).
5. Wire `referenceOverrides` so individual `EReference`s can opt into `FLAT` or `IGNORE`.
6. Then start on the `FLAT` delegate (independent of SQL_TABLES — different use case).

### 8.6 Daanse import compatibility

Daanse (`/opt/git/daanse-jdbc-db/importer/csv`) is the concrete downstream consumer we want
our `SQL_TABLES` output to feed. Its CSV importer (`CsvDataImporter.java`) sets the contract
we need to honour. Confirming what daanse expects shapes a few design tweaks.

#### 8.6.1 How daanse imports CSVs

`CsvDataImporter` is an OSGi-watched directory loader (also using fastcsv). For each `.csv` it
sees:

- **Filename → table name** (`Person.csv` → table `Person`). Extension stripped.
- **Subdirectory → DB schema** (`hr/Person.csv` → table `hr.Person`). Matches the JPA
  `test1/` layout you showed earlier.
- **Row 1 → column names**
- **Row 2 → SQL types** parsed by `parseColumnDataType()`
  (`CsvDataImporter.java:348-383`). Handles `JDBCType.valueOf(name)` plus optional
  `(size)` or `(size,precision)`. Recognized types: `VARCHAR`, `BIGINT`, `INTEGER`, `SMALLINT`,
  `DECIMAL`, `NUMERIC`, `REAL`, `DATE`, `TIMESTAMP`, `TIME`, `BOOLEAN`. Unknown ones fall back
  to `VARCHAR`.
- **Row 3+ → data**, batch-inserted via `PreparedStatement`.

For each file daanse issues `DROP TABLE IF EXISTS` → `CREATE TABLE` → `INSERT`.

#### 8.6.2 What daanse does NOT do

- **No FK awareness.** `createTable()` (`CsvDataImporter.java:205-219`) passes `null` as the
  foreign-keys argument to `ddlGenerator().createTable(...)`. Daanse never infers, declares,
  or enforces FKs.
- **No "mapping table" recognition.** A file named `Person_contacts_Mapping.csv` is just
  another table — daanse doesn't know it's a join table.
- **No special handling of `._ref` columns.** A column named `address._ref` would be created
  as-is. Legal but ugly in SQL (would need quoting in most dialects).

#### 8.6.3 How gecko's mapping-table pattern plays with daanse

Functionally fine — all the data lands in the DB. The relational structure is implicit, not
declared:

```
out.zip                          → import into a DB schema:
├── Person.csv                   → CREATE TABLE Person (...)
├── Address.csv                  → CREATE TABLE Address (...)
├── Contact.csv                  → CREATE TABLE Contact (...)
└── Person_contacts_Mapping.csv  → CREATE TABLE Person_contacts_Mapping (
                                       person_id BIGINT,
                                       contact_id BIGINT
                                   )
```

After import the join is performable in SQL — the relationship is in the data, just not as a
constraint. That's typically what you want for CSV ingestion anyway; declared FKs would
prevent loading in any order and would clash with daanse's `DROP/CREATE/INSERT` cycle.

#### 8.6.4 Tweaks our codec should adopt for daanse-friendliness

1. **`address_id` instead of `address._ref`** — gecko's `._ref` suffix is SQL-unfriendly and
   would need quoting in most dialects. Switching to `<refname>_id` matches JPA/Hibernate
   convention. Trivial codec-side change; big DX win.
2. **Subdirectory-as-schema** — the codec should be able to emit `<schema>/<EClass>.csv`
   layout inside the ZIP. Derive the schema from EPackage namespace, an annotation, or an
   option. Daanse already treats this as schema scoping.
3. **Cleaner mapping-table file names** — drop the `_Mapping` suffix; e.g. emit
   `person_contacts.csv` instead of `Person_contacts_Mapping.csv`. Daanse doesn't care, but
   human readers and future tooling do.
4. **Type vocabulary aligned to daanse** — the SQL types in our default EMF → SQL table
   (§5.1) are already a subset of what daanse recognizes. The only gap is `DOUBLE PRECISION`
   (we map `EDouble` to it), which daanse doesn't list explicitly and would fall back to
   `VARCHAR`. Either change our default to `DOUBLE` (cleaner cross-dialect) or accept that
   users targeting daanse should set `columnTypes` for double columns. Recommendation: change
   the default to `DOUBLE`.

#### 8.6.5 Open question — should we emit FK constraint metadata anyway?

Daanse ignores it today, but a future smarter importer might honor it. Options:

- **Sidecar file** (e.g., `_schema.json` or `_constraints.sql`) listing FKs explicitly.
- **Comment lines** in each CSV (daanse already supports a `commentCharacter` option) carrying
  `# FK: address_id REFERENCES Address(id)` lines.
- **Nothing** — keep CSVs pure data and accept that consumers infer relationships from naming.

Recommendation: ship "nothing" first (matches daanse's contract), revisit if we ever target an
importer that wants FK metadata.

---

## 9. Implementation status

This section tracks what's actually built in the `org.eclipse.fennec.codec.csv` bundle today
versus what's still on the roadmap from the design sections above.

### 9.1 Implemented and tested

#### Single-EObject mode (default, `OPTION_REFERENCE_MODE=IGNORE`)

- `CsvFormatProvider`, `CsvFormatDelegate`, `SqlTypeMapper` — produces the 3-row CSV
  (header / SQL types / data) for a single `EObject`'s `EAttribute`s.
- **All existing codec option mechanics flow through** (§7): `useNamesFromExtendedMetaData`,
  `ignore` / `globalIgnoreFeatures`, `forceWrite`, `serializeNull/Empty/Default`, custom value
  writers, `dateFormat` — they all participate via the standard `CodecResource` →
  `CodecEObjectSerializer` pipeline.
- `OPTION_COLUMN_TYPES` (preferred `EStructuralFeature` key; `String` key also accepted)
  overrides SQL type per feature.
- `OPTION_DELIMITER`, `OPTION_QUOTE_MODE`, `OPTION_LINE_ENDING`, `OPTION_CHARSET` — dialect
  control.
- References, multi-valued attributes, contained `EObject`s — silently skipped with a `FINE`
  log line (matches the "MVP, attributes only" scope of §4).
- OSGi `Resource.Factory` registered for `.csv` extension via `CsvResourceFactoryComponent`.

#### SQL_TABLES mode (MVP spike, `OPTION_REFERENCE_MODE=SQL_TABLES`)

- `CsvSqlTablesDelegate` — BFS walk from the root `EObject`, per-`EClass` matrices, ZIP output
  with one CSV entry per visited `EClass`.
- Pseudo `_id` (BIGINT) primary key per EClass; foreign-key columns named `<refname>_id`
  (BIGINT) — daanse-friendly naming (§8.6.4 item 1).
- **Single-valued** `EReference`s (containment + non-containment) emit a FK column.
- Multi-valued `EAttribute`s joined with `;` in a single cell (same as IGNORE mode).
- OSGi `Resource.Factory` also registers `.csvz` extension that defaults to SQL_TABLES mode
  (file path `out.csvz` works through `ResourceSetImpl`; `out.csv.zip` does not, because EMF's
  `URI.fileExtension()` only returns the last segment).
- `IdentityHashMap`-based visit set in the graph walk — cycle-safe by construction.

#### Plumbing changes outside the bundle

- `CodecFormatProvider` (in `org.eclipse.fennec.codec.api`) extended with a default
  `createWriter(target, rootObject, saveOptions)` overload — backwards compatible (other
  providers inherit the default that delegates to the existing `createWriter(target)`).
- `CodecResource.doSaveWithFormat()` calls the new overload, passing the root `EObject` and
  effective save options.

#### Tests

| Test class | Verifies |
|---|---|
| `CsvFormatProviderTest` | Provider metadata + read-not-supported |
| `CsvWriterTest` | Single-EObject 3-row layout, `columnTypes` override, stateless auto-discovery |
| `CsvExtendedMetaDataTest` | EMD names appear in header when option is set |
| `CsvCustomKeyTest` | Per-feature `key` annotation renames the column |
| `CsvForceWriteTest` | `forceWrite` brings transient/volatile/derived feature into header |
| `CsvGlobalIgnoreTest` | `globalIgnoreFeatures` drops columns (and values) |
| `CsvVisibilityTest` | Per-feature `ignoreWrite`/`ignore` drop columns; `ignoreRead` does not |
| `CsvValueHandlingTest` | `serializeNull` / `serializeEmpty` / `serializeDefault` |
| `CsvSqlTablesTest` | SQL_TABLES emits ZIP with two CSVs + FK linking; empty-ref case |

### 9.2 Not yet implemented

| Item | Reference | Notes |
|---|---|---|
| Codec option pipeline in SQL_TABLES mode | §8 | `CsvSqlTablesDelegate` currently uses `feat.getName()` directly. `useNamesFromExtendedMetaData`, per-feature `ignore`, `forceWrite`, custom value writers, `dateFormat` are **not** applied. Needs the delegate to read from `ConfigurationResolver`. |
| Multi-valued references → mapping CSVs (join tables) | §8.1.4 / §8.5 step 3 | Currently skipped with a `FINE` log line. Gecko's `Person_contacts_Mapping.csv` pattern. |
| Cleaner mapping-table file names | §8.6.4 item 3 | Becomes relevant when mapping tables land. |
| Subdirectory-as-schema layout in the ZIP | §8.6.4 item 2 | Daanse treats subdirectories as DB schemas. |
| `DOUBLE` instead of `DOUBLE PRECISION` for daanse compat | §8.6.4 item 4 | Single-character change in `SqlTypeMapper` defaults. |
| `referenceOverrides` (per-`EReference` mode override) | §8.5 step 5 | Not started. |
| `FLAT` reference mode | §5.3, §8.5 step 6 | Different audience (single-sheet / spreadsheets); a third delegate variant. |
| CSV reader / deserialization | — | `createReader()` throws `UnsupportedOperationException`. Not on the roadmap. |
| FK constraint metadata sidecar | §8.6.5 | Decided "nothing" — matches daanse's contract. |

### 9.3 Bundle layout

```
org.eclipse.fennec.codec.csv/
├── bnd.bnd                                       # depends on de.siegmar.fastcsv 4.2.0
├── src/org/eclipse/fennec/codec/csv/
│   ├── CodecCsvOptions.java                      # option keys + ReferenceMode enum
│   ├── CsvFormatProvider.java                    # CodecFormatProvider<InputStream, OutputStream>
│   ├── CsvFormatDelegate.java                    # IGNORE-mode writer delegate
│   ├── CsvSqlTablesDelegate.java                 # SQL_TABLES-mode writer delegate
│   ├── CsvResourceFactoryComponent.java          # OSGi DS: csv + csvz extensions
│   ├── SqlTypeMapper.java                        # default EMF → SQL type table
│   └── package-info.java
└── test/org/eclipse/fennec/codec/csv/
    ├── CsvFormatProviderTest.java
    ├── CsvWriterTest.java
    ├── CsvExtendedMetaDataTest.java
    ├── CsvCustomKeyTest.java
    ├── CsvForceWriteTest.java
    ├── CsvGlobalIgnoreTest.java
    ├── CsvVisibilityTest.java
    ├── CsvValueHandlingTest.java
    └── CsvSqlTablesTest.java
```
