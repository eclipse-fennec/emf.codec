# Codec V2 Development Guide

This document provides context for continuing codec development across sessions. It captures the goals, current state, and links to detailed architecture documentation.

**Last Updated:** 2026-08-19

**Session Summary (2026-08-19) — issue #154, an EMap with a non-String key read back empty:**

`ReferenceDeserializationEntry.deserializeEMap` assigned the JSON field name straight onto the key
feature (`entry.eSet(keyFeature, key)`). For `EMap<EInt, EString>` that is a `String` on an `EInt`
feature, so it threw — and because the `catch` sat around the **whole** `while` loop, the first bad
key aborted the entire map. The caller got a successful load with an empty map, which is
indistinguishable from a document that carried no entries. Measured in
`eclipse-fennec/emf.persistence-jpa` (`MongoEMapRoundTripTest`): the write was already correct
(`counts: {"1": "one"}`), only the read lost everything.

- **Fix:** keys go through `EcoreUtil.createFromString(keyAttribute.getEAttributeType(), name)` —
  the exact inverse of the `toString()` the writer uses, covering `EInt`, `ELong`, enums and any
  custom factory. Verified against EMF's `EFactoryImpl`/`EEnumLiteralImpl`: for a dynamic enum,
  `toString()` is the literal and `createFromString` throws `IllegalArgumentException` on an unknown
  one, so the two sides line up.
- **The try/catch is now per entry.** An unparseable name costs its own entry, the rest of the map is
  read, and `parser.skipChildren()` keeps the stream in sync. The failure is reported through
  `ConversionFailures.report` (owner = the entry class), so it follows the existing strictness
  hierarchy: a warning by default, an `IllegalStateException` under `strictOnConversion`. No new
  strictness flag was introduced.
- **Tests:** `EMapNonStringKeyTest` (+ `test-emap-keys.ecore`, a separate model so the shared
  `test-emap.ecore` assertions keep their exact JSON) — int-key and enum-key round trips, a foreign
  document, a broken key in lenient and strict mode, and an unknown enum literal. All six red before
  the fix.
- **Test trap worth remembering:** `EMap<Integer, V>.get(1)` binds to `List.get(int index)`, not
  `Map.get(Object)`. Use `get(Integer.valueOf(1))` or the assertion compares against the entry object.
- **Spec:** `10-reference.md` gained §8.1 "EMap keys" (the flatten section moved to §8.2) stating the
  conversion in both directions and that dropping the whole map is not allowed.
- **Follow-up (2026-08-19, same day):** the two missing write-back tests were added to
  `EMapNonStringKeyTest`. `keysSurviveTheWriteBack` reads a document with int and enum keys, writes
  it again and compares the parsed trees (field order and whitespace must not decide) — verified by
  dumping the output, `{"counts":{"1":"one","2":"two"},"levels":{"LOW":"quiet","HIGH":"loud"}}`.
  `writeBackNormalisesTheKey` pins the boundary of that claim: a key spelled `"007"` comes back as
  `"7"`, because the round trip goes through the data type and not through the text. That is correct
  normalisation, and it is now written down so nobody "fixes" it later. Spec §8.1 says the same.
- **Docs completed (2026-08-19):** the new diagnostic was missing from the error catalog.
  `15-error-handling.md` §6.3 now lists both EMap rows — the entry class without key/value feature
  (ERROR, map skipped) and the unparseable key (ERROR under `strictOnConversion`, else WARNING,
  **that entry** dropped) — and §5.1 names `ReferenceDeserializationEntry` as their source.
  `11-feature.md` §11.5 was incomplete too: it described `strictOnConversion` purely in terms of a
  feature keeping its default, which is not what happens for a map key. Checked that the docs-site
  picks this up automatically — `docs/codec-v2-spec/` is on the `guides.mjs` allowlist,
  `node sync-guides.mjs` runs clean and rewrites the cross-links to sibling routes;
  `docs-site/docs/guides/` is gitignored and built by CI, so nothing is committed there.
- **Not touched:** an EMap key feature that is an `EReference` still round-trips through
  `toString()`/raw name — nonsense on both sides, but out of scope here and not observed in the wild.

**Session Summary (2026-08-18) — issue #152, contained children without a resource were written as `$ref: "#//"`:**

`ReferenceSerializationEntry.isCrossDocument` fell through to comparing the write context's resource
against `target.eResource()`, so a containment child reporting no resource of its own was classified
cross-document and written as `{"_type": …, "$ref": "#//"}` instead of being inlined. Models generated
with `suppressNotification="true"` hit this for **every** contained child: their containment features are
backed by a `BasicInternalEList`, which never sets the child's container, so the child reports neither
container nor resource. Surfaced in the Model Atlas (`scope-api`/`workflow-api` set the flag):
`GET /scopes/{scope}` registries arrived as empty stubs on the REST client, breaking
`ValidationServiceImpl.resolveConstraintSet` (`No COCL registry found in scope: jena`). The XML writers
never consult the child's resource, so the same graph was lossless in XMI and lossy in JSON.

- **Fix (Guido Grune, branch `fix/subtype_containment_serialization`):** a target that owns no resource
  cannot be referenced, so it is part of the document being written — `isCrossDocument` now short-circuits
  on `target.eResource() == null` before consulting the context. Genuine cross-document containment is
  unaffected: those children own a direct resource and are caught by the preceding `eDirectResource()`
  branch (existing `CrossDocumentContainmentTest` stays green).
- **Tests:** `SubtypeContainmentSerializationTest` covers both write paths (plain `CodecResourceFactory`
  and the `CodecFormatResourceFactory` delegate) for container-less children (red before the fix, verified),
  subtype instances in same/foreign packages (regression, already worked), and — added this session —
  a full round trip proving the child's data and concrete type survive. Test-infra reminder confirmed again:
  dynamic test packages must be resource-backed (`new ResourceImpl(URI.createURI(nsURI)).getContents().add(pkg)`),
  otherwise the written `_type` has no schema part and the reader cannot resolve the root class.
- **Spec sharpened** (spec follows the output): `10-reference.md` §5.1.1 and §7.2 now state the exact
  detection rule — cross-document containment means `eDirectResource() != null` (or a proxy), matching
  EMF's `XMLSaveImpl.saveElement`; a resource-less child is always inlined. §9.3.1 already had it right.
- **Not touched:** the deeper `suppressNotification` root (children never get a container) still affects
  `EcoreUtil.copy` (#94); the Model Atlas end-to-end check via `RemoteValidationIT` (needs the rebuilt
  `jena-snapshot` image with this codec) is still open.

**Session Summary (2026-08-11) — issue #147, a public non-OSGi entry point for openapi and jsonschema:**

`OpenApiResourceFactoryImpl` had moved into the non-exported `…openapi.internal` package, which left no
supported way to load an OpenAPI document outside OSGi (emf.util's plain-Java `openapi.ecore` importer
broke on it and worked around it with a copied security-requirement reader). Resolution, applied to both
format bundles symmetrically:

- **`*Impl` public and plain, `*Component` internal and wiring** (continues #58): `OpenApiResourceFactoryImpl`
  (`…codec.openapi`) and `JsonSchemaResourceFactoryImpl` (`…jsonschema.v2`) carry no DS annotations;
  `OpenApiResourceFactoryComponent` and `JsonSchemaResourceFactoryComponent` in the respective `internal`
  packages extend them and hold `@Component`/`@Activate`/`@Reference`.
- **Value handlers are plain objects, not services.** The `@Component(service = CodecValueReader/Writer.class)`
  annotations came off the four OpenAPI handlers (moved to the exported `…codec.openapi.value`) and the four
  jsonschema handlers in `…v2.value`. Both factories fill their registry themselves via
  `initializeValueRegistry(CodecValueRegistry)` (protected, the extension point) resp. the public static
  `registerDefaultValueHandlers(CodecValueRegistry)` — the latter for consumers who wire an
  `OpenApiResourceImpl`/`JsonSchemaResourceImpl` without the factory, which is the second half of #147.
  Note the whiteboard did work before: a delayed `@Component` providing a service is activated when
  `CodecValueRegistryComponent` binds it — `immediate=true` is only needed for components without a service.
- **The DS components copy the shared registry** (`super(ms, registry.copy())`). Registering plain objects
  into the shared `CodecValueRegistryComponent` singleton would outlive the providing bundle (nothing
  unbinds them) and would pin its classloader. Handlers coming from the shared registry are taken over and
  **win** over the defaults, because `registerDefaultValueHandlers` only registers names that are absent
  (`hasReader`/`hasWriter`) — that is also what makes `super.initializeValueRegistry(registry)` usable as the
  last statement of an override.
- **Regression caught on the way:** the jsonschema no-arg constructor briefly handed an *empty* registry to
  `JsonSchemaResourceImpl`, whose `registry != null` fallback then no longer fired — the four EPackage/EClass
  handlers were silently missing standalone. The fallback now delegates to `registerDefaultValueHandlers`,
  so the list exists once.
- **Packaging fixed:** `…jsonschema.v2.internal/package-info.java` still had `@org.osgi.annotation.bundle.Export`
  while its own javadoc said "deliberately not exported" — the package is private now. Verified in the built
  manifests: exported are `…jsonschema.v2` + `.v2.value` and `…codec.openapi` + `.openapi.value`, private are
  both `internal` packages, and neither bundle provides `CodecValueReader/Writer` services any more.
  The same `@Export`-vs-javadoc contradiction sat in nine more packages and is fixed too, see #149 below.
**Fixed — issue #149, nine internal packages were exported against their own javadoc:**

`@org.osgi.annotation.bundle.Export` sat above a javadoc reading "deliberately **not** exported (issue #58)"
in bson, cbor, csv, geojson, ods, rlang, xlsx, yaml and metadata.provider — and without a `@Version`, so the
packages shipped with the bundle version (`0.1.0`), which is no importable contract. Checked first that no
consumer outside the owning bundle exists; the supported non-OSGi entry points of those bundles are the
exported `*FormatProvider` classes, and every `internal` package holds only `*ResourceFactoryComponent`,
`CodecAspectProviderComponent` or `*OverridableCodecOptions`.

**geojson was the exception**, and the lesson worth keeping: a mechanical export removal would have
reproduced #147 there. `GeoJsonResourceFactoryImpl` was DS component *and* documented non-OSGi entry point in
one class ("For non-OSGi usage, use `GeoJsonResourceFactoryImpl(MetadataService)`") and it sat in the
`internal` package — an `*Impl`, not a `*Component`. It got the #147 treatment instead: public in
`…codec.geojson` with an added standalone no-arg constructor, DS annotations moved to
`GeoJsonResourceFactoryComponent` (including the static `geojsonPackage` reference that keeps it unsatisfied
without the model). The generated component XML is identical apart from the component name — worth knowing if
anyone ever configures it by PID. `docs/osgi-resource-factory-architecture.md` used this class as its worked
example and was updated with it.

**When adding a format bundle, the rule is now:** `*Impl` public and free of DS annotations, `*Component`
internal and carrying them, value handlers registered by the factory rather than as services, and the
`internal` package-info without `@Export`.

**Fixed in passing — issue #148, `fallbackStrategy=ERROR` did not fail the load inside containment:**

Removing a debug leftover uncovered a spec violation, so the two belong together. The leftover
(`if (true) throw new IllegalStateException("TEMP-PROOF swallowed: …")` in `ReferenceDeserializationEntry`,
committed in f0cd400 / #128 on 2026-08-06 and published on `snapshot` for five days) turned **every**
containment-deserialization error into a throw instead of a collected diagnostic. It also kept
`CodecResourceInlineMappingTest.throwsOnUnknownDiscriminator` green for the wrong reason: with the leftover
gone, `TypeDiscriminatorRegistry.resolve`'s `IllegalStateException` for an unmapped discriminator under
`fallbackStrategy=ERROR` (spec `08-discriminator-mapping.md:337` — "Fail immediately, throw exception") was
caught by the broad `catch (Exception e)` and downgraded to a diagnostic, so a load that must fail
succeeded with a partial model.

The fix needed **no new exception type**: `deserializeContainedObject`, `deserializeReferenceElement` and
`CodecEObjectDeserializer` already carried `catch (IllegalStateException e) { throw e; }` with exactly that
rationale — the convention existed, the callers one level up undid it. The clause is now applied at the five
remaining sites that wrap a nested deserialization or type resolution (containment element, non-containment
element, `deserializeFullObject`, EMap, EMap value). The same broad catch in `AttributeDeserializationEntry`,
`IdDeserializationEntry` and `FeaturePathTypeResolver` was left alone on purpose — no fatal condition
originates there today. Full `./gradlew build` green afterwards, so nothing relied on the swallowing.

**Session Summary (2026-08-08) — the code-quality block worked off (#80 with nine sub-issues):**

Hardening is done; the tracker holds only #46 and its sub-issue #145. The lesson worth carrying
forward is about **triage**, not any single fix.

- **The severity labels only partly held.** #81 was correctly `major` and a genuine leak: the
  static `PER_PACKAGE_VIEW` in `TypeDiscriminatorService` held every `PackageMetadata` — and
  through it the `EPackage` and all its `EClass`es — for the lifetime of the JVM, because
  `unregisterPackage` cleaned the instance registries and nothing else. A re-registered package
  was served its predecessor's view. But #67 was filed as `info` and carried the highest cost if
  missed (Maven Central artifacts are immutable), while #62 was `minor` and would have demanded
  the most work — splitting two working converters, ~4500 lines, with no defects pointing at
  them. **Read the issue, not the label.**
- **Surveying usage before acting contradicted the issue twice.** #58 lists the
  `jsonschema.v2.value` handlers and the JAX-RS feature classes as components to hide; the
  openapi bundle uses the first group in *production* code, so hiding them would have broken it.
  Of the six classes in `format.impl` (#59), only three are used outside the package — those
  moved to the exported `format.jackson`, the rest stayed and the package lost its `@Export`.
  **Verified in the built manifest, not just at the compiler.**
- **Release exclusion has a convention, in a repo nobody thought to check.** `-releaserepo:
  Sonatype` is workspace-wide from the bnd library; the per-project opt-out is
  `-maven-release: local`, as used by **`/opt/git/fennec-model.atlas`** — not by
  emf.persistence-jpa, emf.osgi or fennec-odata, which is why the first search came up empty.
- **The trap next to it:** `workspace.library/required.bndrun` resolves to the list that becomes
  the library's `-buildpath` and from there its maven dependencies. `examples` and the TCK are
  in it, so marking either `local` would leave the published library pointing at an artifact
  that is not on Central. **Check that file before excluding anything.** It also settles a
  question that looked open: the TCK is a declared dependency of the published library, so it is
  already published API. Its package name keeps the reserved `tests` qualifier by decision.
- **Three issues were closed without code**, each with the condition that would justify
  reopening: #62 (converters — reopen when they appear in bug reports), #66 and #68 (both
  conclude "observation only" / "None required now" themselves).
- **Also done:** 134 date-based `@since` tags became `1.0` (#65) — verified first that all 54
  exported packages really are at 1.0 and nothing is released. And #83, which turned out to be
  fixed since a6e6ebc but had left standing exactly the fallback its own *Suggested fix* warned
  about: `uri.resolve(resourceURI)` against the reader's relative base `temp/id` throws
  `IllegalArgumentException`. The existing test passed only because it used an absolute base.

**Session Summary (2026-08-06/07) — the #110 campaign worked off end to end (#112-#121, #124, #129, #131, #132, #134):**

All eleven asymmetry tickets are merged and closed, plus the three defects the work itself uncovered. **Read `docs/codec-v2-ser-deser-asymmetry-review.md` first** — it still carries the per-finding evidence; this entry records what changed and, more usefully, *why the shape of the bugs repeated*.

- **The two root causes held up.** Root cause 1 (write side resolves an effective/scoped value, read side reads it raw or global) produced #112, #116, #119 and #120. Root cause 2 (permissive writer, closed-allowlist reader) produced #115, #117 and #118. A **third** emerged late and was not in the original review: diagnostics that never reach the resource, so nobody can act on them (#131, #134).
- **The hang was three bugs stacked.** A `FormatDelegateParser` numeric token lost its value through Jackson's `TokenBuffer` deferred numbers (#129) → the conversion failed two levels from the cause and was **swallowed** → the parser stayed mid-object → an unguarded `while (parser.nextToken() != END_ARRAY)` waited for a token that could never come and the whole suite hung with no output. Fixed in three layers: the parser caches per-token values with type-exact first reads (#129), nothing is swallowed without resynchronising (#131), and all 27 token loops go through the new `util/TokenLoops` which terminates on stream end (#132). **If a test run ever hangs again, look for a swallowed exception before looking at the loop.**
- **Strictness is now a hierarchy (decision by Mark, #134).** `DeserializationMode.STRICT` is the umbrella: **any** error fails the load with an `IOException` carrying a `CodecDiagnosticException`; `strictOnUnknown` and `strictOnConversion` are subsets that fail for one kind of problem each. The diagnostics stay on the resource *as well* — reporting and failing, not one instead of the other. The spec had contradicted itself (07-supertype.md §9.3 said "fail" and the code threw; 06-type.md §6.5.2 said "→ ERROR" and the code carried on); §6.5.2 is now explicit. Breaking, deliberately — `STRICT` had no users.
- **Second standing rule from the same decision: a diagnostic that only reaches the JUL logger does not exist.** `TypeResolutionHelper`'s ten "could not resolve" messages were logger-only, so a caller saw the generic `Type resolved via fallback` — *that* resolution fell back, never *why*. It now takes an optional `DiagnosticCollector`; old signatures delegate. Same treatment for the type-as-attribute failure path. `ContextHelper.getDiagnosticCollector` gained the null guard its callers were working around — that guard's absence caused 22 test failures when the wiring landed.
- **Instrument, don't sprinkle.** For #131's "assert diagnostics everywhere", patching the load path to print every diagnostic and running the full suite found **50 dirty loads across 27 test classes** in one pass. Most were deliberate negative tests; the sweep isolated one real defect (a non-changeable feature reported as `Unknown feature`, which `strictOnUnknown` escalated into a failed load for a field the model declares) and three expectations that were described in comments rather than checked. Recommended technique for the next coverage question.
- **Not every finding is a defect.** The same-nsURI fallback warning looked like a test gap and is correct behaviour: two versions sharing an nsURI cannot be told apart from the document alone, so resolving through the caller's hint deserves the warning — telling them apart from the data is what fingerprinting is for. It is asserted now rather than tolerated. Blind-fixing it would have removed a real signal.
- **#120 decision (Mark):** an object reached through its container's `idFeatures` writes its features, **not** a second `_id` — it *is* the parent's identity, and `ID_ONLY` would have hidden the components that are its only source. Effectively `FEATURE_ONLY` for that position only; the same class elsewhere keeps its configured key mode. Spec 09-id.md §4 gained §4.1-§4.5 (separator belongs to the contained type, the STRUCTURED form, the ban on id config at the reference, the empty-reference case).
- **#110 separator precedence (Mark):** a separator present in the document overrides the configured one, because the configured value is the more likely injection point.
- **#124 closed the coverage gap that let #113 hide.** `FileRoundTripTest` writes to a `@TempDir` and loads through a **fresh** `ResourceSet`, on both write paths: polymorphic containment, same-document references asserted by identity, reference-based and multi-part ids, multi-valued attributes, `EJavaObject` map/list. All 11 green on the first run — no defects, the proof was what was missing. Before this, 1 test class of 138 wrote a real file.
- **State:** 1497 tests, 0 failures. #110 itself is still open as the campaign bracket; everything under it is done.

**Session Summary (2026-08-05) — ser/deser asymmetry review (#110) + crash fix #111:**

The #108 work raised the question whether more ser/deser asymmetries exist. A four-way sweep (id, type/supertype, attribute, reference + orchestration) produced ~34 candidates; every severe one was re-read in the code before being reported. **Read `docs/codec-v2-ser-deser-asymmetry-review.md` before touching any entry class** — it carries the full assessment with file:line evidence.

- **The rating matters more than the count: 1 crash + 16 real defects.** 7 candidates are one-directional **by design**, 2 are documented limitations, 3 are residue, the rest are gaps missing on *both* sides. Calibration case (Mark): the supertype plane is a **write direction** — `_supertype` is for consumers, state is rebuilt from `_type` + features, and reading it only makes sense as opt-in verification. See [[supertype-is-a-write-only-plane]] in the assistant memory. The spec itself settles most calls: 10-reference.md §9.2/§10.4, 11-feature.md §13.1 (the visibility gates deliberately differ per direction), 13-load-save-options.md §2.11.
- **Two root causes** cover nearly every defect: (1) the write side resolves an *effective/scoped* value while the read side reads it *raw or global* (the crash, scoped `typeKey`, `typeSchemaKey`, PLAIN `_separator`, the `_supertype` key); (2) the write side has a permissive fallback, the read side a closed allowlist (`EJavaObject` maps, array component types, id conversion, `EDate`). Both hid because **every round-trip test drives both sides from the same resolver** — new tests must load with a *fresh* resolver.
- **Two standing rules set by Mark:** where the codec does more than the spec, the **spec is pulled up to the code** (the written output is the contract — hence #117: if the writer emits an array type, the reader must read it back and §7.1 gains the row); and where the spec is vague, it is **sharpened by adding wording**, not reinterpreted silently.
- **Four decisions:** STRUCTURED `_id` follows the spec (`idKeyMode`/`idValueKey`, #119, existing tests get pulled along); `EDate` in plain JSON moves to the canonical ISO form (#118); `codec.flatten` becomes a documented write-only export feature (#121); `idFeatures` on an EReference gets implemented (#120).
- **Fixed here:** #111 — `typeFormat=STRUCTURED` + `superTypeSerialize=true` threw an NPE on save because `ser/TypeSerializationEntry.java:360` used the raw `getSuperTypeKey()` (null default) instead of `getEffectiveSuperTypeKey(STRUCTURED)`. Regression test `CodecResourceSuperTypeTest.superTypeEmbeddedInStructuredTypeObject`.
- **Open:** #112-#117 and #119-#121 are filed individually; the remaining BUG entries and the RESIDUE batch live in the review doc only and share root cause 1.

**Session Summary (2026-08-05) — issue #108, STRUCTURED id decode:**

Single-defect session, found via emf.persistence-jpa#110 (Mongo compound `_id`). `IdDeserializationEntry.deserializeStructured` overwrote an id component with the combined value whenever the `eID` attribute was itself one of the `idFeatures`. Fixed by scoping the combined write to the separate-derived-key case; the inverse gap on the PLAIN path (a derived key attribute was never filled at all) was fixed in the same PR, so both formats now restore identical object state. See §7.3 (2026-08-05) for the full entry. Full `./gradlew build` green (3419 tests, 0 failures).

**Session Summary (2026-08-05) — id-plane issue batch #99-#104 (branch `issue#99-#104`, one commit per issue, not pushed):**

All six issues came out of the MongoDB backend work (emf.persistence-jpa#110) but were verified as general codec defects first; every claim was confirmed against the code before touching anything.

- **#99 repurposed to a spec fix, no code change.** The original ask (fallback over *all* `iD="true"` attributes) targets a model state Ecore forbids: `EcoreValidator.validateEClass_AtMostOneID` errors on a second id attribute, so the `getEIDAttribute()` fallback in the entries was correct all along. Spec 09-id.md §1/§5.0/both flow diagrams, 04-common-types.md and 16-annotation-reference.md now say "the single eID attribute"; compound identity is `idFeatures`-only. The GitHub issue was retitled/rewritten accordingly, with a consumer note that #110 is **not** gated on codec changes.
- **#100 settled as "documented derived knob".** `IdConfig.getStrategy()` has no ser/deser consumer — the runtime switch is the resolved id feature list. Spec §5.0 carries the informational-knob note, §7 is phrased by feature count, the `IdConfig.getStrategy()` javadoc says it, and `IdConfig.validate()` already errors on COMBINED-with-empty-`idFeatures` (test 2.3 in `IdConfigSpecTest`). The existing `CodecResourceIdTest` suite (ID_FIELD + multi-`idFeatures` → combined output) is the behavioral pin. Bonus fix: `Builder.useId(false)` mapped to `idStrategy="NONE"` — a literal that **does not exist** on the enum, silent no-op — and now maps to `idKeyMode=NONE`. GeoJSON (the only `useId(false)` user) thereby lost an accidental `_id` in its output.
- **#101 implemented (behavioral change of the default output!).** `keyMode=ID_ONLY` (the default) now suppresses id-component EAttributes in the payload — identity is no longer duplicated. Decisions signed off by Mark: explicit `forceWrite` on a feature wins over the suppression; a PLAIN combined component containing the separator logs a ser warning (the split is now the only deser source). EReference id sources stay in the payload (spec §4). Deser side needed nothing (PLAIN split / STRUCTURED readers already restore). Fallout handled: the `Order` fixture in `test-annotated.ecore` only round-tripped **because of** the duplication (its `-` separator collides with `CUST-001`/dates) — it now declares `idKeyMode=BOTH` explicitly, and a new `OrderCompact` fixture pins the ID_ONLY round trip through `_id` alone; the two `idOnTop` tests use BOTH (the floated feature must exist), spec §8.7 note updated.
- **#102 fixed:** `CodecAspectProvider.buildIdConfig` now trims `idFeatures` tokens, drops empties, and a class-level list **replaces** the copied package default instead of appending (issue-#75 layering). Tests in `CodecProfileBuildTest`.
- **#103 fixed twice — the word-swap existed on two planes.** `CodecOptions.CODEC_ID_SERIALIZE_SEPARATOR` is now `codec.idSeparatorSerialize` (was the swapped form, silent no-op); a reflective guard test pins every `CODEC_ID_*` constant to a resolvable `ConfigProperty`. Same swap in the annotation plane: `KEY_ID_SERIALIZE_SEPARATOR` parsed `idSerializeSeparator` while the spec documents `idSeparatorSerialize` — fixed with the fixture.
- **#104 implemented, deliberately named `objectId` (not the doc example's `mongoId` — Mark wants no Mongo branding; `ObjectId` is a plain BSON spec type from `org.bson`, already on the buildpath, no driver added).** The id plane now actually consumes `idValueWriterName`/`idValueReaderName` (the spec flow diagrams always showed the hook; nothing implemented it): `IdSerializationEntry`/`IdDeserializationEntry` take the `CodecEntryContext` and route the PLAIN id scalar through registry handlers. `FormatDelegateGenerator` got the ObjectId passthrough mirroring the #97 DateTime pattern. BSON module ships `ObjectIdValueWriter` (hex → native `ObjectId`, `ObjectId.isValid`-guarded, string fallback) and `ObjectIdValueReader`; pinned by `BsonObjectIdRoundTripTest` (native round trip, non-hex degradation, opt-in default). Docs: 14-custom-values.md shipped-handler note, 09-id.md §5.0, format-tck-capability-map.md.

~~**Known leftover (candidate for a new issue):** `AspectToPropertiesConverter` never maps `idSeparatorSerialize`, `idOnTop`, `idValueReaderName`, `idValueWriterName` into the property bridge~~ — filed and fixed as #106 in the same session, see below. Issues #99-#104 were closed by PR #105.

**Verification:** full `./gradlew build` green after every issue commit (final state: all modules, 0 failures).

**Follow-up #106 — property bridge completeness + `idOnTop` default flip (same session, PR #107):**

- **Bridge:** `AspectToPropertiesConverter` now forwards `idSeparatorSerialize`, `idOnTop`, `idValueReaderName`, `idValueWriterName` (the missing value-handler keys had silently disabled annotation-wiring of the #104 BSON `objectId` handler) and, same gap class on the supertype side, `superTypeAsArray`, `superTypeSeparator`, `superTypeFormat`. `AspectToPropertiesConverterTest` guards the full annotation-fillable key set and pins every emitted class-level key to a canonical `ConfigProperty`.
- **`superTypeFormat` needed presence detection:** runtime default is `null` (inherit from `typeFormat`) but a generated enum getter can never return null — the attribute is now `unsettable="true"` in `codec.ecore` (regenerated by Mark; generation runs via bnd `-generate`, but ask Mark first per his global preference) and the bridge forwards it only when `eIsSet`.
- **BREAKING — `idOnTop` defaults to `true` (decision by Mark):** the model default (`true`, "MongoDB: _id first") and the runtime default (`false`) contradicted each other, making an explicit `idOnTop=true` unrepresentable in the bridge. Spec and runtime now follow the model: `ConfigProperty.ID_ON_TOP`, 09-id.md §5.0/§8.7/§12, 05-global-options, 01-architecture, 16-annotation-reference, codec-options-reference. Combined with #101, the default document shape is now `{"_id": ..., "_type": ..., features...}`; tabular exports float the eID column to front by default (explicit `idOnTop=false` coverage kept). Downstream consumers asserting exact field order (e.g. emf.persistence-jpa) may need updates.

**Session Summary (2026-08-03) — issue #97, BSON native BsonDateTime:**

**`java.util.Date` attributes now round-trip through BSON's native date-time type (commit `ab3c490` on `snapshot`):**

- **API:** `FormatDelegate` grew the `supportsNativeDateTime()` (default `false`) / `writeDateTime(long epochMillis)` (default: `writeLong`) escape hatch, mirroring the existing native-ObjectId pair. `BsonFormatDelegate` implements it via `BsonDocumentWriter.writeDateTime`; `BsonFormatProvider.BsonStreamWriter` and `FormatDelegateGenerator` pass it through. `AttributeSerializationEntry.writeDateValue` uses it when the generator is a `FormatDelegateGenerator` with native support.
- **Deliberate deviation from the issue text:** no `nativeDateTime` opt-in option — native is the **default** whenever no `dateFormat` is configured. Justification (signed off by Mark): the old default wrote `Date.toString()` (`Fri Feb 13 ...`), which `convertObjectFromString` (only `yyyy-MM-dd`/ISO) could never parse back, so the default-format round trip was already broken and there is no legacy data to protect; the BSON format is a new development without released consumers. A configured `dateFormat` always wins and keeps the string form; JSON/YAML/CBOR delegates keep the `false` default and are byte-identical. Documented in the issue: https://github.com/eclipse-fennec/emf.codec/issues/97#issuecomment-5171067084
- **Read-back:** `BsonFormatReaderDelegate` maps `DATE_TIME` → `TokenType.VALUE_NUMBER_INT` and `readLong()` reads it via `reader.readDateTime()` — no new TokenType. `AttributeDeserializationEntry.convertFromInteger` converts a long to `Date` when the target instance class is Date-assignable (this also upgrades JSON: epoch-millis ints for EDate attributes now bind instead of failing at `eSet`). The string path stays as fallback for `dateFormat`-written documents.
- **`Date[]` array attributes fixed symmetrically:** the write side emits native elements through `writeValue`, and `readObjectArray` now converts `VALUE_NUMBER_INT` elements directly to `Date` — previously the epoch-millis string went through `convertObjectFromString`, threw, and the element was **silently dropped** (warning only).
- **Tests:** `BsonDateTimeRoundTripTest` (native round trip, `Date[]` round trip, configured-`dateFormat`-stays-string; the date assertion is timezone-tolerant on purpose), `BsonFormatDelegateTest` unit additions, `ArrayAttributeDeserializationTest.dateArrayFromEpochMillis`.
- **Docs:** `codec-options-reference.md` — the `codec.dateFormat` row no longer claims an "ISO 8601 fallback" (that was never true); it now states native-vs-`toString()` behavior and warns the `toString()` fallback is write-only. Spec `17-format-abstraction.md` §6.1 documents the native date-time contract.
- **Verification:** local `./gradlew build` green (0 test failures; the Felix Jetty weaving-hook message in the OSGi run is pre-existing noise). CI run 30848039352 was still in flight at handoff.
- ~~**Open follow-up:** `java.time` types (`Instant`, `LocalDateTime`, …) still fall through to `toString()`~~ — done in the same session, see #98 below.

**Follow-up #98 — native path extended to the instant-like `java.time` types (same session):**

- **Scope decision (round-trip correctness as the hard requirement, per Mark):** `Instant`, `LocalDateTime`, `LocalDate` go native (zone-less types use the **UTC convention** on both sides — lossless); `OffsetDateTime`/`ZonedDateTime` **never** go native because an epoch instant cannot restore the offset/zone — they stay on the ISO-8601 string path, which round-trips exactly. All other `java.time` types (`LocalTime`, `Duration`, …) stay strings. `Instant` sub-milli precision is truncated (BSON DateTime is millis).
- **Opt-out:** a configured `dateFormat` keeps the `java.time` types on strings too (it is not applied to them — `SimpleDateFormat` can't format `java.time` — it just disables the native path), so one option keeps all temporal values string-shaped.
- **Latent bug found by the new tests:** the `java.time` **string** read path was broken all along — two independent holes. (1) `convertObjectFromString`'s reflection chain looked up `parse(String)`, but the `java.time` types declare `parse(CharSequence)` → `NoSuchMethodException`, so the `SAFE_REFLECTION_TARGETS` allowlist never actually worked for them; now falls back to the `CharSequence` signature. (2) The single-value path (`convertFromString`) went straight to `EcoreUtil.createFromString`, which throws for dynamic `EDataType`s without factory conversion → caught upstream → **silent `null`** + warning; now falls back to `convertObjectFromString` (reflection allowlist) when EMF conversion throws, with the reflection failure attached as suppressed exception. Generated models with real factory conversion are untouched (EMF path still tried first).
- **Where:** `AttributeSerializationEntry` (`writeJavaTimeValue`/`toEpochMillis`), `AttributeDeserializationEntry` (`isEpochMillisTarget`/`fromEpochMillis` shared by `convertFromInteger` and `readObjectArray`, plus the two string-path fixes). Tests in `BsonDateTimeRoundTripTest` (java.time natively, `Instant[]` elements, `ZonedDateTime` stays string and round-trips, `dateFormat` opts `Instant` out and the ISO string still round-trips). Spec 17 §6.1 now carries the full type/mapping table; `codec-options-reference.md` `codec.dateFormat` row updated.

**Session Summary (2026-07-30) — metadata migration:**

**Migrated the codec onto the emf.osgi metadata & fingerprint API (umbrella #85, sub-issues #86-#92; PR #95 against `snapshot`):**

The dependency on `eclipse-fennec/model.metadata` is gone. `org.eclipse.fennec.model.metadata[.api]` is no longer on any buildpath, in any bndrun, or in `cnf/central.mvn`. Consumers now use `org.eclipse.fennec.emf.osgi.metadata` (API + service) and `org.eclipse.fennec.emf.osgi.model.metadata` (the tree); the six codec enums (`SerializationFormat`, `TypeStrategy`, `IdStrategy`, `IdKeyMode`, `SuperTypeSelection`, `EnumSerializationStrategy`) are codec-owned in `org.eclipse.fennec.codec.metadata.model.codec` after `codec.ecore` became standalone (#87). 137 Java files, 18 `bnd.bnd`, four bndruns.

**This supersedes the OSGi/metadata statements in the 2026-02-25 entries below** — the `AspectProvider` SPI, `MetadataServiceComponent` in a codec-side `model.metadata` bundle, and `PackageMetadata.getProfiles()` no longer exist.

What changed beyond renames — read this before touching aspect code:

- **Aspect access is the one trap.** An aspect now lives in `AspectEntry.content`, typed as a bare `EObject`. Filtering `getAspects()` by aspect type therefore **compiles and is always empty**, because the type check runs against `Object`. Use `CodecAspectProvider.codecAspect(aspects, Type.class)`, which matches the `codec` type id *and* the content type. This bit `AspectToPropertiesConverter` and nine test sites during the migration.
- **Diagnostics moved to the owning entry** (`AspectEntry.diagnostics`), and `DiagnosticContainer.getAllDiagnostics()` deliberately does **not** aggregate them. Anything that expected codec diagnostics to surface through the tree needs an explicit walk.
- **Provider SPI:** `CodecAspectProvider implements MetadataHandler` with a single `onPackageRegistered(PackageMetadata)`, invoked once per model version before publication, mutating the real tree. The old per-element builders, `getAspectTypeId()`, and `buildProfiles(filteredCopy)` with its copy-and-filter guarantee are gone. The profile is the content of the package-level entry; per-class profiles sit inside `CodecPackageProfile.getClassProfiles()`.
- **Lookup:** `CodecResource` keys package metadata by `EPackage`, not nsURI — that overload resolves or builds, so prior registration is no longer a precondition (`requirePackageRegistered` → `ensurePackageMetadata`). The nsURI overload still exists but is explicitly best-effort (most recently registered version).
- **Optionals:** the new API returns `Optional` where the old one returned nullable, and `List` where it returned `EList`.
- **Non-OSGi bootstrap:** `MetadataServiceFactory.create()` keeps its signature and delegates to `MetadataServices.createWhiteboard()` (emf.osgi #66/#67). No component artifact is on any `-buildpath`; the codec manifest imports only the metadata and model packages. The implementation bundle is on the `-testpath` once workspace-wide in `cnf/build.bnd`, because `createWhiteboard()` reads the default `FingerprintService` from `FingerprintHelper` and a flat test classpath has to carry it. In OSGi it arrives via DS.
- **`EMFModelInfo` retired** in `codec.rest`: the JAX-RS root-type lookup uses `MetadataIndexReader.findAllByInstanceClassName`, which the default `MapBasedMetadataIndex` builds from `EClass.getInstanceClassName()` — the same property `EMFModelInfoImpl` indexed. Ambiguity is now an error naming both candidates by nsURI and fingerprint, where the old `Map<Class, EClassifier>` silently kept the last registration.

Verification: `./gradlew build --rerun-tasks` → 130 tasks, 3530 tests, 0 failures; CI green on Java 21 and 25. Warnings went from 117 to 3 (`org.apiguardian.api` added to the `codec.tests` buildpath — javac stops after 100 warnings, so that noise had been hiding two real deprecation sites).

Still open: `org.eclipse.fennec.emf.osgi.model.info` remains a runbundle in `required.bndrun` and `playground/launch.bndrun` although nothing imports it; `fennecEMFModels` and `fennecM2X` still pin the pre-1.1.0 `emf.osgi` artifacts (harmless — 0.1.2 does not export the fingerprint packages, so a resolve fails cleanly).

**Session Summary (2026-07-30):**

**Fixed GitHub issues #93 + #94 — codec.rest: EObject writer re-parents a resource-less object and doesn't restore it on serialization failure:**

Sibling of model.atlas #161. `EObjectMessageBodyHandler.writeTo` added the live resource-less EObject to a temporary response resource (`http://test.test`) and removed it again with plain code after the write — a failing serialization skipped the cleanup, leaving the object's `eResource()` pointing at the temp resource forever (all later hrefs computed as `http://test.test#...`). First fix attempt (matching model.atlas) serialized an `EcoreUtil.copy(t)`, but that regressed (issue #94): `EcoreUtil.Copier` requires many-features to return `Setting`-implementing lists, and models generated with **suppressed notifications** back them with `BasicInternalEList` (not a `Setting`) — `BasicEObjectImpl.eSetting` then throws `ClassCastException`, turning every response into a 500. Final fix: the null-resource branch still attaches the live object, but detaches it (remove from contents + remove temp resource from the per-request `ResourceSet`) in a `finally` — the transient re-parenting during the write is accepted. In `BaseJakartaCodecMessageBodyReaderWriter.writeResourceTo`, the reference resource is now only added to the `ResourceSet` in the branch that actually uses it (was: unconditional add plus a duplicate add — the discarded instance leaked into the per-request `ResourceSet` on the same-class path) and is removed in a `finally` around `save`. Regression tests: `EObjectMessageBodyHandlerTest` (rest bundle unit tests) — failure-path restore, detach after successful write, ResourceSet left clean, foreign-resource-type copy path, and a suppressed-notifications mimic (custom `EObjectImpl` whose many-ref is a `BasicInternalEList`, created via a custom `EFactory` so the Copier repro is faithful).

**Fixed GitHub issue #43 — OpenAPI import: schema-to-schema `$ref` features in the generated schemas EPackage had `eType == null`:**

In the embedded/direct-definitions mode (OpenAPI `components/schemas` content handed to `EPackageValueReader` → `converter.convert(jsonNode, null)`, so `schemaFeature == null`), `extractSchemaNameFromRef` left refs like `#/components/schemas/Category` as `components/schemas/Category`, while `classifierMap` holds `Category` — every deferred lookup missed and the `EReference` stayed untyped (ref only preserved as the `jsonschema`/`ref` EAnnotation). Fix in `JsonSchemaToEPackageConverter`: new `DEFINITION_CONTAINER_PREFIXES` (`components/schemas/`, `definitions/`, `$defs/`, `schemas/`, longest first) stripped as fallback when the `schemaFeature` prefix doesn't match. Deliberately prefix-stripping instead of last-path-segment so organizational-namespace keys (`configs/Name`) keep working. All `$ref` call sites go through this method, so array `items.$ref`, allOf, anyOf/oneOf and `additionalProperties` refs are covered too. Also stopped stamping `noTypeInfo=true` on `$ref` properties in `addCommonAnnotations` (a `$ref` schema has no `type` keyword by design); this also fixed a latent round-trip issue where a `{type: array, items: {$ref}}` feature inherited `noTypeInfo` from its items node and the writer then suppressed `"type": "array"`. Regression test: `OpenApiSchemasRefETypeReproTest` (was a disabled reproducer in the working tree).

**Fixed GitHub issue #44 — OpenAPI security requirements lost their scheme names:**

`{"api_key": []}` deserialized to an empty `SecurityRequirement`: in OpenAPI JSON the requirement object IS the map (dynamic scheme names as field names), while the model wraps entries in the `schemes` EMap feature — no `schemes` field exists in JSON for the generic deserializer to match. Fix following the established `OperationValueReader` pattern:
- `org.eclipse.fennec.codec.openapi/src/.../SecurityRequirementValueReader.java` — new `ReferenceValueReader<SecurityRequirement>` (name `securityRequirement`), reads each JSON field as scheme→scopes EMap entry; `@Component(service = CodecValueReader.class)` for OSGi.
- `org.eclipse.fennec.codec.openapi/src/.../SecurityRequirementValueWriter.java` — mirror `ReferenceValueWriter` (same name, separate registry map) writing `{ "<scheme>": [scopes...] }` so `save()` emits the OpenAPI shape.
- `OpenApiResourceFactoryImpl` plain-Java constructor registers both.
- `org.eclipse.fennec.openapi.model/model/openapi_v3.ecore` — `valueReaderName`/`valueWriterName = securityRequirement` codec EAnnotations on `OpenAPI.security` and `Operation.security`; model regenerated via `./gradlew :org.eclipse.fennec.openapi.model:generate --rerun-tasks` (the task considers itself UP-TO-DATE after ecore-only edits — force the rerun).
- Tests: `OpenApiSecurityRequirementReproTest` enabled (global + per-operation + round-trip), `OpenApiSecurityTest.deserializesGlobalSecurityRequirements` sharpened to assert scheme names and scopes (it previously only asserted list size, which is how the data loss slipped through).

**Fixed GitHub issue #45 — runtime value-binding options cleaned up (implement the unique part, remove/deprecate the redundant part):**

Analysis result (validated against code + git history): of the spec-13 §4 "runtime value binding" option family, only fragments were implemented, all on the attribute side. Decision (with Mark): keep exactly one runtime mechanism and deprecate/remove the redundant ones.

- **Implemented:** `CODEC_FEATURE_VALUE_READER_INSTANCES`/`_WRITER_INSTANCES` now work for **references** too (previously attributes only). `ReferenceDeserializationEntry`: new `resolveEffectiveReferenceReader(...)` (instance from context attr, `canHandle`-checked, else the pre-resolved config/annotation reader) replacing the dead "Priority 1" name lookup and the `resolveRuntimeValueReader` placeholder — whose premise ("requires registry access enhancement") was outdated, the registry was reachable via `entryContext.getValueRegistry()` all along; branch conditions (`isContainment() || referenceReader != null`) now use `hasCustomReferenceReader(ctxt)` so runtime-bound readers also apply to non-containment references. `ReferenceSerializationEntry`: mirror `resolveEffectiveReferenceWriter(...)`. TDD tests: `CodecResourceCustomValueTest.ReferenceInstanceBindingTests` — 7 tests covering single/multi containment read (per element), non-containment read, single/multi containment write (per element), `canHandle`-rejection fallback to default, and instance-beats-config-bound-registry-reader priority (config binding via `CODEC_EREFERENCE_CONFIG` + `CODEC_VALUE_READER_NAME`).
- **Removed (never functional, no consumers, no tests — born as documentation-only constants in commit f2e21d1):** `CODEC_VALUE_READERS`/`CODEC_VALUE_WRITERS` (per-load registry registration, spec-13 §4.1) — constants deleted from `CodecOptions`, entries removed from `CodecResource.KNOWN_RUNTIME_OPTIONS`.
- **Deprecated (functional for attributes + tested, but redundant with the general config resolution):** `CODEC_FEATURE_VALUE_READERS`/`_WRITERS` (names per feature) — `@Deprecated` with pointer to the `"ClassName.featureName"` → `valueReaderName`/`valueWriterName` config path (spec 02) and to instances. Attribute code paths and their tests stay until actual removal. On references a binding now produces an explicit deprecation warning diagnostic instead of silence.
- **Explicitly documented as not implemented** (spec 13 §4.3 + user docs `codec-options-reference.md`): per-load registration of readers/writers that model annotations (`valueReaderName`) resolve — they must be in the registry when the resource is created.
- **Docs updated:** spec `13-load-save-options.md` (§4 restructured, §6 priority table/flow now instances-based, §7.1 example, summary table), `14-custom-values.md` (§10.2), `docs/codec-options-reference.md` (runtime table + not-supported note).

Both issues carried downstream workarounds in `eclipse-fennec/emf.util` (`OpenApiImporter.repairDanglingRefs(...)`, own `SecurityRequirementValueReader` + manual registry assembly) that can be removed once these fixes are released.

---

**Session Summary (2026-07-02):**

**New feature: JSON Schema validation keywords compiled to OCL invariants (Phase 2 + Phase 4 of the EMF/OCL mapping guide):**

`JsonSchemaToEPackageConverter` already parsed and preserved every assertion keyword (`minLength`, `maxLength`, `pattern`, `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`, `uniqueItems`) and `format` as inert `EAnnotation` details under `AnnotationSources.JSONSCHEMA`, but nothing ever turned them into an enforceable constraint. Added an opt-in load option that compiles these into real OCL invariants, wired using EMF's standard validation-delegate annotation convention — the delegate URI itself is configurable, so it works against classic Eclipse OCL, its Pivot dialect, or Data In Motion's own `/opt/git/emf.m2x` OCL engine, without fennec-codec ever taking a compile/runtime dependency on any OCL implementation.

*Design doc:* `docs/OCL-Constraint-Generation-Implementation-Plan.md` — full rationale, including why `minItems`/`maxItems` are out of scope (already enforced structurally via `lowerBound`/`upperBound`) and why Phase 4's `discriminator` keyword is **not** implemented: every discriminated-union pattern in this converter (`discriminatorKey`/`discriminatedUnion`, `commonBase`/`variant`, `multiType`/`variantIndex`) discriminates structurally — the concrete `EClass` itself is the type tag — and never retains a literal discriminator field on the object to assert against, so the guide's `self.oclIsTypeOf(Dog) implies self.type = 'Dog'` pattern has nothing real to check here.

*How constraints become enforceable (not just decorative):* three annotations, per EMF's standard convention (confirmed against `emf.m2x`'s `OclValidationDelegateFactory` + its `ocl-user-guide.md` §9): (1) `EPackage` annotation, source `EcorePackage.eNS_URI`, detail `validationDelegates` = the delegate URI; (2) `EClass` annotation, same source, detail `constraints` = space-separated invariant names; (3) `EClass` annotation, source = the delegate URI, with `invariantName -> oclExpression` details. Whichever `EValidator.ValidationDelegate` is registered for that URI at runtime does the evaluating.

*New options (`CodecJsonSchemaOptions`):*
- `OPTION_GENERATE_OCL_CONSTRAINTS` (`"codec.jsonschema.generateOclConstraints"`, boolean, default `false`)
- `OPTION_OCL_DELEGATE_URI` (`"codec.jsonschema.oclDelegateUri"`, String, default `DEFAULT_OCL_DELEGATE_URI` = the Eclipse OCL Pivot URI, which `emf.m2x` also serves as an alias)

*Files changed:*
- `org.eclipse.fennec.codec.jsonschema/src/.../converter/ocl/JsonSchemaOclConstraintGenerator.java` — new. Post-processing pass, decoupled from `JsonSchemaToEPackageConverter`; only reads the `JSONSCHEMA`-source annotations already written. Invariant naming: `<featureName>_<keyword>` (e.g. `age_minimum`). `multipleOf` requires an integer-typed feature (OCL `mod()` is integer-only); `exclusiveMinimum`/`exclusiveMaximum` require the Draft 2020-12 numeric form (legacy Draft-04 boolean form is skipped); `uniqueItems` requires a multi-valued feature; `format` is limited to `uuid`/`email` via a small extensible regex table — unrecognized formats are silently left alone. Unmappable combinations are skipped with a `JsonSchemaConversionDiagnostic` warning, not a hard failure.
- `org.eclipse.fennec.codec.jsonschema/src/.../converter/JsonSchemaConversionDiagnostic.java` — `Code.OCL_GENERATION_SKIPPED` + `oclGenerationSkipped(keyword, location, reason)` factory
- `org.eclipse.fennec.codec.jsonschema/src/.../constants/CodecJsonSchemaOptions.java` — the two new option constants
- `org.eclipse.fennec.codec.jsonschema/src/.../JsonSchemaResourceImpl.java` — `doLoad` invokes the generator when the option is set, merges its diagnostics into `getWarnings()`
- `docs/codec-options-reference.md` — new option rows under "JSON Schema"

*Tests added:*
- `org.eclipse.fennec.codec.jsonschema/test/.../converter/ocl/JsonSchemaOclConstraintGenerationTest.java` — unit tests on the generator (each keyword, escaping, integer/multi-valued type guards, custom delegate URI, merge-with-existing-annotations behavior, EClass-with-no-owning-EPackage diagnostic) plus end-to-end tests through `JsonSchemaResourceImpl` (option off by default, option on, custom delegate URI). All green.

*Out of scope:* automated end-to-end tests that install `emf.m2x`'s engine and evaluate real objects (fennec-codec has zero Gradle references to `emf.m2x`; the manual playground live-test below covers this need for now). Also out of scope: OCL generation for JSON Schema logical/conditional applicators (`if`/`then`/`else`, `allOf`/`anyOf`/`oneOf` — mapping guide §3), which was explicitly not requested this session.

**Follow-up: playground live-test bench + a real escaping bug found and fixed by it.**

For future sessions: to manually re-verify OCL constraint generation against a live engine, use the playground's `JsonschemaOCLResource` (`org.eclipse.fennec.codec.playground`) rather than writing a new test bench — `POST /jsonschema-ocl/schema` then `POST /jsonschema-ocl/validate`, details below. After the escaping fix, confirmed end-to-end against the running m2x engine: a schema with `minLength`/`minimum`/`multipleOf`/`format:email` correctly produced `ERROR` diagnostics for `name_minLength`, `age_minimum`, and `quantity_multipleOf` on an invalid instance, with no parser errors.

Added `JsonschemaOCLResource` in `org.eclipse.fennec.codec.playground` — a manual test bench for the feature above, exercised via the running OSGi framework (`launch.bndrun`), not JUnit:
- `POST /jsonschema-ocl/schema` — loads a JSON Schema body with `OPTION_GENERATE_OCL_CONSTRAINTS=true` (delegate URI = m2x's native `http://www.eclipse.org/fennec/m2x/ocl/1.0`) via the DS-injected `ResourceSet`, registers the resulting `EPackage` in `EPackage.Registry.INSTANCE` (keyed by its `$id`-derived nsURI — global static registry, needed because JAX-RS resources here are `PROTOTYPE`-scoped and don't survive between requests), and returns `{"nsUri": ..., "types": {"ClassName": "<nsUri>#//ClassName", ...}}`.
- `POST /jsonschema-ocl/validate` — takes the instance body directly as an `EObject` parameter (JAX-RS dispatches to `EObjectMessageBodyHandler`, already wired via `org.eclipse.fennec.codec.rest`), so no manual resource handling or type parameters are needed at all — the instance JSON just needs a `"_type": "<nsUri>#//ClassName"` field, which the codec's existing URI type-strategy resolves straight through the same `EPackage.Registry.INSTANCE` (`TypeResolutionHelper.resolveFromUri`, `TypeDeserializationEntry`). Runs `Diagnostician.INSTANCE.validate(instance)` (confirmed against `/opt/git/model.atlas`'s `ValidationServiceImpl.validate()` as the correct, minimal way to trigger delegate-based OCL validation) and hand-builds the diagnostic tree as JSON (no generic POJO JSON provider exists in this playground — only `EObject` bodies are handled).
- `bnd.bnd` needed `org.eclipse.fennec.codec.jsonschema` added to `-buildpath` (it was already runtime-only via `launch.bndrun`).

Live-testing this immediately found a real bug that the unit tests couldn't catch (they never invoke an actual OCL parser): a `format: email` invariant failed with `OCL parse error: token recognition error at: ''^[^@\\s'`. Root cause, confirmed against `emf.m2x`'s `Ocl.g4:290-296` grammar: its `STRING_LITERAL` rule only recognizes a fixed backslash-escape whitelist (`\\`, `\'`, `\"`, `\n`, `\t`, `\r`, `\f`, `\b`, `\xHH`, `\uHHHH`, octal) with **no doubled-quote (`''`) escape** — the OMG OCL/SQL convention `oclStringLiteral()` had originally assumed. Fixed: escape backslashes as `\\` first, then quotes as `\'` (order matters). Also upgraded `unquote()` from naive quote-stripping to a real Jackson `readTree` parse (falling back to the raw value when it isn't valid JSON, for the unquoted `format` value) — needed so a `pattern` containing a JSON-escaped backslash doesn't come out still-escaped and get double-escaped by the fix above. Full writeup: `docs/OCL-Constraint-Generation-Implementation-Plan.md` §"String literal escaping (found via live testing)". Caveat noted there: this escaping is m2x-specific; a real Eclipse OCL/Pivot delegate expects the doubled-quote convention instead.

*Files changed:* `org.eclipse.fennec.codec.playground/bnd.bnd`, `.../playground/jsonschema/JsonschemaOCLResource.java` (new); `JsonSchemaOclConstraintGenerator.java` (`unquote()`, `oclStringLiteral()` fixes); `JsonSchemaOclConstraintGenerationTest.java` (fixed `patternWithQuoteIsEscaped` expectation, added `patternWithBackslashIsEscaped` and backslash-escaping assertions on the format tests).

---

**Session Summary (2026-06-30 latest):**

**New feature: `flatten` annotation for EMap containment references:**

LLM API gateways (LiteLLM, Bifrost) accept provider-specific extra parameters as flat top-level keys in an OpenAI-compatible request body. Modeling these in EMF as an `EMap<String, String>` containment reference caused the codec to wrap them under the feature name (`"extraParameters": { "k": "v" }`), which those APIs reject. No existing hook could suppress the feature key — `CodecValueWriter` / `ReferenceValueWriter` are both called after `gen.writeName(key)` has already fired.

Added a `flatten` flag: when set on a containment `EReference` whose type is an EMap entry class (`instanceTypeName = "java.util.Map$Entry"`), the serializer omits the feature key entirely and writes each map entry's key/value pair directly into the enclosing JSON object.

*Files changed:*
- `org.eclipse.fennec.codec.api/src/.../config/ConfigProperty.java` — `FLATTEN` entry (FEATURE scope, WRITE, default `false`)
- `org.eclipse.fennec.codec.api/src/.../config/FeatureConfig.java` — `flatten` field, `isFlatten()`, builder setter, `toBuilder()`, `mergeWith()`
- `org.eclipse.fennec.codec.api/src/.../constants/CodecOptions.java` — `CODEC_FLATTEN = "codec.flatten"`
- `org.eclipse.fennec.codec/src/.../ser/ReferenceSerializationEntry.java` — early-return flatten path in `serialize()`; new `serializeFlattenedEMap()` method (same as `serializeEMap()` without the object wrapper)

*How to use:* Pass via `CODEC_EREFERENCE_CONFIG` save option:
```java
Map<EReference, Map<String, Object>> refConfig = new HashMap<>();
refConfig.put(extraParametersRef, Map.of("flatten", "true"));
options.put("codec.eReferenceConfig", refConfig);
resource.save(out, options);
```
Annotation-based config (`flatten=true` in the EAnnotation detail map) works automatically through the existing `ConfigurationResolver` / `mergeWith()` pipeline.

*Constraint:* `flatten` is silently ignored on non-EMap references or single-valued references — only many-valued EMap containment references are affected.

*Tests added:*
- `org.eclipse.fennec.codec.osgi.tests/EMapExample.flattenedMapSerialization()` — verifies entries appear at root level with no container key, using runtime `CODEC_EREFERENCE_CONFIG` option

*Out of scope (tracked separately):* The reverse operation — routing unknown flat keys back into an EMap containment reference during deserialization.

---

**Session Summary (2026-06-26 latest):**

**Issue #16 — OpenAPI post-processing eliminated; `schemasPackage` now round-trips via codec annotations:**

The old approach populated `Components.schemasPackage` via a `doLoad()` override that called `JsonSchemaToEPackageConverter.convertFromSchemaMap(schemas)` after normal deserialization. This meant two redundant representations (`schemas` EMap and `schemasPackage` EPackage) and a manual post-processing step.

Replaced with codec annotation-driven round-trip:
- `schemas` EReference annotated `@Codec(ignore=true)` — codec bypasses it entirely.
- `schemasPackage` EReference annotated `@Codec(key="schemas", valueReaderName="jsonSchemaToEPackage", valueWriterName="ePackageToOpenApiSchemas")` — codec reads/writes the `schemas` JSON key directly via value reader/writer.

Two new `@Component` services registered for automatic OSGi pickup:
- `OperationValueReader` — promoted from manually-instantiated to `@Component(service = CodecValueReader.class)`.
- `OpenApiSchemasValueWriter` — new class wrapping `EPackageValueWriter("definitions", true)` with name `"ePackageToOpenApiSchemas"`. The `embedInFeature=true` flag extracts just the flat `{"Pet": {...}}` map rather than the full `{"definitions": {"Pet": {...}}}` document.

`OpenApiResourceImpl` no longer creates its own `CodecValueRegistry`. `OpenApiResourceFactoryImpl` now injects `CodecValueRegistry` via OSGi DS (`@Reference`) and passes it through; the no-arg standalone constructor builds a local registry registering `OperationValueReader`, `EPackageValueReader`, and `OpenApiSchemasValueWriter` explicitly.

Side-fix: `EPackageToJsonSchemaConverter.writeSingleValuedReference` had a latent NPE when an EReference's type was null (unresolved cross-`$ref` in complex schemas like Kubernetes API). This was never triggered before because the converter was never called on save for OpenAPI resources. Added null guard — emits `{}` (open schema) for unresolved types.

*Files changed:*
- `org.eclipse.fennec.codec.openapi/src/.../OpenApiResourceImpl.java` — removed `createValueRegistry()` and `doLoad()` override; constructor now takes `CodecValueRegistry`
- `org.eclipse.fennec.codec.openapi/src/.../OpenApiResourceFactoryImpl.java` — injects `CodecValueRegistry` via DS; no-arg constructor builds local registry
- `org.eclipse.fennec.codec.openapi/src/.../OperationValueReader.java` — added `@Component(service = CodecValueReader.class)`
- `org.eclipse.fennec.codec.openapi/src/.../OpenApiSchemasValueWriter.java` — new class
- `org.eclipse.fennec.openapi.model/model/openapi_v3.ecore` — `schemas` gets `ignore=true`; `schemasPackage` gets `key="schemas"`, `valueReaderName="jsonSchemaToEPackage"`, `valueWriterName="ePackageToOpenApiSchemas"`
- `org.eclipse.fennec.codec.jsonschema/src/.../EPackageToJsonSchemaConverter.java` — null guard in `writeSingleValuedReference`
- `org.eclipse.fennec.codec.openapi/test/.../OpenApiSchemaTest.java` — `ComponentSchemas` and `SchemaRoundTrip` tests rewritten to assert on `getSchemasPackage()` / EClass
- `org.eclipse.fennec.codec.openapi/test/.../OpenApiRefHandlingTest.java` — removed `CompositionWithRef` nested class (tests used `getSchemas()` EMap)
- `org.eclipse.fennec.codec.openapi/test/.../OpenApiSchemaDefaultTest.java` — removed (all tests used `getSchemas()` chain to `Schema.default`)

---

**Verified issue #11 — enum deserialization with unknown values no longer crashes:**

Issue #11 reported a `NullPointerException` ("Cannot invoke `EEnumLiteral.getInstance()` because `literal` is null") when deserializing JSON containing an enum value not present in the EMF model. Investigation showed that `convertEnumFromString` and `convertEnumFromInteger` in `AttributeDeserializationEntry` already have null guards on the lookup result and return `null` for unknown values; `deserializeSingleValued` then skips the `eSet`, leaving the attribute at its default. The fix was already in place — no code change required.

Added regression tests to prevent the crash from being reintroduced:
- `EnumSerializationTest.UnknownEnumValues` (3 tests): unknown string value, unknown integer value, unknown value in multi-valued array — all assert no exception, other fields still loaded, and known values preserved.

---

**Fix issue #13 — `getGlobalProperty()` ignores `codec.`-prefixed keys:**

`ConfigurationResolver.getGlobalProperty()` looked up property values using only the short key (e.g. `"fieldOrder"`) via a raw `Map.containsKey()` call. If a caller passed the `codec.`-prefixed form (e.g. `Map.of("codec.fieldOrder", "ALPHABETICAL")`), as the `getKey()` javadoc says to do, the value was silently not found and the property fell back to its default.

This affected every caller of `getGlobalProperty()` in `CodecResource.createObjectMapper()`: `FIELD_ORDER`, `SMART_COMPRESSION`, `DATE_FORMAT`, `IGNORE_FEATURES`, `USE_NAMES_FROM_EXTENDED_METADATA`, and all `EXPAND_*` properties.

The inconsistency was that `ConfigMergeHelper.getValue()` — used by every `TypeConfig.mergeWith()` / `FeatureConfig.mergeWith()` call — already tried both forms. `getGlobalProperty()` was the only path that did not.

*Fix:* Replace the manual `source.containsKey(key)` loop in `getGlobalProperty()` with `ConfigMergeHelper.getValue(source, property)`, which checks both the short key and the prefixed key.

*Files changed:*
- `org.eclipse.fennec.codec.api/src/.../config/ConfigurationResolver.java` — `getGlobalProperty()` now uses `ConfigMergeHelper.getValue()`

*Tests added:*
- `ConfigurationResolverTest.GetGlobalProperty` (5 tests): short key found, prefixed key found, short key wins when both present, default returned when absent, prefixed key found across all five config sources.

---

**Fix issue #31 — `fieldOrder=ALPHABETICAL` not honored in JSON/Jackson serialization:**

`ConfigProperty.FIELD_ORDER` was declared and used by tabular exporters (`TabularDocumentBuilder`) but never read in the Jackson serialization path. `CodecResource.createObjectMapper()` did not extract it from the `ConfigurationResolver`, so `CodecModule` always built with `sortPropertiesAlphabetically=false`, making alphabetical ordering effectively dead in JSON/YAML/BSON/CBOR.

*Fix:* Two lines added in `CodecResource.createObjectMapper()` — read `ConfigProperty.FIELD_ORDER` from the operation resolver (which already includes save/load options via `enrichWithOptions`) and map `"ALPHABETICAL"` to the boolean passed to `CodecModule.Builder.sortPropertiesAlphabetically()`. The existing sorting logic in `CodecEObjectSerializer.applyOrdering()` was already correct; it just was never triggered.

*Files changed:*
- `org.eclipse.fennec.codec/src/.../resource/CodecResource.java` — extract `FIELD_ORDER` + wire `sortPropertiesAlphabetically` into module builder

*Tests added:*
- `FormatDelegateJsonRoundTripTest.FieldOrder` — `alphabeticalFieldOrderSortsKeysAlphabetically` (was failing, now passes) + `defaultFieldOrderPreservesDeclarationOrder` (control test)

---

**OSGi `@RequireCodec*` meta-annotations and `@Capability` declarations for all format bundles:**

Added the OSGi resolver wiring pattern used by gecko (`emf.configurator` namespace) to every codec format bundle, so consumers can declare their format dependencies declaratively rather than spelling out raw `Require-Capability` headers.

*Pattern* — for each format bundle, three artefacts are created:
1. `annotation/RequireCodec<Format>.java` — a `@Retention(CLASS)` `@Target({TYPE, PACKAGE})` meta-annotation carrying `@Requirement(namespace="emf.configurator", name="RESOURCE_FACTORY", filter="(emf.configuratorName=FennecCodec<Format>)")`. Consumers place this on their `package-info.java` or type.
2. `annotation/package-info.java` — exports the new annotation package (`@Export @Version("1.0.0")`).
3. Main package's `package-info.java` — adds `@Capability(namespace="emf.configurator", name="RESOURCE_FACTORY", attribute="emf.configuratorName=FennecCodec<Format>", version="1.0.0")` so bnd embeds the matching `Provide-Capability` header in the bundle manifest.

*Bundles covered and capability names:*

| Bundle | Annotation | Capability name |
|--------|-----------|-----------------|
| `org.eclipse.fennec.codec.rest` | `@RequireCodecMessageBodyReaderWriter` | `fennec.codec.rest` ns, `messagebody` name |
| `org.eclipse.fennec.codec` | `@RequireCodecJson` | `FennecCodecJson` |
| `org.eclipse.fennec.codec.yaml` | `@RequireCodecYaml` | `FennecCodecYaml` |
| `org.eclipse.fennec.codec.bson` | `@RequireCodecBson` | `FennecCodecBson` |
| `org.eclipse.fennec.codec.cbor` | `@RequireCodecCbor` | `FennecCodecCbor` |
| `org.eclipse.fennec.codec.csv` | `@RequireCodecCsv` | `FennecCodecCsv` |
| `org.eclipse.fennec.codec.ods` | `@RequireCodecOds` | `FennecCodecOds` |
| `org.eclipse.fennec.codec.xlsx` | `@RequireCodecXlsx` | `FennecCodecXlsx` |
| `org.eclipse.fennec.codec.rlang` | `@RequireCodecRLang` | `FennecCodecRLang` |
| `org.eclipse.fennec.codec.jsonschema` | `@RequireCodecJsonSchema` (in `v2.annotation`) | `FennecCodecJsonSchema` |

*Note:* The `codec.rest` bundle uses its own `fennec.codec.rest` namespace (capability `messagebody`) rather than `emf.configurator`, because `@RequireCodecMessageBodyReaderWriter` requires the JAX-RS message body reader/writer component, not a resource factory.

The `@Capability` for jsonschema goes on `v2/package-info.java` because `JsonSchemaResourceFactoryImpl` lives in `org.eclipse.fennec.codec.jsonschema.v2`; the annotation class lives in `v2.annotation`.

---

**Fix: `CODEC_FEATURE_VALUE_WRITERS` name-based binding silently ignored; tabular path now supports custom value writers:**

Two related issues fixed, both in the `CODEC_FEATURE_VALUE_WRITERS` / `CODEC_FEATURE_VALUE_READERS` option paths.

*Issue 1 — Name-based binding ignored in Jackson path (JSON/YAML/BSON/CBOR):*
`ContextHelper.getFeatureValueWriter()` and `getFeatureValueReader()` existed but were never called inside `resolveEffectiveWriter()` / `resolveEffectiveReader()`. Passing `CODEC_FEATURE_VALUE_WRITERS = Map.of(attr, "myWriterName")` silently fell through to the default serializer.

Fix: added Priority 2 (name lookup from `CodecValueRegistry`) in both `AttributeSerializationEntry` and `AttributeDeserializationEntry`, between Priority 1 (instance binding via `CODEC_FEATURE_VALUE_WRITER_INSTANCES`) and Priority 3 (annotation-resolved writer name).

*Issue 2 — Custom value writers silently ignored in tabular path (CSV/ODS/XLSX/R-Lang):*
`TabularDocumentBuilder` builds typed `Cell` objects directly from `EObject.eGet()` values without going through `AttributeSerializationEntry`, so `CODEC_FEATURE_VALUE_WRITERS` had no effect.

Fix:
- Added `CodecOptions.INTERNAL_VALUE_REGISTRY` (in `codec.api`) to carry the `CodecValueRegistry` through `effectiveOptions` from `CodecResource` to `TabularDocumentBuilder`, avoiding a circular dependency (tabular depends on codec; codec must not depend on tabular).
- Threaded `registry` + `featureWriters` map through the entire tabular build chain (`buildIgnore`, `buildFlat`, `buildSqlTables`, `makeAttributeCell`).
- Added `invokeWriterAsCell()` + `MinimalWriterContext` — invokes the writer through a temporary `JsonMapper` generator, captures the output as bytes, reads it back via a `JsonParser`, and maps the token type to the appropriate `Cell` subtype (String/Long/Double/Boolean).

*TCK coverage extended:*
Added `writerResolvedByNameFromRegistry()` and `readerResolvedByNameFromRegistry()` to `AbstractCustomValueTCK`. YAML, CBOR, and BSON automatically inherit both tests.

*Files changed:*
- `org.eclipse.fennec.codec.api/src/.../constants/CodecOptions.java` — `INTERNAL_VALUE_REGISTRY`
- `org.eclipse.fennec.codec/src/.../ser/AttributeSerializationEntry.java` — Priority 2 in `resolveEffectiveWriter()`
- `org.eclipse.fennec.codec/src/.../deser/AttributeDeserializationEntry.java` — Priority 2 in `resolveEffectiveReader()`
- `org.eclipse.fennec.codec/src/.../resource/CodecResource.java` — inject registry into `effectiveOptions`
- `org.eclipse.fennec.codec.tabular/src/.../TabularDocumentBuilder.java` — `invokeWriterAsCell()`, `MinimalWriterContext`, registry/featureWriters threading
- `org.eclipse.fennec.codec.tests/src/.../tck/AbstractCustomValueTCK.java` — 2 new name-binding TCK tests + `createResource(CodecValueRegistry)` helper
- `org.eclipse.fennec.codec/test/.../CodecResourceCustomValueTest.java` — `NameBindingTests` nested class (2 tests)

---

**Session Summary (2026-06-25 latest):**

**`idOnTop` bug fix + EIDAttribute feature promotion (JSON/YAML path):**

Fixed two related issues in `CodecEObjectSerializer` so that `idOnTop` behaves consistently across all formats (JSON, YAML, BSON, CBOR) and matches the tabular exporter behaviour.

*Bug 1 — Default field order was wrong:*
`buildSerializationEntries()` added the `_id` entry first (before `_type`/`_supertype`), making the default output `{"_id":…,"_type":…}` instead of the spec-mandated `{"_type":…,"_id":…}` (§8.7, `idOnTop=false`). Fixed by reordering entry construction: type → supertype → id → features.

*Bug 2 — `idOnTop=true` did not float the EIDAttribute feature:*
The tabular exporters' `idOnTop` floats the EClass's `getEIDAttribute()` column to front. The JSON path only floated the synthetic `_id` key; the actual `eID=true` attribute (e.g. `personId`) stayed in its natural declaration position. Fixed by extending `applyOrdering()` with an `EClass` parameter: it now resolves the EIDAttribute's JSON key via `config.resolveFeatureConfig(eClass.getEIDAttribute())` and floats it alongside `_id`. Handles both the plain-`idOnTop` and `sortAlphabetically+idOnTop` cases.

*Files changed:*
- `org.eclipse.fennec.codec/src/.../ser/CodecEObjectSerializer.java` — reordered `buildSerializationEntries()`, updated `applyOrdering()` signature + implementation
- `org.eclipse.fennec.codec/test/.../resource/CodecResourceIdTest.java` — added `IdOnTopOrderingTests` nested class (5 tests: default order, `idOnTop` via resolver, `idOnTop` as save option, EIDAttribute feature to front, EIDAttribute feature before `_type`)

*Spec updated:* `docs/codec-v2-spec/09-id.md` §8.7 (serialization order table + flow diagram).

---

**Session Summary (2026-06-10 latest):**

**JSON Schema round-trip gap test + end-user round-trip fidelity docs:**

Added `JsonSchemaRoundTripComparisonIntegrationTest` (in
`org.eclipse.fennec.codec.jsonschema.tests`) — round-trips the bundled sample schemas
(`core-ir.schema.json`, `mapping.schema.json`) **schema → EPackage → schema** through the
registered `application/schema+json` resource and runs a *semantic* JSON-Schema diff against the
original to surface gaps in the reverse (`EPackage → JSON Schema`) path.

- The differ normalizes cosmetic, non-semantic differences so only real structural losses remain:
  `allOf` inheritance is flattened (merging `$ref` parents + inline members), the
  `additionalProperties: false` closed-object policy is ignored when the source is silent, and
  documentation/identity keywords (`description`/`title`/`$id`/`$schema`/`$anchor`), `x-*` vendor
  extensions, and `["string","null"]` nullability decoration are ignored.
- Remaining gaps are asserted against a **documented baseline** (`EXPECTED_CORE_IR_GAPS`,
  `EXPECTED_MAPPING_GAPS`), grouped by four causes: (1) synthetic classifiers (inline enums,
  `…MapEntry`, anonymous map value types) leak into `$defs`; (2) `additionalProperties` maps
  round-trip as `type:array` and drop from `required`; (3) `oneOf` unions are not reconstructed
  (abstract base loses `oneOf`; `MappingField` variants flattened); (4) `$ref` collections lose
  `type:array` + `required` membership. Green today; a **closed** gap ⇒ shrink the baseline, a
  **new** gap ⇒ flagged as a regression.
- These groups match the "deliberately not handled" scope of the
  `jsonschema-to-ecore-conversion` work; closing them would require schema-side `x-` hints
  (e.g. `x-containment`, an enum-name hint, a `MappingField`-collapse marker).
- End-user documentation added: new **"Round-Trip Fidelity (schema → EPackage → schema)"** section
  in `org.eclipse.fennec.codec.jsonschema/jsonschema-architecture.md` — a what-survives /
  what-doesn't table with the implication that an exported schema is semantically equivalent for
  instance data but structurally different, so the original `.schema.json` should remain the source
  of truth.
- Full `:org.eclipse.fennec.codec.jsonschema.tests:testOSGi` green (50 tests).

**Session Summary (2026-06-04 latest):**

**ODS: clickable FK hyperlinks (FK → target sheet), parity with XLSX:**

Closes the long-deferred "ODS hyperlinks" item. In `SQL_TABLES` mode, an `FkCell` now becomes a
clickable link to the first row of its target sheet (`#<target-sheet>.A1`), so e.g. clicking
`address_id` on a `Person` sheet jumps to the `Address` sheet — matching what XLSX already did.

- Depends on the vendored sods fork carrying [PR #63](https://github.com/miachm/SODS/pull/63)
  (`LinkedValue` + `Range.addLinkedValue`), rebased onto **1.10.0** and dropped into
  `cnf/local/com.github.miachm.sods/`. Fork source: `/opt/git/my-SODS` (branch `linked-values`).
- `CodecOdsOptions.OPTION_GENERATE_LINKS` (`"codec.ods.generateLinks"`), **default `true`** — mirrors
  `CodecXlsxOptions.OPTION_GENERATE_LINKS`, and added to `OdsOverridableCodecOptions`'s REST
  client-override whitelist (parity with XLSX).
- `OdsRenderer` reworked into a **two-pass** design (create every `Sheet` first, then fill) so an FK
  can resolve a link to a target table visited later — same approach as `XlsxRenderer`. Link target
  resolved via `FkCell.getTargetEClass()` → `Map<EClass, Sheet>`.
- **Value-type caveat:** the sods writer drops a cell's value once a link is attached, so a linked FK
  carries its id as the link's *display text* and is a **string** cell (XLSX keeps it numeric +
  hyperlink). With `generateLinks=false` the FK stays numeric (prior behavior).
- **Tests:** `OdsRendererTest` — FK-link tests assert on the raw `content.xml` because the sods
  *reader* does not parse links back (only the writer emits them); the two pre-existing structural
  SQL_TABLES tests now pass `generateLinks=false` to keep asserting numeric FKs.

**Session Summary (2026-06-02):**

**REST: client-overridable codec options (whitelisted, via `Codec-Options` header):**

Clients can now override a whitelisted subset of codec load/save options per request, without an
endpoint annotation. Full design + as-built in `docs/codec-rest-client-overridable-options.md`.

- `RestOverridableCodecOptions` SPI in `codec.api` (`Map<String,Class<?>> overridableKeys()`); each
  module registers a `@Component` listing its safe keys via its own constants (no magic strings, no
  jakarta dependency in format bundles). Contributors: `CoreOverridableCodecOptions` (in `codec`),
  `Csv/Ods/Xlsx/RLangOverridableCodecOptions`.
- Single `ClientCodecOptionsFilter` in `codec.rest` collects the whitelist services, parses the
  `Codec-Options` request header (`key=value`, comma-separated), keeps only whitelisted keys, and
  sets the `JakartaRestConstants.CLIENT_CODEC_OPTIONS` request property.
- `BaseJakartaCodecMessageBodyReaderWriter.getClientCodecOptions()` merges them after
  `handleAnnotedOptions` (client wins) on both read and write. Value parsing shared with the
  annotation path via `CodecOptionValues.parse`.
- Secure by default (empty whitelist ⇒ ignored). Risky keys (expand, typeStrategy, value writers)
  deliberately not contributed. Tests: `ClientCodecOptionsFilterTest`.
- ~~Follow-up noted separately: `FIELD_ORDER`/alphabetical ordering is still unwired in the Jackson
  serialization path (the tabular exporters honor it; JSON does not yet).~~ **Fixed 2026-06-26 (issue #31).**

**CSV: dataTypeInSecondRow option to toggle the SQL-type row:**

Added `CodecCsvOptions.OPTION_DATA_TYPE_IN_SECOND_ROW` (`"codec.csv.dataTypeInSecondRow"`),
**default `true`** (keeps current behavior). When `false`, `CsvRenderer` emits only the header row
followed by data rows (no SQL-type second row) — applies to single-CSV (IGNORE/FLAT) and every
per-table + join-table CSV in SQL_TABLES. Resolved in `CsvRenderer.resolveTypeRow(options)` and
threaded through `writeTable`/`writeJoinTableToZipEntry`. Tests: `CsvWriterTest.TypeRowToggle`.

**Tabular exporters: honor idOnTop + fieldOrder=ALPHABETICAL column ordering:**

`TabularDocumentBuilder` now applies column ordering across IGNORE/FLAT/SQL_TABLES (CSV/ODS/XLSX/R):
- `fieldOrder=ALPHABETICAL` codec option (default `DECLARATION`) sorts columns by header
  (case-insensitive). Read from the codec options map via `ConfigProperty.FIELD_ORDER.getKey()`
  (also accepts `codec.fieldOrder`).
- `idOnTop` codec option floats the EClass's `getEIDAttribute()` column to first; resolved through
  `resolver.resolveIdConfig(eClass).isOnTop()`. In SQL_TABLES the synthetic `_id` PK stays first and
  the eID attribute floats ahead of the other own columns.
- Shared helpers `orderColumns(...)`, `resolveAlphabetical(opts)`, `resolveIdOnTop(eClass, …)` in
  `TabularDocumentBuilder`. Tests in `TabularDocumentBuilderOptionTest`.
- Scope: exporters only. ~~NOTE/follow-up: `FIELD_ORDER` is declared but **not wired into the Jackson
  serialization path** anywhere (`sortPropertiesAlphabetically` is never set from options) — open a
  separate issue to wire alphabetical ordering into JSON/Jackson for full consistency.~~ **Fixed 2026-06-26 (issue #31).**

**Unify tabular export on one path; honor value gate + enumSerialization everywhere:**

CSV IGNORE mode previously used a separate Jackson path (`CsvFormatDelegate`) while FLAT/SQL_TABLES
and all ODS/XLSX/R modes used the shared `TabularDocumentDelegate → TabularDocumentBuilder →
<renderer>` pipeline. The Jackson path honored the value gate and `enumSerialization`; the tabular
path silently skipped them — so the same object exported as CSV-IGNORE vs ODS-IGNORE diverged.

- `FeatureConfig.shouldSerializeValue(value, defaultValue, manyEmpty)` — extracted the value gate
  (serializeNull/Empty/Default) into one shared method; `AttributeSerializationEntry.shouldSerialize`
  now calls it (single source of truth, no drift).
- `TabularDocumentBuilder` — applies the value gate in `buildIgnore`/`buildFlat`/`buildSqlTables`:
  a column appears iff at least one row has a qualifying value (matches the Jackson/`CsvValueHandlingTest`
  contract); gated-out rows get `EmptyCell`. Now honors `enumSerialization` (LITERAL/NAME/VALUE) for
  EMF `Enumerator` and Java `Enum` in `makeScalarCell`/`stringifyScalar`.
- `CsvFormatProvider` — all reference modes now flow through `TabularDocumentDelegate + CsvRenderer`;
  the IGNORE special case is gone. `CsvFormatDelegate` deleted (unreferenced).
- Behavior change: ODS/XLSX/R IGNORE are now option-aware (null/default/empty columns drop unless the
  matching `serialize*` option is set) — the intended consistency fix.
- Out of scope (follow-up): id/type strategy (`_id`/`_type` columns) for the tabular grid.
- ~~Custom value writers (`CodecValueWriter`) on the tabular path~~ — **Fixed 2026-06-26**: `CODEC_FEATURE_VALUE_WRITERS` is now supported in `TabularDocumentBuilder` via `invokeWriterAsCell` + `MinimalWriterContext`; see §7.3.
- Tests: `CsvEnumSerializationTest` (CSV enum strategies), `TabularDocumentBuilderOptionTest`
  (format-agnostic value gate + enums); updated `TabularDocumentBuilderCellTypingTest`'s null-cell
  test to keep the column via `serializeNull(true)`. Full `./gradlew build` green.

---

**Session Summary (2026-03-31):**

**JSON Schema Nullable Type Support (Issue 3 fix):**

Added nullable type handling for JSON Schema ↔ EPackage conversion, fixing the known Issue 3 where `"type": ["string", "null"]` created 3 artificial classes instead of a simple nullable EAttribute.

- `EPackageToJsonSchemaConverter.writeSingleValuedAttribute()` — optional single-valued EAttributes (`lowerBound=0`) now emit `"type": ["<type>", "null"]` instead of `"type": "<type>"`. Applies to primitive data types and date/time types. Optional EEnum attributes emit `"default": "<first literal>"` instead of nullable type arrays, preserving the standard enum schema structure.
- `JsonSchemaToEPackageConverter.createTypedFeature()` — two-element type arrays with `"null"` (e.g., `["string", "null"]` or `["null", "integer"]`) now create a simple `EAttribute(lowerBound=0)` via the new `extractNullableType()` method, instead of delegating to `handleMultiTypeProperty()`.
- `JsonSchemaToEPackageConverter.createEEnum()` — when a `"default"` value is present, the default literal is reordered to be the first literal (value 0) in the EEnum, ensuring it becomes the EMF default.
- Type arrays with 3+ elements or two non-null types still produce the artificial class hierarchy as before.
- Tests: `NewFeaturesTest.NullableTypeTests` — 11 tests (read nullable string/integer/null-first, three-type array fallback, read enum with/without default, write nullable string/integer/boolean, write default for optional enum, roundtrip)
- Documentation: `jsonschema-architecture.md` updated with new "Nullable Types" section

---

**Session Summary (2026-03-18):**

**DateFormat ConfigProperty — Full Stack Implementation:**

Added `dateFormat` as a new ConfigProperty across the entire codec stack, enabling per-feature, per-EClass, and global date format configuration via SimpleDateFormat patterns.

- `ConfigProperty.DATE_FORMAT` — new enum entry (`String`, default `null`, levels: GLOBAL/ECLASS/FEATURE, directions: READ/WRITE)
- `CodecOptions.CODEC_DATE_FORMAT` — new constant (`"codec.dateFormat"`)
- `FeatureConfig` — added `dateFormat` field, accessor, builder method, merge support (short + prefixed key)
- `EffectiveCodecConfig` (API interface) — added `getDateFormat()` default method returning `null`
- `EffectiveCodecConfig` (runtime impl) — added `dateFormat` field, constructor wiring, builder, `@Override getDateFormat()`
- `CodecModule` — added `dateFormat` field, wired into both `createEffectiveConfig()` methods
- `CodecResource.createObjectMapper()` — extracts `DATE_FORMAT` from `ConfigurationResolver` and passes to `CodecModule.Builder`
- `AttributeSerializationEntry.writeValue()` — new Date branch delegates to `writeDateValue()` using configured format
- `AttributeDeserializationEntry.convertObjectFromString()` — checks configured format before falling back to ISO patterns
- Ecore model: added `dateFormat` attribute to `FeatureCodecAspect` in `codec.ecore` + `codec.genmodel`, regenerated src-gen
- `CodecAspectProvider` — parses `dateFormat` from EAnnotation details (EAttribute only)
- `AspectToPropertiesConverter` — converts `dateFormat` aspect to property map
- `CodecAnnotationConstants.KEY_DATE_FORMAT` — new constant
- Tests: `ConfigPropertyTest`, `FeatureConfigTest`, `EffectiveCodecConfigTest`, `FeatureConfigResolverSpecTest` (6 resolution tests)

**REST Bundle Migration (`org.eclipse.fennec.codec.rest`):**

Migrated the REST bundle annotations from legacy EMFJs constants to the new CodecOptions-based system.

- `@CodecConfig` annotation — complete rewrite mapping all `CodecOptions` constants, organized into sections:
  Feature (dateFormat, serialize*, enum, valueReader/WriterName), Type (strategy, format, key, include, nameKey, schemaKey),
  ID (strategy, format, key, valueKey, keyMode, onTop), Reference (format, key, typeKey),
  SuperType (serialize, key, strategy), Global (smartCompression). Fixed typo: `idStartegy` → `idStrategy`.
- `@RootElement` annotation — added `rootType()` (EClass URI) and `rootSchema()` (EPackage namespace URI) fields,
  mapping to `CodecResource.CODEC_ROOT_TYPE` and `CodecResource.CODEC_ROOT_SCHEMA` (legacy keys, by design).
- `CodecAnnotationConverter` — completed `convertCodecConfig()` method converting all `@CodecConfig` fields to `CodecOptions` entries.
  String keys (default `""`) use `putIfNotBlank`; booleans and string enums always written.
- `CodecOptions.CODEC_SMART_COMPRESSION` — new constant (`"codec.smartCompression"`) added to support `@CodecConfig.smartCompression()`.
- `bnd.bnd` — added `codec.api` and `codec` to `-buildpath`, plus `jakarta.ws.rs-api`, `model.metadata`, `emf.osgi.model.info`, `jakartars`.
- Tests: `CodecAnnotationConverterTest` with `AnnotationHelper` (annotation proxy builder for tests).
  Test sections: canHandle, RootElement conversion, Feature/Type/ID/Reference/SuperType/Global configuration, Defaults, Full Custom.

**Known Gap: Jackson-Specific Features Not Configurable via Load/Save Options:**

There is currently no way for users to configure Jackson-specific serialization/deserialization features
(e.g., `SerializationFeature.INDENT_OUTPUT`, `DeserializationFeature.*`, `MapperFeature.*`, `StreamWriteFeature.*`)
through the codec's load/save option maps. The `JsonMapper.Builder` in `CodecResource.createObjectMapper()` is either
provided externally via the constructor or created fresh internally — but its feature configuration is not exposed
through the options system.

The only current workaround is to pass a pre-configured `JsonMapper.Builder` when constructing the `CodecResource`
programmatically, which requires direct access to resource creation and is not available to REST endpoint users
or users who rely on `ResourceFactory`-based resource creation.

This means features like pretty-printing (`INDENT_OUTPUT`), lenient parsing, or other Jackson behaviors cannot be
toggled per-request via load/save options. Each such feature would need to be explicitly added as a `ConfigProperty`
and wired into `createObjectMapper()`, as was done for `dateFormat`. The only pretty-print option in the codebase
is `CodecJsonSchemaOptions.OPTION_PRETTY_PRINT` (`"codec.jsonschema.pretty.print"`) in the jsonschema module,
which is not part of the core codec.

**Decision (2026-03-18):** Leave this as-is for now — to be discussed with the team before adding any Jackson
feature passthrough mechanism.

---

**Session Summary (2026-03-16):**

**Security Hardening — S-2 through S-6 (continued from previous session):**

Systematic security fixes based on `docs/codec-security-analysis.md`. S-1 was already fixed. This session implemented S-2 through S-6.

**S-2: Nesting Depth Protection** (done in earlier session, included for completeness)
- `MAX_NESTING_DEPTH=200` depth counter added to all recursive value-reading chains
- `CodecEObjectDeserializer`: `readCurrentValue()`, `readObjectAsMap()`, `readArrayAsList()` — deferred properties path
- `AttributeDeserializationEntry`: `readAnyJsonValue()`, `readJsonObjectAsMap()`, `readJsonArrayAsCollection()`, `readJsonObjectToString()`, `readJsonArrayToString()`, `readArrayValue()`, `readNestedArray()` — attribute path
- Recovery: `parser.skipChildren()` + return null + warning diagnostic (keeps parser consistent)
- Test: `NestingDepthProtectionTest` — 9 tests

**S-3: Collection Size Guard** (done in earlier session, included for completeness)
- `MAX_COLLECTION_SIZE=100,000` with `limitExceeded` flag pattern
- Applied to both `CodecEObjectDeserializer` and `AttributeDeserializationEntry`
- Recovery: warning emitted once, remaining elements skipped via `skipChildren()`
- Test: `CollectionSizeProtectionTest` — 7 tests

**S-4: Type Resolution Scoping** ✅ NEW
- `TypeResolutionHelper.resolveFromSimpleName(String, EPackage)` — scoped to context package only
- `TypeResolutionHelper.resolveFromClassName(String, EPackage)` — scoped to context package only
- `TypeResolutionHelper.resolveFromNumeric()` — global scan fallback removed
- `TypeDeserializationEntry.resolveEClass()` — derives context package from context schema URI or hint EClass; adds warning diagnostic when resolution fails due to missing context
- Also fixes S-9 (Numeric Classifier ID Ambiguity) — same root cause
- Updated existing tests: `TypeDeserializationEntryTest` NAME strategy tests now pass hint, `TypeResolutionHelperTest.resolvesByIdWithoutHint` expects null
- Test: `TypeResolutionScopingTest` — 11 tests
- Spec updates: `06-type.md` (context schema table, CLASS resolution flow, NUMERIC requirement, type resolution rules), `15-error-handling.md` (§9.3)

**S-5: Reflection Allowlist** ✅ NEW
- `SAFE_REFLECTION_TARGETS` (13 types) in `AttributeDeserializationEntry`
- `convertObjectFromString()` checks allowlist before any reflection; rejected types throw `IllegalArgumentException`, caught by caller as warning diagnostic
- `BigDecimal`, `BigInteger`, `UUID`, `Date` handled by direct code paths before the allowlist check
- Allowlist: `URI`, `URL`, + 11 `java.time` types
- Test: `ReflectionAllowlistTest` — 6 tests (was 7, adjusted after removing directly-handled types from set)
- Spec updates: `15-error-handling.md` (§9.5)

**S-6: Jackson StreamReadConstraints** ✅ NEW
- `STREAM_READ_CONSTRAINTS` constant in `CodecResource`: nesting 500 (backstop above codec's 200), strings 10 MB, field names 10 KB
- Applied to all three parsing paths: `CodecJsonFactory` builder, format provider load IOContext, format provider save IOContext
- Nesting depth set to 500 (not 200) so the codec's own `MAX_NESTING_DEPTH` (200) triggers first with graceful recovery; Jackson acts as hard backstop
- Test: `StreamReadConstraintsTest` — 6 tests
- Spec updates: `15-error-handling.md` (§9.6)

*Files changed (S-4):*
- `org.eclipse.fennec.codec/src/.../util/TypeResolutionHelper.java` — scoped overloads, removed global scan fallbacks
- `org.eclipse.fennec.codec/src/.../deser/TypeDeserializationEntry.java` — context package derivation, warning diagnostic
- `org.eclipse.fennec.codec/test/.../deser/TypeResolutionScopingTest.java` — new (11 tests)
- `org.eclipse.fennec.codec/test/.../deser/TypeDeserializationEntryTest.java` — updated NAME tests
- `org.eclipse.fennec.codec/test/.../util/TypeResolutionHelperTest.java` — updated resolvesByIdWithoutHint

*Files changed (S-5):*
- `org.eclipse.fennec.codec/src/.../deser/AttributeDeserializationEntry.java` — `SAFE_REFLECTION_TARGETS`, allowlist check in `convertObjectFromString()`
- `org.eclipse.fennec.codec/test/.../deser/ReflectionAllowlistTest.java` — new (6 tests)

*Files changed (S-6):*
- `org.eclipse.fennec.codec/src/.../resource/CodecResource.java` — `STREAM_READ_CONSTRAINTS`, applied to all parsing paths
- `org.eclipse.fennec.codec/test/.../resource/StreamReadConstraintsTest.java` — new (6 tests)

*Documentation updated:*
- `docs/codec-security-analysis.md` — S-4, S-5, S-6, S-9 marked as fixed; mitigation plan, configurable limits, design mitigations, BSI mapping, embedder guide (§6.4, §6.9, §6.10, §6.8), testing section all updated
- `docs/codec-v2-spec/06-type.md` — context schema table, CLASS resolution flow, NUMERIC requirement, type resolution rules
- `docs/codec-v2-spec/15-error-handling.md` — §9.3 (Type Resolution Scoping), §9.5 (Reflection Allowlist), §9.6 (StreamReadConstraints)

**Next steps:** Security issues S-7 through S-11 remain (S-7 YAML tags safe by default, S-8 CBOR indefinite-length, S-10 URI scheme validation, S-11 custom value handler audit). S-12 through S-15 are accepted risks.

---

**Session Summary (2026-03-10 previous):**

**JSON Schema Inline Refs Option (`OPTION_INLINE_REFS`):**

Added `CodecJsonSchemaOptions.OPTION_INLINE_REFS` (`"codec.jsonschema.inlineRefs"`) — when `true`, all `$ref` references are replaced with inlined object definitions, and the `$defs`/`definitions` section is omitted entirely. This is required for APIs (e.g., some AI structured-output endpoints) that do not accept JSON Schema with `$ref` references.

*Implementation details:*
- **Containment & non-containment references:** Both single-valued and multi-valued references inline the full class definition at the reference site instead of writing `{"$ref": "..."}`.
- **Abstract type references:** `oneOf`/`anyOf` arrays contain inlined concrete subclass definitions instead of `$ref` entries.
- **Inheritance flattening:** When `inlineRefs` is enabled, `allOf` with `$ref` to parent definitions is automatically flattened (same as `flatAllOf`), since parent `$ref` would be unresolvable without `$defs`.
- **Cycle detection:** A stack-based guard (`inlineStack`) detects self-referencing types (e.g., `TreeNode` with `children: TreeNode[]`) and breaks recursion by emitting `{"type": "object"}` for the cyclic reference.
- **$defs omission:** Both `writePackage()` (EPackage mode) and `convertEClass()` (single-class mode) skip writing the definitions section.

*Files changed:*
- `CodecJsonSchemaOptions.java` — added `OPTION_INLINE_REFS` constant
- `EPackageToJsonSchemaConverter.java` — added `isInlineRefs()`, `writeSubclassRefsOrInline()`, `writeInlinedRefOrCycleGuard()` methods; modified `writeSingleValuedReference()`, `writeMultiValuedReference()`, `writeObjectClass()`, `writePackage()`, `convertEClass()`

*Tests added:*
- `NewFeaturesTest.InlineRefsTests` (new `@Nested` class): `inlinesContainmentReference()`, `inlinesNonContainmentReference()`, `inlinesMultiValuedContainmentReference()`, `inlinesAbstractTypeWithOneOf()`, `handlesCircularReferences()`, `flattensInheritance()`, `packageConverterOmitsDefinitions()`, `inlinesSingleValuedAbstractNonContainment()`

*Documentation:* Updated `jsonschema-architecture.md` — added `OPTION_INLINE_REFS` to options table, added "Inline Refs" section.

**ReferenceValueWriter/Reader honoured for non-containment references:**

When a `ReferenceValueWriter` (or `ReferenceValueReader`) is configured via `valueWriterName` / `valueReaderName` EAnnotation on a non-containment reference, it is now used for inline serialization/deserialization — the same as for containment references. Previously, the custom handler was resolved and stored but silently ignored at runtime because the dispatch logic only checked `reference.isContainment()`.

*Serialization fix (`ReferenceSerializationEntry.java`):*
- Added a new branch in `serializeReference()`: after the containment path but before `shouldExpandReference()`, check if `referenceWriter != null`. If so, delegate to the custom writer even for non-containment references.

*Deserialization fix (`ReferenceDeserializationEntry.java`):*
- In `deserializeSingleValued()`, `deserializeMultiValued()`, and `deserializeSingleElement()`: changed the condition from `reference.isContainment()` to `reference.isContainment() || referenceReader != null`. When a `ReferenceValueReader` is configured, the inline JSON object is deserialized through it instead of the non-containment `$ref` path.

*Rename:* `containmentWriter` → `referenceWriter`, `containmentReader` → `referenceReader` (internal fields only, no API impact). Updated related comments.

*Spec updated:* `14-custom-values.md` — §1.2 use-case table, §4.1 type table, §4.2 heading/description, serialization flow diagram now reflect that `ReferenceValueWriter`/`ReferenceValueReader` work for both containment and non-containment.

*Tests:* `JsonSchemaValueHolderIntegrationTest.serializationNonContainedWithInheritance()` — verifies non-containment EReference with `valueWriterName="eClassToJsonSchema"` produces inline JSON Schema (with `$defs`, `$ref`, etc.) instead of a default URI reference object.

---

**Session Summary (2026-03-05 latest):**

**JSON Schema Vendor Extensions & Reference Handling Overhaul:**

Added `x-*` vendor extension properties to JSON Schema converters for lossless EMF round-trips, and changed containment reference serialization from inline objects to `$ref`.

*Change 1 — Vendor extensions for class metadata (`x-abstract`, `x-interface`):*
- **Root cause:** Abstract and interface flags on EClasses were lost during JSON Schema round-trip. The serializer never wrote them; the deserializer only inferred abstractness from `oneOf`/`anyOf` patterns (discriminated unions).
- **Fix (serialization):** `EPackageToJsonSchemaConverter.writeEClassContent()` now writes `"x-abstract": true` when `eClass.isAbstract()` and `"x-interface": true` when `eClass.isInterface()`.
- **Fix (deserialization):** `JsonSchemaToEPackageConverter.createEClass()` and `createClassWithAllOf()` read `x-abstract` and `x-interface` and set the corresponding flags. Interface also implies abstract in EMF.
- **Files:** `EPackageToJsonSchemaConverter.java`, `JsonSchemaToEPackageConverter.java`, `JsonSchemaKeywords.java`

*Change 2 — Containment references use `$ref` instead of inlining (`x-containment`):*
- **Root cause:** Containment references to concrete types inlined the full class definition inside the property. When the same class was also referenced non-containment (via `$ref` in `$defs`), the deserializer created duplicate artificial classes instead of reusing the existing definition.
- **Fix (serialization):** `writeSingleValuedReference()` and `writeMultiValuedReference()` now use `$ref` + `"x-containment": true` for containment references to concrete types, instead of inlining. Abstract containment also changed from `writeInlinedSubclassDefinitions` to `writeSubclassRefs` (using `$ref` in `oneOf`). `collectReferencedClasses()` now includes containment references in `$defs`.
- **Fix (deserialization):** `createStructuralFeature()` reads `x-containment` from the property node and overrides the default containment flag on `EReference`.
- **Files:** `EPackageToJsonSchemaConverter.java`, `JsonSchemaToEPackageConverter.java`

*Change 3 — `oneOf` with `$ref` entries resolves to common abstract supertype:*
- **Root cause:** When a property had `oneOf` with `$ref` entries pointing to classes sharing a common abstract supertype, `createOneOfFeature()` created new artificial classes via `processOneOf()` instead of resolving to the existing supertype.
- **Fix:** Added `resolveCommonSuperTypeFromRefs()` that checks if all `oneOf` entries are `$ref` to known classes with a shared abstract supertype (checking both resolved `ESuperTypes` and unresolved `allOfRefMap` entries). When found, creates an `EReference` directly to the supertype. Falls back to the original artificial class creation otherwise.
- **Files:** `JsonSchemaToEPackageConverter.java`

*Change 4 — `convertToEClass` now processes `$defs` before root definition:*
- **Root cause:** In the single-class conversion path (`convertToEClass`), containment references now use `$ref` to classes in `$defs`. But `$defs` entries were never processed, so `$ref` resolution failed (referenced class not in `classifierMap`).
- **Fix:** `convertToEClass()` now processes `$defs`/`definitions`/`schemas` entries before the root schema definition, populating `classifierMap` first. Also sets `schemaFeature` so `extractSchemaNameFromRef()` can strip the definitions prefix correctly.
- **Files:** `JsonSchemaToEPackageConverter.java`

*Tests added:*
- `JsonSchemaResourceTest.RoundTripTests`: `epackageWithInterface()`, `epackageWithNonContainmentReference()`, `epackageWithMixedReferences()`, `epackageWithAbstractContainmentReference()`
- `JsonSchemaResourceTest.EClassRoundTripTests` (new `@Nested` class): `simpleEClass()`, `eClassWithContainmentReference()`, `eClassWithNonContainmentReference()`, `eClassWithMixedReferences()`, `eClassWithMultiValuedContainment()`
- Updated `NewFeaturesTest`: `multiValuedContainmentRef_hasRefItemsWithXContainment()`, `concreteTypeRef_unchanged()`, `containmentAbstractRef_usesOneOfWithInlinedDefs()` — all updated to expect `$ref` + `x-containment` instead of inlined objects.

*Change 5 — `OPTION_SUPPRESS_VENDOR_EXTENSIONS` option:*
- Added `CodecJsonSchemaOptions.OPTION_SUPPRESS_VENDOR_EXTENSIONS` (`"codec.jsonschema.suppressVendorExtensions"`) — when `true`, all `x-*` properties (`x-abstract`, `x-interface`, `x-containment`) are omitted from serialized output. Useful when target APIs/validators reject vendor extensions.
- All `x-*` writes in `EPackageToJsonSchemaConverter` are guarded by `!suppressVendorExtensions`, resolved from options at init time alongside `suppressedKeywords`.
- **Files:** `CodecJsonSchemaOptions.java`, `EPackageToJsonSchemaConverter.java`

*Tests added (suppression):*
- `JsonSchemaResourceTest.SuppressVendorExtensionsTests` (new `@Nested` class): 4 EPackage tests (`epackage_abstractClass_noXAbstract`, `epackage_interfaceClass_noXInterface`, `epackage_containmentRef_noXContainment`, `epackage_multiValuedContainment_noXContainment`) + 2 EClass tests (`eclass_abstractClass_noXAbstract`, `eclass_containmentRef_noXContainment`).

*Documentation:* Updated `jsonschema-architecture.md` — added "Vendor Extensions" section with suppression note, added `OPTION_SUPPRESS_VENDOR_EXTENSIONS` to options table, updated "Array Items" section to reflect `$ref` for containment references.

---

**Session Summary (2026-03-05):**

**Type Serialization Integration Tests & Bug Fixes:**

Created `CodecResourceTypeOptionsTest.java` (`org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/resource/`) — a comprehensive integration test suite exercising type serialization/deserialization through `CodecResource` save/load. The test file has 9 `@Nested` groups: NoneStrategy, NameStrategy, NumericStrategy, SchemaAndTypeStrategy, CustomTypeKey, StructuredFormat, PerClassTypeConfig, TypeScope (`@Disabled`), and PerReferenceTypeConfig. Uses `test-roundtrip.ecore` (Person, Address, Company) and `test-type-strategy.ecore`.

The tests exposed 5 bugs in the configuration and serialization/deserialization layers:

*Bug 1 — Per-EClass config not overriding global type strategy:*
- **Root cause:** `ConfigurationResolver.extractClassProperties()` only looked up `ConfigProperty.ECLASS_CONFIG.getKey()` (`"eClassConfig"`), but options passed via `CodecOptions.CODEC_ECLASS_CONFIG` use the prefixed key `"codec.eClassConfig"`. Same issue for `EREFERENCE_CONFIG` and `EATTRIBUTE_CONFIG` in `extractFeatureProperties()`.
- **Fix:** Added fallback to try `ConfigProperty.*.getPropertyKey()` (prefixed key) when the short key yields no result, in both `extractClassProperties` and `extractFeatureProperties`.
- **Files:** `ConfigurationResolver.java`

*Bug 2 — CODEC_ROOT_SCHEMA load option not working:*
- **Root cause:** `CodecResource.enrichWithOptions()` called `resolver.toBuilder().optionsProperties(options).build()`, which **replaced** the existing optionsProperties (containing typeKey, typeStrategy, etc.) with just the load options (containing only CODEC_ROOT_SCHEMA). All original type configuration was lost on load.
- **Fix:** Added `getOptionsProperties()` getter to `ConfigurationResolver`. Changed `enrichWithOptions` to **merge** existing options with load/save options (load options take precedence via `Map.putAll`).
- **Files:** `ConfigurationResolver.java`, `CodecResource.java`

*Bug 3 — NUMERIC strategy ignoring context schema hint:*
- **Root cause:** `TypeResolutionHelper.resolveFromNumeric(String, EClass)` only used `hintEClass` for EPackage lookup. When no hint class was provided (common case with CODEC_ROOT_SCHEMA), it fell through to scanning all registered packages — which is unreliable. The context schema URI from `CODEC_ROOT_SCHEMA` was never consulted.
- **Fix:** Added overload `resolveFromNumeric(String, EClass, String contextSchemaUri)` that tries: (1) hint class package, (2) context schema package, (3) all registered packages. Updated `TypeDeserializationEntry` NUMERIC case to pass `ContextHelper.getContextSchemaUri(ctxt)`.
- **Files:** `TypeResolutionHelper.java`, `TypeDeserializationEntry.java`

*Bug 4 — STRUCTURED format deserialization dropping all properties:*
- **Root cause:** `CodecEObjectDeserializer.readTypeValueAsString()` returned `null` when encountering `START_OBJECT` (a structured type value like `{"type":"Person","schema":"..."}`), without consuming the nested object. This left the Jackson parser positioned **inside** the type object, causing all subsequent top-level fields (name, age, etc.) to be misinterpreted or skipped.
- **Fix:** Added `readStructuredTypeObject()` method that properly parses the inner object, extracts `type`/`schema`/`classifier` values using configured inner keys, composes full URIs when schema is present (`nsURI#//ClassName`), handles `classifier` via `TypeResolutionHelper.resolveFromNumeric`, and correctly consumes the entire nested object. Also added `VALUE_NUMBER_INT` handling for plain numeric type values.
- **Files:** `CodecEObjectDeserializer.java`

*Bug 5 — Per-reference type config not overriding global:*
- **Root cause:** `resolveTypeConfig(EClass)` only merged GLOBAL and ECLASS level properties. No code path existed to layer feature-scoped properties from `CODEC_EREFERENCE_CONFIG` into type config resolution, even though `typeStrategy` is valid at FEATURE level per `ConfigProperty`.
- **Fix:** Added `resolveTypeConfig(EClass, EStructuralFeature, DiagnosticCollector)` to `ConfigurationResolver` that layers feature overrides on top of EClass-level resolution. Added `resolveTypeConfig(EClass, EStructuralFeature)` to `EffectiveCodecConfig`. Updated `CodecEObjectSerializer.serialize()` to read `ContextHelper.getCurrentSerializationReference(ctxt)` and use the feature-aware overload when a reference context exists.
- **Files:** `ConfigurationResolver.java`, `EffectiveCodecConfig.java`, `CodecEObjectSerializer.java`

---

**Session Summary (2026-03-04):**

**JSON Schema Deserialization Fixes (`JsonSchemaToEPackageConverter`):**

Comparison with gecko-codec's `EnhancedJsonSchemaToEPackageDeserializer` revealed several issues in the JSON Schema → EPackage deserialization. Full findings documented in `org.eclipse.fennec.codec.jsonschema/jsonschema-deserialization-findings.md`.

*Fix 1 — `allOf` inline property merging (data loss):*
- `createClassWithAllOf` only used the first non-`$ref` inline schema, silently dropping properties from subsequent inline schemas
- Now properly iterates ALL `allOf` elements, merges properties with deduplication, and accumulates `required` fields across all inline schemas
- Example: `allOf: [{$ref: Base}, {properties: {street}}, {properties: {city}}]` now correctly produces an EClass with both `street` and `city`

*Fix 2 — `anyOf` property-level reference resolution (class bloat + broken hierarchy):*
- Old approach (`createMultiValueReference` + `createParentFromCommonProperties`) eagerly created artificial parent classes from raw JSON nodes during initial processing, which:
  - Ignored existing common supertypes (e.g., Cat/Dog both extending Animal via `allOf`)
  - Created empty artificial classes when referenced schemas used `allOf` (no top-level `properties`)
  - Used `parentClassMaps` with fragile `Map<String, JsonNode>` cache key
  - Never populated `anyOfRefMap`, leaving artificial parents disconnected from the type hierarchy
- New deferred approach:
  - `createMultiValueReference` now records `DeferredAnyOfReference` entries instead of eagerly resolving
  - `resolveAnyOfPropertyReferences()` runs AFTER `resolveAllOfReferences()` so the full inheritance hierarchy is available
  - Walks the supertype chains to find the **lowest common supertype** via `findLowestCommonSupertype()`
  - If a common supertype exists (e.g., Animal), uses it directly — no artificial class created
  - If no common supertype exists, creates an artificial parent, extracts common features (by name + type) from the target classes into it, removes the originals from target classes, and wires target classes as subtypes
  - Uses sorted target name list as cache key for order-independent reuse across identical `anyOf` sets
- Removed `parentClassMaps` field and `createParentFromCommonProperties` method

*Use cases now covered (with tests in `NewFeaturesTest`):*

| Test | Scenario |
|------|----------|
| `allOfMergesMultipleInlineSchemas` | allOf with 3 inline schemas — all properties merged, required accumulated |
| `allOfMergesInlineSchemasWithRefs` | allOf with $ref parent + 2 inline schemas — supertype resolved, properties merged |
| `allOfDeduplicatesProperties` | allOf with overlapping property names — no duplicate features |
| `anyOfSameRefsReusesType` | Two properties with identical anyOf [Cat, Dog] — same type reused |
| `anyOfCommonPropertiesInDifferentOrderReusesType` | Cat defines {name, age, purrs}, Dog defines {age, name, barks} — common features {name, age} extracted regardless of property order |
| `anyOfResolvesToExistingCommonSupertype` | Cat/Dog extend Animal via allOf — anyOf resolves to Animal, no artificial class |
| `anyOfResolvesToCommonSupertypeDefinedAfterSubtypes` | Animal defined AFTER Cat/Dog in schema — deferred resolution still finds Animal |
| `anyOfNoCommonSupertypeCreatesArtificialParent` | Circle/Rectangle with no shared supertype — artificial parent created, both wired as subtypes |
| `anyOfFindsLowestCommonAncestor` | Animal → Mammal → Cat/Dog — anyOf resolves to Mammal (lowest), not Animal |

*Remaining known issues (documented in findings, not yet fixed):*
- ~~Issue 3: Multi-type `"null"` (e.g., `"type": ["string", "null"]`) creates 3 artificial classes instead of a nullable EAttribute~~ — **Fixed (2026-03-31):** Two-element type arrays with `"null"` now create a simple `EAttribute(lowerBound=0)` instead of artificial classes. Serialization emits `["type", "null"]` for optional single-valued attributes.
- Issue 4: Context-specific variant base class always created even with 0 common properties
- Issue 5: `$ref` default containment is `false` (should be `true` to match typical JSON Schema semantics)

---

**Session Summary (2026-03-02 latest):**

**Custom Properties for Format-Specific Options:**
- Introduced generic `customProperties` map on `EffectiveCodecConfig` — automatically collects all `codec.*` load/save options that don't match known `ConfigProperty` keys or runtime-only options
- Replaced hard-coded `allFieldsRequired`, `useAnchorRefs`, `flatAllOf` boolean fields on `EffectiveCodecConfig` with the generic `customProperties` approach
- Removed `getConverterOptions()` from the API interface in favor of `getCustomProperties()`
- Updated `CodecResource.extractCustomProperties()` — filters options by `codec.*` prefix, excluding known `ConfigProperty` keys and runtime options
- Updated `CodecModule` to pass `customProperties` through to `EffectiveCodecConfig.Builder`
- Value writers (`EClassValueWriter`, `EPackageValueWriter`) now use `ctx.getConfig().getCustomProperties()`

**JSON Schema Option Key Migration:**
- Migrated all JSON Schema option constants in `CodecJsonSchemaOptions` to use `codec.jsonschema.*` prefix:
  - `"useAnchorRefs"` → `"codec.jsonschema.useAnchorRefs"`
  - `"allFieldsRequired"` → `"codec.jsonschema.allFieldsRequired"`
  - `"flatAllOf"` → `"codec.jsonschema.flatAllOf"`
- Added `OPTION_USE_NAMES_FROM_EXTENDED_METADATA` (`"codec.jsonschema.useNamesFromExtendedMetadata"`)
- Added `OPTION_SUPPRESS_KEYWORDS` (`"codec.jsonschema.suppressKeywords"`) — `Collection<String>` of JSON Schema keywords to suppress in output

**JSON Schema Bug Fix — Missing `items` for Arrays:**
- Fixed `EPackageToJsonSchemaConverter.writeMultiValuedAttribute()` — `items` property was only written when a `@jsonschema(items="true")` annotation was present; now always emitted for multi-valued attributes

**JSON Schema Keyword Suppression:**
- `OPTION_SUPPRESS_KEYWORDS` allows suppressing specific JSON Schema keywords (e.g., `maxItems`, `minItems`, `description`, `additionalProperties`, `$comment`, `deprecated`, `writeOnly`, `uniqueItems`, `format`)
- Useful for generating schemas compatible with AI structured-output APIs that don't support certain keywords
- Applied `isSuppressed()` guards across all keyword write sites in `EPackageToJsonSchemaConverter`

**New Tests:**
- `MultiValuedAttributeTests` — 7 tests: string/int/boolean/double/enum arrays, bounded arrays, containment reference arrays
- `SuppressKeywordsTests` — 6 tests: suppress maxItems, minItems, description, additionalProperties, $comment, multiple keywords

**Documentation:**
- Updated `jsonschema-architecture.md` — options table with full `codec.jsonschema.*` keys, custom properties integration section, keyword suppression section, array items section
- Updated `codec-v2-development-guide.md` — session summary, task hierarchy, test counts

**Previous Session Summary (2026-02-25):**

**OSGi Integration:**

- Added `MetadataServiceComponent` in `org.eclipse.fennec.model.metadata` bundle — DS component exposing `MetadataWhiteboard` and `MetadataService` as OSGi services; has `@Reference(MULTIPLE, DYNAMIC)` for `EPackage`, `AspectProvider`, `MetadataIndex` (OPTIONAL), and `MetadataHandler` whiteboard entries

- Added `CodecAspectProviderComponent` in `org.eclipse.fennec.codec.metadata` bundle — DS component extending `CodecAspectProvider`, registered as `AspectProvider` OSGi service; automatically picked up by `MetadataServiceComponent` and applied to all registered EPackages

- Added `org.eclipse.fennec.codec.osgi.tests` bundle with 15 OSGi integration tests mirroring the `org.eclipse.fennec.codec.examples` suite; tests use `@InjectService MetadataService`, `@InjectBundleContext BundleContext`, and `ctx.registerService(EPackage.class, pkg, null)` — no manual `MetadataServiceFactory.create()` or `metadataService.registerPackage(pkg)` calls

- Special pattern for `ExternalTypeDiscriminatorExample`: registers `TypeDiscriminatorService` as `MetadataHandler` OSGi service _before_ the EPackage so that `MetadataServiceComponent.addHandler()` notifies it of package events automatically

**Package restructuring (required for OSGi bndrun resolution and to avoid `ClassNotFoundException` at runtime):**

- Exported package `org.eclipse.fennec.model.metadata.service` in `org.eclipse.fennec.model.metadata` bnd.bnd — it was missing from the exported API, causing the bndrun resolver to fail
- Renamed package `org.eclipse.fennec.codec.value` → `org.eclipse.fennec.codec.value.impl` in the `org.eclipse.fennec.codec` bundle to eliminate split-package conflict with the identically named package in `org.eclipse.fennec.codec.api`
- Moved classes in `org.eclipse.fennec.codec.format` (in the `org.eclipse.fennec.codec` bundle) to `org.eclipse.fennec.codec.format.impl` for the same reason — the public API types (`FormatDelegate`, `FormatReaderDelegate`, `CodecFormatProvider`, `TokenType`) remain in `codec.api`, while the implementation bridge classes (`FormatDelegateGenerator`, `FormatDelegateParser`, `JacksonFormatProvider`) now live in the `.impl` sub-package of the codec bundle

**New bundle — `org.eclipse.fennec.codec.workspace.library`:**

- Provides all codec dependencies as an OSGi library bundle for workspace consumption
- Added Fennec Common Models Library `org.eclipse.fennec.models:org.eclipse.fennec.common.models.library:0.0.1-SNAPSHOT` to be able to use `org.geojson.model` from there
- Removed `org.geojson.model` from local folder (not needed anymore, as we use the one provided by the Fennec Common Models library)

**Jackson Dependency upgrades:**
- Upgraded Jackson from 3.0.2 to 3.1.0

- Upgraded jackson-annotations from 2.20 to 2.21

- Upgraded org.snakeyaml:snakeyaml-engine from 2.10 to 3.0.1

  

**Previous Session Summary (2026-02-24):**

**JSON Schema enhancements:**
- Added `EClassValueReader` / `EClassValueWriter` (embed single-class JSON Schema in other formats)
- Added `EClassToJsonSchemaConverter` / `JsonSchemaToEClassConverter` (thin wrapper converters)
- Refactored `EPackageToJsonSchemaConverter.writeEClass` → `writeEClass` + `writeEClassContent`; added `convertEClass` + `writeEClassDocumentMetadata`
- Added `convertToEClass` to `JsonSchemaToEPackageConverter`; preserves `title` as `originalTitle` annotation for round-trip fidelity
- Added `OPTION_ALL_FIELDS_REQUIRED` option (`"allFieldsRequired"`) — marks every property required, for AI structured-output schemas
- Added `EClassValueHandlerTest` (ReaderTests, WriterTests, RoundTripTests) and `AllFieldsRequiredTests` in `NewFeaturesTest`
- Fixed `withoutOption_onlyMandatoryFeaturesRequired` test: replaced fragile string-position check with Jackson JSON parsing + `requiredContains()` helper
- Created `org.eclipse.fennec.codec.jsonschema/jsonschema-architecture.md` documenting both usage modes, converter classes, all options, annotation mapping, and three open issues (OI-1: CodecResource bypass, OI-2: no OSGi whiteboard, OI-3: no default $schema)

**Previous Session Summary (2026-02-17):**

**Plan F TCK Test Suite — COMPLETE:**

Comprehensive Technology Compatibility Kit (TCK) for all format providers. Three phases of abstract TCK classes verified across BSON, CBOR, and YAML formats.

*TCK Phases:*
- **P0 Core Round-Trip** (Phase 1): Basic attribute types, containment/non-containment references, enums, multi-valued attributes, complex round-trip
- **P1 Feature Strategies** (Phase 2): Type strategy, ID strategy, enum strategy, polymorphism, reference format, value handling, custom key
- **P2 Advanced Features** (Phase 3): EMap, SuperType, visibility, force read/write, global ignore, strictness, array root, large payloads, extended metadata, custom value reader/writer

*Bug Fixes During TCK Development:*
- **Array root serialization** — `CodecResource.doSaveWithFormat()` now handles multiple root objects correctly (iterates individually within array start/end)
- **`CodecFormatProvider.supportsArrayRoot()`** — New capability method; `BsonFormatProvider` returns `false` (single-document format), throws `IOException` on multi-root save
- **BSON reader infinite loop** — `BsonFormatReaderDelegate` now tracks `valueConsumed` flag to skip unconsumed primitive values when `nextToken()` is called without reading. Verified with 11 dedicated tests covering all BSON value types.

**Previous Session Summary (2026-02-16):**

**Plan E Multi-Format Support — COMPLETE:**

All steps implemented and verified. Four format providers operational: JSON (default), BSON, CBOR, YAML.

*Architecture:*
- `FormatDelegate<T>` / `FormatReaderDelegate<S>` — format-agnostic write/read interfaces
- `FormatDelegateGenerator<T>` / `FormatDelegateParser<S>` — Jackson bridge (extends GeneratorBase/ParserBase)
- `JacksonFormatProvider` — handles any Jackson-based streaming format (JSON, CBOR, YAML, Smile)
- `BsonFormatProvider` — handles BSON via in-memory BsonDocument with binary stream conversion
- `CodecResource` — optional `CodecFormatProvider` field; when set, uses FormatDelegate path

*Format Providers:*

| Project | Provider | Type | Tests |
|---------|----------|------|-------|
| `org.eclipse.fennec.codec` | (default JSON, no FormatDelegate) | Jackson native | ~1,231 |
| `org.eclipse.fennec.codec.bson` | `BsonFormatProvider` | In-memory BsonDocument | 98 |
| `org.eclipse.fennec.codec.cbor` | `CborFormatProvider` extends `JacksonFormatProvider` | Binary (CBORFactory) | 53 |
| `org.eclipse.fennec.codec.yaml` | `YamlFormatProvider` extends `JacksonFormatProvider` | Text (YAMLFactory) | 53 |

*Key design decisions:*
- JSON is NOT migrated to FormatDelegate. The original `CodecJsonFactory` → Jackson native path remains the default. FormatDelegate is an additional path, verified to produce identical output.
- `BsonFormatProvider` implements `CodecFormatProvider<InputStream, OutputStream>` (not `<BsonDocument, BsonDocument>`) so it works with CodecResource's stream-based save/load API.
- CBOR and YAML providers are one-class projects — they just extend `JacksonFormatProvider` with the respective factory.
- YAML requires `org.snakeyaml.engine` as transitive dependency of `jackson-dataformat-yaml`.

**Previous Session (2026-02-08):**

**Plan D Verification (Discriminator Refactoring):**
- ✅ **D1 (Remove MAPPED from TypeStrategy)** — DONE, spec `06-type.md` confirms removal
- ✅ **D2 (Inline Mapping for References)** — DONE, fully implemented + `CodecResourceInlineMappingTest.java`
- ✅ **D3 (Property-Based Discriminator Config)** — DONE, documented in spec sections 4.4 and 5.1
- ✅ **D4 (STRUCTURED Format + Discriminator)** — DONE, spec `08-discriminator-mapping.md` §1.4 clarified
  - Discriminator mapping has priority over STRUCTURED format
  - When both are configured, discriminator value goes inside STRUCTURED `_type` object
  - Implementation verified in `TypeSerializationEntry.java` and `TypeDeserializationEntry.java`

**Spec Updates:**
- Added section 1.4 "Interaction with TypeFormat (PLAIN vs STRUCTURED)" to `08-discriminator-mapping.md`
- Updated `codec-v2-plans.md` with Plan D verification results

**Previous Session (2026-02-08):**

**Plan B Phase 3 Progress:**
- ✅ **GAP-011 (Misconfiguration test coverage)** — Already covered with 22 test files
- ✅ **GAP-012 (DeserializationMode usage)** — IMPLEMENTED
  - `ContextHelper`: Added `DESERIALIZATION_MODE` constant + helper methods (`isStrictMode()`, `isLenientMode()`, `isAutoDetectMode()`)
  - `CodecResource`: Wires `CODEC_DESERIALIZATION_MODE` load option to context
  - `CodecEObjectDeserializer`: Type resolution issues ERROR in STRICT, WARNING in LENIENT; unexpected type tokens handled
  - `TypeDeserializationEntry`: `handleTypeResolutionFailure()` uses mode for ERROR vs WARNING
  - `SuperTypeDeserializationEntry`: Validation is opt-in feature (independent of mode); mode controls error handling
  - `DeserializationModeTest.java`: 12 new tests covering LENIENT/STRICT/AUTO_DETECT behavior
- ⏸️ **GAP-013 (Enum-level annotation support)** — POSTPONED per user request

**Key Design Decision (GAP-012):**
- **DeserializationMode** (STRICT/LENIENT/AUTO_DETECT) controls whether errors **break** deserialization or produce warnings
- **SuperType validation** is a separate opt-in feature via `validateSuperTypeHierarchy` flag, independent of DeserializationMode

**Earlier Session (2026-02-08):**

**Plan B Phase 2 COMPLETE — All 5 GAPs Done:**
- ✅ **GAP-008 (Value Reader/Writer handlers)** — Already implemented; added missing `featureValueReaderInstances`/`featureValueWriterInstances` load/save options wiring
  - `ContextHelper`: Added `FEATURE_VALUE_READER_INSTANCES`, `FEATURE_VALUE_WRITER_INSTANCES` constants
  - `CodecResource`: Wired instance options in load/save paths
  - `AttributeDeserializationEntry`/`AttributeSerializationEntry`: Added `resolveEffectiveReader()`/`resolveEffectiveWriter()` for runtime instance binding
  - `CodecResourceCustomValueTest`: Added `InstanceBindingTests` (2 tests)
- ✅ **GAP-009 (Scope wiring for runtime options)** — Implemented EClass/EReference/EAttribute keyed config maps
  - `ConfigProperty`: Added `ECLASS_CONFIG`, `EREFERENCE_CONFIG`, `EATTRIBUTE_CONFIG` scope keys
  - `ConfigurationResolver`: `extractClassProperties()` and `extractFeatureProperties()` support both `Map<EClass,...>` and `Map<String,...>`
  - `ReferenceConfig`: Added `typeKey` and `idKey` per-reference overrides
  - `ConfigurationResolverTest`: Added `ScopeConfigurationTests` (5 tests)
- ✅ **GAP-007 (Expand deserialization)** — Already implemented; verified with `ExpandReferenceTest.java` (18 tests)
- ✅ **GAP-006 (Metadata merge behavior)** — Already implemented; verified with 15 spec test files
- ✅ **GAP-010 (Hierarchy resolution tests)** — Already implemented; verified coverage across TypeResolutionHelper (27), SuperTypeConfig (33), spec tests

**Previous Session (2026-02-08 earlier):**
- Verified GAP-002 (Fallback Strategy) — Already in `TypeDiscriminatorService`
- Implemented GAP-003 (Feature Strictness) — `ClassConfig`, strictOnUnknown/strictOnMissing
- Postponed GAP-004 (Diagnostic Options), GAP-014 (inherit enum) to later release

**Next Session:** Plan E complete. All format providers (BSON, CBOR, YAML) operational. Possible next work: Plan B remaining GAPs, documentation, Smile format, or new features.

---

## 0. Active Task Hierarchy (SESSION CONTINUITY)

This section tracks the current task hierarchy to prevent context loss during nested investigations.

### 0.1 How to Use This Section

**When starting work:** Check this section first to understand where we are.

**When a new issue arises during work:**
1. **ASK:** "Is this a child task (fix now, return to parent) or independent task (add to TODO, continue)?"
2. **If child task:** Add it to the hierarchy below with proper indentation
3. **If independent task:** Add to §5.4 "Remaining Work" or §10.4 "Current TODO List"

**When completing a task:** Mark it ✅ and return to the parent task.

**Format:**
```
MAIN TASK: [description] - [status: ACTIVE/PAUSED/✅]
├── CHILD: [description] - [status]
│   ├── CHILD: [sub-issue] - [status]
│   └── CHILD: [sub-issue] - [status]
└── RETURN TO: [next step after children complete]
```

### 0.2 Current Task Hierarchy

```

COMPLETED: code-quality block #80 - ✅ (2026-08-08, PRs #143, #144, #146)
│  - #58/#59 api-export: 21 components to .internal, format.impl split into format.jackson
│  - #81 per-package view leak; #65 @since dates -> 1.0
│  - #67 -maven-release: local (convention from fennec-model.atlas)
│  - #62/#66/#68 closed with rationale, no code change
│  - Open: #46 release preparation + #145

COMPLETED: ser/deser asymmetry campaign #110 - ✅ (2026-08-07, PRs #125-#141)
│  - #112-#121 all merged and closed; #129/#131/#132 (hang: parser, swallow, loop guards)
│  - #134 STRICT is the umbrella that fails the load; strictOn* are subsets
│  - #124 file round trips through a fresh ResourceSet, both write paths
│  - Diagnostics must reach the resource, never the logger alone
│  - #110 kept open as the bracket; open follow-ups: #83, code-quality block

COMPLETED: Issue #73 Phase B — in-band EPackage fingerprint - ✅ (2026-07-25, PR #77)
│  - Self-describing multi-version documents: the version comes from the data, not the caller
│  - Model: FingerprintMode (NONE|FIRST_TOUCH) + fingerprintKey on TypeSerializationConfig
│  - Write: FingerprintPins, first touch only; a due fingerprint beats smart-compression omission
│  - Read: whole type context collected before resolving (PLAIN needs type AND fingerprint)
│  - K7 break is structural: read keys in a context attribute seeded from caller options only
│  - B.2 full failure matrix (StreamFingerprintOutcome), caller-wins precedence, diagnostic cap
│  - B.3 fingerprint in STRUCTURED reference type objects; reference resolution via PackageResolver
│  - S7 AbstractFingerprintTCK for YAML/CBOR/BSON; column formats suppress the carrier with a warning
│  - Spec: 06 §8, 13 §2.10–§2.12, 10 §1.2.1, 03/02/16 tables; workdoc §8.2a is the decision log
│  - 3514 tests, 0 failures, 3 documented skips. Default OFF, so R1 holds by construction
│  - Follow-ups filed: #75 (EPackage annotation level), #76 (smart compression drops second root)

COMPLETED: Issue #54 Phase A — fingerprint-aware config resolution - ✅ (2026-07-24, PR #74 merged)
│  - A.1 instance-based annotation config, A.2/A.4 codec.rootFingerprint + EPackage root schema
│  - A.3+B.5 binding package-resolution order via per-load PackageResolver, count-based candidate rule
│  - B.6 discriminator registries as per-step composed views

COMPLETED: Custom Properties + JSON Schema Enhancements - ✅ (2026-03-02)
│  - Generic customProperties map on EffectiveCodecConfig (replaces hard-coded format-specific fields)
│  - CodecResource.extractCustomProperties() — auto-collects codec.* options not matching known keys
│  - CodecModule passes customProperties through to EffectiveCodecConfig
│  - JSON Schema option keys migrated to codec.jsonschema.* namespace
│  - Fixed missing array items in EPackageToJsonSchemaConverter
│  - Added OPTION_SUPPRESS_KEYWORDS for keyword suppression
│  - 13 new tests (7 array + 6 suppression)
│  - Updated jsonschema-architecture.md and codec-v2-development-guide.md

COMPLETED: OSGi Facades + Integration Tests + Package Restructuring - ✅ (2026-02-25)
│  - MetadataServiceComponent (org.eclipse.fennec.model.metadata): DS whiteboard for EPackage/AspectProvider/MetadataHandler
│  - CodecAspectProviderComponent (org.eclipse.fennec.codec.metadata): DS AspectProvider service
│  - org.eclipse.fennec.codec.osgi.tests: 15 OSGi integration tests with @InjectService MetadataService
│  - Exported org.eclipse.fennec.model.metadata.service (was missing, blocked bndrun resolver)
│  - Renamed codec.value → codec.value.impl (split-package conflict with codec.api)
│  - Moved codec.format impl classes → codec.format.impl (split-package conflict with codec.api)

COMPLETED: workspace.library bundle - ✅ (2026-02-25)
│  - Added org.eclipse.fennec.codec.workspace.library (OSGi library bundle for codec deps)

COMPLETED: Jackson dependency upgrades - ✅ (2026-02-25)
│  - Jackson 3.0.1 → 3.1.0
│  - jackson-annotations 2.20 → 2.21


COMPLETED: JSON Schema EClass Handlers + allFieldsRequired + docs - ✅ (2026-02-24)
│  - EClassValueReader / EClassValueWriter (embed single-class JSON Schema)
│  - EClassToJsonSchemaConverter / JsonSchemaToEClassConverter (thin wrappers)
│  - EPackageToJsonSchemaConverter: writeEClassContent refactor, convertEClass, OPTION_ALL_FIELDS_REQUIRED
│  - JsonSchemaToEPackageConverter: convertToEClass + originalTitle annotation preservation
│  - EClassValueHandlerTest + AllFieldsRequiredTests; fixed test assertion bug
│  - jsonschema-architecture.md created

COMPLETED: Plan F TCK Test Suite - ✅ (2026-02-17)
│
│  Phase F1 (P0 Core): Abstract round-trip TCKs - ✅
│  │  - 6 suites: attributes, containment refs, non-containment refs, enums, multi-valued, complex
│  Phase F2 (P1 Features): Feature strategy TCKs - ✅
│  │  - 7 suites: type, ID, enum, polymorphism, reference, value handling, custom key
│  Phase F3 (P2 Advanced): Advanced feature TCKs - ✅
│  │  - 10 suites: EMap, SuperType, visibility, force, global ignore, strictness,
│  │    array root, large payload, extended metadata, custom value
│  │  - 8 ecore models + 10 abstract TCK classes + 30 format subclasses
│  Bug Fixes: - ✅
│  │  - Array root serialization in CodecResource.doSaveWithFormat()
│  │  - CodecFormatProvider.supportsArrayRoot() capability method
│  │  - BsonFormatReaderDelegate valueConsumed fix (+ 11 regression tests)
│
COMPLETED: Plan E Multi-Format Support - ✅ (2026-02-16)
│  - BSON: BsonFormatProvider (98 tests)
│  - CBOR: CborFormatProvider (53 tests)
│  - YAML: YamlFormatProvider (53 tests)
│
PREVIOUS: Plan B + Plan D Complete - ✅ (2026-02-08)
PREVIOUS: Integration Tests + PLAIN Reference Format - ✅ (2026-02-06)
> **Older completed tasks available in git history**

```

## 1. Project Goal

**Fennec Codec V2** is a rewrite of the EMF JSON codec with a metadata-driven architecture:

- **Metadata Layer** parses EAnnotations at EPackage registration time
- **Codec Layer** consumes metadata during serialization/deserialization
- **Configuration** supports 5 sources (Options, Resource, Factory, Module, Annotation) merged hierarchically
- **Spec-driven development** — implementation follows `docs/codec-v2-spec/`

## 2. Architecture Overview

### 2.1 Two-Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│ org.eclipse.fennec.codec (Codec Runtime)                   │
│                                                             │
│ ┌─────────────────────┐   ┌───────────────────────────┐   │
│ │ CodecEObjectSerializer├──→ SerializationEntry (Type, │   │
│ │ CodecEObjectDeserializer  │ ID, Feature, Reference)   │   │
│ └─────────────────────┘   └───────────────────────────┘   │
│            │                                               │
│            ▼                                               │
│ ┌─────────────────────────────────────────────────────┐   │
│ │ EffectiveCodecConfig (resolves per-feature config) │   │
│ └─────────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────────┘
                       │ uses metadata
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ org.eclipse.fennec.codec.metadata (Metadata Service)       │
│                                                             │
│ ┌────────────────────┐   ┌──────────────────────────────┐  │
│ │ CodecAspectProvider├──→│ Parses EAnnotations          │  │
│ │ MetadataService     │   │ Creates aspect objects       │  │
│ └────────────────────┘   │ (TypeDiscriminatorService,   │  │
│                           │  ClassConfig, FeatureConfig) │  │
│                           └──────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 Key Components

| Component | Location | Purpose |
|-----------|----------|---------|
| **MetadataService** | `codec.metadata` | EPackage registration, aspect creation |
| **MetadataServiceComponent** | `emf.osgi.metadata` (emf.osgi project) | DS OSGi facade: whiteboard for EPackage/MetadataHandler/MetadataIndex services |
| **CodecAspectProvider** | `codec.metadata.provider` | `MetadataHandler`: parses EAnnotations → aspect entries + profile |
| **CodecAspectProviderComponent** | `codec.metadata.provider` | DS OSGi facade: registers `CodecAspectProvider` as `MetadataHandler` service |
| **TypeDiscriminatorService** | `codec.metadata.type` | Manages discriminator→EClass mappings |
| **EffectiveCodecConfig** | `codec.api.config.effective` | Per-feature config resolution |
| **CodecEObjectSerializer** | `codec.ser` | Orchestrates serialization entries |
| **CodecEObjectDeserializer** | `codec.deser` | Orchestrates deserialization entries |
| **SerializationEntry** | `codec.ser` | Type/ID/Feature/Reference serializers |
| **DeserializationEntry** | `codec.deser` | Type/ID/Feature/Reference deserializers |
| **FormatDelegate\<T\>** | `codec.api.format` | Format-agnostic write interface |
| **FormatReaderDelegate\<S\>** | `codec.api.format` | Format-agnostic read interface |
| **FormatDelegateGenerator\<T\>** | `codec.format.impl` | Jackson bridge: wraps FormatDelegate as JsonGenerator |
| **FormatDelegateParser\<S\>** | `codec.format.impl` | Jackson bridge: wraps FormatReaderDelegate as JsonParser |
| **JacksonFormatProvider** | `codec.format.impl` | Factory for Jackson-based formats (JSON, CBOR, YAML) |
| **BsonFormatProvider** | `codec.bson` | Factory for BSON format (in-memory BsonDocument) |

### 2.3 Configuration Hierarchy (5 Sources)

From highest to lowest priority:

1. **SaveOptions/LoadOptions** (per-operation)
2. **Resource options** (per-resource)
3. **ResourceFactory options** (per-factory)
4. **CodecModule** (global config)
5. **EAnnotation** (model metadata)

See `docs/codec-v2-spec/02-config-resolution.md` for details.

## 3. Current State (2026-03-02)

### 3.1 What Works

✅ **Configuration Layer (metadata.ecore + codec.api)**
- All 6 config types: Type, SuperType, Discriminator, ID, Feature, Reference
- Spec tests complete (54+48+38+78+64+56 = 338 tests)
- Resolver spec tests complete (24+21+17+17+30+23+20 = 152 tests)
- Layer 1 annotation validation (parse-time errors for invalid EAnnotations)
- Config merging (5-source hierarchy with toBuilder() pattern)

✅ **Metadata Service**
- EPackage registration
- Aspect parsing (CodecAspectProvider)
- TypeDiscriminatorService (discriminator value → EClass mapping)
- Diagnostic collection during parsing

✅ **Codec Runtime (fully cleaned up)**
- `org.eclipse.fennec.codec.*` packages (all deprecated `old codec.v2.*` code deleted)
- Serialization entries (Type, ID, Feature, Reference)
- Deserialization entries (Type, ID, Feature, Reference)
- CodecEObjectSerializer/Deserializer orchestrators
- CodecResource + CodecModule
- Jackson integration (JsonParser/JsonGenerator + custom buffer)
- EffectiveCodecConfig (per-feature resolution)
- ConfigurationResolver (replaces old CodecConfiguration)

✅ **Utility Helpers (org.eclipse.fennec.codec.util)**
- `AnnotationHelper` — ExtendedMetaData annotation lookups
- `CodecResourceHelper` — Type resolution, compatibility checks
- `MetadataServiceFactory` — MetadataWhiteboard creation
- `TypeResolutionHelper` — EClass resolution by name, class name, numeric ID, URI (22 tests)
- `EMapHelper` — EMap reference detection, key/value feature access (18 tests)

✅ **Integration Tests**
- 23 resource test files (193 tests) — migrated to new codec.* packages
- 9 ser/deser/type integration tests — migrated to new codec.* packages
- GeoJson, JsonSchema, OpenAPI codecs — migrated to non-deprecated API
- ForceReadWriteTest.java — verifies two-gate model + forceRead/forceWrite

✅ **Discriminator Mapping (Type Mapping Registry)**
- All 19 tests in CodecResourceInlineMappingTest passing
- Deserialization, serialization, ERROR/FALLBACK strategies, root-level, supertype inheritance

✅ **Multi-Format Support (Plan E)**
- FormatDelegate abstraction: `FormatDelegate<T>`, `FormatReaderDelegate<S>`, `CodecFormatProvider<S,T>`
- Jackson bridge: `FormatDelegateGenerator<T>` (extends GeneratorBase), `FormatDelegateParser<S>` (extends ParserBase)
- BSON format: `BsonFormatDelegate`, `BsonFormatReaderDelegate`, `BsonFormatProvider` (98 tests)
- CBOR format: `CborFormatProvider` extends `JacksonFormatProvider` (53 tests)
- YAML format: `YamlFormatProvider` extends `JacksonFormatProvider` (53 tests)
- Feature parity verified: `AbstractFormatFeatureParityTest` + `JsonFormatFeatureParityTest`
- CodecResource: optional `CodecFormatProvider` field, `doSaveWithFormat()`/`doLoadWithFormat()`
- `CodecFormatProvider.supportsArrayRoot()` — capability check (BSON returns false)

✅ **Custom Properties for Format-Specific Options (2026-03-02)**
- `EffectiveCodecConfig.getCustomProperties()` — generic `Map<String, Object>` for format-specific options
- `CodecResource.extractCustomProperties()` — auto-collects `codec.*` options not matching known `ConfigProperty` or runtime keys
- `CodecModule.Builder.customProperties()` — passes through to `EffectiveCodecConfig`
- JSON Schema options use `codec.jsonschema.*` namespace: `allFieldsRequired`, `useAnchorRefs`, `flatAllOf`, `useNamesFromExtendedMetadata`, `suppressKeywords`
- `OPTION_SUPPRESS_KEYWORDS` — suppress specific JSON Schema keywords in output (e.g., `maxItems`, `description`)
- Array `items` property now always emitted for multi-valued attributes (was previously annotation-gated)

✅ **OSGi Integration (2026-02-25)** — ⚠️ superseded by the 2026-07-30 entry: the `AspectProvider` SPI and the codec-side `model.metadata` bundle no longer exist
- `MetadataServiceComponent` — DS component in `org.eclipse.fennec.model.metadata`; exposes `MetadataWhiteboard` and `MetadataService` as OSGi services; whiteboard references: `EPackage` (MULTIPLE, DYNAMIC), `AspectProvider` (MULTIPLE, DYNAMIC), `MetadataIndex` (OPTIONAL), `MetadataHandler` (MULTIPLE, DYNAMIC)
- `CodecAspectProviderComponent` — DS component in `org.eclipse.fennec.codec.metadata`; registered as `AspectProvider` OSGi service; bound automatically by `MetadataServiceComponent`
- `org.eclipse.fennec.codec.osgi.tests` — 15 OSGi integration tests using `@InjectService`/`@InjectBundleContext`; EPackages registered as OSGi services (`ctx.registerService(EPackage.class, pkg, null)`) rather than via `MetadataServiceFactory`
- Package split fixes: `org.eclipse.fennec.model.metadata.service` exported; `codec.value` impl classes moved to `codec.value.impl`; `codec.format` impl classes moved to `codec.format.impl`
- `test.bndrun` explicitly requires `org.eclipse.fennec.codec.metadata` (DYNAMIC reference not auto-resolved by bnd)

✅ **TCK Test Suite (Plan F)**
- 18 abstract TCK classes in `org.eclipse.fennec.codec.tests` covering all codec features
- 8 dedicated ecore test models for TCK scenarios
- P0: 6 core round-trip suites (attributes, references, enums, multi-valued, complex)
- P1: 7 feature strategy suites (type, ID, enum, polymorphism, reference, value handling, custom key)
- P2: 10 advanced feature suites (EMap, SuperType, visibility, force, global ignore, strictness, array root, large payload, extended metadata, custom value)
- Each format (BSON, CBOR, YAML) has all 18 TCK suites as concrete subclasses
- BSON additionally has 11 unconsumed-value regression tests verifying the `valueConsumed` fix

✅ **Deprecated Code Removed**
- All `old codec.v2.*` source/test files deleted (55 src + 9 test)
- All `codec.api.value.*` and `codec.api.diagnostic.*` files deleted (11 src + 14 test)
- Zero skipped tests remaining

### 3.2 Test Status

> **2026-08-07:** `org.eclipse.fennec.codec` alone now runs **1,497** tests in 138 classes
> (0 failures, 2 skips) after the #110 campaign — the table below is the 2026-03-02 snapshot
> and is stale for that row. Coverage shape worth knowing: 5 test classes use a `ResourceSet`
> and 2 write real files (`CrossResourceReferenceTest`, `FileRoundTripTest`); everything else
> drives both directions in memory, which is precisely what hid #113.

**Current Counts (2026-03-02, after custom properties + JSON Schema enhancements):**

| Project | Tests | Suites | Notes |
|---------|------:|-------:|-------|
| `org.eclipse.fennec.codec.api` | 1,039 | 179 | |
| `org.eclipse.fennec.codec` | 1,231 | 373 | |
| `org.eclipse.fennec.codec.bson` | 98 | 33 | |
| `org.eclipse.fennec.codec.cbor` | 53 | 24 | |
| `org.eclipse.fennec.codec.yaml` | 53 | 24 | |
| `org.eclipse.fennec.codec.metadata` | 255 | 61 | |
| `org.eclipse.fennec.codec.geojson` | 34 | 13 | |
| `org.eclipse.fennec.codec.jsonschema` | 146 | 43 | +13 new array/suppression tests |
| `org.eclipse.fennec.codec.openapi` | 75 | 34 | |
| `org.eclipse.fennec.codec.osgi.tests` | 15+ | 15 | OSGi integration tests |
| **Total** | **3,061+** | **799+** | |

All tests pass with 0 failures, 0 errors, 0 skipped.

**Test Organization:**
- `org.eclipse.fennec.codec.api/test` — config API tests (spec + resolver tests)
- `org.eclipse.fennec.codec.metadata/test` — aspect provider tests + TypeDiscriminatorServiceTest
- `org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/*` — runtime tests (all active)
- `org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/util/*` — helper unit tests
- `org.eclipse.fennec.codec/test/org/eclipse/fennec/codec/format/*` — FormatDelegate parity + integration tests
- `org.eclipse.fennec.codec.tests/src` — abstract TCK classes + ecore models (shared test infrastructure)
- `org.eclipse.fennec.codec.bson/test` — BSON format delegate + provider + TCK tests
- `org.eclipse.fennec.codec.cbor/test` — CBOR TCK tests
- `org.eclipse.fennec.codec.yaml/test` — YAML TCK tests

### 3.3 What's Next

**COMPLETED:**
1. **Plan E: Multi-Format Support** — ✅ COMPLETE (BSON, CBOR, YAML all operational)
   - FormatDelegate abstraction fully operational with 4 format providers
   - Total format tests: 204 (BSON 98 + CBOR 53 + YAML 53) + JSON parity tests in codec project

2. **Plan F: TCK Test Suite** — ✅ COMPLETE (18 abstract TCK classes × 3 formats)
   - P0 Core: 6 round-trip suites
   - P1 Features: 7 strategy suites
   - P2 Advanced: 10 advanced feature suites
   - Bug fixes: array root serialization, `supportsArrayRoot()` capability, BSON reader `valueConsumed` fix

**OPEN (as of 2026-08-07):**
1. **#110** — the asymmetry campaign bracket. Every ticket under it is done; it stays open only
   until someone decides the bracket has served its purpose.
2. **#83** — `XMLURIHandler.resolve()` crashes with `ArrayIndexOutOfBoundsException`. Untouched.
3. **Code-quality block** (#58-#81) — a batch of its own, unrelated to the hardening work.
4. **Cross-repo** — emf.persistence-jpa#116 (Mongo) and emf.search#33 (Lucene) ask the two
   downstream consumers to verify the id-plane behaviour through their own stacks;
   eclipse-fennec/.github#20 documents the three CI gates.

**DEFERRED:**
1. Plan B remaining: GAP-004 (Diagnostic Options), GAP-013 (Enum annotations), GAP-014 (inherit enum)
2. Plan C: Documentation examples (DOC-001 through DOC-004)
3. Performance testing
4. User guide, migration guide

## 4. Key Documents

### 4.1 Specification (Source of Truth)

**Location:** `docs/codec-v2-spec/`

| File | Purpose |
|------|---------|
| `00-overview.md` | Table of contents |
| `01-introduction.md` | Goals, architecture, terminology |
| `02-config-resolution.md` | 5-source hierarchy, scope chain |
| `06-type.md` | Type serialization/deserialization |
| `07-supertype.md` | SuperType configuration |
| `08-discriminator-mapping.md` | Discriminator resolution |
| `09-id.md` | ID serialization/deserialization |
| `10-reference.md` | Reference serialization/deserialization |
| `11-feature.md` | Feature (attribute) serialization/deserialization |
| `12-feature-serialization.md` | Feature visibility model, gates |
| `15-error-handling.md` | Diagnostics, validation rules |
| `16-annotation-reference.md` | Complete property reference |
| `17-format-abstraction.md` | PLAIN vs STRUCTURED formats |

### 4.2 Architecture Documents

**Metadata Layer:**
- `org.eclipse.fennec.codec.metadata/codec-metadata-architecture.md`
- generic metadata & fingerprint API: the `emf.osgi` project (`org.eclipse.fennec.emf.osgi.metadata`, `docs/model-fingerprint-guide.md`) — consumed, not part of this workspace

**JSON Schema:**
- `org.eclipse.fennec.codec.jsonschema/jsonschema-architecture.md`

**Development:**
- `docs/codec-v2-development-guide.md` (this file)
- `docs/codec-v2-plans.md` (roadmap, GAP analysis)
- `docs/codec-v2-reference.md` (EMF concepts, terminology, API reference)

## 5. Development Workflow

### 5.1 Making Changes

1. **Check spec first** — `docs/codec-v2-spec/`
2. **Write test** — TDD approach (spec test or integration test)
3. **Implement** — follow spec exactly
4. **Verify** — run relevant test suite
5. **Update docs** — if behavior clarified or new feature added

### 5.2 Testing Strategy

**Spec Tests** (codec.api):
- Unit tests for configuration objects
- Resolver tests for configuration resolution
- Validation tests for diagnostic rules

**Integration Tests** (codec):
- Resource tests (CodecResource end-to-end)
- Ser/Deser tests (specific features)
- Roundtrip tests (symmetry verification)

**TCK Tests** (codec.tests + format projects):
- Abstract TCK classes define format-agnostic test logic with `createFormatProvider()` + `getFileExtension()` hooks
- Each format project provides concrete subclasses (e.g., `BsonEMapTCKTest extends AbstractEMapTCK`)
- Dedicated ecore models per TCK topic (in `codec.tests/src/.../tck/`)
- Three priority tiers: P0 (core round-trip), P1 (feature strategies), P2 (advanced features)
- Tests use `ConfigurationResolver.builder()` for per-test configuration
- Format capability checks via `CodecFormatProvider.supportsArrayRoot()` for graceful test adaptation

**Custom Codec Tests** (codec.geojson, codec.jsonschema, codec.openapi):
- Domain-specific serialization tests
- Custom value reader/writer tests

### 5.3 Common Patterns

**Configuration Resolution:**
```java
EffectiveCodecConfig effectiveConfig = ...;
TypeConfig resolved = effectiveConfig.resolveTypeConfig(eClass);
if (resolved.getTypeStrategy() == TypeStrategy.NAME) {
    // Use name-based type serialization
}
```

**Aspect Creation (Metadata Layer):**
```java
// In CodecAspectProvider
ClassConfig config = MetadataFactory.eINSTANCE.createClassConfig();
config.setTypeStrategy(TypeStrategy.NAME);
// Validation happens here (Layer 1)
validateTypeConfig(config, eClass, diagnostics);
```

**Entry Pattern (Codec Layer):**
```java
// Serialization
TypeSerializationEntry entry = new TypeSerializationEntry(effectiveConfig, ...);
entry.serialize(eObject, generator, context);

// Deserialization
TypeDeserializationEntry entry = new TypeDeserializationEntry(effectiveConfig, ...);
EClass resolved = entry.resolveEClass(parser, context, currentReference);
```

**Discriminator Mapping (Type Mapping Registry):**
```java
// Deserialization - targeted resolve with mapId
String mapId = getDiscriminatorMapId(hintEClass);
EClass resolved = typeResolver.resolve(mapId, discriminatorValue, namespace, hintEClass, fallbackStrategy);

// Serialization - reverse lookup
String mapId = typeDiscriminatorService.getMapIdForEClass(actualEClass);
String discriminatorValue = typeDiscriminatorService.getDiscriminatorValue(mapId, actualEClass);
```

### 5.4 Remaining Work

**Plan B Phase 1: Migration GAPs**
- [✅] GAP-001: Feature Visibility — DONE
- [✅] GAP-005: ID Value Key — DONE
- [✅] GAP-002: Fallback Strategy wiring — DONE (TypeDiscriminatorService handles inline + typeMapping fallback)
- [✅] GAP-003: Feature Strictness — DONE (ClassConfig, strictOnUnknown/Missing in deserialization)
- [⏸️] GAP-014: inherit enum — POSTPONED to B2 (requires codec.ecore model change)
- [⏸️] GAP-004: Diagnostic Options — POSTPONED to B2 (NEW FEATURE)

**Integration Tests**
- [✅] Discriminator mapping tests (CodecResourceInlineMappingTest.java)
- [✅] Feature visibility integration tests (FeatureVisibilityIntegrationTest.java — ignoreRead/Write/ignore)
- [✅] Reference expansion integration tests (ExpandReferenceTest.java — edge cases added)
- [✅] PLAIN reference format tests (PlainReferenceFormatTest.java — ser/deser/round-trip)
- [✅] Strictness integration tests (StrictnessIntegrationTest.java — strictOnUnknown/Missing)
- [✅] Type resolution tests (TypeStrategy*.java, TypeResolution*.java, TypeDeserializationEntryTest — URI/NAME/NUMERIC/CLASS/SCHEMA_AND_TYPE, contexts, hints)
- [✅] ID serialization tests (CodecResourceIdTest.java — 26 tests covering PLAIN/STRUCTURED, IdKeyMode, separators, round-trip)

**Code Quality** (completed)
- [✅] Deprecated code removal (old codec.v2.*, codec.api.value.*, codec.api.diagnostic.*)
- [✅] @claude comment cleanup
- [✅] Helper extraction: TypeResolutionHelper, EMapHelper

**Documentation:**
- [ ] User guide (how to use codec v2)
- [ ] Migration guide (v1 → v2)
- [ ] Performance guide

## 6. Debugging Tips

### 6.1 Common Issues

**Issue: Config not taking effect**
- Check scope chain (Options > Resource > Factory > Module > Annotation)
- Check if property is runtime-only (can't be in EAnnotation)
- Check if validation rule prevents it (check diagnostics)

**Issue: Type resolution fails**
- Check TypeStrategy (NONE disables type info)
- Check discriminator mapping (registered in MetadataService?)
- Check fallback chain (discriminator → hints → declared type)

**Issue: Feature not serialized/deserialized**
- Check visibility gates (ignore, ignoreWrite, ignoreRead)
- Check force flags (forceWrite, forceRead for volatile/transient)
- Check value gates (serializeNull, serializeEmpty, serializeDefault)

**Issue: the test run hangs with no output**
- Look for a **swallowed exception first**, not at the loop. The sequence that cost a full
  session: a value was lost → the conversion threw two levels from the cause → the catch
  logged and continued → the parser stayed mid-object → the enclosing loop waited for an
  `END_ARRAY` that could never arrive. See #129/#131/#132.
- All token loops must go through `util/TokenLoops`, which terminates on stream end. A raw
  `while (parser.nextToken() != END_ARRAY)` is the bug, not the symptom.
- To find the culprit fast: run with `--tests` narrowed, or patch `CodecResource`'s load path
  to print diagnostics and run the suite once (see below).

**Issue: something is wrong but nothing is reported**
- A diagnostic that reaches only the JUL logger is invisible to a caller — every component
  must report through `DiagnosticCollector`/`ContextHelper.addWarning`, and static utilities
  take it as a parameter (`TypeResolutionHelper`).
- To sweep the whole suite: temporarily print `getErrors()`/`getWarnings()` after
  `diagnosticCollector.addToResource(this)` in both load paths, run the full suite, and group
  the output by test class. One pass surfaced 50 dirty loads across 27 test classes.
- A load that reports and still returns normally is only visible to callers who check.
  `DeserializationMode.STRICT` turns any error into a failed load.

**Issue: Discriminator mapping not working**
- Check mapId extraction: is discriminatorMapId set on DiscriminatorConfig?
- Check fallback strategy: ERROR vs FALLBACK behavior
- Check supertype walking: does getMapIdForEClass() find the config?
- Check exception propagation: is IllegalStateException from ERROR strategy being re-thrown?

### 6.2 Diagnostic Inspection

All metadata parsing errors are collected in `DiagnosticCollector`:

```java
DiagnosticCollector diagnostics = ...;
for (Diagnostic diag : diagnostics.getDiagnostics()) {
    System.err.println(diag.getSeverity() + ": " + diag.getMessage());
}
```

## 7. Known Issues & Limitations

### 7.1 Current Limitations

1. ~~**Cross-document containment references** — not yet supported (deser only)~~ — closed by
   #113/#114: a containment in another resource is written as a reference rather than inlined,
   and resolves on read. Pinned by `resource/CrossResourceReferenceTest` (real files, fresh
   resource set) on both write paths. Non-containments still come back as **proxies** by
   design — EMF resolves them on access or via `EcoreUtil.resolve`.
2. **Custom Jackson modules** — limited integration
3. **Streaming mode** — not optimized for large documents

### 7.2 Spec Gaps (To Be Addressed)

1. Entry-build pattern not documented in spec §1.2
2. Deserialization gate shouldDeserialize() usage not explicit
3. isChangeable() pre-check not documented — the runtime behaviour is settled (a
   non-changeable feature present in the document is reported as *not changeable, value
   dropped*, never as an unknown feature, so `strictOnUnknown` does not fail on it — #131);
   the spec has yet to say so

### 7.3 Fixed Bugs

**2026-08-06/07 — the #110 batch (compact; per-defect evidence is in the review doc):**

| # | Defect | Where it lived |
|---|---|---|
| #112 | PLAIN id decode ignored a single-entry `idFeatures` list | `IdDeserializationEntry` |
| #113 | Cross-resource references written as same-document fragments on the plain save path | `ReferenceSerializationEntry` — the source resource was only looked up in the context, never via `source.eResource()` |
| #114 | Mixed multi-valued references lost elements | `ReferenceDeserializationEntry` |
| #115 | `EJavaObject` `Map`/`List` written as `toString()`, read back as String | both attribute entries |
| #116 | Class/reference-scoped `typeKey`/`typeStrategy` ignored on read | `CodecEObjectDeserializer.isTypeKey` checked the global key first |
| #117 | Array component types the writer emits were not read back | `AttributeDeserializationEntry` — short/byte/char/boxed readers were missing |
| #118 | `EDate` unreadable in formats without native date-time | now `EcoreUtil.convertToString(EDATE, …)` both ways |
| #119 | `idKeyMode`/`idValueKey` inside the STRUCTURED `_id` object | a single id value goes under the inner key, several under their feature names |
| #120 | `idFeatures` on an EReference stringified the reference | the contained object's own id config builds it; that object then writes features, not a second `_id` |
| #121 | `codec.flatten` — documented as write-only, read side stopped warning | spec 10-reference.md §8.2 |
| #123 | OpenAPI `$ref` misread as a cross-document marker | `modelOwnsRefKey()` checks name *and* codec annotation key |
| #129 | Buffered numeric lost its value through Jackson's deferred numbers | `FormatDelegateParser` caches per token, type-exact first reads |
| #131 | Conversion failures swallowed; no way to escalate | `util/ConversionFailures` + `strictOnConversion`; a non-changeable feature is no longer "unknown" |
| #132 | Unguarded token loops spun forever after a swallowed error | `util/TokenLoops` terminates on stream end |
| #134 | Type resolution had no escalation path, reasons logger-only | STRICT fails the load; `TypeResolutionHelper` reports into the resource |

**2026-08-05 (b):**
✅ **`typeFormat=STRUCTURED` + `superTypeSerialize=true` threw an NPE on save** (`TypeSerializationEntry`, issue #111)
- `serializeSuperTypeInStructured` took the raw `superTypeConfig.getSuperTypeKey()`, which is `null` by default (the key is format-dependent), and handed it to `gen.writeArrayPropertyStart(null)`
- The PLAIN entry had it right all along (`SuperTypeSerializationEntry:81` uses `getEffectiveSuperTypeKey(format)`); only the embedded STRUCTURED path used the raw getter
- Fix: resolve the embedded key via `getEffectiveSuperTypeKey(SerializationFormat.STRUCTURED)` — the block sits inside the STRUCTURED `_type` object, so the STRUCTURED default (`supertype`) applies when nothing is configured
- Tests: `CodecResourceSuperTypeTest.superTypeEmbeddedInStructuredTypeObject`. The existing unit tests always set `superTypeKey` explicitly, which is exactly what masked the null default

**2026-08-05:**
✅ **STRUCTURED id decode clobbered a component when the eID attribute is itself an id feature** (`IdDeserializationEntry`, issue #108)
- `OrderLine` with `idFeatures=[orderId, lineNo]`, `orderId` also the `eID` attribute: after decoding `{"_id": {"orderId": "A", "lineNo": 2}}` the object carried `orderId="A-2"` instead of `"A"`
- Root cause: `deserializeStructured` sets the component features first, then — for `idValues.size() > 1` with an EIDAttribute present — additionally wrote `combineIdValues(...)` into the EIDAttribute. That write is meant for a **separate derived key** attribute; when the EIDAttribute is one of the id features it overwrites the component just restored
- Fix: the combined write is skipped when the EIDAttribute's name appears in the resolved id feature list (or in the parsed `idValues`). The derived-key case is unchanged
- **Same session, the mirror-image gap on the PLAIN path:** PLAIN only split the `_id` string onto the components and never fed a separate derived key attribute, so a `DerivedKeyPerson`-shaped class round-tripped through STRUCTURED but lost its `key` through PLAIN. PLAIN now writes the raw combined `_id` string into a derived key attribute
- Both paths now share `isDerivedKeyAttribute(List)` as the single rule ("the EIDAttribute is not one of the id features and is changeable"); the `isChangeable()` guard replaces the STRUCTURED path's catch-and-log for non-changeable attributes
- Tests: `IdDeserializationEntryTest` — `keepsComponentValueWhenEIdAttributeIsIdFeature` + `setsCombinedValueOnSeparateDerivedIdAttribute`, once in `StructuredFormatTests` and once in `MultipleIdFeaturesTests` (PLAIN). New fixtures in `test-deserialization.ecore`: `OrderLine` (eID attribute is an id feature) and `DerivedKeyPerson` (separate derived key)

**2026-07-29:**
✅ **`XMLURIHandler.resolve()` crash on short relative `xsi:schemaLocation` URIs** (`XMLURIHandler`, issue #83)
- POSTing XMI with an `xsi:schemaLocation` whose relative URI ends in `.ecore` but has fewer than 3 segments (e.g. `sensinact-mapping.ecore`) threw `ArrayIndexOutOfBoundsException: Index -2 out of bounds for length 1` during deserialization in `BaseJakartaCodecMessageBodyReaderWriter`
- Root cause: the `.ecore` branch unconditionally read the last three URI segments to build a `platform:/plugin/<bundle>/<folder>/<file>` URI
- Fix: guard with `segmentCount() < 3` — short schema-location hints are returned untouched
- Tests: `XMLURIHandlerTest.Resolve` (5 tests: 1- and 2-segment `.ecore` URIs untouched, 3-segment platform-plugin mapping, absolute-path passthrough, non-ecore resolution against resource URI)

**2026-06-26:**
✅ **Enum deserialization crash on unknown values** (`AttributeDeserializationEntry`, issue #11)
- NPE ("Cannot invoke `EEnumLiteral.getInstance()` because `literal` is null") when JSON contained an enum value not in the model
- Already fixed: `convertEnumFromString`/`convertEnumFromInteger` return `null` for unknown lookups; `deserializeSingleValued` skips `eSet` when value is null, leaving the field at its default
- Regression tests added: `EnumSerializationTest.UnknownEnumValues` (3 tests: unknown string, unknown integer, unknown value in multi-valued array)

✅ **`fieldOrder=ALPHABETICAL` ignored in JSON/Jackson serialization** (`CodecResource`, issue #31)
- `createObjectMapper()` never read `ConfigProperty.FIELD_ORDER` from the resolver, so `CodecModule` was always built with `sortPropertiesAlphabetically=false`
- Fix: extract `FIELD_ORDER` from `operationResolver` and pass `"ALPHABETICAL".equalsIgnoreCase(fieldOrder)` to `CodecModule.Builder.sortPropertiesAlphabetically()`
- Tests: `FormatDelegateJsonRoundTripTest.FieldOrder` (2 tests)

✅ **`getGlobalProperty()` ignores `codec.`-prefixed keys** (`ConfigurationResolver`, issue #13)
- `getGlobalProperty()` only looked up the short key (`"fieldOrder"`) via a raw map lookup; passing `"codec.fieldOrder"` (as the javadoc suggests) silently fell back to the default
- Affected all properties read in `CodecResource.createObjectMapper()`: `FIELD_ORDER`, `SMART_COMPRESSION`, `DATE_FORMAT`, `IGNORE_FEATURES`, `USE_NAMES_FROM_EXTENDED_METADATA`, all `EXPAND_*`
- Fix: replaced manual `containsKey(key)` loop with `ConfigMergeHelper.getValue()`, which already tries both key forms and is used by every `mergeWith()` path
- Tests: `ConfigurationResolverTest.GetGlobalProperty` (5 tests)

✅ **`CODEC_FEATURE_VALUE_WRITERS` name-based binding silently ignored** (`AttributeSerializationEntry`, `AttributeDeserializationEntry`)
- `ContextHelper.getFeatureValueWriter/Reader()` existed but was never called in `resolveEffectiveWriter/Reader()` — passing a writer/reader name via `CODEC_FEATURE_VALUE_WRITERS`/`READERS` options silently fell through to the default serializer in all Jackson-based formats (JSON, YAML, BSON, CBOR)
- Fix: added Priority 2 (name-based lookup from registry) in both entry classes, between Priority 1 (instance binding via `CODEC_FEATURE_VALUE_WRITER_INSTANCES`) and Priority 3 (annotation-resolved writer name)
- Tests: `CodecResourceCustomValueTest.NameBindingTests` (2 tests); `AbstractCustomValueTCK` extended with `writerResolvedByNameFromRegistry` + `readerResolvedByNameFromRegistry` (automatically run by YAML, CBOR, BSON TCK subclasses)

✅ **`CODEC_FEATURE_VALUE_WRITERS` silently ignored on the tabular path (CSV/ODS/XLSX/R-Lang)** (`TabularDocumentBuilder`)
- `TabularDocumentBuilder` builds `Cell` objects directly from `EObject.eGet()` values, bypassing `AttributeSerializationEntry` entirely — `CODEC_FEATURE_VALUE_WRITERS` had no effect regardless of what was configured
- Fix: added `CodecOptions.INTERNAL_VALUE_REGISTRY` (in `codec.api`) to carry the `CodecValueRegistry` through `effectiveOptions` from `CodecResource` without a circular dependency; threaded `registry` + `featureWriters` map through the entire tabular build chain; added `invokeWriterAsCell()` + `MinimalWriterContext` to invoke the writer via a temporary Jackson generator and map the resulting token to the appropriate `Cell` subtype (String/Long/Double/Boolean)
- Verified with Gogo command `exportWithWriter CSV FLAT` in playground bundle

**2026-06-25:**
✅ **`idOnTop=true` had no effect on JSON/YAML field order** (`CodecEObjectSerializer`)
- `buildSerializationEntries()` added `_id` before `_type`, so `idOnTop=true` was a no-op for the `_id` key (already first) and `idOnTop=false` produced wrong order (`{"_id":…,"_type":…}` instead of spec `{"_type":…,"_id":…}`)
- Additionally, `idOnTop` only moved the synthetic `_id` key; the `eID=true` EAttribute feature remained in declaration order, inconsistent with tabular exporters where `idOnTop` floats `getEIDAttribute()` to front
- Fix 1: reordered `buildSerializationEntries()` → type → supertype → id → features (spec §8.7 default order)
- Fix 2: `applyOrdering()` now accepts `EClass`, resolves the EIDAttribute's JSON key, and floats it alongside `_id` when `idOnTop=true`
- Tests: `CodecResourceIdTest.IdOnTopOrderingTests` (5 tests)

✅ **BSON serializes document twice** (`BsonFormatProvider.BsonStreamWriter`)
- `BsonStreamWriter.flush()` and `close()` both called `serializeToStream()`, writing the in-memory `BsonDocument` to the `OutputStream` twice
- Root cause: Jackson's `GeneratorBase.close()` calls `flush()` before `_closeInput()`, so both fire during every normal `CodecResource.save()` — resulting in two complete BSON documents in the output stream
- Fix: removed `serializeToStream()` from `flush()`; the document is now serialized only in `close()`. `flush()` is a no-op for the in-memory delegate (`BsonFormatDelegate.flush()` does nothing)
- Regression test: `BsonFormatProviderTest.StreamWriterDocumentCount` — calls `writer.flush()` + `writer.close()` in sequence and asserts exactly one BSON document in the output (verified by parsing the raw 4-byte length-prefixed BSON framing)

**2026-02-17:**
✅ **Array root serialization crash** (CodecResource)
- `doSaveWithFormat()` passed `EObject[]` to `ObjectWriter` typed for `EObject` → `InvalidDefinitionException`
- Fix: iterate objects individually within array start/end when multiple root objects present

✅ **BSON multi-root not supported** (BsonFormatProvider, CodecFormatProvider)
- Added `supportsArrayRoot()` to `CodecFormatProvider` interface (default `true`)
- `BsonFormatProvider` overrides to return `false` — throws `IOException` on multi-root save
- `AbstractArrayRootTCK` tests both supported (round-trip) and unsupported (assertThrows) paths

✅ **BSON reader infinite loop on unconsumed values** (BsonFormatReaderDelegate)
- BSON's state machine requires explicit value consumption (unlike Jackson streaming parsers)
- When codec skipped unknown fields by calling `nextToken()` without reading, reader stayed in VALUE state forever
- Fix: `valueConsumed` boolean flag — set `false` in `handleBsonType()` for primitives, set `true` in all read methods
- At start of `nextToken()`, if `!valueConsumed && state == VALUE`, calls `reader.skipValue()` to advance
- Verified with 11 dedicated tests covering all BSON value types (string, int, long, double, decimal128, boolean, binary, ObjectId, multiple consecutive, array context, nested SuperType scenario)

**2026-02-05:**
✅ **Discriminator mapping not using targeted resolve()** (TypeDeserializationEntry, CodecEObjectDeserializer)
- Now extracts mapId from DiscriminatorConfig and calls targeted `resolve(mapId, value, ...)`
- Fallback strategy (ERROR vs FALLBACK) now respected correctly

✅ **Exception propagation for ERROR strategy** (CodecEObjectDeserializer)
- IllegalStateException from unknown discriminator now re-thrown in replayDeferredValue() and deserializeContainedObject()

✅ **FeaturePathTypeResolver handling standard _type paths** (FeaturePathTypeResolver)
- Added `!isTypeKey(discriminatorPath)` guard to prevent interference with standard type key

✅ **Discriminator mapping serialization** (TypeSerializationEntry)
- Added resolveTypeMappingDiscriminator() that calls TypeDiscriminatorService.getDiscriminatorValue(mapId, eClass)
- Fixed hasDiscriminatorPath() to only suppress _type for nested paths

✅ **Supertype inheritance for discriminator maps** (TypeDiscriminatorService)
- getMapIdForEClass() now walks supertypes via getEAllSuperTypes()

**2026-02-04:**
✅ **forceRead not working for volatile features** (ConfigurationResolver:420)
- Only checked `isForceWrite()`, now checks `isForceRead()` too

✅ **forceWrite two-gate model** (AttributeSerializationEntry, ReferenceSerializationEntry)
- forceWrite now ONLY affects visibility gate, NOT value gate
- serializeNull/Empty/Default still apply with forceWrite=true

## 8. Gradle Commands

```bash
# Build everything
./gradlew build

# Test specific project
./gradlew :org.eclipse.fennec.codec:test
./gradlew :org.eclipse.fennec.codec.metadata:test
./gradlew :org.eclipse.fennec.codec.api:test
./gradlew :org.eclipse.fennec.codec.bson:test
./gradlew :org.eclipse.fennec.codec.cbor:test
./gradlew :org.eclipse.fennec.codec.yaml:test

# Test specific test class
./gradlew :org.eclipse.fennec.codec:test --tests CodecResourceInlineMappingTest

# Clean build
./gradlew clean build

# Skip tests
./gradlew build -x test
```

**Note:** Do NOT use `testOSGi` for v2 projects (only for old codec).

## 9. Session Handoff Protocol

### 9.1 At Session Start

1. Read this document (section 0 "Active Task Hierarchy")
2. Check git status: `git status`, `git log --oneline -10`
3. Review "Next Session" in header
4. Ask user for clarification if needed

### 9.2 During Session

1. Update section 0.2 "Current Task Hierarchy" as work progresses
2. Mark tasks ✅ when complete
3. Add child tasks for nested investigations
4. Document decisions and blockers

### 9.3 At Session End

1. Update header: "Last Updated", "Session Summary", "Next Session"
2. Add new "COMPLETED" entry in section 0.2
3. Mark all tasks ✅
4. Commit changes: descriptive commit message

### 9.4 Current TODO List

**Plan E — Multi-Format Support:** ✅ COMPLETE (see `codec-v2-plans.md` §7)

**Plan F — TCK Test Suite:** ✅ COMPLETE

| Phase | Suites | Status |
|-------|--------|--------|
| P0 Core Round-Trip | 6 abstract TCK classes | ✅ |
| P1 Feature Strategies | 7 abstract TCK classes | ✅ |
| P2 Advanced Features | 10 abstract TCK classes + 8 ecore models | ✅ |
| Format Subclasses | 18 × 3 formats = 54 concrete test classes | ✅ |
| Bug Fixes | Array root, supportsArrayRoot, BSON valueConsumed | ✅ |

**TCK Abstract Classes** (in `org.eclipse.fennec.codec.tests`):
- P0: `AbstractRoundTripTCK` (6 suites for basic types/refs/enums)
- P1: `Abstract{TypeStrategy,IdStrategy,EnumStrategy,Polymorphism,ReferenceFormat,ValueHandling,CustomKey}TCK`
- P2: `Abstract{EMap,SuperType,Visibility,ForceReadWrite,GlobalIgnore,Strictness,ArrayRoot,LargePayload,ExtendedMetaData,CustomValue}TCK`

**Completed this session (2026-08-31):**
- [✅] Issue #171: `typeStrategy=NONE` honoured on read — step 3 of the read flow is skipped
      entirely and skipping it is not reported; a value under the type key is data; the
      NONE-without-root-type error names its actual reason (`TypeStrategyNoneOnReadTest`)
- [✅] Issue #173: `codec.typeHintMode` implemented — `OVERRIDE` makes `CODEC_ROOT_TYPE` win
      over the root's body type, contained objects untouched, warning when a stated type is
      discarded or when the mode is inert; EObject nesting depth now tracked on the read
      context to tell root from contained (`TypeHintModeTest`)
- [✅] Issue #174: unusable config values reported — `ConfigProperty` records the enum its
      value must name, `ConfigValueValidator` walks all five sources incl. scoped nested maps,
      `getBoolean` no longer turns an unparseable string into `false`
      (`ConfigValueValidationTest`)
- [✅] Issue #175: feature-level type and reference annotations forwarded —
      `AspectToPropertiesConverter.extractReferenceAspectProperties` was empty, dropping nine
      documented `F`-level properties (`FeatureAspectBridgeTest`)
- [✅] Issue #175 follow-up: 18 attributes made `unsettable` in `codec.ecore` (model
      regenerated by the maintainer) so the bridge asks `isSetX()` instead of comparing against
      the model default. `refKey="_ref"` and `refFormat="PLAIN"` are honoured, and an explicit
      `false` on a boolean feature flag overrides a wider scope
      (`FeatureAnnotationReferenceFormatTest`)
- [ ] Still open from #175: feature-level `idKey`/`idFormat` is documented
      (`02-config-resolution.md:483,485`) but not parsed and not resolvable — no
      `resolveIdConfig(EClass, EStructuralFeature, …)` overload exists

**Completed previous session (2026-03-02):**
- [✅] Custom properties: generic `customProperties` map on `EffectiveCodecConfig` (replaces hard-coded format fields)
- [✅] `CodecResource.extractCustomProperties()` — auto-collects `codec.*` options
- [✅] JSON Schema option key migration to `codec.jsonschema.*` namespace
- [✅] Fixed missing array `items` in `EPackageToJsonSchemaConverter`
- [✅] `OPTION_SUPPRESS_KEYWORDS` — keyword suppression for JSON Schema output
- [✅] 13 new tests (7 array + 6 suppression)
- [✅] Updated `jsonschema-architecture.md` and `codec-v2-development-guide.md`

**Completed previous session (2026-02-25):**
- [✅] OSGi facades: `MetadataServiceComponent`, `CodecAspectProviderComponent`
- [✅] OSGi integration tests: `org.eclipse.fennec.codec.osgi.tests` (15 test classes)
- [✅] Package split fixes: exported `model.metadata.service`; `codec.value.impl`; `codec.format.impl`
- [✅] DOC-001 through DOC-004: OSGi integration examples (covered by osgi.tests bundle)

**Deferred:**
- [ ] GAP-004: Diagnostic Options integration
- [ ] GAP-013: Enum-level annotation support
- [ ] GAP-014: inherit enum type mismatch
- [ ] Smile format: `org.eclipse.fennec.codec.smile` — add when demand arises

## 10. Reference Information

> See [`codec-v2-reference.md`](codec-v2-reference.md) for full reference details (EMF concepts, terminology, Jackson integration, metadata service usage, config builder patterns, deprecated API audit + migration order).

---

## 11. Session Continuity Tips

If context is lost:

1. Read this document first: `docs/codec-v2-development-guide.md`
2. Check spec for details: `docs/codec-v2-serialization-spec.md`
3. Review current TODO state (if available)
4. Examine recent git commits for context
5. Ask user for clarification if needed

The key insight: **MetadataService parses EAnnotations at EPackage registration time and creates pre-computed aspect objects that the codec uses at serialization time.**

**For Discriminator Mapping:** All packages are registered with MetadataService before serialization/deserialization. When registering an EPackage, the MetadataService runs through codec aspects and updates the TypeDiscriminatorService. By the time deserialization happens, there's already a mapping from discriminator values (like "temp-sensor") to EClasses. The runtime uses targeted `resolve(mapId, value, ...)` for correct fallback strategy behavior (ERROR throws exception, FALLBACK returns null). Serialization performs reverse lookup via `getDiscriminatorValue(mapId, eClass)`. The system supports supertype inheritance (walks supertypes to find discriminator config) and root-level discriminator mapping (not just for contained references).
