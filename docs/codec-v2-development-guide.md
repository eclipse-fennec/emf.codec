# Codec V2 Development Guide

This document provides context for continuing codec development across sessions. It captures the goals, current state, and links to detailed architecture documentation.

**Last Updated:** 2026-03-05

**Session Summary (2026-03-05 latest):**

**Type Serialization Integration Tests & Bug Fixes:**

Created `CodecResourceTypeOptionsTest.java` (`org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/resource/`) — a comprehensive integration test suite exercising type serialization/deserialization through `CodecResource` save/load. The test file has 9 `@Nested` groups: NoneStrategy, NameStrategy, NumericStrategy, SchemaAndTypeStrategy, CustomTypeKey, StructuredFormat, PerClassTypeConfig, TypeScope (`@Disabled`), and PerReferenceTypeConfig. Uses `test-roundtrip.ecore` (Person, Address, Company) and `test-type-strategy.ecore`.

The tests exposed 5 bugs in the configuration and serialization/deserialization layers:

*Bug 1 — Per-EClass config not overriding global type strategy:*
- **Root cause:** `ConfigurationResolver.extractClassProperties()` only looked up `ConfigProperty.ECLASS_CONFIG.getKey()` (`"eClassConfig"`), but options passed via `CodecOptions.CODEC_ECLASS_CONFIG` use the prefixed key `"codec.eClassConfig"`. Same issue for `EREFERENCE_CONFIG` and `EATTRIBUTE_CONFIG` in `extractFeatureProperties()`.
- **Fix:** Added fallback to try `ConfigProperty.*.getPropertyKey()` (prefixed key) when the short key yields no result, in both `extractClassProperties` and `extractFeatureProperties`.
- **Files:** `ConfigurationResolver.java`

*Bug 2 — CODEC_ROOT_SCHEMA load option not working:*
- **Root cause:** `CodecResource.enrichWithOptions()` called `resolver.toBuilder().optionsProperties(options).build()`, which **replaced** the existing optionsProperties (containing typeKey, typeStrategy, etc.) with just the load options (containing only CODEC_ROOT_SCHEMA). All original type configuration was lost on load.
- **Fix:** Added `getOptionsProperties()` getter to `ConfigurationResolver`. Changed `enrichWithOptions` to **merge** existing options with load/save options (load options take precedence via `Map.putAll`).
- **Files:** `ConfigurationResolver.java`, `CodecResource.java`

*Bug 3 — NUMERIC strategy ignoring context schema hint:*
- **Root cause:** `TypeResolutionHelper.resolveFromNumeric(String, EClass)` only used `hintEClass` for EPackage lookup. When no hint class was provided (common case with CODEC_ROOT_SCHEMA), it fell through to scanning all registered packages — which is unreliable. The context schema URI from `CODEC_ROOT_SCHEMA` was never consulted.
- **Fix:** Added overload `resolveFromNumeric(String, EClass, String contextSchemaUri)` that tries: (1) hint class package, (2) context schema package, (3) all registered packages. Updated `TypeDeserializationEntry` NUMERIC case to pass `ContextHelper.getContextSchemaUri(ctxt)`.
- **Files:** `TypeResolutionHelper.java`, `TypeDeserializationEntry.java`

*Bug 4 — STRUCTURED format deserialization dropping all properties:*
- **Root cause:** `CodecEObjectDeserializer.readTypeValueAsString()` returned `null` when encountering `START_OBJECT` (a structured type value like `{"type":"Person","schema":"..."}`), without consuming the nested object. This left the Jackson parser positioned **inside** the type object, causing all subsequent top-level fields (name, age, etc.) to be misinterpreted or skipped.
- **Fix:** Added `readStructuredTypeObject()` method that properly parses the inner object, extracts `type`/`schema`/`classifier` values using configured inner keys, composes full URIs when schema is present (`nsURI#//ClassName`), handles `classifier` via `TypeResolutionHelper.resolveFromNumeric`, and correctly consumes the entire nested object. Also added `VALUE_NUMBER_INT` handling for plain numeric type values.
- **Files:** `CodecEObjectDeserializer.java`

*Bug 5 — Per-reference type config not overriding global:*
- **Root cause:** `resolveTypeConfig(EClass)` only merged GLOBAL and ECLASS level properties. No code path existed to layer feature-scoped properties from `CODEC_EREFERENCE_CONFIG` into type config resolution, even though `typeStrategy` is valid at FEATURE level per `ConfigProperty`.
- **Fix:** Added `resolveTypeConfig(EClass, EStructuralFeature, DiagnosticCollector)` to `ConfigurationResolver` that layers feature overrides on top of EClass-level resolution. Added `resolveTypeConfig(EClass, EStructuralFeature)` to `EffectiveCodecConfig`. Updated `CodecEObjectSerializer.serialize()` to read `ContextHelper.getCurrentSerializationReference(ctxt)` and use the feature-aware overload when a reference context exists.
- **Files:** `ConfigurationResolver.java`, `EffectiveCodecConfig.java`, `CodecEObjectSerializer.java`

---

**Session Summary (2026-03-04):**

