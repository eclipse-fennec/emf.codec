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

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TypeDiscriminatorService}.
 */
@DisplayName("TypeDiscriminatorService")
class TypeDiscriminatorServiceTest {

    private TypeDiscriminatorService service;
    private EClass personClass;
    private EClass customerClass;
    private EClass deviceClass;

    @BeforeEach
    void setUp() {
        service = new TypeDiscriminatorService();

        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");

        customerClass = EcoreFactory.eINSTANCE.createEClass();
        customerClass.setName("Customer");

        deviceClass = EcoreFactory.eINSTANCE.createEClass();
        deviceClass.setName("Device");
    }

    @Nested
    @DisplayName("getOrCreateRegistry")
    class GetOrCreateRegistry {

        @Test
        @DisplayName("creates new registry for unknown mapId")
        void createsNewRegistry() {
            TypeDiscriminatorRegistry registry = service.getOrCreateRegistry("test-map");

            assertNotNull(registry);
            assertEquals("test-map", registry.getMapId());
            assertTrue(service.hasRegistry("test-map"));
        }

        @Test
        @DisplayName("returns existing registry for known mapId")
        void returnsExistingRegistry() {
            TypeDiscriminatorRegistry first = service.getOrCreateRegistry("test-map");
            TypeDiscriminatorRegistry second = service.getOrCreateRegistry("test-map");

            assertSame(first, second);
        }

        @Test
        @DisplayName("throws on null mapId")
        void throwsOnNullMapId() {
            assertThrows(NullPointerException.class, () -> service.getOrCreateRegistry(null));
        }
    }

    @Nested
    @DisplayName("getRegistry")
    class GetRegistry {

        @Test
        @DisplayName("returns registry for known mapId")
        void returnsRegistryForKnown() {
            service.getOrCreateRegistry("test-map");
            assertNotNull(service.getRegistry("test-map"));
        }

        @Test
        @DisplayName("returns null for unknown mapId")
        void returnsNullForUnknown() {
            assertNull(service.getRegistry("unknown"));
        }

        @Test
        @DisplayName("returns null for null mapId")
        void returnsNullForNull() {
            assertNull(service.getRegistry(null));
        }
    }

    @Nested
    @DisplayName("getEClass")
    class GetEClass {

        @Test
        @DisplayName("returns EClass from specific registry")
        void returnsEClassFromSpecificRegistry() {
            service.getOrCreateRegistry("map1").register("person", personClass);
            service.getOrCreateRegistry("map2").register("customer", customerClass);

            assertEquals(personClass, service.getEClass("map1", "person"));
            assertEquals(customerClass, service.getEClass("map2", "customer"));
            assertNull(service.getEClass("map1", "customer"));
            assertNull(service.getEClass("map2", "person"));
        }

        @Test
        @DisplayName("returns null for unknown mapId")
        void returnsNullForUnknownMapId() {
            assertNull(service.getEClass("unknown", "person"));
        }
    }

    @Nested
    @DisplayName("getEClassFromAny")
    class GetEClassFromAny {

        @Test
        @DisplayName("finds EClass across all registries")
        void findsEClassAcrossAllRegistries() {
            service.getOrCreateRegistry("map1").register("person", personClass);
            service.getOrCreateRegistry("map2").register("customer", customerClass);

            assertEquals(personClass, service.getEClassFromAny("person"));
            assertEquals(customerClass, service.getEClassFromAny("customer"));
        }

        @Test
        @DisplayName("returns null for unknown discriminator")
        void returnsNullForUnknown() {
            service.getOrCreateRegistry("map1").register("person", personClass);

            assertNull(service.getEClassFromAny("unknown"));
        }

        @Test
        @DisplayName("returns null for null discriminator")
        void returnsNullForNull() {
            assertNull(service.getEClassFromAny(null));
        }
    }

    @Nested
    @DisplayName("getDiscriminatorValue")
    class GetDiscriminatorValue {

        @Test
        @DisplayName("returns discriminator from specific registry")
        void returnsDiscriminatorFromSpecificRegistry() {
            service.getOrCreateRegistry("map1").register("person", personClass);
            service.getOrCreateRegistry("map2").register("customer", customerClass);

            assertEquals("person", service.getDiscriminatorValue("map1", personClass));
            assertEquals("customer", service.getDiscriminatorValue("map2", customerClass));
            assertNull(service.getDiscriminatorValue("map1", customerClass));
        }

