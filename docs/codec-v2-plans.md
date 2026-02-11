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

**Status:** NOT STARTED (Future Feature)

**Goal:** Extend codec to support formats beyond JSON (BSON, CSV, Ecowitt, etc.) while maintaining feature parity with the JSON implementation.

### Background

The current codec is tightly coupled to Jackson's JSON streaming API (`JsonParser`, `JsonGenerator`). However, the old codebase (`org.eclipse.fennec.codec`) had a working abstraction layer that supported both:

1. **Jackson-compatible formats** — JSON, CSV, XML, YAML via Jackson's `TokenStreamFactory` derivatives
2. **Custom non-Jackson formats** — MongoDB BSON, Ecowitt weather protocol

The architecture intentionally has **two parallel branches** with duplication, as long as comprehensive tests prove feature parity.

### Existing Old Codec Implementations (Reference)

| Project | Format | Approach |
|---------|--------|----------|
| `o.e.f.codec` (old core) | JSON | Jackson `JsonParser`/`JsonGenerator` |
| `o.e.f.codec.csv` | CSV/Query-string | Custom `CodecParserBaseImpl` |
| `o.e.f.codec.mongo` | MongoDB BSON | Custom `CodecParserBaseImpl`/`CodecGeneratorBaseImpl` wrapping `BsonReader`/`BsonWriter` |
| `o.e.f.codec.ecowitt` | Ecowitt protocol | Custom `CodecParserBaseImpl` |

### Jackson Format Ecosystem (Future Integration)

Jackson provides extensive format support through its dataformat modules, all using the **same streaming API**:

| Category | Formats | Jackson Module | Notes |
|----------|---------|----------------|-------|
| **Text** | JSON | `jackson-core` | Default, built-in |
| **Text** | CSV | `jackson-dataformat-csv` | Tabular data |
| **Text** | YAML | `jackson-dataformat-yaml` | Config files |
| **Text** | XML | `jackson-dataformat-xml` | Legacy integration |
| **Text** | Properties | `jackson-dataformat-properties` | Java properties |
| **Binary** | Avro | `jackson-dataformat-avro` | Schema-based, compact |
| **Binary** | Protobuf | `jackson-dataformat-protobuf` | Google Protocol Buffers |
| **Binary** | CBOR | `jackson-dataformat-cbor` | Concise Binary Object Representation |
| **Binary** | Smile | `jackson-dataformat-smile` | Binary JSON, 100% compatible |
| **Binary** | Ion | `jackson-dataformat-ion` | Amazon Ion (text + binary) |

**Key Advantage:** All Jackson formats use `TokenStreamFactory.createParser()` / `createGenerator()`, meaning codec should work with **any Jackson format** without code changes, only dependency additions.

