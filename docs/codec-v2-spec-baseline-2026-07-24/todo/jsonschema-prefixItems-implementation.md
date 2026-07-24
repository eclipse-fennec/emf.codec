# JSON Schema `prefixItems` Implementation Plan

## Overview

`prefixItems` is a JSON Schema keyword (Draft 2020-12) for **tuple validation** - arrays with a fixed sequence of items where each position has a specific type.

**Example JSON Schema:**
```json
{
  "type": "array",
  "prefixItems": [
    { "type": "string" },
    { "type": "integer" },
    { "type": "boolean" }
  ]
}
```

Represents tuples like `["hello", 42, true]`.

---

## EMF Challenge

EMF arrays are **homogeneous** - all elements must be the same type (`EList<T>`). There's no native tuple support.

---

## Proposed Solution: EClass with Positional Attributes

Convert the tuple schema to an EClass where each position becomes a named attribute.

### Deserialization (JSON Schema → EPackage)

**Input:**
```json
{
  "definitions": {
    "Coordinate": {
      "type": "array",
      "prefixItems": [
        { "type": "number", "description": "latitude" },
        { "type": "number", "description": "longitude" },
        { "type": "number", "description": "altitude" }
      ]
    }
  }
}
```

**Output EClass:**
```
EClass Coordinate {
  @EAnnotation(source="http://fennec.eclipse.org/jsonschema", details={tuple=true, tupleIndex=0})
  item0: EDouble  // or "latitude" if title/description available

  @EAnnotation(source="http://fennec.eclipse.org/jsonschema", details={tupleIndex=1})
  item1: EDouble  // or "longitude"

  @EAnnotation(source="http://fennec.eclipse.org/jsonschema", details={tupleIndex=2})
  item2: EDouble  // or "altitude"
}
```

### Attribute Naming Strategy

1. **Use `title`** from prefixItems schema if available
2. **Use `description`** as fallback (extract first word or camelCase)
3. **Default to `item0`, `item1`, `item2`...** if no metadata

### Serialization (EPackage → JSON Schema)

When an EClass has the `tuple=true` annotation:

1. Generate `"type": "array"` instead of `"type": "object"`
2. Generate `prefixItems` array from attributes ordered by `tupleIndex`
3. Optionally generate `"items": false` to disallow additional items

**Output:**
```json
{
  "Coordinate": {
    "type": "array",
    "prefixItems": [
      { "type": "number" },
      { "type": "number" },
      { "type": "number" }
    ],
    "items": false
  }
}
```

---

## Implementation Steps

### Phase 1: Deserialization (JsonSchemaToEPackageConverter)

1. **Detect tuple pattern** in `processDefinition()`:
   ```java
   if (schemaNode.has("prefixItems") && isArrayType(schemaNode)) {
       return createTupleClass(name, schemaNode);
   }
   ```

2. **Create `createTupleClass()` method:**
   ```java
   private EClass createTupleClass(String name, JsonNode schemaNode) {
       EClass tupleClass = ecoreFactory.createEClass();
       tupleClass.setName(name);

       // Mark as tuple
       addEAnnotation(tupleClass, JSONSCHEMA_SOURCE, "tuple", "true");

       ArrayNode prefixItems = (ArrayNode) schemaNode.get("prefixItems");
       for (int i = 0; i < prefixItems.size(); i++) {
           JsonNode itemSchema = prefixItems.get(i);
           String attrName = deriveTupleAttributeName(itemSchema, i);
           EAttribute attr = createAttributeFromSchema(attrName, itemSchema);

           // Store tuple index
           addEAnnotation(attr, JSONSCHEMA_SOURCE, "tupleIndex", String.valueOf(i));

           tupleClass.getEStructuralFeatures().add(attr);
       }

       // Handle "items" for additional items beyond prefixItems
       if (schemaNode.has("items")) {
           // Store for round-trip
           addEAnnotation(tupleClass, JSONSCHEMA_SOURCE, "items",
                         schemaNode.get("items").toString());
       }

       return tupleClass;
   }
   ```

3. **Attribute name derivation:**
   ```java
   private String deriveTupleAttributeName(JsonNode itemSchema, int index) {
       if (itemSchema.has("title")) {
           return sanitizeName(itemSchema.get("title").asText());
       }
       if (itemSchema.has("description")) {
           String desc = itemSchema.get("description").asText();
           // Extract first word or use as-is if short
           return sanitizeName(desc.split("\\s+")[0]);
       }
       return "item" + index;
   }
   ```

