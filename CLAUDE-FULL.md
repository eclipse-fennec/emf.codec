# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Build and Testing
- `./gradlew build` - Build all projects
- `./gradlew clean` - Clean build artifacts
- `./gradlew testOSGi` - Run OSGi integration tests for all projects
- `./gradlew codeCoverageReport` - Generate Jacoco coverage report (outputs to `build/reports/jacoco/codeCoverageReport/`)

### Single Test Execution
Individual test projects can be tested using their `.bndrun` files:
- Test bundles are located in `*/test.bndrun` files
- Generated test runners are in `*/generated/test.bndrun`
- Use `./gradlew :project-name:testOSGi` for specific project tests

### Quality Checks
- `./gradlew sonar` - Run SonarCloud analysis (requires SONAR_TOKEN)
- License check: `docker run -it --rm -v $(pwd):/github/workspace ghcr.io/apache/skywalking-eyes/license-eye header check`

### Release Commands
- `./gradlew release` - Create release build (main branch only)
- Release artifacts go to `cnf/release/`

## Project Architecture

### Core Framework Structure
This is an Eclipse EMF codec framework built on Jackson, providing a common abstraction layer for serializing/deserializing EMF EObjects to various formats (JSON, MongoDB, etc.). The architecture follows a modular OSGi design.

#### Key Components
- **org.eclipse.fennec.codec** - Core codec framework and base classes
- **org.eclipse.fennec.codec.json** - JSON implementation using Jackson
- **org.eclipse.fennec.codec.mongo** - MongoDB persistence implementation
- **org.eclipse.fennec.codec.jsonschema** - JSON Schema to/from EMF EPackage conversion
- **org.eclipse.fennec.codec.csv** - CSV format serialization
- **org.eclipse.fennec.codec.info** - Model annotation processing and codec metadata
- **org.eclipse.fennec.codec.constants** - Shared constants and annotations

#### Configuration System
The framework uses three main configurators:
1. **CodecFactoryConfigurator** - Sets up JsonFactory with streaming features
2. **ObjectMapperConfigurator** - Configures Jackson ObjectMapper features
3. **CodecModuleConfigurator** - Manages codec-specific serialization options

#### Serialization Flow
1. `CodecResource` combines all configurators with runtime options
2. `CodecEObjectSerializer`/`CodecEObjectDeserializer` handle EMF object processing
3. Multiple specialized `CodecInfoSerializer` implementations handle different aspects (ID, type, features, references)
4. `CodecGeneratorBaseImpl`/`CodecParserBaseImpl` provide persistence-agnostic base layer

### EMF Model Projects
- **org.eclipse.fennec.codec.demo.model** - Example Person model
- **org.eclipse.fennec.codec.test.models** - Test models with annotations, arrays, generics
- **org.eclipse.fennec.dragino.message.model** - LoRaWAN Dragino device models
- **org.eclipse.fennec.em310udl.mesage.model** - EM310 UDL sensor models
- **org.eclipse.fennec.lorawan.uplink.model** - LoRaWAN uplink message models

### Testing Infrastructure
- **org.eclipse.fennec.codec.test.helper** - Common test utilities and base classes
- Each implementation has corresponding `.test` project with OSGi integration tests
- Tests use `.bndrun` files to define OSGi runtime requirements

## Development Notes

### Build System
- Uses BND/Gradle with OSGi bundle management
- Maven dependencies are managed through `cnf/central.mvn`
- Local artifacts cached in `cnf/local/`
- Bundle versions follow semantic versioning with SNAPSHOT suffixes

### Code Annotations
The framework supports extensive codec annotations for customizing serialization:
- `@CODEC_TRANSIENT` - Skip serialization of features
- `@CODEC_ID_STRATEGY` - Configure ID generation (ID_FIELD, COMBINED)
- `@CODEC_TYPE_INCLUDE`/`@CODEC_TYPE_USE` - Control type information serialization
- See `CodecAnnotations.java` for complete list

### Testing Approach
- Unit tests in `*/test/` directories using JUnit 5
- OSGi integration tests via `.bndrun` runtime definitions
- Test data in `*/test-data/` directories
- Use TestOSGi gradle task for full integration testing

### Jackson 3.x Migration
The codebase has been migrated to Jackson 3.x (tools.jackson.*) - be aware of package name changes from com.fasterxml.jackson.* in older versions.

### Key Options Classes
When working with save/load operations, the main option classes are:
- `org.eclipse.fennec.codec.constants.ObjectMapperOptions` - Jackson ObjectMapper settings
- `org.eclipse.fennec.codec.constants.CodecModuleOptions` - Codec module serialization settings
- `org.eclipse.fennec.codec.constants.CodecModelInfoOptions` - Per-EClass codec settings
- `org.eclipse.fennec.codec.constants.CodecResourceOptions` - Resource-level options (e.g., CODEC_ROOT_OBJECT for deserialization)

Use `CodecOptionsBuilder` to construct options maps:
```java
Map<String, Object> options = CodecOptionsBuilder
    .create()
    .forClass(PersonPackage.eINSTANCE.getPerson())
    .idStrategy("ID_FIELD")
    .build();
```

---

## Codec Development (Active)

The new codec implementation. For full context, see:

- **`docs/codec-v2-development-guide.md`** - Current state, next steps, and session continuity
- **`docs/codec-v2-spec/`** - Complete specification (source of truth)
- **`org.eclipse.fennec.model.metadata/model-metadata-architecture.md`** - MetadataService, Aspect pattern
- **`org.eclipse.fennec.codec.metadata/codec-metadata-architecture.md`** - Codec aspects, EAnnotation mapping

### Core Projects

| Project | Purpose |
|---------|---------|
| `org.eclipse.fennec.codec` | Codec runtime (serialization/deserialization) |
| `org.eclipse.fennec.codec.api` | Configuration API |
| `org.eclipse.fennec.codec.metadata` | Codec-specific aspects and annotation parsing |
| `org.eclipse.fennec.model.metadata` | Generic MetadataService infrastructure |

### Key Concepts

1. **MetadataService**: Dynamic component that parses EAnnotations when EPackages are registered and creates pre-computed aspect objects
2. **Aspects**: EMF-based configuration objects attached to ClassMetadata/FeatureMetadata
3. **Configuration Hierarchy**: 6 levels from Load/Save options (highest) to Built-in defaults (lowest)

### Annotation Source

All codec annotations use source: `http://eclipse.org/fennec/codec` with markers:
- `codec.type`, `codec.id`, `codec.reference`, `codec.supertype`

### Current Focus

Building annotation parsing in `org.eclipse.fennec.codec.metadata` that:
1. Reads EAnnotations from EPackage/EClass/EStructuralFeature
2. Applies spec-defined defaults when annotations are missing
3. Creates ClassCodecAspect, FeatureCodecAspect, ReferenceCodecAspect objects
- java comments annotation with @claude or @CLAUDE are instructions for you, similar like a code review comment. please look at them
- dont use full qualified class names in code. we use import instead
- always look in the spec first, if you dont know how to use the codec. the spec should be our source of truth / user manual. if it isnt, we have to make it more clearer

---

## Development Workflow (MUST FOLLOW)

### Codec Testing (CRITICAL)
- **Use `./gradlew :org.eclipse.fennec.codec:test` for codec tests** - plain JUnit 5
- **Do NOT use `testOSGi` for new codec projects** - OSGi tests are for the old codec only
- Other projects: `:org.eclipse.fennec.codec.metadata:test`, `:org.eclipse.fennec.model.metadata:test`

### Workflow Rules

1. **Specification First (ALWAYS)**
   - **NEVER check codebase before checking spec** - spec is the source of truth
   - Always check the spec (`docs/codec-v2-spec/`) before implementing OR investigating
   - For any question (error handling, behavior, configuration) → check spec FIRST
   - If spec is unclear, clarify it first - don't reverse-engineer from code
   - Never implement something that contradicts the spec
   - **Follow links in spec documents** - when you see a matrix with ✅/❌ entries, follow the link to the detailed section to understand the WHY
   - **If the WHY isn't clear quickly** - this is a warning sign that the spec lacks clarity. Ask the user or flag as a spec improvement, don't dig into implementation code to reverse-engineer the reasoning
   - **Key spec documents:**
     - `15-error-handling.md` - Error handling, diagnostics, logging
     - `16-annotation-reference.md` - All annotation keys and valid levels
     - `08-discriminator-mapping.md` - Discriminator and inline mappings

2. **Test-Driven Development**
   - Create/update tests BEFORE implementation
   - Tests must FAIL before implementation (proves test is valid)
   - Tests must PASS after implementation
   - Spec tests verify spec compliance - they define expected behavior

3. **Failing Tests = Investigation Required**
   - NEVER change tests just to make them pass
   - Failing tests are signals - investigate the root cause:
     - Implementation bug?
     - Unintended side effect from another change?
     - Spec gap that needs clarification?
   - Even if the failing test seems unrelated, investigate - it may reveal important side effects

4. **Contradictions & Uncertainty (IMPORTANT)**
   - When you detect a **contradiction** between spec, implementation, and tests - **ASK the user early**
   - If investigation takes more than **2-3 search/read cycles** without clarity - **stop and ask**
   - **If you become unsure during investigation** - stop and ask immediately
     - Don't continue down a potentially wrong path
     - Wrong assumptions compound and waste context
   - Present the issue clearly:
     - "Spec says X, but implementation does Y, and test expects Z"
     - "I found conflicting information in [files]"
     - "I'm unsure whether [A] or [B] is the intended behavior"
   - Offer options:
     - "Would you like me to investigate further?"
     - "Can you clarify which behavior is intended?"
     - "Should I update the spec/implementation/test to align?"
   - **Don't spend 10+ minutes investigating when you could get a hint in seconds**
   - **Staying on the right track is more valuable than exhaustive investigation**

5. **Code Quality**
   - Use imports, NEVER fully qualified class names
   - Look for `@claude` or `@CLAUDE` comments - these are instructions
   - Review for performance and Java best practices

6. **Cross-Project Changes**
   - Update spec FIRST
   - Check consistency with existing behavior
   - Then test, then implement

7. **Session Handoff**
   - Update `docs/codec-v2-development-guide.md` at session end
   - Document: what was done, what's next, any blockers

8. **Package Migration (COMPLETED)**
   - Old `codec.v2.*` packages have been migrated to `codec.*` and deleted
   - Old codec projects moved to `old/` folder (reference only)
   - All active code lives in `org.eclipse.fennec.codec.*` packages