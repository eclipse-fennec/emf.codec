# Codec Metadata Architecture

## Overview

`org.eclipse.fennec.codec.metadata` provides codec-specific aspects for the Model Metadata Service. It extends `org.eclipse.fennec.model.metadata` with serialization configuration defined entirely in EMF (`codec.ecore`).

**Purpose:** Define how EMF model elements (EClasses, EStructuralFeatures) are serialized/deserialized to various formats (JSON, XML, CSV, MongoDB, etc.).

---

## Dependencies

```
org.eclipse.fennec.codec.metadata
         │
         └──→ org.eclipse.fennec.model.metadata (metadata.ecore - base infrastructure)
```

**No dependency on codec** - this is purely metadata, not serialization logic.

---

## Project Structure

```
org.eclipse.fennec.codec.metadata/
├── model/
│   └── codec.ecore                    (EMF model for codec aspects, configs, profiles)
│
├── src/
│   └── org/eclipse/fennec/codec/metadata/
│       ├── provider/
│       │   ├── CodecAnnotationConstants.java   (annotation source, keys, defaults)
│       │   ├── CodecAspectProvider.java         (AspectProvider implementation + profile builder)
│       │   └── package-info.java
│       ├── type/
│       │   ├── TypeDiscriminatorReader.java      (read-only query interface)
│       │   ├── TypeDiscriminatorRegistry.java    (registry for type mappings)
│       │   ├── TypeDiscriminatorService.java     (discriminator resolution + MetadataHandler)
│       │   └── package-info.java
│       └── util/
│           ├── AnnotationParseHelper.java       (parsing utility for annotation values)
│           └── package-info.java
│
├── src-gen/                           (EMF-generated code)
│   └── org/eclipse/fennec/codec/metadata/model/codec/
│       ├── CodecPackage.java
│       ├── CodecFactory.java
│       ├── ClassCodecAspect.java
│       ├── FeatureCodecAspect.java
│       ├── ReferenceCodecAspect.java
│       ├── TypeSerializationConfig.java
│       ├── IdSerializationConfig.java
│       ├── ReferenceSerializationConfig.java
│       ├── SuperTypeSerializationConfig.java
│       ├── FeatureSerializationConfig.java
│       ├── CodecConfig.java
│       ├── CodecPackageProfile.java
│       ├── CodecClassProfile.java
│       └── impl/                      (implementation classes)
│
└── test/
    └── org/eclipse/fennec/codec/metadata/
        ├── provider/
        │   ├── CodecAspectProviderValidConfigTest.java  (valid annotation parsing)
        │   ├── CodecAspectProviderMisconfigTest.java     (misconfiguration diagnostics)
        │   ├── CodecProfileBuildTest.java                (profile building + retrieval)
        │   ├── test-codec-annotations.ecore              (test model with annotations)
        │   └── ...
        └── type/
            └── TypeDiscriminatorServiceTest.java         (discriminator service + handler tests)
```

---

## EMF Model: codec.ecore

The codec aspects and configuration classes are defined in `codec.ecore`, which extends `metadata.ecore`.