**JSON Schema Deserialization Fixes (`JsonSchemaToEPackageConverter`):**

Comparison with gecko-codec's `EnhancedJsonSchemaToEPackageDeserializer` revealed several issues in the JSON Schema → EPackage deserialization. Full findings documented in `org.eclipse.fennec.codec.jsonschema/jsonschema-deserialization-findings.md`.

*Fix 1 — `allOf` inline property merging (data loss):*
- `createClassWithAllOf` only used the first non-`$ref` inline schema, silently dropping properties from subsequent inline schemas
- Now properly iterates ALL `allOf` elements, merges properties with deduplication, and accumulates `required` fields across all inline schemas
- Example: `allOf: [{$ref: Base}, {properties: {street}}, {properties: {city}}]` now correctly produces an EClass with both `street` and `city`

*Fix 2 — `anyOf` property-level reference resolution (class bloat + broken hierarchy):*
- Old approach (`createMultiValueReference` + `createParentFromCommonProperties`) eagerly created artificial parent classes from raw JSON nodes during initial processing, which:
  - Ignored existing common supertypes (e.g., Cat/Dog both extending Animal via `allOf`)
  - Created empty artificial classes when referenced schemas used `allOf` (no top-level `properties`)
  - Used `parentClassMaps` with fragile `Map<String, JsonNode>` cache key
  - Never populated `anyOfRefMap`, leaving artificial parents disconnected from the type hierarchy
- New deferred approach:
  - `createMultiValueReference` now records `DeferredAnyOfReference` entries instead of eagerly resolving
  - `resolveAnyOfPropertyReferences()` runs AFTER `resolveAllOfReferences()` so the full inheritance hierarchy is available
  - Walks the supertype chains to find the **lowest common supertype** via `findLowestCommonSupertype()`
  - If a common supertype exists (e.g., Animal), uses it directly — no artificial class created
  - If no common supertype exists, creates an artificial parent, extracts common features (by name + type) from the target classes into it, removes the originals from target classes, and wires target classes as subtypes
  - Uses sorted target name list as cache key for order-independent reuse across identical `anyOf` sets
- Removed `parentClassMaps` field and `createParentFromCommonProperties` method

*Use cases now covered (with tests in `NewFeaturesTest`):*

| Test | Scenario |
|------|----------|
| `allOfMergesMultipleInlineSchemas` | allOf with 3 inline schemas — all properties merged, required accumulated |
| `allOfMergesInlineSchemasWithRefs` | allOf with $ref parent + 2 inline schemas — supertype resolved, properties merged |
| `allOfDeduplicatesProperties` | allOf with overlapping property names — no duplicate features |
| `anyOfSameRefsReusesType` | Two properties with identical anyOf [Cat, Dog] — same type reused |
| `anyOfCommonPropertiesInDifferentOrderReusesType` | Cat defines {name, age, purrs}, Dog defines {age, name, barks} — common features {name, age} extracted regardless of property order |
| `anyOfResolvesToExistingCommonSupertype` | Cat/Dog extend Animal via allOf — anyOf resolves to Animal, no artificial class |
| `anyOfResolvesToCommonSupertypeDefinedAfterSubtypes` | Animal defined AFTER Cat/Dog in schema — deferred resolution still finds Animal |
| `anyOfNoCommonSupertypeCreatesArtificialParent` | Circle/Rectangle with no shared supertype — artificial parent created, both wired as subtypes |
| `anyOfFindsLowestCommonAncestor` | Animal → Mammal → Cat/Dog — anyOf resolves to Mammal (lowest), not Animal |

*Remaining known issues (documented in findings, not yet fixed):*
- Issue 3: Multi-type `"null"` (e.g., `"type": ["string", "null"]`) creates 3 artificial classes instead of a nullable EAttribute
- Issue 4: Context-specific variant base class always created even with 0 common properties
- Issue 5: `$ref` default containment is `false` (should be `true` to match typical JSON Schema semantics)

---

**Session Summary (2026-03-02 latest):**

**Custom Properties for Format-Specific Options:**
- Introduced generic `customProperties` map on `EffectiveCodecConfig` — automatically collects all `codec.*` load/save options that don't match known `ConfigProperty` keys or runtime-only options
- Replaced hard-coded `allFieldsRequired`, `useAnchorRefs`, `flatAllOf` boolean fields on `EffectiveCodecConfig` with the generic `customProperties` approach
- Removed `getConverterOptions()` from the API interface in favor of `getCustomProperties()`
- Updated `CodecResource.extractCustomProperties()` — filters options by `codec.*` prefix, excluding known `ConfigProperty` keys and runtime options
- Updated `CodecModule` to pass `customProperties` through to `EffectiveCodecConfig.Builder`
- Value writers (`EClassValueWriter`, `EPackageValueWriter`) now use `ctx.getConfig().getCustomProperties()`

**JSON Schema Option Key Migration:**
- Migrated all JSON Schema option constants in `CodecJsonSchemaOptions` to use `codec.jsonschema.*` prefix:
  - `"useAnchorRefs"` → `"codec.jsonschema.useAnchorRefs"`
  - `"allFieldsRequired"` → `"codec.jsonschema.allFieldsRequired"`
  - `"flatAllOf"` → `"codec.jsonschema.flatAllOf"`
