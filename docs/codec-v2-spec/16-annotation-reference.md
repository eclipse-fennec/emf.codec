# Codec V2 Annotation & Configuration Reference

[← Error Handling](15-error-handling.md) | [Next: Format Abstraction →](17-format-abstraction.md)

---

## Document Purpose

This document is the **definitive reference** for codec configuration. It covers:

1. **Configuration Resolution** - How configuration is resolved across two dimensions
2. **Annotation Keys** - Which keys are valid at each EMF element level
3. **Error Cases** - What happens with misplaced annotations
4. **API Parity** - Equivalence between EAnnotations, Properties, and Builder methods

**Relationship between configuration methods:**
```
EAnnotations ⊆ Properties ⊆ Builder Methods

- Every EAnnotation key has a Property equivalent
- Every Property has a Builder method
- Some Properties may NOT have EAnnotation equivalents (runtime-only, marked 🔧)
- Some Builder methods may be convenience wrappers
```

---

## Configuration Resolution (Two Dimensions)

Configuration resolution operates across **two orthogonal dimensions**:

### Dimension 1: Source Hierarchy (Vertical)

Configuration can be provided at different sources. **Higher priority sources override lower ones.**

| Priority | Source | Lifecycle | Configuration Methods |
|:--------:|--------|-----------|----------------------|
| 1 (highest) | **Load/Save Options** | Per-operation | Property map |
| 2 | **Resource** | Per-resource | Property map, ConfigBuilder |
| 3 | **ResourceFactory** | Per-factory | Property map, ConfigBuilder |
| 4 | **Jackson Module Config** | Per-codec | Property map, ConfigBuilder |
| 5 | **EAnnotations** | Per-model (static) | Ecore model annotations |
| 6 (lowest) | **Built-in Defaults** | Global | Hardcoded in codec |

**Key principle:** Dynamic overrides static. Runtime settings always win over model-defined settings.

```
Load/Save Options    ← dynamic, per-operation
        ↓
Resource config      ← can be set when creating resource
        ↓
ResourceFactory      ← factory-wide defaults
        ↓
Jackson Module       ← codec-wide defaults
        ↓
EAnnotations         ← static, defined in .ecore model
        ↓
Built-in Defaults    ← hardcoded fallback
        ↓
    ════════════════════════
    │ Effective Value      │
    ════════════════════════
```

### Dimension 2: Scope Chain (Horizontal)

Within any single source level, configuration can be scoped to different EMF elements. **More specific scopes override less specific ones.**

| Priority | Scope | Applies To |
|:--------:|-------|------------|
| 1 (highest) | **EReference/EAttribute** | Single feature |
| 2 | **EClass** | All instances of that class |
| 3 | **Global** | All objects in codec (with optional StrategyScope) |
| 4 (lowest) | **Default** | Built-in fallback |

```
EReference annotation    ← most specific
        ↓
EClass annotation        ← class-level default
        ↓
Global config            ← codec-wide (respects StrategyScope)
        ↓
Built-in Default         ← hardcoded fallback
        ↓
    ════════════════════════
    │ Effective Value      │
    ════════════════════════
```

### Combined Resolution

When resolving a configuration value, **both dimensions are considered**:

```
For property P on EReference R of EClass C:

1. Check Load/Save options for R.P, then C.P, then global P
2. Check Resource config for R.P, then C.P, then global P
3. Check ResourceFactory for R.P, then C.P, then global P
4. Check Jackson Module for R.P, then C.P, then global P
5. Check EAnnotation on R for P
6. Check EAnnotation on C for P
7. Check global config for P (respecting StrategyScope)
8. Use built-in default for P

First non-null value wins.
```

### StrategyScope (Global Level Modifier)

At the Global level, certain properties support **StrategyScope** to control where they apply:

| StrategyScope | Applies To |
|---------------|------------|
| `ALL` | Root + all containments + all non-containments **(default)** |
| `ROOT_ONLY` | Root object only |
| `ROOT_CONTAINMENT` | Root + objects accessed via containment references |
| `ROOT_NON_CONTAINMENT` | Root + objects accessed via non-containment references |

**Example:** Global `typeStrategy=NAME` with `typeScope=ROOT_ONLY` means:
- Root object uses NAME strategy
- All nested objects fall back to default (URI)

---

## Annotation Source

All codec annotations use the source: `http://eclipse.org/fennec/codec`

**Implementation:** See `CodecAnnotationConstants.CODEC_SOURCE`

---

## Naming Convention

Configuration can be specified via EAnnotations, property maps, or the Builder API. All three use a consistent naming scheme:

| Method | Key Format | Example |
|--------|------------|---------|
| **EAnnotation detail** | `camelCase` | `<details key="typeStrategy" value="NAME"/>` |
| **Property map** | `codec.` + `camelCase` | `codec.typeStrategy=NAME` |
| **Java constant** | `CODEC_` + `SCREAMING_SNAKE` | `CODEC_TYPE_STRATEGY = "codec.typeStrategy"` |
| **Builder method** | `.camelCase(...)` | `.typeStrategy(TypeStrategy.NAME)` |

**Key principle:** The property key is simply the EAnnotation key prefixed with `codec.`

```
EAnnotation key:  typeStrategy
Property key:     codec.typeStrategy
                  ^^^^^^
                  just add "codec." prefix
```

### Load/Save Option Keys

Load/Save options use the same convention:

| Java Constant | String Value | Description |
|---------------|--------------|-------------|
| `CODEC_ROOT_TYPE` | `"codec.rootType"` | Type hint for root object |
| `CODEC_ROOT_SCHEMA` | `"codec.rootSchema"` | Schema context for NAME strategy |
| `CODEC_FEATURE_TYPE_HINTS` | `"codec.featureTypeHints"` | Per-feature type hints |
| `CODEC_TYPE_HINT_MODE` | `"codec.typeHintMode"` | HINT or OVERRIDE |
| `CODEC_DESERIALIZATION_MODE` | `"codec.deserializationMode"` | STRICT, LENIENT, AUTO_DETECT |
| `CODEC_VALUE_READERS` | `"codec.valueReaders"` | Readers to register (uses `getName()`) |
| `CODEC_VALUE_WRITERS` | `"codec.valueWriters"` | Writers to register (uses `getName()`) |
| `CODEC_FEATURE_VALUE_READERS` | `"codec.featureValueReaders"` | Reader names per feature |
| `CODEC_FEATURE_VALUE_WRITERS` | `"codec.featureValueWriters"` | Writer names per feature |
| `CODEC_FEATURE_VALUE_READER_INSTANCES` | `"codec.featureValueReaderInstances"` | Reader instances per feature |
| `CODEC_FEATURE_VALUE_WRITER_INSTANCES` | `"codec.featureValueWriterInstances"` | Writer instances per feature |
| `CODEC_FAIL_FAST` | `"codec.failFast"` | Throw on first error (default: false) |
| `CODEC_SUPPRESS_WARNINGS` | `"codec.suppressWarnings"` | Suppress all warnings (default: false) |
| `CODEC_SUPPRESS_WARNING_SOURCES` | `"codec.suppressWarningSources"` | Suppress warnings by source |
| `CODEC_DIAGNOSTIC_HANDLER` | `"codec.diagnosticHandler"` | Custom diagnostic handler |

---

## Programmatic Configuration (Property Maps & Builders)

While EAnnotations define scope implicitly (by their location on EClass/EReference/EAttribute), property maps and builders need explicit structure to specify scope levels.

### Scope Level Keys

| Java Constant | String Value | Type | Description |
|---------------|--------------|------|-------------|
| `CODEC_ECLASS_CONFIG` | `"codec.eClassConfig"` | `Map<EClass, Map<String, Object>>` | Per-EClass configuration |
| `CODEC_EREFERENCE_CONFIG` | `"codec.eReferenceConfig"` | `Map<EReference, Map<String, Object>>` | Per-EReference configuration |
| `CODEC_EATTRIBUTE_CONFIG` | `"codec.eAttributeConfig"` | `Map<EAttribute, Map<String, Object>>` | Per-EAttribute configuration |

### Property Map Structure

```java
// Global configuration (applies to all objects, respecting StrategyScope)
Map<String, Object> options = new HashMap<>();
options.put("codec.typeStrategy", "NAME");
options.put("codec.typeKey", "_type");
options.put("codec.idKey", "_id");

// Per-EClass configuration
Map<EClass, Map<String, Object>> eClassConfig = new HashMap<>();

Map<String, Object> friendConfig = new HashMap<>();
friendConfig.put("codec.typeStrategy", "NAME");
friendConfig.put("codec.typeFormat", "STRUCTURED");
friendConfig.put("codec.typeNameKey", "friendKind");
eClassConfig.put(ExamplePackage.Literals.FRIEND, friendConfig);

Map<String, Object> addressConfig = new HashMap<>();
addressConfig.put("codec.idFormat", "STRUCTURED");
eClassConfig.put(ExamplePackage.Literals.ADDRESS, addressConfig);

options.put("codec.eClassConfig", eClassConfig);

// Per-EReference configuration
Map<EReference, Map<String, Object>> eReferenceConfig = new HashMap<>();

Map<String, Object> friendsRefConfig = new HashMap<>();
friendsRefConfig.put("codec.typeKey", "fType");
friendsRefConfig.put("codec.idKey", "friendId");
eReferenceConfig.put(ExamplePackage.Literals.PERSON__FRIENDS, friendsRefConfig);

options.put("codec.eReferenceConfig", eReferenceConfig);

// Use in load/save
resource.save(outputStream, options);
resource.load(inputStream, options);
```

### Builder API

The Builder API provides a fluent way to construct the same configuration:

```java
CodecConfiguration config = CodecConfiguration.builder()
    // Global settings
    .typeStrategy(TypeStrategy.NAME)
    .typeKey("_type")
    .idKey("_id")

    // Per-EClass settings
    .forClass(ExamplePackage.Literals.FRIEND)
        .typeStrategy(TypeStrategy.NAME)
        .typeFormat(SerializationFormat.STRUCTURED)
        .typeNameKey("friendKind")
        .end()

    .forClass(ExamplePackage.Literals.ADDRESS)
        .idFormat(SerializationFormat.STRUCTURED)
        .end()

    // Per-EReference settings
    .forReference(ExamplePackage.Literals.PERSON__FRIENDS)
        .typeKey("fType")
        .idKey("friendId")
        .end()

    .build();
```

### Typed Configuration Objects (Future)

> **Note:** This section describes a planned feature. The exact API (class names, method signatures) will be designed when implementing the builder layer.

Scope configuration maps will accept **either** raw property maps **or** typed configuration objects:

```java
Map<EReference, Object> eReferenceConfig = new HashMap<>();

// Option 1: Raw property map
Map<String, Object> config = Map.of("codec.typeKey", "fType");
eReferenceConfig.put(someReference, config);

// Option 2: Typed configuration object (future)
eReferenceConfig.put(otherReference, /* typed config from builder */);
```

**Benefits of typed configuration objects:**
- Compile-time type safety and IDE auto-completion
- Validation during build (invalid combinations caught early)
- Self-documenting, reusable across multiple features

**Benefits of raw property maps:**
- External configuration friendly (JSON/YAML parsed directly)
- Dynamic configuration where types aren't known at compile time

Both styles can be mixed in the same configuration.

---

### Value Type Flexibility

Property maps use `Map<String, Object>`, which allows flexible value types. The codec accepts multiple representations for common types:

| Expected Type | Accepted Values | Example |
|---------------|-----------------|---------|
| **Enum** (e.g., `TypeStrategy`) | Java enum literal | `TypeStrategy.NAME` |
| | String (case-insensitive) | `"NAME"` or `"name"` |
| **EClass** | EClass instance | `ExamplePackage.Literals.PERSON` |
| | EClass URI string | `"http://example.org#//Person"` |
| **Boolean** | Java boolean | `true`, `false` |
| | String | `"true"`, `"false"` (case-insensitive) |
| **Integer** | Java Integer/int | `42` |
| | String | `"42"` |
| **String** | String | `"_type"` |

```java
// All equivalent for enum:
options.put("codec.typeStrategy", TypeStrategy.NAME);
options.put("codec.typeStrategy", "NAME");
options.put("codec.typeStrategy", "name");  // case-insensitive

// All equivalent for EClass:
Map<EClass, Map<String, Object>> classConfig = new HashMap<>();
classConfig.put(ExamplePackage.Literals.PERSON, personConfig);
// or via URI string key:
Map<String, Map<String, Object>> classConfigByUri = new HashMap<>();
classConfigByUri.put("http://example.org#//Person", personConfig);

// All equivalent for boolean:
options.put("codec.serializeNull", true);
options.put("codec.serializeNull", "true");
options.put("codec.serializeNull", Boolean.TRUE);
```

**Builder methods** provide type safety with overloaded methods:

```java
CodecConfiguration.builder()
    // Type-safe enum
    .typeStrategy(TypeStrategy.NAME)
    // Or string variant (convenience)
    .typeStrategy("NAME")

    // Type-safe EClass
    .forClass(ExamplePackage.Literals.PERSON)
    // Or URI variant (for dynamic configuration)
    .forClass("http://example.org#//Person")

    // Boolean - no string variant needed (just use boolean)
    .serializeNull(true)
    .build();
```

