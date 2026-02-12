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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.SuperTypeSelection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SuperTypeConfig} and its merge behavior.
 */
@DisplayName("SuperTypeConfig")
class SuperTypeConfigTest {

    @Nested
    @DisplayName("defaults()")
    class Defaults {

        @Test
        @DisplayName("returns config with spec-defined defaults")
        void returnsConfigWithSpecDefaults() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            assertFalse(config.isSerialize());
            assertEquals(SuperTypeSelection.ALL, config.getStrategy());
            assertNull(config.getFormat());  // inherits from typeFormat
            assertTrue(config.isAsArray());
            assertEquals(",", config.getSeparator());
            assertNull(config.getSuperTypeKey());  // format-dependent default
            assertNull(config.getValueReaderName());
            assertNull(config.getValueWriterName());
            // Note: schemaKey removed - supertype inherits from TypeConfig
            // Note: nameKey removed - superTypeKey has format-dependent default
        }
    }

    @Nested
    @DisplayName("getEffectiveSuperTypeKey()")
    class GetEffectiveSuperTypeKey {

        @Test
        @DisplayName("returns _supertype for PLAIN format when key not set")
        void returnsUnderscoreForPlainFormat() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            assertEquals("_supertype", config.getEffectiveSuperTypeKey(SerializationFormat.PLAIN));
        }

        @Test
        @DisplayName("returns supertype for STRUCTURED format when key not set")
        void returnsNoUnderscoreForStructuredFormat() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            assertEquals("supertype", config.getEffectiveSuperTypeKey(SerializationFormat.STRUCTURED));
        }

        @Test
        @DisplayName("returns explicit key regardless of format")
        void returnsExplicitKeyRegardlessOfFormat() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .superTypeKey("customKey")
                    .build();

            assertEquals("customKey", config.getEffectiveSuperTypeKey(SerializationFormat.PLAIN));
            assertEquals("customKey", config.getEffectiveSuperTypeKey(SerializationFormat.STRUCTURED));
        }

        @Test
        @DisplayName("returns _supertype for null format (defaults to PLAIN)")
        void returnsUnderscoreForNullFormat() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            // When format is null, treat as PLAIN
            assertEquals("_supertype", config.getEffectiveSuperTypeKey(null));
        }
    }

    @Nested
    @DisplayName("mergeWith()")
    class MergeWith {

        @Test
        @DisplayName("returns same instance when source is null")
        void returnsSameInstanceWhenSourceIsNull() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(null);

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns same instance when source is empty")
        void returnsSameInstanceWhenSourceIsEmpty() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of());

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns new instance when source has values")
        void returnsNewInstanceWhenSourceHasValues() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeSerialize", true));

            assertNotSame(config, result);
        }

        @Test
        @DisplayName("overrides serialize from source using short key")
        void overridesSerializeFromSourceUsingShortKey() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeSerialize", true));

            assertTrue(result.isSerialize());
        }

        @Test
        @DisplayName("overrides serialize from source using prefixed key")
        void overridesSerializeFromSourceUsingPrefixedKey() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("codec.superTypeSerialize", true));

            assertTrue(result.isSerialize());
        }

        @Test
        @DisplayName("overrides strategy from source using enum")
        void overridesStrategyFromSourceUsingEnum() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeStrategy", SuperTypeSelection.SINGLE));

            assertEquals(SuperTypeSelection.SINGLE, result.getStrategy());
        }

        @Test
        @DisplayName("overrides strategy from source using string value")
        void overridesStrategyFromSourceUsingString() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeStrategy", "ALL_EMF"));

            assertEquals(SuperTypeSelection.ALL_EMF, result.getStrategy());
        }

        @Test
        @DisplayName("overrides format from source")
        void overridesFormatFromSource() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeFormat", SerializationFormat.STRUCTURED));

            assertEquals(SerializationFormat.STRUCTURED, result.getFormat());
        }

        @Test
        @DisplayName("overrides format from source using string value")
        void overridesFormatFromSourceUsingString() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeFormat", "PLAIN"));

            assertEquals(SerializationFormat.PLAIN, result.getFormat());
        }

        @Test
        @DisplayName("overrides asArray from source")
        void overridesAsArrayFromSource() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            assertTrue(config.isAsArray());

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeAsArray", false));

            assertFalse(result.isAsArray());
        }

        @Test
        @DisplayName("overrides separator from source")
        void overridesSeparatorFromSource() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeSeparator", ";"));

            assertEquals(";", result.getSeparator());
        }

        @Test
        @DisplayName("overrides superTypeKey from source")
        void overridesSuperTypeKeyFromSource() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeKey", "parentTypes"));

            assertEquals("parentTypes", result.getSuperTypeKey());
        }

        // Note: schemaKey test removed - supertype inherits schemaKey from TypeConfig
        // Note: nameKey test removed - superTypeKey has format-dependent default

        @Test
        @DisplayName("overrides valueReaderName from source")
        void overridesValueReaderNameFromSource() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeValueReaderName", "customReader"));

            assertEquals("customReader", result.getValueReaderName());
        }

        @Test
        @DisplayName("overrides valueWriterName from source")
        void overridesValueWriterNameFromSource() {
            SuperTypeConfig config = SuperTypeConfig.defaults();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeValueWriterName", "customWriter"));

            assertEquals("customWriter", result.getValueWriterName());
        }

        @Test
        @DisplayName("keeps base values when not in source")
        void keepsBaseValuesWhenNotInSource() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.SINGLE)
                    .asArray(false)
                    .separator("|")
                    .build();

            SuperTypeConfig result = config.mergeWith(Map.of("superTypeFormat", "STRUCTURED"));

            // These should be kept from base
            assertTrue(result.isSerialize());
            assertEquals(SuperTypeSelection.SINGLE, result.getStrategy());
            assertFalse(result.isAsArray());
            assertEquals("|", result.getSeparator());
            // This should be overridden
            assertEquals(SerializationFormat.STRUCTURED, result.getFormat());
        }
    }

    @Nested
    @DisplayName("cascading merge")
    class CascadingMerge {

        @Test
        @DisplayName("supports chained merge calls")
        void supportsChainedMergeCalls() {
            // Simulates: defaults → annotation → module → factory → options
            SuperTypeConfig config = SuperTypeConfig.defaults()
                    .mergeWith(Map.of("superTypeSerialize", true))                      // annotation layer
                    .mergeWith(Map.of("superTypeStrategy", "SINGLE"))                   // module layer
                    .mergeWith(Map.of("superTypeFormat", "STRUCTURED"))                 // factory layer
                    .mergeWith(Map.of("superTypeKey", "supertypes"));                   // options layer (highest priority)

            // Options overrides all previous values for superTypeKey
            assertEquals("supertypes", config.getSuperTypeKey());
            // Module setting preserved
            assertEquals(SuperTypeSelection.SINGLE, config.getStrategy());
            // Factory setting preserved
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            // Annotation setting preserved
            assertTrue(config.isSerialize());
        }

        @Test
        @DisplayName("later layers override earlier layers")
        void laterLayersOverrideEarlierLayers() {
            SuperTypeConfig config = SuperTypeConfig.defaults()
                    .mergeWith(Map.of("superTypeKey", "layer1"))
                    .mergeWith(Map.of("superTypeKey", "layer2"))
                    .mergeWith(Map.of("superTypeKey", "layer3"));

            assertEquals("layer3", config.getSuperTypeKey());
        }

        @Test
        @DisplayName("intermediate results are immutable")
        void intermediateResultsAreImmutable() {
            SuperTypeConfig base = SuperTypeConfig.defaults();
            SuperTypeConfig afterAnnotation = base.mergeWith(Map.of("superTypeKey", "annotationKey"));
            SuperTypeConfig afterModule = afterAnnotation.mergeWith(Map.of("superTypeKey", "moduleKey"));

            // All three should be different instances
            assertNotSame(base, afterAnnotation);
            assertNotSame(afterAnnotation, afterModule);

            // And have different values
            assertNull(base.getSuperTypeKey());  // format-dependent default
            assertEquals("annotationKey", afterAnnotation.getSuperTypeKey());
            assertEquals("moduleKey", afterModule.getSuperTypeKey());
        }
    }

    @Nested
    @DisplayName("toBuilder()")
    class ToBuilder {

        @Test
        @DisplayName("creates builder with same values")
        void createsBuilderWithSameValues() {
            SuperTypeConfig original = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.SINGLE)
                    .format(SerializationFormat.STRUCTURED)
                    .asArray(false)
                    .separator("|")
                    .superTypeKey("types")
                    .valueReaderName("reader")
                    .valueWriterName("writer")
                    .build();

            SuperTypeConfig copy = original.toBuilder().build();

            assertEquals(original.isSerialize(), copy.isSerialize());
            assertEquals(original.getStrategy(), copy.getStrategy());
            assertEquals(original.getFormat(), copy.getFormat());
            assertEquals(original.isAsArray(), copy.isAsArray());
            assertEquals(original.getSeparator(), copy.getSeparator());
            assertEquals(original.getSuperTypeKey(), copy.getSuperTypeKey());
            assertEquals(original.getValueReaderName(), copy.getValueReaderName());
            assertEquals(original.getValueWriterName(), copy.getValueWriterName());
            // Note: schemaKey removed - supertype inherits from TypeConfig
            // Note: nameKey removed - superTypeKey has format-dependent default
        }

        @Test
        @DisplayName("toBuilder result is independent from original")
        void toBuilderResultIsIndependentFromOriginal() {
            SuperTypeConfig original = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.ALL)
                    .build();

            SuperTypeConfig modified = original.toBuilder()
                    .strategy(SuperTypeSelection.SINGLE)
                    .build();

            // Original should not be affected
            assertEquals(SuperTypeSelection.ALL, original.getStrategy());
            // Modified should have new value
            assertEquals(SuperTypeSelection.SINGLE, modified.getStrategy());
        }
    }

    @Nested
    @DisplayName("validate()")
    class Validate {

        @Test
        @DisplayName("returns same instance after validation")
        void returnsSameInstanceAfterValidation() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            SuperTypeConfig result = config.validate(diagnostics);

            assertSame(config, result);
        }

        @Test
        @DisplayName("warns when separator set but asArray=true")
        void warnsWhenSeparatorSetButAsArrayTrue() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .asArray(true)
                    .separator("|")  // non-default separator
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertEquals(1, diagnostics.getWarningCount());
        }

        @Test
        @DisplayName("does not warn when separator is default and asArray=true")
        void doesNotWarnWhenSeparatorIsDefaultAndAsArrayTrue() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .asArray(true)
                    .separator(",")  // default separator
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("does not warn when separator set and asArray=false")
        void doesNotWarnWhenSeparatorSetAndAsArrayFalse() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .asArray(false)
                    .separator("|")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        // Note: nameKey validation tests removed - nameKey no longer exists
        // The constraint "STRUCTURED format + typeStrategy=NONE + superTypeSerialize=true"
        // must be validated at a higher level where TypeConfig is also available
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("builder() creates default config")
        void builderCreatesDefaultConfig() {
            SuperTypeConfig config = SuperTypeConfig.builder().build();

            assertFalse(config.isSerialize());
            assertEquals(SuperTypeSelection.ALL, config.getStrategy());
            assertNull(config.getFormat());
            assertTrue(config.isAsArray());
            assertEquals(",", config.getSeparator());
            assertNull(config.getSuperTypeKey());  // format-dependent default
            // Note: schemaKey removed - supertype inherits from TypeConfig
            // Note: nameKey removed - superTypeKey has format-dependent default
        }

        @Test
        @DisplayName("builder chain fluently sets multiple properties")
        void builderChainFluentlySetMultipleProperties() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.SINGLE)
                    .format(SerializationFormat.STRUCTURED)
                    .asArray(false)
                    .separator("|")
                    .superTypeKey("types")
                    .valueReaderName("reader")
                    .valueWriterName("writer")
                    .build();

            assertTrue(config.isSerialize());
            assertEquals(SuperTypeSelection.SINGLE, config.getStrategy());
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertFalse(config.isAsArray());
            assertEquals("|", config.getSeparator());
            assertEquals("types", config.getSuperTypeKey());
            assertEquals("reader", config.getValueReaderName());
            assertEquals("writer", config.getValueWriterName());
            // Note: schemaKey removed - supertype inherits from TypeConfig
            // Note: nameKey removed - superTypeKey has format-dependent default
        }
    }
}
