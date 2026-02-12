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
import org.eclipse.fennec.model.metadata.EnumSerializationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link FeatureConfig} and its merge behavior.
 */
@DisplayName("FeatureConfig")
class FeatureConfigTest {

    @Nested
    @DisplayName("defaults()")
    class Defaults {

        @Test
        @DisplayName("returns config with spec-defined defaults")
        void returnsConfigWithSpecDefaults() {
            FeatureConfig config = FeatureConfig.defaults();

            assertNull(config.getKey());
            assertFalse(config.isIgnore());
            assertFalse(config.isIgnoreRead());
            assertFalse(config.isIgnoreWrite());
            assertFalse(config.isForceRead());
            assertFalse(config.isForceWrite());
            assertFalse(config.isSerializeNull());
            assertFalse(config.isSerializeEmpty());
            assertFalse(config.isSerializeDefault());
            assertEquals(EnumSerializationStrategy.LITERAL, config.getEnumSerialization());
            assertNull(config.getValueReaderName());
            assertNull(config.getValueWriterName());
        }
    }

    @Nested
    @DisplayName("mergeWith()")
    class MergeWith {

        @Test
        @DisplayName("returns same instance when source is null")
        void returnsSameInstanceWhenSourceIsNull() {
            FeatureConfig config = FeatureConfig.defaults();

            FeatureConfig result = config.mergeWith(null);

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns same instance when source is empty")
        void returnsSameInstanceWhenSourceIsEmpty() {
            FeatureConfig config = FeatureConfig.defaults();

            FeatureConfig result = config.mergeWith(Map.of());

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns new instance when source has values")
        void returnsNewInstanceWhenSourceHasValues() {
            FeatureConfig config = FeatureConfig.defaults();

            FeatureConfig result = config.mergeWith(Map.of("key", "customKey"));

            assertNotSame(config, result);
        }

        @Test
        @DisplayName("overrides key from source using short key")
        void overridesKeyFromSourceUsingShortKey() {
            FeatureConfig config = FeatureConfig.defaults();

            FeatureConfig result = config.mergeWith(Map.of("key", "customKey"));

            assertEquals("customKey", result.getKey());
        }

        @Test
        @DisplayName("overrides key from source using prefixed key")
        void overridesKeyFromSourceUsingPrefixedKey() {
            FeatureConfig config = FeatureConfig.defaults();

            FeatureConfig result = config.mergeWith(Map.of("codec.key", "prefixedKey"));

            assertEquals("prefixedKey", result.getKey());
        }

        @Test
        @DisplayName("overrides ignore from source")
        void overridesIgnoreFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isIgnore());

            FeatureConfig result = config.mergeWith(Map.of("ignore", true));

            assertTrue(result.isIgnore());
        }

        @Test
        @DisplayName("overrides ignoreRead from source")
        void overridesIgnoreReadFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isIgnoreRead());

            FeatureConfig result = config.mergeWith(Map.of("ignoreRead", true));

