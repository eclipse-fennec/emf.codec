# Configuration Scenarios

[← Format Abstraction](17-format-abstraction.md) | [Next: Test Coverage →](19-test-coverage.md)

---

## 1. Minimal Configuration (Defaults)

Using all built-in defaults:

```java
CodecConfig config = CodecConfig.builder().build();
```

**Output:**
```json
{
  "_type": "http://example.org/person/1.0#//Person",
  "_id": "john-doe",
  "firstName": "John",
  "lastName": "Doe"
}
```

---

## 2. STRUCTURED Format

All metadata as nested objects:

```java
CodecConfig config = CodecConfig.builder()
    .format(SerializationFormat.STRUCTURED)
    .build();
```

**Output:**
```json
{
  "_type": {
    "schema": "http://example.org/person/1.0",
    "name": "Person"
  },
  "_id": {
    "firstName": "John",
    "lastName": "Doe"
  },
  "firstName": "John",
  "lastName": "Doe"
}
```

---

## 3. With SuperTypes Enabled

```java
CodecConfig config = CodecConfig.builder()
    .supertype(SuperTypeSerializationConfig.builder()
        .enabled(true)
        .build())
    .build();
```

**Output:**
```json
{
  "_type": "http://example.org/person/1.0#//Person",
  "_supertype": ["Entity", "Auditable"],
  "_id": "john-doe",
  "firstName": "John",
  "lastName": "Doe"
}
```

---

## 4. Smart Compression

```java
CodecConfig config = CodecConfig.builder()
    .smartCompression(true)
    .build();
```

**Output (type omitted when inferable):**
```json
{
  "_id": "john-doe",
  "firstName": "John",
  "lastName": "Doe",
  "addresses": [
    { "street": "123 Main St" },
    { "street": "456 Oak Ave" }
  ]
}
```

---

## 5. Custom Keys

```java
CodecConfig config = CodecConfig.builder()
    .type(TypeSerializationConfig.builder()
        .typeKey("@type")
        .build())
    .id(IdSerializationConfig.builder()
        .idKey("@id")
        .build())
    .build();
```

**Output:**
```json
{
  "@type": "http://example.org/person/1.0#//Person",
  "@id": "john-doe",
  "firstName": "John",
  "lastName": "Doe"
}
```

---

## 6. Reference Expansion

```java
CodecConfig config = CodecConfig.builder()
    .expand(true)
    .expandDepth(2)
    .build();
```

**Output:**
```json
{
  "_type": "http://example.org/person/1.0#//Person",
  "_id": "john-doe",
  "firstName": "John",
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_id": "acme-corp",
    "name": "Acme Corporation"
  }
}
```

---

## 7. NUMERIC Strategy (Compact)

```java
CodecConfig config = CodecConfig.builder()
    .useNumericIds(true)
    .build();
```

**Output:**
```json
{
  "_type": { "schema": "http://example.org/person/1.0", "classifier": 3 },
  "5": "John",
  "6": "Doe"
}
```

> The inner keys of the NUMERIC strategy are `schema` (configurable via `typeSchemaKey`) and
> the fixed `classifier`. Earlier revisions of this chapter showed abbreviated `s`/`c` keys,
> which were never implemented — corrected here against the code
> (`CodecEObjectDeserializer`, `TypeDeserializationEntry`).

---

[Next: Test Coverage →](19-test-coverage.md)
