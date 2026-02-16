# Codec V2 Development Guide

This document provides context for continuing codec development across sessions. It captures the goals, current state, and links to detailed architecture documentation.

**Last Updated:** 2026-02-16 (Plan E Implementation Started)

**Session Summary (2026-02-16 latest):**

**Plan E Implementation Started (Multi-Format Support):**
- Refined FormatDelegate<T> architecture with concrete implementation plan
- Strategy: Build abstraction first → verify with existing JSON tests (~1000+) → BSON as first non-Jackson format
- Key insight: `FormatDelegateGenerator<T>` extends Jackson's `GeneratorBase` (IS a `JsonGenerator`), so zero entry class changes needed
- Architecture: `FormatDelegate<T>` for writers, `FormatReaderDelegate<S>` for readers, `CodecFormatProvider<S,T>` factory
- Jackson bridge: `FormatDelegateGenerator<T>` wraps FormatDelegate as JsonGenerator, `FormatDelegateParser<S>` wraps FormatReaderDelegate as JsonParser
- `CodecReadContext` (already exists) provides EMF context for non-JSON parsers; deserializer has existing fallback via `DeserializationContext` attributes
- Implementation order: E1 (interfaces) → E2 (bridge) → E3 (JSON impl) → E4 (CodecResource) → E5 (verify) → E6 (BSON) → E7 (CBOR etc.)
- See `codec-v2-plans.md` Plan E for full details with file listings and step tracking

**Old Code Archived:**
- Moved old codec projects to `old/` folder (reference implementations for Plan E)
- Updated `settings.gradle` to exclude `old/` and `docs/` from Gradle build
- Old projects preserved as reference: `org.eclipse.fennec.codec`, `org.eclipse.fennec.codec.mongo`, etc.

**Previous Session (2026-02-08):**

**Plan D Verification (Discriminator Refactoring):**
- ✅ **D1 (Remove MAPPED from TypeStrategy)** — DONE, spec `06-type.md` confirms removal
- ✅ **D2 (Inline Mapping for References)** — DONE, fully implemented + `CodecResourceInlineMappingTest.java`
- ✅ **D3 (Property-Based Discriminator Config)** — DONE, documented in spec sections 4.4 and 5.1
- ✅ **D4 (STRUCTURED Format + Discriminator)** — DONE, spec `08-discriminator-mapping.md` §1.4 clarified
  - Discriminator mapping has priority over STRUCTURED format
  - When both are configured, discriminator value goes inside STRUCTURED `_type` object
  - Implementation verified in `TypeSerializationEntry.java` and `TypeDeserializationEntry.java`

**Spec Updates:**
- Added section 1.4 "Interaction with TypeFormat (PLAIN vs STRUCTURED)" to `08-discriminator-mapping.md`
- Updated `codec-v2-plans.md` with Plan D verification results

**Previous Session (2026-02-08):**

**Plan B Phase 3 Progress:**
- ✅ **GAP-011 (Misconfiguration test coverage)** — Already covered with 22 test files
- ✅ **GAP-012 (DeserializationMode usage)** — IMPLEMENTED
  - `ContextHelper`: Added `DESERIALIZATION_MODE` constant + helper methods (`isStrictMode()`, `isLenientMode()`, `isAutoDetectMode()`)
  - `CodecResource`: Wires `CODEC_DESERIALIZATION_MODE` load option to context
  - `CodecEObjectDeserializer`: Type resolution issues ERROR in STRICT, WARNING in LENIENT; unexpected type tokens handled
  - `TypeDeserializationEntry`: `handleTypeResolutionFailure()` uses mode for ERROR vs WARNING
  - `SuperTypeDeserializationEntry`: Validation is opt-in feature (independent of mode); mode controls error handling
  - `DeserializationModeTest.java`: 12 new tests covering LENIENT/STRICT/AUTO_DETECT behavior
