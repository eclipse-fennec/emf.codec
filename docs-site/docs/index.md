---
layout: home

hero:
  name: Fennec Codec
  text: EMF serialization, format-agnostic
  tagline: An EMF codec framework built on Jackson 3.x — serialize and deserialize EMF EObjects to JSON, BSON, CBOR, YAML, CSV and more, driven by declarative configuration.
  image:
    src: /fennec-logo.png
    alt: Eclipse Fennec logo
  actions:
    - theme: brand
      text: Read the Specification
      link: /guides/00-overview
    - theme: alt
      text: Annotation Reference
      link: /guides/16-annotation-reference
    - theme: alt
      text: View on GitHub
      link: https://github.com/eclipse-fennec/emf.codec

features:
  - icon: 📄
    title: Specification
    details: The complete Codec V2 serialization specification — architecture, configuration resolution, type/reference/feature strategies and the full annotation reference.
    link: /guides/00-overview
    linkText: Start at the Overview
  - icon: ⚙️
    title: Configuration
    details: Declarative, layered configuration — global options, naming conventions, type/supertype/discriminator, IDs and references — resolved through a well-defined scope chain.
    link: /guides/02-config-resolution
    linkText: Configuration Resolution
  - icon: 🧩
    title: Formats
    details: One model, many wire formats. A format-abstraction layer plugs in JSON, BSON, CBOR, YAML and tabular (CSV / ODS / XLSX) providers behind the same codec.
    link: /guides/17-format-abstraction
    linkText: Format Abstraction
  - icon: 📚
    title: References
    details: Practical references — the full codec options catalogue, EMF concepts and terminology, and worked tabular-exporter examples.
    link: /guides/codec-options-reference
    linkText: Codec Options Reference
---

## About Fennec Codec

Fennec Codec (`org.eclipse.fennec.codec`) is an EMF de-/serialization framework built on
**Jackson 3.x** for serializing and deserializing EMF `EObject`s to JSON and
other formats — **BSON**, **CBOR**, **YAML**, **CSV**, **ODS**, **XLSX**,
**GeoJSON** and more. Serialization is driven by declarative configuration
(annotations and options) resolved through a well-defined scope chain, so the
same model can be rendered many ways without touching code.

The documentation here is the **Codec V2 specification** — the source of truth
for how the codec resolves configuration and serializes types, references,
features and IDs across formats — together with practical references. Internal
development notes (plans, architecture deep-dives, security analyses) live in the
[`docs/` folder on GitHub](https://github.com/eclipse-fennec/emf.codec/tree/main/docs).
