# Format Abstraction and Custom Parsers/Generators

[← Annotation Reference](16-annotation-reference.md) | [Next: Scenarios →](18-scenarios.md)

---

> **See also:**
> - [Custom Values](14-custom-values.md) for value reader/writer interfaces and registration
> - [Error Handling](15-error-handling.md) for diagnostic reporting from custom formats

---

This chapter defines how the codec supports multiple serialization formats beyond JSON, including BSON (MongoDB), CSV, query strings, and custom protocols.

## 1. Overview

The codec is designed to be **format-agnostic** at its core, with format-specific adapters for different backends:

| Format | Backend | Use Cases |
|--------|---------|-----------|
| JSON | Jackson native | REST APIs, file storage, configuration |
| BSON | MongoDB driver | MongoDB persistence |
| CSV/Query String | Custom parser | HTTP query parameters, simple key-value |
| EcoWitt | Custom parser | Weather station device protocol |
| JSON Schema | Jackson | Schema generation/parsing |

**Design Goal:** The serialization/deserialization logic (type handling, ID strategies, references) should be implemented once and work across all formats.

---

## 2. Architecture

### 2.1 Layered Design

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Application Layer                                │
│         (EMF Resource, load/save operations)                        │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────┐
│                     Codec Logic Layer                                │
│    (Serialization/Deserialization Entries, Type/ID/Reference)       │
│                                                                      │
│  - TypeSerializationEntry / TypeDeserializationEntry                │
│  - IdSerializationEntry / IdDeserializationEntry                    │
│  - ReferenceSerializationEntry / ReferenceDeserializationEntry      │
│  - AttributeSerializationEntry / AttributeDeserializationEntry      │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────┐
│                   Value Transformation Layer                         │
│              (CodecValueReader / CodecValueWriter)                   │
│                                                                      │
│  - Format-agnostic value transformation                             │
│  - Date formatting, Base64 encoding, custom ID schemes              │
│  - Pluggable via configuration                                      │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────┐
│                   Stream Abstraction Layer                           │
│              (CodecStreamReader / CodecStreamWriter)                 │
│                                                                      │
│  - Abstract token-based read/write operations                       │
│  - Hides format-specific details                                    │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
         ┌──────────────────────┼──────────────────────┐
         │                      │                      │
         ▼                      ▼                      ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  JSON Adapter   │  │  BSON Adapter   │  │  CSV Adapter    │
│  (Jackson)      │  │  (MongoDB)      │  │  (Query String) │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

### 2.2 Token Model

All formats are abstracted to a common token stream model:

```java
public enum CodecToken {
    // Structure tokens
    START_OBJECT,
    END_OBJECT,
    START_ARRAY,
    END_ARRAY,

    // Content tokens
    PROPERTY_NAME,

    // Value tokens
    VALUE_STRING,
    VALUE_NUMBER_INT,
    VALUE_NUMBER_FLOAT,
    VALUE_TRUE,
    VALUE_FALSE,
    VALUE_NULL,
    VALUE_BINARY,        // For formats with native binary support (BSON)
    VALUE_EMBEDDED,      // For format-specific embedded types

    // Special
    NOT_AVAILABLE
}
```

---

## 3. Stream Abstraction Interfaces

### 3.1 CodecStreamReader

Abstract interface for reading from any format:

```java
public interface CodecStreamReader extends Closeable {

    // Token navigation
    CodecToken currentToken();
    CodecToken nextToken();

    // Property access
    String currentName();

    // Value access - primitives
    String getString();
    int getIntValue();
    long getLongValue();
    double getDoubleValue();
    boolean getBooleanValue();

    // Value access - complex
    byte[] getBinaryValue();
    BigDecimal getDecimalValue();
    BigInteger getBigIntegerValue();

    // Format-specific value access
    <T> T getNativeValue(Class<T> type);

    // Location for diagnostics
    CodecLocation currentLocation();

    // Structure navigation
    void skipChildren();

    // Context
    int getCurrentDepth();
    boolean isInArray();
    boolean isInObject();
}
```

### 3.2 CodecStreamWriter

Abstract interface for writing to any format:

