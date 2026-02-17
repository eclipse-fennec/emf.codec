# Codec V2 Reference Information

This file contains reference information extracted from the development guide.
For the main guide, see `codec-v2-development-guide.md`.

## 1. EMF Concepts

| EMF Term | Codec V2 Context |
|----------|------------------|
| **EClass** | Type being serialized/deserialized |
| **EAttribute** | Simple feature (String, int, Date) |
| **EReference** | Object reference (containment or non-containment) |
| **EPackage** | Registered with MetadataService for metadata |
| **EAnnotation** | Source of configuration (lowest priority) |
| **EFactory** | Used to create instances during deserialization |

## 2. Codec V2 Terminology

| Term | Definition |
|------|------------|
| **Aspect** | Metadata object (ClassConfig, FeatureConfig) |
| **Discriminator** | Value used to identify EClass (e.g., "temp-sensor" → TempSensor) |
| **Entry** | Serialization/Deserialization unit (Type, ID, Feature, Reference) |
| **Effective Config** | Resolved configuration after merging 5 sources |
| **Scope Chain** | Global → Class → Feature (3 levels) |
| **Source Hierarchy** | Options → Resource → Factory → Module → Annotation (5 levels) |
| **Visibility Gate** | First gate: ignore/ignoreWrite/ignoreRead/force* |
| **Value Gate** | Second gate: serializeNull/Empty/Default |

## 3. Jackson Integration

```java
// Serialization context
CodecWriterContext ctx = new CodecWriterContextImpl(generator, context, effectiveConfig);

// Deserialization context
CodecReaderContext ctx = new CodecReaderContextImpl(parser, context, effectiveConfig);

// Custom value reader/writer
public class MyValueReader implements AttributeValueReader {
    @Override
    public Object read(CodecReaderContext context, EAttribute attribute) {
        JsonParser parser = context.getParser();
        return parser.readValueAs(MyType.class);
    }
}
```

## 4. Metadata Service Usage

```java
// Register EPackage (triggers aspect parsing)
MetadataService metadataService = MetadataServiceFactory.getInstance();
DiagnosticCollector diagnostics = new DiagnosticCollectorImpl();
metadataService.registerPackage(MyPackage.eINSTANCE, diagnostics);

// Get aspects
ClassConfig classConfig = metadataService.getClassConfig(eClass);
FeatureConfig featureConfig = metadataService.getFeatureConfig(eClass, feature);

// Type discriminator service
TypeDiscriminatorService typeService = metadataService.getTypeDiscriminatorService();
EClass resolved = typeService.resolve("temp-sensor");
```

## 5. Configuration Builder Pattern

```java
ConfigurationResolver config = ConfigurationResolver.builder()
    .typeStrategy(TypeStrategy.NAME)
    .idKeyMode(IdKeyMode.ID_ONLY)
    .idFeatures(List.of("id"))
    .superTypeStrategy(SuperTypeStrategy.ALL)
    .build();

// Per-class override
ClassConfig classConfig = ClassConfig.builder()
    .typeStrategy(TypeStrategy.URI)
    .build();

// Per-feature override
FeatureConfig featureConfig = FeatureConfig.builder()
    .ignore(true)
    .build();
```

## 6. Diagnostic Severity Levels

| Severity | Meaning | Example |
|----------|---------|---------|
| **ERROR** | Invalid configuration, feature disabled | typeValueReaderName on EReference |
| **WARNING** | Questionable but allowed | Runtime-only key in EAnnotation |
| **INFO** | Informational message | Deprecated key usage |

## 7. Type Resolution Priority Chain

During deserialization:

1. **ValueReader** (if registered) — full delegation, bypass all type logic
2. **Explicit `_type` field** (in JSON) — highest priority from data
3. **Discriminator mapping** (TypeDiscriminatorService) — inline or global
4. **Type hints** (CODEC_FEATURE_TYPE_HINTS) — EAnnotation on EReference
5. **Declared type** (EReference.eReferenceType) — fallback
6. **Error** (if abstract and no resolution) — DeserializationMode controls behavior

