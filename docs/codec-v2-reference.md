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

## 8. Deprecated API Audit

### 8.1 Deprecation Summary

**OLD API (deprecated):**
- `org.eclipse.fennec.codec.api.value.*` — old value reader/writer interfaces
- `org.eclipse.fennec.codec.v2.*` — old runtime packages

**NEW API (non-deprecated):**
- `org.eclipse.fennec.codec.value.*` — new value reader/writer interfaces
- `org.eclipse.fennec.codec.*` — new runtime packages

**Key Difference:** New value reader/writer interfaces use context objects
(`CodecReaderContext` / `CodecWriterContext`) instead of raw Jackson `JsonParser`/`JsonGenerator` +
`DeserializationContext`/`SerializationContext`. The context objects provide additional access to
`EffectiveCodecConfig` and `DiagnosticCollector`.

### 8.2 SRC Files Using Deprecated APIs (NEW packages — must migrate)

These are **new** `codec.*` package files that still import from deprecated old packages:

| File | Deprecated Types Used | Migration Notes |
|------|----------------------|-----------------|
| `codec.resource.CodecResource` | `CodecValueRegistry` | Change import to `codec.value.CodecValueRegistry` |
| `codec.module.CodecModule` | `CodecValueRegistry` | Change import to `codec.value.CodecValueRegistry` |
| `codec.config.effective.EffectiveCodecConfig` | `CodecValueRegistry`, `CodecValueReader`, `CodecValueWriter` | Change imports; update lookup methods to return new interfaces |
| `codec.ser.AttributeSerializationEntry` | `CodecValueRegistry`, `CodecValueWriter`, `AttributeValueWriter` | Change imports; update `write()` call sites to pass `CodecWriterContext` |
| `codec.ser.ReferenceSerializationEntry` | `CodecValueRegistry`, `CodecValueWriter`, `ReferenceValueWriter` | Change imports; update `write()` call sites to pass `CodecWriterContext` |
| `codec.deser.AttributeDeserializationEntry` | `CodecValueRegistry`, `CodecValueReader`, `AttributeValueReader` | Change imports; update `read()` call sites to pass `CodecReaderContext` |
| `codec.deser.ReferenceDeserializationEntry` | `CodecValueRegistry`, `CodecValueReader`, `ReferenceValueReader` | Change imports; update `read()` call sites to pass `CodecReaderContext` |
| `codec.deser.DeserializationState` | `DiagnosticCollector` (v2.util) | Change import to `codec.diagnostic.DiagnosticCollector` |

### 8.3 SRC Files Using Deprecated APIs (OLD v2.* packages — will be deleted)

These files are in the old deprecated packages and will be deleted after migration. No action needed:

| File | Deprecated Types Used |
|------|----------------------|
| `codec.v2.deser.ReferenceDeserializationEntry` | `DeserializationState` |
| `codec.v2.resource.CodecResource` | `DeserializationState` |

### 8.4 TEST Files Using Deprecated APIs (NEW packages — must migrate)

| Test File | Deprecated Types Used |
|-----------|----------------------|
| `codec.deser.AttributeDeserializationEntryCanHandleTest` | `AttributeValueReader`, `CodecValueReader`, `CodecValueRegistry` |
| `codec.deser.ReferenceDeserializationEntryCanHandleTest` | `ReferenceValueReader`, `CodecValueReader`, `CodecValueRegistry` |
| `codec.deser.ReferenceDeserializationEntryCustomReaderTest` | `CodecValueReader`, `CodecValueRegistry` |
| `codec.ser.AttributeSerializationEntryCanHandleTest` | `AttributeValueWriter`, `CodecValueWriter`, `CodecValueRegistry` |
| `codec.ser.ReferenceSerializationEntryCanHandleTest` | `ReferenceValueWriter`, `CodecValueWriter`, `CodecValueRegistry` |
| `codec.ser.ReferenceSerializationEntryCustomWriterTest` | `CodecValueWriter`, `CodecValueRegistry` |
| `codec.module.CodecModuleBuilderTest` | `CodecValueRegistry` |
| `codec.resource.CodecResourceCustomValueTest` | `CodecValueRegistry` |

### 8.5 Migration Order (Recommended)

Migration should be bottom-up, starting with the easiest changes:

1. **Import-only migrations (Low effort)**
   - [ ] `DeserializationState`: `codec.v2.deser` → `codec.deser` (2 old-package files)
   - [ ] `DiagnosticCollector`: `codec.v2.util` → `codec.diagnostic` (in `DeserializationState`)
   - [ ] `CodecValueRegistry`: `codec.api.value` → `codec.value` (all files above)

2. **Value reader/writer migration (High effort — requires context objects)**
   - [ ] Create `CodecReaderContext` / `CodecWriterContext` implementations (or update existing)
   - [ ] Update `AttributeSerializationEntry` to use new `CodecValueWriter` with `CodecWriterContext`
   - [ ] Update `AttributeDeserializationEntry` to use new `CodecValueReader` with `CodecReaderContext`
   - [ ] Update `ReferenceSerializationEntry` to use new `CodecValueWriter` with `CodecWriterContext`
   - [ ] Update `ReferenceDeserializationEntry` to use new `CodecValueReader` with `CodecReaderContext`
   - [ ] Update `EffectiveCodecConfig` to return new value reader/writer interfaces

3. **Test migrations (after SRC is done)**
   - [ ] Update all 8 test files listed in §8.4 to use new value interfaces
   - [ ] Verify custom value reader/writer tests create `CodecReaderContext` / `CodecWriterContext`
