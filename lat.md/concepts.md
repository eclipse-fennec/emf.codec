# Domain Concepts

Core vocabulary of the Fennec Codec: the EMF modeling terms it operates on, the serialization targets it writes, the metadata fields in its wire format, and the strategy enums that control both.

## EMF and Ecore

Fennec Codec serializes objects of the Eclipse Modeling Framework (EMF). Everything the codec touches is described by an Ecore model, not by Java reflection or hand-written mappings.

Key terms used throughout the lattice:

- **EObject** — a model instance (the thing being serialized)
- **EClass** — its type; lives in an **EPackage** registered under a namespace URI (nsURI)
- **EStructuralFeature** — a property of an EClass: **EAttribute** (value-typed) or **EReference** (object-typed; *containment* references own their target, non-containment references point to objects owned elsewhere)
- **EAnnotation** — string key/value details attached to any model element under a source URI; the codec's per-model configuration lives here
- **Resource** — EMF's persistence unit with `load()`/`save()`; a codec is a Resource implementation (see [[architecture#Codec Runtime]])

## Serialization Targets

Besides plain attribute values, the codec serializes five kinds of information about an EObject. Each target has its own spec chapter and configuration family.

| Target | Purpose | Applies to |
|--------|---------|-----------|
| **Type** | Identifies the EClass of an object | all EObjects |
| **ID** | Unique identity of an object | EObjects with identity |
| **Reference** | Points to non-contained objects | non-containment EReferences |
| **Cross-doc containment** | Points to contained objects living in other documents | containment EReferences across documents |
| **SuperType** | Lists supertypes for querying/indexing | EObjects (optional) |

## Metadata Fields

The wire format carries codec metadata in reserved keys alongside data fields: `_type`, `_supertype`, `_id`, and `_ref` (inside structured references).

Default field order is `_type` → `_supertype` → `_id` → features; the `idOnTop` option floats `_id` (and the EClass's eID attribute) to the front. On read, field order is irrelevant — see [[architecture#Deferred Properties]].

## Strategies and Formats

A small set of enums (defined in spec chapter `docs/codec-v2-spec/04-common-types.md`) controls how each target is rendered. They combine freely per feature, per class, or globally.

- **SerializationFormat** — `PLAIN` (bare value: `"_type": "Person"`) vs `STRUCTURED` (object form: `"_type": {"type": "Person"}`); default PLAIN
- **TypeStrategy** — `URI` (full EMF URI, default), `NAME`, `SCHEMA_AND_TYPE`, `NUMERIC` (classifier ID), `NONE`
- **IdStrategy** — `ID_FIELD` (use the eID attribute, default) or `COMBINED` (concatenate several features)
- **IdKeyMode** — what gets written: `ID_ONLY` (just `_id`, default), `BOTH`, `FEATURE_ONLY`, `NONE`
- **StrategyScope** — where a strategy applies: `ALL` (default), `ROOT_ONLY`, `ROOT_CONTAINMENT`, `ROOT_NON_CONTAINMENT`

## Discriminator Mapping

Type resolution driven by values in the data itself instead of an explicit `_type` field. Essential for external JSON APIs and IoT/LoRaWAN payloads whose schema the codec does not control.

Two approaches (spec chapter `docs/codec-v2-spec/08-discriminator-mapping.md`):

- **Type Mapping Registry** — EClass annotations declare discriminator values; `TypeDiscriminatorService` (in `org.eclipse.fennec.codec.metadata`) maps value → EClass at runtime
- **Inline Mapping** — EReference annotations map field values to target types locally

## Aspects and the Shadow Model

Instead of re-parsing EAnnotations on every load/save, the metadata layer computes a "shadow model" once at EPackage registration time. See [[decisions#Metadata as a Shadow EMF Model]] for why.

Registered EPackages are wrapped into `PackageMetadata` → `ClassMetadata` → `AttributeMetadata`/`ReferenceMetadata`, each carrying pluggable **Aspects**. The codec contributes `ClassCodecAspect`, `FeatureCodecAspect`, and `ReferenceCodecAspect` (defined in `org.eclipse.fennec.codec.metadata/model/codec.ecore`); other concerns (ORM, historization) can contribute their own aspect types without touching the domain model.

## Value Readers and Writers

The codec's extension point for custom conversion of specific types or features: `CodecValueReader` / `CodecValueWriter` implementations registered in a `CodecValueRegistry` (`org.eclipse.fennec.codec.api`).

Activation paths: globally by registration, per-feature via `valueReaderName`/`valueWriterName` EAnnotations, or per-operation via reader/writer *instances* in load/save options (see [[decisions#One Runtime Value-Binding Mechanism]]). The OpenAPI and JSON Schema formats are built almost entirely on this mechanism — e.g. an EPackage-valued feature whose JSON representation is a schema map is read by `EPackageValueReader`.
