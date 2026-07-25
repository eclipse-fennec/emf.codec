# SuperType Serialization

[← Type Serialization](06-type.md) | [Next: Discriminator Mapping →](08-discriminator-mapping.md)

---

> **See also:**
> - [Naming Conventions](03-naming-conventions.md) for key naming conventions
> - [Annotation Reference](16-annotation-reference.md) (SuperType Configuration) for complete configuration keys

---

SuperType serialization describes class inheritance hierarchy. It has two orthogonal dimensions:
- **Strategy** (`superTypeStrategy`): ALL, ALL_EMF, SINGLE, NONE - which supertypes to include
- **Presentation** (`superTypeAsArray`): true (array) or false (string) - how to present multiple values

**Important:** SuperType is an **extension of Type configuration**:
- Inherits `typeFormat` when `superTypeFormat` is not explicitly set
- Inherits `typeScope` for scope control
- Only applies at Global and EClass level (not EReference or EAttribute)

---

## 1. Format Follows Type

| Type Format | SuperType Output Location |
|-------------|---------------------------|
| PLAIN | Standalone `_supertype` field at root level |
| STRUCTURED | Inside `_type` object as `supertype` field |

---

## 2. PLAIN Format (Type is PLAIN)

When Type format is PLAIN, SuperType is a standalone field at root level.

### 2.1 ARRAY Presentation (Default)

Array of URI strings:

```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": ["http://example.org/1.0#//Entity", "http://audit.org/1.0#//Auditable"]
}
```

With smart compression (same namespace → simple name):

```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": ["Entity", "http://audit.org/1.0#//Auditable"]
}
```

### 2.2 STRING Presentation

Separator-joined URI string (default separator is `,`):

```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": "http://example.org/1.0#//Entity,http://audit.org/1.0#//Auditable"
}
```

With smart compression:

```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": "Entity,http://audit.org/1.0#//Auditable"
}
```

---

## 3. STRUCTURED Format (Type is STRUCTURED)

When Type format is STRUCTURED, SuperType is included **inside** the `_type` object as a `supertype` field. Presentation (ARRAY or STRING) still applies.

### 3.1 ARRAY Presentation (Default)

```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "Person",
    "supertype": ["Entity", "http://audit.org/1.0#//Auditable"]
  }
}
```

### 3.2 STRING Presentation

```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "Person",
    "supertype": "Entity,http://audit.org/1.0#//Auditable"
  }
}
```

---

## 4. Value Resolution Rules

SuperType values follow namespace matching rules based on the root EClass's EPackage:

| Condition | SuperType Value |
|-----------|-----------------|
| Supertype from the **same EPackage instance** as the root EClass | Simple EClass name (e.g., `"Entity"`) |
| Supertype from a **different EPackage** — including another version of the same `nsURI` | Full EClass URI (e.g., `"http://audit.org/1.0#//Auditable"`) |
| Smart compression **OFF** | Always full EClass URI |

**Example:** If root type is `http://example.org/1.0#//Person`:
- `Entity` from `http://example.org/1.0` → `"Entity"` (same package)
- `Auditable` from `http://audit.org/1.0` → `"http://audit.org/1.0#//Auditable"` (different package)