- ⏸️ **GAP-013 (Enum-level annotation support)** — POSTPONED per user request

**Key Design Decision (GAP-012):**
- **DeserializationMode** (STRICT/LENIENT/AUTO_DETECT) controls whether errors **break** deserialization or produce warnings
- **SuperType validation** is a separate opt-in feature via `validateSuperTypeHierarchy` flag, independent of DeserializationMode

**Earlier Session (2026-02-08):**

**Plan B Phase 2 COMPLETE — All 5 GAPs Done:**
- ✅ **GAP-008 (Value Reader/Writer handlers)** — Already implemented; added missing `featureValueReaderInstances`/`featureValueWriterInstances` load/save options wiring
  - `ContextHelper`: Added `FEATURE_VALUE_READER_INSTANCES`, `FEATURE_VALUE_WRITER_INSTANCES` constants
  - `CodecResource`: Wired instance options in load/save paths
  - `AttributeDeserializationEntry`/`AttributeSerializationEntry`: Added `resolveEffectiveReader()`/`resolveEffectiveWriter()` for runtime instance binding
  - `CodecResourceCustomValueTest`: Added `InstanceBindingTests` (2 tests)
- ✅ **GAP-009 (Scope wiring for runtime options)** — Implemented EClass/EReference/EAttribute keyed config maps
  - `ConfigProperty`: Added `ECLASS_CONFIG`, `EREFERENCE_CONFIG`, `EATTRIBUTE_CONFIG` scope keys
  - `ConfigurationResolver`: `extractClassProperties()` and `extractFeatureProperties()` support both `Map<EClass,...>` and `Map<String,...>`
  - `ReferenceConfig`: Added `typeKey` and `idKey` per-reference overrides
  - `ConfigurationResolverTest`: Added `ScopeConfigurationTests` (5 tests)
- ✅ **GAP-007 (Expand deserialization)** — Already implemented; verified with `ExpandReferenceTest.java` (18 tests)
- ✅ **GAP-006 (Metadata merge behavior)** — Already implemented; verified with 15 spec test files
- ✅ **GAP-010 (Hierarchy resolution tests)** — Already implemented; verified coverage across TypeResolutionHelper (27), SuperTypeConfig (33), spec tests

**Previous Session (2026-02-08 earlier):**
- Verified GAP-002 (Fallback Strategy) — Already in `TypeDiscriminatorService`
- Implemented GAP-003 (Feature Strictness) — `ClassConfig`, strictOnUnknown/strictOnMissing
- Postponed GAP-004 (Diagnostic Options), GAP-014 (inherit enum) to later release

**Next Session:** Continue Plan E implementation. Start with Phase E1 (FormatDelegate interfaces in codec.api). See `codec-v2-plans.md` §7 for detailed step tracking.

---

## 0. Active Task Hierarchy (SESSION CONTINUITY)

This section tracks the current task hierarchy to prevent context loss during nested investigations.

### 0.1 How to Use This Section

**When starting work:** Check this section first to understand where we are.

**When a new issue arises during work:**
1. **ASK:** "Is this a child task (fix now, return to parent) or independent task (add to TODO, continue)?"
2. **If child task:** Add it to the hierarchy below with proper indentation
3. **If independent task:** Add to §5.4 "Remaining Work" or §10.4 "Current TODO List"

**When completing a task:** Mark it ✅ and return to the parent task.

**Format:**
```
MAIN TASK: [description] - [status: ACTIVE/PAUSED/✅]
├── CHILD: [description] - [status]
│   ├── CHILD: [sub-issue] - [status]
│   └── CHILD: [sub-issue] - [status]
└── RETURN TO: [next step after children complete]
```

### 0.2 Current Task Hierarchy

