# Type Serialization

[← Global Options](05-global-options.md) | [Next: SuperType Serialization →](07-supertype.md)

---

> **See also:**
> - [Common Types](04-common-types.md) for TypeStrategy and SerializationFormat enums
> - [Naming Conventions](03-naming-conventions.md) for key naming conventions
> - [Annotation Reference](16-annotation-reference.md) (Type Configuration) for complete configuration keys and scope applicability

---

## 1. Format × Strategy for Type

Type serialization uses **two orthogonal dimensions**:

1. **Format** (`typeFormat`): PLAIN | STRUCTURED - how the data is presented
2. **Strategy** (`typeStrategy`): URI | NAME | NUMERIC | SCHEMA_AND_TYPE | NONE - what information is transported

Both can have independent **scopes** (`typeScope`, `typeFormatScope`) that control where they apply. See [Configuration Resolution](02-config-resolution.md) (section 5) for scope details.

> **Note:** `MAPPED` was removed from TypeStrategy. Discriminator-based type resolution is now a separate optional layer. See [Discriminator Mapping](08-discriminator-mapping.md) for details.

### 1.1 PLAIN Format Examples

| Strategy | Output Example |
|----------|----------------|
| URI | `"_type": "http://example.org/person/1.0#//Person"` |
| NAME | `"_type": "Person"` |
| CLASS | `"_type": "org.example.Person"` |
| NUMERIC | `"_type": "3"` |
| SCHEMA_AND_TYPE | `"_schema": "http://example.org/person/1.0", "_type": "Person"` |
| NONE | (no type field written) |

> **Note:** CLASS strategy requires `EClass.getInstanceClassName()` to be set (typical for EMF-generated code). If the instance class name is null, serialization fails with an ERROR. See [section 6.4.5](#645-class-strategy-resolution) for deserialization details and recommendations.

### 1.2 STRUCTURED Format Examples

| Strategy | Output Example |
|----------|----------------|
| URI | `"_type": { "type": "http://example.org/person/1.0#//Person" }` |
| NAME | `"_type": { "type": "Person" }` |
| CLASS | `"_type": { "type": "org.example.Person" }` |
| NUMERIC | `"_type": { "schema": "http://example.org/person/1.0", "classifier": 3 }` |
| SCHEMA_AND_TYPE | `"_type": { "schema": "http://example.org/person/1.0", "type": "Person" }` |
| NONE | (no type field written) |

### 1.3 Discriminator Mapping (Separate Layer)

Discriminator-based type resolution is **not** a TypeStrategy - it's an **orthogonal layer** that works alongside any strategy. This enables type discrimination based on a value found within the data itself.

> **Full Documentation:** See [Discriminator Mapping](08-discriminator-mapping.md) for complete details including:
> - Type Mapping Registry (centralized and distributed configuration)
> - Inline Mapping on EReference
> - Fallback strategy and error handling
> - Programmatic configuration

**Use cases:**
- **IoT/LoRaWAN devices** - where device type is embedded in payload metadata
- **JSON Schema oneOf patterns** - where type is determined by feature presence or values
- **Legacy APIs** - where type information is encoded in application-specific fields

**Resolution priority during deserialization:**
1. Type Mapping Registry (if configured on EClass) - highest priority
2. Inline Mapping (if configured on EReference)
3. Type Strategy Resolution (URI, NAME, etc.)
4. Fallback (reference type, CODEC_FEATURE_TYPE_HINTS, CODEC_ROOT_TYPE)

### 1.4 NONE Strategy

The `NONE` strategy indicates that no type information should be written or expected.

**Serialization with NONE:**
- No type field (`_type`) is written
- Useful for homogeneous collections, APIs that don't expect type metadata, or compact output

**Deserialization with NONE:**
- Deserializer does NOT look for type field in JSON
- Type resolution relies entirely on:
  1. `CODEC_ROOT_TYPE` load option (for root object)
  2. `EReference.getEReferenceType()` (for nested objects)
- **ERROR** if reference type is abstract and no concrete type can be determined

**Example:**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.NONE)
    .build();

// Serialization output - no _type field
// { "name": "John", "age": 30 }

// Deserialization - MUST provide CODEC_ROOT_TYPE
Map<String, Object> options = Map.of(
    CodecResource.CODEC_ROOT_TYPE, PersonPackage.Literals.PERSON
);
resource.load(input, options);
```

### 1.6 SCHEMA_AND_TYPE Strategy

The SCHEMA_AND_TYPE strategy transports both schema URI and type name as separate pieces of information.

**PLAIN format** - two separate top-level fields:
```json
{
  "_schema": "http://example.org/person/1.0",
  "_type": "Person"
}
```

**STRUCTURED format** - nested object with both:
```json
{
  "_type": {
    "schema": "http://example.org/person/1.0",
    "type": "Person"
  }
}
```

**Configurable keys (PLAIN):**
- `typeSchemaKey`: schema field (default: `_schema`)
- `typeKey`: type field (default: `_type`)

**Configurable keys (STRUCTURED):**
- `typeKey`: outer container key (default: `_type`)
- `typeSchemaKey`: schema field inside object (default: `schema`)
- `typeNameKey`: type name field inside object (default: `type`)
- `superTypeKey`: supertype field inside object (default: `supertype`, optional)

### 1.7 Type Strategy Scope and Containment Behavior

Type strategy configuration applies **per-class**, not globally to all objects in a hierarchy. This has important implications for containment references.

#### 1.7.1 Strategy Scope Rules

| Strategy | Applies To | Children Behavior |
|----------|-----------|-------------------|
| **URI** | Configured class | Children use their own configured strategy (or default URI) |
| **SCHEMA_AND_TYPE** | **Root object only** | Children fall back to default (URI) |
| **NAME** | Configured class | Children use their own configured strategy (or default URI) |
| **CLASS** | Configured class | Children use their own configured strategy |
| **NUMERIC** | Configured class | Children use their own configured strategy |
| **NONE** | Configured class | Children use their own configured strategy |

**Key Rule:** SCHEMA_AND_TYPE is a **root-only strategy**. It establishes a context schema at the root level but does not propagate to contained objects.

#### 1.7.2 Example: SCHEMA_AND_TYPE with Containments

**Model:**
- `Company` (configured with SCHEMA_AND_TYPE)
- `Person` (no configuration → uses default URI)
- `Company.employees: Person[*]` (containment)

**Configuration on Company:**
```xml
<eClassifiers xsi:type="ecore:EClass" name="Company">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="typeStrategy" value="SCHEMA_AND_TYPE"/>
  </eAnnotations>
  ...
</eClassifiers>
```

**Output:**
```json
{
  "_schema": "http://example.org/1.0",
  "_type": "Company",
  "name": "Acme Inc",
  "employees": [
    {
      "_type": "http://example.org/1.0#//Person",
      "name": "Alice"
    },
    {
      "_type": "http://example.org/1.0#//Person",
      "name": "Bob"
    }
  ]
}
```

**Observations:**
- Root `Company`: uses `_schema` + simple `_type` (SCHEMA_AND_TYPE strategy)
- Contained `Person`: uses full URI (default strategy, not inherited from parent)

#### 1.5.3 Combining with Smart Compression

To achieve compact output where contained objects use simple type names, enable **Smart Compression** (see [Global Options - Smart Compression](05-global-options.md#1-smart-compression)).

**With Smart Compression enabled:**
```json
{
  "_schema": "http://example.org/1.0",
  "_type": "Company",
  "name": "Acme Inc",
  "employees": [
    {
      "_type": "Person",
      "name": "Alice"
    }
  ]
}
```

Smart Compression uses the root's schema context to simplify same-schema types to simple names.

#### 1.5.4 Per-Reference Type Configuration

Type configuration **is supported at EReference level**, allowing different type handling for specific references:

```java
CodecConfiguration config = CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.URI)  // Global default
    .forReference(CompanyPackage.Literals.COMPANY__EXTERNAL_PARTNER)
        .typeStrategy(TypeStrategy.NAME)
        .typeKey("@type")
    .build();
