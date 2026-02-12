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
package org.eclipse.fennec.codec.jsonschema.v2.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.EffectiveCodecConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.json.JsonMapper;

/**
 * Tests for EPackageValueReader and EPackageValueWriter.
 * <p>
 * These value handlers enable embedded JSON Schema support within the codec v2
 * value transformation layer, for use cases like OpenAPI components/schemas.
 * </p>
 */
@DisplayName("EPackage Value Handler Tests")
class EPackageValueHandlerTest {

    private final ObjectMapper mapper = JsonMapper.builder().build();

    // ========================================================================
    // Reader Tests
    // ========================================================================

    @Nested
    @DisplayName("EPackageValueReader")
    class ReaderTests {

        @Test
        @DisplayName("reads embedded JSON Schema with definitions")
        void readsEmbeddedSchemaWithDefinitions() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/embedded",
                    "title": "EmbeddedPackage",
                    "definitions": {
                        "Person": {
                            "type": "object",
                            "properties": {
                                "name": { "type": "string" },
                                "age": { "type": "integer" }
                            }
                        }
                    }
                }
                """;

            EPackageValueReader reader = new EPackageValueReader();

            try (JsonParser parser = mapper.createParser(json)) {
                parser.nextToken(); // Move to START_OBJECT

                EPackage result = reader.read(createReaderContext(parser), createDummyReference());

                assertNotNull(result);
                assertEquals("EmbeddedPackage", result.getName());
                assertEquals("http://example.org/embedded", result.getNsURI());

                EClass person = (EClass) result.getEClassifier("Person");
                assertNotNull(person);
                assertEquals(2, person.getEStructuralFeatures().size());
            }
        }

        @Test
        @DisplayName("reads embedded JSON Schema with $defs")
        void readsEmbeddedSchemaWithDefs() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/modern",
                    "title": "ModernPackage",
                    "$defs": {
                        "Address": {
                            "type": "object",
                            "properties": {
                                "street": { "type": "string" },
                                "city": { "type": "string" }
                            }
                        }
                    }
                }
                """;

            EPackageValueReader reader = new EPackageValueReader();

