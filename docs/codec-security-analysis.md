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

### S-1: BSON Unbounded Memory Allocation ✅ FIXED

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

### S-2: Uncontrolled JSON/YAML/CBOR Nesting Depth ✅ FIXED

| | |
|---|---|
| **Severity** | CRITICAL |
| **Vector** | Deeply nested JSON structures (10,000+ levels) — `readCurrentValue()` recurses without depth tracking |
| **Impact** | StackOverflowError, Denial of Service |
| **Prerequisite** | Attacker controls input stream |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/CodecEObjectDeserializer.java` — `readCurrentValue()`, `readArrayAsList()`, `readObjectAsMap()`; `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/AttributeDeserializationEntry.java` — `readAnyJsonValue()`, `readJsonArrayAsCollection()`, `readJsonObjectAsMap()`, `readJsonObjectToString()`, `readJsonArrayToString()`, `readArrayValue()` |

**Analysis:** Previously, recursive value-reading methods had no depth tracking. Fixed by adding a `MAX_NESTING_DEPTH` (200) limit to all recursive chains in both `CodecEObjectDeserializer` (deferred properties path) and `AttributeDeserializationEntry` (EJavaObject attributes, JSON-to-String conversion, multi-dimensional arrays). When the depth limit is exceeded, `parser.skipChildren()` is called to keep the parser in a consistent state, the value is dropped (returns `null`), and a warning diagnostic is added to the resource. The recursion then unwinds naturally as each level finds its matching END_ARRAY/END_OBJECT.

**BSI reference:** CWE-674 (Uncontrolled Recursion), CWE-400 (Resource Exhaustion)

---

### S-3: Unbounded Array/Object Size in Deserialization ✅ FIXED

| | |
|---|---|
| **Severity** | HIGH |
| **Vector** | JSON array with millions of elements — `readArrayAsList()` has no size limit |
| **Impact** | OutOfMemoryError, Denial of Service |
| **Prerequisite** | Attacker controls input stream |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/CodecEObjectDeserializer.java` — `readArrayAsList()`, `readObjectAsMap()`; `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/AttributeDeserializationEntry.java` — `readJsonArrayAsCollection()`, `readJsonObjectAsMap()` |

**Analysis:** Previously, collection-accumulating methods had no size check. Fixed by adding a `MAX_COLLECTION_SIZE` (100,000) limit to all recursive value-reading loops in both `CodecEObjectDeserializer` (deferred properties path) and `AttributeDeserializationEntry` (EJavaObject attributes). When the limit is exceeded, the warning is emitted once and remaining elements are skipped via `parser.skipChildren()` without accumulation, then the loop drains to the matching end token. The truncated collection is returned with the elements read so far.

**BSI reference:** CWE-400 (Resource Exhaustion), CWE-770 (Allocation Without Limits)

---

### S-4: Type Confusion via Unrestricted EPackage Scanning ✅ FIXED

| | |
|---|---|
| **Severity** | HIGH |
| **Vector** | Simple class name `"Person"` matches unintended EClass in a different registered EPackage |
| **Impact** | Type confusion — deserialization into wrong EClass, potential data corruption |
| **Prerequisite** | Attacker controls `_type` value in JSON + multiple EPackages registered |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/util/TypeResolutionHelper.java` — `resolveFromSimpleName()` |

**Analysis:** When using the `SIMPLE_NAME` type strategy, the resolver iterates **all** registered EPackages and returns the **first match**. The iteration order is undefined (`EPackage.Registry.INSTANCE.keySet()`), making resolution non-deterministic when multiple packages define classes with the same name.

**Fix:** All three non-URI type strategies (NAME, CLASS, NUMERIC) now require a schema hint (`CODEC_ROOT_SCHEMA` or `CODEC_ROOT_TYPE`) to scope resolution. Without a hint, resolution fails with a warning diagnostic on the resource instead of scanning all registered EPackages. The scoped overloads `resolveFromSimpleName(String, EPackage)`, `resolveFromClassName(String, EPackage)` are called from `TypeDeserializationEntry`, which derives the context package from the deserialization context schema URI or from the hint EClass.

**BSI reference:** CWE-843 (Access of Resource Using Incompatible Type)

---

### S-5: Reflection-Based Object Instantiation from User Input ✅ FIXED

| | |
|---|---|
| **Severity** | HIGH |
| **Vector** | Attribute deserialization invokes `targetType.getConstructor(String.class).newInstance(stringValue)` |
| **Impact** | Arbitrary constructor invocation if malicious EDataType is registered |
| **Prerequisite** | Attacker controls both ECORE metamodel and JSON data |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/AttributeDeserializationEntry.java` — `convertObjectFromString()` |

