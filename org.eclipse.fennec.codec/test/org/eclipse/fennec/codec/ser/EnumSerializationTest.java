/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.metadata.model.codec.CodecFactory;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.EnumSerializationStrategy;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for enum serialization strategies.
 * <p>
 * The test model has an enum Status with different name/literal values:
 * <ul>
 *   <li>PENDING (value=0, literal="pending")</li>
 *   <li>ACTIVE (value=1, literal="Active")</li>
 *   <li>COMPLETED (value=2, literal="COMPLETED")</li>
 * </ul>
 * </p>
 *
 * @see EnumSerializationStrategy
 * @see <a href="docs/codec-v2-spec/08-feature.md#4-enum-serialization">Spec: Enum Serialization</a>
 */
@DisplayName("Enum Serialization Tests")
class EnumSerializationTest {

    private static final String TEST_ECORE = "test-enum-serialization.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass taskClass;
    private EAttribute nameAttribute;
    private EAttribute statusAttribute;
    private EAttribute previousStatusesAttribute;
    private EEnum statusEnum;

    // Enum literals
    private EEnumLiteral pendingLiteral;
    private EEnumLiteral activeLiteral;
    private EEnumLiteral completedLiteral;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(EnumSerializationTest.class);
        testPackage = ecoreHelper.loadEcore(TEST_ECORE);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        taskClass = ecoreHelper.getEClass(testPackage, "Task");
        nameAttribute = (EAttribute) ecoreHelper.getFeature(taskClass, "name");
        statusAttribute = (EAttribute) ecoreHelper.getFeature(taskClass, "status");
        previousStatusesAttribute = (EAttribute) ecoreHelper.getFeature(taskClass, "previousStatuses");

        statusEnum = (EEnum) testPackage.getEClassifier("Status");
        pendingLiteral = statusEnum.getEEnumLiteral("PENDING");
        activeLiteral = statusEnum.getEEnumLiteral("ACTIVE");
        completedLiteral = statusEnum.getEEnumLiteral("COMPLETED");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createTask(String name, Enumerator status) {
        EObject task = testPackage.getEFactoryInstance().create(taskClass);
        task.eSet(nameAttribute, name);
        task.eSet(statusAttribute, status);
        return task;
    }

    @Nested
    @DisplayName("LITERAL strategy (default)")
    class LiteralStrategy {

        @Test
        @DisplayName("serializes enum using literal string")
        void serializesEnumUsingLiteralString() throws IOException {
            // ACTIVE has name="ACTIVE" but literal="Active"
            EObject task = createTask("Test Task", activeLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.LITERAL);

            // Should use literal "Active", not name "ACTIVE"
            assertTrue(json.contains("\"Active\""), "JSON should contain literal 'Active', got: " + json);
            assertFalse(json.contains("\"ACTIVE\""), "JSON should NOT contain name 'ACTIVE'");
        }

        @Test
        @DisplayName("round-trips enum with LITERAL strategy")
        void roundTripsEnumWithLiteralStrategy() throws IOException {
            EObject task = createTask("Test Task", activeLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.LITERAL);
            EObject loaded = deserialize(json, EnumSerializationStrategy.LITERAL);

            assertNotNull(loaded);
            assertEquals(activeLiteral.getInstance(), loaded.eGet(statusAttribute));
        }

        @Test
        @DisplayName("round-trips enum where name equals literal")
        void roundTripsEnumWhereNameEqualsLiteral() throws IOException {
            // COMPLETED has name="COMPLETED" and literal="COMPLETED" (same)
            EObject task = createTask("Test Task", completedLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.LITERAL);
            assertTrue(json.contains("\"COMPLETED\""), "JSON should contain 'COMPLETED'");

            EObject loaded = deserialize(json, EnumSerializationStrategy.LITERAL);

            assertNotNull(loaded);
            assertEquals(completedLiteral.getInstance(), loaded.eGet(statusAttribute));
        }
    }

    @Nested
    @DisplayName("NAME strategy")
    class NameStrategy {

        @Test
        @DisplayName("serializes enum using name string")
        void serializesEnumUsingNameString() throws IOException {
            // ACTIVE has name="ACTIVE" but literal="Active"
            EObject task = createTask("Test Task", activeLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.NAME);

            // Should use name "ACTIVE", not literal "Active"
            assertTrue(json.contains("\"ACTIVE\""), "JSON should contain name 'ACTIVE', got: " + json);
        }