```
ACTIVE: Plan E Multi-Format Support - IN PROGRESS (2026-02-16)
│
│  Phase E1: FormatDelegate Interfaces (codec.api) - PENDING
│  │  - E1.1: TokenType enum
│  │  - E1.2: FormatDelegate<T> interface
│  │  - E1.3: FormatReaderDelegate<S> interface
│  │  - E1.4: CodecFormatProvider<S,T> interface
│  │
│  Phase E2: Jackson Bridge (codec) - PENDING
│  │  - E2.1: FormatDelegateGenerator<T> extends GeneratorBase
│  │  - E2.2: FormatDelegateParser<S> extends ParserBase
│  │  - E2.3: TokenTypeMapper utility
│  │
│  Phase E3: JSON/Jackson FormatDelegate impl - PENDING
│  │  - E3.1: JacksonStreamFormatDelegate (wraps any Jackson generator)
│  │  - E3.2: JacksonStreamFormatReaderDelegate (wraps any Jackson parser)
│  │  - E3.3: JacksonFormatProvider (takes TokenStreamFactory)
│  │
│  Phase E4: Refactor CodecResource - PENDING
│  │  - E4.1: Add CodecFormatProvider to CodecResource
│  │  - E4.2: Add Resource fallback in ContextHelper + deserializer
│  │  - E4.3: doSaveWithFormat() / doLoadWithFormat()
│  │  - E4.4: CodecFormatResourceFactory
│  │
│  Phase E5: Verify with existing JSON tests - PENDING
│  │  - E5.1: All ~1000+ tests through FormatDelegate layer
│  │  - E5.2: AbstractFormatFeatureParityTest
│  │  - E5.3: JsonFormatFeatureParityTest (baseline)
│  │
│  Phase E6: BSON Format - PENDING
│  │  - E6.1: org.eclipse.fennec.codec.bson project
│  │  - E6.2: BsonFormatDelegate<BsonDocument>
│  │  - E6.3: BsonFormatReaderDelegate<BsonDocument>
│  │  - E6.4: BsonFormatProvider
│  │  - E6.5: Round-trip + parity tests
│  │
│  Phase E7: Additional Jackson Formats (CBOR, YAML, etc.) - FUTURE

PREVIOUS: Plan B + Plan D Complete - ✅ (2026-02-08)
│  - Plan B: 12/14 GAPs done, 2 postponed (GAP-004, GAP-013/014)
│  - Plan D: Discriminator refactoring verified complete
│  - Old code archived to old/ folder
│  - Total: ~2519+ tests, 0 failures

PREVIOUS: Integration Tests + PLAIN Reference Format - ✅ (2026-02-06)
> **Older completed tasks available in git history**

```

## 1. Project Goal

**Fennec Codec V2** is a rewrite of the EMF JSON codec with a metadata-driven architecture:

- **Metadata Layer** parses EAnnotations at EPackage registration time
- **Codec Layer** consumes metadata during serialization/deserialization
- **Configuration** supports 5 sources (Options, Resource, Factory, Module, Annotation) merged hierarchically
- **Spec-driven development** — implementation follows `docs/codec-v2-spec/`

## 2. Architecture Overview

### 2.1 Two-Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│ org.eclipse.fennec.codec (Codec Runtime)                   │
│                                                             │
│ ┌─────────────────────┐   ┌───────────────────────────┐   │
│ │ CodecEObjectSerializer├──→ SerializationEntry (Type, │   │
│ │ CodecEObjectDeserializer  │ ID, Feature, Reference)   │   │
│ └─────────────────────┘   └───────────────────────────┘   │
│            │                                               │
│            ▼                                               │
│ ┌─────────────────────────────────────────────────────┐   │
│ │ EffectiveCodecConfig (resolves per-feature config) │   │
│ └─────────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────────┘
                       │ uses metadata
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ org.eclipse.fennec.codec.metadata (Metadata Service)       │
│                                                             │
│ ┌────────────────────┐   ┌──────────────────────────────┐  │
│ │ CodecAspectProvider├──→│ Parses EAnnotations          │  │
│ │ MetadataService     │   │ Creates aspect objects       │  │
│ └────────────────────┘   │ (TypeDiscriminatorService,   │  │
│                           │  ClassConfig, FeatureConfig) │  │
│                           └──────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 Key Components

