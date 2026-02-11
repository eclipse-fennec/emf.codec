# Custom Value Readers/Writers

[← Load/Save Options](13-load-save-options.md) | [Next: Error Handling →](15-error-handling.md)

---

> **See also:**
> - [Annotation Reference](16-annotation-reference.md) (Feature Configuration) for `valueReaderName` and `valueWriterName` keys
> - [Load/Save Options](13-load-save-options.md) for `CODEC_FEATURE_VALUE_READERS` and `CODEC_FEATURE_VALUE_WRITERS`

---

Extensibility hooks for custom serialization logic.

## 1. Overview

Custom value readers/writers allow you to:
- Transform values during serialization/deserialization
- Handle special data types (dates, binary, custom formats)
- Implement domain-specific encoding
- Customize reference URI formats
- Convert embedded formats (e.g., JSON Schema to EPackage)

### 1.1 Interface Hierarchy

The system provides a **type-safe interface hierarchy**:

```
CodecValueReader<T, F extends EStructuralFeature>
├── AttributeValueReader<T>      extends CodecValueReader<T, EAttribute>
└── ReferenceValueReader<T>      extends CodecValueReader<T, EReference>
                                 where T extends EObject

CodecValueWriter<T, F extends EStructuralFeature>
├── AttributeValueWriter<T>      extends CodecValueWriter<T, EAttribute>
└── ReferenceValueWriter<T>      extends CodecValueWriter<T, EReference>
                                 where T extends EObject
```

### 1.2 Use Cases by Interface

| Interface | Use Case | Example |
|-----------|----------|---------|
| `AttributeValueReader<T>` | Transform primitive/data type values | ISO 8601 date parsing |
| `AttributeValueWriter<T>` | Format primitive/data type values | Base64 encoding |
| `ReferenceValueReader<T>` | Read containment references in custom format | JSON Schema → EPackage |
| `ReferenceValueWriter<T>` | Write containment references in custom format | EPackage → JSON Schema |
| `CodecValueReader<String, EReference>` | Transform non-containment reference URIs | MongoDB ObjectId → EMF URI |
| `CodecValueWriter<EObject, EReference>` | Write non-containment reference URIs | Custom URI scheme |

### 1.3 Core Components

| Component | Purpose |
|-----------|---------|
| `CodecValueWriter<T, F>` | Base interface for custom serialization |
| `CodecValueReader<T, F>` | Base interface for custom deserialization |
| `AttributeValueWriter<T>` | Specialized for EAttribute with `canHandle()` |
| `AttributeValueReader<T>` | Specialized for EAttribute with `canHandle()` |
| `ReferenceValueWriter<T>` | Specialized for containment EReference with `canHandle()` |
| `ReferenceValueReader<T>` | Specialized for containment EReference with `canHandle()` |
| `CodecValueRegistry` | Registry to store named readers/writers |

---

## 2. Interface Architecture

### 2.1 Base Interfaces

```java
public interface CodecValueReader<T, F extends EStructuralFeature> {
    /**
     * Returns the unique name for this reader.
     * Used for auto-registration in CodecValueRegistry.
     */
    String getName();

    /**
     * Reads a value from the parser context.
     */
    T read(CodecReaderContext ctx, F feature) throws IOException;
}

public interface CodecValueWriter<T, F extends EStructuralFeature> {
    /**
     * Returns the unique name for this writer.
     * Used for auto-registration in CodecValueRegistry.
     */
    String getName();

    /**
     * Writes a value to the generator context.
     */
    void write(T value, F feature, CodecWriterContext ctx) throws IOException;
}
```

### 2.2 Context Interfaces

The context interfaces provide access to the codec configuration, Jackson contexts, and diagnostics:

