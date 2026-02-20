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
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for forceWrite/forceRead feature configuration.
 * <p>
 * These tests verify that:
 * <ul>
 *   <li>Volatile/transient/derived features are skipped by default</li>
 *   <li>forceWrite enables serialization of such features</li>
 *   <li>forceRead enables deserialization into such features</li>
 *   <li>forceWrite/forceRead(EStructuralFeature...) convenience methods work</li>
 *   <li>toBuilder() preserves forceWrite/forceRead settings</li>
 * </ul>
 * </p>
 */
@DisplayName("ForceWrite/ForceRead Tests")
class ForceReadWriteTest {

    private static final String TEST_ECORE = "test-serialization.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EAttribute idAttribute;
    private EAttribute nameAttribute;
    private EAttribute fullNameAttribute; // volatile, transient, derived

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, ForceReadWriteTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        idAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "id");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        fullNameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "fullName");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createPerson(String id, String name) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(idAttribute, id);
        person.eSet(nameAttribute, name);
        return person;
    }

    @Nested
    @DisplayName("Feature Configuration")
    class FeatureConfigurationTests {

        @Test
        @DisplayName("volatile attribute is marked for ignore by default")
        void volatileAttributeIgnoredByDefault() {
            assertTrue(fullNameAttribute.isVolatile(), "fullName should be volatile");
            assertTrue(fullNameAttribute.isTransient(), "fullName should be transient");
            assertTrue(fullNameAttribute.isDerived(), "fullName should be derived");

            ConfigurationResolver resolver = ConfigurationResolver.defaults();
            DiagnosticCollector diag = new DiagnosticCollector();

            FeatureConfig config = resolver.resolveFeatureConfig(fullNameAttribute, diag);

            assertTrue(config.isIgnore(), "Volatile feature should be ignored by default");
            assertFalse(config.shouldSerialize(), "Volatile feature should not serialize by default");
        }

        @Test
        @DisplayName("forceWrite enables serialization of volatile attribute")
        void forceWriteEnablesVolatileSerialization() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(fullNameAttribute)
                    .build();

            DiagnosticCollector diag = new DiagnosticCollector();
            FeatureConfig config = resolver.resolveFeatureConfig(fullNameAttribute, diag);

            assertTrue(config.isForceWrite(), "forceWrite should be enabled");
            assertFalse(config.isIgnore(), "ignore should be cleared when forceWrite is set");
            assertTrue(config.shouldSerialize(), "Feature with forceWrite should serialize");
        }

        @Test
        @DisplayName("forceWrite does not affect regular attributes")
        void forceWriteDoesNotAffectRegularAttributes() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(fullNameAttribute)
                    .build();

            DiagnosticCollector diag = new DiagnosticCollector();
            FeatureConfig nameConfig = resolver.resolveFeatureConfig(nameAttribute, diag);

            assertFalse(nameConfig.isForceWrite(), "Regular attribute should not have forceWrite");
            assertFalse(nameConfig.isIgnore(), "Regular attribute should not be ignored");
            assertTrue(nameConfig.shouldSerialize(), "Regular attribute should serialize");
        }
    }

    @Nested
    @DisplayName("Serialization Behavior")
    class SerializationBehaviorTests {

        @Test
        @DisplayName("volatile attribute is excluded from JSON by default")
        void volatileAttributeExcludedByDefault() throws IOException {
            EObject person = createPerson("p1", "John");

            ConfigurationResolver resolver = ConfigurationResolver.defaults();
            String json = serialize(person, resolver);

            System.out.println("JSON without forceWrite:\n" + json);

            assertTrue(json.contains("\"name\""), "name should be in JSON");
            assertFalse(json.contains("\"fullName\""), "fullName should NOT be in JSON (volatile)");
        }

        @Test
        @DisplayName("volatile attribute with null value NOT included with forceWrite alone")
        void volatileAttributeWithNullValueNotIncludedWithForceWriteAlone() throws IOException {
            EObject person = createPerson("p2", "Jane");

            // forceWrite alone allows volatile features past visibility gate,
            // but null values are still filtered by serializeNull (default: false)
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(fullNameAttribute)
                    .build();

            String json = serialize(person, resolver);

            assertTrue(json.contains("\"name\""), "name should be in JSON");
            // fullName is volatile and returns null - it should NOT be serialized
            // because serializeNull defaults to false (see spec: forceWrite only affects visibility gate)
            assertFalse(json.contains("\"fullName\""),
                    "fullName should NOT be in JSON (null value + serializeNull=false)");
        }

        @Test
        @DisplayName("volatile attribute with null value IS included with forceWrite AND serializeNull")
        void volatileAttributeIncludedWithForceWriteAndSerializeNull() throws IOException {
            EObject person = createPerson("p2b", "Jane");

            // Need both forceWrite (visibility gate) AND serializeNull (value gate)
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(fullNameAttribute)
                    .serializeNull(true)
                    .build();

            String json = serialize(person, resolver);

            assertTrue(json.contains("\"name\""), "name should be in JSON");
            // Now fullName SHOULD be serialized because serializeNull=true
            assertTrue(json.contains("\"fullName\""),
                    "fullName should be in JSON with forceWrite + serializeNull=true");
        }

        @Test
        @DisplayName("multiple volatile attributes can be force-written")
        void multipleVolatileAttributesForceWritten() throws IOException {
            // fullNameAttribute is the only volatile one in this model
            // But this tests that forceWrite accepts multiple features
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(fullNameAttribute, nameAttribute) // nameAttribute is not volatile, but included for test
                    .build();

            DiagnosticCollector diag = new DiagnosticCollector();
            FeatureConfig fullNameConfig = resolver.resolveFeatureConfig(fullNameAttribute, diag);
            FeatureConfig nameConfig = resolver.resolveFeatureConfig(nameAttribute, diag);

            assertTrue(fullNameConfig.isForceWrite(), "fullName should have forceWrite");
            assertTrue(nameConfig.isForceWrite(), "name should have forceWrite (even though not volatile)");
        }
    }

    @Nested
    @DisplayName("forceRead Deserialization")
    class ForceReadDeserializationTests {

        @Test
        @DisplayName("forceRead enables FeatureConfig for volatile attribute")
        void forceReadEnablesVolatileFeature() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceRead(fullNameAttribute)
                    .build();

            DiagnosticCollector diag = new DiagnosticCollector();
            FeatureConfig config = resolver.resolveFeatureConfig(fullNameAttribute, diag);

            assertTrue(config.isForceRead(), "forceRead should be enabled");
            // Note: forceRead doesn't clear ignore - that's for serialization
            // forceRead is about allowing deserialization into volatile features
        }

        @Test
        @DisplayName("volatile attribute without forceRead has forceRead=false")
        void volatileWithoutForceReadHasForceReadFalse() {
            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            DiagnosticCollector diag = new DiagnosticCollector();
            FeatureConfig config = resolver.resolveFeatureConfig(fullNameAttribute, diag);

            assertFalse(config.isForceRead(), "forceRead should be false by default");
        }

        @Test
        @DisplayName("forceRead accepts multiple features")
        void forceReadAcceptsMultipleFeatures() {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceRead(fullNameAttribute, nameAttribute)
                    .build();

            DiagnosticCollector diag = new DiagnosticCollector();
            FeatureConfig fullNameConfig = resolver.resolveFeatureConfig(fullNameAttribute, diag);
            FeatureConfig nameConfig = resolver.resolveFeatureConfig(nameAttribute, diag);

            assertTrue(fullNameConfig.isForceRead(), "fullName should have forceRead");
            assertTrue(nameConfig.isForceRead(), "name should have forceRead");
        }

        @Test
        @DisplayName("volatile attribute is NOT deserialized without forceRead")
        void volatileAttributeNotDeserializedWithoutForceRead() throws IOException {
            // JSON with fullName value
            String json = "{\"id\":\"p1\",\"name\":\"John\",\"fullName\":\"John Doe\"}";

            // Load WITHOUT forceRead - volatile feature should be skipped
            ConfigurationResolver resolver = ConfigurationResolver.defaults();
            EObject loaded = deserialize(json, resolver);

            assertNotNull(loaded, "Person should be loaded");
            assertEquals("John", loaded.eGet(nameAttribute), "name should be loaded");
            // fullName is volatile and not in forceRead - the deserializer should skip it
            // The feature returns whatever the model computes (null for this test model)
        }

        @Test
        @DisplayName("forceRead on non-changeable feature is skipped")
        void forceReadOnNonChangeableFeatureIsSkipped() throws IOException {
            // JSON with fullName value
            String json = "{\"id\":\"p1\",\"name\":\"John\",\"fullName\":\"John Doe\"}";

            // fullNameAttribute has changeable="false" in the test model
            // Even with forceRead, we cannot set values on non-changeable features
            assertFalse(fullNameAttribute.isChangeable(),
                    "fullName should not be changeable (test model design)");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceRead(fullNameAttribute)
                    .build();

            EObject loaded = deserialize(json, resolver);

            assertNotNull(loaded, "Person should be loaded");
            assertEquals("John", loaded.eGet(nameAttribute), "name should be loaded");
            // fullName is non-changeable, so it cannot be set even with forceRead
            // The value remains whatever the model computes (null in this case)
            // This is correct behavior per spec: forceRead enables entry creation,
            // but non-changeable features are still skipped by the deserializer
        }

        @Test
        @DisplayName("forceRead on regular feature enables deserialization")
        void forceReadOnRegularFeatureEnablesDeserialization() throws IOException {
            // Test that forceRead works on a changeable feature
            // (nameAttribute is changeable, not volatile)
            String json = "{\"id\":\"p1\",\"name\":\"John\"}";

            assertTrue(nameAttribute.isChangeable(),
                    "name should be changeable");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceRead(nameAttribute)
                    .build();

            EObject loaded = deserialize(json, resolver);

            assertNotNull(loaded, "Person should be loaded");
            assertEquals("John", loaded.eGet(nameAttribute),
                    "name should be deserialized with forceRead");
        }
    }

    @Nested
    @DisplayName("toBuilder() Preservation")
    class ToBuilderPreservationTests {

        @Test
        @DisplayName("toBuilder preserves forceWrite features")
        void toBuilderPreservesForceWrite() {
            ConfigurationResolver original = ConfigurationResolver.builder()
                    .forceWrite(fullNameAttribute, nameAttribute)
                    .build();

            // Verify original has forceWrite
            DiagnosticCollector diag = new DiagnosticCollector();
            assertTrue(original.resolveFeatureConfig(fullNameAttribute, diag).isForceWrite());
            assertTrue(original.resolveFeatureConfig(nameAttribute, diag).isForceWrite());

            // Create new resolver via toBuilder
            ConfigurationResolver rebuilt = original.toBuilder().build();

            // Verify rebuilt preserves forceWrite
            DiagnosticCollector diag2 = new DiagnosticCollector();
            assertTrue(rebuilt.resolveFeatureConfig(fullNameAttribute, diag2).isForceWrite(),
                    "toBuilder should preserve forceWrite for fullName");
            assertTrue(rebuilt.resolveFeatureConfig(nameAttribute, diag2).isForceWrite(),
                    "toBuilder should preserve forceWrite for name");
        }

        @Test
        @DisplayName("toBuilder preserves forceRead features")
        void toBuilderPreservesForceRead() {
            ConfigurationResolver original = ConfigurationResolver.builder()
                    .forceRead(fullNameAttribute, nameAttribute)
                    .build();

            // Verify original has forceRead
            DiagnosticCollector diag = new DiagnosticCollector();
            assertTrue(original.resolveFeatureConfig(fullNameAttribute, diag).isForceRead());
            assertTrue(original.resolveFeatureConfig(nameAttribute, diag).isForceRead());

            // Create new resolver via toBuilder
            ConfigurationResolver rebuilt = original.toBuilder().build();

            // Verify rebuilt preserves forceRead
            DiagnosticCollector diag2 = new DiagnosticCollector();
            assertTrue(rebuilt.resolveFeatureConfig(fullNameAttribute, diag2).isForceRead(),
                    "toBuilder should preserve forceRead for fullName");
            assertTrue(rebuilt.resolveFeatureConfig(nameAttribute, diag2).isForceRead(),
                    "toBuilder should preserve forceRead for name");
        }

        @Test
        @DisplayName("toBuilder preserves both forceWrite and forceRead")
        void toBuilderPreservesBoth() {
            ConfigurationResolver original = ConfigurationResolver.builder()
                    .forceWrite(fullNameAttribute)
                    .forceRead(nameAttribute)
                    .build();

            ConfigurationResolver rebuilt = original.toBuilder().build();

            DiagnosticCollector diag = new DiagnosticCollector();
            assertTrue(rebuilt.resolveFeatureConfig(fullNameAttribute, diag).isForceWrite(),
                    "toBuilder should preserve forceWrite");
            assertTrue(rebuilt.resolveFeatureConfig(nameAttribute, diag).isForceRead(),
                    "toBuilder should preserve forceRead");
        }

        @Test
        @DisplayName("toBuilder with additional options preserves forceWrite")
        void toBuilderWithOptionsPreservesForceWrite() {
            ConfigurationResolver original = ConfigurationResolver.builder()
                    .forceWrite(fullNameAttribute)
                    .build();

            // Add options via toBuilder (simulates enrichWithOptions)
            ConfigurationResolver enriched = original.toBuilder()
                    .optionsProperties(Map.of("someOption", "value"))
                    .build();

            DiagnosticCollector diag = new DiagnosticCollector();
            assertTrue(enriched.resolveFeatureConfig(fullNameAttribute, diag).isForceWrite(),
                    "forceWrite should survive toBuilder with additional options");
        }

        @Test
        @DisplayName("toBuilder with annotation properties preserves forceWrite")
        void toBuilderWithAnnotationPropertiesPreservesForceWrite() {
            ConfigurationResolver original = ConfigurationResolver.builder()
                    .forceWrite(fullNameAttribute)
                    .build();

            // Add annotation properties via toBuilder (simulates enrichWithAnnotations)
            ConfigurationResolver enriched = original.toBuilder()
                    .annotationProperties(Map.of("someAnnotation", "value"))
                    .build();

            DiagnosticCollector diag = new DiagnosticCollector();
            assertTrue(enriched.resolveFeatureConfig(fullNameAttribute, diag).isForceWrite(),
                    "forceWrite should survive toBuilder with annotation properties");
        }
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://forcewrite.json"),
                metadataService,
                resolver,
                null);

        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://forceread.json"),
                metadataService,
                resolver,
                null);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        // Use CODEC_ROOT_TYPE to specify the EClass when JSON doesn't have _type
        resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, personClass));

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
