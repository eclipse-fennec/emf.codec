# Configuration Resolution

[← Architecture](01-architecture.md)

---

This chapter defines how configuration is resolved across all codec features. Understanding this hierarchy is essential as it applies to every serialization target (type, ID, reference, supertype) and feature.

## 1. Two-Dimensional Configuration Model

Configuration resolution operates across **two orthogonal dimensions**:

1. **Source Hierarchy (Vertical)**: Where configuration comes from (runtime vs. static)
2. **Scope Chain (Horizontal)**: What EMF element configuration applies to (feature vs. class vs. global)

This model allows fine-grained control: you can set a global default in the model, override it per-class at runtime, and further override it per-feature for specific operations.

---

## 2. Dimension 1: Source Hierarchy (Vertical)

Configuration can be provided at different sources. **Higher priority sources override lower ones.**

| Priority | Source | Lifecycle | Configuration Methods |
|:--------:|--------|-----------|----------------------|
| 1 (highest) | **Load/Save Options** | Per-operation | Property map |
| 2 | **Resource** | Per-resource | Property map, ConfigBuilder |
| 3 | **ResourceFactory** | Per-factory | Property map, ConfigBuilder |
| 4 | **Jackson Module Config** | Per-codec | Property map, ConfigBuilder |
| 5 | **EAnnotations** | Per-model (static) | Ecore model annotations |
| 6 (lowest) | **Built-in Defaults** | Global | Hardcoded in codec |

**Key principle:** Dynamic overrides static. Runtime settings always win over model-defined settings.

```
Load/Save Options    ← dynamic, per-operation
        ↓
Resource config      ← can be set when creating resource
        ↓
ResourceFactory      ← factory-wide defaults
        ↓
Jackson Module       ← codec-wide defaults
        ↓
EAnnotations         ← static, defined in .ecore model
        ↓
Built-in Defaults    ← hardcoded fallback
        ↓
    ════════════════════════
    │ Effective Value      │
    ════════════════════════
```

---

## 3. Dimension 2: Scope Chain (Horizontal)

Within any single source level, configuration can be scoped to different EMF elements. **More specific scopes override less specific ones.**

| Priority | Scope | Applies To |
|:--------:|-------|------------|
| 1 (highest) | **EReference/EAttribute** | Single feature |
| 2 | **EClass** | All instances of that class |
| 3 | **EPackage** | All classes in that package |
| 4 | **Global** | All objects in codec (with optional StrategyScope) |
| 5 (lowest) | **Default** | Built-in fallback |

```
EReference annotation    ← most specific
        ↓
EClass annotation        ← class-level default
        ↓
EPackage annotation      ← package-wide default
        ↓
Global config            ← codec-wide (respects StrategyScope)
        ↓
Built-in Default         ← hardcoded fallback
        ↓
    ════════════════════════
    │ Effective Value      │
    ════════════════════════
```

### 3.1 The EPackage Scope Is Annotation-Internal

The EPackage scope exists **only within the annotation source**. It is merged into each class's
annotation configuration when the package profile is built, so the vertical source hierarchy of
§2 keeps seeing exactly **one** annotation layer — options, resource, factory and module are
unaffected by its existence.

Two consequences follow from that, and both are deliberate:

- **Merging is per property.** A class that configures one key inherits the package's remaining
  keys rather than resetting them to built-in defaults. A class states an *exception* to its
  package.
- **The layering is driven by which keys an annotation actually contains**, not by comparing
  values against defaults. A class explicitly restating a default value still overrides its
  package — the two are different statements, and only the annotation's own key set can tell
  them apart.

**Resolution uses the directly owning package only.** Subpackage hierarchies are not traversed:
the codec resolves through `EClass.getEPackage()` everywhere and never walks
`getESubpackages()` / `getESuperPackage()`.