- Added `OPTION_USE_NAMES_FROM_EXTENDED_METADATA` (`"codec.jsonschema.useNamesFromExtendedMetadata"`)
- Added `OPTION_SUPPRESS_KEYWORDS` (`"codec.jsonschema.suppressKeywords"`) — `Collection<String>` of JSON Schema keywords to suppress in output

**JSON Schema Bug Fix — Missing `items` for Arrays:**
- Fixed `EPackageToJsonSchemaConverter.writeMultiValuedAttribute()` — `items` property was only written when a `@jsonschema(items="true")` annotation was present; now always emitted for multi-valued attributes

**JSON Schema Keyword Suppression:**
- `OPTION_SUPPRESS_KEYWORDS` allows suppressing specific JSON Schema keywords (e.g., `maxItems`, `minItems`, `description`, `additionalProperties`, `$comment`, `deprecated`, `writeOnly`, `uniqueItems`, `format`)
- Useful for generating schemas compatible with AI structured-output APIs that don't support certain keywords
- Applied `isSuppressed()` guards across all keyword write sites in `EPackageToJsonSchemaConverter`

**New Tests:**
- `MultiValuedAttributeTests` — 7 tests: string/int/boolean/double/enum arrays, bounded arrays, containment reference arrays
- `SuppressKeywordsTests` — 6 tests: suppress maxItems, minItems, description, additionalProperties, $comment, multiple keywords

**Documentation:**
- Updated `jsonschema-architecture.md` — options table with full `codec.jsonschema.*` keys, custom properties integration section, keyword suppression section, array items section
- Updated `codec-v2-development-guide.md` — session summary, task hierarchy, test counts

**Previous Session Summary (2026-02-25):**

**OSGi Integration:**

- Added `MetadataServiceComponent` in `org.eclipse.fennec.model.metadata` bundle — DS component exposing `MetadataWhiteboard` and `MetadataService` as OSGi services; has `@Reference(MULTIPLE, DYNAMIC)` for `EPackage`, `AspectProvider`, `MetadataIndex` (OPTIONAL), and `MetadataHandler` whiteboard entries

- Added `CodecAspectProviderComponent` in `org.eclipse.fennec.codec.metadata` bundle — DS component extending `CodecAspectProvider`, registered as `AspectProvider` OSGi service; automatically picked up by `MetadataServiceComponent` and applied to all registered EPackages

- Added `org.eclipse.fennec.codec.osgi.tests` bundle with 15 OSGi integration tests mirroring the `org.eclipse.fennec.codec.examples` suite; tests use `@InjectService MetadataService`, `@InjectBundleContext BundleContext`, and `ctx.registerService(EPackage.class, pkg, null)` — no manual `MetadataServiceFactory.create()` or `metadataService.registerPackage(pkg)` calls

- Special pattern for `ExternalTypeDiscriminatorExample`: registers `TypeDiscriminatorService` as `MetadataHandler` OSGi service _before_ the EPackage so that `MetadataServiceComponent.addHandler()` notifies it of package events automatically

**Package restructuring (required for OSGi bndrun resolution and to avoid `ClassNotFoundException` at runtime):**

- Exported package `org.eclipse.fennec.model.metadata.service` in `org.eclipse.fennec.model.metadata` bnd.bnd — it was missing from the exported API, causing the bndrun resolver to fail
- Renamed package `org.eclipse.fennec.codec.value` → `org.eclipse.fennec.codec.value.impl` in the `org.eclipse.fennec.codec` bundle to eliminate split-package conflict with the identically named package in `org.eclipse.fennec.codec.api`
- Moved classes in `org.eclipse.fennec.codec.format` (in the `org.eclipse.fennec.codec` bundle) to `org.eclipse.fennec.codec.format.impl` for the same reason — the public API types (`FormatDelegate`, `FormatReaderDelegate`, `CodecFormatProvider`, `TokenType`) remain in `codec.api`, while the implementation bridge classes (`FormatDelegateGenerator`, `FormatDelegateParser`, `JacksonFormatProvider`) now live in the `.impl` sub-package of the codec bundle

**New bundle — `org.eclipse.fennec.codec.workspace.library`:**

- Provides all codec dependencies as an OSGi library bundle for workspace consumption
- Added Fennec Common Models Library `org.eclipse.fennec.models:org.eclipse.fennec.common.models.library:0.0.1-SNAPSHOT` to be able to use `org.geojson.model` from there
- Removed `org.geojson.model` from local folder (not needed anymore, as we use the one provided by the Fennec Common Models library)

**Jackson Dependency upgrades:**
- Upgraded Jackson from 3.0.2 to 3.1.0

- Upgraded jackson-annotations from 2.20 to 2.21

- Upgraded org.snakeyaml:snakeyaml-engine from 2.10 to 3.0.1

  

**Previous Session Summary (2026-02-24):**

