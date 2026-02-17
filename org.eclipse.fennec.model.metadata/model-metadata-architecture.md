# Architecture Specification: EMF Model Metadata Service

## 1. Executive Summary

This document outlines the architecture for a **Model Metadata Service**, a centralized runtime metadata registry for an EMF-based framework.

**The Goal:** To decouple technical concerns (Serialization/Codecs, ORM mapping, Historization strategies, Units of Measurement) from the core domain model (Ecore). Instead of relying on slow runtime parsing of `EAnnotations` or polluting the domain model with technical attributes, we establish a dedicated "Shadow Model" (Metadata Layer) that is computed once at startup.

**Implementation:** The metadata layer is itself an EMF model (`metadata.ecore`), enabling:
- Type-safe access to configuration
- Serialization/persistence of pre-computed metadata
- Extension via EMF inheritance (codec aspects, ORM aspects, etc.)

## 2. Problem Statement & Motivation

- **Performance:** Iterating over `EAnnotations` and parsing string-based details during high-frequency operations (like serialization or DB writes) is inefficient.
- **Separation of Concerns:** The domain model (`WeatherForecast`, `PVPlant`) should not contain hard-coded logic for SQL column names, JSON keys, or InfluxDB retention policies.
- **External Models:** We often use third-party Ecore models (e.g., Geo-Standards) where we cannot add `EAnnotations` physically. We need a way to "attach" metadata externally.
- **Polyglot Persistence:** A single `EAttribute` (e.g., `temperature`) might need to be persisted as a current value in SQL (ORM) AND as a time-series stream in OGC SensorThings API.

## 3. Core Architecture: EMF-Based Aspect Pattern

The architecture follows a **Feature-Aspect pattern** implemented entirely in EMF. The `MetadataService` acts as a repository where every registered `EPackage` is wrapped by a `PackageMetadata` object containing `ClassMetadata` and `FeatureMetadata`, each with pluggable "Aspects".

### 3.1. EMF Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              metadata.ecore                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────┐         ┌──────────────────────────────────┐          │
│  │ MetadataRegistry │────────▶│      PackageMetadata             │          │
│  │                  │ *       │                                  │          │
│  └──────────────────┘         │ - ePackage                       │          │
│                               │ - nsURI                          │          │
│                               │ - profiles: PackageProfile[*]    │          │
│                               └────────┬─────────────────────────┘          │
│                                        │ contains *                         │
│                               ┌────────▼─────────┐                          │
│                               │  ClassMetadata   │                          │
│                               │                  │                          │
│                               │ - eClass         │                          │
│                               │ - name           │                          │
│                               │ - typeURI        │                          │
│                               │ - hasId          │                          │
│                               │ - superTypes     │                          │
│                               │ - aspects: ClassAspect[*]                   │
│                               └────────┬─────────┘                          │
│                                        │ contains *                         │
│                    ┌───────────────────┴───────────────────┐                │
│                    │                                       │                │
│           ┌────────▼─────────┐                 ┌───────────▼───────┐        │
│           │ AttributeMetadata│                 │ ReferenceMetadata │        │
│           │                  │                 │                   │        │
│           │ - eAttribute     │                 │ - eReference      │        │
│           │ - isId           │                 │ - containment     │        │
│           │ - defaultValue   │                 │ - targetClass     │        │
│           │ - aspects        │                 │ - oppositeMetadata│        │
│           └──────────────────┘                 │ - aspects         │        │
│                    ▲                           └───────────────────┘        │
│                    │                                    ▲                   │
│                    └─────────┬──────────────────────────┘                   │
│                              │                                              │
│                    ┌─────────▼─────────┐                                    │
│                    │  FeatureMetadata  │ (abstract)                         │
│                    │                   │                                    │
│                    │ - eFeature        │                                    │
│                    │ - name            │                                    │
│                    │ - featureID       │                                    │
│                    │ - aspects: FeatureAspect[*]                            │
│                    └───────────────────┘                                    │
│                                                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                            ASPECT HIERARCHY                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│                        ┌─────────────┐                                       │
│                        │   Aspect    │ (abstract)                            │
│                        │             │                                       │
│                        │ - typeId    │                                       │
│                        └──────┬──────┘                                       │
│                               │                                              │
│              ┌────────────────┼────────────────┬─────────────────┐           │
│              │                │                │                 │           │
│     ┌────────▼────────┐  ┌────▼──────┐  ┌────────▼────────┐     │           │
│     │ PackageAspect   │  │ClassAspect│  │  FeatureAspect  │     │           │
│     │   (abstract)    │  │ (abstract)│  │   (abstract)    │     │           │
│     └─────────────────┘  └────┬──────┘  └────────┬────────┘     │           │
│                               │                  │               │           │
│                               │ extended by codec.ecore          │           │
│                               ▼                  ▼               │           │
│                      ClassCodecAspect    FeatureCodecAspect      │           │
│                      ClassORMAspect      FeatureORMAspect        │           │
│                      ClassHistoryAspect  FeatureHistoryAspect    │           │
│                                                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                            PROFILE HIERARCHY                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│                    ┌─────────────────┐                                       │
│                    │ PackageProfile  │                                       │
│                    │                 │                                       │
│                    │ - typeId        │                                       │
│                    │ - classProfiles │                                       │
│                    └────────┬────────┘                                       │
│                             │ contains *                                    │
│                    ┌────────▼────────┐                                       │
│                    │  ClassProfile   │                                       │
│                    │                 │                                       │
│                    │ - eClass        │                                       │
│                    │ - typeId        │                                       │
│                    └─────────────────┘                                       │
│                             ▲                                                │
│                             │ extended by codec.ecore                       │
│                             ▼                                                │
│                    CodecClassProfile                                         │
│                    ORMClassProfile                                           │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2. Key EMF Classes

