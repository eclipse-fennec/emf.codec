# Feature Serialization

[← Reference Serialization](10-reference.md) | [Next: Polymorphism →](12-polymorphism.md)

---

> **See also:**
> - [Naming Conventions](03-naming-conventions.md) for key naming conventions
> - [Annotation Reference](16-annotation-reference.md) (Feature Configuration) for complete configuration keys

---

Features (EStructuralFeatures) have additional serialization options beyond the metadata targets.

## 1. Null, Default, and Empty Value Handling

### 1.1 Codec-Wide Defaults

Global settings that apply to all features unless overridden:

| Annotation Key | Property Key | Global | ERef | EAttr | Default | Description |
|----------------|--------------|:------:|:----:|:-----:|---------|-------------|
| `serializeDefaults` | `codec.serializeDefaults` | ✅ | ✅ | ✅ | `false` | Include fields with default values |
| `serializeNull` | `codec.serializeNull` | ✅ | ✅ | ✅ | `false` | Include fields with explicit null values |
| `serializeEmpty` | `codec.serializeEmpty` | ✅ | ✅ | ✅ | `false` | Include empty collections |

**Java Builder (Codec-Wide):**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .serializeDefaults(false)  // Default: omit default values
    .serializeNull(false)      // Default: omit null values
    .serializeEmpty(false)     // Default: omit empty collections
    .build();
```

**Default Behavior (serializeDefaults=false, serializeNull=false, serializeEmpty=false):**
```json
{
  "_type": "http://example.org/person/1.0#//Person",
  "_id": "john-doe",
  "firstName": "John",
  "lastName": "Doe"
  // "middleName" omitted (null)
  // "age" omitted (default value 0)
  // "tags" omitted (empty collection)
}
```

**With serializeNull=true:**
```json
{
  "_type": "http://example.org/person/1.0#//Person",
  "_id": "john-doe",
  "firstName": "John",
  "middleName": null,
  "lastName": "Doe"
}
```

**With serializeEmpty=true:**
```json
{
  "_type": "http://example.org/person/1.0#//Person",
  "_id": "john-doe",
  "firstName": "John",
  "lastName": "Doe",
  "tags": []
}
```

### 1.2 Feature Visibility Control

Control which features are serialized/deserialized using directional ignore and force options:

| Annotation Key | Property Key | ERef | EAttr | Default | Description |
|----------------|--------------|:----:|:-----:|---------|-------------|
| `ignore` | `codec.ignore` | ✅ | ✅ | `false` | Skip both read AND write |
| `ignoreRead` | `codec.ignoreRead` | ✅ | ✅ | `false` | Skip deserialization only |
| `ignoreWrite` | `codec.ignoreWrite` | ✅ | ✅ | `false` | Skip serialization only |
| `forceRead` | `codec.forceRead` | ✅ | ✅ | `false` | Force read EMF transient/volatile |
| `forceWrite` | `codec.forceWrite` | ✅ | ✅ | `false` | Force write EMF transient/volatile |

**Note:** These options are per-feature only (not supported on EClass).

**EAnnotation (on EStructuralFeature):**

```xml
<!-- Ignore feature completely (both read and write) -->
<eStructuralFeatures xsi:type="ecore:EAttribute" name="internalCache">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="ignore" value="true"/>
  </eAnnotations>
</eStructuralFeatures>

