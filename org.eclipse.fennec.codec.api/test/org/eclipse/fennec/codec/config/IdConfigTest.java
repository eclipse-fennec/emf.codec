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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode;
import org.eclipse.fennec.codec.metadata.model.codec.IdStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link IdConfig} and its merge behavior.
 */
@DisplayName("IdConfig")
class IdConfigTest {

    @Nested
    @DisplayName("defaults()")
    class Defaults {

        @Test
        @DisplayName("returns config with spec-defined defaults")
        void returnsConfigWithSpecDefaults() {
            IdConfig config = IdConfig.defaults();

            assertEquals("_id", config.getKey());
            assertEquals(IdStrategy.ID_FIELD, config.getStrategy());
            assertEquals(IdKeyMode.ID_ONLY, config.getKeyMode());
            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertEquals("id", config.getValueKey());
            assertEquals(List.of(), config.getIdFeatures());
            assertEquals("-", config.getSeparator());
            assertEquals("separator", config.getSeparatorKey());
            assertTrue(config.isSerializeSeparator());
            assertTrue(config.isOnTop());
        }
    }

    @Nested
    @DisplayName("mergeWith()")
    class MergeWith {

        @Test
        @DisplayName("returns same instance when source is null")
        void returnsSameInstanceWhenSourceIsNull() {
            IdConfig config = IdConfig.defaults();

            IdConfig result = config.mergeWith(null);

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns same instance when source is empty")
        void returnsSameInstanceWhenSourceIsEmpty() {
            IdConfig config = IdConfig.defaults();

            IdConfig result = config.mergeWith(Map.of());

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns new instance when source has values")
        void returnsNewInstanceWhenSourceHasValues() {
            IdConfig config = IdConfig.defaults();

            IdConfig result = config.mergeWith(Map.of("idKey", "id"));

            assertNotSame(config, result);
        }

        @Test
        @DisplayName("overrides key from source using short key")
        void overridesKeyFromSourceUsingShortKey() {
            IdConfig config = IdConfig.defaults();

            IdConfig result = config.mergeWith(Map.of("idKey", "customId"));

            assertEquals("customId", result.getKey());
        }

        @Test
        @DisplayName("overrides key from source using prefixed key")
        void overridesKeyFromSourceUsingPrefixedKey() {
            IdConfig config = IdConfig.defaults();

            IdConfig result = config.mergeWith(Map.of("codec.idKey", "prefixedId"));

            assertEquals("prefixedId", result.getKey());
        }

        @Test
        @DisplayName("overrides strategy from source")
        void overridesStrategyFromSource() {
            IdConfig config = IdConfig.defaults();

            IdConfig result = config.mergeWith(Map.of("idStrategy", IdStrategy.COMBINED));

            assertEquals(IdStrategy.COMBINED, result.getStrategy());
        }

        @Test
        @DisplayName("overrides strategy from source using string value")
        void overridesStrategyFromSourceUsingString() {
            IdConfig config = IdConfig.defaults();

            IdConfig result = config.mergeWith(Map.of("idStrategy", "COMBINED"));

            assertEquals(IdStrategy.COMBINED, result.getStrategy());
        }

        @Test
        @DisplayName("overrides boolean from source")
        void overridesBooleanFromSource() {
            IdConfig config = IdConfig.defaults();
            assertTrue(config.isOnTop());

            IdConfig result = config.mergeWith(Map.of("idOnTop", false));

            assertFalse(result.isOnTop());
        }

        @Test
        @DisplayName("overrides list from source")
        void overridesListFromSource() {
            IdConfig config = IdConfig.defaults();

            IdConfig result = config.mergeWith(Map.of("idFeatures", List.of("name", "type")));

            assertEquals(List.of("name", "type"), result.getIdFeatures());
        }

        @Test
        @DisplayName("keeps base values when not in source")
        void keepsBaseValuesWhenNotInSource() {
            IdConfig config = IdConfig.builder()
                    .key("customKey")
                    .strategy(IdStrategy.COMBINED)
                    .onTop(true)
                    .build();

            IdConfig result = config.mergeWith(Map.of("idFormat", "STRUCTURED"));

            // These should be kept from base
            assertEquals("customKey", result.getKey());
            assertEquals(IdStrategy.COMBINED, result.getStrategy());
            assertTrue(result.isOnTop());
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
            IdConfig config = IdConfig.defaults()
                    .mergeWith(Map.of("idKey", "annotationId"))           // annotation layer
                    .mergeWith(Map.of("idStrategy", "COMBINED"))          // module layer
                    .mergeWith(Map.of("idOnTop", true))                   // factory layer
                    .mergeWith(Map.of("idKey", "optionsId"));             // options layer (highest priority)

            // Options overrides annotation
            assertEquals("optionsId", config.getKey());
            // Module setting preserved
            assertEquals(IdStrategy.COMBINED, config.getStrategy());
            // Factory setting preserved
            assertTrue(config.isOnTop());
        }

        @Test
        @DisplayName("later layers override earlier layers")
        void laterLayersOverrideEarlierLayers() {
            IdConfig config = IdConfig.defaults()
                    .mergeWith(Map.of("idKey", "layer1"))
                    .mergeWith(Map.of("idKey", "layer2"))
                    .mergeWith(Map.of("idKey", "layer3"));

            assertEquals("layer3", config.getKey());
        }

        @Test
        @DisplayName("intermediate results are immutable")
        void intermediateResultsAreImmutable() {
            IdConfig base = IdConfig.defaults();
            IdConfig afterAnnotation = base.mergeWith(Map.of("idKey", "annotationId"));
            IdConfig afterModule = afterAnnotation.mergeWith(Map.of("idKey", "moduleId"));

            // All three should be different instances
            assertNotSame(base, afterAnnotation);
            assertNotSame(afterAnnotation, afterModule);

            // And have different values
            assertEquals("_id", base.getKey());
            assertEquals("annotationId", afterAnnotation.getKey());
            assertEquals("moduleId", afterModule.getKey());
        }
    }

    @Nested
    @DisplayName("toBuilder()")
    class ToBuilder {

        @Test
        @DisplayName("creates builder with same values")
        void createsBuilderWithSameValues() {
            IdConfig original = IdConfig.builder()
                    .key("customKey")
                    .strategy(IdStrategy.COMBINED)
                    .format(SerializationFormat.STRUCTURED)
                    .idFeatures(List.of("a", "b"))
                    .onTop(true)
                    .build();

            IdConfig copy = original.toBuilder().build();

            assertEquals(original.getKey(), copy.getKey());
            assertEquals(original.getStrategy(), copy.getStrategy());
            assertEquals(original.getFormat(), copy.getFormat());
            assertEquals(original.getIdFeatures(), copy.getIdFeatures());
            assertEquals(original.isOnTop(), copy.isOnTop());
        }
    }

    @Nested
    @DisplayName("validate()")
    class Validate {

        @Test
        @DisplayName("returns same instance when no validation issues")
        void returnsSameInstanceWhenNoValidationIssues() {
            IdConfig config = IdConfig.defaults();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            IdConfig result = config.validate(diagnostics);

            assertSame(config, result);
            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("adds error when COMBINED strategy has empty idFeatures")
        void addsErrorWhenCombinedStrategyHasEmptyIdFeatures() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of())
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors());
            assertEquals(1, diagnostics.getErrorCount());
            assertTrue(diagnostics.getErrors().get(0).getMessage().contains("idFeatures"));
        }

