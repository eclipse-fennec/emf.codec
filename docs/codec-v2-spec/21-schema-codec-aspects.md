# 21 - Schemas and Codec Aspects

> **Status:** reading (§3) is implemented for all rows except `additionalProperties` (#278) and
> `oneOf` / `anyOf` without discriminator (#279). Writing (§4) is open (#271). The decisions of §6
> were taken on 2026-10-16.

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

Implemented already, because the reader depends on it: enum values are listed as their literals,
`int32` / `float` mark the number types that differ from the reader's defaults, and a
non-containment reference carries `x-containment: false`.

A class read from a foreign schema (§3) resolves to `typeStrategy=NONE`, no id, containment
references and its original keys, so writing it reproduces the foreign schema (§2.1).

## 5. Vendor extensions

`x-containment`, `x-abstract` and `x-interface` stay as they are: the writer emits them unless
`codec.jsonschema.suppressVendorExtensions=true`, the reader honours them. They are what lets a
codec-written schema round-trip an Ecore that a foreign schema could not express.

## 6. Decisions

1. **Nullable values.** Features of a `nullable` / `type: [x, "null"]` property are unsettable and
   get `serializeNull=true`: an explicit `null` in the payload is read as a set `null` and written
   again. An absent member stays unset and is not written ([11 §1](11-feature.md)).
2. **`additionalProperties`.** `true` or a schema maps to a catch-all map feature on the class.
   - New class aspect `additionalPropertiesFeature` (option `codec.additionalPropertiesFeature`,
     a property map per EClass): names the map feature. On read, every member the class does not
     declare goes into that map; on write, the map entries are written as members after the
     declared features. The map feature itself is never a JSON key.
   - Value type: `additionalProperties: <schema with $ref>` gives a containment map of that class;
     `true` or `{}` gives a map of JSON values, kept as the raw JSON text of each member.
   - The feature name is `additionalProperties`, made unique when the class declares a property of
     that name.
   - `additionalProperties: false` or absent: no map; an undeclared member is dropped with a
     warning, as today.
3. **`oneOf` / `anyOf` without `discriminator`.** The subclass is inferred from the members present.
   - New class aspect `typeInference=STRUCTURAL` on the abstract base (option
     `codec.typeInference`, values `NONE` (default) and `STRUCTURAL`).
   - A subclass is a candidate when every required property is present and, for a class without
     catch-all map, no member is undeclared in it. Exactly one candidate: that class. Several: the
     one declaring the most of the present members; a tie is decided by declaration order, with a
     warning. None: `fallbackStrategy` of [08](08-discriminator-mapping.md).
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
