# Error Handling & Diagnostics

[← Custom Values](14-custom-values.md) | [Next: Annotation Reference →](16-annotation-reference.md)

---

> **See also:**
> - [Annotation Reference](16-annotation-reference.md) (Load/Save Options) for complete option reference
> - [Load/Save Options](13-load-save-options.md) (section 5) for strictness settings

---

The codec uses EMF's standard diagnostic mechanism for reporting errors and warnings during serialization and deserialization.

## 0. Validation Layers

Validation happens at **four distinct layers**, each with different timing and scope:

| Layer | When | What | Where Implemented |
|-------|------|------|-------------------|
| **1. Annotation Parsing** | EPackage registration | Invalid annotation keys, wrong level, invalid values | `codec.metadata` (AspectProvider) |
| **2. Config Resolution** | First access to config | Self-contained constraints (e.g., nameKey with PLAIN format) | `Config.validate()` methods |
| **3. Cross-Config Validation** | Config resolution | Multi-config constraints (e.g., STRUCTURED + NONE + superType) | `ConfigurationResolver.validateCrossConfig()` |
| **4. Runtime (Ser/Deser)** | During operation | Data-dependent errors (e.g., CLASS strategy + null instanceClassName) | Serializer/Deserializer entries |

### Layer 1: Annotation Parsing

Occurs when an EPackage is registered with the MetadataService. Validates:
- Annotation keys are at correct level (e.g., `typeMapId` only on EClass, not EReference)
- Enum values are valid
- Boolean values are valid

**Implementation:** `CodecAspectProvider.checkForClassOnlyKeys()` in `codec.metadata` project.

See [Section 6.10](#610-annotation-parsing-errors-metadata-layer) for error scenarios.

### Layer 2: Config Resolution (Self-Contained)

Occurs when configuration is first resolved for an EClass/EStructuralFeature. Each `Config.validate()` method checks:
- Properties that are ignored in certain modes (e.g., `typeNameKey` ignored when format is PLAIN)
- Property dependencies within the same config

#### Layer 2a: Unusable Values (issue #174)

Occurs at the first resolution, once per diagnostic collector, and covers **every** source: load
and save options, resource, factory and module properties, and annotations — including their
class- and feature-scoped nested maps. `ConfigValueValidator` asks one question per configured
key that names a `ConfigProperty`: would this value be dropped?

A value that does not parse is replaced by a fallback, and a fallback is invisible — the codec
then behaves exactly as if the setting had never been written, which cannot be told apart from a
setting that is not implemented. So it is reported as a WARNING naming the property, the value,
the legal values and what applies instead:

```
Invalid value 'NOPE' for config property 'typeStrategy' (resource properties);
legal values are [URI, NAME, CLASS, NUMERIC, SCHEMA_AND_TYPE, NONE].
The value is ignored, so a wider scope or the default (URI) applies.
```

Legal values are declared next to the property, not in the message: a `ConfigProperty` whose
value names an enum literal records that enum, and everything a message says about legal values
is derived from it.

Judged: enum-named values, booleans, integers. `typeInclude="yes"` is a WARNING and keeps the
fallback — it used to become an explicit `false`, since `Boolean.parseBoolean` answers false to
everything that is not `"true"`, so an unparseable value inverted the caller's intent instead of
being no value at all. Case is not judged: `typeStrategy="name"` resolves to `NAME` silently, as
intended.

Not judged: **unknown keys**. A property map legitimately carries runtime options that are not
`ConfigProperty` keys, and separating those from typos is the caller's job (`CodecResource` does
it for load and save options).

### Layer 3: Cross-Config Validation

