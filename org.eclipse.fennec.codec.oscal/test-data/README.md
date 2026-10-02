# OSCAL test data

| Folder | Content | Licence | Committed |
|---|---|---|---|
| `fixtures/` | Small OSCAL documents written for the tests: members the real files do not use, markup edge cases, `$schema`, a single `mappings` object. `fixtures/numbers/` holds the documents of `OscalNumberAndBinaryTest`. | EPL-2.0 (this project) | yes |
| `nist/` | The OSCAL examples of NIST, one or more per model (catalog, profile, component-definition, SSP, assessment plan and results, POA&M), OSCAL 1.1.2 to 1.2.2. | CC0 1.0 / public domain ([usnistgov/oscal-content](https://github.com/usnistgov/oscal-content/blob/main/LICENSE.md)) | yes |
| `nist-large/` | NIST SP 800-53 rev5 catalog (10 MB), for the performance test. | CC0 1.0 / public domain | no |
| `bsi/` | All OSCAL files of the BSI Stand-der-Technik-Bibliothek (Grundschutz++, WLAN, Lieferkettensicherheit, Mappings, component definitions). | CC BY-SA 4.0 | no |

`nist/`, `nist-large/` and `bsi/` each have a `COMMIT` file with the commit of the source repository the files come from.

## Getting the local data

The tests that need `bsi/` or `nist-large/` are skipped when the folder is missing
(`BsiRoundTripTest`, `GrundschutzContentTest`, parts of `OscalPerformanceTest` and
`NistRoundTripTest`). To run them, run this from `org.eclipse.fennec.codec.oscal`:

```bash
# BSI Stand-der-Technik-Bibliothek, CC BY-SA 4.0
git clone https://github.com/BSI-Bund/Stand-der-Technik-Bibliothek.git /tmp/bsi
git -C /tmp/bsi checkout a12831136f4122a4c43854622180a24f2576a41e
mkdir -p test-data/bsi
(cd /tmp/bsi && find control_layer implementation_layer -name '*.json' -print0 \
  | xargs -0 cp --parents -t "$OLDPWD/test-data/bsi")
git -C /tmp/bsi rev-parse HEAD > test-data/bsi/COMMIT

# NIST SP 800-53 rev5, CC0
mkdir -p test-data/nist-large
curl -fL -o test-data/nist-large/NIST_SP-800-53_rev5_catalog.json \
  https://raw.githubusercontent.com/usnistgov/oscal-content/78650f02ad9321bb7b817846f8fbd4f2bcd620de/nist.gov/SP800-53/rev5/json/NIST_SP-800-53_rev5_catalog.json
```

The system property `oscal.bsi.dir` points the BSI tests to another folder.

A newer BSI commit may change the files. `BsiRoundTripTest` then still has to pass, apart from
the documented deviations of invalid files. `GrundschutzContentTest` also checks a few literal
values of control GC.1.1, which may need an update.
