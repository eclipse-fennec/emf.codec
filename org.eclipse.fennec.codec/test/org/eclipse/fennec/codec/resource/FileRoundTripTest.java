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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.jackson.JacksonFormatProvider;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tools.jackson.core.json.JsonFactory;

/**
 * Round trips through real files, loaded by a resource set that did not write them
 * (issue #124).
 * <p>
 * Almost every round-trip test in the suite hands the same in-memory objects to both sides,
 * which hides anything that depends on the document actually being written and read back: a
 * type URI that only resolves because the writer's package happened to be reachable, an id
 * that is derived rather than stored, a value that survives because nobody serialised it.
 * That is how #113 stayed invisible - the situation was never set up.
 * </p>
 * <p>
 * These tests write to disk and load into a <b>fresh</b> {@link ResourceSet}, so resolution
 * has nowhere to cheat. Every load also asserts clean diagnostics: a round trip that reports
 * problems is not a round trip (issue #131).
 * </p>
 */
@DisplayName("Round trips through real files")
class FileRoundTripTest {

    private static final String ADVANCED_ECORE = "test-advanced.ecore";
    private static final String ID_ECORE = "test-id.ecore";
    private static final String DESER_ECORE =
            "/org/eclipse/fennec/codec/deser/test-deserialization.ecore";

    @TempDir
    Path tempDir;

    private EcoreHelper ecoreHelper;
    private EPackage advancedPackage;
    private EPackage idPackage;
    private MetadataWhiteboard metadataService;

    private EClass zooClass;
    private EClass dogClass;
    private EClass catClass;
    private EAttribute zooNameAttribute;
    private EAttribute animalNameAttribute;
    private EAttribute breedAttribute;
    private EAttribute colorAttribute;
    private EReference animalsRef;
    private EReference featuredAnimalRef;

    private EClass idHolderClass;
    private EClass containedIdClass;
    private EClass multiIdClass;
    private EReference myIdRef;
    private EAttribute userGroupAttribute;
    private EAttribute userIdAttribute;
    private EAttribute firstNameAttribute;
    private EAttribute lastNameAttribute;
    private EAttribute sequenceAttribute;

    private EPackage deserPackage;
    private EClass deserPersonClass;
    private EAttribute tagsAttribute;
    private EAttribute metadataAttribute;
    private EAttribute deserNameAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();