**JSON Schema enhancements:**
- Added `EClassValueReader` / `EClassValueWriter` (embed single-class JSON Schema in other formats)
- Added `EClassToJsonSchemaConverter` / `JsonSchemaToEClassConverter` (thin wrapper converters)
- Refactored `EPackageToJsonSchemaConverter.writeEClass` → `writeEClass` + `writeEClassContent`; added `convertEClass` + `writeEClassDocumentMetadata`
- Added `convertToEClass` to `JsonSchemaToEPackageConverter`; preserves `title` as `originalTitle` annotation for round-trip fidelity
- Added `OPTION_ALL_FIELDS_REQUIRED` option (`"allFieldsRequired"`) — marks every property required, for AI structured-output schemas
- Added `EClassValueHandlerTest` (ReaderTests, WriterTests, RoundTripTests) and `AllFieldsRequiredTests` in `NewFeaturesTest`
- Fixed `withoutOption_onlyMandatoryFeaturesRequired` test: replaced fragile string-position check with Jackson JSON parsing + `requiredContains()` helper
- Created `org.eclipse.fennec.codec.jsonschema/jsonschema-architecture.md` documenting both usage modes, converter classes, all options, annotation mapping, and three open issues (OI-1: CodecResource bypass, OI-2: no OSGi whiteboard, OI-3: no default $schema)

**Previous Session Summary (2026-02-17):**

**Plan F TCK Test Suite — COMPLETE:**

Comprehensive Technology Compatibility Kit (TCK) for all format providers. Three phases of abstract TCK classes verified across BSON, CBOR, and YAML formats.

*TCK Phases:*
- **P0 Core Round-Trip** (Phase 1): Basic attribute types, containment/non-containment references, enums, multi-valued attributes, complex round-trip
- **P1 Feature Strategies** (Phase 2): Type strategy, ID strategy, enum strategy, polymorphism, reference format, value handling, custom key
- **P2 Advanced Features** (Phase 3): EMap, SuperType, visibility, force read/write, global ignore, strictness, array root, large payloads, extended metadata, custom value reader/writer

*Bug Fixes During TCK Development:*
- **Array root serialization** — `CodecResource.doSaveWithFormat()` now handles multiple root objects correctly (iterates individually within array start/end)
- **`CodecFormatProvider.supportsArrayRoot()`** — New capability method; `BsonFormatProvider` returns `false` (single-document format), throws `IOException` on multi-root save
- **BSON reader infinite loop** — `BsonFormatReaderDelegate` now tracks `valueConsumed` flag to skip unconsumed primitive values when `nextToken()` is called without reading. Verified with 11 dedicated tests covering all BSON value types.

**Previous Session Summary (2026-02-16):**

**Plan E Multi-Format Support — COMPLETE:**

All steps implemented and verified. Four format providers operational: JSON (default), BSON, CBOR, YAML.

*Architecture:*
- `FormatDelegate<T>` / `FormatReaderDelegate<S>` — format-agnostic write/read interfaces
- `FormatDelegateGenerator<T>` / `FormatDelegateParser<S>` — Jackson bridge (extends GeneratorBase/ParserBase)
- `JacksonFormatProvider` — handles any Jackson-based streaming format (JSON, CBOR, YAML, Smile)
- `BsonFormatProvider` — handles BSON via in-memory BsonDocument with binary stream conversion
- `CodecResource` — optional `CodecFormatProvider` field; when set, uses FormatDelegate path

*Format Providers:*

| Project | Provider | Type | Tests |
|---------|----------|------|-------|
| `org.eclipse.fennec.codec` | (default JSON, no FormatDelegate) | Jackson native | ~1,231 |
| `org.eclipse.fennec.codec.bson` | `BsonFormatProvider` | In-memory BsonDocument | 98 |
| `org.eclipse.fennec.codec.cbor` | `CborFormatProvider` extends `JacksonFormatProvider` | Binary (CBORFactory) | 53 |
| `org.eclipse.fennec.codec.yaml` | `YamlFormatProvider` extends `JacksonFormatProvider` | Text (YAMLFactory) | 53 |

*Key design decisions:*
- JSON is NOT migrated to FormatDelegate. The original `CodecJsonFactory` → Jackson native path remains the default. FormatDelegate is an additional path, verified to produce identical output.
- `BsonFormatProvider` implements `CodecFormatProvider<InputStream, OutputStream>` (not `<BsonDocument, BsonDocument>`) so it works with CodecResource's stream-based save/load API.
- CBOR and YAML providers are one-class projects — they just extend `JacksonFormatProvider` with the respective factory.
- YAML requires `org.snakeyaml.engine` as transitive dependency of `jackson-dataformat-yaml`.

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

**Next Session:** Plan E complete. All format providers (BSON, CBOR, YAML) operational. Possible next work: Plan B remaining GAPs, documentation, Smile format, or new features.

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

COMPLETED: Custom Properties + JSON Schema Enhancements - ✅ (2026-03-02)
│  - Generic customProperties map on EffectiveCodecConfig (replaces hard-coded format-specific fields)
│  - CodecResource.extractCustomProperties() — auto-collects codec.* options not matching known keys
│  - CodecModule passes customProperties through to EffectiveCodecConfig
│  - JSON Schema option keys migrated to codec.jsonschema.* namespace
│  - Fixed missing array items in EPackageToJsonSchemaConverter
│  - Added OPTION_SUPPRESS_KEYWORDS for keyword suppression
│  - 13 new tests (7 array + 6 suppression)
│  - Updated jsonschema-architecture.md and codec-v2-development-guide.md

