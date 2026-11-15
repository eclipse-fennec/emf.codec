# 22 - JSON Schema → Ecore

[← Schemas and Codec Aspects](21-schema-codec-aspects.md) | [Overview](00-overview.md)

---

> **Status:** draft (#318). §§3-8 describe what `JsonSchemaToEPackageConverter` does today and are
> covered by its tests; §9 lists what is not mapped yet. Spec [21](21-schema-codec-aspects.md) holds
> the codec aspects the mapping sets; this document holds the structure.

## 1. Principle

A schema is read into the **JSON Schema model** (`org.eclipse.fennec.codec.jsonschema.model`,
`SchemaJson.read`), which keeps every keyword of draft-04 to 2020-12 and the OpenAPI vocabulary
without loss (#317). The **Ecore** is derived from the model:

- The model is the source. Whatever Ecore cannot express stays in the model - it is never lost,
  only not modelled. Some of it is also copied into annotations so that an EPackage alone can be
  written back as a schema (§7).
- Each schema of the model that the mapping turns into a classifier, or into the type of a
  feature, links to it: `Schema.classifier` (transient). A classifier set before is kept.
- Ecore is the only target of the mapping. Other formats are produced from the model, not from
  the Ecore.

## 2. Entry points

| Call | Input | Result |
|---|---|---|
| `convert(Schema root, String schemaFeature)` | a schema document: definitions under `schemaFeature` (`definitions`, `$defs`, `null` to detect them), top-level `properties` | EPackage named after `title`, nsURI from `$id`, a root class for top-level `properties` (§3.2) |
| `convertDefinitions(Map<String, Schema>)` | named schemas, e.g. the `components/schemas` of an OpenAPI description | EPackage with a classifier per name. The names are taken as they are: a schema named `schemas` or `properties` is a schema, not a section |
| `convert(JsonNode, String)`, `convert(InputStream, String)` | JSON text | same as `convert(Schema, ...)` without the link of §1 |
| `convertFromSchemaMap(EMap)` | a map of model schemas: as `convertDefinitions`, package `schemas`; any other EObject: the old reflective path | |

`$ref`s resolve against the definitions: `#/<schemaFeature>/<Name>`, `#/components/schemas/<Name>`,
`#/definitions/<Name>`, `#/$defs/<Name>`, and `#<anchor>` for an `$anchor`. A `$ref` into another
document is not resolved (§8).

## 3. Definitions

A definition becomes one classifier, named after its key (first letter upper case; the key is kept
as `originalName` and `ExtendedMetaData` `name` when it differs). The first matching rule decides:

| # | Schema | Classifier |
|---|---|---|
| 1 | `anyOf`/`oneOf` of one schema and `{type: null}`, only annotations around it | the mapping of that schema (nullable, §6) |
| 2 | `anyOf` only (no `oneOf`, `type`, `properties`) | as `oneOf`, annotation `unionKeyword=anyOf` |
| 3 | `oneOf` of `$ref`s only | abstract `EClass`; the referenced classes become its subclasses |
| 4 | `oneOf` with `minProperties: 1`, `maxProperties: 1` | abstract `EClass`, one subclass per option, named `<Key><Name>` after the option's single required key |
| 5 | other `oneOf` | abstract base, one variant class per option (context-specific variants) |
| 6 | `enum` | `EEnum`: literal = the value as written, name = a valid Ecore name derived from it |
| 7 | `allOf` | `EClass`: `$ref` entries are supertypes, the properties of all inline entries are merged, `required` accumulated |
| 8 | `type: object` | `EClass` (§3.1) |
| 9 | `type: array` | array wrapper `EClass` with one multi-valued feature `items` (annotation `arrayWrapper`); an array of arrays nests a wrapper |
| 10 | `type: <scalar>` or a list of types | `EDataType` (§5), facets as annotations |
| 11 | no `type`, but `properties`, `additionalProperties`, `patternProperties`, `required`, or `if` with `then`/`else` | `EClass` |
| 12 | no constraint on the type at all (`{}`, only annotations) | `EDataType` of `java.lang.Object` |
| 13 | only a `$ref`, annotations around it at most (an alias) | no classifier of its own: another name of the target's classifier, also through a chain of aliases. A `$ref` to the alias, `allOf` of it and its `Schema.classifier` are the target's. A cycle or a missing target stays unresolved |
| 14 | `not`, an `if` without `then`/`else` | no classifier |

Definitions with nothing but object members (`configs: {kafka: {...}}`) are namespaces: their
children are definitions with the qualified name `configs/kafka`.

### 3.1 Classes

- `properties` → one feature per property (§4), in document order.
- `required` → `lowerBound=1` for single-valued features; a multi-valued feature takes its lower
  bound from `minItems`.
- `additionalProperties: true` or a schema, next to `properties` → catch-all map (spec 21 §6.2).
- A schema that is only a map - `additionalProperties` without `properties`, as a definition or used
  directly as a request or response body - gets the same catch-all map (#322): a map of an entry class
  for a `$ref` value, otherwise the raw JSON of each entry. (A property of that shape becomes a map
  feature instead, §4.)
- `patternProperties` → catch-all map as well, also without `properties` and next to
  `additionalProperties: false`: the members matching a pattern are kept. The map holds objects of
  a class when all patterns (and `additionalProperties`) share one `$ref` schema, otherwise the raw
  JSON of each member. The patterns are annotated (`patternProperties`) and written back; they are
  not checked when reading.
- `if` / `then` / `else` → the properties of `then` and `else` the class does not declare become
  optional features of it (annotation `conditional=then|else`), so that their members are read
  whichever branch applies. The condition is not checked; `required` of a branch is not applied.
  The three keywords are annotated and written back as they were; the conditional features are not
  written as properties.
- `additionalProperties` without `properties` on a property → map feature (§4).
- `discriminator` → codec aspects (spec 21 §3, spec 08).
- `x-abstract`, `x-interface` → the flags (spec 21 §5).

### 3.2 Root class

Top-level `properties` of a document become a class named after the package (`Root` without a
title), annotation `rootClass=true`.

## 4. Properties

| Property schema | Feature |
|---|---|
| `$ref` to a class | containment `EReference` (`x-containment: false`, `x-ref`: non-containment) |
| `$ref` to an enum or data type | `EAttribute` of that type |
| `type: string` with `enum` | `EAttribute` of an `EEnum` named `<Class><Property>`, annotation `inline` |
| `type: <scalar>` | `EAttribute` (§5) |
| `type: array` | the feature of `items`, `upperBound` from `maxItems` (`-1` without), `lowerBound` from `minItems` |
| `type: array` with `prefixItems`, or `items` as a list (a tuple) | multi-valued, non-unique `EAttribute` `EJavaObject`: the values as they are (native). The property schema is annotated (`schema`) and written back as it was |
| `type: object` with `additionalProperties` (a schema), no `properties` | multi-valued containment of a `<Property>MapEntry` class (`key: EString`, `value`: the `$ref` class or a value class) |
| `type: object` otherwise | containment `EReference` to an artificial class |
| `type: [x, "null"]` | the feature of `x`, nullable (§6) |
| `type: [x, y, ...]` | containment of an abstract artificial class with one variant class per type (`value` attribute) |
| `oneOf` of `$ref`s with a common abstract supertype | containment `EReference` of that supertype |
| `oneOf` otherwise | containment of an artificial union class (§3 rules 4, 5) |
| `anyOf` with a `$ref` branch | containment `EReference`, typed after the supertypes are resolved |
| `anyOf` without `$ref` | `EAttribute` `EJavaObject`, the alternatives as annotation |
| `const` | the feature of the const's type, annotation `const` |
| no type information | `EAttribute` `EJavaObject`, annotation `noTypeInfo` |

A reference whose target cannot be resolved gets `EObject` (attributes `EJavaObject`), annotation
`unresolvedType`, and a diagnostic.

## 5. Scalars

| `type` | `format` | Data type |
|---|---|---|
| `integer` | `int32` | `EIntegerObject` |
| `integer` | other, none | `ELongObject` |
| `number` | `float` | `EFloatObject` |
| `number` | other, none | `EDoubleObject` |
| `boolean` | | `EBooleanObject` |
| `string` | any | `EString`, the format as annotation |
| other, none | | `EJavaObject` |

Wrapper types, never primitives (spec 21 §3). `JsonSchemaToEPackageConverter.dataType(type, format)`
is this table; the OpenAPI use sites (`OasSchemas`) use it as well.

## 6. Nullable

`nullable: true` (OpenAPI 3.0), `type: [x, "null"]` and `anyOf`/`oneOf` of one schema and
`{type: null}` mean the same: the feature of the non-null schema, `unsettable=true` and
`serializeNull=true` (spec 21 §6.1). A definition or a `oneOf` option of a nullable type is mapped as
that type. Nullability belongs to the feature, not to the classifier: schemas that differ only in it
share one classifier.

## 7. Annotations

Source `http://fennec.eclipse.org/jsonschema` unless stated otherwise:

- `description` → GenModel `documentation`; `deprecated: true` → GenModel `deprecated`.
- `default`, `minItems`, `maxItems`, `uniqueItems`, `minLength`, `maxLength`, `pattern`, `format`,
  `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`, `const`, `title`,
  `example`, `examples`, `contentEncoding`, `contentMediaType`, `readOnly`, `writeOnly` → a detail
  of the same name (the JSON text).
- `$comment` → `comment`; `$anchor` → `anchor` (and a name `$ref`s resolve).
- `$schema` → `schema` on the package; `title` → `originalTitle` on the package.

They let an EPackage be written back as a schema without the model (spec 21 §4). With the model
at hand, the model is the authority.

## 8. Not in the Ecore

These keywords have no Ecore counterpart. They stay in the model; the converter reports them as a
diagnostic (`JsonSchemaKeywords`):

- partially: `patternProperties` (§3.1: members kept, patterns not checked), `unevaluatedProperties`;
- partially: `if`/`then`/`else` (§3.1: branch properties as optional features, condition not
  checked), `prefixItems` (§4: values kept, positions not typed);
- not at all: `not`, `contains`/`minContains`/`maxContains`,
  `propertyNames`, `dependentRequired`, `dependentSchemas`, `$dynamicRef`/`$dynamicAnchor`,
  `unevaluatedItems`, `contentSchema`, `$vocabulary`;
- not mapped and not reported: `$recursiveRef`/`$recursiveAnchor`, `dependencies`,
  `additionalItems`, `$ref`s into other documents.

Validation keywords (`pattern`, `minimum`, ...) are annotations (§7); they are not constraints of
the Ecore. Validating a payload against them is a matter of the schema, not of the mapping.

## 9. Open

1. A top-level tuple definition (`type: array` with `prefixItems`) is an array wrapper of its
   `items`; the positions are not kept in the Ecore.
2. Whether the mapping fits a model transformation (m2x/QVT) - no m2x dependency in the codec until
   then.
