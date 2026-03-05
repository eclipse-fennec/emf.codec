# JSON Schema Deserialization: Findings & Comparison

Comparison of `JsonSchemaToEPackageConverter` (fennec-codec) vs `EnhancedJsonSchemaToEPackageDeserializer` (gecko-codec) deserialization logic.

**Date:** 2026-03-04
**Files compared:**
- Fennec: `org.eclipse.fennec.codec.jsonschema/src/org/eclipse/fennec/codec/jsonschema/v2/converter/JsonSchemaToEPackageConverter.java`
- Gecko: `org.eclipse.fennec.codec.jsonschema/src/org/eclipse/fennec/codec/jsonschema/readers/EnhancedJsonSchemaToEPackageDeserializer.java`

---

## Issue 1: `allOf` inline property merging is broken (DATA LOSS)

**Severity:** High — silently drops properties

**Fennec** (`createClassWithAllOf`, line 1197):

Only picks the **first** non-`$ref` element from `allOf` and ignores all subsequent inline schemas:

```java
for (int i = 0; i < allOfNode.size(); i++) {
    JsonNode allOf = allOfNode.get(i);
    if (allOf.has("$ref")) {
        String refPath = allOf.get("$ref").asString();
        String referencedSchemaName = extractSchemaNameFromRef(refPath);
        parentNames.add(referencedSchemaName);
    } else {
        if (eClass == null) {  // ← BUG: only picks the FIRST non-ref element
            eClass = createEClass(allOf, name, qualifiedName);
        }
        // subsequent inline schemas are silently ignored!
    }
}
```

**Gecko** (`handleAllOf`, line 1292):

Properly merges properties from ALL inline schemas, checks for duplicate properties, and accumulates `required` from all elements:

```java
for (JsonNode allOfOption : allOfArray) {
    if (allOfOption.has("$ref")) {
        String refPath = allOfOption.get("$ref").asString();
        String referencedSchemaName = extractSchemaNameFromRef(refPath);
        deferredReferences.add(new DeferredAllOfReference(eClass, referencedSchemaName));
    }

    // Merge properties from ALL inline schemas
    if (allOfOption.has("properties")) {
        JsonNode propsNode = allOfOption.get("properties");
        for (String propName : propsNode.propertyNames()) {
            // Check for duplicates before adding
            boolean exists = false;
            for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
                if (feature.getName().equals(propName)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                EStructuralFeature feature = createStructuralFeature(
                    propsNode.get(propName), propName, qualifiedName);
                if (feature != null) {
                    eClass.getEStructuralFeatures().add(feature);
                }
            }
        }
    }

    // Collect required from ALL allOf elements
    if (allOfOption.has("required")) {
        ArrayNode reqArray = (ArrayNode) allOfOption.get("required");
        for (JsonNode req : reqArray) {
            allRequiredProps.add(req.asString());
        }
    }
}

// Apply combined required properties after merging
for (String requiredProp : allRequiredProps) {
    for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
        if (feature.getName().equals(requiredProp)) {
            feature.setLowerBound(1);
            break;
        }
    }
}
```

**Example of data loss:**

```json
{
  "allOf": [
    { "$ref": "#/$defs/BaseAddress" },
    { "properties": { "street": { "type": "string" } }, "required": ["street"] },
    { "properties": { "city": { "type": "string" } }, "required": ["city"] }
  ]
}
```

Fennec result: EClass with only `street` (from first inline schema). `city` is silently dropped.
Gecko result: EClass with both `street` and `city`, both marked as required.

**Fix:** Rewrite `createClassWithAllOf` to iterate ALL allOf elements, merge properties with deduplication, and accumulate required fields across all inline schemas.

---

## Issue 2: `anyOf` at property level creates unnecessary artificial parent classes (CLASS BLOAT)

**Severity:** Medium — creates artificial classes that serve no real purpose

**Fennec** (`createMultiValueReference`, line 1556):

For every property-level `anyOf` with `$ref` entries, fennec:
1. Looks up **raw JSON schema nodes** for each `$ref`
2. Extracts common properties across those raw schemas
3. Creates an **artificial parent class** from those common properties
4. Registers the artificial parent in `classifierMap`

```java
private EReference createMultiValueReference(JsonNode jsonNode, String name, String contextPath) {
    // ... collects all $ref paths ...

    // Creates artificial parent from common properties — unnecessary!
    EClass parent = createParentFromCommonProperties(refClassesNodes, contextPath);
    reference.setEType(parent);
    return reference;
}
```

`createParentFromCommonProperties` (line 1592) then creates a new EClass with common properties extracted from referenced schemas. This means for `"anyOf": [{"$ref": "#/$defs/Cat"}, {"$ref": "#/$defs/Dog"}]`, fennec creates:
- An `ArtificialClassifierN` with properties common to Cat and Dog
- This class is NOT a proper supertype of Cat/Dog in the type hierarchy

