# Testing Strategy

How the codebase is tested: spec-driven unit tests, a shared format TCK, OSGi integration test projects, and a manual playground bench. Over 3,000 tests; the build is expected green with zero skips.

## Test Layers

Four layers, from fast to heavy. New codec work is developed test-first against the spec ([[decisions#Spec-First Development]]).

| Layer | Where | Runner |
|-------|-------|--------|
| Spec/config tests | `org.eclipse.fennec.codec.api/test` | `./gradlew :<project>:test` (JUnit 5) |
| Runtime + integration tests | `org.eclipse.fennec.codec/test`, `codec.metadata/test`, format bundles' `test/` | `test` (JUnit 5) |
| OSGi integration tests | `*.osgi.tests`, `*.jsonschema.tests`, `*.rest.tests` | `testOSGi` (bnd `test.bndrun`, live framework) |
| Manual bench | `org.eclipse.fennec.codec.playground` | run via `launch.bndrun`, not JUnit |

Rule from CLAUDE.md: `testOSGi` belongs to the dedicated `*.tests` integration projects only — do **not** add `testOSGi` to the codec-v2 code bundles; their tests run as plain JUnit 5 `test`.

## TCK Suites

`org.eclipse.fennec.codec.tests` contains ~18 abstract, format-agnostic TCK classes (`tck/Abstract*TCK.java`) plus shared ecore test models. Format bundles put it on their `-testpath` and subclass every suite.

Coverage tiers: P0 core round-trips (attributes, references, enums, multi-valued, complex), P1 strategy suites (type, ID, enum, polymorphism, reference, value handling, custom keys), P2 advanced (EMap, supertypes, visibility, force, strictness, array roots, large payloads, custom values). The spec-to-test mapping lives in `docs/codec-v2-spec/19-test-coverage.md`.

## OSGi Integration Tests

Three dedicated projects run scenarios inside a live OSGi framework to verify DS wiring, whiteboard binding, and capability resolution — things plain JUnit cannot see.

- `org.eclipse.fennec.codec.osgi.tests` — codec + format examples with EPackages registered as OSGi services
- `org.eclipse.fennec.codec.jsonschema.tests` — JSON Schema round-trip/conversion integration, including the semantic round-trip diff with a documented gap baseline
- `org.eclipse.fennec.codec.rest.tests` — JAX-RS resource-set wiring and client-override behavior

## Playground

`org.eclipse.fennec.codec.playground` is a runnable OSGi app (Gogo shell commands + JAX-RS resources) for live experiments that unit tests can't cover.

Example: validating generated OCL constraints against a real engine (`JsonschemaOCLResource`: `POST /jsonschema-ocl/schema`, then `/validate`).

It has historically caught real bugs that unit tests structurally miss (e.g. OCL string-literal escaping against the live m2x parser); prefer extending it over writing one-off benches.
