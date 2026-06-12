# Codec V2 Remaining Plans

This document consolidates all active plans for completing the codec migration. It merges the
Spec Compliance Refactoring Plan and the Deprecated API Migration Plan into a single phased roadmap.

**Created:** 2026-02-02
**Updated:** 2026-06-12

**Related documents:**
- [`docs/codec-v2-development-guide.md`](codec-v2-development-guide.md) — Session continuity, current state
- [`docs/codec-v2-spec/`](codec-v2-spec/) — Specification (source of truth)

---

## Table of Contents

1. [Current State](#1-current-state)
2. [Architecture Overview](#2-architecture-overview)
3. [Plan B: Spec Compliance Gaps](#3-plan-b-spec-compliance-gaps)
4. [Plan C: Documentation Examples](#4-plan-c-documentation-examples-deferred)
5. [Plan E: Multi-Format Support](#5-plan-e-multi-format-support)
6. [Plan F: TCK Test Suite](#6-plan-f-tck-test-suite)
7. [Plan G: RDF Format Support (Jena)](#7-plan-g-rdf-format-support-jena)
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
- Total: 2519 tests, 0 failures, 0 skipped across 7 projects (now 3,016 across 9 projects after Plan E+F)

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
| **Plan B** | Fill spec compliance gaps (12/14 GAPs done, 2 postponed) | ✅ MOSTLY COMPLETE |
| **Plan C** | Documentation examples (deferred from spec review) | Not Started |
| ~~**Plan E**~~ | ~~Multi-format support (BSON, CBOR, YAML)~~ | ✅ COMPLETE |
| ~~**Plan F**~~ | ~~TCK test suite (3 phases, 18 abstract TCKs)~~ | ✅ COMPLETE |
| **Plan G** | RDF format support (RDF/XML, Turtle, JSON-LD) via Apache Jena | Not Started |

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

## 3. Plan B: Spec Compliance Gaps

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

## 4. Plan C: Documentation Examples (Deferred)

These documentation tasks were identified during spec review but deferred as lower priority.

| ID | Task | Spec Section | Priority |
|----|------|--------------|----------|
| DOC-001 | SuperType load/save options examples | `07-supertype.md` §6 | MEDIUM |
| DOC-002 | Reference expand examples (annotation + options + side-by-side output) | `10-reference.md` §4.2 | HIGH |
| DOC-003 | Custom value readers/writers registration examples | `14-custom-values.md` §3-4 | MEDIUM |
| DOC-004 | NUMERIC type strategy round-trip example | `06-type.md` §1.6 | MEDIUM |

---

## 5. Plan E: Multi-Format Support

**Status:** ✅ COMPLETE (2026-02-16)

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

#### Step 1: FormatDelegate API interfaces ── ✅ Done
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

#### Step 2: TokenTypeMapper utility ── ✅ Done
**Phase:** E2 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 2a | `codec/.../format/TokenTypeMapper.java` | Bidirectional mapping `TokenType` ↔ Jackson `JsonToken` |
| 2b | `codec/test/.../format/TokenTypeMapperTest.java` | Unit tests for all mappings |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 3: FormatDelegateGenerator (write bridge) ── ✅ Done
**Phase:** E2 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 3a | `codec/.../format/FormatDelegateGenerator.java` | Extends `GeneratorBase`, wraps `FormatDelegate<T>`, routes all `write*()` calls |
| 3b | `codec/test/.../format/FormatDelegateGeneratorTest.java` | Unit tests with a mock FormatDelegate |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 4: FormatDelegateParser (read bridge) ── ✅ Done
**Phase:** E2 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 4a | `codec/.../format/FormatDelegateParser.java` | Extends `ParserBase`, wraps `FormatReaderDelegate<S>`, installs `CodecReadContext` |
| 4b | `codec/test/.../format/FormatDelegateParserTest.java` | Unit tests with a mock FormatReaderDelegate |

**Key:** Installs existing `CodecReadContext` (from `codec.context`) as `_streamReadContext` for EMF context propagation.

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 5: JacksonStreamFormatDelegate (write impl) ── ✅ Done
**Phase:** E3 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 5a | `codec/.../format/impl/JacksonStreamFormatDelegate.java` | Wraps any Jackson `JsonGenerator` behind `FormatDelegate<OutputStream>` |
| 5b | `codec/test/.../format/impl/JacksonStreamFormatDelegateTest.java` | Unit tests: write through delegate, verify JSON output |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 6: JacksonStreamFormatReaderDelegate (read impl) ── ✅ Done
**Phase:** E3 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 6a | `codec/.../format/impl/JacksonStreamFormatReaderDelegate.java` | Wraps any Jackson `JsonParser` behind `FormatReaderDelegate<InputStream>` |
| 6b | `codec/test/.../format/impl/JacksonStreamFormatReaderDelegateTest.java` | Unit tests: read through delegate, verify parsed values |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 7: JacksonFormatProvider ── ✅ Done
**Phase:** E3 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 7a | `codec/.../format/impl/JacksonFormatProvider.java` | Takes `TokenStreamFactory`, creates writer/reader delegates |
| 7b | `codec/test/.../format/impl/JacksonFormatProviderTest.java` | Unit tests: create delegates, verify round-trip |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 8: Context fallback for non-JSON parsers ── ✅ Done
**Phase:** E4 | **Project:** `codec`

Add `Resource` as a `DeserializationContext` attribute so the deserializer works without `CodecJsonReadContext`.

| Sub-step | File | Description |
|----------|------|-------------|
| 8a | `codec/.../context/ContextHelper.java` | Add `RESOURCE` attribute key + `getResource(ctxt)` helper |
| 8b | `codec/.../deser/CodecEObjectDeserializer.java` | Add fallback: if `emfContext` is null, get resource from `ContextHelper.getResource(ctxt)` |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes (existing tests still work — backward compatible)

---

#### Step 9: CodecResource format provider support ── ✅ Done
**Phase:** E4 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 9a | `codec/.../resource/CodecResource.java` | Add optional `CodecFormatProvider` field + constructor overload |
| 9b | `codec/.../resource/CodecResource.java` | Add `doSaveWithFormat()` — creates FormatDelegateGenerator, uses ObjectMapper |
| 9c | `codec/.../resource/CodecResource.java` | Add `doLoadWithFormat()` — creates FormatDelegateParser, feeds to ObjectReader |
| 9d | `codec/.../resource/CodecResource.java` | Wire `doSave()`/`doLoad()` to delegate when format provider is set |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes (no format provider → existing path unchanged)

---

#### Step 10: CodecFormatResourceFactory ── ✅ Done
**Phase:** E4 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 10a | `codec/.../resource/CodecFormatResourceFactory.java` | Convenience factory: creates CodecResource with a CodecFormatProvider |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 11: JSON FormatDelegate integration test ── ✅ Done
**Phase:** E5 | **Project:** `codec`

Route existing codec operations through the full FormatDelegate → FormatDelegateGenerator → entry path.

| Sub-step | File | Description |
|----------|------|-------------|
| 11a | `codec/test/.../format/FormatDelegateJsonRoundTripTest.java` | End-to-end: serialize EObject via FormatDelegate path, deserialize back, compare |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes

---

#### Step 12: Feature parity test framework ── ✅ Done
**Phase:** E5 | **Project:** `codec`

| Sub-step | File | Description |
|----------|------|-------------|
| 12a | `codec/test/.../format/AbstractFormatFeatureParityTest.java` | Abstract test suite: attributes, references, types, IDs, enums, round-trips |
| 12b | `codec/test/.../format/JsonFormatFeatureParityTest.java` | Concrete: runs parity suite via JSON FormatDelegate path |

**Verify:** `./gradlew :org.eclipse.fennec.codec:test` passes — JSON parity baseline established

---

#### Step 13: BSON project setup ── ✅ Done
**Phase:** E6 | **Project:** `codec.bson` (new)

| Sub-step | File | Description |
|----------|------|-------------|
| 13a | `org.eclipse.fennec.codec.bson/bnd.bnd` | OSGi bundle config, dependency on `org.mongodb:bson` |
| 13b | `org.eclipse.fennec.codec.bson/src/.../bson/package-info.java` | Package declaration |

**Verify:** `./gradlew build` compiles (empty project)

---

#### Step 14: BsonFormatDelegate (write) ── ✅ Done
**Phase:** E6 | **Project:** `codec.bson`

| Sub-step | File | Description |
|----------|------|-------------|
| 14a | `codec.bson/.../BsonFormatDelegate.java` | `FormatDelegate<BsonDocument>`, wraps `BsonDocumentWriter` |
| 14b | `codec.bson/test/.../BsonFormatDelegateTest.java` | Unit tests: write values, verify BsonDocument content |

Ported from the original `MongoCodecGenerator.java` (see [pre-rework branch](https://github.com/geckoprojects-org/org.gecko.codec/tree/snapshot)).

**Verify:** `./gradlew :org.eclipse.fennec.codec.bson:test` passes

---

#### Step 15: BsonFormatReaderDelegate (read) ── ✅ Done
**Phase:** E6 | **Project:** `codec.bson`

| Sub-step | File | Description |
|----------|------|-------------|
| 15a | `codec.bson/.../BsonFormatReaderDelegate.java` | `FormatReaderDelegate<BsonDocument>`, wraps `BsonDocumentReader` |
| 15b | `codec.bson/test/.../BsonFormatReaderDelegateTest.java` | Unit tests: read from BsonDocument, verify parsed values |

Ported from the original `MongoCodecParser.java` (see [pre-rework branch](https://github.com/geckoprojects-org/org.gecko.codec/tree/snapshot)).

**Verify:** `./gradlew :org.eclipse.fennec.codec.bson:test` passes

---

#### Step 16: BsonFormatProvider + round-trip tests ── ✅ Done
**Phase:** E6 | **Project:** `codec.bson`

| Sub-step | File | Description |
|----------|------|-------------|
| 16a | `codec.bson/.../BsonFormatProvider.java` | `CodecFormatProvider<BsonDocument, BsonDocument>` |
| 16b | `codec.bson/test/.../BsonRoundTripTest.java` | Serialize EObject → BsonDocument → deserialize back → compare |
| 16c | `codec.bson/test/.../BsonFormatFeatureParityTest.java` | Extends `AbstractFormatFeatureParityTest` for BSON |

**Verify:** `./gradlew :org.eclipse.fennec.codec.bson:test` passes — full BSON feature parity confirmed

---

#### Step 17: Additional Jackson Formats ── ✅ Done

- ✅ `org.eclipse.fennec.codec.cbor` — `CborFormatProvider` extends `JacksonFormatProvider` (15 tests)
- ✅ `org.eclipse.fennec.codec.yaml` — `YamlFormatProvider` extends `JacksonFormatProvider` (15 tests)
  - Requires `org.snakeyaml.engine` as transitive dependency
- `org.eclipse.fennec.codec.smile` — not yet implemented (add when needed)

### Key Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| **Implementation order** | Abstraction → JSON verify → BSON | Existing ~1000+ JSON tests prove abstraction works before adding new formats |
| **Entry class changes** | Zero | Entries use `JsonGenerator`/`JsonParser`; `FormatDelegateGenerator` IS a `JsonGenerator` |
| **EMF context for non-JSON parsers** | `DeserializationContext` attributes (existing fallback) + `CodecReadContext` | Deserializer already has fallback path when `CodecJsonReadContext` is not available |
| **FormatDelegate vs old approach** | FormatDelegate (POJOs) over old `CodecGeneratorBaseImpl` (Jackson subclass) | Cleaner, supports non-stream targets, no Jackson internal dependency |
| **Project structure** | Separate Gradle project per format | Clean OSGi bundles, optional dependencies |

### Old Codec Reference

The original V1 codec implementations are available on GitHub for reference:

- **Pre-rework (original code):** https://github.com/geckoprojects-org/org.gecko.codec/tree/snapshot
- **Before this rework:** https://github.com/geckoprojects-org/org.gecko.codec/tree/issue%2348

---

## 6. Plan F: TCK Test Suite

**Status:** ✅ COMPLETE (2026-02-17)

**Goal:** Comprehensive Technology Compatibility Kit (TCK) ensuring all format providers (BSON, CBOR, YAML) pass identical feature tests. Abstract test classes define format-agnostic logic; each format provides trivial concrete subclasses.

### Architecture

```
org.eclipse.fennec.codec.tests/src/.../tck/
├── test-tck-*.ecore           (8 ecore models)
├── Abstract*TCK.java          (18 abstract test classes)
│
org.eclipse.fennec.codec.bson/test/.../bson/
├── Bson*TCKTest.java          (18 concrete subclasses)
│
org.eclipse.fennec.codec.cbor/test/.../cbor/
├── Cbor*TCKTest.java          (18 concrete subclasses)
│
org.eclipse.fennec.codec.yaml/test/.../yaml/
├── Yaml*TCKTest.java          (18 concrete subclasses)
```

### TCK Phases

#### P0: Core Round-Trip (6 suites)

| Suite | Tests | Ecore Model |
|-------|------:|-------------|
| Attribute types | 7 | test-tck.ecore |
| Containment references | 2 | test-tck.ecore |
| Non-containment references | 2 | test-tck.ecore |
| Enum attributes | 2 | test-tck.ecore |
| Multi-valued attributes | 2 | test-tck.ecore |
| Complex round-trip | 1 | test-tck.ecore |

#### P1: Feature Strategies (7 suites)

| Suite | Tests | Ecore Model |
|-------|------:|-------------|
| Type Strategy | 2 | test-tck-type.ecore |
| ID Strategy | 4 | test-tck-id.ecore |
| Enum Strategy | 2 | test-tck-enum.ecore |
| Polymorphism | 2 | test-tck-polymorphism.ecore |
| Reference Format | 2 | test-tck-reference.ecore |
| Value Handling | 3 | test-tck-valuehandling.ecore |
| Custom Key | 1 | test-tck-customkey.ecore |

#### P2: Advanced Features (10 suites)

| Suite | Tests | Ecore Model |
|-------|------:|-------------|
| EMap | 2 | test-tck-emap.ecore |
| SuperType | 2 | test-tck-supertype.ecore |
| Visibility | 3 | test-tck-visibility.ecore |
| Force Read/Write | 2 | test-tck-force.ecore |
| Global Ignore | 2 | test-tck-visibility.ecore (shared) |
| Strictness | 2 | test-tck-strictness.ecore |
| Array Root | 2 | test-tck-arrayroot.ecore |
| Large Payload (1000 objects) | 1 | test-tck-arrayroot.ecore (shared) |
| Extended MetaData | 1 | test-tck-extmetadata.ecore |
| Custom Value Reader/Writer | 1 | test-tck-customvalue.ecore |

### Bug Fixes During TCK Development

1. **Array root serialization** — `CodecResource.doSaveWithFormat()` now iterates objects individually within array start/end instead of passing `EObject[]`
2. **`CodecFormatProvider.supportsArrayRoot()`** — New default method (returns `true`); `BsonFormatProvider` overrides to `false` and throws `IOException` on multi-root
3. **BSON reader `valueConsumed` fix** — `BsonFormatReaderDelegate` tracks whether primitive values were consumed; `nextToken()` calls `skipValue()` for unconsumed values. 11 regression tests in `BsonFormatReaderDelegateTest$UnconsumedValues`.

### Concrete Subclass Pattern

Each format subclass is trivial (same pattern for all 18 × 3 = 54 classes):

```java
public class BsonEMapTCKTest extends AbstractEMapTCK {
    @Override
    protected CodecFormatProvider<?, ?> createFormatProvider() {
        return new BsonFormatProvider();
    }
    @Override
    protected String getFileExtension() {
        return "bson";
    }
}
```

---

## 7. Plan G: RDF Format Support (Jena)

**Status:** Not Started (planned 2026-06-12)

**Goal:** Support RDF serialization formats — RDF/XML, Turtle, JSON-LD (plus N-Triples nearly for free) — in the codec via Apache Jena. One implementation builds/reads an in-memory Jena `Model`; the concrete syntax is selected at the I/O boundary via Jena's `RDFDataMgr`/`Lang`.

**Background:** A working PoC exists at `n:\git\civitas\civitas-core-development\PoCs\dcatemf\` (`org.eclipse.fennec.emf.jena.EMFJenaMapper` + `JenaResourceImpl`/`JenaResourceFactoryImpl`). It demonstrates a cycle-aware EObject→Jena-Model traversal with a solid Java→XSD datatype mapping and a `PlainLiteral` convention for language-tagged literals. The Spring parts of the PoC are HTTP transport glue and are not relevant here.

**What carries over from the PoC:** traversal skeleton with cycle detection, XSD datatype table, namespace prefix registration from `EPackage.nsPrefix/nsURI`, `PlainLiteral` → langString convention, the Lang/content-type registration list.

**What does NOT carry over:**
- The PoC ignores all codec configuration (ID/type strategies, feature keys, serialize gates, value writers) — every decision point must be routed through `ConfigurationResolver`.
- Predicate IRIs come from `EcoreUtil.getURI(feature)` (e.g. `http://www.w3.org/ns/dcat#//Catalog/title`) — EMF-internal, not interoperable vocabulary IRIs like `dct:title`. Needs annotation-driven predicate mapping.
- **Known PoC bug:** `model.createResource(nsPrefix + ":" + className)` — Jena does not expand prefixes in `createResource()`, so the rdf:type IRI is the literal string `"dcat:Catalog"`. Must always use full IRIs.
- No RDF→EObject direction exists at all.

### Architecture

RDF is graph-based, not a token stream. Like the CSV provider's `SQL_TABLES` mode, the RDF delegates **bypass the Jackson token pipeline** and walk the EMF graph directly, applying codec options themselves via the resolver-aware overload `CodecFormatProvider.createWriter(target, rootObjects, saveOptions, resolver)`.

```
WRITE:  EObject graph ──RdfModelBuilder──▶ Jena Model ──RDFDataMgr.write(Lang)──▶ OutputStream
                          (resolver-aware                  (syntax chosen by ext /
                           custom traversal)                content type / option)

READ:   InputStream ──RDFDataMgr.read(Lang)──▶ Jena Model ──RdfModelReader──▶ EObject graph
                                                              (rdf:type IRI → EClass,
                                                               2-pass reference resolution)
```

| Component | Role |
|-----------|------|
| `RdfFormatProvider` | `CodecFormatProvider<InputStream, OutputStream>`; one provider, all RDF syntaxes; `supportsArrayRoot()` = true (multiple roots = multiple typed resources in one graph) |
| `RdfWriterDelegate` | `FormatDelegate<OutputStream>`; ignores token methods, exposes the custom traversal; `flush()` writes the Model |
| `RdfModelBuilder` | The actual traversal: EObject graph → Jena Model (port of PoC mapper, config-driven) |
| `RdfReaderDelegate` / `RdfModelReader` | Jena Model → EObject graph |
| `RdfResourceFactoryComponent` | OSGi DS `Resource.Factory` for extensions `ttl`, `jsonld`, `rdf`, `nt` and content types `text/turtle`, `application/ld+json`, `application/rdf+xml`, `application/n-triples` |

### Key Design Decisions (to be fixed in spec chapter `21-rdf.md`)

| Decision | Proposed Choice | Rationale |
|----------|-----------------|-----------|
| **Syntax selection** | File extension / content type, overridable by save/load option `codec.rdf.lang` | Mirrors how Jena `Lang` works; one bundle covers all syntaxes |
| **Instance IRI generation** | Resolved ID (existing `idStrategy`/`idFeatures`) + new `rdfBaseIri` prefix; blank node if no ID resolvable | Reuses codec ID machinery; blank nodes are the natural RDF fallback |
| **rdf:type IRI** | Default `EPackage.nsURI` + `#`/`/` + `EClass.name` (full IRI, never prefixed string); overridable per class via `rdfTypeIri` | Fixes PoC bug; allows mapping to standard vocabularies (`dcat:Catalog`) |
| **Predicate IRI** | Default: package nsURI + feature key (resolved `key` from FeatureConfig); overridable per feature via `rdfPredicateIri` | Interop with real vocabularies (`dct:title`) requires explicit mapping |
| **Containment** | Plain triples; contained objects without ID become blank nodes | Simple, valid RDF; named graphs deferred (see Non-Goals) |
| **Non-containment refs** | Object property to the target's IRI; target's triples only emitted if in same Resource (otherwise IRI-only, like PLAIN format) | Matches codec PLAIN/STRUCTURED semantics in RDF terms |
| **Language-tagged literals** | Support the `PlainLiteral` EClass convention (value+lang) detected structurally via new `rdfLangString` annotation; plus per-attribute fixed `rdfLang` | PoC convention proven with DCAT-AP; annotation makes it model-driven instead of name-based |
| **Datatypes** | Port PoC Java→XSD table; honor existing `valueWriter`/`dateFormat` config before falling back to typed literals | Consistency with other formats |
| **Type resolution on read** | rdf:type IRI → EClass via registered EPackages + `rdfTypeIri` reverse map; honor `fallbackStrategy` (ERROR/SKIP/FALLBACK) for unknown types | Reuses discriminator fallback semantics |
| **Jackson** | Not used at all for RDF | RDF is a graph; Jena's Model API is the right abstraction |

**Non-Goals (this plan):** named-graph/TriG support, SPARQL endpoints, OWL/RDFS schema generation from Ecore (an `EPackage → OWL ontology` converter à la jsonschema is a possible future Plan), streaming for very large graphs (Model is built in memory).

### New Annotation Keys (annotation source `http://eclipse.org/fennec/codec`)

| Key | Scope | Default | Meaning |
|-----|-------|---------|---------|
| `rdfBaseIri` | EPackage, EClass | (none) | Base IRI prefix for instance IRIs (`baseIri` + resolved ID) |
| `rdfTypeIri` | EClass | nsURI-derived | Full IRI for rdf:type triple |
| `rdfPredicateIri` | EAttribute, EReference | nsURI + key | Full predicate IRI for the feature |
| `rdfLangString` | EClass | false | Marks a value+lang EClass to be emitted as a language-tagged literal |
| `rdfLang` | EAttribute | (none) | Fixed language tag for a string attribute |

Load/save options: `codec.rdf.lang` (TURTLE / JSONLD / RDFXML / NTRIPLES), `codec.rdf.baseIri`, `codec.rdf.prefixes` (Map<String,String> extra prefix registrations).

### Risks / Open Questions

1. **Jena OSGi readiness** — Jena 4.x/5.x jars are not OSGi bundles; `jena-osgi` is unmaintained. Likely needs bnd-wrapping/repackaging (same approach as the michaods repackaging, commit `70ba8e3`). **Resolve in Step G1 before anything else.**
2. **Jena dependency weight** — `jena-arq` pulls a large dependency tree; check what the minimal set for parse/write is (`jena-core` + `jena-arq` is expected).
3. **JSON-LD flavor** — Jena 4.x: JSON-LD 1.1 via titanium-json-ld. Decide whether `@context` shaping (framing) is in scope or plain expanded/compacted output suffices for v1. Proposal: plain output for v1, framing deferred.
4. **Read-path ID extraction** — when instance IRIs are `baseIri + id`, reading must strip the base to recover the ID feature value. Needs a documented, reversible rule.
5. **Unordered graphs** — RDF has no feature/field order; many-valued features have no guaranteed order after round-trip (RDF multi-valued properties are sets unless rdf:List is used). Decide: document as known limitation vs. `rdf:List`/`rdf:Seq` opt-in. Proposal: document limitation for v1.

### Implementation Steps (Commit-Friendly)

#### Step G0: Spec chapter ── Not Started
**Project:** `docs`

| Sub-step | File | Description |
|----------|------|-------------|
| G0a | `docs/codec-v2-spec/21-rdf.md` | Spec chapter: mapping rules (instance IRI, type IRI, predicate IRI, literals, references, blank nodes), annotation keys, load/save options, read-path resolution, limitations |
| G0b | `docs/codec-v2-spec/16-annotation-reference.md` | Register the new `rdf*` annotation keys |

**Verify:** Spec review with user — design decisions above confirmed/amended before code.

---

#### Step G1: Dependencies + bundle skeleton ── Not Started
**Project:** `cnf`, `org.eclipse.fennec.codec.rdf` (new)

| Sub-step | File | Description |
|----------|------|-------------|
| G1a | `cnf/central.mvn` | Add Apache Jena (`jena-core`, `jena-arq` + transitive minimum) |
| G1b | (investigation) | Verify OSGi header situation; if jars are not bundles, repackage/wrap (michaods pattern) |
| G1c | `org.eclipse.fennec.codec.rdf/bnd.bnd` + `package-info.java` | New bundle, depends on codec.api, codec, Jena |
| G1d | `org.eclipse.fennec.codec.rdf/test/.../JenaSmokeTest.java` | Smoke test: build a tiny Model, write Turtle + JSON-LD + RDF/XML, parse back — proves the dependency works in the build |

**Verify:** `./gradlew :org.eclipse.fennec.codec.rdf:test` passes

---

#### Step G2: Metadata extension ── Not Started
**Project:** `codec.metadata`, `codec.api`

| Sub-step | File | Description |
|----------|------|-------------|
| G2a | `codec.metadata/model/codec.ecore` | Add `RdfSerializationConfig` fields to aspects (baseIri, typeIri, predicateIri, langString, lang) + regenerate |
| G2b | `codec.metadata/.../CodecAspectProvider.java` | Parse `rdf*` keys; validation (e.g. `rdfPredicateIri` must be absolute IRI, `rdfTypeIri` only on EClass) |
| G2c | `codec.api/.../config/RdfConfig.java` | Config record + merge semantics, exposed via `ConfigurationResolver` |
| G2d | tests | `RdfConfigSpecTest`, `RdfConfigResolverSpecTest`, misconfig cases in `CodecAspectProviderMisconfigTest` (follow existing `*ConfigSpecTest` pattern) |

**Verify:** `./gradlew :org.eclipse.fennec.codec.metadata:test :org.eclipse.fennec.codec.api:test`

---

#### Step G3: Writer ── Not Started
**Project:** `codec.rdf`

| Sub-step | File | Description |
|----------|------|-------------|
| G3a | `codec.rdf/.../RdfModelBuilder.java` | EObject graph → Jena Model. Port PoC traversal (cycle detection, datatype table, prefix registration); route IRIs/gates/values through `ConfigurationResolver`; fix prefix-expansion bug; blank nodes for ID-less objects |
| G3b | `codec.rdf/.../RdfWriterDelegate.java` | `FormatDelegate<OutputStream>`; holds roots+options+resolver; `flush()` → `RDFDataMgr.write(out, model, lang)` |
| G3c | `codec.rdf/test/.../RdfModelBuilderTest.java` | Unit tests against the Model (not text): type triples, literals, langStrings, references, cycles, blank nodes |

**Verify:** `./gradlew :org.eclipse.fennec.codec.rdf:test`

---

#### Step G4: Format provider + resource factory ── Not Started
**Project:** `codec.rdf`

| Sub-step | File | Description |
|----------|------|-------------|
| G4a | `codec.rdf/.../RdfFormatProvider.java` | Lang resolution (URI ext → content type → `codec.rdf.lang` option), `validateSaveOptions()` for ext/lang mismatch warnings |
| G4b | `codec.rdf/.../RdfResourceFactoryComponent.java` | OSGi DS component for `ttl`/`jsonld`/`rdf`/`nt` + content types |
| G4c | `codec.rdf/test/.../RdfFormatProviderTest.java` | Lang selection matrix, save-option validation |

**Verify:** `./gradlew :org.eclipse.fennec.codec.rdf:test` — end-to-end save of a CodecResource as Turtle/JSON-LD/RDF/XML works

---

#### Step G5: Reader ── Not Started
**Project:** `codec.rdf`

| Sub-step | File | Description |
|----------|------|-------------|
| G5a | `codec.rdf/.../RdfModelReader.java` | Pass 1: typed resources → EClass resolution (`rdfTypeIri` reverse map, nsURI heuristic, `fallbackStrategy`), create EObjects, set attributes (XSD→Java, langString reassembly). Pass 2: resolve object properties to containment/cross references; IRI → ID extraction |
| G5b | `codec.rdf/.../RdfReaderDelegate.java` | `FormatReaderDelegate<InputStream>`: `RDFDataMgr.read` + `RdfModelReader`, populate Resource contents |
| G5c | `codec.rdf/test/.../RdfModelReaderTest.java` | Unit tests: type resolution incl. fallback ERROR/SKIP/FALLBACK, blank node containment, dangling IRI refs (proxy vs diagnostic) |

**Verify:** `./gradlew :org.eclipse.fennec.codec.rdf:test`

---

#### Step G6: Round-trip + TCK integration ── Not Started
**Project:** `codec.rdf`, `codec.tests`

See [Test Strategy](#test-strategy-plan-g) below for full detail.

| Sub-step | File | Description |
|----------|------|-------------|
| G6a | `codec.rdf/test/.../RdfRoundTripTest.java` | Parameterized over TURTLE/JSONLD/RDFXML/NTRIPLES: save → load → `EcoreUtil.equals` |
| G6b | `codec.rdf/test/.../Rdf*TCKTest.java` | Concrete subclasses for the **applicable** abstract TCKs (see applicability matrix) |
| G6c | `codec.rdf/test/.../RdfGraphIsomorphismTest.java` | Model-level assertions via `Model.isIsomorphicWith()` |
| G6d | `codec.rdf/test/.../DcatInteropTest.java` | DCAT-AP fixture (from PoC models): output parses strictly with riot, expected vocabulary IRIs present |

**Verify:** `./gradlew :org.eclipse.fennec.codec.rdf:test`

---

#### Step G7: Documentation + handoff ── Not Started

| Sub-step | File | Description |
|----------|------|-------------|
| G7a | `docs/codec-v2-spec/21-rdf.md` | Reconcile spec with implementation reality |
| G7b | `docs/codec-v2-development-guide.md` | Update current state |
| G7c | `org.eclipse.fennec.codec.rdf/readme.md` | Usage examples per syntax, annotation cookbook (DCAT mapping example) |

---

### Test Strategy (Plan G)

RDF differs from every existing format in two ways that shape the strategy: output is an **unordered graph** (text comparison is meaningless — blank node labels and statement order vary), and the same Model serializes to **multiple syntaxes**. Therefore: assert on graphs, not strings; run everything per-Lang via parameterization.

#### Layer 1: Unit tests (per component, steps G2–G5)

- **Config/metadata** (`codec.metadata`, `codec.api`): `RdfConfigSpecTest` + `RdfConfigResolverSpecTest` following the established `*ConfigSpecTest` pattern — merge cascade (options → factory → module → annotation → default), scope chain (feature → class → global), validation diagnostics for malformed IRIs and misplaced keys.
- **Writer** (`RdfModelBuilderTest`): assert directly on the Jena `Model` (statement queries), never on serialized text. Cases: rdf:type IRIs (incl. the PoC prefix bug as a regression test), every row of the XSD datatype table, langString emission, single/many features, serialize gates (null/empty/default), containment → blank nodes, cross-references → IRI objects, cyclic graphs terminate, shared objects emit triples once.
- **Reader** (`RdfModelReaderTest`): hand-built Models as input (no parsing involved). Cases: type resolution + ERROR/SKIP/FALLBACK, attribute coercion XSD→Java, langString reassembly, blank-node containment, IRI → ID extraction, dangling references, multiple roots.

#### Layer 2: Round-trip tests (step G6a)

`@ParameterizedTest` over `Lang.TURTLE, Lang.JSONLD, Lang.RDFXML, Lang.NTRIPLES`:

1. **EMF-level:** EObject graph → save(lang) → load(lang) → `EcoreUtil.equals()` with original. **Caveat:** many-valued feature order is NOT preserved in RDF (sets, not lists) — round-trip assertions on many-valued features must compare as multisets, not sequences. This is the one place RDF legitimately diverges from the other formats' TCK expectations.
2. **Graph-level:** save → parse → save → parse; assert `model1.isIsomorphicWith(model2)`. Catches lossy writer/reader asymmetries that EMF-level comparison can mask.

#### Layer 3: TCK reuse (step G6b)

Reuse the Plan F abstract TCKs via concrete `Rdf*TCKTest` subclasses (`createFormatProvider()` → `RdfFormatProvider`, extension `ttl`) — **with an applicability review per suite**. Expected matrix:

| TCK Suite | Applicable? | Note |
|-----------|-------------|------|
| Attribute types, Enum, Complex round-trip | ✅ | Direct |
| Multi-valued attributes | ⚠️ | Order-insensitive comparison needed (see Layer 2 caveat) |
| Containment / non-containment refs | ✅ | Blank nodes / IRIs |
| Type / ID strategy | ⚠️ | Strategies surface as IRI shapes, not JSON keys — may need RDF-specific assertions |
| Polymorphism, SuperType | ✅ | rdf:type carries the concrete class; supertype triples optional |
| Reference format (PLAIN/STRUCTURED) | ⚠️ | Both collapse to IRI objects in RDF — verify expectation or exclude with rationale |
| Visibility, Force, Global Ignore, Strictness | ✅ | Config gates are format-agnostic |
| Value handling, Custom value R/W, Custom key, ExtMetadata | ✅ | Key feeds the default predicate IRI |
| EMap | ⚠️ | Decide mapping (entry blank nodes) in spec first |
| Array root | ✅ | Multiple typed roots in one graph |
| Large payload (1000 objects) | ✅ | Also serves as memory sanity check for the in-Model approach |

Every suite excluded or weakened gets a one-line rationale in the test class — no silent skips.

#### Layer 4: Interoperability & conformance (step G6d)

This layer exists because round-trip tests only prove self-consistency — RDF's whole point is that *other* tools consume it.

- **Strict parse check:** every produced output must parse with Jena riot in strict mode (errors, not warnings) for its declared Lang. Run as part of the parameterized round-trip.
- **DCAT-AP fixture:** port the PoC's DCAT model (Catalog/Dataset/PlainLiteral) as a test fixture with `rdf*` annotations mapping to the real vocabularies; assert the output contains `http://www.w3.org/ns/dcat#Catalog`, `http://purl.org/dc/terms/title` with `@de` language tags etc. — i.e., the annotation mapping produces *standard* DCAT, not EMF-shaped IRIs.
- **Cross-syntax equivalence:** the same input saved as Turtle, JSON-LD, and RDF/XML must yield isomorphic Models when parsed back.
- **Optional (stretch):** SHACL validation of the DCAT fixture against DCAT-AP shapes (Jena ships a SHACL engine — no new dependency).

#### Layer 5: Negative & robustness tests

- Unknown rdf:type IRI on load → fallbackStrategy ERROR throws / SKIP drops with diagnostic / FALLBACK instantiates fallback EClass.
- Malformed input per Lang (truncated Turtle, invalid JSON-LD) → clean `IOException`/diagnostics, no partial Resource contents.
- Literal/datatype mismatches (e.g. `"abc"^^xsd:int`) → diagnostic, honoring LENIENT/STRICT `DeserializationMode`.
- IRI edge cases: IDs needing percent-encoding, missing `rdfBaseIri` with ID present, blank-node-only graphs.
- Save-option validation: `.ttl` URI + `codec.rdf.lang=JSONLD` → warning from `validateSaveOptions()`.

#### Test data

- Reuse existing TCK ecores (`test-tck*.ecore`) for Layer 3.
- New `test-rdf-annotations.ecore`: small model exercising every `rdf*` key (vocabulary-mapped class, langString class, fixed-lang attribute, baseIri).
- New `test-rdf-dcat.ecore`: trimmed DCAT-AP fixture for Layer 4 (ported from PoC `dcatap.ecore`/`rdf.ecore`).

#### Exit criteria

- All applicable TCK suites green for `ttl` (reference syntax) + round-trip layer green for all four Langs.
- DCAT interop test proves standard-vocabulary output.
- `./gradlew build` green; no `testOSGi` usage (per project rules).

---

## 8. Execution Roadmap

### Recommended Order

```
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

Plan E (Multi-Format Support) — ✅ COMPLETE (2026-02-16):
  E1-E7: FormatDelegate abstraction + BSON/CBOR/YAML format providers

Plan F (TCK Test Suite) — ✅ COMPLETE (2026-02-17):
  F1: P0 Core round-trip TCKs (6 abstract suites)
  F2: P1 Feature strategy TCKs (7 abstract suites)
  F3: P2 Advanced feature TCKs (10 abstract suites + 8 ecore models)
  F4: Bug fixes (array root, supportsArrayRoot, BSON valueConsumed)

Plan G (RDF Format Support via Jena) — Not Started (planned 2026-06-12):
  G0: Spec chapter 21-rdf.md (spec-first — confirm design decisions)
  G1: Jena dependency + OSGi wrapping check + bundle skeleton
  G2: Metadata extension (rdf* annotation keys, RdfConfig)
  G3: Writer (RdfModelBuilder + RdfWriterDelegate)
  G4: RdfFormatProvider + ResourceFactory (Lang selection)
  G5: Reader (RdfModelReader + RdfReaderDelegate)
  G6: Round-trip + TCK subclasses + DCAT interop tests
  G7: Docs + dev guide update
```

**Notes:**
- GAP-004 is a new feature — lower priority
- Helper classes extracted (TypeResolutionHelper, EMapHelper) improving testability

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
