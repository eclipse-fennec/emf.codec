# Migration Plan: Jakarta REST MessageBodyReaderWriter

**gecko emf.rest → fennec codec**

## Overview

Replace the old `org.gecko.emf.rest` Jakarta handlers (which use `emf-json` / `emfcloud-jackson`) with new handlers that use fennec's `CodecResource` for serialization/deserialization.

The old implementation lives in `/opt/git/geckoprojects-emf-utils/org.gecko.emf.rest` (Jakarta RS classes only — ignore the jaxrs package).

---

## Step 1: Create a new bundle `org.eclipse.fennec.codec.rest`

Create in the fennec-codec workspace with this structure:

```
org.eclipse.fennec.codec.rest/
├── bnd.bnd
└── src/org/eclipse/fennec/codec/rest/
    ├── annotations/
    │   ├── AnnotationConverter.java          (interface)
    │   ├── CodecConfig.java                  (annotation, replaces @EMFJSONConfig)
    │   ├── RootElement.java                  (annotation, same concept)
    │   ├── ResourceEClass.java               (annotation, same concept)
    │   ├── ContentNotEmpty.java              (annotation, same concept)
    │   ├── ValidateContent.java              (annotation, same concept)
    │   └── ResourceOption.java               (annotation, same concept)
    └── internal/
        ├── AbstractCodecAnnotationHandler.java
        ├── BaseCodecMessageBodyReaderWriter.java
        ├── CodecResourceMessageBodyHandler.java   (handles Resource)
        ├── CodecEObjectMessageBodyHandler.java     (handles EObject)
        └── CodecAnnotationConverter.java           (converts @CodecConfig → CodecOptions)
```

---

## Step 2: Define the `@CodecConfig` annotation (replaces `@EMFJSONConfig`)

Map old EMFJs options to new `CodecOptions` constants:

| Old (`@EMFJSONConfig`)   | New (`@CodecConfig`)         | Codec Option                         |
|--------------------------|------------------------------|--------------------------------------|
| `dateFormat`             | `dateFormat`                 | `CODEC_DATE_FORMAT` (if exists, or pass through options) |
| `indentOutput`           | `indentOutput`               | Pass as Jackson `SerializationFeature` |
| `serializeDefaultValues` | `serializeDefaultValues`     | `CODEC_DEFAULT_VALUE_STRATEGY`       |
| `serializeTypes`         | `typeInclude`                | `CODEC_TYPE_INCLUDE` (`ALWAYS` / `NONE`) |
| `useId`                  | `idStrategy`                 | `CODEC_ID_STRATEGY`                  |
| `refFieldName`           | `referenceKey`               | `CODEC_REFERENCE_KEY`                |
| `idFieldName`            | `idKey`                      | `CODEC_ID_KEY`                       |
| `typeFieldName`          | `typeKey`                    | `CODEC_TYPE_KEY`                     |
| `typeUSE` (URI/NAME/CLASS) | `typeStrategy`            | `CODEC_TYPE_STRATEGY` (`URI`/`NAME`/`CLASS`) |
| `typePackageUri`         | `typeSchemaKey`              | `CODEC_TYPE_SCHEMA_KEY`              |

**Action:** Before finalizing the annotation fields, cross-check each mapping against the actual constants in `CodecOptions.java` and the codec spec in `docs/codec-v2-spec/`.

---

## Step 3: Rewrite `BaseCodecMessageBodyReaderWriter`

Key changes from old to new:

### 3.1 Dependency changes

1. **Drop `ResourceSetFactory`** — Instead, inject `CodecResourceFactory` (or the OSGi `Resource.Factory` service registered by `CodecResourceFactoryComponent`)
2. **Drop `EMFModelInfo`** — Replace with `MetadataService` for EClass lookups during deserialization

### 3.2 Replace the factory-lookup-by-content-type pattern

- Old: `resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().get(mediaType)`
- New: Directly use `CodecResourceFactory.createResource(uri)` — the codec handles format via the `CodecFormatProvider` system

### 3.3 Replace option building

- Old: `EMFJs.OPTION_*` constants, `XMLResource.OPTION_*`
- New: `CodecOptions.CODEC_*` constants passed as `Map<String, Object>` to `resource.load()/save()`

### 3.4 Root element resolution

- Old: `modelInfo.getEClassifierForClass(type)` → `EMFJs.OPTION_ROOT_ELEMENT`
- New: `metadataService.getClassMetadataByName(...)` or pass `CodecOptions.CODEC_ROOT_TYPE`

### 3.5 Remove `XMLURIHandler`

Codec handles URI resolution internally — no need for the old `XMLURIHandler`.

### 3.6 Write flow

```java
CodecResource resource = (CodecResource) codecResourceFactory.createResource(URI.createURI("temp://rest"));
resource.getContents().add(EcoreUtil.copy(eObject));
Map<String, Object> options = buildOptionsFromAnnotations(annotations);
resource.save(entityStream, options);
```

