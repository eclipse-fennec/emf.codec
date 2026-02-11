
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.IdKeyMode;
import org.eclipse.fennec.model.metadata.IdStrategy;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based tests for {@link IdConfig}.
 * <p>
 * These tests are derived directly from the codec-v2 specification documents:
 * <ul>
 *   <li>{@code docs/codec-v2-spec/09-id.md} - ID Serialization specification</li>
 *   <li>{@code docs/codec-v2-spec/16-annotation-reference.md} - Annotation Reference</li>
 * </ul>
 * <p>
 * Test organization:
 * <ul>
 *   <li>Section 1: Default values (from spec §12)</li>
 *   <li>Section 2: IdStrategy values (from spec §5.0)</li>
 *   <li>Section 3: IdKeyMode values (from spec §2)</li>
 *   <li>Section 4: Format × Strategy combinations (from spec §3)</li>
 *   <li>Section 5: Validation rules (from spec §13.1)</li>
 *   <li>Section 6: Merge behavior (from spec: Configuration Resolution)</li>
 *   <li>Section 7: ValueWriter/ValueReader flow effectiveness (from spec §10, §11)</li>
 *   <li>Section 8: idOnTop ordering constraint (from spec §8.7)</li>
 * </ul>
 */
@DisplayName("IdConfig Spec Tests")
class IdConfigSpecTest {

    // ========================================================================
    // Section 1: Default Values
    // Spec reference: 09-id.md §12 "Default ID Settings"
    // ========================================================================

    @Nested
    @DisplayName("1. Default Values (spec §12)")
    class DefaultValues {

        /**
         * Spec §12: "Strategy | codec.idStrategy | ID_FIELD"
         */
        @Test
        @DisplayName("1.1 idStrategy defaults to ID_FIELD")
        void idStrategy_defaultsToIdField() {
            IdConfig config = IdConfig.defaults();
            assertEquals(IdStrategy.ID_FIELD, config.getStrategy());
        }

        /**
         * Spec §12: "Format | codec.idFormat | PLAIN"
         */
        @Test
        @DisplayName("1.2 idFormat defaults to PLAIN")
        void idFormat_defaultsToPlain() {
            IdConfig config = IdConfig.defaults();
            assertEquals(SerializationFormat.PLAIN, config.getFormat());
        }

        /**
         * Spec §12: "ID Key | codec.idKey | _id"
         */
        @Test
        @DisplayName("1.3 idKey defaults to '_id'")
        void idKey_defaultsToUnderscore() {
            IdConfig config = IdConfig.defaults();
            assertEquals("_id", config.getKey());
        }

        /**
         * Spec §12: "Value Key | codec.idValueKey | id"
         */
        @Test
        @DisplayName("1.4 idValueKey defaults to 'id'")
        void idValueKey_defaultsToId() {
            IdConfig config = IdConfig.defaults();
            assertEquals("id", config.getValueKey());
        }

        /**
         * Spec §12: "Separator | codec.idSeparator | -"
         */
        @Test
        @DisplayName("1.5 idSeparator defaults to '-'")
        void idSeparator_defaultsToHyphen() {
            IdConfig config = IdConfig.defaults();
            assertEquals("-", config.getSeparator());
        }

        /**
         * Spec §12: "Separator Key | codec.idSeparatorKey | separator"
         */
        @Test
        @DisplayName("1.6 idSeparatorKey defaults to 'separator'")
        void idSeparatorKey_defaultsToSeparator() {
            IdConfig config = IdConfig.defaults();
            assertEquals("separator", config.getSeparatorKey());
        }

        /**
         * Spec §12: "Key Mode | codec.idKeyMode | ID_ONLY"
         */
        @Test
        @DisplayName("1.7 idKeyMode defaults to ID_ONLY")
        void idKeyMode_defaultsToIdOnly() {
            IdConfig config = IdConfig.defaults();
            assertEquals(IdKeyMode.ID_ONLY, config.getKeyMode());
        }

