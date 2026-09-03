# Custom Value Readers/Writers

[← Load/Save Options](13-load-save-options.md) | [Next: Error Handling →](15-error-handling.md)

---

> **See also:**
> - [Annotation Reference](16-annotation-reference.md) (Feature Configuration) for `valueReaderName` and `valueWriterName` keys
> - [Load/Save Options](13-load-save-options.md) for `CODEC_FEATURE_VALUE_READER_INSTANCES` and `CODEC_FEATURE_VALUE_WRITER_INSTANCES`
> - [§13 Prefix Readers/Writers](#13-prefix-readerswriters) for document keys that belong to a backend, not to a feature (`CODEC_PREFIX_WRITER_INSTANCES`, `CODEC_PREFIX_READER_INSTANCES`)

---

Extensibility hooks for custom serialization logic.

## 1. Overview

Custom value readers/writers allow you to:
- Transform values during serialization/deserialization
- Handle special data types (dates, binary, custom formats)
- Implement domain-specific encoding
- Customize reference URI formats
- Convert embedded formats (e.g., JSON Schema to EPackage)

A second, key-bound extension point — **prefix readers/writers** — lets a backend own document
keys that have no feature behind them at all (`_owner` beside `_id`). It is described in
[§13](#13-prefix-readerswriters); everything before that is about feature values.

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
| `ReferenceValueReader<T>` | Read references in custom format (containment or non-containment) | JSON Schema → EPackage |
| `ReferenceValueWriter<T>` | Write references in custom format (containment or non-containment) | EPackage → JSON Schema |
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
| **Inline Object** | `ReferenceValueReader<T>` / `ReferenceValueWriter<T>` | Convert entire object structure (containment or non-containment) | JSON Schema ↔ EPackage, JSON Schema ↔ EClass |
| **Non-Containment URI** | `CodecValueReader<String, EReference>` / `CodecValueWriter<EObject, EReference>` | Transform reference URI | MongoDB ObjectId ↔ EMF URI |

### 4.2 Inline Object Reference Readers/Writers

Used for references (containment **or** non-containment) where the referenced object needs custom inline conversion. When a `ReferenceValueWriter`/`ReferenceValueReader` is configured via `valueWriterName`/`valueReaderName`, it is used regardless of the reference's containment flag — the annotation is an explicit user intent to serialize the object inline in a custom format.

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

#### Serialization Flow

```
ReferenceSerializationEntry
    │
    ├─ Lookup customWriter from registry (valueWriterName)
    │
    ├─ If writer instanceof ReferenceValueWriter:
    │       │
    │       ├─ Check canHandle(reference)
    │       │       │
    │       │       ├─ YES: store as referenceWriter
    │       │       │
    │       │       └─ NO: Log warning, use default serialization
    │
    ├─ At serialization time:
    │   ├─ Containment + referenceWriter → referenceWriter.write(...)
    │   ├─ Containment + no writer → ctxt.writeValue(gen, target)
    │   ├─ Non-containment + referenceWriter → referenceWriter.write(...)
    │   └─ Non-containment + no writer → writeReferenceObject (URI/$ref)
    │
    └─ Key: referenceWriter is used for BOTH containment and non-containment
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

> **Shipped id-plane handlers (issue #104):** For the *id plane* (as opposed to the reference
> URI transformation above) the BSON module ships a ready-made pair registered under the name
> **`objectId`**: `ObjectIdValueWriter` stores a valid 24-char hex id as a **native BSON
> `ObjectId`** (guarded by `ObjectId.isValid`, plain string otherwise), and
> `ObjectIdValueReader` restores the hex string on load. Wire it via `idValueWriterName` /
> `idValueReaderName` (see [09-id.md §5.0](09-id.md#50-configuration-keys)). `ObjectId` is a
> plain BSON spec type from the `org.bson` library — no MongoDB driver involved.

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

#### Per-Feature Binding

Bind readers/writers to specific features:

| Option Key | Type | Description |
|------------|------|-------------|
| `codec.featureValueReaderInstances` | `Map<EStructuralFeature, CodecValueReader>` | Reader instances per feature (direct binding, bypasses registry) |
| `codec.featureValueWriterInstances` | `Map<EStructuralFeature, CodecValueWriter>` | Writer instances per feature (direct binding, bypasses registry) |
| `codec.featureValueReaders` | `Map<EStructuralFeature, String>` | **Deprecated** — use config resolution (`valueReaderName`) or instances |
| `codec.featureValueWriters` | `Map<EStructuralFeature, String>` | **Deprecated** — use config resolution (`valueWriterName`) or instances |

Instance binding works for EAttributes and EReferences alike (see
[Load/Save Options §4](13-load-save-options.md)). To bind a *registered* reader/writer by
name per operation, use the general config resolution instead
(`"ClassName.featureName"` → `valueReaderName`/`valueWriterName`).

**Example:**
```java
Map<String, Object> options = Map.of(
    // Bind an instance directly (bypasses registry)
    "codec.featureValueReaderInstances", Map.of(
        MyPackage.Literals.PERSON__UPDATED_AT, new ISODateReader()
    ),

    // Bind a registered reader by name via config resolution
    "Person.createdAt", Map.of("valueReaderName", "isoDate")
);
```

> **Note — no runtime registration:** there is deliberately no option to register
> readers/writers into the `CodecValueRegistry` per load/save operation (the former
> `codec.valueReaders`/`codec.valueWriters` options were never implemented and have been
> removed, see issue #45). Readers/writers resolved by name — including those referenced
> by `valueReaderName`/`valueWriterName` model annotations — must be present in the
> registry when the resource is created.

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

## 13. Prefix Readers/Writers

*Issue #193 (supersedes #151). Spec first, then implementation — see the sub-issues there.*

### 13.1 Purpose

A backend that stores one document per EObject sometimes needs bookkeeping of its own next to
the codec's output — the Fennec Mongo backend wanted `_owner` beside `_id`, so that dropping a
containment subtree deletes the child documents it owned. Such a key has **no feature behind
it**: nothing in the model produces it, nothing in the model consumes it. The value readers and
writers of §2–§9 cannot express it — they transform the value of a feature, and are looked up by
name from a feature's configuration.

Prefix readers/writers are the key-bound counterpart. A backend registers, **per document key**, a
writer that produces the field from the EObject and a reader that consumes the field when the
object is read back. The codec does not know what the key means; it knows who owns it.

The name says where the fields go: into the *prefix* of the object, after the codec's own metadata
and before the model's features.

### 13.2 Interfaces

```java
public interface CodecPrefixWriter {
    /**
     * Writes the field for {@code key} — field name and value — through the context's
     * generator, or returns {@code false} to write nothing for this object.
     */
    boolean write(String key, EObject object, CodecPrefixWriterContext ctx) throws IOException;
}

public interface CodecPrefixReader {
    /**
     * Consumes the value found under {@code key}. The context's parser is positioned at the
     * value; {@code target} is the EObject being built, already created.
     */
    void read(String key, EObject target, CodecPrefixReaderContext ctx) throws IOException;
}

public interface CodecPrefixWriterContext {
    JsonGenerator getGenerator();          // inside the object, after the metadata fields
    SerializationContext getJacksonContext();
    EffectiveCodecConfig getConfig();
    DiagnosticCollector getDiagnostics();
    void addWarning(String message);
    void addError(String message);
}

public interface CodecPrefixReaderContext {
    JsonParser getParser();                // positioned at the value of the key
    DeserializationContext getJacksonContext();
    EffectiveCodecConfig getConfig();
    DiagnosticCollector getDiagnostics();
    void addWarning(String message);
    void addError(String message);
}
```

The **key is a parameter**, not a property of the handler. One handler instance may be registered
under several keys and is told which one it is serving. There is no `getName()`, no `canHandle()`:
the registry is the binding.

**Writer contract.** The writer writes the field name itself (`writeName(key)`) and then the
value, using the generator. It writes **that one field or nothing** — one key, one field. The
codec does not verify this; a writer that emits other field names breaks the read side's ability
to route them and is a bug in the writer. Returning `false` means the field is absent for this
object; that is how a writer restricts itself to roots, to cross-document children, or to whatever
it decides — the codec has no scope configuration for prefix keys.

**Reader contract.** The reader consumes exactly the value the parser is positioned at (a scalar,
an object or an array, whole) and leaves the parser after it. Whether the parser is the live
stream or a replay of a buffered value (§13.5) is invisible to the reader. It may do anything
with the value: ignore it (the "reserved key" of #151 is an empty reader), attach an `Adapter`
to `target`, fill an index on the resource, validate. It must not set features on `target`
that the document also carries — that is the feature entry's job and would be written twice.

### 13.3 Registry

```java
public class CodecPrefixRegistry {
    CodecPrefixRegistry register(String key, CodecPrefixWriter writer);   // duplicate key → IllegalArgumentException
    CodecPrefixRegistry register(String key, CodecPrefixReader reader);   // duplicate key → IllegalArgumentException
    CodecPrefixRegistry unregisterWriter(String key);
    CodecPrefixRegistry unregisterReader(String key);
    Optional<CodecPrefixWriter> getWriter(String key);
    Optional<CodecPrefixReader> getReader(String key);
    boolean hasWriter(String key);
    boolean hasReader(String key);
    List<String> writerKeys();     // registration order — this is the output order (§13.4)
    Set<String> readerKeys();
    CodecPrefixRegistry copy();
}
```

Registration is **by key, lookup is a map**. A key may have one writer and one reader, registered
independently — a backend that only ever reads a foreign document registers a reader alone
(§13.6, row "reader only"). A second writer or a second reader for the same key is rejected at
registration; there is no "first wins" and no priority. The registry's key sets are the complete
list of what a backend occupies in a document.

The registry is a **sister** of `CodecValueRegistry`, not part of it: different handler shape,
different lookup, and a prefix key must never be confused with a value handler name.

### 13.4 Position in the output — the prefix

Prefix fields are written **after the codec's metadata fields and before the first feature**, in
registration order (`writerKeys()`):

```
metadata (_type, _supertype, fingerprint, _id — in whatever order idOnTop and the formats
          produce, see 09-id.md §8.7 and 06-type.md §5.0.2)
prefix   (registered keys, registration order)
features (model order, or as ordered by the feature configuration)
```

Rules that follow from this:

- **`idOnTop` does not move the prefix.** With `idOnTop=true` the `_id` key and the promoted
  eID attribute float to the front; the prefix still follows the last metadata field
  (`_type` / `_supertype` / fingerprint). With `idOnTop=false` the prefix follows `_id`.
- **STRUCTURED type or id: the prefix follows the metadata object, it is never placed inside
  it.** The merged metadata object of 06-type.md §7 is the codec's; prefix keys are the backend's.
- **`sortPropertiesAlphabetically` sorts features, not the prefix.** The prefix block keeps its
  slot and its registration order.
- **Every object is a candidate**, not only the root: a contained object gets the same writers
  called with itself as `object`. Declining (`false`) is per call.
- **A key that collides with a feature name of the class being written is not written as a
  prefix field.** The feature is written, the writer is skipped for that class, and a WARNING
  reaches the resource once per class and key (§13.7). Data loss is worse than noise.

Example — a writer registered under `_owner` that writes the container's id and declines for the
root, model `Order` containing `Item`:

```json
{
  "_id": "o1",
  "_type": "Order",
  "items": [
    { "_id": "i1", "_type": "Item", "_owner": "o1", "label": "Bolt" },
    { "_id": "i2", "_type": "Item", "_owner": "o1", "label": "Nut" }
  ]
}
```

### 13.5 Reading — routing order and deferral

On read, a property name is resolved in this order:

1. the deserialization entries — features, `_id`, `_type`, `_supertype` — and the codec's own
   deliberately-unread keys (separator key, non-deserialized features);
2. the **prefix registry** (and the `codec.prefixReaderInstances` option, which wins per key);
3. the unknown branch, exactly as today: WARNING, or an `IllegalStateException` under
   `strictOnUnknown`.

Step 1 before step 2 is what makes the feature win a name clash on the read side too: a prefix key
equal to a feature name is read as the feature, the reader is skipped for that class, WARNING once
per class and key.

**Deferral.** A prefix key usually precedes the type — that is the point of the prefix — so the
EObject does not exist yet when the key is met. Such a value goes into the deferred map like any
other early property (01-architecture.md §5.1) and the reader is invoked in the replay, once the
EObject exists, with a parser over the buffered value. A key met after the object exists is read
in place. The reader sees a parser positioned at the value in both cases and cannot tell the
difference.

### 13.6 Diagnostics — no new strictness flag

The rule is one sentence: **a key with a registered reader is known; a key without one is
unknown.** Everything else follows from the existing hierarchy (15-error-handling.md §0,
11-feature.md §11.5):

| registered | write | read |
|---|---|---|
| **reader and writer** | field written | consumed by the reader, no diagnostic, also under `strictOnUnknown`; write → read → write reproduces the document |
| **writer only** | field written | *unknown*: WARNING (lenient) / load fails (`strictOnUnknown`) — the backend wrote a key it does not read, and is told so on every read |
| **reader only** | nothing written | consumed silently in both modes; the document was written by someone else |
| **neither** | nothing written | today's behaviour, unchanged |

- A **reader that throws** follows the strictness hierarchy: STRICT fails the load; otherwise an
  ERROR diagnostic on the resource, the value is dropped, the rest of the object is read.
- A **writer that throws** is a write-side WARNING (15-error-handling.md §5.2) and the field is
  skipped; the save does not fail on it, like every other write-side fallback.
- **Name clash**, either side: WARNING once per class and key, the feature wins (§13.4, §13.5).

There is deliberately **no option to silence an unregistered key**. Registering an empty reader is
that option, and it leaves a trace of who owns the key.

### 13.7 Registration and configuration

Mirrors §5 and §6.4 for value handlers, with the key in place of the name.

**OSGi.** `CodecPrefixWriter` and `CodecPrefixReader` are registered as services carrying the
service property `codec.prefix.key` (`String+`; several keys register the same service under each).
`CodecPrefixRegistryComponent` collects them into a shared `CodecPrefixRegistry`; the resource
factory components bind it optionally and hand a `copy()` to every resource, exactly as they do
for the value registry (osgi-resource-factory-architecture.md). A service without the property is
ignored with a log warning; a second service for a key already taken is ignored with a log
warning — the registry's `IllegalArgumentException` is for programmatic misuse and must not tear
down the component.

**Plain Java.** Build the registry and hand it to the resource or to a factory:

```java
CodecPrefixRegistry registry = new CodecPrefixRegistry()
    .register("_owner", new OwnerWriter())
    .register("_owner", new OwnerReader())
    .register("_ownerRef", new OwnerWriter());     // same instance, second key

// per resource: the full constructor
new CodecResource(uri, metadataService, resolver, valueRegistry, registry, mapperBuilder, formatProvider, null);

// or per factory, for every resource it creates
CodecResourceFactory factory = new CodecResourceFactory(metadataService);
factory.setPrefixRegistry(registry);
CodecFormatResourceFactory bson = new CodecFormatResourceFactory(metadataService, new BsonFormatProvider());
bson.setPrefixRegistry(registry);
```

**Load/save options** — per-operation instance binding that wins over the registry for its key
(13-load-save-options.md §4.3):

```java
Map<String, Object> options = Map.of(
    "codec.prefixWriterInstances", Map.of("_owner", new OwnerWriter()),   // save
    "codec.prefixReaderInstances", Map.of("_owner", new OwnerReader())    // load
);
```

There is **no annotation** for prefix keys, and no per-class configuration: a prefix key is a
property of the document store, not of the model.

### 13.8 What this is not

- Not a way to read arbitrary unknown keys into the model. A key nobody registered is unknown.
- Not a feature-value hook. A backend that wants to transform how a *feature* is written uses §2–§9.
- Not shipped with handlers. This repository provides the extension point and a test pair; the
  `_owner` pair belongs to the backend that needs it.

---

[Next: Error Handling →](15-error-handling.md)
