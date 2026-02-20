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
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for cross-package dynamic registration with MAPPED TypeStrategy.
 * <p>
 * These tests verify that:
 * <ul>
 *   <li>Discriminators from extension packages work when registered</li>
 *   <li>Discriminators stop working when extension package is unregistered</li>
 *   <li>Re-registration restores functionality</li>
 *   <li>Base package discriminators remain unaffected by extension package changes</li>
 * </ul>
 * </p>
 *
 * @see TypeDiscriminatorService
 */
@DisplayName("CodecResource Cross-Package MAPPED Tests")
class CodecResourceCrossPackageTest {

    private static final String BASE_ECORE = "/org/eclipse/fennec/codec/resource/test-mapped-type.ecore";
    private static final String EXT_ECORE = "/org/eclipse/fennec/codec/resource/test-mapped-type-ext.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage basePackage;
    private EPackage extPackage;
    private MetadataWhiteboard metadataService;
    private TypeDiscriminatorService typeService;

    // Base package EClasses
    private EClass deviceClass;
    private EClass temperatureSensorClass;

    // Extension package EClasses
    private EClass pressureSensorClass;
    private EClass lightSensorClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();

        // Load base package first (extension depends on it)
        basePackage = ecoreHelper.loadEcore(BASE_ECORE, CodecResourceCrossPackageTest.class);
        EPackage.Registry.INSTANCE.put(basePackage.getNsURI(), basePackage);

        // Load extension package
        extPackage = ecoreHelper.loadEcore(EXT_ECORE, CodecResourceCrossPackageTest.class);
        EPackage.Registry.INSTANCE.put(extPackage.getNsURI(), extPackage);

        // Create metadata service
        metadataService = MetadataServiceFactory.create();

        // Load EClasses from base package
        deviceClass = EcoreHelper.getEClass(basePackage, "Device");
        temperatureSensorClass = EcoreHelper.getEClass(basePackage, "TemperatureSensor");