        @Test
        @DisplayName("returns null for unknown mapId")
        void returnsNullForUnknownMapId() {
            assertNull(service.getDiscriminatorValue("unknown", personClass));
        }
    }

    @Nested
    @DisplayName("getDiscriminatorValueFromAny")
    class GetDiscriminatorValueFromAny {

        @Test
        @DisplayName("finds discriminator across all registries")
        void findsDiscriminatorAcrossAllRegistries() {
            service.getOrCreateRegistry("map1").register("person", personClass);
            service.getOrCreateRegistry("map2").register("customer", customerClass);

            assertEquals("person", service.getDiscriminatorValueFromAny(personClass));
            assertEquals("customer", service.getDiscriminatorValueFromAny(customerClass));
        }

        @Test
        @DisplayName("returns null for unknown EClass")
        void returnsNullForUnknown() {
            service.getOrCreateRegistry("map1").register("person", personClass);

            assertNull(service.getDiscriminatorValueFromAny(deviceClass));
        }

        @Test
        @DisplayName("returns null for null EClass")
        void returnsNullForNull() {
            assertNull(service.getDiscriminatorValueFromAny(null));
        }
    }

    @Nested
    @DisplayName("multiple mapIds")
    class MultipleMapIds {

        @Test
        @DisplayName("same discriminator can map to different EClasses in different mapIds")
        void sameDiscriminatorDifferentMappings() {
            // In "api-v1", "entity" maps to Person
            service.getOrCreateRegistry("api-v1").register("entity", personClass);
            // In "api-v2", "entity" maps to Customer
            service.getOrCreateRegistry("api-v2").register("entity", customerClass);

            assertEquals(personClass, service.getEClass("api-v1", "entity"));
            assertEquals(customerClass, service.getEClass("api-v2", "entity"));
        }

        @Test
        @DisplayName("same EClass can have different discriminators in different mapIds")
        void sameEClassDifferentDiscriminators() {
            // Person is "person" in map1
            service.getOrCreateRegistry("map1").register("person", personClass);
            // Person is "user" in map2
            service.getOrCreateRegistry("map2").register("user", personClass);

            assertEquals("person", service.getDiscriminatorValue("map1", personClass));
            assertEquals("user", service.getDiscriminatorValue("map2", personClass));
        }
    }

    @Nested
    @DisplayName("getMapIds / getRegistries")
    class CollectionMethods {

        @Test
        @DisplayName("getMapIds returns all registered mapIds")
        void getMapIdsReturnsAll() {
            service.getOrCreateRegistry("map1");
            service.getOrCreateRegistry("map2");
            service.getOrCreateRegistry("map3");

            var mapIds = service.getMapIds();

            assertEquals(3, mapIds.size());
            assertTrue(mapIds.contains("map1"));
            assertTrue(mapIds.contains("map2"));
            assertTrue(mapIds.contains("map3"));
        }

        @Test
        @DisplayName("getRegistries returns all registries")
        void getRegistriesReturnsAll() {
            service.getOrCreateRegistry("map1").register("a", personClass);
            service.getOrCreateRegistry("map2").register("b", customerClass);

            var registries = service.getRegistries();

            assertEquals(2, registries.size());
        }

        @Test
        @DisplayName("getTotalMappings returns sum of all mappings")
        void getTotalMappingsReturnsSum() {
            service.getOrCreateRegistry("map1").register("a", personClass);
            service.getOrCreateRegistry("map1").register("b", customerClass);
            service.getOrCreateRegistry("map2").register("c", deviceClass);

            assertEquals(3, service.getTotalMappings());
        }
    }

    @Nested
    @DisplayName("clear")
    class Clear {

        @Test
        @DisplayName("removes all registries")
        void removesAllRegistries() {
            service.getOrCreateRegistry("map1").register("person", personClass);
            service.getOrCreateRegistry("map2").register("customer", customerClass);

            service.clear();

            assertEquals(0, service.getMapIds().size());
            assertEquals(0, service.getTotalMappings());
            assertFalse(service.hasRegistry("map1"));
            assertFalse(service.hasRegistry("map2"));
        }
    }

    @Test
    @DisplayName("toString includes registry count and total mappings")
    void toStringIncludesCounts() {
        service.getOrCreateRegistry("map1").register("person", personClass);
        service.getOrCreateRegistry("map2").register("customer", customerClass);

        String str = service.toString();

        assertTrue(str.contains("2")); // registries
        assertTrue(str.contains("2")); // totalMappings
    }

