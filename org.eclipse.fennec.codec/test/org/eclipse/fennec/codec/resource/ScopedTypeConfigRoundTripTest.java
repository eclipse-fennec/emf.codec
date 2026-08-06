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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Round trips for class- and reference-scoped type configuration (issue #116).
 * <p>
 * The write side resolves the type config with reference context; the read side only knows
 * the global one. A scoped override therefore produces a document the reader cannot
 * interpret. The reference type here is <b>abstract</b> ({@code Animal}), so there is no
 * fallback that could mask the problem — the concrete subtype has to come from the document.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-type.md">Spec: Type Serialization</a>
 */
@DisplayName("Scoped type config round trips")
class ScopedTypeConfigRoundTripTest {

    private static final String TEST_ECORE = "test-advanced.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass zooClass;
    private EClass dogClass;
    private EAttribute zooNameAttribute;
    private EAttribute breedAttribute;
    private EReference animalsRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, ScopedTypeConfigRoundTripTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        zooClass = EcoreHelper.getEClass(testPackage, "Zoo");
        dogClass = EcoreHelper.getEClass(testPackage, "Dog");
        zooNameAttribute = (EAttribute) EcoreHelper.getFeature(zooClass, "name");
        breedAttribute = (EAttribute) EcoreHelper.getFeature(dogClass, "breed");
        animalsRef = (EReference) EcoreHelper.getFeature(zooClass, "animals");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("a reference-scoped typeKey is understood on read")
    void referenceScopedTypeKeyRoundTrips() throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put(CodecOptions.CODEC_EREFERENCE_CONFIG,
                Map.of(animalsRef, Map.of(CodecOptions.CODEC_TYPE_KEY, "kind")));

        String json = serialize(zooWithDog(), options);
        assertTrue(json.contains("\"kind\""),
                "the reference-scoped type key must be written, was: " + json);

        EObject loaded = deserialize(json, options);
        assertConcreteDogRestored(loaded, json);
    }

    @Test
    @DisplayName("a class-scoped typeKey is understood on read")
    void classScopedTypeKeyRoundTrips() throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put(CodecOptions.CODEC_ECLASS_CONFIG,
                Map.of(dogClass, Map.of(CodecOptions.CODEC_TYPE_KEY, "kind")));

        String json = serialize(zooWithDog(), options);
        assertTrue(json.contains("\"kind\""),
                "the class-scoped type key must be written, was: " + json);

        EObject loaded = deserialize(json, options);
        assertConcreteDogRestored(loaded, json);
    }

    @Test
    @DisplayName("a class-scoped NUMERIC strategy is understood on read")
    void classScopedNumericStrategyRoundTrips() throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put(CodecOptions.CODEC_TYPE_STRATEGY, "URI");
        options.put(CodecOptions.CODEC_ECLASS_CONFIG,
                Map.of(dogClass, Map.of(CodecOptions.CODEC_TYPE_STRATEGY, "NUMERIC")));

        String json = serialize(zooWithDog(), options);
        EObject loaded = deserialize(json, options);
        assertConcreteDogRestored(loaded, json);
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    @SuppressWarnings("unchecked")
    private void assertConcreteDogRestored(EObject loaded, String json) {
        assertNotNull(loaded, "the document must load, json was: " + json);
        List<EObject> animals = (List<EObject>) loaded.eGet(animalsRef);
        assertEquals(1, animals.size(), "the animal must survive, json was: " + json);

        EObject animal = animals.get(0);
        assertFalse(animal.eClass().isAbstract(), "an abstract type cannot be the result");
        assertEquals(dogClass, animal.eClass(),
                "the concrete subtype must be restored from the document, json was: " + json);
        assertEquals("Beagle", animal.eGet(breedAttribute));
    }

    @SuppressWarnings("unchecked")
    private EObject zooWithDog() {
        EObject zoo = testPackage.getEFactoryInstance().create(zooClass);
        zoo.eSet(zooNameAttribute, "City Zoo");

        EObject dog = testPackage.getEFactoryInstance().create(dogClass);
        dog.eSet(breedAttribute, "Beagle");
        ((List<EObject>) zoo.eGet(animalsRef)).add(dog);
        return zoo;
    }

    private CodecResource createResource(Map<String, Object> configOptions) {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(configOptions)
                .build();
        return new CodecResource(URI.createURI("test://scoped-type.json"),
                metadataService, resolver, null);
    }

    private String serialize(EObject object, Map<String, Object> configOptions) throws IOException {
        CodecResource resource = createResource(configOptions);
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, Map<String, Object> configOptions) throws IOException {
        CodecResource resource = createResource(configOptions);
        Map<String, Object> loadOptions = new HashMap<>();
        loadOptions.put(CodecResource.CODEC_ROOT_TYPE, zooClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), loadOptions);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
