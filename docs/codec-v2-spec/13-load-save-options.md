# Load/Save Options

[← Polymorphism](12-polymorphism.md) | [Next: Custom Values →](14-custom-values.md)

---

> **See also:**
> - [Annotation Reference](16-annotation-reference.md) (Load/Save Options) for complete option reference
> - [Configuration Resolution](02-config-resolution.md) for how options fit in the configuration hierarchy

---

This section defines the runtime options passed to `resource.load(options)` and `resource.save(options)`. These options allow per-operation configuration that overrides static model annotations.

## 1. Overview

### 1.1 Configuration Principle

Every configuration that can be set via EAnnotations must also be available as:
1. **EAnnotation** - Declarative, static configuration in the .ecore model
2. **CodecOptionsBuilder** - Programmatic configuration for code-based setup
3. **Load/Save Options** - Map-based properties for Spring/OSGi integration

### 1.2 Option Priority

Load/Save options have the **highest priority** in the configuration hierarchy:

| Priority | Level | Scope |
|----------|-------|-------|
| 1 (highest) | **Load/Save options** | Per-operation |
| 2 | ResourceFactory defaults | Per-factory |
| 3 | Codec module config | Per-codec |
| 4 | Configuration properties | External |
| 5 | EAnnotations | Per-model |
| 6 (lowest) | Built-in defaults | Global |

---

## 2. Root Element Options

### 2.1 Option Keys

| Java Constant | Property Key | Value Type | Direction | Description |
|---------------|--------------|------------|-----------|-------------|
| `CODEC_ROOT_TYPE` | `codec.rootType` | `EClass` or `String` | Load | Type hint for root object |
| `CODEC_ROOT_SCHEMA` | `codec.rootSchema` | `String` (URI) or `EPackage` | Load | Schema context for NAME strategy |
| `CODEC_ROOT_FINGERPRINT` | `codec.rootFingerprint` | `String` (fingerprint) | Load | Optional; selects the package version a String root type resolves against (multi-version) |

### 2.2 EMF Resource Contents

An EMF Resource can contain multiple root-level EObjects in its `contents` list:

```java
Resource resource = ...;
resource.getContents().add(person1);  // First root object
resource.getContents().add(person2);  // Second root object
resource.getContents().add(person3);  // Third root object
```

### 2.3 Serialization Behavior

**Single Root Object** - Serialized as a JSON object:
```json
{
  "_type": "Person",
  "name": "John Doe",
  "age": 30
}
```

**Multiple Root Objects** - Serialized as a JSON array:
```json
[
  { "_type": "Person", "name": "John Doe", "age": 30 },
  { "_type": "Person", "name": "Jane Smith", "age": 25 }
]
```

### 2.4 Deserialization Behavior

The deserializer automatically detects whether the root is an object or array:

| JSON Root Token | Behavior |
|-----------------|----------|
| `{` (START_OBJECT) | Deserialize single root object |
| `[` (START_ARRAY) | Deserialize each array element as a root object |

### 2.5 CODEC_ROOT_TYPE Option

When JSON does not contain type information (no `_type` field), the `CODEC_ROOT_TYPE` option provides a type hint for deserialization.

```java
/**
 * Load option key for root object type hint.
 * Value: EClass or String (type URI / qualified name)
 */
public static final String CODEC_ROOT_TYPE = "CODEC_ROOT_TYPE";
```

**Usage:**
```java
Map<String, Object> options = new HashMap<>();
options.put(CodecResource.CODEC_ROOT_TYPE, personEClass);

resource.load(inputStream, options);
```

**Behavior with Arrays:**

When the root is a JSON array and `CODEC_ROOT_TYPE` is specified:
- The type hint applies to **each element** in the array
- All elements are deserialized using the specified EClass
- Each deserialized EObject is added to `resource.getContents()`

**Example:**

JSON (no type information):
```json
[
  { "name": "John Doe", "age": 30 },
  { "name": "Jane Smith", "age": 25 }
]
```

Java:
```java
options.put(CodecResource.CODEC_ROOT_TYPE, personEClass);
resource.load(inputStream, options);

// Result: resource.getContents() contains 2 Person objects
assertEquals(2, resource.getContents().size());
assertTrue(resource.getContents().get(0).eClass() == personEClass);
```

### 2.6 Type Hint vs Content Type Priority

When both `_type` field and `CODEC_ROOT_TYPE` are present:

| Priority | Source | Behavior |
|----------|--------|----------|
| 1 (highest) | `_type` field in JSON | Used for type resolution |
| 2 | `CODEC_ROOT_TYPE` option | Fallback if no `_type` field |

The `_type` field always takes precedence. This allows:
- Default type via `CODEC_ROOT_TYPE`
- Override per-object via `_type` field

### 2.7 CODEC_ROOT_SCHEMA Option

For SCHEMA_AND_TYPE strategy, provides the schema context:

```java
/**
 * Load option key for schema context.
 * Value: String (EPackage nsURI) or EPackage instance
 */
public static final String CODEC_ROOT_SCHEMA = "CODEC_ROOT_SCHEMA";
```

**Usage:**
```java
Map<String, Object> options = new HashMap<>();
options.put(CodecResource.CODEC_ROOT_SCHEMA, "http://example.org/person/1.0");

resource.load(inputStream, options);
```

The value may also be an **`EPackage` instance** (additive): the codec uses its
`nsURI` as the context schema. Passing the instance is the multi-version-safe form —
it names the exact package version (identity), where a bare nsURI String cannot
disambiguate between versions sharing that nsURI.

### 2.8 CODEC_ROOT_FINGERPRINT Option

`CODEC_ROOT_FINGERPRINT` is an **optional** load option for callers that cannot supply
a concrete `EClass` instance (e.g. Jakarta REST `MessageBodyReader`/`Writer`, where the
root type arrives as a String). It names the **package version** — by its model
fingerprint (`PackageMetadata.getModelFingerprint()`) — against which a String root type
is resolved, so that under same-nsURI multi-version the String resolves to the *correct*
version's `EClass` instance.

```java
/**
 * Optional load option: package model fingerprint selecting the version a
 * String root type / schema resolves against. Value: String (fingerprint).
 */
public static final String CODEC_ROOT_FINGERPRINT = "codec.rootFingerprint";
```

- **Canonical key:** `codec.rootFingerprint`. Both `CodecOptions.CODEC_ROOT_FINGERPRINT`
  and `CodecResource.CODEC_ROOT_FINGERPRINT` carry this **single value**, so either
  constant works and the dotted/literal duality that affects `CODEC_ROOT_TYPE` /
  `CODEC_ROOT_SCHEMA` does **not** apply here (K9).
- **Optional:** in the common single-version case it is pure noise and may be omitted;
  omitting it preserves today's behavior exactly.

**Resolution:**
1. `getPackageMetadataByFingerprint(fp)` → the selected `PackageMetadata` (version).
   **Unknown fingerprint → ERROR** in every mode (§2.9).
2. With a **String** `CODEC_ROOT_TYPE`: the class is resolved **by name within the
   selected package** (`packageMetadata.getEPackage().getEClassifier(name)`), yielding the
   version-correct `EClass` instance → resolution continues exactly as if that instance
   had been passed (Scenario A). This bypasses the global, last-wins type-URI index.
3. With no root type: the selected package's `nsURI` is used as the context schema (as
   `CODEC_ROOT_SCHEMA` would), when no schema is otherwise set.

### 2.9 Root Fingerprint Consistency (explicit-signal rules)

Explicit caller signals are validated where checkable, trusted where not, and never
silently degraded. These rules apply in **both** strictness modes (LENIENT and STRICT) —
strictness governs tolerance toward *data*, not toward the *caller*.

- **Checkable — fingerprint alongside an instance option.** When
  `CODEC_ROOT_FINGERPRINT` is given together with an instance-based root option —
  `CODEC_ROOT_TYPE` as an `EClass`, or `CODEC_ROOT_SCHEMA` as an `EPackage` — the codec
  verifies the option fingerprint against the instance's package fingerprint
  (`getPackageMetadata(ePackage).getModelFingerprint()`). **Mismatch → ERROR** (two
  contradictory explicit statements are a caller bug). Redundant-but-consistent is fine
  (no diagnostic).
- **Non-checkable — String root options.** When the root options are Strings, the
  fingerprint is trusted but must **resolve**: an **unknown option fingerprint → ERROR**,
  never a silent fallback to nsURI resolution.
- **Scope:** these rules govern the *option* fingerprint only. Behavior for a fingerprint
  carried *inside the data stream* is a Phase B concern (not specified here).

---

## 3. Feature Type Hints

| Java Constant | Property Key | Value Type | Direction | Description |
|---------------|--------------|------------|-----------|-------------|
| `CODEC_FEATURE_TYPE_HINTS` | `codec.featureTypeHints` | `Map<EStructuralFeature, EClass>` | Load | EClass hints for specific features |

