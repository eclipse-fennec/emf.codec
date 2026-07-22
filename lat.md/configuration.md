# Configuration Model

How codec behavior is configured: a two-dimensional resolution model (source hierarchy × scope chain), a strict naming convention across all carriers, and runtime load/save options.

## Two Dimensions

Every option is resolved along two independent axes: **where it was declared** (the source hierarchy) and **what it applies to** (the scope chain). Understanding this pair is a prerequisite for everything else.

The full rules are in `docs/codec-v2-spec/02-config-resolution.md`.

The result of resolution is one immutable `EffectiveCodecConfig` per operation — see [[architecture#Codec Runtime]] and [[decisions#Immutable Effective Configuration]].

## Source Hierarchy

Configuration can come from six sources; a higher source overrides a lower one, per key.

| Priority | Source | Granularity |
|----------|--------|-------------|
| 1 (highest) | Load/Save options | per operation |
| 2 | Resource options | per resource |
| 3 | ResourceFactory defaults | per factory |
| 4 | `CodecModule` config | per codec |
| 5 | EAnnotations (via metadata layer) | per model |
| 6 (lowest) | Built-in defaults | global |

Merging is implemented by `ConfigurationResolver` (`org.eclipse.fennec.codec.api/src/org/eclipse/fennec/codec/config/ConfigurationResolver.java`) with `ConfigMergeHelper`, which accepts both the short key (`fieldOrder`) and the prefixed key (`codec.fieldOrder`) from every source.

## Scope Chain

Independent of the source, an option is looked up from the most specific scope outward: **feature → class → global**, then the built-in default.

Per-class and per-feature values can be supplied at runtime through the scope-level option keys `codec.eClassConfig`, `codec.eReferenceConfig`, and `codec.eAttributeConfig` (maps of EClass/EStructuralFeature → option map), or in the model through EAnnotations on the corresponding element.

## Naming Conventions

One concept, four spellings — mechanical and predictable across all carriers (spec chapter `docs/codec-v2-spec/03-naming-conventions.md`):

| Carrier | Example |
|---------|---------|
| EAnnotation detail key | `typeStrategy` |
| Property map key | `codec.typeStrategy` |
| Java constant (`CodecOptions`) | `CODEC_TYPE_STRATEGY` |
| Builder method | `.typeStrategy(...)` |

The definitive matrix of every property, its valid scopes, and implementation status is `docs/codec-v2-spec/16-annotation-reference.md`; the user-facing option list is `docs/codec-options-reference.md`.

## Load and Save Options

Options passed to `resource.load(options)` / `resource.save(options)` sit at the top of the source hierarchy and carry the runtime-only keys.

Notable runtime options (spec chapter `docs/codec-v2-spec/13-load-save-options.md`):

- `CODEC_ROOT_TYPE` / `CODEC_ROOT_SCHEMA` — root type hint and schema context for documents without type info
- `CODEC_FEATURE_TYPE_HINTS` — per-feature type hints; hint mode HINT vs OVERRIDE
- Deserialization mode — STRICT, LENIENT, AUTO_DETECT
- `CODEC_FEATURE_VALUE_READER_INSTANCES` / `_WRITER_INSTANCES` — per-operation custom value binding (the single supported runtime binding mechanism, see [[decisions#One Runtime Value-Binding Mechanism]])

## Format-Specific Custom Properties

Formats define their own namespaced options (e.g. `codec.jsonschema.*`, `codec.csv.*`, `codec.ods.*`) without the core knowing them: `CodecResource` auto-collects unknown `codec.*` keys into `EffectiveCodecConfig.getCustomProperties()`.

Examples: `codec.jsonschema.generateOclConstraints`, `codec.csv.dataTypeInSecondRow`, `codec.ods.generateLinks`.

## REST Client Overrides

REST clients may override a whitelisted subset of codec options per request via the `Codec-Options` header — secure by default (empty whitelist ⇒ header ignored).

Each bundle contributes its safe keys as a `RestOverridableCodecOptions` service; `ClientCodecOptionsFilter` in `org.eclipse.fennec.codec.rest` enforces the whitelist. Deliberately excluded: risky keys like `expand`, `typeStrategy`, value writers. Design doc: `docs/codec-rest-client-overridable-options.md`.
