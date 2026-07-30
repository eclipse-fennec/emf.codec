# GitHub CI

CI for this repository runs entirely through the **reusable workflows** published in
[`eclipse-fennec/.github`](https://github.com/eclipse-fennec/.github). The workflows in this repo
are thin callers: they decide *when* something runs and with which permissions, while the actual
steps — license gate, build matrix, signing, publishing, docs deploy, security scans — live in one
place for the whole organisation.

Every call is **pinned to a commit SHA** with the tag in a trailing comment, so a change upstream
cannot silently alter this repository's CI.

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
│   PR / feature branch    │
└────────────┬─────────────┘
             │  push / pull_request
             ▼
   ┌──────────────────┐   ┌────────────────────────┐
   │   build.yml      │   │ dependency-review.yml  │
   │  → verify        │   │  → dependency-review   │
   └──────────────────┘   └────────────────────────┘
             │
             │  merge into snapshot
             ▼
   ┌──────────────────────────────────────────────┐
   │  snapshot.yml                                │
   │  verify → release (do-release: false) → docs │
   └──────────────────────────────────────────────┘
             │
             │  merge into main
             ▼
   ┌──────────────────────────────────────────────┐
   │  release.yml                                 │
   │  verify → release (do-release: true) → docs  │
   └──────────────────────────────────────────────┘

   scorecard.yml   — scheduled + on main (OpenSSF Scorecard)
   docs.yml        — manual re-deploy of the documentation site
```

The `verify → release → docs` chain is sequential on purpose: nothing is published unless the build
passed, and the documentation is deployed only after a successful publish.

## The callers

| Workflow | Trigger | Calls |
|---|---|---|
| `build.yml` | push to any branch except `main`/`snapshot`, and every PR | `reusable-verify.yml` |
| `snapshot.yml` | push to `snapshot` | `reusable-verify.yml` → `reusable-release.yml` (`do-release: false`) → `reusable-docs.yml` |
| `release.yml` | push to `main` | `reusable-verify.yml` → `reusable-release.yml` (`do-release: true`) → `reusable-docs.yml` |
| `docs.yml` | `workflow_dispatch` | `reusable-docs.yml` |
| `dependency-review.yml` | pull requests | `reusable-dependency-review.yml` |
| `scorecard.yml` | weekly schedule, push to `main`, branch-protection changes | `reusable-scorecard.yml` |

`dash-licenses.yml` is not part of this chain: it is `workflow_dispatch`-only and regenerates the
Eclipse `DEPENDENCIES` file on demand.

## What the reusables do

**`reusable-verify`** — the license headers gate everything: if
[skywalking-eyes](https://github.com/apache/skywalking-eyes) rejects a file, no build runs. Then a
matrix build over **Java 21 and 25** runs `./gradlew clean build testOSGi`, followed by `perfTest`.
It receives **no credentials**.

**`reusable-release`** — the only workflow that sees the publishing secrets, which the callers pass
with `secrets: inherit`. It runs `./gradlew build testOSGi release`; `do-release` decides whether
that is a snapshot or a signed release.

**`reusable-docs`** — builds the VitePress site in [`docs-site/`](../docs-site) and deploys it to
GitHub Pages under a versioned sub-path, `https://eclipse-fennec.github.io/emf.codec/<branch>/`,
plus a root redirect so `/emf.codec/` lands on the current line.

**`reusable-dependency-review`** / **`reusable-scorecard`** — dependency diff on pull requests, and
the OpenSSF Scorecard scan. Scorecard needs its scopes on the *calling* job, which is why
`scorecard.yml` declares job-level permissions rather than relying on the workflow ceiling.

## Permissions

Each caller sets a workflow-level ceiling, and the reusables cannot exceed it:

- `build.yml`, `dependency-review.yml` — read-only on contents (the latter may comment on PRs).
- `snapshot.yml`, `release.yml` — additionally `pages: write` and `id-token: write`, which only the
  docs deploy needs. The credential-scoped release step gets its secrets through `secrets: inherit`;
  verify and docs never see them.
- `scorecard.yml` — `read-all` at workflow level, with the scan's own scopes on the job.

## Published artifacts

Releases and snapshots are published to **Sonatype Central**, from which
releases sync to Maven Central. The group id is `org.eclipse.fennec.codec`.

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

## Secrets

Passed to `reusable-release` with `secrets: inherit`, so they are configured once for the
organisation rather than per repository:

| Secret name                          | Purpose                                  |
|--------------------------------------|------------------------------------------|
| `CENTRAL_SONATYPE_TOKEN_USERNAME`    | Sonatype Central user token              |
| `CENTRAL_SONATYPE_TOKEN_PASSWORD`    | Sonatype Central token password          |
| `GPG_PRIVATE_KEY`                    | ASCII-armored GPG private key            |
| `GPG_PASSPHRASE`                     | Passphrase for the private key           |
| `GPG_KEY_ID`                         | Long-form key id (used by the build)     |

Only `reusable-release` receives them; the verify and docs steps run without credentials. The GPG
keyring handling and secret masking live in that reusable workflow.

## Reproducing CI locally

* What `reusable-verify` runs:
  ```bash
  ./gradlew clean build testOSGi --info
  ./gradlew perfTest --info
  ```
* License headers:
  ```bash
  docker run --rm -v $(pwd):/github/workspace \
    ghcr.io/apache/skywalking-eyes/license-eye header check
  ```
* Documentation site:
  ```bash
  cd docs-site && npm ci && npm run build
  ```
* The release path cannot be reproduced locally: it publishes to Sonatype Central and requires the
  project signing key.