### Aspect Classes

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              codec.ecore                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  From metadata.ecore:          Extended in codec.ecore:                      │
│                                                                              │
│  ┌─────────────┐               ┌────────────────────┐                       │
│  │ ClassAspect │◄──────────────│ ClassCodecAspect   │                       │
│  │ (abstract)  │               │                    │                       │
│  └─────────────┘               │ - typeConfig       │                       │
│                                │ - idConfig         │                       │
│                                │ - superTypeConfig  │                       │
│                                │ - inheritFromParent│                       │
│                                │ - discriminatorVal │                       │
│                                └────────────────────┘                       │
│                                                                              │
│  ┌──────────────┐              ┌────────────────────┐                       │
│  │FeatureAspect │◄─────────────│ FeatureCodecAspect │                       │
│  │ (abstract)   │              │                    │                       │
│  └──────────────┘              │ - effectiveKey     │                       │
│                                │ - serialize        │                       │
│                                │ - serializeNull    │                       │
│                                │ - serializeEmpty   │                       │
│                                │ - serializeDefaults│                       │
│                                │ - valueWriterName  │                       │
│                                │ - valueReaderName  │                       │
│                                │ - enumSerialization│                       │
│                                └─────────┬──────────┘                       │
│                                          │                                  │
│                                          │ extends                          │
│                                          ▼                                  │
│                                ┌────────────────────┐                       │
│                                │ReferenceCodecAspect│                       │
│                                │                    │                       │
│                                │ - referenceConfig  │                       │
│                                │ - typeConfig       │                       │
│                                │ - inheritTypeFrom  │                       │
│                                │   Target           │                       │
│                                │ - expand           │                       │
│                                │ - inlineTypeMaps   │                       │
│                                │ - fallbackStrategy │                       │
│                                │ - fallbackEClass   │                       │
│                                └────────────────────┘                       │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Configuration Classes

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Configuration Hierarchy                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  From metadata.ecore:          Extended in codec.ecore:                      │
│                                                                              │
│  ┌───────────────┐             ┌─────────────────────────┐                  │
│  │ BaseTypeConfig│◄────────────│ TypeSerializationConfig │                  │
│  │               │             │                         │                  │
│  │ - strategy    │             │ - discriminatorPath     │                  │
│  │ - include     │             │ - discriminatorValue    │                  │
│  │ - typeKey     │             └─────────────────────────┘                  │
│  │ - schemaKey   │                                                          │
│  │ - nameKey     │                                                          │
│  └───────────────┘                                                          │
│                                                                              │
│  ┌───────────────┐             ┌─────────────────────────┐                  │
│  │ BaseIdConfig  │◄────────────│ IdSerializationConfig   │                  │
│  │               │             │                         │                  │
│  │ - strategy    │             │ - idFeatures            │                  │
│  │ - keyMode     │             │ - idValueWriterName     │                  │
│  │ - format      │             │ - idValueReaderName     │                  │
│  │ - idKey       │             └─────────────────────────┘                  │
│  │ - separator   │                                                          │
│  └───────────────┘                                                          │
│                                                                              │
│  ┌─────────────────┐           ┌───────────────────────────┐                │
│  │BaseReferenceConf│◄──────────│ReferenceSerializationConf │                │
│  │                 │           │                           │                │
│  │ - format        │           │ - includeType             │                │
│  │ - typeKey       │           │ - expand                  │                │
│  │ - refKey        │           └───────────────────────────┘                │
│  └─────────────────┘                                                        │
│                                                                              │
│  ┌─────────────────┐           ┌───────────────────────────┐                │
│  │BaseSuperTypeConf│◄──────────│SuperTypeSerializationConf │                │
│  │                 │           │                           │                │
│  │ - enabled       │           │ - useSmartCompression     │                │
│  │ - selection     │           └───────────────────────────┘                │
│  │ - format        │                                                        │
│  │ - superTypesKey │                                                        │
│  └─────────────────┘                                                        │
│                                                                              │
│  ┌─────────────────┐           ┌───────────────────────────┐                │
│  │BaseFeatureConfig│◄──────────│FeatureSerializationConfig │                │
│  │                 │           │                           │                │
│  │ - key           │           │ - featureName             │                │
│  │ - serialize     │           │ - valueWriterName         │                │
│  │ - serializeNull │           │ - valueReaderName         │                │
│  │ - serializeEmpty│           │ - expand                  │                │
│  │ - serializeDef  │           │ - referenceConfig         │                │
│  └─────────────────┘           │ - typeConfig              │                │
│                                └───────────────────────────┘                │
└─────────────────────────────────────────────────────────────────────────────┘
```

### CodecConfig (Root Configuration)

```
CodecConfig
  - format: SerializationFormat = PLAIN
  - useNumericIds: boolean = false
  - typeConfig: TypeSerializationConfig (containment)
  - containmentTypeConfig: TypeSerializationConfig (containment)
  - referenceTypeConfig: TypeSerializationConfig (containment)
  - idConfig: IdSerializationConfig (containment)
  - referenceConfig: ReferenceSerializationConfig (containment)
  - superTypeConfig: SuperTypeSerializationConfig (containment)
  - featureConfigs: FeatureSerializationConfig[*] (containment)
  - expand: boolean = false
  - expandDepth: int = 1
  - expandIgnoreBidirectional: boolean = true
  - serializeNull: boolean = false
  - serializeEmpty: boolean = false
  - serializeDefaults: boolean = false