```

**Output:**
```json
{
  "_type": "http://example.org#//Company",
  "employees": [
    { "_type": "http://example.org#//Person" }
  ],
  "externalPartner": {
    "@type": "Partner"
  }
}
```

**Use cases:**
- External APIs expecting different type conventions
- Cross-package references needing full URIs while internal refs use simple names
- JSON-LD style output for specific references

> **Note:** Per-reference configuration overrides EClass configuration, which overrides global configuration. See [Configuration Resolution](02-config-resolution.md) (section 4).

---

### 1.7 NUMERIC Strategy

The NUMERIC strategy uses EMF classifier IDs instead of type names for compactness.

**PLAIN format** - just the classifier ID as string:
```json
{
  "_type": "3"
}
```

**STRUCTURED format** - schema + classifier ID:
```json
{
  "_type": { "schema": "http://example.org/person/1.0", "classifier": 3 }
}
```

**Configurable keys (STRUCTURED):**
- `typeKey`: outer key (default: `_type`)
- `typeSchemaKey`: schema field (default: `schema`)
- `classifierKey`: classifier ID field (default: `classifier`)

> **Warning:** Classifier IDs are positional and can change when the model evolves. See [Global Options - NUMERIC Strategy](05-global-options.md#2-numeric-strategy) for details.

> **Deserialization requirement:** NUMERIC strategy **requires** a schema hint (`CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE`) for deserialization. Classifier IDs are package-specific — without knowing which package, the ID cannot be resolved to an EClass. Without a hint, resolution fails with a WARNING diagnostic (S-4: no global EPackage scan).

---

## 2. SuperType in STRUCTURED Format

When Type format is STRUCTURED and SuperType is enabled, supertype information is included **inside** the `_type` object as a `supertype` field:

```json
{
  "_type": {
    "schema": "http://example.org/person/1.0",
    "type": "Person",
    "supertype": ["Entity", "http://audit.org/1.0#//Auditable"]
  }
}
```

SuperType values follow namespace matching rules:
- **Same namespace** as schema: simple EClass name (e.g., `"Entity"`)
- **Different namespace**: full EClass URI (e.g., `"http://audit.org/1.0#//Auditable"`)

> **See [SuperType Serialization](09-supertype.md)** for complete SuperType configuration including selection modes, presentation styles, and PLAIN format examples.

---

## 3. Type Configuration

The configuration defines **format, strategy, and keys** - not actual values. Values come from the EObject at runtime.

> **See also:** [Annotation Reference](16-annotation-reference.md) (Type Configuration) for complete key reference and scope applicability.

### 3.1 Configuration Keys

| Annotation Key | Property Key | Default | Description |
|----------------|--------------|---------|-------------|
| `typeStrategy` | `codec.typeStrategy` | `URI` | What type information to transport |
| `typeFormat` | `codec.typeFormat` | `PLAIN` | How data is presented (flat vs nested) |
| `typeKey` | `codec.typeKey` | `_type` | Outer key (both formats) |
| `typeNameKey` | `codec.typeNameKey` | `type` | Inner type name key (STRUCTURED only) |
| `typeSchemaKey` | `codec.typeSchemaKey` | `schema` | Inner schema key (STRUCTURED only) |
| `typeValueWriterName` | `codec.typeValueWriterName` | — | Custom value writer service name (full delegation, see [§5.0 step 3a](#50-type-serialization-flow)) |
| `typeValueReaderName` | `codec.typeValueReaderName` | — | Custom value reader service name (full delegation, see [§6.3.0 step 3a](#630-type-deserialization-flow)) |
| — | `codec.typeScope` | `ALL` | Strategy scope (runtime-only, see below) |
| — | `codec.typeFormatScope` | `ALL` | Format scope (runtime-only, see below) |

> **Note:** `typeScope` and `typeFormatScope` are **runtime-only** configuration - no EAnnotation equivalent. They control where strategy/format applies (ROOT_ONLY, ROOT_CONTAINMENT, etc.). If found in EAnnotation, a WARNING is logged and the annotation is ignored. See [Configuration Resolution](02-config-resolution.md) (section 5).

> **Recommendation:** Use keys that start with `_` or `@` (like `_type`, `@type`) for `typeKey`, `typeSchemaKey`, and `typeNameKey`. This helps distinguish metadata from data fields. Without such prefixes, there's risk of collision with ordinary data keys during deserialization - the deserializer might misinterpret a data field as type information.

### 3.2 EAnnotation (on EClass or EReference)

```xml
<eClassifiers xsi:type="ecore:EClass" name="Person">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="typeStrategy" value="SCHEMA_AND_TYPE"/>
    <details key="typeFormat" value="STRUCTURED"/>
    <details key="typeKey" value="_type"/>
  </eAnnotations>
</eClassifiers>
```

Type configuration can also be applied at EReference level for per-reference overrides:

```xml
<eStructuralFeatures xsi:type="ecore:EReference" name="externalPartner">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="typeStrategy" value="URI"/>
    <details key="typeKey" value="@type"/>
  </eAnnotations>
</eStructuralFeatures>
```

### 3.3 Java Builder (Runtime Override)

**Minimal (default: PLAIN format, URI strategy):**
```java
CodecConfiguration config = CodecConfiguration.builder().build();
```
**Resulting JSON:**
```json
{
  "_type": "http://example.org/person/1.0#//Person"
}
```

**PLAIN format with NAME strategy:**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .typeFormat(SerializationFormat.PLAIN)
    .typeStrategy(TypeStrategy.NAME)
    .build();
```
**Resulting JSON:**
```json
{
  "_type": "Person"
}
```

**STRUCTURED format with SCHEMA_AND_TYPE strategy:**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .typeFormat(SerializationFormat.STRUCTURED)
    .typeStrategy(TypeStrategy.SCHEMA_AND_TYPE)
    .build();
```
**Resulting JSON:**
```json
{
  "_type": {
    "schema": "http://example.org/person/1.0",
    "type": "Person"
  }
}
```

**With custom keys:**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .typeFormat(SerializationFormat.STRUCTURED)
    .typeStrategy(TypeStrategy.SCHEMA_AND_TYPE)
    .typeKey("@context")
    .typeSchemaKey("@vocab")
    .typeNameKey("@type")
    .build();
```
**Resulting JSON:**
```json
{
  "@context": {
    "@vocab": "http://example.org/person/1.0",
    "@type": "Person"
  }
}
```

