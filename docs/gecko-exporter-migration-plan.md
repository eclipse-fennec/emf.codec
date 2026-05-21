# Gecko exporter migration to fennec-codec — plan

**Date:** 2026-05-21

## Status

| Phase | State | Notes |
|---|---|---|
| Phase 1 — extract shared infrastructure (§8) | ✅ **Done** (2026-05-21) | `codec.tabular.model` and `codec.tabular` bundles in place; CSV refactored onto the shared layer; all CSV tests green; new `TabularDocumentBuilderCellTypingTest` covers cell-typing correctness. |
| Phase 2 — ODS (§9) | ⬜ Not started | |
| Phase 3 — XLSX (§10) | ⬜ Not started | |
| Phase 4 — R Language (§11) | ⬜ Not started (optional) | |

**Phase 1 deliverables actually shipped:**

- `org.eclipse.fennec.codec.tabular.model` — `tabular.ecore` + `tabular.genmodel` with `TabularDocument`, `Table`, `Column`, `Row`, `JoinTable`, `JoinTableRow`, abstract `Cell` + nine concrete subclasses, and the `ReferenceMode` / `MultiValuedRefStrategy` / `ColumnSource` EEnums. Generated Java in `src/`.
- `org.eclipse.fennec.codec.tabular` — `CodecTabularOptions` (shared option keys), `SqlTypeMapper` (relocated from CSV), `TabularDocumentBuilder` (all three reference modes; `FeatureConfig` integration; typed cells), `TabularDocumentRenderer<T>` SPI, `TabularDocumentDelegate<T>` generic FormatDelegate.
- `org.eclipse.fennec.codec.csv` refactored — `CsvRenderer` implements the SPI; `CsvFormatProvider` routes IGNORE through the existing Jackson `CsvFormatDelegate` and FLAT/SQL_TABLES through `TabularDocumentDelegate + CsvRenderer`. `CsvSqlTablesDelegate`, `CsvFlatDelegate`, and the in-CSV `SqlTypeMapper` deleted. `CodecCsvOptions` trimmed to CSV-only dialect knobs.
- Tests — six existing CSV test files repointed from `CodecCsvOptions.*` shared keys to `CodecTabularOptions.*` and the model-bundle enums. New `TabularDocumentBuilderCellTypingTest` (16 tests) in the tabular bundle.
- Docs — `csv-codec-investigation.md` and `csv-codec-vs-gecko-comparison.md` updated to the new namespace. This plan retains the old→new key mapping table in §7 as the migration record.

**Carried open from phase 1 (not blockers for phase 2):**

- Custom value writers in tabular renderers — IGNORE still uses the Jackson path in the CSV provider; FLAT/SQL_TABLES bypass the pipeline and don't yet support `CodecValueWriter`. Re-open if a phase-2 consumer needs them.
- The "second tabular test class" candidates (`TabularDocumentDelegateTest`, `SqlTypeMapperTest`) were considered and deferred — CSV tests cover their concerns transitively today.

## 1. Goal

Migrate the four gecko EMF exporters into fennec-codec:

- `org.gecko.emf.exporter.csv` → already done as `org.eclipse.fennec.codec.csv` (see
  `csv-codec-investigation.md` and `csv-codec-vs-gecko-comparison.md`).
- `org.gecko.emf.exporter.ods` → ODS spreadsheet (sods library).
- `org.gecko.emf.exporter.xlsx` → XLSX (Apache POI).
- `org.gecko.emf.exporter.r_lang` → RData binary format.

All four gecko exporters extend the same `AbstractEMFExporter` base class and share the
`exportEObjectsToMatrices(...)` matrix-building infrastructure. The format-specific code is
the rendering layer. That seam is what we want to reproduce in fennec-codec.

## 2. Architectural decisions

Locked in at planning time:

| Decision | Choice |
|---|---|
| Cell typing in the shared model | **Typed cells.** Cells carry typed values (`long`, `double`, `Date`, `String`, `boolean`, FK ref, ...). CSV renderer stringifies at emit time; ODS/XLSX use the native types so spreadsheets sum/format correctly. |
| Data-model representation | **EMF / ecore.** `model/tabular.ecore` + generated Java, same pattern as `codec.metadata/model/codec.ecore` and `persistence-jpa/model/eorm.ecore`. Cell typing is a polymorphic `Cell` hierarchy (one concrete EClass per cell variant). |
| Options namespace | **Hard rename** to `codec.tabular.*` for shared knobs. No `codec.csv.*` aliases — the existing CSV options are days old, blast radius is tiny, cleaner long-term. CSV-only knobs (delimiter / quote / line / charset) stay under `codec.csv.*`. |
| Phase 1 scope | **All three reference modes** (IGNORE / FLAT / SQL_TABLES). Uniform abstraction. ODS/XLSX inherit IGNORE-mode (single-sheet attributes-only) and FLAT-mode (single-sheet flattened) for free. |
| Module placement | **New `codec.tabular` module** parallel to `codec.api`. Clean dependency direction: `codec.csv` / `codec.ods` / `codec.xlsx` all depend on `codec.tabular`. |

## 3. Module layout

```
org.eclipse.fennec.codec.api                  (existing — CodecFormatProvider etc.)
org.eclipse.fennec.codec                      (existing — CodecResource, mapper)
org.eclipse.fennec.codec.tabular              (NEW)
    ├── model/
    │     tabular.ecore     — TabularDocument / Table / Column / Cell hierarchy / JoinTable
    │     tabular.genmodel  — Java code generation descriptor
    ├── src-gen/            — generated Java (Tabular*Impl, TabularFactory, TabularPackage)
    ├── Builder
    │     TabularDocumentBuilder — graph walk, FeatureConfig, strategy, schemas
    ├── Options
    │     CodecTabularOptions — shared knobs + enums
    └── Renderer SPI
          TabularDocumentRenderer — what each format must implement

org.eclipse.fennec.codec.csv                  (existing — REFACTORED in phase 1)
    ├── CodecCsvOptions — CSV-only knobs (delimiter/quote/line/charset)
    ├── CsvFormatProvider — registered for .csv / .csvz
    ├── CsvFormatDelegate — IGNORE-mode renderer (kept; or replaced by tabular renderer)
    └── CsvRenderer (NEW) — implements TabularDocumentRenderer for CSV/ZIP output

org.eclipse.fennec.codec.ods                  (NEW — phase 2)
    ├── CodecOdsOptions — ODS-only knobs (styling? column widths?)
    ├── OdsFormatProvider — registered for .ods
    └── OdsRenderer — implements TabularDocumentRenderer using sods

org.eclipse.fennec.codec.xlsx                 (NEW — phase 3)
    ├── CodecXlsxOptions — XLSX-only knobs (cell formats, freeze panes?)
    ├── XlsxFormatProvider — registered for .xlsx
    └── XlsxRenderer — implements TabularDocumentRenderer using POI

org.eclipse.fennec.codec.rlang                (NEW — phase 4, optional)
    ├── CodecRLangOptions
    ├── RLangFormatProvider — registered for .rdata / .rds
    └── RLangRenderer — implements TabularDocumentRenderer with custom RData serialization
```

## 4. The TabularDocument data model

The intermediate representation every renderer consumes. Defined as an EMF / ecore model
(`model/tabular.ecore`), generated to Java via `tabular.genmodel`. Built once per save
operation by `TabularDocumentBuilder`. The shape below is the ecore content; the generated Java
classes (`TabularDocumentImpl`, `TableImpl`, …) and factory (`TabularFactory.eINSTANCE.create…()`)
are what builder and renderers actually touch.

### EPackage `tabular`