```

### Enumerations

**FallbackStrategy** (for reference type resolution failures):
- `SKIP` - Skip serialization of the reference
- `FALLBACK` - Use fallback EClass
- `ERROR` - Throw error

**StrategyScope** (for ID/type/reference strategy scoping):
- `ALL` - Apply to all references
- `ROOT_ONLY` - Apply to root objects only
- `CONTAINMENT_ONLY` - Apply to containment references only
- `NON_CONTAINMENT_ONLY` - Apply to non-containment references only

**EnumSerializationStrategy**:
- `LITERAL` - Use literal string value
- `VALUE` - Use numeric value
- `NAME` - Use enum constant name

---

## EAnnotation Mapping

### Annotation Source

**All codec annotations use a SINGLE source:** `http://eclipse.org/fennec/codec`

Configuration is specified entirely through detail key-value pairs. All config types (type, id, supertype, reference, feature) use the same annotation source with prefixed keys.

### Annotation Key Categories

| Key Category | Prefix | Applies To | Example Keys |
|-------------|--------|------------|-------------|
| Type | `type*` | EClass, EReference | `typeStrategy`, `typeKey`, `typeFormat`, `typeMapId`, `typeDiscriminator` |
| ID | `id*` | EClass | `idStrategy`, `idKey`, `idFeatures`, `idFormat`, `idKeyMode` |
| SuperType | `superType*` | EClass | `superTypeSerialize`, `superTypeKey`, `superTypeStrategy` |
| Reference | `ref*` | EReference | `refFormat`, `refKey` |
| Feature | (no prefix) | EAttribute, EReference | `key`, `transient`, `serialize`, `serializeNull`, `serializeEmpty` |
| Expand | `expand` | EReference | `expand` |
| Enum | `enumSerialization` | EAttribute | `enumSerialization` |

### Example EAnnotation → Aspect

**Ecore:**
```xml
<eClassifiers xsi:type="ecore:EClass" name="Person">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="idStrategy" value="ID_FIELD"/>
    <details key="idKey" value="_id"/>
    <details key="typeStrategy" value="URI"/>
    <details key="typeKey" value="_type"/>
    <details key="superTypeSerialize" value="true"/>
    <details key="superTypeStrategy" value="ALL"/>
  </eAnnotations>
  <eStructuralFeatures xsi:type="ecore:EAttribute" name="firstName">
    <eAnnotations source="http://eclipse.org/fennec/codec">
      <details key="key" value="first_name"/>
    </eAnnotations>
  </eStructuralFeatures>
  <eStructuralFeatures xsi:type="ecore:EReference" name="address">
    <eAnnotations source="http://eclipse.org/fennec/codec">
      <details key="refFormat" value="STRUCTURED"/>
      <details key="refKey" value="$ref"/>
      <details key="expand" value="true"/>
    </eAnnotations>
  </eStructuralFeatures>
</eClassifiers>
```

**Resolved Aspect (EMF objects):**
```java
ClassCodecAspect aspect = ...;  // from ClassMetadata.getAspects()

// ID config
IdSerializationConfig idConfig = aspect.getIdConfig();
idConfig.getStrategy();     // IdStrategy.ID_FIELD
idConfig.getIdKey();        // "_id"

// Type config
TypeSerializationConfig typeConfig = aspect.getTypeConfig();
typeConfig.getStrategy();   // TypeStrategy.URI
typeConfig.getTypeKey();    // "_type"

// SuperType config
SuperTypeSerializationConfig superTypeConfig = aspect.getSuperTypeConfig();
superTypeConfig.isEnabled();     // true
superTypeConfig.getSelection();  // SuperTypeSelection.ALL
```

---

## Built-in Defaults (from spec)

Defaults are defined directly in the Ecore model via `defaultValueLiteral`:

**Type Configuration:**
```
strategy = URI
typeKey = "_type"
include = true
schemaKey = "schema"
nameKey = "name"
```

**ID Configuration:**
```
strategy = ID_FIELD
idKey = "_id"
separator = "-"
keyMode = ID_ONLY
format = PLAIN
```

**Reference Configuration:**
```
format = PLAIN
typeKey = "_type"
refKey = "_ref"
includeType = true
```

**SuperType Configuration:**
```
enabled = false
selection = ALL
superTypeKey = "_supertype"
format = PLAIN
```

**Feature Configuration:**
```
serialize = true
serializeNull = false
serializeDefaults = false
serializeEmpty = false
```

---

## CodecAnnotationConstants

