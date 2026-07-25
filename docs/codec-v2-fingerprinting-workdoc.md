# Fingerprint-Aware Codec — Working Document

**Status:** WORKING DOCUMENT — precursor to spec changes. Nothing here is implemented
unless explicitly marked. Decisions are tagged `[DECIDED]`, `[PROPOSED]`, `[OPEN]`.

**Goal:** make the codec consistent in the presence of multiple live `EPackage`
versions sharing the same nsURI (stage-aware registration), by incorporating the
model fingerprint (model.metadata#15) into configuration resolution,
serialization, and deserialization.

**Drivers:**
- [model.atlas#156](https://github.com/eclipse-fennec/model.atlas/issues/156) —
  production bug: with two live versions of one nsURI, objects of one version were
  serialized with the other version's metadata; unregistering one version broke the
  survivor.
- [model.metadata#15](https://github.com/eclipse-fennec/model.metadata/issues/15) —
  fingerprint-keyed registry (implemented, consumed since issue #53).
- emf.codec#54 — call-site switch to stateless fingerprint lookup (in progress; the
  coincidence-proof test below was written for it and exposed the codec-side gap).

---

## 1. Verified facts (ground truth, with code references)

These were verified against the canonical `model.metadata` snapshot (1.0.0-SNAPSHOT,
2026-07-23) and the current codec `snapshot` branch. They are the foundation for
everything below.

| # | Fact | Evidence |
|---|------|----------|
| F1 | The model fingerprint **includes EAnnotations** (sorted by source, details by key; only GenModel `documentation` excluded). Two versions differing *only* in codec annotations therefore have **different fingerprints**. | `DefaultFingerprintService` (model.metadata), class javadoc + `appendAnnotations` |
| F2 | The fingerprint has a **canonicalization scheme version tag** ("bump when the scheme changes"). Old data must keep resolving after a scheme bump. | `DefaultFingerprintService.SCHEME` |
| F3 | Fingerprints exist **only at package level**: `PackageMetadata.getModelFingerprint()`, `MetadataService.getPackageMetadataByFingerprint(String)`. There is **no** class/feature/operation fingerprint — and none is needed: sub-package identity is answered by **EClass/EFeature instance identity** within the fingerprinted package. | api jar javap; `ClassMetadata`/`FeatureMetadata`/`OperationMetadata` have no fingerprint field |
| F4 | `MetadataService.getClassMetadata(EClass)` is **instance-keyed** (`Map<EClass, ClassMetadata> classesByEClass`) — two same-named EClasses from two versions resolve to their own metadata. This part is already multi-version-safe. | `MetadataServiceImpl:104,227` |
| F5 | `MetadataService.getPackageMetadata(EPackage)` is fingerprint-keyed, memoized per instance, resolve-or-build on miss, never null for a non-null package. | `MetadataService` javadoc (api) |
| F6 | The codec's config bridge **flattens all registered packages into one map keyed by bare class name** (`properties.put(eClass.getName(), classProps)`). Under same-nsURI multi-version, the last registered version **overwrites** the first — every object of that class name then (de)serializes with the wrong version's config. This is the codec-side root cause. | `AspectToPropertiesConverter.buildAnnotationProperties` (`org.eclipse.fennec.codec/src/.../config/bridge/AspectToPropertiesConverter.java:139`), wired in `CodecResource.enrichWithAnnotations` |
| F7 | `CODEC_ROOT_TYPE` accepts **both** an `EClass` instance **and a String URI**; `CODEC_ROOT_SCHEMA` is String-based. The String path resolves via `MetadataService.getClassMetadataByURI(uri)`. | `CodecResourceHelper.resolveRootEClass:63-78`, `CodecResource:105-110`, `CodecOptions:63-69` |
| F8 | The URI index behind `getClassMetadataByURI` is a plain map keyed by type URI: **last-wins** on registration collision (`classesByURI.put`) and **destructive on unregister** (`classesByURI.remove(typeURI)` — removing version 1 deletes the URI entry even while version 2 survives). Under multi-version, the String path is therefore ambiguous *and* fragile. | `MapBasedMetadataIndex:52,94,182,298` (model.metadata) |
| F11 | **The ONLY class-name-keyed config path in code is the annotation bridge** (`AspectToPropertiesConverter:139`, `properties.put(eClass.getName(), …)` — the F6 site A.1 fixes). Per-class *string* addressing (`codec.Person.type.strategy`, spec 02 §11.4) is **not implemented** — no hierarchical/per-class key parsing exists anywhere in the config package (verified: no `.`-splitting of keys). Consequence: **A.1 is the sole multi-version config-collision point that exists in code today.** | `CodecResource:844-850`, config-package grep |
| F12 | **All instance-keyed caller-config channels are already multi-version-safe** (verified): `CODEC_ECLASS_CONFIG` = `Map<EClass,…>`, `CODEC_EREFERENCE_CONFIG` = `Map<EReference,…>`, `CODEC_EATTRIBUTE_CONFIG` = `Map<EAttribute,…>`; feature type hints and value readers/writers are all `Map<EStructuralFeature,…>`. Distinct version instances (`entityA` ≠ `entityB`) never collide — same principle as `classesByEClass`. So the collision is confined to the *name-keyed* annotation bridge (F6/A.1); the *string/URI-addressed* per-class config does not exist (F11/P3). | `CodecOptions:174-186`, `ContextHelper:81-135` |
| F10 | model.metadata **already stores all versions per nsURI**: `packagesByNsURI` is a `Map<String, List<PackageMetadata>>`. `getPackageMetadata(String nsURI)` deliberately returns the **most recently registered** version ("best effort under multi-version ambiguity" — verbatim comment). So today an nsURI lookup yields exactly one (the last) version; the others exist but are not reachable through the API. | `MetadataServiceImpl:91` + `getPackageMetadata(String)` impl |
| F9 | A coincidence-proof e2e test exists (uncommitted): two packages, same nsURI, same structure, only the configured JSON key differs (`alpha` vs `beta`) — so the observable output is driven *solely* by the config. It currently fails against F6 exactly as predicted (`{"_type":"#//Entity","beta":"X"}` for a version-A object). A structure-only test passes **by accident** (ser+deser use the same wrong config consistently) — this is why the assertions are keyed to the diverging config. | `org.eclipse.fennec.codec/test/.../resource/SameNsUriMultiVersionCodecTest.java` |

---

## 2. Use cases A/B — runtime split, implementation phases `[DECIDED]`

At **runtime**, A and B are a use-case split: two coexisting paths that both must
work permanently. In **implementation**, they are phases: B's version selection
feeds into A's per-step scoped resolution, so A is the foundation and B builds on
it (B without A has no footing; A without B is incomplete for self-describing
data).

**`[DECIDED]` Both phases are implemented now, back to back on the same branch —
not deferred** — accepting that #53 (done, green) waits unmerged behind the B
timeline and the branch lives longer (rebase risk if `snapshot` moves). Phase B
implementation starts only after the `[OPEN]` decisions gating it (B.1 carrier,
Q1, document pinning) are settled — the gate is decision, not code.

Issue mapping: **#54 = Phase A** (matches its issue text); **Phase B gets its own
issue** (spec extension: fingerprint in data).

### Scenario A — the version is answered by the caller

The caller holds the concrete instance: a per-stage `ResourceSet`, or
`CODEC_ROOT_TYPE` with an `EClass` instance. **No in-band fingerprint is needed** —
the instance *is* the version. The codec must only stop losing that instance
internally.

Covers: atlas#156 ("the version question is answered by the caller"), the e2e test,
and all embedded/OSGi usage where the caller owns the ResourceSet.

### Scenario B — self-describing data

The reader must derive the version **from the data stream alone** (nsURI in
`_type`), and multiple versions live under that nsURI. Only here does the
fingerprint need to travel **inside the data** (plus load-option overrides for
foreign data). This is the format/spec extension.

---

## 2b. Architectural principle: stateless per-step config context `[DECIDED — direction]`

The codec is **stateless**: all configuration — including the fingerprinted
`PackageMetadata` — is an *input to one conversion step*. Any derived lookup
structure (eclassURI → config map) is **scoped to that single conversion step**,
never global. The global, name-keyed bridge (F6) violates this principle; fixing
A.1 *is* restoring it.

**Fingerprint currency `[DECIDED]`:** every fingerprint that is *communicated* —
in options (`codec.rootFingerprint`), in serialized data (root or per-site), in
diagnostics — is an **EPackage fingerprint** (`PackageMetadata.getModelFingerprint()`),
the currency the MetadataService can resolve (`getPackageMetadataByFingerprint`).
The class within a version is identified by `_type` (name/URI) — there are no
class/feature/operation fingerprints anywhere (F3: below the package, instance
identity suffices). Whatever the codec derives internally (e.g. a per-step
EClass→config map) is implementation detail: valid for one conversion step,
never serialized, never API. In a document containing objects from several
packages (or, in Phase B, mixed versions), each in-band fingerprint is still a
package fingerprint — the one of the package owning the type at that site.

Consequences:

- **Save:** the object graph supplies concrete instances throughout
  (`eObject.eClass()` → `getEPackage()` → `getPackageMetadata(ePackage)` →
  contained `ClassMetadata` by instance). With strictly instance-keyed resolution,
  even a *mixed-version graph* (objects of both versions in one document) is
  correct; a per-step URI-keyed map is at most an optimization. **At save time the
  correct config is always available — no ambiguity exists.**
- **Load:** "all fingerprinted versions of the eclass config" are needed as a
  **candidate set, not as one merged map** — a merged eclassURI→config map across
  versions is the clash by construction. Resolution is two-phase per site:
  1. **Select the version**: `codec.rootFingerprint` option > fingerprint from the
     data stream > nsURI candidate query (A.3: exactly one → take it; several →
     error with candidate list).
  2. **Then** build/use the scoped map of *that* `PackageMetadata` — effectively
     `(nsURI, fingerprint) → PackageMetadata → class config`, lazily.
- **Ordering invariant:** version selection happens **before** any URI-keyed map
  is built for that nsURI. A map built before reading an in-stream fingerprint can
  hold the wrong version — this invariant is what rules that out.
- **Document pinning `[DECIDED]`:** version selection (option fingerprint,
  stream fingerprint, or the nsURI candidate query) runs **at most once
  per load per nsURI**; the selected `PackageMetadata` is pinned in the per-step
  context and every subsequent object of that nsURI uses it — no per-object
  lookup, no repeated query. Saving never selects at all: each object's package
  instance is its own pin. Only exception (Phase B, to be confirmed in the B
  design pass): an **explicit** per-site fingerprint in the data overrides the
  pin locally — an explicit signal, not a repeated lookup; absent it, the pin
  applies. Pinning is a per-nsURI cache, **not** "one package per document": a
  reference or polymorphic `_type` that brings in a *new* nsURI mid-document
  triggers the first selection for that nsURI on the fly (entry fingerprint if
  present → direct fingerprint resolve; absent → nsURI candidate query: one →
  take it, several → error A.3) — and the result is pinned too.

## 3. Concretization A (instance + string paths)

### A.1 Instance-based config resolution `[DECIDED]`

Replace the name-keyed flattened `annotationProperties` bridge (F6) with per-instance
resolution: at (de)serialization time resolve config from the **concrete EClass**
via `getClassMetadata(EClass)` / `getClassProfile(EClass, "codec")` (instance-keyed,
F4), reached from the object (`eObject.eClass()`) or the root-type hint. The
fingerprinted `PackageMetadata` (F5) is the entry point; its contained
`ClassMetadata` are instance-identified — no name map in between.

- No model.metadata change required for this part.
- `CodecResource.requirePackageRegistered` → `resolvePackageMetadata(ePackage)`
  (instance-based, `getPackageMetadata(EPackage)`) is already done on the #53/#54
  branch.
- Risk: this path is used by *every* serialization → full test suite + TCKs gate it.

### A.2 String-based root options `[CONFIRMED NEED — user input]`

F7 confirms the String variants exist and must stay: some callers **cannot** provide
an `EClass` instance (e.g. Jakarta REST `MessageBodyReader`/`Writer`, where the root
type arrives via annotation/string configuration). For those:

- **New option `codec.rootFingerprint`** (naming parallel to `codec.rootType` /
  `codec.rootSchema` in `CodecOptions`): explicitly selects the package version the
  String URI should resolve against. Resolution:
  `getPackageMetadataByFingerprint(fp)` → contained class by name → `EClass`
  instance → continue as Scenario A. **Canonical key + alias rule up front (K9):**
  the dotted `CodecOptions` form `codec.rootFingerprint` is canonical; declare the
  alias handling so it does not inherit the existing `codec.rootType` vs
  `CODEC_ROOT_TYPE` duality (today the load path reads the latter spelling).
- **The option is optional** `[DECIDED]`. Rationale: in the common single-version
  case it is pure noise; forcing it would break every existing REST/string-based
  caller.
- "Instance-based" means the derived lookup keys on the **EClass object itself**
  (identity — EMF objects do not override equals/hashCode), never on the class
  name or the class URI: `nsURI#//Entity` is identical for all versions and
  collides exactly like the bare name. Strings are only the *gate* (translated
  once into the right instance by the version-selection phase); the instance is
  the key.

### A.3 Ambiguity policy when the fingerprint is absent `[DECIDED — no trial]`

Candidate selection is **count-based, not trial-based** (the trial fallback was
dropped — see the rationale below and K5). When resolving a type URI / nsURI
without a fingerprint (neither option nor stream):

| # candidates for the nsURI | Behavior (both modes) |
|---|---|
| **0** | error — nsURI unknown to the MetadataService (or fall to B.5 tier 4 for foreign packages) |
| **exactly 1** | **use it** — the normal single-version case; no cost, no behavior change (R1) |
| **> 1** | **error**, listing the candidate fingerprints; the caller disambiguates via `codec.rootFingerprint`. **No trial, no auto-pick, in lenient and strict alike.** |

Rationale for dropping the trial: the trial only ever applied in one narrow
corner (multiple candidates AND no fingerprint AND lenient), and there
"tell me which version" (error + candidate list) is a more honest, actionable
answer than an auto-hit whose "strict-probe passes" does not perfectly
correlate with "is the right version". Dropping it also removes all
input-replay/buffering/sandbox machinery (K5) — the single expensive,
net-new piece of Phase A. The only mode difference that remains in the
fingerprint area is B.2 (unknown *stream* fingerprint: lenient fallback vs
strict error).

Design points for A.3:

- **Q1 — candidate enumeration `[DECIDED — model.metadata issue]`:** fixed in
  model.metadata#16: an **additive versions getter** exposing the list that
  already exists internally (F10), bundled with the **S12 fix** (destructive
  class-URI index removal, F8). Rationale: every consumer faces the same
  question (codec now, emf.persistence-jpa later — stored documents outlive
  model versions by design); the answer belongs to the data owner, not
  replicated per consumer. Guiding line: **facts live in model.metadata**
  (version enumeration, fingerprint lookup), **selection policy stays with the
  consumers**. Not blocking Phase A: until the getter ships, the codec iterates
  `MetadataRegistry.getPackages()` as a clearly marked interim with an expiry
  note.
- **F8 side issue** (folded into model.metadata#16): the URI index's destructive
  `remove` breaks the *survivor's* String lookup after an unregister (atlas#156
  signature at index level); the **class URI index** survivor case appears
  untested (`MetadataServiceSameNsUriMultiInstanceTest` covers only package
  level).
- *(Former Q2 "candidate rejection" and Q3 "several pass" and Q4 "trial cost"
  are obsolete — they only existed for the trial mechanism.)*

### A.4 Consistency of `rootFingerprint` with the root options `[DECIDED]`

Principle (shared with A.3/B.2): **explicit caller signals are validated where
checkable, trusted where not, and never silently degraded.** Strictness modes
govern tolerance towards *data*, not towards the *caller* — the rules below
apply in **both** modes:

- **Checkable case** — `codec.rootFingerprint` given alongside an instance-based
  root option (`ROOT_TYPE` as `EClass`, `ROOT_SCHEMA` as `EPackage`): the codec
  verifies the option fingerprint against the instance's package fingerprint
  (`getPackageMetadata(ePackage).getModelFingerprint()` — memoized, cheap).
  **Mismatch → error**, in every mode: two contradictory explicit statements are
  a caller bug, not an ambiguity to resolve. Redundant-but-consistent → fine,
  no diagnostic noise.
- **Non-checkable case** — root options given as Strings: the fingerprint is
  trusted, but it must **resolve** (`getPackageMetadataByFingerprint` yields a
  config) — **unknown option fingerprint → error**, never a silent fallback to
  nsURI resolution.
- Scope note: this governs the *option* fingerprint only. Behavior for an
  unknown fingerprint *in the data stream* (foreign/old data) is B.2 and stays
  open for the Phase B design.
- Side task (Phase A, additive per R2/R3): `CODEC_ROOT_SCHEMA` currently accepts
  **Strings only** (`resolveContextSchema` checks `instanceof String`) — the
  `EPackage`-instance variant is added as part of this work.

---

## 4. Concretization B (fingerprint in the data)

### B.1 Carrier decision `[DECIDED]`

**A dedicated, optional field next to `_type`.** Key default `_fingerprint`
(matching the `_type`/`_id` naming pattern), configurable analog to `typeKey` /
`typeSchemaKey` (annotation + option: `fingerprintKey`).

- **Write default: OFF** — forced by R1 (a default-on would change every
  existing test's output), and by the simplicity principle: not every use case
  is a fingerprint scenario. For those the codec must stay trivially usable —
  no fingerprint → config by nsURI → exactly one result → take it, done. Zero
  fingerprint machinery unless opted in (option and/or package annotation).
- **Placement when writing (first-touch rule):** emit the field at the **first
  occurrence of each distinct EPackage instance** in document order — the root
  for the root's package, plus every site where a new package instance enters
  the document (supertype substitution, reference/containment targets from
  other packages) — and additionally at any site whose instance differs from
  the pin already established for its nsURI (mixed-version marker). Writer
  first-touch and reader pinning are the same rule seen from both sides.
- **The fingerprint is a citizen of the type context** — it attaches to the
  existing type-serialization mechanisms instead of adding a fourth one: sibling
  key next to a plain `_type`; **inside** the type object where the type is
  written as a complex element (STRUCTURED); and **inside** the id+type
  on-top/preamble mechanism (`idOnTop` & co.) where that is used. Smart
  compression integrates the same way — see S5.
- **Write conservative, read liberal:** persisting happens *exclusively* in the
  sparse opt-in/first-touch mode above — no "write everywhere" variant. Reading
  accepts the configured `fingerprintKey` wherever it appears in any of the
  type-context locations.
- **⚠ Chicken-and-egg — state this loudly in the spec (K7):** read-liberal must
  know the key *before* the version is selected, but an annotation-configured
  key lives in the config that exists only *after* selection — a strict cycle.
  **Break:** for **reading**, the fingerprint key comes from caller-side sources
  ONLY (options / resource / factory / module), and the default key
  (`_fingerprint`) is ALWAYS accepted additionally; the **annotation** level
  configures the key for **writing** only — never for reading. The spec must
  name this cycle explicitly so nobody later "fixes" it by reading the key from
  the model and reintroduces the paradox.
- **Slot & naming (K8):** the fingerprint has a defined position in the ordering
  tables — `_id → _type → fingerprint → features` — because `idOnTop` is only an
  ordering constraint, not a preamble object. Per 03-naming-conventions: the key
  **inside** the STRUCTURED type object is unprefixed `fingerprint`; only the
  PLAIN sibling is `_fingerprint`.
- The **preamble/schema map** alternative is not the default mechanism; it stays
  on file only as a possible later, format-specific optimization for
  payload-critical delegates (e.g. BSON) — see S7.

**Rejected — fragment on the schema/nsURI** (user's own concern confirmed): the
fragment position in type URIs is already occupied (`nsURI#//Entity`), and the
nsURI is a load-bearing identity across EMF — `EPackage.Registry` lookups, proxy
URIs, cross-resource reference URIs (issue #50), JSON-Schema `$ref`. Overloading it
leaks the fingerprint into all of those and breaks literal nsURI comparisons.

**Alternative for payload-sensitive formats** `[OPEN]`: a document preamble /
schema map (`handle → {nsURI, fingerprint}`, objects reference the handle). More
machinery; only worth it under payload pressure (BSON documents, column formats).
Could be a format-delegate-level concern rather than the JSON default.

Properties of the dedicated field:
- **Optional on write** (config/option-controlled; default OFF — resolved below,
  `fingerprintMode=NONE`) and **optional on read** — absence ⇒ today's nsURI single-version behavior
  (backward compatible, F2-safe: carrier content is `scheme:hash`, scheme tag
  travels with the value).
- Read **inline where `_type` is read today** — no pre-scan needed; covers
  polymorphic sub-trees with per-object types naturally.
- `strictOnUnknown` must whitelist the configured fingerprint key.
- Foreign readers can ignore it (it is just another property).

#### B.1a Annotation-model surface `[DECIDED — 2026-07-25, lead]`

Settled before any code, so the ecore is regenerated exactly once. Both features
land on `TypeSerializationConfig` in **this** repo
(`org.eclipse.fennec.codec.metadata/model/codec.ecore`) — `BaseTypeConfig` lives in
the canonical `model.metadata` bundles and stays untouched. B.2, B.3 and the S7
format split need **no** model change (runtime/option concerns).

- **`fingerprintMode` : `FingerprintMode` (`NONE` | `FIRST_TOUCH`), default `NONE`.**
  The write switch is an **enum, not a boolean**: `typeInclude` is deprecated on the
  annotation layer precisely because an on/off boolean duplicates what the strategy
  enum already expresses (`CodecAspectProvider.checkForDeprecatedTypeInclude`,
  T-V30/T-V31 — "use `typeStrategy=NONE` instead"). `NONE` as the default satisfies
  R1 by construction, and the deferred BSON preamble carrier (S7) can be added as a
  third literal without an API break.
- **`fingerprintKey` : EString, no `defaultValueLiteral`.** The default lives in
  `ConfigProperty`, so `null` keeps "not configured" distinguishable — the shape
  `AspectToPropertiesConverter.putIfNotDefault` already relies on. **K8 needs no new
  mechanism:** `TypeSerializationEntry.getPlainSchemaKey()` is exactly the
  one-value-two-placements rule (inner `schema`, PLAIN derives `_schema`, existing
  `_`/`@` prefixes respected) — the fingerprint reuses it for inner `fingerprint` /
  PLAIN `_fingerprint`. The K7 write-only contract is spelled out in the feature's
  GenModel documentation, at the place a future maintainer would look before
  "fixing" the cycle.

**Contradiction found and resolved (§8.1 step 3):** B.1 above describes the opt-in as
"option and/or **package** annotation", but there is **no EPackage annotation level** —
`16-annotation-reference.md` §484 lists it as a proposal and the linked
`docs/codec-v2-spec-working/epackage-scope-proposal.md` no longer exists (lost in the
spec rewrite); `CodecAspectProvider` parses EClass and EReference only. **Decision:**
Phase B ships **class-level** opt-in (annotation on the root class) plus the
caller-side option for document-wide activation. The EPackage level becomes its own
issue — the fingerprint's currency is the EPackage (F3), so B.1 is meant to be lifted
onto that level later, without a breaking change (`fingerprintMode` simply gains a
scope).

*Side finding (P-class, not part of B.1):* `AspectToPropertiesConverter:186` suppresses
`typeNameKey` against default `"name"`, while the real default is `"type"`
(`ConfigProperty:73`, matching 03 §5.1) — an annotation `typeNameKey="name"` is
silently dropped. Fix separately.

### B.2 Load-side resolution `[DECIDED]`

When a fingerprint is extracted (from data or from `codec.rootFingerprint`):
`getPackageMetadataByFingerprint(fp)` → `PackageMetadata.getEPackage()` → concrete
instance → Scenario A path.

Failure cases — the shared line: strictness modes govern tolerance towards
**data**; explicit signals are never silently degraded:

- **Unknown stream fingerprint** (resolves to nothing — foreign data, old data,
  or a scheme bump, S10: an old-scheme fingerprint of an *unchanged* model no
  longer matches):
  - **lenient:** warning diagnostic naming the unknown fingerprint + fallback to
    the nsURI path (one candidate → take it; several → error with candidate
    list, A.3). Readable data stays readable across scheme bumps.
  - **strict:** error.
- **Option fingerprint ≠ stream fingerprint, both resolvable** (the mandatory R4
  negative case): precedence is `option > stream` (§2b selection order) — the
  caller is the closer authority and may deliberately override the data.
  - **lenient:** the option wins + warning diagnostic naming both fingerprints.
  - **strict:** error.
  - Contrast with A.4: there the caller contradicts **themselves** (two own
    options — a bug, error in every mode); here the caller contradicts the
    **data** (possibly intentional — tolerable in lenient, but loud).

**Addendum — signal contract classes (resolves K3).** The precedence here is
deliberately the *opposite* of the type hint's, and the spec makes that
explicit rather than hiding it:

- `CODEC_ROOT_TYPE` is by contract a **hint** (weak signal, default
  `TypeHintMode.HINT`): it answers "what is this object?" — and there the
  stream is rightly the authority, because **polymorphism requires it** (a
  subtype in the data *must* win or subclasses could never deserialize under a
  root hint). The hint only helps when the data is silent. Collision = WARNING,
  content wins — unchanged (R1).
- `codec.rootFingerprint` is by contract a **directive** (strong signal): it
  answers "in which model world is all of this interpreted?" — a property of
  the load, chosen by whoever orchestrates it (per-stage ResourceSet, migration
  reader deliberately re-reading old data against a chosen version). Data may
  self-describe; the orchestrator may overrule. Directive collisions are loud
  (B.2) and self-contradictions are errors in every mode (A.4).
- Capability symmetry exists already: callers wanting option-wins for the type
  have `TypeHintMode.OVERRIDE`. Only the *defaults* differ — grounded in the
  signal nature. No HINT/OVERRIDE machinery is added for the fingerprint: two
  look-alike mode switches with opposite defaults would confuse more than they
  order; "stream fingerprint should win" is expressed by omitting the option.
- Spec placement: a "signal contract classes" passage in 06-type and
  13-load-save-options.

### B.3 Reference URIs `[DECIDED]`

- **STRUCTURED references** (`{"_type": …, "$ref": …}`): the reference's type
  object is type context — the fingerprint attaches per the B.1 rule, sparse as
  always (only when the target nsURI has no pin yet or deviates from it).
- **Resolution:** entry fingerprint present → direct fingerprint resolve; absent
  → nsURI candidate query (A.3: one → take it, several → error). New nsURIs
  mid-document select on the fly and are then pinned (§2b).
- **Proxies:** the entry fingerprint is what makes cross-resource proxy creation
  version-correct — the codec must pick the right EClass *instance* at
  ref-read time to build the proxy at all (cross-package inheritance: the
  declared `eReferenceType` is only an upper bound, see S13).
- **PLAIN references** (bare URI string — no structural room): resolve against
  the **context** (the pin of the target nsURI / the ResourceSet). Documents
  needing self-describing mixed-version cross-references use STRUCTURED — the
  format choice carries the capability. URI enrichment (query params/fragments)
  stays rejected (nsURI identity contamination). Clashes surface through the
  regular error mechanisms and diagnostics (A.3 candidate rule, S13 consistency
  checks) — never silently.

---

### A.5 String-addressed caller config is version-agnostic `[DECIDED]`

Resolves K2. The class-name-addressed configuration of levels 1–4
(`codec.Person.type.strategy=…` in options/factory/module/properties) applies
to **all versions** of that class name — a documented contract, not a fix:

1. Semantically right: such config expresses caller intent over the *name*
   ("Persons serialize like this"), written without version knowledge, and it
   *should* keep applying across model revisions.
2. Version-specific config already has its place: the **model annotation** —
   part of the ecore, feeds the fingerprint, inherently versioned. Clean
   division: *model config versioned (annotations), caller config
   cross-version (strings)*.
3. The alternative (fingerprint-qualified option syntax) is UX poison —
   unmaintainable hashes in properties files, invalidated by every rebuild.

Where the all-versions semantics touches structurally diverging versions (a
feature exists in only one), the regular rules apply (unknown feature →
ignored/diagnostic per mode). A version-specific caller-config syntax, should a
real need ever arise, would be a later additive extension — not now.

**Code reality (F11, de-risks Phase A):** per-class *string* addressing of
levels 1–4 is **not implemented today** — no hierarchical key parsing exists;
the only class-name-keyed path in code is the annotation bridge (level 5, the
F6 site fixed by A.1). So A.5 does not describe a present collision to fix — it
sets the **contract for the day this config style is implemented** (spec 02
§11.4 must be marked not-yet-implemented per P3). It also means **A.1 is the
sole multi-version config-collision point in the current code**, which
tightens Phase A's scope.

### B.5 Package resolution order — replaces the global-registry fallbacks `[DECIDED]`

Whenever the codec must resolve an nsURI (or a class name) to an `EPackage`
instance during a conversion step, the source order is binding:

1. **The pin** (per-step context, §2b) — if the nsURI was already selected in
   this load, that is the answer.
2. **The ResourceSet's package registry** — caller context with concrete
   instances (the per-stage ResourceSet of atlas#156 is exactly this);
   instance-precise, no ambiguity — the registry analog of `ROOT_TYPE` as an
   instance.
3. **MetadataService selection** — candidates by nsURI (model.metadata#16
   getter), then the standing rules: one → take it; fingerprint present →
   direct; several → error with candidate list (A.3).
4. **`EPackage.Registry.INSTANCE` only as a last resort for packages the
   MetadataService does not know at all** (true foreign/plain-EMF packages).
   Hard rule: for an nsURI the service *does* know, the global registry is
   **never** consulted — it can hold only one version per nsURI, so consulting
   it would reopen the back door around version selection.

The two registry-**iteration** sites (`TypeResolutionHelper:99/:170` — class
search across all globally registered packages, order-dependent) are converted
to the MetadataService candidate query with the same ambiguity rules.

R1-compatible: in the single-version case all four tiers agree with today's
behavior — only the source order becomes binding.

### B.6 Discriminator registries — per-step composition `[DECIDED]`

Resolves K1 (the "second F6"): `TypeDiscriminatorService` stops being the
*holder* of global mapId state and becomes the *builder/cache* of per-instance
views:

1. **Discriminator mappings are model config** — they originate from
   annotations and thus belong to the fingerprinted `PackageMetadata`. The
   derived lookup maps are built **per PackageMetadata instance** and memoized:
   built once, immutable, freely reusable (instance-bound → thread-safe).
2. **The effective mapId view is composed per conversion step** from the
   contributions of the **pinned** packages — lazily, on first discriminator
   use in the step. Necessary because mapIds can span packages (base class
   defines the path, concrete classes from other packages register their
   values — the cross-package inheritance case, S13).
3. **Value collisions inside the composed view** (two pinned packages map the
   same discriminator value to different classes) → diagnostic/error per the
   standing severity rules — never last-wins.
4. **Resolution of mapped classes** follows the B.5 source order — as instances
   from the pinned `PackageMetadata`, not as URI-string lookups.
5. **R1 check:** with a single version the composition of exactly one
   contribution is identical to today's global registry — behavior unchanged.

The OSGi component remains; only its role changes (holder → builder/cache).

### B.4 FingerprintService legacy-scheme compat mode `[DECIDED — future capability, model.metadata]`

Answers the S10 residual gap. Mechanics:

- A fingerprint is physically `scheme:hash` (today `fp1:<sha256>`); the scheme
  tag versions the **canonicalization algorithm**, not the model. Resolution is
  exact string match; fingerprints are **opaque** to everyone but the
  FingerprintService.
- **Compat mode:** on an exact-match miss, the resolver parses the scheme prefix
  out of the incoming print. If it names a *retained* legacy scheme, the
  FingerprintService **recomputes** the candidate packages' prints in that
  legacy scheme (candidates = the nsURI context of the resolution site, i.e.
  the Q1 versions query) and compares — restoring exact resolution for data
  written before a scheme bump. The same unchanged model thus stays exactly
  identifiable across scheme generations.
- **Requirement:** the FingerprintService retains legacy scheme implementations
  — every shipped scheme version stays computable. (Caching legacy prints per
  `PackageMetadata` is an implementation detail.)
- **Diagnostics:** resolving via a legacy scheme emits an info/warning
  diagnostic naming both schemes — a nudge to rewrite/migrate the data to
  current prints, never a silent path.
- **Ownership & trigger:** pure facts territory → model.metadata (the codec
  needs nothing beyond the B.2 baseline). Not built now — there is only `fp1`;
  the **first actual scheme bump triggers a dedicated model.metadata ticket**
  for this capability.

## 5. Findings register (side effects to track)

Each finding gets concretized step by step; status tracked here.

| ID | Finding | Affected spec | Status |
|----|---------|---------------|--------|
| S1 | Config bridge flattens by class name (F6) — root cause, fix = A.1 | 02-config-resolution | `[ANALYZED]` |
| S2 | String root options ambiguous under multi-version (F7/F8) — fix = A.2/A.3 | 13-load-save-options, 05-global-options | `[ANALYZED]` |
| S3 | Reference URIs not version-qualified — resolved by B.3 (STRUCTURED carries the fingerprint in its type context; PLAIN resolves against pin/ResourceSet context; proxies get version-correct EClass selection at ref-read time) | 10-reference | `[DECIDED — see B.3]` |
| S4 | `EPackage.Registry.INSTANCE` fallbacks are single-version by construction (`TypeResolutionHelper`, `TypeDeserializationEntry`, `FeaturePathTypeResolver`) — resolved by the binding source order in **B.5** (pin → ResourceSet registry → MetadataService selection → global registry only for unknown-to-service packages; iteration sites → candidate query) | 06-type, 12-polymorphism | `[DECIDED — see B.5]` |
| S5 | Smart compression + fingerprint are a **pair, not a conflict**: the root fingerprint (type context, B.1) anchors the package more precisely than any nsURI — compressed simple names then resolve against the pinned package, making smart compression multi-version-capable. Residual: multi-version *without* a fingerprint + smart compression stays unresolvable → falls under the A.3 rule (several candidates, none named → error). | 06-type (smart compression) | `[ANALYZED — resolved via B.1 anchor]` |
| S6 | Polymorphic sub-trees need per-`_type`-site fingerprints, not root-only (solved structurally by B.1 inline field) | 12-polymorphism | `[ANALYZED]` |
| S7 | **In-band where the format has properties, option path where not.** Property-stream formats (BSON/CBOR/YAML/…) mirror the token stream — the fingerprint flows through automatically; proven per format by a fingerprint TCK suite (R4–R6 rules). Column/tabular formats (CSV/XLSX/ODS/tabular) get **no in-band carrier** — they are homogeneous and option-driven; the fingerprint travels as `codec.rootFingerprint`. Documented as a capability row in `docs/format-tck-capability-map.md`. Preamble stays parked as a possible BSON payload optimization (B.1). | 17-format-abstraction | `[DECIDED]` |
| S8 | Backward compat: no-fingerprint data keeps working; `strictOnUnknown` whitelists the fingerprint key | 15-error-handling, 05-global-options | `[ANALYZED]` |
| S9 | Trial-and-error silent mis-map risk — **removed at the source**: the trial fallback was dropped (A.3, K5). Multiple candidates without a fingerprint → error with candidate list in both modes; no probing, no silent mis-map possible. | 15-error-handling | `[RESOLVED — trial dropped]` |
| S10 | Scheme drift: baseline behavior is B.2 (unknown fingerprint → lenient fallback/strict error), so a scheme bump never makes single-version data unreadable. Residual gap: old **mixed-version** data loses its disambiguation power after a bump → answered by the FingerprintService legacy-scheme compat mode, see **B.4**. Spec note: fingerprints are opaque exact-match strings incl. scheme prefix — nobody but the service parses them. | 99-open-questions, (model.metadata) | `[ANALYZED — baseline via B.2, compat mode B.4]` |
| S11 | **Not affected by fingerprinting.** OpenAPI: a fixed metamodel versioned via its nsURI — its fingerprint is effectively a constant, not a multi-version case. JSON Schema: a presentation form of an ecore (model.atlas: output only; the standalone jsonschema→ecore converter produces a *draft* ecore) — same situation as OpenAPI. No spec work needed. | — | `[RESOLVED — out of scope]` |
| S12 | URI index destructive remove kills survivor lookup (F8) — filed together with the Q1 versions getter as [model.metadata#16](https://github.com/eclipse-fennec/model.metadata/issues/16) | (model.metadata) | `[FILED — model.metadata#16]` |
| S13 | **Transitive version consistency:** with cross-package inheritance, a subclass EClass physically references concrete superclass *instances* of a specific base-package version (`getESuperTypes()`) — selecting a dependent package's version implicitly selects its dependencies' versions. Pins must be instance-graph-consistent; a conflict (pin says A-v1, a later-resolving subclass demands A-v2 via its supertype chain) is an **error** with a clear message, never silently rebent. The declared `eReferenceType` of a reference is only an upper bound for post-resolution validation — the concrete target class/package always comes from the data. | 10-reference, 12-polymorphism | `[ANALYZED — decided direction]` |

---

## 6. Spec impact (chapters to touch once decisions land)

- `02-config-resolution.md` — instance-based resolution replaces name-keyed
  annotation properties (A.1); resolution order incorporating fingerprint.
- `05-global-options.md` / `13-load-save-options.md` — `codec.rootFingerprint`,
  fingerprint write option, ambiguity/strict behavior.
- `06-type.md` — fingerprint field next to `_type`; smart-compression constraint.
- `10-reference.md` — reference URI versioning (B.3, own design pass).
- `12-polymorphism.md` — per-site fingerprint semantics.
- `15-error-handling.md` — ambiguity diagnostics, strict-mode failures, unknown
  fingerprint handling.
- `16-annotation-reference.md` — new annotation keys (fingerprint key naming).
- `17-format-abstraction.md` — carrier transport in format delegates.
- `99-open-questions.md` — Q1, S3–S12 until resolved.

## 7. Development & test requirements `[DECIDED — binding]`

This document refines the spec, i.e. it talks about things that do not exist yet —
what it produces are **requirements** to be implemented later. The following are
binding conditions on that implementation, independent of which `[OPEN]` decisions
land which way.

### 7.1 Compatibility of existing tests

- **R1 — behavior-stable:** every existing test must run exactly as before after
  the fingerprinting work. Fingerprinting may only change **load/save
  configuration** (e.g. additionally passing a fingerprint as a load property) —
  never the expected behavior or data of an existing test.
- **R2 — config paradigm preserved:** where an existing test needs a config-level
  adaptation, the adaptation must not violate the config's own principles:
  - a **property-based** config stays property-based — new *properties* may be
    added;
  - a **builder-based** config stays builder-based — the builder may gain new
    *property callbacks*;
  - no test is migrated to a different config style as part of fingerprinting.
- **R3 — additive API surface (implication of R1/R2):** fingerprint support must
  be additive on **both** config surfaces symmetrically: new option constants
  (property path) *and* new builder callbacks (builder path). Neither surface may
  require the other.

### 7.2 New fingerprint-aware tests

- **R4 — negative cases are mandatory:** every fingerprint feature test suite must
  cover misconfiguration, at minimum: a fingerprint passed as load option while
  **the data stream carries a different one** → the defined behavior (diagnostic /
  error depending on strictness) is asserted, not just "it loads". Further
  mandatory negatives as applicable: unknown option fingerprint, unknown in-stream
  fingerprint, ambiguity without any fingerprint in strict mode.
- **R5 — at least 2 variants, spot checks with 3:** fingerprint tests always use
  **≥ 2 variants** of the same nsURI as the mandatory baseline; selected suites
  additionally run **3 variants** as spot checks (catches "works for exactly two"
  logic such as binary first/last shortcuts).
- **R6 — both variants fully verified (coincidence-proof):** in the mandatory
  2-variant tests, **both** variants are fully asserted in both directions:
  - **save:** each variant's save must end in **its own** correct format/semantics
    — assert the *differing* output (own key present, other variant's key absent);
  - **load:** assert against the **differing** eclasses/features of each variant —
    the correct instance/feature is populated, the other variant's is not.
  The existing `SameNsUriMultiVersionCodecTest` (F9: `alpha`/`beta` cross
  assertions) is the template for this pattern.

## 8. Working method & order `[DECIDED — binding]`

### 8.1 Method (applies to every work package; goes into every ticket)

1. **Refine the spec** for the work package (draft the change).
2. **Uncover contradictions** with the existing spec.
3. **Clarify contradictions** with the project lead — no silent reinterpretation.
4. **Derive tests** from the refined spec — test-first, i.e. failing first; tests
   must cover the spec variants (incl. the R4–R6 negative/multi-variant rules).
5. **Implement** until the derived tests are green (R1–R3 compatibility rules
   hold throughout).

### 8.2 Issue mapping

- **Phase A** → issue **#54** (reframed to this document).
- **Phase B** → issue **#73** (spec extension: fingerprint in data).
- **model.metadata#16** → all-versions getter + destructive class-URI-index fix.
- Smaller work steps are checklists in #54/#73; split into sub-issues on demand.
- The method above (8.1) is copied into each ticket.

### 8.2a Phase B implementation record (2026-07-25)

Phase B (#73) is implemented on `feature/issue-73-fingerprint-phase-b`, one commit per work
package, each R1-gated (no existing test changed) and CI-green:

| Step | What landed |
|---|---|
| **B.1a** | Model surface: `FingerprintMode` (`NONE`\|`FIRST_TOUCH`) + `fingerprintKey` on `TypeSerializationConfig`. Enum rather than a boolean, because `typeInclude` is deprecated for exactly that pattern. |
| **B.1 spec** | 06 §8 as the single description of the carrier; 13 §2.10/§2.11 options + signal contract (K3); key tables in 03, property table in 02, annotation table in 16. |
| **B.1 config** | `ConfigProperty`, `TypeConfig` (incl. `getPlainFingerprintKey()`), `CodecOptions`, annotation parsing, bridge, REST surface (R3). |
| **B.1 write** | `FingerprintPins` + first-touch write in PLAIN and STRUCTURED; a due fingerprint beats smart compression's type omission. |
| **B.1 read** | Type context collected before resolving (PLAIN needs both type *and* fingerprint); read keys seeded from caller options into the context, making the K7 break **structural**. |
| **B.2** | Full R4 matrix as `StreamFingerprintOutcome`; caller-vs-stream precedence; LENIENT nsURI fallback; diagnostic cap per fingerprint value. |
| **B.3** | Fingerprint inside a STRUCTURED reference's type object; reference parser no longer misreads it as projection data (W14); reference type resolution routed through `PackageResolver`. |
| **S7** | `AbstractFingerprintTCK` extended by YAML/CBOR/BSON; column formats report `supportsInBandFingerprint() == false` and the carrier is suppressed with a warning. |

**Decisions taken during implementation** (all recorded in the spec):

- **B.1a**: model surface settled up front with the lead so the ecore is regenerated once;
  `fingerprintMode` as an enum; `fingerprintKey` without a default literal.
- **EPackage annotation level does not exist** → Phase B ships class-level opt-in plus the
  caller option; the level itself is **issue #75**.
- **PLAIN ordering** (lead decision): the K8 slot `_type → fingerprint` stands; instead the
  reader collects the whole type context before resolving. No spec revision needed.

**Defects found and fixed inside Phase B:**

- The reader moved its pin whenever it resolved an explicit fingerprint, so `A1, A2, A1` came
  back as `A1, A2, A2` — the unmarked third root was reinterpreted. The pin now keeps the first
  version, matching the writer.
- The STRUCTURED reference parser counted the fingerprint as projection data, and its type
  resolution bypassed `PackageResolver` for the global registry.

**Defect found, filed, not fixed here:** **issue #76** — with several roots, smart compression
writes a bare name for the second root that the reader cannot resolve, dropping the object
silently. Reproduces with one version and no fingerprint, so it is not ours; both possible
fixes are R1 decisions of their own. `FingerprintRoundTripTest` §5.1 is disabled pointing at it.

**Baseline:** 3514 tests, 0 failures, 3 documented skips (2 pre-existing + the #76 one).

### 8.3 Order

1. **This document** — review together, settle `[PROPOSED]` → `[DECIDED]`
   (especially: pinning, A.2 optionality, A.3 candidate rule, B.1 carrier).
2. **Phase A** (#54, per 8.1): spec refinement (02, 05, 13, 15) → contradiction
   check → clarification → failing tests (F9 template) → implementation.
3. **Phase B** (new issue, per 8.1): carrier (B.1), load resolution (B.2),
   references (B.3) → spec chapters 06/10/12/17 → tests → implementation.
4. **model.metadata follow-up ticket** for S12 (+ optional Q1 index query).

---

## 9. Contradiction & coherence review (2026-07-24)

Three full-context reviewers checked every decision above against the existing
spec (all 21 chapters) and against concept/architecture/UX coherence. 42
findings, fully triaged into four buckets: **K** (needs a decision — worked
through step by step below), **W** (work items, no principle decision — feed
into the spec update and sub-issues), **P** (pre-existing spec-internal
inconsistencies found along the way — not caused by this design, fixed
opportunistically during the rewrite), **C** (areas explicitly confirmed
coherent).

### 9.1 K — decision register

| # | Topic | Status |
|---|-------|--------|
| K1 | **Discriminator registries are a second F6:** `TypeDiscriminatorService` holds global, mapId-keyed state with URI-string forward/reverse maps — last-wins collision under multi-version, ambiguous URI→instance resolution. Resolved by the per-step composition design → **B.6**. | `[DECIDED — see B.6]` |
| K2 | **Class-name-addressed caller config (levels 1–4):** collides under multi-version like F6, but there is nothing "right" to resolve — declared **version-agnostic by design** → **A.5**. | `[DECIDED — see A.5]` |
| K3 | **Precedence asymmetry type vs fingerprint:** resolved via option (a) — the asymmetry is justified by signal nature and made explicit in the spec ("signal contract classes": hint vs directive) → see the addendum in **B.2**. | `[DECIDED — see B.2 addendum]` |
| K4 | **The third mode `[DECIDED — code-verified]`:** the strictness branch is **binary in code** — `ContextHelper.isStrictMode()` is literally `"STRICT".equals(getDeserializationMode(ctxt))`; no deser branch distinguishes AUTO_DETECT from LENIENT (`CodecEObjectDeserializer:490` "LENIENT/AUTO_DETECT mode: Warning and fall back to hint"). Default is LENIENT, hard-coded (`ContextHelper.getDeserializationMode` returns `"LENIENT"` on a missing attribute, lines 907/917; matches `ConfigProperty` default + enum "This is the default"). Decisions: (1) **canonical default = LENIENT** → fix 13 §5.1 (says AUTO_DETECT) and 15 §10 (says STRICT) as P-fixes, R1-neutral (code already does LENIENT). (2) **matrix mapping: strict = {STRICT}, lenient = {LENIENT, AUTO_DETECT}** — one sentence in the spec; our binary matrices stand unchanged. (3) AUTO_DETECT's structure-probing is orthogonal to error strictness; the fingerprint field participates in auto-detection like `_type` (W12). | `[DECIDED]` |
| K5 | **Trial needs input replay** — **dissolved by dropping the trial** (A.3). No buffering/mark-reset/sandbox is needed: the single-candidate case loads directly (no cost), the multi-candidate-without-fingerprint case errors immediately with the candidate list. The one net-new, replay-heavy piece of Phase A is gone. | `[RESOLVED — trial dropped]` |
| K6 | **`strictOnUnknown`/`strictOnMissing` not yet implemented** (99 §2.1): was a prerequisite *only* for the strict probe — with the trial dropped it is **no longer a fingerprinting prerequisite**. It remains a normal feature-strictness gap in the codec, tracked independently of this work. | `[RESOLVED — not a fingerprinting dependency]` |
| K7 | **`fingerprintKey` bootstrap is a CHICKEN-AND-EGG problem — must be stated in the spec with full emphasis, not buried.** Read-liberal requires knowing the key *before* version selection, but an annotation-configured key lives in the config that exists only *after* selection — a strict cycle. **Resolution:** for **reading**, the fingerprint key comes from caller-side sources ONLY (options/resource/factory/module) and the default key (`_fingerprint`) is ALWAYS accepted additionally; the **annotation** level configures the key for **writing** only, never for reading. The spec must call this out explicitly as the chicken-and-egg break, so nobody later "fixes" it by reading the key from the model. | `[DECIDED]` |
| K8 | **Carrier slot & naming `[DECIDED]`:** `idOnTop` is only an ordering constraint, not a preamble *object* — the fingerprint gets a defined slot in the ordering tables instead: `_id → _type → fingerprint → features`. Naming per 03-naming-conventions: **inside** the STRUCTURED type object the key is unprefixed `fingerprint`; only the PLAIN sibling is `_fingerprint`. | `[DECIDED]` |
| K9 | **Options-key duality `[DECIDED]`:** two live spellings exist today (`CodecOptions.CODEC_ROOT_TYPE = "codec.rootType"` vs `CodecResource."CODEC_ROOT_TYPE"` — the load path reads the latter). `codec.rootFingerprint` declares the canonical key (`codec.rootFingerprint`, the `CodecOptions` dotted form) + an alias rule up front, so it does not inherit the duality. | `[DECIDED]` |

### 9.2 W — work items (no principle decision; → spec update & sub-issues)

- **W1** Rewrite 02 §8 / 01 §4–5 ("resolved once", "immutable snapshot") to the per-step model: immutable per selected version, pins as per-load cache (§2b).
- **W2** Add the version-selection pre-phase to the 02 §4 resolution algorithm (ordering invariant).
- **W3** Register `codec.rootFingerprint` in all option tables/registries (02 §11.13/§12, 13, options-reference).
- **W4** Pin-awareness family: smart-compression eligibility (06 §6.4.4/§1.5.3, 05 §1.6) from nsURI-compare to pin/instance-compare; suppress reference type-omission when a fingerprint is due (10 §5.1.2/12 §1.2); supertype namespace compare (07 §4).
- **W5** Metadata-merge inner keys: add fingerprint to 05 §5.2 tables, §5.4 ordering, §5.5 collision check.
- **W6** Error catalog: fix 13 §8.1 (String is legal for `CODEC_ROOT_TYPE`), add the new error/diagnostic cases (A.3 multi-candidate ambiguity with remedy text, A.4, B.2).
- **W7** Remove the "Unregistered EPackage → ERROR" case (15 §6.5, §0 Layer 1) — replaced by the pull path (F5).
- **W8** Preserve the S-4 security constraints (bounded scan) in the B.5 conversion of the registry-iteration sites; make the 15 §9.3 distinction explicit (candidate iteration within ONE nsURI ≠ global package scan).
- **W9** Consolidate 99 §1 Q1 (cross-package reference handling) with S13.
- **W10** 06 §6.4.5 remediation text: nsURI-based context schema cannot disambiguate versions — point to `codec.rootFingerprint`.
- **W11** Update the MetadataIndex API references in 06 §6.4.5 once model.metadata#16 ships (candidate enumeration).
- **W12** Add fingerprint to the 06 key tables (auto-detected keys, STRUCTURED inner keys).
- **W13** STRICT supertype validation (07 §9.2): validate within the resolved class's instance hierarchy (S13), not via the URI index.
- **W14** Teach the STRUCTURED reference parser the fingerprintKey (10 §9.2.4 step 3b — currently misclassified as projection/orphan data).
- **W15** Add pin-awareness note to PLAIN-ref/proxy resolution text (10 §1.1/§9.3 — compatible, needs wording).
- **W16** 12-polymorphism: explicit sentence that `inherit=ALL` across package boundaries ties effective config to the base-package version of the instance chain (S13).
- **W17** Format capability flag so column formats suppress the in-band field (17; S7) + capability-map row + fingerprint TCK suite.
- **W18** A.1 must reproduce the annotation-layer merge semantics exactly (supertype walk parents-first, child-overrides, global layer); document the intended behavior change: an unregistered EClass no longer silently inherits a same-named registered class's config.
- **W19** Enumerate the complete B.2 case matrix (= R4 catalog); includes the yet-unlisted cases: option present × stream unknown (proposal: option wins + warning), option = stream (no-op), per-site unknown after pin.
- **W20** Pin store lives in the per-load context (not the per-resource resolver); document concurrency (parallel loads may pin different versions) and the invalidation story (unregister/re-register vs memoization).
- **W21** REST plumbing as its own sub-issue (how JAX-RS callers set `rootFingerprint`).
- **W22** Write-switch spec: option name, which config scopes carry it, R3 builder callback, annotation-on/option-off conflict rule.
- **W23** A.3 error text (several candidates, no fingerprint) names the remedy (`codec.rootFingerprint`) and lists the candidate fingerprints.
- **W24** Diagnostic flooding cap (dedupe per unknown fingerprint value) + align candidate lists in errors with S-12 (model-inventory disclosure).
- **W25** Document the XMI/cross-format round-trip boundary (XMI never carries a fingerprint).

### 9.3 P — pre-existing spec-internal inconsistencies (fix during rewrite)

Code-checked against current reality (2026-07-24). Principle: **code is the
current truth; where the spec describes something not implemented, the spec must
say so explicitly** (as 99 §2.1 already does for feature strictness) — not
silently align or delete.

- P0 `deserializationMode` default: LENIENT (02 §11.11, **code**) vs AUTO_DETECT (13 §5.1) vs STRICT (15 §10) — canonical LENIENT per K4; fix 13/15. `[RESOLVED — code]`
- P1 `typeStrategy` default: **code = "URI"** (`ConfigProperty.TYPE_STRATEGY`) → 00/05/99 correct, 02 §11.3 (NAME) wrong; fix 02. `[RESOLVED — code]`
- P2 **fail-fast is a ground condition, not a config parameter** (project-lead intent): "abort as early as possible" is baseline behavior, so the `CODEC_FAIL_FAST` option + the `failFast=false` default (15 §4.1) contradict the intended design. Two clean separations to carry into the spec: (a) **fail-fast ≠ lenient/strict** — lenient/strict *classifies* (is this an error or a tolerated warning?), fail-fast governs *propagation of an actual error* (abort now vs collect-and-continue); it does not turn lenient warnings into errors. (b) Our fingerprint error cases (A.4 mismatch, B.2 strict, A.3 multi-candidate) are already eager hard-stops — consistent with this principle. Removing/deprecating the `CODEC_FAIL_FAST` option is an R1-relevant API change → confirm separately, not folded into fingerprinting silently. `[PRINCIPLE stated; option-removal to confirm]`
- P3 Per-class string addressing (`codec.Person.type.strategy`, 02 §11.4) is **NOT implemented** (F11) — no hierarchical key parsing exists. Mark §11.4 explicitly as not-yet-implemented in the spec; the K2/A.5 decision (version-agnostic) governs its *future* implementation, not present behavior. `[RESOLVED — code: unimplemented, mark in spec]`
- P4 NUMERIC inner keys: **code = `schema`/`classifier`** (`CodecEObjectDeserializer:350`, `TypeDeserializationEntry:290`) → 18 §7 (`s`/`c`) is a wrong/unimplemented abbreviation; fix 18. `[RESOLVED — code]`
- P5 13 §2.5 javadoc ("Value: EClass") vs §2.1 ("EClass or String") — **code accepts both** (`CodecResourceHelper.resolveRootEClass`), so §2.1 is right; fix the §2.5 javadoc. `[RESOLVED — code]`

### 9.4 C — confirmed coherent

Fingerprint currency (EPackage-only, F3-aligned) · save path ambiguity-free
(resolver caches already instance-keyed; A.1 correctly localized at the
annotation source) · `fingerprintKey` embedding in `TypeConfig` + real builder
surfaces (R3 satisfiable symmetrically) · naming scheme fits the three-surface
convention (modulo K8/K9) · A.3 candidate rule deterministic (identical
duplicates collapse by fingerprint identity — no spurious ambiguity; count-based,
no trial) · pinning/first-touch
symmetry incl. S13 direction · B.5 source order · scheme-drift solution
(B.2 baseline + B.4) · S7 format split · R1–R6 + §8.1 method enforceable;
F9 test is the right template.

*Traceability: all 42 reviewer findings map into K1–K9, W1–W25, P1–P5, or C —
none dropped.*

---

*Created 2026-07-24 as part of the #53/#54 work; see also
`docs/codec-v2-plans.md` and the Big Picture doc in model.metadata.*
