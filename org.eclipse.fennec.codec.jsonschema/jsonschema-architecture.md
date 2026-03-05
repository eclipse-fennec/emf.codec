# JSON Schema Support — Architecture

`org.eclipse.fennec.codec.jsonschema` provides bidirectional conversion between EMF (`EPackage` / `EClass`) and JSON Schema documents.

---

## Usage Modes

### Mode 1 — Standalone file I/O via `JsonSchemaResourceImpl`

Use this mode when the JSON Schema is a standalone file that you want to load or save as an EMF resource.

`JsonSchemaResourceImpl` extends `CodecResource`, which gives it access to the codec configuration pipeline (including `customProperties` for format-specific options). However, JSON Schema conversion is a **meta-format** operation — it converts between metamodels rather than serializing EObject instances — so not all `CodecResource` features apply. The resource creates its own `ConfigurationResolver` and `CodecValueRegistry` internally.

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
out.save(Map.of(CodecJsonSchemaOptions.OPTION_PRETTY_PRINT, Boolean.TRUE));
```

In OSGi, `JsonSchemaResourceFactoryImpl` is registered automatically as a DS component (`@Component`) for the `"jsonschema"` extension and `"application/schema+json"` content type. It receives a `MetadataService` via `@Reference` injection. Outside OSGi, the no-arg constructor uses `MetadataServiceFactory.create()` to obtain a `MetadataWhiteboard` instance; register the factory manually as shown above.

If you want to work with `.json` files (a common convention for JSON Schema), register the same factory under `"json"` as well — but be aware that EMF uses the same `"json"` extension by default for `XMIResourceFactoryImpl`, so prefer using `"jsonschema"` or the content-type registry.

**Load options:**

| Option key | Type | Default | Description |
|------------|------|---------|-------------|
| `codec.jsonschema.feature.key` | `String` | auto-detect | Definitions section key (`"definitions"`, `"$defs"`, `"schemas"`). Pass `null` to auto-detect. |

**Save options:**

| Option key | Type | Default | Description |
|------------|------|---------|-------------|
| `codec.jsonschema.feature.key` | `String` | `"$defs"` | Definitions section key written in the output. |
| `codec.jsonschema.pretty.print` | `Boolean` | `true` | Format output with indentation. |

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

Options are passed via `CodecJsonSchemaOptions` constants from `org.eclipse.fennec.codec.jsonschema.v2.constants`. When used through `CodecResource` load/save, they flow automatically via the `customProperties` mechanism (see below).

| Constant | Value | Type | Description |
|----------|-------|------|-------------|
| `OPTION_ALL_FIELDS_REQUIRED` | `"codec.jsonschema.allFieldsRequired"` | `Boolean` | Mark every property as `required`, regardless of `lowerBound`. Useful for AI structured-output schemas. |
| `OPTION_USE_ANCHOR_REFS` | `"codec.jsonschema.useAnchorRefs"` | `Boolean` | Use `$anchor`/`$ref` instead of inline `type: object` for cross-class references. |
| `OPTION_FLAT_ALL_OF` | `"codec.jsonschema.flatAllOf"` | `Boolean` | Flatten `allOf`/`$ref` inheritance — inline parent properties directly into child definitions. |
| `OPTION_USE_NAMES_FROM_EXTENDED_METADATA` | `"codec.jsonschema.useNamesFromExtendedMetadata"` | `Boolean` | Resolve property names from ExtendedMetaData annotations instead of EMF feature names. |
| `OPTION_SUPPRESS_KEYWORDS` | `"codec.jsonschema.suppressKeywords"` | `Collection<String>` | Set of JSON Schema keywords to suppress in the output (e.g., `"maxItems"`, `"description"`, `"additionalProperties"`). |
| `OPTION_SUPPRESS_VENDOR_EXTENSIONS` | `"codec.jsonschema.suppressVendorExtensions"` | `Boolean` | Suppress all `x-*` vendor extension properties (`x-abstract`, `x-interface`, `x-containment`). Use when the target API does not accept vendor extensions. |

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
    CodecJsonSchemaOptions.OPTION_ALL_FIELDS_REQUIRED, Boolean.TRUE,
    CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS, Set.of("maxItems", "description")
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

## Vendor Extensions (`x-*` Properties)

The converters use JSON Schema vendor extension properties to preserve EMF metadata that has no native JSON Schema equivalent. These are written during serialization and consumed during deserialization for lossless round-trips.

| Extension | Type | Applies to | Description |
|-----------|------|------------|-------------|
| `x-abstract` | `boolean` | Class definition | `true` when `EClass.isAbstract()`. Read back as `eClass.setAbstract(true)`. |
| `x-interface` | `boolean` | Class definition | `true` when `EClass.isInterface()`. Read back as `eClass.setInterface(true)` (also sets abstract). |
| `x-containment` | `boolean` | Reference property | `true` for containment references, absent for non-containment. Overrides the default heuristic (inline object = containment, `$ref` = non-containment). |

**Serialization example:**

```json
{
  "Shape": {
    "x-abstract": true,
    "type": "object",
    "properties": { "color": { "type": "string" } },
    "additionalProperties": false
  },
  "Canvas": {
    "type": "object",
    "properties": {
      "shapes": {
        "type": "array",
        "items": {
          "oneOf": [
            { "$ref": "#/definitions/Circle" },
            { "$ref": "#/definitions/Rectangle" }
          ]
        },
        "x-containment": true
      }
    }
  }
}
```

**Reference handling:** Both containment and non-containment references to concrete types use `$ref`. The `x-containment` flag distinguishes them. For abstract types, containment references produce `oneOf`/`anyOf` with `$ref` entries to concrete subclasses plus `x-containment: true`. Non-containment abstract references produce `oneOf`/`anyOf` without `x-containment`.

**Deserialization:** When a property-level `oneOf`/`anyOf` contains only `$ref` entries that all resolve to classes sharing a common abstract supertype, the deserializer creates an `EReference` to that supertype directly — no artificial classes are generated. The `x-containment` flag is applied after feature creation to set the correct containment mode.

These extensions are registered in `JsonSchemaKeywords.EXTENSION_SUPPORTED` and classified as `SupportLevel.FULL`.

**Suppression:** Set `OPTION_SUPPRESS_VENDOR_EXTENSIONS` to `true` to omit all `x-*` properties from the output. This is useful when generating schemas for APIs or validators that reject vendor extensions. Note that round-trip fidelity is reduced: abstract/interface flags and containment mode will be lost.

```java
converter.convert(ePackage, out, "$defs", true, Map.of(
    CodecJsonSchemaOptions.OPTION_SUPPRESS_VENDOR_EXTENSIONS, Boolean.TRUE
));
```

---

## Class Name Capitalization

JSON Schema `title` values are treated as Java class names: the first character is upper-cased to form the `EClass.name`. To preserve the exact original value for round-trips, the reader stores the raw title in a `JSONSCHEMA` annotation with key `originalTitle`. The writer reads this annotation (falling back to `EXTENDED_METADATA "name"`, then `JSONSCHEMA "originalName"`, then `EClass.getName()`) when emitting the `title` field.

---

## Enum Descriptions

JSON Schema `enum` only supports a flat list of string values — there is no per-value description mechanism. To work around this, the converter combines enum-level and per-literal GenModel documentation into a single `description` string:

```
"<enum-description>. <LITERAL1>=<doc1>, <LITERAL2>=<doc2>"
```

**Rules:**
- If the `EEnum` has GenModel documentation **and** at least one `EEnumLiteral` has GenModel documentation, both are combined with `. ` as separator
- If only literals have documentation (no enum-level doc), the description contains only the literal descriptions
- If only the enum has documentation (no literal docs), the description is just the enum-level text
- Literals without documentation are omitted from the combined string

**Example EMF model:**
```
EEnum "Color" @GenModel(documentation="Available colors")
  ├── RED    @GenModel(documentation="Primary red")
  ├── GREEN  @GenModel(documentation="Primary green")
  └── BLUE   @GenModel(documentation="Primary blue")