**Analysis:** The fallback conversion path uses reflection to invoke `String` constructors, `valueOf()`, and `parse()` static methods on the target Java class. While `targetType` comes from the EDataType's `instanceClass`, a malicious ECORE model could define a data type whose instance class has side effects in its constructor.

**Fix:** Added `SAFE_REFLECTION_TARGETS` allowlist (13 types: `URI`, `URL`, and 11 `java.time` types). `BigDecimal`, `BigInteger`, `UUID`, and `Date` are handled by direct code paths before the check. The `convertObjectFromString()` method now rejects types not on the allowlist before attempting any reflection. Rejected types throw `IllegalArgumentException`, which is caught by the caller and converted to a warning diagnostic. All other types must go through `EcoreUtil.createFromString()` which is EMF-controlled.

**BSI reference:** CWE-470 (Use of Externally-Controlled Input to Select Classes or Code)

---

### S-6: Missing Jackson StreamReadConstraints Configuration ✅ FIXED

| | |
|---|---|
| **Severity** | HIGH |
| **Vector** | Default Jackson constraints allow 20MB strings, 1000 nesting depth, 50KB field names |
| **Impact** | Memory exhaustion, stack overflow |
| **Prerequisite** | Attacker controls input stream |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/resource/CodecResource.java` — `doLoadWithFormat()`, `doSaveWithFormat()` |

**Analysis:** `StreamReadConstraints.defaults()` is used without customization. Jackson 3.1.0 defaults allow: max nesting depth 1000, max string length 20MB, max field name length 50KB. These are generous for most codec use cases and should be tightened.

**Fix:** Replaced `StreamReadConstraints.defaults()` with hardened `STREAM_READ_CONSTRAINTS` constant in `CodecResource`. Applied to all three parsing paths: default JSON (via `CodecJsonFactory` builder), format provider load, and format provider save. Limits: nesting depth 200, string length 10 MB, field name length 10 KB.

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

### S-9: Numeric Classifier ID Ambiguity ✅ FIXED (by S-4)

| | |
|---|---|
| **Severity** | MEDIUM |
| **Vector** | Numeric type ID `"3"` matches different EClasses depending on package iteration order |
| **Impact** | Non-deterministic type resolution, type confusion |
| **Prerequisite** | Attacker controls `_type` value + multiple packages with overlapping classifier IDs |
| **File** | `org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/util/TypeResolutionHelper.java` — `resolveFromNumeric()` |

**Analysis:** When using `NUMERIC` type strategy without a context schema URI, the fallback scans all registered packages in undefined order. Classifier IDs are only unique within a single EPackage.

**Fix:** The global scan fallback in `resolveFromNumeric()` has been removed as part of the S-4 fix. NUMERIC strategy now requires a hint EClass or context schema URI; without either, resolution fails with a warning.

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
| Max nesting depth | 200 (`CodecEObjectDeserializer.MAX_NESTING_DEPTH`) | S-2 | ✅ Implemented |
| Max collection size | 100,000 (`CodecEObjectDeserializer.MAX_COLLECTION_SIZE`) | S-3 | ✅ Implemented |
| Jackson max nesting depth | 500 (`CodecResource.STREAM_READ_CONSTRAINTS`) — backstop above codec's own 200 | S-6 | ✅ Implemented |
| Jackson max string length | 10 MB (`CodecResource.STREAM_READ_CONSTRAINTS`) | S-6 | ✅ Implemented |
| Jackson max field name length | 10 KB (`CodecResource.STREAM_READ_CONSTRAINTS`) | S-6 | ✅ Implemented |

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
| Jackson StreamReadConstraints | Implemented | Hardened limits: nesting 500 (backstop), strings 10 MB, field names 10 KB (S-6) |
| BSON payload size limit | Implemented | `readNBytes()` with configurable max + try-with-resources (S-1, S-13) |
| Nesting depth limit | Implemented | `MAX_NESTING_DEPTH=200` in deserializer + attribute entry with `skipChildren()` recovery (S-2) |
| Collection size limit | Implemented | `MAX_COLLECTION_SIZE=100,000` in deserializer + attribute entry; remaining elements skipped (S-3) |
| Type resolution scoping | Implemented | NAME, CLASS, NUMERIC strategies scoped to context package; no global EPackage scan (S-4, S-9) |
| Reflection allowlist | Implemented | `SAFE_REFLECTION_TARGETS` (13 types) gates `convertObjectFromString()` reflection (S-5) |

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

#### S-2: Nesting Depth Protection in Deserializer ✅ IMPLEMENTED

**Problem:** `readCurrentValue()` recurses without depth tracking.

**Solution:** `MAX_NESTING_DEPTH` (200) — depth counter added to all recursive value-reading chains in `CodecEObjectDeserializer` (deferred properties) and `AttributeDeserializationEntry` (EJavaObject attributes, JSON-to-String conversion, multi-dimensional arrays). When the limit is exceeded, `parser.skipChildren()` is called to properly consume the remaining nested content (keeping the parser consistent), the value is dropped (`null`), and a warning diagnostic is added to the resource. The recursion unwinds naturally as each level finds its matching END_ARRAY/END_OBJECT.

**Tests:** `NestingDepthProtectionTest` — 9 tests:
- `AttributePath`: deeply nested objects, arrays, mixed nesting → warning diagnostic
- `AttributePath`: objects within limit → no diagnostic
- `DeferredPath`: deeply nested deferred objects, arrays → warning diagnostic, EObject still produced
- `DeferredPath`: deferred within limit → no diagnostic
- `Constants`: MAX_NESTING_DEPTH value, shared between classes

---

### CRITICAL Priority


### HIGH Priority

#### S-3: Collection Size Guard in Deserializer ✅ IMPLEMENTED

**Problem:** `readArrayAsList()` and `readObjectAsMap()` accumulate without limit.

**Solution:** `MAX_COLLECTION_SIZE` (100,000) — size check added to all collection-accumulating loops in `CodecEObjectDeserializer` (deferred properties) and `AttributeDeserializationEntry` (EJavaObject attributes). When the limit is exceeded, the warning is emitted once and remaining elements are skipped via `parser.skipChildren()` without accumulation. The loop continues to drain tokens until the matching end token, keeping the parser consistent. The truncated collection is returned with elements read so far.

**Tests:** `CollectionSizeProtectionTest` — 7 tests:
- `AttributePath`: oversized array, oversized object → warning diagnostic; small array → no diagnostic
- `DeferredPath`: oversized deferred array, oversized deferred object → warning diagnostic; EObject still produced
- `Constants`: MAX_COLLECTION_SIZE value, shared between classes

---

#### S-4: Type Resolution Scoping ✅ IMPLEMENTED

**Problem:** NAME, CLASS, and NUMERIC type strategies scan all registered EPackages non-deterministically.

**Solution:** Scoped resolution — all three non-URI strategies now require a context package derived from a schema hint. `TypeResolutionHelper` provides scoped overloads: `resolveFromSimpleName(String, EPackage)`, `resolveFromClassName(String, EPackage)`. `resolveFromNumeric()` had its global scan fallback removed. `TypeDeserializationEntry.resolveEClass()` derives the context package from the deserialization context schema URI or from the hint EClass, and adds a warning diagnostic to the resource when resolution fails due to missing context.

**Tests:** `TypeResolutionScopingTest` — 11 tests:
- `HelperScoping`: resolveFromSimpleName with/without context, resolveFromClassName with/without context, resolveFromNumeric with/without hints
- `NameStrategyE2E`: NAME with CODEC_ROOT_TYPE resolves, NAME without hint produces warning
- `NumericStrategyE2E`: NUMERIC with CODEC_ROOT_TYPE resolves

---

#### S-5: Restrict Reflection Targets ✅ IMPLEMENTED

**Problem:** `convertObjectFromString()` invokes constructors/methods on arbitrary classes.

**Solution:** `SAFE_REFLECTION_TARGETS` allowlist (13 types) in `AttributeDeserializationEntry`. The `convertObjectFromString()` method checks the allowlist before any reflection. Types not on the list throw `IllegalArgumentException`, caught by the caller as a warning diagnostic. Safe types for reflection: `URI`, `URL`, `Instant`, `LocalDate`, `LocalTime`, `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime`, `Duration`, `Period`, `Year`, `YearMonth`, `MonthDay`. (`BigDecimal`, `BigInteger`, `UUID`, `Date` are handled by direct code paths before the allowlist check.)

**Tests:** `ReflectionAllowlistTest` — 6 tests:
- `AllowlistContents`: standard value types, java.time types, dangerous types excluded, Object/String excluded, allowlist size
- `E2EBlocked`: arbitrary type not in allowlist

---

#### S-6: Configure StreamReadConstraints ✅ IMPLEMENTED

**Problem:** Default Jackson constraints are too generous.

**Solution:** `STREAM_READ_CONSTRAINTS` constant in `CodecResource` with hardened limits: nesting depth 500 (was 1000, backstop above codec's own 200 limit), string length 10 MB (was 20 MB), field name length 10 KB (was 50 KB). Applied to all three parsing paths: default JSON factory, format provider load, and format provider save.

**Tests:** `StreamReadConstraintsTest` — 6 tests:
- `ConstraintValues`: non-null, nesting depth 200, string length 10 MB, name length 10 KB, tighter than defaults
- `OversizedFieldName`: field name exceeding 10 KB rejected

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

#### S-9: Numeric Type Resolution Scoping ✅ IMPLEMENTED (by S-4)

**Problem:** Numeric classifier ID resolution falls back to scanning all packages.

**Solution:** Fixed as part of S-4 — the global scan fallback in `resolveFromNumeric()` has been removed. NUMERIC strategy now requires a hint EClass or context schema URI.

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
| Stream size limits | BSON: `maxPayloadSize` (default 100 MB) (S-1); JSON/YAML/CBOR: Jackson defaults | ✅ Implemented (S-1) |
| Nesting depth limits | Deserializer: `MAX_NESTING_DEPTH=200` (S-2); Jackson: default 1000 | ✅ Implemented |
| Collection size limits | Deserializer: `MAX_COLLECTION_SIZE=100,000` (S-3) | ✅ Implemented |
| Type value validation | Scoped resolution via context package (S-4) | ✅ Implemented |
| URI scheme validation | Proxy URIs not scheme-validated (S-10) | Missing |
| YAML type tag filtering | Safe by Jackson 3.1.0 default (S-7) | By dependency |

### 5.3 Requirement Area: Availability (§4.6)

| TR Requirement | Implementation | Status |
|----------------|----------------|--------|
| Stream size limit | BSON: `maxPayloadSize` (default 100 MB) (S-1) | ✅ Implemented |
| Nesting depth limit | `MAX_NESTING_DEPTH=200` in deserializer + attribute entry (S-2) | ✅ Implemented |
| Collection size guard | `MAX_COLLECTION_SIZE=100,000` in deserializer + attribute entry (S-3) | ✅ Implemented |
| CBOR item limits | No indefinite-length protection (S-8) | Missing |
| StreamReadConstraints | Hardened: nesting 200, strings 10 MB, names 10 KB (S-6) | ✅ Implemented |

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
| Security-specific tests | 6 tests in `BsonFormatProviderTest.PayloadSizeLimit` (S-1); 9 tests in `NestingDepthProtectionTest` (S-2); 7 tests in `CollectionSizeProtectionTest` (S-3); 11 tests in `TypeResolutionScopingTest` (S-4, S-9); 6 tests in `ReflectionAllowlistTest` (S-5); 6 tests in `StreamReadConstraintsTest` (S-6) | Partial |
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

### 6.2 Nesting Depth Protection

The codec enforces a maximum nesting depth of 200 for recursive JSON value reading during deserialization. Deeply nested structures beyond this limit are silently truncated (value dropped, warning diagnostic added to resource). This protects against `StackOverflowError` from malicious payloads.

The limit applies to:
- **Deferred properties** (properties appearing before `_type`) — in `CodecEObjectDeserializer`
- **EJavaObject attributes** — in `AttributeDeserializationEntry`
- **JSON-to-String conversion** — when String attributes encounter nested JSON
- **Multi-dimensional arrays** — nested array type deserialization

```java
// Check for nesting depth warnings after loading
resource.load(inputStream, options);
for (Diagnostic warning : resource.getWarnings()) {
    if (warning.getMessage().contains("Maximum nesting depth exceeded")) {
        // Potentially malicious input - deeply nested structure was truncated
        log.warn("Deeply nested input truncated: " + warning.getMessage());
    }
}
```

### 6.3 Collection Size Protection

The codec enforces a maximum of 100,000 elements per collection (array or object) during recursive value reading in deserialization. Collections exceeding this limit are truncated — elements up to the limit are kept, remaining elements are skipped, and a warning diagnostic is added to the resource.

### 6.4 Type Resolution Scoping

When using NAME, CLASS, or NUMERIC type strategies (non-default), you **must** provide a schema hint to scope type resolution to a specific EPackage. Without a hint, resolution fails with a warning diagnostic instead of scanning all registered packages (which could cause type confusion in multi-package environments).

```java
// Always provide CODEC_ROOT_TYPE or CODEC_ROOT_SCHEMA with non-URI strategies
Map<String, Object> options = Map.of(
    CodecResource.CODEC_ROOT_TYPE, MyPackage.Literals.MY_ROOT_CLASS
);
resource.load(inputStream, options);

