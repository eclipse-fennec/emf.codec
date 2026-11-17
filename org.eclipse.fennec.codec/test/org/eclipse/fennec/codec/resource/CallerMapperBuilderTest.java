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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.cfg.SerializerFactoryConfig;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ser.BasicSerializerFactory;

/**
 * A caller-supplied {@link JsonMapper.Builder} is a template (issue #324). Adding the
 * per-operation codec module to the caller's builder wrote that module's serializers back into
 * the builder on every {@code build()}: the builder grew with each save and load and kept every
 * earlier operation's module alive.
 */
@DisplayName("Caller-supplied JsonMapper.Builder")
class CallerMapperBuilderTest {

    private static final String NS_URI = "urn:codec:test:caller-mapper-builder";

    private MetadataWhiteboard metadataService;
    private EClass nodeClass;
    private EAttribute nodeId;
    private EAttribute nodeLabel;

    @BeforeEach
    void setUp() {
        EcoreFactory f = EcoreFactory.eINSTANCE;
        nodeClass = f.createEClass();
        nodeClass.setName("Node");
        nodeId = f.createEAttribute();
        nodeId.setName("id");
        nodeId.setEType(EcorePackage.Literals.ESTRING);
        nodeId.setID(true);
        nodeClass.getEStructuralFeatures().add(nodeId);
        nodeLabel = f.createEAttribute();
        nodeLabel.setName("label");
        nodeLabel.setEType(EcorePackage.Literals.ESTRING);
        nodeClass.getEStructuralFeatures().add(nodeLabel);

        EPackage pkg = f.createEPackage();
        pkg.setName("callermapper");
        pkg.setNsPrefix("callermapper");
        pkg.setNsURI(NS_URI);
        pkg.getEClassifiers().add(nodeClass);
        new ResourceImpl(URI.createURI(NS_URI)).getContents().add(pkg);
        EPackage.Registry.INSTANCE.put(NS_URI, pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
    }

    @Test
    @DisplayName("a second save on the same resource uses its own options")
    void secondSaveUsesItsOwnOptions() throws IOException {
        CodecResource resource = resource(JsonMapper.builder());
        resource.getContents().add(node("n1"));

        String first = save(resource, "firstKey");
        String second = save(resource, "secondKey");

        assertTrue(first.contains("\"firstKey\""), first);
        assertTrue(second.contains("\"secondKey\""), second);
        assertFalse(second.contains("\"firstKey\""), second);
    }

    @Test
    @DisplayName("a module-level option of the second save is honoured")
    void secondSaveModuleOption() throws IOException {
        CodecResource resource = resource(JsonMapper.builder());
        resource.getContents().add(node("n1"));

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        resource.save(out1, Map.of());
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        resource.save(out2, Map.of(CodecOptions.CODEC_IGNORE_FEATURES, List.of("label")));
        String first = out1.toString(StandardCharsets.UTF_8);
        String second = out2.toString(StandardCharsets.UTF_8);

        assertTrue(first.contains("\"label-n1\""), first);
        assertFalse(second.contains("\"label-n1\""), "first=" + first + " second=" + second);
    }

    @Test
    @DisplayName("resources sharing one builder each use their own options")
    void sharedBuilderAcrossResources() throws IOException {
        JsonMapper.Builder shared = JsonMapper.builder();
        CodecResource a = resource(shared);
        a.getContents().add(node("a"));
        CodecResource b = resource(shared);
        b.getContents().add(node("b"));

        String fromA = save(a, "keyA");
        String fromB = save(b, "keyB");

        assertTrue(fromA.contains("\"keyA\""), fromA);
        assertTrue(fromB.contains("\"keyB\""), fromB);
        assertFalse(fromB.contains("\"keyA\""), fromB);
    }

    @Test
    @DisplayName("saves do not grow the caller's builder")
    void callerBuilderDoesNotGrow() throws Exception {
        JsonMapper.Builder builder = JsonMapper.builder();
        CodecResource resource = resource(builder);
        resource.getContents().add(node("n1"));
        // Jackson's own first build() of the builder adds its java.time setup once
        int before = serializerProviders(builder);

        for (int i = 0; i < 3; i++) {
            save(resource, "k");
        }

        assertEquals(before, serializerProviders(builder));
    }

    /** Jackson has no public view of a builder's serializer providers, hence reflection. */
    private static int serializerProviders(JsonMapper.Builder builder) throws ReflectiveOperationException {
        Field field = BasicSerializerFactory.class.getDeclaredField("_factoryConfig");
        field.setAccessible(true);
        SerializerFactoryConfig config = (SerializerFactoryConfig) field.get(builder.serializerFactory());
        int count = 0;
        for (Object ignored : config.serializers()) {
            count++;
        }
        return count;
    }

    private CodecResource resource(JsonMapper.Builder builder) {
        return new CodecResource(URI.createURI("test://caller-mapper-builder.json"),
                metadataService, ConfigurationResolver.defaults(), builder);
    }

    private EObject node(String id) {
        EObject node = nodeClass.getEPackage().getEFactoryInstance().create(nodeClass);
        node.eSet(nodeId, id);
        node.eSet(nodeLabel, "label-" + id);
        return node;
    }

    private static String save(CodecResource resource, String idKey) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of(CodecOptions.CODEC_ID_KEY, idKey));
        return out.toString(StandardCharsets.UTF_8);
    }
}
