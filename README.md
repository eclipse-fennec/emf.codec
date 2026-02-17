[![CI Build](https://github.com/eclipse-fennec/emf.codec/actions/workflows/build.yml/badge.svg)](https://github.com/eclipse-fennec/emf.codec/actions/workflows/build.yml)[![License](https://github.com/eclipse-fennec/emf.codec/actions/workflows/license.yml/badge.svg)](https://github.com/eclipse-fennec/emf.codec/actions/workflows/license.yml)

# Eclipse Fennec Codec

An EMF codec framework built on Jackson 3.x for serializing and deserializing EMF EObjects to JSON and other formats (BSON, CSV, etc.).

## Documentation

The complete codec specification is available in [`docs/codec-v2-spec/`](docs/codec-v2-spec/00-overview.md):

- **[00 - Overview & Table of Contents](docs/codec-v2-spec/00-overview.md)** - Start here
- **[01 - Architecture](docs/codec-v2-spec/01-architecture.md)** - Component structure, serialization flow
- **[02 - Configuration Resolution](docs/codec-v2-spec/02-config-resolution.md)** - Source hierarchy, scope chain
- **[06 - Type Serialization](docs/codec-v2-spec/06-type.md)** - Type strategies (URI, NAME, NUMERIC, etc.)
- **[09 - ID Serialization](docs/codec-v2-spec/09-id.md)** - ID strategies, key modes, formats
- **[10 - Reference Serialization](docs/codec-v2-spec/10-reference.md)** - Reference handling, expand, proxy
- **[11 - Feature Serialization](docs/codec-v2-spec/11-feature.md)** - Attribute/feature configuration
- **[16 - Annotation Reference](docs/codec-v2-spec/16-annotation-reference.md)** - Complete property reference

Further documentation:

| Document | Purpose |
|----------|---------|
| [Development Guide](docs/codec-v2-development-guide.md) | Current state, architecture, session continuity |
| [Plans & Roadmap](docs/codec-v2-plans.md) | Active plans, GAP analysis |
| [Reference](docs/codec-v2-reference.md) | EMF concepts, terminology, API reference |

## Project Structure

| Project | Purpose |
|---------|---------|
| `org.eclipse.fennec.codec` | Codec runtime (serialization/deserialization) |
| `org.eclipse.fennec.codec.api` | Configuration API (TypeConfig, IdConfig, etc.) |
| `org.eclipse.fennec.codec.metadata` | Codec-specific metadata aspects |
| `org.eclipse.fennec.model.metadata` | Generic metadata service infrastructure |
| `org.eclipse.fennec.codec.bson` | BSON (MongoDB) format provider |
| `org.eclipse.fennec.codec.cbor` | CBOR format provider |
| `org.eclipse.fennec.codec.yaml` | YAML format provider |
| `org.eclipse.fennec.codec.geojson` | GeoJSON codec extension |
| `org.eclipse.fennec.codec.jsonschema` | JSON Schema codec extension |
| `org.eclipse.fennec.codec.openapi` | OpenAPI codec extension |
| `org.eclipse.fennec.codec.examples` | Usage examples |

## Build

```bash
./gradlew build                                    # Build all
./gradlew :org.eclipse.fennec.codec:test            # Codec tests (JUnit 5)
./gradlew :org.eclipse.fennec.codec.metadata:test  # Metadata tests
./gradlew :org.eclipse.fennec.codec.api:test       # API tests
```

## Developers

* **Juergen Albert** (jalbert) / [j.albert@data-in-motion.biz](mailto:j.albert@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *architect*, *developer*
* **Mark Hoffmann** (mhoffmann) / [m.hoffmann@data-in-motion.biz](mailto:m.hoffmann@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *developer*, *architect*

## License

**Eclipse Public License 2.0**

## Copyright

Data In Motion Consulting GmbH - All rights reserved

---
Data In Motion Consulting GmbH - [info@data-in-motion.biz](mailto:info@data-in-motion.biz)
