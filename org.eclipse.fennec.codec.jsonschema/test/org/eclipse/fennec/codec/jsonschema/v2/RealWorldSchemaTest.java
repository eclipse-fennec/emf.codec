/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.codec.jsonschema.v2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests using real-world JSON Schema files from the test-data folder.
 * <p>
 * These tests validate that the JSON Schema v2 converter correctly handles
 * complex, real-world schemas including:
 * <ul>
 *   <li>Meter reading schemas with required properties</li>
 *   <li>Pipeline schemas with discriminated unions (oneOf with minProperties/maxProperties)</li>
 *   <li>Nested namespace structures (configs/kafka)</li>
 *   <li>Top-level properties creating rootClass</li>
 * </ul>
 * </p>
 */
@DisplayName("Real-World Schema Tests")
class RealWorldSchemaTest {

    private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";
    private MetadataWhiteboard metadataService;

    /**
     * Loads JSON Schema from a string.
     */
    private EPackage loadJsonSchema(String json, String schemaFeature) throws IOException {
    	metadataService = MetadataServiceFactory.create();
    	
        JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
                URI.createURI("schema.jsonschema"), metadataService);

        Map<String, Object> options = new HashMap<>();
        if (schemaFeature != null) {
            options.put(CodecJsonSchemaOptions.OPTION_SCHEMA_FEATURE, schemaFeature);
        }

        try (var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            resource.load(is, options);
        }