        /**
         * Spec §12: "Serialize Separator | codec.idSeparatorSerialize | true"
         */
        @Test
        @DisplayName("1.8 idSeparatorSerialize defaults to true")
        void idSeparatorSerialize_defaultsToTrue() {
            IdConfig config = IdConfig.defaults();
            assertTrue(config.isSerializeSeparator());
        }

        /**
         * Spec §12: "ID on Top | codec.idOnTop | false"
         */
        @Test
        @DisplayName("1.9 idOnTop defaults to false")
        void idOnTop_defaultsToFalse() {
            IdConfig config = IdConfig.defaults();
            assertFalse(config.isOnTop());
        }

        /**
         * Spec §5.0: "idFeatures" defaults to empty list (resolved from eID at runtime)
         */
        @Test
        @DisplayName("1.10 idFeatures defaults to empty list")
        void idFeatures_defaultsToEmptyList() {
            IdConfig config = IdConfig.defaults();
            assertEquals(List.of(), config.getIdFeatures());
        }

        /**
         * Spec §5.0: valueWriterName/valueReaderName default to null (no custom writer/reader)
         */
        @Test
        @DisplayName("1.11 valueWriterName and valueReaderName default to null")
        void valueWriterReader_defaultToNull() {
            IdConfig config = IdConfig.defaults();
            assertNull(config.getValueWriterName());
            assertNull(config.getValueReaderName());
        }
    }

    // ========================================================================
    // Section 2: IdStrategy Values
    // Spec reference: 09-id.md §5.0 "IdStrategy values"
    // ========================================================================

    @Nested
    @DisplayName("2. IdStrategy Values (spec §5.0)")
    class IdStrategyValues {

        /**
         * Spec §5.0: "ID_FIELD (default) | Use EMF eID attribute(s)"
         */
        @Test
        @DisplayName("2.1 ID_FIELD strategy uses eID attributes")
        void idFieldStrategy_usesEidAttributes() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.ID_FIELD)
                    .build();

