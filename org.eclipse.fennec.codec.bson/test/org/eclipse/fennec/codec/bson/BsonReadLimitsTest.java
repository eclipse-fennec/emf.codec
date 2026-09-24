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
package org.eclipse.fennec.codec.bson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
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
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The read limits reach BSON (issue #232).
 * <p>
 * BSON parses with the BSON library, not Jackson, so none of the codec's parser limits applied:
 * only the payload size was checked, a string above 10 MB was accepted, and the recursive
 * decoder overflowed the stack on a 120 KB document with 10 000 nested levels. The raw document
 * is now checked iteratively before it is decoded.
 * </p>
 */
@DisplayName("BSON read limits")
class BsonReadLimitsTest {

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass docClass;

    @BeforeEach
    void setUp() {
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("limits");
        testPackage.setNsPrefix("limits");
        testPackage.setNsURI("http://test.org/limits/bson/1.0");
        docClass = EcoreFactory.eINSTANCE.createEClass();
        docClass.setName("Doc");
        testPackage.getEClassifiers().add(docClass);
        EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
        name.setName("name");
        name.setEType(EcorePackage.Literals.ESTRING);
        docClass.getEStructuralFeatures().add(name);
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

    @Test
    @DisplayName("a document above 16 MiB is refused by default")
    void payloadDefault() {
        byte[] doc = bson(new BsonDocument("name", new BsonString("y".repeat(9_000_000)))
                .append("other", new BsonString("z".repeat(8_000_000))));

        assertThrows(IOException.class, () -> load(doc, Map.of()));
    }

    @Test
    @DisplayName("codec.maxPayloadSize lowers the document limit")
    void payloadLowered() {
        byte[] doc = bson(new BsonDocument("name", new BsonString("y".repeat(3_000))));

        assertThrows(IOException.class, () -> load(doc, Map.of(CodecOptions.CODEC_MAX_PAYLOAD_SIZE, 2_000)));
    }

    @Test
    @DisplayName("codec.maxPayloadSize raises the document limit")
    void payloadRaised() throws IOException {
        byte[] doc = bson(new BsonDocument("name", new BsonString("y".repeat(9_000_000)))
                .append("other", new BsonString("z".repeat(8_000_000))));

        EObject loaded = load(doc, Map.of(CodecOptions.CODEC_MAX_PAYLOAD_SIZE, 32L * 1024 * 1024));

        assertEquals(9_000_000, ((String) loaded.eGet(docClass.getEStructuralFeature("name"))).length());
    }

    @Test
    @DisplayName("10 000 nested levels fail with an IOException, not a StackOverflowError")
    void deepNestingIsRefused() {
        IOException failure = assertThrows(IOException.class, () -> load(nested(10_000), Map.of()));

        assertTrue(failure.getMessage().contains(CodecOptions.CODEC_MAX_NESTING_DEPTH), failure::getMessage);
    }

    @Test
    @DisplayName("codec.maxNestingDepth bounds nesting")
    void nestingDepth() {
        assertThrows(IOException.class, () -> load(nested(60), Map.of(CodecOptions.CODEC_MAX_NESTING_DEPTH, 50)));
    }

    @Test
    @DisplayName("nesting below the default loads")
    void nestingBelowDefault() throws IOException {
        EObject loaded = load(nested(100), Map.of());

        assertTrue(loaded.eIsSet(docClass.getEStructuralFeature("child")));
    }

    @Test
    @DisplayName("codec.maxStringLength bounds a string value")
    void stringLength() {
        byte[] doc = bson(new BsonDocument("name", new BsonString("y".repeat(2_000))));

        assertThrows(IOException.class, () -> load(doc, Map.of(CodecOptions.CODEC_MAX_STRING_LENGTH, 1_000)));
    }

    @Test
    @DisplayName("the default string limit of 10 MB applies")
    void stringDefault() {
        byte[] doc = bson(new BsonDocument("name", new BsonString("y".repeat(12_000_000))));

        assertThrows(IOException.class, () -> load(doc, Map.of()));
    }

    @Test
    @DisplayName("codec.maxNameLength bounds a property name")
    void nameLength() {
        byte[] doc = bson(new BsonDocument("k".repeat(200), new BsonInt32(1)).append("name", new BsonString("a")));

        assertThrows(IOException.class, () -> load(doc, Map.of(CodecOptions.CODEC_MAX_NAME_LENGTH, 100)));
    }

    @Test
    @DisplayName("a string inside an array is checked too")
    void stringInArray() {
        byte[] doc = bson(new BsonDocument("name", new BsonString("a")).append("list",
                new BsonArray(List.of(new BsonString("y".repeat(2_000))))));

        assertThrows(IOException.class, () -> load(doc, Map.of(CodecOptions.CODEC_MAX_STRING_LENGTH, 1_000)));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static byte[] bson(BsonDocument document) {
        BasicOutputBuffer buffer = new BasicOutputBuffer();
        new BsonDocumentCodec().encode(new BsonBinaryWriter(buffer), document, EncoderContext.builder().build());
        return buffer.toByteArray();
    }

    /** {@code depth} nested {"child": {...}} documents, written byte by byte - no recursion here either. */
    private static byte[] nested(int depth) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] name = "child\0".getBytes(StandardCharsets.US_ASCII);
        for (int level = depth; level >= 1; level--) {
            out.writeBytes(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(5 + 12 * level).array());
            out.write(0x03);
            out.writeBytes(name);
        }
        out.writeBytes(new byte[] { 5, 0, 0, 0, 0 });
        for (int level = 0; level < depth; level++) {
            out.write(0);
        }
        return out.toByteArray();
    }

    private EObject load(byte[] doc, Map<String, Object> extra) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://limits.bson"), metadataService,
                ConfigurationResolver.defaults(), null, null, new BsonFormatProvider());
        Map<String, Object> options = new HashMap<>(extra);
        options.put(CodecResource.CODEC_ROOT_TYPE, docClass);
        resource.load(new ByteArrayInputStream(doc), options);
        return resource.getContents().get(0);
    }
}
