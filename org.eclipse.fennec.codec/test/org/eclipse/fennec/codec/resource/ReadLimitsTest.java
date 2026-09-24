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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.format.jackson.JacksonFormatProvider;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import tools.jackson.core.json.JsonFactory;

/**
 * Read limits are secure by default and overridable per load (issue #232).
 * <p>
 * Five load options bound what one document may cost: {@code codec.maxPayloadSize} (16 MiB),
 * {@code codec.maxNestingDepth} (500), {@code codec.maxStringLength} (10 MB),
 * {@code codec.maxNameLength} (10 KB) and {@code codec.maxCollectionSize} (100 000). They are
 * checked on the default JSON path and on the format-provider path alike - the latter used to
 * run on Jackson's defaults, because the provider parses with its own factory.
 * </p>
 */
@DisplayName("Read limits")
class ReadLimitsTest {

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass docClass;
    private EAttribute metadataAttribute;

    @BeforeEach
    void setUp() {
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("limits");
        testPackage.setNsPrefix("limits");
        testPackage.setNsURI("http://test.org/limits/1.0");
        docClass = EcoreFactory.eINSTANCE.createEClass();
        docClass.setName("Doc");
        testPackage.getEClassifiers().add(docClass);
        EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
        name.setName("name");
        name.setEType(EcorePackage.Literals.ESTRING);
        docClass.getEStructuralFeatures().add(name);
        metadataAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        metadataAttribute.setName("metadata");
        metadataAttribute.setEType(EcorePackage.Literals.EJAVA_OBJECT);
        docClass.getEStructuralFeatures().add(metadataAttribute);
        EReference child = EcoreFactory.eINSTANCE.createEReference();
        child.setName("child");
        child.setEType(docClass);
        child.setContainment(true);
        docClass.getEStructuralFeatures().add(child);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
    }

    // ========================================================================
    // Defaults
    // ========================================================================

    @Test
    @DisplayName("the defaults are the published constants")
    void defaults() {
        assertEquals(16L * 1024 * 1024, CodecOptions.DEFAULT_MAX_PAYLOAD_SIZE);
        assertEquals(500, CodecOptions.DEFAULT_MAX_NESTING_DEPTH);
        assertEquals(10_000_000, CodecOptions.DEFAULT_MAX_STRING_LENGTH);
        assertEquals(10_000, CodecOptions.DEFAULT_MAX_NAME_LENGTH);
        assertEquals(100_000, CodecOptions.DEFAULT_MAX_COLLECTION_SIZE);
    }

    @ParameterizedTest(name = "via format provider: {0}")
    @ValueSource(booleans = { false, true })
    @DisplayName("a document above 16 MiB is refused by default")
    void payloadDefault(boolean viaProvider) {
        String json = "{\"name\":\"" + "y".repeat(9_000_000) + "\",\"metadata\":\"" + "z".repeat(8_000_000) + "\"}";

        assertThrows(IOException.class, () -> load(json, viaProvider, Map.of()));
    }

    // ========================================================================
    // Overrides
    // ========================================================================

    @ParameterizedTest(name = "via format provider: {0}")
    @ValueSource(booleans = { false, true })
    @DisplayName("codec.maxPayloadSize lowers the document limit")
    void payloadLowered(boolean viaProvider) {
        // Jackson checks the document size when it refills its input buffer (~8 KB), so the
        // document has to be well past both the limit and one buffer
        String json = "{\"name\":\"" + "y".repeat(60_000) + "\"}";

        assertThrows(IOException.class,
                () -> load(json, viaProvider, Map.of(CodecOptions.CODEC_MAX_PAYLOAD_SIZE, 20_000)));
    }

    @ParameterizedTest(name = "via format provider: {0}")
    @ValueSource(booleans = { false, true })
    @DisplayName("codec.maxPayloadSize raises the document limit")
    void payloadRaised(boolean viaProvider) throws IOException {
        String json = "{\"name\":\"" + "y".repeat(9_000_000) + "\",\"metadata\":\"" + "z".repeat(8_000_000) + "\"}";

        EObject doc = load(json, viaProvider, Map.of(CodecOptions.CODEC_MAX_PAYLOAD_SIZE, 32L * 1024 * 1024));

        assertEquals(9_000_000, ((String) doc.eGet(docClass.getEStructuralFeature("name"))).length());
    }

    @ParameterizedTest(name = "via format provider: {0}")
    @ValueSource(booleans = { false, true })
    @DisplayName("codec.maxStringLength bounds a single string value")
    void stringLength(boolean viaProvider) {
        String json = "{\"name\":\"" + "y".repeat(2_000) + "\"}";

        assertThrows(IOException.class,
                () -> load(json, viaProvider, Map.of(CodecOptions.CODEC_MAX_STRING_LENGTH, 1_000)));
    }

