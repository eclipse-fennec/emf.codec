# Configuration Scenarios

[← Format Abstraction](17-format-abstraction.md) | [Next: Test Coverage →](19-test-coverage.md)

Each scenario shows the save options (`resource.save(out, options)`) and the resulting output.

---

## 1. Minimal Configuration (Defaults)

Using all built-in defaults - no options:

```java
resource.save(out, Map.of());
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
Map<String, Object> options = Map.of(
    CodecOptions.CODEC_TYPE_FORMAT, "STRUCTURED",
    CodecOptions.CODEC_ID_FORMAT, "STRUCTURED");
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
Map<String, Object> options = Map.of(
    CodecOptions.CODEC_SUPERTYPE_SERIALIZE, true);
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
Map<String, Object> options = Map.of(
    CodecOptions.CODEC_SMART_COMPRESSION, true);
```

**Output (types of the root's schema as simple names, see [05 §1](05-global-options.md)):**
```json
{
  "_type": "http://example.org/person/1.0#//Person",
  "_id": "john-doe",
  "firstName": "John",
  "lastName": "Doe",
  "addresses": [
    { "_type": "Address", "street": "123 Main St" },
    { "_type": "Address", "street": "456 Oak Ave" }
  ]
}
```

---

## 5. Custom Keys

```java
Map<String, Object> options = Map.of(
    CodecOptions.CODEC_TYPE_KEY, "@type",
    CodecOptions.CODEC_ID_KEY, "@id");
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
Map<String, Object> options = Map.of(
    CodecOptions.CODEC_EXPAND, true,
    CodecOptions.CODEC_EXPAND_DEPTH, 2);
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
Map<String, Object> options = Map.of(
    CodecOptions.CODEC_TYPE_STRATEGY, "NUMERIC");
```

**Output:**
```json
{
  "_type": { "schema": "http://example.org/person/1.0", "classifier": 3 },
  "firstName": "John",
  "lastName": "Doe"
}
```

> NUMERIC applies to the type only. Feature keys stay feature names; an earlier revision
> showed numeric feature keys (`"5"`) behind a `useNumericIds` switch, neither of which exists.

> The inner keys of the NUMERIC strategy are `schema` (configurable via `typeSchemaKey`) and
> the fixed `classifier`. Earlier revisions of this chapter showed abbreviated `s`/`c` keys,
> which were never implemented — corrected here against the code
> (`CodecEObjectDeserializer`, `TypeDeserializationEntry`).

---

[Next: Test Coverage →](19-test-coverage.md)
