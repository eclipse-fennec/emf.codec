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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * An EMap whose key feature is not an EString (issue #154).
 * <p>
 * The field name of a map entry is always a string, so reading it back means converting it
 * through the key feature's data type. Assigning the raw name instead throws, and because the
 * failure used to be caught around the whole loop, the caller saw a successfully loaded object
 * with an empty map - a plausible-looking wrong answer.
 * </p>
 */
@DisplayName("EMap with a non-String key")
class EMapNonStringKeyTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-emap-keys.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass containerClass;
    private EEnum levelEnum;
    private EReference countsRef;
    private EReference levelsRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, EMapNonStringKeyTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        containerClass = EcoreHelper.getEClass(testPackage, "Container");
        levelEnum = (EEnum) testPackage.getEClassifier("Level");
        countsRef = (EReference) EcoreHelper.getFeature(containerClass, "counts");
        levelsRef = (EReference) EcoreHelper.getFeature(containerClass, "levels");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Round trip
    // ========================================================================

    @Test
    @DisplayName("an EInt key survives the round trip")
    void intKeyRoundTrip() throws IOException {
        EObject original = createContainer("Counts");

        @SuppressWarnings("unchecked")
        EMap<Integer, String> counts = (EMap<Integer, String>) original.eGet(countsRef);
        counts.put(1, "one");
        counts.put(2, "two");

        String json = serialize(original);
        assertTrue(json.contains("\"1\""), "the key is written as the field name: " + json);

        EObject restored = deserialize(json);
        assertNotNull(restored);

        @SuppressWarnings("unchecked")
        EMap<Integer, String> restoredCounts = (EMap<Integer, String>) restored.eGet(countsRef);

        assertEquals(2, restoredCounts.size(), "both entries come back");
        assertEquals("one", restoredCounts.get(Integer.valueOf(1)));
        assertEquals("two", restoredCounts.get(Integer.valueOf(2)));
    }

    @Test
    @DisplayName("an enum key survives the round trip")
    void enumKeyRoundTrip() throws IOException {
        EObject original = createContainer("Levels");

        @SuppressWarnings("unchecked")
        EMap<Object, String> levels = (EMap<Object, String>) original.eGet(levelsRef);
        levels.put(levelEnum.getEEnumLiteral("LOW").getInstance(), "quiet");
        levels.put(levelEnum.getEEnumLiteral("HIGH").getInstance(), "loud");

        String json = serialize(original);
        assertTrue(json.contains("\"LOW\""), "the literal is the field name: " + json);

        EObject restored = deserialize(json);
        assertNotNull(restored);

        @SuppressWarnings("unchecked")
        EMap<Object, String> restoredLevels = (EMap<Object, String>) restored.eGet(levelsRef);

        assertEquals(2, restoredLevels.size(), "both entries come back");
        assertEquals("quiet", restoredLevels.get(levelEnum.getEEnumLiteral("LOW").getInstance()));
        assertEquals("loud", restoredLevels.get(levelEnum.getEEnumLiteral("HIGH").getInstance()));
    }

    @Test
    @DisplayName("reading a document written elsewhere converts the key")
    void intKeyFromForeignDocument() throws IOException {
        String json = """
                {
                  "_type": "%s#//Container",
                  "name": "Counts",
                  "counts": { "1": "one", "2": "two" }
                }
                """.formatted(testPackage.getNsURI());

        @SuppressWarnings("unchecked")
        EMap<Integer, String> counts = (EMap<Integer, String>) deserialize(json).eGet(countsRef);

        assertEquals(2, counts.size());
        assertEquals("one", counts.get(Integer.valueOf(1)));
    }

    // ========================================================================
    // A key the data type cannot parse
    // ========================================================================

    /** A key that is no int at all, between two that are. */
    private String documentWithBrokenKey() {
        return """
                {
                  "_type": "%s#//Container",
                  "name": "Counts",
                  "counts": { "1": "one", "not-a-number": "two", "3": "three" }
                }
                """.formatted(testPackage.getNsURI());
    }

    @Test
    @DisplayName("a key the data type cannot parse costs its own entry, not the whole map")
    void brokenKeyDropsOnlyItsEntry() throws IOException {
        CodecResource resource = load(documentWithBrokenKey(), ConfigurationResolver.defaults());

        EObject container = resource.getContents().get(0);

        @SuppressWarnings("unchecked")
        EMap<Integer, String> counts = (EMap<Integer, String>) container.eGet(countsRef);

        assertEquals(2, counts.size(), "the entries around the broken one survive");
        assertEquals("one", counts.get(Integer.valueOf(1)));
        assertEquals("three", counts.get(Integer.valueOf(3)));
        assertFalse(resource.getWarnings().isEmpty(), "the dropped entry leaves a diagnostic");
    }

    @Test
    @DisplayName("strictOnConversion turns an unparseable key into a failure")
    void strictFailsOnBrokenKey() {
        ConfigurationResolver strict = ConfigurationResolver.builder()
                .resourceProperties(Map.of("strictOnConversion", true))
                .build();

        assertThrows(Exception.class, () -> load(documentWithBrokenKey(), strict),
                "a caller can demand a failure instead of a quietly shortened map");
    }

    @Test
    @DisplayName("an enum literal the model does not know is reported the same way")
    void unknownEnumLiteralIsReported() throws IOException {
        String json = """
                {
                  "_type": "%s#//Container",
                  "name": "Levels",
                  "levels": { "LOW": "quiet", "NOPE": "?" }
                }
                """.formatted(testPackage.getNsURI());

        CodecResource resource = load(json, ConfigurationResolver.defaults());

        @SuppressWarnings("unchecked")
        EMap<Object, String> levels = (EMap<Object, String>) resource.getContents().get(0).eGet(levelsRef);

        assertEquals(1, levels.size(), "only the known literal makes it into the map");
        assertEquals("quiet", levels.get(levelEnum.getEEnumLiteral("LOW").getInstance()));
        assertFalse(resource.getWarnings().isEmpty(), "the dropped entry leaves a diagnostic");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject createContainer(String name) {
        EObject container = testPackage.getEFactoryInstance().create(containerClass);
        container.eSet(containerClass.getEStructuralFeature("name"), name);
        return container;
    }

    private CodecResource createResource(ConfigurationResolver resolver) {
        return new CodecResource(URI.createURI("test://emap-keys.json"), metadataService, resolver, null);
    }

    private String serialize(EObject eObject) throws IOException {
        CodecResource resource = createResource(ConfigurationResolver.defaults());
        resource.getContents().add(eObject);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, new HashMap<>());
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json) throws IOException {
        return load(json, ConfigurationResolver.defaults()).getContents().get(0);
    }

    private CodecResource load(String json, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = createResource(resolver);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, containerClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource;
    }
}