> **"Same namespace" means the same package *instance*, not an equal `nsURI` string.** Two
> versions of one model share an `nsURI`, so a string comparison would call them the same schema
> and emit a bare name. The reader resolves a bare name against the version *pinned* for that
> `nsURI` — under cross-package or cross-version inheritance that is a different package, and the
> supertype would silently be the wrong one. A bare name is only safe when the supertype really
> lives in the same package object.
>
> This is the same rule as for [type smart compression](05-global-options.md#1-smart-compression),
> for the same reason.

---

## 5. SuperType Strategy

The `superTypeStrategy` configuration controls which supertypes to include:

| Value | Description |
|-------|-------------|
| `ALL` **(default)** | All domain model supertypes (excludes EMF base classes like EObject, EModelElement) |
| `ALL_EMF` | All supertypes including EMF base classes |
| `SINGLE` | Only the immediate/direct supertype |
| `NONE` | No supertypes (equivalent to `superTypeSerialize=false`) |

**Example:** Given `Employee extends Person extends Entity`:

| Strategy | Output |
|----------|--------|
| `ALL` | `["Person", "Entity"]` |
| `ALL_EMF` | `["Person", "Entity", "EObject"]` |
| `SINGLE` | `["Person"]` (only direct parent) |
| `NONE` | (no supertype field) |

---

## 6. SuperType Configuration

### 6.0 Configuration Constraints

SuperType configuration has dependencies on Type configuration:

| Constraint | Condition | Result |
|------------|-----------|--------|
| **STRUCTURED requires Type** | `typeFormat=STRUCTURED` + `typeStrategy=NONE` + `superTypeSerialize=true` | **ERROR** - Cannot write supertype inside `_type` object when no `_type` is written |
| **Format follows Type** | `superTypeFormat` not explicitly set | Inherits from `typeFormat` |
| **Key default depends on format** | `superTypeKey` not explicitly set | PLAIN → `_supertype`, STRUCTURED → `supertype` |
| **Scope follows Type** | `typeScope` controls where supertype is written | SuperType written only for objects matching `typeScope` |
| **Separator symmetry** | Custom `superTypeSeparator` used during serialization | Same separator MUST be configured for deserialization |
| **Custom reader conflict** | STRUCTURED + `typeValueReaderName` + `superTypeValueReaderName` | `superTypeValueReaderName` IGNORED + **WARNING** |
| **Custom writer conflict** | STRUCTURED + `typeValueWriterName` + `superTypeValueWriterName` | `superTypeValueWriterName` IGNORED + **WARNING** |

#### 6.0.1 STRUCTURED Format + typeStrategy=NONE

**Invalid configuration:**
```java
// ERROR: supertype needs _type object but typeStrategy=NONE means no _type
CodecConfiguration config = CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.NONE)        // No _type field
    .typeFormat(SerializationFormat.STRUCTURED)
    .superTypeSerialize(true)               // Wants to write supertype
    .build();
```

**Valid alternatives:**
```java
// Option 1: Use PLAIN format (supertype becomes standalone field)
CodecConfiguration config = CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.NONE)
    .typeFormat(SerializationFormat.PLAIN)  // PLAIN allows standalone _supertype
    .superTypeSerialize(true)
    .build();
// Output: { "_supertype": ["Entity"], "name": "John" }

// Option 2: Don't serialize supertype when type is disabled
CodecConfiguration config = CodecConfiguration.builder()
    .typeStrategy(TypeStrategy.NONE)
    .superTypeSerialize(false)              // Disabled
    .build();
// Output: { "name": "John" }
```

#### 6.0.2 Separator Symmetry Requirement

When using string presentation (`superTypeAsArray=false`) with a **non-default separator**, the same separator must be configured for both serialization and deserialization:

```java
// Serialization with custom separator
CodecConfiguration serConfig = CodecConfiguration.builder()
    .superTypeSerialize(true)
    .superTypeAsArray(false)
    .superTypeSeparator("|")  // Custom separator
    .build();
// Output: "_supertype": "Entity|Auditable"

// Deserialization MUST match:
CodecConfiguration deserConfig = CodecConfiguration.builder()
    .superTypeAsArray(false)
    .superTypeSeparator("|")  // Same separator required!
    .build();
// Otherwise "Entity|Auditable" won't be parsed correctly
```

#### 6.0.3 Custom ValueReader/ValueWriter Conflict in STRUCTURED Format

In STRUCTURED format, the `_type` field is a JSON object containing both type and supertype information:
```json
{ "_type": { "schema": "...", "type": "Person", "supertype": ["Entity"] } }
```

When a **custom TypeValueReader** is configured, it reads the **entire** `_type` object. If a **SuperTypeValueReader** is also configured, there's a conflict - both want to process the same data.

**Resolution:** In STRUCTURED format, when both are configured:
- `superTypeValueReaderName` is **IGNORED**
- A **WARNING diagnostic** is raised
- The custom TypeValueReader is responsible for handling supertype data internally

The same applies to writers: `superTypeValueWriterName` is ignored when `typeValueWriterName` is configured in STRUCTURED format.

**Workaround:** Handle supertype processing inside your custom TypeValueReader/TypeValueWriter:

```java
public class MyTypeValueReader implements TypeValueReader {
    @Override
    public String readTypeValue(JsonNode typeNode, DeserializationContext ctx) {
        // Read type info
        String schema = typeNode.get("schema").asText();
        String type = typeNode.get("type").asText();

        // Also handle supertype if present (since superTypeValueReader is ignored)
        if (typeNode.has("supertype")) {
            JsonNode supertypeNode = typeNode.get("supertype");
            // ... custom supertype processing
        }

        return schema + "#//" + type;
    }
}
```

> **Note:** In PLAIN format, there's no conflict - TypeValueReader reads `_type` (string) and SuperTypeValueReader reads `_supertype` (separate field).

### 6.1 Configuration Keys

| Annotation Key | Property Key | Default | Description |
|----------------|--------------|---------|-------------|
| `superTypeSerialize` | `codec.superTypeSerialize` | `false` | Enable supertype serialization |
| `superTypeStrategy` | `codec.superTypeStrategy` | `ALL` | Which supertypes to include |
| `superTypeKey` | `codec.superTypeKey` | (format-dependent) | JSON property name for supertype |
| `superTypeAsArray` | `codec.superTypeAsArray` | `true` | Array (true) or string (false) |
| `superTypeSeparator` | `codec.superTypeSeparator` | `,` | Separator for string presentation |
| `superTypeFormat` | `codec.superTypeFormat` | (inherits from `typeFormat`) | Output format (PLAIN/STRUCTURED) |
| `superTypeValueWriterName` | `codec.superTypeValueWriterName` | — | Custom value writer service name (see [§8.1 step 3](#81-serialization-steps) and [§6.0.3](#603-custom-valuereadervaluewriter-conflict-in-structured-format)) |
| `superTypeValueReaderName` | `codec.superTypeValueReaderName` | — | Custom value reader service name (see [§9.2 step 2](#92-strict-mode-validation) and [§6.0.3](#603-custom-valuereadervaluewriter-conflict-in-structured-format)) |

**`superTypeKey` default value:**

The default value for `superTypeKey` depends on the effective `superTypeFormat`:

| Effective Format | Default `superTypeKey` | Reason |
|------------------|------------------------|--------|
| PLAIN | `_supertype` | Underscore prefix for root-level metadata fields |
| STRUCTURED | `supertype` | No underscore inside `_type` object (follows `typeNameKey` pattern) |

When explicitly set, the configured value is used regardless of format.

> **Note:** In STRUCTURED format, the `schemaKey` is inherited from Type configuration (see [Type Serialization](06-type.md)).

> **Note:** SuperType configuration only applies at **Global** and **EClass** level. It is invalid on EReference or EAttribute (SuperType describes class inheritance, not how an object is accessed).

### 6.2 EAnnotation (on EClass)

```xml
<eClassifiers xsi:type="ecore:EClass" name="Person">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="superTypeSerialize" value="true"/>
    <details key="superTypeStrategy" value="ALL"/>
    <details key="superTypeAsArray" value="true"/>
    <details key="superTypeSeparator" value=","/>
    <details key="superTypeKey" value="_supertype"/>
  </eAnnotations>
</eClassifiers>
```

### 6.3 Java Builder (Runtime Override)

**Enable with defaults (ALL strategy, array presentation):**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .superTypeSerialize(true)
    .build();
```

**With STRING presentation:**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .superTypeSerialize(true)
    .superTypeAsArray(false)
    .superTypeSeparator("|")  // Custom separator
    .build();
```

**With SINGLE strategy:**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .superTypeSerialize(true)
    .superTypeStrategy(SuperTypeStrategy.SINGLE)
    .build();
```

**Property Map:**
```java
Map<String, Object> options = new HashMap<>();
options.put("codec.superTypeSerialize", true);
options.put("codec.superTypeStrategy", "SINGLE");
options.put("codec.superTypeAsArray", false);
```

---

## 7. Examples by Configuration

### 7.1 Type PLAIN + SuperType ARRAY (most common)

```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": ["Entity", "http://audit.org/1.0#//Auditable"],
  "name": "John"
}
```

### 7.2 Type PLAIN + SuperType STRING

```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": "Entity,http://audit.org/1.0#//Auditable",
  "name": "John"
}
```

### 7.3 Type STRUCTURED + SuperType ARRAY

```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "Person",
    "supertype": ["Entity", "http://audit.org/1.0#//Auditable"]
  },
  "name": "John"
}
```

### 7.4 Type STRUCTURED + SuperType STRING

```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "Person",
    "supertype": "Entity,http://audit.org/1.0#//Auditable"
  },
  "name": "John"
}
```

### 7.5 SINGLE Selection

```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": ["Entity"],
  "name": "John"
}
```

Or with STRING presentation (single value, no separator needed):

```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": "Entity",
  "name": "John"
}
```

---

## 8. Serialization Flow

SuperType serialization happens as **step 3d** in the Type Serialization Flow (see [06-type.md](06-type.md) section 5).

### 8.1 Serialization Steps

```
1. Check superTypeSerialize
   └─ false → DONE (no supertype written)
   └─ true → Continue

