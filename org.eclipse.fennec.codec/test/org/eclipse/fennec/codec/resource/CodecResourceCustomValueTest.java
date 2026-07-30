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
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueReader;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

/**
 * Integration tests for custom value readers/writers in CodecResource.
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
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, CodecResourceCustomValueTest.class);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "age");

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
    @DisplayName("Name Binding Tests (via options)")
    class NameBindingTests {

        @Test
        @DisplayName("Writer name binding via save options resolves from registry")
        void writerNameBindingViaSaveOptions() throws IOException {
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

            valueRegistry.register(uppercaseWriter);

            EObject person = createPerson("alice", 30);
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            Map<String, Object> saveOptions = Map.of(
                    CodecOptions.CODEC_FEATURE_VALUE_WRITERS, Map.of(nameAttribute, "uppercase")
            );

            String json = serializeWithOptions(person, resolver, valueRegistry, saveOptions);
            assertTrue(json.contains("\"ALICE\""), "Should contain uppercase ALICE, but got: " + json);
        }

        @Test
        @DisplayName("Reader name binding via load options resolves from registry")
        void readerNameBindingViaLoadOptions() throws IOException {
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

            valueRegistry.register(lowercaseReader);

            String json = "{\"name\": \"UPPERCASE_NAME\", \"age\": 25}";
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            Map<String, Object> loadOptions = Map.of(
                    CodecResource.CODEC_ROOT_TYPE, personClass,
                    CodecOptions.CODEC_FEATURE_VALUE_READERS, Map.of(nameAttribute, "lowercase")
            );

            EObject loaded = deserializeWithOptions(json, resolver, valueRegistry, loadOptions);
            assertNotNull(loaded);
            assertEquals("uppercase_name", loaded.eGet(nameAttribute));
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

    @Nested
    @DisplayName("Reference Instance Binding Tests (via options)")
    class ReferenceInstanceBindingTests {

        private EClass addressClass;
        private EClass companyClass;
        private EReference addressReference;
        private EReference employeesReference;
        private EAttribute streetAttribute;
        private EAttribute cityAttribute;

        @BeforeEach
        void setUpReferences() {
            addressClass = EcoreHelper.getEClass(testPackage, "Address");
            companyClass = EcoreHelper.getEClass(testPackage, "Company");
            addressReference = (EReference) EcoreHelper.getFeature(personClass, "address");
            employeesReference = (EReference) EcoreHelper.getFeature(companyClass, "employees");
            streetAttribute = (EAttribute) EcoreHelper.getFeature(addressClass, "street");
            cityAttribute = (EAttribute) EcoreHelper.getFeature(addressClass, "city");
        }

        /** Reads an Address from the compact shape {@code {"compact": "street|city"}}. */
        private ReferenceValueReader<EObject> compactAddressReader() {
            return new ReferenceValueReader<>() {
                @Override
                public String getName() {
                    return "compactAddress";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return reference.getEReferenceType() == addressClass;
                }

                @Override
                public EObject read(CodecReaderContext ctx, EReference reference) throws IOException {
                    JsonParser parser = ctx.getParser();
                    String compact = null;
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        if ("compact".equals(parser.currentName())) {
                            parser.nextToken();
                            compact = parser.getString();
                        }
                    }
                    if (compact == null) {
                        return null;
                    }
                    String[] parts = compact.split("\\|");
                    EObject address = testPackage.getEFactoryInstance().create(addressClass);
                    address.eSet(streetAttribute, parts[0]);
                    address.eSet(cityAttribute, parts[1]);
                    return address;
                }
            };
        }

        @Test
        @DisplayName("Reader instance binding applies to a single-valued containment reference")
        void readerInstanceBindingForContainmentReference() throws IOException {
            String json = "{\"name\": \"Alice\", \"age\": 30, \"address\": {\"compact\": \"Main St|Springfield\"}}";
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            Map<String, Object> loadOptions = Map.of(
                    CodecResource.CODEC_ROOT_TYPE, personClass,
                    CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES, Map.of(addressReference, compactAddressReader())
            );

            EObject loaded = deserializeWithOptions(json, resolver, valueRegistry, loadOptions);
            assertNotNull(loaded);
            EObject address = (EObject) loaded.eGet(addressReference);
            assertNotNull(address, "custom reference reader must produce the Address");
            assertEquals("Main St", address.eGet(streetAttribute));
            assertEquals("Springfield", address.eGet(cityAttribute));
        }

        @Test
        @DisplayName("Reader instance binding applies per element of a multi-valued containment reference")
        void readerInstanceBindingForMultiValuedContainmentReference() throws IOException {
            ReferenceValueReader<EObject> compactPersonReader = new ReferenceValueReader<>() {
                @Override
                public String getName() {
                    return "compactPerson";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return reference.getEReferenceType() == personClass;
                }

                @Override
                public EObject read(CodecReaderContext ctx, EReference reference) throws IOException {
                    JsonParser parser = ctx.getParser();
                    String name = null;
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        if ("n".equals(parser.currentName())) {
                            parser.nextToken();
                            name = parser.getString();
                        }
                    }
                    EObject person = testPackage.getEFactoryInstance().create(personClass);
                    person.eSet(nameAttribute, name);
                    return person;
                }
            };

            String json = "{\"name\": \"ACME\", \"employees\": [{\"n\": \"Alice\"}, {\"n\": \"Bob\"}]}";
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            Map<String, Object> loadOptions = Map.of(
                    CodecResource.CODEC_ROOT_TYPE, companyClass,
                    CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES, Map.of(employeesReference, compactPersonReader)
            );

            EObject company = deserializeWithOptions(json, resolver, valueRegistry, loadOptions);
            assertNotNull(company);
            @SuppressWarnings("unchecked")
            java.util.List<EObject> employees = (java.util.List<EObject>) company.eGet(employeesReference);
            assertEquals(2, employees.size(), "custom reference reader must produce every element");
            assertEquals("Alice", employees.get(0).eGet(nameAttribute));
            assertEquals("Bob", employees.get(1).eGet(nameAttribute));
        }

        @Test
        @DisplayName("Reader instance binding applies to a non-containment reference")
        void readerInstanceBindingForNonContainmentReference() throws IOException {
            EReference managerReference = (EReference) EcoreHelper.getFeature(personClass, "manager");
            ReferenceValueReader<EObject> compactPersonReader = new ReferenceValueReader<>() {
                @Override
                public String getName() {
                    return "compactPerson";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return reference.getEReferenceType() == personClass;
                }

                @Override
                public EObject read(CodecReaderContext ctx, EReference reference) throws IOException {
                    JsonParser parser = ctx.getParser();
                    String name = null;
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        if ("n".equals(parser.currentName())) {
                            parser.nextToken();
                            name = parser.getString();
                        }
                    }
                    EObject person = testPackage.getEFactoryInstance().create(personClass);
                    person.eSet(nameAttribute, name);
                    return person;
                }
            };

            String json = "{\"name\": \"Alice\", \"manager\": {\"n\": \"Boss\"}}";
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            Map<String, Object> loadOptions = Map.of(
                    CodecResource.CODEC_ROOT_TYPE, personClass,
                    CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES, Map.of(managerReference, compactPersonReader)
            );

            EObject loaded = deserializeWithOptions(json, resolver, valueRegistry, loadOptions);
            assertNotNull(loaded);
            EObject manager = (EObject) loaded.eGet(managerReference);
            assertNotNull(manager, "custom reader must apply to the non-containment reference");
            assertEquals("Boss", manager.eGet(nameAttribute));
        }

        @Test
        @DisplayName("Writer instance binding applies per element of a multi-valued containment reference")
        void writerInstanceBindingForMultiValuedContainmentReference() throws IOException {
            ReferenceValueWriter<EObject> compactPersonWriter = new ReferenceValueWriter<>() {
                @Override
                public String getName() {
                    return "compactPerson";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return reference.getEReferenceType() == personClass;
                }

                @Override
                public void write(EObject value, EReference reference, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeStartObject();
                    ctx.getGenerator().writeStringProperty("n", (String) value.eGet(nameAttribute));
                    ctx.getGenerator().writeEndObject();
                }
            };

            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(EcoreHelper.getFeature(companyClass, "name"), "ACME");
            @SuppressWarnings("unchecked")
            java.util.List<EObject> employees = (java.util.List<EObject>) company.eGet(employeesReference);
            employees.add(createPerson("Alice", 30));
            employees.add(createPerson("Bob", 25));

            ConfigurationResolver resolver = ConfigurationResolver.defaults();
            Map<String, Object> saveOptions = Map.of(
                    CodecOptions.CODEC_FEATURE_VALUE_WRITER_INSTANCES, Map.of(employeesReference, compactPersonWriter)
            );

            String json = serializeWithOptions(company, resolver, valueRegistry, saveOptions);
            assertTrue(json.contains("\"n\""), "custom writer must produce the compact shape, but got: " + json);
            assertTrue(json.contains("Alice") && json.contains("Bob"), "both elements expected, but got: " + json);
            assertFalse(json.contains("\"name\":\"Alice\""), "default Person shape must not be written, but got: " + json);
        }

        @Test
        @DisplayName("Rejected canHandle falls back to default deserialization")
        void readerInstanceCanHandleRejectionFallsBack() throws IOException {
            ReferenceValueReader<EObject> rejectingReader = new ReferenceValueReader<>() {
                @Override
                public String getName() {
                    return "rejecting";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return false;
                }

                @Override
                public EObject read(CodecReaderContext ctx, EReference reference) throws IOException {
                    throw new IllegalStateException("must never be called");
                }
            };

            String json = "{\"name\": \"Alice\", \"address\": {\"street\": \"Main St\", \"city\": \"Springfield\"}}";
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            Map<String, Object> loadOptions = Map.of(
                    CodecResource.CODEC_ROOT_TYPE, personClass,
                    CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES, Map.of(addressReference, rejectingReader)
            );

            EObject loaded = deserializeWithOptions(json, resolver, valueRegistry, loadOptions);
            assertNotNull(loaded);
            EObject address = (EObject) loaded.eGet(addressReference);
            assertNotNull(address, "default deserialization must apply when canHandle rejects");
            assertEquals("Main St", address.eGet(streetAttribute));
        }

        @Test
        @DisplayName("Reader instance takes priority over a config-bound registry reader")
        void readerInstanceTakesPriorityOverConfigBoundReader() throws IOException {
            ReferenceValueReader<EObject> registryReader = new ReferenceValueReader<>() {
                @Override
                public String getName() {
                    return "registryAddress";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return reference.getEReferenceType() == addressClass;
                }

                @Override
                public EObject read(CodecReaderContext ctx, EReference reference) throws IOException {
                    ctx.getParser().skipChildren();
                    EObject address = testPackage.getEFactoryInstance().create(addressClass);
                    address.eSet(streetAttribute, "FROM_REGISTRY");
                    return address;
                }
            };
            valueRegistry.register(registryReader);

            String json = "{\"name\": \"Alice\", \"address\": {\"compact\": \"Main St|Springfield\"}}";
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            Map<String, Object> loadOptions = Map.of(
                    CodecResource.CODEC_ROOT_TYPE, personClass,
                    // Config path binds the registry reader ...
                    CodecOptions.CODEC_EREFERENCE_CONFIG,
                    Map.of(addressReference, Map.of(CodecOptions.CODEC_VALUE_READER_NAME, "registryAddress")),
                    // ... but the bound instance must win
                    CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES, Map.of(addressReference, compactAddressReader())
            );

            EObject loaded = deserializeWithOptions(json, resolver, valueRegistry, loadOptions);
            assertNotNull(loaded);
            EObject address = (EObject) loaded.eGet(addressReference);
            assertNotNull(address);
            assertEquals("Main St", address.eGet(streetAttribute),
                    "instance binding must take priority over the config-bound registry reader");
        }

        @Test
        @DisplayName("Writer instance binding applies to a single-valued containment reference")
        void writerInstanceBindingForContainmentReference() throws IOException {
            ReferenceValueWriter<EObject> compactAddressWriter = new ReferenceValueWriter<>() {
                @Override
                public String getName() {
                    return "compactAddress";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return reference.getEReferenceType() == addressClass;
                }

                @Override
                public void write(EObject value, EReference reference, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeStartObject();
                    ctx.getGenerator().writeStringProperty("compact",
                            value.eGet(streetAttribute) + "|" + value.eGet(cityAttribute));
                    ctx.getGenerator().writeEndObject();
                }
            };

            EObject person = createPerson("Alice", 30);
            EObject address = testPackage.getEFactoryInstance().create(addressClass);
            address.eSet(streetAttribute, "Main St");
            address.eSet(cityAttribute, "Springfield");
            person.eSet(addressReference, address);

            ConfigurationResolver resolver = ConfigurationResolver.defaults();
            Map<String, Object> saveOptions = Map.of(
                    CodecOptions.CODEC_FEATURE_VALUE_WRITER_INSTANCES, Map.of(addressReference, compactAddressWriter)
            );

            String json = serializeWithOptions(person, resolver, valueRegistry, saveOptions);
            assertTrue(json.contains("\"compact\""), "custom reference writer must produce the compact shape, but got: " + json);
            assertTrue(json.contains("Main St|Springfield"), "compact value expected, but got: " + json);
            assertFalse(json.contains("\"street\""), "default Address shape must not be written, but got: " + json);
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
