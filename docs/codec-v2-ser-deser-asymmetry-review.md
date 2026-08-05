# Codec V2 — ser/deser asymmetry review

**Date:** 2026-08-05 · **Status:** assessment only, nothing implemented · **Scope:** all four entry planes (id, type/supertype, attribute, reference) plus entry orchestration

## Why this document

A sweep for "written on one side, not read on the other" produced ~34 candidates. Raw
candidate counts are misleading: the codec has **deliberately one-directional planes**, and
flagging those as round-trip defects is a false positive.

The calibration case is the supertype plane (Mark, 2026-08-05): `_supertype` exists for
consumers and interop, the object state is rebuilt from `_type` plus features, and reading it
only ever makes sense as *verification*. `SuperTypeDeserializationEntry` is accordingly
opt-in via `validateSuperTypeHierarchy` and does nothing without it. "Not read" is therefore
not data loss there.

The spec itself settles most of these calls — it documents intended asymmetry explicitly
(10-reference.md §9.2 design note and §10.4 symmetry table, 11-feature.md §13.1 visibility
gate table, 13-load-save-options.md §2.11, 09-id.md §11 flow) and it states where symmetry or
losslessness **is** promised. Every finding below was re-judged against that.

## Decisions (Mark, 2026-08-05)

Two rules were set for this review and they override some of the ratings below:

1. **Where the codec does more than the spec, the spec is pulled up to the code.** The written
   output is the contract. Concretely: if the writer emits an array type, the reader must read
   it back and spec §7.1 gains the type — see GAP-4, now a BUG.
2. **Where the spec is vague, it gets sharpened rather than reinterpreted.** Ambiguities are
   resolved by adding wording, not by silently picking a reading.

Four open questions were decided:

| Question | Decision |
|---|---|
| STRUCTURED `_id`: feature names (code) vs. `idKeyMode`/`idValueKey` (spec) | **Code follows the spec** — implement `idKeyMode` inside the `_id` object and read/write `idValueKey`; existing tests that pin today's shape get pulled along |
| `java.util.Date` in plain JSON | **Writer switches to the EMF canonical ISO form** when no `dateFormat` is set, so JSON round-trips; BSON keeps its native path |
| `codec.flatten` | **Documented as a deliberately write-only export feature** (read side ignores it cleanly instead of warning) — reading it back is structurally ambiguous |
| `idFeatures` pointing at an EReference | **Implement it** on both sides as the spec already describes |

## Rating scheme

| Class | Meaning | Action |
|---|---|---|
| **BUG** | Breaks a promise the spec makes (symmetry required, lossless round trip, documented behavior) | fix |
| **CRASH** | Hard failure, independent of the symmetry question | fix first |
| **BY DESIGN** | Spec documents the one-directionality | none |
| **KNOWN LIMIT** | Spec names it as not-yet-implemented | roadmap, not a new finding |
| **RESIDUE** | Consequence of a legitimate asymmetry: diagnostic noise, `strictOnUnknown` trap, unreachable opt-in | cheap cleanup |
| **GAP** | Missing/undocumented on *both* sides — a feature or spec question, not an asymmetry | decide separately |

---

## CRASH

**TYPE-1 — `typeFormat=STRUCTURED` + `superTypeSerialize=true` fails on save.**
`ser/TypeSerializationEntry.java:360` takes the raw `superTypeConfig.getSuperTypeKey()`
(default `null`) instead of `getEffectiveSuperTypeKey(format)` that the PLAIN entry uses
(`ser/SuperTypeSerializationEntry.java:81`). The path is reachable whenever both options are
set (`ser/CodecEObjectSerializer.java:186-190`) and ends in
`gen.writeArrayPropertyStart(null)`. Documented, valid config combination (spec 07 §3/§6.1).
No test combines STRUCTURED with supertypes; the unit tests always set `superTypeKey`
explicitly. **One-line fix plus a test.**

---

## BUG — breaks a stated promise

**ID-1 — a single-entry `idFeatures` list is ignored on the PLAIN read path.**
Ser always uses the configured feature; PLAIN deser honors `idFeatures` only from two entries
up (`deser/IdDeserializationEntry.java:150`), so one entry falls into the "single eID
attribute" branch: the value lands on the eID attribute, or is dropped when the class has
none. STRUCTURED handles it correctly, which is what makes PLAIN the broken side. Silent data
loss on an ordinary config. Spec §1 gives `idFeatures` precedence. No test — every
`idFeatures` in the test tree has ≥2 entries.