        advancedPackage = ecoreHelper.loadEcore(ADVANCED_ECORE, FileRoundTripTest.class);
        idPackage = ecoreHelper.loadEcore(ID_ECORE, FileRoundTripTest.class);
        EPackage.Registry.INSTANCE.put(advancedPackage.getNsURI(), advancedPackage);
        EPackage.Registry.INSTANCE.put(idPackage.getNsURI(), idPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(advancedPackage);
        metadataService.registerPackage(idPackage);

        zooClass = EcoreHelper.getEClass(advancedPackage, "Zoo");
        dogClass = EcoreHelper.getEClass(advancedPackage, "Dog");
        catClass = EcoreHelper.getEClass(advancedPackage, "Cat");
        zooNameAttribute = (EAttribute) EcoreHelper.getFeature(zooClass, "name");
        animalNameAttribute = (EAttribute) EcoreHelper.getFeature(
                EcoreHelper.getEClass(advancedPackage, "Animal"), "name");
        breedAttribute = (EAttribute) EcoreHelper.getFeature(dogClass, "breed");
        colorAttribute = (EAttribute) EcoreHelper.getFeature(catClass, "color");
        animalsRef = (EReference) EcoreHelper.getFeature(zooClass, "animals");
        featuredAnimalRef = (EReference) EcoreHelper.getFeature(zooClass, "featuredAnimal");

        idHolderClass = EcoreHelper.getEClass(idPackage, "IdHolder");
        containedIdClass = EcoreHelper.getEClass(idPackage, "ContainedId");
        multiIdClass = EcoreHelper.getEClass(idPackage, "MultiId");
        myIdRef = (EReference) EcoreHelper.getFeature(idHolderClass, "myId");
        userGroupAttribute = (EAttribute) EcoreHelper.getFeature(containedIdClass, "userGroup");
        userIdAttribute = (EAttribute) EcoreHelper.getFeature(containedIdClass, "userId");
        firstNameAttribute = (EAttribute) EcoreHelper.getFeature(multiIdClass, "firstName");
        lastNameAttribute = (EAttribute) EcoreHelper.getFeature(multiIdClass, "lastName");
        sequenceAttribute = (EAttribute) EcoreHelper.getFeature(multiIdClass, "sequence");

        deserPackage = ecoreHelper.loadEcore(DESER_ECORE, FileRoundTripTest.class);
        EPackage.Registry.INSTANCE.put(deserPackage.getNsURI(), deserPackage);
        metadataService.registerPackage(deserPackage);
        deserPersonClass = EcoreHelper.getEClass(deserPackage, "Person");
        tagsAttribute = (EAttribute) EcoreHelper.getFeature(deserPersonClass, "tags");
        metadataAttribute = (EAttribute) EcoreHelper.getFeature(deserPersonClass, "metadata");
        deserNameAttribute = (EAttribute) EcoreHelper.getFeature(deserPersonClass, "name");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(advancedPackage.getNsURI());
        EPackage.Registry.INSTANCE.remove(idPackage.getNsURI());
        EPackage.Registry.INSTANCE.remove(deserPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("subtypes in a polymorphic containment survive the file")
    void polymorphicContainmentSurvivesTheFile() throws IOException {
        EObject zoo = advancedPackage.getEFactoryInstance().create(zooClass);
        zoo.eSet(zooNameAttribute, "City Zoo");
        @SuppressWarnings("unchecked")
        List<EObject> animals = (List<EObject>) zoo.eGet(animalsRef);
        animals.add(animal(dogClass, "Rex", breedAttribute, "Collie"));
        animals.add(animal(catClass, "Mimi", colorAttribute, "black"));

        EObject loaded = roundTrip(zoo, "zoo.json", zooClass);

        @SuppressWarnings("unchecked")
        List<EObject> loadedAnimals = (List<EObject>) loaded.eGet(animalsRef);
        assertEquals(2, loadedAnimals.size(), "both animals come back");
        assertEquals("Dog", loadedAnimals.get(0).eClass().getName(),
                "the concrete subtype has to come from the document, not from the hint");
        assertEquals("Cat", loadedAnimals.get(1).eClass().getName());
        assertEquals("Collie", loadedAnimals.get(0).eGet(breedAttribute),
                "and its own features with it");
        assertEquals("black", loadedAnimals.get(1).eGet(colorAttribute));
    }

    @Test
    @DisplayName("a same-document reference into a containment list points at the loaded object")
    void sameDocumentReferenceResolvesToTheContainedInstance() throws IOException {
        EObject zoo = advancedPackage.getEFactoryInstance().create(zooClass);
        zoo.eSet(zooNameAttribute, "City Zoo");
        EObject rex = animal(dogClass, "Rex", breedAttribute, "Collie");
        @SuppressWarnings("unchecked")
        List<EObject> animals = (List<EObject>) zoo.eGet(animalsRef);
        animals.add(rex);
        animals.add(animal(catClass, "Mimi", colorAttribute, "black"));
        zoo.eSet(featuredAnimalRef, rex);

        EObject loaded = roundTrip(zoo, "zoo-featured.json", zooClass);

        @SuppressWarnings("unchecked")
        List<EObject> loadedAnimals = (List<EObject>) loaded.eGet(animalsRef);
        EObject featured = (EObject) loaded.eGet(featuredAnimalRef);
        assertNotNull(featured, "the reference has to survive");
        assertEquals("Rex", featured.eGet(animalNameAttribute));
        assertTrue(featured == loadedAnimals.get(0),
                "and point at the very object in the list, not at a copy of it");
    }

    @Test
    @DisplayName("a reference-based id is rebuilt from the file, not carried over in memory")
    void referenceBasedIdSurvivesTheFile() throws IOException {
        EObject contained = idPackage.getEFactoryInstance().create(containedIdClass);
        contained.eSet(userGroupAttribute, "sales");
        contained.eSet(userIdAttribute, 42L);
        EObject holder = idPackage.getEFactoryInstance().create(idHolderClass);
        holder.eSet(myIdRef, contained);

        Path file = tempDir.resolve("id-holder.json");
        EObject loaded = roundTrip(holder, "id-holder.json", idHolderClass);

        assertTrue(Files.readString(file).contains("\"_id\":\"sales_42\""),
                "the id is on disk in its joined form: " + Files.readString(file));
        EObject loadedContained = (EObject) loaded.eGet(myIdRef);
        assertNotNull(loadedContained, "the components are the only source of the data");
        assertEquals("sales", loadedContained.eGet(userGroupAttribute));
        assertEquals(42L, loadedContained.eGet(userIdAttribute));
    }

    @Test
    @DisplayName("a multi-part id round-trips through the file under its feature names")
    void multiPartIdSurvivesTheFile() throws IOException {
        EObject multiId = idPackage.getEFactoryInstance().create(multiIdClass);
        multiId.eSet(firstNameAttribute, "John");
        multiId.eSet(lastNameAttribute, "Doe");
        multiId.eSet(sequenceAttribute, 1);

        EObject loaded = roundTrip(multiId, "multi-id.json", multiIdClass);

        assertEquals("John", loaded.eGet(firstNameAttribute));
        assertEquals("Doe", loaded.eGet(lastNameAttribute));
        assertEquals(1, loaded.eGet(sequenceAttribute));
    }

    @Test
    @DisplayName("the loading resource set really is a different one")
    void loadingSetIsNotTheWritingSet() throws IOException {
        EObject zoo = advancedPackage.getEFactoryInstance().create(zooClass);
        zoo.eSet(zooNameAttribute, "City Zoo");

        ResourceSet writing = newResourceSet();
        Resource written = writing.createResource(fileUri("separate-sets.json"));
        written.getContents().add(zoo);
        written.save(Collections.emptyMap());

        ResourceSet reading = newResourceSet();
        Resource read = reading.createResource(fileUri("separate-sets.json"));
        read.load(Map.of(CodecResource.CODEC_ROOT_TYPE, zooClass));

        assertNotSame(writing, reading, "the point of the exercise");
        assertNotSame(zoo, read.getContents().get(0),
                "a fresh set cannot hand back the object that was written");
        assertEquals("City Zoo", read.getContents().get(0).eGet(zooNameAttribute));
        assertNoDiagnostics(read);
    }

    @Test
    @DisplayName("subtypes survive the file on the format-delegate path too")
    void polymorphicContainmentSurvivesTheFileViaDelegate() throws IOException {
        // The delegate path writes through a different generator, so the type it puts on disk
        // is worth checking separately - issue #124 named it as covered for references only
        EObject zoo = advancedPackage.getEFactoryInstance().create(zooClass);
        zoo.eSet(zooNameAttribute, "City Zoo");
        @SuppressWarnings("unchecked")
        List<EObject> animals = (List<EObject>) zoo.eGet(animalsRef);
        animals.add(animal(dogClass, "Rex", breedAttribute, "Collie"));
        animals.add(animal(catClass, "Mimi", colorAttribute, "black"));

        EObject loaded = roundTrip(zoo, "zoo-delegate.json", zooClass, true);

        @SuppressWarnings("unchecked")
        List<EObject> loadedAnimals = (List<EObject>) loaded.eGet(animalsRef);
        assertEquals(2, loadedAnimals.size());
        assertEquals("Dog", loadedAnimals.get(0).eClass().getName());
        assertEquals("Cat", loadedAnimals.get(1).eClass().getName());
        assertEquals("Collie", loadedAnimals.get(0).eGet(breedAttribute));
    }

    @Test
    @DisplayName("a same-document reference resolves on the format-delegate path too")
    void sameDocumentReferenceResolvesViaDelegate() throws IOException {
        EObject zoo = advancedPackage.getEFactoryInstance().create(zooClass);
        zoo.eSet(zooNameAttribute, "City Zoo");
        EObject rex = animal(dogClass, "Rex", breedAttribute, "Collie");
        @SuppressWarnings("unchecked")
        List<EObject> animals = (List<EObject>) zoo.eGet(animalsRef);
        animals.add(rex);
        animals.add(animal(catClass, "Mimi", colorAttribute, "black"));
        zoo.eSet(featuredAnimalRef, rex);

        EObject loaded = roundTrip(zoo, "zoo-featured-delegate.json", zooClass, true);

        @SuppressWarnings("unchecked")
        List<EObject> loadedAnimals = (List<EObject>) loaded.eGet(animalsRef);
        assertTrue((EObject) loaded.eGet(featuredAnimalRef) == loadedAnimals.get(0),
                "the reference has to point at the object in the list, not at a copy");
    }

    @Test
    @DisplayName("a reference-based id survives the file on the format-delegate path too")
    void referenceBasedIdSurvivesTheFileViaDelegate() throws IOException {
        EObject contained = idPackage.getEFactoryInstance().create(containedIdClass);
        contained.eSet(userGroupAttribute, "sales");
        contained.eSet(userIdAttribute, 42L);
        EObject holder = idPackage.getEFactoryInstance().create(idHolderClass);
        holder.eSet(myIdRef, contained);

        EObject loaded = roundTrip(holder, "id-holder-delegate.json", idHolderClass, true);

        EObject loadedContained = (EObject) loaded.eGet(myIdRef);
        assertNotNull(loadedContained);
        assertEquals("sales", loadedContained.eGet(userGroupAttribute));
        assertEquals(42L, loadedContained.eGet(userIdAttribute));
    }

    @Test
    @DisplayName("a multi-valued attribute keeps its order through the file")
    void multiValuedAttributeKeepsOrder() throws IOException {
        EObject person = deserPackage.getEFactoryInstance().create(deserPersonClass);
        person.eSet(deserNameAttribute, "Alice");
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) person.eGet(tagsAttribute);
        tags.addAll(List.of("beta", "alpha", "gamma"));

        EObject loaded = roundTrip(person, "tags.json", deserPersonClass);

        assertEquals(List.of("beta", "alpha", "gamma"), loaded.eGet(tagsAttribute),
                "insertion order is data, not a detail");
    }

    @Test
    @DisplayName("an EJavaObject map survives the file as a map")
    void javaObjectMapSurvivesTheFile() throws IOException {
        // Issue #115: this used to go out as toString() and come back a String, with the
        // round trip in memory none the wiser
        EObject person = deserPackage.getEFactoryInstance().create(deserPersonClass);
        person.eSet(deserNameAttribute, "Alice");
        person.eSet(metadataAttribute, Map.of("role", "admin"));

        EObject loaded = roundTrip(person, "metadata-map.json", deserPersonClass);

        Object metadata = loaded.eGet(metadataAttribute);
        assertInstanceOf(Map.class, metadata, "a map has to come back a map, was: " + metadata);
        assertEquals("admin", ((Map<?, ?>) metadata).get("role"));
    }

    @Test
    @DisplayName("an EJavaObject list survives the file as a list")
    void javaObjectListSurvivesTheFile() throws IOException {
        EObject person = deserPackage.getEFactoryInstance().create(deserPersonClass);
        person.eSet(deserNameAttribute, "Alice");
        person.eSet(metadataAttribute, List.of("one", "two"));

        EObject loaded = roundTrip(person, "metadata-list.json", deserPersonClass);

        Object metadata = loaded.eGet(metadataAttribute);
        assertInstanceOf(List.class, metadata, "a list has to come back a list, was: " + metadata);
        assertEquals(List.of("one", "two"), metadata);
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject animal(EClass type, String name, EAttribute ownFeature, Object ownValue) {
        EObject animal = advancedPackage.getEFactoryInstance().create(type);
        animal.eSet(animalNameAttribute, name);
        animal.eSet(ownFeature, ownValue);
        return animal;
    }

    /** Writes to a real file, then loads it in a resource set that has never seen it. */
    private EObject roundTrip(EObject root, String fileName, EClass rootType) throws IOException {
        return roundTrip(root, fileName, rootType, false);
    }

    /** Writes to a real file, then loads it in a resource set that has never seen it. */
    private EObject roundTrip(EObject root, String fileName, EClass rootType,
            boolean withFormatProvider) throws IOException {
        Resource written = newResourceSet(withFormatProvider).createResource(fileUri(fileName));
        written.getContents().add(root);
        written.save(Collections.emptyMap());

        Resource read = newResourceSet(withFormatProvider).createResource(fileUri(fileName));
        read.load(Map.of(CodecResource.CODEC_ROOT_TYPE, rootType));

        assertNoDiagnostics(read);
        assertTrue(!read.getContents().isEmpty(), "the file has to yield a root object");
        return read.getContents().get(0);
    }

    private void assertNoDiagnostics(Resource resource) {
        assertTrue(resource.getErrors().isEmpty() && resource.getWarnings().isEmpty(),
                "a round trip that reports problems is not one: errors=" + resource.getErrors()
                        + " warnings=" + resource.getWarnings());
    }

    private ResourceSet newResourceSet() {
        return newResourceSet(false);
    }

    private ResourceSet newResourceSet(boolean withFormatProvider) {
        ResourceSet rs = new ResourceSetImpl();
        Resource.Factory factory = withFormatProvider
                ? new CodecFormatResourceFactory(metadataService,
                        new JacksonFormatProvider("json", new JsonFactory()),
                        ConfigurationResolver.defaults())
                : new CodecResourceFactory(metadataService, ConfigurationResolver.defaults());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("json", factory);
        return rs;
    }

    private URI fileUri(String fileName) {
        return URI.createFileURI(tempDir.resolve(fileName).toAbsolutePath().toString());
    }
}
