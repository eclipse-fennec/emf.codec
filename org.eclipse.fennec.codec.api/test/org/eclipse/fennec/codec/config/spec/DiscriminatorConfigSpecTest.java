/*
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
package org.eclipse.fennec.codec.config.spec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.config.DiscriminatorConfig;
import org.eclipse.fennec.codec.config.DiscriminatorConfig.FallbackStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based tests for {@link DiscriminatorConfig}.
 * <p>
 * These tests are derived directly from the codec-v2 specification documents:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/08-discriminator-mapping.md} - Discriminator Mapping specification</li>
 *   <li>{@code docs/codec-v2-spec/16-annotation-reference.md} - Annotation Reference</li>
 * </ul>
 * <p>
 * Test organization:
 * <ul>
 *   <li>Section 1: Default values</li>
 *   <li>Section 2: Validation rules</li>
 *   <li>Section 3: FallbackStrategy values</li>
 *   <li>Section 4: Type Mapping Registry configuration</li>
 *   <li>Section 5: Inline Mapping configuration</li>
 *   <li>Section 6: Merge behavior</li>
 * </ul>
 */
@DisplayName("DiscriminatorConfig Spec Tests")
class DiscriminatorConfigSpecTest {

    // ========================================================================
    // Section 1: Default Values
    // Spec reference: 08-discriminator-mapping.md section 3, 16-annotation-reference.md
    // ========================================================================

    @Nested
    @DisplayName("1. Default Values (spec section 3)")
    class DefaultValues {

        /**
         * Spec (16-annotation-reference.md): "fallbackStrategy | SKIP (default)"
         * Note: The spec clearly states SKIP is the default because it enables
         * graceful continuation to Type Strategy resolution.
         */
        @Test
        @DisplayName("1.1 fallbackStrategy defaults to SKIP")
        void fallbackStrategy_defaultsToSkip() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            assertEquals(FallbackStrategy.SKIP, config.getFallbackStrategy(),
                "Spec says: fallbackStrategy defaults to SKIP for graceful continuation");
        }