Occurs after config resolution when multiple configs interact. Examples:
- **STRUCTURED + NONE + superTypeSerialize=true** → ERROR (can't write supertype in _type object when no _type)
- **STRUCTURED + both value readers** → WARNING (superType reader ignored)
- **STRUCTURED + both value writers** → WARNING (superType writer ignored)

### Layer 4: Runtime (Serialization/Deserialization)

Occurs during actual serialization or deserialization. Examples:
- **CLASS strategy + instanceClassName is null** → ERROR
- **Abstract type without concrete resolution** → ERROR
- **Unknown discriminator value** → depends on `fallbackStrategy`

See [Section 6](#6-complete-error-scenarios) for comprehensive error scenarios.

---

## 1. Principles

- All errors and warnings are collected in the EMF Resource's diagnostics (`resource.getErrors()`, `resource.getWarnings()`)
- Errors cause the load/save operation to fail after all diagnostics are collected (unless fail-fast mode)
- Warnings do not cause failure but are reported for user awareness
- All diagnostics are also logged via the standard logging mechanism
- The `DeserializationMode` setting affects how strictly errors are enforced

---

## 2. Error Severity

| Severity | Behavior | Examples |
|----------|----------|----------|
| **ERROR** | Operation fails, diagnostic added | Cannot instantiate abstract type, unresolved type URI, missing required type info |
| **WARNING** | Operation continues, diagnostic added | Type collision (content type differs from hint), deprecated option usage, unknown field |

### 2.1 DeserializationMode Impact

The `DeserializationMode` setting (see [Load/Save Options](13-load-save-options.md) section 5) affects error severity:

| Scenario | STRICT Mode | LENIENT Mode |
|----------|-------------|--------------|
| Unknown field in JSON | ERROR | WARNING (field skipped) |
| Type mismatch | ERROR | WARNING (best-effort conversion) |
| Missing required field | ERROR | WARNING (default used) |
| Value conversion failure | ERROR | WARNING (default used) |

---

## 3. Diagnostic Information

Each diagnostic includes:
- **Message** - Description of the issue
- **Location** - Resource URI, line/column if available
- **Source** - Codec component that raised the issue (for programmatic filtering)

---

## 4. Option Keys

### 4.1 Diagnostic Control Options

| Java Constant | Property Key | Value Type | Default | Description |
|---------------|--------------|------------|---------|-------------|
| `CODEC_FAIL_FAST` | `codec.failFast` | `Boolean` | `false` | Throw on first error instead of collecting all |
| `CODEC_SUPPRESS_WARNINGS` | `codec.suppressWarnings` | `Boolean` | `false` | Suppress all warnings |
| `CODEC_SUPPRESS_WARNING_SOURCES` | `codec.suppressWarningSources` | `Set<String>` | empty | Suppress warnings from specific sources |
| `CODEC_DIAGNOSTIC_HANDLER` | `codec.diagnosticHandler` | `DiagnosticHandler` | null | Custom diagnostic handler |

### 4.2 Usage Examples

**Basic diagnostic checking:**
```java
resource.load(inputStream, options);

// Check for errors
for (Diagnostic error : resource.getErrors()) {
    System.err.println("ERROR: " + error.getMessage());
}

// Check for warnings
for (Diagnostic warning : resource.getWarnings()) {
    System.out.println("WARNING: " + warning.getMessage());
}
```

**Fail-fast mode:**
```java
Map<String, Object> options = Map.of(
    "codec.failFast", true
);

try {
    resource.load(inputStream, options);
} catch (CodecException e) {
    // First error encountered - operation aborted
    System.err.println("Failed: " + e.getMessage());
}
```

**Suppress warnings:**
```java
Map<String, Object> options = Map.of(
    // Suppress all warnings
    "codec.suppressWarnings", true
);
resource.load(inputStream, options);
// resource.getWarnings() will be empty
```

**Suppress specific warning sources:**
```java
Map<String, Object> options = Map.of(
    "codec.suppressWarningSources", Set.of(
        "TypeDeserializationEntry",
        "AttributeDeserializationEntry"
    )
);
resource.load(inputStream, options);
// Warnings from these sources are filtered out
```

---

## 5. Diagnostic Sources

Each diagnostic includes a source identifier for programmatic handling.

### 5.1 Deserialization Sources

| Source | Component | Error Examples | Warning Examples |
|--------|-----------|----------------|------------------|
| `CodecEObjectDeserializer` | Root deserializer | No type info and no hint, failed EObject creation | Unexpected token |
| `TypeDeserializationEntry` | Type resolution | — | Could not resolve EClass, unexpected token |
| `IdDeserializationEntry` | ID parsing | EObject not yet created | STRUCTURED ID format mismatch, parse errors |
| `ReferenceDeserializationEntry` | Reference handling | EObject not yet created, no deserializer found, EMap entry class without key/value feature | Unexpected token, EMap key not parseable by its data type |
| `AttributeDeserializationEntry` | Attribute parsing | EObject not yet created | Value conversion failure, unexpected token |
| `CodecValueReader` (custom) | Custom value reading | Invalid value format, parse failure | Deprecated format, data migration hints |

### 5.2 Serialization Sources

| Source | Component | Error Examples | Warning Examples |
|--------|-----------|----------------|------------------|
| `CodecEObjectSerializer` | Root serializer | Null EObject, unregistered EPackage | — |
| `TypeSerializationEntry` | Type writing | Unknown type strategy | — |
| `IdSerializationEntry` | ID writing | Missing ID feature, null ID value | Combined ID with null component |
| `ReferenceSerializationEntry` | Reference writing | Circular reference (non-expand), cross-doc without URI | Unresolved proxy serialized |
| `AttributeSerializationEntry` | Attribute writing | Value conversion failure | Enum value not found |
| `CodecValueWriter` (custom) | Custom value writing | Cannot serialize value, null value | Lossy conversion, precision loss |

### 5.3 Common Sources (Both Directions)

| Source | Component | Error Examples | Warning Examples |
|--------|-----------|----------------|------------------|
| `CodecResource` | Resource operations | I/O failure, stream error | Unexpected token |
| `ConfigurationResolver` | Config resolution | Invalid option type, unknown enum value | Deprecated option usage |

### 5.4 Programmatic Source Detection

```java
for (Diagnostic error : resource.getErrors()) {
    String source = error.getSource();

    switch (source) {
        // Deserialization
        case "TypeDeserializationEntry" -> handleTypeError(error);
        case "IdDeserializationEntry" -> handleIdError(error);
        case "ReferenceDeserializationEntry" -> handleRefError(error);
        case "AttributeDeserializationEntry" -> handleAttrError(error);
        // Serialization
        case "TypeSerializationEntry" -> handleTypeError(error);
        case "IdSerializationEntry" -> handleIdError(error);
        case "ReferenceSerializationEntry" -> handleRefError(error);
        case "AttributeSerializationEntry" -> handleAttrError(error);
        // Custom value handlers
        case String s when s.startsWith("CodecValue") -> handleCustomValueError(error);
        default -> handleGenericError(error);
    }
}
```

---

## 6. Complete Error Scenarios

### Deserialization Errors

### 6.1 Type Resolution Errors (Deserialization)

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| No type info and no hint | ERROR | `Cannot deserialize: no type information and no CODEC_ROOT_TYPE hint` | Operation fails |
| Unknown type value (with hint) | WARNING | `Could not resolve EClass from type value: {value}` | Falls back to hint |
| Unknown type value (no hint) | ERROR | `Cannot deserialize: no type information and no CODEC_ROOT_TYPE hint` | Operation fails |
| Abstract type without concrete hint | ERROR | `Cannot instantiate abstract type: {type}` | Operation fails |
| Type collision (incompatible) | WARNING | `Type collision: CODEC_ROOT_TYPE={hint} but content type={actual}` | Content type used |

### 6.2 ID Errors (Deserialization)

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| Missing ID attribute | WARNING | `No ID attribute found for EClass {class}` | ID not set |
| Invalid separator in combined ID | WARNING | `Cannot split ID value '{value}' with separator '{sep}'` | Partial ID set |
| STRUCTURED format mismatch | WARNING | `Expected STRUCTURED ID format but found {actual}` | Best-effort parse |
| ID feature not found | WARNING | `ID feature '{feature}' not found in EClass {class}` | Feature skipped |

### 6.3 Reference Errors (Deserialization)

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| Circular reference in expand | WARNING | `Circular reference detected during expand: {path}` | Reference skipped |
| Unresolved proxy URI | WARNING | `Cannot resolve proxy URI: {uri}` | Proxy created |
| Invalid reference format | WARNING | `Expected {expected} reference format but found {actual}` | Best-effort parse |
| Missing ref key | WARNING | `Reference object missing '_ref' key` | Reference skipped |
| EMap entry class without key or value feature | ERROR | `EMap entry class '{class}' missing key or value feature` | Map skipped |
| EMap key not parseable by its data type | ERROR under `strictOnConversion`, else WARNING | `EMap key '{key}' of '{reference}' is no valid {type}, the entry is dropped: {cause}` | **That entry** dropped, the rest of the map is read |

The last row is the one to read carefully: a field name that the key data type cannot parse costs
**its own entry only**. Dropping the whole map would hand the caller a successful load with an empty
map, which is indistinguishable from a document that carried no entries (issue #154). Callers who
would rather fail than receive a shortened map set `strictOnConversion` — the key conversion joins
the same escalation path as every other dropped value, it has no flag of its own. See
[10-reference.md §8.1](10-reference.md#81-emap-keys).

### 6.4 Feature Errors (Deserialization)

| Scenario | STRICT | LENIENT | Message Template | Recovery |
|----------|--------|---------|------------------|----------|
| Unknown feature in JSON | ERROR | WARNING | `Unknown feature '{name}' for EClass {class}` | Field skipped |
| Value conversion failure | ERROR | WARNING | `Cannot convert value '{value}' to type {type}` | Default used |
| Required feature missing | ERROR | WARNING | `Required feature '{name}' not found in JSON` | Default used |
| Type mismatch | ERROR | WARNING | `Expected {expected} but found {actual} for '{name}'` | Best-effort conversion |

### Serialization Errors

### 6.5 Type Serialization Errors

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| Null EObject | ERROR | `Cannot serialize null EObject` | Operation fails |
| Unregistered EPackage | ERROR | `EPackage not registered: {nsURI}` | Operation fails |
| Unknown type strategy | ERROR | `Unknown TypeStrategy: {strategy}` | Operation fails |

### 6.6 ID Serialization Errors

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| Missing ID feature | WARNING | `ID feature '{feature}' not found in EClass {class}` | ID field skipped |
| Null ID value | WARNING | `ID value is null for feature '{feature}'` | ID field skipped |
| Combined ID with null component | WARNING | `Combined ID component '{feature}' is null` | Component omitted |

### 6.7 Reference Serialization Errors

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| Circular reference (expand mode) | WARNING | `Circular reference detected: {path}` | Written as reference, not expanded |
| Cross-document without URI | ERROR | `Cannot serialize cross-document reference: target has no URI` | Operation fails |
| Unresolved proxy | WARNING | `Serializing unresolved proxy: {uri}` | Proxy URI written |
| Null reference in required feature | WARNING | `Required reference '{feature}' is null` | Field skipped |

### 6.8 Feature Serialization Errors

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| Value conversion failure | ERROR | `Cannot convert value '{value}' to JSON` | Operation fails |
| Enum value not found | WARNING | `Enum literal '{literal}' not found in {enum}` | Literal name used |
| Unsupported attribute type | ERROR | `Cannot serialize attribute type: {type}` | Operation fails |

### Common Errors (Both Directions)

### 6.9 Configuration Errors

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| Invalid discriminator path | ERROR | `Invalid discriminator path: {path}` | Operation fails |
| Unknown discriminator value | WARNING | `Unknown discriminator value '{value}' for map '{mapId}'` | Falls back to type strategy |
| Invalid scope value | ERROR | `Invalid StrategyScope value: {value}` | Operation fails |
| Invalid option type | ERROR | `Expected {expected} for option '{key}' but got {actual}` | Operation fails |

### 6.10 Annotation Parsing Errors (Metadata Layer)

These errors occur during EPackage registration when the `codec.metadata` layer parses EAnnotations into Aspect objects. The metadata layer **validates annotations** and ensures only valid configurations are stored in Aspect objects.

**Key principle:** Aspect objects always contain valid configurations. Invalid annotations are **ignored** (not applied) and **logged as diagnostics**.

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| Annotation key at wrong level | WARNING | `Annotation key '{key}' is not valid on {elementType}, ignored` | Key ignored, not applied to Aspect |
| Unknown annotation key | WARNING | `Unknown annotation key '{key}' on {element}` | Key ignored |
| Invalid enum value | WARNING | `Invalid value '{value}' for enum {enumType}, using default` | Default value used |
| Invalid boolean value | WARNING | `Invalid boolean value '{value}' for key '{key}'` | Default value used |

**Examples:**

```
WARNING: Annotation key 'typeMapId' is not valid on EReference, ignored
WARNING: Annotation key 'typeDiscriminatorPath' is not valid on EReference, ignored
WARNING: Annotation key 'idStrategy' is not valid on EAttribute, ignored
WARNING: Unknown annotation key 'fooBar' on EClass 'Person'
```

**Diagnostic Collection Pattern:**

Diagnostics are collected hierarchically in the metadata model. Each metadata element owns its diagnostics (the container identifies the source), with derived `allDiagnostics` features for convenient aggregation:

```
PackageMetadata
  ├── diagnostics: EList<MetadataDiagnostic>     [own package-level issues]
  └── allDiagnostics: EList<MetadataDiagnostic>  [derived] = own + all class diagnostics
      │
      └── ClassMetadata
            ├── diagnostics: EList<MetadataDiagnostic>     [own class-level issues]
            └── allDiagnostics: EList<MetadataDiagnostic>  [derived] = own + all feature diagnostics
                  │
                  └── FeatureMetadata
                        └── diagnostics: EList<MetadataDiagnostic>  [own feature-level issues]
```

**Interface:** `DiagnosticContainer` provides the `diagnostics` containment reference, implemented by `PackageMetadata`, `ClassMetadata`, and `FeatureMetadata`.

**Accessing diagnostics:**

```java
MetadataService metadataService = ...;
PackageMetadata metadata = metadataService.getPackageMetadata(myPackage);

// Get all diagnostics for entire package (including all classes and features)
for (MetadataDiagnostic diagnostic : metadata.getAllDiagnostics()) {
    System.out.println(diagnostic.getSeverity() + ": " + diagnostic.getMessage());
    // Container of diagnostic identifies the source element
    EObject source = diagnostic.eContainer();
}

// Get diagnostics for a specific class (including its features)
ClassMetadata classMetadata = metadata.getClasses().get(0);
for (MetadataDiagnostic diagnostic : classMetadata.getAllDiagnostics()) {
    // ...
}

// Get only direct diagnostics for a feature
FeatureMetadata featureMetadata = classMetadata.getFeatures().get(0);
for (MetadataDiagnostic diagnostic : featureMetadata.getDiagnostics()) {
    // ...
}
```

**Implementation:** See [Annotation Reference](16-annotation-reference.md) for which keys are valid at each EMF element level (EClass, EReference, EAttribute).

### 6.11 Custom Value Reader/Writer Errors

Custom value readers and writers (see [Custom Values](14-custom-values.md)) can report diagnostics via their context. The source name defaults to the reader/writer's `getName()` value.

| Scenario | Severity | Message Template | Recovery |
|----------|----------|------------------|----------|
| Invalid value format | ERROR | `Cannot parse value: {details}` | Operation fails (or default in LENIENT) |
| Value out of range | ERROR | `Value {value} out of valid range` | Operation fails (or clamped in LENIENT) |
| Deprecated format detected | WARNING | `Deprecated format, consider migrating to {newFormat}` | Value parsed with warning |
| Precision loss | WARNING | `Precision loss converting {source} to {target}` | Best-effort conversion |
| Missing optional data | WARNING | `Optional field '{field}' not present` | Default used |

**Example - Custom reader reporting diagnostics:**
```java
public class GeoCoordinateReader implements CodecValueReader<GeoCoordinate, EAttribute> {
    @Override
    public String getName() { return "geoCoordinate"; }

    @Override
    public GeoCoordinate read(CodecReaderContext ctx, EAttribute feature) throws IOException {
        JsonParser parser = ctx.getParser();

        // Validate coordinates
        double lat = parser.getDoubleValue();
        if (lat < -90 || lat > 90) {
            ctx.addError("Latitude " + lat + " out of valid range [-90, 90]");
            return null;
        }

        // Warn about precision
        if (hasExcessivePrecision(lat)) {
            ctx.addWarning("Coordinate precision exceeds 6 decimal places, truncating");
        }

        return new GeoCoordinate(lat, lon);
    }
}
```

---

## 7. Diagnostic API

### 7.1 Accessing Diagnostic Details

```java
resource.load(inputStream, options);

for (Diagnostic error : resource.getErrors()) {
    System.err.println("ERROR: " + error.getMessage());

    // Access location if available
    if (error instanceof ResourceDiagnostic rd) {
        System.err.println("  Location: " + rd.getLocation());
        System.err.println("  Line: " + rd.getLine() + ", Column: " + rd.getColumn());
    }
}
```

### 7.2 Custom Diagnostic Handler

For advanced use cases (logging, metrics, alerting), provide a custom handler:

```java
DiagnosticHandler handler = new DiagnosticHandler() {
    @Override
    public void handleError(String message, String source, Object location) {
        logger.error("[{}] {}", source, message);
        metrics.incrementCounter("codec.errors." + source);
    }

    @Override
    public void handleWarning(String message, String source, Object location) {
        logger.warn("[{}] {}", source, message);
        metrics.incrementCounter("codec.warnings." + source);
    }
};

Map<String, Object> options = Map.of(
    "codec.diagnosticHandler", handler
);
resource.load(inputStream, options);
```

---

## 8. Implementation Details

The codec uses a `DiagnosticCollector` internally to aggregate errors and warnings:

1. **Initialization:** A `DiagnosticCollector` is created at the start of each load/save operation
2. **Propagation:** The collector is passed through Jackson's context attributes
3. **Collection:** Each deserialization/serialization entry adds diagnostics via `ContextHelper.addError()`/`addWarning()`
4. **Finalization:** After the operation, `collector.addToResource(resource)` transfers all diagnostics to the EMF Resource

### 8.1 Null-Safety

All diagnostic methods are null-safe. When context is null (e.g., in unit tests), diagnostics are silently skipped:

```java
// Safe to call even if ctxt is null
ContextHelper.addWarning(ctxt, "message", parser, "Source");
```

### 8.2 Custom Value Reader/Writer Integration

Custom value readers and writers can report diagnostics via their context objects:

```java
public class MyValueReader implements CodecValueReader<MyType, EAttribute> {
    @Override
    public MyType read(CodecReaderContext ctx, EAttribute feature) throws IOException {
        // Report warning via context
        ctx.addWarning("Deprecated format detected, consider migrating");

        // Report error (will be collected, operation continues unless fail-fast)
        ctx.addError("Invalid value format");

        return parseValue(ctx.getParser());
    }
}
```

See [Custom Values](14-custom-values.md) (section 4) for complete context API.

---

## 9. Security Limits

The codec enforces built-in limits to protect against denial-of-service attacks via malicious input.

### 9.1 Nesting Depth Limit

| Property | Value |
|----------|-------|
| **Constant** | `CodecEObjectDeserializer.MAX_NESTING_DEPTH` |
| **Default** | 200 |
| **Scope** | All recursive value reading during deserialization |
| **Recovery** | `parser.skipChildren()`, value dropped, WARNING diagnostic added |

Deeply nested JSON/YAML/CBOR structures (objects within objects, arrays within arrays, or mixed) are truncated at 200 levels of nesting. When the limit is exceeded:

1. The remaining nested content is skipped via `parser.skipChildren()` to keep the parser in a consistent state
2. The value is dropped (`null`)
3. A warning diagnostic with source `CodecEObjectDeserializer` or `AttributeDeserializationEntry` is added to the resource
4. Deserialization continues — the EObject is still produced, only the deeply nested value is missing

The limit protects two deserialization paths:
- **Deferred properties** — values appearing before `_type` in JSON, read by `CodecEObjectDeserializer.readCurrentValue()`
- **EJavaObject / JSON-to-String attributes** — values read by `AttributeDeserializationEntry.readAnyJsonValue()` and `readJsonStructureAsString()`

**CWE references:** CWE-674 (Uncontrolled Recursion), CWE-400 (Resource Exhaustion).

### 9.2 Collection Size Limit

| Property | Value |
|----------|-------|
| **Constant** | `CodecEObjectDeserializer.MAX_COLLECTION_SIZE` |
| **Default** | 100,000 |
| **Scope** | All collection-accumulating loops during recursive value reading |
| **Recovery** | Remaining elements skipped, WARNING diagnostic added, truncated collection returned |

Arrays and objects with more than 100,000 elements are truncated during deserialization. When the limit is exceeded:

1. A warning diagnostic is emitted once
2. Remaining elements are skipped via `parser.skipChildren()` without accumulating
3. The loop drains to the matching END_ARRAY/END_OBJECT
4. The truncated collection (with elements read so far) is returned

The limit applies to the same paths as the nesting depth limit (deferred properties and EJavaObject attributes).

**CWE references:** CWE-400 (Resource Exhaustion), CWE-770 (Allocation Without Limits).

### 9.3 Type Resolution Scoping

| Property | Value |
|----------|-------|
| **Strategies affected** | `NAME`, `CLASS`, `NUMERIC` |
| **Requirement** | Schema hint (`CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE`) |
| **Scope** | `TypeResolutionHelper`, `TypeDeserializationEntry` |
| **Recovery** | Resolution returns `null`, warning diagnostic added to resource |

Non-URI type strategies resolve type values within a **scoped context package** derived from the schema hint. Without a schema hint, resolution fails instead of scanning all registered EPackages. This prevents type confusion when multiple packages define classes with the same name or overlapping classifier IDs.

The `URI` strategy (default) is not affected — full URIs are always unambiguous **on the nsURI**; version selection under same-nsURI multi-version is a separate step (below).

> **Bounded candidate query ≠ global scan (B.5/A.3).** Selecting the *version* of a
> known nsURI (via `getPackageMetadataVersions(nsURI)`, see 06 §6.4.6) is a lookup
> **scoped to that one nsURI** — it is not the forbidden global cross-package scan. When
> that scoped query returns **more than one** version and no `codec.rootFingerprint`/pin
> disambiguates, resolution **fails with an error listing the candidate fingerprints**
> (in every strictness mode), rather than silently picking the last-registered version.

> **Deserialization requirement:** When using `NAME`, `CLASS`, or `NUMERIC` strategy, always provide a schema hint via `CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE` load option.

### 9.4 BSON Payload Size Limit

| Property | Value |
|----------|-------|
| **Constant** | `CodecOptions.CODEC_MAX_PAYLOAD_SIZE` |
| **Default** | 100 MB |
| **Scope** | BSON format provider |
| **Recovery** | `IOException` thrown before decoding |

See the Security Analysis for details.

### 9.5 Reflection Allowlist for Type Conversion

| Property | Value |
|----------|-------|
| **Constant** | `AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS` |
| **Size** | 13 types (+ 4 handled by direct code paths) |
| **Scope** | `convertObjectFromString()` in attribute deserialization |
| **Recovery** | `IllegalArgumentException` → warning diagnostic, element skipped |

When deserializing array attributes with custom component types (e.g., `Date[]`, `UUID[]`), the codec uses reflection to invoke `String` constructors or `valueOf`/`parse` static methods. Only types on the `SAFE_REFLECTION_TARGETS` allowlist are permitted. All other types are rejected with a warning diagnostic. `BigDecimal`, `BigInteger`, `UUID`, and `Date` are handled by direct code paths before the allowlist check. The allowlist contains: `URI`, `URL`, and 11 `java.time` types (`Instant`, `LocalDate`, `LocalTime`, `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime`, `Duration`, `Period`, `Year`, `YearMonth`, `MonthDay`).

### 9.6 Jackson StreamReadConstraints

| Property | Value |
|----------|-------|
| **Constant** | `CodecResource.STREAM_READ_CONSTRAINTS` |
| **Max nesting depth** | 500 (Jackson default: 1000) — backstop above codec's own 200 limit |
| **Max string length** | 10 MB (Jackson default: 20 MB) |
| **Max field name length** | 10 KB (Jackson default: 50 KB) |
| **Scope** | All JSON/YAML/CBOR/BSON parsing via `CodecResource` |
| **Recovery** | Jackson throws exception before codec processing |

The codec configures Jackson's `StreamReadConstraints` with tighter limits than the 3.1.0 defaults. These limits are applied at the Jackson parser level, providing a first line of defense before the codec's own limits (nesting depth, collection size) are checked. The constraints are applied to:
- Default JSON path (via `CodecJsonFactory` builder)
- Format provider load path (`doLoadWithFormat`)
- Format provider save path (`doSaveWithFormat`)

**CWE reference:** CWE-400 (Resource Exhaustion).

---

## 10. Option Reference Summary


| Option Key | Type | Default | Description |
|------------|------|---------|-------------|
| `codec.failFast` | `Boolean` | `false` | Throw on first error |
| `codec.suppressWarnings` | `Boolean` | `false` | Suppress all warnings |
| `codec.suppressWarningSources` | `Set<String>` | empty | Suppress warnings by source |
| `codec.diagnosticHandler` | `DiagnosticHandler` | null | Custom handler |
| `codec.deserializationMode` | `DeserializationMode` | `LENIENT` | Strictness level (see section 2.1) |

---

[Next: Annotation Reference →](16-annotation-reference.md)
