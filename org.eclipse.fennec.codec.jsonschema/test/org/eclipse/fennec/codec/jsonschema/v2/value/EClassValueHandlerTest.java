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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.constants.AnnotationSources;
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
 * Tests for EClassValueReader and EClassValueWriter.
 * <p>
 * These value handlers enable embedding a single JSON Schema class document
 * within other formats via the codec v2 value transformation layer.
 * </p>
 */
@DisplayName("EClass Value Handler Tests")
class EClassValueHandlerTest {

    private final ObjectMapper mapper = JsonMapper.builder().build();

    // ========================================================================
    // Reader Tests
    // ========================================================================

    @Nested
    @DisplayName("EClassValueReader")
    class ReaderTests {

        @Test
        @DisplayName("reads a full JSON Schema document and produces an EClass")
        void readsFullSchemaDocument() throws IOException {
            String json = """
                {
                    "$schema": "https://json-schema.org/draft/2020-12/schema",
                    "$id": "http://test#MyClass",
                    "title": "MyClass",
                    "description": "doc",
                    "type": "object",
                    "properties": {
                        "name": { "type": "string" }
                    }
                }
                """;

            EClassValueReader reader = new EClassValueReader();

            try (JsonParser parser = mapper.createParser(json)) {
                parser.nextToken();

                EClass result = reader.read(createReaderContext(parser), createDummyEClassReference());

                assertNotNull(result);
                assertEquals("MyClass", result.getName());
                assertEquals(1, result.getEStructuralFeatures().size());
                assertEquals("name", result.getEStructuralFeatures().get(0).getName());

                // GEN_MODEL documentation annotation
                EAnnotation genModel = result.getEAnnotation(AnnotationSources.GEN_MODEL);
                assertNotNull(genModel);
                assertEquals("doc", genModel.getDetails().get("documentation"));

                // JSONSCHEMA id annotation
                EAnnotation jsonschema = result.getEAnnotation(AnnotationSources.JSONSCHEMA);
                assertNotNull(jsonschema);
                assertEquals("http://test#MyClass", jsonschema.getDetails().get("id"));
            }
        }

        @Test
        @DisplayName("canHandle returns true for EClass reference")
        void canHandleReturnsTrueForEClassReference() {
            EClassValueReader reader = new EClassValueReader();
            assertTrue(reader.canHandle(createDummyEClassReference()));
        }
    }

    // ========================================================================
    // Writer Tests
    // ========================================================================

    @Nested
    @DisplayName("EClassValueWriter")
    class WriterTests {

        @Test
        @DisplayName("writes EClass with attributes as JSON Schema with title and properties")
        void writesEClassWithAttributes() throws IOException {
            EClass eClass = createTestEClass();

            EClassValueWriter writer = new EClassValueWriter();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (JsonGenerator gen = mapper.createGenerator(baos)) {
                writer.write(eClass, createDummyEClassReference(), createWriterContext(gen));
            }

            String json = baos.toString(StandardCharsets.UTF_8);

            assertTrue(json.contains("\"title\""));
            assertTrue(json.contains("\"TestClass\""));
            assertTrue(json.contains("\"type\""));
            assertTrue(json.contains("\"object\""));
            assertTrue(json.contains("\"name\""));
            assertTrue(json.contains("\"age\""));
        }

        @Test
        @DisplayName("writes EClass with JSONSCHEMA id annotation as $id")
        void writesEClassWithIdAnnotation() throws IOException {
            EClass eClass = createTestEClass();
            addAnnotation(eClass, AnnotationSources.JSONSCHEMA, "id", "http://test#TestClass");

            EClassValueWriter writer = new EClassValueWriter();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (JsonGenerator gen = mapper.createGenerator(baos)) {
                writer.write(eClass, createDummyEClassReference(), createWriterContext(gen));
            }

            String json = baos.toString(StandardCharsets.UTF_8);
            assertTrue(json.contains("\"$id\""));
            assertTrue(json.contains("http://test#TestClass"));
        }

        @Test
        @DisplayName("writes EClass with GEN_MODEL documentation annotation as description")
        void writesEClassWithDocumentationAnnotation() throws IOException {
            EClass eClass = createTestEClass();
            addAnnotation(eClass, AnnotationSources.GEN_MODEL, "documentation", "Test documentation");

            EClassValueWriter writer = new EClassValueWriter();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (JsonGenerator gen = mapper.createGenerator(baos)) {
                writer.write(eClass, createDummyEClassReference(), createWriterContext(gen));
            }

            String json = baos.toString(StandardCharsets.UTF_8);
            assertTrue(json.contains("\"description\""));
            assertTrue(json.contains("Test documentation"));
        }

