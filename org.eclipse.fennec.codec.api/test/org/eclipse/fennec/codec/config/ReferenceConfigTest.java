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

import java.util.Map;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ReferenceConfig} and its merge behavior.
 */
@DisplayName("ReferenceConfig")
class ReferenceConfigTest {

    @Nested
    @DisplayName("defaults()")
    class Defaults {

        @Test
        @DisplayName("returns config with spec-defined defaults")
        void returnsConfigWithSpecDefaults() {
            ReferenceConfig config = ReferenceConfig.defaults();

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals("_ref", config.getRefKey());
            assertEquals("_type", config.getRefTypeKey());
            assertEquals("$proxy", config.getProxyKey());
            assertFalse(config.isExpand());
            assertFalse(config.isExpandGlobal());
            assertEquals(1, config.getExpandDepth());
            assertTrue(config.isExpandIgnoreBidirectional());
            assertTrue(config.isSerializeInstanceType());
            assertEquals(null, config.getValueReaderName());
            assertEquals(null, config.getValueWriterName());
        }
    }

    @Nested
    @DisplayName("mergeWith()")
    class MergeWith {

        @Test
        @DisplayName("returns same instance when source is null")
        void returnsSameInstanceWhenSourceIsNull() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(null);

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns same instance when source is empty")
        void returnsSameInstanceWhenSourceIsEmpty() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of());

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns new instance when source has values")
        void returnsNewInstanceWhenSourceHasValues() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of("refKey", "$id"));

            assertNotSame(config, result);
        }

        @Test
        @DisplayName("overrides refKey from source using short key")
        void overridesRefKeyFromSourceUsingShortKey() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of("refKey", "$id"));

            assertEquals("$id", result.getRefKey());
        }

        @Test
        @DisplayName("overrides refKey from source using prefixed key")
        void overridesRefKeyFromSourceUsingPrefixedKey() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of("codec.refKey", "prefixedRef"));

            assertEquals("prefixedRef", result.getRefKey());
        }

        @Test
        @DisplayName("overrides format from source")
        void overridesFormatFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of("refFormat", SerializationFormat.PLAIN));

            assertEquals(SerializationFormat.PLAIN, result.getFormat());
        }

        @Test
        @DisplayName("overrides format from source using string value")
        void overridesFormatFromSourceUsingString() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of("refFormat", "PLAIN"));

            assertEquals(SerializationFormat.PLAIN, result.getFormat());
        }

        @Test
        @DisplayName("overrides refTypeKey from source")
        void overridesRefTypeKeyFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of("refTypeKey", "type"));

            assertEquals("type", result.getRefTypeKey());
        }

        @Test
        @DisplayName("overrides proxyKey from source")
        void overridesProxyKeyFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of("proxyKey", "$isProxy"));

            assertEquals("$isProxy", result.getProxyKey());
        }

        @Test
        @DisplayName("overrides expand from source")
        void overridesExpandFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertFalse(config.isExpand());

            ReferenceConfig result = config.mergeWith(Map.of("expand", true));

            assertTrue(result.isExpand());
        }

        @Test
        @DisplayName("overrides expandGlobal from source")
        void overridesExpandGlobalFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertFalse(config.isExpandGlobal());

            ReferenceConfig result = config.mergeWith(Map.of("expandGlobal", true));

            assertTrue(result.isExpandGlobal());
        }

        @Test
        @DisplayName("overrides expandDepth from source")
        void overridesExpandDepthFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertEquals(1, config.getExpandDepth());

            ReferenceConfig result = config.mergeWith(Map.of("expandDepth", 2));

            assertEquals(2, result.getExpandDepth());
        }

        @Test
        @DisplayName("overrides expandIgnoreBidirectional from source")
        void overridesExpandIgnoreBidirectionalFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertTrue(config.isExpandIgnoreBidirectional());

            ReferenceConfig result = config.mergeWith(Map.of("expandIgnoreBidirectional", false));

            assertFalse(result.isExpandIgnoreBidirectional());
        }

        @Test
        @DisplayName("overrides serializeInstanceType from source")
        void overridesSerializeInstanceTypeFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertTrue(config.isSerializeInstanceType());

            ReferenceConfig result = config.mergeWith(Map.of("serializeInstanceType", false));

            assertFalse(result.isSerializeInstanceType());
        }

        @Test
        @DisplayName("overrides valueReaderName from source")
        void overridesValueReaderNameFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of("valueReaderName", "myReader"));

            assertEquals("myReader", result.getValueReaderName());
        }

        @Test
        @DisplayName("overrides valueWriterName from source")
        void overridesValueWriterNameFromSource() {
            ReferenceConfig config = ReferenceConfig.defaults();

            ReferenceConfig result = config.mergeWith(Map.of("valueWriterName", "myWriter"));

            assertEquals("myWriter", result.getValueWriterName());
        }

        @Test
        @DisplayName("keeps base values when not in source")
        void keepsBaseValuesWhenNotInSource() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .refKey("customRef")
                    .expandGlobal(true)
                    .expandDepth(2)
                    .build();

            ReferenceConfig result = config.mergeWith(Map.of("refFormat", "PLAIN"));

            // These should be kept from base
            assertEquals("customRef", result.getRefKey());
            assertTrue(result.isExpandGlobal());
            assertEquals(2, result.getExpandDepth());
            // This should be overridden
            assertEquals(SerializationFormat.PLAIN, result.getFormat());
        }
    }

    @Nested
    @DisplayName("cascading merge")
    class CascadingMerge {

        @Test
        @DisplayName("supports chained merge calls")
        void supportsChainedMergeCalls() {
            // Simulates: defaults → annotation → module → factory → options
            ReferenceConfig config = ReferenceConfig.defaults()
                    .mergeWith(Map.of("refKey", "annotationRef"))           // annotation layer
                    .mergeWith(Map.of("expand", true))                       // module layer
                    .mergeWith(Map.of("expandDepth", 2))                     // factory layer
                    .mergeWith(Map.of("refKey", "optionsRef"));              // options layer (highest priority)

            // Options overrides annotation
            assertEquals("optionsRef", config.getRefKey());
            // Module setting preserved
            assertTrue(config.isExpand());
            // Factory setting preserved
            assertEquals(2, config.getExpandDepth());
        }

        @Test
        @DisplayName("later layers override earlier layers")
        void laterLayersOverrideEarlierLayers() {
            ReferenceConfig config = ReferenceConfig.defaults()
                    .mergeWith(Map.of("refKey", "layer1"))
                    .mergeWith(Map.of("refKey", "layer2"))
                    .mergeWith(Map.of("refKey", "layer3"));

            assertEquals("layer3", config.getRefKey());
        }

        @Test
        @DisplayName("intermediate results are immutable")
        void intermediateResultsAreImmutable() {
            ReferenceConfig base = ReferenceConfig.defaults();
            ReferenceConfig afterAnnotation = base.mergeWith(Map.of("refKey", "annotationRef"));
            ReferenceConfig afterModule = afterAnnotation.mergeWith(Map.of("refKey", "moduleRef"));

            // All three should be different instances
            assertNotSame(base, afterAnnotation);
            assertNotSame(afterAnnotation, afterModule);

            // And have different values
            assertEquals("_ref", base.getRefKey());
            assertEquals("annotationRef", afterAnnotation.getRefKey());
            assertEquals("moduleRef", afterModule.getRefKey());
        }
    }

    @Nested
    @DisplayName("toBuilder()")
    class ToBuilder {

        @Test
        @DisplayName("creates builder with same values")
        void createsBuilderWithSameValues() {
            ReferenceConfig original = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refKey("customRef")
                    .refTypeKey("customType")
                    .proxyKey("customProxy")
                    .expand(true)
                    .expandGlobal(true)
                    .expandDepth(2)
                    .expandIgnoreBidirectional(false)
                    .serializeInstanceType(false)
                    .valueReaderName("reader")
                    .valueWriterName("writer")
                    .build();

            ReferenceConfig copy = original.toBuilder().build();

            assertEquals(original.getFormat(), copy.getFormat());
            assertEquals(original.getRefKey(), copy.getRefKey());
            assertEquals(original.getRefTypeKey(), copy.getRefTypeKey());
            assertEquals(original.getProxyKey(), copy.getProxyKey());
            assertEquals(original.isExpand(), copy.isExpand());
            assertEquals(original.isExpandGlobal(), copy.isExpandGlobal());
            assertEquals(original.getExpandDepth(), copy.getExpandDepth());
            assertEquals(original.isExpandIgnoreBidirectional(), copy.isExpandIgnoreBidirectional());
            assertEquals(original.isSerializeInstanceType(), copy.isSerializeInstanceType());
            assertEquals(original.getValueReaderName(), copy.getValueReaderName());
            assertEquals(original.getValueWriterName(), copy.getValueWriterName());
        }

        @Test
        @DisplayName("creates independent builder from original")
        void createsIndependentBuilderFromOriginal() {
            ReferenceConfig original = ReferenceConfig.defaults();
            ReferenceConfig modified = original.toBuilder()
                    .refKey("newKey")
                    .build();

            // Original should be unchanged
            assertEquals("_ref", original.getRefKey());
            assertEquals("newKey", modified.getRefKey());
        }
    }

    @Nested
    @DisplayName("validate()")
    class Validate {

        @Test
        @DisplayName("returns this for validation chaining")
        void returnsThisForValidationChaining() {
            ReferenceConfig config = ReferenceConfig.defaults();
            DiagnosticCollector collector = new DiagnosticCollector();

            ReferenceConfig result = config.validate(collector);

            assertSame(config, result);
        }

        @Test
        @DisplayName("warning when refTypeKey is set but format is PLAIN")
        void warningWhenRefTypeKeySetButFormatIsPlain() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refTypeKey("type")
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            assertTrue(collector.hasWarnings());
            assertEquals(1, collector.getWarningCount());
            assertTrue(collector.getWarnings().get(0).getMessage()
                    .contains("refTypeKey is ignored when refFormat is not STRUCTURED"));
        }

        @Test
        @DisplayName("warning when refTypeKey is set to non-default but format is PLAIN")
        void warningWhenRefTypeKeyNonDefaultButFormatIsPlain() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refTypeKey("customType")
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            assertTrue(collector.hasWarnings());
            assertEquals(1, collector.getWarningCount());
        }

        @Test
        @DisplayName("no warning when refTypeKey is default and format is PLAIN")
        void noWarningWhenRefTypeKeyDefaultAndFormatIsPlain() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refTypeKey("_type") // default value
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            assertFalse(collector.hasWarnings());
        }

        @Test
        @DisplayName("no warning when refTypeKey is set and format is STRUCTURED")
        void noWarningWhenRefTypeKeySetAndFormatIsStructured() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .refTypeKey("type")
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            assertFalse(collector.hasWarnings());
        }

        @Test
        @DisplayName("warning when expandDepth is greater than 1")
        void warningWhenExpandDepthGreaterThanOne() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expandDepth(2)
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            assertTrue(collector.hasWarnings());
            assertTrue(collector.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandDepth values greater than 1 are not yet supported")));
        }

        @Test
        @DisplayName("warning when expandDepth is 3")
        void warningWhenExpandDepthIsThree() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expandDepth(3)
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            assertTrue(collector.hasWarnings());
            assertTrue(collector.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandDepth values greater than 1 are not yet supported")));
        }

        @Test
        @DisplayName("no warning when expandDepth is 1")
        void noWarningWhenExpandDepthIsOne() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expandDepth(1)
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            // Should have no warnings related to expandDepth
            assertFalse(collector.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandDepth")));
        }

        @Test
        @DisplayName("info when expandIgnoreBidirectional is changed but expand is not enabled")
        void infoWhenExpandIgnoreBidirectionalChangedButExpandNotEnabled() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(false)
                    .expandGlobal(false)
                    .expandIgnoreBidirectional(false) // changed from default true
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            assertTrue(collector.hasWarnings());
            assertTrue(collector.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandIgnoreBidirectional is ignored when expand is not enabled")));
        }

        @Test
        @DisplayName("no info when expandIgnoreBidirectional is default value and expand is not enabled")
        void noInfoWhenExpandIgnoreBidirectionalDefaultAndExpandNotEnabled() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(false)
                    .expandGlobal(false)
                    .expandIgnoreBidirectional(true) // default value
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            // Should have no info/warning about expandIgnoreBidirectional
            assertFalse(collector.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandIgnoreBidirectional")));
        }

        @Test
        @DisplayName("no info when expandIgnoreBidirectional is changed but expand is enabled")
        void noInfoWhenExpandIgnoreBidirectionalChangedButExpandEnabled() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(true)
                    .expandIgnoreBidirectional(false)
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            // Should have no info/warning about expandIgnoreBidirectional
            assertFalse(collector.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandIgnoreBidirectional")));
        }

        @Test
        @DisplayName("no info when expandIgnoreBidirectional is changed but expandGlobal is enabled")
        void noInfoWhenExpandIgnoreBidirectionalChangedButExpandGlobalEnabled() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(false)
                    .expandGlobal(true)
                    .expandIgnoreBidirectional(false)
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            // Should have no info/warning about expandIgnoreBidirectional
            assertFalse(collector.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandIgnoreBidirectional")));
        }

        @Test
        @DisplayName("multiple warnings are all collected")
        void multipleWarningsAreAllCollected() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refTypeKey("customType")
                    .expandDepth(5)
                    .expandIgnoreBidirectional(false)
                    .build();

            DiagnosticCollector collector = new DiagnosticCollector();
            config.validate(collector);

            assertTrue(collector.hasWarnings());
            // Should have at least 3 warnings (refTypeKey, expandDepth, expandIgnoreBidirectional)
            assertTrue(collector.getWarningCount() >= 3);
        }
    }

    @Nested
    @DisplayName("shouldExpand()")
    class ShouldExpand {

        @Test
        @DisplayName("returns true when expand is true")
        void returnsTrueWhenExpandIsTrue() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(true)
                    .expandGlobal(false)
                    .build();

            assertTrue(config.shouldExpand());
        }

        @Test
        @DisplayName("returns true when expandGlobal is true")
        void returnsTrueWhenExpandGlobalIsTrue() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(false)
                    .expandGlobal(true)
                    .build();

            assertTrue(config.shouldExpand());
        }

        @Test
        @DisplayName("returns true when both expand and expandGlobal are true")
        void returnsTrueWhenBothExpandAndExpandGlobalAreTrue() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(true)
                    .expandGlobal(true)
                    .build();

            assertTrue(config.shouldExpand());
        }

        @Test
        @DisplayName("returns false when both expand and expandGlobal are false")
        void returnsFalseWhenBothArefalse() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(false)
                    .expandGlobal(false)
                    .build();

            assertFalse(config.shouldExpand());
        }

        @Test
        @DisplayName("returns false for default config")
        void returnsFalseForDefaultConfig() {
            ReferenceConfig config = ReferenceConfig.defaults();

            assertFalse(config.shouldExpand());
        }
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("builds config with custom values")
        void buildsConfigWithCustomValues() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refKey("id")
                    .proxyKey("isProxy")
                    .expand(true)
                    .expandDepth(2)
                    .expandIgnoreBidirectional(false)
                    .build();

            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertEquals("id", config.getRefKey());
            assertEquals("isProxy", config.getProxyKey());
            assertTrue(config.isExpand());
            assertEquals(2, config.getExpandDepth());
            assertFalse(config.isExpandIgnoreBidirectional());
        }

        @Test
        @DisplayName("builder uses defaults for unset values")
        void builderUsesDefaultsForUnsetValues() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .refKey("custom")
                    .build();

            // Custom value set
            assertEquals("custom", config.getRefKey());
            // Defaults applied for unset values
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals("$proxy", config.getProxyKey());
            assertFalse(config.isExpand());
        }
    }
}
