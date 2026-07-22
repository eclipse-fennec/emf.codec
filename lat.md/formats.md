# Formats

The codec core is format-agnostic; this file maps the format landscape. It covers the delegate abstraction, the streaming formats (JSON, YAML, CBOR, BSON), the tabular exporters (CSV, ODS, XLSX, R), the schema formats, and REST transport.

## Format Abstraction

A format plugs in as a `CodecFormatProvider<S,T>` supplying a `FormatDelegate<T>` (write) and `FormatReaderDelegate<S>` (read); the codec runtime never sees the wire format.

The SPI interfaces live in `org.eclipse.fennec.codec.api/src/org/eclipse/fennec/codec/format/`.

Jackson bridges in `org.eclipse.fennec.codec` (`format/impl/`) wrap the delegates as regular Jackson streaming classes: `FormatDelegateGenerator<T>` (extends `GeneratorBase`) and `FormatDelegateParser<S>` (extends `ParserBase`). Providers declare capabilities such as `supportsArrayRoot()` (BSON: false). Spec chapter: `docs/codec-v2-spec/17-format-abstraction.md`; rationale: [[decisions#Format-Agnostic Core on Jackson 3]].

## Streaming Formats

JSON is the reference format, built directly into `org.eclipse.fennec.codec` via `JacksonFormatProvider`. YAML and CBOR subclass `JacksonFormatProvider` in their own bundles, swapping the Jackson dataformat underneath.

BSON (`org.eclipse.fennec.codec.bson`) is the proof of the abstraction: `BsonFormatDelegate`/`BsonFormatReaderDelegate` build an in-memory `BsonDocument` with no Jackson dataformat involved. All streaming formats pass the same TCK — see [[testing#TCK Suites]].

## Tabular Family

CSV, ODS, XLSX, and R-Lang exports share one pipeline: `TabularDocumentBuilder` turns an EObject graph into a format-neutral `TabularDocument`, and a per-format `TabularDocumentRenderer` writes it out.

The builder lives in `org.eclipse.fennec.codec.tabular`; the intermediate `TabularDocument` EMF model in `org.eclipse.fennec.codec.tabular.model`.

- **Reference modes**: IGNORE (drop references), FLAT (flatten into columns), SQL_TABLES (one table per EClass with `_id` PKs and FK columns; join tables for many-to-many)
- Renderers: `CsvRenderer`, `OdsRenderer` (vendored SODS fork), `XlsxRenderer` (Apache POI), `RLangRenderer`
- The builder honors the same codec semantics as the streaming path — value gates (`serializeNull/Empty/Default`), `enumSerialization`, `fieldOrder`, `idOnTop`, custom value writers — so the same object exports consistently in every format
- ODS/XLSX render FK cells as clickable hyperlinks to the target sheet (`generateLinks` option, default true)

## Schema Formats

Three bundles adapt the codec to schema-shaped domains: JSON Schema, OpenAPI, and GeoJSON.

`org.eclipse.fennec.codec.jsonschema` converts bidirectionally between JSON Schema documents and EMF `EPackage`s (`JsonSchemaToEPackageConverter` / `EPackageToJsonSchemaConverter`, package `...jsonschema.v2`). It can optionally compile schema validation keywords (`minLength`, `pattern`, `minimum`, …) into OCL invariants wired via EMF validation delegates — opt-in, engine-agnostic (`codec.jsonschema.generateOclConstraints`).

`org.eclipse.fennec.codec.openapi` reads/writes OpenAPI v3 documents into the EMF model of `org.eclipse.fennec.openapi.model`, delegating embedded `components/schemas` to the jsonschema converters via value readers/writers ([[concepts#Value Readers and Writers]]). `org.eclipse.fennec.codec.geojson` adapts the codec to GeoJSON using the external `org.geojson.model`.

Round-trip fidelity caveat (schema → EPackage → schema is semantically but not structurally identical): `org.eclipse.fennec.codec.jsonschema/jsonschema-architecture.md`.

## REST Integration

`org.eclipse.fennec.codec.rest` provides Jakarta REST (JAX-RS) `MessageBodyReader`/`Writer`s so endpoints can consume and produce `EObject`/`Resource` parameters directly.

Endpoint behavior is configured via the `@CodecConfig` and `@RootElement` annotations (converted to codec options by `CodecAnnotationConverter`); clients can additionally override whitelisted options per request — see [[configuration#REST Client Overrides]]. Format bundles advertise themselves to the OSGi resolver via `@RequireCodec*` / `@Capability` annotations in the `emf.configurator` namespace.