<!-- Ignore on write only (read from JSON but don't write back) -->
<eStructuralFeatures xsi:type="ecore:EAttribute" name="importedField">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="ignoreWrite" value="true"/>
  </eAnnotations>
</eStructuralFeatures>

<!-- Force write volatile/derived feature -->
<eStructuralFeatures xsi:type="ecore:EAttribute" name="coordinates" volatile="true">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="forceWrite" value="true"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Resolution logic:**
```
Serialization:
1. If feature in ignoreFeatures OR ignoreWrite=true OR ignore=true → skip
2. If EMF transient/volatile/derived → check forceWrite
   - forceWrite=true → continue
   - forceWrite=false → skip
3. Serialize normally

Deserialization:
1. If feature in ignoreFeatures OR ignoreRead=true OR ignore=true → skip
2. If EMF transient/volatile → check forceRead
   - forceRead=true → continue (feature must be changeable)
   - forceRead=false → skip
3. Deserialize normally
```

**Use cases:**
- `ignore=true`: Completely hide a feature from JSON (both directions)
- `ignoreWrite=true`: Read from JSON but don't write back (one-way import)
- `ignoreRead=true`: Write to JSON but ignore during read (computed output fields)
- `forceWrite=true`: Serialize volatile/derived features (e.g., GeoJSON coordinates)
- `forceRead=true`: Deserialize into volatile/derived features (roundtrip support)

### 1.3 Feature-Specific Value Overrides

Override codec-wide settings for individual features:

| Annotation Key | Property Key | ERef | EAttr | Default | Description |
|----------------|--------------|:----:|:-----:|---------|-------------|
| `serializeNull` | `codec.serializeNull` | ✅ | ✅ | `false` | Include null values in output |
| `serializeDefaults` | `codec.serializeDefaults` | ✅ | ✅ | `false` | Include default values in output |
| `serializeEmpty` | `codec.serializeEmpty` | ✅ | ✅ | `false` | Include empty collections in output |

**Semantics:** `true` = include in output, `false` = omit from output. Feature-level overrides codec-wide.

**EAnnotation (on EStructuralFeature):**

```xml
<!-- Force include null values for this feature (override codec default) -->
<eStructuralFeatures xsi:type="ecore:EAttribute" name="middleName">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="serializeNull" value="true"/>
  </eAnnotations>
</eStructuralFeatures>

<!-- Skip default values for this feature -->
<eStructuralFeatures xsi:type="ecore:EAttribute" name="counter">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="serializeDefaults" value="false"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Java Builder (Runtime Override):**
```java
// Ignore feature completely
FeatureConfigBuilder.forFeature("internalCache")
    .ignore(true)
    .build();

// Force serialize null for specific feature (override codec default)
FeatureConfigBuilder.forFeature("middleName")
    .serializeNull(true)
    .build();

// Skip default values for specific feature
FeatureConfigBuilder.forFeature("counter")
    .serializeDefaults(false)
    .build();
```

**Resolution Order:**
1. Feature-specific setting (if defined)
2. Codec-wide setting
3. Built-in default

### 1.4 Runtime Ignore List

The `ignoreFeatures` property provides runtime bulk ignore without model annotations:

| Property Key | Global | EClass | Description |
|--------------|:------:|:------:|-------------|
| `codec.ignoreFeatures` | 🔧 | 🔧 | Features to ignore (both read+write) |

**Note:** This is a **runtime-only** property (no EAnnotation support).

**Accepts:**
- Comma-separated string: `"internalId,debugInfo,tempCache"`
- `List<String>`: `List.of("internalId", "debugInfo", "tempCache")`
- `List<EStructuralFeature>`: `List.of(MyPackage.Literals.MY_CLASS__INTERNAL_ID, ...)`

**Property Map:**
```java
Map<String, Object> options = Map.of(
    "codec.ignoreFeatures", "internalId,debugInfo,tempCache"
);

// Or with list
Map<String, Object> options = Map.of(
    "codec.ignoreFeatures", List.of("internalId", "debugInfo")
);
```

### 1.5 Deserialization Behavior

The `serializeNull`, `serializeDefaults`, and `serializeEmpty` settings are **serialization-only** configurations. They control what is written to JSON but do **NOT** affect how JSON is read.

Deserialization follows these rules:

| JSON Input | Deserialization Result |
|------------|------------------------|
| Field with explicit `null` value | See type-specific behavior below |
| Field missing from JSON | Feature retains EMF default value |
| Field with value | Sets feature to parsed value |

#### Type-Specific Null Handling

EMF handles `null` differently for object types vs primitive types:

| Feature Type | JSON `null` Result | Reason |
|--------------|-------------------|--------|
| **Object types** (EString, EObject, etc.) | Sets to `null` | Object references can hold null |
| **Primitive types** (EInt, EBoolean, EDouble, etc.) | Resets to EMF default | Primitives cannot hold null; EMF treats null as "unset" |

**Important:** For primitive types, `eSet(attribute, null)` resets to the `defaultValueLiteral`, NOT the Java primitive default (0, false).

**Example:**

Given an EMF model with:
```java
EAttribute middleName;  // EString, optional (no default)
EAttribute count;       // EInt, defaultValueLiteral="42"
EAttribute active;      // EBoolean, defaultValueLiteral="true"
```

| JSON Input | middleName | count | active |
|------------|------------|-------|--------|
| `{"middleName": null, "count": 10}` | `null` | `10` | `true` (EMF default) |
| `{"count": 10}` | `null` (String default) | `10` | `true` (EMF default) |
| `{"middleName": "M", "count": null}` | `"M"` | `42` (EMF default¹) | `true` (EMF default) |
| `{}` | `null` | `42` (EMF default) | `true` (EMF default) |

¹ Note: `count: null` results in `42` (the EMF defaultValueLiteral), NOT `0` (Java int default).

**Key Points:**

1. **Explicit `null` for object types** → Feature is set to `null`
2. **Explicit `null` for primitive types** → Feature is reset to EMF default (via `eSet(attr, null)`)
3. **Missing field** → Feature retains its EMF default value (same effect as `null` for primitives)
4. **`serializeNull`/`serializeDefaults` do NOT affect deserialization** - these are output controls only
5. **Multi-valued features** → JSON `null` is ignored; missing field results in empty list

**Rationale:**

- **Object types respect explicit `null`**: If the JSON explicitly contains `"field": null`, the intent is to set the value to null.
- **Primitive types use EMF semantics**: EMF primitives cannot hold null, so null is interpreted as "unset" which returns the default value.
- **Missing fields use defaults**: This aligns with JSON conventions where omitted fields are unspecified, and EMF defaults provide sensible fallback values.
- **Symmetry not required**: Serialization decisions (what to include) are independent from deserialization decisions (how to interpret).

---

## 2. Feature Key Customization

Override the JSON property name for a feature.

| Annotation Key | Property Key | ERef | EAttr | Default | Description |
|----------------|--------------|:----:|:-----:|---------|-------------|
| `key` | `codec.key` | ✅ | ✅ | (feature name) | Custom JSON property name |

**Note:** Feature key customization is per-feature only (not supported on EClass).

**Resolution Order (highest to lowest priority):**
1. **Config Builder** - Runtime configuration override
2. **Model Annotation** - `<details key="key" value="..."/>` on EStructuralFeature
3. **ExtendedMetaData name** - Only when `useNamesFromExtendedMetadata=true` (see [Section 3](#3-extended-metadata-names))
4. **Feature Name** - Default EMF feature name

### 2.1 Model Annotation

Customize feature names directly in the EMF model using codec annotations:
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="firstName">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="key" value="first_name"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Resulting Output (from annotation):**
```json
{
  "first_name": "John",
  "lastName": "Doe"
}
```

### 2.2 Config Builder Override

The config builder can override annotation-defined names at runtime:

```java
// Override the annotation-defined key
FeatureConfigBuilder.forFeature("firstName")
    .key("givenName")  // Overrides annotation
    .build();
```

**Resulting Output (config overrides annotation):**
```json
{
  "givenName": "John",
  "lastName": "Doe"
}
```

This allows models to define sensible defaults via annotations while still permitting runtime customization for specific use cases (e.g., different API versions, external system integration).

---

## 3. Extended Metadata Names

Use names from XSD extended metadata annotations instead of EMF feature names.

| Property Key | Global | Default | Description |
|--------------|:------:|---------|-------------|
| `codec.useNamesFromExtendedMetadata` | 🔧 | `false` | Use ExtendedMetaData names |

**Note:** This is a **runtime-only** property (no EAnnotation support).

### 3.1 Configuration

**Java Builder:**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .useNamesFromExtendedMetadata(true)
    .build();
```

**Property Map:**
```java
Map<String, Object> options = Map.of(
    "codec.useNamesFromExtendedMetadata", true
);
```

### 3.2 Resolution Priority

When resolving the JSON key for a feature, the following priority applies:

1. **Explicit codec annotation `key`** - From `@CODEC_KEY` or `<details key="key" value="..."/>` (highest priority)
2. **ExtendedMetaData `name`** - Only when `useNamesFromExtendedMetadata=true`
3. **Feature name** - Default EMF feature name (fallback)

### 3.3 EAnnotation Format

The ExtendedMetaData annotation uses the standard EMF format:

```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="titles" upperBound="-1"
    eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EString">
  <eAnnotations source="http:///org/eclipse/emf/ecore/util/ExtendedMetaData">
    <details key="name" value="title"/>
  </eAnnotations>
</eStructuralFeatures>
```

With `useNamesFromExtendedMetadata=true`, the feature `titles` serializes as `"title"` in JSON.

### 3.4 Use Case

This is useful when EMF models are generated from XSD and you want to preserve the original XML element/attribute names.

### 3.5 Migration Note

> **Breaking change from v1:** The previous codec defaulted `useNamesFromExtendedMetadata` to `true`.
> In v2, the default is `false` for the following reasons:
>
> - **Principle of least surprise** - JSON keys match EMF feature names unless explicitly configured
> - **Explicit over implicit** - ExtendedMetaData usage is opt-in
> - **Simpler mental model** - Feature name in code matches JSON key by default
> - **Performance** - No annotation lookup overhead by default
>
> If migrating from v1 and relying on ExtendedMetaData names, add `.useNamesFromExtendedMetadata(true)` to your configuration.

---

## 4. Enum Serialization

Control how enum values are serialized:

| Annotation Key | Property Key | Global | EAttr | Default | Description |
|----------------|--------------|:------:|:-----:|---------|-------------|
| `enumSerialization` | `codec.enumSerialization` | ✅ | ✅ | `LITERAL` | How to serialize enum values |

**Note:** `enumSerialization` does not apply to EReference (enums are attributes).

**EnumSerialization Values:**

| Value | Description | Example |
|-------|-------------|---------|
| `LITERAL` **(default)** | Use enum literal name | `"status": "ACTIVE"` |
| `VALUE` | Use enum ordinal value | `"status": 1` |
| `NAME` | Use enum name | `"status": "Active"` |

### 4.1 EAnnotation (on EEnum or EAttribute)

On the EEnum itself (applies to all usages):
```xml
<eClassifiers xsi:type="ecore:EEnum" name="Status">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="enumSerialization" value="LITERAL"/>
  </eAnnotations>
  <eLiterals name="ACTIVE" value="0"/>
  <eLiterals name="INACTIVE" value="1"/>
</eClassifiers>
```

Or on a specific EAttribute (overrides EEnum setting):
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="status" eType="#//Status">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="enumSerialization" value="VALUE"/>
  </eAnnotations>
</eStructuralFeatures>
```

### 4.2 Java Builder (Codec-Wide or Feature-Specific)

```java
// Codec-wide default
CodecConfiguration config = CodecConfiguration.builder()
    .enumSerialization(EnumSerialization.LITERAL)
    .build();

// Feature-specific override
FeatureConfigBuilder.forFeature("status")
    .enumSerialization(EnumSerialization.VALUE)
    .build();
```

**Property Map:**
```java
// Codec-wide
Map<String, Object> options = Map.of(
    "codec.enumSerialization", "LITERAL"
);
```

### 4.3 Deserialization Behavior

Deserialization automatically detects and handles the serialization format:

| Input Type | Handling |
|------------|----------|
| String | First tries `EEnum.getEEnumLiteral(name)`, then `EEnum.getEEnumLiteralByLiteral(literal)` |
| Integer | Looked up by enum value using `EEnum.getEEnumLiteral(int)` |

All three strategies round-trip correctly:

- **LITERAL strategy**: Uses literal lookup
- **NAME strategy**: Uses name lookup
- **VALUE strategy**: Uses integer value lookup

The deserializer tries name lookup first, then falls back to literal lookup, so both NAME and LITERAL serialized values are properly deserialized.

---

## 5. Default Feature Settings

| Setting | Property Key | Default Value |
|---------|--------------|---------------|
| Ignore | `codec.ignore` | `false` |
| Ignore Read | `codec.ignoreRead` | `false` |
| Ignore Write | `codec.ignoreWrite` | `false` |
| Force Read | `codec.forceRead` | `false` |
| Force Write | `codec.forceWrite` | `false` |
| Serialize Null | `codec.serializeNull` | `false` |
| Serialize Defaults | `codec.serializeDefaults` | `false` |
| Serialize Empty | `codec.serializeEmpty` | `false` |
| Enum Serialization | `codec.enumSerialization` | `LITERAL` |
| Use Names From ExtendedMetaData | `codec.useNamesFromExtendedMetadata` | `false` |
| Ignore Features | `codec.ignoreFeatures` | Empty (none) |

---

## 6. Force Serialize Volatile/Transient Features

By default, EMF features marked as `volatile`, `transient`, or `derived` are **not serialized**. This is because:
- **Transient** features are meant to be non-persistent
- **Derived** features are computed from other features
- **Volatile** features don't have storage and are computed on-the-fly

However, some models (like GeoJSON) use volatile features to provide computed views of data that **must** be serialized. The `forceWrite` and `forceRead` configuration allows overriding this behavior.

### 6.1 Configuration

Use per-feature `forceWrite` and `forceRead` annotations (see [Section 1.2](#12-feature-visibility-control)):

**EAnnotation:**
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="data" volatile="true">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="forceWrite" value="true"/>
    <details key="forceRead" value="true"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Java Builder (per-feature):**
```java
// Force serialize specific volatile feature by name
FeatureConfigBuilder.forFeature("data")
    .forceWrite(true)
    .forceRead(true)
    .build();
```

**Java Builder (type-safe convenience method):**
```java
// Force serialize multiple volatile features using EStructuralFeature references
// This is type-safe and recommended when you have access to the generated package
ConfigurationResolver resolver = ConfigurationResolver.builder()
    .forceWrite(
        GeoJsonPackage.Literals.POINT__DATA,
        GeoJsonPackage.Literals.LINE_STRING__DATA,
        GeoJsonPackage.Literals.GEO_JSON_OBJECT__BBOX
    )
    .forceRead(
        GeoJsonPackage.Literals.POINT__DATA,
        GeoJsonPackage.Literals.LINE_STRING__DATA,
        GeoJsonPackage.Literals.GEO_JSON_OBJECT__BBOX
    )
    .build();
```

The `forceWrite(EStructuralFeature...)` and `forceRead(EStructuralFeature...)` methods on `ConfigurationResolver.Builder` provide a type-safe way to force serialization of specific features. This is particularly useful for models like GeoJSON where multiple classes have volatile features that must be serialized.

### 6.2 Resolution Logic

When determining if a feature should be serialized:

1. Check if `ignore=true` OR `ignoreWrite=true` OR in `ignoreFeatures` → skip
2. Check if volatile/transient/derived:
   - If `forceWrite=true` → continue
   - Otherwise → skip
3. Serialize normally

### 6.3 Use Case: GeoJSON

The GeoJSON EMF model stores coordinates in a structured `Coordinates` object but exposes them as a `double[]` array through a volatile `data` attribute. For proper GeoJSON output, this volatile attribute must be serialized:

**Ecore model:**
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="data"
    eType="#//DoubleArray1D" volatile="true">
  <eAnnotations source="http:///org/eclipse/emf/ecore/util/ExtendedMetaData">
    <details key="name" value="coordinates"/>
  </eAnnotations>
</eStructuralFeatures>
```

**EAnnotation on volatile feature:**
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="data"
    eType="#//DoubleArray1D" volatile="true">
  <eAnnotations source="http:///org/eclipse/emf/ecore/util/ExtendedMetaData">
    <details key="name" value="coordinates"/>
  </eAnnotations>
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="forceWrite" value="true"/>
    <details key="forceRead" value="true"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Configuration:**
```java
CodecConfiguration config = CodecConfiguration.builder()
    .typeKey("type")
    .typeStrategy(TypeStrategy.NAME)
    .useNamesFromExtendedMetadata(true)
    .build();
```

**Output:**
```json
{"type": "Point", "coordinates": [8.6821, 50.1109]}
```

Without `forceWrite=true`, the output would only be:
```json
{"type": "Point"}
```

### 6.4 Deserialization

The `forceRead` configuration enables deserialization of volatile features from JSON, allowing round-trip support for computed attributes.

> **Note:** For deserialization to work, the volatile feature must still be **changeable** (`isChangeable()=true`). Non-changeable features cannot be set even with `forceRead=true`.

---

## 7. Array Attributes

EAttributes can have array data types (EDataTypes with array instance classes). The codec supports both primitive and object arrays of arbitrary dimensions.

### 7.1 Supported Array Types

| Array Type | Example JSON | Use Case |
|------------|--------------|----------|
| `double[]` | `[8.68, 50.11]` | GeoJSON Point coordinates |
| `double[][]` | `[[0,0], [1,1], [2,2]]` | GeoJSON LineString coordinates |
| `double[][][]` | `[[[0,0], [1,0], [1,1], [0,0]]]` | GeoJSON Polygon coordinates |
| `double[][][][]` | Nested arrays | GeoJSON MultiPolygon coordinates |
| `int[]` | `[1, 2, 3, 4, 5]` | Integer sequences |
| `long[]` | `[1000000000000, ...]` | Large integer sequences |
| `float[]` | `[1.5, 2.5, 3.5]` | Float sequences |
| `boolean[]` | `[true, false, true]` | Boolean flags |
| `String[]` | `["a", "b", "c"]` | String lists |
| `Date[]` | `["2025-01-14", "2024-12-25"]` | Date sequences |
| `BigDecimal[]` | `["123.456", "789.012"]` | Precise decimal sequences |
| `short[]`, `byte[]` | `[1, 2, 3]` | Compact integer sequences, e.g. `EByteArray` |
| `char[]`, `Character[]` | `["a", "b"]` | Characters, written as one-character strings |
| `Integer[]`, `Long[]`, `Double[]`, `Float[]`, `Short[]`, `Byte[]` | `[1, null, 3]` | Boxed numbers — unlike the primitive forms these may contain `null` |
| `Boolean[]` | `[true, null, false]` | Boxed booleans, `null` allowed |

> The boxed forms keep `null` elements: a primitive array cannot hold them, a boxed one can,
> and the writer emits them as JSON `null`.

> **`Date` without a configured `dateFormat`** is written in EMF's canonical form
> (`EcoreUtil.convertToString`), which carries the zone offset and reads back unchanged. It
> used to be `Date.toString()` — a form neither EMF nor this codec can parse, so such a value
> was silently lost on read. A configured `dateFormat` still wins, and formats with a native
> date-time type (BSON) keep using it.

### 7.2 Defining Array Data Types in Ecore

Array attributes require custom EDataTypes with the appropriate instance class:

```xml
<!-- 1D double array -->
<eClassifiers xsi:type="ecore:EDataType" name="DoubleArray1D"
    instanceClassName="double[]"/>

<!-- 2D double array -->
<eClassifiers xsi:type="ecore:EDataType" name="DoubleArray2D"
    instanceClassName="double[][]"/>

<!-- 3D double array -->
<eClassifiers xsi:type="ecore:EDataType" name="DoubleArray3D"
    instanceClassName="double[][][]"/>

<!-- Using the array type in an EClass -->
<eClassifiers xsi:type="ecore:EClass" name="Point">
  <eStructuralFeatures xsi:type="ecore:EAttribute" name="coordinates"
      eType="#//DoubleArray1D"/>
</eClassifiers>
```

### 7.3 Deserialization Behavior

The deserializer automatically detects array types based on:

1. **EDataType instance class** - Determines the target array type
2. **JSON token** - `START_ARRAY` triggers array deserialization
3. **Element type** - Primitive vs object arrays handled differently

**Primitive arrays** (`double[]`, `int[]`, etc.):
- Elements read directly from JSON numbers
- Both integer and float JSON numbers accepted for `double[]`

**Object arrays** (`Date[]`, `BigDecimal[]`, etc.):
- Elements converted from JSON strings via type-specific parsing
- Common types: `Date` (ISO format), `BigDecimal`, `BigInteger`, `UUID`
- Fallback: String constructor or `valueOf`/`parse` static methods

**Multi-dimensional arrays**:
- Recursively processed based on component type
- `double[][]` contains `double[]` elements
- `double[][][]` contains `double[][]` elements

### 7.4 Example: GeoJSON Coordinates

The GeoJSON model defines coordinate arrays using EDataTypes:

```xml
<eClassifiers xsi:type="ecore:EDataType" name="DoubleArray1D"
    instanceClassName="double[]"/>
<eClassifiers xsi:type="ecore:EDataType" name="DoubleArray2D"
    instanceClassName="double[][]"/>
<eClassifiers xsi:type="ecore:EDataType" name="DoubleArray3D"
    instanceClassName="double[][][]"/>
```

**Point** uses `double[]`:
```json
{"type": "Point", "coordinates": [8.6821, 50.1109]}
```

**LineString** uses `double[][]`:
```json
{"type": "LineString", "coordinates": [[0, 0], [10, 10], [20, 20]]}
```

**Polygon** uses `double[][][]`:
```json
{
  "type": "Polygon",
  "coordinates": [
    [[0, 0], [10, 0], [10, 10], [0, 10], [0, 0]],
    [[2, 2], [8, 2], [8, 8], [2, 8], [2, 2]]
  ]
}
```

### 7.5 Empty and Jagged Arrays

**Empty arrays** are supported:
```json
{"coordinates": []}
```
Results in a zero-length array (`new double[0]`).

**Jagged arrays** (varying inner lengths) are supported:
```json
{"data": [[1.0], [2.0, 3.0], [4.0, 5.0, 6.0]]}
```
Results in `double[3][]` with inner arrays of lengths 1, 2, 3.

### 7.6 Difference: Array Attribute vs Multi-Valued Attribute

| Aspect | Array Attribute | Multi-Valued Attribute |
|--------|-----------------|------------------------|
| EMF definition | `EAttribute` with array EDataType | `EAttribute` with `upperBound=-1` |
| Instance class | `double[]`, `String[]`, etc. | `EList<Double>`, `EList<String>` |
| JSON format | Same: `[1.0, 2.0, 3.0]` | Same: `[1.0, 2.0, 3.0]` |
| Multi-dimensional | Supported (`double[][]`) | Not directly supported |
| Use case | Fixed-structure data (coordinates) | Variable-length collections |

**Example - Array attribute:**
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="coordinates"
    eType="#//DoubleArray1D"/>
```

**Example - Multi-valued attribute:**
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="values"
    upperBound="-1" eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EDouble"/>
```

Both serialize to JSON arrays, but array attributes support multi-dimensional nesting while multi-valued attributes provide EMF list semantics.

---

## 8. JSON Structure to String Conversion (Deserialization Only)

When a String-typed EAttribute encounters a JSON object or array during deserialization, the codec converts the entire structure to a JSON string representation. This is a **deserialization-only** feature and does not support lossless roundtrips.

### 8.1 Use Case

Some schemas (like OpenAPI/JSON Schema) define string-typed attributes that can accept any JSON value. For example, the OpenAPI Schema `default` field can be:
- A string: `"default": "hello"`
- A number: `"default": 42`
- An object: `"default": {"timeout": 30}`
- An array: `"default": [1, 2, 3]`

When the EMF model defines such an attribute as `EString`, the codec needs to handle objects and arrays gracefully.

### 8.2 Deserialization Behavior

When a String-typed attribute encounters:

| Input JSON | Result String |
|------------|---------------|
| `{"timeout": 30}` | `"{\"timeout\":30}"` |
| `[1, 2, 3]` | `"[1,2,3]"` |
| `{"nested": {"a": [1]}}` | `"{\"nested\":{\"a\":[1]}}"` |
| `[]` | `"[]"` |
| `{}` | `"{}"` |

The JSON structure is serialized to a compact JSON string without extra whitespace.

### 8.3 Roundtrip Limitation

**Important:** This conversion is **not roundtrip-safe**.

- **Input:** `{"default": {"timeout": 30}}`
- **After deserialization:** `default = "{\"timeout\":30}"` (String)
- **After serialization:** `{"default": "{\"timeout\":30}"}` (escaped string)

The re-serialized output contains an escaped string, not the original JSON object.

### 8.4 Recommended Solution for Roundtrips

If roundtrip preservation is required, use `EJavaObject` instead of `EString`:

```xml
<!-- Recommended for any-value attributes -->
<eStructuralFeatures xsi:type="ecore:EAttribute" name="default"
    eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EJavaObject"/>
```

With `EJavaObject`:
- JSON object → `Map<String, Object>` (preserves structure)
- JSON array → `List<Object>` (preserves structure)
- Roundtrip works correctly

### 8.5 When to Use String vs EJavaObject

| Scenario | Use `EString` | Use `EJavaObject` |
|----------|---------------|-------------------|
| Import-only (no re-export) | ✓ (simpler) | ✓ |
| Roundtrip required | ✗ | ✓ |
| Need to inspect structure | ✗ | ✓ |
| Just need string representation | ✓ | Possible |

---

## 9. EJavaObject Attributes

EAttributes with type `EJavaObject` (instance class `Object.class`) can hold any JSON value. The codec provides full support for deserializing and serializing these dynamic-typed attributes.

### 9.1 Defining EJavaObject Attributes

```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="metadata"
    eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EJavaObject"/>
```

### 9.2 Deserialization Behavior

When deserializing an `EJavaObject` attribute, JSON values are converted to native Java types:

| JSON Type | Java Type |
|-----------|-----------|
| String | `String` |
| Integer | `Long` |
| Decimal | `Double` |
| Boolean | `Boolean` |
| Null | `null` |
| Array | `List<Object>` |
| Object | `Map<String, Object>` (LinkedHashMap) |

**Example:**

```json
{
  "metadata": {
    "enabled": true,
    "count": 42,
    "tags": ["a", "b"],
    "config": {"timeout": 30}
  }
}
```

After deserialization:
```java
Object metadata = eObject.eGet(metadataAttribute);
// metadata is LinkedHashMap<String, Object>
Map<String, Object> map = (Map<String, Object>) metadata;
Boolean enabled = (Boolean) map.get("enabled");     // true
Long count = (Long) map.get("count");               // 42L
List<?> tags = (List<?>) map.get("tags");           // ["a", "b"]
Map<?, ?> config = (Map<?, ?>) map.get("config");   // {timeout=30}
```

### 9.3 Serialization Behavior

When serializing an `EJavaObject` attribute, Java types are converted to JSON:

| Java Type | JSON Type |
|-----------|-----------|
| `String` | String |
| `Number` (Integer, Long, Double, etc.) | Number |
| `Boolean` | Boolean |
| `null` | null |
| `List`, `Collection` | Array |
| `Map` | Object |

This enables **lossless roundtrips** for any JSON value.

### 9.4 Use Cases

**1. Schema-less metadata:**
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="extensions"
    eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EJavaObject"/>
```

**2. OpenAPI/JSON Schema default values:**
```xml
<!-- Schema default can be any JSON value -->
<eStructuralFeatures xsi:type="ecore:EAttribute" name="default"
    eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EJavaObject"/>
```

**3. Dynamic configuration:**
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="options"
    eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EJavaObject"/>
```

### 9.5 Type Safety Considerations

Since `EJavaObject` accepts any value, type checking must be done at runtime:

```java
Object value = eObject.eGet(metadataAttribute);
if (value instanceof Map<?, ?> map) {
    // Handle object
} else if (value instanceof List<?> list) {
    // Handle array
} else if (value instanceof String str) {
    // Handle string
} else if (value instanceof Number num) {
    // Handle number
} else if (value instanceof Boolean bool) {
    // Handle boolean
}
```

### 9.6 Nested Structures

Nested JSON structures are fully supported. Arrays within objects, objects within arrays, and arbitrary nesting depths are all preserved:

```json
{
  "metadata": {
    "level1": {
      "level2": {
        "values": [1, 2, {"nested": true}]
      }
    }
  }
}
```

All nested structures are recursively converted to `Map<String, Object>` and `List<Object>`.

---

## 10. Custom Value Readers and Writers

For features requiring custom serialization logic, you can specify named value reader/writer services.

| Annotation Key | Property Key | ERef | EAttr | Default | Description |
|----------------|--------------|:----:|:-----:|---------|-------------|
| `valueReaderName` | `codec.valueReaderName` | ✅ | ✅ | (none) | Custom value reader service name |
| `valueWriterName` | `codec.valueWriterName` | ✅ | ✅ | (none) | Custom value writer service name |

**EAnnotation:**
```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="timestamp">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="valueReaderName" value="isoDateReader"/>
    <details key="valueWriterName" value="isoDateWriter"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Java Builder:**
```java
FeatureConfigBuilder.forFeature("timestamp")
    .valueReaderName("isoDateReader")
    .valueWriterName("isoDateWriter")
    .build();
```

**Use cases:**
- Custom date/time formatting
- Domain-specific value transformations
- Legacy format compatibility

---

## 11. Unknown Field and Strictness Handling

When deserializing JSON, fields may appear that don't correspond to any EMF feature in the target EClass. The strictness configuration controls how such mismatches are handled.

### 11.1 Strictness Configuration

| Annotation Key | Property Key | Global | EClass | Default | Description |
|----------------|--------------|:------:|:------:|---------|-------------|
| `strictOnUnknown` | `codec.strictOnUnknown` | ✅ | ✅ | `false` | ERROR on unknown JSON field |
| `strictOnMissing` | `codec.strictOnMissing` | ✅ | ✅ | `false` | ERROR on missing required feature |
| `strictOnConversion` | `codec.strictOnConversion` | ✅ | ✅ | `false` | ERROR when a present value cannot be converted |

**Note:** Strictness is not supported on EReference or EAttribute level. It applies to all features of a class uniformly.

**Behavior:**

| Mode | Unknown Field | Missing Required Field |
|------|---------------|------------------------|
| **LENIENT (default)** | Warning + skip | Warning + use default |
| **STRICT** (`strict*=true`) | Error + fail | Error + fail |

**Scope behavior:**
- **Global:** Applies to all features everywhere
- **EClass:** Applies to all features of that class

### 11.2 Default Behavior (LENIENT)

Unknown fields generate a **WARNING** and are **skipped**:

```json
{
  "_type": "http://example.org/1.0#//Person",
  "firstName": "John",
  "unknownField": "some value",
  "anotherUnknown": 123
}
```

**Result:**
- `firstName` is set on the Person object
- `unknownField` and `anotherUnknown` are skipped
- Warnings are added to `resource.getWarnings()`
- Deserialization completes successfully

### 11.3 Diagnostic Message

Unknown fields produce the following warning:

| Severity | Message Template | Source |
|----------|------------------|--------|
| WARNING | `Unknown feature '{name}' for EClass {class}` | AttributeDeserializationEntry |

### 11.4 Accessing Unknown Field Warnings

```java
resource.load(inputStream, options);

for (Diagnostic warning : resource.getWarnings()) {
    if (warning.getMessage().contains("Unknown feature")) {
        System.out.println("Unknown field: " + warning.getMessage());
    }
}
```

### 11.5 Strictness Use Cases

- `strictOnConversion=true`: Fail instead of storing a default when a value is present but
  cannot be converted. Worth considering wherever a wrong value is more expensive than a
  failed load: by default such a value is reported as a diagnostic and the feature keeps its
  default, which a caller **cannot distinguish from the value being absent** — `0` and `0.0`
  are values a model may legitimately hold. The diagnostic is the only trace, and it is easy
  to miss because the load itself reports success.
- `strictOnUnknown=true` at Global: Fail-fast for any unexpected JSON field (strict schema validation)
- `strictOnMissing=true` on EAttribute: Ensure this specific required field is always present
- `strictOnUnknown=false` on EReference: Allow extension fields in objects accessed via this reference (forward compatibility)

### 11.6 Relationship to Other Strictness Concepts

The codec has three independent strictness mechanisms at different levels:

| Mechanism | Scope | What it Controls | Default |
|-----------|-------|------------------|---------|
| `strictOnUnknown` / `strictOnMissing` | **Feature-level** | Unknown JSON fields, missing EMF features | `false` (LENIENT) |
| `CODEC_DESERIALIZATION_MODE` | **Type-level** | How flexibly to interpret `_type` values | `LENIENT` |
| `CODEC_FAIL_FAST` | **Diagnostic-level** | When to throw vs collect errors | `false` (collect all) |

These are **orthogonal** — they can be combined independently:

- `strictOnUnknown=true` + `failFast=false`: Collect all unknown field errors, report them all at once
- `strictOnUnknown=true` + `failFast=true`: Abort on first unknown field
- `DeserializationMode.STRICT` + `strictOnUnknown=false`: Strict about type format, lenient about unknown fields
- `DeserializationMode.LENIENT` + `strictOnMissing=true`: Flexible type resolution, but require all fields

> **Common confusion:** `DeserializationMode.STRICT` does NOT imply `strictOnUnknown=true`. `DeserializationMode` controls **type resolution** strategy (how `_type` values are parsed). `strictOnUnknown`/`strictOnMissing` controls **feature handling** (unknown/missing JSON fields). `failFast` controls **error reporting** (throw immediately vs collect). Each can be configured independently.

**Implementation status:** `strictOnUnknown` and `strictOnMissing` are specified and have `ConfigProperty` entries but are not yet wired in the runtime deserialization pipeline. See [99-open-questions.md](99-open-questions.md) for tracking.

---

## 12. Feature Serialization Flow

This section documents the complete serialization pipeline for features. Serialization is **EMF-driven**: the codec iterates EStructuralFeatures to build entries, then iterates entries to write JSON.

### 12.0 Feature Serialization Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    FEATURE SERIALIZATION FLOW                               │
└─────────────────────────────────────────────────────────────────────────────┘

INPUT: EObject, EStructuralFeature, effective config (annotations + save options)

┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. VISIBILITY GATE (build-time: determines if entry is created)            │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    1a. IGNORE CHECK                                                         │
│        Is ignore=true OR ignoreWrite=true?                                  │
│        ├─ YES → SKIP (no entry created) ───────────────────→ DONE ✗        │
│        └─ NO  → continue                                                   │
│                                                                             │
│    1b. RUNTIME IGNORE LIST CHECK                                            │
│        Is feature in ignoreFeatures list?                                   │
│        (ignoreFeatures accepts: comma-separated string, List<String>,       │
│         or List<EStructuralFeature>)                                        │
│        ├─ YES → SKIP ──────────────────────────────────────→ DONE ✗        │
│        └─ NO  → continue                                                   │
│                                                                             │
│    1c. EMF STRUCTURAL CHECK                                                 │
│        Is feature transient OR volatile OR derived?                         │
│        ├─ YES → Is forceWrite=true?                                         │
│        │        ├─ YES → continue (forced)                                  │
│        │        └─ NO  → SKIP ─────────────────────────────→ DONE ✗        │
│        └─ NO  → continue                                                   │
│                                                                             │
│    Feature passed visibility gate → create SerializationEntry               │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 2. KEY RESOLUTION (build-time: determines JSON property name)              │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Resolve JSON key from highest to lowest priority:                        │
│                                                                             │
│    ┌───────┬──────────────────────────────────────────────────────────────┐ │
│    │ Prio  │ Source                                                       │ │
│    ├───────┼──────────────────────────────────────────────────────────────┤ │
│    │  1    │ Config Builder / Load-Save option override                   │ │
│    │  2    │ EAnnotation: <details key="key" value="..."/>               │ │
│    │  3    │ ExtendedMetaData name (only if useNamesFromExtendedMetadata  │ │
│    │       │ =true; source "http:///org/eclipse/emf/ecore/util/          │ │
│    │       │ ExtendedMetaData", key "name")                              │ │
│    │  4    │ EStructuralFeature.getName() (EMF feature name)             │ │
│    └───────┴──────────────────────────────────────────────────────────────┘ │
│                                                                             │
│    Result: effectiveKey (used as JSON property name)                        │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 3. CUSTOM VALUE WRITER CHECK (build-time: determines delegation)           │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Is valueWriterName configured for this feature?                          │
│    ├─ YES → Full delegation to named ValueWriter service                   │
│    │        - Writer receives: EObject, feature, JsonGenerator, config     │
│    │        - Writer handles key writing AND value writing                  │
│    │        - Framework skips steps 4-5 ───────────────────→ DONE ✓        │
│    │                                                                       │
│    └─ NO  → Continue with built-in serialization (step 4)                  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 4. VALUE GATE (runtime: per-value check, entry.shouldSerialize())          │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Read value from EObject: Object value = eObject.eGet(feature)           │
│                                                                             │
│    4a. NULL CHECK                                                           │
│        Is value null?                                                       │
│        ├─ YES → Is serializeNull=true?                                      │
│        │        ├─ YES → Write: "key": null ───────────────→ DONE ✓        │
│        │        └─ NO  → SKIP (omit from output) ─────────→ DONE ✗        │
│        └─ NO  → continue                                                   │
│                                                                             │
│    4b. EMPTY CHECK (multi-valued features only)                             │
│        Is feature.isMany() AND collection is empty?                         │
│        ├─ YES → Is serializeEmpty=true?                                     │
│        │        ├─ YES → Write: "key": [] ─────────────────→ DONE ✓        │
│        │        └─ NO  → SKIP (omit from output) ─────────→ DONE ✗        │
│        └─ NO  → continue                                                   │
│                                                                             │
│    4c. DEFAULT CHECK (single-valued features only)                          │
│        Is value equal to the feature's default value?                       │
│        ├─ YES → Is serializeDefaults=true?                                  │
│        │        ├─ YES → continue (write the default)                       │
│        │        └─ NO  → SKIP (omit from output) ─────────→ DONE ✗        │
│        └─ NO  → continue                                                   │
│                                                                             │
│    Value passed all gates → proceed to write                                │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 5. VALUE WRITING (runtime: produces JSON output)                           │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Write JSON property name: gen.writeFieldName(effectiveKey)               │
│                                                                             │
│    5a. FEATURE TYPE DISPATCH                                                │
│                                                                             │
│        ┌──────────────────────────┬────────────────────────────────────────┐│
│        │ Feature Type             │ Handling                               ││
│        ├──────────────────────────┼────────────────────────────────────────┤│
│        │ EAttribute (single)      │ Write scalar value (see 5b)           ││
│        │ EAttribute (many)        │ Write JSON array of scalar values     ││
│        │ EAttribute (array type)  │ Write JSON array (possibly nested)    ││
│        │ EAttribute (EJavaObject) │ Write native JSON value (any type)    ││
│        │ EReference (single)      │ See Reference flow (10-reference.md)  ││
│        │ EReference (many)        │ Write JSON array of objects/refs      ││
│        │ EReference (EMap)        │ Write JSON object (key-value pairs)   ││
│        └──────────────────────────┴────────────────────────────────────────┘│
│                                                                             │
│    5b. ATTRIBUTE VALUE SERIALIZATION                                        │
│                                                                             │
│        ┌──────────────────────────┬────────────────────────────────────────┐│
│        │ Attribute Type           │ JSON Output                            ││
│        ├──────────────────────────┼────────────────────────────────────────┤│
│        │ EString                  │ String: "value"                        ││
│        │ EInt, ELong, EShort      │ Number: 42                             ││
│        │ EFloat, EDouble          │ Number: 3.14                           ││
│        │ EBoolean                 │ Boolean: true/false                    ││
│        │ EDate                    │ String: ISO format                     ││
│        │ EBigDecimal, EBigInteger │ Number or String (configurable)       ││
│        │ EEnum                    │ See 5c                                 ││
│        │ EJavaObject              │ Native JSON (map→object, list→array)  ││
│        │ Array EDataType          │ JSON array (1D, 2D, 3D, ...)          ││
│        └──────────────────────────┴────────────────────────────────────────┘│
│                                                                             │
│    5c. ENUM SERIALIZATION (see §4)                                          │
│                                                                             │
│        ┌──────────────────────────┬────────────────────────────────────────┐│
│        │ Strategy                 │ Output                                 ││
│        ├──────────────────────────┼────────────────────────────────────────┤│
│        │ LITERAL (default)        │ String: enum literal name              ││
│        │ NAME                     │ String: enum name                      ││
│        │ VALUE                    │ Integer: enum ordinal value            ││
│        └──────────────────────────┴────────────────────────────────────────┘│
│                                                                             │
│    ──────────────────────────────────────────────────────→ DONE ✓           │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 12.0.1 Serialization Gate Summary

The feature serialization pipeline has **two levels of gating**:

| Gate | When | What | Decides |
|------|------|------|---------|
| **Visibility Gate** (step 1) | Build-time (entry creation) | ignore, ignoreWrite, ignoreFeatures, transient/volatile + forceWrite | Whether feature participates at all |
| **Value Gate** (step 4) | Runtime (per-object) | null/empty/default + serializeNull/serializeEmpty/serializeDefaults | Whether this specific value is written |

> **Design rationale:** The two-level gate separates structural decisions (which features exist) from value decisions (which values are interesting). This means the entry map is built once per EClass, while value checks run per object instance.

---

## 13. Feature Deserialization Flow

This section documents the complete deserialization pipeline for features.

Unlike serialization (which is **EMF-driven** — iterates features, writes JSON), deserialization is **JSON-driven** — Jackson iterates JSON tokens and the codec looks up matching entries. This fundamental asymmetry shapes the entire flow.

### 13.0 Object-Level Deserialization Flow (Jackson-Driven)

Jackson calls `CodecEObjectDeserializer.deserialize(parser, ctxt)` with the parser positioned at `START_OBJECT`. The deserializer must resolve the type **before** it can process any feature — because it doesn't know which EClass (and thus which features) to expect until the `_type` field is read.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│              OBJECT DESERIALIZATION FLOW (Jackson-Driven)                   │
│                                                                             │
│  INPUT: JsonParser at START_OBJECT, optional EClass hint (from             │
│         containment reference type or CODEC_ROOT_OBJECT option)            │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│ PHASE 1: TYPE-FIRST SCAN (before any feature processing)                   │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Jackson's token loop drives the scan:                                    │
│                                                                             │
│    while (parser.nextToken() != END_OBJECT) {                              │
│        propertyName = parser.currentName()                                  │
│        parser.nextToken()  // move to value                                │
│                                                                             │
│        ┌─────────────────────────────────────────────────────────────────┐  │
│        │ Is this the _schema key?                                        │  │
│        │ ├─ YES → store schemaValue, continue                           │  │
│        │ │        (used for PLAIN SCHEMA_AND_TYPE format)               │  │
│        │ │                                                               │  │
│        │ ├─ Is this the _type key?                                       │  │
│        │ │  ├─ YES → RESOLVE TYPE (see Type Deser Flow, 06-type.md)     │  │
│        │ │  │        EClass now known → create EObject                   │  │
│        │ │  │        → replay deferred properties (Phase 2)             │  │
│        │ │  │        → switch to Phase 3 (normal processing)            │  │
│        │ │  │                                                            │  │
│        │ │  └─ NO  → DEFER this property                                │  │
│        │ │           Read value as raw Java object (String, Long,       │  │
│        │ │           Double, Boolean, Map, List, null)                   │  │
│        │ │           Store in deferredProperties map                     │  │
│        │ └───────────────────────────────────────────────────────────────│  │
│        └─────────────────────────────────────────────────────────────────┘  │
│    }                                                                        │
│                                                                             │
│    If END_OBJECT reached without _type → fall back to hint EClass          │
│    If no hint either → ERROR: "no type information found"                  │
│                                                                             │
│    KEY INSIGHT: JSON field order is unpredictable. Fields appearing         │
│    BEFORE _type are deferred. Fields AFTER _type are processed directly.   │
│    This means any field can be deferred — not just data fields.            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ PHASE 2: REPLAY DEFERRED PROPERTIES                                        │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    EClass is now resolved → build deserialization entries (see §13.1)      │
│                                                                             │
│    For each deferred property:                                              │
│    ┌────────────────────────────────────────────────────────────────────┐   │
│    │ 1. Look up entry by property name in entries map                   │   │
│    │ 2. If found and value is not null:                                 │   │
│    │    → Create TokenBuffer, write deferred value into it              │   │
│    │    → Create parser from TokenBuffer                                │   │
│    │    → Call entry.deserialize(state, bufferParser, ctxt)             │   │
│    │                                                                    │   │
│    │ Why TokenBuffer? Deferred values are stored as raw Java objects    │   │
│    │ (e.g., Long not BigInteger). Replaying through TokenBuffer gives  │   │
│    │ the entry proper JSON tokens for type-correct conversion.          │   │
│    └────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│    After replay → continue with Phase 3 for remaining JSON fields          │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ PHASE 3: NORMAL PROPERTY PROCESSING (remaining JSON fields)                │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Jackson continues the token loop from where Phase 1 left off:           │
│                                                                             │
│    while (parser.nextToken() != END_OBJECT) {                              │
│        propertyName = parser.currentName()                                  │
│        parser.nextToken()  // move to value                                │
│                                                                             │
│        → deserializeProperty(state, propertyName, parser, ctxt)            │
│          (see §13.2: Per-Field Processing)                                 │
│    }                                                                        │
│                                                                             │
│    After END_OBJECT → return EObject                                        │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 13.1 Feature Entry Building (Visibility Gate)

Before any feature can be deserialized, the codec builds a `Map<String, DeserializationEntry>` that maps JSON property names to feature handlers. This is the **visibility gate** — features excluded here cannot be deserialized regardless of what appears in the JSON.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ BUILD DESERIALIZATION ENTRIES (once per EClass)                             │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Input: resolved EClass                                                   │
│    Output: Map<String, DeserializationEntry>                               │
│                                                                             │
│    Step 1: Add metadata entries                                             │
│    ┌────────────────────────────────────────────────────────────────────┐   │
│    │ _type entry     → if typeConfig.isEnabled()                        │   │
│    │ _id entry       → if idConfig.isEnabled()                          │   │
│    │ _supertype entry → if superTypeConfig is present                   │   │
│    └────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│    Step 2: Add feature entries                                              │
│    For each feature in eClass.getEAllStructuralFeatures():                 │
│                                                                             │
│    ┌────────────────────────────────────────────────────────────────────┐   │
│    │ VISIBILITY GATE                                                    │   │
│    │                                                                    │   │
│    │ Gate 1: DESERIALIZE CHECK                                          │   │
│    │   EffectiveFeatureConfig.shouldDeserialize()                       │   │
│    │   Evaluates (in order):                                            │   │
│    │   a. ignore=true OR ignoreRead=true?        → SKIP               │   │
│    │   b. feature in ignoreFeatures list?         → SKIP               │   │
│    │   c. EMF transient OR volatile?                                    │   │
│    │      └─ forceRead=true?                                            │   │
│    │         ├─ YES → continue (forced)                                 │   │
│    │         └─ NO  → SKIP                                              │   │
│    │   d. All checks passed                       → VISIBLE            │   │
│    │                                                                    │   │
│    │ Gate 2: CHANGEABLE CHECK                                           │   │
│    │   feature.isChangeable()?                                          │   │
│    │   ├─ NO  → SKIP (cannot set values, even with forceRead)          │   │
│    │   └─ YES → continue                                               │   │
│    │                                                                    │   │
│    │ Note: Unlike serialization (which also checks derived),            │   │
│    │ deserialization only checks transient/volatile + changeable.       │   │
│    │ Derived features that are changeable and not transient CAN         │   │
│    │ be deserialized.                                                   │   │
│    └────────────────────────────────────────────────────────────────────┘   │
│                               │                                             │
│                               ▼                                             │
│    ┌────────────────────────────────────────────────────────────────────┐   │
│    │ KEY RESOLUTION (same as serialization, see §12.0 step 2)          │   │
│    │                                                                    │   │
│    │ 1. Config Builder override                                         │   │
│    │ 2. EAnnotation key                                                 │   │
│    │ 3. ExtendedMetaData name (if useNamesFromExtendedMetadata=true)   │   │
│    │ 4. EStructuralFeature.getName()                                    │   │
│    │                                                                    │   │
│    │ Result: effectiveKey (used to match incoming JSON property names)  │   │
│    └────────────────────────────────────────────────────────────────────┘   │
│                               │                                             │
│                               ▼                                             │
│    ┌────────────────────────────────────────────────────────────────────┐   │
│    │ CREATE ENTRY                                                       │   │
│    │                                                                    │   │
│    │ ┌──────────────────────────┬──────────────────────────────────┐    │   │
│    │ │ Feature Type             │ Entry Type                       │    │   │
│    │ ├──────────────────────────┼──────────────────────────────────┤    │   │
│    │ │ EAttribute               │ AttributeDeserializationEntry   │    │   │
│    │ │ EReference               │ ReferenceDeserializationEntry   │    │   │
│    │ └──────────────────────────┴──────────────────────────────────┘    │   │
│    │                                                                    │   │
│    │ entries.put(effectiveKey, entry)                                   │   │
│    └────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│    Result: Map<effectiveKey → DeserializationEntry>                         │
│    Contains: metadata entries + visible feature entries                     │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 13.2 Per-Field Processing (JSON-Driven)

For each JSON field (whether from Phase 2 replay or Phase 3 normal processing), the codec looks up the entry and delegates:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ PER-FIELD PROCESSING                                                        │
│    ─────────────────────────────────────────────────────────────────────────│
│                                                                             │
│    Input: propertyName (from JSON), parser (positioned at value token)     │
│                                                                             │
│ ┌───────────────────────────────────────────────────────────────────────┐   │
│ │ Step 1: ENTRY LOOKUP                                                  │   │
│ │                                                                       │   │
│ │   entry = entries.get(propertyName)                                   │   │
│ │                                                                       │   │
│ │   ├─ FOUND → entry.deserialize(state, parser, ctxt)                  │   │
│ │   │          (delegate to entry — see Step 2/3 below)                │   │
│ │   │                                                                   │   │
│ │   └─ NOT FOUND → UNKNOWN FIELD                                       │   │
│ │                                                                       │   │
│ │       Is strictOnUnknown=true? (effective config for this EClass)    │   │
│ │       ├─ YES → ERROR diagnostic                                     │   │
│ │       │        "Unknown feature '{name}' for EClass {class}"        │   │
│ │       │        (with failFast=true → throw immediately)             │   │
│ │       └─ NO  → WARNING diagnostic (same message)                    │   │
│ │                                                                       │   │
│ │       parser.skipChildren()                                          │   │
│ │       (skip value including nested objects/arrays)                   │   │
│ │                                                                       │   │
│ │   Note: strictOnUnknown is resolved from effective config:           │   │
│ │   feature-level → EClass-level → global. See §11 for details.       │   │
│ └───────────────────────────────────────────────────────────────────────┘   │
│                               │                                             │
│                               ▼                                             │
│ ┌───────────────────────────────────────────────────────────────────────┐   │
│ │ Step 2: ATTRIBUTE DESERIALIZATION (AttributeDeserializationEntry)      │   │
│ │                                                                       │   │
│ │   2a. CUSTOM VALUE READER CHECK                                       │   │
│ │       Is valueReaderName configured?                                  │   │
│ │       ├─ YES → Delegate to named ValueReader service                 │   │
│ │       │        Reader handles parsing AND setting the value          │   │
│ │       │        → DONE                                                 │   │
│ │       └─ NO  → continue                                              │   │
│ │                                                                       │   │
│ │   2b. SINGLE vs MULTI-VALUED DISPATCH                                 │   │
│ │       ┌──────────────────────────────────────────────────────────┐    │   │
│ │       │ feature.isMany()?                                        │    │   │
│ │       │ ├─ YES → Parser token is START_ARRAY?                    │    │   │
│ │       │ │        ├─ YES → iterate array, parse each element     │    │   │
│ │       │ │        │        add to EList via eObject.eGet(feature) │    │   │
│ │       │ │        └─ NO  → single value in array context          │    │   │
│ │       │ │                 (wrap and add to EList)                 │    │   │
│ │       │ │                                                        │    │   │
│ │       │ └─ NO  → Parse single value, eObject.eSet(feature, val) │    │   │
│ │       └──────────────────────────────────────────────────────────┘    │   │
│ │                                                                       │   │
│ │   2c. VALUE PARSING (readValue per token type)                        │   │
│ │                                                                       │   │
│ │       ┌──────────────────────────┬──────────────────────────────────┐ │   │
│ │       │ JSON Token               │ Conversion                       │ │   │
│ │       ├──────────────────────────┼──────────────────────────────────┤ │   │
│ │       │ VALUE_NULL               │ Object type → eSet(null)         │ │   │
│ │       │                          │ Primitive → reset to EMF default │ │   │
│ │       │                          │ Multi-valued → no-op (ignored)   │ │   │
│ │       │ VALUE_STRING             │ convertFromString(dataType)      │ │   │
│ │       │ VALUE_NUMBER_INT         │ EEnum? → enum by ordinal         │ │   │
│ │       │                          │ else → convertFromInteger(class) │ │   │
│ │       │ VALUE_NUMBER_FLOAT       │ convertFromFloat(class)          │ │   │
│ │       │ VALUE_TRUE / VALUE_FALSE │ Boolean conversion               │ │   │
│ │       │ START_ARRAY              │ Array EDataType → Java array     │ │   │
│ │       │                          │ (double[], int[], nested, etc.)  │ │   │
│ │       │ START_OBJECT             │ EString → JSON-to-string (§8)    │ │   │
│ │       │                          │ EJavaObject → Map<String,Object> │ │   │
│ │       └──────────────────────────┴──────────────────────────────────┘ │   │
│ │                                                                       │   │
│ │   2d. ENUM DESERIALIZATION (§4.3)                                     │   │
│ │       ┌──────────────────────────┬──────────────────────────────────┐ │   │
│ │       │ JSON Value               │ Resolution                       │ │   │
│ │       ├──────────────────────────┼──────────────────────────────────┤ │   │
│ │       │ String                   │ Try name first, then literal     │ │   │
│ │       │ Integer                  │ Lookup by enum ordinal value     │ │   │
│ │       └──────────────────────────┴──────────────────────────────────┘ │   │
│ └───────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│ ┌───────────────────────────────────────────────────────────────────────┐   │
│ │ Step 3: REFERENCE DESERIALIZATION (ReferenceDeserializationEntry)      │   │
│ │                                                                       │   │
│ │   3a. SINGLE vs MULTI-VALUED DISPATCH (same as attributes)            │   │
│ │                                                                       │   │
│ │   3b. PER-ELEMENT: CONTAINMENT vs NON-CONTAINMENT                     │   │
│ │                                                                       │   │
│ │       ┌────────────────────────────────────────────────────────────┐  │   │
│ │       │ reference.isContainment()?                                  │  │   │
│ │       │                                                             │  │   │
│ │       │ YES (Containment) → RECURSIVE DESERIALIZATION               │  │   │
│ │       │   1. Resolve type hint for child:                           │  │   │
│ │       │      - Runtime CODEC_FEATURE_TYPE_HINTS option              │  │   │
│ │       │      - Custom valueReaderName                               │  │   │
│ │       │      - Declared reference.getEReferenceType()               │  │   │
│ │       │   2. Set hint via ContextHelper.setExpectedType()           │  │   │
│ │       │   3. Call Jackson: deser.deserialize(parser, ctxt)          │  │   │
│ │       │      → Re-enters CodecEObjectDeserializer.deserialize()    │  │   │
│ │       │      → Child object follows same Phase 1-3 flow            │  │   │
│ │       │   4. eObject.eSet(reference, childObject)                   │  │   │
│ │       │                                                             │  │   │
│ │       │ NO (Non-containment) → BUFFER AND INSPECT                   │  │   │
│ │       │   1. Buffer entire JSON object into TokenBuffer             │  │   │
│ │       │   2. Scan buffered content for _ref field                   │  │   │
│ │       │   3. Three cases:                                           │  │   │
│ │       │      ┌──────────────────────────────────────────────────┐   │  │   │
│ │       │      │ _ref only          │ Create proxy URI            │   │  │   │
│ │       │      │                    │ → add to unresolvedRefs     │   │  │   │
│ │       │      │                    │   (resolved post-load)      │   │  │   │
│ │       │      ├────────────────────┼─────────────────────────────┤   │  │   │
│ │       │      │ _ref + data fields │ Deserialize full object     │   │  │   │
│ │       │      │ (projection)       │ → set proxy URI on result   │   │  │   │
│ │       │      │                    │   (projection pattern)      │   │  │   │
│ │       │      ├────────────────────┼─────────────────────────────┤   │  │   │
│ │       │      │ no _ref            │ Deserialize as orphan       │   │  │   │
│ │       │      │ (expanded ref)     │ → set directly on feature   │   │  │   │
│ │       │      └──────────────────────────────────────────────────┘   │  │   │
│ │       └────────────────────────────────────────────────────────────┘  │   │
│ └───────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│ ┌───────────────────────────────────────────────────────────────────────┐   │
│ │ Step 4: POST-PROCESSING (after all JSON fields consumed)              │   │
│ │                                                                       │   │
│ │   4a. UNRESOLVED REFERENCES                                           │   │
│ │       Collected in shared list across entire object tree              │   │
│ │       Resolved by CodecResource after load completes                 │   │
│ │       (URI-based resolution via Resource.getEObject())               │   │
│ │                                                                       │   │
│ │   4b. MISSING FIELD CHECK (§11, strictOnMissing)                      │   │
│ │       For each entry that was NOT visited during JSON parsing:       │   │
│ │                                                                       │   │
│ │       Is strictOnMissing=true? (effective config for this feature)   │   │
│ │       ├─ YES → Is feature required? (lower bound > 0 in Ecore)      │   │
│ │       │        ├─ YES → ERROR diagnostic                            │   │
│ │       │        │        "Required feature '{name}' missing for       │   │
│ │       │        │         EClass {class}"                             │   │
│ │       │        │        (with failFast=true → throw immediately)    │   │
│ │       │        └─ NO  → no diagnostic (optional feature, OK)        │   │
│ │       └─ NO  → no diagnostic (LENIENT: retain EMF default value)    │   │
│ │                                                                       │   │
│ │       Note: strictOnMissing is resolved from effective config:       │   │
│ │       feature-level → EClass-level → global. See §11 for details.   │   │
│ └───────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 13.3 Serialization vs Deserialization Comparison

| Aspect | Serialization | Deserialization |
|--------|---------------|-----------------|
| **Driver** | **EMF-driven**: iterate features → write JSON | **JSON-driven**: Jackson iterates tokens → look up features |
| **Type handling** | Write `_type` at known position | Must scan for `_type` first; defer all fields seen before it |
| **Entry building** | Build entries → iterate them in order | Build entries → use as lookup map for incoming JSON fields |
| **Visibility gate** | `shouldSerialize()`: ignore, ignoreWrite, ignoreFeatures, transient/volatile/derived + forceWrite | `shouldDeserialize()`: ignore, ignoreRead, ignoreFeatures, transient/volatile + forceRead, changeable |
| **Value gate** | `shouldSerialize(state)`: null/empty/default checks | None — if JSON field present and entry exists, always process |
| **Unknown fields** | N/A (only writes known features) | Skip unknown (LENIENT) or error (STRICT) |
| **Missing fields** | Omitted by value gate (null/empty/default) | Retain EMF defaults (LENIENT) or error (STRICT) |
| **Ordering** | Controlled (idOnTop, alphabetical) | JSON field order (unpredictable) |
| **Nested objects** | Direct recursive call to serializer | Recursive call via Jackson with type hint in context |
| **References** | Inline or `{_ref: "..."}` | Buffer + inspect for `_ref` field (three-case dispatch) |
| **Custom handler** | valueWriterName | valueReaderName |

> **Key insight:** The fundamental asymmetry is that serialization *controls* the output order and structure, while deserialization must *react* to whatever JSON arrives. This is why deserialization needs the deferred properties pattern and the buffer-inspect-dispatch pattern for references.

---

[Next: Polymorphism →](12-polymorphism.md)