The `CodecAnnotationConstants` class provides annotation source URIs and detail key constants.

```java
public final class CodecAnnotationConstants {
    // Single annotation source
    public static final String CODEC_SOURCE = "http://eclipse.org/fennec/codec";
    public static final String TYPE_MAPPING_SOURCE_PREFIX = "http://eclipse.org/fennec/codec/typeMapping/";
    public static final String INLINE_MAPPING_SOURCE = "http://eclipse.org/fennec/codec/inlineMapping";

    // ID keys
    public static final String KEY_ID_STRATEGY = "idStrategy";
    public static final String KEY_ID_KEY = "idKey";
    public static final String KEY_ID_FEATURES = "idFeatures";
    public static final String KEY_ID_FORMAT = "idFormat";
    public static final String KEY_ID_KEY_MODE = "idKeyMode";
    public static final String KEY_ID_SEPARATOR = "idSeparator";
    public static final String KEY_ID_VALUE_WRITER = "idValueWriter";
    public static final String KEY_ID_VALUE_READER = "idValueReader";

    // Type keys
    public static final String KEY_TYPE_STRATEGY = "typeStrategy";
    public static final String KEY_TYPE_KEY = "typeKey";
    public static final String KEY_TYPE_FORMAT = "typeFormat";
    public static final String KEY_TYPE_MAP_ID = "typeMapId";
    public static final String KEY_TYPE_DISCRIMINATOR = "typeDiscriminator";
    public static final String KEY_TYPE_INCLUDE = "typeInclude";  // deprecated

    // SuperType keys
    public static final String KEY_SUPERTYPE_SERIALIZE = "superTypeSerialize";
    public static final String KEY_SUPERTYPE_KEY = "superTypeKey";
    public static final String KEY_SUPERTYPE_STRATEGY = "superTypeStrategy";
    public static final String KEY_SUPERTYPE_FORMAT = "superTypeFormat";

    // Reference keys
    public static final String KEY_REF_FORMAT = "refFormat";
    public static final String KEY_REF_KEY = "refKey";
    public static final String KEY_REF_INCLUDE_TYPE = "refIncludeType";

    // Feature keys
    public static final String KEY_KEY = "key";
    public static final String KEY_TRANSIENT = "transient";
    public static final String KEY_SERIALIZE = "serialize";
    public static final String KEY_SERIALIZE_NULL = "serializeNull";
    public static final String KEY_SERIALIZE_EMPTY = "serializeEmpty";
    public static final String KEY_SERIALIZE_DEFAULTS = "serializeDefaults";
    public static final String KEY_VALUE_WRITER = "valueWriter";
    public static final String KEY_VALUE_READER = "valueReader";

    // Expand key
    public static final String KEY_EXPAND = "expand";

    // Enum key
    public static final String KEY_ENUM_SERIALIZATION = "enumSerialization";

    // Helper methods
    public static boolean isTypeMapAnnotation(String source);
    public static String extractMapId(String source);
    public static String extractStaticDiscriminatorValue(String key);
}
```

---

## CodecAspectProvider Implementation

**CodecAspectProvider** is the full AspectProvider implementation (1,197 lines) responsible for:

1. **Annotation Parsing**: Reads EAnnotations from EPackage/EClass/EStructuralFeature
2. **Aspect Building**: Creates ClassCodecAspect, FeatureCodecAspect, ReferenceCodecAspect
3. **Profile Building**: Creates CodecPackageProfile/CodecClassProfile with pre-merged configs
4. **Validation**: Detects and reports misconfigurations via MetadataDiagnostic

### Key Methods

