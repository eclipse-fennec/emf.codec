# 21 - Schemas and Codec Aspects

> **Status: draft** for #277 (reading) and #271 (writing). Nothing in this chapter is implemented
> yet; the open questions in §6 are to be decided before the implementation starts.

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
| no id concept | no `eID` attribute | none — with no id attribute no `_id` is written |

The aspects are set only where the default differs; a generated EPackage carries no annotation that
repeats a default.

## 4. Writing: codec aspect → schema

The writer resolves the configuration per class and feature (`ConfigurationResolver`) with the
save options of the schema resource and describes what the codec writes:

| Resolved configuration | Schema |
|---|---|
| type info written (`typeStrategy` other than `NONE`) | optional property `typeKey` (`_type`), `type: string` |
| id written (`idKeyMode` `ID_ONLY` / `BOTH`) | property `idKey` (`_id`); under `ID_ONLY` the id attribute itself is not a property |
| `superTypeSerialize=true` | optional property `superTypeKey`, array or string per `superTypeAsArray` |
| containment reference | `$ref` to the target schema |
| non-containment reference, `STRUCTURED` | object with `refKey` (`_ref`) and `refTypeKey` (`_type`) |
| non-containment reference, `PLAIN` | `type: string` |
| feature `key` | property name |
| ignored feature / `ignoreWrite` | not a property |

A class read from a foreign schema (§3) resolves to `typeStrategy=NONE`, no id, containment
references and its original keys, so writing it reproduces the foreign schema (§2.1).

## 5. Vendor extensions

`x-containment`, `x-abstract` and `x-interface` stay as they are: the writer emits them unless
`codec.jsonschema.suppressVendorExtensions=true`, the reader honours them. They are what lets a
codec-written schema round-trip an Ecore that a foreign schema could not express.

## 6. Open questions

1. **Nullable values.** A property that is `nullable` / `type: [x, "null"]` and is `null` in the
   payload is read as an unset or null value; the codec does not write it again
   (`serializeNull=false`), which breaks §2.2. Set `serializeNull=true` on such features, or keep
   the payload difference?
2. **`additionalProperties`.** `true` or a schema allows members the class does not declare. Today
   they are dropped with a warning. Map them to an `EMap` feature with a catch-all reader / writer,
   or keep dropping?
3. **`oneOf` / `anyOf` without `discriminator`.** The codec cannot pick the subclass from the
   payload. Leave the reference typed with the abstract base and fail on read (`fallbackStrategy`),
   or infer the subclass from the members present?
4. **Discriminator property.** OpenAPI declares the discriminator property in the subschemas as an
   ordinary property. Does the codec keep reading it into its feature next to using it for the type
   decision, and write it from the feature?
5. **Enum values** that are not valid Ecore literal names (`in-progress`, `1`): kept as the literal
   (`EEnumLiteral.getLiteral()`), which the codec writes — to be verified.
6. **`format` values** (`date-time`, `date`, `byte`, …): which data types keep the exact string
   (fraction digits, offset) through the payload round trip.
7. **Ids from the schema.** A foreign API often has an `id` property. It stays an ordinary
   attribute (no `eID`), so references between payloads are not resolved — or is an opt-in to mark
   it as id wanted?

## 7. Test plan

- Petstore (OpenAPI 3.0) and one larger public OpenAPI document as test data, each with example
  payloads from the document's `examples`.
- Both acceptance checks of §2 per schema and payload.
- A codec-written schema (EPackage → schema → EPackage) keeps classes, references, containment
  and supertypes, as today.
