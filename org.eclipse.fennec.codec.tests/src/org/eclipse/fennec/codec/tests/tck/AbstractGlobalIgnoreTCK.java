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
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for global ignore feature tests.
 * <p>
 * Uses the visibility ecore model (Person class).
 */
public abstract class AbstractGlobalIgnoreTCK {

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
        ecoreHelper = new EcoreHelper(AbstractGlobalIgnoreTCK.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = ecoreHelper.getEClass(testPackage, "Person");
        nameAttr = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        ageAttr = (EAttribute) ecoreHelper.getFeature(personClass, "age");
        scoreAttr = (EAttribute) ecoreHelper.getFeature(personClass, "score");
        secretAttr = (EAttribute) ecoreHelper.getFeature(personClass, "secret");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("global ignore skips single feature")
    void globalIgnoreSkipsSerialization() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .globalIgnoreFeatures("secret")
                .build();

        EObject person = createPerson("Alice", 30, 95.5, "top-secret");

        EObject loaded = roundTrip(person, personClass, config);

        assertNotNull(loaded);
        assertEquals("Alice", loaded.eGet(nameAttr));
        assertEquals(30, loaded.eGet(ageAttr));
        assertEquals(95.5, (Double) loaded.eGet(scoreAttr), 0.001);
        assertNull(loaded.eGet(secretAttr), "secret should be globally ignored");
    }

    @Test
    @DisplayName("global ignore skips multiple features")
    void globalIgnoreMultipleFeatures() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .globalIgnoreFeatures("secret", "score")
                .build();

        EObject person = createPerson("Bob", 25, 88.0, "classified");

        EObject loaded = roundTrip(person, personClass, config);

        assertNotNull(loaded);
        assertEquals("Bob", loaded.eGet(nameAttr));
        assertEquals(25, loaded.eGet(ageAttr));
        assertEquals(0.0, (Double) loaded.eGet(scoreAttr), 0.001, "score should be default (0.0)");
        assertNull(loaded.eGet(secretAttr), "secret should be globally ignored");
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
                URI.createURI("test://globalignore." + getFileExtension()),
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

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }
}