    // ========================================================================
    // registerStaticMappings
    // ========================================================================

    @Nested
    @DisplayName("registerStaticMappings")
    class RegisterStaticMappings {

        private Function<String, EClass> resolver;

        @BeforeEach
        void setUpResolver() {
            resolver = uri -> {
                if (uri.endsWith("#//Person")) return personClass;
                if (uri.endsWith("#//Customer")) return customerClass;
                if (uri.endsWith("#//Device")) return deviceClass;
                return null;
            };
        }

        @Test
        @DisplayName("registers discriminator-to-EClass mapping entries")
        void registersMappingEntries() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("person-type", "http://test#//Person");
            details.put("customer-type", "http://test#//Customer");

            service.registerStaticMappings("my-map", details, resolver);

            assertEquals(personClass, service.getEClass("my-map", "person-type"));
            assertEquals(customerClass, service.getEClass("my-map", "customer-type"));
            assertEquals(2, service.getTotalMappings());
        }

        @Test
        @DisplayName("excludes known config keys from mapping entries")
        void excludesConfigKeys() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("typeDiscriminatorPath", "info.type");
            details.put("typeDiscriminator", "my-disc");
            details.put("fallbackStrategy", "ERROR");
            details.put("fallbackEClass", "http://test#//Device");
            details.put("actual-mapping", "http://test#//Person");

            service.registerStaticMappings("my-map", details, resolver);

