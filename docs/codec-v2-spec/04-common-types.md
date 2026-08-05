# Common Types and Enumerations

[← JSON Key Configuration](03-naming-conventions.md) | [Next: Global Options →](05-global-options.md)

---

This chapter defines the enumerations and common types used throughout the codec configuration. These types control **what**, **how**, and **where** information is serialized.

| Concern | Types |
|---------|-------|
| **What** to serialize | TypeStrategy, IdStrategy, IdKeyMode, SuperTypeSelection |
| **How** to format output | SerializationFormat, SuperTypePresentation |
| **Where** strategies apply | StrategyScope |
| **Deserialization behavior** | DeserializationMode, TypeHintMode, FallbackStrategy |

## 1. SerializationFormat

Controls **how** data is presented (the structure of the output).

```java
public enum SerializationFormat {
    /**
     * Simple value output - string, number, or array of simple values.
     * The value itself is directly usable without parsing.
     */
    PLAIN,

    /**
     * Nested object output with configurable keys.
     * The value is a map/object containing structured information.
     */
    STRUCTURED
}
```

### 1.1 Format Comparison

| Format | Single Value | Array Value |
|--------|--------------|-------------|
| **PLAIN** | `"_type": "Person"` | `"_supertype": ["Entity", "Auditable"]` |
| **STRUCTURED** | `"_type": { "schema": "...", "type": "..." }` | `"_supertype": [{"schema": "...", "type": "Entity"}, ...]` |

### 1.2 When to Use Each Format

| Use Case | Recommended Format | Reason |
|----------|-------------------|--------|
| Compact output, simple cases | PLAIN | Minimal JSON overhead |
| Multi-part information | STRUCTURED | Schema + type together |
| Multiple ID features | STRUCTURED | Show individual feature values |
| Cross-system interop | STRUCTURED | Self-documenting output |

### 1.3 Applicability

SerializationFormat applies to these serialization targets:

| Target | PLAIN Example | STRUCTURED Example |
|--------|---------------|-------------------|
| Type | `"_type": "Person"` | `"_type": {"schema": "...", "type": "..."}` |
| ID | `"_id": "John-Doe"` | `"_id": {"firstName": "John", ...}` |
| Reference | `"_ref": "acme-corp"` | `"_ref": {"_type": "...", "uri": "..."}` |
| SuperType | `["Entity", "Auditable"]` | `[{"schema": "...", "type": "Entity"}, ...]` |

**Default:** `PLAIN`

> **See also:** [Naming Conventions](03-naming-conventions.md) (section 2) for key naming differences between formats.

---

## 2. StrategyScope

Controls **where** a strategy/format applies in the object hierarchy.

```java
public enum StrategyScope {
    /**
     * Apply to all objects in the serialization tree.
     */
    ALL,

    /**
     * Apply only to root-level objects (resource.getContents()).
     */
    ROOT_ONLY,

    /**
     * Apply to root objects and objects reached via containment references.
     */
    ROOT_CONTAINMENT,

    /**
     * Apply to root objects and objects reached via non-containment references.
     */
    ROOT_NON_CONTAINMENT
}
```

### 2.1 Scope Visualization

```
resource.getContents()
├── Person (ROOT)                    ← ROOT_ONLY, ROOT_CONTAINMENT, ROOT_NON_CONTAINMENT, ALL
│   ├── Address (containment)        ← ROOT_CONTAINMENT, ALL
│   │   └── Country (containment)    ← ROOT_CONTAINMENT, ALL
│   └── Company (non-containment)    ← ROOT_NON_CONTAINMENT, ALL
│       └── CEO (containment)        ← ALL only (not root, containment of non-root)
```

### 2.2 When to Use Each Scope

| Scope | Use Case |
|-------|----------|
| `ALL` | Consistent type info throughout **(default)** |
| `ROOT_ONLY` | Smart compression - type info only at root, rely on schema for nested |
| `ROOT_CONTAINMENT` | Type info for contained objects, not referenced ones |
| `ROOT_NON_CONTAINMENT` | Type info for references (useful when references are polymorphic) |

### 2.3 Applicability

StrategyScope is used for:
- `codec.typeScope` - Where to apply TypeStrategy
- `codec.typeFormatScope` - Where to apply type SerializationFormat
- `codec.idScope` - Where to apply IdStrategy
- `codec.idFormatScope` - Where to apply ID SerializationFormat

**Default:** `ALL`

> **See also:** [Configuration Resolution](02-config-resolution.md) (section 5) for how StrategyScope interacts with configuration hierarchy.

---

## 3. TypeStrategy

Controls **what** type information is serialized.

