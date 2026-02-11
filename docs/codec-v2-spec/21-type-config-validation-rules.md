# Type Configuration Validation Rules

[← Code Conventions](20-code-conventions.md) | [Annotation Reference](16-annotation-reference.md)

---

> **⚠️ DEPRECATED**: This document is deprecated. Validation rules have been moved to the authoritative spec documents:
>
> | Topic | Authoritative Location |
> |-------|------------------------|
> | **Type Configuration** | [06-type.md section 7](06-type.md#7-configuration-validation-rules) |
> | **SuperType Configuration** | [07-supertype.md section 6.0](07-supertype.md#60-configuration-constraints) |
> | **Discriminator Mapping** | [08-discriminator-mapping.md](08-discriminator-mapping.md) |
>
> This document is retained temporarily for reference during migration but will be removed in a future cleanup.

---

## Document Purpose & Workflow (DEPRECATED)

This document captures validation rules for Type Configuration to enable **test generation**.

### Workflow Instructions (MUST FOLLOW)

1. **Start from spec** - Read annotation summary + detailed sections (16-annotation-reference.md, 06-type.md)

2. **When unclear** - Follow these TWO steps:
   - **First:** Look in the old v2 code (serializers/deserializers) to extract the actual logic
   - **Second:** Compare what you found with what the spec says (your understanding of what the property does and its validation rules)

3. **If STILL unclear after steps 1 & 2** - **ASK THE USER** before guessing!
   - Don't make assumptions about behavior
   - Don't invent validation rules
   - Present what you found and ask for clarification

4. **When a question is clarified** - **UPDATE THE SPEC IMMEDIATELY**
   - The spec is the source of truth
   - Any clarification must be reflected in the corresponding spec chapters (06-type.md, 16-annotation-reference.md, etc.)
   - This ensures the same question doesn't arise again in future work
   - Update ALL relevant locations where the clarification applies

5. **Document validation rules** - Create structured reference with R/W rules, language descriptions, and examples

6. **Generate tests** - Use this document to create config tests (easier to review a table than 50+ tests)

### Sources

| Source | Location | Purpose |
|--------|----------|---------|
| Spec - Annotation Reference | `16-annotation-reference.md` | Valid levels, keys, defaults |
| Spec - Type Serialization | `06-type.md` | Serialization/deserialization behavior |
| Old v2 Serializer | `codec/ser/TypeSerializationEntry.java` | Write (W) logic |
| Old v2 Deserializer | `codec/deser/TypeDeserializationEntry.java` | Read (R) logic |
| New Config Classes | `codec.api/config/TypeConfig.java`, `ConfigProperty.java` | Defaults, validation |

---

## Legend

| Abbreviation | Meaning |
|--------------|---------|
| **R** | Read (Deserialization) |
| **W** | Write (Serialization) |
| **RW** | Both Read and Write |
| **G** | Global level |
| **C** | EClass level |
| **F** | Feature (EReference) level |
| **A** | EAttribute level |
| **N/A** | Not Applicable |
| **REQ** | Required |
| **OPT** | Optional |

---

## Type Configuration Properties

### Overview Table

| Property | Annotation Key | Default | Levels | Direction | Section |
|----------|---------------|---------|--------|-----------|---------|
| typeStrategy | `typeStrategy` | `URI` | G, C, F | RW | [1](#1-typestrategy) |
| typeFormat | `typeFormat` | `PLAIN` | G, C, F | RW | [2](#2-typeformat) |
| typeKey | `typeKey` | `_type` | G, C, F | RW | [3](#3-typekey) |
| typeNameKey | `typeNameKey` | `type` | G, C, F | RW | [4](#4-typenamekey) |
| typeSchemaKey | `typeSchemaKey` | `schema` | G, C, F | RW | [5](#5-typeschemakey) |
| typeScope | — | `ALL` | G only | RW | [6](#6-typescope) |
| typeFormatScope | — | `ALL` | G only | RW | [7](#7-typeformatscope) |
| typeValueReaderName | `typeValueReaderName` | `null` | G, C | R | [8](#8-typevaluereaderwritername) |
| typeValueWriterName | `typeValueWriterName` | `null` | G, C | W | [8](#8-typevaluereaderwritername) |
| discriminatorPath | `typeDiscriminatorPath` | `null` | C | RW | [9](#9-discriminator-related) |
| discriminatorValue | `typeDiscriminator` | `null` | C | RW | [9](#9-discriminator-related) |

### Dependent Global Properties

These properties are **not** part of Type Configuration but affect type serialization/deserialization behavior at runtime:

| Property | Default | Direction | Effect on Type | Spec Reference |
|----------|---------|-----------|----------------|----------------|
| `smartCompression` | `false` | RW | When `true`, contained objects with same schema as root use simple type names instead of full URIs | [05-global-options.md](05-global-options.md#1-smart-compression) |
| `deserializationMode` | `LENIENT` | R | Controls error handling when type cannot be resolved: `STRICT`=ERROR, `LENIENT`=WARNING+fallback | [06-type.md](06-type.md#552-deserialization-mode-type-resolution-strictness) |

> **Note:** These dependencies only matter for Phase 2 (runtime tests). Phase 1 config tests do not need to consider these properties.

#### Serialization/Deserialization Symmetry

The following must be configured **symmetrically** for serialization and deserialization:

| Property | Auto-detect? | Symmetry Required |
|----------|--------------|-------------------|
| `typeStrategy` | ❌ NO | ✅ YES - must match |
| `typeFormat` | ✅ YES | ❌ NO - can be auto-detected from JSON structure |
| `smartCompression` | ❌ NO | ✅ YES - must match |
| `typeKey` | — | Should match (deserializer recognizes common keys automatically) |

**Mismatch handling:**
- Strategy mismatch: Type resolution likely fails or produces wrong EClass
- Smart compression mismatch: WARNING, type may not resolve correctly

> **Future feature:** Auto-detection of `typeStrategy` and `smartCompression` from content is not currently supported. The user must explicitly configure both sides to match.
---

## 1. typeStrategy

**What it does:** Controls **what information** is written to identify the object's type.

### Valid Values

| Value | W Output | R Input Requirement |
|-------|----------|---------------------|
| `URI` (default) | Full EClass URI: `"http://example.org#//Person"` | Self-describing, resolvable via EPackage registry |
| `NAME` | Simple EClass name: `"Person"` | Requires schema context (`CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE`) |
| `CLASS` | Java class name: `"org.example.PersonImpl"` | **Requires** `EClass.getInstanceClassName()` (ERROR if null). See [06-type.md section 6.4.5](06-type.md#645-class-strategy-resolution) for deserialization details. |
| `SCHEMA_AND_TYPE` | Two fields or structured object | Schema + name combined |
| `NUMERIC` | EMF classifier ID: `"3"` | Requires schema context, IDs are positional (fragile) |
| `NONE` | No type field written | `CODEC_ROOT_TYPE` required, or reference must be concrete |

### Serialization (W) Behavior

```
W: Strategy determines what value is written
   - URI     → EcoreUtil.getURI(eClass).toString()
   - NAME    → eClass.getName()
   - CLASS   → eClass.getInstanceClassName() (ERROR if null)
   - NUMERIC → String.valueOf(eClass.getClassifierID())
   - SCHEMA_AND_TYPE (PLAIN)  → TWO fields: _schema + _type (simple name)
   - SCHEMA_AND_TYPE (STRUCT) → Nested object: {schema: "...", type: "..."}
   - NONE    → No _type field written
```

**Code Reference:** `TypeSerializationEntry.resolveTypeValue()` (lines 335-361)

### Deserialization (R) Behavior

```
R: Deserializer resolves EClass based on strategy and input format
   Resolution order:
   1. If value matches full URI pattern (scheme://...#//ClassName) → resolve directly
   2. If smartCompression=true → try context schema + simple name
   3. Try discriminator lookup (TypeDiscriminatorService)
   4. Based on configured strategy:
      - NAME → search all packages by simple name
      - CLASS → search by instance class name
      - NUMERIC → find by classifier ID (needs hint package)
      - SCHEMA_AND_TYPE → schema + simple name → compose URI
```

> **Note:** Full URI pattern requires scheme, host, and `#//` followed by EClass name (e.g., `http://example.org/model#//Person`). A string ending with just `#//` is NOT a valid URI.

**Code Reference:** `TypeDeserializationEntry.resolveEClass()` (lines 401-464)

### Validation Rules

#### Configuration-Level Rules (testable without serialization)

| Rule | Severity | Description | Spec Reference |
|------|----------|-------------|----------------|
| V1.4 | ERROR | CLASS strategy requires EClass.getInstanceClassName() to be set | [06-type.md section 1.2](06-type.md#12-class-strategy) |

#### Runtime-Level Rules (require serialization to test) - FUTURE

| Rule | Direction | Severity | Description | Spec Reference |
|------|-----------|----------|-------------|----------------|
| V1.1 | R | WARNING | NAME strategy without CODEC_ROOT_SCHEMA is non-deterministic | [06-type.md section 6.4](06-type.md#64-context-schema-and-name-strategy) |
| V1.2 | R | ERROR | NUMERIC strategy requires hint (CODEC_ROOT_SCHEMA or CODEC_ROOT_TYPE) | [06-type.md section 6.3.0 step 3c](06-type.md#630-type-resolution-flow) |
| V1.3 | R | * | NONE strategy + abstract reference type + no hint → depends on DeserializationMode | [06-type.md section 6.5.2](06-type.md#652-deserialization-mode-type-resolution-strictness) |
| V1.5 | R | ERROR | CLASS strategy: instanceClassName not found in any registered package | [06-type.md section 6.4.5](06-type.md#645-class-strategy-resolution) |
| V1.6 | R | ERROR | CLASS strategy: ambiguous instanceClassName (multiple packages) without CODEC_ROOT_SCHEMA | [06-type.md section 6.4.5](06-type.md#645-class-strategy-resolution) |

> **V1.1, V1.2, V1.3, V1.5, V1.6** are runtime validation rules that depend on the actual deserialization context. They will be tested in the next evolution phase when codec serialization tests are created.

**V1.3 - DeserializationMode dependency:**
- `DeserializationMode.LENIENT` (default): WARNING, attempt to continue
- `DeserializationMode.STRICT`: ERROR

See [06-type.md section 6.5.2](06-type.md#652-deserialization-mode-type-resolution-strictness) for full documentation.

### Examples

**W: URI strategy (default)**
```json
{ "_type": "http://example.org/test#//Person", "name": "John" }
```

**W: NAME strategy**
```json
{ "_type": "Person", "name": "John" }
```

**W: SCHEMA_AND_TYPE + PLAIN**
```json
{ "_schema": "http://example.org/test", "_type": "Person", "name": "John" }
```

**W: SCHEMA_AND_TYPE + STRUCTURED**
```json
{ "_type": { "schema": "http://example.org/test", "type": "Person" }, "name": "John" }
```

**W: NUMERIC + PLAIN**
```json
{ "_type": "3", "name": "John" }
```

**W: NUMERIC + STRUCTURED**
```json
{ "_type": { "schema": "http://example.org/test", "classifier": 3 }, "name": "John" }
```

---

## 2. typeFormat

**What it does:** Controls **where/how** type information is presented in the JSON structure.

### Valid Values

| Value | Description |
|-------|-------------|
| `PLAIN` (default) | Direct field value(s) at root level |
| `STRUCTURED` | Nested object containing type metadata |

### Serialization (W) Behavior

```
W: Format determines JSON structure
   - PLAIN + any strategy except SCHEMA_AND_TYPE:
     Single field: "_type": "<value>"

   - PLAIN + SCHEMA_AND_TYPE:
     Two separate fields: "_schema": "...", "_type": "..."
     Note: schema key gets underscore prefix if not already prefixed

   - STRUCTURED + any strategy:
     Nested object: "_type": { <inner fields> }
     Inner field uses nameKey (default "type") not typeKey
```

**Code Reference:**
- `TypeSerializationEntry.serializePlain()` (lines 166-183)
- `TypeSerializationEntry.serializeStructured()` (lines 235-276)

### Deserialization (R) Behavior

```
R: Format is auto-detected from JSON structure
   - Value is string → PLAIN format
   - Value is object → STRUCTURED format

   Detection happens in deserialize() based on JsonToken:
   - VALUE_STRING → PLAIN
   - START_OBJECT → STRUCTURED (parseStructuredType)
```

**Code Reference:** `TypeDeserializationEntry.deserializeWithSchemaHint()` (lines 142-198)

### Validation Rules

| Rule | Level | Severity | Description | Spec Reference |
|------|-------|----------|-------------|----------------|
| V2.1 | Config | WARNING | typeNameKey set when format is PLAIN → ignored | [06-type.md section 1.5](06-type.md#15-type-format-plain-vs-structured) |
| V2.2 | Config | WARNING | typeSchemaKey set when format is PLAIN and strategy is not SCHEMA_AND_TYPE → ignored | [06-type.md section 1.5](06-type.md#15-type-format-plain-vs-structured) |

### Examples

**PLAIN + URI:**
```json
{ "_type": "http://example.org/test#//Person" }
```

**STRUCTURED + URI:**
```json
{ "_type": { "type": "http://example.org/test#//Person" } }
```

**STRUCTURED + SCHEMA_AND_TYPE:**
```json
{ "_type": { "schema": "http://example.org/test", "type": "Person" } }
```

---

## 3. typeKey

**What it does:** The outer JSON property key for type information.

### Default: `_type`

### Serialization (W) Behavior

```
W: typeKey is used as the JSON property name
   - PLAIN: gen.writeStringProperty(typeKey, value)
   - STRUCTURED: gen.writeName(typeKey); gen.writeStartObject()...
```

### Deserialization (R) Behavior

```
R: Deserializer recognizes multiple type keys automatically:
   - "_type" (default, recommended)
   - "_class" (alternative)
   - "@type" (JSON-LD compatible)
   - "eClass" (EMF/emfjson-jackson compatible)

   Note: "type" is NOT auto-recognized (too common in data like GeoJSON)
```

**Code Reference:** Spec section 5.1 "Type Key Recognition"

### Validation Rules

*No validation rules* - typeKey is a simple string property.

### Recommendation

Use keys that start with `_` or `@` (like `_type`, `@type`). This helps distinguish metadata from data fields. Without such prefixes, there's risk of collision with ordinary data keys during deserialization - the deserializer might misinterpret a data field as type information.

### Examples

**Custom typeKey "@class":**
```json
{ "@class": "http://example.org/test#//Person" }
```

---

## 4. typeNameKey

**What it does:** Inner type name key in STRUCTURED format.

### Default: `type`

### When Applicable

- **STRUCTURED format only** - ignored for PLAIN
- Used inside the `_type` object as the key for the type value

### Serialization (W) Behavior

```
W: In STRUCTURED format, nameKey is the key for the type value
   - SCHEMA_AND_TYPE: { "schema": "...", "<nameKey>": "Person" }
   - URI/NAME/CLASS: { "<nameKey>": "<value>" }
   - NUMERIC: { "schema": "...", "classifier": N } (nameKey not used)
```

**Code Reference:** `TypeSerializationEntry.serializeStructured()` (lines 254-269)

### Validation Rules

| Rule | Level | Severity | Description |
|------|-------|----------|-------------|
| V4.1 | Config | WARNING | typeNameKey set when typeFormat is PLAIN → ignored |

### Examples

**Custom typeNameKey "@type" in STRUCTURED:**
```json
{ "_type": { "schema": "...", "@type": "Person" } }
```

---

## 5. typeSchemaKey

**What it does:** Key for schema in STRUCTURED format or PLAIN SCHEMA_AND_TYPE.

### Default: `schema`

### When Applicable

- **STRUCTURED format** - as inner key
- **PLAIN + SCHEMA_AND_TYPE** - as separate field (gets `_` prefix if needed)

### Serialization (W) Behavior

```
W: Schema key usage depends on format/strategy:

   PLAIN + SCHEMA_AND_TYPE:
   - Schema key gets underscore prefix if not already prefixed
   - getPlainSchemaKey(): if starts with "_" or "@" → use as-is, else add "_"
   - Example: "schema" → "_schema", "@vocab" → "@vocab"

   STRUCTURED + SCHEMA_AND_TYPE or NUMERIC:
   - Schema key used as-is inside the nested object
```

**Code Reference:** `TypeSerializationEntry.getPlainSchemaKey()` (lines 194-201)

### Validation Rules

| Rule | Level | Severity | Description |
|------|-------|----------|-------------|
| V5.1 | Config | WARNING | typeSchemaKey set when format is PLAIN and strategy is not SCHEMA_AND_TYPE → ignored |

### Examples

**Custom schemaKey "@vocab" in PLAIN SCHEMA_AND_TYPE:**
```json
{ "@vocab": "http://example.org/test", "_type": "Person" }
```

---

## 6. typeScope

**What it does:** Controls WHERE the type strategy applies in the object hierarchy.

### Default: `ALL`

### Valid Values

| Value | Applies To |
|-------|------------|
| `ALL` (default) | Root + all containments + all non-containments |
| `ROOT_ONLY` | Root object only, children use defaults |
| `ROOT_CONTAINMENT` | Root + objects via containment references |
| `ROOT_NON_CONTAINMENT` | Root + objects via non-containment references |

### Availability

- **Runtime only** (🔧) - NO EAnnotation equivalent
- Only configurable via property maps or builder API
- Only valid at GLOBAL level

### Validation Rules

| Rule | Level | Severity | Description |
|------|-------|----------|-------------|
| V6.1 | Config | WARNING | typeScope via EAnnotation → ignored (runtime-only property) |
| V6.2 | Config | WARNING | typeScope at EClass or EReference level → ignored (only valid at Global) |

---

## 7. typeFormatScope

**What it does:** Controls WHERE the type format applies (independent from strategy scope).

### Default: `ALL`

Same values as typeScope. Can be combined with typeScope for fine-grained control.

### Example: Independent Scopes

```java
// Strategy at root only, format for root + containments
.typeStrategy(TypeStrategy.SCHEMA_AND_TYPE)
.typeScope(StrategyScope.ROOT_ONLY)
.typeFormat(SerializationFormat.STRUCTURED)
.typeFormatScope(StrategyScope.ROOT_CONTAINMENT)
```

Result:
| Object Level | Strategy | Format |
|--------------|----------|--------|
| Root | SCHEMA_AND_TYPE | STRUCTURED |
| Containment | URI (fallback) | STRUCTURED |
| Non-containment | URI (fallback) | PLAIN (fallback) |

---

## 8. typeValueReaderName / typeValueWriterName

**What it does:** Custom value reader/writer service names for type serialization.

### Default: `null` (use built-in)

### Availability

- **typeValueReaderName**: G, C levels, R direction
- **typeValueWriterName**: G, C levels, W direction

### Validation Rules

| Rule | Level | Severity | Description |
|------|-------|----------|-------------|
| V8.1 | Config | ERROR | typeValueReaderName on EReference → class-intrinsic |
| V8.2 | Config | ERROR | typeValueWriterName on EReference → class-intrinsic |

---

## 9. Discriminator-Related

These properties configure discriminator mapping (separate layer from TypeStrategy).

### discriminatorPath (typeDiscriminatorPath)

**What it does:** JSON path to discriminator value within the data.

### Effect on Type Serialization

```
W: When discriminatorPath is configured:
   - shouldSerialize() returns false for _type
   - Type info is written at the discriminator path (replaces _type)

R: Deserializer reads discriminatorPath value for type resolution
   - Priority: Type Mapping Registry → Inline Mapping → Type Strategy → Fallback
```

> **Design rationale:** Type Mapping Registry (Jackson's `@JsonTypeInfo` + `@JsonSubTypes` pattern) has highest priority as the primary, flexible mechanism. Inline Mapping is a simpler, more static variant.

**Code Reference:** `TypeSerializationEntry.hasDiscriminatorPath()` (lines 127-131)

### discriminatorValue (typeDiscriminator)

**What it does:** This class's discriminator value for distributed registration.

```
W: When discriminatorValue is configured:
   - Uses discriminatorValue instead of EClass-derived value
   - Enables custom type strings like "person-entity"
```

**Code Reference:** `TypeSerializationEntry.resolveTypeValue()` (lines 337-340)

---

## Invalid Configuration Matrix

| Configuration | Level | Severity | Reason |
|---------------|-------|----------|--------|
| `typeValueReaderName` on EReference | F | ERROR | Value reader is class-intrinsic |
| `typeValueWriterName` on EReference | F | ERROR | Value writer is class-intrinsic |
| `typeScope` via EAnnotation | Any | WARNING | Runtime-only property, ignored |
| `typeFormatScope` via EAnnotation | Any | WARNING | Runtime-only property, ignored |
| Any `type*` key on EAttribute | A | ERROR | Type config not applicable to attributes |
| `typeDiscriminatorPath` on EReference | F | ERROR | Discriminator path is per-class |
| `typeDiscriminator` on EReference | F | ERROR | Discriminator value is per-class |

---

## Smart Compression

Smart compression is a **separate global property** (`codec.smartCompression`, default: `false`) that affects type output. It is documented in [05-global-options.md](05-global-options.md#1-smart-compression).

**Effect on Type Output:**
- When `smartCompression=true`: Root uses full URI, contained objects in same schema use simple names
- When `smartCompression=false` (default): All objects use their configured strategy as-is

> **Note:** Smart compression is NOT part of Type Configuration - it's a global option. This document focuses on Type Configuration properties only.

---

## Test Categories

Tests are separated into two phases:
- **Phase 1 (Current):** Configuration-level tests - no serialization required
- **Phase 2 (Future):** Runtime/behavioral tests - require actual de-/serialization

---

### Phase 1: Configuration Tests (Current Focus)

These tests validate configuration objects without invoking the codec.

#### 1.1 Default Value Tests
- [ ] TypeConfig.defaults() returns spec-defined defaults
- [ ] ConfigProperty enum has correct defaults

#### 1.2 Invalid Configuration Tests
- [ ] typeValueReaderName on EReference → ERROR
- [ ] typeValueWriterName on EReference → ERROR
- [ ] typeScope via EAnnotation → WARNING (ignored)
- [ ] typeFormatScope via EAnnotation → WARNING (ignored)
- [ ] type* on EAttribute → ERROR
- [ ] CLASS strategy with null instanceClassName → ERROR

#### 1.3 Config Warning Tests
- [ ] typeNameKey set when format=PLAIN → WARNING (ignored)
- [ ] typeSchemaKey set when format=PLAIN and strategy≠SCHEMA_AND_TYPE → WARNING (ignored)

---

### Phase 2: Runtime Tests (FUTURE - requires codec)

These tests require actual serialization/deserialization.

#### 2.1 Strategy × Format Matrix Tests (W)
For each strategy (URI, NAME, CLASS, NUMERIC, SCHEMA_AND_TYPE, NONE):
- [ ] PLAIN format output
- [ ] STRUCTURED format output
- [ ] Custom keys

#### 2.2 Deserialization Format Detection (R)
- [ ] PLAIN string → detected correctly
- [ ] STRUCTURED object → detected correctly
- [ ] Auto-detect strategy from content

#### 2.3 Resolution Priority Tests (R)
- [ ] Full URI → resolves directly
- [ ] Simple name + context schema → smart compression
- [ ] Discriminator lookup
- [ ] Strategy-based fallback

#### 2.4 Scope Tests (requires containment hierarchy)
- [ ] ROOT_ONLY → children use default
- [ ] ROOT_CONTAINMENT → containments inherit, non-containments use default
- [ ] Independent typeScope and typeFormatScope

#### 2.5 DeserializationMode Tests (R)
- [ ] Unknown type + LENIENT mode → WARNING, use reference type fallback
- [ ] Unknown type + STRICT mode → ERROR
- [ ] Unknown type + abstract reference + LENIENT → WARNING (cannot instantiate)
- [ ] Unknown type + abstract reference + STRICT → ERROR
- [ ] NAME strategy without CODEC_ROOT_SCHEMA + LENIENT → WARNING
- [ ] NUMERIC strategy without hint + STRICT → ERROR

#### 2.6 Smart Compression Tests (RW)
- [ ] smartCompression=true: root uses full URI, contained same-schema uses simple name
- [ ] smartCompression=false: all objects use configured strategy
- [ ] Mismatch: serialize with true, deserialize with false → WARNING

#### 2.7 Edge Cases
- [ ] Null EPackage handling
- [ ] Empty discriminator value → use EClass-derived
- [ ] Full URI pattern validation (must have scheme://...#//ClassName)

---

[← Code Conventions](20-code-conventions.md) | [Annotation Reference](16-annotation-reference.md)
