# Quality review — fennec-codec (emf.codec) — 2026-07-24
Mode: quick · Scope: whole repo · Rule sets: SOLID/OSGi + Eclipse Foundation (see skill references)

## Summary

| Severity | api-export | api-evolution | release-readiness | solid-srp | solid-dip | naming | javadoc | Total |
|----------|-----------|---------------|-------------------|-----------|-----------|--------|---------|-------|
| blocker  | 1         |               |                   |           |           |        |         | **1** |
| major    | 2         | 1             | 1                 |           |           |        |         | **4** |
| minor    |           |               | 1                 | 1         |           | 3      | 1       | **6** |
| info     |           |               | 2                 | 1         | 1         |        |         | **4** |

Overall the codebase is in good shape: license headers are present on **all** hand-written and generated Java files, CI/publishing has the correct shape (release on push to `main`, PR-triggered publishing correctly disabled, license enforcement via SkyWalking Eyes active), all six Eclipse root documents exist and are adapted, DS components use constructor injection with correctly handled dynamic references, and extension follows the whiteboard/provider pattern — no format-name switch chains anywhere. The dominant theme of the findings is **API-surface hygiene**: DS components and `*Impl` classes live in exported packages across most format-provider bundles, two exported packages have no explicit version, and the Dash/IP-check tooling is missing at repo level.

## Findings

### F1 · blocker · api-export · org.eclipse.fennec.model.metadata
- **Where:** org.eclipse.fennec.model.metadata/src/org/eclipse/fennec/model/metadata/service/package-info.java:19
- **What:** The exported package `org.eclipse.fennec.model.metadata.service` contains only implementation classes (`MetadataServiceImpl`, `MetadataServiceComponent`, `MapBasedMetadataIndex`) — an impl-only package published as API.
- **Why it matters:** Everything public in an exported package is API. Consumers can (and will) bind to the impl and the DS component class directly, freezing implementation details into the compatibility contract before the first release.
- **Suggested fix:** Remove `@Export` from the package (the service is consumed via the `MetadataService` interface in `org.eclipse.fennec.model.metadata.api`, which stays exported), or move the classes to a non-exported `...metadata.service.internal` package.

### F2 · major · api-export · systemic (12 bundles)
- **Where:** anchor: org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/resource/CodecValueRegistryComponent.java:44 (package `org.eclipse.fennec.codec.resource` is exported)
- **What:** DS component classes and `*Impl` classes sit inside exported packages throughout the format-provider bundles, mixed in with the deliberate API (providers, `CodecResource`).
- **Why it matters:** DS components are wiring, not API — exporting them lets clients instantiate/extend them, bypassing DS lifecycle and configuration, and every internal change to them becomes a potential breaking API change under semantic versioning.
- **Suggested fix:** Move DS components (and impl-only helpers) into a non-exported sibling package (e.g. `.internal`), keeping providers/annotations exported. Occurrences:
  - org.eclipse.fennec.codec.resource — `CodecResourceFactoryComponent`, `CodecValueRegistryComponent`
  - org.eclipse.fennec.codec.bson — `BsonResourceFactoryComponent` (+ delegates)
  - org.eclipse.fennec.codec.cbor — `CborResourceFactoryComponent`
  - org.eclipse.fennec.codec.csv — `CsvResourceFactoryComponent`, `CsvOverridableCodecOptions`
  - org.eclipse.fennec.codec.yaml — `YamlResourceFactoryComponent`
  - org.eclipse.fennec.codec.ods — `OdsResourceFactoryComponent`, `OdsOverridableCodecOptions`
  - org.eclipse.fennec.codec.rlang — `RLangResourceFactoryComponent`, `RLangOverridableCodecOptions`
  - org.eclipse.fennec.codec.xlsx — `XlsxResourceFactoryComponent`, `XlsxOverridableCodecOptions`
  - org.eclipse.fennec.codec.geojson — `GeoJsonResourceFactoryImpl`
  - org.eclipse.fennec.codec.jsonschema (`...jsonschema.v2`) — `JsonSchemaResourceFactoryImpl`, `JsonSchemaResourceImpl`; `...v2.value` — 4 reader/writer components
  - org.eclipse.fennec.codec.openapi — entire exported package is components/impls (`OpenApiResourceFactoryImpl`, `OpenApiResourceImpl`, 4 reader/writer components)
  - org.eclipse.fennec.codec.metadata (`...metadata.provider`) — `CodecAspectProviderComponent`
  - (org.eclipse.fennec.codec.rest `...jakartas.feature` — `CodecResourceSetFeature`/`CodecResourceSetCleanupFilter` are exported components, but JAX-RS feature classes are plausibly deliberate API for non-OSGi use; decide explicitly.)

