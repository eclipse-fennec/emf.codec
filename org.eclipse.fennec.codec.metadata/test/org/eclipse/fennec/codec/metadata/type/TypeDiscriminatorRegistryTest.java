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

import java.util.function.Function;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TypeDiscriminatorRegistry}.
 */
@DisplayName("TypeDiscriminatorRegistry")
class TypeDiscriminatorRegistryTest {

    private TypeDiscriminatorRegistry registry;
    private EClass personClass;
    private EClass customerClass;

    @BeforeEach
    void setUp() {
        registry = new TypeDiscriminatorRegistry("test-mapId");

        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");

        customerClass = EcoreFactory.eINSTANCE.createEClass();
        customerClass.setName("Customer");
    }

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("stores mapId")
        void storesMapId() {
            assertEquals("test-mapId", registry.getMapId());
        }

        @Test
        @DisplayName("throws on null mapId")
        void throwsOnNullMapId() {
            assertThrows(NullPointerException.class, () -> new TypeDiscriminatorRegistry(null));
        }
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("registers discriminator to EClass mapping")
        void registersMapping() {
            registry.register("person", personClass);

            assertEquals(personClass, registry.getEClass("person"));
            assertEquals("person", registry.getDiscriminatorValue(personClass));
        }

        @Test
        @DisplayName("allows multiple discriminators")
        void allowsMultipleDiscriminators() {
            registry.register("person", personClass);
            registry.register("customer", customerClass);

            assertEquals(personClass, registry.getEClass("person"));
            assertEquals(customerClass, registry.getEClass("customer"));
            assertEquals(2, registry.size());
        }

        @Test
        @DisplayName("overwrites existing mapping")
        void overwritesExisting() {
            registry.register("person", personClass);
            registry.register("person", customerClass);

            assertEquals(customerClass, registry.getEClass("person"));
            assertEquals(1, registry.size());
        }

        @Test
        @DisplayName("throws on null discriminator")
        void throwsOnNullDiscriminator() {
            assertThrows(NullPointerException.class, () -> registry.register(null, personClass));
        }