**MetadataRegistry** - Root container for all package metadata. Can be serialized for fast startup.

**PackageMetadata** - Wraps an `EPackage`:
- Reference to the original `EPackage`
- Cached `nsURI` for fast lookup
- Contains all `ClassMetadata` for classes in this package
- Contains `profiles: PackageProfile[*]` - pre-computed configuration profiles per provider

**ClassMetadata** - Wraps an `EClass`:
- Reference to the original `EClass`
- Cached `name`, `typeURI`, `classifierID`
- Pre-resolved `superTypes` and `allSuperTypes`
- Pre-resolved `idFeatures` (features forming the ID)
- **Contains `aspects: ClassAspect[*]`** - pluggable configurations

**FeatureMetadata** (abstract) - Base for feature metadata:
- Reference to the original `EStructuralFeature`
- Cached `name`, `featureID`
- **Contains `aspects: FeatureAspect[*]`** - pluggable configurations

**AttributeMetadata** extends FeatureMetadata:
- Reference to `EAttribute`
- `isId` flag, `defaultValue`

**ReferenceMetadata** extends FeatureMetadata:
- Reference to `EReference`
- `containment` flag
- Pre-resolved `targetClassMetadata`, `oppositeMetadata`

**PackageProfile** - Pre-computed configuration profile for a package:
- `typeId` - identifies the provider that created this profile
- Contains `ClassProfile[*]` - pre-computed configuration per class

**ClassProfile** - Pre-computed configuration for a class:
- `eClass` - reference to the EClass
- `typeId` - identifies the provider that created this profile
- Extended by codec.ecore, orm.ecore, etc.

### 3.3. Aspect Base Classes

**Aspect** (abstract) - Base for all aspects:
```java
// Generated from metadata.ecore
public interface Aspect extends EObject {
    String getTypeId();  // e.g., "codec", "orm", "history"
    void setTypeId(String value);
}
```

**PackageAspect** (abstract) extends Aspect - For package-level configuration
**ClassAspect** (abstract) extends Aspect - For class-level configuration
**FeatureAspect** (abstract) extends Aspect - For feature-level configuration

## 4. Aspect Definitions (Extension Points)

Aspects are defined in separate Ecore models that extend `metadata.ecore`. Each concern (codec, ORM, history) defines its own aspect types.

### 4.1. Codec Aspect (in `codec.ecore`)

