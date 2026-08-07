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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@code idKeyMode} and {@code idValueKey} in STRUCTURED format (issue #119).
 * <p>
 * The two settings answer different questions, which spec 09-id.md conflated:
 * </p>
 * <ul>
 *   <li>{@code idKeyMode} — <b>where</b> the identity lives: in the id section only
 *       ({@code ID_ONLY}), in the body as well ({@code BOTH}), or in the body alone
 *       ({@code FEATURE_ONLY}). Same meaning in PLAIN and STRUCTURED.</li>
 *   <li>{@code idValueKey} — <b>what</b> the {@code _id} object carries: the feature keys
 *       always, plus the combined value under this key when one is configured.</li>
 * </ul>
 */
@DisplayName("STRUCTURED id key mode")
class StructuredIdKeyModeTest {

    private static final String TEST_ECORE = "test-id.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass multiIdClass;
    private EClass personClass;
    private EAttribute personIdAttribute;
    private EAttribute firstNameAttribute;
    private EAttribute lastNameAttribute;
    private EAttribute sequenceAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, StructuredIdKeyModeTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        multiIdClass = EcoreHelper.getEClass(testPackage, "MultiId");
        personClass = EcoreHelper.getEClass(testPackage, "Person");
        personIdAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "personId");
        firstNameAttribute = (EAttribute) EcoreHelper.getFeature(multiIdClass, "firstName");
        lastNameAttribute = (EAttribute) EcoreHelper.getFeature(multiIdClass, "lastName");
        sequenceAttribute = (EAttribute) EcoreHelper.getFeature(multiIdClass, "sequence");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("ID_ONLY keeps the id features inside _id only")
    void idOnlyKeepsFeaturesInsideId() throws IOException {
        String json = serialize(multiId(), resolver("ID_ONLY", null));

        assertTrue(json.contains("\"_id\":{"), "an _id object is written, was: " + json);
        assertTrue(idSection(json).contains("\"firstName\":\"John\""),
                "the id features live inside _id, was: " + json);
        assertFalse(body(json).contains("\"firstName\""),
                "and must not be duplicated in the body, was: " + json);
    }

    @Test
    @DisplayName("BOTH repeats the id features in the body")
    void bothRepeatsFeaturesInBody() throws IOException {
        String json = serialize(multiId(), resolver("BOTH", null));

        assertTrue(json.contains("\"_id\":{"), "an _id object is written, was: " + json);
        assertTrue(body(json).contains("\"firstName\""),
                "BOTH keeps the features in the body as well, was: " + json);
    }

    @Test
    @DisplayName("FEATURE_ONLY writes no _id at all")
    void featureOnlyWritesNoIdSection() throws IOException {
        String json = serialize(multiId(), resolver("FEATURE_ONLY", null));

        assertFalse(json.contains("\"_id\""), "no id section for FEATURE_ONLY, was: " + json);
        assertTrue(json.contains("\"firstName\":\"John\""),
                "the features stay in the body, was: " + json);
    }

    @Test
    @DisplayName("a single id value uses the inner key, not the feature name")
    void singleIdUsesInnerKey() throws IOException {
        String json = serialize(person(), personResolver(null, null));

        assertTrue(json.contains("\"_id\":{\"id\":\"maho\"}"),
                "default inner key is 'id', was: " + json);
    }

    @Test
    @DisplayName("idValueKey renames the inner key")
    void idValueKeyRenamesInnerKey() throws IOException {
        String json = serialize(person(), personResolver(null, "xyz"));

        assertTrue(json.contains("\"_id\":{\"xyz\":\"maho\"}"),
                "idValueKey=xyz must yield {\"_id\":{\"xyz\":...}}, was: " + json);
    }

    @Test
    @DisplayName("idKey and idValueKey rename both levels")
    void idKeyAndValueKeyRenameBothLevels() throws IOException {
        String json = serialize(person(), personResolver("abc", "xyz"));

        assertTrue(json.contains("\"abc\":{\"xyz\":\"maho\"}"),
                "idKey=abc with idValueKey=xyz must yield {\"abc\":{\"xyz\":...}}, was: " + json);
    }

    @Test
    @DisplayName("a renamed inner key round-trips")
    void renamedInnerKeyRoundTrips() throws IOException {
        ConfigurationResolver config = personResolver("abc", "xyz");
        String json = serialize(person(), config);

        EObject loaded = deserializePerson(json, config);
        assertEquals("maho", loaded.eGet(personIdAttribute));
    }

    @Test
    @DisplayName("BOTH with a renamed inner key: container and body use different names")
    void bothWithRenamedInnerKey() throws IOException {
        // The two levels are named independently: idValueKey inside the container, the
        // feature name in the body
        ConfigurationResolver config = personResolver(null, "xyz", "BOTH");
        String json = serialize(person(), config);

        assertTrue(idSection(json).contains("\"xyz\":\"maho\""),
                "the container uses the inner key, was: " + json);
        assertTrue(body(json).contains("\"personId\":\"maho\""),
                "the body uses the feature name, was: " + json);

        EObject loaded = deserializePerson(json, config);
        assertEquals("maho", loaded.eGet(personIdAttribute),
                "both spellings describe the same identity and must load");
    }

    @Test
    @DisplayName("multiple id components round-trip under their feature names")
    void idOnlyRoundTrips() throws IOException {
        ConfigurationResolver config = resolver("ID_ONLY", null);
        String json = serialize(multiId(), config);

        EObject loaded = deserialize(json, config);
        assertEquals("John", loaded.eGet(firstNameAttribute));
        assertEquals("Doe", loaded.eGet(lastNameAttribute));
        assertEquals(1, loaded.eGet(sequenceAttribute));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    /** The substring covering the _id object, so body and id section can be told apart. */
    private String idSection(String json) {
        int start = json.indexOf("\"_id\":{");
        if (start < 0) {
            return "";
        }
        int depth = 0;
        for (int i = json.indexOf('{', start); i < json.length(); i++) {
            if (json.charAt(i) == '{') {
                depth++;
            } else if (json.charAt(i) == '}') {
                depth--;
                if (depth == 0) {
                    return json.substring(start, i + 1);
                }
            }
        }
        return "";
    }

    /** Everything outside the _id object. */
    private String body(String json) {
        String section = idSection(json);
        return section.isEmpty() ? json : json.replace(section, "");
    }

    private EObject person() {
        EObject obj = testPackage.getEFactoryInstance().create(personClass);
        obj.eSet(personIdAttribute, "maho");
        return obj;
    }

    private ConfigurationResolver personResolver(String idKey, String valueKey) {
        return personResolver(idKey, valueKey, null);
    }

    private ConfigurationResolver personResolver(String idKey, String valueKey, String keyMode) {
        Map<String, Object> props = new HashMap<>(Map.of("idFormat", "STRUCTURED"));
        if (keyMode != null) {
            props.put("idKeyMode", keyMode);
        }
        if (idKey != null) {
            props.put("idKey", idKey);
        }
        if (valueKey != null) {
            props.put("idValueKey", valueKey);
        }
        return ConfigurationResolver.builder().resourceProperties(props).build();
    }

    private EObject deserializePerson(String json, ConfigurationResolver resolver)
            throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://structured-id.json"),
                metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().get(0);
    }

    private EObject multiId() {
        EObject obj = testPackage.getEFactoryInstance().create(multiIdClass);
        obj.eSet(firstNameAttribute, "John");
        obj.eSet(lastNameAttribute, "Doe");
        obj.eSet(sequenceAttribute, 1);
        return obj;
    }

    private ConfigurationResolver resolver(String keyMode, String valueKey) {
        Map<String, Object> props = new HashMap<>(Map.of(
                "idFormat", "STRUCTURED",
                "idFeatures", List.of("firstName", "lastName", "sequence"),
                "idSeparator", "-",
                "idKeyMode", keyMode));
        if (valueKey != null) {
            props.put("idValueKey", valueKey);
        }
        return ConfigurationResolver.builder().resourceProperties(props).build();
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://structured-id.json"),
                metadataService, resolver, null);
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://structured-id.json"),
                metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, multiIdClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().get(0);
    }
}