### 3.1 Problem Statement

Some EMF models have features typed as `EObject` to allow arbitrary content:

```xml
<!-- OpenAPI Example model -->
<eStructuralFeatures xsi:type="ecore:EReference" name="value"
    eType="ecore:EClass http://www.eclipse.org/emf/2002/Ecore#//EObject"
    containment="true"/>
```

During deserialization, when the codec encounters such a feature:
1. The declared type is `EObject` (abstract, cannot be instantiated)
2. The JSON may not contain `_type` information
3. The codec needs a hint to determine the concrete type

### 3.2 CODEC_FEATURE_TYPE_HINTS

Provides EClass type hints for specific features.

```java
/**
 * Load option key for feature-specific type hints.
 * Value: Map<EStructuralFeature, EClass>
 */
public static final String CODEC_FEATURE_TYPE_HINTS = "CODEC_FEATURE_TYPE_HINTS";
```

**Usage:**
```java
Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
typeHints.put(
    OpenAPIPackage.eINSTANCE.getExample_Value(),
    PersonPackage.eINSTANCE.getPerson()
);
typeHints.put(
    OpenAPIPackage.eINSTANCE.getExtension_Value(),
    ConfigPackage.eINSTANCE.getConfiguration()
);

Map<String, Object> options = new HashMap<>();
options.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

resource.load(inputStream, options);
```

### 3.3 Multi-Valued Features

For `EObject[*]` features (upperBound = -1), the type hint applies to **all elements**:

```java
typeHints.put(
    OpenAPIPackage.eINSTANCE.getCallback_Paths(),  // EObject[*]
    PathItemPackage.eINSTANCE.getPathItem()
);
```

If different types are needed in the same array, each element must have a `_type` field (which overrides the hint).

### 3.4 Integration with Discriminator Mapping

The `CODEC_FEATURE_TYPE_HINTS` option integrates with [Discriminator Mapping](08-discriminator-mapping.md) fallback resolution:

```java
// Model defines inline mapping but no fallbackEClass
// At runtime, provide fallback via feature type hint
Map<EStructuralFeature, EClass> hints = Map.of(
    PersonPackage.Literals.PERSON__CONTACTS, ContactPackage.Literals.GENERIC_CONTACT
);
options.put(CODEC_FEATURE_TYPE_HINTS, hints);
```

**Priority:** Model-defined `fallbackEClass` takes precedence over `CODEC_FEATURE_TYPE_HINTS`.

---

## 4. Feature Value Readers/Writers

> **See also:** [Custom Value Readers/Writers](14-custom-values.md) for complete value reader/writer documentation including interface definitions and examples.

### 4.1 Per-Feature Direct Instance Binding

| Java Constant | Property Key | Value Type | Direction | Description |
|---------------|--------------|------------|-----------|-------------|
| `CODEC_FEATURE_VALUE_READER_INSTANCES` | `codec.featureValueReaderInstances` | `Map<EStructuralFeature, CodecValueReader>` | Load | Reader instances per feature |
| `CODEC_FEATURE_VALUE_WRITER_INSTANCES` | `codec.featureValueWriterInstances` | `Map<EStructuralFeature, CodecValueWriter>` | Save | Writer instances per feature |

Directly bind reader/writer instances to specific features. This bypasses the registry and
is **the supported runtime mechanism** for ad-hoc customizations. It works for:

- **EAttributes** — bind an `AttributeValueReader`/`AttributeValueWriter` (or generic `CodecValueReader`/`CodecValueWriter`)
- **EReferences** — bind a `ReferenceValueReader`/`ReferenceValueWriter` (containment and non-containment; for multi-valued references the reader/writer is invoked per element)

An instance binding takes priority over a `valueReaderName`/`valueWriterName` binding from
config or model annotations. If a bound instance's `canHandle(...)` rejects the reference,
a warning is emitted and the codec falls back to the config/annotation reader or default
handling.

**Usage:**
```java
Map<String, Object> options = Map.of(
    "codec.featureValueReaderInstances", Map.of(
        MyPackage.Literals.PERSON__CREATED_AT, new ISODateReader(),          // EAttribute
        MyPackage.Literals.PERSON__ADDRESS, new CompactAddressReader()       // EReference
    ),
    "codec.featureValueWriterInstances", Map.of(
        MyPackage.Literals.PERSON__CREATED_AT, new ISODateWriter()
    )
);

resource.save(outputStream, options);
```