COMPLETED: OSGi Facades + Integration Tests + Package Restructuring - ✅ (2026-02-25)
│  - MetadataServiceComponent (org.eclipse.fennec.model.metadata): DS whiteboard for EPackage/AspectProvider/MetadataHandler
│  - CodecAspectProviderComponent (org.eclipse.fennec.codec.metadata): DS AspectProvider service
│  - org.eclipse.fennec.codec.osgi.tests: 15 OSGi integration tests with @InjectService MetadataService
│  - Exported org.eclipse.fennec.model.metadata.service (was missing, blocked bndrun resolver)
│  - Renamed codec.value → codec.value.impl (split-package conflict with codec.api)
│  - Moved codec.format impl classes → codec.format.impl (split-package conflict with codec.api)

COMPLETED: workspace.library bundle - ✅ (2026-02-25)
│  - Added org.eclipse.fennec.codec.workspace.library (OSGi library bundle for codec deps)

COMPLETED: Jackson dependency upgrades - ✅ (2026-02-25)
│  - Jackson 3.0.1 → 3.1.0
│  - jackson-annotations 2.20 → 2.21


COMPLETED: JSON Schema EClass Handlers + allFieldsRequired + docs - ✅ (2026-02-24)
│  - EClassValueReader / EClassValueWriter (embed single-class JSON Schema)
│  - EClassToJsonSchemaConverter / JsonSchemaToEClassConverter (thin wrappers)
│  - EPackageToJsonSchemaConverter: writeEClassContent refactor, convertEClass, OPTION_ALL_FIELDS_REQUIRED
│  - JsonSchemaToEPackageConverter: convertToEClass + originalTitle annotation preservation
│  - EClassValueHandlerTest + AllFieldsRequiredTests; fixed test assertion bug
│  - jsonschema-architecture.md created

COMPLETED: Plan F TCK Test Suite - ✅ (2026-02-17)
│
│  Phase F1 (P0 Core): Abstract round-trip TCKs - ✅
│  │  - 6 suites: attributes, containment refs, non-containment refs, enums, multi-valued, complex
│  Phase F2 (P1 Features): Feature strategy TCKs - ✅
│  │  - 7 suites: type, ID, enum, polymorphism, reference, value handling, custom key
│  Phase F3 (P2 Advanced): Advanced feature TCKs - ✅
│  │  - 10 suites: EMap, SuperType, visibility, force, global ignore, strictness,
│  │    array root, large payload, extended metadata, custom value
│  │  - 8 ecore models + 10 abstract TCK classes + 30 format subclasses
│  Bug Fixes: - ✅
│  │  - Array root serialization in CodecResource.doSaveWithFormat()
│  │  - CodecFormatProvider.supportsArrayRoot() capability method
│  │  - BsonFormatReaderDelegate valueConsumed fix (+ 11 regression tests)
│
COMPLETED: Plan E Multi-Format Support - ✅ (2026-02-16)
│  - BSON: BsonFormatProvider (98 tests)
│  - CBOR: CborFormatProvider (53 tests)
│  - YAML: YamlFormatProvider (53 tests)
│
PREVIOUS: Plan B + Plan D Complete - ✅ (2026-02-08)
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
| **MetadataServiceComponent** | `model.metadata.service` | DS OSGi facade: whiteboard for EPackage/AspectProvider/MetadataHandler services |
| **CodecAspectProvider** | `codec.metadata.provider` | Parses EAnnotations → Config objects |
| **CodecAspectProviderComponent** | `codec.metadata.provider` | DS OSGi facade: registers `CodecAspectProvider` as `AspectProvider` service |
| **TypeDiscriminatorService** | `codec.metadata.type` | Manages discriminator→EClass mappings |
| **EffectiveCodecConfig** | `codec.api.config.effective` | Per-feature config resolution |
| **CodecEObjectSerializer** | `codec.ser` | Orchestrates serialization entries |
| **CodecEObjectDeserializer** | `codec.deser` | Orchestrates deserialization entries |
| **SerializationEntry** | `codec.ser` | Type/ID/Feature/Reference serializers |
| **DeserializationEntry** | `codec.deser` | Type/ID/Feature/Reference deserializers |
| **FormatDelegate\<T\>** | `codec.api.format` | Format-agnostic write interface |
| **FormatReaderDelegate\<S\>** | `codec.api.format` | Format-agnostic read interface |
| **FormatDelegateGenerator\<T\>** | `codec.format.impl` | Jackson bridge: wraps FormatDelegate as JsonGenerator |
| **FormatDelegateParser\<S\>** | `codec.format.impl` | Jackson bridge: wraps FormatReaderDelegate as JsonParser |
| **JacksonFormatProvider** | `codec.format.impl` | Factory for Jackson-based formats (JSON, CBOR, YAML) |
| **BsonFormatProvider** | `codec.bson` | Factory for BSON format (in-memory BsonDocument) |

