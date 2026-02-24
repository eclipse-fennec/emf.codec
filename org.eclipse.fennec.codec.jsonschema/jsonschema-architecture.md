# JSON Schema Support — Architecture

`org.eclipse.fennec.codec.jsonschema` provides bidirectional conversion between EMF (`EPackage` / `EClass`) and JSON Schema documents.

---

## Usage Modes

### Mode 1 — Standalone file I/O via `JsonSchemaResourceImpl`

Use this mode when the JSON Schema is a standalone file that you want to load or save as an EMF resource.

```java
ResourceSet rs = new ResourceSetImpl();
rs.getResourceFactoryRegistry()
  .getExtensionToFactoryMap()
  .put("jsonschema", new JsonSchemaResourceFactoryImpl());

// Load: JSON Schema file → EPackage
Resource resource = rs.getResource(URI.createFileURI("/path/to/schema.jsonschema"), true);
EPackage ePackage = (EPackage) resource.getContents().get(0);

// Save: EPackage → JSON Schema file
Resource out = rs.createResource(URI.createFileURI("/path/to/out.jsonschema"));
out.getContents().add(ePackage);
out.save(Map.of(JsonSchemaResourceImpl.OPTION_PRETTY_PRINT, Boolean.TRUE));
```

In OSGi, the factory is registered automatically as a DS component for the `"jsonschema"` extension and `"application/schema+json"` content type. Outside OSGi, register manually as shown above.

If you want to work with `.json` files (a common convention for JSON Schema), register the same factory under `"json"` as well — but be aware that EMF uses the same `"json"` extension by default for `XMIResourceFactoryImpl`, so prefer using `"jsonschema"` or the content-type registry.

**Load options:**

| Option key | Type | Default | Description |
|------------|------|---------|-------------|
| `jsonschema.feature.key` | `String` | auto-detect | Definitions section key (`"definitions"`, `"$defs"`, `"schemas"`). Pass `null` to auto-detect. |

**Save options:**

| Option key | Type | Default | Description |
|------------|------|---------|-------------|
| `jsonschema.feature.key` | `String` | `"$defs"` | Definitions section key written in the output. |
| `jsonschema.pretty.print` | `Boolean` | `true` | Format output with indentation. |

Conversion diagnostics (unsupported or partially supported JSON Schema constructs) are reported as `Resource.getWarnings()`.

### Mode 2 — Embedded schemas via value handlers

Use this mode when a JSON Schema document is **nested inside another format** (e.g., an OpenAPI document). The value handlers integrate with the codec v2 value transformation layer.

#### EPackage ↔ JSON Schema (multi-class schemas with `definitions`)

```java
// Reading an EPackage from an embedded JSON Schema (e.g. OpenAPI components/schemas):
registry.registerReader("schemas", new EPackageValueReader());

// Writing an EPackage as an embedded JSON Schema:
registry.registerWriter("schemas", new EPackageValueWriter());
```

- Reader name: `"jsonSchemaToEPackage"`
- Writer name: `"ePackageToJsonSchema"`
- Handles `EReference` whose type is `EPackage` (or a supertype).

#### EClass ↔ JSON Schema (single-class documents)

```java
// Reading a single JSON Schema document into an EClass:
registry.registerReader("schema", new EClassValueReader());

// Writing an EClass as a single JSON Schema document:
registry.registerWriter("schema", new EClassValueWriter());
```

- Reader name: `"jsonSchemaToEClass"`
- Writer name: `"eClassToJsonSchema"`
- Handles `EReference` whose type is `EClass` (or a supertype).

---

## Converter Classes

### `EPackageToJsonSchemaConverter`

Converts an `EPackage` (or a single `EClass`) to a JSON Schema document.

```java
// EPackage → schema with $defs
new EPackageToJsonSchemaConverter().convert(ePackage, outputStream, "$defs", true);

// Single EClass → schema document
new EPackageToJsonSchemaConverter().convertEClass(eClass, outputStream, true, Map.of());
```

**Options (`Map<String, Object>`):**

| Constant | Value | Description |
|----------|-------|-------------|
| `OPTION_ALL_FIELDS_REQUIRED` | `"allFieldsRequired"` | Mark every property as `required`, regardless of `lowerBound`. Useful for AI structured-output schemas. |
| `OPTION_USE_ANCHOR_REFS` | `"useAnchorRefs"` | Use `$anchor`/`$ref` instead of inline `type: object` for cross-class references. |

### `JsonSchemaToEPackageConverter`

Converts a JSON Schema document to an `EPackage`.

```java
// From InputStream
EPackage pkg = new JsonSchemaToEPackageConverter().convert(inputStream, "$defs");

// From JsonNode
EPackage pkg = new JsonSchemaToEPackageConverter().convert(jsonNode, null);

// Single-class mode
EClass cls = new JsonSchemaToEPackageConverter().convertToEClass(jsonNode, "MyClass");
```

### `EClassToJsonSchemaConverter`

Thin wrapper around `EPackageToJsonSchemaConverter` for single-class schemas.

