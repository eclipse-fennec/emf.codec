# Format Abstraction and Custom Parsers/Generators

[← Annotation Reference](16-annotation-reference.md) | [Next: Scenarios →](18-scenarios.md)

---

> **See also:**
> - [Custom Values](14-custom-values.md) for value reader/writer interfaces and registration
> - [Error Handling](15-error-handling.md) for diagnostic reporting from custom formats

---

This chapter defines how the codec supports multiple serialization formats beyond JSON, including BSON (MongoDB), CSV, query strings, and custom protocols.

## 1. Overview

The codec logic - type, ID, reference, attribute handling, configuration, diagnostics - is written
once against **Jackson's streaming API** (`JsonParser` / `JsonGenerator`). Other formats plug in
underneath by presenting themselves as a Jackson parser and generator, so the codec logic never
sees which format it reads or writes.

| Format | Bundle | Backend | Read | Write |
|--------|--------|---------|:----:|:-----:|
| JSON | `codec` | Jackson (default path, or `JacksonFormatProvider`) | ✅ | ✅ |
| CBOR | `codec.cbor` | Jackson CBOR via `JacksonFormatProvider` | ✅ | ✅ |
| YAML | `codec.yaml` | Jackson YAML via `JacksonFormatProvider` | ✅ | ✅ |
| BSON | `codec.bson` | `org.mongodb.bson`, via `BsonDocument` | ✅ | ✅ |
| CSV, ODS, XLSX, RData | `codec.csv`, `codec.ods`, `codec.xlsx`, `codec.rlang` | tabular renderers (`codec.tabular`) | — | ✅ |

Format-specific *resources* build on this: GeoJSON, JSON Schema and OpenAPI (§11).

---

## 2. Architecture

### 2.1 Layered Design

```
┌──────────────────────────────────────────────────────────────────────┐
│  CodecResource (EMF Resource: load / save)                           │
└───────────────────────────────┬──────────────────────────────────────┘
                                │
┌───────────────────────────────▼──────────────────────────────────────┐
│  Codec logic (Jackson databind)                                      │
│  CodecEObjectSerializer / CodecEObjectDeserializer and the entries:  │
│  Type*, Id*, Reference*, Attribute* (De)SerializationEntry           │
│  Value transformation: CodecValueReader / CodecValueWriter           │
└───────────────────────────────┬──────────────────────────────────────┘
                                │ JsonParser / JsonGenerator
          ┌─────────────────────┴───────────────────────┐
          │                                             │
┌─────────▼──────────────┐              ┌───────────────▼─────────────────────┐
│ Default JSON path      │              │ Format provider path                │
│ CodecJsonFactory       │              │ FormatDelegateParser / -Generator   │
│ (no format provider)   │              │   adapt a FormatReaderDelegate /    │
└────────────────────────┘              │   FormatDelegate to Jackson         │
                                        └───────────────┬─────────────────────┘
                                                        │ CodecFormatProvider
                              ┌─────────────────────────┼──────────────────────────┐
                              ▼                         ▼                          ▼
                    JacksonFormatProvider      BsonFormatProvider        tabular providers
                    (JSON, CBOR, YAML)         (BsonDocument)            (CSV, ODS, XLSX, RData;
                                                                          write only)
```

`CodecResource` takes the default JSON path when it has no format provider, and the provider path
otherwise (`doLoadWithFormat` / `doSaveWithFormat`).

### 2.2 Token Model

A `FormatReaderDelegate` reports its position as a `TokenType`
(`org.eclipse.fennec.codec.format`), which `FormatDelegateParser` maps onto Jackson's
`JsonToken`:

```java
public enum TokenType {
    START_OBJECT, END_OBJECT, START_ARRAY, END_ARRAY,
    FIELD_NAME,
    VALUE_STRING, VALUE_NUMBER_INT, VALUE_NUMBER_FLOAT, VALUE_BOOLEAN, VALUE_NULL,
    VALUE_BINARY,        // native binary (BSON); surfaces as VALUE_EMBEDDED_OBJECT
    NOT_AVAILABLE
}
```

---

## 3. Format Interfaces

All in `org.eclipse.fennec.codec.format` (bundle `codec.api`).

### 3.1 CodecFormatProvider

The factory of one format: it creates the reader and writer delegates for a source or target.

```java
public interface CodecFormatProvider<S, T> {
    String getFormatId();
    String[] getFileExtensions();                 // default: { getFormatId() }
    String[] getContentTypes();                   // default: none

    FormatReaderDelegate<S> createReader(S source) throws IOException;
    default FormatReaderDelegate<S> createReader(S source, Map<String, Object> loadOptions); // #232

    FormatDelegate<T> createWriter(T target) throws IOException;
    default FormatDelegate<T> createWriter(T target, EObject root, Map<String, Object> saveOptions);
    default FormatDelegate<T> createWriter(T target, List<? extends EObject> roots,
            Map<String, Object> saveOptions);
    default FormatDelegate<T> createWriter(T target, List<? extends EObject> roots,
            Map<String, Object> saveOptions, ConfigurationResolver resolver);

    default boolean supportsArrayRoot();          // default true; BSON: false
    default boolean supportsInBandFingerprint();  // default true; column formats: false
    default List<String> validateSaveOptions(URI uri, Map<String, Object> options);
}
```

The richer `createWriter` / `createReader` overloads default to the plain ones, so a provider only
overrides what it needs: the tabular providers use the resolver overload to walk the model
themselves, and the read-limit mapping (§7.3) uses `createReader(source, loadOptions)`.

### 3.2 FormatReaderDelegate

```java
public interface FormatReaderDelegate<S> {
    void setSource(S source);
    S getSource();
    TokenType nextToken() throws IOException;
    TokenType currentToken();
    String currentName() throws IOException;
    void skipChildren() throws IOException;
    String readString() throws IOException;
    int readInt() throws IOException;
    long readLong() throws IOException;
    float readFloat() throws IOException;
    double readDouble() throws IOException;
    BigInteger readBigInteger() throws IOException;
    BigDecimal readBigDecimal() throws IOException;
    boolean readBoolean() throws IOException;
    byte[] readBinary() throws IOException;
    default boolean supportsNativeObjectId();     // default false
    default Object readObjectId() throws IOException; // default readString()
    void close() throws IOException;
}
```

### 3.3 FormatDelegate