| Component | Location | Purpose |
|-----------|----------|---------|
| **MetadataService** | `codec.metadata` | EPackage registration, aspect creation |
| **CodecAspectProvider** | `codec.metadata.provider` | Parses EAnnotations → Config objects |
| **TypeDiscriminatorService** | `codec.metadata.type` | Manages discriminator→EClass mappings |
| **EffectiveCodecConfig** | `codec.api.config.effective` | Per-feature config resolution |
| **CodecEObjectSerializer** | `codec.ser` | Orchestrates serialization entries |
| **CodecEObjectDeserializer** | `codec.deser` | Orchestrates deserialization entries |
| **SerializationEntry** | `codec.ser` | Type/ID/Feature/Reference serializers |
| **DeserializationEntry** | `codec.deser` | Type/ID/Feature/Reference deserializers |

### 2.3 Configuration Hierarchy (5 Sources)

From highest to lowest priority:

1. **SaveOptions/LoadOptions** (per-operation)
2. **Resource options** (per-resource)
3. **ResourceFactory options** (per-factory)
4. **CodecModule** (global config)
5. **EAnnotation** (model metadata)

See `docs/codec-v2-spec/02-config-resolution.md` for details.

## 3. Current State (2026-02-05)

### 3.1 What Works

✅ **Configuration Layer (metadata.ecore + codec.api)**
- All 6 config types: Type, SuperType, Discriminator, ID, Feature, Reference
- Spec tests complete (54+48+38+78+64+56 = 338 tests)
- Resolver spec tests complete (24+21+17+17+30+23+20 = 152 tests)
- Layer 1 annotation validation (parse-time errors for invalid EAnnotations)
- Config merging (5-source hierarchy with toBuilder() pattern)

✅ **Metadata Service**
- EPackage registration
- Aspect parsing (CodecAspectProvider)
- TypeDiscriminatorService (discriminator value → EClass mapping)
- Diagnostic collection during parsing

✅ **Codec Runtime (fully cleaned up)**
- `org.eclipse.fennec.codec.*` packages (all deprecated `old codec.v2.*` code deleted)
- Serialization entries (Type, ID, Feature, Reference)
- Deserialization entries (Type, ID, Feature, Reference)
- CodecEObjectSerializer/Deserializer orchestrators
- CodecResource + CodecModule
- Jackson integration (JsonParser/JsonGenerator + custom buffer)
- EffectiveCodecConfig (per-feature resolution)
- ConfigurationResolver (replaces old CodecConfiguration)

✅ **Utility Helpers (org.eclipse.fennec.codec.util)**
- `AnnotationHelper` — ExtendedMetaData annotation lookups
- `CodecResourceHelper` — Type resolution, compatibility checks
- `MetadataServiceFactory` — MetadataWhiteboard creation
- `TypeResolutionHelper` — EClass resolution by name, class name, numeric ID, URI (22 tests)
- `EMapHelper` — EMap reference detection, key/value feature access (18 tests)

✅ **Integration Tests**
- 23 resource test files (193 tests) — migrated to new codec.* packages
- 9 ser/deser/type integration tests — migrated to new codec.* packages
- GeoJson, JsonSchema, OpenAPI codecs — migrated to non-deprecated API
- ForceReadWriteTest.java — verifies two-gate model + forceRead/forceWrite

✅ **Discriminator Mapping (Type Mapping Registry)**
- All 19 tests in CodecResourceInlineMappingTest passing
- Deserialization, serialization, ERROR/FALLBACK strategies, root-level, supertype inheritance

✅ **Deprecated Code Removed**
- All `old codec.v2.*` source/test files deleted (55 src + 9 test)
- All `codec.api.value.*` and `codec.api.diagnostic.*` files deleted (11 src + 14 test)
- Zero skipped tests remaining

