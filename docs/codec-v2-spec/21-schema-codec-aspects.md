# 21 - Schemas and Codec Aspects

> **Status:** implemented - reading (§3, #277, #278, #279) and writing (§4, #271). The decisions
> of §6 were taken on 2026-10-16.

## 1. Principle

A JSON Schema (or the `components/schemas` of an OpenAPI document) describes JSON. The codec
configuration on an Ecore (codec annotations, see [16](16-annotation-reference.md), plus the
options of [13](13-load-save-options.md)) decides which JSON the codec reads and writes for that
Ecore. Schema and codec configuration therefore have to describe the same wire format:

- **Reading a schema** (schema → EPackage, `JsonSchemaToEPackageConverter`) produces the Ecore
  structure **and** the codec aspects under which the codec reads and writes exactly the JSON the
  schema describes.
- **Writing a schema** (EPackage → schema, `EPackageToJsonSchemaConverter`) renders the resolved
  codec configuration of each class and feature, not the bare Ecore structure.

The codec configuration on the Ecore is the single source of truth: the reader produces it, the
writer reads it. The aspects are stored as codec annotations (`http://eclipse.org/fennec/codec`) on
the generated classifiers and features, so the EPackage carries them wherever it is used (OSGi,
REST, a saved `.ecore`) and does not depend on resource options.

## 2. Acceptance

For a foreign schema `S` and a payload `P` that is valid against `S`:

1. **Schema round trip:** `S` → EPackage → schema is `S`, apart from formatting, member order and
   the vendor extensions of §5.
2. **Payload round trip:** `P` loaded through the codec and saved again is `P`, apart from
   formatting and member order. Nothing is added (no `_type`, `_id`, `_supertype`), nothing is
   dropped.

## 3. Reading: schema construct → Ecore and codec aspect

| Schema construct | Ecore | Codec aspect |
|---|---|---|
| object schema under `components/schemas` / `definitions` / `$defs` | `EClass` | `typeStrategy=NONE` on the class: no `_type` is written, the type comes from the declared feature type |
| property | `EAttribute` / `EReference` | `key=<property name>` when the Ecore name differs from it (sanitized name) |
| `$ref` as property value or array `items` | **containment** `EReference` | none — the referenced object is inlined in the JSON. `x-containment: false` keeps a non-containment reference (codec-written schemas only) |
| `allOf: [ { $ref: Parent }, { … } ]` | supertype `Parent` | none — `superTypeSerialize` stays off |
| `oneOf` / `anyOf` of `$ref`s with `discriminator` | abstract base, one subclass per entry | `typeDiscriminatorPath=<propertyName>` on the base, one mapping entry per value of `discriminator.mapping` (implicit mapping: the schema name), see [08](08-discriminator-mapping.md) |
| `required` | `lowerBound=1` | none |
| `type: integer` | `EIntegerObject` for `format: int32`, otherwise `ELongObject` | none |
| `type: number` | `EFloatObject` for `format: float`, otherwise `EDoubleObject` | none |
| `type: boolean` | `EBooleanObject` | none |
| `type: string` (any `format`) | `EString`, the `format` kept as annotation | none — the string is kept exactly |
| `enum` | `EEnum`, literal = the value as written, name = a valid Ecore name derived from it | none — the codec writes the literal |
| `nullable: true` / `type: [x, "null"]` | feature as above, `unsettable=true` | `serializeNull=true` (§6.1) |
| `$ref` to an enum or data type schema | `EAttribute` of that type | none |
| `additionalProperties: true` or a schema | catch-all map feature (§6.2) | `additionalPropertiesFeature=<feature>` on the class |
| `discriminator.propertyName` | ordinary attribute in the subclasses (§6.4) | none |
| `id` or any other property | ordinary attribute, no `eID` (§6.5) | none — with no id attribute no `_id` is written |

Scalars are wrapper types, never primitives: with a primitive, a `0` or `false` in the payload
equals the Ecore default, counts as unset and is not written again, which breaks §2.2.

The aspects are set only where the default differs; a generated EPackage carries no annotation that
repeats a default.

## 4. Writing: codec aspect → schema

The writer resolves the configuration per class and feature (`ConfigurationResolver`) - the codec
annotations of the package being written included, also when no metadata service knows it - with
the save options of the schema resource, and describes what the codec writes:

| Resolved configuration | Schema |
|---|---|
| type info written (`typeStrategy` other than `NONE`) | optional property `typeKey` (`_type`), `type: string` (`STRUCTURED`: an object with `typeNameKey`), `x-codec: type` |
| `superTypeSerialize=true` | optional property `superTypeKey`, array or string per `superTypeAsArray`, `x-codec: superType` |
| id written (`idKeyMode` `ID_ONLY` / `BOTH`, the class has an id attribute or `COMBINED` id features) | property `idKey` (`_id`), required when the id attribute is, `x-codec: id`, `x-codec-feature: <id attribute>`; under `ID_ONLY` the id attributes are not properties |
| containment reference | `$ref` to the target schema, `x-containment: true` |
| non-containment reference, `STRUCTURED` | object with `refKey` (`_ref`, required) and `refTypeKey` (`_type`), `x-containment: false`, `x-ref: <target>` |
| non-containment reference, `PLAIN` | `type: string`, `x-containment: false`, `x-ref: <target>` |
| feature `key` | property name, also in `required` |
| ignored feature / `ignoreWrite` / catch-all map | not a property |
| `serializeNull=true` | `null` allowed (`type: [x, "null"]`, OpenAPI 3.0 `nullable: true`); otherwise not |
| catch-all map | `additionalProperties: true`, or `{ "$ref": <value class> }` |
| source `additionalProperties` annotation | written as read; a class with neither says nothing |
| enum | the literals; `default` only as the source schema stated it; an enum the reader derived from an inline property stays inline |
| `oneOf` union read from a schema | `oneOf` of its members in their order, and its `discriminator`; the members name no `allOf` to it |
| type mapping registry (discriminator) | OpenAPI `discriminator`: `propertyName`, `mapping` for values other than the schema name |
| number types | `format: int32` / `float` where the type differs from the reader's defaults (long, double) |

A class read from a foreign schema (§3) resolves to `typeStrategy=NONE`, no id, containment
references and its original keys, so writing it reproduces the foreign schema (§2.1).

## 5. Vendor extensions

The writer emits them unless `codec.jsonschema.suppressVendorExtensions=true`, the reader honours
them. They are what lets a codec-written schema be read back into the same Ecore, which a plain
schema could not express:

| Extension | Meaning | Reader |
|---|---|---|
| `x-abstract`, `x-interface` | abstract class, interface | sets the flag |
| `x-containment` | containment (`true`) or not (`false`) | sets containment |
| `x-codec` | a member the codec writes for the object itself: `type`, `superType`, `id` | codec configuration instead of a feature: type info kept (no `typeStrategy=NONE`), `superTypeSerialize`, the id attribute named by `x-codec-feature` (`iD=true`); a key other than the default becomes `typeKey` / `idKey` |
| `x-ref` | target schema of a non-containment reference | a non-containment reference of that type |

Without them, a codec-written schema reads as a foreign one: `_type` and `_id` become ordinary
attributes and a non-containment reference an object of its own.

## 6. Decisions

1. **Nullable values.** Features of a `nullable` / `type: [x, "null"]` property are unsettable and
   get `serializeNull=true`: an explicit `null` in the payload is read as a set `null` and written
   again. An absent member stays unset and is not written ([11 §1](11-feature.md)).
2. **`additionalProperties`.** `true` or a schema on a class with declared properties maps to a
   catch-all map feature on the class.
   - Class aspect `additionalPropertiesFeature` (option `codec.additionalPropertiesFeature`, see
     [16](16-annotation-reference.md#foreign-schema-payloads)): names the map feature. On read,
     every member the class does not declare goes into that map, in document order; on write, the
     map entries are written as members after the declared features. The map feature itself is
     never a JSON key, and the schema writer does not list it as a property.
   - Value type: `additionalProperties: <schema with $ref>` gives a map of an entry class
     `<Class>AdditionalPropertiesEntry` with a containment `value` of that class; any other schema
     (`true`, `{}`, a scalar schema) gives an `EStringToStringMapEntry` map that keeps the raw JSON
     text of each member (`"red"` with its quotes, `{"max":3}`, `null`).
   - The feature name is `additionalProperties`, numbered when the class declares a property of
     that name.
   - `additionalProperties: false` or absent: no map; an undeclared member is dropped with a
     warning, as before.
3. **`oneOf` / `anyOf` without `discriminator`.** The subclass is inferred from the members present.
   - Class aspect `typeInference=STRUCTURAL` (option `codec.typeInference`, values `NONE`
     (default) and `STRUCTURAL`). The reader sets it on every abstract class with concrete
     subclasses that has no discriminator - the unions of `oneOf` / `anyOf` as well as
     `x-abstract` classes: without type information there is no other way to read them.
   - A concrete subclass (or the declared class itself) is a candidate when every required member
     is present and, for a class without catch-all map, no member is undeclared in it. Exactly one
     candidate: that class. Several: the one declaring the most of the present members; a tie is
     decided by declaration order, with a warning. None: an error, the object is not read.
   - A discriminator, an explicit type key or a root type hint wins over inference.
4. **Discriminator property.** The discriminator property stays an ordinary attribute in the
   subclasses: reading uses its value for the type decision **and** sets the attribute; writing
   writes the attribute. When the attribute is unset, the writer writes the mapped value of the
   object's class, so the payload always carries it. It is never written twice.
5. **Ids.** A schema property named `id` (or any other) is an ordinary attribute. No `eID`, no
   `_id`, references between payloads are not resolved.
6. **Enum and `format`** (verified against the reader): the enum literal is the value as written and
   is what the codec writes; `format` of strings is only annotated, the value stays an exact string.
   Literal *names* are to be sanitized (`in-progress`, `1` are no valid Ecore names); integer and
   number types follow the table of §3 instead of today's `EInt` / `EDouble`.

## 7. Test plan

- Petstore (OpenAPI 3.0) and one larger public OpenAPI document as test data, each with example
  payloads from the document's `examples`.
- Both acceptance checks of §2 per schema and payload.
- Payloads with explicit `null`, `0`, `false`, large `int64` values, undeclared members under
  `additionalProperties`, and `oneOf` without discriminator.
- A codec-written schema (EPackage → schema → EPackage) keeps classes, references, containment
  and supertypes, as today.
