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
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for featurePath-based MAPPED TypeStrategy.
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.CodecResourceFeaturePathTypeTest}.
 * </p>
 *
 * @see TypeDiscriminatorService
 */
@DisplayName("CodecResource FeaturePath-Based Type Tests")
class CodecResourceFeaturePathTypeTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-featurepath-type.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private TypeDiscriminatorService typeService;

    // EClasses
    private EClass deviceInfoClass;
    private EClass uplinkMessageClass;
    private EClass temperatureMessageClass;
    private EClass humidityMessageClass;
    private EClass simpleMessageClass;
    private EClass textMessageClass;
    private EClass binaryMessageClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(CodecResourceFeaturePathTypeTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        typeService = TypeDiscriminatorService.fromMetadataService(metadataService);

        deviceInfoClass = ecoreHelper.getEClass(testPackage, "DeviceInfo");
        uplinkMessageClass = ecoreHelper.getEClass(testPackage, "UplinkMessage");
        temperatureMessageClass = ecoreHelper.getEClass(testPackage, "TemperatureMessage");
        humidityMessageClass = ecoreHelper.getEClass(testPackage, "HumidityMessage");
        simpleMessageClass = ecoreHelper.getEClass(testPackage, "SimpleMessage");
        textMessageClass = ecoreHelper.getEClass(testPackage, "TextMessage");
        binaryMessageClass = ecoreHelper.getEClass(testPackage, "BinaryMessage");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createDeviceInfo(String deviceId, String profileName) {
        EObject info = testPackage.getEFactoryInstance().create(deviceInfoClass);
        info.eSet(deviceInfoClass.getEStructuralFeature("deviceId"), deviceId);
        info.eSet(deviceInfoClass.getEStructuralFeature("profileName"), profileName);
        return info;
    }

    private EObject createTemperatureMessage(String deviceId, String profileName, double temp, String unit) {
        EObject msg = testPackage.getEFactoryInstance().create(temperatureMessageClass);
        msg.eSet(uplinkMessageClass.getEStructuralFeature("info"), createDeviceInfo(deviceId, profileName));
        msg.eSet(uplinkMessageClass.getEStructuralFeature("timestamp"), "2025-12-28T10:00:00Z");
        msg.eSet(temperatureMessageClass.getEStructuralFeature("temperature"), temp);
        msg.eSet(temperatureMessageClass.getEStructuralFeature("unit"), unit);
        return msg;
    }

    private EObject createTextMessage(String messageType, String payload, String text) {
        EObject msg = testPackage.getEFactoryInstance().create(textMessageClass);
        msg.eSet(simpleMessageClass.getEStructuralFeature("messageType"), messageType);
        msg.eSet(simpleMessageClass.getEStructuralFeature("payload"), payload);
        msg.eSet(textMessageClass.getEStructuralFeature("text"), text);
        return msg;
    }

    private CodecResource createResource() {
        return createResource(false);
    }

    private CodecResource createResource(boolean smartCompression) {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(Map.of("smartCompression", smartCompression))
                .build();
        return new CodecResource(
                URI.createURI("test://featurepath.json"),
                metadataService,
                resolver,
                null);
    }

    // ========================================================================
    // Annotation Parsing Tests
    // ========================================================================

    @Nested
    @DisplayName("FeaturePath Annotation Parsing")
    class FeaturePathAnnotationParsing {

        @Test
        @DisplayName("parses typeKeyFeaturePath from UplinkMessage")
        void parsesTypeKeyFeaturePathFromUplinkMessage() {
            ClassMetadata metadata = metadataService.getClassMetadata(uplinkMessageClass);
            assertNotNull(metadata, "ClassMetadata should exist for UplinkMessage");

            ClassCodecAspect aspect = metadata.getAspects().stream()
                    .filter(ClassCodecAspect.class::isInstance)
                    .map(ClassCodecAspect.class::cast)
                    .findFirst()
                    .orElse(null);

            assertNotNull(aspect, "ClassCodecAspect should exist");
            assertNotNull(aspect.getTypeConfig(), "TypeConfig should exist");

            String discriminatorPath = aspect.getTypeConfig().getDiscriminatorPath();
            assertEquals("info.profileName", discriminatorPath,
                    "discriminatorPath should be 'info.profileName'");
        }

        @Test
        @DisplayName("discriminators are registered for temperature and humidity profiles")
        void discriminatorsRegisteredForProfiles() {
            assertNotNull(typeService.getEClassFromAny("temperature-profile"),
                    "temperature-profile discriminator should be registered");
            assertEquals(temperatureMessageClass, typeService.getEClassFromAny("temperature-profile"));

            assertNotNull(typeService.getEClassFromAny("humidity-profile"),
                    "humidity-profile discriminator should be registered");
            assertEquals(humidityMessageClass, typeService.getEClassFromAny("humidity-profile"));
        }

        @Test
        @DisplayName("discriminatorPath is registered at registry level")
        void discriminatorPathRegisteredAtRegistryLevel() {
            String path = typeService.getAnyDiscriminatorPath();
            assertNotNull(path, "discriminatorPath should be set from UplinkMessage's annotation");
            assertTrue(path.equals("info.profileName") || path.equals("messageType"),
                    "discriminatorPath should be 'info.profileName' or 'messageType', got: " + path);
        }

        @Test
        @DisplayName("SimpleMessage has correct discriminatorPath")
        void simpleMessageHasCorrectDiscriminatorPath() {
            ClassMetadata metadata = metadataService.getClassMetadata(simpleMessageClass);
            assertNotNull(metadata, "ClassMetadata should exist for SimpleMessage");

            ClassCodecAspect aspect = metadata.getAspects().stream()
                    .filter(ClassCodecAspect.class::isInstance)
                    .map(ClassCodecAspect.class::cast)
                    .findFirst()
                    .orElse(null);

            assertNotNull(aspect, "ClassCodecAspect should exist for SimpleMessage");
            assertNotNull(aspect.getTypeConfig(), "TypeConfig should exist for SimpleMessage");
            assertEquals("messageType", aspect.getTypeConfig().getDiscriminatorPath(),
                    "SimpleMessage's discriminatorPath should be 'messageType'");
        }

        @Test
        @DisplayName("simple message discriminators are registered")
        void simpleMessageDiscriminatorsRegistered() {
            assertNotNull(typeService.getEClassFromAny("text"),
                    "text discriminator should be registered");
            assertEquals(textMessageClass, typeService.getEClassFromAny("text"));

            assertNotNull(typeService.getEClassFromAny("binary"),
                    "binary discriminator should be registered");
            assertEquals(binaryMessageClass, typeService.getEClassFromAny("binary"));
        }
    }

    // ========================================================================
    // Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("FeaturePath Serialization")
    class FeaturePathSerialization {

        @Test
        @DisplayName("serializes nested path - discriminator from info.profileName")
        void serializesNestedPath_discriminatorFromInfoProfileName() throws IOException {
            EObject msg = createTemperatureMessage("device-001", "temperature-profile", 23.5, "celsius");

            String json = serialize(msg, true);
            System.out.println("TemperatureMessage JSON:\n" + json);

            assertFalse(json.startsWith("{\"_type\":"),
                    "Root should NOT start with _type when using featurePath");

            String nsUri = testPackage.getNsURI();
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//DeviceInfo\""),
                    "Nested DeviceInfo should have _type with full URI");

            assertTrue(json.contains("\"profileName\"") && json.contains("\"temperature-profile\""),
                    "JSON should contain profileName with discriminator value");

            assertTrue(json.contains("\"temperature\"") && json.contains("23.5"),
                    "JSON should contain temperature value");
        }

        @Test
        @DisplayName("serializes single-level path - discriminator from messageType")
        void serializesSingleLevelPath_discriminatorFromMessageType() throws IOException {
            EObject msg = createTextMessage("text", "test payload", "Hello World");

            String json = serialize(msg, true);
            System.out.println("TextMessage JSON:\n" + json);

            assertFalse(json.contains("\"_type\""),
                    "JSON should NOT contain _type field when using featurePath");

            assertTrue(json.contains("\"messageType\"") && json.contains("\"text\""),
                    "JSON should contain messageType with discriminator value");
        }
    }

    // ========================================================================
    // Deserialization Tests
    // ========================================================================

    @Nested
    @DisplayName("FeaturePath Deserialization")
    class FeaturePathDeserialization {

        @Test
        @DisplayName("deserializes nested path WITHOUT CODEC_ROOT_TYPE - type discovered from content")
        void deserializesNestedPath_withoutRootObjectHint() throws IOException {
            String json = """
                {
                    "info": {
                        "deviceId": "device-001",
                        "profileName": "temperature-profile"
                    },
                    "timestamp": "2025-12-28T10:00:00Z",
                    "temperature": 23.5,
                    "unit": "celsius"
                }
                """;

            EObject result = deserializeWithoutHint(json);

            assertNotNull(result, "Should deserialize temperature message without CODEC_ROOT_TYPE");
            assertEquals(temperatureMessageClass, result.eClass(),
                    "Should resolve to TemperatureMessage based on info.profileName");
            assertEquals(23.5, (Double) result.eGet(temperatureMessageClass.getEStructuralFeature("temperature")), 0.001);
        }

        @Test
        @DisplayName("deserializes nested path - info.profileName at beginning (with fallback hint)")
        void deserializesNestedPath_infoProfileNameAtBeginning() throws IOException {
            String json = """
                {
                    "info": {
                        "deviceId": "device-001",
                        "profileName": "temperature-profile"
                    },
                    "timestamp": "2025-12-28T10:00:00Z",
                    "temperature": 23.5,
                    "unit": "celsius"
                }
                """;

            EObject result = deserialize(json, uplinkMessageClass);

            assertNotNull(result, "Should deserialize temperature message");
            assertEquals(temperatureMessageClass, result.eClass(),
                    "Should resolve to TemperatureMessage based on info.profileName");
            assertEquals(23.5, (Double) result.eGet(temperatureMessageClass.getEStructuralFeature("temperature")), 0.001);
        }

        @Test
        @DisplayName("deserializes nested path - info.profileName at end (buffering required)")
        void deserializesNestedPath_infoProfileNameAtEnd() throws IOException {
            String json = """
                {
                    "timestamp": "2025-12-28T10:00:00Z",
                    "temperature": 23.5,
                    "unit": "celsius",
                    "info": {
                        "deviceId": "device-001",
                        "profileName": "temperature-profile"
                    }
                }
                """;

            EObject result = deserialize(json, uplinkMessageClass);

            assertNotNull(result, "Should deserialize temperature message even when discriminator is at end");
            assertEquals(temperatureMessageClass, result.eClass(),
                    "Should resolve to TemperatureMessage based on info.profileName");
        }

        @Test
        @DisplayName("deserializes single-level path")
        void deserializesSingleLevelPath() throws IOException {
            String json = """
                {
                    "messageType": "text",
                    "payload": "test payload",
                    "text": "Hello World"
                }
                """;

            EObject result = deserialize(json, simpleMessageClass);

            assertNotNull(result, "Should deserialize text message");
            assertEquals(textMessageClass, result.eClass(),
                    "Should resolve to TextMessage based on messageType");
            assertEquals("Hello World", result.eGet(textMessageClass.getEStructuralFeature("text")));
        }

        @Test
        @DisplayName("deserializes humidity message from nested path")
        void deserializesHumidityMessage() throws IOException {
            String json = """
                {
                    "info": {
                        "deviceId": "device-002",
                        "profileName": "humidity-profile"
                    },
                    "timestamp": "2025-12-28T10:00:00Z",
                    "humidity": 65.5
                }
                """;

            EObject result = deserialize(json, uplinkMessageClass);

            assertNotNull(result, "Should deserialize humidity message");
            assertEquals(humidityMessageClass, result.eClass(),
                    "Should resolve to HumidityMessage based on info.profileName");
            assertEquals(65.5, (Double) result.eGet(humidityMessageClass.getEStructuralFeature("humidity")), 0.001);
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("FeaturePath Round-Trip")
    class FeaturePathRoundTrip {

        @Test
        @DisplayName("round-trips temperature message via nested path")
        void roundTripsTemperatureMessage() throws IOException {
            EObject original = createTemperatureMessage("device-001", "temperature-profile", 23.5, "celsius");

            String json = serialize(original);
            System.out.println("Round-trip JSON:\n" + json);

            EObject loaded = deserialize(json, uplinkMessageClass);

            assertNotNull(loaded);
            assertEquals(temperatureMessageClass, loaded.eClass());
            assertEquals(23.5, (Double) loaded.eGet(temperatureMessageClass.getEStructuralFeature("temperature")), 0.001);

            EObject info = (EObject) loaded.eGet(uplinkMessageClass.getEStructuralFeature("info"));
            assertNotNull(info);
            assertEquals("device-001", info.eGet(deviceInfoClass.getEStructuralFeature("deviceId")));
            assertEquals("temperature-profile", info.eGet(deviceInfoClass.getEStructuralFeature("profileName")));
        }

        @Test
        @DisplayName("round-trips text message via single-level path")
        void roundTripsTextMessage() throws IOException {
            EObject original = createTextMessage("text", "test payload", "Hello World");

            String json = serialize(original);

            EObject loaded = deserialize(json, simpleMessageClass);

            assertNotNull(loaded);
            assertEquals(textMessageClass, loaded.eClass());
            assertEquals("text", loaded.eGet(simpleMessageClass.getEStructuralFeature("messageType")));
            assertEquals("Hello World", loaded.eGet(textMessageClass.getEStructuralFeature("text")));
        }

        @Test
        @DisplayName("round-trips temperature message with featurePath (smart compression N/A)")
        void roundTripsTemperatureMessage_withSmartCompression() throws IOException {
            EObject original = createTemperatureMessage("device-001", "temperature-profile", 23.5, "celsius");

            String json = serialize(original, true);
            System.out.println("Round-trip JSON (smart compression ON but N/A for featurePath):\n" + json);

            assertFalse(json.startsWith("{\"_type\":"),
                    "Root should NOT start with _type when using featurePath");

            String nsUri = testPackage.getNsURI();
            assertTrue(json.contains("\"_type\":\"" + nsUri + "#//DeviceInfo\""),
                    "Nested DeviceInfo should have _type with full URI");

            EObject loaded = deserialize(json, uplinkMessageClass);

            assertNotNull(loaded, "Should deserialize with featurePath");
            assertEquals(temperatureMessageClass, loaded.eClass(),
                    "Should resolve to TemperatureMessage via featurePath");
            assertEquals(23.5, (Double) loaded.eGet(temperatureMessageClass.getEStructuralFeature("temperature")), 0.001);

            EObject info = (EObject) loaded.eGet(uplinkMessageClass.getEStructuralFeature("info"));
            assertNotNull(info, "Nested info should be deserialized");
            assertEquals("device-001", info.eGet(deviceInfoClass.getEStructuralFeature("deviceId")));
            assertEquals("temperature-profile", info.eGet(deviceInfoClass.getEStructuralFeature("profileName")));
        }

        @Test
        @DisplayName("round-trips text message with smart compression (no _type in JSON)")
        void roundTripsTextMessage_withSmartCompression() throws IOException {
            EObject original = createTextMessage("text", "test payload", "Hello World");

            String json = serialize(original, true);
            System.out.println("Round-trip JSON (smart compression ON):\n" + json);

            assertFalse(json.contains("\"_type\""),
                    "JSON should NOT contain _type when using smart compression");

            EObject loaded = deserialize(json, simpleMessageClass);

            assertNotNull(loaded, "Should deserialize without _type field");
            assertEquals(textMessageClass, loaded.eClass(),
                    "Should resolve to TextMessage via featurePath");
            assertEquals("text", loaded.eGet(simpleMessageClass.getEStructuralFeature("messageType")));
            assertEquals("test payload", loaded.eGet(simpleMessageClass.getEStructuralFeature("payload")));
            assertEquals("Hello World", loaded.eGet(textMessageClass.getEStructuralFeature("text")));
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String serialize(EObject object) throws IOException {
        return serialize(object, false);
    }

    private String serialize(EObject object, boolean smartCompression) throws IOException {
        CodecResource resource = createResource(smartCompression);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootEClass) throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private EObject deserializeWithoutHint(String json) throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