        @Test
        @DisplayName("round-trips enum with NAME strategy when name equals literal")
        void roundTripsEnumWithNameStrategyWhenNameEqualsLiteral() throws IOException {
            // COMPLETED has name="COMPLETED" and literal="COMPLETED" (same)
            // This should round-trip successfully
            EObject task = createTask("Test Task", completedLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.NAME);
            assertTrue(json.contains("\"COMPLETED\""), "JSON should contain 'COMPLETED'");

            EObject loaded = deserialize(json, EnumSerializationStrategy.NAME);

            assertNotNull(loaded);
            assertEquals(completedLiteral.getInstance(), loaded.eGet(statusAttribute));
        }

        @Test
        @DisplayName("round-trips enum with NAME strategy when name differs from literal")
        void roundTripsEnumWithNameStrategyWhenNameDiffersFromLiteral() throws IOException {
            // ACTIVE has name="ACTIVE" but literal="Active"
            // Deserialization now uses EEnum.getEEnumLiteral(name) which finds by name
            EObject task = createTask("Test Task", activeLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.NAME);

            // Serialization should use name "ACTIVE"
            assertTrue(json.contains("\"ACTIVE\""), "JSON should contain name 'ACTIVE', got: " + json);
            assertFalse(json.contains("\"Active\""), "JSON should NOT contain literal 'Active'");

            // Deserialization should work - we now use EEnum.getEEnumLiteral(name) for lookup
            EObject loaded = deserialize(json, EnumSerializationStrategy.NAME);

            assertNotNull(loaded);
            assertEquals(activeLiteral.getInstance(), loaded.eGet(statusAttribute));
        }

        @Test
        @DisplayName("serializes enum where name differs from literal")
        void serializesEnumWhereNameDiffersFromLiteral() throws IOException {
            // COMPLETED has same name and literal, ACTIVE has different name/literal
            EObject task = createTask("Test Task", activeLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.NAME);

            // Should use name "ACTIVE", not literal "Active"
            assertTrue(json.contains("\"ACTIVE\""), "JSON should contain name 'ACTIVE', got: " + json);
        }
    }

    @Nested
    @DisplayName("VALUE strategy")
    class ValueStrategy {

        @Test
        @DisplayName("serializes enum using ordinal value")
        void serializesEnumUsingOrdinalValue() throws IOException {
            // ACTIVE has value=1
            EObject task = createTask("Test Task", activeLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.VALUE);

            // Should use value 1, not string
            assertTrue(json.contains("\"status\":1") || json.contains("\"status\": 1"),
                    "JSON should contain status:1, got: " + json);
        }

        @Test
        @DisplayName("round-trips enum with VALUE strategy")
        void roundTripsEnumWithValueStrategy() throws IOException {
            EObject task = createTask("Test Task", activeLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.VALUE);
            EObject loaded = deserialize(json, EnumSerializationStrategy.VALUE);

            assertNotNull(loaded);
            assertEquals(activeLiteral.getInstance(), loaded.eGet(statusAttribute));
        }

        @Test
        @DisplayName("serializes each enum value correctly")
        void serializesEachEnumValueCorrectly() throws IOException {
            // Test all three values
            EObject task0 = createTask("Task 0", pendingLiteral.getInstance());    // value=0
            EObject task1 = createTask("Task 1", activeLiteral.getInstance());     // value=1
            EObject task2 = createTask("Task 2", completedLiteral.getInstance());  // value=2

            String json0 = serialize(task0, EnumSerializationStrategy.VALUE);
            String json1 = serialize(task1, EnumSerializationStrategy.VALUE);
            String json2 = serialize(task2, EnumSerializationStrategy.VALUE);

            // Note: PENDING (value=0) is the default value, so it won't be serialized by default
            // (serializeDefaults is false). This is expected behavior.
            assertFalse(json0.contains("\"status\""),
                    "PENDING (default value 0) should NOT be serialized when serializeDefaults=false, got: " + json0);
            assertTrue(json1.contains("\"status\":1") || json1.contains("\"status\": 1"),
                    "ACTIVE should serialize to 1, got: " + json1);
            assertTrue(json2.contains("\"status\":2") || json2.contains("\"status\": 2"),
                    "COMPLETED should serialize to 2, got: " + json2);
        }
    }

    @Nested
    @DisplayName("Multi-valued enum attributes")
    class MultiValuedEnums {