### 3.2 Test Status

**Current Counts (2026-02-05, after cleanup + helper extraction):**
- **codec:** 992 tests, 0 failures, 0 skipped
- **codec.api:** ~490 tests
- **codec.metadata:** ~220 tests
- **model.metadata:** ~200 tests
- **codec.geojson, codec.jsonschema, codec.openapi:** ~617 tests combined
- **Total across all 7 projects:** ~2519 tests, 0 failures, 0 skipped

**Test Organization:**
- `org.eclipse.fennec.codec.api/test` — config API tests (spec + resolver tests)
- `org.eclipse.fennec.codec.metadata/test` — aspect provider tests + TypeDiscriminatorServiceTest
- `org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/*` — runtime tests (all active)
- `org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/util/*` — helper unit tests

### 3.3 What's Next

**ACTIVE (Current):**
1. **Plan E: Multi-Format Support** — FormatDelegate abstraction + BSON
   - See `codec-v2-plans.md` §7 for detailed phase/step tracking
   - E1→E2→E3→E4→E5 (verify with JSON) → E6 (BSON) → E7 (CBOR etc.)

**DEFERRED:**
1. Plan B remaining: GAP-004 (Diagnostic Options), GAP-013 (Enum annotations), GAP-014 (inherit enum)
2. Plan C: Documentation examples (DOC-001 through DOC-004)
3. Performance testing
4. User guide, migration guide

## 4. Key Documents

### 4.1 Specification (Source of Truth)

**Location:** `docs/codec-v2-spec/`

| File | Purpose |
|------|---------|
| `00-overview.md` | Table of contents |
| `01-introduction.md` | Goals, architecture, terminology |
| `02-config-resolution.md` | 5-source hierarchy, scope chain |
| `06-type.md` | Type serialization/deserialization |
| `07-supertype.md` | SuperType configuration |
| `08-discriminator-mapping.md` | Discriminator resolution |
| `09-id.md` | ID serialization/deserialization |
| `10-reference.md` | Reference serialization/deserialization |
| `11-feature.md` | Feature (attribute) serialization/deserialization |
| `12-feature-serialization.md` | Feature visibility model, gates |
| `15-error-handling.md` | Diagnostics, validation rules |
| `16-annotation-reference.md` | Complete property reference |
| `17-format-abstraction.md` | PLAIN vs STRUCTURED formats |

### 4.2 Architecture Documents

**Metadata Layer:**
- `org.eclipse.fennec.model.metadata/model-metadata-architecture.md`
- `org.eclipse.fennec.codec.metadata/codec-metadata-architecture.md`

**Development:**
- `docs/codec-v2-development-guide.md` (this file)
- `docs/codec-v2-plans.md` (roadmap, GAP analysis)
- `docs/codec-v2-migration-notes.md` (V1 → V2 migration guide)
- `docs/codec-v2-reference.md` (EMF concepts, terminology, API reference)

## 5. Development Workflow

### 5.1 Making Changes

1. **Check spec first** — `docs/codec-v2-spec/`
2. **Write test** — TDD approach (spec test or integration test)
3. **Implement** — follow spec exactly
4. **Verify** — run relevant test suite
5. **Update docs** — if behavior clarified or new feature added

### 5.2 Testing Strategy

**Spec Tests** (codec.api):
- Unit tests for configuration objects
- Resolver tests for configuration resolution
- Validation tests for diagnostic rules

**Integration Tests** (codec):
- Resource tests (CodecResource end-to-end)
- Ser/Deser tests (specific features)
- Roundtrip tests (symmetry verification)

**Custom Codec Tests** (codec.geojson, codec.jsonschema, codec.openapi):
- Domain-specific serialization tests
- Custom value reader/writer tests

### 5.3 Common Patterns

