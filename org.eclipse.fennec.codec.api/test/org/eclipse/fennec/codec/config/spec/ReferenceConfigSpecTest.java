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

import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based tests for {@link ReferenceConfig}.
 * <p>
 * These tests are derived directly from the codec-v2 specification documents:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/10-reference.md} - Reference Serialization specification</li>
 *   <li>{@code docs/codec-v2-spec/16-annotation-reference.md} - Annotation Reference</li>
 * </ul>
 * <p>
 * Test organization:
 * <ul>
 *   <li>Section 1: Default values (from spec section 8)</li>
 *   <li>Section 2: Format and key customization (from spec section 3)</li>
 *   <li>Section 3: Expansion control (from spec section 5)</li>
 *   <li>Section 4: Computed properties - shouldExpand() (from spec section 5)</li>
 *   <li>Section 5: Validation rules R-V5, R-V6, R-V7 (from spec section 10)</li>
 *   <li>Section 6: Custom value reader/writer</li>
 *   <li>Section 7: Merge behavior (from spec: Configuration Resolution)</li>
 *   <li>Section 8: Format × Expand combinations</li>
 * </ul>
 */
@DisplayName("ReferenceConfig Spec Tests")
class ReferenceConfigSpecTest {

    // ========================================================================
    // Section 1: Default Values
    // Spec reference: 10-reference.md section 8 "Default Reference Settings"
    // ========================================================================

    @Nested
    @DisplayName("1. Default Values (spec section 8)")
    class DefaultValues {

        /**
         * Spec section 8: "Format | codec.refFormat | STRUCTURED"
         */
        @Test
        @DisplayName("1.1 format defaults to STRUCTURED")
        void format_defaultsToStructured() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
        }