**`TabularDocument`** (root EClass; container for the whole export)
- `referenceMode : ReferenceMode` — enum: `IGNORE` / `FLAT` / `SQL_TABLES`.
- `tables : Table[*]` (containment) — one per visited `EClass`; insertion-ordered.
- `joinTables : JoinTable[*]` (containment) — empty for IGNORE / FLAT.

**`Table`**
- `eClass : EClass` (non-containment ref into Ecore) — null for the single-table FLAT case.
- `name : EString` — basis for sheet name / file name.
- `schema : EString` — from `codec.tabular.schemas`, may be null.
- `columns : Column[*]` (containment) — column order.
- `rows : Row[*]` (containment).

**`Column`**
- `header : EString` — resolved key (possibly dotted for FLAT).
- `sqlType : EString` — `VARCHAR`, `BIGINT`, `DOUBLE`, … — for CSV's type row and ODS/XLSX
  cell-format hints.
- `source : ColumnSource` — enum: `PK` / `ATTRIBUTE` / `FK_PARENT` / `FK_CHILD` /
  `FLAT_NESTED`.
- `feature : EStructuralFeature` (non-containment ref into Ecore) — the EAttribute or
  EReference that gave rise to this column, when applicable; null for `PK` and the
  parent-side of `FK_CHILD`.

**`Row`**
- `cells : Cell[*]` (containment) — parallel-indexed to `Table.columns`.

**`Cell`** *(abstract supertype)*
- Nine concrete subclasses, each carrying a typed `value` attribute:

| Subclass | Attribute | Notes |
|---|---|---|
| `StringCell` | `value : EString` | |
| `LongCell` | `value : ELong` | covers EInt/ELong/EShort/EByte |
| `DoubleCell` | `value : EDouble` | covers EFloat/EDouble |
| `BigDecimalCell` | `value : EBigDecimal` | |
| `BooleanCell` | `value : EBoolean` | |
| `DateCell` | `value : EDate` + `dateFormat : EString` | format hint forwarded to renderers |
| `BinaryCell` | `value : EByteArray` | renderers pick the encoding (Base64 etc.) |
| `FkCell` | `targetId : ELong` + `targetEClass : EClass` | BIGINT in CSV, hyperlink in XLSX/ODS |
| `EmptyCell` | — | distinguishes "unset" from "explicit empty" |

Renderers dispatch via `instanceof` on the concrete subclass and use the typed accessor:

```java
switch (cell) {
    case LongCell c       -> sheet.write(c.getValue());          // POI numeric cell
    case DateCell c       -> sheet.write(c.getValue(), c.getDateFormat());
    case FkCell c         -> sheet.writeHyperlink(c.getTargetEClass(), c.getTargetId());
    case EmptyCell c      -> sheet.writeBlank();
    // ...
}
```

**`JoinTable`**
- `ownerEClass : EClass` (non-containment).
- `ref : EReference` (non-containment).
- `fileName : EString` — e.g. `warehouse_items`.
- `schema : EString` — may be null.
- `ownerCol, targetCol : EString` — column header names.
- `rows : JoinTableRow[*]` (containment).

**`JoinTableRow`**
- `ownerId : ELong`
- `targetId : ELong`
- `targetEClass : EClass` (non-containment) — captures the actual instance type, useful when
  the EReference's eType has subclasses.

### Notes

- The model depends on the Ecore EPackage (`http://www.eclipse.org/emf/2002/Ecore`) for the
  `EClass`, `EStructuralFeature`, `EReference` cross-refs. Standard pattern; the
  `tabular.genmodel` references the Ecore genmodel.
- Insertion order on multi-valued features matters: `Table.columns`, `Row.cells`,
  `TabularDocument.tables`, `TabularDocument.joinTables`. EMF preserves it on `EList`, so the model
  shape doesn't need explicit ordering fields.
