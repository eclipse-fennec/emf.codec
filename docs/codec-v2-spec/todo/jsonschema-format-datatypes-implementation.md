# JSON Schema `format` to EDataType Mapping

## Overview

JSON Schema's `format` keyword provides semantic hints about string values. Currently, we preserve these as annotations, but we could map them to proper EMF EDataTypes for type safety and tooling support.

---

## Current State

```json
{
  "timestamp": {
    "type": "string",
    "format": "date-time"
  }
}
```

Currently produces:
```
EAttribute timestamp : EString
  @EAnnotation(source="http://fennec.eclipse.org/jsonschema", details={format="date-time"})
```

**Problem:** No type safety, no IDE support, codec must interpret annotation at runtime.

---

## Proposed Solution

Map `format` values to proper EDataTypes with Java instance classes.

### Format Mapping Table

| JSON Schema Format | Java Type | EMF Type | Status |
|-------------------|-----------|----------|--------|
| `date-time` | `java.util.Date` | `EDate` | ✅ Built-in |
| `date` | `java.time.LocalDate` | `ELocalDate` | ❌ New |
| `time` | `java.time.LocalTime` | `ELocalTime` | ❌ New |
| `duration` | `java.time.Duration` | `EDuration` | ❌ New |
| `uuid` | `java.util.UUID` | `EUUID` | ❌ New |
| `uri` | `java.net.URI` | `EURI` | ❌ New |
| `uri-reference` | `java.net.URI` | `EURI` | ❌ New (same as uri) |
| `email` | `String` | `EString` | Keep as annotation |
| `hostname` | `String` | `EString` | Keep as annotation |
| `ipv4` | `String` | `EString` | Keep as annotation |
| `ipv6` | `String` | `EString` | Keep as annotation |
| `regex` | `java.util.regex.Pattern` | `EPattern` | ❌ New (optional) |

**Note:** Formats like `email`, `hostname`, `ipv4`, `ipv6` are validation hints, not distinct types. They remain `EString` with annotation.

---

## Implementation Options

### Option A: Contribute to EMF Core (Preferred)

Propose new EDataTypes for `org.eclipse.emf.ecore.EcorePackage`:

```java
// In EcorePackage
EDataType ELOCAL_DATE = ...;      // java.time.LocalDate
EDataType ELOCAL_TIME = ...;      // java.time.LocalTime
EDataType ELOCAL_DATE_TIME = ...; // java.time.LocalDateTime
EDataType EINSTANT = ...;         // java.time.Instant
EDataType EDURATION = ...;        // java.time.Duration
EDataType EUUID = ...;            // java.util.UUID
EDataType EURI = ...;             // java.net.URI
```

**Advantages:**
- Standard types available to all EMF users
- Generated code works out-of-the-box
- IDE tooling support
- No custom package dependency

**Challenges:**
- EMF contribution process (Eclipse IP review, etc.)
- Backwards compatibility considerations
- `java.time.*` requires Java 8+ (should be fine now)

**EMF Bugzilla/GitHub:**
- Check existing requests for java.time support
- Propose as enhancement

### Option B: Fennec Formats Package (Interim Solution)

Create `org.eclipse.fennec.codec.formats` package with predefined EDataTypes:

```
org.eclipse.fennec.codec.formats/
├── model/
│   └── formats.ecore
├── src-gen/
│   ├── FormatsPackage.java
│   ├── FormatsFactory.java
│   └── FormatsFactoryImpl.java  // Contains converters
```

**formats.ecore:**
```xml
<ecore:EPackage name="formats" nsURI="http://eclipse.org/fennec/formats/1.0">
  <eClassifiers xsi:type="ecore:EDataType" name="LocalDate"
                instanceClassName="java.time.LocalDate"/>
  <eClassifiers xsi:type="ecore:EDataType" name="LocalTime"
                instanceClassName="java.time.LocalTime"/>
  <eClassifiers xsi:type="ecore:EDataType" name="LocalDateTime"
                instanceClassName="java.time.LocalDateTime"/>
  <eClassifiers xsi:type="ecore:EDataType" name="Instant"
                instanceClassName="java.time.Instant"/>
  <eClassifiers xsi:type="ecore:EDataType" name="Duration"
                instanceClassName="java.time.Duration"/>
  <eClassifiers xsi:type="ecore:EDataType" name="UUID"
                instanceClassName="java.util.UUID"/>
  <eClassifiers xsi:type="ecore:EDataType" name="URI"
                instanceClassName="java.net.URI"/>
</ecore:EPackage>
```

**FormatsFactoryImpl.java (converters):**
```java
public String convertLocalDateToString(EDataType eDataType, Object value) {
    return value == null ? null : ((LocalDate) value).toString(); // ISO-8601
}

public LocalDate createLocalDateFromString(EDataType eDataType, String value) {
    return value == null || value.isEmpty() ? null : LocalDate.parse(value);
}

public String convertUUIDToString(EDataType eDataType, Object value) {
    return value == null ? null : value.toString();
}

public UUID createUUIDFromString(EDataType eDataType, String value) {
    return value == null || value.isEmpty() ? null : UUID.fromString(value);
}

public String convertInstantToString(EDataType eDataType, Object value) {
    return value == null ? null : ((Instant) value).toString(); // ISO-8601
}

public Instant createInstantFromString(EDataType eDataType, String value) {
    return value == null || value.isEmpty() ? null : Instant.parse(value);
}

// ... etc for other types
```

