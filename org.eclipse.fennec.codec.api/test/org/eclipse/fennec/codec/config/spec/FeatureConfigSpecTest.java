
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
package org.eclipse.fennec.codec.config.spec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based tests for {@link FeatureConfig}.
 * <p>
 * These tests are derived directly from the codec-v2 specification documents:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/11-feature.md} - Feature Serialization specification</li>
 *   <li>{@code docs/codec-v2-spec/16-annotation-reference.md} - Annotation Reference</li>
 * </ul>
 * <p>
 * Test organization:
 * <ul>
 *   <li>Section 1: Default values (from spec section 5)</li>
 *   <li>Section 2: Directional visibility flags (from spec section 1.2)</li>
 *   <li>Section 3: Computed properties - shouldSerialize/shouldDeserialize (from spec section 1.2)</li>
 *   <li>Section 4: Validation rules (contradictory flag combinations)</li>
 *   <li>Section 5: Serialize value flags (from spec section 1.1, 1.3)</li>
 *   <li>Section 6: Enum serialization strategy (from spec section 4)</li>
 *   <li>Section 7: Key customization (from spec section 2)</li>
 *   <li>Section 8: Custom value reader/writer (from spec section 10)</li>
 *   <li>Section 9: Merge behavior (from spec: Configuration Resolution)</li>
 * </ul>
 */
@DisplayName("FeatureConfig Spec Tests")
class FeatureConfigSpecTest {

    // ========================================================================
    // Section 1: Default Values
    // Spec reference: 11-feature.md section 5 "Default Feature Settings"
    // ========================================================================

    @Nested
    @DisplayName("1. Default Values (spec section 5)")
    class DefaultValues {

        /**
         * Spec section 5: "Key | codec.key | null (feature name)"
         */
        @Test
        @DisplayName("1.1 key defaults to null (use feature name)")
        void key_defaultsToNull() {
            FeatureConfig config = FeatureConfig.defaults();
            assertNull(config.getKey());
        }