**REF-1 — cross-resource references are written as same-document fragments on the plain JSON
save path.** `ContextHelper.RESOURCE` is attached only on the format-provider path
(`resource/CodecResource.java:499`) and on load (`:609`), never for the default
`mapper.writeValue`. Without it, `ser/ReferenceSerializationEntry.java:375-391` classifies
every foreign-resource reference as document-internal and writes a bare `//@companies.0`,
which then resolves against the *loading* resource. Note this is a **write-side information
loss**, independent of the read-side limitation in REF-KL1 below: the document no longer
records that the target was external. Cross-document containment is inlined for the same
reason.

**REF-2 — mixed multi-valued references lose elements.** Expanded/projection elements are
appended immediately (`deser/ReferenceDeserializationEntry.java:635,649`), plain `$ref`
elements are only recorded with their original index (`:639`) and patched later via
`list.set(index, …)` (`resource/CodecResource.java:929-936`). In a mixed array the recorded
index no longer matches the shortened list, so the patch overwrites a different element.
Spec 10 §9.2 explicitly lists "Multi-valued: mixed PLAIN + STRUCTURED → each element
auto-detected independently" as supported.

**TYPE-3 / ORCH-4 — class- and reference-scoped `typeKey` / `typeStrategy` are applied on
write and ignored on read.** Ser resolves with reference context
(`ser/CodecEObjectSerializer.java:126-129`); `isTypeKey` consults only the hint class's config
and then the global one (`deser/CodecEObjectDeserializer.java:335-357`), and the strategy
comes from `resolveGlobalTypeConfig()` (`:511-516`). Result: the concrete subtype is lost or
the load fails. Documented feature (spec 06 §1.5.4). Existing tests assert the written JSON
only, never load it back.

**ATTR-1 — `Map`/`List` in `EJavaObject` attributes are written as `toString()`.**
The writer has no `Map`/`Collection` branch and falls into
`gen.writeString(value.toString())` (`ser/AttributeSerializationEntry.java:202`), while the
reader rebuilds `Map`/`List` from JSON objects and arrays. Spec 11 §9.3 states the mapping
(`List/Collection → Array`, `Map → Object`) and claims "lossless roundtrips" verbatim.

**REF-4 — smart compression writes short type names into reference objects that the reference
type reader rejects.** `ser/ReferenceSerializationEntry.java:488-496` emits `"Manager"`;
`util/TypeResolutionHelper.java:331-333` treats only values containing `#` as resolvable, so
the concrete subtype degrades to the declared reference type — or the reference is dropped
when that type is abstract. Note the *object-level* type reader does resolve simple names
(`deser/TypeDeserializationEntry.java:443-473`); the two type readers diverge. Config
symmetry is given here, so spec 05's symmetry requirement does not excuse it.

**ID-2 / ORCH-3 — the `_separator` written for PLAIN compound ids is never read.**
Ser writes it (`ser/IdSerializationEntry.java:157-160`); PLAIN deser always uses
`config.getSeparator()` — `getSeparatorKey()` appears in the deserializer only in the
STRUCTURED branch (`deser/IdDeserializationEntry.java:243`). Spec §9.3 step 2 requires the
in-document separator to win. Weakened but not excused by the config-symmetry doctrine:
symmetric config makes it invisible, which is exactly why no test caught it.

**TYPE-4 — a custom `typeSchemaKey` is written but hardcoded on read.**
Ser uses the configured key (`ser/TypeSerializationEntry.java:181-188,282-288`), the reader
accepts only `_schema` and `@vocab` (`deser/CodecEObjectDeserializer.java:87,362-365`). The
schema is dropped and the bare class name may then be unresolvable.

**ATTR-4 — `forceWrite` and `forceRead` un-gate each other.** For volatile/transient/derived
features the resolver sets `ignore=true` only when *neither* flag is set
(`ConfigurationResolver.java:516-520`), so one flag opens both planes. Spec 11 §605-609 keeps
them separate. Existing tests only cover "both set" or "neither set".

**REF-6 — proxy-with-projection keeps the raw ref string as proxy URI.**
`deser/ReferenceDeserializationEntry.java:505,633` calls `URI.createURI(refUri)` verbatim,
while the plain-proxy path absolutizes and tries local resolution first
(`resource/CodecResource.java:975-981,1038-1061`). A same-document projection is therefore
never resolvable although the identical reference without projection is.

**TYPE-5 — an abstract fallback type throws instead of the LENIENT skip.**
`deser/CodecEObjectDeserializer.java:300-309` uses the hint without an abstractness check →
`deser/DeserializationState.java:235` throws and aborts the whole load. Spec 06 §6.3.0 step 4c
requires WARNING + skip in LENIENT. The featurePath flow does guard (`:1079`).

**TYPE-6 — NUMERIC type resolution bypasses the per-load `PackageResolver`.**
IDs are resolved straight from `EPackage.Registry.INSTANCE`
(`util/TypeResolutionHelper.java:224-259`), so an in-band fingerprint or pinned version does
not steer resolution — while the writer emits IDs of one specific package version. Bites
exactly the multi-version setups the fingerprint feature targets.