```java
CodecAspectProvider implements AspectProvider {

    @Override
    public String getAspectTypeId() {
        return "codec";
    }

    @Override
    public ClassAspect buildClassAspect(ClassMetadata classMetadata) {
        // Parses EClass annotations, creates ClassCodecAspect
        // Contains typeConfig, idConfig, superTypeConfig (if present in annotations)
    }

    @Override
    public FeatureAspect buildAttributeAspect(AttributeMetadata attributeMetadata) {
        // Parses EAttribute annotations, creates FeatureCodecAspect
        // Contains effectiveKey, serialize flags, valueWriter/Reader
    }

    @Override
    public FeatureAspect buildReferenceAspect(ReferenceMetadata referenceMetadata) {
        // Parses EReference annotations, creates ReferenceCodecAspect
        // Extends FeatureCodecAspect with referenceConfig, typeConfig, expand
    }

    @Override
    public PackageProfile buildProfiles(PackageMetadata filteredMetadataCopy) {
        // Creates CodecPackageProfile containing CodecClassProfile per EClass
        // Pre-merges annotation configs with built-in defaults
    }

    // Private helpers
    private CodecClassProfile buildClassProfile(ClassMetadata classMetadata);
    private FeatureSerializationConfig buildFeatureConfig(FeatureMetadata featureMetadata);
    private IdSerializationConfig buildIdConfig(Map<String, String> details, ClassMetadata classMetadata);
    private TypeSerializationConfig buildTypeConfig(Map<String, String> details, ClassMetadata classMetadata);
    private SuperTypeSerializationConfig buildSuperTypeConfig(Map<String, String> details);
    private ReferenceSerializationConfig buildReferenceConfig(Map<String, String> details, ReferenceMetadata referenceMetadata);
}
```

---

## Profile Building

**Profile Building** provides pre-merged annotation-layer configuration.

When `buildProfiles(filteredMetadataCopy)` is called, CodecAspectProvider:

1. Creates `CodecPackageProfile` containing one `CodecClassProfile` per EClass
2. For each EClass:
   - **TypeConfig**: Copied from ClassCodecAspect if present, otherwise created with EMF defaults (strategy=URI, format=PLAIN, typeKey="_type")
   - **IdConfig**: Copied or defaulted (strategy=ID_FIELD, format=PLAIN, idKey="_id")
   - **SuperTypeConfig**: Copied or defaulted (enabled=false, selection=ALL)
   - **FeatureConfigs**: One `FeatureSerializationConfig` per feature
3. For each feature:
   - `key` from aspect's `effectiveKey` or feature name
   - `serialize`, `serializeNull`, `serializeEmpty`, `serializeDefaults` from aspect
   - For EReferences: copies `referenceConfig`, `typeConfig`, `expand` from ReferenceCodecAspect

This profile represents the **annotation-layer resolved state** (levels 5+6 in the configuration hierarchy). At runtime, the ConfigurationResolver merges dynamic overrides (levels 1-4) on top.

**Key difference between Aspects and Profiles:**
- **Aspects** contain only explicitly configured values from annotations (may be null/empty)
- **Profiles** always contain complete configuration with built-in defaults applied

---

## Annotation Validation

**CodecAspectProvider** detects and reports misconfigurations as `MetadataDiagnostic` objects:

- **Class-only keys on EStructuralFeatures** (e.g., `idStrategy` on an attribute)
- **Reference-only keys on EAttributes** (e.g., `refFormat` on an attribute)
- **Runtime-only keys in EAnnotations** (e.g., `idScope` — should only be set at runtime)
- **Deprecated keys** (e.g., `typeInclude`)
- **Invalid enum values** (e.g., `typeStrategy="INVALID"`)

Diagnostics are attached to the PackageMetadata and can be queried via:
```java
List<MetadataDiagnostic> diagnostics = packageMetadata.getDiagnostics();
for (MetadataDiagnostic diagnostic : diagnostics) {
    Severity severity = diagnostic.getSeverity();  // ERROR, WARNING, INFO
    String message = diagnostic.getMessage();
    EObject source = diagnostic.getSource();        // EClass or EStructuralFeature
}
```

---

## Usage with MetadataService

```java
MetadataWhiteboard service = ...;

// Register provider and package
CodecAspectProvider codecProvider = new CodecAspectProvider();
service.registerAspectProvider(codecProvider);
PackageMetadata pkgMeta = service.registerPackage(PersonPackage.eINSTANCE);

// Access class aspect (raw annotation values only)
ClassAspect aspect = service.getClassAspect(PersonPackage.Literals.PERSON, "codec");
if (aspect instanceof ClassCodecAspect codecAspect) {
    TypeSerializationConfig typeConfig = codecAspect.getTypeConfig();
    // May be null if not annotated
}

// Access pre-computed profile (recommended for runtime)
ClassProfile profile = service.getClassProfile(PersonPackage.Literals.PERSON, "codec");
if (profile instanceof CodecClassProfile codecProfile) {
    TypeSerializationConfig typeConfig = codecProfile.getTypeConfig(); // Always present with defaults
    IdSerializationConfig idConfig = codecProfile.getIdConfig();       // Always present with defaults

    for (FeatureSerializationConfig fc : codecProfile.getFeatureConfigs()) {
        String key = fc.getKey();
        boolean serialize = fc.getSerialize();
    }
}
```

