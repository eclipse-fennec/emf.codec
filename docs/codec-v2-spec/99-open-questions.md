# Open Questions & Implementation Roadmap

[← Test Coverage](19-test-coverage.md)

---

> **See also:** [Annotation Reference](16-annotation-reference.md) (Implementation Status) for detailed feature-by-feature tracking.

This document tracks open design questions and planned implementation work. For detailed implementation status of individual features, see the Implementation Status section in [16-annotation-reference.md](16-annotation-reference.md#implementation-status).

---

## 1. Open Design Questions

### Q1: EPackage Scope Level

Should we add EPackage as a scope level between EClass and Global?

**Context:** Currently the scope chain is `EReference/EAttribute → EClass → Global → Default`. Adding EPackage would allow package-wide defaults.

**Proposal:** See [epackage-scope-proposal.md](../codec-v2-spec-working/epackage-scope-proposal.md)

**Decision needed:**
- Cross-package reference handling (target class's package vs source reference's package)
- Nested package behavior (direct parent only vs ancestor chain)
- Whether to include `typeScope`/`typeFormatScope` at package level

**Status:** Under review

---

### Q2: EMF Configuration Model

Should we model the codec configuration as an EMF model (Ecore) for better tooling and persistence?

**Context:** Currently configuration is programmatic (builders, property maps) or via EAnnotations. An EMF model would enable:
- XMI persistence of configurations
- EMF-based tooling (editors, validation)
- Code generation for builders and constants

**Proposal:** See [emf-configuration-model-proposal.md](../codec-v2-spec-working/emf-configuration-model-proposal.md)

**Decision needed:**
- Merge semantics (`null` = not set vs explicit unset)
- Validation timing (build-time vs runtime)
- Discriminator mapping representation

**Status:** Under review

---

### Q3: EEnum-Level Annotation Support

Should `enumSerialization` be configurable on `EEnum` itself (in addition to per-feature)?

**Context:** Currently enum serialization mode can only be set per-feature. Setting it on the EEnum would provide a default for all uses of that enum.

**Options:**
- A) Per-feature only (current)
- B) EEnum-level with per-feature override
- C) Global → EEnum → Feature hierarchy

**Status:** Not started

---

### Q4: Annotation Source Refactoring

Should inline mapping and type mapping use dedicated annotation sources?

**Context:** Currently all codec annotations use single source `http://eclipse.org/fennec/codec`. Dedicated sources would be cleaner:
- `http://eclipse.org/fennec/codec/inlineMapping`
- `http://eclipse.org/fennec/codec/typeMapping/{mapId}`

**Trade-offs:**
- Pro: Cleaner separation, easier to query
- Con: More complex annotation parsing, migration burden

**Status:** Under consideration

---

## 2. Implementation Roadmap

### 2.1 High Priority (Spec vs Implementation Gaps)

These features are specified but not yet implemented:

| Feature | Spec | Constants | Model | Provider | Codec | Tests |
|---------|:----:|:---------:|:-----:|:--------:|:-----:|:-----:|
| **Feature Visibility** (`ignore*`, `force*`) | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Metadata Merge** (`metadataMerge`, `metadataKey`) | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Fallback Handling** (`fallbackStrategy`, `fallbackEClass`) | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Feature Strictness** (`strictOnUnknown`, `strictOnMissing`) | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **ID Value Key** (`idValueKey`) | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Diagnostic Options** (`failFast`, `suppressWarnings`, etc.) | ✅ | 🔶 | ❌ | ❌ | 🔶 | ❌ |

#### Feature Visibility Refactor

Replace deprecated `transient`/`serialize` with directional visibility:

```
transient=true  →  ignore=true
serialize=false →  ignoreWrite=true
```

New annotations: `ignore`, `ignoreRead`, `ignoreWrite`, `forceRead`, `forceWrite`

**Implementation steps:**
1. Add constants to `CodecAnnotationConstants`
2. Add to `BaseFeatureConfig` EMF model
3. Update `CodecAspectProvider` parsing
4. Update codec serializer/deserializer
5. Add tests
6. Update spec `11-feature.md`

#### Metadata Merge

Combine type and ID into single metadata object in STRUCTURED format:

