# Client-overridable codec options over REST — design & implementation plan

**Status:** implemented (2026-06-02) — see §12 for the as-built notes
**Author:** Ilenia Salvadori
**Date:** 2026-06-02
**Module(s):** `org.eclipse.fennec.codec.rest`, `org.eclipse.fennec.codec.api`, plus a small contribution in each format bundle (`csv`, `ods`, `xlsx`, `rlang`, …)

---

## 1. Summary (the short version)

Today, the codec REST layer lets a service **developer** fix load/save options on an endpoint
through annotations (`@ResourceOption` / `@EMFResourceOptions` / `@CodecConfig`). A **client**
calling that endpoint cannot influence those options at request time.

We want to let clients override a **controlled subset** of options per request (e.g. ask for CSV
without the SQL-type row, or for `FLAT` reference mode), **without** opening every option to the
outside world (security) and **without** creating awkward module dependencies.

The plan:

- A **single** JAX-RS request filter in `codec.rest` reads the request, keeps only the
  **whitelisted** option keys, and hands the resulting option map to the codec message-body
  reader/writer, which merges it into the EMF `load`/`save` options.
- The whitelist is **not** hardcoded in `codec.rest`. Each module declares *its own* safe option
  keys by registering a small OSGi service (`RestOverridableCodecOptions`). The filter collects them
  all at runtime.

Result: clients get controlled, per-request configurability; security is enforced by an explicit,
per-module allow-list; and no module ends up with a dependency it shouldn't have.

---

## 2. Current state

Option flow today (verified in code):

- `@ResourceOption(key, value, valueType)` and the container `@EMFResourceOptions` are declared on
  a JAX-RS resource **method or parameter** (`annotations/ResourceOption.java`,
  `annotations/EMFResourceOptions.java`).
- In `BaseJakartaCodecMessageBodyReaderWriter`, both `writeResourceTo` and `readResourceFrom` build
  an options `Map` and call `handleAnnotedOptions(annotations, options, resourceSet, isWrite)`,
  then `resource.save(stream, options)` / `resource.load(stream, options)`.
- `handleAnnotedOptions` (via `AbstractCodecAnnotationHandler`) reads **only annotations**. The
  request's headers, the `MediaType` parameters, and the injected `ContainerRequestContext` are
  **not** consulted for options.
- `AbstractCodecAnnotationHandler.createValue(...)` parses the annotation's string `value()` into
  the declared `valueType()` (`String`, `Integer`, `Double`, `Long`, `Boolean`, `EClass`).

**Gap:** there is no path for client-supplied options. **Reusable asset:** `createValue`'s
string→typed parsing; the existing filter/registration pattern in `BasicResourceSetFilter`.

---

## 3. Goals / non-goals

**Goals**
- Allow clients to override a **whitelisted** subset of codec load/save options per request.
- Keep the whitelist owned by each module, expressed with that module's **own constants** (no
  magic strings), so allowed keys can't drift from the keys the code actually reads.
- Single, centralized merge point (no competing filters).
- Secure by default (empty whitelist ⇒ no client override possible).

**Non-goals (for this iteration)**
- Exposing *all* options to clients.
- A config-admin allow-list (can be layered on later; see §10).
- Changing the existing annotation mechanism (it stays; client values layer on top).

---

## 4. Design overview

```
HTTP request ──► [ClientCodecOptionsFilter]  (single, in codec.rest)
                        │  collects whitelists from all RestOverridableCodecOptions services
                        │  reads the "Codec-Options" request header, keeps only whitelisted keys,
                        │  parses values by declared type
                        ▼
              requestContext.setProperty(CLIENT_CODEC_OPTIONS, Map<String,Object>)
                        │
                        ▼
        [EMF*MessageBodyHandler] ──► BaseJakartaCodecMessageBodyReaderWriter
                        reads the request property and MERGES it into the
                        options map (after handleAnnotedOptions), then load()/save()

RestOverridableCodecOptions services (one per module, plain DS components):
   codec.api  → core options (ConfigProperty.* keys)
   codec.csv  → CodecCsvOptions.* / CodecTabularOptions.*
   codec.ods  → (its overridable keys)
   ...
```