**With scope control (runtime-only):**
```java
CodecConfiguration config = CodecConfiguration.builder()
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

**At serialization time:**
1. Serializer resolves effective config for current context (class, reference, scope)
2. Determines output structure from `typeFormat` (PLAIN vs STRUCTURED)
3. Determines content from `typeStrategy` (URI, NAME, SCHEMA_AND_TYPE, etc.)
4. Extracts actual values from EObject's EClass (schema URI, name)
5. Combines config (format/strategy/keys) + EObject data (values) → JSON output

---

## 4. Default Type Settings

| Setting | Property Key | Default Value |
|---------|--------------|---------------|
| Format | `codec.typeFormat` | `PLAIN` |
| Strategy | `codec.typeStrategy` | `URI` |
| Type Key | `codec.typeKey` | `_type` |
| Schema Key | `codec.typeSchemaKey` | `schema` |
| Name Key | `codec.typeNameKey` | `type` |
| Scope | `codec.typeScope` | `ALL` |
| Format Scope | `codec.typeFormatScope` | `ALL` |

> **Note:** To disable type serialization, use `typeStrategy=NONE`.

**Default Output (PLAIN + URI):**
```json
{
  "_type": "http://example.org/person/1.0#//Person"
}
```

**STRUCTURED + SCHEMA_AND_TYPE Output:**
```json
{
  "_type": {
    "schema": "http://example.org/person/1.0",
    "type": "Person"
  }
}
```

---

## 5. Serialization

The serializer writes type information based on a **priority chain**: discriminator mappings first, then type strategy.

### 5.0 Type Serialization Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     TYPE SERIALIZATION FLOW                                 │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: EObject to serialize, context (EReference if nested, save options)

┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. TYPE MAPPING REGISTRY (on EClass)                                        │
│    ─────────────────────────────────────────────────────────────────────────│
│    Is there a typeMapping annotation on this EClass (or its base)?          │
│                                                                             │
│    NO → Continue to step 2                                                  │
│                                                                             │
│    YES:                                                                     │
│      → Get discriminatorPath (e.g., "info.profileName")                     │
│      → Get discriminatorValue for this EClass:                              │
│          - From typeDiscriminator annotation on this EClass, OR             │
│          - Reverse lookup in registry mappings (EClass → value)             │
│      → Found? ────────────────────────────────────────────────────────────  │
│        │ YES → Write discriminatorValue at discriminatorPath                │
│        │       Do NOT write _type field ─────────────────→ DONE ✓           │
│        │                                                                    │
│        └ NO (EClass not in registry) → Check fallbackStrategy (default:SKIP)│
│            - ERROR    → Fail immediately                                    │
│            - SKIP     → WARNING, continue to step 2                         │
│            - FALLBACK → Use fallbackEClass (MUST be set, else ERROR)        │
│                         Write fallbackEClass's discriminatorValue           │
│                         → DONE ✓                                            │
│                                                                             │
│    Note: The discriminatorPath replaces _type entirely. Type Mapping        │
│    Registry is our implementation of Jackson's @JsonTypeInfo + @JsonSubTypes│
│    pattern. See: 08-discriminator-mapping.md for full documentation.        │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 2. INLINE MAPPING (on EReference)                                           │
│    ─────────────────────────────────────────────────────────────────────────│
│    Is this a nested object (accessed via EReference)?                       │
│                                                                             │
│    NO (root object) → Continue to step 3                                    │
│                                                                             │
│    YES:                                                                     │
│      Is there an inlineMapping annotation on this EReference?               │
│                                                                             │
│      NO → Continue to step 3                                                │
│                                                                             │
│      YES:                                                                   │
│        → Get typeKey (configured on EReference or default _type)            │
│        → Get discriminatorValue for this EClass:                            │
│            Reverse lookup in inline mappings (EClass → value)               │
│        → Found? ──────────────────────────────────────────────────────────  │
│          │ YES → Write discriminatorValue at typeKey                        │
│          │       Do NOT proceed to Type Strategy ────────→ DONE ✓           │
│          │                                                                  │
│          └ NO (EClass not in inline mappings) → Check fallbackStrategy      │
│              (default: SKIP):                                               │
│              - ERROR    → Fail immediately                                  │
│              - SKIP     → WARNING, continue to step 3                       │
│              - FALLBACK → Use fallbackEClass (MUST be set, else ERROR)      │
│                           Write fallbackEClass's discriminatorValue         │
│                           → DONE ✓                                          │
│                                                                             │
│    Note: Inline mapping uses typeKey, not a custom path. It's a simpler,    │
│    more static variant of Type Mapping Registry.                            │
│    See: 08-discriminator-mapping.md section 5 for full documentation.       │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 3. TYPE STRATEGY RESOLUTION                                                 │
│    ─────────────────────────────────────────────────────────────────────────│
│    Get effective config from annotations + save options:                    │
│      - typeStrategy (default: URI)                                          │
│      - typeFormat (default: PLAIN)                                          │
│      - typeKey (default: _type)                                             │
│      - typeNameKey (default: type)      ← for STRUCTURED format             │
│      - typeSchemaKey (default: schema)  ← for STRUCTURED format             │
│      - smartCompression (default: false)                                    │
│                                                                             │
│    If typeStrategy = NONE:                                                  │
│      → Do NOT write any type field ──────────────────────→ DONE ✓           │
│                                                                             │
│    3a. CHECK CUSTOM VALUE WRITER                                            │
│                                                                             │
│        Is typeValueWriterName configured?                                   │
│        ├─ YES → Full delegation to custom TypeValueWriter service           │
│        │        - Has: JsonGenerator, EffectiveCodecConfig, DiagnosticCollector│
│        │        - Writer handles everything: value, format, keys, supertype │
│        │        - Framework does NOT apply format/strategy/smartCompression  │
│        │        → DONE ✓                                                    │
│        │                                                                    │
│        └─ NO → Use built-in value generation (strategy-dependent):          │
│            ┌─────────────────────────────────────────────────────────────┐  │
│            │ Strategy        │ Value                                     │  │
│            ├─────────────────┼───────────────────────────────────────────┤  │
│            │ URI             │ EcoreUtil.getURI(eClass).toString()       │  │
│            │ NAME            │ eClass.getName()                          │  │
│            │ CLASS           │ eClass.getInstanceClassName()             │  │
│            │                 │ → ERROR if null!                          │  │
│            │ NUMERIC         │ String.valueOf(eClass.getClassifierID())  │  │
│            │ SCHEMA_AND_TYPE │ schema: ePackage.getNsURI()               │  │
│            │                 │ type: eClass.getName()                    │  │
│            └─────────────────────────────────────────────────────────────┘  │
│                                                                             │
│    3b. Apply smartCompression (if enabled)                                  │
│        Is this a nested object AND same schema as root?                     │
│        ├─ YES → Use simple name instead of full URI                         │
│        └─ NO  → Use value from 3a as-is                                     │
│                                                                             │
│    3c. WRITE TYPE based on format                                           │
│        ┌─────────────────────────────────────────────────────────────────┐  │
│        │ Format + Strategy      │ Output                                 │  │
│        ├────────────────────────┼────────────────────────────────────────┤  │
│        │ PLAIN + URI/NAME/CLASS │ "_type": "<value>"                     │  │
│        │ PLAIN + NUMERIC        │ "_type": "<classifierID>"              │  │
│        │ PLAIN + SCHEMA_AND_TYPE│ "_schema": "...", "_type": "..."       │  │
│        │ STRUCTURED + any       │ "_type": { <nested object> }           │  │
│        └─────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│    If strategy fails (e.g., CLASS with null instanceClassName):             │
│      → ERROR (no fallback - user misconfiguration)                          │
│                                                                             │
│    3d. WRITE SUPERTYPE (if superTypeSerialize=true)                         │
│        See [SuperType Serialization](07-supertype.md) for full details.     │
│                                                                             │
│        ┌─────────────────────────────────────────────────────────────────┐  │
│        │ Type Format  │ SuperType Output                                 │  │
│        ├──────────────┼──────────────────────────────────────────────────┤  │
│        │ PLAIN        │ Standalone "_supertype" field at root level      │  │
│        │ STRUCTURED   │ "supertype" field inside "_type" object          │  │
│        └─────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│        SuperType values use smart compression based on ROOT schema:         │
│        - Same namespace as root → simple name (e.g., "Entity")              │
│        - Different namespace → full URI (e.g., "http://other/1.0#//Base")   │
│                                                                             │
│        Note: SuperType is written even when discriminator mapping (steps    │
│        1-2) handled type resolution. Discriminator only affects _type,      │
│        not supertype information.                                           │
│                                                                             │
│    ──────────────────────────────────────────────────────→ DONE ✓           │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.0.1 Serialization Priority Summary

| Priority | Mechanism | Configured On | Applies To |
|----------|-----------|---------------|------------|
| 1 | Type Mapping Registry | EClass (base) | All objects of that type |
| 2 | Inline Mapping | EReference | Objects accessed via that reference |
| 3 | Type Strategy | EClass, EReference, Global | Standard type field writing |

> **Note:** Unlike deserialization, serialization has no step 4 (Fallback hints). If discriminator/inline mappings fail with `fallbackStrategy=FALLBACK` but no `fallbackEClass`, or if Type Strategy fails, it's an ERROR.

### 5.0.2 Runtime Ordering Constraint (idOnTop)

The type serialization flow above determines **what** type information to write. The **position** of this output relative to `_id` is controlled by the `idOnTop` property (see [09-id.md §8.7](09-id.md#87-metadata-field-ordering-idontop)):

| `idOnTop` | Metadata Write Order |
|-----------|----------------------|
| `true` | `_id` → `_type` → `_supertype` → features |
| `false` **(default)** | `_type` → `_supertype` → `_id` → features |

This is a **runtime orchestration constraint** — the `CodecEObjectSerializer` evaluates `idOnTop` from the effective ID config and invokes the type and ID serialization entries in the appropriate order. The type flow itself does not need to know about `idOnTop`; the orchestrator handles the sequencing.

> **Deserialization:** Field order is irrelevant — JSON objects are unordered. See [Architecture §5.1](01-architecture.md#51-deferred-properties-order-independent-parsing).

---

## 6. Deserialization

### 6.1 Type Key Recognition

The deserializer automatically recognizes the following property names as type discriminators:

| Key | Description |
|-----|-------------|
| `_type` | Default type key (recommended) |
| `_class` | Alternative type key |
| `@type` | JSON-LD compatible |
| `eClass` | EMF/emfjson-jackson compatible |

> **Important:** The property name `type` is **NOT** automatically recognized as a type key, because it is a very common attribute name in data formats like GeoJSON (`"type": "Point"`), OpenAPI-generated models, etc.

To use `type` as type discriminator, configure it explicitly:

```java
CodecConfiguration config = CodecConfiguration.builder()
    .globalTypeKey("type")
    .build();