### Phase 2: Serialization (EPackageToJsonSchemaConverter)

1. **Detect tuple EClass** in `writeClassDefinition()`:
   ```java
   String isTuple = extractAnnotationDetail(eClass, JSONSCHEMA_SOURCE, "tuple");
   if ("true".equals(isTuple)) {
       writeTupleDefinition(eClass, gen);
       return;
   }
   ```

2. **Create `writeTupleDefinition()` method:**
   ```java
   private void writeTupleDefinition(EClass tupleClass, JsonGenerator gen) throws IOException {
       gen.writeStartObject();

       gen.writeStringProperty("type", "array");

       // Collect attributes ordered by tupleIndex
       List<EAttribute> tupleAttrs = tupleClass.getEAttributes().stream()
           .filter(a -> getAnnotationDetail(a, JSONSCHEMA_SOURCE, "tupleIndex") != null)
           .sorted(Comparator.comparingInt(a ->
               Integer.parseInt(getAnnotationDetail(a, JSONSCHEMA_SOURCE, "tupleIndex"))))
           .toList();

       gen.writeName("prefixItems");
       gen.writeStartArray();
       for (EAttribute attr : tupleAttrs) {
           writeAttributeAsArrayItem(attr, gen);
       }
       gen.writeEndArray();

       // Write "items" if stored
       String items = extractAnnotationDetail(tupleClass, JSONSCHEMA_SOURCE, "items");
       if (items != null) {
           gen.writeName("items");
           // Parse and write stored JSON
           writeRawJson(items, gen);
       }

       gen.writeEndObject();
   }
   ```

### Phase 3: JsonSchemaKeywords Update

Update `JsonSchemaKeywords.java`:

```java
// Move prefixItems from UNSUPPORTED_KEYWORDS to FULLY_SUPPORTED_KEYWORDS
private static final Set<String> ARRAY_KEYWORDS = Set.of(
    "items", "minItems", "maxItems", "uniqueItems", "prefixItems"
);
```

### Phase 4: Tests

Create `TupleSchemaTest.java`:

1. **Read tuple schema** - verify EClass with positional attributes
2. **Write tuple EClass** - verify `prefixItems` output
3. **Round-trip** - read → write → read produces equivalent result
4. **Mixed tuple** - different types at each position
5. **Tuple with `items: false`** - disallow additional items
6. **Tuple with `items: schema`** - allow typed additional items
7. **Nested tuple** - tuple containing references to other types
8. **Named positions** - use title/description for attribute names

---

## Edge Cases

1. **Empty `prefixItems`**: Create empty EClass with `tuple=true` annotation
2. **`prefixItems` with `$ref`**: Resolve reference, create EReference instead of EAttribute
3. **`prefixItems` + `items`**:
   - `items: false` → no additional items allowed
   - `items: {schema}` → additional items of that type (store in annotation)
4. **Nested tuples**: Tuple containing another tuple → nested EClass references

---

## Alternatives Considered

### Alternative 1: EJavaObject Array
- Lose type safety
- Simple implementation
- Poor EMF tooling support
- **Rejected**: Doesn't preserve type information

### Alternative 2: Custom EDataType
- Create `ETuple` datatype with custom serialization
- Complex implementation
- Requires custom instance class
- **Rejected**: Too complex, poor tooling integration

### Alternative 3: Marker Interface Pattern
- Create interface `ITuple` that tuple classes implement
- Provides runtime identification
- **Could be added later** if needed for codec integration

---

## Integration with Codec V2

When deserializing JSON data using a tuple-based EPackage:

1. Codec detects array in JSON
2. Checks target type annotation for `tuple=true`
3. Creates EObject instance of tuple EClass
4. Maps array positions to attributes by `tupleIndex`

This requires codec v2 awareness of tuple pattern - **separate implementation task**.

---

## Status

- [ ] Phase 1: Deserialization
- [ ] Phase 2: Serialization
- [ ] Phase 3: JsonSchemaKeywords update
- [ ] Phase 4: Tests
- [ ] Codec V2 integration (separate task)