```java
EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();

// Basic
converter.convert(eClass, out);

// Pretty-printed
converter.convert(eClass, out, true);

// All fields required (AI structured output)
converter.convert(eClass, out, true, true);

// Full option control
converter.convert(eClass, out, true, Map.of(
    EPackageToJsonSchemaConverter.OPTION_ALL_FIELDS_REQUIRED, Boolean.TRUE
));
```

### `JsonSchemaToEClassConverter`

Thin wrapper around `JsonSchemaToEPackageConverter` for single-class schemas. Derives the class name from the `"title"` field, or defaults to `"EClass"`.

```java
JsonSchemaToEClassConverter converter = new JsonSchemaToEClassConverter();

EClass cls = converter.convert(jsonNode);               // name from "title"
EClass cls = converter.convert(jsonNode, "ExplicitName");
```

---

## Annotation Mapping

The converters read and write the following EAnnotation sources.

### `http://fennec.eclipse.org/jsonschema` (JSONSCHEMA)

| EAnnotation detail key | JSON Schema field | Applies to |
|------------------------|-------------------|------------|
| `schema` | `$schema` | EPackage, EClass |
| `id` | `$id` | EClass |
| `originalTitle` | `title` (exact, pre-capitalization value) | EClass |
| `anchor` | `$anchor` | EClass |
| `comment` | `$comment` | EClass |
| `deprecated` | `deprecated` | EClass, EStructuralFeature |
| `additionalProperties` | `additionalProperties` | EClass |

### `http://www.eclipse.org/emf/2002/GenModel` (GEN_MODEL)

| EAnnotation detail key | JSON Schema field | Applies to |
|------------------------|-------------------|------------|
| `documentation` | `description` | EClass, EStructuralFeature |

### `http://www.eclipse.org/emf/2002/Ecore/ExtendedMetaData` (EXTENDED_METADATA)

| EAnnotation detail key | Effect |
|------------------------|--------|
| `name` | Used as original (pre-capitalization) name for the title field |

---

## Class Name Capitalization

JSON Schema `title` values are treated as Java class names: the first character is upper-cased to form the `EClass.name`. To preserve the exact original value for round-trips, the reader stores the raw title in a `JSONSCHEMA` annotation with key `originalTitle`. The writer reads this annotation (falling back to `EXTENDED_METADATA "name"`, then `JSONSCHEMA "originalName"`, then `EClass.getName()`) when emitting the `title` field.

---

## `required` Array

A property is included in the `required` array if:

- The structural feature has `lowerBound >= 1` (i.e., `feature.isRequired()`), **or**
- The `OPTION_ALL_FIELDS_REQUIRED` converter option is set to `true`.

---

## Diagnostics

Unresolvable `$ref` links, unsupported `allOf`/`anyOf` constructs, or missing type information produce `JsonSchemaConversionDiagnostic` entries. In standalone mode these are surfaced as `Resource.getWarnings()`. In programmatic use retrieve them via `JsonSchemaToEPackageConverter.getDiagnostics()`.

---

## Open Issues

### OI-1: `JsonSchemaResourceImpl` bypasses `CodecResource`

`JsonSchemaResourceImpl` extends `ResourceImpl` directly, not `CodecResource`. This is intentional — JSON Schema conversion is a **meta-format** operation (converting between metamodels, not serializing EObject instances). However, it means:

- **Codec annotations are ignored.** `FeatureCodecAspect` settings such as `effectiveKey` (property rename), `ignore` / `ignoreRead` / `ignoreWrite`, and `enumSerialization` are not consulted during schema generation or parsing. The produced schema reflects the raw Ecore model, not what the codec actually serializes at runtime.
- If a class uses `effectiveKey = "firstName"` for an attribute named `name`, the generated JSON Schema will still use `"name"` as the property key, not `"firstName"`.

**Impact:** Consumers generating schemas for codec-driven APIs should be aware that the schema and the actual wire format may differ if codec annotations are in use.

**Resolution path:** A future integration layer could accept a `MetadataService` / `EffectiveCodecConfig` and use it to resolve the effective property names and ignore-flags before emitting schema properties.

### OI-2: No OSGi whiteboard for `MetadataService`

`MetadataServiceImpl` is not a Declarative Services (DS) component. In an OSGi environment, there is currently no whiteboard that automatically registers incoming `EPackage` OSGi services with a `MetadataService`. Wiring must be done manually:

```java
MetadataServiceImpl svc = new MetadataServiceImpl();
svc.addEPackage(MyModelPackage.eINSTANCE);
// ... add aspect providers, metadata handlers, etc.
```

**Resolution path:** A `MetadataWhiteboardComponent` DS component (in `org.eclipse.fennec.model.metadata`) that dynamically tracks `EPackage`, `AspectProvider`, `MetadataHandler`, and `MetadataIndex` services, and a `CodecAspectProviderComponent` (in `org.eclipse.fennec.codec.metadata`) that registers `CodecAspectProvider` as an `AspectProvider` OSGi service.

### OI-3: No default `$schema` declaration

The writer does not emit a `$schema` field unless the EPackage or EClass carries an explicit `JSONSCHEMA "schema"` annotation. Consumers relying on schema version auto-detection may need to add this annotation or post-process the output.