```

Or via EAnnotation on the EPackage:
```xml
<eAnnotations source="http://eclipse.org/fennec/codec">
  <details key="typeKey" value="type"/>
</eAnnotations>
```

### 6.2 Format Detection

Deserializers detect the format from the JSON structure:

| Input | Detected Format | Strategy |
|-------|-----------------|----------|
| `"_type": "string"` | PLAIN (auto-detected) | From effective config (NOT auto-detected) |
| `"_type": { ... }` | STRUCTURED (auto-detected) | From effective config (NOT auto-detected) |
| `"_schema": ..., "_type": "string"` | PLAIN (auto-detected) | SCHEMA_AND_TYPE (from config) |

> **Important:** Format is auto-detected from JSON structure, but strategy must be configured to match serialization. Strategy auto-detection is a future feature.

### 6.3 Type Resolution

The deserializer resolves the EClass based on a **priority chain**: discriminator mappings first, then type strategy, then fallback hints.

#### 6.3.0 Type Resolution Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     TYPE DESERIALIZATION FLOW                               │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: JSON object, context (EReference if nested, load options)

┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. TYPE MAPPING REGISTRY (on EClass)                                        │
│    ─────────────────────────────────────────────────────────────────────────│
│    Is there a typeMapping annotation on the expected EClass (or its base)?  │
│                                                                             │
│    NO → Continue to step 2                                                  │
│                                                                             │
│    YES:                                                                     │
│      → Read value from discriminatorPath (e.g., "info.profileName")         │
│      → Lookup value in registry mappings                                    │
│      → Found? ────────────────────────────────────────────→ RESOLVED ✓      │
│      → Not found? Check fallbackStrategy (default: SKIP):                   │
│          - ERROR    → Fail immediately                                      │
│          - SKIP     → WARNING, continue to step 2                           │
│          - FALLBACK → Use fallbackEClass (MUST be set, else ERROR)          │
│                       → RESOLVED ✓                                          │
│                                                                             │
│    Note: Type Mapping Registry is our implementation of Jackson's           │
│    @JsonTypeInfo + @JsonSubTypes pattern. It's the primary, flexible        │
│    mechanism for discriminator-based type resolution.                       │
│    See: 08-discriminator-mapping.md for full documentation.                 │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 2. INLINE MAPPING (on EReference)                                           │
│    ─────────────────────────────────────────────────────────────────────────│
│    Is this a nested object (accessed via EReference)?                       │
│                                                                             │
│    NO (root object) → Continue to step 3                                    │
│                                                                             │
│    YES:                                                                     │
│      Is there an inlineMapping annotation on this EReference?               │
│                                                                             │
│      NO → Continue to step 3                                                │
│                                                                             │
│      YES:                                                                   │
│        → Read value from typeKey (configured on EReference or default)      │
│        → Lookup value in inline mappings                                    │
│        → Found? ──────────────────────────────────────────→ RESOLVED ✓      │
│        → Not found? Check fallbackStrategy (default: SKIP):                 │
│            - ERROR    → Fail immediately                                    │
│            - SKIP     → WARNING, continue to step 3                         │
│            - FALLBACK → Use fallbackEClass (MUST be set, else ERROR)        │
│                         → RESOLVED ✓                                        │
│                                                                             │
│    Note: Inline Mapping is a simpler, more static variant of Type Mapping   │
│    Registry. It's per-reference only and less flexible.                     │
│    See: 08-discriminator-mapping.md section 5 for full documentation.       │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 3. TYPE STRATEGY RESOLUTION                                                 │
│    ─────────────────────────────────────────────────────────────────────────│
│    Get effective config from annotations + load options:                    │
│      - typeStrategy (default: URI)      ← MUST match serialization config!  │
│      - typeFormat (default: PLAIN)      ← can be auto-detected from JSON    │
│      - typeKey (default: _type)                                             │
│      - typeNameKey (default: type)      ← for STRUCTURED format             │
│      - typeSchemaKey (default: schema)  ← for STRUCTURED format             │
│      - smartCompression (default: false)← MUST match serialization config!  │
│                                                                             │
│    If typeStrategy = NONE:                                                  │
│      → Skip type field processing entirely                                  │
│      → Go directly to step 4 (Fallback)                                     │
│                                                                             │
│    3a. CHECK CUSTOM VALUE READER                                            │
│                                                                             │
│        Is typeValueReaderName configured?                                   │
│        ├─ YES → Full delegation to custom TypeValueReader service           │
│        │        - Has: JsonParser, EffectiveCodecConfig, DiagnosticCollector│
│        │        - Reader handles everything: locate field, parse, resolve   │
│        │        - Framework does NOT apply format detection/strategy        │
│        │        → RESOLVED ✓                                                │
│        │                                                                    │
│        └─ NO → Continue with built-in logic (step 3b)                       │
│                                                                             │
│    3b. DETECT FORMAT from JSON structure (auto-detection OK)                │
│        - Type field value is STRING? → PLAIN                                │
│        - Type field value is OBJECT? → STRUCTURED                           │
│        - Type field not present? → Go to step 4 (Fallback)                  │
│                                                                             │
│    3c. EXTRACT TYPE VALUE (built-in, strategy-dependent)                    │
│                                                                             │
│            ┌─────────────────────────────────────────────────────────────┐  │
│            │ Strategy        │ Extraction                                │  │
│            ├─────────────────┼───────────────────────────────────────────┤  │
│            │ URI             │ Read single value from typeKey            │  │
│            │ NAME            │ Read single value from typeKey            │  │
│            │ CLASS           │ Read single value from typeKey            │  │
│            │ NUMERIC (PLAIN) │ Read single value from typeKey            │  │
│            │ NUMERIC (STRUCT)│ Read schema + classifier from nested obj  │  │
│            │ SCHEMA_AND_TYPE │ Read schema + type (two fields or nested) │  │
│            └─────────────────────────────────────────────────────────────┘  │
│                                                                             │
│    3c'. APPLY SMART COMPRESSION EXPANSION (if smartCompression = true)      │
│                                                                             │
│        Smart compression writes simple names for same-schema types.         │
│        On deserialization, we must expand simple names back to full URIs.   │
│        See: 05-global-options.md section 1.6 for full rules.                │
│                                                                             │
│        Is the extracted type value a SIMPLE NAME (no "#" or "://")?         │
│        ├─ YES → Expand using context schema:                                │
│        │        - Context schema from: root's URI, CODEC_ROOT_SCHEMA, etc.  │
│        │        - Result: contextSchema + "#//" + simpleName                │
│        │        - Example: "Person" → "http://example.org/1.0#//Person"     │
│        │                                                                    │
│        └─ NO (already full URI) → Use value as-is                           │
│                                                                             │
│        Note: This is the REVERSE of serialization step 3b in section 5.     │
│        If serialization compressed "http://example.org/1.0#//Person" to     │
│        "Person", deserialization expands it back.                           │
│                                                                             │
│    3d. RESOLVE EClass based on CONFIGURED strategy (NO auto-detection!)     │
│                                                                             │
│        Note: If smartCompression was applied in 3c', value is now a         │
│        full URI. The strategy-based resolution below handles both cases.    │
│                                                                             │
│        Strategy-based resolution:                                           │
│        ┌─────────────────────────────────────────────────────────────────┐  │
│        │ Strategy        │ Resolution                                    │  │
│        ├─────────────────┼───────────────────────────────────────────────┤  │
│        │ URI             │ Parse full URI (scheme://...#//ClassName)     │  │
│        │ NAME            │ Simple name + context schema                  │  │
│        │ CLASS           │ Lookup by instanceClassName                   │  │
│        │ NUMERIC         │ Classifier ID + hint package (REQUIRED!)      │  │
│        │ SCHEMA_AND_TYPE │ Compose URI from extracted schema + name      │  │
│        └─────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│    Resolved? ─────────────────────────────────────────────→ RESOLVED ✓      │
│    Not resolved? → Continue to step 4                                       │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 4. FALLBACK (see section 6.5.1 for TypeHintMode details)                    │
│    ─────────────────────────────────────────────────────────────────────────│
│    Check CODEC_TYPE_HINT_MODE (default: HINT)                               │
│                                                                             │
│    ┌─────────────────────────────────────────────────────────────────────┐  │
│    │ HINT MODE (default)                                                 │  │
│    │ ───────────────────────────────────────────────────────────────────│  │
│    │ Try in order:                                                       │  │
│    │   1. EReference.getEReferenceType() (if concrete)                   │  │
│    │   2. CODEC_FEATURE_TYPE_HINTS (per-feature runtime hint)            │  │
│    │   3. CODEC_ROOT_TYPE (root type hint)                               │  │
│    │                                                                     │  │
│    │ If resolved → log WARNING, RESOLVED ✓                               │  │
│    │ If reference type is abstract and no hints resolve → step 4c        │  │
│    └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│    ┌─────────────────────────────────────────────────────────────────────┐  │
│    │ OVERRIDE MODE                                                       │  │
│    │ ───────────────────────────────────────────────────────────────────│  │
│    │ Try in order (reference type IGNORED):                              │  │
│    │   1. CODEC_FEATURE_TYPE_HINTS (per-feature runtime hint)            │  │
│    │   2. CODEC_ROOT_TYPE (root type hint)                               │  │
│    │                                                                     │  │
│    │ If resolved → log WARNING, RESOLVED ✓                               │  │
│    │ If neither hint is set → ERROR (fail, no implicit fallback)         │  │
│    └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│    4c. FINAL FAILURE (no fallback resolved)                                 │
│                                                                             │
│        Is this the ROOT object?                                             │
│        ├─ YES → ERROR (mandatory - cannot deserialize without root type)    │
│        │        DeserializationMode doesn't apply here                      │
│        │                                                                    │
│        └─ NO (nested object) → Check DeserializationMode:                   │
│             ├─ LENIENT (default) → WARNING + skip:                          │
│             │    - Single reference: set to null                            │
│             │    - Multi reference: don't add element to collection         │
│             │                                                               │
│             └─ STRICT → ERROR                                               │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 5. SUPERTYPE PARSING (after type resolution complete)                       │
│    ─────────────────────────────────────────────────────────────────────────│
│    See [SuperType Serialization](07-supertype.md) section 8 for full details.│
│                                                                             │
│    SuperType parsing is OPTIONAL and does NOT affect type resolution.       │
│    The type (EClass) is already resolved from steps 1-4.                    │
│                                                                             │
│    If type field was detected as PLAIN format:                              │
│      → Look for standalone supertype field (superTypeKey, default _supertype)│
│      → Parse if present (array or separator-joined string)                  │
│                                                                             │
│    If type field was detected as STRUCTURED format:                         │
│      → Look for supertype inside _type object (key: supertype)              │
│      → Parse if present (array or separator-joined string)                  │
│                                                                             │
│    Validation (based on DeserializationMode):                               │
│      ┌─────────────────────────────────────────────────────────────────────┐│
│      │ LENIENT (default) │ Parse supertype, IGNORE values (no validation)  ││
│      │ STRICT            │ Validate declared supertypes match EClass       ││
│      │                   │ hierarchy. Fail if mismatch.                    ││
│      │ AUTO_DETECT       │ Same as LENIENT for supertype                   ││
│      └─────────────────────────────────────────────────────────────────────┘│
│                                                                             │
│    Note: SuperType parsing happens even if discriminator mapping (steps 1-2)│
│    resolved the type. SuperType is independent of how type was resolved.    │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 6.3.1 Resolution Priority Summary

| Priority | Mechanism | Configured On | Applies To |
|----------|-----------|---------------|------------|
| 1 | Type Mapping Registry | EClass (base) | All objects of that type |
| 2 | Inline Mapping | EReference | Objects accessed via that reference |
| 3 | Type Strategy | EClass, EReference, Global | Standard type field resolution |
| 4 | Fallback | Load options | Last resort when above fail |

> **Design rationale:** Type Mapping Registry has highest priority because it's our implementation of Jackson's `@JsonTypeInfo` + `@JsonSubTypes` pattern - the primary, flexible mechanism for polymorphic type handling. Inline Mapping is a simpler, more static variant for per-reference cases.

> **Important - Symmetry requirement:** The `typeStrategy` and `smartCompression` settings must match between serialization and deserialization. Format can be auto-detected, but strategy cannot.

> **Future feature:** Auto-detection of strategy from content is not currently supported.

**Example (PLAIN + URI):**
```json
{
  "_type": "http://example.org/person/1.0#//Person",
  "name": "John"
}
```
The deserializer extracts `http://example.org/person/1.0` as namespace URI and `Person` as class name.

