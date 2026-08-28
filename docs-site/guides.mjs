// The published, user-facing docs (allowlist). Shared by the sync script and the
// VitePress config so the set and its order are defined exactly once.
//   file  — source markdown under ../docs (may live in a sub-folder, e.g. codec-v2-spec/)
//   title — sidebar / nav label
//   group — sidebar section the entry belongs to
//
// Unlike emf.m2x there is no `slug`: this site covers a single topic (the codec),
// so the route name is derived from the file's base name (see `slugFor`). The
// numbered spec files therefore keep their natural order (00-…, 01-…, …).
export const GUIDES = [
  // The specification — the source of truth, numbered and ordered.
  { file: 'codec-v2-spec/00-overview.md', title: '00 · Overview', group: 'Specification' },
  { file: 'codec-v2-spec/01-architecture.md', title: '01 · Architecture', group: 'Specification' },
  { file: 'codec-v2-spec/02-config-resolution.md', title: '02 · Configuration Resolution', group: 'Specification' },
  { file: 'codec-v2-spec/03-naming-conventions.md', title: '03 · JSON Key Configuration', group: 'Specification' },
  { file: 'codec-v2-spec/04-common-types.md', title: '04 · Common Types & Enumerations', group: 'Specification' },
  { file: 'codec-v2-spec/05-global-options.md', title: '05 · Global Options', group: 'Specification' },
  { file: 'codec-v2-spec/06-type.md', title: '06 · Type Serialization', group: 'Specification' },
  { file: 'codec-v2-spec/07-supertype.md', title: '07 · SuperType Serialization', group: 'Specification' },
  { file: 'codec-v2-spec/08-discriminator-mapping.md', title: '08 · Discriminator Mapping', group: 'Specification' },
  { file: 'codec-v2-spec/09-id.md', title: '09 · ID Serialization', group: 'Specification' },
  { file: 'codec-v2-spec/10-reference.md', title: '10 · Reference Serialization', group: 'Specification' },
  { file: 'codec-v2-spec/11-feature.md', title: '11 · Feature Serialization', group: 'Specification' },
  { file: 'codec-v2-spec/12-polymorphism.md', title: '12 · Polymorphism & Inheritance', group: 'Specification' },
  { file: 'codec-v2-spec/13-load-save-options.md', title: '13 · Load/Save Options', group: 'Specification' },
  { file: 'codec-v2-spec/14-custom-values.md', title: '14 · Custom Value Readers/Writers', group: 'Specification' },
  { file: 'codec-v2-spec/15-error-handling.md', title: '15 · Error Handling & Diagnostics', group: 'Specification' },
  { file: 'codec-v2-spec/16-annotation-reference.md', title: '16 · Annotation & Configuration Reference', group: 'Specification' },
  { file: 'codec-v2-spec/17-format-abstraction.md', title: '17 · Format Abstraction', group: 'Specification' },
  { file: 'codec-v2-spec/18-scenarios.md', title: '18 · Configuration Scenarios', group: 'Specification' },
  { file: 'codec-v2-spec/19-test-coverage.md', title: '19 · Test Coverage & Expectations', group: 'Specification' },
  { file: 'codec-v2-spec/20-code-conventions.md', title: '20 · Code Conventions & Patterns', group: 'Specification' },

  // Standalone references.
  { file: 'codec-options-reference.md', title: 'Codec Options Reference', group: 'References' },
  { file: 'codec-v2-reference.md', title: 'Codec V2 Reference', group: 'References' },
  { file: 'tabular-exporter-examples.md', title: 'Tabular Exporter Examples', group: 'References' },
];

// Route name for a guide: the file's base name without the .md extension.
// e.g. 'codec-v2-spec/06-type.md' -> '06-type', served at /guides/06-type.
export function slugFor(file) {
  return file.replace(/^.*\//, '').replace(/\.md$/, '');
}