// Check for scoping warnings
for (Diagnostic warning : resource.getWarnings()) {
    if (warning.getMessage().contains("requires a schema hint")) {
        log.warn("Type resolution failed: " + warning.getMessage());
    }
}
```

The default type strategy (`URI`) is not affected — full URIs are always unambiguous.

### 6.5 BSON Payload Size Limiting

The codec enforces a default 100 MB limit. For tighter control, configure the `BsonFormatProvider`:

```java
// Tightened limit for untrusted sources (10 MB)
long maxBsonSize = 10 * 1024 * 1024L;
BsonFormatProvider bsonProvider = new BsonFormatProvider(maxBsonSize);
CodecResource resource = new CodecResource(uri, metadataService,
        resolver, null, null, bsonProvider);
resource.load(inputStream, options);
```

### 6.6 Production Logging Configuration

```properties
# Suppress detailed codec diagnostics in production
org.eclipse.fennec.codec.level = SEVERE
org.eclipse.fennec.codec.deser.level = SEVERE
org.eclipse.fennec.codec.resource.level = SEVERE
```

### 6.7 Custom Value Handler Security

```java
// Only register audited, trusted value handlers
CodecValueRegistry registry = new CodecValueRegistry();
registry.register(new MyAuditedValueWriter());  // internally reviewed