        @Test
        @DisplayName("writes null EClass as null")
        void writesNullAsNull() throws IOException {
            EClassValueWriter writer = new EClassValueWriter();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (JsonGenerator gen = mapper.createGenerator(baos)) {
                writer.write(null, createDummyEClassReference(), createWriterContext(gen));
            }

            String json = baos.toString(StandardCharsets.UTF_8);
            assertEquals("null", json.trim());
        }

        @Test
        @DisplayName("canHandle returns true for EClass reference")
        void canHandleReturnsTrueForEClassReference() {
            EClassValueWriter writer = new EClassValueWriter();
            assertTrue(writer.canHandle(createDummyEClassReference()));
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip")
    class RoundTripTests {

        @Test
        @DisplayName("round-trip EClass through value handlers preserves name, features and annotations")
        void roundTripEClass() throws IOException {
            EClass original = createTestEClass();
            addAnnotation(original, AnnotationSources.GEN_MODEL, "documentation", "Test documentation");
            addAnnotation(original, AnnotationSources.JSONSCHEMA, "id", "http://test#TestClass");

            // Write
            EClassValueWriter writer = new EClassValueWriter();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (JsonGenerator gen = mapper.createGenerator(baos)) {
                writer.write(original, createDummyEClassReference(), createWriterContext(gen));
            }

            String json = baos.toString(StandardCharsets.UTF_8);

            // Read back
            EClassValueReader reader = new EClassValueReader();
            try (JsonParser parser = mapper.createParser(json)) {
                parser.nextToken();

                EClass result = reader.read(createReaderContext(parser), createDummyEClassReference());

                assertNotNull(result);
                assertEquals(original.getName(), result.getName());
                assertEquals(original.getEStructuralFeatures().size(),
                             result.getEStructuralFeatures().size());

                // Check that id annotation survived
                EAnnotation jsonschema = result.getEAnnotation(AnnotationSources.JSONSCHEMA);
                assertNotNull(jsonschema);
                assertEquals("http://test#TestClass", jsonschema.getDetails().get("id"));

                // Check that documentation survived
                EAnnotation genModel = result.getEAnnotation(AnnotationSources.GEN_MODEL);
                assertNotNull(genModel);
                assertEquals("Test documentation", genModel.getDetails().get("documentation"));
            }
        }

        @Test
        @DisplayName("round-trip preserves lowercase-first title exactly")
        void roundTripPreservesLowercaseTitle() throws IOException {
            // Schema with a lowercase-first title: the reader capitalizes the EClass name
            // but must preserve the original title so the writer can emit it unchanged.
            String json = """
                {
                    "title": "myClass",
                    "type": "object",
                    "properties": {
                        "value": { "type": "string" }
                    }
                }
                """;

            EClassValueReader reader = new EClassValueReader();
            EClass eClass;
            try (JsonParser parser = mapper.createParser(json)) {
                parser.nextToken();
                eClass = reader.read(createReaderContext(parser), createDummyEClassReference());
            }
            assertNotNull(eClass);
            assertEquals("MyClass", eClass.getName()); // capitalized by converter

            // Write back and verify the original title is preserved
            EClassValueWriter writer = new EClassValueWriter();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (JsonGenerator gen = mapper.createGenerator(baos)) {
                writer.write(eClass, createDummyEClassReference(), createWriterContext(gen));
            }

            String out = baos.toString(StandardCharsets.UTF_8);
            assertTrue(out.contains("\"myClass\""), "title should be original 'myClass', got: " + out);
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Creates a dummy EReference with EType = EClass.
     */
    private EReference createDummyEClassReference() {
        EReference ref = EcoreFactory.eINSTANCE.createEReference();
        ref.setName("schema");
        ref.setEType(EcorePackage.Literals.ECLASS);
        ref.setContainment(true);
        return ref;
    }

    /**
     * Creates a test EClass "TestClass" with EAttributes name:EString, age:EInt.
     */
    private EClass createTestEClass() {
        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName("TestClass");

        EAttribute nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        eClass.getEStructuralFeatures().add(nameAttr);

        EAttribute ageAttr = EcoreFactory.eINSTANCE.createEAttribute();
        ageAttr.setName("age");
        ageAttr.setEType(EcorePackage.Literals.EINT);
        eClass.getEStructuralFeatures().add(ageAttr);

        return eClass;
    }

    private void addAnnotation(EClass eClass, String source, String key, String value) {
        EAnnotation annotation = eClass.getEAnnotation(source);
        if (annotation == null) {
            annotation = EcoreFactory.eINSTANCE.createEAnnotation();
            annotation.setSource(source);
            eClass.getEAnnotations().add(annotation);
        }
        annotation.getDetails().put(key, value);
    }

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
