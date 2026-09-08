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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.deser.CodecEObjectDeserializer;
import org.eclipse.fennec.codec.util.CodecResourceHelper;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * The root type and root schema options answer to both of their keys (issue #208).
 * <p>
 * The canonical dotted key from the spec ({@code codec.rootType}, {@code codec.rootSchema})
 * used to be inert: only the literal spelling {@code CodecResource} exposes was ever read,
 * so a caller following the spec got a load failure that named an option they had set. Both
 * keys now reach every reading site, and setting them to values that disagree is reported
 * rather than silently resolved by precedence.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/13-load-save-options.md">Spec 13 §2.1: Root Element Options</a>
 */
@DisplayName("Root options - both keys are read (issue #208)")
class RootOptionDualKeyTest {

    private static final String ROUNDTRIP_ECORE = "test-roundtrip.ecore";

    /** No type information in the document, so the root type option has to carry it. */
    private static final String UNTYPED_PERSON = "{\"name\":\"Alice\",\"age\":30}";

    private EcoreHelper ecoreHelper;
    private EPackage roundtripPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;
    private EClass addressClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        roundtripPackage = ecoreHelper.loadEcore(
                "/org/eclipse/fennec/codec/resource/" + ROUNDTRIP_ECORE, RootOptionDualKeyTest.class);
        EPackage.Registry.INSTANCE.put(roundtripPackage.getNsURI(), roundtripPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(roundtripPackage);

        personClass = EcoreHelper.getEClass(roundtripPackage, "Person");
        addressClass = EcoreHelper.getEClass(roundtripPackage, "Address");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(roundtripPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Nested
    @DisplayName("root type")
    class RootType {

        @Test
        @DisplayName("the canonical dotted key codec.rootType types the root")
        void dottedKey() throws IOException {
            EObject loaded = load(UNTYPED_PERSON, Map.of(CodecOptions.CODEC_ROOT_TYPE, personClass));

            assertPerson(loaded);
        }

        @Test
        @DisplayName("the literal key CODEC_ROOT_TYPE keeps working")
        void literalKey() throws IOException {
            EObject loaded = load(UNTYPED_PERSON, Map.of(CodecResource.CODEC_ROOT_TYPE, personClass));

            assertPerson(loaded);
        }

        @Test
        @DisplayName("the dotted key also accepts a type URI String")
        void dottedKeyWithUriString() throws IOException {
            String typeUri = roundtripPackage.getNsURI() + "#//Person";

            EObject loaded = load(UNTYPED_PERSON, Map.of(CodecOptions.CODEC_ROOT_TYPE, typeUri));

            assertPerson(loaded);
        }

        @Test
        @DisplayName("both keys with the same value are redundant, not a conflict")
        void bothKeysAgree() throws IOException {
            EObject loaded = load(UNTYPED_PERSON, Map.of(
                    CodecOptions.CODEC_ROOT_TYPE, personClass,
                    CodecResource.CODEC_ROOT_TYPE, personClass));

            assertPerson(loaded);
        }

        @Test
        @DisplayName("both keys with contradictory values fail the load")
        void bothKeysConflict() {
            Map<String, Object> options = Map.of(
                    CodecOptions.CODEC_ROOT_TYPE, personClass,
                    CodecResource.CODEC_ROOT_TYPE, addressClass);

            IOException failure = assertThrows(IOException.class, () -> load(UNTYPED_PERSON, options));

            assertTrue(failure.getMessage().contains(CodecOptions.CODEC_ROOT_TYPE)
                            && failure.getMessage().contains(CodecOptions.CODEC_ROOT_TYPE_LITERAL),
                    "the failure must name both keys, was: " + failure.getMessage());
            assertTrue(failure.getMessage().contains("Person") && failure.getMessage().contains("Address"),
                    "the failure must name both values, was: " + failure.getMessage());
        }
    }

    @Nested
    @DisplayName("root schema")
    class RootSchema {

        private final Map<String, Object> nameStrategy = Map.of(CodecOptions.CODEC_TYPE_STRATEGY, "NAME");

        @Test
        @DisplayName("the canonical dotted key codec.rootSchema scopes NAME resolution")
        void dottedKey() throws IOException {
            String json = saveWithSimpleName();

            EObject loaded = load(json, nameStrategy,
                    Map.of(CodecOptions.CODEC_ROOT_SCHEMA, roundtripPackage.getNsURI()));

            assertPerson(loaded);
        }

        @Test
        @DisplayName("the literal key CODEC_ROOT_SCHEMA keeps working")
        void literalKey() throws IOException {
            String json = saveWithSimpleName();

            EObject loaded = load(json, nameStrategy,
                    Map.of(CodecResource.CODEC_ROOT_SCHEMA, roundtripPackage.getNsURI()));

            assertPerson(loaded);
        }

        @Test
        @DisplayName("both keys with the same value are redundant, not a conflict")
        void bothKeysAgree() throws IOException {
            String json = saveWithSimpleName();

            EObject loaded = load(json, nameStrategy, Map.of(
                    CodecOptions.CODEC_ROOT_SCHEMA, roundtripPackage.getNsURI(),
                    CodecResource.CODEC_ROOT_SCHEMA, roundtripPackage.getNsURI()));

            assertPerson(loaded);
        }

        @Test
        @DisplayName("both keys with contradictory values fail the load")
        void bothKeysConflict() throws IOException {
            String json = saveWithSimpleName();
            Map<String, Object> loadOptions = Map.of(
                    CodecOptions.CODEC_ROOT_SCHEMA, roundtripPackage.getNsURI(),
                    CodecResource.CODEC_ROOT_SCHEMA, "http://test.example.org/other/1.0");

            IOException failure = assertThrows(IOException.class,
                    () -> load(json, nameStrategy, loadOptions));

            assertTrue(failure.getMessage().contains(CodecOptions.CODEC_ROOT_SCHEMA)
                            && failure.getMessage().contains(CodecOptions.CODEC_ROOT_SCHEMA_LITERAL),
                    "the failure must name both keys, was: " + failure.getMessage());
            assertTrue(failure.getMessage().contains("http://test.example.org/other/1.0"),
                    "the failure must name the contradicting value, was: " + failure.getMessage());
        }

        private String saveWithSimpleName() throws IOException {
            CodecResource resource = createResource(nameStrategy);
            EObject person = roundtripPackage.getEFactoryInstance().create(personClass);
            person.eSet(personClass.getEStructuralFeature("name"), "Alice");
            person.eSet(personClass.getEStructuralFeature("age"), 30);
            resource.getContents().add(person);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, Map.of());
            String json = out.toString(StandardCharsets.UTF_8);
            assertTrue(json.contains("\"Person\""), "NAME strategy must write the simple name, was: " + json);
            return json;
        }
    }

    @Nested
    @DisplayName("key constants")
    class KeyConstants {

        /**
         * The literal spelling has a single declaration, {@code CodecOptions}; every constant
         * that used to spell it out now aliases that one, so the keys cannot drift apart.
         */
        @Test
        @DisplayName("every literal constant carries the one declaration")
        void literalConstantsAgree() {
            assertEquals(CodecOptions.CODEC_ROOT_TYPE_LITERAL, CodecResource.CODEC_ROOT_TYPE);
            assertEquals(CodecOptions.CODEC_ROOT_TYPE_LITERAL, CodecResourceHelper.CODEC_ROOT_TYPE);
            assertEquals(CodecOptions.CODEC_ROOT_TYPE_LITERAL, CodecEObjectDeserializer.CODEC_ROOT_TYPE);
            assertEquals(CodecOptions.CODEC_ROOT_SCHEMA_LITERAL, CodecResource.CODEC_ROOT_SCHEMA);
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private void assertPerson(EObject loaded) {
        assertNotNull(loaded, "the document must load");
        assertEquals(personClass, loaded.eClass());
        assertEquals("Alice", loaded.eGet(personClass.getEStructuralFeature("name")));
        assertEquals(30, loaded.eGet(personClass.getEStructuralFeature("age")));
    }

    private EObject load(String json, Map<String, Object> loadOptions) throws IOException {
        return load(json, Map.of(), loadOptions);
    }

    private EObject load(String json, Map<String, Object> configOptions, Map<String, Object> loadOptions)
            throws IOException {
        CodecResource resource = createResource(configOptions);
        Map<String, Object> effective = new HashMap<>(configOptions);
        effective.putAll(loadOptions);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), effective);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private CodecResource createResource(Map<String, Object> configOptions) {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .optionsProperties(configOptions)
                .build();
        return new CodecResource(URI.createURI("root-options.json"), metadataService, config, null);
    }
}