        @Test
        @DisplayName("round-trips multi-valued enum with LITERAL strategy")
        @SuppressWarnings("unchecked")
        void roundTripsMultiValuedEnumWithLiteralStrategy() throws IOException {
            EObject task = createTask("Test Task", completedLiteral.getInstance());
            List<Enumerator> previousStatuses = (List<Enumerator>) task.eGet(previousStatusesAttribute);
            previousStatuses.add(pendingLiteral.getInstance());
            previousStatuses.add(activeLiteral.getInstance());

            String json = serialize(task, EnumSerializationStrategy.LITERAL);
            assertTrue(json.contains("\"pending\""), "JSON should contain literal 'pending'");
            assertTrue(json.contains("\"Active\""), "JSON should contain literal 'Active'");

            EObject loaded = deserialize(json, EnumSerializationStrategy.LITERAL);

            assertNotNull(loaded);
            List<Enumerator> loadedStatuses = (List<Enumerator>) loaded.eGet(previousStatusesAttribute);
            assertEquals(2, loadedStatuses.size());
            assertEquals(pendingLiteral.getInstance(), loadedStatuses.get(0));
            assertEquals(activeLiteral.getInstance(), loadedStatuses.get(1));
        }

        @Test
        @DisplayName("round-trips multi-valued enum with VALUE strategy")
        @SuppressWarnings("unchecked")
        void roundTripsMultiValuedEnumWithValueStrategy() throws IOException {
            EObject task = createTask("Test Task", completedLiteral.getInstance());
            List<Enumerator> previousStatuses = (List<Enumerator>) task.eGet(previousStatusesAttribute);
            previousStatuses.add(pendingLiteral.getInstance());   // value=0
            previousStatuses.add(activeLiteral.getInstance());    // value=1

            String json = serialize(task, EnumSerializationStrategy.VALUE);
            // Array should contain [0, 1]
            assertTrue(json.contains("[0,1]") || json.contains("[0, 1]") || json.contains("[ 0, 1 ]"),
                    "JSON should contain array [0,1], got: " + json);

            EObject loaded = deserialize(json, EnumSerializationStrategy.VALUE);

            assertNotNull(loaded);
            List<Enumerator> loadedStatuses = (List<Enumerator>) loaded.eGet(previousStatusesAttribute);
            assertEquals(2, loadedStatuses.size());
            assertEquals(pendingLiteral.getInstance(), loadedStatuses.get(0));
            assertEquals(activeLiteral.getInstance(), loadedStatuses.get(1));
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String serialize(EObject object, EnumSerializationStrategy strategy) throws IOException {
        // Create metadata service with enum strategy configured via aspect
        MetadataWhiteboard ms = MetadataServiceFactory.create();
        ms.registerPackage(testPackage);

        // Set enum strategy on feature metadata
        setEnumStrategy(ms, statusAttribute, strategy);
        setEnumStrategy(ms, previousStatusesAttribute, strategy);

        CodecResource resource = new CodecResource(
                URI.createURI("test://enum-test.json"),
                ms,
                ConfigurationResolver.defaults(),
                null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());

        String json = out.toString(StandardCharsets.UTF_8);
        System.out.println("Serialized JSON (" + strategy + "):\n" + json);
        return json;
    }

    private EObject deserialize(String json, EnumSerializationStrategy strategy) throws IOException {
        // Create metadata service with enum strategy configured via aspect
        MetadataWhiteboard ms = MetadataServiceFactory.create();
        ms.registerPackage(testPackage);

        // Set enum strategy on feature metadata
        setEnumStrategy(ms, statusAttribute, strategy);
        setEnumStrategy(ms, previousStatusesAttribute, strategy);

        CodecResource resource = new CodecResource(
                URI.createURI("test://enum-test.json"),
                ms,
                ConfigurationResolver.defaults(),
                null);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, taskClass);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private void setEnumStrategy(MetadataWhiteboard ms, EAttribute attribute, EnumSerializationStrategy strategy) {
        FeatureMetadata featureMetadata = ms.getFeatureMetadata(attribute);
        if (featureMetadata != null) {
            // Find or create FeatureCodecAspect
            FeatureCodecAspect aspect = featureMetadata.getAspects().stream()
                    .filter(FeatureCodecAspect.class::isInstance)
                    .map(FeatureCodecAspect.class::cast)
                    .findFirst()
                    .orElseGet(() -> {
                        FeatureCodecAspect newAspect = CodecFactory.eINSTANCE.createFeatureCodecAspect();
                        featureMetadata.getAspects().add(newAspect);
                        return newAspect;
                    });
            aspect.setEnumSerialization(strategy);
        }
    }
}