```java
public enum TypeStrategy {
    /**
     * Full EMF EClass URI.
     * Example: "http://example.org/person/1.0#//Person"
     */
    URI,

    /**
     * Simple EClass name without package.
     * Example: "Person"
     */
    NAME,

    /**
     * Schema URI and type name as separate pieces.
     * Requires STRUCTURED format for single field, or uses two fields in PLAIN.
     */
    SCHEMA_AND_TYPE,

    /**
     * EMF classifier ID (numeric).
     * Example: "3"
     * WARNING: IDs are positional and may change with model evolution.
     */
    NUMERIC,

    /**
     * No type information serialized.
     * Requires CODEC_ROOT_TYPE hint for deserialization.
     */
    NONE
}
```

### 3.1 Strategy Comparison

| Strategy | PLAIN Output | STRUCTURED Output |
|----------|--------------|-------------------|
| `URI` | `"_type": "http://example.org/1.0#//Person"` | `"_type": { "uri": "http://..." }` |
| `NAME` | `"_type": "Person"` | `"_type": { "type": "Person" }` |
| `SCHEMA_AND_TYPE` | `"_schema": "http://...", "_type": "Person"` | `"_type": { "schema": "http://...", "type": "Person" }` |
| `NUMERIC` | `"_type": "3"` | `"_type": { "schema": "http://...", "classifier": 3 }` |
| `NONE` | (no type field) | (no type field) |

### 3.2 When to Use Each Strategy

| Strategy | Use Case | Advantages | Disadvantages |
|----------|----------|------------|---------------|
| `URI` | Cross-system, long-term storage | Self-contained, unambiguous | Verbose |
| `NAME` | Single-package models, APIs | Readable, compact | Ambiguous if multiple packages have same class name |
| `SCHEMA_AND_TYPE` | Multi-package with readable types | Both schema context and readable name | Two values needed |
| `NUMERIC` | Transient data, compact output | Very compact | Fragile - IDs change with model evolution |
| `NONE` | Homogeneous collections, compact APIs | Most compact | Requires type hint for deserialization |

**Default:** `URI`

> **See also:** [Type Serialization](06-type.md) for complete type configuration, [Discriminator Mapping](08-discriminator-mapping.md) for custom type resolution.

---

## 4. IdStrategy

Controls how object identity is serialized.

```java
public enum IdStrategy {
    /**
     * Use the attribute marked with eID="true" in the model.
     * Ecore allows at most one eID attribute per EClass.
     */
    ID_FIELD,

    /**
     * Combine multiple features into a single ID value.
     * Features and separator are configurable.
     */
    COMBINED
}
```

### 4.1 Strategy Comparison

| Strategy | Configuration | Example Output (PLAIN) |
|----------|---------------|----------------------|
| `ID_FIELD` | Use model's eID attribute | `"_id": "john-doe"` |
| `COMBINED` | `features=firstName,lastName`, `separator=-` | `"_id": "John-Doe"` |

### 4.2 When to Use Each Strategy

| Strategy | Use Case |
|----------|----------|
| `ID_FIELD` | Model already has eID attribute, simple ID scheme |
| `COMBINED` | Need composite key from multiple features |

**Default:** `ID_FIELD`

> **See also:** [ID Serialization](09-id.md) for complete ID configuration including STRUCTURED format examples.

---

## 5. IdKeyMode

Controls what is written to JSON for ID values.

```java
public enum IdKeyMode {
    /**
     * Only write the _id field with the computed ID value.
     * Individual ID features are NOT written separately.
     */
    ID_ONLY,

    /**
     * Write both the _id field AND the individual features.
     * Useful when features should be queryable independently.
     */
    BOTH,

    /**
     * Write individual features but NOT the _id field.
     * ID is implicit from the feature values.
     */
    FEATURE_ONLY,

    /**
     * Do not write any ID information.
     */
    NONE
}
```

### 5.1 Mode Comparison

| Mode | Output for `Person{firstName="John", lastName="Doe"}` |
|------|-------------------------------------------------------|
| `ID_ONLY` | `{"_id": "John-Doe"}` |
| `BOTH` | `{"_id": "John-Doe", "firstName": "John", "lastName": "Doe"}` |
| `FEATURE_ONLY` | `{"firstName": "John", "lastName": "Doe"}` |
| `NONE` | `{}` (no ID fields) |

### 5.2 When to Use Each Mode

| Mode | Use Case |
|------|----------|
| `ID_ONLY` | Compact output, ID is only needed for references |
| `BOTH` | Features should be queryable, but also need stable ID |
| `FEATURE_ONLY` | ID features are the primary data, no separate ID field needed |
| `NONE` | No ID serialization required |

**Default:** `ID_ONLY`

> **See also:** [ID Serialization](09-id.md) (section 3) for IdKeyMode examples with COMBINED strategy.

---

## 6. SuperTypeSelection

Controls which supertypes are included in supertype serialization.