```

**Generated JSON Schema:**
```json
{
  "description": "Available colors. RED=Primary red, GREEN=Primary green, BLUE=Primary blue",
  "enum": ["RED", "GREEN", "BLUE"],
  "type": "string"
}
```

---

## `required` Array

A property is included in the `required` array if:

- The structural feature has `lowerBound >= 1` (i.e., `feature.isRequired()`), **or**
- The `OPTION_ALL_FIELDS_REQUIRED` converter option is set to `true`.

---

## Custom Properties Integration

JSON Schema options flow through the codec's generic `customProperties` mechanism introduced in `EffectiveCodecConfig`. When a user passes load/save options to `CodecResource`, any `codec.*` key that is **not** a known `ConfigProperty` and **not** a known runtime option is automatically collected into `customProperties`. Format-specific options use a sub-namespace:

```java
// Via CodecResource save options — options flow automatically
resource.save(outputStream, Map.of(
    "codec.jsonschema.allFieldsRequired", true,
    "codec.jsonschema.flatAllOf", true,
    "codec.jsonschema.suppressKeywords", Set.of("maxItems", "description")
));
```

Value writers access these via `ctx.getConfig().getCustomProperties()`, which returns the full map. The converters receive the same map and look up their options by the full `codec.jsonschema.*` key.

---

## Keyword Suppression

The `OPTION_SUPPRESS_KEYWORDS` option allows suppressing specific JSON Schema keywords from the generated output. This is useful when generating schemas for AI structured-output APIs or other consumers that do not support certain keywords.

**Suppressible keywords:**

| Keyword | Where it appears |
|---------|-----------------|
| `description` | EClass, EAttribute, EReference |
| `$comment` | EClass, EStructuralFeature |
| `deprecated` | EClass, EStructuralFeature |
| `additionalProperties` | EClass definitions |
| `minItems` | Multi-valued attributes/references |
| `maxItems` | Multi-valued attributes/references |
| `writeOnly` | EAttribute, EReference |
| `uniqueItems` | EAttribute, EReference |
| `format` | EAttribute |

**Example:**

```java
Map<String, Object> options = Map.of(
    CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS,
    Set.of("maxItems", "minItems", "description", "additionalProperties")
);
converter.convert(ePackage, out, "$defs", true, options);
```

Structural keywords (`type`, `properties`, `required`, `items`, `$ref`, `allOf`, `oneOf`) are not suppressible — they define the schema's shape.

---

## Array Items

Multi-valued EMF features (`isMany() == true`) always produce a JSON Schema `array` type with an `items` property declaring the element type:

```json
{
  "type": "array",
  "items": { "type": "string" },
  "minItems": 1
}
```

The element type is derived from:
- **EAttribute**: the EDataType is mapped to a JSON Schema type (`string`, `integer`, `number`, `boolean`)
- **EEnum**: `"type": "string"` with `"enum"` constraint listing all literals
- **EReference (containment)**: a `$ref` to the referenced EClass definition, with `"x-containment": true`
- **EReference (non-containment)**: a `$ref` to the referenced EClass definition

---

## Diagnostics

Unresolvable `$ref` links, unsupported `allOf`/`anyOf` constructs, or missing type information produce `JsonSchemaConversionDiagnostic` entries. In standalone mode these are surfaced as `Resource.getWarnings()`. In programmatic use retrieve them via `JsonSchemaToEPackageConverter.getDiagnostics()`.

---

## Resolved Issues

### RI-1: `JsonSchemaResourceImpl` now extends `CodecResource`

`JsonSchemaResourceImpl` now extends `CodecResource` instead of `ResourceImpl`. This gives it access to the codec configuration pipeline, including `customProperties` for format-specific options. The resource creates its own `ConfigurationResolver` (with `typeInclude(false)`) and `CodecValueRegistry` (with JSON Schema value readers/writers pre-registered).

**Caveat:** Because JSON Schema conversion is a **meta-format** operation (converting between metamodels, not serializing EObject instances), not all `CodecResource` features are utilized. In particular:

- **Codec annotations are not fully consulted.** `FeatureCodecAspect` settings such as `effectiveKey` (property rename), `ignore` / `ignoreRead` / `ignoreWrite`, and `enumSerialization` are not applied during schema generation or parsing. The produced schema reflects the raw Ecore model, not what the codec actually serializes at runtime.
- If a class uses `effectiveKey = "firstName"` for an attribute named `name`, the generated JSON Schema will still use `"name"` as the property key, not `"firstName"`.

**Future:** A deeper integration layer could use the `MetadataService` already available on the resource to resolve effective property names and ignore-flags before emitting schema properties.

### RI-2: OSGi DS support for `MetadataService`

`JsonSchemaResourceFactoryImpl` is now a Declarative Services `@Component` that receives a `MetadataService` via `@Reference` injection. In a non-OSGi environment, the no-arg constructor uses `MetadataServiceFactory.create()` to obtain a `MetadataWhiteboard` instance.

### RI-3: `$schema` declaration via options

The `$schema` field is now resolved with the following fallback chain:

1. `JSONSCHEMA "schema"` EAnnotation on the EClass (single-class mode) or EPackage
2. `OPTION_SCHEMA_DRAFT` (`codec.jsonschema.draft`) from converter options / custom properties
3. If none is set, `$schema` is omitted

The `OPTION_SCHEMA_DRAFT` value can be either a draft shorthand (`"draft-04"`, `"draft-06"`, `"draft-07"`, `"2019-09"`, `"2020-12"`) which is mapped to the canonical `$schema` URI, or a full URI string.

```java
resource.save(outputStream, Map.of(
    CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, "2020-12"
));
// → "$schema": "https://json-schema.org/draft/2020-12/schema"
```