        /**
         * Spec section 8: "Ref Key | codec.refKey | $ref"
         * Note: spec §8 table says "_ref" but ConfigProperty default is "$ref"
         */
        @Test
        @DisplayName("1.2 refKey defaults to $ref")
        void refKey_defaultsToRef() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertEquals("$ref", config.getRefKey());
        }

        /**
         * Spec section 8: "Type Key | codec.refTypeKey | _type"
         */
        @Test
        @DisplayName("1.3 refTypeKey defaults to _type")
        void refTypeKey_defaultsToType() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertEquals("_type", config.getRefTypeKey());
        }

        /**
         * Spec section 8: "Proxy Key | codec.proxyKey | $proxy"
         */
        @Test
        @DisplayName("1.4 proxyKey defaults to $proxy")
        void proxyKey_defaultsToProxy() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertEquals("$proxy", config.getProxyKey());
        }

        /**
         * Spec section 8: "Expand (specific refs) | codec.expand | false"
         */
        @Test
        @DisplayName("1.5 expand defaults to false")
        void expand_defaultsToFalse() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertFalse(config.isExpand());
        }

        /**
         * Spec section 8: "Expand Global | codec.expandGlobal | false"
         */
        @Test
        @DisplayName("1.6 expandGlobal defaults to false")
        void expandGlobal_defaultsToFalse() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertFalse(config.isExpandGlobal());
        }

        /**
         * Spec section 8: "Expand Depth | codec.expandDepth | 1"
         */
        @Test
        @DisplayName("1.7 expandDepth defaults to 1")
        void expandDepth_defaultsToOne() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertEquals(1, config.getExpandDepth());
        }

        /**
         * Spec section 8: "Expand Ignore Bidirectional | codec.expandIgnoreBidirectional | true"
         */
        @Test
        @DisplayName("1.8 expandIgnoreBidirectional defaults to true")
        void expandIgnoreBidirectional_defaultsToTrue() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertTrue(config.isExpandIgnoreBidirectional());
        }

        /**
         * Spec: "serializeInstanceType defaults to true"
         */
        @Test
        @DisplayName("1.9 serializeInstanceType defaults to true")
        void serializeInstanceType_defaultsToTrue() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertTrue(config.isSerializeInstanceType());
        }

        /**
         * Spec: valueReaderName defaults to null (no custom reader)
         */
        @Test
        @DisplayName("1.10 valueReaderName defaults to null")
        void valueReaderName_defaultsToNull() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertNull(config.getValueReaderName());
        }

        /**
         * Spec: valueWriterName defaults to null (no custom writer)
         */
        @Test
        @DisplayName("1.11 valueWriterName defaults to null")
        void valueWriterName_defaultsToNull() {
            ReferenceConfig config = ReferenceConfig.defaults();
            assertNull(config.getValueWriterName());
        }

        /**
         * Spec: All defaults come from ConfigProperty enum
         */
        @Test
        @DisplayName("1.12 defaults match ConfigProperty enum values")
        void defaults_matchConfigPropertyEnum() {
            ReferenceConfig config = ReferenceConfig.defaults();

            assertEquals(SerializationFormat.valueOf(ConfigProperty.REF_FORMAT.getDefaultValue()), config.getFormat());
            assertEquals(ConfigProperty.REF_KEY.<String>getDefaultValue(), config.getRefKey());
            assertEquals(ConfigProperty.REF_TYPE_KEY.<String>getDefaultValue(), config.getRefTypeKey());
            assertEquals(ConfigProperty.PROXY_KEY.<String>getDefaultValue(), config.getProxyKey());
            // Note: ReferenceConfig.expand (boolean) is for THIS reference's expansion state.
            // ConfigProperty.EXPAND is a List of references to expand (different semantics).
            // The default for a reference's expand state is false (not expanded unless explicitly listed).
            assertFalse(config.isExpand(), "Default expand state for a reference should be false");
            assertEquals(ConfigProperty.EXPAND_GLOBAL.<Boolean>getDefaultValue(), config.isExpandGlobal());
            assertEquals(ConfigProperty.EXPAND_DEPTH.<Integer>getDefaultValue(), config.getExpandDepth());
            assertEquals(ConfigProperty.EXPAND_IGNORE_BIDIRECTIONAL.<Boolean>getDefaultValue(), config.isExpandIgnoreBidirectional());
            assertEquals(ConfigProperty.SERIALIZE_INSTANCE_TYPE.<Boolean>getDefaultValue(), config.isSerializeInstanceType());
        }
    }

    // ========================================================================
    // Section 2: Format and Key Customization
    // Spec reference: 10-reference.md section 3 "Reference Configuration"
    // ========================================================================

    @Nested
    @DisplayName("2. Format and Key Customization (spec section 3)")
    class FormatAndKeyCustomization {

        /**
         * Spec section 1.1: "PLAIN Strategy - Single value (URI or ID)"
         */
        @Test
        @DisplayName("2.1 PLAIN format can be set")
        void plainFormat_canBeSet() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .build();

            assertEquals(SerializationFormat.PLAIN, config.getFormat());
        }

        /**
         * Spec section 1.2: "STRUCTURED Strategy (Default) - Nested object with type and reference"
         */
        @Test
        @DisplayName("2.2 STRUCTURED format can be set explicitly")
        void structuredFormat_canBeSetExplicitly() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .build();

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
        }

        /**
         * Spec section 3.1: "refKey | codec.refKey | $ref | Reference value key"
         */
        @Test
        @DisplayName("2.3 custom refKey overrides default")
        void customRefKey_overridesDefault() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .refKey("$id")
                    .build();

            assertEquals("$id", config.getRefKey());
        }

        /**
         * Spec section 3.1: "refTypeKey | codec.refTypeKey | _type | Type key in STRUCTURED"
         */
        @Test
        @DisplayName("2.4 custom refTypeKey overrides default")
        void customRefTypeKey_overridesDefault() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .refTypeKey("eClass")
                    .build();

            assertEquals("eClass", config.getRefTypeKey());
        }

        /**
         * Spec: "proxyKey can be customized"
         */
        @Test
        @DisplayName("2.5 custom proxyKey overrides default")
        void customProxyKey_overridesDefault() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .proxyKey("_isProxy")
                    .build();

            assertEquals("_isProxy", config.getProxyKey());
        }

        /**
         * Spec section 3.1: All format+key settings independent
         */
        @Test
        @DisplayName("2.6 all keys can be customized independently")
        void allKeys_canBeCustomizedIndependently() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refKey("id")
                    .refTypeKey("type")
                    .proxyKey("proxy")
                    .build();

            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertEquals("id", config.getRefKey());
            assertEquals("type", config.getRefTypeKey());
            assertEquals("proxy", config.getProxyKey());
        }
    }

    // ========================================================================
    // Section 3: Expansion Control
    // Spec reference: 10-reference.md section 5 "Proxy and Expand Handling"
    // ========================================================================

    @Nested
    @DisplayName("3. Expansion Control (spec section 5)")
    class ExpansionControl {

        /**
         * Spec section 5: "expand=true: Inline full object instead of proxy"
         */
        @Test
        @DisplayName("3.1 expand=true enables inline expansion")
        void expandTrue_enablesInlineExpansion() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(true)
                    .build();

            assertTrue(config.isExpand());
            assertTrue(config.shouldExpand());
        }

        /**
         * Spec section 5: "expandGlobal=true: Expand all non-containment references"
         */
        @Test
        @DisplayName("3.2 expandGlobal=true enables global expansion")
        void expandGlobalTrue_enablesGlobalExpansion() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expandGlobal(true)
                    .build();

            assertTrue(config.isExpandGlobal());
            assertTrue(config.shouldExpand());
        }

        /**
         * Spec section 5: "expandDepth=1: Only direct references"
         */
        @Test
        @DisplayName("3.3 expandDepth controls nesting depth")
        void expandDepth_controlsNestingDepth() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expandDepth(2)
                    .build();

            assertEquals(2, config.getExpandDepth());
        }

        /**
         * Spec section 5: "expandIgnoreBidirectional=true: Skip opposite references during expansion"
         */
        @Test
        @DisplayName("3.4 expandIgnoreBidirectional=false allows bidirectional expansion")
        void expandIgnoreBidirectionalFalse_allowsBidirectional() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expandIgnoreBidirectional(false)
                    .build();

            assertFalse(config.isExpandIgnoreBidirectional());
        }

        /**
         * Spec: serializeInstanceType=false suppresses type in STRUCTURED
         */
        @Test
        @DisplayName("3.5 serializeInstanceType=false suppresses type info")
        void serializeInstanceTypeFalse_suppressesType() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .serializeInstanceType(false)
                    .build();

            assertFalse(config.isSerializeInstanceType());
        }
    }

    // ========================================================================
    // Section 4: Computed Properties - shouldExpand()
    // Spec reference: 10-reference.md section 5
    // ========================================================================

    @Nested
    @DisplayName("4. Computed Properties - shouldExpand() (spec section 5)")
    class ComputedProperties {

        /**
         * Spec: "A reference is expanded if either expand=true or expandGlobal=true"
         */
        @Test
        @DisplayName("4.1 shouldExpand: expand=true, expandGlobal=false → true")
        void shouldExpand_expandTrue() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(true)
                    .expandGlobal(false)
                    .build();

            assertTrue(config.shouldExpand());
        }

        @Test
        @DisplayName("4.2 shouldExpand: expand=false, expandGlobal=true → true")
        void shouldExpand_expandGlobalTrue() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(false)
                    .expandGlobal(true)
                    .build();

            assertTrue(config.shouldExpand());
        }

        @Test
        @DisplayName("4.3 shouldExpand: both true → true")
        void shouldExpand_bothTrue() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(true)
                    .expandGlobal(true)
                    .build();

            assertTrue(config.shouldExpand());
        }

        @Test
        @DisplayName("4.4 shouldExpand: both false → false")
        void shouldExpand_bothFalse() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(false)
                    .expandGlobal(false)
                    .build();

            assertFalse(config.shouldExpand());
        }

        @Test
        @DisplayName("4.5 shouldExpand: default config → false")
        void shouldExpand_defaultConfig() {
            ReferenceConfig config = ReferenceConfig.defaults();

            assertFalse(config.shouldExpand());
        }
    }

    // ========================================================================
    // Section 5: Validation Rules
    // Spec reference: 10-reference.md section 10 (R-V5, R-V6, R-V7)
    // ========================================================================

    @Nested
    @DisplayName("5. Validation Rules (spec section 10: R-V5, R-V6, R-V7)")
    class ValidationRules {

        /**
         * Spec R-V5: "refTypeKey ≠ default + refFormat ≠ STRUCTURED → WARNING"
         */
        @Test
        @DisplayName("5.1 R-V5: refTypeKey custom + PLAIN format → WARNING")
        void rv5_refTypeKeyCustomWithPlain_warning() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refTypeKey("customType")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Spec R-V5: Custom refTypeKey with PLAIN format should produce WARNING");
            assertTrue(diagnostics.getWarnings().get(0).getMessage()
                    .contains("refTypeKey is ignored when refFormat is not STRUCTURED"));
        }

        /**
         * Spec R-V5: refTypeKey=default with PLAIN → no warning
         */
        @Test
        @DisplayName("5.2 R-V5: refTypeKey default + PLAIN format → no warning")
        void rv5_refTypeKeyDefaultWithPlain_noWarning() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("refTypeKey")),
                "Spec R-V5: Default refTypeKey with PLAIN should NOT produce warning");
        }

        /**
         * Spec R-V5: refTypeKey custom + STRUCTURED → no warning
         */
        @Test
        @DisplayName("5.3 R-V5: refTypeKey custom + STRUCTURED format → no warning")
        void rv5_refTypeKeyCustomWithStructured_noWarning() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .refTypeKey("customType")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        /**
         * Spec R-V6: "expandDepth > 1 → WARNING"
         */
        @Test
        @DisplayName("5.4 R-V6: expandDepth=2 → WARNING")
        void rv6_expandDepthGreaterThanOne_warning() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expandDepth(2)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Spec R-V6: expandDepth > 1 should produce WARNING");
            assertTrue(diagnostics.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandDepth")));
        }

        /**
         * Spec R-V6: expandDepth=1 → no warning
         */
        @Test
        @DisplayName("5.5 R-V6: expandDepth=1 → no warning")
        void rv6_expandDepthOne_noWarning() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expandDepth(1)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandDepth")));
        }

        /**
         * Spec R-V7: "expandIgnoreBidirectional ≠ default + shouldExpand() = false → INFO"
         */
        @Test
        @DisplayName("5.6 R-V7: expandIgnoreBidirectional=false + no expand → INFO")
        void rv7_expandIgnoreBidirectionalChanged_noExpand_info() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(false)
                    .expandGlobal(false)
                    .expandIgnoreBidirectional(false)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Spec R-V7: expandIgnoreBidirectional changed without expand should produce diagnostic");
            assertTrue(diagnostics.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandIgnoreBidirectional")));
        }

        /**
         * Spec R-V7: expandIgnoreBidirectional changed but expand enabled → no info
         */
        @Test
        @DisplayName("5.7 R-V7: expandIgnoreBidirectional=false + expand=true → no info")
        void rv7_expandIgnoreBidirectionalChanged_withExpand_noInfo() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(true)
                    .expandIgnoreBidirectional(false)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandIgnoreBidirectional")));
        }

        /**
         * Spec R-V7: expandIgnoreBidirectional changed but expandGlobal enabled → no info
         */
        @Test
        @DisplayName("5.8 R-V7: expandIgnoreBidirectional=false + expandGlobal=true → no info")
        void rv7_expandIgnoreBidirectionalChanged_withExpandGlobal_noInfo() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(false)
                    .expandGlobal(true)
                    .expandIgnoreBidirectional(false)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("expandIgnoreBidirectional")));
        }

        /**
         * Spec: Default config passes validation cleanly
         */
        @Test
        @DisplayName("5.9 default config passes validation cleanly")
        void defaultConfig_passesCleanly() {
            ReferenceConfig config = ReferenceConfig.defaults();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }

        /**
         * Spec: validate() returns same instance
         */
        @Test
        @DisplayName("5.10 validate() returns same config instance")
        void validate_returnsSameInstance() {
            ReferenceConfig config = ReferenceConfig.defaults();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            ReferenceConfig validated = config.validate(diagnostics);

            assertSame(config, validated);
        }

        /**
         * Spec: Multiple validation rules triggered simultaneously
         */
        @Test
        @DisplayName("5.11 multiple validation rules produce multiple diagnostics")
        void multipleRules_multipleDiagnostics() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refTypeKey("customType")   // R-V5
                    .expandDepth(3)              // R-V6
                    .expandIgnoreBidirectional(false) // R-V7
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertTrue(diagnostics.getWarningCount() >= 3,
                "At least 3 diagnostics: R-V5, R-V6, R-V7");
        }
    }

    // ========================================================================
    // Section 6: Custom Value Reader/Writer
    // ========================================================================

    @Nested
    @DisplayName("6. Custom Value Reader/Writer")
    class CustomValueReaderWriter {

        @Test
        @DisplayName("6.1 valueWriterName is carried through config")
        void valueWriterName_carriedThroughConfig() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .valueWriterName("customRefWriter")
                    .build();

            assertEquals("customRefWriter", config.getValueWriterName());
        }

        @Test
        @DisplayName("6.2 valueReaderName is carried through config")
        void valueReaderName_carriedThroughConfig() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .valueReaderName("customRefReader")
                    .build();

            assertEquals("customRefReader", config.getValueReaderName());
        }

        @Test
        @DisplayName("6.3 both reader and writer can be set simultaneously")
        void bothReaderAndWriter_canBeSet() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .valueWriterName("refWriter")
                    .valueReaderName("refReader")
                    .build();

            assertEquals("refWriter", config.getValueWriterName());
            assertEquals("refReader", config.getValueReaderName());
        }

        @Test
        @DisplayName("6.4 custom reader/writer don't cause validation errors")
        void customNames_noValidationErrors() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .valueWriterName("myWriter")
                    .valueReaderName("myReader")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }
    }

    // ========================================================================
    // Section 7: Merge Behavior
    // Spec reference: 02-config-resolution.md "Configuration Resolution"
    // ========================================================================

    @Nested
    @DisplayName("7. Merge Behavior (spec: Configuration Resolution)")
    class MergeBehavior {

        /**
         * Spec: "Higher priority sources override lower ones"
         */
        @Test
        @DisplayName("7.1 property map overrides existing values")
        void propertyMap_overridesExistingValues() {
            ReferenceConfig base = ReferenceConfig.builder()
                    .refKey("original")
                    .expand(false)
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.refKey", "overridden");
            override.put("codec.expand", true);

            ReferenceConfig merged = base.mergeWith(override);

            assertEquals("overridden", merged.getRefKey());
            assertTrue(merged.isExpand());
        }

        /**
         * Spec: Non-overridden values are preserved from base
         */
        @Test
        @DisplayName("7.2 non-overridden values preserved from base")
        void nonOverriddenValues_preservedFromBase() {
            ReferenceConfig base = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .refKey("customRef")
                    .expandDepth(2)
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.expand", true);

            ReferenceConfig merged = base.mergeWith(override);

            // Overridden
            assertTrue(merged.isExpand());
            // Preserved
            assertEquals(SerializationFormat.PLAIN, merged.getFormat());
            assertEquals("customRef", merged.getRefKey());
            assertEquals(2, merged.getExpandDepth());
        }

        /**
         * Spec: Empty or null source should not change config
         */
        @Test
        @DisplayName("7.3 empty property map returns same config")
        void emptyPropertyMap_returnsSameConfig() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .expand(true)
                    .build();

            ReferenceConfig merged = config.mergeWith(new HashMap<>());
            assertSame(config, merged);

            merged = config.mergeWith(null);
            assertSame(config, merged);
        }

        /**
         * Spec: Cascading merge chain (annotations → module → factory → options)
         */
        @Test
        @DisplayName("7.4 cascading merge: later layers override earlier")
        void cascadingMerge_laterLayersOverride() {
            ReferenceConfig config = ReferenceConfig.defaults()
                    .mergeWith(Map.of("codec.refKey", "annotationRef"))
                    .mergeWith(Map.of("codec.expand", true))
                    .mergeWith(Map.of("codec.refKey", "optionsRef"));

            assertEquals("optionsRef", config.getRefKey());
            assertTrue(config.isExpand());
        }

        /**
         * Spec: String values accepted for enums
         */
        @Test
        @DisplayName("7.5 enum values accepted as strings")
        void enumValues_acceptedAsStrings() {
            ReferenceConfig base = ReferenceConfig.defaults();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.refFormat", "PLAIN");

            ReferenceConfig merged = base.mergeWith(override);

            assertEquals(SerializationFormat.PLAIN, merged.getFormat());
        }

        /**
         * Spec: Boolean values from property map
         */
        @Test
        @DisplayName("7.6 boolean values merge correctly")
        void booleanValues_mergeCorrectly() {
            ReferenceConfig base = ReferenceConfig.defaults();
            assertFalse(base.isExpand());
            assertFalse(base.isExpandGlobal());

            Map<String, Object> override = new HashMap<>();
            override.put("codec.expand", true);
            override.put("codec.expandGlobal", true);

            ReferenceConfig merged = base.mergeWith(override);

            assertTrue(merged.isExpand());
            assertTrue(merged.isExpandGlobal());
        }

        /**
         * Spec: Integer values merge correctly
         */
        @Test
        @DisplayName("7.7 integer values merge correctly")
        void integerValues_mergeCorrectly() {
            ReferenceConfig base = ReferenceConfig.defaults();
            assertEquals(1, base.getExpandDepth());

            Map<String, Object> override = new HashMap<>();
            override.put("codec.expandDepth", 3);

            ReferenceConfig merged = base.mergeWith(override);

            assertEquals(3, merged.getExpandDepth());
        }

        /**
         * Spec: All reference properties can be merged
         */
        @Test
        @DisplayName("7.8 all reference properties can be set via property map")
        void allProperties_viaMerge() {
            ReferenceConfig base = ReferenceConfig.defaults();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.refFormat", "PLAIN");
            override.put("codec.refKey", "id");
            override.put("codec.refTypeKey", "type");
            override.put("codec.proxyKey", "isProxy");
            override.put("codec.expand", true);
            override.put("codec.expandGlobal", true);
            override.put("codec.expandDepth", 2);
            override.put("codec.expandIgnoreBidirectional", false);
            override.put("codec.serializeInstanceType", false);
            override.put("codec.valueReaderName", "myReader");
            override.put("codec.valueWriterName", "myWriter");

            ReferenceConfig merged = base.mergeWith(override);

            assertEquals(SerializationFormat.PLAIN, merged.getFormat());
            assertEquals("id", merged.getRefKey());
            assertEquals("type", merged.getRefTypeKey());
            assertEquals("isProxy", merged.getProxyKey());
            assertTrue(merged.isExpand());
            assertTrue(merged.isExpandGlobal());
            assertEquals(2, merged.getExpandDepth());
            assertFalse(merged.isExpandIgnoreBidirectional());
            assertFalse(merged.isSerializeInstanceType());
            assertEquals("myReader", merged.getValueReaderName());
            assertEquals("myWriter", merged.getValueWriterName());
        }

        /**
         * Spec: Higher-priority valueWriterName overrides lower-priority
         */
        @Test
        @DisplayName("7.9 higher-priority valueWriterName overrides lower-priority")
        void valueWriterName_higherPriorityOverrides() {
            ReferenceConfig base = ReferenceConfig.builder()
                    .valueWriterName("annotationWriter")
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.valueWriterName", "runtimeWriter");

            ReferenceConfig merged = base.mergeWith(override);

            assertEquals("runtimeWriter", merged.getValueWriterName());
        }
    }

    // ========================================================================
    // Section 8: Format × Expand Combinations
    // Spec reference: 10-reference.md section 5
    // ========================================================================

    @Nested
    @DisplayName("8. Format × Expand Combinations (spec section 5)")
    class FormatExpandCombinations {

        /**
         * Spec: STRUCTURED + expand=false → normal proxy serialization (default)
         */
        @Test
        @DisplayName("8.1 STRUCTURED + no expand: default proxy format")
        void structured_noExpand_defaultProxy() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .expand(false)
                    .build();

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertFalse(config.shouldExpand());
        }

        /**
         * Spec: STRUCTURED + expand=true → inline full object
         */
        @Test
        @DisplayName("8.2 STRUCTURED + expand=true: inline expansion")
        void structured_expand_inlineExpansion() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .expand(true)
                    .build();

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertTrue(config.shouldExpand());
        }

        /**
         * Spec: PLAIN + expand=false → bare string reference
         */
        @Test
        @DisplayName("8.3 PLAIN + no expand: bare string reference")
        void plain_noExpand_bareString() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .expand(false)
                    .build();

            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertFalse(config.shouldExpand());
        }

        /**
         * Spec: PLAIN + expand=true → expand overrides format behavior
         */
        @Test
        @DisplayName("8.4 PLAIN + expand=true: expand takes precedence")
        void plain_expand_expandTakesPrecedence() {
            ReferenceConfig config = ReferenceConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .expand(true)
                    .build();

            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertTrue(config.shouldExpand());
        }
    }
}