**Example (STRUCTURED + SCHEMA_AND_TYPE):**
```json
{
  "_type": {
    "schema": "http://example.org/person/1.0",
    "type": "Person"
  },
  "name": "John"
}
```

### 6.3.2 Unknown Type Handling

When the deserializer cannot resolve a type value to a known EClass, the behavior depends on whether a **type hint** is available.

#### Fallback to Type Hint

If a `CODEC_ROOT_TYPE` hint is provided (or the reference type is concrete), the deserializer uses it as fallback:

| Scenario | Behavior | Severity |
|----------|----------|----------|
| Unknown type value, hint available | Use hint, continue | WARNING |
| Unknown type value, no hint | Fail | ERROR |

**Example - Unknown type with hint (succeeds):**
```java
Map<String, Object> options = Map.of(
    CodecResourceOptions.CODEC_ROOT_TYPE, PersonPackage.Literals.PERSON
);
resource.load(inputStream, options);
// JSON: {"_type": "UnknownType", "name": "John"}
// Result: WARNING logged, Person created using hint
```

**Example - Unknown type without hint (fails):**
```java
resource.load(inputStream, Collections.emptyMap());
// JSON: {"_type": "UnknownType", "name": "John"}
// Result: ERROR - "Cannot deserialize: no type information found and no CODEC_ROOT_TYPE hint"
```

