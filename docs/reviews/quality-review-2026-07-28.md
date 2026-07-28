# Quality review — fennec-codec (emf.codec) — 2026-07-28
Mode: quick + followup (previous: quality-review-2026-07-24.md) · Scope: whole repo, full reads focused on code changed since 2026-07-24 · Rule sets: SOLID/OSGi + Eclipse Foundation (see skill references)

## Summary

| Severity | api-export | osgi-ds | release-readiness | solid-srp | solid-dip | naming | javadoc | Total |
|----------|-----------|---------|-------------------|-----------|-----------|--------|---------|-------|
| blocker  |           |         |                   |           |           |        |         | **0** |
| major    | 2         | 1       | 1                 |           |           |        |         | **4** |
| minor    |           |         |                   | 1         |           | 1      | 1       | **3** |
| info     |           |         | 2                 | 1         | 1         |        |         | **4** |

Clear net improvement since 2026-07-24: 4 of 15 previous findings are resolved, the former blocker is gone (its bundle moved out of this repo), and the substantial new code landed since then (fingerprint carrier B.1–B.6, `PackageResolver`, smart-compression context, CI migration to org reusable workflows) is in good shape — all new files carry correct EPL-2.0 headers, no new unversioned exports, license enforcement survived the CI migration (skywalking-eyes gates every build inside `reusable-verify`, pinned to a SHA), and the global-registry fallback was refactored into a documented tiered `PackageResolver` exactly along the lines the previous review suggested. The two recurring themes are unchanged, though: DS components still live in exported packages across ~12 bundles (open since 2026-07-24), and the 12 restricted Dash entries still await IP review. One new major finding: a static per-package view cache in the new discriminator code that never evicts unregistered packages.

## Previous findings status