- The `TabularDocument` itself isn't directly serialized today — it's an in-memory
  intermediate. But modelling it in EMF leaves the door open to dump-for-debug,
  validation via OCL, etc.

## 5. TabularDocumentBuilder

```java
public final class TabularDocumentBuilder {

    /** Returns a freshly populated {@link TabularDocument} (an EMF EObject from the tabular model). */
    public static TabularDocument build(
        List<? extends EObject> roots,
        Map<String, Object> options,
        ConfigurationResolver resolver) {
        TabularDocument doc = TabularFactory.eINSTANCE.createTabularDocument();
        // populate doc.getTables(), doc.getJoinTables(), set doc.setReferenceMode(...)
        return doc;
    }
}
```

Inputs:
- The list of root `EObject`s (from `Resource.getContents()`).
- The save-time options map.
- The operation `ConfigurationResolver`, already enriched with save options.

`TabularDocument`, `Table`, `Column`, `Cell` and friends are the generated EMF EObjects from
`tabular.ecore`. The builder uses `TabularFactory.eINSTANCE.createXxx()` and the typed setters.

Behaviour, driven by the resolved `ReferenceMode`:
- **IGNORE.** Single `Table` for the first EClass; one row per root; attributes only.
- **FLAT.** Single `Table` with dotted column names; one row per root; references flattened
  via path-stack cycle protection.
- **SQL_TABLES.** Multiple tables (one per visited EClass) + join tables. Walk-time strategy
  (PREFER_FK_COLUMN vs ALWAYS_JOIN_TABLE) controls how multi-valued refs are emitted.

All `FeatureConfig` resolution happens here: `useNamesFromExtendedMetaData`,
`@codec(key=...)`, `globalIgnoreFeatures`, `forceWrite`, `dateFormat`. Renderers receive
already-resolved column names and pre-formatted (or typed) cells.

## 6. Renderer SPI

```java
public interface TabularDocumentRenderer<T> {
    void render(TabularDocument doc, T target, Map<String, Object> options) throws IOException;
}
```

Format-specific responsibilities (the only thing each format module owns):

- File / sheet naming (e.g. `Person.csv` vs sheet `Person`).
- Schema-as-subdirectory vs schema-as-prefix vs ignored.
- Cell rendering (stringify, format, style).
- Container packaging (ZIP, single OutputStream, Workbook, SpreadSheet).

### Integration with CodecFormatProvider

Each format module's `CodecFormatProvider` does roughly:

```java
@Override
public FormatDelegate<OutputStream> createWriter(
        OutputStream target,
        List<? extends EObject> rootObjects,
        Map<String, Object> saveOptions,
        ConfigurationResolver resolver) {
    return new TabularDocumentDelegate<>(
        target,
        rootObjects,
        saveOptions,
        resolver,
        new OdsRenderer());   // or CsvRenderer / XlsxRenderer
}
```