#### Resolution Order

When resolving type values, the deserializer tries multiple strategies:

1. **Full URI** - If value matches EMF URI pattern (`scheme://...#//ClassName`), resolve directly
2. **Java class name** - If value contains `.`, look up by instance class name
3. **Discriminator mapping** - Check TypeDiscriminatorService for mapped values
4. **Simple name** - Look up by class name in context schema
5. **Numeric ID** - Parse as classifier ID within context schema
6. **Fallback** - Use hint if available, otherwise ERROR

#### Diagnostic Messages

| Scenario | Message |
|----------|---------|
| Type value not resolved | `Could not resolve EClass from type value: {value}` |
| No type info and no hint | `Cannot deserialize: no type information found and no CODEC_ROOT_TYPE hint` |

See [Error Handling](00-overview.md#2-error-and-warning-handling) for the complete error scenarios table.

### 6.4 Context Schema and NAME Strategy

The **NAME strategy** writes only the simple EClass name (e.g., `"Person"` instead of `"http://example.org/1.0#//Person"`). For deserialization, this requires a **Context Schema** to resolve the simple name back to a full EClass URI.

#### 6.4.1 CODEC_ROOT_SCHEMA Option

Explicitly sets the context schema for deserialization:

```java
Map<String, Object> options = new HashMap<>();
options.put(CodecResourceOptions.CODEC_ROOT_SCHEMA, "http://geojson.org/1.0");
resource.load(inputStream, options);
```

With this option, all simple type names are resolved against the specified schema:
- `"type": "Point"` → `http://geojson.org/1.0#//Point`
- `"type": "Feature"` → `http://geojson.org/1.0#//Feature`

**Example (GeoJSON-like):**
```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "geometry": {
        "type": "Point",
        "coordinates": {"longitude": 8.68, "latitude": 50.11}
      }
    }
  ]
}
```

#### 6.4.2 CODEC_ROOT_TYPE Option

Specifies the expected root EClass for deserialization. **Additionally**, it implicitly sets the context schema from the EClass's package URI.

```java
Map<String, Object> options = new HashMap<>();
options.put(CodecResourceOptions.CODEC_ROOT_TYPE, GeoJsonPackage.eINSTANCE.getFeatureCollection());
resource.load(inputStream, options);
```

This provides **two benefits**:
1. Root object type is known → no `_type` needed at root level
2. Context schema is established → nested objects can use simple names

**Example (no _type at root needed):**
```json
{
  "features": [
    {
      "type": "Feature",
      "geometry": {
        "type": "Point",
        "coordinates": {"longitude": 8.68, "latitude": 50.11}
      }
    }
  ]
}
```

#### 6.4.3 Context Schema Resolution Order

| Source | Priority | Description |
|--------|----------|-------------|
| `CODEC_ROOT_SCHEMA` | 1 (highest) | Explicitly provided schema URI |
| `CODEC_ROOT_TYPE` | 2 | Extracted from hint EClass's package |
| First full URI in content | 3 | Smart compression: schema from root's `_type` |

> **Security (S-4):** Without a context schema, NAME, CLASS, and NUMERIC strategies **fail with a warning diagnostic** instead of scanning all registered EPackages. This prevents type confusion when multiple packages define classes with the same name. Always provide `CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE` when using these strategies.

NAME strategy uses `MetadataIndexReader.findByClassName(nsURI, className)` for context-aware lookup. See [CLASS Strategy Resolution](#645-class-strategy-resolution) for the full MetadataIndex API.

#### 6.4.4 Smart Compression

When the root object uses a full URI, the schema is automatically extracted and used for nested objects:

```json
{
  "_type": "http://example.org/company/1.0#//Company",
  "name": "Acme Inc",
  "employees": [
    {"_type": "Person", "name": "Alice"},
    {"_type": "Person", "name": "Bob"}
  ]
}
```

The deserializer:
1. Parses root `_type`: `http://example.org/company/1.0#//Company`
2. Extracts context schema: `http://example.org/company/1.0`
3. Resolves `"Person"` → `http://example.org/company/1.0#//Person`

> **Important:** Smart compression requires symmetric configuration. If content was serialized with `smartCompression=true`, deserialization must also use `smartCompression=true`. A mismatch produces a WARNING. See [Global Options - Smart Compression](05-global-options.md#1-smart-compression) for details.

#### 6.4.5 CLASS Strategy Resolution

The **CLASS strategy** writes the Java class name (`EClass.getInstanceClassName()`) as the type value. Deserialization must reverse this lookup.

##### Serialization

```java
// Writes: "_type": "org.example.model.impl.PersonImpl"
String typeValue = eClass.getInstanceClassName();
```

> **Requirement:** `EClass.getInstanceClassName()` must be non-null. This is typical for EMF-generated code. If null, serialization fails with ERROR.

##### Deserialization

CLASS strategy resolution uses the **MetadataService** to lookup EClass by `instanceClassName`. The resolution follows the **context schema** pattern (same as NAME strategy):

**ROOT OBJECT:**
```
1. Check CODEC_ROOT_TYPE hint → extract context package nsURI
2. Check CODEC_ROOT_SCHEMA hint → use as context package nsURI
3. If context nsURI exists:
   → MetadataService.findByInstanceClassName(nsUri, className)
   → If found → RESOLVED ✓
4. No hint or not found in context package:
   → WARNING diagnostic: "CLASS strategy requires CODEC_ROOT_SCHEMA or CODEC_ROOT_TYPE"
   → Resolution returns null (S-4: no global EPackage scan)
```

**NESTED OBJECT:**
```
1. Get context from EReference.getEReferenceType().getEPackage().getNsURI()
2. MetadataService.findByInstanceClassName(contextNsUri, className)
3. If found → RESOLVED ✓
4. If not found in context package:
   → WARNING diagnostic: resolution fails (S-4: no global EPackage scan)
```

##### Ambiguous Resolution (Multiple Matches)

When multiple EClasses share the same `instanceClassName` (common with typed maps like `java.util.Map$Entry`), and no context schema narrows the search:

- **Severity:** ERROR
- **Message:** `Ambiguous instanceClassName "{className}": found in multiple packages: [{uri1}, {uri2}, ...]. Use CODEC_ROOT_SCHEMA to disambiguate.`

This is a **standard EMF pattern** (typed map entries), so we cannot treat duplicate `instanceClassName` as a configuration error. Instead, users must provide context via `CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE`.

##### Recommendation

**Strongly recommend** using `CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE` with CLASS strategy:

```java
// Good: Context provided, unambiguous resolution
Map<String, Object> options = Map.of(
    CodecResourceOptions.CODEC_ROOT_SCHEMA, "http://example.org/model/1.0"
);
resource.load(inputStream, options);

// Risky: No context, may fail if className exists in multiple packages
resource.load(inputStream, Collections.emptyMap());
```

##### MetadataIndex API

The `MetadataIndexReader` (accessed via `MetadataService.getIndexReader()`) provides indexed lookup for CLASS strategy resolution:

```java
interface MetadataIndexReader {
    // Lookup by instanceClassName within a specific package (context-aware)
    ClassMetadata findByInstanceClassName(String nsURI, String instanceClassName);

    // Lookup across all registered packages (may return multiple)
    EList<ClassMetadata> findAllByInstanceClassName(String instanceClassName);

    // Similar methods for NAME strategy
    ClassMetadata findByClassName(String nsURI, String className);
    EList<ClassMetadata> findAllByClassName(String className);

    // URI strategy uses direct lookup
    ClassMetadata findClassByURI(String uri);
}
```

**Usage Example:**
```java
MetadataService metadataService = ...; // OSGi service or injected
MetadataIndexReader index = metadataService.getIndexReader();

// Context-aware lookup (preferred)
String contextNsURI = eReference.getEReferenceType().getEPackage().getNsURI();
ClassMetadata meta = index.findByInstanceClassName(contextNsURI, "org.example.PersonImpl");

// Global search (when no context available)
EList<ClassMetadata> matches = index.findAllByInstanceClassName("org.example.PersonImpl");
if (matches.size() == 1) {
    // Unambiguous
} else if (matches.size() > 1) {
    // ERROR: Ambiguous, need CODEC_ROOT_SCHEMA
}
```

The index is built automatically when EPackages are registered via `MetadataService.registerPackage()`. Current implementation (`MapBasedMetadataIndex`) uses in-memory ConcurrentHashMaps; future versions may use Lucene for large-scale deployments.

### 6.5 Type Resolution Rules

| Content has `_type` | `CODEC_ROOT_TYPE` set | Behavior |
|---------------------|-------------------------|----------|
| Yes (full URI) | No | Use content type, establish context schema |
| Yes (simple name) | No | Resolve via context schema; WARNING if no schema (S-4) |
| Yes | Yes | Use content type, context schema from hint |
| No | Yes | Use hint type, context schema from hint |
| No | No | **ERROR**: Cannot determine type |

When both content type and hint are present but differ:
- Log a WARNING
- Continue with content type (content wins)
- Example: Hint says `Person`, content says `Employee` → use `Employee`, log warning

#### 6.5.1 Type Hint Mode (CODEC_TYPE_MODE)

By default, `CODEC_ROOT_TYPE` and `CODEC_ROOT_SCHEMA` behave as **hints/fallbacks**, not as highest-priority overrides. This is an **intentional deviation** from the standard configuration hierarchy (where Load/Save options have highest priority).

**Rationale:**
- Most common use case is "help deserialize when type info is missing"
- Users expect content type information to be respected
- Forcing override by default would cause unexpected behavior

| Mode | Behavior | Use Case |
|------|----------|----------|
| `HINT` **(default)** | Content type wins when present; hint used as fallback | General deserialization, flexible input handling |
| `OVERRIDE` | Hint always wins for root object, ignoring content type | Force-deserialize as specific type regardless of content |

**HINT Mode (default):**
```
Type Resolution Priority:
1. Content type information (from JSON _type field) - wins when present
2. CODEC_ROOT_TYPE / CODEC_ROOT_SCHEMA - used as fallback when content has no type
3. Reference type (EReference.getEReferenceType()) - for nested objects
4. ERROR - if no type determinable
```

**OVERRIDE Mode:**
```
Type Resolution Priority:
1. CODEC_ROOT_TYPE - always wins for root object (follows strict config hierarchy)
2. Content type information - ignored for root object
3. Reference type - for nested objects (CODEC_ROOT_TYPE only affects root)
```

**Configuration:**
```java
Map<String, Object> options = new HashMap<>();
options.put(CodecResource.CODEC_ROOT_TYPE, PersonPackage.Literals.PERSON);
options.put(CodecResource.CODEC_TYPE_MODE, TypeHintMode.OVERRIDE);  // Force type
resource.load(inputStream, options);
```

**Example - HINT mode (default):**
```java
// JSON has type info - content wins, hint ignored
String json = """
    { "_type": "http://example.org#//Employee", "name": "John" }
    """;

options.put(CODEC_ROOT_TYPE, PersonPackage.Literals.PERSON);
// Result: Deserializes as Employee (content wins)
// If Employee is subtype of Person: no warning
// If incompatible: WARNING logged but content still wins
```

**Example - OVERRIDE mode:**
```java
// Force deserialize as specific type, ignore content type
String json = """
    { "_type": "http://example.org#//OldType", "name": "John" }
    """;

options.put(CODEC_ROOT_TYPE, PersonPackage.Literals.PERSON);
options.put(CODEC_TYPE_MODE, TypeHintMode.OVERRIDE);
// Result: Deserializes as Person (override mode, content type ignored)
```

> **Note:** This behavior deviates from the general configuration hierarchy principle where Load/Save options (Level 1) have highest priority. The deviation exists for practical usability reasons.

#### 6.5.2 Deserialization Mode (Type Resolution Strictness)

The `DeserializationMode` controls how strictly the deserializer follows the configured type strategy when resolving types.

| Mode | Behavior | Use Case |
|------|----------|----------|
| `STRICT` | Type field MUST match configured strategy exactly; missing/malformed → ERROR | Guaranteed format compliance |
| `LENIENT` **(default)** | Try configured strategy first, then fallback resolution | Maximum interoperability |
| `AUTO_DETECT` | Ignore configured strategy; probe JSON structure to determine format | Unknown/mixed formats |

**Why LENIENT is default:**
- Most users want deserialization to succeed when possible
- Real-world JSON often has minor variations
- STRICT is available for those who need format guarantees
- LENIENT + warnings provides best of both worlds

**STRICT Mode:**
```java
// Config says SCHEMA_AND_TYPE, but JSON only has simple name → ERROR
CodecConfiguration config = CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.SCHEMA_AND_TYPE)
    .deserializationMode(DeserializationMode.STRICT)
    .build();

// JSON: {"_type": "Person", "name": "John"}  // No _schema field
// Result: ERROR - "Missing schema field, config requires SCHEMA_AND_TYPE"
```

**LENIENT Mode (default):**
```java
// Config says SCHEMA_AND_TYPE, but JSON only has simple name → try fallback
CodecConfiguration config = CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.SCHEMA_AND_TYPE)
    // .deserializationMode(DeserializationMode.LENIENT)  // default
    .build();

// JSON: {"_type": "Person", "name": "John"}
// Result: WARNING logged, resolves "Person" using CODEC_ROOT_SCHEMA or hint
```

**AUTO_DETECT Mode:**
```java
// Ignore config, probe JSON structure
CodecConfiguration config = CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.URI)  // Config says URI
    .deserializationMode(DeserializationMode.AUTO_DETECT)
    .build();

// JSON: {"_type": "Person", "name": "John"}  // Simple name
// Result: Auto-detects NAME format, resolves using context
```

**Resolution Behavior by Mode:**

| Scenario | STRICT | LENIENT | AUTO_DETECT |
|----------|--------|---------|-------------|
| Missing type field | ERROR | Use hints | Use hints |
| Wrong format | ERROR | Try fallbacks | Probe format |
| Unknown type value | ERROR | WARNING + fallback | WARNING + fallback |

**Warning Messages (LENIENT mode):**
```
WARNING: Type resolved via fallback. Config: typeStrategy=SCHEMA_AND_TYPE,
         but type "Person" resolved using CODEC_ROOT_SCHEMA hint.
         Consider updating config or JSON to match.
```

> **Note:** `DeserializationMode` controls **type resolution** strictness. For **feature handling** strictness (unknown/missing features), see [Feature Strictness](11-feature.md#7-feature-strictness).

### 6.6 Field Order Independence

JSON field order is **irrelevant** for deserialization. The `_type` field can appear anywhere in the object - data fields appearing before it are deferred and processed after type resolution.

> **See also:** [Architecture](01-architecture.md) (section 5.1 - Deferred Properties) for the general mechanism that applies to all metadata fields (`_type`, `_id`, etc.).

### 6.7 Polymorphic Containment References

When an EReference points to an abstract EClass, the concrete type must be specified in the JSON:

**Model:**
```
Geometry (abstract)
  ├── Point
  ├── LineString
  └── Polygon

Feature
  └── geometry: Geometry (containment)
```

**JSON:**
```json
{
  "_type": "http://geojson.org/1.0#//Feature",
  "geometry": {
    "_type": "http://geojson.org/1.0#//Point",
    "coordinates": {"longitude": 8.68, "latitude": 50.11}
  }
}
```

**With context schema (NAME strategy):**
```json
{
  "type": "Feature",
  "geometry": {
    "type": "Point",
    "coordinates": {"longitude": 8.68, "latitude": 50.11}
  }
}
```

The deserializer:
1. Gets the reference type (`Geometry`) as a hint
2. Reads `_type`/`type` from the nested object
3. Resolves concrete class (`Point`) which must be a subtype of the hint
4. If no `_type` and reference type is **concrete**: uses reference type
5. If no `_type` and reference type is **abstract**: **ERROR**

---

## 6. Real-World Example: GeoJSON

This section demonstrates deserialization of real [GeoJSON](https://geojson.org/) data using the [org.geojson.model](https://github.com/eclipse-fennec/org.eclipse.fennec.emf.models/tree/main/org.geojson.model) EMF model.

### 6.1 The GeoJSON Model

The GeoJSON model uses several codec-relevant features:

1. **`type` as discriminator** - GeoJSON uses `"type": "Point"` instead of `"_type"`
2. **ExtendedMetaData for feature names** - The `data` attribute maps to JSON key `coordinates`
3. **Array data types** - Coordinates are `double[]`, `double[][]`, `double[][][]` arrays
4. **Polymorphic containment** - `Feature.geometry` can be Point, LineString, Polygon, etc.

**Model structure:**
```
GeoJsonObject (abstract)
  └── bbox: double[] (via ExtendedMetaData: "bbox")

Geometry (interface, extends GeoJsonObject)
  ├── Point
  │     └── data: double[] (via ExtendedMetaData: "coordinates")
  ├── LineString
  │     └── data: double[][] (via ExtendedMetaData: "coordinates")
  ├── Polygon
  │     └── data: double[][][] (via ExtendedMetaData: "coordinates")
  └── ...

Feature (extends GeoJsonObject)
  ├── id: String
  ├── geometry: Geometry (containment)
  └── properties: EObject (containment)

FeatureCollection (extends GeoJsonObject)
  └── features: Feature[*] (containment)
```

### 6.2 Configuration

To deserialize real GeoJSON, configure:

```java
CodecConfiguration config = CodecConfiguration.builder()
    .typeKey("type")                        // GeoJSON uses "type" not "_type"
    .useNamesFromExtendedMetaData(true)     // Map "coordinates" → data attribute
    .build();

Map<String, Object> options = new HashMap<>();
options.put(CodecResource.CODEC_ROOT_SCHEMA, "https://geojson.org/model/2016");

resource.load(inputStream, options);
```

### 6.3 Example: Point

**GeoJSON Input:**
```json
{
  "type": "Point",
  "coordinates": [8.6821, 50.1109]
}
```

**Deserialization:**
1. `"type": "Point"` → resolves to `GeoJsonPackage.eINSTANCE.getPoint()`
2. `"coordinates"` → maps to `data` attribute (via ExtendedMetaData)
3. `[8.6821, 50.1109]` → deserialized as `double[]`

### 6.4 Example: Polygon with BBox

**GeoJSON Input:**
```json
{
  "type": "Polygon",
  "bbox": [11.504, 50.895, 11.567, 50.913],
  "coordinates": [
    [
      [11.554, 50.901],
      [11.554, 50.901],
      [11.554, 50.900],
      [11.554, 50.901]
    ]
  ]
}
```

**Deserialization:**
1. `"type": "Polygon"` → `Polygon` EClass
2. `"bbox"` → `double[4]` bounding box array
3. `"coordinates"` → `double[][][]` (rings containing coordinate arrays)

### 6.5 Example: FeatureCollection

**GeoJSON Input:**
```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "id": "berlin",
      "geometry": {
        "type": "Point",
        "coordinates": [13.405, 52.52]
      },
      "properties": null
    },
    {
      "type": "Feature",
      "id": "frankfurt",
      "geometry": {
        "type": "Point",
        "coordinates": [8.682, 50.110]
      },
      "properties": null
    }
  ]
}
```

**Deserialization:**
1. Root: `FeatureCollection` with 2 features
2. Each feature: polymorphic `geometry` resolved by nested `"type"` field
3. Coordinates deserialized as `double[]` arrays

### 6.6 Key Takeaways

| GeoJSON Requirement | Codec Configuration |
|---------------------|---------------------|
| `type` as discriminator | `.typeKey("type")` |
| `coordinates` → `data` | `.useNamesFromExtendedMetaData(true)` |
| Context schema for NAME | `CODEC_ROOT_SCHEMA` option |
| Array coordinates | Automatic (see [Feature Serialization - Array Attributes](11-feature.md#6-array-attributes)) |

---

## 7. Configuration Validation Rules

This section documents validation rules for Type Configuration that are checked when building the effective configuration.

### 7.1 Property Applicability Rules

| Rule ID | Property | Invalid At | Severity | Description |
|---------|----------|------------|----------|-------------|
| T-V1 | `typeValueReaderName` | EReference | ERROR | Value reader is class-intrinsic (applies to objects of that EClass, not reference-specific) |
| T-V2 | `typeValueWriterName` | EReference | ERROR | Value writer is class-intrinsic (applies to objects of that EClass, not reference-specific) |
| T-V3 | `typeScope` | EAnnotation | WARNING | Runtime-only property, ignored if found in EAnnotation |
| T-V4 | `typeFormatScope` | EAnnotation | WARNING | Runtime-only property, ignored if found in EAnnotation |
| T-V5 | Any `type*` key | EAttribute | ERROR | Type configuration not applicable to attributes |
| T-V6 | `typeDiscriminatorPath` | EReference | ERROR | Discriminator path is per-class (see [08-discriminator-mapping.md](08-discriminator-mapping.md)) |
| T-V7 | `typeDiscriminator` | EReference | ERROR | Discriminator value is per-class (see [08-discriminator-mapping.md](08-discriminator-mapping.md)) |

### 7.2 Strategy-Specific Rules

| Rule ID | Condition | Severity | Description |
|---------|-----------|----------|-------------|
| T-V10 | CLASS strategy + `instanceClassName` is null | ERROR | CLASS strategy requires `EClass.getInstanceClassName()` to be set |

### 7.3 Format-Dependent Warnings

| Rule ID | Condition | Severity | Description |
|---------|-----------|----------|-------------|
| T-V20 | `typeNameKey` set + format=PLAIN | WARNING | `typeNameKey` is only used in STRUCTURED format; value ignored |
| T-V21 | `typeSchemaKey` set + format=PLAIN + strategy≠SCHEMA_AND_TYPE | WARNING | `typeSchemaKey` is only used in STRUCTURED format or PLAIN+SCHEMA_AND_TYPE; value ignored |

### 7.4 Serialization/Deserialization Symmetry

These properties must be configured **symmetrically** for serialization and deserialization to work correctly:

| Property | Auto-Detectable? | Symmetry Required | Mismatch Behavior |
|----------|------------------|-------------------|-------------------|
| `typeStrategy` | ❌ NO | ✅ YES - must match | Type resolution fails or produces wrong EClass |
| `typeFormat` | ✅ YES (from JSON structure) | ❌ NO | N/A - auto-detected |
| `smartCompression` | ❌ NO | ✅ YES - must match | WARNING, type may not resolve correctly |
| `typeKey` | ❌ NO | Should match | Deserializer auto-recognizes common keys (_type, @type, _class, eClass) |

> **Future feature:** Auto-detection of `typeStrategy` and `smartCompression` from content is not currently supported.

### 7.6 Related Validation Rules

- **SuperType Configuration:** See [07-supertype.md section 6.0](07-supertype.md#60-configuration-constraints) for SuperType-specific validation rules
- **Discriminator Mapping:** See [08-discriminator-mapping.md](08-discriminator-mapping.md) for discriminator-related validation rules

---

[Next: SuperType Serialization →](07-supertype.md)
