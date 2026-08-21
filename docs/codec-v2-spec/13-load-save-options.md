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
- **Scope:** these rules govern the *option* fingerprint only. A fingerprint carried
  *inside the data stream* is the in-band carrier of
  [06-type.md §8](06-type.md#8-in-band-epackage-fingerprint); its precedence against this
  option follows the signal contract in §2.10.

### 2.10 In-Band Fingerprint Options

The in-band carrier is configured by two options. Both are **write-side** switches, with the
read-side restriction on `codec.fingerprintKey` described below.

| Java Constant | Property Key | Value Type | Direction | Description |
|---------------|--------------|------------|-----------|-------------|
| `CODEC_FINGERPRINT_MODE` | `codec.fingerprintMode` | `String` (`NONE` \| `FIRST_TOUCH`) | Save | Opt-in for writing the in-band EPackage fingerprint. Default `NONE` |
| `CODEC_FINGERPRINT_KEY` | `codec.fingerprintKey` | `String` | Save + Load | Key carrying the fingerprint. Default `fingerprint` (PLAIN sibling `_fingerprint`) |

- `codec.fingerprintMode` governs **writing only**. Reading is always liberal: a reader
  accepts a fingerprint it finds regardless of this option
  ([06-type.md §8.4](06-type.md#84-read-liberal)).
- `codec.fingerprintKey` is the **only** way to tell a *reader* about a non-default key.
  Model annotations configure the key for writing exclusively — see the chicken-and-egg
  break in [06-type.md §8.5](06-type.md#85--the-fingerprintkey-chicken-and-egg-problem).
  The default key is always accepted in addition to a configured one.

### 2.11 Signal Contract Classes: Hint vs Directive

`CODEC_ROOT_TYPE` and `codec.rootFingerprint` resolve collisions with the data stream in
**opposite** directions. That asymmetry is deliberate and follows from what each signal
*means*, so it is stated here rather than left to be discovered:

| | `CODEC_ROOT_TYPE` | `codec.rootFingerprint` |
|---|---|---|
| **Class** | **Hint** — weak signal | **Directive** — strong signal |
| **Question it answers** | "What is this object?" | "In which model world is all of this interpreted?" |
| **On collision with the stream** | **Stream wins** (default `TypeHintMode.HINT`), WARNING | **Option wins**, WARNING in LENIENT / ERROR in STRICT |
| **Why** | Polymorphism *requires* it: a subtype in the data must beat a root hint, or subclasses could never deserialize under one | The version is a property of the *load*, chosen by whoever orchestrates it — a migration reader deliberately re-reading old data against a chosen version must be able to overrule the document |

A type hint only helps when the data is silent. A version directive is chosen by the caller
who knows the context the data is being read in, and the data may not overrule that choice.

**No mode switch is added for the fingerprint.** Callers who want option-wins for the *type*
already have `TypeHintMode.OVERRIDE`; the reverse for the fingerprint — "let the stream win"
— is expressed by simply **omitting the option**. Two look-alike mode switches with opposite
defaults would confuse more than they order.

Self-contradiction is a different matter from contradicting the data: two *caller* options
that disagree are a bug and an ERROR in every mode (§2.9), while the caller overruling the
*document* is legitimate and merely loud.

### 2.12 Stream Fingerprint Case Matrix

The complete set of outcomes when a document carries a fingerprint. Strictness governs
tolerance towards **data**, so the same situation is a warning in LENIENT and an error in
STRICT — but it is never *nothing*: an explicit signal is not silently degraded.

| Situation | LENIENT | STRICT |
|---|---|---|
| Resolves to a version | Used and pinned, no diagnostic | Same |
| Equals the caller's `codec.rootFingerprint` | No-op, no diagnostic | Same |
| Differs from the caller's fingerprint | Caller wins + WARNING naming both | ERROR |
| Unknown, and the caller supplied a fingerprint | Caller wins + WARNING | ERROR |
| Unknown, no caller fingerprint | WARNING + fall back to `nsURI` resolution | ERROR |
| Unknown at a later site after a version was pinned | WARNING + the pin stands | ERROR |

**Why the LENIENT fallback matters.** An unknown fingerprint is not necessarily broken data.
The canonicalization scheme that produces fingerprints can be bumped, and then an *unchanged*
model's previously recorded fingerprint no longer matches anything. Falling back to the
`nsURI` path keeps that data readable; the `nsURI` rule then applies as always — a single
candidate is taken, several remain an error naming them (§8.1).

**Diagnostic flooding.** A document repeating one unresolvable fingerprint across thousands of
objects produces **one** warning for that value, not thousands. Suppression is per distinct
fingerprint value, so a second bad value is still reported.

**The pin is never moved by a later object.** A fingerprint identifies the version of the
object carrying it; the pin keeps the *first* version established for an `nsURI`. This is the
read-side half of the writer's first-touch rule: a writer leaves objects matching the pin
unmarked, so moving the pin would silently reinterpret exactly those objects — the ones that
carry no information of their own — as belonging to a deviating version.

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
| Unresolvable `_type` and no `CODEC_ROOT_TYPE` | ERROR | "Cannot deserialize: type value '<value>' could not be resolved and no CODEC_ROOT_TYPE hint was given" |
| Invalid `CODEC_ROOT_TYPE` (not a resolvable EClass or String type identifier) | ERROR | Type hint must be an EClass or a resolvable type URI / qualified name |
| Unknown `CODEC_ROOT_FINGERPRINT` (no package for fingerprint) | ERROR | "Unknown root fingerprint: `<fp>`" |
| `CODEC_ROOT_FINGERPRINT` conflicts with an instance root option's package fingerprint | ERROR | "Root fingerprint `<fp>` does not match `<option>` package `<nsURI>` (`<fp2>`)" |
| Ambiguous nsURI: `> 1` registered version and no `codec.rootFingerprint`/pin (A.3) | ERROR | "Ambiguous nsURI `<nsURI>`: `N` versions registered `[<fp1>, <fp2>, …]`; pass `codec.rootFingerprint` to select one" |
| Abstract EClass as hint | ERROR | Cannot instantiate abstract class |
| JSON property not in EClass | WARNING | Unknown property (skipped) |

**In-band fingerprint (§2.12).** These arise from the *data*, so their severity depends on the
strictness mode — unlike the caller-side rows above, which are caller bugs and errors in every
mode:

| Scenario | LENIENT | STRICT | Message |
|----------|---------|--------|---------|
| Unknown fingerprint in the data | WARNING, falls back to `nsURI` resolution | ERROR | "Unknown fingerprint `<fp>` in the data for nsURI `<nsURI>`; falling back to nsURI resolution" |
| Data fingerprint contradicts `codec.rootFingerprint` | WARNING, caller wins | ERROR | "Fingerprint in the data (`<fp>`) contradicts the caller's fingerprint (`<fp2>`) for nsURI `<nsURI>`; the caller's choice wins" |

Both are reported **once per distinct fingerprint value** per load, so a document repeating one
bad value cannot bury the rest of the diagnostics. Candidate lists in ambiguity messages name
fingerprints, not model contents, keeping the model inventory undisclosed (S-12).

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
| `CODEC_ROOT_FINGERPRINT` | `codec.rootFingerprint` | `String` | Selects the package version (directive, §2.11) |
| `CODEC_FINGERPRINT_KEY` | `codec.fingerprintKey` | `String` | Non-default key for the in-band fingerprint (§2.10) |

### Save Options

| Java Constant | Property Key | Value Type | Purpose |
|---------------|--------------|------------|---------|
| `CODEC_FINGERPRINT_MODE` | `codec.fingerprintMode` | `String` (`NONE` \| `FIRST_TOUCH`) | Opt-in for writing the in-band fingerprint (§2.10) |
| `CODEC_FINGERPRINT_KEY` | `codec.fingerprintKey` | `String` | Key to write the fingerprint under (§2.10) |

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
