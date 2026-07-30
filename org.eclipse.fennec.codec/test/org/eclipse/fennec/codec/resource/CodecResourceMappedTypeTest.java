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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.provider.CodecAspectProvider;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.model.metadata.ClassMetadata;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for MAPPED TypeStrategy integration.
 * <p>
 * These tests verify that:
 * <ul>
 *   <li>Discriminator values from EAnnotations are correctly parsed</li>
 *   <li>Serialization outputs discriminator values (e.g., "temp-sensor")</li>
 *   <li>Deserialization resolves discriminator values back to correct EClasses</li>
 *   <li>Polymorphic containers work with MAPPED strategy</li>
 * </ul>
 * </p>
 *
 * @see TypeDiscriminatorService
 */
@DisplayName("CodecResource MAPPED TypeStrategy Tests")
class CodecResourceMappedTypeTest {

    private static final String TEST_ECORE = "test-mapped-type.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private TypeDiscriminatorService typeService;

    // EClasses
    private EClass temperatureSensorClass;
    private EClass humiditySensorClass;
    private EClass gpsTrackerClass;
    private EClass deviceContainerClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore("/org/eclipse/fennec/codec/resource/" + TEST_ECORE, CodecResourceMappedTypeTest.class);

        // Register package in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service with CodecAspectProvider
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Create type discriminator service from metadata
        typeService = TypeDiscriminatorService.fromMetadataService(metadataService);

        // Load EClasses
        temperatureSensorClass = EcoreHelper.getEClass(testPackage, "TemperatureSensor");
        humiditySensorClass = EcoreHelper.getEClass(testPackage, "HumiditySensor");
        gpsTrackerClass = EcoreHelper.getEClass(testPackage, "GPSTracker");
        deviceContainerClass = EcoreHelper.getEClass(testPackage, "DeviceContainer");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createTemperatureSensor() {
        return testPackage.getEFactoryInstance().create(temperatureSensorClass);
    }

    private EObject createHumiditySensor() {
        return testPackage.getEFactoryInstance().create(humiditySensorClass);
    }

    private EObject createGPSTracker() {
        return testPackage.getEFactoryInstance().create(gpsTrackerClass);
    }