### F3 · major · api-export · org.eclipse.fennec.codec
- **Where:** org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/format/impl/package-info.java:14
- **What:** The package `org.eclipse.fennec.codec.format.impl` is exported and serves as de-facto API — `JacksonFormatProvider` in it is the documented base class extended by the cbor and yaml bundles (`CborFormatProvider.java:40`, `YamlFormatProvider.java:37`).
- **Why it matters:** An `impl`-named exported package signals "not API" while actually being a cross-bundle extension point; tooling and reviewers will treat changes to it as free, silently breaking downstream providers.
- **Suggested fix:** Split the intended extension surface (`JacksonFormatProvider`, delegate base classes) into a properly named exported package (e.g. `org.eclipse.fennec.codec.format.jackson` or `.format.spi`) and make the remaining true internals private.

### F4 · major · api-evolution · systemic (3 packages) — ✅ fixed 2026-07-24
- **Where:** org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/context/package-info.java:30
- **What:** Exported packages without an explicit package version: `org.eclipse.fennec.codec.context` (package-info.java:30) and `org.eclipse.fennec.codec.config.effective` (package-info.java:27) carry `@Export` but no `@Version`; `org.eclipse.fennec.codec.tests.tck` is exported via `org.eclipse.fennec.codec.tests/bnd.bnd:4` without a version.
- **Why it matters:** They silently inherit the bundle version (visible as `0.1.0` in the generated manifest, vs. `1.0.0` for all sibling packages) and will drift on every bundle-version bump, making semantic versioning of the API meaningless and breaking future baselining.
- **Suggested fix:** Add `@org.osgi.annotation.versioning.Version("1.0.0")` to both package-info files; change the TCK export to `Export-Package: org.eclipse.fennec.codec.tests.tck;version=1.0.0`.

### F5 · minor · naming · org.eclipse.fennec.codec.tests
- **Where:** org.eclipse.fennec.codec.tests/bnd.bnd:4
- **What:** The exported API package `org.eclipse.fennec.codec.tests.tck` contains the reserved qualifier `tests` in its name.
- **Why it matters:** Per Eclipse naming conventions, `tests` marks non-API packages; a TCK that format-provider projects compile against is API and should not carry it.
- **Suggested fix:** If the TCK is meant as a published compatibility kit, consider `org.eclipse.fennec.codec.tck` (own bundle or renamed package); if it is build-internal only, keep it but document that the bundle is not released API.

### F6 · minor · solid-srp · org.eclipse.fennec.codec.jsonschema
- **Where:** org.eclipse.fennec.codec.jsonschema/src/org/eclipse/fennec/codec/jsonschema/v2/converter/JsonSchemaToEPackageConverter.java:73
- **What:** The two hand-written converter classes are very large single units: `JsonSchemaToEPackageConverter` (2600 lines) and `EPackageToJsonSchemaConverter` (1968 lines), each handling the full conversion in one class.
- **Why it matters:** At this size every schema-facet change (types, constraints, annotations, references) touches the same class — hard to review, hard to test facets in isolation, high merge-conflict surface.
- **Suggested fix:** Extract per-facet mappers (type mapping, constraints, annotations, cross-references) that the converter orchestrates; the exported-package boundary already supports this (`...v2.converter` is API, helpers can be private).