### 4.2 Per-Feature Binding by Name (deprecated)

| Java Constant | Property Key | Value Type | Direction | Description |
|---------------|--------------|------------|-----------|-------------|
| `CODEC_FEATURE_VALUE_READERS` | `codec.featureValueReaders` | `Map<EStructuralFeature, String>` | Load | **Deprecated** — reader names per feature |
| `CODEC_FEATURE_VALUE_WRITERS` | `codec.featureValueWriters` | `Map<EStructuralFeature, String>` | Save | **Deprecated** — writer names per feature |

**Deprecated.** Binding a *registered* reader/writer name to a feature per operation is
already covered by the general configuration resolution (see
[Config Resolution](02-config-resolution.md)): pass a `"ClassName.featureName"` entry with
a `valueReaderName`/`valueWriterName` property in the load/save options. For *unregistered*
readers/writers use instance binding (§4.1).

```java
// Supported replacement: bind a registered reader via config resolution
Map<String, Object> options = Map.of(
    "OpenAPI.security", Map.of("valueReaderName", "securityRequirement")
);
```

Note: these options only ever worked for **EAttributes**. For EReferences they were always
ignored; since their deprecation a binding for a reference produces a warning diagnostic.

### 4.3 Runtime Registration (not supported)

There is deliberately **no** load/save option to register readers/writers into the
`CodecValueRegistry` per operation. Readers/writers resolved by name — whether through
`valueReaderName`/`valueWriterName` model annotations or config properties — must already
be present in the registry when the resource is created (registered at factory/module
construction, via OSGi services, or programmatically on the `CodecValueRegistry`).

In particular the following edge case is **not implemented**: a model annotation
(`valueReaderName`) declaring a reader that the caller supplies only at load time. Callers
in that situation must either register the reader on the registry before creating the
resource, or bind an instance per feature with §4.1.