// Do NOT register handlers from untrusted sources
// registry.register(untrustedHandler);  // RISK: arbitrary code execution
```

### 6.8 Type Strategy Selection

| Strategy | Security | Use When |
|----------|----------|----------|
| `FULL_URI` | Best | Untrusted input — fully qualified, no ambiguity |
| `CLASS_NAME` | Scoped | Requires schema hint — scoped to context package (S-4) |
| `SIMPLE_NAME` | Scoped | Requires schema hint — scoped to context package (S-4) |
| `NUMERIC` | Scoped | Requires schema hint — scoped to context package (S-4, S-9) |

### 6.9 Reflection Allowlist

The codec restricts reflection-based type conversion in `convertObjectFromString()` to a fixed allowlist of 13 safe types (`SAFE_REFLECTION_TARGETS`), plus 4 types handled by direct code paths. This prevents arbitrary constructor invocation if a malicious EDataType with a dangerous `instanceClass` is registered.

If your application uses custom EDataTypes with instance classes not on the allowlist, the codec will reject them with a warning and skip the value. Use `EcoreUtil.createFromString()` in a custom value handler (`CodecValueReader`) instead for non-standard types.

### 6.10 Jackson StreamReadConstraints

The codec applies hardened Jackson `StreamReadConstraints` to all parsing paths:

| Limit | Codec Value | Jackson Default |
|-------|-------------|-----------------|
| Max nesting depth | 500 (backstop; codec's own limit is 200) | 1000 |
| Max string length | 10 MB | 20 MB |
| Max field name length | 10 KB | 50 KB |

These are enforced at the Jackson parser level before the codec's own limits (S-2 nesting depth, S-3 collection size) are checked. Violations throw a Jackson exception that surfaces as an error on the resource.

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