### 3.7 Read flow

```java
CodecResource resource = (CodecResource) codecResourceFactory.createResource(URI.createURI("temp://rest"));
Map<String, Object> options = buildOptionsFromAnnotations(annotations);
options.put(CodecOptions.CODEC_ROOT_TYPE, resolvedEClass); // if available
resource.load(entityStream, options);
```

---

## Step 4: Implement the two concrete handlers

### 4.1 `CodecResourceMessageBodyHandler` — handles `Resource` types

- OSGi `@Component` with `ServiceScope.PROTOTYPE`
- `@JakartarsExtension`, `@JakartarsName("FennecCodecResourceMessageBodyReaderWriter")`
- `@Produces(MediaType.WILDCARD)` / `@Consumes(MediaType.WILDCARD)`
- `isReadable`/`isWriteable`: check `Resource.class.isAssignableFrom(type)` + media type is supported

### 4.2 `CodecEObjectMessageBodyHandler` — handles `EObject` types

- OSGi `@Component` with `ServiceScope.SINGLETON`
- Same whiteboard config
- `isReadable`/`isWriteable`: check `EObject.class.isAssignableFrom(type)` + media type is supported
- On read: extract first content from resource and return the EObject
- On write: wrap detached EObject in a temporary resource, serialize, then clean up

---

## Step 5: Implement `CodecAnnotationConverter`

Replaces `EMFJsonAnnotationConverter`. Converts `@CodecConfig` annotation fields into `CodecOptions.*` entries in the options map. Also handles `@RootElement` by resolving the EClass URI via `MetadataService`.

---

## Step 6: Handle media type → format provider mapping

The old system relied on `contentTypeToFactoryMap` in EMF's `ResourceSet`. The new system needs a way to resolve `MediaType` → `CodecFormatProvider`.

### Option A (recommended)

Maintain a map of content types to `CodecFormatProvider` instances, populated via OSGi whiteboard. Each `CodecFormatProvider` already declares `getContentTypes()` — use this.

### Option B

Register multiple `CodecResourceFactoryComponent` instances (one per format) with different `EMF_MODEL_CONTENT_TYPE` service properties, and let the whiteboard resolve them.

The `isReadable`/`isWriteable` check should query this map to determine if the requested media type is supported.

---

## Step 7: bnd.bnd configuration

```properties
Bundle-Name: Fennec Codec Jakarta REST Integration
Bundle-Description: Jakarta RS MessageBodyReader/Writer using Fennec Codec

-buildpath: \
    org.osgi.service.component.annotations,\
    org.osgi.service.jakartars;version=latest,\
    jakarta.ws.rs-api;version=latest,\
    org.eclipse.fennec.codec.api;version=snapshot,\
    org.eclipse.fennec.codec;version=snapshot,\
    org.eclipse.fennec.model.metadata;version=snapshot,\
    org.eclipse.emf.ecore,\
    org.eclipse.emf.common

Export-Package: org.eclipse.fennec.codec.rest.annotations
Private-Package: org.eclipse.fennec.codec.rest.internal
```

---

## Step 8: Validation annotations

Port these annotations as-is (they are format-agnostic):

- `@ContentNotEmpty` — check `resource.getContents().isEmpty()`
- `@ResourceEClass` — check first content's EClass name
- `@ValidateContent` — run `Diagnostician.INSTANCE.validate()`

The validation handler throws `WebApplicationException` with appropriate HTTP status codes (400, 406) just like the old one.

---

## Step 9: Testing

1. **Unit tests** for `CodecAnnotationConverter` — verify each `@CodecConfig` field maps to the correct `CodecOptions` key
2. **Integration tests** with a real Jakarta RS whiteboard:
   - Serialize/deserialize EObjects via `application/json`
   - Serialize/deserialize via `application/cbor`, `application/yaml` (if format providers are available)
   - Annotation-driven configuration (`@CodecConfig` on endpoint methods)
   - Validation annotations (`@ContentNotEmpty`, `@ResourceEClass`, `@ValidateContent`)
3. **TCK extension** — consider adding REST-specific TCK tests if cross-format REST verification is needed

---

## Migration checklist for consumers

When migrating existing REST endpoints from old to new:

| Change | Details |
|--------|---------|
| Bundle dependency | `org.gecko.emf.rest` → `org.eclipse.fennec.codec.rest` |
| Import package | `org.gecko.emf.rest.annotations.json.*` → `org.eclipse.fennec.codec.rest.annotations.*` |
| Annotation | `@EMFJSONConfig` → `@CodecConfig` |
| Annotation | `@RootElement` → `@RootElement` (new package) |
| Require capability | Drop `@RequireEMFJson`, no equivalent needed (codec auto-registers) |
| Options constants | `EMFJs.OPTION_*` → `CodecOptions.CODEC_*` |
| Whiteboard filter | `(emf=true)` → decide on new property name, e.g. `(fennec.codec=true)` |