**Resolution rules:**
1. If value is already the expected type → use directly
2. If value is String and expected type has `valueOf(String)` → convert
3. If value is String and expected type is EClass → resolve via URI
4. If value is String and expected type is Boolean → `Boolean.parseBoolean()`
5. Otherwise → throw `IllegalArgumentException` with clear message

### Resolution Example

Given the configuration above, serializing a `Friend` object accessed via `Person.friends`:

```
Resolution for typeKey:
1. Check EReference (Person.friends) → "fType" ✓ FOUND

Resolution for typeStrategy:
1. Check EReference (Person.friends) → not set
2. Check EClass (Friend) → NAME ✓ FOUND

Resolution for typeFormat:
1. Check EReference (Person.friends) → not set
2. Check EClass (Friend) → STRUCTURED ✓ FOUND

Resolution for typeNameKey:
1. Check EReference (Person.friends) → not set
2. Check EClass (Friend) → "friendKind" ✓ FOUND

Result:
{
  "fType": {              // from EReference config
    "friendKind": "Friend" // format from EClass, nameKey from EClass
  },
  "friendId": "f1",        // from EReference config
  "name": "Mark"
}
```

---

## Common Configuration Types

Several configuration types are shared across multiple features (Type, ID, SuperType, Reference). This section defines them once; specific feature sections reference back here.

### SerializationFormat

Controls **where** information is placed in the JSON structure. Used by `typeFormat`, `idFormat`, `superTypeFormat`, `refFormat`.

| Value | Output Structure | Description |
|-------|-----------------|-------------|
| `PLAIN` **(default)** | Direct field value | Information as simple value: `"_type": "Person"` |
| `STRUCTURED` | Nested object | Information as object with multiple fields: `"_type": { "type": "Person" }` |

**PLAIN format** - compact, common for simple cases:
```json
{
  "_type": "http://example.org#//Person",
  "_id": "123",
  "name": "John"
}
```

**STRUCTURED format** - richer metadata, multiple related fields grouped:
```json
{
  "_type": {
    "schema": "http://example.org",
    "type": "Person",
    "supertype": ["Entity"]
  },
  "_id": {
    "value": "123",
    "separator": "-"
  },
  "name": "John"
}
```

**When to use STRUCTURED:**
- When you need schema + type as separate fields
- When supertype information should be included in the same object
- When ID is composed of multiple parts with metadata
- For richer metadata in any field

**Underscore convention:**
- **PLAIN:** Metadata fields use `_` prefix at root level (`_type`, `_id`)
- **STRUCTURED:** Outer key has `_` prefix, inner keys don't need it (they're inside a metadata object)

> **Detailed examples:** See [Type Serialization](06-type.md) (section 1.1-1.2) for type format examples, [ID Serialization](09-id.md) for ID format examples.

### StrategyScope

Controls **where** a strategy or format applies within the object hierarchy. Used by `typeScope`, `typeFormatScope`, `idScope`, `idFormatScope`.

| Value | Applies To | Use Case |
|-------|------------|----------|
| `ALL` **(default)** | Root + all containments + all non-containments | Uniform handling everywhere |
| `ROOT_ONLY` | Root object only, children use defaults | SCHEMA_AND_TYPE at root, URI for nested |
| `ROOT_CONTAINMENT` | Root + objects via containment references | Different handling for non-containment refs |
| `ROOT_NON_CONTAINMENT` | Root + objects via non-containment references | Different handling for containment refs |

**Key principle:** Strategy and Format have **independent scopes**. This enables powerful combinations:

```java
CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.SCHEMA_AND_TYPE)
    .typeScope(StrategyScope.ROOT_ONLY)           // Strategy at root only
    .typeFormat(SerializationFormat.STRUCTURED)
    .typeFormatScope(StrategyScope.ROOT_CONTAINMENT)  // Format at root + containments
    .build();
```

| Object Level | Strategy | Format |
|--------------|----------|--------|
| Root | SCHEMA_AND_TYPE | STRUCTURED |
| Containment | URI (fallback) | STRUCTURED |
| Non-containment | URI (fallback) | PLAIN (fallback) |

> **Detailed examples:** See [18-scenarios.md](18-scenarios.md) for comprehensive configuration examples.

