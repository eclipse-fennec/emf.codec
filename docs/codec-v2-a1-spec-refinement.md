# A.1 — Instance-Based Config Resolution — Spec Refinement & Contradiction Report

**Issue:** #54 (Phase A), work package **A.1**.
**Method:** workdoc §8.1 steps **1 (refine spec / draft change)** + **2 (uncover contradictions)**.
This document is a **review artifact** — it drafts the intended change to `02-config-resolution.md`
and lists the contradictions to clarify with the project lead (step 3). **No spec files are edited
yet.** Tests (step 4) and implementation (step 5) follow only after the open points below are settled.

**Baseline:** `feature/issue-54-fingerprint-phase-a`, 3543 tests green against model.metadata Build 8.

---

## 1. Ground truth (code) — the F6 collision has two halves

The class-name collision is not only on the *build* side; it is a build→consume pair:

| Half | Site | Behavior |
|---|---|---|
| **Build (write)** | `AspectToPropertiesConverter.buildAnnotationProperties` — `properties.put(eClass.getName(), classProps)` (`.../config/bridge/AspectToPropertiesConverter.java:139`), iterating **all** packages | class config stored under the **bare class name**; under same-nsURI multi-version the last-registered version **overwrites** the first |
| **Consume (read)** | `ConfigurationResolver.extractClassProperties(map, eClass)` **Pattern 2**: `String className = eClass.getName(); source.get(className)` (`.../config/ConfigurationResolver.java:855-861`); features via `extractFeatureProperties` Pattern 2/3 (`:912`, `:919-921`) | annotation config fetched back **by name** — so both ends of the bridge are name-keyed |

**What is already instance-safe (must be preserved, R1):**
- Result caches are instance-keyed: `typeConfigCache`/`classConfigCache`/`featureConfigCache` are
  `ConcurrentHashMap<EClass|EStructuralFeature, …>` via `computeIfAbsent` (`ConfigurationResolver.java:75-81`, `:135`, `:403`, `:468`).
- The **runtime** caller-config layers use instance-keyed **Pattern 1** (`ConfigProperty.ECLASS_CONFIG`
  → `eClassMap.get(eClass)`, `:842-853`; `EREFERENCE_CONFIG`/`EATTRIBUTE_CONFIG` `:887-909`). These are
  the F12-safe channels — untouched.
- So the **only** name-keyed source in the whole resolution path is the annotation bridge (confirms F11).
  No class-**URI** keying exists anywhere.

**Annotation-layer merge semantics that A.1 must reproduce exactly (W18):**
- Supertype walk lives **only** in `resolveTypeConfig` (`ConfigurationResolver.java:135-154`):
  `defaults → global → for each ec.getEAllSuperTypes() (parents-first) → child ec (last) → runtime layers`.
  → **parents-first, child-overrides**, full transitive closure (`getEAllSuperTypes` = effectively `inherit=ALL`).
- The supertype merge also goes through the **name-keyed** `extractClassProperties(annotationProperties, superType)`
  → so the fix must make the supertype walk **instance-based** too (`superType` identity), or a wrong-version
  supertype config could be picked up by name.
- No `resolve*Config` other than type does a parent walk (id/feature/reference/class/discriminator merge
  the exact instance only).
- `getClassProfile(EClass, "codec")` / `getClassMetadata(EClass)` are **not** called anywhere in the
  resolution path today — all annotation config flows through the name bridge.

---

## 2. Refinement draft — `02-config-resolution.md`

### 2.1 §4 Combined Resolution Algorithm — add an identity clause `[A.1 core]`

The algorithm text (`02:82-114`) is abstract ("EClass C", "EAnnotation on C") and not wrong, but it never
states the **key** by which a class/feature is matched. Add an explicit invariant:

> **Identity, not name.** Class- and feature-level configuration — from **every** source, annotations
> included — is resolved against the concrete **`EClass` / `EStructuralFeature` instance** (object
> identity; EMF does not override `equals`/`hashCode`). It is **never** keyed on the bare class name or a
> type URI. Two same-named `EClass`es from two package versions sharing one nsURI are **distinct
> instances** and resolve to **their own** configuration. The instance reaches the resolver from the
> object being (de)serialized (`eObject.eClass()`) or from a root-type hint held as an `EClass`.

### 2.2 §8 EffectiveConfig Pattern — full rewrite to the per-step/pin model `[A.1; D2]`

