# JSON Key Configuration

[← Configuration Resolution](02-config-resolution.md)

---

## 1. Overview

The codec uses special JSON keys to represent metadata (type, schema, supertype, id, references). This chapter describes the **JSON output keys** and their configuration.

> **Note:** For configuration key naming conventions (EAnnotations, property maps, Builder API), see [Annotation Reference](16-annotation-reference.md) (Naming Convention).

**Key Principle:**
- **PLAIN format**: Keys use `_` prefix and appear at root level
- **STRUCTURED format**: Keys without prefix appear inside nested container objects

---

## 2. Default Key Names

### 2.1 PLAIN Format Keys

In PLAIN format, metadata appears as direct key-value pairs at root level:

| Feature | Default Key | Config Key | Example |
|---------|-------------|------------|---------|
| Type | `_type` | `typeKey` | `"_type": "http://example.org#//Person"` |
| Schema | `_schema` | `typeSchemaKey` | `"_schema": "http://example.org"` |
| SuperType | `_supertype` | `superTypeKey` | `"_supertype": "Entity"` |
| ID | `_id` | `idKey` | `"_id": "maho"` |
| Reference | `_ref` | `refKey` | `"_ref": "datainmotion"` |
| Fingerprint | `_fingerprint` | `fingerprintKey` | `"_fingerprint": "fp1:9f2c4e…"` |

### 2.2 STRUCTURED Format Keys

In STRUCTURED format, metadata is grouped into container objects with inner keys:

| Feature | Outer Key (Config) | Inner Key (Config) | Example |
|---------|-------------------|-------------------|---------|
| Type | `_type` (`typeKey`) | `type` (`typeNameKey`) | `"_type": { "type": "Person" }` |
| Schema | — | `schema` (`typeSchemaKey`) | `"_type": { "schema": "http://..." }` |
| SuperType | — | `supertype` (`superTypeKey`) | `"_type": { "supertype": "Entity" }` |
| ID | `_id` (`idKey`) | `id` (`idValueKey`) | `"_id": { "id": "maho" }` |
| Separator | — | `separator` (`idSeparatorKey`) | `"_id": { "separator": "-" }` |
| Reference | (inline) | `ref` (`refKey`) | `{ "ref": "datainmotion" }` |
| Ref Type | — | `type` (`refTypeKey`) | `{ "type": "Company", "ref": "..." }` |
| Fingerprint | — | `fingerprint` (`fingerprintKey`) | `"_type": { "type": "Person", "fingerprint": "fp1:9f2c4e…" }` |

**Note:** Inner keys (`typeNameKey`, `idValueKey`, `idSeparatorKey`) only apply to STRUCTURED format.

**One config key, two placements.** `typeSchemaKey` and `fingerprintKey` are each a
*single* configuration value that appears differently depending on format: the
configured value is the **inner** (unprefixed) key, and the PLAIN sibling is derived
from it by prefixing `_`. A configured value that already starts with `_` or `@` is
taken as-is, so `@vocab` stays `@vocab` in both formats. Configuring the key does not
require knowing which format will be used.

---

## 3. PLAIN Format Examples

All metadata keys appear at root level with `_` prefix:

### 3.1 Minimal (URI Strategy)
```json
{
  "_type": "http://example.org/1.0#//Person",
  "_id": "maho",
  "firstName": "Mark",
  "lastName": "Hoffmann"
}
```

### 3.2 With Schema (SCHEMA_AND_TYPE Strategy)
```json
{
  "_schema": "http://example.org/1.0",
  "_type": "Person",
  "_id": "maho",
  "firstName": "Mark"
}
```

### 3.3 With SuperType
```json
{
  "_schema": "http://example.org/1.0",
  "_type": "BusinessPerson",
  "_supertype": "Person",
  "_id": "maho",
  "firstName": "Mark"
}
```

### 3.4 Multiple SuperTypes
```json
{
  "_schema": "http://example.org/1.0",
  "_type": "BusinessPerson",
  "_supertype": ["Person", "Auditable"],
  "_id": "maho",
  "firstName": "Mark"
}
```

### 3.5 With Non-Containment Reference
```json
{
  "_type": "http://example.org/1.0#//Person",
  "_id": "maho",
  "firstName": "Mark",
  "employer": { "_ref": "datainmotion" }
}
```

---

## 4. STRUCTURED Format Examples

Metadata is grouped into container objects, inner keys have no prefix:

### 4.1 Type Container
```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "Person"
  },
  "_id": "maho",
  "firstName": "Mark"
}
```

### 4.2 With SuperType
```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "BusinessPerson",
    "supertype": "Person"
  },
  "_id": "maho",
  "firstName": "Mark"
}
```

### 4.3 Multiple SuperTypes
```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "BusinessPerson",
    "supertype": ["Person", "Auditable"]
  },
  "_id": "maho",
  "firstName": "Mark"
}
```