**Configuration Resolution:**
```java
EffectiveCodecConfig effectiveConfig = ...;
TypeConfig resolved = effectiveConfig.resolveTypeConfig(eClass);
if (resolved.getTypeStrategy() == TypeStrategy.NAME) {
    // Use name-based type serialization
}
```

**Aspect Creation (Metadata Layer):**
```java
// In CodecAspectProvider
ClassConfig config = MetadataFactory.eINSTANCE.createClassConfig();
config.setTypeStrategy(TypeStrategy.NAME);
// Validation happens here (Layer 1)
validateTypeConfig(config, eClass, diagnostics);
```

**Entry Pattern (Codec Layer):**
```java
// Serialization
TypeSerializationEntry entry = new TypeSerializationEntry(effectiveConfig, ...);
entry.serialize(eObject, generator, context);

// Deserialization
TypeDeserializationEntry entry = new TypeDeserializationEntry(effectiveConfig, ...);
EClass resolved = entry.resolveEClass(parser, context, currentReference);
```

**Discriminator Mapping (Type Mapping Registry):**
```java
// Deserialization - targeted resolve with mapId
String mapId = getDiscriminatorMapId(hintEClass);
EClass resolved = typeResolver.resolve(mapId, discriminatorValue, namespace, hintEClass, fallbackStrategy);

// Serialization - reverse lookup
String mapId = typeDiscriminatorService.getMapIdForEClass(actualEClass);
String discriminatorValue = typeDiscriminatorService.getDiscriminatorValue(mapId, actualEClass);
```

### 5.4 Remaining Work

**Plan B Phase 1: Migration GAPs**
- [✅] GAP-001: Feature Visibility — DONE
- [✅] GAP-005: ID Value Key — DONE
- [✅] GAP-002: Fallback Strategy wiring — DONE (TypeDiscriminatorService handles inline + typeMapping fallback)
- [✅] GAP-003: Feature Strictness — DONE (ClassConfig, strictOnUnknown/Missing in deserialization)
- [⏸️] GAP-014: inherit enum — POSTPONED to B2 (requires codec.ecore model change)
- [⏸️] GAP-004: Diagnostic Options — POSTPONED to B2 (NEW FEATURE)

**Integration Tests**
- [✅] Discriminator mapping tests (CodecResourceInlineMappingTest.java)
- [✅] Feature visibility integration tests (FeatureVisibilityIntegrationTest.java — ignoreRead/Write/ignore)
- [✅] Reference expansion integration tests (ExpandReferenceTest.java — edge cases added)
- [✅] PLAIN reference format tests (PlainReferenceFormatTest.java — ser/deser/round-trip)
- [✅] Strictness integration tests (StrictnessIntegrationTest.java — strictOnUnknown/Missing)
- [✅] Type resolution tests (TypeStrategy*.java, TypeResolution*.java, TypeDeserializationEntryTest — URI/NAME/NUMERIC/CLASS/SCHEMA_AND_TYPE, contexts, hints)
- [✅] ID serialization tests (CodecResourceIdTest.java — 26 tests covering PLAIN/STRUCTURED, IdKeyMode, separators, round-trip)

**Code Quality** (completed)
- [✅] Deprecated code removal (old codec.v2.*, codec.api.value.*, codec.api.diagnostic.*)
- [✅] @claude comment cleanup
- [✅] Helper extraction: TypeResolutionHelper, EMapHelper

**Documentation:**
- [ ] User guide (how to use codec v2)
- [ ] Migration guide (v1 → v2)
- [ ] Performance guide

## 6. Debugging Tips

### 6.1 Common Issues