**ID-4 — id values are written with `toString()` and read with `EcoreUtil.createFromString()`.**
`ser/IdSerializationEntry.java:228,260` vs `deser/IdDeserializationEntry.java:399,422`. For
`EDate` ids and custom datatypes with a non-identity converter the id cannot be restored;
`dateFormat` is honored for ordinary attributes and ignored for ids. Same root cause as the
`EDate` item under KNOWN LIMIT.

**ID-5 — `idKeyMode=NONE` is a hard disable on the write side only.**
No keyMode check exists anywhere in `deser/IdDeserializationEntry.java`. Spec §11 step 1 is
explicit: "NONE is a hard disable", the value reader is ignored too. Practically shielded by
the config-symmetry doctrine, so low severity.

**ATTR-5 — `null` elements in multi-valued attributes are written and dropped on read.**
`ser/AttributeSerializationEntry.java:146-151` writes them,
`deser/AttributeDeserializationEntry.java:236-241` discards them, shifting all following
indices. The spec covers only *field*-level null for many-valued features, so this sits at the
BUG/GAP border — the index shift is what makes me keep it here.

**ID-7 — the PLAIN id read path lacks the `isChangeable`/`isMany` guards every other path
applies.** `deser/IdDeserializationEntry.java:168-173` calls `eSet` unguarded, while STRUCTURED
filters (`:280`) and the general feature path does too. The two formats disagree; robustness
issue rather than data loss.

---

## BY DESIGN — no defect

**ORCH-1 / TYPE-2 — `_supertype` is written but not read.** The calibration case. The plane is
one-directional; state is rebuilt from `_type` and features. See RESIDUE below for what is
left of it.

**ATTR-6 — non-changeable attributes are written but have no read entry.** Spec 11 §13.1
specifies *different* visibility gates per direction: write checks
ignore/ignoreWrite/transient/volatile/derived + forceWrite, read additionally checks
`changeable`. Spec 11:609 states non-changeable features cannot be set even with
`forceRead=true`, and 10-reference.md:817 says excluded features have no entry and their JSON
fields "are skipped as unknown". Exactly the supertype pattern.

**ATTR-7 — `enumSerialization` is not consulted on read.** Spec 11 §459-474 prescribes the
name-then-literal lookup the reader implements. Only the spec's claim that all three
strategies round-trip is wrong for enums whose name set and literal set collide → that claim
is a GAP, the code is fine.

**ORCH-5 — the read-side fingerprint key comes from caller options only.** Deliberate and
commented in place (`resource/CodecResource.java:382-387`), see spec 06 §8.5 chicken-and-egg.

**Write-only by spec, correctly one-directional:** `idOnTop`, `fieldOrder`, `serializeNull` /
`serializeEmpty` / `serializeDefault` (11-feature.md §214-268 says "symmetry not required"),
`expand`/`expandGlobal`, `serializeInstanceType`, `refFormat` (auto-detected on read per
10-reference.md §10.4).

---

## KNOWN LIMIT — already documented as unimplemented

**REF-KL1 — cross-document containment cannot be read back** (written as a ref object, the
reader builds a new empty object; with `refFormat=PLAIN` the value is dropped entirely).
Spec 10 §9.3 states it outright: "Full automatic cross-resource reference resolution during
deserialization is not yet implemented." Covers the read side of REF-3 as well — but *not*
REF-1, which is a write-side defect.

**ATTR-3 — `java.util.Date` in plain JSON without `dateFormat` cannot be read back.**
`writeDateValue` falls back to `date.toString()`
(`ser/AttributeSerializationEntry.java:219`), which neither EMF nor the reader's two patterns
parse. Known from #97/#98: solved natively for BSON, consciously left open for JSON, and the
method javadoc says the fallback is not machine-readable. Spec 11 §7.1 meanwhile documents the
`Date[]` wire form as `["2025-01-14", …]`, which the writer never produces — spec and
implementation contradict each other. **Decided: the writer moves to the canonical ISO form**,
so this leaves KNOWN LIMIT and becomes a fix.

---

## RESIDUE — cheap cleanups, no data at risk

- **The deserializer does not recognize its own `_supertype` key.** Registered under `null`
  (`deser/SuperTypeDeserializationEntry.java:74` vs `SuperTypeConfig.java:257`), so the field
  takes the unknown-property path: one warning diagnostic per object, and under
  `strictOnUnknown=true` a hard `IllegalStateException` while reading the codec's own output
  (`deser/CodecEObjectDeserializer.java:867`). Same effect for non-changeable attributes
  (ATTR-6) and flattened EMaps (GAP-2). Resolving the read key via
  `getEffectiveSuperTypeKey(format)` fixes it in one line.