    private EObject createDeviceContainer() {
        return testPackage.getEFactoryInstance().create(deviceContainerClass);
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://mapped.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    // ========================================================================
    // Aspect Parsing Tests - verify discriminator annotations are parsed
    // ========================================================================

    @Nested
    @DisplayName("Discriminator Annotation Parsing")
    class DiscriminatorAnnotationParsing {

        @Test
        @DisplayName("parses discriminator values from annotations")
        void parsesDiscriminatorValuesFromAnnotations() {
            // Check TemperatureSensor
            ClassMetadata tempMeta = metadataService.getClassMetadata(temperatureSensorClass).orElseThrow();
            ClassCodecAspect tempAspect = getCodecAspect(tempMeta);
            assertEquals("temp-sensor", tempAspect.getDiscriminatorValue());

            // Check HumiditySensor
            ClassMetadata humidMeta = metadataService.getClassMetadata(humiditySensorClass).orElseThrow();
            ClassCodecAspect humidAspect = getCodecAspect(humidMeta);
            assertEquals("humidity-sensor", humidAspect.getDiscriminatorValue());

            // Check GPSTracker
            ClassMetadata gpsMeta = metadataService.getClassMetadata(gpsTrackerClass).orElseThrow();
            ClassCodecAspect gpsAspect = getCodecAspect(gpsMeta);
            assertEquals("gps-tracker", gpsAspect.getDiscriminatorValue());
        }

        private ClassCodecAspect getCodecAspect(ClassMetadata metadata) {
            return CodecAspectProvider.codecAspect(metadata.getAspects(), ClassCodecAspect.class);
        }
    }

    // ========================================================================
    // TypeDiscriminatorService Tests
    // ========================================================================

    @Nested
    @DisplayName("TypeDiscriminatorService Integration")
    class TypeDiscriminatorServiceIntegration {

        @Test
        @DisplayName("TypeDiscriminatorService is populated from MetadataService")
        void typeServicePopulatedFromMetadata() {
            assertTrue(typeService.getTotalMappings() >= 3,
                    "TypeDiscriminatorService should have at least 3 mappings");
        }

        @Test
        @DisplayName("resolves EClass from discriminator value")
        void resolvesEClassFromDiscriminator() {
            EClass resolved = typeService.getEClassFromAny("temp-sensor");
            assertNotNull(resolved, "Should resolve temp-sensor discriminator");
            assertEquals(temperatureSensorClass, resolved);

            resolved = typeService.getEClassFromAny("humidity-sensor");
            assertNotNull(resolved, "Should resolve humidity-sensor discriminator");
            assertEquals(humiditySensorClass, resolved);

            resolved = typeService.getEClassFromAny("gps-tracker");
            assertNotNull(resolved, "Should resolve gps-tracker discriminator");
            assertEquals(gpsTrackerClass, resolved);
        }

        @Test
        @DisplayName("resolves discriminator value from EClass")
        void resolvesDiscriminatorFromEClass() {
            String discriminator = typeService.getDiscriminatorValueFromAny(temperatureSensorClass);
            assertEquals("temp-sensor", discriminator);

            discriminator = typeService.getDiscriminatorValueFromAny(humiditySensorClass);
            assertEquals("humidity-sensor", discriminator);

            discriminator = typeService.getDiscriminatorValueFromAny(gpsTrackerClass);
            assertEquals("gps-tracker", discriminator);
        }
    }

    // ========================================================================
    // Serialization Tests - verify MAPPED strategy outputs discriminator
    // ========================================================================

    @Nested
    @DisplayName("MAPPED Serialization")
    class MappedSerialization {

        @Test
        @DisplayName("serializes TemperatureSensor with discriminator value")
        void serializesWithDiscriminatorValue() throws IOException {
            EObject sensor = createTemperatureSensor();
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("deviceId"), "device-001");
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("timestamp"), "2025-12-17T10:00:00Z");
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("temperature"), 25.5);
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("unit"), "celsius");

            String json = serialize(sensor);
            System.out.println("TemperatureSensor JSON:\n" + json);

            // Should use discriminator value "temp-sensor" instead of URI
            assertTrue(json.contains("\"temp-sensor\""),
                    "JSON should contain discriminator value 'temp-sensor'");
            // Should NOT contain full URI
            assertFalse(json.contains("http://test.example.org"),
                    "JSON should NOT contain package URI");
        }

        @Test
        @DisplayName("serializes HumiditySensor with discriminator value")
        void serializesHumiditySensorWithDiscriminator() throws IOException {
            EObject sensor = createHumiditySensor();
            sensor.eSet(humiditySensorClass.getEStructuralFeature("deviceId"), "device-002");
            sensor.eSet(humiditySensorClass.getEStructuralFeature("humidity"), 65.0);

            String json = serialize(sensor);
            System.out.println("HumiditySensor JSON:\n" + json);

            assertTrue(json.contains("\"humidity-sensor\""),
                    "JSON should contain discriminator value 'humidity-sensor'");
        }

        @Test
        @DisplayName("serializes GPSTracker with discriminator value")
        void serializesGPSTrackerWithDiscriminator() throws IOException {
            EObject tracker = createGPSTracker();
            tracker.eSet(gpsTrackerClass.getEStructuralFeature("deviceId"), "device-003");
            tracker.eSet(gpsTrackerClass.getEStructuralFeature("latitude"), 52.520008);
            tracker.eSet(gpsTrackerClass.getEStructuralFeature("longitude"), 13.404954);

            String json = serialize(tracker);
            System.out.println("GPSTracker JSON:\n" + json);

            assertTrue(json.contains("\"gps-tracker\""),
                    "JSON should contain discriminator value 'gps-tracker'");
        }
    }

    // ========================================================================
    // Deserialization Tests - verify discriminator values resolve to EClasses
    // ========================================================================

    @Nested
    @DisplayName("MAPPED Deserialization")
    class MappedDeserialization {

        @Test
        @DisplayName("deserializes TemperatureSensor from discriminator value")
        void deserializesFromDiscriminatorValue() throws IOException {
            String json = """
                {
                    "_type": "temp-sensor",
                    "deviceId": "device-001",
                    "timestamp": "2025-12-17T10:00:00Z",
                    "temperature": 25.5,
                    "unit": "celsius"
                }
                """;

            EObject result = deserialize(json, temperatureSensorClass);

            assertNotNull(result);
            assertEquals(temperatureSensorClass, result.eClass());
            assertEquals("device-001", result.eGet(temperatureSensorClass.getEStructuralFeature("deviceId")));
            assertEquals(25.5, (Double) result.eGet(temperatureSensorClass.getEStructuralFeature("temperature")), 0.001);
        }

        @Test
        @DisplayName("deserializes HumiditySensor from discriminator value")
        void deserializesHumiditySensorFromDiscriminator() throws IOException {
            String json = """
                {
                    "_type": "humidity-sensor",
                    "deviceId": "device-002",
                    "humidity": 65.0
                }
                """;

            EObject result = deserialize(json, humiditySensorClass);

            assertNotNull(result);
            assertEquals(humiditySensorClass, result.eClass());
            assertEquals(65.0, (Double) result.eGet(humiditySensorClass.getEStructuralFeature("humidity")), 0.001);
        }
    }

    // ========================================================================
    // Round-trip Tests
    // ========================================================================

    @Nested
    @DisplayName("MAPPED Round-trip")
    class MappedRoundTrip {

        @Test
        @DisplayName("round-trips TemperatureSensor with MAPPED strategy")
        void roundTripsTemperatureSensor() throws IOException {
            EObject sensor = createTemperatureSensor();
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("deviceId"), "sensor-123");
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("timestamp"), "2025-12-17T12:00:00Z");
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("temperature"), 22.5);
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("unit"), "celsius");

            // Serialize
            String json = serialize(sensor);
            System.out.println("Round-trip JSON:\n" + json);

            // Deserialize
            EObject loaded = deserialize(json, temperatureSensorClass);

            // Verify
            assertNotNull(loaded);
            assertEquals(temperatureSensorClass, loaded.eClass());
            assertEquals("sensor-123", loaded.eGet(temperatureSensorClass.getEStructuralFeature("deviceId")));
            assertEquals("2025-12-17T12:00:00Z", loaded.eGet(temperatureSensorClass.getEStructuralFeature("timestamp")));
            assertEquals(22.5, (Double) loaded.eGet(temperatureSensorClass.getEStructuralFeature("temperature")), 0.001);
            assertEquals("celsius", loaded.eGet(temperatureSensorClass.getEStructuralFeature("unit")));
        }

        @Test
        @DisplayName("round-trips GPSTracker with MAPPED strategy")
        void roundTripsGPSTracker() throws IOException {
            EObject tracker = createGPSTracker();
            tracker.eSet(gpsTrackerClass.getEStructuralFeature("deviceId"), "gps-456");
            tracker.eSet(gpsTrackerClass.getEStructuralFeature("latitude"), 48.8566);
            tracker.eSet(gpsTrackerClass.getEStructuralFeature("longitude"), 2.3522);

            // Serialize
            String json = serialize(tracker);

            // Deserialize
            EObject loaded = deserialize(json, gpsTrackerClass);

            // Verify
            assertNotNull(loaded);
            assertEquals(gpsTrackerClass, loaded.eClass());
            assertEquals("gps-456", loaded.eGet(gpsTrackerClass.getEStructuralFeature("deviceId")));
            assertEquals(48.8566, (Double) loaded.eGet(gpsTrackerClass.getEStructuralFeature("latitude")), 0.0001);
            assertEquals(2.3522, (Double) loaded.eGet(gpsTrackerClass.getEStructuralFeature("longitude")), 0.0001);
        }
    }

    // ========================================================================
    // Polymorphic Container Tests
    // ========================================================================

    @Nested
    @DisplayName("Polymorphic Container with MAPPED")
    class PolymorphicContainerTests {

        @Test
        @DisplayName("serializes container with mixed device types")
        @SuppressWarnings("unchecked")
        void serializesContainerWithMixedDevices() throws IOException {
            EObject container = createDeviceContainer();
            container.eSet(deviceContainerClass.getEStructuralFeature("name"), "IoT Hub");

            EObject tempSensor = createTemperatureSensor();
            tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("deviceId"), "temp-1");
            tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("temperature"), 20.0);

            EObject humiditySensor = createHumiditySensor();
            humiditySensor.eSet(humiditySensorClass.getEStructuralFeature("deviceId"), "humid-1");
            humiditySensor.eSet(humiditySensorClass.getEStructuralFeature("humidity"), 55.0);

            EObject gpsTracker = createGPSTracker();
            gpsTracker.eSet(gpsTrackerClass.getEStructuralFeature("deviceId"), "gps-1");
            gpsTracker.eSet(gpsTrackerClass.getEStructuralFeature("latitude"), 40.7128);
            gpsTracker.eSet(gpsTrackerClass.getEStructuralFeature("longitude"), -74.0060);

            EReference devicesRef = (EReference) deviceContainerClass.getEStructuralFeature("devices");
            List<EObject> devices = (List<EObject>) container.eGet(devicesRef);
            devices.add(tempSensor);
            devices.add(humiditySensor);
            devices.add(gpsTracker);

            String json = serialize(container);
            System.out.println("Container JSON:\n" + json);

            // All three discriminator values should appear
            assertTrue(json.contains("\"temp-sensor\""), "Should contain temp-sensor discriminator");
            assertTrue(json.contains("\"humidity-sensor\""), "Should contain humidity-sensor discriminator");
            assertTrue(json.contains("\"gps-tracker\""), "Should contain gps-tracker discriminator");
        }

        @Test
        @DisplayName("round-trips container with mixed device types")
        @SuppressWarnings("unchecked")
        void roundTripsContainerWithMixedDevices() throws IOException {
            EObject container = createDeviceContainer();
            container.eSet(deviceContainerClass.getEStructuralFeature("name"), "Test Hub");

            EObject tempSensor = createTemperatureSensor();
            tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("deviceId"), "temp-rt");
            tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("temperature"), 18.5);
            tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("unit"), "celsius");

            EObject gpsTracker = createGPSTracker();
            gpsTracker.eSet(gpsTrackerClass.getEStructuralFeature("deviceId"), "gps-rt");
            gpsTracker.eSet(gpsTrackerClass.getEStructuralFeature("latitude"), 51.5074);
            gpsTracker.eSet(gpsTrackerClass.getEStructuralFeature("longitude"), -0.1278);

            EReference devicesRef = (EReference) deviceContainerClass.getEStructuralFeature("devices");
            List<EObject> devices = (List<EObject>) container.eGet(devicesRef);
            devices.add(tempSensor);
            devices.add(gpsTracker);

            // Serialize
            String json = serialize(container);
            System.out.println("Round-trip container JSON:\n" + json);

            // Deserialize
            EObject loaded = deserialize(json, deviceContainerClass);

            // Verify container
            assertNotNull(loaded);
            assertEquals(deviceContainerClass, loaded.eClass());
            assertEquals("Test Hub", loaded.eGet(deviceContainerClass.getEStructuralFeature("name")));

            // Verify devices
            List<EObject> loadedDevices = (List<EObject>) loaded.eGet(devicesRef);
            assertEquals(2, loadedDevices.size());

            // First device should be TemperatureSensor
            EObject firstDevice = loadedDevices.get(0);
            assertEquals(temperatureSensorClass, firstDevice.eClass());
            assertEquals("temp-rt", firstDevice.eGet(temperatureSensorClass.getEStructuralFeature("deviceId")));
            assertEquals(18.5, (Double) firstDevice.eGet(temperatureSensorClass.getEStructuralFeature("temperature")), 0.001);

            // Second device should be GPSTracker
            EObject secondDevice = loadedDevices.get(1);
            assertEquals(gpsTrackerClass, secondDevice.eClass());
            assertEquals("gps-rt", secondDevice.eGet(gpsTrackerClass.getEStructuralFeature("deviceId")));
            assertEquals(51.5074, (Double) secondDevice.eGet(gpsTrackerClass.getEStructuralFeature("latitude")), 0.0001);
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String serialize(EObject object) throws IOException {
        CodecResource resource = createResource();
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
}