Defines how an EClass/EFeature is serialized to JSON, XML, etc.

```
ClassCodecAspect extends ClassAspect
  - typeConfig: TypeSerializationConfig
  - idConfig: IdSerializationConfig
  - superTypeConfig: SuperTypeSerializationConfig
  - discriminatorValue: String
  - inheritFromParent: boolean

FeatureCodecAspect extends FeatureAspect
  - effectiveKey: String
  - serialize: boolean
  - serializeNull/Empty/Defaults: boolean
  - valueWriterName, valueReaderName: String
  - enumSerialization: EnumSerializationStrategy

ReferenceCodecAspect extends FeatureCodecAspect
  - referenceConfig: ReferenceSerializationConfig
  - typeConfig: TypeSerializationConfig
  - expand: boolean
```

### 4.2. ORM Aspect (future)

Defines how an EFeature maps to a relational database column.

```
FeatureORMAspect extends FeatureAspect
  - columnName: String
  - sqlType: String
  - isNullable: boolean
  - converterClass: String
```

### 4.3. Unit Aspect (future)

Defines the physical unit of the data.

```
FeatureUnitAspect extends FeatureAspect
  - symbol: String (e.g., "W/m²")
  - category: String (e.g., "Irradiance")
  - siBaseUnit: String
  - conversionFactor: double
```

### 4.4. History Aspect (future)

Defines if and how a feature's value history should be preserved.

```
FeatureHistoryAspect extends FeatureAspect
  - strategy: HistoryStrategy (SNAPSHOT, TIME_SERIES)
  - standard: HistoryStandard (NONE, NGSI_LD, OGC_STA)
  - targetIdentifier: String
```

## 5. Configuration Classes (in metadata.ecore)

Base configuration classes define defaults directly in the Ecore model:

```
BaseTypeConfig (abstract)
  - strategy: TypeStrategy = URI
  - include: boolean = true
  - typeKey: String = "_type"
  - schemaKey: String = "schema"
  - nameKey: String = "name"

BaseIdConfig (abstract)
  - strategy: IdStrategy = ID_FIELD
  - keyMode: IdKeyMode = ID_ONLY
  - format: SerializationFormat = PLAIN
  - idKey: String = "_id"
  - separator: String = "-"
  - onTop: boolean = false
  - serializeSeparator: boolean = false
  - separatorKey: String = "_separator"
  - valueKey: String = "_value"

BaseReferenceConfig (abstract)
  - format: SerializationFormat = PLAIN
  - typeKey: String = "_type"
  - refKey: String = "_ref"

BaseSuperTypeConfig (abstract)
  - enabled: boolean = false
  - selection: SuperTypeSelection = ALL
  - format: SerializationFormat = PLAIN
  - superTypeKey: String = "_supertype"
  - asArray: boolean = true
  - separator: String = "/"
```

Concrete config classes in `codec.ecore` extend these to add codec-specific fields.

## 6. Enums (in metadata.ecore)

```
SerializationFormat: PLAIN, STRUCTURED

TypeStrategy: NAME, CLASS, URI, MAPPED, STRUCTURED, SCHEMA_AND_TYPE, NUMERIC

IdStrategy: ID_FIELD, COMBINED, NONE

IdKeyMode: ID_ONLY, BOTH, FEATURE_ONLY, NONE

SuperTypeSelection: ALL, ALL_EMF, SINGLE, NONE

EnumSerializationStrategy: LITERAL, VALUE, NAME
```

## 7. Resolution Strategy (The Registration Lifecycle)

When `registerPackage(EPackage)` is called, the service executes the following lifecycle:

### 7.1. Registration Lifecycle

**Phase 1: Build Metadata Wrappers**
1. Create `PackageMetadata` wrapper for the `EPackage`
2. Build `ClassMetadata` for each `EClass` in the package
3. Build `FeatureMetadata` (AttributeMetadata/ReferenceMetadata) for each `EStructuralFeature`

