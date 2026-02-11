# Discriminator Mapping

[← SuperType Serialization](07-supertype.md) | [Next: ID Serialization →](09-id.md)

---

> **See also:** [Annotation Reference](16-annotation-reference.md) (Discriminator Mapping) for configuration key reference.

---

Discriminator mapping provides type resolution based on field values within the JSON data itself. This is a **separate layer** from TypeStrategy - it works alongside any strategy to translate arbitrary discriminator values to concrete EClasses.

## 1. Overview

### 1.1 Why Discriminator Mapping?

Standard TypeStrategy values (URI, NAME, CLASS, etc.) represent the type directly - the serialized value is self-describing. Discriminator mapping handles cases where the type information is encoded differently:

| Aspect | TypeStrategy (URI, NAME, etc.) | Discriminator Mapping |
|--------|-------------------------------|----------------------|
| **What it answers** | "What value represents this type?" | "How do I translate an arbitrary string to a type?" |
| **Serialization output** | Deterministic from EClass | Requires external mapping definition |
| **Deserialization input** | Self-describing (URI resolvable) | Requires registry lookup |
| **Standalone?** | Yes | No - needs discriminator definitions |

### 1.2 When to Use Discriminator Mapping

- **IoT/LoRaWAN devices** - device type embedded in payload metadata (e.g., `info.profileName`)
- **JSON Schema oneOf patterns** - type determined by feature presence or values
- **Legacy APIs** - type information encoded in application-specific fields
- **External APIs** - where you cannot control the type field name or format

### 1.3 Resolution Priority

During deserialization, type resolution follows this priority:

```
1. Type Mapping Registry (if configured on EClass or base class)
2. Inline Mapping (if configured on the EReference)
3. Type Strategy Resolution (URI, NAME, etc.)
4. Fallback (reference type, CODEC_FEATURE_TYPE_HINTS, CODEC_ROOT_TYPE)
```

> **Design rationale:** Type Mapping Registry has highest priority because it's our implementation of Jackson's `@JsonTypeInfo` + `@JsonSubTypes` pattern - the primary, flexible mechanism for polymorphic type handling. Inline Mapping is a simpler, more static variant for per-reference cases.

