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

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based tests for {@link SuperTypeConfig}.
 * <p>
 * These tests are derived directly from the codec-v2 specification documents:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/07-supertype.md} - SuperType Serialization specification</li>
 *   <li>{@code docs/codec-v2-spec/16-annotation-reference.md} - Annotation Reference</li>
 * </ul>
 * <p>
 * Test organization:
 * <ul>
 *   <li>Section 1: Default values (from spec section 6.1)</li>
 *   <li>Section 2: Format-dependent key defaults</li>
 *   <li>Section 3: Validation rules (from spec section 6.0)</li>
 *   <li>Section 4: SuperTypeStrategy values</li>
 *   <li>Section 5: Merge behavior</li>
 * </ul>
 */
@DisplayName("SuperTypeConfig Spec Tests")
class SuperTypeConfigSpecTest {

    // ========================================================================
    // Section 1: Default Values
    // Spec reference: 07-supertype.md section 6.1 "Configuration Keys"
    // ========================================================================

    @Nested
    @DisplayName("1. Default Values (spec section 6.1)")
    class DefaultValues {

        /**
         * Spec: "superTypeSerialize | codec.superTypeSerialize | false"
         * SuperType is disabled by default.
         */
        @Test
        @DisplayName("1.1 superTypeSerialize defaults to false")
        void superTypeSerialize_defaultsToFalse() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            assertFalse(config.isSerialize());
        }

