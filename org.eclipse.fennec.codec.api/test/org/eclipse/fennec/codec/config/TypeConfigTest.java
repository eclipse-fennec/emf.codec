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
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TypeConfig} and its merge behavior.
 */
@DisplayName("TypeConfig")
class TypeConfigTest {

    @Nested
    @DisplayName("defaults()")
    class Defaults {

        @Test
        @DisplayName("returns config with spec-defined defaults")
        void returnsConfigWithSpecDefaults() {
            TypeConfig config = TypeConfig.defaults();

            assertTrue(config.isInclude());
            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertEquals(TypeStrategy.URI, config.getStrategy());  // Spec: URI is default
            assertEquals("_type", config.getTypeKey());
            assertEquals("schema", config.getSchemaKey());
            assertEquals("type", config.getNameKey());
            assertNull(config.getDiscriminatorPath());
            assertNull(config.getDiscriminatorValue());
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
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(null);

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns same instance when source is empty")
        void returnsSameInstanceWhenSourceIsEmpty() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of());

            assertSame(config, result);
        }

        @Test
        @DisplayName("returns new instance when source has values")
        void returnsNewInstanceWhenSourceHasValues() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeKey", "customType"));

            assertNotSame(config, result);
        }

        @Test
        @DisplayName("overrides include from source using short key")
        void overridesIncludeFromSourceUsingShortKey() {
            TypeConfig config = TypeConfig.defaults();
            assertTrue(config.isInclude());

            TypeConfig result = config.mergeWith(Map.of("typeInclude", false));

            assertFalse(result.isInclude());
        }

        @Test
        @DisplayName("overrides include from source using prefixed key")
        void overridesIncludeFromSourceUsingPrefixedKey() {
            TypeConfig config = TypeConfig.defaults();
            assertTrue(config.isInclude());

            TypeConfig result = config.mergeWith(Map.of("codec.typeInclude", false));

            assertFalse(result.isInclude());
        }

        @Test
        @DisplayName("overrides format from source using short key")
        void overridesFormatFromSourceUsingShortKey() {
            TypeConfig config = TypeConfig.defaults();
            assertEquals(SerializationFormat.PLAIN, config.getFormat());

            TypeConfig result = config.mergeWith(Map.of("typeFormat", "STRUCTURED"));

            assertEquals(SerializationFormat.STRUCTURED, result.getFormat());
        }

        @Test
        @DisplayName("overrides format from source using enum instance")
        void overridesFormatFromSourceUsingEnumInstance() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeFormat", SerializationFormat.STRUCTURED));

            assertEquals(SerializationFormat.STRUCTURED, result.getFormat());
        }

        @Test
        @DisplayName("overrides strategy from source using short key")
        void overridesStrategyFromSourceUsingShortKey() {
            TypeConfig config = TypeConfig.defaults();
            assertEquals(TypeStrategy.URI, config.getStrategy());  // Spec: URI is default

            TypeConfig result = config.mergeWith(Map.of("typeStrategy", "NAME"));

            assertEquals(TypeStrategy.NAME, result.getStrategy());
        }

        @Test
        @DisplayName("overrides strategy from source using enum instance")
        void overridesStrategyFromSourceUsingEnumInstance() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeStrategy", TypeStrategy.CLASS));

            assertEquals(TypeStrategy.CLASS, result.getStrategy());
        }

        @Test
        @DisplayName("overrides typeKey from source")
        void overridesTypeKeyFromSource() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeKey", "customTypeKey"));

            assertEquals("customTypeKey", result.getTypeKey());
        }

        @Test
        @DisplayName("overrides schemaKey from source")
        void overridesSchemaKeyFromSource() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeSchemaKey", "customSchema"));

            assertEquals("customSchema", result.getSchemaKey());
        }

        @Test
        @DisplayName("overrides nameKey from source")
        void overridesNameKeyFromSource() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeNameKey", "customType"));

            assertEquals("customType", result.getNameKey());
        }

        @Test
        @DisplayName("overrides discriminatorPath from source")
        void overridesDiscriminatorPathFromSource() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeDiscriminatorPath", "kind"));

            assertEquals("kind", result.getDiscriminatorPath());
        }

        @Test
        @DisplayName("overrides discriminatorValue from source")
        void overridesDiscriminatorValueFromSource() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeDiscriminator", "Person"));

            assertEquals("Person", result.getDiscriminatorValue());
        }

        @Test
        @DisplayName("overrides valueReaderName from source")
        void overridesValueReaderNameFromSource() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeValueReaderName", "myReader"));

            assertEquals("myReader", result.getValueReaderName());
        }

        @Test
        @DisplayName("overrides valueWriterName from source")
        void overridesValueWriterNameFromSource() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of("typeValueWriterName", "myWriter"));

            assertEquals("myWriter", result.getValueWriterName());
        }

        @Test
        @DisplayName("keeps base values when not in source")
        void keepsBaseValuesWhenNotInSource() {
            TypeConfig config = TypeConfig.builder()
                    .typeKey("customKey")
                    .strategy(TypeStrategy.CLASS)
                    .include(false)
                    .build();

            TypeConfig result = config.mergeWith(Map.of("typeFormat", "STRUCTURED"));

            // These should be kept from base
            assertEquals("customKey", result.getTypeKey());
            assertEquals(TypeStrategy.CLASS, result.getStrategy());
            assertFalse(result.isInclude());
            // This should be overridden
            assertEquals(SerializationFormat.STRUCTURED, result.getFormat());
        }

        @Test
        @DisplayName("merges multiple values in one call")
        void mergesMultipleValuesInOneCall() {
            TypeConfig config = TypeConfig.defaults();

            TypeConfig result = config.mergeWith(Map.of(
                    "typeKey", "customKey",
                    "typeFormat", "STRUCTURED",
                    "typeStrategy", "NUMERIC",
                    "typeInclude", false
            ));

            assertEquals("customKey", result.getTypeKey());
            assertEquals(SerializationFormat.STRUCTURED, result.getFormat());
            assertEquals(TypeStrategy.NUMERIC, result.getStrategy());
            assertFalse(result.isInclude());
        }
    }

    @Nested
    @DisplayName("cascading merge")
    class CascadingMerge {

        @Test
        @DisplayName("supports chained merge calls")
        void supportsChainedMergeCalls() {
            // Simulates: defaults → annotation → module → factory → options
            TypeConfig config = TypeConfig.defaults()
                    .mergeWith(Map.of("typeKey", "annotationType"))                      // annotation layer
                    .mergeWith(Map.of("typeStrategy", "NUMERIC"))                       // module layer
                    .mergeWith(Map.of("typeFormat", "STRUCTURED"))                      // factory layer
                    .mergeWith(Map.of("typeKey", "optionsType"));                       // options layer (highest priority)

            // Options overrides annotation
            assertEquals("optionsType", config.getTypeKey());
            // Module setting preserved
            assertEquals(TypeStrategy.NUMERIC, config.getStrategy());
            // Factory setting preserved
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
        }

        @Test
        @DisplayName("later layers override earlier layers")
        void laterLayersOverrideEarlierLayers() {
            TypeConfig config = TypeConfig.defaults()
                    .mergeWith(Map.of("typeStrategy", "NAME"))
                    .mergeWith(Map.of("typeStrategy", "CLASS"))
                    .mergeWith(Map.of("typeStrategy", "URI"));

            assertEquals(TypeStrategy.URI, config.getStrategy());
        }

        @Test
        @DisplayName("intermediate results are immutable")
        void intermediateResultsAreImmutable() {
            TypeConfig base = TypeConfig.defaults();
            TypeConfig afterAnnotation = base.mergeWith(Map.of("typeKey", "annotationType"));
            TypeConfig afterModule = afterAnnotation.mergeWith(Map.of("typeKey", "moduleType"));

            // All three should be different instances
            assertNotSame(base, afterAnnotation);
            assertNotSame(afterAnnotation, afterModule);

            // And have different values
            assertEquals("_type", base.getTypeKey());
            assertEquals("annotationType", afterAnnotation.getTypeKey());
            assertEquals("moduleType", afterModule.getTypeKey());
        }

        @Test
        @DisplayName("complex cascade with multiple properties")
        void complexCascadeWithMultipleProperties() {
            TypeConfig config = TypeConfig.defaults()
                    .mergeWith(Map.of(
                            "typeKey", "annotationKey",
                            "typeFormat", "PLAIN",
                            "typeStrategy", "NAME"
                    ))
                    .mergeWith(Map.of(
                            "typeFormat", "STRUCTURED",
                            "typeNameKey", "typeName",
                            "typeSchemaKey", "typeSchema"
                    ))
                    .mergeWith(Map.of(
                            "typeKey", "finalKey",
                            "typeInclude", false
                    ));

            assertEquals("finalKey", config.getTypeKey());
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals(TypeStrategy.NAME, config.getStrategy());
            assertEquals("typeName", config.getNameKey());
            assertEquals("typeSchema", config.getSchemaKey());
            assertFalse(config.isInclude());
        }
    }

    @Nested
    @DisplayName("toBuilder()")
    class ToBuilder {

        @Test
        @DisplayName("creates builder with same values")
        void createsBuilderWithSameValues() {
            TypeConfig original = TypeConfig.builder()
                    .include(false)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.CLASS)
                    .typeKey("customType")
                    .schemaKey("customSchema")
                    .nameKey("customName")
                    .discriminatorPath("kind")
                    .discriminatorValue("Person")
                    .valueReaderName("myReader")
                    .valueWriterName("myWriter")
                    .build();

            TypeConfig copy = original.toBuilder().build();

            assertEquals(original.isInclude(), copy.isInclude());
            assertEquals(original.getFormat(), copy.getFormat());
            assertEquals(original.getStrategy(), copy.getStrategy());
            assertEquals(original.getTypeKey(), copy.getTypeKey());
            assertEquals(original.getSchemaKey(), copy.getSchemaKey());
            assertEquals(original.getNameKey(), copy.getNameKey());
            assertEquals(original.getDiscriminatorPath(), copy.getDiscriminatorPath());
            assertEquals(original.getDiscriminatorValue(), copy.getDiscriminatorValue());
            assertEquals(original.getValueReaderName(), copy.getValueReaderName());
            assertEquals(original.getValueWriterName(), copy.getValueWriterName());
        }

        @Test
        @DisplayName("preserves defaults when not modified")
        void preservesDefaultsWhenNotModified() {
            TypeConfig copy = TypeConfig.defaults().toBuilder().build();

            assertTrue(copy.isInclude());
            assertEquals(SerializationFormat.PLAIN, copy.getFormat());
            assertEquals(TypeStrategy.URI, copy.getStrategy());  // Spec: URI is default
            assertEquals("_type", copy.getTypeKey());
        }

        @Test
        @DisplayName("allows modification after toBuilder")
        void allowsModificationAfterToBuilder() {
            TypeConfig original = TypeConfig.defaults();
            TypeConfig modified = original.toBuilder()
                    .typeKey("newKey")
                    .format(SerializationFormat.STRUCTURED)
                    .build();

            assertEquals("_type", original.getTypeKey());
            assertEquals("newKey", modified.getTypeKey());
            assertEquals(SerializationFormat.PLAIN, original.getFormat());
            assertEquals(SerializationFormat.STRUCTURED, modified.getFormat());
        }
    }

    @Nested
    @DisplayName("validate()")
    class Validate {

        @Test
        @DisplayName("returns same config when validation succeeds")
        void returnsSameConfigWhenValidationSucceeds() {
            TypeConfig config = TypeConfig.defaults();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            TypeConfig result = config.validate(diagnostics);

            assertSame(config, result);
            assertFalse(diagnostics.hasErrors());
            assertFalse(diagnostics.hasWarnings());
        }

        @Test
        @DisplayName("warns when nameKey is not default and format is not STRUCTURED")
        void warnsWhenNameKeyNotDefaultAndFormatNotStructured() {
            TypeConfig config = TypeConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .nameKey("customName")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Expected warning for nameKey when format is PLAIN");
        }

        @Test
        @DisplayName("no warning when nameKey is default even if format is not STRUCTURED")
        void noWarningWhenNameKeyIsDefaultEvenIfFormatNotStructured() {
            TypeConfig config = TypeConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .nameKey("type")  // default value
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                    "Should not warn for nameKey when it has default value");
        }

        @Test
        @DisplayName("no warning when nameKey is not default and format is STRUCTURED")
        void noWarningWhenNameKeyNotDefaultAndFormatIsStructured() {
            TypeConfig config = TypeConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .nameKey("customName")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                    "Should not warn for nameKey when format is STRUCTURED");
        }

        @Test
        @DisplayName("warns when schemaKey is not default and format is not STRUCTURED and strategy is not SCHEMA_AND_TYPE")
        void warnsWhenSchemaKeyNotDefaultAndFormatNotStructuredAndStrategyNotSchemaAndType() {
            TypeConfig config = TypeConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .strategy(TypeStrategy.NAME)
                    .schemaKey("customSchema")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Expected warning for schemaKey when format is PLAIN and strategy is not SCHEMA_AND_TYPE");
        }

        @Test
        @DisplayName("no warning when schemaKey is default even if format not STRUCTURED")
        void noWarningWhenSchemaKeyIsDefaultEvenIfFormatNotStructured() {
            TypeConfig config = TypeConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .schemaKey("schema")  // default value
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                    "Should not warn for schemaKey when it has default value");
        }

        @Test
        @DisplayName("no warning when schemaKey is not default but format is STRUCTURED")
        void noWarningWhenSchemaKeyNotDefaultButFormatIsStructured() {
            TypeConfig config = TypeConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.NAME)
                    .schemaKey("customSchema")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                    "Should not warn for schemaKey when format is STRUCTURED");
        }

        @Test
        @DisplayName("no warning when schemaKey is not default but strategy is SCHEMA_AND_TYPE")
        void noWarningWhenSchemaKeyNotDefaultButStrategyIsSchemaAndType() {
            TypeConfig config = TypeConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .schemaKey("customSchema")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                    "Should not warn for schemaKey when strategy is SCHEMA_AND_TYPE");
        }

        @Test
        @DisplayName("multiple validation constraints can warn together")
        void multipleValidationConstraintsCanWarnTogether() {
            TypeConfig config = TypeConfig.builder()
                    .format(SerializationFormat.PLAIN)
                    .strategy(TypeStrategy.NAME)
                    .nameKey("customName")
                    .schemaKey("customSchema")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertTrue(diagnostics.hasWarnings(),
                    "Expected multiple warnings for both nameKey and schemaKey");
        }

        @Test
        @DisplayName("validation with STRUCTURED format and SCHEMA_AND_TYPE strategy produces no warnings")
        void validationWithStructuredFormatAndSchemaAndTypeProducesNoWarnings() {
            TypeConfig config = TypeConfig.builder()
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .nameKey("customName")
                    .schemaKey("customSchema")
                    .build();
            DiagnosticCollector diagnostics = new DiagnosticCollector();

            config.validate(diagnostics);

            assertFalse(diagnostics.hasWarnings(),
                    "Should not warn when format is STRUCTURED and strategy is SCHEMA_AND_TYPE");
        }
    }

    @Nested
    @DisplayName("builder pattern")
    class BuilderPattern {

        @Test
        @DisplayName("builds config with all properties set")
        void buildsConfigWithAllPropertiesSet() {
            TypeConfig config = TypeConfig.builder()
                    .include(false)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.NUMERIC)
                    .typeKey("myType")
                    .schemaKey("mySchema")
                    .nameKey("myName")
                    .discriminatorPath("kind")
                    .discriminatorValue("Dog")
                    .valueReaderName("reader")
                    .valueWriterName("writer")
                    .build();

            assertFalse(config.isInclude());
            assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
            assertEquals(TypeStrategy.NUMERIC, config.getStrategy());
            assertEquals("myType", config.getTypeKey());
            assertEquals("mySchema", config.getSchemaKey());
            assertEquals("myName", config.getNameKey());
            assertEquals("kind", config.getDiscriminatorPath());
            assertEquals("Dog", config.getDiscriminatorValue());
            assertEquals("reader", config.getValueReaderName());
            assertEquals("writer", config.getValueWriterName());
        }

        @Test
        @DisplayName("builder with partial properties uses defaults for rest")
        void builderWithPartialPropertiesUsesDefaultsForRest() {
            TypeConfig config = TypeConfig.builder()
                    .typeKey("customKey")
                    .strategy(TypeStrategy.CLASS)
                    .build();

            assertEquals("customKey", config.getTypeKey());
            assertEquals(TypeStrategy.CLASS, config.getStrategy());
            assertTrue(config.isInclude());
            assertEquals(SerializationFormat.PLAIN, config.getFormat());
            assertEquals("schema", config.getSchemaKey());  // default
            assertEquals("type", config.getNameKey());  // default
        }

        @Test
        @DisplayName("multiple builder calls with same key use last value")
        void multipleBuilderCallsWithSameKeyUseLastValue() {
            TypeConfig config = TypeConfig.builder()
                    .typeKey("first")
                    .typeKey("second")
                    .typeKey("third")
                    .build();

            assertEquals("third", config.getTypeKey());
        }
    }

    @Nested
    @DisplayName("enum properties")
    class EnumProperties {

        @Test
        @DisplayName("all TypeStrategy enum values are valid")
        void allTypeStrategyEnumValuesAreValid() {
            for (TypeStrategy strategy : TypeStrategy.values()) {
                TypeConfig config = TypeConfig.builder()
                        .strategy(strategy)
                        .build();

                assertEquals(strategy, config.getStrategy());
            }
        }

        @Test
        @DisplayName("all SerializationFormat enum values are valid")
        void allSerializationFormatEnumValuesAreValid() {
            for (SerializationFormat format : SerializationFormat.values()) {
                TypeConfig config = TypeConfig.builder()
                        .format(format)
                        .build();

                assertEquals(format, config.getFormat());
            }
        }
    }
}