See [Type Deserialization - Type Resolution Flow](06-type.md#630-type-resolution-flow) for the complete deserialization flow with all decision points.

### 1.4 Interaction with TypeFormat (PLAIN vs STRUCTURED)

Discriminator mapping operates **independently** of the configured `typeFormat`. When discriminator mapping is active, it takes priority over the format-specific type strategy serialization:

| TypeFormat | Discriminator Mapping Configured | Behavior |
|------------|----------------------------------|----------|
| PLAIN | No | Standard: `"_type": "http://example.org#//Person"` |
| PLAIN | Yes | Discriminator value: `"_type": "temp"` |
| STRUCTURED | No | Standard: `"_type": {"schema": "...", "type": "Person"}` |
| STRUCTURED | Yes | **Discriminator value in structured format**: `"_type": {"type": "temp"}` |

**Key insight:** When `typeFormat=STRUCTURED` but discriminator mapping is configured:
- The discriminator value is written **inside** the structured `_type` object
- The standard STRUCTURED format schema/type decomposition is **not applied**
- Discriminator mapping has priority; STRUCTURED format adapts to contain the discriminator value

**Serialization example (discriminator + STRUCTURED):**
```json
{
  "_type": {
    "type": "temp"
  },
  "sensorId": "s-001",
  "celsius": 22.5
}
```

**Deserialization:** The deserializer parses the STRUCTURED `_type` object, extracts the `type` field value (`"temp"`), and resolves it through the discriminator registry — same as PLAIN format.

> **Note:** For NUMERIC and SCHEMA_AND_TYPE strategies in STRUCTURED format, discriminator mapping still takes priority. If a discriminator value is found, it replaces the standard strategy output entirely.

---

## 2. Annotation Sources

Discriminator mappings use **dedicated annotation sources** (not the main `http://eclipse.org/fennec/codec` source):

| Feature | Annotation Source | Applies To |
|---------|------------------|------------|
| Type Mapping | `http://eclipse.org/fennec/codec/typeMapping/{mapId}` | EClass |
| Inline Mapping | `http://eclipse.org/fennec/codec/inlineMapping` | EReference |

---

## 3. Configuration Properties

| Annotation Key | Source | EClass | ERef | Description |
|----------------|--------|:------:|:----:|-------------|
| `typeDiscriminatorPath` | `typeMapping/{mapId}` | ✅ | ❌ | JSON path to discriminator value (dot notation) |
| `typeDiscriminator` | `typeMapping/{mapId}` | ✅ | ❌ | This class's discriminator value (distributed registration) |
| `{value}={EClass URI}` | `typeMapping/{mapId}` or `inlineMapping` | ✅ | ✅ | Mapping entries as direct key/value details |
| `fallbackStrategy` | `typeMapping/{mapId}` or `inlineMapping` | ✅ | ✅ | `ERROR`, `SKIP`, `FALLBACK` (**default:** `SKIP`) |
| `fallbackEClass` | `typeMapping/{mapId}` or `inlineMapping` | ✅ | ✅ | Explicit fallback EClass URI |

> **Note:** The mapId is **embedded in the annotation source URI** (`http://eclipse.org/fennec/codec/typeMapping/{mapId}`), not a separate detail key. Mapping entries are direct key/value details where key = discriminator value and value = EClass URI.

| Property Key | Description |
|--------------|-------------|
| `codec.typeMapId` | Registry ID (for programmatic/property configuration) |
| `codec.typeDiscriminatorPath` | JSON path to discriminator value |
| `codec.typeDiscriminator` | This class's discriminator value |
| `codec.typeMappings` | Mappings as `Map<String, String>` (EClass property config) |
| `codec.inlineMappings` | Mappings as `Map<String, String>` (EReference property config) |
| `codec.fallbackStrategy` | `ERROR`, `SKIP`, `FALLBACK` |
| `codec.fallbackEClass` | Explicit fallback EClass URI |

**Implementation:** `CodecAnnotationConstants.TYPE_MAPPING_SOURCE_PREFIX`, `INLINE_MAPPING_SOURCE`, `KEY_TYPE_DISCRIMINATOR`, `KEY_TYPE_DISCRIMINATOR_PATH`, `KEY_FALLBACK_STRATEGY`, `KEY_FALLBACK_ECLASS`

---

## 4. Type Mapping Registry (on EClass)

The type mapping registry defines discriminator-to-EClass mappings using a dedicated annotation source that includes the registry ID.

**Annotation source:** `http://eclipse.org/fennec/codec/typeMapping/{mapId}`

The `{mapId}` is embedded in the source URI, making it structurally impossible to forget.

### 4.1 Centralized Configuration (Static Mappings)

Define all mappings on the base class. Useful when you control all concrete classes and want the mapping in one place.

**How it works:**
1. **Base class** uses annotation source with `{mapId}` and defines `typeDiscriminatorPath` + all mappings
2. Mappings are direct key/value details (key = discriminator value, value = EClass URI)
3. Concrete classes don't need any annotations

```xml
<!-- UplinkMessage is the abstract base class -->
<eClassifiers name="UplinkMessage">
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <!-- JSON path where discriminator value is found (dot notation for nested) -->
    <details key="typeDiscriminatorPath" value="info.profileName"/>
    <!-- Mappings: discriminator value → EClass URI -->
    <details key="temp-sensor" value="http://example.org#//TemperatureSensor"/>
    <details key="humidity-sensor" value="http://example.org#//HumiditySensor"/>
    <details key="pressure-sensor" value="http://example.org#//PressureSensor"/>
  </eAnnotations>
</eClassifiers>
```

**Deserialization example:**
```json
{
  "info": {
    "profileName": "temp-sensor",
    "timestamp": "2026-01-23T10:00:00Z"
  },
  "value": 23.5
}
```
→ Deserializer reads `info.profileName` = `"temp-sensor"` → looks up in `lorawan-devices` registry → resolves to `TemperatureSensor`

### 4.2 Distributed Configuration (Self-Registration)

Concrete classes can register themselves with a registry. Useful when concrete classes are in different packages or when you want extensible type hierarchies.

**How it works:**
1. **Base class** defines the registry with `typeDiscriminatorPath` using the `typeMapping/{mapId}` annotation source
2. **Concrete classes** register themselves using the **same annotation source** with `typeDiscriminator`
3. At runtime, `TypeDiscriminatorService` maintains the registry and resolves types

The mapId is always embedded in the annotation source URI — both base and concrete classes use the same source, ensuring consistency.

**Base class configuration:**
```xml
<eClassifiers name="UplinkMessage">
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <details key="typeDiscriminatorPath" value="info.profileName"/>
  </eAnnotations>
</eClassifiers>
```

**Concrete class self-registration:**
```xml
<!-- TemperatureMessage extends UplinkMessage -->
<eClassifiers name="TemperatureMessage">
  <!-- Same annotation source as the base class — mapId "lorawan-devices" is in the URI -->
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <details key="typeDiscriminator" value="temperature-profile"/>
  </eAnnotations>
</eClassifiers>
```

### 4.3 Combining Static and Distributed Configuration

Static mappings and distributed registration **can be combined** in the same registry. This enables extensible type hierarchies where the base package defines known types, and extension packages can add new types without modifying the base.

**Base class with static mappings:**
```xml
<eClassifiers name="UplinkMessage">
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <details key="typeDiscriminatorPath" value="info.profileName"/>
    <!-- Known types defined statically -->
    <details key="temp-sensor" value="http://example.org#//TemperatureSensor"/>
    <details key="humidity-sensor" value="http://example.org#//HumiditySensor"/>
  </eAnnotations>
</eClassifiers>
```

**Extension class registering dynamically (e.g., from another package):**
```xml
<!-- FooMessage extends UplinkMessage, defined in a different package -->
<eClassifiers name="FooMessage">
  <!-- Uses the same typeMapping source — mapId "lorawan-devices" matches the base class -->
  <eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
    <details key="typeDiscriminator" value="foo-bar"/>
  </eAnnotations>
</eClassifiers>
```

**Result:** The `lorawan-devices` registry contains:
- `temp-sensor` → `TemperatureSensor` (from static mapping)
- `humidity-sensor` → `HumiditySensor` (from static mapping)
- `foo-bar` → `FooMessage` (from distributed registration)

### 4.4 Programmatic Configuration

**Builder API:**
```java
ClassConfigBuilder.forEClass(ExamplePackage.Literals.UPLINK_MESSAGE)
    .typeMapId("lorawan-devices")
    .typeDiscriminatorPath("info.profileName")
    .addDiscriminatorMapping("temp-sensor", TemperaturePackage.Literals.TEMPERATURE_SENSOR)
    .addDiscriminatorMapping("humidity-sensor", HumidityPackage.Literals.HUMIDITY_SENSOR)
    .build();
```

**Property Map:**
```java
Map<String, Object> config = Map.of(
    "codec.typeMapId", "lorawan-devices",
    "codec.typeDiscriminatorPath", "info.profileName",
    "codec.typeMappings", Map.of(
        "temp-sensor", "http://example.org#//TemperatureSensor",
        "humidity-sensor", "http://example.org#//HumiditySensor"
    )
);
```

---

## 5. Inline Mapping (on EReference)

Inline mapping defines value→EClass mappings directly on an EReference using a dedicated annotation source. This is the simplest approach for small, reference-specific mappings.

**Annotation source:** `http://eclipse.org/fennec/codec/inlineMapping`

**How it works:**
1. **Main codec annotation** defines the type key (`typeKey`)
2. **Inline mapping annotation** defines mappings as direct key/value details
3. Only applies to objects accessed through this specific reference
4. Uses the same fallback and error handling as Type Mapping Registry

```xml
<eStructuralFeatures name="contacts" upperBound="-1" eType="#//Contact">
  <!-- Type key in main codec annotation -->
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="typeKey" value="contactType"/>
  </eAnnotations>
  <!-- Mappings in dedicated annotation -->
  <eAnnotations source="http://eclipse.org/fennec/codec/inlineMapping">
    <details key="friend" value="http://example.org#//Friend"/>
    <details key="enemy" value="http://example.org#//Enemy"/>
    <details key="colleague" value="http://example.org#//Colleague"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Deserialization example:**
```json
{
  "name": "John",
  "contacts": [
    { "contactType": "friend", "name": "Alice" },
    { "contactType": "enemy", "name": "Bob" },
    { "contactType": "colleague", "name": "Carol" }
  ]
}
```
→ Each contact's `contactType` is looked up in inline mapping → resolves to `Friend`, `Enemy`, `Colleague` respectively

### 5.1 Programmatic Configuration

For property maps or builder configuration, use `codec.inlineMappings`:

**Property Map:**
```java
// Per-EReference configuration with inline mappings
Map<EReference, Map<String, Object>> eReferenceConfig = new HashMap<>();

Map<String, Object> contactsConfig = Map.of(
    "codec.typeKey", "contactType",
    "codec.inlineMappings", Map.of(
        "friend", "http://example.org#//Friend",
        "enemy", "http://example.org#//Enemy",
        "colleague", ExamplePackage.Literals.COLLEAGUE  // EClass instance also works
    ),
    "codec.fallbackStrategy", "SKIP"
);
eReferenceConfig.put(PersonPackage.Literals.PERSON__CONTACTS, contactsConfig);

options.put("codec.eReferenceConfig", eReferenceConfig);
```

**Builder API:**
```java
CodecConfiguration.builder()
    .forReference(PersonPackage.Literals.PERSON__CONTACTS)
        .typeKey("contactType")
        .inlineMapping("friend", FriendPackage.Literals.FRIEND)
        .inlineMapping("enemy", EnemyPackage.Literals.ENEMY)
        .inlineMapping("colleague", ColleaguePackage.Literals.COLLEAGUE)
        .fallbackStrategy(FallbackStrategy.SKIP)
        .end()
    .build();
```

---

## 6. Fallback and Error Handling

When a discriminator value cannot be resolved to an EClass, the behavior is controlled by `fallbackStrategy` and `fallbackEClass`.

### 6.1 Fallback Strategy Values

| Value | Behavior |
|-------|----------|
| `SKIP` **(default)** | Log WARNING, continue to next resolution step |
| `ERROR` | Fail immediately, throw exception |
| `FALLBACK` | Use `fallbackEClass` (MUST be set, else ERROR) |

**"Next resolution step" depends on where fallback occurs:**
- Type Mapping Registry (step 1) fails with SKIP → try Inline Mapping (step 2), then Type Strategy (step 3)
- Inline Mapping (step 2) fails with SKIP → try Type Strategy (step 3)

> **Note:** `fallbackStrategy` only applies to Type Mapping Registry and Inline Mapping (steps 1 and 2 of type resolution). Type Strategy (step 3) does not have a configurable fallback strategy - if it cannot resolve the type, it continues to step 4 (Fallback hints: reference type, CODEC_FEATURE_TYPE_HINTS, CODEC_ROOT_TYPE).

### 6.2 Fallback Behavior

```
When discriminator value not found in mappings:

1. Check fallbackStrategy (default: SKIP):
   - ERROR    → Fail immediately
   - SKIP     → WARNING, continue to next resolution step
   - FALLBACK → Use fallbackEClass:
                 - If fallbackEClass is set → use it, RESOLVED ✓
                 - If fallbackEClass is NOT set → ERROR (misconfiguration)
```

**FALLBACK requires fallbackEClass:** When using `fallbackStrategy=FALLBACK`, you MUST configure `fallbackEClass`. This is explicit by design - if you want automatic fallback to reference type or hints, use `SKIP` instead (which continues to Type Strategy and eventually the hint-based fallback in step 4).

See [Type Serialization - Type Resolution Flow](06-type.md#630-type-resolution-flow) for the complete deserialization flow.

### 6.3 Relationship to Feature Type Hints

The `CODEC_FEATURE_TYPE_HINTS` load option (see [Load/Save Options](13-load-save-options.md)) integrates with inline mapping fallback resolution. This allows runtime control over fallback types without modifying the model:

```java
// Model defines inline mapping but no fallbackEClass
// At runtime, provide fallback via feature type hint
Map<EStructuralFeature, EClass> hints = Map.of(
    PersonPackage.Literals.PERSON__CONTACTS, ContactPackage.Literals.GENERIC_CONTACT
);
options.put(CODEC_FEATURE_TYPE_HINTS, hints);
```

**Priority:** `fallbackEClass` (model/config) takes precedence over `CODEC_FEATURE_TYPE_HINTS` (runtime).

### 6.4 Examples

```xml
<!-- Type Mapping with default SKIP behavior -->
<!-- If discriminator value not found → WARNING, continue to Type Strategy -->
<eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
  <details key="typeDiscriminatorPath" value="info.profileName"/>
  <!-- fallbackStrategy defaults to SKIP -->
  <details key="temp-sensor" value="http://example.org#//TemperatureSensor"/>
  <details key="humidity-sensor" value="http://example.org#//HumiditySensor"/>
</eAnnotations>

<!-- Type Mapping with explicit fallback EClass -->
<!-- Unknown discriminator → use GenericMessage -->
<eAnnotations source="http://eclipse.org/fennec/codec/typeMapping/lorawan-devices">
  <details key="typeDiscriminatorPath" value="info.profileName"/>
  <details key="fallbackStrategy" value="FALLBACK"/>
  <details key="fallbackEClass" value="http://example.org#//GenericMessage"/>
  <details key="temp-sensor" value="http://example.org#//TemperatureSensor"/>
  <details key="humidity-sensor" value="http://example.org#//HumiditySensor"/>
</eAnnotations>

<!-- Inline Mapping with ERROR (fail fast, strict mode) -->
<eAnnotations source="http://eclipse.org/fennec/codec/inlineMapping">
  <details key="fallbackStrategy" value="ERROR"/>
  <details key="friend" value="http://example.org#//Friend"/>
</eAnnotations>

<!-- Inline Mapping with default SKIP behavior -->
<!-- Unknown value → WARNING, continue to Type Strategy resolution -->
<eAnnotations source="http://eclipse.org/fennec/codec/inlineMapping">
  <details key="friend" value="http://example.org#//Friend"/>
  <details key="enemy" value="http://example.org#//Enemy"/>
</eAnnotations>
```

**Property keys for programmatic configuration:**

| Property Key | Description |
|--------------|-------------|
| `codec.fallbackStrategy` | `ERROR`, `SKIP`, `FALLBACK` |
| `codec.fallbackEClass` | EClass URI string or EClass instance |

---

## 7. Deserialization Flow

This section documents the detailed deserialization flow for both Type Mapping Registry and Inline Mapping. It complements the high-level type resolution flow in [06-type.md §6.3.0](06-type.md#630-type-resolution-flow) with implementation-level decision points.

### 7.1 Type Mapping Registry — Deserialization

When deserializing a contained object (e.g., a `Sensor` inside `SensorHub.sensors`), the deserializer checks whether the **expected EClass** (the reference type or hint) has a Type Mapping Registry configured.

**Config resolution:** For the expected EClass (e.g., `Sensor`), resolve:
- `mapId` — from `DiscriminatorConfig.getTypeMapId()` (derived from the `typeMapping/{mapId}` annotation source)
- `discriminatorPath` — from `DiscriminatorConfig.getTypeDiscriminatorPath()` (e.g., `_type` or `info.profileName`)
- `fallbackStrategy` — from `DiscriminatorConfig.getFallbackStrategy()` (default: `SKIP`)
- `fallbackEClass` — from `DiscriminatorConfig.getFallbackEClass()` (only when `FALLBACK`)

If `mapId` and `discriminatorPath` are both present, the deserializer enters **discriminator mapping mode**.

#### 7.1.1 Standard Discriminator Path (matches typeKey)

When `discriminatorPath` equals the configured type key (e.g., both are `_type`), the discriminator value is at the same JSON field that the standard type resolution reads. No special scanning is needed.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ TYPE MAPPING REGISTRY — STANDARD PATH (discriminatorPath == typeKey)        │
│                                                                             │
│ Example: Sensor with typeMapping/strict-sensors, discriminatorPath="_type"  │
│ JSON: { "_type": "temp", "sensorId": "s-001", "celsius": 22.5 }           │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: JSON object, hintEClass (Sensor), DiscriminatorConfig (mapId, path, fallback)

1. Read discriminator value from typeKey field
   → "_type" = "temp"

2. Lookup in TARGETED registry: resolve(mapId="strict-sensors", "temp")
   │
   ├─ Found → EClass URI → resolve to TempSensor ────────────→ RESOLVED ✓
   │
   └─ Not found → Apply fallbackStrategy from DiscriminatorConfig:
       ├─ ERROR    → Fail immediately (throw exception)
       ├─ SKIP     → WARNING, continue to Type Strategy (step 3 in 06-type.md)
       └─ FALLBACK → Use fallbackEClass ─────────────────────→ RESOLVED ✓
                     (fallbackEClass MUST be set, else ERROR)

Note: Resolution uses resolve(mapId, ...) — NOT resolveFromAny().
      This ensures the correct registry's fallback strategy is applied.
```

**Key implementation requirement:** The standard deserialization flow must receive the `DiscriminatorConfig` (mapId + fallbackStrategy) for the expected EClass. When a mapId is present, the discriminator value is looked up via `TypeDiscriminatorService.resolve(mapId, value, eClassResolver)` — the **targeted** method that respects the specific registry's fallback strategy.

#### 7.1.2 Non-Standard Discriminator Path (nested path)

When `discriminatorPath` differs from the type key (e.g., `info.profileName`), the discriminator value is at a **different location** than `_type`. This requires scanning ahead into nested JSON structures before type resolution can occur.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ TYPE MAPPING REGISTRY — NESTED PATH (discriminatorPath != typeKey)          │
│                                                                             │
│ Example: UplinkMessage with typeMapping/lorawan, path="info.profileName"   │
│ JSON: { "info": { "profileName": "temp-sensor", ... }, "value": 23.5 }    │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: JSON parser at START_OBJECT, hintEClass, DiscriminatorConfig

1. Use FeaturePathTypeResolver to scan JSON content:
   a. Split discriminatorPath by "." → path segments ["info", "profileName"]
   b. Buffer ALL tokens while scanning (for replay after resolution)
   c. Track nesting depth, match path segments at correct depth
   d. Extract value at final segment → "temp-sensor"

2. Lookup in TARGETED registry: resolve(mapId="lorawan", "temp-sensor")
   │
   ├─ Found → EClass URI → resolve to TemperatureSensor ─────→ RESOLVED ✓
   │
   └─ Not found → Apply fallbackStrategy (same as §7.1.1)

3. Create buffered parser from scanned tokens
   → Replay entire JSON object for property deserialization

Note: FeaturePathTypeResolver consumes the original parser. All further
      deserialization uses the buffered replay parser.
```

**When to use FeaturePathTypeResolver:** Only when `discriminatorPath` is a nested/non-standard path that differs from the type key. When `discriminatorPath == typeKey`, the standard flow handles it (§7.1.1) — no buffering needed.

#### 7.1.3 Config Data Flow

The following config data must be available when deserializing a contained object with Type Mapping Registry:

```
┌──────────────────────────────────────────────────────────────────────────┐
│ CONFIG FLOW FOR TYPE MAPPING REGISTRY                                    │
│                                                                          │
│ EClass (Sensor)                                                          │
│   └─ EAnnotation: typeMapping/strict-sensors                             │
│        ├─ typeDiscriminatorPath = "_type"                                │
│        ├─ fallbackStrategy = ERROR                                       │
│        └─ mappings: temp → TempSensor URI, alert → AlertSensor URI       │
│                                                                          │
│                        ↓ parsed at startup                               │
│                                                                          │
│ TypeDiscriminatorService                                                 │
│   └─ Registry "strict-sensors"                                           │
│        ├─ discriminatorPath = "_type"                                    │
│        ├─ fallbackStrategy = ERROR                                       │
│        └─ entries: { "temp" → TempSensor URI, "alert" → AlertSensor URI }│
│                                                                          │
│ DiscriminatorConfig (for Sensor EClass)                                  │
│   ├─ typeMapId = "strict-sensors"                                        │
│   ├─ typeDiscriminatorPath = "_type"                                     │
│   ├─ fallbackStrategy = ERROR                                            │
│   └─ fallbackEClass = null                                               │
│                                                                          │
│                        ↓ available at deserialization                     │
│                                                                          │
│ Deserializer                                                             │
│   1. Resolve DiscriminatorConfig for hintEClass (Sensor)                 │
│   2. Extract mapId, discriminatorPath, fallbackStrategy                  │
│   3. Read discriminator value from discriminatorPath field               │
│   4. Call TypeDiscriminatorService.resolve(mapId, value, resolver)       │
│   5. Handle fallback per fallbackStrategy                                │
└──────────────────────────────────────────────────────────────────────────┘
```

#### 7.1.4 Example: SensorHub Deserialization

Complete walkthrough using the Sensor/TempSensor model:

**Model:**
```
SensorHub
  └─ sensors: Sensor[*] (containment)

Sensor (abstract)
  ├─ typeMapping/strict-sensors: discriminatorPath="_type", fallbackStrategy=ERROR
  ├─ sensorId: String
  ├── TempSensor: typeDiscriminator="temp", celsius: double
  └── AlertSensor: typeDiscriminator="alert", message: String
```

**JSON:**
```json
{
  "_type": "http://example.org#//SensorHub",
  "sensors": [
    { "_type": "temp", "sensorId": "s-001", "celsius": 22.5 },
    { "_type": "alert", "sensorId": "s-002", "message": "Battery low" },
    { "_type": "unknown", "sensorId": "s-003" }
  ]
}
```

**Deserialization steps:**

1. **Root object:** `_type` = full URI → standard URI strategy → `SensorHub` ✓
2. **sensors[0]:** hintEClass=Sensor → DiscriminatorConfig: mapId=strict-sensors, path=_type, fallback=ERROR
   - Read `_type` = `"temp"` → `resolve("strict-sensors", "temp")` → `TempSensor` ✓
3. **sensors[1]:** same config
   - Read `_type` = `"alert"` → `resolve("strict-sensors", "alert")` → `AlertSensor` ✓
4. **sensors[2]:** same config
   - Read `_type` = `"unknown"` → `resolve("strict-sensors", "unknown")` → NOT FOUND
   - fallbackStrategy = ERROR → **Fail immediately** ✗

### 7.2 Inline Mapping — Deserialization

Inline mapping resolution is triggered when the current **EReference** has an `inlineMapping` annotation. The mapId for inline mapping is derived from the reference identity (e.g., `PersonContainer#contacts`).

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ INLINE MAPPING — DESERIALIZATION FLOW                                       │
│                                                                             │
│ Example: PersonContainer.contacts with inlineMapping annotation             │
│ JSON: { "contactType": "friend", "name": "Alice" }                        │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: JSON object, currentReference (contacts), typeKey from reference config

1. Read discriminator value from typeKey field (configured on EReference)
   → "contactType" = "friend"

2. Lookup in reference-scoped registry:
   resolveForReference(contacts, "friend")
   │
   ├─ Found → EClass URI → resolve to Friend ─────────────────→ RESOLVED ✓
   │
   └─ Not found → Apply fallbackStrategy from inlineMapping annotation:
       ├─ ERROR    → Fail immediately
       ├─ SKIP     → WARNING, continue to Type Strategy (step 3 in 06-type.md)
       └─ FALLBACK → Use fallbackEClass ─────────────────────→ RESOLVED ✓

Note: The typeKey for inline mapping comes from the EReference's codec
      annotation (e.g., typeKey="contactType"), NOT from the EClass.
      Inline mapping uses TypeDiscriminatorService.resolveForReference(),
      which targets the reference-specific registry.
```

**Key difference from Type Mapping Registry:**
- Type Mapping Registry: config comes from the **EClass** (Sensor), uses `resolve(mapId, ...)`
- Inline Mapping: config comes from the **EReference** (contacts), uses `resolveForReference(ref, ...)`
- Inline Mapping always reads from the **typeKey** field (never a nested path)

### 7.3 Combined Resolution Order

When both Type Mapping Registry and Inline Mapping could apply to the same object, the resolution follows the priority from §1.3:

```
Deserializing a contained object:
  hintEClass = reference type (e.g., Sensor, Contact)
  currentReference = the EReference being deserialized

1. Does hintEClass have a DiscriminatorConfig with mapId?
   ├─ YES → Type Mapping Registry mode (§7.1)
   │        Read from discriminatorPath, resolve via mapId
   │        On SKIP fallback → continue to step 2
   │
   └─ NO → Continue to step 2

2. Does currentReference have an inlineMapping annotation?
   ├─ YES → Inline Mapping mode (§7.2)
   │        Read from typeKey, resolve via reference registry
   │        On SKIP fallback → continue to step 3
   │
   └─ NO → Continue to step 3

3. Type Strategy Resolution (standard _type field handling)
   → See 06-type.md §6.3.0 step 3

4. Fallback (reference type, hints, root type)
   → See 06-type.md §6.3.0 step 4
```

---

## 8. Serialization Flow

This section documents the detailed serialization flow for both Type Mapping Registry and Inline Mapping. It complements the high-level type serialization flow in [06-type.md §5.0](06-type.md#50-type-serialization-flow) with implementation-level details.

### 8.1 Type Mapping Registry — Serialization

When serializing an object that has a Type Mapping Registry configured on its EClass (or base class), the serializer writes the **discriminator value** instead of the standard type URI/name.

**Config resolution:** For the EObject's EClass (e.g., `TempSensor`), resolve:
- `mapId` — from `DiscriminatorConfig.getTypeMapId()` (e.g., `strict-sensors`)
- `discriminatorPath` — from `DiscriminatorConfig.getTypeDiscriminatorPath()` (e.g., `_type` or `info.profileName`)
- `fallbackStrategy` — from `DiscriminatorConfig.getFallbackStrategy()` (default: `SKIP`)
- `fallbackEClass` — from `DiscriminatorConfig.getFallbackEClass()` (only when `FALLBACK`)

If `mapId` is present, the serializer enters **discriminator mapping mode**.

#### 8.1.1 Standard Discriminator Path (matches typeKey)

When `discriminatorPath` equals the configured type key (e.g., both are `_type`), the discriminator value is written at the same JSON field as the standard type output.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ TYPE MAPPING REGISTRY — STANDARD PATH (discriminatorPath == typeKey)        │
│                                                                             │
│ Example: TempSensor with typeMapping/strict-sensors, discriminatorPath="_type"│
│ Output: { "_type": "temp", "sensorId": "s-001", "celsius": 22.5 }         │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: EObject (TempSensor), DiscriminatorConfig (mapId, path, fallback)

1. Get discriminator value for this EClass from TARGETED registry:
   getDiscriminatorValue(mapId="strict-sensors", TempSensor)
   │
   │ Sources (checked in order):
   │   a. Reverse lookup in registry mappings (EClass URI → value)
   │   b. typeDiscriminator annotation on this EClass → "temp"
   │
   ├─ Found → discriminatorValue = "temp"
   │
   └─ Not found (EClass not in registry) → Apply fallbackStrategy:
       ├─ ERROR    → Fail immediately (throw exception)
       ├─ SKIP     → WARNING, continue to Type Strategy (step 3 in 06-type.md)
       └─ FALLBACK → Use fallbackEClass:
                     Get fallbackEClass's discriminator value from registry
                     (fallbackEClass MUST be set, else ERROR)

2. Write discriminator value at typeKey field
   → "_type": "temp"

   Do NOT proceed to Type Strategy ──────────────────────────→ DONE ✓

Note: Resolution uses getDiscriminatorValue(mapId, ...) — NOT
      getDiscriminatorValueFromAny(). This ensures the correct registry
      is used for the reverse lookup.
```

#### 8.1.2 Non-Standard Discriminator Path (nested path)

When `discriminatorPath` differs from the type key (e.g., `info.profileName`), the discriminator value is written at a **nested location** within the JSON structure. The value is typically written as part of a feature that maps to the nested path.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ TYPE MAPPING REGISTRY — NESTED PATH (discriminatorPath != typeKey)          │
│                                                                             │
│ Example: TemperatureSensor with typeMapping/lorawan, path="info.profileName"│
│ Output: { "info": { "profileName": "temp-sensor", ... }, "value": 23.5 }  │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: EObject (TemperatureSensor), DiscriminatorConfig

1. Get discriminator value (same as §8.1.1 step 1)
   → discriminatorValue = "temp-sensor"

2. The discriminator value lives at a nested feature path.
   It is written during feature serialization when the corresponding
   feature (e.g., "info.profileName") is serialized.

   Do NOT write a standard _type field.

   Do NOT proceed to Type Strategy ──────────────────────────→ DONE ✓

Note: The discriminatorPath is a dot-separated JSON path.
      For serialization, the value is placed at the correct nested
      location as part of normal feature output. The serializer must
      ensure the discriminator value is included even if the feature
      value is otherwise empty/default.
```

#### 8.1.3 Config Data Flow

The following config data must be available when serializing an object with Type Mapping Registry:

```
┌──────────────────────────────────────────────────────────────────────────┐
│ CONFIG FLOW FOR TYPE MAPPING REGISTRY — SERIALIZATION                    │
│                                                                          │
│ EObject (TempSensor)                                                     │
│   └─ EClass: TempSensor extends Sensor                                   │
│        └─ EAnnotation: typeMapping/strict-sensors                         │
│             └─ typeDiscriminator = "temp"                                 │
│                                                                          │
│ EClass (Sensor — base)                                                   │
│   └─ EAnnotation: typeMapping/strict-sensors                             │
│        ├─ typeDiscriminatorPath = "_type"                                │
│        ├─ fallbackStrategy = ERROR                                       │
│        └─ mappings: temp → TempSensor URI, alert → AlertSensor URI       │
│                                                                          │
│                        ↓ parsed at startup                               │
│                                                                          │
│ TypeDiscriminatorService                                                 │
│   └─ Registry "strict-sensors"                                           │
│        ├─ discriminatorPath = "_type"                                    │
│        ├─ fallbackStrategy = ERROR                                       │
│        ├─ forward: { "temp" → TempSensor URI, "alert" → AlertSensor URI}│
│        └─ reverse: { TempSensor URI → "temp", AlertSensor URI → "alert"}│
│                                                                          │
│ DiscriminatorConfig (for TempSensor EClass — inherited from Sensor)      │
│   ├─ typeMapId = "strict-sensors"                                        │
│   ├─ typeDiscriminatorPath = "_type"                                     │
│   ├─ fallbackStrategy = ERROR                                            │
│   └─ fallbackEClass = null                                               │
│                                                                          │
│                        ↓ available at serialization                       │
│                                                                          │
│ Serializer                                                               │
│   1. Resolve DiscriminatorConfig for EObject's EClass (TempSensor)       │
│   2. Extract mapId, discriminatorPath                                    │
│   3. Get discriminator value: getDiscriminatorValue(mapId, TempSensor)   │
│   4. Write value at discriminatorPath (= typeKey "_type")                │
│   5. Skip Type Strategy — discriminator mapping handled type output      │
└──────────────────────────────────────────────────────────────────────────┘
```

#### 8.1.4 Example: SensorHub Serialization

Complete walkthrough using the Sensor/TempSensor model:

**Model:**
```
SensorHub
  └─ sensors: Sensor[*] (containment)

Sensor (abstract)
  ├─ typeMapping/strict-sensors: discriminatorPath="_type", fallbackStrategy=ERROR
  ├─ sensorId: String
  ├── TempSensor: typeDiscriminator="temp", celsius: double
  └── AlertSensor: typeDiscriminator="alert", message: String
```

**EObjects:**
```
SensorHub
  sensors[0] = TempSensor { sensorId="s-001", celsius=22.5 }
  sensors[1] = AlertSensor { sensorId="s-002", message="Battery low" }
```

**Serialization steps:**

1. **Root object (SensorHub):** No typeMapping on SensorHub → standard URI strategy
   - Write `"_type": "http://example.org#//SensorHub"`
2. **sensors[0] (TempSensor):** DiscriminatorConfig: mapId=strict-sensors, path=_type
   - `getDiscriminatorValue("strict-sensors", TempSensor)` → `"temp"`
   - Write `"_type": "temp"` (NOT the full URI)
   - Skip Type Strategy
3. **sensors[1] (AlertSensor):** same config
   - `getDiscriminatorValue("strict-sensors", AlertSensor)` → `"alert"`
   - Write `"_type": "alert"`

**Output:**
```json
{
  "_type": "http://example.org#//SensorHub",
  "sensors": [
    { "_type": "temp", "sensorId": "s-001", "celsius": 22.5 },
    { "_type": "alert", "sensorId": "s-002", "message": "Battery low" }
  ]
}
```

### 8.2 Inline Mapping — Serialization

When serializing an object accessed through an EReference that has an `inlineMapping` annotation, the serializer writes the **inline mapping discriminator value** at the configured type key.

**Config resolution:** For the current EReference (e.g., `contacts`):
- The reference-scoped registry is identified by the reference identity (e.g., `PersonContainer#contacts`)
- `typeKey` comes from the EReference's codec annotation (e.g., `contactType`)
- `fallbackStrategy` and `fallbackEClass` come from the inlineMapping annotation

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ INLINE MAPPING — SERIALIZATION FLOW                                         │
│                                                                             │
│ Example: Friend via PersonContainer.contacts with inlineMapping             │
│ Output: { "contactType": "friend", "name": "Alice" }                      │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: EObject (Friend), currentReference (contacts)

1. Check if currentReference has inlineMapping annotation
   │
   ├─ NO → Continue to Type Strategy (step 3 in 06-type.md)
   │
   └─ YES → Get reference-scoped registry

2. Get discriminator value for this EClass:
   getDiscriminatorValueForReference(contacts, Friend)
   → Reverse lookup in inline mappings (Friend URI → "friend")
   │
   ├─ Found → discriminatorValue = "friend"
   │
   └─ Not found (EClass not in inline mappings) → Apply fallbackStrategy:
       ├─ ERROR    → Fail immediately (throw exception)
       ├─ SKIP     → WARNING, continue to Type Strategy (step 3 in 06-type.md)
       └─ FALLBACK → Use fallbackEClass:
                     Get fallbackEClass's discriminator value from registry
                     (fallbackEClass MUST be set, else ERROR)

3. Write discriminator value at typeKey (from EReference config)
   → "contactType": "friend"

   Do NOT proceed to Type Strategy ──────────────────────────→ DONE ✓
```

**Key difference from Type Mapping Registry:**
- Type Mapping Registry: config comes from the **EClass** (Sensor), uses `getDiscriminatorValue(mapId, ...)`
- Inline Mapping: config comes from the **EReference** (contacts), uses `getDiscriminatorValueForReference(ref, ...)`
- Inline Mapping always writes at the **typeKey** field (never a nested path)

#### 8.2.1 Example: PersonContainer Serialization

**Model:**
```
PersonContainer
  └─ contacts: Contact[*] (containment)
       ├─ codec annotation: typeKey="contactType"
       └─ inlineMapping: friend → Friend URI, colleague → Colleague URI

Contact (abstract)
  ├─ name: String
  ├── Friend: since: Date
  └── Colleague: department: String
```

**EObjects:**
```
PersonContainer
  contacts[0] = Friend { name="Alice", since=2020-01-01 }
  contacts[1] = Colleague { name="Bob", department="Engineering" }
```

**Serialization steps:**

1. **Root object (PersonContainer):** standard URI strategy
2. **contacts[0] (Friend):** currentReference=contacts has inlineMapping
   - `getDiscriminatorValueForReference(contacts, Friend)` → `"friend"`
   - Write `"contactType": "friend"` (using typeKey from reference config)
   - Skip Type Strategy
3. **contacts[1] (Colleague):** same reference config
   - `getDiscriminatorValueForReference(contacts, Colleague)` → `"colleague"`
   - Write `"contactType": "colleague"`

**Output:**
```json
{
  "_type": "http://example.org#//PersonContainer",
  "contacts": [
    { "contactType": "friend", "name": "Alice", "since": "2020-01-01" },
    { "contactType": "colleague", "name": "Bob", "department": "Engineering" }
  ]
}
```

### 8.3 Combined Serialization Order

When both Type Mapping Registry and Inline Mapping could apply, the serialization follows the same priority as deserialization (§7.3):

```
Serializing a contained object:
  eObject = the EObject being serialized
  currentReference = the EReference through which it's accessed

1. Does eObject's EClass (or base) have a DiscriminatorConfig with mapId?
   ├─ YES → Type Mapping Registry mode (§8.1)
   │        Get discriminator value from targeted registry
   │        Write at discriminatorPath
   │        On SKIP fallback → continue to step 2
   │
   └─ NO → Continue to step 2

2. Does currentReference have an inlineMapping annotation?
   ├─ YES → Inline Mapping mode (§8.2)
   │        Get discriminator value from reference registry
   │        Write at typeKey
   │        On SKIP fallback → continue to step 3
   │
   └─ NO → Continue to step 3

3. Type Strategy Resolution (standard _type field writing)
   → See 06-type.md §5.0 step 3
```

---

## 9. Invalid Configurations

| Misconfiguration | Severity | Reason |
|------------------|----------|--------|
| `typeMapping/{mapId}` annotation on EReference | ERROR | Type mapping registry is class-level, not per-reference |
| `typeDiscriminator` in main codec on EReference | ERROR | Discriminator values are per-class, not per-reference |
| `typeDiscriminatorPath` in main codec on EReference | ERROR | Discriminator path is defined on base class |
| `inlineMapping` annotation on EClass | WARNING | Inline mappings are per-reference only |
| `inlineMapping` annotation on EAttribute | ERROR | Inline mappings are per-reference only |

---

## 10. Summary

| Feature | Annotation Source | Applies To | Use Case |
|---------|------------------|------------|----------|
| **Type Mapping Registry** | `http://eclipse.org/fennec/codec/typeMapping/{mapId}` | EClass | Centralized or distributed type mappings |
| **Inline Mapping** | `http://eclipse.org/fennec/codec/inlineMapping` | EReference | Per-reference type mappings |

**Key differences:**
- Type Mapping Registry defines mappings on the base class (centralized) or concrete classes (distributed)
- Inline Mapping defines mappings on the EReference that accesses the objects
- Both support fallback strategies (`ERROR`, `SKIP`, `FALLBACK`)

---

[Next: ID Serialization →](09-id.md)
