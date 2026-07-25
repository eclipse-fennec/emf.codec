# Format TCK - Feature Capability Map

This document defines the feature capabilities that a codec format implementation must support,
maps them to abstract TCK test groups, and evaluates relevance per format.

---

## 1. Format Characteristics

Understanding what makes each format unique helps reason about which features need testing.

| Characteristic | JSON | YAML | CBOR | BSON |
|----------------|:----:|:----:|:----:|:----:|
| Text-based | yes | yes | no | no |
| Binary-native | no | no | yes | yes |
| Number precision | IEEE 754 | IEEE 754 | tagged types | int32/int64/double/Decimal128 |
| Long (int64) fidelity | lossy >2^53 | lossy >2^53 | native | native |
| Native binary data | no (Base64) | no (Base64) | yes | yes |
| Native date/time | no | no | tag-based | yes (UTC millis) |
| Native ObjectId | no | no | no | yes |
| Map key types | string only | string only | any type | string only |
| Null representation | `null` | `null` / `~` | simple value | BsonNull |
| Nested object depth | unlimited | unlimited | unlimited | unlimited |
| Jackson backend | JsonFactory | YAMLFactory | CBORFactory | BsonFactory |

---

## 2. Feature Capability Map

### Priority Legend

| Priority | Meaning |
|----------|---------|
| **P0** | Must have - basic correctness, will fail visibly if broken |
| **P1** | Should have - important codec features that formats may handle differently |
| **P2** | Nice to have - advanced features, edge cases, configuration variations |

### Relevance Legend

| Symbol | Meaning |
|--------|---------|
| **!!** | Critical - format-specific behavior makes this especially important to test |
| **!** | Relevant - feature should work but no special format concern expected |
| **.** | Low relevance - unlikely to differ from JSON baseline, but TCK ensures it |

---

### 2.1 P0 - Core Serialization (Must Have)

These are the fundamental building blocks. If any of these fail, the format is broken.

| # | Feature | Abstract Test Group | JSON | YAML | CBOR | BSON | Rationale |
|---|---------|---------------------|:----:|:----:|:----:|:----:|-----------|
| 1 | String attribute | `AttributeRoundTrip` | ! | ! | ! | ! | Universal baseline |
| 2 | Int attribute | `AttributeRoundTrip` | ! | ! | !! | !! | Binary formats have native int32; must not lose precision or widen |
| 3 | Long attribute | `AttributeRoundTrip` | ! | ! | !! | !! | JSON/YAML lose precision >2^53; CBOR/BSON have native int64 |
| 4 | Double attribute | `AttributeRoundTrip` | ! | ! | !! | !! | Binary formats: IEEE 754 exact; text formats: string round-trip drift |
| 5 | Boolean attribute | `AttributeRoundTrip` | ! | ! | ! | ! | Universal baseline |
| 6 | Enum attribute (LITERAL) | `EnumRoundTrip` | ! | ! | ! | ! | Default strategy, string-based, should work everywhere |
| 7 | Multi-valued attribute | `MultiValuedRoundTrip` | ! | ! | ! | ! | Array encoding differs per format |
| 8 | Empty list | `MultiValuedRoundTrip` | ! | !! | ! | ! | YAML has `[]` vs absent distinction; some formats omit empty |
| 9 | Single containment | `ContainmentRoundTrip` | ! | ! | ! | ! | Nested object structure |
| 10 | Multi containment | `ContainmentRoundTrip` | ! | ! | ! | ! | Array of nested objects |
| 11 | Single non-containment ref | `ReferenceRoundTrip` | ! | ! | ! | ! | Reference identity preservation across formats |
| 12 | Multi non-containment ref | `ReferenceRoundTrip` | ! | ! | ! | ! | Reference list ordering |
| 13 | ID attribute | `IdRoundTrip` | ! | ! | ! | !! | BSON: `_id` has special semantics in MongoDB |
| 14 | Complex object graph | `ComplexGraphRoundTrip` | ! | ! | ! | ! | All features combined in one object tree |

**Test class:** `AbstractCoreRoundTripTCK`
**Test model:** Extends existing `test-format-parity.ecore`

---

### 2.2 P1 - Codec Feature Strategies (Should Have)

These test the codec's configurable strategies. The format layer must correctly pass through
configuration-driven output variations.

