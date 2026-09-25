# Polymorphism and Inheritance

[← Feature Serialization](11-feature.md) | [Next: Load/Save Options →](13-load-save-options.md)

---

> **See also:**
> - [Reference Serialization](10-reference.md) for reference serialization options
> - [Annotation Reference](16-annotation-reference.md) (Reference Configuration) for complete configuration keys

---

## 1. Reference Type vs Instance Type

When serializing references, the serializer decides which type information to write based on the actual instance type and the `serializeInstanceType` setting.

### 1.1 Configuration

| Annotation Key | Property Key | Global | ERef | Default | Description |
|----------------|--------------|:------:|:----:|---------|-------------|
| `serializeInstanceType` | `codec.serializeInstanceType` | ✅ | ✅ | `true` | Write instance type (true) or reference type (false) |

### 1.2 Default Behavior (serializeInstanceType=true)

**Core Rule:** The serializer uses the **concrete instance type** (from `eObject.eClass()`), not the declared reference type.

**Smart Compression Interaction:**

| Smart Compression | Instance Type == Reference Type | Action |
|-------------------|--------------------------------|--------|
| ON | Yes | Omit `_type` (can be inferred) |
| ON | No | Write instance type |
| OFF | Yes | Write instance type |
| OFF | No | Write instance type |

**Examples:**

```java
// Reference typed as Person, contains FancyPerson instance
EReference employeeRef;  // type = Person
EObject instance;        // eClass = FancyPerson
```

**Smart Compression ON, instance type differs from reference type:**
```json
{
  "employee": {
    "_type": "http://example.org/person/1.0#//FancyPerson",
    "_ref": "john-doe"
  }
}
```

**Smart Compression ON, instance type equals reference type:**
```json
{
  "employee": {
    "_ref": "john-doe"
  }
}
```
(Type omitted because it can be inferred from the reference declaration)

**Smart Compression OFF (always writes instance type):**
```json
{
  "employee": {
    "_type": "http://example.org/person/1.0#//FancyPerson",
    "_ref": "john-doe"
  }
}
```

### 1.3 Writing Reference Type (serializeInstanceType=false)

When `serializeInstanceType=false`, the serializer writes the **declared reference type** instead of the instance type:

**EAnnotation (on EReference):**
```xml
<eStructuralFeatures xsi:type="ecore:EReference" name="employee" eType="#//Person">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="serializeInstanceType" value="false"/>
  </eAnnotations>
</eStructuralFeatures>
```

**Java Builder:**
```java
ReferenceConfigBuilder.forReference(PersonPackage.Literals.COMPANY__EMPLOYEE)
    .serializeInstanceType(false)  // Write declared type, not instance type
    .build();
```

**Property Map:**
```java
Map<String, Object> options = Map.of(
    CodecOptions.CODEC_SERIALIZE_INSTANCE_TYPE, false
);
```

**Result (reference typed as Person, instance is FancyPerson):**
```json
{
  "employee": {
    "_type": "http://example.org/person/1.0#//Person",
    "_ref": "john-doe"
  }
}
```

> **⚠️ Warning:** This is a **serialization-only option**. Using `serializeInstanceType=false` may cause deserialization failures if:
> - The reference type is an interface (cannot instantiate)
> - The reference type is abstract (cannot instantiate)
> - Instance-specific features from subclass are lost
>
> Use only when the consuming system specifically requires the declared type.

---

## 2. Annotation Inheritance

How codec configuration is inherited across the EClass hierarchy.

### 2.1 What the codec does (code reality)

- **Type configuration** (`typeStrategy`, `typeKey`, …) is inherited through the **full** EClass
  hierarchy: `ConfigurationResolver.resolveTypeConfig` walks `EClass.getEAllSuperTypes()`,
  parents first, and the child overrides.
- **All other configuration** (id, feature, reference, class, discriminator) is **not**
  inherited.

No switch changes this today.

### 2.2 The `inherit` key — accepted, not yet evaluated

| Annotation Key | Property Key | Level | Type | Default | Status |
|----------------|--------------|:-----:|------|---------|--------|
| `inherit` | `codec.inherit` | EClass | Boolean | `true` | Parsed, **no effect** |

```xml
<eClassifiers xsi:type="ecore:EClass" name="Employee" eSuperTypes="#//Person">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="inherit" value="true"/>
  </eAnnotations>
</eClassifiers>
```

The annotation is read into `ClassCodecAspect.inheritFromParent`. It stays because every
annotation key has its option pair, and `codec.inherit` is that pair; nothing reads the option yet.
(The LoRaWAN sample models once cited as its users were removed: they used annotation sources the
codec does not recognise, so none of their annotations was ever read.) Neither changes the behavior in §2.1 — `inherit="false"`
does **not** stop the type-config inheritance (issue #222).

> **Earlier design, not implemented.** A previous version of this section described
> inheritance *levels* `DIRECT` (default) / `ALL` / `NONE`, a `CodecConfiguration.inherit(...)`
> builder and a `ClassConfigBuilder`. None of these exist. If inheritance control is built, it is
> a new feature and gets its own specification.

---

## 3. Inheritance Resolution Order

For type configuration (the only inherited configuration, §2.1), the most specific setting wins:

1. Concrete class annotations (highest priority)
2. Parent annotations, nearer ancestors before farther ones - the whole hierarchy
3. Global codec defaults (lowest priority)

### 3.1 Inheritance Across Package Boundaries

The walk over the full hierarchy also leaves the concrete class's own package, and that has a
consequence worth stating: the inherited configuration comes from the **base package
version that the instance chain actually points at** — the concrete `EClass` instances reachable
through `eSuperTypes` — not from whichever version of that base `nsURI` happens to be resolved
elsewhere in the load.

This matters when several versions of a base model are registered at once. The same concrete
class, built against two different versions of its base package, has two different effective
configurations, and each is correct for its own instance chain. Configuration follows instance
identity, exactly as [02 §8](02-config-resolution.md) resolves it.

The declared `eReferenceType` of a reference is therefore only an **upper bound** on what may
arrive there: a subtype from another package version satisfies it. Version-correct resolution at
read time is what makes that safe — see
[10 §1.2.1](10-reference.md#121-version-identity-in-a-reference-entry).

---

## 4. Default Polymorphism Settings

| Setting | Property Key | Default Value |
|---------|--------------|---------------|
| Serialize Instance Type | `codec.serializeInstanceType` | `true` |
| Annotation Inheritance | `codec.inherit` | `true` (accepted, no effect yet - §2.2) |

---

[Next: Load/Save Options →](13-load-save-options.md)