```json
// Without merge
{ "_type": {...}, "_id": {...}, "name": "John" }

// With merge (metadataKey="_meta")
{ "_meta": { "type": {...}, "id": {...} }, "name": "John" }
```

#### Fallback Handling

For discriminator mappings when value doesn't match any mapping:

| `fallbackStrategy` | Behavior |
|-------------------|----------|
| `SKIP` **(default)** | Log WARNING, continue to next resolution step (Type Strategy) |
| `ERROR` | Fail immediately |
| `FALLBACK` | Use `fallbackEClass` (MUST be set, else ERROR) |

> **Note:** See [08-discriminator-mapping.md](08-discriminator-mapping.md#6-fallback-and-error-handling) for complete documentation.

#### Feature Strictness

Control deserialization behavior for missing/unknown features:

| Setting | Behavior |
|---------|----------|
| `strictOnUnknown=true` | Error on unknown JSON field |
| `strictOnUnknown=false` | Skip unknown fields **(default)** |
| `strictOnMissing=true` | Error on missing required field |
| `strictOnMissing=false` | Use default value **(default)** |

---

### 2.2 Medium Priority

| Feature | Description | Status |
|---------|-------------|--------|
| **Type/SuperType Value Reader/Writer** | Wire `typeValueReaderName`, `typeValueWriterName` | Constants exist, not wired |
| **Expand Deserialization** | Deserialize `expand=true` refs as detached EObjects | Not started |
| **Scope Wiring** | Wire `typeScope`, `idScope` etc. in config resolution | Model exists, not used |

---

### 2.3 Lower Priority / Future

| Feature | Description | Reference |
|---------|-------------|-----------|
| EPackage Scope Level | Package-wide configuration defaults | [Proposal](../codec-v2-spec-working/epackage-scope-proposal.md) |
| EMF Configuration Model | Model config as Ecore for tooling | [Proposal](../codec-v2-spec-working/emf-configuration-model-proposal.md) |
| Binary Format Support | CBOR, MessagePack adapters | [17-format-abstraction.md](17-format-abstraction.md) |
| Streaming API | Large document support | [Format Abstraction](17-format-abstraction.md) (section 11) |
| Validation Hooks | Custom validators during deser | Future |

---

## 3. Resolved Questions

### Design Decisions (Closed)

| Question | Resolution | Date |
|----------|------------|------|
| Type strategy default | URI (full EClass URI) | 2026-01 |
| Reference format default | STRUCTURED (with `_type` and `_ref`) | 2026-01 |
| Smart compression behavior | Omit type when instance equals declared type | 2026-01 |
| Configuration hierarchy | Dynamic before static, 6 levels | 2026-01 |
| Proxy handling | Create EMF proxies for unresolved references | 2026-01 |
| SuperType selection default | SINGLE (only direct parent) | 2026-01 |
| Error recovery | Fail fast on first error | 2026-01 |
| Circular reference detection | No opposite expansion (`expandIgnoreBidirectional=true`) | 2026-01 |
| Feature order guarantee | Inherently stable (EMF model order) | 2026-01 |
| PLAIN vs STRUCTURED default | STRUCTURED for references | 2026-01 |
| Property key naming | Flat `codec.camelCase` (not hierarchical) | 2026-01 |
| Root type option name | `CODEC_ROOT_TYPE` (not `CODEC_ROOT_OBJECT`) | 2026-01 |

### Spec Clarifications (Closed)

| Issue | Resolution |
|-------|------------|
| Smart compression docs | Consolidated in [05-global-options.md](05-global-options.md) |
| Value reader/writer context | Added `CodecReaderContext`/`CodecWriterContext` with config access |
| Diagnostic reporting | Custom readers/writers use `ctx.addError()`/`ctx.addWarning()` |

---

## 4. Contributing

When adding new questions:

1. **Design questions** go in Section 1 with clear context and options
2. **Implementation gaps** go in Section 2 with status tracking
3. **Resolved items** move to Section 3 with resolution summary

When resolving questions:

1. Update the relevant spec chapter(s)
2. Move question to Section 3 with resolution
3. Update [16-annotation-reference.md](16-annotation-reference.md) Implementation Status if applicable

---

[← Back to Overview](00-overview.md)
