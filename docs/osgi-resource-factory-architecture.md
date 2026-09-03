# OSGi Codec Resource Factory — Architecture Proposal

## 1. Problem Statement

In OSGi integration tests (and OSGi applications in general), creating a `CodecResource` requires manual wiring:

```java
CodecResource resource = new CodecResource(
    URI.createURI("test://example.json"),
    metadataService,
    ConfigurationResolver.defaults(),
    valueRegistry,
    null,           // mapperBuilder
    formatProvider, // null for JSON
    typeDiscriminatorReader
);
```

The standard EMF pattern — `resourceSet.createResource(URI)` — does not work because there is no OSGi-registered `Resource.Factory` that produces fully-wired `CodecResource` instances.

**Goal:** Enable `resourceSet.createResource(URI.createURI("foo.json"))` to return a `CodecResource` with all dependencies injected via OSGi Declarative Services.

## 2. Existing Patterns

### 2.1 Domain-Specific Factories (GeoJSON, OpenAPI, JSON Schema)

These bundles register `Resource.Factory` as DS components with `@Reference MetadataService`. The
factory itself is plain Java in the exported package, the DS annotations sit on a `*Component`
subclass in the non-exported `internal` package (issues #58, #147) — so the factory stays usable
outside OSGi and the wiring stays out of the API:

```java
// org.eclipse.fennec.codec.geojson.GeoJsonResourceFactoryImpl (exported)
public class GeoJsonResourceFactoryImpl extends ResourceFactoryImpl {
    public GeoJsonResourceFactoryImpl(MetadataService metadataService) { ... }
    public GeoJsonResourceFactoryImpl() { ... }   // standalone: own whiteboard
}

// org.eclipse.fennec.codec.geojson.internal.GeoJsonResourceFactoryComponent (private)
@Component(service = Resource.Factory.class,
    property = {
        EMFNamespaces.EMF_CONFIGURATOR_NAME + "=" + GeoJsonPackage.eNAME,
        EMFNamespaces.EMF_MODEL_FILE_EXT + "=" + "geojson",
        EMFNamespaces.EMF_MODEL_VERSION + "=" + "1.0",
        EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=" + GeoJsonResourceFactoryImpl.CONTENT_TYPE_GEO_JSON,
        EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=" + GeoJsonResourceFactoryImpl.CONTENT_TYPE_GEO_JSON_LEGACY
    },
    reference = {
        @Reference(name = "geojsonPackage", service = GeoJsonPackage.class)
    })
public class GeoJsonResourceFactoryComponent extends GeoJsonResourceFactoryImpl {
    @Activate
    public GeoJsonResourceFactoryComponent(@Reference MetadataService metadataService) {
        super(metadataService);
    }
}
```

**Every factory registers its content type(s), not only a file extension.** The REST message body
reader/writer resolves the factory for a request exclusively through
`getContentTypeToFactoryMap()`, so a factory without `EMF_MODEL_CONTENT_TYPE` is invisible to
`@Produces`/`@Consumes` (issue #168: GeoJSON could not be served as `application/geo+json`).
Several types are several property values, as in CSV and GeoJSON.

**Limitation:** These are tightly coupled to a specific model and do not support custom value registries, format providers, or configuration beyond defaults.

### 2.2 Non-OSGi Factories

- `CodecResourceFactory` — plain Java factory with setter-based DI; supports `MetadataService`, `ConfigurationResolver`, `JsonMapper.Builder`, default save/load options. Does **not** support `CodecValueRegistry`, `CodecFormatProvider`, or `TypeDiscriminatorReader`.
- `CodecFormatResourceFactory` — adds `CodecFormatProvider` support but still no value registry or type discriminator.

### 2.3 MetadataServiceComponent (Whiteboard Orchestrator)

The `MetadataServiceComponent` is the established whiteboard pattern in this codebase:
- Collects `EPackage`, `AspectProvider`, `MetadataHandler` services dynamically
- Uses `DYNAMIC` policy with `MULTIPLE` cardinality for most references
- Registered as `immediate = true`

### 2.4 Format Providers (BSON, CBOR, YAML)

Currently plain Java classes with no DS annotations:
- `BsonFormatProvider`, `CborFormatProvider`, `YamlFormatProvider`
- Each implements `CodecFormatProvider<S, T>`
- No OSGi service registration

## 3. Proposed Architecture

### 3.1 Overview

```
┌──────────────────────────────────────────────────────────┐
│                    OSGi Service Registry                  │
│                                                          │
│  ┌─────────────────┐  ┌──────────────────────────────┐  │
│  │ MetadataService  │  │ CodecValueWriter/Reader (*)  │  │
│  │ (required)       │  │ (whiteboard, 0..n)           │  │
│  └────────┬────────┘  └──────────────┬───────────────┘  │
│           │                          │                   │
│  ┌────────┴──────────────────────────┴───────────────┐  │
│  │         CodecResourceFactoryComponent             │  │
│  │         (service = Resource.Factory)               │  │
│  │                                                    │  │
│  │  - Assembles CodecValueRegistry from whiteboard    │  │
│  │  - Builds ConfigurationResolver from Config Admin  │  │
│  │  - Creates fully-wired CodecResource instances     │  │
│  └────────┬──────────────────────────────────────────┘  │
│           │                                              │
│  ┌────────┴────────┐  ┌────────────────────────────┐   │
│  │ Resource.Factory │  │ CodecFormatProvider (*)     │   │
│  │ (json extension) │  │ (optional, 0..n)           │   │
│  └─────────────────┘  └────────────────────────────┘   │
│                                                          │
│  Per-format factory instances (BSON, CBOR, YAML)         │
│  created dynamically when CodecFormatProvider appears    │
└──────────────────────────────────────────────────────────┘
```

### 3.2 Component 1: `CodecResourceFactoryComponent`

**Bundle:** `org.eclipse.fennec.codec`
**Package:** `org.eclipse.fennec.codec.resource`

This is the core component. It registers as `Resource.Factory` for the `.json` extension and assembles a `CodecValueRegistry` from whiteboardcollected value writers/readers.

```java
@Component(
    name = "CodecResourceFactory",
    service = Resource.Factory.class,
    property = {
        EMFNamespaces.EMF_MODEL_FILE_EXT + "=json",
        EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/json"
    }
)
public class CodecResourceFactoryComponent extends ResourceFactoryImpl {

    private final MetadataService metadataService;
    private final CodecValueRegistry valueRegistry = new CodecValueRegistry();
    private volatile ConfigurationResolver resolver = ConfigurationResolver.defaults();

    @Activate
    public CodecResourceFactoryComponent(
            @Reference MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    // --- Whiteboard: Custom Value Writers ---
    @Reference(
        cardinality = ReferenceCardinality.MULTIPLE,
        policy = ReferencePolicy.DYNAMIC,
        unbind = "removeValueWriter"
    )
    void addValueWriter(CodecValueWriter<?, ?> writer) {
        valueRegistry.register(writer);
    }

    void removeValueWriter(CodecValueWriter<?, ?> writer) {
        valueRegistry.unregisterWriter(writer.getName());
    }

    // --- Whiteboard: Custom Value Readers ---
    @Reference(
        cardinality = ReferenceCardinality.MULTIPLE,
        policy = ReferencePolicy.DYNAMIC,
        unbind = "removeValueReader"
    )
    void addValueReader(CodecValueReader<?, ?> reader) {
        valueRegistry.register(reader);
    }

    void removeValueReader(CodecValueReader<?, ?> reader) {
        valueRegistry.unregisterReader(reader.getName());
    }

    @Override
    public Resource createResource(URI uri) {
        return new CodecResource(
            uri,
            metadataService,
            resolver,
            valueRegistry.copy(),  // snapshot to avoid concurrent modification
            null,                  // mapperBuilder (default)
            null,                  // formatProvider (JSON = null)
            null                   // typeDiscriminatorReader (built internally)
        );
    }
}
```

**Key design decisions:**

1. **`valueRegistry.copy()`** — Each `CodecResource` gets a snapshot of the registry at creation time. This avoids race conditions if writers/readers are added/removed while a resource is in use.

2. **No `TypeDiscriminatorReader` injection** — `CodecResource` already builds one internally from `MetadataService` when needed (in `createObjectMapper()`). Injecting an external one is an optimization for high-throughput scenarios that can be added later.

3. **`ConfigurationResolver` from defaults** — see Section 3.4 for Config Admin integration.

### 3.3 Component 2: Format-Specific Factory Components

Each format provider bundle (BSON, CBOR, YAML) registers its own `CodecFormatProvider` as an OSGi service and a corresponding `Resource.Factory`.

**Option A — One component per format bundle (recommended):**

```java
// In org.eclipse.fennec.codec.cbor bundle
@Component(
    name = "CborResourceFactory",
    service = Resource.Factory.class,
    property = {
        EMFNamespaces.EMF_MODEL_FILE_EXT + "=cbor",
        EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/cbor"
    }
)
public class CborResourceFactoryComponent extends ResourceFactoryImpl {

    private final MetadataService metadataService;
    private final CborFormatProvider formatProvider = new CborFormatProvider();

    @Activate
    public CborResourceFactoryComponent(
            @Reference MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public Resource createResource(URI uri) {
        return new CodecResource(
            uri, metadataService,
            ConfigurationResolver.defaults(),
            null, null,
            formatProvider
        );
    }
}
```

This follows the same pattern as `GeoJsonResourceFactoryImpl` / `GeoJsonResourceFactoryComponent` — simple, explicit, one component per format. Each format bundle owns its factory.

**Option B — Central factory with dynamic format discovery (alternative):**

The `CodecResourceFactoryComponent` could also collect `CodecFormatProvider` services via whiteboard and dynamically register additional `Resource.Factory` services for each format's file extensions. This is more complex and harder to reason about, so Option A is recommended unless there's a need for a single configurable factory.

### 3.4 Component 3: Value Handler Registration

Custom value handlers (like `EClassValueWriter`/`EClassValueReader` from the JSON Schema bundle) register themselves as OSGi services so the factory's whiteboard picks them up automatically.

```java
// In org.eclipse.fennec.codec.jsonschema bundle
@Component(service = CodecValueWriter.class)
public class EClassValueWriterComponent extends EClassValueWriter {
    // Inherits getName() = "eClassToJsonSchema"
    // Inherits canHandle() and write() from EClassValueWriter
}

@Component(service = CodecValueReader.class)
public class EClassValueReaderComponent extends EClassValueReader {
    // Inherits getName() = "jsonSchemaToEClass"
    // Inherits canHandle() and read() from EClassValueReader
}
```

Alternatively, if modifying existing classes to add `@Component` is preferred over wrapper subclasses, the annotations can be added directly to `EClassValueWriter` and `EClassValueReader`. However, this couples the implementation to OSGi, so the wrapper approach keeps them framework-agnostic.

### 3.5 Configuration via Config Admin (Future Enhancement)

A `@Designate` + `@ObjectClassDefinition` annotation on the factory component would allow configuring `ConfigurationResolver` properties from Config Admin:

```java
@ObjectClassDefinition(name = "Codec Resource Factory Configuration")
@interface CodecResourceFactoryConfig {
    boolean typeInclude() default true;
    String idKeyMode() default "BOTH";
    boolean useNamesFromExtendedMetadata() default false;
    // ... other ConfigProperty values
}

@Component(...)
@Designate(ocd = CodecResourceFactoryConfig.class)
public class CodecResourceFactoryComponent extends ResourceFactoryImpl {

    @Activate
    public CodecResourceFactoryComponent(
            @Reference MetadataService metadataService,
            CodecResourceFactoryConfig config) {
        this.metadataService = metadataService;
        this.resolver = ConfigurationResolver.builder()
            .typeInclude(config.typeInclude())
            .idKeyMode(config.idKeyMode())
            .useNamesFromExtendedMetaData(config.useNamesFromExtendedMetadata())
            .build();
    }

    @Modified
    void modified(CodecResourceFactoryConfig config) {
        // Rebuild resolver — new resources get updated config
        this.resolver = ConfigurationResolver.builder()...build();
    }
}
```

This is a nice-to-have for Phase 2 and not required for the initial implementation.

## 4. ResourceSet Integration

In OSGi environments using [Gecko EMF](https://github.com/geckoprojects-org/org.gecko.emf) or similar, `Resource.Factory` services registered with `EMF_MODEL_FILE_EXT` properties are automatically picked up by `ResourceSet` implementations. The standard flow:

```
resourceSet.createResource(URI.createURI("data.json"))
  → ResourceSet.getResourceFactoryRegistry()
  → looks up factory for ".json" extension
  → finds CodecResourceFactoryComponent
  → calls createResource(URI)
  → returns fully-wired CodecResource
```

No explicit registration code is needed in user bundles — the DS component registration handles everything.

## 5. Impact on Existing Tests

With this architecture, OSGi tests simplify from:

```java
// Before
CodecResource resource = new CodecResource(
    URI.createURI("test://example.json"), metadataService,
    ConfigurationResolver.defaults(), valueRegistry, null);
```

To:

```java
// After
@InjectService
ResourceSet resourceSet;  // or create one and let the factory be discovered

Resource resource = resourceSet.createResource(URI.createURI("test://example.json"));
```

The `MetadataService`, `CodecValueRegistry` (populated from whiteboard), and `ConfigurationResolver` are all injected automatically.

## 6. Implementation Phases

### Phase 1 — Core Factory (Minimum Viable)
1. Create `CodecResourceFactoryComponent` in `org.eclipse.fennec.codec`
2. Add whiteboard collection for `CodecValueWriter`/`CodecValueReader`
3. Register for `.json` extension
4. Add OSGi integration test verifying `resourceSet.createResource()` works

### Phase 2 — Format Providers
5. Create `CborResourceFactoryComponent` in `org.eclipse.fennec.codec.cbor`
6. Create `YamlResourceFactoryComponent` in `org.eclipse.fennec.codec.yaml`
7. (BSON is in-memory, no file extension — skip unless needed)
8. Register `EClassValueWriter`/`EClassValueReader` as OSGi services

### Phase 3 — Configuration
9. Add `@Designate` + Config Admin support for `ConfigurationResolver` properties
10. Add default save/load options support

## 7. Open Questions

1. **ReferenceValueReader vs CodecValueReader service type** — The whiteboard collects `CodecValueWriter<?, ?>` and `CodecValueReader<?, ?>`. `ReferenceValueWriter`/`ReferenceValueReader` extend these, so they'll be collected too. However, should the whiteboard also have a separate target filter for reference-specific handlers?

2. **Multiple JSON factories** — If an application wants two different JSON factories with different configurations (e.g., one with `typeInclude=false`), they'd need separate component instances via factory configuration. Is this a requirement?

3. **ResourceSet service** — Does the OSGi environment provide a `ResourceSet` service, or should the factory also register a pre-configured `ResourceSet`? This depends on whether Gecko EMF or a similar bridge is in the target platform.

4. **Backward compatibility** — The existing `CodecResourceFactory` (non-OSGi) should remain unchanged. The new `CodecResourceFactoryComponent` is a separate class that builds on the same `CodecResource` constructor.