2. Check superTypeStrategy
   └─ NONE → DONE (no supertype written)
   └─ ALL/ALL_EMF/SINGLE → Collect supertypes from EClass hierarchy

3. Check superTypeValueWriterName
   ├─ Configured → Delegate to custom writer
   │              Input: EObject, List<EClass> (collected supertypes)
   │              Output: value to write (array or string)
   │              Note: In STRUCTURED format with typeValueWriterName,
   │                    superTypeValueWriterName is IGNORED (see §6.0.3)
   │
   └─ Not configured → Continue with built-in:

4. Apply smart compression (global setting)
   └─ For each supertype EClass:
      ├─ Same namespace as ROOT → simple name ("Entity")
      └─ Different namespace → full URI ("http://other/1.0#//Base")

5. Format output based on superTypeAsArray
   ├─ true → JSON array: ["Entity", "http://other/1.0#//Base"]
   └─ false → Separator-joined string: "Entity,http://other/1.0#//Base"

6. Write to JSON based on effective format
   ├─ PLAIN → Write standalone field at superTypeKey (default: _supertype)
   └─ STRUCTURED → Write inside _type object at superTypeKey (default: supertype)
```

---

## 9. Deserialization

SuperType information is typically **not needed for deserialization** since the concrete type (`_type`) fully determines the EClass to instantiate. The inheritance hierarchy is already defined in the EMF model.

**Both PLAIN and STRUCTURED formats are supported for deserialization:**

| Format | SuperType Location | Handled By |
|--------|-------------------|------------|
| PLAIN | Standalone `_supertype` field | `SuperTypeDeserializationEntry` |
| STRUCTURED | Inside `_type` object as `supertype` field | `TypeDeserializationEntry` |

### 9.1 Default Behavior (LENIENT mode)

By default (`DeserializationMode.LENIENT` or `AUTO_DETECT`), the deserializer:
1. **Skips** the supertype field entirely (does not read or parse)
2. Resolves the EClass from the `_type` field only
3. Creates the EObject based on the resolved type

> **Note:** In LENIENT mode, the supertype field is simply ignored - no parsing overhead.

### 9.2 Strict Mode Validation

When `DeserializationMode.STRICT` is set, the deserializer validates supertype hierarchy:

```
1. Resolve EClass from _type field (steps 1-4 in Type Deserialization Flow)

