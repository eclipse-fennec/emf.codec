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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Advanced integration tests for CodecResource.
 * <p>
 * Tests advanced scenarios:
 * <ul>
 *   <li>Polymorphic lists (inheritance hierarchies)</li>
 *   <li>Bidirectional references</li>
 *   <li>Circular references</li>
 *   <li>Null/default value handling</li>
 *   <li>Error scenarios</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/11-polymorphism.md">Spec: Polymorphism and Inheritance</a>
 * @see <a href="docs/codec-v2-spec/08-reference.md">Spec: Reference Serialization</a>
 * @see <a href="docs/codec-v2-spec/09-feature.md">Spec: Feature Serialization</a>
 * @see <a href="docs/codec-v2-spec/15-test-coverage.md#4-advanced-scenario-coverage">Spec: Test Coverage</a>
 */
@DisplayName("CodecResource Advanced Tests")
class CodecResourceAdvancedTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-advanced.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // Polymorphism classes
    private EClass animalClass;
    private EClass dogClass;
    private EClass catClass;
    private EClass birdClass;
    private EClass zooClass;

    // Bidirectional classes
    private EClass personClass;

    // Circular reference classes
    private EClass nodeClass;
    private EClass graphClass;

    // Null/default classes
    private EClass entityClass;

    // Attributes and references
    private EAttribute animalNameAttr;
    private EAttribute animalAgeAttr;
    private EAttribute dogBreedAttr;
    private EAttribute dogTrainedAttr;
    private EAttribute catIndoorAttr;
    private EAttribute catColorAttr;
    private EAttribute birdCanFlyAttr;
    private EAttribute birdWingspanAttr;
    private EAttribute zooNameAttr;
    private EReference zooAnimalsRef;
    private EReference zooFeaturedRef;

    private EAttribute personNameAttr;
    private EReference personParentRef;
    private EReference personChildrenRef;

    private EAttribute nodeIdAttr;
    private EAttribute nodeValueAttr;
    private EReference nodeNextRef;
    private EReference nodeConnectionsRef;
    private EAttribute graphNameAttr;
    private EReference graphNodesRef;

    private EAttribute entityRequiredNameAttr;
    private EAttribute entityOptionalDescAttr;
    private EAttribute entityDefaultedIntAttr;
    private EAttribute entityDefaultedBoolAttr;
    private EAttribute entityDefaultedStringAttr;
    private EReference entityOptionalChildRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, CodecResourceAdvancedTest.class);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Load polymorphism classes
        animalClass = EcoreHelper.getEClass(testPackage, "Animal");
        dogClass = EcoreHelper.getEClass(testPackage, "Dog");
        catClass = EcoreHelper.getEClass(testPackage, "Cat");
        birdClass = EcoreHelper.getEClass(testPackage, "Bird");
        zooClass = EcoreHelper.getEClass(testPackage, "Zoo");

        // Load bidirectional classes
        personClass = EcoreHelper.getEClass(testPackage, "Person");

        // Load circular reference classes
        nodeClass = EcoreHelper.getEClass(testPackage, "Node");
        graphClass = EcoreHelper.getEClass(testPackage, "Graph");

        // Load null/default classes
        entityClass = EcoreHelper.getEClass(testPackage, "Entity");

        // Load animal attributes
        animalNameAttr = (EAttribute) EcoreHelper.getFeature(animalClass, "name");
        animalAgeAttr = (EAttribute) EcoreHelper.getFeature(animalClass, "age");
        dogBreedAttr = (EAttribute) EcoreHelper.getFeature(dogClass, "breed");
        dogTrainedAttr = (EAttribute) EcoreHelper.getFeature(dogClass, "trained");
        catIndoorAttr = (EAttribute) EcoreHelper.getFeature(catClass, "indoor");
        catColorAttr = (EAttribute) EcoreHelper.getFeature(catClass, "color");
        birdCanFlyAttr = (EAttribute) EcoreHelper.getFeature(birdClass, "canFly");
        birdWingspanAttr = (EAttribute) EcoreHelper.getFeature(birdClass, "wingspan");
        zooNameAttr = (EAttribute) EcoreHelper.getFeature(zooClass, "name");
        zooAnimalsRef = (EReference) EcoreHelper.getFeature(zooClass, "animals");
        zooFeaturedRef = (EReference) EcoreHelper.getFeature(zooClass, "featuredAnimal");

        // Load person attributes
        personNameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        personParentRef = (EReference) EcoreHelper.getFeature(personClass, "parent");
        personChildrenRef = (EReference) EcoreHelper.getFeature(personClass, "children");

        // Load node attributes
        nodeIdAttr = (EAttribute) EcoreHelper.getFeature(nodeClass, "id");
        nodeValueAttr = (EAttribute) EcoreHelper.getFeature(nodeClass, "value");
        nodeNextRef = (EReference) EcoreHelper.getFeature(nodeClass, "next");
        nodeConnectionsRef = (EReference) EcoreHelper.getFeature(nodeClass, "connections");
        graphNameAttr = (EAttribute) EcoreHelper.getFeature(graphClass, "name");
        graphNodesRef = (EReference) EcoreHelper.getFeature(graphClass, "nodes");

        // Load entity attributes
        entityRequiredNameAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "requiredName");
        entityOptionalDescAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "optionalDescription");
        entityDefaultedIntAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "defaultedInt");
        entityDefaultedBoolAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "defaultedBoolean");
        entityDefaultedStringAttr = (EAttribute) EcoreHelper.getFeature(entityClass, "defaultedString");
        entityOptionalChildRef = (EReference) EcoreHelper.getFeature(entityClass, "optionalChild");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Factory Methods
    // ========================================================================

    private EObject createDog(String name, int age, String breed, boolean trained) {
        EObject dog = testPackage.getEFactoryInstance().create(dogClass);
        dog.eSet(animalNameAttr, name);
        dog.eSet(animalAgeAttr, age);
        dog.eSet(dogBreedAttr, breed);
        dog.eSet(dogTrainedAttr, trained);
        return dog;
    }

    private EObject createCat(String name, int age, boolean indoor, String color) {
        EObject cat = testPackage.getEFactoryInstance().create(catClass);
        cat.eSet(animalNameAttr, name);
        cat.eSet(animalAgeAttr, age);
        cat.eSet(catIndoorAttr, indoor);
        cat.eSet(catColorAttr, color);
        return cat;
    }

    private EObject createBird(String name, int age, boolean canFly, double wingspan) {
        EObject bird = testPackage.getEFactoryInstance().create(birdClass);
        bird.eSet(animalNameAttr, name);
        bird.eSet(animalAgeAttr, age);
        bird.eSet(birdCanFlyAttr, canFly);
        bird.eSet(birdWingspanAttr, wingspan);
        return bird;
    }

    private EObject createZoo(String name) {
        EObject zoo = testPackage.getEFactoryInstance().create(zooClass);
        zoo.eSet(zooNameAttr, name);
        return zoo;
    }

    private EObject createPerson(String name) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(personNameAttr, name);
        return person;
    }

    private EObject createNode(String id, int value) {
        EObject node = testPackage.getEFactoryInstance().create(nodeClass);
        node.eSet(nodeIdAttr, id);
        node.eSet(nodeValueAttr, value);
        return node;
    }

    private EObject createGraph(String name) {
        EObject graph = testPackage.getEFactoryInstance().create(graphClass);
        graph.eSet(graphNameAttr, name);
        return graph;
    }

    private EObject createEntity(String requiredName) {
        EObject entity = testPackage.getEFactoryInstance().create(entityClass);
        entity.eSet(entityRequiredNameAttr, requiredName);
        return entity;
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://advanced.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    // ========================================================================
    // POLYMORPHIC LISTS
    // ========================================================================

    /**
     * Tests for polymorphic list serialization.
     *
     * @see <a href="docs/codec-v2-spec/11-polymorphism.md">Spec: Polymorphism and Inheritance</a>
     */
    @Nested
    @DisplayName("Polymorphic List Tests")
    class PolymorphicListTests {

        @Test
        @DisplayName("round-trips zoo with mixed animal types")
        @SuppressWarnings("unchecked")
        void roundTripsZooWithMixedAnimalTypes() throws IOException {
            // Create zoo with different animal types
            EObject zoo = createZoo("City Zoo");

            EObject dog = createDog("Rex", 5, "German Shepherd", true);
            EObject cat = createCat("Whiskers", 3, true, "Orange");
            EObject bird = createBird("Tweety", 2, true, 0.15);

            List<EObject> animals = (List<EObject>) zoo.eGet(zooAnimalsRef);
            animals.add(dog);
            animals.add(cat);
            animals.add(bird);

            // Serialize
            String json = serialize(zoo);
            System.out.println("Polymorphic Zoo JSON:\n" + json);

            // Verify type information is present
            assertTrue(json.contains("Dog"), "JSON should contain Dog type");
            assertTrue(json.contains("Cat"), "JSON should contain Cat type");
            assertTrue(json.contains("Bird"), "JSON should contain Bird type");

            // Deserialize
            EObject loaded = deserialize(json, zooClass);

            // Verify
            assertNotNull(loaded);
            assertEquals("City Zoo", loaded.eGet(zooNameAttr));

            List<EObject> loadedAnimals = (List<EObject>) loaded.eGet(zooAnimalsRef);
            assertEquals(3, loadedAnimals.size());

            // Verify types are correct
            EObject loadedDog = loadedAnimals.get(0);
            assertEquals(dogClass, loadedDog.eClass());
            assertEquals("Rex", loadedDog.eGet(animalNameAttr));
            assertEquals(5, loadedDog.eGet(animalAgeAttr));
            assertEquals("German Shepherd", loadedDog.eGet(dogBreedAttr));
            assertEquals(true, loadedDog.eGet(dogTrainedAttr));

            EObject loadedCat = loadedAnimals.get(1);
            assertEquals(catClass, loadedCat.eClass());
            assertEquals("Whiskers", loadedCat.eGet(animalNameAttr));
            assertEquals(true, loadedCat.eGet(catIndoorAttr));
            assertEquals("Orange", loadedCat.eGet(catColorAttr));

            EObject loadedBird = loadedAnimals.get(2);
            assertEquals(birdClass, loadedBird.eClass());
            assertEquals("Tweety", loadedBird.eGet(animalNameAttr));
            assertEquals(true, loadedBird.eGet(birdCanFlyAttr));
            assertEquals(0.15, (Double) loadedBird.eGet(birdWingspanAttr), 0.001);
        }

        @Test
        @DisplayName("round-trips zoo with featured animal reference to polymorphic contained object")
        @SuppressWarnings("unchecked")
        void roundTripsZooWithFeaturedAnimalReference() throws IOException {
            EObject zoo = createZoo("Safari Zoo");

            EObject lion = createCat("Simba", 8, false, "Golden");
            EObject parrot = createBird("Polly", 15, true, 0.3);

            List<EObject> animals = (List<EObject>) zoo.eGet(zooAnimalsRef);
            animals.add(lion);
            animals.add(parrot);

            // Set featured animal to the parrot
            zoo.eSet(zooFeaturedRef, parrot);

            // Serialize
            String json = serialize(zoo);

            // Deserialize
            EObject loaded = deserialize(json, zooClass);

            // Verify
            List<EObject> loadedAnimals = (List<EObject>) loaded.eGet(zooAnimalsRef);
            assertEquals(2, loadedAnimals.size());

            EObject loadedFeatured = (EObject) loaded.eGet(zooFeaturedRef);
            assertNotNull(loadedFeatured, "Featured animal should be resolved");
            assertEquals("Polly", loadedFeatured.eGet(animalNameAttr));
            assertSame(loadedAnimals.get(1), loadedFeatured, "Featured should reference contained parrot");
        }

        @Test
        @DisplayName("round-trips empty polymorphic list")
        @SuppressWarnings("unchecked")
        void roundTripsEmptyPolymorphicList() throws IOException {
            EObject zoo = createZoo("Empty Zoo");

            String json = serialize(zoo);
            EObject loaded = deserialize(json, zooClass);

            assertNotNull(loaded);
            assertEquals("Empty Zoo", loaded.eGet(zooNameAttr));

            List<EObject> loadedAnimals = (List<EObject>) loaded.eGet(zooAnimalsRef);
            assertTrue(loadedAnimals.isEmpty());
        }
    }

    // ========================================================================
    // BIDIRECTIONAL REFERENCES
    // ========================================================================

    /**
     * Tests for bidirectional reference serialization.
     *
     * @see <a href="docs/codec-v2-spec/08-reference.md">Spec: Reference Serialization</a>
     */
    @Nested
    @DisplayName("Bidirectional Reference Tests")
    class BidirectionalReferenceTests {

        @Test
        @DisplayName("round-trips parent with children (bidirectional containment)")
        @SuppressWarnings("unchecked")
        void roundTripsParentWithChildren() throws IOException {
            EObject parent = createPerson("Alice");

            EObject child1 = createPerson("Bob");
            EObject child2 = createPerson("Carol");

            List<EObject> children = (List<EObject>) parent.eGet(personChildrenRef);
            children.add(child1);
            children.add(child2);

            // Verify bidirectional is set before serialization
            assertEquals(parent, child1.eGet(personParentRef));
            assertEquals(parent, child2.eGet(personParentRef));

            // Serialize
            String json = serialize(parent);
            System.out.println("Bidirectional Parent-Children JSON:\n" + json);

            // Deserialize
            EObject loaded = deserialize(json, personClass);

            // Verify
            assertNotNull(loaded);
            assertEquals("Alice", loaded.eGet(personNameAttr));

            List<EObject> loadedChildren = (List<EObject>) loaded.eGet(personChildrenRef);
            assertEquals(2, loadedChildren.size());

            EObject loadedChild1 = loadedChildren.get(0);
            EObject loadedChild2 = loadedChildren.get(1);

            assertEquals("Bob", loadedChild1.eGet(personNameAttr));
            assertEquals("Carol", loadedChild2.eGet(personNameAttr));

            // Verify bidirectional parent reference is restored
            assertSame(loaded, loadedChild1.eGet(personParentRef), "Child1 parent should reference loaded parent");
            assertSame(loaded, loadedChild2.eGet(personParentRef), "Child2 parent should reference loaded parent");
        }

        @Test
        @DisplayName("round-trips grandparent with nested children")
        @SuppressWarnings("unchecked")
        void roundTripsGrandparentWithNestedChildren() throws IOException {
            EObject grandparent = createPerson("Grandpa");

            EObject parent = createPerson("Dad");
            EObject child = createPerson("Junior");

            // Build hierarchy: Grandpa -> Dad -> Junior
            List<EObject> grandpaChildren = (List<EObject>) grandparent.eGet(personChildrenRef);
            grandpaChildren.add(parent);

            List<EObject> parentChildren = (List<EObject>) parent.eGet(personChildrenRef);
            parentChildren.add(child);

            // Serialize
            String json = serialize(grandparent);

            // Deserialize
            EObject loaded = deserialize(json, personClass);

            // Verify
            assertNotNull(loaded);
            assertEquals("Grandpa", loaded.eGet(personNameAttr));

            List<EObject> loadedGrandpaChildren = (List<EObject>) loaded.eGet(personChildrenRef);
            assertEquals(1, loadedGrandpaChildren.size());

            EObject loadedParent = loadedGrandpaChildren.get(0);
            assertEquals("Dad", loadedParent.eGet(personNameAttr));
            assertSame(loaded, loadedParent.eGet(personParentRef));

            List<EObject> loadedParentChildren = (List<EObject>) loadedParent.eGet(personChildrenRef);
            assertEquals(1, loadedParentChildren.size());

            EObject loadedChild = loadedParentChildren.get(0);
            assertEquals("Junior", loadedChild.eGet(personNameAttr));
            assertSame(loadedParent, loadedChild.eGet(personParentRef));
        }
    }

    // ========================================================================
    // CIRCULAR REFERENCES
    // ========================================================================

    /**
     * Tests for circular reference serialization.
     *
     * @see <a href="docs/codec-v2-spec/08-reference.md">Spec: Reference Serialization</a>
     */
    @Nested
    @DisplayName("Circular Reference Tests")
    class CircularReferenceTests {

        @Test
        @DisplayName("round-trips simple circular reference (A -> B -> A)")
        @SuppressWarnings("unchecked")
        void roundTripsSimpleCircularReference() throws IOException {
            EObject graph = createGraph("Circular Graph");

            EObject nodeA = createNode("A", 1);
            EObject nodeB = createNode("B", 2);

            List<EObject> nodes = (List<EObject>) graph.eGet(graphNodesRef);
            nodes.add(nodeA);
            nodes.add(nodeB);

            // Create circular: A -> B -> A
            nodeA.eSet(nodeNextRef, nodeB);
            nodeB.eSet(nodeNextRef, nodeA);

            // Serialize
            String json = serialize(graph);
            System.out.println("Circular Reference JSON:\n" + json);

            // Deserialize
            EObject loaded = deserialize(json, graphClass);

            // Verify
            assertNotNull(loaded);
            List<EObject> loadedNodes = (List<EObject>) loaded.eGet(graphNodesRef);
            assertEquals(2, loadedNodes.size());

            EObject loadedA = loadedNodes.get(0);
            EObject loadedB = loadedNodes.get(1);

            assertEquals("A", loadedA.eGet(nodeIdAttr));
            assertEquals("B", loadedB.eGet(nodeIdAttr));

            // Verify circular references are restored
            assertSame(loadedB, loadedA.eGet(nodeNextRef), "A.next should reference B");
            assertSame(loadedA, loadedB.eGet(nodeNextRef), "B.next should reference A");
        }

        @Test
        @DisplayName("round-trips self-referential node (A -> A)")
        @SuppressWarnings("unchecked")
        void roundTripsSelfReferentialNode() throws IOException {
            EObject graph = createGraph("Self-Reference Graph");

            EObject nodeA = createNode("A", 42);

            List<EObject> nodes = (List<EObject>) graph.eGet(graphNodesRef);
            nodes.add(nodeA);

            // Self-reference: A -> A
            nodeA.eSet(nodeNextRef, nodeA);

            // Serialize
            String json = serialize(graph);

            // Deserialize
            EObject loaded = deserialize(json, graphClass);

            // Verify
            List<EObject> loadedNodes = (List<EObject>) loaded.eGet(graphNodesRef);
            assertEquals(1, loadedNodes.size());

            EObject loadedA = loadedNodes.get(0);
            assertEquals("A", loadedA.eGet(nodeIdAttr));

            // Verify self-reference is restored
            assertSame(loadedA, loadedA.eGet(nodeNextRef), "A.next should reference itself");
        }

        @Test
        @DisplayName("round-trips multi-valued circular connections")
        @SuppressWarnings("unchecked")
        void roundTripsMultiValuedCircularConnections() throws IOException {
            EObject graph = createGraph("Multi-Connection Graph");

            EObject nodeA = createNode("A", 1);
            EObject nodeB = createNode("B", 2);
            EObject nodeC = createNode("C", 3);

            List<EObject> nodes = (List<EObject>) graph.eGet(graphNodesRef);
            nodes.add(nodeA);
            nodes.add(nodeB);
            nodes.add(nodeC);

            // A connects to B and C
            List<EObject> aConnections = (List<EObject>) nodeA.eGet(nodeConnectionsRef);
            aConnections.add(nodeB);
            aConnections.add(nodeC);

            // B connects to A and C (circular)
            List<EObject> bConnections = (List<EObject>) nodeB.eGet(nodeConnectionsRef);
            bConnections.add(nodeA);
            bConnections.add(nodeC);

            // C connects to A (circular)
            List<EObject> cConnections = (List<EObject>) nodeC.eGet(nodeConnectionsRef);
            cConnections.add(nodeA);

            // Serialize
            String json = serialize(graph);

            // Deserialize
            EObject loaded = deserialize(json, graphClass);

            // Verify
            List<EObject> loadedNodes = (List<EObject>) loaded.eGet(graphNodesRef);
            assertEquals(3, loadedNodes.size());

            EObject loadedA = loadedNodes.get(0);
            EObject loadedB = loadedNodes.get(1);
            EObject loadedC = loadedNodes.get(2);

            List<EObject> loadedAConns = (List<EObject>) loadedA.eGet(nodeConnectionsRef);
            assertEquals(2, loadedAConns.size());
            assertSame(loadedB, loadedAConns.get(0));
            assertSame(loadedC, loadedAConns.get(1));

            List<EObject> loadedBConns = (List<EObject>) loadedB.eGet(nodeConnectionsRef);
            assertEquals(2, loadedBConns.size());
            assertSame(loadedA, loadedBConns.get(0));
            assertSame(loadedC, loadedBConns.get(1));

            List<EObject> loadedCConns = (List<EObject>) loadedC.eGet(nodeConnectionsRef);
            assertEquals(1, loadedCConns.size());
            assertSame(loadedA, loadedCConns.get(0));
        }
    }

    // ========================================================================
    // NULL/DEFAULT VALUE HANDLING
    // ========================================================================

    /**
     * Tests for null and default value handling.
     *
     * @see <a href="docs/codec-v2-spec/09-feature.md">Spec: Feature Serialization</a>
     */
    @Nested
    @DisplayName("Null/Default Value Tests")
    class NullDefaultValueTests {

        @Test
        @DisplayName("deserializes entity with default values when not in JSON")
        void deserializesEntityWithDefaultValues() throws IOException {
            // JSON with only required field
            String json = """
                {
                    "_type": "http://test.example.org/advanced/1.0#//Entity",
                    "requiredName": "Test Entity"
                }
                """;

            EObject loaded = deserialize(json, entityClass);

            assertNotNull(loaded);
            assertEquals("Test Entity", loaded.eGet(entityRequiredNameAttr));

            // Check defaults are applied
            assertEquals(42, loaded.eGet(entityDefaultedIntAttr));
            assertEquals(true, loaded.eGet(entityDefaultedBoolAttr));
            assertEquals("default", loaded.eGet(entityDefaultedStringAttr));

            // Optional should be null
            assertNull(loaded.eGet(entityOptionalDescAttr));
            assertNull(loaded.eGet(entityOptionalChildRef));
        }

        @Test
        @DisplayName("round-trips entity with explicit values overriding defaults")
        void roundTripsEntityWithExplicitValues() throws IOException {
            EObject entity = createEntity("Custom Entity");
            entity.eSet(entityOptionalDescAttr, "A description");
            entity.eSet(entityDefaultedIntAttr, 100);
            entity.eSet(entityDefaultedBoolAttr, false);
            entity.eSet(entityDefaultedStringAttr, "custom");

            String json = serialize(entity);
            EObject loaded = deserialize(json, entityClass);

            assertEquals("Custom Entity", loaded.eGet(entityRequiredNameAttr));
            assertEquals("A description", loaded.eGet(entityOptionalDescAttr));
            assertEquals(100, loaded.eGet(entityDefaultedIntAttr));
            assertEquals(false, loaded.eGet(entityDefaultedBoolAttr));
            assertEquals("custom", loaded.eGet(entityDefaultedStringAttr));
        }

        @Test
        @DisplayName("round-trips entity with null optional child")
        void roundTripsEntityWithNullOptionalChild() throws IOException {
            EObject entity = createEntity("Parent Entity");
            // Don't set optional child

            String json = serialize(entity);
            EObject loaded = deserialize(json, entityClass);

            assertNotNull(loaded);
            assertNull(loaded.eGet(entityOptionalChildRef));
        }

        @Test
        @DisplayName("round-trips entity with nested optional child")
        void roundTripsEntityWithNestedOptionalChild() throws IOException {
            EObject parent = createEntity("Parent");
            EObject child = createEntity("Child");
            parent.eSet(entityOptionalChildRef, child);

            String json = serialize(parent);
            EObject loaded = deserialize(json, entityClass);

            assertNotNull(loaded);
            EObject loadedChild = (EObject) loaded.eGet(entityOptionalChildRef);
            assertNotNull(loadedChild);
            assertEquals("Child", loadedChild.eGet(entityRequiredNameAttr));
        }

        /**
         * Tests the distinction between explicit null in JSON vs missing field.
         * <p>
         * Per spec section 1.3 (Deserialization Behavior):
         * - Explicit null in JSON → sets feature to null (for object types like EString)
         * - Explicit null in JSON for primitive types → EMF resets to defaultValueLiteral
         * - Missing field → feature retains EMF default value (same as eUnset behavior)
         * </p>
         * <p>
         * EMF Behavior Note: For primitive types (EInt, EBoolean, etc.), eSet(attr, null)
         * resets the feature to its EMF default (from defaultValueLiteral), NOT to the
         * Java primitive default (0, false). This is standard EMF behavior.
         * </p>
         *
         * @see <a href="docs/codec-v2-spec/09-feature.md#13-deserialization-behavior">Spec 1.3: Deserialization Behavior</a>
         */
        @Test
        @DisplayName("distinguishes explicit null from missing field in deserialization")
        void distinguishesExplicitNullFromMissingField() throws IOException {
            // JSON with explicit null for defaultedString (should set to null)
            // and missing defaultedInt (should use EMF default 42)
            String json = """
                {
                    "_type": "http://test.example.org/advanced/1.0#//Entity",
                    "requiredName": "Null Test Entity",
                    "defaultedString": null
                }
                """;

            EObject loaded = deserialize(json, entityClass);

            assertNotNull(loaded);
            assertEquals("Null Test Entity", loaded.eGet(entityRequiredNameAttr));

            // Explicit null in JSON for String (object type) → should be null
            assertNull(loaded.eGet(entityDefaultedStringAttr),
                    "Explicit null in JSON should set String feature to null");

            // Missing field → should use EMF default 42
            assertEquals(42, loaded.eGet(entityDefaultedIntAttr),
                    "Missing field should retain EMF default value");

            // Missing field → should use EMF default true
            assertEquals(true, loaded.eGet(entityDefaultedBoolAttr),
                    "Missing field should retain EMF default value");
        }

        /**
         * Tests that explicit null for primitive types results in the EMF default value.
         * <p>
         * EMF Behavior: eSet(attr, null) for primitives resets to defaultValueLiteral.
         * JSON null + EInt with default 42 → 42 (EMF default), NOT 0 (Java primitive default)
         * JSON null + EBoolean with default true → true (EMF default), NOT false (Java primitive default)
         * </p>
         * <p>
         * This behavior is consistent with EMF's interpretation of null as "unset" for primitives,
         * which resets to the declared default value.
         * </p>
         */
        @Test
        @DisplayName("explicit null for primitive types resets to EMF default value")
        void explicitNullForPrimitiveResetsToEmfDefault() throws IOException {
            // JSON with explicit null for defaultedInt (should reset to EMF default 42)
            String json = """
                {
                    "_type": "http://test.example.org/advanced/1.0#//Entity",
                    "requiredName": "Primitive Null Test",
                    "defaultedInt": null,
                    "defaultedBoolean": null
                }
                """;

            EObject loaded = deserialize(json, entityClass);

            // Explicit null for EInt → EMF resets to defaultValueLiteral (42), NOT Java's 0
            assertEquals(42, loaded.eGet(entityDefaultedIntAttr),
                    "Explicit null for primitive EInt should reset to EMF default (42), not Java primitive default (0)");

            // Explicit null for EBoolean → EMF resets to defaultValueLiteral (true), NOT Java's false
            assertEquals(true, loaded.eGet(entityDefaultedBoolAttr),
                    "Explicit null for primitive EBoolean should reset to EMF default (true), not Java primitive default (false)");
        }

        /**
         * Tests that serializeNull/serializeDefaults settings do NOT affect deserialization.
         * <p>
         * Per spec: These are serialization-only configurations.
         * </p>
         */
        @Test
        @DisplayName("serializeNull setting does not affect deserialization behavior")
        void serializeNullDoesNotAffectDeserialization() throws IOException {
            // This test verifies that regardless of how the JSON was produced
            // (with serializeNull=true or false), the deserializer handles it consistently

            // JSON as if produced with serializeNull=true (explicit nulls present)
            String jsonWithNulls = """
                {
                    "_type": "http://test.example.org/advanced/1.0#//Entity",
                    "requiredName": "With Nulls",
                    "optionalDescription": null,
                    "defaultedInt": 42,
                    "defaultedBoolean": true,
                    "defaultedString": "default"
                }
                """;

            // JSON as if produced with serializeNull=false (nulls omitted)
            String jsonWithoutNulls = """
                {
                    "_type": "http://test.example.org/advanced/1.0#//Entity",
                    "requiredName": "Without Nulls",
                    "defaultedInt": 42,
                    "defaultedBoolean": true,
                    "defaultedString": "default"
                }
                """;

            EObject loadedWithNulls = deserialize(jsonWithNulls, entityClass);
            EObject loadedWithoutNulls = deserialize(jsonWithoutNulls, entityClass);

            // Both should have null for optionalDescription
            // (explicit null vs missing both result in null for optional String)
            assertNull(loadedWithNulls.eGet(entityOptionalDescAttr));
            assertNull(loadedWithoutNulls.eGet(entityOptionalDescAttr));

            // Other values should be identical
            assertEquals(42, loadedWithNulls.eGet(entityDefaultedIntAttr));
            assertEquals(42, loadedWithoutNulls.eGet(entityDefaultedIntAttr));
            assertEquals(true, loadedWithNulls.eGet(entityDefaultedBoolAttr));
            assertEquals(true, loadedWithoutNulls.eGet(entityDefaultedBoolAttr));
        }
    }

    // ========================================================================
    // ERROR SCENARIOS
    // ========================================================================

    /**
     * Tests for error handling scenarios.
     *
     * @see <a href="docs/codec-v2-spec/00-overview.md#2-error-and-warning-handling">Spec: Error Handling</a>
     */
    @Nested
    @DisplayName("Error Scenario Tests")
    class ErrorScenarioTests {

        @Test
        @DisplayName("handles unknown type gracefully")
        void handlesUnknownTypeGracefully() throws IOException {
            String json = """
                {
                    "_type": "http://nonexistent.example.org/unknown#//UnknownClass",
                    "name": "Test"
                }
                """;

            // Should handle gracefully - returns null when type cannot be resolved
            EObject loaded = deserialize(json, null);
            assertNull(loaded, "Unknown type should result in null");
        }

        @Test
        @DisplayName("handles malformed JSON gracefully")
        void handlesMalformedJsonGracefully() {
            String json = "{ invalid json }";

            assertThrows(Exception.class, () -> deserialize(json, entityClass));
        }

        @Test
        @DisplayName("handles empty JSON object")
        void handlesEmptyJsonObject() throws IOException {
            String json = "{}";

            // Should return null or throw depending on implementation
            try {
                deserialize(json, entityClass);
                // If it doesn't throw, the result should be null or an empty object
                // This depends on the implementation's handling of missing _type
            } catch (Exception e) {
                // Expected - no type information
            }
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String serialize(EObject object) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootEClass) throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        if (rootEClass != null) {
            options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