```java
public interface CodecReaderContext {
    /** The Jackson parser for reading JSON tokens */
    JsonParser getParser();

    /** The Jackson deserialization context */
    DeserializationContext getJacksonContext();

    /** The effective codec configuration (merged from all sources) */
    EffectiveCodecConfig getConfig();

    /** Collector for warnings and errors */
    DiagnosticCollector getDiagnostics();

    /** Convenience: add a warning diagnostic */
    void addWarning(String message);

    /** Convenience: add an error diagnostic */
    void addError(String message);
}

public interface CodecWriterContext {
    /** The Jackson generator for writing JSON tokens */
    JsonGenerator getGenerator();

    /** The Jackson serialization context */
    SerializationContext getJacksonContext();

    /** The effective codec configuration (merged from all sources) */
    EffectiveCodecConfig getConfig();

    /** Collector for warnings and errors */
    DiagnosticCollector getDiagnostics();

    /** Convenience: add a warning diagnostic */
    void addWarning(String message);

    /** Convenience: add an error diagnostic */
    void addError(String message);
}
```

**Why provide EffectiveCodecConfig?**

Custom readers/writers often need configuration context:
- A date writer might check a global date format setting
- A reference writer might need to know if smart compression is enabled
- A custom type writer might need the configured `typeStrategy`

**Why provide DiagnosticCollector?**

Custom readers/writers should report problems through the standard diagnostic mechanism:
- Missing required fields → warning or error
- Invalid format → warning with fallback or error
- Deprecated format detected → warning

### 2.3 Specialized Attribute Interfaces

```java
public interface AttributeValueReader<T> extends CodecValueReader<T, EAttribute> {
    /**
     * Checks if this reader can handle the given attribute.
     */
    boolean canHandle(EAttribute attribute);
}

public interface AttributeValueWriter<T> extends CodecValueWriter<T, EAttribute> {
    /**
     * Checks if this writer can handle the given attribute.
     */
    boolean canHandle(EAttribute attribute);
}
```

### 2.4 Specialized Reference Interfaces

```java
public interface ReferenceValueReader<T extends EObject>
        extends CodecValueReader<T, EReference> {
    /**
     * Checks if this reader can handle the given reference.
     * Typically checks if the reference type is compatible.
     */
    boolean canHandle(EReference reference);
}

public interface ReferenceValueWriter<T extends EObject>
        extends CodecValueWriter<T, EReference> {
    /**
     * Checks if this writer can handle the given reference.
     * Typically checks if the reference type is compatible.
     */
    boolean canHandle(EReference reference);
}
```

### 2.5 The `canHandle()` Method

The `canHandle()` method enables **type-safe validation** at construction time:

```java
public class EPackageValueReader implements ReferenceValueReader<EPackage> {

    @Override
    public String getName() {
        return "jsonSchemaToEPackage";
    }

    @Override
    public boolean canHandle(EReference reference) {
        // Only handle references of type EPackage
        return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(
            reference.getEReferenceType());
    }

    @Override
    public EPackage read(CodecReaderContext ctx, EReference ref) throws IOException {
        // Convert JSON Schema to EPackage
        // Access config if needed: ctx.getConfig().getTypeStrategy()
        TreeNode tree = ctx.getParser().readValueAsTree();
        return converter.convert((JsonNode) tree, null);
    }
}
```

Benefits:
- **Early validation**: Incompatible readers/writers are rejected at construction
- **Clear error messages**: Log warnings when reader/writer cannot handle a feature
- **Type safety**: Prevents runtime ClassCastException

---

## 3. Attribute Value Readers/Writers

### 3.1 Purpose

Transform attribute values during serialization/deserialization:
- Custom date/time formats
- Binary encoding (Base64, hex)
- Domain-specific value transformations

### 3.2 Example: Date Formatting