Per **D2**, §8 (and companion 01 §4–5) is rewritten now to the per-step / per-selected-version model:

- The illustrative merge `ConfigurationMerger.merge(...)` (`02:210-218`) does not reflect code
  (`ConfigurationResolver` merges lazily per instance; `EffectiveCodecConfig` is a thin delegator). Mark the
  snippet **illustrative**, not literal API.
- State the **instance-identity keying**: per-class/per-feature caches key by **EClass / EStructuralFeature
  instance** (`computeIfAbsent`), so the resolved config is naturally multi-version-correct — **no name map
  between the metadata and the resolved config**. Annotation config is sourced from `getClassMetadata`/
  `getClassProfile` and memoized here (D3).
- Rewrite "resolved once / immutable snapshot" → **immutable per selected package version**; the resolver's
  instance caches are the **per-load derived view**. Forward-reference the **pin** (per-load context, version
  *selection*) to the fingerprinting workdoc §2b `[DECIDED]`, and mark the pin/selection layer
  **not-yet-implemented** (A.1 ships the instance-identity part; selection lands in A.3/Phase B).
- Companion edit in **01 §4–5** (same wording: drop "resolved once, immutable snapshot" as a global claim →
  per-step immutable-per-version).

### 2.3 Behavior change to document (W18) `[A.1]`

Add a short note (candidate: end of §7 or a new §8 subsection):

> **Behavior change (multi-version correctness):** annotation config is matched by class instance, so an
> `EClass` that is **not registered** in the MetadataService no longer silently inherits the config of a
> **same-named, registered** class. Previously the name-keyed bridge would apply the registered class's
> config to any same-named class. Callers relying on that coincidence must register the class (or supply
> config via the instance-keyed caller channels).

This is the one intended deviation from prior *observable* behavior; everything else is R1-stable.

### 2.4 P-fixes inside chapter 02 (bundled into #54's spec deliverable)

- **P1 — `typeStrategy` default.** §11.3 table (`02:405`) says default **`NAME`**. Code is **`URI`**
  (`ConfigProperty.java:58` `TYPE_STRATEGY(..., "URI", …)`); chapters 00/05/99 already say URI; and **02 §5**
  itself (`02:132` "fall back to default (URI)") already says URI. → Fix §11.3 `NAME` → **`URI`**.
  (Resolves an intra-chapter contradiction, not only a cross-chapter one.)
- **P3 — per-class *string* addressing not implemented.** §12 "Property Map Format" shows
  `codec.Person.type.strategy=…` / `codec.Person.firstName.key=…` as if supported (`02:651-657`). The
  resolver *has* name-keyed read patterns (`:855`, `:912`, `:919`), but **no parser produces these dotted
  per-class keys** from a property string (F11: no `.`-splitting). → Mark the per-class/per-feature-string
  examples in §12 explicitly **"not yet implemented"** (same convention as 99 §2.1), and cross-reference the
  A.5 decision (version-agnostic) as governing the *future* implementation, not present behavior.

---

## 3. Contradiction report (§8.1 step 2)

| # | Contradiction | Location | Proposed resolution | Bucket |
|---|---|---|---|---|
| CR-1 | `typeStrategy` default stated as `NAME` | 02 §11.3 (`:405`) | → `URI` (matches code, 00/05/99, and 02 §5) | P1 |
| CR-2 | Per-class dotted string keys shown as usable | 02 §12 (`:651-657`) | mark "not yet implemented"; A.5 governs future | P3 |
| CR-3 | §8 merge snippet / "immutable snapshot" reads as literal API and hides instance-keying | 02 §8 (`:205-229`) | mark illustrative; state instance-keyed caches; defer per-step/pin rewrite to A.3 | W1 (scoped) |
| CR-4 | **Annotation inheritance is documented as general but implemented for *type config only*.** §3 scope chain + §11.7 `inherit` (DIRECT/ALL/NONE) imply hierarchy inheritance for all class-level config; code walks supertypes **only** in `resolveTypeConfig`, and `inherit` DIRECT/NONE is **never read** (`CODEC_INHERIT` has zero consumers; only `getEAllSuperTypes` = ALL). | 02 §3 (`:55-78`), §11.7 (`:476-489`) vs `ConfigurationResolver.java:135-154` | **needs lead decision — see O1.** A.1 must reproduce *current* behavior (type-only, ALL); options: (a) mark §11.7 `inherit` modes + non-type inheritance "not implemented" (P-style), or (b) file a follow-up to extend inheritance. Do **not** silently broaden inheritance under R1. | K/decision |

