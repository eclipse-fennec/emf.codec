# Code Conventions & Patterns

[← Test Coverage](19-test-coverage.md) | [Next: Open Questions →](99-open-questions.md)

---

## 1. Overview

This document defines code conventions, patterns, and helper classes for the codec-v2 implementation. It serves as:
- A reference for developers writing codec code
- A knowledge base for the code-reviewer agent
- A living document updated through code review feedback

---

## 2. Java Version & Language Features

**Target: Java 17+**

### 2.1 Required Idioms

| Feature | Usage | Example |
|---------|-------|---------|
| Records | Immutable data carriers, DTOs | `record TypeMapping(String discriminator, EClass target) {}` |
| Pattern matching instanceof | Type checks with binding | `if (obj instanceof String s) { ... }` |
| Switch expressions | Multi-branch returns | `return switch(strategy) { case URI -> ...; };` |
| Text blocks | Multi-line strings (tests, JSON) | `String json = """ { "name": "x" } """;` |
| Sealed classes | Closed type hierarchies | `sealed interface Aspect permits ClassAspect, FeatureAspect {}` |

### 2.2 Standard Library Preferences

**Always prefer Java standard library over custom implementations:**

```java
// Null handling - use java.util.Objects
Objects.requireNonNull(value, "value must not be null");
Objects.requireNonNullElse(value, defaultValue);
Objects.requireNonNullElseGet(value, () -> computeDefault());
Objects.equals(a, b);  // null-safe
Objects.hash(f1, f2, f3);  // for hashCode()

// Collections - use factory methods
List.of(a, b, c);      // immutable
Set.of(a, b, c);       // immutable
Map.of(k1, v1, k2, v2); // immutable
Map.entry(k, v);       // immutable entry

// Maps - use compute methods
map.getOrDefault(key, defaultValue);
map.computeIfAbsent(key, k -> new ArrayList<>());
map.putIfAbsent(key, value);
map.merge(key, value, (old, neu) -> old + neu);

// Strings
String.join(delimiter, elements);
str.isBlank();    // better than isEmpty() for whitespace
str.strip();      // Unicode-aware trim
str.lines();      // stream of lines
str.formatted(args);  // like String.format but instance method

// Arrays
Arrays.equals(a, b);
Arrays.copyOf(array, length);
Arrays.stream(array);
```

---

## 3. Helper Classes

### 3.1 Design Principles

Helper classes should be:
1. **Stateless** - all methods static, no instance state
2. **Pure** - output depends only on input, no side effects
3. **Focused** - single responsibility
4. **Testable** - can be unit tested in isolation

**Structure:**
```java
public final class XxxHelper {

    private XxxHelper() {
        // Utility class - prevent instantiation
    }

    public static ReturnType methodName(ParamType param) {
        Objects.requireNonNull(param, "param must not be null");
        // implementation
    }
}
```

### 3.2 Existing Helper Classes

| Class | Package | Responsibility |
|-------|---------|----------------|
| `EcoreHelper` | `o.e.f.model.metadata.utils` | EMF Ecore utilities (find EClass, EPackage, features) |
| `AnnotationParseHelper` | `o.e.f.codec.metadata.util` | Parse EAnnotation details to typed values (boolean, string, enum) |

### 3.3 Planned Helper Classes

| Class | Package | Responsibility | Status |
|-------|---------|----------------|--------|
| `ConfigMergeHelper` | `o.e.f.codec.config` | Merge configuration from multiple sources | TODO |

### 3.4 AnnotationParseHelper API

The `AnnotationParseHelper` class provides methods for parsing EAnnotation detail values:

#### Boolean Parsing
```java
// Parse boolean with default
boolean enabled = AnnotationParseHelper.parseBoolean(details, "key", false);

// Call consumer if key is present
AnnotationParseHelper.ifBooleanPresent(details, "key", config::setEnabled);
```

#### String Parsing
```java
// Get string with default
String value = AnnotationParseHelper.getString(details, "key", "default");

// Call consumer if non-blank value present
AnnotationParseHelper.ifStringPresent(details, "key", config::setValue);
```

#### Enum Parsing
```java
// Parse enum with default (uses Locale.ROOT for case conversion)
TypeStrategy strategy = AnnotationParseHelper.parseEnum(
    details, "typeStrategy", TypeStrategy.class, TypeStrategy.URI);

// Call consumer if valid enum value present
AnnotationParseHelper.ifEnumPresent(details, "typeStrategy",
    TypeStrategy.class, config::setStrategy);
```

#### Key Presence Checks
```java
// Check if any key is present
if (AnnotationParseHelper.hasAnyKey(details, "key1", "key2", "key3")) { ... }

// Check if all keys are present
if (AnnotationParseHelper.hasAllKeys(details, "key1", "key2")) { ... }
```

#### Prefix Extraction
```java
// Extract suffix from prefixed key (e.g., "inlineMapping.friend" -> "friend")
String suffix = AnnotationParseHelper.extractSuffix(key, "inlineMapping.");
```

### 3.5 When to Extract a Helper

Extract to a helper class when you see:
- **Same pattern 3+ times** - e.g., boolean parsing from map
- **Complex logic reused** - e.g., enum parsing with Locale.ROOT
- **Cross-cutting concern** - e.g., null-safe value extraction

Do NOT extract when:
- **Used only once** - inline is clearer
- **Tightly coupled to instance state** - keep as private method
- **Simple delegation** - e.g., `map.get(key)` doesn't need wrapping

---

## 4. Null Safety Patterns

### 4.1 Method Parameters

```java
// Public API methods - validate immediately
public void process(EClass eClass, Map<String, String> options) {
    Objects.requireNonNull(eClass, "eClass must not be null");
    Objects.requireNonNull(options, "options must not be null");
    // ...
}

// Internal methods - use @Nullable annotation or document
private void processInternal(EClass eClass, @Nullable String hint) {
    // hint may be null - handle it
}
```