**Note:** StrategyScope is **runtime-only** (🔧) - no EAnnotation equivalent: a scope says *where* in an object graph a setting applies, which is a property of the operation rather than of the model. See [Type Configuration](#type-configuration) below for the full reasoning, and [99-open-questions.md Q1](99-open-questions.md#q1-epackage-scope-level) for the related package-level question.

---

## Quick Reference Matrix

### Legend
- ✅ = Valid at this level (has EAnnotation, Property, AND Builder support)
- 🔧 = Property/Builder only (no EAnnotation - runtime configuration)
- ❌ = Not valid at this level
- 🔶 = Valid but with limitations (see notes)

### Scope Levels
| Level | EMF Element | Example |
|-------|-------------|---------|
| **Global** | Codec configuration | `CodecConfiguration.builder()...` |
| **EPackage** | `EPackage` | Annotation on `<ecore:EPackage>` — defaults for every class in the package |
| **EClass** | `EClassifier` | Annotation on `<eClassifiers>` |
| **EReference** | `EReference` | Annotation on containment/non-containment reference |
| **EAttribute** | `EAttribute` | Annotation on attribute |
| **EStructuralFeature** | Both EReference and EAttribute | Common feature config |

> **EPackage-level annotations are package-wide defaults** (issue #75). An annotation on the
> `EPackage` configures every class in it; a class then states its exceptions on top, **per
> property**, so overriding one key does not reset the package's others.
>
> This level is **annotation-internal**: it is merged into each class's annotation configuration
> when the package profile is built, so the vertical source hierarchy still sees exactly one
> annotation layer — see [02 §3.1](02-config-resolution.md#31-the-epackage-scope-is-annotation-internal).
>
> Two boundaries: resolution uses the **directly owning** package only (subpackage hierarchies are
> not traversed anywhere in the codec), and **scopes do not participate** — see the reasoning under
> [Type Configuration](#type-configuration) below.
>
> Typical use is configuration that is conceptually per-package. `fingerprintMode` /
> `fingerprintKey` ([06 §8.7](06-type.md#87-configuration)) are the motivating example: the
> fingerprint's currency *is* the `EPackage`.

---

## Type Configuration

Type configuration describes how type information is serialized/deserialized.

> **Detailed documentation:** See [06-type.md](06-type.md) for complete type serialization specification including format/strategy examples, deserialization behavior, and real-world use cases (GeoJSON).

| Annotation Key | Property Key | Global | EPkg | EClass | ERef | EAttr | Description |
|----------------|--------------|:------:|:----:|:------:|:----:|:-----:|-------------|
| `typeStrategy` | `codec.typeStrategy` | ✅ | ✅ | ✅ | ✅ | ❌ | Type identification strategy (see [TypeStrategy](#typestrategy-values) below) |
| `typeFormat` | `codec.typeFormat` | ✅ | ✅ | ✅ | ✅ | ❌ | Output format (see [SerializationFormat](#serializationformat)) |
| `typeKey` | `codec.typeKey` | ✅ | ✅ | ✅ | ✅ | ❌ | Outer JSON key (**default:** `_type`) |
| `typeNameKey` | `codec.typeNameKey` | ✅ | ✅ | ✅ | ✅ | ❌ | Inner type name key in STRUCTURED (**default:** `type`) |
| `typeSchemaKey` | `codec.typeSchemaKey` | ✅ | ✅ | ✅ | ✅ | ❌ | Inner schema key in STRUCTURED (**default:** `schema`) |
| `typeValueReaderName` | `codec.typeValueReaderName` | ✅ | ❌ | ✅ | ❌ | ❌ | Custom value reader service name |
| `typeValueWriterName` | `codec.typeValueWriterName` | ✅ | ❌ | ✅ | ❌ | ❌ | Custom value writer service name |
| `fingerprintMode` | `codec.fingerprintMode` | ✅ | ✅ | ✅ | ❌ | ❌ | Opt-in for writing the in-band EPackage fingerprint: `NONE` (**default**) or `FIRST_TOUCH` (see [06-type.md §8](06-type.md#8-in-band-epackage-fingerprint)) |
| `fingerprintKey` | `codec.fingerprintKey` | ✅ | ✅ | ✅ | ❌ | ❌ | Key carrying the fingerprint (**default:** `fingerprint`, PLAIN sibling `_fingerprint`). **Write-only as an annotation** — see below |

> **⚠ `fingerprintKey` as an annotation configures writing only.** A reader must know the
> key *before* it can select the model version, but an annotation-configured key lives in
> the configuration that only exists *after* that selection — a genuine cycle. It is broken
> by taking the read key from caller-side sources only (options, resource, factory, module),
> while always additionally accepting the default key. A document written with a custom,
> annotation-configured key is therefore readable only by a caller who supplies that key
> out-of-band. Full rationale: [06-type.md §8.5](06-type.md#85--the-fingerprintkey-chicken-and-egg-problem).

> **No `fingerprintInclude` flag.** On/off is expressed through `fingerprintMode=NONE`,
> deliberately mirroring `typeStrategy=NONE` — a parallel boolean is the pattern that made
> `typeInclude` deprecated (T-V30/T-V31).
| — | `codec.typeScope` | 🔧 | ❌ | ❌ | ❌ | ❌ | Strategy scope (see [StrategyScope](#strategyscope)) |
| — | `codec.typeFormatScope` | 🔧 | ❌ | ❌ | ❌ | ❌ | Format scope (see [StrategyScope](#strategyscope)) |

> **Why `typeScope`/`typeFormatScope` are runtime-only:** these properties control *where* a strategy applies (`ROOT_ONLY`, `ROOT_CONTAINMENT`, …), which is a property of the *operation*, not of the model. A model cannot know whether one of its classes will be somebody's root or somebody else's nested content — the same class is both, in different documents.
>
> Cross-package references make that decisive rather than merely untidy: "root" and "containment" are positions in a document, and a document assembled from several packages has exactly one root, which at most one of those packages owns. A package-level scope would therefore have to state something about positions outside its own model. That is why scopes are also answered with "no" for a future package level — see [99-open-questions.md Q1](99-open-questions.md#q1-epackage-scope-level).

> **Recommendation:** Use keys that start with `_` or `@` (like `_type`, `@type`) for `typeKey`, `typeSchemaKey`, and `typeNameKey`. This helps distinguish metadata from data fields and prevents collision with ordinary data keys during deserialization.

**Implementation:** `CodecAnnotationConstants.KEY_TYPE_*`

### TypeStrategy Values

Values for the `typeStrategy` annotation key. These control **what information** is written to identify the object's type.

| Value | Serialization Output | Deserialization Requirement |
|-------|---------------------|----------------------------|
| `URI` **(default)** | Full EClass URI: `"http://example.org#//Person"` | Self-describing, resolvable via EPackage registry |
| `NAME` | Simple EClass name: `"Person"` | Requires schema context (`CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE`) |
| `CLASS` | Java class name: `"org.example.PersonImpl"` | **Requires** `EClass.getInstanceClassName()` to be set (ERROR if null) |
| `SCHEMA_AND_TYPE` | Two fields: `"_schema": "...", "_type": "Person"` | Schema + name combined |
| `NUMERIC` | EMF classifier ID: `"3"` | **Requires** `CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE` - IDs are package-specific |
| `NONE` | No type field written | `CODEC_ROOT_TYPE` required, or reference must be concrete |

> **Note:** `MAPPED` was removed from TypeStrategy. Discriminator-based type resolution is now a separate optional layer. See [Discriminator Mapping Configuration](#discriminator-mapping-configuration) below.

### Invalid Configurations

| Misconfiguration | Severity | Reason |
|------------------|----------|--------|
| `typeValueReaderName` on EReference | ERROR | Value reader/writer is class-intrinsic |
| `typeValueWriterName` on EReference | ERROR | Value reader/writer is class-intrinsic |
| `typeScope` via EAnnotation | WARNING | Runtime-only (🔧), annotation is ignored |
| `typeFormatScope` via EAnnotation | WARNING | Runtime-only (🔧), annotation is ignored |
| Any `type*` key on EAttribute | ERROR | Type config not applicable to attributes |

### Annotation-Only Directive: `inherit`

The `inherit` key controls how codec annotations are inherited across the EClass hierarchy. It is an **annotation-only directive** - it does NOT have a property equivalent and is consumed during annotation parsing.

| Annotation Key | Global | EClass | Default | Description |
|----------------|:------:|:------:|---------|-------------|
| `inherit` | ✅ | ✅ | `DIRECT` | Annotation inheritance level |

**Values:**

| Value | Description |
|-------|-------------|
| `DIRECT` **(default)** | Inherit from immediate parent EClass only |
| `ALL` | Inherit from full hierarchy up to EObject |
| `NONE` | No inheritance, use only this EClass's annotations |

**Example:**
```xml
<eClassifiers xsi:type="ecore:EClass" name="Employee" eSuperTypes="#//Person">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="inherit" value="ALL"/>
  </eAnnotations>
</eClassifiers>
```

> **Note:** `inherit` affects how `CodecAspectProvider` resolves annotations when building AspectConfig. It is NOT a runtime property and does NOT participate in the property resolution matrix. See [12-polymorphism.md](12-polymorphism.md#2-annotation-inheritance-levels) for details.

---

## SuperType Configuration

SuperType describes class inheritance hierarchy. This is an **extension of Type** - it follows Type's format and scope settings.

> **Detailed documentation:** See [07-supertype.md](07-supertype.md) for complete supertype serialization specification.

| Annotation Key | Property Key | Global | EPkg | EClass | ERef | EAttr | Description |
|----------------|--------------|:------:|:----:|:------:|:----:|:-----:|-------------|
| `superTypeSerialize` | `codec.superTypeSerialize` | ✅ | ✅ | ✅ | ❌ | ❌ | Enable supertype serialization (**default:** `false`) |
| `superTypeKey` | `codec.superTypeKey` | ✅ | ✅ | ✅ | ❌ | ❌ | JSON key (**default:** format-dependent, see below) |
| `superTypeStrategy` | `codec.superTypeStrategy` | ✅ | ✅ | ✅ | ❌ | ❌ | Which supertypes to include (see below) |
| `superTypeAsArray` | `codec.superTypeAsArray` | ✅ | ✅ | ✅ | ❌ | ❌ | Array vs string (**default:** `true`) |
| `superTypeSeparator` | `codec.superTypeSeparator` | ✅ | ✅ | ✅ | ❌ | ❌ | Separator for string (**default:** `,`) |
| `superTypeFormat` | `codec.superTypeFormat` | ✅ | ✅ | ✅ | ❌ | ❌ | Output format (see [SerializationFormat](#serializationformat)) |
| `superTypeValueReaderName` | `codec.superTypeValueReaderName` | ✅ | ❌ | ✅ | ❌ | ❌ | Custom value reader service name |
| `superTypeValueWriterName` | `codec.superTypeValueWriterName` | ✅ | ❌ | ✅ | ❌ | ❌ | Custom value writer service name |

> **Note:** `superTypeSchemaKey` removed - SuperType inherits `schemaKey` from TypeConfig.
> **Note:** `superTypeNameKey` removed - `superTypeKey` has format-dependent default:
> - **PLAIN format:** `_supertype` (underscore prefix for root-level metadata)
> - **STRUCTURED format:** `supertype` (no underscore inside `_type` object)

**Implementation:** `CodecAnnotationConstants.KEY_SUPERTYPE_*`

### SuperTypeStrategy Values

Values for the `superTypeStrategy` annotation key. These control **which supertypes** to include in the output.

| Value | Description |
|-------|-------------|
| `ALL` **(default)** | All supertypes from the model (excludes EMF internal types like `EObject`) |
| `ALL_EMF` | All supertypes including EMF internal types (`EObject`, `EModelElement`, etc.) |
| `SINGLE` | Only the direct (first) supertype - the immediate parent class |
| `NONE` | No supertypes (same as `superTypeSerialize=false`) |

**Example:** Given `Employee extends Person extends Entity`:

| Strategy | Output |
|----------|--------|
| `ALL` | `["Person", "Entity"]` |
| `ALL_EMF` | `["Person", "Entity", "EObject"]` |
| `SINGLE` | `["Person"]` (only direct parent) |
| `NONE` | (no supertype field) |

**Why NOT on EReference:** SuperType describes the CLASS inheritance, not how it's accessed. A `Person` has the same supertypes whether accessed via `company.employees` or `order.customer`.

**Inherits from Type:**
- Uses `typeScope` (property: `codec.typeScope`) for scope control
- Uses `typeFormat` for PLAIN/STRUCTURED when `superTypeFormat` not explicitly set
- Uses `typeFormatScope` for format scope

### Invalid Configurations

| Misconfiguration | Severity | Reason |
|------------------|----------|--------|
| Any `superType*` key on EReference | ERROR | SuperType is class-intrinsic, not reference-specific |
| Any `superType*` key on EAttribute | ERROR | SuperType not applicable to attributes |

---

## Discriminator Mapping Configuration

Discriminator mapping provides type resolution via field values. This is a **separate layer** from TypeStrategy - it works alongside any strategy to translate arbitrary discriminator values to concrete EClasses.

> **Full documentation:** See [Discriminator Mapping](08-discriminator-mapping.md) for complete specification including Type Mapping Registry, Inline Mapping, and fallback handling.

**When to use discriminator mapping:**
- **IoT/LoRaWAN devices** - device type embedded in payload metadata (e.g., `info.profileName`)
- **JSON Schema oneOf patterns** - type determined by feature presence or values
- **Legacy APIs** - type information encoded in application-specific fields
- **External APIs** - where you cannot control the type field name or format

### Annotation Sources

Discriminator mappings use **dedicated annotation sources** (not the main `http://eclipse.org/fennec/codec` source):

| Feature | Annotation Source | Applies To |
|---------|------------------|------------|
| Type Mapping | `http://eclipse.org/fennec/codec/typeMapping/{mapId}` | EClass |
| Inline Mapping | `http://eclipse.org/fennec/codec/inlineMapping` | EReference |

### Properties

| Annotation Key | Property Key | EClass | ERef | Description |
|----------------|--------------|:------:|:----:|:----:|-------------|
| — | `codec.typeMapId` | ✅ | ❌ | Registry ID (embedded in annotation source URI) |
| `typeDiscriminatorPath` | `codec.typeDiscriminatorPath` | ✅ | ❌ | JSON path to discriminator value |
| `{value}` | — | ✅ | ✅ | Mapping entries as direct key/value details |
| — | `codec.typeMappings` | ✅ | ❌ | Mappings as nested Map (for EClass property config) |
| — | `codec.inlineMappings` | ❌ | ✅ | Mappings as nested Map (for EReference property config) |
| `typeDiscriminator` | `codec.typeDiscriminator` | ✅ | ❌ | This class's discriminator value (distributed registration) |
| `fallbackStrategy` | `codec.fallbackStrategy` | ✅ | ✅ | `ERROR`, `SKIP`, `FALLBACK` (**default:** `SKIP`) |
| `fallbackEClass` | `codec.fallbackEClass` | ✅ | ✅ | Explicit fallback EClass URI |

**Implementation:** `CodecAnnotationConstants.KEY_TYPE_MAP_ID`, `KEY_TYPE_DISCRIMINATOR`, `KEY_TYPE_DISCRIMINATOR_PATH`, `KEY_INLINE_MAPPINGS`, `ANNOTATION_SOURCE_TYPE_MAPPING_PREFIX`, `ANNOTATION_SOURCE_INLINE_MAPPING`

**Resolution priority during deserialization:**
1. **Type Mapping Registry** (if configured on EClass) - highest priority
2. **Inline Mapping** (if configured on the EReference)
3. **Type Strategy Resolution** (URI, NAME, etc.)
4. **Fallback** (reference type, `CODEC_FEATURE_TYPE_HINTS`, `CODEC_ROOT_TYPE`)

### Fallback and Error Handling

When a discriminator value cannot be resolved to an EClass, the behavior is controlled by `fallbackStrategy` and `fallbackEClass`.

| Detail Key | Values | Default | Description |
|------------|--------|---------|-------------|
| `fallbackStrategy` | `ERROR`, `SKIP`, `FALLBACK` | `SKIP` | What to do when discriminator value not found |
| `fallbackEClass` | EClass URI | — | Explicit fallback type (required when strategy is `FALLBACK`) |

**FallbackStrategy values:**

| Value | Behavior |
|-------|----------|
| `SKIP` **(default)** | Log WARNING, continue to next resolution step (Type Strategy) |
| `ERROR` | Fail immediately, throw exception |
| `FALLBACK` | Use `fallbackEClass` (MUST be set, else ERROR) |

**Fallback behavior:**

```
When discriminator value not found in mappings:

1. Check fallbackStrategy (default: SKIP):
   - ERROR    → Fail immediately
   - SKIP     → WARNING, continue to next resolution step (Type Strategy)
   - FALLBACK → Use fallbackEClass:
                 - If fallbackEClass is set → use it, RESOLVED ✓
                 - If fallbackEClass is NOT set → ERROR (misconfiguration)
```

**Note:** `SKIP` is the default because it enables graceful continuation to Type Strategy resolution, which then has its own fallback chain (reference type, CODEC_FEATURE_TYPE_HINTS, CODEC_ROOT_TYPE). Use `FALLBACK` only when you want to explicitly specify a fallback type without continuing to Type Strategy.

**Relationship to Feature Type Hints:**

The `CODEC_FEATURE_TYPE_HINTS` load option (see [Per-Feature Type Hints](#per-feature-type-hints-codec_feature_type_hints)) integrates with inline mapping fallback resolution. This allows runtime control over fallback types without modifying the model:

```java
// Model defines inline mapping but no fallbackEClass
// At runtime, provide fallback via feature type hint
Map<EStructuralFeature, EClass> hints = Map.of(
    PersonPackage.Literals.PERSON__CONTACTS, ContactPackage.Literals.GENERIC_CONTACT
);
options.put(CODEC_FEATURE_TYPE_HINTS, hints);
```

**Priority:** `fallbackEClass` (model/config) takes precedence over `CODEC_FEATURE_TYPE_HINTS` (runtime).

**Examples:**

```xml
<!-- Type Mapping with default SKIP behavior -->
<!-- If discriminator value not found → WARNING, continue to Type Strategy -->
<eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
  <details key="typeDiscriminatorPath" value="info.profileName"/>
  <!-- fallbackStrategy defaults to SKIP -->
  <details key="temp-sensor" value="http://example.org#//TemperatureSensor"/>
  <details key="humidity-sensor" value="http://example.org#//HumiditySensor"/>
</eAnnotations>

<!-- Type Mapping with explicit fallback EClass -->
<!-- Unknown discriminator → use GenericMessage -->
<eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
  <details key="typeDiscriminatorPath" value="info.profileName"/>
  <details key="fallbackStrategy" value="FALLBACK"/>
  <details key="fallbackEClass" value="http://example.org#//GenericMessage"/>
  <details key="temp-sensor" value="http://example.org#//TemperatureSensor"/>
  <details key="humidity-sensor" value="http://example.org#//HumiditySensor"/>
</eAnnotations>

<!-- Inline Mapping with ERROR (fail fast, strict mode) -->
<eAnnotations source="http://eclipse.org/fennec/codec/inlineMapping">
  <details key="fallbackStrategy" value="ERROR"/>
  <details key="friend" value="http://example.org#//Friend"/>
</eAnnotations>

<!-- Inline Mapping with default SKIP behavior -->
<!-- Unknown value → WARNING, continue to Type Strategy resolution -->
<eAnnotations source="http://eclipse.org/fennec/codec/inlineMapping">
  <details key="friend" value="http://example.org#//Friend"/>
  <details key="enemy" value="http://example.org#//Enemy"/>
</eAnnotations>
```

**Property keys for programmatic configuration:**

| Property Key | Description |
|--------------|-------------|
| `codec.fallbackStrategy` | `ERROR`, `SKIP`, `FALLBACK` |
| `codec.fallbackEClass` | EClass URI string or EClass instance |
| `codec.typeMappings` | `Map<String, String>` or `Map<String, EClass>` (EClass level) |
| `codec.inlineMappings` | `Map<String, String>` or `Map<String, EClass>` (EReference level) |

**Programmatic Inline Mapping Example:**

```java
// Per-EReference configuration with inline mappings
Map<EReference, Map<String, Object>> eReferenceConfig = new HashMap<>();

Map<String, Object> contactsConfig = Map.of(
    "codec.typeKey", "contactType",
    "codec.inlineMappings", Map.of(
        "friend", "http://example.org#//Friend",
        "enemy", "http://example.org#//Enemy",
        "colleague", ExamplePackage.Literals.COLLEAGUE  // EClass instance also works
    ),
    "codec.fallbackStrategy", "SKIP"
);
eReferenceConfig.put(PersonPackage.Literals.PERSON__CONTACTS, contactsConfig);

options.put("codec.eReferenceConfig", eReferenceConfig);
```

### Type Mapping Registry (on EClass)

The type mapping registry defines discriminator-to-EClass mappings using a dedicated annotation source that includes the registry ID.

**Annotation source:** `http://eclipse.org/fennec/codec/typeMapping/{mapId}`

The `{mapId}` is embedded in the source URI, making it structurally impossible to forget.

#### Centralized Configuration (Static Mappings)

Define all mappings on the base class. Useful when you control all concrete classes and want the mapping in one place.

**How it works:**
1. **Base class** uses annotation source with `{mapId}` and defines `typeDiscriminatorPath` + all mappings
2. Mappings are direct key/value details (key = discriminator value, value = EClass URI)
3. Concrete classes don't need any annotations

```xml
<!-- UplinkMessage is the abstract base class -->
<eClassifiers name="UplinkMessage">
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <!-- JSON path where discriminator value is found (dot notation for nested) -->
    <details key="typeDiscriminatorPath" value="info.profileName"/>
    <!-- Mappings: discriminator value → EClass URI -->
    <details key="temp-sensor" value="http://example.org#//TemperatureSensor"/>
    <details key="humidity-sensor" value="http://example.org#//HumiditySensor"/>
    <details key="pressure-sensor" value="http://example.org#//PressureSensor"/>
  </eAnnotations>
</eClassifiers>
```

**Deserialization example:**
```json
{
  "info": {
    "profileName": "temp-sensor",
    "timestamp": "2026-01-23T10:00:00Z"
  },
  "value": 23.5
}
```
→ Deserializer reads `info.profileName` = `"temp-sensor"` → looks up in `lorawan-devices` registry → resolves to `TemperatureSensor`

#### Distributed Configuration (Self-Registration)

Concrete classes can register themselves with a registry. Useful when concrete classes are in different packages or when you want extensible type hierarchies.

**How it works:**
1. **Base class** defines the registry with `typeDiscriminatorPath` using the `typeMapping/{mapId}` annotation source
2. **Concrete classes** register themselves using the **same annotation source** with `typeDiscriminator`
3. At runtime, `TypeDiscriminatorService` maintains the registry and resolves types

The mapId is always embedded in the annotation source URI — both base and concrete classes use the same source, ensuring consistency.

**Base class configuration:**
```xml
<eClassifiers name="UplinkMessage">
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <details key="typeDiscriminatorPath" value="info.profileName"/>
  </eAnnotations>
</eClassifiers>
```

**Concrete class self-registration:**
```xml
<!-- TemperatureMessage extends UplinkMessage -->
<eClassifiers name="TemperatureMessage">
  <!-- Same annotation source as the base class — mapId "lorawan-devices" is in the URI -->
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <details key="typeDiscriminator" value="temperature-profile"/>
  </eAnnotations>
</eClassifiers>
```

#### Combining Static and Distributed Configuration

Static mappings and distributed registration **can be combined** in the same registry. This enables extensible type hierarchies where the base package defines known types, and extension packages can add new types without modifying the base.

**Base class with static mappings:**
```xml
<eClassifiers name="UplinkMessage">
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <details key="typeDiscriminatorPath" value="info.profileName"/>
    <!-- Known types defined statically -->
    <details key="temp-sensor" value="http://example.org#//TemperatureSensor"/>
    <details key="humidity-sensor" value="http://example.org#//HumiditySensor"/>
  </eAnnotations>
</eClassifiers>
```

**Extension class registering dynamically (e.g., from another package):**
```xml
<!-- FooMessage extends UplinkMessage, defined in a different package -->
<eClassifiers name="FooMessage">
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <details key="typeDiscriminator" value="foo-bar"/>
  </eAnnotations>
</eClassifiers>
```

**Result:** The `lorawan-devices` registry contains:
- `temp-sensor` → `TemperatureSensor` (from static mapping)
- `humidity-sensor` → `HumiditySensor` (from static mapping)
- `foo-bar` → `FooMessage` (from distributed registration)

#### Programmatic Configuration

**Builder API:**
```java
ClassConfigBuilder.forEClass(ExamplePackage.Literals.UPLINK_MESSAGE)
    .typeMapId("lorawan-devices")
    .typeDiscriminatorPath("info.profileName")
    .addDiscriminatorMapping("temp-sensor", TemperaturePackage.Literals.TEMPERATURE_SENSOR)
    .addDiscriminatorMapping("humidity-sensor", HumidityPackage.Literals.HUMIDITY_SENSOR)
    .build();
```

**Property Map:**
```java
Map<String, Object> config = Map.of(
    "codec.typeMapId", "lorawan-devices",
    "codec.typeDiscriminatorPath", "info.profileName",
    "codec.typeMappings", Map.of(
        "temp-sensor", "http://example.org#//TemperatureSensor",
        "humidity-sensor", "http://example.org#//HumiditySensor"
    )
);
```

### Inline Mapping (on EReference)

Inline mapping defines value→EClass mappings directly on an EReference using a dedicated annotation source. This is the simplest approach for small, reference-specific mappings.

**Annotation source:** `http://eclipse.org/fennec/codec/inlineMapping`

**How it works:**
1. **Main codec annotation** defines the type key (`typeKey`)
2. **Inline mapping annotation** defines mappings as direct key/value details
3. Only applies to objects accessed through this specific reference
4. Uses the same [Fallback and Error Handling](#fallback-and-error-handling) as Type Mapping Registry

```xml
<eStructuralFeatures name="contacts" upperBound="-1" eType="#//Contact">
  <!-- Type key in main codec annotation -->
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="typeKey" value="contactType"/>
  </eAnnotations>
  <!-- Mappings in dedicated annotation -->
  <eAnnotations source="http://eclipse.org/fennec/codec/inlineMapping">
    <details key="friend" value="http://example.org#//Friend"/>
    <details key="enemy" value="http://example.org#//Enemy"/>
    <details key="colleague" value="http://example.org#//Colleague"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Deserialization example:**
```json
{
  "name": "John",
  "contacts": [
    { "contactType": "friend", "name": "Alice" },
    { "contactType": "enemy", "name": "Bob" },
    { "contactType": "colleague", "name": "Carol" }
  ]
}
```
→ Each contact's `contactType` is looked up in inline mapping → resolves to `Friend`, `Enemy`, `Colleague` respectively

**Error handling:** Inline mappings support `fallbackStrategy` and `fallbackEClass` just like Type Mapping Registry. If a discriminator value is not found in the mapping, the fallback resolution chain applies:
1. Check `fallbackStrategy` (ERROR/SKIP/FALLBACK)
2. Use `fallbackEClass` if defined
3. Use `CODEC_FEATURE_TYPE_HINTS` if set for this reference
4. Use reference type if concrete

See [Fallback and Error Handling](#fallback-and-error-handling) above for detailed examples.

### Invalid Configurations

| Misconfiguration | Severity | Reason |
|------------------|----------|--------|
| `typeDiscriminator` on EReference | ERROR | Discriminator values are per-class, not per-reference |
| `typeDiscriminatorPath` on EReference | ERROR | Discriminator path is defined on base class |
| `typeMapping/{mapId}` annotation on EReference | ERROR | Type mapping registry is class-level |
| `inlineMapping` annotation on EClass | WARNING | Inline mappings are per-reference only |

---

## ID Configuration

ID configuration describes how an object identifies itself.

> **Detailed documentation:** See [09-id.md](09-id.md) for complete ID serialization specification.

| Annotation Key | Property Key | Global | EPkg | EClass | ERef | EAttr | Description |
|----------------|--------------|:------:|:----:|:------:|:----:|:-----:|-------------|
| `idStrategy` | `codec.idStrategy` | ✅ | ✅ | ✅ | ❌ | ❌ | ID building strategy (see [IdStrategy](#idstrategy-values) below) |
| `idFormat` | `codec.idFormat` | ✅ | ✅ | ✅ | ✅ | ❌ | Output format (see [SerializationFormat](#serializationformat)) |
| `idKey` | `codec.idKey` | ✅ | ✅ | ✅ | ✅ | ❌ | Outer JSON key (**default:** `_id`) |
| `idValueKey` | `codec.idValueKey` | ✅ | ✅ | ✅ | ❌ | ❌ | Inner key in STRUCTURED (**default:** `id`) |
| `idFeatures` | `codec.idFeatures` | ❌ | ✅ | ✅ | ❌ | ❌ | Comma-separated feature names for COMBINED |
| `idSeparator` | `codec.idSeparator` | ✅ | ✅ | ✅ | ❌ | ❌ | Separator for COMBINED (**default:** `-`) |
| `idSeparatorKey` | `codec.idSeparatorKey` | ✅ | ✅ | ✅ | ❌ | ❌ | Key for separator field (**default:** `separator`) |
| `idSeparatorSerialize` | `codec.idSeparatorSerialize` | ✅ | ❌ | ✅ | ❌ | ❌ | Include separator in STRUCTURED (**default:** `true`) |
| `idKeyMode` | `codec.idKeyMode` | ✅ | ✅ | ✅ | ❌ | ❌ | ID_ONLY, BOTH, or FEATURE_ONLY (see below) |
| `idOnTop` | `codec.idOnTop` | ✅ | ✅ | ✅ | ❌ | ❌ | ID before type in output (**default:** `true`) |
| `idValueReaderName` | `codec.idValueReaderName` | ✅ | ✅ | ✅ | ❌ | ❌ | Custom value reader service name |
| `idValueWriterName` | `codec.idValueWriterName` | ✅ | ✅ | ✅ | ❌ | ❌ | Custom value writer service name |
| — | `codec.idScope` | 🔧 | ❌ | ❌ | ❌ | ❌ | Strategy scope (see [StrategyScope](#strategyscope)) |
| — | `codec.idFormatScope` | 🔧 | ❌ | ❌ | ❌ | ❌ | Format scope (see [StrategyScope](#strategyscope)) |

**Implementation:** `CodecAnnotationConstants.KEY_ID_*`

### IdStrategy Values

Values for the `idStrategy` annotation key. These control **how** the object's ID value is built.

| Value | Description |
|-------|-------------|
| `ID_FIELD` **(default)** | Use the attribute marked with `eID="true"` (at most one per EClass) |
| `COMBINED` | Combine `idFeatures` with `idSeparator` |

### IdKeyMode Values

Values for the `idKeyMode` annotation key. These control **what** to serialize.

| Value | `_id` field | ID features |
|-------|:-----------:|:-----------:|
| `ID_ONLY` **(default)** | ✅ | ❌ |
| `BOTH` | ✅ | ✅ |
| `FEATURE_ONLY` | ❌ | ✅ |
| `NONE` | ❌ | ❌ |

> **Note:** When `IdKeyMode=NONE`, the `IdStrategy` is ignored - no ID serialization happens at all.

### PLAIN Format Examples

| IdStrategy | IdKeyMode | Output |
|------------|-----------|--------|
| ID_FIELD | ID_ONLY | `{ "_id": "maho", "name": "Mark" }` |
| ID_FIELD | BOTH | `{ "_id": "maho", "user": "maho", "name": "Mark" }` |
| ID_FIELD | FEATURE_ONLY | `{ "user": "maho", "name": "Mark" }` |
| ID_FIELD | NONE | `{ "name": "Mark" }` |
| COMBINED | ID_ONLY | `{ "_id": "maho-123", "name": "Mark" }` |
| COMBINED | BOTH | `{ "_id": "maho-123", "user": "maho", "userNum": "123", "name": "Mark" }` |
| COMBINED | FEATURE_ONLY | `{ "user": "maho", "userNum": "123", "name": "Mark" }` |
| COMBINED | NONE | `{ "name": "Mark" }` |

### STRUCTURED Format Examples

In STRUCTURED format (`idFormat=STRUCTURED`), `IdKeyMode` controls what goes **inside** the `_id` object.

The inner key for the combined/single ID value is configurable via `idValueKey` (default: `id`).

**ID_FIELD + ID_ONLY:**
```json
{
  "_id": {
    "id": "maho"
  },
  "name": "Mark"
}
```

**ID_FIELD + BOTH:**
```json
{
  "_id": {
    "id": "maho",
    "user": "maho"
  },
  "name": "Mark"
}
```

**ID_FIELD + FEATURE_ONLY:**
```json
{
  "_id": {
    "user": "maho"
  },
  "name": "Mark"
}
```

**ID_FIELD + NONE:**
```json
{
  "name": "Mark"
}
```

**COMBINED + ID_ONLY:**
```json
{
  "_id": {
    "id": "maho-123",
    "separator": "-"
  },
  "name": "Mark"
}
```

**COMBINED + BOTH:**
```json
{
  "_id": {
    "id": "maho-123",
    "user": "maho",
    "userNum": "123",
    "separator": "-"
  },
  "name": "Mark"
}
```

**COMBINED + FEATURE_ONLY:**
```json
{
  "_id": {
    "user": "maho",
    "userNum": "123",
    "separator": "-"
  },
  "name": "Mark"
}
```

**COMBINED + NONE:**
```json
{
  "name": "Mark"
}
```

> **Edge cases:** For more complex scenarios (e.g., features both inside `_id` AND at top-level), use custom `idValueReaderName` / `idValueWriterName`.

**Why `idStrategy` NOT on EReference:** An object's identity is intrinsic - a `Person` has the same ID whether accessed via `company.employees` or `order.customer`.

**Why `idFormat/idKey` CAN be on EReference:** Presentation of the ID can vary by context (e.g., different API conventions).

### Invalid Configurations

| Misconfiguration | Severity | Reason |
|------------------|----------|--------|
| `idStrategy` on EReference | ERROR | ID strategy is class-intrinsic |
| `idFeatures` on EReference | ERROR | ID features are class-specific |
| `idSeparator` on EReference | ERROR | Separator is class-specific |
| `idSeparatorKey` on EReference | ERROR | Separator config is class-specific |
| `idSeparatorSerialize` on EReference | ERROR | Separator config is class-specific |
| `idKeyMode` on EReference | ERROR | Key mode is class-intrinsic |
| `idValueKey` on EReference | ERROR | Inner key is class-specific |
| `idOnTop` on EReference | ERROR | Field ordering is class-specific |
| `idValueReaderName` on EReference | ERROR | Value reader/writer is class-intrinsic |
| `idValueWriterName` on EReference | ERROR | Value reader/writer is class-intrinsic |
| `idScope` via EAnnotation | WARNING | Scope is runtime-only (🔧) — ignored, not harmful |
| `idFormatScope` via EAnnotation | WARNING | Scope is runtime-only (🔧) — ignored, not harmful |
| Any `id*` key on EAttribute | ERROR | ID config not applicable to attributes |

---

## Metadata Merge Configuration

When both `typeFormat` and `idFormat` are `STRUCTURED`, you can optionally merge type, supertype, and ID information into a single metadata object.

| Annotation Key | Property Key | Global | EPkg | EClass | ERef | EAttr | Description |
|----------------|--------------|:------:|:----:|:------:|:----:|:-----:|-------------|
| `metadataMerge` | `codec.metadataMerge` | ✅ | ❌ | ✅ | ❌ | ❌ | Merge type + id into single object (**default:** `false`) |
| `metadataKey` | `codec.metadataKey` | ✅ | ❌ | ✅ | ❌ | ❌ | Key for merged metadata object (**default:** `_metadata`) |

**Prerequisite:** `metadataMerge` only applies when `typeFormat == STRUCTURED && idFormat == STRUCTURED`. Otherwise ignored.

### Without Merge **(default)**

```java
Config.builder()
    .typeStrategy(TypeStrategy.SCHEMA_AND_TYPE)
    .typeFormat(SerializationFormat.STRUCTURED)
    .typeKey("_type")
    .superTypeSerialize(true)
    .idFormat(SerializationFormat.STRUCTURED)
    .idKey("_id")
    .build()
```

```json
{
  "_type": {
    "schema": "http://example.org",
    "type": "Friend",
    "superTypes": ["Person"]
  },
  "_id": {
    "id": "maho"
  },
  "name": "Mark"
}
```

### With Merge

```java
Config.builder()
    .typeStrategy(TypeStrategy.SCHEMA_AND_TYPE)
    .typeFormat(SerializationFormat.STRUCTURED)
    .superTypeSerialize(true)
    .idFormat(SerializationFormat.STRUCTURED)
    .metadataMerge(true)
    .metadataKey("_metadata")  // default
    .build()
```

```json
{
  "_metadata": {
    "schema": "http://example.org",
    "type": "Friend",
    "superTypes": ["Person"],
    "id": "maho"
  },
  "name": "Mark"
}
```

### Field Order Inside Merged Object

The `idOnTop` property still controls whether ID comes first or last:

**`idOnTop=true` (default):**
```json
{
  "_metadata": {
    "id": "maho",
    "schema": "http://example.org",
    "type": "Friend",
    "superTypes": ["Person"]
  }
}
```

**`idOnTop=false`:**
```json
{
  "_metadata": {
    "schema": "http://example.org",
    "type": "Friend",
    "superTypes": ["Person"],
    "id": "maho"
  }
}
```

Field order (with `idOnTop=false`):
1. `schema` (if strategy includes schema, e.g., SCHEMA_AND_TYPE)
2. `type` (type name)
3. `superTypes` (if `superTypeSerialize=true`)
4. `id` (ID value)

### Inner Keys in Merged Object

When merged, the inner keys from type, supertype, and ID configuration are used:

| Configuration | Inner Key Property | Default | Used In Merged Object |
|---------------|-------------------|---------|----------------------|
| Type name | `typeNameKey` | `type` | ✅ |
| Schema | `typeSchemaKey` | `schema` | ✅ |
| SuperTypes | `superTypeKey` | `superTypes` | ✅ |
| ID | `idKey` | `id` | ✅ (uses `idKey` value without `_` prefix) |

> **Note:** When merged, the outer keys (`typeKey`, `idKey`) are ignored - only the inner keys matter.

### Invalid Configurations

| Misconfiguration | Severity | Reason |
|------------------|----------|--------|
| `metadataMerge` on EReference | ERROR | Metadata merge is class-intrinsic |
| `metadataKey` on EReference | ERROR | Metadata key is class-specific |
| Any `metadata*` key on EAttribute | ERROR | Metadata config not applicable to attributes |

---

## Reference Configuration

Reference configuration describes how non-containment references (and cross-document containments) are serialized.

> **Detailed documentation:** See [10-reference.md](10-reference.md) for complete reference serialization specification.

| Annotation Key | Property Key | Global | EPkg | EClass | ERef | EAttr | Description |
|----------------|--------------|:------:|:----:|:------:|:----:|:-----:|-------------|
| `refFormat` | `codec.refFormat` | ✅ | ❌ | ❌ | ✅ | ❌ | Output format (see [SerializationFormat](#serializationformat)) |
| `refKey` | `codec.refKey` | ✅ | ❌ | ❌ | ✅ | ❌ | Reference value key (**default:** `_ref`) |
| `refTypeKey` | `codec.refTypeKey` | ✅ | ❌ | ❌ | ✅ | ❌ | Type key in STRUCTURED (**default:** `_type`) |
| `expand` | `codec.expand` | ✅ | ❌ | ✅ | ✅ | ❌ | Inline specific refs (list at G/C, boolean at F) (**default:** `false`/empty) |
| `expandGlobal` | `codec.expandGlobal` | ✅ | ❌ | ✅ | ❌ | ❌ | Expand ALL non-containment references (**default:** `false`) |
| `expandDepth` | `codec.expandDepth` | ✅ | ❌ | ✅ | ✅ | ❌ | Max depth for nested expansion (**default:** `1`) |
| `expandIgnoreBidirectional` | `codec.expandIgnoreBidirectional` | ✅ | ❌ | ✅ | ✅ | ❌ | Skip opposite references during expand (**default:** `true`) |
| `serializeInstanceType` | `codec.serializeInstanceType` | ✅ | ❌ | ❌ | ✅ | ❌ | Write instance type vs reference type (**default:** `true`) |

**Implementation:** `CodecAnnotationConstants.KEY_REF_*`, `CodecAnnotationConstants.KEY_EXPAND`, `CodecAnnotationConstants.KEY_EXPAND_GLOBAL`, `CodecAnnotationConstants.KEY_SERIALIZE_INSTANCE_TYPE`

**Expand behavior:**
- `expandGlobal=true`: Expand ALL non-containment references
- `expand` at F level: `true`/`false` - expand THIS specific reference
- `expand` at G/C level: list of reference names to expand
- A reference is expanded if `expandGlobal=true` OR it's in the `expand` list

**Serialization behavior:**
- `expand=false` **(default)**: Write reference as proxy (`"_ref": "/persons/123"`)
- `expand=true`: Inline the full referenced object (useful for denormalization)
- `serializeInstanceType=true` **(default)**: Write the actual instance type (`eObject.eClass()`)
- `serializeInstanceType=false`: Write the declared reference type (`eReference.getEReferenceType()`)

**Deserialization behavior:**

- `expand=false` **(default)**: Read proxy, resolve reference later
- `expand=true`: **TODO (future)** - Read inlined object, create as detached EObject (not contained). Should emit warning that non-containment reference object is detached.

> **⚠️ Warning:** `serializeInstanceType=false` is a **serialization-only option**. It may cause deserialization failures if the reference type is abstract/interface or if instance-specific features are lost. See [12-polymorphism.md](12-polymorphism.md#13-writing-reference-type-serializeinstancetypefalse) for details.

**Why NOT on EClass:** Different references on the same class may need different formats. `employer` might use STRUCTURED while `friends` uses PLAIN.

### Invalid Configurations

| Misconfiguration | Severity | Reason |
|------------------|----------|--------|
| Any `ref*` key on EClass | WARNING | Ignored - reference config is per-reference only |
| Any `ref*` key on EAttribute | ERROR | Reference config not applicable to attributes |
| `expand` on EClass | WARNING | Ignored - expand is per-reference only |
| `expand` on EAttribute | ERROR | Expand not applicable to attributes |

---

## Feature Configuration (EStructuralFeature)

Feature configuration controls serialization/deserialization behavior for individual features. Applies to both EReference and EAttribute.

> **Detailed documentation:** See [11-feature.md](11-feature.md) for complete feature serialization specification including null/default handling, enum serialization, and array attributes.

### Feature Visibility Control

#### Per-Feature Annotations

| Annotation Key | Property Key | Global | EPkg | EClass | ERef | EAttr | Description |
|----------------|--------------|:------:|:----:|:------:|:----:|:-----:|-------------|
| `ignore` | `codec.ignore` | ❌ | ❌ | ❌ | ✅ | ✅ | Skip both read AND write (**default:** `false`) |
| `ignoreRead` | `codec.ignoreRead` | ❌ | ❌ | ❌ | ✅ | ✅ | Skip deserialization only (**default:** `false`) |
| `ignoreWrite` | `codec.ignoreWrite` | ❌ | ❌ | ❌ | ✅ | ✅ | Skip serialization only (**default:** `false`) |
| `forceRead` | `codec.forceRead` | ❌ | ❌ | ❌ | ✅ | ✅ | Force read EMF transient/volatile (**default:** `false`) |
| `forceWrite` | `codec.forceWrite` | ❌ | ❌ | ❌ | ✅ | ✅ | Force write EMF transient/volatile (**default:** `false`) |

**Note:** `ignore`, `ignoreRead`, and `ignoreWrite` are codec-level controls. `forceRead` and `forceWrite` override EMF-level `transient`, `volatile`, and `derived` markers.

#### Feature List (Runtime Only)

| Property Key | Global | EClass | Description |
|--------------|:------:|:------:|-------------|
| `codec.ignoreFeatures` | 🔧 | 🔧 | Features to ignore (both read+write) |

The `ignoreFeatures` property is **runtime-only** (no EAnnotation) and accepts:
- Comma-separated string: `"internalId,debugInfo,tempCache"`
- `List<String>`: `List.of("internalId", "debugInfo", "tempCache")`
- `List<EStructuralFeature>`: `List.of(MyPackage.Literals.MY_CLASS__INTERNAL_ID, ...)`

For directional control (`ignoreRead`, `ignoreWrite`) or forcing volatile features (`forceRead`, `forceWrite`), use the per-feature annotations above.

**Resolution logic:**
```
Serialization:
1. If feature in ignoreFeatures OR ignoreWrite=true OR ignore=true → skip
2. If EMF transient/volatile/derived → check forceWrite
   - forceWrite=true → continue
   - forceWrite=false → skip
3. Serialize normally

Deserialization:
1. If feature in ignoreFeatures OR ignoreRead=true OR ignore=true → skip
2. If EMF transient/volatile → check forceRead
   - forceRead=true → continue (feature must be changeable)
   - forceRead=false → skip
3. Deserialize normally
```

**Use cases:**
- `ignore=true`: Completely hide a feature from JSON (both directions)
- `ignoreWrite=true`: Read from JSON but don't write back (one-way import)
- `ignoreRead=true`: Write to JSON but ignore during read (computed output fields)
- `forceWrite=true`: Serialize volatile/derived features (e.g., GeoJSON coordinates)
- `forceRead=true`: Deserialize into volatile/derived features (roundtrip support)
- `ignoreFeatures`: Runtime bulk ignore without model annotations

### Feature Serialization Options

| Annotation Key | Property Key | Global | EPkg | EClass | ERef | EAttr | Description |
|----------------|--------------|:------:|:----:|:------:|:----:|:-----:|-------------|
| `key` | `codec.key` | ❌ | ❌ | ❌ | ✅ | ✅ | Custom JSON property name |
| `serializeNull` | `codec.serializeNull` | ✅ | ❌ | ❌ | ✅ | ✅ | Include null values (**default:** `false`) |
| `serializeEmpty` | `codec.serializeEmpty` | ✅ | ❌ | ❌ | ✅ | ✅ | Include empty collections (**default:** `false`) |
| `serializeDefaults` | `codec.serializeDefaults` | ✅ | ❌ | ❌ | ✅ | ✅ | Include default values (**default:** `false`) |
| `enumSerialization` | `codec.enumSerialization` | ✅ | ❌ | ❌ | ❌ | ✅ | How to serialize enum values |
| `valueReaderName` | `codec.valueReaderName` | ❌ | ❌ | ❌ | ✅ | ✅ | Custom value reader service name |
| `valueWriterName` | `codec.valueWriterName` | ❌ | ❌ | ❌ | ✅ | ✅ | Custom value writer service name |

**Implementation:** `CodecAnnotationConstants.KEY_KEY`, `KEY_IGNORE`, `KEY_IGNORE_READ`, `KEY_IGNORE_WRITE`, `KEY_FORCE_READ`, `KEY_FORCE_WRITE`, `KEY_SERIALIZE_NULL`, `KEY_SERIALIZE_EMPTY`, `KEY_SERIALIZE_DEFAULTS`, `KEY_VALUE_READER_NAME`, `KEY_VALUE_WRITER_NAME`, `KEY_ENUM_SERIALIZATION`

### EnumSerialization Values
| Value | Description |
|-------|-------------|
| `LITERAL` | Use enum literal name: `"ACTIVE"` **(default)** |
| `VALUE` | Use enum ordinal: `1` |
| `NAME` | Use enum name: `"Active"` |

### Migration from `transient` and `serialize`

Previous versions used `transient` and `serialize` properties. These are now **deprecated** in favor of the directional `ignore*` and `force*` properties:

| Old Property | New Equivalent | Notes |
|--------------|----------------|-------|
| `transient=true` | `ignoreWrite=true` | Codec-level, serialization only |
| `serialize=false` | `ignoreWrite=true` | Same as transient |
| `forceSerialize` | `forceWrite=true` | Now per-feature, directional |

**Why the change:**
- `transient` was confusing (codec-level vs EMF-level?)
- `serialize` only affected one direction - unclear from name
- New naming is explicit: `ignoreRead`, `ignoreWrite`, `forceRead`, `forceWrite`

### Invalid Configurations

| Misconfiguration | Severity | Reason |
|------------------|----------|--------|
| `enumSerialization` on EReference | WARNING | Ignored - enum serialization only applies to EAttributes |
| `ignore*` / `force*` on EClass | WARNING | Ignored - feature visibility is per-feature only |
| `key` on EClass | WARNING | Ignored - JSON key customization is per-feature only |

---

## Feature Strictness

Controls how deserializer handles unexpected mismatches.

| Annotation Key | Property Key | Global | EPkg | EClass | Default | Description |
|----------------|--------------|:------:|:----:|:------:|---------|-------------|
| `strictOnUnknown` | `codec.strictOnUnknown` | ✅ | ❌ | ✅ | `false` | ERROR on unknown JSON field |
| `strictOnMissing` | `codec.strictOnMissing` | ✅ | ❌ | ✅ | `false` | ERROR on missing required feature |

**Note:** Strictness is not supported on EReference or EAttribute level. It applies to all features of a class uniformly.

**LENIENT (default):** Unknown fields → WARNING + skip. Missing required → WARNING + use default.

**STRICT:** Unknown fields → ERROR. Missing required → ERROR.

**Scope behavior:**
- **Global:** Applies to all features everywhere
- **EClass:** Applies to all features of that class

**Use cases:**
- `strictOnUnknown=true` at Global: Fail-fast for any unexpected JSON field (strict schema validation)
- `strictOnMissing=true` on EClass: Ensure all required features of this class are present

**Implementation:** `CodecAnnotationConstants.KEY_STRICT_ON_UNKNOWN`, `KEY_STRICT_ON_MISSING`

---

## Global Options Summary (Codec-wide)

This section summarizes all global-level runtime options. Most are described in detail in their respective sections.

| Property Key | Builder | Default | Described In |
|--------------|---------|---------|--------------|
| `codec.typeStrategy` | `.typeStrategy(...)` | `URI` | [Type Configuration](#type-configuration) |
| `codec.typeFormat` | `.typeFormat(...)` | `PLAIN` | [Type Configuration](#type-configuration) |
| `codec.typeKey` | `.typeKey(...)` | `_type` | [Type Configuration](#type-configuration) |
| `codec.typeScope` | `.typeScope(...)` | `ALL` | [Type Configuration](#type-configuration) |
| `codec.typeFormatScope` | `.typeFormatScope(...)` | `ALL` | [Type Configuration](#type-configuration) |
| `codec.idStrategy` | `.idStrategy(...)` | `ID_FIELD` | [ID Configuration](#id-configuration) |
| `codec.idFormat` | `.idFormat(...)` | `PLAIN` | [ID Configuration](#id-configuration) |
| `codec.idKey` | `.idKey(...)` | `_id` | [ID Configuration](#id-configuration) |
| `codec.idScope` | `.idScope(...)` | `ALL` | [ID Configuration](#id-configuration) |
| `codec.superTypeSerialize` | `.superTypeSerialize(...)` | `false` | [SuperType Configuration](#supertype-configuration) |
| `codec.refFormat` | `.refFormat(...)` | `PLAIN` | [Reference Configuration](#reference-configuration) |
| `codec.refKey` | `.refKey(...)` | `_ref` | [Reference Configuration](#reference-configuration) |
| `codec.ignoreFeatures` | `.ignoreFeatures(...)` | — | [Feature Visibility Control](#feature-visibility-control) |
| `codec.serializeNull` | `.serializeNull(...)` | `false` | [Feature Serialization Options](#feature-serialization-options) |
| `codec.serializeEmpty` | `.serializeEmpty(...)` | `false` | [Feature Serialization Options](#feature-serialization-options) |
| `codec.serializeDefaults` | `.serializeDefaults(...)` | `false` | [Feature Serialization Options](#feature-serialization-options) |
| `codec.enumSerialization` | `.enumSerialization(...)` | `LITERAL` | [Feature Serialization Options](#feature-serialization-options) |
| `codec.strictOnUnknown` | `.strictOnUnknown(...)` | `false` | [Feature Strictness](#feature-strictness) |
| `codec.strictOnMissing` | `.strictOnMissing(...)` | `false` | [Feature Strictness](#feature-strictness) |
| `codec.smartCompression` | `.smartCompression(boolean)` | `false` | Omit redundant type info |
| `codec.useNamesFromExtendedMetaData` | `.useNamesFromExtendedMetaData(boolean)` | `false` | Use XSD names for features |
| `codec.sortPropertiesAlphabetically` | `.sortPropertiesAlphabetically(boolean)` | `false` | Sort JSON properties |

---

## Load/Save Options

Options passed to `resource.load(options)` or `resource.save(options)`. These are per-operation settings that override all other configuration levels.

### Option Keys

| Option Key | Type | Direction | Description |
|------------|------|-----------|-------------|
| `CODEC_ROOT_TYPE` | `EClass` or `String` | Load | Type hint for root object ([details](#type-hints-codec_root_type-codec_root_schema)) |
| `CODEC_ROOT_SCHEMA` | `String` (URI) | Load | Schema context for NAME strategy ([details](#type-hints-codec_root_type-codec_root_schema)) |
| `CODEC_TYPE_HINT_MODE` | `TypeHintMode` | Load | How type hints interact with JSON content ([details](#type-hint-mode-codec_type_hint_mode)) |
| `CODEC_DESERIALIZATION_MODE` | `DeserializationMode` | Load | Type resolution strictness ([details](#deserialization-mode-codec_deserialization_mode)) |
| `CODEC_FEATURE_TYPE_HINTS` | `Map<EStructuralFeature, EClass>` | Load | Type hints per feature ([details](#per-feature-type-hints-codec_feature_type_hints)) |
| `CODEC_FEATURE_VALUE_READERS` | `Map<EStructuralFeature, String>` | Load | Custom readers per feature |
| `CODEC_FEATURE_VALUE_WRITERS` | `Map<EStructuralFeature, String>` | Save | Custom writers per feature |
| `CODEC_VALUE_READERS` | `List<CodecValueReader>` | Load | Global readers to register ([details](14-custom-values.md)) |
| `CODEC_VALUE_WRITERS` | `List<CodecValueWriter>` | Save | Global writers to register ([details](14-custom-values.md)) |
| `CODEC_FAIL_FAST` | `Boolean` | Both | Throw on first error ([details](#diagnostic-options)) |
| `CODEC_SUPPRESS_WARNINGS` | `Boolean` | Both | Suppress all warnings ([details](#diagnostic-options)) |
| `CODEC_SUPPRESS_WARNING_SOURCES` | `Set<String>` | Both | Suppress warnings by source ([details](#diagnostic-options)) |
| `CODEC_DIAGNOSTIC_HANDLER` | `DiagnosticHandler` | Both | Custom diagnostic handler ([details](#diagnostic-options)) |

### Type Hints (CODEC_ROOT_TYPE, CODEC_ROOT_SCHEMA)

When deserializing JSON that lacks type information (e.g., `typeStrategy=NONE` or external JSON), you can provide type hints:

```java
// Hint the root object type
options.put(CODEC_ROOT_TYPE, PersonPackage.Literals.PERSON);

// Or by URI string
options.put(CODEC_ROOT_TYPE, "http://example.org#//Person");

// For NAME strategy, provide schema context
options.put(CODEC_ROOT_SCHEMA, "http://example.org");
options.put(CODEC_ROOT_TYPE, "Person");  // Will resolve to http://example.org#//Person
```

### Type Hint Mode (CODEC_TYPE_HINT_MODE)

Controls how `CODEC_ROOT_TYPE` interacts with type information found in the JSON content.

| Value | Behavior |
|-------|----------|
| `HINT` **(default)** | JSON content wins if present; hint used as fallback |
| `OVERRIDE` | Hint always wins; JSON content type ignored |

**HINT mode (default):**
```java
options.put(CODEC_ROOT_TYPE, PersonPackage.Literals.PERSON);
options.put(CODEC_TYPE_HINT_MODE, TypeHintMode.HINT);

// JSON with type → uses JSON type (Employee)
{ "_type": "http://example.org#//Employee", "name": "John" }

// JSON without type → uses hint (Person)
{ "name": "John" }
```

**OVERRIDE mode:**
```java
options.put(CODEC_ROOT_TYPE, PersonPackage.Literals.PERSON);
options.put(CODEC_TYPE_HINT_MODE, TypeHintMode.OVERRIDE);

// JSON type ignored → always uses hint (Person)
{ "_type": "http://example.org#//Employee", "name": "John" }  // Creates Person, not Employee
```

**When to use OVERRIDE:**
- Consuming external JSON where `_type` has different semantics
- Testing/mocking with fixed types
- Migration scenarios where JSON types are outdated

### Per-Feature Type Hints (CODEC_FEATURE_TYPE_HINTS)

**The problem:** During deserialization, the codec needs to know the EClass of each object it creates. Normally, `_type` in the JSON provides this. But there are situations where type information is missing:

- **PLAIN reference format** — carries only a URI string, no room for `_type`
- **STRUCTURED reference without `_type`** — the JSON object was written without type info (e.g., `serializeInstanceType=false`, or external/third-party JSON)
- **Containment features** — external JSON that omits `_type` for nested objects
- **Abstract declared types** — the EReference/EAttribute declares an abstract type or interface, but the JSON doesn't say which concrete subtype to create

In all these cases, the codec's final fallback is `EReference.getEReferenceType()` (the declared type). If that type is abstract, deserialization fails — you can't instantiate an abstract EClass.

**The solution:** `CODEC_FEATURE_TYPE_HINTS` provides a per-feature EClass that the codec uses when JSON lacks type information. It sits in the type resolution fallback chain **above** the declared type but **below** any type found in the JSON:

```
Type resolution priority (highest to lowest):
1. _type field in JSON (if present)
2. Discriminator / inline mapping (if configured)
3. CODEC_FEATURE_TYPE_HINTS (if set for this feature)    ← this option
4. EReference.getEReferenceType() / declared type (final fallback)
```

**Usage:**

```java
Map<EStructuralFeature, EClass> featureHints = Map.of(
    PersonPackage.Literals.PERSON__ADDRESS, AddressPackage.Literals.HOME_ADDRESS,
    PersonPackage.Literals.PERSON__EMPLOYER, CompanyPackage.Literals.CORPORATION
);
options.put(CODEC_FEATURE_TYPE_HINTS, featureHints);
```

> **Note:** `CODEC_FEATURE_TYPE_HINTS` is a **Load-only** option. It has no effect on serialization — the serializer always has the real EObject and knows its actual EClass. Hints are only needed during deserialization when the object doesn't exist yet and the JSON doesn't tell us its type.

**When to use which:**

| Scenario | Solution | Why |
|----------|----------|-----|
| PLAIN reference format with abstract declared type | Feature type hint | No `_type` in JSON, need concrete EClass for proxy |
| External JSON without `_type` fields | Feature type hint | Fixed concrete type per feature |
| Polymorphic collection with discriminator values | [Inline Mapping](#inline-mapping-on-ereference) | Multiple types resolved from value |
| Concrete declared type, no polymorphism | Neither — declared type suffices | Fallback works automatically |

**Relationship to Inline Mapping:**

| Aspect | Feature Type Hints | Inline Mapping |
|--------|-------------------|----------------|
| **Configuration** | Runtime (load options) | Model (EAnnotation) or runtime |
| **Resolution** | Fixed type per feature | Discriminator value → type |
| **Polymorphism** | Single type | Multiple types based on value |
| **Use case** | External JSON without type info | Polymorphic collections with type markers |

Feature type hints also integrate with inline mapping's fallback resolution - see [Fallback and Error Handling](#fallback-and-error-handling) in Discriminator Mapping.

See [Reference Deserialization Flow](10-reference.md#924-reference-deserialization-flow) for how hints fit into the full deserialization flow.

### Deserialization Mode (CODEC_DESERIALIZATION_MODE)

Controls how the deserializer resolves type information from JSON. This affects how flexible the codec is when the JSON doesn't exactly match the configured `typeStrategy`.

| Value | Description |
|-------|-------------|
| `STRICT` | Type field MUST match configured strategy exactly |
| `LENIENT` **(default)** | Try configured strategy first, then fallbacks |
| `AUTO_DETECT` | Ignore config, probe JSON structure to determine strategy |

**STRICT mode:**
- Expects type information in exactly the format specified by `typeStrategy`
- If `typeStrategy=URI`, the `_type` field must contain a valid EClass URI
- If `typeStrategy=NAME`, the `_type` field must contain a simple class name (requires schema context)
- Fails with ERROR if type field is missing or in wrong format

**LENIENT mode (default):**
- Tries configured strategy first
- If that fails, attempts fallback strategies in order:
  1. Try parsing as URI (`http://...#//ClassName`)
  2. Try parsing as NAME (simple class name, using schema context if available)
  3. Try parsing as CLASS (Java class name)
  4. Use `CODEC_ROOT_TYPE` hint if provided
  5. Use reference type if concrete
- Logs WARNING for fallback usage, continues with best match

**AUTO_DETECT mode:**
- Ignores configured `typeStrategy` entirely
- Probes the JSON structure to determine what's present:
  - Contains `://` → treat as URI
  - Contains `.` and looks like Java class → treat as CLASS
  - Contains `_schema` + `_type` → treat as SCHEMA_AND_TYPE
  - Otherwise → treat as NAME
- Useful for consuming JSON from unknown/varying sources

**Examples:**

```java
// STRICT - requires exact match
options.put(CODEC_DESERIALIZATION_MODE, DeserializationMode.STRICT);
// { "_type": "Person" } → ERROR (expected URI format)

// LENIENT - flexible with fallbacks **(default)**
options.put(CODEC_DESERIALIZATION_MODE, DeserializationMode.LENIENT);
// { "_type": "Person" } → OK (falls back to NAME with warning)

// AUTO_DETECT - probe JSON structure
options.put(CODEC_DESERIALIZATION_MODE, DeserializationMode.AUTO_DETECT);
// { "_type": "Person" } → OK (detected as NAME)
// { "_type": "http://example.org#//Person" } → OK (detected as URI)
```

**When to use each mode:**

| Mode | Use Case |
|------|----------|
| `STRICT` | Production systems with controlled JSON sources; fail-fast validation |
| `LENIENT` | General-purpose; handles minor format variations gracefully |
| `AUTO_DETECT` | Consuming JSON from external/unknown sources; maximum flexibility |

### Diagnostic Options

Control how errors and warnings are reported during serialization/deserialization.

> **See also:** [Error Handling & Diagnostics](15-error-handling.md) for complete diagnostic documentation including error scenarios and custom handlers.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `CODEC_FAIL_FAST` | `Boolean` | `false` | Throw exception on first error instead of collecting all |
| `CODEC_SUPPRESS_WARNINGS` | `Boolean` | `false` | Suppress all warnings from `resource.getWarnings()` |
| `CODEC_SUPPRESS_WARNING_SOURCES` | `Set<String>` | empty | Suppress warnings from specific sources only |
| `CODEC_DIAGNOSTIC_HANDLER` | `DiagnosticHandler` | null | Custom handler for errors/warnings |

```java
// Fail-fast mode - abort on first error
options.put("codec.failFast", true);

// Suppress warnings from specific sources
options.put("codec.suppressWarningSources", Set.of(
    "TypeDeserializationEntry",
    "AttributeDeserializationEntry"
));

// Custom diagnostic handler
options.put("codec.diagnosticHandler", new DiagnosticHandler() {
    @Override
    public void handleError(String message, String source, Object location) {
        logger.error("[{}] {}", source, message);
    }
    @Override
    public void handleWarning(String message, String source, Object location) {
        logger.warn("[{}] {}", source, message);
    }
});
```

---

## Property to Builder Mapping

| Annotation Key | Property Key | Builder Method | Scope |
|----------------|--------------|----------------|-------|
| `typeStrategy` | `codec.typeStrategy` | `.typeStrategy(TypeStrategy)` | Global, Class, Reference |
| `typeFormat` | `codec.typeFormat` | `.typeFormat(SerializationFormat)` | Global, Class, Reference |
| `typeKey` | `codec.typeKey` | `.typeKey(String)` | Global, Class, Reference |
| — | `codec.typeScope` | `.typeScope(StrategyScope)` | Global only (runtime) |
| — | `codec.typeFormatScope` | `.typeFormatScope(StrategyScope)` | Global only (runtime) |
| `idStrategy` | `codec.idStrategy` | `.idStrategy(IdStrategy)` | Global, Class |
| `idFormat` | `codec.idFormat` | `.idFormat(SerializationFormat)` | Global, Class, Reference |
| `idKey` | `codec.idKey` | `.idKey(String)` | Global, Class, Reference |
| `idFeatures` | `codec.idFeatures` | `.idFeatures(String...)` | Class only |
| — | `codec.idScope` | `.idScope(StrategyScope)` | Global only (runtime) |
| `refFormat` | `codec.refFormat` | `.refFormat(SerializationFormat)` | Global, Reference |
| `expand` | `codec.expand` | `.expand(boolean)` | Global, Reference |
| `refKey` | `codec.refKey` | `.refKey(String)` | Global, Reference |
| `key` | `codec.key` | `.key(String)` | Feature |
| `ignore` | `codec.ignore` | `.ignore(boolean)` | Feature |
| `ignoreRead` | `codec.ignoreRead` | `.ignoreRead(boolean)` | Feature |
| `ignoreWrite` | `codec.ignoreWrite` | `.ignoreWrite(boolean)` | Feature |
| `forceRead` | `codec.forceRead` | `.forceRead(boolean)` | Feature |
| `forceWrite` | `codec.forceWrite` | `.forceWrite(boolean)` | Feature |
| — | `codec.ignoreFeatures` | `.ignoreFeatures(...)` | Global, Class (runtime) |

---

## Implementation Status

This section tracks the implementation status of features documented in this reference against the codebase.

### Legend

| Symbol | Meaning |
|:------:|---------|
| ✅ | Fully implemented and tested |
| 🔶 | Partially implemented (gaps exist) |
| ❌ | Not implemented |
| N/A | Not applicable (runtime-only, no annotation) |

### Type Configuration

| Property | Annotation Constant | EMF Model | AspectProvider | Codec v2 | Tests | Spec |
|----------|:-------------------:|:---------:|:--------------:|:--------:|:-----:|:----:|
| `typeStrategy` | ✅ `KEY_TYPE_STRATEGY` | ✅ `BaseTypeConfig.strategy` | ✅ | ✅ | ✅ | ✅ |
| `typeFormat` | ✅ `KEY_TYPE_FORMAT` | ✅ `BaseTypeConfig.format` | ✅ | ✅ | ✅ | ✅ |
| `typeKey` | ✅ `KEY_TYPE_KEY` | ✅ `BaseTypeConfig.typeKey` | ✅ | ✅ | ✅ | ✅ |
| `typeNameKey` | ✅ `KEY_TYPE_NAME_KEY` | ✅ `BaseTypeConfig.nameKey` | ✅ | ✅ | ✅ | ✅ |
| `typeSchemaKey` | ✅ `KEY_TYPE_SCHEMA_KEY` | ✅ `BaseTypeConfig.schemaKey` | ✅ | ✅ | ✅ | ✅ |
| `typeValueReaderName` | ✅ `KEY_TYPE_VALUE_READER_NAME` | ❌ | ❌ | ❌ | ❌ | 🔶 |
| `typeValueWriterName` | ✅ `KEY_TYPE_VALUE_WRITER_NAME` | ❌ | ❌ | ❌ | ❌ | 🔶 |
| `typeScope` | N/A (runtime) | ✅ `TypeSerializationConfig.strategyScope` | N/A | 🔶 | 🔶 | ✅ |
| `typeFormatScope` | N/A (runtime) | ✅ `TypeSerializationConfig.formatScope` | N/A | 🔶 | 🔶 | ✅ |

### SuperType Configuration

| Property | Annotation Constant | EMF Model | AspectProvider | Codec v2 | Tests | Spec |
|----------|:-------------------:|:---------:|:--------------:|:--------:|:-----:|:----:|
| `superTypeSerialize` | ✅ `KEY_SUPERTYPE_SERIALIZE` | ✅ `BaseSuperTypeConfig.enabled` | ✅ | ✅ | ✅ | ✅ |
| `superTypeKey` | ✅ `KEY_SUPERTYPE_KEY` | ✅ `BaseSuperTypeConfig.superTypeKey` | ✅ | ✅ | ✅ | ✅ |
| `superTypeStrategy` | ✅ `KEY_SUPERTYPE_STRATEGY` | ✅ `BaseSuperTypeConfig.selection` | ✅ | ✅ | ✅ | ✅ |
| `superTypeAsArray` | ✅ `KEY_SUPERTYPE_AS_ARRAY` | ✅ `BaseSuperTypeConfig.asArray` | ✅ | ✅ | ✅ | ✅ |
| `superTypeSeparator` | ✅ `KEY_SUPERTYPE_SEPARATOR` | ✅ `BaseSuperTypeConfig.separator` | ✅ | ✅ | ✅ | ✅ |
| `superTypeFormat` | ✅ `KEY_SUPERTYPE_FORMAT` | ✅ `SuperTypeSerializationConfig.format` | ✅ | ✅ | ✅ | ✅ |
| `superTypeSchemaKey` | ❌ removed | ❌ removed | ❌ | ❌ | ❌ | ✅ inherits from TypeConfig |
| `superTypeNameKey` | ❌ removed | ❌ removed | ❌ | ❌ | ❌ | ✅ superTypeKey has format-dependent default |
| `superTypeValueReaderName` | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| `superTypeValueWriterName` | ✅ `KEY_SUPERTYPE_WRITER_NAME` | ❌ | ❌ | ❌ | ❌ | 🔶 |

### ID Configuration

| Property | Annotation Constant | EMF Model | AspectProvider | Codec v2 | Tests | Spec |
|----------|:-------------------:|:---------:|:--------------:|:--------:|:-----:|:----:|
| `idStrategy` | ✅ `KEY_ID_STRATEGY` | ✅ `BaseIdConfig.strategy` | ✅ | ✅ | ✅ | ✅ |
| `idFormat` | ✅ `KEY_ID_FORMAT` | ✅ `BaseIdConfig.format` | ✅ | ✅ | ✅ | ✅ |
| `idKey` | ✅ `KEY_ID_KEY` | ✅ `BaseIdConfig.idKey` | ✅ | ✅ | ✅ | ✅ |
| `idValueKey` | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `idFeatures` | ✅ `KEY_ID_FEATURES` | ✅ `IdSerializationConfig.idFeatures` | ✅ | ✅ | ✅ | ✅ |
| `idSeparator` | ✅ `KEY_ID_SEPARATOR` | ✅ `BaseIdConfig.separator` | ✅ | ✅ | ✅ | ✅ |
| `idSeparatorKey` | ✅ `KEY_ID_SEPARATOR_KEY` | ✅ `BaseIdConfig.separatorKey` | ✅ | ✅ | ✅ | ✅ |
| `idSeparatorSerialize` | ✅ `KEY_ID_SERIALIZE_SEPARATOR` | ✅ `BaseIdConfig.serializeSeparator` | ✅ | ✅ | ✅ | ✅ |
| `idKeyMode` | ✅ `KEY_ID_KEY_MODE` | ✅ `BaseIdConfig.keyMode` | ✅ | ✅ | ✅ | ✅ |
| `idOnTop` | ✅ `KEY_ID_ON_TOP` | ✅ `BaseIdConfig.onTop` | ✅ | ✅ | ✅ | ✅ |
| `idValueReaderName` | ✅ `KEY_ID_VALUE_READER_NAME` | ✅ `IdSerializationConfig.idValueReaderName` | ✅ | ✅ | ✅ | ✅ |
| `idValueWriterName` | ✅ `KEY_ID_VALUE_WRITER_NAME` | ✅ `IdSerializationConfig.idValueWriterName` | ✅ | ✅ | ✅ | ✅ |
| `idScope` | N/A (runtime) | ✅ `IdSerializationConfig.strategyScope` | N/A | 🔶 | 🔶 | ✅ |
| `idFormatScope` | N/A (runtime) | ✅ `IdSerializationConfig.formatScope` | N/A | 🔶 | 🔶 | ✅ |

### Metadata Merge Configuration

| Property | Annotation Constant | EMF Model | AspectProvider | Codec v2 | Tests | Spec |
|----------|:-------------------:|:---------:|:--------------:|:--------:|:-----:|:----:|
| `metadataMerge` | ✅ `KEY_METADATA_MERGE` | ✅ `ClassCodecAspect.metadataMerge` | ✅ | ❌ | ✅ | ✅ (+ ConfigProperty) |
| `metadataKey` | ✅ `KEY_METADATA_KEY` | ✅ `ClassCodecAspect.metadataKey` | ✅ | ❌ | ✅ | ✅ (+ ConfigProperty) |

### Reference Configuration

| Property | Annotation Constant | EMF Model | AspectProvider | Codec v2 | Tests | Spec |
|----------|:-------------------:|:---------:|:--------------:|:--------:|:-----:|:----:|
| `refFormat` | ✅ `KEY_REF_FORMAT` | ✅ `ReferenceSerializationConfig.format` | ✅ | ✅ | ✅ | ✅ |
| `refKey` | ✅ `KEY_REF_KEY` | ✅ `ReferenceSerializationConfig.refKey` | ✅ | ✅ | ✅ | ✅ |
| `refTypeKey` | ✅ `KEY_REF_TYPE_KEY` | ✅ `ReferenceSerializationConfig.typeKey` | ✅ | ✅ | ✅ | ✅ |
| `expand` | ✅ `KEY_EXPAND` | ✅ `ReferenceSerializationConfig.expand` | ✅ | ✅ ser | 🔶 | ✅ |
| `serializeInstanceType` | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |

**Note:** `expand` serialization works, but deserialization of expanded objects as detached EObjects is TODO.

**Note:** `serializeInstanceType` is specified but not yet implemented.

### Feature Configuration

| Property | Annotation Constant | EMF Model | AspectProvider | Codec v2 | Tests | Spec |
|----------|:-------------------:|:---------:|:--------------:|:--------:|:-----:|:----:|
| `key` | ✅ `KEY_KEY` | ✅ `BaseFeatureConfig.key` | ✅ | ✅ | ✅ | ✅ |
| `transient` | ✅ `KEY_TRANSIENT` | — (maps to serialize) | ✅ | ✅ | ✅ | 🔶 deprecated |
| `serialize` | ✅ `KEY_SERIALIZE` | ✅ `BaseFeatureConfig.serialize` | ✅ | ✅ | ✅ | 🔶 deprecated |
| `ignore` | ✅ `KEY_IGNORE` | ✅ `FeatureCodecAspect.ignore` | ✅ | ❌ | ✅ | ✅ |
| `ignoreRead` | ✅ `KEY_IGNORE_READ` | ✅ `FeatureCodecAspect.ignoreRead` | ✅ | ❌ | ✅ | ✅ |
| `ignoreWrite` | ✅ `KEY_IGNORE_WRITE` | ✅ `FeatureCodecAspect.ignoreWrite` | ✅ | ❌ | ✅ | ✅ |
| `forceRead` | ✅ `KEY_FORCE_READ` | ✅ `FeatureCodecAspect.forceRead` | ✅ | ❌ | ✅ | ✅ |
| `forceWrite` | ✅ `KEY_FORCE_WRITE` | ✅ `FeatureCodecAspect.forceWrite` | ✅ | ❌ | ✅ | ✅ |
| `serializeNull` | ✅ `KEY_SERIALIZE_NULL` | ✅ `BaseFeatureConfig.serializeNull` | ✅ | ✅ | ✅ | ✅ |
| `serializeEmpty` | ✅ `KEY_SERIALIZE_EMPTY` | ✅ `BaseFeatureConfig.serializeEmpty` | ✅ | ✅ | ✅ | ✅ |
| `serializeDefaults` | ✅ `KEY_SERIALIZE_DEFAULTS` | ✅ `BaseFeatureConfig.serializeDefaults` | ✅ | ✅ | ✅ | ✅ |
| `enumSerialization` | ✅ `KEY_ENUM_SERIALIZATION` | ✅ `BaseFeatureConfig.enumSerialization` | ✅ | ✅ | ✅ | ✅ |
| `valueReaderName` | ✅ `KEY_VALUE_READER_NAME` | ✅ `FeatureSerializationConfig.valueReaderName` | ✅ | ✅ | ✅ | ✅ |
| `valueWriterName` | ✅ `KEY_VALUE_WRITER_NAME` | ✅ `FeatureSerializationConfig.valueWriterName` | ✅ | ✅ | ✅ | ✅ |

### Feature Ignore List (Runtime)

| Property | EMF Model | Codec v2 | Tests | Spec |
|----------|:---------:|:--------:|:-----:|:----:|
| `ignoreFeatures` | N/A | 🔶 (GlobalIgnoreFeatureTest) | 🔶 | ✅ |

### Feature Strictness

| Property | Annotation Constant | EMF Model | AspectProvider | Codec v2 | Tests | Spec |
|----------|:-------------------:|:---------:|:--------------:|:--------:|:-----:|:----:|
| `strictOnUnknown` | ✅ | ✅ | ✅ | ❌ | ❌ | ✅ |
| `strictOnMissing` | ✅ | ✅ | ✅ | ❌ | ❌ | ✅ |

### Discriminator Mapping

| Property | Annotation Constant | EMF Model | AspectProvider | Codec v2 | Tests | Spec |
|----------|:-------------------:|:---------:|:--------------:|:--------:|:-----:|:----:|
| `typeMapId` | ✅ `KEY_TYPE_MAP_ID` | ✅ `TypeSerializationConfig.mapId` | ✅ | ✅ | ✅ | ✅ |
| `typeDiscriminator` | ✅ `KEY_TYPE_DISCRIMINATOR` | ✅ `TypeSerializationConfig.discriminatorValue` | ✅ | ✅ | ✅ | ✅ |
| `typeDiscriminatorPath` | ✅ `KEY_TYPE_DISCRIMINATOR_PATH` | ✅ `TypeSerializationConfig.discriminatorPath` | ✅ | ✅ | ✅ | ✅ |
| `typeDiscriminator.*` (prefix) | ✅ `KEY_TYPE_DISCRIMINATOR_PREFIX` | ✅ | ✅ | ✅ | ✅ | 🔶 |
| `fallbackStrategy` | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `fallbackEClass` | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |

### Inline Mapping

| Property | Annotation Constant | EMF Model | AspectProvider | Codec v2 | Tests | Spec |
|----------|:-------------------:|:---------:|:--------------:|:--------:|:-----:|:----:|
| `inlineMapping.*` (prefix) | ✅ `KEY_INLINE_MAPPING_PREFIX` | ✅ `InlineTypeMapping` | ✅ | ✅ | ✅ | 🔶 |
| Dedicated annotation source | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |

**Note:** Document proposes dedicated `http://eclipse.org/fennec/codec/inlineMapping` source, but implementation uses prefix-based `inlineMapping.*` in main source.

### Load/Save Options

| Option | Implementation | Tests | Spec |
|--------|:--------------:|:-----:|:----:|
| `CODEC_ROOT_TYPE` | ✅ | ✅ | ✅ |
| `CODEC_ROOT_SCHEMA` | ✅ | ✅ | ✅ |
| `CODEC_TYPE_HINT_MODE` | ✅ | ✅ | ✅ |
| `CODEC_DESERIALIZATION_MODE` | ✅ | ✅ | ✅ |
| `CODEC_FEATURE_TYPE_HINTS` | ✅ | ✅ | ✅ |
| `CODEC_FEATURE_VALUE_READERS` | ✅ | ✅ | ✅ |
| `CODEC_FEATURE_VALUE_WRITERS` | ✅ | ✅ | ✅ |
| `CODEC_VALUE_READERS` | ✅ | ✅ | ✅ |
| `CODEC_VALUE_WRITERS` | ✅ | ✅ | ✅ |
| `CODEC_FAIL_FAST` | 🔶 | ❌ | ✅ |
| `CODEC_SUPPRESS_WARNINGS` | 🔶 | ❌ | ✅ |
| `CODEC_SUPPRESS_WARNING_SOURCES` | 🔶 | ❌ | ✅ |
| `CODEC_DIAGNOSTIC_HANDLER` | 🔶 | ❌ | ✅ |

**Note:** Diagnostic options are specified but implementation status varies. See [15-error-handling.md](15-error-handling.md) for details.

---

## TODO / Open Items

### High Priority (Spec vs Implementation Gaps)

- [ ] **Feature Visibility Refactor**: Add `ignore`, `ignoreRead`, `ignoreWrite`, `forceRead`, `forceWrite` to replace deprecated `transient`/`serialize`
  - Add to `CodecAnnotationConstants`
  - Add to `BaseFeatureConfig` EMF model
  - Update `CodecAspectProvider` parsing
  - Update codec serializer/deserializer
  - Add tests

- [ ] **Metadata Merge**: Implement `metadataMerge` and `metadataKey` for combining type+id in STRUCTURED format
  - Add to `CodecAnnotationConstants`
  - Add to EMF model (ClassCodecAspect or new MetadataMergeConfig)
  - Implement in codec serializer

- [ ] **Fallback Handling**: Implement `fallbackStrategy` and `fallbackEClass` for discriminator mappings
  - Add to `CodecAnnotationConstants`
  - Add to EMF model
  - Implement fallback resolution chain in deserializer

- [ ] **Feature Strictness**: Implement `strictOnUnknown` and `strictOnMissing`
  - Add to `CodecAnnotationConstants`
  - Add to EMF model
  - Implement in deserializer with proper error/warning handling

- [ ] **ID Value Key**: Add `idValueKey` for inner key in STRUCTURED ID format
  - Add to `CodecAnnotationConstants`
  - Add to `BaseIdConfig` EMF model

### Medium Priority

- [ ] **Type/SuperType Value Reader/Writer**: Complete implementation
  - `typeValueReaderName`, `typeValueWriterName` - constants exist but not wired
  - `superTypeValueReaderName` - missing constant

- [ ] **Annotation Source Refactor**: Consider dedicated annotation sources for inline/type mappings
  - `http://eclipse.org/fennec/codec/inlineMapping`
  - `http://eclipse.org/fennec/codec/typeMapping/{mapId}`

- [ ] **Expand Deserialization**: Implement deserialization for `expand=true` non-containment references
  - Create detached EObjects
  - Emit warning about non-contained objects

### Lower Priority / Future

- [ ] **Scope Implementation**: Wire scope configuration in runtime config resolution
  - `typeScope`, `typeFormatScope`, `idScope`, `idFormatScope` exist in model but not fully used

- [ ] **EPackage Scope Level**: see [99-open-questions.md Q1](99-open-questions.md#q1-epackage-scope-level) (design questions resolved, scope decision open — issue #75)

- [ ] **EMF Configuration Model**: see [99-open-questions.md Q2](99-open-questions.md#q2-emf-configuration-model)

- [ ] Update spec `11-feature.md` to align with new `ignore*` / `force*` naming

- [ ] Review EEnum-level annotation support (enumSerialization on EEnum itself)

---

## Revision History

| Date | Changes |
|------|---------|
| 2026-07-25 | Added `fingerprintMode` / `fingerprintKey` to Type Configuration (in-band EPackage fingerprint, issue #73) |
| 2026-07-25 | Resolved the dead links to `codec-v2-spec-working/`: those working documents were lost in the spec rewrite. The reasoning they held is now inline here (why scopes are runtime-only) or consolidated in [99-open-questions.md](99-open-questions.md) Q1/Q2, so it cannot go missing with a file again (issue #75) |
| 2026-01-23 | Added Diagnostic Options subsection with cross-reference to 15-error-handling.md |
| 2026-01-23 | Added cross-references for diagnostic and custom value options in Load/Save Options table |
| 2026-01-23 | Refactored Feature Configuration: replaced `transient`/`serialize` with directional `ignore*`/`force*` naming |
| 2026-01-23 | Added per-feature annotations: `ignore`, `ignoreRead`, `ignoreWrite`, `forceRead`, `forceWrite` |
| 2026-01-23 | Added `ignoreFeatures` runtime-only list property (accepts String list or EStructuralFeature list) |
| 2026-01-23 | Moved NONE from IdStrategy to IdKeyMode; added idValueKey; added PLAIN/STRUCTURED format matrices |
| 2026-01-23 | Added Metadata Merge Configuration section for combining type + id into single object |
| 2026-01-23 | Added Error Handling section for discriminator mappings (fallbackStrategy, fallbackEClass) |
| 2026-01-23 | Refactored Discriminator Mapping to use dedicated annotation sources (`typeMapping/{mapId}`, `inlineMapping`) |
| 2026-01-23 | Added "Common Configuration Types" section with SerializationFormat and StrategyScope explanations |
| 2026-01-23 | Updated Type, SuperType, ID, Reference sections to reference common types and spec documents |
| 2026-01-23 | Added serialization/deserialization behavior notes to TypeStrategy, IdStrategy tables |
| 2026-01-23 | Added note about EPackage scope level proposal (the linked working document was later lost — see the 2026-07-25 row) |
| 2026-01-23 | Added explanation why `typeScope`/`typeFormatScope` remain runtime-only |
| 2026-01-23 | Added Typed Configuration Objects section (future placeholder for builder-based configs) |
| 2026-01-23 | Added Value Type Flexibility section with coercion rules for enums, EClass, booleans |
| 2026-01-23 | Added Programmatic Configuration section (Property Maps & Builders with scope level keys) |
| 2026-01-23 | Added `CODEC_ECLASS_CONFIG`, `CODEC_EREFERENCE_CONFIG`, `CODEC_EATTRIBUTE_CONFIG` keys |
| 2026-01-23 | Added property map structure and builder API examples with resolution walkthrough |
| 2026-01-23 | **Naming convention decision:** Property keys use simple `codec.` + camelCase format |
| 2026-01-23 | Updated all property keys from hierarchical (`codec.type.strategy`) to flat (`codec.typeStrategy`) |
| 2026-01-23 | Renamed `CODEC_ROOT_OBJECT` to `CODEC_ROOT_TYPE` with value `codec.rootType` |
| 2026-01-23 | Added Load/Save Option Keys section with Java constant names |
| 2026-01-23 | Added Configuration Resolution section (two-dimensional: Source Hierarchy + Scope Chain) |
| 2026-01-23 | Made all default values **bold** for visibility |
| 2026-01-23 | Fixed TypeStrategy NAME description: "simple EClass name" |
| 2026-01-23 | Aligned annotation keys with `CodecAnnotationConstants` implementation |
| 2026-01-23 | Removed MAPPED from TypeStrategy values (now separate discriminator layer) |
| 2026-01-23 | Added Implementation Status section |
| 2026-01-23 | Added CLASS strategy to TypeStrategy values |
| 2026-01-22 | Restructured as living document with clear purpose |
| 2026-01-22 | Added Global level to Type, SuperType, ID, Reference configs per proposal |
| 2026-01-22 | Added Feature Ignore List and Feature Strictness sections |
| 2026-01-22 | Added Property to Builder Mapping table |
| 2026-01-22 | Aligned with type-strategy-scope-proposal.md Section 3.1 |
