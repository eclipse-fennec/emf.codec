# Fennec Codec for Plain Java

This module provides a simple way to use the Fennec EMF Codec in plain Java applications without OSGi.

## Overview

The Fennec Codec serializes and deserializes EMF EObjects to/from JSON using Jackson 3.x. This module wraps the codec components and provides a simple API for non-OSGi environments.

## Quick Start

### 1. Basic Setup

```java
import org.eclipse.fennec.codec.java.CodecSetup;

// Create codec setup and register your EPackage(s)
CodecSetup codecSetup = new CodecSetup();
codecSetup.registerEPackage(MyModelPackage.eINSTANCE);
```

### 2. Serialization (Save)

```java
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import java.io.ByteArrayOutputStream;

// Create your EObject
Person person = MyModelFactory.eINSTANCE.createPerson();
person.setName("John");
person.setAge(30);

// Create ResourceSet and Resource
ResourceSet rs = codecSetup.createResourceSet();
Resource resource = rs.createResource(URI.createURI("person.json"));
resource.getContents().add(person);

// Serialize to OutputStream
ByteArrayOutputStream baos = new ByteArrayOutputStream();
resource.save(baos, null);

String json = baos.toString(StandardCharsets.UTF_8);
// {"_id":"John","_type":"Person","age":30,"name":"John"}
```

### 3. Deserialization (Load)

```java
import org.eclipse.fennec.codec.options.CodecResourceOptions;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

String json = """
    {
        "_type": "Person",
        "name": "Jane",
        "age": 25
    }
    """;

ResourceSet rs = codecSetup.createResourceSet();
Resource resource = rs.createResource(URI.createURI("person.json"));

// Load options - CODEC_ROOT_OBJECT is required for deserialization
Map<String, Object> loadOptions = new HashMap<>();
loadOptions.put(CodecResourceOptions.CODEC_ROOT_OBJECT, MyModelPackage.eINSTANCE.getPerson());

ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
resource.load(bais, loadOptions);

Person person = (Person) resource.getContents().get(0);
```

## Configuration

### Default Configuration

The `CodecSetup` uses sensible defaults:
- Type information is serialized (`_type` field)
- ID is serialized on top (`_id` field)
- Null and empty values are not serialized
- Default values are not serialized

### Custom Module Configuration

Use `DefaultCodecModuleConfig.builder()` to customize the codec behavior:

```java
import org.eclipse.fennec.codec.java.DefaultCodecModuleConfig;

CodecSetup codecSetup = new CodecSetup(
    DefaultCodecModuleConfig.builder()
        .serializeType(true)           // Include _type field
        .useId(true)                   // Include _id field
        .idOnTop(true)                 // Place _id at the top
        .serializeDefaultValue(false)  // Skip default values
        .serializeNullValue(false)     // Skip null values
        .serializeEmptyValue(false)    // Skip empty collections
        .deserializeType(true)         // Process type info on load
        .build()
);
```

### Per-Operation Options with CodecOptionsBuilder

For fine-grained control over individual save/load operations:

```java
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;

// Save options with URI type strategy
Map<String, Object> saveOptions = CodecOptionsBuilder.create()
    .forClass(MyModelPackage.eINSTANCE.getPerson())
        .typeStrategy("URI")    // Use EClass URI as type (e.g., "http://example.com/model#//Person")
        .idStrategy("ID_FIELD") // Use ID field for _id
    .build();

resource.save(baos, saveOptions);

// Load options
Map<String, Object> loadOptions = CodecOptionsBuilder.create()
    .rootObject(MyModelPackage.eINSTANCE.getPerson())  // Required for deserialization
    .deserializeType(true)                              // Process type information
    .forClass(MyModelPackage.eINSTANCE.getPerson())
        .typeStrategy("URI")
    .build();

resource.load(bais, loadOptions);
```

## Type Strategies

The codec supports different strategies for serializing type information:

| Strategy | Example `_type` Value |
|----------|----------------------|
| `NAME`   | `"Person"` |
| `CLASS`  | `"com.example.model.Person"` |
| `URI`    | `"http://example.com/model/1.0#//Person"` |

## Configuration Options Reference

### Module Configuration (DefaultCodecModuleConfig.Builder)

| Option | Default | Description |
|--------|---------|-------------|
| `serializeType` | `true` | Include `_type` field in output |
| `deserializeType` | `false` | Process `_type` field on load |
| `useId` | `true` | Include `_id` field in output |
| `idOnTop` | `true` | Place `_id` at the beginning of JSON |
| `serializeIdField` | `false` | Also serialize the ID feature separately |
| `serializeDefaultValue` | `false` | Include features with default values |
| `serializeNullValue` | `false` | Include features with null values |
| `serializeEmptyValue` | `false` | Include empty collections |
| `serializeSuperTypes` | `false` | Include `_supertype` field |
| `writeEnumLiterals` | `false` | Use enum literal names instead of values |

### Load Options (CodecResourceOptions)

| Option | Description |
|--------|-------------|
| `CODEC_ROOT_OBJECT` | **Required** - The EClass to instantiate when loading |

### Per-Class Options (CodecOptionsBuilder)

| Option | Description |
|--------|-------------|
| `typeStrategy(String)` | Type serialization strategy: `NAME`, `CLASS`, `URI` |
| `typeKey(String)` | Custom key for type field (default: `_type`) |
| `idStrategy(String)` | ID generation strategy: `ID_FIELD`, `COMBINED` |
| `idKey(String)` | Custom key for ID field (default: `_id`) |
| `idSeparator(String)` | Separator for combined IDs |
| `ignoreFeatures(...)` | Features to exclude from serialization |

## File Operations

The codec can also work with files:

```java
// Save to file
Resource resource = rs.createResource(URI.createFileURI("/path/to/output.json"));
resource.getContents().add(myObject);
resource.save(null);

// Load from file
Resource resource = rs.createResource(URI.createFileURI("/path/to/input.json"));
Map<String, Object> options = new HashMap<>();
options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, MyPackage.eINSTANCE.getMyClass());
resource.load(options);
```

## Dependencies

This module requires:
- EMF Core (`org.eclipse.emf.ecore`)
- Jackson 3.x (`tools.jackson.core`)
- OSGi Converter (`org.osgi.util.converter`) - for configuration handling

## License

Eclipse Public License 2.0
