# JSON Schema Validation Keywords → OCL Invariants (Phase 2 + Phase 4)

**Status:** Implemented and verified — unit tests green, and confirmed
end-to-end against the live m2x OCL engine via the playground (see
[String literal escaping (found via live testing)](#string-literal-escaping-found-via-live-testing)
for a real bug this live test caught and the fix).
**Related:** [JSON Schema Validation to EMF/OCL Mapping Guide](JSON.Schema.Validation.to.EMFOCL.Mapping.Guide.md)
**Scope:** Phase 2 (Assertion Keywords to OCL Invariants) in full, and Phase 4
(OpenAPI Extensions & Formats) **limited to `format` (`uuid`/`email`)** — see
[Discriminator: out of scope](#discriminator-out-of-scope) below for why the
`discriminator` part of Phase 4 is not implemented here.

## Context

The mapping guide documents how JSON Schema / OpenAPI validation keywords
should translate into OCL invariants. Today, `JsonSchemaToEPackageConverter`
already parses and *preserves* every Phase 2 assertion keyword (`minLength`,
`maxLength`, `pattern`, `minimum`, `maximum`, `exclusiveMinimum`,
`exclusiveMaximum`, `multipleOf`) and the Phase 4 `format` keyword as inert
`EAnnotation` key/value pairs under `AnnotationSources.JSONSCHEMA` — but
nothing ever turns them into an enforceable constraint. `minItems`/`maxItems`
are the exception: those are already fully enforced structurally via
`lowerBound`/`upperBound` (`JsonSchemaToEPackageConverter.java:1782-1790`), so
they're out of scope here.

The goal: add an opt-in load option that compiles the remaining Phase 2/4
keywords into real OCL invariants, wired using EMF's standard "validation
delegate" annotation convention, with the delegate URI itself configurable —
so it works against the classic Eclipse OCL delegate, its Pivot variant, or
Data In Motion's own `/opt/git/emf.m2x` OCL engine, without fennec-codec ever
taking a compile/runtime dependency on any of them.

Confirmed decisions:
- Invariant naming: `<featureName>_<keyword>` (e.g. `age_minimum`,
  `email_pattern`).
- Unmappable/unsupported combinations are skipped with a warning diagnostic
  (reusing `JsonSchemaConversionDiagnostic`/`resource.getWarnings()`), not a
  hard failure.

### Discriminator: out of scope

The guide's Phase 4 discriminator example assumes a literal type-tag field
survives on the object after parsing (OpenAPI's `discriminator.propertyName`
convention: e.g. a real `type`/`petType` attribute holding `"Dog"`), and
checks it against the object's runtime type:
`self.oclIsTypeOf(Dog) implies self.type = 'Dog'`.

`JsonSchemaToEPackageConverter` doesn't produce that shape anywhere. Every
discriminated-union code path it has — `discriminatorKey`/`discriminatedUnion`
(`JsonSchemaToEPackageConverter.java:836,900-901`, a required-wrapper-key
pattern like `{"kafka": {...}}` vs `{"file": {...}}`), `commonBase`/`variant`
(`:999-1047`), and `multiType`/`variantIndex` (`:1946-1971`) — discriminates
**structurally**: the concrete `EClass` the converter chooses to instantiate
*is* the type tag. None of them retain a literal discriminator field as
EMF-visible data on the resulting object.

That means an OCL invariant here wouldn't just be redundant (an object can
only ever be instantiated as the `EClass` the converter decided on, so
`oclIsTypeOf` checks are true by construction) — it would reference a feature
(`self.type`, `self.petType`, ...) that doesn't exist on the `EClass` at all,
and wouldn't even parse. Decision: skip discriminator OCL generation
entirely for this change. If a future JSON Schema/OpenAPI shape introduces a
genuine retained discriminator field, this would need its own design pass.

## How constraints become enforceable (not just decorative)

Confirmed by reading `emf.m2x`'s `OclValidationDelegateFactory.java:57-89` and
its `docs/ocl-user-guide.md` §9: a constraint only actually validates at
runtime if three annotations exist:

1. `EPackage` EAnnotation, source `EcorePackage.eNS_URI`
   (`"http://www.eclipse.org/emf/2002/Ecore"`), detail
   `validationDelegates` = the configured delegate URI (space-joined if others
   already present).
2. `EClass` EAnnotation, same source, detail `constraints` = space-separated
   invariant names declared on that class (merged with any pre-existing list).
3. `EClass` EAnnotation, source = the configured delegate URI, with
   `invariantName -> oclExpression` details.

fennec-codec only ever writes these three annotation shapes — plain strings.
Whichever OCL engine is registered under that URI at runtime (Eclipse OCL,
its Pivot dialect, or `emf.m2x`, which serves both its native URI
`http://www.eclipse.org/fennec/m2x/ocl/1.0` and the legacy Pivot URI
`http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot`) does the evaluating.

### String literal escaping (found via live testing)

Live-testing against the m2x engine (via
`org.eclipse.fennec.codec.playground`'s `JsonschemaOCLResource` — a
`/jsonschema-ocl/schema` + `/jsonschema-ocl/validate` REST pair for manually
exercising schema→EPackage→OCL→`Diagnostician` end-to-end) surfaced a real
bug that the unit test suite couldn't catch, since it never invokes an
actual OCL parser: a `format: email` invariant failed with `OCL parse error:
token recognition error at: ''^[^@\\s'`.

Root cause: m2x's `STRING_LITERAL` grammar rule (`Ocl.g4:290-296`) only
recognizes a fixed whitelist of backslash escapes (`\\`, `\'`, `\"`, `\n`,
`\t`, `\r`, `\f`, `\b`, `\xHH`, `\uHHHH`, octal) — a bare `\s` or `\.` is a
lexer error, and there is **no doubled-quote (`''`) escape** for a literal
quote (unlike the classic OMG OCL / SQL convention). `JsonSchemaOclConstraintGenerator.oclStringLiteral()`
originally only doubled quotes (`''`) and never escaped backslashes at all —
wrong on both counts for this grammar. Fixed to: escape every literal
backslash as `\\` first, then escape every literal quote as `\'` (order
matters, so quote-escaping doesn't double the backslash just introduced).

Separately, `unquote()` was upgraded from naive quote-stripping (matching
`EPackageToJsonSchemaConverter`'s existing but JSON-escape-unaware
convention) to a real Jackson `readTree` parse, falling back to the raw
value if it isn't valid JSON (needed for the unquoted `format` annotation
value) — otherwise a `pattern` value containing a JSON-escaped backslash
(e.g. `"^\\d+$"`) would come out of `unquote()` still double-escaped, and
`oclStringLiteral()` would double it again into garbage.

**Caveat:** this escaping convention is specific to m2x's grammar. A real
Eclipse OCL / Pivot delegate follows the OMG-standard doubled-quote
convention instead and may not accept `\'`-style escapes. If this generator
is ever used against a non-m2x delegate, the escaping strategy may need to
be made delegate-aware.

## Changes

### 1. New options — `org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions`

Add, following the existing `codec.jsonschema.*` naming convention used by
e.g. `OPTION_USE_ANCHOR_REFS`:

```java
public static final String OPTION_GENERATE_OCL_CONSTRAINTS = "codec.jsonschema.generateOclConstraints"; // boolean, default false
public static final String OPTION_OCL_DELEGATE_URI = "codec.jsonschema.oclDelegateUri"; // String, default DEFAULT_OCL_DELEGATE_URI
public static final String DEFAULT_OCL_DELEGATE_URI = "http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot";
```

The default is the Pivot URI so it works out of the box against either real
Eclipse OCL or `emf.m2x` (which serves that URI as an alias) without the
caller needing to know which engine is installed.

### 2. New diagnostic code — `JsonSchemaConversionDiagnostic.java`

Add `Code.OCL_GENERATION_SKIPPED` and a factory method
`oclGenerationSkipped(String keyword, String location, String reason)`,
mirroring the existing `partialSupport(...)` / `unsupportedFeature(...)`
pattern (`JsonSchemaConversionDiagnostic.java:154-191`).

### 3. New class — `org.eclipse.fennec.codec.jsonschema.v2.converter.ocl.JsonSchemaOclConstraintGenerator`

A self-contained post-processing pass, decoupled from
`JsonSchemaToEPackageConverter` — it only reads the `AnnotationSources.JSONSCHEMA`
details that converter already writes. Entry point:

```java
List<JsonSchemaConversionDiagnostic> generate(EObject root, String delegateUri)
```

`root` is either an `EPackage` (walk `getEClassifiers()` filtered to `EClass`)
or a single `EClass` (the `convertToEClass` path). For each `EClass`:

- Iterate `eClass.getEStructuralFeatures()` (locally declared only — inherited
  features get their invariants from the declaring superclass, which EMF's
  validator already walks).
- Read each feature's `AnnotationSources.JSONSCHEMA` EAnnotation details.
  Values were stored via Jackson `JsonNode.toString()`
  (`JsonSchemaToEPackageConverter.java:2350-2365`), so re-parse each detail
  value with `JsonMapper.readTree(...)` (Jackson is already a project
  dependency) rather than hand-rolling quote-stripping — gives correct
  `asString()`/`asInt()`/`asDouble()` access.
- Per keyword, emit the OCL body from the guide's §2 table:
  - `minLength`/`maxLength` → `self.<f>.size() >= N` / `<= N`
  - `pattern` → `self.<f>.matches('<escaped-pattern>')`
  - `minimum`/`maximum` → `self.<f> >= N` / `<= N`
  - `exclusiveMinimum`/`exclusiveMaximum` → `self.<f> > N` / `< N`
  - `multipleOf` → `self.<f>.mod(N) = 0`, but only when the feature's
    `EDataType` is an integer type (`EInt`/`ELong`/wrapper). Otherwise skip +
    `oclGenerationSkipped` diagnostic (per the guide's note that `mod` is
    integer-only).
  - `uniqueItems` (`true`, and `feature.isMany()`) →
    `self.<f>->isUnique(e | e)`
  - `format` → only for recognized formats, via a small extensible
    `Map<String,String> FORMAT_PATTERNS` seeded with `uuid` and `email` (the
    two the guide specifies) → `self.<f>.matches('<regex>')`. Unrecognized
    formats are silently left as-is (no diagnostic — `format` is
    advisory/open-ended per spec, not a hard assertion).
- No discriminator handling — see
  [Discriminator: out of scope](#discriminator-out-of-scope).
- String literal escaping: wrap in `'...'`, doubling internal `'` per OCL
  convention. No JSON-regex → Java-regex translation is attempted (matches
  the guide's own caveat to "ensure your OCL environment supports
  `matches()`").
- After collecting `invariantName -> oclExpression` pairs for a class, apply
  the three-annotation wiring described above via a small
  `getOrCreateAnnotation(EModelElement, String source)` helper, merging into
  any existing `constraints` / `validationDelegates` detail values rather
  than overwriting.
- If `eClass.getEPackage() == null` (the standalone `convertToEClass` path
  has no owning package), skip step 1 (package-level `validationDelegates`)
  and emit an `oclGenerationSkipped` diagnostic noting the constraints were
  written but won't self-activate without a package context — annotations 2
  and 3 are still written since they're portable once the EClass is later
  attached to a package.

### 4. Wiring — `JsonSchemaResourceImpl.doLoad` (`JsonSchemaResourceImpl.java:123-145`)

After the existing `schemaToEPackageConverter.convert(...)` /
`convertToEClass(...)` call produces `eObj`, and only when
`OPTION_GENERATE_OCL_CONSTRAINTS` resolves truthy:

```java
if (extractOption(options, CodecJsonSchemaOptions.OPTION_GENERATE_OCL_CONSTRAINTS, Boolean.FALSE)) {
    String delegateUri = extractOption(options, CodecJsonSchemaOptions.OPTION_OCL_DELEGATE_URI,
            CodecJsonSchemaOptions.DEFAULT_OCL_DELEGATE_URI);
    getWarnings().addAll(new JsonSchemaOclConstraintGenerator().generate(eObj, delegateUri));
}
```

placed right before the existing diagnostics-transfer block so both sets of
warnings end up on `getWarnings()` together.

### 5. Docs

- `docs/codec-v2-spec/13-load-save-options.md` and
  `docs/codec-v2-spec/16-annotation-reference.md`: add the two new option
  keys and the new annotation shapes, following the existing table format
  already used for other `codec.jsonschema.*` options.
- Javadoc on the two new `CodecJsonSchemaOptions` constants, matching the
  style of the existing ones (see `OPTION_USE_ANCHOR_REFS` doc comment).

## Tests (TDD — write first)

New `org.eclipse.fennec.codec.jsonschema.v2.converter.ocl.JsonSchemaOclConstraintGenerationTest`,
following the `@Nested`-per-keyword style of `NewFeaturesTest.java`. Load a
schema via `JsonSchemaResourceImpl` with
`OPTION_GENERATE_OCL_CONSTRAINTS=true` and assert directly on the resulting
`EPackage`/`EClass` annotations (structural verification, engine-agnostic):

- Option off by default → no `validationDelegates`/`constraints`/delegate
  annotations are added, existing `JSONSCHEMA`-source annotations unchanged.
- Each Phase 2 keyword individually → correct invariant name, correct OCL
  string, correct three-annotation wiring (including merge behavior when a
  class already has unrelated `constraints`).
- `multipleOf` on a `EDouble` feature → skipped + `OCL_GENERATION_SKIPPED`
  diagnostic in `getWarnings()`.
- `pattern` containing a single quote → correctly escaped as `''` in the
  emitted OCL string.
- Custom `OPTION_OCL_DELEGATE_URI` → annotations use that URI instead of the
  default.
- `format: uuid` / `format: email` → correct regex invariant; an
  unrecognized `format` value → no invariant, no diagnostic.
- `convertToEClass` (`CODEC_ROOT_TYPE=ECLASS`) path → constraints/delegate
  annotations still written on the EClass, but package-level
  `validationDelegates` skipped with a diagnostic (no owning `EPackage`).

## Verification

- `./gradlew :org.eclipse.fennec.codec.jsonschema:test` — run the new test
  class plus the full existing suite to confirm no regressions (in
  particular `NewFeaturesTest`, `JsonSchemaDiagnosticsTest`, and the round-trip
  save tests, since `preserveAdditionalSchemaProperties` output is untouched).
- Manual spot check: load a schema with `minLength`/`pattern`/`multipleOf`
  with the option enabled, dump the resulting Ecore model (or inspect
  `EPackage.getEAnnotation(...)` in a quick scratch test), and confirm the
  three annotations are present and well-formed.
- Out of scope for this change (flagged, not blocking): an end-to-end test
  that actually installs `emf.m2x`'s `OclEngineImpl.installDelegates()` and
  evaluates a real object against the generated invariants would need a new
  test-scope dependency on `org.eclipse.fennec.m2x.ocl.engine` — currently
  fennec-codec has zero references to `emf.m2x` in any Gradle file. Worth
  doing as a follow-up once that dependency is deliberately added; not part
  of this change since the annotation-writing side must stay engine-agnostic.