**Phase 2: Build Aspects**
- For each registered `AspectProvider`:
  1. Call `buildPackageAspect(packageMetadata)` → attach to PackageMetadata
  2. For each ClassMetadata: call `buildClassAspect(classMetadata)` → attach to ClassMetadata
  3. For each FeatureMetadata:
     - If AttributeMetadata: call `buildAttributeAspect(attributeMetadata)`
     - If ReferenceMetadata: call `buildReferenceAspect(referenceMetadata)`
     - Attach resulting FeatureAspect to FeatureMetadata

**Phase 3: Resolve Cross-References**
- Resolve supertype references between ClassMetadata objects
- Resolve target class references for ReferenceMetadata
- Resolve opposite references for ReferenceMetadata

**Phase 4: Build Profiles (Provider Isolation)**
- For each registered `AspectProvider`:
  1. Create a **filtered copy** of `PackageMetadata` containing ONLY aspects with this provider's `typeId`
  2. Call `provider.buildProfiles(filteredMetadataCopy)`
  3. Provider returns a `PackageProfile` containing `ClassProfile` objects with pre-computed configuration
  4. Store the `PackageProfile` in `PackageMetadata.profiles`

**Phase 5: Index and Notify**
- Index the package via `MetadataIndex` (if set)
- Notify all registered `MetadataHandler` instances via `handler.onPackageRegistered(pkgMetadata)`

### 7.2. Aspect Resolution (3-Layer Cascade)

When building aspects, providers follow a **3-Layer Cascade**. The first source to provide a value wins:

1. **Level 1: External Configuration (Highest Priority)**
   - Source: A dedicated DSL (`.modelinfo` file) or Java Config class.
   - Benefit: Allows annotating third-party models without touching their Ecore.

2. **Level 2: EAnnotations (Standard EMF)**
   - Source: Standard `.ecore` file annotations.
   - Example: `source="http://eclipse.org/fennec/codec"`, `details="strategy=URI"`
   - Benefit: Keeps definitions close to the model.

3. **Level 3: Convention / Defaults (Fallback)**
   - Source: EMF default values in config classes.
   - Logic: Default values defined in `metadata.ecore` and `codec.ecore`.

### 7.3. Profile Building

Profiles provide **pre-computed, provider-specific configuration** that combines:
- Annotation-layer configuration from aspects
- Resolved inheritance and cross-references
- Provider-specific computation logic

**Provider Isolation:**
- Each provider receives a filtered copy containing ONLY its own aspects
- Providers cannot see or depend on other providers' aspects
- This ensures clean separation between codec, ORM, history, etc.

**Example:** CodecAspectProvider receives filtered metadata with only `ClassCodecAspect` and `FeatureCodecAspect` objects, builds `CodecClassProfile` with fully resolved type/id/supertype/reference configuration.

## 8. Service API (metadata-api.ecore)

The service interfaces are defined in EMF (`metadata-api.ecore`), enabling type-safe operations and code generation.

### 8.1 MetadataService (Consumer API - Read-Only)

Main service interface for metadata lookup. This is the interface used by consumers (serializers, ORM mappers, etc.):

```
MetadataService (interface)
  // Package operations (read-only)
  +getPackageMetadata(nsURI: String): PackageMetadata

  // Class lookup
  +getClassMetadata(eClass: EClass): ClassMetadata
  +getClassMetadataByURI(uri: String): ClassMetadata
  +getClassMetadataByName(className: String, nsURI: String): ClassMetadata

  // Feature lookup
  +getFeatureMetadata(feature: EStructuralFeature): FeatureMetadata
  +getFeatureMetadataByURI(uri: String): FeatureMetadata
  +getFeatureMetadataByName(featureName: String, className: String, nsURI: String): FeatureMetadata
  +getFeatureMetadataFromClass(featureName: String, classMetadata: ClassMetadata): FeatureMetadata

  // Aspect lookup (convenience)
  +getPackageAspect(ePackage: EPackage, aspectTypeId: String): PackageAspect
  +getClassAspect(eClass: EClass, aspectTypeId: String): ClassAspect
  +getFeatureAspect(feature: EStructuralFeature, aspectTypeId: String): FeatureAspect

  // Profile lookup
  +getPackageProfile(ePackage: EPackage, typeId: String): PackageProfile
  +getPackageProfileByNsURI(nsURI: String, typeId: String): PackageProfile
  +getClassProfile(eClass: EClass, typeId: String): ClassProfile
  +getClassProfileByURI(eClassURI: String, typeId: String): ClassProfile

  // Registry access
  +getRegistry(): MetadataRegistry
  +getIndexReader(): MetadataIndexReader
```