*History:* the former options `CODEC_VALUE_READERS`/`CODEC_VALUE_WRITERS`
(`codec.valueReaders`/`codec.valueWriters`) specified exactly this, but were never
implemented and have been removed (see issue #45).

---

## 5. Deserialization Mode

| Java Constant | Property Key | Value Type | Direction | Description |
|---------------|--------------|------------|-----------|-------------|
| `CODEC_DESERIALIZATION_MODE` | `codec.deserializationMode` | `DeserializationMode` | Load | Type resolution strictness |
| `CODEC_TYPE_HINT_MODE` | `codec.typeHintMode` | `TypeHintMode` | Load | How type hints interact with JSON content |

### 5.1 CODEC_DESERIALIZATION_MODE

Controls how strict the deserializer is about type matching and unknown fields.

```java
/**
 * Load option key for deserialization strictness.
 * Value: DeserializationMode enum
 */
public static final String CODEC_DESERIALIZATION_MODE = "CODEC_DESERIALIZATION_MODE";
```

**DeserializationMode values:**

| Value | Behavior |
|-------|----------|
| `STRICT` | Fail on unknown fields, require exact type matches |
| `LENIENT` **(default)** | Skip unknown fields, allow compatible type coercion |
| `AUTO_DETECT` | Infer mode from JSON structure (opt-in; groups with `LENIENT` for error strictness) |

**Usage:**
```java
Map<String, Object> options = new HashMap<>();
options.put(CodecResource.CODEC_DESERIALIZATION_MODE, DeserializationMode.LENIENT);

resource.load(inputStream, options);
```

### 5.2 CODEC_TYPE_HINT_MODE

Controls how type hints interact with JSON `_type` fields.

```java
/**
 * Load option key for type hint behavior.
 * Value: TypeHintMode enum
 */
public static final String CODEC_TYPE_HINT_MODE = "CODEC_TYPE_HINT_MODE";
```

**TypeHintMode values:**

| Value | Behavior |
|-------|----------|
| `HINT` **(default)** | Type hint is fallback; JSON `_type` takes precedence |
| `OVERRIDE` | Type hint overrides JSON `_type` (useful for schema migration) |

---

## 6. Resolution Priority

When deserializing a feature, the codec resolves type information in this order:

| Priority | Source | Description |
|----------|--------|-------------|
| 1 (highest) | `_type` field in JSON | Explicit type information in the data |
| 2 | `CODEC_FEATURE_VALUE_READER_INSTANCES` | Load option - ValueReader instance |
| 3 | `CODEC_FEATURE_TYPE_HINTS` | Load option - EClass hint |
| 4 | `valueReaderName` config property | Load/save options (`"ClassName.featureName"`) or EAnnotation |
| 5 | Standard type resolution | Uses declared reference type |

### 6.1 Resolution Flow

```
Deserializing EObject-typed feature
    │
    ├─ JSON contains _type field?
    │       │
    │       └─ YES: Use _type for type resolution
    │
    ├─ CODEC_FEATURE_VALUE_READER_INSTANCES contains entry for this feature?
    │       │
    │       └─ YES: Delegate to the bound ValueReader instance
    │
    ├─ CODEC_FEATURE_TYPE_HINTS contains entry for this feature?
    │       │
    │       └─ YES: Use EClass as concrete type
    │
    ├─ valueReaderName configured (options or EAnnotation)?
    │       │
    │       └─ YES: Delegate to registered reader
    │
    └─ No hint available:
            │
            ├─ Try standard type resolution from JSON
            │
            └─ If fails: Log WARNING, skip feature
```

### 6.2 Why ValueReader has Priority over EClass Hint

When both `CODEC_FEATURE_VALUE_READER_INSTANCES` and `CODEC_FEATURE_TYPE_HINTS` have entries for the same feature, the ValueReader takes priority because:

1. **Full Control**: A ValueReader provides complete control over deserialization
2. **Complex Types**: Some features need custom parsing logic, not just type instantiation
3. **Explicit Intent**: Specifying a reader indicates intent to handle the feature specially

---

## 7. Combining Options

### 7.1 Using Multiple Options Together

```java
Map<String, Object> loadOptions = new HashMap<>();

// Root type hint
loadOptions.put(CodecResource.CODEC_ROOT_TYPE, openApiEClass);

// Feature type hints for simple cases
Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
typeHints.put(
    OpenAPIPackage.eINSTANCE.getExample_Value(),
    PersonPackage.eINSTANCE.getPerson()
);
loadOptions.put(CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints);

// ValueReader instances for complex cases
Map<EStructuralFeature, CodecValueReader> readerInstances = new HashMap<>();
readerInstances.put(
    OpenAPIPackage.eINSTANCE.getComponents_Schemas(),
    new EPackageValueReader()
);
loadOptions.put(CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES, readerInstances);

resource.load(inputStream, loadOptions);
```

### 7.2 CodecOptionsBuilder

```java
Map<String, Object> options = CodecOptionsBuilder.create()
    // Root type hint
    .rootType(PersonPackage.eINSTANCE.getPerson())
    // EClass type hints
    .featureTypeHint(
        OpenAPIPackage.eINSTANCE.getExample_Value(),
        PersonPackage.eINSTANCE.getPerson()
    )
    // ValueReader names
    .featureValueReader(
        OpenAPIPackage.eINSTANCE.getComponents_Schemas(),
        "jsonSchemaToEPackage"
    )
    // ValueWriter names
    .featureValueWriter(
        OpenAPIPackage.eINSTANCE.getComponents_Schemas(),
        "ePackageToJsonSchema"
    )
    .build();
```

---

## 8. Error Handling

### 8.1 Root Element Errors

| Scenario | Severity | Behavior |
|----------|----------|----------|
| No `_type` and no `CODEC_ROOT_TYPE` | ERROR | "Cannot deserialize: no type information found and no CODEC_ROOT_TYPE hint" |
| Invalid `CODEC_ROOT_TYPE` (not a resolvable EClass or String type identifier) | ERROR | Type hint must be an EClass or a resolvable type URI / qualified name |
| Unknown `CODEC_ROOT_FINGERPRINT` (no package for fingerprint) | ERROR | "Unknown root fingerprint: `<fp>`" |
| `CODEC_ROOT_FINGERPRINT` conflicts with an instance root option's package fingerprint | ERROR | "Root fingerprint `<fp>` does not match `<option>` package `<nsURI>` (`<fp2>`)" |
| Abstract EClass as hint | ERROR | Cannot instantiate abstract class |
| JSON property not in EClass | WARNING | Unknown property (skipped) |

### 8.2 Feature Type Hint Errors

| Scenario | Severity | Behavior |
|----------|----------|----------|
| EObject-typed feature without hint and without `_type` | WARNING | Feature skipped, value is `null` |
| Hint EClass is abstract | ERROR | Cannot instantiate abstract class |
| Hint EClass incompatible with reference type | WARNING | Hint ignored, fallback to default resolution |
| `null` value in type hints map | WARNING | Treated as no hint for that feature |

### 8.3 ValueReader/Writer Errors

| Scenario | Severity | Behavior |
|----------|----------|----------|
| Reader name not found in registry | WARNING | Falling back to type hint or skip |
| Writer name not found in registry | WARNING | Falling back to default serialization |

---

## 9. Option Reference Summary

> **See also:** [Custom Value Readers/Writers](14-custom-values.md) for complete value reader/writer documentation.

### Load Options

| Java Constant | Property Key | Value Type | Purpose |
|---------------|--------------|------------|---------|
| `CODEC_ROOT_TYPE` | `codec.rootType` | `EClass` or `String` | Type hint for root object(s) |
| `CODEC_ROOT_SCHEMA` | `codec.rootSchema` | `String` | Schema context for NAME strategy |
| `CODEC_FEATURE_TYPE_HINTS` | `codec.featureTypeHints` | `Map<EStructuralFeature, EClass>` | EClass hints for specific features |
| `CODEC_DESERIALIZATION_MODE` | `codec.deserializationMode` | `DeserializationMode` | Strictness level |
| `CODEC_TYPE_HINT_MODE` | `codec.typeHintMode` | `TypeHintMode` | Hint vs Override behavior |

### Value Reader/Writer Options (Load & Save)

| Java Constant | Property Key | Value Type | Purpose |
|---------------|--------------|------------|---------|
| `CODEC_FEATURE_VALUE_READER_INSTANCES` | `codec.featureValueReaderInstances` | `Map<EStructuralFeature, CodecValueReader>` | Reader instances per feature |
| `CODEC_FEATURE_VALUE_WRITER_INSTANCES` | `codec.featureValueWriterInstances` | `Map<EStructuralFeature, CodecValueWriter>` | Writer instances per feature |
| `CODEC_FEATURE_VALUE_READERS` | `codec.featureValueReaders` | `Map<EStructuralFeature, String>` | **Deprecated** — use config resolution (`valueReaderName`) or instances |
| `CODEC_FEATURE_VALUE_WRITERS` | `codec.featureValueWriters` | `Map<EStructuralFeature, String>` | **Deprecated** — use config resolution (`valueWriterName`) or instances |

---

## 10. Examples

### 10.1 Loading External API Data

Common use case: Loading JSON from an external API that doesn't include EMF type information.

**JSON (sites.json):**
```json
[
  {
    "id": 100046725,
    "name": "Jena-Goldbergrampe",
    "location": { "lat": 50.888, "lon": 11.612 }
  },
  {
    "id": 100046726,
    "name": "Jena-Oberaue",
    "location": { "lat": 50.923, "lon": 11.589 }
  }
]
```

**Java:**
```java
// Load the Ecore model
EPackage bikePackage = loadEcore("bike.ecore");
EClass siteClass = (EClass) bikePackage.getEClassifier("site");

// Configure codec with metadata
MetadataService metadataService = MetadataServiceFactory.create();
metadataService.registerPackage(bikePackage);

// Load JSON with type hint
CodecResource resource = new CodecResource(uri, metadataService, config, null);
Map<String, Object> options = new HashMap<>();
options.put(CodecResource.CODEC_ROOT_TYPE, siteClass);

resource.load(new FileInputStream("sites.json"), options);

// Access loaded sites
for (EObject site : resource.getContents()) {
    System.out.println(site.eGet(siteClass.getEStructuralFeature("name")));
}
```

### 10.2 OpenAPI with Domain Model Example Values

**OpenAPI JSON:**
```json
{
  "openapi": "3.0.3",
  "info": { "title": "My API", "version": "1.0" },
  "components": {
    "examples": {
      "personExample": {
        "summary": "A person example",
        "value": { "name": "John Doe", "age": 30 }
      }
    }
  }
}
```

**Java:**
```java
Map<EStructuralFeature, EClass> typeHints = Map.of(
    OpenAPIPackage.eINSTANCE.getExample_Value(),
    PersonPackage.eINSTANCE.getPerson()
);

Map<String, Object> options = Map.of(
    CodecOptions.CODEC_FEATURE_TYPE_HINTS, typeHints
);

resource.load(inputStream, options);

// Access the example value as Person
OpenAPI api = (OpenAPI) resource.getContents().get(0);
Example example = api.getComponents().getExamples().get("personExample");
Person person = (Person) example.getValue();
assertEquals("John Doe", person.getName());
```

---

[Next: Custom Values →](14-custom-values.md)