- **The opt-in supertype verification is unreachable.** `validateSuperTypeHierarchy=true` never
  fires, because the entry sits under the `null` key; it only works if `superTypeKey` is also
  set by hand. Same for the STRUCTURED validation path
  (`deser/TypeDeserializationEntry.java:305-406`), reachable only via `deserializeWithSchemaHint`,
  which no production code calls.
- **`superTypeFormat` affects the key default but never placement** — placement follows
  `typeConfig.getFormat()` alone (`ser/CodecEObjectSerializer.java:172-204`), contradicting
  spec 07 §6.1.

---

## GAP — missing on both sides, decide separately

- **GAP-1 — `idValueKey` has no consumer at all.** Only the property bridge references it
  (`config/bridge/AspectToPropertiesConverter.java:201`). STRUCTURED always writes feature
  names, the reader only matches feature names — self-consistent, so round trips survive, but
  spec §3.3/§7 asks for the combined value under `idValueKey`, and the current STRUCTURED tests
  pin the deviation. **Decided: the code follows the spec**, tests get pulled along.
- **GAP-2 — `codec.flatten` exists only on the write side and not in the spec at all.**
  `isFlatten()` has one consumer, EMap serialization
  (`ser/ReferenceSerializationEntry.java:223`); a flattened map comes back empty. The word
  "flatten" appears in no spec file and in no test. **Decided: document it as a deliberately
  write-only export feature**, and have the read side ignore the keys cleanly instead of
  warning — reassembling the map is ambiguous once the object carries other unknown fields.
- **GAP-3 — `idFeatures` pointing at an EReference is unimplemented on both sides** (spec §4/§9.6).
  Not an asymmetry — but the write side emits an object `toString()` including the identity
  hash as `_id`, which is actively wrong output. **Decided: implement it** on both sides as the
  spec already describes.
- ~~**GAP-4 — array component types beyond the documented set.**~~ **Re-rated BUG by decision 1.**
  `byte[]`, `short[]`, `char[]` and all boxed arrays are written as JSON numbers but come back as
  zero-length arrays (`deser/AttributeDeserializationEntry.java:393-410` + allowlist `:641`).
  Spec 11 §7.1 does not list them, so on spec grounds alone no promise is broken — but the
  writer emits them, so the reader must restore them and §7.1 gains the types.
- **GAP-5 — plain Java enums (non-`Enumerator`) do not round-trip.** Writer has a branch
  (`ser/AttributeSerializationEntry.java:312-324`), reader has none; `VALUE` even produces a
  `ClassCastException` outside the guarded block. Requires an unusual model — generated EMF
  enums implement `Enumerator`.
- **GAP-6 — alternate type keys `_class`/`@type`/`eClass` are unreachable** because the global
  `typeKey` default is non-null, so `deser/CodecEObjectDeserializer.java:353-356` is dead code.
  Interop-only; the writer never emits them.
- **GAP-7 — discriminator values are matched against the context schema before the mappings**
  (`deser/TypeDeserializationEntry.java:524-572`), so a mapping value colliding with a class
  name resolves to the class. Both sides otherwise agree.
- **Spec-vs-impl notes without round-trip impact:** `SuperTypeSelection.ALL` collects direct
  supertypes only; `CLASS` strategy falls back to `eClass.getName()` where spec 06 §5.0 demands
  an ERROR; `DeserializationMode.AUTO_DETECT` has no callers; `classifierKey` (spec 06 §1.7) does
  not exist in `TypeConfig` and is hardcoded symmetrically on both sides.

---

## Suggested order

1. **TYPE-1** — crash, one line, immediate.
2. **ID-1, REF-1, REF-2** — silent data loss on ordinary configs.
3. **ATTR-1, TYPE-3, REF-4** — silent loss on documented features.
4. The remaining BUG entries as one batch; **RESIDUE** as a second, cheap batch (one shared
   root cause: raw vs. effective key resolution).
5. **GAP** entries need a decision before any code: implement, document as unsupported, or
   remove the half-feature.

## Two root causes

Nearly every BUG entry is one of two shapes:

1. **The write side resolves an effective/scoped value, the read side reads it raw or global.**
   TYPE-1, TYPE-3, TYPE-4, ID-2, and the `_supertype` residue.
2. **The write side has a permissive fallback, the read side a closed allowlist.**
   ATTR-1, GAP-4, ID-4, and the `EDate` limitation.

Both stayed invisible because the round-trip tests drive **both sides from the same resolver**
and the test models contain only well-supported types. Any fix should come with a test that
loads with a *fresh* resolver, not the one used for saving.
