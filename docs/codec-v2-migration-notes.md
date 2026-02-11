# Codec V2 Migration Notes (V1 → V2)

This file contains migration notes extracted from the development guide.
For the main guide, see `codec-v2-development-guide.md`.

## 1. Package Changes

| V1 Package | V2 Package | Status |
|------------|------------|--------|
| `codec.v2.config.*` | `codec.config.*` | Migrated |
| `codec.v2.ser.*` | `codec.ser.*` | Migrated |
| `codec.v2.deser.*` | `codec.deser.*` | Migrated |
| `codec.v2.context.*` | `codec.context.*` | Migrated |
| `codec.v2.util.*` | `codec.util.*`, `codec.diagnostic.*` | Migrated |
| `codec.v2.module.*` | `codec.module.*` | Migrated |
| `codec.v2.resource.*` | `codec.resource.*` | Migrated |
| `codec.api.value.*` | `codec.value.*` | Migrated |

## 2. API Changes

**CodecConfiguration → ConfigurationResolver:**
```java
// Old (deprecated)
CodecConfiguration config = new CodecConfiguration();
config.setTypeInclude(true); // deprecated

// New
ConfigurationResolver config = ConfigurationResolver.builder()
    .typeStrategy(TypeStrategy.NAME)
    .build();
```

**CodecValueReader/Writer → Context-based:**
```java
// Old (deprecated)
Object read(JsonParser parser, EAttribute attr, DeserializationContext ctx);

// New
Object read(CodecReaderContext context, EAttribute attr);
```

**Resource constant:**
```java
// Old
CodecResource.CODEC_ROOT_OBJECT

// New
CodecResource.CODEC_ROOT_TYPE
```

## 3. EAnnotation Changes

**Deprecated keys:**
- `transient` → `ignore`
- `serialize` → Use `ignore`, `ignoreWrite`, `ignoreRead`
- `typeInclude` → `typeStrategy`
- `useId` → `idKeyMode`
- `useType` → `typeStrategy`

**New visibility model (Feature):**
```java
// Old
@FennecCodec(serialize=false)

// New
@FennecCodec(ignore=true)  // Both directions
@FennecCodec(ignoreWrite=true)  // Serialize only
@FennecCodec(ignoreRead=true)  // Deserialize only
@FennecCodec(forceWrite=true)  // Force serialize volatile/transient
@FennecCodec(forceRead=true)  // Force deserialize volatile/transient
```