### 2.3 Configuration Hierarchy (5 Sources)

From highest to lowest priority:

1. **SaveOptions/LoadOptions** (per-operation)
2. **Resource options** (per-resource)
3. **ResourceFactory options** (per-factory)
4. **CodecModule** (global config)
5. **EAnnotation** (model metadata)

See `docs/codec-v2-spec/02-config-resolution.md` for details.

## 3. Current State (2026-03-02)

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

✅ **Multi-Format Support (Plan E)**
- FormatDelegate abstraction: `FormatDelegate<T>`, `FormatReaderDelegate<S>`, `CodecFormatProvider<S,T>`
- Jackson bridge: `FormatDelegateGenerator<T>` (extends GeneratorBase), `FormatDelegateParser<S>` (extends ParserBase)
- BSON format: `BsonFormatDelegate`, `BsonFormatReaderDelegate`, `BsonFormatProvider` (98 tests)
- CBOR format: `CborFormatProvider` extends `JacksonFormatProvider` (53 tests)
- YAML format: `YamlFormatProvider` extends `JacksonFormatProvider` (53 tests)
- Feature parity verified: `AbstractFormatFeatureParityTest` + `JsonFormatFeatureParityTest`
- CodecResource: optional `CodecFormatProvider` field, `doSaveWithFormat()`/`doLoadWithFormat()`
- `CodecFormatProvider.supportsArrayRoot()` — capability check (BSON returns false)

✅ **Custom Properties for Format-Specific Options (2026-03-02)**
- `EffectiveCodecConfig.getCustomProperties()` — generic `Map<String, Object>` for format-specific options
- `CodecResource.extractCustomProperties()` — auto-collects `codec.*` options not matching known `ConfigProperty` or runtime keys
- `CodecModule.Builder.customProperties()` — passes through to `EffectiveCodecConfig`
- JSON Schema options use `codec.jsonschema.*` namespace: `allFieldsRequired`, `useAnchorRefs`, `flatAllOf`, `useNamesFromExtendedMetadata`, `suppressKeywords`
- `OPTION_SUPPRESS_KEYWORDS` — suppress specific JSON Schema keywords in output (e.g., `maxItems`, `description`)
- Array `items` property now always emitted for multi-valued attributes (was previously annotation-gated)

✅ **OSGi Integration (2026-02-25)**
- `MetadataServiceComponent` — DS component in `org.eclipse.fennec.model.metadata`; exposes `MetadataWhiteboard` and `MetadataService` as OSGi services; whiteboard references: `EPackage` (MULTIPLE, DYNAMIC), `AspectProvider` (MULTIPLE, DYNAMIC), `MetadataIndex` (OPTIONAL), `MetadataHandler` (MULTIPLE, DYNAMIC)
- `CodecAspectProviderComponent` — DS component in `org.eclipse.fennec.codec.metadata`; registered as `AspectProvider` OSGi service; bound automatically by `MetadataServiceComponent`
- `org.eclipse.fennec.codec.osgi.tests` — 15 OSGi integration tests using `@InjectService`/`@InjectBundleContext`; EPackages registered as OSGi services (`ctx.registerService(EPackage.class, pkg, null)`) rather than via `MetadataServiceFactory`
- Package split fixes: `org.eclipse.fennec.model.metadata.service` exported; `codec.value` impl classes moved to `codec.value.impl`; `codec.format` impl classes moved to `codec.format.impl`
- `test.bndrun` explicitly requires `org.eclipse.fennec.codec.metadata` (DYNAMIC reference not auto-resolved by bnd)

✅ **TCK Test Suite (Plan F)**
- 18 abstract TCK classes in `org.eclipse.fennec.codec.tests` covering all codec features
- 8 dedicated ecore test models for TCK scenarios
- P0: 6 core round-trip suites (attributes, references, enums, multi-valued, complex)
- P1: 7 feature strategy suites (type, ID, enum, polymorphism, reference, value handling, custom key)
- P2: 10 advanced feature suites (EMap, SuperType, visibility, force, global ignore, strictness, array root, large payload, extended metadata, custom value)
- Each format (BSON, CBOR, YAML) has all 18 TCK suites as concrete subclasses
- BSON additionally has 11 unconsumed-value regression tests verifying the `valueConsumed` fix

✅ **Deprecated Code Removed**
- All `old codec.v2.*` source/test files deleted (55 src + 9 test)
- All `codec.api.value.*` and `codec.api.diagnostic.*` files deleted (11 src + 14 test)
- Zero skipped tests remaining

### 3.2 Test Status

**Current Counts (2026-03-02, after custom properties + JSON Schema enhancements):**

