# JSON Schema Validation to EMF/OCL Mapping Guide

This document serves as a reference guide for converting **JSON Schema (Draft 2020-12)** and **OpenAPI 3.x** validation constraints into **EMF (Eclipse Modeling Framework)** structural constraints and **OCL (Object Constraint Language)** invariants.

## 1. Core Structural Keywords (EMF Meta-model Mapping)

Many JSON Schema constraints do not require complex OCL formulas; they map directly to structural properties of your `EClass`, `EAttribute`, or `EReference`.

| **JSON Schema Keyword** | **OpenAPI Equivalent** | **EMF / Ecore Mapping**     | **Notes**                                |
| ----------------------- | ---------------------- | --------------------------- | ---------------------------------------- |
| `"type": "string"`      | `type: string`         | `EAttribute` (eString)      | Core type mapping.                       |
| `"type": "integer"`     | `type: integer`        | `EAttribute` (eInt / eLong) |                                          |
| `"type": "array"`       | (Array type)           | `upperBound = -1`           | Defines a collection.                    |
| `"required": [...]`     | `required: [...]`      | `lowerBound = 1`            | Applied to the specific features listed. |
| `"readOnly": true`      | `readOnly: true`       | `changeable = false`        | Structural behavioral restriction.       |
| `"enum": [...]`         | `enum: [...]`          | `EEnum` / `EEnumLiteral`    | Standard enumeration definition.         |

## 2. Assertion Keywords to OCL Invariants

When a constraint restricts the *value* rather than the structure, it must be compiled into an OCL invariant (`inv`).

### String Constraints

- **`minLength` / `maxLength`**
  - *OCL:* `self.attribute.size() >= Min` or `self.attribute.size() <= Max`
- **`pattern` (Regex)**
  - *OCL:* `self.attribute.matches('^regex$')`
  - *Note:* Ensure your OCL environment supports the `matches()` operation (standard in Eclipse OCL).

### Numeric Constraints

- **`minimum` / `maximum`**
  - *OCL:* `self.attribute >= value` / `self.attribute <= value`
- **`exclusiveMinimum` / `exclusiveMaximum`**
  - *OCL:* `self.attribute > value` / `self.attribute < value`
- **`multipleOf`**
  - *OCL:* `self.attribute.mod(value) = 0` *(Note: `mod` is natively supported for Integers in OCL)*

### Array/Collection Constraints

- **`minItems` / `maxItems`**
  - *OCL:* `self.collection->size() >= value`
- **`uniqueItems`**
  - *OCL:* `self.collection->isUnique(iterator \| iterator)`

## 3. Logical & Conditional Applicators (Advanced Mapping)

JSON Schema applicators change the logical scope of validation. They require careful context-tracking during deserialization.

### `if`-`then`-`else` (Conditional Logic)

Evaluates a condition at the current structural context level.

- **JSON Schema:**

  JSON

  ```
  {
    "if": { "properties": { "status": { "const": "ACTIVE" } } },
    "then": { "properties": { "activationDate": { "type": "string" } } }
  }
  ```

- **OCL Invariant:**

  Object Constraint Language

  ```
  context MyEClass
  inv IfThenConstraint:
    (self.status = 'ACTIVE') implies not self.activationDate.oclIsUndefined()
  ```

### Boolean Logic (`allOf`, `anyOf`, `oneOf`)

- **`allOf` (AND):** Every subschema must be valid.
  - *OCL:* `ConditionA and ConditionB`
- **`anyOf` (OR):** At least one subschema must be valid.
  - *OCL:* `ConditionA or ConditionB`
- **`oneOf` (XOR):** Exactly one subschema must be valid.
  - *OCL:* `ConditionA xor ConditionB` *(Note: For more than two conditions, use an explicit count: `Sequence{CondA, CondB, CondC}->select(c \| c)->size() = 1`)*

## 4. OpenAPI 3.x Extensions & Formats

OpenAPI introduces specialized constraints for API contracts that require uniform translation rules.

- **`discriminator` (Polymorphism):** Ensures the type property matches the explicit schema implementation.
  - *OCL:* `self.oclIsTypeOf(Dog) implies self.type = 'Dog'`
- **`format: uuid`**
  - *OCL:* `self.uuid.matches('^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$')`
- **`format: email`**
  - *OCL:* Requires a regex translation string mapped inside a `.matches()` block.

## 5. Implementation Strategy: Context Tracking

When building your compiler/deserializer, maintain a **Context Stack**.

```
[Root JSON Schema]  --> Map to: EPackage / Root EClass
       │
       └──> ["properties"]["user"] --> Push Context: User EClass
                 │
                 └──> ["if"] --> Compile OCL using "self." scoped to User EClass
```

Always evaluate constraints relative to the closest `EClass` mapped by the parent structural keywords (`properties`, `items`, or `$ref`).

## 6. Official & Useful Specifications

### JSON Schema Official Docs

- [Understanding JSON Schema - Validation Keywords](https://json-schema.org/understanding-json-schema/keywords): Highly readable, categorized breakdown of all common validation concepts.
- [JSON Schema Validation Specification (Draft 2020-12)](https://json-schema.org/draft/2020-12/json-schema-validation): The formal specification detailing exact keyword behavior and edge cases.
- [LearnJSONSchema - Validation Glossary](https://www.google.com/search?q=https://www.learnjsonschema.com/2020-12/validation/): A comprehensive lookup matrix for assertions and keywords.

### OpenAPI Specification Docs

- [OpenAPI 3.0.3 - Schema Object Spec](https://www.google.com/search?q=https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md%23schemaObject): Reference for tools operating on older OAS 3.0 definitions (uses older JSON Schema Draft 00 variant).
- [OpenAPI 3.1.0 - Schema Object Spec](https://www.google.com/search?q=https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md%23schemaObject): Reference for modern API specs fully aligned with JSON Schema Draft 2020-12.
- [Swagger Data Models Guide](https://www.google.com/search?q=https://swagger.io/docs/specification/data-models/data-types/): Practical execution examples for data-type validations.

### Eclipse OCL Docs

- [Eclipse OCL Invariant Documentation](https://www.google.com/search?q=https://help.eclipse.org/2023-03/index.jsp%3Ftopic%3D%2Forg.eclipse.ocl.doc%2Fhelp%2FProgrammaticValidation.html): Guide on parsing and anchoring custom OCL strings dynamically into operational EMF models.