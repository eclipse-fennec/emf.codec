# OSCAL Codec

`org.eclipse.fennec.codec.oscal` reads and writes [NIST OSCAL](https://pages.nist.gov/OSCAL/) JSON
documents as EMF models of `gov.nist.oscal.model` (Eclipse Fennec common models). All OSCAL models
are supported: catalog, profile, component-definition, system-security-plan, assessment-plan,
assessment-results, plan-of-action-and-milestones and mapping-collection.

## Setup

**OSGi.** Require the codec with `@RequireCodecOscal`. Its DS component registers a
`Resource.Factory` for the content type `application/oscal+json` once the OSCAL model and a
`MetadataService` are present:

```java
@Reference(target = "(emf.contentType=application/oscal+json)")
Resource.Factory oscalFactory;

Resource resource = resourceSet.createResource(uri, "application/oscal+json");
```

There is no file extension: OSCAL JSON files end in `.json`, which belongs to the generic codec.

**Plain Java.** The no-arg factory brings its own metadata service:

```java
Resource resource = new OscalResourceFactoryImpl().createResource(URI.createFileURI(path));
resource.load(in, Map.of());
Catalog catalog = ((DocumentRoot) resource.getContents().get(0)).getCatalog();
```

## The document

An OSCAL JSON document is one object with one member that names the model, optionally next to
`$schema`:

```json
{ "$schema": "…", "catalog": { "uuid": "…", "metadata": { … } } }
```

A loaded resource always holds the model's `DocumentRoot`: `getCatalog()`, `getProfile()`,
`getComponentDefinition()`, … return the model, `getSchema()` the `$schema` member. For saving,
the content may also be the bare model object (`Catalog`, `Profile`, …): it is written inside its
member. Any other content is rejected with an `IOException`.

## Mapping rules

| OSCAL JSON | Model | Note |
|---|---|---|
| `markup-line`, `markup-multiline` (`title`, `remarks`, `prose`, `description`, …) | `String` | Markdown kept exactly, parameter inserts (`insert: param`) included; no Markdown ↔ XHTML conversion |
| `prose` of `parts` | `Part.getProse()` | |
| date-time | `XMLGregorianCalendar` | all fraction digits kept; a zero offset is written as `Z` (`+00:00` reads the same instant) |
| `decimal`, `integer`, `nonNegativeInteger`, … | `BigDecimal`, `BigInteger` | exact, written as JSON numbers |
| `base64` value | `byte[]` | written as Base64 |
| `revisions` | `Metadata.getRevision()` | |
| a single object where OSCAL wants an array (`mappings`, also seen in invalid files) | list | read as a list of one, written as an array |
| a member OSCAL does not define | - | dropped, with a warning on the resource (`getWarnings()`) |

**Mapping direction.** A map entry of a mapping collection reads *source* `relationship`
*target* (NIST IR 8477): `subset-of` says the source is a subset of the target. Sources, targets
and the relationship are kept as written.

## OSCAL versions

The model is OSCAL 1.2.3. Documents of OSCAL 1.1.2, 1.1.3, 1.2.1 and 1.2.2 read and write without
loss; `metadata/oscal-version` is kept as it is.

## YAML

OSCAL defines YAML with the same structure. Pass a format provider:

```java
new OscalResourceImpl(uri, metadataService, null, new YamlFormatProvider());
```

There is no OSGi factory for YAML, so that the bundle does not depend on `codec.yaml`. Outside JSON
the content has to be the `DocumentRoot`.

## Tested with

- all OSCAL files of the BSI Stand-der-Technik-Bibliothek (Grundschutz++, WLAN,
  Lieferkettensicherheit, mappings, component definitions), also through YAML,
- the NIST OSCAL examples in JSON and YAML and NIST SP 800-53 rev5.

Each value is checked in its feature and the written document is compared with the original.
See `org.eclipse.fennec.codec.oscal/test-data/README.md` for the test data and its licences.