2. Check superTypeValueReaderName (PLAIN format only)
   ├─ Configured → Delegate to custom reader
   │              Input: raw JSON value (array or string)
   │              Output: List<String> (supertype identifiers)
   │              Note: In STRUCTURED format with typeValueReaderName,
   │                    superTypeValueReaderName is IGNORED (see §6.0.3)
   │
   └─ Not configured → Continue with built-in:

3. Parse supertype field
   ├─ Array → Extract each element as string
   └─ String → Split by superTypeSeparator (MUST match serialization!)

4. Validate each declared supertype
   └─ For each supertype identifier:
      ├─ Resolve to EClass (simple name or full URI)
      └─ Check: Is it in resolved EClass's getEAllSuperTypes()?
         ├─ YES → Continue
         └─ NO → Validation FAILURE
```

**Validation Rules:**
- Each declared supertype must exist in the EClass's `getEAllSuperTypes()` hierarchy
- Supertypes can be declared as simple names (same namespace) or full URIs (different namespace)
- Order of supertypes is not significant for validation
- Missing supertypes in JSON (subset) is acceptable
- Extra supertypes in JSON (not in actual hierarchy) causes validation failure

### 9.3 Configuration

SuperType validation is controlled by `DeserializationMode` (see [Load/Save Options](13-load-save-options.md)):

| Mode | SuperType Behavior |
|------|-------------------|
| `STRICT` | Read and validate hierarchy, fail on mismatch |
| `LENIENT` | Skip entirely (no read, no validation) |
| `AUTO_DETECT` | Same as LENIENT for supertype |

**Java Builder:**
```java
Map<String, Object> options = Map.of(
    CodecResourceOptions.DESERIALIZATION_MODE, DeserializationMode.STRICT
);
resource.load(inputStream, options);
```

### 9.4 Deserialization Examples

**PLAIN format with ARRAY presentation:**
```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": ["Entity", "http://audit.org/1.0#//Auditable"],
  "name": "John"
}
```

**PLAIN format with STRING presentation:**
```json
{
  "_type": "http://example.org/1.0#//Person",
  "_supertype": "Entity,http://audit.org/1.0#//Auditable",
  "name": "John"
}
```

**STRUCTURED format with ARRAY presentation:**
```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "Person",
    "supertype": ["Entity", "http://audit.org/1.0#//Auditable"]
  },
  "name": "John"
}
```

**STRUCTURED format with STRING presentation:**
```json
{
  "_type": {
    "schema": "http://example.org/1.0",
    "type": "Person",
    "supertype": "Entity,http://audit.org/1.0#//Auditable"
  },
  "name": "John"
}
```

### 9.5 Error Handling

When validation fails:
- Throw `SuperTypeValidationException` with descriptive message
- Include expected supertypes (from model)
- Include declared supertypes (from JSON)
- Include resolved EClass name

**Example error:**
```
SuperType hierarchy validation failed for EClass 'Person':
  Declared supertypes: [Entity, InvalidType]
  Expected supertypes: [Entity, NamedElement]
  Invalid supertype(s): [InvalidType] not found in hierarchy
```

---

[Next: Discriminator Mapping →](08-discriminator-mapping.md)

---

> **See also:** [Annotation Reference](16-annotation-reference.md) (SuperType Configuration) for complete configuration keys and invalid configuration scenarios.