```java
public interface FormatDelegate<T> {
    void setTarget(T target);
    T getTarget();
    void writeStartObject() throws IOException;
    void writeEndObject() throws IOException;
    void writeStartArray() throws IOException;
    void writeEndArray() throws IOException;
    void writeName(String name) throws IOException;
    void writeString(String value) throws IOException;
    void writeInt(int value) throws IOException;
    void writeLong(long value) throws IOException;
    void writeFloat(float value) throws IOException;
    void writeDouble(double value) throws IOException;
    void writeBigInteger(BigInteger value) throws IOException;
    void writeBigDecimal(BigDecimal value) throws IOException;
    void writeBoolean(boolean value) throws IOException;
    void writeNull() throws IOException;
    void writeBinary(byte[] data) throws IOException;
    default boolean supportsNativeObjectId();     // default false
    default void writeObjectId(Object value) throws IOException;
    default boolean supportsNativeDateTime();     // default false
    default void writeDateTime(long epochMillis) throws IOException;
    void flush() throws IOException;
    void close() throws IOException;
}
```

### 3.4 The Jackson Bridge

`FormatDelegateParser` (a `JsonParser`) and `FormatDelegateGenerator` (a `JsonGenerator`), in
`org.eclipse.fennec.codec.format.jackson`, wrap a delegate so the unchanged codec logic reads and
writes through it. `getDelegate()` returns the wrapped delegate for code that needs a native
capability (§6).

---

## 4. Value Reader/Writer Abstraction

> **See also:** [Custom Values](14-custom-values.md) for the complete interface definitions.

Value readers and writers work on Jackson's parser and generator through a context:

```java
public interface CodecValueReader<T, F extends EStructuralFeature> {
    String getName();                              // registry name
    T read(CodecReaderContext ctx, F feature) throws IOException;
}

public interface CodecValueWriter<T, F extends EStructuralFeature> {
    String getName();
    void write(T value, F feature, CodecWriterContext ctx) throws IOException;
}

public interface CodecReaderContext {
    JsonParser getParser();
    DeserializationContext getJacksonContext();
    EffectiveCodecConfig getConfig();
    DiagnosticCollector getDiagnostics();
    default void addWarning(String message);
    default void addError(String message);
}

public interface CodecWriterContext {
    JsonGenerator getGenerator();
    SerializationContext getJacksonContext();
    EffectiveCodecConfig getConfig();
    DiagnosticCollector getDiagnostics();
    default void addWarning(String message);
    default void addError(String message);
}
```

Because every format is a `JsonParser` / `JsonGenerator` to the codec, a value reader or writer
works across formats unchanged. On the provider path the parser is a `FormatDelegateParser` and
the generator a `FormatDelegateGenerator`.

> **See also:** [Error Handling](15-error-handling.md) for diagnostic reporting from custom readers/writers.

---

## 5. Format Providers

### 5.1 JacksonFormatProvider (JSON, CBOR, YAML)

Wraps a Jackson `TokenStreamFactory`. `CborFormatProvider` and `YamlFormatProvider` are thin
subclasses with a `CBORFactory` / `YAMLFactory`:

```java
new JacksonFormatProvider("json", new JsonFactory());
new CborFormatProvider();
new YamlFormatProvider();
```

JSON can also be read and written without a provider, on the default path (`CodecJsonFactory`).

### 5.2 BsonFormatProvider

Reads the input into a `BsonDocument` and streams it through `BsonFormatReaderDelegate`; writes
through `BsonFormatDelegate` into a `BsonDocument` that is encoded at the end. It supports native
ObjectId, date-time and binary values (§6) and no array root.

### 5.3 Tabular Providers (CSV, ODS, XLSX, RData)

Write only - `createReader` throws `UnsupportedOperationException`. They use the
`createWriter(…, resolver)` overload and render through `codec.tabular`
(`TabularDocumentBuilder`), because a table is not a token stream. They validate save options
against the URI (`validateSaveOptions`); CSV, ODS and XLSX do not carry an in-band fingerprint.

---

## 6. Format-Specific Features

### 6.1 Native Type Support

Some formats have native types that don't exist in JSON:

| Format | Native Types | Handling |
|--------|--------------|----------|
| BSON | ObjectId, DateTime, Binary | `supportsNativeObjectId()` / `writeObjectId` / `readObjectId`, `supportsNativeDateTime()` / `writeDateTime`, `writeBinary` / `readBinary` |
| JSON, CBOR, YAML | - (CBOR: binary) | values as the format's primitives; binary through Jackson (`writeBinary`: Base64 in JSON) |
| CSV, ODS, XLSX, RData | - | cell values, rendered by `codec.tabular` |

**Native date-time (built-in).** The format delegate declares a native date-time
type via `supportsNativeDateTime()` (default `false`) and writes it via
`writeDateTime(long epochMillis)` — mirroring the `supportsNativeObjectId()` /
`writeObjectId(Object)` escape hatch. Temporal attribute values without a
configured `dateFormat` are written natively when the delegate supports it (BSON:
`BsonDateTime`); a configured `dateFormat` always wins and keeps every temporal
value on the string vocabulary. On read, a native date-time surfaces as
`VALUE_NUMBER_INT` carrying epoch milliseconds, which the attribute
deserialization converts back to the target temporal type — no dedicated token
type is needed. Formats without native support are unchanged.

Covered temporal types and their epoch-millisecond mapping:

| Type | Write | Read back | Notes |
|------|-------|-----------|-------|
| `java.util.Date` | `getTime()` | `new Date(millis)` | `dateFormat` wins; without it the legacy `toString()` fallback (non-native formats) is write-only |
| `java.time.Instant` | `toEpochMilli()` | `Instant.ofEpochMilli(millis)` | sub-millisecond precision is truncated (BSON DateTime is millis) |
| `java.time.LocalDateTime` | UTC convention | `LocalDateTime.ofInstant(…, UTC)` | zone-less — UTC convention on both sides, lossless round trip |
| `java.time.LocalDate` | UTC start of day | `Instant.ofEpochMilli(…).atZone(UTC).toLocalDate()` | lossless round trip |
| `OffsetDateTime`, `ZonedDateTime` | — (ISO string) | `parse(CharSequence)` | never native: an epoch instant cannot restore the offset/zone; the ISO-8601 `toString()` form round-trips exactly |

All other `java.time` types (`LocalTime`, `Duration`, `Period`, `Year`, `YearMonth`,
…) stay on the ISO string path unconditionally.

### 6.2 Native Values in a Custom Value Writer or Reader

A value writer reaches the format through the generator. On the provider path it is a
`FormatDelegateGenerator`, whose delegate reports its capabilities:

```java
public class ObjectIdValueWriter implements CodecValueWriter<String, EAttribute> {

    @Override
    public String getName() {
        return "objectId";
    }

    @Override
    public void write(String value, EAttribute attr, CodecWriterContext ctx) throws IOException {
        if (ctx.getGenerator() instanceof FormatDelegateGenerator<?> gen
                && gen.getDelegate().supportsNativeObjectId()) {
            gen.getDelegate().writeObjectId(value);      // native ObjectId (BSON)
        } else {
            ctx.getGenerator().writeString(value);       // every other format
        }
    }
}
```