```java
public interface CodecStreamWriter extends Closeable, Flushable {

    // Structure
    void writeStartObject();
    void writeEndObject();
    void writeStartArray();
    void writeEndArray();

    // Property name
    void writePropertyName(String name);

    // Values - primitives
    void writeString(String value);
    void writeNumber(int value);
    void writeNumber(long value);
    void writeNumber(double value);
    void writeNumber(BigDecimal value);
    void writeBoolean(boolean value);
    void writeNull();

    // Values - complex
    void writeBinary(byte[] data);

    // Format-specific value writing
    <T> void writeNativeValue(T value);

    // Raw output (for pre-formatted content)
    void writeRaw(String raw);
}
```

### 3.3 CodecLocation

Location information for diagnostics:

```java
public interface CodecLocation {
    long getCharOffset();    // -1 if not available
    int getLineNr();         // -1 if not available
    int getColumnNr();       // -1 if not available
    String getSourceRef();   // Resource URI or input description
}
```

---

## 4. Value Reader/Writer Abstraction

### 4.1 Current Design (Jackson-coupled)

The current interfaces are tightly coupled to Jackson:

```java
// Current - Jackson specific
public interface CodecValueReader<T, F extends EStructuralFeature> {
    T read(JsonParser parser, F feature, DeserializationContext ctxt) throws IOException;
}

public interface CodecValueWriter<T, F extends EStructuralFeature> {
    void write(T value, F feature, JsonGenerator gen, SerializationContext ctxt) throws IOException;
}
```

### 4.2 Current Design (Context-based)

> **See also:** [Custom Values](14-custom-values.md) for the complete interface definitions including `getName()` and context APIs.

The current interfaces provide context wrappers that include configuration access:

```java
// Current design - see 14-custom-values.md for complete definitions
public interface CodecValueReader<T, F extends EStructuralFeature> {
    String getName();  // For registry registration
    T read(CodecReaderContext ctx, F feature) throws IOException;
}

public interface CodecValueWriter<T, F extends EStructuralFeature> {
    String getName();  // For registry registration
    void write(T value, F feature, CodecWriterContext ctx) throws IOException;
}
```

### 4.3 Context Interfaces

The context interfaces provide access to the underlying parser/generator, configuration, and diagnostics:

```java
public interface CodecReaderContext {
    JsonParser getParser();                    // Jackson parser
    DeserializationContext getJacksonContext(); // Jackson context
    EffectiveCodecConfig getConfig();          // Resolved configuration
    DiagnosticCollector getDiagnostics();      // Error/warning collector
    void addWarning(String message);           // Convenience method
    void addError(String message);             // Convenience method
}

public interface CodecWriterContext {
    JsonGenerator getGenerator();              // Jackson generator
    SerializationContext getJacksonContext();  // Jackson context
    EffectiveCodecConfig getConfig();          // Resolved configuration
    DiagnosticCollector getDiagnostics();      // Error/warning collector
    void addWarning(String message);           // Convenience method
    void addError(String message);             // Convenience method
}
```

> **See also:** [Error Handling](15-error-handling.md) for diagnostic reporting from custom readers/writers.

### 4.4 Future: Format-Agnostic Stream Abstraction

For non-Jackson formats (BSON, CSV), stream adapters can wrap the context:

```java
public class JacksonStreamReader implements CodecStreamReader {
    private final JsonParser parser;

    public JacksonStreamReader(JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public CodecToken currentToken() {
        return mapToken(parser.currentToken());
    }

    @Override
    public String getString() {
        return parser.getString();
    }

    // ... delegate all methods to JsonParser
}

public class JacksonStreamWriter implements CodecStreamWriter {
    private final JsonGenerator generator;

    // ... delegate all methods to JsonGenerator
}
```

---

## 5. Format Adapters

### 5.1 JSON Adapter (Jackson Native)

The default adapter using Jackson's built-in JSON support:

```java
public class JsonCodecAdapter implements CodecFormatAdapter {

    @Override
    public CodecStreamReader createReader(InputStream input, CodecReadContext ctxt) {
        JsonFactory factory = ctxt.getJsonFactory();
        JsonParser parser = factory.createParser(input);
        return new JacksonStreamReader(parser);
    }

    @Override
    public CodecStreamWriter createWriter(OutputStream output, CodecWriteContext ctxt) {
        JsonFactory factory = ctxt.getJsonFactory();
        JsonGenerator generator = factory.createGenerator(output);
        return new JacksonStreamWriter(generator);
    }
}
```

### 5.2 BSON Adapter (MongoDB)