---

## Type Discriminator Service

### TypeDiscriminatorReader (Read-Only Query Interface)

`TypeDiscriminatorReader` is the read-only query interface for type discriminator lookups. It is used by the codec serialization/deserialization pipeline to resolve type discriminator values without needing access to the mutable `TypeDiscriminatorService`.

```java
TypeDiscriminatorReader (interface)
  // Serialization (EClass → discriminator value)
  +getDiscriminatorValue(mapId: String, eClass: EClass): String
  +getDiscriminatorValueFromAny(eClass: EClass): String
  +getDiscriminatorValueForReference(ref: EReference, eClass: EClass): String
  +getMapIdForEClass(eClass: EClass): String

  // Deserialization (discriminator value → EClass)
  +getEClass(mapId: String, discriminatorValue: String): EClass
  +getEClassFromAny(discriminatorValue: String): EClass
  +resolve(mapId: String, discriminatorValue: String, resolver: Function): EClass
  +resolveFromAny(discriminatorValue: String, resolver: Function): EClass
  +resolveForReference(ref: EReference, discriminatorValue: String, resolver: Function): EClass

  // Path
  +getAnyDiscriminatorPath(): String
  +getDiscriminatorPath(mapId: String): String
```

### TypeDiscriminatorService (MetadataHandler Implementation)

`TypeDiscriminatorService` implements both `MetadataHandler` and `TypeDiscriminatorReader`. As a `MetadataHandler`, it is notified by the `MetadataWhiteboard` when packages are registered/unregistered and incrementally updates its discriminator registries.

**Handler Lifecycle:**
- `onPackageRegistered(PackageMetadata)` — Scans the package's `ClassMetadata` for `ClassCodecAspect` discriminator values and `typeMapping/{mapId}` annotations. Registers mappings incrementally rather than rebuilding the entire service.
- `onPackageUnregistered(PackageMetadata)` — Removes all discriminator mappings for EClasses from the unregistered package.
- `clear()` — Removes all registries and mappings.

**Two Population Modes:**

1. **Managed (recommended):** Register as a `MetadataHandler` on the whiteboard. Mappings are updated automatically as packages are registered/unregistered.
   ```java
   TypeDiscriminatorService typeService = new TypeDiscriminatorService();
   whiteboard.addMetadataHandler(typeService);
   // Mappings are populated automatically via handler callbacks
   ```

2. **Static:** Use `TypeDiscriminatorService.fromMetadataService(metadataService)` for a one-time snapshot. This scans all currently registered packages but does NOT update when packages change.
   ```java
   TypeDiscriminatorService typeService = TypeDiscriminatorService.fromMetadataService(metadataService);
   // Static snapshot — not updated when packages change
   ```

**Usage with CodecResource:**
```java
// Pass the reader interface to CodecResource
CodecResource resource = new CodecResource(uri, contentType, metadataService,
    codecAspectProvider, formatProvider, options, typeService);
// CodecResource uses the TypeDiscriminatorReader for type resolution
// instead of creating a fresh TypeDiscriminatorService on every save/load
```

---

## Current Status and Next Steps

### Completed:
1. ✅ CodecAspectProvider implementation (1,197 lines)
2. ✅ Aspect building (ClassCodecAspect, FeatureCodecAspect, ReferenceCodecAspect)
3. ✅ Profile building (CodecPackageProfile, CodecClassProfile)
4. ✅ Annotation validation and diagnostics
5. ✅ TypeDiscriminatorService with MetadataHandler integration
6. ✅ TypeDiscriminatorReader interface (read-only query API)
7. ✅ Test coverage (179+ tests passing)

### Next Steps:
1. **EClass hierarchy inheritance** - Implement `inherit=DIRECT/ALL/NONE` across parent EClasses in profile building
2. **Runtime ConfigurationResolver** - Merge dynamic overrides (levels 1-4) on top of profile (levels 5+6)
3. **Codec V2 integration** - Wire profiles into the new codec serialization pipeline
4. **Custom value readers/writers** - Implement ValueReader/ValueWriter registry and resolution
