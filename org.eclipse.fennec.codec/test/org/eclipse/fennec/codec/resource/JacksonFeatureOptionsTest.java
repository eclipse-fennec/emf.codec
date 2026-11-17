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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson features handed over as {@code codec.jackson.<FeatureType>.<FEATURE_NAME>} options
 * (issue #324, spec 13-load-save-options.md §11).
 */
@DisplayName("Jackson feature options")
class JacksonFeatureOptionsTest {

    private static final String NS_URI = "urn:codec:test:jackson-features";
    private static final String TRAILING = "codec.jackson.DeserializationFeature.FAIL_ON_TRAILING_TOKENS";

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
        pkg.setName("jacksonfeatures");
        pkg.setNsPrefix("jacksonfeatures");
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

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("a JsonWriteFeature reaches the generator")
        void jsonWriteFeature() throws IOException {
            String json = save(Map.of("codec.jackson.JsonWriteFeature.ESCAPE_NON_ASCII", true));

            assertTrue(json.contains("gr\\u00FCn"), json);
        }

        @Test
        @DisplayName("a SerializationFeature given as string reaches the mapper")
        void serializationFeatureAsString() throws IOException {
            String json = save(Map.of("codec.jackson.SerializationFeature.INDENT_OUTPUT", "TRUE"));

            assertTrue(json.contains("\n"), json);
        }

        @Test
        @DisplayName("without the option the output is unchanged")
        void withoutOption() throws IOException {
            String json = save(Map.of());

            assertTrue(json.contains("grün"), json);
            assertFalse(json.contains("\n"), json);
        }

        @Test
        @DisplayName("the resource's default save options count, a save's own option wins")
        void defaultSaveOptions() throws IOException {
            CodecResource resource = resource(null);
            resource.getContents().add(node());
            resource.getDefaultSaveOptions().put("codec.jackson.SerializationFeature.INDENT_OUTPUT", true);

            assertTrue(save(resource, Map.of()).contains("\n"));
            assertFalse(save(resource, Map.of("codec.jackson.SerializationFeature.INDENT_OUTPUT", false))
                    .contains("\n"));
        }

        @Test
        @DisplayName("an option wins over the caller's mapper builder")
        void optionOverBuilder() throws IOException {
            CodecResource resource = resource(JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT));
            resource.getContents().add(node());

            String json = save(resource, Map.of("codec.jackson.SerializationFeature.INDENT_OUTPUT", false));

            assertFalse(json.contains("\n"), json);
        }
    }

    @Nested
    @DisplayName("load")
    class Load {

        @Test
        @DisplayName("a JsonReadFeature reaches the parser")
        void jsonReadFeature() throws IOException {
            String json = withComment(save(Map.of()));

            Resource resource = load(json, Map.of("codec.jackson.JsonReadFeature.ALLOW_JAVA_COMMENTS", true));

            assertEquals(1, resource.getContents().size());
        }

        @Test
        @DisplayName("without the option a comment fails the load")
        void withoutOption() throws IOException {
            String json = withComment(save(Map.of()));

            assertThrows(IOException.class, () -> load(json, Map.of()));
        }

        @Test
        @DisplayName("the resource's default load options count")
        void defaultLoadOptions() throws IOException {
            String json = withComment(save(Map.of()));
            CodecResource resource = resource(null);
            resource.getDefaultLoadOptions().put("codec.jackson.JsonReadFeature.ALLOW_JAVA_COMMENTS", true);

            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            assertEquals(1, resource.getContents().size());
        }
    }

    @Nested
    @DisplayName("codec-owned features")
    class CodecOwned {

        @Test
        @DisplayName("are kept and the option is reported")
        void keptByDefault() throws IOException {
            Resource resource = load(save(Map.of()), Map.of(TRAILING, true));

            assertTrue(warnings(resource).stream().anyMatch(m -> m.contains("FAIL_ON_TRAILING_TOKENS")
                    && m.contains("is set by the codec") && m.contains(CodecOptions.CODEC_JACKSON_ALLOW_OVERRIDE)),
                    warnings(resource).toString());
        }

        @Test
        @DisplayName("are overridden with allowOverride, reported as such")
        void overridden() throws IOException {
            Resource resource = load(save(Map.of()),
                    Map.of(TRAILING, true, CodecOptions.CODEC_JACKSON_ALLOW_OVERRIDE, true));

            assertFalse(warnings(resource).stream().anyMatch(m -> m.contains("is set by the codec")),
                    warnings(resource).toString());
            assertTrue(warnings(resource).stream().anyMatch(m -> m.contains("FAIL_ON_TRAILING_TOKENS")
                    && m.contains("overrides")), warnings(resource).toString());
        }

        @Test
        @DisplayName("an option for a feature the codec does not own is not reported")
        void notOwned() throws IOException {
            CodecResource resource = resource(null);
            resource.getContents().add(node());
            save(resource, Map.of("codec.jackson.SerializationFeature.INDENT_OUTPUT", true));

            assertEquals(List.of(), warnings(resource));
        }
    }

    @Nested
    @DisplayName("unusable options")
    class Unusable {

        @Test
        @DisplayName("an unknown feature type is reported")
        void unknownType() throws IOException {
            assertReported(Map.of("codec.jackson.NopeFeature.X", true), "Unknown Jackson feature type 'NopeFeature'");
        }

        @Test
        @DisplayName("an unknown feature name is reported")
        void unknownName() throws IOException {
            assertReported(Map.of("codec.jackson.SerializationFeature.NOPE", true),
                    "Unknown Jackson feature 'SerializationFeature.NOPE'");
        }

        @Test
        @DisplayName("a value that is not a boolean is reported")
        void badValue() throws IOException {
            assertReported(Map.of("codec.jackson.SerializationFeature.INDENT_OUTPUT", "yes"),
                    "Invalid value 'yes'");
        }

        private void assertReported(Map<String, Object> options, String expected) throws IOException {
            CodecResource resource = resource(null);
            resource.getContents().add(node());
            String json = save(resource, options);

            assertFalse(json.contains("\n"), json);
            assertTrue(warnings(resource).stream().anyMatch(m -> m.contains(expected)),
                    warnings(resource).toString());
        }
    }

    private CodecResource resource(JsonMapper.Builder builder) {
        return new CodecResource(URI.createURI("test://jackson-features.json"),
                metadataService, ConfigurationResolver.defaults(), builder);
    }

    private EObject node() {
        EObject node = nodeClass.getEPackage().getEFactoryInstance().create(nodeClass);
        node.eSet(nodeId, "n1");
        node.eSet(nodeLabel, "grün");
        return node;
    }

    private String save(Map<String, Object> options) throws IOException {
        CodecResource resource = resource(null);
        resource.getContents().add(node());
        return save(resource, options);
    }

    private static String save(CodecResource resource, Map<String, Object> options) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return out.toString(StandardCharsets.UTF_8);
    }

    private Resource load(String json, Map<String, Object> options) throws IOException {
        CodecResource resource = resource(null);
        Map<String, Object> loadOptions = new HashMap<>(options);
        loadOptions.put(CodecOptions.CODEC_ROOT_TYPE, nodeClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), loadOptions);
        return resource;
    }

    private static String withComment(String json) {
        return json.replaceFirst("\\{", "{ /* a comment */ ");
    }

    private static List<String> warnings(Resource resource) {
        return resource.getWarnings().stream().map(Resource.Diagnostic::getMessage).toList();
    }
}