A value reader does the same through `FormatDelegateParser.getDelegate()` (e.g. `readObjectId()`).

### 6.3 Binary Data Handling

Binary needs no custom writer: the codec writes `byte[]` through `JsonGenerator.writeBinary`,
which JSON encodes as Base64 and `FormatDelegateGenerator` hands to `FormatDelegate.writeBinary`
(native binary in BSON). On read, a native binary value surfaces as `VALUE_EMBEDDED_OBJECT`.

---

## 7. Configuration

### 7.1 Format Selection

The format is determined by the resource factory the resource came from, which is selected by:

1. **File extension**: `.json`, `.bson`, `.csv`
2. **Content type**: `application/json`, `application/bson`

There is no load/save option that switches the format of an existing resource. An earlier draft
named a `CODEC_FORMAT` option for this; it was never implemented, and the constant was removed
(#222).

### 7.2 Resource Factories

There is no central format registry. Each format bundle registers an OSGi `Resource.Factory`
component whose service properties carry the file extension and content type
(`EMFNamespaces.EMF_MODEL_FILE_EXT`, `EMFNamespaces.EMF_MODEL_CONTENT_TYPE`); EMF's OSGi
integration picks the factory by those properties.

| Component | Bundle | Extensions | Content types |
|-----------|--------|------------|---------------|
| `CodecResourceFactoryComponent` | `codec` | `json` | `application/json` |
| `BsonResourceFactoryComponent` | `codec.bson` | `bson` | `application/bson` |
| `CborResourceFactoryComponent` | `codec.cbor` | `cbor` | `application/cbor` |
| `YamlResourceFactoryComponent` | `codec.yaml` | `yaml`, `yml` | `application/yaml`, `text/yaml` |
| `CsvResourceFactoryComponent` | `codec.csv` | `csv`, `csvz` | `text/csv`, `application/x-csv-zip` |
| `OdsResourceFactoryComponent` | `codec.ods` | `ods` | `application/vnd.oasis.opendocument.spreadsheet` |
| `XlsxResourceFactoryComponent` | `codec.xlsx` | `xlsx` | — |
| `RLangResourceFactoryComponent` | `codec.rlang` | `RData`, `rdataz` | `application/x-rdata`, `application/x-rdata-zip` |

Outside OSGi, `CodecFormatResourceFactory` creates `CodecResource`s for a given provider:

```java
CodecFormatResourceFactory factory =
        new CodecFormatResourceFactory(metadataService, new CborFormatProvider());
Resource resource = factory.createResource(URI.createURI("data.cbor"));
```

### 7.3 Read Limits

The read limits of a load (`codec.maxPayloadSize`, `maxNestingDepth`, `maxStringLength`,
`maxNameLength`, `maxCollectionSize`) reach a provider through
`createReader(source, loadOptions)`, which maps them onto the format's own settings - see
[Error Handling §9.0](15-error-handling.md).

> **Earlier design, not implemented.** A previous version of this document described a stream
> abstraction (`CodecStreamReader`, `CodecStreamWriter`, `CodecLocation`, `CodecToken`), format
> adapters (including a query-string adapter), a `CodecFormatRegistry` / `CodecFormatAdapter`
> and a three-phase migration path towards them. None of these exist; the Jackson bridge (§3.4)
> took their place.

---

## 8. Format Extension Projects

The following projects provide pre-configured resources for specific formats:

| Project | Format | Base Class | Description |
|---------|--------|------------|-------------|
| `org.eclipse.fennec.codec.geojson` | GeoJSON | `CodecResource` | Pre-configured for GeoJSON with `type` key, NAME strategy |
| `org.eclipse.fennec.codec.jsonschema` | JSON Schema | `ResourceImpl` | Meta-format: JSON Schema ↔ EPackage conversion |

**Note:** Most format extensions extend `CodecResource` for standard EObject serialization. JSON Schema is special because it's a **meta-format** that converts the schema itself (EPackage), not instances.

### 8.1 GeoJSON Extension

**Project:** `org.eclipse.fennec.codec.geojson`

Pre-configured `CodecResource` for [GeoJSON](https://geojson.org/) format:

```java
// Configuration applied automatically (GeoJsonResourceImpl)
ConfigurationResolver.builder()
    .typeKey("type")                          // GeoJSON uses "type" not "_type"
    .typeStrategy(TypeStrategy.NAME)          // Simple names: Point, Feature, etc.
    .useNamesFromExtendedMetaData(true)       // Maps "coordinates" correctly
    .useId(false)                             // Feature.id is a regular property
    .typeInclude(true)
    .forceWrite(volatileFeatures)             // Volatile "data" and "bbox" attributes
    .forceRead(volatileFeatures)
    .build();
```

**Usage:**

```java
// OSGi - inject via DS
@Reference
Resource.Factory geoJsonFactory;

Resource resource = geoJsonFactory.createResource(URI.createURI("map.geojson"));
resource.load(inputStream, Collections.emptyMap());
FeatureCollection fc = (FeatureCollection) resource.getContents().get(0);

// Non-OSGi - create directly
MetadataService metadataService = MetadataServiceFactory.create();
metadataService.registerPackage(GeoJsonPackage.eINSTANCE);

GeoJsonResourceImpl resource = new GeoJsonResourceImpl(
    URI.createURI("map.geojson"),
    metadataService);
```

### 8.2 JSON Schema Extension

**Project:** `org.eclipse.fennec.codec.jsonschema`

Provides bidirectional conversion between JSON Schema and EMF EPackage. Unlike other format extensions, JSON Schema is a **meta-format** that converts between metamodels rather than serializing EObjects.

#### Architecture Decision

JSON Schema conversion operates at a different level than normal codec operations:
- **Normal codec**: Serializes/deserializes EObject instances using EPackage as schema
- **JSON Schema**: Converts the EPackage itself to/from a schema format

Therefore, the JSON Schema extension provides **two integration patterns**:

| Pattern | Use Case | Implementation |
|---------|----------|----------------|
| **Standalone** | `.jsonschema` files, schema generation | `JsonSchemaResourceImpl` (extends `ResourceImpl`) |
| **Embedded** | OpenAPI `components/schemas`, AI structured output | `EPackageValueReader` / `EPackageValueWriter` |

#### 8.2.1 Standalone Mode

For standalone JSON Schema files, use `JsonSchemaResourceImpl` directly:

```java
// Load JSON Schema → EPackage
JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
    URI.createURI("schema.jsonschema"));

Map<String, Object> options = new HashMap<>();
options.put(JsonSchemaResourceImpl.OPTION_SCHEMA_FEATURE, "definitions");

resource.load(inputStream, options);
EPackage ePackage = (EPackage) resource.getContents().get(0);

// Save EPackage → JSON Schema
resource.getContents().add(myEPackage);
resource.save(outputStream, options);
```

**Supported Options:**

| Option | Values | Description |
|--------|--------|-------------|
| `OPTION_SCHEMA_FEATURE` | `"definitions"`, `"$defs"`, `"schemas"`, `null` | Key for schema definitions (null = auto-detect) |
| `OPTION_PRETTY_PRINT` | `true`, `false` | Format output with indentation (default: true) |
| `OPTION_SCHEMA_DRAFT` | `"draft-04"`, `"draft-07"`, `"2020-12"` | JSON Schema draft version |

**Note:** `JsonSchemaResourceImpl` extends `ResourceImpl` directly, not `CodecResource`, because it performs meta-format conversion rather than standard EObject serialization.

#### 8.2.2 Embedded Mode

For JSON Schema embedded within other formats (e.g., OpenAPI), use the value handlers that integrate with codec v2's value transformation layer:

```java
// Register value handlers for embedded schema
CodecValueRegistry registry = new CodecValueRegistry();

// Reader: JSON Schema → EPackage
registry.registerReader(
    EcorePackage.Literals.EPACKAGE,                    // Value type
    OpenApiPackage.Literals.COMPONENTS__SCHEMAS,       // Feature
    new EPackageValueReader("schemas")                 // Handler
);

// Writer: EPackage → JSON Schema
registry.registerWriter(
    EcorePackage.Literals.EPACKAGE,
    OpenApiPackage.Literals.COMPONENTS__SCHEMAS,
    new EPackageValueWriter("schemas", true)           // embedInFeature=true
);

// Use with CodecResource
CodecResource resource = new CodecResource(
    uri, metadataService, config, registry, null);
```

**EPackageValueReader:**

| Constructor | Description |
|-------------|-------------|
| `EPackageValueReader()` | Auto-detect schema feature |
| `EPackageValueReader(schemaFeature)` | Use specific feature key |

**EPackageValueWriter:**

| Constructor | Description |
|-------------|-------------|
| `EPackageValueWriter()` | Full JSON Schema document |
| `EPackageValueWriter(schemaFeature)` | Specific feature key |
| `EPackageValueWriter(schemaFeature, embedInFeature)` | If `true`, output only definitions content |

#### 8.2.3 JSON Schema Features

The converters support these JSON Schema features:

**Deserialization (JSON Schema → EPackage):**

| JSON Schema | EMF Mapping |
|-------------|-------------|
| `$id` | `EPackage.nsURI` |
| `title` | `EPackage.name` |
| `type: "object"` | `EClass` |
| `type: "string" + enum` | `EEnum` |
| `properties` | `EAttribute` / `EReference` |
| `$ref` | `EReference` (non-containment) |
| `allOf` | `ESuperTypes` (inheritance) |
| `oneOf` (discriminated) | Abstract base + concrete subclasses |
| `oneOf` (variants) | Base class with variant subclasses |
| `type: ["string", "integer"]` | Artificial union class |

**Enhanced Features:**

| Annotation | Effect |
|------------|--------|
| `minProperties: 1, maxProperties: 1` | Discriminated union pattern |
| Nested definitions (e.g., `configs/kafka`) | `namespacePath` annotation |
| Top-level `properties` | `rootClass` annotation |
| `minLength`, `maxLength`, `pattern`, etc. | Preserved as annotations |

**Serialization (EPackage → JSON Schema):**

| EMF Element | JSON Schema Output |
|-------------|-------------------|
| `EPackage` | Schema document with `$id`, `title` |
| `EClass` | `type: "object"` with `properties` |
| `EEnum` | `type: "string"` with `enum` |
| `ESuperTypes` | `allOf` with `$ref` |
| `EAttribute (many)` | `type: "array"` |
| `EReference (containment)` | Nested object |
| `EReference (non-containment)` | `$ref` |

#### 8.2.4 Example: OpenAPI Integration

```java
// OpenAPI document with embedded schemas
{
  "openapi": "3.0.0",
  "info": { "title": "My API", "version": "1.0" },
  "components": {
    "schemas": {
      "Person": {
        "type": "object",
        "properties": {
          "name": { "type": "string" },
          "age": { "type": "integer" }
        }
      }
    }
  }
}
```

With registered value handlers, the `components.schemas` object is automatically converted to/from an `EPackage` containing the `Person` EClass.

### 8.3 Creating Custom Format Extensions

To create a custom format extension:

1. **Extend `CodecResource`** with format-specific configuration:

```java
public class MyFormatResourceImpl extends CodecResource {

    public static final ConfigurationResolver MY_FORMAT_CONFIG = ConfigurationResolver.builder()
        .typeKey("@type")
        .typeStrategy(TypeStrategy.URI)
        // ... format-specific settings
        .build();

    public MyFormatResourceImpl(URI uri, MetadataService metadataService) {
        super(uri, metadataService, MY_FORMAT_CONFIG, null, null);
    }
}
```

2. **Create ResourceFactory** as OSGi DS component:

```java
@Component(service = Resource.Factory.class, property = {
    EMFNamespaces.EMF_MODEL_FILE_EXT + "=myformat"
})
public class MyFormatResourceFactoryImpl extends ResourceFactoryImpl {

    private final MetadataService metadataService;

    @Activate
    public MyFormatResourceFactoryImpl(@Reference MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public Resource createResource(URI uri) {
        return new MyFormatResourceImpl(uri, metadataService);
    }
}
```

---

## 9. JSON Schema Version Support and Feature Coverage

This section provides a comprehensive reference for JSON Schema support in the codec.

### 9.1 Supported JSON Schema Versions

The JSON Schema converter supports multiple draft versions:

| Draft Version | `$schema` URI | Definitions Key | Status |
|---------------|---------------|-----------------|--------|
| Draft-04 | `http://json-schema.org/draft-04/schema#` | `definitions` | ✅ Supported |
| Draft-06 | `http://json-schema.org/draft-06/schema#` | `definitions` | ✅ Supported |
| Draft-07 | `http://json-schema.org/draft-07/schema#` | `definitions` | ✅ Supported (Primary) |
| Draft 2019-09 | `https://json-schema.org/draft/2019-09/schema` | `$defs` | ✅ Supported |
| Draft 2020-12 | `https://json-schema.org/draft/2020-12/schema` | `$defs` | ✅ Supported |

**Note:** The converter auto-detects the definitions key (`definitions` vs `$defs`) or uses the explicitly specified `OPTION_SCHEMA_FEATURE`.

### 9.2 Complete Feature Matrix

#### 9.2.1 Core Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `$schema` | EAnnotation | ✅ | ✅ | Preserved in annotation |
| `$id` | `EPackage.nsURI` | ✅ | ✅ | |
| `$ref` | `EReference` | ✅ | ✅ | Non-containment reference |
| `$defs` / `definitions` | EClassifiers | ✅ | ✅ | Auto-detected |
| `$anchor` | EAnnotation + classifierMap | ✅ | ✅ | Local schema reference by name |
| `$dynamicRef` | - | ❌ | ❌ | Draft 2020-12, not supported |
| `$dynamicAnchor` | - | ❌ | ❌ | Draft 2020-12, not supported |
| `$vocabulary` | - | ❌ | ❌ | Meta-schema feature |

#### 9.2.2 Type Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `type: "object"` | `EClass` | ✅ | ✅ | |
| `type: "array"` | `upperBound = -1` | ✅ | ✅ | |
| `type: "string"` | `EString` | ✅ | ✅ | |
| `type: "number"` | `EDouble` | ✅ | ✅ | |
| `type: "integer"` | `EInt` | ✅ | ✅ | |
| `type: "boolean"` | `EBoolean` | ✅ | ✅ | |
| `type: "null"` | - | ⚠️ | ⚠️ | Handled via nullability |
| `type: ["string", "integer"]` | Union class | ✅ | ✅ | Creates artificial base + variants |

#### 9.2.3 Object Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `properties` | `EStructuralFeature` | ✅ | ✅ | Creates attributes/references |
| `required` | `lowerBound = 1` | ✅ | ✅ | |
| `additionalProperties` | EAnnotation | ✅ | ✅ | Boolean or schema, preserved |
| `patternProperties` | - | ⚠️ | ⚠️ | Preserved as annotation, no EMF equivalent |
| `propertyNames` | - | ❌ | ❌ | Not mappable to EMF |
| `minProperties` | EAnnotation | ✅ | ✅ | Used for discriminated union detection |
| `maxProperties` | EAnnotation | ✅ | ✅ | Used for discriminated union detection |
| `unevaluatedProperties` | - | ⚠️ | ✅ | Written for discriminated unions |
| `dependentRequired` | - | ❌ | ❌ | Not mappable to EMF |
| `dependentSchemas` | - | ❌ | ❌ | Not mappable to EMF |

#### 9.2.4 Array Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `items` | Element type | ✅ | ✅ | Single schema for all items |
| `prefixItems` | - | ❌ | ❌ | Draft 2020-12 tuple validation |
| `minItems` | `lowerBound` | ✅ | ✅ | |
| `maxItems` | `upperBound` | ✅ | ✅ | |
| `uniqueItems` | EAnnotation | ✅ | ✅ | Preserved as annotation |
| `contains` | - | ❌ | ❌ | Not mappable to EMF |
| `minContains` | - | ❌ | ❌ | Not mappable to EMF |
| `maxContains` | - | ❌ | ❌ | Not mappable to EMF |
| `unevaluatedItems` | - | ❌ | ❌ | Draft 2020-12 |

#### 9.2.5 Composition Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `allOf` | `ESuperTypes` | ✅ | ✅ | Inheritance hierarchy |
| `anyOf` | Abstract + subtypes | ✅ | ✅ | Creates parent with common props |
| `oneOf` | Abstract + subtypes | ✅ | ✅ | Discriminated union or variants |
| `not` | - | ❌ | ❌ | Not mappable to EMF |
| `if` / `then` / `else` | - | ❌ | ❌ | Conditional schemas not mappable |

#### 9.2.6 String Validation Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `minLength` | EAnnotation | ✅ | ✅ | Preserved for validation |
| `maxLength` | EAnnotation | ✅ | ✅ | Preserved for validation |
| `pattern` | EAnnotation | ✅ | ✅ | Regex pattern preserved |
| `format` | EAnnotation | ✅ | ✅ | See format table below |

**Recognized String Formats:**

| Format | Preserved | Notes |
|--------|:---------:|-------|
| `date-time` | ✅ | ISO 8601 |
| `date` | ✅ | |
| `time` | ✅ | |
| `duration` | ✅ | ISO 8601 duration |
| `email` | ✅ | |
| `idn-email` | ✅ | |
| `hostname` | ✅ | |
| `idn-hostname` | ✅ | |
| `ipv4` | ✅ | |
| `ipv6` | ✅ | |
| `uri` | ✅ | |
| `uri-reference` | ✅ | |
| `iri` | ✅ | |
| `iri-reference` | ✅ | |
| `uuid` | ✅ | |
| `uri-template` | ✅ | |
| `json-pointer` | ✅ | |
| `relative-json-pointer` | ✅ | |
| `regex` | ✅ | |

#### 9.2.7 Numeric Validation Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `minimum` | EAnnotation | ✅ | ✅ | |
| `maximum` | EAnnotation | ✅ | ✅ | |
| `exclusiveMinimum` | EAnnotation | ✅ | ✅ | |
| `exclusiveMaximum` | EAnnotation | ✅ | ✅ | |
| `multipleOf` | EAnnotation | ✅ | ✅ | |

#### 9.2.8 Annotation Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `title` | Name / EAnnotation | ✅ | ✅ | Used for EPackage.name |
| `description` | GenModel documentation | ✅ | ✅ | |
| `default` | EAnnotation | ✅ | ✅ | |
| `examples` | EAnnotation | ✅ | ✅ | |
| `deprecated` | GenModel annotation | ✅ | ✅ | Uses GenModel for tooling support |
| `readOnly` | EAnnotation | ✅ | ✅ | |
| `writeOnly` | EAnnotation | ✅ | ✅ | |
| `$comment` | EAnnotation | ✅ | ✅ | Preserved as "comment" annotation |

#### 9.2.9 Content Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `contentEncoding` | EAnnotation | ✅ | ✅ | e.g., "base64" |
| `contentMediaType` | EAnnotation | ✅ | ✅ | e.g., "image/png" |
| `contentSchema` | - | ❌ | ❌ | Complex, not mappable |

#### 9.2.10 Enum and Const

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `enum` | `EEnum` | ✅ | ✅ | String enums become EEnum |
| `const` | EAnnotation | ✅ | ✅ | Fixed value preserved |

### 9.3 Feature Legend

| Symbol | Meaning |
|--------|---------|
| ✅ | Fully supported |
| ⚠️ | Partially supported (preserved as annotation, may not round-trip perfectly) |
| ❌ | Not supported |

### 9.4 EMF Limitations

The following JSON Schema features have **no natural EMF equivalent** and cannot be represented:

1. **Conditional Schemas** (`if`/`then`/`else`): EMF has no conditional feature mechanism
2. **Negation** (`not`): EMF cannot express "not this type"
3. **Tuple Validation** (`prefixItems`): EMF arrays are homogeneous
4. **Property Names Validation** (`propertyNames`): EMF features have fixed names
5. **Contains Constraints** (`contains`, `minContains`, `maxContains`): EMF has no "at least one matching" constraint
6. **Dependent Constraints** (`dependentRequired`, `dependentSchemas`): No EMF equivalent
7. **Dynamic References** (`$dynamicRef`, `$dynamicAnchor`): Complex recursive patterns
8. **Content Schema** (`contentSchema`): Complex embedded schema for content validation

### 9.5 Annotations Source

All JSON Schema metadata is preserved in EMF EAnnotations with these sources:

| Source | Purpose |
|--------|---------|
| `http://fennec.eclipse.org/jsonschema` | JSON Schema-specific metadata |
| `http://www.eclipse.org/emf/2002/GenModel` | Documentation (description) |
| `http:///org/eclipse/emf/ecore/util/ExtendedMetaData` | Original names |

### 9.6 Special Patterns

#### 9.6.1 Discriminated Unions

When JSON Schema uses the pattern:
```json
{
  "type": "object",
  "minProperties": 1,
  "maxProperties": 1,
  "oneOf": [
    { "required": ["kafka"], "properties": { "kafka": { "$ref": "..." } } },
    { "required": ["file"], "properties": { "file": { "$ref": "..." } } }
  ]
}
```

This creates:
- Abstract EClass with `discriminatedUnion=true` annotation
- Concrete subclasses for each option with `discriminatorKey` annotation
- Type mapping annotations for codec deserialization

#### 9.6.2 Context-Specific Variants (oneOf without discriminator)

When `oneOf` has multiple complete schemas with overlapping properties:
- Creates abstract base class with `commonBase=true` annotation
- Extracts common properties to base class
- Creates variant subclasses with `variant=<title>` annotation

#### 9.6.3 Namespace Paths

Nested definition structures like `definitions/configs/kafka` are handled:
- Intermediate nodes without schema keywords are organizational namespaces
- EClassifiers get `namespacePath` annotation (e.g., `configs`)
- `$ref` paths resolve correctly across namespaces

### 9.7 Diagnostic Warnings

> **See also:** [Error Handling](15-error-handling.md) for the general codec diagnostics mechanism.

The JSON Schema converter reports issues through EMF's standard diagnostics mechanism. After loading a schema, check `resource.getWarnings()` for any conversion warnings.

#### 9.7.1 Accessing Diagnostics

```java
// Load schema
JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
    URI.createURI("schema.jsonschema"));
resource.load(inputStream, options);

// Check for warnings about unsupported features
for (Resource.Diagnostic warning : resource.getWarnings()) {
    System.out.println("Warning: " + warning.getMessage());
    System.out.println("  Location: " + warning.getLocation());
}

// Alternatively, access converter diagnostics directly
JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
EPackage ePackage = converter.convert(inputStream, "definitions");

for (JsonSchemaConversionDiagnostic diag : converter.getDiagnostics()) {
    System.out.println("[" + diag.getCode() + "] " + diag.getMessage());
}
```

#### 9.7.2 Diagnostic Codes

| Code | Description | Example Keywords |
|------|-------------|------------------|
| `UNSUPPORTED_FEATURE` | Keyword cannot be mapped to EMF | `not`, `if`/`then`/`else`, `prefixItems`, `contains` |
| `PARTIAL_SUPPORT` | Keyword preserved as annotation but no semantic EMF equivalent | `patternProperties`, `$comment` |
| `COMPLEX_ANYOF` | Complex `anyOf` with different schemas detected | - |
| `UNRESOLVED_REFERENCE` | A `$ref` could not be resolved | - |

#### 9.7.3 Warning Messages

| Condition | Warning Message |
|-----------|-----------------|
| Unsupported keyword | "Unsupported JSON Schema keyword '{keyword}' - cannot be mapped to EMF" |
| Partially supported keyword | "Keyword '{keyword}' is partially supported: {detail}" |
| Complex `anyOf` | "Complex anyOf with different schemas detected. May require manual modeling." |
| Unresolved `$ref` | "Could not resolve reference: {path}" |

#### 9.7.4 Helper Class: JsonSchemaKeywords

The `JsonSchemaKeywords` utility class provides programmatic access to keyword support information:

```java
// Check support level for a keyword
JsonSchemaKeywords.SupportLevel level = JsonSchemaKeywords.getSupportLevel("not");
// Returns: SupportLevel.NONE

level = JsonSchemaKeywords.getSupportLevel("properties");
// Returns: SupportLevel.FULL

level = JsonSchemaKeywords.getSupportLevel("patternProperties");
// Returns: SupportLevel.PARTIAL

// Check keyword categories
boolean isSupported = JsonSchemaKeywords.isFullySupported("allOf");     // true
boolean isUnsupported = JsonSchemaKeywords.isUnsupported("prefixItems"); // true
```

### 9.8 Round-Trip Fidelity

**Round-trip guaranteed** for:
- Basic types (string, number, integer, boolean)
- Object structures with properties
- Arrays with items and bounds (minItems/maxItems)
- Enums
- Inheritance (`allOf`)
- Required properties
- Descriptions and documentation
- Format annotations
- Validation constraints (min/max, pattern, etc.)

**Round-trip may differ** for:
- `oneOf`/`anyOf` structures (structural changes for EMF compatibility)
- Multi-type properties (converted to union classes)
- Deeply nested namespace paths
- `patternProperties` (preserved but not semantically mapped)

### 9.9 Schema Reference Methods: `$anchor` vs JSON Pointer

JSON Schema supports two methods for referencing definitions within a schema:

#### 9.9.1 JSON Pointer References (Default)

JSON Pointer references use the path syntax `#/definitions/Name`:

```json
{
  "definitions": {
    "Address": {
      "type": "object",
      "properties": {
        "street": { "type": "string" }
      }
    },
    "Person": {
      "type": "object",
      "properties": {
        "home": { "$ref": "#/definitions/Address" }
      }
    }
  }
}
```

**Advantages:**
- Universal - works in all JSON Schema versions
- Self-documenting - shows the exact path to the definition
- Default behavior - no special configuration needed

#### 9.9.2 Anchor-Based References

Anchor-based references use `$anchor` to define a short name and `#anchorName` to reference it:

```json
{
  "definitions": {
    "Address": {
      "$anchor": "address",
      "type": "object",
      "properties": {
        "street": { "type": "string" }
      }
    },
    "Person": {
      "type": "object",
      "properties": {
        "home": { "$ref": "#address" }
      }
    }
  }
}
```

**Advantages:**
- Shorter references in large schemas
- References survive definition moves/renames
- Introduced in JSON Schema 2019-09

#### 9.9.3 Serialization Options

When converting EPackage to JSON Schema, the default is JSON Pointer references. To generate anchor-based references, use the `OPTION_USE_ANCHOR_REFS` option:

```java
// Default: JSON Pointer references
EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
writer.convert(ePackage, outputStream, "definitions", true);
// Output: "$ref": "#/definitions/Address"

// With anchor option: generates $anchor and uses anchor refs
Map<String, Object> options = Map.of(
    EPackageToJsonSchemaConverter.OPTION_USE_ANCHOR_REFS, true
);
writer.convert(ePackage, outputStream, "definitions", true, options);
// Output: "$anchor": "address" and "$ref": "#address"
```

#### 9.9.4 Round-Trip Behavior

| Input Schema | Output without option | Output with `OPTION_USE_ANCHOR_REFS` |
|--------------|----------------------|--------------------------------------|
| JSON Pointer refs | JSON Pointer refs | Anchor refs (anchors generated) |
| Anchor refs | Anchor refs (preserved) | Anchor refs (preserved) |
| Mixed | Mixed (preserved) | Anchor refs where possible |

**Note:** Existing `$anchor` annotations from the input schema are always preserved, regardless of the option setting.

#### 9.9.5 Per-Class Override

You can also enable anchors for specific classes via EAnnotation:

```java
// Add annotation to specific EClass
EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
annotation.setSource("http://fennec.eclipse.org/jsonschema");
annotation.getDetails().put("useAnchor", "true");
myEClass.getEAnnotations().add(annotation);
```

This generates `$anchor` for that class and uses anchor refs when referencing it, even without the global option.

---

## 10. Working with Generated EPackages

Once you've converted a JSON Schema to an EPackage, you can use it for deserializing JSON data that conforms to the schema.

### 10.1 Example: Simple Schema

```java
// Step 1: Load JSON Schema and convert to EPackage
JsonSchemaResourceImpl schemaRes = new JsonSchemaResourceImpl(
    URI.createURI("meter-reading.jsonschema"));

Map<String, Object> schemaOptions = new HashMap<>();
schemaOptions.put(JsonSchemaResourceImpl.OPTION_SCHEMA_FEATURE, "definitions");

schemaRes.load(inputStream, schemaOptions);
EPackage ePackage = (EPackage) schemaRes.getContents().get(0);

// Step 2: Register the generated EPackage
resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);

// Step 3: Find the target EClass
EClass meterReadingClass = (EClass) ePackage.getEClassifier("MeterReading");

// Step 4: Deserialize JSON data using the generated EPackage
// (requires codec v2 CodecResource with proper configuration)
```

### 10.2 Important: EPackage Registration

When working with dynamically generated EPackages, you must register them in:

1. **ResourceSet's package registry** - so EMF can find the EPackage by URI:
   ```java
   resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
   ```

2. **MetadataService** (for codec v2) - so codec can generate metadata:
   ```java
   metadataService.registerPackage(ePackage);
   ```

### 10.3 Limitations with oneOf / Union Types

JSON Schema's `oneOf` construct creates challenges for deserialization:

**The Problem:**
- When converting `oneOf` to EMF, an abstract EClass is typically generated with concrete variant subclasses
- During deserialization, the codec needs to determine which concrete subclass to instantiate
- JSON data doesn't always contain explicit type discriminators

**Current Workaround:**

Provide explicit type mapping via load options:

```java
Map<String, Object> options = Map.of(
    CodecOptions.CODEC_ROOT_TYPE, rootEClass,
    CodecOptions.CODEC_ECLASS_CONFIG, Map.of(inputNodeClass, Map.of(
        CodecOptions.CODEC_TYPE_KEY, "_type",
        CodecOptions.CODEC_TYPE_MAP_ID, "inputNodes",
        CodecOptions.CODEC_TYPE_DISCRIMINATOR_PATH, "_type",
        CodecOptions.CODEC_TYPE_MAPPINGS, Map.of(
            "kafka", kafkaInputNodeClass,
            "file", fileInputNodeClass))));
```

(Type mappings from options: 08 §4.4.)

**Known Limitation:**

Type mapping based solely on property name presence (discriminating based on which property is set) is not currently supported. You must either:
1. Add explicit type discriminator fields to your JSON data
2. Pre-determine the type through other means and specify it in options

---

## 11. Future Work

The following features are planned but not yet implemented. See the linked documents for implementation details.

### 11.1 Tuple Validation (`prefixItems`)

JSON Schema's `prefixItems` keyword for arrays with typed positional elements (tuples).

**Status:** Planned

**Details:** [todo/jsonschema-prefixItems-implementation.md](todo/jsonschema-prefixItems-implementation.md)

### 11.2 Format-to-EDataType Mapping

Map JSON Schema `format` values to proper EMF EDataTypes instead of just preserving as annotations.

| Format | Target Type |
|--------|-------------|
| `date-time` | `EDate` (built-in) |
| `date` | `LocalDate` (new) |
| `time` | `LocalTime` (new) |
| `duration` | `Duration` (new) |
| `uuid` | `UUID` (new) |
| `uri` | `URI` (new) |

**Status:** Planned (pending team discussion on EMF contribution)

**Details:** [todo/jsonschema-format-datatypes-implementation.md](todo/jsonschema-format-datatypes-implementation.md)

---

## 12. OpenAPI Integration Example

This section demonstrates a complete real-world integration: embedding JSON Schema handling within OpenAPI documents using the value reader/writer pattern.

### 12.1 Overview

OpenAPI 3.x documents embed JSON Schema definitions in `components/schemas`. The Fennec codec handles this by:

1. **Reading**: Converting JSON Schema objects to EMF `EPackage` with `EClass` definitions
2. **Writing**: Converting `EPackage` back to JSON Schema format

This is implemented using the `ReferenceValueReader/Writer` pattern described in [Custom Values](14-custom-values.md).

### 12.2 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     OpenApiResourceImpl                          │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                   CodecValueRegistry                         ││
│  │  ┌─────────────────────┐  ┌─────────────────────┐           ││
│  │  │ EPackageValueReader │  │ EPackageValueWriter │           ││
│  │  │   (name: "schemas") │  │   (name: "schemas") │           ││
│  │  └──────────┬──────────┘  └──────────┬──────────┘           ││
│  └─────────────┼────────────────────────┼───────────────────────┘│
│                │                        │                        │
│                ▼                        ▼                        │
│  ┌─────────────────────────┐  ┌─────────────────────────────┐   │
│  │ JsonSchemaToEPackage    │  │ EPackageToJsonSchema        │   │
│  │ Converter               │  │ Converter                   │   │
│  └─────────────────────────┘  └─────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 12.3 Model Definition

The OpenAPI model defines the `schemas` reference as a containment to `EPackage`:

```xml
<!-- openapi.ecore -->
<eClassifiers xsi:type="ecore:EClass" name="Components">
  <eStructuralFeatures xsi:type="ecore:EReference" name="schemas"
                       eType="ecore:EClass http://www.eclipse.org/emf/2002/Ecore#//EPackage"
                       containment="true">
    <eAnnotations source="http://eclipse.org/fennec/codec">
      <details key="valueReaderName" value="schemas"/>
      <details key="valueWriterName" value="schemas"/>
    </eAnnotations>
  </eStructuralFeatures>
</eClassifiers>
```

Key points:
- `eType` is `EPackage` from Ecore metamodel
- `containment="true"` means the schemas are owned by the Components object
- Codec annotations specify the reader/writer names in the registry

### 12.4 Resource Configuration

#### 12.4.1 OpenApiResourceImpl

```java
public class OpenApiResourceImpl extends CodecResource {

    public OpenApiResourceImpl(URI uri) {
        super(uri);
    }

    @Override
    protected CodecValueRegistry createValueRegistry() {
        CodecValueRegistry registry = new CodecValueRegistry();

        // Register JSON Schema ↔ EPackage converters
        registry.registerReader("schemas", new EPackageValueReader());
        registry.registerWriter("schemas", new EPackageValueWriter("schemas", true));

        return registry;
    }
}
```

#### 12.4.2 Factory Registration

```java
public class OpenApiResourceFactoryImpl implements Resource.Factory {

    @Override
    public Resource createResource(URI uri) {
        return new OpenApiResourceImpl(uri);
    }
}
```

### 12.5 Value Reader/Writer Implementation

#### 12.5.1 EPackageValueReader

```java
public class EPackageValueReader implements ReferenceValueReader<EPackage> {

    @Override
    public boolean canHandle(EReference reference) {
        // Only handle references to EPackage
        return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(
            reference.getEReferenceType());
    }

    @Override
    public EPackage read(JsonParser parser, EReference ref,
                         DeserializationContext ctxt) throws IOException {
        // Use converter to transform JSON Schema to EPackage
        JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
        return converter.convert(parser);
    }
}
```

#### 12.5.2 EPackageValueWriter

```java
public class EPackageValueWriter implements ReferenceValueWriter<EPackage> {

    private final String definitionsKey;
    private final boolean useDefinitions;

    public EPackageValueWriter(String definitionsKey, boolean useDefinitions) {
        this.definitionsKey = definitionsKey;
        this.useDefinitions = useDefinitions;
    }

    @Override
    public boolean canHandle(EReference reference) {
        return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(
            reference.getEReferenceType());
    }

    @Override
    public void write(EPackage value, EReference ref, JsonGenerator gen,
                      SerializationContext ctxt) throws IOException {
        EPackageToJsonSchemaConverter converter =
            new EPackageToJsonSchemaConverter(useDefinitions, definitionsKey);
        converter.convert(value, gen);
    }
}
```

### 12.6 Usage Example

#### 12.6.1 Loading an OpenAPI Document

```java
// Register factory
Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
    .put("json", new OpenApiResourceFactoryImpl());

// Register package
EPackage.Registry.INSTANCE.put(
    OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);

// Load
ResourceSet resourceSet = new ResourceSetImpl();
Resource resource = resourceSet.createResource(
    URI.createFileURI("petstore.json"));

Map<String, Object> options = new HashMap<>();
options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
resource.load(options);

// Access
OpenApi openApi = (OpenApi) resource.getContents().get(0);
EPackage schemas = openApi.getComponents().getSchemas();

// Use schemas
EClass petClass = (EClass) schemas.getEClassifier("Pet");
EAttribute nameAttr = (EAttribute) petClass.getEStructuralFeature("name");
```

#### 12.6.2 Creating and Saving

```java
// Create OpenAPI programmatically
OpenApi openApi = OpenApiFactory.eINSTANCE.createOpenApi();
openApi.setOpenapi("3.0.3");

Info info = OpenApiFactory.eINSTANCE.createInfo();
info.setTitle("My API");
info.setVersion("1.0.0");
openApi.setInfo(info);

// Create schemas as EPackage
EPackage schemas = EcoreFactory.eINSTANCE.createEPackage();
schemas.setName("schemas");
schemas.setNsURI("http://example.com/api/schemas");

EClass userClass = EcoreFactory.eINSTANCE.createEClass();
userClass.setName("User");
// ... add attributes

schemas.getEClassifiers().add(userClass);

Components components = OpenApiFactory.eINSTANCE.createComponents();
components.setSchemas(schemas);
openApi.setComponents(components);

// Save
Resource resource = new OpenApiResourceImpl(
    URI.createFileURI("output.json"));
resource.getContents().add(openApi);
resource.save(null);
```

### 12.7 Roundtrip Behavior

Most schemas are preserved through roundtrip:

| Aspect | Preserved | Notes |
|--------|-----------|-------|
| Class names | ✅ | Exact match |
| Property names | ✅ | Exact match |
| Property types | ✅ | Mapped to closest JSON Schema type |
| Required fields | ✅ | Via `lowerBound >= 1` |
| Enumerations | ✅ | Full literal preservation |
| References | ✅ | Via `$ref` |
| Descriptions | ✅ | Via GenModel annotations |

**Artificial Schemas:** Some schemas are marked as "artificial" during conversion (e.g., inline object definitions). These are expanded inline during serialization and not recreated as top-level schemas.

### 12.8 Real-World Test Results

| File | Size | Schemas | After Roundtrip | Preservation |
|------|------|---------|-----------------|--------------|
| petstore.json | 46 KB | 8 | 8 | 100% |
| bike.json | ~100 KB | 54 | 50 | 93% |
| sevdesk.json | 678 KB | 234 | 199 | 85% |
| kubernetes-api.json | 1.9 MB | 286 | 286 | 100% |

### 12.9 Limitations

1. **Swagger 2.0**: Only OpenAPI 3.x is supported. Swagger 2.0 uses `definitions` instead of `components/schemas` and has different structure.

2. **Schema Loss**: Artificial/inline schemas are expanded and not preserved as named schemas after roundtrip.

---

[Next: Scenarios →](18-scenarios.md)