| # | Feature | Abstract Test Group | JSON | YAML | CBOR | BSON | Rationale |
|---|---------|---------------------|:----:|:----:|:----:|:----:|-----------|
| 15 | Enum strategy: VALUE (ordinal) | `EnumStrategyTests` | ! | ! | !! | !! | Binary formats encode numbers natively; must not confuse int enum with regular int |
| 16 | Enum strategy: NAME | `EnumStrategyTests` | ! | ! | ! | ! | String-based, low risk |
| 17 | TypeStrategy: NAME | `TypeStrategyTests` | ! | ! | ! | ! | Simple string `_type` field |
| 18 | TypeStrategy: URI | `TypeStrategyTests` | ! | ! | ! | ! | URI string in `_type` field |
| 19 | TypeStrategy: NONE | `TypeStrategyTests` | ! | !! | ! | ! | YAML: type inference without `_type` is tricky with implicit typing |
| 20 | TypeStrategy: SCHEMA_AND_TYPE | `TypeStrategyTests` | ! | ! | ! | ! | Structured `_type` object; tests nested metadata object |
| 21 | ID strategy: ID_FIELD | `IdStrategyTests` | ! | ! | ! | !! | BSON: `_id` is special, must preserve as native field |
| 22 | ID strategy: COMBINED | `IdStrategyTests` | ! | ! | ! | ! | Separator-based combined keys |
| 23 | ID format: STRUCTURED | `IdStrategyTests` | ! | ! | ! | ! | Nested `_id` object |
| 24 | IdKeyMode: BOTH | `IdKeyModeTests` | ! | ! | ! | !! | BSON: `_id` duplication behavior with MongoDB |
| 25 | IdKeyMode: NONE | `IdKeyModeTests` | ! | ! | ! | ! | No ID output |
| 26 | Reference format: PLAIN | `ReferenceFormatTests` | ! | ! | ! | ! | `"_ref": "/path"` |
| 27 | Reference format: STRUCTURED | `ReferenceFormatTests` | ! | ! | ! | ! | `{"_ref": "...", "_type": "..."}` |
| 28 | Null serialization | `NullHandlingTests` | ! | !! | !! | !! | YAML: `null`/`~`/absent; CBOR: null token; BSON: BsonNull vs absent |
| 29 | Empty collection serialization | `EmptyCollectionTests` | ! | !! | ! | ! | YAML: `[]` vs absent is ambiguous in some parsers |
| 30 | Default value serialization | `DefaultValueTests` | ! | ! | ! | ! | `serializeDefault=true/false` |
| 31 | Custom key names | `CustomKeyTests` | ! | ! | ! | ! | `key="myName"` annotation |
| 32 | Polymorphism (inheritance) | `PolymorphismTests` | ! | ! | ! | ! | Type resolution with mixed-type containment lists |
| 33 | Bidirectional references | `BidirectionalRefTests` | ! | ! | ! | ! | Opposite reference restoration |
| 34 | Circular references | `CircularRefTests` | ! | ! | ! | ! | A->B->A; must not stack-overflow in any format |

**Test class:** `AbstractCodecFeatureTCK`
**Test model:** New `test-tck-features.ecore` (needs polymorphism hierarchy, bidirectional refs)

---

### 2.3 P2 - Advanced & Edge Cases (Nice to Have)

These cover less common configurations, edge cases, and advanced integration scenarios.