            assertEquals(IdStrategy.ID_FIELD, config.getStrategy());
            // No idFeatures required for ID_FIELD — resolved from eID at runtime
            assertEquals(List.of(), config.getIdFeatures());
        }

        /**
         * Spec §5.0: "COMBINED | Combine multiple features with separator"
         * Spec §3.2: COMBINED requires idFeatures and uses idSeparator
         */
        @Test
        @DisplayName("2.2 COMBINED strategy requires idFeatures and separator")
        void combinedStrategy_requiresFeaturesAndSeparator() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of("firstName", "lastName", "sequence"))
                    .separator("-")
                    .build();

            assertEquals(IdStrategy.COMBINED, config.getStrategy());
            assertEquals(List.of("firstName", "lastName", "sequence"), config.getIdFeatures());
            assertEquals("-", config.getSeparator());
        }

        /**
         * Spec §5.0: COMBINED with empty idFeatures is invalid
         * Spec §13.1: Validated in IdConfig.validate()
         */
        @Test
        @DisplayName("2.3 COMBINED strategy with empty idFeatures produces ERROR")
        void combinedStrategy_emptyFeatures_producesError() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of())
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors(),
                "COMBINED strategy with empty idFeatures must produce ERROR");
            assertTrue(diagnostics.getErrors().get(0).getMessage().contains("idFeatures"));
        }

        /**
         * Spec §5.0: ID_FIELD strategy with empty idFeatures is valid
         * (features resolved from eID at runtime)
         */
        @Test
        @DisplayName("2.4 ID_FIELD strategy with empty idFeatures is valid")
        void idFieldStrategy_emptyFeatures_isValid() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.ID_FIELD)
                    .idFeatures(List.of())
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
        }

        /**
         * Spec: Both strategies are valid for both PLAIN and STRUCTURED formats
         */
        @Test
        @DisplayName("2.5 All IdStrategy values are valid for both formats")
        void allStrategies_validForBothFormats() {
            for (IdStrategy strategy : IdStrategy.values()) {
                for (SerializationFormat format : SerializationFormat.values()) {
                    IdConfig config = IdConfig.builder()
                            .strategy(strategy)
                            .format(format)
                            .idFeatures(strategy == IdStrategy.COMBINED
                                    ? List.of("f1", "f2") : List.of())
                            .build();

                    DiagnosticCollector diagnostics = new DiagnosticCollector();
                    config.validate(diagnostics);

                    assertFalse(diagnostics.hasErrors(),
                        "Strategy " + strategy + " with format " + format + " should be valid");
                }
            }
        }
    }

    // ========================================================================
    // Section 3: IdKeyMode Values
    // Spec reference: 09-id.md §2 "ID Key Mode"
    // ========================================================================

    @Nested
    @DisplayName("3. IdKeyMode Values (spec §2)")
    class IdKeyModeValues {

        /**
         * Spec §2: "ID_ONLY (default) | Only _id key"
         */
        @Test
        @DisplayName("3.1 ID_ONLY mode: only _id key")
        void idOnlyMode() {
            IdConfig config = IdConfig.builder()
                    .keyMode(IdKeyMode.ID_ONLY)
                    .build();

            assertEquals(IdKeyMode.ID_ONLY, config.getKeyMode());
        }

        /**
         * Spec §2: "BOTH | _id + original feature names"
         */
        @Test
        @DisplayName("3.2 BOTH mode: _id + feature names")
        void bothMode() {
            IdConfig config = IdConfig.builder()
                    .keyMode(IdKeyMode.BOTH)
                    .build();

            assertEquals(IdKeyMode.BOTH, config.getKeyMode());
        }

        /**
         * Spec §2: "FEATURE_ONLY | Only original feature names"
         */
        @Test
        @DisplayName("3.3 FEATURE_ONLY mode: only feature names")
        void featureOnlyMode() {
            IdConfig config = IdConfig.builder()
                    .keyMode(IdKeyMode.FEATURE_ONLY)
                    .build();

            assertEquals(IdKeyMode.FEATURE_ONLY, config.getKeyMode());
        }

        /**
         * Spec §2: "NONE | No ID serialization"
         * "When IdKeyMode=NONE, the IdStrategy is ignored — no ID serialization happens at all."
         */
        @Test
        @DisplayName("3.4 NONE mode: no ID serialization (hard disable)")
        void noneMode_hardDisable() {
            IdConfig config = IdConfig.builder()
                    .keyMode(IdKeyMode.NONE)
                    .strategy(IdStrategy.COMBINED) // strategy is irrelevant with NONE
                    .idFeatures(List.of("f1", "f2"))
                    .build();

            assertEquals(IdKeyMode.NONE, config.getKeyMode());
            // Strategy is stored but will be ignored at runtime due to NONE
            assertEquals(IdStrategy.COMBINED, config.getStrategy());
        }

        /**
         * Spec §2: All four IdKeyMode values can be set with any format
         */
        @Test
        @DisplayName("3.5 All IdKeyMode values are valid for both formats")
        void allKeyModes_validForBothFormats() {
            for (IdKeyMode keyMode : IdKeyMode.values()) {
                for (SerializationFormat format : SerializationFormat.values()) {
                    IdConfig config = IdConfig.builder()
                            .keyMode(keyMode)
                            .format(format)
                            .build();

                    DiagnosticCollector diagnostics = new DiagnosticCollector();
                    config.validate(diagnostics);

                    assertFalse(diagnostics.hasErrors(),
                        "KeyMode " + keyMode + " with format " + format + " should be valid");
                }
            }
        }
    }

    // ========================================================================
    // Section 4: Format × Strategy Combinations
    // Spec reference: 09-id.md §3.1-3.4
    // ========================================================================

    @Nested
    @DisplayName("4. Format × Strategy Combinations (spec §3)")
    class FormatStrategyCombinations {

        /**
         * Spec §3.1: "Plain Format (default) - Single Feature"
         * Default config: PLAIN format, ID_FIELD strategy, ID_ONLY mode
         */
        @Test
        @DisplayName("4.1 PLAIN + ID_FIELD + ID_ONLY: default config")
        void plain_idField_idOnly() {
            IdConfig config = IdConfig.defaults();

            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertEquals(IdStrategy.ID_FIELD, config.getStrategy());
            assertEquals(IdKeyMode.ID_ONLY, config.getKeyMode());
            assertEquals("_id", config.getKey());
        }

        /**
         * Spec §3.2: "Plain Format - Multiple Features"
         * COMBINED strategy with separator
         */
        @Test
        @DisplayName("4.2 PLAIN + COMBINED: multiple features with separator")
        void plain_combined_multipleFeatures() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of("firstName", "lastName", "sequence"))
                    .separator("-")
                    .build();

            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertEquals(IdStrategy.COMBINED, config.getStrategy());
            assertEquals(List.of("firstName", "lastName", "sequence"), config.getIdFeatures());
            assertEquals("-", config.getSeparator());
        }

        /**
         * Spec §3.3: "Structured Format - Single Feature"
         * STRUCTURED format uses idValueKey for inner key
         */
        @Test
        @DisplayName("4.3 STRUCTURED + ID_FIELD: uses idValueKey for inner key")
        void structured_idField_usesValueKey() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(IdStrategy.ID_FIELD)
                    .valueKey("id")
                    .build();

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals("id", config.getValueKey());
            assertEquals("_id", config.getKey()); // outer key unchanged
        }

        /**
         * Spec §3.4: "Structured Format - Multiple Features"
         * STRUCTURED + COMBINED + separator
         */
        @Test
        @DisplayName("4.4 STRUCTURED + COMBINED: idValueKey + separator inside _id object")
        void structured_combined_valueKeyAndSeparator() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of("firstName", "lastName", "sequence"))
                    .separator("-")
                    .valueKey("id")
                    .separatorKey("separator")
                    .serializeSeparator(true)
                    .build();

            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals(IdStrategy.COMBINED, config.getStrategy());
            assertEquals("id", config.getValueKey());
            assertEquals("separator", config.getSeparatorKey());
            assertTrue(config.isSerializeSeparator());
        }

        /**
         * Spec §5.1: "With idSeparatorSerialize=false" → no separator in output
         */
        @Test
        @DisplayName("4.5 COMBINED + serializeSeparator=false: no separator field")
        void combined_serializeSeparatorFalse() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of("firstName", "lastName"))
                    .separator("-")
                    .serializeSeparator(false)
                    .build();

            assertFalse(config.isSerializeSeparator());
        }

        /**
         * Spec §3: Custom separator value
         */
        @Test
        @DisplayName("4.6 Custom separator value (e.g. '_')")
        void customSeparator() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of("userGroup", "userId"))
                    .separator("_")
                    .build();

            assertEquals("_", config.getSeparator());
        }
    }

    // ========================================================================
    // Section 5: Validation Rules
    // Spec reference: 09-id.md §13.1 "Property Applicability Rules"
    // ========================================================================

    @Nested
    @DisplayName("5. Validation Rules (spec §13.1)")
    class ValidationRules {

        /**
         * Spec: COMBINED + empty idFeatures = ERROR
         * Covered by IdConfig.validate()
         */
        @Test
        @DisplayName("5.1 COMBINED with empty idFeatures: ERROR")
        void combined_emptyFeatures_error() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of())
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors());
            assertEquals(1, diagnostics.getErrorCount());
        }

        /**
         * Spec: idValueKey with PLAIN format and non-default value → WARNING
         * "idValueKey is only meaningful for STRUCTURED format"
         */
        @Test
        @DisplayName("5.2 Non-default idValueKey with PLAIN format: WARNING")
        void nonDefaultValueKey_plainFormat_warning() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .valueKey("customId")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("idValueKey"));
        }

        /**
         * Spec: Default idValueKey with PLAIN format → no warning
         */
        @Test
        @DisplayName("5.3 Default idValueKey with PLAIN format: no WARNING")
        void defaultValueKey_plainFormat_noWarning() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .valueKey("id")  // default
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        /**
         * Spec: Non-default idValueKey with STRUCTURED format → no warning (correct usage)
         */
        @Test
        @DisplayName("5.4 Non-default idValueKey with STRUCTURED format: no WARNING")
        void nonDefaultValueKey_structuredFormat_noWarning() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .valueKey("customId")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        /**
         * Spec: idSeparatorKey with PLAIN format and non-default value → WARNING
         * "idSeparatorKey is only meaningful for STRUCTURED format"
         */
        @Test
        @DisplayName("5.5 Non-default idSeparatorKey with PLAIN format: WARNING")
        void nonDefaultSeparatorKey_plainFormat_warning() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .separatorKey("customSep")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertTrue(diagnostics.getWarnings().get(0).getMessage().contains("idSeparatorKey"));
        }

        /**
         * Spec: Default separatorKey with PLAIN format → no warning
         */
        @Test
        @DisplayName("5.6 Default idSeparatorKey with PLAIN format: no WARNING")
        void defaultSeparatorKey_plainFormat_noWarning() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .separatorKey("separator")  // default
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings());
        }

        /**
         * Spec: Multiple validation issues collected together
         */
        @Test
        @DisplayName("5.7 Multiple warnings collected: idValueKey + idSeparatorKey with PLAIN")
        void multipleWarnings_collected() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .valueKey("customId")
                    .separatorKey("customSep")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings());
            assertEquals(2, diagnostics.getWarningCount());
        }

        /**
         * Spec: Both errors and warnings collected simultaneously
         */
        @Test
        @DisplayName("5.8 Errors and warnings collected: COMBINED empty + PLAIN custom valueKey")
        void errorsAndWarnings_collected() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of())
                    .format(SerializationFormat.PLAIN)
                    .valueKey("customId")
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertTrue(diagnostics.hasErrors());
            assertTrue(diagnostics.hasWarnings());
            assertEquals(1, diagnostics.getErrorCount());
            assertEquals(1, diagnostics.getWarningCount());
        }

        /**
         * Spec: Default config passes validation with no issues
         */
        @Test
        @DisplayName("5.9 Default config passes validation cleanly")
        void defaultConfig_passesCleanly() {
            IdConfig config = IdConfig.defaults();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }

        /**
         * Spec §3.4: STRUCTURED + COMBINED with all keys set → valid, no warnings
         */
        @Test
        @DisplayName("5.10 STRUCTURED + COMBINED fully configured: no issues")
        void structured_combined_fullyConfigured_valid() {
            IdConfig config = IdConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of("firstName", "lastName"))
                    .separator("-")
                    .valueKey("id")
                    .separatorKey("separator")
                    .serializeSeparator(true)
                    .build();

            DiagnosticCollector diagnostics = new DiagnosticCollector();
            config.validate(diagnostics);

            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }
    }

    // ========================================================================
    // Section 6: Merge Behavior
    // Spec reference: 16-annotation-reference.md "Configuration Resolution"
    // ========================================================================

    @Nested
    @DisplayName("6. Merge Behavior (spec: Configuration Resolution)")
    class MergeBehavior {

        /**
         * Spec: "Higher priority sources override lower ones"
         */
        @Test
        @DisplayName("6.1 Property map overrides existing values")
        void propertyMap_overridesExistingValues() {
            IdConfig base = IdConfig.builder()
                    .strategy(IdStrategy.ID_FIELD)
                    .key("_id")
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.idStrategy", "COMBINED");
            override.put("codec.idKey", "@id");
            override.put("codec.idFeatures", List.of("f1", "f2"));

            IdConfig merged = base.mergeWith(override);

            assertEquals(IdStrategy.COMBINED, merged.getStrategy());
            assertEquals("@id", merged.getKey());
            assertEquals(List.of("f1", "f2"), merged.getIdFeatures());
        }

        /**
         * Spec: Non-overridden values are preserved from base
         */
        @Test
        @DisplayName("6.2 Non-overridden values preserved from base")
        void nonOverriddenValues_preservedFromBase() {
            IdConfig base = IdConfig.builder()
                    .strategy(IdStrategy.COMBINED)
                    .idFeatures(List.of("a", "b"))
                    .separator("_")
                    .onTop(true)
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.idFormat", "STRUCTURED");

            IdConfig merged = base.mergeWith(override);

            // Overridden
            assertEquals(SerializationFormat.STRUCTURED, merged.getFormat());
            // Preserved
            assertEquals(IdStrategy.COMBINED, merged.getStrategy());
            assertEquals(List.of("a", "b"), merged.getIdFeatures());
            assertEquals("_", merged.getSeparator());
            assertTrue(merged.isOnTop());
        }

        /**
         * Spec: Cascading merge chain (annotations → module → factory → options)
         */
        @Test
        @DisplayName("6.3 Cascading merge: later layers override earlier")
        void cascadingMerge_laterLayersOverride() {
            IdConfig config = IdConfig.defaults()
                    .mergeWith(Map.of("idKey", "annotationId"))
                    .mergeWith(Map.of("idStrategy", "COMBINED"))
                    .mergeWith(Map.of("idOnTop", true))
                    .mergeWith(Map.of("idKey", "optionsId"));

            assertEquals("optionsId", config.getKey());         // latest overrides
            assertEquals(IdStrategy.COMBINED, config.getStrategy()); // preserved
            assertTrue(config.isOnTop());                        // preserved
        }

        /**
         * Spec: String values accepted for enums
         */
        @Test
        @DisplayName("6.4 Enum values accepted as strings")
        void enumValues_acceptedAsStrings() {
            IdConfig base = IdConfig.defaults();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.idStrategy", "COMBINED");
            override.put("codec.idFormat", "STRUCTURED");
            override.put("codec.idKeyMode", "BOTH");

            IdConfig merged = base.mergeWith(override);

            assertEquals(IdStrategy.COMBINED, merged.getStrategy());
            assertEquals(SerializationFormat.STRUCTURED, merged.getFormat());
            assertEquals(IdKeyMode.BOTH, merged.getKeyMode());
        }

        /**
         * Spec: Boolean values from property map
         */
        @Test
        @DisplayName("6.5 Boolean values merge correctly")
        void booleanValues_mergeCorrectly() {
            IdConfig base = IdConfig.defaults();
            assertFalse(base.isOnTop());
            assertTrue(base.isSerializeSeparator());

            Map<String, Object> override = new HashMap<>();
            override.put("codec.idOnTop", true);
            override.put("codec.idSeparatorSerialize", false);

            IdConfig merged = base.mergeWith(override);

            assertTrue(merged.isOnTop());
            assertFalse(merged.isSerializeSeparator());
        }

        /**
         * Spec: idKeyMode NONE can be set via merge
         */
        @Test
        @DisplayName("6.6 IdKeyMode.NONE can be set via property map")
        void idKeyModeNone_viaMerge() {
            IdConfig base = IdConfig.defaults();
            assertEquals(IdKeyMode.ID_ONLY, base.getKeyMode());

            IdConfig merged = base.mergeWith(Map.of("codec.idKeyMode", "NONE"));

            assertEquals(IdKeyMode.NONE, merged.getKeyMode());
        }
    }

    // ========================================================================
    // Section 7: ValueWriter/ValueReader Flow Effectiveness
    // Spec reference: 09-id.md §10 (ser flow) and §11 (deser flow)
    //
    // Flow priority (ser):  NONE check → ValueWriter (full delegation → DONE) → built-in
    // Flow priority (deser): NONE check → ValueReader (full delegation → DONE) → built-in
    // ========================================================================

    @Nested
    @DisplayName("7. ValueWriter/ValueReader Flow Effectiveness (spec §10, §11)")
    class ValueWriterReaderFlowEffectiveness {

        /**
         * Spec §10 step 2: "Is idValueWriterName configured?"
         */
        @Test
        @DisplayName("7.1 valueWriterName is carried through config")
        void valueWriterName_carriedThroughConfig() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.ID_FIELD)
                    .valueWriterName("myCustomIdWriter")
                    .build();

            assertEquals("myCustomIdWriter", config.getValueWriterName());
            assertEquals(IdStrategy.ID_FIELD, config.getStrategy());
        }

        /**
         * Spec §11 step 2: "Is idValueReaderName configured?"
         */
        @Test
        @DisplayName("7.2 valueReaderName is carried through config")
        void valueReaderName_carriedThroughConfig() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.ID_FIELD)
                    .valueReaderName("myCustomIdReader")
                    .build();

            assertEquals("myCustomIdReader", config.getValueReaderName());
            assertEquals(IdStrategy.ID_FIELD, config.getStrategy());
        }

        /**
         * Spec §10 step 1: "IdKeyMode=NONE → Skip ID serialization entirely"
         * "NONE is a hard disable. ValueWriter is also ignored."
         * Config can hold both; flow enforces priority.
         */
        @Test
        @DisplayName("7.3 NONE + valueWriterName: both stored (flow enforces priority)")
        void noneKeyMode_withValueWriterName_bothStored() {
            IdConfig config = IdConfig.builder()
                    .keyMode(IdKeyMode.NONE)
                    .valueWriterName("myIdWriter")
                    .build();

            assertEquals(IdKeyMode.NONE, config.getKeyMode());
            assertEquals("myIdWriter", config.getValueWriterName());
            // At runtime, NONE check happens first → writer never invoked
        }

        /**
         * Spec §11 step 1: "IdKeyMode=NONE → Skip ID deserialization entirely"
         * Config can hold both; flow enforces priority.
         */
        @Test
        @DisplayName("7.4 NONE + valueReaderName: both stored (flow enforces priority)")
        void noneKeyMode_withValueReaderName_bothStored() {
            IdConfig config = IdConfig.builder()
                    .keyMode(IdKeyMode.NONE)
                    .valueReaderName("myIdReader")
                    .build();

            assertEquals(IdKeyMode.NONE, config.getKeyMode());
            assertEquals("myIdReader", config.getValueReaderName());
            // At runtime, NONE check happens first → reader never invoked
        }

        /**
         * Spec: ValueWriter/ValueReader names merge from higher-priority property maps
         */
        @Test
        @DisplayName("7.5 valueWriterName/valueReaderName merge from property map")
        void valueWriterReaderName_mergeFromPropertyMap() {
            IdConfig base = IdConfig.builder()
                    .strategy(IdStrategy.ID_FIELD)
                    .build();

            assertNull(base.getValueWriterName());
            assertNull(base.getValueReaderName());

            Map<String, Object> override = new HashMap<>();
            override.put("codec.idValueWriterName", "runtimeIdWriter");
            override.put("codec.idValueReaderName", "runtimeIdReader");

            IdConfig merged = base.mergeWith(override);

            assertEquals("runtimeIdWriter", merged.getValueWriterName());
            assertEquals("runtimeIdReader", merged.getValueReaderName());
            assertEquals(IdStrategy.ID_FIELD, merged.getStrategy());
        }

        /**
         * Spec: Higher-priority writer overrides lower-priority
         */
        @Test
        @DisplayName("7.6 Higher-priority valueWriterName overrides lower-priority")
        void valueWriterName_higherPriorityOverrides() {
            IdConfig base = IdConfig.builder()
                    .strategy(IdStrategy.ID_FIELD)
                    .valueWriterName("annotationWriter")
                    .build();

            Map<String, Object> override = new HashMap<>();
            override.put("codec.idValueWriterName", "runtimeWriter");

            IdConfig merged = base.mergeWith(override);

            assertEquals("runtimeWriter", merged.getValueWriterName());
        }

        /**
         * Spec §10 step 2: ValueWriter provides full delegation regardless of strategy.
         * Config should accept writer with any strategy.
         */
        @Test
        @DisplayName("7.7 valueWriterName valid with any strategy (full delegation)")
        void valueWriterName_validWithAnyStrategy() {
            for (IdStrategy strategy : IdStrategy.values()) {
                IdConfig config = IdConfig.builder()
                        .strategy(strategy)
                        .idFeatures(strategy == IdStrategy.COMBINED
                                ? List.of("f1", "f2") : List.of())
                        .valueWriterName("customWriter")
                        .build();

                DiagnosticCollector diagnostics = new DiagnosticCollector();
                config.validate(diagnostics);

                assertEquals("customWriter", config.getValueWriterName(),
                    "valueWriterName should be stored with strategy " + strategy);
                assertFalse(diagnostics.hasErrors(),
                    "valueWriterName should not cause errors with strategy " + strategy);
            }
        }

        /**
         * Spec §11 step 2: ValueReader provides full delegation regardless of strategy.
         */
        @Test
        @DisplayName("7.8 valueReaderName valid with any strategy (full delegation)")
        void valueReaderName_validWithAnyStrategy() {
            for (IdStrategy strategy : IdStrategy.values()) {
                IdConfig config = IdConfig.builder()
                        .strategy(strategy)
                        .idFeatures(strategy == IdStrategy.COMBINED
                                ? List.of("f1", "f2") : List.of())
                        .valueReaderName("customReader")
                        .build();

                DiagnosticCollector diagnostics = new DiagnosticCollector();
                config.validate(diagnostics);

                assertEquals("customReader", config.getValueReaderName(),
                    "valueReaderName should be stored with strategy " + strategy);
                assertFalse(diagnostics.hasErrors(),
                    "valueReaderName should not cause errors with strategy " + strategy);
            }
        }

        /**
         * Spec §10/§11: A config can have both writer and reader — they are independent.
         */
        @Test
        @DisplayName("7.9 Both valueWriterName and valueReaderName can be set")
        void bothWriterAndReader_canBeSet() {
            IdConfig config = IdConfig.builder()
                    .strategy(IdStrategy.ID_FIELD)
                    .valueWriterName("myIdWriter")
                    .valueReaderName("myIdReader")
                    .build();

            assertEquals("myIdWriter", config.getValueWriterName());
            assertEquals("myIdReader", config.getValueReaderName());
        }
    }

    // ========================================================================
    // Section 8: idOnTop Ordering Constraint
    // Spec reference: 09-id.md §8.7 "Runtime Orchestration Constraint"
    // ========================================================================

    @Nested
    @DisplayName("8. idOnTop Ordering Constraint (spec §8.7)")
    class IdOnTopOrderingConstraint {

        /**
         * Spec §8.7: "idOnTop=false (default) → Type entry → SuperType entry → ID entry → features"
         */
        @Test
        @DisplayName("8.1 idOnTop=false (default): type before id")
        void idOnTopFalse_default_typeBeforeId() {
            IdConfig config = IdConfig.defaults();

            assertFalse(config.isOnTop(),
                "Spec §8.7: default idOnTop must be false");
        }

        /**
         * Spec §8.7: "idOnTop=true → ID entry → Type entry → SuperType entry → features"
         */
        @Test
        @DisplayName("8.2 idOnTop=true: id before type")
        void idOnTopTrue_idBeforeType() {
            IdConfig config = IdConfig.builder()
                    .onTop(true)
                    .build();

            assertTrue(config.isOnTop());
        }

        /**
         * Spec §8.7: idOnTop can be set via merge (runtime override)
         */
        @Test
        @DisplayName("8.3 idOnTop can be overridden via property map")
        void idOnTop_canBeOverridden() {
            IdConfig base = IdConfig.defaults();
            assertFalse(base.isOnTop());

            IdConfig merged = base.mergeWith(Map.of("codec.idOnTop", true));
            assertTrue(merged.isOnTop());
        }

        /**
         * Spec §8.7: idOnTop is independent of IdKeyMode (except NONE disables ID entirely)
         */
        @Test
        @DisplayName("8.4 idOnTop is stored with any IdKeyMode")
        void idOnTop_storedWithAnyKeyMode() {
            for (IdKeyMode keyMode : IdKeyMode.values()) {
                IdConfig config = IdConfig.builder()
                        .onTop(true)
                        .keyMode(keyMode)
                        .build();

                assertTrue(config.isOnTop(),
                    "idOnTop should be stored with keyMode " + keyMode);
            }
        }
    }
}
