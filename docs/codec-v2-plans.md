# Codec V2 Remaining Plans

This document consolidates all active plans for completing the codec migration. It merges the
Spec Compliance Refactoring Plan and the Deprecated API Migration Plan into a single phased roadmap.

**Created:** 2026-02-02
**Updated:** 2026-02-08

**Related documents:**
- [`docs/codec-v2-development-guide.md`](codec-v2-development-guide.md) — Session continuity, current state
- [`docs/codec-v2-spec/`](codec-v2-spec/) — Specification (source of truth)

---

## Table of Contents

1. [Current State](#1-current-state)
2. [Architecture Overview](#2-architecture-overview)
3. [Plan A: Deprecated API Migration](#3-plan-a-deprecated-api-migration--complete) ✅
4. [Plan B: Spec Compliance Gaps](#4-plan-b-spec-compliance-gaps)
5. [Plan C: Documentation Examples](#5-plan-c-documentation-examples-deferred)
6. [Plan D: Discriminator Refactoring](#6-plan-d-discriminator-refactoring-deferred)
7. [Plan E: Multi-Format Support](#7-plan-e-multi-format-support)
8. [Execution Roadmap](#8-execution-roadmap)
9. [Critical Files Reference](#9-critical-files-reference)

---

## 1. Current State

### What's Done

The 8-step package migration from `codec.v2.*` to `codec.*` is **complete**:
- New spec-compliant classes live in `org.eclipse.fennec.codec.*` packages
- **All deprecated code deleted** (old `codec.v2.*` src/test + old `codec.api.value.*`/`codec.api.diagnostic.*`)
- `ConfigurationResolver` replaced `CodecConfiguration` + `ConfigurationMerger`
- All 6 config types have spec + resolver tests (Type, Id, SuperType, Feature, Reference, Discriminator)

**Plan A (Deprecated API Migration) is COMPLETE** (2026-02-04):
- Migrated `codec.api.value.*` to `codec.value.*` API
- Migrated dependent projects: `codec.geojson`, `codec.jsonschema`, `codec.openapi`
- Fixed `forceWrite`/`forceRead` bugs (two-gate model, volatile features)

**Code Cleanup COMPLETE** (2026-02-05):
- Deleted 55 src + 9 test files from `codec.v2.*`, 11 src + 14 test files from deprecated API
- Extracted helper classes: `TypeResolutionHelper` (22 tests), `EMapHelper` (18 tests)
- Cleaned up all `@claude` comments
- Total: 2519 tests, 0 failures, 0 skipped across 7 projects

**Integration Tests + PLAIN Reference Format COMPLETE** (2026-02-06):
- Created `FeatureVisibilityIntegrationTest.java` (20 tests for ignoreRead/ignoreWrite/ignore)
- Enhanced `ExpandReferenceTest.java` (+8 edge case tests)
- Implemented PLAIN reference format serialization and deserialization
- Created `PlainReferenceFormatTest.java` (16 tests)
- Updated spec `10-reference.md` §1.1 PLAIN Strategy
- Total: ~2535 tests, 0 failures

### What Remains

| Category | Description | Status |
|----------|-------------|--------|
| ~~**Plan A**~~ | ~~Migrate deprecated API + cleanup~~ | ✅ COMPLETE |
| **Plan B** | Fill spec compliance gaps (12/14 GAPs done, 2 postponed) | ✅ MOSTLY COMPLETE |
| **Plan C** | Documentation examples (deferred from spec review) | Not Started |
| ~~**Plan D**~~ | ~~Discriminator refactoring~~ | ✅ VERIFIED COMPLETE |
| **Plan E** | Multi-format support (BSON, CSV, Ecowitt) | Not Started |

---

## 2. Architecture Overview

### Package Structure

```
codec.api project (org.eclipse.fennec.codec.api):
├── codec.value.*          ← API interfaces (CodecValueReader/Writer, CodecReaderContext, etc.)
├── codec.config.*         ← Config record types (TypeConfig, IdConfig, etc.)
└── codec.diagnostic.*     ← DiagnosticCollector

codec project (org.eclipse.fennec.codec):
├── codec.ser.*            ← Serialization entries (Type, ID, Feature, Reference)
├── codec.deser.*          ← Deserialization entries (Type, ID, Feature, Reference)
├── codec.config.*         ← Configuration (effective, resolver)
├── codec.module.*         ← CodecModule (Jackson integration)
├── codec.resource.*       ← CodecResource (EMF resource)
├── codec.util.*           ← Helpers (AnnotationHelper, TypeResolutionHelper, EMapHelper, etc.)
├── codec.jackson.*        ← Jackson integration (contexts, buffers)
├── codec.context.*        ← Codec contexts (read/write, entry)
└── codec.value.*          ← Value registry
```

Note: All deprecated `codec.v2.*` and `codec.api.value.*` packages have been deleted (2026-02-05).

### The EffectiveCodecConfig Layers

Three types coexist — this is intentional, not a clash:

| Type | Location | Purpose |
|------|----------|---------|
| **Interface** | `codec.api → codec.value.EffectiveCodecConfig` | Narrow view for custom readers/writers (7 parameter-free getters) |
| **NEW Class** | `codec.config.effective.EffectiveCodecConfig` | Full operation-scoped resolver (~50 methods, builder pattern) |

The interface and class serve **different architectural layers**:
- The **interface** is a snapshot capturing already-resolved configs for the current context
- The **class** is the resolver that computes configs per-EClass/per-feature

A `SimpleEffectiveCodecConfig` bridge (Plan A, Step 1) will connect them.

### The CodecValueRegistry Copies

| Type | Location | Status |
|------|----------|--------|
| **NEW** | `codec.api → codec.value.CodecValueRegistry` | Uses new `CodecValueReader/Writer` with `getName()` auto-registration |
| **OLD** | `codec.api → codec.api.value.CodecValueRegistry` | Deprecated — uses old reader/writer interfaces |

### 3D Configuration Resolution Model

The effective configuration is determined by a 3D matrix:

**Dimension 1: Sources** (priority, highest first):
`Load/Save Options → Resource Options → ResourceFactory → Module → Annotation → Built-in Default`

**Dimension 2: Scope** (specificity, most specific first):
`Feature (EAttribute/EReference) → EClass → Global → Default`

**Dimension 3: Direction:**
`Serialization (write) | Deserialization (read)`

Resolution: First non-null value wins, scanning scope left-to-right, source top-to-bottom.

---

## 3. Plan A: Deprecated API Migration ✅ COMPLETE

**Completed:** 2026-02-04

Replaced all usages of deprecated `codec.api.value.*` types with `codec.value.*` types.

### What Was Done

1. **Core codec migration** - Entry classes, module, resource now use new API
2. **Dependent projects migrated:**
   - `codec.geojson` - Uses `ConfigurationResolver` + `forceWrite`/`forceRead`
   - `codec.jsonschema` - `EPackageValueReader`/`Writer` use new context API
   - `codec.openapi` - `OperationValueReader` uses new context API
3. **Bug fixes:**
   - `forceWrite` now correctly implements two-gate model (visibility vs value gate)
   - `forceRead` now works for volatile features in `ConfigurationResolver`

### API Changes Summary

| Aspect | Old (`codec.api.value`) | New (`codec.value`) |
|--------|------------------------|---------------------|
| Reader method | `read(JsonParser, F, DeserializationContext)` | `read(CodecReaderContext, F)` |
| Writer method | `write(T, F, JsonGenerator, SerializationContext)` | `write(T, F, CodecWriterContext)` |
| Registration | `registerReader("name", reader)` | `register(reader)` (uses `getName()`) |
| Config access | Not available | Via `ctx.getConfig()` |

---

## 4. Plan B: Spec Compliance Gaps

### Gap Summary

| ID | Gap | Priority | Phase | Type | Status |
|----|-----|----------|-------|------|--------|
| GAP-001 | Feature Visibility (directional ignore/force) | HIGH | B1 | — | ✅ DONE |
| GAP-002 | Fallback Strategy enum support | HIGH | B1 | MIGRATION | ✅ DONE |
| GAP-003 | Feature Strictness (strictOnUnknown/Missing) | HIGH | B1 | MIGRATION | ✅ DONE |
| GAP-004 | Diagnostic Options integration | MEDIUM | B2 | NEW FEATURE | POSTPONED |
| GAP-005 | ID Value Key support | HIGH | B1 | — | ✅ DONE |
| GAP-014 | `inherit` annotation type mismatch (boolean vs enum) | MEDIUM | B2 | MIGRATION+MODEL | POSTPONED |
| GAP-006 | Metadata Merge behavior | MEDIUM | B2 | | ✅ DONE |
| GAP-007 | Expand deserialization | MEDIUM | B2 | | ✅ DONE |
| GAP-008 | Value Reader/Writer handlers | MEDIUM | B2 | | ✅ DONE |
| GAP-009 | Scope wiring for runtime options | MEDIUM | B2 | | ✅ DONE |
| GAP-010 | Hierarchy resolution tests | MEDIUM | B2 | | ✅ DONE |
| GAP-011 | Additional misconfig test coverage | LOW | B3 | | ✅ DONE |
| GAP-012 | DeserializationMode usage | LOW | B3 | | |
| GAP-013 | Enum-level annotation support | LOW | B3 | | |

### Phase B1: Core Metadata Gaps (HIGH Priority)

#### GAP-001: Feature Visibility (Directional Ignore/Force) ✅ DONE

**Spec:** 11-feature.md §1.2

**Status:** FULLY IMPLEMENTED. All five boolean fields (`ignore`, `ignoreRead`, `ignoreWrite`, `forceRead`, `forceWrite`) exist in:
- `FeatureCodecAspect` (codec.ecore)
- `CodecAspectProvider` (parses all 5 annotation keys)
- `FeatureConfig` (API layer, with `shouldSerialize()`/`shouldDeserialize()` computed methods)
- `ConfigurationResolver` (resolves all visibility flags + builder convenience methods)
- Runtime serialization/deserialization entries (use `shouldSerialize()`/`shouldDeserialize()`)
- Tests: `FeatureConfigSpecTest`, `ForceReadWriteTest`, `ConfigurationResolverTest`

#### GAP-002: Fallback Strategy Enum Support ✅ DONE

**Spec:** 10-reference.md §10.2, 08-discriminator-mapping.md §6

**Status:** FULLY IMPLEMENTED. Per spec §10.2, `fallbackStrategy` and `fallbackEClass` belong to **Discriminator Mapping**, not Reference Configuration. They apply to:
- **Type Mapping Registry** (on EClass via `typeMapping/{mapId}` annotation source)
- **Inline Mapping** (on EReference via `inlineMapping` annotation source)

**Implementation:**
- `TypeDiscriminatorService.registerInlineMappings()` — parses `fallbackStrategy` + `fallbackEClass` from `inlineMapping` annotations on EReference
- `TypeDiscriminatorService.registerFallbackConfig()` — parses fallback config from `typeMapping/{mapId}` annotations on EClass
- `TypeDiscriminatorRegistry.resolve()` — applies fallback strategy (ERROR throws, SKIP returns null, FALLBACK uses fallbackEClass)
- `DiscriminatorConfig` (API layer) — has `fallbackStrategy` + `fallbackEClass` fields

**Tests:** `CodecResourceInlineMappingTest` (19 tests covering ERROR, FALLBACK, SKIP strategies for inline mapping and type mapping registries)

#### GAP-003: Feature Strictness ✅ DONE

**Spec:** 11-feature.md §11

**Status:** FULLY IMPLEMENTED. Strictness controls how the deserializer handles:
- `strictOnUnknown=true`: ERROR on unknown JSON field (throws IllegalStateException)
- `strictOnUnknown=false`: WARNING + skip unknown field (default)
- `strictOnMissing=true`: ERROR on missing required EMF feature (lowerBound >= 1)
- `strictOnMissing=false`: WARNING + use default value (default)

**Implementation:**
- `ClassConfig` (API layer) — immutable config with `strictOnUnknown`, `strictOnMissing`
- `ConfigurationResolver.resolveClassConfig(EClass)` — resolves with full merge cascade
- `ConfigurationResolver.Builder.strictOnUnknown/strictOnMissing()` — convenience methods
- `EffectiveCodecConfig.resolveClassConfig(EClass)` — bridges to deserializer
- `CodecEObjectDeserializer.deserializeProperty()` — checks strictOnUnknown for non-deferred fields
- `CodecEObjectDeserializer.processDeferredProperties()` — checks strictOnUnknown for deferred fields
- `CodecEObjectDeserializer.checkStrictOnMissing()` — checks strictOnMissing after deserialization

**Tests:** `StrictnessIntegrationTest` (11 tests covering strictOnUnknown, strictOnMissing, and class-level overrides)

#### GAP-004: Diagnostic Options Integration

**Spec:** 15-error-handling.md §6

**Required:** Allow callers to configure diagnostic severity levels via `DiagnosticOptions`.

#### GAP-005: ID Value Key Support ✅ DONE

**Spec:** 09-id.md §4

**Status:** FULLY IMPLEMENTED. `valueKey` exists in:
- `BaseIdConfig` (metadata.ecore) with default `"id"`
- `IdConfig` (API layer) with builder, merge, validation
- `AspectToPropertiesConverter` bridges ecore→properties
- Used in STRUCTURED ID serialization/deserialization

#### GAP-014: `inherit` Annotation Type Mismatch

**Spec:** 12-polymorphism.md §2, 02-config-resolution.md §11.7

**Current state:**
- `ClassCodecAspect.inheritFromParent` is a **boolean** (default true) in codec.ecore
- Annotation key `KEY_INHERIT = "inherit"` exists
- No `AnnotationInheritance` enum defined

**Required:** `AnnotationInheritance` enum with values: `DIRECT` (default), `ALL`, `NONE`. Replace boolean with enum. This requires codec.ecore regeneration.

### Phase B2: Advanced Features (MEDIUM Priority)

| GAP | Description |
|-----|-------------|
| GAP-006 | Review metadata merge semantics against spec |
| GAP-007 | Implement expand deserialization + round-trip tests |
| GAP-008 | Wire value reader/writer handler names to actual implementations |
| GAP-009 | Connect scope resolution for load/save options at runtime |
| GAP-010 | Comprehensive hierarchy resolution test coverage |

#### GAP-006: Metadata merge behavior ✅ DONE

**Spec:** 02-config-resolution.md (Two-Dimensional Configuration Model)

**Status:** FULLY IMPLEMENTED. Metadata merge behavior is comprehensively tested via spec tests:
- **15 Spec Test Files**: TypeConfig, IdConfig, SuperTypeConfig, DiscriminatorConfig, FeatureConfig, ReferenceConfig, MetadataMerge, Strictness, ConfigurationResolver
- **Source Hierarchy (Vertical)**: OPTIONS → RESOURCE → FACTORY → MODULE → ANNOTATION → DEFAULT (tested)
- **Scope Chain (Horizontal)**: FEATURE → ECLASS → GLOBAL → DEFAULT (tested)
- **Combined Resolution**: Both dimensions interact correctly (tested)
- **Mergeable Pattern**: All config types implement `Mergeable<T>` with proper `mergeWith()` semantics

#### GAP-008: Value Reader/Writer handlers ✅ DONE

**Spec:** 14-custom-values.md

**Status:** FULLY IMPLEMENTED. All components are in place:
- **API interfaces**: `CodecValueReader<T,F>`, `CodecValueWriter<T,F>`, `AttributeValueReader<T>`, `AttributeValueWriter<T>`, `ReferenceValueReader<T>`, `ReferenceValueWriter<T>`
- **Context interfaces**: `CodecReaderContext`, `CodecWriterContext` with parser/generator, config, diagnostics
- **Registry**: `CodecValueRegistry` with auto-registration via `getName()`
- **Entry classes**: All entries (`AttributeDeserializationEntry`, `AttributeSerializationEntry`, `ReferenceDeserializationEntry`, `ReferenceSerializationEntry`) invoke readers/writers with `canHandle()` validation
- **Activation via annotation**: `valueReaderName`/`valueWriterName` on features
- **Load/Save options by name**: `codec.featureValueReaders`, `codec.featureValueWriters`
- **Load/Save options by instance**: `codec.featureValueReaderInstances`, `codec.featureValueWriterInstances` (wired 2026-02-08)
- **Tests**: `CodecResourceCustomValueTest.java` (including instance binding tests), entry-level tests

#### GAP-007: Expand deserialization ✅ DONE

**Spec:** 10-reference.md §5 (Proxy and Expand Handling)

**Status:** FULLY IMPLEMENTED. Expand deserialization + round-trip is complete:
- **ReferenceDeserializationEntry**: `deserializeOrphanObject()` handles expanded objects (no `_ref`)
- **Auto-detection**: Presence of `_ref` → proxy reference; absence → orphan (expanded)
- **Projection support**: `_ref` + additional fields → proxy with projected data
- **Multi-valued**: Array of expanded objects supported
- **Tests**: `ExpandReferenceTest.java` (18 tests covering serialization, deserialization, round-trip, edge cases)

#### GAP-009: Scope wiring for runtime options ✅ DONE

**Spec:** 16-annotation-reference.md §§2-3 (Scope Chain, Programmatic Configuration)

**Status:** FULLY IMPLEMENTED. Scope-level configuration via load/save options is now fully supported:
- **ConfigProperty**: Added `ECLASS_CONFIG`, `EREFERENCE_CONFIG`, `EATTRIBUTE_CONFIG` scope keys
- **ConfigurationResolver**: `extractClassProperties()` and `extractFeatureProperties()` now support both:
  - `Map<EClass, Map<String, Object>>` via `codec.eClassConfig` (type-safe EClass keys)
  - `Map<String, Object>` with class name strings (backward compatible)
- **ReferenceConfig**: Added `typeKey` and `idKey` per-reference overrides (per spec §3 example)
- **Tests**: `ConfigurationResolverTest$ScopeConfigurationTests` (5 tests covering EClass/EReference/EAttribute scoping)

#### GAP-010: Hierarchy resolution tests ✅ DONE

**Spec:** 02-config-resolution.md §4 (Combined Resolution Algorithm), 06-type.md (Type Resolution)

**Status:** FULLY IMPLEMENTED. Comprehensive test coverage for hierarchy resolution:
- **TypeResolutionHelperTest**: 27 tests covering URI resolution, NAME strategy, smart compression
- **TypeResolutionHintTest**: Tests for feature-specific type hints
- **TypeResolutionUriTest**: Tests for URI-based type resolution
- **SuperTypeConfigTest**: 33 tests covering merge cascade and EAllSuperTypes annotation walk
- **ConfigurationResolverSpecTest**: Tests for two-dimensional resolution (vertical source + horizontal scope)
- **ConfigurationResolver**: `getAnnotationConfig()` walks `eClass.getEAllSuperTypes()` for inherited annotations

### Phase B3: Polish (LOW Priority) ✅ MOSTLY COMPLETE

| GAP | Description | Status |
|-----|-------------|--------|
| GAP-011 | Additional misconfig test cases | ✅ DONE (22 test files) |
| GAP-012 | Wire `DeserializationMode` enum (LENIENT, STRICT, AUTO_DETECT) | ✅ DONE |
| GAP-013 | Codec annotations on EEnum types | ⏸️ POSTPONED |

#### GAP-011: Additional misconfig test coverage ✅ DONE

**Status:** COMPREHENSIVE COVERAGE EXISTS. 22 test files cover validation, diagnostics, and misconfiguration scenarios:
- `TypeConfigTest`, `TypeConfigSpecTest`, `TypeConfigResolverSpecTest` - Type validation
- `ReferenceConfigTest`, `ReferenceConfigSpecTest`, `ReferenceConfigResolverSpecTest` - Reference validation
- `FeatureConfigTest`, `FeatureConfigSpecTest`, `FeatureConfigResolverSpecTest` - Feature validation
- `IdConfigTest`, `IdConfigSpecTest`, `IdConfigResolverSpecTest` - ID validation
- `SuperTypeConfigTest`, `SuperTypeConfigSpecTest`, `SuperTypeConfigResolverSpecTest` - SuperType validation
- `DiscriminatorConfigTest`, `DiscriminatorConfigSpecTest`, `DiscriminatorConfigResolverSpecTest` - Discriminator validation
- `StrictnessConfigSpecTest`, `MetadataMergeConfigSpecTest` - Strictness and merge validation
- All validate() methods have tests; DiagnosticCollector warns/errors for invalid configs

#### GAP-012: DeserializationMode usage ✅ DONE

**Spec:** 06-type.md §6.5.2 (Deserialization Mode), 07-supertype.md §9.3

**Status:** FULLY IMPLEMENTED. `DeserializationMode` (LENIENT, STRICT, AUTO_DETECT) is now wired into the runtime:

**Implementation:**
- `ContextHelper`: Added `DESERIALIZATION_MODE` constant + helper methods (`isStrictMode()`, `isLenientMode()`, `isAutoDetectMode()`, `getDeserializationMode()`, `setDeserializationMode()`)
- `CodecResource.doLoad()`: Reads `CODEC_DESERIALIZATION_MODE` from load options and sets as context attribute
- `CodecEObjectDeserializer.readTypeValueAsString()`: Reports ERROR (STRICT) or WARNING (LENIENT) for unexpected type tokens
- `CodecEObjectDeserializer.resolveTypeFromValue()`: Issues ERROR (STRICT) or WARNING (LENIENT) when type resolution fails
- `TypeDeserializationEntry.handleTypeResolutionFailure()`: Uses mode to determine ERROR vs WARNING severity

**Key Design Decision:**
- **DeserializationMode** controls whether errors **break** deserialization (STRICT → ERROR) or just produce warnings (LENIENT → WARNING)
- **SuperType validation** is a separate opt-in feature via `validateSuperTypeHierarchy` flag, independent of DeserializationMode
- `strictOnUnknown`/`strictOnMissing` remain separate features for unknown fields and missing required features

**Tests:** `DeserializationModeTest.java` (12 tests covering LENIENT, STRICT, AUTO_DETECT, fallback behavior, error reporting)

#### GAP-013: Enum-level annotation support ⏸️ POSTPONED

**Status:** POSTPONED per user request. Spec 11-feature.md §4.1 mentions `enumSerialization` on EEnum, but currently only EAttribute-level is implemented. This is a "nice to have" for global enum serialization strategy.

---

## 5. Plan C: Documentation Examples (Deferred)

These documentation tasks were identified during spec review but deferred as lower priority.

| ID | Task | Spec Section | Priority |
|----|------|--------------|----------|
| DOC-001 | SuperType load/save options examples | `07-supertype.md` §6 | MEDIUM |
| DOC-002 | Reference expand examples (annotation + options + side-by-side output) | `10-reference.md` §4.2 | HIGH |
| DOC-003 | Custom value readers/writers registration examples | `14-custom-values.md` §3-4 | MEDIUM |
| DOC-004 | NUMERIC type strategy round-trip example | `06-type.md` §1.6 | MEDIUM |

---

## 6. Plan D: Discriminator Refactoring

**Status: VERIFIED COMPLETE** (2026-02-08)

These items were verified against current spec and implementation:

### D1: Remove MAPPED from TypeStrategy Enum

**Status:** ✅ DONE

`MAPPED` has been removed from `TypeStrategy` enum. Discriminator mapping is now a separate layer that works alongside any strategy. See spec `06-type.md` line 23: "MAPPED was removed from TypeStrategy."

### D2: Inline Mapping for References

**Status:** ✅ DONE

Inline mapping is fully implemented with dedicated annotation source:
- Annotation source: `http://eclipse.org/fennec/codec/inlineMapping`
- Fully documented in spec `08-discriminator-mapping.md` section 5
- Tests in `CodecResourceInlineMappingTest.java`

### D3: Property-Based Discriminator Configuration

**Status:** ✅ DONE

Programmatic configuration fully documented in spec:
- `08-discriminator-mapping.md` sections 4.4 and 5.1 show builder API and property map examples
- Property keys: `codec.typeMapId`, `codec.typeDiscriminatorPath`, `codec.typeMappings`, `codec.inlineMappings`

### D4: STRUCTURED Format with Discriminator Mapping

**Status:** ✅ DONE (Spec Clarified)

Original issue assumed STRUCTURED format should emit WARNING when discriminator mapping is configured. **Actual behavior:** Discriminator mapping has priority over STRUCTURED format — when both are configured, the discriminator value is written inside the STRUCTURED `_type` object:

```json
{ "_type": { "type": "temp" }, "sensorId": "s-001" }
```

Spec `08-discriminator-mapping.md` section 1.4 now clarifies this interaction. Implementation handles this correctly in:
- `TypeSerializationEntry.java` lines 244-251 (serialization)
- `TypeDeserializationEntry.java` lines 505-540 (deserialization)

---

## 7. Plan E: Multi-Format Support

**Status:** IN PROGRESS (Started 2026-02-16)

**Goal:** Extend codec to support formats beyond JSON (BSON, CBOR, YAML, Lucene, etc.) while maintaining feature parity with the JSON implementation.

**Strategy:** Build FormatDelegate abstraction first, verify with existing JSON tests (~1000+), then add BSON as first non-Jackson format.

### Architecture: FormatDelegate Pattern

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           EMF Entry Layer                                   │
│  (TypeSerializationEntry, IdSerializationEntry, AttributeSerializationEntry,│
│   ReferenceSerializationEntry + deserialization counterparts)               │
│  Uses: JsonGenerator / JsonParser (standard Jackson API)                    │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
              ┌───────────────────────┴───────────────────────┐
              ▼                                               ▼
┌──────────────────────────────┐            ┌──────────────────────────────┐
│ Direct Jackson Path          │            │ FormatDelegate Bridge Path    │
│ (existing, unchanged)        │            │ (new, for non-JSON formats)  │
│                              │            │                              │
│ JsonGenerator / JsonParser   │            │ FormatDelegateGenerator<T>   │
│ from JsonFactory,            │            │ extends GeneratorBase        │
│ CBORFactory, etc.            │            │ wraps FormatDelegate<T>      │
│                              │            │                              │
│ Works with ANY Jackson       │            │ FormatDelegateParser<S>      │
│ format automatically!        │            │ extends ParserBase           │
└──────────────────────────────┘            │ wraps FormatReaderDelegate<S>│
                                            └──────────────┬───────────────┘
                                                           │
                                    ┌──────────────────────┼──────────────────────┐
                                    ▼                      ▼                      ▼
                          ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
                          │ JacksonStream    │  │ BsonFormat       │  │ LuceneFormat     │
                          │ FormatDelegate   │  │ Delegate         │  │ Delegate         │
                          │ <OutputStream>   │  │ <BsonDocument>   │  │ <Document>       │
                          │                  │  │                  │  │                  │
                          │ Wraps any Jackson│  │ Wraps BsonWriter │  │ Builds Lucene    │
                          │ generator        │  │ (in-memory)      │  │ Document         │
                          └──────────────────┘  └──────────────────┘  └──────────────────┘
```

**Key Insight:** All entry classes use standard `JsonGenerator`/`JsonParser` methods. The `FormatDelegateGenerator<T>` extends Jackson's `GeneratorBase` (which IS a `JsonGenerator`), so entry classes work unchanged with any format. Zero entry class modifications needed.

### Format I/O Types

| Format | Write Target (T) | Read Source (S) | Notes |
|--------|------------------|-----------------|-------|
| **JSON** | `OutputStream` | `InputStream` | Streaming bytes (Jackson direct path) |
| **CBOR/Smile/YAML** | `OutputStream` | `InputStream` | Jackson formats (direct or via delegate) |
| **MongoDB BSON** | `BsonDocument` | `BsonDocument` | In-memory object (FormatDelegate path) |
| **Lucene** | `Document` | `Document` | Lucene Document (FormatDelegate path) |

### Implementation Steps (Commit-Friendly)

Each step below is a self-contained unit that compiles, can be tested, and should be committed before moving to the next. Steps are numbered sequentially across all phases.

---

#### Step 1: FormatDelegate API interfaces ── 🔖 Commit Point
**Phase:** E1 | **Project:** `codec.api`

Create the core format abstraction interfaces. These are pure API with no implementation.

| Sub-step | File | Description |
|----------|------|-------------|
| 1a | `codec.api/.../format/TokenType.java` | Format-agnostic token enum (START_OBJECT, VALUE_STRING, etc.) |
| 1b | `codec.api/.../format/FormatDelegate.java` | Writer delegate interface with generic target type `<T>` |
| 1c | `codec.api/.../format/FormatReaderDelegate.java` | Reader delegate interface with generic source type `<S>` |
| 1d | `codec.api/.../format/CodecFormatProvider.java` | Factory interface for creating format delegates |

**Verify:** `./gradlew :org.eclipse.fennec.codec.api:test` passes (no new tests needed — pure interfaces)

---

#### Step 2: TokenTypeMapper utility ── 🔖 Commit Point
**Phase:** E2 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 2a | `codec/.../format/TokenTypeMapper.java` | Bidirectional mapping `TokenType` ↔ Jackson `JsonToken` |
| 2b | `codec/test/.../format/TokenTypeMapperTest.java` | Unit tests for all mappings |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 3: FormatDelegateGenerator (write bridge) ── 🔖 Commit Point
**Phase:** E2 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 3a | `codec/.../format/FormatDelegateGenerator.java` | Extends `GeneratorBase`, wraps `FormatDelegate<T>`, routes all `write*()` calls |
| 3b | `codec/test/.../format/FormatDelegateGeneratorTest.java` | Unit tests with a mock FormatDelegate |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 4: FormatDelegateParser (read bridge) ── 🔖 Commit Point
**Phase:** E2 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 4a | `codec/.../format/FormatDelegateParser.java` | Extends `ParserBase`, wraps `FormatReaderDelegate<S>`, installs `CodecReadContext` |
| 4b | `codec/test/.../format/FormatDelegateParserTest.java` | Unit tests with a mock FormatReaderDelegate |

**Key:** Installs existing `CodecReadContext` (from `codec.context`) as `_streamReadContext` for EMF context propagation.

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 5: JacksonStreamFormatDelegate (write impl) ── 🔖 Commit Point
**Phase:** E3 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 5a | `codec/.../format/impl/JacksonStreamFormatDelegate.java` | Wraps any Jackson `JsonGenerator` behind `FormatDelegate<OutputStream>` |
| 5b | `codec/test/.../format/impl/JacksonStreamFormatDelegateTest.java` | Unit tests: write through delegate, verify JSON output |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 6: JacksonStreamFormatReaderDelegate (read impl) ── 🔖 Commit Point
**Phase:** E3 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 6a | `codec/.../format/impl/JacksonStreamFormatReaderDelegate.java` | Wraps any Jackson `JsonParser` behind `FormatReaderDelegate<InputStream>` |
| 6b | `codec/test/.../format/impl/JacksonStreamFormatReaderDelegateTest.java` | Unit tests: read through delegate, verify parsed values |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 7: JacksonFormatProvider ── 🔖 Commit Point
**Phase:** E3 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 7a | `codec/.../format/impl/JacksonFormatProvider.java` | Takes `TokenStreamFactory`, creates writer/reader delegates |
| 7b | `codec/test/.../format/impl/JacksonFormatProviderTest.java` | Unit tests: create delegates, verify round-trip |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 8: Context fallback for non-JSON parsers ── 🔖 Commit Point
**Phase:** E4 | **Project:** `codec`

Add `Resource` as a `DeserializationContext` attribute so the deserializer works without `CodecJsonReadContext`.

| Sub-step | File | Description |
|----------|------|-------------|
| 8a | `codec/.../context/ContextHelper.java` | Add `RESOURCE` attribute key + `getResource(ctxt)` helper |
| 8b | `codec/.../deser/CodecEObjectDeserializer.java` | Add fallback: if `emfContext` is null, get resource from `ContextHelper.getResource(ctxt)` |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes (existing tests still work — backward compatible)

---

#### Step 9: CodecResource format provider support ── 🔖 Commit Point
**Phase:** E4 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 9a | `codec/.../resource/CodecResource.java` | Add optional `CodecFormatProvider` field + constructor overload |
| 9b | `codec/.../resource/CodecResource.java` | Add `doSaveWithFormat()` — creates FormatDelegateGenerator, uses ObjectMapper |
| 9c | `codec/.../resource/CodecResource.java` | Add `doLoadWithFormat()` — creates FormatDelegateParser, feeds to ObjectReader |
| 9d | `codec/.../resource/CodecResource.java` | Wire `doSave()`/`doLoad()` to delegate when format provider is set |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes (no format provider → existing path unchanged)

---

#### Step 10: CodecFormatResourceFactory ── 🔖 Commit Point
**Phase:** E4 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 10a | `codec/.../resource/CodecFormatResourceFactory.java` | Convenience factory: creates CodecResource with a CodecFormatProvider |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 11: JSON FormatDelegate integration test ── 🔖 Commit Point
**Phase:** E5 | **Project:** `codec`

Route existing codec operations through the full FormatDelegate → FormatDelegateGenerator → entry path.

| Sub-step | File | Description |
|----------|------|-------------|
| 11a | `codec/test/.../format/FormatDelegateJsonRoundTripTest.java` | End-to-end: serialize EObject via FormatDelegate path, deserialize back, compare |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 12: Feature parity test framework ── 🔖 Commit Point
**Phase:** E5 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 12a | `codec/test/.../format/AbstractFormatFeatureParityTest.java` | Abstract test suite: attributes, references, types, IDs, enums, round-trips |
| 12b | `codec/test/.../format/JsonFormatFeatureParityTest.java` | Concrete: runs parity suite via JSON FormatDelegate path |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes — JSON parity baseline established

---

#### Step 13: BSON project setup ── 🔖 Commit Point
**Phase:** E6 | **Project:** `codec.bson` (new)

| Sub-step | File | Description |
|----------|------|-------------|
| 13a | `org.eclipse.fennec.codec.bson/bnd.bnd` | OSGi bundle config, dependency on `org.mongodb:bson` |
| 13b | `org.eclipse.fennec.codec.bson/src/.../bson/package-info.java` | Package declaration |

**Verify:** `./gradlew build` compiles (empty project)

---

#### Step 14: BsonFormatDelegate (write) ── 🔖 Commit Point
**Phase:** E6 | **Project:** `codec.bson`

| Sub-step | File | Description |
|----------|------|-------------|
| 14a | `codec.bson/.../BsonFormatDelegate.java` | `FormatDelegate<BsonDocument>`, wraps `BsonDocumentWriter` |
| 14b | `codec.bson/test/.../BsonFormatDelegateTest.java` | Unit tests: write values, verify BsonDocument content |

Port from `old/org.eclipse.fennec.codec.mongo/src/.../MongoCodecGenerator.java`.

**Verify:** `./gradlew :org.eclipse.fennec.codec.bson:test` passes

---

#### Step 15: BsonFormatReaderDelegate (read) ── 🔖 Commit Point
**Phase:** E6 | **Project:** `codec.bson`

| Sub-step | File | Description |
|----------|------|-------------|
| 15a | `codec.bson/.../BsonFormatReaderDelegate.java` | `FormatReaderDelegate<BsonDocument>`, wraps `BsonDocumentReader` |
| 15b | `codec.bson/test/.../BsonFormatReaderDelegateTest.java` | Unit tests: read from BsonDocument, verify parsed values |

Port from `old/org.eclipse.fennec.codec.mongo/src/.../MongoCodecParser.java`.

**Verify:** `./gradlew :org.eclipse.fennec.codec.bson:test` passes

---

#### Step 16: BsonFormatProvider + round-trip tests ── 🔖 Commit Point
**Phase:** E6 | **Project:** `codec.bson`

| Sub-step | File | Description |
|----------|------|-------------|
| 16a | `codec.bson/.../BsonFormatProvider.java` | `CodecFormatProvider<BsonDocument, BsonDocument>` |
| 16b | `codec.bson/test/.../BsonRoundTripTest.java` | Serialize EObject → BsonDocument → deserialize back → compare |
| 16c | `codec.bson/test/.../BsonFormatFeatureParityTest.java` | Extends `AbstractFormatFeatureParityTest` for BSON |

**Verify:** `./gradlew :org.eclipse.fennec.codec.bson:test` passes — full BSON feature parity confirmed

---

#### Step 17+ (Future): Additional Jackson Formats

Each format is a single-commit project:
- `org.eclipse.fennec.codec.cbor` — `CborFormatProvider` + parity test
- `org.eclipse.fennec.codec.yaml` — `YamlFormatProvider` + parity test
- `org.eclipse.fennec.codec.smile` — `SmileFormatProvider` + parity test

### Key Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| **Implementation order** | Abstraction → JSON verify → BSON | Existing ~1000+ JSON tests prove abstraction works before adding new formats |
| **Entry class changes** | Zero | Entries use `JsonGenerator`/`JsonParser`; `FormatDelegateGenerator` IS a `JsonGenerator` |
| **EMF context for non-JSON parsers** | `DeserializationContext` attributes (existing fallback) + `CodecReadContext` | Deserializer already has fallback path when `CodecJsonReadContext` is not available |
| **FormatDelegate vs old approach** | FormatDelegate (POJOs) over old `CodecGeneratorBaseImpl` (Jackson subclass) | Cleaner, supports non-stream targets, no Jackson internal dependency |
| **Project structure** | Separate Gradle project per format | Clean OSGi bundles, optional dependencies |

### Old Codec Reference

The old codec projects in `old/` folder serve as reference implementations:

| Old Project | Reference For |
|-------------|---------------|
| `old/org.eclipse.fennec.codec/` | `CodecGeneratorBaseImpl`, `CodecParserBaseImpl` pattern |
| `old/org.eclipse.fennec.codec.mongo/` | `MongoCodecGenerator`, `MongoCodecParser` → port to `BsonFormatDelegate` |
| `old/org.eclipse.fennec.codec.csv/` | `CodecCSVParser` → future `CsvFormatReaderDelegate` |
| `old/org.eclipse.fennec.codec.ecowitt/` | Custom protocol parser → future reference |

---

## 8. Execution Roadmap

### Recommended Order

```
Plan A (Deprecated API Migration) ✅ COMPLETE

Plan B Phase 1 (Core Metadata Gaps):
  GAP-001: Feature Visibility (directional ignore/force)
  GAP-002: Fallback Strategy wiring
  GAP-003: Feature Strictness attributes
  GAP-004: Diagnostic Options
  GAP-005: ID Value Key
  GAP-014: inherit enum

Plan B Phase 2 (Advanced Features):
  GAP-008: Value Reader/Writer handlers
  GAP-009: Scope wiring for runtime options
  GAP-007: Expand deserialization
  GAP-006: Metadata merge behavior
  GAP-010: Hierarchy resolution tests

Plan B Phase 3 (Polish):
  GAP-011, GAP-012, GAP-013

Plan C (Documentation Examples) — can be done in parallel:
  DOC-001 through DOC-004

Plan D (Discriminator Refactoring) — ✅ VERIFIED COMPLETE (2026-02-08):
  D1: Remove MAPPED from TypeStrategy — DONE (spec confirms removal)
  D2: Inline mapping for references — DONE (fully implemented + tested)
  D3: Property-based discriminator config — DONE (documented in spec)
  D4: STRUCTURED format with discriminator — DONE (spec clarified, implementation correct)

Plan E (Multi-Format Support) — IN PROGRESS (started 2026-02-16):
  E1: FormatDelegate interfaces (TokenType, FormatDelegate<T>, FormatReaderDelegate<S>, CodecFormatProvider)
  E2: Jackson Bridge (FormatDelegateGenerator extends GeneratorBase, FormatDelegateParser extends ParserBase)
  E3: JSON/Jackson FormatDelegate impl (JacksonStreamFormatDelegate wraps any Jackson generator)
  E4: Refactor CodecResource (add CodecFormatProvider support, Resource fallback in deserializer)
  E5: Verify with existing JSON tests (~1000+ tests through FormatDelegate layer)
  E6: BSON format (BsonFormatDelegate<BsonDocument>, port from old MongoCodecGenerator/Parser)
  E7: Additional Jackson formats (CBOR, YAML, Smile — trivial once E3 works)
```

**Notes:**
- Plan A is complete: deprecated code fully deleted, codebase clean
- GAP-002, GAP-003, GAP-014 are migration tasks (existed in old code)
- GAP-004 is a new feature (never existed in old code) — lower priority
- Helper classes extracted (TypeResolutionHelper, EMapHelper) improving testability
- GAP-008 (value handlers) can proceed now that Plan A is done

### Property Matrix (for Plan B reference)

#### Type Properties (Spec 07§2)
| Property | Levels | Direction | Default |
|----------|--------|-----------|---------|
| typeStrategy | G, C, F | both | NAME |
| typeKey | G, C, F | both | _type |
| typeInclude | G, C, F | both | WRAPPER_OBJECT |
| typeSchemaKey | G, C, F | both | _schema |
| typeNameKey | G, C, F | both | _name |

#### ID Properties (Spec 06)
| Property | Levels | Direction | Default |
|----------|--------|-----------|---------|
| idStrategy | G, C | both | ID_FIELD |
| idKey | G, C | both | _id |
| idKeyMode | G, C | both | ID_ONLY |
| idFormat | G, C | both | PLAIN |
| idOnTop | G, C | write | true |
| idValueKey | G, C | both | value |
| idFeatures | C | both | [] |

#### Feature Properties (Spec 07§3)
| Property | Levels | Direction | Default |
|----------|--------|-----------|---------|
| key | F | both | (feature name) |
| visibility | G, C, F | read/write separate | NONE |
| serializeNull | G, C, F | write | false |
| serializeEmpty | G, C, F | write | false |
| serializeDefault | G, C, F | write | false |

#### Reference Properties (Spec 08)
| Property | Levels | Direction | Default |
|----------|--------|-----------|---------|
| expand | G, C, F | both | false |
| expandDepth | G, C, F | both | 1 |
| discriminatorPath | F | read | null |
| discriminatorValue | F | read | null |
| fallbackStrategy | G, C, F | read | FALLBACK |
| fallbackEClass | F | read | null |

#### Strictness Properties (Spec 07§6)
| Property | Levels | Direction | Default |
|----------|--------|-----------|---------|
| strictOnUnknown | G, C | read | false |
| strictOnMissing | G, C | read | false |

*Legend: G=Global, C=EClass, F=Feature*

---

## 9. Critical Files Reference

### Plan A: Files to Modify

**New files to create (codec.api project):**
- `src/org/eclipse/fennec/codec/value/DefaultCodecReaderContext.java`
- `src/org/eclipse/fennec/codec/value/DefaultCodecWriterContext.java`
- `src/org/eclipse/fennec/codec/value/SimpleEffectiveCodecConfig.java`
- `test/org/eclipse/fennec/codec/value/DefaultCodecReaderContextTest.java`
- `test/org/eclipse/fennec/codec/value/DefaultCodecWriterContextTest.java`

**SRC files to modify (codec project, `codec.*` package):**
- `config/effective/EffectiveCodecConfig.java`
- `module/CodecModule.java`
- `resource/CodecResource.java`
- `ser/AttributeSerializationEntry.java`
- `ser/ReferenceSerializationEntry.java`
- `deser/AttributeDeserializationEntry.java`
- `deser/ReferenceDeserializationEntry.java`
- `ser/CodecEObjectSerializer.java` (orchestrator — entry creation)

**TEST files to modify (codec project, `codec.*` package):**
- `deser/AttributeDeserializationEntryCanHandleTest.java`
- `deser/ReferenceDeserializationEntryCanHandleTest.java`
- `deser/ReferenceDeserializationEntryCustomReaderTest.java`
- `ser/AttributeSerializationEntryCanHandleTest.java`
- `ser/ReferenceSerializationEntryCanHandleTest.java`
- `ser/ReferenceSerializationEntryCustomWriterTest.java`
- `module/CodecModuleBuilderTest.java`
- `resource/CodecResourceCustomValueTest.java`

### Plan B: Files to Modify

**Ecore model:**
- `org.eclipse.fennec.codec.metadata/model/codec.ecore` (add enums, attributes)

**Aspect provider:**
- `org.eclipse.fennec.codec.metadata/src/*/CodecAspectProvider.java`

**Tests:**
- `org.eclipse.fennec.codec.metadata/test/*/CodecAspectProviderValidConfigTest.java`
- `org.eclipse.fennec.codec.metadata/test/*/CodecAspectProviderMisconfigTest.java`
- `org.eclipse.fennec.codec.metadata/test/*/test-codec-annotations.ecore`

### Testing Commands

```bash
# Codec V2 tests (JUnit 5, NOT OSGi)
./gradlew :org.eclipse.fennec.codec:cleanTest :org.eclipse.fennec.codec:test

# Metadata tests
./gradlew :org.eclipse.fennec.codec.metadata:test

# Full build
./gradlew build
```
