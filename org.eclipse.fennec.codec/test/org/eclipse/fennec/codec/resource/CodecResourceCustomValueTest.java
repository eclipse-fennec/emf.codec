/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
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
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for custom value readers/writers in CodecResource.
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.CodecResourceCustomValueTest}.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/10-custom-values.md">Spec 10: Custom Value Readers/Writers</a>
 */
@DisplayName("CodecResource Custom Value Tests")
class CodecResourceCustomValueTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private EClass personClass;
    private EAttribute nameAttribute;
    private EAttribute ageAttribute;
    private MetadataWhiteboard metadataService;
    private CodecValueRegistry valueRegistry;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(CodecResourceCustomValueTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = ecoreHelper.getEClass(testPackage, "Person");
        nameAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        ageAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "age");

        valueRegistry = new CodecValueRegistry();
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
    }

    private EObject createPerson(String name, int age) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttribute, name);
        person.eSet(ageAttribute, age);
        return person;
    }

    private String serialize(EObject eObject, ConfigurationResolver resolver, CodecValueRegistry registry) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodecResource resource = new CodecResource(
                URI.createURI("test.json"),
                metadataService,
                resolver,
                registry,
                null);
        resource.getContents().add(eObject);
        resource.save(baos, Collections.emptyMap());
        return baos.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootClass, ConfigurationResolver resolver, CodecValueRegistry registry) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        CodecResource resource = new CodecResource(
                URI.createURI("test.json"),
                metadataService,
                resolver,
                registry,
                null);
        resource.load(bais, Collections.singletonMap(CodecResource.CODEC_ROOT_TYPE, rootClass));
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    @Nested
    @DisplayName("Custom Writer Tests")
    class CustomWriterTests {

        @Test
        @DisplayName("Should register and retrieve custom writer")
        void shouldRegisterAndRetrieveCustomWriter() {
            CodecValueWriter<Integer, EAttribute> doublingWriter = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "doublingWriter";
                }

                @Override
                public void write(Integer value, EAttribute feature, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeNumber(value * 2);
                }
            };

            valueRegistry.registerWriter("doublingWriter", doublingWriter);

            assertTrue(valueRegistry.hasWriter("doublingWriter"));
            assertEquals(doublingWriter, valueRegistry.getWriter("doublingWriter").orElse(null));
        }

        @Test
        @DisplayName("Custom writer should be available after registration")
        void customWriterAvailableAfterRegistration() {
            CodecValueWriter<String, EAttribute> prefixWriter = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "prefixWriter";
                }

                @Override
                public void write(String value, EAttribute feature, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeString("PREFIX_" + value);
                }
            };

            valueRegistry.registerWriter("prefixWriter", prefixWriter);

            assertTrue(valueRegistry.hasWriter("prefixWriter"));
            assertNotNull(valueRegistry.getWriter("prefixWriter").orElse(null));
        }
    }

    @Nested
    @DisplayName("Custom Reader Tests")
    class CustomReaderTests {

        @Test
        @DisplayName("Should register and retrieve custom reader")
        void shouldRegisterAndRetrieveCustomReader() {
            CodecValueReader<Integer, EAttribute> halvingReader = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "halvingReader";
                }

                @Override
                public Integer read(CodecReaderContext ctx, EAttribute feature) throws IOException {
                    return ctx.getParser().getIntValue() / 2;
                }
            };

            valueRegistry.registerReader("halvingReader", halvingReader);

            assertTrue(valueRegistry.hasReader("halvingReader"));
            assertEquals(halvingReader, valueRegistry.getReader("halvingReader").orElse(null));
        }
    }

    @Nested
    @DisplayName("Round-trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("Should serialize and deserialize without custom readers/writers")
        void roundTripWithoutCustomHandlers() throws IOException {
            EObject person = createPerson("Alice", 30);

            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            String json = serialize(person, resolver, valueRegistry);
            assertTrue(json.contains("\"name\""), "Should contain name key, but got: " + json);
            assertTrue(json.contains("\"Alice\""), "Should contain Alice value, but got: " + json);
            assertTrue(json.contains("\"age\""), "Should contain age key, but got: " + json);
            assertTrue(json.contains("30"), "Should contain 30 value, but got: " + json);

            EObject loaded = deserialize(json, personClass, resolver, valueRegistry);
            assertNotNull(loaded);
            assertEquals("Alice", loaded.eGet(nameAttribute));
            assertEquals(30, loaded.eGet(ageAttribute));
        }

        @Test
        @DisplayName("Registry should be properly passed through the configuration chain")
        void registryPassedThroughConfigChain() throws IOException {
            CodecValueWriter<String, EAttribute> testWriter = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "testWriter";
                }

                @Override
                public void write(String value, EAttribute feature, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeString("TEST");
                }
            };
            CodecValueReader<String, EAttribute> testReader = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "testReader";
                }

                @Override
                public String read(CodecReaderContext ctx, EAttribute feature) {
                    return "RESULT";
                }
            };
            valueRegistry.registerWriter("testWriter", testWriter);
            valueRegistry.registerReader("testReader", testReader);

            EObject person = createPerson("Bob", 25);

            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            String json = serialize(person, resolver, valueRegistry);
            assertNotNull(json);
            assertFalse(json.isEmpty());

            assertTrue(valueRegistry.hasWriter("testWriter"));
            assertTrue(valueRegistry.hasReader("testReader"));
        }
    }

    @Nested
    @DisplayName("Registry Integration Tests")
    class RegistryIntegrationTests {

        @Test
        @DisplayName("Multiple writers can be registered")
        void multipleWritersRegistered() {
            CodecValueWriter<String, EAttribute> writer1 = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "upperWriter";
                }

                @Override
                public void write(String value, EAttribute feature, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeString(value.toUpperCase());
                }
            };
            CodecValueWriter<Integer, EAttribute> writer2 = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "timesTeWriter";
                }

                @Override
                public void write(Integer value, EAttribute feature, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeNumber(value * 10);
                }
            };
            CodecValueWriter<Boolean, EAttribute> writer3 = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "yesNoWriter";
                }

                @Override
                public void write(Boolean value, EAttribute feature, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeString(value ? "yes" : "no");
                }
            };

            valueRegistry.registerWriter("upperWriter", writer1);
            valueRegistry.registerWriter("timesTeWriter", writer2);
            valueRegistry.registerWriter("yesNoWriter", writer3);

            assertEquals(3, valueRegistry.getWriters().size());
            assertTrue(valueRegistry.hasWriter("upperWriter"));
            assertTrue(valueRegistry.hasWriter("timesTeWriter"));
            assertTrue(valueRegistry.hasWriter("yesNoWriter"));
        }

        @Test
        @DisplayName("Multiple readers can be registered")
        void multipleReadersRegistered() {
            CodecValueReader<String, EAttribute> reader1 = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "lowerReader";
                }

                @Override
                public String read(CodecReaderContext ctx, EAttribute feature) throws IOException {
                    return ctx.getParser().getString().toLowerCase();
                }
            };
            CodecValueReader<Integer, EAttribute> reader2 = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "divideReader";
                }

                @Override
                public Integer read(CodecReaderContext ctx, EAttribute feature) throws IOException {
                    return ctx.getParser().getIntValue() / 10;
                }
            };

            valueRegistry.registerReader("lowerReader", reader1);
            valueRegistry.registerReader("divideReader", reader2);

            assertEquals(2, valueRegistry.getReaders().size());
            assertTrue(valueRegistry.hasReader("lowerReader"));
            assertTrue(valueRegistry.hasReader("divideReader"));
        }

        @Test
        @DisplayName("Null registry should not cause errors")
        void nullRegistryHandledGracefully() throws IOException {
            EObject person = createPerson("Charlie", 40);

            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            String json = serialize(person, resolver, null);
            assertNotNull(json);
            assertTrue(json.contains("\"Charlie\""));

            EObject loaded = deserialize(json, personClass, resolver, null);
            assertNotNull(loaded);
            assertEquals("Charlie", loaded.eGet(nameAttribute));
        }
    }

    @Nested
    @DisplayName("Instance Binding Tests (via options)")
    class InstanceBindingTests {

        @Test
        @DisplayName("Writer instance binding via save options")
        void writerInstanceBindingViaSaveOptions() throws IOException {
            CodecValueWriter<String, EAttribute> uppercaseWriter = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "uppercase";
                }

                @Override
                public void write(String value, EAttribute feature, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeString(value.toUpperCase());
                }
            };

            EObject person = createPerson("alice", 30);
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            // Use instance binding via options
            Map<String, Object> saveOptions = Map.of(
                    CodecOptions.CODEC_FEATURE_VALUE_WRITER_INSTANCES, Map.of(nameAttribute, uppercaseWriter)
            );

            String json = serializeWithOptions(person, resolver, valueRegistry, saveOptions);
            assertTrue(json.contains("\"ALICE\""), "Should contain uppercase ALICE, but got: " + json);
        }

        @Test
        @DisplayName("Reader instance binding via load options")
        void readerInstanceBindingViaLoadOptions() throws IOException {
            CodecValueReader<String, EAttribute> lowercaseReader = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "lowercase";
                }

                @Override
                public String read(CodecReaderContext ctx, EAttribute feature) throws IOException {
                    return ctx.getParser().getString().toLowerCase();
                }
            };

            String json = "{\"name\": \"UPPERCASE_NAME\", \"age\": 25}";
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            // Use instance binding via options
            Map<String, Object> loadOptions = Map.of(
                    CodecResource.CODEC_ROOT_TYPE, personClass,
                    CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES, Map.of(nameAttribute, lowercaseReader)
            );

            EObject loaded = deserializeWithOptions(json, resolver, valueRegistry, loadOptions);
            assertNotNull(loaded);
            assertEquals("uppercase_name", loaded.eGet(nameAttribute));
        }
    }

    // Helper methods for options-based serialization

    private String serializeWithOptions(EObject eObject, ConfigurationResolver resolver,
            CodecValueRegistry registry, Map<String, Object> options) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodecResource resource = new CodecResource(
                URI.createURI("test.json"),
                metadataService,
                resolver,
                registry,
                null);
        resource.getContents().add(eObject);
        resource.save(baos, options);
        return baos.toString(StandardCharsets.UTF_8);
    }

    private EObject deserializeWithOptions(String json, ConfigurationResolver resolver,
            CodecValueRegistry registry, Map<String, Object> options) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        CodecResource resource = new CodecResource(
                URI.createURI("test.json"),
                metadataService,
                resolver,
                registry,
                null);
        resource.load(bais, options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