### F7 · minor · naming · org.eclipse.fennec.codec.workspace.library — ✅ fixed 2026-07-24
- **Where:** org.eclipse.fennec.codec.workspace.library/bnd.bnd:14
- **What:** Typo in the bundle name: `Bundle-Name: Eclispe Fennec Codec`.
- **Why it matters:** The name is user-visible metadata shipped in the manifest (and shows up on Maven Central).
- **Suggested fix:** `Eclipse Fennec Codec`.

### F8 · minor · naming · org.eclipse.fennec.codec.rest, org.eclipse.fennec.codec.playground — ✅ fixed 2026-07-24
- **Where:** org.eclipse.fennec.codec.rest/bnd.bnd:1
- **What:** `org.eclipse.fennec.codec.rest` and `org.eclipse.fennec.codec.playground` set no `Bundle-Name`/`Bundle-Description`, unlike every other bundle in the workspace.
- **Why it matters:** Manifest metadata is what consumers see in repositories and tooling; the rest bundle in particular is shipped API.
- **Suggested fix:** Add both headers, matching the style of the sibling bundles.

### F9 · minor · javadoc · org.eclipse.fennec.codec.api
- **Where:** org.eclipse.fennec.codec.api/src/org/eclipse/fennec/codec/format/CodecFormatProvider.java:47
- **What:** API javadoc uses dates in `@since` tags (`@since 2026-02-16`, `@since 2026-05`; also `@since Feb 27, 2026` in `CodecResourceFactoryComponent`) instead of version numbers.
- **Why it matters:** `@since` documents the API version that introduced an element — with dates, consumers can't correlate against the package/bundle version when checking compatibility.
- **Suggested fix:** Use the package version, e.g. `@since 1.0`; fix the pattern before the first release while all values are trivially `1.0`.

### F10 · info · solid-dip · org.eclipse.fennec.codec
- **Where:** org.eclipse.fennec.codec/src/org/eclipse/fennec/codec/util/TypeResolutionHelper.java:99
- **What:** Type resolution falls back to iterating the global `EPackage.Registry.INSTANCE` (also `FeaturePathTypeResolver.java:280`, `TypeDeserializationEntry.java:418,552`) — read-only use of the static global registry.
- **Why it matters:** Global-registry coupling makes resolution dependent on ambient JVM state rather than the ResourceSet/metadata service; the rule set treats existing read-only use as acceptable legacy, so this is an observation only.
- **Suggested fix:** Prefer resolving through the `MetadataService`/ResourceSet package registry first (already partially done) and treat `INSTANCE` strictly as last-resort fallback; avoid adding new call sites.

### F11 · info · release-readiness · org.eclipse.fennec.codec.playground, org.eclipse.fennec.codec.examples
- **Where:** org.eclipse.fennec.codec.playground/bnd.bnd:1
- **What:** The playground and examples bundles carry no marker excluding them from release/publication, so they appear to be published to Maven Central along with the real bundles.
- **Why it matters:** Scratch/demo bundles on Maven Central are immutable once released and enlarge the supported API surface unintentionally.
- **Suggested fix:** Verify against the fennec library's release setup; exclude both from the release repository if they are not meant to ship.

### F12 · info · solid-srp · org.eclipse.fennec.codec.api
- **Where:** org.eclipse.fennec.codec.api/src/org/eclipse/fennec/codec/config/ConfigurationResolver.java:79
- **What:** `ConfigurationResolver` is 1810 lines in the API bundle; structurally cohesive (parallel `resolve*Config` methods per config type + builder), so no violation — but it is the single largest API class and worth watching.
- **Why it matters:** Each new config dimension grows the same class and its cache; the parallel-method structure would support extraction of a per-config-type resolver strategy if it keeps growing.
- **Suggested fix:** None required now; consider splitting per-config-type resolution internals if a new config family is added.