**References:**
- [Jackson Binary Formats Repository](https://github.com/FasterXML/jackson-dataformats-binary)
- [Jackson Main Portal](https://github.com/FasterXML/jackson)

### Architecture: Two-Branch Design

```
                       ┌─────────────────────────┐
                       │   Codec Entry Layer     │
                       │ (Type, ID, Feature,     │
                       │  Reference, SuperType)  │
                       └───────────┬─────────────┘
                                   │
                    ┌──────────────┴──────────────┐
                    ▼                             ▼
         ┌─────────────────────┐      ┌─────────────────────┐
         │ Jackson-Based Branch│      │ Custom Format Branch │
         │   (JsonParser/Gen)  │      │ (CodecParserBaseImpl)│
         │                     │      │                      │
         │ Works with ANY      │      │ For non-Jackson      │
         │ Jackson format!     │      │ protocols            │
         └─────────┬───────────┘      └──────────┬──────────┘
                   │                              │
        ┌──────────┴──────────┐        ┌─────────┴─────────┐
        ▼          ▼          ▼        ▼         ▼         ▼
   Text Formats Binary Formats      BSON    Ecowitt   Custom
   ┌─────────┐  ┌──────────┐     (MongoDB)  (Weather) Protocols
   │ JSON    │  │ Avro     │
   │ CSV     │  │ Protobuf │
   │ YAML    │  │ CBOR     │
   │ XML     │  │ Smile    │
   └─────────┘  └──────────┘
```

**Key Principles:**
1. **Jackson branch serves BOTH text AND binary formats** — Same streaming API (`JsonParser`/`JsonGenerator`)
2. **Custom branch for non-Jackson protocols** — MongoDB BSON, Ecowitt, proprietary formats
3. Duplication between branches is acceptable as long as tests prove feature parity

### Core Architecture: FormatDelegate Pattern with Pluggable I/O

The codec architecture uses **Jackson 3 as the foundation** with a **FormatDelegate pattern** for pluggable format support. Critically, **input/output types are format-specific** — not everything is a stream.

#### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           EMF Entry Layer                                   │
│              (TypeEntry, IdEntry, FeatureEntry, ReferenceEntry)             │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    CodecWriter<T> / CodecReader<S>                          │
│                    T = output target type, S = input source type            │
│                    EMF-aware operations                                     │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    JacksonCodecWriter<T> / JacksonCodecReader<S>            │
│                    extends GeneratorBase / ParserBase                       │
│                                                                             │
│   Handles: EMF context, state machine, CodecWriteContext, TokenBuffer       │
│   Delegates to → FormatDelegate<T> / FormatReaderDelegate<S>                │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
          ┌───────────────────────────┼───────────────────────────┐
          ▼                           ▼                           ▼
┌───────────────────────┐   ┌───────────────────────┐   ┌───────────────────────┐
│ JsonFormatDelegate    │   │ BsonFormatDelegate    │   │ LuceneFormatDelegate  │
│ <OutputStream>        │   │ <BsonDocument>        │   │ <Document>            │
│                       │   │                       │   │                       │
│ Target: OutputStream  │   │ Target: BsonDocument  │   │ Target: Lucene Doc    │
│ Output: byte stream   │   │ Output: in-memory obj │   │ Output: for indexing  │
└───────────────────────┘   └───────────────────────┘   └───────────────────────┘
```

#### Format I/O Types

| Format | Write Target (T) | Read Source (S) | Notes |
|--------|------------------|-----------------|-------|
| **JSON** | `OutputStream` | `InputStream` | Streaming bytes |
| **CBOR/Avro/Smile** | `OutputStream` | `InputStream` | Streaming bytes (Jackson) |
| **MongoDB BSON** | `BsonDocument` | `BsonDocument` | In-memory object |
| **Lucene** | `Document` | `Document` | Lucene Document object |
| **Query String** | `StringBuilder` | `String` | URL parameters |

#### FormatDelegate Types

| Delegate | Target Type | Purpose |
|----------|-------------|---------|
| **JsonFormatDelegate** | `OutputStream` | Keep existing JSON impl as-is |
| **JacksonFormatDelegate** | `OutputStream` | Wraps AvroGenerator, CBORGenerator, etc. |
| **BsonFormatDelegate** | `BsonDocument` | MongoDB's native in-memory document |
| **LuceneFormatDelegate** | `Document` | Lucene Document for indexing |

#### Interfaces

```java
// Format-specific writer delegate (generic target type)
public interface FormatDelegate<T> {

    // Target management
    void setTarget(T target);
    T getTarget();

    // Document lifecycle
    void writeStartDocument();
    void writeEndDocument();

    // Array lifecycle
    void writeStartArray(String name);
    void writeEndArray();

    // Field writing
    void writeName(String name);
    void writeString(String name, String value);
    void writeInt(String name, int value);
    void writeLong(String name, long value);
    void writeDouble(String name, double value);
    void writeBoolean(String name, boolean value);
    void writeBinary(String name, byte[] value);
    void writeNull(String name);

    // Format-specific features
    void writeObjectId(String name, Object value);
    boolean supportsNativeObjectId();

    // Finalization
    void flush();
    void close();
}

// Format-specific reader delegate (generic source type)
public interface FormatReaderDelegate<S> {

    // Source management
    void setSource(S source);
    S getSource();

    // Navigation
    boolean hasNext();
    TokenType nextToken();
    TokenType currentToken();

    // Reading
    String currentName();
    String readString();
    int readInt();
    long readLong();
    double readDouble();
    boolean readBoolean();
    byte[] readBinary();

    // Format-specific
    Object readObjectId();
    boolean supportsNativeObjectId();
}

// Simple token type enum (format-agnostic)
public enum TokenType {
    START_OBJECT,
    END_OBJECT,
    START_ARRAY,
    END_ARRAY,
    FIELD_NAME,
    VALUE_STRING,
    VALUE_NUMBER,
    VALUE_BOOLEAN,
    VALUE_NULL,
    VALUE_BINARY
}
```

#### Example Implementations

```java
// JSON: streaming to OutputStream
public class JsonFormatDelegate implements FormatDelegate<OutputStream> {
    private OutputStream target;
    private JsonGenerator generator;

    @Override
    public void setTarget(OutputStream target) {
        this.target = target;
        this.generator = jsonFactory.createGenerator(target);
    }

    @Override
    public OutputStream getTarget() {
        return target;
    }
}

// BSON: in-memory BsonDocument
public class BsonFormatDelegate implements FormatDelegate<BsonDocument> {
    private BsonDocument target;
    private BsonDocumentWriter writer;

    @Override
    public void setTarget(BsonDocument target) {
        this.target = target;
        this.writer = new BsonDocumentWriter(target);
    }

    @Override
    public BsonDocument getTarget() {
        return target;  // Return the populated document
    }

    @Override
    public void writeObjectId(String name, Object value) {
        if (value instanceof ObjectId) {
            writer.writeObjectId(name, (ObjectId) value);  // Native!
        } else {
            writer.writeString(name, value.toString());
        }
    }
}

// Lucene: Document for indexing
public class LuceneFormatDelegate implements FormatDelegate<Document> {
    private Document target;

    @Override
    public void setTarget(Document target) {
        this.target = target;
    }

    @Override
    public Document getTarget() {
        return target;  // Ready for IndexWriter.addDocument()
    }

    @Override
    public void writeString(String name, String value) {
        target.add(new TextField(name, value, Field.Store.YES));
    }

    @Override
    public void writeObjectId(String name, Object value) {
        target.add(new StringField(name, value.toString(), Field.Store.YES));
    }
}
```

#### Factory Pattern for Format Selection

```java
public interface CodecFormatFactory<S, T> {
    FormatDelegate<T> createWriter(T target);
    FormatReaderDelegate<S> createReader(S source);
}

// Usage examples
CodecFormatFactory<InputStream, OutputStream> jsonFormat = new JsonFormatFactory();
CodecFormatFactory<BsonDocument, BsonDocument> bsonFormat = new BsonFormatFactory();
CodecFormatFactory<Document, Document> luceneFormat = new LuceneFormatFactory();

// Serialize EObject to different targets
EObject person = ...;

// To JSON bytes
OutputStream jsonOut = new ByteArrayOutputStream();
codec.serialize(person, jsonFormat.createWriter(jsonOut));

// To BsonDocument (in-memory)
BsonDocument bsonDoc = new BsonDocument();
codec.serialize(person, bsonFormat.createWriter(bsonDoc));
// bsonDoc is now populated, ready for MongoDB insert

// To Lucene Document
Document luceneDoc = new Document();
codec.serialize(person, luceneFormat.createWriter(luceneDoc));
// luceneDoc is now populated, ready for IndexWriter.addDocument()
```

### Key Benefits

1. **Single JacksonCodecWriter** — All EMF logic, context management, state tracking in ONE place
2. **Keep existing JSON code** — Extract byte-writing parts into `JsonFormatDelegate<OutputStream>`
3. **Jackson formats for free** — `JacksonFormatDelegate<OutputStream>` wraps any Jackson generator
4. **Custom formats with custom targets** — `BsonFormatDelegate<BsonDocument>`, `LuceneFormatDelegate<Document>`
5. **Type-safe I/O** — Generics ensure correct source/target types per format
6. **Feature parity via tests** — Same EMF operations must work across ALL formats

### Test Strategy

The key to this architecture is **feature parity testing**:

1. **Abstract Feature Tests** — Define tests in terms of abstract operations, not JSON syntax
2. **Parameterized Tests** — Same test suite runs against all formats
3. **Round-Trip Tests** — Serialize → Deserialize → Compare for each format

```java
@ParameterizedTest
@MethodSource("allFormats")
void shouldSerializeAndDeserializeEObject(FormatConfig format) {
    // Create test EObject
    Person person = TestFactory.eINSTANCE.createPerson();
    person.setName("John");

    // Serialize to format
    byte[] serialized = codec.serialize(person, format);

    // Deserialize back
    Person deserialized = codec.deserialize(serialized, format, Person.class);

    // Assert equality
    assertThat(deserialized.getName()).isEqualTo("John");
}
```

### Implementation Phases

#### Phase E1: Refactor to FormatDelegate Pattern (Foundation)

| Step | Description | Effort |
|------|-------------|--------|
| **E1.1** | Define `FormatDelegate<T>` interface with generic target type | LOW |
| **E1.2** | Define `FormatReaderDelegate<S>` interface with generic source type | LOW |
| **E1.3** | Define `TokenType` enum (format-agnostic token representation) | LOW |
| **E1.4** | Define `CodecFormatFactory<S, T>` factory interface | LOW |
| **E1.5** | Extract `JsonFormatDelegate<OutputStream>` from existing JSON impl | MEDIUM |
| **E1.6** | Extract `JsonReaderDelegate<InputStream>` from existing JSON impl | MEDIUM |
| **E1.7** | Refactor `JacksonCodecWriter` to use `FormatDelegate<T>` injection | MEDIUM |
| **E1.8** | Refactor `JacksonCodecReader` to use `FormatReaderDelegate<S>` injection | MEDIUM |
| **E1.9** | Tests: Verify existing JSON behavior unchanged | LOW |

#### Phase E2: JacksonFormatDelegate (Jackson Formats)

| Step | Description | Effort |
|------|-------------|--------|
| **E2.1** | Implement `JacksonFormatDelegate<OutputStream>` wrapping any `JsonGenerator` | LOW |
| **E2.2** | Implement `JacksonReaderDelegate<InputStream>` wrapping any `JsonParser` | LOW |
| **E2.3** | Test with CBOR (`jackson-dataformat-cbor`) | LOW |

**Why CBOR?**
- Binary format (proves binary works, not just text like JSON)
- No schema required (unlike Avro/Protobuf)
- Widely used standard (RFC 8949, IoT, COSE)
- If CBOR works, other Jackson formats (Smile, Avro, etc.) will work too

**Note:** Once `JacksonFormatDelegate` works with CBOR, ALL Jackson formats work automatically.

#### Phase E3: BsonFormatDelegate (MongoDB BSON)

| Step | Description | Effort |
|------|-------------|--------|
| **E3.1** | Implement `BsonFormatDelegate<BsonDocument>` wrapping `BsonDocumentWriter` | MEDIUM |
| **E3.2** | Implement `BsonReaderDelegate<BsonDocument>` wrapping `BsonDocumentReader` | MEDIUM |
| **E3.3** | Handle native ObjectId, Decimal128, Binary types | MEDIUM |
| **E3.4** | Feature parity tests: JSON ↔ BSON round-trip | MEDIUM |

**Testing without MongoDB:**

BSON can be tested purely in-memory without any MongoDB connection:

```java
// Dependency: org.mongodb:bson:4.11.1 (NOT mongo-java-driver!)

@Test
void testBsonRoundTrip() {
    // Write to in-memory BsonDocument (no MongoDB needed)
    BsonDocument doc = new BsonDocument();
    FormatDelegate<BsonDocument> writer = new BsonFormatDelegate();
    writer.setTarget(doc);

    // ... serialize EObject ...

    // Read back from same BsonDocument
    FormatReaderDelegate<BsonDocument> reader = new BsonReaderDelegate();
    reader.setSource(doc);

    // ... deserialize to EObject ...
}
```

**Note:** Target is `BsonDocument` (in-memory object), not `OutputStream`. This proves custom non-stream targets work.

#### Phase E4: Feature Parity Test Framework

| Step | Description | Effort |
|------|-------------|--------|
| **E4.1** | Abstract test suite for EMF operations (format-independent) | MEDIUM |
| **E4.2** | Parameterized tests across all FormatDelegates | LOW |
| **E4.3** | Round-trip tests: EMF → Format → EMF | MEDIUM |

**Focused Test Strategy:**

| Layer | Format | Target/Source | Purpose |
|-------|--------|---------------|---------|
| **JsonFormatDelegate** | JSON | `OutputStream`/`InputStream` | Existing baseline, text format |
| **JacksonFormatDelegate** | CBOR | `OutputStream`/`InputStream` | Proves Jackson binary formats work |
| **BsonFormatDelegate** | BSON | `BsonDocument`/`BsonDocument` | Proves custom in-memory targets work |

This covers:
- ✅ Text format (JSON)
- ✅ Binary format via Jackson (CBOR)
- ✅ Custom non-stream format (BSON in-memory)
- ✅ Native type handling (BSON ObjectId, Decimal128)

**Test Matrix (3 formats):**

| Feature | JSON | CBOR | BSON |
|---------|------|------|------|
| Target type | `OutputStream` | `OutputStream` | `BsonDocument` |
| Source type | `InputStream` | `InputStream` | `BsonDocument` |
| Type serialization | ✓ | ✓ | ✓ |
| SuperType serialization | ✓ | ✓ | ✓ |
| ObjectId | string | string | native ObjectId |
| Decimal128 | string/double | string/double | native Decimal128 |
| EObject lifecycle | ✓ | ✓ | ✓ |
| EReference (expand) | ✓ | ✓ | ✓ |
| EAttribute (all types) | ✓ | ✓ | ✓ |

**Note:** Other Jackson formats (Avro, Smile, YAML, XML) will work once CBOR works — no separate testing needed.

### Files to Create (codec project)

```
org.eclipse.fennec.codec/
├── src/org/eclipse/fennec/codec/
│   ├── format/
│   │   ├── CodecParserBase.java          # Abstract parser (port from old)
│   │   ├── CodecGeneratorBase.java       # Abstract generator (port from old)
│   │   ├── CodecParserFactory.java       # Parser factory interface
│   │   └── CodecGeneratorFactory.java    # Generator factory interface
│   └── format/impl/
│       ├── JsonCodecParserFactory.java   # Jackson JSON (default)
│       └── JsonCodecGeneratorFactory.java
└── test/org/eclipse/fennec/codec/
    └── format/
        ├── FormatFeatureParityTest.java  # Parameterized across formats
        └── FormatRoundTripTest.java      # Round-trip validation
```

### Project Structure

#### Existing Projects (Reference)

The old codec projects have been moved to the `old/` folder and are excluded from the Gradle build.
They serve as reference implementations for Plan E (Multi-Format Support).

```
old/                                   # Archived - excluded from build
├── org.eclipse.fennec.codec/         # Old codec with FormatDelegate pattern (reference)
├── org.eclipse.fennec.codec.mongo/   # MongoDB BSON (reference for BsonFormatDelegate)
├── org.eclipse.fennec.codec.csv/     # CSV/query-string parser
├── org.eclipse.fennec.codec.ecowitt/ # Ecowitt weather protocol
└── ...                                # Other archived projects
```

**Note:** The `old/` and `docs/` folders are excluded from the Gradle build via `settings.gradle`.

#### New Structure (After Refactoring)

```
org.eclipse.fennec.codec/
├── src/org/eclipse/fennec/codec/
│   ├── format/                        # FormatDelegate pattern
│   │   ├── CodecWriter.java           # EMF-aware writer interface
│   │   ├── CodecReader.java           # EMF-aware reader interface
│   │   ├── FormatDelegate.java        # Format-specific encoding interface
│   │   ├── FormatReaderDelegate.java  # Format-specific decoding interface
│   │   ├── JacksonCodecWriter.java    # Single impl, delegates to FormatDelegate
│   │   ├── JacksonCodecReader.java    # Single impl, delegates to FormatReaderDelegate
│   │   └── impl/
│   │       ├── JsonFormatDelegate.java      # Default JSON (existing code)
│   │       └── JacksonFormatDelegate.java   # Wraps any Jackson generator
│   └── ... (existing packages)
└── test/org/eclipse/fennec/codec/
    └── format/
        ├── FormatFeatureParityTest.java     # Parameterized across formats
        └── FormatRoundTripTest.java         # EMF → Format → EMF

org.eclipse.fennec.codec.bson/               # MongoDB BSON format
├── src/.../bson/
│   ├── BsonFormatDelegate.java              # Wraps BsonWriter
│   └── BsonFormatReaderDelegate.java        # Wraps BsonReader
└── test/...
    └── BsonFeatureParityTest.java           # Proves parity with JSON
```

#### Jackson Format Modules (Optional, Minimal)

```
org.eclipse.fennec.codec.avro/         # Just: dependency + JacksonFormatDelegate config + tests
org.eclipse.fennec.codec.cbor/         # Just: dependency + JacksonFormatDelegate config + tests
```

These are trivial once `JacksonFormatDelegate` exists.

### Notes

- This plan builds on top of existing codec functionality
- Jackson-based JSON remains the default and primary format
- Custom formats are opt-in via factory registration
- All format implementations must pass the same feature parity tests
- Refer to old codebase (`org.eclipse.fennec.codec.*`) for working examples

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

Plan E (Multi-Format Support) — NOT STARTED:
  E1: Refactor to FormatDelegate<T> pattern with pluggable I/O types
      - FormatDelegate<T> for writers, FormatReaderDelegate<S> for readers
      - Extract JsonFormatDelegate<OutputStream> from existing code
  E2: Implement JacksonFormatDelegate + test with CBOR (binary format)
  E3: Implement BsonFormatDelegate<BsonDocument> (in-memory, no MongoDB needed)
  E4: Feature parity tests: JSON ↔ CBOR ↔ BSON round-trips
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