### 8.2 MetadataWhiteboard (Admin API - Extends MetadataService)

Extended interface for administrative operations (package registration, provider management):

```
MetadataWhiteboard extends MetadataService
  // Package registration
  +registerPackage(ePackage: EPackage): PackageMetadata
  +unregisterPackage(ePackage: EPackage): void

  // Provider management
  +registerAspectProvider(provider: AspectProvider): void
  +unregisterAspectProvider(provider: AspectProvider): void
  +getAspectProviders(): EList<AspectProvider>

  // Index management
  +getMetadataIndex(): MetadataIndex
  +setMetadataIndex(index: MetadataIndex): void
  +unsetMetadataIndex(index: MetadataIndex): void

  // Handler management
  +addMetadataHandler(handler: MetadataHandler): void
  +removeMetadataHandler(handler: MetadataHandler): void
```

### 8.3 AspectProvider

Extension point for contributing aspects and profiles to metadata:

```
AspectProvider (interface)
  +getAspectTypeId(): String                                     // e.g., "codec", "orm", "history"
  +buildPackageAspect(packageMetadata: PackageMetadata): PackageAspect
  +buildClassAspect(classMetadata: ClassMetadata): ClassAspect
  +buildFeatureAspect(featureMetadata: FeatureMetadata): FeatureAspect
  +buildAttributeAspect(attributeMetadata: AttributeMetadata): FeatureAspect
  +buildReferenceAspect(referenceMetadata: ReferenceMetadata): FeatureAspect
  +buildProfiles(filteredMetadataCopy: PackageMetadata): PackageProfile
```

**Key Points:**
- Methods receive **metadata wrappers** (PackageMetadata, ClassMetadata, etc.), NOT raw ECore objects
- Metadata wrappers provide access to the original ECore objects AND to other metadata
- `buildProfiles` receives a **filtered copy** containing only this provider's aspects
- Profiles are built AFTER all cross-references are resolved

**Provider Flow:**
1. `MetadataWhiteboard.registerAspectProvider(codecProvider)` - Register provider
2. `MetadataWhiteboard.registerPackage(MyPackage.eINSTANCE)` - Triggers all providers
3. For each EPackage/EClass/EFeature, providers build aspects
4. Cross-references are resolved
5. For each provider, build profiles with filtered metadata copy
6. Profiles are stored in PackageMetadata

### 8.4 MetadataHandler (Lifecycle Callback)

Generic lifecycle callback triggered by the whiteboard when packages are registered or unregistered. This enables external services to react to metadata changes without coupling to the internal registration logic.

```
MetadataHandler (interface)
  +onPackageRegistered(packageMetadata: PackageMetadata): void
  +onPackageUnregistered(packageMetadata: PackageMetadata): void
  +clear(): void
```

**Key Points:**
- `onPackageRegistered` is called **after** all aspects and profiles have been built, so the `PackageMetadata` is fully populated
- `onPackageUnregistered` is called **before** the package is removed from internal maps, so the metadata is still valid
- `clear` is called when the handler is removed via `removeMetadataHandler`
- **Late binding:** When `addMetadataHandler` is called, the handler immediately receives `onPackageRegistered` for all already-registered packages (same pattern as `setMetadataIndex`)
- Duplicate handlers are ignored (same instance is not added twice)
- Thread-safe: handlers are stored in a `CopyOnWriteArrayList`

**Example: TypeDiscriminatorService as MetadataHandler**
```java
MetadataWhiteboard whiteboard = new MetadataServiceImpl();
whiteboard.registerAspectProvider(new CodecAspectProvider());

// Create handler and register it
TypeDiscriminatorService typeService = new TypeDiscriminatorService();
whiteboard.addMetadataHandler(typeService);

// Now register packages — handler is notified automatically
whiteboard.registerPackage(SensorPackage.eINSTANCE);

// Or if packages were registered first, late binding notifies the handler
// about all existing packages when addMetadataHandler is called
```