Two responsibilities are deliberately **decoupled**:

| Concern | Needs jakarta? | Lives where |
|--------|----------------|-------------|
| The **filter** (reads request, merges) | yes | **single** component in `codec.rest` |
| The **whitelist** (which keys are safe) | no  | one tiny DS service **per owning module** |

Because option **keys are strings**, `codec.rest` never needs a compile dependency on
`codec.csv`/`codec.ods`/… — it discovers their allowed keys at runtime. Because the whitelist is a
plain OSGi service, the format bundles never need a dependency on jakarta.

---

## 5. The SPI: `RestOverridableCodecOptions`

New interface in **`org.eclipse.fennec.codec.api`** (already a dependency of every format bundle
and of `codec.rest`):

```java
package org.eclipse.fennec.codec.config; // or .../rest/spi if preferred

/**
 * Contributes the set of codec option keys that a module considers safe for a REST client to
 * override per request, together with each value's type (for parsing client-supplied strings).
 * Implementations are registered as OSGi services and collected by the REST layer.
 */
public interface RestOverridableCodecOptions {

    /**
     * @return option key → value type. The key MUST be the same constant the module's option
     *         resolver reads (e.g. CodecCsvOptions.OPTION_DELIMITER), so the allow-list cannot
     *         drift from the code. Value type drives parsing (String/Boolean/Integer/EClass/…).
     */
    Map<String, Class<?>> overridableKeys();
}
```

> Variant: return `Set<String>` if you don't want per-key typing and are happy to pass values as
> strings (the format providers already parse string option values). The `Map<String,Class<?>>`
> form is recommended because it lets the filter reuse the existing typed parsing and validate input.

Each module ships a small `@Component` implementation referencing **its own** constants:

```java
// org.eclipse.fennec.codec.csv
@Component
public class CsvOverridableOptions implements RestOverridableCodecOptions {
    @Override public Map<String, Class<?>> overridableKeys() {
        return Map.of(
            CodecCsvOptions.OPTION_DATA_TYPE_IN_SECOND_ROW, Boolean.class,
            CodecCsvOptions.OPTION_DELIMITER,               String.class,
            CodecTabularOptions.OPTION_REFERENCE_MODE,      String.class
        );
    }
}
```

```java
// org.eclipse.fennec.codec.api (core options)
@Component
public class CoreOverridableOptions implements RestOverridableCodecOptions {
    @Override public Map<String, Class<?>> overridableKeys() {
        return Map.of(
            ConfigProperty.SERIALIZE_DEFAULT.getKey(), Boolean.class,
            ConfigProperty.SERIALIZE_NULL.getKey(),    Boolean.class,
            ConfigProperty.ENUM_SERIALIZATION.getKey(), String.class,
            ConfigProperty.FIELD_ORDER.getKey(),        String.class,
            ConfigProperty.ID_ON_TOP.getKey(),          Boolean.class
        );
    }
}
```

---

## 6. The filter: `ClientCodecOptionsFilter` (single, in `codec.rest`)

Mirrors `BasicResourceSetFilter` (DS `@Component`, `@JakartarsExtension`, `ContainerRequestFilter`).

```java
@Component
@JakartarsExtension
@JakartarsName("ClientCodecOptionsFilter")
@ServiceRanking(1)
public class ClientCodecOptionsFilter implements ContainerRequestFilter {

    // Collect every module's contribution; dynamic, greedy.
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    private volatile List<RestOverridableCodecOptions> contributions;

    static final String HEADER = "Codec-Options";

    @Override
    public void filter(ContainerRequestContext ctx) {
        Map<String, Class<?>> whitelist = unionOf(contributions);     // key → type
        if (whitelist.isEmpty()) return;

        List<String> headerValues = ctx.getHeaders().get(HEADER);     // case-insensitive lookup
        if (headerValues == null || headerValues.isEmpty()) return;

        Map<String, Object> clientOptions = new HashMap<>();
        // One header, comma-separated "key=value" pairs (values may repeat across multiple headers).
        for (String headerValue : headerValues) {
            for (String pair : headerValue.split(",")) {
                int eq = pair.indexOf('=');
                if (eq <= 0) continue;
                String key = pair.substring(0, eq).trim();
                String raw = pair.substring(eq + 1).trim();
                Class<?> type = whitelist.get(key);                   // only whitelisted keys pass
                if (type != null) {
                    clientOptions.put(key, parse(raw, type));         // reuse createValue logic
                }
            }
        }
        if (!clientOptions.isEmpty()) {
            ctx.setProperty(JakartaRestConstants.CLIENT_CODEC_OPTIONS, clientOptions);
        }
    }
}
```