            assertTrue(result.isIgnoreRead());
        }

        @Test
        @DisplayName("overrides ignoreWrite from source")
        void overridesIgnoreWriteFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isIgnoreWrite());

            FeatureConfig result = config.mergeWith(Map.of("ignoreWrite", true));

            assertTrue(result.isIgnoreWrite());
        }

        @Test
        @DisplayName("overrides forceRead from source")
        void overridesForceReadFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isForceRead());

            FeatureConfig result = config.mergeWith(Map.of("forceRead", true));

            assertTrue(result.isForceRead());
        }

        @Test
        @DisplayName("overrides forceWrite from source")
        void overridesForceWriteFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isForceWrite());

            FeatureConfig result = config.mergeWith(Map.of("forceWrite", true));

            assertTrue(result.isForceWrite());
        }

        @Test
        @DisplayName("overrides serializeNull from source")
        void overridesSerializeNullFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isSerializeNull());

            FeatureConfig result = config.mergeWith(Map.of("serializeNull", true));

            assertTrue(result.isSerializeNull());
        }

        @Test
        @DisplayName("overrides serializeEmpty from source")
        void overridesSerializeEmptyFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isSerializeEmpty());

            FeatureConfig result = config.mergeWith(Map.of("serializeEmpty", true));

            assertTrue(result.isSerializeEmpty());
        }

        @Test
        @DisplayName("overrides serializeDefault from source")
        void overridesSerializeDefaultFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isSerializeDefault());

            FeatureConfig result = config.mergeWith(Map.of("serializeDefault", true));

            assertTrue(result.isSerializeDefault());
        }

        @Test
        @DisplayName("overrides enumSerialization from source")
        void overridesEnumSerializationFromSource() {
            FeatureConfig config = FeatureConfig.defaults();

            FeatureConfig result = config.mergeWith(Map.of("enumSerialization", EnumSerializationStrategy.VALUE));

            assertEquals(EnumSerializationStrategy.VALUE, result.getEnumSerialization());
        }

        @Test
        @DisplayName("overrides enumSerialization from source using string value")
        void overridesEnumSerializationFromSourceUsingString() {
            FeatureConfig config = FeatureConfig.defaults();

            FeatureConfig result = config.mergeWith(Map.of("enumSerialization", "VALUE"));

            assertEquals(EnumSerializationStrategy.VALUE, result.getEnumSerialization());
        }

        @Test
        @DisplayName("overrides valueReaderName from source")
        void overridesValueReaderNameFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertNull(config.getValueReaderName());

            FeatureConfig result = config.mergeWith(Map.of("valueReaderName", "customReader"));

            assertEquals("customReader", result.getValueReaderName());
        }

        @Test
        @DisplayName("overrides valueWriterName from source")
        void overridesValueWriterNameFromSource() {
            FeatureConfig config = FeatureConfig.defaults();
            assertNull(config.getValueWriterName());

            FeatureConfig result = config.mergeWith(Map.of("valueWriterName", "customWriter"));

            assertEquals("customWriter", result.getValueWriterName());
        }

        @Test
        @DisplayName("keeps base values when not in source")
        void keepsBaseValuesWhenNotInSource() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("customKey")
                    .ignore(true)
                    .serializeNull(true)
                    .build();

            FeatureConfig result = config.mergeWith(Map.of("forceWrite", true));

            // These should be kept from base
            assertEquals("customKey", result.getKey());
            assertTrue(result.isIgnore());
            assertTrue(result.isSerializeNull());
            // This should be overridden
            assertTrue(result.isForceWrite());
        }

        @Test
        @DisplayName("overrides multiple values at once")
        void overridesMultipleValuesAtOnce() {
            FeatureConfig config = FeatureConfig.defaults();

            FeatureConfig result = config.mergeWith(Map.of(
                    "key", "newKey",
                    "ignore", true,
                    "serializeNull", true,
                    "enumSerialization", "VALUE"));

            assertEquals("newKey", result.getKey());
            assertTrue(result.isIgnore());
            assertTrue(result.isSerializeNull());
            assertEquals(EnumSerializationStrategy.VALUE, result.getEnumSerialization());
        }
    }

    @Nested
    @DisplayName("cascading merge")
    class CascadingMerge {

        @Test
        @DisplayName("supports chained merge calls")
        void supportsChainedMergeCalls() {
            // Simulates: defaults → annotation → module → factory → options
            FeatureConfig config = FeatureConfig.defaults()
                    .mergeWith(Map.of("key", "annotationKey"))           // annotation layer
                    .mergeWith(Map.of("ignore", true))                   // module layer
                    .mergeWith(Map.of("forceWrite", true))               // factory layer
                    .mergeWith(Map.of("key", "optionsKey"));             // options layer (highest priority)

            // Options overrides annotation
            assertEquals("optionsKey", config.getKey());
            // Module setting preserved
            assertTrue(config.isIgnore());
            // Factory setting preserved
            assertTrue(config.isForceWrite());
        }

        @Test
        @DisplayName("later layers override earlier layers")
        void laterLayersOverrideEarlierLayers() {
            FeatureConfig config = FeatureConfig.defaults()
                    .mergeWith(Map.of("key", "layer1"))
                    .mergeWith(Map.of("key", "layer2"))
                    .mergeWith(Map.of("key", "layer3"));

            assertEquals("layer3", config.getKey());
        }

        @Test
        @DisplayName("intermediate results are immutable")
        void intermediateResultsAreImmutable() {
            FeatureConfig base = FeatureConfig.defaults();
            FeatureConfig afterAnnotation = base.mergeWith(Map.of("key", "annotationKey"));
            FeatureConfig afterModule = afterAnnotation.mergeWith(Map.of("key", "moduleKey"));

            // All three should be different instances
            assertNotSame(base, afterAnnotation);
            assertNotSame(afterAnnotation, afterModule);

            // And have different values
            assertNull(base.getKey());
            assertEquals("annotationKey", afterAnnotation.getKey());
            assertEquals("moduleKey", afterModule.getKey());
        }
    }

    @Nested
    @DisplayName("toBuilder()")
    class ToBuilder {

        @Test
        @DisplayName("creates builder with same values")
        void createsBuilderWithSameValues() {
            FeatureConfig original = FeatureConfig.builder()
                    .key("customKey")
                    .ignore(true)
                    .ignoreRead(true)
                    .ignoreWrite(true)
                    .forceRead(true)
                    .forceWrite(true)
                    .serializeNull(true)
                    .serializeEmpty(true)
                    .serializeDefault(true)
                    .enumSerialization(EnumSerializationStrategy.VALUE)
                    .valueReaderName("reader")
                    .valueWriterName("writer")
                    .build();

            FeatureConfig copy = original.toBuilder().build();

            assertEquals(original.getKey(), copy.getKey());
            assertEquals(original.isIgnore(), copy.isIgnore());
            assertEquals(original.isIgnoreRead(), copy.isIgnoreRead());
            assertEquals(original.isIgnoreWrite(), copy.isIgnoreWrite());
            assertEquals(original.isForceRead(), copy.isForceRead());
            assertEquals(original.isForceWrite(), copy.isForceWrite());
            assertEquals(original.isSerializeNull(), copy.isSerializeNull());
            assertEquals(original.isSerializeEmpty(), copy.isSerializeEmpty());
            assertEquals(original.isSerializeDefault(), copy.isSerializeDefault());
            assertEquals(original.getEnumSerialization(), copy.getEnumSerialization());
            assertEquals(original.getValueReaderName(), copy.getValueReaderName());
            assertEquals(original.getValueWriterName(), copy.getValueWriterName());
        }

        @Test
        @DisplayName("toBuilder() produces independent copy")
        void toBuilderProducesIndependentCopy() {
            FeatureConfig original = FeatureConfig.builder()
                    .key("original")
                    .ignore(true)
                    .build();

            FeatureConfig modified = original.toBuilder()
                    .key("modified")
                    .ignore(false)
                    .build();

            // Original should be unchanged
            assertEquals("original", original.getKey());
            assertTrue(original.isIgnore());
            // Modified should have new values
            assertEquals("modified", modified.getKey());
            assertFalse(modified.isIgnore());
        }
    }

    @Nested
    @DisplayName("validate()")
    class Validate {

        @Test
        @DisplayName("returns same instance when no violations")
        void returnsSameInstanceWhenNoViolations() {
            FeatureConfig config = FeatureConfig.defaults();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            FeatureConfig result = config.validate(diagnostics);

            assertSame(config, result);
            assertTrue(diagnostics.getWarnings().isEmpty());
            assertTrue(diagnostics.getErrors().isEmpty());
        }

        @Test
        @DisplayName("warns when ignore=true with forceRead=true")
        void warnsWhenIgnoreTrueWithForceReadTrue() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceRead(true)
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            FeatureConfig result = config.validate(diagnostics);

            // forceRead takes precedence, so config is still valid
            assertSame(config, result);
            assertEquals(1, diagnostics.getWarnings().size());
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("ignore=true combined with forceRead"));
        }

        @Test
        @DisplayName("warns when ignore=true with forceWrite=true")
        void warnsWhenIgnoreTrueWithForceWriteTrue() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceWrite(true)
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            FeatureConfig result = config.validate(diagnostics);

            // forceWrite takes precedence, so config is still valid
            assertSame(config, result);
            assertEquals(1, diagnostics.getWarnings().size());
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("ignore=true combined with forceRead/forceWrite"));
        }

        @Test
        @DisplayName("warns when ignoreRead=true with forceRead=true")
        void warnsWhenIgnoreReadTrueWithForceReadTrue() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .forceRead(true)
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            FeatureConfig result = config.validate(diagnostics);

            // forceRead takes precedence, so config is still valid
            assertSame(config, result);
            assertEquals(1, diagnostics.getWarnings().size());
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("ignoreRead=true combined with forceRead=true"));
        }

        @Test
        @DisplayName("warns when ignoreWrite=true with forceWrite=true")
        void warnsWhenIgnoreWriteTrueWithForceWriteTrue() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreWrite(true)
                    .forceWrite(true)
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            FeatureConfig result = config.validate(diagnostics);

            // forceWrite takes precedence, so config is still valid
            assertSame(config, result);
            assertEquals(1, diagnostics.getWarnings().size());
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("ignoreWrite=true combined with forceWrite=true"));
        }

        @Test
        @DisplayName("warns on multiple violations")
        void warnsOnMultipleViolations() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceRead(true)
                    .ignoreWrite(true)
                    .forceWrite(true)
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            FeatureConfig result = config.validate(diagnostics);

            assertSame(config, result);
            // ignore with force (covers both forceRead and forceWrite) + ignoreWrite with forceWrite
            assertEquals(2, diagnostics.getWarnings().size());
        }

        @Test
        @DisplayName("does not warn when only forceRead/forceWrite are set")
        void doesNotWarnWhenOnlyForceAreSet() {
            FeatureConfig config = FeatureConfig.builder()
                    .forceRead(true)
                    .forceWrite(true)
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            FeatureConfig result = config.validate(diagnostics);

            assertSame(config, result);
            assertTrue(diagnostics.getWarnings().isEmpty());
        }

        @Test
        @DisplayName("does not warn when only ignore flags are set alone")
        void doesNotWarnWhenOnlyIgnoreFlagsAreSetAlone() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            FeatureConfig result = config.validate(diagnostics);

            assertSame(config, result);
            assertTrue(diagnostics.getWarnings().isEmpty());
        }
    }

    @Nested
    @DisplayName("shouldSerialize()")
    class ShouldSerialize {

        @Test
        @DisplayName("returns true when forceWrite=true (regardless of ignore flags)")
        void returnsTrueWhenForceWriteTrueRegardlessOfIgnoreFlags() {
            FeatureConfig config = FeatureConfig.builder()
                    .forceWrite(true)
                    .ignore(true)
                    .ignoreWrite(true)
                    .build();

            assertTrue(config.shouldSerialize());
        }

        @Test
        @DisplayName("returns false when ignore=true and forceWrite=false")
        void returnsFalseWhenIgnoreTrueAndForceWriteFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceWrite(false)
                    .build();

            assertFalse(config.shouldSerialize());
        }

        @Test
        @DisplayName("returns false when ignoreWrite=true and forceWrite=false")
        void returnsFalseWhenIgnoreWriteTrueAndForceWriteFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreWrite(true)
                    .forceWrite(false)
                    .build();

            assertFalse(config.shouldSerialize());
        }

        @Test
        @DisplayName("returns true when all ignore flags are false")
        void returnsTrueWhenAllIgnoreFlagsAreFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(false)
                    .ignoreWrite(false)
                    .forceWrite(false)
                    .build();

            assertTrue(config.shouldSerialize());
        }

        @Test
        @DisplayName("returns false when ignore=true even if ignoreWrite=false")
        void returnsFalseWhenIgnoreTrueEvenIfIgnoreWriteFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .ignoreWrite(false)
                    .forceWrite(false)
                    .build();

            assertFalse(config.shouldSerialize());
        }

        @Test
        @DisplayName("forceWrite takes precedence over all ignore flags")
        void forceWriteTakesPrecedenceOverAllIgnoreFlags() {
            FeatureConfig[] configs = {
                    FeatureConfig.builder().ignore(true).forceWrite(true).build(),
                    FeatureConfig.builder().ignoreWrite(true).forceWrite(true).build(),
                    FeatureConfig.builder().ignore(true).ignoreWrite(true).forceWrite(true).build()
            };

            for (FeatureConfig config : configs) {
                assertTrue(config.shouldSerialize(),
                        "forceWrite should override ignore flags");
            }
        }
    }

    @Nested
    @DisplayName("shouldDeserialize()")
    class ShouldDeserialize {

        @Test
        @DisplayName("returns true when forceRead=true (regardless of ignore flags)")
        void returnsTrueWhenForceReadTrueRegardlessOfIgnoreFlags() {
            FeatureConfig config = FeatureConfig.builder()
                    .forceRead(true)
                    .ignore(true)
                    .ignoreRead(true)
                    .build();

            assertTrue(config.shouldDeserialize());
        }

        @Test
        @DisplayName("returns false when ignore=true and forceRead=false")
        void returnsFalseWhenIgnoreTrueAndForceReadFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceRead(false)
                    .build();

            assertFalse(config.shouldDeserialize());
        }

        @Test
        @DisplayName("returns false when ignoreRead=true and forceRead=false")
        void returnsFalseWhenIgnoreReadTrueAndForceReadFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .forceRead(false)
                    .build();

            assertFalse(config.shouldDeserialize());
        }

        @Test
        @DisplayName("returns true when all ignore flags are false")
        void returnsTrueWhenAllIgnoreFlagsAreFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(false)
                    .ignoreRead(false)
                    .forceRead(false)
                    .build();

            assertTrue(config.shouldDeserialize());
        }

        @Test
        @DisplayName("returns false when ignore=true even if ignoreRead=false")
        void returnsFalseWhenIgnoreTrueEvenIfIgnoreReadFalse() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .ignoreRead(false)
                    .forceRead(false)
                    .build();

            assertFalse(config.shouldDeserialize());
        }

        @Test
        @DisplayName("forceRead takes precedence over all ignore flags")
        void forceReadTakesPrecedenceOverAllIgnoreFlags() {
            FeatureConfig[] configs = {
                    FeatureConfig.builder().ignore(true).forceRead(true).build(),
                    FeatureConfig.builder().ignoreRead(true).forceRead(true).build(),
                    FeatureConfig.builder().ignore(true).ignoreRead(true).forceRead(true).build()
            };

            for (FeatureConfig config : configs) {
                assertTrue(config.shouldDeserialize(),
                        "forceRead should override ignore flags");
            }
        }
    }

    @Nested
    @DisplayName("Independent serialize and deserialize control")
    class IndependentSerializeDeserializeControl {

        @Test
        @DisplayName("ignore affects both serialize and deserialize")
        void ignoreAffectsBothSerializeAndDeserialize() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .build();

            assertFalse(config.shouldSerialize());
            assertFalse(config.shouldDeserialize());
        }

        @Test
        @DisplayName("ignoreWrite does not affect shouldDeserialize")
        void ignoreWriteDoesNotAffectShouldDeserialize() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreWrite(true)
                    .build();

            assertFalse(config.shouldSerialize());
            assertTrue(config.shouldDeserialize());
        }

        @Test
        @DisplayName("ignoreRead does not affect shouldSerialize")
        void ignoreReadDoesNotAffectShouldSerialize() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .build();

            assertTrue(config.shouldSerialize());
            assertFalse(config.shouldDeserialize());
        }

        @Test
        @DisplayName("forceWrite does not affect shouldDeserialize")
        void forceWriteDoesNotAffectShouldDeserialize() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceWrite(true)
                    .build();

            assertTrue(config.shouldSerialize());
            assertFalse(config.shouldDeserialize());
        }

        @Test
        @DisplayName("forceRead does not affect shouldSerialize")
        void forceReadDoesNotAffectShouldSerialize() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceRead(true)
                    .build();

            assertFalse(config.shouldSerialize());
            assertTrue(config.shouldDeserialize());
        }

        @Test
        @DisplayName("can force read while ignoring write")
        void canForceReadWhileIgnoringWrite() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreWrite(true)
                    .forceRead(true)
                    .build();

            assertFalse(config.shouldSerialize());
            assertTrue(config.shouldDeserialize());
        }

        @Test
        @DisplayName("can force write while ignoring read")
        void canForceWriteWhileIgnoringRead() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .forceWrite(true)
                    .build();

            assertTrue(config.shouldSerialize());
            assertFalse(config.shouldDeserialize());
        }
    }
}
