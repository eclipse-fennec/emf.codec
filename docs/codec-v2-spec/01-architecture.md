# Serialization Architecture

[← Back to Overview](00-overview.md)

---

## 1. Component Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      CodecResource                               │
│  (EMF Resource implementation)                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────┐    ┌──────────────────┐                   │
│  │ ConfigurationMerger │  │ EffectiveCodecConfig │               │
│  │ (merges all levels) │──▶│ (immutable snapshot) │              │
│  └──────────────────┘    └──────────────────┘                   │
│                                  │                               │
│                                  ▼                               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    Jackson Integration                     │   │
│  │  ┌────────────────┐  ┌────────────────┐                   │   │
│  │  │ CodecJsonFactory │ │ CodecJsonParser │                  │   │
│  │  └────────────────┘  └────────────────┘                   │   │
│  │  ┌────────────────────────────────────────────────────┐   │   │
│  │  │ CodecJsonReadContext / CodecJsonWriteContext        │   │   │
│  │  │ (carries EMF state during parsing/generation)       │   │   │
│  │  └────────────────────────────────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Configuration Merging

The `ConfigurationMerger` combines all configuration levels into a single `EffectiveCodecConfig`:

```
Load/Save Options (highest priority)
        ↓
ResourceFactory Defaults
        ↓
Codec Module Config
        ↓
Configuration Properties
        ↓
EAnnotations (MetadataService)
        ↓
Built-in Defaults (lowest priority)
        ↓
    ═══════════════════════════
    │ EffectiveCodecConfig    │
    │ (immutable, lazy-cached) │
    ═══════════════════════════
```

> **See also:** [Configuration Resolution](02-config-resolution.md) for the complete two-dimensional configuration model (Source Hierarchy × Scope Chain).

---

## 3. Context Hierarchy

During serialization/deserialization, context objects carry EMF state:

```
EMFContextHolder (internal holder)
    │
    ├── EffectiveCodecConfig (single source of truth)
    │       ├── getClassConfig(EClass) → EffectiveClassConfig
    │       └── getFeatureConfig(EStructuralFeature) → EffectiveFeatureConfig
    │
    ├── Current EObject
    ├── Current EStructuralFeature
    ├── Current Type Hint (for deserialization)
    └── EMF Resource
```

---

## 4. Serialization Flow

1. `CodecResource.doSave()` creates `EffectiveCodecConfig` via `ConfigurationMerger`
2. `CodecJsonFactory` creates generator with `CodecJsonWriteContext`
3. `CodecEObjectSerializer` writes metadata fields, then iterates features:
   - **Metadata field ordering:** The `idOnTop` property (from effective ID config) determines whether `_id` or `_type` is written first:
     - `idOnTop=true`: `_id` → `_type` → `_supertype` → features
     - `idOnTop=false` (default): `_type` → `_supertype` → `_id` → features
   - Get `EffectiveFeatureConfig` for each feature
   - Delegate to `SerializationEntry` (Attribute, Reference, etc.)
4. Each entry uses pre-merged config (no fallback logic needed)

> **See also:** [Type](06-type.md) (§5.0.2), [ID](09-id.md) (§8.7, §10 step 6), [Reference](10-reference.md), [Feature](11-feature.md) for serialization details per target.

---

## 5. Deserialization Flow

1. `CodecResource.doLoad()` creates `EffectiveCodecConfig` via `ConfigurationMerger`
2. `CodecJsonFactory` creates parser with `CodecJsonReadContext`
3. `CodecEObjectDeserializer`:
   - Resolve type (from content or hint)
   - Create EObject
   - Iterate JSON fields, match to `DeserializationEntry`
4. Post-processing: resolve references

> **See also:** [Load/Save Options](13-load-save-options.md) for root type hints and feature type hints during deserialization.

### 5.1 Deferred Properties (Order-Independent Parsing)

JSON field order is **irrelevant** for deserialization. Metadata fields (`_type`, `_id`, etc.) can appear anywhere in the object - before, after, or between data fields.

When data fields appear **before** metadata fields, they are **deferred** and processed after metadata resolution:

```json
{
  "name": "John",
  "age": 30,
  "_type": "http://example.org/1.0#//Person",
  "_id": "john-123"
}
```

**Processing:**
1. `name` and `age` encountered before `_type` → stored in deferred buffer
2. `_type` encountered → EClass resolved, EObject created
3. `_id` encountered → ID set on EObject
4. Deferred properties replayed → `name` and `age` set on EObject

**Supported deferred value types:**
- Primitives: String, Number, Boolean, null
- Nested objects: Stored as `Map<String, Object>`, replayed as JSON objects
- Arrays: Stored as `List<Object>`, replayed as JSON arrays
- Deep nesting: Fully supported

**Why this matters:**
- JSON spec does not guarantee object key order
- Different serializers/APIs may emit fields in different orders
- Allows natural JSON authoring without worrying about metadata position
- ID-based references within the same document work regardless of order

---

## 6. Key Classes

| Class | Purpose |
|-------|---------|
| `CodecResource` | EMF Resource implementation |
| `ConfigurationMerger` | Merges configuration levels |
| `EffectiveCodecConfig` | Immutable merged config |
| `EffectiveClassConfig` | Per-EClass effective config |
| `EffectiveFeatureConfig` | Per-feature effective config |
| `CodecJsonFactory` | Creates parser/generator |
| `CodecJsonReadContext` | Read-side EMF context |
| `CodecJsonWriteContext` | Write-side EMF context |
| `EMFContextHolder` | Internal state holder |
| `SerializationEntry` | Per-feature serializer |
| `DeserializationEntry` | Per-feature deserializer |

> **See also:** [Annotation Reference](16-annotation-reference.md) for all EAnnotation keys and their corresponding Java configuration.

---

[Next: Configuration Resolution →](02-config-resolution.md)