    @ParameterizedTest(name = "via format provider: {0}")
    @ValueSource(booleans = { false, true })
    @DisplayName("codec.maxNameLength bounds a property name")
    void nameLength(boolean viaProvider) {
        String json = "{\"" + "k".repeat(200) + "\":1,\"name\":\"a\"}";

        assertThrows(IOException.class,
                () -> load(json, viaProvider, Map.of(CodecOptions.CODEC_MAX_NAME_LENGTH, 100)));
    }

    @ParameterizedTest(name = "via format provider: {0}")
    @ValueSource(booleans = { false, true })
    @DisplayName("codec.maxNestingDepth bounds nesting")
    void nestingDepth(boolean viaProvider) {
        String json = nested(60);

        assertThrows(IOException.class,
                () -> load(json, viaProvider, Map.of(CodecOptions.CODEC_MAX_NESTING_DEPTH, 50)));
    }

    @ParameterizedTest(name = "via format provider: {0}")
    @ValueSource(booleans = { false, true })
    @DisplayName("nesting below the default limit still loads")
    void nestingBelowDefault(boolean viaProvider) throws IOException {
        EObject doc = load(nested(100), viaProvider, Map.of());

        assertTrue(doc.eIsSet(docClass.getEStructuralFeature("child")));
    }

    @ParameterizedTest(name = "via format provider: {0}")
    @ValueSource(booleans = { false, true })
    @DisplayName("codec.maxCollectionSize truncates an untyped collection, with a warning")
    void collectionSize(boolean viaProvider) throws IOException {
        String json = "{\"name\":\"a\",\"metadata\":[1,2,3,4,5,6,7,8,9,10,11,12]}";
        Map<String, Object> options = new HashMap<>();
        options.put(CodecOptions.CODEC_MAX_COLLECTION_SIZE, 5);

        CodecResource resource = newResource(viaProvider);
        options.put(CodecResource.CODEC_ROOT_TYPE, docClass);
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), options);

        assertEquals(5, ((List<?>) resource.getContents().get(0).eGet(metadataAttribute)).size());
        assertTrue(resource.getWarnings().stream().anyMatch(w -> w.getMessage().contains("maximum size")),
                () -> "warnings=" + resource.getWarnings());
    }

    // ========================================================================
    // Invalid values
    // ========================================================================

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "0", "-1", "lots" })
    @DisplayName("an unusable limit fails the load instead of silently falling back")
    void invalidValue(String value) {
        IOException failure = assertThrows(IOException.class,
                () -> load("{\"name\":\"a\"}", false, Map.of(CodecOptions.CODEC_MAX_PAYLOAD_SIZE, value)));

        assertTrue(failure.getMessage().contains(CodecOptions.CODEC_MAX_PAYLOAD_SIZE), failure::getMessage);
    }

    @Test
    @DisplayName("a limit may be given as a string")
    void stringValue() throws IOException {
        assertThrows(IOException.class, () -> load("{\"name\":\"" + "y".repeat(2_000) + "\"}", false,
                Map.of(CodecOptions.CODEC_MAX_STRING_LENGTH, "1000")));
        load("{\"name\":\"a\"}", false, Map.of(CodecOptions.CODEC_MAX_STRING_LENGTH, "1000"));
    }

    @Test
    @DisplayName("a StackOverflowError while loading arrives as an IOException")
    void stackOverflowIsTranslated() {
        CodecResource resource = newResource(false);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, docClass);
        options.put(CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES,
                Map.of(docClass.getEStructuralFeature("name"), new CodecValueReader<Object, EAttribute>() {
                    @Override
                    public String getName() {
                        return "overflowing";
                    }

                    @Override
                    public Object read(CodecReaderContext ctx, EAttribute feature) {
                        throw new StackOverflowError();
                    }
                }));

        IOException failure = assertThrows(IOException.class,
                () -> resource.load(new ByteArrayInputStream("{\"name\":\"a\"}".getBytes(UTF_8)), options));

        assertInstanceOf(StackOverflowError.class, failure.getCause());
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static String nested(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("{\"child\":");
        }
        sb.append("{\"name\":\"leaf\"}");
        sb.append("}".repeat(depth));
        return sb.toString();
    }

    private CodecResource newResource(boolean viaProvider) {
        URI uri = URI.createURI("test://limits.json");
        if (viaProvider) {
            return new CodecResource(uri, metadataService, ConfigurationResolver.defaults(), null, null,
                    new JacksonFormatProvider("json", new JsonFactory()));
        }
        return new CodecResource(uri, metadataService, ConfigurationResolver.defaults(), null);
    }

    private EObject load(String json, boolean viaProvider, Map<String, Object> extra) throws IOException {
        CodecResource resource = newResource(viaProvider);
        Map<String, Object> options = new HashMap<>(extra);
        options.put(CodecResource.CODEC_ROOT_TYPE, docClass);
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), options);
        return resource.getContents().get(0);
    }
}