### 4.2 Return Values

```java
// Prefer Optional for "might not exist" semantics
public Optional<EClass> findEClass(String name) {
    // ...
}

// Use @Nullable annotation when Optional is too heavy
@Nullable
public String getAnnotationValue(EAnnotation ann, String key) {
    return ann.getDetails().get(key);
}

// Never return null for collections - return empty
public List<EClass> getSuperTypes(EClass eClass) {
    if (eClass == null) {
        return List.of();  // not null!
    }
    return eClass.getESuperTypes();
}
```

### 4.3 Common Null-Safe Patterns

```java
// Map access
String value = map.getOrDefault(key, "");

// Chained access - use Optional or explicit checks
// Bad: object.getFoo().getBar().getValue()  // NPE risk
// Good:
Optional.ofNullable(object)
    .map(Obj::getFoo)
    .map(Foo::getBar)
    .map(Bar::getValue)
    .orElse(defaultValue);

// Or explicit (faster, clearer for simple cases):
if (object != null && object.getFoo() != null) {
    Bar bar = object.getFoo().getBar();
    if (bar != null) {
        return bar.getValue();
    }
}
return defaultValue;
```

---

## 5. EMF-Specific Patterns

### 5.1 EAnnotation Access

```java
// Get annotation by source
public static Optional<EAnnotation> getAnnotation(EModelElement element, String source) {
    return Optional.ofNullable(element.getEAnnotation(source));
}

// Get detail value with default
public static String getDetail(EAnnotation ann, String key, String defaultValue) {
    if (ann == null) {
        return defaultValue;
    }
    return ann.getDetails().getOrDefault(key, defaultValue);
}

// Check if annotation has any of the keys
public static boolean hasAnyKey(EAnnotation ann, String... keys) {
    if (ann == null) {
        return false;
    }
    EMap<String, String> details = ann.getDetails();
    for (String key : keys) {
        if (details.containsKey(key)) {
            return true;
        }
    }
    return false;
}
```

### 5.2 EClass Navigation

```java
// Get all features including inherited
public static List<EStructuralFeature> getAllFeatures(EClass eClass) {
    return eClass.getEAllStructuralFeatures();
}

// Find feature by name (case-insensitive option)
public static Optional<EStructuralFeature> findFeature(EClass eClass, String name) {
    return eClass.getEAllStructuralFeatures().stream()
        .filter(f -> f.getName().equals(name))
        .findFirst();
}

// Check if EClass is subtype of another
public static boolean isSubtypeOf(EClass subtype, EClass supertype) {
    return supertype.isSuperTypeOf(subtype);
}
```

### 5.3 Memory Safety with EMF

```java
// Always remove adapters when done
try {
    eObject.eAdapters().add(adapter);
    // work with adapter
} finally {
    eObject.eAdapters().remove(adapter);
}

// Don't hold references to EObjects in static fields
// Bad:
private static EClass cachedClass;  // memory leak!

// Good: Use weak references or don't cache
private static final Map<String, WeakReference<EClass>> cache = new WeakHashMap<>();
```

---

## 6. Testing Patterns

### 6.1 Test Structure

```java
@DisplayName("HelperClass")
class HelperClassTest {

    @Nested
    @DisplayName("methodName")
    class MethodNameTests {

        @Test
        @DisplayName("returns X when Y")
        void returnsXWhenY() {
            // Arrange
            var input = ...;

            // Act
            var result = HelperClass.methodName(input);

            // Assert
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("throws when null input")
        void throwsWhenNullInput() {
            assertThatNullPointerException()
                .isThrownBy(() -> HelperClass.methodName(null))
                .withMessage("input must not be null");
        }
    }
}
```

### 6.2 Test Data

```java
// Use text blocks for JSON
String json = """
    {
        "_type": "http://example.org#//Person",
        "name": "John",
        "age": 30
    }
    """;

// Use records for test fixtures
record TestCase(String name, String input, String expected) {}

List<TestCase> testCases = List.of(
    new TestCase("simple", "input1", "expected1"),
    new TestCase("complex", "input2", "expected2")
);
```

---

## 7. Code Review Feedback Log

This section tracks patterns identified during code reviews that should become conventions.

| Date | Pattern | Description | Status |
|------|---------|-------------|--------|
| 2026-01-24 | Initial conventions | Document created | Active |
| 2026-01-24 | AnnotationParseHelper | Extracted ~80 lines of repetitive enum/boolean/string parsing from CodecAspectProvider | Implemented |
| 2026-01-24 | Null safety checks | Added Objects.requireNonNull() to all public API methods in CodecAspectProvider | Implemented |
| 2026-01-24 | Pattern matching instanceof | Updated buildFeatureAspect() to use Java 16+ pattern matching | Implemented |
| 2026-01-24 | Locale.ROOT | All enum parsing now uses Locale.ROOT via AnnotationParseHelper | Implemented |
| 2026-01-24 | No wildcard imports | Added rule to code-reviewer agent | Active |
| 2026-01-24 | License header rule | Added rule to code-reviewer agent with current year requirement | Active |

---

## 8. Feedback Loop

This document evolves based on code review findings:

```
┌─────────────────────┐
│  code-reviewer      │
│  reviews code       │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  Identifies new     │
│  pattern/helper     │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  Reports to user    │
│  with suggestion    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  User approves      │
│  the change         │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  Update this doc    │
│  (Section 3, 4, 7)  │
└─────────────────────┘
```

**Do NOT directly modify this document** - always report suggestions to the user for approval first.

---

[Next: Open Questions →](99-open-questions.md)
