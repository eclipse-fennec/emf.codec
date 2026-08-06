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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for feature visibility (ignore) tests.
 */
public abstract class AbstractVisibilityTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-visibility.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EAttribute nameAttr;
    private EAttribute ageAttr;
    private EAttribute scoreAttr;
    private EAttribute secretAttr;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractVisibilityTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        nameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttr = (EAttribute) EcoreHelper.getFeature(personClass, "age");
        scoreAttr = (EAttribute) EcoreHelper.getFeature(personClass, "score");
        secretAttr = (EAttribute) EcoreHelper.getFeature(personClass, "secret");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("ignoreWrite skips serialization of feature")
    void ignoreWriteSkipsSerialization() throws IOException {
        Map<String, Object> featureProps = Map.of("ignoreWrite", true);
        Map<String, Object> classProps = Map.of("secret", featureProps);
        ConfigurationResolver config = ConfigurationResolver.builder()
                .moduleProperties(Map.of("Person", classProps))
                .build();

        EObject person = createPerson("Alice", 30, 95.5, "top-secret");

        EObject loaded = roundTrip(person, personClass, config);

        assertNotNull(loaded);
        assertEquals("Alice", loaded.eGet(nameAttr));
        assertEquals(30, loaded.eGet(ageAttr));
        assertNull(loaded.eGet(secretAttr), "secret should not be serialized");
    }

    @Test
    @DisplayName("ignoreRead skips deserialization of feature")
    void ignoreReadSkipsDeserialization() throws IOException {
        Map<String, Object> featureProps = Map.of("ignoreRead", true);
        Map<String, Object> classProps = Map.of("secret", featureProps);
        ConfigurationResolver config = ConfigurationResolver.builder()
                .moduleProperties(Map.of("Person", classProps))
                .build();

        EObject person = createPerson("Bob", 25, 88.0, "classified");

        EObject loaded = roundTrip(person, personClass, config);

        assertNotNull(loaded);
        assertEquals("Bob", loaded.eGet(nameAttr));
        assertEquals(25, loaded.eGet(ageAttr));
        assertNull(loaded.eGet(secretAttr), "secret should not be deserialized");
    }

    @Test
    @DisplayName("ignore both directions skips feature entirely")
    void ignoreBothDirections() throws IOException {
        Map<String, Object> featureProps = Map.of("ignore", true);
        Map<String, Object> classProps = Map.of("secret", featureProps);
        ConfigurationResolver config = ConfigurationResolver.builder()
                .moduleProperties(Map.of("Person", classProps))
                .build();

        EObject person = createPerson("Charlie", 35, 72.0, "hidden");

        EObject loaded = roundTrip(person, personClass, config);

        assertNotNull(loaded);
        assertEquals("Charlie", loaded.eGet(nameAttr));
        assertEquals(35, loaded.eGet(ageAttr));
        assertNull(loaded.eGet(secretAttr), "secret should be ignored in both directions");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject createPerson(String name, int age, double score, String secret) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttr, name);
        person.eSet(ageAttr, age);
        person.eSet(scoreAttr, score);
        person.eSet(secretAttr, secret);
        return person;
    }

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://visibility." + getFileExtension()),
                metadataService, config,
                null, null, createFormatProvider());
    }

    private EObject roundTrip(EObject object, EClass rootEClass, ConfigurationResolver config) throws IOException {
        CodecResource saveResource = createResource(config);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        CodecResource loadResource = createResource(config);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), options);
        assertNoDiagnostics(loadResource);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    /**
     * Fails when a round trip reported problems (issue #131).
     * <p>
     * Deserialization catches, logs and continues, so a load succeeds even when a value was
     * dropped. The diagnostics are the only trace - a test that ignores them cannot tell a
     * clean round trip from a lossy one.
     * </p>
     */
    private static void assertNoDiagnostics(CodecResource resource) {
        assertTrue(resource.getErrors().isEmpty(),
                "round trip reported errors: " + resource.getErrors());
        assertTrue(resource.getWarnings().isEmpty(),
                "round trip reported warnings: " + resource.getWarnings());
    }
}