Notes:
- **Request channel:** a single `Codec-Options` request header carrying comma-separated
  `key=value` pairs, where each `key` is the full option key (matched against the whitelist), e.g.

  ```
  GET /persons/1
  Accept: text/csv
  Codec-Options: codec.csv.dataTypeInSecondRow=false, codec.tabular.referenceMode=FLAT
  ```

  Headers were chosen over query params to keep the URL clean. One header keeps all codec tuning in
  one place; HTTP header names are case-insensitive, so the `Codec-Options` lookup is robust. (Query
  params or `MediaType` parameters can be added later as alternative channels — see §10.)
- **Unknown / non-whitelisted keys are ignored**, never forwarded.
- Parsing reuses the same logic as `AbstractCodecAnnotationHandler.createValue` — extract it into a
  shared helper (e.g. `CodecOptionValues.parse(String, Class<?>)`) so both the annotation path and
  the client path use one implementation.

---

## 7. Merge into load/save options

`EMFResourceMessageBodyHandler` / `EObjectMessageBodyHandler` already inject
`Provider<ContainerRequestContext>`. They read the request property and pass it into the base:

```java
Map<String,Object> clientOptions =
    (Map<String,Object>) requestContextProvider.get().getProperty(CLIENT_CODEC_OPTIONS);
super.readResourceFrom(..., clientOptions);   // new param; null-safe
```

In `BaseJakartaCodecMessageBodyReaderWriter.{readResourceFrom,writeResourceTo}`, after the existing
`handleAnnotedOptions(...)` call:

```java
handleAnnotedOptions(annotations, options, resourceSet, isWrite);
if (clientOptions != null) options.putAll(clientOptions);   // precedence policy below
```

**Precedence policy (decision):** *client wins for whitelisted keys.* Rationale: whitelisting a key
is the explicit decision to let clients set it, so `putAll` after the annotations is correct.
(Alternative — annotations always win — is a one-line change: only put keys absent from `options`.)

---

## 8. Security

- **Secure by default:** with no `RestOverridableCodecOptions` services registered (or empty maps), the
  whitelist is empty and no client override is possible.
- **Explicit, owned allow-list:** a key is overridable only because the module that owns it said so,
  in code, using its own constant.
- **Blast-radius awareness:** keep risky keys *out* of the whitelist — e.g. `expand` / `expandDepth`
  (response amplification), `typeStrategy`, value reader/writer names. Document this guidance in the
  SPI javadoc.
- **Typed parsing/validation:** the key→type map means malformed client input fails fast in
  `parse(...)` rather than reaching the codec.

---

## 9. Implementation steps

1. **`codec.api`**
   - Add `RestOverridableCodecOptions` interface.
   - (Optional) add `CoreOverridableOptions` `@Component` for core `ConfigProperty` keys.
2. **`codec.rest`**
   - Add `JakartaRestConstants.CLIENT_CODEC_OPTIONS` request-property key.
   - Extract the string→type parsing out of `AbstractCodecAnnotationHandler.createValue` into a
     shared, reusable helper.
   - Add `ClientCodecOptionsFilter` (collects services, reads the `Codec-Options` header, sets request property).
   - Thread an optional `Map<String,Object> clientOptions` param through
     `BaseJakartaCodecMessageBodyReaderWriter.readResourceFrom/writeResourceTo`; merge after
     `handleAnnotedOptions`.
   - Update `EMFResourceMessageBodyHandler` and `EObjectMessageBodyHandler` to read the request
     property and pass it down.