| Prev | Status | Note |
|------|--------|------|
| F1 (blocker, api-export) | **n/a** | `org.eclipse.fennec.model.metadata` moved to the `eclipse-fennec/model.metadata` repo (issue #53); consumed from Maven now. The finding travels with the code — re-raise it in that repo's review. |
| F2 (major, api-export) | **still open** | Re-verified anchors: `codec.resource`, bson, csv, openapi unchanged. → carried as F1' below. **Open 2 consecutive reviews.** |
| F3 (major, api-export) | **still open** | `org.eclipse.fennec.codec.format.impl` still `@Export`ed. → carried as F2'. **Open 2 consecutive reviews.** |
| F4 (major, api-evolution) | **resolved** | `@Version("1.0.0")` on `context` and `config.effective`; TCK export versioned. Repo-wide sweep found no unversioned exports. |
| F5 (minor, naming) | **still open** | `org.eclipse.fennec.codec.tests.tck` unchanged. → carried as F5'. |
| F6 (minor, solid-srp) | **still open** | Converters byte-identical in size (2600/1968 lines). → carried as F6'. |
| F7 (minor, naming) | **resolved** | `Bundle-Name: Eclipse Fennec Codec` — typo fixed. |
| F8 (minor, naming) | **resolved** | rest and playground both have `Bundle-Name` + `Bundle-Description`. |
| F9 (minor, javadoc) | **still open** | Date-based `@since` persists and **accrues in new code** (`TypeDiscriminatorService` `@since 2025-12-17`, new `CodecOptions` constants `@since 2026-06`). → carried as F7'. |
| F10 (info, solid-dip) | **partially resolved** | New `PackageResolver` implements the suggested tiering (MetadataService first, `INSTANCE` documented last resort); `TypeDeserializationEntry.buildNumericTypeValue` (:420) still hits `INSTANCE` directly without the resolver. |
| F11 (info, release-readiness) | **still open** | playground/examples still carry no release-exclusion marker. |
| F12 (info, solid-srp) | **still applicable** | `ConfigurationResolver` unchanged at 1810 lines; still cohesive, still worth watching. |
| F13 (major, release-readiness) | **partially resolved** | Dash setup complete (`tools/dash-licenses.sh`, workflow, `DEPENDENCIES` committed). Remainder: 12 `restricted` entries pending IP review; `dash-licenses.yml` is `workflow_dispatch`-only with `continue-on-error` until then (documented decision, #46/#48). → carried as F3'. |
| F14 (minor, release-readiness) | **resolved** | README "Branches & releases" section states branch model and `org.eclipse.fennec.codec:*` coordinates. |
| F15 (info, release-readiness) | **still open (acceptable)** | Baselining still commented out in `cnf/build.bnd:16`; still no tags/releases, so still acceptable — becomes major after the first release. |

**Totals: 4 resolved · 2 partially resolved · 8 still open · 1 n/a.**

## Findings

### F1' · major · api-export · systemic (~12 bundles) — carried over from 2026-07-24 (F2), open 2 consecutive reviews
- **Where:** anchor: org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/resource/CodecResourceFactoryComponent.java (package exported via `resource/package-info.java:19`)
- **What:** DS component classes and `*Impl` classes still sit inside exported packages across the format-provider bundles (re-verified: `codec.resource` components, `BsonResourceFactoryComponent`, `CsvResourceFactoryComponent`, `OpenApiResourceFactoryImpl`/`OpenApiResourceImpl`; full occurrence list in the 2026-07-24 report remains accurate). The new fingerprint code adds a borderline occurrence: `FingerprintPins` — documented as "one instance per save, not thread-safe", i.e. an internal mechanism — lives in the exported `codec.util` package.
- **Why it matters:** Everything public in an exported package is API; each release makes internal changes to these classes potentially breaking. The window to fix this cheaply closes at the first release — and the repo is visibly moving toward one (Dash, README, CI all release-ready now).
- **Suggested fix:** Unchanged from the previous review: one mechanical sweep moving DS components and impl-only helpers to non-exported `.internal` sibling packages. Since this is now open across two reviews with a release approaching, consider making it a tracked issue with a milestone rather than review advice.

### F2' · major · api-export · org.eclipse.fennec.codec — carried over from 2026-07-24 (F3), open 2 consecutive reviews
- **Where:** org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/format/impl/package-info.java (still `@Export @Version("1.0.0")`)
- **What:** The `impl`-named package `org.eclipse.fennec.codec.format.impl` is still exported and still serves as the cross-bundle extension surface (`JacksonFormatProvider`, `JacksonStreamFormatDelegate` extended by cbor/yaml).
- **Why it matters:** An exported package whose name says "not API" but which downstream bundles compile against will be treated as free-to-change by tooling and reviewers, silently breaking the format providers.
- **Suggested fix:** As before: split the intended extension surface into a properly named exported package (`.format.jackson` or `.format.spi`), privatize the rest.

### F3' · major · release-readiness · repository — carried over from 2026-07-24 (F13, remainder)
- **Where:** DEPENDENCIES (12 `restricted` entries); .github/workflows/dash-licenses.yml:7 (`workflow_dispatch` only, `continue-on-error: true`)
- **What:** The Dash tooling is in place, but 12 restricted dependencies still await IP review, and until then the license check does not run automatically and cannot fail a build (documented as intentional, refs #46/#48).
- **Why it matters:** Restricted entries are a release blocker; the manual-only, non-failing workflow means a newly added restricted dependency between now and the IP review would go unnoticed.
- **Suggested fix:** File the IP review (`tools/dash-licenses.sh --review --project technology.fennec`) for the 12 entries now — it has lead time — and re-enable automatic triggers as soon as the baseline is clean.
- **Update (same day):** 7 of the 12 restricted entries turned out to be unused index leftovers in `cnf/central.mvn` (aQute.libg, gogo.commands.provider, wrapper.hamcrest, felix.threaddump, webconsole.plugins.useradmin, jackson-dataformat-properties, stale felix.http.jetty 4.2.8 — bndruns pin 5.0.4). They were removed and `DEPENDENCIES` regenerated: **5 restricted entries remain** (SODS, fastcsv, poi, xmlbeans, gecko example.model.basic), of which the gecko example model is playground-only. Only the 4 format-backend libraries truly gate a release.

### F4' · major · osgi-ds · org.eclipse.fennec.codec.metadata — NEW
- **Where:** org.eclipse.fennec.codec.metadata/src/org/eclipse/fennec/codec/metadata/type/TypeDiscriminatorService.java:100 (populated at :204)
- **What:** The per-package discriminator view cache `PER_PACKAGE_VIEW` is a static `ConcurrentHashMap<PackageMetadata, TypeDiscriminatorService>` filled by `perPackageView(...)` via `computeIfAbsent`, and no code path ever removes entries — `onPackageUnregistered(...)` (:289) cleans the instance registries but not the static cache.
- **Why it matters:** The class's own `MetadataHandler` contract anticipates package unregistration, yet an unregistered `PackageMetadata` — with its `EPackage` and all `EClass`es — stays strongly referenced in the static map forever. In a dynamic OSGi deployment (model bundle updates, repeated register/unregister cycles) this is a memory leak and serves stale views for re-registered packages; this is the same bug class as the historical EMFFileWatcher EPackage leak the rule set calls out.
- **Suggested fix:** Evict in the unregister path (`PER_PACKAGE_VIEW.remove(packageMetadata)` from `onPackageUnregistered`/`unregisterPackage`), or make the cache non-static and owned by whatever composes the per-load views so its lifetime is bounded by its owner.

### F5' · minor · naming · org.eclipse.fennec.codec.tests — carried over from 2026-07-24 (F5)
- **Where:** org.eclipse.fennec.codec.tests/bnd.bnd:4
- **What:** The exported TCK package still carries the reserved `tests` qualifier (`org.eclipse.fennec.codec.tests.tck`), and the TCK surface is growing (`AbstractFingerprintTCK` added, consumed by bson/cbor/yaml test bundles).
- **Why it matters:** The more format providers compile against the TCK, the more expensive the eventual rename; `tests` marks non-API per Eclipse naming rules.
- **Suggested fix:** Decide before first release: rename to `org.eclipse.fennec.codec.tck` if it ships, or document the bundle as never-released.

### F6' · minor · solid-srp · org.eclipse.fennec.codec.jsonschema — carried over from 2026-07-24 (F6)
- **Where:** org.eclipse.fennec.codec.jsonschema/src/org/eclipse/fennec/codec/jsonschema/v2/converter/JsonSchemaToEPackageConverter.java:73
- **What:** The two converter monoliths are unchanged (2600 and 1968 lines).
- **Why it matters / Suggested fix:** As in the previous review — extract per-facet mappers; unchanged evidence, unchanged advice.

### F7' · minor · javadoc · org.eclipse.fennec.codec.api (and others) — carried over from 2026-07-24 (F9), still accruing
- **Where:** org.eclipse.fennec.codec.api/src/org/eclipse/fennec/codec/constants/CodecOptions.java:636 (`@since 2026-06`, added since the last review); also new `@since 2025-12-17` in TypeDiscriminatorService.java:83
- **What:** Date-based `@since` tags continue to be written in new API code, so the inventory grows rather than shrinks.
- **Why it matters:** Every new occurrence raises the cost of the eventual sweep; `@since` should correlate with package versions for compatibility checks.
- **Suggested fix:** Adopt `@since 1.0` for everything pre-first-release now (a single regex sweep), and switch the habit in new code — otherwise this finding will be back a third time.

### F8' · info · solid-dip · org.eclipse.fennec.codec — carried over from 2026-07-24 (F10), partially resolved
- **Where:** org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/deser/TypeDeserializationEntry.java:420
- **What:** The suggested tiered resolution was implemented (`PackageResolver`, `INSTANCE` as documented tier-4 last resort; `resolvePackageVia` falls back only when no resolver is present) — but `buildNumericTypeValue` still queries `EPackage.Registry.INSTANCE` directly without going through the resolver.
- **Why it matters:** One remaining bypass of an otherwise clean abstraction; low impact (read-only lookup), flagged so it doesn't seed new direct call sites.
- **Suggested fix:** Route the numeric-type lookup through the per-load `PackageResolver` where one is available, as its siblings now do.

### F9' · info · release-readiness · org.eclipse.fennec.codec.playground, org.eclipse.fennec.codec.examples — carried over from 2026-07-24 (F11)
- **Where:** org.eclipse.fennec.codec.playground/bnd.bnd:1
- **What / fix:** Unchanged: no release-exclusion marker on the playground/examples bundles; verify against the fennec library release setup before the first release.

### F10' · info · solid-srp · org.eclipse.fennec.codec.api — carried over from 2026-07-24 (F12)
- **Where:** org.eclipse.fennec.codec.api/src/org/eclipse/fennec/codec/config/ConfigurationResolver.java:79
- **What / fix:** Unchanged at 1810 lines, still structurally cohesive; watch item only.

### F11' · info · release-readiness · repository — carried over from 2026-07-24 (F15)
- **Where:** cnf/build.bnd:16
- **What / fix:** Baselining still commented out; still no releases, so still acceptable. Must be enabled together with the `release-obr` branch as part of the first release — at that point this becomes a major finding.

## Systemic issues

1. **Exported-package hygiene (F1', F2') is now the oldest open theme** — open since 2026-07-24, ~12 bundles, and the repo's release preparations (Dash, README, CI) are otherwise converging. The `.internal`-sweep should be scheduled as an issue with a pre-release milestone; new code keeps inheriting the convention (`FingerprintPins` in exported `util`), so the cost grows with every feature.
2. **Date-based `@since` (F7')** keeps replicating into new API; fix the habit, not just the inventory.

## Skipped / not reviewed

- **Follow-up pass:** all 15 previous findings re-verified against current code at their (re-located) positions.
- **Full reads (fresh pass):** everything changed since 2026-07-24 — fingerprint feature files (`FingerprintPins`, `PackageResolver`, `AbstractFingerprintTCK`, TCK tests), `TypeDiscriminatorService`/`TypeDiscriminatorRegistry` (partial: key sections around the static cache and handler contract), migrated CI workflows, `docs/ci.md`, README, `DEPENDENCIES`, changed `bnd.bnd` files, license headers on every file added since the last review (all clean).
- **Skimmed only:** changed format providers (csv/ods/xlsx — S7 applicability change), `CodecResource.java` (1053 lines, several fingerprint commits), serializer/deserializer entry changes beyond the `INSTANCE` call sites.
- **Not re-reviewed:** bundles untouched since 2026-07-24 (previous findings for them re-verified individually, but no fresh full read); EMF-generated code (headers/export checks only, per rules); `docs-site/`.
- **Not verifiable locally:** content of the org reusable workflows (`eclipse-fennec/.github@227b1df`) — license-gate behavior taken from `docs/ci.md`; whether playground/examples actually publish (F9').
