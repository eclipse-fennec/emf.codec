# Codec V2 Serialization Specification

This specification defines how EMF EObjects are serialized to and deserialized from JSON (and other formats). It covers type information, identity, references, and all configuration options.

---

## PART I: FOUNDATIONS

Before configuring the codec, understand how it works and how configuration is resolved.

### [01 - Architecture](01-architecture.md)

How the codec works internally. This chapter explains the component structure (CodecResource, serializers, deserializers), how configuration from multiple sources is merged into an immutable EffectiveCodecConfig, and the serialization/deserialization flow. Read this first to understand the big picture.

### [02 - Configuration Resolution](02-config-resolution.md)

How configuration is resolved across two dimensions: the **source hierarchy** (load/save options → resource → factory → module → annotations → defaults) and the **scope chain** (feature → class → global). Understanding this is essential - every configuration option follows these rules.

### [03 - Naming Conventions](03-naming-conventions.md)

The consistent naming scheme used throughout the codec: EAnnotation keys (`typeStrategy`), property map keys (`codec.typeStrategy`), Java constants (`CODEC_TYPE_STRATEGY`), and builder methods (`.typeStrategy(...)`). Also covers the scope-level keys like `codec.eClassConfig` for per-class configuration.

### [04 - Common Types](04-common-types.md)

Definitions of the enums and types used across all configuration: `SerializationFormat` (PLAIN vs STRUCTURED), `StrategyScope` (ALL, ROOT_ONLY, etc.), `TypeStrategy`, `IdStrategy`, `IdKeyMode`, and others. Reference this when you encounter these types in other chapters.

### [05 - Global Options](05-global-options.md)

Codec-wide settings that affect all serialization: smart compression (omit redundant type info), field ordering, numeric ID optimization, and the global feature ignore list. These are the defaults that feature-specific settings can override.

---

## PART II: SERIALIZATION FEATURES

Each serialization target (type, ID, reference, etc.) has its own configuration options.

### [06 - Type Serialization](06-type.md)

How EClass type information is written to JSON. Strategies include URI (full EMF URI), NAME (simple class name), CLASS (Java class), NUMERIC (classifier ID), and NONE. Covers both PLAIN format (`"_type": "Person"`) and STRUCTURED format (`"_type": {"type": "Person"}`).

### [07 - SuperType Serialization](07-supertype.md)

Optional serialization of supertype information for querying and indexing. Configure which supertypes to include (SINGLE, ALL, ALL_EMF), presentation format (array or string), and how they integrate with the type field in STRUCTURED format.

### [08 - Discriminator Mapping](08-discriminator-mapping.md)

Type resolution based on values in the JSON data itself, rather than explicit type fields. Two approaches: **Type Mapping Registry** (EClass annotations define discriminator values) and **Inline Mapping** (EReference annotations map values to types). Essential for IoT/LoRaWAN payloads and external JSON APIs.

### [09 - ID Serialization](09-id.md)

How object identity is serialized. Strategies: ID_FIELD (use eID attribute), COMBINED (concatenate multiple features). Key modes control what's written: ID_ONLY (just `_id`), BOTH (`_id` + individual features), FEATURE_ONLY, NONE. Supports PLAIN and STRUCTURED formats.

### [10 - Reference Serialization](10-reference.md)

How non-containment references point to other objects. Formats include PLAIN (just the reference value) and STRUCTURED (object with `_ref` and optional `_type`). The `expand` option serializes the full referenced object inline. Also covers cross-document containment detection.

### [11 - Feature Serialization](11-feature.md)

Configuration for individual EAttributes and EReferences: custom JSON keys, null/empty/default value handling, enum serialization modes (LITERAL, VALUE, NAME), and the ignore/force flags for controlling which features are serialized.

### [12 - Polymorphism and Inheritance](12-polymorphism.md)

How the codec handles polymorphic references where the actual type differs from the declared type. Covers annotation inheritance (feature → class → global → default) and type resolution strategies for heterogeneous collections.

---

## PART III: RUNTIME & ADVANCED

Options that apply at load/save time and advanced features.

### [13 - Load/Save Options](13-load-save-options.md)

