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
package org.eclipse.fennec.codec.metadata.type;

import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.metadata.provider.CodecAspectProvider;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.service.MetadataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for TypeDiscriminator functionality with MetadataService.
 * <p>
 * Tests the complete flow from EAnnotations → MetadataService → TypeDiscriminatorService,
 * including dynamic registration and unregistration of EPackages.
 * </p>
 */
@DisplayName("TypeDiscriminator Integration")
class TypeDiscriminatorIntegrationTest {

    private MetadataWhiteboard metadataService;
    private CodecAspectProvider codecAspectProvider;

    @SuppressWarnings("restriction")
	@BeforeEach
    void setUp() {
        metadataService = new MetadataServiceImpl();
        codecAspectProvider = new CodecAspectProvider();
        metadataService.registerAspectProvider(codecAspectProvider);
    }

    // ========================================================================
    // Helper methods to create EPackages programmatically
    // ========================================================================

    /**
     * Creates an EPackage with a single EClass that has discriminator annotations.
     * Uses dedicated typeMapping/{mapId} annotation source.
     */
    private EPackage createPackageWithDiscriminator(String nsURI, String className,
            String mapId, String discriminatorValue, String discriminatorPath) {

        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("test");
        pkg.setNsURI(nsURI);
        pkg.setNsPrefix("test");

        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName(className);

        // Add typeMapping/{mapId} annotation with discriminator config
        EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
        ann.setSource(TYPE_MAPPING_SOURCE_PREFIX + mapId);
        ann.getDetails().put(KEY_TYPE_DISCRIMINATOR, discriminatorValue);
        if (discriminatorPath != null) {
            ann.getDetails().put(KEY_TYPE_DISCRIMINATOR_PATH, discriminatorPath);
        }
        eClass.getEAnnotations().add(ann);

        pkg.getEClassifiers().add(eClass);
        return pkg;
    }

    /**
     * Creates an abstract base class with discriminatorPath (defines the mapping context).
     * Uses dedicated typeMapping/{mapId} annotation source.
     */
    private EClass createBaseClassWithPath(EPackage pkg, String className, String mapId, String path) {
        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName(className);
        eClass.setAbstract(true);

        EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
        ann.setSource(TYPE_MAPPING_SOURCE_PREFIX + mapId);
        ann.getDetails().put(KEY_TYPE_DISCRIMINATOR_PATH, path);
        eClass.getEAnnotations().add(ann);

        pkg.getEClassifiers().add(eClass);
        return eClass;
    }

    /**
     * Creates a concrete class with discriminator value (extends base class).
     * Uses dedicated typeMapping/{mapId} annotation source.
     */
    private EClass createConcreteClass(EPackage pkg, String className, String mapId,
            String discriminatorValue, EClass... superTypes) {
        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName(className);

        for (EClass superType : superTypes) {
            eClass.getESuperTypes().add(superType);
        }

        EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
        ann.setSource(TYPE_MAPPING_SOURCE_PREFIX + mapId);
        ann.getDetails().put(KEY_TYPE_DISCRIMINATOR, discriminatorValue);
        eClass.getEAnnotations().add(ann);

        pkg.getEClassifiers().add(eClass);
        return eClass;
    }

    // ========================================================================
    // Static Mapping Tests (from annotations)
    // ========================================================================

    @Nested
    @DisplayName("Static Mapping from Annotations")
    class StaticMappingTests {