`TabularDocumentDelegate` lives in `codec.tabular`. It:
1. Buffers the `FormatDelegate` write calls (they're no-ops for tabular renderers, same as
   today's `CsvSqlTablesDelegate`).
2. On `close()`, builds the `TabularDocument` via `TabularDocumentBuilder` and hands it to the
   renderer.

The Jackson-pipeline path (today's `CsvFormatDelegate` for IGNORE mode in CSV) is the only
exception: IGNORE-mode CSV currently uses the Jackson writer pipeline to support custom value
writers, and we want to keep that path intact. Two ways to reconcile:

- (a) Keep `CsvFormatDelegate` (Jackson-driven) for IGNORE mode in CSV; route SQL_TABLES /
  FLAT through `TabularDocumentDelegate`. ODS/XLSX always go through `TabularDocumentDelegate`,
  meaning they don't (yet) support custom value writers.
- (b) Drop the Jackson path for CSV IGNORE too; lose custom-value-writer support across the
  board until we add a one-shot capture generator to the tabular builder.

(a) is the pragmatic choice. (b) is the cleaner end state.

## 7. Options remapping

Existing `codec.csv.*` keys (recently shipped) and where they go:

| Old key | New key | Notes |
|---|---|---|
| `codec.csv.referenceMode` | `codec.tabular.referenceMode` | Used by all tabular formats |
| `codec.csv.columnTypes` | `codec.tabular.columnTypes` | Maps to SQL types in CSV, cell formats in ODS/XLSX |
| `codec.csv.fkColumnSuffix` | `codec.tabular.fkColumnSuffix` | FK suffix is universal |
| `codec.csv.schemas` | `codec.tabular.schemas` | Realised as subdirectory in CSV; sheet-name prefix in ODS/XLSX |
| `codec.csv.multiValuedRefStrategy` | `codec.tabular.multiValuedRefStrategy` | Strategy enum (and its values) move with it |
| `codec.csv.delimiter` | `codec.csv.delimiter` (stays) | CSV-only |
| `codec.csv.quoteMode` | `codec.csv.quoteMode` (stays) | CSV-only |
| `codec.csv.lineEnding` | `codec.csv.lineEnding` (stays) | CSV-only |
| `codec.csv.charset` | `codec.csv.charset` (stays) | CSV-only (ODS is UTF-8 by spec; XLSX is binary) |

The `CodecCsvOptions.ReferenceMode` enum and `MultiValuedRefStrategy` enum move to
`CodecTabularOptions`. The names of the enum *values* stay the same.

Hard rename, no aliases. Anyone using the recent `codec.csv.*` shared knobs will see a
compile / runtime mismatch and migrate.

## 8. Phase 1 — extract shared infrastructure

Single goal: refactor existing CSV without behaviour change. No new format yet.

1. Create the `org.eclipse.fennec.codec.tabular` module (bnd.bnd, build.gradle, base
   package). Use `codec.metadata` as the template for the ecore + genmodel + gradle wiring.
2. Author `model/tabular.ecore` and `model/tabular.genmodel` per §4. Generate Java
   (`src-gen/`). Verify the generated `TabularPackage` / `TabularFactory` / `*Impl` classes
   compile in isolation.
3. Move enums and shared option keys to `CodecTabularOptions`. Delete the old keys from
   `CodecCsvOptions`. The `ReferenceMode` and `MultiValuedRefStrategy` enums become EMF
   `EEnum`s inside `tabular.ecore` (so they're also reachable via the model) — keep matching
   Java enums in `CodecTabularOptions` for ergonomic option-typing.
4. Define `TabularDocumentRenderer<T>` SPI as a plain Java interface (behavior, not data).
5. Implement `TabularDocumentBuilder.build(...)`. Port the existing walk + FeatureConfig logic
   from `CsvSqlTablesDelegate` and `CsvFlatDelegate`. The builder populates the EMF
   `TabularDocument` via `TabularFactory.eINSTANCE`. IGNORE mode = single-`Table` document driven
   by the first root's EClass.
6. Implement `TabularDocumentDelegate<T>` in `codec.tabular` — generic FormatDelegate that
   triggers the build on `close()` and hands off to the renderer.
7. Implement `CsvRenderer` in `codec.csv` — replaces `CsvSqlTablesDelegate.emit*` and
   `CsvFlatDelegate.emit*`. Dispatches on the concrete `Cell` subclass; stringifies typed
   cells (using `DateCell.getDateFormat()` for date formatting).
8. Update `CsvFormatProvider.createWriter(...)`:
   - IGNORE mode: keep `CsvFormatDelegate` (Jackson path) for backward compatibility with
     custom value writers.
   - FLAT / SQL_TABLES: route through `TabularDocumentDelegate` with `CsvRenderer`.
9. Update all existing CSV tests for the new option keys (`codec.tabular.*`). Functionally
   the same outputs.
10. Update `csv-codec-investigation.md` §9 and `csv-codec-vs-gecko-comparison.md` where
    they reference the old keys.

Acceptance: all existing CSV tests pass with no behavior change; renamed option keys are the
only public API delta.

## 9. Phase 2 — ODS

1. Create `org.eclipse.fennec.codec.ods` module.
2. Add `com.github.miachm.sods` (sods) to the bundle dependencies — same library gecko uses.
3. Implement `OdsRenderer`:
   - One `Sheet` per `Table`. Sheet name = schema-prefix + EClass name (e.g. `hr.Person`).
   - One `Sheet` per `JoinTable` named `<owner>_<refname>`.
   - Cell types map directly: `LongCell` → numeric, `DateCell` → date with format, etc.
   - Header row styling (bold, background colour) — minimum viable; defer fancy styling.
   - No type row (ODS columns have native type metadata).
4. `OdsFormatProvider`, `OdsCodecResourceFactoryComponent` registered for `.ods`.
5. Tests: port `CsvSqlTablesTest` / `CsvSqlTablesMultiRefTest` / `CsvFlatTest` shape — load
   the produced ODS with sods and assert on its `SpreadSheet` model.
6. `CodecOdsOptions` if any genuinely ODS-only knobs are needed (mostly: no).

## 10. Phase 3 — XLSX

1. Create `org.eclipse.fennec.codec.xlsx` module.
2. Add `org.apache.servicemix.bundles.poi` (or modern POI bundle) dependency.
3. Implement `XlsxRenderer`:
   - One `Sheet` per `Table`. Sheet names limited to 31 characters (POI restriction) — may
     need truncation logic.
   - Cell styling: header bold, date cells use `DataFormat`, FK cells could be hyperlinks
     to the target sheet (POI supports `HyperlinkType.SHEET_MODULE_REFERENCE`).
   - Freeze the header row.
4. `XlsxFormatProvider` registered for `.xlsx`.
5. Tests mirror ODS pattern, loading the produced Workbook back via POI.

## 11. Phase 4 — R Language

Optional and deferred. RData binary serialization is its own subsystem (gecko implements it
inline with `SYMSXP` / `LISTSXP` / `CHARSXP` constants). When tackled:

1. Create `org.eclipse.fennec.codec.rlang` module.
2. Port the RData serialization code from gecko's `EMFRLangExporter`.
3. Two output modes per gecko: all-dataframes-in-one-file vs one-dataframe-per-ZIP-file.
4. Renderer: walk the document, convert each `Table` to columnar arrays, emit RData.

## 12. Future / open questions

- **Custom value writers in tabular renderers.** The Jackson-pipeline path is what makes them
  work today in CSV IGNORE mode. To support them across all tabular renderers we'd need to
  let the document builder run a capture generator per cell. Out of scope for phase 1; revisit
  once a real user shows up wanting them in SQL_TABLES.
- **Reader support.** None of the gecko exporters read. The fennec-codec has reader contracts
  on the JSON / CBOR / BSON / YAML side, but the tabular formats are write-only today. CSV
  read would need a separate design pass; ODS / XLSX read would need it too. Out of scope.
- **ODS / XLSX schemas.** "Schema" in CSV means subdirectory. In a single workbook it might
  become a sheet-name prefix (`hr.Person`) or be ignored entirely. Default behaviour TBD when
  the ODS renderer lands.
- **Cell styling option family.** ODS and XLSX users may want header styling, frozen panes,
  column widths. These don't make sense for CSV, so they'd live under format-specific option
  namespaces (`codec.ods.*`, `codec.xlsx.*`). Defer concrete shape until a need surfaces.
- **`@gecko-extender` parity.** Gecko's exporter has options like `OPTION_SHOW_URIS` (emit
  EMF URIs instead of pseudo-ids for FKs). If any gecko consumer actually uses these,
  decide whether to port. Probably not by default — daanse / JPA / eorm consumers don't
  want URI strings in FK columns.