**Gecko:**

Does NOT handle `anyOf` at property level in `createStructuralFeature` — the feature just isn't created. The `anyOf` at class level is stored as annotation only:

```java
private void handleAnyOf(JsonNode schemaNode, EClass eClass, String qualifiedName) {
    // ... checks if only required constraints ...
    // In all cases, just stores as annotation
    addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "anyOf", anyOfArray.toString());
}
```

### Sub-issue 2a: Duplicate anyOf with identical refs

If the same `anyOf: [Cat, Dog]` appears on two different properties (e.g., `favoritePet` and `secondPet`), the `parentClassMaps` cache (keyed by `Map<String, JsonNode>` of common properties) **does reuse** the same artificial class. So duplicates are avoided — but the cache key relies on `JsonNode.equals()` / `hashCode()` via `HashMap`, which is fragile and order-dependent.

### Sub-issue 2b: Referenced classes share a common supertype via `allOf` — ignored

Consider:
```json
{
  "$defs": {
    "Animal": { "type": "object", "properties": { "name": {"type":"string"} } },
    "Cat": { "allOf": [{"$ref":"#/$defs/Animal"}, {"properties":{"purrs":{"type":"boolean"}}}] },
    "Dog": { "allOf": [{"$ref":"#/$defs/Animal"}, {"properties":{"barks":{"type":"boolean"}}}] },
    "Owner": {
      "type": "object",
      "properties": {
        "pet": { "anyOf": [{"$ref":"#/$defs/Cat"}, {"$ref":"#/$defs/Dog"}] }
      }
    }
  }
}
```

`createMultiValueReference` looks at the **raw JSON nodes** for Cat and Dog. Since Cat's raw schema is an `allOf` node (not a `type: object` with `properties`), the check `haveAllProperties = refClassesNodes.values().stream().allMatch(jn -> jn.has("properties"))` returns **false**. Result: an **empty artificial class** with no features, completely ignoring that both Cat and Dog extend Animal.

The correct behavior would be: look at the already-resolved Cat and Dog EClasses in `classifierMap`, check their `getESuperTypes()`, and if they share a common supertype (`Animal`), use Animal as the reference type instead of creating an artificial class.

### Sub-issue 2c: Processing order — supertypes may not be resolved yet

Even if the code looked at the resolved EClass hierarchy, there is a **timing problem**. The `allOf` parent resolution (Cat extends Animal, Dog extends Animal) happens in `resolveAllOfReferences()`, which runs **after** all definitions are processed. So at the time `createMultiValueReference` runs for `Owner.pet`:

- Cat and Dog EClasses exist in `classifierMap`
- But their `getESuperTypes()` is still **empty** — the `allOfRefMap` entries haven't been resolved yet
- Therefore, even a hierarchy-aware check would find no common supertype

This means the fix needs a **two-phase approach**: during initial processing, just record the anyOf `$ref` targets on the EReference (as a deferred reference). Then during the resolution phase — after `resolveAllOfReferences()` has wired up the inheritance — walk up the resolved type hierarchies to find the common supertype.

### Sub-issue 2d: `anyOfRefMap` is dead code

`anyOfRefMap` is declared (line 85) and initialized (line 386), but **never populated** anywhere. `resolveAnyOfReferences()` iterates over it but it's always empty, making it a no-op. This means the artificial parent created by `createParentFromCommonProperties` has **no supertype relationship** with Cat/Dog — it's a disconnected class in the type hierarchy.

### Summary of sub-issues

| Sub-issue | What happens | Correct? |
|-----------|-------------|----------|
| 2a: Same anyOf [Cat, Dog] used twice | Reuses artificial class via `parentClassMaps` cache | Partially (fragile key) |
| 2b: Cat/Dog extend Animal via allOf | Creates empty artificial class, ignores Animal | **No** |
| 2c: Processing order | Supertypes not resolved yet during anyOf processing | **Timing bug** |
| 2d: `anyOfRefMap` / `resolveAnyOfReferences` | Never populated, dead code — no type hierarchy link | **Bug** |

### Recommended fix

Replace `createMultiValueReference` and `createParentFromCommonProperties` with a deferred resolution approach:

1. **During initial processing** (`createStructuralFeature` for anyOf): create the EReference, store the list of `$ref` target names as annotation or in a deferred map, and set the EReference type to `EObject` temporarily.

2. **During resolution phase** (new method, runs AFTER `resolveAllOfReferences`): for each deferred anyOf reference:
   - Look up the resolved EClasses for all `$ref` targets
   - Walk up their `getESuperTypes()` chains to find the **lowest common supertype**
   - If a common supertype exists → use it as the EReference type
   - If no common supertype exists → only then create an artificial parent class, and wire the referenced classes as its subtypes via `anyOfRefMap`