```java
public class ISODateReader implements AttributeValueReader<Date> {
    private final SimpleDateFormat sdf;

    public ISODateReader() {
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public String getName() {
        return "isoDate";
    }

    @Override
    public boolean canHandle(EAttribute attribute) {
        return attribute.getEAttributeType().getInstanceClass() == Date.class;
    }

    @Override
    public Date read(CodecReaderContext ctx, EAttribute attr) throws IOException {
        try {
            return sdf.parse(ctx.getParser().getString());
        } catch (ParseException e) {
            ctx.addWarning("Invalid date format: " + e.getMessage());
            return null;  // Or throw IOException for strict mode
        }
    }
}

public class ISODateWriter implements AttributeValueWriter<Date> {
    private final SimpleDateFormat sdf;

    public ISODateWriter() {
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public String getName() {
        return "isoDate";
    }

    @Override
    public boolean canHandle(EAttribute attribute) {
        return attribute.getEAttributeType().getInstanceClass() == Date.class;
    }

    @Override
    public void write(Date date, EAttribute attr, CodecWriterContext ctx)
            throws IOException {
        ctx.getGenerator().writeString(sdf.format(date));
    }
}
```

### 3.3 Example: Using EffectiveConfig

Custom readers/writers can access the codec configuration:

```java
public class ConfigAwareDateWriter implements AttributeValueWriter<Date> {
    @Override
    public String getName() {
        return "configAwareDate";
    }

    @Override
    public boolean canHandle(EAttribute attribute) {
        return attribute.getEAttributeType().getInstanceClass() == Date.class;
    }

    @Override
    public void write(Date date, EAttribute attr, CodecWriterContext ctx)
            throws IOException {
        // Access configuration to determine format
        EffectiveCodecConfig config = ctx.getConfig();

        // Example: check a hypothetical dateFormat setting
        String format = config.getDateFormat().orElse("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        ctx.getGenerator().writeString(sdf.format(date));
    }
}
```

### 3.4 Example: Lambda Style with Explicit Name

For simple cases, use the builder with an explicit name:

```java
// Register via builder with explicit name (since lambdas can't implement getName())
CodecConfiguration.builder()
    .valueWriter("base64", (value, attr, ctx) ->
        ctx.getGenerator().writeString(
            Base64.getEncoder().encodeToString((byte[]) value)))
    .valueReader("base64", (ctx, attr) ->
        Base64.getDecoder().decode(ctx.getParser().getString()))
    .build();
```

### 3.4 Serialization Flow

```
AttributeSerializationEntry
    │
    ├─ Lookup customWriter from registry
    │
    ├─ If writer instanceof AttributeValueWriter:
    │       │
    │       ├─ Check canHandle(attribute)
    │       │       │
    │       │       ├─ YES: Use this writer
    │       │       │
    │       │       └─ NO: Log warning, fall back to default
    │
    ├─ If customWriter available:
    │       customWriter.write(value, attribute, gen, ctxt)
    │
    └─ Otherwise: Default serialization
```

---

## 4. Reference Value Readers/Writers

### 4.1 Two Types of Reference Customization

| Type | Interface | Purpose | When Used |
|------|-----------|---------|-----------|
| **Containment** | `ReferenceValueReader<T>` / `ReferenceValueWriter<T>` | Convert entire object structure | JSON Schema ↔ EPackage |
| **Non-Containment URI** | `CodecValueReader<String, EReference>` / `CodecValueWriter<EObject, EReference>` | Transform reference URI | MongoDB ObjectId ↔ EMF URI |

### 4.2 Containment Reference Readers/Writers

Used for **containment references** where embedded content needs custom conversion.

#### Example: JSON Schema to EPackage (OpenAPI)

```java
public class EPackageValueReader implements ReferenceValueReader<EPackage> {
    private final JsonSchemaToEPackageConverter converter;

    public EPackageValueReader(JsonSchemaToEPackageConverter converter) {
        this.converter = converter;
    }

    @Override
    public String getName() {
        return "jsonSchemaToEPackage";
    }

    @Override
    public boolean canHandle(EReference reference) {
        return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(
            reference.getEReferenceType());
    }

    @Override
    public EPackage read(CodecReaderContext ctx, EReference ref) throws IOException {
        TreeNode tree = ctx.getParser().readValueAsTree();
        EPackage result = converter.convert((JsonNode) tree, null);
        if (result == null) {
            ctx.addWarning("Failed to convert JSON Schema to EPackage");
        }
        return result;
    }
}

public class EPackageValueWriter implements ReferenceValueWriter<EPackage> {
    private final EPackageToJsonSchemaConverter converter;

    public EPackageValueWriter(EPackageToJsonSchemaConverter converter) {
        this.converter = converter;
    }

    @Override
    public String getName() {
        return "ePackageToJsonSchema";
    }

    @Override
    public boolean canHandle(EReference reference) {
        return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(
            reference.getEReferenceType());
    }

    @Override
    public void write(EPackage value, EReference ref, CodecWriterContext ctx)
            throws IOException {
        JsonNode schema = converter.convert(value);
        ctx.getGenerator().writePOJO(schema);
    }
}
```