        @Test
        @DisplayName("registers discriminator from single class annotation")
        void registersFromSingleClass() {
            // Create package with discriminator-annotated class
            EPackage pkg = createPackageWithDiscriminator(
                "http://test.example/static1",
                "TemperatureSensor",
                "iot-sensors",
                "temp-sensor",
                "info.sensorType"
            );

            // Register package
            metadataService.registerPackage(pkg);

            // Build TypeDiscriminatorService from metadata
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Verify mapping exists
            EClass resolved = service.getEClass("iot-sensors", "temp-sensor");
            assertNotNull(resolved, "Should resolve temp-sensor to EClass");
            assertEquals("TemperatureSensor", resolved.getName());

            // Verify reverse mapping
            String discriminator = service.getDiscriminatorValue("iot-sensors", resolved);
            assertEquals("temp-sensor", discriminator);

            // Verify discriminator path
            assertEquals("info.sensorType", service.getDiscriminatorPath("iot-sensors"));
        }

        @Test
        @DisplayName("registers multiple classes in same mapId")
        void registersMultipleClassesSameMapId() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("sensors");
            pkg.setNsURI("http://test.example/sensors");
            pkg.setNsPrefix("sensors");

            // Create base class with path
            EClass baseClass = createBaseClassWithPath(pkg, "Sensor", "iot-sensors", "info.sensorType");

            // Create concrete classes
            EClass tempSensor = createConcreteClass(pkg, "TemperatureSensor", "iot-sensors", "temp-sensor", baseClass);
            EClass humiditySensor = createConcreteClass(pkg, "HumiditySensor", "iot-sensors", "humidity-sensor", baseClass);
            EClass pressureSensor = createConcreteClass(pkg, "PressureSensor", "iot-sensors", "pressure-sensor", baseClass);

            metadataService.registerPackage(pkg);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Verify all mappings
            assertEquals(tempSensor, service.getEClass("iot-sensors", "temp-sensor"));
            assertEquals(humiditySensor, service.getEClass("iot-sensors", "humidity-sensor"));
            assertEquals(pressureSensor, service.getEClass("iot-sensors", "pressure-sensor"));

            // Verify registry has all 3 mappings
            TypeDiscriminatorRegistry registry = service.getRegistry("iot-sensors");
            assertNotNull(registry);
            assertEquals(3, registry.size());
        }

        @Test
        @DisplayName("isolates mappings by mapId")
        void isolatesMappingsByMapId() {
            // Create two packages with same discriminator value but different mapIds
            EPackage pkg1 = createPackageWithDiscriminator(
                "http://test.example/api-v1",
                "PersonV1",
                "api-v1",
                "entity",
                "_type"
            );
            EPackage pkg2 = createPackageWithDiscriminator(
                "http://test.example/api-v2",
                "PersonV2",
                "api-v2",
                "entity",  // Same discriminator value!
                "_type"
            );

            metadataService.registerPackage(pkg1);
            metadataService.registerPackage(pkg2);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Same discriminator value maps to different classes in different contexts
            EClass v1Class = service.getEClass("api-v1", "entity");
            EClass v2Class = service.getEClass("api-v2", "entity");

            assertNotNull(v1Class);
            assertNotNull(v2Class);
            assertNotEquals(v1Class, v2Class);
            assertEquals("PersonV1", v1Class.getName());
            assertEquals("PersonV2", v2Class.getName());
        }

        @Test
        @DisplayName("inherits discriminatorPath from supertype")
        void inheritsDiscriminatorPathFromSupertype() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("devices");
            pkg.setNsURI("http://test.example/devices");
            pkg.setNsPrefix("devices");

            // Base class defines the path
            EClass baseClass = createBaseClassWithPath(pkg, "Device", "lorawan", "deviceInfo.profileName");

            // Concrete class has discriminator but NO path (should inherit)
            EClass concreteClass = EcoreFactory.eINSTANCE.createEClass();
            concreteClass.setName("DraginoSensor");
            concreteClass.getESuperTypes().add(baseClass);

            EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
            ann.setSource(TYPE_MAPPING_SOURCE_PREFIX + "lorawan");
            ann.getDetails().put(KEY_TYPE_DISCRIMINATOR, "Dragino_LSE01");
            // Note: NO discriminatorPath here - should inherit from base
            concreteClass.getEAnnotations().add(ann);
            pkg.getEClassifiers().add(concreteClass);