3. **Clean up**: remove `parentClassMaps` and `createParentFromCommonProperties`. Either repurpose or remove `anyOfRefMap`.

---

## Issue 3: Multi-type `"null"` creates unnecessary variant classes (CLASS BLOAT)

**Severity:** Medium — very common pattern causes 3 classes instead of 1 attribute

The pattern `"type": ["string", "null"]` is extremely common in JSON Schema and simply means "nullable string". Both fennec and gecko have this issue.

**Current behavior** (`handleMultiTypeProperty`, fennec line 1645, gecko line 1092):

For `"type": ["string", "null"]`, creates:
- 1 abstract base class (`ArtificialClassifierN`)
- 1 string variant (`ArtificialClassifierNVariant0` with `value: EString`)
- 1 null variant (`ArtificialClassifierNVariant1` with `value: EJavaObject`)
- 1 containment EReference to the base

That's **3 artificial classes and 1 reference** instead of a simple nullable `EAttribute`.

**Expected behavior:**

Filter out `"null"` from the type array. If only one non-null type remains, create a simple `EAttribute` with `lowerBound=0` (nullable). Only create the union hierarchy if there are 2+ non-null types.

```java
// Pseudocode for fix:
List<String> nonNullTypes = filterOut(typeArray, "null");
boolean isNullable = nonNullTypes.size() < typeArray.size();

if (nonNullTypes.size() == 1) {
    // Simple nullable attribute
    EAttribute attr = createEAttribute();
    attr.setName(name);
    attr.setEType(mapJsonTypeToEcore(nonNullTypes.get(0)));
    attr.setLowerBound(isNullable ? 0 : 1);
    return attr;
}
// else: proceed with union hierarchy for genuinely multi-typed properties
```

---

## Issue 4: Context-specific variant base class always created (MINOR BLOAT)

**Severity:** Low — creates empty abstract class

**Fennec** (`createContextSpecificVariants`, line 878):

The abstract base class is **always** created regardless of whether there are common properties:

```java
EClass baseClass = ecoreFactory.createEClass();
String baseClassName = capitalizeFirst(name) + "Base";
baseClass.setName(baseClassName);
baseClass.setAbstract(true);
// ... always added to classifierMap ...

boolean hasCommonProperties = analysis.commonPropertyRatio >= SIMILARITY_THRESHOLD
    && !analysis.commonProperties.isEmpty();
// If no common properties, baseClass exists but is completely empty
```

When `commonPropertyRatio < 0.3` or there are no common properties, the base class has zero structural features — it's an empty abstract class that serves only as a type marker.

**Fix:** When there are no common properties, consider whether the base class is truly necessary. If only one variant exists, skip the base entirely. If variants share no structure, consider using an EInterface or storing the oneOf relationship as annotation.

---

## Issue 5: `$ref` default containment differs (SEMANTIC)

**Severity:** Medium — affects object graph semantics

| | Fennec | Gecko |
|---|--------|-------|
| Default containment for `$ref` | `false` (line 1402) | `true` (line 854) |

**Fennec** (`createRefFeature`, line 1399):
```java
reference.setContainment(false); // $ref typically means non-containment reference
```

**Gecko** (`createStructuralFeature`, line 845):
```java
reference.setContainment(true); // Assume containment for now
```

In JSON Schema, `$ref` within `properties` typically means the referenced object is **embedded/contained** in the parent. Non-containment references (cross-references to shared objects) are not a native JSON Schema concept — they are an EMF concept.

**Fix:** Default to `containment=true` for `$ref` in properties, matching gecko behavior. Non-containment should only be used when explicitly indicated (e.g., via a custom annotation or specific schema pattern).

---

## Summary

| # | Issue | Severity | Type | Fennec Location |
|---|-------|----------|------|-----------------|
| 1 | `allOf` drops inline properties after first | **High** | Data loss | `createClassWithAllOf` (line 1197) |
| 2 | `anyOf` property-level creates artificial parents | **Medium** | Class bloat | `createMultiValueReference` (line 1556) |
| 3 | Multi-type `"null"` creates 3 classes | **Medium** | Class bloat | `handleMultiTypeProperty` (line 1645) |
| 4 | Empty base class for context variants | **Low** | Minor bloat | `createContextSpecificVariants` (line 878) |
| 5 | `$ref` default containment is `false` | **Medium** | Semantic | `createRefFeature` (line 1399) |

### Recommended fix order:
1. Issue 1 — `allOf` data loss (highest impact, properties are silently dropped)
2. Issue 5 — `$ref` containment default (semantic correctness)
3. Issue 3 — Multi-type `"null"` handling (most common source of class bloat)
4. Issue 2 — `anyOf` artificial parents (class bloat)
5. Issue 4 — Empty base class (minor cleanup)