        /**
         * Spec: fallbackEClass defaults to null
         */
        @Test
        @DisplayName("1.2 fallbackEClass defaults to null")
        void fallbackEClass_defaultsToNull() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            assertNull(config.getFallbackEClass());
        }

        /**
         * Spec: typeMapId defaults to null
         */
        @Test
        @DisplayName("1.3 typeMapId defaults to null")
        void typeMapId_defaultsToNull() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            assertNull(config.getTypeMapId());
        }

        /**
         * Spec: typeDiscriminatorPath defaults to null
         */
        @Test
        @DisplayName("1.4 typeDiscriminatorPath defaults to null")
        void typeDiscriminatorPath_defaultsToNull() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            assertNull(config.getTypeDiscriminatorPath());
        }

        /**
         * Spec: typeDiscriminator defaults to null
         */
        @Test
        @DisplayName("1.5 typeDiscriminator defaults to null")
        void typeDiscriminator_defaultsToNull() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            assertNull(config.getTypeDiscriminator());
        }

        /**
         * Spec: typeMappings defaults to empty map
         */
        @Test
        @DisplayName("1.6 typeMappings defaults to empty map")
        void typeMappings_defaultsToEmptyMap() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            assertTrue(config.getTypeMappings().isEmpty());
        }

        /**
         * Spec: inlineMappings defaults to empty map
         */
        @Test
        @DisplayName("1.7 inlineMappings defaults to empty map")
        void inlineMappings_defaultsToEmptyMap() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            assertTrue(config.getInlineMappings().isEmpty());
        }

        /**
         * Default config should have no registry or inline mapping configured
         */
        @Test
        @DisplayName("1.8 defaults have no mapping configured")
        void defaults_haveNoMappingConfigured() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            assertFalse(config.hasTypeMappingRegistry());
            assertFalse(config.hasInlineMapping());
            assertFalse(config.isRegisteredWithRegistry());
        }
    }

    // ========================================================================
    // Section 2: Validation Rules
    // Spec reference: 08-discriminator-mapping.md, 16-annotation-reference.md
    // ========================================================================

    @Nested
    @DisplayName("2. Validation Rules")
    class ValidationRules {

        /**
         * Spec: typeDiscriminator requires typeMapId for distributed registration
         */
        @Test
        @DisplayName("2.1 typeDiscriminator without typeMapId produces ERROR")
        void typeDiscriminator_withoutTypeMapId_producesError() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeDiscriminator("my-discriminator")
                    // no typeMapId
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors(),
                "Expected ERROR: typeDiscriminator requires typeMapId");
        }

        /**
         * Spec: typeDiscriminator with typeMapId is valid
         */
        @Test
        @DisplayName("2.2 typeDiscriminator with typeMapId produces no ERROR")
        void typeDiscriminator_withTypeMapId_noError() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("my-registry")
                    .typeDiscriminator("my-discriminator")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
        }

        /**
         * Spec: typeMappings requires typeMapId for registry identification
         */
        @Test
        @DisplayName("2.3 typeMappings without typeMapId produces ERROR")
        void typeMappings_withoutTypeMapId_producesError() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("sensor", "http://example.org#//Sensor")
                    // no typeMapId
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors(),
                "Expected ERROR: typeMappings requires typeMapId");
        }

        /**
         * Spec: typeDiscriminatorPath requires typeMapId
         */
        @Test
        @DisplayName("2.4 typeDiscriminatorPath without typeMapId produces ERROR")
        void typeDiscriminatorPath_withoutTypeMapId_producesError() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeDiscriminatorPath("info.type")
                    // no typeMapId
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors(),
                "Expected ERROR: typeDiscriminatorPath requires typeMapId");
        }

        /**
         * Spec: typeMappings without typeDiscriminatorPath is likely a misconfiguration
         */
        @Test
        @DisplayName("2.5 typeMappings without typeDiscriminatorPath produces WARNING")
        void typeMappings_withoutDiscriminatorPath_producesWarning() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("my-registry")
                    .addTypeMapping("sensor", "http://example.org#//Sensor")
                    // no typeDiscriminatorPath
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Expected WARNING: typeMappings without typeDiscriminatorPath");
        }

        /**
         * Spec: fallbackEClass only meaningful when fallbackStrategy=FALLBACK
         */
        @Test
        @DisplayName("2.6 fallbackEClass with non-FALLBACK strategy produces WARNING")
        void fallbackEClass_withNonFallbackStrategy_producesWarning() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .fallbackStrategy(FallbackStrategy.SKIP)
                    .fallbackEClass("http://example.org#//Default")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Expected WARNING: fallbackEClass ignored when strategy is not FALLBACK");
        }

        /**
         * Spec: fallbackEClass with fallbackStrategy=FALLBACK is valid
         */
        @Test
        @DisplayName("2.7 fallbackEClass with FALLBACK strategy produces no WARNING")
        void fallbackEClass_withFallbackStrategy_noWarning() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .fallbackStrategy(FallbackStrategy.FALLBACK)
                    .fallbackEClass("http://example.org#//Default")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        /**
         * Valid complete type mapping registry configuration
         */
        @Test
        @DisplayName("2.8 Valid type mapping registry config produces no diagnostics")
        void validTypeMappingRegistry_noDiagnostics() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("lorawan-devices")
                    .typeDiscriminatorPath("info.profileName")
                    .addTypeMapping("temp-sensor", "http://example.org#//TemperatureSensor")
                    .addTypeMapping("humidity-sensor", "http://example.org#//HumiditySensor")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }
    }

    // ========================================================================
    // Section 3: FallbackStrategy Values
    // Spec reference: 08-discriminator-mapping.md section on Fallback and Error Handling
    // ========================================================================

    @Nested
    @DisplayName("3. FallbackStrategy Values")
    class FallbackStrategyValues {

        /**
         * Spec: ERROR - Fail immediately, throw exception
         */
        @Test
        @DisplayName("3.1 ERROR strategy is valid")
        void errorStrategy_isValid() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .fallbackStrategy(FallbackStrategy.ERROR)
                    .build();

            assertEquals(FallbackStrategy.ERROR, config.getFallbackStrategy());
        }

        /**
         * Spec: SKIP (default) - Log WARNING, continue to next resolution step
         */
        @Test
        @DisplayName("3.2 SKIP strategy is valid")
        void skipStrategy_isValid() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .fallbackStrategy(FallbackStrategy.SKIP)
                    .build();

            assertEquals(FallbackStrategy.SKIP, config.getFallbackStrategy());
        }

        /**
         * Spec: FALLBACK - Use fallbackEClass (MUST be set, else ERROR)
         */
        @Test
        @DisplayName("3.3 FALLBACK strategy is valid")
        void fallbackStrategy_isValid() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .fallbackStrategy(FallbackStrategy.FALLBACK)
                    .fallbackEClass("http://example.org#//Default")
                    .build();

            assertEquals(FallbackStrategy.FALLBACK, config.getFallbackStrategy());
            assertEquals("http://example.org#//Default", config.getFallbackEClass());
        }

        /**
         * All FallbackStrategy values should be valid
         */
        @Test
        @DisplayName("3.4 All FallbackStrategy values are valid")
        void allValues_areValid() {
            for (FallbackStrategy strategy : FallbackStrategy.values()) {
                DiscriminatorConfig config = DiscriminatorConfig.builder()
                        .fallbackStrategy(strategy)
                        .build();

                assertEquals(strategy, config.getFallbackStrategy());
            }
        }
    }

    // ========================================================================
    // Section 4: Type Mapping Registry Configuration
    // Spec reference: 08-discriminator-mapping.md section 4
    // ========================================================================

    @Nested
    @DisplayName("4. Type Mapping Registry (spec section 4)")
    class TypeMappingRegistry {

        /**
         * Spec section 4.1: Centralized configuration - all mappings on base class
         */
        @Test
        @DisplayName("4.1 Centralized configuration with static mappings")
        void centralizedConfiguration_staticMappings() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("lorawan-devices")
                    .typeDiscriminatorPath("info.profileName")
                    .addTypeMapping("temp-sensor", "http://example.org#//TemperatureSensor")
                    .addTypeMapping("humidity-sensor", "http://example.org#//HumiditySensor")
                    .build();

            assertEquals("lorawan-devices", config.getTypeMapId());
            assertEquals("info.profileName", config.getTypeDiscriminatorPath());
            assertEquals(2, config.getTypeMappings().size());
            assertEquals("http://example.org#//TemperatureSensor", config.getTypeMappings().get("temp-sensor"));
            assertTrue(config.hasTypeMappingRegistry());
        }

        /**
         * Spec section 4.2: Distributed configuration - concrete class self-registration
         */
        @Test
        @DisplayName("4.2 Distributed configuration with self-registration")
        void distributedConfiguration_selfRegistration() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("lorawan-devices")
                    .typeDiscriminator("temperature-profile")
                    .build();

            assertEquals("lorawan-devices", config.getTypeMapId());
            assertEquals("temperature-profile", config.getTypeDiscriminator());
            assertTrue(config.isRegisteredWithRegistry());
        }

        /**
         * Spec: typeDiscriminatorPath supports dot notation for nested paths
         */
        @Test
        @DisplayName("4.3 typeDiscriminatorPath supports nested paths")
        void discriminatorPath_supportsNestedPaths() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("my-registry")
                    .typeDiscriminatorPath("metadata.device.type")
                    .build();

            assertEquals("metadata.device.type", config.getTypeDiscriminatorPath());
        }
    }

    // ========================================================================
    // Section 5: Inline Mapping Configuration
    // Spec reference: 08-discriminator-mapping.md section 5
    // ========================================================================

    @Nested
    @DisplayName("5. Inline Mapping (spec section 5)")
    class InlineMapping {

        /**
         * Spec: Inline mappings for per-reference type resolution
         */
        @Test
        @DisplayName("5.1 Inline mappings configuration")
        void inlineMappings_configuration() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("friend", "http://example.org#//Friend")
                    .addInlineMapping("enemy", "http://example.org#//Enemy")
                    .build();

            assertEquals(2, config.getInlineMappings().size());
            assertEquals("http://example.org#//Friend", config.getInlineMappings().get("friend"));
            assertEquals("http://example.org#//Enemy", config.getInlineMappings().get("enemy"));
            assertTrue(config.hasInlineMapping());
        }

        /**
         * Spec: Inline mappings don't require typeMapId (they're independent)
         */
        @Test
        @DisplayName("5.2 Inline mappings don't require typeMapId")
        void inlineMappings_noTypeMapIdRequired() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("friend", "http://example.org#//Friend")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors(),
                "Inline mappings should not require typeMapId");
        }
    }

    // ========================================================================
    // Section 6: Merge Behavior
    // Spec reference: 16-annotation-reference.md "Configuration Resolution"
    // ========================================================================

    @Nested
    @DisplayName("6. Merge Behavior")
    class MergeBehavior {

        /**
         * Property map values should override existing config values
         */
        @Test
        @DisplayName("6.1 Property map overrides existing values")
        void propertyMap_overridesExistingValues() {
            DiscriminatorConfig base = DiscriminatorConfig.builder()
                    .typeMapId("old-registry")
                    .fallbackStrategy(FallbackStrategy.SKIP)
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.typeMapId", "new-registry");
            override.put("codec.fallbackStrategy", "ERROR");

            DiscriminatorConfig merged = base.mergeWith(override);

            assertEquals("new-registry", merged.getTypeMapId());
            assertEquals(FallbackStrategy.ERROR, merged.getFallbackStrategy());
        }

        /**
         * Mappings should be merged (override entries take precedence)
         */
        @Test
        @DisplayName("6.2 Mappings are merged with override precedence")
        void mappings_areMerged() {
            DiscriminatorConfig base = DiscriminatorConfig.builder()
                    .typeMapId("registry")
                    .addTypeMapping("sensor-a", "http://example.org#//SensorA")
                    .addTypeMapping("sensor-b", "http://example.org#//SensorB")
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.typeMappings", Map.of(
                    "sensor-b", "http://example.org#//SensorBNew",  // override
                    "sensor-c", "http://example.org#//SensorC"      // add new
            ));

            DiscriminatorConfig merged = base.mergeWith(override);

            assertEquals(3, merged.getTypeMappings().size());
            assertEquals("http://example.org#//SensorA", merged.getTypeMappings().get("sensor-a"));
            assertEquals("http://example.org#//SensorBNew", merged.getTypeMappings().get("sensor-b"));
            assertEquals("http://example.org#//SensorC", merged.getTypeMappings().get("sensor-c"));
        }

        /**
         * Empty or null source should not change config
         */
        @Test
        @DisplayName("6.3 Empty property map returns same config")
        void emptyPropertyMap_returnsSameConfig() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("registry")
                    .build();

            DiscriminatorConfig merged = config.mergeWith(new HashMap<>());
            assertSame(config, merged);

            merged = config.mergeWith(null);
            assertSame(config, merged);
        }

        /**
         * Enum values accepted as strings (case-insensitive)
         */
        @Test
        @DisplayName("6.4 Enum values accepted as strings (case-insensitive)")
        void enumValues_acceptedAsStrings() {
            DiscriminatorConfig base = DiscriminatorConfig.defaults();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.fallbackStrategy", "error");  // lowercase

            DiscriminatorConfig merged = base.mergeWith(override);

            assertEquals(FallbackStrategy.ERROR, merged.getFallbackStrategy());
        }
    }
}