            metadataService.registerPackage(pkg);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Verify mapping works
            assertEquals(concreteClass, service.getEClass("lorawan", "Dragino_LSE01"));

            // Verify path was inherited from base class
            assertEquals("deviceInfo.profileName", service.getDiscriminatorPath("lorawan"));
        }
    }

    // ========================================================================
    // Dynamic Registration Tests
    // ========================================================================

    @Nested
    @DisplayName("Dynamic Registration")
    class DynamicRegistrationTests {

        @Test
        @DisplayName("adds mapping when new package is registered")
        void addsMappingOnPackageRegister() {
            // Start with empty service
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);
            assertEquals(0, service.getTotalMappings());

            // Register a package with discriminator
            EPackage pkg = createPackageWithDiscriminator(
                "http://test.example/dynamic1",
                "NewDevice",
                "dynamic-registry",
                "new-device",
                "type"
            );
            metadataService.registerPackage(pkg);

            // Rebuild service (simulate what would happen with listener/notification)
            service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Verify mapping was added
            assertEquals(1, service.getTotalMappings());
            assertTrue(service.hasRegistry("dynamic-registry"));
            EClass resolved = service.getEClass("dynamic-registry", "new-device");
            assertNotNull(resolved);
            assertEquals("NewDevice", resolved.getName());
        }

        @Test
        @DisplayName("adds multiple classes from same package")
        void addsMultipleClassesFromSamePackage() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("multi");
            pkg.setNsURI("http://test.example/multi");
            pkg.setNsPrefix("multi");

            // Add three classes with discriminators
            createConcreteClass(pkg, "ClassA", "multi-map", "a");
            createConcreteClass(pkg, "ClassB", "multi-map", "b");
            createConcreteClass(pkg, "ClassC", "multi-map", "c");

            metadataService.registerPackage(pkg);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // All three should be mapped
            assertEquals(3, service.getTotalMappings());
            assertNotNull(service.getEClass("multi-map", "a"));
            assertNotNull(service.getEClass("multi-map", "b"));
            assertNotNull(service.getEClass("multi-map", "c"));
        }

        @Test
        @DisplayName("extends existing registry when package uses same mapId")
        void extendsExistingRegistry() {
            // First package
            EPackage pkg1 = createPackageWithDiscriminator(
                "http://test.example/ext1",
                "ExistingClass",
                "shared-registry",
                "existing",
                "type"
            );
            metadataService.registerPackage(pkg1);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);
            assertEquals(1, service.getTotalMappings());

            // Second package with same mapId
            EPackage pkg2 = createPackageWithDiscriminator(
                "http://test.example/ext2",
                "NewClass",
                "shared-registry",  // Same mapId
                "new",
                null  // Will use existing path
            );
            metadataService.registerPackage(pkg2);
            service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Both should be in same registry
            assertEquals(2, service.getTotalMappings());
            assertEquals(1, service.getMapIds().size());
            assertTrue(service.hasRegistry("shared-registry"));

            TypeDiscriminatorRegistry registry = service.getRegistry("shared-registry");
            assertEquals(2, registry.size());
            assertNotNull(registry.getEClass("existing"));
            assertNotNull(registry.getEClass("new"));
        }
    }

    // ========================================================================
    // Dynamic Unregistration Tests
    // ========================================================================

    @Nested
    @DisplayName("Dynamic Unregistration")
    class DynamicUnregistrationTests {

        @Test
        @DisplayName("removes mapping when package is unregistered")
        void removesMappingOnPackageUnregister() {
            // Register package
            EPackage pkg = createPackageWithDiscriminator(
                "http://test.example/unregister1",
                "ToBeRemoved",
                "removal-test",
                "to-remove",
                "type"
            );
            metadataService.registerPackage(pkg);

            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);
            assertEquals(1, service.getTotalMappings());
            assertNotNull(service.getEClass("removal-test", "to-remove"));

            // Unregister package
            metadataService.unregisterPackage(pkg);

            // Rebuild service
            service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Mapping should be gone
            assertEquals(0, service.getTotalMappings());
            assertNull(service.getEClass("removal-test", "to-remove"));
        }

        @Test
        @DisplayName("removes only specific package mappings, keeps others")
        void removesOnlySpecificPackageMappings() {
            // Register two packages with same mapId
            EPackage pkg1 = createPackageWithDiscriminator(
                "http://test.example/partial1",
                "KeepMe",
                "partial-removal",
                "keep",
                "type"
            );
            EPackage pkg2 = createPackageWithDiscriminator(
                "http://test.example/partial2",
                "RemoveMe",
                "partial-removal",
                "remove",
                null
            );

            metadataService.registerPackage(pkg1);
            metadataService.registerPackage(pkg2);

            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);
            assertEquals(2, service.getTotalMappings());

            // Unregister only pkg2
            metadataService.unregisterPackage(pkg2);
            service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Only pkg1's mapping should remain
            assertEquals(1, service.getTotalMappings());
            assertNotNull(service.getEClass("partial-removal", "keep"));
            assertNull(service.getEClass("partial-removal", "remove"));
        }

        @Test
        @DisplayName("handles unregister of non-existent package gracefully")
        void handlesNonExistentPackageUnregister() {
            EPackage phantom = EcoreFactory.eINSTANCE.createEPackage();
            phantom.setNsURI("http://phantom.example/doesnotexist");

            // Should not throw
            assertDoesNotThrow(() -> metadataService.unregisterPackage(phantom));
        }

        @Test
        @DisplayName("can re-register package after unregistration")
        void canReRegisterAfterUnregistration() {
            EPackage pkg = createPackageWithDiscriminator(
                "http://test.example/reregister",
                "ReRegisterable",
                "reregister-test",
                "reregister-value",
                "type"
            );

            // Register
            metadataService.registerPackage(pkg);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);
            assertNotNull(service.getEClass("reregister-test", "reregister-value"));

            // Unregister
            metadataService.unregisterPackage(pkg);
            service = TypeDiscriminatorService.fromMetadataService(metadataService);
            assertNull(service.getEClass("reregister-test", "reregister-value"));

            // Re-register
            metadataService.registerPackage(pkg);
            service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Should be back
            assertNotNull(service.getEClass("reregister-test", "reregister-value"));
            assertEquals("ReRegisterable", service.getEClass("reregister-test", "reregister-value").getName());
        }
    }

    // ========================================================================
    // Integration with TypeDiscriminatorService.unregisterPackage()
    // ========================================================================

    @Nested
    @DisplayName("TypeDiscriminatorService Direct Unregister")
    class DirectUnregisterTests {

        @Test
        @DisplayName("unregisterPackage removes mappings from service")
        void unregisterPackageRemovesMappings() {
            EPackage pkg = createPackageWithDiscriminator(
                "http://test.example/direct-unregister",
                "DirectUnregister",
                "direct-test",
                "direct-value",
                "type"
            );

            PackageMetadata pkgMetadata = metadataService.registerPackage(pkg);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            assertEquals(1, service.getTotalMappings());

            // Use direct unregister on service
            service.unregisterPackage(pkgMetadata);

            // Mapping should be removed from service (without rebuilding)
            assertEquals(0, service.getTotalMappings());
            assertNull(service.getEClass("direct-test", "direct-value"));
        }

        @Test
        @DisplayName("unregisterClass removes single class mapping")
        void unregisterClassRemovesSingleMapping() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("multi");
            pkg.setNsURI("http://test.example/class-unregister");
            pkg.setNsPrefix("multi");

            createConcreteClass(pkg, "KeepMe", "class-test", "keep");
            createConcreteClass(pkg, "RemoveMe", "class-test", "remove");

            PackageMetadata pkgMetadata = metadataService.registerPackage(pkg);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            assertEquals(2, service.getTotalMappings());

            // Find the ClassMetadata for "RemoveMe"
            var classMetadata = pkgMetadata.getClasses().stream()
                .filter(cm -> "RemoveMe".equals(cm.getName()))
                .findFirst()
                .orElseThrow();

            // Unregister just that class
            service.unregisterClass(classMetadata);

            // Only RemoveMe should be gone
            assertEquals(1, service.getTotalMappings());
            assertNotNull(service.getEClass("class-test", "keep"));
            assertNull(service.getEClass("class-test", "remove"));
        }
    }

    // ========================================================================
    // Edge Cases
    // ========================================================================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("handles class without discriminator annotation")
        void handlesClassWithoutDiscriminator() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("nodiscriminator");
            pkg.setNsURI("http://test.example/nodiscriminator");
            pkg.setNsPrefix("nd");

            EClass eClass = EcoreFactory.eINSTANCE.createEClass();
            eClass.setName("NoDiscriminator");
            // No codec annotation at all
            pkg.getEClassifiers().add(eClass);

            metadataService.registerPackage(pkg);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            assertEquals(0, service.getTotalMappings());
        }

        @Test
        @DisplayName("handles class with typeMapping source but no discriminator value")
        void handlesMapIdWithoutDiscriminator() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("mapidonly");
            pkg.setNsURI("http://test.example/mapidonly");
            pkg.setNsPrefix("mo");

            EClass eClass = EcoreFactory.eINSTANCE.createEClass();
            eClass.setName("MapIdOnly");

            // typeMapping source present but no typeDiscriminator detail
            EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
            ann.setSource(TYPE_MAPPING_SOURCE_PREFIX + "orphan-mapid");
            // No typeDiscriminator!
            eClass.getEAnnotations().add(ann);
            pkg.getEClassifiers().add(eClass);

            metadataService.registerPackage(pkg);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // Should not create a mapping
            assertEquals(0, service.getTotalMappings());
        }

        @Test
        @DisplayName("discriminator without typeMapping source is not registered")
        void discriminatorWithoutTypeMappingSourceNotRegistered() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("nomap");
            pkg.setNsURI("http://test.example/nomap");
            pkg.setNsPrefix("nm");

            EClass eClass = EcoreFactory.eINSTANCE.createEClass();
            eClass.setName("NoMapClass");

            // Only codec annotation, no typeMapping/{mapId} source
            // discriminator value without a typeMapping source should not register
            EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
            ann.setSource(CODEC_SOURCE);
            eClass.getEAnnotations().add(ann);
            pkg.getEClassifiers().add(eClass);

            metadataService.registerPackage(pkg);
            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // No typeMapping source means no mapId, so no registration
            assertEquals(0, service.getTotalMappings());
        }

        @Test
        @DisplayName("getEClassFromAny searches across all registries")
        void getEClassFromAnySearchesAllRegistries() {
            EPackage pkg1 = createPackageWithDiscriminator("http://test1", "Class1", "map1", "disc1", null);
            EPackage pkg2 = createPackageWithDiscriminator("http://test2", "Class2", "map2", "disc2", null);
            EPackage pkg3 = createPackageWithDiscriminator("http://test3", "Class3", "map3", "disc3", null);

            metadataService.registerPackage(pkg1);
            metadataService.registerPackage(pkg2);
            metadataService.registerPackage(pkg3);

            TypeDiscriminatorService service = TypeDiscriminatorService.fromMetadataService(metadataService);

            // getEClassFromAny should find any of them
            assertNotNull(service.getEClassFromAny("disc1"));
            assertNotNull(service.getEClassFromAny("disc2"));
            assertNotNull(service.getEClassFromAny("disc3"));
            assertNull(service.getEClassFromAny("unknown"));
        }
    }
}