        /**
         * Spec: "superTypeStrategy | codec.superTypeStrategy | ALL"
         */
        @Test
        @DisplayName("1.2 superTypeStrategy defaults to ALL")
        void superTypeStrategy_defaultsToAll() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            assertEquals(SuperTypeSelection.ALL, config.getStrategy());
        }

        /**
         * Spec: "superTypeAsArray | codec.superTypeAsArray | true"
         */
        @Test
        @DisplayName("1.3 superTypeAsArray defaults to true")
        void superTypeAsArray_defaultsToTrue() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            assertTrue(config.isAsArray());
        }

        /**
         * Spec: "superTypeSeparator | codec.superTypeSeparator | ,"
         */
        @Test
        @DisplayName("1.4 superTypeSeparator defaults to comma")
        void superTypeSeparator_defaultsToComma() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            assertEquals(",", config.getSeparator());
        }

        /**
         * Spec: "superTypeFormat | codec.superTypeFormat | (inherits from typeFormat)"
         * When not set, format should be null (meaning inherit from TypeConfig).
         */
        @Test
        @DisplayName("1.5 superTypeFormat defaults to null (inherits from typeFormat)")
        void superTypeFormat_defaultsToNull() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            assertNull(config.getFormat());
        }

        /**
         * Spec: "superTypeKey | codec.superTypeKey | (format-dependent)"
         * When not explicitly set, key is null (resolver applies format-dependent default).
         */
        @Test
        @DisplayName("1.6 superTypeKey defaults to null (format-dependent)")
        void superTypeKey_defaultsToNull() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            assertNull(config.getSuperTypeKey());
        }

        /**
         * Spec: valueReaderName and valueWriterName default to null
         */
        @Test
        @DisplayName("1.7 valueReaderName and valueWriterName default to null")
        void valueReaderWriter_defaultToNull() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            assertNull(config.getValueReaderName());
            assertNull(config.getValueWriterName());
        }
    }

    // ========================================================================
    // Section 2: Format-Dependent Key Defaults
    // Spec reference: 07-supertype.md section 6.1 "superTypeKey default value"
    // ========================================================================

    @Nested
    @DisplayName("2. Format-Dependent Key Defaults (spec section 6.1)")
    class FormatDependentKeyDefaults {

        /**
         * Spec: "PLAIN format: _supertype (underscore prefix for root-level metadata)"
         */
        @Test
        @DisplayName("2.1 getEffectiveSuperTypeKey returns '_supertype' for PLAIN format")
        void effectiveKey_plainFormat_returnsUnderscoreSupertype() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            String effectiveKey = config.getEffectiveSuperTypeKey(SerializationFormat.PLAIN);
            assertEquals("_supertype", effectiveKey);
        }

        /**
         * Spec: "STRUCTURED format: supertype (no underscore inside _type object)"
         */
        @Test
        @DisplayName("2.2 getEffectiveSuperTypeKey returns 'supertype' for STRUCTURED format")
        void effectiveKey_structuredFormat_returnsSupertype() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            String effectiveKey = config.getEffectiveSuperTypeKey(SerializationFormat.STRUCTURED);
            assertEquals("supertype", effectiveKey);
        }

        /**
         * Spec: "When explicitly set, the configured value is used regardless of format."
         */
        @Test
        @DisplayName("2.3 Explicit superTypeKey overrides format-dependent default")
        void explicitKey_overridesDefault() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .superTypeKey("customSuperType")
                    .build();

            assertEquals("customSuperType", config.getEffectiveSuperTypeKey(SerializationFormat.PLAIN));
            assertEquals("customSuperType", config.getEffectiveSuperTypeKey(SerializationFormat.STRUCTURED));
        }

        /**
         * Constants should match spec values
         */
        @Test
        @DisplayName("2.4 Constants match spec values")
        void constants_matchSpecValues() {
            assertEquals("_supertype", SuperTypeConfig.DEFAULT_KEY_PLAIN);
            assertEquals("supertype", SuperTypeConfig.DEFAULT_KEY_STRUCTURED);
        }
    }

    // ========================================================================
    // Section 3: Validation Rules
    // Spec reference: 07-supertype.md section 6.0 "Configuration Constraints"
    // ========================================================================

    @Nested
    @DisplayName("3. Validation Rules (spec section 6.0)")
    class ValidationRules {

        /**
         * Spec constraint: "separator is only meaningful when asArray=false"
         * When asArray=true (default), custom separator produces WARNING.
         */
        @Test
        @DisplayName("3.1 Custom separator with asArray=true produces WARNING")
        void customSeparator_asArrayTrue_producesWarning() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .asArray(true)  // default
                    .separator("|") // non-default separator
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                "Expected WARNING: separator is ignored when asArray=true");
            assertTrue(diagnostics.getWarnings().stream()
                    .anyMatch(d -> d.getMessage().contains("superTypeSeparator")));
        }

        /**
         * Default separator with asArray=true should NOT produce warning
         */
        @Test
        @DisplayName("3.2 Default separator with asArray=true produces no WARNING")
        void defaultSeparator_asArrayTrue_noWarning() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .asArray(true)
                    // separator keeps default ","
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                "Default separator with asArray=true should not produce warning");
        }

        /**
         * Spec: Custom separator with asArray=false is valid
         */
        @Test
        @DisplayName("3.3 Custom separator with asArray=false produces no WARNING")
        void customSeparator_asArrayFalse_noWarning() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .asArray(false)
                    .separator("|")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                "Custom separator with asArray=false should be valid");
        }

        /**
         * Spec: Valid configuration should produce no warnings
         */
        @Test
        @DisplayName("3.4 Valid config produces no warnings")
        void validConfig_noWarnings() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.ALL)
                    .asArray(true)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
            assertFalse(diagnostics.hasErrors());
        }

        /**
         * Note: The constraint "STRUCTURED + typeStrategy=NONE + superTypeSerialize=true → ERROR"
         * must be validated at a higher level where TypeConfig is also available.
         * SuperTypeConfig alone cannot validate this - it needs TypeConfig's strategy.
         */
        @Test
        @DisplayName("3.5 Cross-config validation note: STRUCTURED + NONE requires higher-level validation")
        void crossConfigValidation_documentedAsHigherLevel() {
            // This test documents that SuperTypeConfig.validate() cannot detect
            // the invalid combination of typeStrategy=NONE + superTypeSerialize=true
            // because it doesn't have access to TypeConfig.
            //
            // The spec says: "STRUCTURED format + typeStrategy=NONE + superTypeSerialize=true → ERROR"
            // This validation must happen in a combined validator or during effective config resolution.

            SuperTypeConfig config = SuperTypeConfig.builder()
                    .serialize(true)
                    .format(SerializationFormat.STRUCTURED)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            // SuperTypeConfig alone cannot detect this error
            // The error will be caught at effective config resolution time
            assertFalse(diagnostics.hasErrors(),
                "SuperTypeConfig alone cannot validate typeStrategy dependency");
        }
    }

    // ========================================================================
    // Section 4: SuperTypeStrategy Values
    // Spec reference: 07-supertype.md section 5 "SuperType Strategy"
    // ========================================================================

    @Nested
    @DisplayName("4. SuperTypeStrategy Values (spec section 5)")
    class SuperTypeStrategyValues {

        /**
         * Spec: "ALL (default) - All domain model supertypes"
         */
        @Test
        @DisplayName("4.1 ALL is the default strategy")
        void all_isDefaultStrategy() {
            SuperTypeConfig config = SuperTypeConfig.defaults();
            assertEquals(SuperTypeSelection.ALL, config.getStrategy());
        }

        /**
         * Spec: "ALL_EMF - All supertypes including EMF base classes"
         */
        @Test
        @DisplayName("4.2 ALL_EMF strategy is valid")
        void allEmf_isValid() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .strategy(SuperTypeSelection.ALL_EMF)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertEquals(SuperTypeSelection.ALL_EMF, config.getStrategy());
            assertFalse(diagnostics.hasErrors());
        }

        /**
         * Spec: "SINGLE - Only the immediate/direct supertype"
         */
        @Test
        @DisplayName("4.3 SINGLE strategy is valid")
        void single_isValid() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .strategy(SuperTypeSelection.SINGLE)
                    .build();

            assertEquals(SuperTypeSelection.SINGLE, config.getStrategy());
        }

        /**
         * Spec: "NONE - No supertypes (equivalent to superTypeSerialize=false)"
         */
        @Test
        @DisplayName("4.4 NONE strategy is valid")
        void none_isValid() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .strategy(SuperTypeSelection.NONE)
                    .build();

            assertEquals(SuperTypeSelection.NONE, config.getStrategy());
        }

        /**
         * All SuperTypeSelection values should be valid
         */
        @Test
        @DisplayName("4.5 All SuperTypeSelection values are valid")
        void allValues_areValid() {
            for (SuperTypeSelection selection : SuperTypeSelection.values()) {
                SuperTypeConfig config = SuperTypeConfig.builder()
                        .strategy(selection)
                        .build();

                DiagnosticCollector diagnostics = new DiagnosticCollector();
                config.validate(diagnostics);

                assertFalse(diagnostics.hasErrors(),
                    "Strategy " + selection + " should be valid");
            }
        }
    }

    // ========================================================================
    // Section 5: Merge Behavior
    // Spec reference: 16-annotation-reference.md "Configuration Resolution"
    // ========================================================================

    @Nested
    @DisplayName("5. Merge Behavior (spec: Configuration Resolution)")
    class MergeBehavior {

        /**
         * Spec: Property map values should override existing config values.
         */
        @Test
        @DisplayName("5.1 Property map overrides existing values")
        void propertyMap_overridesExistingValues() {
            SuperTypeConfig base = SuperTypeConfig.builder()
                    .serialize(false)
                    .strategy(SuperTypeSelection.ALL)
                    .asArray(true)
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.superTypeSerialize", true);
            override.put("codec.superTypeStrategy", "SINGLE");
            override.put("codec.superTypeAsArray", false);

            SuperTypeConfig merged = base.mergeWith(override);

            assertTrue(merged.isSerialize());
            assertEquals(SuperTypeSelection.SINGLE, merged.getStrategy());
            assertFalse(merged.isAsArray());
        }

        /**
         * Spec: Empty or null source should not change config
         */
        @Test
        @DisplayName("5.2 Empty property map returns same config")
        void emptyPropertyMap_returnsSameConfig() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .serialize(true)
                    .build();

            SuperTypeConfig merged = config.mergeWith(new HashMap<>());
            assertSame(config, merged);

            merged = config.mergeWith(null);
            assertSame(config, merged);
        }

        /**
         * Spec: Enum values accepted as strings (case-insensitive)
         */
        @Test
        @DisplayName("5.3 Enum values accepted as strings (case-insensitive)")
        void enumValues_acceptedAsStrings() {
            SuperTypeConfig base = SuperTypeConfig.defaults();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.superTypeStrategy", "single");  // lowercase

            SuperTypeConfig merged = base.mergeWith(override);

            assertEquals(SuperTypeSelection.SINGLE, merged.getStrategy());
        }

        /**
         * Boolean values accepted as strings
         */
        @Test
        @DisplayName("5.4 Boolean values accepted as strings")
        void booleanValues_acceptedAsStrings() {
            SuperTypeConfig base = SuperTypeConfig.defaults();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.superTypeSerialize", "true");
            override.put("codec.superTypeAsArray", "false");

            SuperTypeConfig merged = base.mergeWith(override);

            assertTrue(merged.isSerialize());
            assertFalse(merged.isAsArray());
        }
    }

    // ========================================================================
    // Section 6: Format Behavior
    // Spec reference: 07-supertype.md sections 2-3
    // ========================================================================

    @Nested
    @DisplayName("6. Format Behavior (spec sections 2-3)")
    class FormatBehavior {

        /**
         * Spec section 2: "PLAIN Format - SuperType is a standalone field at root level"
         */
        @Test
        @DisplayName("6.1 PLAIN format config is valid")
        void plainFormat_isValid() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .superTypeKey("_supertype")
                    .build();

            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertEquals("_supertype", config.getSuperTypeKey());
        }

        /**
         * Spec section 3: "STRUCTURED Format - SuperType inside _type object"
         */
        @Test
        @DisplayName("6.2 STRUCTURED format config is valid")
        void structuredFormat_isValid() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .superTypeKey("supertype")
                    .build();

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals("supertype", config.getSuperTypeKey());
        }

        /**
         * Spec: Both ARRAY and STRING presentation are valid
         */
        @Test
        @DisplayName("6.3 STRING presentation (asArray=false) is valid")
        void stringPresentation_isValid() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .asArray(false)
                    .separator("|")
                    .build();

            assertFalse(config.isAsArray());
            assertEquals("|", config.getSeparator());
        }
    }
}