#### Serialization Flow (Containment)

```
ReferenceSerializationEntry (containment)
    │
    ├─ Lookup customWriter from registry
    │
    ├─ If writer instanceof ReferenceValueWriter:
    │       │
    │       ├─ Check canHandle(reference)
    │       │       │
    │       │       ├─ YES: containmentWriter.write(target, ref, gen, ctxt)
    │       │       │
    │       │       └─ NO: Log warning, use default serialization
    │
    └─ Default: ctxt.writeValue(gen, target)
```

### 4.3 Non-Containment Reference URI Customization

Used for **non-containment references** to transform URI format.

#### Example: MongoDB ObjectId

```java
public class MongoIdWriter implements CodecValueWriter<EObject, EReference> {
    @Override
    public String getName() {
        return "mongoId";
    }

    @Override
    public void write(EObject target, EReference ref, CodecWriterContext ctx)
            throws IOException {
        Object id = target.eGet(target.eClass().getEStructuralFeature("_id"));
        String value = id != null ? id.toString() :
            target.eResource().getURIFragment(target);
        ctx.getGenerator().writeString(value);
    }
}

public class MongoIdReader implements CodecValueReader<String, EReference> {
    @Override
    public String getName() {
        return "mongoId";
    }

    @Override
    public String read(CodecReaderContext ctx, EReference ref) throws IOException {
        String objectId = ctx.getParser().getString();
        return "#/persons/" + objectId;  // Transform to EMF URI
    }
}
```

#### Serialization Flow (Non-Containment)

```
ReferenceSerializationEntry (non-containment)
    │
    ├─ writeReferenceObject()
    │       │
    │       ├─ Write _type
    │       │
    │       ├─ If uriWriter configured:
    │       │       uriWriter.write(target, reference, gen, ctxt)
    │       │
    │       └─ Default: gen.writeString(getReferenceUri())
```

---

## 5. Registration

There are two ways to register value readers/writers:

1. **Instance-based registration** via builder (recommended) - uses `getName()` for auto-registration
2. **Direct registry manipulation** - explicit name-based registration

### 5.1 Instance-Based Registration via Builder (Recommended)

The builder accepts reader/writer instances and uses `getName()` for auto-registration:

```java
CodecConfiguration config = CodecConfiguration.builder()
    // Single instances - getName() used as registry key
    .valueReader(new ISODateReader())           // registered as "isoDate"
    .valueWriter(new ISODateWriter())           // registered as "isoDate"
    .valueReader(new EPackageValueReader())     // registered as "jsonSchemaToEPackage"
    .valueWriter(new EPackageValueWriter())     // registered as "ePackageToJsonSchema"

    // Multiple instances at once (varargs)
    .valueReaders(new MongoIdReader(), new CustomReader1(), new CustomReader2())
    .valueWriters(new MongoIdWriter(), new CustomWriter1())

    // Lambda/anonymous with explicit name (getName() not available)
    .valueReader("base64", (ctx, attr) ->
        Base64.getDecoder().decode(ctx.getParser().getString()))
    .valueWriter("base64", (value, attr, ctx) ->
        ctx.getGenerator().writeString(Base64.getEncoder().encodeToString((byte[]) value)))

    .build();
```

**Benefits of instance-based registration:**
- Type-safe: compiler ensures reader/writer implements the interface
- Self-documenting: `getName()` provides consistent naming
- No string literals: reader/writer owns its name
- Easier testing: instances can be mocked