            try (JsonParser parser = mapper.createParser(json)) {
                parser.nextToken();

                EPackage result = reader.read(createReaderContext(parser), createDummyReference());

                assertNotNull(result);
                EClass address = (EClass) result.getEClassifier("Address");
                assertNotNull(address);
            }
        }

        @Test
        @DisplayName("reads with auto-detection of schema feature")
        void readsWithAutoDetection() throws IOException {
            String json = """
                {
                    "title": "AutoDetectPackage",
                    "definitions": {
                        "Item": {
                            "type": "object",
                            "properties": {
                                "id": { "type": "string" }
                            }
                        }
                    }
                }
                """;

            EPackageValueReader reader = new EPackageValueReader(); // auto-detect

            try (JsonParser parser = mapper.createParser(json)) {
                parser.nextToken();

                EPackage result = reader.read(createReaderContext(parser), createDummyReference());

                assertNotNull(result);
                assertNotNull(result.getEClassifier("Item"));
            }
        }

        @Test
        @DisplayName("canHandle returns true for EPackage reference")
        void canHandleReturnsTrueForEPackageReference() {
            EPackageValueReader reader = new EPackageValueReader();
            assertTrue(reader.canHandle(createDummyReference()));
        }
    }

    // ========================================================================
    // Writer Tests
    // ========================================================================

    @Nested
    @DisplayName("EPackageValueWriter")
    class WriterTests {

        @Test
        @DisplayName("writes EPackage as full JSON Schema document")
        void writesFullJsonSchemaDocument() throws IOException {
            EPackage ePackage = createTestPackage();

            EPackageValueWriter writer = new EPackageValueWriter("definitions");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (var gen = mapper.createGenerator(baos)) {
                writer.write(ePackage, createDummyReference(), createWriterContext(gen));
            }

            String json = baos.toString(StandardCharsets.UTF_8);

            assertTrue(json.contains("\"$id\""));
            assertTrue(json.contains("http://test.org/package"));
            assertTrue(json.contains("\"definitions\""));
            assertTrue(json.contains("\"TestClass\""));
            assertTrue(json.contains("\"name\""));
            assertTrue(json.contains("\"string\""));
        }

        @Test
        @DisplayName("writes EPackage with embedded definitions only")
        void writesEmbeddedDefinitionsOnly() throws IOException {
            EPackage ePackage = createTestPackage();

            EPackageValueWriter writer = new EPackageValueWriter("definitions", true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (var gen = mapper.createGenerator(baos)) {
                writer.write(ePackage, createDummyReference(), createWriterContext(gen));
            }

            String json = baos.toString(StandardCharsets.UTF_8);

            // Should NOT contain the wrapper
            assertTrue(!json.contains("\"$id\"") || json.indexOf("\"$id\"") > json.indexOf("\"TestClass\""));
            // Should contain the class definition
            assertTrue(json.contains("\"TestClass\""));
        }

        @Test
        @DisplayName("writes null EPackage as null")
        void writesNullAsNull() throws IOException {
            EPackageValueWriter writer = new EPackageValueWriter();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (var gen = mapper.createGenerator(baos)) {
                writer.write(null, createDummyReference(), createWriterContext(gen));
            }

            String json = baos.toString(StandardCharsets.UTF_8);
            assertEquals("null", json.trim());
        }

        @Test
        @DisplayName("canHandle returns true for EPackage reference")
        void canHandleReturnsTrueForEPackageReference() {
            EPackageValueWriter writer = new EPackageValueWriter();
            assertTrue(writer.canHandle(createDummyReference()));
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip")
    class RoundTripTests {

        @Test
        @DisplayName("round-trip EPackage through value handlers")
        void roundTripEPackage() throws IOException {
            EPackage original = createTestPackage();

            // Write
            EPackageValueWriter writer = new EPackageValueWriter("definitions");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (var gen = mapper.createGenerator(baos)) {
                writer.write(original, createDummyReference(), createWriterContext(gen));
            }

            String json = baos.toString(StandardCharsets.UTF_8);

            // Read back
            EPackageValueReader reader = new EPackageValueReader();
            try (JsonParser parser = mapper.createParser(json)) {
                parser.nextToken();

                EPackage result = reader.read(createReaderContext(parser), createDummyReference());

                assertNotNull(result);
                assertEquals(original.getName(), result.getName());
                assertEquals(original.getNsURI(), result.getNsURI());

                EClass originalClass = (EClass) original.getEClassifier("TestClass");
                EClass resultClass = (EClass) result.getEClassifier("TestClass");

                assertNotNull(resultClass);
                assertEquals(originalClass.getEStructuralFeatures().size(),
                           resultClass.getEStructuralFeatures().size());
            }
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Creates a dummy EReference that references EPackage.
     * This simulates a containment reference like OpenAPI's Components.schemas.
     */
    private EReference createDummyReference() {
        EReference ref = EcoreFactory.eINSTANCE.createEReference();
        ref.setName("schemas");
        ref.setEType(EcorePackage.Literals.EPACKAGE);
        ref.setContainment(true);
        return ref;
    }

    private EPackage createTestPackage() {
        EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
        ePackage.setName("TestPackage");
        ePackage.setNsURI("http://test.org/package");
        ePackage.setNsPrefix("test");

        EClass testClass = EcoreFactory.eINSTANCE.createEClass();
        testClass.setName("TestClass");

        EAttribute nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        testClass.getEStructuralFeatures().add(nameAttr);

        EAttribute ageAttr = EcoreFactory.eINSTANCE.createEAttribute();
        ageAttr.setName("age");
        ageAttr.setEType(EcorePackage.Literals.EINT);
        testClass.getEStructuralFeatures().add(ageAttr);

        ePackage.getEClassifiers().add(testClass);

        return ePackage;
    }

    /**
     * Creates a test reader context wrapping the given parser.
     */
    private CodecReaderContext createReaderContext(JsonParser parser) {
        return new CodecReaderContext() {
            @Override
            public JsonParser getParser() {
                return parser;
            }

            @Override
            public DeserializationContext getJacksonContext() {
                return null;
            }

            @Override
            public EffectiveCodecConfig getConfig() {
                return null;
            }

            @Override
            public DiagnosticCollector getDiagnostics() {
                return new DiagnosticCollector();
            }
        };
    }

    /**
     * Creates a test writer context wrapping the given generator.
     */
    private CodecWriterContext createWriterContext(JsonGenerator generator) {
        return new CodecWriterContext() {
            @Override
            public JsonGenerator getGenerator() {
                return generator;
            }

            @Override
            public SerializationContext getJacksonContext() {
                return null;
            }

            @Override
            public EffectiveCodecConfig getConfig() {
                return null;
            }

            @Override
            public DiagnosticCollector getDiagnostics() {
                return new DiagnosticCollector();
            }
        };
    }
}
