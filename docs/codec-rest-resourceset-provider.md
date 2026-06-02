# Codec REST ResourceSet Provider

The `org.eclipse.fennec.codec.rest` bundle exposes the EMF `ResourceSet`
used by the codec message-body handlers as a request-scoped JAX-RS
`@Context` injection. The provider behind that injection is overridable via
the normal Jakarta RS Whiteboard mechanism — publish a higher-ranked OSGi
service implementing `ResourceSetProvider` and the codec rebinds to it
transparently.

## How it self-assembles

```
                       OSGi service registry
                  +-----------------------------+
                  | ResourceSetProvider (default) |  service.ranking = 0
                  | ResourceSetProvider (custom)  |  service.ranking = 100
                  +-----------------------------+
                                  |
                                  |  DYNAMIC, GREEDY @Reference
                                  v
                  +-----------------------------------+
                  |       CodecResourceSetFeature      |
                  |   (singleton JAX-RS extension)    |
                  +-----------------------------------+
                                  |
                  configure() -> Jersey AbstractBinder
                                  |
                                  v
                  ResourceSet.class bound in RequestScoped
                  via CodecResourceSetSupplier
                                  |
                                  v
                  +-----------------------------------+
                  |   @Context ResourceSet  /          |
                  |   @Context Provider<ResourceSet>   |
                  +-----------------------------------+
                                  |
                  response written, then
                                  v
                  CodecResourceSetCleanupFilter
                  calls provider.releaseResourceSet(...)
```

* `CodecResourceSetFeature` is a singleton JAX-RS `Feature`, registered as a
  Whiteboard extension targeting EMF-flagged applications
  (`@JakartarsApplicationSelect("(|(emf=true)(osgi.jakartars.name=.default))")`).
* It holds a `DYNAMIC` / `GREEDY` reference to `ResourceSetProvider`. When a
  higher-ranked provider appears in the OSGi service registry, the
  framework rebinds without restarting the feature.
* In `configure(FeatureContext)` the feature registers a Jersey
  `AbstractBinder` that binds `ResourceSet.class` in `RequestScoped` via
  `CodecResourceSetSupplier`.
* `CodecResourceSetSupplier` calls `provider.getResourceSet(ctx)` once per
  request and stashes the produced `ResourceSet` (plus the active provider)
  on the request context.
* `CodecResourceSetCleanupFilter` (a `ContainerResponseFilter`) reads the
  stashed values after the response is written and calls
  `provider.releaseResourceSet(rs, ctx)`. Jersey's
  `DisposableSupplier.dispose()` is not reliably called for proxied
  bindings, so the cleanup is implemented as an explicit response filter.

## The default provider

`DefaultResourceSetProvider` is shipped with the codec. It wraps the OSGi
`ResourceSetFactory` and returns a fresh `ResourceSet` per request; its
`releaseResourceSet` is a no-op. It is registered with the default service
ranking, so any non-zero-ranked override wins.

## Overriding the provider

To plug in a scoped `ResourceSet` (e.g. resolving from path parameters or
leasing from a `ComponentServiceObjects<ResourceSet>`), publish a new
`ResourceSetProvider` OSGi service with a higher `service.ranking`. The
codec's feature will rebind to it on the next service event.

Minimal sketch:

```java
@Component(service = ResourceSetProvider.class)
@ServiceRanking(100)
public class MyScopedResourceSetProvider implements ResourceSetProvider {

    private static final String LEASED_CSO = "myProvider.cso";

    @Reference
    MyScopeRegistry registry;

    @Override
    public ResourceSet getResourceSet(ContainerRequestContext ctx) {
        String scopeName = ctx.getUriInfo().getPathParameters().getFirst("scopeName");
        ComponentServiceObjects<ResourceSet> cso = registry.lookup(scopeName);
        if (cso == null) {
            throw new WebApplicationException(Response.status(400)
                    .entity("Unknown scope: " + scopeName).build());
        }
        ResourceSet rs = cso.getService();
        ctx.setProperty(LEASED_CSO, cso);
        return rs;
    }

    @Override
    public void releaseResourceSet(ResourceSet rs, ContainerRequestContext ctx) {
        @SuppressWarnings("unchecked")
        ComponentServiceObjects<ResourceSet> cso =
                (ComponentServiceObjects<ResourceSet>) ctx.getProperty(LEASED_CSO);
        if (cso != null) {
            cso.ungetService(rs);
        }
    }
}
```

That is the entire override surface — no JAX-RS extensions to register, no
feature subclassing, no filter chain. The `CodecResourceSetFeature` and
`CodecResourceSetCleanupFilter` continue to provide the JAX-RS wiring and
the deterministic release hook.

### When to override

* Multi-tenant systems where the `ResourceSet` is keyed by a path parameter
  (scope, stage, root folder, etc.).
* Pooled `ResourceSet` instances obtained from a
  `ComponentServiceObjects<ResourceSet>` that must be ungot after each
  request.
* Caching strategies where the same `ResourceSet` is reused across
  requests of the same client/session.

Provider implementations are plain OSGi services — they do not see JAX-RS
`@Context` injection themselves. The `ContainerRequestContext` passed to
`getResourceSet` / `releaseResourceSet` exposes everything the provider
needs (`UriInfo`, headers, properties).

## Consuming the ResourceSet

In any JAX-RS resource or provider that runs in an EMF-targeted
application, declare:

```java
@Context
ResourceSet resourceSet;

// or, lazily:
@Context
jakarta.inject.Provider<ResourceSet> resourceSetProvider;
```

The injected `ResourceSet` is the same instance for the lifetime of a
single request; it is created lazily on first use (so a request that never
touches it does not pay for the provider call and never triggers
`releaseResourceSet`).

The codec's own `MessageBodyReader`/`MessageBodyWriter` implementations
(`EObjectMessageBodyHandler`, `EMFResourceMessageBodyHandler`) use this
mechanism internally — they no longer rely on a `ContainerRequestFilter`
to push a `ResourceSetFactory` into a request property.

## Migration from the previous mechanism

The previous mechanism wired things via `BasicResourceSetFilter` (a
`ContainerRequestFilter`) that pushed a `ResourceSetFactory` onto the
request under
`JakartaRestConstants.RESOLVED_RESOURCE_SET_FACTORY`; handlers then read
it back via `@Context Provider<ContainerRequestContext>` and called
`createResourceSet()` themselves. Both the filter and the constants
interface are removed in favor of the provider SPI described here.

Downstream consumers that previously **shadowed** the
`BasicResourceSetFilter` (by reusing the same `@JakartarsName` with a
higher service ranking) should migrate to publishing a
`ResourceSetProvider` instead. The override semantics are the same — only
the seam moved from a request-filter property to a request-scoped
injection.
