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
package org.eclipse.fennec.codec.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
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
 * Demonstrates custom value readers and writers that transform values
 * during serialization/deserialization.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec: Custom Value Readers/Writers</a>
 */
@DisplayName("Custom Value Examples")
class CustomValueExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-customvalue.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;

    private EClass eventClass;
    private EAttribute eventIdAttr;
    private EAttribute titleAttr;
    private EAttribute timestampAttr;

    // ========================================================================
    // Custom Writers
    // ========================================================================

    /** Writes string values in UPPERCASE */
    static class UppercaseWriter implements CodecValueWriter<String, EAttribute> {
        @Override
        public String getName() {
            return "uppercase";
        }

        @Override
        public void write(String value, EAttribute feature, CodecWriterContext ctx) throws IOException {
            ctx.getGenerator().writeString(value != null ? value.toUpperCase() : null);
        }
    }

    /** Writes timestamp (millis) as ISO-8601 string */
    static class TimestampToIsoWriter implements CodecValueWriter<Long, EAttribute> {
        @Override
        public String getName() {
            return "timestampToIso";
        }

        @Override
        public void write(Long value, EAttribute feature, CodecWriterContext ctx) throws IOException {
            if (value != null) {
                String iso = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(value));
                ctx.getGenerator().writeString(iso);
            } else {
                ctx.getGenerator().writeNull();
            }
        }
    }

    // ========================================================================
    // Custom Readers
    // ========================================================================

    /** Reads string values and converts to lowercase */
    static class LowercaseReader implements CodecValueReader<String, EAttribute> {
        @Override
        public String getName() {
            return "lowercase";
        }

        @Override
        public String read(CodecReaderContext ctx, EAttribute feature) throws IOException {
            String value = ctx.getParser().getString();
            return value != null ? value.toLowerCase() : null;
        }
    }

    /** Reads ISO-8601 string and converts to timestamp (millis) */
    static class IsoToTimestampReader implements CodecValueReader<Long, EAttribute> {
        @Override
        public String getName() {
            return "isoToTimestamp";
        }

        @Override
        public Long read(CodecReaderContext ctx, EAttribute feature) throws IOException {
            String iso = ctx.getParser().getString();
            return iso != null ? Instant.parse(iso).toEpochMilli() : 0L;
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, CustomValueExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        eventClass = EcoreHelper.getEClass(pkg, "Event");
        eventIdAttr = (EAttribute) EcoreHelper.getFeature(eventClass, "eventId");
        titleAttr = (EAttribute) EcoreHelper.getFeature(eventClass, "title");
        timestampAttr = (EAttribute) EcoreHelper.getFeature(eventClass, "timestamp");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("Uppercase writer + lowercase reader — 'Hello' → 'HELLO' → 'hello'")
    void uppercaseWriterLowercaseReader() throws IOException {
        EObject event = pkg.getEFactoryInstance().create(eventClass);
        event.eSet(eventIdAttr, "e1");
        event.eSet(titleAttr, "Hello");

        // Serialize with uppercase writer
        Map<String, Object> saveOptions = Map.of(
                CodecOptions.CODEC_FEATURE_VALUE_WRITER_INSTANCES,
                Map.of(titleAttr, new UppercaseWriter())
        );

        CodecResource saveResource = new CodecResource(
                URI.createURI("test://custom.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        saveResource.getContents().add(event);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, saveOptions);
        String json = out.toString(StandardCharsets.UTF_8);

        assertTrue(json.contains("\"HELLO\""), "Should contain uppercase HELLO");

        // Deserialize with lowercase reader
        Map<String, Object> loadOptions = new HashMap<>();
        loadOptions.put(CodecResource.CODEC_ROOT_TYPE, eventClass);
        loadOptions.put(CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES,
                Map.of(titleAttr, new LowercaseReader()));

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://custom.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), loadOptions);

        EObject loaded = loadResource.getContents().get(0);
        assertEquals("hello", loaded.eGet(titleAttr));
    }

    @Test
    @DisplayName("Timestamp to ISO-8601 — millis → ISO string → millis")
    void timestampFormatterRoundTrip() throws IOException {
        long millis = 1700000000000L; // 2023-11-14T22:13:20Z

        EObject event = pkg.getEFactoryInstance().create(eventClass);
        event.eSet(eventIdAttr, "e2");
        event.eSet(titleAttr, "Conference");
        event.eSet(timestampAttr, millis);

        // Serialize with ISO writer
        Map<String, Object> saveOptions = Map.of(
                CodecOptions.CODEC_FEATURE_VALUE_WRITER_INSTANCES,
                Map.of(timestampAttr, new TimestampToIsoWriter())
        );

        CodecResource saveResource = new CodecResource(
                URI.createURI("test://ts.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        saveResource.getContents().add(event);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, saveOptions);
        String json = out.toString(StandardCharsets.UTF_8);

        assertTrue(json.contains("2023-11-14"), "Should contain ISO date");

        // Deserialize with ISO reader
        Map<String, Object> loadOptions = new HashMap<>();
        loadOptions.put(CodecResource.CODEC_ROOT_TYPE, eventClass);
        loadOptions.put(CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES,
                Map.of(timestampAttr, new IsoToTimestampReader()));

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://ts.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), loadOptions);

        EObject loaded = loadResource.getContents().get(0);
        assertEquals(millis, loaded.eGet(timestampAttr));
    }
}