### 4.4 Structured ID (Multiple Features)
```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "Person"
  },
  "_id": {
    "separator": "-",
    "firstName": "Mark",
    "lastName": "Hoffmann"
  }
}
```

### 4.5 With Non-Containment Reference
```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "Person"
  },
  "_id": "maho",
  "firstName": "Mark",
  "employer": {
    "type": "http://example.org/1.0#//Company",
    "ref": "datainmotion"
  }
}
```

---

## 5. Key Configuration Options

All keys are configurable via EAnnotations, property maps, or CodecConfiguration Builder.

> **See also:** [Annotation Reference](16-annotation-reference.md) for complete configuration key reference.

### 5.1 Configuration Table

| Config Key | PLAIN Default | STRUCTURED Default | Description |
|------------|---------------|-------------------|-------------|
| `typeKey` | `_type` | `_type` | Outer type container/value key |
| `typeNameKey` | — | `type` | Inner type name key |
| `typeSchemaKey` | `_schema` | `schema` | Schema/Namespace key |
| `superTypeKey` | `_supertype` | `supertype` | SuperType key |
| `idKey` | `_id` | `_id` | ID container/value key |
| `idValueKey` | — | `id` | Inner ID value key |
| `idSeparatorKey` | — | `separator` | Separator key in STRUCTURED ID |
| `refKey` | `_ref` | `ref` | Reference value key |
| `refTypeKey` | `_type` | `type` | Reference type key |
| `fingerprintKey` | `_fingerprint` | `fingerprint` | EPackage fingerprint key (see [Type Serialization](06-type.md#8-in-band-epackage-fingerprint)) |

### 5.2 CodecConfiguration Builder

```java
// Custom keys for JSON-LD style output
CodecConfiguration.builder()
    .typeKey("@context")          // Outer type key
    .typeNameKey("@type")         // Inner type name key
    .typeSchemaKey("@vocab")      // Inner schema key
    .superTypeKey("@extends")     // SuperType key
    .idKey("@id")                 // ID key
    .refKey("@id")                // Reference key
    .build();
```

### 5.3 EAnnotation Configuration

EAnnotation keys use camelCase without `codec.` prefix:

```xml
<eAnnotations source="http://eclipse.org/fennec/codec">
  <details key="typeKey" value="@context"/>
  <details key="typeNameKey" value="@type"/>
  <details key="typeSchemaKey" value="@vocab"/>
</eAnnotations>
```

Property maps use `codec.` prefix:

```java
options.put("codec.typeKey", "@context");
options.put("codec.typeNameKey", "@type");
options.put("codec.typeSchemaKey", "@vocab");
```

---

## 6. Custom Key Examples

### 6.1 JSON-LD Style

Configuration:
```java
CodecConfiguration.builder()
    .typeFormat(SerializationFormat.STRUCTURED)
    .typeKey("@context")
    .typeNameKey("@type")
    .typeSchemaKey("@vocab")
    .superTypeKey("@extends")
    .idKey("@id")
    .refKey("@id")
    .build();
```

Output:
```json
{
  "@context": {
    "@vocab": "http://example.org/1.0",
    "@type": "Person",
    "@extends": "Entity"
  },
  "@id": "maho",
  "firstName": "Mark",
  "employer": { "@id": "datainmotion" }
}
```

### 6.2 MongoDB Style

Configuration:
```java
CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.NAME)
    .typeKey("_t")
    .idKey("_id")
    .refKey("$id")
    .build();
```

Output:
```json
{
  "_t": "Person",
  "_id": "maho",
  "firstName": "Mark",
  "employer": { "$id": "datainmotion" }
}
```

---

## 7. Format Detection (Deserialization)

The deserializer auto-detects the format used:

| Detection | Format |
|-----------|--------|
| `"_type": "string"` | PLAIN |
| `"_type": { ... }` | STRUCTURED |
| `"_schema": ..., "_type": ...` (both present) | PLAIN (SCHEMA_AND_TYPE) |

The deserializer uses the configured keys for detection, so custom keys work transparently.

---

## 8. Consistency Rules

1. **Prefix Convention**: Root-level metadata keys use `_` prefix by default, inner keys don't
2. **Container Grouping**: In STRUCTURED format, related metadata (schema, type, supertype) shares one container
3. **Reference Format**: References always use an object wrapper (`{ "_ref": "..." }` or `{ "ref": "..." }`)
4. **Configurability**: Every JSON key is configurable via EAnnotation, property map, or Builder
5. **Naming Parity**: Configuration keys are consistent across all methods:
   - EAnnotation: `typeKey` (camelCase, no prefix)
   - Property map: `codec.typeKey` (add `codec.` prefix)
   - Builder: `.typeKey(...)` (method name matches)

---

[Next: Common Types →](04-common-types.md)