Adapter wrapping MongoDB's BSON reader/writer:

```java
public class BsonCodecAdapter implements CodecFormatAdapter {

    @Override
    public CodecStreamReader createReader(InputStream input, CodecReadContext ctxt) {
        BsonReader bsonReader = new BsonBinaryReader(ByteBuffer.wrap(readBytes(input)));
        return new BsonStreamReader(bsonReader);
    }

    @Override
    public CodecStreamWriter createWriter(OutputStream output, CodecWriteContext ctxt) {
        BsonWriter bsonWriter = new BsonBinaryWriter(output);
        return new BsonStreamWriter(bsonWriter);
    }
}

public class BsonStreamReader implements CodecStreamReader {
    private final BsonReader reader;

    @Override
    public <T> T getNativeValue(Class<T> type) {
        // Support BSON-specific types
        if (type == ObjectId.class) {
            return type.cast(reader.readObjectId());
        }
        if (type == Decimal128.class) {
            return type.cast(reader.readDecimal128());
        }
        return null;
    }

    // ... map BSON types to CodecToken
}
```

### 5.3 Query String Adapter (CSV/EcoWitt)

Adapter for simple key-value formats:

```java
public class QueryStringCodecAdapter implements CodecFormatAdapter {

    @Override
    public CodecStreamReader createReader(InputStream input, CodecReadContext ctxt) {
        Map<String, String> data = QueryStringParser.parse(input);
        return new MapStreamReader(data);
    }

    @Override
    public CodecStreamWriter createWriter(OutputStream output, CodecWriteContext ctxt) {
        return new QueryStringStreamWriter(output);
    }
}

public class MapStreamReader implements CodecStreamReader {
    private final Map<String, String> data;
    private final Iterator<Map.Entry<String, String>> iterator;
    private State state = State.BEGIN;

    // Simulates object structure from flat map:
    // START_OBJECT -> (PROPERTY_NAME, VALUE_STRING)* -> END_OBJECT
}
```

---

## 6. Format-Specific Features

### 6.1 Native Type Support

Some formats have native types that don't exist in JSON:

| Format | Native Types | Handling |
|--------|--------------|----------|
| BSON | ObjectId, Decimal128, BsonBinary, DateTime | `getNativeValue(Class)` / `writeNativeValue(Object)` |
| JSON | - | All values as JSON primitives |
| CSV | - | All values as strings |

**Native date-time (built-in).** The format delegate declares a native date-time
type via `supportsNativeDateTime()` (default `false`) and writes it via
`writeDateTime(long epochMillis)` — mirroring the `supportsNativeObjectId()` /
`writeObjectId(Object)` escape hatch. `java.util.Date` attribute values without a
configured `dateFormat` are written natively when the delegate supports it (BSON:
`BsonDateTime`); a configured `dateFormat` always wins and keeps the string form.
On read, a native date-time surfaces as `VALUE_NUMBER_INT` carrying epoch
milliseconds, which the attribute deserialization converts back to `Date` —
no dedicated token type is needed. Formats without native support are unchanged:
they keep the `dateFormat` string or the legacy `toString()` fallback.

### 6.2 Custom Value Writers for Format-Specific Types

```java
// MongoDB ObjectId writer
public class ObjectIdValueWriter implements CodecValueWriter<String, EAttribute> {

    @Override
    public void write(String value, EAttribute attr, CodecStreamWriter writer, CodecWriteContext ctxt) {
        if (writer instanceof BsonStreamWriter bsonWriter) {
            // Use native ObjectId
            bsonWriter.writeNativeValue(new ObjectId(value));
        } else {
            // Fall back to string representation
            writer.writeString(value);
        }
    }
}

// MongoDB Decimal128 reader
public class Decimal128ValueReader implements CodecValueReader<BigDecimal, EAttribute> {

    @Override
    public BigDecimal read(CodecStreamReader reader, EAttribute attr, CodecReadContext ctxt) {
        Decimal128 native = reader.getNativeValue(Decimal128.class);
        if (native != null) {
            return native.bigDecimalValue();
        }
        // Fall back to string parsing
        return new BigDecimal(reader.getString());
    }
}
```

### 6.3 Binary Data Handling