### 5.2 Direct Registry Manipulation

For programmatic control, use `CodecValueRegistry` directly:

```java
CodecValueRegistry registry = new CodecValueRegistry();

// Register with explicit name
registry.register(new ISODateReader());     // uses reader.getName()
registry.register(new ISODateWriter());     // uses writer.getName()

// Or with override name
registry.registerReader("customDate", new ISODateReader());
registry.registerWriter("customDate", new ISODateWriter());
```

### 5.3 Type Resolution at Construction Time

When a reader/writer is resolved from the registry:

```java
// In ReferenceDeserializationEntry constructor:
CodecValueReader<?, ?> reader = registry.getReader(readerName).orElse(null);

if (reader instanceof ReferenceValueReader<?> refReader) {
    // For containment references
    if (refReader.canHandle(reference)) {
        this.containmentReader = refReader;
    } else {
        LOGGER.warning("Reader cannot handle reference: " + reference.getName());
    }
} else if (reader != null) {
    // For non-containment URI transformation
    this.uriReader = (CodecValueReader<String, EReference>) reader;
}
```

### 5.4 Passing Registry to CodecResource

The registry is typically obtained from the configuration:

```java
CodecConfiguration config = CodecConfiguration.builder()
    .valueReader(new ISODateReader())
    .valueWriter(new ISODateWriter())
    .build();

// Registry is accessible from config
CodecValueRegistry registry = config.getValueRegistry();

// Or pass explicitly to CodecResource
CodecResource resource = new CodecResource(
    uri,
    metadataService,
    config,
    config.getValueRegistry(),
    mapperBuilder
);
```

---

## 6. Activation per Feature

### 6.1 EAnnotation on EAttribute

```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="createdAt" eType="...">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="valueWriterName" value="isoDate"/>
    <details key="valueReaderName" value="isoDate"/>
  </eAnnotations>
</eStructuralFeatures>
```

### 6.2 EAnnotation on Containment EReference

```xml
<eStructuralFeatures xsi:type="ecore:EReference" name="schemas"
                     eType="ecore:EClass http://www.eclipse.org/emf/2002/Ecore#//EPackage"
                     containment="true">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="valueWriterName" value="schemas"/>
    <details key="valueReaderName" value="schemas"/>
  </eAnnotations>
</eStructuralFeatures>
```

### 6.3 EAnnotation on Non-Containment EReference

```xml
<eStructuralFeatures xsi:type="ecore:EReference" name="manager" eType="#//Person">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="valueWriterName" value="mongoId"/>
    <details key="valueReaderName" value="mongoId"/>
  </eAnnotations>
</eStructuralFeatures>
```

### 6.4 Runtime Override via Load/Save Options

**Option A: Reference by name (reader/writer must be in registry)**
```java
Map<String, Object> options = Map.of(
    "codec.featureValueReaders", Map.of(
        MyPackage.Literals.PERSON__CREATED_AT, "isoDate"
    ),
    "codec.featureValueWriters", Map.of(
        MyPackage.Literals.PERSON__CREATED_AT, "isoDate"
    )
);

resource.load(inputStream, options);
```

**Option B: Direct instance binding (no registry lookup)**
```java
Map<String, Object> options = Map.of(
    "codec.featureValueReaderInstances", Map.of(
        MyPackage.Literals.PERSON__CREATED_AT, new ISODateReader()
    ),
    "codec.featureValueWriterInstances", Map.of(
        MyPackage.Literals.PERSON__CREATED_AT, new ISODateWriter()
    )
);

resource.save(outputStream, options);
```

**Option C: Builder API**
```java
Map<String, Object> options = CodecOptionsBuilder.create()
    // By name
    .featureValueReader(MyPackage.Literals.PERSON__CREATED_AT, "isoDate")
    .featureValueWriter(MyPackage.Literals.PERSON__CREATED_AT, "isoDate")
    // Or by instance
    .featureValueReaderInstance(MyPackage.Literals.PERSON__CREATED_AT, new ISODateReader())
    .featureValueWriterInstance(MyPackage.Literals.PERSON__CREATED_AT, new ISODateWriter())
    .build();
```

