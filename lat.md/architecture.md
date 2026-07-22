# Architecture

How the codec works internally: a two-layer design where a metadata service parses model annotations once, and a Jackson-based codec runtime consumes the pre-computed configuration during serialization.

## Two-Layer Architecture

The system splits into a **codec runtime** and a **metadata layer**. The runtime never parses EAnnotations itself; it consumes aspect objects the metadata layer computed at EPackage registration time.

```
org.eclipse.fennec.codec        (codec runtime: ser/deser, Jackson, resource)
        │ uses metadata of
        ▼
org.eclipse.fennec.codec.metadata   (codec aspects: parses codec EAnnotations)
        │ extends
        ▼
org.eclipse.fennec.model.metadata   (generic metadata service: shadow model, whiteboard)
```

Configuration flows in from five sources and is merged into one immutable snapshot — see [[configuration#Source Hierarchy]].

## Codec Runtime

The runtime lives in `org.eclipse.fennec.codec`, with its public configuration API in `org.eclipse.fennec.codec.api`. The central class is `CodecResource`, an EMF Resource implementation whose `doSave`/`doLoad` drive everything.

`CodecResource` source: `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/resource/CodecResource.java`.

Key collaborators:

| Class | Role |
|-------|------|
| `CodecResource` | EMF Resource; entry point for load/save |
| `CodecModule` | Jackson 3 module carrying codec-global config |
| `ConfigurationResolver` (codec.api) | Merges the config sources (the spec calls this role "ConfigurationMerger") |
| `EffectiveCodecConfig` | Immutable merged config; per-class and per-feature lookups |
| `CodecJsonFactory` + `CodecJsonReadContext`/`CodecJsonWriteContext` | Jackson integration; contexts carry EMF state during parsing/generation |
| `CodecEObjectSerializer` / `CodecEObjectDeserializer` | Orchestrators iterating serialization/deserialization entries |
| `SerializationEntry` / `DeserializationEntry` families | Per-target workers: Type, SuperType, ID, Attribute, Reference |

## Serialization Flow

`CodecResource.doSave()` builds the `EffectiveCodecConfig`, then `CodecEObjectSerializer` writes metadata fields (`_type`/`_supertype`/`_id`, order controlled by `idOnTop`) and iterates the object's features.

Each feature is handled by a `SerializationEntry` (in `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/ser/`) that receives its **pre-merged** `EffectiveFeatureConfig` — entries contain no fallback logic (see [[decisions#Immutable Effective Configuration]]).

## Deserialization Flow

`CodecResource.doLoad()` mirrors the save path: resolve the EClass (from `_type` content, discriminator values, or a type hint), create the EObject, match JSON fields to `DeserializationEntry` workers, then post-process to resolve references.

Root and per-feature type hints come from load options — see [[configuration#Load and Save Options]].

## Deferred Properties

JSON field order is irrelevant on read: data fields arriving *before* `_type` are buffered and replayed after the type is resolved and the EObject exists.

The deferred buffer supports primitives, nested objects (as maps), arrays, and deep nesting. This matters because the JSON spec does not guarantee key order and external producers emit fields in arbitrary order — see [[decisions#Order-Independent Parsing]].

## Metadata Service

The generic layer (`org.eclipse.fennec.model.metadata`) is itself an EMF model (`metadata.ecore`): a `MetadataService` wraps registered EPackages into metadata objects with pluggable aspects — the shadow model of [[concepts#Aspects and the Shadow Model]].

The codec-specific layer (`org.eclipse.fennec.codec.metadata`) contributes `CodecAspectProvider` (`src/org/eclipse/fennec/codec/metadata/provider/CodecAspectProvider.java`), which parses codec EAnnotations into typed config objects at registration time and reports misconfigurations as diagnostics, plus `TypeDiscriminatorService` for discriminator→EClass mappings. It deliberately has **no dependency on the codec runtime** — it is pure metadata.

In OSGi, `MetadataServiceComponent` (Declarative Services) acts as a whiteboard: it dynamically binds `EPackage`, `AspectProvider`, and `MetadataHandler` services, so registering an EPackage as an OSGi service is enough to make it codec-ready. Outside OSGi, `MetadataServiceFactory` builds the same wiring manually.

## Format Abstraction

The runtime is format-agnostic: everything above speaks Jackson's streaming API, and non-JSON formats plug in underneath via `FormatDelegate` bridges rather than reimplementing the codec. Details in [[formats#Format Abstraction]].