```java
// Base64 encoding for JSON, native binary for BSON
public class BinaryValueWriter implements CodecValueWriter<byte[], EAttribute> {

    @Override
    public void write(byte[] value, EAttribute attr, CodecStreamWriter writer, CodecWriteContext ctxt) {
        if (writer.supportsNativeBinary()) {
            writer.writeBinary(value);
        } else {
            // Encode as Base64 string
            writer.writeString(Base64.getEncoder().encodeToString(value));
        }
    }
}
```

---

## 7. Configuration

### 7.1 Format Selection

The format is typically determined by:

1. **File extension**: `.json`, `.bson`, `.csv`
2. **Content type**: `application/json`, `application/bson`
3. **Explicit option**: `CODEC_FORMAT` option in load/save options

```java
Map<String, Object> options = new HashMap<>();
options.put(CodecOptions.CODEC_FORMAT, "bson");
resource.save(outputStream, options);
```

### 7.2 Format Adapter Registry

```java
public interface CodecFormatRegistry {
    void registerAdapter(String format, CodecFormatAdapter adapter);
    CodecFormatAdapter getAdapter(String format);
    CodecFormatAdapter getAdapterForExtension(String extension);
    CodecFormatAdapter getAdapterForContentType(String contentType);
}
```

Default registrations:

| Format ID | Extensions | Content Types |
|-----------|------------|---------------|
| `json` | `.json` | `application/json`, `text/json` |
| `bson` | `.bson` | `application/bson` |
| `csv` | `.csv` | `text/csv` |
| `querystring` | - | `application/x-www-form-urlencoded` |

---

## 8. Migration Path

### 8.1 Phase 1: Introduce Abstractions (Non-Breaking)

1. Add `CodecStreamReader`, `CodecStreamWriter`, `CodecToken` interfaces
2. Add `JacksonStreamReader`, `JacksonStreamWriter` adapters
3. Keep existing `CodecValueReader`/`CodecValueWriter` with Jackson types
4. Add overloaded methods accepting stream abstractions

### 8.2 Phase 2: Update Entry Classes

1. Refactor `*SerializationEntry` to use `CodecStreamWriter`
2. Refactor `*DeserializationEntry` to use `CodecStreamReader`
3. Update custom value readers/writers to use abstractions

### 8.3 Phase 3: Add Format Adapters

1. Implement `BsonCodecAdapter`
2. Implement `QueryStringCodecAdapter`
3. Integrate with existing V1 parser/generator infrastructure

---

## 9. Example: Multi-Format Value Reader

A value reader that works with the current Jackson-based implementation:

```java
public class ISO8601DateReader implements CodecValueReader<Date, EAttribute> {

    private static final SimpleDateFormat FORMAT =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public String getName() {
        return "isoDate";
    }

    @Override
    public Date read(CodecReaderContext ctx, EAttribute attr) throws IOException {
        JsonParser parser = ctx.getParser();
        JsonToken token = parser.currentToken();

        switch (token) {
            case VALUE_STRING:
                // Parse ISO8601 string
                try {
                    return FORMAT.parse(parser.getText());
                } catch (ParseException e) {
                    ctx.addError("Invalid ISO8601 date: " + parser.getText());
                    return null;
                }

            case VALUE_NUMBER_INT:
                // Unix timestamp
                return new Date(parser.getLongValue());

            default:
                ctx.addWarning("Unexpected token for date: " + token);
                return null;
        }
    }
}
```

**Future:** When stream abstraction is implemented, the same reader can work across formats by using `CodecStreamReader` instead of `JsonParser`.

---

## 10. Open Questions

1. **Streaming vs Buffering**: Should the abstraction support streaming for large documents, or is buffering acceptable?

2. **Schema Information**: Should the stream abstraction carry schema/type hints for formats that support them?

3. **Pretty Printing**: How to handle format-specific pretty printing options?

4. **Error Recovery**: Should the abstraction support error recovery / partial parsing?

5. **Async Support**: Should we consider async/reactive stream interfaces for future scalability?

---

## 11. Format Extension Projects

The following projects provide pre-configured resources for specific formats:

| Project | Format | Base Class | Description |
|---------|--------|------------|-------------|
| `org.eclipse.fennec.codec.geojson` | GeoJSON | `CodecResource` | Pre-configured for GeoJSON with `type` key, NAME strategy |
| `org.eclipse.fennec.codec.jsonschema` | JSON Schema | `ResourceImpl` | Meta-format: JSON Schema ↔ EPackage conversion |

