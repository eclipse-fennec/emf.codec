# Project Map

Where things live: the repo is a Gradle + bnd OSGi workspace of ~25 bundles, layered as foundation → API → runtime → format/extension bundles, plus test and tooling projects.

## Layering

The dependency graph is strictly layered; nothing below depends on anything above it. `tabular` and `jsonschema` are secondary hubs (tabular for the four exporters, jsonschema for openapi).

```
org.eclipse.fennec.model.metadata        (foundation: generic metadata service)
        ↑
org.eclipse.fennec.codec.metadata        (codec aspects, discriminators)
        ↑
org.eclipse.fennec.codec.api             (config API, SPI contracts, options)
        ↑
org.eclipse.fennec.codec                 (runtime + JSON)
        ↑
formats (yaml, cbor, bson) · tabular family · jsonschema → openapi · geojson · rest
```

There is no codec-v1 code in this repository — the whole tree is the v2 rewrite. The `v2` suffix survives only in the spec directory name and the `...jsonschema.v2` package.

## Core Bundles

The four bundles every other project builds on. Details: [[architecture#Two-Layer Architecture]].

| Bundle | Purpose |
|--------|---------|
| `org.eclipse.fennec.model.metadata` | Generic metadata service: shadow-model registry (`metadata.ecore`), whiteboard DS component. No fennec dependencies. |
| `org.eclipse.fennec.codec.metadata` | Codec aspects (`codec.ecore`), `CodecAspectProvider`, `TypeDiscriminatorService`. Pure metadata — no codec dependency. |
| `org.eclipse.fennec.codec.api` | Public config API (`ConfigurationResolver`, `*Config` types, `CodecOptions`), format SPI (`CodecFormatProvider`, `FormatDelegate`), value SPI (`CodecValueRegistry`, readers/writers). |
| `org.eclipse.fennec.codec` | The codec runtime: `CodecResource`, ser/deser entries, `CodecModule`, Jackson integration, built-in JSON provider. |

## Format Bundles

One bundle per wire format; each registers a `CodecFormatProvider` / resource factory DS component. See [[formats#Streaming Formats]] and [[formats#Tabular Family]].

| Bundle | Format |
|--------|--------|
| `org.eclipse.fennec.codec.yaml` / `.cbor` | Jackson dataformat swaps |
| `org.eclipse.fennec.codec.bson` | BSON via in-memory `BsonDocument` (non-Jackson) |
| `org.eclipse.fennec.codec.tabular` + `.tabular.model` | Shared tabular pipeline + intermediate EMF model |
| `org.eclipse.fennec.codec.csv` / `.ods` / `.xlsx` / `.rlang` | Tabular renderers |

## Extension Bundles

Formats that are less "wire encoding" and more "domain adaptation". See [[formats#Schema Formats]] and [[formats#REST Integration]].

| Bundle | Purpose |
|--------|---------|
| `org.eclipse.fennec.codec.jsonschema` | JSON Schema ↔ EPackage conversion, OCL constraint generation |
| `org.eclipse.fennec.openapi.model` | EMF-generated OpenAPI v3 model (`openapi_v3.ecore`) |
| `org.eclipse.fennec.codec.openapi` | OpenAPI document codec on top of jsonschema |
| `org.eclipse.fennec.codec.geojson` | GeoJSON adaptation (external `org.geojson.model`) |
| `org.eclipse.fennec.codec.rest` | Jakarta REST message body readers/writers, endpoint annotations, `Codec-Options` filter |

## Test and Tooling Projects

Non-shipping projects. Their roles and rules are detailed in [[testing#Test Layers]].

| Project | Role |
|---------|------|
| `org.eclipse.fennec.codec.tests` | Shared abstract TCK + test models |
| `org.eclipse.fennec.codec.examples` | Plain JUnit usage examples |
| `org.eclipse.fennec.codec.osgi.tests`, `.jsonschema.tests`, `.rest.tests` | OSGi integration tests (`testOSGi`) |
| `org.eclipse.fennec.codec.playground` | Runnable manual test bench (`launch.bndrun`) |
| `org.eclipse.fennec.codec.workspace.library` | bnd workspace aggregator (`required.bndrun` only) |

## Branches and Releases

`snapshot` is the active development branch (every push publishes `-SNAPSHOT` artifacts to Sonatype Central); `main` holds the latest release, published to Maven Central under `org.eclipse.fennec.codec:*`. CI details: `docs/ci.md`.
