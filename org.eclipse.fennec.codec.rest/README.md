# org.eclipse.fennec.codec.rest

JAX-RS (Jakarta REST) integration for the Fennec EMF codec. Provides message-body
readers/writers that serialize/deserialize EMF `Resource`s and `EObject`s using the codec, plus
annotations and filters that turn endpoint metadata and client requests into codec load/save
options.

## Collections of EObjects

A resource method may return, or take, a collection of `EObject`s (issue #334):

```java
@GET
@Produces(MediaType.APPLICATION_JSON)
public List<Booking> list() { ... }

@POST
@Consumes(MediaType.APPLICATION_JSON)
public void store(List<Booking> bookings) { ... }
```

- The handler is selected by the **generic** element type, so the collection must be declared as
  one: a method's return or parameter type, or a `GenericEntity<List<Booking>>` inside a `Response`.
  A raw `List` (e.g. `Response.ok(list)`) says nothing about its elements and is left to other providers.
- The elements become the roots of one document. In JSON and other formats with an array root this is
  always an array, also for one element (`[{...}]`) or none (`[]`) - save option `codec.rootAsArray`.
- An element that lives in a resource is copied, not moved: elements are copied together, so a
  reference from one to another stays inside the document, with its `xmi:id`. Detached elements are
  written live and detached again afterwards.
- Reading accepts an array or a single object; the result is an `ArrayList` (for `List`/`Collection`)
  or a `LinkedHashSet` (for `Set`). The objects stay in the resource they were loaded into, as for a
  single `EObject` (issue #246).

## Codec options on an endpoint (server-side)

A service developer fixes load/save options on a resource method or parameter with annotations:

```java
@GET
@Produces("text/csv")
@EMFResourceOptions(options = {
    @ResourceOption(key = "codec.tabular.referenceMode", value = "FLAT"),
    @ResourceOption(key = "serializeDefault", value = "true", valueType = Boolean.class)
})
public Person exportPerson(...) { ... }
```

On serialization these become save options; on a request-body endpoint they become load options.
`@CodecConfig` is a typed alternative mapping to `CodecOptions.CODEC_*` constants.

## Client-overridable options (per request)

A client may override a **whitelisted** subset of codec options per request via the
**`Codec-Options`** header — comma-separated `key=value` pairs:

```
GET /persons/1
Accept: text/csv
Codec-Options: codec.tabular.referenceMode=FLAT, codec.csv.dataTypeInSecondRow=false, serializeDefault=true
```

- The format is chosen by **content negotiation** (`Accept` / `Content-Type`); the header only
  tunes harmless rendering details — so one endpoint serves every format × reference-mode × toggle
  combination instead of a separate endpoint per combination.
- **Client values win** over the endpoint annotations for whitelisted keys.
- **Secure by default:** a key is overridable only if some module explicitly contributes it (see
  below). With no contributions, the header is ignored.

### Contributing overridable keys (per module)

Each module declares the keys it considers safe by registering a `RestOverridableCodecOptions`
service (in `org.eclipse.fennec.codec.api`) — referencing its own constants, no magic strings, and
no dependency on this REST bundle or on Jakarta:

```java
@Component
public class CsvOverridableCodecOptions implements RestOverridableCodecOptions {
    @Override public Map<String, Class<?>> overridableKeys() {
        return Map.of(
            CodecTabularOptions.OPTION_REFERENCE_MODE,      String.class,
            CodecCsvOptions.OPTION_DATA_TYPE_IN_SECOND_ROW, Boolean.class);
    }
}
```

`ClientCodecOptionsFilter` collects all such services, parses the header against the union
whitelist (coercing each value to its declared type), and the codec message-body reader/writer
merges the result into the EMF load/save options.

Keep options with a real blast radius (reference expansion, type strategy, custom value
reader/writer names) **out** of the contributed set.

## See also

- `docs/codec-rest-client-overridable-options.md` — full design, decisions, and as-built notes.
