This directory defines the high-level concepts, business logic, and architecture of this project using markdown. It is managed by [lat.md](https://www.npmjs.com/package/lat.md) — a tool that anchors source code to these definitions. Install the `lat` command with `npm i -g lat.md` and run `lat --help`.

Fennec Codec is an EMF codec framework built on Jackson 3.x, serializing EMF EObjects to JSON, YAML, CBOR, BSON, tabular formats, and more. The normative behavior spec lives in `docs/codec-v2-spec/`; this lattice captures the architecture, design decisions, and domain vocabulary around it.

- [[concepts]] — Domain vocabulary: EMF/Ecore terms, serialization targets, metadata fields, strategy enums, aspects, value readers/writers
- [[architecture]] — Two-layer architecture, codec runtime, ser/deser flows, metadata service, deferred properties
- [[configuration]] — Source hierarchy × scope chain resolution, naming conventions, load/save options, REST overrides
- [[formats]] — Format abstraction and the format landscape: streaming, tabular, schema formats, REST integration
- [[decisions]] — Key design decisions and their rationale (spec-first, shadow metadata model, immutable config, format parity, …)
- [[projects]] — Bundle map: layering, core/format/extension bundles, test projects, branches
- [[testing]] — Test layers, the shared TCK, OSGi integration tests, the playground bench