**Advantages:**
- Immediate solution without waiting for EMF contribution
- Full control over implementation
- Can be migrated to EMF types later

**Challenges:**
- Additional dependency for users
- Generated packages reference FormatsPackage
- Migration effort if EMF adds these types

### Option C: Dynamic EDataTypes (Runtime Only)

Create EDataTypes dynamically during JSON Schema conversion, with conversion logic in codec:

```java
EDataType localDateType = ecoreFactory.createEDataType();
localDateType.setName("LocalDate");
localDateType.setInstanceClassName("java.time.LocalDate");
addEAnnotation(localDateType, JSONSCHEMA_SOURCE, "format", "date");
ePackage.getEClassifiers().add(localDateType);
```

**Advantages:**
- No predefined package needed
- Self-contained in generated EPackage

**Challenges:**
- No generated converter code
- Codec must handle conversion at runtime
- Each generated package has its own copy of types

---

## Recommended Approach

### Short-term: Option B (Fennec Formats Package)

1. Create `org.eclipse.fennec.codec.formats` with common types
2. JSON Schema converter references these types
3. Codec v2 uses standard EMF conversion via FactoryImpl

### Long-term: Option A (EMF Contribution)

1. Discuss with team
2. Check EMF issue tracker for existing proposals
3. Prepare contribution proposal for Eclipse EMF project
4. If accepted, migrate from Fennec package to EMF core types

---

## JSON Schema Converter Changes

### JsonSchemaToEPackageConverter

```java
private EDataType resolveFormatType(String format, EPackage targetPackage) {
    return switch (format) {
        case "date-time" -> EcorePackage.Literals.EDATE;  // Built-in
        case "date" -> FormatsPackage.Literals.LOCAL_DATE;
        case "time" -> FormatsPackage.Literals.LOCAL_TIME;
        case "duration" -> FormatsPackage.Literals.DURATION;
        case "uuid" -> FormatsPackage.Literals.UUID;
        case "uri", "uri-reference", "iri", "iri-reference" -> FormatsPackage.Literals.URI;
        default -> null;  // Keep as EString with annotation
    };
}

private void createAttribute(String name, JsonNode propertyNode, EClass eClass) {
    EAttribute attr = ecoreFactory.createEAttribute();
    attr.setName(name);

    String type = propertyNode.path("type").asText("string");
    String format = propertyNode.path("format").asText(null);

    if ("string".equals(type) && format != null) {
        EDataType formatType = resolveFormatType(format, eClass.getEPackage());
        if (formatType != null) {
            attr.setEType(formatType);
            // Still add annotation for round-trip fidelity
            addEAnnotation(attr, JSONSCHEMA_SOURCE, "format", format);
        } else {
            attr.setEType(EcorePackage.Literals.ESTRING);
            addEAnnotation(attr, JSONSCHEMA_SOURCE, "format", format);
        }
    } else {
        attr.setEType(mapType(type));
    }

    eClass.getEStructuralFeatures().add(attr);
}
```

### EPackageToJsonSchemaConverter

```java
private String inferFormatFromType(EDataType type) {
    if (type == EcorePackage.Literals.EDATE) return "date-time";
    if (type == FormatsPackage.Literals.LOCAL_DATE) return "date";
    if (type == FormatsPackage.Literals.LOCAL_TIME) return "time";
    if (type == FormatsPackage.Literals.DURATION) return "duration";
    if (type == FormatsPackage.Literals.UUID) return "uuid";
    if (type == FormatsPackage.Literals.URI) return "uri";
    return null;
}

private void writeAttribute(EAttribute attr, JsonGenerator gen) {
    // ... existing logic ...

    // Check for format - first from annotation, then infer from type
    String format = extractAnnotationDetail(attr, JSONSCHEMA_SOURCE, "format");
    if (format == null) {
        format = inferFormatFromType((EDataType) attr.getEType());
    }

    if (format != null) {
        gen.writeStringProperty("format", format);
    }
}
```

---

## EMF Contribution Notes

### Existing Discussions

Check these resources for prior art:
- Eclipse EMF GitHub issues
- Eclipse Bugzilla (legacy)
- EMF mailing list archives

Search terms: "java.time", "LocalDate", "Instant", "modern date", "JSR-310"

### Proposal Points

1. **Motivation:**
   - `java.util.Date` is deprecated in favor of `java.time.*`
   - Many data formats (JSON Schema, OpenAPI, etc.) use ISO-8601
   - Current workaround is custom EDataTypes in every project

2. **Scope:**
   - `ELocalDate` - `java.time.LocalDate`
   - `ELocalTime` - `java.time.LocalTime`
   - `ELocalDateTime` - `java.time.LocalDateTime`
   - `EInstant` - `java.time.Instant`
   - `EDuration` - `java.time.Duration`
   - `EUUID` - `java.util.UUID` (bonus)

3. **Compatibility:**
   - Java 8+ required (already the case for modern EMF)
   - No breaking changes to existing types
   - `EDate` remains for backwards compatibility

4. **Implementation:**
   - Add to `EcorePackage`
   - Provide default converters using ISO-8601 format
   - XMI serialization as strings

---

## Status

- [ ] Team discussion
- [ ] Check EMF issue tracker for existing proposals
- [ ] Decision: Fennec package vs EMF contribution vs both
- [ ] If Fennec package: Create `org.eclipse.fennec.codec.formats`
- [ ] If EMF contribution: Prepare proposal
- [ ] Update JSON Schema converters
- [ ] Tests