        /**
         * Spec section 5: "Ignore | codec.ignore | false"
         */
        @Test
        @DisplayName("1.2 ignore defaults to false")
        void ignore_defaultsToFalse() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isIgnore());
        }

        /**
         * Spec section 5: "Ignore Read | codec.ignoreRead | false"
         */
        @Test
        @DisplayName("1.3 ignoreRead defaults to false")
        void ignoreRead_defaultsToFalse() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isIgnoreRead());
        }

        /**
         * Spec section 5: "Ignore Write | codec.ignoreWrite | false"
         */
        @Test
        @DisplayName("1.4 ignoreWrite defaults to false")
        void ignoreWrite_defaultsToFalse() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isIgnoreWrite());
        }

        /**
         * Spec section 5: "Force Read | codec.forceRead | false"
         */
        @Test
        @DisplayName("1.5 forceRead defaults to false")
        void forceRead_defaultsToFalse() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isForceRead());
        }

        /**
         * Spec section 5: "Force Write | codec.forceWrite | false"
         */
        @Test
        @DisplayName("1.6 forceWrite defaults to false")
        void forceWrite_defaultsToFalse() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isForceWrite());
        }

        /**
         * Spec section 5: "Serialize Null | codec.serializeNull | false"
         */
        @Test
        @DisplayName("1.7 serializeNull defaults to false")
        void serializeNull_defaultsToFalse() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isSerializeNull());
        }

        /**
         * Spec section 5: "Serialize Defaults | codec.serializeDefaults | false"
         */
        @Test
        @DisplayName("1.8 serializeDefault defaults to false")
        void serializeDefault_defaultsToFalse() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isSerializeDefault());
        }

        /**
         * Spec section 5: "Serialize Empty | codec.serializeEmpty | false"
         */
        @Test
        @DisplayName("1.9 serializeEmpty defaults to false")
        void serializeEmpty_defaultsToFalse() {
            FeatureConfig config = FeatureConfig.defaults();
            assertFalse(config.isSerializeEmpty());
        }

        /**
         * Spec section 5: "Enum Serialization | codec.enumSerialization | LITERAL"
         */
        @Test
        @DisplayName("1.10 enumSerialization defaults to LITERAL")
        void enumSerialization_defaultsToLiteral() {
            FeatureConfig config = FeatureConfig.defaults();
            assertEquals(EnumSerializationStrategy.LITERAL, config.getEnumSerialization());
        }

        /**
         * Spec section 10: valueReaderName defaults to null (no custom reader)
         */
        @Test
        @DisplayName("1.11 valueReaderName defaults to null")
        void valueReaderName_defaultsToNull() {
            FeatureConfig config = FeatureConfig.defaults();
            assertNull(config.getValueReaderName());
        }

        /**
         * Spec section 10: valueWriterName defaults to null (no custom writer)
         */
        @Test
        @DisplayName("1.12 valueWriterName defaults to null")
        void valueWriterName_defaultsToNull() {
            FeatureConfig config = FeatureConfig.defaults();
            assertNull(config.getValueWriterName());
        }
    }

    // ========================================================================
    // Section 2: Directional Visibility Flags
    // Spec reference: 11-feature.md section 1.2 "Feature Visibility Control"
    // ========================================================================

    @Nested
    @DisplayName("2. Directional Visibility Flags (spec section 1.2)")
    class DirectionalVisibilityFlags {

        /**
         * Spec section 1.2: "ignore=true: Completely hide a feature from JSON (both directions)"
         */
        @Test
        @DisplayName("2.1 ignore=true hides feature from both directions")
        void ignore_hidesBothDirections() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .build();

            assertTrue(config.isIgnore());
            assertFalse(config.shouldSerialize());
            assertFalse(config.shouldDeserialize());
        }

        /**
         * Spec section 1.2: "ignoreWrite=true: Read from JSON but don't write back (one-way import)"
         */
        @Test
        @DisplayName("2.2 ignoreWrite=true skips serialization only")
        void ignoreWrite_skipsSerialization() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreWrite(true)
                    .build();

            assertTrue(config.isIgnoreWrite());
            assertFalse(config.shouldSerialize());
            assertTrue(config.shouldDeserialize());
        }

        /**
         * Spec section 1.2: "ignoreRead=true: Write to JSON but ignore during read (computed output fields)"
         */
        @Test
        @DisplayName("2.3 ignoreRead=true skips deserialization only")
        void ignoreRead_skipsDeserialization() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .build();

            assertTrue(config.isIgnoreRead());
            assertTrue(config.shouldSerialize());
            assertFalse(config.shouldDeserialize());
        }

        /**
         * Spec section 1.2: "forceWrite=true: Serialize volatile/derived features"
         */
        @Test
        @DisplayName("2.4 forceWrite=true forces serialization")
        void forceWrite_forcesSerialization() {
            FeatureConfig config = FeatureConfig.builder()
                    .forceWrite(true)
                    .build();

            assertTrue(config.isForceWrite());
            assertTrue(config.shouldSerialize());
        }

        /**
         * Spec section 1.2: "forceRead=true: Deserialize into volatile/derived features"
         */
        @Test
        @DisplayName("2.5 forceRead=true forces deserialization")
        void forceRead_forcesDeserialization() {
            FeatureConfig config = FeatureConfig.builder()
                    .forceRead(true)
                    .build();

            assertTrue(config.isForceRead());
            assertTrue(config.shouldDeserialize());
        }

        /**
         * Spec section 1.2: All flags false means visible in both directions
         */
        @Test
        @DisplayName("2.6 all flags false: visible in both directions")
        void allFlagsFalse_visibleBothDirections() {
            FeatureConfig config = FeatureConfig.defaults();

            assertTrue(config.shouldSerialize());
            assertTrue(config.shouldDeserialize());
        }
    }

    // ========================================================================
    // Section 3: Computed Properties - shouldSerialize/shouldDeserialize
    // Spec reference: 11-feature.md section 1.2 "Resolution logic"
    // ========================================================================

    @Nested
    @DisplayName("3. Computed Properties (spec section 1.2 resolution logic)")
    class ComputedProperties {

        // --- shouldSerialize truth table ---
        // Spec: "If ignore=true OR ignoreWrite=true → skip"
        //       "If forceWrite=true → continue (even if ignore)"

        /**
         * Spec: ignore=false, ignoreWrite=false, forceWrite=false → shouldSerialize=true
         */
        @Test
        @DisplayName("3.1 shouldSerialize: all false → true")
        void shouldSerialize_allFalse_true() {
            FeatureConfig config = FeatureConfig.defaults();
            assertTrue(config.shouldSerialize());
        }

        /**
         * Spec: ignore=true → shouldSerialize=false
         */
        @Test
        @DisplayName("3.2 shouldSerialize: ignore=true → false")
        void shouldSerialize_ignoreTrue_false() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .build();
            assertFalse(config.shouldSerialize());
        }

        /**
         * Spec: ignoreWrite=true → shouldSerialize=false
         */
        @Test
        @DisplayName("3.3 shouldSerialize: ignoreWrite=true → false")
        void shouldSerialize_ignoreWriteTrue_false() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreWrite(true)
                    .build();
            assertFalse(config.shouldSerialize());
        }

        /**
         * Spec: forceWrite=true overrides ignore=true → shouldSerialize=true
         */
        @Test
        @DisplayName("3.4 shouldSerialize: forceWrite=true overrides ignore=true → true")
        void shouldSerialize_forceWriteOverridesIgnore_true() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceWrite(true)
                    .build();
            assertTrue(config.shouldSerialize());
        }

        /**
         * Spec: forceWrite=true overrides ignoreWrite=true → shouldSerialize=true
         */
        @Test
        @DisplayName("3.5 shouldSerialize: forceWrite=true overrides ignoreWrite=true → true")
        void shouldSerialize_forceWriteOverridesIgnoreWrite_true() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreWrite(true)
                    .forceWrite(true)
                    .build();
            assertTrue(config.shouldSerialize());
        }

        // --- shouldDeserialize truth table ---
        // Spec: "If ignore=true OR ignoreRead=true → skip"
        //       "If forceRead=true → continue (even if ignore)"

        /**
         * Spec: ignore=false, ignoreRead=false, forceRead=false → shouldDeserialize=true
         */
        @Test
        @DisplayName("3.6 shouldDeserialize: all false → true")
        void shouldDeserialize_allFalse_true() {
            FeatureConfig config = FeatureConfig.defaults();
            assertTrue(config.shouldDeserialize());
        }

        /**
         * Spec: ignore=true → shouldDeserialize=false
         */
        @Test
        @DisplayName("3.7 shouldDeserialize: ignore=true → false")
        void shouldDeserialize_ignoreTrue_false() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .build();
            assertFalse(config.shouldDeserialize());
        }

        /**
         * Spec: ignoreRead=true → shouldDeserialize=false
         */
        @Test
        @DisplayName("3.8 shouldDeserialize: ignoreRead=true → false")
        void shouldDeserialize_ignoreReadTrue_false() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .build();
            assertFalse(config.shouldDeserialize());
        }

        /**
         * Spec: forceRead=true overrides ignore=true → shouldDeserialize=true
         */
        @Test
        @DisplayName("3.9 shouldDeserialize: forceRead=true overrides ignore=true → true")
        void shouldDeserialize_forceReadOverridesIgnore_true() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceRead(true)
                    .build();
            assertTrue(config.shouldDeserialize());
        }

        /**
         * Spec: forceRead=true overrides ignoreRead=true → shouldDeserialize=true
         */
        @Test
        @DisplayName("3.10 shouldDeserialize: forceRead=true overrides ignoreRead=true → true")
        void shouldDeserialize_forceReadOverridesIgnoreRead_true() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .forceRead(true)
                    .build();
            assertTrue(config.shouldDeserialize());
        }

        /**
         * Spec: ignoreRead does not affect shouldSerialize
         */
        @Test
        @DisplayName("3.11 ignoreRead does not affect shouldSerialize")
        void ignoreRead_doesNotAffectSerialize() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .build();
            assertTrue(config.shouldSerialize());
        }

        /**
         * Spec: ignoreWrite does not affect shouldDeserialize
         */
        @Test
        @DisplayName("3.12 ignoreWrite does not affect shouldDeserialize")
        void ignoreWrite_doesNotAffectDeserialize() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreWrite(true)
                    .build();
            assertTrue(config.shouldDeserialize());
        }
    }

    // ========================================================================
    // Section 4: Validation Rules
    // Spec reference: FeatureConfig.validate() - contradictory flag combinations
    // ========================================================================

    @Nested
    @DisplayName("4. Validation Rules (contradictory flag combinations)")
    class ValidationRules {

        /**
         * Spec: "ignore=true combined with forceRead/forceWrite is contradictory; force takes precedence"
         */
        @Test
        @DisplayName("4.1 ignore=true + forceWrite=true: WARNING (contradictory)")
        void ignore_forceWrite_warning() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceWrite(true)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Expected WARNING: ignore + forceWrite is contradictory");
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("ignore"));
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("force"));
        }

        /**
         * Spec: "ignore=true combined with forceRead is also contradictory"
         */
        @Test
        @DisplayName("4.2 ignore=true + forceRead=true: WARNING (contradictory)")
        void ignore_forceRead_warning() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceRead(true)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Expected WARNING: ignore + forceRead is contradictory");
        }

        /**
         * Spec: "ignoreRead=true combined with forceRead=true is contradictory; forceRead takes precedence"
         */
        @Test
        @DisplayName("4.3 ignoreRead=true + forceRead=true: WARNING (contradictory)")
        void ignoreRead_forceRead_warning() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .forceRead(true)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Expected WARNING: ignoreRead + forceRead is contradictory");
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("ignoreRead"));
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("forceRead"));
        }

        /**
         * Spec: "ignoreWrite=true combined with forceWrite=true is contradictory; forceWrite takes precedence"
         */
        @Test
        @DisplayName("4.4 ignoreWrite=true + forceWrite=true: WARNING (contradictory)")
        void ignoreWrite_forceWrite_warning() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreWrite(true)
                    .forceWrite(true)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Expected WARNING: ignoreWrite + forceWrite is contradictory");
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("ignoreWrite"));
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("forceWrite"));
        }

        /**
         * Spec: ignore=true + forceRead=true + forceWrite=true produces single warning
         * (the ignore+force warning covers both)
         */
        @Test
        @DisplayName("4.5 ignore=true + forceRead + forceWrite: single WARNING")
        void ignore_bothForce_singleWarning() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .forceRead(true)
                    .forceWrite(true)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            // The ignore+forceRead/forceWrite is caught by a single check
            assertEquals(1, diagnostics.getWarningCount(),
                "ignore + both force flags should produce a single warning");
        }

        /**
         * Spec: Multiple independent contradictions produce separate warnings
         */
        @Test
        @DisplayName("4.6 ignoreRead+forceRead AND ignoreWrite+forceWrite: two WARNINGs")
        void multipleContradictions_separateWarnings() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .forceRead(true)
                    .ignoreWrite(true)
                    .forceWrite(true)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertEquals(2, diagnostics.getWarningCount(),
                "Two independent contradictions should produce two warnings");
        }

        /**
         * Spec: Default config passes validation with no issues
         */
        @Test
        @DisplayName("4.7 default config passes validation cleanly")
        void defaultConfig_passesCleanly() {
            FeatureConfig config = FeatureConfig.defaults();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }

        /**
         * Spec: Non-contradictory configurations produce no warnings
         */
        @Test
        @DisplayName("4.8 non-contradictory config with flags: no warnings")
        void nonContradictory_noWarnings() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignoreRead(true)
                    .forceWrite(true) // force write is not contradictory with ignoreRead
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                "ignoreRead + forceWrite is NOT contradictory");
        }

        /**
         * Spec: validate() returns this (same instance)
         */
        @Test
        @DisplayName("4.9 validate() returns same config instance")
        void validate_returnsSameInstance() {
            FeatureConfig config = FeatureConfig.defaults();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            FeatureConfig validated = config.validate(diagnostics);

            assertSame(config, validated);
        }
    }

    // ========================================================================
    // Section 5: Serialize Value Flags
    // Spec reference: 11-feature.md section 1.1 and 1.3
    // ========================================================================

    @Nested
    @DisplayName("5. Serialize Value Flags (spec section 1.1, 1.3)")
    class SerializeValueFlags {

        /**
         * Spec section 1.1: "serializeNull=true → Include fields with explicit null values"
         */
        @Test
        @DisplayName("5.1 serializeNull=true includes null values")
        void serializeNull_true() {
            FeatureConfig config = FeatureConfig.builder()
                    .serializeNull(true)
                    .build();

            assertTrue(config.isSerializeNull());
        }

        /**
         * Spec section 1.1: "serializeEmpty=true → Include empty collections"
         */
        @Test
        @DisplayName("5.2 serializeEmpty=true includes empty collections")
        void serializeEmpty_true() {
            FeatureConfig config = FeatureConfig.builder()
                    .serializeEmpty(true)
                    .build();

            assertTrue(config.isSerializeEmpty());
        }

        /**
         * Spec section 1.1: "serializeDefaults=true → Include fields with default values"
         */
        @Test
        @DisplayName("5.3 serializeDefault=true includes default values")
        void serializeDefault_true() {
            FeatureConfig config = FeatureConfig.builder()
                    .serializeDefault(true)
                    .build();

            assertTrue(config.isSerializeDefault());
        }

        /**
         * Spec section 1.5: These are serialization-only settings, but config stores them.
         * All three can be set independently.
         */
        @Test
        @DisplayName("5.4 all serialize flags can be set independently")
        void allSerializeFlags_setIndependently() {
            FeatureConfig config = FeatureConfig.builder()
                    .serializeNull(true)
                    .serializeEmpty(false)
                    .serializeDefault(true)
                    .build();

            assertTrue(config.isSerializeNull());
            assertFalse(config.isSerializeEmpty());
            assertTrue(config.isSerializeDefault());
        }
    }

    // ========================================================================
    // Section 6: Enum Serialization Strategy
    // Spec reference: 11-feature.md section 4
    // ========================================================================

    @Nested
    @DisplayName("6. Enum Serialization Strategy (spec section 4)")
    class EnumSerializationStrategyTests {

        /**
         * Spec section 4: "LITERAL (default) → Use enum literal name"
         */
        @Test
        @DisplayName("6.1 LITERAL strategy uses enum literal name")
        void literal_strategy() {
            FeatureConfig config = FeatureConfig.builder()
                    .enumSerialization(EnumSerializationStrategy.LITERAL)
                    .build();

            assertEquals(EnumSerializationStrategy.LITERAL, config.getEnumSerialization());
        }

        /**
         * Spec section 4: "VALUE → Use enum ordinal value"
         */
        @Test
        @DisplayName("6.2 VALUE strategy uses enum ordinal value")
        void value_strategy() {
            FeatureConfig config = FeatureConfig.builder()
                    .enumSerialization(EnumSerializationStrategy.VALUE)
                    .build();

            assertEquals(EnumSerializationStrategy.VALUE, config.getEnumSerialization());
        }

        /**
         * Spec section 4: "NAME → Use enum name"
         */
        @Test
        @DisplayName("6.3 NAME strategy uses enum name")
        void name_strategy() {
            FeatureConfig config = FeatureConfig.builder()
                    .enumSerialization(EnumSerializationStrategy.NAME)
                    .build();

            assertEquals(EnumSerializationStrategy.NAME, config.getEnumSerialization());
        }

        /**
         * Spec section 4: All strategies are valid for any feature config
         */
        @Test
        @DisplayName("6.4 all EnumSerializationStrategy values are valid")
        void allStrategies_valid() {
            for (EnumSerializationStrategy strategy : EnumSerializationStrategy.values()) {
                FeatureConfig config = FeatureConfig.builder()
                        .enumSerialization(strategy)
                        .build();

                DiagnosticCollector diagnostics = new DiagnosticCollector();
                config.validate(diagnostics);

                assertEquals(strategy, config.getEnumSerialization());
                assertFalse(diagnostics.hasErrors(),
                    "Strategy " + strategy + " should not produce errors");
            }
        }
    }

    // ========================================================================
    // Section 7: Key Customization
    // Spec reference: 11-feature.md section 2
    // ========================================================================

    @Nested
    @DisplayName("7. Key Customization (spec section 2)")
    class KeyCustomization {

        /**
         * Spec section 2: "Override the JSON property name for a feature"
         */
        @Test
        @DisplayName("7.1 custom key overrides feature name")
        void customKey_overridesFeatureName() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("first_name")
                    .build();

            assertEquals("first_name", config.getKey());
        }

        /**
         * Spec section 2.2: Config builder can override annotation-defined key
         */
        @Test
        @DisplayName("7.2 key can be set to any string")
        void key_anyString() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("givenName")
                    .build();

            assertEquals("givenName", config.getKey());
        }

        /**
         * Spec section 2: null key means use feature name (default behavior)
         */
        @Test
        @DisplayName("7.3 null key means use feature name")
        void nullKey_useFeatureName() {
            FeatureConfig config = FeatureConfig.builder()
                    .key(null)
                    .build();

            assertNull(config.getKey());
        }
    }

    // ========================================================================
    // Section 8: Custom Value Reader/Writer
    // Spec reference: 11-feature.md section 10
    // ========================================================================

    @Nested
    @DisplayName("8. Custom Value Reader/Writer (spec section 10)")
    class CustomValueReaderWriter {

        /**
         * Spec section 10: "valueWriterName → Custom value writer service name"
         */
        @Test
        @DisplayName("8.1 valueWriterName is carried through config")
        void valueWriterName_carriedThroughConfig() {
            FeatureConfig config = FeatureConfig.builder()
                    .valueWriterName("isoDateWriter")
                    .build();

            assertEquals("isoDateWriter", config.getValueWriterName());
        }

        /**
         * Spec section 10: "valueReaderName → Custom value reader service name"
         */
        @Test
        @DisplayName("8.2 valueReaderName is carried through config")
        void valueReaderName_carriedThroughConfig() {
            FeatureConfig config = FeatureConfig.builder()
                    .valueReaderName("isoDateReader")
                    .build();

            assertEquals("isoDateReader", config.getValueReaderName());
        }

        /**
         * Spec section 10: Both reader and writer can be set simultaneously
         */
        @Test
        @DisplayName("8.3 both valueWriterName and valueReaderName can be set")
        void bothWriterAndReader_canBeSet() {
            FeatureConfig config = FeatureConfig.builder()
                    .valueWriterName("isoDateWriter")
                    .valueReaderName("isoDateReader")
                    .build();

            assertEquals("isoDateWriter", config.getValueWriterName());
            assertEquals("isoDateReader", config.getValueReaderName());
        }

        /**
         * Spec section 10: Writer/reader names don't produce validation errors
         */
        @Test
        @DisplayName("8.4 custom value reader/writer names don't cause validation errors")
        void customNames_noValidationErrors() {
            FeatureConfig config = FeatureConfig.builder()
                    .valueWriterName("myWriter")
                    .valueReaderName("myReader")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }

        /**
         * Spec: Writer/reader names work with ignore flags (config stores both)
         */
        @Test
        @DisplayName("8.5 valueWriterName + ignore: both stored (visibility decided at runtime)")
        void valueWriterName_withIgnore_bothStored() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .valueWriterName("myWriter")
                    .build();

            assertTrue(config.isIgnore());
            assertEquals("myWriter", config.getValueWriterName());
            // At runtime, ignore gate runs first — writer is never invoked
            assertFalse(config.shouldSerialize());
        }
    }

    // ========================================================================
    // Section 9: Merge Behavior
    // Spec reference: 16-annotation-reference.md "Configuration Resolution"
    // ========================================================================

    @Nested
    @DisplayName("9. Merge Behavior (spec: Configuration Resolution)")
    class MergeBehavior {

        /**
         * Spec: "Higher priority sources override lower ones"
         */
        @Test
        @DisplayName("9.1 property map overrides existing values")
        void propertyMap_overridesExistingValues() {
            FeatureConfig base = FeatureConfig.builder()
                    .ignore(false)
                    .key("original")
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.ignore", true);
            override.put("codec.key", "overridden");

            FeatureConfig merged = base.mergeWith(override);

            assertTrue(merged.isIgnore());
            assertEquals("overridden", merged.getKey());
        }

        /**
         * Spec: Non-overridden values are preserved from base
         */
        @Test
        @DisplayName("9.2 non-overridden values preserved from base")
        void nonOverriddenValues_preservedFromBase() {
            FeatureConfig base = FeatureConfig.builder()
                    .ignore(true)
                    .serializeNull(true)
                    .key("customKey")
                    .enumSerialization(EnumSerializationStrategy.NAME)
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.serializeEmpty", true);

            FeatureConfig merged = base.mergeWith(override);

            // Overridden
            assertTrue(merged.isSerializeEmpty());
            // Preserved
            assertTrue(merged.isIgnore());
            assertTrue(merged.isSerializeNull());
            assertEquals("customKey", merged.getKey());
            assertEquals(EnumSerializationStrategy.NAME, merged.getEnumSerialization());
        }

        /**
         * Spec: Empty or null source should not change config
         */
        @Test
        @DisplayName("9.3 empty property map returns same config")
        void emptyPropertyMap_returnsSameConfig() {
            FeatureConfig config = FeatureConfig.builder()
                    .ignore(true)
                    .build();

            FeatureConfig merged = config.mergeWith(new HashMap<>());
            assertSame(config, merged);

            merged = config.mergeWith(null);
            assertSame(config, merged);
        }

        /**
         * Spec: Cascading merge chain (annotations → module → factory → options)
         */
        @Test
        @DisplayName("9.4 cascading merge: later layers override earlier")
        void cascadingMerge_laterLayersOverride() {
            FeatureConfig config = FeatureConfig.defaults()
                    .mergeWith(Map.of("codec.key", "annotationKey"))
                    .mergeWith(Map.of("codec.ignore", true))
                    .mergeWith(Map.of("codec.key", "optionsKey"));

            assertEquals("optionsKey", config.getKey());     // latest overrides
            assertTrue(config.isIgnore());                    // preserved from earlier layer
        }

        /**
         * Spec: String values accepted for enums
         */
        @Test
        @DisplayName("9.5 enum values accepted as strings")
        void enumValues_acceptedAsStrings() {
            FeatureConfig base = FeatureConfig.defaults();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.enumSerialization", "NAME");

            FeatureConfig merged = base.mergeWith(override);

            assertEquals(EnumSerializationStrategy.NAME, merged.getEnumSerialization());
        }

        /**
         * Spec: Boolean values from property map
         */
        @Test
        @DisplayName("9.6 boolean values merge correctly")
        void booleanValues_mergeCorrectly() {
            FeatureConfig base = FeatureConfig.defaults();
            assertFalse(base.isIgnore());
            assertFalse(base.isSerializeNull());

            Map<String, Object> override = new HashMap<>();
            override.put("codec.ignore", true);
            override.put("codec.serializeNull", true);

            FeatureConfig merged = base.mergeWith(override);

            assertTrue(merged.isIgnore());
            assertTrue(merged.isSerializeNull());
        }

        /**
         * Spec: All directional visibility flags can be set via merge
         */
        @Test
        @DisplayName("9.7 all directional flags can be set via property map")
        void allDirectionalFlags_viaMerge() {
            FeatureConfig base = FeatureConfig.defaults();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.ignore", true);
            override.put("codec.ignoreRead", true);
            override.put("codec.ignoreWrite", true);
            override.put("codec.forceRead", true);
            override.put("codec.forceWrite", true);

            FeatureConfig merged = base.mergeWith(override);

            assertTrue(merged.isIgnore());
            assertTrue(merged.isIgnoreRead());
            assertTrue(merged.isIgnoreWrite());
            assertTrue(merged.isForceRead());
            assertTrue(merged.isForceWrite());
        }

        /**
         * Spec: Value writer/reader names merge from higher-priority property maps
         */
        @Test
        @DisplayName("9.8 valueWriterName/valueReaderName merge from property map")
        void valueWriterReaderName_mergeFromPropertyMap() {
            FeatureConfig base = FeatureConfig.defaults();

            assertNull(base.getValueWriterName());
            assertNull(base.getValueReaderName());

            Map<String, Object> override = new HashMap<>();
            override.put("codec.valueWriterName", "runtimeWriter");
            override.put("codec.valueReaderName", "runtimeReader");

            FeatureConfig merged = base.mergeWith(override);

            assertEquals("runtimeWriter", merged.getValueWriterName());
            assertEquals("runtimeReader", merged.getValueReaderName());
        }

        /**
         * Spec: Higher-priority writer overrides lower-priority
         */
        @Test
        @DisplayName("9.9 higher-priority valueWriterName overrides lower-priority")
        void valueWriterName_higherPriorityOverrides() {
            FeatureConfig base = FeatureConfig.builder()
                    .valueWriterName("annotationWriter")
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.valueWriterName", "runtimeWriter");

            FeatureConfig merged = base.mergeWith(override);

            assertEquals("runtimeWriter", merged.getValueWriterName());
        }
    }
}
