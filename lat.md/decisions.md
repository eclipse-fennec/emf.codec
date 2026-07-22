# Design Decisions

The load-bearing decisions behind codec-v2 and the reasoning for each. When changing code, check whether the change fights one of these — if it does, the decision should be revisited explicitly, not eroded.

## Spec-First Development

The specification in `docs/codec-v2-spec/` is the source of truth; implementation follows it, not the other way around. Behavior changes require a spec change first, and tests are written against spec sections (TDD).

Consequence: when code and spec disagree, the spec wins by default — and either the code is fixed or the spec is deliberately amended in the same change.

## Metadata as a Shadow EMF Model

Codec configuration is parsed from EAnnotations **once at EPackage registration** into a "shadow model" of aspects, instead of re-reading annotations during serialization.

The metadata layer is itself an EMF model (`metadata.ecore` extended by `codec.ecore`).

**Why:** (1) performance — string-parsing EAnnotations in hot load/save paths is slow; (2) separation of concerns — domain models carry no technical logic; (3) third-party Ecore models that cannot be annotated can still get metadata attached externally; (4) other concerns (ORM, historization) reuse the same infrastructure with their own aspect types. See [[concepts#Aspects and the Shadow Model]] and the full spec in `org.eclipse.fennec.model.metadata/model-metadata-architecture.md`. Note `org.eclipse.fennec.codec.metadata` deliberately has no dependency on the codec runtime.

## Immutable Effective Configuration

All six config sources are merged **up front** into one immutable, lazily-cached `EffectiveCodecConfig` per operation. Serialization/deserialization entries receive pre-merged per-feature config and contain zero fallback logic.

**Why:** resolution rules live in exactly one place (`ConfigurationResolver` + the config `mergeWith()` chain), entries stay simple, and a config lookup during serialization is a map hit, not a six-level search. See [[configuration#Two Dimensions]].

## Order-Independent Parsing

Deserialization must not depend on JSON key order: data fields arriving before `_type`/`_id` are buffered ("deferred properties") and replayed once the type is resolved.

**Why:** the JSON spec guarantees no key order, external producers differ, and requiring metadata-first would make hand-authored JSON fragile. Mechanism: [[architecture#Deferred Properties]].

## Format-Agnostic Core on Jackson 3

There is exactly one codec engine, speaking Jackson 3's streaming API; other formats implement thin `FormatDelegate` bridges rather than their own codecs. Even non-Jackson formats (BSON) plug in through the same interface.

**Why:** every codec feature (types, IDs, references, discriminators, value readers) works in every format automatically, verified by the shared TCK ([[testing#TCK Suites]]). The alternative — per-format codecs — was the source of drift the TCK exists to prevent. See [[formats#Format Abstraction]].

## One Runtime Value-Binding Mechanism

For binding custom value readers/writers at load/save time, exactly one mechanism is supported: **instances** via `CODEC_FEATURE_VALUE_READER_INSTANCES`/`_WRITER_INSTANCES`.

Name-based per-feature maps are deprecated; per-load registry registration was removed (issue #45).

**Why:** three overlapping option families existed, mostly unimplemented; keeping one honest mechanism (plus the annotation/config path via `valueReaderName` in the model) beats three half-working ones. Readers/writers resolved by *name* must be in the `CodecValueRegistry` when the resource is created.

## OSGi-First Packaging

Every bundle is a Declarative Services citizen; discovery and wiring happen through the OSGi service registry and capability model rather than central configuration.

Concretely: the metadata service is a whiteboard (register an `EPackage` service and it becomes codec-ready), format bundles register `CodecFormatProvider`/resource factories as components, and consumers declare format needs via `@RequireCodec*` capability annotations instead of raw manifest headers.

**Why:** dynamic discovery without central wiring, and resolver-checked deployments. Everything still works outside OSGi through plain constructors (`MetadataServiceFactory`, no-arg factory constructors) — OSGi is the first-class path, not the only one.

## Secure-by-Default REST Overrides

The `Codec-Options` HTTP header lets clients tune serialization per request, but only keys explicitly whitelisted by a `RestOverridableCodecOptions` service are honored.

Everything else is silently dropped, and risky options (expand, typeStrategy, value writers) are deliberately never contributed to the whitelist.

**Why:** client-controlled serialization options are an injection surface; an opt-in whitelist per format bundle keeps the default posture safe. See [[configuration#REST Client Overrides]].

## TCK for Format Parity

Codec features are specified as ~18 abstract TCK test classes in `org.eclipse.fennec.codec.tests`; every format bundle subclasses the full set. A feature isn't done until the TCK passes for JSON, YAML, CBOR, and BSON alike.

**Why:** format parity is a core promise of the single-engine design and would silently rot without an executable check. See [[testing#TCK Suites]].