**Note:** Most format extensions extend `CodecResource` for standard EObject serialization. JSON Schema is special because it's a **meta-format** that converts the schema itself (EPackage), not instances.

### 11.1 GeoJSON Extension

**Project:** `org.eclipse.fennec.codec.geojson`

Pre-configured `CodecResource` for [GeoJSON](https://geojson.org/) format:

```java
// Configuration applied automatically
CodecConfiguration.builder()
    .typeKey("type")                          // GeoJSON uses "type" not "_type"
    .typeStrategy(TypeStrategy.NAME)          // Simple names: Point, Feature, etc.
    .useNamesFromExtendedMetaData(true)       // Maps "coordinates" correctly
    .forceSerialize("data", "bbox")           // Volatile attributes
    .idKeyMode(IdKeyMode.NONE)                // Feature.id is a regular property
    .serializeType(true)
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

### 11.2 JSON Schema Extension

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

#### 11.2.1 Standalone Mode

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

#### 11.2.2 Embedded Mode

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

#### 11.2.3 JSON Schema Features

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

#### 11.2.4 Example: OpenAPI Integration

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

### 11.3 Creating Custom Format Extensions

To create a custom format extension:

1. **Extend `CodecResource`** with format-specific configuration:

```java
public class MyFormatResourceImpl extends CodecResource {

    public static final CodecConfiguration MY_FORMAT_CONFIG = CodecConfiguration.builder()
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

## 12. JSON Schema Version Support and Feature Coverage

This section provides a comprehensive reference for JSON Schema support in the codec.

### 12.1 Supported JSON Schema Versions

The JSON Schema converter supports multiple draft versions:

| Draft Version | `$schema` URI | Definitions Key | Status |
|---------------|---------------|-----------------|--------|
| Draft-04 | `http://json-schema.org/draft-04/schema#` | `definitions` | ✅ Supported |
| Draft-06 | `http://json-schema.org/draft-06/schema#` | `definitions` | ✅ Supported |
| Draft-07 | `http://json-schema.org/draft-07/schema#` | `definitions` | ✅ Supported (Primary) |
| Draft 2019-09 | `https://json-schema.org/draft/2019-09/schema` | `$defs` | ✅ Supported |
| Draft 2020-12 | `https://json-schema.org/draft/2020-12/schema` | `$defs` | ✅ Supported |

**Note:** The converter auto-detects the definitions key (`definitions` vs `$defs`) or uses the explicitly specified `OPTION_SCHEMA_FEATURE`.

### 12.2 Complete Feature Matrix

#### 12.2.1 Core Keywords

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

#### 12.2.2 Type Keywords

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

#### 12.2.3 Object Keywords

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

#### 12.2.4 Array Keywords

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

#### 12.2.5 Composition Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `allOf` | `ESuperTypes` | ✅ | ✅ | Inheritance hierarchy |
| `anyOf` | Abstract + subtypes | ✅ | ✅ | Creates parent with common props |
| `oneOf` | Abstract + subtypes | ✅ | ✅ | Discriminated union or variants |
| `not` | - | ❌ | ❌ | Not mappable to EMF |
| `if` / `then` / `else` | - | ❌ | ❌ | Conditional schemas not mappable |

#### 12.2.6 String Validation Keywords

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

#### 12.2.7 Numeric Validation Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `minimum` | EAnnotation | ✅ | ✅ | |
| `maximum` | EAnnotation | ✅ | ✅ | |
| `exclusiveMinimum` | EAnnotation | ✅ | ✅ | |
| `exclusiveMaximum` | EAnnotation | ✅ | ✅ | |
| `multipleOf` | EAnnotation | ✅ | ✅ | |

#### 12.2.8 Annotation Keywords

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

#### 12.2.9 Content Keywords

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `contentEncoding` | EAnnotation | ✅ | ✅ | e.g., "base64" |
| `contentMediaType` | EAnnotation | ✅ | ✅ | e.g., "image/png" |
| `contentSchema` | - | ❌ | ❌ | Complex, not mappable |

#### 12.2.10 Enum and Const

| Keyword | EMF Mapping | Read | Write | Notes |
|---------|-------------|:----:|:-----:|-------|
| `enum` | `EEnum` | ✅ | ✅ | String enums become EEnum |
| `const` | EAnnotation | ✅ | ✅ | Fixed value preserved |

### 12.3 Feature Legend

| Symbol | Meaning |
|--------|---------|
| ✅ | Fully supported |
| ⚠️ | Partially supported (preserved as annotation, may not round-trip perfectly) |
| ❌ | Not supported |

### 12.4 EMF Limitations

The following JSON Schema features have **no natural EMF equivalent** and cannot be represented:

1. **Conditional Schemas** (`if`/`then`/`else`): EMF has no conditional feature mechanism
2. **Negation** (`not`): EMF cannot express "not this type"
3. **Tuple Validation** (`prefixItems`): EMF arrays are homogeneous
4. **Property Names Validation** (`propertyNames`): EMF features have fixed names
5. **Contains Constraints** (`contains`, `minContains`, `maxContains`): EMF has no "at least one matching" constraint
6. **Dependent Constraints** (`dependentRequired`, `dependentSchemas`): No EMF equivalent
7. **Dynamic References** (`$dynamicRef`, `$dynamicAnchor`): Complex recursive patterns
8. **Content Schema** (`contentSchema`): Complex embedded schema for content validation

### 12.5 Annotations Source

All JSON Schema metadata is preserved in EMF EAnnotations with these sources:

| Source | Purpose |
|--------|---------|
| `http://fennec.eclipse.org/jsonschema` | JSON Schema-specific metadata |
| `http://www.eclipse.org/emf/2002/GenModel` | Documentation (description) |
| `http:///org/eclipse/emf/ecore/util/ExtendedMetaData` | Original names |

### 12.6 Special Patterns

#### 12.6.1 Discriminated Unions

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

#### 12.6.2 Context-Specific Variants (oneOf without discriminator)

When `oneOf` has multiple complete schemas with overlapping properties:
- Creates abstract base class with `commonBase=true` annotation
- Extracts common properties to base class
- Creates variant subclasses with `variant=<title>` annotation

#### 12.6.3 Namespace Paths

Nested definition structures like `definitions/configs/kafka` are handled:
- Intermediate nodes without schema keywords are organizational namespaces
- EClassifiers get `namespacePath` annotation (e.g., `configs`)
- `$ref` paths resolve correctly across namespaces

### 12.7 Diagnostic Warnings

> **See also:** [Error Handling](15-error-handling.md) for the general codec diagnostics mechanism.

The JSON Schema converter reports issues through EMF's standard diagnostics mechanism. After loading a schema, check `resource.getWarnings()` for any conversion warnings.

#### 12.7.1 Accessing Diagnostics

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

#### 12.7.2 Diagnostic Codes

| Code | Description | Example Keywords |
|------|-------------|------------------|
| `UNSUPPORTED_FEATURE` | Keyword cannot be mapped to EMF | `not`, `if`/`then`/`else`, `prefixItems`, `contains` |
| `PARTIAL_SUPPORT` | Keyword preserved as annotation but no semantic EMF equivalent | `patternProperties`, `$comment` |
| `COMPLEX_ANYOF` | Complex `anyOf` with different schemas detected | - |
| `UNRESOLVED_REFERENCE` | A `$ref` could not be resolved | - |

#### 12.7.3 Warning Messages

| Condition | Warning Message |
|-----------|-----------------|
| Unsupported keyword | "Unsupported JSON Schema keyword '{keyword}' - cannot be mapped to EMF" |
| Partially supported keyword | "Keyword '{keyword}' is partially supported: {detail}" |
| Complex `anyOf` | "Complex anyOf with different schemas detected. May require manual modeling." |
| Unresolved `$ref` | "Could not resolve reference: {path}" |

#### 12.7.4 Helper Class: JsonSchemaKeywords

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

### 12.8 Round-Trip Fidelity

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

### 12.9 Schema Reference Methods: `$anchor` vs JSON Pointer

JSON Schema supports two methods for referencing definitions within a schema:

#### 12.9.1 JSON Pointer References (Default)

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

#### 12.9.2 Anchor-Based References

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

#### 12.9.3 Serialization Options

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

#### 12.9.4 Round-Trip Behavior

| Input Schema | Output without option | Output with `OPTION_USE_ANCHOR_REFS` |
|--------------|----------------------|--------------------------------------|
| JSON Pointer refs | JSON Pointer refs | Anchor refs (anchors generated) |
| Anchor refs | Anchor refs (preserved) | Anchor refs (preserved) |
| Mixed | Mixed (preserved) | Anchor refs where possible |

**Note:** Existing `$anchor` annotations from the input schema are always preserved, regardless of the option setting.

#### 12.9.5 Per-Class Override

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

## 13. Working with Generated EPackages

Once you've converted a JSON Schema to an EPackage, you can use it for deserializing JSON data that conforms to the schema.

### 13.1 Example: Simple Schema

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

### 13.2 Important: EPackage Registration

When working with dynamically generated EPackages, you must register them in:

1. **ResourceSet's package registry** - so EMF can find the EPackage by URI:
   ```java
   resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
   ```

2. **MetadataService** (for codec v2) - so codec can generate metadata:
   ```java
   metadataService.registerPackage(ePackage);
   ```

### 13.3 Limitations with oneOf / Union Types

JSON Schema's `oneOf` construct creates challenges for deserialization:

**The Problem:**
- When converting `oneOf` to EMF, an abstract EClass is typically generated with concrete variant subclasses
- During deserialization, the codec needs to determine which concrete subclass to instantiate
- JSON data doesn't always contain explicit type discriminators

**Current Workaround:**

Provide explicit type mapping via load options:

```java
Map<String, Object> options = CodecOptionsBuilder.create()
    .rootObject(rootEClass)
    .forClass(inputNodeClass)
        .typeKey("_type")
        .typeStrategy("NAME")
        .typeMap(Map.of(
            "kafka", "KafkaInputNode",
            "file", "FileInputNode"
        ))
    .build();
```

**Known Limitation:**

Type mapping based solely on property name presence (discriminating based on which property is set) is not currently supported. You must either:
1. Add explicit type discriminator fields to your JSON data
2. Pre-determine the type through other means and specify it in options

---

## 14. Future Work

The following features are planned but not yet implemented. See the linked documents for implementation details.

### 14.1 Tuple Validation (`prefixItems`)

JSON Schema's `prefixItems` keyword for arrays with typed positional elements (tuples).

**Status:** Planned

**Details:** [todo/jsonschema-prefixItems-implementation.md](todo/jsonschema-prefixItems-implementation.md)

### 14.2 Format-to-EDataType Mapping

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

## 15. OpenAPI Integration Example

This section demonstrates a complete real-world integration: embedding JSON Schema handling within OpenAPI documents using the value reader/writer pattern.

### 15.1 Overview

OpenAPI 3.x documents embed JSON Schema definitions in `components/schemas`. The Fennec codec handles this by:

1. **Reading**: Converting JSON Schema objects to EMF `EPackage` with `EClass` definitions
2. **Writing**: Converting `EPackage` back to JSON Schema format

This is implemented using the `ReferenceValueReader/Writer` pattern described in [Custom Values](14-custom-values.md).

### 15.2 Architecture

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

### 15.3 Model Definition

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

### 15.4 Resource Configuration

#### 15.4.1 OpenApiResourceImpl

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

#### 15.4.2 Factory Registration

```java
public class OpenApiResourceFactoryImpl implements Resource.Factory {

    @Override
    public Resource createResource(URI uri) {
        return new OpenApiResourceImpl(uri);
    }
}
```

### 15.5 Value Reader/Writer Implementation

#### 15.5.1 EPackageValueReader

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

#### 15.5.2 EPackageValueWriter

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

### 15.6 Usage Example

#### 15.6.1 Loading an OpenAPI Document

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

#### 15.6.2 Creating and Saving

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

### 15.7 Roundtrip Behavior

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

### 15.8 Real-World Test Results

| File | Size | Schemas | After Roundtrip | Preservation |
|------|------|---------|-----------------|--------------|
| petstore.json | 46 KB | 8 | 8 | 100% |
| bike.json | ~100 KB | 54 | 50 | 93% |
| sevdesk.json | 678 KB | 234 | 199 | 85% |
| kubernetes-api.json | 1.9 MB | 286 | 286 | 100% |

### 15.9 Limitations

1. **Swagger 2.0**: Only OpenAPI 3.x is supported. Swagger 2.0 uses `definitions` instead of `components/schemas` and has different structure.

2. **Schema Loss**: Artificial/inline schemas are expanded and not preserved as named schemas after roundtrip.

---

[Next: Scenarios →](18-scenarios.md)
