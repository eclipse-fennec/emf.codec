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
package org.eclipse.fennec.codec.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.dataformat.yaml.YAMLFactory;

/**
 * The read limits reach YAML (issue #232).
 * <p>
 * The provider parses with its own factory, which used to run on Jackson's defaults: the
 * codec's limits never reached it. They are now handed over per load, and each
 * {@code codec.max*} option bounds a YAML document like a JSON one.
 * </p>
 */
@DisplayName("YAML read limits")
class YamlReadLimitsTest {

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass docClass;

    @BeforeEach
    void setUp() {
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("limits");
        testPackage.setNsPrefix("limits");
        testPackage.setNsURI("http://test.org/limits/YAML/1.0");
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
        byte[] doc = document(g -> g.writeStringProperty("name", "y".repeat(9_000_000))
                .writeStringProperty("other", "z".repeat(8_000_000)));

        assertThrows(IOException.class, () -> load(doc, Map.of()));
    }

    @Test
    @DisplayName("a document below 16 MiB loads")
    void payloadBelowDefault() throws IOException {
        EObject loaded = load(document(g -> g.writeStringProperty("name", "y".repeat(5_000_000))), Map.of());

        assertEquals(5_000_000, ((String) loaded.eGet(docClass.getEStructuralFeature("name"))).length());
    }

    @Test
    @DisplayName("codec.maxPayloadSize lowers the document limit")
    void payloadLowered() {
        byte[] doc = document(g -> g.writeStringProperty("name", "y".repeat(60_000)));

        assertThrows(IOException.class, () -> load(doc, Map.of(CodecOptions.CODEC_MAX_PAYLOAD_SIZE, 20_000)));
    }

    @Test
    @DisplayName("codec.maxStringLength bounds a string value")
    void stringLength() {
        byte[] doc = document(g -> g.writeStringProperty("name", "y".repeat(2_000)));

        assertThrows(IOException.class, () -> load(doc, Map.of(CodecOptions.CODEC_MAX_STRING_LENGTH, 1_000)));
    }

    @Test
    @DisplayName("the default string limit of 10 MB applies")
    void stringDefault() {
        byte[] doc = document(g -> g.writeStringProperty("name", "y".repeat(12_000_000)));

        assertThrows(IOException.class, () -> load(doc, Map.of(CodecOptions.CODEC_MAX_PAYLOAD_SIZE, 64_000_000)));
    }

    @Test
    @DisplayName("codec.maxNameLength bounds a property name")
    void nameLength() {
        byte[] doc = document(g -> g.writeNumberProperty("k".repeat(200), 1).writeStringProperty("name", "a"));

        assertThrows(IOException.class, () -> load(doc, Map.of(CodecOptions.CODEC_MAX_NAME_LENGTH, 100)));
    }

    @Test
    @DisplayName("codec.maxNestingDepth bounds nesting")
    void nestingDepth() {
        byte[] doc = nested(60);

        assertThrows(IOException.class, () -> load(doc, Map.of(CodecOptions.CODEC_MAX_NESTING_DEPTH, 50)));
    }

    @Test
    @DisplayName("nesting below the default loads")
    void nestingBelowDefault() throws IOException {
        EObject loaded = load(nested(100), Map.of());

        assertTrue(loaded.eIsSet(docClass.getEStructuralFeature("child")));
    }

    private byte[] document(Consumer<JsonGenerator> body) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = new YAMLFactory().createGenerator(ObjectWriteContext.empty(), out)) {
            g.writeStartObject();
            body.accept(g);
            g.writeEndObject();
        }
        return out.toByteArray();
    }

    private byte[] nested(int depth) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = new YAMLFactory().createGenerator(ObjectWriteContext.empty(), out)) {
            for (int i = 0; i < depth; i++) {
                g.writeStartObject();
                g.writeName("child");
            }
            g.writeStartObject();
            g.writeStringProperty("name", "leaf");
            g.writeEndObject();
            for (int i = 0; i < depth; i++) {
                g.writeEndObject();
            }
        }
        return out.toByteArray();
    }

    private EObject load(byte[] doc, Map<String, Object> extra) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://limits.yaml"), metadataService,
                ConfigurationResolver.defaults(), null, null, new YamlFormatProvider());
        Map<String, Object> options = new HashMap<>(extra);
        options.put(CodecResource.CODEC_ROOT_TYPE, docClass);
        resource.load(new ByteArrayInputStream(doc), options);
        return resource.getContents().get(0);
    }
}