| Project | Tests | Suites | Notes |
|---------|------:|-------:|-------|
| `org.eclipse.fennec.codec.api` | 1,039 | 179 | |
| `org.eclipse.fennec.codec` | 1,231 | 373 | |
| `org.eclipse.fennec.codec.bson` | 98 | 33 | |
| `org.eclipse.fennec.codec.cbor` | 53 | 24 | |
| `org.eclipse.fennec.codec.yaml` | 53 | 24 | |
| `org.eclipse.fennec.codec.metadata` | 255 | 61 | |
| `org.eclipse.fennec.codec.geojson` | 34 | 13 | |
| `org.eclipse.fennec.codec.jsonschema` | 146 | 43 | +13 new array/suppression tests |
| `org.eclipse.fennec.codec.openapi` | 75 | 34 | |
| `org.eclipse.fennec.codec.osgi.tests` | 15+ | 15 | OSGi integration tests |
| **Total** | **3,061+** | **799+** | |

All tests pass with 0 failures, 0 errors, 0 skipped.

**Test Organization:**
- `org.eclipse.fennec.codec.api/test` — config API tests (spec + resolver tests)
- `org.eclipse.fennec.codec.metadata/test` — aspect provider tests + TypeDiscriminatorServiceTest
- `org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/*` — runtime tests (all active)
- `org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/util/*` — helper unit tests
- `org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/format/*` — FormatDelegate parity + integration tests
- `org.eclipse.fennec.codec.tests/src` — abstract TCK classes + ecore models (shared test infrastructure)
- `org.eclipse.fennec.codec.bson/test` — BSON format delegate + provider + TCK tests
- `org.eclipse.fennec.codec.cbor/test` — CBOR TCK tests
- `org.eclipse.fennec.codec.yaml/test` — YAML TCK tests

### 3.3 What's Next

**COMPLETED:**
1. **Plan E: Multi-Format Support** — ✅ COMPLETE (BSON, CBOR, YAML all operational)
   - FormatDelegate abstraction fully operational with 4 format providers
   - Total format tests: 204 (BSON 98 + CBOR 53 + YAML 53) + JSON parity tests in codec project

2. **Plan F: TCK Test Suite** — ✅ COMPLETE (18 abstract TCK classes × 3 formats)
   - P0 Core: 6 round-trip suites
   - P1 Features: 7 strategy suites
   - P2 Advanced: 10 advanced feature suites
   - Bug fixes: array root serialization, `supportsArrayRoot()` capability, BSON reader `valueConsumed` fix

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

**JSON Schema:**
- `org.eclipse.fennec.codec.jsonschema/jsonschema-architecture.md`

**Development:**
- `docs/codec-v2-development-guide.md` (this file)
- `docs/codec-v2-plans.md` (roadmap, GAP analysis)
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

**TCK Tests** (codec.tests + format projects):
- Abstract TCK classes define format-agnostic test logic with `createFormatProvider()` + `getFileExtension()` hooks
- Each format project provides concrete subclasses (e.g., `BsonEMapTCKTest extends AbstractEMapTCK`)
- Dedicated ecore models per TCK topic (in `codec.tests/src/.../tck/`)
- Three priority tiers: P0 (core round-trip), P1 (feature strategies), P2 (advanced features)
- Tests use `ConfigurationResolver.builder()` for per-test configuration
- Format capability checks via `CodecFormatProvider.supportsArrayRoot()` for graceful test adaptation

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

## 7. Known Issues & Limitations

### 7.1 Current Limitations

1. **Cross-document containment references** — not yet supported (deser only)
2. **Custom Jackson modules** — limited integration
3. **Streaming mode** — not optimized for large documents

### 7.2 Spec Gaps (To Be Addressed)

1. Entry-build pattern not documented in spec §1.2
2. Deserialization gate shouldDeserialize() usage not explicit
3. isChangeable() pre-check not documented

### 7.3 Fixed Bugs

**2026-02-17:**
✅ **Array root serialization crash** (CodecResource)
- `doSaveWithFormat()` passed `EObject[]` to `ObjectWriter` typed for `EObject` → `InvalidDefinitionException`
- Fix: iterate objects individually within array start/end when multiple root objects present

✅ **BSON multi-root not supported** (BsonFormatProvider, CodecFormatProvider)
- Added `supportsArrayRoot()` to `CodecFormatProvider` interface (default `true`)
- `BsonFormatProvider` overrides to return `false` — throws `IOException` on multi-root save
- `AbstractArrayRootTCK` tests both supported (round-trip) and unsupported (assertThrows) paths

✅ **BSON reader infinite loop on unconsumed values** (BsonFormatReaderDelegate)
- BSON's state machine requires explicit value consumption (unlike Jackson streaming parsers)
- When codec skipped unknown fields by calling `nextToken()` without reading, reader stayed in VALUE state forever
- Fix: `valueConsumed` boolean flag — set `false` in `handleBsonType()` for primitives, set `true` in all read methods
- At start of `nextToken()`, if `!valueConsumed && state == VALUE`, calls `reader.skipValue()` to advance
- Verified with 11 dedicated tests covering all BSON value types (string, int, long, double, decimal128, boolean, binary, ObjectId, multiple consecutive, array context, nested SuperType scenario)

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

## 8. Gradle Commands