---

## 7. Null Handling

### 7.1 Attributes

Custom readers/writers are **not called for null values**:
- Null attribute values are serialized as JSON `null`
- JSON `null` is deserialized as `null` without calling reader

### 7.2 References

Custom readers/writers are **not called for null references**:
- Null references are serialized according to `serializeNull` config
- JSON `null` sets the reference to `null` without calling reader

---

## 8. Error Handling

### 8.1 Writer Errors

If a custom writer throws an `IOException`, it is wrapped:

```java
throw new UncheckedIOException(
    "Custom value writer failed for " + feature.getName(), e);
```

### 8.2 Reader Errors

If a custom reader throws an `IOException`, it is wrapped:

```java
throw new UncheckedIOException(
    "Custom value reader failed for " + feature.getName(), e);
```

### 8.3 `canHandle()` Returns False

If a specialized reader/writer's `canHandle()` returns false:
- A warning is logged
- Fall back to default serialization/deserialization
- No error is thrown

### 8.4 Missing Writer/Reader

If a configured name is not found in the registry:
- Fall back to default serialization/deserialization
- No error is thrown (silent fallback)

---

## 9. Registry API

### 9.1 Registration

```java
// Auto-registration using getName()
CodecValueRegistry register(CodecValueReader<?, ?> reader);
CodecValueRegistry register(CodecValueWriter<?, ?> writer);

// Explicit name (overrides getName())
CodecValueRegistry registerReader(String name, CodecValueReader<?, ?> reader);
CodecValueRegistry registerWriter(String name, CodecValueWriter<?, ?> writer);

// Bulk registration
CodecValueRegistry registerAll(CodecValueReader<?, ?>... readers);
CodecValueRegistry registerAll(CodecValueWriter<?, ?>... writers);
```

### 9.2 Lookup

```java
Optional<CodecValueWriter<?, ?>> getWriter(String name);
Optional<CodecValueReader<?, ?>> getReader(String name);
```

### 9.3 Introspection

```java
boolean hasWriter(String name);
boolean hasReader(String name);
Map<String, CodecValueWriter<?, ?>> getWriters();
Map<String, CodecValueReader<?, ?>> getReaders();
Set<String> getWriterNames();
Set<String> getReaderNames();
```

---

## 10. Configuration Hierarchy

Custom value reader/writer resolution follows the standard configuration hierarchy:

1. **Load/Save Options** (highest priority)
   - `codec.featureValueReaderInstances` / `codec.featureValueWriterInstances` (direct instance)
   - `codec.featureValueReaders` / `codec.featureValueWriters` (by name)
2. **ResourceFactory Defaults**
3. **CodecConfiguration**
   - Instances registered via `.valueReader()` / `.valueWriter()`
4. **EAnnotation on EStructuralFeature**
   - `valueReaderName` / `valueWriterName` (by name)
5. **Built-in Defaults** (no custom reader/writer)

### 10.1 Builder Configuration Options

| Builder Method | Description |
|----------------|-------------|
| `.valueReader(reader)` | Register reader using `reader.getName()` |
| `.valueWriter(writer)` | Register writer using `writer.getName()` |
| `.valueReaders(r1, r2, ...)` | Register multiple readers (varargs) |
| `.valueWriters(w1, w2, ...)` | Register multiple writers (varargs) |
| `.valueReader(name, lambda)` | Register lambda reader with explicit name |
| `.valueWriter(name, lambda)` | Register lambda writer with explicit name |

### 10.2 Load/Save Option Keys

> **See also:** [Load/Save Options](13-load-save-options.md) for complete option reference.

#### Global Reader/Writer Registration

Register readers/writers to the registry for the current operation:

| Option Key | Type | Description |
|------------|------|-------------|
| `codec.valueReaders` | `List<CodecValueReader>` | Readers to register (uses `getName()`) |
| `codec.valueWriters` | `List<CodecValueWriter>` | Writers to register (uses `getName()`) |