        assertFalse(resource.getContents().isEmpty(), "Resource should have contents");
        assertInstanceOf(EPackage.class, resource.getContents().get(0));
        return (EPackage) resource.getContents().get(0);
    }

    /**
     * Saves EPackage as JSON Schema.
     */
    private String saveJsonSchema(EPackage ePackage, String schemaFeature) throws IOException {
    	metadataService = MetadataServiceFactory.create();
    	
        JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
                URI.createURI("schema.jsonschema"), metadataService);

        resource.getContents().add(ePackage);

        Map<String, Object> options = new HashMap<>();
        if (schemaFeature != null) {
            options.put(CodecJsonSchemaOptions.OPTION_SCHEMA_FEATURE, schemaFeature);
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            resource.save(os, options);
            return os.toString(StandardCharsets.UTF_8);
        }
    }

    // ========================================================================
    // Meter Reading Schema Tests
    // ========================================================================

    @Nested
    @DisplayName("Meter Reading Schema")
    class MeterReadingSchemaTests {

        @Test
        @DisplayName("loads meter reading schema with required properties")
        void loadsMeterReadingSchema() throws IOException {
            String json = """
                {
                  "$schema": "http://json-schema.org/draft-07/schema#",
                  "$id": "http://example.com/schemas/root.json",
                  "title": "Root Schema",
                  "definitions": {
                    "MeterReading": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "description": "The unique identifier for the meter reading.",
                          "type": "integer"
                        },
                        "meter_id": {
                          "description": "The identifier of the meter that the reading came from.",
                          "type": "string"
                        },
                        "value": {
                          "description": "The value of the meter reading.",
                          "type": "number"
                        },
                        "timestamp": {
                          "description": "The date and time of the meter reading.",
                          "type": "string",
                          "format": "date-time"
                        }
                      },
                      "required": ["id", "meter_id", "value", "timestamp"]
                    }
                  }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            // Verify package metadata (title with space becomes sanitized name)
            assertNotNull(ePackage.getName());
            assertEquals("http://example.com/schemas/root.json", ePackage.getNsURI());

            // Verify MeterReading class
            EClass meterReading = (EClass) ePackage.getEClassifier("MeterReading");
            assertNotNull(meterReading, "MeterReading should exist");
            assertEquals(4, meterReading.getEStructuralFeatures().size());

            // Check id attribute (required, integer)
            EAttribute id = (EAttribute) meterReading.getEStructuralFeature("id");
            assertNotNull(id);
            assertEquals(EcorePackage.Literals.EINT, id.getEAttributeType());
            assertEquals(1, id.getLowerBound(), "id should be required");

            // Check meter_id attribute (required, string)
            EAttribute meterId = (EAttribute) meterReading.getEStructuralFeature("meter_id");
            assertNotNull(meterId);
            assertEquals(EcorePackage.Literals.ESTRING, meterId.getEAttributeType());
            assertEquals(1, meterId.getLowerBound(), "meter_id should be required");

            // Check value attribute (required, number → double)
            EAttribute value = (EAttribute) meterReading.getEStructuralFeature("value");
            assertNotNull(value);
            assertEquals(EcorePackage.Literals.EDOUBLE, value.getEAttributeType());
            assertEquals(1, value.getLowerBound(), "value should be required");

            // Check timestamp attribute (required, string with format)
            EAttribute timestamp = (EAttribute) meterReading.getEStructuralFeature("timestamp");
            assertNotNull(timestamp);
            assertEquals(EcorePackage.Literals.ESTRING, timestamp.getEAttributeType());
            assertEquals(1, timestamp.getLowerBound(), "timestamp should be required");

            // Verify format annotation is preserved
            EAnnotation formatAnnotation = timestamp.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(formatAnnotation);
            assertEquals("date-time", formatAnnotation.getDetails().get("format"));
        }

        @Test
        @DisplayName("round-trips meter reading schema")
        void roundTripsMeterReadingSchema() throws IOException {
            String originalJson = """
                {
                  "$id": "http://example.com/meter",
                  "title": "MeterPackage",
                  "definitions": {
                    "MeterReading": {
                      "type": "object",
                      "properties": {
                        "id": { "type": "integer" },
                        "value": { "type": "number" }
                      },
                      "required": ["id", "value"]
                    }
                  }
                }
                """;

            // Load
            EPackage ePackage = loadJsonSchema(originalJson, "definitions");
            EClass meterReading = (EClass) ePackage.getEClassifier("MeterReading");
            assertNotNull(meterReading);

            // Save
            String savedJson = saveJsonSchema(ePackage, "definitions");

            // Reload
            EPackage reloaded = loadJsonSchema(savedJson, "definitions");
            EClass reloadedMeterReading = (EClass) reloaded.getEClassifier("MeterReading");

            // Verify structure preserved
            assertNotNull(reloadedMeterReading);
            assertEquals(meterReading.getEStructuralFeatures().size(),
                       reloadedMeterReading.getEStructuralFeatures().size());

            // Verify required properties preserved
            EAttribute idAttr = (EAttribute) reloadedMeterReading.getEStructuralFeature("id");
            assertEquals(1, idAttr.getLowerBound(), "id should still be required after round-trip");
        }
    }

    // ========================================================================
    // Pipeline Schema Tests (Discriminated Unions)
    // ========================================================================

    @Nested
    @DisplayName("Pipeline Schema (Discriminated Unions)")
    class PipelineSchemaTests {

        @Test
        @DisplayName("handles discriminated union with minProperties/maxProperties")
        void handlesDiscriminatedUnion() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/pipeline",
                  "title": "PipelinePackage",
                  "$defs": {
                    "configs": {
                      "kafka": {
                        "type": "object",
                        "properties": {
                          "brokers": { "type": "string" },
                          "topic": { "type": "string" }
                        },
                        "required": ["brokers"]
                      },
                      "file": {
                        "type": "object",
                        "properties": {
                          "path": { "type": "string" }
                        },
                        "required": ["path"]
                      }
                    },
                    "inputNode": {
                      "description": "Exactly ONE input per object",
                      "type": "object",
                      "minProperties": 1,
                      "maxProperties": 1,
                      "oneOf": [
                        { "required": ["kafka"], "properties": { "kafka": { "$ref": "#/$defs/configs/kafka" } } },
                        { "required": ["file"], "properties": { "file": { "$ref": "#/$defs/configs/file" } } }
                      ]
                    }
                  }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "$defs");

            // Verify InputNode is abstract
            EClass inputNode = (EClass) ePackage.getEClassifier("InputNode");
            assertNotNull(inputNode, "InputNode should exist");
            assertTrue(inputNode.isAbstract(), "InputNode should be abstract");

            // Verify discriminatedUnion annotation
            EAnnotation jsAnnotation = inputNode.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(jsAnnotation);
            assertEquals("true", jsAnnotation.getDetails().get("discriminatedUnion"));

            // Verify config classes exist with namespace path
            EClass kafkaConfig = (EClass) ePackage.getEClassifier("Kafka");
            assertNotNull(kafkaConfig, "Kafka config should exist");

            EAnnotation kafkaAnnotation = kafkaConfig.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(kafkaAnnotation);
            assertEquals("configs", kafkaAnnotation.getDetails().get("namespacePath"));
        }

        @Test
        @DisplayName("handles top-level properties creating rootClass")
        void handlesTopLevelProperties() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/pipeline-config",
                  "title": "PipelineConfig",
                  "type": "object",
                  "additionalProperties": false,
                  "required": ["input", "output"],
                  "properties": {
                    "input": { "type": "string" },
                    "output": { "type": "string" },
                    "debug": { "type": "boolean" }
                  },
                  "$defs": {
                    "Helper": {
                      "type": "object",
                      "properties": {
                        "name": { "type": "string" }
                      }
                    }
                  }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "$defs");

            // Verify rootClass is created
            EClass rootClass = (EClass) ePackage.getEClassifier("PipelineConfig");
            assertNotNull(rootClass, "PipelineConfig root class should exist");

            EAnnotation jsAnnotation = rootClass.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(jsAnnotation);
            assertEquals("true", jsAnnotation.getDetails().get("rootClass"));

            // Verify properties
            EAttribute input = (EAttribute) rootClass.getEStructuralFeature("input");
            assertNotNull(input);
            assertEquals(1, input.getLowerBound(), "input should be required");

            EAttribute output = (EAttribute) rootClass.getEStructuralFeature("output");
            assertNotNull(output);
            assertEquals(1, output.getLowerBound(), "output should be required");

            EAttribute debug = (EAttribute) rootClass.getEStructuralFeature("debug");
            assertNotNull(debug);
            assertEquals(0, debug.getLowerBound(), "debug should be optional");

            // Verify Helper also exists
            EClass helper = (EClass) ePackage.getEClassifier("Helper");
            assertNotNull(helper, "Helper should also exist");
        }

        @Test
        @DisplayName("handles nested namespace structures")
        void handlesNestedNamespaces() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/nested",
                  "title": "NestedPackage",
                  "$defs": {
                    "configs": {
                      "input": {
                        "kafka": {
                          "type": "object",
                          "properties": {
                            "brokers": { "type": "string" }
                          }
                        }
                      },
                      "output": {
                        "http": {
                          "type": "object",
                          "properties": {
                            "url": { "type": "string" }
                          }
                        }
                      }
                    }
                  }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "$defs");

            // Verify classes are created with namespace paths
            EClass kafka = (EClass) ePackage.getEClassifier("Kafka");
            assertNotNull(kafka, "Kafka should exist");

            EAnnotation kafkaAnnotation = kafka.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(kafkaAnnotation);
            assertEquals("configs/input", kafkaAnnotation.getDetails().get("namespacePath"));

            EClass http = (EClass) ePackage.getEClassifier("Http");
            assertNotNull(http, "Http should exist");

            EAnnotation httpAnnotation = http.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(httpAnnotation);
            assertEquals("configs/output", httpAnnotation.getDetails().get("namespacePath"));
        }
    }

    // ========================================================================
    // Top-Level EClass Tests
    // ========================================================================

    @Nested
    @DisplayName("Top-Level EClass Schema")
    class TopLevelEClassTests {

        @Test
        @DisplayName("handles blob resource with format annotations")
        void handlesBlobResourceWithFormats() throws IOException {
            String json = """
                {
                    "$schema": "http://json-schema.org/draft-07/schema#",
                    "definitions": {
                     "BlobResourceContents": {
                            "properties": {
                                "blob": {
                                    "description": "A base64-encoded string representing the binary data.",
                                    "format": "byte",
                                    "type": "string"
                                },
                                "mimeType": {
                                    "description": "The MIME type of this resource, if known.",
                                    "type": "string"
                                },
                                "uri": {
                                    "description": "The URI of this resource.",
                                    "format": "uri",
                                    "type": "string"
                                }
                            },
                            "required": ["blob", "uri"],
                            "type": "object"
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass blobResource = (EClass) ePackage.getEClassifier("BlobResourceContents");
            assertNotNull(blobResource);

            // Check blob attribute with format
            EAttribute blob = (EAttribute) blobResource.getEStructuralFeature("blob");
            assertNotNull(blob);
            assertEquals(1, blob.getLowerBound(), "blob should be required");

            EAnnotation blobAnnotation = blob.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(blobAnnotation);
            assertEquals("byte", blobAnnotation.getDetails().get("format"));

            // Check uri attribute with format
            EAttribute uri = (EAttribute) blobResource.getEStructuralFeature("uri");
            assertNotNull(uri);
            assertEquals(1, uri.getLowerBound(), "uri should be required");

            EAnnotation uriAnnotation = uri.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(uriAnnotation);
            assertEquals("uri", uriAnnotation.getDetails().get("format"));

            // Check mimeType attribute (optional)
            EAttribute mimeType = (EAttribute) blobResource.getEStructuralFeature("mimeType");
            assertNotNull(mimeType);
            assertEquals(0, mimeType.getLowerBound(), "mimeType should be optional");
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("round-trips discriminated union")
        void roundTripsDiscriminatedUnion() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/union",
                  "title": "UnionPackage",
                  "$defs": {
                    "configs": {
                      "kafka": {
                        "type": "object",
                        "properties": {
                          "brokers": { "type": "string" }
                        }
                      },
                      "file": {
                        "type": "object",
                        "properties": {
                          "path": { "type": "string" }
                        }
                      }
                    },
                    "inputNode": {
                      "type": "object",
                      "minProperties": 1,
                      "maxProperties": 1,
                      "oneOf": [
                        { "required": ["kafka"], "properties": { "kafka": { "$ref": "#/$defs/configs/kafka" } } },
                        { "required": ["file"], "properties": { "file": { "$ref": "#/$defs/configs/file" } } }
                      ]
                    }
                  }
                }
                """;

            // Load
            EPackage original = loadJsonSchema(json, "$defs");
            EClass inputNode = (EClass) original.getEClassifier("InputNode");
            assertNotNull(inputNode);
            assertTrue(inputNode.isAbstract());

            // Save
            String savedJson = saveJsonSchema(original, "$defs");

            // Reload
            EPackage reloaded = loadJsonSchema(savedJson, "$defs");
            EClass reloadedInputNode = (EClass) reloaded.getEClassifier("InputNode");

            // Verify structure preserved
            assertNotNull(reloadedInputNode);
            assertTrue(reloadedInputNode.isAbstract(), "InputNode should still be abstract");

            // Verify discriminatedUnion annotation preserved
            EAnnotation jsAnnotation = reloadedInputNode.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(jsAnnotation);
            assertEquals("true", jsAnnotation.getDetails().get("discriminatedUnion"));
        }

        @Test
        @DisplayName("round-trips format annotations")
        void roundTripsFormatAnnotations() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/formats",
                  "title": "FormatsPackage",
                  "definitions": {
                    "Resource": {
                      "type": "object",
                      "properties": {
                        "uri": { "type": "string", "format": "uri" },
                        "timestamp": { "type": "string", "format": "date-time" },
                        "data": { "type": "string", "format": "byte" }
                      }
                    }
                  }
                }
                """;

            // Load
            EPackage original = loadJsonSchema(json, "definitions");

            // Save
            String savedJson = saveJsonSchema(original, "definitions");

            // Reload
            EPackage reloaded = loadJsonSchema(savedJson, "definitions");
            EClass resource = (EClass) reloaded.getEClassifier("Resource");

            // Verify format annotations preserved
            EAttribute uri = (EAttribute) resource.getEStructuralFeature("uri");
            EAnnotation uriAnnotation = uri.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertEquals("uri", uriAnnotation.getDetails().get("format"));

            EAttribute timestamp = (EAttribute) resource.getEStructuralFeature("timestamp");
            EAnnotation tsAnnotation = timestamp.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertEquals("date-time", tsAnnotation.getDetails().get("format"));

            EAttribute data = (EAttribute) resource.getEStructuralFeature("data");
            EAnnotation dataAnnotation = data.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertEquals("byte", dataAnnotation.getDetails().get("format"));
        }

        @Test
        @DisplayName("round-trips array bounds")
        void roundTripsArrayBounds() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/arrays",
                  "title": "ArraysPackage",
                  "definitions": {
                    "Container": {
                      "type": "object",
                      "properties": {
                        "items": {
                          "type": "array",
                          "items": { "type": "string" },
                          "minItems": 1,
                          "maxItems": 5
                        }
                      }
                    }
                  }
                }
                """;

            // Load
            EPackage original = loadJsonSchema(json, "definitions");
            EClass container = (EClass) original.getEClassifier("Container");
            EAttribute items = (EAttribute) container.getEStructuralFeature("items");
            assertEquals(1, items.getLowerBound());
            assertEquals(5, items.getUpperBound());

            // Save
            String savedJson = saveJsonSchema(original, "definitions");

            // Reload
            EPackage reloaded = loadJsonSchema(savedJson, "definitions");
            EClass reloadedContainer = (EClass) reloaded.getEClassifier("Container");
            EAttribute reloadedItems = (EAttribute) reloadedContainer.getEStructuralFeature("items");

            // Verify bounds preserved
            assertEquals(1, reloadedItems.getLowerBound(), "minItems should be preserved");
            assertEquals(5, reloadedItems.getUpperBound(), "maxItems should be preserved");
        }

        @Test
        @DisplayName("round-trips required properties")
        void roundTripsRequiredProperties() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/required",
                  "title": "RequiredPackage",
                  "definitions": {
                    "Person": {
                      "type": "object",
                      "properties": {
                        "id": { "type": "integer" },
                        "name": { "type": "string" },
                        "nickname": { "type": "string" }
                      },
                      "required": ["id", "name"]
                    }
                  }
                }
                """;

            // Load
            EPackage original = loadJsonSchema(json, "definitions");
            EClass person = (EClass) original.getEClassifier("Person");
            assertEquals(1, ((EAttribute) person.getEStructuralFeature("id")).getLowerBound());
            assertEquals(1, ((EAttribute) person.getEStructuralFeature("name")).getLowerBound());
            assertEquals(0, ((EAttribute) person.getEStructuralFeature("nickname")).getLowerBound());

            // Save
            String savedJson = saveJsonSchema(original, "definitions");

            // Reload
            EPackage reloaded = loadJsonSchema(savedJson, "definitions");
            EClass reloadedPerson = (EClass) reloaded.getEClassifier("Person");

            // Verify required preserved
            assertEquals(1, ((EAttribute) reloadedPerson.getEStructuralFeature("id")).getLowerBound());
            assertEquals(1, ((EAttribute) reloadedPerson.getEStructuralFeature("name")).getLowerBound());
            assertEquals(0, ((EAttribute) reloadedPerson.getEStructuralFeature("nickname")).getLowerBound());
        }

        @Test
        @DisplayName("round-trips inheritance (allOf)")
        void roundTripsInheritance() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/inheritance",
                  "title": "InheritancePackage",
                  "definitions": {
                    "Vehicle": {
                      "type": "object",
                      "properties": {
                        "brand": { "type": "string" }
                      }
                    },
                    "Car": {
                      "allOf": [
                        { "$ref": "#/definitions/Vehicle" },
                        {
                          "type": "object",
                          "properties": {
                            "doors": { "type": "integer" }
                          }
                        }
                      ]
                    }
                  }
                }
                """;

            // Load
            EPackage original = loadJsonSchema(json, "definitions");
            EClass vehicle = (EClass) original.getEClassifier("Vehicle");
            EClass car = (EClass) original.getEClassifier("Car");
            assertTrue(car.getESuperTypes().contains(vehicle));

            // Save
            String savedJson = saveJsonSchema(original, "definitions");

            // Reload
            EPackage reloaded = loadJsonSchema(savedJson, "definitions");
            EClass reloadedVehicle = (EClass) reloaded.getEClassifier("Vehicle");
            EClass reloadedCar = (EClass) reloaded.getEClassifier("Car");

            // Verify inheritance preserved
            assertTrue(reloadedCar.getESuperTypes().contains(reloadedVehicle),
                      "Car should still extend Vehicle after round-trip");
            assertNotNull(reloadedCar.getEStructuralFeature("doors"));
        }

        @Test
        @DisplayName("round-trips enum")
        void roundTripsEnum() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/enum",
                  "title": "EnumPackage",
                  "definitions": {
                    "Priority": {
                      "type": "string",
                      "enum": ["LOW", "MEDIUM", "HIGH", "CRITICAL"]
                    }
                  }
                }
                """;

            // Load
            EPackage original = loadJsonSchema(json, "definitions");
            EClassifier priority = original.getEClassifier("Priority");
            assertInstanceOf(org.eclipse.emf.ecore.EEnum.class, priority);
            org.eclipse.emf.ecore.EEnum priorityEnum = (org.eclipse.emf.ecore.EEnum) priority;
            assertEquals(4, priorityEnum.getELiterals().size());

            // Save
            String savedJson = saveJsonSchema(original, "definitions");

            // Reload
            EPackage reloaded = loadJsonSchema(savedJson, "definitions");
            EClassifier reloadedPriority = reloaded.getEClassifier("Priority");
            assertInstanceOf(org.eclipse.emf.ecore.EEnum.class, reloadedPriority);
            org.eclipse.emf.ecore.EEnum reloadedEnum = (org.eclipse.emf.ecore.EEnum) reloadedPriority;

            // Verify enum literals preserved
            assertEquals(4, reloadedEnum.getELiterals().size());
            assertNotNull(reloadedEnum.getEEnumLiteral("LOW"));
            assertNotNull(reloadedEnum.getEEnumLiteral("MEDIUM"));
            assertNotNull(reloadedEnum.getEEnumLiteral("HIGH"));
            assertNotNull(reloadedEnum.getEEnumLiteral("CRITICAL"));
        }

        @Test
        @DisplayName("round-trips $ref references")
        void roundTripsReferences() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/refs",
                  "title": "RefsPackage",
                  "definitions": {
                    "Address": {
                      "type": "object",
                      "properties": {
                        "street": { "type": "string" },
                        "city": { "type": "string" }
                      }
                    },
                    "Person": {
                      "type": "object",
                      "properties": {
                        "name": { "type": "string" },
                        "address": { "$ref": "#/definitions/Address" }
                      }
                    }
                  }
                }
                """;

            // Load
            EPackage original = loadJsonSchema(json, "definitions");
            EClass person = (EClass) original.getEClassifier("Person");
            EClass address = (EClass) original.getEClassifier("Address");
            EReference addressRef = (EReference) person.getEStructuralFeature("address");
            assertEquals(address, addressRef.getEReferenceType());

            // Save
            String savedJson = saveJsonSchema(original, "definitions");

            // Reload
            EPackage reloaded = loadJsonSchema(savedJson, "definitions");
            EClass reloadedPerson = (EClass) reloaded.getEClassifier("Person");
            EClass reloadedAddress = (EClass) reloaded.getEClassifier("Address");
            EReference reloadedAddressRef = (EReference) reloadedPerson.getEStructuralFeature("address");

            // Verify reference type preserved
            assertEquals(reloadedAddress, reloadedAddressRef.getEReferenceType(),
                        "$ref should resolve to Address after round-trip");
        }
    }

    // ========================================================================
    // Array and Reference Tests
    // ========================================================================

    @Nested
    @DisplayName("Array and Reference Handling")
    class ArrayAndReferenceTests {

        @Test
        @DisplayName("handles array with $ref items")
        void handlesArrayWithRefItems() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/array-ref",
                  "title": "ArrayRefPackage",
                  "definitions": {
                    "Item": {
                      "type": "object",
                      "properties": {
                        "name": { "type": "string" }
                      }
                    },
                    "Container": {
                      "type": "object",
                      "properties": {
                        "items": {
                          "type": "array",
                          "items": { "$ref": "#/definitions/Item" }
                        }
                      }
                    }
                  }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass container = (EClass) ePackage.getEClassifier("Container");
            assertNotNull(container);

            EStructuralFeature items = container.getEStructuralFeature("items");
            assertNotNull(items);
            assertTrue(items.isMany(), "items should be many-valued");
            assertEquals(-1, items.getUpperBound());

            // Verify the reference type
            EClass item = (EClass) ePackage.getEClassifier("Item");
            assertNotNull(item);
        }

        @Test
        @DisplayName("handles array with minItems/maxItems")
        void handlesArrayWithBounds() throws IOException {
            String json = """
                {
                  "$id": "http://example.com/bounded-array",
                  "title": "BoundedArrayPackage",
                  "definitions": {
                    "Pipeline": {
                      "type": "object",
                      "properties": {
                        "processors": {
                          "type": "array",
                          "items": { "type": "string" },
                          "minItems": 1,
                          "maxItems": 10
                        }
                      }
                    }
                  }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass pipeline = (EClass) ePackage.getEClassifier("Pipeline");
            assertNotNull(pipeline);

            EAttribute processors = (EAttribute) pipeline.getEStructuralFeature("processors");
            assertNotNull(processors);
            assertEquals(1, processors.getLowerBound(), "minItems should set lowerBound");
            assertEquals(10, processors.getUpperBound(), "maxItems should set upperBound");
        }
    }
}