        @Test
        @DisplayName("throws on null EClass")
        void throwsOnNullEClass() {
            assertThrows(NullPointerException.class, () -> registry.register("person", null));
        }
    }

    @Nested
    @DisplayName("unregister")
    class Unregister {

        @Test
        @DisplayName("removes mapping and returns EClass")
        void removesMappingAndReturnsEClass() {
            registry.register("person", personClass);

            EClass removed = registry.unregister("person");

            assertEquals(personClass, removed);
            assertNull(registry.getEClass("person"));
            assertNull(registry.getDiscriminatorValue(personClass));
            assertEquals(0, registry.size());
        }

        @Test
        @DisplayName("returns null for unknown discriminator")
        void returnsNullForUnknown() {
            assertNull(registry.unregister("unknown"));
        }

        @Test
        @DisplayName("handles null discriminator")
        void handlesNullDiscriminator() {
            assertNull(registry.unregister(null));
        }
    }

    @Nested
    @DisplayName("getEClass")
    class GetEClass {

        @Test
        @DisplayName("returns EClass for known discriminator")
        void returnsEClassForKnown() {
            registry.register("person", personClass);
            assertEquals(personClass, registry.getEClass("person"));
        }

        @Test
        @DisplayName("returns null for unknown discriminator")
        void returnsNullForUnknown() {
            assertNull(registry.getEClass("unknown"));
        }

        @Test
        @DisplayName("returns null for null discriminator")
        void returnsNullForNull() {
            assertNull(registry.getEClass(null));
        }
    }

    @Nested
    @DisplayName("getDiscriminatorValue")
    class GetDiscriminatorValue {

        @Test
        @DisplayName("returns discriminator for known EClass")
        void returnsDiscriminatorForKnown() {
            registry.register("person", personClass);
            assertEquals("person", registry.getDiscriminatorValue(personClass));
        }

        @Test
        @DisplayName("returns null for unknown EClass")
        void returnsNullForUnknown() {
            assertNull(registry.getDiscriminatorValue(customerClass));
        }

        @Test
        @DisplayName("returns null for null EClass")
        void returnsNullForNull() {
            assertNull(registry.getDiscriminatorValue(null));
        }
    }

    @Nested
    @DisplayName("hasMapping / hasDiscriminator")
    class HasMethods {

        @Test
        @DisplayName("hasMapping returns true for registered discriminator")
        void hasMappingReturnsTrueForRegistered() {
            registry.register("person", personClass);
            assertTrue(registry.hasMapping("person"));
        }

        @Test
        @DisplayName("hasMapping returns false for unknown discriminator")
        void hasMappingReturnsFalseForUnknown() {
            assertFalse(registry.hasMapping("unknown"));
        }

        @Test
        @DisplayName("hasDiscriminator returns true for registered EClass")
        void hasDiscriminatorReturnsTrueForRegistered() {
            registry.register("person", personClass);
            assertTrue(registry.hasDiscriminator(personClass));
        }

        @Test
        @DisplayName("hasDiscriminator returns false for unknown EClass")
        void hasDiscriminatorReturnsFalseForUnknown() {
            assertFalse(registry.hasDiscriminator(customerClass));
        }
    }

    @Nested
    @DisplayName("getDiscriminatorValues")
    class GetDiscriminatorValues {

        @Test
        @DisplayName("returns all discriminator values")
        void returnsAllValues() {
            registry.register("person", personClass);
            registry.register("customer", customerClass);

            var values = registry.getDiscriminatorValues();

            assertEquals(2, values.size());
            assertTrue(values.contains("person"));
            assertTrue(values.contains("customer"));
        }

        @Test
        @DisplayName("returns unmodifiable set")
        void returnsUnmodifiableSet() {
            registry.register("person", personClass);

            var values = registry.getDiscriminatorValues();

            assertThrows(UnsupportedOperationException.class, () -> values.add("test"));
        }
    }

    @Nested
    @DisplayName("clear")
    class Clear {

        @Test
        @DisplayName("removes all mappings")
        void removesAllMappings() {
            registry.register("person", personClass);
            registry.register("customer", customerClass);

            registry.clear();

            assertEquals(0, registry.size());
            assertNull(registry.getEClass("person"));
            assertNull(registry.getEClass("customer"));
        }
    }

    @Test
    @DisplayName("toString includes mapId and size")
    void toStringIncludesMapIdAndSize() {
        registry.register("person", personClass);

        String str = registry.toString();

        assertTrue(str.contains("test-mapId"));
        assertTrue(str.contains("1"));
    }

    // ========================================================================
    // resolve() with fallback strategies
    // ========================================================================

    @Nested
    @DisplayName("resolve")
    class Resolve {

        private EClass fallbackClass;
        private Function<String, EClass> resolver;

        @BeforeEach
        void setUpResolve() {
            fallbackClass = EcoreFactory.eINSTANCE.createEClass();
            fallbackClass.setName("FallbackEntity");

            resolver = uri -> {
                if ("http://test#//FallbackEntity".equals(uri)) {
                    return fallbackClass;
                }
                return null;
            };
        }

        @Test
        @DisplayName("returns EClass for known discriminator regardless of strategy")
        void returnsEClassForKnownDiscriminator() {
            registry.register("person", personClass);
            registry.setFallbackStrategy(FallbackStrategy.ERROR);

            EClass result = registry.resolve("person", resolver);

            assertEquals(personClass, result);
        }

        @Test
        @DisplayName("SKIP strategy returns null for unknown discriminator")
        void skipReturnsNullForUnknown() {
            registry.setFallbackStrategy(FallbackStrategy.SKIP);

            EClass result = registry.resolve("unknown", resolver);

            assertNull(result);
        }

        @Test
        @DisplayName("ERROR strategy throws for unknown discriminator")
        void errorThrowsForUnknown() {
            registry.setFallbackStrategy(FallbackStrategy.ERROR);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> registry.resolve("unknown", resolver));
            assertTrue(ex.getMessage().contains("unknown"),
                "Exception message should contain the discriminator value");
            assertTrue(ex.getMessage().contains("ERROR"),
                "Exception message should mention ERROR strategy");
        }

        @Test
        @DisplayName("FALLBACK strategy resolves fallbackEClass")
        void fallbackResolvesFallbackEClass() {
            registry.setFallbackStrategy(FallbackStrategy.FALLBACK);
            registry.setFallbackEClass("http://test#//FallbackEntity");

            EClass result = registry.resolve("unknown", resolver);

            assertEquals(fallbackClass, result);
        }

        @Test
        @DisplayName("FALLBACK strategy throws when fallbackEClass is null")
        void fallbackThrowsWhenNoFallbackEClass() {
            registry.setFallbackStrategy(FallbackStrategy.FALLBACK);
            // fallbackEClass not set (null)

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> registry.resolve("unknown", resolver));
            assertTrue(ex.getMessage().contains("FALLBACK"),
                "Exception message should mention FALLBACK strategy");
            assertTrue(ex.getMessage().contains("fallbackEClass"),
                "Exception message should mention fallbackEClass");
        }

        @Test
        @DisplayName("FALLBACK strategy throws when fallbackEClass URI cannot be resolved")
        void fallbackThrowsWhenFallbackEClassUnresolvable() {
            registry.setFallbackStrategy(FallbackStrategy.FALLBACK);
            registry.setFallbackEClass("http://test#//NonExistent");

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> registry.resolve("unknown", resolver));
            assertTrue(ex.getMessage().contains("NonExistent"),
                "Exception message should contain the unresolvable URI");
        }

        @Test
        @DisplayName("default strategy is SKIP")
        void defaultStrategyIsSkip() {
            assertEquals(FallbackStrategy.SKIP, registry.getFallbackStrategy());

            // Should return null (not throw) for unknown
            assertNull(registry.resolve("unknown", resolver));
        }
    }

    // ========================================================================
    // isFeaturePath
    // ========================================================================

    @Nested
    @DisplayName("isFeaturePath")
    class IsFeaturePath {

        @Test
        @DisplayName("returns false when no path is set")
        void returnsFalseWhenNoPath() {
            assertFalse(registry.isFeaturePath());
        }

        @Test
        @DisplayName("returns false for simple field name")
        void returnsFalseForSimplePath() {
            registry.setDiscriminatorPath("_type");
            assertFalse(registry.isFeaturePath());
        }

        @Test
        @DisplayName("returns true for nested dot-separated path")
        void returnsTrueForNestedPath() {
            registry.setDiscriminatorPath("info.profileName");
            assertTrue(registry.isFeaturePath());
        }

        @Test
        @DisplayName("returns true for deeply nested path")
        void returnsTrueForDeeplyNestedPath() {
            registry.setDiscriminatorPath("device.info.sensorType");
            assertTrue(registry.isFeaturePath());
        }
    }

    // ========================================================================
    // fallback property getters/setters
    // ========================================================================

    @Nested
    @DisplayName("fallback configuration")
    class FallbackConfig {

        @Test
        @DisplayName("setFallbackStrategy rejects null")
        void setFallbackStrategyRejectsNull() {
            assertThrows(NullPointerException.class,
                    () -> registry.setFallbackStrategy(null));
        }

        @Test
        @DisplayName("setFallbackEClass accepts null")
        void setFallbackEClassAcceptsNull() {
            registry.setFallbackEClass("http://test#//Something");
            registry.setFallbackEClass(null);
            assertNull(registry.getFallbackEClass());
        }

        @Test
        @DisplayName("fallbackEClass defaults to null")
        void fallbackEClassDefaultsToNull() {
            assertNull(registry.getFallbackEClass());
        }
    }
}
