# Codec Options Reference

Complete reference for all load/save options across core codec, format-specific settings,
and REST client overrides. For architecture and configuration resolution rules see
`docs/codec-v2-spec/02-config-resolution.md`. For end-to-end tabular format examples
see `docs/tabular-exporter-examples.md`.

---

## Table of contents

- [Configuration model](#configuration-model)
- [Core options](#core-options)
  - [Type strategy](#type-strategy)
  - [ID strategy](#id-strategy)
  - [Reference](#reference)
  - [Feature & visibility](#feature--visibility)
  - [SuperType](#supertype)
  - [Global](#global)
  - [Runtime (load/save only)](#runtime-loadsave-only)
- [Format options](#format-options)
  - [JSON](#json)
  - [BSON](#bson)
  - [CBOR](#cbor)
  - [YAML](#yaml)
  - [CSV](#csv)
  - [ODS](#ods)
  - [XLSX](#xlsx)
  - [R Language](#r-language-rdata)
  - [Tabular shared](#tabular-shared)
  - [JSON Schema](#json-schema)
- [REST client-overridable options](#rest-client-overridable-options)

---

## Configuration model

Options are resolved from six sources in descending priority. Higher-priority sources
win at every key they specify; unset keys fall through to the next level.

| Priority | Source |
|---|---|
| 1 (highest) | Load / save options map (per-operation) |
| 2 | Resource config |
| 3 | ResourceFactory defaults |
| 4 | CodecModule config (global) |
| 5 | EAnnotation on the model element |
| 6 (lowest) | Built-in defaults |

Options also have a **scope**: *Global*, *EClass*, or *Feature*. A feature-scoped value is
the most specific and overrides the global value for that feature alone. Runtime-only options
(root type hint, value reader instances, etc.) exist only in the load/save map — they cannot
be set via EAnnotations.

Both the short key (e.g. `serializeNull`) and the prefixed key (e.g. `codec.serializeNull`)
are accepted in the options map. **EAnnotation details** always use the short key.
The `CodecOptions` constants use the prefixed form and are the recommended way to pass
options in code.

---

## Core options

These options apply to every format. Java constants are in
`org.eclipse.fennec.codec.constants.CodecOptions`.

### Type strategy

| Option key | Constant | Type | Default | Description |
|---|---|---|---|---|
| `codec.typeStrategy` | `CODEC_TYPE_STRATEGY` | String | `URI` | How type info is written/read. Values: `URI`, `NAME`, `CLASS`, `SCHEMA_AND_TYPE`, `NUMERIC`, `NONE`. |
| `codec.typeKey` | `CODEC_TYPE_KEY` | String | `_type` | JSON property name for the type discriminator field. |
| `codec.typeFormat` | `CODEC_TYPE_FORMAT` | String | `PLAIN` | `PLAIN` writes a bare string; `STRUCTURED` writes `{"type":"…","schema":"…"}`. |
| `codec.typeInclude` | `CODEC_TYPE_INCLUDE` | Boolean | `true` | Set `false` to suppress the type field entirely. |
| `codec.typeNameKey` | `CODEC_TYPE_NAME_KEY` | String | `type` | Inner key for the class name in STRUCTURED format. |
| `codec.typeSchemaKey` | `CODEC_TYPE_SCHEMA_KEY` | String | `schema` | Inner key for the schema URI in STRUCTURED format. |
| `codec.typeScope` | `CODEC_TYPE_SCOPE` | String | `ALL` | Limit where the type strategy is applied. Values: `ALL`, `SUBTYPE_ONLY`. |
| `codec.smartCompression` | `CODEC_SMART_COMPRESSION` | Boolean | `false` | Omit `_type` when the concrete type is unambiguous from context (containment reference with a single possible EClass). |
| `codec.fingerprintMode` | `CODEC_FINGERPRINT_MODE` | String | `NONE` | Write the in-band EPackage fingerprint. `NONE` writes none; `FIRST_TOUCH` writes it at the first occurrence of each package instance. Reading is always liberal and independent of this option. |
| `codec.fingerprintKey` | `CODEC_FINGERPRINT_KEY` | String | `fingerprint` | Key carrying the fingerprint (PLAIN sibling: `_fingerprint`). The only way to tell a *reader* about a non-default key — annotations configure it for writing only. |

### ID strategy

| Option key | Constant | Type | Default | Description |
|---|---|---|---|---|
| `codec.idStrategy` | `CODEC_ID_STRATEGY` | String | `ID_FIELD` | How the identity key is written. Values: `ID_FIELD` (EID attribute), `COMBINED` (multiple features joined), `NONE`. |
| `codec.idKey` | `CODEC_ID_KEY` | String | `_id` | JSON property name for the generated ID field. |
| `codec.idKeyMode` | `CODEC_ID_KEY_MODE` | String | `ID_ONLY` | Controls which key names appear. Values: `ID_ONLY`, `FEATURE_ONLY`, `BOTH`, `NONE`. |
| `codec.idFormat` | `CODEC_ID_FORMAT` | String | `PLAIN` | `PLAIN` writes a bare value; `STRUCTURED` wraps it in an object. |
| `codec.idOnTop` | `CODEC_ID_ON_TOP` | Boolean | `true` | Write the ID field before the type field. |
| `codec.idFeatures` | `CODEC_ID_FEATURES` | List\<String\> | — | Feature names to concatenate for `COMBINED` strategy. |
| `codec.idSeparator` | `CODEC_ID_SEPARATOR` | String | `-` | Separator between parts in `COMBINED` strategy. |

### Reference

| Option key | Constant | Type | Default | Description |
|---|---|---|---|---|
| `codec.refFormat` | `CODEC_REF_FORMAT` | String | `PLAIN` | `PLAIN` writes a bare URI string; `STRUCTURED` wraps it in `{"$ref":"…"}` (with optional `_type`). |
| `codec.refKey` | `CODEC_REF_KEY` | String | `$ref` | Key for the reference URI inside a STRUCTURED object. |
| `codec.refTypeKey` | `CODEC_REF_TYPE_KEY` | String | `_type` | Key for the type hint inside a STRUCTURED reference object. |
| `codec.expand` | `CODEC_EXPAND` | Boolean | `false` | Inline (expand) non-containment referenced objects instead of emitting a `$ref`. |
| `codec.expandDepth` | `CODEC_EXPAND_DEPTH` | Integer | `1` | Maximum depth for recursive expansion. |
| `codec.expandIgnoreBidirectional` | `CODEC_EXPAND_IGNORE_BIDIRECTIONAL` | Boolean | `true` | Skip the back-pointer side of bidirectional references during expansion. |
| `codec.loadReferencedResources` | `CODEC_LOAD_REFERENCED_RESOURCES` | Boolean | `false` | Let resolution load the resource a cross-document reference names. Off means resolution uses what the ResourceSet already holds and leaves a proxy otherwise. |
| `codec.refUriSchemes` | `CODEC_REF_URI_SCHEMES` | List\<String\> | — | URI schemes a reference in the document may name. Empty is unrestricted, with a warning for schemes that leave the process; naming schemes makes it an allowlist and refuses the rest. |

### Feature & visibility

| Option key | Constant | Type | Default | Description |
|---|---|---|---|---|
| `codec.serializeNull` | `CODEC_SERIALIZE_NULL` | Boolean | `false` | Write features whose value is `null`. |
| `codec.serializeEmpty` | `CODEC_SERIALIZE_EMPTY` | Boolean | `false` | Write many-valued features that are empty collections. |
| `codec.serializeDefaults` | `CODEC_SERIALIZE_DEFAULTS` | Boolean | `false` | Write features whose value equals the EAttribute default. |
| `codec.enumSerialization` | `CODEC_ENUM_SERIALIZATION` | String | `LITERAL` | How enum values are written. Values: `LITERAL` (name string), `VALUE` (integer ordinal), `NAME` (EMF name). |
| `codec.dateFormat` | `CODEC_DATE_FORMAT` | String | none | `SimpleDateFormat` pattern for `java.util.Date` attributes, written as string; a configured pattern also keeps the instant-like `java.time` types (`Instant`, `LocalDateTime`, `LocalDate`) on their ISO-8601 string form. Without a pattern, formats with a native date-time type (BSON) store these types natively as epoch milliseconds (zone-less types use the UTC convention); other formats fall back to ISO-8601 `toString()` for `java.time` and to `Date.toString()` for `Date` (the latter is write-only — not parseable on load, configure a pattern for string round-trips). Zoned/offset types always stay on the ISO string path. |
| `codec.fieldOrder` | `CODEC_FIELD_ORDER` | String | `DECLARATION` | Column/field ordering for tabular formats. Values: `DECLARATION`, `ALPHABETICAL`. |
| `codec.key` | `CODEC_KEY` | String | feature name | Override the JSON property name for a specific feature (annotation or per-feature scope only). |
| `codec.transient` | `CODEC_TRANSIENT` | Boolean | `false` | Mark a feature as not serialized/deserialized. |

### SuperType

| Option key | Constant | Type | Default | Description |
|---|---|---|---|---|
| `codec.superTypeSerialize` | `CODEC_SUPERTYPE_SERIALIZE` | Boolean | `false` | Include supertype information as a dedicated field. |
| `codec.superTypeKey` | `CODEC_SUPERTYPE_KEY` | String | `_superTypes` | JSON key for the supertype field. |
| `codec.superTypeStrategy` | `CODEC_SUPERTYPE_STRATEGY` | String | `ALL` | Which supertypes to include. Values: `ALL`, `ALL_EMF`, `SINGLE`, `NONE`. |
| `codec.superTypeAsArray` | `CODEC_SUPERTYPE_AS_ARRAY` | Boolean | `true` | Write supertypes as a JSON array. `false` writes a separator-delimited string. |

### Global

| Option key | Constant | Type | Default | Description |
|---|---|---|---|---|
| `codec.typeMapId` | `CODEC_TYPE_MAP_ID` | String | — | ID of the discriminator mapping registry to use for type resolution. |
| `codec.fallbackStrategy` | `CODEC_FALLBACK_STRATEGY` | String | `SKIP` | What to do when a discriminator value cannot be resolved. Values: `SKIP`, `FALLBACK`, `ERROR`. |
| `codec.deserializationMode` | `CODEC_DESERIALIZATION_MODE` | String | `LENIENT` | Type-resolution failure handling. `STRICT` throws; `LENIENT` records a warning and continues. |
| `codec.throwOnValidationWarnings` | `CODEC_THROW_ON_VALIDATION_WARNINGS` | Boolean | `false` | Turn validation warnings (e.g. URI/option mismatch) into exceptions. |
| `codec.maxPayloadSize` | `CODEC_MAX_PAYLOAD_SIZE` | Long | `104857600` | Maximum bytes read from an input stream (100 MB default). Protects against DoS via oversized payloads. |

### Runtime (load/save only)

These keys are valid only in the per-operation options map. They cannot be set via
EAnnotations or `CodecModule` config.

| Option key | Constant | Type | Description |
|---|---|---|---|
| `codec.rootType` | `CODEC_ROOT_TYPE` | EClass or URI String | Type hint for the root object during load. Required when the JSON has no `_type` field and the type cannot be inferred. Also accepted under the literal key `"CODEC_ROOT_TYPE"` (see below). |
| `codec.rootSchema` | `CODEC_ROOT_SCHEMA` | String (nsURI) or EPackage | EPackage namespace URI (or EPackage instance) used as context for `NAME` strategy type resolution. The instance form is multi-version-safe. Also accepted under the literal key `"CODEC_ROOT_SCHEMA"` (see below). |
| `codec.rootFingerprint` | `CODEC_ROOT_FINGERPRINT` | String (fingerprint) | Optional. Package model fingerprint selecting the version a String root type/schema resolves against under same-nsURI multi-version. Unknown or conflicting fingerprint → error (both modes). |
| `codec.featureTypeHints` | `CODEC_FEATURE_TYPE_HINTS` | Map\<String, EClass\> | Per-feature type hints keyed by feature name. |
| `codec.featureValueReaderInstances` | `CODEC_FEATURE_VALUE_READER_INSTANCES` | Map\<EStructuralFeature, CodecValueReader\> | Bind reader instances directly to features (bypasses the registry). Works for EAttributes and EReferences. |
| `codec.featureValueWriterInstances` | `CODEC_FEATURE_VALUE_WRITER_INSTANCES` | Map\<EStructuralFeature, CodecValueWriter\> | Bind writer instances directly to features (bypasses the registry). Works for EAttributes and EReferences. |
| `codec.featureValueReaders` | `CODEC_FEATURE_VALUE_READERS` | Map\<EStructuralFeature, String\> | **Deprecated** — bind registered readers via `"ClassName.featureName"` → `valueReaderName` instead. Attributes only; ignored for references. |
| `codec.featureValueWriters` | `CODEC_FEATURE_VALUE_WRITERS` | Map\<EStructuralFeature, String\> | **Deprecated** — bind registered writers via `"ClassName.featureName"` → `valueWriterName` instead. Attributes only; ignored for references. |
| `codec.prefixReaderInstances` | `CODEC_PREFIX_READER_INSTANCES` | Map\<String, CodecPrefixReader\> | Bind a prefix reader to a document key for this load; wins over the `CodecPrefixRegistry` for that key. See [Prefix readers/writers](#prefix-readerswriters). |
| `codec.prefixWriterInstances` | `CODEC_PREFIX_WRITER_INSTANCES` | Map\<String, CodecPrefixWriter\> | Bind a prefix writer to a document key for this save; wins over the registry for that key. |
| `codec.eClassConfig` | `CODEC_ECLASS_CONFIG` | Map\<EClass, Map\> | Per-EClass option overrides applied at the EClass scope level. |
| `codec.eReferenceConfig` | `CODEC_EREFERENCE_CONFIG` | Map\<EReference, Map\> | Per-EReference option overrides. |
| `codec.eAttributeConfig` | `CODEC_EATTRIBUTE_CONFIG` | Map\<EAttribute, Map\> | Per-EAttribute option overrides. |

#### Two keys for the root type and root schema

These two options are the only ones with a second, non-dotted key. `codec.rootType` is the
canonical key; `"CODEC_ROOT_TYPE"` — the value of `CodecResource.CODEC_ROOT_TYPE`, which
most existing code passes — names the same option, and the same holds for
`codec.rootSchema` / `"CODEC_ROOT_SCHEMA"`. Either key works at every reading site.

```java
Map<Object, Object> options = new HashMap<>();
options.put(CodecOptions.CODEC_ROOT_TYPE, customerEClass);   // codec.rootType
options.put(CodecResource.CODEC_ROOT_TYPE, customerEClass);  // CODEC_ROOT_TYPE - same option
```

Setting both keys to the *same* value is redundant but harmless. Setting them to values that
disagree fails the load with an error naming both keys and both values, in every strictness
mode — two contradictory statements about one option are a caller bug, not a precedence
question. Values count as equal by identity/equality, so an `EClass` under one key and a type
URI String under the other is a conflict even if they would resolve to the same type.

`codec.rootFingerprint` has no second key.

> **Not supported — runtime registry registration:** there is no option to register
> readers/writers into the `CodecValueRegistry` per load/save operation. In particular,
> a reader/writer that a model annotation (`valueReaderName`/`valueWriterName`) refers to
> cannot be supplied at load time — it must be in the registry when the resource is
> created (factory, OSGi service, or programmatic registration). For ad-hoc cases bind an
> instance per feature with `codec.featureValueReaderInstances`/`...WriterInstances`.
> The former `codec.valueReaders`/`codec.valueWriters` options were never functional and
> have been removed (issue #45).

---

## Format options

Each format accepts all core options plus the format-specific keys listed below.

### JSON

**Bundle:** `org.eclipse.fennec.codec` · **Extension:** `.json` · **Content-Type:** `application/json`

JSON is the default format. It uses the native Jackson path and does not define any
format-specific option keys — all behaviour is controlled by the core options above.
Pretty-printing is not currently exposed as a save option.

---

### BSON

**Bundle:** `org.eclipse.fennec.codec.bson` · **Extension:** `.bson` · **Content-Type:** `application/bson`

BSON has no load/save option keys of its own. The only BSON-specific tuning is the
**maximum payload size**, which is set at construction time on `BsonFormatProvider` and
cannot be changed per-operation:

```java
// 50 MB limit instead of the default 100 MB
BsonFormatProvider provider = new BsonFormatProvider(50L * 1024 * 1024);
```

BSON does not support array-root resources (`supportsArrayRoot()` returns `false`);
a resource with more than one root object cannot be serialized to BSON.

---

### CBOR

**Bundle:** `org.eclipse.fennec.codec.cbor` · **Extension:** `.cbor` · **Content-Type:** `application/cbor`

CBOR is a compact binary JSON superset handled by Jackson's CBOR data format.
No format-specific option keys are defined — all core options apply unchanged.

---

### YAML

**Bundle:** `org.eclipse.fennec.codec.yaml` · **Extensions:** `.yaml`, `.yml` · **Content-Types:** `application/yaml`, `text/yaml`

YAML output is produced via Jackson's YAML data format. No format-specific option keys
are defined — all core options apply unchanged.

---

### CSV

**Bundle:** `org.eclipse.fennec.codec.csv` · **Extensions:** `.csv`, `.csvz` · **Constants:** `CodecCsvOptions`

The `.csv` extension defaults to `IGNORE` reference mode (single flat file).
The `.csvz` extension defaults to `SQL_TABLES` (ZIP of CSVs, one per EClass).
See also [Tabular shared](#tabular-shared) below.

| Option key | Constant | Type | Default | REST | Description |
|---|---|---|---|---|---|
| `codec.csv.delimiter` | `OPTION_DELIMITER` | char / String | `,` | ✓ | Column separator character. |
| `codec.csv.quoteMode` | `OPTION_QUOTE_MODE` | String | `REQUIRED` | ✓ | When to quote fields. Values: `REQUIRED`, `ALWAYS`, `NON_EMPTY`, `EMPTY`. |
| `codec.csv.lineEnding` | `OPTION_LINE_ENDING` | String | `LF` | ✓ | Row terminator. Values: `LF`, `CR`, `CRLF`, `PLATFORM`. |
| `codec.csv.charset` | `OPTION_CHARSET` | String | `UTF-8` | ✓ | Character encoding for the output file. |
| `codec.csv.dataTypeInSecondRow` | `OPTION_DATA_TYPE_IN_SECOND_ROW` | Boolean | `true` | ✓ | Emit the SQL-type row (e.g. `VARCHAR`, `INTEGER`) as the second row after the header. Set `false` to output header + data only. |

---

### ODS

**Bundle:** `org.eclipse.fennec.codec.ods` · **Extension:** `.ods` · **Constants:** `CodecOdsOptions`

OpenDocument Spreadsheet. Cells carry native types. FK cells in `SQL_TABLES` mode become
clickable hyperlinks by default (requires the vendored sods fork with `LinkedValue` support).
The sods writer stores the FK id as the link's display text, making the cell a *string*;
set `OPTION_GENERATE_LINKS = false` to keep FK cells numeric.
See also [Tabular shared](#tabular-shared) below.

| Option key | Constant | Type | Default | REST | Description |
|---|---|---|---|---|---|
| `codec.ods.styleHeader` | `OPTION_STYLE_HEADER` | Boolean | `true` | ✓ | Apply bold + grey background to the header row. |
| `codec.ods.adjustColumnWidth` | `OPTION_ADJUST_COLUMN_WIDTH` | Boolean | `true` | ✓ | Auto-fit column widths to content. |
| `codec.ods.generateLinks` | `OPTION_GENERATE_LINKS` | Boolean | `true` | ✓ | Render FK cells as clickable hyperlinks to the target sheet in `SQL_TABLES` mode. |

---

### XLSX

**Bundle:** `org.eclipse.fennec.codec.xlsx` · **Extension:** `.xlsx` · **Constants:** `CodecXlsxOptions`

Microsoft Excel via Apache POI. Sheet names are auto-truncated to 31 characters with
collision suffixes. FK hyperlinks keep the cell value numeric (unlike ODS).
See also [Tabular shared](#tabular-shared) below.

| Option key | Constant | Type | Default | REST | Description |
|---|---|---|---|---|---|
| `codec.xlsx.styleHeader` | `OPTION_STYLE_HEADER` | Boolean | `true` | ✓ | Apply bold + grey background to the header row. |
| `codec.xlsx.adjustColumnWidth` | `OPTION_ADJUST_COLUMN_WIDTH` | Boolean | `true` | ✓ | Auto-fit column widths to content. |
| `codec.xlsx.freezeHeaderRow` | `OPTION_FREEZE_HEADER_ROW` | Boolean | `true` | ✓ | Freeze the first (header) row when scrolling. |
| `codec.xlsx.generateLinks` | `OPTION_GENERATE_LINKS` | Boolean | `true` | ✓ | Render FK cells as hyperlinks to the target sheet in `SQL_TABLES` mode. |
| `codec.xlsx.defaultDateFormat` | `OPTION_DEFAULT_DATE_FORMAT` | String | `m/d/yy h:mm` | ✓ | Excel number format pattern applied to date cells. |

---

### R Language (.RData)

**Bundle:** `org.eclipse.fennec.codec.rlang` · **Extensions:** `.RData`, `.rdataz` · **Constants:** `CodecRLangOptions`

Binary R serialization. Each table becomes a typed data frame (`INTSXP`, `REALSXP`,
`LGLSXP`, `STRSXP`, native `POSIXct` for dates).
See also [Tabular shared](#tabular-shared) below.

| Option key | Constant | Type | Default | REST | Description |
|---|---|---|---|---|---|
| `codec.rlang.dataframePerFile` | `OPTION_DATAFRAME_PER_FILE` | Boolean | `false` | ✓ | Write one `.RData` per data frame inside a ZIP archive (`.rdataz`). Auto-set to `true` when the URI ends in `.rdataz`. |

---

### Tabular shared

**Bundle:** `org.eclipse.fennec.codec.tabular` · **Applies to:** CSV, ODS, XLSX, R Language · **Constants:** `CodecTabularOptions`

| Option key | Constant | Type | Default | REST | Description |
|---|---|---|---|---|---|
| `codec.tabular.referenceMode` | `OPTION_REFERENCE_MODE` | String | format-dependent¹ | ✓ | How object relationships are rendered. Values: `IGNORE` (attributes only), `FLAT` (dotted column names), `SQL_TABLES` (one table per EClass with FK columns). |
| `codec.tabular.fkColumnSuffix` | `OPTION_FK_COLUMN_SUFFIX` | String | `_id` | — | Suffix appended to FK column names in `SQL_TABLES` mode. |
| `codec.tabular.schemas` | `OPTION_SCHEMAS` | Map\<EClass\|EPackage, String\> | — | — | Schema name prefix per EClass or EPackage. Drives subdirectory (ZIP), sheet-name prefix (ODS/XLSX), or variable-name prefix (R). |
| `codec.tabular.columnTypes` | `OPTION_COLUMN_TYPES` | Map\<EStructuralFeature, String\> | — | — | Override the SQL type string for specific features (e.g. `"DECIMAL(15,2)"`). |
| `codec.tabular.multiValuedRefStrategy` | `OPTION_MULTI_VALUED_REF_STRATEGY` | String | `PREFER_FK_COLUMN` | — | How multi-valued non-containment references are represented. Values: `PREFER_FK_COLUMN`, `ALWAYS_JOIN_TABLE`. |

¹ Default reference mode: `IGNORE` for `.csv`, `.ods`, `.xlsx`, `.RData`; `SQL_TABLES` for `.csvz`, `.rdataz`.

---

### JSON Schema

**Bundle:** `org.eclipse.fennec.codec.jsonschema` · **Content-Type:** `application/schema+json` · **Constants:** `CodecJsonSchemaOptions`

JSON Schema uses its own option namespace (`codec.jsonschema.*`). Options are passed
through `EffectiveCodecConfig.getCustomProperties()` — any `codec.*` key that does not
match a known `ConfigProperty` is automatically collected there.

| Option key | Constant | Type | Default | Description |
|---|---|---|---|---|
| `codec.jsonschema.draft` | `OPTION_SCHEMA_DRAFT` | String | `2020-12` | JSON Schema draft version. Values: `draft-04`, `draft-06`, `draft-07`, `2019-09`, `2020-12`. |
| `codec.jsonschema.pretty.print` | `OPTION_PRETTY_PRINT` | Boolean | `false` | Pretty-print the JSON Schema output. |
| `codec.jsonschema.allFieldsRequired` | `OPTION_ALL_FIELDS_REQUIRED` | Boolean | `false` | Mark every property as `required`. Useful for AI structured-output schemas. |
| `codec.jsonschema.flatAllOf` | `OPTION_FLAT_ALL_OF` | Boolean | `false` | Flatten `allOf` inheritance into a single object definition. |
| `codec.jsonschema.useAnchorRefs` | `OPTION_USE_ANCHOR_REFS` | Boolean | `false` | Use `$anchor` identifiers instead of JSON Pointer `$ref` paths. |
| `codec.jsonschema.inlineRefs` | `OPTION_INLINE_REFS` | Boolean | `false` | Replace all `$ref` uses with inlined definitions. Omits `$defs`. Cycle guard emits `{"type":"object"}` for recursive types. |
| `codec.jsonschema.useNamesFromExtendedMetadata` | `OPTION_USE_NAMES_FROM_EXTENDED_METADATA` | Boolean | `false` | Use `ExtendedMetaData` annotation names for properties instead of feature names. |
| `codec.jsonschema.suppressKeywords` | `OPTION_SUPPRESS_KEYWORDS` | Collection\<String\> | — | Suppress specific JSON Schema keywords from output (e.g. `maxItems`, `description`, `additionalProperties`). Useful for API validators that reject certain keywords. |
| `codec.jsonschema.suppressVendorExtensions` | `OPTION_SUPPRESS_VENDOR_EXTENSIONS` | Boolean | `false` | Omit `x-abstract`, `x-interface`, and `x-containment` vendor extensions from output. |
| `codec.jsonschema.useAnyOfForAbstract` | `OPTION_USE_ANY_OF_FOR_ABSTRACT` | Boolean | `false` | Use `anyOf` instead of `oneOf` for abstract type discriminators. |
| `codec.jsonschema.generateOclConstraints` | `OPTION_GENERATE_OCL_CONSTRAINTS` | Boolean | `false` | Load option. Compile JSON Schema assertion keywords (`minLength`, `maxLength`, `pattern`, `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`, `uniqueItems`) and `format` (`uuid`/`email`) into OCL invariants on the generated `EClass`es, using EMF's standard validation-delegate annotation convention (`validationDelegates` / `constraints` / delegate-URI-sourced expression details). See `docs/OCL-Constraint-Generation-Implementation-Plan.md`. |
| `codec.jsonschema.oclDelegateUri` | `OPTION_OCL_DELEGATE_URI` | String | `http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot` | Load option. The EMF validation-delegate URI generated invariants are registered under. Only relevant when `OPTION_GENERATE_OCL_CONSTRAINTS` is enabled. Any URI works as long as a matching `EValidator.ValidationDelegate` is registered at runtime (Eclipse OCL, its Pivot dialect, or a third-party engine). fennec-codec has no compile/runtime dependency on any OCL engine — it only writes the annotations. |

---

## Prefix readers/writers

A backend that stores one document per EObject can own document keys that have **no feature
behind them** — the persistence layer's `_owner` beside the codec's `_id`, for example. It
registers, *per key*, a `CodecPrefixWriter` that writes the field from the EObject and a
`CodecPrefixReader` that consumes it when the object is read back. Spec:
[Custom Values §13](codec-v2-spec/14-custom-values.md#13-prefix-readerswriters) (issue #193).

```java
CodecPrefixRegistry registry = new CodecPrefixRegistry()
    .register("_owner", (key, object, ctx) -> {          // CodecPrefixWriter
        if (object.eContainer() == null) return false;   // declines: no field for roots
        ctx.getGenerator().writeName(key);
        ctx.getGenerator().writeString(idOf(object.eContainer()));
        return true;
    })
    .register("_owner", (key, target, ctx) ->            // CodecPrefixReader
        ownerIndex.put(target, ctx.getParser().getString()));
```

What to expect:

| | write | read |
|---|---|---|
| **reader and writer registered** | field written after the metadata block (`_type`, `_id`, …), before the first feature, in registration order | consumed by the reader, no diagnostic — also under `strictOnUnknown` |
| **writer only** | field written | *unknown feature*: WARNING, or a failed load under `strictOnUnknown` — you wrote a key you do not read |
| **reader only** | nothing written | a foreign document carrying the key loads silently |
| **neither** | nothing written | unchanged behaviour |

- A key equal to a **feature name** of the class is written and read as the feature; the handler
  is skipped and one WARNING per class and key says so.
- A **reader that throws** is an ERROR diagnostic (the value is dropped); under
  `codec.deserializationMode=STRICT` the load fails. A writer that throws is a WARNING.
- **OSGi:** register the handlers as services with the property `codec.prefix.key`
  (`String` or `String[]`); `CodecPrefixRegistryComponent` collects them and every codec
  resource factory hands a copy of the registry to its resources. **Plain Java:** pass the
  registry to `CodecResource`, or `setPrefixRegistry(...)` on `CodecResourceFactory` /
  `CodecFormatResourceFactory`. **Per operation:** the two options above.
- No annotation, no per-class configuration: a prefix key belongs to the document store, not to
  the model.

## REST client-overridable options

A REST client can override a controlled subset of options per request by sending the
`Codec-Options` HTTP header with comma-separated `key=value` pairs:

```
POST /export
Accept: text/csv
Codec-Options: codec.tabular.referenceMode=SQL_TABLES, codec.csv.dataTypeInSecondRow=false
```

The `ClientCodecOptionsFilter` in `codec.rest` collects whitelists from all registered
`RestOverridableCodecOptions` services, drops any non-whitelisted keys, parses values by
declared type, and merges the result into the save/load options map.
**Client values win over annotation-based options.**

> **Server-side per-request options:** the request property the filter fills
> (`JakartaRestConstants.CLIENT_CODEC_OPTIONS`, a `Map<String, Object>`) is the general per-request
> channel, not a client-only one. An endpoint whose options are known only at runtime may write it
> from its own request filter or from the resource method. Client values win on a shared key in
> either order; see `codec-rest-client-overridable-options.md` §13 for the ordering rules.

> **Key format:** core options use their *short* ConfigProperty key (e.g. `serializeNull`),
> while format-specific options use their full prefixed key (e.g. `codec.csv.delimiter`).
> Non-whitelisted keys are silently ignored.

> **Security:** options with high blast-radius — `expand`, `expandDepth`, `typeStrategy`,
> value reader/writer names — are deliberately excluded from all whitelists. To expose a new
> key, implement `RestOverridableCodecOptions` in the owning bundle and register it as an
> OSGi DS component. See `docs/codec-rest-client-overridable-options.md` for the full design.

### Core — `CoreOverridableCodecOptions`

Applies to all formats.

| Header key | Type | Values |
|---|---|---|
| `serializeNull` | Boolean | `true` / `false` |
| `serializeEmpty` | Boolean | `true` / `false` |
| `serializeDefault` | Boolean | `true` / `false` |
| `enumSerialization` | String | `LITERAL`, `VALUE`, `NAME` |
| `fieldOrder` | String | `DECLARATION`, `ALPHABETICAL` |
| `idOnTop` | Boolean | `true` / `false` |
| `dateFormat` | String | Any `SimpleDateFormat` pattern, e.g. `yyyy-MM-dd` |

### CSV — `CsvOverridableCodecOptions`

Includes all core options above, plus:

| Header key | Type | Values |
|---|---|---|
| `codec.tabular.referenceMode` | String | `IGNORE`, `FLAT`, `SQL_TABLES` |
| `codec.csv.dataTypeInSecondRow` | Boolean | `true` / `false` |
| `codec.csv.delimiter` | String | Single character, e.g. `;` or `,` |
| `codec.csv.quoteMode` | String | `REQUIRED`, `ALWAYS`, `NON_EMPTY`, `EMPTY` |
| `codec.csv.lineEnding` | String | `LF`, `CR`, `CRLF`, `PLATFORM` |
| `codec.csv.charset` | String | Charset name, e.g. `UTF-8`, `ISO-8859-1` |

### ODS — `OdsOverridableCodecOptions`

Includes all core options above, plus:

| Header key | Type | Values |
|---|---|---|
| `codec.tabular.referenceMode` | String | `IGNORE`, `FLAT`, `SQL_TABLES` |
| `codec.ods.styleHeader` | Boolean | `true` / `false` |
| `codec.ods.adjustColumnWidth` | Boolean | `true` / `false` |
| `codec.ods.generateLinks` | Boolean | `true` / `false` |

### XLSX — `XlsxOverridableCodecOptions`

Includes all core options above, plus:

| Header key | Type | Values |
|---|---|---|
| `codec.tabular.referenceMode` | String | `IGNORE`, `FLAT`, `SQL_TABLES` |
| `codec.xlsx.styleHeader` | Boolean | `true` / `false` |
| `codec.xlsx.adjustColumnWidth` | Boolean | `true` / `false` |
| `codec.xlsx.freezeHeaderRow` | Boolean | `true` / `false` |
| `codec.xlsx.generateLinks` | Boolean | `true` / `false` |
| `codec.xlsx.defaultDateFormat` | String | Excel number format pattern, e.g. `yyyy-MM-dd` |

### R Language — `RLangOverridableCodecOptions`

Includes all core options above, plus:

| Header key | Type | Values |
|---|---|---|
| `codec.tabular.referenceMode` | String | `IGNORE`, `FLAT`, `SQL_TABLES` |
| `codec.rlang.dataframePerFile` | Boolean | `true` / `false` |