| # | Feature | Abstract Test Group | JSON | YAML | CBOR | BSON | Rationale |
|---|---------|---------------------|:----:|:----:|:----:|:----:|-----------|
| 35 | EMap round-trip | `EMapTests` | ! | ! | ! | ! | Map-as-object vs map-as-array |
| 36 | SuperType serialization | `SuperTypeTests` | ! | ! | ! | ! | `_superTypes` field |
| 37 | Discriminator mapping | `DiscriminatorTests` | ! | ! | ! | ! | `typeDiscriminatorPath` based type resolution |
| 38 | Expand references | `ExpandRefTests` | ! | ! | ! | ! | Inline full object instead of `_ref` |
| 39 | Feature visibility (ignore) | `VisibilityTests` | ! | ! | ! | ! | `ignore`, `ignoreRead`, `ignoreWrite` |
| 40 | Force read/write | `ForceReadWriteTests` | ! | ! | ! | ! | Override transient/volatile |
| 41 | Global ignore | `GlobalIgnoreTests` | ! | ! | ! | ! | `ignoreFeatures` list |
| 42 | Strictness (unknown fields) | `StrictnessTests` | ! | ! | ! | ! | `strictOnUnknown=true` should fail |
| 43 | Cross-package types | `CrossPackageTests` | ! | ! | ! | ! | Types from different EPackages |
| 44 | Array root | `ArrayRootTests` | ! | ! | ! | ! | Top-level array instead of object |
| 45 | Smart compression | `SmartCompressionTests` | ! | ! | !! | !! | Binary formats: size reduction amplified |
| 46 | Large payloads (1000+ objects) | `PerformanceTests` | ! | . | !! | !! | Binary formats should show performance benefit |
| 47 | Extended metadata names | `ExtendedMetaDataTests` | ! | ! | ! | ! | XSD name mapping |
| 48 | Custom value reader/writer | `CustomValueTests` | ! | ! | ! | !! | BSON: native types (ObjectId, Decimal128) need custom handlers |
| 49 | In-band EPackage fingerprint | `AbstractFingerprintTCK` | ! | ! | ! | ! | Self-describing multi-version documents (#73 B.1). Property-stream formats only — see below |

> **Row 49 applies to property-stream formats only.** The fingerprint is one more named property
> next to the type, so JSON, YAML, CBOR and BSON all carry it. **Column formats (CSV, XLSX, ODS,
> tabular) must not extend `AbstractFingerprintTCK`**: a fingerprint would have to become a
> column repeated on every row, and the tabular shape has no per-object type context to attach it
> to. They report `supportsInBandFingerprint() == false`, which makes the codec suppress the
> carrier (with a warning) even when `codec.fingerprintMode` asks for it; those callers select a
> version with `codec.rootFingerprint` on load instead.
>
> BSON additionally cannot express the *mixed versions of one nsURI* case, which needs two roots
> side by side — `supportsArrayRoot()` is false for it. The TCK skips that one case rather than
> reporting a carrier gap.

**Test class:** `AbstractAdvancedFeatureTCK`
**Test model:** Reuses existing `test-advanced.ecore` + new `test-tck-advanced.ecore`

---

## 3. Format-Specific Reasoning

### 3.1 JSON (Baseline)

JSON is the reference implementation. All TCK tests must pass for JSON first. No special concerns
beyond standard IEEE 754 number limitations.

**Critical areas:** None beyond baseline - JSON is the golden standard.

### 3.2 YAML

YAML is a text-based superset of JSON, but its implicit typing causes subtle issues:

| Concern | Details | Affected Features |
|---------|---------|-------------------|
| **Implicit typing** | YAML auto-converts `true`, `yes`, `on` to boolean; `0x1F` to int | #28 Null, #8 Empty list, #19 TypeStrategy NONE |
| **Null ambiguity** | `null`, `~`, empty value, absent key are all "null" | #28 Null handling |
| **Empty vs absent** | `tags: []` vs no `tags` key - both can mean "empty" | #8 Empty list, #29 Empty collection |
| **Number precision** | Same as JSON (text-based IEEE 754) | #3 Long, #4 Double |
| **Multi-line strings** | YAML has `|`, `>` blocks; round-trip may change whitespace | #1 String (edge case) |

**Recommended priority features:** #8, #19, #28, #29

### 3.3 CBOR

CBOR is a binary format with richer type system than JSON. Main concern is type fidelity:

| Concern | Details | Affected Features |
|---------|---------|-------------------|
| **Native int/long** | CBOR distinguishes int sizes; must map back correctly to EMF types | #2 Int, #3 Long |
| **Float precision** | CBOR supports half/single/double; must use correct width | #4 Double |
| **Binary data** | Native binary support; no Base64 needed | #48 Custom values |
| **Tags** | CBOR tags can mark semantic types (date, bignum); interaction with codec | #15 Enum VALUE |
| **Compact encoding** | Smart compression benefits amplified in binary | #45 Smart compression, #46 Performance |
| **Null token** | Explicit null token vs absent field | #28 Null handling |

**Recommended priority features:** #2, #3, #4, #15, #28, #45, #46

### 3.4 BSON

BSON has the most format-specific concerns due to MongoDB heritage:

| Concern | Details | Affected Features |
|---------|---------|-------------------|
| **`_id` semantics** | BSON/MongoDB treats `_id` specially: auto-generated, indexed, required | #13 ID, #21 ID_FIELD, #24 IdKeyMode BOTH |
| **ObjectId** | Native 12-byte ObjectId type; needs custom value handler | #48 Custom values |
| **Decimal128** | Native 128-bit decimal; BigDecimal mapping | #4 Double (precision) |
| **Int32 vs Int64** | BSON distinguishes int32 and int64; must map correctly | #2 Int, #3 Long |
| **BsonNull** | Explicit null type vs absent field | #28 Null handling |
| **Document size limit** | 16MB per BSON document | #46 Performance |
| **Key ordering** | BSON preserves insertion order; may affect field ordering | #14 Complex graph |
| **Enum as int** | Enum VALUE strategy stores ordinal; BSON int32 vs string | #15 Enum VALUE |

**Recommended priority features:** #2, #3, #13, #15, #21, #24, #28, #48

---

## 4. TCK Test Architecture

### 4.1 Proposed Structure

```
org.eclipse.fennec.codec/
  test/.../format/
    tck/
      AbstractCoreRoundTripTCK.java        ← P0 tests (14 tests)
      AbstractCodecFeatureTCK.java         ← P1 tests (20 tests)
      AbstractAdvancedFeatureTCK.java      ← P2 tests (14 tests)
      TCKModelHelper.java                  ← Shared model setup
      test-tck.ecore                       ← Comprehensive TCK model

    JsonFormatFeatureParityTest.java       ← extends all 3 (exists, needs extending)

org.eclipse.fennec.codec.yaml/
  test/.../
    YamlCoreRoundTripTest.java             ← extends AbstractCoreRoundTripTCK
    YamlCodecFeatureTest.java              ← extends AbstractCodecFeatureTCK (when ready)

org.eclipse.fennec.codec.cbor/
  test/.../
    CborCoreRoundTripTest.java             ← extends AbstractCoreRoundTripTCK
    CborCodecFeatureTest.java              ← extends AbstractCodecFeatureTCK (when ready)

org.eclipse.fennec.codec.bson/
  test/.../
    BsonCoreRoundTripTest.java             ← extends AbstractCoreRoundTripTCK
    BsonCodecFeatureTest.java              ← extends AbstractCodecFeatureTCK (when ready)
    BsonObjectIdTest.java                  ← format-specific (keep existing)
    BsonFormatDelegateTest.java            ← format-specific (keep existing)
```

### 4.2 Abstract TCK Base Class Contract

```java
abstract class AbstractCoreRoundTripTCK {

    /** Provide the format provider to test. */
    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    /** File extension for URIs (e.g., "json", "yaml", "cbor", "bson"). */
    protected abstract String getFileExtension();

    /**
     * Override to disable specific tests for formats that intentionally
     * don't support a feature. Must provide justification.
     */
    protected Set<String> unsupportedFeatures() {
        return Set.of();
    }
}
```

### 4.3 Relationship to Existing Tests

| Existing Test | TCK Replacement | Action |
|---------------|-----------------|--------|
| `AbstractFormatFeatureParityTest` | `AbstractCoreRoundTripTCK` | Evolve into P0 TCK |
| `JsonFormatFeatureParityTest` | `JsonCoreRoundTripTCKTest` | Rename + extend |
| `YamlFormatProviderTest` | `YamlCoreRoundTripTest` + keep provider metadata tests | Split |
| `CborFormatProviderTest` | `CborCoreRoundTripTest` + keep provider metadata tests | Split |
| `BsonFormatProviderTest` | `BsonCoreRoundTripTest` + keep provider metadata tests | Split |

The existing copy-pasted tests can be replaced by the TCK. Format-specific tests
(BSON delegate tests, provider metadata assertions) stay as separate classes.

### 4.4 Test Model Requirements

The TCK needs a comprehensive ecore model. Rather than one giant model, we can layer:

| Model | Used By | New Features Needed |
|-------|---------|---------------------|
| `test-format-parity.ecore` | P0 (exists) | None - sufficient for core round-trip |
| `test-tck-features.ecore` | P1 (new) | Polymorphism hierarchy, bidirectional refs, circular refs, EMaps |
| `test-advanced.ecore` | P2 (exists) | Already has polymorphism, bidirectional, circular |

---

## 5. Implementation Roadmap

### Phase 1: P0 Core TCK (immediate)

1. Rename `AbstractFormatFeatureParityTest` to `AbstractCoreRoundTripTCK`
2. Keep all existing tests (they already cover P0 items #1-#14)
3. Create TCK subclasses in YAML, CBOR, BSON projects
4. Remove copy-pasted `*FormatProviderTest` round-trip tests (keep provider metadata tests)
5. Verify all formats pass

### Phase 2: P1 Feature TCK (next)

1. Create `test-tck-features.ecore` with polymorphism, bidirectional, enum variations
2. Implement `AbstractCodecFeatureTCK` with tests #15-#34
3. Each test method is self-contained: configures strategy, creates model, round-trips
4. Format subclasses extend and run

### Phase 3: P2 Advanced TCK (later)

1. Implement `AbstractAdvancedFeatureTCK` with tests #35-#48
2. Performance tests as optional (not in CI by default)
3. Format-specific custom value tests (BSON ObjectId etc.)

---

## 6. Adding New Features - TCK Extension Rule

**Rule:** When a new codec feature is added that affects serialization or deserialization,
a corresponding TCK test MUST be added to the appropriate abstract class.

Checklist for new features:
- [ ] Add test to `AbstractCoreRoundTripTCK` (if fundamental) or `AbstractCodecFeatureTCK` (if strategy-based)
- [ ] Update this capability map with the new feature row
- [ ] Evaluate format-specific relevance (JSON/YAML/CBOR/BSON columns)
- [ ] Run all format TCK subclasses to verify
- [ ] If a format fails: fix format implementation OR mark as `unsupportedFeatures()` with justification

---

## Changelog

| Date | Change |
|------|--------|
| 2026-02-17 | Initial capability map with 48 features across 3 priority tiers |
