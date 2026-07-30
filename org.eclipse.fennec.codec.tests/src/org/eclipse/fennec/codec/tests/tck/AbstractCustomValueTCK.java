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
package org.eclipse.fennec.codec.tests.tck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for custom value reader/writer tests.
 */
public abstract class AbstractCustomValueTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-customvalue.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass dataRecordClass;
    private EAttribute idAttr;
    private EAttribute labelAttr;
    private EAttribute valueAttr;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCustomValueTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        dataRecordClass = EcoreHelper.getEClass(testPackage, "DataRecord");
        idAttr = (EAttribute) EcoreHelper.getFeature(dataRecordClass, "id");
        labelAttr = (EAttribute) EcoreHelper.getFeature(dataRecordClass, "label");
        valueAttr = (EAttribute) EcoreHelper.getFeature(dataRecordClass, "value");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("custom value reader/writer round-trip")
    void customValueReaderWriterRoundTrip() throws IOException {
        UppercaseWriter uppercaseWriter = new UppercaseWriter();
        LowercaseReader lowercaseReader = new LowercaseReader();

        EObject record = testPackage.getEFactoryInstance().create(dataRecordClass);
        record.eSet(idAttr, "rec-1");
        record.eSet(labelAttr, "Hello");
        record.eSet(valueAttr, 3.14);

        // Serialize with uppercase writer
        CodecResource saveResource = createResource();
        saveResource.getContents().add(record);

        Map<String, Object> saveOptions = new HashMap<>();
        Map<EStructuralFeature, CodecValueWriter<?, ?>> writers = new HashMap<>();
        writers.put(labelAttr, uppercaseWriter);
        saveOptions.put(CodecOptions.CODEC_FEATURE_VALUE_WRITER_INSTANCES, writers);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, saveOptions);

        // Deserialize with lowercase reader
        CodecResource loadResource = createResource();
        Map<String, Object> loadOptions = new HashMap<>();
        loadOptions.put(CodecResource.CODEC_ROOT_TYPE, dataRecordClass);
        Map<EStructuralFeature, CodecValueReader<?, ?>> readers = new HashMap<>();
        readers.put(labelAttr, lowercaseReader);
        loadOptions.put(CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES, readers);

        loadResource.load(new ByteArrayInputStream(out.toByteArray()), loadOptions);

        EObject loaded = loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
        assertNotNull(loaded);
        assertEquals("rec-1", loaded.eGet(idAttr));
        assertEquals("hello", loaded.eGet(labelAttr), "Label should be lowercase after custom reader");
        assertEquals(3.14, (Double) loaded.eGet(valueAttr), 0.001);
    }

    @Test
    @DisplayName("writer resolved by name from registry via save options")
    void writerResolvedByNameFromRegistry() throws IOException {
        UppercaseWriter uppercaseWriter = new UppercaseWriter();

        CodecValueRegistry registry = new CodecValueRegistry();
        registry.register(uppercaseWriter);

        EObject record = testPackage.getEFactoryInstance().create(dataRecordClass);
        record.eSet(idAttr, "rec-2");
        record.eSet(labelAttr, "hello");
        record.eSet(valueAttr, 1.0);

        CodecResource saveResource = createResource(registry);
        saveResource.getContents().add(record);

        Map<String, Object> saveOptions = new HashMap<>();
        saveOptions.put(CodecOptions.CODEC_FEATURE_VALUE_WRITERS, Map.of(labelAttr, "uppercaseWriter"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, saveOptions);

        // Verify the serialized form contains the uppercase value
        String serialized = out.toString(java.nio.charset.StandardCharsets.UTF_8);
        assertNotNull(serialized);
        assertEquals(true, serialized.contains("HELLO"),
                "Serialized output should contain uppercase HELLO, but got: " + serialized);
    }

    @Test
    @DisplayName("reader resolved by name from registry via load options")
    void readerResolvedByNameFromRegistry() throws IOException {
        LowercaseReader lowercaseReader = new LowercaseReader();

        CodecValueRegistry registry = new CodecValueRegistry();
        registry.register(lowercaseReader);

        EObject record = testPackage.getEFactoryInstance().create(dataRecordClass);
        record.eSet(idAttr, "rec-3");
        record.eSet(labelAttr, "UPPERCASE");
        record.eSet(valueAttr, 2.0);

        // Serialize without any custom writer (plain)
        CodecResource saveResource = createResource(null);
        saveResource.getContents().add(record);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Map.of());

        // Deserialize with the lowercase reader resolved by name
        CodecResource loadResource = createResource(registry);
        Map<String, Object> loadOptions = new HashMap<>();
        loadOptions.put(CodecResource.CODEC_ROOT_TYPE, dataRecordClass);
        loadOptions.put(CodecOptions.CODEC_FEATURE_VALUE_READERS, Map.of(labelAttr, "lowercaseReader"));

        loadResource.load(new ByteArrayInputStream(out.toByteArray()), loadOptions);

        EObject loaded = loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
        assertNotNull(loaded);
        assertEquals("uppercase", loaded.eGet(labelAttr),
                "Label should be lowercase after name-resolved reader");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource() {
        return createResource(null);
    }

    private CodecResource createResource(CodecValueRegistry registry) {
        return new CodecResource(
                URI.createURI("test://customvalue." + getFileExtension()),
                metadataService, ConfigurationResolver.defaults(),
                registry, null, createFormatProvider());
    }

    /**
     * Custom writer that converts strings to uppercase.
     */
    static class UppercaseWriter implements CodecValueWriter<String, EAttribute> {

        @Override
        public String getName() {
            return "uppercaseWriter";
        }

        @Override
        public void write(String value, EAttribute feature, CodecWriterContext ctx) throws IOException {
            ctx.getGenerator().writeString(value != null ? value.toUpperCase() : null);
        }
    }

    /**
     * Custom reader that converts strings to lowercase.
     */
    static class LowercaseReader implements CodecValueReader<String, EAttribute> {

        @Override
        public String getName() {
            return "lowercaseReader";
        }

        @Override
        public String read(CodecReaderContext ctx, EAttribute feature) throws IOException {
            String text = ctx.getParser().getString();
            return text != null ? text.toLowerCase() : null;
        }
    }
}
