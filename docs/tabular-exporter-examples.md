# Tabular exporter examples (CSV / ODS / XLSX / R-Lang)

End-to-end recipes for saving EMF resources via the four tabular format
providers shipped by fennec-codec. The user-facing API is the standard EMF
`Resource.save(OutputStream, options)` — pick the right file extension, pass
your options, and you're done.

## Table of contents

- [How dispatch works](#how-dispatch-works)
- [Common setup](#common-setup)
- [CSV](#csv)
- [ODS](#ods)
- [XLSX](#xlsx)
- [R Language (.RData)](#r-language-rdata)
- [Shared tabular options](#shared-tabular-options)
- [Save-option validation (URI/option consistency)](#save-option-validation)
- [Picking the right format](#picking-the-right-format)

---

## How dispatch works

Each tabular bundle registers a `Resource.Factory` (an OSGi DS component) for
its file extensions:

| Bundle | DS component | Extensions |
|---|---|---|
| `org.eclipse.fennec.codec.csv` | `CsvResourceFactoryComponent` | `.csv`, `.csvz` |
| `org.eclipse.fennec.codec.ods` | `OdsResourceFactoryComponent` | `.ods` |
| `org.eclipse.fennec.codec.xlsx` | `XlsxResourceFactoryComponent` | `.xlsx` |
| `org.eclipse.fennec.codec.rlang` | `RLangResourceFactoryComponent` | `.RData`, `.rdataz` |

In an OSGi runtime the components are picked up automatically — the user just
creates a `Resource` for an extension-bearing URI and the right factory does the
rest. The factory wires up the codec-internal `*FormatProvider*` for you;
callers never touch it.

In a plain-Java context (a JUnit test, a CLI tool with no OSGi runtime), you
register the factories explicitly in your `ResourceSet` once at startup.

---

## Common setup

The two snippets below show the same call in OSGi and in plain Java. Pick the
one that matches your runtime; everything after this section assumes a `Resource`
has been created via this pattern.

### Inside OSGi (Karaf, Felix, OSGi-R7 servers, …)

Inject (or look up) the `ResourceSet` your application uses. The DS components
ship with the bundles, so as long as the codec bundles are deployed, the
extensions resolve.

```java
@Reference
private ResourceSet resourceSet;

void exportProducts(List<EObject> products, OutputStream out) throws IOException {
    Resource resource = resourceSet.createResource(URI.createFileURI("products.csvz"));
    resource.getContents().addAll(products);
    resource.save(out, Map.of());
}
```

That's it. The `.csvz` extension picks `CsvResourceFactoryComponent`, which
defaults the reference mode to `SQL_TABLES` and produces a ZIP of CSVs.

### Plain Java (tests, scripts, non-OSGi apps)

Register the `Resource.Factory` instances once on your `ResourceSet`, then use
the same call as above.

```java
ResourceSet rs = new ResourceSetImpl();
Map<String, Object> extMap = rs.getResourceFactoryRegistry().getExtensionToFactoryMap();
extMap.put("csv",    new CsvResourceFactoryComponent(metadataService));
extMap.put("csvz",   new CsvResourceFactoryComponent(metadataService));
extMap.put("ods",    new OdsResourceFactoryComponent(metadataService));
extMap.put("xlsx",   new XlsxResourceFactoryComponent(metadataService));
extMap.put("RData",  new RLangResourceFactoryComponent(metadataService));
extMap.put("rdataz", new RLangResourceFactoryComponent(metadataService));

Resource resource = rs.createResource(URI.createFileURI("products.csvz"));
resource.getContents().addAll(products);
resource.save(out, Map.of());
```

`metadataService` is a `MetadataService` obtained however your app does it
(`MetadataServiceFactory.create()` in tests; an OSGi reference otherwise).

---

## CSV

Three reference modes via `CodecTabularOptions.OPTION_REFERENCE_MODE`.

### IGNORE mode (default for `.csv`)

One CSV file. Attributes only — references are skipped. Useful for "give me a
flat spreadsheet view of these objects."

```java
Resource resource = resourceSet.createResource(URI.createFileURI("products.csv"));
resource.getContents().add(product);
resource.save(out, Map.of());            // IGNORE is the default for .csv
```

Output (3 rows: header, SQL types, data):

```csv
productId,name
VARCHAR,VARCHAR
p-001,Widget
```

### FLAT mode

One CSV with dotted column names for nested references.

```java
Resource resource = resourceSet.createResource(URI.createFileURI("warehouse.csv"));
resource.getContents().add(warehouse);
resource.save(out, Map.of(
    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.FLAT));
```

Output:

```csv
name,featured.productId,featured.name
VARCHAR,VARCHAR,VARCHAR
Acme,p-001,Widget
```

### SQL_TABLES mode (auto-selected by `.csvz`)

ZIP archive with one CSV per visited `EClass` plus FK columns. Choosing the
`.csvz` extension is the same as setting `referenceMode=SQL_TABLES` on a `.csv`
URI — but cleaner, because the file name advertises the format.

```java
Resource resource = resourceSet.createResource(URI.createFileURI("warehouse.csvz"));
resource.getContents().add(warehouse);
resource.save(out, Map.of());            // .csvz → SQL_TABLES default
```

Produces `Warehouse.csv` and `Product.csv` inside the ZIP, with a `featured_id`
foreign-key column on `Warehouse`.

### CSV dialect options

CSV-only knobs (live in `CodecCsvOptions`):

```java
resource.save(out, Map.of(
    CodecCsvOptions.OPTION_DELIMITER,    ';',
    CodecCsvOptions.OPTION_QUOTE_MODE,   "ALWAYS",
    CodecCsvOptions.OPTION_LINE_ENDING,  "CRLF",
    CodecCsvOptions.OPTION_CHARSET,      "ISO-8859-1"));
```

---

## ODS

OpenDocument Spreadsheet. One `Sheet` per `Table`/`JoinTable`. Cells carry
native types (numbers, dates, booleans), not strings. In `SQL_TABLES` mode,
`featured_id` cells on the `Warehouse` sheet are clickable hyperlinks to the
`Product` sheet (`#Product.A1`) by default.

```java
Resource resource = resourceSet.createResource(URI.createFileURI("warehouse.ods"));
resource.getContents().add(warehouse);
resource.save(out, Map.of(
    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES));
```

### ODS-only options

```java
resource.save(out, Map.of(
    CodecOdsOptions.OPTION_STYLE_HEADER,         Boolean.TRUE,   // default
    CodecOdsOptions.OPTION_ADJUST_COLUMN_WIDTH,  Boolean.TRUE,   // default
    CodecOdsOptions.OPTION_GENERATE_LINKS,       Boolean.TRUE)); // default
```

FK hyperlinks need the [forked sods](https://github.com/miachm/SODS/pull/63)
that carries `LinkedValue` / `Range.addLinkedValue` (the `com.github.miachm.sods`
bundle in `cnf/local`). Note the sods writer represents a linked cell as a
*string* carrying the FK id as the link text — so with links on, the FK column is
a clickable string rather than a numeric value. Disable with
`OPTION_GENERATE_LINKS = Boolean.FALSE` to keep numeric FKs.

Schema option produces a sheet-name prefix (`hr.Warehouse`); link targets follow
the prefixed name (`#hr.Product.A1`):

```java
resource.save(out, Map.of(
    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES,
    CodecTabularOptions.OPTION_SCHEMAS,        Map.of(testPackage, "hr")));
```

---

## XLSX

Microsoft Excel via Apache POI. Same shape as ODS, with clickable FK hyperlinks
(on by default), frozen header row, auto-sized columns. Unlike ODS, POI keeps the
FK cell *numeric* and attaches the hyperlink alongside.

```java
Resource resource = resourceSet.createResource(URI.createFileURI("warehouse.xlsx"));
resource.getContents().add(warehouse);
resource.save(out, Map.of(
    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES));
```

In the produced workbook, every `featured_id` cell on the `Warehouse` sheet is a
hyperlink to `'Product'!A1`.

### XLSX-only options

```java
resource.save(out, Map.of(
    CodecXlsxOptions.OPTION_STYLE_HEADER,        Boolean.TRUE,    // default
    CodecXlsxOptions.OPTION_ADJUST_COLUMN_WIDTH, Boolean.TRUE,    // default
    CodecXlsxOptions.OPTION_FREEZE_HEADER_ROW,   Boolean.TRUE,    // default
    CodecXlsxOptions.OPTION_GENERATE_LINKS,      Boolean.TRUE,    // default
    CodecXlsxOptions.OPTION_DEFAULT_DATE_FORMAT, "yyyy-MM-dd"));  // default "m/d/yy h:mm"
```

Disable FK hyperlinks:

```java
resource.save(out, Map.of(
    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES,
    CodecXlsxOptions.OPTION_GENERATE_LINKS,    Boolean.FALSE));
```

Sheet names get auto-truncated to POI's 31-character limit with `~1`/`~2`
collision suffixes — no caller-side action needed for long `EClass` names.

---

## R Language (`.RData`)

Binary R serialization. Each `Table` becomes one R *data frame* with typed
columns (`INTSXP` / `REALSXP` / `LGLSXP` / `STRSXP`, plus native `POSIXct` for
dates).

### Single-file mode (default for `.RData`)

One binary file containing N data frames bound under their EClass names. The R
consumer loads it via `load("warehouse.RData")` and gets each data frame as a
named variable.

```java
Resource resource = resourceSet.createResource(URI.createFileURI("warehouse.RData"));
resource.getContents().add(warehouse);
resource.save(out, Map.of(
    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES));
```

### ZIP mode (`.rdataz`)

A ZIP archive with one single-data-frame `.RData` per entry. Auto-selected by
the `.rdataz` extension.

```java
Resource resource = resourceSet.createResource(URI.createFileURI("warehouse.rdataz"));
resource.getContents().add(warehouse);
resource.save(out, Map.of());            // .rdataz → dataframePerFile=true
```

ZIP entries: `Warehouse.RData`, `Product.RData`. Each is a self-contained
single-data-frame RData blob.

### R-Lang-only option

Explicit toggle, equivalent to using `.rdataz`:

```java
resource.save(out, Map.of(
    CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE));
```

---

## Shared tabular options

These live in `CodecTabularOptions` and apply to **every** tabular format (CSV,
ODS, XLSX, R-Lang). Same key, same semantics across all four.

| Key | Meaning |
|---|---|
| `OPTION_REFERENCE_MODE` | `IGNORE` (attributes only) / `FLAT` (dotted) / `SQL_TABLES` (per-EClass with FKs). |
| `OPTION_FK_COLUMN_SUFFIX` | FK column suffix; default `"_id"`. |
| `OPTION_SCHEMAS` | `Map<EClass-or-EPackage, String>` — drives subdirectory (CSV/ZIP) or sheet-name prefix (ODS/XLSX) or variable-name prefix (R-Lang). |
| `OPTION_COLUMN_TYPES` | Per-feature SQL type override; keyed preferably by `EStructuralFeature`. |
| `OPTION_MULTI_VALUED_REF_STRATEGY` | `PREFER_FK_COLUMN` (default; FK on child) or `ALWAYS_JOIN_TABLE` (separate join table). |

Example combining several:

```java
resource.save(out, Map.of(
    CodecTabularOptions.OPTION_REFERENCE_MODE,             ReferenceMode.SQL_TABLES,
    CodecTabularOptions.OPTION_FK_COLUMN_SUFFIX,           "_fk",
    CodecTabularOptions.OPTION_SCHEMAS,                    Map.of(testPackage, "ops"),
    CodecTabularOptions.OPTION_MULTI_VALUED_REF_STRATEGY,  MultiValuedRefStrategy.ALWAYS_JOIN_TABLE,
    CodecTabularOptions.OPTION_COLUMN_TYPES,               Map.of(
        productClass.getEStructuralFeature("price"), "DECIMAL(15,2)")));
```

---

## Save-option validation

Each format provider validates the URI extension against the save options and
flags mismatches that would produce surprising file contents. By default the
mismatch is **logged at WARN** and added to the resource's diagnostics
(`resource.getWarnings()`), and the save still proceeds.

To turn the warning into a hard failure, set
`CodecOptions.CODEC_THROW_ON_VALIDATION_WARNINGS=true`:

```java
resource.save(out, Map.of(
    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES,
    CodecOptions.CODEC_THROW_ON_VALIDATION_WARNINGS, Boolean.TRUE));
// → IllegalStateException because .csv + SQL_TABLES produces ZIP bytes,
//    but the URI says it's a CSV.
```

Mismatch cases the providers currently flag:

| Format | URI ends in | + option says | Warning |
|---|---|---|---|
| CSV | `.csv` | `SQL_TABLES` | ZIP bytes in a `.csv` file |
| CSV | `.csvz` | `IGNORE` or `FLAT` | flat CSV bytes in a `.csvz` file |
| R-Lang | `.RData` | `dataframePerFile=true` | ZIP bytes in a `.RData` file |
| R-Lang | `.rdataz` | `dataframePerFile=false` | single RData blob in a `.rdataz` file |

To inspect diagnostics after a save (default warn-and-proceed mode):

```java
resource.save(out, Map.of(
    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES));
for (Resource.Diagnostic d : resource.getWarnings()) {
    System.out.println(d.getMessage());
}
```

---

## Picking the right format

| Use case | Format |
|---|---|
| Exchange with SQL databases (subdirectory = schema) | `.csvz` |
| Single flat data dump for a script / pandas / Excel paste | `.csv` (IGNORE or FLAT) |
| Hand to a non-technical user who opens it in LibreOffice | `.ods` |
| Hand to a non-technical user who opens it in Microsoft Excel | `.xlsx` (clickable FKs!) |
| Pipe to an R / RStudio session | `.RData` (single-file) |
| Hand to an R user who wants per-table downloads | `.rdataz` |