**Scopes do not participate** (`typeScope`, `typeFormatScope`, `idScope`, `idFormatScope`). A
scope says *where in a document* a setting applies, which is a property of the operation, not of
a model — see [16-annotation-reference.md](16-annotation-reference.md) and
[99-open-questions.md Q1](99-open-questions.md#q1-epackage-scope-level).

---

## 4. Combined Resolution Algorithm

When resolving a configuration value, **both dimensions are considered**:

```
For property P on EReference R of EClass C:

1. Check Load/Save options for R.P, then C.P, then global P
2. Check Resource config for R.P, then C.P, then global P
3. Check ResourceFactory for R.P, then C.P, then global P
4. Check Jackson Module for R.P, then C.P, then global P
5. Check EAnnotation on R for P
6. Check EAnnotation on C for P
7. Check global config for P (respecting StrategyScope)
8. Use built-in default for P

First non-null value wins.
```

**Example Resolution:**

For `typeStrategy` on `Person.address` (an EReference):

1. Load options: `Person.address.typeStrategy` → not set
2. Load options: `Person.typeStrategy` → not set
3. Load options: `typeStrategy` → not set
4. Resource config: same pattern → not set
5. ResourceFactory: same pattern → not set
6. Jackson Module: same pattern → not set
7. EAnnotation on `address` feature: `@CODEC(codec.type="...")` → not set
8. EAnnotation on `Person` class: `@CODEC(codec.typeStrategy="NAME")` → **found: NAME**

Result: `typeStrategy = NAME`

> **Resolution identity — instance, not name.** Class- and feature-level configuration — from **every**
> source, annotations included — is matched against the concrete **`EClass` / `EStructuralFeature`
> instance** (object identity; EMF does not override `equals`/`hashCode`). It is **never** keyed on the bare
> class name or a type URI. Two same-named `EClass`es from two package versions sharing one nsURI are
> **distinct instances** and resolve to **their own** configuration. The instance reaches the resolver from
> the object being (de)serialized (`eObject.eClass()`) or from a root-type hint held as an `EClass`.

---

## 5. StrategyScope (Global Level Modifier)

At the **Global** level (codec-wide configuration), certain properties support **StrategyScope** to control where they apply. This provides a middle ground between "everywhere" and "nowhere."

| StrategyScope | Applies To |
|---------------|------------|
| `ALL` | Root + all containments + all non-containments **(default)** |
| `ROOT_ONLY` | Root object only |
| `ROOT_CONTAINMENT` | Root + objects accessed via containment references |
| `ROOT_NON_CONTAINMENT` | Root + objects accessed via non-containment references |

**Example:** Global `typeStrategy=NAME` with `typeScope=ROOT_ONLY`:
- Root object uses NAME strategy
- All nested objects fall back to default (URI)

**Configuration:**
```java
CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.NAME)
    .typeScope(StrategyScope.ROOT_ONLY)  // Only applies to root
    .build();
```

**EAnnotation equivalent:**
```xml
<eAnnotations source="http://eclipse.org/fennec/codec">
  <details key="codec.typeStrategy" value="NAME"/>
  <details key="codec.typeScope" value="ROOT_ONLY"/>
</eAnnotations>
```

---

## 6. Configuration Dependencies

Certain settings have logical dependencies. When a parent setting is disabled, dependent settings are implicitly disabled.

### Type and SuperType Dependency

| `serializeType` | `serializeSuperTypes` (configured) | `serializeSuperTypes` (effective) |
|-----------------|-----------------------------------|-----------------------------------|
| `true` | `true` | `true` |
| `true` | `false` | `false` |
| `false` | `true` | `false` (implicitly disabled) |
| `false` | `false` | `false` |

**Rationale:** Supertypes are type information. If type serialization is disabled, serializing supertypes would be inconsistent.

### Smart Compression Dependencies

Smart compression affects multiple targets:
- Type serialization (omit when inferable)
- Reference type (omit when equals declared type)
- Same-schema names (use simple name instead of URI)

---

## 7. EAnnotation and Config Builder Parity

Every codec feature that can be configured via **EAnnotations** can also be configured via **Config Builder**, and vice versa.

**EAnnotation (static, in model):**
```xml
<eStructuralFeatures name="firstName">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="key" value="first_name"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Config Builder (dynamic, at runtime):**
```java
FeatureSerializationConfig.builder()
    .feature("firstName")
    .key("first_name")
    .build();
```

**Load/Save options (per-operation override):**
```java
Map<String, Object> options = new HashMap<>();
options.put("Person.firstName.key", "givenName");  // Overrides both above
resource.save(outputStream, options);
```

---

## 8. EffectiveConfig Pattern

The codec resolves configuration **per conversion step**, lazily and keyed by concrete EMF instance. It does
**not** build a single process-global name→config map.

```java
// Illustrative only — not a literal API. In code, ConfigurationResolver merges
// lazily per instance and EffectiveCodecConfig is a thin delegator over it.
EffectiveCodecConfig effectiveConfig = /* built from all levels for this operation */;

// Per-class / per-feature configs, cached by INSTANCE:
ClassConfig   classConfig   = effectiveConfig.resolveClassConfig(personClass);
FeatureConfig featureConfig = effectiveConfig.resolveFeatureConfig(firstNameFeature);
```

**Instance identity, not name.** The per-class and per-feature caches are keyed by the **`EClass` /
`EStructuralFeature` instance** (`computeIfAbsent`), never by class name or type URI. The annotation layer is
sourced from `MetadataService.getClassMetadata(EClass)` / `getClassProfile(EClass, "codec")` — the
fingerprinted `PackageMetadata` is the entry point (see
[fingerprinting workdoc](../codec-v2-fingerprinting-workdoc.md), F5) — and the **derived** config is
memoized in that same instance cache, so the MetadataService is not re-queried on every call. No bare-name
map sits between the metadata and the resolved config; two same-named `EClass`es from two package versions of
one nsURI are distinct instances and resolve to their own config.

This ensures:
- Configuration for a given instance is resolved **once per operation**, then cached — not recomputed on
  every serialization call.
- The resolved view is **immutable per selected package version** and is a **per-load derived view**, not a
  process-global snapshot.
- Lazy, instance-keyed caching optimizes memory for large models and is naturally multi-version-correct.

> **Version selection / pinning — forward reference, `[not yet implemented]`.** *Which* package version an
> nsURI resolves to (when several coexist under one nsURI) is decided by the per-load **pin** — see the
> [fingerprinting workdoc](../codec-v2-fingerprinting-workdoc.md) §2b `[DECIDED]`. A.1 delivers the
> instance-**identity** part above (the caller already holds the concrete instance); the pin/selection layer
> lands in Phase A.3 / Phase B.

> **Behavior change — multi-version correctness `[A.1]`.** Because config matches by instance, an `EClass`
> that is **not registered** in the MetadataService no longer silently inherits the config of a same-named,
> **registered** class. Previously the name-keyed bridge applied a registered class's config to any
> same-named class. This is the one intended deviation from prior observable behavior; everything else is
> behavior-stable.

---

## 9. ResourceFactory Configuration

The `CodecResourceFactory` provides Level 2 configuration in the hierarchy. It supports both constructor-based and setter-based configuration for flexibility with different environments.

### 9.1 Constructor-Based Configuration (Recommended)

For programmatic use, constructor injection is preferred:

```java
// Minimal - uses default configuration
CodecResourceFactory factory = new CodecResourceFactory(metadataService);

// With custom configuration
CodecConfiguration config = CodecConfiguration.builder()
    .smartCompression(true)
    .typeStrategy(TypeStrategy.NAME)
    .build();
CodecResourceFactory factory = new CodecResourceFactory(metadataService, config);

// With custom Jackson mapper
JsonMapper.Builder mapperBuilder = JsonMapper.builder()
    .enable(SerializationFeature.INDENT_OUTPUT);
CodecResourceFactory factory = new CodecResourceFactory(metadataService, config, mapperBuilder);
```

### 9.2 Setter-Based Configuration (DI Frameworks)

For dependency injection frameworks (Spring, CDI, OSGi DS), use the parameterless constructor with setters:

```java
// Parameterless constructor for DI
CodecResourceFactory factory = new CodecResourceFactory();

// Required: MetadataService must be set before creating resources
factory.setMetadataService(metadataService);

// Optional: Custom configuration (defaults to CodecConfiguration.defaults())
factory.setConfiguration(config);

// Optional: Custom Jackson mapper
factory.setMapperBuilder(mapperBuilder);

// Optional: Default load/save options
factory.setDefaultLoadOptions(loadOptions);
factory.setDefaultSaveOptions(saveOptions);
```

### 9.3 OSGi Declarative Services Example

```java
@Component(service = Resource.Factory.class)
public class CodecResourceFactoryComponent extends CodecResourceFactory {

    @Reference
    public void setMetadataService(MetadataService metadataService) {
        super.setMetadataService(metadataService);
    }

    @Activate
    public void activate(Map<String, Object> properties) {
        // Build configuration from Config Admin properties
        CodecConfiguration config = CodecConfiguration.builder()
            .fromProperties(properties)
            .build();
        setConfiguration(config);
    }
}
```

### 9.4 Spring Configuration Example

```java
@Configuration
public class CodecConfig {

    @Bean
    public CodecResourceFactory codecResourceFactory(MetadataService metadataService) {
        CodecResourceFactory factory = new CodecResourceFactory();
        factory.setMetadataService(metadataService);
        factory.setConfiguration(CodecConfiguration.builder()
            .smartCompression(true)
            .build());
        return factory;
    }
}
```

### 9.5 Default Options

Default load/save options are applied to all resources created by the factory:

```java
Map<Object, Object> defaultLoadOptions = new HashMap<>();
defaultLoadOptions.put(CodecResource.CODEC_ROOT_SCHEMA, "http://example.org/1.0");

factory.setDefaultLoadOptions(defaultLoadOptions);

// All resources created by this factory will use these defaults
Resource resource = factory.createResource(uri);
resource.load(inputStream, null);  // Uses factory defaults
```

Per-operation options override factory defaults:
```java
Map<Object, Object> operationOptions = new HashMap<>();
operationOptions.put(CodecResource.CODEC_ROOT_SCHEMA, "http://other.org/2.0");

resource.load(inputStream, operationOptions);  // Overrides factory default
```

---

## 10. Jackson Configuration

The `JsonMapper.Builder` can be provided at different levels:

| Level | How to Provide | Scope |
|-------|----------------|-------|
| **ResourceFactory** | Constructor parameter or `setMapperBuilder()` | All resources from factory |
| **CodecResource** | Constructor parameter | Single resource |
| **Default** | `null` → `JsonMapper.builder()` | Built-in default |

**Example:**
```java
JsonMapper.Builder customBuilder = JsonMapper.builder()
    .enable(SerializationFeature.INDENT_OUTPUT)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

CodecResourceFactory factory = new CodecResourceFactory(
    metadataService,
    codecConfig,
    customBuilder  // All resources use this base configuration
);
```

The codec applies its serializers/deserializers on top of the provided base configuration.

---

## 11. Property Matrix (Complete Reference)

This section provides the **complete property matrix** showing all configuration properties with their valid levels, directions, and defaults. This is the authoritative reference for the 3D configuration resolution.

### 11.1 Three-Dimensional Resolution

Configuration resolution operates across **three dimensions**:

1. **Sources (Vertical)**: Load/Save → Resource → ResourceFactory → Module → Annotation → Default
2. **Levels (Horizontal)**: Feature → EClass → Global → Default
3. **Direction**: Serialization (write), Deserialization (read), or Both

For any property lookup:
1. **Level resolution** (most specific wins): Feature → EClass → Global → Default
2. **Source resolution** (highest priority wins): Options → ResourceFactory → Module → Annotation → Default
3. **Direction filter**: Property must apply to current direction (read or write)

### 11.2 Legend

| Symbol | Meaning |
|--------|---------|
| G | Global level |
| P | EPackage level (annotation-internal, see [§3.1](#31-the-epackage-scope-is-annotation-internal)) |
| C | EClass level |
| F | Feature (EAttribute/EReference) level |
| R | Read direction (deserialization) |
| W | Write direction (serialization) |
| RW | Both directions |
| (R)W | Write primary, Read potential future feature |
| R(W) | Read primary, Write potential future feature |

### 11.3 Type Properties

| Property | Levels | Direction | Default | Spec Section |
|----------|--------|-----------|---------|--------------|
| `typeStrategy` | G, P, C, F | RW | `URI` | 06-type.md |
| `typeKey` | G, P, C, F | RW | `_type` | 06-type.md |
| `typeFormat` | G, P, C, F | RW | `PLAIN` | 06-type.md |
| `typeSchemaKey` | G, P, C, F | RW | `schema` | 06-type.md |
| `typeNameKey` | G, P, C, F | RW | `type` | 06-type.md |
| `typeScope` | G | RW | `ALL` | 06-type.md |
| `typeFormatScope` | G | RW | `ALL` | 06-type.md |
| `typeValueReaderName` | G, C | R | `null` | 06-type.md |
| `typeValueWriterName` | G, C | W | `null` | 06-type.md |
| `fingerprintMode` | G, P, C | W | `NONE` | 06-type.md §8 |
| `fingerprintKey` | G, P, C | W + R (read: caller-side levels only) | `fingerprint` | 06-type.md §8 |

> **`fingerprintKey` read restriction:** on the **read** side the key is taken from
> caller-side levels only (options, resource, factory, module), never from the model
> annotation, and the default key is always accepted in addition. This is not an
> inconsistency but the resolution of a genuine cycle — see
> [06-type.md §8.5](06-type.md#85--the-fingerprintkey-chicken-and-egg-problem).

### 11.4 ID Properties

| Property | Levels | Direction | Default | Spec Section |
|----------|--------|-----------|---------|--------------|
| `idStrategy` | G, P, C | RW | `ID_FIELD` | 09-id.md |
| `idKey` | G, P, C, F | RW | `_id` | 09-id.md |
| `idValueKey` | G, P, C | RW | `id` | 09-id.md |
| `idFormat` | G, P, C, F | RW | `PLAIN` | 09-id.md |
| `idKeyMode` | G, P, C | RW | `ID_ONLY` | 09-id.md |
| `idFeatures` | P, C | RW | `[]` | 09-id.md |
| `idSeparator` | G, P, C | RW | `-` | 09-id.md |
| `idSeparatorKey` | G, P, C | RW | `separator` | 09-id.md |
| `idSeparatorSerialize` | G, C | (R)W | `true` | 09-id.md |
| `idOnTop` | G, P, C | (R)W | `false` | 09-id.md |
| `idScope` | G | RW | `ALL` | 09-id.md |
| `idFormatScope` | G | RW | `ALL` | 09-id.md |
| `idValueReaderName` | G, P, C | R | `null` | 09-id.md |
| `idValueWriterName` | G, P, C | W | `null` | 09-id.md |

### 11.5 Feature Properties

| Property | Levels | Direction | Default | Spec Section |
|----------|--------|-----------|---------|--------------|
| `key` | F | RW | (feature name) | 11-feature.md |
| `ignore` | G, C, F | RW | `false` | 11-feature.md |
| `ignoreRead` | G, C, F | R | `false` | 11-feature.md |
| `ignoreWrite` | G, C, F | W | `false` | 11-feature.md |
| `forceRead` | G, C, F | R | `false` | 11-feature.md |
| `forceWrite` | G, C, F | W | `false` | 11-feature.md |
| `serializeNull` | G, C, F | (R)W | `false` | 11-feature.md |
| `serializeEmpty` | G, C, F | (R)W | `false` | 11-feature.md |
| `serializeDefault` | G, C, F | (R)W | `false` | 11-feature.md |
| `enumSerialization` | G, F | RW | `LITERAL` | 11-feature.md |
| `valueReaderName` | G, C, F | R | `null` | 14-custom-values.md |
| `valueWriterName` | G, C, F | W | `null` | 14-custom-values.md |

> **Note:** `enumSerialization` applies to EAttributes only (not EReferences). Valid values: `LITERAL`, `VALUE`, `NAME`.

### 11.6 Reference Properties

| Property | Levels | Direction | Default | Spec Section |
|----------|--------|-----------|---------|--------------|
| `refFormat` | G, F | RW | `STRUCTURED` | 10-reference.md |
| `refKey` | G, F | RW | `$ref` | 10-reference.md |
| `refTypeKey` | G, F | RW | `_type` | 10-reference.md |
| `proxyKey` | G, F | RW | `$proxy` | 10-reference.md |
| `expand` | G, C, F | RW | `false` | 10-reference.md |
| `expandGlobal` | G, C | RW | `false` | 10-reference.md |
| `expandDepth` | G, C, F | RW | `1` | 10-reference.md |
| `expandIgnoreBidirectional` | G, C, F | (R)W | `true` | 10-reference.md |
| `serializeInstanceType` | G, F | W | `true` | 12-polymorphism.md |
| `loadReferencedResources` | G | R | `false` | 10-reference.md |
| `refUriSchemes` | G | R | `[]` | 10-reference.md |

> **Note:** `serializeInstanceType=false` is write-only and may cause deserialization failures if reference type is abstract/interface.
>
> **`loadReferencedResources` / `refUriSchemes`:** a reference URI is data from the document, so
> resolution stays inside what is already in memory unless `loadReferencedResources=true` says
> otherwise, and `refUriSchemes` names the schemes a document may use at all (empty = every
> scheme, with a warning for the ones that leave the process). Both are global only and are not
> REST client-overridable - a request must not be able to widen them.
>
> **`expand` vs `expandGlobal`:**
> - `expand` at F level: `true`/`false` - expand this specific reference
> - `expand` at G/C level: list of EReference names/instances to expand
> - `expandGlobal`: `true`/`false` - expand ALL non-containment references
>
> A reference is expanded if `expandGlobal=true` OR the reference is in the `expand` list.

### 11.7 Annotation-Only Directives

These keys are valid only in EAnnotations and control how annotations are processed. They do NOT participate in the property resolution matrix.

| Annotation Key | Levels | Default | Description |
|----------------|--------|---------|-------------|
| `inherit` | G, C | `DIRECT` | Controls EAnnotation inheritance across EClass hierarchy |

**`inherit` values:**
- `DIRECT` (default): Inherit from immediate parent EClass only
- `ALL`: Inherit from full hierarchy up to EObject
- `NONE`: No inheritance, use only this EClass's annotations

> **Note:** `inherit` affects how `CodecAspectProvider` resolves annotations when building AspectConfig. It is consumed during annotation parsing, not during runtime property resolution.

> **⚠ Not yet implemented (code reality, 2026-07-24).** Annotation inheritance across the EClass hierarchy
> is currently applied **only to type configuration** (`ConfigurationResolver.resolveTypeConfig` walks
> `EClass.getEAllSuperTypes()`, parents-first, child-overrides). It is **not** applied to id / feature /
> reference / class / discriminator config. The `inherit` **modes** `DIRECT` and `NONE` are **not honored**
> — the only behavior in code is the full transitive walk (equivalent to `ALL`), and the `codec.inherit`
> option constant has no consumers. Treat the `inherit=DIRECT|NONE` semantics and non-type-config
> inheritance as a documented future capability, not current behavior (convention as 99 §2.1).

### 11.8 Discriminator Mapping Properties

| Property | Levels | Direction | Default | Spec Section |
|----------|--------|-----------|---------|--------------|
| `typeMapId` | C | RW | `null` | 08-discriminator-mapping.md |
| `typeDiscriminatorPath` | C | RW | `null` | 08-discriminator-mapping.md |
| `typeDiscriminator` | C | RW | `null` | 08-discriminator-mapping.md |
| `typeMappings` | C | RW | `null` | 08-discriminator-mapping.md |
| `inlineMappings` | F | RW | `null` | 08-discriminator-mapping.md |
| `discriminatorPath` | F | RW | `null` | 08-discriminator-mapping.md |
| `discriminatorValue` | F | RW | `null` | 08-discriminator-mapping.md |
| `fallbackStrategy` | G, C, F | R(W) | `SKIP` | 08-discriminator-mapping.md |
| `fallbackEClass` | F | R(W) | `null` | 08-discriminator-mapping.md |

### 11.10 SuperType Properties

| Property | Levels | Direction | Default | Spec Section |
|----------|--------|-----------|---------|--------------|
| `superTypeSerialize` | G, P, C | (R)W | `false` | 07-supertype.md |
| `superTypeKey` | G, P, C | (R)W | (format-dependent)¹ | 07-supertype.md |
| `superTypeStrategy` | G, P, C | (R)W | `ALL` | 07-supertype.md |
| `superTypeAsArray` | G, P, C | (R)W | `true` | 07-supertype.md |
| `superTypeSeparator` | G, P, C | (R)W | `,` | 07-supertype.md |
| `superTypeFormat` | G, P, C | (R)W | (inherits typeFormat) | 07-supertype.md |
| `superTypeValueReaderName` | G, C | R | `null` | 07-supertype.md |
| `superTypeValueWriterName` | G, C | W | `null` | 07-supertype.md |

> ¹ `superTypeKey` default: PLAIN format → `_supertype`, STRUCTURED format → `supertype`.
> Note: `superTypeSchemaKey` removed - inherits from TypeConfig. `superTypeNameKey` removed - `superTypeKey` has format-dependent default.

### 11.11 Strictness Properties

| Property | Levels | Direction | Default | Spec Section |
|----------|--------|-----------|---------|--------------|
| `strictOnUnknown` | G, C | R | `false` | 11-feature.md |
| `strictOnMissing` | G, C | R | `false` | 11-feature.md |
| `deserializationMode` | G, C | R | `LENIENT` | 13-load-save-options.md |

### 11.12 Global Options

| Property | Levels | Direction | Default | Spec Section |
|----------|--------|-----------|---------|--------------|
| `smartCompression` | G | RW | `false` | 05-global-options.md |
| `ignoreFeatures` | G, C | RW | `[]` | 05-global-options.md |
| `fieldOrder` | G | (R)W | `DECLARATION` | 05-global-options.md |
| `metadataFieldsFirst` | G | (R)W | `true` | 05-global-options.md |
| `useNamesFromExtendedMetadata` | G | RW | `false` | 11-feature.md |

> **Note:** `useNamesFromExtendedMetadata` is a runtime-only option (no EAnnotation support). When `true`, ExtendedMetaData `name` annotations are used as JSON keys (priority: codec annotation > ExtendedMetaData > feature name).

### 11.13 Load/Save Options (Runtime Only)

These options are only available at runtime via load/save option maps, not via EAnnotations.

| Property | Levels | Direction | Default | Spec Section |
|----------|--------|-----------|---------|--------------|
| `rootType` | G | R | `null` | 13-load-save-options.md |
| `rootSchema` | G | R | `null` | 13-load-save-options.md |
| `rootFingerprint` | G | R | `null` | 13-load-save-options.md |
| `featureTypeHints` | G | R | `null` | 13-load-save-options.md |
| `typeHintMode` | G | R | `HINT` | 13-load-save-options.md |
| `valueReaders` | G | R | `null` | 13-load-save-options.md |
| `valueWriters` | G | W | `null` | 13-load-save-options.md |
| `featureValueReaders` | G | R | `null` | 13-load-save-options.md |
| `featureValueWriters` | G | W | `null` | 13-load-save-options.md |
| `featureValueReaderInstances` | G | R | `null` | 13-load-save-options.md |
| `featureValueWriterInstances` | G | W | `null` | 13-load-save-options.md |

### 11.14 Direction Notes

Properties marked with `(R)W` or `R(W)` indicate:

- **(R)W**: Write is primary use case, Read is potential future feature
  - `idSeparatorSerialize`: R needed to split concatenated ID back into components
  - `idOnTop`: R for ordered parsing/validation
  - `serializeNull/Empty/Default`: R for validation of intentional values
  - `superType*`: R for inheritance hierarchy validation

- **R(W)**: Read is primary use case, Write is potential future feature
  - `fallbackStrategy/fallbackEClass`: W needs read/write config separation design

---

## 12. Load/Save Option Keys Registry

This section documents all option keys that can be passed to `resource.load(options)` or `resource.save(options)`.

### 11.1 Resource-Level Options

These options are defined in `CodecResource`:

| Option Key | Type | Direction | Description |
|------------|------|-----------|-------------|
| `CODEC_ROOT_TYPE` | `EClass` or `String` (URI) | Load | Type hint for root object deserialization |
| `CODEC_ROOT_SCHEMA` | `String` (URI) | Load | Context schema for NAME strategy resolution |

**Usage:**
```java
Map<String, Object> options = new HashMap<>();
options.put(CodecResource.CODEC_ROOT_TYPE, PersonPackage.Literals.PERSON);
options.put(CodecResource.CODEC_ROOT_SCHEMA, "http://example.org/person/1.0");
resource.load(inputStream, options);
```

### 11.2 Context Options

These options are defined in `ContextHelper` and affect the Jackson serialization/deserialization context:

| Option Key | Type | Direction | Description |
|------------|------|-----------|-------------|
| `CODEC_FEATURE_TYPE_HINTS` | `Map<EStructuralFeature, EClass>` | Load | Type hints for specific features |
| `CODEC_FEATURE_VALUE_READERS` | `Map<EStructuralFeature, String>` | Load | Custom value readers per feature |
| `CODEC_FEATURE_VALUE_WRITERS` | `Map<EStructuralFeature, String>` | Save | Custom value writers per feature |

**Usage:**
```java
Map<EStructuralFeature, EClass> typeHints = new HashMap<>();
typeHints.put(GeoPackage.Literals.FEATURE__GEOMETRY, GeoPackage.Literals.POINT);

Map<String, Object> options = new HashMap<>();
options.put(ContextHelper.FEATURE_TYPE_HINTS, typeHints);
resource.load(inputStream, options);
```

### 11.3 Internal Context Keys (Read-Only)

These keys are used internally by the codec and should not be set by users:

| Option Key | Type | Description |
|------------|------|-------------|
| `CODEC_EXPECTED_TYPE` | `EClass` | Current expected type during deserialization |
| `CODEC_UNRESOLVED_REFERENCES` | `List<UnresolvedReference>` | Collected unresolved references |
| `CODEC_DIAGNOSTIC_COLLECTOR` | `DiagnosticCollector` | Error/warning collection |
| `CODEC_SUPPRESS_TYPE` | `Boolean` | Smart compression type suppression flag |
| `CODEC_CONTEXT_SCHEMA_URI` | `String` | Current context schema for smart compression |
| `CODEC_ROOT_SERIALIZED` | `Boolean` | Root object serialization complete flag |
| `CODEC_FEATURE_TYPE_HINT` | `EClass` | Current feature's type hint |

### 11.4 Property Map Format

For configuration via property maps (OSGi Config Admin, Spring, etc.), keys follow this pattern:

```
codec.<target>.<property>
```

**Examples:**
```properties
# Global settings
codec.smartCompression=true
codec.sortPropertiesAlphabetically=false

# Type settings
codec.type.strategy=NAME
codec.type.key=_type

# ID settings
codec.id.enabled=true
codec.id.onTop=true
codec.id.key=_id

# Per-class settings (using fully qualified class name)
codec.Person.type.strategy=URI
codec.Person.id.features=firstName,lastName

# Per-feature settings
codec.Person.firstName.key=first_name
codec.Person.firstName.serialize=true
```

> **⚠ Per-class / per-feature *string* addressing is not yet implemented (code reality, 2026-07-24).** The
> dotted `codec.<ClassName>.<property>` and `codec.<ClassName>.<feature>.<property>` forms shown above are
> **not parsed** into per-class/per-feature config by any current source (no hierarchical key splitting
> exists). Per-class/per-feature caller config today is supplied only through the **instance-keyed**
> channels (`CODEC_ECLASS_CONFIG`, `CODEC_EREFERENCE_CONFIG`, `CODEC_EATTRIBUTE_CONFIG` — keyed by `EClass`/
> `EReference`/`EAttribute` instance) or via model annotations. When the string form is implemented, it is
> **version-agnostic by design** (applies to all versions of a class name) — see the fingerprinting
> workdoc A.5. Until then, treat these examples as illustrative of the intended key shape, not as a working
> feature.

### 11.5 Java Constants

All option keys are available as Java constants:

```java
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.context.ContextHelper;

// Resource options
CodecResource.CODEC_ROOT_TYPE
CodecResource.CODEC_ROOT_SCHEMA

// Context options
ContextHelper.FEATURE_TYPE_HINTS
ContextHelper.FEATURE_VALUE_READERS
ContextHelper.FEATURE_VALUE_WRITERS
```

### 11.6 Type-Safe Configuration (Preferred)

For most use cases, the type-safe `CodecConfiguration.Builder` is preferred over raw option maps:

```java
CodecConfiguration config = CodecConfiguration.builder()
    // Global settings
    .smartCompression(true)
    .typeStrategy(TypeStrategy.NAME)

    // ID settings
    .idKeyMode(IdKeyMode.ID_ONLY)
    .idOnTop(true)
    .idKey("_id")

    // Per-class overrides
    .forClass(PersonPackage.Literals.PERSON)
        .typeStrategy(TypeStrategy.URI)
        .idFeatures("firstName", "lastName")
        .done()

    .build();

CodecResourceFactory factory = new CodecResourceFactory(metadataService, config);
```

The builder approach provides:
- Compile-time type safety
- IDE auto-completion
- Validation of dependent settings

### 11.7 Direction-Specific Configuration

Properties can be scoped to serialization, deserialization, or both using prefixes:

| Prefix | Applies To | Example |
|--------|------------|---------|
| `codec.` | Both ser and deser | `codec.typeStrategy=NAME` |
| `codec.ser.` | Serialization only | `codec.ser.typeStrategy=NAME` |
| `codec.deser.` | Deserialization only | `codec.deser.typeStrategy=URI` |

**Use Case:** Different strategies for serialization vs deserialization:
```java
Map<String, Object> properties = Map.of(
    "codec.ser.typeStrategy", "NAME",      // Serialize with simple names
    "codec.deser.typeStrategy", "URI"      // Deserialize expects full URIs
);

// Build separate configs
CodecConfiguration serConfig = ConfigBuilder.fromMap(properties).buildSerializationConfig();
// serConfig.typeStrategy = NAME

CodecConfiguration deserConfig = ConfigBuilder.fromMap(properties).buildDeserializationConfig();
// deserConfig.typeStrategy = URI
```

**Precedence:** Specific prefix overrides general prefix:
```java
Map<String, Object> properties = Map.of(
    "codec.typeStrategy", "URI",           // Default for both
    "codec.ser.typeStrategy", "NAME"       // Override for serialization only
);
// Serialization: NAME (specific override)
// Deserialization: URI (general default)
```

**When to use direction-specific configuration:**
- External API writes URIs but you want to serialize simple names
- Legacy system compatibility (read old format, write new format)
- Testing/migration scenarios

**EAnnotation alternative:**
Direction-specific configuration is primarily a runtime concern and is not supported via EAnnotations. Use property maps or CodecConfiguration builder for direction-specific settings.

---

[Next: Naming Conventions →](03-naming-conventions.md)