## 9. Usage Example

```java
// Get metadata service (read-only consumer API)
MetadataService service = ...;

// Fast lookup - no Optional, returns null when not found
ClassMetadata classMeta = service.getClassMetadata(PersonPackage.Literals.PERSON);

if (classMeta != null) {
    // Get codec aspect (type-safe EMF object)
    ClassCodecAspect codecAspect = classMeta.getAspects().stream()
        .filter(a -> a instanceof ClassCodecAspect)
        .map(ClassCodecAspect.class::cast)
        .findFirst()
        .orElse(null);

    // Or use convenience method
    ClassAspect aspect = service.getClassAspect(
        PersonPackage.Literals.PERSON,
        "codec"
    );

    if (aspect instanceof ClassCodecAspect codecAspect2) {
        // Access annotation-layer configuration
        TypeSerializationConfig typeConfig = codecAspect2.getTypeConfig();
        TypeStrategy strategy = typeConfig.getStrategy();  // URI, NAME, etc.
        String typeKey = typeConfig.getTypeKey();          // "_type"

        IdSerializationConfig idConfig = codecAspect2.getIdConfig();
        IdStrategy idStrategy = idConfig.getStrategy();    // ID_FIELD, COMBINED, NONE
    }
}

// Profile lookup - fully resolved configuration
ClassProfile classProfile = service.getClassProfile(PersonPackage.Literals.PERSON, "codec");
if (classProfile instanceof CodecClassProfile codecProfile) {
    // Access pre-computed, fully resolved configuration
    TypeSerializationConfig typeConfig = codecProfile.getTypeConfig();
    // This includes inheritance resolution, defaults, etc.
}

// Feature-level lookup
FeatureMetadata featureMeta = service.getFeatureMetadata(
    PersonPackage.Literals.PERSON__FIRST_NAME
);

if (featureMeta != null) {
    FeatureCodecAspect featureAspect = featureMeta.getAspects().stream()
        .filter(a -> a instanceof FeatureCodecAspect)
        .map(FeatureCodecAspect.class::cast)
        .findFirst()
        .orElse(null);

    if (featureAspect != null) {
        String jsonKey = featureAspect.getEffectiveKey();  // "firstName" or custom
        boolean serialize = featureAspect.isSerialize();    // true/false
    }
}

// Admin operations (requires MetadataWhiteboard)
MetadataWhiteboard whiteboard = ...;

// Register package (triggers metadata computation)
PackageMetadata pkgMeta = whiteboard.registerPackage(PersonPackage.eINSTANCE);

// Register aspect provider
AspectProvider codecProvider = new CodecAspectProvider();
whiteboard.registerAspectProvider(codecProvider);
```

## 10. Persistence & Caching

Since metadata is now an EMF model, it can be serialized:

```java
// Save computed metadata to XMI
Resource resource = resourceSet.createResource(URI.createURI("metadata.xmi"));
resource.getContents().add(service.getRegistry());
resource.save(Collections.emptyMap());

// Load pre-computed metadata at startup (skip EAnnotation parsing)
Resource resource = resourceSet.getResource(URI.createURI("metadata.xmi"), true);
MetadataRegistry registry = (MetadataRegistry) resource.getContents().get(0);
whiteboard.loadRegistry(registry);
```

## 11. Benefits of EMF-Based Approach

1. **Type Safety:** Generated Java classes with proper getters/setters
2. **Validation:** EMF validation framework can check constraints
3. **Serialization:** XMI, JSON, or custom format via EMF Resources
4. **Tooling:** Ecore editors, code generation, model transformation
5. **Extensibility:** New aspects via Ecore inheritance
6. **Introspection:** Full EMF reflective API available
7. **Performance:** Pre-computed metadata, O(1) lookups at runtime
8. **Provider Isolation:** Filtered metadata copies ensure clean separation between providers
9. **Profile Caching:** Pre-computed profiles avoid runtime configuration resolution