3. **Format bundles** (`csv`, `ods`, `xlsx`, `rlang`)
   - Add one `@Component implements RestOverridableCodecOptions` per bundle, listing that bundle's safe
     keys via its own constants.
4. **Docs**
   - Note the feature + the `Codec-Options` header channel in the REST module README / codec dev guide.

No new dependency is introduced between `codec.rest` and the format bundles, nor between the format
bundles and jakarta.

---

## 10. Future extensions

- **Config-Admin allow-list (layer A):** add an OCD-configured `String[] allowedOptionKeys` to the
  filter and union it with the SPI contributions, so ops can toggle keys without code.
- **Additional request channels:** support query params and/or `MediaType` parameters
  (`Accept: text/csv; referenceMode=FLAT`) in addition to the `Codec-Options` header.
- **Per-endpoint opt-out:** an annotation to disable client overrides for a specific resource method.

---

## 11. Decisions (locked — boss-approved 2026-06-02)

1. **SPI return type:** `Map<String, Class<?>>` (typed) — enables reuse of the existing
   string→type parsing and fail-fast validation.
2. **Request channel:** a single **`Codec-Options` request header** with comma-separated
   `key=value` pairs. Chosen over query params to keep URLs clean. (Other channels deferred — §10.)
3. **Precedence:** **client wins for whitelisted keys** (client values applied after annotation
   options) — whitelisting a key *is* the decision to let clients set it.
4. **SPI location & name:** interface **`RestOverridableCodecOptions`** in `org.eclipse.fennec.codec.api`.
   Kept in `codec.api` (so format bundles needn't depend on a REST module); named with the `Rest`
   prefix to make its REST-client-override intent unambiguous.

---

## 12. As-built (implemented 2026-06-02)

- **`codec.api`** — `config/RestOverridableCodecOptions` interface (`Map<String,Class<?>> overridableKeys()`).
- **`codec.rest`**
  - `jakartas/JakartaRestConstants` — `CLIENT_CODEC_OPTIONS` (request-property key) and
    `CODEC_OPTIONS_HEADER = "Codec-Options"`.
  - `common/CodecOptionValues.parse(String, Class<?>)` — extracted from
    `AbstractCodecAnnotationHandler.createValue` (which now delegates to it; `EClass` still
    special-cased there since it needs the `ResourceSet`).
  - `jakartas/filter/ClientCodecOptionsFilter` — `@Component @JakartarsExtension` request filter;
    `@Reference(MULTIPLE, DYNAMIC) List<RestOverridableCodecOptions>` for the whitelist; parses the
    `Codec-Options` header (pure static `parseClientOptions(...)`, unit-tested) and sets the request
    property.
  - `BaseJakartaCodecMessageBodyReaderWriter` — `getClientCodecOptions()` hook (default empty);
    `options.putAll(getClientCodecOptions())` after `handleAnnotedOptions` in **both** read and write
    paths (client wins). `EMFResourceMessageBodyHandler` / `EObjectMessageBodyHandler` override the
    hook to read the request property via their injected `ContainerRequestContext`.
- **Per-module contributors** (`@Component implements RestOverridableCodecOptions`, own constants):
  `codec` → `options/CoreOverridableCodecOptions` (serializeNull/Empty/Default, enumSerialization,
  fieldOrder, idOnTop, dateFormat); `codec.csv` → `CsvOverridableCodecOptions` (referenceMode +
  CSV dialect + dataTypeInSecondRow); `codec.ods` / `codec.xlsx` / `codec.rlang` → their rendering
  knobs + referenceMode.
- **Tests:** `ClientCodecOptionsFilterTest` (whitelist filtering, typing, tolerant parsing,
  secure-by-default). Full `./gradlew build` green.
- No new dependency between `codec.rest` and the format bundles; no jakarta dependency added to any
  format bundle.