**Example:**
```java
Map<String, Object> options = Map.of(
    "codec.valueReaders", List.of(new ISODateReader(), new EPackageValueReader()),
    "codec.valueWriters", List.of(new ISODateWriter(), new EPackageValueWriter())
);
resource.load(inputStream, options);
```

#### Per-Feature Binding

Bind readers/writers to specific features:

| Option Key | Type | Description |
|------------|------|-------------|
| `codec.featureValueReaders` | `Map<EStructuralFeature, String>` | Reader names per feature |
| `codec.featureValueWriters` | `Map<EStructuralFeature, String>` | Writer names per feature |
| `codec.featureValueReaderInstances` | `Map<EStructuralFeature, CodecValueReader>` | Reader instances per feature (direct binding) |
| `codec.featureValueWriterInstances` | `Map<EStructuralFeature, CodecValueWriter>` | Writer instances per feature (direct binding) |

**Example:**
```java
Map<String, Object> options = Map.of(
    // Register readers globally
    "codec.valueReaders", List.of(new ISODateReader()),

    // Bind by name (requires reader in registry)
    "codec.featureValueReaders", Map.of(
        MyPackage.Literals.PERSON__CREATED_AT, "isoDate"
    ),

    // Or bind instance directly (bypasses registry)
    "codec.featureValueReaderInstances", Map.of(
        MyPackage.Literals.PERSON__UPDATED_AT, new ISODateReader()
    )
);
```

---

## 11. Implementation Classes

| Class | Purpose |
|-------|---------|
| `CodecValueReader<T, F>` | Base interface for custom deserialization with `getName()` |
| `CodecValueWriter<T, F>` | Base interface for custom serialization with `getName()` |
| `CodecReaderContext` | Context wrapper with parser, config, diagnostics |
| `CodecWriterContext` | Context wrapper with generator, config, diagnostics |
| `AttributeValueReader<T>` | Specialized for EAttribute with `canHandle()` |
| `AttributeValueWriter<T>` | Specialized for EAttribute with `canHandle()` |
| `ReferenceValueReader<T>` | Specialized for containment EReference with `canHandle()` |
| `ReferenceValueWriter<T>` | Specialized for containment EReference with `canHandle()` |
| `CodecValueRegistry` | Named reader/writer storage with auto-registration |
| `AttributeSerializationEntry` | Invokes attribute writers |
| `AttributeDeserializationEntry` | Invokes attribute readers |
| `ReferenceSerializationEntry` | Invokes reference writers (both types) |
| `ReferenceDeserializationEntry` | Invokes reference readers (both types) |

---

## 12. Real-World Example: OpenAPI with JSON Schema

OpenAPI documents embed JSON Schema in `components/schemas`. The codec handles this
by converting between `EPackage` and JSON Schema using custom `ReferenceValueReader/Writer`.

### 12.1 Quick Example

```xml
<!-- In openapi.ecore -->
<eStructuralFeatures xsi:type="ecore:EReference" name="schemas"
                     eType="ecore:EClass http://www.eclipse.org/emf/2002/Ecore#//EPackage"
                     containment="true">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="valueReaderName" value="jsonSchemaToEPackage"/>
    <details key="valueWriterName" value="ePackageToJsonSchema"/>
  </eAnnotations>
</eStructuralFeatures>
```

```java
// Register converters via builder (uses getName() for auto-registration)
CodecConfiguration config = CodecConfiguration.builder()
    .valueReader(new EPackageValueReader(converter))   // "jsonSchemaToEPackage"
    .valueWriter(new EPackageValueWriter(converter))   // "ePackageToJsonSchema"
    .build();
```

```json
{
  "openapi": "3.0.3",
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

The `schemas` object is automatically converted to an `EPackage` with an `EClass` named "Person".

**For complete OpenAPI support documentation, see [Chapter 17: OpenAPI Support](17-format-abstraction.md).**

---

[Next: Error Handling →](15-error-handling.md)