```java
public enum SuperTypeSelection {
    /**
     * Include only the immediate supertype.
     */
    SINGLE,

    /**
     * Include all supertypes in the hierarchy.
     */
    ALL,

    /**
     * Include all EMF supertypes (excluding EObject).
     */
    ALL_EMF,

    /**
     * Do not include any supertype information.
     */
    NONE
}
```

### 6.1 Selection Comparison

For `Customer extends Person extends Entity`:

| Selection | Output |
|-----------|--------|
| `SINGLE` | `["Person"]` |
| `ALL` | `["Person", "Entity"]` |
| `ALL_EMF` | `["Person", "Entity"]` (same as ALL for this example) |
| `NONE` | (no supertype field) |

**Default:** `ALL`

> **See also:** [SuperType Serialization](07-supertype.md) for complete supertype configuration.

---

## 7. SuperTypePresentation

Controls how multiple supertypes are presented.

```java
public enum SuperTypePresentation {
    /**
     * Supertypes as JSON array.
     */
    ARRAY,

    /**
     * Supertypes as comma-separated string.
     */
    STRING
}
```

### 7.1 Presentation Comparison

| Presentation | Output |
|--------------|--------|
| `ARRAY` | `"_supertype": ["Person", "Entity"]` |
| `STRING` | `"_supertype": "Person,Entity"` |

**Default:** `ARRAY`

> **See also:** [SuperType Serialization](07-supertype.md) (section 2) for presentation format configuration.

---

## 8. FallbackStrategy

Controls behavior when discriminator mapping cannot resolve a type.

```java
public enum FallbackStrategy {
    /**
     * Log WARNING, continue to next resolution step (Type Strategy).
     * This is the default.
     */
    SKIP,

    /**
     * Fail immediately with an error.
     */
    ERROR,

    /**
     * Use fallbackEClass (MUST be set, else ERROR).
     * Does NOT continue to Type Strategy.
     */
    FALLBACK
}
```

See [Discriminator Mapping](08-discriminator-mapping.md) for detailed fallback behavior.

**Default:** `SKIP`

---

## 9. DeserializationMode

Controls strictness of deserialization.

```java
public enum DeserializationMode {
    /**
     * Fail on unknown fields, require exact type matches.
     */
    STRICT,

    /**
     * Skip unknown fields, allow compatible type coercion.
     */
    LENIENT,

    /**
     * Infer mode from JSON structure.
     */
    AUTO_DETECT
}
```

**Default:** `AUTO_DETECT`

> **See also:** [Error Handling](15-error-handling.md) for how DeserializationMode affects error reporting.

---

## 10. TypeHintMode

Controls how type hints interact with JSON `_type` fields.

```java
public enum TypeHintMode {
    /**
     * Type hint is fallback; JSON _type takes precedence.
     */
    HINT,

    /**
     * Type hint overrides JSON _type (useful for schema migration).
     */
    OVERRIDE
}
```

**Default:** `HINT`

> **See also:** [Load/Save Options](13-load-save-options.md) (section 2) for using type hints during deserialization.

---

## 11. Summary Table

| Enum | Values | Default | Used In |
|------|--------|---------|---------|
| **SerializationFormat** | PLAIN, STRUCTURED | PLAIN | [Type](06-type.md), [ID](09-id.md), [Reference](10-reference.md), [SuperType](07-supertype.md) |
| **StrategyScope** | ALL, ROOT_ONLY, ROOT_CONTAINMENT, ROOT_NON_CONTAINMENT | ALL | [Config Resolution](02-config-resolution.md#5-strategyscope-global-level-modifier) |
| **TypeStrategy** | URI, NAME, SCHEMA_AND_TYPE, NUMERIC, NONE | URI | [Type](06-type.md) |
| **IdStrategy** | ID_FIELD, COMBINED | ID_FIELD | [ID](09-id.md) |
| **IdKeyMode** | ID_ONLY, BOTH, FEATURE_ONLY, NONE | ID_ONLY | [ID](09-id.md) |
| **SuperTypeSelection** | ALL, ALL_EMF, SINGLE, NONE | ALL | [SuperType](07-supertype.md) |
| **SuperTypePresentation** | ARRAY, STRING | ARRAY | [SuperType](07-supertype.md) |
| **FallbackStrategy** | SKIP, ERROR, FALLBACK | SKIP | [Discriminator Mapping](08-discriminator-mapping.md) |
| **DeserializationMode** | STRICT, LENIENT, AUTO_DETECT | AUTO_DETECT | [Load/Save Options](13-load-save-options.md) |
| **TypeHintMode** | HINT, OVERRIDE | HINT | [Load/Save Options](13-load-save-options.md) |

---

[Next: Global Configuration Options →](05-global-options.md)