```bash
# Build everything
./gradlew build

# Test specific project
./gradlew :org.eclipse.fennec.codec:test
./gradlew :org.eclipse.fennec.codec.metadata:test
./gradlew :org.eclipse.fennec.codec.api:test
./gradlew :org.eclipse.fennec.codec.bson:test
./gradlew :org.eclipse.fennec.codec.cbor:test
./gradlew :org.eclipse.fennec.codec.yaml:test

# Test specific test class
./gradlew :org.eclipse.fennec.codec:test --tests CodecResourceInlineMappingTest

# Clean build
./gradlew clean build

# Skip tests
./gradlew build -x test
```

**Note:** Do NOT use `testOSGi` for v2 projects (only for old codec).

## 9. Session Handoff Protocol

### 9.1 At Session Start

1. Read this document (section 0 "Active Task Hierarchy")
2. Check git status: `git status`, `git log --oneline -10`
3. Review "Next Session" in header
4. Ask user for clarification if needed

### 9.2 During Session

1. Update section 0.2 "Current Task Hierarchy" as work progresses
2. Mark tasks ✅ when complete
3. Add child tasks for nested investigations
4. Document decisions and blockers

### 9.3 At Session End

1. Update header: "Last Updated", "Session Summary", "Next Session"
2. Add new "COMPLETED" entry in section 0.2
3. Mark all tasks ✅
4. Commit changes: descriptive commit message

### 9.4 Current TODO List

**Plan E — Multi-Format Support:** ✅ COMPLETE (see `codec-v2-plans.md` §7)

**Plan F — TCK Test Suite:** ✅ COMPLETE

| Phase | Suites | Status |
|-------|--------|--------|
| P0 Core Round-Trip | 6 abstract TCK classes | ✅ |
| P1 Feature Strategies | 7 abstract TCK classes | ✅ |
| P2 Advanced Features | 10 abstract TCK classes + 8 ecore models | ✅ |
| Format Subclasses | 18 × 3 formats = 54 concrete test classes | ✅ |
| Bug Fixes | Array root, supportsArrayRoot, BSON valueConsumed | ✅ |

**TCK Abstract Classes** (in `org.eclipse.fennec.codec.tests`):
- P0: `AbstractRoundTripTCK` (6 suites for basic types/refs/enums)
- P1: `Abstract{TypeStrategy,IdStrategy,EnumStrategy,Polymorphism,ReferenceFormat,ValueHandling,CustomKey}TCK`
- P2: `Abstract{EMap,SuperType,Visibility,ForceReadWrite,GlobalIgnore,Strictness,ArrayRoot,LargePayload,ExtendedMetaData,CustomValue}TCK`

**Completed this session (2026-03-02):**
- [✅] Custom properties: generic `customProperties` map on `EffectiveCodecConfig` (replaces hard-coded format fields)
- [✅] `CodecResource.extractCustomProperties()` — auto-collects `codec.*` options
- [✅] JSON Schema option key migration to `codec.jsonschema.*` namespace
- [✅] Fixed missing array `items` in `EPackageToJsonSchemaConverter`
- [✅] `OPTION_SUPPRESS_KEYWORDS` — keyword suppression for JSON Schema output
- [✅] 13 new tests (7 array + 6 suppression)
- [✅] Updated `jsonschema-architecture.md` and `codec-v2-development-guide.md`

**Completed previous session (2026-02-25):**
- [✅] OSGi facades: `MetadataServiceComponent`, `CodecAspectProviderComponent`
- [✅] OSGi integration tests: `org.eclipse.fennec.codec.osgi.tests` (15 test classes)
- [✅] Package split fixes: exported `model.metadata.service`; `codec.value.impl`; `codec.format.impl`
- [✅] DOC-001 through DOC-004: OSGi integration examples (covered by osgi.tests bundle)

**Deferred:**
- [ ] GAP-004: Diagnostic Options integration
- [ ] GAP-013: Enum-level annotation support
- [ ] GAP-014: inherit enum type mismatch
- [ ] Smile format: `org.eclipse.fennec.codec.smile` — add when demand arises

## 10. Reference Information

> See [`codec-v2-reference.md`](codec-v2-reference.md) for full reference details (EMF concepts, terminology, Jackson integration, metadata service usage, config builder patterns, deprecated API audit + migration order).

---

## 11. Session Continuity Tips

If context is lost:

1. Read this document first: `docs/codec-v2-development-guide.md`
2. Check spec for details: `docs/codec-v2-serialization-spec.md`
3. Review current TODO state (if available)
4. Examine recent git commits for context
5. Ask user for clarification if needed

The key insight: **MetadataService parses EAnnotations at EPackage registration time and creates pre-computed aspect objects that the codec uses at serialization time.**

**For Discriminator Mapping:** All packages are registered with MetadataService before serialization/deserialization. When registering an EPackage, the MetadataService runs through codec aspects and updates the TypeDiscriminatorService. By the time deserialization happens, there's already a mapping from discriminator values (like "temp-sensor") to EClasses. The runtime uses targeted `resolve(mapId, value, ...)` for correct fallback strategy behavior (ERROR throws exception, FALLBACK returns null). Serialization performs reverse lookup via `getDiscriminatorValue(mapId, eClass)`. The system supports supertype inheritance (walks supertypes to find discriminator config) and root-level discriminator mapping (not just for contained references).
