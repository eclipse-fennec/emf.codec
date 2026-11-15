# OpenAPI Codec

`org.eclipse.fennec.codec.openapi` reads and writes [OpenAPI 3](https://spec.openapis.org/oas/v3.0.3)
documents in JSON and YAML as EMF models of `org.eclipse.fennec.openapi.model` (nsURI
`https://spec.openapis.org/oas/3.0.3/schema`). The data types of an API, `components/schemas`, are
not mapped to the model's `Schema` class but to a plain `EPackage`: an OpenAPI document can be
generated from the EPackages a service already has, and a loaded document hands its schemas back
as EClasses.

## Setup

**OSGi.** Require the codec with `@RequireCodecOpenApi`. Its DS component registers a
`Resource.Factory` once the OpenAPI model, a `MetadataService` and a `CodecValueRegistry` are
present, with these service properties:

| Property | Value |
|---|---|
| `emf.resource.name` | `openapi` |
| `emf.contentType` | `application/vnd.oai.openapi+json` |
| `emf.fileExtension` | `openapi` |
| `emf.configuratorName` | `FennecCodecOpenApi` |

```java
@Reference(target = "(emf.contentType=application/vnd.oai.openapi+json)")
Resource.Factory openApiFactory;

Resource resource = resourceSet.createResource(uri, "application/vnd.oai.openapi+json");
```

The content type is what the REST message body writer selects a factory by: a JAX-RS resource that
returns an `OpenAPI` object with `@Produces("application/vnd.oai.openapi+json")` is written by this
codec.

**YAML.** A second component registers the factory for the YAML media type
`application/vnd.oai.openapi` (`emf.resource.name=openapi-yaml`), **by content type only**: the file
extensions `yaml` / `yml` belong to the generic YAML factory. It needs the YAML syntax from
`org.eclipse.fennec.codec.yaml`, which registers its `YamlFormatProvider` as a `CodecFormatProvider`
service with `codec.format.id=yaml`; without that bundle the component stays unsatisfied and the
codec offers JSON only. The model and the mapping are the same as for JSON.

```java
Resource resource = resourceSet.createResource(uri, "application/vnd.oai.openapi");
```

**Plain Java.** The no-arg factory brings its own metadata service and the value handlers it needs:

```java
Resource resource = new OpenApiResourceFactoryImpl().createResource(URI.createFileURI(path));
resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API));
OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
```

For YAML outside OSGi, hand the format provider and the media type to the factory:

```java
new OpenApiResourceFactoryImpl(new YamlFormatProvider(), OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_YAML);
```

Pass the root type when loading: the document carries no type information. To wire an
`OpenApiResourceImpl` without the factory, fill the `CodecValueRegistry` with
`OpenApiResourceFactoryImpl.registerDefaultValueHandlers(registry)`.

## Writing a document

```java
OpenAPI openApi = OpenApiFactory.eINSTANCE.createOpenAPI();
openApi.setOpenapi("3.0.3");
Info info = OpenApiFactory.eINSTANCE.createInfo();
info.setTitle("Person API");
info.setVersion("1.0.0");
openApi.setInfo(info);

Components components = OpenApiFactory.eINSTANCE.createComponents();
components.setSchemasPackage(EcoreUtil.copy(PersonPackage.eINSTANCE));
openApi.setComponents(components);

resource.getContents().add(openApi);
resource.save(out, null);
```

```json
{
  "openapi": "3.0.3",
  "info": { "title": "Person API", "version": "1.0.0" },
  "paths": {},
  "components": {
    "schemas": {
      "Person": {
        "type": "object",
        "properties": {
          "_type": { "type": "string", "x-codec": "type" },
          "address": { "$ref": "#/components/schemas/Address", "x-containment": true },
          "friend": {
            "type": "object",
            "properties": { "_ref": { "type": "string" }, "_type": { "type": "string" } },
            "required": ["_ref"],
            "x-containment": false,
            "x-ref": "#/components/schemas/Person"
          },
          "firstName": { "type": "string" }
        }
      },
      "Address": {
        "type": "object",
        "properties": { "_type": { "type": "string", "x-codec": "type" }, "city": { "type": "string" } },
        "required": ["city"]
      },
      "Employee": {
        "allOf": [
          { "$ref": "#/components/schemas/Person" },
          { "type": "object", "properties": { "_type": { "type": "string", "x-codec": "type" } } }
        ]
      }
    }
  }
}
```

The schemas describe the JSON the codec writes for instances of the classes, under the codec
configuration of the model and the save options ([spec 21](/guides/21-schema-codec-aspects) §4):
`_type` because the default type strategy writes it, a non-containment reference as the
`{_ref, _type}` object the codec writes for it, an id attribute under `_id` (`ID_ONLY`). A model
whose classes say `typeStrategy=NONE` gets no `_type`, one with `refFormat=PLAIN` a string
reference.

`paths` is always written, as `{}` when there are no entries: OpenAPI 3.0 requires the member
(#276).

## `components/schemas`

| Model | JSON | Note |
|---|---|---|
| `Components.schemasPackage` (`EPackage`) | `components/schemas` | read and written by the JSON Schema converter of `org.eclipse.fennec.codec.jsonschema` |
| `Components.schemas` (`SchemaEntry` map) | - | ignored by the codec, neither read nor written; kept for compatibility and to be removed |

**`schemasPackage` is a containment.** Setting a registered EPackage moves it out of its resource
and out of the package registry's reach. Always set a copy (`EcoreUtil.copy(...)`).

**One package.** `schemasPackage` holds a single EPackage, and only its own classifiers are
written; sub-packages are not. To describe types from several EPackages, copy their EClasses into
one package for the document.

**Entry names.** Each classifier becomes one entry under `components/schemas`, named after the
`originalName` detail of the `http://eclipse.org/fennec/codec/jsonschema` annotation, otherwise the
classifier name. ExtendedMetaData names are used only with
`codec.jsonschema.useNamesFromExtendedMetadata=true`. Every reference and every supertype is a
`$ref` to `#/components/schemas/<name>`.

**Nullability.** `null` is part of a schema only where the codec writes it: for a feature with
`serializeNull=true`. OpenAPI 3.0 has no `"null"` type, so such an attribute is a single `type`
with `nullable: true`, and a multi-type `dataType` annotation becomes `anyOf`. For a document with
`openapi` 3.1 or later the Schema Object is JSON Schema 2020-12 and keeps the type array
(`"type": ["string", "null"]`). The codec decides by the `openapi` value of the document; the
option `codec.jsonschema.nullableKeyword` set by the caller wins.

**`additionalProperties`** is written as the source schema had it, `true` (or a `$ref`) for a class
with a catch-all map, and not at all otherwise.

**Vendor extensions.** `x-containment` marks containment, `x-codec` the members the codec writes for
the object itself (`type`, `superType`, `id`), `x-ref` the target of a non-containment reference.
They let a codec-written schema be read back into the same model;
`codec.jsonschema.suppressVendorExtensions=true` leaves them out.

The other JSON Schema options (`codec.jsonschema.*`, see the
[options reference](/guides/codec-options-reference)) apply to `components/schemas` as well, passed
as save options.

**Reading.** A loaded document's `components/schemas` is converted to a new EPackage in
`schemasPackage`, together with the codec configuration under which the codec reads and writes the
API's payloads as the schemas describe them ([spec 21](/guides/21-schema-codec-aspects)):

- no `_type` or `_id` is written; a `$ref` is an inlined (containment) object, `allOf` a supertype
- a `discriminator` picks the subclass; its property stays an ordinary attribute
- numbers, booleans and enums keep `0`, `false`, large `int64` values and literals like
  `sold-out`; an explicit `null` of a `nullable` property is written again

Register the package with the metadata service and load a payload with the root type option.
Saving the document again writes the refs as they were read.

## YAML

YAML is read with the YAML 1.2 core schema, as the OpenAPI specification recommends:

- **Keys are strings**: `200:` is the response code `"200"`, like `2XX` and `default`.
- **`on`, `off`, `yes`, `no`, `y`, `n` stay strings** - also as enum values and keys. Only where
  the model expects a boolean (`required: yes`, `deprecated: off`) they read as `true` / `false`,
  with a warning; `codec.strictOnConversion` refuses them (issue #297).
- **Anchors and aliases** (`&a` / `*a`) are resolved, for objects, sequences and scalars. The
  expansion is bounded by `codec.yaml.maxAliasTokens` (default 1,000,000 tokens) against
  "billion laughs". Merge keys (`<<: *a`) are not resolved.
- Block scalars (`|`, `>`) are read as text; comments are dropped.
- Lines of only spaces and tabs are emptied before parsing. This works around a snakeyaml-engine
  bug (3.0.1 to 3.2) that refuses a block scalar whose only line is blank but deeper indented than
  the next line, as in the OpenAI description; the value reads as the line break it means
  (issue #298). Spaces on blank lines inside a literal block are therefore not kept.
- **Size**: `codec.maxPayloadSize` (default 16 MiB) bounds a YAML document as well, so the large
  published descriptions (GitHub ~11 MB) load with the defaults.

Writing YAML quotes a string only where YAML would read it as something else (`"200"`, `"on"`,
`"1.0"`) and writes multi-line descriptions as literal blocks. These are the save options
`codec.yaml.minimizeQuotes` and `codec.yaml.literalBlockStyle`, which the OpenAPI resource turns
on unless the caller sets them.

## Schema package identity

The `EPackage` built from `components/schemas` gets an nsURI, a name and a prefix (issue #311), so
that its classes can be referred to from outside the description (e.g. a service contract saved as
XMI):

| | Value | Example (`Swagger Petstore`, `1.0.0`) |
|---|---|---|
| nsURI | `urn:openapi:<title>:<version>`, both reduced to `[a-z0-9.-]` | `urn:openapi:swagger-petstore:1.0.0` |
| name, nsPrefix | lower camel case of the title words | `swaggerPetstore` |

The nsURI does not depend on where the document was loaded from: the same API read from its URL,
a mirror or a local copy, as JSON or YAML, gets the same nsURI. The load option
`codec.openapi.schemasNsURI` (`CodecOpenApiOptions.OPTION_SCHEMAS_NS_URI`) sets it instead.

## References

A `$ref` stays a string in the model; it is what is written back. A reference into the same
document leads to its target (issue #312):

- **Schemas**: loading links every `Schema` with `$ref: #/components/schemas/X` - nested ones such
  as `items` included - to the classifier built for `X`: `Schema.getClassifier()`. The link is
  transient, never written. A schema named `pet` gives the class `Pet`; the link finds it.
- **Other sections** (`parameters`, `responses`, `requestBodies`, `headers`, `examples`, `links`,
  `callbacks`, `securitySchemes`): `OpenApiReferences.resolve(openApi, ref)` returns the object in
  that map of `Components`.

```java
EClassifier type = response.getContent().get("application/json").getSchema().getClassifier();
Parameter limit = (Parameter) OpenApiReferences.resolve(openApi, parameter.getRef());
```

JSON pointers are unescaped (`~1`, `~0`, percent-encoding). An internal reference without target
is a warning on the resource; references into other documents are neither loaded nor reported.

## The rest of the document

Everything outside `components/schemas` maps to the model classes one to one: `Info`, `Server`,
`PathItem`, `Operation`, `Parameter`, `RequestBody`, `Response`, `MediaType`, `SecurityScheme`,
`Tag`, … A Schema Object in a parameter, request body or response is a model `Schema`, not an
EClass; it refers to the generated types with `ref` (`"$ref": "#/components/schemas/Person"`).

| OpenAPI JSON | Model | Note |
|---|---|---|
| `paths./pets.get`, `.post`, … (all eight methods) | `PathItem.getGet()`, … | `Operation.getMethod()` is set from the member name on reading, never written |
| `security`, a requirement object `{ "oauth2": ["read"] }` | `SecurityRequirement.getSchemes()` | scheme name → scopes |
| `$ref` of a parameter, response, request body, header, path item, schema | `getRef()` | kept as a string; internal ones lead to their target, see [References](#references) |
| `in`, `style`, `type` of a security scheme | enums with a `NULL` literal 0 | an absent value stays unset; an explicit value is always written |
| `explode` | `Boolean` | only written when set |
| `x-*` extensions | - | dropped when reading |

## OpenAPI 3.0, 3.1 and 3.2: the oas model

A second model, `org.eclipse.fennec.model.oas` (nsURI `https://eclipse.org/fennec/openapi/3`), covers
OpenAPI 3.0, 3.1 and 3.2 in one package (issues #299-#313). Every feature notes the version it
appears in (annotation `http://eclipse.org/fennec/openapi`, key `since`). The 3.0 model above stays
the one registered for 3.0 until emf.services has moved to the new model; then the old model is
deprecated and removed.

**Resources.** One resource and factory per version and syntax, `OasResourceFactoryImpl(version,
formatProvider)`. In OSGi, only 3.1 and 3.2 are registered:

| Content type | Factory component |
|---|---|
| `application/vnd.oai.openapi+json;version=3.1` | `OasResourceFactory.3.1.json` |
| `application/vnd.oai.openapi;version=3.1` (YAML) | `OasResourceFactory.3.1.yaml` |
| `application/vnd.oai.openapi+json;version=3.2` | `OasResourceFactory.3.2.json` |
| `application/vnd.oai.openapi;version=3.2` (YAML) | `OasResourceFactory.3.2.yaml` |

```java
Resource resource = new OasResourceFactoryImpl(OasVersion.V3_1, new YamlFormatProvider())
        .createResource(URI.createURI("openai.yaml"));
resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
```

A document of another covered version is read with a warning; a version the model does not cover
(`2.0`, `3.3`) is refused.

**Reading** completes the description in the model - no helper is needed:

- `Operation.getMethod()` - `GET`, …, `QUERY`, or the key in `additionalOperations`;
  `PathItem.getAllOperations()` lists all operations of a path.
- Reference Objects keep `ref` and get `target` (the object in `components`); their own
  `summary` / `description` are the 3.1+ overrides. `Tag.getParentTag()` follows `parent`.
- **Schemas** are kept as written, in the JSON Schema model (`org.eclipse.fennec.codec.jsonschema.model`,
  draft-04 to 2020-12 plus the OpenAPI vocabulary): `Components.getSchemas()` maps the names to
  `Schema`s, a use site (`Parameter`, `Header`, `MediaType.schema` / `itemSchema`) holds a `Schema`.
  The Ecore is derived from them when loading - never written:
  - `Components.getSchemasPackage()`: a classifier per named schema; its sub-package `inline` holds
    the classifiers of inline schemas, named after their place (`ListPets200ApplicationJson`);
  - `Schema.getClassifier()` at a use site: the classifier of a `$ref`, of an inline schema, or an
    Ecore data type for a simple type (`EString`, `EIntegerObject`, `ELongObject` for `int64`, …);
    for an array the item type.
- Specification Extensions (`x-…`) are kept in `extensions`, as native JSON values.

**Writing** writes the description as it was read: the schemas from the JSON Schema model, the
rest from the model objects - explicit defaults (`required: false`), explicit `null`s and empty
lists (`parameters: []`) included. A description read and written back gives the same JSON tree;
`OasLosslessTest` checks this for all examples and the large descriptions. Booleans of the model
are `Boolean` objects, unset when the document does not name them.

The OpenAI (3.1) and GitHub (3.1 and 3.0.3) descriptions load into the oas model without errors or
warnings, every response schema typed (`OasLargeDescriptionsTest`), and are written back unchanged
(`OasLosslessTest`).

## Tests with large descriptions

The OpenAI (4.3 MB YAML) and GitHub (11 MB YAML, 14 MB JSON) descriptions are not checked in.
`./gradlew :org.eclipse.fennec.codec.openapi:fetchOpenApiCorpus` downloads them, pinned to a commit,
into `test-data/external` (git-ignored); `OpenApiLargeDescriptionsTest` is skipped while they are
missing, so the regular build stays offline.

## Known gaps

- YAML merge keys (`<<: *a`) are not resolved.
- `$ref`s to other documents are kept as strings; the codec does not load them.