            // Only "actual-mapping" should be registered as a mapping entry
            assertEquals(1, service.getTotalMappings());
            assertEquals(personClass, service.getEClass("my-map", "actual-mapping"));
            // Config keys should NOT be registered as mappings
            assertNull(service.getEClass("my-map", "typeDiscriminatorPath"));
            assertNull(service.getEClass("my-map", "typeDiscriminator"));
            assertNull(service.getEClass("my-map", "fallbackStrategy"));
            assertNull(service.getEClass("my-map", "fallbackEClass"));
        }

        @Test
        @DisplayName("sets discriminatorPath on registry")
        void setsDiscriminatorPath() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("typeDiscriminatorPath", "info.sensorType");

            service.registerStaticMappings("my-map", details, resolver);

            assertEquals("info.sensorType", service.getDiscriminatorPath("my-map"));
        }

        @Test
        @DisplayName("sets fallback strategy on registry")
        void setsFallbackStrategy() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("fallbackStrategy", "ERROR");

            service.registerStaticMappings("my-map", details, resolver);

            TypeDiscriminatorRegistry registry = service.getRegistry("my-map");
            assertNotNull(registry);
            assertEquals(FallbackStrategy.ERROR, registry.getFallbackStrategy());
        }

        @Test
        @DisplayName("sets fallback EClass on registry")
        void setsFallbackEClass() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("fallbackStrategy", "FALLBACK");
            details.put("fallbackEClass", "http://test#//Device");

            service.registerStaticMappings("my-map", details, resolver);

            TypeDiscriminatorRegistry registry = service.getRegistry("my-map");
            assertNotNull(registry);
            assertEquals(FallbackStrategy.FALLBACK, registry.getFallbackStrategy());
            assertEquals("http://test#//Device", registry.getFallbackEClass());
        }

        @Test
        @DisplayName("skips entries with unresolvable EClass URIs")
        void skipsUnresolvableEntries() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("valid", "http://test#//Person");
            details.put("invalid", "http://test#//NonExistent");

            service.registerStaticMappings("my-map", details, resolver);

            assertEquals(1, service.getTotalMappings());
            assertEquals(personClass, service.getEClass("my-map", "valid"));
            assertNull(service.getEClass("my-map", "invalid"));
        }

        @Test
        @DisplayName("skips entries with empty values")
        void skipsEmptyValues() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("person-type", "http://test#//Person");
            details.put("empty-value", "");

            service.registerStaticMappings("my-map", details, resolver);

            assertEquals(1, service.getTotalMappings());
        }

        @Test
        @DisplayName("handles invalid fallbackStrategy value gracefully")
        void handlesInvalidFallbackStrategy() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("fallbackStrategy", "INVALID_VALUE");

            // Should not throw, just log warning
            assertDoesNotThrow(() -> service.registerStaticMappings("my-map", details, resolver));

            // Default SKIP should remain
            TypeDiscriminatorRegistry registry = service.getRegistry("my-map");
            assertNotNull(registry);
            assertEquals(FallbackStrategy.SKIP, registry.getFallbackStrategy());
        }
    }

    // ========================================================================
    // registerInlineMappings
    // ========================================================================

    @Nested
    @DisplayName("registerInlineMappings")
    class RegisterInlineMappings {

        private EReference testRef;
        private Function<String, EClass> resolver;

        @BeforeEach
        void setUpRef() {
            // Create a proper EReference within a package so EcoreUtil.getURI works
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("test");
            pkg.setNsURI("http://test.example/inline");
            pkg.setNsPrefix("test");

            EClass ownerClass = EcoreFactory.eINSTANCE.createEClass();
            ownerClass.setName("Owner");
            pkg.getEClassifiers().add(ownerClass);

            testRef = EcoreFactory.eINSTANCE.createEReference();
            testRef.setName("contacts");
            testRef.setEType(EcorePackage.Literals.EOBJECT);
            testRef.setContainment(true);
            ownerClass.getEStructuralFeatures().add(testRef);

            resolver = uri -> {
                if (uri.endsWith("#//Person")) return personClass;
                if (uri.endsWith("#//Customer")) return customerClass;
                return null;
            };
        }

        @Test
        @DisplayName("registers inline mapping entries scoped to EReference")
        void registersInlineMappings() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("friend", "http://test#//Person");
            details.put("client", "http://test#//Customer");

            String mapId = service.registerInlineMappings(testRef, details, resolver);

            assertNotNull(mapId);
            assertEquals(2, service.getTotalMappings());
            assertEquals(personClass, service.getEClass(mapId, "friend"));
            assertEquals(customerClass, service.getEClass(mapId, "client"));
        }

        @Test
        @DisplayName("returns EReference URI as mapId")
        void returnsReferenceUriAsMapId() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("friend", "http://test#//Person");

            String mapId = service.registerInlineMappings(testRef, details, resolver);

            // mapId should be derived from EcoreUtil.getURI(reference)
            assertNotNull(mapId);
            assertTrue(mapId.contains("contacts") || mapId.contains("Owner"),
                "mapId should be derived from the EReference URI");
        }

        @Test
        @DisplayName("excludes fallback config keys from mapping entries")
        void excludesFallbackConfigKeys() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("fallbackStrategy", "ERROR");
            details.put("fallbackEClass", "http://test#//Person");
            details.put("friend", "http://test#//Customer");

            String mapId = service.registerInlineMappings(testRef, details, resolver);

            // Only "friend" should be a mapping entry
            assertEquals(1, service.getTotalMappings());
            assertEquals(customerClass, service.getEClass(mapId, "friend"));
            assertNull(service.getEClass(mapId, "fallbackStrategy"));
        }

        @Test
        @DisplayName("sets fallback config on inline registry")
        void setsFallbackConfig() {
            Map<String, String> details = new LinkedHashMap<>();
            details.put("fallbackStrategy", "FALLBACK");
            details.put("fallbackEClass", "http://test#//Person");
            details.put("friend", "http://test#//Customer");

            String mapId = service.registerInlineMappings(testRef, details, resolver);

            TypeDiscriminatorRegistry registry = service.getRegistry(mapId);
            assertNotNull(registry);
            assertEquals(FallbackStrategy.FALLBACK, registry.getFallbackStrategy());
            assertEquals("http://test#//Person", registry.getFallbackEClass());
        }
    }

    // ========================================================================
    // resolve (with fallback delegation)
    // ========================================================================

    @Nested
    @DisplayName("resolve")
    class ResolveTests {

        private EClass fallbackClass;
        private Function<String, EClass> resolver;

        @BeforeEach
        void setUpResolve() {
            fallbackClass = EcoreFactory.eINSTANCE.createEClass();
            fallbackClass.setName("Fallback");

            resolver = uri -> {
                if (uri.endsWith("#//Fallback")) return fallbackClass;
                return null;
            };

            // Set up a registry with ERROR strategy
            TypeDiscriminatorRegistry registry = service.getOrCreateRegistry("error-map");
            registry.register("known", personClass);
            registry.setFallbackStrategy(FallbackStrategy.ERROR);

            // Set up a registry with SKIP strategy
            TypeDiscriminatorRegistry skipRegistry = service.getOrCreateRegistry("skip-map");
            skipRegistry.register("known", customerClass);
            skipRegistry.setFallbackStrategy(FallbackStrategy.SKIP);

            // Set up a registry with FALLBACK strategy
            TypeDiscriminatorRegistry fallbackRegistry = service.getOrCreateRegistry("fallback-map");
            fallbackRegistry.register("known", deviceClass);
            fallbackRegistry.setFallbackStrategy(FallbackStrategy.FALLBACK);
            fallbackRegistry.setFallbackEClass("http://test#//Fallback");
        }

        @Test
        @DisplayName("returns EClass for known discriminator")
        void returnsKnownEClass() {
            assertEquals(personClass, service.resolve("error-map", "known", resolver));
        }

        @Test
        @DisplayName("returns null for non-existent mapId")
        void returnsNullForUnknownMapId() {
            assertNull(service.resolve("non-existent", "anything", resolver));
        }

        @Test
        @DisplayName("delegates ERROR fallback to registry")
        void delegatesErrorFallback() {
            assertThrows(IllegalStateException.class,
                    () -> service.resolve("error-map", "unknown", resolver));
        }

        @Test
        @DisplayName("delegates SKIP fallback to registry")
        void delegatesSkipFallback() {
            assertNull(service.resolve("skip-map", "unknown", resolver));
        }

        @Test
        @DisplayName("delegates FALLBACK to registry")
        void delegatesFallback() {
            assertEquals(fallbackClass, service.resolve("fallback-map", "unknown", resolver));
        }
    }

    // ========================================================================
    // resolveForReference
    // ========================================================================

    @Nested
    @DisplayName("resolveForReference")
    class ResolveForReference {

        private EReference testRef;
        private Function<String, EClass> resolver;

        @BeforeEach
        void setUpRef() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("test");
            pkg.setNsURI("http://test.example/resolve-ref");
            pkg.setNsPrefix("test");

            EClass ownerClass = EcoreFactory.eINSTANCE.createEClass();
            ownerClass.setName("Container");
            pkg.getEClassifiers().add(ownerClass);

            testRef = EcoreFactory.eINSTANCE.createEReference();
            testRef.setName("items");
            testRef.setEType(EcorePackage.Literals.EOBJECT);
            testRef.setContainment(true);
            ownerClass.getEStructuralFeatures().add(testRef);

            resolver = uri -> {
                if (uri.endsWith("#//Person")) return personClass;
                return null;
            };

            // Register inline mappings for this reference
            Map<String, String> details = new LinkedHashMap<>();
            details.put("friend", "http://test#//Person");
            service.registerInlineMappings(testRef, details, resolver);
        }

        @Test
        @DisplayName("resolves inline mapping by EReference")
        void resolvesInlineMappingByReference() {
            EClass result = service.resolveForReference(testRef, "friend", resolver);

            assertEquals(personClass, result);
        }

        @Test
        @DisplayName("returns null for unknown discriminator in inline mapping")
        void returnsNullForUnknownDiscriminator() {
            EClass result = service.resolveForReference(testRef, "unknown", resolver);

            assertNull(result);
        }

        @Test
        @DisplayName("returns null for null reference")
        void returnsNullForNullReference() {
            assertNull(service.resolveForReference(null, "friend", resolver));
        }

        @Test
        @DisplayName("returns null for reference without inline mappings")
        void returnsNullForReferenceWithoutMappings() {
            // Create a different reference that has no inline mappings
            EReference otherRef = EcoreFactory.eINSTANCE.createEReference();
            otherRef.setName("other");

            assertNull(service.resolveForReference(otherRef, "friend", resolver));
        }
    }

    // ========================================================================
    // getMapIdForEClass
    // ========================================================================

    @Nested
    @DisplayName("getMapIdForEClass")
    class GetMapIdForEClass {

        @Test
        @DisplayName("returns null for null EClass")
        void returnsNullForNull() {
            assertNull(service.getMapIdForEClass(null));
        }

        @Test
        @DisplayName("returns null for EClass without typeMapping annotation")
        void returnsNullForNoAnnotation() {
            assertNull(service.getMapIdForEClass(personClass));
        }

        @Test
        @DisplayName("returns mapId from direct typeMapping annotation")
        void returnsMapIdFromDirectAnnotation() {
            // Add typeMapping/my-sensors annotation to personClass
            org.eclipse.emf.ecore.EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
            ann.setSource("http://eclipse.org/fennec/codec/typeMapping/my-sensors");
            personClass.getEAnnotations().add(ann);

            assertEquals("my-sensors", service.getMapIdForEClass(personClass));
        }

        @Test
        @DisplayName("returns mapId from supertype typeMapping annotation")
        void returnsMapIdFromSupertype() {
            // Create hierarchy: ConcreteClass extends BaseClass
            // Only BaseClass has the typeMapping annotation
            EClass baseClass = EcoreFactory.eINSTANCE.createEClass();
            baseClass.setName("BaseClass");
            org.eclipse.emf.ecore.EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
            ann.setSource("http://eclipse.org/fennec/codec/typeMapping/strict-sensors");
            baseClass.getEAnnotations().add(ann);

            EClass concreteClass = EcoreFactory.eINSTANCE.createEClass();
            concreteClass.setName("ConcreteClass");
            concreteClass.getESuperTypes().add(baseClass);

            // ConcreteClass has no annotation itself, but inherits from BaseClass
            assertEquals("strict-sensors", service.getMapIdForEClass(concreteClass));
        }

        @Test
        @DisplayName("prefers direct annotation over supertype annotation")
        void prefersDirectOverSupertype() {
            EClass baseClass = EcoreFactory.eINSTANCE.createEClass();
            baseClass.setName("BaseClass");
            org.eclipse.emf.ecore.EAnnotation baseAnn = EcoreFactory.eINSTANCE.createEAnnotation();
            baseAnn.setSource("http://eclipse.org/fennec/codec/typeMapping/base-map");
            baseClass.getEAnnotations().add(baseAnn);

            EClass concreteClass = EcoreFactory.eINSTANCE.createEClass();
            concreteClass.setName("ConcreteClass");
            concreteClass.getESuperTypes().add(baseClass);
            org.eclipse.emf.ecore.EAnnotation concreteAnn = EcoreFactory.eINSTANCE.createEAnnotation();
            concreteAnn.setSource("http://eclipse.org/fennec/codec/typeMapping/concrete-map");
            concreteClass.getEAnnotations().add(concreteAnn);

            assertEquals("concrete-map", service.getMapIdForEClass(concreteClass));
        }
    }

    // ========================================================================
    // findRegistryWithPath
    // ========================================================================

    @Nested
    @DisplayName("findRegistryWithPath")
    class FindRegistryWithPath {

        @Test
        @DisplayName("returns null when no registries exist")
        void returnsNullWhenEmpty() {
            assertNull(service.findRegistryWithPath());
        }

        @Test
        @DisplayName("returns null when no registries have a path")
        void returnsNullWhenNoPathSet() {
            service.getOrCreateRegistry("map1").register("a", personClass);
            service.getOrCreateRegistry("map2").register("b", customerClass);

            assertNull(service.findRegistryWithPath());
        }

        @Test
        @DisplayName("returns registry that has a path set")
        void returnsRegistryWithPath() {
            service.getOrCreateRegistry("no-path").register("a", personClass);

            TypeDiscriminatorRegistry withPath = service.getOrCreateRegistry("with-path");
            withPath.register("b", customerClass);
            withPath.setDiscriminatorPath("info.type");

            TypeDiscriminatorRegistry result = service.findRegistryWithPath();

            assertNotNull(result);
            assertEquals("info.type", result.getDiscriminatorPath());
        }
    }

    // ========================================================================
    // getAnyDiscriminatorPath
    // ========================================================================

    @Nested
    @DisplayName("getAnyDiscriminatorPath")
    class GetAnyDiscriminatorPath {

        @Test
        @DisplayName("returns null when no registries exist")
        void returnsNullWhenEmpty() {
            assertNull(service.getAnyDiscriminatorPath());
        }

        @Test
        @DisplayName("returns null when no registries have a path")
        void returnsNullWhenNoPathSet() {
            service.getOrCreateRegistry("map1");
            assertNull(service.getAnyDiscriminatorPath());
        }

        @Test
        @DisplayName("returns path from registry that has one")
        void returnsPathFromRegistry() {
            service.getOrCreateRegistry("map1");
            service.getOrCreateRegistry("map2").setDiscriminatorPath("_type");

            assertEquals("_type", service.getAnyDiscriminatorPath());
        }
    }
}