**Issue: Config not taking effect**
- Check scope chain (Options > Resource > Factory > Module > Annotation)
- Check if property is runtime-only (can't be in EAnnotation)
- Check if validation rule prevents it (check diagnostics)

**Issue: Type resolution fails**
- Check TypeStrategy (NONE disables type info)
- Check discriminator mapping (registered in MetadataService?)
- Check fallback chain (discriminator → hints → declared type)

**Issue: Feature not serialized/deserialized**
- Check visibility gates (ignore, ignoreWrite, ignoreRead)
- Check force flags (forceWrite, forceRead for volatile/transient)
- Check value gates (serializeNull, serializeEmpty, serializeDefault)

**Issue: Discriminator mapping not working**
- Check mapId extraction: is discriminatorMapId set on DiscriminatorConfig?
- Check fallback strategy: ERROR vs FALLBACK behavior
- Check supertype walking: does getMapIdForEClass() find the config?
- Check exception propagation: is IllegalStateException from ERROR strategy being re-thrown?

### 6.2 Diagnostic Inspection

All metadata parsing errors are collected in `DiagnosticCollector`:

```java
DiagnosticCollector diagnostics = ...;
for (Diagnostic diag : diagnostics.getDiagnostics()) {
    System.err.println(diag.getSeverity() + ": " + diag.getMessage());
}
```

## 7. Migration Notes (V1 → V2)

> See [`codec-v2-migration-notes.md`](codec-v2-migration-notes.md) for full migration details (package changes, API changes, EAnnotation changes).

## 8. Known Issues & Limitations

### 8.1 Current Limitations

1. **Cross-document containment references** — not yet supported (deser only)
2. **Custom Jackson modules** — limited integration
3. **Streaming mode** — not optimized for large documents

### 8.2 Spec Gaps (To Be Addressed)

1. Entry-build pattern not documented in spec §1.2
2. Deserialization gate shouldDeserialize() usage not explicit
3. isChangeable() pre-check not documented

### 8.3 Fixed Bugs

**2026-02-05:**
✅ **Discriminator mapping not using targeted resolve()** (TypeDeserializationEntry, CodecEObjectDeserializer)
- Now extracts mapId from DiscriminatorConfig and calls targeted `resolve(mapId, value, ...)`
- Fallback strategy (ERROR vs FALLBACK) now respected correctly

✅ **Exception propagation for ERROR strategy** (CodecEObjectDeserializer)
- IllegalStateException from unknown discriminator now re-thrown in replayDeferredValue() and deserializeContainedObject()

✅ **FeaturePathTypeResolver handling standard _type paths** (FeaturePathTypeResolver)
- Added `!isTypeKey(discriminatorPath)` guard to prevent interference with standard type key

✅ **Discriminator mapping serialization** (TypeSerializationEntry)
- Added resolveTypeMappingDiscriminator() that calls TypeDiscriminatorService.getDiscriminatorValue(mapId, eClass)
- Fixed hasDiscriminatorPath() to only suppress _type for nested paths

✅ **Supertype inheritance for discriminator maps** (TypeDiscriminatorService)
- getMapIdForEClass() now walks supertypes via getEAllSuperTypes()

**2026-02-04:**
✅ **forceRead not working for volatile features** (ConfigurationResolver:420)
- Only checked `isForceWrite()`, now checks `isForceRead()` too

✅ **forceWrite two-gate model** (AttributeSerializationEntry, ReferenceSerializationEntry)
- forceWrite now ONLY affects visibility gate, NOT value gate
- serializeNull/Empty/Default still apply with forceWrite=true

## 9. Gradle Commands

```bash
# Build everything
./gradlew build

# Test specific project
./gradlew :org.eclipse.fennec.codec:test
./gradlew :org.eclipse.fennec.codec.metadata:test
./gradlew :org.eclipse.fennec.codec.api:test

# Test specific test class
./gradlew :org.eclipse.fennec.codec:test --tests CodecResourceInlineMappingTest

# Clean build
./gradlew clean build

# Skip tests
./gradlew build -x test
```

**Note:** Do NOT use `testOSGi` for v2 projects (only for old codec).

## 10. Session Handoff Protocol

### 10.1 At Session Start

1. Read this document (section 0 "Active Task Hierarchy")
2. Check git status: `git status`, `git log --oneline -10`
3. Review "Next Session" in header
4. Ask user for clarification if needed

### 10.2 During Session

1. Update section 0.2 "Current Task Hierarchy" as work progresses
2. Mark tasks ✅ when complete
3. Add child tasks for nested investigations
4. Document decisions and blockers

### 10.3 At Session End

1. Update header: "Last Updated", "Session Summary", "Next Session"
2. Add new "COMPLETED" entry in section 0.2
3. Mark all tasks ✅
4. Commit changes: descriptive commit message

### 10.4 Current TODO List

**Plan E — Multi-Format Support (active, see `codec-v2-plans.md` §7 for details):**

Each step = one commit. See plans doc for sub-steps and files.

1. [ ] **Step 1:** FormatDelegate API interfaces (codec.api: TokenType, FormatDelegate, FormatReaderDelegate, CodecFormatProvider)
2. [ ] **Step 2:** TokenTypeMapper utility + tests
3. [ ] **Step 3:** FormatDelegateGenerator (write bridge, extends GeneratorBase) + tests
4. [ ] **Step 4:** FormatDelegateParser (read bridge, extends ParserBase) + tests
5. [ ] **Step 5:** JacksonStreamFormatDelegate (write impl) + tests
6. [ ] **Step 6:** JacksonStreamFormatReaderDelegate (read impl) + tests
7. [ ] **Step 7:** JacksonFormatProvider + tests
8. [ ] **Step 8:** Context fallback for non-JSON parsers (ContextHelper + deserializer)
9. [ ] **Step 9:** CodecResource format provider support (doSaveWithFormat/doLoadWithFormat)
10. [ ] **Step 10:** CodecFormatResourceFactory
11. [ ] **Step 11:** JSON FormatDelegate integration test (end-to-end round-trip)
12. [ ] **Step 12:** Feature parity test framework (AbstractFormatFeatureParityTest + JSON baseline)
13. [ ] **Step 13:** BSON project setup (bnd.bnd, empty project)
14. [ ] **Step 14:** BsonFormatDelegate (write) + tests
15. [ ] **Step 15:** BsonFormatReaderDelegate (read) + tests
16. [ ] **Step 16:** BsonFormatProvider + round-trip + parity tests
17. [ ] **Step 17+:** Additional Jackson formats (CBOR, YAML, Smile — one commit each)

**Deferred:**
- [ ] GAP-004: Diagnostic Options integration
- [ ] GAP-013: Enum-level annotation support
- [ ] GAP-014: inherit enum type mismatch
- [ ] DOC-001 through DOC-004: Documentation examples

## 11. Reference Information

> See [`codec-v2-reference.md`](codec-v2-reference.md) for full reference details (EMF concepts, terminology, Jackson integration, metadata service usage, config builder patterns, deprecated API audit + migration order).

---

## 12. Session Continuity Tips

If context is lost:

1. Read this document first: `docs/codec-v2-development-guide.md`
2. Check spec for details: `docs/codec-v2-serialization-spec.md`
3. Review current TODO state (if available)
4. Examine recent git commits for context
5. Ask user for clarification if needed

The key insight: **MetadataService parses EAnnotations at EPackage registration time and creates pre-computed aspect objects that the codec uses at serialization time.**

**For Discriminator Mapping:** All packages are registered with MetadataService before serialization/deserialization. When registering an EPackage, the MetadataService runs through codec aspects and updates the TypeDiscriminatorService. By the time deserialization happens, there's already a mapping from discriminator values (like "temp-sensor") to EClasses. The runtime uses targeted `resolve(mapId, value, ...)` for correct fallback strategy behavior (ERROR throws exception, FALLBACK returns null). Serialization performs reverse lookup via `getDiscriminatorValue(mapId, eClass)`. The system supports supertype inheritance (walks supertypes to find discriminator config) and root-level discriminator mapping (not just for contained references).