Options passed to `resource.load(options)` and `resource.save(options)`: root type hints (`CODEC_ROOT_TYPE`), schema context (`CODEC_ROOT_SCHEMA`), per-feature type hints (`CODEC_FEATURE_TYPE_HINTS`), deserialization mode (STRICT, LENIENT, AUTO_DETECT), and type hint mode (HINT vs OVERRIDE).

### [14 - Custom Value Readers/Writers](14-custom-values.md)

Extend the codec with custom serialization for specific types. Define `CodecValueReader` and `CodecValueWriter` implementations, register them globally or activate per-feature via annotations. Includes examples for OpenAPI, GeoJSON coordinates, and date formatting.

### [15 - Error Handling & Diagnostics](15-error-handling.md)

How the codec reports errors and warnings through EMF's diagnostic mechanism. Error types (type resolution, ID parsing, reference resolution), severity levels, the DiagnosticCollector API, and options for fail-fast mode and warning suppression.

---

## PART IV: REFERENCE

Quick-reference materials for lookup.

### [16 - Annotation & Configuration Reference](16-annotation-reference.md)

**The definitive reference** for all configuration options. Complete matrix of which properties are valid at which scope levels (Global, EClass, EReference, EAttribute), naming conventions, property-to-builder mapping, and implementation status tracking.

---

## PART V: EXTENSIBILITY & EXAMPLES

Extending the codec and practical examples.

### [17 - Format Abstraction](17-format-abstraction.md)

The codec's format-agnostic architecture that enables JSON, BSON, CSV, and custom formats. Covers the stream abstraction interfaces (CodecStreamReader/Writer), token model, format adapters, JSON Schema integration, and the OpenAPI extension example.

### [18 - Configuration Scenarios](18-scenarios.md)

Complete, working examples that combine multiple features: minimal configuration, STRUCTURED format with supertypes, smart compression, custom keys, reference expansion, and NUMERIC strategy for compact output. Use these as starting points for your own configuration.

### [19 - Test Coverage](19-test-coverage.md)

Test coverage matrix mapping spec sections to test files. Use this to find tests for specific features or to identify coverage gaps. Lists all test models and advanced scenario coverage.

### [20 - Code Conventions](20-code-conventions.md)

Java code conventions, patterns, and helper classes for the codec implementation. Covers Java 17+ idioms, null safety with `java.util.Objects`, EMF-specific patterns, and the catalog of existing and planned helper classes. This is a living document updated through code review feedback.

### [99 - Open Questions](99-open-questions.md)

Unresolved design questions and future considerations. Check here before proposing changes - your question may already be captured.

---

## Quick Reference

### Serialization Targets

| Target | Purpose | Applies To |
|--------|---------|------------|
| **Type** | Identifies the EClass of an object | All EObjects |
| **ID** | Unique identifier for an object | EObjects with identity |
| **Reference** | Points to non-contained objects | EReference (non-containment) |
| **Cross-Doc Containment** | Points to contained objects in other documents | EReference (containment, cross-document) |
| **SuperType** | Lists supertypes for querying | EObjects (optional) |

### Configuration Priority

| Priority | Level | Scope |
|----------|-------|-------|
| 1 (highest) | Load/Save options | Per-operation |
| 2 | ResourceFactory defaults | Per-factory |
| 3 | Codec module config | Per-codec |
| 4 | Configuration properties | External |
| 5 | EAnnotations | Per-model |
| 6 (lowest) | Built-in defaults | Global |

### Key Enums

| Enum | Values | Default |
|------|--------|---------|
| SerializationFormat | PLAIN, STRUCTURED | PLAIN |
| TypeStrategy | URI, NAME, SCHEMA_AND_TYPE, NUMERIC, NONE | URI |
| IdStrategy | ID_FIELD, COMBINED | ID_FIELD |
| IdKeyMode | ID_ONLY, BOTH, FEATURE_ONLY, NONE | ID_ONLY |
| StrategyScope | ALL, ROOT_ONLY, ROOT_CONTAINMENT, ROOT_NON_CONTAINMENT | ALL |

---

## Related Documentation

- **[Development Guide](../codec-v2-development-guide.md)** - Implementation status, architecture details, session continuity
