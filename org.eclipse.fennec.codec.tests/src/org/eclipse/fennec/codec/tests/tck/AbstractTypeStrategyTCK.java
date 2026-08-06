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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for type strategy tests.
 * <p>
 * Tests NAME and URI type strategies with polymorphic containment.
 */
public abstract class AbstractTypeStrategyTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass zooClass;
    private EClass dogClass;
    private EClass catClass;
    private EAttribute zooNameAttr;
    private EAttribute animalNameAttr;
    private EAttribute animalAgeAttr;
    private EAttribute breedAttr;
    private EAttribute indoorAttr;
    private EReference animalsRef;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractTypeStrategyTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        zooClass = EcoreHelper.getEClass(testPackage, "Zoo");
        dogClass = EcoreHelper.getEClass(testPackage, "Dog");
        catClass = EcoreHelper.getEClass(testPackage, "Cat");
        zooNameAttr = (EAttribute) EcoreHelper.getFeature(zooClass, "name");
        animalNameAttr = (EAttribute) EcoreHelper.getFeature(EcoreHelper.getEClass(testPackage, "Animal"), "name");
        animalAgeAttr = (EAttribute) EcoreHelper.getFeature(EcoreHelper.getEClass(testPackage, "Animal"), "age");
        breedAttr = (EAttribute) EcoreHelper.getFeature(dogClass, "breed");
        indoorAttr = (EAttribute) EcoreHelper.getFeature(catClass, "indoor");
        animalsRef = (EReference) EcoreHelper.getFeature(zooClass, "animals");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("TypeStrategy NAME preserves concrete types")
    @SuppressWarnings("unchecked")
    void typeStrategyName() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .typeStrategy(TypeStrategy.NAME)
                .build();

        EObject zoo = createZooWithAnimals();
        EObject loaded = roundTrip(zoo, zooClass, config);

        assertNotNull(loaded);
        List<EObject> animals = (List<EObject>) loaded.eGet(animalsRef);
        assertEquals(2, animals.size());

        EObject loadedDog = animals.get(0);
        assertTrue(dogClass.isInstance(loadedDog), "First animal should be Dog");
        assertEquals("Rex", loadedDog.eGet(animalNameAttr));
        assertEquals("Labrador", loadedDog.eGet(breedAttr));

        EObject loadedCat = animals.get(1);
        assertTrue(catClass.isInstance(loadedCat), "Second animal should be Cat");
        assertEquals("Whiskers", loadedCat.eGet(animalNameAttr));
        assertEquals(true, loadedCat.eGet(indoorAttr));
    }

    @Test
    @DisplayName("TypeStrategy URI preserves concrete types")
    @SuppressWarnings("unchecked")
    void typeStrategyUri() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .typeStrategy(TypeStrategy.URI)
                .build();

        EObject zoo = createZooWithAnimals();
        EObject loaded = roundTrip(zoo, zooClass, config);

        assertNotNull(loaded);
        List<EObject> animals = (List<EObject>) loaded.eGet(animalsRef);
        assertEquals(2, animals.size());

        EObject loadedDog = animals.get(0);
        assertTrue(dogClass.isInstance(loadedDog), "First animal should be Dog");
        assertEquals("Rex", loadedDog.eGet(animalNameAttr));
        assertEquals("Labrador", loadedDog.eGet(breedAttr));

        EObject loadedCat = animals.get(1);
        assertTrue(catClass.isInstance(loadedCat), "Second animal should be Cat");
        assertEquals("Whiskers", loadedCat.eGet(animalNameAttr));
        assertEquals(true, loadedCat.eGet(indoorAttr));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    @SuppressWarnings("unchecked")
    private EObject createZooWithAnimals() {
        EObject zoo = testPackage.getEFactoryInstance().create(zooClass);
        zoo.eSet(zooNameAttr, "Test Zoo");

        EObject dog = testPackage.getEFactoryInstance().create(dogClass);
        dog.eSet(animalNameAttr, "Rex");
        dog.eSet(animalAgeAttr, 5);
        dog.eSet(breedAttr, "Labrador");

        EObject cat = testPackage.getEFactoryInstance().create(catClass);
        cat.eSet(animalNameAttr, "Whiskers");
        cat.eSet(animalAgeAttr, 3);
        cat.eSet(indoorAttr, true);

        List<EObject> animals = (List<EObject>) zoo.eGet(animalsRef);
        animals.add(dog);
        animals.add(cat);

        return zoo;
    }

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://typestrategy." + getFileExtension()),
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