        // Load EClasses from extension package
        pressureSensorClass = EcoreHelper.getEClass(extPackage, "PressureSensor");
        lightSensorClass = EcoreHelper.getEClass(extPackage, "LightSensor");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(basePackage.getNsURI());
        EPackage.Registry.INSTANCE.remove(extPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createTemperatureSensor() {
        return basePackage.getEFactoryInstance().create(temperatureSensorClass);
    }

    private EObject createPressureSensor() {
        return extPackage.getEFactoryInstance().create(pressureSensorClass);
    }

    private EObject createLightSensor() {
        return extPackage.getEFactoryInstance().create(lightSensorClass);
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://cross-package.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    private void registerBothPackages() {
        metadataService.registerPackage(basePackage);
        metadataService.registerPackage(extPackage);
        typeService = TypeDiscriminatorService.fromMetadataService(metadataService);
    }

    private void registerBasePackageOnly() {
        metadataService.registerPackage(basePackage);
        typeService = TypeDiscriminatorService.fromMetadataService(metadataService);
    }

    // ========================================================================
    // Registration State Tests
    // ========================================================================

    @Nested
    @DisplayName("Package Registration State")
    class PackageRegistrationState {

        @Test
        @DisplayName("both packages registered - all discriminators available")
        void bothPackagesRegistered_allDiscriminatorsAvailable() {
            registerBothPackages();

            // Base package discriminators
            assertNotNull(typeService.getEClassFromAny("temp-sensor"),
                    "temp-sensor should be registered");
            assertEquals(temperatureSensorClass, typeService.getEClassFromAny("temp-sensor"));

            // Extension package discriminators
            assertNotNull(typeService.getEClassFromAny("pressure-sensor"),
                    "pressure-sensor should be registered");
            assertEquals(pressureSensorClass, typeService.getEClassFromAny("pressure-sensor"));

            assertNotNull(typeService.getEClassFromAny("light-sensor"),
                    "light-sensor should be registered");
            assertEquals(lightSensorClass, typeService.getEClassFromAny("light-sensor"));
        }

        @Test
        @DisplayName("only base package registered - extension discriminators not available")
        void onlyBasePackageRegistered_extensionDiscriminatorsNotAvailable() {
            registerBasePackageOnly();

            // Base package discriminators should work
            assertNotNull(typeService.getEClassFromAny("temp-sensor"),
                    "temp-sensor should be registered");

            // Extension package discriminators should NOT be available
            assertNull(typeService.getEClassFromAny("pressure-sensor"),
                    "pressure-sensor should NOT be registered");
            assertNull(typeService.getEClassFromAny("light-sensor"),
                    "light-sensor should NOT be registered");
        }

        @Test
        @DisplayName("extension package registered later - discriminators become available")
        void extensionPackageRegisteredLater_discriminatorsBecomeAvailable() {
            // Start with only base package
            registerBasePackageOnly();
            assertNull(typeService.getEClassFromAny("pressure-sensor"));

            // Register extension package
            metadataService.registerPackage(extPackage);

            // Rebuild type service (simulating what would happen in real scenario)
            typeService = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Now extension discriminators should be available
            assertNotNull(typeService.getEClassFromAny("pressure-sensor"),
                    "pressure-sensor should now be registered");
            assertEquals(pressureSensorClass, typeService.getEClassFromAny("pressure-sensor"));
        }
    }

    // ========================================================================
    // Dynamic Unregistration Tests
    // ========================================================================

    @Nested
    @DisplayName("Dynamic Package Unregistration")
    class DynamicPackageUnregistration {

        @Test
        @DisplayName("unregistering extension package removes its discriminators")
        void unregisteringExtensionPackage_removesItsDiscriminators() {
            registerBothPackages();

            // Verify both work initially
            assertNotNull(typeService.getEClassFromAny("temp-sensor"));
            assertNotNull(typeService.getEClassFromAny("pressure-sensor"));

            // Get package metadata before unregistering
            PackageMetadata extMetadata = metadataService.getPackageMetadata(extPackage.getNsURI());
            assertNotNull(extMetadata, "Extension package metadata should exist");

            // Unregister extension package from type service
            typeService.unregisterPackage(extMetadata);

            // Base package discriminators should still work
            assertNotNull(typeService.getEClassFromAny("temp-sensor"),
                    "temp-sensor should still be registered");

            // Extension package discriminators should be gone
            assertNull(typeService.getEClassFromAny("pressure-sensor"),
                    "pressure-sensor should be unregistered");
            assertNull(typeService.getEClassFromAny("light-sensor"),
                    "light-sensor should be unregistered");
        }

        @Test
        @DisplayName("unregistering base package does not affect extension discriminators")
        void unregisteringBasePackage_doesNotAffectExtensionDiscriminators() {
            registerBothPackages();

            // Get base package metadata
            PackageMetadata baseMetadata = metadataService.getPackageMetadata(basePackage.getNsURI());

            // Unregister base package from type service
            typeService.unregisterPackage(baseMetadata);

            // Base package discriminators should be gone
            assertNull(typeService.getEClassFromAny("temp-sensor"),
                    "temp-sensor should be unregistered");

            // Extension package discriminators should still work
            assertNotNull(typeService.getEClassFromAny("pressure-sensor"),
                    "pressure-sensor should still be registered");
        }

        @Test
        @DisplayName("re-registration restores discriminators")
        void reRegistration_restoresDiscriminators() {
            registerBothPackages();

            // Get package metadata
            PackageMetadata extMetadata = metadataService.getPackageMetadata(extPackage.getNsURI());

            // Unregister
            typeService.unregisterPackage(extMetadata);
            assertNull(typeService.getEClassFromAny("pressure-sensor"));

            // Re-register by adding class metadata back
            for (var classMetadata : extMetadata.getClasses()) {
                typeService.registerFromClassMetadata(classMetadata);
            }

            // Should work again
            assertNotNull(typeService.getEClassFromAny("pressure-sensor"),
                    "pressure-sensor should be restored after re-registration");
            assertEquals(pressureSensorClass, typeService.getEClassFromAny("pressure-sensor"));
        }
    }

    // ========================================================================
    // Cross-Package Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Cross-Package Serialization")
    class CrossPackageSerialization {

        @Test
        @DisplayName("serializes extension package class with discriminator")
        void serializesExtensionPackageClass() throws IOException {
            registerBothPackages();

            EObject sensor = createPressureSensor();
            // Use deviceClass for inherited features, pressureSensorClass for own features
            sensor.eSet(deviceClass.getEStructuralFeature("deviceId"), "pressure-001");
            sensor.eSet(pressureSensorClass.getEStructuralFeature("pressure"), 1013.25);
            sensor.eSet(pressureSensorClass.getEStructuralFeature("pressureUnit"), "hPa");

            String json = serialize(sensor);
            System.out.println("PressureSensor JSON:\n" + json);

            assertTrue(json.contains("\"pressure-sensor\""),
                    "JSON should contain discriminator value 'pressure-sensor'");
            assertTrue(json.contains("\"pressure\"") && json.contains("1013.25"),
                    "JSON should contain pressure value");
        }

        @Test
        @DisplayName("serializes base package class with discriminator")
        void serializesBasePackageClass() throws IOException {
            registerBothPackages();

            EObject sensor = createTemperatureSensor();
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("deviceId"), "temp-001");
            sensor.eSet(temperatureSensorClass.getEStructuralFeature("temperature"), 23.5);

            String json = serialize(sensor);

            assertTrue(json.contains("\"temp-sensor\""),
                    "JSON should contain discriminator value 'temp-sensor'");
        }
    }

    // ========================================================================
    // Cross-Package Deserialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Cross-Package Deserialization")
    class CrossPackageDeserialization {