        @Test
        @DisplayName("adds error when COMBINED strategy has null idFeatures")
        void addsErrorWhenCombinedStrategyHasNullIdFeatures() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(null)
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors());
            assertEquals(1, diagnostics.getErrorCount());
        }

        @Test
        @DisplayName("no error when COMBINED strategy has non-empty idFeatures")
        void noErrorWhenCombinedStrategyHasNonEmptyIdFeatures() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of("name", "type"))
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
        }

        @Test
        @DisplayName("no error when ID_FIELD strategy has empty idFeatures")
        void noErrorWhenIdFieldStrategyHasEmptyIdFeatures() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.ID_FIELD)
                    .idFeatures(List.of())
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
        }

        @Test
        @DisplayName("adds warning when valueKey is non-default and format is not STRUCTURED")
        void addsWarningWhenValueKeyIsNonDefaultAndFormatIsNotStructured() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .valueKey("customValueKey")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertEquals(1, diagnostics.getWarningCount());
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("idValueKey"));
        }

        @Test
        @DisplayName("no warning when valueKey is default and format is not STRUCTURED")
        void noWarningWhenValueKeyIsDefaultAndFormatIsNotStructured() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .valueKey("id")  // default value
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("no warning when valueKey is non-default and format is STRUCTURED")
        void noWarningWhenValueKeyIsNonDefaultAndFormatIsStructured() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .valueKey("customValueKey")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("adds warning when separatorKey is non-default and format is not STRUCTURED")
        void addsWarningWhenSeparatorKeyIsNonDefaultAndFormatIsNotStructured() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .separatorKey("customSeparatorKey")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertEquals(1, diagnostics.getWarningCount());
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("idSeparatorKey"));
        }

        @Test
        @DisplayName("no warning when separatorKey is default and format is not STRUCTURED")
        void noWarningWhenSeparatorKeyIsDefaultAndFormatIsNotStructured() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .separatorKey("separator")  // default value
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("collects multiple warnings")
        void collectsMultipleWarnings() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .valueKey("customValueKey")
                    .separatorKey("customSeparatorKey")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertEquals(2, diagnostics.getWarningCount());
        }

        @Test
        @DisplayName("collects both errors and warnings")
        void collectsBothErrorsAndWarnings() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of())  // error: empty
                    .format(SerializationFormat.PLAIN)
                    .valueKey("customValueKey")  // warning: non-default
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors());
            assertTrue(diagnostics.hasWarnings());
            assertEquals(1, diagnostics.getErrorCount());
            assertEquals(1, diagnostics.getWarningCount());
        }
    }
}