CR-4 is the substantive one: it was **not** in the fingerprinting workdoc's P-list and surfaced from the
consumption-side reading. It is directly in A.1's path because W18 requires reproducing the merge semantics
"exactly" — which means reproducing the *type-only, ALL* reality, not the broader spec promise.

---

## 4. Decisions from the lead (§8.1 step 3 — settled 2026-07-24)

- **D1 (CR-4 / O1) — mark not-implemented.** §11.7 `inherit` DIRECT/NONE **and** inheritance for
  non-type-config are marked **"not yet implemented"** (convention as 99 §2.1). A.1 reproduces the existing
  **type-config-only** supertype walk, now instance-based (`getEAllSuperTypes()` instances, parents-first,
  child-overrides). No broadening of inheritance under R1.
- **D2 (CR-3 / O2) — full §8 rewrite now.** §8 (and its companion 01 §4–5) is rewritten to the
  **per-step / per-selected-version pin** model (W1) in this A.1 pass — §8 is touched once. The pin/version-
  *selection* concepts are forward-referenced to the already-`[DECIDED]` §2b of the fingerprinting workdoc
  and marked not-yet-implemented where the code does not do them yet; A.1 implements only the instance-
  identity part.
- **D3 (O3) — service-sourced, instance-memoized.** Annotation config is resolved from
  `getClassMetadata(EClass)` / `getClassProfile(EClass,"codec")` (the F5 `PackageMetadata` entry point — **no
  bare-name map in between**), and the **derived** config is **memoized in an instance-keyed cache** so the
  MetadataService is not queried on every resolve. This reuses the resolver's existing instance
  `computeIfAbsent` caches (`typeConfigCache`/`classConfigCache`/`featureConfigCache`); the flattened
  name-keyed `annotationProperties` map and the name-keyed read patterns for the annotation layer are removed.
  (Lead's note verbatim: *"man könnte aber die gefundenen Daten dann in ein keyed map legen, damit man nicht
  jedes mal den Service fragen muss."*)

---

## 5. Applied spec edits (§8.1 step 1 finalized — 2026-07-24)

Committed to the live spec (`docs/codec-v2-spec/`) per D1–D3:

- **02 §4** — added the "Resolution identity — instance, not name" note (A.1 core).
- **02 §8** — full rewrite to the per-step / instance-keyed model (D2): illustrative-snippet marking,
  instance-identity keying, `getClassMetadata`/`getClassProfile` sourcing + instance memoization (D3),
  "immutable per selected version" wording, forward-referenced pin/selection as `[not yet implemented]`,
  and the W18 behavior-change note.
- **02 §11.3** — `typeStrategy` default `NAME` → **`URI`** (P1 / CR-1).
- **02 §11.7** — `inherit` DIRECT/NONE + non-type-config inheritance marked **not yet implemented** (D1 / CR-4).
- **02 §12** — per-class/per-feature *string* keys marked **not yet implemented** (P3 / CR-2).
- **01 §2** — `EffectiveCodecConfig` box label → "(per-step, instance-keyed)" + per-step note incl. a
  **naming-caveat** (code = `ClassConfig`/`FeatureConfig`/`resolveClassConfig`; spec's `EffectiveClassConfig`/
  `getClassConfig` are illustrative aliases).

**Deferred / follow-up (out of A.1 scope, tracked here):**
- Spec-wide rename `EffectiveClassConfig`/`EffectiveFeatureConfig`/`getClassConfig` → code names
  (`ClassConfig`/`FeatureConfig`/`resolveClassConfig`) — a naming cleanup across chapters, not A.1.
- D1 follow-up (optional): a separate issue to implement `inherit`-aware / non-type-config inheritance.

## 6. Cross-chapter contradiction re-scan (done 2026-07-24)

Scanned 05/06/07/13/15 against the settled facts. **05, 06, 07 clean.** 06 is the *correct* reference for
the LENIENT default. Findings + fixes **applied**:

| Chapter/line | Was | Fixed to | Bucket |
|---|---|---|---|
| 13 §5.1 (mode table) | `AUTO_DETECT **(default)**` | `LENIENT **(default)**`; AUTO_DETECT opt-in, groups with LENIENT | P0 |
| 13 §2.5 javadoc (`CODEC_ROOT_TYPE`) | `Value: EClass` | `Value: EClass or String (type URI / qualified name)` | P5 |
| 13 §8 error catalog | "Invalid CODEC_ROOT_TYPE (not an EClass)" | "(not a resolvable EClass or String type identifier)" | P5 |
| 15 §-options table | `deserializationMode … STRICT` + stale "section 6.4" xref | `LENIENT` + "section 2.1" | P0 |

**Follow-up (out of A.1 scope, tracked):** scan **16-annotation-reference.md** for `inherit=DIRECT/NONE` /
non-type inheritance described as implemented — most likely remaining place needing the same
"not yet implemented" note (D1/CR-4). Not touched in this pass.

## 7. Anchor test — re-created & confirmed failing (§8.1 step 4, done 2026-07-24)

`org.eclipse.fennec.codec/test/.../resource/SameNsUriMultiVersionCodecTest.java` — two same-nsURI packages,
identical structure, only the codec `key` annotation differs (`alpha` vs `beta`); R6 cross-assertions.

Result against current (name-keyed) code — **fails exactly as predicted**:
- ❌ `serializesWithOwnConfig` — version A serializes without `alpha` (B won the name collision).
- ❌ `deserializesWithOwnConfig` — version A cannot read its own `alpha` key.
- ✅ `roundTripsPerVersion` — passes **by accident** (ser+deser share the same wrong config) → this is *why*
  the coincidence-proof cross-assertions above are mandatory (F9 rationale confirmed live).
- ✅ `unregisterDoesNotAffectOther` — survivor guard; passes (collision gone once B is removed).

## 8. Implementation — A.1 done (§8.1 step 5, 2026-07-24)

**Change (minimal, resolver untouched):** `AspectToPropertiesConverter.buildAnnotationProperties` now emits
**instance-keyed** annotation config using the shape the resolver already consumes via its instance-keyed
Pattern 1 — `ECLASS_CONFIG` (`Map<EClass, props>`), `EREFERENCE_CONFIG` (`Map<EReference, props>`),
`EATTRIBUTE_CONFIG` (`Map<EAttribute, props>`) — instead of `properties.put(eClass.getName(), …)`. The
bare-name key is gone at the source; the resolver's name-keyed Pattern 2/3 no longer match for the
annotation layer.

**Why this satisfies D1–D3:**
- **D3** — the MetadataService is read **once** (bridge iterates `registry.getPackages()` at
  `CodecResource` construction) into an instance-keyed map; the resolver memoizes derived config in its
  existing per-instance `computeIfAbsent` caches → no per-call service query, no name map.
- **D1** — `resolveTypeConfig`'s supertype walk (`getEAllSuperTypes()`, parents-first, child-overrides)
  now resolves each supertype by **instance** through Pattern 1, automatically — no resolver edit, type-
  config-only inheritance preserved.
- **R1** — `ConfigurationResolver` (1811 lines) is unchanged; the fix is confined to the bridge. No test
  referenced the old name-keyed bridge shape.

**Result:**
- ✅ Anchor `SameNsUriMultiVersionCodecTest` — all 4 green (the two coincidence-proof cases flipped
  red→green).
- ✅ R1 baseline — **3547 tests, 0 failures, 0 errors, 2 skipped** (3543 prior + 4 new); no regression.

**Files:** `org.eclipse.fennec.codec/.../config/bridge/AspectToPropertiesConverter.java` (bridge output +
imports + javadoc). Behavior change per W18 (unregistered same-named EClass no longer inherits a registered
class's config) is now in effect and documented in 02 §8.

## 9. Remaining (out of A.1 scope, tracked)

- 16-annotation-reference.md `inherit`-modes scan (D1/CR-4 follow-up).
- Spec-wide `EffectiveClassConfig`→`ClassConfig` naming cleanup.
- Optional issue: `inherit`-aware / non-type-config inheritance.
- Phase A remainder: **A.2/A.4** (`codec.rootFingerprint`), **A.3+B.5** (candidate rule + resolution
  order), **B.6** (discriminator per-step views).

*Created for #54 / A.1; source of truth = `docs/codec-v2-spec/`; see `docs/codec-v2-fingerprinting-workdoc.md` §3 A.1, W18, F6/F11/F12.*