### F13 · major · release-readiness · repository — ✅ setup added 2026-07-24 (12 restricted entries pending IP review)
- **Where:** DEPENDENCIES:1 (missing — repo root)
- **What:** The Eclipse Dash IP-check setup is entirely missing: no `tools/dash-licenses.sh`/`.bat`, no `.github/workflows/dash-licenses.yml`, and no generated `DEPENDENCIES` file at the repo root.
- **Why it matters:** IP cleanliness is mandatory for an Eclipse release; without the Dash workflow, restricted third-party licenses (this repo pulls POI, xmlbeans, SODS, fastcsv, snakeyaml, mongodb-bson, …) would go unnoticed until release review.
- **Suggested fix:** Copy the Dash setup from `eclipse-fennec/emf.osgi` and adapt (`github-project: emf.codec`); generate `DEPENDENCIES` from the bnd workspace via `bnd repo deps` and commit it. Any `restricted` entries need IP review via `dash-licenses.sh --review --project technology.fennec`.

### F14 · minor · release-readiness · repository
- **Where:** README.md:1
- **What:** README does not state the branch model (`snapshot` = development, `main` = release) nor the Maven coordinates (`org.eclipse.fennec.codec:*`).
- **Why it matters:** The fennec release guide expects the README to tell consumers which branch to target and how to consume the artifacts.
- **Suggested fix:** Add a short "Branches & Releases" section with the group id and an example dependency coordinate; copy the shape from emf.osgi.

### F15 · info · release-readiness · repository
- **Where:** cnf/build.bnd:16
- **What:** Baselining is disabled (`#fennec-baselining: true` commented out).
- **Why it matters:** Acceptable today — the repo has no tags/releases yet, and baselining needs a first release to baseline against. Once 0.1.0 ships, the API-evolution rules (§5) are unenforced without it.
- **Suggested fix:** Enable `fennec-baselining: true` together with the `release-obr` orphan branch as part of the first release, per the fennec release guide.

## Systemic issues

1. **DS components in exported packages (F1, F2, F3):** the workspace convention places each format provider's DS component in the same (exported) package as its provider API. One root cause, ~12 bundles. Fixing the convention once (components → `.internal` sibling package) and applying it mechanically resolves F1/F2 and most of F3 before the first release makes it an API commitment.
2. **Unversioned/implicitly versioned exports (F4):** two package-info files and one manifest header; a single sweep adding explicit `@Version` closes it.

## Skipped / not reviewed

- **EMF-generated code** (`src-gen*` everywhere; `org.eclipse.fennec.openapi.model`, `org.eclipse.fennec.codec.tabular.model` and `org.eclipse.fennec.codec.metadata` generated model code, generated into `src`): structural/naming checks suppressed per rules; license-header and export checks applied.
- **Test bundles** (`*.tests`, plus `test/` folders): header check applied (all clean); no SOLID/structure review per rules.
- **Full reads:** all 25 `bnd.bnd` files, generated manifests of `codec.api`/`codec`/`codec.rest`/`codec.tests`, all `package-info.java` export/version annotations, `CodecResourceFactoryComponent`, `CodecFormatProvider`, package listings of every exported package flagged in F1–F3, repo-level docs and workflows.
- **Skimmed only:** method bodies of the large converter/resolver classes (F6, F12 are sized-based with structure skim), `docs-site/`, `org.eclipse.fennec.codec.examples`, `org.eclipse.fennec.codec.playground` internals, LSP contract drift beyond a signature comparison of the bson/cbor/yaml providers (no drift visible at signature level).
- **Not verifiable locally:** whether playground/examples actually publish to Maven Central (F11) — depends on the fennec library templates outside this repo.
