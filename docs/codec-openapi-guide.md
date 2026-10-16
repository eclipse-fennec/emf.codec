# OpenAPI Codec

`org.eclipse.fennec.codec.openapi` reads and writes [OpenAPI 3](https://spec.openapis.org/oas/v3.0.3)
documents in JSON as EMF models of `org.eclipse.fennec.openapi.model` (nsURI
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
codec. The YAML media type `application/vnd.oai.openapi` is not registered; the codec is neither
built nor tested for YAML.

**Plain Java.** The no-arg factory brings its own metadata service and the value handlers it needs:

```java
Resource resource = new OpenApiResourceFactoryImpl().createResource(URI.createFileURI(path));
resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API));
OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
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

## The rest of the document

Everything outside `components/schemas` maps to the model classes one to one: `Info`, `Server`,
`PathItem`, `Operation`, `Parameter`, `RequestBody`, `Response`, `MediaType`, `SecurityScheme`,
`Tag`, … A Schema Object in a parameter, request body or response is a model `Schema`, not an
EClass; it refers to the generated types with `ref` (`"$ref": "#/components/schemas/Person"`).

| OpenAPI JSON | Model | Note |
|---|---|---|
| `paths./pets.get`, `.post`, … (all eight methods) | `PathItem.getGet()`, … | `Operation.getMethod()` is set from the member name on reading, never written |
| `security`, a requirement object `{ "oauth2": ["read"] }` | `SecurityRequirement.getSchemes()` | scheme name → scopes |
| `$ref` of a parameter, response, request body, header, path item, schema | `getRef()` | kept as a string, not resolved |
| `in`, `style`, `type` of a security scheme | enums with a `NULL` literal 0 | an absent value stays unset; an explicit value is always written |
| `explode` | `Boolean` | only written when set |
| `x-*` extensions | - | dropped when reading |

## Known gaps

- YAML documents are not supported.
- `$ref`s to other documents are kept as strings; the codec does not load them.