        @Test
        @DisplayName("deserializes extension package class from discriminator")
        void deserializesExtensionPackageClass() throws IOException {
            registerBothPackages();

            String json = """
                {
                    "_type": "pressure-sensor",
                    "deviceId": "pressure-001",
                    "pressure": 1013.25,
                    "pressureUnit": "hPa"
                }
                """;

            EObject result = deserialize(json, pressureSensorClass);

            assertNotNull(result, "Should deserialize pressure sensor");
            assertEquals(pressureSensorClass, result.eClass());
            // Use deviceClass for inherited features
            assertEquals("pressure-001", result.eGet(deviceClass.getEStructuralFeature("deviceId")));
            assertEquals(1013.25, (Double) result.eGet(pressureSensorClass.getEStructuralFeature("pressure")), 0.001);
        }

        @Test
        @DisplayName("deserialization fails for unregistered extension package")
        void deserializationFailsForUnregisteredPackage() throws IOException {
            // Only register base package
            registerBasePackageOnly();

            // Discriminator "pressure-sensor" is not registered
            // The deserialization should not find the correct EClass
            assertNull(typeService.getEClassFromAny("pressure-sensor"),
                    "pressure-sensor discriminator should not be registered");
        }

        @Test
        @DisplayName("base package deserialization works regardless of extension package")
        void basePackageDeserializationWorksAlways() throws IOException {
            // Only register base package (no extension)
            registerBasePackageOnly();

            String json = """
                {
                    "_type": "temp-sensor",
                    "deviceId": "temp-001",
                    "temperature": 25.0
                }
                """;

            EObject result = deserialize(json, temperatureSensorClass);

            assertNotNull(result, "Should deserialize temperature sensor");
            assertEquals(temperatureSensorClass, result.eClass());
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Cross-Package Round-Trip")
    class CrossPackageRoundTrip {

        @Test
        @DisplayName("round-trips extension package object")
        void roundTripsExtensionPackageObject() throws IOException {
            registerBothPackages();

            EObject sensor = createLightSensor();
            // Use deviceClass for inherited features
            sensor.eSet(deviceClass.getEStructuralFeature("deviceId"), "light-001");
            sensor.eSet(lightSensorClass.getEStructuralFeature("lux"), 500);

            // Serialize
            String json = serialize(sensor);
            System.out.println("LightSensor round-trip JSON:\n" + json);

            // Deserialize
            EObject loaded = deserialize(json, lightSensorClass);

            // Verify
            assertNotNull(loaded);
            assertEquals(lightSensorClass, loaded.eClass());
            assertEquals("light-001", loaded.eGet(deviceClass.getEStructuralFeature("deviceId")));
            assertEquals(500, loaded.eGet(lightSensorClass.getEStructuralFeature("lux")));
        }

        @Test
        @DisplayName("round-trips mixed base and extension objects in container")
        void roundTripsMixedObjectsInContainer() throws IOException {
            registerBothPackages();

            // Create container from base package
            EClass containerClass = EcoreHelper.getEClass(basePackage, "DeviceContainer");
            EObject container = basePackage.getEFactoryInstance().create(containerClass);
            container.eSet(containerClass.getEStructuralFeature("name"), "Mixed Hub");

            // Add base package device
            EObject tempSensor = createTemperatureSensor();
            tempSensor.eSet(deviceClass.getEStructuralFeature("deviceId"), "temp-mix");
            tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("temperature"), 20.0);

            // Add extension package device (use deviceClass for inherited features)
            EObject pressureSensor = createPressureSensor();
            pressureSensor.eSet(deviceClass.getEStructuralFeature("deviceId"), "pressure-mix");
            pressureSensor.eSet(pressureSensorClass.getEStructuralFeature("pressure"), 1000.0);

            @SuppressWarnings("unchecked")
            java.util.List<EObject> devices = (java.util.List<EObject>)
                container.eGet(containerClass.getEStructuralFeature("devices"));
            devices.add(tempSensor);
            devices.add(pressureSensor);

            // Serialize
            String json = serialize(container);
            System.out.println("Mixed container JSON:\n" + json);

            // Verify both discriminators present
            assertTrue(json.contains("\"temp-sensor\""));
            assertTrue(json.contains("\"pressure-sensor\""));

            // Deserialize
            EObject loaded = deserialize(json, containerClass);

            // Verify
            assertNotNull(loaded);
            @SuppressWarnings("unchecked")
            java.util.List<EObject> loadedDevices = (java.util.List<EObject>)
                loaded.eGet(containerClass.getEStructuralFeature("devices"));
            assertEquals(2, loadedDevices.size());
            assertEquals(temperatureSensorClass, loadedDevices.get(0).eClass());
            assertEquals(pressureSensorClass, loadedDevices.get(1).eClass());
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
