# Reference Serialization

[← ID Serialization](09-id.md) | [Next: Feature Serialization →](11-feature.md)

---

> **See also:**
> - [Naming Conventions](03-naming-conventions.md) for key naming conventions
> - [Annotation Reference](16-annotation-reference.md) (Reference Configuration) for complete configuration keys

---

This chapter covers both **non-containment references** and **cross-document containments**, as they share the same serialization format and configuration.

## 1. Reference Strategies

### 1.1 PLAIN Strategy

Single value (URI or ID):

```json
{
  "employer": "datainmotion"
}
```

Or as URI:
```json
{
  "employer": "http://example.org/data#//@companies.0"
}
```

> **Scope:** PLAIN format applies to **non-containment references** only.

#### Type Resolution for PLAIN Format

Unlike STRUCTURED format (which carries type information in the `_type` field), PLAIN format contains **only the reference value** — no type information is transported. The deserializer must determine the proxy EClass from external sources:

| Priority | Source | Description |
|:--------:|--------|-------------|
| 1 | `CODEC_FEATURE_TYPE_HINTS` | Runtime load option (see [Load/Save Options §3](13-load-save-options.md#3-feature-type-hints)) |
| 2 | `EReference.getEReferenceType()` | Declared reference type from Ecore model |

**Safe usage:** PLAIN format works when:
- The declared reference type is **concrete** (not abstract), OR
- A `CODEC_FEATURE_TYPE_HINTS` entry is provided at load time

**Polymorphism limitation:** PLAIN format loses instance type information. If a reference can hold multiple subtypes (e.g., `Employee` and `Manager` both extend `Person`), the deserializer cannot distinguish them — it uses the declared type or hint, not the original instance type. Use STRUCTURED format when preserving concrete types matters.

### 1.2 STRUCTURED Strategy (Default)

Nested object with type and reference:

```json
{
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_ref": "datainmotion"
  }
}
```

**Configurable keys:**
- `refTypeKey`: type field (default: `_type`)
- `refKey`: reference field (default: `_ref`)

> **Why STRUCTURED is the default:** A non-containment reference needs to carry **two** pieces of information — the **type** of the referenced object (so the deserializer can create the correct proxy EClass) and the **reference URI** (so the proxy can be resolved). PLAIN format can only carry a single string value (the URI), losing the type information entirely (see [§1.1 limitation](#11-plain-strategy)). STRUCTURED format provides a JSON object with room for both `_type` and `_ref`, making it the safe default for polymorphic models. PLAIN is available as an opt-in **only** when the declared reference type is concrete and no subtypes are expected.

#### 1.2.1 Version Identity in a Reference Entry

A reference's type object **is** type context, so the in-band
[EPackage fingerprint](06-type.md#8-in-band-epackage-fingerprint) attaches here as an inner
key, under the same sparse first-touch rule:

```json
{
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "fingerprint": "fp1:9f2c4e…",
    "_ref": "datainmotion"
  }
}
```

Object writer and reference writer **share** the per-save pins, so whichever site first brings
a package into the document announces it, and the other stays silent. A reference is often
that first site.

**Why this matters more for references than anywhere else.** Under cross-package inheritance
the declared `eReferenceType` is only an *upper bound* — the actual target may be a subtype
from another package version. The reader must therefore pick the version-correct `EClass`
*instance* at ref-read time, because that instance is what the proxy is built from. Without it,
the proxy either cannot be created or is created against the wrong version's class.

**The fingerprint key is metadata, not projection data.** A reference entry that carries fields
beyond `_ref`/`_type`/`_id` is treated as a *proxy with projection* and deserialized as a full
object. The fingerprint key must be recognised as part of the type context, or a plain proxy
reference silently changes shape into a projection.

**PLAIN references have no room for it.** A bare URI string cannot carry a fingerprint, so a
PLAIN reference resolves against the context — the pin established for the target's `nsURI`, or
the `ResourceSet`. Documents that need self-describing mixed-version cross-references must use
STRUCTURED: the format choice carries the capability. Enriching the URI itself with query
parameters or fragments stays rejected, since that would contaminate `nsURI` identity, which is
load-bearing across EMF.

---

## 2. Multi-valued References

### PLAIN Strategy
```json
{
  "employees": ["john-doe", "jane-smith", "bob-wilson"]
}
```

### STRUCTURED Strategy (Default)
```json
{
  "employees": [
    { "_type": "http://example.org/person/1.0#//Employee", "_ref": "john-doe" },
    { "_type": "http://example.org/person/1.0#//Employee", "_ref": "jane-smith" },
    { "_type": "http://example.org/person/1.0#//Manager", "_ref": "bob-wilson" }
  ]
}
```

---

## 3. Reference Configuration

The configuration defines **keys and format**, not actual values.

### 3.1 Configuration Keys

| Annotation Key | Property Key | Global | ERef | Default | Description |
|----------------|--------------|:------:|:----:|---------|-------------|
| `refFormat` | `codec.refFormat` | ✅ | ✅ | `STRUCTURED` | Output format (PLAIN, STRUCTURED) |
| `refKey` | `codec.refKey` | ✅ | ✅ | `_ref` | Reference value key |
| `refTypeKey` | `codec.refTypeKey` | ✅ | ✅ | `_type` | Type key in STRUCTURED |
| `expand` | `codec.expand` | ✅ | ✅ | `false` | Inline full object vs proxy |

**Note:** Reference configuration is NOT supported on EClass (different references on the same class may need different formats).

**Note:** Type information in STRUCTURED format follows the type configuration (see [Type Serialization](06-type.md)). With smart compression enabled, type is omitted when instance type equals reference type (see [Global Options - Smart Compression](05-global-options.md#1-smart-compression)).

### 3.2 EAnnotation (on EReference)

```xml
<!-- Switch to PLAIN format (STRUCTURED is default) -->
<eStructuralFeatures xsi:type="ecore:EReference" name="employer" eType="#//Company">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="refFormat" value="PLAIN"/>
  </eAnnotations>
</eStructuralFeatures>
```

### 3.3 Java Builder (Runtime Override)

**Minimal (default: STRUCTURED):**
```java
CodecConfiguration config = CodecConfiguration.builder().build();
```
**Resulting JSON:**
```json
{
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_ref": "acme-corp"
  }
}
```

**PLAIN format:**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .refFormat(SerializationFormat.PLAIN)
    .build();
```
**Resulting JSON:**
```json
{
  "employer": "acme-corp"
}
```

**STRUCTURED with custom keys (JSON-LD style):**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .refFormat(SerializationFormat.STRUCTURED)
    .refTypeKey("@type")
    .refKey("@id")
    .build();
```
**Resulting JSON:**
```json
{
  "employer": {
    "@type": "http://example.org/company/1.0#//Company",
    "@id": "acme-corp"
  }
}
```

### 3.4 Property Map

```java
Map<String, Object> options = Map.of(
    "codec.refFormat", "PLAIN"
);

// Or with custom keys
Map<String, Object> options = Map.of(
    "codec.refFormat", "STRUCTURED",
    "codec.refTypeKey", "@type",
    "codec.refKey", "@id"
);
```

---

## 4. Per-Reference Format Configuration

Unlike Type (where strategy is per-class), Reference format **can be configured per-reference**. This allows different references on the same class to use different formats.

### 4.1 Why Per-Reference Format is Supported

| Aspect | Type Strategy | Reference Format |
|--------|---------------|------------------|
| **Applies to** | The object being serialized | How a reference is written |
| **Semantic binding** | Object has one type identity | Different references may need different formats |
| **Use case** | "Person is always identified as Person" | "employer uses STRUCTURED, friends uses PLAIN" |

**Example:** An EClass with multiple references using different formats:

```java
// Reference to employer uses STRUCTURED (for type safety)
ReferenceConfigBuilder.forReference(PersonPackage.Literals.PERSON__EMPLOYER)
    .refFormat(SerializationFormat.STRUCTURED)
    .build();

// Reference to friends uses PLAIN (for compactness)
ReferenceConfigBuilder.forReference(PersonPackage.Literals.PERSON__FRIENDS)
    .refFormat(SerializationFormat.PLAIN)
    .build();
```

**Resulting JSON:**
```json
{
  "_type": "Person",
  "name": "John",
  "employer": {
    "_type": "http://example.org#//Company",
    "_ref": "acme-corp"
  },
  "friends": ["alice", "bob"]
}
```

### 4.2 Configuration Inheritance

Reference format follows the standard configuration hierarchy:

1. **Per-reference annotation/config** (highest priority)
2. **Global codec config**
3. **Built-in default** (`STRUCTURED`)

If no per-reference format is specified, the global default applies.

---

## 5. Proxy and Expand Handling

### 5.1 Default Behavior: Proxy Serialization

Non-containment references are serialized as **proxies by default**. This requires:
- `type`: Type information (proxy URI doesn't always indicate type)
- `ref`: Proxy URI for resolution

Only objects with a URI can be serialized as references (standard EMF behavior).

**Default proxy output (STRUCTURED):**
```json
{
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_ref": "companies.json#//@companies.0"
  }
}
```

#### 5.1.1 Serialization Algorithm

The serializer follows this decision tree for each reference value:

```
serializeReference(target):
  1. Is refValueWriterName configured?
     → YES: Full delegation to custom ValueWriter (DONE)

  2. Is target null?
     → YES: Write null (if serializeNull enabled) or skip

  3. Is this a containment reference AND target in same resource?
     → YES: Serialize target inline (nested object)

  4. Is this a containment reference AND target in different resource?
     → YES: This is CROSS-DOCUMENT CONTAINMENT → serialize as reference

  5. Is expand enabled for this reference AND target is resolved (not proxy)?
     → YES: Serialize target inline (expanded reference)

  6. DEFAULT: Serialize as proxy reference
```

> **"Same resource" for containment (steps 3/4):** a containment target that owns no resource
> of its own is part of the document being written and is always inlined — only a child owning
> a direct resource (`eDirectResource() != null`), or a proxy, is a cross-document containment.
> This holds even when the child also reports no container: models generated with
> `suppressNotification="true"` back containment features with a `BasicInternalEList`, which
> never sets the child's container. See [§9.3.1](#931-supported-behavior) for the exact rule.

#### 5.1.2 Reference Serialization Flow

This is the detailed flow expanding the decision tree above. It covers null handling, ValueWriter delegation, containment/expand dispatch, URI determination, and format output.

> **Prerequisite:** This flow is invoked from the [Feature Serialization Flow](11-feature.md#12-feature-serialization-flow) for EReference features. The feature layer has already evaluated the **visibility gate** (ignore, ignoreWrite, forceWrite, transient/derived checks) and the **value gate** (null/empty/default checks). Only references that passed both gates reach this flow.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                  REFERENCE SERIALIZATION FLOW                               │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: EReference, target EObject (resolved or proxy), effective ReferenceConfig

┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. VALUE WRITER CHECK (full delegation — evaluated first)                   │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Is refValueWriterName configured?                                        │
│    ├─ YES → Full delegation to named ValueWriter service                   │
│    │        - Writer receives: EObject, EReference, target (may be null),  │
│    │          generator, config                                             │
│    │        - Writer handles ALL output (null handling, containment,       │
│    │          expand, format, type, ref value)                              │
│    │        - Framework skips steps 2-6 ─────────────────→ DONE ✓         │
│    │                                                                       │
│    └─ NO  → Continue with built-in serialization (step 2)                  │
│                                                                             │
│    Note: ValueWriter is checked FIRST, before null check and before        │
│    containment/expand dispatch. Consistent with type flow (06-type.md      │
│    §5.0) and feature flow (11-feature.md §12.0). A custom writer has      │
│    full control over all reference output including null handling.          │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 2. NULL CHECK                                                               │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Is target null?                                                          │
│    ├─ YES → Is serializeNull=true?                                          │
│    │        ├─ YES → Write: "key": null ──────────────────→ DONE ✓         │
│    │        └─ NO  → SKIP (omit from output) ────────────→ DONE ✗         │
│    └─ NO  → continue                                                       │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 3. CONTAINMENT / EXPAND DISPATCH                                            │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    3a. CONTAINMENT CHECK                                                    │
│        Is reference.isContainment() AND target in same resource?            │
│        ├─ YES → Serialize target inline (full object, normal ser flow)      │
│        │        ──────────────────────────────────────────→ DONE ✓         │
│        └─ NO  → continue                                                   │
│                                                                             │
│    3b. CROSS-DOCUMENT CONTAINMENT                                           │
│        Is reference.isContainment() AND target in different resource?       │
│        ├─ YES → This is cross-document containment.                         │
│        │        Serialize as reference (continue to step 4)                 │
│        └─ NO  → This is a non-containment reference. continue              │
│                                                                             │
│    3c. EXPAND CHECK                                                         │
│        Is shouldExpand()=true AND target.eIsProxy()=false?                  │
│        (shouldExpand = expand || expandGlobal)                              │
│        ├─ YES → Is expandIgnoreBidirectional=true                           │
│        │        AND reference has eOpposite?                                │
│        │        ├─ YES → SKIP expand, fall through to proxy ────→ step 4   │
│        │        └─ NO  → Serialize target inline (expanded,                │
│        │                 no _ref, no proxy) ─────────────────→ DONE ✓      │
│        └─ NO  → continue (serialize as proxy reference)                    │
│                                                                             │
│    Note: Proxies are NEVER expanded. Expand requires resolved target.       │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 4. URI DETERMINATION                                                        │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Determine the reference URI value:                                       │
│                                                                             │
│    ┌──────────────────────────┬────────────────────────────────────────┐    │
│    │ Scenario                 │ URI                                    │    │
│    ├──────────────────────────┼────────────────────────────────────────┤    │
│    │ Target is proxy          │ InternalEObject.eProxyURI()            │    │
│    │ Same-document ref        │ Fragment-only (#//@feature.index)      │    │
│    │ Cross-document ref       │ Relative URI from source to target     │    │
│    │ Object without resource  │ Fallback: EClass URI                   │    │
│    └──────────────────────────┴────────────────────────────────────────┘    │
│                                                                             │
│    Result: refValue (URI string)                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 5. FORMAT DISPATCH                                                          │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Is refFormat == PLAIN?                                                   │
│    ├─ YES ──────────────────────────────────────────────────────────────   │
│    │   Write plain string value:                                           │
│    │     gen.writeFieldName(featureName)                                   │
│    │     gen.writeString(refValue)                                         │
│    │                                                                       │
│    │   Example: "employer": "companies.json#//@companies.0"                │
│    │   ────────────────────────────────────────────────────→ DONE ✓       │
│    │                                                                       │
│    └─ NO (STRUCTURED) ─────────────────────────────────────────────────   │
│        Write structured object:                                            │
│          gen.writeFieldName(featureName)                                   │
│          gen.writeStartObject()                                            │
│                                                                            │
│          6a. TYPE FIELD (using Type Config, see 06-type.md)                │
│              Determine which type to write in the reference object:        │
│                                                                            │
│              Is serializeInstanceType=true? (default: true)                │
│              ├─ YES → Use target's actual EClass (instance type)           │
│              │        This is the concrete runtime type of the object.     │
│              │        Needed when declared ref type is abstract/interface. │
│              └─ NO  → Use EReference.getEReferenceType() (declared type)  │
│                       Only safe when declared type is concrete.            │
│                                                                            │
│              Then apply smart compression (see 05-global-options.md):      │
│              - If instance type == declared reference type → omit _type   │
│                (deserializer can infer from EReference declaration)        │
│              - Otherwise → write type using typeKey and strategy           │
│                                                                            │
│              Example: "_type": "http://example.org/1.0#//Company"          │
│                                                                            │
│          6b. REF FIELD                                                     │
│              gen.writeFieldName(refKey)  // default: "$ref"                │
│              gen.writeString(refValue)                                      │
│              Example: "$ref": "companies.json#//@companies.0"              │
│                                                                            │
│          gen.writeEndObject()                                              │
│          ──────────────────────────────────────────────────→ DONE ✓       │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 5.1.3 Serialization Summary

| Step | Phase | What | Decides |
|------|-------|------|---------|
| **ValueWriter** (step 1) | Build-time | Custom writer? | Full delegation vs built-in |
| **Null check** (step 2) | Runtime | Is target null? | Whether to write null or skip |
| **Dispatch** (step 3) | Runtime | Containment? Expand? | Inline vs reference serialization |
| **URI** (step 4) | Runtime | Proxy? Same resource? | Reference URI value |
| **Format** (step 5) | Config | PLAIN vs STRUCTURED | Output shape (string vs object) |
| **Type** (step 6a) | Config | Type strategy + smart compression | Type field in STRUCTURED |

> **Design note:** The ValueWriter check is evaluated first (step 1), before null check and containment/expand logic — consistent with all other flows. A custom writer receives the raw target (which may be null) and has full control over all output. The format dispatch happens AFTER URI determination because both formats use the same URI value — they differ only in output shape (bare string vs structured object).

**Edge Cases:**

| Scenario | Behavior |
|----------|----------|
| Reference to unresolved proxy | Serialize using proxy URI |
| Reference to object with no resource | Use fallback EClass URI |
| Bidirectional reference with expand | Skip if `expandIgnoreBidirectional=true`, fall through to proxy |
| Multi-valued reference | Array of proxy/expanded objects (each element through same flow) |
| Proxy + expand enabled | Expand is skipped (proxy not resolved), serialize as proxy |

### 5.2 Expand: Inline Serialization

When expand is enabled and the reference is **resolved** (not a proxy), the referenced object is serialized inline instead of as a proxy reference.

**Important:** Expanded objects are serialized **without `ref`**. This distinguishes them from proxy references during deserialization:
- With `ref` → proxy (resolve via URI)
- Without `ref` → orphan object (fully deserialized, not contained)

**Expanded output:**
```json
{
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_id": "acme-corp",
    "name": "Acme Corporation",
    "industry": "Manufacturing"
  }
}
```

Note: No `ref` field is present. The object is serialized with its full data, and on deserialization it becomes an orphan object (not a proxy).

**Expand Configuration:**

| Level | Option | Type | Default | Description |
|-------|--------|------|---------|-------------|
| Codec | `expandGlobal` | `boolean` | `false` | Expand ALL non-containment references |
| Codec | `expand` | `EReference...` or `String...` | empty | Expand SPECIFIC references only |
| Codec | `expandDepth` | `int` | `1` | Max depth for nested expansion (only `1` currently supported) |
| Codec | `expandIgnoreBidirectional` | `boolean` | `true` | Skip opposite/bi-directional references |

A reference is expanded if:
- `expandGlobal=true`, OR
- the reference is in the `expand` list (by EReference or by name)

> **Implementation Status:** `expandDepth` values greater than 1 are not yet implemented. Currently, expansion only works for direct references (depth=1). Nested expansion (where expanded objects also expand their references) is planned for a future release.

**EAnnotation:**

On EClass (global for all references in this class):
```xml
<eClassifiers xsi:type="ecore:EClass" name="Person">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="expandGlobal" value="true"/>
  </eAnnotations>
</eClassifiers>
```

On EReference (specific reference):
```xml
<eStructuralFeatures xsi:type="ecore:EReference" name="employer" eType="#//Company">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="expand" value="true"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Java Builder:**

```java
// Expand ALL non-containment references
CodecConfiguration.builder()
    .expandGlobal(true)
    .expandDepth(2)
    .expandIgnoreBidirectional(true)
    .build();

// Expand SPECIFIC references by EReference (type-safe)
CodecConfiguration.builder()
    .expand(
        PersonPackage.eINSTANCE.getPerson_Employer(),
        PersonPackage.eINSTANCE.getPerson_Manager()
    )
    .build();

// Expand SPECIFIC references by name
CodecConfiguration.builder()
    .expand("employer", "manager")
    .build();

// Mixed: add references incrementally
CodecConfiguration.builder()
    .expand(PersonPackage.eINSTANCE.getPerson_Employer())
    .expand("manager")
    .build();
```

**Option Values:**

The `expand` option accepts multiple value types:

| Value Type | Example |
|------------|---------|
| `String` | `"employer"` |
| `String[]` | `new String[]{"employer", "manager"}` |
| `List<String>` | `List.of("employer", "manager")` |
| `EReference` | `PersonPackage.eINSTANCE.getPerson_Employer()` |
| `EReference[]` | `new EReference[]{...}` |
| `List<EReference>` | `List.of(...)` |

At runtime, string names are resolved against the current EClass to find the matching EReference.

### 5.3 Bi-directional Reference Handling

When expanding, bi-directional (opposite) references are **ignored by default** to prevent cycles:

```
Person.employer ←→ Company.employees (opposite)
```

If expanding `employer`, the `employees` back-reference in Company is skipped.

**Configuration:**
- `expandIgnoreBidirectional=true` **(default)**: Skip opposite references
- `expandIgnoreBidirectional=false`: Include (use with caution, may cause cycles)

---

## 6. Type Configuration per Context

Type serialization can be configured differently for each context:

| Context | Config | Use Case |
|---------|--------|----------|
| Root objects | `typeStrategy(...)` | Main serialization |
| Containments | `containmentTypeStrategy(...)` | Inline contained objects |
| References (proxy + expanded) | `referenceTypeStrategy(...)` | Non-containment refs |

**Java Builder:**
```java
CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.URI)  // root: full URI
    .containmentTypeStrategy(TypeStrategy.NAME)  // contained: just name (context clear)
    .referenceTypeStrategy(TypeStrategy.URI)   // refs: full URI for resolution
    .build();
```
**Resulting JSON:**
```json
{
  "_type": "http://example.org/person/1.0#//Person",
  "_id": "john-doe",
  "name": "John Doe",
  "address": {
    "_type": "Address",
    "street": "123 Main St"
  },
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_ref": "companies.json#//@companies.0"
  }
}
```

---

## 7. Cross-Document Containment

When a contained object is stored in a different document, it is serialized similarly to a reference.

### 7.1 Cross-Document Containment Strategies

#### PLAIN Strategy

```json
{
  "address": "addresses.json#//@addresses.0"
}
```

#### STRUCTURED Strategy (Default)

```json
{
  "address": {
    "_type": "http://example.org/address/1.0#//Address",
    "_ref": "addresses.json#//@addresses.0"
  }
}
```

Or with STRUCTURED type format:

```json
{
  "address": {
    "_type": {
      "schema": "http://example.org/address/1.0",
      "type": "Address"
    },
    "_ref": "addresses.json#//@addresses.0"
  }
}
```

### 7.2 Cross-Document Containment Configuration

Cross-document containments use the same configuration as non-containment references. This includes:
- **Format**: PLAIN or STRUCTURED (default: STRUCTURED)
- **Type configuration**: Same type strategy options
- **Smart compression**: Type omitted when instance type equals declared reference type
- **Expand**: Serialize full object instead of reference (if resolved and in memory)

**Detection:** The serializer detects cross-document containment when:
- The EReference is containment (`isContainment() == true`)
- The contained object owns a **direct resource** of its own (`eDirectResource() != null`) that
  differs from the source resource, or is a proxy — the same rule EMF applies in
  `XMLSaveImpl.saveElement` (see [§9.3.1](#931-supported-behavior))

A contained object that reports **no resource of its own** is part of the document being written
and is always inlined, even when it also reports no container (models generated with
`suppressNotification="true"` leave containment children container-less).

> **Implementation Status:** Cross-document containment **resolution during deserialization** is not yet implemented. Serialization of cross-document containments works correctly (serialized as `_ref`), but deserialization will create proxy objects that require manual resolution via the ResourceSet.

**Type configuration:**
```java
CodecConfiguration.builder()
    .containmentTypeStrategy(TypeStrategy.URI)  // full URI for cross-doc resolution
    .build();
```

**With smart compression (when instance type equals declared reference type):**
```json
{
  "address": {
    "_ref": "addresses.json#//@addresses.0"
  }
}
```

---

## 8. Default Reference Settings

| Setting | Property Key | Default Value |
|---------|--------------|---------------|
| Format | `codec.refFormat` | `STRUCTURED` |
| Type Key | `codec.refTypeKey` | `_type` |
| Ref Key | `codec.refKey` | `_ref` |
| Expand Global | `codec.expandGlobal` | `false` |
| Expand (specific refs) | `codec.expand` | empty |
| Expand Depth | `codec.expandDepth` | `1` |
| Expand Ignore Bidirectional | `codec.expandIgnoreBidirectional` | `true` |

**Default Output:**
```json
{
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_ref": "acme-corp"
  }
}
```

---

## 8.1 EMap keys

An EMap is written as a JSON object whose **field names are the entry keys**. A field name is
a string, so a key feature that is not an `EString` is converted in both directions:

| Direction | Conversion |
|---|---|
| write | `toString()` on the key value — for an `EDataType` this is what `EcoreUtil.convertToString` yields |
| read | `EcoreUtil.createFromString(keyAttribute.getEAttributeType(), fieldName)` |

That covers every `EDataType` whose factory implements `createFromString` — `EInt`, `ELong`,
enums, and anything a custom factory handles:

```json
{ "counts": { "1": "one", "2": "two" }, "levels": { "LOW": "quiet", "HIGH": "loud" } }
```

The two directions compose: a document read and written again carries the same field names.
They are **normalised**, not preserved verbatim — a key spelled `"007"` in the incoming document
comes back as `"7"`, because the round trip goes through the data type, not through the text.

**A field name the data type cannot parse costs that one entry, not the map.** The entry is
dropped, the remaining entries are read as usual, and the failure is reported through the same
path as any other dropped value (`strictOnConversion`, see
[11-feature.md §11.1](11-feature.md#111-strictness-configuration)): a warning by default, a load failure under
`strictOnConversion`.

Dropping the whole map is explicitly not allowed. A caller who receives a successful load with
an empty map cannot tell it apart from a document that carried no entries — a plausible-looking
wrong answer, which is the one failure mode a persistence path must not have (issue #154).

## 8.2 Flattened EMaps (`codec.flatten`) — write only

An EMap reference marked `codec.flatten=true` writes its entries **directly into the parent
object** instead of nesting them under the reference key:

```json
{ "_type": "…#//Container", "name": "cfg", "alpha": "one", "beta": "two" }
```

instead of

```json
{ "_type": "…#//Container", "name": "cfg", "metadata": { "alpha": "one", "beta": "two" } }
```

**This is a one-directional export feature.** The map is not restored on read, and that is a
deliberate limitation rather than a gap: once the entries sit at the same level as the
object's own fields, there is no way to tell which key belonged to the map. Any rule would be
a guess as soon as the document carries a field the model does not declare — and documents
from other producers regularly do.

The reader therefore **skips** those keys silently. It must not report them as unknown
features: that would fill the diagnostics of a clean round trip with noise and, under
`strictOnUnknown`, turn the codec's own output into a load failure.

Use it where the document is consumed by something other than this codec — a flat
configuration view, a report, an export for a system that expects plain key-value objects.
Where the map has to survive a round trip, leave `flatten` off.

> Same class as the tabular exports (CSV, ODS, R-Lang): the output is a projection, not a
> serialization that can be read back.

## 9. Deserialization

### 9.1 Reference Resolution Phases

1. **First pass**: Deserialize all objects, collect unresolved references
2. **Second pass**: Resolve references using collected ID/URI mappings

### 9.2 Non-Containment Reference Deserialization

Non-containment references are deserialized based on the presence of `_ref`:

| Has `_ref`? | Has other fields? | Result |
|-------------|-------------------|--------|
| Yes | No | **Proxy** - URI only |
| Yes | Yes | **Proxy with projection** - URI + populated fields |
| No | Yes | **Orphan object** - fully deserialized, not contained |

#### 9.2.1 Proxy Reference **(default)**

When `_ref` is present, create a proxy object:

```json
{
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_ref": "companies.json#//@companies.0"
  }
}
```

**Deserialization:**
1. Create EObject instance using `EcoreUtil.create(eClass)`
2. Cast to `InternalEObject` and call `eSetProxyURI(uri)`
3. Set on parent as non-containment reference

#### 9.2.2 Proxy with Projection

When `_ref` is present along with additional fields, create a proxy with projected data populated:

```json
{
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_ref": "companies.json#//@companies.0",
    "name": "Acme Corporation",
    "industry": "Manufacturing"
  }
}
```

**Deserialization:**
1. Create EObject instance using `EcoreUtil.create(eClass)`
2. Cast to `InternalEObject` and call `eSetProxyURI(uri)`
3. Populate additional fields (`name`, `industry`)
4. Set on parent as non-containment reference

This is useful for projections where commonly needed fields are included to avoid resolving the full object.

#### 9.2.3 Expanded Orphan Object

When `_ref` is absent, deserialize as a full orphan object (expanded non-containment):

```json
{
  "employer": {
    "_type": "http://example.org/company/1.0#//Company",
    "_id": "acme-corp",
    "name": "Acme Corporation",
    "industry": "Manufacturing"
  }
}
```

**Deserialization:**
1. Deserialize as full EObject (same as containment)
2. Object is **not contained** by parent - it's an orphan
3. Object has **no resource** assigned
4. Set on parent as non-containment reference

**Note:** Orphan objects are transient - they exist in memory but are not part of any resource. This matches the expand use case where data is embedded for reading convenience but won't be saved back in this form.

#### 9.2.4 Reference Deserialization Flow

This flow covers how the codec deserializes a JSON value that represents a non-containment reference or cross-document containment. It is invoked from the per-field processing step in [Feature Deserialization Flow](11-feature.md#132-per-field-processing-json-driven) when the entry type is `ReferenceDeserializationEntry`.

> **Prerequisite:** The feature layer has already evaluated the **visibility gate** (ignore, ignoreRead, forceRead, transient/volatile/changeable checks) during entry building (see [§13.1](11-feature.md#131-feature-entry-building-visibility-gate)). Only references that have a `DeserializationEntry` reach this flow — excluded features have no entry and their JSON fields are skipped as unknown.

> **Implementation Status:** This flow is written for **non-containment references**, but
> cross-document containment follows the same path: a containment object carrying the reference
> key becomes a proxy rather than an inline object. Neither is resolved by the codec — see
> [§9.3](#93-cross-resource-references) for who does the resolving.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                  REFERENCE DESERIALIZATION FLOW                              │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: JsonParser positioned at value token, EReference, effective ReferenceConfig

┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. VALUE READER CHECK (full delegation)                                     │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Is refValueReaderName configured?                                        │
│    ├─ YES → Full delegation to named ValueReader service                   │
│    │        - Reader receives: EObject, EReference, parser, config         │
│    │        - Reader handles everything: format detection, ref extraction, │
│    │          type resolution, object creation                              │
│    │        - Framework skips steps 2-6 ───────────────────→ DONE ✓       │
│    │                                                                       │
│    └─ NO  → Continue with built-in deserialization (step 2)                │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 2. FORMAT DETECTION (auto-detected from JSON structure)                     │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    What is the current JSON token?                                          │
│                                                                             │
│    ├─ VALUE_NULL ─────────────────────────────────────────────────────────  │
│    │   Set reference to null on parent                                     │
│    │   ───────────────────────────────────────────────────→ DONE ✓        │
│    │                                                                       │
│    ├─ VALUE_STRING → PLAIN format detected                                 │
│    │   refValue = parser.getText()                                         │
│    │   → Go to step 3a (PLAIN deserialization)                             │
│    │                                                                       │
│    ├─ START_OBJECT → STRUCTURED format detected                            │
│    │   → Go to step 3b (STRUCTURED deserialization)                        │
│    │                                                                       │
│    ├─ START_ARRAY → Multi-valued reference                                 │
│    │   For each element in array:                                          │
│    │     → Apply steps 2-6 recursively per element                         │
│    │     → Collect results into EList                                      │
│    │   ───────────────────────────────────────────────────→ DONE ✓        │
│    │                                                                       │
│    └─ OTHER → ERROR: unexpected token for reference value                  │
│                                                                             │
│    Note: Unlike serialization (where format comes from config), deser-     │
│    ialization auto-detects format from JSON structure. This means a        │
│    STRUCTURED-configured reference can still read PLAIN data.              │
└─────────────────────────────────────────────────────────────────────────────┘
                          │                   │
                    (PLAIN)                   (STRUCTURED)
                          │                   │
                          ▼                   ▼
┌────────────────────────────────┐  ┌─────────────────────────────────────────┐
│ 3a. PLAIN DESERIALIZATION      │  │ 3b. STRUCTURED DESERIALIZATION           │
│ ──────────────────────────────│  │ ───────────────────────────────────────  │
│                                │  │                                         │
│ refValue = string value        │  │ Parse JSON object fields:               │
│                                │  │                                         │
│ Type resolution:               │  │ while (parser.nextToken()) {            │
│ ─────────────────────────────  │  │   field = parser.currentName()          │
│ PLAIN format carries NO type   │  │                                         │
│ information — type must come   │  │   Is field == refTypeKey (default _type)?│
│ from external sources:         │  │   ├─ YES → typeValue = parse type       │
│                                │  │   │        (see 06-type.md deser flow)  │
│ 1. CODEC_FEATURE_TYPE_HINTS   │  │   │                                      │
│    (runtime per-feature hint,  │  │   Is field == refKey (default $ref)?    │
│    see 13-load-save-options)   │  │   ├─ YES → refValue = parser.getText()  │
│                                │  │   │                                      │
│ 2. EReference.getEReferenceType() │  │   Is field == proxyKey ($proxy)?     │
│    (declared reference type)   │  │   ├─ YES → proxyMarker = true           │
│                                │  │   │                                      │
│ If resolved EClass is abstract │  │   Otherwise → store as extra field      │
│ → CODEC_DESERIALIZATION_MODE   │  │              (for projection or orphan) │
│   controls error handling      │  │ }                                       │
│   (see 13-load-save-options)   │  │                                         │
│                                │  │ Type resolution:                        │
│ → Go to step 4                 │  │ - If typeValue present → resolve EClass │
│ (with refValue + EClass)       │  │   (using Type Strategy, see 06-type.md) │
│                                │  │ - If no typeValue → same fallback as    │
│                                │  │   PLAIN (hints → declared type)         │
│                                │  │                                         │
│                                │  │ → Go to step 4                          │
│                                │  │ (with refValue, EClass, extra fields)   │
└────────────────────────────────┘  └─────────────────────────────────────────┘
                          │                   │
                          └─────┬─────────────┘
                                ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 4. REFERENCE KIND DECISION                                                  │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    ┌─────────────────────────────────────────────────────────────────┐      │
│    │ Has refValue (i.e., _ref field present or PLAIN string)?        │      │
│    │                                                                 │      │
│    │ YES → This is a PROXY REFERENCE                                │      │
│    │       Has extra fields?                                         │      │
│    │       ├─ YES → Proxy with Projection (see §9.2.2)             │      │
│    │       └─ NO  → Plain Proxy (see §9.2.1)                       │      │
│    │       → Go to step 5a (create proxy)                           │      │
│    │                                                                 │      │
│    │ NO → This is an EXPANDED ORPHAN OBJECT                         │      │
│    │      (No _ref means this is an expanded inline object)         │      │
│    │      → Go to step 5b (create orphan)                           │      │
│    └─────────────────────────────────────────────────────────────────┘      │
│                                                                             │
│    Note: The presence/absence of refKey is the SOLE discriminator          │
│    between proxy and orphan. This is by design — see §5.2 for why          │
│    expanded objects are serialized without _ref.                            │
└─────────────────────────────────────────────────────────────────────────────┘
                     │                        │
               (has ref)                 (no ref)
                     │                        │
                     ▼                        ▼
┌────────────────────────────────┐  ┌─────────────────────────────────────────┐
│ 5a. CREATE PROXY               │  │ 5b. CREATE ORPHAN (expanded object)      │
│ ──────────────────────────────│  │ ───────────────────────────────────────  │
│                                │  │                                         │
│ 1. Create EObject instance:    │  │ 1. Deserialize as full EObject          │
│    EcoreUtil.create(eClass)    │  │    (same flow as containment deser)     │
│                                │  │                                         │
│ 2. Set proxy URI:              │  │ 2. Object is NOT contained by parent   │
│    ((InternalEObject) obj)     │  │    (it's an orphan — no resource)      │
│    .eSetProxyURI(refValue)     │  │                                         │
│                                │  │ 3. Set on parent as non-containment    │
│ 3. If projection fields exist: │  │    reference                            │
│    Populate extra fields on    │  │                                         │
│    the proxy object            │  │ → Go to step 6                         │
│                                │  │                                         │
│ → Go to step 6                 │  │                                         │
└────────────────────────────────┘  └─────────────────────────────────────────┘
                     │                        │
                     └─────┬──────────────────┘
                           ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 6. SET ON PARENT                                                            │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Is EReference many-valued (isMany())?                                    │
│    ├─ YES → ((EList) parent.eGet(ref)).add(result)                         │
│    └─ NO  → parent.eSet(ref, result)                                       │
│                                                                             │
│    ───────────────────────────────────────────────────────→ DONE ✓         │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Deserialization Summary:**

| Step | Phase | What | Decides |
|------|-------|------|---------|
| **ValueReader** (step 1) | Build-time | Custom reader? | Full delegation vs built-in |
| **Format detect** (step 2) | Runtime | JSON token type | PLAIN (string) vs STRUCTURED (object) |
| **Parse** (step 3a/3b) | Runtime | Extract ref, type, extras | refValue + EClass + extra fields |
| **Kind decision** (step 4) | Runtime | Has refValue? | Proxy vs Orphan |
| **Create** (step 5a/5b) | Runtime | Proxy or full deser | Object + proxy URI or populated fields |
| **Set** (step 6) | Runtime | Single or multi? | eSet vs EList.add |

> **Design note — Serialization/Deserialization Asymmetry:**
> - **Serialization** uses configured format (`refFormat`) to decide output shape
> - **Deserialization** auto-detects format from JSON structure (string → PLAIN, object → STRUCTURED)
> - This means deserialization is **more tolerant** — it can read either format regardless of configuration
> - The `refFormat` config property is primarily a **serialization** concern

**Edge Cases:**

| Scenario | Behavior |
|----------|----------|
| JSON null value | Set reference to null on parent |
| PLAIN string with no matching URI | Create proxy with unresolvable URI |
| STRUCTURED with no `_type` | Fall back to `EReference.getEReferenceType()` |
| STRUCTURED with no `_ref` and no fields | Create empty orphan object |
| Multi-valued: mixed PLAIN + STRUCTURED | Each element auto-detected independently |
| Unknown fields in STRUCTURED | Stored as projection data (proxy) or deserialized (orphan) |

### 9.3 Cross-Resource References

> **Not resolving is the contract, not a gap.** The codec produces proxies and stops there —
> exactly like EMF's XMI reader (`XMLHandler.handleProxy`). Whether a proxy resolves on access
> is decided by EMF and the model, never by the codec. See [§9.3.3](#933-who-resolves-a-proxy).

#### 9.3.1 Supported Behavior

**Serialization:** ✓ Fully supported, containment and non-containment alike
- A contained object that owns a direct resource, or is a proxy, is written as a **reference**
  instead of being inlined — the same rule EMF applies in `XMLSaveImpl.saveElement`
  (`eDirectResource() != null || eIsProxy()`)
- A reference whose target lives in another resource is written with **that resource in the
  URI**, deresolved against the source resource; a same-document target keeps a bare fragment
- The source resource is taken from the object being serialized, so this holds on every save
  path, with or without a format provider

**Deserialization:** ✓ Proxy creation, containment and non-containment alike
- A reference URI produces a **proxy** of the expected type with `eProxyURI` set, resolved
  against the loading resource's URI
- The proxy object is created through the **EFactory of its package**, so a generated model
  yields generated instances and a reflective model dynamic ones — **the codec behaves
  identically for generated and reflective use**
- The proxy is **not resolved by the codec**

#### 9.3.3 Who resolves a proxy

Resolution is EMF semantics and depends on the model, not on the codec:

| Situation | Behavior |
|---|---|
| Non-containment reference, `resolveProxies=true` (default) | EMF resolves on access (`eGet` with resolve) |
| Containment reference, **reflective/dynamic** model | EMF picks a resolving setting delegate at runtime from `resolveProxies` (`EStructuralFeatureImpl`), so it resolves on access |
| Containment reference, **generated** model | Only when the model was generated with the GenModel option **"Containment Proxies"**; otherwise a non-resolving list is generated and the proxy stays a proxy |
| Any of the above, explicitly | `EcoreUtil.resolve(proxy, resourceSet)` always works |

> Cross-resource containment must be *designed into* a model: EMF disables proxy resolution for
> containment references by default for backwards compatibility, and each containment reference
> has to have its `resolveProxies` set deliberately.

#### 9.3.4 When the model owns the reference key

If the referenced type declares a feature under the very key used for references — by name or
via a configured key, as OpenAPI does with `ref` annotated `key="$ref"` — then **the model
wins**: the key carries payload and is never interpreted as a cross-document marker.

#### 9.3.2 Workaround: Manual Resolution

To resolve cross-resource references after loading:

**Option 1: Pre-load resources**

Load all referenced resources into the ResourceSet before deserializing:

```java
ResourceSet resourceSet = new ResourceSetImpl();
resourceSet.getResourceFactoryRegistry()
    .getExtensionToFactoryMap()
    .put("json", new CodecResourceFactory(...));

// Pre-load all resources
Resource companiesResource = resourceSet.getResource(
    URI.createURI("companies.json"), true);
Resource personsResource = resourceSet.getResource(
    URI.createURI("persons.json"), true);

// Now proxies can be resolved via EcoreUtil
EcoreUtil.resolveAll(resourceSet);
```

**Option 2: Lazy resolution**

Use EMF's lazy resolution mechanism:

```java
ResourceSet resourceSet = new ResourceSetImpl();
resourceSet.getResourceFactoryRegistry()
    .getExtensionToFactoryMap()
    .put("json", new CodecResourceFactory(...));

// Load main resource (creates proxies for cross-references)
Resource resource = resourceSet.getResource(
    URI.createURI("persons.json"), true);
Person person = (Person) resource.getContents().get(0);

// Accessing the reference triggers resolution (loads companies.json)
Company employer = person.getEmployer();  // Resolves proxy automatically
```

**Option 3: Explicit resolution**

Resolve specific proxies manually:

```java
Person person = ...; // loaded with unresolved proxy
EObject employerProxy = person.eGet(PersonPackage.Literals.PERSON__EMPLOYER, false);

if (employerProxy.eIsProxy()) {
    URI proxyURI = ((InternalEObject) employerProxy).eProxyURI();
    EObject resolved = resourceSet.getEObject(proxyURI, true);
    person.setEmployer((Company) resolved);
}
```

#### 9.3.3 Expected Future Behavior

Full cross-resource resolution will:
1. Check if resource is already loaded in ResourceSet
2. If not loaded, attempt to load (based on ResourceSet configuration)
3. Resolve proxy to loaded object
4. Fall back to proxy if load fails or is deferred

**Configuration (planned):**

| Option | Type | Description |
|--------|------|-------------|
| `RESOLVE_CROSS_RESOURCE` | boolean | Auto-resolve cross-resource references during load |
| `LOAD_REFERENCED_RESOURCES` | boolean | Auto-load referenced resources into ResourceSet |

### 9.4 Deserialization Options

| Option | Type | Description |
|--------|------|-------------|
| `RESOLVE_PROXIES` | boolean | Resolve proxies during load |

> **Note:** Strictness handling (unknown fields, missing required features) is configured via `strictOnUnknown` and `strictOnMissing` properties at Global/EClass level, not as reference-layer options. See [Feature Strictness](11-feature.md#11-strictness-configuration) for details.

---

## 10. Configuration Validation Rules

This section documents validation rules for Reference Configuration that are checked when building the effective configuration.

### 10.1 Property Applicability Rules

| Rule ID | Property | Invalid At | Severity | Description |
|---------|----------|------------|----------|-------------|
| R-V1 | Any `ref*` key | EClass | WARNING | Reference config is per-reference only, ignored on EClass |
| R-V2 | Any `ref*` key | EAttribute | ERROR | Reference config not applicable to attributes |
| R-V3 | `expand` | EClass | WARNING | Expand is per-reference only, ignored on EClass |
| R-V4 | `expand` | EAttribute | ERROR | Expand not applicable to attributes |

> **Note:** `ref*` includes `refFormat`, `refKey`, and `refTypeKey`.

### 10.2 Ownership Boundaries

Several keys that **appear on EReference** are NOT part of Reference Configuration. They belong to other configuration domains and their validation rules are defined there:

| Key | Belongs To | Validation Defined In |
|-----|------------|----------------------|
| `inlineMapping.*` | Discriminator Mapping | [08-discriminator-mapping.md section 7](08-discriminator-mapping.md#7-invalid-configurations) |
| `fallbackStrategy` | Discriminator Mapping | [08-discriminator-mapping.md section 7](08-discriminator-mapping.md#7-invalid-configurations) |
| `fallbackEClass` | Discriminator Mapping | [08-discriminator-mapping.md section 7](08-discriminator-mapping.md#7-invalid-configurations) |
| `typeStrategy`, `typeKey`, etc. | Type Configuration | [06-type.md section 7](06-type.md#7-configuration-validation-rules) |

These keys are valid on EReference but are governed by their respective specs, not by Reference Configuration. Do NOT add validation rules for them here.

### 10.3 Config-Level Validation Rules

These rules are checked by `ReferenceConfig.validate()` when building the effective configuration. They detect configurations that are technically valid but logically inconsistent or unsupported.

| Rule ID | Condition | Severity | Description |
|---------|-----------|----------|-------------|
| R-V5 | `refTypeKey` ≠ default + `refFormat` ≠ `STRUCTURED` | WARNING | `refTypeKey` is only used in STRUCTURED format; custom value is ignored when format is PLAIN |
| R-V6 | `expandDepth` > 1 | WARNING | Nested expansion (depth > 1) is not yet supported; using depth=1 |
| R-V7 | `expandIgnoreBidirectional` ≠ default + `shouldExpand()` = false | INFO | `expandIgnoreBidirectional` is ignored when neither `expand` nor `expandGlobal` is enabled |

**Details:**

- **R-V5 (refTypeKey + non-STRUCTURED):** The `refTypeKey` field names the type key inside a STRUCTURED reference object. In PLAIN format, there is no object — the reference is a bare string — so a custom `refTypeKey` would never be written or read. This is a WARNING because the config is valid but the custom key has no effect.

- **R-V6 (expandDepth > 1):** Nested expansion (where expanded objects also expand their references) is a planned future feature. Currently only direct expansion (depth=1) is supported. Configuring a higher depth produces a WARNING and the codec silently clamps to depth=1.

- **R-V7 (expandIgnoreBidirectional without expand):** The `expandIgnoreBidirectional` flag controls behavior during reference expansion. When expansion is not enabled (`expand=false` AND `expandGlobal=false`), this flag has no effect. This is an INFO (not WARNING) because it's a harmless misconfiguration that may be intentional (pre-configuring for future expand enablement).

### 10.4 Serialization/Deserialization Symmetry

These properties affect serialization/deserialization symmetry:

| Property | Auto-Detectable? | Symmetry Required | Mismatch Behavior |
|----------|------------------|-------------------|-------------------|
| `refFormat` | ✅ YES (from JSON structure) | ❌ NO | N/A — auto-detected during deserialization |
| `refKey` | ❌ NO | ✅ YES — must match | Ref field not found → orphan instead of proxy |
| `refTypeKey` | ❌ NO | Should match | Type field not found → fallback to declared type |
| `proxyKey` | ❌ NO | ✅ YES — must match | Proxy marker not detected |
| `expand` / `expandGlobal` | N/A | ❌ NO — serialization only | Deserialization uses presence of `_ref` to detect |
| `serializeInstanceType` | N/A | ❌ NO — serialization only | Deserialization auto-detects type presence |

> **Key insight:** The `refFormat` does NOT require symmetry because deserialization auto-detects the format from JSON structure (string → PLAIN, object → STRUCTURED). However, `refKey` requires symmetry — if serialization writes `"$ref"` but deserialization looks for `"_ref"`, it won't find the reference and will treat the object as an expanded orphan.

### 10.5 Related Validation Rules

- **Type Configuration:** See [06-type.md section 7](06-type.md#7-configuration-validation-rules)
- **ID Configuration:** See [09-id.md section 11](09-id.md#11-configuration-validation-rules)
- **Discriminator Mapping:** See [08-discriminator-mapping.md section 7](08-discriminator-mapping.md#7-invalid-configurations)
- **Error Handling:** See [15-error-handling.md section 6.10](15-error-handling.md#610-annotation-parsing-errors-metadata-layer)

---

[Next: Feature Serialization →](11-feature.md)
