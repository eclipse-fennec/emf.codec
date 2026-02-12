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
package org.eclipse.fennec.codec.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.config.DiscriminatorConfig.FallbackStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DiscriminatorConfig} and its merge behavior.
 */
@DisplayName("DiscriminatorConfig")
class DiscriminatorConfigTest {

    @Nested
    @DisplayName("defaults()")
    class Defaults {

        @Test
        @DisplayName("returns config with spec-defined defaults")
        void returnsConfigWithSpecDefaults() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();

            assertNull(config.getTypeMapId());
            assertNull(config.getTypeDiscriminatorPath());
            assertNull(config.getTypeDiscriminator());
            assertEquals(Map.of(), config.getTypeMappings());
            assertEquals(Map.of(), config.getInlineMappings());
            assertEquals(FallbackStrategy.SKIP, config.getFallbackStrategy());  // Spec: SKIP is default
            assertNull(config.getFallbackEClass());
        }
    }

    @Nested
    @DisplayName("mergeWith()")
    class MergeWith {

        @Test
        @DisplayName("returns same instance when source is null")
        void returnsSameInstanceWhenSourceIsNull() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();

            DiscriminatorConfig result = config.mergeWith(null);

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns same instance when source is empty")
        void returnsSameInstanceWhenSourceIsEmpty() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();

            DiscriminatorConfig result = config.mergeWith(Map.of());

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns new instance when source has values")
        void returnsNewInstanceWhenSourceHasValues() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.TYPE_MAP_ID.getKey(), "myRegistry");

            DiscriminatorConfig result = config.mergeWith(source);

            assertNotSame(config, result);
        }

        @Test
        @DisplayName("overrides typeMapId from source")
        void overridesTypeMapIdFromSource() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.TYPE_MAP_ID.getKey(), "myRegistry");

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals("myRegistry", result.getTypeMapId());
        }

        @Test
        @DisplayName("overrides typeDiscriminatorPath from source")
        void overridesTypeDiscriminatorPathFromSource() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.TYPE_DISCRIMINATOR_PATH.getKey(), "info.type");

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals("info.type", result.getTypeDiscriminatorPath());
        }

        @Test
        @DisplayName("overrides typeDiscriminator from source")
        void overridesTypeDiscriminatorFromSource() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.TYPE_DISCRIMINATOR.getKey(), "person");

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals("person", result.getTypeDiscriminator());
        }

        @Test
        @DisplayName("overrides fallbackStrategy from source using enum")
        void overridesFallbackStrategyFromSourceUsingEnum() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.FALLBACK_STRATEGY.getKey(), FallbackStrategy.ERROR);

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals(FallbackStrategy.ERROR, result.getFallbackStrategy());
        }

        @Test
        @DisplayName("overrides fallbackStrategy from source using string")
        void overridesFallbackStrategyFromSourceUsingString() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.FALLBACK_STRATEGY.getKey(), "SKIP");

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals(FallbackStrategy.SKIP, result.getFallbackStrategy());
        }

        @Test
        @DisplayName("overrides fallbackEClass from source")
        void overridesFallbackEClassFromSource() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.FALLBACK_ECLASS.getKey(), "http://example.com/MyClass");

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals("http://example.com/MyClass", result.getFallbackEClass());
        }

        @Test
        @DisplayName("merges typeMappings from source")
        void mergesTypeMappingsFromSource() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .build();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.TYPE_MAPPINGS.getKey(), 
                    Map.of("type2", "http://example.com/Type2"));

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals(2, result.getTypeMappings().size());
            assertEquals("http://example.com/Type1", result.getTypeMappings().get("type1"));
            assertEquals("http://example.com/Type2", result.getTypeMappings().get("type2"));
        }

        @Test
        @DisplayName("source typeMappings override base typeMappings")
        void sourceTypeMappingsOverrideBase() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/OldType1")
                    .build();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.TYPE_MAPPINGS.getKey(), 
                    Map.of("type1", "http://example.com/NewType1"));

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals("http://example.com/NewType1", result.getTypeMappings().get("type1"));
        }

        @Test
        @DisplayName("merges inlineMappings from source")
        void mergesInlineMappingsFromSource() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("inline1", "http://example.com/Inline1")
                    .build();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.INLINE_MAPPINGS.getKey(), 
                    Map.of("inline2", "http://example.com/Inline2"));

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals(2, result.getInlineMappings().size());
            assertEquals("http://example.com/Inline1", result.getInlineMappings().get("inline1"));
            assertEquals("http://example.com/Inline2", result.getInlineMappings().get("inline2"));
        }

        @Test
        @DisplayName("source inlineMappings override base inlineMappings")
        void sourceInlineMappingsOverrideBase() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("inline1", "http://example.com/OldInline1")
                    .build();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.INLINE_MAPPINGS.getKey(), 
                    Map.of("inline1", "http://example.com/NewInline1"));

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals("http://example.com/NewInline1", result.getInlineMappings().get("inline1"));
        }

        @Test
        @DisplayName("keeps base values when not in source")
        void keepsBaseValuesWhenNotInSource() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .typeDiscriminatorPath("info.type")
                    .fallbackStrategy(FallbackStrategy.ERROR)
                    .build();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.FALLBACK_ECLASS.getKey(), "http://example.com/Fallback");

            DiscriminatorConfig result = config.mergeWith(source);

            // These should be kept from base
            assertEquals("myRegistry", result.getTypeMapId());
            assertEquals("info.type", result.getTypeDiscriminatorPath());
            assertEquals(FallbackStrategy.ERROR, result.getFallbackStrategy());
            // This should be overridden
            assertEquals("http://example.com/Fallback", result.getFallbackEClass());
        }

        @Test
        @DisplayName("handles empty maps in source")
        void handlesEmptyMapsInSource() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .build();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.TYPE_MAPPINGS.getKey(), Map.of());

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals(1, result.getTypeMappings().size());
            assertEquals("http://example.com/Type1", result.getTypeMappings().get("type1"));
        }

        @Test
        @DisplayName("merges both typeMappings and inlineMappings in single call")
        void mergesBothMappingsInSingleCall() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .addInlineMapping("inline1", "http://example.com/Inline1")
                    .build();
            Map<String, Object> source = new HashMap<>();
            source.put(ConfigProperty.TYPE_MAPPINGS.getKey(), 
                    Map.of("type2", "http://example.com/Type2"));
            source.put(ConfigProperty.INLINE_MAPPINGS.getKey(), 
                    Map.of("inline2", "http://example.com/Inline2"));

            DiscriminatorConfig result = config.mergeWith(source);

            assertEquals(2, result.getTypeMappings().size());
            assertEquals(2, result.getInlineMappings().size());
        }
    }

    @Nested
    @DisplayName("cascading merge")
    class CascadingMerge {

        @Test
        @DisplayName("supports chained merge calls")
        void supportsChainedMergeCalls() {
            Map<String, Object> source1 = new HashMap<>();
            source1.put(ConfigProperty.TYPE_MAP_ID.getKey(), "registry1");
            
            Map<String, Object> source2 = new HashMap<>();
            source2.put(ConfigProperty.TYPE_DISCRIMINATOR_PATH.getKey(), "type");
            
            Map<String, Object> source3 = new HashMap<>();
            source3.put(ConfigProperty.FALLBACK_STRATEGY.getKey(), "ERROR");
            
            Map<String, Object> source4 = new HashMap<>();
            source4.put(ConfigProperty.TYPE_MAP_ID.getKey(), "registry2");

            DiscriminatorConfig config = DiscriminatorConfig.defaults()
                    .mergeWith(source1)
                    .mergeWith(source2)
                    .mergeWith(source3)
                    .mergeWith(source4);

            // Options overrides annotation
            assertEquals("registry2", config.getTypeMapId());
            // Module setting preserved
            assertEquals("type", config.getTypeDiscriminatorPath());
            // Factory setting preserved
            assertEquals(FallbackStrategy.ERROR, config.getFallbackStrategy());
        }

        @Test
        @DisplayName("later layers override earlier layers")
        void laterLayersOverrideEarlierLayers() {
            Map<String, Object> source1 = new HashMap<>();
            source1.put(ConfigProperty.FALLBACK_STRATEGY.getKey(), "FALLBACK");
            
            Map<String, Object> source2 = new HashMap<>();
            source2.put(ConfigProperty.FALLBACK_STRATEGY.getKey(), "SKIP");
            
            Map<String, Object> source3 = new HashMap<>();
            source3.put(ConfigProperty.FALLBACK_STRATEGY.getKey(), "ERROR");

            DiscriminatorConfig config = DiscriminatorConfig.defaults()
                    .mergeWith(source1)
                    .mergeWith(source2)
                    .mergeWith(source3);

            assertEquals(FallbackStrategy.ERROR, config.getFallbackStrategy());
        }

        @Test
        @DisplayName("intermediate results are immutable")
        void intermediateResultsAreImmutable() {
            DiscriminatorConfig base = DiscriminatorConfig.defaults();
            
            Map<String, Object> source1 = new HashMap<>();
            source1.put(ConfigProperty.TYPE_MAP_ID.getKey(), "registry1");
            DiscriminatorConfig afterAnnotation = base.mergeWith(source1);
            
            Map<String, Object> source2 = new HashMap<>();
            source2.put(ConfigProperty.TYPE_MAP_ID.getKey(), "registry2");
            DiscriminatorConfig afterModule = afterAnnotation.mergeWith(source2);

            // All three should be different instances
            assertNotSame(base, afterAnnotation);
            assertNotSame(afterAnnotation, afterModule);

            // And have different values
            assertNull(base.getTypeMapId());
            assertEquals("registry1", afterAnnotation.getTypeMapId());
            assertEquals("registry2", afterModule.getTypeMapId());
        }

        @Test
        @DisplayName("cascading merges preserve mappings")
        void cascadingMergesPreserveMappings() {
            Map<String, Object> source1 = new HashMap<>();
            source1.put(ConfigProperty.TYPE_MAPPINGS.getKey(), 
                    Map.of("type1", "http://example.com/Type1"));
            
            Map<String, Object> source2 = new HashMap<>();
            source2.put(ConfigProperty.TYPE_MAPPINGS.getKey(), 
                    Map.of("type2", "http://example.com/Type2"));
            
            Map<String, Object> source3 = new HashMap<>();
            source3.put(ConfigProperty.FALLBACK_STRATEGY.getKey(), "ERROR");

            DiscriminatorConfig config = DiscriminatorConfig.defaults()
                    .mergeWith(source1)
                    .mergeWith(source2)
                    .mergeWith(source3);

            assertEquals(2, config.getTypeMappings().size());
            assertEquals("http://example.com/Type1", config.getTypeMappings().get("type1"));
            assertEquals("http://example.com/Type2", config.getTypeMappings().get("type2"));
        }
    }

    @Nested
    @DisplayName("toBuilder()")
    class ToBuilder {

        @Test
        @DisplayName("creates builder with same values")
        void createsBuilderWithSameValues() {
            DiscriminatorConfig original = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .typeDiscriminatorPath("info.type")
                    .typeDiscriminator("person")
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .addInlineMapping("inline1", "http://example.com/Inline1")
                    .fallbackStrategy(FallbackStrategy.ERROR)
                    .fallbackEClass("http://example.com/Fallback")
                    .build();

            DiscriminatorConfig copy = original.toBuilder().build();

            assertEquals(original.getTypeMapId(), copy.getTypeMapId());
            assertEquals(original.getTypeDiscriminatorPath(), copy.getTypeDiscriminatorPath());
            assertEquals(original.getTypeDiscriminator(), copy.getTypeDiscriminator());
            assertEquals(original.getTypeMappings(), copy.getTypeMappings());
            assertEquals(original.getInlineMappings(), copy.getInlineMappings());
            assertEquals(original.getFallbackStrategy(), copy.getFallbackStrategy());
            assertEquals(original.getFallbackEClass(), copy.getFallbackEClass());
        }

        @Test
        @DisplayName("toBuilder() returns independent builder")
        void toBuilderReturnsIndependentBuilder() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("registry1")
                    .build();

            DiscriminatorConfig modified = config.toBuilder()
                    .typeMapId("registry2")
                    .build();

            assertEquals("registry1", config.getTypeMapId());
            assertEquals("registry2", modified.getTypeMapId());
        }
    }

    @Nested
    @DisplayName("validate()")
    class Validate {

        @Test
        @DisplayName("returns same instance when no validation issues")
        void returnsSameInstanceWhenNoValidationIssues() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            DiscriminatorConfig result = config.validate(diagnostics);

            assertSame(config, result);
            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("ERROR when typeDiscriminator is set but typeMapId is null")
        void errorWhenTypeDiscriminatorWithoutTypeMapId() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeDiscriminator("person")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors());
            assertEquals(1, diagnostics.getErrorCount());
            assertTrue(diagnostics.getErrors().get(0).getMessage()
                    .contains("typeDiscriminator requires typeMapId"));
        }

        @Test
        @DisplayName("no error when typeDiscriminator and typeMapId are both set")
        void noErrorWhenTypeDiscriminatorWithTypeMapId() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .typeDiscriminator("person")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
        }

        @Test
        @DisplayName("ERROR when typeMappings is set but typeMapId is null")
        void errorWhenTypeMappingsWithoutTypeMapId() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors());
            assertTrue(diagnostics.getErrors().stream()
                    .anyMatch(d -> d.getMessage().contains("typeMappings requires typeMapId")));
        }

        @Test
        @DisplayName("WARNING when typeMappings is non-empty but typeDiscriminatorPath is null")
        void warningWhenTypeMappingsWithoutTypeDiscriminatorPath() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertEquals(1, diagnostics.getWarningCount());
            assertTrue(diagnostics.getWarnings().get(0).getMessage()
                    .contains("typeMappings is set but typeDiscriminatorPath is not set"));
        }

        @Test
        @DisplayName("no warning when typeMappings and typeDiscriminatorPath are both set with typeMapId")
        void noWarningWhenTypeMappingsWithTypeDiscriminatorPathAndTypeMapId() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .typeDiscriminatorPath("info.type")
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("ERROR when typeDiscriminatorPath is set but typeMapId is null")
        void errorWhenTypeDiscriminatorPathWithoutTypeMapId() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeDiscriminatorPath("info.type")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors());
            assertEquals(1, diagnostics.getErrorCount());
            assertTrue(diagnostics.getErrors().get(0).getMessage()
                    .contains("typeDiscriminatorPath requires typeMapId"));
        }

        @Test
        @DisplayName("no warning when typeMappings is empty")
        void noWarningWhenTypeMappingsIsEmpty() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("WARNING when fallbackEClass is set but fallbackStrategy is not FALLBACK")
        void warningWhenFallbackEClassWithoutFallbackStrategy() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .fallbackStrategy(FallbackStrategy.ERROR)
                    .fallbackEClass("http://example.com/Fallback")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertEquals(1, diagnostics.getWarningCount());
            assertTrue(diagnostics.getWarnings().get(0).getMessage()
                    .contains("fallbackEClass is ignored when fallbackStrategy is not FALLBACK"));
        }

        @Test
        @DisplayName("no warning when fallbackEClass is set with FALLBACK strategy")
        void noWarningWhenFallbackEClassWithFallbackStrategy() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .fallbackStrategy(FallbackStrategy.FALLBACK)
                    .fallbackEClass("http://example.com/Fallback")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("no warning when fallbackEClass is null")
        void noWarningWhenFallbackEClassIsNull() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .fallbackStrategy(FallbackStrategy.ERROR)
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("collects multiple warnings")
        void collectsMultipleWarnings() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .fallbackStrategy(FallbackStrategy.SKIP)
                    .fallbackEClass("http://example.com/Fallback")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertEquals(2, diagnostics.getWarningCount());
        }

        @Test
        @DisplayName("collects both errors and warnings")
        void collectsBothErrorsAndWarnings() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .typeDiscriminator("person")
                    .addTypeMapping("type1", "http://example.com/Type1")  // warning: no path
                    .fallbackStrategy(FallbackStrategy.ERROR)
                    .fallbackEClass("http://example.com/Fallback")  // warning: ignored when not FALLBACK
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertTrue(diagnostics.hasWarnings());
            assertEquals(2, diagnostics.getWarningCount());
        }

        @Test
        @DisplayName("collects multiple errors")
        void collectsMultipleErrors() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeDiscriminator("person")  // error: no typeMapId
                    .typeDiscriminatorPath("info.type")  // error: no typeMapId
                    .addTypeMapping("type1", "http://example.com/Type1")  // error: no typeMapId
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors());
            assertEquals(3, diagnostics.getErrorCount());
        }

        @Test
        @DisplayName("WARNING when fallbackStrategy is SKIP and fallbackEClass is set")
        void warningWhenFallbackStrategyIsSkip() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .fallbackStrategy(FallbackStrategy.SKIP)
                    .fallbackEClass("http://example.com/Fallback")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
        }
    }

    @Nested
    @DisplayName("computed properties")
    class ComputedProperties {

        @Test
        @DisplayName("hasTypeMappingRegistry() returns true when typeMapId is set")
        void hasTypeMappingRegistryWhenTypeMapIdSet() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .build();

            assertTrue(config.hasTypeMappingRegistry());
        }

        @Test
        @DisplayName("hasTypeMappingRegistry() returns true when typeMappings is non-empty")
        void hasTypeMappingRegistryWhenTypeMappingsSet() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .build();

            assertTrue(config.hasTypeMappingRegistry());
        }

        @Test
        @DisplayName("hasTypeMappingRegistry() returns true when typeDiscriminatorPath is set")
        void hasTypeMappingRegistryWhenTypeDiscriminatorPathSet() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeDiscriminatorPath("info.type")
                    .build();

            assertTrue(config.hasTypeMappingRegistry());
        }

        @Test
        @DisplayName("hasTypeMappingRegistry() returns false when none are set")
        void hasTypeMappingRegistryReturnsFalseWhenNoneSet() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();

            assertFalse(config.hasTypeMappingRegistry());
        }

        @Test
        @DisplayName("hasTypeMappingRegistry() returns true when multiple are set")
        void hasTypeMappingRegistryWhenMultipleSet() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .typeDiscriminatorPath("info.type")
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .build();

            assertTrue(config.hasTypeMappingRegistry());
        }

        @Test
        @DisplayName("hasInlineMapping() returns true when inlineMappings is non-empty")
        void hasInlineMappingWhenInlineMappingsSet() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("inline1", "http://example.com/Inline1")
                    .build();

            assertTrue(config.hasInlineMapping());
        }

        @Test
        @DisplayName("hasInlineMapping() returns false when inlineMappings is empty")
        void hasInlineMappingReturnsFalseWhenEmpty() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();

            assertFalse(config.hasInlineMapping());
        }

        @Test
        @DisplayName("isRegisteredWithRegistry() returns true when typeMapId and typeDiscriminator are set")
        void isRegisteredWithRegistryWhenBothSet() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .typeDiscriminator("person")
                    .build();

            assertTrue(config.isRegisteredWithRegistry());
        }

        @Test
        @DisplayName("isRegisteredWithRegistry() returns false when typeMapId is null")
        void isRegisteredWithRegistryReturnsFalseWhenTypeMapIdNull() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeDiscriminator("person")
                    .build();

            assertFalse(config.isRegisteredWithRegistry());
        }

        @Test
        @DisplayName("isRegisteredWithRegistry() returns false when typeDiscriminator is null")
        void isRegisteredWithRegistryReturnsFalseWhenTypeDiscriminatorNull() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .typeMapId("myRegistry")
                    .build();

            assertFalse(config.isRegisteredWithRegistry());
        }

        @Test
        @DisplayName("isRegisteredWithRegistry() returns false when both are null")
        void isRegisteredWithRegistryReturnsFalseWhenBothNull() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();

            assertFalse(config.isRegisteredWithRegistry());
        }
    }

    @Nested
    @DisplayName("builder helpers")
    class BuilderHelpers {

        @Test
        @DisplayName("addTypeMapping() adds entry to typeMappings")
        void addTypeMappingAddsEntry() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .build();

            assertEquals("http://example.com/Type1", config.getTypeMappings().get("type1"));
        }

        @Test
        @DisplayName("addTypeMapping() can be called multiple times")
        void addTypeMappingMultipleTimes() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .addTypeMapping("type2", "http://example.com/Type2")
                    .addTypeMapping("type3", "http://example.com/Type3")
                    .build();

            assertEquals(3, config.getTypeMappings().size());
            assertEquals("http://example.com/Type1", config.getTypeMappings().get("type1"));
            assertEquals("http://example.com/Type2", config.getTypeMappings().get("type2"));
            assertEquals("http://example.com/Type3", config.getTypeMappings().get("type3"));
        }

        @Test
        @DisplayName("addTypeMapping() returns builder for chaining")
        void addTypeMappingReturnsBuilder() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .addTypeMapping("type2", "http://example.com/Type2")
                    .typeMapId("myRegistry")
                    .build();

            assertEquals("myRegistry", config.getTypeMapId());
            assertEquals(2, config.getTypeMappings().size());
        }

        @Test
        @DisplayName("addInlineMapping() adds entry to inlineMappings")
        void addInlineMappingAddsEntry() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("inline1", "http://example.com/Inline1")
                    .build();

            assertEquals("http://example.com/Inline1", config.getInlineMappings().get("inline1"));
        }

        @Test
        @DisplayName("addInlineMapping() can be called multiple times")
        void addInlineMappingMultipleTimes() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("inline1", "http://example.com/Inline1")
                    .addInlineMapping("inline2", "http://example.com/Inline2")
                    .addInlineMapping("inline3", "http://example.com/Inline3")
                    .build();

            assertEquals(3, config.getInlineMappings().size());
            assertEquals("http://example.com/Inline1", config.getInlineMappings().get("inline1"));
            assertEquals("http://example.com/Inline2", config.getInlineMappings().get("inline2"));
            assertEquals("http://example.com/Inline3", config.getInlineMappings().get("inline3"));
        }

        @Test
        @DisplayName("addInlineMapping() returns builder for chaining")
        void addInlineMappingReturnsBuilder() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("inline1", "http://example.com/Inline1")
                    .addInlineMapping("inline2", "http://example.com/Inline2")
                    .fallbackStrategy(FallbackStrategy.SKIP)
                    .build();

            assertEquals(FallbackStrategy.SKIP, config.getFallbackStrategy());
            assertEquals(2, config.getInlineMappings().size());
        }

        @Test
        @DisplayName("addTypeMapping() overwrites existing entry with same key")
        void addTypeMappingOverwritesExisting() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Old")
                    .addTypeMapping("type1", "http://example.com/New")
                    .build();

            assertEquals(1, config.getTypeMappings().size());
            assertEquals("http://example.com/New", config.getTypeMappings().get("type1"));
        }

        @Test
        @DisplayName("addInlineMapping() overwrites existing entry with same key")
        void addInlineMappingOverwritesExisting() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("inline1", "http://example.com/Old")
                    .addInlineMapping("inline1", "http://example.com/New")
                    .build();

            assertEquals(1, config.getInlineMappings().size());
            assertEquals("http://example.com/New", config.getInlineMappings().get("inline1"));
        }
    }

    @Nested
    @DisplayName("immutability")
    class Immutability {

        @Test
        @DisplayName("typeMappings map is unmodifiable")
        void typeMappingsMapIsUnmodifiable() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addTypeMapping("type1", "http://example.com/Type1")
                    .build();

            assertThrows(
                    UnsupportedOperationException.class,
                    () -> config.getTypeMappings().put("type2", "http://example.com/Type2"));
        }

        @Test
        @DisplayName("inlineMappings map is unmodifiable")
        void inlineMappingsMapIsUnmodifiable() {
            DiscriminatorConfig config = DiscriminatorConfig.builder()
                    .addInlineMapping("inline1", "http://example.com/Inline1")
                    .build();

            assertThrows(
                    UnsupportedOperationException.class,
                    () -> config.getInlineMappings().put("inline2", "http://example.com/Inline2"));
        }

        @Test
        @DisplayName("empty defaults typeMappings is unmodifiable")
        void emptyDefaultsTypeMappingsIsUnmodifiable() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();

            assertThrows(
                    UnsupportedOperationException.class,
                    () -> config.getTypeMappings().put("type1", "http://example.com/Type1"));
        }

        @Test
        @DisplayName("empty defaults inlineMappings is unmodifiable")
        void emptyDefaultsInlineMappingsIsUnmodifiable() {
            DiscriminatorConfig config = DiscriminatorConfig.defaults();

            assertThrows(
                    UnsupportedOperationException.class,
                    () -> config.getInlineMappings().put("inline1", "http://example.com/Inline1"));
        }
    }
}
