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
    "codec.serializeInstanceType", false
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

## 2. Annotation Inheritance Levels

Controls how codec annotations are inherited across the EClass hierarchy.

### 2.1 Configuration

| Annotation Key | Property Key | Global | EClass | Default | Description |
|----------------|--------------|:------:|:------:|---------|-------------|
| `inherit` | `codec.inherit` | ✅ | ✅ | `DIRECT` | Annotation inheritance level |

**Inheritance Level Values:**

| Level | Description | Inherits From |
|-------|-------------|---------------|
| `DIRECT` **(default)** | Only direct parent | Immediate superclass/interface |
| `ALL` | Full hierarchy | All ancestors up to EObject |
| `NONE` | No inheritance | Only concrete class annotations |

**Rationale for DIRECT as default:**
- Avoids accidentally inheriting annotations from external library base classes
- Predictable behavior - only looks one level up
- Matches OSGi DS component annotation inheritance semantics

### 2.2 EAnnotation (on EClass)

```xml
<!-- Force inheritance from parent even if parent is from different EPackage -->
<eClassifiers xsi:type="ecore:EClass" name="Employee" eSuperTypes="#//Person">
  <eAnnotations source="http://eclipse.org/fennec/codec">
    <details key="inherit" value="ALL"/>
  </eAnnotations>
</eClassifiers>
```

### 2.3 Java Builder (Runtime Override)

```java
// Global setting
CodecConfiguration config = CodecConfiguration.builder()
    .inherit(AnnotationInheritance.DIRECT)  // Default
    .build();

// Per-class override
ClassConfigBuilder.forEClass(EmployeePackage.Literals.EMPLOYEE)
    .inherit(AnnotationInheritance.ALL)
    .build();
```

**Property Map:**
```java
Map<String, Object> options = Map.of(
    "codec.inherit", "DIRECT"
);
```

### 2.4 Example Hierarchy

```
EObject (EMF base)
  └── Entity (library class, has idStrategy annotation)
        └── Person (your model, has typeStrategy annotation)
              └── Employee (your model, no annotations)
```

| Inheritance Level | Employee sees annotations from |
|-------------------|--------------------------------|
| `NONE` | Employee only (none) |
| `DIRECT` | Employee + Person (typeStrategy) |
| `ALL` | Employee + Person + Entity (typeStrategy, idStrategy) |

---

## 3. Inheritance Resolution Order

When the same annotation exists at multiple levels, the most specific (closest to concrete class) wins:

1. Concrete class annotations (highest priority)
2. Direct parent annotations
3. Grandparent annotations (only if `inherit=ALL`)
4. Global codec defaults (lowest priority)

---

## 4. Default Polymorphism Settings

| Setting | Property Key | Default Value |
|---------|--------------|---------------|
| Serialize Instance Type | `codec.serializeInstanceType` | `true` |
| Annotation Inheritance | `codec.inherit` | `DIRECT` |

---

[Next: Load/Save Options →](13-load-save-options.md)
