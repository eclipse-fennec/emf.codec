# Fennec Codec — Security Analysis & Hardening Guide

Security analysis of the Fennec Codec with attack vector assessment, mitigation planning, and references to BSI guidelines.

## Table of Contents

1. [Threat Model](#1-threat-model)
2. [Attack Vectors](#2-attack-vectors)
3. [Existing Mitigations](#3-existing-mitigations)
4. [Mitigation Plan](#4-mitigation-plan)
5. [BSI TR-03185 Mapping](#5-bsi-tr-03185-mapping)
6. [Embedder Guide](#6-embedder-guide)
7. [References](#7-references)

---

## 1. Threat Model

### 1.1 System Context

The Fennec Codec operates as an **embedded Java library** within a host application. It processes:

- **JSON/YAML/CBOR/BSON streams** — parsed by Jackson 3.1.0 and format-specific libraries
- **EMF models** (in-memory EObject graphs) — serialized and deserialized via `CodecResource`
- **EAnnotation-based configuration** — parsed from `http://eclipse.org/fennec/codec` annotations
- **Custom value handlers** — Java implementations registered by the host application or via OSGi services
- **Type discriminator mappings** — external type-to-EClass registries

The codec is a **data format translation layer** — it converts between EMF object graphs and wire formats (JSON, YAML, CBOR, BSON). The primary threats are **denial of service via resource exhaustion**, **type confusion via deserialization**, and **information disclosure**.

### 1.2 Trust Boundaries

```
┌──────────────────────────────────────────────────────────────────┐
│  Host Application (trusted)                                      │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │  Fennec Codec                                            │    │
│  │  ┌────────────────┐  ┌────────────────┐                  │    │
│  │  │ Jackson Parser  │  │ Deserializer   │                  │    │
│  │  │ (JSON/YAML/    │  │ (Entry-based)  │                  │    │
│  │  │  CBOR/BSON)    │  └───────┬────────┘                  │    │
│  │  └────────────────┘          │                            │    │
│  │  ┌───────────────────────────┼───────────────────────┐    │    │
│  │  │ Trust Boundary            │                       │    │    │
│  │  │  ┌──────────────────┐  ┌──┴──────────┐            │    │    │
│  │  │  │ Custom Value     │  │ Input Stream │            │    │    │
│  │  │  │ Handlers (Java)  │  │ (untrusted)  │            │    │    │
│  │  │  └──────────────────┘  └──────────────┘            │    │    │
│  │  │  ┌──────────────────┐  ┌──────────────┐           │    │    │
│  │  │  │ EAnnotation      │  │ Type Discrim. │           │    │    │
│  │  │  │ Config (model)   │  │ Values (JSON) │           │    │    │
│  │  │  └──────────────────┘  └──────────────┘            │    │    │
│  │  └───────────────────────────────────────────────────┘    │    │
│  └──────────────────────────────────────────────────────────┘    │
│                                                                  │
│  EMF Models  │  EPackage Registry  │  OSGi Service Registry      │
└──────────────────────────────────────────────────────────────────┘
```

### 1.3 Actors

| Actor | Trust Level | Description |
|-------|:-----------:|-------------|
| Host application | High | Creates codec, provides EMF models and configuration |
| Input stream provider | Variable | Provides JSON/YAML/CBOR/BSON data for deserialization |
| EAnnotation author | Variable | Defines codec behavior via model annotations |
| Custom value handler author | Variable | Implements Java value readers/writers extending codec |
| EPackage provider | Variable | Registers EMF metamodels that define allowed types |
| OSGi bundle deployer | Variable | Can register value handlers via OSGi whiteboard |

**Key question:** If the input stream provider or EPackage provider is **untrusted** — what attacks are possible?

---

## 2. Attack Vectors

### S-1: BSON Unbounded Memory Allocation

| | |
|---|---|
| **Severity** | CRITICAL |
| **Vector** | Oversized BSON payload — `InputStream.readAllBytes()` with no size limit |
| **Impact** | OutOfMemoryError, Denial of Service |
| **Prerequisite** | Attacker controls BSON input stream |
| **File** | `org.eclipse.fennec.codec.bson/src/org/eclipse/fennec/codec/bson/BsonFormatProvider.java` — `BsonStreamReader` constructor |

**Analysis:** The `BsonStreamReader` reads the entire input stream into a byte array via `readAllBytes()` before BSON decoding. A multi-GB payload causes immediate OOM. No size limit is enforced.

**BSI reference:** CWE-400 (Resource Exhaustion), CWE-770 (Allocation of Resources Without Limits)

---

### S-2: Uncontrolled JSON/YAML/CBOR Nesting Depth

| | |
|---|---|
| **Severity** | CRITICAL |
| **Vector** | Deeply nested JSON structures (10,000+ levels) — `readCurrentValue()` recurses without depth tracking |
| **Impact** | StackOverflowError, Denial of Service |
| **Prerequisite** | Attacker controls input stream |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/CodecEObjectDeserializer.java` — `readCurrentValue()`, `readArrayAsList()`, `readObjectAsMap()` |

**Analysis:** The `readCurrentValue()` method recursively calls `readObjectAsMap()` and `readArrayAsList()` without tracking or limiting nesting depth. While Jackson 3.1.0 has a default nesting limit of 1000, the codec does not configure `StreamReadConstraints` and relies entirely on upstream defaults.

**BSI reference:** CWE-674 (Uncontrolled Recursion), CWE-400 (Resource Exhaustion)

---

### S-3: Unbounded Array/Object Size in Deserialization

| | |
|---|---|
| **Severity** | HIGH |
| **Vector** | JSON array with millions of elements — `readArrayAsList()` has no size limit |
| **Impact** | OutOfMemoryError, Denial of Service |
| **Prerequisite** | Attacker controls input stream |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/CodecEObjectDeserializer.java` — `readArrayAsList()`, `readObjectAsMap()` |

**Analysis:** Both `readArrayAsList()` and `readObjectAsMap()` accumulate elements in `ArrayList` / `LinkedHashMap` without any size check. A payload with millions of array elements causes unbounded heap allocation.

**BSI reference:** CWE-400 (Resource Exhaustion), CWE-770 (Allocation Without Limits)

---

### S-4: Type Confusion via Unrestricted EPackage Scanning

| | |
|---|---|
| **Severity** | HIGH |
| **Vector** | Simple class name `"Person"` matches unintended EClass in a different registered EPackage |
| **Impact** | Type confusion — deserialization into wrong EClass, potential data corruption |
| **Prerequisite** | Attacker controls `_type` value in JSON + multiple EPackages registered |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/util/TypeResolutionHelper.java` — `resolveFromSimpleName()` |

**Analysis:** When using the `SIMPLE_NAME` type strategy, the resolver iterates **all** registered EPackages and returns the **first match**. The iteration order is undefined (`EPackage.Registry.INSTANCE.keySet()`), making resolution non-deterministic when multiple packages define classes with the same name.

**BSI reference:** CWE-843 (Access of Resource Using Incompatible Type)

---

### S-5: Reflection-Based Object Instantiation from User Input

| | |
|---|---|
| **Severity** | HIGH |
| **Vector** | Attribute deserialization invokes `targetType.getConstructor(String.class).newInstance(stringValue)` |
| **Impact** | Arbitrary constructor invocation if malicious EDataType is registered |
| **Prerequisite** | Attacker controls both ECORE metamodel and JSON data |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/AttributeDeserializationEntry.java` — `convertObjectFromString()` |

**Analysis:** The fallback conversion path uses reflection to invoke `String` constructors, `valueOf()`, and `parse()` static methods on the target Java class. While `targetType` comes from the EDataType's `instanceClass`, a malicious ECORE model could define a data type whose instance class has side effects in its constructor.

**BSI reference:** CWE-470 (Use of Externally-Controlled Input to Select Classes or Code)

---

### S-6: Missing Jackson StreamReadConstraints Configuration

| | |
|---|---|
| **Severity** | HIGH |
| **Vector** | Default Jackson constraints allow 20MB strings, 1000 nesting depth, 50KB field names |
| **Impact** | Memory exhaustion, stack overflow |
| **Prerequisite** | Attacker controls input stream |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/resource/CodecResource.java` — `doLoadWithFormat()`, `doSaveWithFormat()` |

**Analysis:** `StreamReadConstraints.defaults()` is used without customization. Jackson 3.1.0 defaults allow: max nesting depth 1000, max string length 20MB, max field name length 50KB. These are generous for most codec use cases and should be tightened.

**BSI reference:** CWE-400 (Resource Exhaustion)

---

### S-7: YAML Type Tag Injection (Upstream Dependency)

| | |
|---|---|
| **Severity** | MEDIUM |
| **Vector** | YAML type tags like `!!java.lang.ProcessBuilder` in input |
| **Impact** | Potential remote code execution if Jackson YAML defaults change |
| **Prerequisite** | Attacker controls YAML input |
| **File** | `org.eclipse.fennec.codec.yaml/src/org/eclipse/fennec/codec/yaml/YamlFormatProvider.java` — constructor |

**Analysis:** `YAMLFactory` is created with default configuration. Jackson DataFormat YAML 3.1.0 is safe by default (uses Jackson streaming, not SnakeYAML object construction), but no explicit verification or hardening is applied. If upstream defaults change in a future version, this becomes exploitable.

**BSI reference:** CWE-502 (Deserialization of Untrusted Data)

---

### S-8: CBOR Indefinite-Length Item Abuse

| | |
|---|---|
| **Severity** | MEDIUM |
| **Vector** | CBOR indefinite-length arrays/objects (`0x9f` start byte) with unbounded elements |
| **Impact** | Memory exhaustion |
| **Prerequisite** | Attacker controls CBOR input |
| **File** | `org.eclipse.fennec.codec.cbor/src/org/eclipse/fennec/codec/cbor/CborFormatProvider.java` — constructor |

**Analysis:** `CBORFactory` is created with defaults. CBOR supports indefinite-length items that the parser buffers entirely. No CBOR-specific size limits are enforced.

**BSI reference:** CWE-400 (Resource Exhaustion)

---

### S-9: Numeric Classifier ID Ambiguity

| | |
|---|---|
| **Severity** | MEDIUM |
| **Vector** | Numeric type ID `"3"` matches different EClasses depending on package iteration order |
| **Impact** | Non-deterministic type resolution, type confusion |
| **Prerequisite** | Attacker controls `_type` value + multiple packages with overlapping classifier IDs |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/util/TypeResolutionHelper.java` — `resolveFromNumeric()` |

**Analysis:** When using `NUMERIC` type strategy without a context schema URI, the fallback scans all registered packages in undefined order. Classifier IDs are only unique within a single EPackage.

**BSI reference:** CWE-843 (Access of Resource Using Incompatible Type)

---

### S-10: URI Reference Without Scheme Validation

| | |
|---|---|
| **Severity** | MEDIUM |
| **Vector** | JSON `$ref` value pointing to `file:///etc/passwd` or `http://internal-service/` |
| **Impact** | Server-Side Request Forgery (SSRF), path traversal |
| **Prerequisite** | Attacker controls reference URI in JSON |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/resource/CodecResource.java` — `resolveReference()`, `resolveProxyUri()` |

**Analysis:** Proxy URIs are created from user-supplied strings without validating the URI scheme. The codec creates EMF proxy objects pointing to arbitrary locations. While actual resolution depends on the EMF ResourceSet, the proxy URI itself is stored without validation.

**BSI reference:** CWE-918 (Server-Side Request Forgery)

---

### S-11: Custom Value Handler Registration Without Validation

| | |
|---|---|
| **Severity** | MEDIUM |
| **Vector** | Malicious OSGi bundle registers a `CodecValueWriter`/`CodecValueReader` via whiteboard |
| **Impact** | Arbitrary code execution during serialization/deserialization |
| **Prerequisite** | Attacker can deploy an OSGi bundle |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/resource/CodecResourceFactoryComponent.java` — `addValueWriter()`, `addValueReader()` |

**Analysis:** Custom value handlers are registered via OSGi `@Reference` with `MULTIPLE` cardinality and `DYNAMIC` policy. No validation, permission check, or sandboxing is applied. Any bundle can register handlers that execute arbitrary code during codec operations.

**BSI reference:** CWE-94 (Code Injection)

---

### S-12: Information Disclosure in Error Messages

| | |
|---|---|
| **Severity** | LOW |
| **Vector** | Error messages contain EClass names, feature names, type values, and exception details |
| **Impact** | Schema enumeration, internal structure disclosure |
| **Prerequisite** | Attacker observes error output or logs |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/CodecEObjectDeserializer.java` (multiple), `CodecResource.java` |

**Analysis:** Warning messages include: `"Unknown feature 'X' for EClass Y"`, `"Could not resolve EClass from type value: Z"`, `"Missing required feature(s) for EClass Y: A, B, C"`. These leak internal model structure.

**BSI reference:** CWE-209 (Generation of Error Message Containing Sensitive Information)

---

### S-13: BSON Resource Leak on Exception ✅ FIXED

| | |
|---|---|
| **Severity** | LOW |
| **Vector** | Malformed BSON input causes decode exception — `BsonBinaryReader` not closed |
| **Impact** | Resource leak, gradual memory exhaustion under sustained attack |
| **Prerequisite** | Attacker sends malformed BSON repeatedly |
| **File** | `org.eclipse.fennec.codec.bson/src/org/eclipse/fennec/codec/bson/BsonFormatProvider.java` — `BsonStreamReader` constructor |

**Analysis:** Previously, `BsonBinaryReader` was closed via explicit `reader.close()` instead of try-with-resources. Fixed as part of S-1: `BsonBinaryReader` is now wrapped in try-with-resources, ensuring proper cleanup even if `CODEC.decode()` throws.

**BSI reference:** CWE-404 (Improper Resource Shutdown or Release)

---

### S-14: Thread Safety in Shared Serializer State

| | |
|---|---|
| **Severity** | LOW |
| **Vector** | Concurrent serialization sharing `DiagnosticCollector` |
| **Impact** | Diagnostic cross-contamination between threads |
| **Prerequisite** | Multi-threaded serialization with shared `CodecEObjectSerializer` instance |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/ser/CodecEObjectSerializer.java` |

**Analysis:** The serializer holds a reference to a shared `DiagnosticCollector` via `EffectiveCodecConfig`. In multi-threaded scenarios, diagnostics from different serialization operations could mix.

**BSI reference:** CWE-362 (Race Condition)

---

### S-15: TokenBuffer EMF Context Loss

| | |
|---|---|
| **Severity** | LOW |
| **Vector** | Deferred property replay loses EMF type hints |
| **Impact** | Potential type confusion in edge cases with polymorphic deserialization |
| **Prerequisite** | Complex polymorphic model with deferred properties |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/buffer/CodecTokenBuffer.java` — `asParserOnFirstToken()` |

**Analysis:** When replaying buffered tokens, EMF context (type hints, schema URI) from the original parser context is not fully preserved. This is a known limitation documented with a TODO comment.

**BSI reference:** CWE-843 (Access of Resource Using Incompatible Type)

---

## 3. Existing Mitigations

### 3.1 Configurable Limits

| Limit | Default | Protects | Status |
|-------|---------|----------|--------|
| BSON max payload size | 100 MB (`CodecOptions.DEFAULT_MAX_PAYLOAD_SIZE`) | S-1 | ✅ Implemented |

### 3.2 Design-Level Mitigations

| Mitigation | Status | Description |
|------------|--------|-------------|
| No arbitrary class loading | By design | No `Class.forName()` with user input; type resolution via EPackage registry only |
| No `ObjectInputStream` | By design | Uses Jackson databind, not Java serialization — immune to Java deser gadgets |
| No XML parsing | By design | JSON/YAML/CBOR/BSON only — no XXE or billion-laughs XML attacks |
| EMF factory pattern | By design | Objects created via `EcoreUtil.create(EClass)`, not reflection |
| Entry-based deserialization | By design | Structured per-concern entries instead of monolithic deserializer |
| Configuration hierarchy | Implemented | 5-level merge with clear priority (options > factory > module > annotations > defaults) |
| ConcurrentHashMap in registry | Implemented | `CodecValueRegistry` uses thread-safe maps |
| Registry snapshot on use | Implemented | `CodecResourceFactoryComponent` copies registry to avoid concurrent modification |
| Jackson 3.1.0 safe defaults | By dependency | Polymorphic type handling disabled by default; YAML type tags blocked |
| BSON payload size limit | Implemented | `readNBytes()` with configurable max + try-with-resources (S-1, S-13) |

### 3.2 Format-Specific Safety Properties

| Format | Property | Status |
|--------|----------|--------|
| JSON | No polymorphic default typing | Safe by default |
| YAML | SnakeYAML 3.0.1 safe constructors | Safe by default |
| CBOR | Jackson CBOR module | Standard safety |
| BSON | MongoDB BSON 5.6.3 library | No known gadgets |

---

## 4. Mitigation Plan

### Implemented Mitigations

#### S-1: BSON Stream Size Limit ✅ IMPLEMENTED

**Problem:** `readAllBytes()` without size limit enables trivial OOM attacks.

**Solution:** `maxPayloadSize` (default: 100 MB) — `BsonFormatProvider` uses `readNBytes(limit)` with overflow check. Payloads exceeding the limit are rejected with `IOException` before decoding. The limit is configurable via the `BsonFormatProvider(long maxPayloadSize)` constructor and exposed as `CodecOptions.CODEC_MAX_PAYLOAD_SIZE`. Additionally, `BsonBinaryReader` is now wrapped in try-with-resources, also fixing S-13 (resource leak on decode exception).

**Tests:** `BsonFormatProviderTest.PayloadSizeLimit` — 6 tests:
- `defaultLimit`, `customLimit` — construction
- `zeroLimitRejected`, `negativeLimitRejected` — input validation
- `payloadWithinLimit` — valid BSON under the limit succeeds
- `payloadExceedingLimit` — oversized payload throws `IOException`

---

### CRITICAL Priority

#### S-2: Nesting Depth Protection in Deserializer

**Problem:** `readCurrentValue()` recurses without depth tracking.

**Solution:** Add depth counter to `readCurrentValue()`, `readArrayAsList()`, `readObjectAsMap()`:

```java
private static final int MAX_NESTING_DEPTH = 200;

private Object readCurrentValue(JsonParser parser, int depth) {
    if (depth > MAX_NESTING_DEPTH) {
        throw new CodecException("Maximum nesting depth exceeded: " + MAX_NESTING_DEPTH);
    }
    // ... existing logic with depth+1 passed to recursive calls
}
```

---

### HIGH Priority

#### S-3: Collection Size Guard in Deserializer

**Problem:** `readArrayAsList()` and `readObjectAsMap()` accumulate without limit.

**Solution:** Add configurable maximum element count:

```java
private List<Object> readArrayAsList(JsonParser parser, int depth) {
    List<Object> result = new ArrayList<>();
    while (parser.nextToken() != JsonToken.END_ARRAY) {
        if (result.size() >= maxCollectionSize) {
            throw new CodecException("Array exceeds maximum size: " + maxCollectionSize);
        }
        result.add(readCurrentValue(parser, depth + 1));
    }
    return result;
}
```

---

#### S-4: Type Resolution Scoping

**Problem:** Simple name resolution scans all registered EPackages non-deterministically.

**Solution:** Scope type resolution to the context EPackage or an explicit allowlist:

```java
// Preferred: resolve within context package first
public static EClass resolveFromSimpleName(String className, EPackage contextPackage) {
    // 1. Try context package
    EClassifier classifier = contextPackage.getEClassifier(className);
    if (classifier instanceof EClass ec) return ec;
    // 2. Try sub-packages of context
    // 3. Only fall back to global scan if explicitly enabled
}
```

---

#### S-5: Restrict Reflection Targets

**Problem:** `convertObjectFromString()` invokes constructors/methods on arbitrary classes.

**Solution:** Allowlist safe target types for reflection-based conversion:

```java
private static final Set<Class<?>> SAFE_CONVERSION_TARGETS = Set.of(
    java.util.UUID.class,
    java.math.BigDecimal.class,
    java.math.BigInteger.class,
    java.net.URI.class,
    java.time.Instant.class,
    java.time.LocalDate.class,
    java.time.LocalDateTime.class
);

private Object convertObjectFromString(String stringValue, Class<?> targetType) {
    if (!SAFE_CONVERSION_TARGETS.contains(targetType)) {
        // Fall back to EcoreUtil.createFromString() which is EMF-controlled
        return EcoreUtil.createFromString(eDataType, stringValue);
    }
    // ... existing reflection logic
}
```

---

#### S-6: Configure StreamReadConstraints

**Problem:** Default Jackson constraints are too generous.

**Solution:** Apply tighter constraints in `CodecResource`:

```java
StreamReadConstraints constraints = StreamReadConstraints.builder()
    .maxNestingDepth(200)
    .maxStringLength(10_000_000)    // 10MB
    .maxNameLength(10_000)          // 10KB
    .build();
```

Make configurable via codec load/save options.

---

### MEDIUM Priority

#### S-7: YAML Type Tag Hardening

**Status:** Safe by Jackson 3.1.0 defaults. **Recommendation:** Add explicit security test:

```java
@Test
void yamlTypeTag_rejected() {
    String yaml = "!!java.lang.ProcessBuilder [[\"id\"]]";
    assertThrows(CodecException.class, () -> loadFromYaml(yaml));
}
```

---

#### S-8: CBOR Indefinite-Length Protection

**Status:** Partially mitigated by Jackson defaults. **Recommendation:** Configure `CBORFactory` to reject or limit indefinite-length items if Jackson API supports it.

---

#### S-9: Numeric Type Resolution Scoping

**Solution:** Require context schema URI for numeric resolution; reject global fallback:

```java
if (contextSchemaUri == null) {
    throw new CodecException("Numeric type resolution requires a context schema URI");
}
```

---

#### S-10: URI Scheme Validation

**Solution:** Validate proxy URI schemes against an allowlist:

```java
private static final Set<String> ALLOWED_URI_SCHEMES = Set.of("", "platform", "ecore");

private URI resolveProxyUri(String uriString) {
    URI targetUri = URI.createURI(uriString);
    String scheme = targetUri.scheme();
    if (scheme != null && !ALLOWED_URI_SCHEMES.contains(scheme)) {
        throw new CodecException("Unsupported URI scheme: " + scheme);
    }
    // ... existing resolution
}
```

---

#### S-11: Custom Value Handler Audit

**Solution:** Add logging and optional permission check for handler registration:

```java
void addValueWriter(CodecValueWriter<?, ?> writer) {
    LOGGER.info("Registering custom value writer: " + writer.getClass().getName());
    valueRegistry.register(writer);
}
```

---

### LOW Priority (Accepted Risks)

#### S-12: Information Disclosure

**Status:** Accepted risk with recommendation. Error messages should avoid revealing EClass names and feature lists in production deployments. Embedders should configure logging level to `SEVERE` for codec loggers in production.

---

#### S-13: BSON Resource Leak ✅ IMPLEMENTED

**Status:** Fixed as part of the S-1 implementation. `BsonBinaryReader` is now wrapped in try-with-resources, ensuring proper cleanup even if `CODEC.decode()` throws.

---

#### S-14: Thread Safety in Serializer

**Status:** Accepted risk. `DiagnosticCollector` is typically scoped per-operation. Embedders should avoid sharing serializer instances across threads.

---

#### S-15: TokenBuffer Context Loss

**Status:** Known limitation (TODO in code). Low practical impact since deferred property replay is rare.

---

## 5. BSI TR-03185 Mapping

Mapping of identified risks and mitigations to the requirement areas of [BSI TR-03185](https://www.bsi.bund.de/DE/Themen/Unternehmen-und-Organisationen/Standards-und-Zertifizierung/Technische-Richtlinien/TR-nach-Thema-sortiert/tr03185/tr-03185.html) (Secure Software Lifecycle):

### 5.1 Requirement Area: Secure Design (§4.3)

| TR Requirement | Implementation | Status |
|----------------|----------------|--------|
| Threat modeling | Trust boundary diagram (§1.2), actor analysis (§1.3), 15 attack vectors | Present |
| Attack surface minimization | No XML parsing, no Java serialization, no dynamic class loading | By design |
| Defense in depth | Entry-based deserialization, EMF factory pattern, configuration hierarchy | Implemented |
| Secure defaults | Jackson 3.1.0 safe defaults, no polymorphic default typing | By dependency |

### 5.2 Requirement Area: Input Validation (§4.5)

| TR Requirement | Implementation | Status |
|----------------|----------------|--------|
| Stream size limits | BSON: `maxPayloadSize` (default 100 MB) (S-1); JSON/YAML/CBOR: Jackson defaults | ✅ Implemented |
| Nesting depth limits | Deserializer: **missing** (S-2); Jackson: default 1000 | Partial |
| Collection size limits | Deserializer: **missing** (S-3) | Missing |
| Type value validation | Simple name resolution unbounded (S-4) | Missing |
| URI scheme validation | Proxy URIs not scheme-validated (S-10) | Missing |
| YAML type tag filtering | Safe by Jackson 3.1.0 default (S-7) | By dependency |

### 5.3 Requirement Area: Availability (§4.6)

| TR Requirement | Implementation | Status |
|----------------|----------------|--------|
| Stream size limit | BSON: `maxPayloadSize` (default 100 MB) (S-1) | ✅ Implemented |
| Nesting depth limit | No codec-level limit (S-2) | Missing |
| Collection size guard | No limit in readArrayAsList/readObjectAsMap (S-3) | Missing |
| CBOR item limits | No indefinite-length protection (S-8) | Missing |
| StreamReadConstraints | Using defaults, not customized (S-6) | Partial |

### 5.4 Requirement Area: Confidentiality (§4.4)

| TR Requirement | Implementation | Status |
|----------------|----------------|--------|
| Error message filtering | Error messages leak model structure (S-12) | Missing |
| Diagnostic isolation | Thread-shared diagnostics (S-14) | Partial |
| No credential handling | Codec does not handle credentials | By design |

### 5.5 Requirement Area: Third-Party Components (§4.7)

| TR Requirement | Implementation | Status |
|----------------|----------------|--------|
| Jackson 3.1.0 | Current, no known CVEs | Current |
| SnakeYAML Engine 3.0.1 | Safe by default (no auto-instantiation) | Current |
| MongoDB BSON 5.6.3 | Current, no known gadgets | Current |
| EMF 2.41.0 | Current stable | Current |
| Custom handler validation | No validation on OSGi registration (S-11) | Missing |

### 5.6 Requirement Area: Testing (§4.8)

| TR Requirement | Implementation | Status |
|----------------|----------------|--------|
| Security-specific tests | 6 tests in `BsonFormatProviderTest.PayloadSizeLimit` (S-1) | Partial |
| Fuzzing / adversarial input tests | Not present | Missing |
| Format-specific attack tests | Not present | Missing |
| Large payload TCK | `AbstractLargePayloadTCK` exists (functional, not security) | Partial |

### 5.7 Further Relevant BSI Guidelines

| Guideline | Relevance | Reference |
|-----------|-----------|-----------|
| [BSI TR-03185](https://www.bsi.bund.de/DE/Themen/Unternehmen-und-Organisationen/Standards-und-Zertifizierung/Technische-Richtlinien/TR-nach-Thema-sortiert/tr03185/tr-03185.html) | High | Secure software lifecycle — primary guideline |
| [BSI TR-03185-2](https://www.bsi.bund.de/DE/Service-Navi/Presse/Alle-Meldungen-News/Meldungen/TR-03185-2_OSS_251103.html) | High | Part 2 for open-source software (EPL-2.0 license) |
| [BSI IT-Grundschutz](https://www.bsi.bund.de/DE/Themen/Unternehmen-und-Organisationen/Standards-und-Zertifizierung/IT-Grundschutz/it-grundschutz_node.html) | Medium | Foundation framework; APP.6 General Software |

---

## 6. Embedder Guide

Recommendations for developers embedding the Fennec Codec in their applications.

### 6.1 Secure Deserialization (MUST for untrusted input)

```java
import org.eclipse.emf.ecore.resource.Resource;
import java.util.Map;

// 1. Register only the EPackages you need
EPackage.Registry registry = new EPackageRegistryImpl();
registry.put(MyPackage.eNS_URI, MyPackage.eINSTANCE);
// Do NOT use EPackage.Registry.INSTANCE for untrusted input

// 2. Create a scoped ResourceSet
ResourceSet resourceSet = new ResourceSetImpl();
resourceSet.setPackageRegistry(registry);

// 3. Configure codec options with limits
Map<String, Object> options = Map.of(
    // Use full URI type strategy to avoid ambiguous resolution
    CodecOptions.OPT_TYPE_STRATEGY, TypeStrategy.FULL_URI
);

// 4. Load with scoped registry
Resource resource = resourceSet.createResource(uri);
resource.load(inputStream, options);
```

### 6.2 BSON Payload Size Limiting

The codec enforces a default 100 MB limit. For tighter control, configure the `BsonFormatProvider`:

```java
// Tightened limit for untrusted sources (10 MB)
long maxBsonSize = 10 * 1024 * 1024L;
BsonFormatProvider bsonProvider = new BsonFormatProvider(maxBsonSize);
CodecResource resource = new CodecResource(uri, metadataService,
        resolver, null, null, bsonProvider);
resource.load(inputStream, options);
```

### 6.3 Production Logging Configuration

```properties
# Suppress detailed codec diagnostics in production
org.eclipse.fennec.codec.level = SEVERE
org.eclipse.fennec.codec.deser.level = SEVERE
org.eclipse.fennec.codec.resource.level = SEVERE
```

### 6.4 Custom Value Handler Security

```java
// Only register audited, trusted value handlers
CodecValueRegistry registry = new CodecValueRegistry();
registry.register(new MyAuditedValueWriter());  // internally reviewed

// Do NOT register handlers from untrusted sources
// registry.register(untrustedHandler);  // RISK: arbitrary code execution
```

### 6.5 Type Strategy Selection

| Strategy | Security | Use When |
|----------|----------|----------|
| `FULL_URI` | Best | Untrusted input — fully qualified, no ambiguity |
| `CLASS_NAME` | Good | Java class name — unique if instance classes set |
| `SIMPLE_NAME` | Risky | Only trusted input — ambiguous across packages |
| `NUMERIC` | Risky | Only trusted input — ambiguous without schema URI |

---

## 7. References

### BSI Guidelines

- [BSI TR-03185 — Secure Software Lifecycle](https://www.bsi.bund.de/DE/Themen/Unternehmen-und-Organisationen/Standards-und-Zertifizierung/Technische-Richtlinien/TR-nach-Thema-sortiert/tr03185/tr-03185.html) — Primary guideline for secure software development
- [BSI TR-03185-2 — Open Source Software](https://www.bsi.bund.de/DE/Service-Navi/Presse/Alle-Meldungen-News/Meldungen/TR-03185-2_OSS_251103.html) — Part 2 specifically for OSS (relevant for EPL-2.0)
- [BSI IT-Grundschutz](https://www.bsi.bund.de/DE/Themen/Unternehmen-und-Organisationen/Standards-und-Zertifizierung/IT-Grundschutz/it-grundschutz_node.html) — Foundation framework

### CWE References

| CWE | Vectors |
|-----|---------|
| CWE-400 (Resource Exhaustion) | S-1 BSON OOM, S-2 Nesting, S-3 Array Size, S-6 StreamConstraints, S-8 CBOR |
| CWE-770 (Allocation Without Limits) | S-1 BSON readAllBytes, S-3 Unbounded Collections |
| CWE-674 (Uncontrolled Recursion) | S-2 Nesting Depth |
| CWE-843 (Incompatible Type Access) | S-4 Type Confusion, S-9 Numeric ID, S-15 Context Loss |
| CWE-470 (Externally-Controlled Class Selection) | S-5 Reflection |
| CWE-502 (Deserialization of Untrusted Data) | S-7 YAML Tags |
| CWE-918 (Server-Side Request Forgery) | S-10 URI Scheme |
| CWE-94 (Code Injection) | S-11 Custom Handlers |
| CWE-209 (Sensitive Info in Error Message) | S-12 Information Disclosure |
| CWE-404 (Improper Resource Shutdown) | S-13 BSON Leak |
| CWE-362 (Race Condition) | S-14 Thread Safety |

### Dependency Versions Analyzed

| Library | Version | Status |
|---------|---------|--------|
| Jackson Core | 3.1.0 | Current, no known CVEs |
| Jackson Databind | 3.1.0 | Current, no known CVEs |
| Jackson YAML | 3.1.0 | Current, safe YAML defaults |
| Jackson CBOR | 3.1.0 | Current |
| SnakeYAML Engine | 3.0.1 | Current, safe by default |
| MongoDB BSON | 5.6.3 | Current |
| EMF Core | 2.41.0 | Current stable |
| EMF XMI | 2.39.0 | Current stable |
| OSGi SCR | 2.2.12 | Current |
| JUnit Jupiter | 5.14.2 | Current |

### Internal Documents

- [Codec v2 Specification](codec-v2-spec/00-overview.md) — Codec specification (source of truth)
- [Codec v2 Development Guide](codec-v2-development-guide.md) — Current development state
- [JSON Schema Architecture](../org.eclipse.fennec.codec.jsonschema/jsonschema-architecture.md) — JSON Schema extension
- [Codec Metadata Architecture](../org.eclipse.fennec.codec.metadata/codec-metadata-architecture.md) — Metadata subsystem
