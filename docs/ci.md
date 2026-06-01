# GitHub CI

The repository runs five GitHub Actions workflows. Together they cover
pull-request validation, license-header enforcement, automated PR review by
Claude, snapshot publication from the `snapshot` branch, and release
publication from the `main` branch.

All workflow definitions live in [`.github/workflows`](../.github/workflows).

## Branch model

`snapshot` is the active development line — all PRs target it, and every push
publishes a `-SNAPSHOT` artifact. `main` always holds the latest released
version, which is available on
[Maven Central](https://repo1.maven.org/maven2/org/eclipse/fennec/codec/) under
`org.eclipse.fennec.codec:*`.

| Branch     | Purpose                                            | Publishes to                                              |
|------------|----------------------------------------------------|-----------------------------------------------------------|
| `snapshot` | Active development. PRs target this branch.        | Sonatype Central — `-SNAPSHOT` versions                   |
| `main`     | Latest release — code here matches what is on Maven Central. | Sonatype Central → Maven Central — final versions, signed with project GPG key |

## Workflow overview

```
┌─────────────────────────┐
│   PR / feature branch   │
└────────────┬────────────┘
             │  push / pull_request
             ▼
   ┌─────────────────┐   ┌──────────────────┐   ┌────────────────────┐
   │   build.yml     │   │   license.yml    │   │ claude-review.yml  │
   │   (CI Build)    │   │ (License header) │   │   (PR review)      │
   └─────────────────┘   └──────────────────┘   └────────────────────┘
             │
             │  merge into snapshot
             ▼
    ┌─────────────────┐
    │  snapshot.yml   │  →  publishes SNAPSHOT artifacts
    └─────────────────┘
             │
             │  merge into main
             ▼
    ┌─────────────────┐
    │   release.yml   │  →  publishes signed release artifacts
    └─────────────────┘
```

## `build.yml` — CI Build

* **File:** [`.github/workflows/build.yml`](../.github/workflows/build.yml)
* **Triggers:**
  * `push` on any branch **except** `main` and `snapshot`
  * `pull_request` on any branch
* **Purpose:** Validate that the source tree compiles, all tests pass, and
  the performance test suite still runs across the supported Java versions.
* **Matrix:** Java 21 and Java 25 (Temurin) — `fail-fast: false` so a
  failure on one JDK does not cancel the other.
* **Runner:** `ubuntu-latest`.
* **Steps:** checkout → Gradle wrapper validation → set up JDK with Gradle
  cache → `./gradlew build --info` → `./gradlew perfTest --info`.
* **Secrets used:** none — this workflow does not publish anything.

This is the workflow PR authors care about: a green run on both JDKs is the
gating signal for review.

## `license.yml` — License header check

* **File:** [`.github/workflows/license.yml`](../.github/workflows/license.yml)
* **Triggers:** `push`, `pull_request`, and manual `workflow_dispatch`.
* **Purpose:** Verify every source file carries the Eclipse Public License
  2.0 header. Uses [apache/skywalking-eyes](https://github.com/apache/skywalking-eyes)
  (pinned to `v0.8.0`) driven by [`.licenserc.yaml`](../.licenserc.yaml).
* **What it checks:** the SPDX header pattern declared in `.licenserc.yaml`,
  applied to every file *not* listed under `paths-ignore`.
* **Failure mode:** on a PR the action comments on the offending lines via
  `GITHUB_TOKEN`. The fix is to add the standard header (template in
  [`CONTRIBUTING.md`](../CONTRIBUTING.md#license-headers)) and push again.

## `claude-review.yml` — Claude PR Review

* **File:** [`.github/workflows/claude-review.yml`](../.github/workflows/claude-review.yml)
* **Triggers:**
  * On every `pull_request` (opened, synchronize, reopened)
  * On `issue_comment`, `pull_request_review_comment`, or
    `pull_request_review` events that contain `@claude` in the body
* **Purpose:** automated first-pass code review aligned with the
  conventions documented in [`CLAUDE.md`](../CLAUDE.md). The bot focuses on:
  * correctness against the spec under
    [`docs/codec-v2-spec/`](codec-v2-spec/00-overview.md)
  * missing or weak tests for behaviour changes (TDD)
  * fully-qualified class names in code
  * API / spec drift in the `codec` and `codec.metadata` modules
  * hand edits in EMF-generated `src-gen/` directories
* **Permissions:** `contents: read`, `pull-requests: write`, `issues: write`,
  `id-token: write` — needed to leave review comments.
* **Secrets used:** `ANTHROPIC_API_KEY`.

Treat Claude's comments as a fast first-pass review, not as a substitute
for human review.

## `snapshot.yml` — Snapshot Build

* **File:** [`.github/workflows/snapshot.yml`](../.github/workflows/snapshot.yml)
* **Triggers:** `push` to the `snapshot` branch only. Pull requests are
  explicitly excluded so untrusted code cannot reach the publishing step.
* **Purpose:** Build, test, and publish `-SNAPSHOT` artifacts whenever the
  `snapshot` branch advances.
* **Matrix:** Java 21 and Java 25.
* **What it publishes:** only the **Java 21** job runs the publishing step
  (`./gradlew build release --stacktrace --scan --info`). Java 25 builds
  and runs perfTest only, as a compatibility canary.
* **Test artifacts:** JUnit XML reports are uploaded under
  `test-results-java-${java-version}` for every run.
* **Secrets used:**
  * `CENTRAL_SONATYPE_TOKEN_USERNAME`, `CENTRAL_SONATYPE_TOKEN_PASSWORD` — Sonatype Central credentials
  * `GPG_PRIVATE_KEY`, `GPG_PASSPHRASE`, `GPG_KEY_ID` — signing key (imported into the runner's keyring, deleted at the end of the job)

## `release.yml` — Release Build

* **File:** [`.github/workflows/release.yml`](../.github/workflows/release.yml)
* **Triggers:** `push` to the `main` branch only. PRs are explicitly excluded.
* **Purpose:** Cut a signed release to Sonatype Central whenever `main` advances.
* **Matrix:** Java 21 and Java 25. As with `snapshot.yml`, only Java 21
  executes the release step (`./gradlew build release --info` with
  `DO_RELEASE=true`). Java 25 acts as the compatibility check.
* **Secrets used:** same set as `snapshot.yml`.
* **Result:** signed artifacts pushed to Sonatype Central and (after the
  Central sync) to Maven Central.

## Published artifacts

Releases and snapshots are published to **Sonatype Central**, from which
releases sync to Maven Central. The group id is `org.eclipse.fennec.codec`
(plus `org.eclipse.fennec.model.metadata` for the metadata bundle).

| Channel    | Repository URL                                                                                                                   | Pushed by                    |
|------------|----------------------------------------------------------------------------------------------------------------------------------|------------------------------|
| Release    | [Maven Central](https://repo1.maven.org/maven2/org/eclipse/fennec/codec/) — `org.eclipse.fennec.codec:*`                          | `release.yml` on `main`      |
| Snapshot   | [Sonatype Central snapshots](https://central.sonatype.com/repository/maven-snapshots/org/eclipse/fennec/codec/) — `*-SNAPSHOT`   | `snapshot.yml` on `snapshot` |
| Browse     | [search.maven.org `org.eclipse.fennec.codec`](https://search.maven.org/search?q=g:org.eclipse.fennec.codec) — find a version     |                              |

Notable artifacts published from this repository:

* `org.eclipse.fennec.codec:org.eclipse.fennec.codec` — core runtime
* `org.eclipse.fennec.codec:org.eclipse.fennec.codec.api` — public configuration API
* `org.eclipse.fennec.codec:org.eclipse.fennec.codec.metadata` — codec metadata aspects
* `org.eclipse.fennec.codec:org.eclipse.fennec.codec.bson` / `.cbor` / `.yaml` / `.geojson` / `.jsonschema` / `.openapi` — format providers
* `org.eclipse.fennec.model.metadata:*` — generic metadata service infrastructure

## Secrets

| Secret name                          | Purpose                                  |
|--------------------------------------|------------------------------------------|
| `CENTRAL_SONATYPE_TOKEN_USERNAME`    | Sonatype Central user token              |
| `CENTRAL_SONATYPE_TOKEN_PASSWORD`    | Sonatype Central token password          |
| `GPG_PRIVATE_KEY`                    | ASCII-armored GPG private key            |
| `GPG_PASSPHRASE`                     | Passphrase for the private key           |
| `GPG_KEY_ID`                         | Long-form key id (used by the build)     |
| `ANTHROPIC_API_KEY`                  | API key for the Claude PR review workflow |

The GPG key is imported on the fly and the keyring is removed in a final
step that runs even when the job fails (`if: always()`). The build never
echoes secret values.

## Reproducing CI locally

* Full PR build:
  ```bash
  ./gradlew clean build perfTest --info
  ```
* License headers:
  ```bash
  docker run --rm -v $(pwd):/github/workspace \
    ghcr.io/apache/skywalking-eyes/license-eye header check
  ```
* The snapshot / release workflows cannot be reproduced locally because they
  publish to Sonatype Central and require the project signing key.
